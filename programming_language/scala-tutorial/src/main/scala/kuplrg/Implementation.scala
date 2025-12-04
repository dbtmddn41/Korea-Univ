package kuplrg

object Implementation extends Template {

  // ---------------------------------------------------------------------------
  // Basic Data Types
  // ---------------------------------------------------------------------------
  def isEvenPair(x: Int, y: Int): Boolean = (x + y) % 2 == 0


  def validString(str: String, lower: Int, upper: Int): Boolean = lower <= str.length && str.length <= upper

  // ---------------------------------------------------------------------------
  // Functions
  // ---------------------------------------------------------------------------
  def factorial(n: Int): Int = {
    if (n == 0) 1
    else factorial(n - 1) * n
  }

  def magic(x: Int): Int => Int = {
    (y: Int) => {
      if (y % x == 0) y / x
      else (x + 1) * y + (x - y % x)
    }
  }

  def applyK(f: Int => Int, k: Int): Int => Int = {
    if (k == 1) f
    else (x: Int) => f(applyK(f, k - 1)(x))
  }

  // ---------------------------------------------------------------------------
  // Collections
  // ---------------------------------------------------------------------------
  def productPos(l: List[Int]): Int = {
    l
    .filter(_ > 0)
    .foldLeft(1)(_ * _)
  }

  def merge(l: List[Int]): List[Int] = l match {
    case Nil              => Nil
    case x :: Nil         => List(x)
    case x :: y :: tail   => x + y :: merge(tail)
  }

  def generate(init: Int, f: Int => Int, n: Int): List[Int] = {
    if (n == 0) Nil
    else init :: generate(f(init), f, n - 1)
  }

  def incKey(map: Map[String, Int], key: String): Map[String, Int] = {
    map.get(key) match {
      case Some(n)  => map + (key -> (n + 1))
      case None     => map
    }
  }

  def validSums(
    l: List[Int],
    r: List[Int],
    f: (Int, Int) => Boolean,
  ): Set[Int] = {
    (for {
      x <- l
      y <- r
      if f(x, y)
    } yield x + y).toSet
  }

  // ---------------------------------------------------------------------------
  // Trees
  // ---------------------------------------------------------------------------
  import Tree.*

  def count(t: Tree, x: Int): Int = t match {
      case Leaf(value) => if (x == value) 1 else 0
      case Branch(left, value, right) => (if (x == value) 1 else 0) + count(left, x) + count(right, x)
  }

  def heightOf(t: Tree): Int = t match {
    case Leaf(_) => 0
    case Branch(left, _, right) => (heightOf(left) max heightOf(right)) + 1
  }

  def min(t: Tree): Int = t match {
    case Leaf(value) => value
    case Branch(left, value, right) => (min(left) min value) min min(right)
  }

  def sumLeaves(t: Tree): Int = t match {
    case Leaf(value) => value
    case Branch(left, value, right) => sumLeaves(left) + sumLeaves(right)
  }

  def inorder(t: Tree): List[Int] = t match {
      case Leaf(value) => List(value)
      case Branch(left, value, right) => inorder(left) ::: List(value) ::: inorder(right)
  }

  // ---------------------------------------------------------------------------
  // Boolean Expressions
  // ---------------------------------------------------------------------------
  import BE.*

  def isLiteral(expr: BE): Boolean = expr match {
    case Literal(_) => true
    case _          => false
  }

  def countImply(expr: BE): Int = expr match {
    case Literal(value)     => 0
    case And(left, right)   => countImply(left) + countImply(right)
    case Or(left, right)    => countImply(left) + countImply(right)
    case Imply(left, right) => countImply(left) + countImply(right) + 1
    case Not(expr)          => countImply(expr)
  }

  def literals(expr: BE): List[Boolean] = expr match {
    case Literal(value)     => List(value)
    case And(left, right)   => literals(left) ::: literals(right)
    case Or(left, right)    => literals(left) ::: literals(right)
    case Imply(left, right) => literals(left) ::: literals(right)
    case Not(expr)          => literals(expr)
  }

  def getString(expr: BE): String = expr match {
    case Literal(value)     => if (value) "#t" else "#f"
    case And(left, right)   => s"(${getString(left)} & ${getString(right)})"
    case Or(left, right)    => s"(${getString(left)} | ${getString(right)})"
    case Imply(left, right) => s"(${getString(left)} => ${getString(right)})"
    case Not(expr)          => s"!${getString(expr)}"
  }

  def eval(expr: BE): Boolean = expr match {
    case Literal(value)     => value
    case And(left, right)   => eval(left) && eval(right)
    case Or(left, right)    => eval(left) || eval(right)
    case Imply(left, right) => !eval(left) || eval(right)
    case Not(expr)          => !eval(expr)
  }
}
