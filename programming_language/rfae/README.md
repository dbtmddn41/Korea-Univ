> **Note:** This assignment is from the [ku-plrg-classroom/docs](https://github.com/ku-plrg-classroom/docs/tree/main/cose212/rfae) repository.

# `RFAE` - `FAE` with Recursion and Conditionals

The source code contains the following files:
<pre><code>rfae
└─ src
   ├─ main/scala/kuplrg
   │  ├── RFAE.scala ──────────── The definition of the RFAE and parsers
   │  ├── Implementation.scala ── <b style='color:red;'>[[ IMPLEMENTED FILE ]]</b>
   │  ├── Template.scala ──────── The templates of target functions
   │  └── error.scala ─────────── The definition of the `error` function
   └─ test/scala/kuplrg
      ├─ Spec.scala ───────────── <b style='color:red;'>[[ ADDED TESTS ]]</b>
      └─ SpecBase.scala ───────── The base class of test cases</code></pre>

The `RFAE` language is an extension of the [`FAE`](../fae/README.md) language
with **recursion** and **conditionals**. This assignment implemented
the `interp` function.

## Specification of `RFAE` language

See the [`rfae-spec.pdf`](./rfae-spec.pdf) for the syntax and semantics of the
`RFAE` language.

### Run-time Errors

If the given expression meets the following conditions during evaluation, the
`interp` function throws an exception using the `error` function with
corresponding error messages containing their error kinds:

| Error kind | Description |
|:-----------|:------------|
| `free identifier` | The given identifier is not bound in the environment. |
| `invalid operation` | The given operation is not defined for the given operands. |
| `not a function` | The expression does not evaluate to a function in the function application. |
| `not a boolean` | The expression does not evaluate to a boolean in the conditional expression. |

## (Problem #1) `interp`

The `eval` function is a wrapper of the `interp` function. It parses the given
string into an expression and evaluates it with the empty environment:

```scala
def eval(str: String): String = interp(Expr(str), Map.empty).str
```

The `interp` function evaluates the given expression `expr` with the given
environment `env` and returns the result:
```scala
def interp(expr: Expr, env: Env): Value = ???
```
**Implemented the `interp` function in the `Implementation.scala` file.**
