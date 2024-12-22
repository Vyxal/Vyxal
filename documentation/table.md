| Symbol | Keywords | Arity | Vectorises | Peeks | Overloads |
|--------|--|-------|------------|-------|-----------|
| `⊞` | * `counts`</br>* `counts-of` | 1 | 🎵 | ⛓️‍💥 | **Counts of Items** (`|l|s|t|`): [#1.count(x) for x in set(#1)] |
| `÷` | * `divide`</br>* `string-pieces`</br>* `regex-split`</br>* `/`</br>* `div` | 2 | 🎶 | ⛓️‍💥 | **Division** (`|n|u|m|,|n|u|m|`): #1 / #2</br>**String into N Pieces** (`|s|t|r|,|n|u|m|`): Split string #1 into #2 pieces</br>**String into N Pieces** (`|n|u|m|,|s|t|r|`): Split string #2 into #1 pieces</br>**Regex Split** (`|s|t|r|,|s|t|r|`): Split #1 by regex #2 |
| `×` | * `multiply`</br>* `string-repeat`</br>* `ring-translate`</br>* `*`</br>* `times` | 2 | 🎶 | ⛓️‍💥 | **Multiplication** (`|n|u|m|,|n|u|m|`): #1 * #2 (#1 times #2)</br>**String Repeat** (`|s|t|r|,|n|u|m|`): Repeat string #1 #2 times</br>**String Repeat** (`|n|u|m|,|s|t|r|`): Repeat string #2 #1 times</br>**Ring Translate** (`|s|t|r|,|s|t|r|`): Ring translate #1 according to #2.  |
| `∧` | * `and`</br>* `&&`</br>* `logical-and` | 2 | 🎵 | ⛓️‍💥 | **Short Circuit And** (`|a|n|y|,|a|n|y|`): Short circuit and - if #2 is false, return #2, else return #1 |
| `∨` | * `or`</br>* `!!`</br>* `logical-or` | 2 | 🎵 | ⛓️‍💥 | **Short Circuit Or** (`|a|n|y|,|a|n|y|`): Short circuit or - if #2 is true, return #2, else return #1 |
| `¬` | * `not`</br>* `~`</br>* `logical-not` | 1 | 🎵 | ⛓️‍💥 | **Not** (`|a|n|y|`): if #1 is truthy, return False, else return True |
| `ʀ` | * `0->n`</br>* `lowercase`</br>* `range-0->n`</br>* `nrange-0` | 1 | 🎶 | ⛓️‍💥 | **Range 0** (`|n|u|m|`): Range from 0 to #1, exclusive</br>**Lowercase** (`|s|t|r|`): Lowercase #1 |
| `ʁ` | * `0->n++`</br>* `uppercase`</br>* `range-0->n++`</br>* `n+range-0` | 1 | 🎶 | ⛓️‍💥 | **Range 0 Inclusive** (`|n|u|m|`): Range from 0 to #1, inclusive</br>**Uppercase** (`|s|t|r|`): Uppercase #1 |
| `ɾ` | * `1->n++`</br>* `is-alpha?` | 1 | 🎶 | ⛓️‍💥 | **Range 1 Inclusive** (`|n|u|m|`): Range from 1 to #1, inclusive</br>**Is Character Alphabetical** (`|s|t|r|`): Check if #1 is alphabetical (i.e. is a letter) |
| `‹` | * `decrement`</br>* `--`</br>* `pad-to-8`</br>* `dec`</br>* `pad-8`</br>* `pad-to-byte` | 1 | 🎶 | ⛓️‍💥 | **Decrement** (`|n|u|m|`): #1 - 1</br>**Pad to 8** (`|s|t|r|`): Pad #1 to a length that is a multiple of 8 with '0's |
| `›` | * `increment`</br>* `++`</br>* `space-to-0`</br>* `replace-spaces-with-0s`</br>* `inc` | 1 | 🎶 | ⛓️‍💥 | **Increment** (`|n|u|m|`): #1 + 1</br>**Spaces to 0s** (`|s|t|r|`): Replace spaces in #1 with '0's |
| `!` | * `factorial`</br>* `!`</br>* `titlecase`</br>* `fact`</br>* `title`</br>* `fac` | 1 | 🎶 | ⛓️‍💥 | **Factorial** (`|n|u|m|`): Factorial of #1</br>**Titlecase** (`|s|t|r|`): Titlecase #1 |
| `$` | * `swap` | 2 | 🎵 | ⛓️‍💥 | **Swap** (`|a|n|y|,|a|n|y|`): Swap #1 and #2 on the stack: #1 #2 -> #2 #1 |
| `%` | * `mod`</br>* `modulo`</br>* `%`</br>* `remainder` | 2 | 🎶 | ⛓️‍💥 | **Modulo** (`|n|u|m|,|n|u|m|`): #1 % #2 (remainder of #1 divided by #2)</br>**String Format** (`|s|t|r|,|a|n|y|`): Format #1 with #2</br>**String Format** (`|a|n|y|,|s|t|r|`): Format #2 with #1 |
| `&` | * `append` | 2 | 🎵 | ⛓️‍💥 | **Append** (`|a|n|y|,|a|n|y|`): Append #2 to #1 |
| `*` | * `exponentiate`</br>* `pow`</br>* `**`</br>* `power` | 2 | 🎶 | ⛓️‍💥 | **Exponentiation** (`|n|u|m|,|n|u|m|`): #1 ** #2 |
| `+` | * `add`</br>* `+`</br>* `plus`</br>* `addition` | 2 | 🎶 | ⛓️‍💥 | **Addition** (`|n|u|m|,|n|u|m|`): #1 + #2</br>**String and Number Concatenation** (`|s|t|r|,|n|u|m|`): #1 + str(#2)</br>**String and Number Concatenation** (`|n|u|m|,|s|t|r|`): str(#1) + #2</br>**String Concatenation** (`|s|t|r|,|s|t|r|`): #1 + #2 |
| `,` | * `println`</br>* `stdout`</br>* `output`</br>* `out` | 1 | 🎵 | ⛓️‍💥 | **Print** (`|a|n|y|`): Print #1 to stdout, followed by a newline |
| `-` | * `subtract`</br>* `-`</br>* `minus`</br>* `subtraction`</br>* `regex-remove` | 2 | 🎶 | ⛓️‍💥 | **Subtraction** (`|n|u|m|,|n|u|m|`): #1 - #2</br>**Prepend/Append Hyphens** (`|s|t|r|,|n|u|m|`): #1 + #2 * '-'</br>**Prepend/Append Hyphens** (`|n|u|m|,|s|t|r|`): '-' * #1 + #2</br>**Regex Remove** (`|s|t|r|,|s|t|r|`): Remove matches of #2 from #1 |
| `:` | * `dup`</br>* `duplicate` | 1 | 🎵 | ⛓️‍💥 | **Duplicate** (`|a|n|y|`): Push #1 twice to the stack: #1 -> #1 #1 |
| `;` | * `pair`</br>* `cons` | 2 | 🎵 | ⛓️‍💥 | **Pair** (`|a|n|y|,|a|n|y|`): Push a list [#1, #2] to the stack: #1 #2 -> [#1, #2] |
| `\<` | * `less-than`</br>* `<`</br>* `lt` | 2 | 🎶 | ⛓️‍💥 | **Less Than** (`|s|c|l|,|s|c|l|`): #1 < #2 |
| `=` | * `equals`</br>* `==`</br>* `eq` | 2 | 🎶 | ⛓️‍💥 | **Equals** (`|s|c|l|,|s|c|l|`): #1 == #2 |
| `\>` | * `greater-than`</br>* `>`</br>* `gt` | 2 | 🎶 | ⛓️‍💥 | **Greater Than** (`|s|c|l|,|s|c|l|`): #1 > #2 |
| `?` | * `stdin`</br>* `input`</br>* `in` | 0 | 🎵 | ⛓️‍💥 | **Input**: Get the next input item, evaluated. |
| `@` | * `absolute-difference`</br>* `abs-diff`</br>* `levenstein`</br>* `to-overpairs` | 2 | 🎶 | ⛓️‍💥 | **Absolute Difference** (`|n|u|m|,|n|u|m|`): Absolute difference between #1 and #2</br>**Levenstein Distance** (`|s|t|r|,|s|t|r|`): Levenstein distance between #1 and #2</br>**Reduce Overlapping Pairs** (`|l|s|t|,|f|u|n|`): Reduce overlapping pairs in {#1|#2} by function {#2|#1} |
| `A` | * `all`</br>* `all?`</br>* `vowel?`</br>* `is-vowel`</br>* `is-vowel?` | 1 | 🎵 | ⛓️‍💥 | **All** (`|a|n|y|`): Are all elements of #1 are truthy |
| `B` | * `to-binary` | 1 | 🎶 | ⛓️‍💥 | **To Binary** (`|n|u|m|`): Convert #1 to binary</br>**String to Binary** (`|s|t|r|`): Convert each character in #1 to a binary representation of its unicode value |
| `C` | * `count` | 2 | 🎵 | ⛓️‍💥 | **Count** (`|l|s|t|,|s|c|l|`): Count occurrences of #2 in #1</br>**Count** (`|s|c|l|,|l|s|t|`): Count occurrences of #1 in #2</br>**Count** (`|l|s|t|,|l|s|t|`): Count occurrences of the list with shallower depth in the list with deeper depth |
| `D` | * `triplicate` | 1 | 🎵 | ⛓️‍💥 | **Triplicate** (`|a|n|y|`): Push #1 thrice to the stack: #1 -> #1 #1 #1 |
| `E` | * `2**n`</br>* `2pow`</br>* `eval`</br>* `2**` | 1 | 🎶 | ⛓️‍💥 | **2 to the Power of N** (`|n|u|m|`): 2 ** #1</br>**Eval** (`|s|t|r|`): Evaluate #1 |
| `F` | * `filter`</br>* `find`</br>* `index-of` | 2 | 🎵 | ⛓️‍💥 | **Filter** (`|f|u|n|,|a|n|y|`): Filter #1 by function #2</br>**Filter** (`|a|n|y|,|f|u|n|`): Filter #2 by function #1</br>**Find** (`|n|l|s|,|n|l|s|`): Find the index of #1 in #2. Switches #1 and #2 so that the haystack is the deeper list |
| `G` | * `max`</br>* `maximum`</br>* `gen` | 2 | 🎵 | ⛓️‍💥 | **Dyadic Maximum** (`|s|c|l|,|s|c|l|`): Maximum of #1 and #2</br>**Monadic Maximum** (`|l|s|t|`): Maximum of #1</br>**Generate Sequence** (`|n|l|s|,|f|u|n|`): Call #2 on previous results of #2, starting with #1. If #1 is not a list, it is made iterable |
| `H` | * `to-hex`</br>* `from-hex` | 1 | 🎶 | ⛓️‍💥 | **To Hex** (`|n|u|m|`): Convert #1 to hexadecimal</br>**From Hex** (`|s|t|r|`): Convert #1 from hexadecimal to a number. Inverse of 'to-hex' |
| `I` | * `interleave`</br>* `reject` | 2 | 🎵 | ⛓️‍💥 | **Interleave** (`|a|n|y|,|a|n|y|`): Interleave #1 and #2</br>**Reject** (`|a|n|y|,|f|u|n|`): Remove elements of #1 that satisfy function #2</br>**Reject** (`|f|u|n|,|a|n|y|`): Remove elements of #2 that satisfy function #1 |
| `J` | * `join`</br>* `concat` | 2 | 🎵 | ⛓️‍💥 | **Join** (`|l|s|t|,|s|c|l|`): Add #2 to the end of #1</br>**Join** (`|s|c|l|,|l|s|t|`): Prepend #1 to #2</br>**Join / Merge** (`|l|s|t|,|l|s|t|`): Add all elements of #2 to #1</br>**Number Pair** (`|n|u|m|,|n|u|m|`): Create a list of #1 and #2</br>**String Concatenation** (`|s|t|r|||n|u|m|,|s|t|r|||n|u|m|`): string(#1) + string(#2) (if either #1 or #2 is a string) |
| `K` | * `factors`</br>* `is-numeric?`</br>* `is-numeric` | 1 | 🎶 | ⛓️‍💥 | **Factors** (`|n|u|m|`): Get the factors of #1</br>**Is Numeric** (`|s|t|r|`): Check if #1 is numeric |
| `L` | * `length`</br>* `len` | 1 | 🎵 | ⛓️‍💥 | **Length** (`|a|n|y|`): Length of #1 |
| `M` | * `map`</br>* `mold`</br>* `multiplicity`</br>* `regex-match` | 2 | 🎶 | ⛓️‍💥 | **Map** (`|f|u|n|,|a|n|y|`): Map function #1 over #2</br>**Map** (`|a|n|y|,|f|u|n|`): Map function #2 over #1</br>**Mold** (`|l|s|t|,|l|s|t|`): Reshape #1 to the shape of #2</br>**Multiplicity** (`|n|u|m|,|n|u|m|`): How many times #1 divides #2</br>**Regex Match** (`|s|t|r|,|s|t|r|`): Return the first match of #2 in #1 |
| `N` | * `negate`</br>* `swapcase`</br>* `first>-1` | 1 | 🎶 | ⛓️‍💥 | **Negate** (`|n|u|m|`): -#1</br>**Negate** (`|s|t|r|`): Swap the case of each letter #1</br>**First Non-Negative Integer Where Predicate is True** (`|f|u|n|`): First non-negative integer where #1 is true |
| `O` | * `ord`</br>* `chr` | 1 | 🎶 | ⛓️‍💥 | **Character to Unicode** (`|s|t|r|`): Unicode value of each letter in #1</br>**Unicode to Character** (`|n|u|m|`): Character of each unicode value in #1 |
| `P` | * `prefixes` | 1 | 🎵 | ⛓️‍💥 | **Prefixes** (`|l|s|t|`): Get all prefixes of #1. Treats numbers as a list of digits |
| `Q` | * `remove-at`</br>* `regex-groups` | 2 | 🎵 | ⛓️‍💥 | **Remove At** (`|n|s|l|,|n|u|m|`): Remove the element at index #2 from #1</br>**Regex Groups** (`|s|t|r|,|s|t|r|`): Return the groups of the first match of #2 in #1 |
| `R` | * `range`</br>* `reduce`</br>* `regex-match?` | 2 | 🎵 | ⛓️‍💥 | **Range** (`|n|u|m|,|n|u|m|`): Range from #1 to #2, exclusive</br>**Reduce** (`|l|s|t|,|f|u|n|`): Reduce #1 by function #2</br>**Regex Match?** (`|s|t|r|,|s|t|r|`): Check if #2 matches #1 |
| `S` | * `sort` | 1 | 🎵 | ⛓️‍💥 | **Sort** (`|i|t|r|`): Sort #1 |
| `T` | * `transpose`</br>* `triple`</br>* `alpha-only?` | 1 | 🎵 | ⛓️‍💥 | **Transpose** (`|l|s|t|`): Transpose #1</br>**Triple** (`|n|u|m|`): #1 * 3</br>**Does String Contain Only Alphabetic Characters** (`|s|t|r|`): Check if #1 contains only alphabetic characters |
| `U` | * `uninterleave` | 1 | 🎵 | ⛓️‍💥 | **Uninterleave** (`|l|s|t|`): Uninterleave #1 |
| `V` | * `vectorse-reverse`</br>* `1-x` | 1 | 🎵 | ⛓️‍💥 | **Vectorise Reverse** (`|l|s|t|`): Reverse each item in #1</br>**1 - X** (`|n|u|m|`): 1 - #1 |
| `W` | * `wrap` | STACK | 🎵 | ⛓️‍💥 | **Wrap**: Wrap the entire stack into a list |
| `X` | * `cartesian-product` | 2 | 🎵 | ⛓️‍💥 | **Cartesian Product** (`|l|s|t|,|l|s|t|`): Cartesian product of #1 and #2 |
| `Y` | * `list-repeat` | 2 | 🎵 | ⛓️‍💥 | **List Repeat** (`|n|u|m|,|n|u|m|`): A list of #1 repeated #2 times. E.g. 3 4 -> [3, 3, 3, 3]</br>**List Repeat** (`|i|t|r|,|n|u|m|`): A list of #2 instances of string #1</br>**List Repeat** (`|n|u|m|,|i|t|r|`): A list of #1 instances of string #2</br>**Vectorised Repeat** (`|i|t|r|,|l|s|t|[|n|s|l|]|`): Repeat each element of #2 (#1|#1.length) times |
| `Z` | * `zip` | 2 | 🎵 | ⛓️‍💥 | **Zip** (`|l|s|t|,|l|s|t|`): Zip #1 and #2 |
| `^` | * `reverse-stack` | STACK | 🎵 | ⛓️‍💥 | **Reverse Stack**: Reverse the stack |
| `_` | * `pop`</br>* `discard` | 0 | 🎵 | ⛓️‍💥 | **Pop**: Pop the top of the stack |
| `a` | * `any`</br>* `any?`</br>* `uppercase?` | 1 | 🎵 | ⛓️‍💥 | **Any** (`|n|u|m|`): Are any digits of #1 truthy</br>**Is Uppercase** (`|s|t|r|`): Check if #1 is uppercase. With string.len > 1, vectorises over each character</br>**Any** (`|l|s|t|`): Are any elements of #1 truthy |
| `b` | * `from-binary` | 1 | 🎵 | ⛓️‍💥 | **Binary Digits** (`|n|u|m|`): Convert #1's list of digits from binary to base 10</br>**From Binary** (`|s|t|r|`): Convert #1 from binary to a number</br>**From Binary** (`|l|s|t|`): Convert #1 from binary to a number |
| `c` | * `contains`</br>* `contains?`</br>* `is-in` | 2 | 🎵 | ⛓️‍💥 | **Contains** (`|s|c|l|,|s|c|l|`): Is #2 in #1</br>**Contains** (`|l|s|t|,|s|c|l|`): Is #2 in #1</br>**Contains** (`|s|c|l|,|l|s|t|`): Is #1 in #2</br>**Contains** (`|l|s|t|,|l|s|t|`): Is the list with shallower depth in the list with deeper depth |
| `d` | * `double` | 1 | 🎵 | ⛓️‍💥 | **Double** (`|n|u|m|`): #1 * 2</br>**Double** (`|s|t|r|`): Append a copy of #1 to itself |
| `e` | * `even?`</br>* `is-even`</br>* `split-newlines`</br>* `/newline` | 1 | 🎶 | ⛓️‍💥 | **Is Even** (`|n|u|m|`): Is #1 even</br>**Split Newlines** (`|s|t|r|`): Split #1 by newlines |
| `f` | * `flatten` | 1 | 🎵 | ⛓️‍💥 | **List of Digits** (`|n|u|m|`): Push a list of the digits of #1 to the stack</br>**List of Characters** (`|s|t|r|`): Push a list of the characters of #1 to the stack</br>**Flatten** (`|l|s|t|`): Flatten #1 |
| `g` | * `min`</br>* `minimum`</br>* `2gen` | 2 | 🎵 | ⛓️‍💥 | **Dyadic Minimum** (`|s|c|l|,|s|c|l|`): Minimum of #1 and #2</br>**Monadic Minimum** (`|l|s|t|`): Minimum of #1</br>**Generate Sequence** (`|n|l|s|,|f|u|n|`): Call #2 as a dyad infinitely with items of #1 as starting values |
| `h` | * `head`</br>* `first` | 1 | 🎵 | ⛓️‍💥 | **Head** (`|a|n|y|`): First element of #1 |
| `i` | * `index`</br>* `at`</br>* `item-at`</br>* `nth-item`</br>* `collect-unique`</br>* `enclose`</br>* `@<=` | 2 | 🎵 | ⛓️‍💥 | **Nth Element** (`|i|t|r|,|n|u|m|`): Get the #2th element of #1</br>**Nth Element** (`|n|u|m|,|i|t|r|`): Get the #1th element of #2</br>**Vectorised Index** (`|i|t|r|,|l|s|t|[|n|u|m|]|`): [#1[_] for _ in #2]</br>**String Enclose** (`|s|t|r|,|s|t|r|`): enclose #2 in #1 (#1[0:len(#1)//2] + #2 + #1[len(#1)//2:])</br>**Object Member Retrieval** (`|o|b|j|,|s|t|r|`): #1.#2</br>**Object Member Retrieval** (`|s|t|r|,|o|b|j|`): #2.#1</br>**Collect Unique Values (+ Initial Value)** (`|a|n|y|,|f|u|n|`): Apply #2 on #1 and collect unique values. Does include the initial value. |
| `j` | * `join-on` | 2 | 🎵 | ⛓️‍💥 | **Join On** (`|l|s|t|,|s|c|l|`): Join #1 on #2</br>**Join On** (`|s|c|l|,|l|s|t|`): Join #2 on #1</br>**Intersperse** (`|l|s|t|,|l|s|t|`): Intersperse elements of #2 within #1 (e.g. [1, [2,3], 4] [5, 6] -> [1, 5, 6, [2, 3], 5, 6, 4]) |
| `l` | * `log`</br>* `logarithm`</br>* `scan-fixpoint`</br>* `scan-fix`</br>* `same-length?`</br>* `same-length`</br>* `length-equals?`</br>* `length-equals`</br>* `len-eq?` | 2 | 🎶 | ⛓️‍💥 | **Logarithm** (`|n|u|m|,|n|u|m|`): Log base #2 of #1</br>**Scan Fixpoint** (`|f|u|n|,|a|n|y|`): Repeatedly apply #1 to #2 until it doesn't change</br>**Scan Fixpoint** (`|a|n|y|,|f|u|n|`): Repeatedly apply #1 to #2 until it doesn't change</br>**Same Length** (`|s|t|r|,|s|t|r|`): Are #1 and #2 the same length</br>**String Length Equals** (`|s|t|r|,|n|u|m|`): Is the length of #1 equal to #2</br>**String Length Equals** (`|n|u|m|,|s|t|r|`): Is the length of #2 equal to #1 |
| `m` | * `ctx-secondary`</br>* `ctx2`</br>* `ctx-m`</br>* `context-m`</br>* `context-secondary` | 0 | 🎵 | ⛓️‍💥 | **Context Secondary**: Push the secondary context variable to the stack |
| `n` | * `ctx-primary`</br>* `ctx`</br>* `ctx-n`</br>* `context-n`</br>* `context-primary` | 0 | 🎵 | ⛓️‍💥 | **Context Primary**: Push the primary context variable to the stack |
| `o` | * `overlapping-pairs`</br>* `overlapping-sliding-window`</br>* `windows` | 2 | 🎵 | ⛓️‍💥 | **Windows** (`|l|s|t|,|l|s|t|[|n|u|m|]|`): Get overlapping windows of #1 with a window of size #2</br>**Overlapping Slices** (`|a|n|y|,|n|u|m|`): Get overlapping pairs of iterable(#1) with a window of size #2</br>**Overlapping Slices** (`|n|u|m|,|a|n|y|`): Get overlapping pairs of iterable(#2) with a window of size #1 |
| `p` | * `prepend` | 2 | 🎵 | ⛓️‍💥 | **Prepend** (`|a|n|y|,|a|n|y|`): Prepend #2 to #1 |
| `q` | * `quotify` | 1 | 🎵 | ⛓️‍💥 | **Quotify** (`|a|n|y|`): Cast #1 to a string and wrap in quotes |
| `r` | * `replace` | 3 | 🎵 | ⛓️‍💥 | **Replace** (`|n|s|l|,|n|s|l|,|n|s|l|`): Replace all occurrences of #2 in #1 with #3</br>**Zip-With** (`|l|s|t|,|l|s|t|,|f|u|n|`): Zip #1 and #2 and apply #3 to each pair</br>**Zip-With** (`|l|s|t|,|f|u|n|,|l|s|t|`): Zip #1 and #2 and apply #3 to each pair</br>**Zip-With** (`|f|u|n|,|l|s|t|,|l|s|t|`): Zip #1 and #2 and apply #3 to each pair |
| `s` | * `split` | 2 | 🎵 | ⛓️‍💥 | **Split** (`|a|n|y|,|a|n|y|`): Split #1 by #2 |
| `t` | * `tail`</br>* `last` | 1 | 🎵 | ⛓️‍💥 | **Tail** (`|a|n|y|`): Last element of #1 |
| `u` | * `unique` | 1 | 🎵 | ⛓️‍💥 | **Unique** (`|l|s|t|`): Unique elements of #1</br>**Unique By Function** (`|l|s|t|,|f|u|n|`): Unique elements of #1 by applying #2 |
| `w` | * `wrap-in-list` | 1 | 🎵 | ⛓️‍💥 | **Wrap in List** (`|a|n|y|`): Wrap #1 in a list |
| `x` | * `recurse` | STACK | 🎵 | ⛓️‍💥 | **Recurse**: Recursively call the current function (or the top-level program if not in a function) |
| `y` | * `transliterate`</br>* `call-while` | 3 | 🎵 | ⛓️‍💥 | **Transliterate** (`|n|s|l|,|n|s|l|,|n|s|l|`): Replace all occurrences of #2 in #1 with #3</br>**Call While** (`|f|u|n|,|f|u|n|,|a|n|y|`): While #1(#3) is true, #3 = #2(#3). Return the result. Type switchable. |
| `z` | * `zip-with-filler` | 2 | 🎵 | ⛓️‍💥 | **Zip With Filler** (`|l|s|t|,|a|n|y|`): Transpose #1, filling empty spaces with #2 |
| `⨥` | * `+2`</br>* `add-2`</br>* `++++`</br>* `inc-inc`</br>* `strlen==1` | 1 | 🎶 | ⛓️‍💥 | **Add 2** (`|n|u|m|`): #1 + 2</br>**String Length Equals 1** (`|s|t|r|`): Is the length of #1 equal to 1 |
| `⨪` | * `-2`</br>* `subtract-2`</br>* `----`</br>* `dec-dec` | 1 | 🎶 | ⛓️‍💥 | **Subtract 2** (`|n|u|m|`): #1 - 2 |
| `∑` | * `sum`</br>* `sum-of`</br>* `+/`</br>* `/+`</br>* `sigma`</br>* `sigma-in-ohio` | 1 | 🎵 | ⛓️‍💥 | **Sum** (`|l|s|t|`): Sum of #1</br>**Join and Evaluate** (`|l|s|t|[|a|t| |l|e|a|s|t| |1| |s|t|r|]|`): Join #1 and evaluate the result |
| `∏` | * `product`</br>* `product-of`</br>* `*/`</br>* `*/` | 1 | 🎵 | ⛓️‍💥 | **Product** (`|l|s|t|`): Product of #1 |
| `σ` | * `cumulative-sums`</br>* `cumsums`</br>* `cumsum`</br>* `cum-sum`</br>* `-_-` | 1 | 🎵 | ⛓️‍💥 | **Cumulative Sums** (`|l|s|t|`): Cumulative sums of #1 |
| `⇧` | * `grade-up` | 1 | 🎵 | ⛓️‍💥 | **Grade Up** (`|l|s|t|`): Indices that would sort #1 |
| `⇩` | * `grade-down` | 1 | 🎵 | ⛓️‍💥 | **Grade Down** (`|l|s|t|`): Indices that would sort #1 in reverse |
| `∪` | * `union`</br>* `set-union` | 2 | 🎵 | ⛓️‍💥 | **Union** (`|l|s|t|,|l|s|t|`): Union of #1 and #2 |
| `∩` | * `intersection`</br>* `set-intersection` | 2 | 🎵 | ⛓️‍💥 | **Intersection** (`|l|s|t|,|l|s|t|`): Intersection of #1 and #2 |
| `⊍` | * `set-xor` | 2 | 🎵 | ⛓️‍💥 | **Set XOR** (`|l|s|t|,|l|s|t|`): Set XOR of #1 and #2 |
| `⦰` | * `set-difference`</br>* `set-diff` | 2 | 🎵 | ⛓️‍💥 | **Set Difference** (`|l|s|t|,|l|s|t|`): Set difference of #1 and #2 |
| `«` | * `left-shift`</br>* `<<` | 2 | 🎶 | ⛓️‍💥 | **Left Shift** (`|n|u|m|,|n|u|m|`): #1 << #2</br>**Prepend Spaces to Given Length** (`|s|t|r|,|n|u|m|`): Prepend spaces to string #1 until it is #2 characters long</br>**Prepend Spaces to Given Length** (`|n|u|m|,|s|t|r|`): Prepend spaces to string #2 until it is #1 characters long</br>**Prepend Spaces to Length of Second String** (`|s|t|r|,|s|t|r|`): Prepend spaces to string #1 until it is the length of #2 |
| `»` | * `right-shift`</br>* `>>` | 2 | 🎶 | ⛓️‍💥 | **Right Shift** (`|n|u|m|,|n|u|m|`): #1 >> #2</br>**Append Spaces to Given Length** (`|s|t|r|,|n|u|m|`): Append spaces to string #1 until it is #2 characters long</br>**Append Spaces to Given Length** (`|n|u|m|,|s|t|r|`): Append spaces to string #2 until it is #1 characters long</br>**Append Spaces to Length of Second String** (`|s|t|r|,|s|t|r|`): Append spaces to string #1 until it is the length of #2 |
| `Ɠ` | * `max-peek` | 1 | 🎵 | 👀 | **Max Peek** (`|l|s|t|`): Maximum of #1 without popping |
| `ɠ` | * `min-peek` | 1 | 🎵 | 👀 | **Min Peek** (`|l|s|t|`): Minimum of #1 without popping |
| `Ġ` | * `zip-max` | 2 | 🎶 | ⛓️‍💥 | **Zipped Maximum** (`|l|s|t|,|l|s|t|`): Maximum of corresponding elements of #1 and #2</br>**Vectorised Maximum** (`|l|s|t|,|s|c|l|`): Maximum of #2 and #1</br>**Vectorised Maximum** (`|s|c|l|,|l|s|t|`): Maximum of #1 and #2 |
| `⌈` | * `ceil`</br>* `ceiling`</br>* `split-on-spaces` | 1 | 🎶 | ⛓️‍💥 | **Ceiling** (`|n|u|m|`): Ceiling of #1</br>**Split on Spaces** (`|s|t|r|`): Split #1 by spaces |
| `⌊` | * `floor`</br>* `str-to-num` | 1 | 🎶 | ⛓️‍💥 | **Floor** (`|n|u|m|`): Floor of #1</br>**String to Number** (`|s|t|r|`): Convert #1 to a number, ignoring non-digit characters. Returns 0 if no digits are found |
| `⊖` | * `0-slice`</br>* `take`</br>* `0-take` | 2 | 🎵 | ⛓️‍💥 | **0 Slice** (`|i|t|r|,|n|u|m|`): First #2 elements of #1</br>**0 Slice** (`|n|u|m|,|i|t|r|`): First #1 elements of #2</br>**APL Style Take** (`|l|s|t|,|l|s|t|[|n|u|m|]|`): APL style take |
| `⌽` | * `1-slice`</br>* `tail-take`</br>* `1-take` | 2 | 🎵 | ⛓️‍💥 | **1 Slice** (`|i|t|r|,|n|u|m|`): First #2 elements of #1[1:]</br>**1 Slice** (`|n|u|m|,|i|t|r|`): First #1 elements of #2[1:] |
| `£` | * `set-register` | 1 | 🎵 | ⛓️‍💥 | **Set Register** (`|a|n|y|`): Set the register to #1 |
| `¥` | * `get-register` | 0 | 🎵 | ⛓️‍💥 | **Get Register**: Push the register to the stack |
| `↜` | * `rotate-stack-left` | STACK | 🎵 | ⛓️‍💥 | **Rotate Stack Left**: Rotate the stack left |
| `↝` | * `rotate-stack-right` | STACK | 🎵 | ⛓️‍💥 | **Rotate Stack Right**: Rotate the stack right |
| `⬳` | * `rot-left` | 1 | 🎵 | ⛓️‍💥 | **Rotate Left** (`|l|s|t|||s|t|r|`): Rotate #1 left</br>**Rotate Left** (`|l|s|t|||s|t|r|,|n|u|m|`): Rotate #1 left #2 times. Right if #2 is negative |
| `⟿` | * `rot-right` | 1 | 🎵 | ⛓️‍💥 | **Rotate Right** (`|l|s|t|||s|t|r|`): Rotate #1 right</br>**Rotate Right** (`|l|s|t|||s|t|r|,|n|u|m|`): Rotate #1 right #2 times. Left if #2 is negative |
| `≜` | * `assign` | 3 | 🎵 | ⛓️‍💥 | **List Assign** (`|a|n|y|,|n|u|m|,|n|s|l|`): #1[#2] = #3</br>**Augmented List Assignment** (`|a|n|y|,|n|u|m|,|f|u|n|`): #1[#2] = #3(#1[#2])</br>**Vectorised Augmented List Assignment** (`|l|s|t|,|l|s|t|[|n|u|m|]|,|f|u|n|`): #1[_] = #3(#1[_]) for _ in #2</br>**Zipped Assignment** (`|l|s|t|,|l|s|t|,|l|s|t|`): #1[ind] = val for ind, val in zip(#2, #3)</br>**Regex String Replacement** (`|s|t|r|,|s|t|r|,|s|t|r|`): Replace all occurrences of #2 in #1 with #3</br>**Regex Substitution** (`|s|t|r|,|s|t|r|,|f|u|n|`): Replace all occurrences of #2 in #1 with the result of #3</br>**Object Member Assignment** (`|o|b|j|,|s|t|r|,|a|n|y|`): #1.#2 = #3 |
| `⎀` | * `insert` | 3 | 🎵 | ⛓️‍💥 | **Insert** (`|a|n|y|,|n|u|m|,|a|n|y|`): Insert #3 into #1 at index #2</br>**Insert** (`|a|n|y|,|l|s|t|[|n|u|m|]|,|s|c|l|`): Insert #3 into #1 at indices #2</br>**Insert** (`|a|n|y|,|l|s|t|[|n|u|m|]|,|l|s|t|`): Insert items of #3 into #1 at indices #2 |
| `◲` | * `sublists` | 1 | 🎵 | ⛓️‍💥 | **Sublists** (`|a|n|y|`): All sublists of #1 |
| `⊢` | * `10-to-base`</br>* `all-regex-matches` | 2 | 🎵 | ⛓️‍💥 | **10 to Base** (`|n|u|m|,|n|u|m|`): Convert #1 to base #2</br>**10 to Base** (`|n|u|m|,|s|t|r|||l|s|t|`): Convert #1 to base len(#2) using the items of #2</br>**10 to Base** (`|l|s|t|,|n|u|m|`): Convert each item in #1 to base #2</br>**10 to Base** (`|l|s|t|,|l|s|t|`): Convert each item in #1 to the base of the corresponding item in #2</br>**All Regex Matches** (`|s|t|r|,|s|t|r|`): All matches of #2 in #1 |
| `⊣` | * `base-to-10` | 2 | 🎵 | ⛓️‍💥 | **Base to 10** (`|s|c|l|,|n|u|m|`): Convert #1 from base #2 to base 10, assuming a base that is a prefix of [0-9A-Z] for strings</br>**Base to 10** (`|l|s|t|[|n|u|m|||s|t|r|]|,|n|u|m|`): Convert #1 from base #2 to base 10, using the items of #1 as digits</br>**Base to 10** (`|l|s|t|,|n|u|m|`): Convert each item in #1 from base #2 to base 10, assuming a base that is a prefix of [0-9A-Z] for strings |
| `ɦ` | * `head-peek` | 1 | 🎵 | 👀 | **Head Peek** (`|l|s|t|`): First element of #1 without popping |
| `ʈ` | * `tail-peek` | 1 | 🎵 | 👀 | **Tail Peek** (`|l|s|t|`): Last element of #1 without popping |
| `ᐐ` | * `init` | 1 | 🎵 | ⛓️‍💥 | **Init** (`|a|n|y|`): All but the last element of #1 |
| `ᐵ` | * `drop` | 2 | 🎵 | ⛓️‍💥 | **Drop** (`|a|n|y|,|n|u|m|`): All but the first #2 elements of #1</br>**Drop** (`|n|u|m|,|a|n|y|`): All but the first #1 elements of #2</br>**APL Style Drop** (`|l|s|t|,|l|s|t|[|n|u|m|]|`): APL style drop |
| `ᐕ` | * `behead` | 1 | 🎵 | ⛓️‍💥 | **Behead** (`|a|n|y|`): All but the first element of #1 |
| `½` | * `half`</br>* `halve` | 1 | 🎶 | ⛓️‍💥 | **Halve** (`|n|u|m|`): #1 / 2</br>**Two String Halves** (`|s|t|r|`): Split #1 in half |
| `ƶ` | * `range-to-length` | 1 | 🎵 | ⛓️‍💥 | **Range to Length** (`|l|s|t|`): Range from 0 to len(#1) - 1 |
| `Ƶ` | * `range-to-length-1` | 1 | 🎵 | ⛓️‍💥 | **Range to Length 1** (`|l|s|t|`): Range from 1 to len(#1) |
| `⁰` | * `first-input`</br>* `input-0` | 0 | 🎵 | ⛓️‍💥 | **First Input**: Push the first input to the stack |
| `¹` | * `second-input`</br>* `input-1` | 0 | 🎵 | ⛓️‍💥 | **Second Input**: Push the second input to the stack |
| `²` | * `square`</br>* `string-pairs` | 1 | 🎶 | ⛓️‍💥 | **Square** (`|n|u|m|`): #1 ** 2</br>**String Pairs** (`|s|t|r|`): Split #1 into pairs of characters |
| `³` | * `cube`</br>* `string-triples` | 1 | 🎶 | ⛓️‍💥 | **Cube** (`|n|u|m|`): #1 ** 3</br>**String Triples** (`|s|t|r|`): Split #1 into triples of characters |
| `⅟` | * `reciprocal`</br>* `inverse`</br>* `1/`</br>* `without-whitespace`</br>* `no-space`</br>* `spaceless` | 1 | 🎶 | ⛓️‍💥 | **Reciprocal** (`|n|u|m|`): 1 / #1</br>**Without Whitespace** (`|s|t|r|`): Remove all whitespace from #1 |
| `⇄` | * `reverse` | 1 | 🎵 | ⛓️‍💥 | **Reverse** (`|a|n|y|`): Reverse #1 |
| `⧖` | * `permutations` | 1 | 🎵 | ⛓️‍💥 | **Permutations** (`|a|n|y|`): All permutations of #1 |
| `‰` | * `divmod` | 2 | 🎶 | ⛓️‍💥 | **Divmod** (`|n|u|m|,|n|u|m|`): Divmod of #1 and #2 ([#1 // #2, #1 % #2]) |
| `≛` | * `divides?`</br>* `append-spaces`</br>* `regex-span` | 2 | 🎵 | ⛓️‍💥 | **Divides?** (`|n|u|m|,|n|u|m|`): #2 % #1 == 0</br>**Append Spaces** (`|s|t|r|,|n|u|m|`): Append #2 spaces to #1</br>**Append Spaces** (`|n|u|m|,|s|t|r|`): Append #1 spaces to #2</br>**Regex Span** (`|s|t|r|,|s|t|r|`): Span of regex match of pattern #2 in #1 |
| `ℭ` | * `combinations-with-replacement` | 2 | 🎵 | ⛓️‍💥 | **Combinations with Replacement** (`|i|t|r|,|n|u|m|`): All combinations of #1 of length #2 with replacement</br>**Combinations with Replacement** (`|n|u|m|,|i|t|r|`): All combinations of #2 of length #1 with replacement</br>**Combinations of Range with Replacement** (`|n|u|m|,|n|u|m|`): All combinations of range(#1) of length #2 with replacement |
| `℈` | * `combinations-without-replacement` | 2 | 🎵 | ⛓️‍💥 | **Combinations without Replacement** (`|i|t|r|,|n|u|m|`): All combinations of #1 of length #2 without replacement</br>**Combinations without Replacement** (`|n|u|m|,|i|t|r|`): All combinations of #2 of length #1 without replacement</br>**Combinations of Range without Replacement** (`|n|u|m|,|n|u|m|`): All combinations of range(#1) of length #2 without replacement |
| `⦷` | * `abs`</br>* `absolute-value`</br>* `keep-letters` | 1 | 🎶 | ⛓️‍💥 | **Absolute Value** (`|n|u|m|`): Absolute value of #1</br>**Keep Letters** (`|s|t|r|`): Keep only the letters of #1 |
| `Ϣ` | * `chunk-to-length`</br>* `partition-to-length` | 2 | 🎵 | ⛓️‍💥 | **Chunk to Length** (`|a|n|y|,|n|u|m|`): Chunk #1 into parts of length #2</br>**Chunk to Length** (`|n|u|m|,|a|n|y|`): Chunk #2 into parts of length #1</br>**Partition to Lengths** (`|i|t|r|,|l|s|t|[|n|u|m|]|`): Partition #1 into parts of lengths #2 |
| `≤` | * `less-than-or-equal`</br>* `lte`</br>* `<=` | 2 | 🎶 | ⛓️‍💥 | **Less Than or Equal** (`|s|c|l|,|s|c|l|`): #1 <= #2 |
| `≥` | * `greater-than-or-equal`</br>* `gte`</br>* `>=` | 2 | 🎶 | ⛓️‍💥 | **Greater Than or Equal** (`|s|c|l|,|s|c|l|`): #1 >= #2 |
| `≠` | * `not-equal`</br>* `neq`</br>* `!=`</br>* `=n't`</br>* `eqn't`</br>* `equaln't` | 2 | 🎶 | ⛓️‍💥 | **Not Equal** (`|s|c|l|,|s|c|l|`): str(#1) != str(#2) |
| `≡` | * `exact-equals`</br>* `eq+`</br>* `===` | 2 | 🎵 | ⛓️‍💥 | **Equals** (`|a|n|y|,|a|n|y|`): Does #1 exactly equal #2 |
| `•` | * `dot-product`</br>* `bijective-base`</br>* `first-predicate-index` | 2 | 🎵 | ⛓️‍💥 | **Dot Product** (`|l|s|t|,|l|s|t|`): Dot product of #1 and #2</br>**Bijective Base Conversion** (`|n|u|m|,|n|u|m|`):  Convert #1 to bijective base #2</br>**First Index Where Predicate True** (`|n|s|l|,|f|u|n|`): Index of the first value in #1 where function #2 is true</br>**First Index Where Predicate True** (`|f|u|n|,|n|s|l|`): Index of the first value in #2 where function #1 is true |
| `±` | * `signum` | 1 | 🎶 | ⛓️‍💥 | **Signum** (`|n|u|m|`): Sign of #1 |
| `†` | * `lengths-of-consecutives` | 1 | 🎵 | ⛓️‍💥 | **Lengths of Consecutives** (`|l|s|t|`): Lengths of consecutive runs of equal elements in #1 |
| `⎙` | * `peek-print` | 1 | 🎵 | 👀 | **Peek Print** (`|a|n|y|`): Print #1 without popping |
| `✒` | * `print` | 1 | 🎵 | ⛓️‍💥 | **Print** (`|a|n|y|`): Print #1 without a trailing newline |
| `≓` | * `mirror` | 1 | 🎵 | ⛓️‍💥 | **Mirror** (`|a|n|y|`): Mirror #1 (#1 + reverse(#1)), as the original type |
| `Ͼ` | * `vectorised-sums`</br>* `v/+` | 1 | 🎶 | ⛓️‍💥 | **Vectorised Sums** (`|l|s|t|`): Sum of each item in #1. Functionally equivalent to `¨Σ` |
| `⛭` | * `exec`</br>* `10**`</br>* `call`</br>* `@` | 1 | 🎶 | ⛓️‍💥 | **10 to the Power of** (`|n|u|m|`): 10 ** #1</br>**Execute** (`|s|t|r|`): Execute #1 as Vyxal code</br>**Call Function** (`|f|u|n|`): Call function #1 |
| `⏟` | * `modular`</br>* `matrix-multiply`</br>* `regex-full-match?` | 2 | 🎵 | ⛓️‍💥 | **Every Nth Element** (`|i|t|r|,|n|u|m|`): Every #2th element of #1</br>**Every Nth Element** (`|n|u|m|,|i|t|r|`): Every #1th element of #2</br>**Matrix Multiply** (`|l|s|t|,|l|s|t|`): Matrix multiply #1 and #2</br>**Regex Full Match?** (`|s|t|r|,|s|t|r|`): Does pattern #2 fully match #1 |
| `⌭` | * `is-prime`</br>* `prime?`</br>* `quine-cheese` | 1 | 🎶 | ⛓️‍💥 | **Is Prime** (`|n|u|m|`): Is #1 a prime number?</br>**Quine Cheese** (`|s|t|r|`): Quotify #1 and prepend it to #1. (Useful for quines like `"⌭"⌭`) |
| `⏜` | * `over` | STACK | 🎵 | ⛓️‍💥 | **Over**: Duplicate the item below the top of the stack -> #2 #1 #2 |
| `⍢` | * `parity`</br>* `bit`</br>* `last-half` | 1 | 🎶 | ⛓️‍💥 | **Parity** (`|n|u|m|`): Parity of #1 (1 if odd, 0 if even) --> #1 % 2</br>**Last String Half** (`|s|t|r|`): Last half of #1 |
| `ℂ` | * `ncr`</br>* `choose`</br>* `characters-same?`</br>* `fixpoint-collect` | 2 | 🎶 | ⛓️‍💥 | **NCR | N Choose R** (`|n|u|m|,|n|u|m|`): nCr of #1 and #2 (n choose r)</br>**Characters Same?** (`|s|t|r|,|s|t|r|`): Are all characters in #1 the same as #2?</br>**Fixpoint Collect** (`|f|u|n|,|a|n|y|`): Repeatedly apply #1 on #2 until a fixed point is reached, collecting intermediate results</br>**Fixpoint Collect** (`|a|n|y|,|f|u|n|`): Repeatedly apply #2 on #1 until a fixed point is reached, collecting intermediate results |
| `⌹` | * `list-partitions`</br>* `integer-partitions` | 1 | 🎵 | ⛓️‍💥 | **Integers Partitions** (`|n|u|m|`): All possible ways to sum positive integers to #1</br>**List Partitions** (`|i|t|r|`): All possible ways to partition #1 into sublists |
| `⏚` | * `powerset` | 1 | 🎵 | ⛓️‍💥 | **Powerset** (`|a|n|y|`): Powerset of #1 |
| `↯` | * `inclusive-range`</br>* `sort-by`</br>* `regex-split-keep-delimiters` | 2 | 🎶 | ⛓️‍💥 | **Inclusive Range** (`|n|u|m|,|n|u|m|`): Inclusive range from #1 to #2</br>**Sort By** (`|n|s|l|,|f|u|n|`): Sort list #1 (range if num) by function #2</br>**Sort By** (`|f|u|n|,|n|s|l|`): Sort list #2 (range if num) by function #1</br>**Regex Split Keep Delimiters** (`|s|t|r|,|s|t|r|`): Split #1 by regex #2, keeping the delimiters |
| `⊠` | * `cartesian-power`</br>* `regex-index` | 2 | 🎵 | ⛓️‍💥 | **Cartesian Power** (`|a|n|y|,|n|u|m|`): Cartesian power of #1 to the power of #2</br>**Cartesian Power** (`|n|u|m|,|a|n|y|`): Cartesian power of #2 to the power of #1</br>**Regex Index** (`|s|t|r|,|s|t|r|`): Return first index of pattern match #2 in target string #1, -1 if not found</br>**Self-Cartesian Power** (`|i|t|r|,|a|n|y|`): Push #1, and then push the cartesian product of #2 with itself |
| `⚅` | * `random-choice`</br>* `random-element`</br>* `randint`</br>* `random` | 1 | 🎵 | ⛓️‍💥 | **Random Choice** (`|i|t|r|`): Random element of #1</br>**Random Integer** (`|n|u|m|`): Random integer from 0 to #1 |
| `æ` | * `bifuricate`</br>* `bifur`</br>* `bif`</br>* `furry`</br>* `uwu`</br>* `dup-rev`</br>* `dup-reverse`</br>* `owo` | 1 | 🎵 | ⛓️‍💥 | **Bifurcate** (`|a|n|y|`): Duplicate #1 and reverse the duplicate |
| `␣` | * `space` | 0 | 🎵 | ⛓️‍💥 | **Space**: Push a space to the stack |
| `¶` | * `newline` | 0 | 🎵 | ⛓️‍💥 | **Newline**: Push a newline to the stack |
| `★` | * `asterisk` | 0 | 🎵 | ⛓️‍💥 | **Asterisk**: Push an asterisk to the stack |
| `ᑂ` | * `headless-top` | 1 | 🎵 | ⛓️‍💥 | **Head on Top, Rest on Bottom** (`|a|n|y|`): Push #1[1:] and #1[0] |
| `∻` | * `integer-divide`</br>* `int-div`</br>* `//` | 2 | 🎶 | ⛓️‍💥 | **Integer Divide** (`|n|u|m|,|n|u|m|`): #1 // #2 |
| `√` | * `square-root`</br>* `sqrt` | 1 | 🎶 | ⛓️‍💥 | **Square Root** (`|n|u|m|`): Square root of #1 |
| `¿` | * `truthy?` | 1 | 🎶 | ⛓️‍💥 | **Truthy?** (`|s|c|l|`): Is #1 truthy? (Not 0, empty, or false) |
| `◌` | * `round` | 1 | 🎶 | ⛓️‍💥 | **Round** (`|n|u|m|`): Round #1 to the nearest integer, half-up |
| `δ` | * `deltas`</br>* `differences` | 1 | 🎵 | ⛓️‍💥 | **Deltas** (`|l|s|t|`): Deltas/forward differences of #1 - [a - b, b - c, c - d, ...] |
| `☷` | * `partition-after-truthy` | 1 | 🎵 | ⛓️‍💥 | **Partition After Truthy** (`|l|s|t|,|l|s|t|`):  Partition #1 after truthy indices of #2. |
| `✇` | * `edges`</br>* `ends`</br>* `real-imaginary` | 1 | 🎵 | ⛓️‍💥 | **Edges** (`|i|t|r|`): First and last element of #1</br>**Real and Imaginary** (`|n|u|m|`): Real and imaginary parts of #1 |
| `⎃` | * `flatten-and-join-on-nothing` | 1 | 🎵 | ⛓️‍💥 | **Flatten and Join on Nothing** (`|l|s|t|`): Flatten #1 and join on nothing |
| `⎶` | * `trim` | 2 | 🎵 | ⛓️‍💥 | **Trim** (`|a|n|y|,|a|n|y|`): Trim #1 of leading and trailing #2 |
| `⊆` | * `subset?` | 2 | 🎵 | ⛓️‍💥 | **Subset?** (`|l|s|t|,|l|s|t|`): Is the shallower list a subset of the deeper list? Checks windows corresponding to the length of the shallower list |