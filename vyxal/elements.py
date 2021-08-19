"""This is where the element functions are stored

(that is, functions directly corresponding to Vyxal elements). It's also where
the python equivalent of command is stored
"""

import math
import types
from typing import Union

import sympy
from vyxal import helpers
from vyxal import LazyList

NUMBER_TYPE = "number"


def process_element(
    expr: Union[str, types.FunctionType], arity: int
) -> tuple[str, int]:
    """Take a python expression and adds boilerplate for element functions to it

    expr can be a string, which will be added verbatim to the transpiled output,
    or a function, for which a function call will be generated.

    See documents/specs/Transpilation.md for information on what happens here.
    """
    if arity:
        arguments = ["third", "rhs", "lhs"][-arity:]
    else:
        arguments = "_"

    if isinstance(expr, types.FunctionType):
        pushed = f"{expr.__name__}({', '.join(arguments[::-1])}, ctx)"
    else:
        pushed = expr
    py_code = (
        f"{', '.join(arguments)} = pop(stack, {arity}, ctx); "
        f"stack.append({pushed})"
    )
    return py_code, arity


def log_mold_multi(lhs, rhs, ctx):
    """Element •
    (num, num) -> log_lhs(rhs)
    (num, str) -> [char * lhs for char in rhs]
    (str, num) -> [char * rhs for char in lhs]
    (str, str) -> lhs.with_capitalisation_of(rhs)
    (lst, lst) -> lhs molded to the shape of rhs
    """

    ts = vy_type(lhs, rhs, True)

    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: sympy.Rational(math.log(lhs, rhs)),
        (NUMBER_TYPE, str): lambda: "".join([char * lhs for char in rhs]),
        (str, NUMBER_TYPE): lambda: "".join([char * rhs for char in lhs]),
        (str, str): lambda: helpers.transfer_capitalisation(rhs, lhs),
        (list, list): lambda: helpers.mold(lhs, rhs),
    }.get(ts, lambda: vectorise(log_mold_multi, lhs, rhs, ctx=ctx))()


def vectorise(function, lhs, rhs=None, other=None, ctx=None):
    """Do that thing with the things."""


def vy_type(item, other=None, simple=False):
    if other is not None:
        return (vy_type(item, simple=simple), vy_type(other, simple=simple))
    if (x := type(item)) in (int, sympy.Rational, complex):
        return NUMBER_TYPE
    elif simple and isinstance(item, LazyList.LazyList):
        return list
    else:
        return x


elements: dict[str, tuple[str, int]] = {
    "¬": process_element("int(not lhs)", 1),
    "∧": process_element("int(lhs and rhs)", 2),
    "⟑": process_element("int(rhs and lhs)", 2),
    "∨": process_element("int(lhs or rhs)", 2),
    "⟇": process_element("int(rhs or lhs)", 2),
    "÷": ("lhs = pop(stack, 1, ctx); stack += iterable(lhs)", 1),
    "×": process_element("'*'", 0),
    "•": process_element(log_mold_multi, 2),
}
modifiers = {}
