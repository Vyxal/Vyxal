from vyxal.utilities import vy_print

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
this_function = lambda x: vy_print(stack) or x
variables_are_digraphs = False

MAP_START = 1
MAP_OFFSET = 1
_join = False
_vertical_join = False
use_encoding = False