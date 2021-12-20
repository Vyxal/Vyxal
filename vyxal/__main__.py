import sys

import vyxal.main

if len(sys.argv) > 1:
    file_name = sys.argv[1]
    flags = ""
    inputs = []

    if len(sys.argv) > 2:
        flags, inputs = sys.argv[2], sys.argv[3:]

    vyxal.main.execute_vyxal(file_name, flags, inputs)
else:
    vyxal.main.repl()
