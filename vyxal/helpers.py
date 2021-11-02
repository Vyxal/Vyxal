"""This is where the cool functions go that help out stuff.

They aren't directly attached to an element. Consequently, you need to
use type annotations here.
"""

import ast
import collections
import inspect
import itertools
import string
import textwrap
import types
from typing import Any, List, Union

import numpy
import sympy
import vyxal.encoding

from vyxal import lexer
from vyxal.context import DEFAULT_CTX, Context
from vyxal.LazyList import *
from vyxal.dictionary import *

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


def collect_until_false(
    function: types.FunctionType,
    predicate: types.FunctionType,
    initial: Any,
    ctx: Context,
) -> List[Any]:
    val = initial
    while safe_apply(predicate, val, ctx):
        yield val
        val = safe_apply(function, val, ctx)


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


@lazylist
def fixed_point(function: types.FunctionType, initial: Any) -> List[Any]:
    """Repeat function until the result is no longer unique.
    Uses initial as the initial value"""

    previous = None
    current = simplify(initial)

    while previous != current:
        yield current
        previous = deep_copy(current)
        current = safe_apply(function, current)


def foldl(function: types.FunctionType, vector: List[Any], ctx: Context) -> Any:
    """Reduce vector by function"""
    if len(vector) == 0:
        return 0

    working = None

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


def from_base_digits(digits: List[NUMBER_TYPE], base: int) -> int:
    """Returns digits in base 10 using arbitrary base 'base'"""
    # I may have stolen this from Jelly
    ret = 0
    for digit in digits:
        ret = base * ret + digit

    return ret


def get_input(ctx: Context) -> Any:
    """Returns the next input depending on where ctx tells to get the
    input from."""

    if ctx.use_top_input:
        if ctx.inputs[0][0]:
            ret = ctx.inputs[0][0][ctx.inputs[0][1] % len(ctx.inputs[0])]
            ctx.inputs[0][1] += 1
            return ret
        else:
            try:
                temp = vy_eval(input("> " * ctx.repl_mode), ctx)
                return temp
            except:
                return 0
    else:
        if ctx.inputs[-1][0]:
            ret = ctx.inputs[-1][0][ctx.inputs[-1][1] % len(ctx.inputs[-1][0])]
            ctx.inputs[-1][1] += 1
            return ret
        else:
            return 0


def indent_str(string: str, indent: int, end="\n") -> str:

    """Indent a multiline string with 4 spaces, with a newline (or `end`) afterwards."""
    return textwrap.indent(string, "    " * indent) + end


def indent_code(*code, indent: int = 1) -> str:
    """Indent multiple lines (`*code`) by the given amount, then join on newlines."""
    return "\n".join(indent_str(line, indent, end="") for line in code) + "\n"


def iterable(
    item: Any, number_type: Any = None, ctx: Context = DEFAULT_CTX
) -> Union[LazyList, Union[list, str]]:
    """Turn a value into an iterable"""
    item_type = type(item)
    if item_type in [sympy.Rational, int]:
        if ctx.number_as_range or number_type is range:
            return LazyList(range(ctx.range_start, int(item) + ctx.range_end))
        else:
            if item_type is sympy.Rational:
                item = float(item)

            return [int(let) if let not in "-." else let for let in str(item)]
    else:
        return item


def keep(haystack: Any, needle: Any) -> Any:
    """Used for keeping only needle in haystack"""

    ret = []
    for item in haystack:
        if item in needle:
            ret.append(item)

    if type(haystack) is str:
        return "".join(ret)
    else:
        return ret


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
        key = lambda x, ctx=None: x
    if cmp is None:
        cmp = lambda a, b, ctx=None: a < b
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
        key = lambda x, ctx=None: x
    if cmp is None:
        cmp = lambda a, b, ctx=None: a < b
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
    """Mold one list to the shape of the other. Uses the mold function
    that Jelly uses."""
    # https://github.com/DennisMitchell/jellylanguage/blob/70c9fd93ab009c05dc396f8cc091f72b212fb188/jelly/interpreter.py#L578
    for index in range(len(shape)):
        if type(shape[index]) == list:
            mold(content, shape[index])
        else:
            item = content.pop(0)
            shape[index] = item
            content.append(item)
    return shape


def pop(iterable: VyList, count: int, ctx: Context) -> List[Any]:
    """Pops (count) items from iterable. If there isn't enough items
    within iterable, input is used as filler."""

    popped_items = []
    for _ in range(count):
        if iterable:
            popped_items.append(iterable.pop())
        else:
            popped_items.append(get_input(ctx))

    if ctx.retain_popped:
        for item in popped_items[::-1]:
            iterable.append(item)

    if ctx.reverse_flag:
        popped_items = popped_items[::-1]

    if count == 1:
        return popped_items[0]
    return popped_items


