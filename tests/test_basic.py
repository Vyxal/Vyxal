# Simple tests

import os, sys
from multiprocessing import Manager

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)
import Vyxal
from Vyxal import *

header = "stack = []\nregister = 0\nprinted = False\n"
manager = Manager()


def run_code(code, flags="", input_list=[], output_variable=manager.dict()):
    global _join, _vertical_join, use_encoding, input_level, online_version, raw_strings, number_iterable, MAP_START, MAP_OFFSET
    keg_mode = False
    raw_strings = False
    online_version = False
    input_level = 0
    number_iterable = list
    MAP_START = 1
    MAP_OFFSET = 1
    Vyxal._join = False
    _join = False
    _vertical_join = False
    use_encoding = False
    # context_level = 0
    execute(code, flags, "\n".join(input_list), output_variable)
    Vyxal._join = False
    _join = False
    return stack


# This is just a dummy test, it's not feasible to write multiple tests for every single
# overload of every single command
def test_not():
    stack = run_code("2¬")
    assert pop(stack) == 0


def test_is_prime():
    stack = run_code("10ɾƛæ;")
    assert pop(stack)._dereference() == [0, 1, 1, 0, 1, 0, 1, 0, 0, 0]
    

def test_is_square():
    stack = run_code("1000'∆²;")
    f = pop(stack)._dereference()
    assert f == [
        1,
        4,
        9,
        16,
        25,
        36,
        49,
        64,
        81,
        100,
        121,
        144,
        169,
        196,
        225,
        256,
        289,
        324,
        361,
        400,
        441,
        484,
        529,
        576,
        625,
        676,
        729,
        784,
        841,
        900,
        961,
    ]


# from from https://codegolf.stackexchange.com/a/210307
fizzbuzz_output = [
    1,
    2,
    "Fizz",
    4,
    "Buzz",
    "Fizz",
    7,
    8,
    "Fizz",
    "Buzz",
    11,
    "Fizz",
    13,
    14,
    "FizzBuzz",
    16,
    17,
    "Fizz",
    19,
    "Buzz",
    "Fizz",
    22,
    23,
    "Fizz",
    "Buzz",
    26,
    "Fizz",
    28,
    29,
    "FizzBuzz",
    31,
    32,
    "Fizz",
    34,
    "Buzz",
    "Fizz",
    37,
    38,
    "Fizz",
    "Buzz",
    41,
    "Fizz",
    43,
    44,
    "FizzBuzz",
    46,
    47,
    "Fizz",
    49,
    "Buzz",
    "Fizz",
    52,
    53,
    "Fizz",
    "Buzz",
    56,
    "Fizz",
    58,
    59,
    "FizzBuzz",
    61,
    62,
    "Fizz",
    64,
    "Buzz",
    "Fizz",
    67,
    68,
    "Fizz",
    "Buzz",
    71,
    "Fizz",
    73,
    74,
    "FizzBuzz",
    76,
    77,
    "Fizz",
    79,
    "Buzz",
    "Fizz",
    82,
    83,
    "Fizz",
    "Buzz",
    86,
    "Fizz",
    88,
    89,
    "FizzBuzz",
    91,
    92,
    "Fizz",
    94,
    "Buzz",
    "Fizz",
    97,
    98,
    "Fizz",
    "Buzz",
]


def test_fizzbuzz():
    stack = run_code("₁ƛ₍₃₅kF½*ṅ⟇", flags=["j"])
    f = pop(stack)
    assert f == fizzbuzz_output


trailing_zero_testcases = [
    ["512", "2", 9],
    ["248", "2", 3],
    ["364", "265", 0],
    ["764", "2", 2],
    ["336", "284", 0],
    ["517", "422", 0],
    ["554", "37", 0],
    ["972", "3", 5],
    ["12", "6", 1],
    ["72", "2", 3],
    ["44", "2", 2],
    ["51", "16", 0],
    ["32", "2", 5],
    ["56", "7", 1],
    ["60", "2", 2],
    ["8", "3", 0],
    ["18", "3", 2],
    ["107", "43", 0],
]
# from https://codegolf.stackexchange.com/a/224288
def test_trailing_zeroes():
    for [n, b, out] in trailing_zero_testcases:
        stack = run_code("Ǒ", flags="j", input_list=[b, n])
        f = pop(stack)
        assert f == out
        
def test_quit():
    global print, _join
    trip = None
    assert not _join
    def print(first, *args):
        nonlocal trip
        trip = first
    run_code("69 Q", flags="o")
    assert trip != None
    def print(first, *args):
        raise Error("Shouldn't print anything")
    run_code("69 Q")
    run_code("69 Q", flags="O")
    run_code("69 Q")
