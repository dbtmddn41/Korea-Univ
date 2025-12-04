package kuplrg

object Implementation extends Template {

  import Expr.*
  import Value.*
  import Cont.*

  type BOp[T] = (T, T) => T;
  def NumBOp(op: BOp[BigInt], x: String): BOp[Value] = 
    case (NumV(l), NumV(r)) => NumV(op(l, r))
    case _ => error(s"invalid operation")

  val NumAdd = NumBOp(_+_, "_");
  val NumMul = NumBOp(_*_, "_");
  
  def lookupId(name: String, env: Env): Value =
    env.getOrElse(name, error(s"free identifier $name"))

  def interpCPS(expr: Expr, env: Env, k: Value => Value): Value = expr match
  case Num(number) => k(NumV(number))
  case Add(left, right) =>
    interpCPS(left, env, {
      lv => interpCPS(right, env, {
        rv => k(NumAdd(lv, rv))
      })
    })
  case Mul(left, right) =>
    interpCPS(left, env, {
      lv => interpCPS(right, env, {
        rv => k(NumMul(lv, rv))
      })
    })
  case Id(name) => k(lookupId(name, env))
  case Fun(param, body) => k(CloV(param, body, env))
  case App(fun, arg) => 
    interpCPS(fun, env, {
      funv => funv match
        case CloV(p, b, fenv) => interpCPS(arg, env, {
          argv => interpCPS(b, fenv + (p -> argv), k)
        })
        case _ => error("not a function")
    })

  def reduce(k: Cont, s: Stack): (Cont, Stack) = (k, s) match
    case (EmptyK, s) => (EmptyK, s)
    case (EvalK(env, expr, k), s) => expr match
      case Num(n) => (k, NumV(n) :: s)
      case Add(l, r) => (EvalK(env, l, EvalK(env, r, AddK(k))), s)
      case Mul(l, r) => (EvalK(env, l, EvalK(env, r, MulK(k))), s)
      case Id(name) => (k, lookupId(name, env) :: s)
      case Fun(p, b) => (k, CloV(p, b, env) :: s)
      case App(f, e) => (EvalK(env, f, EvalK(env, e, AppK(k))), s)
    case (AddK(k), r :: l :: s) => (k, NumAdd(l, r) :: s)
    case (MulK(k), r :: l :: s) => (k, NumMul(l, r) :: s)
    case (AppK(k), arg :: fun :: s) => fun match
      case CloV(p, b, fenv) => (EvalK(fenv + (p -> arg), b, k), s)
      case _ => error("not a function")
    case _ => error("invalid operation")
}
