Unlike Vyxal 2, Vyxal 3 does not use a hand-written lexer. Instead, it uses the `scala-parser-combinators` libary to turn programs into tokens. The way it works is
very similar to EBNF, but in Scala syntax.

## EBNF Summary

```
Digit ::= "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
Number ::= "0" | (Digit+ ("." (Digit)*)? ("ı" (Digit)*)?)
String ::= '"' [^"„”“]+ '"'
Comment ::= "##" [^\n]+
StructureOpen ::= "[" | "(" | "{" | "λ" | "ƛ" | "Ω" | "₳" | "µ" | "#@"
StructureClose ::= [\})]
AllStructureClose ::= \]
ListOpen ::= "#[" | "⟨"
ListClose ::= "#]" | "⟩"
Digraph ::= [∆øÞ#] AnyCharacter
MonadicModifier ::= [ᵃᵇᶜᵈᵉᶠᶢᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵘᵛᵂᵡᵞᶻᶴ¿′/\\~v@`ꜝ]
DyadicModifier ::= [″∥∦]
TriadicModifier ::= "‴"
QuadraticModifier ::= "⁴"
NewlineModifier ::= "ᵜ"
TieModifier ::= "ᵗ"
Branch ::= "|"
Newline :: = "\n"
Command ::= EverythingElse
GetVariable ::= "#<" ([a-z]|Digit|[A-Z])*
SetVariable ::= "#>" ([a-z]|Digit|[A-Z])*
```

