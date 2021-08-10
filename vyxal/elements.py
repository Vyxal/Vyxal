"""
File: elements.py
Description: This is where the element functions are stored (that is, 
functions directly corresponding to Vyxal elements). It's also where
the python equivalent of command is stored
"""

from helpers import *

# or should that be import helpers?


def process_element(expr: str, arity: int) -> str:
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

    str
        See documents/specs/Transpilation.md for information
    """

    arguments = ", ".join(["third", "rhs", "lhs"][-arity:])

    return f"{arguments} = pop(stack, {arity}, ctx); stack.append({expr})"


elements = {
    # having a dictionary with the same name as the library can't go
    # wrong now can it? \s
}
