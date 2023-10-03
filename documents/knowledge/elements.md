## `` λ `` (Lambda)

Open a lambda - λ...;

-------------------------------
## `` ƛ `` (Lambda Map)

Open a mapping lambda - ƛ

-------------------------------
## `` ¬ `` (Logical Not)

Return the inverse (negation) of the truthiness of an item.

### Overloads

- num a: `not a`
- str a: `a != "" | len(a) > 0`
- lst a: `a != [] | len(a) > 0`
-------------------------------
## `` ∧ `` (Logical And)

Returns the first truthy argument if both are truthy, otherwise returns the first falsy argument.

### Overloads

- any a, any b: `a and b`
-------------------------------
## `` ⟑ `` (Apply Lambda)

Like a mapping lambda, but the results are evaluated immediately, instead of being lazily evaluated

-------------------------------
## `` ∨ `` (Logical Or)

Returns the first truthy argument, otherwise the first falsy argument.

### Overloads

- any a, any b: `a or b`
-------------------------------
## `` ⟇ `` (Remove at Index)

Returns every item in a list except the item at the given index.

### Overloads

- any a, num b: `Remove item b of a`
- num a, any b: `Remove item a of b`
-------------------------------
## `` ÷ `` (Item Split)

Pushes each item of the top of the stack onto the stack.

### Overloads

- num a: `Push each digit of a`
- str a: `Push each character of a`
- lst a: `Push each item of a`
-------------------------------
## `` × `` (Asterisk Literal)

the string "*" (asterisk)

-------------------------------
## `` « `` (Base Compressed String)

Open/close a bijective base-255 compressed string - «...«

-------------------------------
## `` ␤ `` (Newline)

NOP

-------------------------------
## `` » `` (Base Compressed Number)

Open/close a bijective base-255 compressed number - »...»

-------------------------------
## `` ° `` (Complex Number Separator)

Separates the real and imaginary parts of a complex number

-------------------------------
## `` • `` (MultiCommand)

Logarithm / Repeat Character / Capitalisation transfer

### Overloads

- num a, num b: `log_a(b)`
- num a, str b: `[char * a for char in b]`
- str a, num b: `[char * b for char in a]`
- str a, str b: `a.with_capitalisation_of(b)`
- lst a, lst b: `a molded  to  the shape of b`
-------------------------------
## `` ß `` (Conditional Execute)
Executes element A if the top of the stack is truthy

Usage:
```
ß<element>
```

-------------------------------
## `` † `` (Function Call)

Calls a function / executes as python / number of distinct prime factors / vectorised not

### Overloads

- fun a: `a()`
- num a: `len(prime_factors(a))`
- str a: `exec as python`
- lst a: `vectorised not`
-------------------------------
## `` € `` (Split On / Fill By Coordinates)

Split a on b (works on lists and numbers as well) / Fill a matrix by calling a function with the lists of coordinates in the matrix.

### Overloads

- any a, any b: `a split on b`
- any a, fun b: `for each value of a (all the way down) call b with the coordinates of that value and put that at the appropriate position in a`
-------------------------------
## `` ½ `` (Halve)

Halves an item

### Overloads

- num a: `a / 2`
- str a: `a split into two strings of equal lengths (as close as possible)`
-------------------------------
## `` ∆ `` (Mathematical Digraph)

Used for mathematical digraphs

-------------------------------
## `` ø `` (String Digraph)

Used for string-based digraphs

-------------------------------
## `` ↔ `` (Combinations/Remove/Fixed Point Collection)

Does either combinations_with_replacement, removes items from a not in b, or applies a on b until the result stops changing (including the initial value).

### Overloads

- any a, num b: `combinations_with_replacement(a, length=b)`
- fun a, any b: `Apply a on b until the result does not change, yielding intermediate values. Includes the initial value.`
- any a, str b: `remove elements from a that are not in b`
- any a, lst b: `remove elements from a that are not in b`
-------------------------------
## `` ¢ `` (Infinite Replacement / Apply at Indices)

Replace b in a with c until a does not change / Call a function on all elements at specified indices together and put that back in the list

### Overloads

- any a, any b, any c: `replace b in a with c until a does not change`
- lst a, fun b, lst c: `apply function b to items in c at indices in a`
- lst a, lst b, fun c: `apply function c to items in a at indices in b`
- fun a, lst b, lst c: `apply function a to items in b at indices in c`
-------------------------------
## `` ⌐ `` (Complement / Comma Split)

1 - a if number, split by commas if string.

### Overloads

- num a: `1 - a`
- str a: `a.split(",")`
-------------------------------
## `` æ `` (Is Prime / Case Check)

(a is prime) if a is a number, else check which case a is

### Overloads

- num a: `is a prime?`
- str a: `caseof(a) (1 if all letters in a are uppercase, 0 if all letters in a are lowercase, -1 if mixed case)`
-------------------------------
## `` ʀ `` (Inclusive Zero Range)

Inclusive range or whether each character is alphabetical

### Overloads

- num a: `range(0,a + 1) (inclusive range from 0)`
- str a: `[is v alphabetical? for v in a]`
-------------------------------
## `` ʁ `` (Exclusive Zero Range)

Exclusive range or palindromise

### Overloads

- num a: `range(0,a) (exclusive range from 0)`
- str a: `palindromise(a) (a + a[:-1:-1])`
-------------------------------
## `` ɾ `` (Inclusive One Range)

Inclusive range or uppercase

### Overloads

- num a: `range(1,a+1) (inclusive range from 1)`
- str a: `a.uppercase()`
-------------------------------
## `` ɽ `` (Exclusive One Range / Lowercase)

Exclusive range or lowercase

### Overloads

- num a: `range(1,a) (exclusive range from 0)`
- str a: `a.lowercase()`
-------------------------------
## `` Þ `` (List Digraph)

Used for list-related digraphs

-------------------------------
## `` ƈ `` (Choose / random choice / set same / drop while)

Binomial coefficient / choose a random items from b / same except duplicates / drop while

### Overloads

- num a, num b: `a choose b (binomial coefficient)`
- num a, str b: `choose a random items from b`
- str a, num b: `choose b random items from a`
- str a, str b: `are the set of characters in the strings the same?`
- any a, fun b: `remove each item x from the beginning of a until b(x) returns false`
- fun a, any b: `remove each item x from the beginning of b until a(x) returns false`
-------------------------------
## `` ∞ `` (Palindromise)

Palindromise a

### Overloads

- any a: `palindromise a (a + a[:-1:-1])`
-------------------------------
## `` ¨ `` (Other Digraphs)

Used for various random digraphs

-------------------------------
## ``   `` (Space)

NOP

-------------------------------
## `` ! `` (Stack Length)

Push the length of the stack

-------------------------------
## `` " `` (Pair)

Place the top two items into a single list

### Overloads

- any a, any b: `[a, b]`
-------------------------------
## `` # `` (Comment)

The characters until the next newline are commented out

-------------------------------
## `` #{ `` (Multiline Comment)

The characters until the next `}#` are commented out. Nestable.

-------------------------------
## `` $ `` (Swap)

Swap the top two items

### Overloads

- any a, any b: `b, a`
-------------------------------
## `` % `` (Modulo / Format)

Modulo two numbers / format two strings

### Overloads

- num a, num b: `a % b`
- num a, str b: `b.format(a) (replace % in b with a)`
- str a, num b: `a.format(b) (replace % in a with b)`
- str a, str b: `a.format(b) (replace % in a with b)`
- str a, lst b: `a.format(b) (replace % in a with each item of b)`
-------------------------------
## `` & `` (Apply To Register)
Apply the next element to the register

Usage:
```
&<element>
```

-------------------------------
## `` ' `` (Lambda Filter)

Open a filter lambda - '...;

-------------------------------
## `` ( `` (Open For Loop)

Start a for loop, iterating over the popped top of stack.

-------------------------------
## `` ) `` (Close For loop)

Close a for loop

-------------------------------
## `` * `` (Multiplication / Arity Change)

Multiply two numbers or strings / Change the arity of a function

### Overloads

- num a, num b: `a * b`
- num a, str b: `b repeated a times`
- str a, num b: `a repeated b times`
- str a, str b: `ring translate a according to b (in a, replace b[0] with b[1], b[1] with b[2], ..., and b[-1] with b[0])`
- fun a, num b: `change the arity of function a to b`
- num a, fun b: `change the arity of function b to a`
-------------------------------
## `` + `` (Addition)

Adds the top two items on the stack

### Overloads

- num a, num b: `a + b`
- num a, str b: `str(a) + b`
- str a, num b: `a + str(b)`
- str a, str b: `a + b`
- num a, fun b: `First integer x greater than a where b(x) is truthy`
- fun a, num b: `First integer x greater than b where a(x) is truthy`
-------------------------------
## `` , `` (Print)

Print a with trailing newline

### Overloads

- any a: `print(a)`
-------------------------------
## `` - `` (Subtract)

Subtracts the top two items on the stack

### Overloads

- num a, num b: `a - b`
- num a, str b: `("-" * a) + b`
- str a, num b: `a + ("-" * b)`
- str a, str b: `a.replace(b, '')`
-------------------------------
## `` . `` (Decimal Separator)

Decimal separator

-------------------------------
## `` / `` (Divide / Split)

Divide two numbers or split strings

### Overloads

- num a, num b: `a / b`
- num a, str b: `b split into a pieces`
- str a, num b: `a split into b pieces`
- str a, str b: `a.split(b)`
- fun a, num b: `First integer x greater than b where a(x) is falsy`
- num a, fun b: `First integer x greater than a where b(x) is falsy`
-------------------------------
## `` 0 `` (Literal digit 0)

Literal digit 0

-------------------------------
## `` 1 `` (Literal digit 1)

Literal digit 1

-------------------------------
## `` 2 `` (Literal digit 2)

Literal digit 2

-------------------------------
## `` 3 `` (Literal digit 3)

Literal digit 3

-------------------------------
## `` 4 `` (Literal digit 4)

Literal digit 4

-------------------------------
## `` 5 `` (Literal digit 5)

Literal digit 5

-------------------------------
## `` 6 `` (Literal digit 6)

Literal digit 6

-------------------------------
## `` 7 `` (Literal digit 7)

Literal digit 7

-------------------------------
## `` 8 `` (Literal digit 8)

Literal digit 8

-------------------------------
## `` 9 `` (Literal digit 9)

Literal digit 9

-------------------------------
## `` : `` (Duplicate)

Push a twice

### Overloads

- any a: `a,a`
-------------------------------
## `` ; `` (Close Structure)

Close a lambda / map lambda / sort lambda / function

-------------------------------
## `` < `` (Less Than)

Basic comparison - less than

### Overloads

- num a, num b: `a < b`
- num a, str b: `str(a) < b`
- str a, num b: `a < str(b)`
- str a, str b: `a < b`
- any a, fun b: `decrement a until b returns false`
- fun a, any b: `decrement b until a returns false`
-------------------------------
## `` = `` (Equals)

Basic comparison - equals

### Overloads

- num a, num b: `a == b`
- num a, str b: `str(a) == b`
- str a, num b: `a == str(b)`
- str a, str b: `a == b`
-------------------------------
## `` > `` (Greater Than)

Basic comparison - greater than

### Overloads

- num a, num b: `a > b`
- num a, str b: `str(a) > b`
- str a, num b: `a > str(b)`
- str a, str b: `a > b`
- any a, fun b: `increment a until b returns false`
- fun a, any b: `increment b until a returns false`
-------------------------------
## `` ? `` (Input)

Get and evaluate the next input (readline) from the input source.

-------------------------------
## `` @ `` (Vectorised Length)

Lengths of each item in a list

### Overloads

- lst a: `['len(x) for x in a']`
-------------------------------
## `` ¨@ `` (Function Call / Declaration)

Call / declare function (@name; / @name|code;)

-------------------------------
## `` A `` (All)

Check if all items in a list are truthy / check if a character is a vowel

### Overloads

- str a: `is_vowel(a) if a.length == 1 else [is_vowel(z) for z in a]`
- any a: `all(a)`
-------------------------------
## `` B `` (Binary To Decimal)

Convert a binary string or list to base 10

### Overloads

- any a: `int(a,2) (convert from base 2 to base 10)`
-------------------------------
## `` C `` (Chr / Ord)

Convert between characters and ordinals

### Overloads

- num a: `chr(a)`
- str a: `ord(a) if length 1 else list of ordinals`
-------------------------------
## `` D `` (Triplicate)

Push three copies of a to stack

-------------------------------
## `` E `` (Two Power / Python Eval)

2 ** a, or eval(a)

### Overloads

- num a: `2 ** a`
- str a: `eval(a) (safe-eval as python)`
-------------------------------
## `` F `` (Filter)

Filter a list by another list or function.

### Overloads

- any a, fun b: `filter(b,a) (filter a by the ones that b returns a truthy result for)`
- any a, any b: `remove elements of a that are in b`
-------------------------------
## `` G `` (Max)

Maximum value or a

### Overloads

- any a: `max(a)`
-------------------------------
## `` H `` (Hex To Decimal)

Convert hexadecimal to decimal

### Overloads

- any a: `int(a,16) (from hexadecimal)`
-------------------------------
## `` I `` (Into Two Pieces)

