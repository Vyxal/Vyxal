# Vyxal docs

This folder contains documentation on Vyxal. [elements.txt](./elements.txt) has all the elements.

You probably want to read the MD files on how the interpreter works in approximately this order:

1. [High level overview of lexing/tokenizing](./Lexer.md)
2. [High level overview of parsing](./Parser.md)
3. [High level overview of `Interpreter`](./Interpreter.md)
4. [Element implementation](./ElementDocumentation.md)
5. [Modifier implementation](./ModifierImpl.md)
6. [Writing tests](./Tests.md)

If any part of the documentation is lacking (either the Markdown files here or
doc comments in the source code), please let us know (through an issue or in
chat) so we can improve it.

You can use either sbt or Mill as your build tool. See [here](BuildTools.md) for
info on using sbt and Mill.

If you don't know Scala but do know another language, here are some helpful guides:

- [Scala for Python devs](https://docs.scala-lang.org/scala3/book/scala-for-python-devs.html)
- [Scala for Java devs](https://docs.scala-lang.org/scala3/book/scala-for-java-devs.html)
- [Scala for JavaScript devs](https://docs.scala-lang.org/scala3/book/scala-for-javascript-devs.html)

## Miscellaneous stuff

### What's the register?

TODO

### Context Variables

Vyxal has 2 context variables, `N` and `M`. They hold different values inside different
structures:

- Inside for loops, `N` is the current value, and `M` is the current index
- Inside while loops, `N` is the last condition value and `M` is the current index
- Inside lambdas/named functions, `N` is the argument

### Ghost variable

The so-called ghost variable is the variable named ``. You can get and set it just like a normal
variable. Of course, you need to make sure there aren't any valid identifier characters after the
`#$` / `#=` like `#$a1`, or the parser will think you're referring to some variable `a1`. If you're
already using the register, the ghost variable is a good way to store something off the stack
without using up too many characters. Just keep the limitation above in mind.
