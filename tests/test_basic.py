# Simple tests

from test_utils import run_code

import vyxal
from vyxal.array_builtins import deref
from vyxal.builtins import pop


def test_generators():
    # TODO add more test cases
    test_cases = [("1 10 r '5<; f", [1, 2, 3, 4])]
    for code, expected in test_cases:
        stack = run_code(code, flags=["O"])
        print(stack)
        assert deref(pop(stack)) == expected


def test_flatten():
    import vyxal.array_builtins
    import vyxal.builtins
    import vyxal.utilities

    # @make_generator
    # def gen():
    #     for i in range(10):
    #         for j in range(3, 6):
    #             yield [i, j]
    # print(deref(deep_flatten([vyxal.array_builtins.non_negative_integers(), gen()]), limit=5))
    # assert 0
    # TODO add more test cases
    # test_cases = [
    #     ("∞ ∞ W f 5 Ẏ", [0, 1, 2, 3, 4]),
    #     ("1 10 r f 5 Ẏ", [1, 2, 3, 4, 5]),
    # ]
    # import vyxal.interpreter
    # for code, expected in test_cases:
    #     print(vyxal.interpreter.vy_compile(code, vyxal.utilities.vyxal_imports))
    #     assert 0
    #     stack = run_code(code, flags=["O", "c"])
    #     res = deref(pop(stack), limit=20)
    #     print(res)
    #     assert res == expected


def test_vectorise():
    test_cases = [([[1, 2, 3, 4], [5, 6, 2], [0], [[1, 2], 3]], "h", [1, 5, 0, [1, 2]])]

    for vector, cmd, expected in test_cases:
        stack = run_code("v" + cmd, flags=["O"], input_list=[vector])
        assert pop(stack) == expected


def test_not():
    """Test negation"""
    stack = run_code("ƛ¬;", flags=["O", "c"], input_list=[[0, 1, 2, ""]])
    assert pop(stack) == [1, 0, 0, 1]


def test_inputs():
    """Test ⁰ and ¹ for taking inputs"""
    stack = run_code("⁰¹e", flags=["O"], input_list=[1, 2, 3])
    assert pop(stack) == 9


def test_non_negative_integers():
    """Test the generator producing all natural numbers (>=0)"""
    run_code("∞", flags=["O"])


def test_is_prime():
    stack = run_code("10ɾƛæ;", flags=["O"])
    assert pop(stack)._dereference() == [0, 1, 1, 0, 1, 0, 1, 0, 0, 0]


def test_is_square():
    stack = run_code("1000'∆²;", flags=["O"])
    res = pop(stack)._dereference()
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


def test_trailing_zeroes():
    """
    From https://codegolf.stackexchange.com/a/224288
    Test the command to find number of trailing zeroes in a base
    """
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
    for [num, base, expected] in trailing_zero_testcases:
        stack = run_code("Ǒ", flags=["O"], input_list=[base, num])
        print(num, base, expected, stack)
        assert stack == [expected]


def test_deep_vectorise():
    # todo add deeply nested lists and generators
    tests = [
        [[1, 2, 3], [2, 5, 1], [3, 7, 4], "+"],
        [[1, 2, 3], [2, 5, 1], [-1, -3, 2], "-"],
        [[1, 2, 3], [2, 5, -4], [2, 10, -12], "*"],
        [[1, 2, 3], [2, 5, -4], [2, 5 / 2, -4 / 3], "/"],
        [
            ["foo", "bar", 2],
            [3, "baz", "barbaz"],
            ["foofoofoo", ["bbaz", "abaz", "rbaz"], "barbazbarbaz"],
            "*",
        ],
        [[14, "foo"], 3, [0.4162896638657993, "fffoooooo"], "•"],
        [[1, 2, 3], [4, 5, 6], [4, 5, 6], "∨"],
        [[1, 2, 3], [4, 5, 6], [1, 2, 3], "⟇"],
    ]
    for input1, input2, expected, fn in tests:
        stack = run_code(fn, flags=["O"], input_list=[input1, input2])
        res = deref(pop(stack))
        print(input1, fn, input2, "should equal", res)
        assert res == expected


def test_quit():
    real_print = vyxal.builtins.vy_print

    def shouldnt_print(first, *args):
        raise ValueError("Shouldn't print anything")

    vyxal.builtins.vy_print = shouldnt_print
    run_code("69 Q")
    run_code("69 Q", flags=["O"])

    trip = []

    def should_print(first, *args):
        nonlocal trip
        trip.append(first)

    vyxal.builtins.vy_print = should_print
    run_code("69 Q", flags=["o"])
    assert trip
    vyxal.builtins.vy_print = real_print


def test_eval():
    real_print = vyxal.builtins.vy_print
    res = None

    def set_res(result):
        nonlocal res
        res = result
        import vyxal.interpreter

        vyxal.interpreter.vy_print = real_print

    vyxal.builtins.vy_print = set_res
    run_code("`1 2 3 4 5 6 W ∑,`Ė", flags=["D"])
    assert res == 21


"""
def test_foldl_rows():
    tests = [
        (list(range(1, 6)), 'λ*;', 720),
        (reshape(list(range(12)), [3, 4]), 'λ-;', [-6, -14, -22]),
        (reshape(list(range(37)), [3, 3, 4]), 'λ+;',
         [[6, 22, 38],  [54, 70, 86],  [102, 118, 134]])
    ]
    for input_array, fn, expected in tests:
        stack = run_code(fn + "ÞR", input_list=[input_array])
        assert pop(stack) == expected


def test_foldl_cols():
    # todo add more complicated test cases
    tests = [
        (reshape(list(range(1, 10)), [3, 3]), 'λ+;', [12, 15, 18]),
        (reshape(list(range(12)), [3, 4]), 'λ-;', [-12, -13, -14, -15]),
        (reshape(list(range(36)), [3, 3, 4]), 'λ-;',
         [[-12, -13, -14, -15], [-24, -25, -26, -27], [-36, -37, -38, -39]])
    ]
    for input_array, fn, expected in tests:
        stack = run_code(fn + "ÞC", input_list=[input_array])
        assert to_list(pop(stack)) == expected
"""