Push n spaces / quine cheese / into two pieces

### Overloads

- num a: `push a spaces`
- str a: `equivalent to `qp``
- lst a: `split a list into two halves`
-------------------------------
## `` J `` (Merge)

Join two lists or items

### Overloads

- lst a, str b: `a.append(b) (append)`
- lst a, num b: `a.append(b) (append)`
- str a, lst b: `b.prepend(a) (prepend)`
- num a, lst b: `b.prepend(a) (prepend)`
- lst a, lst b: `merged(a,b) (merge)`
- any a, any b: `a + b (concatenate)`
-------------------------------
## `` K `` (Factors / Substrings / Prefixes)

Get either the factors of a, substrings that occur more than once, or prefixes

### Overloads

- num a: `divisors(a) (positive integer factors)`
- str a: `all non-empty substrings of a that occur more than once in a`
- lst a: `prefixes(a) (prefixes)`
-------------------------------
## `` L `` (Length)

Get length of a

### Overloads

- any a: `len(a)`
-------------------------------
## `` M `` (Map Function)

Map function object b over a

### Overloads

- any a, fun b: `map(b,a) (apply function b to each of a)`
- any a, any b: `pair each item of b with a ([[a, i] for i in b])`
-------------------------------
## `` N `` (Negate / Swap Case / First Integer Where Truthy)

Negate a number / swap case of a string / first integer where a function truthy

### Overloads

- num a: `-a  (negate)`
- str a: `swap_case(a) (toggle case)`
- fun a: `first integer where a(n) is true`
-------------------------------
## `` O `` (Count / Maximums-by)

Count number of times b occurs in a / Maximums-by

### Overloads

- any a, any b: `a.count(b)`
- any a, fun b: `all elements in a where the result of b(x) is highest`
-------------------------------
## `` P `` (Strip / Minimums-by)

Remove the set of elements in b from both ends of a / Minimums-by

### Overloads

- any a, any b: `a.strip(b)`
- any a, fun b: `all elements in a where the result of b(x) is lowest`
-------------------------------
## `` Q `` (Quit)

Quit the program

-------------------------------
## `` R `` (Reduce)

Reduce a by b, or reverse each item of b

### Overloads

- num a, num b: `a in base b, using a default alphabet 0-9A-Z`
- any a, fun b: `reduce(b,a) (Reduce a by b)`
- any a, any b: `a, vectorised_reverse(b)`
-------------------------------
## `` S `` (Stringify)

Stringify a list or number

### Overloads

- any a: `str(a) (Stringify)`
-------------------------------
## `` T `` (Truthy Indices / Triple / Triadify)

Get indices of truthy elements, triple, or make the arity of a function 3

### Overloads

- num a: `a * 3`
- any a: `truthy_indices(a)`
- fun a: `set the arity of function a to 3`
-------------------------------
## `` U `` (Uniquify)

Remove duplicates

### Overloads

- any a: `uniquify(a) (remove duplicates)`
-------------------------------
## `` V `` (Replace / Map to Indices)

Replace b with c in a / Map a function at elements of a list whose indices are in another list

### Overloads

- any a, any b, any c: `a.replace(b,c) (replace). b can be a list of substrings to replace. If b is a list of substrings, so can c. If c is shorter, the extra strings in b will be replaced with the empty string, i.e., removed.`
- lst a, lst b, fun c: `for each i in b, change the ith element in a by applying the function, then return the new list`
- lst a, num b, fun c: `replace the bth element in a by applying the function, then return the new list`
-------------------------------
## `` W `` (Wrap)

Stack wrapped into a list

-------------------------------
## `` X `` (Return)

Return early (like Q but actually prints a value)

-------------------------------
## `` Y `` (Interleave)

Interleave two lists

### Overloads

- any a, any b: `interleave(a,b) (a[0], b[0], a[1], b[1], ...)`
-------------------------------
## `` Z `` (Zip)

Zip two lists or Zip a with b mapped over a. Fills with 0s if needed.

### Overloads

- any a, any b: `zip(a,b)`
- any a, fun b: `zip(a,map(b,a)) (zipmap, map and zip)`
-------------------------------
## `` [ `` (Open If Statement)

Open an if Statement

-------------------------------
## `` \ `` (Single char Literal)

Pushes a single character

-------------------------------
## `` ] `` (Close If Statement)

Close an if Statement

-------------------------------
## `` ` `` (String Literal)

A string literal - `...`

-------------------------------
## `` ^ `` (Reverse Stack)

Reverse the stack.

-------------------------------
## `` _ `` (Pop)

Pop the top item of the stack

-------------------------------
## `` a `` (Any)

Check if any items of a list are truthy / Check if a character is an uppercase letter

### Overloads

- str a: `is_uppercase(a) if a.length == 1 else [is_uppercase(z) for z in a]`
- lst a: `any(a) (are any items truthy?)`
-------------------------------
## `` b `` (Binary)

Convert a number or string to binary

### Overloads

- num a: `bin(a) (list of binary digits of a)`
- str a: `[bin(ord(char)) for char in a] (list of binary digits for each codepoint in a)`
-------------------------------
## `` c `` (Contains / First Truthy Item Under Function Application)

Check if one thing contains another / returns the first truthy item in a list after applying a function

### Overloads

- any a, fun b: `first item of a where b(x) is truthy (shortcut for Fh)`
- any a, any b: `b in a (does a contain b, membership, contains)`
-------------------------------
## `` d `` (Double / Dyadify)

Double a number or repeat a string twice / make a function dyadic

### Overloads

- num a: `a * 2 (double)`
- str a: `a * 2 (repeated twice)`
- fun a: `change the arity of the function to 2`
-------------------------------
## `` e `` (Exponentiation)

Exponentiate two numbers / extend string / get length of a regex match

### Overloads

- num a, num b: `a ** b (exponentiation)`
- str a, num b: `append a[0] until a is length b (spaces are used if a is empty)`
- num a, str b: `append b[0] until b is length a (spaces are used if b is empty)`
- str a, str b: `regex.search(pattern=a, string=b).span() (length of regex match)`
-------------------------------
## `` f `` (Flatten)

Turn a number into a list of digits, split a string into a list of characters, or flatten a list.

### Overloads

- num a: `digits of a`
- str a: `list of characters of a`
- lst a: `flatten(a) (deep flatten)`
-------------------------------
## `` g `` (Minimum)

Take the minimum of a list

### Overloads

- any a: `min(a)`
-------------------------------
## `` h `` (Head)

First item of something

### Overloads

- any a: `a[0] (first item)`
-------------------------------
## `` i `` (Index)

Index into a list

### Overloads

- any a, num b: `a[b] (index)`
- num a, any b: `b[a] (index)`
- str a, str b: `enclose b in a (b[0:len(b)//2] + a + b[len(b)//2:])`
- any a, [x] b: `a[:b] (0 to bth item of a)`
- any a, [x,y] b: `a[x:y] (x to yth item of a)`
- any a, [x,y,m] b: `a[x:y:m] (x to yth item of a, taking every mth)`
-------------------------------
## `` j `` (Join)

Join a by b.

### Overloads

- any a, any b: `a.join(b)`
-------------------------------
## `` k `` (Constant Digraph)

Used for constant digraphs.

-------------------------------
## `` l `` (Cumulative Groups / First Non-Negative Truthy Integers)

Cumulative groups (overlapping groups, aperture) / Equal length

### Overloads

- any a, num b: `[a[0:b], a[1:b+1], a[2:b+2], ..., a[-b:]]`
- num a, any b: `[b[0:a], b[1:a+1], b[2:a+2], ..., b[-a:]]`
- any a, any b: `length(a) == length(b)`
- any a, fun b: `first a non-negative integers where b is truthy`
- fun a, any b: `first b non-negative integers where a is truthy`
-------------------------------
## `` m `` (Mirror)

Append input reversed to itself.

### Overloads

- num a: `a + reversed(a) (as number)`
- str a: `a + reversed(a)`
- lst a: `append reversed(a) to a`
-------------------------------
## `` n `` (Context)

Context variable, value of the current loop or function.

-------------------------------
## `` o `` (Remove)

Remove instances of b in a

### Overloads

- num a, fun b: `first a integers where b is truthy (0, 1, -1, ...)`
- fun a, num b: `first b integers where a is truthy`
- any a, any b: `a.replace(b,"")`
-------------------------------
## `` p `` (Prepend)

Prepend b to a

### Overloads

- any a, any b: `a.prepend(b) (prepend b to a)`
-------------------------------
## `` q `` (Uneval)

Enclose in backticks, escape backslashes and backticks.

### Overloads

- any a: `uneval(a) (enclose in backticks + escape)`
-------------------------------
## `` r `` (Range)

Range between two numbers, or cumulative reduce, or regex match

### Overloads

- num a, num b: `range(a,b) (range from a to b)`
- num a, str b: `append spaces to b to make it length a`
- str a, num b: `prepend spaces to a to make it length b`
- any a, fun b: `cumulative_reduce(a,function=b) (prefixes of a reduced by b)`
- str a, str b: `regex.has_match(pattern=a,string= b) (does b match a)`
-------------------------------
## `` s `` (sort)

Sort a list or string

### Overloads

- any a: `sorted(a) (sort)`
-------------------------------
## `` t `` (Tail)

Last item

### Overloads

- any a: `a[-1] (last item)`
-------------------------------
## `` u `` (Minus One)

Push -1

-------------------------------
## `` v `` (Vectorise)
Vectorise an element

Usage:
```
v<element>
```

-------------------------------
## `` ¨v `` (Simple vectorise)
Simple vectorise an element. Well, you'll have to look at the code to know what that means.

Usage:
```
¨v<element>
```

-------------------------------
## `` ¨V `` (Right vectorize)
Right vectorize an element. Like `v`, but vectorizes on the rightmost list instead of the leftmost list.

Usage:
```
¨V<element>
```

-------------------------------
## `` w `` (Listify)

a wrapped in a singleton list

### Overloads

- any a: `[a] (wrap in singleton list)`
-------------------------------
## `` x `` (Recurse)

Call current function

-------------------------------
## `` y `` (Uninterleave)

Push every other item of a, and the rest.

### Overloads

- any a: `a[::2], a[1::2] (every second item, the rest)`
-------------------------------
## `` z `` (Overlapping pairs)

Push overlapping pairs of a. Equivalent to 2l

### Overloads

- any a: `a[i:i+2] for i in range(len(a)-1) (overlapping pairs)`
-------------------------------
## `` { `` (Open While Loop)

Open a while loop - `{...}`

-------------------------------
## `` | `` (Branch In Structure)

Branch the structure - means various things depending on context

-------------------------------
## `` } `` (Close While Loop)

Close a while loop

-------------------------------
## `` ~ `` (Filter / Execute Without Pop)
For monads, filter a list by that. For dyads, execute without popping from the stack.

Usage:
```
~<element>
```

-------------------------------
## `` ↑ `` (Max by Tail)

Maximum by last item

### Overloads

- any a: `max(a, key=lambda x: x[-1]) (maximum by last item)`
-------------------------------
## `` ↓ `` (Min by Tail)

Minimum by last item

### Overloads

- any a: `min(a, key=lambda x: x[-1]) (minimum by last item)`
-------------------------------
## `` ∴ `` (Dyadic Maximum)

Maximum of two values / Maximum of a list by a function

### Overloads

- any a, any b: `max(a,b)`
- any a, fun b: `max(a,key=b)`
-------------------------------
## `` ∵ `` (Dyadic Minimum)

Minimum of two values / Minimum of a list by a function

### Overloads

- any a, any b: `min(a,b)`
- any a, fun b: `min(a,key=b)`
-------------------------------
## `` › `` (Increment / Space Replace With 0)

Add 1 to a number / replace all spaces in a string with "0"

### Overloads

- num a: `a + 1`
- string a: `a.replace(" ","0")`
-------------------------------
## `` ‹ `` (Decrement)

Subtract 1 from a number

### Overloads

- num a: `a - 1`
- str a: `a + "-"`
-------------------------------
## `` ∷ `` (Parity)

A number modulo 2

### Overloads

- num a: `a % 2 (odd?)`
- str a: `second half of A`
-------------------------------
## `` ¤ `` (Empty String)

The empty string

-------------------------------
## `` ð `` (Space)

A Space

-------------------------------
## `` → `` (Variable Set)

Set variable (→name)

-------------------------------
## `` ← `` (Variable Get)

Get the value of a variable (←name)

-------------------------------
## `` β `` (To Base Ten / From Custom Base)

Convert a number from a custom base to base 10

### Overloads

- any a, num b: `a to base 10 from number base b, treating list items / string items as digits`
- str a, str b: `a to base 10 from custom string base b, replacing values in a with their index in b and converting to base 10`
- lst a, lst b: `a to base 10 from custom list base b, replacing values in a with their index in b and converting to base 10`
- str a, lst b: `a to base 10 from custom list base b, parsing a into a list only containing the items in b, then replacing values in a with their index in b and converting to base 10`
-------------------------------
## `` τ `` (From Base Ten / To Custom Base)

Convert a number to a different base from base 10.

### Overloads

- num a, num b: `list of digits of a in base b`
- num a, str b: `a converted into a string of characters of b`
- num a, lst b: `a converted into a list of arbitrary values from b`
-------------------------------
## `` ȧ `` (Absolute value)

