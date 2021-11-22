"""The main interface to the project - you run this file to run Vyxal programs
offline.

"""

import types

import sys
from vyxal.context import Context
from vyxal.elements import *
from vyxal.helpers import simplify
from vyxal.transpile import transpile
from vyxal.parse import *
from vyxal import encoding

if __name__ == "__main__":
    # I'm allowed to have this here this time. Frick you if you say I
    # can't.

    # TODO:    Flag handling.
    # Also,    file handling.
    # Summary: cli handling.

    ctx = Context()
    stack = []

    if len(sys.argv) > 1:
        file_name = sys.argv[1]
        flags = ""
        inputs = []

        if len(sys.argv) > 2:
            flags, inputs = sys.argv[2], sys.argv[3:]

        code = ""

        # Handle file opening flags

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
        if "a" in flags:
            inputs = [inputs]
        elif "f" in flags:
            with open(inputs[0], "r", encoding="utf-8") as f:
                inputs = f.readlines()

        if "Ṡ" in flags:
            inputs = list(map(str, inputs))
        else:
            inputs = list(map(vy_eval, inputs))

        if "H" in flags:
            stack = [100]
        else:
            stack = []

        ctx = Context()
        ctx.inputs[0][0] = inputs
        ctx.stacks.append(stack)

        # TODO: Handle dictionary flags

        code = transpile(code)
        ctx.stacks.append(stack)
        exec(code)
        if not ctx.printed:
            vy_print(stack[-1])
    else:
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
