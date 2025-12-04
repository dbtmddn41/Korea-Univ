package kuplrg

object Implementation extends Template {

  import Expr.*
  import Value.*
  import Type.*

  // def unify(lty: Type, rty: Type, sol: Solution): Solution = (resolve(lty, sol), resolve(rty, sol)) match
  //   case (NumT, NumT) => sol
  //   case (BoolT, BoolT) => sol
  //   case (ArrowT(lpty, lrty), ArrowT(rpty, rrty)) => unify(lrty, rrty, unify(lpty, rpty, sol))
  //   case (VarT(k), VarT(l)) => if (k == l) sol
  //   case (VarT(k), rty) => if (!occur(k, rty, sol)) sol + (k -> Some(rty))
  //   case (lty, VarT(k)) => if (!occur(k, lty, sol)) sol + (k -> Some(lty))
  //   case _ => error(s"cannot unify")
  def unify(lty: Type, rty: Type, sol: Solution): Solution =
    (resolve(lty, sol), resolve(rty, sol)) match
      case (NumT, NumT) => sol
      case (BoolT, BoolT) => sol
      case (ArrowT(lpty, lrty), ArrowT(rpty, rrty)) =>
        unify(lrty, rrty, unify(lpty, rpty, sol))
      case (VarT(k), VarT(l)) if k == l => sol
      case (VarT(k), rty) if !occurs(k, rty, sol) => sol + (k -> Some(rty))
      case (lty, VarT(k)) if !occurs(k, lty, sol) => sol + (k -> Some(lty))
      case _ => error(s"Cannot unify ${lty.str} and ${rty.str}")
  def resolve(ty: Type, sol: Solution): Type = ty match
    case VarT(k) => sol(k) match
      case Some(ty) => resolve(ty, sol)
      case None => ty
    case _ => ty

  def occurs(k: Int, ty: Type, sol: Solution): Boolean = ty match
    case VarT(l) => k == l
    case ArrowT(pty, rty) => occurs(k, pty, sol) || occurs(k, rty, sol)
    case _ => false

  def gen(ty: Type, tenv: TypeEnv, sol: Solution): TypeScheme =
    val ks = (ty.free(sol) -- tenv.foldLeft(Set.empty[Int]) { case (s, (x, ts)) => s ++ ts.free(sol) }).toList
    TypeScheme(ks, ty)
  
  def inst(ts: TypeScheme, sol: Solution): (Type, Solution) =
    val (subst, sol1) = ts.ks.foldLeft((Map.empty[Int, Option[Type]], sol)) { case ((subst, s), k) =>
      val (newty, s1) = newTypeVar(s)
      (subst + (k -> Some(newty)), s1)
      }
    (ts.ty.subst(sol ++ subst), sol1)

  def newTypeVar(sol: Solution): (Type, Solution) =
    val newty = sol.keySet.maxOption.fold(1)(_+1)
    (VarT(newty), sol + (newty -> None))

