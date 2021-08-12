"""
File: vyxal.py
Description: This is the main file for the project - the transpilation
of Vyxal programs actually happens here, and this is what gets executed
offline.
"""

from transpile import transpile

if __name__ == "__main__":
    # I'm allowed to have this here this time. Frick you if you say I
    # can't.

    while True:
        # Vyxal REPL ftw
        line = input("   ")
        line = transpile(line)
        print(line)
