"""
File: elements.py
Description: This is where the element functions are stored (that is, 
functions directly corresponding to Vyxal elements). It's also where
the python equivalent of command is stored
"""
import types
from typing import Union
from helpers import *

# or should that be import helpers?


def add(lhs, rhs, ctx):
    return lhs + rhs


def process_element(
    expr: Union[str, types.FunctionType], arity: int
) -> tuple[str, int]:
    """
    Takes a python expression and adds boilerplate to it corresponding
    to the arity of the element being processed.

    Parameters
    ----------

    expr : str
        The expression to wrap in boilerplate

    arity : int
        The arity of the element - <= 3

    Returns
    -------

    (str, int)
        See documents/specs/Transpilation.md for information
    """

    arguments = ["third", "rhs", "lhs"][-arity:] if arity else "_"
    if isinstance(expr, types.FunctionType):
        return (
            f"{', '.join(arguments)} = pop(stack, {arity}, ctx); "
            f"stack.append({expr.__name__}({', '.join(arguments[::-1])}, ctx))",
            arity,
        )
    else:
        return (
            f"{', '.join(arguments)} = pop(stack, {arity}, ctx); "
            f"stack.append({expr})",
            arity,
        )


elements = {
    "+": process_element(add, 2),
    "!": process_element("len(stack)", 0),
    ":": "lhs = pop(stack, ctx=ctx); stack.append(copy(lhs)); stack.append(copy(lhs))",
}

print(elements)
