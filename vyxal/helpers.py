"""This is where the cool functions go that help out stuff.

They aren't directly attached to an element. Consequently, you need to
use type annotations here.
"""

import ast
import collections
import inspect
import itertools
import math  # lgtm [py/unused-import]
import textwrap
import types
from typing import Any, List, Union

import sympy
from sympy.parsing.sympy_parser import (
    convert_xor,
    implicit_multiplication_application,
    standard_transformations,
)

import vyxal.dictionary
import vyxal.encoding
from vyxal import lexer
from vyxal.context import DEFAULT_CTX, Context
from vyxal.LazyList import *

NUMBER_TYPE = "number"
SCALAR_TYPE = "scalar"
VyList = Union[list, LazyList]


def case_of(value: str) -> int:
    """Returns 1 for all uppercase, 0 for all lowercase, and -1 for
    mixed case."""
    if all(map(lambda x: x.isupper(), value)):
        return 1
    elif all(map(lambda x: x.islower(), value)):
        return 0
    return -1


@lazylist
def collect_until_false(
    predicate: types.FunctionType,
    function: types.FunctionType,
    initial: Any,
    ctx: Context,
) -> List[Any]:
    """Given a function, apply it on a given value while a predicate function
    returns True. Return the list of values that were collected."""
    val = initial
    while safe_apply(predicate, val, ctx=ctx):
        yield val
        val = safe_apply(function, val, ctx=ctx)


def concat(vec1: VyList, vec2: VyList, ctx: Context = None) -> VyList:
    """Concatenate two lists/lazylists"""
    if LazyList not in (type(vec1), type(vec2)):
        return vec1 + vec2

    @lazylist
    def gen():
        for item in vec1:
            yield item
        for item in vec2:
            yield item

    return gen()


def deep_copy(value: Any) -> Any:
    """Because lists and lazylists use memory references. Frick them."""
    if type(value) not in (list, LazyList):
        return value  # because primitives are all like "ooh look at me
        # I don't have any fancy memory references because I'm an epic
        # chad unlike those virgin memory reference needing lists".

    return LazyList(itertools.tee(value)[-1])


def dict_to_list(dictionary: dict) -> List[Any]:
    """Returns a dictionary as [[key, value]]"""
    return [[str(key), dictionary[key]] for key in dictionary]


def digits(num: NUMBER_TYPE) -> List[int]:
    """Get the digits of a (possibly Rational) number.
    This differs from to_base_digits because it works with floats.
    This does NOT include signs and decimal points"""
    if type(num) is sympy.Rational:
        num = float(num)

    return [int(let) if let not in "-." else let for let in str(num)]


@lazylist
def enumerate_md(haystack: VyList, _index_stack: tuple = ()) -> VyList:
    """Gets all the multi-dimensional indices of haystack"""
    for i, item in enumerate(haystack):
        if type(item) in (list, LazyList):
            yield from enumerate_md(item, _index_stack + (i,))
        elif type(item) is str and len(item) > 1:
            yield from enumerate_md(list(item), _index_stack + (i,))
        else:
            yield list(_index_stack) + [i]


@lazylist
def fixed_point(
    function: types.FunctionType, initial: Any, ctx: Context
) -> List[Any]:
    """Repeat function until the result is no longer unique.
    Uses initial as the initial value"""
    previous = None
    current = simplify(initial)

    while previous != current:
        yield current
        previous = deep_copy(current)
        current = safe_apply(function, current, ctx=ctx)


def foldl(
    function: types.FunctionType,
    vector: List[Any],
    initial=None,
    *,
    ctx: Context,
) -> Any:
    """Reduce vector by function"""
    if len(vector) == 0:
        return 0

    working = initial

    for item in vector:
        if working is None:
            working = item
        else:
            working = safe_apply(function, working, item, ctx=ctx)

    return working if working is not None else 0