def primitive_type(item: type) -> Union[str, type]:
    """Turns int/Rational/str into 'Scalar' and everything else
    into list"""

    if type(item) in [int, sympy.Rational, str]:
        return SCALAR_TYPE
    else:
        return list


def reverse_number(
    item: Union[int, sympy.Rational]
) -> Union[int, sympy.Rational]:
    """Reverses a number. Negative numbers are returned negative"""

    sign = -1 if item < 0 else 1
    rev = str(abs(item))[::-1]
    return sympy.Rational(eval(rev) * sign)


def ring_translate(map_source: Union[str, list], string: str) -> str:
    """Ring translates a given string according to the provided mapping
    - that is, map matching elements to the subsequent element in the
    translation ring. The ring wraps around."""
    ret = ""
    LENGTH = len(map_source)
    for char in string:
        if char in map_source:
            ret += map_source[(map_source.index(char) + 1) % LENGTH]
        else:
            ret += char
    return ret


def ring_translate(map_source: Union[str, list], string: str) -> str:
    """Ring translates a given string according to the provided mapping
    - that is, map matching elements to the subsequent element in the
    translation ring. The ring wraps around."""
    ret = ""
    LENGTH = len(map_source)
    for char in string:
        if char in map_source:
            ret += map_source[(map_source.index(char) + 1) % LENGTH]
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
        ret = function(list(args), function, len(args), ctx)
        if len(ret):
            return ret[-1]
        else:
            return []
    elif function.__name__.startswith("FN_"):
        ret = function(list(args), function, ctx)[-1]
        if len(ret):
            return ret[-1]
        else:
            return []
    elif takes_ctx(function):
        return function(*args, ctx)
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
    for item in vector:
        if working is None:
            working = item
        else:
            working = safe_apply(function, working, item, ctx=ctx)
            yield working


def sentence_case(item: str) -> str:
    """Returns the string sentence-cased in an 05AB1E manner"""
    ret = ""
    capitalise = True
    for char in item:
        ret += (lambda: char.lower(), lambda: char.upper())[capitalise]()
        if capitalise and char != " ":
            capitalise = False
        capitalise = capitalise or char in "!?."
    return ret


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
                if index < len(contents):
                    ret += contents[index]
                temp_scc = ""
        else:
            if temp_scc:
                ret += small_dictionary[
                    vyxal.encoding.compression.find(temp_scc)
                ]
                temp_scc = ""
            ret += char

    if temp_scc:
        ret += small_dictionary[vyxal.encoding.compression.find(temp_scc)]
        temp_scc = ""

    return ret


def uncompress_str(string: str) -> str:
    # TODO (lyxal) Implement string (un)compression
    raise NotImplementedError()


def uncompress_num(num: str) -> int:
    # TODO (lyxal) Implement number (un)compression
    raise NotImplementedError()


def vy_eval(item: str, ctx: Context) -> Any:
    """Evaluates an item. Does so safely if using the online
    interpreter"""

    if ctx.online:
        try:
            t = ast.literal_eval(item)
            if type(t) is float:
                t = sympy.Rational(str(t))
            return t
        except Exception:
            # TODO: eval as vyxal
            return item
    else:
        try:
            t = eval(item)
            if type(t) is float:
                t = sympy.Rational(str(t))
            return t
        except Exception:
            return item


def vy_zip(*items) -> list:
    """Like python's zip, but fills shorter lists with 0s"""

    items = list(map(iter, items))
    while True:
        ret = []
        exhausted_count = 0
        for item in items:
            try:
                ret.append(next(item))
            except:
                ret.append(0)
                exhausted_count += 1

        if len(items) == exhausted_count:
            break

        yield ret


def wrap(vector: Union[str, list], width: int) -> List[Any]:
    """A version of textwrap.wrap that plays nice with spaces"""
    ret = []
    temp = []
    for item in vector:
        temp.append(item)
        if len(temp) == width:
            if all([type(x) is str for x in temp]):
                ret.append("".join(temp))
            else:
                ret.append(temp[::])
            temp = []
    if len(temp) < width and temp:
        if all([type(x) is str for x in temp]):
            ret.append("".join(temp))
        else:
            ret.append(temp[::])

    return ret


def wrapify(item: Any) -> List[Any]:
    """Leaves lists as lists, wraps scalars into a list"""
    if primitive_type(item) == SCALAR_TYPE:
        return [item]
    else:
        return item
