from typing import Callable, Tuple, List, Union

from vyxal.builtins import *
from vyxal.array_builtins import *
from vyxal.utilities import *

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


def make_cmd(
    to_fn_call: Union[str, Callable[[List[str]], str]], arity: int
) -> Tuple[str, int]:
    """
    Returns a tuple with the transpiled command and its arity.

    :param to_fn_call
      If Callable, takes a list of variables that hold values popped from the
      stack (reversed) and returns a string representing a value created by
      running some function.
      If str, its format method will be called with the aforementioned list
      of variables as arguments.
    on those variables
    :param arity The arity of the function
    """
    var_names = [f"x{n}" for n in range(arity, 0, -1)]
    if isinstance(to_fn_call, str):
        if arity > 0:
            fn_call = to_fn_call.format(*var_names)
        else:
            fn_call = to_fn_call
    else:
        fn_call = to_fn_call(var_names)
    if arity > 0:
        cmd = f"{', '.join(var_names[::-1])} = pop(vy_globals.stack, {arity});"
    else:
        cmd = ""
    cmd += f"res = {fn_call}; vy_globals.stack.append(res);"
    return cmd, arity


def fn_to_cmd(fn: Union[Callable, str], arity: int) -> Tuple[str, int]:
    """
    Returns a tuple with the transpiled command and its arity.

    :param fn The function to turn into a command, or its name
    :param arity The arity of the function
    """
    fn_name = fn if isinstance(fn, str) else fn.__name__
    return make_cmd(lambda var_names: f"{fn_name}({', '.join(var_names)})", arity)