def format_string(pattern: str, data: Union[str, VyList]) -> str:
    """Returns the pattern formatted with the given data. If the data is
    a string, then the string is reused if there is more than one % to
    be formatted. Otherwise (the data is a list), % are cyclically
    substituted"""
    ret = ""
    index = 0
    f_index = 0

    while index < len(pattern):
        if pattern[index] == "\\":
            ret += "\\" + pattern[index + 1]
            index += 1
        elif pattern[index] == "%":
            ret += str(data[f_index % len(data)])
            f_index += 1
        else:
            ret += pattern[index]
        index += 1
    return ret


def from_base_alphabet(value: str, alphabet: str) -> int:
    """Returns value in base 10 using base len(alphabet)
    [bijective base]"""
    ret = 0
    for digit in value:
        ret = len(alphabet) * ret + alphabet.find(digit)

    return ret


def from_base_digits(digit_list: List[NUMBER_TYPE], base: int) -> int:
    """Returns digits in base 10 using arbitrary base 'base'"""
    # I may have stolen this from Jelly
    ret = 0
    for digit in digit_list:
        ret = base * ret + digit

    return ret


def get_input(ctx: Context) -> Any:
    """Returns the next input depending on where ctx tells to get the
    input from."""
    if ctx.use_top_input:
        if ctx.inputs[0][0]:
            ret = ctx.inputs[0][0][ctx.inputs[0][1] % len(ctx.inputs[0][0])]
            ctx.inputs[0][1] += 1
            return ret
        else:
            try:
                temp = vy_eval(input("> " * ctx.repl_mode), ctx)
                if ctx.empty_input_is_zero and temp == "":
                    return 0
            except Exception:  # skipcq: PYL-W0703
                temp = 0
            return temp
    else:
        if ctx.inputs[-1][0]:
            ret = ctx.inputs[-1][0][ctx.inputs[-1][1] % len(ctx.inputs[-1][0])]
            ctx.inputs[-1][1] += 1
            return ret
        else:
            if len(ctx.inputs) == 1:
                ctx.use_top_input = True
                temp = get_input(ctx)
                ctx.use_top_input = False
                return temp
            else:
                return 0


def indent_str(string: str, indent: int, end="\n") -> str:
    """Indent a multiline string with 4 spaces, with a newline or `end` afterwards."""
    return textwrap.indent(string, "    " * indent) + end


def indent_code(*code, indent: int = 1) -> str:
    """Indent multiple lines (`*code`) by the given amount, then join on newlines."""
    return "\n".join(indent_str(line, indent, end="") for line in code) + "\n"


def invert_brackets(lhs: str) -> str:
    """
    Helper function to swap brackets and parentheses in a string
    """
    res = ""
    pairs = ["()", "[]", "{}", "<>", "/\\"]
    open_close = {x[0]: x[1] for x in pairs}
    close_open = {x[1]: x[0] for x in pairs}
    for char in lhs:
        if char in open_close:
            res += open_close[char]
        elif char in close_open:
            res += close_open[char]
        else:
            res += char
    return res


def is_sympy(value):
    """Whether or not this is a Sympy type"""
    return isinstance(value, sympy.Basic)


def iterable(
    item: Any, number_type: Any = None, ctx: Context = DEFAULT_CTX
) -> Union[LazyList, Union[list, str]]:
    """Turn a value into an iterable"""
    item_type = type(item)
    if item_type is int or is_sympy(item):
        if ctx.number_as_range or number_type is range:
            return LazyList(range(ctx.range_start, int(item) + ctx.range_end))
        else:
            return digits(item)
    else:
        return item


def join_with(lhs, rhs):
    """A generator to concatenate two iterables together"""
    for item in lhs:
        yield vyxalify(item)

    for item in rhs:
        yield vyxalify(item)


def levenshtein_distance(s1: str, s2: str) -> int:
    """Returns the levenshtein distance between two strings"""
    # https://stackoverflow.com/a/32558749
    if len(s1) > len(s2):
        s1, s2 = s2, s1

    distances = range(len(s1) + 1)
    for i2, c2 in enumerate(s2):
        distances_ = [i2 + 1]
        for i1, c1 in enumerate(s1):
            if c1 == c2:
                distances_.append(distances[i1])
            else:
                distances_.append(
                    1 + min((distances[i1], distances[i1 + 1], distances_[-1]))
                )
        distances = distances_
    return distances[-1]


