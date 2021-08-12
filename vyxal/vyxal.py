"""
File: vyxal.py
Description: This is the main file for the project - the transpilation
of Vyxal programs actually happens here, and this is what gets executed
offline.
"""

import lexer
import parse
import elements
from structure import *


class Context:
    def __init__(self):
        self.stack = []
        self.context_values = [0]
        self.inputs = []


ctx = Context()


def lambda_wrap(branch: list[Structure]) -> Lambda:
    """
    Turns a list of structures into a single lambda  Useful
    for dealing with the functions of modifiers. Note that single
    elements pass their arity on to the lambda

    Para
    """

    if len(branch) == 1:
        if isinstance(branch[0], GenericStatement):
            return Lambda([branch[0]])
            # TODO: Actually get arity
            # of the element and make that the arity of the lambda being
            # returned. This'll be possible once we actually get the
            # command dictionary established
        elif isinstance(branch[0], Lambda):
            return branch[0]
        else:
            return Lambda(branch)
    else:
        return Lambda(branch)


def transpile(program: str) -> str:
    program = parse.parse(lexer.tokenise(program))
    compiled = ""
    for structure in program:
        if isinstance(structure, GenericStatement):
            compiled += (
                structure.transpile().format(
                    elements.elements.get(structure.branches[0].value)
                )
                + "\n"
            )
    return compiled


if __name__ == "__main__":
    # I'm allowed to have this here this time. Frick you if you say I
    # can't.

    while True:
        # Vyxal REPL ftw
        line = input("   ")
        line = transpile(line)
        print(line)
