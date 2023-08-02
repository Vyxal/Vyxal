# Modifier Implementation

This doc will go over implementing modifiers. All the modifiers are defined in
[Modifiers.scala](/shared/src/Modifiers.scala).

## The Template

```scala
val modifierName = Modifier(
    "Formal Name | Alternate Formal Name ...",
    """|Description
      |usage: what it does to f
    """.stripMargin,
    List("literate-mode-keyword", ...)
  ) {
    implementation
  }
```
