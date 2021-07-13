codepage = "λƛ¬∧⟑∨⟇÷×«\n»°•ß†€"
codepage += "½∆ø↔¢⌐æʀʁɾɽÞƈ∞¨ "
codepage += "!\"#$%&'()*+,-./01"
codepage += "23456789:;<=>?@A"
codepage += "BCDEFGHIJKLMNOPQ"
codepage += "RSTUVWXYZ[\\]`^_abc"
codepage += "defghijklmnopqrs"
codepage += "tuvwxyz{|}~↑↓∴∵›"
codepage += "‹∷¤ð→←βτȧḃċḋėḟġḣ"
codepage += "ḭŀṁṅȯṗṙṡṫẇẋẏż√⟨⟩"
codepage += "‛₀₁₂₃₄₅₆₇₈¶⁋§ε¡"
codepage += "∑¦≈µȦḂĊḊĖḞĠḢİĿṀṄ"
codepage += "ȮṖṘṠṪẆẊẎŻ₌₍⁰¹²∇⌈"
codepage += "⌊¯±₴…□↳↲⋏⋎꘍ꜝ℅≤≥"
codepage += "≠⁼ƒɖ∪∩⊍£¥⇧⇩ǍǎǏǐǑ"
codepage += "ǒǓǔ⁽‡≬⁺↵⅛¼¾Π„‟"

assert len(codepage) == 256

