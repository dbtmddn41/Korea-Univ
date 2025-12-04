package kuplrg

object Implementation extends Template {

  import Expr.*
  import Value.*
  import Inst.*
  import Control.*

  // ---------------------------------------------------------------------------
  // Problem #1
  // ---------------------------------------------------------------------------
  def reduce(st: State): State =
    val State(k, s, h, mem) = st
    (k, s) match
      case (IEval(env, expr) :: k, s) => expr match
        case EUndef          => State(k, UndefV :: s, h, mem)
        case ENum(n)         => State(k, NumV(n) :: s, h, mem)
        case EBool(b)        => State(k, BoolV(b) :: s, h, mem)
        case EAdd(l, r)      => State(IEval(env, l) :: IEval(env, r) :: IAdd :: k, s, h, mem)
        case EMul(l, r)      => State(IEval(env, l) :: IEval(env, r) :: IMul :: k, s, h, mem)
        case EDiv(l, r)      => State(IEval(env, l) :: IEval(env, r) :: IDiv :: k, s, h, mem)
        case EMod(l, r)      => State(IEval(env, l) :: IEval(env, r) :: IMod :: k, s, h, mem)
        case EEq(l, r)       => State(IEval(env, l) :: IEval(env, r) :: IEq :: k, s, h, mem)
        case ELt(l, r)       => State(IEval(env, l) :: IEval(env, r) :: ILt :: k, s, h, mem)
        case EId(x)          => State(k, mem(lookup(env, x)) :: s, h, mem)
        case EVar(x, i, b)   => State(IEval(env, i) :: IDef(List(x), env, b) :: k, s, h, mem)
        case EAssign(x, e)   => State(IEval(env, e) :: IWrite(lookup(env, x)) :: k, s, h, mem)
        case ESeq(l, r)      => State(IEval(env, l) :: IPop :: IEval(env, r) :: k, s, h, mem)
        case EFun(ps, b)     => State(k, CloV(ps, b, env) :: s, h, mem)
        case EApp(f, es)     => State(IEval(env, f) :: (es.map(e => IEval(env, e)) ::: List(ICall(es.length))) ::: k, s, h, mem)
        case EReturn(e)      => State(IEval(env, e) :: IReturn :: k, s, h, mem)
        case EIf(c, t, e)    =>
          State(IEval(env, c) :: IJmpIf(KValue(IEval(env, t) :: k, s, h)) :: IEval(env, e) :: k, s, h, mem)
        case EWhile(c, b)    => 
          val kval_break = KValue(k, s, h)
          val kval_continue = KValue(IPop :: IEval(env, EWhile(c, b)) :: k, s, h)
          val h_body = h ++ Map(Continue -> kval_continue, Break -> kval_break)
          val kval_body = KValue(IEval(env, b) :: IJmp(Continue) :: Nil, s, h_body)
          State(IEval(env, c) :: IJmpIf(kval_body) :: k, UndefV :: s, h, mem)
        case EBreak          => State(IJmp(Break) :: Nil, UndefV :: s, h, mem)
        case EContinue       => State(IJmp(Continue) :: Nil, UndefV :: s, h, mem)
        case ETry(b, x, c)   => 
          val kval_finally = KValue(k, s, h)
          val kval_throw = KValue(IDef(List(x), env, c) :: k, s, h)
          State(IEval(env, b) :: IJmp(Finally) :: Nil, s, h ++ Map(Throw -> kval_throw, Finally-> kval_finally), mem)
        case EThrow(e)       => State(IEval(env, e) :: IJmp(Throw) :: Nil, s, h, mem)
        case EGen(ps, b)     => State(k, GenV(ps, b, env) :: s, h, mem)
        case EIterNext(i, a) => a match 
          case Some(a) => State(IEval(env, i) :: IEval(env, a) :: INext :: k, s, h, mem)
          case None => State(IEval(env, i) :: IEval(env, EUndef) :: INext :: k, s, h, mem)
        case EYield(e)       =>
          val kval_next = ContV(KValue(k, s, h))
          State(IEval(env, e) :: IYield :: Nil, BoolV(false) :: kval_next :: s, h, mem)
        case EValueField(r)  => State(IEval(env, r) :: IValueField :: k, s, h, mem)
        case EDoneField(r)   => State(IEval(env, r) :: IDoneField :: k, s, h, mem)
      case (IAdd :: k, n2 :: n1 :: s) => State(k, numAdd(n1, n2) :: s, h, mem)
      case (IMul :: k, n2 :: n1 :: s) => State(k, numMul(n1, n2) :: s, h, mem)
      case (IDiv :: k, n2 :: n1 :: s) => State(k, numDiv(n1, n2) :: s, h, mem)
      case (IMod :: k, n2 :: n1 :: s) => State(k, numMod(n1, n2) :: s, h, mem)
      case (IEq :: k, v2 :: v1 :: s) => State(k, BoolV(eq(v1, v2)) :: s, h, mem)
      case (ILt :: k, n2 :: n1 :: s) => State(k, numLt(n1, n2) :: s, h, mem)
      case (IDef(xs, env, b) :: k, s) =>
        if (xs.length > s.length) error("invalid stack")
        val x_addrs = malloc(mem, xs.length)
        State(IEval(env ++ xs.zip(x_addrs).toMap, b) :: k, s.drop(xs.length), h, mem ++ x_addrs.zip(s.take(xs.length).reverse))
      case (IWrite(a) :: k, v :: s) => State(k, v :: s, h, mem + (a -> v))
      case (IPop :: k, v :: s) => State(k, s, h, mem)
      case (IJmpIf(kv) :: k, v :: s) => v match
        case BoolV(true) => kv match
          case KValue(k_, s_, h_) => State(k_, s_, h_, mem)
        case BoolV(false) => State(k, s, h, mem)
        case _ => error("not a boolean")
      case (IJmp(c) :: k, v :: s) => lookup(h, c) match
        case KValue(k_, s_, h_) =>
          val h__ = if (h.contains(Yield)) h_ + (Yield -> lookup(h, Yield)) else h_
          State(k_, v :: s_, h__, mem)
      case (ICall(n) :: k, vcs) => 
        if (n >= vcs.length) error("invalid stack")
        val caller = vcs(n)
        val vs = vcs.take(n)
        val s = vcs.drop(n+1)
        caller match
          case CloV(ps, b, fenv) => 
            val s_body = List.fill(ps.length - n)(UndefV) ::: vs.drop(n - ps.length)
            val h_body = h + (Return -> KValue(k, s, h)) -- Set(Break, Continue, Yield)
            State(IDef(ps, fenv, EReturn(b)) :: Nil, s_body, h_body, mem)
          case GenV(ps, b, genv) =>
            val s_body = List.fill(ps.length - n)(UndefV) ::: vs.drop(n - ps.length)
            val k_body = IPop :: IDef(ps, genv, EReturn(ETry(b, "x", EId("x")))) :: Nil
            val cont_body = ContV(KValue(k_body, s_body, Map.empty))
            val iter_addr = malloc(mem)
            State(k, IterV(iter_addr) :: s, h, mem + (iter_addr -> cont_body))
          case _ => error("not a function or generator")
      case (IReturn :: k, v :: s) =>
        if (h.contains(Yield)) 
          State(IYield :: Nil, v :: BoolV(true) :: ContV(KValue(IReturn :: Nil, Nil, Map.empty)) :: s, h, mem)
        else
          State(IJmp(Return) :: Nil, v :: Nil, h, mem)
      case (INext :: k, v :: IterV(a) :: s) => mem(a) match
        case ContV(KValue(k_, s_, h_)) =>
          val kval = KValue(k, IterV(a) :: s, h)
          State(k_, v :: s_, h_ ++ Map(Yield -> kval, Return -> kval), mem)
        case _ => error("not a continuation value")
      case (IYield :: _, v1 :: b :: v2 :: _) => (lookup(h, Yield), b) match
        case (KValue(k_, IterV(a) :: s_, h_), BoolV(b)) => State(k_, ResultV(v1, b) :: s_, h_, mem + (a -> v2))
        case _ => error("not a continuation value")
      case (IValueField:: k, ResultV(v, _) :: s) => State(k, v :: s, h, mem)
      case (IDoneField:: k, ResultV(_, b) :: s) => State(k, BoolV(b) :: s, h, mem)
      case _ => error("invalid operation")


        
  // ---------------------------------------------------------------------------
  // Problem #2
  // ---------------------------------------------------------------------------
  def bodyOfSquares: String = "while (from <= to) yield (from * from++);"

