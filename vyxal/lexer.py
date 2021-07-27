from typing import List

"""
File: lexer.py
Description: Before Vyxal programs can be grouped into appropriate
structures, they need to be turned into tokens representing the
different components of a program. For the full specification on token
types, go to documents/specs/Lexer.md
"""


class TokenType:
    """
    A class providing a namespace for token type constants. Do not
    create any instances of this class.

    Attributes
    ----------

    LITERAL : str
        Used to denote that a token is a literal. In this case, this is
        defined as numbers and strings. Lists are NOT considered
        to be literal tokens.

    NAME : str
        Used to denote that a token is a name, meaning that it belongs
        to a structure such as a function defintion/call or a variable
        get/set.

    GENERAL : str
        Used to denote that a token does not have a specific type. This
        kind of token can be anything - a digraph, a structure delimiter
        or just a simple element.
    """

    LITERAL: str = "literal"
    NAME: str = "name"
    GENERAL: str = "general"


def tokenise(source: str) -> list[list[TokenType, str]]:
    """
    Transform a Vyxal program into a list of tokens

    Parameters
    ----------
    
    source : str
        The Vyxal program to turn into tokens. This will have a utf-8
        encoding.
    
    Returns
    -------
    list[list[TokenType, str]]
        Each token is represented as a pair of a TokenType constant and
        the portion of the Vyxal program being tokenised. These are all
        contained within a list.
    """
    
    tokens: list[list[TokenType, str]] = []

    # code that does the tokenising here

    return tokens

if __name__ == "__main__":
    # Test cases
    # assert tokenise(<program>) == <expected list of tokens>