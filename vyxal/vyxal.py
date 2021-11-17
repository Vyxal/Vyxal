"""The main interface to the project - you run this file to run Vyxal programs
offline.

"""

import types

import sys
from vyxal.context import Context
from vyxal.elements import *
from vyxal.transpile import transpile
from vyxal.parse import *
from vyxal.LazyList import simplify
from vyxal import lexer

if __name__ == "__main__":
    # I'm allowed to have this here this time. Frick you if you say I
    # can't.

    # TODO:    Flag handling.
    # Also,    file handling.
    # Summary: cli handling.

    ctx = Context()
    stack = []

    if len(sys.argv) > 1:
        with open(sys.argv[1], "r", encoding="utf-8") as f:
            code = f.read()  # TODO: Allow for vyxal raw encoding.
            # That'll be done once flags are handled

        if len(sys.argv) > 2:
            flags = sys.argv[2]
            if len(sys.argv) > 3:
                ctx.inputs = list(map(lambda x: vy_eval(x, ctx), sys.argv[3:]))

        code = transpile(code)
        ctx.stacks.append(stack)
        exec(code)
        print(stack[-1])
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
