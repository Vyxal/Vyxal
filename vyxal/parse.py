"""Parses a list of Vyxal tokens

Once Vyxal programs have been tokenised using lexer.py, the next step is to
group the tokens into their corresponding structures.  This is done by treating
the tokens as a queue and dequeuing tokens until a predicate is matched for
structures.
"""

from __future__ import annotations

import re
import string
from collections import deque
from collections.abc import Iterable
from typing import Optional

from vyxal import lexer, structure

STRUCTURE_INFORMATION = {
    # (Name, Closing character)
    "[": (structure.IfStatement, "]"),
    "(": (structure.ForLoop, ")"),
    "{": (structure.WhileLoop, "}"),
    "@": (structure.FunctionCall, ";"),
    "λ": (structure.Lambda, ";"),
    "ƛ": (structure.LambdaMap, ";"),
    "¨2": (structure.LambdaMapDyadic, ";"),
    "¨3": (structure.LambdaMapTriadic, ";"),
    "¨₂": (structure.LambdaFilterDyadic, ";"),
    "¨₃": (structure.LambdaFilterTriadic, ";"),
    "'": (structure.LambdaFilter, ";"),
    "µ": (structure.LambdaSort, ";"),
    "⟑": (structure.LambdaMapEager, ";"),
    "⟨": (structure.ListLiteral, "⟩"),
    "¨Z": (structure.LambdaZip, ";"),
}

CLOSING_CHARACTERS = "".join([v[1] for v in STRUCTURE_INFORMATION.values()])
OPENING_CHARACTERS = list(STRUCTURE_INFORMATION.keys())
MONADIC_MODIFIERS = list("v⁽&~ßƒɖ") + ["¨=", "¨v", "¨£", "¨p"]
DYADIC_MODIFIERS = list("₌‡₍") + ["¨i"]
TRIADIC_MODIFIERS = list("≬")
# The modifiers are stored as lists to allow for potential digraph
# modifiers.

BREAK_CHARACTER = "X"
RECURSE_CHARACTER = "x"

DEFAULT_ARITY = "default"


def process_parameters(tokens: list[lexer.Token]) -> tuple[str, list[str]]:
    """Handles the tokens from the first branch of a function defintion structure

    Returns a tuple of the name and parameters."""
    token_values = [token.value for token in tokens]
    branch_data = "".join(token_values)
    components = branch_data.split(":")

    name = components[0]
    parameters = []
    # this'll be the list that is returned

    for parameter in components[1:]:
        if parameter.isnumeric() or parameter == "*":
            parameters.append(parameter)
        else:
            parameters.append(re.sub(r"[^A-z_]", "", parameter))

    return name, parameters


def variable_name(tokens: list[lexer.Token]) -> str:
    """Concatenates the value of all tokens and removes non-identifier characters

    The only characters kept are A-Z, a-z, and _
    """
    token_values = [token.value for token in tokens]
    name = "".join(token_values)
    return_name = ""

    for char in name:
        if char in string.ascii_letters + "_":
            return_name += char

    return return_name


