"""
This file is for testing specific elements that can't go in elements.yaml
"""

import os
import sys
import sympy

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

from vyxal.transpile import *
from vyxal.elements import *
from vyxal.context import Context
from vyxal.helpers import *
from vyxal.LazyList import *


def run_vyxal(vy_code, inputs=[], *, debug=False):
    stack = list(map(vyxalify, inputs))
    ctx = Context()
    ctx.stacks.append(stack)

    py_code = transpile(vy_code)
    if debug:
        print(py_code)
    exec(py_code)

    ctx.stacks.pop()
    return stack


def test_all_slices_inf():
    stack = run_vyxal("⁽›1Ḟ 4 Þs")
    expected = [[1, 5, 9], [2, 6, 10], [3, 7, 11], [4, 8, 12]]
    assert [slice[:3] for slice in stack[-1][:4]] == expected


def test_combs_without_replace():
    stack = run_vyxal("Þp3ḋ")
    assert stack[-1][:4] == [[2, 3, 5], [2, 3, 7], [2, 5, 7], [3, 5, 7]]


def test_deltas():
    stack = run_vyxal("Þ∞ ¯")
    assert stack[-1][:4] == [1, 1, 1, 1]


def test_vertical_mirror():
    """Test øṁ"""
    # Join these on newlines into one string and check if the result
    # is as expected
    tests = [
        ("abc", "abccba"),
        ("aj38asd#f|", "aj38asd#f||f#dsa83ja"),
        ("ಠ_ಠ¯\\_(ツ)_/¯", "ಠ_ಠ¯\\_(ツ)_/¯¯/_)ツ(_\\¯ಠ_ಠ"),
        ("><>", "><>><>"),
    ]

    input_str = "\n".join(test[0] for test in tests)
    expected = "\n".join(test[1] for test in tests)

    stack = run_vyxal("øṁ", [input_str])

    actual = stack[-1]
    assert actual == expected


def test_sort_by():
    """Test µ"""

    stack = run_vyxal("314 µ;", [])
    assert stack[-1] == [1, 3, 4]

    stack = run_vyxal("59104 µ;", [])
    assert stack[-1] == [0, 1, 4, 5, 9]


def test_list_sort():
    stack = run_vyxal(
        "⟨ ⟨ 9 | 6 | 9 | 6 | 7 ⟩ | ⟨ 7 | 6 | 4 | 1 | 8 ⟩ | ⟨ 4 | 9 | 4 | 3 | 2 ⟩ | ⟨ 7 | 3 | 3 | 6 | 9 ⟩ | ⟨ 2 | 9 | 1 | 2 | 6 ⟩ ⟩ vs s"
    )
    assert simplify(stack[-1]) == [
        [1, 2, 2, 6, 9],
        [1, 4, 6, 7, 8],
        [2, 3, 4, 4, 9],
        [3, 3, 6, 7, 9],
        [6, 6, 7, 9, 9],
    ]


def test_cumulative_reduce():
    """Test ɖ"""

    stack = run_vyxal("12345 ɖ+")
    assert stack[-1] == [1, 3, 6, 10, 15]

    stack = run_vyxal("34212 ɖ-")
    assert stack[-1] == [3, -1, -3, -4, -6]


def test_map_lambda_as_element():
    """Test that a map lambda is held as a single element"""
    stack = run_vyxal("⁽ƛ1+;M", inputs=[[[1, 2], [3, 4]]])
    assert stack[-1] == [[2, 3], [4, 5]]


def test_vectorise_map_lambda():
    """Test that a map lambda can be vectorised"""
    stack = run_vyxal("vƛ30∴;", inputs=[[[34, 1324, 23], [45, 3]]])
    assert simplify(stack[-1]) == [[34, 1324, 30], [45, 30]]


def test_deep_flatten_inf_list():
    """Test that an infinite list can be fully flattened"""
    stack = run_vyxal("⁽› 1 5 r w Ḟ f")
    assert simplify(stack[:10][0][:10]) == [1, 2, 3, 4, 2, 3, 4, 5, 3, 4]


def test_overdot_X_function_overload():
    stack = run_vyxal("4λ2ḭ;Ẋ")
    assert stack[-1] == 0


def test_beheading_infinite_lists():
    stack = run_vyxal("⁽› 1 Ḟ Ḣ")
    assert stack[-1][0:5] == [2, 3, 4, 5, 6]


def test_equal_lazylists():
    assert LazyList(range(10)) == LazyList(range(10))


def test_group_consecutive_inf_lists():
    stack = run_vyxal("⁽› 1 Ḟ ½ ⌊ Ġ")
    assert stack[-1][:3] == [[0], [1, 1], [2, 2]]


def test_lessthan_lazylists():
    assert LazyList(range(10)) < LazyList(range(11))
    assert LazyList([4, 5, 6]) < LazyList([6, 7, 8])


def test_greaterthan_lazylists():
    assert LazyList([1, 2, 3]) > LazyList([1, 1])
    assert LazyList(range(11)) > LazyList(range(10))


