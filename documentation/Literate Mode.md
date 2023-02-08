A common critique of golfing languages is that it looks like someone took the nearest brick they could
find and threw it at their keyboard. And that's understandable - when you see a whole bunch of unicode
gibberish, it's hard to tell what the hell is going on. That's why Vyxal has a literate mode, which
allows you to write Vyxal code in a more readable format. Literate mode is a superset of Vyxal, so
everything you can do in Vyxal you can do in literate mode. Hell, literate mode is just a wrapper over
the sbcs form - it's literally transpiled token for token - that's how much it's Vyxal!

If you've ever seen languages like Forth and Factor, then literate mode will look familiar to you.
Instead of the traditional one character = one command format, literate mode uses a one word = one
command format. A word is any sequence of `A-Z`, `a-z`, `0-9` and `-?!*+=&%><`.

For example:

```
100 { 3 5 pair mod 0 == } map { fold- times if "Fizz" else "Buzz" endif } map
```

Would turn into

```
100 λ3 5 ; % 0 =} M λ/× ["Fizz"|"Buzz"}} M
```

## Finer Details

### Keywords

Keywords can be found in [elements.txt](elements.txt). Keywords are case sensitive, so `if` and `If` are different keywords.

### Structures

Structures share the exact same syntax as they do in SBCS form. But instead of symbols, you use words
like `if`, `for` and `while`. Here's a list of all the structure words:

```
[} -> if endif
[|} -> if else endif
(} -> for endfor
(|} -> for do endfor
{} -> while endwhile
{|} -> while do endwhile
λ} -> lambda endlambda
```

Branches (`|`) can use any valid keyword for branches, as can `}`s. So `if do endif` is valid, as is `if branch endwhile`. This is because multiple words map to `|` and `}`.

### Lambda Syntax

As well as `lambda ... end`, lambdas can be written as `{code}`.

### Comments

Only `##` comments are avaliable at the moment. This is planned to change once it's figured out how to
do so.

### Groups

You can surround any series of tokens in `()`. This is purely for readability, and is not required. In
fact, the brackets are removed when the code is transpiled.

### Lists

Lists are the same as SBCS form except instead of `#[...#]`, you use `[...]`.

### Variables

Variables are mostly the same as SBCS form - get, set and unpack all have different sigils. The only
difference is variable unpacking, which just uses the same sigil as variable assignment. Here's the
translation table:

```
$name -> get
:=name -> set
f:>name -> name f= top of stack
:=[x|y|z] -> x, y, z = top of stack
```

The ghost variable still works.

### Numbers

Numeric literals are also the same as SBCS form, except instead of `ı`, you use `i`.

### Strings

Strings are no different to SBCS form.

### Raw SBCS

If you want to use SBCS form in literate mode, you can use `# ... #}`. For example:

```
100 { # 3× #} 5 add } map
```

is the same as

```
100 λ 3× 5+} M
```

## Usage for Golfing

Literate mode is scored in UTF-8. That's because there is no codepage. If you want to use literate
mode and still enjoy the benefits of SBCS scoring, include the SBCS form in the answer, but mention
the link is to the literate form. Note that currently, the literate to SBCS translation is not
the most golfed (there's a lot of extraneous whitespace), so you may want to golf the SBCS form after
generating it.