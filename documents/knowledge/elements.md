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

Returns the first truthy argument if both are truthy, otherwise returns the first falsey argument.

### Overloads

- any a, any b: `a and b`
-------------------------------
## `` ⟑ `` (Apply Lambda)

Like a mapping lambda, but the results are evaluated immediately, instead of being lazily evaluated

-------------------------------
## `` ∨ `` (Logical Or)

Returns the first truthy argument, otherwise the first falsey argument.

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

Calls a function / executes as python / len(prime factors) / vectorised not

### Overloads

- fun a: `a()`
- num a: `len(prime_factors(a))`
- str a: `exec as python`
- lst a: `vectorised not`
-------------------------------
## `` € `` (Split On)

Split a on b (works on lists and numbers as well)

### Overloads

- any a, any b: `a split on b`
-------------------------------
## `` ½ `` (Halve)

Halves an item

### Overloads

- num a: `a / 2`
- str a: `a split into two strings of equal lengths (as close as possible)`
-------------------------------
## `` ∆ `` (Mathematic Digraph)

Used for mathematical digraphs

-------------------------------
## `` ø `` (String Digraph)

Used for string-based digraphs

-------------------------------
## `` ↔ `` (Combinations/Remove/Fixed Point Collection)

Does either combinations_with_replacement, removes items from a not in b, or applies a on b until the result stops changing.

### Overloads

- any a, num b: `combinations_with_replacement(a, length=b)`
- fun a, any b: `Apply a on b until the result does not change, yielding intermediate values`
- any a, str b: `Remove elements from a that are not in b`
- any a, lst b: `Remove elements from a that are not in b.`
-------------------------------
## `` ¢ `` (Infinite Replacement / Apply at Indices)

Replace b in a with c until a does not change / Call a function on all elements at specified indices together and put that back in the list

### Overloads

- any a, any b, any c: `replace b in a with c until a does not change`
- lst a, fun b, lst c: `apply function b to items at indices in a`
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
- str a: `caseof(a) - 1 if all letters in a are uppercase, 0 if all letters in a are lowercase, -1 if mixed case`
-------------------------------
## `` ʀ `` (Inclusive Zero Range)

Inclusive range or whether each character is alphabetical

### Overloads

- num a: `range(0,a + 1) (inclusive range from 0)`
- str a: `[is v alphabetical? for v in a]`
-------------------------------
## `` ʁ `` (Exclusive Zero Range)

Exclusive range or palindromised

### Overloads

- num a: `range(0,a) (exclusive range from 0)`
- str a: `palindromised a`
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
## `` ƈ `` (Choose / random choice / set same)

Binomial coefficient / choose a random items from b / same except duplicates

### Overloads

- num a, num b: `a choose b (binomial coefficient)`
- num a, str b: `Choose a random items from b`
- str a, num b: `Choose b random items from a`
- str a, str b: `Check if lists are the same except for duplicates`
-------------------------------
## `` ∞ `` (Palindromise)

Palindromise a

### Overloads

- any a: `palindromised a`
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
- str a, str b: `ring translate b according to a`
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
- num a, str b: `b split into a even length pieces, possibly with an extra part`
- str a, num b: `a split into b even length pieces, possibly with an extra part`
- str a, str b: `a.split(b)`
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
-------------------------------
## `` ? `` (Input)

Get the next input from the input source

-------------------------------
## `` @ `` (Function Call / Declaration)

Call / declare function (@name; / @name|code;)

-------------------------------
## `` A `` (All)

Chck if all items in a list are truthy / check if a character is a vowel

### Overloads

- str a: `is_vowel(a) if a.length == 1 else [is_vowel(z) for z "[char * b for char in a] - Map over each char if the string is multiple characters`
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
- str a: `equivlaent to `qp``
- lst a: `split a list into two halves`
-------------------------------
## `` J `` (Merge)

Join two lists or items

### Overloads

- lst a, str b: `a.append(b) (Append)`
- lst a, num b: `a.append(b) (Append)`
- str a, lst b: `b.prepend(a) (Prepend)`
- num a, lst b: `b.prepend(a) (Prepend)`
- lst a, lst b: `merged(a,b) (Merge)`
- any a, any b: `a + b (Concatenate)`
-------------------------------
## `` K `` (Factors / Substrings / Prefixes)

Get either the factors of a, substrings that occur more than once, or prefixes

### Overloads

- num a: `divisors(a) (factors)`
- str a: `All substrings of a that occur more than once in a`
- lst a: `prefixes(a) (prefixes)`
-------------------------------
## `` L `` (Length)

Get length of a

### Overloads

- any a: `len(a)`
-------------------------------
## `` M `` (Map)

Map b over a

### Overloads

- any a, fun b: `map(b,a) (apply b to each of a)`
- any a, any b: `pair each item of b with a`
-------------------------------
## `` N `` (Negate / Swap Case)

Negate a or swap its case

### Overloads

- num a: `-a  (negate)`
- str a: `swap_case(a) (toggle case)`
-------------------------------
## `` O `` (Count)

