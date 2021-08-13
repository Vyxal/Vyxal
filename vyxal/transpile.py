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
            return structure.Lambda([branch[0]])
            # TODO: Actually get arity
            # of the element and make that the arity of the lambda being
            # returned. This'll be possible once we actually get the
            # command dictionary established
        elif isinstance(branch[0], structure.Lambda):
            return branch[0]
        else:
            return structure.Lambda(branch)
    else:
        return structure.Lambda(branch)


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
    if isinstance(token_or_struct, Token):
        return transpile_token(token_or_struct, indent)
    elif isinstance(token_or_struct, structure.Structure):
        return transpile_structure(token_or_struct, indent)
    raise ValueError(
        f"Input must be a Token or Structure,"
        " was {type(token_or_struct).__name__}: {token_or_struct}"
    )


def transpile_token(token: Token, indent: int) -> str:
    from helpers import indent_str, uncompress

    print(token.name, TokenType.GENERAL)

    if token.name == TokenType.STRING:
        # Make sure we avoid any ACE exploits
        string = uncompress(token)  # TODO: Account for -D flag
        value = string.replace("\\", "\\\\").replace('"', '\\"')
        return indent_str(f'stack.append("{value}")', indent)
    elif token.name == TokenType.NUMBER:
        return indent_str(f"stack.append({token.value})", indent)
    elif token.name == TokenType.GENERAL:
        return elements.elements.get(token.value, ["pass\n", -1])[0]
    elif token.name == TokenType.COMPRESSED_NUMBER:
        return indent_str(f"stack.append({uncompress(token)})")
    elif token.name == TokenType.COMPRESSED_STRING:
        return indent_str(f"stack.append('{uncompress(token)}')")
        # No need to check for ACE exploits here because this string
        # type will only ever contain lower alpha + space.
    elif token.name == TokenType.VARIABLE_GET:
        return indent_str(f"stack.append(VAR_{token.value})")
    elif token.name == TokenType.VARIABLE_SET:
        return indent_str(f"VAR_{token.value} = pop(stack, ctx=ctx)")
    raise ValueError(f"Bad token: {token}")


def transpile_structure(struct: structure.Structure, indent: int) -> str:
    """
    Transpile a single structure.
    # TODO (exedraj/lyxal, user/ysthakur) implement all structures here
    """
    from helpers import indent_str

    if isinstance(struct, structure.GenericStatement):
        return transpile_single(struct.branches[0], indent)
    if isinstance(struct, structure.IfStatement):
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
            res += indent_str("context_values.pop()", new_indent)
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
    if isinstance(struct, structure.ForLoop):
        var = struct.name if struct.name else f"LOOP{secrets.token_hex(16)}"
        var = f"VAR_{var}"
        return (
            indent_str(f"for {var} in iterable(pop(stack)):", indent)
            + indent_str(f"    context_values.append({var})", indent)
            + transpile_ast(struct.body, indent + 1)
            + indent_str("    context_values.pop()", indent)
        )
    if isinstance(struct, structure.WhileLoop):
        return (
            indent_str(f"condition = pop(stack)", indent)
            + indent_str(f"while boolify(condition):", indent)
            + indent_str("    context_values.append(condition)", indent)
            + transpile_ast(struct.body, indent + 1)
            + indent_str("    context_values.pop()", indent)
            + transpile_ast(struct.condition, indent + 1)
            + indent_str("    condition = pop(stack)", indent)
        )
    if isinstance(struct, structure.FunctionCall):
        if len(struct.branches) == 1:
            # That is, you're calling the function
            return f"stack += FN_{struct.branches[0][0]}(stack, ctx=ctx)"
        else:
            # That is, you're defining the function
            # the
            return "def STUFF(): pass\n"
        return """def FN_{}(parameters, *, ctx):
    this = FN_{}
    context_values.append(parameters[-{}:])
    input_level += 1
    stack = []
    {}
    input_values[input_level] = [stack[::], 0]
    {}
    context_values.pop()
    input_level -= 1
    return stack
"""
    if isinstance(struct, structure.Lambda):
        raise Error("I WANT A RAISE")
        return """
def _lambda_{}(parameters, arity, self, *, ctx):
    this = _lambda_{}
    overloaded_arity = False

    if "arity_overload" in dir(self): overloaded_arity = self.arity_overload

    if arity and arity != {}: stack = pop(parameters, arity)
    elif overloaded_arity: stack = pop(parameters, arity)
    else: stack = pop(parameters, {})

    context_values.append(stack[::])
    input_level += 1
    input_values[input_level] = [stack[::], 0]

    {}
    ret = pop(stack)
    context_values.pop()
    input_level -= 1

    return ret
stack.append(_lambda_{})
"""
    if isinstance(struct, structure.FunctionReference):
        raise Error("I WANT A RAISE")
        return "stack.append(FN_{})"
    if isinstance(struct, structure.ListLiteral):
        raise Error("I WANT A RAISE")
        # We have to manually build this because we don't know how
        # many list items there will be.

        temp = "temporary_List = []"
        temp += (
            "\ndef List_item(s, ctx):\n    stack = s[::]\n    "
            "{}\n    return pop(stack, ctx=ctx)\n"
            "temp_List.append(List_item(stack))\n"
        ) * len(self.items)
        temp += "stack.append(temp_List[::])"
        return temp
    if isinstance(struct, structure.MonadicModifier):
        element_A = transpile(lambda_wrap(struct.branches[1][0]))
        return element_A + "\n" + elements.modifiers.get(struct.branches[0])

    if isinstance(struct, structure.DyadicModifier):
        element_A = transpile(lambda_wrap(struct.branches[1][0]))
        element_B = transpile(lambda_wrap(struct.branches[1][1]))
        return (
            element_A
            + "\n"
            + element_B
            + "\n"
            + elements.modifiers.get(struct.branches[0])
        )
    if isinstance(struct, structure.TriadicModifier):
        element_A = transpile(lambda_wrap(struct.branches[1][0]))
        element_B = transpile(lambda_wrap(struct.branches[1][1]))
        element_C = transpile(lambda_wrap(struct.branches[1][2]))
        return (
            element_A
            + "\n"
            + element_B
            + "\n"
            + element_C
            + "\n"
            + elements.modifiers.get(struct.branches[0])
        )

    raise ValueError(struct)
