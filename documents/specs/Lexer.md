# Lexer / Tokenizer

The lexer, a.k.a. tokenizer, is responsible for converting code into a list of
tokens. The structure of various groups of tokens should not be considered at
this point. Vyxal breaks the mold for both conventional practical and golfing
languages - specifically, since function and variable names can be mutliple
characters (which most golfing languages do not have) but letters are typically
separate tokens elsewhere (which most practical languages do not have), lexing
rules need to consider the context of things.

The following list of token types should be comprehensive and complete.

Note that although the **General Tokens** include many things (including
elements, structural components, etc), that is fine as the parser will make the
determination later on.

## Literal Token

`TokenType.LITERAL` should be used for tokens that represent a fixed literal
value, including numbers, strings, etc. Note that digraphs do NOT belong here.
The token's value should be a string that will evaluate to the value being
returned. For example, `123` should return `"123"` and <code>`hello`</code>
should return `"'hello'"`.

### Compressed Strings and Numbers

Compressed strings and numbers both have their own unqiue `TokenType`: `TokenType.COMPRESSED_STRING` and `TokenType.COMPRESSED_NUMBER` respectively.

## Name Token

Following the tokens `°`, `@`, `→`, and `←`, Latin letters and underscores
should be grouped into one token of type `TokenType.NAME`, whose value should
be the name itself.

## General Tokens

Everything else should be a token of type `TokenType.OTHER`, whose value should
be the characters in the token. Digraphs should be grouped here.
