# Simple tests

from Vyxal import *

header = "stack = []\nregister = 0\nprinted = False"

def run_code(code):
    code = VY_compile(code, header)
    context_level = 0
    exec(code)
    return stack

# This is just a dummy test, it's not feasible to write multiple tests for every single
# overload of every single command
def test_not():
    stack = run_code("2¬")
	assert pop(stack) == 0
