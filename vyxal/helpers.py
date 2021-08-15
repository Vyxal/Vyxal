"""
File: helpers.py
Description: This is where the cool functions go that help out stuff.
They aren't directly attached to an element. Consequently, you need to
use type annotations here.
"""

import textwrap
from typing import Union

from vyxal import lexer


def indent_str(string: str, indent: int, end="\n") -> str:
    r"""
    Indent a multiline string with 4 spaces.

    Parameters
    ----------
    string: str
        The string to be indented.
    indent: int
        How many levels of indentation are required.

    Returns
    -------
    str
        `string`, indented by `(4 * indent)` spaces, after `\n`.
    """
    return textwrap.indent(string, "    " * indent) + end


def indent_code(*code, indent: int = 1) -> str:
    r"""
    Indent multiple lines by the given amount, then join on newlines.

    Parameters
    ----------
    code: list[str]
        The lines to be indented.
    indent: int
        How many levels of indentation are required.

    Returns
    -------
    str
        `code`, all indented by `(4 * indent)` spaces, then joined on `\n`.
    """
    return "\n".join(indent_str(line, indent, end="") for line in code) + "\n"


def uncompress(token: lexer.Token) -> Union[int, str]:
    """
    Uncompress the token's value based on the token type.

    Parameters
    ----------
    token: lexer.Token
        The token to uncompress. Will have one of the following token
        types: TokenType.STRING, TokenType.COMPRESSED_NUMBER,
        TokenType.COMPRESSED_STRING


    Returns
    -------
    int | str
        The uncompressed token value. The return type depends on the
        token type.
    """
    if token.name == lexer.TokenType.COMPRESSED_STRING:
        return uncompress_str(token.value)
    if token.name == lexer.TokenType.COMPRESSED_NUMBER:
        return uncompress_num(token.value)

    return token.value


def uncompress_str(string: str) -> str:
    """
    Uncompress a string.

    Parameters
    ----------
    string: str
        The string to uncompress.

    Returns
    -------
    str
        The uncompressed string.
    """
    # TODO (lyxal) Implement string (un)compression
    raise NotImplementedError()


def uncompress_num(num: str) -> int:
    """
    Uncompress a number.

    Parameters
    ----------
    number: str
        The number to uncompress.

    Returns
    -------
    int
        The uncompressed number.
    """
    # TODO (lyxal) Implement number (un)compression
    raise NotImplementedError()
