"""The main interface to the project - you run this file to run Vyxal programs
offline.

"""

from vyxal.context import Context
from vyxal.transpile import transpile
from vyxal.elements import *

ctx = Context()

if __name__ == "__main__":
    # I'm allowed to have this here this time. Frick you if you say I
    # can't.

    # TODO:    Flag handling.
    # Also,    file handling.
    # Summary: cli handling.

    stack = []

    # This is called if a file isn't given, just like it used to.
    ctx.inputs = [[[0], 0]]
    while True:
        # Vyxal REPL ftw
        line = input("   ")
        line = transpile(line)
        stack = []
        print(line)
        exec(line)
        print(stack)
