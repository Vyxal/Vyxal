| Symbol | Arity | Vectorises | Peeks | Overloads |
|--------|-------|------------|-------|-----------|
| ⊞ | 1 | false | false | Counts of Items: [#1.count(x) for x in set(#1)] |
| ÷ | 2 | true | false | Division: #1 / #2</br>String into N Pieces: Split string ArraySeq(#1, #2) into ArraySeq(#2, #1)</br>String into N Pieces: Split string ArraySeq(#2, #1) into ArraySeq(#1, #2)</br>Regex Split: Split #1 by regex #2 |
| × | 2 | true | false | Multiplication: #1 * #2 (#1 times #2)</br>String Repeat: Repeat string ArraySeq(#1, #2) ArraySeq(#2, #1)</br>String Repeat: Repeat string ArraySeq(#2, #1) ArraySeq(#1, #2)</br>Ring Translate: Ring translate #1 according to #2.  |
| ∧ | 2 | false | false | Short Circuit And: Short circuit and - if #2 is false, return #2, else return #1 |
| ∨ | 2 | false | false | Short Circuit Or: Short circuit or - if #2 is true, return #2, else return #1 |
| ¬ | 1 | false | false | Not: if #1 is truthy, return False, else return True |
| ʀ | 1 | true | false | Range 0: Range from 0 to #1, exclusive |
| ʁ | 1 | true | false | Range 0 Inclusive: Range from 0 to #1, inclusive |
| ɾ | 1 | true | false | Range 1 Inclusive: Range from 1 to #1, inclusive |
| ‹ | 1 | true | false | Decrement: #1 - 1 |
| › | 1 | true | false | Increment: #1 + 1 |
| ! | 1 | true | false | Factorial: Factorial of #1 |
| $ | 2 | false | false | Swap: Swap #1 and #2 on the stack: #1 #2 -> #2 #1 |
| % | 2 | true | false | Modulo: #1 % #2 (remainder of #1 divided by #2)</br>String Format: Format ArraySeq(#1, #2) with ArraySeq(#2, #1)</br>String Format: Format ArraySeq(#2, #1) with ArraySeq(#1, #2) |
| & | 2 | false | false | Append: Append #2 to #1 |
| * | 2 | true | false | Exponentiation: #1 ** #2 |
| + | 2 | true | false | Addition: #1 + #2</br>String and Number Concatenation: ArraySeq(#1, str(#1)) + ArraySeq(str(#1), #1)</br>String and Number Concatenation: ArraySeq(str(#2), #2) + ArraySeq(#2, str(#2))</br>String Concatenation: #1 + #2 |
| , | 1 | false | false | Print: Print #1 to stdout, followed by a newline |
| - | 2 | true | false | Subtraction: #1 - #2</br>Prepend/Append Hyphens: ArraySeq(#1, '-' * #1) + ArraySeq('-' * #1, #1)</br>Prepend/Append Hyphens: ArraySeq(#2 * '-', #2) + ArraySeq(#2, #2 * '-')</br>Regex Remove: Remove matches of #2 from #1 |
| : | 1 | false | false | Duplicate: Push #1 twice to the stack: #1 -> #1 #1 |
| ; | 2 | false | false | Pair: Push a list [#1, #2] to the stack: #1 #2 -> [#1, #2] |
| < | 2 | true | false | Less Than: #1 < #2 |
| = | 2 | true | false | Equals: #1 == #2 |
| > | 2 | true | false | Greater Than: #1 > #2 |
| ? | 0 | false | false | Input: Get the next input item, evaluated. |
| @ | 2 | true | false | Absolute Difference: Absolute difference between #1 and #2</br>Levenstein Distance: Levenstein distance between #1 and #2</br>Reduce Overlapping Pairs: Reduce overlapping pairs in {#1|#2} by function {#2|#1} |
| A | 1 | false | false | All: Are all elements of #1 are truthy |
| B | 1 | true | false | To Binary: Convert #1 to binary |
| C | 2 | false | false | Count: Count occurrences of ArraySeq(#2, #1) in ArraySeq(#1, #2)</br>Count: Count occurrences of ArraySeq(#1, #2) in ArraySeq(#2, #1)</br>Count: Count occurrences of the list with shallower depth in the list with deeper depth |
| D | 1 | false | false | Triplicate: Push #1 thrice to the stack: #1 -> #1 #1 #1 |
| E | 1 | true | false | 2 to the Power of N: 2 ** #1 |
| F | 2 | false | false | Filter: Filter ArraySeq(#1, #2) by function ArraySeq(#2, #1)</br>Filter: Filter ArraySeq(#2, #1) by function ArraySeq(#1, #2)</br>Find: Find the index of #1 in #2. Switches #1 and #2 so that the haystack is the deeper list |
| G | 2 | false | false | Dyadic Maximum: Maximum of #1 and #2</br>Monadic Maximum: Maximum of #1</br>Generate Sequence: Call #2 on previous results of #2, starting with #1. If #1 is not a list, it is made iterable |
| H | 1 | true | false | To Hex: Convert #1 to hexadecimal |
| I | 2 | false | false | Interleave: Interleave #1 and #2</br>Reject: Remove elements of ArraySeq(#1, #2) that satisfy function ArraySeq(#2, #1)</br>Reject: Remove elements of ArraySeq(#2, #1) that satisfy function ArraySeq(#1, #2) |
| J | 2 | false | false | Join: </br>Join / Merge: Add all elements of #2 to #1</br>Number Pair: Create a list of #1 and #2</br>String Concatenation: string(#1) + string(#2) (if either #1 or #2 is a string) |
| K | 1 | true | false | Factors: Get the factors of #1 |
| L | 1 | false | false | Length: Length of #1 |
| M | 2 | true | false | Map: Map function ArraySeq(#1, #2) over ArraySeq(#2, #1)</br>Map: Map function ArraySeq(#2, #1) over ArraySeq(#1, #2)</br>Mold: Reshape #1 to the shape of #2</br>Multiplicity: How many times #1 divides #2</br>Regex Match: Return the first match of #2 in #1 |
| N | 1 | true | false | Negate: -#1</br>Negate: Swap the case of each letter #1</br>First Non-Negative Integer Where Predicate is True: First non-negative integer where #1 is true |
| O | 1 | true | false | Character to Unicode: Unicode value of each letter in #1</br>Unicode to Character: Character of each unicode value in #1 |
| P | 1 | false | false | Prefixes: Get all prefixes of #1. Treats numbers as a list of digits |
| Q | 2 | false | false | Remove At: Remove the element at index #2 from #1</br>Regex Groups: Return the groups of the first match of #2 in #1 |
| R | 2 | false | false | Range: Range from #1 to #2, exclusive</br>Reduce: Reduce #1 by function #2</br>Regex Match?: Check if #2 matches #1 |
| S | 1 | false | false | Sort: Sort #1 |
| T | 1 | false | false | Transpose: Transpose #1</br>Triple: #1 * 3</br>Does String Contain Only Alphabetic Characters: Check if #1 contains only alphabetic characters |
| U | 1 | false | false | Uninterleave: Uninterleave #1 |
| V | 1 | false | false | Vectorise Reverse: Reverse each item in #1</br>1 - X: 1 - #1 |
| W | -1 | false | false | Wrap: Wrap the entire stack into a list |
| X | 2 | false | false | Cartesian Product: Cartesian product of #1 and #2 |
| Y | 2 | false | false | List Repeat: A list of #1 repeated #2 times. E.g. 3 4 -> [3, 3, 3, 3]</br>List Repeat: A list of ArraySeq(#2, #1) instances of string ArraySeq(#1, #2)</br>List Repeat: A list of ArraySeq(#1, #2) instances of string ArraySeq(#2, #1)</br>Vectorised Repeat: Repeat each element of #2 (#1|#1.length) times |
| Z | 2 | false | false | Zip: Zip #1 and #2 |
| ^ | -1 | false | false | Reverse Stack: Reverse the stack |
| _ | 0 | false | false | Pop: Pop the top of the stack |
| a | 1 | false | false | Any: Are any digits of #1 truthy</br>Is Uppercase: Check if #1 is uppercase. With string.len > 1, vectorises over each character</br>Any: Are any elements of #1 truthy |
| b | 1 | false | false | Binary Digits: Convert #1's list of digits from binary to base 10</br>From Binary: Convert #1 from binary to a number</br>From Binary: Convert #1 from binary to a number |
| c | 2 | false | false | Contains: Is #2 in #1</br>Contains: Is ArraySeq(#2, #1) in ArraySeq(#1, #2)</br>Contains: Is ArraySeq(#1, #2) in ArraySeq(#2, #1)</br>Contains: Is the list with shallower depth in the list with deeper depth |
| d | 1 | false | false | Double: #1 * 2</br>Double: Append a copy of #1 to itself |
| e | 1 | true | false | Is Even: Is #1 even</br>Split Newlines: Split #1 by newlines |
| f | 1 | false | false | List of Digits: Push a list of the digits of #1 to the stack</br>List of Characters: Push a list of the characters of #1 to the stack</br>Flatten: Flatten #1 |
| g | 2 | false | false | Dyadic Minimum: Minimum of #1 and #2</br>Monadic Minimum: Minimum of #1</br>Generate Sequence: Call #2 as a dyad infinitely with items of #1 as starting values |
| h | 1 | false | false | Head: First element of #1 |
| i | 2 | false | false | Nth Element: Get the ArraySeq(#2, #1)th element of ArraySeq(#1, #2)</br>Nth Element: Get the ArraySeq(#1, #2)th element of ArraySeq(#2, #1)</br>Vectorised Index: [#1[_] for _ in #2]</br>String Enclose: enclose #2 in #1 (#1[0:len(#1)//2] + #2 + #1[len(#1)//2:])</br>Object Member Retrieval: ArraySeq(#1, #2).ArraySeq(#2, #1)</br>Object Member Retrieval: ArraySeq(#2, #1).ArraySeq(#1, #2)</br>Collect Unique Values (+ Initial Value): Apply #2 on #1 and collect unique values. Does include the initial value. |
| j | 2 | false | false | Join On: Join ArraySeq(#1, #2) on ArraySeq(#2, #1)</br>Join On: Join ArraySeq(#2, #1) on ArraySeq(#1, #2)</br>Intersperse: Intersperse elements of #2 within #1 (e.g. [1, [2,3], 4] [5, 6] -> [1, 5, 6, [2, 3], 5, 6, 4]) |
| l | 2 | true | false | Logarithm: Log base #2 of #1</br>Same Length: Are #1 and #2 the same length</br>String Length Equals: Is the length of ArraySeq(#1, #2) equal to ArraySeq(#2, #1)</br>String Length Equals: Is the length of ArraySeq(#2, #1) equal to ArraySeq(#1, #2) |
| m | 0 | false | false | Context Secondary: Push the secondary context variable to the stack |
| n | 0 | false | false | Context Primary: Push the primary context variable to the stack |
| o | 2 | false | false | Windows: Get overlapping windows of #1 with a window of size #2</br>Overlapping Slices: Get overlapping pairs of iterable(ArraySeq(#1, #2)) with a window of size ArraySeq(#2, #1)</br>Overlapping Slices: Get overlapping pairs of iterable(ArraySeq(#2, #1)) with a window of size ArraySeq(#1, #2) |
| p | 2 | false | false | Prepend: Prepend #2 to #1 |
| q | 1 | false | false | Quotify: Cast #1 to a string and wrap in quotes |
| r | 3 | false | false | Replace: Replace all occurrences of #2 in #1 with #3 |
| s | 2 | false | false | Split: Split #1 by #2 |
| t | 1 | false | false | Tail: Last element of #1 |
| u | 1 | false | false | Unique: Unique elements of #1</br>Unique By Function: Unique elements of #1 by applying #2 |
| w | 1 | false | false | Wrap in List: Wrap #1 in a list |
| x | -1 | false | false | Recurse: Recursively call the current function (or the top-level program if not in a function) |
| y | 3 | false | false | Transliterate: Replace all occurrences of #2 in #1 with #3</br>Call While: While #1(#3) is true, #3 = #2(#3). Return the result. Type switchable. |
| z | 2 | false | false | Zip With Filler: Transpose #1, filling empty spaces with #2 |
| ⨥ | 1 | true | false | Add 2: #1 + 2</br>String Length Equals 1: Is the length of #1 equal to 1 |
| ⨪ | 1 | true | false | Subtract 2: #1 - 2 |
| ∑ | 1 | false | false | Sum: Sum of #1</br>Join and Evaluate: Join #1 and evaluate the result |
| ∏ | 1 | false | false | Product: Product of #1 |
| σ | 1 | false | false | Cumulative Sums: Cumulative sums of #1 |
| ⇧ | 1 | false | false | Grade Up: Indices that would sort #1 |
| ⇩ | 1 | false | false | Grade Down: Indices that would sort #1 in reverse |
| ∪ | 2 | false | false | Union: Union of #1 and #2 |
| ∩ | 2 | false | false | Intersection: Intersection of #1 and #2 |
| ⊍ | 2 | false | false | Set XOR: Set XOR of #1 and #2 |
| ⦰ | 2 | false | false | Set Difference: Set difference of #1 and #2 |
| « | 2 | true | false | Left Shift: #1 << #2</br>Prepend Spaces to Given Length: Prepend spaces to string ArraySeq(#1, #2) until it is ArraySeq(#2, #1)</br>Prepend Spaces to Given Length: Prepend spaces to string ArraySeq(#2, #1) until it is ArraySeq(#1, #2)</br>Prepend Spaces to Length of Second String: Prepend spaces to string #1 until it is the length of #2 |
| » | 2 | true | false | Right Shift: #1 >> #2</br>Append Spaces to Given Length: Append spaces to string ArraySeq(#1, #2) until it is ArraySeq(#2, #1)</br>Append Spaces to Given Length: Append spaces to string ArraySeq(#2, #1) until it is ArraySeq(#1, #2)</br>Append Spaces to Length of Second String: Append spaces to string #1 until it is the length of #2 |
| Ɠ | 1 | false | true | Max Peek: Maximum of #1 without popping |
| ɠ | 1 | false | true | Min Peek: Minimum of #1 without popping |
| Ġ | 2 | true | false | Zipped Maximum: Maximum of corresponding elements of #1 and #2</br>Vectorised Maximum: Maximum of ArraySeq(#2, #1) and ArraySeq(#1, #2)</br>Vectorised Maximum: Maximum of ArraySeq(#1, #2) and ArraySeq(#2, #1) |
| ⌈ | 1 | true | false | Ceiling: Ceiling of #1</br>Split on Spaces: Split #1 by spaces |
| ⌊ | 1 | true | false | Floor: Floor of #1</br>String to Number: Convert #1 to a number, ignoring non-digit characters. Returns 0 if no digits are found |
| ⊖ | 2 | false | false | 0 Slice: First ArraySeq(#2, #1) elements of ArraySeq(#1, #2)</br>0 Slice: First ArraySeq(#1, #2) elements of ArraySeq(#2, #1)</br>APL Style Take: APL style take |
| ⌽ | 2 | false | false | 1 Slice: First ArraySeq(#2, #1) elements of ArraySeq(#1, #2)</br>1 Slice: First ArraySeq(#1, #2) elements of ArraySeq(#2, #1) |
| £ | 1 | false | false | Set Register: Set the register to #1 |
| ¥ | 0 | false | false | Get Register: Push the register to the stack |
| ↜ | -1 | false | false | Rotate Stack Left: Rotate the stack left |
| ↝ | -1 | false | false | Rotate Stack Right: Rotate the stack right |
| ⬳ | 1 | false | false | Rotate Left: Rotate #1 left</br>Rotate Left: Rotate #1 left #2 times. Right if #2 is negative |
| ⟿ | 1 | false | false | Rotate Right: Rotate #1 right</br>Rotate Right: Rotate #1 right #2 times. Left if #2 is negative |
| ≜ | 3 | false | false | List Assign: #1[#2] = #3</br>Augmented List Assignment: #1[#2] = #3(#1[#2])</br>Vectorised Augmented List Assignment: #1[_] = #3(#1[_]) for _ in #2</br>Zipped Assignment: #1[ind] = val for ind, val in zip(#2, #3)</br>Regex String Replacement: Replace all occurrences of #2 in #1 with #3</br>Regex Substitution: Replace all occurrences of #2 in #1 with the result of #3</br>Object Member Assignment: #1.#2 = #3 |
| ⎀ | 3 | false | false | Insert: Insert #3 into #1 at index #2</br>Insert: Insert #3 into #1 at indices #2</br>Insert: Insert items of #3 into #1 at indices #2 |
| ◲ | 1 | false | false | Sublists: All sublists of #1 |
| ⊢ | 2 | false | false | 10 to Base: Convert #1 to base #2</br>10 to Base: Convert #1 to base len(#2) using the items of #2</br>10 to Base: Convert each item in #1 to base #2</br>10 to Base: Convert each item in #1 to the base of the corresponding item in #2</br>All Regex Matches: All matches of #2 in #1 |
| ⊣ | 2 | false | false | Base to 10: Convert #1 from base #2 to base 10, assuming a base that is a prefix of [0-9A-Z] for strings</br>Base to 10: Convert #1 from base #2 to base 10, using the items of #1 as digits</br>Base to 10: Convert each item in #1 from base #2 to base 10, assuming a base that is a prefix of [0-9A-Z] for strings |
| ɦ | 1 | false | true | Head Peek: First element of #1 without popping |
| ʈ | 1 | false | true | Tail Peek: Last element of #1 without popping |
| ᐐ | 1 | false | false | Init: All but the last element of #1 |
| ᐵ | 2 | false | false | Drop: All but the first ArraySeq(#2, #1) elements of ArraySeq(#1, #2)</br>Drop: All but the first ArraySeq(#1, #2) elements of ArraySeq(#2, #1)</br>APL Style Drop: APL style drop |
| ᐕ | 1 | false | false | Behead: All but the first element of #1 |
| ½ | 1 | true | false | Halve: #1 / 2</br>Two String Halves: Split #1 in half |
| ƶ | 1 | false | false | Range to Length: Range from 0 to len(#1) - 1 |
| Ƶ | 1 | false | false | Range to Length 1: Range from 1 to len(#1) |
| ⁰ | 0 | false | false | First Input: Push the first input to the stack |
| ¹ | 0 | false | false | Second Input: Push the second input to the stack |
| ² | 1 | true | false | Square: #1 ** 2</br>String Pairs: Split #1 into pairs of characters |
| ³ | 1 | true | false | Cube: #1 ** 3</br>String Triples: Split #1 into triples of characters |
| ⅟ | 1 | true | false | Reciprocal: 1 / #1</br>Without Whitespace: Remove all whitespace from #1 |
| ⇄ | 1 | false | false | Reverse: Reverse #1 |
| ⧖ | 1 | false | false | Permutations: All permutations of #1 |
| ‰ | 2 | true | false | Divmod: Divmod of #1 and #2 ([#1 // #2, #1 % #2]) |
| ≛ | 2 | false | false | Divides?: #2 % #1 == 0</br>Append Spaces: Append ArraySeq(#2, #1) spaces to ArraySeq(#1, #2)</br>Append Spaces: Append ArraySeq(#1, #2) spaces to ArraySeq(#2, #1)</br>Regex Span: Span of regex match of pattern #2 in #1 |
| ℭ | 2 | false | false | Combinations with Replacement: All combinations of ArraySeq(#1, #2) of length ArraySeq(#2, #1)</br>Combinations with Replacement: All combinations of ArraySeq(#2, #1) of length ArraySeq(#1, #2)</br>Combinations of Range with Replacement: All combinations of range(#1) of length #2 with replacement |
| ℈ | 2 | false | false | Combinations without Replacement: All combinations of ArraySeq(#1, #2) of length ArraySeq(#2, #1)</br>Combinations without Replacement: All combinations of ArraySeq(#2, #1) of length ArraySeq(#1, #2)</br>Combinations of Range without Replacement: All combinations of range(#1) of length #2 without replacement |
| ⦷ | 1 | true | false | Absolute Value: Absolute value of #1</br>Keep Letters: Keep only the letters of #1 |
| Ϣ | 2 | false | false | Chunk to Length: Chunk ArraySeq(#1, #2) into parts of length ArraySeq(#2, #1)</br>Chunk to Length: Chunk ArraySeq(#2, #1) into parts of length ArraySeq(#1, #2)</br>Partition to Lengths: Partition #1 into parts of lengths #2 |
| ≤ | 2 | true | false | Less Than or Equal: #1 <= #2 |
| ≥ | 2 | true | false | Greater Than or Equal: #1 >= #2 |
| ≠ | 2 | true | false | Not Equal: str(#1) != str(#2) |
| ≡ | 2 | false | false | Equals: Does #1 exactly equal #2 |
| • | 2 | false | false | Dot Product: Dot product of #1 and #2</br>Bijective Base Conversion:  Convert #1 to bijective base #2</br>First Index Where Predicate True: Index of the first value in ArraySeq(#1, #2) where function ArraySeq(#2, #1)</br>First Index Where Predicate True: Index of the first value in ArraySeq(#2, #1) where function ArraySeq(#1, #2) |
| ± | 1 | true | false | Signum: Sign of #1 |
| † | 1 | false | false | Lengths of Consecutives: Lengths of consecutive runs of equal elements in #1 |
| ⎙ | 1 | false | true | Peek Print: Print #1 without popping |
| ✒ | 1 | false | false | Print: Print #1 without a trailing newline |
| ≓ | 1 | false | false | Mirror: Mirror #1 (#1 + reverse(#1)), as the original type |
| Ͼ | 1 | true | false | Vectorised Sums: Sum of each item in #1. Functionally equivalent to `¨Σ` |
| ⛭ | 1 | true | false | 10 to the Power of: 10 ** #1</br>Execute: Execute #1 as Vyxal code</br>Call Function: Call function #1 |
| ⏟ | 2 | false | false | Every Nth Element: Every ArraySeq(#2, #1)th element of ArraySeq(#1, #2)</br>Every Nth Element: Every ArraySeq(#1, #2)th element of ArraySeq(#2, #1)</br>Matrix Multiply: Matrix multiply #1 and #2</br>Regex Full Match?: Does pattern #2 fully match #1 |
| ⌭ | 1 | true | false | Is Prime: Is #1 a prime number?</br>Quine Cheese: Quotify #1 and prepend it to #1. (Useful for quines like `"⌭"⌭`) |
| ⏜ | -1 | false | false | Over: Duplicate the item below the top of the stack -> #2 #1 #2 |
| ⍢ | 1 | true | false | Parity: Parity of #1 (1 if odd, 0 if even) --> #1 % 2</br>Last String Half: Last half of #1 |
| ℂ | 2 | true | false | NCR | N Choose R: nCr of #1 and #2 (n choose r)</br>Characters Same?: Are all characters in #1 the same as #2?</br>Fixpoint Collect: Repeatedly apply ArraySeq(#1, #2) on ArraySeq(#2, #1)</br>Fixpoint Collect: Repeatedly apply ArraySeq(#2, #1) on ArraySeq(#1, #2) |
| ⌹ | 1 | false | false | Integers Partitions: All possible ways to sum positive integers to #1</br>List Partitions: All possible ways to partition #1 into sublists |
| ⏚ | 1 | false | false | Powerset: Powerset of #1 |
| ↯ | 2 | true | false | Inclusive Range: Inclusive range from #1 to #2</br>Sort By: Sort list ArraySeq(#1, #2) (range if num) by function ArraySeq(#2, #1)</br>Sort By: Sort list ArraySeq(#2, #1) (range if num) by function ArraySeq(#1, #2)</br>Regex Split Keep Delimiters: Split #1 by regex #2, keeping the delimiters |
| ⊠ | 2 | false | false | Cartesian Power: Cartesian power of ArraySeq(#1, #2) to the power of ArraySeq(#2, #1)</br>Cartesian Power: Cartesian power of ArraySeq(#2, #1) to the power of ArraySeq(#1, #2)</br>Regex Index: Return first index of pattern match #2 in target string #1, -1 if not found</br>Self-Cartesian Power: Push #1, and then push the cartesian product of #2 with itself |
| ⚅ | 1 | false | false | Random Choice: Random element of #1</br>Random Integer: Random integer from 0 to #1 |
| æ | 1 | false | false | Bifurcate: Duplicate #1 and reverse the duplicate |
| ␣ | 0 | false | false | Space: Push a space to the stack |
| ¶ | 0 | false | false | Newline: Push a newline to the stack |
| ★ | 0 | false | false | Asterisk: Push an asterisk to the stack |
| ᑂ | 1 | false | false | Head on Top, Rest on Bottom: Push #1[1:] and #1[0] |
| ∻ | 2 | true | false | Integer Divide: #1 // #2 |
| √ | 1 | true | false | Square Root: Square root of #1 |
| ¿ | 1 | true | false | Truthy?: Is #1 truthy? (Not 0, empty, or false) |
| ◌ | 1 | true | false | Round: Round #1 to the nearest integer, half-up |
| δ | 1 | false | false | Deltas: Deltas/forward differences of #1 - [a - b, b - c, c - d, ...] |
| ☷ | 1 | false | false | Partition After Truthy:  Partition #1 after truthy indices of #2. |
| ✇ | 1 | false | false | Edges: First and last element of #1</br>Real and Imaginary: Real and imaginary parts of #1 |
| ⎃ | 1 | false | false | Flatten and Join on Nothing: Flatten #1 and join on nothing |
| ⎶ | 2 | false | false | Trim: Trim #1 of leading and trailing #2 |
| ⊆ | 2 | false | false | Subset?: Is the shallower list a subset of the deeper list? Checks windows corresponding to the length of the shallower list |