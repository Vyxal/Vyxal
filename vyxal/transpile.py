"""
File: transpile.py
Description: This module is for transpiling Vyxal to Python
"""

import secrets
from typing import Union

import elements, helpers, lexer, parse, structure
from lexer import Token, TokenType


def lambda_wrap(branch: list[structure.Structure]) -> structure.Lambda:
    """
    Turns a List of structures into a single lambda. Useful
    for dealing with the functions of modifiers. Note that single
    elements pass their arity on to the lambda
    """

    if len(branch) == 1:
        if isinstance(branch[0], structure.GenericStatement):
            return structure.Lambda(1, [branch])
            # TODO: Actually get arity
            # of the element and make that the arity of the lambda being
            # returned. This'll be possible once we actually get the
            # command dictionary established
        elif isinstance(branch[0], structure.Lambda):
            return branch[0]
        else:
            return structure.Lambda(1, [branch])
    else:
        return structure.Lambda(1, [branch])


def transpile(program: str) -> str:
    return transpile_ast(parse.parse(lexer.tokenise(program)))


def transpile_ast(program: list[structure.Structure], indent=0) -> str:
    """
    Transpile a given program (as a parsed list of structures) into Python
    """
    if not program:
        return helpers.indent_str("pass", indent)
    return "\n".join(
        transpile_single(struct, indent=indent) for struct in program
    )


def transpile_single(
    token_or_struct: Union[Token, structure.Structure], indent: int
) -> str:
    print("transpiling: ", token_or_struct)
    if isinstance(token_or_struct, Token):
        return transpile_token(token_or_struct, indent)
    elif isinstance(token_or_struct, structure.Structure):
        return transpile_structure(token_or_struct, indent)
    raise ValueError(
        "Input must be a Token or Structure,"
        f" was {type(token_or_struct).__name__}: {token_or_struct}"
    )


def transpile_token(token: Token, indent: int) -> str:
    from helpers import indent_str, uncompress

    if token.name == TokenType.STRING:
        # Make sure we avoid any ACE exploits
        string = uncompress(token)  # TODO: Account for -D flag
        return indent_str(f'stack.append("{string!r}")', indent)
    elif token.name == TokenType.NUMBER:
        return indent_str(f"stack.append({token.value})", indent)
    elif token.name == TokenType.GENERAL:
        return indent_str(
            elements.elements.get(token.value, ("pass\n", -1))[0], indent
        )
    elif token.name == TokenType.COMPRESSED_NUMBER:
        return indent_str(f"stack.append({uncompress(token)})", indent)
    elif token.name == TokenType.COMPRESSED_STRING:
        return indent_str(f"stack.append({uncompress(token)!r})", indent)
    elif token.name == TokenType.VARIABLE_GET:
        return indent_str(f"stack.append(VAR_{token.value})", indent)
    elif token.name == TokenType.VARIABLE_SET:
        return indent_str(f"VAR_{token.value} = pop(stack, ctx=ctx)", indent)
    raise ValueError(f"Bad token: {token}")


