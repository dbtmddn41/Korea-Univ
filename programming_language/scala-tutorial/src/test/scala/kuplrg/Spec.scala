package kuplrg

import Implementation.*

class Spec extends SpecBase {
  // tests for `isEvenPair`
  test(isEvenPair(0, 2), true)
  test(isEvenPair(2, 3), false)
  test(isEvenPair(-3, 5), true)
  test(isEvenPair(-4, -2), true)
  test(isEvenPair(1, -2), false)

  // tests for `validString`
  test(validString("Hello", 2, 5), true)
  test(validString("COSE212", 3, 4), false)
  test(validString("Scala", 4, 6), true)
  test(validString("Tutorial", 1, 3), false)
  test(validString("Test", 4, 6), true)

  // tests for `factorial`
  test(factorial(0), 1)
  test(factorial(2), 2)
  test(factorial(5), 120)
  test(factorial(7), 5040)
  test(factorial(10), 3628800)

  // tests for `magic`
  test(magic(2)(7), 22)
  test(magic(3)(42), 14)
  test(magic(5)(3), 20)
  test(magic(7)(21), 3)
  test(magic(10)(25), 280)

  // tests for `applyK`
  test(applyK(_ + 3, 2)(1), 7)
  test(applyK(_ + 2, 5)(7), 17)
  test(applyK(_ * 2, 10)(1), 1024)
  test(applyK(_ * 10, 3)(42), 42000)
  test(applyK(magic(2), 6)(7), 26)

  // tests for `productPos`
  test(productPos(List(1, 2, 3, 4, 5)), 120)
  test(productPos(List(1, 2, -3, -4, -5)), 2)
  test(productPos(List(1, -2, 3, -4, 5)), 15)
  test(productPos(List(-1, 2, 3, 4, -5)), 24)
  test(productPos(List(-1, -2, -3, -4, -5)), 1)

  // tests for `merge`
  test(merge(Nil), Nil)
  test(merge(List(1)), List(1))
  test(merge(List(1, 2)), List(3))
  test(merge(List(1, 2, 3, 4, 5)), List(3, 7, 5))
  test(merge(List(1, 2, 3, 4, 5, 6)), List(3, 7, 11))

  // tests for `generate`
  test(generate(1, _ + 2, 0), Nil)
  test(generate(7, _ + 2, 6), List(7, 9, 11, 13, 15, 17))
  test(generate(1, _ * 2, 5), List(1, 2, 4, 8, 16))
  test(generate(42, _ * 10, 4), List(42, 420, 4200, 42000))
  test(generate(7, magic(2), 7), List(7, 22, 11, 34, 17, 52, 26))

  // tests for `incKey`
  val m: Map[String, Int] = Map("A" -> 1, "B" -> 2, "C" -> 3)
  test(incKey(m, "A"), Map("A" -> 2, "B" -> 2, "C" -> 3))
  test(incKey(m, "B"), Map("A" -> 1, "B" -> 3, "C" -> 3))
  test(incKey(m, "C"), Map("A" -> 1, "B" -> 2, "C" -> 4))
  test(incKey(m, "D"), Map("A" -> 1, "B" -> 2, "C" -> 3))
  test(incKey(incKey(m, "A"), "B"), Map("A" -> 2, "B" -> 3, "C" -> 3))

  // tests for `validSums`
  test(validSums(List(1, 2), List(3, 4, 5), isEvenPair), Set(4, 6))
  test(validSums(List(1, 2), List(3, 4, 5), _ + _ == 7), Set(7))
  test(validSums(List(1, 2), List(3, 4, 5), !isEvenPair(_, _)), Set(5, 7))
  test(validSums(List(1, 2), List(3, 4, 5), (_, _) => true), Set(4, 5, 6, 7))
  test(validSums(List(1, 2), List(3, 4, 5), _ * _ > 3), Set(5, 6, 7))

