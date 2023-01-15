The role of a lexer in a programming language is to take a big program and split it into tiny little parts called "tokens". These tokens are the "parts of speech" of a programming language, just like how things like nouns, verbs and adjectives are parts of speech of spoken languages.

The Vyxal lexer takes programs as a big string of code and uses a library called `Parser Combinators`. A parser combinator is simply taking a whole bunch of rule units (tiny regexes) and combining them in a way which can be automatically matched against the input. If a section of the input program matches one of the combinator rules, it is assigned that token type. The lexer rules for Vyxal are listed below:

