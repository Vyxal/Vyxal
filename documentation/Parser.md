Unlike Vyxal 2, Vyxal 3 does not use a hand-written parser. Instead, it uses the `scala-parser-combinators` libary to turn tokenised programs into Abstract Syntax Trees. The way it works is very similar to EBNF, but in Scala syntax.

## EBNF Summary

```
ListStructure ::= ListOpen Element* (Branch Element*)* ListClose
Literal ::= Number | String | ListStructure
NonStructureElement ::= Literal | Command | Modifier | GetVariable | SetVariable
Element ::= NonStructureElement | Structure
Structure ::= StructureOpen Element* (Branch Element*)* (StructureClose | AllStructureClose )
Modifier ::= MonadicModifier | DyadicModifier | TriadicModifier | QuadraticModifier
```

Note that there will be some differences in how the EBNF is implemented and how it is displayed here, but it is generally correct.
