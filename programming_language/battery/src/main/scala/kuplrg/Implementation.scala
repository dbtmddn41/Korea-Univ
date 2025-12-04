package kuplrg

object Implementation extends Template {

  import Expr.*
  import RecDef.*
  import Value.*
  import Type.*
  import TypeInfo.*

  def mustValid(ty: Type, tenv: TypeEnv): Type = ty match
    case UnitT => UnitT
    case NumT => NumT
    case BoolT => BoolT
    case StrT => StrT
    case IdT(tn, Nil) =>
      if (!tenv.tys.contains(tn)) error(s"invalid type name: $tn")
      IdT(tn, Nil)
    case IdT(tn, tys) => tenv.tys.getOrElse(tn, error(s"invalid type name: $tn")) match
      case TIAdt(tvars, variants) =>
        tys.map(ty => mustValid(ty, tenv))
        IdT(tn, tys)
      case _ => error("not ADT")
    case ArrowT(tvars, ptys, rty) =>
      val newTEnv = tenv.addTypeVars(tvars)
      ptys.map(mustValid(_, newTEnv))
      mustValid(rty, newTEnv)
      ArrowT(tvars, ptys, rty)


  def subst(bodyTy: Type, name: String, ty: Type): Type = bodyTy match
    case UnitT => UnitT
    case NumT => NumT
    case BoolT => BoolT
    case StrT => StrT
    case IdT(x, tys) => 
      if (name == x) ty else IdT(x, tys.map(subst(_, name, ty)))
    case ArrowT(tvars, ptys, rty) =>
      if (tvars.contains(name)) ArrowT(tvars, ptys, rty)
      else ArrowT(tvars, ptys.map(subst(_, name, ty)), subst(rty, name, ty))
      
      
  def subst_list(bodyTy: Type, names: List[String], tys: List[Type]): Type =
    if (names.length != tys.length) error("arity mismatch")
    (names zip tys).foldLeft(bodyTy){case (bty, (name, ty)) => subst(bty, name, ty)}

  def isSame(lty: Type, rty: Type): Boolean = (lty, rty) match
    case (UnitT, UnitT) => true
    case (NumT, NumT) => true
    case (BoolT, BoolT) => true
    case (StrT, StrT) => true
    case (IdT(lname, ltys), IdT(rname, rtys)) =>
      if (ltys.length != rtys.length) error("arity mismatch")
      ltys match
        case head :: tail =>lname == rname && (ltys zip rtys).forall { case (lt, rt) => isSame(lt, rt) }
        case Nil => lname == rname
    case (ArrowT(ltvars, lptys, lrty), ArrowT(rtvars, rptys, rrty)) =>
      if (ltvars.length != rtvars.length || lptys.length != rptys.length) error("arity mismatch")
      val ltvarsId = ltvars.map(IdT(_, Nil))
      (lptys zip rptys).map((lpty, rpty) => isSame(lpty, subst_list(rpty, rtvars, ltvarsId))).forall(p=>p)
      && isSame(lrty, subst_list(rrty, rtvars, ltvarsId))
    case _ => false

  def mustSame(lty: Type, rty: Type, caller: String = ""): Unit = 
    if (!isSame(lty, rty)) error(s"type mismatch: ${lty.str} != ${rty.str} in $caller")

  def TenvironUpdate(recdef: RecDef, tenv: TypeEnv): TypeEnv = recdef match
      case LazyVal(name, ty, init) => tenv.addVar((name-> ty))
      case RecFun(name, tvars, params, rty, body) => 
        tenv.addVar((name -> ArrowT(tvars, params.map(_.ty), rty)))
      case TypeDef(name, tvars, varts) =>
        if (tenv.tys.contains(name)) error(s"already defined type: $name")
        val tenv0 = tenv.addTypeName(name, tvars, varts)
        val enumId = IdT(name, tvars.map(IdT(_, Nil)))
        tenv0.addVars(varts.map(vart => vart.name -> ArrowT(tvars, vart.params.map(_.ty), enumId)))

  def typing(recdef: RecDef, tenv: TypeEnv): Unit = recdef match
      case LazyVal(name, ty, init) => mustValid(ty, tenv); mustSame(ty, typeCheck(init, tenv), "Lazy Val")
      case RecFun(name, tvars, params, rty, body) =>
        tvars.map(x => if (tenv.tys.contains(x)) error(s"already defined type: $x"))
        val newTEnv = tenv.addTypeVars(tvars)
        params.map(p => mustValid(p.ty, newTEnv))
        mustValid(rty, newTEnv)
        mustSame(rty, typeCheck(body, newTEnv.addVars(params.map(p => p.name-> p.ty))), "RecFun")
      case TypeDef(name, tvars, varts) =>
        tvars.map(x => if (tenv.tys.contains(x)) error(s"already defined type: $x"))
        val newTEnv = tenv.addTypeVars(tvars)
        varts.map(v => v.params.map(p => mustValid(p.ty, newTEnv)))

