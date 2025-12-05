> **Note:** This assignment is from the [ku-plrg-classroom/docs](https://github.com/ku-plrg-classroom/docs/tree/main/cose212/f1vae) repository.

# `F1VAE` - `VAE` with First-Order Functions

The source code contains the following files:
<pre><code>f1vae
└─ src
   ├─ main/scala/kuplrg
   │  ├── F1VAE.scala ─────────── The definition of the F1VAE and parsers
   │  ├── Implementation.scala ── <b style='color:red;'>[[ IMPLEMENTED FILE ]]</b>
   │  ├── Template.scala ──────── The templates of target functions
   │  └── error.scala ─────────── The definition of the `error` function
   └─ test/scala/kuplrg
      ├─ Spec.scala ───────────── <b style='color:red;'>[[ ADDED TESTS ]]</b>
      └─ SpecBase.scala ───────── The base class of test cases</code></pre>

The `F1VAE` language is an extension of the [`VAE`](../vae/README.md) language
with **first-order functions**. This assignment implemented two
functions: `interp` and `interpDS`.

## Specification of `F1VAE` language

See the [`f1vae-spec.pdf`](./f1vae-spec.pdf) for the syntax and semantics
of the `F1VAE` language.

### Run-time Errors

If the given expression meets the following conditions during evaluation, the
`interp` (or `interpDS`) function throws an exception using the `error`
function with corresponding error messages containing their error kinds:

| Error kind | Description |
|:-----------|:------------|
| `free identifier` | The given identifier is not bound in the environment. |
| `duplicate function` | The same function name is defined more than once in the function environment. |
| `unknown function` | The given function name is not defined in the function environment. |

## (Problem #1) `interp` (50 points)

The `eval` function is a wrapper of the `interp` function, and performs the
following tasks:

1. It parses the given string `str` into a program object `program`.
2. It creates a function environment `fenv` from the function definitions
   `program.fdefs`.
3. It calls the `interp` function with the expression `program.expr` to evaluate
   it with the empty environment `Map.empty` and the created function
   environment `fenv`.

```scala
def eval(str: String): String =
  val program: Program = Program(str)
  val fenv: FEnv = createFEnv(program.fdefs)
  interp(program.expr, Map.empty, fenv).toString
```

The `interp` function evaluates the given expression `expr` and returns the
result with a given environment `env` and a function environment `fenv`:
```scala
def interp(expr: Expr, env: Env, fenv: FEnv): Value = ???
```
**Implemented the `interp` function in the `Implementation.scala` file.**

## (Problem #2) `interpDS` (50 points)

The `evalDS` function is a wrapper of the `interpDS` function, and performs the
similar tasks as the `eval` function except that it uses the `interpDS`.
```scala
def evalDS(str: String): String =
  val program: Program = Program(str)
  val fenv: FEnv = createFEnv(program.fdefs)
  interpDS(program.expr, Map.empty, fenv).toString
```

The `interpDS` function evaluates the given expression `expr` and returns the
result with a given environment `env` and a function environment `fenv` using
the **dynamic scoping**:
```scala
def interpDS(expr: Expr, env: Env, fenv: FEnv): Value = ???
```