def transpile_structure(struct: structure.Structure, indent: int) -> str:
    """
    Transpile a single structure.
    # TODO (exedraj/lyxal, user/ysthakur) implement all structures here
    """
    from helpers import indent_str

    if isinstance(struct, structure.GenericStatement):
        return transpile_single(struct.branches[0][0], indent)
    if isinstance(struct, structure.IfStatement):
        # Holds indentation level as elifs will be nested inside the else part
        new_indent = indent
        # This will be returned when the Python code is built
        res = ""

        for i in range(-1, len(struct.branches) - 1, 2):
            body = struct.branches[i + 1]

            if i > 0:
                # This isn't the first if branch, so nest it a level
                cond = struct.branches[i]
                res += indent_str("else:", new_indent)
                new_indent += 1
                res += indent_str("ctx.context_values.pop()", new_indent)
                res += transpile_ast(cond, new_indent)

            res += indent_str("condition = pop(stack, ctx=ctx)", new_indent)
            res += indent_str(
                "ctx.context_values.append(condition)", new_indent
            )
            res += indent_str("if boolify(condition, ctx):", new_indent)
            res += transpile_ast(body, new_indent + 1)

        # There's an extra else body at the end
        if len(struct.branches) % 2 == 0:
            body = struct.branches[-1]
            res += indent_str("else:", new_indent)
            res += transpile_ast(body, new_indent + 1)

        # Pop the last condition
        res += indent_str("ctx.context_values.pop()", indent)

        return res
    if isinstance(struct, structure.ForLoop):
        # TODO (user/ysthakur) make it work with multiple variables
        var = (
            struct.names[0] if struct.names else f"LOOP{secrets.token_hex(16)}"
        )
        var = f"VAR_{var}"
        return (
            indent_str(f"for {var} in iterable(pop(stack, ctx=ctx)):", indent)
            + indent_str(f"    ctx.context_values.append({var})", indent)
            + transpile_ast(struct.body, indent + 1)
            + indent_str("    ctx.context_values.pop()", indent)
        )
    if isinstance(struct, structure.WhileLoop):
        return (
            indent_str("condition = pop(stack, ctx=ctx)", indent)
            + indent_str("while boolify(condition):", indent)
            + indent_str("    ctx.context_values.append(condition)", indent)
            + transpile_ast(struct.body, indent + 1)
            + indent_str("    ctx.context_values.pop()", indent)
            + transpile_ast(struct.condition, indent + 1)
            + indent_str("    condition = pop(stack, ctx=ctx)", indent)
        )
    if isinstance(struct, structure.FunctionCall):
        return f"stack += FN_{struct.name}(stack, ctx=ctx)"
    if isinstance(struct, structure.FunctionDef):
        parameter_total = 0
        function_parameters = ""

        for parameter_token in struct.parameters:
            parameter = parameter_token.value
            if parameter.isnumeric():
                parameter_total += int(parameter)
                function_parameters += (
                    f"parameters += pop(stack, {int(parameter)}, ctx)\n"
                )
            elif parameter == "*":
                function_parameters += (
                    "parameters += " + "pop(stack, pop(stack, ctx=ctx), ctx)\n"
                )
            else:
                parameter_total += 1
                function_parameters += f"VAR_{parameter} = pop(stack, ctx=ctx)"

            return (
                indent_str(f"def FN_{struct.name}(stack, ctx)", indent)
                + indent_str("stack = []", indent + 1)
                + indent_str(f"this = FN_{struct.name}", indent + 1)
                + indent_str("ctx.input_level += 1", indent + 1)
                + indent_str(function_parameters, indent + 1)
                + indent_str("stack = parameters", indent + 1)
                + indent_str(
                    "ctx.input_values[ctx.input_level] = [stack[::], 0]",
                    indent + 1,
                )
                + transpile_ast(struct.body, indent + 1)
                + indent_str("ctx.context_values.pop()", indent + 1)
                + indent_str("ctx.input_level -= 1", indent + 1)
                + indent_str("return stack", indent + 1)
            )
    if isinstance(struct, structure.Lambda):
        signature = secrets.token_hex(16)
        # The lambda signature used to be based on time.time() until
        # I realised just how useless that was, because the calls to
        # time.time() happened within only a few milliseconds of each
        # other, meaning int(time.time()) would return the exact same
        # lambda name for multiple lambdas.

        return (
            indent_str(
                f"def _lambda_{signature}(parameters, arity, self, ctx):",
                indent,
            )
            + indent_str(f"this = _lambda_{signature}", indent + 1)
            + indent_str("overloaded_arity = False", indent + 1)
            + indent_str(
                'if "arity_overload" in dir(self): '
                + "overloaded_arity = self.arity_overload",
                indent + 1,
            )
            + indent_str(
                f"if arity and arity != {struct.branches[0]}: "
                + "stack = pop(parameters, arity, ctx))",
                indent + 1,
            )
            + indent_str(
                "elif overloaded_arity: stack = pop(parameters, arity"
                + ", ctx",
                indent + 1,
            )
            + indent_str(
                f"else: stack = pop(parameters, {struct.branches[0]}[0]"
                + ", ctx)",
                indent + 1,
            )
            + indent_str("ctx.context_values.append(stack[::])", indent + 1)
            + indent_str("ctx.input_level += 1", indent + 1)
            + indent_str(
                "ctx.input_values[ctx.input_level] = [stack[::], 0]",
                indent + 1,
            )
            + transpile_ast(struct.branches[1], indent + 1)
            + indent_str("ret = pop(stack, ctx=ctx)", indent + 1)
            + indent_str("ctx.context_values.pop()", indent + 1)
            + indent_str("ctx.input_level -= 1", indent + 1)
            + indent_str("return ret", indent + 1)
            + indent_str(f"stack.append(_lambda_{signature})", indent)
        )

    if isinstance(struct, structure.FunctionReference):
        return indent_str(f"stack.append(FN_{struct.branches[0]})", indent)

    if isinstance(struct, structure.ListLiteral):
        # We have to manually build this because we don't know how
        # many list items there will be.

        temp = indent_str("temporary_list = []", indent)
        for x in struct.items:
            temp += (
                indent_str("def list_item(s, ctx):", indent)
                + indent_str("stack = s[::]", indent + 1)
                + transpile_ast(x, indent + 1)
                + indent_str("return pop(stack, ctx=ctx)", indent + 1)
                + indent_str(
                    "temporary_list.append(list_item(stack, ctx)", indent
                )
            )

        temp += indent_str("stack.append(temp_list[::]", indent)
        return temp

    if isinstance(struct, structure.MonadicModifier):
        element_A = transpile_ast(
            [lambda_wrap([struct.branches[1][0]])], indent
        )
        return element_A + "\n" + elements.modifiers[struct.branches[0]]

    if isinstance(struct, structure.DyadicModifier):
        element_A = transpile_ast(
            [lambda_wrap([struct.branches[1][0]])], indent
        )
        element_B = transpile_ast(
            [lambda_wrap([struct.branches[1][1]])], indent
        )
        return (
            element_A
            + "\n"
            + element_B
            + "\n"
            + indent_str(elements.modifiers[struct.branches[0]], indent)
        )
    if isinstance(struct, structure.TriadicModifier):
        element_A = transpile_ast(
            [lambda_wrap([struct.branches[1][0]])], indent
        )
        element_B = transpile_ast(
            [lambda_wrap([struct.branches[1][1]])], indent
        )
        element_C = transpile_ast(
            [lambda_wrap([struct.branches[1][2]])], indent
        )
        return (
            element_A
            + "\n"
            + element_B
            + "\n"
            + element_C
            + "\n"
            + elements.modifiers[struct.branches[0]]
        )

    raise ValueError(struct)