command_dict = {
    "¬": make_cmd("not {}", 1),
    "∧": make_cmd("{} and {}", 2),
    "⟑": make_cmd("{1} and {0}", 2),
    "∨": make_cmd("{} or {}", 2),
    "⟇": make_cmd("{1} or {0}", 2),
    "÷": (
        "for item in iterable(pop(vy_globals.stack)): vy_globals.stack.append(item)",
        1,
    ),
    "•": fn_to_cmd(log, 2),
    "†": (
        "fn = pop(vy_globals.stack); vy_globals.stack += function_call(fn, vy_globals.stack)",
        1,
    ),
    "€": fn_to_cmd(split, 2),
    "½": fn_to_cmd(halve, 1),
    "↔": fn_to_cmd(combinations_replace_generate, 2),
    "⌐": fn_to_cmd(complement, 1),
    "æ": fn_to_cmd(is_prime, 1),
    "ʀ": (
        "vy_globals.stack.append(orderless_range(0, add(pop(vy_globals.stack), 1)))",
        1,
    ),
    "ʁ": make_cmd("orderless_range(0, {})", 1),
    "ɾ": (
        "vy_globals.stack.append(orderless_range(1, add(pop(vy_globals.stack), 1)))",
        1,
    ),
    "ɽ": make_cmd("orderless_range(1, {})", 1),
    "ƈ": fn_to_cmd(ncr, 2),
    "∞": make_cmd("non_negative_integers()", 0),
    "!": make_cmd("len(vy_globals.stack)", 0),
    '"': make_cmd("[{}, {}]", 2),
    "$": (
        "top, over = pop(vy_globals.stack, 2);"
        "vy_globals.stack.append(top);"
        "vy_globals.stack.append(over)",
        2,
    ),
    "%": fn_to_cmd(modulo, 2),
    "*": fn_to_cmd(multiply, 2),
    "+": fn_to_cmd(add, 2),
    ",": ("vy_print(pop(vy_globals.stack))", 1),
    "-": fn_to_cmd(subtract, 2),
    "/": fn_to_cmd(divide, 2),
    ":": (
        "temp = pop(vy_globals.stack);"
        "vy_globals.stack.append(temp);"
        "vy_globals.stack.append(deref(temp))",
        1,
    ),
    "^": ("vy_globals.stack = vy_globals.stack[::-1]", 0),
    "_": ("pop(vy_globals.stack)", 1),
    "<": make_cmd("compare({}, {}, Comparitors.LESS_THAN)", 2),
    ">": make_cmd("compare({}, {}, Comparitors.GREATER_THAN)", 2),
    "=": make_cmd("compare({}, {}, Comparitors.EQUALS)", 2),
    "?": make_cmd("get_input(0)", 0),
    "A": make_cmd("int(all(iterable({})))", 1),
    "B": make_cmd("vy_int({}, 2)", 1),
    "C": fn_to_cmd(chrord, 1),
    "D": (
        "temp = pop(vy_globals.stack);"
        "vy_globals.stack.append(temp);"
        "vy_globals.stack.append(deref(temp));"
        "vy_globals.stack.append(deref(vy_globals.stack[-1]))",
        1,
    ),
    "E": fn_to_cmd(vy_eval, 1),
    "F": make_cmd("vy_filter({1}, {0})", 2),
    "G": make_cmd("vy_max(iterable({}))", 1),
    "H": make_cmd("vy_int({}, 16)", 1),
    "I": fn_to_cmd(vy_int, 1),
    "J": fn_to_cmd(join, 2),
    "K": fn_to_cmd(divisors_of, 1),
    "L": make_cmd("len(iterable({}))", 1),
    "M": (
        "fn, vector = pop(vy_globals.stack, 2); temp = vy_map(fn, vector); vy_globals.stack.append(temp)",
        2,
    ),
    "N": fn_to_cmd(negate, 1),
    "O": make_cmd("iterable({}).count({})", 2),
    "P": make_cmd("vy_str({}).strip(vy_str({}))", 2),
    "Q": ("exit()", 0),
    "R": (
        "fn, vector = pop(vy_globals.stack, 2); vy_globals.stack += vy_reduce(fn, vector)",
        2,
    ),
    "S": fn_to_cmd(vy_str, 1),
    "T": (
        "vy_globals.stack.append([i for (i, x) in enumerate(pop(vy_globals.stack)) if bool(x)])",
        1,
    ),
    "U": make_cmd("Generator(uniquify({}))", 1),
    "V": fn_to_cmd(replace, 3),
    "W": ("vy_globals.stack = [deref(vy_globals.stack)]; print(vy_globals.stack)", 0),
    "X": ("vy_globals.context_level += 1", 0),
    "Y": fn_to_cmd(interleave, 2),
    "Z": make_cmd(
        "Generator(vy_zip(iterable({}), iterable({})))",
        2,
    ),
    "a": ("vy_globals.stack.append(int(any(iterable(pop(vy_globals.stack)))))", 1),
    "b": fn_to_cmd(vy_bin, 1),
    "c": (
        "needle, haystack = pop(vy_globals.stack, 2);"
        "haystack = iterable(haystack, str)\n"
        "if type(haystack) is str: needle = vy_str(needle)\n"
        "vy_globals.stack.append(int(needle in iterable(haystack, str)))",
        2,
    ),
    "d": make_cmd("multiply({}, 2)", 1),
    "e": fn_to_cmd(exponate, 2),
    "f": make_cmd("flatten(iterable({}))", 1),
    "g": make_cmd("vy_min(iterable({}))", 1),
    "h": make_cmd("iterable({})[0]", 1),
    "i": fn_to_cmd(index, 2),
    "j": fn_to_cmd(join_on, 2),
    "l": fn_to_cmd(nwise_pair, 2),
    "m": fn_to_cmd(mirror, 1),
    "n": make_cmd(
        "vy_globals.context_values[vy_globals.context_level % len(vy_globals.context_values)]",
        0,
    ),
    "o": fn_to_cmd(remove, 2),
    "p": fn_to_cmd(prepend, 2),
    "q": fn_to_cmd(uneval, 1),
    "r": fn_to_cmd(orderless_range, 2),
    "s": fn_to_cmd(vy_sorted, 1),
    "t": make_cmd("iterable({})[-1]", 1),
    "u": make_cmd("-1", 0),
    "w": make_cmd("[{}]", 1),
    "x": ("vy_globals.stack += this_function(vy_globals.stack)", 0),
    "y": ("vy_globals.stack += uninterleave(pop(vy_globals.stack))", 1),
    "z": (
        "fn, vector = pop(vy_globals.stack, 2); vy_globals.stack += vy_zipmap(fn, vector)",
        2,
    ),
    "↑": make_cmd("max({}, key=lambda x: x[-1])", 1),
    "↓": make_cmd("min({}, key=lambda x: x[-1])", 1),
    "∴": fn_to_cmd(vy_max, 2),
    "∵": fn_to_cmd(vy_min, 2),
    "β": make_cmd("utilities.to_ten({}, {})", 2),
    "τ": make_cmd("utilities.from_ten({}, {})", 2),
    "›": ("vy_globals.stack.append(add(pop(vy_globals.stack), 1))", 1),
    "‹": ("vy_globals.stack.append(subtract(pop(vy_globals.stack), 1))", 1),
    "∷": ("vy_globals.stack.append(modulo(pop(vy_globals.stack), 2))", 1),
    "¤": ("vy_globals.stack.append('')", 0),
    "ð": ("vy_globals.stack.append(' ')", 0),
    "ȧ": fn_to_cmd(vy_abs, 1),
    "ḃ": (
        "vy_globals.stack.append(int(not compare(pop(vy_globals.stack), 0, Comparitors.EQUALS)))",
        1,
    ),
    "ċ": (
        "vy_globals.stack.append(compare(pop(vy_globals.stack), 1, Comparitors.NOT_EQUALS))",
        1,
    ),
    "ḋ": fn_to_cmd(
        vy_divmod, 2
    ),  # Dereference because generators could accidentally get exhausted.
    "ė": (
        "vy_globals.stack.append(Generator(enumerate(iterable(pop(vy_globals.stack)))))",
        1,
    ),
    "ḟ": fn_to_cmd(find, 2),
    "ġ": (
        "rhs = pop(vy_globals.stack)\nif vy_type(rhs) in [list, Generator]: vy_globals.stack.append(gcd(rhs))\nelse: vy_globals.stack.append(gcd(pop(vy_globals.stack), rhs))",
        2,
    ),
    "ḣ": (
        "top = iterable(pop(vy_globals.stack)); vy_globals.stack.append(top[0]); vy_globals.stack.append(top[1:])",
        1,
    ),
    "ḭ": fn_to_cmd(integer_divide, 2),
    "ŀ": (
        "start, needle, haystack = pop(vy_globals.stack, 3); vy_globals.stack.append(find(haystack, needle, start))",
        3,
    ),
    "ṁ": (
        "top = iterable(pop(vy_globals.stack)); vy_globals.stack.append(divide(summate(top), len(top)))",
        1,
    ),
    "ṅ": fn_to_cmd(first_n, 1),
    "ȯ": fn_to_cmd(first_n, 2),
    "ṗ": ("vy_globals.stack.append(powerset(iterable(pop(vy_globals.stack))))", 1),
    "ṙ": fn_to_cmd(vy_round, 1),
    "ṡ": (
        "fn , vector = pop(vy_globals.stack, 2); vy_globals.stack.append(vy_sorted(vector, fn))",
        2,
    ),
    "ṫ": (
        "vector = iterable(pop(vy_globals.stack)); vy_globals.stack.append(vector[:-1]); vy_globals.stack.append(vector[-1])",
        1,
    ),
    "ẇ": fn_to_cmd(wrap, 2),
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
    "⁋": fn_to_cmd(osabie_newline_join, 1),
    "§": fn_to_cmd(vertical_join, 1),
    "ε": fn_to_cmd(vertical_join, 2),
    "¡": fn_to_cmd(factorial, 1),
    "∑": (
        "temp = summate(pop(vy_globals.stack));vy_globals.stack.append(temp);print(vy_globals.stack);",
        1,
    ),
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
    "Ċ": fn_to_cmd(counts, 1),
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
    "Ġ": make_cmd("group_consecutive(iterable({}))", 1),
    "Ḣ": ("vy_globals.stack.append(iterable(pop(vy_globals.stack))[1:])", 1),
    "İ": fn_to_cmd(indexed_into, 2),
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
    "Ṙ": fn_to_cmd(reverse, 1),
    "Ṡ": ("vy_globals.stack = [summate(vy_globals.stack)]", 0),
    "Ṫ": ("vy_globals.stack.append(iterable(pop(vy_globals.stack), str)[:-1])", 1),
    "Ẇ": make_cmd("split({}, {}, True)", 2),
    "Ẋ": fn_to_cmd(cartesian_product, 2),
    "Ẏ": make_cmd("one_argument_tail_index({}, {}, 0)", 2),
    "Ż": make_cmd("one_argument_tail_index({}, {}, 1)", 2),
    "⁰": ("vy_globals.stack.append(vy_globals.input_values[0][0][-1])", 0),
    "¹": ("vy_globals.stack.append(vy_globals.input_values[0][0][-2])", 0),
    "²": fn_to_cmd(square, 1),
    "∇": (
        "c, b, a = pop(vy_globals.stack, 3); vy_globals.stack.append(c); vy_globals.stack.append(a); vy_globals.stack.append(b)",
        3,
    ),
    "⌈": fn_to_cmd(ceiling, 1),
    "⌊": fn_to_cmd(floor, 1),
    "¯": fn_to_cmd(deltas, 1),
    "±": fn_to_cmd(sign_of, 1),
    "₴": ("vy_print(pop(vy_globals.stack), end='')", 1),
    "…": (
        "top = pop(vy_globals.stack); vy_globals.stack.append(top); vy_print(top)",
        0,
    ),
    "□": (
        """if vy_globals.inputs: vy_globals.stack.append(vy_globals.inputs)
else:
    s, x = [], input()
    while x:
        s.append(vy_eval(x)); x = input()""",
        0,
    ),
    "↳": fn_to_cmd(rshift, 2),
    "↲": fn_to_cmd(lshift, 2),
    "⋏": fn_to_cmd(bit_and, 2),
    "⋎": fn_to_cmd(bit_or, 2),
    "꘍": fn_to_cmd(bit_xor, 2),
    "ꜝ": fn_to_cmd(bit_not, 1),
    "℅": make_cmd("random.choice(iterable({}))", 1),
    "≤": make_cmd("compare({}, {}, Comparitors.LESS_THAN_EQUALS)", 2),
    "≥": make_cmd("compare({}, {}, Comparitors.GREATER_THAN_EQUALS)", 2),
    "≠": make_cmd("int(deref({}) != deref({}))", 2),
    "⁼": make_cmd("int(deref({}) == deref({})", 2),
    "ƒ": fn_to_cmd(fractionify, 1),
    "ɖ": fn_to_cmd(decimalify, 1),
    "×": ("vy_globals.stack.append('*')", 0),
    "∪": fn_to_cmd(set_union, 2),
    "∩": fn_to_cmd(set_intersection, 2),
    "⊍": fn_to_cmd(set_caret, 2),
    "£": ("register = pop(vy_globals.stack)", 1),
    "¥": ("vy_globals.stack.append(register)", 0),
    "⇧": fn_to_cmd(graded, 1),
    "⇩": fn_to_cmd(graded_down, 1),
    "Ǎ": fn_to_cmd(two_power, 1),
    "ǎ": fn_to_cmd(nth_prime, 1),
    "Ǐ": fn_to_cmd(prime_factors, 1),
    "ǐ": fn_to_cmd(all_prime_factors, 1),
    "Ǒ": fn_to_cmd(order, 2),
    "ǒ": fn_to_cmd(is_empty, 1),
    "Ǔ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack += overloaded_iterable_shift(lhs, rhs, ShiftDirections.LEFT)",
        2,
    ),
    "ǔ": (
        "rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack += overloaded_iterable_shift(lhs, rhs, ShiftDirections.RIGHT)",
        2,
    ),
    "¢": fn_to_cmd(infinite_replace, 3),
    "↵": fn_to_cmd(split_newlines_or_pow_10, 1),
    "⅛": ("vy_globals.global_stack.append(pop(vy_globals.stack))", 1),
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
    "∆S": make_cmd("vectorise(math.asin, {})", 1),
    "∆C": make_cmd("vectorise(math.acos, {})", 1),
    "∆T": make_cmd("math.atan({})", 1),
    "∆q": make_cmd("polynomial([{1}, {0}, 0])", 2),
    "∆Q": make_cmd("polynomial([1, {1}, {0}])", 2),
    "∆P": make_cmd("polynomial(iterable({}))", 1),
    "∆s": make_cmd("vectorise(math.sin, {})", 1),
    "∆c": make_cmd("vectorise(math.cos, {})", 1),
    "∆t": make_cmd("vectorise(math.tan, {})", 1),
    "∆ƈ": make_cmd("divide(factorial({}), factorial(subtract(lhs, {})))", 2),
    "∆±": make_cmd("vectorise(math.copysign, {}, {})", 2),
    "∆K": make_cmd("summate(join(0, divisors_of({})[:-1]))", 1),
    "∆²": fn_to_cmd(is_square, 1),
    "∆e": make_cmd("vectorise(math.exp, {})", 1),
    "∆E": make_cmd("vectorise(math.expm1, {})", 1),
    "∆L": make_cmd("vectorise(math.log, {})", 1),
    "∆l": make_cmd("vectorise(math.log2, {})", 1),
    "∆τ": make_cmd("vectorise(math.log10, {})", 1),
    "∆d": fn_to_cmd(distance_between, 2),
    "∆D": make_cmd("vectorise(math.degrees, {})", 1),
    "∆R": make_cmd("vectorise(math.radians, {})", 1),
    "∆≤": make_cmd("compare(vy_abs({}), 1, Comparitors.LESS_THAN_EQUALS)", 1),
    "∆Ṗ": fn_to_cmd(next_prime, 1),
    "∆ṗ": fn_to_cmd(prev_prime, 1),
    "∆p": fn_to_cmd(closest_prime, 1),
    "∆ṙ": (
        "vy_globals.stack.append(unsympy(sympy.prod(map(sympy.poly('x').__sub__, iterable(pop(vy_globals.stack)))).all_coeffs()[::-1]))",
        1,
    ),
    "∆Ṙ": ("vy_globals.stack.append(random.random())", 0),
    "∆W": make_cmd(
        "vectorise(round, {}, {})", 2
    ),  # if you think I'm making this work with strings, then you can go commit utter go awayance. smh.
    "∆Ŀ": make_cmd("vectorise(lambda x, y: int(numpy.lcm(x, y)), {}, {})", 2),
    "øo": make_cmd("infinite_replace({}, {}, '')", 2),
    "øV": (
        "replacement, needle, haystack = pop(vy_globals.stack, 3); vy_globals.stack.append(infinite_replace(haystack, needle, replacement))",
        3,
    ),
    "øc": make_cmd(
        "'«' + utilities.from_ten(utilities.to_ten({}, utilities.base27alphabet), encoding.codepage_string_compress) + '«'",
        1,
    ),
    "øC": make_cmd(
        "'»' + utilities.from_ten({}, encoding.codepage_number_compress) + '»'", 1
    ),
    "øĊ": fn_to_cmd(centre, 1),
    "øm": make_cmd("palindromise(iterable({}))", 1),
    "øe": make_cmd(
        "run_length_encode(iterable({}, str))",
        1,
    ),
    "ød": fn_to_cmd(run_length_decode, 1),
    "øD": fn_to_cmd(dictionary_compress, 1),
    "øW": make_cmd("split_on_words(vy_str({}))", 1),
    "øṙ": make_cmd(
        "regex_replace(vy_str({}), vy_str({}), {})",
        3,
    ),
    "øp": make_cmd("int(str({}).startswith(str({})))", 2),
    "øP": fn_to_cmd(pluralise, 2),
    "øṁ": fn_to_cmd(vertical_mirror, 1),
    "øṀ": make_cmd(
        "vertical_mirror({}, ['()[]{{}}<>/\\\\', ')(][}}{{><\\\\/']))",
        1,
    ),
    "ø¦": fn_to_cmd(vertical_mirror, 2),
    "Þ…": fn_to_cmd(distribute, 2),
    "Þ↓": make_cmd("min(vy_zipmap({1}, {0}), key=lambda x: x[-1])[0]", 2),
    "Þ↑": make_cmd("max(vy_zipmap({1}, {0}), key=lambda x: x[-1])[0]", 2),
    "Þ×": fn_to_cmd(all_combinations, 1),
    "ÞF": make_cmd(
        "Generator(fibonacci(), is_numeric_sequence=True)",
        0,
    ),
    "Þ!": make_cmd(
        "Generator(factorials(), is_numeric_sequence=True)",
        0,
    ),
    "ÞU": make_cmd("nub_sieve(iterable({}))", 1),
    "ÞT": fn_to_cmd(transpose, 1),
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
    "Þ•": make_cmd("dot_product(iterable({}), iterable({}))", 2),
    "ÞṀ": make_cmd("matrix_multiply(iterable({}), iterable({}))", 2),
    "ÞḊ": fn_to_cmd(determinant, 1),
    "Þ/": make_cmd("diagonal_main(deref({}))", 1),
    "Þ\\": make_cmd("diagonal_anti(deref({}))", 1),
    "ÞR": make_cmd("foldl_rows({1}, deref({0}))", 2),
    "ÞC": make_cmd("foldl_cols({1}, deref({0}))", 2),
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
    "kA": make_cmd("string.ascii_uppercase", 0),
    "ke": make_cmd("math.e", 0),
    "kf": make_cmd("'Fizz'", 0),
    "kb": make_cmd("'Buzz'", 0),
    "kF": make_cmd("'FizzBuzz'", 0),
    "kH": make_cmd("'Hello, World!'", 0),
    "kh": make_cmd("'Hello World'", 0),
    "k1": make_cmd("1000", 0),
    "k2": make_cmd("10000", 0),
    "k3": make_cmd("100000", 0),
    "k4": make_cmd("1000000", 0),
    "k5": make_cmd("10000000", 0),
    "ka": make_cmd("string.ascii_lowercase", 0),
    "kL": make_cmd("string.ascii_letters", 0),
    "kd": make_cmd("string.digits", 0),
    "k6": make_cmd("'0123456789abcdef'", 0),
    "k^": make_cmd("'0123456789ABCDEF'", 0),
    "ko": make_cmd("string.octdigits", 0),
    "kp": make_cmd("string.punctuation", 0),
    "kP": make_cmd("string.printable", 0),
    "kw": make_cmd("string.whitespace", 0),
    "kr": make_cmd("string.digits + string.ascii_letters", 0),
    "kB": make_cmd(
        "string.ascii_uppercase + string.ascii_lowercase",
        0,
    ),
    "kZ": make_cmd("string.ascii_uppercase[::-1]", 0),
    "kz": make_cmd("string.ascii_lowercase[::-1]", 0),
    "kl": make_cmd("string.ascii_letters[::-1]", 0),
    "ki": make_cmd("math.pi", 0),
    "kn": make_cmd("math.nan", 0),
    "kt": make_cmd("math.tau", 0),
    "kD": make_cmd("date.today().isoformat()", 0),
    "kN": make_cmd(
        "[dt.now().hour, dt.now().minute, dt.now().second]",
        0,
    ),
    "kḋ": make_cmd("date.today().strftime('%d/%m/%Y')", 0),
    "kḊ": make_cmd("date.today().strftime('%m/%d/%y')", 0),
    "kð": make_cmd(
        "[date.today().day, date.today().month, date.today().year]",
        0,
    ),
    "kβ": make_cmd("'{}[]<>()'", 0),
    "kḂ": make_cmd("'()[]{}'", 0),
    "kß": make_cmd("'()[]'", 0),
    "kḃ": make_cmd("'([{'", 0),
    "k≥": make_cmd("')]}'", 0),
    "k≤": make_cmd("'([{<'", 0),
    "kΠ": make_cmd("')]}>'", 0),
    "kv": make_cmd("'aeiou'", 0),
    "kV": make_cmd("'AEIOU'", 0),
    "k∨": make_cmd("'aeiouAEIOU'", 0),
    "k⟇": make_cmd("vyxal.commands.codepage", 0),
    "k½": make_cmd("[1, 2]", 0),
    "kḭ": make_cmd("2 ** 32", 0),
    "k+": make_cmd("[1, -1]", 0),
    "k-": make_cmd("[-1, 1]", 0),
    "k≈": make_cmd("[0, 1]", 0),
    "kR": make_cmd("360", 0),
    "kW": make_cmd("'https://'", 0),
    "k℅": make_cmd("'http://'", 0),
    "k↳": make_cmd("'https://www.'", 0),
    "k²": make_cmd("'http://www.'", 0),
    "k¶": make_cmd("512", 0),
    "k⁋": make_cmd("1024", 0),
    "k¦": make_cmd("2048", 0),
    "kṄ": make_cmd("4096", 0),
    "kṅ": make_cmd("8192", 0),
    "k¡": make_cmd("16384", 0),
    "kε": make_cmd("32768", 0),
    "k₴": make_cmd("65536", 0),
    "k×": make_cmd("2147483648", 0),
    "k⁰": make_cmd("'bcfghjklmnpqrstvwxyz'", 0),
    "k¹": make_cmd("'bcfghjklmnpqrstvwxz'", 0),
    "k•": make_cmd("['qwertyuiop', 'asdfghjkl', 'zxcvbnm']", 0),
    "kṠ": make_cmd("dt.now().second", 0),
    "kṀ": make_cmd("dt.now().minute", 0),
    "kḢ": make_cmd("dt.now().hour", 0),
    "kτ": make_cmd("int(dt.now().strftime('%j'))", 0),
    "kṡ": make_cmd("time.time()", 0),
    "k□": make_cmd("[[0,1],[1,0],[0,-1],[-1,0]]", 0),
    "k…": make_cmd("[[0,1],[1,0]]", 0),
    "kɽ": make_cmd("[-1,0,1]", 0),
    "k[": make_cmd("'[]'", 0),
    "k]": make_cmd("']['", 0),
    "k(": make_cmd("'()'", 0),
    "k)": make_cmd("')('", 0),
    "k{": make_cmd("'{}'", 0),
    "k}": make_cmd("'}{'", 0),
    "k/": make_cmd("'/\\\\'", 0),
    "k\\": make_cmd("'\\\\/'", 0),
    "k<": make_cmd("'<>'", 0),
    "k>": make_cmd("'><'", 0),
    "kẇ": make_cmd("dt.now().weekday()", 0),
    "kẆ": make_cmd("dt.now().isoweekday()", 0),
    "k§": make_cmd(
        "['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']",
        0,
    ),
    "kɖ": make_cmd(
        "['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']",
        0,
    ),
    "kṁ": make_cmd("[31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]", 0),
    "k∪": make_cmd("'aeiouy'", 0),
    "k⊍": make_cmd("'AEIOUY'", 0),
    "k∩": make_cmd("'aeiouyAEIOUY'", 0),
    "kṗ": make_cmd("(1 + 5 ** 0.5) / 2", 0),
    "k⋏": make_cmd("2 ** 20", 0),
    "k⋎": make_cmd("2 ** 30", 0),
}

transformers = {
    "⁽": "vy_globals.stack.append(function_A)",
    "v": "temp = transformer_vectorise(function_A, vy_globals.stack); vy_globals.stack.append(temp)",
    "&": "apply_to_register(function_A, vy_globals.stack)",
    "~": "dont_pop(function_A, vy_globals.stack)",
    "ß": "cond = pop(vy_globals.stack)\nif cond: vy_globals.stack += function_call(function_A, vy_globals.stack)",
    "₌": "para_apply(function_A, function_B, vy_globals.stack)",
    "₍": "para_apply(function_A, function_B, vy_globals.stack); rhs, lhs = pop(vy_globals.stack, 2); vy_globals.stack.append([lhs, rhs])",
}
