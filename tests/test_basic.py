# Simple tests

import os, sys
THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)
from Vyxal import *

header = "stack = []\nregister = 0\nprinted = False\n"

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

def test_is_square():
    stack = run_code("1000'∆²;J")
    assert pop(stack) == [0, 1, 4, 9, 16, 25, 36, 49, 64, 81, 100, 121, 144, 169, 196, 225, 256, 289, 324, 361, 400, 441, 484, 529, 576, 625, 676, 729, 784, 841, 900, 961]


test_is_square()