Count number of times b occurs in a

### Overloads

- any a, any b: `a.count(b)`
-------------------------------
## `` P `` (Strip)

a.strip(b) - trim b from both ends of a

### Overloads

- any a, any b: `a.strip(b)`
-------------------------------
## `` Q `` (Quit)

Quit the program

-------------------------------
## `` R `` (Reduce)

Reduce a by b, or reverse each item of b

### Overloads

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

- any a: `uniquify(a) (Remove duplicates)`
-------------------------------
## `` V `` (Replace)

Replace b with c in a

### Overloads

- any a, any b, any c: `a.replace(b,c) (replace)`
-------------------------------
## `` W `` (Wrap)

Stack wrapped into a list

-------------------------------
## `` X `` (Break)

Break out of the current loop or function

-------------------------------
## `` Y `` (Interleave)

Interleave two lists

### Overloads

- any a, any b: `interleave(a,b)`
-------------------------------
## `` Z `` (Zip)

Zip two lists or Zip a with b mapped over a

### Overloads

- any a, any b: `zip(a,b)`
- any a, fun b: `zip(a,map(b,a)) (Zipmap, map and zip)`
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

- str a: `is_uppercase(a) if a.length == 1 else [is_uppercase(z) for z "[char * b for char in a] - Map over each char if the string is multiple characters`
- lst a: `any(a) (Are any items truthy?)`
-------------------------------
## `` b `` (Binary)

Convert a number or string to binary

### Overloads

- num a: `bin(a) - list of binary digits of A`
- str a: `[bin(ord(char)) for char in a] - binary of each codepoint`
-------------------------------
## `` c `` (Contains)

Check if one thing contains another.

### Overloads

- any a, any b: `b in a (Does a contain b, membership, contains)`
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
- str a, str b: `regex.search(pattern=a, string=b).span() (Length of regex match)`
-------------------------------
## `` f `` (Flatten)

Turn a number into a list of digits, a string into a list of characters, and flatten a list.

### Overloads

- num a: `digits of a`
- str a: `a split into list of characters`
- lst a: `flatten(a) (Deep flatten)`
-------------------------------
## `` g `` (Minimum)

Take the minimum of a list

### Overloads

- any a: `min(a)`
-------------------------------
## `` h `` (Head)

First item of something

### Overloads

- any a: `a[0] (First item)`
-------------------------------
## `` i `` (Index)

Index into a list

### Overloads

- any a, num b: `a[b] (Index)`
- any a, [x] b: `a[:b] (0 to bth item of a)`
- any a, [x,y] b: `a[x:y] (x to yth item of a)`
- any a, [x,y,m] b: `a[x:y:m] (x to yth item of a, taking every mth)`
-------------------------------
## `` j `` (Join)

Join a list by a string

### Overloads

- any a, any b: `a.join(b)`
-------------------------------
## `` k `` (Constant Digraph)

Used for constant digraphs.

-------------------------------
## `` l `` (Cumulative Groups)

Cumulative grouping / equal length

### Overloads

- any a, num b: `n-wise_group(a,b) ( Overlapping groups of a of length b)`
- any a, any b: `length(a) == length(b)`
-------------------------------
## `` m `` (Mirror)

Append input reversed to itself.

### Overloads

- num a: `a + reversed(a) (as number)`
- str a: `a + reversed(a)`
- lst a: `Append reversed(a) to a`
-------------------------------
## `` n `` (Context)

Context variable, value of the current loop or function.

-------------------------------
## `` o `` (Remove)

Remove instances of b in a

### Overloads

- num a, fun b: `first a positive integers where b is truthy`
- fun a, num b: `first b positive integers where a is truthy`
- any a, any b: `a.replace(b,"")`
-------------------------------
## `` p `` (Prepend)

Prepend b to a

### Overloads

- any a, any b: `a.prepend(b) ( Prepend b to a)`
-------------------------------
## `` q `` (Uneval)

Enclose in backticks, escape backslashes and backticks.

### Overloads

- any a: `uneval(a) (Enclose in bacticks + escape)`
-------------------------------
## `` r `` (Range)

Range betweeen two numbers, or cumulative reduce, or regex match

### Overloads

- num a, num b: `range(a,b) (Range form a to b)`
- num a, str b: `append spaces to b to make it length a`
- str a, num b: `preprend spaces to a to make it length b`
- any a, fun b: `cumulative_reduce(a,function=b) (Prefixes of a reduced by b)`
- str a, str b: `regex.has_match(pattern=a,string= b) ( Does b match a)`
-------------------------------
## `` s `` (sort)

Sort a list or string

### Overloads

- any a: `sorted(a) (Sort)`
-------------------------------
## `` t `` (Tail)

Last item

### Overloads

- any a: `a[-1] (Last item)`
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
## `` w `` (Listify)

a wrapped in a singleton list

### Overloads

- any a: `[a] (Wrapped in singleton list)`
-------------------------------
## `` x `` (Recurse / Continue / Print Stack)

