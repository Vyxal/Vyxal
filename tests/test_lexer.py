import os
import sys

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

from vyxal.lexer import *


def token_equal(source: str, expected: list[Token]) -> bool:
    """
    Vectorises equality over the tokenised version of the program and
    the expected token list. This is because memory references.

    Parameters
    ----------

    source : str
        The test program to tokenise

    expected: list[Token]
        The expected token list


    Returns
    -------

    bool
        True iff corresponding tokens in the tokenised source and the
        expected list have the same name and value
    """

    return all(
        map(
            lambda x: x[0].name == x[1].name and x[0].value == x[1].value,
            zip(tokenise(source), expected),
        )
    )


def test_single_token():
    assert token_equal("1", [Token(TokenType.NUMBER, "1")])


def test_one_plus_one():
    assert token_equal(
        "1 1+",
        [
            Token(TokenType.NUMBER, "1"),
            Token(TokenType.GENERAL, " "),
            Token(TokenType.NUMBER, "1"),
            Token(TokenType.GENERAL, "+"),
        ],
    )
