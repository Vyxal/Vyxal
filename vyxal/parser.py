"""
File: parser.py
Description: Once Vyxal programs have been tokenised using lexer.py, the
next step is to group the tokens into their corresponding structures.
This is done by treating the tokens as a queue and dequeuing tokens
until a predicate is matched for structures.
"""

from __future__ import annotations

import collections
import string

import lexer

OPENING_CHARACTERS = "[({@λ"


class StructureType:
    """
    A class providing a namespace for structure type constants. Do not
    create any instances of this class.

    Attributes
    ----------

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

    """

    IF_STMT: str = "if_stmt"
    FOR_LOOP: str = "for_loop"
    WHILE_LOOP: str = "while_loop"
    FUNCTION: str = "function"
    LAMBDA: str = "lambda"
    FUNCTION_REF: str = "function_ref"
    VARIABLE_GET: str = "variable_get"
    VARIABLE_SET: str = "variable_set"
    LAMBDA_MAP: str = "lambda_map"
    LAMDBA_FILTER: str = "lambda_filter"
    LAMBDA_SORT: str = "lambda_sort"


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
    bracket_stack: list[str] = []  # This is so we can keep track of
    # which closing characters we need to
    # watch out for when dealing with
    # opening and closing brackets.
    tokens: collections.deque = collections.deque()
    branches: list[list[lexer.Token]] = []  # This will serve as a way
    # to keep track of all the
    # branches of the structure

    structure_name: str = ""

    while tokens:
        head: lexer.Token = tokens.popleft()
        if bracket_stack:
            # that is, if we are currently inside a structure...
            if bracket_stack[-1] == head:
                bracket_stack.pop()

            if not bracket_stack:
                # that is, if what we just closed is the outer-most
                # structure....

                if structure_name == StructureType.FOR_LOOP:
                    branches[0] = variable_name(branches[0])

                elif structure_name == StructureType.FUNCTION:
                    # code that epicly handles parameter stuff
                    pass

                elif structure_name == StructureType.FUNCTION_REF:
                    branches[0] = variable_name(branches[0])

                elif structure_name == StructureType.LAMBDA_MAP:
                    structure_name = StructureType.LAMBDA
                    # code that says to insert the `M` element after
                    # that structure goes here

                elif structure_name == StructureType.LAMDBA_FILTER:
                    structure_name = StructureType.LAMBDA
                    # code that says to insert the `F` element after
                    # that structure goes here

                elif structure_name == StructureType.LAMBDA_SORT:
                    structure_name = StructureType.LAMBDA
                    # code that says to insert the `ṡ` element after
                    # that structure goes here

                structures.append(Structure(structure_name, branches))

    return structures
