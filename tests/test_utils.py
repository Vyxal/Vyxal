import os
import sys
import builtins
from multiprocessing import Manager

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

from vyxal import interpreter as interp

header = "stack = []\nregister = 0\nprinted = False\n"
manager = Manager()


def run_code(code, flags="", input_list=[], output_variable=manager.dict()):
    reset_globals()
    # context_level = 0
    interp.execute(code, flags, "\n".join(map(str, input_list)), output_variable)
    return interp.stack


def reset_globals():
    interp.keg_mode = False
    interp.raw_strings = False
    interp.online_version = False
    interp.input_level = 0
    interp.number_iterable = list
    interp.MAP_START = 1
    interp.MAP_OFFSET = 1
    interp._join = False
    interp._vertical_join = False
    interp.use_encoding = False
    interp.stack = []


def reshape(arr, shape):
    if len(shape) == 1:
        return arr
    rest = shape[1:]
    size = len(arr) // shape[0]
    return [reshape(arr[i * size : (i + 1) * size], rest) for i in range(shape[0])]


def to_list(vector):
    typ = interp.vy_type(vector)
    if typ in (list, interp.Generator):
        return list(
            map(to_list, vector._dereference() if typ is interp.Generator else vector)
        )
    return vector