Call current function (Functions/Lambdas) / Continue (For Loops) / Print the entire stack (otherwise)

-------------------------------
## `` y `` (Uninterleave)

Push every other item of a, and the rest.

### Overloads

- any a: `a[::2], a[1::2] (Every second item, the rest)`
-------------------------------
## `` z `` (Zip-self)

Zip a with itself

### Overloads

- any a: `zip(a,a)`
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

- any a: `max(a, key=lambda x: x[-1]) (Maximum by last item)`
-------------------------------
## `` ↓ `` (Min by Tail)

Minimum by last item

### Overloads

- any a: `min(a, key=lambda x: x[-1]) (Minimum by last item)`
-------------------------------
## `` ∴ `` (Dyadic Maximum)

Maximum of two values

### Overloads

- any a, any b: `max(a,b)`
-------------------------------
## `` ∵ `` (Dyadic Minimum)

Minimum of two values

### Overloads

- any a, any b: `min(a,b)`
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

- num a: `a % 2 (Odd?)`
- str a: `Second half of A`
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
-------------------------------
## `` τ `` (From Base Ten / To Custom Base)

Convert a number to a different base from base 10.

### Overloads

- num a, num b: `List of digits of a in base b`
- num a, str b: `a converted into a string of characters of b`
- num a, lst b: `a converted into a list of arbitrary values from b`
-------------------------------
## `` ȧ `` (Absolute value)

Take the absolute value of a number, or remove whitespace from a string

### Overloads

- num a: `abs(a) (Absolute value)`
- str a: `Remove whitespace from a`
-------------------------------
## `` ḃ `` (Boolify)

Convert an arbitrary value into a truthy or falsy value, vectorises with flag t

### Overloads

- any a: `bool(a) (Booliify)`
-------------------------------
## `` ċ `` (Not One)

Check if something is not equal to 1

### Overloads

- any a: `a != 1`
-------------------------------
## `` ḋ `` (Divmod)

Divmod / combinations / trim

### Overloads

- num a, num b: `[a // b, a % b] (Divmod - division and modulo)`
- str a, num b: `Combinations of a with length b`
- lst a, num b: `Combinations of a with length b`
- str a, str b: `overwrite the start of a with b -> `abcdef` `Joe`Ḋ -> `Joedef``
-------------------------------
## `` ė `` (Enumerate)

Zip with a range of the same length

### Overloads

- any a: `enumerate(a) (Zip with 1...len(a))`
-------------------------------
## `` ḟ `` (Find)

Find a value in another

### Overloads

- any a, any b: `a.find(b) (Indexing)`
- any a, fun b: `truthy indices of mapping b over a`
-------------------------------
## `` ġ `` (Gcd)

Greatest Common Denominator of a list or some numbers

### Overloads

- lst a: `GCD(a) (Gcd of whole list)`
- num a, num b: `gcd(a,b) (Dyadic gcd)`
- str a, str b: `Longest common suffix of a and b`
-------------------------------
## `` ḣ `` (Head Extract)

Separate the first item of something and push both to stack

### Overloads

- any a: `a[0], a[1:] (Head extract)`
-------------------------------
## `` ḭ `` (Floor Division)

Floor divide a by b

### Overloads

- num a, num b: `a // b (Floor division, floor(a / b))`
- str a, num b: `(a divided into b pieces)[0]`
- num a, str b: `(b divided into a pieces)[0]`
- any a, fun b: `Right reduce a by b (foldr)`
- fun a, any b: `Right reduce b by a (foldr)`
-------------------------------
## `` ŀ `` (Left Justify / Gridify / Infinite Replace / Collect until fale)

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
- fun a, fun b, any c: `collect_until_false(predicate=a, modifying_function=b, inital=c)`
-------------------------------
## `` ṁ `` (Mean)

Average of a list - sum / length

### Overloads

- str a: `palindromise(a)`
- lst a: `mean(a)`
-------------------------------
## `` ṅ `` (Join By Nothing)

Join a list by the empty string

### Overloads

- fun a: `First integer for which a(x) is truthy`
- any a: `Join by empty string`
- num a: `abs(a) <= 1`
-------------------------------
## `` ȯ `` (Slice)

Slice from an index to the end

### Overloads

- fun a, num b: `First b integers for which a(x) is truthy`
- any a, num b: `a[b:] (Slice from b to the end)`
- str a, str b: `vertically merge a and b`
-------------------------------
## `` ṗ `` (Powerset)

All possible combinations of a

### Overloads

- any a: `All possible combinations of a`
-------------------------------
## `` ṙ `` (Round)

Round a number to the nearest integer

### Overloads

- num a: `round(a)`
- str a: `quad palindromize with overlap`
-------------------------------
## `` ṡ `` (Sort by Function)

Sort a list by a function / create a range / split on a regex

### Overloads

- any a, fun b: `sorted(a, key=b) (Sort by b)`
- num a, num b: `range(a, b + 1) (Inclusive range from a to b)`
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
- any a, fun b: `Apply b to every second item of a`
- fun a, any b: `Apply a to every second item of b`
- str a, str b: `split a on first occurance of b`
-------------------------------
## `` ẋ `` (Repeat)

