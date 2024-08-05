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
100 { 3 5 pair mod 0 == } map { fold- times ? "Fizz" : "Buzz" end } map
```

Would turn into

```
100λ3 5;%0=}Mλ/×["Fizz"|"Buzz"}}M
```

## Using Literate Mode

Literate mode is avaliable with the `-l` flag. For example, `vyxal -l fizzbuzz.vyxal` will run the code
in `fizzbuzz.vyxal` in literate mode. You can also use the `--literate` flag, which is the same as
`-l`.

## Finer Details

### Keywords

Keywords can be found in [elements.txt](elements.txt). Keywords are case sensitive, so `if` and `If` are different keywords.

### Structures

Structures share the exact same syntax as they do in SBCS form. But instead of symbols, you use words
like `if`, `for` and `while`. Here's a list of all the structure words:

```
[} -> ? end
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

### Calling Functions in Variables

If a variable contains a function, you can call it by wrapping the variable name in
backticks. For example, `` `f` `` will call the function stored in `f`.

### Comments

Only `##` comments are avaliable at the moment. This is planned to change once it's figured out how to
do so.

### Groups

You can surround any series of tokens in `()`. Outside of token moving and modifier
groups, `()` are purely aesthetic. 

#### Modifier Groups

In addition to having keywords for each of `⸠ϩэЧᵈᵉᶠᴳ`, there's a special group
syntax to indicate how many elements to group, and the arity of the modifier.

```
(. *) -> 1 element, arity 1
(: * *) -> 2 elements, arity 1
(:. * * *) -> 3 elements, arity 1
(:: * * * *) -> 4 elements, arity 1
(, *) -> 1 element, arity 2
(; * *) -> 2 elements, arity 2
(;, * * *) -> 3 elements, arity 2
(;; * * * *) -> 4 elements, arity 2
```

A helpful way to remember this is that the number of dots/commas is the number of elements, and the shape of the bottom dots is the arity. `.` is arity 1, `,` is arity 2.

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

### `n't` suffix

Appending `n't` to a word will insert a logical negation element after the
word. For example, `containsn't` is the same as `contains not`.

Note that the resulting element is 2 bytes long, and counts as 2 units of
stuff for modifiers (unless arity grouped).

`n't`s can be stacked, with each `n't` cancelling out the previous chain of
`n't`s. For example, `containsn'tn't` is the same as `contains`.

This suffix serves no practical purpose other than to provide a laugh and
pontentially upvotes on your answer.

## Usage for Golfing

Literate mode is scored in UTF-8. That's because there is no codepage. If you want to use literate
mode and still enjoy the benefits of SBCS scoring, include the SBCS form in the answer, but mention
the link is to the literate form. Note that currently, the literate to SBCS translation is not
the most golfed (there's a lot of extraneous whitespace), so you may want to golf the SBCS form after
generating it.
