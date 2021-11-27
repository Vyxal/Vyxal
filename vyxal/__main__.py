import vyxal.main
import sys

if len(sys.argv) > 1:
    file_name = sys.argv[1]
    flags = ""
    inputs = []

    if len(sys.argv) > 2:
        flags, inputs = sys.argv[2], sys.argv[3:]

    code = ""

    vyxal.main.execute_vyxal(code, flags, inputs)
else:
    vyxal.main.repl()