def local_minima(lhs: str) -> List[Union[int, float]]:
    """Find the local minima of a mathematical function using Sympy"""
    x = sympy.symbols("x")
    d_dx = sympy.diff(sympy.sympify(lhs), x)
    second_dx = sympy.diff(d_dx, x)
    zeros = sympy.solve(d_dx, x)

    return LazyList(z for z in zeros if second_dx.subs(x, z) > 0)


def local_maxima(lhs: str) -> List[Union[int, float]]:
    """Find the local minima of a mathematical function using Sympy"""
    x = sympy.symbols("x")
    d_dx = sympy.diff(sympy.sympify(lhs), x)
    second_dx = sympy.diff(d_dx, x)
    zeros = sympy.solve(d_dx, x)

    return LazyList(z for z in zeros if second_dx.subs(x, z) < 0)


def keep(haystack: Any, needle: Any) -> Any:
    """Used for keeping only needle in haystack"""
    out = "" if type(haystack) is str else []
    for item in haystack:
        if item in needle:
            if type(haystack) is str:
                out += item
            else:
                out.append(item)
    return out


def make_equation(eqn: str) -> sympy:
    """Returns a sympy equation from a string"""
    eqn = eqn.split("=")
    return sympy.Eq(make_expression(eqn[0]), make_expression(eqn[1]))


def make_expression(expr: str) -> sympy:
    """Turns a string into a nice sympy expression"""
    transformations = standard_transformations + (
        implicit_multiplication_application,
        convert_xor,
    )

    return sympy.parse_expr(expr, transformations=transformations)


def max_by(vec: VyList, key=lambda x: x, cmp=None, ctx=DEFAULT_CTX):
    """
    The maximum of a list according to a key function and/or a comparator.

    Parameters:
    key: Any -> Any
    A function to first transform each element of the list before comparing.
    cmp: (Any, Any) -> bool
    A binary function to check if its first argument is less than the second.
    """
    if key is None:

        def key(x, ctx=None):
            return x

    if cmp is None:

        def cmp(a, b, ctx=None):
            return a < b

    return foldl(
        lambda a, b, ctx=ctx: b
        if safe_apply(
            cmp,
            safe_apply(key, a, ctx=ctx),
            safe_apply(key, b, ctx=ctx),
            ctx=ctx,
        )
        else a,
        vec,
        ctx=ctx,
    )


def min_by(vec: VyList, key=None, cmp=None, ctx=DEFAULT_CTX):
    """
    The minimum of a list according to a key function and/or a comparator.

    Parameters:
    key: Any -> Any
    A function to first transform each element of the list before comparing.
    cmp: (Any, Any) -> bool
    A binary function to check if its first argument is less than the second.
    """
    if key is None:

        def key(x, ctx=None):
            return x

    if cmp is None:

        def cmp(a, b, ctx=None):
            return a < b

    return foldl(
        lambda a, b, ctx=ctx: a
        if cmp(key(a, ctx=ctx), key(b, ctx=ctx), ctx=ctx)
        else b,
        vec,
        ctx=ctx,
    )


def mold(
    content: VyList,
    shape: VyList,
) -> VyList:

    """
    Mold a list into a shape.

    Parameters:
    content: VyList
    The list to mold.
    shape: VyList
    The shape to mold the list into.

    Returns:
    VyList
    The content, molded into the shape.
    """
    final = []
    original, content = itertools.tee(content)
    for item in shape:
        temp = []
        if isinstance(item, (int, str)):
            item = [item]
        for _ in item:
            obj = next(content, None)
            if obj is None:
                content = itertools.tee(original)[1]
                obj = next(content)
            temp.append(obj)
        if temp:
            if len(temp) == 1:
                temp = deep_copy(temp[0])
            else:
                temp = deep_copy(temp)
            final.append(temp)

    return final