Take the absolute value of a number, or remove whitespace from a string

### Overloads

- num a: `abs(a) (absolute value)`
- str a: `remove whitespace from a`
-------------------------------
## `` ḃ `` (Boolify)

Convert an arbitrary value into a truthy or falsy value, vectorises with flag t

### Overloads

- any a: `bool(a) (booliify)`
-------------------------------
## `` ċ `` (Not One)

Check if something is not equal to 1

### Overloads

- any a: `a != 1`
-------------------------------
## `` ḋ `` (Divmod)

Divmod / combinations / trim / chunk-while

### Overloads

- num a, num b: `[a // b, a % b] (divmod - division and modulo)`
- str a, num b: `combinations of a with length b`
- lst a, num b: `combinations of a with length b`
- str a, str b: `overwrite the start of a with b (b + a[len(b):])`
- lst a, fun b: `group elements in a by elements that fulfil predicate b`
-------------------------------
## `` ė `` (Enumerate)

Zip with a range of the same length

### Overloads

- any a: `enumerate(a) (zip with 0...len(a))`
-------------------------------
## `` ḟ `` (Find)

Find a value in another

### Overloads

- any a, any b: `a.find(b) (indexing, -1 if not found)`
- any a, fun b: `truthy indices of mapping b over a`
-------------------------------
## `` ġ `` (Gcd / Group by Function)

Greatest Common Denominator of a list or some numbers

### Overloads

- lst a: `GCD(a) (gcd of whole list)`
- num a, num b: `gcd(a,b) (dyadic gcd)`
- str a, str b: `longest common suffix of a and b`
- fun a, any b: `group b by the results of function a`
- any a, fun b: `group a by the results of function b`
-------------------------------
## `` ḣ `` (Head Extract)

Separate the first item of something and push both to stack

### Overloads

- any a: `a[0], a[1:] (head extract)`
-------------------------------
## `` ḭ `` (Floor Division)

Floor divide a by b

### Overloads

- num a, num b: `a // b (floor division, floor(a / b))`
- str a, num b: `(a divided into b pieces)[0]`
- num a, str b: `(b divided into a pieces)[0]`
- any a, fun b: `right reduce a by b (foldr)`
- fun a, any b: `right reduce b by a (foldr)`
-------------------------------
## `` ŀ `` (Left Justify / Gridify / Infinite Replace / Collect until false)

Find one value inside another, starting from a certain index.

### Overloads

- num a, num b, num c: `a <= c <= b`
- num a, num b, str c: `a by b grid of c`
- num a, str b, num c: `a by c grid of b`
- num a, str b, str c: `b.ljust(a,filler=c)`
- str a, num b, num c: `b by c grid of a`
- str a, num b, str c: `a.ljust(c,filler=b)`
- str a, str b, num c: `a.ljust(b,filler=c)`
- str a, str b, str c: `a.infinite_replace(b, c)`
- fun a, fun b, any c: `[c, a(c), a(a(c)), ...], stopping at the first element x such that b(x) is falsy`
-------------------------------
## `` ṁ `` (Mean)

Average of a list - sum / length

### Overloads

- str a: `palindromise(a) (a + a[:-1:-1])`
- num a: `random.randint(0, a)`
- lst a: `mean(a)`
-------------------------------
## `` ṅ `` (Join By Nothing)

Join a list by the empty string. Vectorises if the list contains lists.

### Overloads

- num a: `abs(a) <= 1`
- str a: `pad with 0s to nearest positive multiple of 8`
- lst a: `"".join(a)`
- fun a: `first integer x where a(x) is truthy`
-------------------------------
## `` ȯ `` (Slice)

Slice from an index to the end

### Overloads

- fun a, num b: `first b positive integers for which a(x) is truthy`
- any a, num b: `a[b:] (slice from b to the end)`
- str a, str b: `vertically merge a and b`
-------------------------------
## `` ṗ `` (Powerset)

All possible combinations of a

### Overloads

- any a: `all subsets of a (including the empty subset)`
-------------------------------
## `` ṙ `` (Round)

Round a number to the nearest integer / real and imaginary part of complex number

### Overloads

- num a: `round(a)`
- complex a: `[real(a), imag(a)]`
- str a: `quad palindromise with overlap`
-------------------------------
## `` ṡ `` (Sort by Function)

Sort a list by a function / create a range / split on a regex

### Overloads

- any a, fun b: `sorted(a, key=b) (sort by b)`
- num a, num b: `range(a, b + 1) (inclusive range from a to b)`
- str a, str b: `regex.split(pattern=b, string=a)`
-------------------------------
## `` ṫ `` (Tail Extract)

Remove the last item and push both onto the stack

### Overloads

- any a: `a[:-1],a[-1]`
-------------------------------
## `` ẇ `` (Chunk Wrap)

Wrap a list in chunks of a certain length / apply a function to every second item of a list

### Overloads

- any a, num b: `a wrapped in chunks of length b`
- num a, any b: `b wrapped in chunks of length a`
- any a, lst b: `wrap a into chunks with lengths given in b, repeating if necessary`
- lst a, any b: `wrap b into chunks with lengths given in a, repeating if necessary`
- any a, fun b: `apply b to every second item of a ([a[0], b(a[1]), a[2], ...])`
- fun a, any b: `apply a to every second item of b ([b[0], a(b[1]), b[2], ...])`
- str a, str b: `split a on first occurrence of b`
-------------------------------
## `` ẋ `` (Repeat)

Repeat a value several times

### Overloads

- str a, num b: `a * b`
- num a, str b: `b * a`
- any a, num b: `repeat a b times ([a, a, ...])`
- str a, str b: `a + " " + b`
- fun a, any b: `repeat function a on b while results are not unique ([a(b), a(a(b)), a(a(a(b))), ...] stopping at the first element i such that i == a(i))`
- any a, fun b: `repeat function a on b while results are not unique ([b(a), b(b(a)), b(b(b(a))), ...] stopping at the first element i such that i == b(i))`
-------------------------------
## `` ẏ `` (Exclusive Range Length)

Range from 0 to length of a

### Overloads

- any a: `range(0, len(a)) (exclusive range from 0 to length of a)`
-------------------------------
## `` ż `` (Inclusive Range Length)

Range from 1 to length of a inclusive

### Overloads

- any a: `range(1, len(a)+1) (inclusive range from 1 to length of a)`
-------------------------------
## `` √ `` (Square Root)

Square root a number / every second character of a

### Overloads

- num a: `sqrt(a) (square root)`
- str a: `every second character of a (a[0] + a[2] + ...)`
-------------------------------
## `` ⟨ `` (Open List)

Open a list - ⟨...⟩

-------------------------------
## `` ⟩ `` (Close list)

Close a list - ⟨...⟩

-------------------------------
## `` ‛ `` (Two Character String)

Collect the next two characters as a string - ‛..

-------------------------------
## `` ₀ `` (Ten)

Push 10 to the stack

-------------------------------
## `` ₁ `` (Hundred)

Push 100 to the stack

-------------------------------
## `` ₂ `` (Is Even)

Check if a value is even

### Overloads

- num a: `a % 2 == 0 (even?)`
- any a: `len(a) % 2 == 0 (length even?)`
-------------------------------
## `` ₃ `` (Divisible By Three)

Check if a is divisible by 3

### Overloads

- num a: `a % 3 == 0 (divisible by 3?)`
- any a: `len(a) == 1 (length is 1?)`
-------------------------------
## `` ₄ `` (Twenty Six)

Push 26 to the stack

-------------------------------
## `` ₅ `` (Divisible By Five)

Check if a is divisible by 5

### Overloads

- num a: `a % 5 == 0`
- any a: `a, len(a)`
-------------------------------
## `` ₆ `` (Sixty Four)

Push 64 to the stack

-------------------------------
## `` ₇ `` (One Twenty Eight)

Push 128 to the stack

-------------------------------
## `` ₈ `` (Two Fifty Six)

Push 256 to the stack

-------------------------------
## `` ¶ `` (Newline)

Push a newline to the stack

-------------------------------
## `` ⁋ `` (Join On Newlines)

Join the top of the stack on newlines (insert "\n" between items)

### Overloads

- any a: `"\\n".join(a)`
-------------------------------
## `` § `` (Vertical Join)

Transpose (filling with spaces) and then join on newlines

### Overloads

- any a: `transpose a, join on newlines`
-------------------------------
## `` ε `` (Absolute Difference / Repeat / Regex match / Uniquify by Function)

Returns the absolute difference / Fills an array of a certain length / Does a regex match / Uniquify by function

### Overloads

- num a, num b: `abs(a - b)`
- num a, str b: `[b] * a`
- str a, num b: `[a] * b`
- str a, str b: `regex.match(b, a) (first match of regex b on a)`
- any a, fun b: `keep only unique elements of a, where f(x) is unique`
-------------------------------
## `` ¡ `` (Factorial)

Returns the factorial of the top of the stack

### Overloads

- num a: `factorial(a) (math.gamma(a + 1))`
- str a: `a.sentence_case()`
-------------------------------
## `` ∑ `` (Summate)

Returns the sum of the top of the stack (reduce by addition)

### Overloads

- num a: `sum(digits of a)`
- str a: `a`
- lst a: `sum(a)`
-------------------------------
## `` ¦ `` (Cumulative Sum)

Returns the sums of the prefixes of the top of the stack (cumulatively reduce by addition)

### Overloads

- any a: `cumulative_sum(a) ([a[0], a[0]+a[1], a[0]+a[1]+a[2], ...])`
-------------------------------
## `` ≈ `` (All Equal)

Returns whether all items are equal

### Overloads

- any a: `are all items in a equal?`
-------------------------------
## `` µ `` (Sorting Lambda)

Sort the top of the stack by the function µ...;

-------------------------------
## `` Ȧ `` (Assign)

The equivalent of a[b] = c

### Overloads

- any a, num b, any c: `a but item b (0-indexed) is set to c`
-------------------------------
## `` Ḃ `` (Bifurcate)

Pushes the top of the stack then its reverse. Literally duplicate and reverse

### Overloads

- any a: `a, reversed(a)`
-------------------------------
## `` Ċ `` (Counts)

Returns a list of [item, count of item in the top of stack]

### Overloads

- any a: `[[x, a.count(x)] for x in a]`
-------------------------------
## `` Ḋ `` (Is Divisible / Arbitrary Duplicate / Ordered Group By)

Returns whether two items are divisible / numerous copies of the top of the stack / groups by results of function preserving order (adjacent group-by)

### Overloads

- num a, num b: `a % b == 0`
- num a, str b: `a copies of b`
- str a, num b: `b copies of a`
- str a, str b: `b + " " + a`
- any a, fun b: `group a by the results of b, order is preserved (adjacent group-by)`
- fun a, any b: `group b by the results of a, order is preserved (adjacent group-by)`
-------------------------------
## `` Ė `` (Vyxal Exec / Reciprocal)

Executes as Vyxal / Reciprocal of number

### Overloads

- str a: `vy_exec(a)`
- num a: `1 / a`
-------------------------------
## `` Ḟ `` (Generator / Modulo Index / Format)

Make a generator from function a with initial vector b, or get every nth item or format numbers as decimals.

### Overloads

- num a, num b: `sympy.N(a, b) (evaluate a to b decimal places)`
- str a, num b: `every bth letter of a (a[::b])`
- num a, str b: `every ath letter of b (b[::a])`
- str a, str b: `replace spaces in a with b`
- lst a, num b: `every bth item of a (a[::b])`
- num a, lst b: `every ath item of b (b[::a])`
- fun a, lst b: `generator from function a with initial vector b`
-------------------------------
## `` Ġ `` (Group consecutive)

Group consecutive identical items

### Overloads

- lst a: `group consecutive identical items`
- str a: `group consecutive identical characters`
- num a: `group consecutive identical digits`
-------------------------------
## `` Ḣ `` (Head Remove / Behead)

All but the first item of a list / Drop 1

### Overloads

- lst a: `a[1:] or [] if empty`
- str a: `a[1:] or '' if empty`
- num a: `range(2, a + 1)`
-------------------------------
## `` İ `` (Index into / Collect while unique / Complex Number)

Index into list at indices / Collect values while values are unique (not including the initial value)

### Overloads

- num a, num b: `a + b * i`
- any a, lst b: `[a[item] for item in b]`
- any a, fun b: `Apply b on a and collect unique values. Does not include the initial value.`
-------------------------------
## `` Ŀ `` (Transliterate)

Replace each item of one value in another value with the corresponding element from a third value

### Overloads

- any a, any b, any c: `transliterate(a,b,c) (in a, replace b[0] with c[0], b[1] with c[1], b[2] with c[2], ...)`
- fun a, fun b, any c: `call b on c until a(c) is falsy`
-------------------------------
## `` Ṁ `` (Insert)

Insert a value at a specified index / Map a function over every nth item of a list

### Overloads

- any a, num b, any c: `a.insert(b,c) (insert c at position b in a)`
- any a, num b, fun c: `c mapped over every bth item of a ([c(v) if i%b==0 else v for i,v in enumerate(a)])`
-------------------------------
## `` Ṅ `` (Integer partitions / First Truthy Non-Negative Integer)

