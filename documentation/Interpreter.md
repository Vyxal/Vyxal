# The interpreter

This document will describe how the interpreter works (I say "will" because I'm hoping someone will finish/fix it in the future).

The [Interpreter](../version-3/shared/src/main/scala/Interpreter.scala) object is the entry point for executing code.
It has two methods, both named `execute`, with the following signatures:

```scala
def execute(code: String)(using ctx: Context): Unit
def execute(ast: AST)(using ctx: Context): Unit
```

Don't worry about the `using`, it'll be explained later. Scala lets you define a method with multiple parameter lists
so that you can do [currying](https://en.wikipedia.org/wiki/Currying) more easily. A method like `def foo(a: A)(b: B) = ...`
can be called like `foo(a)(b)`. It's basically the same as defining it like `def foo(a: A) = (b: B) => ...`.

The second is for internal use, the first is the one that code outside of the main Vyxal code will call.
The overload taking a `String` is basically just a helper of sorts around the second overload.
It will parse the string into an AST, call the second overload on that AST, and then print the top of the stack
or whatever the flags tell it to do.

## Context

### The `ctx` parameter

You'll notice that `Interpreter`'s `execute` methods take a parameter of type `Context`. As mentioned before, it's also in its own
parameter list, with a `using` in front of it. That `(using ctx: Context)` parameter list is called a using clause. The official
documentation for them is [here](https://docs.scala-lang.org/scala3/book/ca-given-using-clauses.html). Feel free to
read all of that if you like, but they go into a lot of detail that you don't really need to worry about right now.
Basically, a parameter list with a `using` at the front means that all the parameters in that list are **implicit** parameters.

Implicits are one of Scala's most powerful features, but here we're using it for the simplest of use cases - we're code golfers and
we want to write as little code as possible :). Since `ctx` is an implicit parameter, if there's an implicit
