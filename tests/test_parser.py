import os
import sys

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

from vyxal.lexer import *
from vyxal.parser import *


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
    assert (
        str(fully_parse("1 1+"))
        == "[['none', ['number', '1']], ['none', ['number', '1']], "
        + "['none', ['general', '+']]]"
    )

    assert (
        str(fully_parse("`Hello, World!`"))
        == "[['none', ['string', " + "'Hello, World!']]]"
    )


def test_fizzbuzz():
    assert eval(str(fully_parse("₁ƛ₍₃₅kF½*∑∴"))) == [
        ["none", ["general", "₁"]],
        [
            "lambda",
            [
                "1",
                [
                    [
                        "dyadic_modifier",
                        [
                            "₍",
                            [
                                ["none", ["general", "₃"]],
                                ["none", ["general", "₅"]],
                            ],
                        ],
                    ],
                    ["none", ["general", "kF"]],
                    ["none", ["general", "½"]],
                    ["none", ["general", "*"]],
                    ["none", ["general", "∑"]],
                    ["none", ["general", "∴"]],
                ],
            ],
        ],
        ["none", ["general", "M"]],
    ]


def test_modifiers():
    assert eval(str(fully_parse("⁽*r"))) == [
        ["lambda", ["1", [[["none", ["general", "*"]]]]]],
        ["none", ["general", "r"]],
    ]

    assert eval(str(fully_parse("vv+"))) == [
        [
            "monadic_modifier",
            ["v", [["monadic_modifier", ["v", [["none", ["general", "+"]]]]]]],
        ]
    ]

    assert eval(str(fully_parse("‡₌*ġḭd†"))) == [
        [
            "lambda",
            [
                "1",
                [
                    [
                        "dyadic_modifier",
                        [
                            "₌",
                            [
                                ["none", ["general", "*"]],
                                ["none", ["general", "ġ"]],
                            ],
                        ],
                    ],
                    ["none", ["general", "ḭ"]],
                ],
            ],
        ],
        ["none", ["general", "d"]],
        ["none", ["general", "†"]],
    ]


if __name__ == "__main__":
    test_basic()
    test_fizzbuzz()
    test_modifiers()
    print("everything passed")