Repeat a value several times

### Overloads

- str a, num b: `a * b`
- num a, str b: `b * a`
- any a, num b: `Repeat a b times`
- str a, str b: `a + " " + b`
- fun a, any b: `repeat function a on b while the function results are not-unique`
- any a, fun b: `repeat function b on a while the function results are not-unique`
-------------------------------
## `` ẏ `` (Exclusive Range Length)

Range from 0 to length of a

### Overloads

- any a: `range(0, len(a)) (Exclusive range from 0 to length of a)`
-------------------------------
## `` ż `` (Inclusive Range Length)

Range from 1 to length of a inclusive

### Overloads

- any a: `range(1, len(a)+1) (Inclusive range from 1 to length of a)`
-------------------------------
## `` √ `` (Square Root)

Square root a number / every second character of a

### Overloads

- num a: `sqrt(a) (Square root)`
- str a: `every second character of a`
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

- num a: `a % 2 == 0 (Even?)`
- any a: `len(a) % 2 == 0 (Length even?)`
-------------------------------
## `` ₃ `` (Divisible By three)

Check if a is divisible by 3

### Overloads

- num a: `a % 3 == 0 (divisible by 3?)`
- any a: `len(a) == 1 (Length is 1?)`
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

- any a: `Transpose a, join on newlines`
-------------------------------
## `` ε `` (Absolute Difference / Padded Vertical Join)

Returns the aboslute different (|a - b|) or vertically joins using padding

### Overloads

- num a, num b: `abs(a - b)`
- any a, str b: `Transpose a (filling with b), join on newlines`
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

- any a: `cumulative_sum(a)`
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
## `` Ḋ `` (Is Divisible / Arbitrary Duplicate)

Returns whether two items are divisble / numerious copies of the top of the stack

### Overloads

- num a, num b: `a % b == 0`
- num a, str b: `a copies of b`
- str a, num b: `b copies of a`
- str a, str b: `b + " " + a`
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
- num a, str b: `every ath letter of b`
- str a, num b: `every bth letter of a`
- str a, str b: `replace spaces in a with b`
- lst a, num b: `every bth item of a`
- num a, lst b: `every ath item of b`
- fun a, lst b: `Generator from function a with initial vector b`
-------------------------------
## `` Ġ `` (Group consecutive)

Group consecutive identical items

### Overloads

- lst a: `Group consecutive identical items`
- str a: `Group consecutive identical characters`
- num a: `Group consecutive identical digits`
-------------------------------
## `` Ḣ `` (Head Remove / Behead)

All but the first item of a list / Drop 1

### Overloads

- lst a: `a[1:] or [] if empty`
- str a: `a[1:] or '' if empty`
- num a: `Remove first digit or do nothing if <1`
-------------------------------
## `` İ `` (Index into or collect while unique)

Index into list at indices / Collect values while values are unique

### Overloads

- any a, lst b: `[a[item] for item in b]`
- any a, fun b: `apply b on a and collect unique values`
-------------------------------
## `` Ŀ `` (Transliterate)

Replace each item of one value in another value with the corresponding element from a third value

### Overloads

- any a, any b, any c: `transliterate(a,b,c) (Replace each item of b in c with the corresponding value from a)`
- fun a, fun b, any c: `Call b on c until a(c) is falsey.`
-------------------------------
## `` Ṁ `` (Insert)

Insert a value at a specified index / Map a function over every nth item of a list

### Overloads

- any a, num b, any c: `a.insert(b,c) (Insert c at position b in a)`
- any a, num b, fun c: `c mapped over every bth item of a`
-------------------------------
## `` Ṅ `` (Integer partitions)

Integer partitions / join by space

### Overloads

- num a: `integer_partitions(a) (Integer partitions)`
- any a: `" ".join(a) (Join by space)`
-------------------------------
## `` Ȯ `` (Over)

Push the second-last item of stack to the top

-------------------------------
## `` Ṗ `` (Permutations)

Get all permutations of a value

### Overloads

- any a: `permutations(a) (Get all permutations)`
-------------------------------
## `` Ṙ `` (Reverse)

Reverse a value

### Overloads

- any a: `a, reversed(a)`
-------------------------------
## `` Ṡ `` (Vectorised sums)

Sum of each item in a list

-------------------------------
## `` Ṫ `` (Tail Remove)

Cut off the last item of a list

### Overloads

- any a: `a[:-1] (All but the last item)`
-------------------------------
## `` Ẇ `` (Split And Keep Delimiter)

Split a value and keep the delimiter

### Overloads

- any a, any b: `a.split_and_keep_delimiter(b) (Split and keep the delimiter)`
- fun a, any b: `apply a to every second item of b starting on the first item`
-------------------------------
## `` Ẋ `` (Cartesian Product / Fixpoint)

Take the Cartesian Product of two values, or apply a function until there is no change.

### Overloads