  def typeCheck(expr: Expr, tenv: TypeEnv): Type = expr match
    case EUnit         => UnitT
    case ENum(n)       => NumT
    case EBool(b)      => BoolT
    case EStr(s)       => StrT
    case EId(x)        => tenv.vars.getOrElse(x, error(s"free identifier ${x}"))
    case EAdd(l, r)    =>
      mustSame(typeCheck(l, tenv), NumT)
      mustSame(typeCheck(r, tenv), NumT)
      NumT
    case EMul(l, r)    =>
      mustSame(typeCheck(l, tenv), NumT)
      mustSame(typeCheck(r, tenv), NumT)
      NumT
    case EDiv(l, r)    =>
      mustSame(typeCheck(l, tenv), NumT)
      mustSame(typeCheck(r, tenv), NumT)
      NumT
    case EMod(l, r)    =>
      mustSame(typeCheck(l, tenv), NumT)
      mustSame(typeCheck(r, tenv), NumT)
      NumT
    case EConcat(l, r) =>
      mustSame(typeCheck(l, tenv), StrT)
      mustSame(typeCheck(r, tenv), StrT)
      StrT
    case EEq(l, r)     => 
      mustSame(typeCheck(l, tenv), typeCheck(r, tenv))
      BoolT
    case ELt(l, r)     =>
      mustSame(typeCheck(l, tenv), NumT)
      mustSame(typeCheck(r, tenv), NumT)
      BoolT
    case ESeq(l, r)    =>
      typeCheck(l, tenv)
      typeCheck(r, tenv)
    case EIf(c, t, e)  =>
      mustSame(typeCheck(c, tenv), BoolT)
      val thenTy = typeCheck(t, tenv)
      mustSame(thenTy, typeCheck(e, tenv))
      thenTy
    case EVal(x, t, e, b) => t match
      case Some(t) => 
        mustSame(t, typeCheck(e, tenv), "EVal")
        typeCheck(b, tenv.addVar(x -> t))
      case None => typeCheck(b, tenv.addVar(x -> typeCheck(e, tenv)))
    case EFun(ps, b) =>
      val ptys = ps.map(_.ty)
      for (pty <- ptys) mustValid(pty, tenv)
      ArrowT(Nil, ptys, typeCheck(b, tenv.addVars(ps.map(p => p.name -> p.ty))))
    case EApp(f, ts, as) => 
      for (t <- ts) mustValid(t, tenv)
      typeCheck(f, tenv) match
        case ArrowT(tvars, ptys, retTy) =>
          if (ptys.length != as.length) error("arity mismatch")
          (ptys zip as).map((pt, at) => mustSame(typeCheck(at, tenv), subst_list(pt, tvars, ts), "EApp"))
          subst_list(retTy, tvars, ts)
        case ty => error(s"not a function type: ${ty.str}")
    case ERecDefs(ds, b) =>
      val newTEnv = ds.foldLeft(tenv){ case (tenv, d) => TenvironUpdate(d, tenv)}
      ds.map(d => typing(d, newTEnv))
      mustValid(typeCheck(b, newTEnv), tenv)
    case EMatch(e, cs) => typeCheck(e, tenv) match
      case IdT(tn, tys) => tenv.tys.getOrElse(tn, error(s"invalid type name: $tn")) match
        case TIAdt(tvars, variants) =>
          val xs = cs.map(_.name).toSet
          if (variants.keySet != xs || xs.size != cs.length) error("invalid case")
          cs.map { case MatchCase(x, ps, b) =>
          val newTEnv = tenv.addVars(ps zip variants(x).map(p => subst_list(p.ty, tvars, tys)))
          typeCheck(b, newTEnv)
          }.reduce((lty, rty) => { mustSame(lty, rty, "EMatch"); lty })
        case _ => error("not ADT")
      case _ => error("not a id type")
    case EExit(ty, expr) => mustValid(ty, tenv); mustSame(typeCheck(expr, tenv), StrT); ty

