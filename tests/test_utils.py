import os
import sys
import builtins
from multiprocessing import Manager

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

import vyxal.interpreter

header = "stack = []\nregister = 0\nprinted = False\n"
manager = Manager()


def run_code(code, flags="", input_list=[], output_variable=manager.dict()):
    reset_globals()
    # context_level = 0
    vyxal.interpreter.execute(code, flags, "\n".join(
        map(str, input_list)), output_variable)
    return vyxal.interpreter.stack


def reset_globals():
    vyxal.interpreter.keg_mode = False
    vyxal.interpreter.raw_strings = False
    vyxal.interpreter.online_version = False
    vyxal.interpreter.input_level = 0
    vyxal.interpreter.number_iterable = list
    vyxal.interpreter.MAP_START = 1
    vyxal.interpreter.MAP_OFFSET = 1
    vyxal.interpreter._join = False
    vyxal.interpreter._vertical_join = False
    vyxal.interpreter.use_encoding = False
    vyxal.interpreter.stack = []


def reshape(arr, shape):
    if len(shape) == 1:
        return arr
    rest = shape[1:]
    size = len(arr) // shape[0]
    return [reshape(arr[i * size:(i + 1) * size], rest) for i in range(shape[0])]


def to_list(vector):
    typ = vyxal.interpreter.VY_type(vector)
    if typ in (list, vyxal.interpreter.Generator):
        return list(
            map(
                to_list,
                vector._dereference() if typ is vyxal.interpreter.Generator else vector
            )
        )
    return vector
