package kuplrg

object Implementation extends Template {

  import Expr.*
  import Value.*
  import Type.*

  def mustSame(lty: Type, rty: Type): Unit = 
    if (lty != rty) error(s"type mismatch: ${lty.str} != ${rty.str}")

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
    case Val(x, i, b) => typeCheck(b, tenv + (x -> typeCheck(i, tenv)))
    case Id(x)        => tenv.getOrElse(x, error(s"free identifier ${x}"))
    case Fun(p, t, b) => ArrowT(t, typeCheck(b, tenv + (p -> t)))
    case App(f, e)    => typeCheck(f, tenv) match
      case ArrowT(paramTy, retTy) =>
        mustSame(typeCheck(e, tenv), paramTy)
        retTy
      case ty => error(s"not a function type: ${ty.str}")

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
  type BOp[T] = (T, T) => T
  def NumBOp(op: BOp[BigInt], x: String): BOp[Value] =
    case (NumV(l), NumV(r)) => NumV(op(l, r))
    case (l, r) => error("invalid operation")

  val NumAdd: BOp[Value] = NumBOp(_ + _, "+")
  val NumMul: BOp[Value] = NumBOp(_ * _, "*")
}
