"""
File: lexer.py
Description: Before Vyxal programs can be grouped into appropriate
structures, they need to be turned into tokens representing the
different components of a program. For the full specification on token
types, go to documents/specs/Lexer.md
"""

from __future__ import annotations

import collections
import string


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

    STRING: str = "string"
    NUMBER: str = "number"
    NAME: str = "name"
    GENERAL: str = "general"
    COMPRESSED_NUMBER: str = "compressed_number"
    COMPRESSED_STRING: str = "compressed_string"


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
        self.name: str = token_name
        self.value: str = token_value

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

        return str([self.name, self.value])

    def __eq__(self, rhs: Token) -> bool:
        """
        Returns whether both tokens have the same attributes, because
        memory addresses won't be the same.

        Parameters
        ----------

        rhs : Token
            The token to compare.

        Returns
        -------

        True iff the two token names and values are the same.
        """

        return self.name == rhs.name and self.value == rhs.value


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
    source: collections.deque = collections.deque(source)

    contextual_token_value: str = ""

    while source:
        # By treating the program as a queue, we can dequeue elements
        # until a certain predicate is satisfied. In simple terms, this
        # means it's easier to group things based on order...you don't
        # have to worry about what you group first.

        head: str = source.popleft()
        if head == "\\":  # Need to escape the next character
            if source:
                # This has the consequence of making backslahses at the
                # end of a program not error.

                tokens.append(Token(TokenType.STRING, source.popleft()))

        elif head in "`»«":  # String
            # Dequeue characters until the same string character is
            # reached.
            contextual_token_value = ""
            while source and source[0] != head:
                character: str = source.popleft()
                if head == "`" and character == "\\":
                    # Handle the escape by just dequeueing the next
                    # character
                    if source:
                        contextual_token_value += "\\" + source.popleft()
                else:
                    contextual_token_value += character
            token_type: str = ""
            if head == "`":
                token_type = TokenType.STRING
            elif head == "»":
                token_type = TokenType.COMPRESSED_NUMBER
            elif head == "«":
                token_type = TokenType.COMPRESSED_STRING
            tokens.append(Token(token_type, contextual_token_value))
            if source:
                source.popleft()
        elif head in string.digits + ".":
            contextual_token_value = head
            while source and source[0] in string.digits + ".":
                contextual_token_value += source.popleft()
            tokens.append(Token(TokenType.NUMBER, contextual_token_value))
        elif head == "‛":
            contextual_token_value = ""
            while source and len(contextual_token_value) != 2:
                contextual_token_value += source.popleft()
            tokens.append(Token(TokenType.LITERAL, contextual_token_value))
        elif head in "@→←°":
            tokens.append(Token(TokenType.GENERAL, head))
            contextual_token_value = ""
            while source and source[0] in string.ascii_letters + "_":
                contextual_token_value += source.popleft()

            tokens.append(Token(TokenType.NAME, contextual_token_value))
        elif head == "#":
            while source and source[0] != "\n":
                source.popleft()
            if source:
                source.popleft()
        elif head in "k∆øÞ¨":
            if source and source[0] != "|":
                tokens.append(Token(TokenType.GENERAL, head + source.popleft()))
            else:
                tokens.append(Token(TokenType.GENERAL, head))

        else:
            tokens.append(Token(TokenType.GENERAL, head))
    return tokens
