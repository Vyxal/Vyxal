"""For testing flags"""

import multiprocessing
import os
import sys
import sympy

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

from vyxal.main import execute_vyxal


def get_output_object():
    return {1: ""}


def run_vyxal(code, inputs, flags):
    """Run Vyxal with the given code, inputs and flags"""
    ret = get_output_object()
    execute_vyxal(code, flags + "e", "\n".join(map(repr, inputs)), ret, True)
    return ret[1][:-1]



def test_A_flag():
   """Test the A flag"""

    res = run_vyxal("L", ["abc\ndef\nghi", "gaming\nmore\n\nmoremore"], "A")
    expected = """'abc\ndef\nghi' => 11
'gaming\nmore\n\nmoremore' => 21"""

    assert res == expected

    res = run_vyxal("¶o²⁋=", ["foo\nbar\nbaz"], "A")
    expected = """'foo\nbar\nbaz' => 1"""

    assert res == expected


def test_H_flag():
    """Test the H flag"""

    res = run_vyxal("", [], "H")
    expected = "100"

    assert res == expected


def test_j_flag():
    """Test the j flag"""

    res = run_vyxal("1 2 3 4 5W", [], "j")
    expected = """1
2
3
4
5"""

    assert res == expected


def test_W_flag():
    """Test the W flag"""

    res = run_vyxal("1 2 3 4 5", [], "W")
    expected = """⟨ 1 | 2 | 3 | 4 | 5 ⟩"""

    assert res == expected
