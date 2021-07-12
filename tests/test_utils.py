import os
import sys
from multiprocessing import Manager

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

from vyxal import interpreter as interp
from vyxal import vy_globals

header = "stack = []\nregister = 0\nprinted = False\n"
manager = Manager()


def run_code(code, flags="", input_list=[], output_variable=manager.dict()):
    reset_globals()
    interp.execute(code, flags, "\n".join(map(str, input_list)), output_variable)
    return vy_globals.stack


def reset_globals():
    vy_globals.stack = []
    interp.stack = []
    vy_globals.context_level = 0
    vy_globals.context_values = [0]
    vy_globals.global_stack = []
    vy_globals.input_level = 0
    vy_globals.inputs = []
    vy_globals.input_values = {0: [vy_globals.inputs, 0]}  # input_level: [source, input_index]
    vy_globals.last_popped = []
    vy_globals.keg_mode = False
    vy_globals.number_iterable = list
    vy_globals.raw_strings = False
    vy_globals.online_version = False
    vy_globals.output = ""
    vy_globals.printed = False
    vy_globals.register = 0
    vy_globals.retain_items = False
    vy_globals.reverse_args = False
    vy_globals.safe_mode = False  # You may want to have safe evaluation but not be online.
    vy_globals.stack = []
    vy_globals.variables_are_digraphs = False

    vy_globals.MAP_START = 1
    vy_globals.MAP_OFFSET = 1
    vy_globals._join = False
    vy_globals._vertical_join = False
    vy_globals.use_encoding = False
    vy_globals.set_globals("")


def reshape(arr, shape):
    if len(shape) == 1:
        return arr
    rest = shape[1:]
    size = len(arr) // shape[0]
    return [reshape(arr[i * size: (i + 1) * size], rest) for i in range(shape[0])]


def to_list(vector):
    typ = interp.vy_type(vector)
    if typ in (list, interp.Generator):
        return list(
            map(to_list, vector._dereference() if typ is interp.Generator else vector)
        )
    return vector
