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
        appropriate element token.

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
