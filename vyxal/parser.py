"""
File: parser.py
Description: Once Vyxal programs have been tokenised using lexer.py, the
next step is to group the tokens into their corresponding structures.
This is done by treating the tokens as a queue and dequeuing tokens
until a predicate is matched for structures.
"""

from __future__ import annotations

import string
from collections import deque

try:
    import lexer
except:
    import vyxal.lexer as lexer


class StructureType:
    """
    A class providing a namespace for structure type constants. Do not
    create any instances of this class.

    Attributes
    ----------

    NONE : str
        The generic structure.

    IF_STMT : str
        If statement structure.

    FOR_LOOP : str
        For loop structure.

    WHILE_LOOP : str
        While loop structure.

    FUNCTION : str
        Function structure.

    LAMBDA : str
        Lambda structure. Note that the other lambda types
        (map, filter and sort) are just lambdas followed by the
        appropriate element token. Hence, their attributes won't be
        listed here.

    FUNCTION_REF : str
        Function reference structure.

    VARIABLE_GET : str
        Variable retrieval.

    VARIABLE_SET : str
        Variable assignment.

    LIST : str
        List literal.

    MONADIC_MODIFIER : str
        A monadic modifier - takes the next element

    DYADIC_MODIFIER : str
        A dyadic modifier - takes the next two elements

    TRIADIC_MODIFIER : str
        A triadic modifier - takes the next three elements
    """

    NONE: str = "none"
    IF_STMT: str = "if_stmt"
    FOR_LOOP: str = "for_loop"
    WHILE_LOOP: str = "while_loop"
    FUNCTION: str = "function"
    LAMBDA: str = "lambda"
    FUNCTION_REF: str = "function_ref"
    VARIABLE_GET: str = "variable_get"
    VARIABLE_SET: str = "variable_set"
    LAMBDA_MAP: str = "lambda_map"
    LAMBDA_FILTER: str = "lambda_filter"
    LAMBDA_SORT: str = "lambda_sort"
    LIST: str = "list"
    MONADIC_MODIFIER: str = "monadic_modifier"
    DYADIC_MODIFIER: str = "dyadic_modifier"
    TRIADIC_MODIFIERS: str = "triadic_modifier"


class Structure:
    """
    A class representing Vyxal structures.

    Attributes
    ----------

    name : str
        The name of the structure. Usually a StructureType literal.

    branches : [[lexer.Token]]
        The branches of the structure.

    Parameters
    ----------

    structure_name : str
        The value to use as the name of the structure

    structure_branches : [[lexer.Token]]
        The value to use as the branches of the structure
    """

    def __init__(
        self, structure_name: str, structure_branches: list[list[lexer.Token]]
    ):
        self.name: str = structure_name
        self.branches: list[list[lexer.Token]] = structure_branches

    def __str__(self) -> str:
        """
        Return a nicely formatted representation of the structure

        Returns
        -------
        str
            {name}: {value}
        """

        return f"{self.name}: {self.branches}"

    def __repr__(self) -> str:
        """
        Returns the structure as a stringified list version of name,
        value

        Returns
        -------
        str
            [name, value]
        """

        return str([self.name, self.branches])


STRUCTURE_OVERVIEW: dict[str, tuple[str]] = {
    # (Name, Closing character)
    "[": (StructureType.IF_STMT, "]"),
    "(": (StructureType.FOR_LOOP, ")"),
    "{": (StructureType.WHILE_LOOP, "}"),
    "@": (StructureType.FUNCTION, ";"),
    "λ": (StructureType.LAMBDA, ";"),
    "ƛ": (StructureType.LAMBDA_MAP, ";"),
    "'": (StructureType.LAMBDA_FILTER, ";"),
    "µ": (StructureType.LAMBDA_SORT, ";"),
    "°": (StructureType.FUNCTION_REF, ";"),
    "⟨": (StructureType.LIST, "⟩"),
}

CLOSING_CHARACTERS: str = "".join([v[1] for v in STRUCTURE_OVERVIEW.values()])
OPENING_CHARACTERS: str = "".join(STRUCTURE_OVERVIEW.keys())
MONADIC_MODIFIERS: list[str] = list("v⁽&~ß")
DYADIC_MODIFIERS: list[str] = list("₌‡₍")
TRIADIC_MODIFIERS: list[str] = list("≬")
# The modifiers are stored as lists to allow for potential digraph
# modifiers.


def process_parameters(tokens: list[lexer.Token]) -> list[str]:
    """
    Turns the tokens from the first branch of a function defintion
    structure and returns the name and parameters.

    Parameters
    ----------

    tokens : list[lexer.Token]
        The tokens to turn into the parameter details

    Returns
    -------

    list[str]
        [name, parameters...]
    """
    token_values: list[str] = [token.value for token in tokens]
    branch_data: str = "".join(token_values)
    components: list[str] = branch_data.split(":")

    parameters: list[str] = [variable_name(components[0])]
    # this'll be the list that is returned

    for parameter in components[1:]:
        if parameter.isnumeric() or parameter == "*":
            parameters.append(parameter)
        else:
            parameters.append(variable_name(parameter))

    return parameters


def variable_name(tokens: list[lexer.Token]) -> str:
    """
    Concatenates the value of all tokens and removes non-alphabet/non-
    underscore characters from the result.

    Parameters
    ----------

    tokens : list[lexer.Token]
        The tokens to turn into a single variable name.

    Returns
    -------

    str
        The token values concatenated together with non `[A-z_]`
        characters removed.
    """

    token_values: list[str] = [token.value for token in tokens]
    name: str = "".join(token_values)
    return_name: str = ""

    for char in name:
        if char in string.ascii_letters + "_":
            return_name += char

    return return_name


