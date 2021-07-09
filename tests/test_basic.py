# Simple tests

from test_utils import *
import vyxal.interpreter
import os
import sys
import builtins
from multiprocessing import Manager

# THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
# sys.path.insert(1, THIS_FOLDER)


# This is just a dummy test, it's not feasible to write multiple tests for every single
# overload of every single command
def test_not():
    stack = run_code("2¬")
    assert vyxal.interpreter.pop(stack) == 0


def test_is_prime():
    stack = run_code("10ɾƛæ;")
    assert vyxal.interpreter.pop(stack)._dereference() == [0, 1, 1, 0, 1, 0, 1, 0, 0, 0]


def test_is_square():
    stack = run_code("1000'∆²;")
    res = vyxal.interpreter.pop(stack)._dereference()
    assert res == [
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


def test_trailing_zeroes():
    """
    From https://codegolf.stackexchange.com/a/224288
    Test the command to find number of trailing zeroes in a base
    """
    for [num, base, expected] in trailing_zero_testcases:
        stack = run_code("Ǒ", input_list=[base, num])
        print(num, base, expected, stack)
        assert stack == [expected]


def test_quit():
    real_print = vyxal.interpreter.vy_print

    def shouldnt_print(first, *args):
        raise ValueError("Shouldn't print anything")

    vyxal.interpreter.vy_print = shouldnt_print
    run_code("69 Q")
    run_code("69 Q", flags="O")
    trip = []

    def should_print(first, *args):
        nonlocal trip
        trip.append(first)

    vyxal.interpreter.vy_print = should_print
    run_code("69 Q", flags="o")
    assert trip
    vyxal.interpreter.vy_print = real_print

'''
def test_foldl_rows():
    tests = [
        (list(range(1, 6)), 'λ*;', 720),
        (reshape(list(range(12)), [3, 4]), 'λ-;', [-6, -14, -22]),
        (reshape(list(range(37)), [3, 3, 4]), 'λ+;',
         [[6, 22, 38],  [54, 70, 86],  [102, 118, 134]])
    ]
    for input_array, fn, expected in tests:
        stack = run_code(fn + "ÞR", input_list=[input_array])
        assert vyxal.interpreter.pop(stack) == expected


def test_foldl_cols():
    #todo add more complicated test cases
    tests = [
        (reshape(list(range(1, 10)), [3, 3]), 'λ+;', [12, 15, 18]),
        (reshape(list(range(12)), [3, 4]), 'λ-;', [-12, -13, -14, -15]),
        (reshape(list(range(36)), [3, 3, 4]), 'λ-;',
         [[-12, -13, -14, -15], [-24, -25, -26, -27], [-36, -37, -38, -39]])
    ]
    for input_array, fn, expected in tests:
        stack = run_code(fn + "ÞC", input_list=[input_array])
        assert to_list(vyxal.interpreter.pop(stack)) == expected
'''