def mold_without_repeat(
    content: VyList,
    shape: VyList,
) -> VyList:

    """
    Mold a list into a shape but don't reuse content.

    Parameters:
    content: VyList
    The list to mold.
    shape: VyList
    The shape to mold the list into.

    Returns:
    VyList
    The content, molded into the shape.
    """
    final = []
    _, content = itertools.tee(content)
    for item in shape:
        temp = []
        if isinstance(item, (int, str)):
            item = [item]
        for _ in item:
            obj = next(content, None)
            if obj is None:
                break
            temp.append(obj)
        if temp:
            final.append(temp[::])

    return final


def pad_to_square(array: VyList) -> VyList:
    """
    Returns an array padded to the square of the largest dimension.
    """
    mat = list(map(list, array))
    max_dim = max(len(mat), max(map(len, mat)))
    for row in mat:
        for _ in range(max_dim - len(row)):
            row.append(0)
    if max_dim > len(mat):
        mat += [[0] * max_dim for _ in range(max_dim - len(mat))]
    return mat


def pi_digits(n: int):
    """Generate x digits of Pi. Spigot's formula."""

    @lazylist
    def gen():
        x = n + 1
        k, a, b, a1, b1 = 2, 4, 1, 12, 4
        while x > 0:
            p, q, k = k * k, 2 * k + 1, k + 1
            a, b, a1, b1 = a1, b1, p * a + q * a1, p * b + q * b1
            d, d1 = a / b, a1 / b1
            while d == d1 and x > 0:
                yield int(d)
                x -= 1
                a, a1 = 10 * (a % b), 10 * (a1 % b1)
                d, d1 = a / b, a1 / b1

    return gen()


def pop(iterable_object: VyList, count: int, ctx: Context) -> List[Any]:
    """Pops (count) items from iterable. If there isn't enough items
    within iterable, input is used as filler."""
    popped_items = []
    for _ in range(count):
        if iterable_object:
            popped_items.append(iterable_object.pop())
        else:
            temp = get_input(ctx)
            popped_items.append(temp)

    if ctx.retain_popped:
        for item in popped_items[::-1]:
            iterable_object.append(item)

    if ctx.reverse_flag:
        popped_items = popped_items[::-1]

    if count == 1:
        return popped_items[0]
    return popped_items


def primitive_type(item: Any) -> Union[str, type]:
    """Turns int/Rational/str into 'Scalar' and everything else
    into list"""
    if type(item) in [int, sympy.Rational, str] or is_sympy(item):
        return SCALAR_TYPE
    assert type(item) in [list, LazyList]
    return list


def reverse_number(
    item: Union[int, sympy.Rational]
) -> Union[int, sympy.Rational]:
    """Reverses a number. Negative numbers are returned negative"""
    if item == 0:
        return 0
    sign = -1 if item < 0 else 1
    rev = str(abs(item)).strip("0")[::-1]
    return vyxalify(sympy.Rational(eval(rev) * sign))


def ring_translate(string: str, map_source: Union[str, list]) -> str:
    """Ring translates a given string according to the provided mapping
    - that is, map matching elements to the subsequent element in the
    translation ring. The ring wraps around."""
    ret = ""
    for char in string:
        if char in map_source:
            ret += map_source[(map_source.index(char) + 1) % len(map_source)]
        else:
            ret += char
    return ret


def safe_apply(function: types.FunctionType, *args, ctx) -> Any:
    """
    Applies function to args that adapts to the input style of the passed function.
    If the function is a _lambda (it's been defined within λ...;), it passes a
      list of arguments and length of argument list.
    Otherwise, if the function is a user-defined function (starts with FN_), it
      simply passes the argument list.
    Otherwise, unpack args and call as usual

    *args does NOT contain ctx
    """
    if function.__name__.startswith("_lambda"):
        ret = function(list(args)[::-1], function, len(args), ctx=ctx)
        if len(ret):
            return ret[-1]
        else:
            return []
    elif function.__name__.startswith("VAR_"):
        ret = function(list(args)[::-1], function, ctx=ctx)[-1]
        return ret
    elif takes_ctx(function):
        return function(*args, ctx=ctx)
    return function(*args)