  // ---------------------------------------------------------------------------
  // Helper functions
  // ---------------------------------------------------------------------------
  type BOp[T] = (T, T) => T
  type COp[T] = (T, T) => Boolean
  def checkNonZero(x: BigInt): Unit = if (x == 0) error("invalid operation")
  def numBOp(op: BOp[BigInt], x: String): BOp[Value] =
    case (NumV(l), NumV(r)) => NumV(op(l, r))
    case (l, r) => error("invalid operation")
  def numCOp(op: COp[BigInt], x: String): BOp[Value] =
    case (NumV(l), NumV(r)) => BoolV(op(l, r))
    case (l, r) => error("invalid operation")

  val numAdd: BOp[Value] = numBOp(_ + _, "+")
  val numMul: BOp[Value] = numBOp(_ * _, "*")
  val numDiv: BOp[Value] = numBOp((l, r) => {checkNonZero(r); l / r}, "/")
  val numMod: BOp[Value] = numBOp((l, r) => {checkNonZero(r); l % r}, "%")
  val numLt: BOp[Value] = numCOp(_ < _, "<")
  
  def malloc(mem: Mem, n: Int): List[Addr] =
    val a = malloc(mem)
    (0 until n).toList.map(a + _)

  def malloc(mem: Mem): Addr = mem.keySet.maxOption.fold(0)(_ + 1)

  def lookup(env: Env, x: String): Addr =
    env.getOrElse(x, error(s"free identifier: $x"))

  def lookup(handler: Handler, x: Control): KValue =
    handler.getOrElse(x, error(s"invalid control operation: $x"))

  def eq(l: Value, r: Value): Boolean = (l, r) match
    case (UndefV, UndefV)                   => true
    case (NumV(l), NumV(r))                 => l == r
    case (BoolV(l), BoolV(r))               => l == r
    case (IterV(l), IterV(r))               => l == r
    case (ResultV(lv, ld), ResultV(rv, rd)) => eq(lv, rv) && ld == rd
    case _                                  => false
}