  def typeCheck(
    expr: Expr,
    tenv: TypeEnv,
    sol: Solution,
  ): (Type, Solution) = expr match
    case Num(n)          => (NumT, sol)
    case Bool(b)         => (BoolT, sol)
    case Add(l, r)       => 
      val (lty, sol1) = typeCheck(l, tenv, sol)
      val (rty, sol2) = typeCheck(r, tenv, sol1)
      (NumT, unify(rty, NumT, unify(lty, NumT, sol2)))
    case Mul(l, r)       => 
      val (lty, sol1) = typeCheck(l, tenv, sol)
      val (rty, sol2) = typeCheck(r, tenv, sol1)
      (NumT, unify(rty, NumT, unify(lty, NumT, sol2)))
    case Div(l, r)       => 
      val (lty, sol1) = typeCheck(l, tenv, sol)
      val (rty, sol2) = typeCheck(r, tenv, sol1)
      (NumT, unify(rty, NumT, unify(lty, NumT, sol2)))
    case Mod(l, r)       => 
      val (lty, sol1) = typeCheck(l, tenv, sol)
      val (rty, sol2) = typeCheck(r, tenv, sol1)
      (NumT, unify(rty, NumT, unify(lty, NumT, sol2)))
    case Eq(l, r)        => 
      val (lty, sol1) = typeCheck(l, tenv, sol)
      val (rty, sol2) = typeCheck(r, tenv, sol1)
      (BoolT, unify(rty, NumT, unify(lty, NumT, sol2)))
    case Lt(l, r)        => 
      val (lty, sol1) = typeCheck(l, tenv, sol)
      val (rty, sol2) = typeCheck(r, tenv, sol1)
      (BoolT, unify(rty, NumT, unify(lty, NumT, sol2)))
    case Val(x, i, b)    => 
      val (xty, sol1) = typeCheck(i, tenv, sol)
      typeCheck(b, tenv + (x -> gen(xty, tenv, sol1)), sol1)
    case Id(x)           => 
      val ts = tenv.getOrElse(x, error(s"free identifier $x"))z
      val res = inst(ts, sol)
      res
    case Fun(p, b)       => 
      val (pty, sol1) = newTypeVar(sol)
      val pts = TypeScheme(List.empty, pty)
      val (rty, sol2) = typeCheck(b, tenv + (p -> pts), sol1)
      (ArrowT(pts.ty, rty), sol2)
    case Rec(n, p, b, s) => 
      val (pty, sol1) = newTypeVar(sol)
      val (rty, sol2) = newTypeVar(sol1)
      val fty = ArrowT(pty, rty)
      val tenv1 = tenv + (n -> TypeScheme(List.empty, fty))
      val tenv2 = tenv1 + (p -> TypeScheme(List.empty, pty))
      val (bty, sol3) = typeCheck(b, tenv2, sol2)
      val sol4 = unify(bty, rty, sol3)
      typeCheck(s, tenv1, sol4)
    case App(f, e)       =>
      val (fty, sol1) = typeCheck(f, tenv, sol)
      val (aty, sol2) = typeCheck(e, tenv, sol1)
      val (rty, sol3) = newTypeVar(sol2)
      (rty, unify(ArrowT(aty, rty), fty, sol3))
    case If(c, t, e)     =>
      val (cty, sol1) = typeCheck(c, tenv, sol)
      val (tty, sol2) = typeCheck(t, tenv, sol1)
      val (ety, sol3) = typeCheck(e, tenv, sol2)
      (tty, unify(tty, ety, unify(cty, BoolT, sol3)))

  type BOp[T] = (T, T) => T
  type COp[T] = (T, T) => Boolean
  def checkNonZero(x: BigInt): Unit = if x == 0 then error("invalid operation")
  def numBOp(op: BOp[BigInt], x: String): BOp[Value] =
    case (NumV(left), NumV(right)) => NumV(op(left, right))
    case _ => error("invalid operation $x")
  def numCOp(op: COp[BigInt], x: String): BOp[Value] =
    case (NumV(left), NumV(right)) => BoolV(op(left, right))
    case _ => error("invalid operation ${left.str} $x ${right.str}")

  val numAdd: BOp[Value] = numBOp(_ + _, "+")
  val numMul: BOp[Value] = numBOp(_ * _, "*")
  val numDiv: BOp[Value] = numBOp((left, right) => { checkNonZero(right); left / right }, "/")
  val numMod: BOp[Value] = numBOp((left, right) => { checkNonZero(right); left % right }, "%")
  val numEq: BOp[Value] = numCOp(_ == _, "==")
  val numLt: BOp[Value] = numCOp(_ < _, "<")
  def interp(expr: Expr, env: Env): Value = expr match
    case Num(number) => NumV(number)
    case Bool(bool) => BoolV(bool)
    case Id(name) => env.getOrElse(name, error(s"free identifier: $name"))
    case Add(left, right) => numAdd(interp(left, env), interp(right, env))
    case Mul(left, right) => numMul(interp(left, env), interp(right, env))
    case Div(left, right) => numDiv(interp(left,env), interp(right, env))
    case Mod(left, right) => numMod(interp(left,env), interp(right, env))
    case Eq(left, right) => numEq(interp(left,env), interp(right, env))
    case Lt(left, right) => numLt(interp(left,env), interp(right, env))
    case Val(x, i, b) => interp(b, env + (x -> interp(i, env)))
    case Fun(param, body) => CloV(param, body, () => env)
    case Rec(name, param, body, scope) =>
      lazy val newEnv: Env = env + (name -> CloV(param, body, () => newEnv))
      interp(scope, newEnv)
    case App(fun, arg) => interp(fun, env) match
      case CloV(p, b, fenv) => interp(b, fenv() + (p -> interp(arg, env)))
      case v => error("not a function ${v.str}")
    case If(cond, thenExpr, elseExpr) => interp(cond, env) match
      case BoolV(true) => interp(thenExpr, env)
      case BoolV(false) => interp(elseExpr, env)
      case v => error("not a boolean ${v.str}")
}
