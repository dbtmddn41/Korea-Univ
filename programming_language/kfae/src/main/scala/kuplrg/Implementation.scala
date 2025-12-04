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
  def reduce(k: Cont, s: Stack): (Cont, Stack) = (k, s) match
    case (EmptyK, s) => (k, s)
    case (EvalK(env, expr, k), s) => expr match
      case Num(number) => (k, NumV(number) :: s)
      case Add(left, right) => (EvalK(env, left, EvalK(env, right, AddK(k))), s)
      case Mul(left, right) => (EvalK(env, left, EvalK(env, right, MulK(k))), s)
      case Id(name) => (k, lookupId(name, env) :: s)
      case Fun(param, body) => (k, CloV(param, body, env) :: s)
      case App(fun, arg) => (EvalK(env, fun, EvalK(env, arg, AppK(k))), s)
      case Vcc(name, body) => (EvalK(env + (name -> ContV(k, s)), body, k), s)
    case (AddK(k), n1 :: n2 :: s) => (k, NumAdd(n1, n2) :: s)
    case (MulK(k), n1 :: n2 :: s) => (k, NumMul(n1, n2) :: s)
    case (AppK(k), v2 :: v1 :: s) => v1 match
      case CloV(p, b, e) => (EvalK(e + (p -> v2), b, k), s)
      case ContV(k, s)=> (k, v2 :: s)
      case _ => error(s"not a function")
    case _ => error(s"invalid operation")
  }
