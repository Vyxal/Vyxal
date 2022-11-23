# Rules for Literate Mode Names

Allowed characters: `a-z`, `A-Z`, `0-9`, `-`, `?`, `!`, `*`, `+`, `=`, `&`, `%`, `>`, `<`

## Naming Conventions

- Names should be in `lower-case-with-dashes` format. However, uppercase letters may be used if appropriate
- Names should describe what the overloads of the element do.
- Types go before nouns (e.g. `str-split` or `str-format` instead of `split-str` or `format-str`).
- Question marks can be used for elements that return a boolean value (e.g. `contains?`, `equal?`).
- Keep in mind that literate mode is supposed to be a more readable version of Vyxal, so names should be descriptive and not too long.
- Also, keep in mind that literate mode names also serve as keywords for element search, so the more synonyms the better (where reasonable).