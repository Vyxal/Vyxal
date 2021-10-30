import os, sys

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

from vyxal.transpile import *
from vyxal.context import Context
from vyxal.elements import *
from vyxal.LazyList import *


def test_LogicalNot():
    stack = [1]
    expected = 0
    ctx = Context()
    code = transpile("¬")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [0]
    expected = 1
    ctx = Context()
    code = transpile("¬")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc"]
    expected = 0
    ctx = Context()
    code = transpile("¬")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [""]
    expected = 1
    ctx = Context()
    code = transpile("¬")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = 0
    ctx = Context()
    code = transpile("¬")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[]]
    expected = 1
    ctx = Context()
    code = transpile("¬")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_LogicalAnd():
    stack = [0, 0]
    expected = 0
    ctx = Context()
    code = transpile("∧")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["", 1]
    expected = ""
    ctx = Context()
    code = transpile("∧")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 0]
    expected = 0
    ctx = Context()
    code = transpile("∧")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, 2]
    expected = 2
    ctx = Context()
    code = transpile("∧")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_ReversedLogicalAnd():
    stack = [0, 0]
    expected = 0
    ctx = Context()
    code = transpile("⟑")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["", 1]
    expected = ""
    ctx = Context()
    code = transpile("⟑")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 0]
    expected = 0
    ctx = Context()
    code = transpile("⟑")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, 2]
    expected = 1
    ctx = Context()
    code = transpile("⟑")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_LogicalOr():
    stack = [0, 0]
    expected = 0
    ctx = Context()
    code = transpile("∨")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["", 1]
    expected = 1
    ctx = Context()
    code = transpile("∨")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 0]
    expected = [1, 2, 3]
    ctx = Context()
    code = transpile("∨")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, 2]
    expected = 1
    ctx = Context()
    code = transpile("∨")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_ReversedLogicalOr():
    stack = [0, 0]
    expected = 0
    ctx = Context()
    code = transpile("⟇")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["", 1]
    expected = 1
    ctx = Context()
    code = transpile("⟇")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 0]
    expected = [1, 2, 3]
    ctx = Context()
    code = transpile("⟇")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, 2]
    expected = 2
    ctx = Context()
    code = transpile("⟇")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_ItemSplit():
    stack = [123456]
    expected = 6
    ctx = Context()
    code = transpile("÷")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc"]
    expected = "c"
    ctx = Context()
    code = transpile("÷")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = 3
    ctx = Context()
    code = transpile("÷")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_AsteriskLiteral():
    stack = []
    expected = "*"
    ctx = Context()
    code = transpile("×")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_MultiCommand():
    stack = [8, 2]
    expected = 3.0
    ctx = Context()
    code = transpile("•")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abcde", 4]
    expected = "aaaabbbbccccddddeeee"
    ctx = Context()
    code = transpile("•")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abcde", "FgHIj"]
    expected = "AbCDe"
    ctx = Context()
    code = transpile("•")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3, 4, 5, 6, 7], [[8, 9], 10, 11, 12, [13, 14]]]
    expected = [[1, 2], 3, 4, 5, [6, 7]]
    ctx = Context()
    code = transpile("•")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_FunctionCall():
    stack = [12]
    expected = 2
    ctx = Context()
    code = transpile("†")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 0, 1]]
    expected = [0, 1, 0]
    ctx = Context()
    code = transpile("†")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_SplitOn():
    stack = [1231234, 3]
    expected = ["12", "12", "4"]
    ctx = Context()
    code = transpile("€")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc3def", 3]
    expected = ["abc", "def"]
    ctx = Context()
    code = transpile("€")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3, 4, 3, 2, 1], 4]
    expected = [[1, 2, 3], [3, 2, 1]]
    ctx = Context()
    code = transpile("€")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Halve():
    stack = [8]
    expected = 4
    ctx = Context()
    code = transpile("½")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["FizzBuzz"]
    expected = ["Fizz", "Buzz"]
    ctx = Context()
    code = transpile("½")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[2, 4, 6, 8]]
    expected = [1, 2, 3, 4]
    ctx = Context()
    code = transpile("½")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Combinations_Remove_FixedPointCollection():
    stack = ["cabbage", "abcde"]
    expected = "cabbae"
    ctx = Context()
    code = transpile("↔")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 3, 5, 6, 7, 7, 1], [1, 3, 5]]
    expected = [1, 3, 5, 1]
    ctx = Context()
    code = transpile("↔")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2], 2]
    expected = [[1, 1], [1, 2], [2, 1], [2, 2]]
    ctx = Context()
    code = transpile("↔")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_InfiniteReplacement():
    stack = ["{[[[]]]}", "[]", ""]
    expected = "{}"
    ctx = Context()
    code = transpile("¢")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1444, 44, 34]
    expected = 1334
    ctx = Context()
    code = transpile("¢")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Complement_CommaSplit():
    stack = [5]
    expected = -4
    ctx = Context()
    code = transpile("⌐")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [-5]
    expected = 6
    ctx = Context()
    code = transpile("⌐")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["a,b,c"]
    expected = ["a", "b", "c"]
    ctx = Context()
    code = transpile("⌐")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_IsPrime_CaseCheck():
    stack = [2]
    expected = 1
    ctx = Context()
    code = transpile("æ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [4]
    expected = 0
    ctx = Context()
    code = transpile("æ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["a"]
    expected = 0
    ctx = Context()
    code = transpile("æ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["A"]
    expected = 1
    ctx = Context()
    code = transpile("æ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["!"]
    expected = -1
    ctx = Context()
    code = transpile("æ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_InclusiveZeroRange():
    stack = ["a$c"]
    expected = [1, 0, 1]
    ctx = Context()
    code = transpile("ʀ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1]]
    expected = [[0, 1]]
    ctx = Context()
    code = transpile("ʀ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3]
    expected = [0, 1, 2, 3]
    ctx = Context()
    code = transpile("ʀ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_ExclusiveZeroRange():
    stack = ["1234"]
    expected = "1234321"
    ctx = Context()
    code = transpile("ʁ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1]]
    expected = [[0]]
    ctx = Context()
    code = transpile("ʁ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3]
    expected = [0, 1, 2]
    ctx = Context()
    code = transpile("ʁ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_InclusiveOneRange():
    stack = ["abc"]
    expected = "ABC"
    ctx = Context()
    code = transpile("ɾ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[4, 5]]
    expected = [[1, 2, 3, 4], [1, 2, 3, 4, 5]]
    ctx = Context()
    code = transpile("ɾ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3]
    expected = [1, 2, 3]
    ctx = Context()
    code = transpile("ɾ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_ExclusiveOneRange():
    stack = ["1"]
    expected = 1
    ctx = Context()
    code = transpile("ɽ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[0]]
    expected = 0
    ctx = Context()
    code = transpile("ɽ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3]
    expected = [1, 2]
    ctx = Context()
    code = transpile("ɽ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Choose_randomchoice_setsame():
    stack = [5, 3]
    expected = 10
    ctx = Context()
    code = transpile("ƈ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc", "aaccb"]
    expected = 1
    ctx = Context()
    code = transpile("ƈ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc", "abcd"]
    expected = 0
    ctx = Context()
    code = transpile("ƈ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_StackLength():
    stack = [0, 1, 2]
    expected = 3
    ctx = Context()
    code = transpile("!")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, 1, 1, 1, 1]
    expected = 5
    ctx = Context()
    code = transpile("!")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = []
    expected = 0
    ctx = Context()
    code = transpile("!")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Pair():
    stack = [1, 2]
    expected = [1, 2]
    ctx = Context()
    code = transpile('"')
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, 2, 3]
    expected = [2, 3]
    ctx = Context()
    code = transpile('"')
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], "abc", 3]
    expected = ["abc", 3]
    ctx = Context()
    code = transpile('"')
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Swap():
    stack = [1, 2]
    expected = 1
    ctx = Context()
    code = transpile("$")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, 2, 3]
    expected = 2
    ctx = Context()
    code = transpile("$")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], "abc", 3]
    expected = "abc"
    ctx = Context()
    code = transpile("$")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Modulo_Format():
    stack = [5, 3]
    expected = 2
    ctx = Context()
    code = transpile("%")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello!", 3]
    expected = "o!"
    ctx = Context()
    code = transpile("%")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["Hel%ld!", "lo, Wor"]
    expected = "Hello, World!"
    ctx = Context()
    code = transpile("%")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["% and % and %", [1, 2, 3]]
    expected = "1 and 2 and 3"
    ctx = Context()
    code = transpile("%")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Multiplication():
    stack = [3, 5]
    expected = 15
    ctx = Context()
    code = transpile("*")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [4, -2]
    expected = -8
    ctx = Context()
    code = transpile("*")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [4, "*"]
    expected = "****"
    ctx = Context()
    code = transpile("*")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["x", 5]
    expected = "xxxxx"
    ctx = Context()
    code = transpile("*")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Addition():
    stack = [1, 1]
    expected = 2
    ctx = Context()
    code = transpile("+")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [0, -5]
    expected = -5
    ctx = Context()
    code = transpile("+")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc", 5]
    expected = "abc5"
    ctx = Context()
    code = transpile("+")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [5, "abc"]
    expected = "5abc"
    ctx = Context()
    code = transpile("+")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["Hello, ", "World!"]
    expected = "Hello, World!"
    ctx = Context()
    code = transpile("+")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 4]
    expected = [5, 6, 7]
    ctx = Context()
    code = transpile("+")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], [4, 5, 6]]
    expected = [5, 7, 9]
    ctx = Context()
    code = transpile("+")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Subtract():
    stack = [5, 4]
    expected = 1
    ctx = Context()
    code = transpile("-")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [0, -5]
    expected = 5
    ctx = Context()
    code = transpile("-")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["|", 5]
    expected = "|-----"
    ctx = Context()
    code = transpile("-")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3, "> arrow"]
    expected = "---> arrow"
    ctx = Context()
    code = transpile("-")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abcbde", "b"]
    expected = "acde"
    ctx = Context()
    code = transpile("-")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["aaa", "a"]
    expected = ""
    ctx = Context()
    code = transpile("-")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], [1, 2, 3]]
    expected = [0, 0, 0]
    ctx = Context()
    code = transpile("-")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[10, 20, 30], 5]
    expected = [5, 15, 25]
    ctx = Context()
    code = transpile("-")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Divide_Split():
    stack = [4, 2]
    expected = 2
    ctx = Context()
    code = transpile("/")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abcdef", 3]
    expected = ["ab", "cd", "ef"]
    ctx = Context()
    code = transpile("/")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["1,2,3", ","]
    expected = ["1", "2", "3"]
    ctx = Context()
    code = transpile("/")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_LessThan():
    stack = [1, 2]
    expected = 1
    ctx = Context()
    code = transpile("<")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [2, 1]
    expected = 0
    ctx = Context()
    code = transpile("<")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["a", "b"]
    expected = 1
    ctx = Context()
    code = transpile("<")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [-5, 2]
    expected = 1
    ctx = Context()
    code = transpile("<")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 2]
    expected = [1, 0, 0]
    ctx = Context()
    code = transpile("<")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Equals():
    stack = [1, 1]
    expected = 1
    ctx = Context()
    code = transpile("=")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [2, 1]
    expected = 0
    ctx = Context()
    code = transpile("=")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["a", "b"]
    expected = 0
    ctx = Context()
    code = transpile("=")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["xyz", "xyz"]
    expected = 1
    ctx = Context()
    code = transpile("=")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 2]
    expected = [0, 1, 0]
    ctx = Context()
    code = transpile("=")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, "1"]
    expected = 1
    ctx = Context()
    code = transpile("=")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_GreaterThan():
    stack = [1, 2]
    expected = 0
    ctx = Context()
    code = transpile(">")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [2, 1]
    expected = 1
    ctx = Context()
    code = transpile(">")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["a", "b"]
    expected = 0
    ctx = Context()
    code = transpile(">")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [2, -5]
    expected = 1
    ctx = Context()
    code = transpile(">")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 2]
    expected = [0, 0, 1]
    ctx = Context()
    code = transpile(">")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["5", 10]
    expected = 0
    ctx = Context()
    code = transpile(">")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_All():
    stack = [[1, 2, 3]]
    expected = 1
    ctx = Context()
    code = transpile("A")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[0, 1, 2]]
    expected = 0
    ctx = Context()
    code = transpile("A")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [["", 1, 2]]
    expected = 0
    ctx = Context()
    code = transpile("A")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[]]
    expected = 1
    ctx = Context()
    code = transpile("A")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [""]
    expected = 1
    ctx = Context()
    code = transpile("A")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [0]
    expected = 0
    ctx = Context()
    code = transpile("A")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_BinaryToDecimal():
    stack = [[1, 0, 1]]
    expected = 5
    ctx = Context()
    code = transpile("B")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 1, 1]]
    expected = 7
    ctx = Context()
    code = transpile("B")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["1011"]
    expected = 11
    ctx = Context()
    code = transpile("B")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Chr_Ord():
    stack = [65]
    expected = "A"
    ctx = Context()
    code = transpile("C")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [8482]
    expected = "™"
    ctx = Context()
    code = transpile("C")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["Z"]
    expected = 90
    ctx = Context()
    code = transpile("C")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["ABC"]
    expected = [65, 66, 67]
    ctx = Context()
    code = transpile("C")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[124, 125, 126]]
    expected = ["{", "|", "}"]
    ctx = Context()
    code = transpile("C")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_TwoPower_PythonEval():
    stack = [0]
    expected = 1
    ctx = Context()
    code = transpile("E")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [2]
    expected = 4
    ctx = Context()
    code = transpile("E")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["[1,2,3]"]
    expected = [1, 2, 3]
    ctx = Context()
    code = transpile("E")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Filter():
    stack = [[1, 2, 3], [2, 4, 6]]
    expected = [1, 3]
    ctx = Context()
    code = transpile("F")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abcdef", "daffodil"]
    expected = "bce"
    ctx = Context()
    code = transpile("F")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Max():
    stack = [[1, 3, 2]]
    expected = 3
    ctx = Context()
    code = transpile("G")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["python"]
    expected = "y"
    ctx = Context()
    code = transpile("G")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_HexToDecimal():
    stack = [[1, 2, 3]]
    expected = 291
    ctx = Context()
    code = transpile("H")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["b"]
    expected = 11
    ctx = Context()
    code = transpile("H")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["beedab"]
    expected = 12512683
    ctx = Context()
    code = transpile("H")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Int():
    stack = ["5"]
    expected = 5
    ctx = Context()
    code = transpile("I")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [5]
    expected = 5
    ctx = Context()
    code = transpile("I")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[5]]
    expected = 5
    ctx = Context()
    code = transpile("I")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Join():
    stack = [[1, 2, 3], 4]
    expected = [1, 2, 3, 4]
    ctx = Context()
    code = transpile("J")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc", "def"]
    expected = "abcdef"
    ctx = Context()
    code = transpile("J")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, [2, 3, 4]]
    expected = [1, 2, 3, 4]
    ctx = Context()
    code = transpile("J")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2], [3, 4]]
    expected = [1, 2, 3, 4]
    ctx = Context()
    code = transpile("J")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Factors_Substrings_Prefixes():
    stack = [20]
    expected = [1, 2, 4, 5, 10, 20]
    ctx = Context()
    code = transpile("K")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1]
    expected = 1
    ctx = Context()
    code = transpile("K")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc"]
    expected = ["a", "b", "c", "ab", "bc", "abc"]
    ctx = Context()
    code = transpile("K")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = [[1], [1, 2], [1, 2, 3]]
    ctx = Context()
    code = transpile("K")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Length():
    stack = ["abc"]
    expected = 3
    ctx = Context()
    code = transpile("L")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = 3
    ctx = Context()
    code = transpile("L")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, "wrfwerfgbr", 6]]
    expected = 4
    ctx = Context()
    code = transpile("L")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Map():
    stack = [5, [1, 2, 3]]
    expected = [[5, 1], [5, 2], [5, 3]]
    ctx = Context()
    code = transpile("M")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["z", "hi"]
    expected = [["z", "h"], ["z", "i"]]
    ctx = Context()
    code = transpile("M")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Negate_SwapCase():
    stack = [5]
    expected = -5
    ctx = Context()
    code = transpile("N")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [-1]
    expected = 1
    ctx = Context()
    code = transpile("N")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["a"]
    expected = "A"
    ctx = Context()
    code = transpile("N")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["aBc"]
    expected = "AbC"
    ctx = Context()
    code = transpile("N")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Count():
    stack = [[1, 2, 3, 4, 5, 4, 3], 4]
    expected = 2
    ctx = Context()
    code = transpile("O")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abcdbacsabdcabca", "a"]
    expected = 5
    ctx = Context()
    code = transpile("O")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Strip():
    stack = [[1, 2, 3, 4, 5, 4, 3, 2, 1], [1, 2]]
    expected = [3, 4, 5, 4, 3]
    ctx = Context()
    code = transpile("P")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["    Hello, World!    ", " "]
    expected = "Hello, World!"
    ctx = Context()
    code = transpile("P")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Reduce():
    stack = [[[1, 2], [3, 4]]]
    expected = [[2, 1], [4, 3]]
    ctx = Context()
    code = transpile("R")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[[1, 2]]]
    expected = [[2, 1]]
    ctx = Context()
    code = transpile("R")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Stringify():
    stack = [5]
    expected = "5"
    ctx = Context()
    code = transpile("S")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = "⟨1|2|3⟩"
    ctx = Context()
    code = transpile("S")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["X"]
    expected = "X"
    ctx = Context()
    code = transpile("S")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_TruthyIndices():
    stack = [[0, 1, 0, 2]]
    expected = [1, 3]
    ctx = Context()
    code = transpile("T")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3, 4]]
    expected = [0, 1, 2, 3]
    ctx = Context()
    code = transpile("T")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Uniquify():
    stack = [[1, 3, 5, 5]]
    expected = [1, 3, 5]
    ctx = Context()
    code = transpile("U")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abdbcdbch"]
    expected = "abdch"
    ctx = Context()
    code = transpile("U")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Replace():
    stack = ["hela", "a", "lo"]
    expected = "hello"
    ctx = Context()
    code = transpile("V")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["banana", "n", "nan"]
    expected = "banananana"
    ctx = Context()
    code = transpile("V")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Wrap():
    stack = [1, 2, 3]
    expected = [1, 2, 3]
    ctx = Context()
    code = transpile("W")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = []
    expected = []
    ctx = Context()
    code = transpile("W")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello", 1, 9]
    expected = ["hello", 1, 9]
    ctx = Context()
    code = transpile("W")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Interleave():
    stack = [[1, 3, 5], [2, 4]]
    expected = [1, 2, 3, 4, 5]
    ctx = Context()
    code = transpile("Y")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["srn", "tig"]
    expected = "string"
    ctx = Context()
    code = transpile("Y")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Zip():
    stack = [[1, 2], [3, 4]]
    expected = [[1, 3], [2, 4]]
    ctx = Context()
    code = transpile("Z")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc", [1, 2, 3]]
    expected = [["a", 1], ["b", 2], ["c", 3]]
    ctx = Context()
    code = transpile("Z")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Any():
    stack = [[1, 2, 3]]
    expected = 1
    ctx = Context()
    code = transpile("a")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[0, 0, 0]]
    expected = 0
    ctx = Context()
    code = transpile("a")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[0, 1, 2]]
    expected = 1
    ctx = Context()
    code = transpile("a")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Binary():
    stack = [5]
    expected = [1, 0, 1]
    ctx = Context()
    code = transpile("b")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [" "]
    expected = [[1, 0, 0, 0, 0, 0]]
    ctx = Context()
    code = transpile("b")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[2, 3]]
    expected = [[1, 0], [1, 1]]
    ctx = Context()
    code = transpile("b")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Contains():
    stack = ["abcdef", "a"]
    expected = 1
    ctx = Context()
    code = transpile("c")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["xyz", "a"]
    expected = 0
    ctx = Context()
    code = transpile("c")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 1]
    expected = 1
    ctx = Context()
    code = transpile("c")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 0]
    expected = 0
    ctx = Context()
    code = transpile("c")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Double():
    stack = [5]
    expected = 10
    ctx = Context()
    code = transpile("d")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [0]
    expected = 0
    ctx = Context()
    code = transpile("d")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2]]
    expected = [2, 4]
    ctx = Context()
    code = transpile("d")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["x"]
    expected = "xx"
    ctx = Context()
    code = transpile("d")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["ha"]
    expected = "haha"
    ctx = Context()
    code = transpile("d")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Exponentiation():
    stack = [5, 3]
    expected = 125
    ctx = Context()
    code = transpile("e")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [0, 0]
    expected = 1
    ctx = Context()
    code = transpile("e")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello", 2]
    expected = "hlo"
    ctx = Context()
    code = transpile("e")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Flatten():
    stack = [135]
    expected = [1, 3, 5]
    ctx = Context()
    code = transpile("f")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hi"]
    expected = ["h", "i"]
    ctx = Context()
    code = transpile("f")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[[[1, 2], 3, [[4, [5]], 6], 7], [8, [9]]]]
    expected = [1, 2, 3, 4, 5, 6, 7, 8, 9]
    ctx = Context()
    code = transpile("f")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [-1]
    expected = ["-", 1]
    ctx = Context()
    code = transpile("f")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Minimum():
    stack = ["abc"]
    expected = "a"
    ctx = Context()
    code = transpile("g")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 4, -2]]
    expected = -2
    ctx = Context()
    code = transpile("g")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[5, 3, 9]]
    expected = 3
    ctx = Context()
    code = transpile("g")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Head():
    stack = ["hello"]
    expected = "h"
    ctx = Context()
    code = transpile("h")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = 1
    ctx = Context()
    code = transpile("h")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Index():
    stack = ["abc", 1]
    expected = "b"
    ctx = Context()
    code = transpile("i")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 0]
    expected = 1
    ctx = Context()
    code = transpile("i")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[2, 3, 4, 5], [2]]
    expected = [2, 3]
    ctx = Context()
    code = transpile("i")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 3, 5, 7], [1, 3]]
    expected = [3, 5]
    ctx = Context()
    code = transpile("i")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3, 4, 5, 6, 7, 8, 9, 10], [1, 8, 2]]
    expected = [2, 4, 6, 8]
    ctx = Context()
    code = transpile("i")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Join():
    stack = [[1, 2, 3], "penguin"]
    expected = "1penguin2penguin3"
    ctx = Context()
    code = transpile("j")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [["he", "", "o, wor", "d!"], "l"]
    expected = "hello, world!"
    ctx = Context()
    code = transpile("j")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_CumulativeGroups():
    stack = ["hello", 3]
    expected = ["hel", "ell", "llo"]
    ctx = Context()
    code = transpile("l")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["cake", 2]
    expected = ["ca", "ak", "ke"]
    ctx = Context()
    code = transpile("l")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["cheese", "cake"]
    expected = 0
    ctx = Context()
    code = transpile("l")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["cheese", "salads"]
    expected = 1
    ctx = Context()
    code = transpile("l")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Mirror():
    stack = [123]
    expected = 444
    ctx = Context()
    code = transpile("m")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hi"]
    expected = "hiih"
    ctx = Context()
    code = transpile("m")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = [1, 2, 3, 3, 2, 1]
    ctx = Context()
    code = transpile("m")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Remove():
    stack = ["hello", "l"]
    expected = "heo"
    ctx = Context()
    code = transpile("o")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3, 1, 2], 1]
    expected = [2, 3, 2]
    ctx = Context()
    code = transpile("o")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["bananas and naan", "an"]
    expected = "bas d na"
    ctx = Context()
    code = transpile("o")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Prepend():
    stack = ["ld", "wor"]
    expected = "world"
    ctx = Context()
    code = transpile("p")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 13]
    expected = [13, 1, 2, 3]
    ctx = Context()
    code = transpile("p")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[3, 4, 5], "23"]
    expected = ["23", 3, 4, 5]
    ctx = Context()
    code = transpile("p")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Uneval():
    stack = ["\\"]
    expected = "`\\`"
    ctx = Context()
    code = transpile("q")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["`"]
    expected = "`\``"
    ctx = Context()
    code = transpile("q")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["a"]
    expected = "`a`"
    ctx = Context()
    code = transpile("q")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Range():
    stack = [3, 6]
    expected = [3, 4, 5]
    ctx = Context()
    code = transpile("r")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [4, 8]
    expected = [4, 5, 6, 7]
    ctx = Context()
    code = transpile("r")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_sort():
    stack = [[3, 1, 2]]
    expected = [1, 2, 3]
    ctx = Context()
    code = transpile("s")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["bca"]
    expected = "abc"
    ctx = Context()
    code = transpile("s")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Tail():
    stack = ["hello"]
    expected = "o"
    ctx = Context()
    code = transpile("t")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = 3
    ctx = Context()
    code = transpile("t")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_MinusOne():
    stack = []
    expected = -1
    ctx = Context()
    code = transpile("u")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Listify():
    stack = [1]
    expected = [1]
    ctx = Context()
    code = transpile("w")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello"]
    expected = ["hello"]
    ctx = Context()
    code = transpile("w")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = [[1, 2, 3]]
    ctx = Context()
    code = transpile("w")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Uninterleave():
    stack = ["abcde"]
    expected = "bd"
    ctx = Context()
    code = transpile("y")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3, 4]]
    expected = [2, 4]
    ctx = Context()
    code = transpile("y")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Zipmap():
    stack = [1, [1, 2, 3]]
    expected = [[1, 1], [2, 2], [3, 3]]
    ctx = Context()
    code = transpile("z")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, "zap"]
    expected = [["z", "z"], ["a", "a"], ["p", "p"]]
    ctx = Context()
    code = transpile("z")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_MaxbyTail():
    stack = [[[3, 4], [9, 2]]]
    expected = [3, 4]
    ctx = Context()
    code = transpile("↑")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], [2, 5]]
    expected = [2, 5]
    ctx = Context()
    code = transpile("↑")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_MinbyTail():
    stack = [[[3, 4], [9, 2]]]
    expected = [9, 2]
    ctx = Context()
    code = transpile("↑")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], [2, 5]]
    expected = [1, 2, 3]
    ctx = Context()
    code = transpile("↑")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_DyadicMaximum():
    stack = [5, 3]
    expected = 5
    ctx = Context()
    code = transpile("∴")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello", "goodbye"]
    expected = "hello"
    ctx = Context()
    code = transpile("∴")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3, "(stuff)"]
    expected = 3
    ctx = Context()
    code = transpile("∴")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_DyadicMinimum():
    stack = [5, 3]
    expected = 3
    ctx = Context()
    code = transpile("∵")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello", "goodbye"]
    expected = "goodbye"
    ctx = Context()
    code = transpile("∵")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3, "(stuff)"]
    expected = "(stuff)"
    ctx = Context()
    code = transpile("∵")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Increment():
    stack = [5]
    expected = 6
    ctx = Context()
    code = transpile("›")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[3, 4]]
    expected = [4, 5]
    ctx = Context()
    code = transpile("›")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello"]
    expected = "hello1"
    ctx = Context()
    code = transpile("›")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Decrement():
    stack = [5]
    expected = 4
    ctx = Context()
    code = transpile("‹")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[3, 4]]
    expected = [2, 3]
    ctx = Context()
    code = transpile("‹")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello"]
    expected = "hello-"
    ctx = Context()
    code = transpile("‹")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Parity():
    stack = [2]
    expected = 0
    ctx = Context()
    code = transpile("∷")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3]
    expected = 1
    ctx = Context()
    code = transpile("∷")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello!"]
    expected = "lo!"
    ctx = Context()
    code = transpile("∷")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_EmptyString():
    stack = []
    expected = ""
    ctx = Context()
    code = transpile("¤")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Space():
    stack = []
    expected = " "
    ctx = Context()
    code = transpile("ð")
    exec(code)
    assert simplify(stack[-1]) == expected
