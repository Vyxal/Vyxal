"""Transpiles Vyxal code into Python"""

import re
import secrets
from typing import Union

from vyxal import encoding, helpers, lexer, parse, structure
from vyxal.elements import *
from vyxal.helpers import indent_str, uncompress
from vyxal.lexer import Token, TokenType
from vyxal.encoding import utf8_to_vyxal

NILADIC_TYPES = (
    TokenType.STRING,
    TokenType.NUMBER,
    TokenType.COMPRESSED_NUMBER,
    TokenType.COMPRESSED_STRING,
    TokenType.VARIABLE_GET,
    TokenType.CODEPAGE_NUMBER,
)

# TranspilationOptions has been moved to context.py
from vyxal.context import TranspilationOptions


def lambda_wrap(
    branch: list[vyxal.structure.Structure],
) -> vyxal.structure.Lambda:
    """Turns a List of structures into a single lambda.

    Useful for dealing with the functions of modifiers. Note that single

    elements pass their arity on to the lambda
    """
    if len(branch) == 1:
        if isinstance(branch[0], vyxal.structure.GenericStatement):
            if branch[0].branches[0][0].name in NILADIC_TYPES:
                return vyxal.structure.Lambda(0, branch)
            return vyxal.structure.Lambda(
                elements.get(branch[0].branches[0][0].value, ("", 1))[1],
                branch,
            )
        elif isinstance(branch[0], vyxal.structure.RecurseStatement):
            return vyxal.structure.Lambda(vyxal.parse.DEFAULT_ARITY, branch)
        elif isinstance(branch[0], vyxal.structure.Lambda):
            return branch[0]
        else:
            return vyxal.structure.Lambda(vyxal.parse.DEFAULT_ARITY, branch)
    else:
        return vyxal.structure.Lambda(vyxal.parse.DEFAULT_ARITY, branch)


def transpile(
    program: str,
    options: TranspilationOptions = TranspilationOptions(),
) -> str:
    return transpile_ast(
        vyxal.parse.parse(
            vyxal.lexer.tokenise(program, options.variables_as_digraphs)
        ),
        options=options,
    )


def transpile_ast(
    program: list[vyxal.structure.Structure],
    indent: int = 0,
    options: TranspilationOptions = TranspilationOptions(),
) -> str:
    """Transpile a given program (as a parsed list of structures) into Python"""
    if not program:
        return helpers.indent_str("pass", indent)
    return "\n".join(
        transpile_single(struct, indent=indent, options=options)
        for struct in program
    )


def transpile_single(
    token_or_struct: Union[Token, vyxal.structure.Structure],
    indent: int,
    options: TranspilationOptions,
) -> str:
    """Transpile a single token or structure"""
    if isinstance(token_or_struct, Token):
        return transpile_token(token_or_struct, indent, options=options)
    elif isinstance(token_or_struct, vyxal.structure.Structure):
        return transpile_structure(token_or_struct, indent, options=options)
    raise ValueError(
        "Input must be a Token or Structure,"
        f" was {type(token_or_struct).__name__}: {token_or_struct}"
    )


def transpile_token(
    token: Token, indent: int, options: TranspilationOptions
) -> str:
    """Transpile a single token"""
    if token.name == TokenType.STRING:
        # Make sure we avoid any ACE exploits
        if options.dict_compress and not options.utf8strings:
            string = uncompress(token)
        else:
            string = token.value
        # Can't use {string!r} inside the f-string because that
        # screws up escape sequences.

        # So instead, we have to manually escape the string
        temp = ""
        iterator = iter(string)
        for char in iterator:
            if char == "\\":
                after_char = next(iterator, "")
                if after_char == "`":
                    temp += "`"
                elif after_char == "Π":
                    temp += "Π"
                else:
                    temp += "\\" + after_char
            elif char == '"':
                temp += '\\"'
            elif char == "\n":
                temp += "\\n"
            elif char == "Π" and not options.utf8strings:
                temp += '" + vy_str(pop(stack, 1, ctx), ctx=ctx) + "'
            else:
                temp += char
        if options.utf8strings:
            # Since all string-related stuff is ASCII this shouldn't ACE
            temp = bytes(map(ord, utf8_to_vyxal(temp))).decode("utf-8")
        return indent_str(f'stack.append("{temp}")', indent)
    elif token.name == TokenType.NUMBER:
        parts = [
            "0.5" if part == "." else part for part in token.value.split("°")
        ]

        parts = "+".join(parts)
        if parts[0] == "+":
            parts = (parts or "1") + "I"
        elif parts[-1] == "+":
            parts = parts + "1 * I"
        elif "+" in parts:
            parts = parts + "* I"

        return indent_str(f'stack.append(sympy.nsimplify("{parts}"))', indent)
    elif token.name == TokenType.GENERAL:
        return indent_str(elements.get(token.value, ("pass\n", -1))[0], indent)
    elif token.name == TokenType.COMPRESSED_NUMBER:
        return indent_str(f"stack.append({uncompress(token)})", indent)
    elif token.name == TokenType.COMPRESSED_STRING:
        return indent_str(f"stack.append({uncompress(token)!r})", indent)
    elif token.name == TokenType.VARIABLE_GET:
        if token.value == "":
            return indent_str("stack.append(ctx.ghost_variable)", indent)
        elif token.value[0] == "_":
            return indent_str(f"stack.append(ctx.VAR_{token.value})", indent)
        else:
            return indent_str(f"stack.append(VAR_{token.value});", indent)
    elif token.name == TokenType.VARIABLE_SET:
        if token.value == "":
            return indent_str(
                "ctx.ghost_variable = pop(stack, 1, ctx=ctx)", indent
            )
        elif token.value[0] == "_":
            return indent_str(
                f"ctx.VAR_{token.value} = pop(stack, 1, ctx)", indent
            )
        else:
            return indent_str(
                f"VAR_{token.value} = pop(stack, 1, ctx=ctx)",
                indent,
            )
    elif token.name == TokenType.CODEPAGE_NUMBER:
        return indent_str(
            f"stack.append({encoding.codepage.find(token.value) + 101})", indent
        )
    elif token.name == TokenType.CHARACTER:
        return indent_str(f"stack.append({token.value!r})", indent)
    raise ValueError(f"Bad token: {token}")


