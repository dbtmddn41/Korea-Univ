> **Note:** This assignment is from the [ku-plrg-classroom/docs](https://github.com/ku-plrg-classroom/docs/tree/main/cose212/fvae) repository.

# `FVAE` - `VAE` with First-Class Functions

The source code contains the following files:
<pre><code>fvae
└─ src
   ├─ main/scala/kuplrg
   │  ├── FVAE.scala ──────────── The definition of the FVAE and parsers
   │  ├── Implementation.scala ── <b style='color:red;'>[[ IMPLEMENTED FILE ]]</b>
   │  ├── Template.scala ──────── The templates of target functions
   │  └── error.scala ─────────── The definition of the `error` function
   └─ test/scala/kuplrg
      ├─ Spec.scala ───────────── <b style='color:red;'>[[ ADDED TESTS ]]</b>
      └─ SpecBase.scala ───────── The base class of test cases</code></pre>

The `FVAE` language is an extension of the [`VAE`](../vae/README.md) language
with **first-class functions**. This assignment implemented two
functions: `interp` and `interpDS`.

## Specification of `FVAE` language

See the [`fvae-spec.pdf`](./fvae-spec.pdf) for the syntax and semantics of the
`FVAE` language.

### Run-time Errors

If the given expression meets the following conditions during evaluation, the
`interp` (or `interpDS`) function throws an exception using the `error`
function with corresponding error messages containing their error kinds:

| Error kind | Description |
|:-----------|:------------|
| `free identifier` | The given identifier is not bound in the environment. |
| `invalid operation` | The given operation is not defined for the given operands. |
| `not a function` | The expression does not evaluate to a function in the function application. |

## (Problem #1) `interp` (50 points)

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

## (Problem #2) `interpDS` (50 points)

The `evalDS` function is a wrapper of the `interpDS` function, and performs the
similar tasks as the `eval` function except that it uses the `interpDS`:
```scala
def evalDS(str: String): String = interpDS(Expr(str), Map.empty).str
```

The `interpDS` function evaluates the given expression `expr` with the given
environment `env` and returns the result:
```scala
def interpDS(expr: Expr, env: Env): Value = ???
```
