package kuplrg

object Implementation extends Template {

  import Expr.*
  import Value.*

  type BOp[T] = (T, T) => T
  def NumBOp(op: BOp[BigInt], x: String): BOp[Value] = 
    case (NumV(l), NumV(r)) => NumV(op(l, r))
    case (l, r) => error("invalid operation")
  val NumAdd = NumBOp(_ + _, "+");
  val NumMul = NumBOp(_ * _, "*");
  def malloc(mem: Mem): Addr = mem.keySet.maxOption.fold(0)(_+1)

  def interp(expr: Expr, env: Env, mem: Mem): (Value, Mem) = expr match
    case Num(num) => (NumV(num), mem)
    case Add(left, right) =>
      val (lv, lmem) = interp(left, env, mem);
      val (rv, rmem) = interp(right, env, lmem);
      (NumAdd(lv, rv), rmem)
    case Mul(left, right) =>
      val (lv, lmem) = interp(left, env, mem);
      val (rv, rmem) = interp(right, env, lmem);
      (NumMul(lv, rv), rmem)
    case Id(name) => (env.getOrElse(name, error(s"free identifier $name")), mem)
    case Fun(param, body) => (CloV(param, body, env), mem)
    case App(fun, arg) => interp(fun, env, mem) match
      case (CloV(p, b, e), cmem) =>
        val (argv, argmem) = interp(arg, env, cmem);
        interp(b, e + (p -> argv), argmem)
      case (v, _ )=> error(s"not a function ${v.str}")
    case NewBox(content) =>
      val (cv, cmem) = interp(content, env, mem);
      val new_address = malloc(cmem)
      (BoxV(new_address), cmem + (new_address -> cv))
    case GetBox(box) =>
      val (bv, bmem) = interp(box, env, mem);
      bv match
        case BoxV(addr) => (bmem(addr), bmem)
        case v => error(s"not a box ${v.str}")
    case SetBox(box, content) =>
      val (bv, bmem) = interp(box, env, mem);
      val (cv, cmem) = interp(content, env, bmem);
        bv match
          case BoxV(addr) => (cv, cmem + (addr -> cv))
          case v => error(s"not a box ${v.str}")
    case Seq(left, right) =>
      val (lv, lmem) = interp(left, env, mem);
      interp(right, env, lmem)
}
