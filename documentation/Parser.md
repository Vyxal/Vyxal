Unlike Vyxal 2, Vyxal 3 does not use a hand-written parser. Instead, it uses the `scala-parser-combinators` libary to turn tokenised programs into Abstract Syntax Trees. The way it works is very similar to EBNF, but in Scala syntax.

## EBNF Summary

```
Structure ::= StructureOpen Command* (Branch Command*)* StructureClose
Element ::= Command | Structure | Number | String | Element MonadicModifier | Element Element DyadicModifier | Element Element Element TriadicModifier | Element Element Element Element QuadraticModifier
List ::= ListOpen Element (Branch Element)* ListClose
```