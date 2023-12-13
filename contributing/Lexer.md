The role of a lexer in a programming language is to take a big program and split it into tiny little parts called "tokens". These tokens are the "parts of speech" of a programming language, just like how things like nouns, verbs and adjectives are parts of speech of spoken languages.

The Vyxal lexer takes programs as a big string of code and uses a library called `fastparse` to split it into tokens. Each token grammar rule is converted to a
combination of "parsers". These "parsers" are a sort of functional programming
version of a regular expression, in that instead of matching against a pattern,
precise logic is used to match token values.

For example, the rule for recognising a comment is:

```
"##" ~~/ CharsWhile(c => c != '\n' && c != '\r').!
```

Which reads as:

```
"##" // Match the literal string "##"
~~/ // Set the parser to not backtrack past the ##
CharsWhile(...).! // Consume characters while:
    c != '\n' // The character is not a newline
    && c != '\r' // The character is not a carriage return
.! // Return the characters that were consumed
```

The lexer will apply all rules in a recursive descent manner, meaning that it will try to match the first rule, then the second rule, then the third rule, etc. If a rule fails to match, it will backtrack and try the next rule. If a rule matches, it will not backtrack and will continue to match the next rule.

Here is a table of all the rules in the lexer:

| TokenName               | Rule                                                                                                                                                                                                   | Example                                                                                       | Notes                                                                                                                                                                                                                                                                                                                                                            |
|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Number                  | <pre>Number  = Decimal ("ı" Decimal?)? \|<br>        = "ı"<br>Decimal = PosNum "_"?<br>PosNum  = int ("." int?)? \|<br>        = "." int?<br>int     = "0" \|<br>        = [1-9][0-9]*<br></pre> | `3542`<br>`12.42`<br>`.5`<br>`0`<br>`6.`<br>`.`<br>`1ı5`<br>`4ı`<br>`ı3`<br>`.ı.`             |                                                                                                                                                                                                                                                                                                                                                                  |
| String                  | <pre>String = "\"" NonStringChar* StringCloser<br>NonStringChar = codepage minus "\"„”“"<br>StringCloser = "\"„”“"<br></pre>                                                                 | `"Hello, World"`<br>`"Hello` (at the end of a program)<br>`"Hello„`<br>`"Hello”`<br>`"Hello“` | The four different string closers represent the string type.<br><br>" -> Normal string<br>„ -> Close compressed number<br>” -> Close dictionary compressed string<br>“ -> Close base-255 compressed string                                                                                                                                                       |
| Single Character String | <pre>Character = "'" codepage char<br></pre>                                                                                                                                                       | `'E`<br>`''`<br>`'"`                                                                          | The character can be `'` and it won't try and include any more characters                                                                                                                                                                                                                                                                                        |
| Two Character String    | <pre>TwoCharacters = "ᶴ" codepage codepage<br></pre>                                                                                                                                           | `ᶴhi`<br>`ᶴᶴᶴ`                                                                                | The characters can include `ᶴ` and it won't try and include any more characters                                                                                                                                                                                                                                                                                  |
| Structure Open          | Any of "[({λƛΩ₳µ" and `#@`                                                                                                                                                                             | Any character that is in the group there                                                      |                                                                                                                                                                                                                                                                                                                                                                  |
| Structure Close         | Either `"}"` or `")"`                                                                                                                                                                                  | Either `"}"` or `")"`                                                                         | `")"` closes two structures at once                                                                                                                                                                                                                                                                                                                              |
| Close All Structures    | `]`                                                                                                                                                                                                    | `]`                                                                                           |                                                                                                                                                                                                                                                                                                                                                                  |
| List Open               | `#[` or `⟨`                                                                                                                                                                                            | Either character in the group                                                                 | The angled bracket is not in the codepage, but is accepted in utf-8 files                                                                                                                                                                                                                                                                                        |
| List Close              | `#]` or `⟩`                                                                                                                                                                                            | Either character in the group                                                                 | The angled bracket is not in the codepage, but is accepted in utf-8 files                                                                                                                                                                                                                                                                                        |
| Multigraph              | <pre>Multigraph = DigraphChar Digraphable \|<br>           = "#" TrigraphChar Digraphable<br>DigraphChar = "∆øÞk"<br>TrigraphChar = "[]$!=#>@{"<br>Digraphable = codepage minus trigraphs | `∆o`<br>`ør`<br>`Þk`<br>`k!`<br>`#\|`<br>`#:.`<br>`#:[`<br>`#,a`                              | `#:` followed by any character is a syntax trigraph. The `#:[` case needed to be explicitly defined for lexing priority reasons.<br><br>`#.` followed by any character is a updot trigraph. Something like `#.A` will be equivalent to a literal `Ȧ`<br><br>`#,` followed by any character is a downdot trigrahp. Something like `#,A` will be equivalent to `Ạ` |
| Command                 | *any command/element that is atomic*                                                                                                                                                                   | `a`<br>`e`<br>`k`<br>`E`                                                                      |                                                                                                                                                                                                                                                                                                                                                                  |
| Get Variable            | <pre>Get = "#$" [a-zA-Z0-9_]*</pre>                                                                                                                                                            | `#$variable`<br>`#$my_var`<br>`#$number_15`<br>`#$18_fingers`<br>`#$_abc`<br>`#$`             | Empty variable name gets ghost variable                                                                                                                                                                                                                                                                                                                          |
| Set Variable            | <pre>Set = "#=" [a-zA-Z0-9_]*</pre>                                                                                                                                                            | `#=variable`<br>`#=my_var`<br>`#=number_15`<br>`#=18_fingers`<br>`#=_abc`<br>`#=`             | Empty variable name sets ghost variable                                                                                                                                                                                                                                                                                                                          |
| Augment Variable        | <pre>Augment = "#>" [a-zA-Z0-9_]*</pre>                                                                                                                                                        | `#>variable`<br>`#>my_var`<br>`#>number_15`<br>`#>18_fingers`<br>`#>_abc`<br>`#>`             |                                                                                                                                                                                                                                                                                                                                                                  |
| Monadic Modifier        | *any monadic modifier*                                                                                                                                                                                 | `ᵃ`<br>`ᵇ`<br>`ᶜ`<br>`ᵈ`<br>`ᵉ`<br>`ᶠ`<br>`ᶢ`<br>`ᴴ`                                          | There are many more monadic modifiers                                                                                                                                                                                                                                                                                                                            |
| Dyadic Modifier         | *any dyadic modifier*                                                                                                                                                                                  | `″`<br>`∥`<br>`∦`                                                                             |                                                                                                                                                                                                                                                                                                                                                                  |
| Triadic Modifier        | *any triadic modifier*                                                                                                                                                                                 | `‴`                                                                                           |                                                                                                                                                                                                                                                                                                                                                                  |
| Tetradic Modifier       | *any tetradic modifier*                                                                                                                                                                                | `⁴`                                                                                           |                                                                                                                                                                                                                                                                                                                                                                  |
| Special Modifier        | *any special modifier*                                                                                                                                                                                 | `ᵗ`<br>`ᵜ`                                                                                    |                                                                                                                                                                                                                                                                                                                                                                  |
| Comment                 | <pre>Comment = "##" codepage* Newline</pre>                                                                                                                                                    | `## This is a comment`                                                                        |                                                                                                                                                                                                                                                                                                                                                                  |
| Branch                  | `\|`                                                                                                                                                                                                   | `\|`                                                                                          |                                                                                                                                                                                                                                                                                                                                                                  |
| Newline                 | `\n` (literal newline)                                                                                                                                                                                 | literal newline                                                                               | Included for parsing structures and modifiers that rely upon newlines                                                                                                                                                                                                                                                                                            |

After lexing has matched as many patterns as possible, either a list of `Token`s will be returned, or a `VyxalCompilationError` will be returned.