def scalarify(value: Any) -> Union[Any, List[Any]]:
    """Returns value[0] if value is a list of length 1, else value"""
    if type(value) in (list, LazyList):
        if len(value) == 1:
            return value[0]
        else:
            return value
    else:
        return value


@lazylist
def scanl(
    function: types.FunctionType, vector: List[Any], ctx: Context
) -> List[Any]:
    """Cumulative reduction of vector by function"""
    working = None
    vector = iterable(vector, ctx=ctx)
    for item in vector:
        if working is None:
            working = item
        else:
            yield working
            working = safe_apply(function, working, item, ctx=ctx)
    yield working


def sentence_case(item: str) -> str:
    """Returns the string sentence-cased in an 05AB1E manner"""
    ret = ""
    capitalise = True
    for char in item:
        if capitalise:
            ret += char.upper()
        else:
            ret += char.lower()
        if capitalise and char != " ":
            capitalise = False
        capitalise = capitalise or char in "!?."
    return ret


def simplify(value: Any) -> Union[int, float, str, list]:
    """
    Simplify values.
    Turns sympy values into floats, including sympy values in lists
    """
    if isinstance(value, (int, float, str)):
        return value
    elif is_sympy(value):
        return eval(sympy.pycode(value))
    else:
        return [simplify(x) for x in value]


def stationary_points(lhs: str) -> List[Union[int, float]]:
    """Returns a list of stationary points of a mathematical function"""
    x = sympy.symbols("x")
    d_dx = sympy.diff(sympy.sympify(lhs), x)
    zeros = sympy.solve(d_dx, x)

    return LazyList(zeros)


def suffixes(string: str, ctx: Context) -> List[str]:
    """Returns a list of suffixes of string"""
    return [string[-i:] for i in range(len(string))]


def takes_ctx(function: types.FunctionType) -> bool:
    """Whether or not a function accepts a context argument"""
    argspec = inspect.getfullargspec(function)
    return "ctx" in argspec.args or "ctx" in argspec.kwonlyargs


def to_base_digits(value: int, base: int) -> List[int]:
    """Returns value in base 'base' from base 10 as a list of digits"""
    ret = []
    n = value

    while n > base:
        n, digit = divmod(n, base)
        ret.append(digit)
    ret.append(n)
    return ret[::-1]


def to_base_alphabet(value: int, alphabet: str) -> str:
    """to_base_digit with a custom base"""
    temp = to_base_digits(value, len(alphabet))
    return "".join([alphabet[i] for i in temp])


def transfer_capitalisation(source: str, target: str) -> str:
    """Returns target with the capitalisation of source"""
    ret = ""
    for i in range(min(len(source), len(target))):
        if source[i].isupper():
            ret += target[i].upper()
        elif source[i].islower():
            ret += target[i].lower()
        else:
            ret += target[i]

    if len(target) > len(source):
        ret += target[i + 1 :]

    return ret


def transpose(
    vector: VyList, filler: Any = None, ctx: Context = None
) -> VyList:
    """Transposes a vector"""
    vector = iterable(vector, ctx=ctx)
    temp = itertools.zip_longest(*map(iterable, vector), fillvalue=filler)

    return vyxalify((item for item in x if item is not None) for x in temp)


def uncompress(token: lexer.Token) -> Union[int, str]:
    """Uncompress the token's value based on the token type.

    Handles the following token types: TokenType.STRING,
    TokenType.COMPRESSED_NUMBER, TokenType.COMPRESSED_STRING
    """
    if token.name == lexer.TokenType.STRING:
        return uncompress_dict(token.value)
    if token.name == lexer.TokenType.COMPRESSED_STRING:
        return uncompress_str(token.value)
    if token.name == lexer.TokenType.COMPRESSED_NUMBER:
        return uncompress_num(token.value)

    return token.value


