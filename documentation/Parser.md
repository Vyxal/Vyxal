Unlike Vyxal 2, Vyxal 3 does not use a hand-written parser. Instead, it uses the `scala-parser-combinators` libary to turn tokenised programs into Abstract Syntax Trees. The way it works is very similar to EBNF, but in Scala syntax.

## EBNF Summary

```
Structure ::= (StructureOpen Element* (Branch Element*)? StructureClose) | (StructureOpen (PartialStructure)* (Branch PartialStructure*)? AllStructureClose)
PartialStructure ::= StructureOpen Element* (Branch Element*)? StructureClose?
Element ::= Number | String | Structure | MonadicModifier Element | DyadicModifier Element Element | TriadicModifier Element Element Element | QuadraticModifier Element Element Element Element
NewlineAST ::= Newline 
```