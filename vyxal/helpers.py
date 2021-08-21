"""This is where the cool functions go that help out stuff.

They aren't directly attached to an element. Consequently, you need to
use type annotations here.
"""

import ast
import textwrap
from typing import Any, List, Union

import sympy
from vyxal import lexer
from vyxal import LazyList
from vyxal import context


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


def indent_str(string: str, indent: int, end="\n") -> str:

    """Indent a multiline string with 4 spaces, with a newline (or `end`) afterwards."""
    return textwrap.indent(string, "    " * indent) + end


def indent_code(*code, indent: int = 1) -> str:
    """Indent multiple lines (`*code`) by the given amount, then join on newlines."""
    return "\n".join(indent_str(line, indent, end="") for line in code) + "\n"


def iterable(
    item: Any, ctx: context.Context
) -> Union[LazyList.LazyList, Union[list, str]]:
    """Makes sure that a value is an iterable"""

    if (t := type(item)) in [sympy.Rational, int]:
        if ctx.number_as_range:
            return LazyList.LazyList(
                range(ctx.range_start, int(item) + ctx.range_end)
            )
        else:
            if t is sympy.Rational:
                item = float(item)
            return [int(let) if let not in "-." else let for let in str(item)]
    else:
        return item


def mold(
    content: Union[list, LazyList.LazyList],
    shape: Union[list, LazyList.LazyList],
) -> Union[list, LazyList.LazyList]:
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
    iterable: Union[list, LazyList.LazyList], count: int, ctx: context.Context
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
