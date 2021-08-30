import os
import sys
from typing import Generic

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

from vyxal.lexer import *
from vyxal.parse import *
from vyxal.structure import *


def fully_parse(program: str) -> list[Structure]:
    """
    Essentially, wrap tokenise(program) in parse

    Parameters
    ----------

    program : str
        The program to tokenise and then parse

    Returns
    -------

    list[Structure]
        Quite literally parse(tokenise(program))
    """
    return parse(tokenise(program))  # see what I mean?


def test_basic():
    assert str(fully_parse("1 1+")) == str(
        [
            GenericStatement([Token(TokenType.NUMBER, "1")]),
            GenericStatement([Token(TokenType.NUMBER, "1")]),
            GenericStatement([Token(TokenType.GENERAL, "+")]),
        ]
    )

    assert str(fully_parse("`Hello, World!`")) == str(
        [GenericStatement([Token(TokenType.STRING, "Hello, World!")])]
    )


def test_fizzbuzz():
    assert str(fully_parse("₁ƛ₍₃₅kF½*∑∴")) == str(
        [
            GenericStatement([Token(TokenType.GENERAL, "₁")]),
            Lambda(
                "1",
                [
                    DyadicModifier(
                        "₍",
                        GenericStatement([Token(TokenType.GENERAL, "₃")]),
                        GenericStatement([Token(TokenType.GENERAL, "₅")]),
                    ),
                    GenericStatement([Token(TokenType.GENERAL, "kF")]),
                    GenericStatement([Token(TokenType.GENERAL, "½")]),
                    GenericStatement([Token(TokenType.GENERAL, "*")]),
                    GenericStatement([Token(TokenType.GENERAL, "∑")]),
                    GenericStatement([Token(TokenType.GENERAL, "∴")]),
                ],
            ),
            GenericStatement([Token(TokenType.GENERAL, "M")]),
        ]
    )


def test_modifiers():
    assert str(fully_parse("⁽*r")) == str(
        [
            Lambda("1", GenericStatement([Token(TokenType.GENERAL, "*")])),
            GenericStatement([Token(TokenType.GENERAL, "r")]),
        ]
    )

    assert str(fully_parse("vv+")) == str(
        [
            MonadicModifier(
                "v",
                MonadicModifier(
                    "v", GenericStatement([Token(TokenType.GENERAL, "+")])
                ),
            )
        ]
    )

    assert str(fully_parse("‡₌*ġḭd†")) == str(
        [
            Lambda(
                "1",
                [
                    DyadicModifier(
                        "₌",
                        GenericStatement([Token(TokenType.GENERAL, "*")]),
                        GenericStatement([Token(TokenType.GENERAL, "ġ")]),
                    ),
                    GenericStatement([Token(TokenType.GENERAL, "ḭ")]),
                ],
            ),
            GenericStatement([Token(TokenType.GENERAL, "d")]),
            GenericStatement([Token(TokenType.GENERAL, "†")]),
        ]
    )


"""
def test_structures():
    assert fully_parse("[1 1+|`nice`") == [
        [
            "if_stmt",
            [
                [
                    ["none", ["number", "1"]],
                    ["none", ["number", "1"]],
                    ["none", ["general", "+"]],
                ],
                [["none", ["string", "nice"]]],
            ],
        ]
    ]

    assert fully_parse("1 10r(i|n2*,") == [
        ["none", ["number", "1"]],
        ["none", ["number", "10"]],
        ["none", ["general", "r"]],
        [
            "for_loop",
            [
                "i",
                [
                    ["none", ["general", "n"]],
                    ["none", ["number", "2"]],
                    ["none", ["general", "*"]],
                    ["none", ["general", ","]],
                ],
            ],
        ],
    ]

    assert fully_parse("@triple:1|3*;") == [
        [
            "function",
            [
                ["triple", "1"],
                [["none", ["number", "3"]], ["none", ["general", "*"]]],
            ],
        ]
    ]

    assert fully_parse("(code‛|c") == [
        [
            "for_loop",
            [
                [
                    ["none", ["general", "c"]],
                    ["none", ["general", "o"]],
                    ["none", ["general", "d"]],
                    ["none", ["general", "e"]],
                    ["none", ["string", "|c"]],
                ]
            ],
        ]
    ]

"""
if __name__ == "__main__":
    test_basic()
    test_fizzbuzz()
    test_modifiers()
    test_structures()
    print("everything passed")
