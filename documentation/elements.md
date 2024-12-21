| Symbol | Keywords | Arity | Vectorises | Peeks | Overloads |
|---|---|---|---|---|---|
| ⊞ | counts, counts-of | 1 | No | No | **Counts of Items**(lst): [#1.count(x) for x in set(#1)] |
| ÷ | divide, string-pieces, regex-split, /, div | 2 | Yes | No | **Division**(num, num): #1 / #2<br/>**String into N Pieces**(str, num): Split string {#1|#2} into {#2|#1} pieces (Type-Switchable)<br/>**Regex Split**(str, str): Split #1 by regex #2 |
| × | multiply, string-repeat, ring-translate, *, times | 2 | Yes | No | **Multiplication**(num, num): #1 * #2 (#1 times #2)<br/>**String Repeat**(str, num): Repeat string {#1|#2} {#2|#1} times (Type-Switchable)<br/>**Ring Translate**(str, str): Ring translate #1 according to #2.  |
| ∧ | and, &&, logical-and | 2 | No | No | **Short Circuit And**(any, any): Short circuit and - if #2 is false, return #2, else return #1 |
| ∨ | or, !!, logical-or | 2 | No | No | **Short Circuit Or**(any, any): Short circuit or - if #2 is true, return #2, else return #1 |
| ¬ | not, ~, logical-not | 1 | No | No | **Not**(any): if #1 is truthy, return False, else return True |
| ʀ | 0->n, lowercase, range-0->n, nrange-0 | 1 | Yes | No | **Range 0**(num): Range from 0 to #1, exclusive<br/>**Lowercase**(str): Lowercase #1 (Type-Switchable) |
| ʁ | 0->n++, uppercase, range-0->n++, n+range-0 | 1 | Yes | No | **Range 0 Inclusive**(num): Range from 0 to #1, inclusive<br/>**Uppercase**(str): Uppercase #1 (Type-Switchable) |
| ɾ | 1->n++, is-alpha? | 1 | Yes | No | **Range 1 Inclusive**(num): Range from 1 to #1, inclusive<br/>**Is Character Alphabetical**(str): Check if #1 is alphabetical (i.e. is a letter) (Type-Switchable) |
| ‹ | decrement, --, pad-to-8, dec, pad-8, pad-to-byte | 1 | Yes | No | **Decrement**(num): #1 - 1<br/>**Pad to 8**(str): Pad #1 to a length that is a multiple of 8 with '0's (Type-Switchable) |
| › | increment, ++, space-to-0, replace-spaces-with-0s, inc | 1 | Yes | No | **Increment**(num): #1 + 1<br/>**Spaces to 0s**(str): Replace spaces in #1 with '0's (Type-Switchable) |
| ! | factorial, !, titlecase, fact, title, fac | 1 | Yes | No | **Factorial**(num): Factorial of #1<br/>**Titlecase**(str): Titlecase #1 (Type-Switchable) |
| $ | swap | 2 | No | No | **Swap**(any, any): Swap #1 and #2 on the stack: #1 #2 -> #2 #1 |
| % | mod, modulo, %, remainder | 2 | Yes | No | **Modulo**(num, num): #1 % #2 (remainder of #1 divided by #2)<br/>**String Format**(str, any): Format {#1|#2} with {#2|#1} (Type-Switchable) |
| & | append | 2 | No | No | **Append**(any, any): Append #2 to #1 |
| * | exponentiate, pow, **, power | 2 | Yes | No | **Exponentiation**(num, num): #1 ** #2 |
| + | add, +, plus, addition | 2 | Yes | No | **Addition**(num, num): #1 + #2<br/>**String and Number Concatenation**(str, num): {#1|str(#1)} + {str(#2)|#2} (Type-Switchable)<br/>**String Concatenation**(str, str): #1 + #2 |
| , | println, stdout, output, out | 1 | No | No | **Print**(any): Print #1 to stdout, followed by a newline |
| - | subtract, -, minus, subtraction, regex-remove | 2 | Yes | No | **Subtraction**(num, num): #1 - #2<br/>**Prepend/Append Hyphens**(str, num): {#1|'-' * #1} + {#2 * '-'|#2} (Type-Switchable)<br/>**Regex Remove**(str, str): Remove matches of #2 from #1 |
| : | dup, duplicate | 1 | No | No | **Duplicate**(any): Push #1 twice to the stack: #1 -> #1 #1 |
| ; | pair, cons | 2 | No | No | **Pair**(any, any): Push a list [#1, #2] to the stack: #1 #2 -> [#1, #2] |
| < | less-than, <, lt | 2 | Yes | No | **Less Than**(scl, scl): #1 < #2 |
| = | equals, ==, eq | 2 | Yes | No | **Equals**(scl, scl): #1 == #2 |
| > | greater-than, >, gt | 2 | Yes | No | **Greater Than**(scl, scl): #1 > #2 |
| ? | stdin, input, in | 0 | No | No | **Input**(): Get the next input item, evaluated. |
| @ | absolute-difference, abs-diff, levenstein, to-overpairs | 2 | Yes | No | **Absolute Difference**(num, num): Absolute difference between #1 and #2<br/>**Levenstein Distance**(str, str): Levenstein distance between #1 and #2<br/>**Reduce Overlapping Pairs**(lst, fun): Reduce overlapping pairs in {#1|#2} by function {#2|#1} |
| A | all, all?, vowel?, is-vowel, is-vowel? | 1 | No | No | **All**(any): Are all elements of #1 are truthy |
| B | to-binary | 1 | Yes | No | **To Binary**(num): Convert #1 to binary<br/>**String to Binary**(str): Convert each character in #1 to a binary representation of its unicode value (Type-Switchable) |
| C | count | 2 | No | No | **Count**(lst, scl): Count occurrences of {#2|#1} in {#1|#2} (Type-Switchable)<br/>**Count**(lst, lst): Count occurrences of the list with shallower depth in the list with deeper depth |
| D | triplicate | 1 | No | No | **Triplicate**(any): Push #1 thrice to the stack: #1 -> #1 #1 #1 |
| E | 2**n, 2pow, eval, 2** | 1 | Yes | No | **2 to the Power of N**(num): 2 ** #1<br/>**Eval**(str): Evaluate #1 (Type-Switchable) |
| F | filter, find, index-of | 2 | No | No | **Filter**(fun, any): Filter {#1|#2} by function {#2|#1} (Type-Switchable)<br/>**Find**(nls, nls): Find the index of #1 in #2. Switches #1 and #2 so that the haystack is the deeper list |
| G | max, maximum, gen | 2 | No | No | **Dyadic Maximum**(scl, scl): Maximum of #1 and #2<br/>**Monadic Maximum**(lst): Maximum of #1<br/>**Generate Sequence**(nls, fun): Call #2 on previous results of #2, starting with #1. If #1 is not a list, it is made iterable |
| H | to-hex, from-hex | 1 | Yes | No | **To Hex**(num): Convert #1 to hexadecimal<br/>**From Hex**(str): Convert #1 from hexadecimal to a number. Inverse of 'to-hex' (Type-Switchable) |
| I | interleave, reject | 2 | No | No | **Interleave**(any, any): Interleave #1 and #2<br/>**Reject**(any, fun): Remove elements of {#1|#2} that satisfy function {#2|#1} (Type-Switchable) |
| J | join, concat | 2 | No | No | **Join**(lst, scl): {Add #2 to the end of #1|Prepend #1 to #2} (Type-Switchable)<br/>**Join / Merge**(lst, lst): Add all elements of #2 to #1<br/>**Number Pair**(num, num): Create a list of #1 and #2<br/>**String Concatenation**(str|num, str|num): string(#1) + string(#2) (if either #1 or #2 is a string) |
| K | factors, is-numeric?, is-numeric | 1 | Yes | No | **Factors**(num): Get the factors of #1<br/>**Is Numeric**(str): Check if #1 is numeric (Type-Switchable) |
| L | length, len | 1 | No | No | **Length**(any): Length of #1 |
| M | map, mold, multiplicity, regex-match | 2 | Yes | No | **Map**(fun, any): Map function {#1|#2} over {#2|#1} (Type-Switchable)<br/>**Mold**(lst, lst): Reshape #1 to the shape of #2<br/>**Multiplicity**(num, num): How many times #1 divides #2<br/>**Regex Match**(str, str): Return the first match of #2 in #1 |
| N | negate, swapcase, first>-1 | 1 | Yes | No | **Negate**(num): -#1<br/>**Negate**(str): Swap the case of each letter #1<br/>**First Non-Negative Integer Where Predicate is True**(fun): First non-negative integer where #1 is true |
| O | ord, chr | 1 | Yes | No | **Character to Unicode**(str): Unicode value of each letter in #1<br/>**Unicode to Character**(num): Character of each unicode value in #1 |
| P | prefixes | 1 | No | No | **Prefixes**(lst): Get all prefixes of #1. Treats numbers as a list of digits |
| Q | remove-at, regex-groups | 2 | No | No | **Remove At**(nsl, num): Remove the element at index #2 from #1<br/>**Regex Groups**(str, str): Return the groups of the first match of #2 in #1 |
| R | range, reduce, regex-match? | 2 | No | No | **Range**(num, num): Range from #1 to #2, exclusive<br/>**Reduce**(lst, fun): Reduce #1 by function #2<br/>**Regex Match?**(str, str): Check if #2 matches #1 |
| S | sort | 1 | No | No | **Sort**(itr): Sort #1 |
| T | transpose, triple, alpha-only? | 1 | No | No | **Transpose**(lst): Transpose #1<br/>**Triple**(num): #1 * 3<br/>**Does String Contain Only Alphabetic Characters**(str): Check if #1 contains only alphabetic characters |
| U | uninterleave | 1 | No | No | **Uninterleave**(lst): Uninterleave #1 |
| V | vectorse-reverse, 1-x | 1 | No | No | **Vectorise Reverse**(lst): Reverse each item in #1<br/>**1 - X**(num): 1 - #1 |
| W | wrap | -1 | No | No | **Wrap**(): Wrap the entire stack into a list |
| X | cartesian-product | 2 | No | No | **Cartesian Product**(lst, lst): Cartesian product of #1 and #2 |
| Y | list-repeat | 2 | No | No | **List Repeat**(num, num): A list of #1 repeated #2 times. E.g. 3 4 -> [3, 3, 3, 3]<br/>**List Repeat**(itr, num): A list of {#2|#1} instances of string {#1|#2} (Type-Switchable)<br/>**Vectorised Repeat**(itr, lst[nsl]): Repeat each element of #2 (#1|#1.length) times |
| Z | zip | 2 | No | No | **Zip**(lst, lst): Zip #1 and #2 |
| ^ | reverse-stack | -1 | No | No | **Reverse Stack**(): Reverse the stack |
| _ | pop, discard | 0 | No | No | **Pop**(): Pop the top of the stack |
| a | any, any?, uppercase? | 1 | No | No | **Any**(num): Are any digits of #1 truthy<br/>**Is Uppercase**(str): Check if #1 is uppercase. With string.len > 1, vectorises over each character<br/>**Any**(lst): Are any elements of #1 truthy |
| b | from-binary | 1 | No | No | **Binary Digits**(num): Convert #1's list of digits from binary to base 10<br/>**From Binary**(str): Convert #1 from binary to a number<br/>**From Binary**(lst): Convert #1 from binary to a number |
| c | contains, contains?, is-in | 2 | No | No | **Contains**(scl, scl): Is #2 in #1<br/>**Contains**(lst, scl): Is {#2|#1} in {#1|#2} (Type-Switchable)<br/>**Contains**(lst, lst): Is the list with shallower depth in the list with deeper depth |
| d | double | 1 | No | No | **Double**(num): #1 * 2<br/>**Double**(str): Append a copy of #1 to itself |
| e | even?, is-even, split-newlines, /newline | 1 | Yes | No | **Is Even**(num): Is #1 even<br/>**Split Newlines**(str): Split #1 by newlines |
| f | flatten | 1 | No | No | **List of Digits**(num): Push a list of the digits of #1 to the stack<br/>**List of Characters**(str): Push a list of the characters of #1 to the stack<br/>**Flatten**(lst): Flatten #1 |
| g | min, minimum, 2gen | 2 | No | No | **Dyadic Minimum**(scl, scl): Minimum of #1 and #2<br/>**Monadic Minimum**(lst): Minimum of #1<br/>**Generate Sequence**(nls, fun): Call #2 as a dyad infinitely with items of #1 as starting values |
| h | head, first | 1 | No | No | **Head**(any): First element of #1 |
| i | index, at, item-at, nth-item, collect-unique, enclose, @<= | 2 | No | No | **Nth Element**(itr, num): Get the {#2|#1}th element of {#1|#2} (Type-Switchable)<br/>**Vectorised Index**(itr, lst[num]): [#1[_] for _ in #2]<br/>**String Enclose**(str, str): enclose #2 in #1 (#1[0:len(#1)//2] + #2 + #1[len(#1)//2:])<br/>**Object Member Retrieval**(obj, str): {#1|#2}.{#2|#1} (Type-Switchable)<br/>**Collect Unique Values (+ Initial Value)**(any, fun): Apply #2 on #1 and collect unique values. Does include the initial value. |
| j | join-on | 2 | No | No | **Join On**(lst, scl): Join {#1|#2} on {#2|#1} (Type-Switchable)<br/>**Intersperse**(lst, lst): Intersperse elements of #2 within #1 (e.g. [1, [2,3], 4] [5, 6] -> [1, 5, 6, [2, 3], 5, 6, 4]) |
| l | log, logarithm, scan-fixpoint, scan-fix, same-length?, same-length, length-equals?, length-equals, len-eq? | 2 | Yes | No | **Logarithm**(num, num): Log base #2 of #1<br/>**Scan Fixpoint**(fun, any): Repeatedly apply #1 to #2 until it doesn't change (Type-Switchable)<br/>**Same Length**(str, str): Are #1 and #2 the same length<br/>**String Length Equals**(str, num): Is the length of {#1|#2} equal to {#2|#1} (Type-Switchable) |
| m | ctx-secondary, ctx2, ctx-m, context-m, context-secondary | 0 | No | No | **Context Secondary**(): Push the secondary context variable to the stack |
| n | ctx-primary, ctx, ctx-n, context-n, context-primary | 0 | No | No | **Context Primary**(): Push the primary context variable to the stack |
| o | overlapping-pairs, overlapping-sliding-window, windows | 2 | No | No | **Windows**(lst, lst[num]): Get overlapping windows of #1 with a window of size #2<br/>**Overlapping Slices**(any, num): Get overlapping pairs of iterable({#1|#2}) with a window of size {#2|#1} (Type-Switchable) |
| p | prepend | 2 | No | No | **Prepend**(any, any): Prepend #2 to #1 |
| q | quotify | 1 | No | No | **Quotify**(any): Cast #1 to a string and wrap in quotes |
| r | replace | 3 | No | No | **Replace**(nsl, nsl, nsl): Replace all occurrences of #2 in #1 with #3<br/>**Zip-With**(lst, lst, fun): Zip #1 and #2 and apply #3 to each pair (Type-Switchable) |
| s | split | 2 | No | No | **Split**(any, any): Split #1 by #2 |
| t | tail, last | 1 | No | No | **Tail**(any): Last element of #1 |
| u | unique | 1 | No | No | **Unique**(lst): Unique elements of #1<br/>**Unique By Function**(lst, fun): Unique elements of #1 by applying #2 |
| w | wrap-in-list | 1 | No | No | **Wrap in List**(any): Wrap #1 in a list |
| x | recurse | -1 | No | No | **Recurse**(): Recursively call the current function (or the top-level program if not in a function) |
| y | transliterate, call-while | 3 | No | No | **Transliterate**(nsl, nsl, nsl): Replace all occurrences of #2 in #1 with #3<br/>**Call While**(fun, fun, any): While #1(#3) is true, #3 = #2(#3). Return the result. Type switchable. |
| z | zip-with-filler | 2 | No | No | **Zip With Filler**(lst, any): Transpose #1, filling empty spaces with #2 |
| ⨥ | +2, add-2, ++++, inc-inc, strlen==1 | 1 | Yes | No | **Add 2**(num): #1 + 2<br/>**String Length Equals 1**(str): Is the length of #1 equal to 1 |
| ⨪ | -2, subtract-2, ----, dec-dec | 1 | Yes | No | **Subtract 2**(num): #1 - 2 |
| ∑ | sum, sum-of, +/, /+, sigma, sigma-in-ohio | 1 | No | No | **Sum**(lst): Sum of #1<br/>**Join and Evaluate**(lst[at least 1 str]): Join #1 and evaluate the result |
| ∏ | product, product-of, */, */ | 1 | No | No | **Product**(lst): Product of #1 |
| σ | cumulative-sums, cumsums, cumsum, cum-sum, -_- | 1 | No | No | **Cumulative Sums**(lst): Cumulative sums of #1 |
| ⇧ | grade-up | 1 | No | No | **Grade Up**(lst): Indices that would sort #1 |
| ⇩ | grade-down | 1 | No | No | **Grade Down**(lst): Indices that would sort #1 in reverse |
| ∪ | union, set-union | 2 | No | No | **Union**(lst, lst): Union of #1 and #2 |
| ∩ | intersection, set-intersection | 2 | No | No | **Intersection**(lst, lst): Intersection of #1 and #2 |
| ⊍ | set-xor | 2 | No | No | **Set XOR**(lst, lst): Set XOR of #1 and #2 |
| ⦰ | set-difference, set-diff | 2 | No | No | **Set Difference**(lst, lst): Set difference of #1 and #2 |
| « | left-shift, << | 2 | Yes | No | **Left Shift**(num, num): #1 << #2<br/>**Prepend Spaces to Given Length**(str, num): Prepend spaces to string {#1|#2} until it is {#2|#1} characters long (Type-Switchable)<br/>**Prepend Spaces to Length of Second String**(str, str): Prepend spaces to string #1 until it is the length of #2 |
| » | right-shift, >> | 2 | Yes | No | **Right Shift**(num, num): #1 >> #2<br/>**Append Spaces to Given Length**(str, num): Append spaces to string {#1|#2} until it is {#2|#1} characters long (Type-Switchable)<br/>**Append Spaces to Length of Second String**(str, str): Append spaces to string #1 until it is the length of #2 |
| Ɠ | max-peek | 1 | No | Yes | **Max Peek**(lst): Maximum of #1 without popping |
| ɠ | min-peek | 1 | No | Yes | **Min Peek**(lst): Minimum of #1 without popping |
| Ġ | zip-max | 2 | Yes | No | **Zipped Maximum**(lst, lst): Maximum of corresponding elements of #1 and #2<br/>**Vectorised Maximum**(lst, scl): Maximum of {#2|#1} and {#1|#2} (Type-Switchable) |
| ⌈ | ceil, ceiling, split-on-spaces | 1 | Yes | No | **Ceiling**(num): Ceiling of #1<br/>**Split on Spaces**(str): Split #1 by spaces |
| ⌊ | floor, str-to-num | 1 | Yes | No | **Floor**(num): Floor of #1<br/>**String to Number**(str): Convert #1 to a number, ignoring non-digit characters. Returns 0 if no digits are found |
| ⊖ | 0-slice, take, 0-take | 2 | No | No | **0 Slice**(itr, num): First {#2|#1} elements of {#1|#2} (Type-Switchable)<br/>**APL Style Take**(lst, lst[num]): APL style take |
| ⌽ | 1-slice, tail-take, 1-take | 2 | No | No | **1 Slice**(itr, num): First {#2|#1} elements of {#1|#2}[1:] (Type-Switchable) |
| £ | set-register | 1 | No | No | **Set Register**(any): Set the register to #1 |
| ¥ | get-register | 0 | No | No | **Get Register**(): Push the register to the stack |
| ↜ | rotate-stack-left | -1 | No | No | **Rotate Stack Left**(): Rotate the stack left |
| ↝ | rotate-stack-right | -1 | No | No | **Rotate Stack Right**(): Rotate the stack right |
| ⬳ | rot-left | 1 | No | No | **Rotate Left**(lst|str): Rotate #1 left<br/>**Rotate Left**(lst|str, num): Rotate #1 left #2 times. Right if #2 is negative |
| ⟿ | rot-right | 1 | No | No | **Rotate Right**(lst|str): Rotate #1 right<br/>**Rotate Right**(lst|str, num): Rotate #1 right #2 times. Left if #2 is negative |
| ≜ | assign | 3 | No | No | **List Assign**(any, num, nsl): #1[#2] = #3<br/>**Augmented List Assignment**(any, num, fun): #1[#2] = #3(#1[#2])<br/>**Vectorised Augmented List Assignment**(lst, lst[num], fun): #1[_] = #3(#1[_]) for _ in #2<br/>**Zipped Assignment**(lst, lst, lst): #1[ind] = val for ind, val in zip(#2, #3)<br/>**Regex String Replacement**(str, str, str): Replace all occurrences of #2 in #1 with #3<br/>**Regex Substitution**(str, str, fun): Replace all occurrences of #2 in #1 with the result of #3<br/>**Object Member Assignment**(obj, str, any): #1.#2 = #3 |
| ⎀ | insert | 3 | No | No | **Insert**(any, num, any): Insert #3 into #1 at index #2<br/>**Insert**(any, lst[num], scl): Insert #3 into #1 at indices #2<br/>**Insert**(any, lst[num], lst): Insert items of #3 into #1 at indices #2 |
| ◲ | sublists | 1 | No | No | **Sublists**(any): All sublists of #1 |
| ⊢ | 10-to-base, all-regex-matches | 2 | No | No | **10 to Base**(num, num): Convert #1 to base #2<br/>**10 to Base**(num, str|lst): Convert #1 to base len(#2) using the items of #2<br/>**10 to Base**(lst, num): Convert each item in #1 to base #2<br/>**10 to Base**(lst, lst): Convert each item in #1 to the base of the corresponding item in #2<br/>**All Regex Matches**(str, str): All matches of #2 in #1 |
| ⊣ | base-to-10 | 2 | No | No | **Base to 10**(scl, num): Convert #1 from base #2 to base 10, assuming a base that is a prefix of [0-9A-Z] for strings<br/>**Base to 10**(lst[num|str], num): Convert #1 from base #2 to base 10, using the items of #1 as digits<br/>**Base to 10**(lst, num): Convert each item in #1 from base #2 to base 10, assuming a base that is a prefix of [0-9A-Z] for strings |
| ɦ | head-peek | 1 | No | Yes | **Head Peek**(lst): First element of #1 without popping |
| ʈ | tail-peek | 1 | No | Yes | **Tail Peek**(lst): Last element of #1 without popping |
| ᐐ | init | 1 | No | No | **Init**(any): All but the last element of #1 |
| ᐵ | drop | 2 | No | No | **Drop**(any, num): All but the first {#2|#1} elements of {#1|#2} (Type-Switchable)<br/>**APL Style Drop**(lst, lst[num]): APL style drop |
| ᐕ | behead | 1 | No | No | **Behead**(any): All but the first element of #1 |
| ½ | half, halve | 1 | Yes | No | **Halve**(num): #1 / 2<br/>**Two String Halves**(str): Split #1 in half |
| ƶ | range-to-length | 1 | No | No | **Range to Length**(lst): Range from 0 to len(#1) - 1 |
| Ƶ | range-to-length-1 | 1 | No | No | **Range to Length 1**(lst): Range from 1 to len(#1) |
| ⁰ | first-input, input-0 | 0 | No | No | **First Input**(): Push the first input to the stack |
| ¹ | second-input, input-1 | 0 | No | No | **Second Input**(): Push the second input to the stack |
| ² | square, string-pairs | 1 | Yes | No | **Square**(num): #1 ** 2<br/>**String Pairs**(str): Split #1 into pairs of characters |
| ³ | cube, string-triples | 1 | Yes | No | **Cube**(num): #1 ** 3<br/>**String Triples**(str): Split #1 into triples of characters |
| ⅟ | reciprocal, inverse, 1/, without-whitespace, no-space, spaceless | 1 | Yes | No | **Reciprocal**(num): 1 / #1<br/>**Without Whitespace**(str): Remove all whitespace from #1 |
| ⇄ | reverse | 1 | No | No | **Reverse**(any): Reverse #1 |
| ⧖ | permutations | 1 | No | No | **Permutations**(any): All permutations of #1 |
| ‰ | divmod | 2 | Yes | No | **Divmod**(num, num): Divmod of #1 and #2 ([#1 // #2, #1 % #2]) |
| ≛ | divides?, append-spaces, regex-span | 2 | No | No | **Divides?**(num, num): #2 % #1 == 0<br/>**Append Spaces**(str, num): Append {#2|#1} spaces to {#1|#2} (Type-Switchable)<br/>**Regex Span**(str, str): Span of regex match of pattern #2 in #1 |
| ℭ | combinations-with-replacement | 2 | No | No | **Combinations with Replacement**(itr, num): All combinations of {#1|#2} of length {#2|#1} with replacement (Type-Switchable)<br/>**Combinations of Range with Replacement**(num, num): All combinations of range(#1) of length #2 with replacement |
| ℈ | combinations-without-replacement | 2 | No | No | **Combinations without Replacement**(itr, num): All combinations of {#1|#2} of length {#2|#1} without replacement (Type-Switchable)<br/>**Combinations of Range without Replacement**(num, num): All combinations of range(#1) of length #2 without replacement |
| ⦷ | abs, absolute-value, keep-letters | 1 | Yes | No | **Absolute Value**(num): Absolute value of #1<br/>**Keep Letters**(str): Keep only the letters of #1 |
| Ϣ | chunk-to-length, partition-to-length | 2 | No | No | **Chunk to Length**(any, num): Chunk {#1|#2} into parts of length {#2|#1} (Type-Switchable)<br/>**Partition to Lengths**(itr, lst[num]): Partition #1 into parts of lengths #2 |
| ≤ | less-than-or-equal, lte, <= | 2 | Yes | No | **Less Than or Equal**(scl, scl): #1 <= #2 |
| ≥ | greater-than-or-equal, gte, >= | 2 | Yes | No | **Greater Than or Equal**(scl, scl): #1 >= #2 |
| ≠ | not-equal, neq, !=, =n't, eqn't, equaln't | 2 | Yes | No | **Not Equal**(scl, scl): str(#1) != str(#2) |
| ≡ | exact-equals, eq+, === | 2 | No | No | **Equals**(any, any): Does #1 exactly equal #2 |
| • | dot-product, bijective-base, first-predicate-index | 2 | No | No | **Dot Product**(lst, lst): Dot product of #1 and #2<br/>**Bijective Base Conversion**(num, num):  Convert #1 to bijective base #2<br/>**First Index Where Predicate True**(nsl, fun): Index of the first value in {#1|#2} where function {#2|#1} is true (Type-Switchable) |
| ± | signum | 1 | Yes | No | **Signum**(num): Sign of #1 |
| † | lengths-of-consecutives | 1 | No | No | **Lengths of Consecutives**(lst): Lengths of consecutive runs of equal elements in #1 |
| ⎙ | peek-print | 1 | No | Yes | **Peek Print**(any): Print #1 without popping |
| ✒ | print | 1 | No | No | **Print**(any): Print #1 without a trailing newline |
| ≓ | mirror | 1 | No | No | **Mirror**(any): Mirror #1 (#1 + reverse(#1)), as the original type |
| Ͼ | vectorised-sums, v/+ | 1 | Yes | No | **Vectorised Sums**(lst): Sum of each item in #1. Functionally equivalent to `¨Σ` |
| ⛭ | exec, 10**, call, @ | 1 | Yes | No | **10 to the Power of**(num): 10 ** #1<br/>**Execute**(str): Execute #1 as Vyxal code<br/>**Call Function**(fun): Call function #1 |
| ⏟ | modular, matrix-multiply, regex-full-match? | 2 | No | No | **Every Nth Element**(itr, num): Every {#2|#1}th element of {#1|#2} (Type-Switchable)<br/>**Matrix Multiply**(lst, lst): Matrix multiply #1 and #2<br/>**Regex Full Match?**(str, str): Does pattern #2 fully match #1 |
| ⌭ | is-prime, prime?, quine-cheese | 1 | Yes | No | **Is Prime**(num): Is #1 a prime number?<br/>**Quine Cheese**(str): Quotify #1 and prepend it to #1. (Useful for quines like `"⌭"⌭`) |
| ⏜ | over | -1 | No | No | **Over**(): Duplicate the item below the top of the stack -> #2 #1 #2 |
| ⍢ | parity, bit, last-half | 1 | Yes | No | **Parity**(num): Parity of #1 (1 if odd, 0 if even) --> #1 % 2<br/>**Last String Half**(str): Last half of #1 |
| ℂ | ncr, choose, characters-same?, fixpoint-collect | 2 | Yes | No | **NCR | N Choose R**(num, num): nCr of #1 and #2 (n choose r)<br/>**Characters Same?**(str, str): Are all characters in #1 the same as #2?<br/>**Fixpoint Collect**(fun, any): Repeatedly apply {#1|#2} on {#2|#1} until a fixed point is reached, collecting intermediate results (Type-Switchable) |
| ⌹ | list-partitions, integer-partitions | 1 | No | No | **Integers Partitions**(num): All possible ways to sum positive integers to #1<br/>**List Partitions**(itr): All possible ways to partition #1 into sublists |
| ⏚ | powerset | 1 | No | No | **Powerset**(any): Powerset of #1 |
| ↯ | inclusive-range, sort-by, regex-split-keep-delimiters | 2 | Yes | No | **Inclusive Range**(num, num): Inclusive range from #1 to #2<br/>**Sort By**(nsl, fun): Sort list {#1|#2} (range if num) by function {#2|#1} (Type-Switchable)<br/>**Regex Split Keep Delimiters**(str, str): Split #1 by regex #2, keeping the delimiters |
| ⊠ | cartesian-power, regex-index | 2 | No | No | **Cartesian Power**(any, num): Cartesian power of {#1|#2} to the power of {#2|#1} (Type-Switchable)<br/>**Regex Index**(str, str): Return first index of pattern match #2 in target string #1, -1 if not found<br/>**Self-Cartesian Power**(itr, any): Push #1, and then push the cartesian product of #2 with itself |
| ⚅ | random-choice, random-element, randint, random | 1 | No | No | **Random Choice**(itr): Random element of #1<br/>**Random Integer**(num): Random integer from 0 to #1 |
| æ | bifuricate, bifur, bif, furry, uwu, dup-rev, dup-reverse, owo | 1 | No | No | **Bifurcate**(any): Duplicate #1 and reverse the duplicate |
| ␣ | space | 0 | No | No | **Space**(): Push a space to the stack |
| ¶ | newline | 0 | No | No | **Newline**(): Push a newline to the stack |
| ★ | asterisk | 0 | No | No | **Asterisk**(): Push an asterisk to the stack |
| ᑂ | headless-top | 1 | No | No | **Head on Top, Rest on Bottom**(any): Push #1[1:] and #1[0] |
| ∻ | integer-divide, int-div, // | 2 | Yes | No | **Integer Divide**(num, num): #1 // #2 |
| √ | square-root, sqrt | 1 | Yes | No | **Square Root**(num): Square root of #1 |
| ¿ | truthy? | 1 | Yes | No | **Truthy?**(scl): Is #1 truthy? (Not 0, empty, or false) |
| ◌ | round | 1 | Yes | No | **Round**(num): Round #1 to the nearest integer, half-up |
| δ | deltas, differences | 1 | No | No | **Deltas**(lst): Deltas/forward differences of #1 - [a - b, b - c, c - d, ...] |
| ☷ | partition-after-truthy | 1 | No | No | **Partition After Truthy**(lst, lst):  Partition #1 after truthy indices of #2. |
| ✇ | edges, ends, real-imaginary | 1 | No | No | **Edges**(itr): First and last element of #1<br/>**Real and Imaginary**(num): Real and imaginary parts of #1 |
| ⎃ | flatten-and-join-on-nothing | 1 | No | No | **Flatten and Join on Nothing**(lst): Flatten #1 and join on nothing |
| ⎶ | trim | 2 | No | No | **Trim**(any, any): Trim #1 of leading and trailing #2 |
| ⊆ | subset? | 2 | No | No | **Subset?**(lst, lst): Is the shallower list a subset of the deeper list? Checks windows corresponding to the length of the shallower list |
