package kuplrg

object Implementation extends Template {

  import Expr.*
  import Value.*
  import Type.*

  def mustValid(ty: Type, tenv: TypeEnv): Type = ty match
    case NumT => NumT
    case ArrowT(pty, rty) =>
      ArrowT(mustValid(pty, tenv), mustValid(rty, tenv))
    case VarT(vn) =>
      if (!tenv.tys.contains(vn)) error(s"invalid type name: $vn")
      VarT(vn)
    case PolyT(name, ty) => mustValid(ty, tenv.addType(name))

  def isSame(lty: Type, rty: Type): Boolean = (lty, rty) match
    case (NumT, NumT) => true
    case (ArrowT(lpty, lrty), ArrowT(rpty, rrty)) => isSame(lpty, rpty) && isSame(lrty, rrty)
    case (VarT(ln), VarT(rn)) => ln == rn
    case (PolyT(ln, lty), PolyT(rn, rty)) => isSame(lty, subst(rty, rn, VarT(ln)))
    case _ => false

  def mustSame(lty: Type, rty: Type): Unit = 
    if (!isSame(lty, rty)) error(s"type mismatch: ${lty.str} != ${rty.str}")

  def subst(bodyTy: Type, name: String, ty: Type): Type = bodyTy match
    case NumT => NumT
    case ArrowT(pty, rty) => ArrowT(subst(pty, name, ty), subst(rty, name, ty))
    case VarT(x) => if (name == x) ty else VarT(x)
    case PolyT(x, bodyTy) =>
      if (name == x) PolyT(x, bodyTy)
      else PolyT(x, subst(bodyTy, name, ty))

  def typeCheck(expr: Expr, tenv: TypeEnv): Type = expr match
    case Num(n)       => NumT
    case Add(l, r)    =>
      mustSame(typeCheck(l, tenv), NumT)
      mustSame(typeCheck(r, tenv), NumT)
      NumT
    case Mul(l, r)    => 
      mustSame(typeCheck(l, tenv), NumT)
      mustSame(typeCheck(r, tenv), NumT)
      NumT
    case Val(x, i, b) => typeCheck(b, tenv.addVar(x -> typeCheck(i, tenv)))
    case Id(x)        => tenv.vars.getOrElse(x, error(s"free identifier ${x}"))
    case Fun(p, t, b) =>
      mustValid(t, tenv)
      ArrowT(t, typeCheck(b, tenv.addVar(p -> t)))
    case App(f, e)    => typeCheck(f, tenv) match
      case ArrowT(paramTy, retTy) =>
        mustSame(typeCheck(e, tenv), paramTy)
        retTy
      case ty => error(s"not a function type: ${ty.str}")
    case TypeAbs(p, b) => 
      if (tenv.tys.contains(p)) error(s"already defined type: $p")
      PolyT(p, typeCheck(b, tenv.addType(p)))
    case TypeApp(e, t) => typeCheck(e, tenv) match
      case PolyT(name, ty) => subst(ty, name, mustValid(t, tenv))
      case t => error(s"not a polymorphic type: ${t.str}")

  type BOp[T] = (T, T) => T
  def NumBOp(op: BOp[BigInt], x: String): BOp[Value] =
    case (NumV(l), NumV(r)) => NumV(op(l, r))
    case (l, r) => error("invalid operation")
  val NumAdd: BOp[Value] = NumBOp(_ + _, "+")
  val NumMul: BOp[Value] = NumBOp(_ * _, "*")

  def interp(expr: Expr, env: Env): Value = expr match
    case Num(n)       => NumV(n)
    case Add(l, r)    => NumAdd(interp(l, env), interp(r, env))
    case Mul(l, r)    => NumMul(interp(l, env), interp(r, env))
    case Val(x, i, b) => interp(b, env + (x -> interp(i, env)))
    case Id(x)        => env.getOrElse(x, error(s"free identifier ${x}"))
    case Fun(p, t, b) => CloV(p, b, env)
    case App(f, e)    => interp(f, env) match
      case CloV(p, b, fenv) => interp(b, fenv + (p -> interp(e, env)))
      case v => error(s"not a function type: ${v.str}") 
    case TypeAbs(p, b) => TypeAbsV(p, b, env)
    case TypeApp(e, t) => interp(e, env) match
      case TypeAbsV(tname, tbody, tenv) => interp(tbody, tenv)
      case v => error(s"not a type abstraction: ${v.str}")

}