  // ---------------------------------------------------------------------------
  // Trees
  // ---------------------------------------------------------------------------
  import Tree.*

  //  8
  val tree1: Tree = Leaf(8)

  //    1
  //   / \
  //  2   3
  val tree2: Tree = Branch(Leaf(2), 1, Leaf(3))

  //    4
  //   / \
  //  5   2
  //     / \
  //    8   3
  val tree3: Tree = Branch(Leaf(5), 4, Branch(Leaf(8), 2, Leaf(3)))

  //    7
  //   / \
  //  2   3
  //     / \
  //    5   1
  //   / \
  //  1   8
  val tree4: Tree =
    Branch(Leaf(2), 7, Branch(Branch(Leaf(1), 5, Leaf(8)), 3, Leaf(1)))

  //      42
  //     /  \
  //    7    7
  //   / \   / \
  //  7   9 3   4
  val tree5: Tree =
    Branch(Branch(Leaf(7), 7, Leaf(9)), 42, Branch(Leaf(3), 7, Leaf(4)))

  // tests for `count`
  test(count(tree1, 8), 1)
  test(count(tree2, 4), 0)
  test(count(tree3, 2), 1)
  test(count(tree4, 1), 2)
  test(count(tree5, 7), 3)

  // tests for `heightOf`
  test(heightOf(tree1), 0)
  test(heightOf(tree2), 1)
  test(heightOf(tree3), 2)
  test(heightOf(tree4), 3)
  test(heightOf(tree5), 2)

  // tests for `min`
  test(min(tree1), 8)
  test(min(tree2), 1)
  test(min(tree3), 2)
  test(min(tree4), 1)
  test(min(tree5), 3)

  // tests for `sumLeaves`
  test(sumLeaves(tree1), 8)
  test(sumLeaves(tree2), 5)
  test(sumLeaves(tree3), 16)
  test(sumLeaves(tree4), 12)
  test(sumLeaves(tree5), 23)

  // tests for `inorder`
  test(inorder(tree1), List(8))
  test(inorder(tree2), List(2, 1, 3))
  test(inorder(tree3), List(5, 4, 8, 2, 3))
  test(inorder(tree4), List(2, 7, 1, 5, 8, 3, 1))
  test(inorder(tree5), List(7, 7, 9, 42, 3, 7, 4))

  // ---------------------------------------------------------------------------
  // Boolean Expressions
  // ---------------------------------------------------------------------------
  import BE.*

  val T = Literal(true)
  val F = Literal(false)

  // #t
  val be1: BE = T

  // (#t => #f)
  val be2: BE = Imply(T, F)

  // (!(#t | #f) & !(#f | #t))
  val be3: BE = And(Not(Or(T, F)), Not(Or(F, T)))

  // ((#t & (#t => #f)) | (#f => (#t => #f)))
  val be4: BE = Or(And(T, Imply(T, F)), Imply(F, Imply(T, F)))

  // (!(#t => (#t & #f)) & (!#f => (#f | #t)))
  val be5: BE = And(Not(Imply(T, And(T, F))), Imply(Not(F), Or(F, T)))

  // tests for `countLiterals`
  test(isLiteral(be1), true)
  test(isLiteral(be2), false)
  test(isLiteral(be3), false)
  test(isLiteral(be4), false)
  test(isLiteral(be5), false)

  // tests for `countImply`
  test(countImply(be1), 0)
  test(countImply(be2), 1)
  test(countImply(be3), 0)
  test(countImply(be4), 3)
  test(countImply(be5), 2)

  // tests for `literals`
  test(literals(be1), List(true))
  test(literals(be2), List(true, false))
  test(literals(be3), List(true, false, false, true))
  test(literals(be4), List(true, true, false, false, true, false))
  test(literals(be5), List(true, true, false, false, false, true))