def uncompress_dict(source: str) -> str:
    """Implements Vyxal dictionary compression"""
    characters = collections.deque(source)
    ret, temp_scc = "", ""
    escaped = False
    while characters:
        char = characters.popleft()
        if escaped:
            if temp_scc:
                pos = vyxal.encoding.compression.find(temp_scc)
                if pos < len(vyxal.dictionary.small_dictionary):
                    ret += vyxal.dictionary.small_dictionary[pos]
                temp_scc = ""
            if char not in vyxal.encoding.compression:
                ret += "\\"
            ret += char
            escaped = False

        elif char == "\\":
            escaped = True
        elif char in vyxal.encoding.compression:
            temp_scc += char
            if len(temp_scc) == 2:
                index = from_base_alphabet(temp_scc, vyxal.encoding.compression)
                if index < len(vyxal.dictionary.contents):
                    ret += vyxal.dictionary.contents[index]
                temp_scc = ""
        else:
            if temp_scc:
                pos = vyxal.encoding.compression.find(temp_scc)
                if pos < len(vyxal.dictionary.small_dictionary):
                    ret += vyxal.dictionary.small_dictionary[pos]
                temp_scc = ""
                if char == " ":
                    continue
            ret += char

    if temp_scc:
        pos = vyxal.encoding.compression.find(temp_scc)
        if pos < len(vyxal.dictionary.small_dictionary):
            ret += vyxal.dictionary.small_dictionary[pos]

    return ret


def uncompress_str(string: str) -> str:
    """Decompress a base 255 compressed string"""
    base_10_representation = from_base_alphabet(
        string, vyxal.encoding.codepage_string_compress
    )

    actual = to_base_alphabet(
        base_10_representation, vyxal.encoding.base_27_alphabet
    )
    return actual


def uncompress_num(num: str) -> int:
    """Decompress a base 255 compressed number"""
    return from_base_alphabet(num, vyxal.encoding.codepage_number_compress)


def urlify(item: str) -> str:
    """Makes a url ready for requesting"""
    if not (item.startswith("http://") or item.startswith("https://")):
        return "https://" + item
    return item


def vy_eval(item: str, ctx: Context) -> Any:
    """Evaluates an item. Does so safely if using the online
    interpreter"""
    if ctx.online:
        try:
            t = ast.literal_eval(item)
            if type(t) is float:
                t = sympy.Rational(str(t))
            return vyxalify(t)
        except Exception:  # skipcq: PYL-W0703
            # TODO: eval as vyxal
            return item
    else:
        try:
            t = eval(item)
            if type(t) is float:
                t = sympy.Rational(str(t))
            return vyxalify(t)
        except Exception:  # skipcq: PYL-W0703
            return item


def vyxalify(value: Any) -> Any:
    """Takes a value and returns it as one of the four types we use here."""
    if isinstance(value, sympy.core.numbers.Integer):
        return int(value)
    elif is_sympy(value):
        return sympy.nsimplify(value, rational=True)
    elif isinstance(value, (float, complex)):
        return sympy.nsimplify(value, rational=True)
    elif isinstance(
        value, (int, sympy.Rational, str, LazyList, types.FunctionType)
    ):
        return value
    elif isinstance(value, list):
        return list(map(vyxalify, value))
    else:
        return LazyList(map(vyxalify, value))


def wrap_with_width(vector: Union[str, list], width: int) -> List[Any]:
    """A version of textwrap.wrap that plays nice with spaces"""
    ret = []
    temp = []
    for item in vector:
        temp.append(item)
        if len(temp) == width:
            if all(type(x) is str for x in temp):
                ret.append("".join(temp))
            else:
                ret.append(temp[::])
            temp = []
    if len(temp) < width and temp:
        if all(type(x) is str for x in temp):
            ret.append("".join(temp))
        else:
            ret.append(temp[::])

    return ret


def wrapify(item: Any, count: int = None, ctx: Context = None) -> List[Any]:
    """Leaves lists as lists, wraps scalars into a list"""
    if count is not None:
        temp = pop(item, count, ctx)
        if count == 1:
            return [temp]
        else:
            return temp
    else:
        if primitive_type(item) == SCALAR_TYPE:
            return [item]
        else:
            return item
