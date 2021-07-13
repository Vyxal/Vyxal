import os
import sys

# Pipped modules

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

# Execution variables
context_level = 0
context_values = [0]
global_stack = []
input_level = 0
inputs = []
input_values = {0: [inputs, 0]}  # input_level: [source, input_index]
last_popped = []
keg_mode = False
number_iterable = list
raw_strings = False
online_version = False
output = ""
printed = False
register = 0
retain_items = False
reverse_args = False
safe_mode = False  # You may want to have safe evaluation but not be online.
stack = []
variables_are_digraphs = False

MAP_START = 1
MAP_OFFSET = 1
_join = False
_vertical_join = False
use_encoding = False


def this_function(x):
    from vyxal.builtins import vy_print

    vy_print(stack)
    return x


def set_globals(flags):
    global stack, inputs, MAP_START, MAP_OFFSET, _join
    global _vertical_join, use_encoding, reverse_args, keg_mode, safe_mode
    global number_iterable, raw_strings

    if "H" in flags:
        stack = [100]

    if "a" in flags:
        inputs = [inputs]

    if "M" in flags:
        MAP_START = 0

    if "m" in flags:
        MAP_OFFSET = 0

    if "Ṁ" in flags:
        MAP_START = 0
        MAP_OFFSET = 0

    if "R" in flags:
        number_iterable = range

    _join = "j" in flags
    _vertical_join = "L" in flags
    use_encoding = "v" in flags
    reverse_args = "r" in flags
    keg_mode = "K" in flags
    safe_mode = "E" in flags
    raw_strings = "D" in flags
