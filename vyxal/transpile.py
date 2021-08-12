"""
File: transpile_ast.py
Description: This module is for transpiling Vyxal to Python
"""

import secrets
from typing import List, Union

from vyxal import elements, helpers, lexer, parse
from vyxal.lexer import Token, TokenType
from vyxal.structure import *


def lambda_wrap(branch: List[Structure]) -> Lambda:
    """
    Turns a List of structures into a single lambda  Useful
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
    return transpile_ast(parse.parse(lexer.tokenise(program)))


def transpile_ast(program: List[Structure], indent=0) -> str:
    """
    Transpile a given program (as a parsed list of structures) into Python
    """
    if not program:
        return helpers.indent_str("pass", indent)
    return "\n".join(transpile_single(struct, indent=indent) for struct in program)


def transpile_single(token_or_struct: Union[Token, Structure], indent: int) -> str:
    if isinstance(token_or_struct, Token):
        return transpile_token(token_or_struct, indent)
    elif isinstance(token_or_struct, Structure):
        return transpile_structure(token_or_struct, indent)
    raise ValueError(
        f"Input must be a Token or Structure, was {type(token_or_struct).__name__}: {token_or_struct}"
    )


def transpile_token(token: Token, indent: int) -> str:
    from vyxal.helpers import indent_str

    if token.name == TokenType.STRING:
        return indent_str(f"stack.append('{token.value}')", indent)
    elif token.name == TokenType.NUMBER:
        return indent_str(f"stack.append({token.value})", indent)
    elif token.name == TokenType.NAME:
        pass
    elif token.name == TokenType.GENERAL:
        pass  # return elements.elements.get(token.value) @lyxal is this right?
    elif token.name == TokenType.COMPRESSED_NUMBER:
        pass
    elif token.name == TokenType.COMPRESSED_STRING:
        pass
    elif token.name == TokenType.VARIABLE_GET:
        pass
    elif token.name == TokenType.VARIABLE_SET:
        pass
    raise ValueError(f"Bad token: {token}")


def transpile_structure(struct: Structure, indent: int) -> str:
    """
    Transpile a single structure.
    """
    from vyxal.helpers import indent_str

    if isinstance(struct, GenericStatement):
        return transpile_single(struct.branches[0], indent)
    if isinstance(struct, IfStatement):
        # Add an empty "branch" as the first condition
        branches = [""] + struct.branches
        # Holds indentation level as elifs will be nested inside the else part
        new_indent = indent
        # This will be returned when the Python code is built
        res = ""

        for i in range(0, len(branches) - 1, 2):
            cond = branches[i]
            body = branches[i + 1]

            if i > 0:
                # This isn't the first if branch, so nest it a level
                res += indent_str("else:", new_indent)
                new_indent += 1
                res += transpile_ast(cond, new_indent)

            res += indent_str("condition = pop(stack)", new_indent)
            res += indent_str("context_values.append(condition)", new_indent)
            res += indent_str("if boolify(condition):", new_indent)
            res += transpile_ast(body, new_indent + 1)

        # There's an extra else body at the end
        if len(branches) % 2 == 1:
            body = branches[-1]
            res += indent_str("else:", new_indent)
            res += transpile_ast(body, new_indent + 1)

        # Pop all the conditions from before
        res += len(branches) // 2 * indent_str("context_values.pop()", indent)

        return res
    if isinstance(struct, ForLoop):
        var = struct.name if struct.name else f"LOOP{secrets.token_hex(16)}"
        var = f"VAR_{var}"
        return (
            indent_str(f"for {var} in iterable(pop(stack)):", indent)
            + indent_str("    context_values.append({var})", indent)
            + transpile_ast(struct.body, indent + 1)
            + indent_str("    context_values.pop()", indent)
        )
    if isinstance(struct, WhileLoop):
        return (
            indent_str(f"condition = pop(stack)", indent)
            + indent_str(f"while boolify(condition):", indent)
            + indent_str("    context_values.append(condition)", indent)
            + transpile_ast(struct.body, indent + 1)
            + indent_str("    context_values.pop()", indent)
            + transpile_ast(struct.condition, indent + 1)
            + indent_str("    condition = pop(stack)", indent)
        )
    if isinstance(struct, FunctionCall):
        raise Error("I WANT A RAISE")
    if isinstance(struct, Lambda):
        raise Error("I WANT A RAISE")
    if isinstance(struct, LambdaMap):
        raise Error("I WANT A RAISE")
    if isinstance(struct, LambdaFilter):
        raise Error("I WANT A RAISE")
    if isinstance(struct, LambdaSort):
        raise Error("I WANT A RAISE")
    if isinstance(struct, FunctionReference):
        raise Error("I WANT A RAISE")
    if isinstance(struct, ListLiteral):
        raise Error("I WANT A RAISE")
    if isinstance(struct, MonadicModifier):
        raise Error("I WANT A RAISE")
    if isinstance(struct, DyadicModifier):
        raise Error("I WANT A RAISE")
    if isinstance(struct, TriadicModifier):
        raise Error("I WANT A RAISE")

    raise ValueError(struct)
