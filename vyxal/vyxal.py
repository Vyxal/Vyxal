"""The main interface to the project - you run this file to run Vyxal programs
offline.
"""

from vyxal.context import Context
from vyxal.transpile import transpile

ctx = Context()

if __name__ == "__main__":
    # I'm allowed to have this here this time. Frick you if you say I
    # can't.

    # TODO:    Flag handling.
    # Also,    file handling.
    # Summary: cli handling.

    # This is called if a file isn't given, just like it used to.
    while True:
        # Vyxal REPL ftw
        line = input("   ")
        line = transpile(line)
        print(line)
