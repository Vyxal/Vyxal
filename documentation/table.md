Element, Modifier, and Syntax Reference

## Elements

- `nsl` = Number/String/List
- `any` = Any type
- `num` = Number
- `str` = String
- `lst` = List
- `fun` = Function
- `obj` = User-defined object

| Symbol | Keywords | Arity | Vectorises | Overloads |
|--------|--|------|-----------|-----------|
| `÷` | `divide`</br>`string-pieces`</br>`regex-split`</br>`/`</br>`div` | 2 | vec  | **Division** (`num,num`): #1 / #2</br>**String into N Pieces** (`str,num`): Split string #1 into #2 pieces</br>**String into N Pieces** (`num,str`): Split string #2 into #1 pieces</br>**Regex Split** (`str,str`): Split #1 by regex #2 |
| `Ṫ` | `untruth` | 1 |   | **Untruth** (`lst`): Create a list of 1s at indices in #1, 0s elsewhere |
| `›` | `increment`</br>`++`</br>`space-to-0`</br>`replace-spaces-with-0s`</br>`inc` | 1 | vec  | **Increment** (`num`): #1 + 1</br>**Spaces to 0s** (`str`): Replace spaces in #1 with '0's |
| `∧` | `and`</br>`&&`</br>`logical-and` | 2 |   | **Logical And** (`nsl,nsl`): Python-style and - if #2 is false, return #2, else return #1</br>**Short Circuit And** (`fun,fun`): Short circuit and - if #2() is false, return #2(), else return #1() |
| `γ` | `wrap-len-2` | 1 |   | **Wrap to Length 2** (`any`): Wrap #1 into chunks of length 2 |
| `‹` | `decrement`</br>`--`</br>`pad-to-8`</br>`dec`</br>`pad-8`</br>`pad-to-byte` | 1 | vec  | **Decrement** (`num`): #1 - 1</br>**Pad to 8** (`str`): Pad #1 to a length that is a multiple of 8 with '0's |
| `⊞` | `counts`</br>`counts-of` | 1 |   | **Counts of Items** (`lst`): [#1.count(x) for x in set(#1)] |
| `¬` | `not`</br>`~`</br>`logical-not` | 1 |   | **Not** (`any`): if #1 is truthy, return False, else return True |
| `∨` | `or`</br>`!!`</br>`logical-or` | 2 |   | **Loigcal Or** (`any,any`): Python style or - if #2 is true, return #2, else return #1</br>**Short Circuit Or** (`fun,fun`): Short circuit or - if #2() is true, return #2(), else return #1() |
| `∑` | `sum`</br>`sum-of`</br>`+/`</br>`/+`</br>`sigma`</br>`sigma-in-ohio` | 1 |   | **Sum** (`lst`): Sum of #1</br>**Join and Evaluate** (`lst[at least 1 str]`): Join #1 and evaluate the result |
| `×` | `multiply`</br>`string-repeat`</br>`ring-translate`</br>`*`</br>`times` | 2 | vec  | **Multiplication** (`num,num`): #1 * #2 (#1 times #2)</br>**String Repeat** (`str,num`): Repeat string #1 #2 times</br>**String Repeat** (`num,str`): Repeat string #2 #1 times</br>**Ring Translate** (`str,str`): Ring translate #1 according to #2.  |
| `!` | `factorial`</br>`!`</br>`titlecase`</br>`fact`</br>`title`</br>`fac` | 1 | vec  | **Factorial** (`num`): Factorial of #1</br>**Titlecase** (`str`): Titlecase #1 |
| `$` | `swap` | 2 |   | **Swap** (`any,any`): Swap #1 and #2 on the stack: #1 #2 -> #2 #1 |
| `%` | `mod`</br>`modulo`</br>`%`</br>`remainder` | 2 | vec  | **Modulo** (`num,num`): #1 % #2 (remainder of #1 divided by #2)</br>**String Format** (`str,any`): Format #1 with #2</br>**String Format** (`any,str`): Format #2 with #1 |
| `&` | `append` | 2 |   | **Append** (`any,any`): Append #2 to #1 |
| `*` | `exponentiate`</br>`pow`</br>`**`</br>`power` | 2 | vec  | **Exponentiation** (`num,num`): #1 ** #2 |
| `+` | `add`</br>`+`</br>`plus`</br>`addition` | 2 | vec  | **Addition** (`num,num`): #1 + #2</br>**String and Number Concatenation** (`str,num`): #1 + str(#2)</br>**String and Number Concatenation** (`num,str`): str(#1) + #2</br>**String Concatenation** (`str,str`): #1 + #2 |
| `,` | `println`</br>`stdout`</br>`output`</br>`out` | 1 |   | **Print** (`any`): Print #1 to stdout, followed by a newline |
| `-` | `subtract`</br>`-`</br>`minus`</br>`subtraction`</br>`regex-remove` | 2 | vec  | **Subtraction** (`num,num`): #1 - #2</br>**Prepend/Append Hyphens** (`str,num`): #1 + #2 * '-'</br>**Prepend/Append Hyphens** (`num,str`): '-' * #1 + #2</br>**Regex Remove** (`str,str`): Remove matches of #2 from #1 |
| `:` | `dup`</br>`duplicate` | 1 |   | **Duplicate** (`any`): Push #1 twice to the stack: #1 -> #1 #1 |
| `;` | `pair`</br>`cons` | 2 |   | **Pair** (`any,any`): Push a list [#1, #2] to the stack: #1 #2 -> [#1, #2] |
| `\<` | `less-than`</br>`<`</br>`lt` | 2 | vec  | **Less Than** (`scl,scl`): #1 < #2 |
| `=` | `equals`</br>`==`</br>`eq` | 2 | vec  | **Equals** (`scl,scl`): #1 == #2 |
| `\>` | `greater-than`</br>`>`</br>`gt` | 2 | vec  | **Greater Than** (`scl,scl`): #1 > #2 |
| `?` | `stdin`</br>`input`</br>`in` | 0 |   | **Input**: Get the next input item, evaluated. |
| `#?` | `inputs`</br>`all-inputs`</br>`all-stdin` | 0 |   | **Inputs**: Get all the global inputs as a list |
| `@` | `absolute-difference`</br>`abs-diff`</br>`levenstein`</br>`to-overpairs` | 2 | vec  | **Absolute Difference** (`num,num`): Absolute difference between #1 and #2</br>**Levenstein Distance** (`str,str`): Levenstein distance between #1 and #2</br>**Reduce Overlapping Pairs** (`lst,fun`): Reduce overlapping pairs in {#1|#2} by function {#2|#1} |
| `A` | `all`</br>`all?`</br>`vowel?`</br>`is-vowel`</br>`is-vowel?` | 1 |   | **All** (`any`): Are all elements of #1 are truthy |
| `B` | `to-binary` | 1 | vec  | **To Binary** (`num`): Convert #1 to binary</br>**String to Binary** (`str`): Convert each character in #1 to a binary representation of its unicode value |
| `C` | `count` | 2 |   | **Count** (`lst,scl`): Count occurrences of #2 in #1</br>**Count** (`scl,lst`): Count occurrences of #1 in #2</br>**Count** (`lst,lst`): Count occurrences of the list with shallower depth in the list with deeper depth |
| `#C` | `compress` | 1 | vec  | **Compress** (`str`): Compress #1 using the Vyxal compression algorithm |
| `D` | `triplicate` | 1 |   | **Triplicate** (`any`): Push #1 thrice to the stack: #1 -> #1 #1 #1 |
| `E` | `2**n`</br>`2pow`</br>`eval`</br>`2**` | 1 | vec  | **2 to the Power of N** (`num`): 2 ** #1</br>**Eval** (`str`): Evaluate #1 |
| `F` | `filter`</br>`find`</br>`index-of` | 2 |   | **Filter** (`fun,any`): Filter #1 by function #2</br>**Filter** (`any,fun`): Filter #2 by function #1</br>**Find** (`nls,nls`): Find the index of #1 in #2. Switches #1 and #2 so that the haystack is the deeper list |
| `G` | `max-of`</br>`maximum-of` | 1 |   | **Monadic Maximum** (`lst`): Maximum of #1 |
| `H` | `to-hex`</br>`from-hex` | 1 | vec  | **To Hex** (`num`): Convert #1 to hexadecimal</br>**From Hex** (`str`): Convert #1 from hexadecimal to a number. Inverse of 'to-hex' |
| `I` | `interleave`</br>`reject` | 2 |   | **Interleave** (`any,any`): Interleave #1 and #2</br>**Reject** (`any,fun`): Remove elements of #1 that satisfy function #2</br>**Reject** (`fun,any`): Remove elements of #2 that satisfy function #1 |
| `J` | `join`</br>`concat` | 2 |   | **Join** (`lst,scl`): Add #2 to the end of #1</br>**Join** (`scl,lst`): Prepend #1 to #2</br>**Join / Merge** (`lst,lst`): Add all elements of #2 to #1</br>**Number Pair** (`num,num`): Create a list of #1 and #2</br>**String Concatenation** (`str\|num,str\|num`): string(#1) + string(#2) (if either #1 or #2 is a string) |
| `K` | `factors`</br>`is-numeric?`</br>`is-numeric` | 1 | vec  | **Factors** (`num`): Get the factors of #1</br>**Is Numeric** (`str`): Check if #1 is numeric |
| `L` | `length`</br>`len` | 1 |   | **Length** (`any`): Length of #1 |
| `M` | `map`</br>`mold`</br>`multiplicity`</br>`regex-match` | 2 | vec  | **Map** (`fun,any`): Map function #1 over #2</br>**Map** (`any,fun`): Map function #2 over #1</br>**Mold** (`lst,lst`): Reshape #1 to the shape of #2</br>**Multiplicity** (`num,num`): How many times #1 divides #2</br>**Regex Match** (`str,str`): Return the first match of #2 in #1 |
| `N` | `negate`</br>`swapcase`</br>`first>-1` | 1 | vec  | **Negate** (`num`): -#1</br>**Negate** (`str`): Swap the case of each letter #1</br>**First Non-Negative Integer Where Predicate is True** (`fun`): First non-negative integer where #1 is true |
| `O` | `ord`</br>`chr` | 1 | vec  | **Character to Unicode** (`str`): Unicode value of each letter in #1</br>**Unicode to Character** (`num`): Character of each unicode value in #1 |
| `P` | `prefixes` | 1 |   | **Prefixes** (`lst`): Get all prefixes of #1. Treats numbers as a list of digits |
| `Q` | `remove-at`</br>`regex-groups` | 2 |   | **Remove At** (`nsl,num`): Remove the element at index #2 from #1</br>**Regex Groups** (`str,str`): Return the groups of the first match of #2 in #1 |
| `R` | `range`</br>`reduce`</br>`regex-match?` | 2 |   | **Range** (`num,num`): Range from #1 to #2, exclusive</br>**Reduce** (`lst,fun`): Reduce #1 by function #2</br>**Regex Match?** (`str,str`): Check if #2 matches #1 |
| `S` | `sort` | 1 |   | **Sort** (`itr`): Sort #1 |
| `T` | `transpose`</br>`triple`</br>`alpha-only?` | 1 |   | **Transpose** (`lst`): Transpose #1</br>**Triple** (`num`): #1 * 3</br>**Does String Contain Only Alphabetic Characters** (`str`): Check if #1 contains only alphabetic characters |
| `U` | `uninterleave` | 1 |   | **Uninterleave** (`lst`): Uninterleave #1 |
| `V` | `vectorse-reverse`</br>`1-x` | 1 |   | **Vectorise Reverse** (`lst`): Reverse each item in #1</br>**1 - X** (`num`): 1 - #1 |
| `W` | `wrap` | STACK |   | **Wrap**: Wrap the entire stack into a list |
| `X` | `cartesian-product` | 2 |   | **Cartesian Product** (`lst,lst`): Cartesian product of #1 and #2 |
| `Y` | `list-repeat` | 2 |   | **List Repeat** (`num,num`): A list of #1 repeated #2 times. E.g. 3 4 -> [3, 3, 3, 3]</br>**List Repeat** (`itr,num`): A list of #2 instances of string #1</br>**List Repeat** (`num,itr`): A list of #1 instances of string #2</br>**Vectorised Repeat** (`itr,lst[nsl]`): Repeat each element of #2 (#1|#1.length) times |
| `Z` | `zip` | 2 |   | **Zip** (`lst,lst`): Zip #1 and #2 |
| `^` | `reverse-stack` | STACK |   | **Reverse Stack**: Reverse the stack |
| `_` | `pop`</br>`discard` | 0 |   | **Pop**: Pop the top of the stack |
| `\`` | `len-stack` | 0 |   | **Length of Stack**: Push the length of the stack to the stack |
| `a` | `any`</br>`any?`</br>`uppercase?` | 1 |   | **Any** (`num`): Are any digits of #1 truthy</br>**Is Uppercase** (`str`): Check if #1 is uppercase. With string.len > 1, vectorises over each character</br>**Any** (`lst`): Are any elements of #1 truthy |
| `b` | `from-binary` | 1 |   | **Binary Digits** (`num`): Convert #1's list of digits from binary to base 10</br>**From Binary** (`str`): Convert #1 from binary to a number</br>**From Binary** (`lst`): Convert #1 from binary to a number |
| `c` | `contains`</br>`contains?`</br>`is-in` | 2 |   | **Contains** (`scl,scl`): Is #2 in #1</br>**Contains** (`lst,scl`): Is #2 in #1</br>**Contains** (`scl,lst`): Is #1 in #2</br>**Contains** (`lst,lst`): Is the list with shallower depth in the list with deeper depth |
| `d` | `double` | 1 |   | **Double** (`num`): #1 * 2</br>**Double** (`str`): Append a copy of #1 to itself |
| `e` | `even?`</br>`is-even`</br>`split-newlines`</br>`/newline` | 1 | vec  | **Is Even** (`num`): Is #1 even</br>**Split Newlines** (`str`): Split #1 by newlines |
| `f` | `flatten` | 1 |   | **List of Digits** (`num`): Push a list of the digits of #1 to the stack</br>**List of Characters** (`str`): Push a list of the characters of #1 to the stack</br>**Flatten** (`lst`): Flatten #1 |
| `g` | `min-of`</br>`minimum-of` | 1 |   | **Monadic Minimum** (`lst`): Minimum of #1 |
| `h` | `head`</br>`first` | 1 |   | **Head** (`any`): First element of #1 |
| `i` | `index`</br>`at`</br>`item-at`</br>`nth-item`</br>`collect-unique`</br>`enclose`</br>`<**` | 2 |   | **Nth AddElement** (`itr,num`): Get the #2th element of #1</br>**Nth AddElement** (`num,itr`): Get the #1th element of #2</br>**Vectorised Index** (`itr,lst[num]`): [#1[_] for _ in #2]</br>**String Enclose** (`str,str`): enclose #2 in #1 (#1[0:len(#1)//2] + #2 + #1[len(#1)//2:])</br>**Object Member Retrieval** (`obj,str`): #1.#2</br>**Object Member Retrieval** (`str,obj`): #2.#1</br>**Collect Unique Values (+ Initial Value)** (`any,fun`): Apply #2 on #1 and collect unique values. Does include the initial value. |
| `j` | `join-on` | 2 |   | **Join On** (`lst,scl`): Join #1 on #2</br>**Join On** (`scl,lst`): Join #2 on #1</br>**Intersperse** (`lst,lst`): Intersperse elements of #2 within #1 (e.g. [1, [2,3], 4] [5, 6] -> [1, 5, 6, [2, 3], 5, 6, 4]) |
| `l` | `log`</br>`logarithm`</br>`scan-fixpoint`</br>`scan-fix`</br>`same-length?`</br>`same-length`</br>`length-equals?`</br>`length-equals`</br>`len-eq?` | 2 | vec  | **Logarithm** (`num,num`): Log base #2 of #1</br>**Scan Fixpoint** (`fun,any`): Repeatedly apply #1 to #2 until it doesn't change</br>**Scan Fixpoint** (`any,fun`): Repeatedly apply #1 to #2 until it doesn't change</br>**Same Length** (`str,str`): Are #1 and #2 the same length</br>**String Length Equals** (`str,num`): Is the length of #1 equal to #2</br>**String Length Equals** (`num,str`): Is the length of #2 equal to #1 |
| `m` | `ctx-secondary`</br>`ctx2`</br>`ctx-m`</br>`context-m`</br>`context-secondary` | 0 |   | **Context Secondary**: Push the secondary context variable to the stack |
| `n` | `ctx-primary`</br>`ctx`</br>`ctx-n`</br>`context-n`</br>`context-primary` | 0 |   | **Context Primary**: Push the primary context variable to the stack |
| `o` | `overlapping-sliding-window`</br>`windows`</br>`reduce-overlaps-by` | 2 |   | **Windows** (`lst,lst[num]`): Get overlapping windows of #1 with a window of size #2</br>**Overlapping Slices** (`any,num`): Get overlapping pairs of iterable(#1) with a window of size #2</br>**Overlapping Slices** (`num,any`): Get overlapping pairs of iterable(#2) with a window of size #1</br>**Reduce Overlapping Slices** (`lst,fun`): Reduce overlapping slices of length #2.arity in #1 by function #2</br>**Reduce Overlapping Slices** (`fun,lst`): Reduce overlapping slices of length #2.arity in #1 by function #2</br>**Reduce Set-Sized Overlapping Slices** (`lst,num,fun`): Reduce overlapping slices of length #2 in #1 by function #3</br>**Reduce Set-Sized Overlapping Slices** (`lst,fun,num`): Reduce overlapping slices of length #3 in #1 by function #2 |
| `p` | `prepend` | 2 |   | **Prepend** (`any,any`): Prepend #2 to #1 |
| `q` | `quotify` | 1 |   | **Quotify** (`any`): Cast #1 to a string and wrap in quotes |
| `r` | `replace` | 3 |   | **Replace** (`nsl,nsl,nsl`): Replace all occurrences of #2 in #1 with #3</br>**Zip-With** (`lst,lst,fun`): Zip #1 and #2 and apply #3 to each pair</br>**Zip-With** (`lst,fun,lst`): Zip #1 and #2 and apply #3 to each pair</br>**Zip-With** (`fun,lst,lst`): Zip #1 and #2 and apply #3 to each pair |
| `s` | `split` | 2 |   | **Split** (`any,any`): Split #1 by #2 |
| `t` | `tail`</br>`last` | 1 |   | **Tail** (`any`): Last element of #1 |
| `u` | `unique` | 1 |   | **Unique** (`lst`): Unique elements of #1</br>**Unique By Function** (`lst,fun`): Unique elements of #1 by applying #2 |
| `v` | `overlapping-pairs`</br>`reduce-pairs-by` | 1 |   | **Overlapping Pairs** (`lst`): Get overlapping pairs of #1</br>**Reduce Overlapping Pairs** (`lst,fun`): Reduce overlapping pairs in #1 by function #2 |
| `w` | `wrap-in-list` | 1 |   | **Wrap in List** (`any`): Wrap #1 in a list |
| `x` | `recurse` | STACK |   | **Recurse**: Recursively call the current function (or the top-level program if not in a function) |
| `y` | `transliterate`</br>`call-while` | 3 |   | **Transliterate** (`nsl,nsl,nsl`): Replace all occurrences of #2 in #1 with #3</br>**Call While** (`fun,fun,any`): While #1(#3) is true, #3 = #2(#3). Return the result. Type switchable. |
| `z` | `zip-with-filler` | 2 |   | **Zip With Filler** (`lst,any`): Transpose #1, filling empty spaces with #2 |
| `⨥` | `+2`</br>`add-2`</br>`++++`</br>`inc-inc`</br>`strlen==1` | 1 | vec  | **Add 2** (`num`): #1 + 2</br>**String Length Equals 1** (`str`): Is the length of #1 equal to 1 |
| `⨪` | `-2`</br>`subtract-2`</br>`----`</br>`dec-dec`</br>`flip-bracket-palindrome` | 1 | vec  | **Subtract 2** (`num`): #1 - 2</br>**Flip Bracket Palindrome** (`str`): Palindromise #1 by appending the reverse with brackets and slashes flipped |
| `Π` | `product`</br>`product-of`</br>`*/` | 1 |   | **Product** (`lst`): Product of #1</br>**Number to Binary as String** (`num`): Convert #1 to binary as a string |
| `σ` | `cumulative-sums`</br>`cumsums`</br>`cumsum`</br>`cum-sum`</br>`-_-` | 1 |   | **Cumulative Sums** (`lst`): Cumulative sums of #1 |
| `⇧` | `grade-up` | 1 |   | **Grade Up** (`lst`): Indices that would sort #1 |
| `⇩` | `grade-down` | 1 |   | **Grade Down** (`lst`): Indices that would sort #1 in reverse |
| `∪` | `union`</br>`set-union` | 2 |   | **Union** (`lst,lst`): Union of #1 and #2 |
| `∩` | `intersection`</br>`set-intersection` | 2 |   | **Intersection** (`lst,lst`): Intersection of #1 and #2 |
| `⊍` | `set-xor` | 2 |   | **Set XOR** (`lst,lst`): Set XOR of #1 and #2 |
| `⦰` | `set-difference`</br>`set-diff` | 2 |   | **Set Difference** (`lst,lst`): Set difference of #1 and #2 |
| `«` | `left-shift`</br>`<<` | 2 | vec  | **Left Shift** (`num,num`): #1 << #2</br>**Prepend Spaces to Given Length** (`str,num`): Prepend spaces to string #1 until it is #2 characters long</br>**Prepend Spaces to Given Length** (`num,str`): Prepend spaces to string #2 until it is #1 characters long</br>**Prepend Spaces to Length of Second String** (`str,str`): Prepend spaces to string #1 until it is the length of #2 |
| `»` | `right-shift`</br>`>>` | 2 | vec  | **Right Shift** (`num,num`): #1 >> #2</br>**Append Spaces to Given Length** (`str,num`): Append spaces to string #1 until it is #2 characters long</br>**Append Spaces to Given Length** (`num,str`): Append spaces to string #2 until it is #1 characters long</br>**Append Spaces to Length of Second String** (`str,str`): Append spaces to string #1 until it is the length of #2 |
| `Ɠ` | `max-peek` | 1* |   | **Max Peek** (`lst`): Maximum of #1 without popping |
| `ɠ` | `min-peek` | 1* |   | **Min Peek** (`lst`): Minimum of #1 without popping |
| `Ġ` | `zip-max`</br>`max-dyad`</br>`max-ab`</br>`gen` | 2 | vec  | **Zipped Maximum** (`lst,lst`): Maximum of corresponding elements of #1 and #2</br>**Vectorised Maximum** (`lst,scl`): Maximum of #2 and #1</br>**Vectorised Maximum** (`scl,lst`): Maximum of #1 and #2</br>**Dyadic Maximum** (`scl,scl`): Maximum of #1 and #2</br>**Generate Sequence** (`nls,fun`): Call #2 on previous results of #2, starting with #1.</br>**Generate Sequence** (`fun,nls`): Call #1 on previous results of #1, starting with #2. |
| `ġ` | `zip-min`</br>`min-dyad`</br>`min-ab`</br>`2gen` | 2 | vec  | **Zipped Minimum** (`lst,lst`): Minimum of corresponding elements of #1 and #2</br>**Vectorised Minimum** (`lst,scl`): Minimum of #2 and #1</br>**Vectorised Minimum** (`scl,lst`): Minimum of #1 and #2</br>**Dyadic Minimum** (`scl,scl`): Minimum of #1 and #2</br>**Generate Sequence** (`nls,fun`): Call #2 as a dyad infinitely with items of #1 as starting values |
| `◲` | `sublists` | 1 |   | **Sublists** (`any`): All sublists of #1 |
| `⌈` | `ceil`</br>`ceiling`</br>`split-on-spaces` | 1 | vec  | **Ceiling** (`num`): Ceiling of #1</br>**Split on Spaces** (`str`): Split #1 by spaces |
| `⌊` | `floor`</br>`str-to-num` | 1 | vec  | **Floor** (`num`): Floor of #1</br>**String to Number** (`str`): Convert #1 to a number, ignoring non-digit characters. Returns 0 if no digits are found |
| `⊖` | `0-slice`</br>`take`</br>`0-take` | 2 |   | **0 Slice** (`itr,num`): First #2 elements of #1</br>**0 Slice** (`num,itr`): First #1 elements of #2</br>**APL Style Take** (`lst,lst[num]`): APL style take |
| `⌽` | `1-slice`</br>`tail-take`</br>`1-take` | 2 |   | **1 Slice** (`itr,num`): First #2 elements of #1[1:]</br>**1 Slice** (`num,itr`): First #1 elements of #2[1:] |
| `£` | `set-register` | 1 |   | **Set Register** (`any`): Set the register to #1 |
| `¥` | `get-register` | 0 |   | **Get Register**: Push the register to the stack |
| `↜` | `rotate-stack-left` | STACK |   | **Rotate Stack Left**: Rotate the stack left |
| `↝` | `rotate-stack-right` | STACK |   | **Rotate Stack Right**: Rotate the stack right |
| `↺` | `rot-left` | 1 |   | **Rotate Left** (`lst\|str`): Rotate #1 left</br>**Rotate Left** (`lst\|str,num`): Rotate #1 left #2 times. Right if #2 is negative |
| `↻` | `rot-right` | 1 |   | **Rotate Right** (`lst\|str`): Rotate #1 right</br>**Rotate Right** (`lst\|str,num`): Rotate #1 right #2 times. Left if #2 is negative |
| `≜` | `assign`</br>`**>` | 3 |   | **List Assign** (`any,num,nsl`): #1[#2] = #3</br>**Augmented List Assignment** (`any,num,fun`): #1[#2] = #3(#1[#2])</br>**Vectorised Augmented List Assignment** (`lst,lst[num],fun`): #1[_] = #3(#1[_]) for _ in #2</br>**Zipped Assignment** (`lst,lst,lst`): #1[ind] = val for ind, val in zip(#2, #3)</br>**Regex String Replacement** (`str,str,str`): Replace all occurrences of #2 in #1 with #3</br>**Regex Substitution** (`str,str,fun`): Replace all occurrences of #2 in #1 with the result of #3</br>**Object Member Assignment** (`obj,str,any`): #1.#2 = #3 |
| `⎀` | `insert` | 3 |   | **Insert** (`any,num,any`): Insert #3 into #1 at index #2</br>**Insert** (`any,lst[num],scl`): Insert #3 into #1 at indices #2</br>**Insert** (`any,lst[num],lst`): Insert items of #3 into #1 at indices #2 |
| `⊢` | `10-to-base`</br>`all-regex-matches` | 2 |   | **10 to Base** (`num,num`): Convert #1 to base #2</br>**10 to Base** (`num,str\|lst`): Convert #1 to base len(#2) using the items of #2</br>**10 to Base** (`lst,num`): Convert each item in #1 to base #2</br>**10 to Base** (`lst,lst`): Convert each item in #1 to the base of the corresponding item in #2</br>**All Regex Matches** (`str,str`): All matches of #2 in #1 |
| `⊣` | `base-to-10` | 2 |   | **Base to 10** (`scl,num`): Convert #1 from base #2 to base 10, assuming a base that is a prefix of [0-9A-Z] for strings</br>**Base to 10** (`lst[num\|str],num`): Convert #1 from base #2 to base 10, using the items of #1 as digits</br>**Base to 10** (`lst,num`): Convert each item in #1 from base #2 to base 10, assuming a base that is a prefix of [0-9A-Z] for strings |
| `ɦ` | `head-peek` | 1* |   | **Head Peek** (`lst`): First element of #1 without popping |
| `ʈ` | `tail-peek` | 1* |   | **Tail Peek** (`lst`): Last element of #1 without popping |
| `ᐐ` | `init` | 1 |   | **Init** (`any`): All but the last element of #1 |
| `ᐵ` | `drop` | 2 |   | **Drop** (`any,num`): All but the first #2 elements of #1</br>**Drop** (`num,any`): All but the first #1 elements of #2</br>**APL Style Drop** (`lst,lst[num]`): APL style drop |
| `ᐕ` | `behead` | 1 |   | **Behead** (`any`): All but the first element of #1 |
| `½` | `half`</br>`halve` | 1 | vec  | **Halve** (`num`): #1 / 2</br>**Two String Halves** (`str`): Split #1 in half |
| `ƶ` | `range-to-length` | 1 |   | **Range to Length** (`lst`): Range from 0 to len(#1) - 1 |
| `Ƶ` | `range-to-length-1` | 1 |   | **Range to Length 1** (`lst`): Range from 1 to len(#1) |
| `⁰` | `first-input`</br>`input-0` | 0 |   | **First Input**: Push the first input to the stack |
| `¹` | `second-input`</br>`input-1` | 0 |   | **Second Input**: Push the second input to the stack |
| `²` | `square`</br>`string-pairs` | 1 | vec  | **Square** (`num`): #1 ** 2</br>**String Pairs** (`str`): Split #1 into pairs of characters |
| `³` | `cube`</br>`string-triples` | 1 | vec  | **Cube** (`num`): #1 ** 3</br>**String Triples** (`str`): Split #1 into triples of characters |
| `⅟` | `reciprocal`</br>`inverse`</br>`1/`</br>`without-whitespace`</br>`no-space`</br>`spaceless` | 1 | vec  | **Reciprocal** (`num`): 1 / #1</br>**Without Whitespace** (`str`): Remove all whitespace from #1 |
| `⇄` | `reverse` | 1 |   | **Reverse** (`any`): Reverse #1 |
| `⧖` | `permutations`</br>`map-over-permutations` | 1 |   | **Permutations** (`any`): All permutations of #1</br>**Map Over Permutations** (`any,fun`): Map #2 over all permutations of #1 |
| `‰` | `divmod` | 2 | vec  | **Divmod** (`num,num`): Divmod of #1 and #2 ([#1 // #2, #1 % #2]) |
| `≛` | `divides?`</br>`append-spaces`</br>`regex-span` | 2 |   | **Divides?** (`num,num`): #2 % #1 == 0</br>**Append Spaces** (`str,num`): Append #2 spaces to #1</br>**Append Spaces** (`num,str`): Append #1 spaces to #2</br>**Regex Span** (`str,str`): Span of regex match of pattern #2 in #1 |
| `ℭ` | `combinations-with-replacement` | 2 |   | **Combinations with Replacement** (`itr,num`): All combinations of #1 of length #2 with replacement</br>**Combinations with Replacement** (`num,itr`): All combinations of #2 of length #1 with replacement</br>**Combinations of Range with Replacement** (`num,num`): All combinations of range(#1) of length #2 with replacement |
| `℈` | `combinations-without-replacement` | 2 |   | **Combinations without Replacement** (`itr,num`): All combinations of #1 of length #2 without replacement</br>**Combinations without Replacement** (`num,itr`): All combinations of #2 of length #1 without replacement</br>**Combinations of Range without Replacement** (`num,num`): All combinations of range(#1) of length #2 without replacement |
| `⦷` | `abs`</br>`absolute-value`</br>`keep-letters` | 1 | vec  | **Absolute Value** (`num`): Absolute value of #1</br>**Keep Letters** (`str`): Keep only the letters of #1 |
| `Ϣ` | `chunk-to-length`</br>`partition-to-length` | 2 |   | **Chunk to Length** (`any,num`): Chunk #1 into parts of length #2</br>**Chunk to Length** (`num,any`): Chunk #2 into parts of length #1</br>**Partition to Lengths** (`itr,lst[num]`): Partition #1 into parts of lengths #2 |
| `≤` | `less-than-or-equal`</br>`lte`</br>`<=` | 2 | vec  | **Less Than or Equal** (`scl,scl`): #1 <= #2 |
| `≥` | `greater-than-or-equal`</br>`gte`</br>`>=` | 2 | vec  | **Greater Than or Equal** (`scl,scl`): #1 >= #2 |
| `≠` | `not-equal`</br>`neq`</br>`!=`</br>`=n't`</br>`eqn't`</br>`equaln't` | 2 | vec  | **Not Equal** (`scl,scl`): str(#1) != str(#2) |
| `≡` | `exact-equals`</br>`eq+`</br>`===` | 2 |   | **Equals** (`any,any`): Does #1 exactly equal #2 |
| `•` | `dot-product`</br>`bijective-base`</br>`first-predicate-index` | 2 |   | **Dot Product** (`lst,lst`): Dot product of #1 and #2</br>**Bijective Base Conversion** (`num,num`):  Convert #1 to bijective base #2</br>**First Index Where Predicate True** (`nsl,fun`): Index of the first value in #1 where function #2 is true</br>**First Index Where Predicate True** (`fun,nsl`): Index of the first value in #2 where function #1 is true |
| `±` | `signum` | 1 | vec  | **Signum** (`num`): Sign of #1 |
| `†` | `lengths-of-consecutives` | 1 |   | **Lengths of Consecutives** (`lst`): Lengths of consecutive runs of equal elements in #1 |
| `⎙` | `peek-print` | 1* |   | **Peek Print** (`any`): Print #1 without popping |
| `✒` | `print` | 1 |   | **Print** (`any`): Print #1 without a trailing newline |
| `≓` | `mirror` | 1 |   | **Mirror** (`any`): Mirror #1 (#1 + reverse(#1)), as the original type |
| `Ͼ` | `vectorised-sums`</br>`v/+` | 1 | vec  | **Vectorised Sums** (`lst`): Sum of each item in #1. Functionally equivalent to `¨Σ` |
| `ᴥ` | `exec`</br>`10**`</br>`call`</br>`@` | 1 | vec  | **10 to the Power of** (`num`): 10 ** #1</br>**Execute** (`str`): Execute #1 as Vyxal code</br>**Call Function** (`fun`): Call function #1 |
| `ℳ` | `modular`</br>`matrix-multiply`</br>`regex-full-match?` | 2 |   | **Every Nth AddElement** (`itr,num`): Every #2th element of #1</br>**Every Nth AddElement** (`num,itr`): Every #1th element of #2</br>**Matrix Multiply** (`lst,lst`): Matrix multiply #1 and #2</br>**Regex Full Match?** (`str,str`): Does pattern #2 fully match #1 |
| `℗` | `is-prime`</br>`prime?`</br>`quine-cheese` | 1 | vec  | **Is Prime** (`num`): Is #1 a prime number?</br>**Quine Cheese** (`str`): Quotify #1 and prepend it to #1. (Useful for quines like `"⌭"⌭`) |
| `↸` | `roll` | 3 |   | **Roll** (`any,any,any`): #1 #2 #3 -> #3 #1 #2 |
| `⍢` | `parity`</br>`bit`</br>`last-half` | 1 | vec  | **Parity** (`num`): Parity of #1 (1 if odd, 0 if even) --> #1 % 2</br>**Last String Half** (`str`): Last half of #1 |
| `ℂ` | `ncr`</br>`choose`</br>`characters-same?`</br>`fixpoint-collect` | 2 | vec  | **NCR | N Choose R** (`num,num`): nCr of #1 and #2 (n choose r)</br>**Characters Same?** (`str,str`): Are all characters in #1 the same as #2?</br>**Fixpoint Collect** (`fun,any`): Repeatedly apply #1 on #2 until a fixed point is reached, collecting intermediate results</br>**Fixpoint Collect** (`any,fun`): Repeatedly apply #2 on #1 until a fixed point is reached, collecting intermediate results |
| `⌹` | `list-partitions`</br>`integer-partitions` | 1 |   | **Integers Partitions** (`num`): All possible ways to sum positive integers to #1</br>**List Partitions** (`itr`): All possible ways to partition #1 into sublists |
| `⏚` | `powerset`</br>`vectorise` | 1 |   | **Powerset** (`nsl`): Powerset of #1</br>**Vectorise** (`fun`): Apply #1 as if it were a pervasive element |
| `↯` | `inclusive-range`</br>`sort-by`</br>`regex-split-keep-delimiters` | 2 | vec  | **Inclusive Range** (`num,num`): Inclusive range from #1 to #2</br>**Sort By** (`nsl,fun`): Sort list #1 (range if num) by function #2</br>**Sort By** (`fun,nsl`): Sort list #2 (range if num) by function #1</br>**Regex Split Keep Delimiters** (`str,str`): Split #1 by regex #2, keeping the delimiters |
| `⊠` | `cartesian-power`</br>`regex-index` | 2 |   | **Cartesian Power** (`any,num`): Cartesian power of #1 to the power of #2</br>**Cartesian Power** (`num,any`): Cartesian power of #2 to the power of #1</br>**Regex Index** (`str,str`): Return first index of pattern match #2 in target string #1, -1 if not found</br>**Self-Cartesian Power** (`itr,any`): Push #1, and then push the cartesian product of #2 with itself |
| `⚅` | `random-choice`</br>`random-element`</br>`randint`</br>`random` | 1 |   | **Random Choice** (`itr`): Random element of #1</br>**Random Integer** (`num`): Random integer from 0 to #1 |
| `kæ` | `all-primes`</br>`primes` | 0 |   | **All Primes**: Push a list of every prime number to the stack |
| `æ` | `bifuricate`</br>`bifur`</br>`bif`</br>`furry`</br>`uwu`</br>`dup-rev`</br>`dup-reverse`</br>`owo`</br>`peek-function`</br>`peek-call`</br>`@@` | 1 |   | **Bifurcate** (`any`): Duplicate #1 and reverse the duplicate</br>**Call Function Without Popping** (`fun`): Call #1 without popping its arguments |
| `␣` | `space` | 0 |   | **Space**: Push a space to the stack |
| `¶` | `newline` | 0 |   | **Newline**: Push a newline to the stack |
| `★` | `asterisk` | 0 |   | **Asterisk**: Push an asterisk to the stack |
| `ᑂ` | `headless-top` | 1 |   | **Head on Top, Rest on Bottom** (`any`): Push #1[1:] and #1[0] |
| `∻` | `integer-divide`</br>`int-div`</br>`//` | 2 | vec  | **Integer Divide** (`num,num`): #1 // #2 |
| `√` | `square-root`</br>`sqrt` | 1 | vec  | **Square Root** (`num`): Square root of #1 |
| `⍰` | `truthy?` | 1 | vec  | **Truthy?** (`scl`): Is #1 truthy? (Not 0, empty, or false) |
| `◌` | `round` | 1 | vec  | **Round** (`num`): Round #1 to the nearest integer, half-up |
| `δ` | `deltas`</br>`differences` | 1 |   | **Deltas** (`lst`): Deltas/forward differences of #1 - [a - b, b - c, c - d, ...] |
| `☷` | `partition-after-truthy` | 1 |   | **Partition After Truthy** (`lst,lst`):  Partition #1 after truthy indices of #2. |
| `✇` | `edges`</br>`ends`</br>`real-imaginary` | 1 |   | **Edges** (`itr`): First and last element of #1</br>**Real and Imaginary** (`num`): Real and imaginary parts of #1 |
| `⎃` | `flatten-and-join-on-nothing` | 1 |   | **Flatten and Join on Nothing** (`lst`): Flatten #1 and join on nothing |
| `⎶` | `trim` | 2 |   | **Trim** (`any,any`): Trim #1 of leading and trailing #2 |
| `⊆` | `subset?` | 2 |   | **Subset?** (`lst,lst`): Is the shallower list a subset of the deeper list? Checks windows corresponding to the length of the shallower list |
| `⍨` | `dump` | 1 |   | **Dump** (`any`): Push all items of #1 to the stack |
| `⎘` | `flatten-by-depth`</br>`flatten-depth` | 2 |   | **Flatten by Depth** (`lst,num`): Flatten #1 by #2 levels</br>**Flatten by Depth** (`lst`): Flatten #1 by 1 level |
| `ꜝ` | `keep-truthy` | 1 |   | **Keep Truthy** (`lst`): Keep only the truthy elements of #1 |
| `≈` | `all-same` | 1 |   | **All Same** (`any`): Are all elements of #1 the same? |
| `≊` | `all-equal-item` | 2 |   | **All Equal Item** (`lst,any`): Are all elements of #1 equal to #2? |
| `κ` | `gcd` | 2 | vec  | **GCD** (`num,num`): GCD of #1 and #2</br>**GCD of List** (`lst`): GCD of all elements of #1</br>**GCD of List with Initial Value** (`lst,num`): GCD of all elements of #1.append(#2) |
| `↳` | `retrieve-from-outer` | 1 |   | **Retrieve Item at Index from Outer Stack** (`num`): Retrieve the item at index #1 from the outer stack, current stack if at top level</br>**Retrieve Item at Index from Outer Stack N-Layers Up** (`lst[num, num]`): Retrieve the item at index #2 from the stack #1 levels up |
| `ʀ` | `0->n`</br>`lowercase`</br>`range-0->n`</br>`nrange-0` | 1 | vec  | **Range 0** (`num`): Range from 0 to #1, exclusive</br>**Lowercase** (`str`): Lowercase #1 |
| `ʁ` | `0->n++`</br>`uppercase`</br>`range-0->n++`</br>`n+range-0` | 1 | vec  | **Range 0 Inclusive** (`num`): Range from 0 to #1, inclusive</br>**Uppercase** (`str`): Uppercase #1 |
| `ɾ` | `1->n++`</br>`is-alpha?` | 1 | vec  | **Range 1 Inclusive** (`num`): Range from 1 to #1, inclusive</br>**Is Character Alphabetical** (`str`): Check if #1 is alphabetical (i.e. is a letter) |
| `▲` | `mask` | 2 |   | **Mask** (`any,any`): Keep elements of #1 where the corresponding element of #2 is truthy |
| `Ṭ` | `truthy-indexes` | 1 |   | **Truthy Indexes** (`lst`): Indexes of truthy elements in #1 |
| `⤻` | `over` | STACK |   | **Over**: Duplicate the item below the top of the stack -> #2 #1 #2 |
| `⤺` | `around` | STACK |   | **Around**: Duplicate the top of the stack around the item below the top of the stack -> #1 #2 #1 |
| `Ŀ` | `vlen`</br>`lengths` | 1 |   | **Vectorised Lengths** (`lst`): Length of each element in #1 |
| `¤` | `stringify`</br>`to-str`</br>`str` | 1 |   | **Stringify** (`any`): Stringify #1 |
| `①` | `10` | 0 |   | **10**: Push 10 to the stack |
| `②` | `16` | 0 |   | **16**: Push 16 to the stack |
| `③` | `32` | 0 |   | **32**: Push 32 to the stack |
| `④` | `64` | 0 |   | **64**: Push 64 to the stack |
| `⑤` | `100` | 0 |   | **100**: Push 100 to the stack |
| `⑥` | `128` | 0 |   | **128**: Push 128 to the stack |
| `⑦` | `256` | 0 |   | **256**: Push 256 to the stack |
| `⑧` | `-1` | 0 |   | **-1**: Push -1 to the stack |
| `⑨` | `empty-string` | 0 |   | **Empty string**: Push "" to the stack |
| `„` | `join-on-spaces`</br>`*space`</br>`<0`</br>`is-negative?` | 1 |   | **Join on Spaces** (`lst`): Join #1 on spaces</br>**Is negative?** (`num`): Push 1 if #1 < 0, 0 otherwise |
| `”` | `join-on-newlines`</br>`*newline`</br>`one?->n` | 1 |   | **Join on Newlines** (`lst`): Join #1 on newlines</br>**Push Context Variable N if 1** (`num`): Push the context variable N if #1 is 1 |
| `“` | `join-on-empty-string`</br>`*empty`</br>`is-alphanumeric?`</br>`insignificant?`</br>`first-positive-integer`</br>`first-n>0` | 1 |   | **Join on Empty String** (`lst`): Join #1 on the empty string</br>**Is alphanumeric?** (`str`): Push 1 if #1 is alphanumeric, 0 otherwise</br>**First Positive Integer Where Function is Truthy** (`fun`): Push the first positive integer where #1 is truthy</br>**Is Insignificant?** (`num`): abs(#1) <= 1 |

## Modifiers

| Symbol | Keywords | Number of Elements | Overloads |
|--------|--|------------------|-----------|
| `~` | `filter:`</br>`without-popping:`</br>`peek:` | 1 | <table><tr><td>**Filter**</td><td>`mon`</td><td>Filter the top of the stack with #1</td><td>`#[1\|2\|3\|4\|5#] ~2≛ -> [2, 4]`</td></tr></br><tr><td>**Peek**</td><td>`dyd+`</td><td>Apply #1 without popping</td><td>`3 4 5 ~+ -> 3 4 9`</td></tr></table> |
| `⩔` | `at-simple-levels:`</br>`@simple:` | 1 | <table><tr><td>**At Simple Levels**</td><td>`mon`</td><td>Apply #1 at the simple levels of the top of the stack</td><td>`#[#[#[1\|2\|3#]\|#[#[4\|5\|#[6\|7\|8#]#]#]#]#] ⩔L -> [[3, [[1, 1, 3]]]]`</td></tr></table> |
| `Ẅ` | `zip-with:` | 1 | <table><tr><td>**Zip With**</td><td>`dyd`</td><td>Pop two lists and zip them, reducing each pair with #1</td><td>`#[1\|2\|3#] #[4\|5\|6#] ¨; -> [[1, 4], [2, 5], [3, 6]]`</td></tr></table> |
| `⎇` | `dip:` | 1 | <table><tr><td>**Dip**</td><td>`mon`</td><td>Save the top stack item, apply #1, then push the saved item</td><td>`3 4 5 2 ⎇+ -> 3 9 2`</td></tr></table> |
| `ᖶ` | `if-else:` | 2 | <table><tr><td>**If Else**</td><td>`any,any`</td><td>If the top of the stack is truthy, apply #1, else apply #2</td><td>`3 1 ᖶd½ -> 6`</td></tr></table> |
| `¿` | `if:` | 1 | <table><tr><td>**If**</td><td>`any`</td><td>If the top of the stack is truthy, apply #1</td><td>`3 1 ¿d -> 6`</td></tr></table> |
| `∥` | `parallel-apply:`</br>`para:` | 2 | <table><tr><td>**Parallel Apply**</td><td>`mon,mon`</td><td>Apply #1 and #2 on separate stacks and push both results</td><td>`3 4 ∥d½ -> 8 2`</td></tr></table> |
| `∦` | `parallel-apply-wrap:`</br>`paraw:` | 2 | <table><tr><td>**Parallel Apply Wrap**</td><td>`mon,mon`</td><td>Apply #1 and #2 on separate stacks and push both results wrapped in a list. Equivalent to `∥#1#2;`</td><td>`3 4 ∦d½ -> [8, 2]`</td></tr></table> |
| `∺` | `correspond:` | 2 | <table><tr><td>**Correspond**</td><td>`mon,mon`</td><td>Apply #1 to <under> and #2 to <top></td><td>`3 4 ∺d½ -> 6 2`</td></tr></br><tr><td>**Dyadic Correspond**</td><td>`dyd+,dyd+`</td><td>Apply #2 to #2.arity top items, and #1 to #1.arity items under that</td><td>`3 4 5 6 ∺+- -> 7 1_`</td></tr></table> |
| `⁜` | `group-by:`</br>`window-reduce:` | 1 | <table><tr><td>**Group By**</td><td>`mon`</td><td>Group items of the top of the stack by results of #1</td><td>`#[1\|3\|4\|5\|2\|4#] ⁜e -> [[1,3],[4],[5],[2,4]]`</td></tr></br><tr><td>**Window Reduce**</td><td>`dyd+`</td><td>Reduce each overlapping window of size #1.arity with #1</td><td>`#[1\|2\|3\|4\|5\|6#] ⁜λ3\|+} -> [6, 9, 12, 15]`</td></tr></table> |
| `⑴` | `*:` | 1 | <table><tr><td>**Next AddElement as Lambda**</td><td>`any`</td><td>Wrap #1 in a lambda and push it</td><td>`⑴+ = λ+}`</td></tr></table> |
| `⑵` | `**:` | 2 | <table><tr><td>**Next Two Elements as Lambda**</td><td>`any,any`</td><td>Wrap #1 and #2 in a lambda and push it</td><td>`⑵+* = λ+*}`</td></tr></table> |
| `⑶` | `***:` | 3 | <table><tr><td>**Next Three Elements as Lambda**</td><td>`any,any,any`</td><td>Wrap #1, #2, and #3 in a lambda and push it</td><td>`⑶+*~ = λ+*~}`</td></tr></table> |
| `⑷` | `****:` | 4 | <table><tr><td>**Next Four Elements as Lambda**</td><td>`any,any,any,any`</td><td>Wrap #1, #2, #3, and #4 in a lambda and push it</td><td>`⑷+*~d = λ+*~d}`</td></tr></table> |
| `⎂` | `both:` | 1 | <table><tr><td>**Both**</td><td>`any`</td><td>Apply #1 to both the top of stack (or however many arguments), and under stack (or however many arguments under the arity). Effectively ... #1(top - arity, top - arity * 2) #1(top -> top - arity)</td><td>`3 4 ⎂d -> 6 8 \|\| 1 2 3 4 ⎂+ -> 3 7`</td></tr></table> |
| `⟒` | `left-fork:` | 2 | <table><tr><td>**Left Fork**</td><td>`dyd+,dyd+`</td><td>Apply #1 but keep the under stack, and then apply #2. Effectively #2(#1(top, under), under)</td><td>`3 4 ⟒+× -> 28`</td></tr></table> |
| `ᛞ` | `inner-product:` | 2 | <table><tr><td>**Inner Product**</td><td>`dyd,dyd`</td><td>Inner product of #1 and #2</td><td>`#[1\|2\|3#] #[4\|5\|6#] ᛞ×+ -> 32`</td></tr></table> |
| `▦` | `outer-product:` | 1 | <table><tr><td>**Outer Product**</td><td>`dyd`</td><td>Outer product of #1 and #2</td><td>`#[1\|2\|3#] #[4\|5\|6#] ▦; -> [[[1,4],[1,5],[1,6]],[[2,4],[2,5],[2,6],[3,4],[3,5],[3,6]]]`</td></tr></table> |
| `¨` | `each:` | 1 | <table><tr><td>**Each**</td><td>`any`</td><td>Map #1 over the top of the stack</td><td>`#[#[1\|2\|3#]\|#[4\|2\|3#]\|#[1\|5\|3#]#] ¨G -> [3, 4, 5]`</td></tr></table> |

## Syntax
        
| Symbol | Name | Keywords | Description | Usage |
|--------|----|--|-----------|------|
| `#:~` | Retrieve Original Element | `$.` | Call the original, vyxal defined, meaning of an element. Useful for when you want to define a new element with the same name as a built-in one | <code>#:~<name></code> |
| `#:@` | Defined Element Call | `$@` | Call a defined element | <code>#:@<name></code> |
| `#>` | Augmented Assignment | `:>` | Apply a function to a variable value and store the result in the same variable. | <code><function> #> <variable></code> |
| `Ω` | Open Filter Lambda | `filter-lam`</br>`filter<`</br>`filter-lambda` | Open a lambda that automatically filters the top of the stack by its function | <code>Ω<code>}</code> |
| `₳` | Open Reduce/Accumulate Lambda | `reduce-lam`</br>`reduce<`</br>`reduce-lambda`</br>`fold<`</br>`fold-lam`</br>`fold-lambda` | Open a lambda that automatically reduces/accumulates the top of the stack by its function | <code>₳<code>}</code> |
| `#]` | Close List | `]` | Close a list. Pushes the list to the stack when closed. | <code>#[item|item|item#]</code> |
| `#:\`` | Defined Modifier Call | `$:` | Call a defined modifier | <code>#:`<name></code> |
| `#¤` | Context Paramter Index | ``n`` | Index into the list of context parameters. | <code>¤<number></code> |
| `#=` | Assign Variable | `:=` | Assign a variable to a value. | <code>#=<variable></code> |
| `#::R` | Record Definition | `record` | Define a record with members | <code>#:R<name>|#$restricted #=private #!public}</code> |
| `#::+` | Extension Method | `extension` | Define an overload on a custom element based on types. Requires at least one type to be specified. | <code>#::+<name>|<arg1>|<type1>|<arg2>|<type2>...|<impl>}</code> |
| `#[` | Open List | `[` | Open a list. Pushes the list to the stack when closed. | <code>#[item|item|item#]</code> |
| `#::` | Element/Modifier Definition | `define` | Define a custom element/modifier that can be used in programs | <code>#::<mode><name>|<arg>|<arg>...|<code>}</code> |
| `#{` | If/Elif/Else Statement | `if` | Open an if statement. Allows for if/elif/else statements | <code>#{<if condition>|<code>|<else if condition>|<code>|<else code>}</code> |
| `#:[` | Variable Unpacking | `:=[` | Unpack the top of the stack into a list of variables. | <code>#:[<var>|<var>|<var>]</code> |
| `##` | Comment |  | Comment out the rest of the line | <code>##<comment></code> |
| `λ` | Open Lambda | `lam`</br>`lambda`</br>`{` | Open a lambda. | <code>λ<parameters>|<code>}</code> |
| `ƛ` | Open Map Lambda | `map-lam`</br>`map<`</br>`map-lambda` | Open a lambda that automatically maps its function to the top of the stack | <code>ƛ<code>}</code> |
| `µ` | Open Sort Lambda | `sort-lam`</br>`sort<`</br>`sort-lambda` | Open a lambda that automatically sorts the top of the stack by its function | <code>µ<code>}</code> |
| `⎋` | Close a Structure and Get the First Item | `end-and-head`</br>`end-head` | Match and close the nearest open structure, then push the first item of the result to the stack | <code><structure open> <code> ⎋ <code not in structure></code> |
| `⍟` | Close a Structure and Flatten | `end-and-flatten`</br>`end-flatten` | Match and close the nearest open structure, then flatten the result | <code><structure open> <code> ⍟ <code not in structure></code> |
| `⎊` | Open Map Over Permutations Lambda | `map-permutations`</br>`map-perms`</br>`map-permutations<`</br>`permutations<` | Open a lambda that automatically maps over the permutations of the top of the stack | <code>⎊<code>}</code> |
| `⎄` | Generator Structure | `relation<`</br>`generate<`</br>`generate-from<` | Open a generator structure. Allows for generator expressions | <code>⎄<code>|<initial vector>}</code> |
| `"` | Open/Close String |  | Open/close a string. If the string is closed, push it to the stack. Closes all string types | <code>"string contents"</code> |
| `#$` | Retrieve Variable | `$` | Push the value of a variable. | <code>#$<variable></code> |
| `#` | Miscellaneous Digraphs |  | Used for miscellaneous digraphs | <code>#<character></code> |
| `'` | One Character String |  | Push the next character as a string | <code>'<character></code> |
| `(` | For Loop | `for`</br>`for<`</br>`do-to-each`</br>`each-as` | Open a for loop. For each item in the top of the stack, execute code, storing loop variable. | <code><iterable> (<variable>|<code>}</code> |
| `)` | Close Two Structures | `end-end` | Match and close two open structures. | <code><structure open><structure open> <code> ) <code not in structure></code> |
| `.` | Decimal Separator |  | Used to separate the integer and fractional parts of a number | <code><integer>.<fractional></code> |
| `0` | Numeric Literal |  | The number 0 | <code>0</code> |
| `1` | Numeric Literal |  | The number 1 | <code>1</code> |
| `2` | Numeric Literal |  | The number 2 | <code>2</code> |
| `3` | Numeric Literal |  | The number 3 | <code>3</code> |
| `4` | Numeric Literal |  | The number 4 | <code>4</code> |
| `5` | Numeric Literal |  | The number 5 | <code>5</code> |
| `6` | Numeric Literal |  | The number 6 | <code>6</code> |
| `7` | Numeric Literal |  | The number 7 | <code>7</code> |
| `8` | Numeric Literal |  | The number 8 | <code>8</code> |
| `9` | Numeric Literal |  | The number 9 | <code>9</code> |
| `[` | Ternary Statement | `?`</br>`?->` | Open a ternary statement. Pop condition, if truthy, run <ontrue>, else run <onfalse> | <code><condition> [<ontrue>|<onfalse>}</code> |
| `]` | Close All Structures | `close-all`</br>`end-all` | Match and close all open structures. | <code><structure openers>] <code not in structure></code> |
| `k` | Constant Digraphs |  | Used for constant-related digraphs | <code>k<character></code> |
| `{` | While Loop | `while`</br>`while<` | Open a while loop. While the top of the stack is truthy, execute code. | <code>{<condition>|<code>}</code> |
| `\|` | Structure Branch | `:`</br>`->`</br>`else:`</br>`else`</br>`elif`</br>`else-if`</br>`body`</br>`do`</br>`branch`</br>`then`</br>`in`</br>`using`</br>`no?`</br>`=>`</br>`from` | Delimit the next section in a structure. | <code><structure open> <code> | <code> ...</code> |
| `}` | Close A Structure | `end`</br>`endfor`</br>`end-for`</br>`endwhile`</br>`end-while`</br>`endlambda`</br>`end-lambda`</br>`end` | Match and close the nearest open structure. | <code><structure open> <code> } <code not in structure></code> |
| `Ꮬ` | Two Character String |  | Push the next two characters as a string | <code>Ꮬ<character><character></code> |
| `Ꮠ` | Two Byte Number |  | Push the next two bytes as a number, converted from bijective base 255 using the codepage | <code>Ꮠ<character><character></code> |
| `Þ` | List Digraphs |  | Used for list-related digraphs | <code>Þ<character></code> |
| `∆` | Mathematical Digraphs |  | Used for math-related digraphs | <code>∆<character></code> |
| `ø` | String Digraphs |  | Used for string-related digraphs | <code>ø<character></code> |
| `„` | Base-252 Compressed String |  | Decompress and push a string, converted from a bijective base 252 number using the codepage | <code>"<compressed string>„</code> |
| `”` | Dictionary Compressed String |  | Decompress and push a string using SSS compression, shamelessly stolen from Jelly | <code>"<compressed string>”</code> |
| `“` | Base-252 Compressed Number |  | Decompress and push a number, converted from a bijective base 252 number using the codepage | <code>"<compressed number>“</code> |