def parse(
    token_list: Iterable[lexer.Token], parent: Optional[type] = None
) -> list[structure.Structure]:
    """Main parse function: transforms a list of Tokens into a list of Structures."""
    structures: list[structure.Structure] = []
    bracket_stack = []  # all currently open structures
    tokens = deque(token_list)
    branches: list[structure.Branch] = []  # This will serve as a way
    # to keep track of all the
    # branches of the structure

    structure_cls = structure.GenericStatement

    while tokens:
        head = tokens.popleft()
        if head.name in (
            lexer.TokenType.STRING,
            lexer.TokenType.CHARACTER,
            lexer.TokenType.COMPRESSED_NUMBER,
            lexer.TokenType.NUMBER,
            lexer.TokenType.COMPRESSED_STRING,
        ):
            structures.append(structure.GenericStatement([head]))
        elif head.name in (
            lexer.TokenType.VARIABLE_GET,
            lexer.TokenType.VARIABLE_SET,
        ):
            structures.append(structure.GenericStatement([head]))
        elif head.value == BREAK_CHARACTER:
            structures.append(structure.BreakStatement(parent))
        elif head.value == RECURSE_CHARACTER:
            structures.append(structure.RecurseStatement(parent))
        elif (
            head.name == lexer.TokenType.GENERAL
            and head.value in OPENING_CHARACTERS
        ):
            structure_cls, end_bracket = STRUCTURE_INFORMATION[head.value]
            bracket_stack.append(end_bracket)

            branches = _get_branches(tokens, bracket_stack)
            # Now, we have to actually process the branch(es) to make
            # them _nice_ for the transpiler.

            """
            Structures that have to be manually processed are:

            - For loops: if there are two+ branches, the first must be
                         made into a valid variable name (alpha + _).
            - Functions: if there are two+ branches, the first must have
                         its paramters extracted from the first branch.
            - Function References: the name must be turned into a valid
                                   function name.
            - Non-standard Lambdas: these must have their corresponding
                                    element appended after the normal
                                    lambda is appended to the structure
                                    list. This is because non-standard
                                    lambdas are just normal lambdas +
                                    an element.
            """
            if structure_cls == structure.ForLoop:
                var_names = []
                if len(branches) > 1:
                    var_names = [
                        variable_name(branch) for branch in branches[:-1]
                    ]
                body = parse(branches[-1], structure_cls)
                structures.append(structure.ForLoop(var_names, body))
            elif structure_cls == structure.WhileLoop:
                if len(branches) == 1:
                    # If there's no condition, it's an infinite loop
                    condition = [lexer.Token(lexer.TokenType.NUMBER, "1")]
                else:
                    condition = parse(branches[0], structure_cls)
                structures.append(
                    structure.WhileLoop(
                        condition, parse(branches[-1], structure_cls)
                    )
                )

            elif structure_cls == structure.FunctionCall:
                name, parameters = process_parameters(branches[0])
                if len(branches) > 1:
                    # It's got a body, so it's a function definition
                    body = parse(branches[-1], structure_cls)
                    structures.append(
                        structure.FunctionDef(name, parameters, body)
                    )
                else:
                    # No body, so it's a function call
                    assert not parameters
                    structures.append(structure.FunctionCall(name))

            elif structure_cls == structure.Lambda:
                if len(branches) == 1:
                    # that is, there is only a body - no arity
                    arity = DEFAULT_ARITY
                else:
                    try:
                        token_values = [token.value for token in branches[0]]
                        arity = int("".join(token_values))
                    except ValueError as ve:
                        raise ValueError(
                            "Arity must be parsable as an integer"
                        ) from ve
                    if arity < -1:
                        raise ValueError(
                            "Arity must be greater than or equal to -1"
                        )
                structures.append(
                    structure.Lambda(arity, parse(branches[-1], structure_cls))
                )

            elif structure_cls == structure.LambdaMap:
                structures.append(
                    structure.LambdaMap(parse(branches[0], structure_cls))
                )

            elif structure_cls == structure.LambdaMapDyadic:
                structures.append(
                    structure.LambdaMapDyadic(parse(branches[0], structure_cls))
                )

            elif structure_cls == structure.LambdaMapTriadic:
                structures.append(
                    structure.LambdaMapTriadic(
                        parse(branches[0], structure_cls)
                    )
                )

            elif structure_cls == structure.LambdaZip:
                structures.append(
                    structure.LambdaZip(parse(branches[0], structure_cls))
                )

            elif structure_cls == structure.LambdaMapEager:
                structures.append(
                    structure.LambdaMapEager(parse(branches[0], structure_cls))
                )

            elif structure_cls == structure.LambdaFilter:
                structures.append(
                    structure.LambdaFilter(parse(branches[0], structure_cls))
                )

            elif structure_cls == structure.LambdaSort:
                structures.append(
                    structure.LambdaSort(parse(branches[0], structure_cls))
                )

            else:
                branches = list(
                    map(lambda x: parse(x, parent or structure_cls), branches)
                )
                structures.append(structure_cls(*branches))

        elif head.value in MONADIC_MODIFIERS:
            # the way to deal with all modifiers is to parse everything
            # after the modifier and dequeue as many structures as
            # needed to satisfy the arity of the modifier. It's import-
            # -ant that you break the while loop after dealing with the
            # modifier.
            if not tokens:
                break
            remaining = parse(tokens)
            relevant = remaining[0]

            if isinstance(
                remaining[0],
                (structure.RecurseStatement, structure.BreakStatement),
            ):
                remaining[0].parent_structure = structure.MonadicModifier

            if head.value == "⁽":
                # 1-element lambda
                structures.append(
                    structure.Lambda(DEFAULT_ARITY, [remaining[0]])
                )
            elif head.value == "ß":
                # Conditional execute
                # This is a bit of a hacky fix, as modifiers are not
                # powerful enough to deal with this.
                structures.append(structure.IfStatement([remaining[0]]))
            else:
                structures.append(
                    structure.MonadicModifier(head.value, remaining[0])
                )
            structures += remaining[1:]
            break
        elif head.value in DYADIC_MODIFIERS:
            if not tokens:
                break
            remaining = parse(tokens)

            if len(remaining) < 2:
                remaining.append(
                    structure.GenericStatement(
                        [lexer.Token(lexer.TokenType.GENERAL, "\n")]
                    )
                )

            if isinstance(
                remaining[0],
                (structure.RecurseStatement, structure.BreakStatement),
            ):
                remaining[0].parent_structure = structure.DyadicModifier

            if isinstance(
                remaining[1],
                (structure.RecurseStatement, structure.BreakStatement),
            ):
                remaining[1].parent_structure = structure.DyadicModifier

            if head.value == "‡":
                # 2-element lambda
                structures.append(
                    structure.Lambda(
                        DEFAULT_ARITY, [remaining[0], remaining[1]]
                    )
                )
            elif head.value == "¨i":
                structures.append(
                    structure.IfStatement([remaining[0]], [remaining[1]])
                )
            else:
                structures.append(
                    structure.DyadicModifier(
                        head.value, remaining[0], remaining[1]
                    )
                )
            structures += remaining[2:]
            break
        elif head.value in TRIADIC_MODIFIERS:
            if not tokens:
                break
            remaining = parse(tokens)
            if isinstance(
                remaining[0],
                (structure.RecurseStatement, structure.BreakStatement),
            ):
                remaining[0].parent_structure = structure.TriadicModifier

            if isinstance(
                remaining[1],
                (structure.RecurseStatement, structure.BreakStatement),
            ):
                remaining[1].parent_structure = structure.TriadicModifier

            if isinstance(
                remaining[2],
                (structure.RecurseStatement, structure.BreakStatement),
            ):
                remaining[2].parent_structure = structure.TriadicModifier
            if head.value == "≬":
                # 3-element lambda
                structures.append(
                    structure.Lambda(
                        DEFAULT_ARITY,
                        [remaining[0], remaining[1], remaining[2]],
                    )
                )
            else:
                structures.append(
                    structure.TriadicModifier(
                        head.value,
                        remaining[0],
                        remaining[1],
                        remaining[2],
                    )
                )
            structures += remaining[3:]
            break
        elif head.name == lexer.TokenType.GENERAL and any(
            (head.value in CLOSING_CHARACTERS, head.value in " |")
        ):
            # that is, if someone has been a sussy baka
            # with their syntax (probably intentional).
            continue  # ignore it. This also ignores spaces btw
        else:
            structures.append(structure.GenericStatement([head]))

    return structures


