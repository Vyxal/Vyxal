"""
File: helpers.py
Description: This is where the cool functions go that help out stuff.
They aren't directly attached to an element. Consequently, you need to
use type annotations here.
"""

import textwrap
from typing import Union
import lexer


def indent_str(string: str, indent: int, end="\n") -> str:
    """
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
    """
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
    Uncompress the token's value based on the token type

    Parameters
    ----------

    token: lexer.token
        The token to uncompress. Will have one of the following token
        types: TokenType.STRING, TokenType.COMPRESSED_NUMBER,
        TokenType.COMPRESSED_STRING


    Returns
    -------
    int | str
        The uncompressed token value. The return type depends on the
        token type.
    """

    # TODO: Implement the compression stuff
    return token.value
