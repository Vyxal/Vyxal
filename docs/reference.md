cmd  |  stack   |out/*effect
---|---|---
| ! |              |len(stack)
| " |              |* 3 character lambda
| $ |       a,b    |b,a
| % |       a,b    |a % b
| & |       a      |* register = a
| ' |              |* filter lambda
| ( |              |* open a for loop: (variable|code)
| ) |              |* close a for loop
| \* |       a,b    |a * b
| + |       a,b    |a + b
| , |       a      |print(a)
| - |       a,b    |a - b
| . |       a      |print(vyrepr(a))
| / |       a,b    |a / b
| 0 |              |* integer literal
| 1 |              |* integer literal
| 2 |              |* integer literal
| 3 |              |* integer literal
| 4 |              |* integer literal
| 5 |              |* integer literal
| 6 |              |* integer literal
| 7 |              |* integer literal
| 8 |              |* integer literal
| 9 |              |* integer literal
| : |       a      |a,a
| ; |              |* end a structure
| \< |       a,b    |a < b
| = |       a,b    |a == b (vectorising)
| \> |       a,b    |a > b
| ? |              |* take input from either cmd line or stdin, whichever is first
| @ |              |* define / call a function
| A |       a      |all(a)
| B |       a      |int(a, 2) # binary to base 10
| C |       a      |chr(a) if a is int/ord(a) is a is chr
| D |       a      |a,a,a
| E |       a      |eval(a)
| F |       a,f    |a with elements that give truthy results when f is applied
| G |       a      |max(a) # max of iterable
| H |       a      |int(a, 16) # hex to decimal
| I |       a      |int(a)
| J |       a,b    |concat(a, b)
| K |              |factors_of(a)
| L |       a      |len(a)
| M |       a,f    |f mapped to each element in a
| N |       a      |a * -1
| O |       a,b    |a.count(b)
| P |       a,b    |a.strip(b)
| Q |              |* end execution (exit())
| R |       a,f    |reduce a by function f
| S |       a      |str(a)
| T |       a      |[_ for _ in a if bool(_)]
| U |       a      |uniquify(a)
| V |       a,b,c  |a.replace(b, c)
| W |              |* wrap the entire stack into a single list
| X |              |* context level down (+1)
| Y |       a,b    |interleave(x, y)
| Z |       a,b    |zip(x, y)
| [ |              |* if statement
| \\ |              |* one char string
| ] |              |* close if statement
| ` |              |* string delimiter
| a |       a      |any(a)
| b |       a      |bin(a) #base 10 to binary
| c |       a,b    |a in b (non-vectorising)
| d |       a      |a * 2
| e |       a,b    |a ^ b (exponentiation)
| f |       a      |flattened(a)
| g |       a      |min(a) # min of iterable
| h |       a      |a[0]
| i |       a,b    |a[b]
| j |       a,b    |a.join(b)
| k |       a      |* push the constant at the next char k<char>
| l |       a,b    |nwise_group(a, b)
| m |       a      |a + a[::-1]
| n |              |* contextual variable
| o |       a,b    |a.replace(b, '') # same as a b❝V
| p |       a,b    |a.startswith(b)
| q |       a      |"`" + a + "`"
| r |       a,b    |range(a, b)
| s |       a      |sorted(a)
| t |       a      |a[-1]
| u |              |-1
| v |              |* vectorise the next command
| w |       a      |[a]
| x |              |* context level up (-1)
| y |       a      |uninterleave(a)
| z |       a,f    |zipmap the contents of a with function f
| { |              |* open while loop
| \| |              |* structure branch
| } |              |* close while loop
| ~ |              |* perform next operation without popping anything
| λ |              |* start a lambda
| ƛ |              |* start a mapping lambda
| ¬ |       a      |not a
| ∧ |       a,b    |a and b
| ⟑ |       a,b    |b and a
| ∨ |       a,b    |a or b
| ⟇ |       a,b    |b or a
| ÷ |       a      |item_split(a)
| « |              |* base 255 string
| » |              |* base 255 number
| ° |              |* function reference
| • |       a,b    |* multi-command
| ․ |              |* function reference of a built-in
| ⍎ |       a      |* call function a
| Ṛ |       a,b    |random.randint(a,b)
| ½ |       a      |a / 2
| ∆ |              |* two byte math functions
| ø |              |* two byte string functions
| Ï |       a,b    |combinations_with_replacement(a, length=b)
| Ô |              |list of positive odd numbers
| Ç |       a      |1 - a
| æ |       a      |is_prime(a)
| ʀ |       a      |range(0, a + 1)
| ʁ |       a      |range(0, a)
| ɾ |       a      |range(1, a + 1)
| ɽ |       a      |range(1, a)
| Þ |              |* two byte list functions
| ƈ |       a,b    |ncr(a, b)
| ∞ |              |infinite list of positive integers (0 included)
| ⫙ |              |* two byte misc functions
| ß |       a      |* perform next command if a is truthy
| ⎝ |       a      |min(a, key=lambda x: x[-1])
| ⎠ |       a      |max(a, key=lambda x: x[-1])
| ⎡ |       a,b    |max(a, b)
| ⎣ |       a,b    |min(a, b)
| ⨥ |       a      |a + 1
| ⨪ |       a      |a - 1
| ∺ |       a      |a % 2
| ❝ |              |""
| ð |              |" "
| → |       a      |* variable = a
| ← |              |variable
| Ð |       a,b    |to_base_ten(a, b) #a_b => base 10
| ř |       a,b    |repeat(a, b)
| Š |       a,b    |from_base_ten(a, b) #a_10 => base_b
| č |       a      |a != 1
| √ |       a      |sqrt(a)
| ⳹ |       a,b    |a // b
| Ẋ |       a,b    |a xor b
| Ȧ |       a      |abs(a)
| Ȯ |       a      |oct(a)
| Ḋ |       a,b    |divmod(a, b)
| Ė |       a      |enumerate(a)
| Ẹ |              |enumerate(stack)
| ṙ |       a      |round(a)
| ∑ |       a      |sum(a)
| Ĥ |       a      |100
| ⟨ |              |* open a list ⟨⟩⟨...|...⟩
| ⟩ |              |* close a list
| ı |              |* 2 char compressed string
| ⁌ |       a      |'\n'.join(a)
| Τ |              |10
| Ĵ |       a      |''.join(a)
| ² |       a      |a ^ 2
| ‿ |       a,b    |[a, b]
| ⁂ |       a,b    |inclusive_range(a, b)
| ĸ |       a,b    |if a and b are integers, absolute difference, else b evenly distributed over the elements of a
| ¶ |              |"\n"
| ⁋ |       a      |vertical_join(a)
| ⁑ |       a,b    |vertical_join(a, padding=b)
| Ń |       f,a    |first n integers where function f is true
| ń |       f      |first integer where function f is true
| ‼ |       a      |factorial(a)
| ⨊ |       a      |cumulative_sums(a)
| ≈ |       a      |all_equal(a) # every item is the same
| ʗ |       a      |counts_of_items(a)
| ◁ |       a      |reversed(a)
| ⊐ |       a      |a[:-1]
| ∫ |              |sum(entire_stack)
| ⍋ |       a      |graded_up(a)
| ⍒ |       a      |graded_down(a) # short for ⍋◁
| ∈ |       a,f    |a.indexes_where_truthy(fn)
| ₛ |       a,f    |sorted(a, key=fn) # sort by function result
| £ |              |set register without emptying
| Œ |       a,b    |[a[n] for n in b]
| œ |       a,b    |a % b == 0
| ≕ |       a,b    |a == b (Non-vectorising)
| ≠ |       a,b    |a != b (vectorising)
| ¥ |              |push register without emptying
| ⁱ |       a,b    |a[b:] #other forms of indexing use i
| ‹ |       a,b    |a << b
| › |       a,b    |a >> b
| ⍲ |       a,b    |a and b (bitwise) # a & b
| ⍱ |       a,b    |a or b (bitwise) # a | b
| ‸ |       a,b    |a xor b (bitwise)
| ¡ |       a      |not a (bitwise) # ~a
| ⊑ |       a,b    |a.insert(0, b)
| ≀ |       a,b,c  |a.insert(b, c)
| ℅ |       a      |random element from a
| ≤ |       a,b    |a <= b
| ≥ |       a,b    |a >= b
| ↜ |              |stack[-2]
| ≗ |       a,b,c  |a[b] = c
| ⋯ |       a      |integer_partitions(a)
| ⧢ |       a      |permutations(a)
| ũ |       a      |* treat compressed string as integer list
| ⁰ |       a,b    |a[0:b]
| ¹ |       a,b    |a[1:b]
| ª |              |* push index of next character in Vyxal's codepage
| ₑ |       a      |exec(VyCompile(a))
| ϊ |       a      |range(1, len(a) + 1)
| ≎ |       a      |group_consecutive(a)
| ⇿ |       a,b,c  |transliterate(a, b, c) # a: string to transliterate, b/c: mapping
| ⊛ |       a      |stack[0], stack[1:]
| × |       a,b    |cartesian_product(a, b)
| ¯ |       a      |deltas(a)
| ± |       a      |sign_of(a)
| ⊂ |       a,b    |all combinations with length b of a
| ⍞ |              |all inputs wrapped in a list
| ፣ |              |print t.o.s without popping
| ₴ |       a      |print(a, end="")
| ⍉ |       a      |transpose(a)
| ΐ |       a      |range(0, len(a))
| ₁ |              |most recent input
| ⊘ |       a,b    |a.split_and_keep_delimiter(b)
| ᶢ |       a,(b)  |gcd(a) if a is a list, else gcd(a, b)
| ₌ |              |* apply the next two built-ins parallel 3 4 ₌+- -> 7 -1
| ↭ |       a,b,c  |c,a,b
| ſ |       (a)    |a[1:] if top of stack is list/Generator else 69
| ƀ |       f,a,(b)|push Generator of function f with intial vector a, limited to b items (if present)
| Ɓ |       a      |bool(a)
| ⁚ |       a       |a[:-1], a[-1]
| ⌈ |       a      |ceiling(a)
| ⌊ |       a      |floor(a)
| ⊓ |       a,b    |a wrapped in chunks of b
| ⊣ |       a,b    |a.trim(b) # Like Jelly's trim
| Ḟ |       a,b    |a.find(b)
| ḟ |       a,b,c  |a.find(b, start=c)
| ∪ |       a,b    |set union
| ∩ |       a,b    |set intersection
| ⊍ |       a,b    |set(a) ^ set(b)
| ⁜ |       a      |1 / a
| ⌑ |              |* 2 character lambda
| Ḇ |       a      |bifuricate(a)
| ₂ |              |*second most recent input
| ⁾ |       a      |10 ** a
| ₦ |       a      |a.split("\n")
| ¼ |       a      |a / 4
| ƒ |       a      |fractionify(a) # returns a two-item list of [numerator, denominator]
| ɖ |       a      |decimalify(a) # opposite of ƒ
| Ꝓ |       a      |powerset(a)
| ′ |       a      |prime_factorisation(a)
| \\n |             |NOP
| \\t |             |NOP
| \<space> |        |NOP
| ₥ |      a       |average(a)
| ″ |      a       |ath prime
| α |              |push 26
| β |              |push 64
| γ |              |push 128
| Π |      a       |product(a)
| Δc |     a       |math.cos(a)
| ΔC |     a       |math.arccos(a)
| ∆q |     a,b     |roots of quadratic ax^2 + bx = 0
| ∆Q |     a,b     |roots of quadratic x^2 + ax + b = 0
| Δs |     a       |math.sin(a)
| ΔS |     a       |math.arcsin(a)
| Δt |     a       |math.tan(a)
| ΔT |     a       |math.arctan(a)
| ∆P |     a       |roots of polynomial with coefficients in a: [2, 5, 1, 3] -> 2x^3 + 5x^2 + x + 3 = 0
| ∆ƈ |     a,b     |n pick r (npr)
| ∆± |     a,b     |math.copysign(a, b)
| øo |     a,b     |a.remove_until_no_change(b)
| øV |     a,b,c   |a.replace_until_no_change(b, c)
| øc |     a       |base_255_string_compressed(a)
| øC |     a       |base_255_number_compressed(a)
| øl |     a       |a.lower()
| øU |     a       |a.upper()
| øt |     a       |a.titlecase()
| ø$ |     a       |a.swapcase()
| øe |     a       |run_length_encoded(a)
| ød |     a       |run_length_decoded(a)
| ⫙' |             |* rotate entire stack left
| ⫙" |             |* rotate entire stack right
