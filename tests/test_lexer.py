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
            lambda x: x[0] == x[1],
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


def test_strings():
    assert token_equal(
        "`Hello, World!`", [Token(TokenType.STRING, "Hello, World!")]
    )
    assert token_equal(
        "`Hello, World!", [Token(TokenType.STRING, "Hello, World!")]
    )
    assert token_equal(
        "`Escaped backtick? \``",
        [Token(TokenType.STRING, "Escaped backtick? \\`")],
    )
    assert token_equal("\`", [Token(TokenType.STRING, "`")])
    assert token_equal(
        "k`Hi",
        [
            Token(TokenType.GENERAL, "k`"),
            Token(TokenType.GENERAL, "H"),
            Token(TokenType.GENERAL, "i"),
        ],
    )


def test_comments():
    assert token_equal(
        "1 1 + # line comment ;)",
        [
            Token(TokenType.NUMBER, "1"),
            Token(TokenType.GENERAL, " "),
            Token(TokenType.NUMBER, "1"),
            Token(TokenType.GENERAL, " "),
            Token(TokenType.GENERAL, "+"),
            Token(TokenType.GENERAL, " "),
        ],
    )


def test_numbers():
    assert token_equal("23", [Token(TokenType.NUMBER, "23")])
    # TODO: More number cases


def test_variables():
    assert token_equal(
        "→variable_name", [Token(TokenType.VARIABLE_SET, "variable_name")]
    )


if __name__ == "__main__":  # For testing outside of the workflow
    test_single_token()
    test_one_plus_one()
    test_strings()
    test_comments()
    test_numbers()
    test_variables()
    print("everything passed.")
