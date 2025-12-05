> **Note:** This assignment is from the [ku-plrg-classroom/docs](https://github.com/ku-plrg-classroom/docs/tree/main/cose212/ptfae) repository.

# `PTFAE` - `TFAE` with Parametric Polymorphism

The source code contains the following files:
<pre><code>ptfae
└─ src
   ├─ main/scala/kuplrg
   │  ├── PTFAE.scala ─────────── The definition of the PTFAE and parsers
   │  ├── Implementation.scala ── <b style='color:red;'>[[ IMPLEMENTED FILE ]]</b>
   │  ├── Template.scala ──────── The templates of target functions
   │  └── error.scala ─────────── The definition of the `error` function
   └─ test/scala/kuplrg
      ├─ Spec.scala ───────────── <b style='color:red;'>[[ ADDED TESTS ]]</b>
      └─ SpecBase.scala ───────── The base class of test cases</code></pre>

The `PTFAE` language is an extension of the [`TFAE`](../tfae/README.md) language
with **parametric polymorphism**.  This assignment implemented two
functions: `typeCheck` and `interp`.

## Specification of `PTFAE` language

See the [`ptfae-spec.pdf`](./ptfae-spec.pdf) for the syntax, type system, and
semantics of the `PTFAE` language.

### Type Errors

If the type checker finds a type error in a given expression, the `typeCheck`
function throws an exception using the `error` function:
```scala
testExc(eval("(x: Number) => x(1)"))
```

### Run-time Errors

Similarly, if the semantics of the given expression is not defined, the `interp`
function throws an exception using the `error` function:
```scala
testExc(eval("x"))
```
However, you don't need to consider the specific error messages for both type
errors and run-time errors.  We will not test error messages but only test
whether the error is thrown by the `error` function or not.

## The `eval` function

The `eval` function is a wrapper of the `typeCheck` and `interp` functions and
performs the following three steps:

1. It parses the given string into an expression.
1. It checks the type of the expression with the empty type environment using
   the `typeCheck` function
1. It evaluates the expression with the empty environment using the `interp`
   function

Finally, it returns the string representation of the pair of the resulting value
and the type of the expression:
```scala
def eval(str: String): String =
  val expr = Expr(str)
  val ty = typeCheck(expr, TypeEnv())
  val v = interp(expr, Map.empty)
  s"${v.str}: ${ty.str}"
```

## (Problem #1) `typeCheck`

The `typeCheck` function checks the type of the given expression `expr` with the
given type environment `tenv` and returns the type of the expression:
```scala
def typeCheck(expr: Expr, tenv: TypeEnv): Type = ???
```
**Implemented the `typeCheck` function in the `Implementation.scala`
file.**

## (Problem #2) `interp`

The `interp` function evaluates the given expression `expr` with the given
environment `env` and returns the result:
```scala
def interp(expr: Expr, env: Env): Value = ???
```
**Implemented the `interp` function in the `Implementation.scala` file.**