- any a, any b: `cartesian-product(a,b)`
- fun a, any b: `Apply a on b until b does not change`
-------------------------------
## `` Ẏ `` (Slice Until)

Slice a list until a certain index / find all results for a regex match

### Overloads

- any a, num b: `a[0:b] (Slice until b)`
- num a, any b: `b[0:a] (Slice until a)`
- str a, str b: `regex.findall(pattern=a,string=b) (Find all matches for a regex)`
-------------------------------
## `` Ż `` (Slice From One Until)

Slice from index 1 until a number / get groups of a gregex match

### Overloads

- any a, num b: `a[1:b] (Slice from 1 until b)`
- num a, any b: `b[1:a] (Slice from 1 until a)`
- str a, str b: `regex.match(pattern=a,string=b).groups() (Get groups for a regex match)`
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
## `` ⁰ `` (First Input)

Push the first input

-------------------------------
## `` ¹ `` (Second Input)

Push the second input

-------------------------------
## `` ² `` (Square)

Square a number / Format a string into a square

### Overloads

- num a: `a ** 2 (Squared)`
- str a: `a formatted as a square`
-------------------------------
## `` ∇ `` (Shift)

Shift the top of stack two values down

### Overloads

- any a, any b, any c: `c,a,b (Shift)`
-------------------------------
## `` ⌈ `` (Ceiling)

Take the ceiling of a number / split a string on spaces

### Overloads

- num a: `ceil(a) (Ceiling)`
- str a: `Split on spaces`
-------------------------------
## `` ⌊ `` (Floor)

Floor a number / extract the integer part of a string

### Overloads

- num a: `floor(a) (Floor)`
- str a: `Integer part of a`
-------------------------------
## `` ¯ `` (Deltas)

Deltas (consecutive differences)

### Overloads

- any a: `deltas(a) (consecutive differences)`
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

Right-bitshift a value / right-justify a string

### Overloads

- num a, num b: `a << b`
- num a, str b: `a.rjust(b)`
- str a, num b: `b.rjust(a)`
- str a, str b: `a.rjust(len(b)-len(a))`
-------------------------------
## `` ↲ `` (Left Bit Shift)

Left-bitshift a value / left-justify a string

### Overloads

- num a, num b: `a >> b`
- num a, str b: `a.ljust(b)`
- str a, num b: `b.ljust(a)`
- str a, str b: `a.ljust(len(b)-len(a))`
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

Performs bitwise not on a number / check if any letters are uppercase

### Overloads

- num a: `~a`
- str a: `any_upper(a)`
-------------------------------
## `` ℅ `` (Random Choice)

Random choice of single item from array

### Overloads

- lst a: `random.choice(a)`
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
## `` ∩ `` (Tranpose)

Transpose an array

### Overloads

- any a: `Transposed array`
-------------------------------
## `` ⊍ `` (Symmetric Set difference)

Uncommon elements of two arrays

### Overloads

- any a, any b: `list(set(a) ^ set(b))`
-------------------------------
## `` £ `` (Set Register)

set the register to argument value

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

Remove non-alphabetical characters / power with base 2

### Overloads

- str a: `filter(isalpha, a)`
- num a: `2 ** a`
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

- num a: `prime_factorization(a)`
- str a: `a + a[0]`
-------------------------------
## `` ǐ `` (Prime factors)

all prime factors / Title Case string

### Overloads

- num a: `prime_factors(a)`
- str a: `title_case(a)`
-------------------------------
## `` Ǒ `` (Multiplicity)

Order, Multiplicity, Valuation / remove till fixpoint

### Overloads

- num a, num b: `multiplicity(a,b)`
- str a, str b: `remove_till_fixpoint(a,b)`
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

Compressed number in 1-128 (prefix)

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

- lst[num] a: `reduce list by multiplication`
- lst[str|lst] a: `reduce list by cartesian product`
-------------------------------
## `` „ `` (Rotate Stack Left)

Rotate Stack Left

-------------------------------
## `` ‟ `` (Rotate Stack Right)

Rotate Stack Right

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
## `` kP `` (Printable ASCII)

printable ascii

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
## `` kn `` (NaN)

math.nan

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
- num a, str b: `solve a such that a = b`
- str a, num b: `solve b such that b = a`
- str a, str b: `solve equation a = b for x`
-------------------------------
## `` ∆Q `` (General Quadratic Solver)

Solve a quadratic equation of the form x^2 + ax + b = 0

### Overloads

- num a, num b: `roots(a, b) / x^2 + ax + b = 0`
- num a, str b: `evaluate single variable expression b with x=a`
- str a, num b: `evaluate single variable expression a with x=b`
- str a, str b: `solve equations a and b simultaneously`
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
## `` ∆² `` (Perfect Square?)

Is the number a perfect square? (1, 4, 9, 16, 25, 36)

### Overloads

- num a: `is_perfect_square(a)`
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

Get the straight line distance between two points (x1, y1) and (x2, y2)

### Overloads

- lst a, lst b: `euclidian_distance(a, b)`
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