  type BOp[T] = (T, T) => T
  type COp[T] = (T, T) => Boolean
  def checkNonZero(x: BigInt): Unit = if x == 0 then error("invalid operation")
  def numBOp(op: BOp[BigInt], x: String): BOp[Value] =
    case (NumV(left), NumV(right)) => NumV(op(left, right))
    case _ => error("invalid operation $x")
  def numCOp(op: COp[BigInt], x: String): BOp[Value] =
    case (NumV(left), NumV(right)) => BoolV(op(left, right))
    case _ => error("invalid operation ${left.str} $x ${right.str}")
  def strBOp(op: BOp[String], x: String): BOp[Value] =
    case (StrV(left), StrV(right)) => StrV(op(left, right))
    case _ => error("invalid operation $x")

  val numAdd: BOp[Value] = numBOp(_ + _, "+")
  val numMul: BOp[Value] = numBOp(_ * _, "*")
  val numDiv: BOp[Value] = numBOp((left, right) => { checkNonZero(right); left / right }, "/")
  val numMod: BOp[Value] = numBOp((left, right) => { checkNonZero(right); left % right }, "%")
  val numEq: BOp[Value] = numCOp(_ == _, "==")
  val numLt: BOp[Value] = numCOp(_ < _, "<")
  val strCat: BOp[Value] = strBOp(_ ++ _, "++")


  def interp(expr: Expr, env: Env): Value = expr match
    case EUnit         => UnitV
    case ENum(n)       => NumV(n)
    case EBool(b)      => BoolV(b)
    case EStr(s)       => StrV(s)
    case EId(x)        => 
      val xval = env.getOrElse(x, error(s"free identifier $x"))
      xval match
        case ExprV(xexpr, xenv) => interp(xexpr, xenv())
        case _ => xval
    case EAdd(l, r)    => numAdd(interp(l, env), interp(r, env))
    case EMul(l, r)    => numMul(interp(l, env), interp(r, env))
    case EDiv(l, r)    => numDiv(interp(l, env), interp(r, env))
    case EMod(l, r)    => numMod(interp(l, env), interp(r, env))
    case EConcat(l, r) => strCat(interp(l, env), interp(r, env))
    case EEq(l, r)     => BoolV(eq(interp(l, env), interp(r, env)))
    case ELt(l, r)     => numLt(interp(l, env), interp(r, env))
    case ESeq(l, r)    => interp(l, env); interp(r, env)
    case EIf(c, t, e)  => interp(c, env) match
      case BoolV(true) => interp(t, env)
      case BoolV(false) => interp(e, env)
      case v => error("not a boolean ${v.str}")
    case EVal(x, t, e, b) => interp(b, env + (x -> interp(e, env)))
    case EFun(ps, b) => CloV(ps.map(_.name), b, () => env)
    case EApp(f, ts, as) => interp(f, env) match
      case CloV(ps, b, fenv) =>
        if (as.length != ps.length) error("arity mismatch")
        interp(b, fenv() ++ (as zip ps).map((a, p) => p -> interp(a, env)))
      case ConstrV(name) => VariantV(name, as.map(interp(_, env)))
      case v => error("not a function ${v.str}")
    case ERecDefs(ds, b) =>
      lazy val newEnv: Env = ds.foldLeft(env){ case (e, d) => environUpdate(d, e, () => newEnv) }
      interp(b, newEnv)
    case EMatch(e, cs) => interp(e, env) match
      case VariantV(wname, vs) => cs.find(_.name == wname) match
        case Some(MatchCase(_, ps, b)) =>
          if (ps.length != vs.length) error("arity mismatch")
          interp(b, env ++ (ps zip vs))
        case None => error(s"no such case: $wname")
      case v => error(s"not a variant: ${v.str}")
    case EExit(ty, expr) => error(s"$expr")

  def eq(left: Value, right: Value): Boolean = (left, right) match
    case (UnitV, UnitV) => true
    case (NumV(l), NumV(r)) => l == r
    case (BoolV(l), BoolV(r)) => l == r
    case (StrV(l), StrV(r)) => l == r
    case (VariantV(lname, lv), VariantV(rname, rv)) =>
      if (lname.length != rname.length) false
      else (lname == rname) && (lv zip rv).forall{ case(l, r) => eq(l, r) }
    case _ => false

  def environUpdate(recdef: RecDef, env1: Env, env2: () => Env): Env = recdef match
      case LazyVal(name, ty, init) => env1 + (name -> ExprV(init,  env2))
      case RecFun(name, tvars, params, rty, body) => 
        env1 + (name -> CloV(params.map(_.name), body, env2))
      case TypeDef(name, tvars, varts) =>
        env1 ++ (varts.map(v => v.name -> ConstrV(v.name)))
} 