command_dict = {
    "¬": ("vy_globals.stack.append(int(not pop(vy_globals.stack)))", 1),
    "∧": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(lhs and rhs)",
        2,
    ),
    "⟑": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(rhs and lhs)",
        2,
    ),
    "∨": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(lhs or rhs)",
        2,
    ),
    "⟇": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(rhs or lhs)",
        2,
    ),
    "÷": (
        "for item in iterable(pop(vy_globals.stack)): vy_globals.stack.append(item)",
        1,
    ),
    "•": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(log(lhs, rhs))",
        2,
    ),
    "†": (
        "fn = pop(vy_globals.stack); vy_globals.stack += function_call(fn, vy_globals.stack)",
        1,
    ),
    "€": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(split(lhs, rhs))",
        2,
    ),
    "½": ("vy_globals.stack.append(halve(pop(vy_globals.stack)))", 1),
    "↔": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(combinations_replace_generate(lhs, rhs))",
        2,
    ),
    "⌐": ("vy_globals.stack.append(complement(pop(vy_globals.stack)))", 1),
    "æ": ("vy_globals.stack.append(is_prime(pop(vy_globals.stack)))", 1),
    "ʀ": (
        "vy_globals.stack.append(orderless_range(0, add(pop(vy_globals.stack), 1)))",
        1,
    ),
    "ʁ": ("vy_globals.stack.append(orderless_range(0, pop(vy_globals.stack)))", 1),
    "ɾ": (
        "vy_globals.stack.append(orderless_range(1, add(pop(vy_globals.stack), 1)))",
        1,
    ),
    "ɽ": ("vy_globals.stack.append(orderless_range(1, pop(vy_globals.stack)))", 1),
    "ƈ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(ncr(lhs, rhs))",
        2,
    ),
    "∞": ("vy_globals.stack.append(Generator.from_index_function(lambda x: x))", 0),
    "!": ("vy_globals.stack.append(len(vy_globals.stack))", 0),
    '"': (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append([lhs, rhs])",
        2,
    ),
    "$": (
        "top, over = pop(vy_globals.stack, 2); vy_globals.stack.append(top); vy_globals.stack.append(over)",
        2,
    ),
    "%": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(modulo(lhs, rhs))",
        2,
    ),
    "*": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(multiply(lhs, rhs))",
        2,
    ),
    "+": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(add(lhs, rhs))",
        2,
    ),
    ",": ("vy_print(pop(vy_globals.stack))", 1),
    "-": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(subtract(lhs, rhs))",
        2,
    ),
    "/": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(divide(lhs, rhs))",
        2,
    ),
    ":": (
        "temp = pop(vy_globals.stack); vy_globals.stack.append(temp); vy_globals.stack.append(deref(temp))",
        1,
    ),
    "^": ("vy_globals.stack = vy_globals.stack[::-1]", 0),
    "_": ("pop(vy_globals.stack)", 1),
    "<": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(compare(lhs, rhs, Comparitors.LESS_THAN))",
        2,
    ),
    ">": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(compare(lhs, rhs, Comparitors.GREATER_THAN))",
        2,
    ),
    "=": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(compare(lhs, rhs, Comparitors.EQUALS))",
        2,
    ),
    "?": ("vy_globals.stack.append(get_input(0))", 0),
    "A": ("vy_globals.stack.append(int(all(iterable(pop(vy_globals.stack)))))", 1),
    "B": ("vy_globals.stack.append(vy_int(pop(vy_globals.stack), 2))", 1),
    "C": ("vy_globals.stack.append(chrord(pop(vy_globals.stack)))", 1),
    "D": (
        "temp = pop(vy_globals.stack); vy_globals.stack.append(temp); vy_globals.stack.append(deref(temp)); vy_globals.stack.append(deref(vy_globals.stack[-1]))",
        1,
    ),
    "E": ("vy_globals.stack.append(vy_eval(pop(vy_globals.stack)))", 1),
    "F": (
        "fn, vector = pop(vy_globals.stack, 2); vy_globals.stack.append(vy_filter(fn, vector))",
        2,
    ),
    "G": ("vy_globals.stack.append(vy_max(iterable(pop(vy_globals.stack))))", 1),
    "H": ("vy_globals.stack.append(vy_int(pop(vy_globals.stack), 16))", 1),
    "I": ("vy_globals.stack.append(vy_int(pop(vy_globals.stack)))", 1),
    "J": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(join(lhs, rhs))",
        2,
    ),
    "K": ("vy_globals.stack.append(divisors_of(pop(vy_globals.stack)))", 1),
    "L": (
        "top = pop(vy_globals.stack); vy_globals.stack.append(len(iterable(top)))",
        1,
    ),
    "M": (
        "fn, vector = pop(vy_globals.stack, 2); temp = vy_map(fn, vector); vy_globals.stack.append(temp)",
        2,
    ),
    "N": ("vy_globals.stack.append(negate(pop(vy_globals.stack)))", 1),
    "O": (
        "needle, haystack = pop(vy_globals.stack, 2); vy_globals.stack.append(iterable(haystack).count(needle))",
        2,
    ),
    "P": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(vy_str(lhs).strip(vy_str(rhs)))",
        2,
    ),
    "Q": ("exit()", 0),
    "R": (
        "fn, vector = pop(vy_globals.stack, 2); vy_globals.stack += vy_reduce(fn, vector)",
        2,
    ),
    "S": ("vy_globals.stack.append(vy_str(pop(vy_globals.stack)))", 1),
    "T": (
        "vy_globals.stack.append([i for (i, x) in enumerate(pop(vy_globals.stack)) if bool(x)])",
        1,
    ),
    "U": ("vy_globals.stack.append(Generator(uniquify(pop(vy_globals.stack))))", 1),
    "V": (
        "replacement, needle, haystack = pop(vy_globals.stack, 3); vy_globals.stack.append(replace(haystack, needle, replacement))",
        3,
    ),
    "W": ("vy_globals.stack = [deref(vy_globals.stack)]", 0),
    "X": ("context_level += 1", 0),
    "Y": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(interleave(lhs, rhs))",
        2,
    ),
    "Z": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(Generator(vy_zip(iterable(lhs), iterable(rhs))))",
        2,
    ),
    "a": ("vy_globals.stack.append(int(any(iterable(pop(vy_globals.stack)))))", 1),
    "b": ("vy_globals.stack.append(vy_bin(pop(vy_globals.stack)))", 1),
    "c": (
        "needle, haystack = pop(vy_globals.stack, 2); haystack = iterable(haystack, str)\nif type(haystack) is str: needle = vy_str(needle)\nvy_globals.stack.append(int(needle in iterable(haystack, str)))",
        2,
    ),
    "d": ("vy_globals.stack.append(multiply(pop(vy_globals.stack), 2))", 1),
    "e": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(exponate(lhs, rhs))",
        2,
    ),
    "f": ("vy_globals.stack.append(flatten(iterable(pop(vy_globals.stack))))", 1),
    "g": ("vy_globals.stack.append(vy_min(iterable(pop(vy_globals.stack))))", 1),
    "h": ("vy_globals.stack.append(iterable(pop(vy_globals.stack))[0])", 1),
    "i": (
        "rhs, lhs = pop(vy_globals.stack, 2)\nvy_globals.stack.append(index(lhs, rhs))",
        2,
    ),
    "j": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(join_on(lhs, rhs))",
        2,
    ),
    "l": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(nwise_pair(lhs, rhs))",
        2,
    ),
    "m": ("item = pop(vy_globals.stack); vy_globals.stack.append(mirror(item))", 1),
    "n": (
        "vy_globals.stack.append(context_values[context_level % len(context_values)])",
        0,
    ),
    "o": (
        "needle, haystack = pop(vy_globals.stack, 2); vy_globals.stack.append(remove(haystack, needle))",
        2,
    ),
    "p": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(prepend(lhs, rhs))",
        2,
    ),
    "q": ("vy_globals.stack.append(uneval(vy_str(pop(vy_globals.stack))))", 1),
    "r": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(orderless_range(lhs, rhs))",
        2,
    ),
    "s": ("vy_globals.stack.append(vy_sorted(pop(vy_globals.stack)))", 1),
    "t": ("vy_globals.stack.append(iterable(pop(vy_globals.stack))[-1])", 1),
    "u": ("vy_globals.stack.append(-1)", 0),
    "w": ("vy_globals.stack.append([pop(vy_globals.stack)])", 1),
    "x": ("vy_globals.stack += this_function(vy_globals.stack)", 0),
    "y": ("vy_globals.stack += uninterleave(pop(vy_globals.stack))", 1),
    "z": (
        "fn, vector = pop(vy_globals.stack, 2); vy_globals.stack += vy_zipmap(fn, vector)",
        2,
    ),
    "↑": (
        "vy_globals.stack.append(max(pop(vy_globals.stack), key=lambda x: x[-1]))",
        1,
    ),
    "↓": (
        "vy_globals.stack.append(min(pop(vy_globals.stack), key=lambda x: x[-1]))",
        1,
    ),
    "∴": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(vy_max(lhs, rhs))",
        2,
    ),
    "∵": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(vy_min(lhs, rhs))",
        2,
    ),
    "β": (
        "alphabet, number = pop(vy_globals.stack, 2); vy_globals.stack.append(utilities.to_ten(number, alphabet))",
        2,
    ),
    "τ": (
        "alphabet, number = pop(vy_globals.stack, 2); vy_globals.stack.append(utilities.from_ten(number, alphabet))",
        2,
    ),
    "›": ("vy_globals.stack.append(add(pop(vy_globals.stack), 1))", 1),
    "‹": ("vy_globals.stack.append(subtract(pop(vy_globals.stack), 1))", 1),
    "∷": ("vy_globals.stack.append(modulo(pop(vy_globals.stack), 2))", 1),
    "¤": ("vy_globals.stack.append('')", 0),
    "ð": ("vy_globals.stack.append(' ')", 0),
    "ȧ": ("vy_globals.stack.append(vy_abs(pop(vy_globals.stack)))", 1),
    "ḃ": (
        "vy_globals.stack.append(int(not compare(pop(vy_globals.stack), 0, Comparitors.EQUALS)))",
        1,
    ),
    "ċ": (
        "vy_globals.stack.append(compare(pop(vy_globals.stack), 1, Comparitors.NOT_EQUALS))",
        1,
    ),
    "ḋ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(vy_divmod(lhs, rhs))",
        2,
    ),  # Dereference because generators could accidentally get exhausted.
    "ė": (
        "vy_globals.stack.append(Generator(enumerate(iterable(pop(vy_globals.stack)))))",
        1,
    ),
    "ḟ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(find(lhs, rhs))",
        2,
    ),
    "ġ": (
        "rhs = pop(vy_globals.stack)\nif vy_type(rhs) in [list, Generator]: vy_globals.stack.append(gcd(rhs))\nelse: vy_globals.stack.append(gcd(pop(vy_globals.stack), rhs))",
        2,
    ),
    "ḣ": (
        "top = iterable(pop(vy_globals.stack)); vy_globals.stack.append(top[0]); vy_globals.stack.append(top[1:])",
        1,
    ),
    "ḭ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(integer_divide(lhs, rhs))",
        2,
    ),
    "ŀ": (
        "start, needle, haystack = pop(vy_globals.stack, 3); vy_globals.stack.append(find(haystack, needle, start))",
        3,
    ),
    "ṁ": (
        "top = iterable(pop(vy_globals.stack)); vy_globals.stack.append(divide(summate(top), len(top)))",
        1,
    ),
    "ṅ": ("vy_globals.stack.append(first_n(pop(vy_globals.stack)))", 1),
    "ȯ": (
        "n, fn = pop(vy_globals.stack, 2); vy_globals.stack.append(first_n(fn, n))",
        2,
    ),
    "ṗ": ("vy_globals.stack.append(powerset(iterable(pop(vy_globals.stack))))", 1),
    "ṙ": ("vy_globals.stack.append(vy_round(pop(vy_globals.stack)))", 1),
    "ṡ": (
        "fn , vector = pop(vy_globals.stack, 2); vy_globals.stack.append(vy_sorted(vector, fn))",
        2,
    ),
    "ṫ": (
        "vector = iterable(pop(vy_globals.stack)); vy_globals.stack.append(vector[:-1]); vy_globals.stack.append(vector[-1])",
        1,
    ),
    "ẇ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(wrap(lhs, rhs))",
        2,
    ),
    "ẋ": (
        "rhs, lhs = pop(vy_globals.stack, 2); main = None;\nif vy_type(lhs) is Function: main = pop(vy_globals.stack)\nvy_globals.stack.append(repeat(lhs, rhs, main))",
        2,
    ),
    "ẏ": (
        "obj = iterable(pop(vy_globals.stack)); vy_globals.stack.append(Generator(range(0, len(obj))))",
        1,
    ),
    "ż": (
        "obj = iterable(pop(vy_globals.stack)); vy_globals.stack.append(Generator(range(1, len(obj) + 1)))",
        1,
    ),
    "√": ("vy_globals.stack.append(exponate(pop(vy_globals.stack), 0.5))", 1),
    "₀": ("vy_globals.stack.append(10)", 0),
    "₁": ("vy_globals.stack.append(100)", 0),
    "₂": (
        "vy_globals.stack.append(const_divisibility(pop(vy_globals.stack), 2, lambda item: len(item) % 2 == 0))",
        1,
    ),
    "₃": (
        "vy_globals.stack.append(const_divisibility(pop(vy_globals.stack), 3, lambda item: len(item) == 1))",
        1,
    ),
    "₄": ("vy_globals.stack.append(26)", 0),
    "₅": (
        "top = pop(vy_globals.stack); res = const_divisibility(top, 5, lambda item: (top, len(item)))\nif type(res) is tuple: vy_globals.stack += list(res)\nelse: vy_globals.stack.append(res)",
        1,
    ),
    "₆": ("vy_globals.stack.append(64)", 0),
    "₇": ("vy_globals.stack.append(128)", 0),
    "₈": ("vy_globals.stack.append(256)", 0),
    "¶": ("vy_globals.stack.append('\\n')", 0),
    "⁋": ("vy_globals.stack.append(osabie_newline_join(pop(vy_globals.stack)))", 1),
    "§": ("vy_globals.stack.append(vertical_join(pop(vy_globals.stack)))", 1),
    "ε": (
        "padding, vector = pop(vy_globals.stack, 2); vy_globals.stack.append(vertical_join(vector, padding))",
        2,
    ),
    "¡": ("vy_globals.stack.append(factorial(pop(vy_globals.stack)))", 1),
    "∑": ("vy_globals.stack.append(summate(pop(vy_globals.stack)))", 1),
    "¦": (
        "vy_globals.stack.append(cumulative_sum(iterable(pop(vy_globals.stack))))",
        1,
    ),
    "≈": (
        "vy_globals.stack.append(int(len(set(iterable(pop(vy_globals.stack)))) == 1))",
        1,
    ),
    "Ȧ": (
        "value, lst_index, vector = pop(vy_globals.stack, 3); vy_globals.stack.append(assigned(iterable(vector), lst_index, value))",
        3,
    ),
    "Ḃ": ("vy_globals.stack += bifurcate(pop(vy_globals.stack))", 1),
    "Ċ": ("vy_globals.stack.append(counts(pop(vy_globals.stack)))", 1),
    "Ḋ": (
        "rhs, lhs = pop(vy_globals.stack, 2); ret = is_divisble(lhs, rhs)\nif type(ret) is tuple: vy_globals.stack += list(ret)\nelse: vy_globals.stack.append(ret)",
        2,
    ),
    "Ė": ("vy_globals.stack += vy_exec(pop(vy_globals.stack))", 1),
    "Ḟ": (
        """top = pop(vy_globals.stack)
if vy_type(top) is Number:
    limit = int(top); vector = pop(vy_globals.stack)
else:
    limit = -1; vector = top
fn = pop(vy_globals.stack)
vy_globals.stack.append(Generator(fn, limit=limit, initial=iterable(vector)))
""",
        2,
    ),
    "Ġ": (
        "vy_globals.stack.append(group_consecutive(iterable(pop(vy_globals.stack))))",
        1,
    ),
    "Ḣ": ("vy_globals.stack.append(iterable(pop(vy_globals.stack))[1:])", 1),
    "İ": (
        "indices, vector = pop(vy_globals.stack, 2); vy_globals.stack.append(indexed_into(vector, indices))",
        2,
    ),
    "Ŀ": (
        "new, original, value = pop(vy_globals.stack, 3)\nif Function in map(type, (new, original, value)): vy_globals.stack.append(repeat_no_collect(value, original, new))\nelse: vy_globals.stack.append(transliterate(iterable(original, str), iterable(new, str), iterable(value, str)))",
        3,
    ),
    "Ṁ": (
        "item, index, vector = pop(vy_globals.stack, 3);\nif Function in map(type, (item, index, vector)): vy_globals.stack.append(map_every_n(vector, item, index))\nelse: vy_globals.stack.append(inserted(vector, item, index))",
        3,
    ),
    "Ṅ": (
        "top = pop(vy_globals.stack);\nif vy_type(top) == Number:vy_globals.stack.append(Generator(partition(top)))\nelse: vy_globals.stack.append(' '.join([vy_str(x) for x in top]))",
        1,
    ),  # ---------------------------
    "Ȯ": (
        "if len(vy_globals.stack) >= 2: vy_globals.stack.append(vy_globals.stack[-2])\nelse: vy_globals.stack.append(get_input(0))",
        0,
    ),
    "Ṗ": (
        "vy_globals.stack.append(Generator(permutations(iterable(pop(vy_globals.stack)))))",
        1,
    ),
    "Ṙ": ("vy_globals.stack.append(reverse(pop(vy_globals.stack)))", 1),
    "Ṡ": ("vy_globals.stack = [summate(vy_globals.stack)]", 0),
    "Ṫ": ("vy_globals.stack.append(iterable(pop(vy_globals.stack), str)[:-1])", 1),
    "Ẇ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(split(lhs, rhs, True))",
        2,
    ),
    "Ẋ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(cartesian_product(lhs, rhs))",
        2,
    ),
    "Ẏ": (
        "index, vector = pop(vy_globals.stack, 2); vy_globals.stack.append(one_argument_tail_index(vector, index, 0))",
        2,
    ),
    "Ż": (
        "index, vector = pop(vy_globals.stack, 2); vy_globals.stack.append(one_argument_tail_index(vector, index, 1))",
        2,
    ),
    "⁰": ("vy_globals.stack.append(input_values[0][0][-1])", 0),
    "¹": ("vy_globals.stack.append(input_values[0][0][-2])", 0),
    "²": ("x = pop(vy_globals.stack); vy_globals.stack.append(square(x))", 1),
    "∇": (
        "c, b, a = pop(vy_globals.stack, 3); vy_globals.stack.append(c); vy_globals.stack.append(a); vy_globals.stack.append(b)",
        3,
    ),
    "⌈": ("vy_globals.stack.append(ceiling(pop(vy_globals.stack)))", 1),
    "⌊": ("vy_globals.stack.append(floor(pop(vy_globals.stack)))", 1),
    "¯": ("vy_globals.stack.append(deltas(pop(vy_globals.stack)))", 1),
    "±": ("vy_globals.stack.append(sign_of(pop(vy_globals.stack)))", 1),
    "₴": ("vy_print(pop(vy_globals.stack), end='')", 1),
    "…": (
        "top = pop(vy_globals.stack); vy_globals.stack.append(top); vy_print(top)",
        0,
    ),
    "□": (
        "if inputs: vy_globals.stack.append(inputs)\nelse:\n    s, x = [], input()\n    while x:\n        s.append(vy_eval(x)); x = input()",
        0,
    ),
    "↳": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(rshift(lhs, rhs))",
        2,
    ),
    "↲": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(lshift(lhs, rhs))",
        2,
    ),
    "⋏": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(bit_and(lhs, rhs))",
        2,
    ),
    "⋎": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(bit_or(lhs, rhs))",
        2,
    ),
    "꘍": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(bit_xor(lhs, rhs))",
        2,
    ),
    "ꜝ": ("vy_globals.stack.append(bit_not(pop(vy_globals.stack)))", 1),
    "℅": ("vy_globals.stack.append(random.choice(iterable(pop(vy_globals.stack))))", 1),
    "≤": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(compare(lhs, rhs, Comparitors.LESS_THAN_EQUALS))",
        2,
    ),
    "≥": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(compare(lhs, rhs, Comparitors.GREATER_THAN_EQUALS))",
        2,
    ),
    "≠": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(int(deref(lhs) != deref(rhs)))",
        2,
    ),
    "⁼": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(int(deref(lhs) == deref(rhs)))",
        2,
    ),
    "ƒ": ("vy_globals.stack.append(fractionify(pop(vy_globals.stack)))", 1),
    "ɖ": ("vy_globals.stack.append(decimalify(pop(vy_globals.stack)))", 1),
    "×": ("vy_globals.stack.append('*')", 0),
    "∪": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(set_union(lhs, rhs))",
        2,
    ),
    "∩": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(set_intersection(lhs, rhs))",
        2,
    ),
    "⊍": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(set_caret(lhs, rhs))",
        2,
    ),
    "£": ("register = pop(vy_globals.stack)", 1),
    "¥": ("vy_globals.stack.append(register)", 0),
    "⇧": ("vy_globals.stack.append(graded(pop(vy_globals.stack)))", 1),
    "⇩": ("vy_globals.stack.append(graded_down(pop(vy_globals.stack)))", 1),
    "Ǎ": ("vy_globals.stack.append(two_power(pop(vy_globals.stack)))", 1),
    "ǎ": ("vy_globals.stack.append(nth_prime(pop(vy_globals.stack)))", 1),
    "Ǐ": ("vy_globals.stack.append(prime_factors(pop(vy_globals.stack)))", 1),
    "ǐ": ("vy_globals.stack.append(all_prime_factors(pop(vy_globals.stack)))", 1),
    "Ǒ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(order(lhs, rhs))",
        2,
    ),
    "ǒ": ("vy_globals.stack.append(is_empty(pop(vy_globals.stack)))", 1),
    "Ǔ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack += overloaded_iterable_shift(lhs, rhs, ShiftDirections.LEFT)",
        2,
    ),
    "ǔ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack += overloaded_iterable_shift(lhs, rhs, ShiftDirections.RIGHT)",
        2,
    ),
    "¢": (
        "replacement, needle, haystack = pop(vy_globals.stack, 3); vy_globals.stack.append(infinite_replace(haystack, needle, replacement))",
        3,
    ),
    "↵": (
        "vy_globals.stack.append(split_newlines_or_pow_10(pop(vy_globals.stack)))",
        1,
    ),
    "⅛": ("global_stack.append(pop(vy_globals.global_stack))", 1),
    "¼": ("vy_globals.stack.append(pop(vy_globals.global_stack))", 0),
    "¾": ("vy_globals.stack.append(deref(vy_globals.global_stack))", 0),
    "Π": ("vy_globals.stack.append(product(iterable(pop(vy_globals.stack))))", 1),
    "„": (
        "vy_globals.stack = iterable_shift(vy_globals.stack, ShiftDirections.LEFT)",
        0,
    ),
    "‟": (
        "vy_globals.stack = iterable_shift(vy_globals.stack, ShiftDirections.RIGHT)",
        0,
    ),
    "∆S": (
        "arg = pop(vy_globals.stack); vy_globals.stack.append(vectorise(math.asin, arg))",
        1,
    ),
    "∆C": (
        "arg = pop(vy_globals.stack); vy_globals.stack.append(vectorise(math.acos, arg))",
        1,
    ),
    "∆T": ("arg = pop(vy_globals.stack); vy_globals.stack.append(math.atan(arg))", 1),
    "∆q": (
        "coeff_a, coeff_b = pop(vy_globals.stack, 2); vy_globals.stack.append(polynomial([coeff_a, coeff_b, 0]))",
        2,
    ),
    "∆Q": (
        "coeff_b, coeff_c = pop(vy_globals.stack, 2); vy_globals.stack.append(polynomial([1, coeff_b, coeff_c]))",
        2,
    ),
    "∆P": (
        "coeff = iterable(pop(vy_globals.stack)); vy_globals.stack.append(polynomial(coeff));",
        1,
    ),
    "∆s": (
        "arg = pop(vy_globals.stack); vy_globals.stack.append(vectorise(math.sin, arg))",
        1,
    ),
    "∆c": (
        "arg = pop(vy_globals.stack); vy_globals.stack.append(vectorise(math.cos, arg))",
        1,
    ),
    "∆t": (
        "arg = pop(vy_globals.stack); vy_globals.stack.append(vectorise(math.tan, arg))",
        1,
    ),
    "∆ƈ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(divide(factorial(lhs), factorial(subtract(lhs, rhs))))",
        2,
    ),
    "∆±": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(vectorise(math.copysign, lhs, rhs))",
        2,
    ),
    "∆K": (
        "arg = pop(vy_globals.stack); vy_globals.stack.append(summate(join(0, divisors_of(arg)[:-1])))",
        1,
    ),
    "∆²": ("arg = pop(vy_globals.stack); vy_globals.stack.append(is_square(arg))", 1),
    "∆e": (
        "arg = pop(vy_globals.stack); vy_globals.stack.append(vectorise(math.exp, arg))",
        1,
    ),
    "∆E": (
        "arg = pop(vy_globals.stack); vy_globals.stack.append(vectorise(math.expm1, arg))",
        1,
    ),
    "∆L": (
        "arg = pop(vy_globals.stack); vy_globals.stack.append(vectorise(math.log, arg))",
        1,
    ),
    "∆l": (
        "arg = pop(vy_globals.stack); vy_globals.stack.append(vectorise(math.log2, arg))",
        1,
    ),
    "∆τ": (
        "arg = pop(vy_globals.stack); vy_globals.stack.append(vectorise(math.log10, arg))",
        1,
    ),
    "∆d": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(distance_between(lhs, rhs))",
        2,
    ),
    "∆D": (
        "arg = pop(vy_globals.stack); vy_globals.stack.append(vectorise(math.degrees, arg))",
        1,
    ),
    "∆R": (
        "arg = pop(vy_globals.stack); vy_globals.stack.append(vectorise(math.radians, arg))",
        1,
    ),
    "∆≤": (
        "arg = pop(vy_globals.stack); vy_globals.stack.append(compare(vy_abs(arg), 1, Comparitors.LESS_THAN_EQUALS))",
        1,
    ),
    "∆Ṗ": ("vy_globals.stack.append(next_prime(pop(vy_globals.stack)))", 1),
    "∆ṗ": ("vy_globals.stack.append(prev_prime(pop(vy_globals.stack)))", 1),
    "∆p": ("vy_globals.stack.append(closest_prime(pop(vy_globals.stack)))", 1),
    "∆ṙ": (
        "vy_globals.stack.append(unsympy(sympy.prod(map(sympy.poly('x').__sub__, iterable(pop(vy_globals.stack)))).all_coeffs()[::-1]))",
        1,
    ),
    "∆Ṙ": ("vy_globals.stack.append(random.random())", 0),
    "∆W": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(vectorise(round, lhs, rhs))",
        2,
    ),  # if you think I'm making this work with strings, then you can go commit utter go awayance. smh.
    "∆Ŀ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(vectorise(lambda x, y: int(numpy.lcm(x, y)), lhs, rhs))",
        2,
    ),
    "øo": (
        "needle, haystack = pop(vy_globals.stack, 2); vy_globals.stack.append(infinite_replace(haystack, needle, ''))",
        2,
    ),
    "øV": (
        "replacement, needle, haystack = pop(vy_globals.stack, 3); vy_globals.stack.append(infinite_replace(haystack, needle, replacement))",
        3,
    ),
    "øc": (
        "value = pop(vy_globals.stack); vy_globals.stack.append('«' + utilities.from_ten(utilities.to_ten(value, utilities.base27alphabet), encoding.codepage_string_compress) + '«')",
        1,
    ),
    "øC": (
        "number = pop(vy_globals.stack); vy_globals.stack.append('»' + utilities.from_ten(number, encoding.codepage_number_compress) + '»')",
        1,
    ),
    "øĊ": ("vy_globals.stack.append(centre(pop(vy_globals.stack)))", 1),
    "øm": ("vy_globals.stack.append(palindromise(iterable(pop(vy_globals.stack))))", 1),
    "øe": (
        "vy_globals.stack.append(run_length_encode(iterable(pop(vy_globals.stack), str)))",
        1,
    ),
    "ød": ("vy_globals.stack.append(run_length_decode(pop(vy_globals.stack)))", 1),
    "øD": ("vy_globals.stack.append(dictionary_compress(pop(vy_globals.stack)))", 1),
    "øW": ("vy_globals.stack.append(split_on_words(vy_str(pop(vy_globals.stack))))", 1),
    "øṙ": (
        "replacent, pattern, source = pop(vy_globals.stack, 3); vy_globals.stack.append(regex_replace(vy_str(source), vy_str(pattern), replacent))",
        3,
    ),
    "øp": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(int(str(lhs).startswith(str(rhs))))",
        2,
    ),
    "øP": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(pluralise(lhs, rhs))",
        2,
    ),
    "øṁ": ("vy_globals.stack.append(vertical_mirror(pop(vy_globals.stack)))", 1),
    "øṀ": (
        "vy_globals.stack.append(vertical_mirror(pop(vy_globals.stack), ['()[]{}<>/\\\\', ')(][}{><\\\\/']))",
        1,
    ),
    "ø¦": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(vertical_mirror(lhs, rhs))",
        2,
    ),
    "Þ…": (
        "value, vector = pop(vy_globals.stack, 2); vy_globals.stack.append(distribute(vector, value))",
        2,
    ),
    "Þ↓": (
        "fn, vector = pop(vy_globals.stack, 2); vy_globals.stack.append(min(vy_zipmap(fn, vector), key=lambda x: x[-1])[0])",
        2,
    ),
    "Þ↑": (
        "fn, vector = pop(vy_globals.stack, 2); vy_globals.stack.append(max(vy_zipmap(fn, vector), key=lambda x: x[-1])[0])",
        2,
    ),
    "Þ×": (
        "vector = pop(vy_globals.stack); vy_globals.stack.append(all_combinations(vector));",
        1,
    ),
    "ÞF": (
        "vy_globals.stack.append(Generator(fibonacci(), is_numeric_sequence=True))",
        0,
    ),
    "Þ!": (
        "vy_globals.stack.append(Generator(factorials(), is_numeric_sequence=True))",
        0,
    ),
    "ÞU": ("vy_globals.stack.append(nub_sieve(iterable(pop(vy_globals.stack))))", 1),
    "ÞT": ("vy_globals.stack.append(transpose(pop(vy_globals.stack)))", 1),
    "ÞD": (
        "vy_globals.stack.append(Generator(diagonals(iterable(pop(vy_globals.stack), list))))",
        1,
    ),
    "ÞS": (
        "vy_globals.stack.append(Generator(sublists(iterable(pop(vy_globals.stack), list))))",
        1,
    ),
    "ÞṪ": (
        "rhs, lhs = pop(vy_globals.stack, 2); print(lhs, rhs) ;vy_globals.stack.append(Generator(itertools.zip_longest(*iterable(lhs), fillvalue=rhs)))",
        2,
    ),
    "Þ℅": (
        "top = iterable(pop(vy_globals.stack)); vy_globals.stack.append(random.sample(top, len(top)))",
        1,
    ),
    "Þ•": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(dot_product(iterable(lhs), iterable(rhs)))",
        2,
    ),
    "ÞṀ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append(matrix_multiply(iterable(lhs), iterable(rhs)))",
        2,
    ),
    "ÞḊ": ("vy_globals.stack.append(determinant(pop(vy_globals.stack)))", 1),
    "Þ/": ("vy_globals.stack.append(diagonal_main(deref(pop(vy_globals.stack))))", 1),
    "Þ\\": ("vy_globals.stack.append(diagonal_anti(deref(pop(vy_globals.stack))))", 1),
    "ÞR": (
        "fn, vector = pop(vy_globals.stack, 2); vy_globals.stack.append(foldl_rows(fn, deref(vector)))",
        2,
    ),
    "ÞC": (
        "fn, vector = pop(vy_globals.stack, 2); vy_globals.stack.append(foldl_cols(fn, deref(vector)))",
        2,
    ),
    "¨U": (
        "if not online_version: vy_globals.stack.append(request(pop(vy_globals.stack)))",
        1,
    ),
    "¨M": (
        "function, indices, original = pop(vy_globals.stack, 3); vy_globals.stack.append(map_at(function, iterable(original), iterable(indices)))",
        3,
    ),
    "¨,": ("vy_print(pop(vy_globals.stack), end=' ')", 1),
    "¨…": (
        "top = pop(vy_globals.stack); vy_globals.stack.append(top); vy_print(top, end=' ')",
        1,
    ),
    "¨t": ("vectorise(time.sleep, pop(vy_globals.stack))", 1),
    "kA": ("vy_globals.stack.append(string.ascii_uppercase)", 0),
    "ke": ("vy_globals.stack.append(math.e)", 0),
    "kf": ("vy_globals.stack.append('Fizz')", 0),
    "kb": ("vy_globals.stack.append('Buzz')", 0),
    "kF": ("vy_globals.stack.append('FizzBuzz')", 0),
    "kH": ("vy_globals.stack.append('Hello, World!')", 0),
    "kh": ("vy_globals.stack.append('Hello World')", 0),
    "k1": ("vy_globals.stack.append(1000)", 0),
    "k2": ("vy_globals.stack.append(10000)", 0),
    "k3": ("vy_globals.stack.append(100000)", 0),
    "k4": ("vy_globals.stack.append(1000000)", 0),
    "k5": ("vy_globals.stack.append(10000000)", 0),
    "ka": ("vy_globals.stack.append(string.ascii_lowercase)", 0),
    "kL": ("vy_globals.stack.append(string.ascii_letters)", 0),
    "kd": ("vy_globals.stack.append(string.digits)", 0),
    "k6": ("vy_globals.stack.append('0123456789abcdef')", 0),
    "k^": ("vy_globals.stack.append('0123456789ABCDEF')", 0),
    "ko": ("vy_globals.stack.append(string.octdigits)", 0),
    "kp": ("vy_globals.stack.append(string.punctuation)", 0),
    "kP": ("vy_globals.stack.append(string.printable)", 0),
    "kw": ("vy_globals.stack.append(string.whitespace)", 0),
    "kr": ("vy_globals.stack.append(string.digits + string.ascii_letters)", 0),
    "kB": (
        "vy_globals.stack.append(string.ascii_uppercase + string.ascii_lowercase)",
        0,
    ),
    "kZ": ("vy_globals.stack.append(string.ascii_uppercase[::-1])", 0),
    "kz": ("vy_globals.stack.append(string.ascii_lowercase[::-1])", 0),
    "kl": ("vy_globals.stack.append(string.ascii_letters[::-1])", 0),
    "ki": ("vy_globals.stack.append(math.pi)", 0),
    "kn": ("vy_globals.stack.append(math.nan)", 0),
    "kt": ("vy_globals.stack.append(math.tau)", 0),
    "kD": ("vy_globals.stack.append(date.today().isoformat())", 0),
    "kN": (
        "vy_globals.stack.append([dt.now().hour, dt.now().minute, dt.now().second])",
        0,
    ),
    "kḋ": ("vy_globals.stack.append(date.today().strftime('%d/%m/%Y'))", 0),
    "kḊ": ("vy_globals.stack.append(date.today().strftime('%m/%d/%y'))", 0),
    "kð": (
        "vy_globals.stack.append([date.today().day, date.today().month, date.today().year])",
        0,
    ),
    "kβ": ("vy_globals.stack.append('{}[]<>()')", 0),
    "kḂ": ("vy_globals.stack.append('()[]{}')", 0),
    "kß": ("vy_globals.stack.append('()[]')", 0),
    "kḃ": ("vy_globals.stack.append('([{')", 0),
    "k≥": ("vy_globals.stack.append(')]}')", 0),
    "k≤": ("vy_globals.stack.append('([{<')", 0),
    "kΠ": ("vy_globals.stack.append(')]}>')", 0),
    "kv": ("vy_globals.stack.append('aeiou')", 0),
    "kV": ("vy_globals.stack.append('AEIOU')", 0),
    "k∨": ("vy_globals.stack.append('aeiouAEIOU')", 0),
    "k⟇": ("vy_globals.stack.append(vyxal.commands.codepage)", 0),
    "k½": ("vy_globals.stack.append([1, 2])", 0),
    "kḭ": ("vy_globals.stack.append(2 ** 32)", 0),
    "k+": ("vy_globals.stack.append([1, -1])", 0),
    "k-": ("vy_globals.stack.append([-1, 1])", 0),
    "k≈": ("vy_globals.stack.append([0, 1])", 0),
    "k/": ("vy_globals.stack.append('/\\\\')", 0),
    "kR": ("vy_globals.stack.append(360)", 0),
    "kW": ("vy_globals.stack.append('https://')", 0),
    "k℅": ("vy_globals.stack.append('http://')", 0),
    "k↳": ("vy_globals.stack.append('https://www.')", 0),
    "k²": ("vy_globals.stack.append('http://www.')", 0),
    "k¶": ("vy_globals.stack.append(512)", 0),
    "k⁋": ("vy_globals.stack.append(1024)", 0),
    "k¦": ("vy_globals.stack.append(2048)", 0),
    "kṄ": ("vy_globals.stack.append(4096)", 0),
    "kṅ": ("vy_globals.stack.append(8192)", 0),
    "k¡": ("vy_globals.stack.append(16384)", 0),
    "kε": ("vy_globals.stack.append(32768)", 0),
    "k₴": ("vy_globals.stack.append(65536)", 0),
    "k×": ("vy_globals.stack.append(2147483648)", 0),
    "k⁰": ("vy_globals.stack.append('bcfghjklmnpqrstvwxyz')", 0),
    "k¹": ("vy_globals.stack.append('bcfghjklmnpqrstvwxz')", 0),
    "k•": ("vy_globals.stack.append(['qwertyuiop', 'asdfghjkl', 'zxcvbnm'])", 0),
    "kṠ": ("vy_globals.stack.append(dt.now().second)", 0),
    "kṀ": ("vy_globals.stack.append(dt.now().minute)", 0),
    "kḢ": ("vy_globals.stack.append(dt.now().hour)", 0),
    "kτ": ("vy_globals.stack.append(int(dt.now().strftime('%j')))", 0),
    "kṡ": ("vy_globals.stack.append(time.time())", 0),
    "k□": ("vy_globals.stack.append([[0,1],[1,0],[0,-1],[-1,0]])", 0),
    "k…": ("vy_globals.stack.append([[0,1],[1,0]])", 0),
    "kɽ": ("vy_globals.stack.append([-1,0,1])", 0),
    "k[": ("vy_globals.stack.append('[]')", 0),
    "k]": ("vy_globals.stack.append('][')", 0),
    "k(": ("vy_globals.stack.append('()')", 0),
    "k)": ("vy_globals.stack.append(')(')", 0),
    "k{": ("vy_globals.stack.append('{}')", 0),
    "k}": ("vy_globals.stack.append('}{')", 0),
    "k/": ("vy_globals.stack.append('/\\\\')", 0),
    "k\\": ("vy_globals.stack.append('\\\\/')", 0),
    "k<": ("vy_globals.stack.append('<>')", 0),
    "k>": ("vy_globals.stack.append('><')", 0),
    "kẇ": ("vy_globals.stack.append(dt.now().weekday())", 0),
    "kẆ": ("vy_globals.stack.append(dt.now().isoweekday())", 0),
    "k§": (
        "vy_globals.stack.append(['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'])",
        0,
    ),
    "kɖ": (
        "vy_globals.stack.append(['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'])",
        0,
    ),
    "kṁ": (
        "vy_globals.stack.append([31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)]",
        0,
    ),
    "k∪": ("vy_globals.stack.append('aeiouy')", 0),
    "k⊍": ("vy_globals.stack.append('AEIOUY')", 0),
    "k∩": ("vy_globals.stack.append('aeiouyAEIOUY')", 0),
    "kṗ": ("vy_globals.stack.append((1 + 5 ** 0.5) / 2)", 0),
    "k⋏": ("vy_globals.stack.append(2 ** 20)", 0),
    "k⋎": ("vy_globals.stack.append(2 ** 30)", 0),
}

transformers = {
    "⁽": "vy_globals.stack.append(function_A)",
    "v": "vy_globals.stack.append(transformer_vectorise(function_A, vy_globals.stack))",
    "&": "apply_to_register(function_A, vy_globals.stack)",
    "~": "dont_pop(function_A, vy_globals.stack)",
    "ß": "cond = pop(vy_globals.stack)\nif cond: vy_globals.stack += function_call(function_A, vy_globals.stack)",
    "₌": "para_apply(function_A, function_B, vy_globals.stack)",
    "₍": "para_apply(function_A, function_B, vy_globals.stack); rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append([lhs, rhs])",
}
