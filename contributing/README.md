# Vyxal docs

This folder contains documentation for anyone wanting to contribute to Vyxal.

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
