"""Transpiles Vyxal code into Python"""


import secrets
from typing import Union

from vyxal import elements, helpers, lexer, parse, structure
from vyxal.helpers import indent_str, uncompress
from vyxal.lexer import Token, TokenType
from vyxal.LazyList import vyxalify


def lambda_wrap(branch: list[structure.Structure]) -> structure.Lambda:
    """Turns a List of structures into a single lambda.

    Useful for dealing with the functions of modifiers. Note that single

    elements pass their arity on to the lambda
    """

    if len(branch) == 1:
        if isinstance(branch[0], structure.GenericStatement):
            return structure.Lambda(
                elements.elements.get(branch[0].branches[0][0].value, ("", 1))[
                    1
                ],
                branch,
            )
        elif isinstance(branch[0], structure.Lambda):
            return branch[0]
        else:
            return structure.Lambda(1, branch)
    else:
        return structure.Lambda(1, branch)


def transpile(program: str) -> str:
    return transpile_ast(parse.parse(lexer.tokenise(program)))


def transpile_ast(program: list[structure.Structure], indent=0) -> str:
    """Transpile a given program (as a parsed list of structures) into Python"""

    if not program:
        return helpers.indent_str("pass", indent)
    return "\n".join(
        transpile_single(struct, indent=indent) for struct in program
    )


def transpile_single(
    token_or_struct: Union[Token, structure.Structure], indent: int
) -> str:
    if isinstance(token_or_struct, Token):
        return transpile_token(token_or_struct, indent)
    elif isinstance(token_or_struct, structure.Structure):
        return transpile_structure(token_or_struct, indent)
    raise ValueError(
        "Input must be a Token or Structure,"
        f" was {type(token_or_struct).__name__}: {token_or_struct}"
    )


def transpile_token(token: Token, indent: int) -> str:
    if token.name == TokenType.STRING:
        # Make sure we avoid any ACE exploits
        string = uncompress(token)  # TODO: Account for -D flag
        # Can't use {string!r} inside the f-string because that
        # screws up escape sequences.
        return indent_str(f'stack.append("{string}")', indent)
    elif token.name == TokenType.NUMBER:
        if token.value.count("."):
            if token.value == ".":
                return indent_str("stack.append(sympy.Rational(1, 2))", indent)
            return indent_str(
                f'stack.append(vyxalify(sympy.Rational("{token.value}")))',
                indent,
            )
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
        return indent_str(f"VAR_{token.value} = pop(stack, 1, ctx=ctx)", indent)
    raise ValueError(f"Bad token: {token}")


