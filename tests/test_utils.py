import os
import sys
import builtins
from multiprocessing import Manager

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

import Vyxal

header = "stack = []\nregister = 0\nprinted = False\n"
manager = Manager()


def run_code(code, flags="", input_list=[], output_variable=manager.dict()):
    reset_globals()
    # context_level = 0
    Vyxal.execute(code, flags, "\n".join(
        map(str, input_list)), output_variable)
    return Vyxal.stack


def reset_globals():
    Vyxal.keg_mode = False
    Vyxal.raw_strings = False
    Vyxal.online_version = False
    Vyxal.input_level = 0
    Vyxal.number_iterable = list
    Vyxal.MAP_START = 1
    Vyxal.MAP_OFFSET = 1
    Vyxal._join = False
    Vyxal._vertical_join = False
    Vyxal.use_encoding = False
    Vyxal.stack = []


def reshape(arr, shape):
    if len(shape) == 1:
        return arr
    rest = shape[1:]
    size = len(arr) // shape[0]
    return [reshape(arr[i * size:(i + 1) * size], rest) for i in range(shape[0])]


def to_list(vector):
    typ = Vyxal.VY_type(vector)
    if typ in (list, Vyxal.Generator):
        return list(
            map(
                to_list,
                vector._dereference() if typ is Vyxal.Generator else vector
            )
        )
    return vector