- num a, num b: `round(a, no_dec_places=b)`
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
## `` ∆Ė `` (N Digits of Euler's Number (e) / Sympy Evaluate)

Get the first n digits of Euler's number (e) / evaluate an expression as sympy

### Overloads

- num a: `First n digits of e`
- str a: `evaluate(a)`
-------------------------------
## `` ∆ė `` (Nth Digit of Euler's Number (e) / Differentiate)

Get the nth digit of Euler's number (e)

### Overloads

- num a: `nth_digit_of_e(a)`
- str a: `derivative(a)`
-------------------------------
## `` ∆f `` (nth Fibonacci Number)

Get the nth fibonacci number

### Overloads

- num a: `nth_fibonacci(a)`
-------------------------------
## `` ∆B `` (Random Bits)

Get a list of random bits to length n

### Overloads

- num a: `random_bits(a)`
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

Increment a until b(a) is false

### Overloads

- any a, fun b: `while b(a): a += 1`
- fun a, any b: `while a(b): b += 1`
-------------------------------
## `` ∆‹ `` (Decrement until false)

Decrement a until b(a) is false

### Overloads

- any a, fun b: `while b(a): a -= 1`
- fun a, any b: `while a(b): b -= 1`
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

- any a, str b, num c: `Pad a to the left with c so a has length b`
- any a, num b, str c: `Pad a to the left with b so a has length c`
-------------------------------
## `` ø↲ `` (Custom Pad Right)

Pad a string to the right with a certain character

### Overloads

- any a, str b, num c: `Pad a to the right with c so a has length b`
- any a, num b, str c: `Pad a to the right with b so a has length c`
-------------------------------
## `` øM `` (Flip Brackets Vertical Palindromise)

Vertically palindromise and reverse brackets and slashes, without duplicating center

### Overloads

- any a: `Palindromise, without duplicating center, and flip brackets and slashes in the second half`
-------------------------------
## `` øo `` (Remove Until No change)

Remove b from a until a does not change

### Overloads

- str a, str b: `Remove b from a until a does not change`
- str a, lst b: `Remove everything in b (in order) from a until a does not change`
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
## `` øĊ `` (Center)

Center a list of strings

### Overloads

- lst a: `Center(a) (Pad each item with spaces so all are centered)`
-------------------------------
## `` øe `` (Run Length Encoding)

Run length encoding, convert from string to list of characters and amount repeated.

### Overloads

- str a: `run_length_encoded(a)`
-------------------------------
## `` ød `` (Run Length Decoding)

Run length decoding, convert from list of characters and lengths to a string

### Overloads

- lst a: `run_length_decoded(a)`
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
## `` øṙ `` (Regex replace)

Replace matches of a with c in b

### Overloads

- any a, any b, fun c: `Apply c to matches of a in b`
- any a, any b, any c: `Replace matches of a with c in b`
-------------------------------
## `` øp `` (Starts With)

Check if one value starts with another

### Overloads

- any a, any b: `a.startswith(b) (Starts with b?)`
-------------------------------
## `` øP `` (Pluralise Count)

Create a sentence of the form 'a bs'

### Overloads

- num a, str b: `a + " " + b + (s if a != 1 else "") (Concatenate with space, append a s if not 1)`
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
## `` Þ* `` (Cartesian product over list)

Cartesian product over a list of lists

### Overloads

- lst a: `itertools.product(*a)`
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
## `` ÞẊ `` (Cartesian Power)

Cartesian power, cartesian product with self n times

### Overloads

- any a, num b: `cartesian_power(a, b)`
- num a, any b: `cartesian_power(b, a)`
-------------------------------
## `` Þf `` (Flatten By depth)

Flatten a list by a certain depth (default 1)

### Overloads

- lst a, num b: `Flatten a by depth b`
- any a, lst b: `Flatten b by depth 1, push a as well`
-------------------------------
## `` ÞB `` (Random Bits)

Fill a list with random bits

### Overloads

- num a: `List of length a filled with random bits`
- any a: `List of length n(a) filled with random bits`
-------------------------------
## `` Þ< `` (All Less Than Increasing)

Find all numbers less than a certain value in a (potentially infinite) list assumed to be (non-strictly) increasing

### Overloads

- any a, num b: `All values of a up to (not including) the first greater than or equal to b`
-------------------------------
## `` Þǔ `` (Untruth)

Return a list with 1s at the (0-indexed) indices in a, and 0s elsewhere

### Overloads

- any a: `[int(x in a) for x in range(len(a))]`
-------------------------------
## `` Þi `` (Multidimensional Indexing)

Index a list of coordinates into a value.

### Overloads

- lst a, lst b: `a[b[0]][b[1]][b[2]]... Reduce by indexing with a as initial value`
-------------------------------
## `` Þḟ `` (Multidimensional Search)

Find the first multidimensional index of a value in another

### Overloads

- lst a, any b: `Find the first occurrence of a in b and return as a multidimensional index`
-------------------------------
## `` Þm `` (Zero Matrix)

Given a list of dimensions, create a matrix with those dimensions, filled with zeroes

