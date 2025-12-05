> **Note:** This assignment is from the [ku-plrg-classroom/docs](https://github.com/ku-plrg-classroom/docs/tree/main/cose212/mfae) repository.

# `MFAE` - `FAE` with Mutable Variables

The source code contains the following files:
<pre><code>mfae
└─ src
   ├─ main/scala/kuplrg
   │  ├── MFAE.scala ──────────── The definition of the MFAE and parsers
   │  ├── Implementation.scala ── <b style='color:red;'>[[ IMPLEMENTED FILE ]]</b>
   │  ├── Template.scala ──────── The templates of target functions
   │  └── error.scala ─────────── The definition of the `error` function
   └─ test/scala/kuplrg
      ├─ Spec.scala ───────────── <b style='color:red;'>[[ ADDED TESTS ]]</b>
      └─ SpecBase.scala ───────── The base class of test cases</code></pre>

The `MFAE` language is an extension of the [`FAE`](../fae/README.md) language
with **mutable variables**. This assignment implemented two
functions: `interp` and `interpCBR`.

## Specification of `MFAE` language

See the [`mfae-spec.pdf`](./mfae-spec.pdf) for the syntax and semantics of the
`MFAE` language.

### Run-time Errors

If the given expression meets the following conditions during evaluation, the
`interp` (or `interpCBR`) function throws an exception using the `error`
function with corresponding error messages containing their error kinds:

| Error kind | Description |
|:-----------|:------------|
| `free identifier` | The given identifier is not bound in the environment. |
| `invalid operation` | The given operation is not defined for the given operands. |
| `not a function` | The expression does not evaluate to a function in the function application. |

## (Problem #1) `interp`

The `eval` function is a wrapper of the `interp` function. It 1) parses the
given string into an expression, 2) evaluates it with the empty environment and
the empty memory, and 3) returns only the value in the string format:
```scala
def eval(str: String): String =
  val (v, _) = interp(Expr(str), Map.empty, Map.empty)
  v.str
```

The `interp` function 1) evaluates the given expression `expr` with the given
environment `env` and memory `mem`, and 2) returns a pair of value and memory as
the result:
```scala
def interp(expr: Expr, env: Env, mem: Mem): (Value, Mem) = ???
```
**Implemented the `interp` function in the `Implementation.scala` file.**

## (Problem #2) `interpCBR`

The `evalCBR` function is a wrapper of the `interpCBR` function, and performs
the similar tasks as the `eval` function except that it uses the `interpCBR`:
```scala
def evalCBR(str: String): String =
  val (v, _) = interpCBR(Expr(str), Map.empty, Map.empty)
  v.str
```

The `interpCBR` function 1) evaluates the given expression `expr` with the given
environment `env` and memory `mem`, and 2) returns a pair of value and memory as
the result:
```scala
def interpCBR(expr: Expr, env: Env, mem: Mem): (Value, Mem) = ???
```
