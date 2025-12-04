package kuplrg

object Implementation extends Template {

  import Expr.*
  import Value.*

  def strict(v: Value): Value = v match
    case ExprV(e, env) => strict(interp(e, env))
    case v => v
  type BOp[T] = (T, T) => T
  def NumBOp(op: BOp[BigInt], x: String): BOp[Value] = (l, r) =>
    (strict(l), strict(r)) match
      case (NumV(l), NumV(r)) => NumV(op(l, r))
      case (l, r) => error("invalid operation")
  val NumAdd: BOp[Value] = NumBOp(_ + _, "+")
  val NumMul: BOp[Value] = NumBOp(_ * _, "*")

  def interp(expr: Expr, env: Env): Value = expr match
    case Num(n)    => NumV(n)
    case Add(l, r) => NumAdd(interp(l, env), interp(r, env))
    case Mul(l, r) => NumMul(interp(l, env), interp(r, env))
    case Id(x)     => env.getOrElse(x, error(s"free identifier $x"))
    case Fun(p, b) => CloV(p, b, env)
    case App(f, e) => strict(interp(f, env)) match
      case CloV(p, b, fenv) => interp(b, fenv + (p -> ExprV(e, env)))
      case v => error(s"not a function ${v.str}")
}
