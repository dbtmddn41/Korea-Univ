> **Note:** This assignment is from the [ku-plrg-classroom/docs](https://github.com/ku-plrg-classroom/docs/tree/main/cose212/fae-cps) repository.

# `FAE-cps` - `FAE` with Continuation-Passing Style

The source code contains the following files:
<pre><code>fae-cps
└─ src
   ├─ main/scala/kuplrg
   │  ├── FAE.scala ───────────── The definition of the FAE and parsers
   │  ├── Implementation.scala ── <b style='color:red;'>[[ IMPLEMENTED FILE ]]</b>
   │  ├── Template.scala ──────── The templates of target functions
   │  └── error.scala ─────────── The definition of the `error` function
   └─ test/scala/kuplrg
      ├─ Spec.scala ───────────── <b style='color:red;'>[[ ADDED TESTS ]]</b>
      └─ SpecBase.scala ───────── The base class of test cases</code></pre>

The `FAE-cps` language is exactly the same as the [`FAE`](../fae/README.md)
language, but it uses the **continuation-passing style** (CPS) for the
interpreter. This assignment implemented two functions: `interpCPS`
and `reduce`.

## Specification of `FAE-cps` language

The specification of the `FAE-cps` language is exactly the same as the
[`FAE`](../fae/README.md) language. However, we redefine the semantics using the
continuation-passing style (CPS) in [`fae-cps-spec.pdf`](./fae-cps-spec.pdf).

### Run-time Errors

If the given expression meets the following conditions during evaluation, the
`interpCPS` or `reduce` function throws an exception using the `error`
function with corresponding error messages containing their error kinds:

| Error kind | Description |
|:-----------|:------------|
| `free identifier` | The given identifier is not bound in the environment. |
| `invalid operation` | The given operation is not defined for the given operands. |
| `not a function` | The expression does not evaluate to a function in the function application. |

## (Problem #1) `interpCPS` (50 points)

The `evalCPS` function is a wrapper of the `interpCPS` function. It parses the
given string into an expression and evaluates it with the empty environment and
the identity function as the continuation function:

```scala
def evalCPS(str: String): String = interpCPS(Expr(str), Map.empty, v => v).str
```

The `interpCPS` function evaluates the given expression `expr` with the given
environment `env` and the continuation function `k` to produce a value:
```scala
def interpCPS(expr: Expr, env: Env, k: Value => Value): Value = ???
```
**Implemented the `interpCPS` function in the `Implementation.scala` file.**

## (Problem #2) `reduce` (50 points)

The `evalK` function is another way to implement the interpreter by repeatedly
calling the `reduce` function until the continuation becomes empty:
```scala
def evalK(str: String): String =
  import Cont.*
  def aux(k: Cont, s: Stack): Value = reduce(k, s) match
    case (EmptyK, List(v)) => v
    case (k, s) => aux(k, s)
  aux(EvalK(Map.empty, Expr(str), EmptyK), List.empty).str
```

The `reduce` function reduces a state `(k, s)` consisting of a continuation `k`
and a stack `s` into a new state `(k', s')`:
```scala
def reduce(k: Cont, s: Stack): (Cont, Stack) = ???
```

**Implemented the `reduce` function in the `Implementation.scala` file.**
