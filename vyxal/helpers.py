"""This is where the cool functions go that help out stuff.

They aren't directly attached to an element. Consequently, you need to
use type annotations here.
"""

import textwrap
from typing import Union

from vyxal import lexer
from vyxal import LazyList


def indent_str(string: str, indent: int, end="\n") -> str:

    """Indent a multiline string with 4 spaces, with a newline (or `end`) afterwards."""
    return textwrap.indent(string, "    " * indent) + end


def indent_code(*code, indent: int = 1) -> str:
    """Indent multiple lines (`*code`) by the given amount, then join on newlines."""
    return "\n".join(indent_str(line, indent, end="") for line in code) + "\n"


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
