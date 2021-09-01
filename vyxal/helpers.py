"""This is where the cool functions go that help out stuff.

They aren't directly attached to an element. Consequently, you need to
use type annotations here.
"""

import ast
import textwrap
import types
from typing import Any, List, Union

import sympy

from vyxal import context, lexer
from vyxal.LazyList import *

NUMBER_TYPE = "number"
SCALAR_TYPE = "scalar"


def deep_copy(value: Any) -> Any:
    """Because lists and lazylists use memory references. Frick them."""

    if type(value) not in (list, LazyList):
        return value  # because primitives are all like "ooh look at me
        # I don't have any fancy memory references because I'm an epic
        # chad unlike those virgin memory reference needing lists".

    # Use itertools.tee for (LazyL|l)ists
    return LazyList(itertools.tee(value)[-1])


def get_input(ctx: context.Context) -> Any:
    """Returns the next input depending on where ctx tells to get the
    input from."""

    if ctx.use_top_input:
        if ctx.inputs[0][0]:
            ret = ctx.inputs[0][ctx.inputs[0][1] % len(ctx.inputs[0])]
            ctx.inputs[0][1] += 1
            return ret
        else:
            try:
                temp = vy_eval(input("> " * ctx.repl_mode), ctx)
                return temp
            except:
                return 0
    else:
        ret = ctx.inputs[-1][ctx.inputs[-1][1] % len(ctx.inputs[-1])]
        ctx.inputs[-1][1] += 1
        return ret


@lazylist
def fixed_point(function: types.FunctionType, initial: Any) -> List[Any]:
    """Repeat function until the result is no longer unique.
    Uses initial as the initial value"""

    previous = None
    current = simplify(initial)

    while previous != current:
        yield current
        prevuous = deep_copy(current)
        current = safe_apply(function, current)


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


def indent_str(string: str, indent: int, end="\n") -> str:

    """Indent a multiline string with 4 spaces, with a newline (or `end`) afterwards."""
    return textwrap.indent(string, "    " * indent) + end


def indent_code(*code, indent: int = 1) -> str:
    """Indent multiple lines (`*code`) by the given amount, then join on newlines."""
    return "\n".join(indent_str(line, indent, end="") for line in code) + "\n"


def iterable(
    item: Any, number_type: Any = None, ctx: context.Context = None
) -> Union[LazyList, Union[list, str]]:
    """Makes sure that a value is an iterable"""

    if (t := type(item)) in [sympy.Rational, int]:
        if ctx.number_as_range or number_type is range:
            return LazyList(range(ctx.range_start, int(item) + ctx.range_end))
        else:
            if t is sympy.Rational:
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


def mold(
    content: Union[list, LazyList],
    shape: Union[list, LazyList],
) -> Union[list, LazyList]:
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


def pop(
    iterable: Union[list, LazyList], count: int, ctx: context.Context
) -> List[Any]:
    """Pops (count) items from iterable. If there isn't enough items
    within iterable, input is used as filler."""

    popped_items = []
    for _ in range(count):
        if iterable:
            popped_items.append(iterable.pop())
        else:
            ctx.use_top_input = True
            popped_items.append(get_input(ctx))
            ctx.use_top_input = False

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


def safe_apply(function: types.FunctionType, *args, ctx) -> Any:
    """
    Applies function to args that adapts to the input style of the passed function.
    If the function is a _lambda (it's been defined within λ...;), it passes a
      list of arguments and length of argument list.
    Otherwise, if the function is a user-defined function (starts with FN_), it
      simply passes the argument list.
    Otherwise, unpack args and call as usual

    *args contains ctx
    """

    if function.__name__.startswith("_lambda"):
        ret = function(list(args), len(args), function, ctx)
        if len(ret):
            return ret[-1]
        else:
            return []
    elif function.__name__.startswith("FN_"):
        ret = function(list(args), ctx)[-1]
        if len(ret):
            return ret[-1]
        else:
            return []
    return function(*args, ctx)


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


def uncompress(token: lexer.Token) -> Union[int, str]:
    """Uncompress the token's value based on the token type.

    Handles the following token types: TokenType.STRING,
    TokenType.COMPRESSED_NUMBER, TokenType.COMPRESSED_STRING
    """
    if token.name == lexer.TokenType.COMPRESSED_STRING:
        return uncompress_str(token.value)
    if token.name == lexer.TokenType.COMPRESSED_NUMBER:
        return uncompress_num(token.value)

    return token.value


def uncompress_str(string: str) -> str:
    # TODO (lyxal) Implement string (un)compression
    raise NotImplementedError()


def uncompress_num(num: str) -> int:
    # TODO (lyxal) Implement number (un)compression
    raise NotImplementedError()


def vy_eval(item: str, ctx: context.Context) -> Any:
    """Evaluates an item. Does so safely if using the online
    interpreter"""

    if ctx.online:
        try:
            return ast.literal_eval(item)
        except:
            # TODO: eval as vyxal
            return item
    else:
        try:
            return eval(item)
        except:
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
    # Because textwrap.wrap doesn't consistently play nice with spaces
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
