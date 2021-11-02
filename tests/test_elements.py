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


def test_CombinationsRemoveFixedPointCollection():
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


def test_ComplementCommaSplit():
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


def test_IsPrimeCaseCheck():
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


def test_Chooserandomchoicesetsame():
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


def test_Palindromise():
    stack = [1, 2, 3]
    expected = [1, 2, 3, 2, 1]
    ctx = Context()
    code = transpile("∞")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, 2, 3, 4]
    expected = [1, 2, 3, 4, 3, 2, 1]
    ctx = Context()
    code = transpile("∞")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, 2, 3, 4, 5]
    expected = [1, 2, 3, 4, 5, 4, 3, 2, 1]
    ctx = Context()
    code = transpile("∞")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, 2, 3, 4, 5, 6]
    expected = [1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1]
    ctx = Context()
    code = transpile("∞")
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


def test_ModuloFormat():
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


def test_DivideSplit():
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
    expected = 1
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


def test_ChrOrd():
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


def test_TwoPowerPythonEval():
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


def test_FactorsSubstringsPrefixes():
    stack = [20]
    expected = [1, 2, 4, 5, 10, 20]
    ctx = Context()
    code = transpile("K")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1]
    expected = [1]
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


def test_NegateSwapCase():
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
    expected = "`\\``"
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


def test_Zipself():
    stack = [[1, 2, 3]]
    expected = [[1, 1], [2, 2], [3, 3]]
    ctx = Context()
    code = transpile("z")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["zap"]
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

    stack = [[[1, 2, 3], [2, 5]]]
    expected = [2, 5]
    ctx = Context()
    code = transpile("↑")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_MinbyTail():
    stack = [[[3, 4], [9, 2]]]
    expected = [9, 2]
    ctx = Context()
    code = transpile("↓")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[[1, 2, 3], [2, 5]]]
    expected = [1, 2, 3]
    ctx = Context()
    code = transpile("↓")
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


def test_IncrementSpaceReplaceWith0():
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

    stack = ["  101"]
    expected = "00101"
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