def test_compare_infinite_lists():
    stack = run_vyxal("Þ∞")
    assert stack[-1] > LazyList([1, 2, 3, 4, 5, 6, 7, 8, 9, 10])
    assert LazyList([2, 3]) > stack[-1]


def test_infinite_list_sublists():
    stack = run_vyxal("⁽›1 Ḟ ÞS")
    assert stack[-1][:5] == [[1], [1, 2], [2], [1, 2, 3], [2, 3]]


def test_prefixes_of_infinite_lists():
    stack = run_vyxal("⁽›1Ḟ K")
    assert stack[-1][:3] == [[1], [1, 2], [1, 2, 3]]


def test_cartesian_product_infinite_lists():
    stack = run_vyxal("⁽›1Ḟ :Ẋ")
    assert stack[-1][:7] == [
        [1, 1],
        [1, 2],
        [2, 1],
        [1, 3],
        [2, 2],
        [3, 1],
        [1, 4],
    ]


def test_ath_infinite_lists():
    stack = run_vyxal("⁽›1Ḟ 4 Ḟ")
    assert stack[-1][:5] == [1, 5, 9, 13, 17]


def test_filter_infinite_lists():
    stack = run_vyxal("⁽›1Ḟ ⁽⇧1Ḟ 3 Ẏ F")
    assert stack[-1][:4] == [2, 4, 6, 7]


def test_all_equal_infinite_lists():
    stack = run_vyxal("Þ∞ ≈")
    assert stack[-1] == 0


def test_powerset_inf():
    stack = run_vyxal("⁽› 1 Ḟ ṗ", debug=True)
    assert stack[-1][:4] == [[], [1], [2], [1, 2]]


def test_shallow_flatten():
    stack = run_vyxal("⁽› 1 5rw Ḟ Þf", debug=True)
    assert stack[-1][:9] == [1, 2, 3, 4, 2, 3, 4, 5, 3]


def test_slice_to_end_infinite_lists():
    stack = run_vyxal("⁽›1Ḟ 20 ȯ")
    assert stack[-1][:5] == [21, 22, 23, 24, 25]


def test_strip_infinite_lists():
    """Ensure that P only strips from the start for infinite lists"""
    stack = run_vyxal("⁽›1Ḟ 1 9 r P")
    assert stack[-1][:4] == [9, 10, 11, 12]


def test_interleave():
    stack = run_vyxal("⁽›1Ḟ ⁽⇧1Ḟ Y")
    assert stack[-1][:6] == [1, 1, 2, 3, 3, 5]


def test_compressed_strings():
    stack = run_vyxal("«×Fṫ«")
    assert stack[-1] == "a hyb"


def test_to_base_digits():
    stack = to_base_digits(64, 2)
    assert stack == [1, 0, 0, 0, 0, 0, 0]


def test_wrap_inf():
    stack = run_vyxal("⁽› 1 Ḟ 3 ẇ")
    assert stack[-1][:3] == [[1, 2, 3], [4, 5, 6], [7, 8, 9]]


def test_apply_to_every_other_inf_list():
    stack = run_vyxal("⁽› 1 Ḟ ⁽› ẇ")
    assert stack[-1][:4] == [1, 3, 3, 5]


def test_max_by_function():
    stack = run_vyxal("`word wordier wordiest` ⌈⁽LÞ↑")
    assert stack[-1] == "wordiest"


def test_min_by_function():
    stack = run_vyxal("`word wordier wordiest` ⌈⁽LÞ↓")
    assert stack[-1] == "word"


def test_map_to_every_second_item():
    stack = run_vyxal("1 10r ‡›I ẇ")
    assert stack[-1] == [1, "   ", 3, "     ", 5, "       ", 7, "         ", 9]

    stack = run_vyxal("1 10r ‡›I Ẇ")
    assert stack[-1] == [
        "  ",
        2,
        "    ",
        4,
        "      ",
        6,
        "        ",
        8,
        "          ",
    ]


def test_bool_input():
    stack = run_vyxal("1+", inputs=[True])
    assert stack == ["True1"]


def test_tilde_monad():
    stack = run_vyxal("⟨ `a*` | `*-` | ` b ` | `` | `+c` | `d` | `()` ⟩ ~Ǎ")
    assert stack[-1][:4] == ["a*", " b ", "+c", "d"]


def test_tilde_dyad():
    stack = run_vyxal("1 2 ~+")
    assert stack == [1, 2, 3]


def test_vectorised_nilad():
    stack = run_vyxal("123 f vkd")
    assert stack[-1][:3] == ["0123456789", "0123456789", "0123456789"]


def test_transpose_inf():
    stack = run_vyxal("Þ∞ ƛÞ∞ +; ∩")
    assert [row[:3] for row in stack[-1][:3]] == [
        [2, 3, 4],
        [3, 4, 5],
        [4, 5, 6],
    ]
