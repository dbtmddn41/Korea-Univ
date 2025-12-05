> **Note:** This assignment is from the [ku-plrg-classroom/docs](https://github.com/ku-plrg-classroom/docs/tree/main/cose212/vae) repository.

# `VAE` - `AE` with Variables

The source code contains the following files:
<pre><code>vae
└─ src
   ├─ main/scala/kuplrg
   │  ├── VAE.scala ───────────── The definition of the VAE and parsers
   │  ├── Implementation.scala ── <b style='color:red;'>[[ IMPLEMENTED FILE ]]</b>
   │  ├── Template.scala ──────── The templates of target functions
   │  └── error.scala ─────────── The definition of the `error` function
   └─ test/scala/kuplrg
      ├─ Spec.scala ───────────── <b style='color:red;'>[[ ADDED TESTS ]]</b>
      └─ SpecBase.scala ───────── The base class of test cases</code></pre>

The `VAE` language is an extension of the [`AE`](../ae/README.md) language with
**variables**. This assignment implemented five functions: `interp`,
`freeIds`, `bindingIds`, `boundIds`, and `shadowedIds`.

## Specification of `VAE` language

See the [`vae-spec.pdf`](./vae-spec.pdf) for the syntax and semantics
of the `VAE` language.

### Run-time Errors

If the given expression meets the following conditions during evaluation,
the `interp` function throws an exception using the `error` function
with corresponding error messages containing their error kinds:

| Error kind | Description |
|:-----------|:------------|
| `free identifier` | The given identifier is not bound in the environment. |

## (Problem #1) `interp` (20 points)

The `eval` function is a wrapper of the `interp` function. It parses the given
string into an expression and evaluates it with the empty environment:
```scala
def eval(str: String): String = interp(Expr(str), Map.empty).toString
```

The `interp` function evaluates the given expression `expr` with the given
environment `env` and returns the result:
```scala
def interp(expr: Expr, env: Env): Value = ???
```
**Implemented the `interp` function in the `Implementation.scala` file.**

## (Problem #2) `freeIds` (20 points)

The `freeIds` function returns the set of free identifiers in the given
expression `expr`:
```scala
def freeIds(expr: Expr): Set[String] = ???
```
**Implemented the `freeIds` function in the `Implementation.scala` file.**

## (Problem #3) `bindingIds` (20 points)

The `bindingIds` function returns the set of binding identifiers in the given
expression `expr`:
```scala
def bindingIds(expr: Expr): Set[String] = ???
```
**Implemented the `bindingIds` function in the `Implementation.scala` file.**

## (Problem #4) `boundIds` (20 points)

The `boundIds` function returns the set of bound identifiers in the given
expression `expr`:
```scala
def boundIds(expr: Expr): Set[String] = ???
```
**Implemented the `boundIds` function in the `Implementation.scala` file.**

## (Problem #5) `shadowedIds` (20 points)

The `shadowedIds` function returns the set of shadowed identifiers in the given
expression `expr`:
```scala
def shadowedIds(expr: Expr): Set[String] = ???
```
**Implemented the `shadowedIds` function in the `Implementation.scala` file.**