def test_ToBaseTenFromCustomBase():
    stack = [43, 5]
    expected = 23
    ctx = Context()
    code = transpile("β")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["banana", "nab"]
    expected = 577
    ctx = Context()
    code = transpile("β")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[15, 23, 9], 31]
    expected = 15137
    ctx = Context()
    code = transpile("β")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_FromBaseTenToCustomBase():
    stack = [1234567, "abc"]
    expected = "cacccabbbbcab"
    ctx = Context()
    code = transpile("τ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1234567, 5]
    expected = [3, 0, 4, 0, 0, 1, 2, 3, 2]
    ctx = Context()
    code = transpile("τ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [928343, ["he", "ll", "o"]]
    expected = [
        "ll",
        "o",
        "he",
        "o",
        "he",
        "ll",
        "ll",
        "ll",
        "ll",
        "he",
        "he",
        "he",
        "o",
    ]
    ctx = Context()
    code = transpile("τ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Absolutevalue():
    stack = [1]
    expected = 1
    ctx = Context()
    code = transpile("ȧ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [-1]
    expected = 1
    ctx = Context()
    code = transpile("ȧ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [" ch ee s e "]
    expected = "cheese"
    ctx = Context()
    code = transpile("ȧ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[-1, 2, -5]]
    expected = [1, 2, 5]
    ctx = Context()
    code = transpile("ȧ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Boolify():
    stack = [0]
    expected = 0
    ctx = Context()
    code = transpile("ḃ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1]
    expected = 1
    ctx = Context()
    code = transpile("ḃ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[]]
    expected = 0
    ctx = Context()
    code = transpile("ḃ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["x"]
    expected = 1
    ctx = Context()
    code = transpile("ḃ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_NotOne():
    stack = [[]]
    expected = 1
    ctx = Context()
    code = transpile("ċ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["1"]
    expected = 0
    ctx = Context()
    code = transpile("ċ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [5]
    expected = 1
    ctx = Context()
    code = transpile("ċ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1]
    expected = 0
    ctx = Context()
    code = transpile("ċ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Divmod():
    stack = [5, 3]
    expected = [1, 2]
    ctx = Context()
    code = transpile("ḋ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abcd", 3]
    expected = ["abc", "abd", "acd", "bcd"]
    ctx = Context()
    code = transpile("ḋ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 2]
    expected = [[1, 2], [1, 3], [2, 3]]
    ctx = Context()
    code = transpile("ḋ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abcdef", "Joe"]
    expected = ["Joedef"]
    ctx = Context()
    code = transpile("ḋ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Enumerate():
    stack = ["abc"]
    expected = [[0, "a"], [1, "b"], [2, "c"]]
    ctx = Context()
    code = transpile("ė")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = [[0, 1], [1, 2], [2, 3]]
    ctx = Context()
    code = transpile("ė")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Find():
    stack = [[1, 2, 3], 2]
    expected = 1
    ctx = Context()
    code = transpile("ḟ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello", "l"]
    expected = 2
    ctx = Context()
    code = transpile("ḟ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Gcd():
    stack = [[1, 3, 2]]
    expected = 1
    ctx = Context()
    code = transpile("ġ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[60, 42, 108]]
    expected = 6
    ctx = Context()
    code = transpile("ġ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [50, 35]
    expected = 5
    ctx = Context()
    code = transpile("ġ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["laugh", "cough"]
    expected = "ugh"
    ctx = Context()
    code = transpile("ġ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_HeadExtract():
    stack = ["hello"]
    expected = "ello"
    ctx = Context()
    code = transpile("ḣ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = [2, 3]
    ctx = Context()
    code = transpile("ḣ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_FloorDivision():
    stack = [5, 3]
    expected = 1
    ctx = Context()
    code = transpile("ḭ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello!", 3]
    expected = "he"
    ctx = Context()
    code = transpile("ḭ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3, "hello!"]
    expected = "he"
    ctx = Context()
    code = transpile("ḭ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_LeftJustifyGridifyInfiniteReplaceCollectuntilfale():
    stack = [1, 3, 2]
    expected = 1
    ctx = Context()
    code = transpile("ŀ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Mean():
    stack = [[1, 2, 3]]
    expected = 2
    ctx = Context()
    code = transpile("ṁ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[4, 71, -63]]
    expected = 4
    ctx = Context()
    code = transpile("ṁ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_JoinByNothing():
    stack = [["a", "b", "c"]]
    expected = "abc"
    ctx = Context()
    code = transpile("ṅ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = 123
    ctx = Context()
    code = transpile("ṅ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Slice():
    stack = ["hello", 2]
    expected = "llo"
    ctx = Context()
    code = transpile("ȯ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 1]
    expected = [2, 3]
    ctx = Context()
    code = transpile("ȯ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Powerset():
    stack = ["ab"]
    expected = [[], ["a"], ["b"], ["a", "b"]]
    ctx = Context()
    code = transpile("ṗ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, 2, 3]
    expected = [[], [1], [2], [3], [1, 2], [1, 3], [2, 3], [1, 2, 3]]
    ctx = Context()
    code = transpile("ṗ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Round():
    stack = [5.5]
    expected = 6
    ctx = Context()
    code = transpile("ṙ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3.2]
    expected = 3
    ctx = Context()
    code = transpile("ṙ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[5.5, 3.2]]
    expected = [6, 3]
    ctx = Context()
    code = transpile("ṙ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [-4.7]
    expected = -5
    ctx = Context()
    code = transpile("ṙ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [-4.5]
    expected = -4
    ctx = Context()
    code = transpile("ṙ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_FunctionSort():
    stack = [3, 4]
    expected = [3, 4]
    ctx = Context()
    code = transpile("ṡ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, 5]
    expected = [1, 2, 3, 4, 5]
    ctx = Context()
    code = transpile("ṡ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc1def2ghi", "\\d+"]
    expected = ["abc", "def", "ghi"]
    ctx = Context()
    code = transpile("ṡ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_TailExtract():
    stack = ["abc"]
    expected = "c"
    ctx = Context()
    code = transpile("ṫ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = 3
    ctx = Context()
    code = transpile("ṫ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_ChunkWrap():
    stack = ["abcdef", 2]
    expected = ["ab", "cd", "ef"]
    ctx = Context()
    code = transpile("ẇ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3, 4, 5, 6], 3]
    expected = [[1, 2, 3], [4, 5, 6]]
    ctx = Context()
    code = transpile("ẇ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Repeat():
    stack = [[1, 2, 3], 3]
    expected = [[1, 2, 3], [1, 2, 3], [1, 2, 3]]
    ctx = Context()
    code = transpile("ẋ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["x", 5]
    expected = "xxxxx"
    ctx = Context()
    code = transpile("ẋ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_ExclusiveRangeLength():
    stack = ["abc"]
    expected = [0, 1, 2]
    ctx = Context()
    code = transpile("ẏ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2]]
    expected = [0, 1]
    ctx = Context()
    code = transpile("ẏ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_InclusiveRangeLength():
    stack = ["abc"]
    expected = [1, 2, 3]
    ctx = Context()
    code = transpile("ż")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2]]
    expected = [1, 2]
    ctx = Context()
    code = transpile("ż")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_SquareRoot():
    stack = [4]
    expected = 2
    ctx = Context()
    code = transpile("√")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello"]
    expected = "hlo"
    ctx = Context()
    code = transpile("√")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Ten():
    stack = []
    expected = 10
    ctx = Context()
    code = transpile("₀")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Hundred():
    stack = []
    expected = 100
    ctx = Context()
    code = transpile("₁")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_IsEven():
    stack = [5]
    expected = 0
    ctx = Context()
    code = transpile("₂")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [2]
    expected = 1
    ctx = Context()
    code = transpile("₂")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello"]
    expected = 0
    ctx = Context()
    code = transpile("₂")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2]]
    expected = 1
    ctx = Context()
    code = transpile("₂")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_DivisibleBythree():
    stack = [5]
    expected = 0
    ctx = Context()
    code = transpile("₃")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [6]
    expected = 1
    ctx = Context()
    code = transpile("₃")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hi"]
    expected = 0
    ctx = Context()
    code = transpile("₃")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1]]
    expected = 1
    ctx = Context()
    code = transpile("₃")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_TwentySix():
    stack = []
    expected = 26
    ctx = Context()
    code = transpile("₄")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_DivisibleByFive():
    stack = [4]
    expected = 0
    ctx = Context()
    code = transpile("₅")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [5]
    expected = 1
    ctx = Context()
    code = transpile("₅")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello"]
    expected = 5
    ctx = Context()
    code = transpile("₅")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = 3
    ctx = Context()
    code = transpile("₅")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_SixtyFour():
    stack = []
    expected = 64
    ctx = Context()
    code = transpile("₆")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_OneTwentyEight():
    stack = []
    expected = 128
    ctx = Context()
    code = transpile("₇")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_TwoFiftySix():
    stack = []
    expected = 256
    ctx = Context()
    code = transpile("₈")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Newline():
    stack = []
    expected = "\\n"
    ctx = Context()
    code = transpile("¶")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_JoinOnNewlines():
    stack = [[1, 2, 3, 4, 5, 6]]
    expected = "1\n2\n3\n4\n5\n6"
    ctx = Context()
    code = transpile("⁋")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [["Hello", "World!"]]
    expected = "Hello\nWorld!"
    ctx = Context()
    code = transpile("⁋")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_VerticalJoin():
    stack = [["abc", "def", "ghi"]]
    expected = "adg\nbeh\ncfi"
    ctx = Context()
    code = transpile("§")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [["***", "****", "*****"]]
    expected = "  *\n **\n***\n***\n***"
    ctx = Context()
    code = transpile("§")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_AbsoluteDifferencePaddedVerticalJoin():
    stack = [5, 1]
    expected = 4
    ctx = Context()
    code = transpile("ε")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, 5]
    expected = 4
    ctx = Context()
    code = transpile("ε")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3, 3]
    expected = 0
    ctx = Context()
    code = transpile("ε")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [["***", "****", "*****"], "."]
    expected = "..*\n.**\n***\n***\n***"
    ctx = Context()
    code = transpile("ε")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [["abc", "def", "ghi"], "."]
    expected = "adg\nbeh\ncfi"
    ctx = Context()
    code = transpile("ε")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Factorial():
    stack = [5]
    expected = 120
    ctx = Context()
    code = transpile("¡")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello my name jeff. ur sussy baka"]
    expected = "Hello my name jeff. Ur sussy baka"
    ctx = Context()
    code = transpile("¡")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3, 4, 5]]
    expected = [1, 2, 6, 24, 120]
    ctx = Context()
    code = transpile("¡")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Summate():
    stack = [[1, 2, 3, 4, 5]]
    expected = 15
    ctx = Context()
    code = transpile("∑")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [["abc", "def", 10]]
    expected = "abcdef10"
    ctx = Context()
    code = transpile("∑")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [12345]
    expected = 15
    ctx = Context()
    code = transpile("∑")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_CumulativeSum():
    stack = [12345]
    expected = [1, 3, 6, 10, 15]
    ctx = Context()
    code = transpile("¦")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abcdef"]
    expected = ["a", "ab", "abc", "abcd", "abcde", "abcdef"]
    ctx = Context()
    code = transpile("¦")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3, 4, 5]]
    expected = [1, 3, 6, 10, 15]
    ctx = Context()
    code = transpile("¦")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_AllEqual():
    stack = [1111]
    expected = 1
    ctx = Context()
    code = transpile("≈")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["acc"]
    expected = 0
    ctx = Context()
    code = transpile("≈")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 2, 1]]
    expected = 0
    ctx = Context()
    code = transpile("≈")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[]]
    expected = 1
    ctx = Context()
    code = transpile("≈")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Assign():
    stack = [[1, 2, 3, 4], 1, 0]
    expected = [1, 0, 3, 4]
    ctx = Context()
    code = transpile("Ȧ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["Hello ", ", World!", 5]
    expected = "Hello, World!"
    ctx = Context()
    code = transpile("Ȧ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [69320, 2, 4]
    expected = [6, 9, 4, 2, 0]
    ctx = Context()
    code = transpile("Ȧ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Bifurcate():
    stack = [203]
    expected = 302
    ctx = Context()
    code = transpile("Ḃ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc"]
    expected = "cab"
    ctx = Context()
    code = transpile("Ḃ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3, 4]]
    expected = [4, 3, 2, 1]
    ctx = Context()
    code = transpile("Ḃ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Counts():
    stack = [[1, 2, 2, 3, 3, 3, 3]]
    expected = [[1, 1], [2, 2], [3, 4]]
    ctx = Context()
    code = transpile("Ċ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["Hello, World!"]
    expected = [
        ["W", 1],
        ["!", 1],
        [" ", 1],
        ["o", 2],
        ["d", 1],
        [",", 1],
        ["H", 1],
        ["l", 3],
        ["e", 1],
        ["r", 1],
    ]
    ctx = Context()
    code = transpile("Ċ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_IsDivisibleArbitraryDuplicate():
    stack = [15, 5]
    expected = 1
    ctx = Context()
    code = transpile("Ḋ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc", 3]
    expected = "abc"
    ctx = Context()
    code = transpile("Ḋ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[5, 13, 29, 48, 12], 2]
    expected = [0, 0, 0, 1, 1]
    ctx = Context()
    code = transpile("Ḋ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_VyxalExecReciprocal():
    stack = [[2, 3, -1]]
    expected = [0.5, 1 / 3, -1]
    ctx = Context()
    code = transpile("Ė")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["kH"]
    expected = "Hello, World!"
    ctx = Context()
    code = transpile("Ė")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Groupconsecutive():
    stack = [[1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 5, 5]]
    expected = [[1, 1, 1], [2, 2, 2, 2, 2, 2], [3, 3, 3, 3, 3], [4, 4], [5, 5]]
    ctx = Context()
    code = transpile("Ġ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["Hello, World!"]
    expected = [
        ["H"],
        ["e"],
        ["l", "l"],
        ["o"],
        [","],
        [" "],
        ["W"],
        ["o"],
        ["r"],
        ["l"],
        ["d"],
        ["!"],
    ]
    ctx = Context()
    code = transpile("Ġ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Tail():
    stack = [[0, [43, 69], "foo"]]
    expected = [[43, 69], "foo"]
    ctx = Context()
    code = transpile("Ḣ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[]]
    expected = []
    ctx = Context()
    code = transpile("Ḣ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["foo"]
    expected = "oo"
    ctx = Context()
    code = transpile("Ḣ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [""]
    expected = ""
    ctx = Context()
    code = transpile("Ḣ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1234.56]
    expected = 234.56
    ctx = Context()
    code = transpile("Ḣ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [0.2]
    expected = 0.2
    ctx = Context()
    code = transpile("Ḣ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Indexintoorfindcycle():
    stack = [[0, 2, 4], ["foo", "bar", -69, 420, "baz"]]
    expected = ["foo", -69, "baz"]
    ctx = Context()
    code = transpile("İ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Transliterate():
    stack = ["abcdefcba", "abc", "123"]
    expected = "123def321"
    ctx = Context()
    code = transpile("Ŀ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 0], [2], [5]]
    expected = [1, 5, 0]
    ctx = Context()
    code = transpile("Ŀ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc", "ab", ["bb", "cc"]]
    expected = ["bb", "cc", "c"]
    ctx = Context()
    code = transpile("Ŀ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Insert():
    stack = [[1, 3, 4], 1, 2]
    expected = [1, 2, 3, 4]
    ctx = Context()
    code = transpile("Ṁ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["wyz", 1, "x"]
    expected = "wxyz"
    ctx = Context()
    code = transpile("Ṁ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["jknop", 2, "lm"]
    expected = "jklmnop"
    ctx = Context()
    code = transpile("Ṁ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Integerpartitions():
    stack = [5]
    expected = [
        [5],
        [1, 4],
        [1, 1, 3],
        [1, 1, 1, 2],
        [1, 1, 1, 1, 1],
        [1, 2, 2],
        [2, 3],
    ]
    ctx = Context()
    code = transpile("Ṅ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello"]
    expected = "h e l l o"
    ctx = Context()
    code = transpile("Ṅ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = "1 2 3"
    ctx = Context()
    code = transpile("Ṅ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Over():
    stack = [4, 5]
    expected = 4
    ctx = Context()
    code = transpile("Ȯ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hi", "bye"]
    expected = "hi"
    ctx = Context()
    code = transpile("Ȯ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Permutations():
    stack = ["abc"]
    expected = ["abc", "abc", "bac", "bca", "cab", "cba"]
    ctx = Context()
    code = transpile("Ṗ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2]]
    expected = [[1, 2], [2, 1]]
    ctx = Context()
    code = transpile("Ṗ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Reverse():
    stack = [203]
    expected = 302
    ctx = Context()
    code = transpile("Ṙ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc"]
    expected = "cab"
    ctx = Context()
    code = transpile("Ṙ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3, 4]]
    expected = [4, 3, 2, 1]
    ctx = Context()
    code = transpile("Ṙ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Sumofstack():
    stack = [[1, 2, 3], [4, 5, 6]]
    expected = [5, 7, 9]
    ctx = Context()
    code = transpile("Ṡ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3, 4, 5]
    expected = 12
    ctx = Context()
    code = transpile("Ṡ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hi", "bye"]
    expected = "hibye"
    ctx = Context()
    code = transpile("Ṡ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_TailRemove():
    stack = ["1234"]
    expected = "234"
    ctx = Context()
    code = transpile("Ṫ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = [1, 2]
    ctx = Context()
    code = transpile("Ṫ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_SplitAndKeepDelimiter():
    stack = ["a b c", " "]
    expected = ["a", " ", "b", " ", "c"]
    ctx = Context()
    code = transpile("Ẇ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["xyzabc123abc", "b"]
    expected = ["xyza", "b", "c123a", "b", "c"]
    ctx = Context()
    code = transpile("Ẇ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_CartesianProduct():
    stack = ["ab", "cd"]
    expected = ["ac", "ad", "bc", "bd"]
    ctx = Context()
    code = transpile("Ẋ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2], [3, 4]]
    expected = [[1, 3], [1, 4], [2, 3], [2, 4]]
    ctx = Context()
    code = transpile("Ẋ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_SliceUntil():
    stack = ["abc", 1]
    expected = "a"
    ctx = Context()
    code = transpile("Ẏ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 2]
    expected = [1, 2]
    ctx = Context()
    code = transpile("Ẏ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_SliceFromOneUntil():
    stack = ["abc", 2]
    expected = "b"
    ctx = Context()
    code = transpile("Ż")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3], 3]
    expected = [2, 3]
    ctx = Context()
    code = transpile("Ż")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Square():
    stack = [5]
    expected = 25
    ctx = Context()
    code = transpile("²")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello"]
    expected = ["hel", "lo"]
    ctx = Context()
    code = transpile("²")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["bye"]
    expected = ["by", "e"]
    ctx = Context()
    code = transpile("²")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = [1, 4, 9]
    ctx = Context()
    code = transpile("²")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Shift():
    stack = [1, 4, 5]
    expected = 4
    ctx = Context()
    code = transpile("∇")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["my", "hi", "bye"]
    expected = "hi"
    ctx = Context()
    code = transpile("∇")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Ceiling():
    stack = [5]
    expected = 5
    ctx = Context()
    code = transpile("⌈")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [4.5]
    expected = 5
    ctx = Context()
    code = transpile("⌈")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1.52, 2.9, 3.3]]
    expected = [2, 3, 4]
    ctx = Context()
    code = transpile("⌈")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello world"]
    expected = ["hello", "world"]
    ctx = Context()
    code = transpile("⌈")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Floor():
    stack = [5.3]
    expected = 5
    ctx = Context()
    code = transpile("⌊")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[5.3, 4.7]]
    expected = [4, 5]
    ctx = Context()
    code = transpile("⌊")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["123abc"]
    expected = 123
    ctx = Context()
    code = transpile("⌊")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Deltas():
    stack = [1, 2, 3]
    expected = [1, 1]
    ctx = Context()
    code = transpile("¯")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, 1, 1]
    expected = [0, 0]
    ctx = Context()
    code = transpile("¯")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [40, 61, 3]
    expected = [21, -58]
    ctx = Context()
    code = transpile("¯")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Sign():
    stack = [1]
    expected = 1
    ctx = Context()
    code = transpile("±")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hi"]
    expected = "hi"
    ctx = Context()
    code = transpile("±")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [-5]
    expected = -1
    ctx = Context()
    code = transpile("±")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [0]
    expected = 0
    ctx = Context()
    code = transpile("±")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_RightBitShift():
    stack = [4, 1]
    expected = 2
    ctx = Context()
    code = transpile("↳")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [8, "green"]
    expected = "   green"
    ctx = Context()
    code = transpile("↳")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello", "cheeseburger"]
    expected = "       hello"
    ctx = Context()
    code = transpile("↳")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_LeftBitShift():
    stack = [4, 1]
    expected = 8
    ctx = Context()
    code = transpile("↲")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [8, "green"]
    expected = "green   "
    ctx = Context()
    code = transpile("↲")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello", "cheeseburger"]
    expected = "hello       "
    ctx = Context()
    code = transpile("↲")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_BitwiseAnd():
    stack = [420, 69]
    expected = 4
    ctx = Context()
    code = transpile("⋏")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc", 10]
    expected = "   abc    "
    ctx = Context()
    code = transpile("⋏")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["no", "yesnt"]
    expected = " no "
    ctx = Context()
    code = transpile("⋏")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_BitwiseOr():
    stack = [420, 69]
    expected = 485
    ctx = Context()
    code = transpile("⋎")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [2, "abc"]
    expected = "ab"
    ctx = Context()
    code = transpile("⋎")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc", 2]
    expected = "ab"
    ctx = Context()
    code = transpile("⋎")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["Hello", "lower"]
    expected = "Hellower"
    ctx = Context()
    code = transpile("⋎")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_BitwiseXor():
    stack = [420, 69]
    expected = 481
    ctx = Context()
    code = transpile("꘍")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [5, "ab"]
    expected = "     ab"
    ctx = Context()
    code = transpile("꘍")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["ab", 5]
    expected = "ab     "
    ctx = Context()
    code = transpile("꘍")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["atoll", "bowl"]
    expected = 3
    ctx = Context()
    code = transpile("꘍")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_BitwiseNot():
    stack = [220]
    expected = -221
    ctx = Context()
    code = transpile("ꜝ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["Hello"]
    expected = 1
    ctx = Context()
    code = transpile("ꜝ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_RandomChoice():
    stack = [[1, 2, 3]]
    expected = 2
    ctx = Context()
    code = transpile("℅")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = 1
    ctx = Context()
    code = transpile("℅")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = 3
    ctx = Context()
    code = transpile("℅")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_LesserThanorEqualTo():
    stack = [1, 2]
    expected = 1
    ctx = Context()
    code = transpile("≤")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_GreaterThanorEqualTo():
    stack = [1, 2]
    expected = 0
    ctx = Context()
    code = transpile("≥")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_NotEqualTo():
    stack = [1, 2]
    expected = 1
    ctx = Context()
    code = transpile("≠")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_ExactlyEqualTo():
    stack = [1, 2]
    expected = 1
    ctx = Context()
    code = transpile("⁼")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Fractionify():
    stack = [0.5]
    expected = [1, 2]
    ctx = Context()
    code = transpile("ƒ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = "0.3"
    expected = [3, 10]
    ctx = Context()
    code = transpile("ƒ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Decimalify():
    stack = [1, 2]
    expected = 0.5
    ctx = Context()
    code = transpile("ɖ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3, 4]
    expected = 0.75
    ctx = Context()
    code = transpile("ɖ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_SetUnion():
    stack = [[1, 2], [2, 3, 4]]
    expected = [1, 2, 3, 4]
    ctx = Context()
    code = transpile("∪")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_SetIntersection():
    stack = [[1, 2], [2, 3, 4]]
    expected = [2]
    ctx = Context()
    code = transpile("∩")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_SymmetricSetdifference():
    stack = [[1, 2], [2, 3, 4]]
    expected = [1, 3, 4]
    ctx = Context()
    code = transpile("⊍")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_GradeUp():
    stack = [[420, 69, 1337]]
    expected = [2, 1, 3]
    ctx = Context()
    code = transpile("⇧")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["Heloo"]
    expected = "HELOO"
    ctx = Context()
    code = transpile("⇧")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [4]
    expected = 6
    ctx = Context()
    code = transpile("⇧")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_GradeDown():
    stack = [[420, 69, 1337]]
    expected = [3, 1, 2]
    ctx = Context()
    code = transpile("⇩")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["Heloo"]
    expected = "heloo"
    ctx = Context()
    code = transpile("⇩")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [4]
    expected = 2
    ctx = Context()
    code = transpile("⇩")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Removenonalphabets():
    stack = ["Helo1233adc__"]
    expected = "Heloadc"
    ctx = Context()
    code = transpile("Ǎ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [8]
    expected = 256
    ctx = Context()
    code = transpile("Ǎ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Nthprime():
    stack = [3]
    expected = 7
    ctx = Context()
    code = transpile("ǎ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc"]
    expected = ["a", "ab", "abc", "", "b", "bc", "", "", "c", "", "", ""]
    ctx = Context()
    code = transpile("ǎ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Primefactorization():
    stack = [45]
    expected = [3, 5]
    ctx = Context()
    code = transpile("Ǐ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc"]
    expected = "abca"
    ctx = Context()
    code = transpile("Ǐ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Primefactors():
    stack = [45]
    expected = [3, 3, 5]
    ctx = Context()
    code = transpile("ǐ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abc def"]
    expected = "Abc Def"
    ctx = Context()
    code = transpile("ǐ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Multiplicity():
    stack = [45, 3]
    expected = 2
    ctx = Context()
    code = transpile("Ǒ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["aaabbbc", "ab"]
    expected = "c"
    ctx = Context()
    code = transpile("Ǒ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Modulo3():
    stack = [45]
    expected = 0
    ctx = Context()
    code = transpile("ǒ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [""]
    expected = 1
    ctx = Context()
    code = transpile("ǒ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_RotateLeft():
    stack = [3, [4, 5, 5, 6]]
    expected = [6, 4, 5, 5]
    ctx = Context()
    code = transpile("Ǔ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3, [1, 2, 3, 4]]
    expected = [2, 3, 4, 1]
    ctx = Context()
    code = transpile("Ǔ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_RotateRight():
    stack = [3, [4, 5, 5, 6]]
    expected = [5, 5, 6, 4]
    ctx = Context()
    code = transpile("ǔ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3, [1, 2, 3, 4]]
    expected = [4, 1, 2, 3]
    ctx = Context()
    code = transpile("ǔ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_SplitOnnewlines():
    stack = ["a\nb\nc"]
    expected = ["a", "b", "c"]
    ctx = Context()
    code = transpile("↵")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [3]
    expected = 1000
    ctx = Context()
    code = transpile("↵")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_ProductofArray():
    stack = [3, 4, 5]
    expected = 60
    ctx = Context()
    code = transpile("Π")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Uppercasealphabet():
    stack = []
    expected = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    ctx = Context()
    code = transpile("kA")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_eEulersnumber():
    stack = []
    expected = 2.718281828459045
    ctx = Context()
    code = transpile("ke")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Fizz():
    stack = []
    expected = "Fizz"
    ctx = Context()
    code = transpile("kf")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Buzz():
    stack = []
    expected = "Buzz"
    ctx = Context()
    code = transpile("kb")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_FizzBuzz():
    stack = []
    expected = "FizzBuzz"
    ctx = Context()
    code = transpile("kF")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_HelloWorld():
    stack = []
    expected = "Hello, World!"
    ctx = Context()
    code = transpile("kH")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_HelloWorld():
    stack = []
    expected = "Hello World"
    ctx = Context()
    code = transpile("kh")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_1000():
    stack = []
    expected = 1000
    ctx = Context()
    code = transpile("k1")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_1000():
    stack = []
    expected = 10000
    ctx = Context()
    code = transpile("k2")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_10000():
    stack = []
    expected = 100000
    ctx = Context()
    code = transpile("k3")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_1000000():
    stack = []
    expected = 1000000
    ctx = Context()
    code = transpile("k4")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Lowercasealphabet():
    stack = []
    expected = "abcdefghijklmnopqrstuvwxyz"
    ctx = Context()
    code = transpile("ka")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Lowercaseanduppercasealphabet():
    stack = []
    expected = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    ctx = Context()
    code = transpile("kL")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Digits():
    stack = []
    expected = "0123456789"
    ctx = Context()
    code = transpile("kd")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Hexdigitslowercase():
    stack = []
    expected = "0123456789abcdef"
    ctx = Context()
    code = transpile("k6")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Hexdigitsuppercase():
    stack = []
    expected = "0123456789ABCDEF"
    ctx = Context()
    code = transpile("k^")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Octaldigits():
    stack = []
    expected = "01234567"
    ctx = Context()
    code = transpile("ko")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Punctuation():
    stack = []
    expected = string.punctuation
    ctx = Context()
    code = transpile("kp")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_PrintableASCII():
    stack = []
    expected = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"
    ctx = Context()
    code = transpile("kP")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Digitslowercasealphabetanduppercasealphabet():
    stack = []
    expected = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    ctx = Context()
    code = transpile("kr")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Uppercaseandlowercasealphabet():
    stack = []
    expected = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    ctx = Context()
    code = transpile("kB")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Uppercasealphabetreversed():
    stack = []
    expected = "ZYXWVUTSRQPONMLKJIHGFEDCBA"
    ctx = Context()
    code = transpile("kZ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Lowercasealphabetreversed():
    stack = []
    expected = "zyxwvutsrqponmlkjihgfedcba"
    ctx = Context()
    code = transpile("kz")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Uppercaseandlowercasealphabetreversed():
    stack = []
    expected = "ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba"
    ctx = Context()
    code = transpile("kl")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Pi():
    stack = []
    expected = 3.141592653589793
    ctx = Context()
    code = transpile("ki")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_NaN():
    stack = []
    expected = math.nan
    ctx = Context()
    code = transpile("kn")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Goldenratiophi():
    stack = []
    expected = 1.618033988749895
    ctx = Context()
    code = transpile("kg")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Squarerootof2():
    stack = []
    expected = 1.4142135623730951
    ctx = Context()
    code = transpile("k₂")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Squarerootof3():
    stack = []
    expected = 1.7320508075688772
    ctx = Context()
    code = transpile("k₃")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Squarerootof5():
    stack = []
    expected = 2.23606797749979
    ctx = Context()
    code = transpile("k₅")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Bracessquarebracketsanglebracketsandparentheses():
    stack = []
    expected = "{}[]<>()"
    ctx = Context()
    code = transpile("kβ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Parenthesessquarebracketsandbraces():
    stack = []
    expected = "()[]{}"
    ctx = Context()
    code = transpile("kḂ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Parenthesesandsquarebrackets():
    stack = []
    expected = "()[]"
    ctx = Context()
    code = transpile("kß")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Openingbrackets():
    stack = []
    expected = "([{"
    ctx = Context()
    code = transpile("kḃ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Closingbrackets():
    stack = []
    expected = ")]}"
    ctx = Context()
    code = transpile("k≥")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Openingbracketswith():
    stack = []
    expected = "([{<"
    ctx = Context()
    code = transpile("k≤")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Closingbracketswith():
    stack = []
    expected = ")]}>"
    ctx = Context()
    code = transpile("kΠ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Lowercasevowels():
    stack = []
    expected = "aeiou"
    ctx = Context()
    code = transpile("kv")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Upercasevowels():
    stack = []
    expected = "AEIOU"
    ctx = Context()
    code = transpile("kV")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Lowercaseanduppercasevowels():
    stack = []
    expected = "aeiouAEIOU"
    ctx = Context()
    code = transpile("k∨")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_12():
    stack = []
    expected = [1, 2]
    ctx = Context()
    code = transpile("k½")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_4294967296():
    stack = []
    expected = 4294967296
    ctx = Context()
    code = transpile("kḭ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_11():
    stack = []
    expected = [1, -1]
    ctx = Context()
    code = transpile("k+")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_11():
    stack = []
    expected = [-1, 1]
    ctx = Context()
    code = transpile("k-")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_01():
    stack = []
    expected = [0, 1]
    ctx = Context()
    code = transpile("k≈")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Slashes():
    stack = []
    expected = "/\\"
    ctx = Context()
    code = transpile("k/")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_360():
    stack = []
    expected = 360
    ctx = Context()
    code = transpile("kR")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_https():
    stack = []
    expected = "https://"
    ctx = Context()
    code = transpile("kW")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_http():
    stack = []
    expected = "http://"
    ctx = Context()
    code = transpile("k℅")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_httpswww():
    stack = []
    expected = "https://www."
    ctx = Context()
    code = transpile("k↳")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_httpwww():
    stack = []
    expected = "http://www."
    ctx = Context()
    code = transpile("k²")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_512():
    stack = []
    expected = 512
    ctx = Context()
    code = transpile("k¶")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_1024():
    stack = []
    expected = 1024
    ctx = Context()
    code = transpile("k⁋")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_2048():
    stack = []
    expected = 2048
    ctx = Context()
    code = transpile("k¦")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_4096():
    stack = []
    expected = 4096
    ctx = Context()
    code = transpile("kṄ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_8192():
    stack = []
    expected = 8192
    ctx = Context()
    code = transpile("kṅ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_16384():
    stack = []
    expected = 16384
    ctx = Context()
    code = transpile("k¡")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_32768():
    stack = []
    expected = 32768
    ctx = Context()
    code = transpile("kε")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_65536():
    stack = []
    expected = 65536
    ctx = Context()
    code = transpile("k₴")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_2147483648():
    stack = []
    expected = 2147483648
    ctx = Context()
    code = transpile("k×")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Lowercaseconsonantswithy():
    stack = []
    expected = "bcfghjklmnpqrstvwxyz"
    ctx = Context()
    code = transpile("k⁰")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_BFcommandset():
    stack = []
    expected = "[]<>-+.,"
    ctx = Context()
    code = transpile("kT")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Bracketpairlist():
    stack = []
    expected = ["()", "[]", "{}", "<>"]
    ctx = Context()
    code = transpile("kṗ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Nestedbrackets():
    stack = []
    expected = "([{<>}])"
    ctx = Context()
    code = transpile("kṖ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Amogus():
    stack = []
    expected = "ඞ"
    ctx = Context()
    code = transpile("kS")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_220():
    stack = []
    expected = 1048576
    ctx = Context()
    code = transpile("k₂")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_230():
    stack = []
    expected = 1073741824
    ctx = Context()
    code = transpile("k₃")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_LowercaseVowelsWithY():
    stack = []
    expected = "aeiouy"
    ctx = Context()
    code = transpile("k∪")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_UppercaseVowelsWithY():
    stack = []
    expected = "AEIOUY"
    ctx = Context()
    code = transpile("k⊍")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_VowelsWithY():
    stack = []
    expected = "aeiouyAEIOUY"
    ctx = Context()
    code = transpile("k∩")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Parenthesise():
    stack = ["xyz"]
    expected = "(xyz)"
    ctx = Context()
    code = transpile("øb")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [5]
    expected = "(5)"
    ctx = Context()
    code = transpile("øb")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = ["(1)", "(2)", "(3)"]
    ctx = Context()
    code = transpile("øb")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Bracketify():
    stack = ["xyz"]
    expected = "[xyz]"
    ctx = Context()
    code = transpile("øB")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [5]
    expected = "[5]"
    ctx = Context()
    code = transpile("øB")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = ["[1]", "[2]", "[3]"]
    ctx = Context()
    code = transpile("øB")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_CurlyBracketify():
    stack = ["xyz"]
    expected = "{xyz}"
    ctx = Context()
    code = transpile("øḃ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [5]
    expected = "{5}"
    ctx = Context()
    code = transpile("øḃ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = ["{1}", "{2}", "{3}"]
    ctx = Context()
    code = transpile("øḃ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_AngleBracketify():
    stack = ["xyz"]
    expected = "<xyz>"
    ctx = Context()
    code = transpile("øḂ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [5]
    expected = "<5>"
    ctx = Context()
    code = transpile("øḂ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[1, 2, 3]]
    expected = ["<1>", "<2>", "<3>"]
    ctx = Context()
    code = transpile("øḂ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_BalancedBrackets():
    stack = ["xyz"]
    expected = 1
    ctx = Context()
    code = transpile("øβ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["([)]"]
    expected = 0
    ctx = Context()
    code = transpile("øβ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["({<[]>})"]
    expected = 1
    ctx = Context()
    code = transpile("øβ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [")("]
    expected = 0
    ctx = Context()
    code = transpile("øβ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_CustomPadLeft():
    stack = ["xyz", "x", 4]
    expected = "xxyz"
    ctx = Context()
    code = transpile("ø↳")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["123", "&", 8]
    expected = "&&&&&123"
    ctx = Context()
    code = transpile("ø↳")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["324", " ", 2]
    expected = "324"
    ctx = Context()
    code = transpile("ø↳")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_CustomPadRight():
    stack = ["xyz", "x", 4]
    expected = "xyzx"
    ctx = Context()
    code = transpile("ø↲")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["123", "&", 8]
    expected = "123&&&&&"
    ctx = Context()
    code = transpile("ø↲")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["324", " ", 2]
    expected = "324"
    ctx = Context()
    code = transpile("ø↲")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_RingTranslate():
    stack = ["aba", "ba"]
    expected = "bab"
    ctx = Context()
    code = transpile("øĿ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello", "lo"]
    expected = "heool"
    ctx = Context()
    code = transpile("øĿ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello, world", "aeiou"]
    expected = "hillu, wurld"
    ctx = Context()
    code = transpile("øĿ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_IsVowel():
    stack = ["a"]
    expected = 1
    ctx = Context()
    code = transpile("øv")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["y"]
    expected = 0
    ctx = Context()
    code = transpile("øv")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hi"]
    expected = [0, 1]
    ctx = Context()
    code = transpile("øv")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_IsUppercase():
    stack = ["A"]
    expected = 1
    ctx = Context()
    code = transpile("øċ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["a"]
    expected = 0
    ctx = Context()
    code = transpile("øċ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["Hi"]
    expected = [1, 0]
    ctx = Context()
    code = transpile("øċ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_FlipBracketsPalindromise():
    stack = ["(x"]
    expected = "(x)"
    ctx = Context()
    code = transpile("øM")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["{] "]
    expected = "{] [}"
    ctx = Context()
    code = transpile("øM")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["/*>X"]
    expected = "/*>X<*\\"
    ctx = Context()
    code = transpile("øM")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_RemoveUntilNochange():
    stack = ["((()))", "()"]
    expected = ""
    ctx = Context()
    code = transpile("øo")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["--+--+-", ["--", "+-"]]
    expected = "+"
    ctx = Context()
    code = transpile("øo")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_ReplaceUntilNoChange():
    stack = ["xyzzzzz", "yzz", "yyyz"]
    expected = "xyyyyyyyyyz"
    ctx = Context()
    code = transpile("øV")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["abb", "ab", "aa"]
    expected = "aaa"
    ctx = Context()
    code = transpile("øV")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_StringCompress():
    stack = ["hello"]
    expected = "«B²z«"
    ctx = Context()
    code = transpile("øc")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello world"]
    expected = "«⟇÷Ċ$⌈¢2«"
    ctx = Context()
    code = transpile("øc")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_NumberCompress():
    stack = [234]
    expected = "»⇧»"
    ctx = Context()
    code = transpile("øC")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [27914632409837421]
    expected = "»fðǐ4'∞Ẏ»"
    ctx = Context()
    code = transpile("øC")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Center():
    stack = [["ab", "cdef"]]
    expected = [" ab ", "cdef"]
    ctx = Context()
    code = transpile("øĊ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [["xyz", "a", "bcdef"]]
    expected = [" xyz ", "  a  ", "bcdef"]
    ctx = Context()
    code = transpile("øĊ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_RunLengthEncoding():
    stack = ["abc"]
    expected = [["a", 1], ["b", 1], ["c", 1]]
    ctx = Context()
    code = transpile("øe")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["aaa"]
    expected = [["a", 3]]
    ctx = Context()
    code = transpile("øe")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_RunLengthDecoding():
    stack = [[["x", 3]]]
    expected = "xxx"
    ctx = Context()
    code = transpile("ød")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [[["z", 2], ["a", 3]]]
    expected = "zzaaa"
    ctx = Context()
    code = transpile("ød")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_DictionaryCompression():
    stack = ["withree"]
    expected = "`wi∧ḭ`"
    ctx = Context()
    code = transpile("øD")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello"]
    expected = "`ƈṙ`"
    ctx = Context()
    code = transpile("øD")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["Vyxal"]
    expected = "`₴ŀ`"
    ctx = Context()
    code = transpile("øD")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Grouponwords():
    stack = ["abc*xyz"]
    expected = ["abc", "*", "xyz"]
    ctx = Context()
    code = transpile("øW")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["$$$"]
    expected = ["$", "$", "$"]
    ctx = Context()
    code = transpile("øW")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Regexreplace():
    stack = [".{3}", "hello", "x"]
    expected = "xlo"
    ctx = Context()
    code = transpile("øṙ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["\W", "Hello, World!", "E"]
    expected = "HelloEEWorldE"
    ctx = Context()
    code = transpile("øṙ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_Palindromise():
    stack = [[1, 2, 3]]
    expected = [1, 2, 3, 2, 1]
    ctx = Context()
    code = transpile("øm")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello"]
    expected = "hellolleh"
    ctx = Context()
    code = transpile("øm")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_StartsWith():
    stack = ["hello", "h"]
    expected = 1
    ctx = Context()
    code = transpile("øp")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello", "hello"]
    expected = 1
    ctx = Context()
    code = transpile("øp")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello", "x"]
    expected = 0
    ctx = Context()
    code = transpile("øp")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["hello", ""]
    expected = 1
    ctx = Context()
    code = transpile("øp")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_PluraliseCount():
    stack = [4, "hello"]
    expected = "4 hellos"
    ctx = Context()
    code = transpile("øP")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [1, "hello"]
    expected = "1 hello"
    ctx = Context()
    code = transpile("øP")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [0, "hello"]
    expected = "0 hellos"
    ctx = Context()
    code = transpile("øP")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_VerticalMirror():
    pass  # TODO implement this test!!!


def test_FlipBracketsVerticalMirror():
    stack = ["[}"]
    expected = "[}{]"
    ctx = Context()
    code = transpile("øṀ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = [")X"]
    expected = ")XX("
    ctx = Context()
    code = transpile("øṀ")
    exec(code)
    assert simplify(stack[-1]) == expected

    stack = ["</tag>"]
    expected = "</tag><gat\>"
    ctx = Context()
    code = transpile("øṀ")
    exec(code)
    assert simplify(stack[-1]) == expected


def test_VerticalMirrorCustomMapping():
    pass  # TODO implement this test!!!