def transpile_structure(
    struct: vyxal.structure.Structure,
    indent: int,
    options: TranspilationOptions,
) -> str:
    """Transpile a single vyxal.structure."""
    if isinstance(struct, vyxal.structure.GenericStatement):
        return transpile_single(struct.branches[0][0], indent, options=options)
    if isinstance(struct, vyxal.structure.IfStatement):
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
                res += transpile_ast(cond, new_indent, options=options)

            res += indent_str("condition = pop(stack, 1, ctx=ctx)", new_indent)
            res += indent_str("if boolify(condition, ctx):", new_indent)
            res += transpile_ast(body, new_indent + 1, options=options)

        # There's an extra else body at the end
        if len(struct.branches) % 2 == 0:
            body = struct.branches[-1]
            res += indent_str("else:", new_indent)
            res += transpile_ast(body, new_indent + 1, options=options)

        return res
    if isinstance(struct, vyxal.structure.ForLoop):
        # TODO (user/cgccuser) make it work with multiple variables
        var = (
            struct.names[0] if struct.names else f"LOOP{secrets.token_hex(16)}"
        )
        var = re.sub("[^A-Za-z0-9_]", "", var)
        var = f"VAR_{var}"
        if var == "VAR_":
            var = "ctx.ghost_variable"
        return (
            indent_str(
                f"for {var} in iterable(pop(stack, 1, ctx=ctx), range, ctx):",
                indent,
            )
            + indent_str(f"    ctx.context_values.append({var})", indent)
            + transpile_ast(struct.body, indent + 1, options=options)
            + indent_str("    ctx.context_values.pop()", indent)
        )
    if isinstance(struct, vyxal.structure.WhileLoop):
        return (
            transpile_ast(struct.condition, indent, options=options)
            + indent_str("condition = pop(stack, 1, ctx=ctx)", indent)
            + indent_str("counter = ctx.range_start", indent)
            + indent_str("while boolify(condition, ctx):", indent)
            + indent_str("    ctx.context_values.append(condition)", indent)
            + indent_str("    ctx.context_values.append(counter)", indent)
            + transpile_ast(struct.body, indent + 1, options=options)
            + indent_str("    ctx.context_values.pop()", indent)
            + transpile_ast(struct.condition, indent + 1, options=options)
            + indent_str("    condition = pop(stack, 1, ctx=ctx)", indent)
            + indent_str("    counter += 1", indent)
        )
    if isinstance(struct, vyxal.structure.FunctionCall):
        var = re.sub("[^A-Za-z0-9_]", "", struct.name)

        return indent_str(
            f"stack += VAR_{var}(stack, self=None, ctx=ctx)", indent
        )

    if isinstance(struct, vyxal.structure.FunctionDef):
        parameter_total = 0
        function_parameters = ""
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
                    f"VAR_{re.sub('[^A-z0-9_]', '', parameter)} ="
                    + "pop(arg_stack, 1, ctx=ctx)\n"
                )

        var = re.sub("[^A-Za-z0-9_]", "", struct.name)
        return (
            indent_str(
                f"def VAR_{var}(arg_stack, self, arity=-1, ctx=None):",
                indent,
            )
            + indent_str("parameters = []", indent + 1)
            + indent_str(function_parameters, indent + 1)
            + indent_str("stack = parameters[::]", indent + 1)
            + indent_str(
                "ctx.context_values.append(parameters[::])", indent + 1
            )
            + indent_str("ctx.stacks.append(stack)", indent + 1)
            + indent_str("ctx.inputs.append([parameters[::-1], 0])", indent + 1)
            + indent_str(f"this = VAR_{var}", indent + 1)
            + indent_str(
                transpile_ast(struct.body, options=options),
                indent + 1,
            )
            + indent_str("ctx.context_values.pop()", indent + 1)
            + indent_str("ctx.inputs.pop()", indent + 1)
            + indent_str("ctx.stacks.pop()", indent + 1)
            + indent_str("return stack", indent + 1)
        )
    if isinstance(struct, vyxal.structure.LambdaZip):
        return (
            indent_str(
                "rhs, lhs = pop(stack, 2, ctx); stack.append(vy_zip(lhs, rhs, ctx=ctx))",
                indent,
            )
            + transpile_lambda(
                struct.lam,
                indent,
                options=options,
                extra="lhs = pop(stack, 1, ctx); stack += iterable(lhs, ctx=ctx)",
            )
            + transpile_token(
                Token(TokenType.GENERAL, struct.after),
                indent,
                options=options,
            )
        )
    if isinstance(struct, vyxal.structure.Lambda):
        return transpile_lambda(struct, indent, options=options)
    if isinstance(struct, vyxal.structure.LambdaOp):
        return transpile_lambda(
            struct.lam, indent, options=options
        ) + transpile_token(
            Token(TokenType.GENERAL, struct.after),
            indent,
            options=options,
        )

    if isinstance(struct, vyxal.structure.ListLiteral):
        # We have to manually build this because we don't know how
        # many list items there will be.

        temp = indent_str("temp_list = []", indent)
        for x in struct.items:
            if x:
                temp += (
                    indent_str("def list_item(s, ctx):", indent)
                    + indent_str("stack = list(deep_copy(s))", indent + 1)
                    + transpile_ast(x, indent + 1, options=options)
                    + indent_str("if len(stack) == 0: return", indent + 1)
                    + indent_str("return pop(stack, 1, ctx=ctx)", indent + 1)
                    + indent_str("f = list_item(stack, ctx)", indent)
                    + indent_str(
                        "if f is not None: temp_list.append(f)", indent
                    )
                )

        temp += indent_str("stack.append(list(deep_copy(temp_list)))", indent)
        return temp

    if isinstance(struct, vyxal.structure.MonadicModifier):
        element_A = transpile_ast(
            [lambda_wrap([struct.function_A])],
            indent,
            options=options,
        )

        eager = isinstance(
            struct.function_A,
            (vyxal.structure.RecurseStatement, vyxal.structure.BreakStatement),
        )
        return (
            element_A
            + "\n"
            + indent_str("eager = " + str(eager), indent)
            + indent_str("function_A = pop(stack, 1, ctx)", indent)
            + indent_str(modifiers.get(struct.modifier, "pass"), indent)
        )

    if isinstance(struct, vyxal.structure.DyadicModifier):
        element_A = transpile_ast(
            [lambda_wrap([struct.function_A])],
            indent,
            options=options,
        )
        element_B = transpile_ast(
            [lambda_wrap([struct.function_B])],
            indent,
            options=options,
        )
        return (
            element_A
            + "\n"
            + indent_str("function_A = pop(stack, 1, ctx)", indent)
            + element_B
            + "\n"
            + indent_str("function_B = pop(stack, 1, ctx)", indent)
            + indent_str(modifiers.get(struct.modifier, "pass"), indent)
        )
    if isinstance(struct, vyxal.structure.TriadicModifier):
        element_A = transpile_ast(
            [lambda_wrap([struct.function_A])],
            indent,
            options=options,
        )
        element_B = transpile_ast(
            [lambda_wrap([struct.function_B])],
            indent,
            options=options,
        )
        element_C = transpile_ast(
            [lambda_wrap([struct.function_C])],
            indent,
            options=options,
        )
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
            + indent_str(modifiers.get(struct.modifier, "pass"), indent)
        )

    if isinstance(struct, vyxal.structure.BreakStatement):
        if struct.parent_structure == vyxal.structure.IfStatement:
            return indent_str("pass", indent)
        elif struct.parent_structure in (
            vyxal.structure.ForLoop,
            vyxal.structure.WhileLoop,
        ):
            return indent_str("break", indent)
        elif struct.parent_structure == vyxal.structure.FunctionDef:
            return (
                indent_str("ctx.inputs.pop()", indent)
                + indent_str("ctx.context_values.pop()", indent)
                + indent_str("return stack", indent)
            )
        elif struct.parent_structure == vyxal.structure.Lambda:
            return (
                indent_str("ret = [pop(stack, 1, ctx=ctx)]", indent)
                + indent_str("ctx.context_values.pop()", indent)
                + indent_str("ctx.inputs.pop()", indent)
                + indent_str("return ret", indent)
            )
        else:
            return indent_str("pass", indent)
    if isinstance(struct, vyxal.structure.RecurseStatement):
        if struct.parent_structure == vyxal.structure.IfStatement:
            return indent_str("pass", indent)
        elif struct.parent_structure in (
            vyxal.structure.ForLoop,
            vyxal.structure.WhileLoop,
        ):
            return indent_str("continue", indent)
        elif struct.parent_structure == vyxal.structure.FunctionDef:
            return indent_str(
                "stack.append(this(stack, this, ctx=ctx))", indent
            )
        elif struct.parent_structure == vyxal.structure.Lambda:
            return indent_str("stack += this(stack, this, ctx=ctx)", indent)
        elif struct.parent_structure in (
            vyxal.structure.MonadicModifier,
            vyxal.structure.DyadicModifier,
            vyxal.structure.TriadicModifier,
        ):
            return indent_str(
                "stack += ctx.function_stack[-2](stack, ctx.function_stack[-2],"
                " ctx=ctx)\n",
                indent,
            )
        else:
            return indent_str("vy_print(stack, ctx=ctx)", indent)

    raise ValueError(f"Structure {struct} was not of the right kind")