def transpile_structure(struct: structure.Structure, indent: int) -> str:
    """Transpile a single structure."""

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

            res += indent_str("condition = pop(stack, 1, ctx=ctx)", new_indent)
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
        # TODO (user/cgccuser) make it work with multiple variables
        var = (
            struct.names[0] if struct.names else f"LOOP{secrets.token_hex(16)}"
        )
        var = f"VAR_{var}"
        return (
            indent_str(
                f"for {var} in iterable(pop(stack, 1, ctx=ctx), range, ctx):",
                indent,
            )
            + indent_str(f"    ctx.context_values.append({var})", indent)
            + transpile_ast(struct.body, indent + 1)
            + indent_str("    ctx.context_values.pop()", indent)
        )
    if isinstance(struct, structure.WhileLoop):
        return (
            indent_str("condition = pop(stack, 1, ctx=ctx)", indent)
            + indent_str("while boolify(condition):", indent)
            + indent_str("    ctx.context_values.append(condition)", indent)
            + transpile_ast(struct.body, indent + 1)
            + indent_str("    ctx.context_values.pop()", indent)
            + transpile_ast(struct.condition, indent + 1)
            + indent_str("    condition = pop(stack, ctx=ctx)", indent)
        )
    if isinstance(struct, structure.FunctionCall):
        return (
            f"stack += FN_{struct.name}(stack, self=FN_{struct.name}, ctx=ctx)"
        )
    if isinstance(struct, structure.FunctionDef):
        parameter_total = 0
        function_parameters = ""
        print(struct.parameters)
        for parameter in struct.parameters:
            if parameter.isnumeric():
                parameter_total += int(parameter)
                function_parameters += (
                    f"parameters += wrapify(arg_stack, {int(parameter)}"
                    + ", ctx)\n"
                )
            elif parameter == "*":
                function_parameters += (
                    "parameters += "
                    + "wrapify(stack, pop(arg_stack, 1, ctx=ctx), ctx=ctx)"
                    + "\n"
                )
            else:
                parameter_total += 1
                function_parameters += (
                    f"VAR_{parameter} = pop(arg_stack, 1, ctx=ctx)\n"
                )

        return (
            indent_str(
                f"def FN_{struct.name}(arg_stack, self, arity=-1, ctx=None):",
                indent,
            )
            + indent_str("parameters = []", indent + 1)
            + indent_str(function_parameters, indent + 1)
            + indent_str("stack = parameters[::]", indent + 1)
            + indent_str(
                "ctx.context_values.append(parameters[::])", indent + 1
            )
            + indent_str("ctx.stacks.append(stack)", indent + 1)
            + indent_str("ctx.inputs.append([parameters[::], 0])", indent + 1)
            + indent_str(f"this = FN_{struct.name}", indent + 1)
            + indent_str(transpile_ast(struct.body), indent + 1)
            + indent_str("ctx.context_values.pop()", indent + 1)
            + indent_str("ctx.inputs.pop()", indent + 1)
            + indent_str("ctx.stacks.pop()", indent + 1)
            + indent_str("return stack", indent + 1)
        )
    if isinstance(struct, structure.Lambda):
        id_ = secrets.token_hex(16)
        # The lambda id used to be based on time.time() until
        # I realised just how useless that was, because the calls to
        # time.time() happened within only a few milliseconds of each
        # other, meaning int(time.time()) would return the exact same
        # lambda name for multiple lambdas.

        return (
            indent_str(
                f"def _lambda_{id_}(arg_stack, self, arity=-1, ctx=None):",
                indent,
            )
            + indent_str(
                "if arity != -1: stack = wrapify(arg_stack, arity, "
                + "ctx=ctx)",
                indent + 1,
            )
            + indent_str(
                "elif 'stored_arity' in dir(self): "
                + "stack = wrapify(arg_stack, self.stored_arity, ctx)",
                indent + 1,
            )
            + indent_str(
                "else: stack = wrapify(arg_stack, "
                + f"{struct.branches[0]}"
                + ", ctx)",
                indent + 1,
            )
            + indent_str(f"this = _lambda_{id_}", indent + 1)
            + indent_str("ctx.context_values.append(stack[::])", indent + 1)
            + indent_str(
                "ctx.inputs.append([stack[::], 0]);",
                indent + 1,
            )
            + indent_str("ctx.stacks.append(stack)", indent + 1)
            + indent_str(transpile_ast(struct.body), indent + 1)
            + indent_str("res = [pop(stack, 1, ctx)]", indent + 1)
            + indent_str("ctx.context_values.pop()", indent + 1)
            + indent_str("ctx.inputs.pop()", indent + 1)
            + indent_str("ctx.stacks.pop()", indent + 1)
            + indent_str("return res", indent + 1)
            + indent_str(f"_lambda_{id_}.arity = {struct.branches[0]}", indent)
            + indent_str(f"stack.append(_lambda_{id_})", indent)
        )

    if isinstance(struct, structure.FunctionReference):
        return indent_str(f"stack.append(FN_{struct.branches[0]})", indent)

    if isinstance(struct, structure.ListLiteral):
        # We have to manually build this because we don't know how
        # many list items there will be.

        temp = indent_str("temp_list = []", indent)
        for x in struct.items:
            temp += (
                indent_str("def list_item(s, ctx):", indent)
                + indent_str("stack = s[::]", indent + 1)
                + transpile_ast(x, indent + 1)
                + indent_str("return pop(stack, 1, ctx=ctx)", indent + 1)
                + indent_str("temp_list.append(list_item(stack, ctx))", indent)
            )

        temp += indent_str("stack.append(temp_list[::])", indent)
        return temp

    if isinstance(struct, structure.MonadicModifier):
        element_A = transpile_ast([lambda_wrap([struct.function_A])], indent)
        return (
            element_A
            + "\n"
            + indent_str("function_A = pop(stack, 1, ctx)", indent)
            + indent_str(
                elements.modifiers.get(struct.modifier, "pass"), indent
            )
        )

    if isinstance(struct, structure.DyadicModifier):
        element_A = transpile_ast([lambda_wrap([struct.function_A])], indent)
        element_B = transpile_ast([lambda_wrap([struct.function_B])], indent)
        return (
            element_A
            + "\n"
            + indent_str("function_A = pop(stack, 1, ctx)", indent)
            + element_B
            + "\n"
            + indent_str("function_B = pop(stack, 1, ctx)", indent)
            + indent_str(
                elements.modifiers.get(struct.modifier, "pass"), indent
            )
        )
    if isinstance(struct, structure.TriadicModifier):
        element_A = transpile_ast([lambda_wrap([struct.function_A])], indent)
        element_B = transpile_ast([lambda_wrap([struct.function_B])], indent)
        element_C = transpile_ast([lambda_wrap([struct.function_C])], indent)
        return (
            element_A
            + "\n"
            + indent_str("function_A = pop(stack, 1, ctx)", indent)
            + element_B
            + "\n"
            + indent_str("function_B = pop(stack, 1, ctx)", indent)
            + element_C
            + "\n"
            + indent_str("function_C = pop(stack, 1, ctx)", indent)
            + indent_str(
                elements.modifiers.get(struct.modifier, "pass"), indent
            )
        )

    if isinstance(struct, structure.BreakStatement):
        if struct.parent_structure == structure.IfStatement:
            return indent_str("pass", indent)
        elif (
            struct.parent_structure == structure.ForLoop
            or struct.parent_structure == structure.WhileLoop
        ):
            return indent_str("break", indent)
        elif struct.parent_structure == structure.FunctionDef:
            return (
                indent_str("ctx.inputs.pop()", indent)
                + indent_str("ctx.context_values.pop()", indent)
                + indent_str("return stack", indent)
            )
        elif struct.parent_structure == structure.Lambda:
            return (
                indent_str("ret = [pop(stack, 1, ctx=ctx)]", indent)
                + indent_str("ctx.context_values.pop()", indent)
                + indent_str("ctx.inputs.pop()", indent)
                + indent_str("return ret", indent)
            )
        else:
            return indent_str("pass", indent)
    if isinstance(struct, structure.RecurseStatement):
        if struct.parent_structure == structure.IfStatement:
            return indent_str("pass", indent)
        elif (
            struct.parent_structure == structure.ForLoop
            or struct.parent_structure == structure.WhileLoop
        ):
            return indent_str("continue", indent)
        elif struct.parent_structure == structure.FunctionDef:
            return indent_str(
                "stack.append(this(stack, this, ctx=ctx))", indent
            )
        elif struct.parent_structure == structure.Lambda:
            return indent_str(
                "stack.append(this(stack, this, ctx=ctx))", indent
            )
        else:
            return indent_str("vy_print(stack, ctx=ctx)", indent)

    assert False
