"""
File: vyxal.py
Description: This is the main file for the project - the transpilation
of Vyxal programs actually happens here, and this is what gets executed
offline.
"""

import lexer
import parse
import structure


def lambda_wrap(branch: list[structure.Structure]) -> structure.Lambda:
    """
    Turns a list of structures into a single lambda structure. Useful
    for dealing with the functions of modifiers. Note that single
    elements pass their arity on to the lambda
    """

    pass
