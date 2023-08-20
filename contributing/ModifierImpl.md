# Modifier Implementation

This doc will go over implementing modifiers. All the modifiers are defined in
[Modifiers.scala](/shared/src/Modifiers.scala). Each modifier takes a list of
ASTs and returns a new AST (they're akin to macros in other languages).

## The Template

```scala
"<symbol>" -> Modifier(
    "Formal Name | Alternate Formal Name ...",
    """|Description
       |usage: what it does to f""".stripMargin,
    List("literate-mode-keyword", ...),
    <arity>
  ) { case List(...) =>
    implementation
  }
```
