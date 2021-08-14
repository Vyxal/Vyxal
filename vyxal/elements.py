"""
File: elements.py
Description: This is where the element functions are stored (that is,
functions directly corresponding to Vyxal elements). It's also where
the python equivalent of command is stored
"""
import types
from typing import Union


def process_element(
    expr: Union[str, types.FunctionType], arity: int
) -> tuple[str, int]:
    """
    Take a python expression and adds boilerplate to it corresponding
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
        pushed = f"{expr.__name__}({', '.join(arguments[::-1])}, ctx)"
    else:
        pushed = expr
    py_code = (
        f"{', '.join(arguments)} = pop(stack, {arity}, ctx); "
        f"stack.append({pushed})"
    )
    return py_code, arity


elements: dict[str, tuple[str, int]] = {}
modifiers = {}
