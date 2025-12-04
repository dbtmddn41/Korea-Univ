package kuplrg

object Implementation extends Template {

  import Expr.*
  import Value.*

  // ---------------------------------------------------------------------------
  // Problem #1
  // ---------------------------------------------------------------------------
  def interp(expr: Expr, env: Env): Value = expr match
    case ENum(number) => NumV(number)
    case EBool(bool) => BoolV(bool)
    case EId(name) => env.getOrElse(name, error(s"free identifier $name"))
    case EAdd(left, right) => NumAdd(interp(left, env), interp(right, env))
    case EMul(left, right) => NumMul(interp(left, env), interp(right, env))
    case EDiv(left, right) => NumDiv(interp(left, env), interp(right, env))
    case EMod(left, right) => NumMod(interp(left, env), interp(right, env))
    case EEq(left, right) => BoolV(eq(interp(left, env), interp(right, env)))
    case ELt(left, right) => NumLt(interp(left, env), interp(right, env))
    case EIf(cond, thenExpr, elseExpr) => interp(cond, env) match
      case BoolV(true) => interp(thenExpr, env)
      case BoolV(false) => interp(elseExpr, env)
      case _ => error("not a boolean") 
    case ENil => NilV
    case ECons(head, tail) =>
      val tail_v = interp(tail, env)
      tail_v match
        case NilV | ConsV(_, _) => ConsV(interp(head, env), tail_v)
        case _ => error("invalid operation")
    case EHead(list) => interp(list, env) match
      case ConsV(head, _) => head
      case NilV => error("empty list")
      case _ => error("not a list")
    case ETail(list) => interp(list, env) match
      case ConsV(_, tail) => tail
      case NilV => error("empty list")
      case _ => error("not a list")
    case EMap(list, fun) => map(interp(list, env), interp(fun, env))
    case EFlatMap(list, fun) => join(map(interp(list, env), interp(fun, env)))
    case EFilter(list, fun) => filter(interp(list, env), interp(fun, env))
    case EFoldLeft(list, init, fun) => foldLeft(interp(list, env), interp(init, env), interp(fun, env))
    case ETuple(exprs) => TupleV(exprs.map(e => interp(e, env)))
    case EProj(tuple, index) => interp(tuple, env) match
      case TupleV(values) => if (index <= values.length) values(index-1) else error("out of bounds")
      case _ => error("not a tuple")
    case EVal(name, value, scope) => interp(scope, env + (name -> interp(value, env)))
    case EFun(params, body) => CloV(params, body, () => env)
    case ERec(defs, scope) =>
      lazy val newEnv:Env = env ++ defs.map(fdef => (fdef.name, CloV(fdef.params, fdef.body, () => newEnv))).toMap
      interp(scope, newEnv)
    case EApp(fun, args) => app(interp(fun, env), args.map(arg => interp(arg, env)))
  
  type BOp[T] = (T, T) => T
  type COp[T] = (T, T) => Boolean
  def checkNonZero(x: BigInt): Unit = if (x == 0) error("invalid operation")
  def NumBOp(op: BOp[BigInt], x: String): BOp[Value] =
    case (NumV(l), NumV(r)) => NumV(op(l, r))
    case (l, r) => error("invalid operation")
  def NumCOp(op: COp[BigInt], x: String): BOp[Value] =
    case (NumV(l), NumV(r)) => BoolV(op(l, r))
    case (l, r) => error("invalid operation")

  val NumAdd: BOp[Value] = NumBOp(_ + _, "+")
  val NumMul: BOp[Value] = NumBOp(_ * _, "*")
  val NumDiv: BOp[Value] = NumBOp((l, r) => {checkNonZero(r); l / r}, "/")
  val NumMod: BOp[Value] = NumBOp((l, r) => {checkNonZero(r); l % r}, "%")
  val NumLt: BOp[Value] = NumCOp(_ < _, "<")

  def eq(left: Value, right: Value): Boolean = (left, right) match
    case (NumV(l), NumV(r)) => l == r
    case (BoolV(l), BoolV(r)) => l == r
    case (NilV, NilV) => true
    case (NilV, ConsV(_, _)) => false
    case (ConsV(_, _), NilV) => false
    case (ConsV(h1, t1), ConsV(h2, t2)) => eq(h1, h2) && eq(t1,t2)
    case _ => false

  def map(list: Value, func: Value): Value = (list, func) match
    case (NilV, _) => NilV
    case (ConsV(head, tail), _) => ConsV(app(func, List(head)), map(tail, func))
    case _ => error("not a list")

  def join(list: Value): Value = list match
    case NilV => NilV
    case ConsV(NilV, tail) => join(tail)
    case ConsV(ConsV(head, tail1), tail2) => ConsV(head, join(ConsV(tail1, tail2)))
    case _ => error("invalid operation")

  def filter(list: Value, func: Value): Value = (list, func) match 
    case (NilV, _) => NilV
    case (ConsV(head, tail), _) => app(func, List(head)) match
      case BoolV(true) => ConsV(head, filter(tail, func))
      case BoolV(false) => filter(tail, func)
      case _ => error("not a boolean") 
    case _ => error("not a list")

  def foldLeft(list: Value, init: Value, func: Value): Value = (list, init, func) match
    case (NilV, val_i, _) => val_i
    case (ConsV(head, tail), val_i, _) => foldLeft(tail, app(func, List(init, head)), func)
    case _ => error("not a list")

  def app(func: Value, args: List[Value]): Value = func match
    case CloV(params, body, fenv) =>
      if (params.length == args.length)
        interp(body, fenv() ++ params.zip(args).toMap)
      else
        error("arity mismatch")
    case _ => error("not a function")

  // ---------------------------------------------------------------------------
  // Problem #2
  // ---------------------------------------------------------------------------
  def subExpr1: String = "l <- lists; x <- l; if (pred(x));"

  def subExpr2: String = "(x * x)"
}
