from ast import Break
import os
import sys

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
            LambdaMap(
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
                ]
            ),
        ]
    )


def test_modifiers():
    assert str(fully_parse("⁽*r")) == str(
        [
            Lambda(
                "default", [GenericStatement([Token(TokenType.GENERAL, "*")])]
            ),
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
                "default",
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


def test_structures():
    assert str(fully_parse("[1 1+|`nice`")) == str(
        [
            IfStatement(
                [
                    GenericStatement([Token(TokenType.NUMBER, "1")]),
                    GenericStatement([Token(TokenType.NUMBER, "1")]),
                    GenericStatement([Token(TokenType.GENERAL, "+")]),
                ],
                [GenericStatement([Token(TokenType.STRING, "nice")])],
            )
        ]
    )

    assert str(fully_parse("1 10r(i|n2*,")) == str(
        [
            GenericStatement([Token(TokenType.NUMBER, "1")]),
            GenericStatement([Token(TokenType.NUMBER, "10")]),
            GenericStatement([Token(TokenType.GENERAL, "r")]),
            ForLoop(
                ["i"],
                [
                    GenericStatement([Token(TokenType.GENERAL, "n")]),
                    GenericStatement([Token(TokenType.NUMBER, "2")]),
                    GenericStatement([Token(TokenType.GENERAL, "*")]),
                    GenericStatement([Token(TokenType.GENERAL, ",")]),
                ],
            ),
        ]
    )

    assert str(fully_parse("@triple:1|3*;")) == str(
        [
            FunctionDef(
                "triple",
                ["1"],
                [
                    GenericStatement([Token(TokenType.NUMBER, "3")]),
                    GenericStatement([Token(TokenType.GENERAL, "*")]),
                ],
            )
        ]
    )

    assert str(fully_parse("(code‛|c")) == str(
        [
            ForLoop(
                [],
                [
                    GenericStatement([Token(TokenType.GENERAL, "c")]),
                    GenericStatement([Token(TokenType.GENERAL, "o")]),
                    GenericStatement([Token(TokenType.GENERAL, "d")]),
                    GenericStatement([Token(TokenType.GENERAL, "e")]),
                    GenericStatement([Token(TokenType.STRING, "|c")]),
                ],
            )
        ]
    )


def test_modifiers_in_char_literals():
    assert str(fully_parse("\\&")) == str(
        [GenericStatement([Token(TokenType.CHARACTER, "&")])]
    )


def test_branch_literals_in_structures():
    assert str(fully_parse("(`|`,")) == str(
        [
            ForLoop(
                [],
                [
                    GenericStatement([Token(TokenType.STRING, "|")]),
                    GenericStatement([Token(TokenType.GENERAL, ",")]),
                ],
            )
        ]
    )

    assert str(fully_parse("[\\|,")) == str(
        [
            IfStatement(
                [
                    GenericStatement([Token(TokenType.CHARACTER, "|")]),
                    GenericStatement([Token(TokenType.GENERAL, ",")]),
                ],
            )
        ]
    )


def test_break_works_good_with_modifiers():
    assert str(fully_parse("vX")) == str(
        [MonadicModifier("v", BreakStatement(None))]
    )

    assert str(fully_parse("₌XX")) == str(
        [DyadicModifier("₌", BreakStatement(None), BreakStatement(None))]
    )

    assert str(fully_parse("≬3dX")) == str(
        [
            Lambda(
                "default",
                [
                    GenericStatement([Token(TokenType.NUMBER, "3")]),
                    GenericStatement([Token(TokenType.GENERAL, "d")]),
                    BreakStatement(None),
                ],
            )
        ]
    )


def test_break_works_good_with_modifiers():
    assert str(fully_parse("vx")) == str(
        [MonadicModifier("v", RecurseStatement(None))]
    )

    assert str(fully_parse("₌xx")) == str(
        [DyadicModifier("₌", RecurseStatement(None), RecurseStatement(None))]
    )

    assert str(fully_parse("≬3dx")) == str(
        [
            Lambda(
                "default",
                [
                    GenericStatement([Token(TokenType.NUMBER, "3")]),
                    GenericStatement([Token(TokenType.GENERAL, "d")]),
                    RecurseStatement(None),
                ],
            )
        ]
    )


def test_lambda_to_newline():

    g = str(fully_parse("++)"))
    h = str(
        [
            Lambda(
                "default",
                [
                    GenericStatement([Token(TokenType.GENERAL, "+")]),
                    GenericStatement([Token(TokenType.GENERAL, "+")]),
                ],
            )
        ]
    )

    assert g == h
