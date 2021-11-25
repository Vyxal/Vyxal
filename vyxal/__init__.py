"""The main interface to the project - you run this file to run Vyxal programs
offline.

"""

import sys
import types

from vyxal import encoding
from vyxal.context import Context
from vyxal.elements import *
from vyxal.transpile import transpile


def execute_vyxal(file_name, flags, inputs, output_var=None, online_mode=False):
    ctx = Context()
    stack = []
    ctx.online_output = output_var
    ctx.online = online_mode

    if online_mode:
        inputs = inputs.split("\n")  # have to do this here because file writing

    # Handle input handling flags
    if "h" in flags:  # Help flag
        print("Help message")
        exit(0)

    if "e" in flags:  # Program is file name
        code = file_name
    elif "v" in flags:  # Open file using Vyxal encoding
        with open(file_name, "rb") as f:
            code = f.read()
            code = encoding.vyxal_to_utf8(code)
    else:  # Open file using UTF-8 encoding:
        with open(file_name, "r", encoding="utf-8") as f:
            code = f.read()

    # Handle input handling flags
    if "a" in flags:  # All inputs as array
        inputs = [inputs]
    elif "f" in flags:  # Read inputs from file
        with open(inputs[0], "r", encoding="utf-8") as f:
            inputs = f.readlines()

    if "Ṡ" in flags:  # All inputs as strings
        inputs = list(map(str, inputs))
    else:
        inputs = list(map(lambda x: vy_eval(x, ctx), inputs))

    if "H" in flags:  # Pre-initalise stack to 100
        stack = [100]
    else:
        stack = []

    ctx.inputs[0][0] = inputs
    ctx.stacks.append(stack)

    # Handle runtime flags

    if "Ṁ" in flags:  # Implicit ranges are [0, n)
        ctx.range_start = 0
        ctx.range_end = 0

    elif "M" in flags:  # Implicit ranges are [0, n]
        ctx.range_start = 0

    elif "m" in flags:  # Implicit ranges are [1, n)
        ctx.range_end = 0

    ctx.reverse_flag = "r" in flags
    ctx.number_as_range = "R" in flags
    ctx.dictionary_compression = not "D" in flags
    ctx.variable_length_1 = "V" in flags
    ctx.truthy_lists = "t" in flags  # L431 in elements.py
    ctx.vyxal_lists = not "P" in flags

    if "2" in flags:
        ctx.default_arity = 2
    elif "3" in flags:
        ctx.default_arity = 3
    else:
        ctx.default_arity = 1

    code = transpile(code, ctx.dictionary_compression)

    if "c" in flags:  # Show transpiled code
        vy_print(code + "\n", ctx=ctx)

    ctx.stacks.append(stack)
    exec(code)
    if not (ctx.printed or "O" in flags) or "o" in flags:
        output = pop(stack, 1, ctx)
        for flag in flags:
            if flag == "j":
                output = join(output, "\n", ctx)
            elif flag == "s":
                output = vy_sum(output, ctx)
            elif flag == "d":
                output = vy_sum(deep_flatten(output, ctx), ctx)
            elif flag == "Ṫ":
                output = vy_sum(stack, ctx)
                stack = [output]
            elif flag == "L":
                output = vertical_join(output, ctx=ctx)
            elif flag == "S":
                output = join(output, " ", ctx)
            elif flag == "C":
                output = center(output, ctx)
                output = join(output, "\n", ctx)
            elif flag == "G":
                output = monadic_maximum(output, ctx)
            elif flag == "g":
                output = monadic_minimum(output, ctx)
            elif flag == "W":
                output = vy_str(stack, ctx)
            elif flag == "ṡ":
                output = join(stack, " ", ctx)
            elif flag == "J":
                output = join(stack, "\n", ctx)
            elif flag == "…":
                if vy_type(output, simple=True) is list:
                    output = output[:100]
            else:
                pass
        vy_print(output, ctx=ctx)


def repl():
    ctx, stack = Context(), []
    # This is called if a file isn't given, just like it used to.
    ctx.repl_mode = True
    while True:
        # Vyxal REPL ftw
        line = transpile(input(">>> "))
        stack = []
        ctx.stacks.append(stack)  # Finally, a use case for assignment by
        # reference. Never thought I'd fine a time
        # when it wouldn't be an actual pain.
        print(line)
        exec(line)

        res = []
        while len(stack):
            top = stack.pop()
            if isinstance(top, types.FunctionType):
                res.append(top(stack, top, ctx=ctx)[-1])
            else:
                res.append(top)
        res = res[::-1]

        vy_print(res, ctx=ctx)
        ctx.stacks.pop()


if __name__ == "__main__":

    if len(sys.argv) > 1:
        file_name = sys.argv[1]
        flags = ""
        inputs = []

        if len(sys.argv) > 2:
            flags, inputs = sys.argv[2], sys.argv[3:]

        code = ""

        execute_vyxal(code, flags, inputs)

    else:
        repl()
