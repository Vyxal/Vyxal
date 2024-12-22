| Symbol | Arity | Vectorises | Peeks | Overloads |
|--------|-------|------------|-------|-----------|
| `⊞` | 1 | false | false | **Counts of Items** (`lst`): [#1.count(x) for x in set(#1)] |
| `÷` | 2 | true | false | **Division** (`num,num`): #1 / #2</br>**String into N Pieces** (`str,num`): Split string #1 into #2 pieces</br>**String into N Pieces** (`num,str`): Split string #2 into #1 pieces</br>**Regex Split** (`str,str`): Split #1 by regex #2 |
| `×` | 2 | true | false | **Multiplication** (`num,num`): #1 * #2 (#1 times #2)</br>**String Repeat** (`str,num`): Repeat string #1 #2 times</br>**String Repeat** (`num,str`): Repeat string #2 #1 times</br>**Ring Translate** (`str,str`): Ring translate #1 according to #2.  |
| `∧` | 2 | false | false | **Short Circuit And** (`any,any`): Short circuit and - if #2 is false, return #2, else return #1 |
| `∨` | 2 | false | false | **Short Circuit Or** (`any,any`): Short circuit or - if #2 is true, return #2, else return #1 |
| `¬` | 1 | false | false | **Not** (`any`): if #1 is truthy, return False, else return True |
| `ʀ` | 1 | true | false | **Range 0** (`num`): Range from 0 to #1, exclusive</br>**Lowercase** (`str`): Lowercase #1 |
| `ʁ` | 1 | true | false | **Range 0 Inclusive** (`num`): Range from 0 to #1, inclusive</br>**Uppercase** (`str`): Uppercase #1 |
| `ɾ` | 1 | true | false | **Range 1 Inclusive** (`num`): Range from 1 to #1, inclusive</br>**Is Character Alphabetical** (`str`): Check if #1 is alphabetical (i.e. is a letter) |
| `‹` | 1 | true | false | **Decrement** (`num`): #1 - 1</br>**Pad to 8** (`str`): Pad #1 to a length that is a multiple of 8 with '0's |
| `›` | 1 | true | false | **Increment** (`num`): #1 + 1</br>**Spaces to 0s** (`str`): Replace spaces in #1 with '0's |
| `!` | 1 | true | false | **Factorial** (`num`): Factorial of #1</br>**Titlecase** (`str`): Titlecase #1 |
| `$` | 2 | false | false | **Swap** (`any,any`): Swap #1 and #2 on the stack: #1 #2 -> #2 #1 |
| `%` | 2 | true | false | **Modulo** (`num,num`): #1 % #2 (remainder of #1 divided by #2)</br>**String Format** (`str,any`): Format #1 with #2</br>**String Format** (`any,str`): Format #2 with #1 |
| `&` | 2 | false | false | **Append** (`any,any`): Append #2 to #1 |
| `*` | 2 | true | false | **Exponentiation** (`num,num`): #1 ** #2 |
| `+` | 2 | true | false | **Addition** (`num,num`): #1 + #2</br>**String and Number Concatenation** (`str,num`): #1 + str(#2)</br>**String and Number Concatenation** (`num,str`): str(#1) + #2</br>**String Concatenation** (`str,str`): #1 + #2 |
| `,` | 1 | false | false | **Print** (`any`): Print #1 to stdout, followed by a newline |
| `-` | 2 | true | false | **Subtraction** (`num,num`): #1 - #2</br>**Prepend/Append Hyphens** (`str,num`): #1 + #2 * '-'</br>**Prepend/Append Hyphens** (`num,str`): '-' * #1 + #2</br>**Regex Remove** (`str,str`): Remove matches of #2 from #1 |
| `:` | 1 | false | false | **Duplicate** (`any`): Push #1 twice to the stack: #1 -> #1 #1 |
| `;` | 2 | false | false | **Pair** (`any,any`): Push a list [#1, #2] to the stack: #1 #2 -> [#1, #2] |
| `\<` | 2 | true | false | **Less Than** (`scl,scl`): #1 < #2 |
| `=` | 2 | true | false | **Equals** (`scl,scl`): #1 == #2 |
| `\>` | 2 | true | false | **Greater Than** (`scl,scl`): #1 > #2 |
| `?` | 0 | false | false | **Input** (``): Get the next input item, evaluated. |
| `@` | 2 | true | false | **Absolute Difference** (`num,num`): Absolute difference between #1 and #2</br>**Levenstein Distance** (`str,str`): Levenstein distance between #1 and #2</br>**Reduce Overlapping Pairs** (`lst,fun`): Reduce overlapping pairs in {#1|#2} by function {#2|#1} |
| `A` | 1 | false | false | **All** (`any`): Are all elements of #1 are truthy |
| `B` | 1 | true | false | **To Binary** (`num`): Convert #1 to binary</br>**String to Binary** (`str`): Convert each character in #1 to a binary representation of its unicode value |
| `C` | 2 | false | false | **Count** (`lst,scl`): Count occurrences of #2 in #1</br>**Count** (`scl,lst`): Count occurrences of #1 in #2</br>**Count** (`lst,lst`): Count occurrences of the list with shallower depth in the list with deeper depth |
| `D` | 1 | false | false | **Triplicate** (`any`): Push #1 thrice to the stack: #1 -> #1 #1 #1 |
| `E` | 1 | true | false | **2 to the Power of N** (`num`): 2 ** #1</br>**Eval** (`str`): Evaluate #1 |
| `F` | 2 | false | false | **Filter** (`fun,any`): Filter #1 by function #2</br>**Filter** (`any,fun`): Filter #2 by function #1</br>**Find** (`nls,nls`): Find the index of #1 in #2. Switches #1 and #2 so that the haystack is the deeper list |
| `G` | 2 | false | false | **Dyadic Maximum** (`scl,scl`): Maximum of #1 and #2</br>**Monadic Maximum** (`lst`): Maximum of #1</br>**Generate Sequence** (`nls,fun`): Call #2 on previous results of #2, starting with #1. If #1 is not a list, it is made iterable |
| `H` | 1 | true | false | **To Hex** (`num`): Convert #1 to hexadecimal</br>**From Hex** (`str`): Convert #1 from hexadecimal to a number. Inverse of 'to-hex' |
| `I` | 2 | false | false | **Interleave** (`any,any`): Interleave #1 and #2</br>**Reject** (`any,fun`): Remove elements of #1 that satisfy function #2</br>**Reject** (`fun,any`): Remove elements of #2 that satisfy function #1 |
| `J` | 2 | false | false | **Join** (`lst,scl`): Add #2 to the end of #1</br>**Join** (`scl,lst`): Prepend #1 to #2</br>**Join / Merge** (`lst,lst`): Add all elements of #2 to #1</br>**Number Pair** (`num,num`): Create a list of #1 and #2</br>**String Concatenation** (`str|num,str|num`): string(#1) + string(#2) (if either #1 or #2 is a string) |
| `K` | 1 | true | false | **Factors** (`num`): Get the factors of #1</br>**Is Numeric** (`str`): Check if #1 is numeric |
| `L` | 1 | false | false | **Length** (`any`): Length of #1 |
| `M` | 2 | true | false | **Map** (`fun,any`): Map function #1 over #2</br>**Map** (`any,fun`): Map function #2 over #1</br>**Mold** (`lst,lst`): Reshape #1 to the shape of #2</br>**Multiplicity** (`num,num`): How many times #1 divides #2</br>**Regex Match** (`str,str`): Return the first match of #2 in #1 |
| `N` | 1 | true | false | **Negate** (`num`): -#1</br>**Negate** (`str`): Swap the case of each letter #1</br>**First Non-Negative Integer Where Predicate is True** (`fun`): First non-negative integer where #1 is true |
| `O` | 1 | true | false | **Character to Unicode** (`str`): Unicode value of each letter in #1</br>**Unicode to Character** (`num`): Character of each unicode value in #1 |
| `P` | 1 | false | false | **Prefixes** (`lst`): Get all prefixes of #1. Treats numbers as a list of digits |
| `Q` | 2 | false | false | **Remove At** (`nsl,num`): Remove the element at index #2 from #1</br>**Regex Groups** (`str,str`): Return the groups of the first match of #2 in #1 |
| `R` | 2 | false | false | **Range** (`num,num`): Range from #1 to #2, exclusive</br>**Reduce** (`lst,fun`): Reduce #1 by function #2</br>**Regex Match?** (`str,str`): Check if #2 matches #1 |
| `S` | 1 | false | false | **Sort** (`itr`): Sort #1 |
| `T` | 1 | false | false | **Transpose** (`lst`): Transpose #1</br>**Triple** (`num`): #1 * 3</br>**Does String Contain Only Alphabetic Characters** (`str`): Check if #1 contains only alphabetic characters |
| `U` | 1 | false | false | **Uninterleave** (`lst`): Uninterleave #1 |
| `V` | 1 | false | false | **Vectorise Reverse** (`lst`): Reverse each item in #1</br>**1 - X** (`num`): 1 - #1 |
| `W` | -1 | false | false | **Wrap** (``): Wrap the entire stack into a list |
| `X` | 2 | false | false | **Cartesian Product** (`lst,lst`): Cartesian product of #1 and #2 |
| `Y` | 2 | false | false | **List Repeat** (`num,num`): A list of #1 repeated #2 times. E.g. 3 4 -> [3, 3, 3, 3]</br>**List Repeat** (`itr,num`): A list of #2 instances of string #1</br>**List Repeat** (`num,itr`): A list of #1 instances of string #2</br>**Vectorised Repeat** (`itr,lst[nsl]`): Repeat each element of #2 (#1|#1.length) times |
| `Z` | 2 | false | false | **Zip** (`lst,lst`): Zip #1 and #2 |
| `^` | -1 | false | false | **Reverse Stack** (``): Reverse the stack |
| `_` | 0 | false | false | **Pop** (``): Pop the top of the stack |
| `a` | 1 | false | false | **Any** (`num`): Are any digits of #1 truthy</br>**Is Uppercase** (`str`): Check if #1 is uppercase. With string.len > 1, vectorises over each character</br>**Any** (`lst`): Are any elements of #1 truthy |
| `b` | 1 | false | false | **Binary Digits** (`num`): Convert #1's list of digits from binary to base 10</br>**From Binary** (`str`): Convert #1 from binary to a number</br>**From Binary** (`lst`): Convert #1 from binary to a number |
| `c` | 2 | false | false | **Contains** (`scl,scl`): Is #2 in #1</br>**Contains** (`lst,scl`): Is #2 in #1</br>**Contains** (`scl,lst`): Is #1 in #2</br>**Contains** (`lst,lst`): Is the list with shallower depth in the list with deeper depth |
| `d` | 1 | false | false | **Double** (`num`): #1 * 2</br>**Double** (`str`): Append a copy of #1 to itself |
| `e` | 1 | true | false | **Is Even** (`num`): Is #1 even</br>**Split Newlines** (`str`): Split #1 by newlines |
| `f` | 1 | false | false | **List of Digits** (`num`): Push a list of the digits of #1 to the stack</br>**List of Characters** (`str`): Push a list of the characters of #1 to the stack</br>**Flatten** (`lst`): Flatten #1 |
| `g` | 2 | false | false | **Dyadic Minimum** (`scl,scl`): Minimum of #1 and #2</br>**Monadic Minimum** (`lst`): Minimum of #1</br>**Generate Sequence** (`nls,fun`): Call #2 as a dyad infinitely with items of #1 as starting values |
| `h` | 1 | false | false | **Head** (`any`): First element of #1 |
| `i` | 2 | false | false | **Nth Element** (`itr,num`): Get the #2th element of #1</br>**Nth Element** (`num,itr`): Get the #1th element of #2</br>**Vectorised Index** (`itr,lst[num]`): [#1[_] for _ in #2]</br>**String Enclose** (`str,str`): enclose #2 in #1 (#1[0:len(#1)//2] + #2 + #1[len(#1)//2:])</br>**Object Member Retrieval** (`obj,str`): #1.#2</br>**Object Member Retrieval** (`str,obj`): #2.#1</br>**Collect Unique Values (+ Initial Value)** (`any,fun`): Apply #2 on #1 and collect unique values. Does include the initial value. |
| `j` | 2 | false | false | **Join On** (`lst,scl`): Join #1 on #2</br>**Join On** (`scl,lst`): Join #2 on #1</br>**Intersperse** (`lst,lst`): Intersperse elements of #2 within #1 (e.g. [1, [2,3], 4] [5, 6] -> [1, 5, 6, [2, 3], 5, 6, 4]) |
| `l` | 2 | true | false | **Logarithm** (`num,num`): Log base #2 of #1</br>**Scan Fixpoint** (`fun,any`): Repeatedly apply #1 to #2 until it doesn't change</br>**Scan Fixpoint** (`any,fun`): Repeatedly apply #1 to #2 until it doesn't change</br>**Same Length** (`str,str`): Are #1 and #2 the same length</br>**String Length Equals** (`str,num`): Is the length of #1 equal to #2</br>**String Length Equals** (`num,str`): Is the length of #2 equal to #1 |
| `m` | 0 | false | false | **Context Secondary** (``): Push the secondary context variable to the stack |
| `n` | 0 | false | false | **Context Primary** (``): Push the primary context variable to the stack |
| `o` | 2 | false | false | **Windows** (`lst,lst[num]`): Get overlapping windows of #1 with a window of size #2</br>**Overlapping Slices** (`any,num`): Get overlapping pairs of iterable(#1) with a window of size #2</br>**Overlapping Slices** (`num,any`): Get overlapping pairs of iterable(#2) with a window of size #1 |
| `p` | 2 | false | false | **Prepend** (`any,any`): Prepend #2 to #1 |
| `q` | 1 | false | false | **Quotify** (`any`): Cast #1 to a string and wrap in quotes |
| `r` | 3 | false | false | **Replace** (`nsl,nsl,nsl`): Replace all occurrences of #2 in #1 with #3</br>**Zip-With** (`lst,lst,fun`): Zip #1 and #2 and apply #3 to each pair</br>**Zip-With** (`lst,fun,lst`): Zip #1 and #2 and apply #3 to each pair</br>**Zip-With** (`fun,lst,lst`): Zip #1 and #2 and apply #3 to each pair |
| `s` | 2 | false | false | **Split** (`any,any`): Split #1 by #2 |
| `t` | 1 | false | false | **Tail** (`any`): Last element of #1 |
| `u` | 1 | false | false | **Unique** (`lst`): Unique elements of #1</br>**Unique By Function** (`lst,fun`): Unique elements of #1 by applying #2 |
| `w` | 1 | false | false | **Wrap in List** (`any`): Wrap #1 in a list |
| `x` | -1 | false | false | **Recurse** (``): Recursively call the current function (or the top-level program if not in a function) |
| `y` | 3 | false | false | **Transliterate** (`nsl,nsl,nsl`): Replace all occurrences of #2 in #1 with #3</br>**Call While** (`fun,fun,any`): While #1(#3) is true, #3 = #2(#3). Return the result. Type switchable. |
| `z` | 2 | false | false | **Zip With Filler** (`lst,any`): Transpose #1, filling empty spaces with #2 |
| `⨥` | 1 | true | false | **Add 2** (`num`): #1 + 2</br>**String Length Equals 1** (`str`): Is the length of #1 equal to 1 |
| `⨪` | 1 | true | false | **Subtract 2** (`num`): #1 - 2 |
| `∑` | 1 | false | false | **Sum** (`lst`): Sum of #1</br>**Join and Evaluate** (`lst[at least 1 str]`): Join #1 and evaluate the result |
| `∏` | 1 | false | false | **Product** (`lst`): Product of #1 |
| `σ` | 1 | false | false | **Cumulative Sums** (`lst`): Cumulative sums of #1 |
| `⇧` | 1 | false | false | **Grade Up** (`lst`): Indices that would sort #1 |
| `⇩` | 1 | false | false | **Grade Down** (`lst`): Indices that would sort #1 in reverse |
| `∪` | 2 | false | false | **Union** (`lst,lst`): Union of #1 and #2 |
| `∩` | 2 | false | false | **Intersection** (`lst,lst`): Intersection of #1 and #2 |
| `⊍` | 2 | false | false | **Set XOR** (`lst,lst`): Set XOR of #1 and #2 |
| `⦰` | 2 | false | false | **Set Difference** (`lst,lst`): Set difference of #1 and #2 |
| `«` | 2 | true | false | **Left Shift** (`num,num`): #1 << #2</br>**Prepend Spaces to Given Length** (`str,num`): Prepend spaces to string #1 until it is #2 characters long</br>**Prepend Spaces to Given Length** (`num,str`): Prepend spaces to string #2 until it is #1 characters long</br>**Prepend Spaces to Length of Second String** (`str,str`): Prepend spaces to string #1 until it is the length of #2 |
| `»` | 2 | true | false | **Right Shift** (`num,num`): #1 >> #2</br>**Append Spaces to Given Length** (`str,num`): Append spaces to string #1 until it is #2 characters long</br>**Append Spaces to Given Length** (`num,str`): Append spaces to string #2 until it is #1 characters long</br>**Append Spaces to Length of Second String** (`str,str`): Append spaces to string #1 until it is the length of #2 |
| `Ɠ` | 1 | false | true | **Max Peek** (`lst`): Maximum of #1 without popping |
| `ɠ` | 1 | false | true | **Min Peek** (`lst`): Minimum of #1 without popping |
| `Ġ` | 2 | true | false | **Zipped Maximum** (`lst,lst`): Maximum of corresponding elements of #1 and #2</br>**Vectorised Maximum** (`lst,scl`): Maximum of #2 and #1</br>**Vectorised Maximum** (`scl,lst`): Maximum of #1 and #2 |
| `⌈` | 1 | true | false | **Ceiling** (`num`): Ceiling of #1</br>**Split on Spaces** (`str`): Split #1 by spaces |
| `⌊` | 1 | true | false | **Floor** (`num`): Floor of #1</br>**String to Number** (`str`): Convert #1 to a number, ignoring non-digit characters. Returns 0 if no digits are found |
| `⊖` | 2 | false | false | **0 Slice** (`itr,num`): First #2 elements of #1</br>**0 Slice** (`num,itr`): First #1 elements of #2</br>**APL Style Take** (`lst,lst[num]`): APL style take |
| `⌽` | 2 | false | false | **1 Slice** (`itr,num`): First #2 elements of #1[1:]</br>**1 Slice** (`num,itr`): First #1 elements of #2[1:] |
| `£` | 1 | false | false | **Set Register** (`any`): Set the register to #1 |
| `¥` | 0 | false | false | **Get Register** (``): Push the register to the stack |
| `↜` | -1 | false | false | **Rotate Stack Left** (``): Rotate the stack left |
| `↝` | -1 | false | false | **Rotate Stack Right** (``): Rotate the stack right |
| `⬳` | 1 | false | false | **Rotate Left** (`lst|str`): Rotate #1 left</br>**Rotate Left** (`lst|str,num`): Rotate #1 left #2 times. Right if #2 is negative |
| `⟿` | 1 | false | false | **Rotate Right** (`lst|str`): Rotate #1 right</br>**Rotate Right** (`lst|str,num`): Rotate #1 right #2 times. Left if #2 is negative |
| `≜` | 3 | false | false | **List Assign** (`any,num,nsl`): #1[#2] = #3</br>**Augmented List Assignment** (`any,num,fun`): #1[#2] = #3(#1[#2])</br>**Vectorised Augmented List Assignment** (`lst,lst[num],fun`): #1[_] = #3(#1[_]) for _ in #2</br>**Zipped Assignment** (`lst,lst,lst`): #1[ind] = val for ind, val in zip(#2, #3)</br>**Regex String Replacement** (`str,str,str`): Replace all occurrences of #2 in #1 with #3</br>**Regex Substitution** (`str,str,fun`): Replace all occurrences of #2 in #1 with the result of #3</br>**Object Member Assignment** (`obj,str,any`): #1.#2 = #3 |
| `⎀` | 3 | false | false | **Insert** (`any,num,any`): Insert #3 into #1 at index #2</br>**Insert** (`any,lst[num],scl`): Insert #3 into #1 at indices #2</br>**Insert** (`any,lst[num],lst`): Insert items of #3 into #1 at indices #2 |
| `◲` | 1 | false | false | **Sublists** (`any`): All sublists of #1 |
| `⊢` | 2 | false | false | **10 to Base** (`num,num`): Convert #1 to base #2</br>**10 to Base** (`num,str|lst`): Convert #1 to base len(#2) using the items of #2</br>**10 to Base** (`lst,num`): Convert each item in #1 to base #2</br>**10 to Base** (`lst,lst`): Convert each item in #1 to the base of the corresponding item in #2</br>**All Regex Matches** (`str,str`): All matches of #2 in #1 |
| `⊣` | 2 | false | false | **Base to 10** (`scl,num`): Convert #1 from base #2 to base 10, assuming a base that is a prefix of [0-9A-Z] for strings</br>**Base to 10** (`lst[num|str],num`): Convert #1 from base #2 to base 10, using the items of #1 as digits</br>**Base to 10** (`lst,num`): Convert each item in #1 from base #2 to base 10, assuming a base that is a prefix of [0-9A-Z] for strings |
| `ɦ` | 1 | false | true | **Head Peek** (`lst`): First element of #1 without popping |
| `ʈ` | 1 | false | true | **Tail Peek** (`lst`): Last element of #1 without popping |
| `ᐐ` | 1 | false | false | **Init** (`any`): All but the last element of #1 |
| `ᐵ` | 2 | false | false | **Drop** (`any,num`): All but the first #2 elements of #1</br>**Drop** (`num,any`): All but the first #1 elements of #2</br>**APL Style Drop** (`lst,lst[num]`): APL style drop |
| `ᐕ` | 1 | false | false | **Behead** (`any`): All but the first element of #1 |
| `½` | 1 | true | false | **Halve** (`num`): #1 / 2</br>**Two String Halves** (`str`): Split #1 in half |
| `ƶ` | 1 | false | false | **Range to Length** (`lst`): Range from 0 to len(#1) - 1 |
| `Ƶ` | 1 | false | false | **Range to Length 1** (`lst`): Range from 1 to len(#1) |
| `⁰` | 0 | false | false | **First Input** (``): Push the first input to the stack |
| `¹` | 0 | false | false | **Second Input** (``): Push the second input to the stack |
| `²` | 1 | true | false | **Square** (`num`): #1 ** 2</br>**String Pairs** (`str`): Split #1 into pairs of characters |
| `³` | 1 | true | false | **Cube** (`num`): #1 ** 3</br>**String Triples** (`str`): Split #1 into triples of characters |
| `⅟` | 1 | true | false | **Reciprocal** (`num`): 1 / #1</br>**Without Whitespace** (`str`): Remove all whitespace from #1 |
| `⇄` | 1 | false | false | **Reverse** (`any`): Reverse #1 |
| `⧖` | 1 | false | false | **Permutations** (`any`): All permutations of #1 |
| `‰` | 2 | true | false | **Divmod** (`num,num`): Divmod of #1 and #2 ([#1 // #2, #1 % #2]) |
| `≛` | 2 | false | false | **Divides?** (`num,num`): #2 % #1 == 0</br>**Append Spaces** (`str,num`): Append #2 spaces to #1</br>**Append Spaces** (`num,str`): Append #1 spaces to #2</br>**Regex Span** (`str,str`): Span of regex match of pattern #2 in #1 |
| `ℭ` | 2 | false | false | **Combinations with Replacement** (`itr,num`): All combinations of #1 of length #2 with replacement</br>**Combinations with Replacement** (`num,itr`): All combinations of #2 of length #1 with replacement</br>**Combinations of Range with Replacement** (`num,num`): All combinations of range(#1) of length #2 with replacement |
| `℈` | 2 | false | false | **Combinations without Replacement** (`itr,num`): All combinations of #1 of length #2 without replacement</br>**Combinations without Replacement** (`num,itr`): All combinations of #2 of length #1 without replacement</br>**Combinations of Range without Replacement** (`num,num`): All combinations of range(#1) of length #2 without replacement |
| `⦷` | 1 | true | false | **Absolute Value** (`num`): Absolute value of #1</br>**Keep Letters** (`str`): Keep only the letters of #1 |
| `Ϣ` | 2 | false | false | **Chunk to Length** (`any,num`): Chunk #1 into parts of length #2</br>**Chunk to Length** (`num,any`): Chunk #2 into parts of length #1</br>**Partition to Lengths** (`itr,lst[num]`): Partition #1 into parts of lengths #2 |
| `≤` | 2 | true | false | **Less Than or Equal** (`scl,scl`): #1 <= #2 |
| `≥` | 2 | true | false | **Greater Than or Equal** (`scl,scl`): #1 >= #2 |
| `≠` | 2 | true | false | **Not Equal** (`scl,scl`): str(#1) != str(#2) |
| `≡` | 2 | false | false | **Equals** (`any,any`): Does #1 exactly equal #2 |
| `•` | 2 | false | false | **Dot Product** (`lst,lst`): Dot product of #1 and #2</br>**Bijective Base Conversion** (`num,num`):  Convert #1 to bijective base #2</br>**First Index Where Predicate True** (`nsl,fun`): Index of the first value in #1 where function #2 is true</br>**First Index Where Predicate True** (`fun,nsl`): Index of the first value in #2 where function #1 is true |
| `±` | 1 | true | false | **Signum** (`num`): Sign of #1 |
| `†` | 1 | false | false | **Lengths of Consecutives** (`lst`): Lengths of consecutive runs of equal elements in #1 |
| `⎙` | 1 | false | true | **Peek Print** (`any`): Print #1 without popping |
| `✒` | 1 | false | false | **Print** (`any`): Print #1 without a trailing newline |
| `≓` | 1 | false | false | **Mirror** (`any`): Mirror #1 (#1 + reverse(#1)), as the original type |
| `Ͼ` | 1 | true | false | **Vectorised Sums** (`lst`): Sum of each item in #1. Functionally equivalent to `¨Σ` |
| `⛭` | 1 | true | false | **10 to the Power of** (`num`): 10 ** #1</br>**Execute** (`str`): Execute #1 as Vyxal code</br>**Call Function** (`fun`): Call function #1 |
| `⏟` | 2 | false | false | **Every Nth Element** (`itr,num`): Every #2th element of #1</br>**Every Nth Element** (`num,itr`): Every #1th element of #2</br>**Matrix Multiply** (`lst,lst`): Matrix multiply #1 and #2</br>**Regex Full Match?** (`str,str`): Does pattern #2 fully match #1 |
| `⌭` | 1 | true | false | **Is Prime** (`num`): Is #1 a prime number?</br>**Quine Cheese** (`str`): Quotify #1 and prepend it to #1. (Useful for quines like `"⌭"⌭`) |
| `⏜` | -1 | false | false | **Over** (``): Duplicate the item below the top of the stack -> #2 #1 #2 |
| `⍢` | 1 | true | false | **Parity** (`num`): Parity of #1 (1 if odd, 0 if even) --> #1 % 2</br>**Last String Half** (`str`): Last half of #1 |
| `ℂ` | 2 | true | false | **NCR | N Choose R** (`num,num`): nCr of #1 and #2 (n choose r)</br>**Characters Same?** (`str,str`): Are all characters in #1 the same as #2?</br>**Fixpoint Collect** (`fun,any`): Repeatedly apply #1 on #2 until a fixed point is reached, collecting intermediate results</br>**Fixpoint Collect** (`any,fun`): Repeatedly apply #2 on #1 until a fixed point is reached, collecting intermediate results |
| `⌹` | 1 | false | false | **Integers Partitions** (`num`): All possible ways to sum positive integers to #1</br>**List Partitions** (`itr`): All possible ways to partition #1 into sublists |
| `⏚` | 1 | false | false | **Powerset** (`any`): Powerset of #1 |
| `↯` | 2 | true | false | **Inclusive Range** (`num,num`): Inclusive range from #1 to #2</br>**Sort By** (`nsl,fun`): Sort list #1 (range if num) by function #2</br>**Sort By** (`fun,nsl`): Sort list #2 (range if num) by function #1</br>**Regex Split Keep Delimiters** (`str,str`): Split #1 by regex #2, keeping the delimiters |
| `⊠` | 2 | false | false | **Cartesian Power** (`any,num`): Cartesian power of #1 to the power of #2</br>**Cartesian Power** (`num,any`): Cartesian power of #2 to the power of #1</br>**Regex Index** (`str,str`): Return first index of pattern match #2 in target string #1, -1 if not found</br>**Self-Cartesian Power** (`itr,any`): Push #1, and then push the cartesian product of #2 with itself |
| `⚅` | 1 | false | false | **Random Choice** (`itr`): Random element of #1</br>**Random Integer** (`num`): Random integer from 0 to #1 |
| `æ` | 1 | false | false | **Bifurcate** (`any`): Duplicate #1 and reverse the duplicate |
| `␣` | 0 | false | false | **Space** (``): Push a space to the stack |
| `¶` | 0 | false | false | **Newline** (``): Push a newline to the stack |
| `★` | 0 | false | false | **Asterisk** (``): Push an asterisk to the stack |
| `ᑂ` | 1 | false | false | **Head on Top, Rest on Bottom** (`any`): Push #1[1:] and #1[0] |
| `∻` | 2 | true | false | **Integer Divide** (`num,num`): #1 // #2 |
| `√` | 1 | true | false | **Square Root** (`num`): Square root of #1 |
| `¿` | 1 | true | false | **Truthy?** (`scl`): Is #1 truthy? (Not 0, empty, or false) |
| `◌` | 1 | true | false | **Round** (`num`): Round #1 to the nearest integer, half-up |
| `δ` | 1 | false | false | **Deltas** (`lst`): Deltas/forward differences of #1 - [a - b, b - c, c - d, ...] |
| `☷` | 1 | false | false | **Partition After Truthy** (`lst,lst`):  Partition #1 after truthy indices of #2. |
| `✇` | 1 | false | false | **Edges** (`itr`): First and last element of #1</br>**Real and Imaginary** (`num`): Real and imaginary parts of #1 |
| `⎃` | 1 | false | false | **Flatten and Join on Nothing** (`lst`): Flatten #1 and join on nothing |
| `⎶` | 2 | false | false | **Trim** (`any,any`): Trim #1 of leading and trailing #2 |
| `⊆` | 2 | false | false | **Subset?** (`lst,lst`): Is the shallower list a subset of the deeper list? Checks windows corresponding to the length of the shallower list |