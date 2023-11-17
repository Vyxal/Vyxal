
# Information Tables

## Elements

| Symbol | Trigraph |  Name | Keywords | Arity | Vectorises | Overloads |
 --- | --- | --- | --- | --- | --- | --- |
 `🌮` |  | Taco | `taco` | NA | :x: | `very funky`
 `🍪` |  | Cookie | `cookie` | NA | :x: | `cookie.`
 `ඞ` |  | ඞ | `sus` | NA | :x: | `ඞ`
 `¦` | `#.;` | Pipe | `pipe` | 0 | :x: | `"\|"`
 `ᵛ` | `#^v` | Decrement | `decr`, `decrement` | 1 | :white_check_mark: | `a: num` => `a - 1`
 `!` |  | Factorial | `fact`, `factorial` | 1 | :white_check_mark: | `a: num` => `a!`
 `$` |  | Swap | `swap` | NA | :x: | `a, b` => `b, a`
 `%` |  | Modulo / String Formatting | `mod`, `modulo`, `str-format`, `format`, `%`, `strfmt` | 2 | :x: | `a: num, b: num` => `a % b`
 | | | | | | | `a: str, b: any` => `a.format(b) (replace %s with b if scalar value or each item in b if vector)`
 `&` |  | Append | `append` | 2 | :x: | `a: any, b: any` => `list(a) ++ [b]`
 `*` |  | Exponentation / Remove Nth Letter / Trim | `exp`, `**`, `pow`, `exponent`, `remove-letter`, `str-trim` | 2 | :white_check_mark: | `a: num, b: num` => `a ^ b`
 | | | | | | | `a: str, b: num` => `a with the bth letter removed`
 | | | | | | | `a: num, b: str` => `b with the ath letter removed`
 | | | | | | | `a: str, b: str` => `trim b from both sides of a`
 `+` |  | Addition | `add`, `+`, `plus` | 2 | :white_check_mark: | `a: num, b: num` => `a + b`
 | | | | | | | `a: num, b: str` => `a + b`
 | | | | | | | `a: str, b: num` => `a + b`
 | | | | | | | `a: str, b: str` => `a + b`
 `,` |  | Print | `print`, `puts`, `out`, `println` | NA | :x: | `a` => `printed to stdout`
 `-` |  | Subtraction | `sub`, `subtract`, `minus`, `str-remove`, `remove`, `str-remove-all`, `remove-all` | 2 | :white_check_mark: | `a: num, b: num` => `a - b`
 | | | | | | | `a: str, b: num` => `a + b '-'s (or '-'s + a if b < 0)`
 | | | | | | | `a: num, b: str` => `a '-'s + b (or b + '-'s if a < 0)`
 | | | | | | | `a: str, b: str` => `a with b removed`
 `:` |  | Duplicate | `dup` | NA | :x: | `a` => `a, a`
 `;` |  | Pair | `pair` | 2 | :x: | `a, b` => `[a, b]`
 `<` |  | Less Than | `lt`, `less`, `less-than`, `<`, `less?`, `smaller?` | 2 | :white_check_mark: | `a: num, b: num` => `a < b`
 | | | | | | | `a: str, b: num` => `a < str(b)`
 | | | | | | | `a: num, b: str` => `str(a) < b`
 | | | | | | | `a: str, b: str` => `a < b`
 `=` |  | Equals | `eq`, `==`, `equal`, `same?`, `equals?`, `equal?` | 2 | :white_check_mark: | `a: any, b: any` => `a == b`
 `>` |  | Greater Than | `gt`, `greater`, `greater-than`, `greater?`, `bigger?` | 2 | :white_check_mark: | `a: num, b: num` => `a > b`
 | | | | | | | `a: str, b: num` => `a > str(b)`
 | | | | | | | `a: num, b: str` => `str(a) > b`
 | | | | | | | `a: str, b: str` => `a > b`
 `?` |  | Get Input | `get-input`, `input`, `stdin`, `readline` | 0 | :x: | `input`
 `A` |  | All Truthy / All() / Is Vowel? | `all`, `is-vowel?`, `vowel?` | 1 | :x: | `a: str` => `is (a) a vowel? vectorises for strings len > 1`
 | | | | | | | `a: list` => `is (a) all truthy?`
 `B` |  | Convert From Binary | `from-binary`, `bin->dec`, `bin->decimal` | 1 | :x: | `a: num` => `str(a) from binary`
 | | | | | | | `a: str` => `int(a, 2)`
 | | | | | | | `a: lst` => `int(a, 2), using list of digits`
 `C` |  | Count | `count` | 2 | :x: | `a: any, b: any` => `count(b in a)`
 `D` |  | Triplicate | `trip` | NA | :x: | `a` => `[a, a, a]`
 `E` |  | 2 Power / Evaluate | `two^`, `two**`, `eval` | 1 | :white_check_mark: | `a: num` => `2^a`
 | | | | | | | `a: str` => `evaluate (not execute) a`
 `F` |  | Filter by Function / From Base | `filter`, `keep-by`, `from-base`, `10->b` | 2 | :x: | `a: fun, b: lst` => `Filter b by truthy results of a`
 | | | | | | | `a: lst, b: fun` => `Filter a by truthy results of b`
 | | | | | | | `a: num, b: num` => `a from base b to base 10`
 | | | | | | | `a: num, b: str\|lst` => `a from base with alphabet b to base 10`
 `G` |  | Monadic Maximum / Dyadic Maximum / Generate From Function / Vectorised Maximum | `max`, `maximum`, `generator` | 2 | :x: | `a: lst` => `Maximum of a`
 | | | | | | | `a: non-lst, b: non-lst` => `Maximum of a and b`
 | | | | | | | `a: lst, b: fun` => `Call b infinitely with items of a as starting values`
 `H` |  | Hexadecimal / To Hexadecimal | `hex`, `hexadecimal`, `to-hex`, `to-hexadecimal` | 1 | :white_check_mark: | `a: num` => `a in hexadecimal`
 | | | | | | | `a: str` => `a as a hexadecimal number to base 10`
 `I` |  | Interleave / Reject By Function | `interleave`, `reject` | 2 | :x: | `a: lst, b: lst` => `Interleave a and b`
 | | | | | | | `a: any, b: fun` => `Reject elements of a by applying b`
 `J` |  | Merge | `merge` | 2 | :x: | `a: lst, b: lst` => `Merge a and b`
 | | | | | | | `a: any, b: lst` => `Prepend a to b`
 | | | | | | | `a: lst, b: any` => `Append b to a`
 | | | | | | | `a: num, b: num` => `num(str(a) + str(b))`
 | | | | | | | `a: any, b: any` => `str(a) + str(b)`
 `K` |  | Factors / Is Numeric? | `factors`, `divisors`, `is-numeric`, `is-num`, `is-number`, `is-num?`, `is-number?` | 1 | :white_check_mark: | `a: num` => `Factors of a`
 | | | | | | | `a: str` => `Is a numeric?`
 `L` |  | Length / Length of List | `length`, `len`, `length-of`, `len-of`, `size` | 1 | :x: | `a: any` => `Length of a`
 `M` |  | Map Function / Mold Lists / Multiplicity | `map`, `mold`, `multiplicity`, `times-divide` | 2 | :x: | `a: any, b: fun` => `a.map(b)`
 | | | | | | | `a: fun, b: any` => `b.map(a)`
 | | | | | | | `a: lst, b: lst` => `a molded to the shape of b`
 | | | | | | | `a: num, b: num` => `how many times b divides a`
 `N` |  | Negation / Swap Case / First Non-Negative Integer Where Predicate is True | `neg`, `negate`, `swap-case`, `caseswap`, `first-non-negative`, `first-nonneg`, `first>-1` | 1 | :white_check_mark: | `a: num` => `-a`
 | | | | | | | `a: str` => `a.swapCase()`
 | | | | | | | `a: fun` => `first non-negative integer where predicate a is true`
 `O` |  | Ord/Chr | `ord`, `chr` | 1 | :x: | `a: str` => `ord(a)`
 | | | | | | | `a: num` => `chr(a)`
 `P` |  | Prefixes | `prefixes` | 1 | :x: | `a: lst` => `Prefixes of a`
 `Q` |  | Exit / Quit | `exit`, `quit` | NA | :x: | `a` => `Stop program execution`
 `R` |  | Reduce by Function Object / Dyadic Range / Regex Match | `fun-reduce`, `reduce`, `fold-by`, `range`, `a->b`, `regex-match?`, `re-match?`, `has-regex-match?`, `fold` | 2 | :x: | `a: fun, b: any` => `reduce iterable b by function a`
 | | | | | | | `a: any, b: fun` => `reduce iterable a by function b`
 | | | | | | | `a: num, b: num` => `the range [a, b)`
 | | | | | | | `a: str, b: num\|str` => `does regex pattern b match haystack a?`
 `S` |  | Sort ascending | `sort`, `sortasc`, `sort-asc` | 1 | :x: | `a: any` => `convert to list and sort ascending`
 `T` |  | Triple / Contains Only Alphabet / Transpose | `triple`, `alphabet?`, `alphabetical?`, `contains-only-alphabet?`, `contains-only-alphabetical?`, `transpose`, `flip`, `reverse-axes`, `flip-axes`, `permute-axes` | 1 | :x: | `a: num` => `3 * a`
 | | | | | | | `a: str` => `does a contain only alphabet characters?`
 | | | | | | | `a: any` => `transpose a`
 `U` |  | Uninterleave | `uninterleave` | NA | :x: | `a: any` => `uninterleave a`
 `V` |  | Vectorised Reverse / Complement / Title Case | `vectorised-reverse`, `vec-reverse`, `complement`, `titlecase`, `title-case` | 1 | :x: | `a: lst` => `each element of a reversed`
 | | | | | | | `a: num` => `1 - a`
 | | | | | | | `a: str` => `a converted to title case`
 `W` |  | Wrap | `wrap` | NA | :x: | `a, b, c, ...,` => `[a, b, c, ...]`
 `X` |  | Return Statement | `return`, `ret` | NA | :x: | `a` => `return a`
 `Y` |  | List Repeat | `wrap-repeat` | 2 | :x: | `a: any, b: num` => `a repeated b times, wrapped in a list`
 | | | | | | | `a: num, b: any` => `b repeated a times, wrapped in a list`
 | | | | | | | `a: lst\|str, b: lst[num]` => `a[_] repeated b[_] times, wrapped in a list`
 `Z` |  | Zip | `zip`, `zip-map` | 2 | :x: | `a: lst, b: lst` => `zip a and b`
 | | | | | | | `a: lst, b: fun` => `[[x, b(x)] for x in a]`
 | | | | | | | `a: fun, b: lst` => `[[a(x), x] for x in b]`
 `\` |  | Dump | `dump` | 1 | :x: | `a: any` => `dump all values on the stack`
 `_` |  | Pop and Discard | `pop`, `discard` | NA | :x: | `a` => ``
 `a` |  | Any Truthy / Any() / Is Uppercase? | `any`, `is-uppercase?`, `is-upper?`, `upper?` | 1 | :x: | `a: str` => `is (a) uppercase? vectorises for strings len > 1`
 | | | | | | | `a: list` => `is (a) any truthy?`
 `b` |  | Convert To Binary | `to-binary`, `dec->bin`, `decimal->bin` | 1 | :white_check_mark: | `a: num` => `convert a to binary`
 | | | | | | | `a: str` => `bin(ord(x) for x in a)`
 `c` |  | Contains | `contains`, `in` | 2 | :x: | `a: any, b: lst` => `is element a in list b?`
 | | | | | | | `a: any, b: any` => `is str(b) in str(a)?`
 `d` |  | Double | `double` | 1 | :white_check_mark: | `a: num` => `a * 2`
 | | | | | | | `a: str` => `a + a`
 `e` |  | Is Even / Split on Newlines | `even?`, `even`, `is-even?`, `split-on-newlines`, `newline-split`, `split-newlines` | 1 | :white_check_mark: | `a: num` => `a % 2 == 0`
 | | | | | | | `a: str` => `a split on newlines`
 `f` |  | Flatten | `flatten`, `flat` | 1 | :x: | `a: lst` => `Flattened a`
 `g` |  | Monadic Minimum / Dyadic Minimum / Generate From Function (Dyadic) / Vectorised Minimum | `min`, `minimum`, `generator-dyadic` | 2 | :x: | `a: lst` => `Minimum of a`
 | | | | | | | `a: non-lst, b: non-lst` => `Minimum of a and b`
 | | | | | | | `a: lst, b: fun` => `Call b infinitely with items of a as starting values (dyadic)`
 `h` |  | Head / First Item | `head`, `first`, `first-item` | 1 | :x: | `a: lst` => `a[0]`
 `i` |  | Index / Collect Unique Application Values / Enclose | `index`, `at`, `item-at`, `nth-item`, `collect-unique`, `enclose` | 2 | :x: | `a: lst, b: num` => `a[b]`
 | | | | | | | `a: lst, b: lst` => `a[_] for _ in b`
 | | | | | | | `a: str, b: lst` => `''.join(a[i] for i in b)`
 | | | | | | | `a: any, b: fun` => `Apply b on a and collect unique values. Does include the initial value.`
 | | | | | | | `a: str, b: str` => `enclose b in a (a[0:len(a)//2] + b + a[len(a)//2:])`
 `j` |  | Join On | `join-on`, `join`, `join-with`, `join-by` | 2 | :x: | `a: lst, b: str\|num` => `a join on b`
 | | | | | | | `a: lst, b: lst` => `Intersperse elements of b within a`
 `l` |  | Length of Each Item | `length-vectorised`, `length-vect`, `len-vect`, `len-vectorised`, `vec-len`, `vec-length`, `vlen` | 1 | :x: | `a: lst` => `Length of each item in a`
 `m` |  | Get Context Variable M | `get-context-m`, `context-m`, `c-var-m`, `ctx-m`, `ctx-secondary` | 0 | :x: | `context variable m`
 `n` |  | Get Context Variable N | `get-context-n`, `context-n`, `c-var-n`, `ctx-n`, `ctx-primary` | 0 | :x: | `context variable n`
 `o` |  | Overlap / Overlapping Slices | `overlap`, `overlaps`, `overlapping`, `overlapping-slices` | 2 | :x: | `a: lst, b: num` => `Overlapping slices of a of length b`
 | | | | | | | `a: lst\|str` => `Overlapping slices of a of length 2`
 `p` |  | Prepend | `prepend` | 2 | :x: | `a: lst, b: any` => `b prepended to a`
 `q` |  | Quotify | `quotify` | 1 | :x: | `a: any` => `enclose a in quotes, escape backslashes and quote marks`
 `r` |  | Replace | `replace`, `zip-with` | 3 | :x: | `a: str, b: str, c: str` => `replace all instances of b in a with c`
 | | | | | | | `a: fun, b: any, c: any` => `reduce items in zip(b, c) by a`
 `s` |  | Split | `split` | 2 | :x: | `a: any, b: any` => `split a by b`
 `t` |  | Tail / Last Item | `tail`, `last`, `last-item` | 1 | :x: | `a: lst` => `a[-1]`
 `u` |  | Uniquify | `uniquify` | 1 | :x: | `a: lst\|str\|num` => `a with duplicates removed`
 `w` |  | Wrap Singleton | `wrap-singleton`, `enlist` | 1 | :x: | `a` => `[a]`
 `x` |  | Recursion / Recurse | `recurse` | NA | :x: | `call the current function recursively`
 `y` |  | To Base | `to-base` | 2 | :x: | `a: num, b: num` => `a in base b`
 | | | | | | | `a: num, b: str\|lst` => `a in base with alphabet b`
 | | | | | | | `a: lst, b: num` => `each x in a in base b`
 | | | | | | | `a: lst, b: str\|lst` => `each x in a in base with alphabet b`
 `z` |  | Inclusive zero Range / Is Lowercase | `inclusive-zero-range`, `zero->n`, `is-lowercase?`, `lowercase?`, `lower?` | 1 | :white_check_mark: | `a: num` => `[0, 1, ..., a]`
 | | | | | | | `a: str` => `is a lowercase?`
 `Ȧ` | `#.A` | Absolute Value / Keep Alphabet Characters | `abs`, `absolute-value`, `keep-alphabet` | 1 | :white_check_mark: | `a: num` => `\|a\|`
 | | | | | | | `a: str` => `keep alphabet characters of a`
 `Ḃ` | `#.B` | Execute lambda without popping / Evaluate as Vyxal without popping / Boolean Mask / Is 1? | `peek-call`, `exec-peek`, `boolean-mask`, `bool-mask`, `strict-boolify`, `is-1?` | 1 | :x: | `a: fun` => `Execute a without popping`
 | | | | | | | `a: str` => `Evaluate a as Vyxal without popping`
 | | | | | | | `a: lst` => `Return a boolean array with 1s at the indices in a list.`
 | | | | | | | `a: num` => `Is a == 1?`
 `Ċ` | `#.C` | Cycle / Is Positive? | `cycle`, `is-positive?`, `positive?`, `>0?` | 1 | :x: | `a: lst` => `a ++ a ++ a ++ ...`
 | | | | | | | `a: num` => `a > 0`
 `Ḋ` | `#.D` | Divides? / Append Spaces / Remove Duplicates by Function | `divides?`, `+-spaces`, `dedup-by` | 2 | :x: | `a: num, b: num` => `a % b == 0`
 | | | | | | | `a: str, b: num` => `a + ' ' * b`
 | | | | | | | `a: num, b: str` => `b + ' ' * a`
 | | | | | | | `a: lst, b: fun` => `Remove duplicates from a by applying b to each element`
 `Ė` | `#.E` | Execute lambda / Evaluate as Vyxal / Power with base 10 | `execute-lambda`, `evaluate-as-vyxal`, `power-base-10`, `call`, `@` | 1 | :x: | `a: fun` => `Execute a`
 | | | | | | | `a: str` => `Evaluate a as Vyxal`
 | | | | | | | `a: num` => `10 ** n`
 `Ḟ` | `#.F` | Find | `find` | 2 | :x: | `a: any, b: any` => `a.indexOf(b) (-1 if not found)`
 | | | | | | | `a: any, b: fun` => `truthy indices of mapping b over a`
 `Ġ` | `#.G` | Group by Function Result | `group-by` | 2 | :x: | `a: any, b: fun` => `group a by the results of b`
 | | | | | | | `a: fun, b: any` => `group b by the results of a`
 `Ḣ` | `#.H` | Head Remove / Behead | `head-remove`, `behead` | 1 | :x: | `a: str` => `a[1:]`
 | | | | | | | `a: any` => `toList(a)[1:]`
 `İ` | `#.I` | Index into Multiple / Collect While Unique / Complex Number | `index-into-multiple`, `collect-while-unique`, `complex` | 2 | :x: | `a: num, b: num` => `a.real + b.real * i`
 | | | | | | | `a: any, b: lst` => ``[a[item] for item in b]``
 | | | | | | | `a: any, b: fun` => `Apply b on a and collect unique values (until fixpoint). Does not include the initial value.`
 `Ŀ` | `#.L` | Logarithm / Scan Fixpoint / Same Length? / Length Equals? | `log`, `logarithm`, `scan-fixpoint`, `scan-fix`, `same-length?`, `same-length`, `length-equals?`, `length-equals`, `len-eq?` | 2 | :white_check_mark: | `a: num, b: num` => `log_b(a)`
 | | | | | | | `a: fun, b: any` => `apply until a previous value is repeated, collecting intermediate results`
 | | | | | | | `a: str, b: str` => `a same length as b`
 | | | | | | | `a: str, b: num` => `len(a) == b`
 `Ṁ` | `#.M` | Modular / Matrix Multiply / Regex Full Match? | `nth-items`, `modular`, `maxtrix-multiply`, `mat-multiply`, `mat-mul`, `regex-full-match?`, `full-match?` | 2 | :x: | `a: str\|lst, b: num` => `return every b-th element of a. If b is zero, mirror: prepend a to its reverse.`
 | | | | | | | `a: num, b: str\|lst` => `return every a-th element of b. If a is zero, mirror: append b to its reverse.`
 | | | | | | | `a: lst, b: lst` => `a * b (matrix multiply)`
 | | | | | | | `a: str, b: str` => `does the entirety of a match b?`
 `Ṅ` | `#.N` | Join on Nothing / First Positive Integer / Is Alphanumeric | `nothing-join`, `concat-fold`, `join-on-nothing`, `empty-join`, `single-string`, `as-single-string`, `first-positive-integer`, `first-n>0`, `is-alphanumeric`, `is-alphanum`, `is-alnum` | 1 | :x: | `a: lst` => `a join on nothing`
 | | | | | | | `a: str` => `is a alphanumeric?`
 | | | | | | | `a: fun` => `First positive integer ([1, 2, 3, ...]) for which a returns true`
 `Ȯ` | `#.O` | Over | `over` | 0 | :x: | `_` => `push a copy of the second item on the stack over the first`
 | | | | | | | `a b` => `a b a`
 `Ṗ` | `#.P` | Permutations | `permutations`, `perms` | 1 | :x: | `a: lst` => `Permutations of a`
 `Ṙ` | `#.R` | Rotate Left | `abc->bca`, `rot-left`, `rotate-left` | 1 | :x: | `a: any` => `rotate left once`
 `Ṡ` | `#.S` | Vectorised Sums | `vectorised-sums`, `vec-sums` | 1 | :x: | `a: lst` => `sum of each element of a`
 `Ṫ` | `#.T` | Init | `init`, `remove-last` | 1 | :x: | `a: lst` => `a[:-1]`
 | | | | | | | `a: str` => `a[:-1]`
 `Ẇ` | `#.W` | Wrap to Length / Predicate Slice From 0 | `wrap-length`, `pred-slice-0` | 2 | :x: | `a: lst, b: num` => `a wrapped in chunks of length b`
 | | | | | | | `a: fun, b: num` => `first b truthy integers where a is truthy`
 `Ẋ` | `#.X` | Cartesian Product | `cartesian-product`, `cartesian`, `cart-prod`, `cart` | 2 | :x: | `a: list, b: list` => `cartesian product of a and b`
 `ι` |  | Length 0-Range | `zero->len` | 1 | :x: | `a: any` => ``[0, 1, 2, ..., len(a)-1]``
 `κ` |  | Lenght 1-Range | `one->len` | 1 | :x: | `a: any` => ``[1, 2, 3, ..., len(a)]``
 `ȧ` | `#.a` | Absolute Difference / Apply to Neighbours | `abs-diff`, `apply-to-neighbours` | 2 | :white_check_mark: | `a: num, b: num` => `\|a - b\|`
 | | | | | | | `a: lst, b: fun` => `apply b to each pair of neighbours in a [applies to windows of length 2]`
 `ḃ` |  | Bit / Parity / Last Half of String | `bit`, `parity`, `str-last-half` | 1 | :white_check_mark: | `a: num` => `parity of a (a % 2)`
 | | | | | | | `a: str` => `last half of a`
 `ċ` | `#.c` | N Choose K / Character Set Equal? / Repeat Until No Change | `n-choose-k`, `ncr`, `nck`, `choose`, `char-set-equal?`, `char-set-eq?`, `until-stable` | 2 | :white_check_mark: | `a: num, b: num` => `a choose b`
 | | | | | | | `a: str, b: str` => `are the character sets of a and b equal?`
 | | | | | | | `a: fun, b: any` => `run a on b until the result no longer changes returning all intermediate results`
 `ḋ` | `#.d` | Dot Product / To Bijective Base / First Index Where Predicate Truthy | `dot-product`, `bijective-base`, `dot-prod`, `first-index-where` | 2 | :x: | `a: lst, b: lst` => `Dot product of a and b`
 | | | | | | | `a: num, b: num` => `Convert a to bijective base b`
 `ė` | `#.e` | Reciprocal / Remove Whitespace | `reciprocal`, `recip`, `remove-whitespace`, `remove-space`, `1/` | 1 | :white_check_mark: | `a: num` => `1/a`
 | | | | | | | `a: str` => `a with all whitespace removed`
 `ḟ` | `#.f` | Prime Factors / Remove Non-Alphabet | `prime-factors`, `remove-non-alphabet` | 1 | :white_check_mark: | `a: num` => `prime factors of a`
 | | | | | | | `a: str` => `a with all non-alphabet characters removed`
 `ġ` | `#.g` | Group By Consecutive Items | `group-by-consecutive` | 1 | :x: | `a: any` => `group consecutive identical items of lst(a)`
 `ḣ` | `#.h` | Head Extract | `head-extract`, `split-at-head` | 1 | :x: | `a: lst\|str` => `Push a[0], then a[1:] onto the stack`
 `ŀ` | `#.l` | Transliterate / Call While | `transliterate`, `call-while` | 3 | :x: | `any a, any b, any c` => `transliterate(a,b,c) (in a, replace b[0] with c[0], b[1] with c[1], b[2] with c[2], ...)`
 | | | | | | | `a: fun, b: fun, c: any` => `call b on c until a(c) is falsy`
 `ṁ` | `#.m` | Mirror | `mirror` | 1 | :x: | `num a: a + reversed(a) (as number)`
 | | | | | | | `str a: a + reversed(a)`
 | | | | | | | `lst a: append reversed(a) to a`
 `ṅ` | `#.n` | Join On Newlines / Pad Binary to Mod 8 / Context if 1 | `join-newlines`, `newline-join`, `join-on-newlines`, `binary-pad-8`, `bin-pad-8`, `one?->context`, `one?->n` | 1 | :x: | `a: lst` => `a join on newlines`
 | | | | | | | `a: str` => `a padded to a multiple of 8 with 0s`
 | | | | | | | `a: num` => `a if a == 1 push context variable n`
 `ȯ` | `#.o` | Boolify | `boolify` | 1 | :x: | `a: any` => `bool(a)`
 `ṗ` | `#.p` | List Partitions / Integer Partitions | `list-partitions`, `list-parts`, `integer-partitions`, `int-partitions`, `int-parts` | 1 | :x: | `a: lst` => `List partitions of a`
 | | | | | | | `a: num` => `Integer partitions of a (all possible ways to sum to a)`
 `ṙ` | `#.r` | Rotate Right | `abc->cab`, `rot-right`, `rotate-right` | 1 | :x: | `a: any` => `rotate right once`
 `ṡ` | `#.s` | Sort by Function Object / Partition by Numbers | `sort-by`, `sortby`, `sort-by-fun`, `sortbyfun`, `sort-fun`, `sortfun`, `partition-by` | 2 | :x: | `a: fun, b: any` => `sort iterable b by function a`
 | | | | | | | `a: any, b: fun` => `sort iterable a by function b`
 | | | | | | | `a: lst, b: lst[num]` => `partition a into sublists of length items in b`
 `ṫ` | `#.t` | Last Extract | `last-extract`, `split-at-last` | 1 | :x: | `a: lst\|str` => `Push a[-1], then a[:-1] onto the stack`
 `ẋ` | `#.x` | Cartesian Power | `cartesian-power` | 2 | :x: | `a: lst, b: num` => `cart_prod([a] * n)`
 `ƒ` |  | Partition After Truthy Indices | `partition-after-truthy` | 2 | :x: | `a: lst, b: lst` => `partition a after truthy indices in b`
 `Θ` | `#.`` | Zero Slice Until | `0>b`, `zero-slice`, `zero-slice-until`, `take`, `slice-to`, `lst-truncate`, `first-n-items`, `first-n` | 2 | :x: | `a: lst, b: num` => `[a[0], a[1], ..., a[b-1]]`
 `Φ` | `#.\|` | Slice from 1 | `1->b` | 2 | :x: | `a: lst, b: num` => `a[1:b]`
 | | | | | | | `a: num, b: lst` => `b[1:a]`
 `§` | `#,o` | Print without newline | `print-no-newline` | NA | :x: | `a` => `printed to stdout without newline`
 `Ạ` | `#,A` | Assign | `assign`, `assign-at`, `assign<>`, `assign<x>`, `a<x>=`, `a<x>=y`, `a<x>?=y`, `set-item`, `apply-at` | 3 | :x: | `a: lst, b: num, c: non-fun` => `assign c to a at the index b / a[b] = c`
 | | | | | | | `a: lst, b: num, c: fun` => `a[b] c= <stack items> (augmented assignment to list)`
 | | | | | | | `a: lst, b: lst, c: lst` => `assign c to a at the indices in b`
 `Ḅ` | `#,B` | Unique Prime Factors / Case Of | `unique-prime-factors`, `case-of` | 1 | :white_check_mark: | `a: num` => `unique prime factors of a`
 | | | | | | | `a: str` => `case of each character of a (uppercase = 1, lowercase = 0)`
 `Ḥ` | `#,H` | Head Extract | `head-extract-swap`, `split-at-head-swap` | 1 | :x: | `a: lst\|str` => `Push a[1:], then a[0] onto the stack`
 `Ị` | `#,I` | Insert | `insert`, `insert-at` | 3 | :x: | `a: any, b: num, c: any` => `insert c at position b in a`
 | | | | | | | `a: any, b: lst, c: any` => `insert c at positions b in a`
 | | | | | | | `a: any, b: lst[num], c: lst` => `insert c[i] at position b[i] in a`
 `Ḷ` | `#,L` | Sort by Length | `sort-by-length`, `sort-by-len`, `order-by-length`, `order-by-len`, `length-sort`, `len-sort` | 1 | :x: | `a: lst` => `sort a by length`
 `Ṃ` | `#,M` | Bit Length / Matrix Inverse | `bit-length`, `matrix-inverse` | 1 | :white_check_mark: | `a: num` => `bit length of a`
 | | | | | | | `a: lst[lst]` => `matrix inverse of a`
 `Ọ` | `#,O` | Print without popping | `print-no-pop` | NA | :x: | `a` => `printed to stdout without popping`
 `Ṛ` | `#,R` | Reverse | `reverse` | 1 | :x: | `a: any` => `reverse a`
 `Ṣ` | `#,S` | Sublists | `sublists` | 1 | :x: | `a: lst` => `sublists of a`
 `Ṭ` | `#,T` | Trim / Cumulative Reduce | `trim`, `scanl`, `cumulative-reduce` | 2 | :x: | `a: any, b: any` => `Trim all elements of b from both sides of a.`
 | | | | | | | `a: fun, b: any` => `cumulative reduce b by function a`
 `…` | `#..` | Increment Twice / Vectorised Head | `incr-twice`, `vec-head` | 1 | :x: | `a: num` => `a + 2`
 | | | | | | | `a: lst` => `[x[0] for x in a]`
 `≤` | `#,<` | Less Than Or Equal To | `le`, `less-than-or-equal-to` | 2 | :white_check_mark: | `a: num, b: num` => `a <= b`
 | | | | | | | `a: str, b: num` => `a <= str(b)`
 | | | | | | | `a: num, b: str` => `str(a) <= b`
 | | | | | | | `a: str, b: str` => `a <= b`
 `≥` | `#,>` | Greater Than Or Equal To | `ge`, `greater-than-or-equal-to` | 2 | :white_check_mark: | `a: num, b: num` => `a >= b`
 | | | | | | | `a: str, b: num` => `a >= str(b)`
 | | | | | | | `a: num, b: str` => `str(a) >= b`
 | | | | | | | `a: str, b: str` => `a >= b`
 `≠` | `#.=` | Not Equal | `not-equal`, `=n't` | 2 | :x: | `a: any, b: any` => `a !== b (non-vectorising)`
 `₌` | `#,=` | Exactly Equals | `===`, `exactly-equal`, `strictly-equal?` | 2 | :x: | `a: any, b: any` => `a === b (non-vectorising)`
 `⁺` | `#^+` | Square / Pairs | `square`, `pairs` | 1 | :white_check_mark: | `a: num` => `a ** 2`
 | | | | | | | `a: str` => `a split into pairs`
 `⁻` | `#^-` | Cube / Threes | `cube`, `threes` | 1 | :white_check_mark: | `a: num` => `a ** 3`
 | | | | | | | `a: str` => `a split into chunks of length 3`
 `⁾` | `#^)` | Surround / Character Multiply | `surround`, `character-multiply` | 2 | :x: | `a: num, b: str` => `each character in b repeated a times`
 | | | | | | | `a: any, b: any` => `a prepended and appended to b`
 `√` | `#,*` | Square Root | `sqrt`, `square-root` | 1 | :white_check_mark: | `a: num` => `sqrt(a)`
 `∑` |  | Sum | `sum`, `/+`, `+/` | 1 | :x: | `a: lst` => `sum of a`
 `«` | `#.<` | Bitshift Left | `bitwise-left-shift`, `left-shift` | 2 | :white_check_mark: | `a: num, b: num` => `a << b`
 `»` | `#.>` | Bitshift Right | `bitwise-right-shift`, `right-shift` | 2 | :white_check_mark: | `a: num, b: num` => `a >> b`
 `⌐` | `#.!` | Bitwise Not | `bitwise-not` | 1 | :white_check_mark: | `a: num` => `~a`
 `∴` | `#.:` | Bitwise And | `bitwise-and` | 2 | :white_check_mark: | `a: num, b: num` => `a & b`
 `∵` | `#,:` | Bitwise Or | `bitwise-or` | 2 | :white_check_mark: | `a: num, b: num` => `a \| b`
 `⊻` | `#,v` | Bitwise Xor | `bitwise-xor` | 2 | :white_check_mark: | `a: num, b: num` => `a ^ b`
 `₀` | `#,0` | Ten | `ten` | 0 | :x: | `10`
 `₁` | `#,1` | Sixteen | `sixteen` | 0 | :x: | `16`
 `₂` | `#,2` | Twenty-six | `twenty-six` | 0 | :x: | `26`
 `₃` | `#,3` | Thirty-two | `thirty-two` | 0 | :x: | `32`
 `₄` | `#,4` | Sixty-four | `sixty-four` | 0 | :x: | `64`
 `₅` | `#,5` | One hundred | `one-hundred` | 0 | :x: | `100`
 `₆` | `#,6` | One hundred twenty-eight | `one-hundred-twenty-eight` | 0 | :x: | `128`
 `₇` | `#,7` | Two hundred fifty-six | `two-hundred-fifty-six` | 0 | :x: | `256`
 `₈` | `#,8` | Alphabet | `alphabet`, `a-z` | 0 | :x: | `"abcdefghijklmnopqrstuvwxyz"`
 `₉` | `#,9` | Empty array | `empty-list`, `nil-list`, `new-list` | 0 | :x: | `[]`
 `½` | `#.5` | Halve | `halve` | 1 | :white_check_mark: | `a: num` => `a / 2`
 | | | | | | | `a: str` => `a split into two pieces`
 `ʀ` | `#.~` | Exclusive Zero Range / Lowercase | `0->n`, `zero-range`, `lowered-range`, `to-lower`, `lower`, `lowercase` | 1 | :white_check_mark: | `a: num` => `[0..a)`
 | | | | | | | `a: str` => `a.lower()`
 `ɾ` | `#,~` | Inclusive One Range / Uppercase | `one->n`, `one-range`, `to-upper`, `upper`, `uppercase` | 1 | :white_check_mark: | `a: num` => `[1..a]`
 | | | | | | | `a: str` => `a.upper()`
 `¯` | `#^_` | Deltas | `deltas` | 1 | :x: | `a: lst` => `forward-differences of a`
 `×` | `#.*` | Multiplication | `mul`, `multiply`, `times`, `str-repeat`, `*`, `ring-trans` | 2 | :white_check_mark: | `a: num, b: num` => `a * b`
 | | | | | | | `a: num, b: str` => `b repeated a times`
 | | | | | | | `a: str, b: num` => `a repeated b times`
 | | | | | | | `a: str, b: str` => `ring translate a according to b`
 `÷` | `#./` | Divide / Split | `divide`, `div`, `str-split` | 2 | :white_check_mark: | `a: num, b: num` => `a / b`
 | | | | | | | `a: str, b: str` => `Split a on the regex b`
 `£` | `#^=` | Set Register | `set-register`, `->register`, `set-reg`, `->reg` | 1 | :x: | `a: any` => `register = a`
 `¥` | `#^$` | Get Register | `get-register`, `get-reg`, `register`, `<-register`, `<-reg` | NA | :x: | `push the value of the register`
 `←` | `#^<` | Rotate Stack Left | `rotate-stack-left` | NA | :x: | `rotate the entire stack left once`
 `↑` | `#^^` | Grade Up | `grade-up` | 1 | :x: | `a: any` => `indices that will sort a`
 `→` | `#^>` | Rotate Stack Right | `rotate-stack-right` | NA | :x: | `rotate the entire stack right once`
 `↓` | `#^;` | Grade Down | `grade-down` | 1 | :x: | `a: any` => `indices that will reverse-sort a`
 `±` | `#,+` | Sign | `sign` | 1 | :white_check_mark: | `a: num` => `sign of a`
 `†` | `#.&` | Length of Consecutive Groups | `len-consecutive`, `gvl`, `gavel` | 1 | :x: | `a: any` => `lengths of consecutive groups of a`
 `Π` |  | Product | `product`, `prod` | 1 | :x: | `a: lst` => `product of a`
 `¬` | `#,!` | Logical Not | `non-vec-not`, `non-vec-logical-not` | 1 | :x: | `a: any` => `!a`
 `∧` | `#,&` | Logical And | `and`, `logical-and` | 2 | :white_check_mark: | `a: any, b: any` => `a && b`
 `∨` | `#,\|` | Logical Or | `or`, `logical-or` | 2 | :white_check_mark: | `a: any, b: any` => `a \|\| b`
 `⁰` | `#^0` | First Input | `first-input`, `input-0` | 0 | :x: | `The first input to the program`
 `¹` | `#^1` | Second Input | `second-input`, `input-1` | 0 | :x: | `The second input to the program`
 `²` | `#^2` | Third Input | `third-input`, `input-2` | 0 | :x: | `The third input to the program`
 `⌈` |  | Ceiling | `ceiling`, `ceil` | 1 | :white_check_mark: | `a: num` => `ceil(a)`
 `⌊` |  | Floor | `floor` | 1 | :white_check_mark: | `a: num` => `floor(a)`
 `Ɠ` | `#.9` | Maximum without popping | `max-no-pop` | 1 | :x: | `a: lst` => `max(a) without popping a`
 `ɠ` | `#.6` | Minimum without popping | `min-no-pop` | 1 | :x: | `a: lst` => `min(a) without popping a`
 `ð` | `#.b` | Space | `space` | 0 | :x: | `" "`
 `€` | `#^(` | Suffixes | `suffixes` | 1 | :x: | `a: lst` => `Suffixes of a`
 `¶` | `#,
` | Newline | `newline` | 0 | :x: | `chr(10)`
 `ᶿ` | `#^`` | Cartesian Product Unsafe | `cartesian-product-unsafe`, `cartesian-unsafe`, `cart-prod-unsafe`, `cart-unsafe` | 2 | :x: | `a: list, b: list` => `cartesian product of a and b in the standard order, but without accounting for infinite lists`
 `ᶲ` | `#^\|` | Stringify | `to-string`, `stringify`, `str` | 1 | :x: | `a: any` => `str(a)`
 `•` | `#,.` | Asterisk | `asterisk` | 0 | :x: | `"*"`
 `≈` | `#^~` | All Equal? | `all-equal`, `all-equal?` | 1 | :x: | `a: lst` => `are all elements of a equal?`
 `ꜝ` | `#^!` | Increment | `incr`, `increment` | 1 | :white_check_mark: | `a: num` => `a + 1`
 `#C` |  | Compress String Using Dictionary | `compress-dict`, `dict-comp`, `compress` | 1 | :x: | `a: str` => `compress a using the dictionary`
 `#X` |  | Loop Break | `break` | 0 | :x: | `break out of the current loop`
 `#v` |  | [Internal Use] Vectorise (Element Form)  |  | NA | :x: | `*a, f` => `f vectorised over however many arguments in a. It is recommended to use the modifier instead`
 `#x` |  | Loop Continue | `continue` | 0 | :x: | `continue the current loop`
 `#~` |  | [Internal Use] Apply Without Popping (Element Form) |  | NA | :x: | `*a, f` => `f applied to the stack without popping items. Use the modifier instead.`
 `ÞT` |  | Transpose Safe | `transpose-safe` | 1 | :x: | `a: any` => `transpose a`


## Modifiers

| Symbol | Trigraph | Name | Keywords | Arity | Description |
 --- | --- | --- | --- | --- | --- |
 `ᵃ` | `#^a` | Apply to Neighbours / Number of Truthy Elements | `apply-to-neighbours:`, `count-truthy:`, `apply-neighbours:`, `apply-to-neighbors:`, `apply-neighbors:`, `2lvf:`, `twolif:`, `to-pairs:`, `to-overlaps:`, `count:` | 1 | <pre>To each overlapping pair, reduce it by an element<br>Apply a dyadic element for all pairs of neighboring elements.<br>Count the number of truthy elements in a list under a mondaic element<br>ȧf<monad>: Count how many items in a list are truthy after applying f to each<br>ᵃf<dyad>: equivalent to pushing the function, then calling ȧ</pre> |
 `ᵇ` | `#^b` | Apply Without Popping / Remove Duplicates by | `without-popping:`, `peek:`, `dedup-by:`, `remove-duplicates-by:` | 1 | <pre>Apply a 2+ arity element to the stack without popping<br>Remove duplicates from a list by an element<br>ᵇf<dyadtriadtetrad>: apply f to the stack without popping<br>ᵇf<monad>: remove duplicates from a list by applying f to each pair of elements</pre> |
 `ᶜ` | `#^c` | Reduce Columns / Map Over Suffixes | `reduce-columns:`, `map-over-suffixes:`, `fold-cols:`, `foldl-cols:`, `fold-columns-by:`, `reduce-columns-by:`, `over-suffixes:` | 1 | <pre>Reduce columns of a 2d list by a function<br>Map an element over suffixes</pre> |
 `ᵈ` | `#^d` | Dyadic Single Element Lambda | `*2:` | 1 | <pre>Turn the next element (whether that be a structure/modifier/element) into a dyadic lambda<br>ᵈf: Push the equivalent of λ2f} to the stack</pre> |
 `ᵉ` | `#^e` | Dyadic Double Element Lambda | `**2:` | 2 | <pre>Turn the next two elements (whether that be a structure/modifier/element) into a dyadic lambda<br>ᵉfg: Push the equivalent of λ2fg} to the stack</pre> |
 `ᶠ` | `#^f` | Dyadic Triple Element Lambda | `***2:` | 3 | <pre>Turn the next three elements (whether that be a structure/modifier/element) into a dyadic lambda<br>ᶠfgh: Push the equivalent of λ2fgh} to the stack</pre> |
 `ᴳ` |  | Dyadic Quadruple Element Lambda | `****2:` | 4 | <pre>Turn the next four elements (whether that be a structure/modifier/element) into a dyadic lambda<br>ᵍfghi: Push the equivalent of λ2fghi} to the stack</pre> |
 `ᴴ` | `#^H` | Apply To Head | `apply-to-head:` | 1 | <pre>Apply element only to the head of list<br>ᴴf: Apply f to the head of the top of the stack</pre> |
 `ᶤ` | `#^i` | First Index Where | `first-index-where:`, `first-index-of:`, `ind-of:`, `find-by:` | 1 | <pre>Find the first index where an element is truthy<br>ᶤf: find the first index where f is truthy</pre> |
 `ᶨ` | `#^j` | Loop and Collect While Unique | `collect-while-unique:` | 1 | <pre>Loop and Collect While Unique<br>ᶨf: Loop and collect while unique</pre> |
 `ᵏ` | `#^k` | Key | `key:` | 1 | <pre>Map an element over the groups formed by identical items.<br>ᵏf: Map f over the groups formed by identical items</pre> |
 `ᶪ` | `#^l` | Loop While Unique | `loop-while-unique:` | 1 | <pre>Loop While Unique - similar to ᶨ, but doesn't collect<br>ᶪf: Loop while unique</pre> |
 `ᵐ` | `#^m` | Maximum By | `max-by:`, `maximum-by:` | 1 | <pre>Maximum By Element<br>ᵐf: Maximum of top of stack based on results of f</pre> |
 `ⁿ` | `#^n` | Minimum By | `min-by:`, `minimum-by:` | 1 | <pre>Minimum By Element<br>ᵐf: Minimum of top of stack based on results of f</pre> |
 `ᵒ` | `#^o` | Outer Product / Table | `outer-product:`, `table:` | 1 | <pre>Outer product<br>ᵒf: Pop two lists, then make a matrix from them by applying f to each pair of elements</pre> |
 `ᵖ` | `#^p` | Map Over Prefixes | `map-over-prefixes:`, `over-prefixes:` | 1 | <pre>Map an element over the prefixes of a list<br>ᵖf: Map f over prefixes</pre> |
 `ᴿ` | `#^R` | Apply to Register | `apply-to-register:`, `to-register:`, `to-reg:` | 1 | <pre>Apply a function to the register. Essentially, push<br>the reigster value to the stack, apply the function, and<br>then pop back into the register<br>ᴿf: Apply f to the register</pre> |
 `ᶳ` | `#^s` | Sort By | `sort-by:`, `scanl:` | 1 | <pre>Sort By Element / Scanl<br>ᶳf: Sort top of stack based on results of f<br>ᶳf: Cumulatively reduce a list of items</pre> |
 `ᵗ` | `#^t` | Unassigned |  | 1 | <pre>Unassigned</pre> |
 `ᵘ` | `#^u` | Collect Until No Change / Neighbours All Equal? | `collect-until-no-change:`, `until-stable:`, `stablise:`, `neighbours-equals:` | 1 | <pre>Run func on the prev result until the result no longer changes<br>returning all intermediate results<br>Given a dyadic function, apply the function to all overlapping pairs of elements<br>and test if all results are equal<br>ᵘf: Collect until no change</pre> |
 `ᵂ` | `#^W` | Dip | `dip:` | 1 | <pre>Stash the top of the stack temporarily, and then apply<br>the function. Finally, push the stashed value<br>ᵂf: pop M, apply f, push M</pre> |
 `ᵡ` | `#^X` | Scan Fixed Point | `scan-fix:` | 1 | <pre>Scan a function until it reaches a fixed point<br>ᵡf: scan f until a fixed point is reached / apply until a previous value is repeated, collecting intermediate results</pre> |
 `ᵞ` | `#^y` | Invariant Under? / Vertical Scan | `invariant-under:`, `vertical-scan:`, `vscan:`, `v-scan:`, `invariant?:`, `same?:` | 1 | <pre>Check if a function is invariant under a transformation / vertical scan<br>ᵞf: check if top of stack is invariant under a transformation<br>ᵞf: scanl columns by f</pre> |
 `ᶻ` | `#^z` | Zip With / Reject by | `zip-with:`, `zipwith:` | 1 | <pre><br>      Given a dyadic function, zip two lists and reduce each by f<br>       and then check if all results are equal.<br>      Given a monadic function, the inverse of monadic /.<br>      Filters where the function is falsey</pre> |
 `⸠` | `#^.` | Single Element Lambda | `*:` | 1 | <pre>Turn the next element (whether that be a structure/modifier/element) into a lambda<br>⸠f: Push the equivalent of λf} to the stack</pre> |
 `ϩ` | `#^:` | Double Element Lambda | `**:` | 2 | <pre>Turn the next two elements (whether that be a structure/modifier/element) into a lambda<br>ϩfg: Push the equivalent of λfg} to the stack</pre> |
 `э` | `#^%` | Triple Element Lambda | `***:` | 3 | <pre>Turn the next three elements (whether that be a structure/modifier/element) into a lambda<br>эfgh: Push the equivalent of λfgh} to the stack</pre> |
 `Ч` | `#^4` | Quadruple Element Lambda | `****:` | 4 | <pre>Turn the next four elements (whether that be a structure/modifier/element) into a lambda<br>Чfghi: Push the equivalent of λfghi} to the stack</pre> |
 `/` |  | Foldl / Reduce By / Filter by | `foldl:`, `reduce:`, `/:`, `fold:`, `reduceby:-` | 1 | <pre>Reduce a list by an element<br>/f: reduce by element f</pre> |
 `<code>`</code>` |  | Map as Stacks | `vec-dump:`, `map-dump:` | 1 | <pre>Map a function over the top of the stack, treating each iteration<br>as if it were a stack of items. Essentially, dump before mapping<br></pre> |
 `v` |  | Vectorise | `vectorise:`, `vec:`, `v:` | 1 | <pre>Vectorises<br>vf: f but vectorised</pre> |
 `∥` |  | Parallel Apply | `parallel-apply:`, `para-apply:`, `paraply:`, `!!:` | 2 | <pre>Parallel apply two elements to the top of the stack<br></pre> |
 `∦` |  | Parallel Apply and Wrap | `parallel-apply-and-wrap:`, `para-apply-and-wrap:`, `<paraply>:`, `<!!>:` | 2 | <pre>Parallel apply two elements to the top of the stack<br>and wrap the result in a list<br></pre> |
 `¿` | `#.?` | Conditional Execution | `if-top:`, `if:` | 1 | <pre>Pop the top of the stack, and, if it's truthy,<br>apply a function</pre> |


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
 `„` | `#,"` | Base-255 Compressed String | Decompress and push a string, converted from a bijective base 255 number using the codepage | <pre>„&lt;compressed string&gt;"</pre> |
 `”` | `#^'` | Dictionary Compressed String | Decompress and push a string using SSS compression, shamelessly stolen from Jelly | <pre>”&lt;compressed string&gt;"</pre> |
 `“` | `#^"` | Base-255 Compressed Number | Decompress and push a number, converted from a bijective base 255 number using the codepage | <pre>“&lt;compressed number&gt;"</pre> |
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