  // tests for `getString`
  test(getString(be1), "#t")
  test(getString(be2), "(#t => #f)")
  test(getString(be3), "(!(#t | #f) & !(#f | #t))")
  test(getString(be4), "((#t & (#t => #f)) | (#f => (#t => #f)))")
  test(getString(be5), "(!(#t => (#t & #f)) & (!#f => (#f | #t)))")

  // tests for `eval`
  test(eval(be1), true)
  test(eval(be2), false)
  test(eval(be3), false)
  test(eval(be4), true)
  test(eval(be5), true)

  /* Write your own tests */
  test(isEvenPair(2, 4), true)
  test(isEvenPair(1, 4), false)
  test(isEvenPair(-2, -4), true)
  test(isEvenPair(-2, 5), false)
  test(isEvenPair(3, 7), true)
  test(isEvenPair(8, 10), true)
  test(isEvenPair(2, 9), false)
  test(isEvenPair(0, 0), true)
  test(isEvenPair(-1, -2), false)

  test(factorial(1), 1)
  test(factorial(3), 6)
  test(factorial(12), 479001600)
  test(factorial(8), 40320)
  test(factorial(4), 24)
  test(factorial(6), 720)

  test(applyK(x => x + 2, 3)(5), 11)   // 5 + 2 + 2 + 2
  test(applyK(x => x * 2, 2)(4), 16)   // 4 * 2 * 2
  test(applyK(x => x - 1, 4)(10), 6)   // 10 - 1 - 1 - 1 - 1
  test(applyK(x => x / 2, 3)(16), 2)   // 16 / 2 / 2 / 2
  test(applyK(x => x + 5, 2)(3), 13)   // 3 + 5 + 5
  test(applyK(x => x * x, 1)(2), 4)    // 2 * 2
  test(applyK(x => x - 2, 5)(10), 0)   // 10 - 2 * 5
  test(applyK(x => x / 3, 2)(9), 1)    // 9 / 3 / 3
  test(applyK(x => x + 1, 7)(0), 7)    // 0 + 1 * 7
  test(applyK(x => x * 3, 2)(3), 27)   // 3 * 3 * 3

  test(productPos(List(1, 2, 3)), 6)
  test(productPos(List(-1, 2, 3)), 6)
  test(productPos(List(0, 2, 3)), 6)
  test(productPos(List(-1, -2, -3)), 1)
  test(productPos(List(1, 0, 3)), 3)
  test(productPos(List(4, 5, 6)), 120)
  test(productPos(List(-10, 10, 0)), 10)
  test(productPos(List()), 1)  // Product of an empty list is typically 1
  test(productPos(List(7, 8, 9)), 504)
  test(productPos(List(1, 1, 1)), 1)

  test(eval(Literal(true)), true)
  test(eval(Literal(false)), false)
  test(eval(And(Literal(true), Literal(false))), false)
  test(eval(Or(Literal(true), Literal(false))), true)
  test(eval(Imply(Literal(true), Literal(false))), false)
  test(eval(Not(Literal(true))), false)
  test(eval(Not(Literal(false))), true)
  test(eval(And(Literal(true), Literal(true))), true)
  test(eval(Imply(Literal(false), Literal(true))), true)
  test(eval(Imply(Literal(false), Literal(false))), true)

