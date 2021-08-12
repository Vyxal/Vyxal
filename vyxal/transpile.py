"""
File: transpile.py
Description: This module is for transpiling Vyxal to Python
"""


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
    """Transpile a given program into Python"""
    return transpile_structures(parse.parse(lexer.tokenise(program)))


def transpile_structures(program: list[Structure], indent=0) -> str:
    """
    Transpile a list of structures.
    """
    if not program:
        return helpers.indent_str("pass", indent)
    return "\n".join(transpile_structure(struct, indent=indent) for struct in program)


def transpile_structure(struct: Structure, indent=0) -> str:
    """
    Transpile a single structure.
    """
    from helpers import indent_str, indent_code

    if isinstance(struct, GenericStatement):
        pass
    if isinstance(struct, IfStatement):
        branches = [""] + struct.branches

        new_indent = indent

        for i in range(0, len(branches), 2):
            cond = branches[i]
            body = branches[i + 1]

            if i > 0:
                # This isn't the first if branch, so nest it a level
                res += indent_str("else:", new_indent)
                new_indent += 1
                res += transpile_structures(cond, new_indent)

            res += indent_str("condition = pop(stack)", new_indent)
            res += indent_str("context_values.append(condition)", new_indent)
            res += indent_str("if boolify(condition):", new_indent)
            res += transpile_structures(body, new_indent + 1)

        # There's an extra else body at the end
        if len(branches) % 2 == 1:
            body = branches[-1]
            res += indent_str("else:", new_indent)
            res += transpile_structures(body, new_indent + 1)

        # Pop all the conditions from before
        res += len(branches) // 2 * indent_str("context_values.pop()", indent)

        return res
    if isinstance(struct, ForLoop):
        var = struct.name if struct.name else f"LOOP{secrets.token_hex(16)}"
        var = f"VAR_{var}"
        return (
            indent_str(f"for {var} in iterable(pop(stack)):", indent)
            + indent_str("    context_values.append({var})", indent)
            + transpile_structures(struct.body, indent + 1)
            + indent_str("    context_values.pop()", indent)
        )
    if isinstance(struct, WhileLoop):
        return (
            indent_str(f"condition = pop(stack)", indent)
            + indent_str(f"while boolify(condition):", indent)
            + indent_str("    context_values.append(condition)", indent)
            + transpile_structures(struct.body, indent + 1)
            + indent_str("    context_values.pop()", indent)
            + transpile_structures(struct.condition, indent + 1)
            + indent_str("    condition = pop(stack)", indent)
        )
    if isinstance(struct, FunctionCall):
        pass
    if isinstance(struct, Lambda):
        pass
    if isinstance(struct, LambdaMap):
        pass
    if isinstance(struct, LambdaFilter):
        pass
    if isinstance(struct, LambdaSort):
        pass
    if isinstance(struct, FunctionReference):
        pass
    if isinstance(struct, ListLiteral):
        pass
    if isinstance(struct, MonadicModifier):
        pass
    if isinstance(struct, DyadicModifier):
        pass
    if isinstance(struct, TriadicModifier):
        pass
