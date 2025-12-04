package kuplrg

object Implementation extends Template {

  import Expr.*
  import Value.*

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