### Overloads

- lst a: `Matrix with dimensions each item of a, where the first is the innermost and the last is the outermost`
-------------------------------
## `` ÞṄ `` (Infinite Integer Partitions)

Infinite list of sets of positive integers (equivalent to Þ∞vṄÞf)

-------------------------------
## `` ÞZ `` (Fill By Coordinates)

Fill a matrix by calling a function with the lists of coordinates in the matrix.

### Overloads

- any a, fun b: `For each value of a (all the way down) call b with the coordinates of that value and put that at the appropriate position in a.`
-------------------------------
## `` Þ… `` (Evenly Distribute)

Evenly distribute a number over elements of a list

### Overloads

- list a, num b: `Evenly distribute a over all elements of b, adding each part.`
-------------------------------
## `` Þ↓ `` (Minimum By Function)

Find the minimum value of a list by applying a function to each element

### Overloads

- lst a, fun b: `Minimum value of a by applying b to each element`
-------------------------------
## `` Þ↑ `` (Maximum By Function)

Find the maximum value of a list by applying a function to each element

### Overloads

- lst a, fun b: `Maximum value of a by applying b to each element`
-------------------------------
## `` Þ× `` (All Combinations)

All combinations of a list / string, of all lengths, with replacement

### Overloads

- any a: `All combinations of a list / string, of all lengths and all orders, with replacement`
-------------------------------
## `` Þx `` (All Combinations Without Replacement)

All combinations of a list / string, of all lengths, without replacement

### Overloads

- any a: `All combinations of a list / string, of all lengths and all orders, without replacement`
-------------------------------
## `` ÞF `` (All Fibbonacci)

All Fibbonacci numbers as a LazyList.

-------------------------------
## `` Þ! `` (All Factorials)

All factorials as a LazyList.

-------------------------------
## `` ÞU `` (Uniquify Mask)

A list of booleans describing which elements of a will remain after uniquifying.

### Overloads

- any a: `A list of booleans describing which elements of a will remain after uniquifying.`
-------------------------------
## `` ÞD `` (Diagonals)

Diagonals of a matrix, starting with the main diagonal.

### Overloads

- lst a: `Diagonals of a matrix, starting with the main diagonal.`
-------------------------------
## `` ÞS `` (Sublists)

Sublists of a list.

### Overloads

- lst a: `Sublists of a list.`
-------------------------------
## `` ÞṪ `` (Transpose With Filler)

Transpose a matrix, with a filler value for empty cells.

### Overloads

- lst a, any b: `Transpose a matrix, with a filler value for empty cells.`
-------------------------------
## `` Þ℅ `` (Random Permutation)

Random permutation of a list / string

### Overloads

- any a: `Random permutation of a list / string`
-------------------------------
## `` ÞṀ `` (Matrix Multiplication)

Multiply two matrices together.

### Overloads

- lst a, lst b: `Matrix multiplication`
-------------------------------
## `` ÞḊ `` (Matrix Determinant)

Calculate the determinant of a matrix.

### Overloads

- lst a: `Calculate the determinant of a matrix.`
-------------------------------
## `` Þ\ `` (Antidiagonal)

Antidiagonal of a matrix

### Overloads

- lst a: `Antidiagonal of a matrix`
-------------------------------
## `` Þ/ `` (Main Diagonal)

Diagonal of a matrix

### Overloads

- lst a: `Diagonal of a matrix`
-------------------------------
## `` ÞR `` (Matrix Row Reduce)

Reduce rows of a matrix by a function.

### Overloads

- lst a, fun b: `Reduce rows of a matrix by a function.`
-------------------------------
## `` ÞC `` (Matrix Column Reduce)

Reduce columns of a matrix by a function.

### Overloads

- lst a, fun b: `Reduce columns of a matrix by a function.`
-------------------------------
## `` Þ• `` (Dot Product)

Dot product of two lists.

### Overloads

- lst a, lst b: `Dot product of two lists.`
-------------------------------
## `` Þṁ `` (Mold without repeat)

Mold a list without repeating elements.

### Overloads

- lst a, lst b: `Mold a list without repeating elements.`
-------------------------------
## `` ÞM `` (Maximal Indicies)

Indicies of the maximal elements of a list.

### Overloads

- lst a: `Indicies of the maximal elements of a list.`
-------------------------------
## `` Þ∴ `` (Elementwise Vectorised Dyadic Maximum)

Elementwise vectorised dyadic maximum.

### Overloads

- lst a, lst b: `Elementwise vectorised dyadic maximum.`
-------------------------------
## `` Þ∵ `` (Elementwise Vectorised Dyadic Minimum)

Elementwise vectorised dyadic minimum.

### Overloads

- lst a, lst b: `Elementwise vectorised dyadic minimum.`
-------------------------------
## `` Þs `` (All Slices of a List)

Get all slices of a list, skipping a certain number of items

### Overloads

- lst a, int b: `Get all slices of a list, skipping a certain number of items`
- int a, lst b: `Same as lst-int but with arguments swapped`
-------------------------------
## `` Þ¾ `` (Empty the Global Array)