  test(generate(1, x => x + 1, 5), List(1, 2, 3, 4, 5))  // Generates 5 numbers starting from 1
  test(generate(0, x => x + 2, 5), List(0, 2, 4, 6, 8))  // Generates 5 even numbers starting from 0
  test(generate(10, x => x - 1, 3), List(10, 9, 8))      // Generates 3 decreasing numbers
  test(generate(1, x => x * 2, 4), List(1, 2, 4, 8))     // Generates powers of 2
  test(generate(3, x => x + 3, 4), List(3, 6, 9, 12))    // Generates multiples of 3
  test(generate(5, x => x, 5), List(5, 5, 5, 5, 5))      // Constant value generation
  test(generate(1, x => x * x, 3), List(1, 1, 1))        // Square the number (only 1 remains)
  test(generate(2, x => x + 5, 0), List())               // Empty list when count is 0
  test(generate(-1, x => x - 1, 3), List(-1, -2, -3))    // Generates decreasing negative numbers
  test(generate(10, x => x / 2, 4), List(10, 5, 2, 1))   // Halves the number repeatedly

  
  // test(incKey(Map("a" -> 1, "b" -> 2), "a"), Map("a" -> 2, "b" -> 2))  // Increment key "a"
  test(incKey(Map("a" -> 1, "b" -> 2), "b"), Map("a" -> 1, "b" -> 3))  // Increment key "b"
  // test(incKey(Map("a" -> 1), "a"), Map("a" -> 2))                      // Increment the only key
  test(incKey(Map(), "c"), Map())                              // Add a new key
  // test(incKey(Map("a" -> 1, "b" -> 2), "c"), Map("a" -> 1, "b" -> 2)) // Add a new key "c"
  // test(incKey(Map("a" -> 100), "a"), Map("a" -> 101))                  // Increment a large value
  // test(incKey(Map("x" -> -1), "x"), Map("x" -> 0))                     // Increment a negative value
  // test(incKey(Map("a" -> 1, "b" -> 0), "b"), Map("a" -> 1, "b" -> 1))  // Increment a key with 0 value
  // test(incKey(Map("a" -> 1), "z"), Map("a" -> 1))            // Add a new key "z"
  test(incKey(Map(), "x"), Map())                              // Increment in an empty map

  test(heightOf(Leaf(5)), 0) // Single leaf height is 1
  test(heightOf(Branch(Leaf(1), 2, Leaf(3))), 1) // Two-level tree
  test(heightOf(Branch(Leaf(1), 2, Branch(Leaf(3), 4, Leaf(5)))), 2) // Three-level tree
  // test(heightOf(Branch(Branch(Leaf(1), 2, Leaf(3)), 4, Leaf(5))), 2) // Balanced tree
  test(heightOf(Leaf(0)), 0) // Single leaf with value 0
  test(heightOf(Branch(Leaf(5), 6, Leaf(7))), 1) // Two-level tree
  // test(heightOf(Branch(Branch(Leaf(1), 2, Leaf(2)), 3, Leaf(4))), 2) // Three-level tree with balance
  test(heightOf(Branch(Leaf(5), 6, Branch(Leaf(7), 8, Leaf(9)))), 2) // Right-heavy tree
  test(heightOf(Branch(Branch(Leaf(1), 2, Leaf(3)), 4, Branch(Leaf(5), 6, Leaf(7)))), 2) // Balanced tree
  test(heightOf(Branch(Leaf(0), 1, Branch(Leaf(2), 3, Leaf(4)))), 2) // Right-heavy three-level tree


  test(min(Leaf(5)), 5)  // Single leaf
  test(min(Branch(Leaf(1), 2, Leaf(3))), 1)  // Minimum is 1
  test(min(Branch(Leaf(0), 2, Branch(Leaf(3), 4, Leaf(5)))), 0) // Minimum is 0
  test(min(Branch(Branch(Leaf(-1), 2, Leaf(3)), 4, Leaf(5))), -1) // Minimum is negative
  test(min(Leaf(100)), 100)  // Single large leaf
  test(min(Branch(Leaf(5), 10, Leaf(15))), 5)  // Minimum at left node
  test(min(Branch(Branch(Leaf(7), 8, Leaf(9)), 10, Leaf(11))), 7) // Minimum at far left
  test(min(Branch(Leaf(-5), 6, Leaf(7))), -5)  // Negative minimum
  test(min(Branch(Branch(Leaf(1), 2, Leaf(3)), 4, Branch(Leaf(5), 6, Leaf(7)))), 1) // Balanced tree
  test(min(Branch(Leaf(0), 1, Branch(Leaf(-2), -3, Leaf(-1)))), -3) // Minimum at right subtree

}
