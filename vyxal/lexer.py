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


class Token:
    """
    A class representing tokens of code

    Attributes
    ----------

    name : str
        The name of the token. Usually a TokenType literal

    value : str
        The value of the token

    Parameters
    ----------

    token_name : str
        The value to use as the name of the token

    token_value : str
        The value to use as the value of the token

    """

    def __init__(self, token_name: str, token_value: str):
        self.name = token_name
        self.value = token_value

    def __str__(self) -> str:
        """
        Return a nicely formatted representation of the token

        Returns
        -------

        str
            {name}: {value}
        """

        return f"{self.name}: {self.value}"

    def __repr__(self) -> str:
        """
        Returns the token as a stringified list version of name, value

        Returns
        -------
        str
            [name, value]
        """

        return f"[{self.name}, {self.value}]"


def tokenise(source: str) -> list[Token]:
    """
    Transform a Vyxal program into a list of tokens

    Parameters
    ----------

    source : str
        The Vyxal program to turn into tokens. This will have a utf-8
        encoding.

    Returns
    -------
    list[Token]
        Each token is represented as a Token object.
    """

    tokens: list[Token] = []

    # code that does the tokenising here

    return tokens


if __name__ == "__main__":
    # Test cases
    # assert tokenise(<program>) == <expected list of tokens>
    pass
