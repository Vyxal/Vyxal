"""lexes Vyxal code

Before Vyxal programs can be grouped into appropriate
structures, they need to be turned into tokens representing the
different components of a program. For the full specification on token
types, go to documents/specs/Lexer.md
"""

from __future__ import annotations

import collections
import string
from enum import Enum


class TokenType(Enum):
    """Enum describing the possible types that tokens can have

    Since this is an Enum, do not instantiate it!
    """

    STRING = "string"
    NUMBER = "number"
    CHARACTER = "character"

    # `general` is used to denote that a token does not have a specific type.
    # This kind of token can be anything - a digraph, a structure delimiter or
    # just a simple element.

    GENERAL = "general"

    COMPRESSED_NUMBER = "compressed_number"
    COMPRESSED_STRING = "compressed_string"
    VARIABLE_GET = "variable_get"
    VARIABLE_SET = "variable_set"
    CODEPAGE_NUMBER = "codepage_number"


class Token:
    def __init__(self, token_name: TokenType, token_value: str):
        self.name = token_name
        self.value = token_value

    def __str__(self) -> str:
        """Give a nicely formatted representation of the token"""
        return f"{self.name.value}: {self.value}"

    def __repr__(self) -> str:
        """Returns the token as a stringified list version of name, value"""
        return f"Token({self.name.value!r}, {self.value!r})"

    def __eq__(self, rhs) -> bool:

        if not isinstance(rhs, Token):
            return NotImplemented
        return self.name == rhs.name and self.value == rhs.value


def tokenise(source_str: str) -> list[Token]:
    """Main lexing function: transforms a Vyxal program into a list of tokens


    Returns a list of Token objects.
    """
    tokens = []
    source: collections.deque[str] = collections.deque(source_str)

    contextual_token_value = ""

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

                # Why? Because say that \ is the last character in a
                # program. That means that after assigning head, the
                # source variable is empty. Without checking to make
                # sure that the source deque isn't empty, popping from
                # an empty deque would cause an error.
                tokens.append(Token(TokenType.CHARACTER, source.popleft()))

        elif head in "`»«":  # String
            # Dequeue characters until the same string character is
            # reached.
            contextual_token_value = ""
            while source and source[0] != head:
                character = source.popleft()
                if head == "`" and character == "\\":
                    # Handle the escape by just dequeueing the next
                    # character
                    if source:
                        contextual_token_value += "\\" + source.popleft()
                else:
                    contextual_token_value += character
            if head == "`":
                token_type = TokenType.STRING
            elif head == "»":
                token_type = TokenType.COMPRESSED_NUMBER
            elif head == "«":
                token_type = TokenType.COMPRESSED_STRING
            tokens.append(Token(token_type, contextual_token_value))
            if source:
                source.popleft()
        elif head in string.digits + ".°":
            contextual_token_value = head
            if head == "0" and not (source and source[0] in "°."):
                # Handle the special case of 0.
                tokens.append(Token(TokenType.NUMBER, contextual_token_value))
            else:
                while (
                    source
                    and source[0] in string.digits + ".°"
                    and (contextual_token_value + source[0]).count("°") < 2
                    and all(
                        x.count(".") < 2
                        for x in (contextual_token_value + source[0]).split("°")
                    )
                ):
                    contextual_token_value += source.popleft()
                tokens.append(Token(TokenType.NUMBER, contextual_token_value))
        elif head == "‛":
            contextual_token_value = ""
            while source and len(contextual_token_value) != 2:
                contextual_token_value += source.popleft()
            tokens.append(Token(TokenType.STRING, contextual_token_value))
        elif head in "→←":
            contextual_token_value = ""
            while source and source[0] in string.ascii_letters + "_":
                contextual_token_value += source.popleft()

            if head == "→":
                tokens.append(
                    Token(TokenType.VARIABLE_SET, contextual_token_value)
                )
            else:
                tokens.append(
                    Token(TokenType.VARIABLE_GET, contextual_token_value)
                )
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
        elif head == "⁺":
            if source:
                tokens.append(
                    Token(TokenType.CODEPAGE_NUMBER, source.popleft())
                )

        else:
            tokens.append(Token(TokenType.GENERAL, head))
    return tokens
