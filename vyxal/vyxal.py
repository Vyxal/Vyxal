"""The main interface to the project - you run this file to run Vyxal programs
offline.

"""

from vyxal.context import Context
from vyxal.elements import *
from vyxal.transpile import transpile
from vyxal.parse import *
from vyxal import lexer

if __name__ == "__main__":
    # I'm allowed to have this here this time. Frick you if you say I
    # can't.

    # TODO:    Flag handling.
    # Also,    file handling.
    # Summary: cli handling.

    ctx = Context()
    stack = []

    # This is called if a file isn't given, just like it used to.
    ctx.repl_mode = True
    while True:
        # Vyxal REPL ftw
        line = transpile(input(">>> "))
        stack = []
        ctx.stack = stack  # Finally, a use case for assignment by
        # reference. Never thought I'd fine a time
        # when it wouldn't be an actual pain.
        # print(line)
        exec(line)
        vy_print(stack, ctx=ctx)
