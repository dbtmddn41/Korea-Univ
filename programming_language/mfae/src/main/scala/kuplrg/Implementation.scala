package kuplrg

object Implementation extends Template {

  import Expr.*
  import Value.*

  type BOp[T] = (T, T) => T;
  def NumBOp(op: BOp[BigInt], x: String): BOp[Value] = 
    case (NumV(l), NumV(r)) => NumV(op(l, r))
    case _ => error(s"invalid operation")

  val NumAdd = NumBOp(_+_, "_");
  val NumMul = NumBOp(_*_, "_");
  def malloc(mem: Mem): Addr = mem.keySet.maxOption.fold(0)(_+1)
  def lookupId(env: Env, name: String): Addr =
    env.getOrElse(name, error(s"free identifier $name"))

  def interp(expr: Expr, env: Env, mem: Mem): (Value, Mem) = expr match
    case Num(n)       => (NumV(n), mem)
    case Add(l, r)    => 
      val (lv, lmem) = interp(l, env, mem);
      val (rv, rmem) = interp(r, env, lmem);
      (NumAdd(lv, rv), rmem)
    case Mul(l, r)    => 
      val (lv, lmem) = interp(l, env, mem);
      val (rv, rmem) = interp(r, env, lmem);
      (NumMul(lv, rv), rmem)
    case Var(x, i, b) => 
      val (iv, imem) = interp(i, env, mem);
      val addr = malloc(imem)
      interp(b, env + (x -> addr), imem + (addr -> iv))
    case Id(x)        => (mem(lookupId(env, x)), mem)
    case Fun(p, b)    => (CloV(p, b, env), mem)
    case App(f, a)    => 
      val (fv, fmem) = interp(f, env, mem);
      val (av, amem) = interp(a, env, fmem);
      val a_addr = malloc(amem)
      fv match
        case CloV(p, b, e) => 
          val (av, amem) = interp(a, env, fmem);
          val a_addr = malloc(amem);
          interp(b, e + (p -> a_addr), fmem + (a_addr -> av))
        case v => error(s"not a function ${v.str}")
    case Assign(x, e) =>
      val (ev, emem) = interp(e, env, mem);
      val x_addr = env.getOrElse(x, error(s"free identifier $x"));
      (ev, emem + (x_addr -> ev))
    case Seq(l, r)    =>
      val (lv, lmem) = interp(l, env, mem);
      interp(r, env, lmem)

  def interpCBR(expr: Expr, env: Env, mem: Mem): (Value, Mem) = expr match
    case Num(n)       => (NumV(n), mem)
    case Add(l, r)    => 
      val (lv, lmem) = interpCBR(l, env, mem);
      val (rv, rmem) = interpCBR(r, env, lmem);
      (NumAdd(lv, rv), rmem)
    case Mul(l, r)    => 
      val (lv, lmem) = interpCBR(l, env, mem);
      val (rv, rmem) = interpCBR(r, env, lmem);
      (NumMul(lv, rv), rmem)
    case Var(x, i, b) => 
      val (iv, imem) = interpCBR(i, env, mem);
      val addr = malloc(imem)
      interpCBR(b, env + (x -> addr), imem + (addr -> iv))
    case Id(x)        => (mem(lookupId(env, x)), mem)
    case Fun(p, b)    => (CloV(p, b, env), mem)
    case App(f, a)    => 
      val (fv, fmem) = interpCBR(f, env, mem);
      fv match
        case CloV(p, b, e) => a match
          case Id(x) => interpCBR(b, e + (p -> lookupId(env, x)), fmem)
          case _ => 
            val (av, amem) = interpCBR(a, env, fmem);
            val a_addr = malloc(fmem)
            interpCBR(b, e + (p -> a_addr), amem + (a_addr -> av))
        case v => error(s"not a function ${v.str}")
    case Assign(x, e) =>
      val (ev, emem) = interpCBR(e, env, mem);
      val x_addr = env.getOrElse(x, error(s"free identifier $x"));
      (ev, emem + (x_addr -> ev))
    case Seq(l, r)    =>
      val (lv, lmem) = interpCBR(l, env, mem);
      interpCBR(r, env, lmem)
}
