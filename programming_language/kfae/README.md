> **Note:** This assignment is from the [ku-plrg-classroom/docs](https://github.com/ku-plrg-classroom/docs/tree/main/cose212/kfae) repository.

# `KFAE` - `FAE` with First-Class Continuations

The source code contains the following files:
<pre><code>kfae
└─ src
   ├─ main/scala/kuplrg
   │  ├── KFAE.scala ──────────── The definition of the KFAE and parsers
   │  ├── Implementation.scala ── <b style='color:red;'>[[ IMPLEMENTED FILE ]]</b>
   │  ├── Template.scala ──────── The templates of target functions
   │  └── error.scala ─────────── The definition of the `error` function
   └─ test/scala/kuplrg
      ├─ Spec.scala ───────────── <b style='color:red;'>[[ ADDED TESTS ]]</b>
      └─ SpecBase.scala ───────── The base class of test cases</code></pre>

The `KFAE` language is an extension of the [`FAE`](../fae/README.md) language
with **first-class continuations**. This assignment implemented the
`reduce` function.

## Specification of `KFAE` language

See the [`kfae-spec.pdf`](./kfae-spec.pdf) for the syntax and semantics of the
`KFAE` language.

### Run-time Errors

If the given expression meets the following conditions during evaluation, the
`reduce` function throws an exception using the `error` function with
corresponding error messages containing their error kinds:

| Error kind | Description |
|:-----------|:------------|
| `free identifier` | The given identifier is not bound in the environment. |
| `invalid operation` | The given operation is not defined for the given operands. |
| `not a function` | The expression does not evaluate to a function or continuation in the function application. |

## (Problem #1) `reduce`

The `eval` function is a `KFAE` interpreter that takes an expression in a
string form and repeatedly calls the `reduce` function until the continuation
becomes empty to compute the final result:
```scala
def eval(str: String): String =
  import Cont.*
  def aux(k: Cont, s: Stack): Value = reduce(k, s) match
    case (EmptyK, List(v)) => v
    case (k, s) => aux(k, s)
  aux(EvalK(Map.empty, Expr(str), EmptyK), List.empty).str
```

The `reduce` function represents the reduction rules of the `KFAE` language as a
function that reduces a state `(k, s)` consisting of a continuation `k` and a
stack `s` into a new state `(k', s')`:
```scala
def reduce(k: Cont, s: Stack): (Cont, Stack) = ???
```

**Implemented the `reduce` function in the `Implementation.scala` file.**
