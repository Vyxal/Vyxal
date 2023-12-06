
# Information Tables

## Elements

| Symbol | Trigraph |  Name | Keywords | Arity | Vectorises | Overloads |
 --- | --- | --- | --- | --- | --- | --- |
 <code>🌮</code> |  | Taco | `taco` | NA | :x: | `very funky`
 <code>🍪</code> |  | Cookie | `cookie` | NA | :x: | `cookie.`
 <code>ඞ</code> |  | ඞ | `sus` | NA | :x: | `ඞ`
 <code>!</code> |  | Factorial | `fact`, `factorial` | 1 | :white_check_mark: | `a: num` => `a!`
 <code>$</code> |  | Swap | `swap` | NA | :x: | `a, b` => `b, a`
 <code>%</code> |  | Modulo / String Formatting | `mod`, `modulo`, `str-format`, `format`, `%`, `strfmt` | 2 | :x: | `a: num, b: num` => `a % b`
 | | | | | | | `a: str, b: any` => `a.format(b) (replace %s with b if scalar value or each item in b if vector)`
 <code>&</code> |  | Append | `append` | 2 | :x: | `a: any, b: any` => `list(a) ++ [b]`
 <code>*</code> |  | Exponentation / Remove Nth Letter / Trim | `exp`, `**`, `pow`, `exponent`, `remove-letter`, `str-trim` | 2 | :white_check_mark: | `a: num, b: num` => `a ^ b`
 | | | | | | | `a: str, b: num` => `a with the bth letter removed`
 | | | | | | | `a: num, b: str` => `b with the ath letter removed`
 | | | | | | | `a: str, b: str` => `trim b from both sides of a`
 <code>+</code> |  | Addition | `add`, `+`, `plus` | 2 | :white_check_mark: | `a: num, b: num` => `a + b`
 | | | | | | | `a: num, b: str` => `a + b`
 | | | | | | | `a: str, b: num` => `a + b`
 | | | | | | | `a: str, b: str` => `a + b`
 <code>,</code> |  | Print | `print`, `puts`, `out`, `println` | NA | :x: | `a` => `printed to stdout`
 <code>-</code> |  | Subtraction | `sub`, `subtract`, `minus`, `str-remove`, `remove`, `str-remove-all`, `remove-all` | 2 | :white_check_mark: | `a: num, b: num` => `a - b`
 | | | | | | | `a: str, b: num` => `a + b '-'s (or '-'s + a if b < 0)`
 | | | | | | | `a: num, b: str` => `a '-'s + b (or b + '-'s if a < 0)`
 | | | | | | | `a: str, b: str` => `a with b removed`
 <code>:</code> |  | Duplicate | `dup` | NA | :x: | `a` => `a, a`
 <code>;</code> |  | Pair | `pair` | 2 | :x: | `a, b` => `[a, b]`
 <code><</code> |  | Less Than | `lt`, `less`, `less-than`, `<`, `less?`, `smaller?` | 2 | :white_check_mark: | `a: num, b: num` => `a < b`
 | | | | | | | `a: str, b: num` => `a < str(b)`
 | | | | | | | `a: num, b: str` => `str(a) < b`
 | | | | | | | `a: str, b: str` => `a < b`
 <code>=</code> |  | Equals | `eq`, `==`, `equal`, `same?`, `equals?`, `equal?` | 2 | :white_check_mark: | `a: any, b: any` => `a == b`
 <code>></code> |  | Greater Than | `gt`, `greater`, `greater-than`, `greater?`, `bigger?` | 2 | :white_check_mark: | `a: num, b: num` => `a > b`
 | | | | | | | `a: str, b: num` => `a > str(b)`
 | | | | | | | `a: num, b: str` => `str(a) > b`
 | | | | | | | `a: str, b: str` => `a > b`
 <code>?</code> |  | Get Input | `get-input`, `input`, `stdin`, `readline` | 0 | :x: | `input`
 <code>A</code> |  | All Truthy / All() / Is Vowel? | `all`, `is-vowel?`, `vowel?` | 1 | :x: | `a: str` => `is (a) a vowel? vectorises for strings len > 1`
 | | | | | | | `a: list` => `is (a) all truthy?`
 <code>B</code> |  | Convert From Binary | `from-binary`, `bin->dec`, `bin->decimal` | 1 | :x: | `a: num` => `str(a) from binary`
 | | | | | | | `a: str` => `int(a, 2)`
 | | | | | | | `a: lst` => `int(a, 2), using list of digits`
 <code>C</code> |  | Count | `count` | 2 | :x: | `a: any, b: any` => `count(b in a)`
 <code>D</code> |  | Triplicate | `trip` | NA | :x: | `a` => `[a, a, a]`
 <code>E</code> |  | 2 Power / Evaluate | `two^`, `two**`, `eval` | 1 | :white_check_mark: | `a: num` => `2^a`
 | | | | | | | `a: str` => `evaluate (not execute) a`
 <code>F</code> |  | Filter by Function / From Base | `filter`, `keep-by`, `from-base`, `10->b` | 2 | :x: | `a: fun, b: lst` => `Filter b by truthy results of a`
 | | | | | | | `a: lst, b: fun` => `Filter a by truthy results of b`
 | | | | | | | `a: num, b: num` => `a from base b to base 10`
 | | | | | | | `a: num, b: str\|lst` => `a from base with alphabet b to base 10`
 <code>G</code> |  | Monadic Maximum / Dyadic Maximum / Generate From Function / Vectorised Maximum | `max`, `maximum`, `generator` | 2 | :x: | `a: lst` => `Maximum of a`
 | | | | | | | `a: non-lst, b: non-lst` => `Maximum of a and b`
 | | | | | | | `a: lst, b: fun` => `Call b infinitely with items of a as starting values`
 <code>H</code> |  | Hexadecimal / To Hexadecimal | `hex`, `hexadecimal`, `to-hex`, `to-hexadecimal` | 1 | :white_check_mark: | `a: num` => `a in hexadecimal`
 | | | | | | | `a: str` => `a as a hexadecimal number to base 10`
 <code>I</code> |  | Interleave / Reject By Function | `interleave`, `reject` | 2 | :x: | `a: lst, b: lst` => `Interleave a and b`
 | | | | | | | `a: any, b: fun` => `Reject elements of a by applying b`
 <code>J</code> |  | Merge | `merge` | 2 | :x: | `a: lst, b: lst` => `Merge a and b`
 | | | | | | | `a: any, b: lst` => `Prepend a to b`
 | | | | | | | `a: lst, b: any` => `Append b to a`
 | | | | | | | `a: num, b: num` => `num(str(a) + str(b))`
 | | | | | | | `a: any, b: any` => `str(a) + str(b)`
 <code>K</code> |  | Factors / Is Numeric? | `factors`, `divisors`, `is-numeric`, `is-num`, `is-number`, `is-num?`, `is-number?` | 1 | :white_check_mark: | `a: num` => `Factors of a`
 | | | | | | | `a: str` => `Is a numeric?`
 <code>L</code> |  | Length / Length of List | `length`, `len`, `length-of`, `len-of`, `size` | 1 | :x: | `a: any` => `Length of a`
 <code>M</code> |  | Map Function / Mold Lists / Multiplicity | `map`, `mold`, `multiplicity`, `times-divide` | 2 | :x: | `a: any, b: fun` => `a.map(b)`
 | | | | | | | `a: fun, b: any` => `b.map(a)`
 | | | | | | | `a: lst, b: lst` => `a molded to the shape of b`
 | | | | | | | `a: num, b: num` => `how many times b divides a`
 <code>N</code> |  | Negation / Swap Case / First Non-Negative Integer Where Predicate is True | `neg`, `negate`, `swap-case`, `caseswap`, `first-non-negative`, `first-nonneg`, `first>-1` | 1 | :white_check_mark: | `a: num` => `-a`
 | | | | | | | `a: str` => `a.swapCase()`
 | | | | | | | `a: fun` => `first non-negative integer where predicate a is true`
 <code>O</code> |  | Ord/Chr | `ord`, `chr` | 1 | :x: | `a: str` => `ord(a)`
 | | | | | | | `a: num` => `chr(a)`
 <code>P</code> |  | Prefixes | `prefixes` | 1 | :x: | `a: lst` => `Prefixes of a`
 <code>Q</code> |  | Remove At | `remove-at` | 2 | :x: | `a: lst, b: num` => `a with bth element removed`
 <code>R</code> |  | Reduce by Function Object / Dyadic Range / Regex Match / Set Union | `fun-reduce`, `reduce`, `fold-by`, `range`, `a->b`, `regex-match?`, `re-match?`, `has-regex-match?`, `fold`, `union` | 2 | :x: | `a: fun, b: any` => `reduce iterable b by function a`
 | | | | | | | `a: any, b: fun` => `reduce iterable a by function b`
 | | | | | | | `a: num, b: num` => `the range [a, b)`
 | | | | | | | `a: str, b: num\|str` => `does regex pattern b match haystack a?`
 | | | | | | | `a: lst, b: lst` => `union of a and b`
 <code>S</code> |  | Sort ascending | `sort`, `sortasc`, `sort-asc` | 1 | :x: | `a: any` => `convert to list and sort ascending`
 <code>T</code> |  | Triple / Contains Only Alphabet / Transpose | `triple`, `alphabet?`, `alphabetical?`, `contains-only-alphabet?`, `contains-only-alphabetical?`, `transpose`, `flip`, `reverse-axes`, `flip-axes`, `permute-axes` | 1 | :x: | `a: num` => `3 * a`
 | | | | | | | `a: str` => `does a contain only alphabet characters?`
 | | | | | | | `a: any` => `transpose a`
 <code>U</code> |  | Uninterleave | `uninterleave` | NA | :x: | `a: any` => `uninterleave a`
 <code>V</code> |  | Vectorised Reverse / Complement / Title Case | `vectorised-reverse`, `vec-reverse`, `complement`, `titlecase`, `title-case` | 1 | :x: | `a: lst` => `each element of a reversed`
 | | | | | | | `a: num` => `1 - a`
 | | | | | | | `a: str` => `a converted to title case`
 <code>W</code> |  | Wrap | `wrap` | NA | :x: | `a, b, c, ...,` => `[a, b, c, ...]`
 <code>X</code> |  | Return Statement | `return`, `ret` | NA | :x: | `a` => `return a`
 <code>Y</code> |  | List Repeat | `wrap-repeat` | 2 | :x: | `a: any, b: num` => `a repeated b times, wrapped in a list`
 | | | | | | | `a: num, b: any` => `b repeated a times, wrapped in a list`
 | | | | | | | `a: lst\|str, b: lst[num]` => `a[_] repeated b[_] times, wrapped in a list`
 <code>Z</code> |  | Zip | `zip`, `zip-map` | 2 | :x: | `a: lst, b: lst` => `zip a and b`
 | | | | | | | `a: lst, b: fun` => `[[x, b(x)] for x in a]`
 | | | | | | | `a: fun, b: lst` => `[[a(x), x] for x in b]`
 <code>\\</code> |  | Dump | `dump` | 1 | :x: | `a: any` => `dump all values on the stack`
 <code>_</code> |  | Pop and Discard | `pop`, `discard` | NA | :x: | `a` => ``
 <code>`</code> |  | Length of Stack | `length-of-stack`, `stack-length`, `stack-len` | NA | :x: | `push the length of the stack`
 <code>a</code> |  | Any Truthy / Any() / Is Uppercase? | `any`, `is-uppercase?`, `is-upper?`, `upper?` | 1 | :x: | `a: str` => `is (a) uppercase? vectorises for strings len > 1`
 | | | | | | | `a: list` => `is (a) any truthy?`
 <code>b</code> |  | Convert To Binary | `to-binary`, `dec->bin`, `decimal->bin` | 1 | :white_check_mark: | `a: num` => `convert a to binary`
 | | | | | | | `a: str` => `bin(ord(x) for x in a)`
 <code>c</code> |  | Contains | `contains`, `in` | 2 | :x: | `a: any, b: lst` => `is element a in list b?`
 | | | | | | | `a: any, b: any` => `is str(b) in str(a)?`
 <code>d</code> |  | Double | `double` | 1 | :white_check_mark: | `a: num` => `a * 2`
 | | | | | | | `a: str` => `a + a`
 <code>e</code> |  | Is Even / Split on Newlines | `even?`, `even`, `is-even?`, `split-on-newlines`, `newline-split`, `split-newlines` | 1 | :white_check_mark: | `a: num` => `a % 2 == 0`
 | | | | | | | `a: str` => `a split on newlines`
 <code>f</code> |  | Flatten | `flatten`, `flat` | 1 | :x: | `a: lst` => `Flattened a`
 <code>g</code> |  | Monadic Minimum / Dyadic Minimum / Generate From Function (Dyadic) / Vectorised Minimum | `min`, `minimum`, `generator-dyadic` | 2 | :x: | `a: lst` => `Minimum of a`
 | | | | | | | `a: non-lst, b: non-lst` => `Minimum of a and b`
 | | | | | | | `a: lst, b: fun` => `Call b infinitely with items of a as starting values (dyadic)`
 <code>h</code> |  | Head / First Item | `head`, `first`, `first-item` | 1 | :x: | `a: lst` => `a[0]`
 <code>i</code> |  | Index / Collect Unique Application Values / Enclose | `index`, `at`, `item-at`, `nth-item`, `collect-unique`, `enclose` | 2 | :x: | `a: lst, b: num` => `a[b]`
 | | | | | | | `a: lst, b: lst` => `a[_] for _ in b`
 | | | | | | | `a: str, b: lst` => `''.join(a[i] for i in b)`
 | | | | | | | `a: any, b: fun` => `Apply b on a and collect unique values. Does include the initial value.`
 | | | | | | | `a: str, b: str` => `enclose b in a (a[0:len(a)//2] + b + a[len(a)//2:])`
 <code>j</code> |  | Join On | `join-on`, `join`, `join-with`, `join-by` | 2 | :x: | `a: lst, b: str\|num` => `a join on b`
 | | | | | | | `a: lst, b: lst` => `Intersperse elements of b within a`
 <code>k1</code> |  | 1000 | `one-thousand`, `l000`, `lk` | 0 | :x: | `1000`
 <code>k2</code> |  | 10000 | `ten-thousand`, `l0000`, `l0k` | 0 | :x: | `10000`
 <code>k3</code> |  | 100000 | `one-hundered-thousand`, `l00000`, `l00k` | 0 | :x: | `100000`
 <code>k4</code> |  | 1000000 | `one-million`, `l000000`, `l000k`, `lm` | 0 | :x: | `1000000`
 <code>k6</code> |  | Hex Digits (lowercase) | `hex-digits`, `hex-digs`, `hex-lowercase`, `hex-lower`, `hex-l`, `hex-lc` | 0 | :x: | `"0123456789abcdef"`
 <code>kA</code> |  | Uppercase Alphabet | `uppercase-alphabet`, `uppercase-alpha`, `A->Z`, `A-Z`, `amazon` | 0 | :x: | `"ABCDEFGHIJKLMNOPQRSTUVWXYZ"`
 <code>kB</code> |  | Uppercase and lowercase | `uppercase-and-lowercase`, `uppercase-and-lowercase-alpha`, `A->Za->z`, `A-Za-z` | 0 | :x: | `"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"`
 <code>kF</code> |  | FizzBuzz | `fizzbuzz`, `FB` | 0 | :x: | `"FizzBuzz"`
 <code>kH</code> |  | Hello, World! | `hello-world!`, `HW!` | 0 | :x: | `"Hello, World!"`
 <code>kL</code> |  | Lowercase and Uppercase Alphabet | `lowercase-and-uppercase-alphabet`, `lowercase-and-uppercase-alpha`, `a->zA->Z`, `a-zA-Z` | 0 | :x: | `"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"`
 <code>kP</code> |  | Printable Ascii | `printable-ascii`, `all-ascii` | 0 | :x: | `All of printable ascci. That excludes newline`
 <code>kR</code> |  | Digits, Uppercase, Lowercase | `digits-uppercase-lowercase`, `digs-upper-lower`, `o9AZaz`, `o-9A-Za-z` | 0 | :x: | `"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"`
 <code>kZ</code> |  | Uppercase Alphabet Reversed | `uppercase-alphabet-reversed`, `uppercase-alpha-reversed`, `Z->A` | 0 | :x: | `"ZYXWVUTSRQPONMLKJIHGFEDCBA"`
 <code>k^</code> |  | Hex Digits (uppercase) | `hex-uppercase`, `hex-upper`, `hex-u`, `hex-uc` | 0 | :x: | `"0123456789ABCDEF"`
 <code>ka</code> |  | Lowercase Alphabet | `lowercase-alphabet`, `lowercase-alpha`, `a->z`, `a-z` | 0 | :x: | `"abcdefghijklmnopqrstuvwxyz"`
 <code>kb</code> |  | Buzz | `buzz`, `BUZZ` | 0 | :x: | `"Buzz"`
 <code>kd</code> |  | Digits | `digits`, `digs`, `o-9` | 0 | :x: | `"0123456789"`
 <code>ke</code> |  | Euler's Number | `euler's-number`, `euler`, `e-num` | 0 | :x: | `2.718281828459045`
 <code>kf</code> |  | Fizz | `fizz`, `FIZZ` | 0 | :x: | `"Fizz"`
 <code>kg</code> |  | Phi | `phi`, `golden-ratio`, `golden`, `l-618033988749895` | 0 | :x: | `Literally just phi`
 <code>kh</code> |  | Hello World | `hello-world`, `HW` | 0 | :x: | `"Hello World"`
 <code>ki</code> |  | Pi | `pi`, `E-14`, `E-1415926535897` | 0 | :x: | `Literally just pi`
 <code>kl</code> |  | Upper and Lowercase Alphabet Reversed | `upper-and-lowercase-alphabet-reversed`, `upper-and-lowercase-alpha-reversed`, `Z->Az->a`, `Z-Az-a` | 0 | :x: | `"ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba"`
 <code>ko</code> |  | Octal Digits | `octal-digits`, `octal-digs`, `o-7` | 0 | :x: | `"01234567"`
 <code>kp</code> |  | Punctuation | `punctuation`, `punct` | 0 | :x: | `All punctuation characters`
 <code>kr</code> |  | Digits, Lowercase, Uppercase | `digits-lowercase-uppercase`, `digs-lower-upper`, `o9azAZ`, `o-9a-zA-Z` | 0 | :x: | `"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"`
 <code>kz</code> |  | Lowercase Alphabet Reversed | `lowercase-alphabet-reversed`, `lowercase-alpha-reversed`, `z->a`, `nozama` | 0 | :x: | `"zyxwvutsrqponmlkjihgfedcba"`
 <code>l</code> |  | Length of Each Item | `length-vectorised`, `length-vect`, `len-vect`, `len-vectorised`, `vec-len`, `vec-length`, `vlen` | 1 | :x: | `a: lst` => `Length of each item in a`
 <code>m</code> |  | Get Context Variable M | `get-context-m`, `context-m`, `c-var-m`, `ctx-m`, `ctx-secondary` | 0 | :x: | `context variable m`
 <code>n</code> |  | Get Context Variable N | `get-context-n`, `context-n`, `c-var-n`, `ctx-n`, `ctx-primary` | 0 | :x: | `context variable n`
 <code>o</code> |  | Overlap / Overlapping Slices | `overlap`, `overlaps`, `overlapping`, `overlapping-slices` | 2 | :x: | `a: lst, b: num` => `Overlapping slices of a of length b`
 | | | | | | | `a: lst\|str` => `Overlapping slices of a of length 2`
 <code>p</code> |  | Prepend | `prepend` | 2 | :x: | `a: lst, b: any` => `b prepended to a`
 <code>q</code> |  | Quotify / Nth Prime | `quotify`, `nth-prime`, `prime-n` | 1 | :white_check_mark: | `a: str` => `enclose a in quotes, escape backslashes and quote marks`
 | | | | | | | `a: num` => `nth prime`
 <code>r</code> |  | Replace | `replace`, `zip-with` | 3 | :x: | `a: str, b: str, c: str` => `replace all instances of b in a with c`
 | | | | | | | `a: fun, b: any, c: any` => `reduce items in zip(b, c) by a`
 <code>s</code> |  | Split | `split` | 2 | :x: | `a: any, b: any` => `split a by b`
 <code>t</code> |  | Tail / Last Item | `tail`, `last`, `last-item` | 1 | :x: | `a: lst` => `a[-1]`
 <code>u</code> |  | Uniquify | `uniquify` | 1 | :x: | `a: lst\|str\|num` => `a with duplicates removed`
 <code>v</code> |  | Decrement | `decr`, `decrement` | 1 | :white_check_mark: | `a: num` => `a - 1`
 <code>w</code> |  | Wrap Singleton | `wrap-singleton`, `enlist` | 1 | :x: | `a` => `[a]`
 <code>x</code> |  | Recursion / Recurse | `recurse` | NA | :x: | `call the current function recursively`
 <code>y</code> |  | To Base | `to-base` | 2 | :x: | `a: num, b: num` => `a in base b`
 | | | | | | | `a: num, b: str\|lst` => `a in base with alphabet b`
 | | | | | | | `a: lst, b: num` => `each x in a in base b`
 | | | | | | | `a: lst, b: str\|lst` => `each x in a in base with alphabet b`
 <code>z</code> |  | Inclusive zero Range / Is Lowercase | `inclusive-zero-range`, `zero->n`, `is-lowercase?`, `lowercase?`, `lower?` | 1 | :white_check_mark: | `a: num` => `[0, 1, ..., a]`
 | | | | | | | `a: str` => `is a lowercase?`
 <code>Ȧ</code> | <code>#.A</code> | Absolute Value / Keep Alphabet Characters | `abs`, `absolute-value`, `keep-alphabet` | 1 | :white_check_mark: | `a: num` => `\|a\|`
 | | | | | | | `a: str` => `keep alphabet characters of a`
 <code>Ḃ</code> | <code>#.B</code> | Execute lambda without popping / Evaluate as Vyxal without popping / Boolean Mask / Is 1? | `peek-call`, `exec-peek`, `boolean-mask`, `bool-mask`, `strict-boolify`, `is-1?` | 1 | :x: | `a: fun` => `Execute a without popping`
 | | | | | | | `a: str` => `Evaluate a as Vyxal without popping`
 | | | | | | | `a: lst` => `Return a boolean array with 1s at the indices in a list.`
 | | | | | | | `a: num` => `Is a == 1?`
 <code>Ċ</code> | <code>#.C</code> | Set XOR | `set-xor` | 2 | :x: | `a: lst, b: lst` => `set xor of a and b`
 <code>Ḋ</code> | <code>#.D</code> | Divides? / Append Spaces / Remove Duplicates by Function | `divides?`, `+-spaces`, `dedup-by` | 2 | :x: | `a: num, b: num` => `a % b == 0`
 | | | | | | | `a: str, b: num` => `a + ' ' * b`
 | | | | | | | `a: num, b: str` => `b + ' ' * a`
 | | | | | | | `a: lst, b: fun` => `Remove duplicates from a by applying b to each element`
 <code>Ė</code> | <code>#.E</code> | Execute lambda / Evaluate as Vyxal / Power with base 10 | `execute-lambda`, `evaluate-as-vyxal`, `power-base-10`, `call`, `@` | 1 | :x: | `a: fun` => `Execute a`
 | | | | | | | `a: str` => `Evaluate a as Vyxal`
 | | | | | | | `a: num` => `10 ** n`
 <code>Ḟ</code> | <code>#.F</code> | Find | `find` | 2 | :x: | `a: any, b: any` => `a.indexOf(b) (-1 if not found)`
 | | | | | | | `a: any, b: fun` => `truthy indices of mapping b over a`
 <code>Ġ</code> | <code>#.G</code> | Group by Function Result / Greatest Common Divisor | `group-by`, `gcd` | 2 | :x: | `a: any, b: fun` => `group a by the results of b`
 | | | | | | | `a: fun, b: any` => `group b by the results of a`
 | | | | | | | `a: num, b: num` => `gcd(a, b)`
 | | | | | | | `a: lst[num], b: num` => `gcd of b and all elements of a`
 | | | | | | | `a: lst[num]` => `gcd of all items in a.`
 <code>Ḣ</code> | <code>#.H</code> | Head Remove / Behead | `head-remove`, `behead` | 1 | :x: | `a: str` => `a[1:]`
 | | | | | | | `a: any` => `toList(a)[1:]`
 <code>İ</code> | <code>#.I</code> | Index into Multiple / Collect While Unique / Complex Number | `index-into-multiple`, `collect-while-unique`, `complex` | 2 | :x: | `a: num, b: num` => `a.real + b.real * i`
 | | | | | | | `a: any, b: lst` => ``[a[item] for item in b]``
 | | | | | | | `a: any, b: fun` => `Apply b on a and collect unique values (until fixpoint). Does not include the initial value.`
 <code>Ŀ</code> | <code>#.L</code> | Logarithm / Scan Fixpoint / Same Length? / Length Equals? | `log`, `logarithm`, `scan-fixpoint`, `scan-fix`, `same-length?`, `same-length`, `length-equals?`, `length-equals`, `len-eq?` | 2 | :white_check_mark: | `a: num, b: num` => `log_b(a)`
 | | | | | | | `a: fun, b: any` => `apply until a previous value is repeated, collecting intermediate results`
 | | | | | | | `a: str, b: str` => `a same length as b`
 | | | | | | | `a: str, b: num` => `len(a) == b`
 <code>Ṁ</code> | <code>#.M</code> | Modular / Matrix Multiply / Regex Full Match? | `nth-items`, `modular`, `maxtrix-multiply`, `mat-multiply`, `mat-mul`, `regex-full-match?`, `full-match?` | 2 | :x: | `a: str\|lst, b: num` => `return every b-th element of a. If b is zero, mirror: prepend a to its reverse.`
 | | | | | | | `a: num, b: str\|lst` => `return every a-th element of b. If a is zero, mirror: append b to its reverse.`
 | | | | | | | `a: lst, b: lst` => `a * b (matrix multiply)`
 | | | | | | | `a: str, b: str` => `does the entirety of a match b?`
 <code>Ṅ</code> | <code>#.N</code> | Is Prime? / Quine Cheese | `prime?`, `quineify` | 1 | :white_check_mark: | `a: num` => `is a prime?`
 | | | | | | | `a: str` => `quote a and prepend to a`
 <code>Ȯ</code> | <code>#.O</code> | Over | `over` | 0 | :x: | `_` => `push a copy of the second item on the stack over the first`
 | | | | | | | `a b` => `a b a`
 <code>Ṗ</code> | <code>#.P</code> | Permutations | `permutations`, `perms` | 1 | :x: | `a: lst` => `Permutations of a`
 <code>Ṙ</code> | <code>#.R</code> | Rotate Left | `abc->bca`, `rot-left`, `rotate-left` | 1 | :x: | `a: any` => `rotate left once`
 <code>Ṡ</code> | <code>#.S</code> | Vectorised Sums / Integer Division | `vectorised-sums`, `vec-sums`, `integer-division`, `int-div`, `int-rizz` | 1 | :x: | `a: lst` => `sum of each element of a`
 | | | | | | | `a: num, b: num` => `a // b`
 <code>Ṫ</code> | <code>#.T</code> | Init | `init`, `remove-last` | 1 | :x: | `a: lst` => `a[:-1]`
 | | | | | | | `a: str` => `a[:-1]`
 <code>Ẇ</code> | <code>#.W</code> | Wrap to Length / Predicate Slice From 0 | `wrap-length`, `pred-slice-0`, `size-chunk` | 2 | :x: | `a: lst, b: num` => `a wrapped in chunks of length b`
 | | | | | | | `a: fun, b: num` => `first b truthy integers where a is truthy`
 <code>Ẋ</code> | <code>#.X</code> | Cartesian Product | `cartesian-product`, `cartesian`, `cart-prod`, `cart` | 2 | :x: | `a: list, b: list` => `cartesian product of a and b`
 <code>ι</code> |  | Length 0-Range | `zero->len` | 1 | :x: | `a: any` => ``[0, 1, 2, ..., len(a)-1]``
 <code>κ</code> |  | Length 1-Range | `one->len` | 1 | :x: | `a: any` => ``[1, 2, 3, ..., len(a)]``
 <code>ȧ</code> | <code>#.a</code> | Absolute Difference / Apply to Neighbours | `abs-diff`, `apply-to-neighbours` | 2 | :white_check_mark: | `a: num, b: num` => `\|a - b\|`
 | | | | | | | `a: lst, b: fun` => `apply b to each pair of neighbours in a [applies to windows of length 2]`
 <code>ḃ</code> |  | Bit / Parity / Last Half of String | `bit`, `parity`, `str-last-half` | 1 | :white_check_mark: | `a: num` => `parity of a (a % 2)`
 | | | | | | | `a: str` => `last half of a`
 <code>ċ</code> | <code>#.c</code> | N Choose K / Character Set Equal? / Repeat Until No Change | `n-choose-k`, `ncr`, `nck`, `choose`, `char-set-equal?`, `char-set-eq?`, `until-stable` | 2 | :white_check_mark: | `a: num, b: num` => `a choose b`
 | | | | | | | `a: str, b: str` => `are the character sets of a and b equal?`
 | | | | | | | `a: fun, b: any` => `run a on b until the result no longer changes returning all intermediate results`
 <code>ḋ</code> | <code>#.d</code> | Dot Product / To Bijective Base / First Index Where Predicate Truthy | `dot-product`, `bijective-base`, `dot-prod`, `first-index-where` | 2 | :x: | `a: lst, b: lst` => `Dot product of a and b`
 | | | | | | | `a: num, b: num` => `Convert a to bijective base b`
 <code>ė</code> | <code>#.e</code> | Reciprocal / Remove Whitespace | `reciprocal`, `recip`, `remove-whitespace`, `remove-space`, `1/` | 1 | :white_check_mark: | `a: num` => `1/a`
 | | | | | | | `a: str` => `a with all whitespace removed`
 <code>ḟ</code> | <code>#.f</code> | Prime Factors / Remove Alphabet | `prime-factors`, `remove-alphabet` | 1 | :white_check_mark: | `a: num` => `prime factors of a`
 | | | | | | | `a: str` => `a with all alphabet characters removed`
 <code>ġ</code> | <code>#.g</code> | Group By Consecutive Items | `group-by-consecutive` | 1 | :x: | `a: any` => `group consecutive identical items of lst(a)`
 <code>ḣ</code> | <code>#.h</code> | Head Extract | `head-extract`, `split-at-head` | 1 | :x: | `a: lst\|str` => `Push a[0], then a[1:] onto the stack`
 <code>ŀ</code> | <code>#.l</code> | Transliterate / Call While | `transliterate`, `call-while` | 3 | :x: | `any a, any b, any c` => `transliterate(a,b,c) (in a, replace b[0] with c[0], b[1] with c[1], b[2] with c[2], ...)`
 | | | | | | | `a: fun, b: fun, c: any` => `call b on c until a(c) is falsy`
 <code>ṁ</code> | <code>#.m</code> | Mirror | `mirror`, `ab->abba` | 1 | :x: | `num a: a + reversed(a) (as number)`
 | | | | | | | `str a: a + reversed(a)`
 | | | | | | | `lst a: append reversed(a) to a`
 <code>ṅ</code> | <code>#.n</code> | Palindromise | `palindromise`, `palindrome`, `ab->aba` | 1 | :x: | `a: any` => `palindromise a`
 <code>ȯ</code> | <code>#.o</code> | Boolify | `boolify` | 1 | :x: | `a: any` => `bool(a)`
 <code>ṗ</code> | <code>#.p</code> | List Partitions / Integer Partitions | `list-partitions`, `list-parts`, `integer-partitions`, `int-partitions`, `int-parts`, `partitions` | 1 | :x: | `a: lst` => `List partitions of a`
 | | | | | | | `a: num` => `Integer partitions of a (all possible ways to sum to a)`
 <code>ṙ</code> | <code>#.r</code> | Rotate Right | `abc->cab`, `rot-right`, `rotate-right` | 1 | :x: | `a: any` => `rotate right once`
 <code>ṡ</code> | <code>#.s</code> | Sort by Function Object / Partition by Numbers / Set Difference | `sort-by`, `sortby`, `sort-by-fun`, `sortbyfun`, `sort-fun`, `sortfun`, `partition-by`, `set-difference`, `set-diff` | 2 | :x: | `a: fun, b: any` => `sort iterable b by function a`
 | | | | | | | `a: any, b: fun` => `sort iterable a by function b`
 | | | | | | | `a: lst, b: lst` => `set difference`
 <code>ṫ</code> | <code>#.t</code> | Last Extract | `last-extract`, `split-at-last` | 1 | :x: | `a: lst\|str` => `Push a[-1], then a[:-1] onto the stack`
 <code>ẋ</code> | <code>#.x</code> | Cartesian Power / Regex Get Match | `cartesian-power`, `re-match`, `regex-match` | 2 | :x: | `a: lst, b: num` => `cart_prod([a] * n)`
 | | | | | | | `a: str, b: str` => `regex match of b in a`
 | | | | | | | `a: list, b: str` => `regex match of b of each element of a`
 | | | | | | | `a: str, b: list` => `regex match of each element of b in a`
 <code>ƒ</code> |  | Partition After Truthy Indices | `partition-after-truthy` | 2 | :x: | `a: lst, b: lst` => `partition a after truthy indices in b`
 <code>Θ</code> | <code>#.`</code> | Zero Slice Until | `0>b`, `zero-slice`, `zero-slice-until`, `take`, `slice-to`, `lst-truncate`, `first-n-items`, `first-n` | 2 | :x: | `a: lst, b: num>=0` => `[a[0], a[1], ..., a[b-1]]`
 | | | | | | | `a: lst, b: num<0` => `[a[b + 1], a[b + 2], ..., a[-1]]`
 | | | | | | | `a: lst, b: lst[num]` => `apl style take`
 <code>Φ</code> | <code>#.\|</code> | Slice from 1 | `1->b` | 2 | :x: | `a: lst, b: num` => `a[1:b]`
 | | | | | | | `a: num, b: lst` => `b[1:a]`
 <code>§</code> | <code>#,o</code> | Print without newline | `print-no-newline` | NA | :x: | `a` => `printed to stdout without newline`
 <code>Ạ</code> | <code>#,A</code> | Assign | `assign`, `assign-at`, `assign<>`, `assign<x>`, `a<x>=`, `a<x>=y`, `a<x>?=y`, `set-item`, `apply-at` | 3 | :x: | `a: lst, b: num, c: non-fun` => `assign c to a at the index b / a[b] = c`
 | | | | | | | `a: lst, b: num, c: fun` => `a[b] c= <stack items> (augmented assignment to list)`
 | | | | | | | `a: lst, b: lst, c: lst` => `assign c to a at the indices in b`
 <code>Ḅ</code> | <code>#,B</code> | Unique Prime Factors / Case Of | `unique-prime-factors`, `case-of` | 1 | :white_check_mark: | `a: num` => `unique prime factors of a`
 | | | | | | | `a: str` => `case of each character of a (uppercase = 1, lowercase = 0)`
 <code>Ḥ</code> | <code>#,H</code> | Head Extract Under | `head-extract-under`, `split-at-head-under`, `head-extract-swap`, `headless-swap`, `head-swap` | 1 | :x: | `a: lst\|str` => `Push a[1:], then a[0] onto the stack`
 <code>Ị</code> | <code>#,I</code> | Insert | `insert`, `insert-at` | 3 | :x: | `a: any, b: num, c: any` => `insert c at position b in a`
 | | | | | | | `a: any, b: lst, c: any` => `insert c at positions b in a`
 | | | | | | | `a: any, b: lst[num], c: lst` => `insert c[i] at position b[i] in a`
 <code>Ḷ</code> | <code>#,L</code> | Sort by Length | `sort-by-length`, `sort-by-len`, `order-by-length`, `order-by-len`, `length-sort`, `len-sort` | 1 | :x: | `a: lst` => `sort a by length`
 <code>Ṃ</code> | <code>#,M</code> | -1 Power Of / Split on Spaces | `neg-one-power-of`, `neg1**`, `neg1^`, `neg1-power-of`, `neg1-power`, `split-on-spaces`, `split-spaces`, `space-split` | 1 | :white_check_mark: | `a: num` => `-1 ** a`
 | | | | | | | `a: str` => `a split on spaces`
 <code>Ọ</code> | <code>#,O</code> | Print without popping | `print-no-pop` | NA | :x: | `a` => `printed to stdout without popping`
 <code>Ṛ</code> | <code>#,R</code> | Reverse | `reverse` | 1 | :x: | `a: any` => `reverse a`
 <code>Ṣ</code> | <code>#,S</code> | Sublists | `sublists` | 1 | :x: | `a: lst` => `sublists of a`
 <code>Ṭ</code> | <code>#,T</code> | Trim / Cumulative Reduce | `trim`, `scanl`, `cumulative-reduce` | 2 | :x: | `a: any, b: any` => `Trim all elements of b from both sides of a.`
 | | | | | | | `a: fun, b: any` => `cumulative reduce b by function a`
 <code>…</code> | <code>#..</code> | Increment Twice / Vectorised Head | `incr-twice`, `vec-head` | 1 | :x: | `a: num` => `a + 2`
 | | | | | | | `a: lst` => `[x[0] for x in a]`
 <code>≤</code> | <code>#,<</code> | Less Than Or Equal To | `le`, `less-than-or-equal-to` | 2 | :white_check_mark: | `a: num, b: num` => `a <= b`
 | | | | | | | `a: str, b: num` => `a <= str(b)`
 | | | | | | | `a: num, b: str` => `str(a) <= b`
 | | | | | | | `a: str, b: str` => `a <= b`
 <code>≥</code> | <code>#,></code> | Greater Than Or Equal To | `ge`, `greater-than-or-equal-to` | 2 | :white_check_mark: | `a: num, b: num` => `a >= b`
 | | | | | | | `a: str, b: num` => `a >= str(b)`
 | | | | | | | `a: num, b: str` => `str(a) >= b`
 | | | | | | | `a: str, b: str` => `a >= b`
 <code>≠</code> | <code>#.=</code> | Not Equal | `not-equal`, `=n't` | 2 | :x: | `a: any, b: any` => `a !== b (non-vectorising)`
 <code>₌</code> | <code>#,=</code> | Exactly Equals | `===`, `exactly-equal`, `strictly-equal?` | 2 | :x: | `a: any, b: any` => `a === b (non-vectorising)`
 <code>⁺</code> | <code>#^+</code> | Powerset | `powerset` | 1 | :x: | `a: lst` => `powerset of a`
 <code>⁻</code> | <code>#^-</code> | Cube / Threes | `cube`, `threes` | 1 | :white_check_mark: | `a: num` => `a ** 3`
 | | | | | | | `a: str` => `a split into chunks of length 3`
 <code>⁾</code> | <code>#^)</code> | Set Intersection / Flatten By Depth / Character Multiply | `set-intersection`, `intersection`, `flatten-by-depth`, `intersect` | 2 | :x: | `a: lst, b: lst` => `set intersection of a and b`
 | | | | | | | `a: str, b: str` => `set intersection of a and b`
 | | | | | | | `a: lst, b: num` => `flatten a by depth b`
 | | | | | | | `a: num, b: str` => `each character in b repeated a times`
 | | | | | | | `a: str, b: num` => `each character in a repeated b times`
 <code>√</code> | <code>#,*</code> | Square Root | `sqrt`, `square-root` | 1 | :white_check_mark: | `a: num` => `sqrt(a)`
 <code>∑</code> |  | Sum | `sum`, `/+`, `+/` | 1 | :x: | `a: lst` => `sum of a`
 <code>«</code> | <code>#.<</code> | Bitshift Left | `bitwise-left-shift`, `left-shift`, `left-pad`, `pad-left` | 2 | :white_check_mark: | `a: num, b: num` => `a << b`
 | | | | | | | `a: num, b: str` => `b padded to length a with spaces prepended`
 | | | | | | | `a: str, b: num` => `a padded to length b with spaces prepended`
 | | | | | | | `a: str, b: str` => `a padded to length of b with spaces prepended`
 <code>»</code> | <code>#.></code> | Bitshift Right | `bitwise-right-shift`, `right-shift`, `right-pad`, `pad-right` | 2 | :white_check_mark: | `a: num, b: num` => `a >> b`
 | | | | | | | `a: num, b: str` => `b padded to length a with spaces appended`
 | | | | | | | `a: str, b: num` => `a padded to length b with spaces appended`
 | | | | | | | `a: str, b: str` => `a padded to length of b with spaces appended`
 <code>⌐</code> | <code>#.!</code> | Bitwise Not | `bitwise-not` | 1 | :white_check_mark: | `a: num` => `~a`
 <code>∴</code> | <code>#.:</code> | Bitwise And | `bitwise-and` | 2 | :white_check_mark: | `a: num, b: num` => `a & b`
 <code>∵</code> | <code>#,:</code> | Bitwise Or | `bitwise-or` | 2 | :white_check_mark: | `a: num, b: num` => `a \| b`
 <code>⊻</code> | <code>#,v</code> | Bitwise Xor | `bitwise-xor`, `insert-space` | 2 | :white_check_mark: | `a: num, b: num` => `a ^ b`
 | | | | | | | `a: str, b: str` => `a + space + b`
 <code>₀</code> | <code>#,0</code> | Ten | `ten`, `l0` | 0 | :x: | `10`
 <code>₁</code> | <code>#,1</code> | Sixteen | `sixteen`, `l6` | 0 | :x: | `16`
 <code>₂</code> | <code>#,2</code> | Twenty-six | `twenty-six`, `Z6`, `z6` | 0 | :x: | `26`
 <code>₃</code> | <code>#,3</code> | Thirty-two | `thirty-two`, `E2` | 0 | :x: | `32`
 <code>₄</code> | <code>#,4</code> | Sixty-four | `sixty-four`, `b4` | 0 | :x: | `64`
 <code>₅</code> | <code>#,5</code> | One hundred | `one-hundred`, `l00` | 0 | :x: | `100`
 <code>₆</code> | <code>#,6</code> | One hundred twenty-eight | `one-hundred-twenty-eight`, `l28` | 0 | :x: | `128`
 <code>₇</code> | <code>#,7</code> | Two hundred fifty-six | `two-hundred-fifty-six`, `Z56`, `z56` | 0 | :x: | `256`
 <code>₈</code> | <code>#,8</code> | -1 | `negative-one`, `neg-1`, `-1` | 0 | :x: | `-1`
 <code>₉</code> | <code>#,9</code> | Empty array | `empty-list`, `nil-list`, `new-list`, `<>` | 0 | :x: | `[]`
 <code>½</code> | <code>#.5</code> | Halve | `halve` | 1 | :white_check_mark: | `a: num` => `a / 2`
 | | | | | | | `a: str` => `a split into two pieces`
 <code>ʀ</code> | <code>#.~</code> | Exclusive Zero Range / Lowercase | `0->n`, `zero-range`, `lowered-range`, `to-lower`, `lower`, `lowercase` | 1 | :white_check_mark: | `a: num` => `[0..a)`
 | | | | | | | `a: str` => `a.lower()`
 <code>ɾ</code> | <code>#,~</code> | Inclusive One Range / Uppercase | `one->n`, `one-range`, `to-upper`, `upper`, `uppercase` | 1 | :white_check_mark: | `a: num` => `[1..a]`
 | | | | | | | `a: str` => `a.upper()`
 <code>¯</code> | <code>#^_</code> | Deltas | `deltas` | 1 | :x: | `a: lst` => `forward-differences of a`
 <code>×</code> | <code>#.*</code> | Multiplication | `mul`, `multiply`, `times`, `str-repeat`, `*`, `ring-trans` | 2 | :white_check_mark: | `a: num, b: num` => `a * b`
 | | | | | | | `a: num, b: str` => `b repeated a times`
 | | | | | | | `a: str, b: num` => `a repeated b times`
 | | | | | | | `a: str, b: str` => `ring translate a according to b`
 <code>÷</code> | <code>#./</code> | Divide / Split | `divide`, `div`, `str-split` | 2 | :white_check_mark: | `a: num, b: num` => `a / b`
 | | | | | | | `a: str, b: str` => `Split a on the regex b`
 <code>£</code> | <code>#^=</code> | Set Register | `set-register`, `->register`, `set-reg`, `->reg` | 1 | :x: | `a: any` => `register = a`
 <code>¥</code> | <code>#^$</code> | Get Register | `get-register`, `get-reg`, `register`, `<-register`, `<-reg` | NA | :x: | `push the value of the register`
 <code>←</code> | <code>#^<</code> | Rotate Stack Left | `rotate-stack-left` | NA | :x: | `rotate the entire stack left once`
 <code>↑</code> | <code>#^^</code> | Grade Up | `grade-up` | 1 | :x: | `a: any` => `indices that will sort a`
 <code>→</code> | <code>#^></code> | Rotate Stack Right | `rotate-stack-right` | NA | :x: | `rotate the entire stack right once`
 <code>↓</code> | <code>#^;</code> | Grade Down | `grade-down` | 1 | :x: | `a: any` => `indices that will reverse-sort a`
 <code>±</code> | <code>#,+</code> | Sign | `sign` | 1 | :white_check_mark: | `a: num` => `sign of a`
 <code>†</code> | <code>#.&</code> | Length of Consecutive Groups | `len-consecutive`, `gvl`, `gavel` | 1 | :x: | `a: any` => `lengths of consecutive groups of a`
 <code>Π</code> |  | Product | `product`, `prod` | 1 | :x: | `a: lst` => `product of a`
 <code>¬</code> | <code>#,!</code> | Logical Not | `non-vec-not`, `non-vec-logical-not` | 1 | :x: | `a: any` => `!a`
 <code>∧</code> | <code>#,&</code> | Logical And | `and`, `logical-and` | 2 | :white_check_mark: | `a: any, b: any` => `a && b`
 <code>∨</code> | <code>#,\|</code> | Logical Or | `or`, `logical-or` | 2 | :white_check_mark: | `a: any, b: any` => `a \|\| b`
 <code>⁰</code> | <code>#^0</code> | First Input | `first-input`, `input-0` | 0 | :x: | `The first input to the program`
 <code>¹</code> | <code>#^1</code> | Second Input | `second-input`, `input-1` | 0 | :x: | `The second input to the program`
 <code>²</code> | <code>#^2</code> | Square / Pairs | `square`, `pairs` | 1 | :white_check_mark: | `a: num` => `a ** 2`
 | | | | | | | `a: str` => `a split into pairs`
 <code>⌈</code> |  | Ceiling | `ceiling`, `ceil` | 1 | :white_check_mark: | `a: num` => `ceil(a)`
 <code>⌊</code> |  | Floor | `floor`, `str-num`, `str->num`, `str-to-num` | 1 | :white_check_mark: | `a: num` => `floor(a)`
 | | | | | | | `a: str` => `cast a to num by ignoring non-numeric digits. Returns 0 if there's no valid number`
 <code>Ɠ</code> | <code>#.9</code> | Maximum without popping | `max-no-pop` | 1 | :x: | `a: lst` => `max(a) without popping a`
 <code>ɠ</code> | <code>#.6</code> | Minimum without popping | `min-no-pop` | 1 | :x: | `a: lst` => `min(a) without popping a`
 <code>„</code> | <code>#,"</code> | Join on Spaces / Is Negative? (Used when not closing a string) | `space-join`, `join-on-spaces`, `is-negative?`, `negative?` | 1 | :x: | `a: lst` => `a join on spaces`
 | | | | | | | `a: num` => `a < 0`
 <code>”</code> | <code>#^'</code> | Join On Newlines / Pad Binary to Mod 8 / Context if 1 | `join-newlines`, `newline-join`, `join-on-newlines`, `binary-pad-8`, `bin-pad-8`, `one?->context`, `one?->n` | 1 | :x: | `a: lst` => `a join on newlines`
 | | | | | | | `a: str` => `a padded to a multiple of 8 with 0s`
 | | | | | | | `a: num` => `a if a == 1 push context variable n`
 <code>ð</code> | <code>#.b</code> | Space | `space` | 0 | :x: | `" "`
 <code>€</code> | <code>#^(</code> | Suffixes | `suffixes` | 1 | :x: | `a: lst` => `Suffixes of a`
 <code>“</code> | <code>#^"</code> | Join on Nothing / First Positive Integer / Is Alphanumeric | `nothing-join`, `concat-fold`, `join-on-nothing`, `empty-join`, `single-string`, `as-single-string`, `first-positive-integer`, `first-n>0`, `is-alphanumeric`, `is-alphanum`, `is-alnum` | 1 | :x: | `a: lst` => `a join on nothing`
 | | | | | | | `a: str` => `is a alphanumeric?`
 | | | | | | | `a: fun` => `First positive integer ([1, 2, 3, ...]) for which a returns true`
 <code>¶</code> | <code>#,␤</code> | Newline | `newline` | 0 | :x: | `chr(10)`
 <code>ᶿ</code> | <code>#^`</code> | Bifuricate | `bifuricate`, `bifur`, `bif`, `furry`, `uwu`, `dup-rev`, `dup-reverse`, `owo` | 1 | :x: | `a: lst` => `Push a, then push a reversed`
 <code>ᶲ</code> | <code>#^\|</code> | Stringify | `to-string`, `stringify`, `str` | 1 | :x: | `a: any` => `str(a)`
 <code>•</code> | <code>#,.</code> | Asterisk | `asterisk` | 0 | :x: | `"*"`
 <code>≈</code> | <code>#^~</code> | All Equal? | `all-equal`, `all-equal?` | 1 | :x: | `a: lst` => `are all elements of a equal?`
 <code>ꜝ</code> | <code>#^!</code> | Increment | `incr`, `increment` | 1 | :white_check_mark: | `a: num` => `a + 1`
 <code>#C</code> |  | Compress String Using Dictionary | `compress-dict`, `dict-comp`, `compress` | 1 | :x: | `a: str` => `compress a using the dictionary`
 <code>#Q</code> |  | Exit / Quit | `exit`, `quit` | NA | :x: | `a` => `Stop program execution`
 <code>#X</code> |  | Loop Break | `break` | 0 | :x: | `break out of the current loop`
 <code>#c</code> |  | Base-252 Compress String or Number | `compress-252`, `compress-b` | 1 | :white_check_mark: | `a: str` => `compress a using base 252`
 | | | | | | | `a: num` => `compress a using base 252`
 <code>#v</code> |  | [Internal Use] Vectorise (Element Form)  |  | NA | :x: | `*a, f` => `f vectorised over however many arguments in a. It is recommended to use the modifier instead`
 <code>#x</code> |  | Loop Continue | `continue` | 0 | :x: | `continue the current loop`
 <code>#~</code> |  | [Internal Use] Apply Without Popping (Element Form) |  | NA | :x: | `*a, f` => `f applied to the stack without popping items. Use the modifier instead.`
 <code>∆q</code> |  | Prime Exponents | `prime-exponents`, `prime-exps` | 1 | :white_check_mark: | `a: num` => `push a list of the power of each prime in the prime factors of a`
 <code>∆ḟ</code> |  | All Prime Exponents | `all-prime-exponents`, `all-prime-exps` | 1 | :white_check_mark: | `a: num` => `for all primes less than or equal to a, push the power of that prime in the factorisation of a`
 <code>ø⁾</code> |  | Surround | `surround` | 2 | :x: | `a: any, b: any` => `a prepended and appended to b`
 <code>Þ0</code> |  | Zero Pad | `zero-pad`, `pizza-tower` | 2 | :x: | `a: lst\|str, b: num` => `a padded with 0s to length b. Positive b prepends 0s, negative b appends 0s`
 | | | | | | | `a: lst\|str, b: lst\|str` => `a padded with 0s to length of b. Positive b prepends 0s, negative b appends 0s`
 <code>ÞO</code> |  | Grid Neighbours (Wrap Around) | `grid-neighbours-wrap`, `grid-neighbors-wrap`, `adjacent-cells-wrap`, `adj-cells-wrap`, `surrounding-cells-wrap` | 1 | :x: | `a: lst[lst]` => `Grid neighbours of a - up, down, left, right - wrapping around`
 | | | | | | | `a: lst[lst], b: num` => `Grid neighbours of a - right, down, left, up of a, wrapping around and start from direction b => 0: right, 1: down, 2: left, 3: up. Negative b does not include middle, positive b does`
 <code>ÞT</code> |  | Transpose Safe | `transpose-safe` | 1 | :x: | `a: any` => `transpose a`
 <code>Þo</code> |  | Grid Neighbours | `grid-neighbours`, `grid-neighbors`, `adjacent-cells`, `adj-cells`, `surrounding-cells` | 1 | :x: | `a: lst[lst]` => `Grid neighbours of a - right, down, left, up of a`
 | | | | | | | `a: lst[lst], b: num` => `Grid neighbours of a - right, down, left, up of a and start from direction b => 0: right, 1: down, 2: left, 3: up. Negative b does not include middle, positive b does`
 <code>ÞĊ</code> |  | Cycle / Is Positive? | `cycle`, `is-positive?`, `positive?`, `>0?` | 1 | :x: | `a: lst` => `a ++ a ++ a ++ ...`
 | | | | | | | `a: num` => `a > 0`
 <code>ÞȮ</code> |  | Grid Neighbours (Diagonals, Wrap Around) | `grid-neighbours-diagonals-wrap`, `grid-neighbors-diagonals-wrap`, `adjacent-cells-diagonals-wrap`, `adj-cells-diagonals-wrap`, `surrounding-cells-diagonals-wrap`, `eight-cells-wrap` | 1 | :x: | `a: lst[lst]` => `Grid neighbours of a - up, down, left, right, diagonals - wrapping around`
 | | | | | | | `a: lst[lst], b: num` => `Grid neighbours of a - right, down, left, up of a, wrapping around and start from direction b => 0: right, 1: down, 2: left, 3: up, 4: down-right, 5: up-left, 6: down-left, 7: up-left. Negative b does not include middle, positive b does`
 <code>ÞẊ</code> |  | Cartesian Product Unsafe | `cartesian-product-unsafe`, `cartesian-unsafe`, `cart-prod-unsafe`, `cart-unsafe` | 2 | :x: | `a: list, b: list` => `cartesian product of a and b in the standard order, but without accounting for infinite lists`
 <code>Þċ</code> |  | Multi-Set XOR | `multi-set-xor` | 2 | :x: | `a: lst, b: lst` => `multi-set xor of a and b`
 <code>Þṅ</code> |  | Multi-Set Difference | `multi-set-difference`, `multi-set-diff` | 2 | :x: | `a: lst, b: lst` => `multi-set difference of a and b`
 <code>Þȯ</code> |  | Grid Neighbours (Diagonals) | `grid-neighbours-diagonals`, `grid-neighbors-diagonals`, `adjacent-cells-diagonals`, `adj-cells-diagonals`, `surrounding-cells-diagonals`, `eight-cells` | 1 | :x: | `a: lst[lst]` => `Grid neighbours of a - up, down, left, right, diagonals`
 | | | | | | | `a: lst[lst], b: num` => `Grid neighbours of a - right, down, left, up of a and start from direction b => 0: right, 1: down, 2: left, 3: up, 4: down-right, 5: up-left, 6: down-left, 7: up-left. Negative b does not include middle, positive b does`
 <code>ÞṂ</code> |  | Matrix Inverse | `matrix-inverse` | 1 | :white_check_mark: | `a: lst[lst]` => `matrix inverse of a`
 <code>Þ⁾</code> |  | Multi-Set Intersection | `multi-set-intersection`, `multi-set-intersect` | 2 | :x: | `a: lst, b: lst` => `multi-set intersection of a and b`


## Modifiers

| Symbol | Trigraph | Name | Keywords | Arity | Description |
 --- | --- | --- | --- | --- | --- |
 <code>ᵃ</code> | <code>#^a</code> | Apply to Neighbours / Number of Truthy Elements | `apply-to-neighbours:`, `count-truthy:`, `apply-neighbours:`, `apply-to-neighbors:`, `apply-neighbors:`, `2lvf:`, `twolif:`, `to-pairs:`, `to-overlaps:`, `count:` | 1 | <pre>To each overlapping pair, reduce it by an element<br>Apply a dyadic element for all pairs of neighboring elements.<br>Count the number of truthy elements in a list under a mondaic element<br>ȧf<monad>: Count how many items in a list are truthy after applying f to each<br>ᵃf<dyad>: equivalent to pushing the function, then calling ȧ</pre> |
 <code>ᵇ</code> | <code>#^b</code> | Apply Without Popping / Remove Duplicates by | `without-popping:`, `peek:`, `dedup-by:`, `remove-duplicates-by:` | 1 | <pre>Apply a 2+ arity element to the stack without popping<br>Remove duplicates from a list by an element<br>ᵇf<dyadtriadtetrad>: apply f to the stack without popping<br>ᵇf<monad>: remove duplicates from a list by applying f to each pair of elements</pre> |
 <code>ᶜ</code> | <code>#^c</code> | Reduce Columns / Map Over Suffixes | `reduce-columns:`, `map-over-suffixes:`, `fold-cols:`, `foldl-cols:`, `fold-columns-by:`, `reduce-columns-by:`, `over-suffixes:` | 1 | <pre>Reduce columns of a 2d list by a function<br>Map an element over suffixes</pre> |
 <code>ᵈ</code> | <code>#^d</code> | Dyadic Single Element Lambda | `*2:` | 1 | <pre>Turn the next element (whether that be a structure/modifier/element) into a dyadic lambda<br>ᵈf: Push the equivalent of λ2f} to the stack</pre> |
 <code>ᵉ</code> | <code>#^e</code> | Dyadic Double Element Lambda | `**2:` | 2 | <pre>Turn the next two elements (whether that be a structure/modifier/element) into a dyadic lambda<br>ᵉfg: Push the equivalent of λ2fg} to the stack</pre> |
 <code>ᶠ</code> | <code>#^f</code> | Dyadic Triple Element Lambda | `***2:` | 3 | <pre>Turn the next three elements (whether that be a structure/modifier/element) into a dyadic lambda<br>ᶠfgh: Push the equivalent of λ2fgh} to the stack</pre> |
 <code>ᴳ</code> | <code></code> | Dyadic Quadruple Element Lambda | `****2:` | 4 | <pre>Turn the next four elements (whether that be a structure/modifier/element) into a dyadic lambda<br>ᵍfghi: Push the equivalent of λ2fghi} to the stack</pre> |
 <code>ᴴ</code> | <code>#^H</code> | Apply To Head | `apply-to-head:` | 1 | <pre>Apply element only to the head of list<br>ᴴf: Apply f to the head of the top of the stack</pre> |
 <code>ᶤ</code> | <code>#^i</code> | First Index Where | `first-index-where:`, `first-index-of:`, `ind-of:`, `find-by:` | 1 | <pre>Find the first index where an element is truthy<br>ᶤf: find the first index where f is truthy</pre> |
 <code>ᶨ</code> | <code>#^j</code> | Loop and Collect While Unique | `collect-while-unique:` | 1 | <pre>Loop and Collect While Unique<br>ᶨf: Loop and collect while unique</pre> |
 <code>ᵏ</code> | <code>#^k</code> | Key | `key:` | 1 | <pre>Map an element over the groups formed by identical items.<br>ᵏf: Map f over the groups formed by identical items</pre> |
 <code>ᶪ</code> | <code>#^l</code> | Loop While Unique | `loop-while-unique:` | 1 | <pre>Loop While Unique - similar to ᶨ, but doesn't collect<br>ᶪf: Loop while unique</pre> |
 <code>ᵐ</code> | <code>#^m</code> | Maximum By | `max-by:`, `maximum-by:` | 1 | <pre>Maximum By Element<br>ᵐf: Maximum of top of stack based on results of f</pre> |
 <code>ⁿ</code> | <code>#^n</code> | Minimum By | `min-by:`, `minimum-by:` | 1 | <pre>Minimum By Element<br>ᵐf: Minimum of top of stack based on results of f</pre> |
 <code>ᵒ</code> | <code>#^o</code> | Outer Product / Table | `outer-product:`, `table:` | 1 | <pre>Outer product<br>ᵒf: Pop two lists, then make a matrix from them by applying f to each pair of elements</pre> |
 <code>ᵖ</code> | <code>#^p</code> | Map Over Prefixes | `map-over-prefixes:`, `over-prefixes:` | 1 | <pre>Map an element over the prefixes of a list<br>ᵖf: Map f over prefixes</pre> |
 <code>ᴿ</code> | <code>#^R</code> | Apply to Register | `apply-to-register:`, `to-register:`, `to-reg:` | 1 | <pre>Apply a function to the register. Essentially, push<br>the register value to the stack, apply the function, and<br>then pop back into the register<br>ᴿf: Apply f to the register</pre> |
 <code>ᶳ</code> | <code>#^s</code> | Sort By | `sort-by:`, `scanl:` | 1 | <pre>Sort By Element / Scanl<br>ᶳf: Sort top of stack based on results of f<br>ᶳf: Cumulatively reduce a list of items</pre> |
 <code>ᵗ</code> | <code>#^t</code> | Map as Stacks | `vec-dump:`, `map-dump:` | 1 | <pre>Map a function over the top of the stack, treating each iteration<br>as if it were a stack of items. Essentially, dump before mapping<br></pre> |
 <code>ᵘ</code> | <code>#^u</code> | Collect Until No Change / Neighbours All Equal? | `collect-until-no-change:`, `until-stable:`, `stablise:`, `neighbours-equals:` | 1 | <pre>Run func on the prev result until the result no longer changes<br>returning all intermediate results<br>Given a dyadic function, apply the function to all overlapping pairs of elements<br>and test if all results are equal<br>ᵘf: Collect until no change</pre> |
 <code>ᵛ</code> | <code>#^v</code> | Vectorise | `vectorise:`, `vec:`, `v:` | 1 | <pre>Vectorises<br>ᵛf: f but vectorised</pre> |
 <code>ᵂ</code> | <code>#^W</code> | Dip | `dip:` | 1 | <pre>Stash the top of the stack temporarily, and then apply<br>the function. Finally, push the stashed value<br>ᵂf: pop M, apply f, push M</pre> |
 <code>ᵡ</code> | <code>#^X</code> | Scan Fixed Point | `scan-fix:` | 1 | <pre>Scan a function until it reaches a fixed point<br>ᵡf: scan f until a fixed point is reached / apply until a previous value is repeated, collecting intermediate results</pre> |
 <code>ᵞ</code> | <code>#^y</code> | Invariant Under? / Vertical Scan | `invariant-under:`, `vertical-scan:`, `vscan:`, `v-scan:`, `invariant?:`, `same?:` | 1 | <pre>Check if a function is invariant under a transformation / vertical scan<br>ᵞf: check if top of stack is invariant under a transformation<br>ᵞf: scanl columns by f</pre> |
 <code>ᶻ</code> | <code>#^z</code> | Zip With / Reject by | `zip-with:`, `zipwith:` | 1 | <pre><br>      Given a dyadic function, zip two lists and reduce each by f<br>       and then check if all results are equal.<br>      Given a monadic function, the inverse of monadic /.<br>      Filters where the function is falsey</pre> |
 <code>⸠</code> | <code>#^.</code> | Single Element Lambda | `*:` | 1 | <pre>Turn the next element (whether that be a structure/modifier/element) into a lambda<br>⸠f: Push the equivalent of λf} to the stack</pre> |
 <code>ϩ</code> | <code>#^:</code> | Double Element Lambda | `**:` | 2 | <pre>Turn the next two elements (whether that be a structure/modifier/element) into a lambda<br>ϩfg: Push the equivalent of λfg} to the stack</pre> |
 <code>э</code> | <code>#^%</code> | Triple Element Lambda | `***:` | 3 | <pre>Turn the next three elements (whether that be a structure/modifier/element) into a lambda<br>эfgh: Push the equivalent of λfgh} to the stack</pre> |
 <code>Ч</code> | <code>#^4</code> | Quadruple Element Lambda | `****:` | 4 | <pre>Turn the next four elements (whether that be a structure/modifier/element) into a lambda<br>Чfghi: Push the equivalent of λfghi} to the stack</pre> |
 <code>/</code> | <code></code> | Foldl / Reduce By / Filter by | `foldl:`, `reduce:`, `/:`, `fold:`, `reduceby:-` | 1 | <pre>Reduce a list by an element<br>/f: reduce by element f</pre> |
 <code>∥</code> | <code></code> | Parallel Apply | `parallel-apply:`, `para-apply:`, `paraply:`, `!!:` | 2 | <pre>Parallel apply two elements to the top of the stack<br></pre> |
 <code>∦</code> | <code></code> | Parallel Apply and Wrap | `parallel-apply-and-wrap:`, `para-apply-and-wrap:`, `<paraply>:`, `<!!>:` | 2 | <pre>Parallel apply two elements to the top of the stack<br>and wrap the result in a list<br></pre> |
 <code>¿</code> | <code>#.?</code> | Conditional Execution | `if-top:`, `if:` | 1 | <pre>Pop the top of the stack, and, if it's truthy,<br>apply a function</pre> |


## Syntax Features

| Symbol | Trigraph | Name | Description | Usage |
 --- | --- | --- | --- | --- |
 `ᶴ` |  | Two Character String | Push the next two characters as a string | <pre>ᶴ&lt;character&gt;&lt;character&gt;</pre> |
 `"` |  | Open/Close String | Open/close a string. If the string is closed, push it to the stack. Closes all string types | <pre>"string contents"</pre> |
 `'` |  | One Character String | Push the next character as a string | <pre>'&lt;character&gt;</pre> |
 `(` |  | For Loop | Open a for loop. For each item in the top of the stack, execute code, storing loop variable. | <pre>&lt;iterable&gt; (&lt;variable&gt;\|&lt;code&gt;}</pre> |
 `)` |  | Close Two Structures | Match and close two open structures. | <pre>&lt;structure open&gt;&lt;structure open&gt; &lt;code&gt; ) &lt;code not in structure&gt;</pre> |
 `.` |  | Decimal Separator | Used to separate the integer and fractional parts of a number | <pre>&lt;integer&gt;.&lt;fractional&gt;</pre> |
 `0` |  | Numeric Literal | The number 0 | <pre>0</pre> |
 `1` |  | Numeric Literal | The number 1 | <pre>1</pre> |
 `2` |  | Numeric Literal | The number 2 | <pre>2</pre> |
 `3` |  | Numeric Literal | The number 3 | <pre>3</pre> |
 `4` |  | Numeric Literal | The number 4 | <pre>4</pre> |
 `5` |  | Numeric Literal | The number 5 | <pre>5</pre> |
 `6` |  | Numeric Literal | The number 6 | <pre>6</pre> |
 `7` |  | Numeric Literal | The number 7 | <pre>7</pre> |
 `8` |  | Numeric Literal | The number 8 | <pre>8</pre> |
 `9` |  | Numeric Literal | The number 9 | <pre>9</pre> |
 `[` |  | Ternary Statement | Open a ternary statement. Pop condition, if truthy, run <ontrue>, else run <onfalse> | <pre>&lt;condition&gt; [&lt;ontrue&gt;\|&lt;onfalse&gt;}</pre> |
 `]` |  | Close All Structures | Match and close all open structures. | <pre>&lt;structure openers&gt;] &lt;code not in structure&gt;</pre> |
 `k` |  | Constant Digraphs | Used for constant-related digraphs | <pre>k&lt;character&gt;</pre> |
 `{` |  | While Loop | Open a while loop. While the top of the stack is truthy, execute code. | <pre>{&lt;condition&gt;\|&lt;code&gt;}</pre> |
 `\|` |  | Structure Branch | Delimit the next section in a structure. | <pre>&lt;structure open&gt; &lt;code&gt; \| &lt;code&gt; ...</pre> |
 `}` |  | Close A Structure | Match and close the nearest open structure. | <pre>&lt;structure open&gt; &lt;code&gt; } &lt;code not in structure&gt;</pre> |
 `~` |  | Two Byte Number | Push the next two bytes as a number, converted from bijective base 255 using the codepage | <pre>~&lt;character&gt;&lt;character&gt;</pre> |
 `Ḍ` | `#,D` | Open Decision Problem Structure | Open a decision problem structure. Returns whether an iterable has any items that match a predicate | <pre>Ḍ&lt;predicate&gt;\|&lt;container&gt; }</pre> |
 `Ṇ` | `#,N` | Generator Structure | Open a generator structure. Allows for generator expressions | <pre>Ṇ&lt;code&gt;\|&lt;initial vector&gt;}</pre> |
 `λ` | `#.{` | Open Lambda | Open a lambda. | <pre>λ&lt;parameters&gt;\|&lt;code&gt;}</pre> |
 `ƛ` | `#.[` | Open Map Lambda | Open a lambda that automatically maps its function to the top of the stack | <pre>ƛ&lt;code&gt;}</pre> |
 `Ω` | `#.(` | Open Filter Lambda | Open a lambda that automatically filters the top of the stack by its function | <pre>Ω&lt;code&gt;}</pre> |
 `₳` | `#,{` | Open Reduce/Accumulate Lambda | Open a lambda that automatically reduces/accumulates the top of the stack by its function | <pre>₳&lt;code&gt;}</pre> |
 `µ` | `#,(` | Open Sort Lambda | Open a lambda that automatically sorts the top of the stack by its function | <pre>µ&lt;code&gt;}</pre> |
 `¤` | `#.@` | Context Paramter Index | Index into the list of context parameters. | <pre>¤&lt;number&gt;</pre> |
 `ı` | `#.i` | Imaginary Number | Used to represent the imaginary unit | <pre>&lt;real&gt;ı&lt;imaginary&gt;</pre> |
 `„` | `#,"` | Base-252 Compressed String | Decompress and push a string, converted from a bijective base 252 number using the codepage | <pre>"&lt;compressed string&gt;„</pre> |
 `”` | `#^'` | Dictionary Compressed String | Decompress and push a string using SSS compression, shamelessly stolen from Jelly | <pre>"&lt;compressed string&gt;”</pre> |
 `“` | `#^"` | Base-252 Compressed Number | Decompress and push a number, converted from a bijective base 252 number using the codepage | <pre>"&lt;compressed number&gt;“</pre> |
 `#:[` |  | Variable Unpacking | Unpack the top of the stack into a list of variables. | <pre>#:[&lt;var&gt;\|&lt;var&gt;\|&lt;var&gt;]</pre> |
 `#` |  | Miscellaneous Digraphs | Used for miscellaneous digraphs | <pre>#&lt;character&gt;</pre> |
 `##` |  | Comment | Comment out the rest of the line | <pre>##&lt;comment&gt;</pre> |
 `#$` |  | Retrieve Variable | Push the value of a variable. | <pre>#$&lt;variable&gt;</pre> |
 `#=` |  | Assign Variable | Assign a variable to a value. | <pre>#=&lt;variable&gt;</pre> |
 `#>` |  | Augmented Assignment | Apply a function to a variable value and store the result in the same variable. | <pre>&lt;function&gt; #&gt; &lt;variable&gt;</pre> |
 `#[` |  | Open List | Open a list. Pushes the list to the stack when closed. | <pre>#[item\|item\|item#]</pre> |
 `#]` |  | Close List | Close a list. Pushes the list to the stack when closed. | <pre>#[item\|item\|item#]</pre> |
 `#{` |  | If/Elif/Else Statement | Open an if statement. Allows for if/elif/else statements | <pre>#{&lt;if condition&gt;\|&lt;code&gt;\|&lt;else if condition&gt;\|&lt;code&gt;\|&lt;else code&gt;}</pre> |
 `∆` | `#.\` | Mathematical Digraphs | Used for math-related digraphs | <pre>∆&lt;character&gt;</pre> |
 `ø` | `#,/` | String Digraphs | Used for string-related digraphs | <pre>ø&lt;character&gt;</pre> |
 `Þ` | `#.)` | List Digraphs | Used for list-related digraphs | <pre>Þ&lt;character&gt;</pre> |