Empty the global array.

-------------------------------
## `` Þr `` (Remove Last Item and Prepend 0)

Remove the last item of a list and prepend 0. A shortcut for Ṫ0p

### Overloads

- lst a: `Remove the last item of a list and prepend 0. A shortcut for Ṫ0p`
-------------------------------
## `` Þ∞ `` (Infinite List)

An infinite list of positive integers

-------------------------------
## `` ÞR `` (Remove Last Item From Cumulative Sums and Prepend 0)

Remove the last item of the cumulative sums of a list and prepend 0. A shortcut for ¦Ṫ0p

### Overloads

- lst a: `Remove the last item of the cumulative sums of a list and prepend 0. A shortcut for ¦Ṫ0p`
-------------------------------
## `` Þẇ `` (Unwrap)

Take a and push a[0]+a[-1] and a[1:-1]

### Overloads

- lst a: `Take a and push a[0]+a[-1] and a[1:-1]`
-------------------------------
## `` Þg `` (Shortest By Length)

Return the shortest item in a list.

### Overloads

- lst a: `Return the shortest item in a list.`
-------------------------------
## `` ÞG `` (Longest By Length)

Return the longest item in a list.

### Overloads

- lst a: `Return the longest item in a list.`
-------------------------------
## `` Þṡ `` (Sort By Length)

Sort a list by length.

### Overloads

- lst a: `Sort a list by length.`
-------------------------------
## `` ÞṠ `` (Is Sorted?)

Returns true if an item is sorted in ascending order using default sorting rules.

### Overloads

- lst a: `Returns true if an item is sorted in ascending order using default sorting rules.`
-------------------------------
## `` ÞṘ `` (Is Sorted in Reverse?)

Returns true if an item is sorted in descending order using default sorting rules.

### Overloads

- lst a: `Returns true if an item is sorted in descending order using default sorting rules.`
-------------------------------
## `` ÞȮ `` (Is Ordered?)

Returns true if the item is sorted in either descending or ascending order.

### Overloads

- lst a: `Returns true if the item is sorted in either descending or ascending order.`
-------------------------------
## `` ÞĊ `` (Is Unordered?)

Returns true if the item is not sorted in either descending or ascending order.

### Overloads

- lst a: `Returns true if the item is not sorted in either descending or ascending order.`
-------------------------------
## `` ÞK `` (Suffixes)

Suffixes of a list.

### Overloads

- lst a: `Suffixes of a list.`
-------------------------------
## `` Þİ `` (First n Items and Rest)

a[:b] and a[b:]

### Overloads

- lst a, int b: `a[:b] and a[b:]`
-------------------------------
## `` ÞN `` (Alternating Negation)

An infinite list of an item. then that item negated, then that item, and so on. Uses the negation element for negation.

### Overloads

- any a: `[a, -a, a, -a, ...]`
-------------------------------
## `` ¨□ `` (Parse direction arrow to integer)

Map characters in `>^<v` to integers (0, 1, 2, 3 respectively)

### Overloads

- str a: `Map characters in `>^<v` to integers`
-------------------------------
## `` ¨^ `` (Parse direction arrow to vector)

Map characters in `>^<v` to direction vectors

### Overloads

- str a: `Map characters in `>^<v` to direction vectors`
-------------------------------
## `` ¨U `` (Get Request)

Send a GET request to a URL

### Overloads

- str a: `Send a GET request to a URL`
-------------------------------
## `` ¨= `` (Invariant After Application)
Push whether the result of applying an element to an item is the same as the original item

Usage:
```
¨=<element>
```

-------------------------------
## `` ¨M `` (Map To Indices)

Map a function to elements of a list whose indices are in another list

### Overloads

- lst a, lst b, fun c: `Map a function to elements of a list whose indices are in another list`
-------------------------------
## `` ¨, `` (Print With Space)

Print a value with a space after it

### Overloads

- any a: `Print a value with a space after it`
-------------------------------
## `` ¨… `` (Print With Space Without Popping)

Print a value with a space after it, without popping it

### Overloads

- any a: `Print a value with a space after it, without popping it`
-------------------------------
## `` ¨> `` (Strict Greater Than)

Non-vectorising greater than - useful for lists. Note that all corresponding elements should be of the same type.

### Overloads

- any a, any b: `Non-vectorising greater than - useful for lists`
-------------------------------
## `` ¨< `` (Strict Less Than)

Non-vectorising greater than - useful for lists. Note that all corresponding elements should be of the same type.

### Overloads

- any a, any b: `Non-vectorising greater than - useful for lists`
-------------------------------
## `` ¨* `` (All Multiples)

Return all multiples of a

### Overloads

- num a: `[a*1, a*2, a*3, a*4, ...]`
- str a: `[a*1, a*2, a*3, a*4, ...]`
-------------------------------
## `` ¨£ `` (Star Map)
Reduce each pair of two lists zipped together by a function. Equivalent to Zvƒ

Usage:
```
¨£<element>
```

-------------------------------
