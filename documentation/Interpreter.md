# The interpreter

This document will describe how the interpreter works (I say "will" because I'm
hoping someone will finish/fix it in the future). You don't need to read this
whole thing to get a handle on how the interpreter works, you can probably just
skim this or even forgo reading this file and dive into the source code.

Table of contents (unnecessary but tables of contents are kinda cool):

- [`Interpreter.execute`](#interpreterexecute)
- [Contexts](#contexts)
  - [The `ctx` parameter](#the-ctx-parameter)
  - [The `Context` class](#the-context-class)
- [Runtime values](#runtime-values)
- [Back to `execute`](#back-to-execute)
- [Executing functions](#executing-functions)

## `Interpreter.execute`

The [Interpreter](/version-3/shared/src/main/scala/Interpreter.scala) object
is the entry point for executing code. It has two methods, both named `execute`,
with the following signatures:

```scala
def execute(code: String)(using ctx: Context): Unit
def execute(ast: AST)(using ctx: Context): Unit
```

Don't worry about the `using`, it'll be explained [later](#the-ctx-parameter).
Scala lets you define a method with multiple parameter lists so that you can do
[currying](https://en.wikipedia.org/wiki/Currying) more easily. These two
methods are basically equivalent (Scala does compile them to different things,
but we don't need to worry about that):

```scala
def foo(a: A)(b: B): C = c(a, b)
def foo(a: A): (B => C) = (b: B) => c(a, b)
```

Both can be called using `foo(a)(b)` (or `foo(a)`, to get a value of type
`B => C`).

The second overload of `execute` (the one taking an AST) is for internal use,
and the first is the one that code outside of the main Vyxal code will call. The
overload taking a `String` is basically just a helper of sorts. It will parse
the string into an AST, call the second overload on that AST, and then print the
top of the stack or whatever the flags tell it to do.

## Contexts

If you know what `Context` is, feel free to [skip](#back-to-execute) this section.

### The `ctx` parameter

TL;DR: `ctx` is an implicit parameter ([docs][implicit docs]). You can skip
[ahead](#the-context-class) if you want.

You'll notice that `Interpreter`'s `execute` methods take a parameter of type
`Context`. As mentioned before, it's also in its own parameter list, with a
`using` in front of it. That `(using ctx: Context)` parameter list is called a
using clause. The official documentation for them is [here][implicit docs].
Feel free to read all of that if you like, but they go into a lot of detail that
you don't really need to worry about right now. A parameter list with a `using`
at the front means that all the parameters in that list are **implicit**
parameters.

Implicits are one of Scala's most powerful features, but here we're using it for
the simplest of use cases - we're code golfers and we want to write as little
code as possible :). If you've worked on Vyxal 2, you'll remember that nearly
every element implementation needs `ctx` passed to it, and sometimes we'd forget
to do that and it would error. Here, making `ctx` an implicit parameter avoids
all that.

Since `ctx` is an implicit parameter, if there's an
implicit value of type `Context` in scope, when calling `execute`, you won't
have to explicitly pass that value, you can just say `execute(myAst)` and the
compiler will automatically pass that implicit `Context` for you. If you want
to explicitly pass the context, you can do `execute(myAst)(using myContext)`.

If you are inside a method that has an implicit `Context` parameter (like
the `execute` method(s) inside `Interpreter` [above](#interpreterexecute))

### The `Context` class

Now, on to the [`Context`][Context]
class itself. It's used for keeping track of everything in the current
execution context/scope. Every scope (while loops, for loops, functions) gets
its own context holding the stack, variables, inputs, and a few other things for
that scope.

The `Context` class also has a `globals` field. All the `Context`s
have the same [`Globals`](/version-3/shared/src/main/scala/Globals.scala) object
in their `globals` field, and it holds the settings (set using flags), the
global inputs (passed in the "Inputs" field of the online interpreter), and the
register.

Every element's implementation takes a `Context` as input, even if it
doesn't directly use it. See [this][Element-Impls] for information on how the elements are implemented.

`Context` has methods for pushing to and popping from the stack, getting and
setting variables, and creating child contexts. See the [Context] class for all
of them.

## Runtime values

Vyxal has 4 basic types:

- Strings
- Numbers
- Functions
- Lists (which are heterogeneous and may contain any of these 4)

Internally, the [`VAny`](/version-3/shared/src/main/scala/VAny.scala) type is
used. It's a [union type] that's more or less equivalent to
`String | VNum | VFun | VList`.

### `String`

Represents a Vyxal string. This is simply Scala's own `String` class, so there's
no need for additional conversions or anything.

### `VNum`

Represents a Vyxal number. Under the hood, it uses a `Complex[Real]` (a complex
number whose components are real numbers) from the
[Spire](https://typelevel.org/spire/) library. There are implicit conversions
from `Int`s and `Double`s and stuff to `VNum` for convenience.

### `VFun`

Represents a Vyxal function object (*not* a function definition). It holds the
implementation of the function, its arity, its parameters (optional), and the
`Context` it was defined in (the reason for keeping the context is given
[here](#executing-functions)).

### `VList`

Represents a Vyxal list. Scala doesn't allow recursive type aliases, so it's
a whole class of its own wrapping around a `Seq[VAny]` (a class from Scala's
standard library).

## Back to `execute`

`execute(ast)` pattern matches on the inputted AST and decides what to do based on that.

- If it's a number or string literal, it simply pushes it onto the stack.
- If it's an `AST.Group` (multiple ASTs grouped into a single AST), then it
  `execute`s each one of them in order.
- If it's a command (`+`, `M`, etc.), it gets the corresponding implementation
  from [`Elements`](/version-3/shared/src/main/scala/Elements.scala) and
  calls that, passing it the [`ctx` parameter](#the-ctx-parameter).
- If it's an if statement, it executes it in the current context. They don't
  need their own child contexts.
- If it's a while loop or for loop, a child context is created. It uses the same
  stack and everything, the only thing different about the child context is the
  [context variables](./README.md/#what-are-context-variables), which are set
  according to the type of loop. Note: context variables are a Vyxal feature,
  not to be confused with the `Context` class, which is an implementation thing
  that has nothing to do with context variables apart from the fact that the
  `Context` class holds the two context variables for that scope.

## Executing functions

The `Interpreter.executeFn` method is used to execute a function. It's actually
called when someone uses the `Ė` (execute) element (note: this symbol was used
at the time of writing but it may be different when you're reading it). When
that element is used, the top of the stack is popped and if it's a function
object (a `VFun`), `Interpreter.executeFn` is called with that popped `VFun` as
input.

There, a new `Context` is made for the function to execute in. It's a
Frankensteinian monster made from the current context and the context the
functin was defined in.

- The new context's `inputs` (arguments) are obtained by popping the necessary
  number of elements off the outer context's stack (given by the `arity` field
  in `VFun`)
- The new context's `stack` is completely empty
- The new context's map of variables are copied from the context that the
  function was defined in (just the `Map`, not the values of the variables
  themselves). In addition to this, it has access to variables to the outer
  context (the `parent` context).

---

[implicit docs]: https://docs.scala-lang.org/scala3/book/ca-given-using-clauses.html
[Context]: /version-3/shared/src/main/scala/Context.scala
[Element-Impls]: Element-Impls.md
[union type]: https://docs.scala-lang.org/scala3/book/types-union.html