def _get_branches(tokens: deque[lexer.Token], bracket_stack: list[str]):
    branches: list[structure.Branch] = [[]]
    # important: each branch is a list of tokens, hence why
    # it's a double nested list to start with - each
    # token gets appended to the last branch in the branches
    # list.

    while tokens and bracket_stack:
        # that is, while there are still tokens to consider,
        # while we are still in the structure and while the
        # next value isn't the closing character for the
        # structure (i.e. isn't Token(TokenType.GENERAL, "x"))
        # where x = the corresponding closing character).
        token: lexer.Token = tokens.popleft()
        if (
            token.name == lexer.TokenType.GENERAL
            and token.value
            and token.value in OPENING_CHARACTERS
        ):
            branches[-1].append(token)
            bracket_stack.append(STRUCTURE_INFORMATION[token.value][-1])

        elif token.name == lexer.TokenType.GENERAL and token.value == "|":
            if len(bracket_stack) == 1:
                # that is, we are in the outer-most structure.
                branches.append([])
            else:
                branches[-1].append(token)
        elif (
            token.name == lexer.TokenType.GENERAL
            and token.value
            and token.value in CLOSING_CHARACTERS
        ):
            # that is, it's a closing character that isn't
            # the one we're expecting.
            if token.value == bracket_stack[-1]:
                # that is, if it's closing the inner-most
                # structure

                bracket_stack.pop()
                if bracket_stack:
                    branches[-1].append(token)
        else:
            branches[-1].append(token)

    return branches
