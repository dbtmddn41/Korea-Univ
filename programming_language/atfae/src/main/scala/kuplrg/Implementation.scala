package kuplrg

object Implementation extends Template {

  import Expr.*
  import Value.*
  import Type.*

  def mustValid(ty: Type, tenv: TypeEnv): Type = ty match
    case NumT => NumT
    case BoolT => BoolT
    case ArrowT(ptys, rty) =>
      ArrowT(ptys.map(mustValid(_, tenv)), mustValid(rty, tenv))
    case NameT(tn) =>
      if (!tenv.tys.contains(tn)) error(s"invalid type name: $tn")
      NameT(tn)

  def mustSame(lty: Type, rty: Type): Unit = 
    if (lty != rty) error(s"type mismatch: ${lty.str} != ${rty.str}")

  def typeCheck(expr: Expr, tenv: TypeEnv): Type = expr match
    case Num(n)       => NumT
    case Bool(b)      => BoolT
    case Add(l, r)    =>
      mustSame(typeCheck(l, tenv), NumT)
      mustSame(typeCheck(r, tenv), NumT)
      NumT
    case Mul(l, r)    =>
      mustSame(typeCheck(l, tenv), NumT)
      mustSame(typeCheck(r, tenv), NumT)
      NumT
    case Div(l, r)    =>
      mustSame(typeCheck(l, tenv), NumT)
      mustSame(typeCheck(r, tenv), NumT)
      NumT
    case Mod(l, r)    =>
      mustSame(typeCheck(l, tenv), NumT)
      mustSame(typeCheck(r, tenv), NumT)
      NumT
    case Eq(l, r)     =>
      mustSame(typeCheck(l, tenv), NumT)
      mustSame(typeCheck(r, tenv), NumT)
      BoolT
    case Lt(l, r)     =>
      mustSame(typeCheck(l, tenv), NumT)
      mustSame(typeCheck(r, tenv), NumT)
      BoolT
    case Val(x, i, b) => typeCheck(b, tenv.addVar(x -> typeCheck(i, tenv)))
    case Id(x)        => tenv.vars.getOrElse(x, error(s"free identifier ${x}"))
    case Fun(ps, b) =>
      val ptys = ps.map(_.ty)
      for (pty <- ptys) mustValid(pty, tenv)
      ArrowT(ptys, typeCheck(b, tenv.addVars(ps.map(p => p.name -> p.ty))))
    case App(f, es)    => typeCheck(f, tenv) match
      case ArrowT(ptys, retTy) =>
        if (ptys.length != es.length) error("arity mismatch")
        (ptys zip es).map((pt, at) => mustSame(typeCheck(at, tenv), pt))
        retTy
      case ty => error(s"not a function type: ${ty.str}")
    case If(c, t, e)  =>
      mustSame(typeCheck(c, tenv), BoolT)
      val thenTy = typeCheck(t, tenv)
      mustSame(thenTy, typeCheck(e, tenv))
      thenTy
    case Rec(n, ps, rt, b, s) =>
      val ptys = ps.map(_.ty)
      for (pty <- ptys) mustValid(pty, tenv)
      mustValid(rt, tenv)
      val fTy = ArrowT(ptys, rt)
      mustSame(typeCheck(b, tenv.addVar(n -> fTy).addVars(ps.map(p => p.name -> p.ty))), rt)
      typeCheck(s, tenv.addVar(n -> fTy))
    case TypeDef(x, vs, b) =>
      if (tenv.tys.contains(x)) error(s"already defined type: $x")
      val newTEnv = tenv.addType(x, vs.map(v => v.name -> v.ptys).toMap)
      vs.map(_.ptys.map(mustValid(_, newTEnv)))
      mustValid(typeCheck(
        b, newTEnv.addVars(vs.map(v => v.name -> ArrowT(v.ptys, NameT(x))))
        ), tenv)
    case Match(e, cs) => typeCheck(e, tenv) match
      case NameT(tn) => 
        val ts = tenv.tys.getOrElse(tn, error(s"unknown type: $tn"))
        val xs = cs.map(_.name).toSet
        if (ts.keySet != xs || xs.size != cs.length) error("invalid case")
        cs.map { case MatchCase(x, ps, b) =>
          typeCheck(b, tenv.addVars(ps zip ts(x)))
        }.reduce((lty, rty) => { mustSame(lty, rty); lty })
      case _ => error("not a variant")

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
    case Fun(params, body) => CloV(params.map(_.name), body, () => env)
    case Rec(name, params, rty, body, scope) =>
      lazy val newEnv: Env = env + (name -> CloV(params.map(_.name), body, () => newEnv))
      interp(scope, newEnv)
    case App(fun, args) => interp(fun, env) match
      case CloV(ps, b, fenv) => interp(b, fenv() ++ (ps zip args.map(interp(_, env))))
      case ConstrV(name) => VariantV(name, args.map(interp(_, env)))
      case v => error("not a function ${v.str}")
    case If(cond, thenExpr, elseExpr) => interp(cond, env) match
      case BoolV(true) => interp(thenExpr, env)
      case BoolV(false) => interp(elseExpr, env)
      case v => error("not a boolean ${v.str}")
    case TypeDef(x, vs, b) =>
      interp(b, env ++ vs.map(v => v.name -> ConstrV(v.name)))
    case Match(e, cs) => interp(e, env) match
      case VariantV(wname, vs) => cs.find(_.name == wname) match
        case Some(MatchCase(_, ps, b)) =>
          if (ps.length != vs.length) error("arity mismatch")
          interp(b, env ++ (ps zip vs))
        case None => error(s"no such case: $wname")
      case v => error(s"not a variant: ${v.str}")
}
