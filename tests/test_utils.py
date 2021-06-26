import os
import sys
import builtins
from multiprocessing import Manager

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

import Vyxal

manager = Manager()


def run_code(code, flags="", input_list=[], output_variable=manager.dict()):
    reset_globals()
    # context_level = 0
    Vyxal.execute(code, flags, "\n".join(input_list), output_variable)
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