Integer partitions / join by space

### Overloads

- num a: `integer_partitions(a) (integer partitions)`
- any a: `" ".join(a) (join by space)`
- fun a: `first truthy non-negative integer where a is truthy`
-------------------------------
## `` Ȯ `` (Over)

Push the second-last item of stack to the top

-------------------------------
## `` Ṗ `` (Permutations)

Get all permutations of a value

### Overloads

- any a: `permutations(a) (get all permutations)`
-------------------------------
## `` Ṙ `` (Reverse)

Reverse a value

### Overloads

- any a: `reversed(a)`
-------------------------------
## `` Ṡ `` (Vectorised sums / Strip whitespace from both sides / Is positive)

Sum of each item in a list

### Overloads

- lst a: `vectorising_sum(a)`
- str a: `a.strip()`
- num a: `is_positive(a)`
-------------------------------
## `` Ṫ `` (Tail Remove / Truthy Under)

Cut off the last item of a list / push 1 under the top of the stack

### Overloads

- num a: `push 1 then the popped item`
- any a: `a[:-1] (all but the last item)`
-------------------------------
## `` Ẇ `` (Split And Keep Delimiter)

Split a value and keep the delimiter

### Overloads

- any a, any b: `a.split_and_keep_delimiter(b) (split and keep the delimiter)`
- fun a, any b: `apply a to every second item of b starting on the first item`
-------------------------------
## `` Ẋ `` (Cartesian Product / Fixpoint)

Take the Cartesian Product of two values, or apply a function until there is no change. If arguments are numbers, turns them into ranges.


### Overloads

- any a, any b: `cartesian-product(a,b)`
- fun a, any b: `apply a on b until b does not change`
-------------------------------
## `` Ẏ `` (Slice Until)

Slice a list until a certain index / find all results for a regex match

### Overloads

- any a, num b: `a[0:b] (slice until b)`
- num a, any b: `b[0:a] (slice until a)`
- str a, str b: `regex.findall(pattern=a,string=b) (find all matches for a regex)`
- any a, fun b: `take results from a while b(x) is truthy`
- fun a, any b: `take results from b while a(x) is truthy`
-------------------------------
## `` Ż `` (Slice From One Until)

Slice from index 1 until a number / get groups of a regex match

### Overloads

- any a, num b: `a[1:b] (slice from 1 until b)`
- num a, any b: `b[1:a] (slice from 1 until a)`
- str a, str b: `regex.match(pattern=a,string=b).groups() (Get groups for a regex match)`
- fun a, any b: `get all groups from b where a(x) is truthy`
-------------------------------
## `` ₌ `` (Parallel Apply)
Parallel apply two elements to the top of the stack

Usage:
```
₌<element><element>
```

-------------------------------
## `` ₍ `` (Parallel Apply Wrap)
Parallel apply two elements and wrap the results in a list

Usage:
```
₍<element><element>
```

-------------------------------
## `` ⁰ `` (Very Last Input)

Push the very last input (input[::-1][0] or input[-1]) to the stack

-------------------------------
## `` ¹ `` (Second Last Input)

Push the second last input (input[::-1][1] or input[-2]) to the stack

-------------------------------
## `` ² `` (Square)

Square a number / Format a string into a square

### Overloads

- num a: `a ** 2 (squared)`
- str a: `a formatted as a square (list of sqrt(len(a)) strings, each sqrt(len(a)) long, such that joining the strings and removing spaces in the end gives a)`
-------------------------------
## `` ∇ `` (Shift)

Shift the top of stack two values down

### Overloads

- any a, any b, any c: `c,a,b (shift)`
-------------------------------
## `` ⌈ `` (Ceiling)

Take the ceiling of a number / Imaginary part of complex number / split a string on spaces

### Overloads

- num a: `ceil(a) (ceiling)`
- complex a: `imaginary part of a`
- str a: `split on spaces`
-------------------------------
## `` ⌊ `` (Floor)

Floor a number / real part of complex number / extract the integer part of a string

### Overloads

- num a: `floor(a) (floor)`
- complex a: `real part of a`
- str a: `integer part of a`
-------------------------------
## `` ¯ `` (Deltas)

Deltas (consecutive differences)

### Overloads

- any a: `deltas(a) ([a[1] - a[0], a[2] - a[1], ...])`
-------------------------------
## `` ± `` (Sign)

Get the sign of a number

### Overloads

- num a: `sign_of(a) (positive = 1, 0 = 0; negative = -1)`
- str a: `is a numeric`
-------------------------------
## `` ₴ `` (Print Without Newline)

Print a value without a trailing newline

-------------------------------
## `` … `` (Print Without Popping)

Print a value without popping the stack

-------------------------------
## `` □ `` (Input List)

All inputs wrapped in a list

-------------------------------
## `` ↳ `` (Right Bit Shift)

Right-bitshift a value / right-justify a string / apply a on b until the result stops changing (not including the initial value)

### Overloads

- num a, num b: `a << b`
- num a, str b: `a.rjust(b)`
- str a, num b: `b.rjust(a)`
- str a, str b: `a.rjust(len(b)-len(a))`
- fun a, any b: `Apply a on b until the result does not change, yielding intermediate values. Does not the initial value.`
- any a, fun b: `Apply b on a until the result does not change, yielding intermediate values. Does not the initial value.`
-------------------------------
## `` ↲ `` (Left Bit Shift)

Left-bitshift a value / left-justify a string / Collect while unique with initial value

### Overloads

- num a, num b: `a >> b`
- num a, str b: `a.ljust(b)`
- str a, num b: `b.ljust(a)`
- str a, str b: `a.ljust(len(b)-len(a))`
- any a, fun b: `Collect values while values are unique (including the initial value)`
- fun a, any b: `Collect values while values are unique (including the initial value)`
-------------------------------
## `` ⋏ `` (Bitwise And)

Performs bitwise and between two numbers / centre a string

### Overloads

- num a, num b: `a & b`
- num a, str b: `b.center(a)`
- str a, num b: `a.center(b)`
- str a, str b: `a.center(len(b) - len(a))`
-------------------------------
## `` ⋎ `` (Bitwise Or)

Performs bitwise or between two numbers / Removes a character at nth index / Merges strings on longest common prefix and suffix

### Overloads

- num a, num b: `a | b`
- num a, str b: `b[:a]+b[a+1:]`
- str a, num b: `a[:b]+a[b+1:]`
- str a, str b: `merge_join(a,b)`
-------------------------------
## `` ꘍ `` (Bitwise Xor)

Performs bitwise xor between two numbers / appends n spaces to a string / prepends n characters to a string / Levenshtein Distance

### Overloads

- num a, num b: `a ^ b`
- num a, str b: `\" \" * a + b`
- str a, num b: `a + \" \" * b`
- str a, str b: `levenshtein_distance(a,b)`
-------------------------------
## `` ꜝ `` (Bitwise Not)

Performs bitwise not on a number / check if any letters are uppercase / keep only truthy elements of a list

### Overloads

- num a: `~a`
- str a: `any_upper(a)`
- lst a: `keep truthy`
-------------------------------
## `` ℅ `` (Random Choice)

Random choice of single item from array

### Overloads

- lst a: `random.choice(a)`
- num a: `Random integer from 1 to a`
-------------------------------
## `` ≤ `` (Lesser Than or Equal To)

a is lesser than or equal to b?

### Overloads

- any a, any b: `a <= b`
-------------------------------
## `` ≥ `` (Greater Than or Equal To)

a is greater than or equal to b?

### Overloads

- any a, any b: `a >= b`
-------------------------------
## `` ≠ `` (Not Equal To)

a is not equal to b?

### Overloads

- any a, any b: `a != b`
-------------------------------
## `` ⁼ `` (Exactly Equal To)

a equal to b? (non-vectorizing)

### Overloads

- any a, any b: `a == b`
-------------------------------
## `` ƒ `` (Reduce by)
Reduce by an element

Usage:
```
ƒ<element>
```

-------------------------------
## `` ɖ `` (Scan by)
Cumulatively reduce by an element

Usage:
```
ɖ<element>
```

-------------------------------
## `` ∪ `` (Set Union)

Merge two arrays without duplicates

### Overloads

- any a, any b: `list(set(a).union(set(b)))`
-------------------------------
## `` ∩ `` (Transpose)

Transpose an array

### Overloads

- any a: `transposed array`
-------------------------------
## `` ⊍ `` (Symmetric Set difference)

Uncommon elements of two arrays

### Overloads

- any a, any b: `list(set(a) ^ set(b))`
-------------------------------
## `` £ `` (Set Register)

Set the register to argument value

### Overloads

- any a: `set_register(a)`
-------------------------------
## `` ¥ `` (Push Register)

Push the current register value

-------------------------------
## `` ⇧ `` (Grade Up)

Indices of elements to sort in ascending order / uppercase / increment number twice

### Overloads

- lst a: `graded_up(a)`
- str a: `a.upper()`
- num a: `a + 2`
-------------------------------
## `` ⇩ `` (Grade Down)

Indices of elements to sort in descending order / lowercase / decrement number twice

### Overloads

- lst a: `graded_down(a)`
- str a: `a.lower()`
- num a: `a - 2`
-------------------------------
## `` Ǎ `` (Remove non-alphabets)

Remove non-alphabetical characters / power with base -1

### Overloads

- str a: `filter(isalpha, a)`
- num a: `(-1) ** a`
-------------------------------
## `` ǎ `` (Nth prime)

nth prime / all substrings

### Overloads

- str a: `substrings(a)`
- num a: `nth_prime(a)`
-------------------------------
## `` Ǐ `` (Prime factorization)

prime factorization / append first element

### Overloads

- num a: `prime_factorization(a) (distinct prime factors)`
- str a: `a + a[0]`
- lst a: `a + [a[0]]`
-------------------------------
## `` ǐ `` (Prime factors)

all prime factors / Title Case string

### Overloads

- num a: `prime_factors(a) (prime factors possibly with repetition)`
- str a: `title_case(a)`
-------------------------------
## `` Ǒ `` (Multiplicity / Remove Fixpoint / First Truthy Index Under Function)

Order, Multiplicity, Valuation / remove till fixpoint / First truthy index under function application

### Overloads

- num a, num b: `multiplicity(a,b)`
- str a, str b: `remove_till_fixpoint(a,b)`
- fun a, any b: `first index in a where b(x) is truthy (shortcut for ḟh)`
-------------------------------
## `` ǒ `` (Modulo 3)

Modulo 3 / Split into Length 2

### Overloads

- num a: `a % 3`
- str a: `a split into chunks of length 2`
-------------------------------
## `` Ǔ `` (Rotate Left)

Rotate Left / Rotate Left Once

### Overloads

- any a, num b: `rotate_left(a,b)`
- any a, any b: `a,(b[1:]+b[:1])`
-------------------------------
## `` ǔ `` (Rotate Right)

Rotate Right / Rotate Right Once

### Overloads

- any a, num b: `rotate_right(a,b)`
- any a, any b: `a,(b[-1:]+b[:-1])`
-------------------------------
## `` ⁽ `` (One Element Lambda)

One Element lambda function (prefix)

-------------------------------
## `` ‡ `` (Two Element Lambda)

Two Element lambda function (prefix)

-------------------------------
## `` ≬ `` (Three Element Lambda)

Three Element lambda function (prefix)

-------------------------------
## `` ⁺ `` (Index of next character in codepage)

Return the index of the next character in the vyxal code page + 101

-------------------------------
## `` ↵ `` (Split On newlines)

Split on newlines / Power with base 10

### Overloads

- str a: `a.split("\n")`
- num a: `10 ** a`
-------------------------------
## `` ⅛ `` (Push To Global Array)

Push to global array (no popping)

-------------------------------
## `` ¼ `` (Pop From Global Array)

Pop from global array, push to stack

-------------------------------
## `` ¾ `` (Push Global Array)

Push global array, no modification of global array

-------------------------------
## `` Π `` (Product of Array / Cartesian product over list)

Product of Array / Cartesian product over a list of lists

### Overloads

- num a: `binary representation of a (shortcut for bṅ)`
- lst[num] a: `reduce list by multiplication`
- lst[str|lst] a: `reduce list by Cartesian product`
-------------------------------
## `` „ `` (Rotate Stack Left)

Rotate Stack Left

-------------------------------
## `` ‟ `` (Rotate Stack Right)

Rotate Stack Right

-------------------------------
## `` 🍪 `` (Cookie)

print "cookie" forever

-------------------------------
## `` ඞ `` (sus)

print "sus"

-------------------------------
## `` kA `` (Uppercase alphabet)

"ABCDEFGHIJKLMNOPQRSTUVWXYZ" (uppercase alphabet)

-------------------------------
## `` ke `` (e, Euler's number)

