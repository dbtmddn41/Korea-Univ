package kuplrg

object Implementation extends Template {

  import Expr.*
  import Value.*
  import Type.*

  def mustValid(ty: Type, tenv: TypeEnv): Type = ty match
    case NumT => NumT
    case BotT => BotT
    case TopT => TopT
    case ArrowT(pty, rty) =>
      ArrowT(mustValid(pty, tenv), mustValid(rty, tenv))
    case RecordT(fs) =>
      RecordT(fs)
  def isSubtype(lty: Type, rty: Type): Boolean = (lty, rty) match
    case (BotT, _) => true
    case (_, TopT) => true
    case (ArrowT(pty1, rty1), ArrowT(pty2, rty2)) => isSubtype(pty2, pty1) && isSubtype(rty1, rty2)
    case (RecordT(fs1), RecordT(fs2)) =>
      fs2.forall { case (field, t2) =>
        fs1.get(field) match {
          case Some(t1) => isSubtype(t1, t2)
          case None => false
        }
      }
    case (t1, t2) => t1 == t2
  def mustSubtype(lty: Type, rty: Type): Unit =
    if (!isSubtype(lty, rty)) error(s"type mismatch: ${lty.str} != ${rty.str}")
    
  def typeCheck(expr: Expr, tenv: TypeEnv): Type = expr match
    case Num(n)       => NumT
    case Add(l, r)    => 
      mustSubtype(typeCheck(l, tenv), NumT)
      mustSubtype(typeCheck(r, tenv), NumT)
      NumT
    case Mul(l, r)    => 
      mustSubtype(typeCheck(l, tenv), NumT)
      mustSubtype(typeCheck(r, tenv), NumT)
      NumT
    case Id(x)        => tenv.getOrElse(x, error(s"free identifier: $x"))
    case Fun(p, t, b) =>
      mustValid(t, tenv)
      ArrowT(t, typeCheck(b, tenv + (p -> t)))
    case App(f, e)    => typeCheck(f, tenv) match
      case ArrowT(pty, rty) =>
        mustSubtype(typeCheck(e, tenv), pty)
        rty
      case _ => error(s"not a function: $f")
    case Access(r, f) => typeCheck(r, tenv) match
      case RecordT(fs) => fs.getOrElse(f, error(s"no such field: $f"))
      case v => error(s"not a record: $v")
    case Val(x, None, i, b) => typeCheck(b, tenv + (x -> typeCheck(i, tenv)))
    case Val(x, Some(t), i, b) =>
      mustSubtype(typeCheck(i, tenv), t)
      typeCheck(b, tenv + (x -> t))
    case Record(fs) => RecordT(fs.map { case (rec, ty) => (rec, typeCheck(ty, tenv)) }.toMap)
    case Exit => BotT

  type BOp[T] = (T, T) => T
  def NumBOp(op: BOp[BigInt], x: String): BOp[Value] =
    case (NumV(l), NumV(r)) => NumV(op(l, r))
    case (l, r) => error("invalid operation")
  val NumAdd: BOp[Value] = NumBOp(_ + _, "+")
  val NumMul: BOp[Value] = NumBOp(_ * _, "*")

  def interp(expr: Expr, env: Env): Value = expr match
    case Num(n)       => NumV(n)
    case Add(l, r)    => NumAdd(interp(l, env), interp(r, env))
    case Mul(l, r)    => NumMul(interp(l, env), interp(r, env))
    case Id(x)        => env.getOrElse(x, error(s"free indentifier: $x"))
    case Fun(p, t, b) => CloV(p, b, env)
    case App(f, e)    => interp(f, env) match
      case CloV(p, b, fenv) => interp(b, fenv + (p -> interp(e, env)))
      case v => error(s"not a function: $v")
    case Access(r, f) => interp(r, env) match
      case RecordV(fields) => fields.getOrElse(f, error(s"no such field: $f"))
      case v => error(s"not a record: $v")
    case Val(x, _, i, b) => interp(b, env + (x -> interp(i, env)))
    case Record(fs) => RecordV(fs.map{ case (rec, field) => (rec, interp(field, env)) }.toMap)
    case Exit => error("exit")
}