def parse(tokens: list[lexer.Token]) -> list[Structure]:
    """
    Transforms a tokenised Vyxal program into a list of Structures.

    Parameters
    ----------

    tokens : list[lexer.Token]
        This is the tokens that have been generated by the lexer.

    Returns
    -------

    list[Structure]
        A list of structures within the program.
    """
    structures: list[Structure] = []
    bracket_stack: list[str] = []  # all currently open structures
    tokens: deque = deque(tokens)
    branches: list[list[lexer.Token]] = []  # This will serve as a way
    # to keep track of all the
    # branches of the structure

    structure_name: str = StructureType.NONE

    while tokens:
        head: lexer.Token = tokens.popleft()
        if head.value in OPENING_CHARACTERS:
            structure_name = STRUCTURE_OVERVIEW[head.value][0]
            bracket_stack.append(STRUCTURE_OVERVIEW[head.value][1])
            branches = [[]]
            # important: each branch is a list of tokens, hence why
            # it's a double nested list to start with - each
            # token gets appended to the last branch in the branches
            # list.

            CLOSING_TOKEN: lexer.Token = lexer.Token(
                lexer.TokenType.GENERAL, bracket_stack[0]
            )
            while tokens and bracket_stack:
                # that is, while there are still tokens to consider,
                # while we are still in the structure and while the
                # next value isn't the closing character for the
                # structure (i.e. isn't Token(TokenType.GENERAL, "x"))
                # where x = the corresponding closing character).
                token: lexer.Token = tokens.popleft()
                if token.value in OPENING_CHARACTERS:
                    branches[-1].append(token)
                    bracket_stack.append(STRUCTURE_OVERVIEW[token.value][-1])

                elif token.value == "|":
                    if len(bracket_stack) == 1:
                        # that is, we are in the outer-most structure.
                        branches.append([])
                    else:
                        branches[-1].append(token)
                elif token.value in CLOSING_CHARACTERS:
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
            after_token: Structure = None
            if structure_name == StructureType.FOR_LOOP:
                if len(branches) > 1:
                    branches[0] = variable_name(branches[0])
                branches[-1] = parse(branches[-1])

            elif structure_name == StructureType.FUNCTION:
                branches[0] = process_parameters(branches[0])
                if len(branches) > 1:
                    branches[-1] = parse(branches[-1])
            elif structure_name == StructureType.FUNCTION_REF:
                branches[0] = variable_name(branches)
            elif structure_name == StructureType.LAMBDA:
                if len(branches) == 1:
                    # that is, there is only a body - no arity
                    branches.insert(0, "1")
                else:
                    branches[0] = branches[0].value
                branches[1] = parse(branches[1])
            elif structure_name == StructureType.LAMBDA_MAP:
                branches.insert(0, "1")
                branches[1] = parse(branches[1])
                structure_name = StructureType.LAMBDA
                after_token = Structure(
                    StructureType.NONE,
                    lexer.Token(lexer.TokenType.GENERAL, "M"),
                )
                # laziness ftw
            elif structure_name == StructureType.LAMBDA_MAP:
                branches.insert(0, "1")
                branches[1] = parse(branches[1])
                structure_name = StructureType.LAMBDA
                after_token = Structure(
                    StructureType.NONE,
                    lexer.Token(lexer.TokenType.GENERAL, "F"),
                )
                # laziness ftw
            elif structure_name == StructureType.LAMBDA_MAP:
                branches.insert(0, "1")
                branches[1] = parse(branches[1])
                structure_name = StructureType.LAMBDA
                after_token = Structure(
                    StructureType.NONE,
                    lexer.Token(lexer.TokenType.GENERAL, "ṡ"),
                )
                # laziness ftw
            else:
                branches = list(map(parse, branches))

            structures.append(Structure(structure_name, branches))
            if after_token is not None:
                structures.append(after_token)
        elif head.value in MONADIC_MODIFIERS:
            # the way to deal with all modifiers is to parse everything
            # after the modifier and dequeue as many structures as
            # needed to satisfy the arity of the modifier. It's import-
            # -ant that you break the while loop after dealing with the
            # modifier.

            remaining: list[Structure] = parse(tokens)
            if head.value == "⁽":
                structures.append(
                    Structure(StructureType.LAMBDA, ["1", [[remaining[0]]]])
                )
            else:
                structures.append(
                    Structure(
                        StructureType.MONADIC_MODIFIER,
                        [head.value, [remaining[0]]],
                    )
                )
            structures += remaining[1:]
            break
        elif head.value in DYADIC_MODIFIERS:
            remaining: list[Structure] = parse(tokens)
            if head.value == "‡":
                structures.append(
                    Structure(
                        StructureType.LAMBDA,
                        ["1", [remaining[0], remaining[1]]],
                    )
                )
            else:
                structures.append(
                    Structure(
                        StructureType.DYADIC_MODIFIER,
                        [head.value, [remaining[0], remaining[1]]],
                    )
                )
            structures += remaining[2:]
            break
        elif head.value in TRIADIC_MODIFIERS:
            remaining: list[Structure] = parse(tokens)
            if head.value == "‡":
                structures.append(
                    Structure(
                        StructureType.LAMBDA,
                        ["1", [remaining[0], remaining[1], remaining[2]]],
                    )
                )
            else:
                structures.append(
                    Structure(
                        StructureType.TRIADIC_MODIFIERS,
                        [
                            head.value,
                            [remaining[0], remaining[1], remaining[2]],
                        ],
                    )
                )
            structures += remaining[3:]
            break
        elif any((head.value in CLOSING_CHARACTERS, head.value in " |")):
            # that is, if someone has been a sussy baka
            # with their syntax (probably intentional).
            continue  # ignore it. This also ignores spaces btw
        else:
            structures.append(Structure(StructureType.NONE, head))

    return structures