2.718281828459045 (math.e, Euler's number)

-------------------------------
## `` kf `` (Fizz)

Fizz

-------------------------------
## `` kb `` (Buzz)

Buzz

-------------------------------
## `` kF `` (FizzBuzz)

FizzBuzz

-------------------------------
## `` kH `` (Hello, World!)

Hello, World!

-------------------------------
## `` kh `` (Hello World (No Punctuation))

Hello World

-------------------------------
## `` k1 `` (1000)

10^3 / 1000

-------------------------------
## `` k2 `` (10000)

10^4 / 10000

-------------------------------
## `` k3 `` (100000)

10^5 / 100000

-------------------------------
## `` k4 `` (1000000)

10^6 / 1000000

-------------------------------
## `` ka `` (Lowercase alphabet)

"abcdefghijklmnopqrstuvwxyz" (lowercase alphabet)

-------------------------------
## `` kL `` (Lowercase and uppercase alphabet)

"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" (uppercase+lowercase alphabet)

-------------------------------
## `` kd `` (Digits)

"0123456789" (Digits 0-9)

-------------------------------
## `` k6 `` (Hex digits (lowercase))

"0123456789abcdef" (Hex digits)

-------------------------------
## `` k^ `` (Hex digits (uppercase))

"0123456789ABCDEF" (Hex digits uppercase)

-------------------------------
## `` ko `` (Octal digits)

"01234567" (Octal digits)

-------------------------------
## `` kp `` (Punctuation)

string.punctuation (Punctuations)

-------------------------------
## `` kP `` (Printable ASCII Without Space)

printable ascii exluding space

-------------------------------
## `` kQ `` (Printable ASCII With Space)

printable ascii with space

-------------------------------
## `` kw `` (ASCII Whitespace)

All ASCII whitespace

-------------------------------
## `` kr `` (Digits, lowercase alphabet, and uppercase alphabet)

"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" (0-9A-Za-z)

-------------------------------
## `` kB `` (Uppercase and lowercase alphabet)

"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" (A-Za-z)

-------------------------------
## `` kZ `` (Uppercase alphabet reversed)

"ZYXWVUTSRQPONMLKJIHGFEDCBA" (uppercase alphabet reversed)

-------------------------------
## `` kz `` (Lowercase alphabet reversed)

"zyxwvutsrqponmlkjihgfedcba" (lowercase alphabet reversed)

-------------------------------
## `` kl `` (Uppercase and lowercase alphabet, reversed)

"ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba" (Z-Az-a)

-------------------------------
## `` ki `` (Pi)

3.141592653589793 (Pi)

-------------------------------
## `` kg `` (Golden ratio/phi)

1.618033988749895 (golden ratio/phi)

-------------------------------
## `` kD `` (Current day in the format YYYY-MM-DD)

Current day in the format YYYY-MM-DD

-------------------------------
## `` kN `` (Current time as a list of ⟨hh|mm|ss⟩)

Current time as a list of ⟨hh|mm|ss⟩

-------------------------------
## `` kḋ `` (Current day in the format DD/MM/YYYY)

Current day in the format DD/MM/YYYY

-------------------------------
## `` kḊ `` (Current day in the format MM/DD/YYYY)

Current day in the format MM/DD/YYYY

-------------------------------
## `` kð `` (Current day in the format ⟨DD|MM|YYYY⟩)

Current day in the format ⟨DD|MM|YYYY⟩

-------------------------------
## `` kβ `` (Braces, square brackets, angle brackets, and parentheses)

{}[]<>()

-------------------------------
## `` kḂ `` (Parentheses, square brackets, and braces)

"()[]{}" (Brackets)

-------------------------------
## `` kß `` (Parentheses and square brackets)

()[]

-------------------------------
## `` kḃ `` (Opening brackets)

"([{" (Open brackets)

-------------------------------
## `` k≥ `` (Closing brackets)

")]}" (Close brackets)

-------------------------------
## `` k≤ `` (Opening brackets (with <))

"([{<" (Fish bones :P)

-------------------------------
## `` kΠ `` (Closing brackets (with >))

")]}>" (Closing brackets)

-------------------------------
## `` kv `` (Lowercase vowels)

"aeiou" (Vowels lowercase)

-------------------------------
## `` kV `` (Upercase vowels)

"AEIOU" (Vowels uppercase)

-------------------------------
## `` k∨ `` (Lowercase and uppercase vowels)

"aeiouAEIOU" (vowelsVOWELS)

-------------------------------
## `` k⟇ `` (Vyxal codepage)

Yields the Vyxal codepage

-------------------------------
## `` k½ `` ([1, 2])

[1, 2]

-------------------------------
## `` kḭ `` (4294967296)

2 ** 32, 2^32, 4294967296

-------------------------------
## `` k+ `` ([1, -1])

[1, -1]

-------------------------------
## `` k- `` ([-1, 1])

[-1, 1]

-------------------------------
## `` k≈ `` ([0, 1])

[0, 1]

-------------------------------
## `` k/ `` (Slashes)

"/\\" (Forwardslash, backslash)

-------------------------------
## `` kR `` (360)

360

-------------------------------
## `` kW `` (https://)

https://

-------------------------------
## `` k℅ `` (http://)

http://

-------------------------------
## `` k↳ `` (https://www.)

https://www.

-------------------------------
## `` k² `` (http://www.)

http://www.

-------------------------------
## `` k¶ `` (512)

512

-------------------------------
## `` k⁋ `` (1024)

1024

-------------------------------
## `` k¦ `` (2048)

2048

-------------------------------
## `` kṄ `` (4096)

4096

-------------------------------
## `` kṅ `` (8192)

8192

-------------------------------
## `` k¡ `` (16384)

16384

-------------------------------
## `` kε `` (32768)

32768

-------------------------------
## `` k₴ `` (65536)

65536

-------------------------------
## `` k× `` (2147483648)

2147483648

-------------------------------
## `` k⁰ `` (Lowercase consonants with y)

bcdfghjklmnpqrstvwxyz

-------------------------------
## `` k¹ `` (Lowercase consonants without y)

bcdfghjklmnpqrstvwxz

-------------------------------
## `` kT `` (BF command set)

BF command set ("[]<>-+.,")

-------------------------------
## `` kṗ `` (Bracket pair list)

List of bracket pairs ("[(),[],{},<>]")

-------------------------------
## `` kṖ `` (Nested brackets)

String of all brackets nested ("([{<>}])")

-------------------------------
## `` kS `` (Amogus)

Amogus ("ඞ")

-------------------------------
## `` k₁ `` ([1, 1])

The list [1, 1]

-------------------------------
## `` k₂ `` (2 ** 20)

2 to the power of 20, 1048576

-------------------------------
## `` k₃ `` (2 ** 30)

2 to the power of 30, 1073741824

-------------------------------
## `` k∪ `` (Lowercase Vowels With Y)

Lowercase vowels with y, "aeiouy"

-------------------------------
## `` k⊍ `` (Uppercase Vowels With Y)

Uppercase vowels with y, "AEIOUY"

-------------------------------
## `` k∩ `` (Vowels With Y)

Vowels with y, "aeiouyAEIOUY"

-------------------------------
## `` k□ `` (Directions)

Cardinal directions, [[0,1],[1,0],[0,-1],[-1,0]]

-------------------------------
## `` kṘ `` (Roman Numerals)

IVXLCDM

-------------------------------
## `` k• `` (Qwerty Keyboard)

The list ["qwertyuiop","asdfghjkl","zxcvbnm"]

-------------------------------
## `` ∆b `` (Binary String)

Get a binary string of a number

### Overloads

- num a: `bin(a).replace("0b", "")`
-------------------------------
## `` ∆c `` (Cosine)

Get the cosine of an angle in radians

### Overloads

- num a: `math.cos(a)`
-------------------------------
## `` ∆C `` (Arc Cosine)

Get the arccosine of an angle in radians

### Overloads

- num a: `math.arrcos(a)`
-------------------------------
## `` ∆q `` (Quadratic Solver)

Solve a quadratic equation of the form ax^2 + bx = 0

### Overloads

- num a, num b: `x such that ax^2 + bx = 0`
- num a, str b: `solve for x such that a = b(x)`
- str a, num b: `solve for x such that a(x) = b`
- str a, str b: `solve for x such that a(x) = b(x)`
-------------------------------
## `` ∆Q `` (General Quadratic Solver)

Solve a quadratic equation of the form x^2 + ax + b = 0

### Overloads

- num a, num b: `roots(a, b) / x^2 + ax + b = 0`
- num a, str b: `evaluate single variable expression b with x=a`
- str a, num b: `evaluate single variable expression a with x=b`
- str a, str b: `solve equations a and b simultaneously for x and y`
-------------------------------
## `` ∆s `` (Sine)

Get the sine of an angle in radians

### Overloads

- num a: `math.sin(a)`
-------------------------------
## `` ∆S `` (Arc Sine)

Get the arcsine of an angle in radians

### Overloads

- num a: `math.arcsin(a)`
-------------------------------
## `` ∆t `` (Tangent)

Get the tangent of an angle in radians

### Overloads

- num a: `math.tan(a)`
-------------------------------
## `` ∆T `` (Arc Tangent)

Get the arctangent of an angle in radians

### Overloads

- num a: `math.arctan(a)`
-------------------------------
## `` ∆Ṫ `` (Arc Tangent 2)

Get the arctangent of an angle in radians

### Overloads

- num a, num b: `math.arctan2(a, b)`
-------------------------------
## `` ∆P `` (Polynomial Solver)

Solve a polynomial of the form a[0]x^len(a) + a[1]x^len(a)-1 ... = 0

### Overloads

- lst a: `roots(a)`
-------------------------------
## `` ∆ƈ `` (n Pick r (npr))

Get the number of combinations of r items from a set of n items

### Overloads

- num a, num b: `n_pick_r(a, b)`
- num a, str b: `n_pick_r(a, len(b))`
- str a, num b: `n_pick_r(len(a), b)`
- str a, str b: `n_pick_r(len(a), len(b))`
-------------------------------
## `` ∆± `` (Copy Sign)

Copy the sign of one number to the other

### Overloads

- num a, num b: `math.copysign(a, b)`
-------------------------------
## `` ∆K `` (Sum of Proper Divisors / Stationary Points)

Get the sum of all proper divisors of a number /  get the stationary points of a function

### Overloads

- num a: `sum_of_proper_divisors(a)`
- str a: `stationary_points(a)`
-------------------------------
## `` ∆² `` (Perfect Square? / Square Expression)

Is the number a perfect square? (1, 4, 9, 16, 25, 36) / Raise an algebraic expression to the power of 2

### Overloads

- num a: `is_perfect_square(a)`
- str a: `expr ** 2`
-------------------------------
## `` ∆e `` (Euler's Number (e) raised to power a)

Get the value of Euler's number (e) raised to the power of a

### Overloads

- num a: `e ** a`
- str a: `simplify expression a`
-------------------------------
## `` ∆E `` ((Euler's Number (e) Raised to Power a) - 1)

Get the value of Euler's number (e) raised to the power of a minus 1

### Overloads

- num a: `(e ** a) - 1`
- str a: `expand expression a`
-------------------------------
## `` ∆L `` (Natural Logarithm)

Get the natural logarithm of a number

### Overloads

- num a: `math.log(a)`
-------------------------------
## `` ∆l `` (Logarithm (log_2))

Get the logarithm of a number to base 2

### Overloads

- num a: `math.log2(a)`
-------------------------------
## `` ∆τ `` (Common Logarithm)

Get the common logarithm of a number

### Overloads

- num a: `math.log10(a)`
-------------------------------
## `` ∆d `` (Straight Line Distance)

Get the straight line distance between two points (x1, x2, ..., xn) and (y1, y2, ..., yn)

### Overloads

- lst a, lst b: `euclidean_distance(a, b)`
-------------------------------
## `` ∆D `` (To Degrees)

Convert an angle from radians to degrees

### Overloads

- num a: `math.degrees(a)`
-------------------------------
## `` ∆R `` (To Radians)

Convert an angle from degrees to radians

### Overloads

- num a: `math.radians(a)`
-------------------------------
## `` ∆Ṗ `` (Next Prime After a Number / Discriminant of Polynomial)

Get the next prime number after a given number / the discriminant of a polynomial

### Overloads

- num a: `next_prime(a)`
- str a: `discriminant(a)`
-------------------------------
## `` ∆ṗ `` (First Prime Before a Number / Factor Expression)

Get the first prime number before a given number / factor a mathematical expression

### Overloads

- num a: `prev_prime(a)`
- str a: `factorise(a)`
-------------------------------
## `` ∆p `` (Nearest Prime to a Number / Python equivalent of an expression)

Get the prime number closest to a given number, get the greater to break ties / return the python equivalent of a mathematical expression - sympy's .pycode() function

### Overloads

- num a: `nearest_prime(a)`
- str a: `sympy.nsimplify(a).pycode()`
-------------------------------
## `` ∆ṙ `` (Polynomial from Roots)

Get the polynomial with coefficients from the roots of a polynomial

### Overloads

- list a: `polynomial(a)`
-------------------------------
## `` ∆W `` (Round to n Decimal Places)

Round a number to n decimal places

### Overloads

- num a, num b: `round(a, no_dec_places=b) (b significant digits)`
-------------------------------
## `` ∆% `` (Modular Exponentiation)

Get the modular exponentiation a**b mod c

### Overloads

- any a, any b, any c: `pow(a, b, c)`
-------------------------------
## `` ∆Ŀ `` (Least Common Multiple)

Get the least common multiple of two numbers

### Overloads

- lst a: `lcm(a)`
- num a, num b: `lcm(a, b)`
-------------------------------
## `` ∆i `` (nth Digit of Pi / Integrate)

Get the nth digit of pi

### Overloads

- num a: `nth_digit_of_pi(a)`
- str a: `antiderivative of a`
-------------------------------
## `` ∆I `` (First N Digits of Pi)

Generate the first n digits of pi

### Overloads

- num a: `the first (a + 1)th digits of pi`
-------------------------------
## `` ∆Ė `` (N Digits of Euler's Number (e) / Sympy Evaluate)

Get the first n digits of Euler's number (e) / evaluate an expression as sympy

### Overloads

- num a: `first n digits of e`
- str a: `evaluate(a)`
-------------------------------
## `` ∆ė `` (Nth Digit of Euler's Number (e) / Differentiate)

Get the nth digit of Euler's number (e)

### Overloads

- num a: `nth_digit_of_e(a)`
- str a: `derivative(a)`
-------------------------------
## `` ∆f `` (nth Fibonacci Number)

Get the nth fibonacci number, 1-indexed

### Overloads

- num a: `nth_fibonacci(a) (0 -> 1, 1 -> 1, 2 -> 2, ...)`
-------------------------------
## `` ∆F `` (nth Fibonacci Number, 0-indexed)

Get the nth fibonacci number, 0-indexed

### Overloads

- num a: `nth_fibonacci(a) (0 -> 0, 1 -> 1, 2 -> 1, ...)`
-------------------------------
## `` ∆Ṙ `` (Random Float)

Get a random float in the range [0, 1), pseudo random number

### Overloads

- num a: `random.random()`
-------------------------------
## `` ∆ṫ `` (Totient Function / Local Minima)

Get the totient function of a number / local minima of a function

### Overloads

- num a: `totient(a)`
- str a: `local_minima(a)`
-------------------------------
## `` ∆n `` (Next Power)

Get the next power of b after a.

### Overloads

- num a, num b: `b ** floor(log(a, b) + 1)`
-------------------------------
## `` ∆ḟ `` (Previous Power)

Get the previous power of b before a.

### Overloads

- num a, num b: `b ** ceil(log(a, b) - 1)`
-------------------------------
## `` ∆Z `` (ZFill)

Pad a string with zeros to a given length

### Overloads

- str, num a: `zfill(a, b)`
-------------------------------
## `` ∆ċ `` (Nth Cardinal)

Get the nth cardinal / convert number to words

### Overloads

- num a: `num_to_words(a)`
-------------------------------
## `` ∆o `` (Nth Ordinal)

Get the nth ordinal / convert number to wordth ordinal

### Overloads

- num a: `num_to_ordinal(a)`
-------------------------------
## `` ∆M `` (Mode)

Get the mode of a list

### Overloads

- lst a: `mode(a)`
-------------------------------
## `` ∆ṁ `` (Median)

Get the median of a list - returns a list of the two middle items if even length list (use ṁ to average them)

### Overloads

- lst a: `median(a)`
-------------------------------
## `` ∆Ċ `` (Polynomial Expression From Coefficients)

Get the polynomial expression from a list of coefficients

### Overloads

- num a: `polynomial of degree n`
- str a: `a`
- lst a: `polynomial_expression(a)`
-------------------------------
## `` ∆¢ `` (Carmichael Function)

Get the Carmichael function of a number / Local Maxima

### Overloads

- num a: `carmichael(a)`
- str a: `local_maxima(a)`
-------------------------------
## `` ∆› `` (Increment until false)

Increment a until b(a) is false (deprecated, use `>` instead)

### Overloads

- any a, fun b: `while b(a): a += 1`
- fun a, any b: `while a(b): b += 1`
-------------------------------
## `` ∆‹ `` (Decrement until false)

Decrement a until b(a) is false (deprecated, use `<` instead)

### Overloads

- any a, fun b: `while b(a): a -= 1`
- fun a, any b: `while a(b): b -= 1`
-------------------------------
## `` ∆ǐ `` (Prime Exponents)

Get the exponents of prime factors of a number

### Overloads

- num a: `prime_exponents(a) (in the order of prime_factors(a))`
-------------------------------
## `` ∆Ǐ `` (All Prime Exponents)

Get all exponents of prime factors less than the maximum prime factor

### Overloads

- num a: `prime_exponents(a) (includes 0s)`
-------------------------------
## `` ∆* `` (Next Multiple)

Get the next multiple of a number greater than another number

### Overloads

- num, num a: `get the next multiple of b that is greater than a`
-------------------------------
## `` ∆ȯ `` (Hyperbolic Cosine)

Get the hyperbolic cosine of a number in radians

### Overloads

- num a: `cosh(a)`
-------------------------------
## `` ∆Ȯ `` (Hyperbolic Arccosine)

Get the hyperbolic arccosine of a number in radians

### Overloads

- num a: `acosh(a)`
-------------------------------
## `` ∆ṡ `` (Hyperbolic Sine)

Get the hyperbolic sine of a number in radians

### Overloads

- num a: `sinh(a)`
-------------------------------
## `` ∆Ṡ `` (Hyperbolic Arcsine)

Get the hyperbolic arcsine of a number in radians

### Overloads

- num a: `asinh(a)`
-------------------------------
## `` ∆ṅ `` (Hyperbolic Tangent)

Get the hyperbolic tangent of a number in radians

### Overloads

- num a: `tanh(a)`
-------------------------------
## `` ∆Ṅ `` (Hyperbolic Arctangent)

Get the hyperbolic arctangent of a number in radians

### Overloads

- num a: `atanh(a)`
-------------------------------
## `` ∆/ `` (Hypotenuse)

Get the hypotenuse of a right-angled triangle - equivalent to `²∑√`

### Overloads

- lst a: `sqrt(sum(x ** 2 for x in a))`
-------------------------------
## `` ∆r `` (Reduced Echelon Form)

Get the reduced echelon form of a matrix

### Overloads

- lst a: `reduced_echelon_form(a)`
-------------------------------
## `` øb `` (Parenthesise)

Parenthesise a string

### Overloads

- any a: `"("" + a + ")"`
-------------------------------
## `` øB `` (Bracketify)

Enclose a string in brackets

### Overloads

- any a: `"["" + a + "]"`
-------------------------------
## `` øḃ `` (Curly Bracketify)

Enclose a string in curly brackets

### Overloads

- any a: `"{"" + a + "}"`
-------------------------------
## `` øḂ `` (Angle Bracketify)

Enclose a string in angle brackets

### Overloads

- any a: `"<"" + a + ">"`
-------------------------------
## `` øβ `` (Balanced Brackets)

Check if brackets in a string ("{}()[]<>") are balanced

### Overloads

- any a: `balanced_brackets(a)`
-------------------------------
## `` ø↳ `` (Custom Pad Left)

Pad a string to the left with a certain character

### Overloads

- any a, str b, num c: `pad a to the left with c so a has length b`
- any a, num b, str c: `pad a to the left with b so a has length c`
-------------------------------
## `` ø↲ `` (Custom Pad Right)

Pad a string to the right with a certain character

### Overloads

- any a, str b, num c: `pad a to the right with c so a has length b`
- any a, num b, str c: `pad a to the right with b so a has length c`
-------------------------------
## `` øM `` (Flip Brackets Vertical Palindromise)

Vertically palindromise and reverse brackets and slashes, without duplicating center

### Overloads

- any a: `palindromise, without duplicating center, and flip brackets and slashes in the second half`
-------------------------------
## `` øA `` (Letter to Number)

Convert a letter to a number, or vice versa (1-indexed)

### Overloads

- str a: `number_to_letter(a)`
- num a: `letter_to_number(a)`
-------------------------------
## `` øṗ `` (Flip Brackets Vertical Palindromise, Center, Join on Newlines)

Vertically palindromise each and reverse brackets and slashes, without duplicating center, then center and join by newlines. Equivalent to `øMøĊ⁋`

### Overloads

- any a: `palindromise each, without duplicating center, flip brackets and slashes in the second half, center by padding with spaces, and join by newlines`
-------------------------------
## `` øm `` (Flip Brackets Vertical Mirror, Center, Join on Newlines)

Vertically mirror each and reverse brackets and slashes, then center and join by newlines. Equivalent to `øṀøĊ⁋`

### Overloads

- any a: `mirror each, flip brackets and slashes in the second half, center by padding with spaces, and join by newlines`
-------------------------------
## `` øo `` (Remove Until No change)

Remove b from a until a does not change

### Overloads

- str a, str b: `remove b from a until a does not change`
- str a, lst b: `remove everything in b (in order) from a until a does not change`
-------------------------------
## `` øO `` (Count Overlapping)

Count the number of overlapping occurances of b in a

### Overloads

- any a, any b: `Count the number of overlapping occurances of b in a`
-------------------------------
## `` øV `` (Replace Until No Change)

Replace b with c in a until a does not change

### Overloads

- str a, str b, str c: `a.replace_until_no_change(b,c)`
-------------------------------
## `` øc `` (String Compress)

Compress a string of lowercase letters and spaces in base 255

### Overloads

- str a: `base_255_string_compress(a)`
-------------------------------
## `` øC `` (Number Compress)

Compress a positive integer in base 255

### Overloads

- num a: `base_255_number_compress(a)`
-------------------------------
## `` ø% `` (SHA256 Hash)

Hash a string using the SHA256 hash algorithm.

-------------------------------
## `` øĊ `` (Center)

Center a list of strings

### Overloads

- lst a: `center(a) (pad each item with spaces so all are the same length and centered)`
-------------------------------
## `` øe `` (Run Length Encoding)

Run length encoding, convert from string/list to list of items and amount repeated.

### Overloads

- str a: `run_length_encoded(a)`
-------------------------------
## `` øĖ `` (Separated Run Length Encoding)

Run length encoding, convert from string/list to list of items and list of amounts. Equivalent to `øe∩÷`

### Overloads

- str a: `run length encode a and push items and lengths`
-------------------------------
## `` ød `` (Run Length Decoding)

Run length decoding, convert from list of characters and lengths to a string/list

### Overloads

- lst a: `run_length_decoded(a)`
-------------------------------
## `` øḊ `` (Dyadic Run Length Decode)

Run length decoding, convert list of characters and list of lengths to a string/list

### Overloads

- lst a, lst b: `run length decode with items a and lengths b`
-------------------------------
## `` øD `` (Dictionary Compression)

Optimally compress a string of English using words from the Vyxal dictionary

### Overloads

- str a: `dictionary_compressed(a)`
-------------------------------
## `` øW `` (Group on words)

Group a string on words

### Overloads

- str a: `Group a on words, leaving chunks of [a-zA-Z] together and having everything else as a single character`
-------------------------------
## `` øċ `` (Semi Optimal number compress)

Semi-optimally compress a number

### Overloads

- num a: `optimal_number_compress(a)`
-------------------------------
## `` øṙ `` (Regex replace)

Replace matches of a with c in b

### Overloads

- any a, any b, fun c: `apply c to matches of a in b`
- any a, any b, any c: `replace matches of a with c in b`
-------------------------------
## `` øp `` (Starts With)

Check if one value starts with another

### Overloads

- any a, any b: `a.startswith(b) (Starts with b?)`
-------------------------------
## `` øE `` (Ends With)

Check if one value ends with another

### Overloads

- any a, any b: `a.endswith(b) (ends with b?)`
-------------------------------
## `` øf `` (Ends With Set)

Check if a value ends with others

### Overloads

- any a, any b: `does a end with all of b?`
-------------------------------
## `` øs `` (Starts With Set)

Check if a value starts with others

### Overloads

- any a, any b: `does a start with all of b?`
-------------------------------
## `` øP `` (Pluralise Count)

Create a sentence of the form 'a bs'

### Overloads

- num a, str b: `a + " " + b + (s if a != 1 else "") (concatenate with space, append a s if not 1)`
-------------------------------
## `` øṁ `` (Vertical Mirror)

Vertical Mirror - Split by newlines, mirror each line, join by newlines

### Overloads

- str a: `vertical_mirror(a)`
-------------------------------
## `` øṀ `` (Flip Brackets Vertical Mirror)

Vertical mirror, and swap brackets and slashes in the second half.

### Overloads

- any a: `vertical_mirror(a, mapping = flip brackets and slashes)`
-------------------------------
## `` øṖ `` (String Partitions)

All partitions of a string/list

### Overloads

- any a: `all_partitions(a)`
-------------------------------
## `` øḋ `` (To Decimal)

Convert a rational to its decimal representation.

### Overloads

- num a: `to_decimal(a)`
- str a: `decimal representation of interpreting lhs as a fraction`
-------------------------------
## `` ø⟇ `` (Get Codepage Character / Get Codepage Index)

Get the character at a certain index in the vyxal codepage / Get the index of a character in the vyxal codepage

### Overloads

- num a: `vyxal_codepage[a]`
- str a: `vyxal_codepage.index(a)`
-------------------------------
## `` øṘ `` (Roman Numeral)

Convert a decimal to its roman numeral representation / Convert a roman numeral to its decimal representation.

### Overloads

- num a: `to_roman_numeral(a)`
- str a: `from_roman_numeral(a)`
-------------------------------
## `` øJ `` (Parse JSON)

Parse a JSON string into a Vyxal object

### Overloads

- str a: `json.loads(a)`
-------------------------------
## `` øḞ `` (Replace First Occurrence)

Replace the first instance of an item with another item

### Overloads

- any a, any b, any c: `a.replace(b, c, count=1). See "V" (Replace) for specifics.`
-------------------------------
## `` øṄ `` (Replace Nth Occurrence)

Replace the nth instance of an item with another item. If n is negative, then replaces the last nth instance.

### Overloads

- any a, any b, any c, any d: `a.replace_nth_occurrence(b, c, d)`
-------------------------------
## `` øS `` (Strip whitespace from both sides)

Strip whitespace from both sides of a string / Remove trailing zeros from a number

### Overloads

- str a: `a.strip()`
- num a: `remove trailing zeros`
-------------------------------
## `` øL `` (Strip whitespace from the left side)

Strip whitespace from the left side of a string

### Overloads

- str a: `a.lstrip()`
-------------------------------
## `` øR `` (Strip whitespace from the right side)

Strip whitespace from the right side of a string

### Overloads

- str a: `a.rstrip()`
-------------------------------
## `` øl `` (Strip from the left side)

Strip from the left side of a string

### Overloads

- str a, num b: `a.lstrip(b)`
-------------------------------
## `` ør `` (Strip from the right side)

Strip from the right side of a string

### Overloads

- str a, num b: `a.rstrip(b)`
-------------------------------
## `` ø^ `` (Canvas Draw)

Draw on a canvas (see knowledge/spec/canvas.md for more details) and return it as a string

### Overloads

- num a, lst b, str c: `draw with a = length, b = dirs, c = text`
- num a, str b, str c: `draw with a = length, b/c dependent on dir validity`
- any a, num b, any c: `draw with b = length ^`
- any a, any b, num c: `draw with c = length ^`
- str a, any b, any c: `draw with a = text, b/c dependent on dir validity`
- lst a, str b, any c: `draw with b = text, ^`
- lst a, lst b, str c: `draw with c = text, ^`
-------------------------------
## `` ø∧ `` (Global Canvas Draw)

Draw on the global canvas (see knowledge/spec/canvas.md for more details), which is implicitly printed.

### Overloads

- num a, lst b, str c: `draw with a = length, b = dirs, c = text`
- num a, str b, str c: `draw with a = length, b/c dependent on dir validity`
- any a, num b, any c: `draw with b = length ^`
- any a, any b, num c: `draw with c = length ^`
- str a, any b, any c: `draw with a = text, b/c dependent on dir validity`
- lst a, str b, any c: `draw with b = text, ^`
- lst a, lst b, str c: `draw with c = text, ^`
-------------------------------
## `` ø. `` (Surround)

Surround a value with another

### Overloads

- str a, str b: `a.surround(b)`
- lst a, any b: `a.surround(b)`
- any a, lst b: `b.surround(a)`
-------------------------------
## `` øŀ `` (Left Align)

Left align a string/string list

### Overloads

- str a: `justify to left`
- lst a: `justify each to left`
-------------------------------
## `` øɽ `` (Right Align)

Right align a string/string list

### Overloads

- str a: `justify to right`
- lst a: `justify each to right`
-------------------------------
## `` Þ* `` (Cartesian product over list)

Cartesian product over a list of lists

### Overloads

- lst a: `itertools.product(*a)`
-------------------------------
## `` Þa `` (Adjacency matrix (Directed))

Adjacency matrix of directed graph (nonzero A_ij denotes edge from i to j)

### Overloads

- lst a: `adjacency matrix of directed graph (where a = [[i, j] for each edge i to j])`
-------------------------------
## `` Þn `` (Infinite list of all integers)

All integers in an infinite list (0, 1, -1, 2, -2, ...)

-------------------------------
## `` Þż `` (Lift)

Multiply a numeric list by a range from 1 to its length

### Overloads

- lst a: `lift`
-------------------------------
## `` ÞŻ `` (Sort Every Level)

Sort every level of a multidimensional list

### Overloads

- lst a: `sort every level`
-------------------------------
## `` ÞA `` (Adjacency matrix (Undirected))

Adjacency matrix of undirected graph

### Overloads

- lst a: `adjacency matrix of undirected graph (where a = [[i, j] for each edge i to j])`
-------------------------------
## `` Þo `` (Ordinals)

An infinite list of first, second, third, fourth etc

-------------------------------
## `` Þc `` (Cardinals)

An infinite list of one, two, three, four etc

-------------------------------
## `` Þp `` (Primes)

An infinite list of primes

-------------------------------
## `` Þu `` (All Unique)

Are all elements of a list/string unique?

### Overloads

- any a: `all_unique(a)`
-------------------------------
## `` Þj `` (Depth)

Depth of ragged list

### Overloads

- lst a: `Depth`
-------------------------------
## `` ÞẊ `` (Cartesian Power)

Cartesian power, cartesian product with self n times. If both arguments are numbers, turns the left into a range.


### Overloads

- any a, num b: `cartesian_power(a, b)`
- num a, any b: `cartesian_power(b, a)`
-------------------------------
## `` Þf `` (Flatten By depth)

Flatten a list by a certain depth (default 1)

### Overloads

- lst a, num b: `flatten a by depth b`
- any a, lst b: `a, flatten b by depth 1`
-------------------------------
## `` ÞB `` (Random Bits)

Fill a list with random bits

### Overloads

- num a: `list of length a filled with random bits`
- any a: `list of length n(a) filled with random bits`
-------------------------------
## `` Þ< `` (All Less Than Increasing)

Find all numbers less than a certain value in a (potentially infinite) list assumed to be (non-strictly) increasing

### Overloads

- any a, num b: `all values of a up to (not including) the first greater than or equal to b`
-------------------------------
## `` Þǔ `` (Untruth)

Return a list with 1s at the (0-indexed) indices in a, and 0s elsewhere

### Overloads

- any a: `[int(x in a) for x in range(max(a))]`
-------------------------------
## `` ÞǓ `` (Connected Uniquify)

Remove occurences of adjacent duplicates in a list

### Overloads

- any a: `connected uniquify a (`Ġvh`)`
-------------------------------
## `` Þk `` (2-dimensional Convolution)

Return two-dimensional convolution of matrices

### Overloads

- lst a, lst b: `2D-Convolution of a and b`
-------------------------------
## `` Þƈ `` (Jelly-style Convolution)

Return two-dimensional convolution of matrices, in the style of Jelly's æc

### Overloads

- lst a, lst b: `2D-Convolution of a and b`
-------------------------------
## `` Þi `` (Multidimensional Indexing)

Index a list of coordinates into a value.

### Overloads

- lst a, lst b: `reduce by indexing with a as initial value (a[b[0]][b[1]][b[2]]...)`
-------------------------------
## `` ÞI `` (All Indices (Multidimensional))

All multidimensional indices of element in list

### Overloads

- lst a, any b: `all indices of b in a`
- any a, lst b: `all indices of a in b`
- any a, any b: `all indices of b in a`
-------------------------------
## `` Þḟ `` (Multidimensional Search)

Find the first multidimensional index of a value in another

### Overloads

- lst a, any b: `find the first occurrence of a in b and return as a multidimensional index`
-------------------------------
## `` ÞḞ `` (Fill to make rectangular)

Fill a 2-D list to make it rectangular

### Overloads

- lst a, any b: `fill a with b to make it rectangular`
- any a, lst b: `fill b with a to make it rectangular`
-------------------------------
## `` Þm `` (Zero Matrix)

Given a list of dimensions, create a matrix with those dimensions, filled with zeroes

### Overloads

- lst a: `matrix with dimensions each item of a, where the first is the innermost and the last is the outermost`
-------------------------------
## `` ÞṄ `` (Infinite Integer Partitions)

Infinite list of sets of positive integers (equivalent to Þ∞vṄÞf)

-------------------------------
## `` Þ÷ `` (Divide List Into N Equal Length Parts)

Divide a list into n equal length parts

### Overloads

- any a, num b: `divide a into b equal length parts`
- num a, any b: `divide b into a equal length parts`
-------------------------------
## `` ÞZ `` (Fill By Coordinates)

Fill a matrix by calling a function with the lists of coordinates in the matrix.

### Overloads

- any a, fun b: `for each value of a (all the way down) call b with the coordinates of that value and put that at the appropriate position in a`
-------------------------------
## `` Þ… `` (Evenly Distribute)

Evenly distribute a number over elements of a list

### Overloads

- list a, num b: `[i + b // len(a) for i in a], with any excess added to the last element, such that the sum of the list increases by b`
-------------------------------
## `` Þ↓ `` (Minimum By Function)

Find the minimum value of a list by applying a function to each element

### Overloads

- lst a, fun b: `minimum value of a by applying b to each element`
-------------------------------
## `` Þ↑ `` (Maximum By Function)

Find the maximum value of a list by applying a function to each element

### Overloads

- lst a, fun b: `maximum value of a by applying b to each element`
-------------------------------
## `` Þ× `` (All Combinations)

All combinations of a list / string, of all lengths, with replacement

### Overloads

- any a: `all (non-empty) combinations of a, of all lengths and all orders, with replacement`
-------------------------------
## `` Þx `` (All Combinations Without Replacement)

All combinations of a list / string, of all lengths, without replacement

### Overloads

- any a: `all (non-empty) combinations of a, of all lengths and all orders, without replacement`
-------------------------------
## `` ÞF `` (All Fibonacci)

All Fibonacci numbers as a LazyList.

-------------------------------
## `` Þ! `` (All Factorials)

All factorials as a LazyList.

-------------------------------
## `` ÞU `` (Uniquify Mask)

A list of booleans describing which elements of a will remain after uniquifying.

### Overloads

- any a: `a list of booleans describing which elements of a will remain after uniquifying`
-------------------------------
## `` ÞD `` (Diagonals)

Diagonals of a matrix, starting with the main diagonal.

### Overloads

- lst a: `diagonals of a, starting with the main diagonal`
-------------------------------
## `` Þ√ `` (Diagonals Ordered)

Diagonals of a matrix, starting with the shortest top diagonal

### Overloads

- lst a: `diagonals of a, starting with the shortest top diagonal`
-------------------------------
## `` Þḋ `` (Anti-diagonals)

Anti-diagonals of a matrix, starting with the main anti-diagonal.

### Overloads

- lst a: `anti-diagonals of a, starting with the main anti-diagonal`
-------------------------------
## `` Þ` `` (Anti-diagonals Ordered)

Anti-diagonals of a matrix, starting with the shortest top anti-diagonal

### Overloads

- lst a: `anti-diagonals of a, starting with the shortest top anti-diagonal`
-------------------------------
## `` ÞS `` (Sublists)

Sublists of a list.

### Overloads

- lst a: `non-empty sublists of a`
-------------------------------
## `` ÞṪ `` (Transpose With Filler)

Transpose a matrix, with a filler value for empty cells.

### Overloads

- lst a, any b: `transpose a, with filler value b`
-------------------------------
## `` Þ℅ `` (Random Permutation)

Random permutation of a list / string

### Overloads

- any a: `random permutation of a`
-------------------------------
## `` ÞṀ `` (Matrix Multiplication)

Multiply two matrices together.

### Overloads

- lst a, lst b: `matrix multiply a and b`
-------------------------------
## `` ÞḊ `` (Matrix Determinant)

Calculate the determinant of a matrix.

### Overloads

- lst a: `determinant(a)`
-------------------------------
## `` Þ\ `` (Anti-diagonal)

Anti-diagonal of a matrix

### Overloads

- lst a: `antidiagonal(a)`
-------------------------------
## `` Þ/ `` (Main Diagonal)

Diagonal of a matrix

### Overloads

- lst a: `diagonal(a)`
-------------------------------
## `` ÞC `` (Matrix Column Reduce)

Reduce columns of a matrix by a function.

### Overloads

- lst a, fun b: `reduce columns of a with b`
-------------------------------
## `` ÞĠ `` (Gridify)

Gridify a 2-D list by padding each element with space to make columns aligned, joining each row on spaces, then joining by newlines.

### Overloads

- lst a: `gridify a`
-------------------------------
## `` Þ∨ `` (Multiset Difference)

Similar to set difference, but with duplicates allowed.

### Overloads

- lst a, lst b: `multiset difference of a and b`
-------------------------------
## `` Þ∩ `` (Multiset Intersection)

Similar to set intersection, but with duplicates allowed.

### Overloads

- lst a, lst b: `multiset intersection of a and b`
-------------------------------
## `` Þ∪ `` (Multiset Union)

Similar to set union, but with duplicates allowed.

### Overloads

- lst a, lst b: `multiset union of a and b`
-------------------------------
## `` Þḭ `` (Non-Modular Index)

Get the nth item of a list, but without modular arithmetic. Negative indices are allowed.

### Overloads

- lst a, int b: `nth item of a list (errors if out of bounds)`
-------------------------------
## `` Þ⊍ `` (Multiset Symmetric Difference)

Similar to set symmetric difference, but with duplicates allowed.

### Overloads

- lst a, lst b: `multiset symmetric difference of a and b`
-------------------------------
## `` Þ• `` (Dot Product)

Dot product of two lists.

### Overloads

- lst a, lst b: `dot product of a and b`
-------------------------------
## `` Þṁ `` (Mold without repeat)

Mold a list without repeating elements.

### Overloads

- lst a, lst b: `mold a list without repeating elements`
-------------------------------
## `` ÞM `` (Maximal Indices)

Indices of the maximal elements of a list.

### Overloads

- lst a: `indices of the maximal elements of a`
-------------------------------
## `` Þ∴ `` (Elementwise Vectorised Dyadic Maximum)

Elementwise vectorised dyadic maximum.

### Overloads

- lst a, lst b: `[max(a[0], b[0]), max(a[1], b[1]), ...]`
-------------------------------
## `` Þ∵ `` (Elementwise Vectorised Dyadic Minimum)

Elementwise vectorised dyadic minimum.

### Overloads

- lst a, lst b: `[min(a[0], b[0]), min(a[1], b[1]), ...]`
-------------------------------
## `` Þs `` (All Slices of a List)

Get all slices of a list, skipping a certain number of items

### Overloads

- lst a, int b: `[a[::b], a[1::b], a[2::b], ...]`
- int a, lst b: `[b[::a], b[1::a], b[2::a], ...]`
-------------------------------
## `` Þ¾ `` (Empty the Global Array)

Empty the global array.

-------------------------------
## `` Þr `` (Remove Last Item and Prepend 0)

Remove the last item of a list and prepend 0. A shortcut for Ṫ0p

### Overloads

- lst a: `[0] + a[:-1]`
-------------------------------
## `` Þ∞ `` (Infinite List of Positive Integers)

An infinite list of positive integers

-------------------------------
## `` Þ: `` (Infinite List of Non-Negative Integers)

An infinite list of non-negative integers

-------------------------------
## `` ÞR `` (Remove Last Item From Cumulative Sums and Prepend 0)

Remove the last item of the cumulative sums of a list and prepend 0. A shortcut for ¦Ṫ0p

### Overloads

- lst a: `[0, a[0], a[0]+a[1], ..., a[0]+a[1]+...+a[-2]]`
-------------------------------
## `` Þẇ `` (Unwrap)

Take a and push a[0]+a[-1] and a[1:-1]

### Overloads

- lst a: `a[0]+a[-1], a[1:-1]`
-------------------------------
## `` Þg `` (Shortest By Length)

Return the shortest item in a list.

### Overloads

- lst a: `the shortest item of a`
-------------------------------
## `` ÞG `` (Longest By Length)

Return the longest item in a list.

### Overloads

- lst a: `the longest item of a`
-------------------------------
## `` Þṡ `` (Sort By Length)

Sort a list by length.

### Overloads

- lst a: `sort a from shortest to longest`
-------------------------------
## `` ÞṠ `` (Is Sorted?)

Returns true if an item is sorted in ascending order using default sorting rules.

### Overloads

- lst a: `is a sorted in increasing order?`
-------------------------------
## `` ÞṘ `` (Is Sorted in Reverse?)

Returns true if an item is sorted in descending order using default sorting rules.

### Overloads

- lst a: `is a sorted in decreasing order?`
-------------------------------
## `` ÞȮ `` (Is Ordered?)

Returns true if the item is sorted in either descending or ascending order.

### Overloads

- lst a: `is a sorted in increasing or decreasing order?`
-------------------------------
## `` ÞĊ `` (Is Unordered?)

Returns true if the item is not sorted in either descending or ascending order.

### Overloads

- lst a: `is a not sorted, in either increasing or decreasing order?`
-------------------------------
## `` Þ⇧ `` (Is Strictly Ascending?)

Returns true if the list is in strictly ascending order.

### Overloads

- lst a: `is a in strictly ascending order?`
-------------------------------
## `` Þ⇩ `` (Is Strictly Descending?)

Returns true if the list is in strictly descending order.

### Overloads

- lst a: `is a in strictly descending order?`
-------------------------------
## `` Þċ `` (Cycle)

Form an infinite list from a vector.

### Overloads

- lst a: `[a[0], a[1], ..., a[-1], a[0], a[1], ..., a[-1], a[0], ...]`
-------------------------------
## `` ÞK `` (Suffixes)

Suffixes of a list.

### Overloads

- lst a: `[a, a[:-1], a[:-2], ..., a[:1]]`
-------------------------------
## `` ÞT `` (Multi-dimensional truthy indices)

Multi-dimensional indices of truthy elements

### Overloads

- lst a: `Multi-dimensional indices of truthy elements in a`
-------------------------------
## `` Þİ `` (First n Items and Rest)

Push the first n items of a, then the rest of a

### Overloads

- lst a, int b: `a[:b], a[b:]`
- int a, lst b: `b[:a], b[a:]`
-------------------------------
## `` ÞN `` (Alternating Negation)

An infinite list of an item, then that item negated, then that item, and so on. Uses the negation element for negation.

### Overloads

- any a: `[a, -a, a, -a, ...]`
-------------------------------
## `` Þ□ `` (Identity Matrix of Size n)

A matrix with 1s on the main diagonal and zeroes elsewhere

### Overloads

- num a: `the a x a identity matrix`
-------------------------------
## `` Þe `` (Matrix Exponentiation)

A matrix multiplied by itself n times

### Overloads

- lst a, num b: `a ** b (matrix exponentiation)`
- num a, lst b: `b ** a (matrix exponentiation)`
-------------------------------
## `` Þd `` (Distance matrix (Directed))

Distance matrix of directed graph

### Overloads

- lst a: `distance matrix of a directed graph (where a = [[i, j] for each edge i to j])`
-------------------------------
## `` Þw `` (Distance matrix (Undirected))

Distance matrix of undirected graph

### Overloads

- lst a: `distance matrix of an undirected graph (where a = [[i, j] for each edge i to j])`
-------------------------------
## `` ÞṖ `` (Split Before Indices)

Split a list before indices in another list

### Overloads

- lst a, lst b: `Split a list before indices in another list`
-------------------------------
## `` Þṗ `` (Split on Truthy Indices)

Split a list on truthy indices / Partition a list on truthy items

### Overloads

- lst a, lst b: `Split a on truthy indices in b`
-------------------------------
## `` Þẏ `` (Multidimensonal Indices)

A list of indices for a multidimensional list

### Overloads

- lst a: `A list of indices for a multidimensional list`
-------------------------------
## `` Þė `` (Multidimensonal Enumeration)

Enumerate a list and all its sublists

### Overloads

- lst a: `Enumerate a list and all its sublists`
-------------------------------
## `` Þz `` (Group Indices)

Group indices by their corresponding values

### Overloads

- any a: `Group indices of identical items. Like Ġ in Jelly`
-------------------------------
## `` Þ‟ `` (List from Anti-Diagonals)

List of lists from anti-diagonals of a list

### Overloads

- lst a: `List of lists from anti-diagonals of a list`
-------------------------------
## `` Þ„ `` (List from Diagonals)

List of lists from diagonals of a list

### Overloads

- lst a: `List of lists from diagonals of a list`
-------------------------------
## `` ¨□ `` (Parse direction arrow to integer)

Map characters in `>^<v` to integers (0, 1, 2, 3 respectively)

### Overloads

- str a: `map on a, replacing `>^<v` with integers, and others with -1 ([`>^<v`.find(a[0]), `>^<v`.find(a[1]), ...])`
-------------------------------
## `` ¨^ `` (Parse direction arrow to vector)

Map characters in `>^<v` to direction vectors

### Overloads

- str a: `map on a, replacing `>^<v` with [1, 0], [0, 1], etc., and others with [0, 0]`
-------------------------------
## `` ¨U `` (Get Request)

Send a GET request to a URL

### Overloads

- str a: `send a GET request to a`
-------------------------------
## `` ¨= `` (Invariant After Application)
Push whether the result of applying an element to an item is the same as the original item

Usage:
```
¨=<element>
```

-------------------------------
## `` ¨M `` (Map At Indices)

Map a function at elements of a list whose indices are in another list

### Overloads

- lst a, lst b, fun c: `change the items in a with indices in by applying function c`
- lst a, num b, fun c: `change the bth item in a by applying function c`
-------------------------------
## `` ¨, `` (Print With Space)

Print a value with a space after it

### Overloads

- any a: `print a followed by a space`
-------------------------------
## `` ¨… `` (Print With Space Without Popping)

Print a value with a space after it, without popping it

### Overloads

- any a: `print a followed by a space, then push a`
-------------------------------
## `` ¨> `` (Strict Greater Than)

Non-vectorising greater than - useful for lists. Note that all corresponding elements should be of the same type.

### Overloads

- any a, any b: `Non-vectorising greater than - useful for lists`
-------------------------------
## `` ¨≥ `` (Strict Greater Than or Equal To)

Non-vectorising greater than or equal to - useful for lists. Note that all corresponding elements should be of the same type.

### Overloads

- any a, any b: `Non-vectorising greater than or equal to - useful for lists`
-------------------------------
## `` ¨< `` (Strict Less Than)

Non-vectorising greater than - useful for lists. Note that all corresponding elements should be of the same type.

### Overloads

- any a, any b: `a > b (non-vectorising)`
-------------------------------
## `` ¨≤ `` (Strict Less Than or Equal To)

Non-vectorising greater than or equal to - useful for lists. Note that all corresponding elements should be of the same type.

### Overloads

- any a, any b: `Non-vectorising greater than or equal to - useful for lists`
-------------------------------
## `` ¨* `` (All Multiples)

Return all multiples of a

### Overloads

- num a: `[a*1, a*2, a*3, a*4, ...]`
- str a: `[a*1, a*2, a*3, a*4, ...]`
-------------------------------
## `` ¨e `` (All Powers)

Return all powers of a

### Overloads

- num a: `[a**1, a**2, a**3, a**4, ...]`
- str a: `[a**1, a**2, a**3, a**4, ...]`
-------------------------------
## `` ¨² `` (All Powers of 2)

Return all powers of 2

### Overloads

- none a: `[2**1, 2**2, 2**3, 2**4, ...]`
-------------------------------
## `` ¨₀ `` (All Powers of 10)

Return all powers of 10

### Overloads

- none a: `[10**1, 10**2, 10**3, 10**4, ...]`
-------------------------------
## `` ¨£ `` (Star Map)
Reduce each pair of two lists zipped together by a function. Equivalent to Zvƒ

Usage:
```
¨£<element>
```

-------------------------------
## `` ¨ẇ `` (Wrap Last n Items)

Wrap the last n items on the stack into a list

### Overloads

- num a: `last a items of the stack, as a list; does not pop anything other than a`
-------------------------------
## `` ¨2 `` (Dyadic Map Lambda)

Open a dyadic mapping lambda - ¨2...; Receives item and index.

-------------------------------
## `` ¨3 `` (Triadic Map Lambda)

Open a triadic mapping lambda - ¨3...; Receives item, index, and vector.

-------------------------------
## `` ¨₂ `` (Dyadic Filter Lambda)

Open a dyadic filter lambda - ¨₂...; Receives item and index.

-------------------------------
## `` ¨₃ `` (Triadic Filter Lambda)

Open a triadic filter lambda - ¨₃...; Receives item, index, and vector.

-------------------------------
## `` ¨Z `` (Zip lambda)

Open a zip lambda - ¨Z...; Pops top two items off stack, zips them, and loops over them, pushing each item to the stack. Equivalent to `Zƛ÷...;`.

-------------------------------
## `` ¨p `` (For Each Overlapping Pair)
Run element for each overlapping pair. Equivalent to `2lvƒ`

Usage:
```
¨p<element>
```

-------------------------------
## `` ¨? `` (Explicit STDIN)

Read from STDIN, even if there are arguments

-------------------------------
## `` ¨S `` (Override Inputs)

Overrides the list of inputs

-------------------------------
## `` ¨R `` (Reset Inputs)

Resets the list of inputs to what they were before overriding with `¨S`

-------------------------------
## `` ¨i `` (If/Else)
If the top of the stack is truthy, run the first element, otherwise the second.

Usage:
```
¨i<element><element>
```

-------------------------------
## `` ¨ḭ `` (Unevaluated Input)

Push the next input as a string, unevaluated. Like ? but without implicit conversions

-------------------------------
## `` ¨" `` (Parse Into List)

Splits a into a list using only the strings in b, with longest strings in b being scanned first.

-------------------------------
## `` ¨Ȯ `` (First x Integers Greater Than or Equal to y)

Push the first x integers greater than or equal to y where z(_) is truthy

### Overloads

- fun a, num b, num c: `Push the first b integers that are greater than or equal to c where a(_) is truthy`
- num a, fun b, num c: `Push the first c integers that are greater than or equal to a where b(_) is truthy`
- num a, num b, fun c: `Push the first b integers that are greater than or equal to a where c(_) is truthy`
-------------------------------
## `` ¨ȯ `` (First x Integers Greater Than y)

Push the first x integers greater than y where z(_) is truthy

### Overloads

- fun a, num b, num c: `Push the first b integers that are greater than c where a(_) is truthy`
- num a, fun b, num c: `Push the first c integers that are greater than a where b(_) is truthy`
- num a, num b, fun c: `Push the first b integers that are greater than a where c(_) is truthy`
-------------------------------
## `` ¨x `` (Continue)

Continue a Loop

-------------------------------
## `` ¨X `` (Print the entire stack)

Print the entire stack

-------------------------------
## `` ¨¤ `` (Break)

Break a Loop

-------------------------------