def transpile_lambda(
    lam: vyxal.structure.Lambda,
    indent: int,
    options: TranspilationOptions,
    extra: str = "",
):
    id_ = secrets.token_hex(16)
    # The lambda id used to be based on time.time() until
    # I realised just how useless that was, because the calls to
    # time.time() happened within only a few milliseconds of each
    # other, meaning int(time.time()) would return the exact same
    # lambda name for multiple lambdas.

    return (
        indent_str(
            f"def _lambda_{id_}(arg_stack, self, arity=None, ctx=None):",
            indent,
        )
        + indent_str(
            "if 'stored_arity' in dir(self): "
            + "stack = wrapify(arg_stack, self.stored_arity, ctx)",
            indent + 1,
        )
        + indent_str(
            "elif arity is not None: stack = wrapify(arg_stack, arity, "
            + "ctx=ctx) if arity != -1 else arg_stack[-1]",
            indent + 1,
        )
        + indent_str(
            "else: stack = wrapify(arg_stack, "
            + (
                str(lam.arity)
                if lam.arity != "default"
                else "ctx.default_arity"
            )
            + ", ctx)"
            if lam.arity != -1
            else "else: stack = arg_stack[-1]",
            indent + 1,
        )
        + indent_str(
            "this = self;",
            indent + 1,
        )
        + indent_str("ctx.function_stack.append(this)", indent + 1)
        + indent_str(
            "ctx.context_values.append(list(deep_copy(stack)) "
            "if len(stack) != 1 else deep_copy(stack[0]))",
            indent + 1,
        )
        + indent_str(
            "ctx.inputs.append([list(deep_copy(stack))[::-1], 0]);",
            indent + 1,
        )
        + indent_str("ctx.stacks.append(stack);", indent + 1)
        + (extra and indent_str(extra, indent + 1))
        + indent_str(
            transpile_ast(lam.body, options=options),
            indent + 1,
        )
        + indent_str(
            "if arity is not None: res = [pop(stack, 1, ctx=ctx)] if arity != -1"
            " else [stack]",
            indent + 1,
        )
        + indent_str(
            "elif 'stored_arity' in dir(self): res = [pop(stack, 1, ctx=ctx)] "
            "if self.stored_arity != -1 else [stack]",
            indent + 1,
        )
        + indent_str(
            "else: res = [pop(stack, 1, ctx=ctx)]"
            if lam.arity != -1
            else "else: res = [stack]",
            indent + 1,
        )
        + indent_str("ctx.context_values.pop()", indent + 1)
        + indent_str("ctx.inputs.pop()", indent + 1)
        + indent_str("ctx.stacks.pop()", indent + 1)
        + indent_str("ctx.function_stack.pop()", indent + 1)
        + indent_str("return res", indent + 1)
        + indent_str(
            f"_lambda_{id_}.arity = "
            + (
                str(lam.arity)
                if lam.arity != "default"
                else "ctx.default_arity"
            ),
            indent,
        )
        + indent_str(f"stack.append(_lambda_{id_})", indent)
    )
