import os, sys

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + '/..'
sys.path.insert(1, THIS_FOLDER)

from vyxal.transpile import *
from vyxal.context import Context
from vyxal.elements import *
from vyxal.LazyList import *
def test_LogicalNot():
	ctx = Context()
	ctx.stack = [1]
	expected = 0
	code = transpile('¬');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [0]
	expected = 1
	code = transpile('¬');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc"]
	expected = 0
	code = transpile('¬');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [""]
	expected = 1
	code = transpile('¬');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 0
	code = transpile('¬');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[]]
	expected = 1
	code = transpile('¬');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_LogicalAnd():
	ctx = Context()
	ctx.stack = [0, 0]
	expected = 0
	code = transpile('∧');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["", 1]
	expected = ""
	code = transpile('∧');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3], 0]
	expected = 0
	code = transpile('∧');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1, 2]
	expected = 2
	code = transpile('∧');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_ReversedLogicalAnd():
	ctx = Context()
	ctx.stack = [0, 0]
	expected = 0
	code = transpile('⟑');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["", 1]
	expected = ""
	code = transpile('⟑');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3], 0]
	expected = 0
	code = transpile('⟑');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1, 2]
	expected = 1
	code = transpile('⟑');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_LogicalOr():
	ctx = Context()
	ctx.stack = [0, 0]
	expected = 0
	code = transpile('∨');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["", 1]
	expected = 1
	code = transpile('∨');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3], 0]
	expected = [1,2,3]
	code = transpile('∨');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1, 2]
	expected = 1
	code = transpile('∨');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_ReversedLogicalOr():
	ctx = Context()
	ctx.stack = [0, 0]
	expected = 0
	code = transpile('⟇');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["", 1]
	expected = 1
	code = transpile('⟇');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3], 0]
	expected = [1,2,3]
	code = transpile('⟇');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1, 2]
	expected = 2
	code = transpile('⟇');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_ItemSplit():
	ctx = Context()
	ctx.stack = [123456]
	expected = 6
	code = transpile('÷');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc"]
	expected = "c"
	code = transpile('÷');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 3
	code = transpile('÷');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_AsteriskLiteral():
	ctx = Context()
	ctx.stack = []
	expected = "*"
	code = transpile('×');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_MultiCommand():
	ctx = Context()
	ctx.stack = [8, 2]
	expected = 3.0
	code = transpile('•');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abcde", 4]
	expected = "aaaabbbbccccddddeeee"
	code = transpile('•');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abcde", "FgHIj"]
	expected = "AbCDe"
	code = transpile('•');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3,4,5,6,7], [[8, 9], 10, 11, 12, [13, 14]]]
	expected = [[1, 2], 3, 4, 5, [6, 7]]
	code = transpile('•');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_FunctionCall():
	ctx = Context()
	ctx.stack = [12]
	expected = 2
	code = transpile('†');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1, 0, 1]]
	expected = [0, 1, 0]
	code = transpile('†');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_SplitOn():
	ctx = Context()
	ctx.stack = [1231234, 3]
	expected = ["12", "12", "4"]
	code = transpile('€');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc3def", 3]
	expected = ["abc", "def"]
	code = transpile('€');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1, 2, 3, 4, 3, 2, 1], 4]
	expected = [[1, 2, 3], [3, 2, 1]]
	code = transpile('€');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Halve():
	ctx = Context()
	ctx.stack = [8]
	expected = 4
	code = transpile('½');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["FizzBuzz"]
	expected = ["Fizz", "Buzz"]
	code = transpile('½');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[2, 4, 6, 8]]
	expected = [1, 2, 3, 4]
	code = transpile('½');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_CombinationsRemoveFixedPointCollection():
	ctx = Context()
	ctx.stack = ["cabbage", "abcde"]
	expected = "cabbae"
	code = transpile('↔');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,3,5,6,7,7,1],[1,3,5]]
	expected = [1,3,5,1]
	code = transpile('↔');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2],2]
	expected = [[1,1],[1,2],[2,1],[2,2]]
	code = transpile('↔');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_InfiniteReplacement():
	ctx = Context()
	ctx.stack = ["{[[[]]]}","[]",""]
	expected = "{}"
	code = transpile('¢');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1444,44,34]
	expected = 1334
	code = transpile('¢');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_ComplementCommaSplit():
	ctx = Context()
	ctx.stack = [5]
	expected = -4
	code = transpile('⌐');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [-5]
	expected = 6
	code = transpile('⌐');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["a,b,c"]
	expected = ["a","b","c"]
	code = transpile('⌐');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_IsPrimeCaseCheck():
	ctx = Context()
	ctx.stack = [2]
	expected = 1
	code = transpile('æ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [4]
	expected = 0
	code = transpile('æ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["a"]
	expected = 0
	code = transpile('æ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["A"]
	expected = 1
	code = transpile('æ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["!"]
	expected = -1
	code = transpile('æ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_InclusiveZeroRange():
	ctx = Context()
	ctx.stack = ["a$c"]
	expected = [1, 0, 1]
	code = transpile('ʀ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1]]
	expected = [[0, 1]]
	code = transpile('ʀ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3]
	expected = [0,1,2,3]
	code = transpile('ʀ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_ExclusiveZeroRange():
	ctx = Context()
	ctx.stack = ["1234"]
	expected = "1234321"
	code = transpile('ʁ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1]]
	expected = [[0]]
	code = transpile('ʁ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3]
	expected = [0,1,2]
	code = transpile('ʁ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_InclusiveOneRange():
	ctx = Context()
	ctx.stack = ["abc"]
	expected = "ABC"
	code = transpile('ɾ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[4, 5]]
	expected = [[1, 2, 3, 4], [1, 2, 3, 4, 5]]
	code = transpile('ɾ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3]
	expected = [1,2,3]
	code = transpile('ɾ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_ExclusiveOneRangeLowercase():
	ctx = Context()
	ctx.stack = ["1aBC"]
	expected = "1abc"
	code = transpile('ɽ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[0]]
	expected = [[]]
	code = transpile('ɽ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3]
	expected = [1,2]
	code = transpile('ɽ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Chooserandomchoicesetsame():
	ctx = Context()
	ctx.stack = [5,3]
	expected = 10
	code = transpile('ƈ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc","aaccb"]
	expected = 1
	code = transpile('ƈ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc","abcd"]
	expected = 0
	code = transpile('ƈ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Palindromise():
	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = [1,2,3,2,1]
	code = transpile('∞');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3,4]]
	expected = [1,2,3,4,3,2,1]
	code = transpile('∞');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3,4,5]]
	expected = [1,2,3,4,5,4,3,2,1]
	code = transpile('∞');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3,4,5,6]]
	expected = [1,2,3,4,5,6,5,4,3,2,1]
	code = transpile('∞');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello"]
	expected = "hellolleh"
	code = transpile('∞');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_StackLength():
	ctx = Context()
	ctx.stack = [0,1,2]
	expected = 3
	code = transpile('!');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1,1,1,1,1]
	expected = 5
	code = transpile('!');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = []
	expected = 0
	code = transpile('!');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Pair():
	ctx = Context()
	ctx.stack = [1, 2]
	expected = [1, 2]
	code = transpile('"');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1, 2, 3]
	expected = [2, 3]
	code = transpile('"');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1, 2, 3], "abc", 3]
	expected = ["abc", 3]
	code = transpile('"');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Swap():
	ctx = Context()
	ctx.stack = [1, 2]
	expected = 1
	code = transpile('$');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1, 2, 3]
	expected = 2
	code = transpile('$');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1, 2, 3], "abc", 3]
	expected = "abc"
	code = transpile('$');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_ModuloFormat():
	ctx = Context()
	ctx.stack = [5,3]
	expected = 2
	code = transpile('%');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello!",3]
	expected = "o!"
	code = transpile('%');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["Hel%ld!","lo, Wor"]
	expected = "Hello, World!"
	code = transpile('%');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["% and % and %",[1,2,3]]
	expected = "1 and 2 and 3"
	code = transpile('%');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Multiplication():
	ctx = Context()
	ctx.stack = [3,5]
	expected = 15
	code = transpile('*');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [4,-2]
	expected = -8
	code = transpile('*');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [4,"*"]
	expected = "****"
	code = transpile('*');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["x",5]
	expected = "xxxxx"
	code = transpile('*');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["aeiou","hello"]
	expected = "hillu"
	code = transpile('*');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Addition():
	ctx = Context()
	ctx.stack = [1, 1]
	expected = 2
	code = transpile('+');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [0, -5]
	expected = -5
	code = transpile('+');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc", 5]
	expected = "abc5"
	code = transpile('+');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [5, "abc"]
	expected = "5abc"
	code = transpile('+');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["Hello, ", "World!"]
	expected = "Hello, World!"
	code = transpile('+');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3], 4]
	expected = [5, 6, 7]
	code = transpile('+');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3], [4,5,6]]
	expected = [5, 7, 9]
	code = transpile('+');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Subtract():
	ctx = Context()
	ctx.stack = [5, 4]
	expected = 1
	code = transpile('-');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [0, -5]
	expected = 5
	code = transpile('-');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["|", 5]
	expected = "|-----"
	code = transpile('-');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3, "> arrow"]
	expected = "---> arrow"
	code = transpile('-');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abcbde", "b"]
	expected = "acde"
	code = transpile('-');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["aaa", "a"]
	expected = ""
	code = transpile('-');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1, 2, 3], [1, 2, 3]]
	expected = [0, 0, 0]
	code = transpile('-');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[10, 20, 30], 5]
	expected = [5, 15, 25]
	code = transpile('-');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_DivideSplit():
	ctx = Context()
	ctx.stack = [4,2]
	expected = 2
	code = transpile('/');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abcdef",3]
	expected = ["ab","cd","ef"]
	code = transpile('/');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["1,2,3",","]
	expected = ["1","2","3"]
	code = transpile('/');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_LessThan():
	ctx = Context()
	ctx.stack = [1, 2]
	expected = 1
	code = transpile('<');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [2, 1]
	expected = 0
	code = transpile('<');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["a","b"]
	expected = 1
	code = transpile('<');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [-5,2]
	expected = 1
	code = transpile('<');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3],2]
	expected = [1,0,0]
	code = transpile('<');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Equals():
	ctx = Context()
	ctx.stack = [1, 1]
	expected = 1
	code = transpile('=');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [2, 1]
	expected = 0
	code = transpile('=');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["a","b"]
	expected = 0
	code = transpile('=');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["xyz","xyz"]
	expected = 1
	code = transpile('=');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3],2]
	expected = [0,1,0]
	code = transpile('=');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1,"1"]
	expected = 1
	code = transpile('=');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_GreaterThan():
	ctx = Context()
	ctx.stack = [1, 2]
	expected = 0
	code = transpile('>');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [2, 1]
	expected = 1
	code = transpile('>');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["a","b"]
	expected = 0
	code = transpile('>');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [2,-5]
	expected = 1
	code = transpile('>');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3],2]
	expected = [0,0,1]
	code = transpile('>');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["5",10]
	expected = 1
	code = transpile('>');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_All():
	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 1
	code = transpile('A');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[0,1,2]]
	expected = 0
	code = transpile('A');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [["",1,2]]
	expected = 0
	code = transpile('A');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[]]
	expected = 1
	code = transpile('A');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [""]
	expected = []
	code = transpile('A');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [0]
	expected = 0
	code = transpile('A');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["a"]
	expected = 1
	code = transpile('A');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["y"]
	expected = 0
	code = transpile('A');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hi"]
	expected = [0,1]
	code = transpile('A');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_BinaryToDecimal():
	ctx = Context()
	ctx.stack = [[1,0,1]]
	expected = 5
	code = transpile('B');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,1,1]]
	expected = 7
	code = transpile('B');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["1011"]
	expected = 11
	code = transpile('B');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_ChrOrd():
	ctx = Context()
	ctx.stack = [65]
	expected = "A"
	code = transpile('C');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [8482]
	expected = "™"
	code = transpile('C');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["Z"]
	expected = 90
	code = transpile('C');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["ABC"]
	expected = [65,66,67]
	code = transpile('C');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[123,124,125]]
	expected = ["{","|","}"]
	code = transpile('C');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_TwoPowerPythonEval():
	ctx = Context()
	ctx.stack = [0]
	expected = 1
	code = transpile('E');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [2]
	expected = 4
	code = transpile('E');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["[1,2,3]"]
	expected = [1,2,3]
	code = transpile('E');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Filter():
	ctx = Context()
	ctx.stack = [[1,2,3],[2,4,6]]
	expected = [1,3]
	code = transpile('F');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abcdef","daffodil"]
	expected = "bce"
	code = transpile('F');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Max():
	ctx = Context()
	ctx.stack = [[1,3,2]]
	expected = 3
	code = transpile('G');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["python"]
	expected = "y"
	code = transpile('G');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_HexToDecimal():
	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 291
	code = transpile('H');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["b"]
	expected = 11
	code = transpile('H');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["beedab"]
	expected = 12512683
	code = transpile('H');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Int():
	ctx = Context()
	ctx.stack = ["5"]
	expected = 5
	code = transpile('I');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [5]
	expected = 5
	code = transpile('I');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[5]]
	expected = 5
	code = transpile('I');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Join():
	ctx = Context()
	ctx.stack = [[1,2,3],4]
	expected = [1,2,3,4]
	code = transpile('J');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc","def"]
	expected = "abcdef"
	code = transpile('J');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1,[2,3,4]]
	expected = [1,2,3,4]
	code = transpile('J');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2],[3,4]]
	expected = [1,2,3,4]
	code = transpile('J');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_FactorsSubstringsPrefixes():
	ctx = Context()
	ctx.stack = [20]
	expected = [1,2,4,5,10,20]
	code = transpile('K');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1]
	expected = [1]
	code = transpile('K');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["adbcdbcd"]
	expected = {"b","c","d","bc","cd","bcd","db","dbc"}
	code = transpile('K');print(code)
	exec(code)
	assert set(simplify(ctx.stack[-1])) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = [[1],[1,2],[1,2,3]]
	code = transpile('K');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Length():
	ctx = Context()
	ctx.stack = ["abc"]
	expected = 3
	code = transpile('L');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 3
	code = transpile('L');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,"wrfwerfgbr",6]]
	expected = 4
	code = transpile('L');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Map():
	ctx = Context()
	ctx.stack = [5,[1,2,3]]
	expected = [[5,1],[5,2],[5,3]]
	code = transpile('M');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["z","hi"]
	expected = [["z","h"],["z","i"]]
	code = transpile('M');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_NegateSwapCase():
	ctx = Context()
	ctx.stack = [5]
	expected = -5
	code = transpile('N');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [-1]
	expected = 1
	code = transpile('N');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["a"]
	expected = "A"
	code = transpile('N');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["aBc"]
	expected = "AbC"
	code = transpile('N');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Count():
	ctx = Context()
	ctx.stack = [[1,2,3,4,5,4,3], 4]
	expected = 2
	code = transpile('O');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abcdbacsabdcabca","a"]
	expected = 5
	code = transpile('O');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Strip():
	ctx = Context()
	ctx.stack = [[1, 2, 3, 4, 5, 4, 3, 2, 1], [1, 2]]
	expected = [3, 4, 5, 4, 3]
	code = transpile('P');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["    Hello, World!    ", " "]
	expected = "Hello, World!"
	code = transpile('P');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Reduce():
	ctx = Context()
	ctx.stack = [[[1,2],[3,4]]]
	expected = [[2,1],[4,3]]
	code = transpile('R');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[[1,2]]]
	expected = [[2,1]]
	code = transpile('R');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Stringify():
	ctx = Context()
	ctx.stack = [5]
	expected = "5"
	code = transpile('S');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = "⟨1|2|3⟩"
	code = transpile('S');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["X"]
	expected = "X"
	code = transpile('S');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_TruthyIndices():
	ctx = Context()
	ctx.stack = [[0,1,0,2]]
	expected = [1,3]
	code = transpile('T');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3,4]]
	expected = [0,1,2,3]
	code = transpile('T');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Uniquify():
	ctx = Context()
	ctx.stack = [[1,3,5,5]]
	expected = [1,3,5]
	code = transpile('U');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abdbcdbch"]
	expected = "abdch"
	code = transpile('U');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Replace():
	ctx = Context()
	ctx.stack = ["hela","a","lo"]
	expected = "hello"
	code = transpile('V');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["banana","n","nan"]
	expected = "banananana"
	code = transpile('V');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Wrap():
	ctx = Context()
	ctx.stack = [1,2,3]
	expected = [1,2,3]
	code = transpile('W');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = []
	expected = []
	code = transpile('W');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello",1,9]
	expected = ["hello",1,9]
	code = transpile('W');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Interleave():
	ctx = Context()
	ctx.stack = [[1,3,5],[2,4]]
	expected = [1,2,3,4,5]
	code = transpile('Y');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["srn","tig"]
	expected = "string"
	code = transpile('Y');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Zip():
	ctx = Context()
	ctx.stack = [[1,2],[3,4]]
	expected = [[1,3],[2,4]]
	code = transpile('Z');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc",[1,2,3]]
	expected = [["a",1],["b",2],["c",3]]
	code = transpile('Z');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Any():
	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 1
	code = transpile('a');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[0,0,0]]
	expected = 0
	code = transpile('a');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[0,1,2]]
	expected = 1
	code = transpile('a');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["A"]
	expected = 1
	code = transpile('a');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["a"]
	expected = 0
	code = transpile('a');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["Hi"]
	expected = [1,0]
	code = transpile('a');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Binary():
	ctx = Context()
	ctx.stack = [5]
	expected = [1,0,1]
	code = transpile('b');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [" "]
	expected = [[1,0,0,0,0,0]]
	code = transpile('b');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[2,3]]
	expected = [[1,0],[1,1]]
	code = transpile('b');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Contains():
	ctx = Context()
	ctx.stack = ["abcdef","a"]
	expected = 1
	code = transpile('c');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["xyz","a"]
	expected = 0
	code = transpile('c');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3],1]
	expected = 1
	code = transpile('c');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3],0]
	expected = 0
	code = transpile('c');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Double():
	ctx = Context()
	ctx.stack = [5]
	expected = 10
	code = transpile('d');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [0]
	expected = 0
	code = transpile('d');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2]]
	expected = [2,4]
	code = transpile('d');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["x"]
	expected = "xx"
	code = transpile('d');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["ha"]
	expected = "haha"
	code = transpile('d');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Exponentiation():
	ctx = Context()
	ctx.stack = [5,3]
	expected = 125
	code = transpile('e');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [0,0]
	expected = 1
	code = transpile('e');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello",2]
	expected = "hlo"
	code = transpile('e');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Flatten():
	ctx = Context()
	ctx.stack = [135]
	expected = [1,3,5]
	code = transpile('f');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hi"]
	expected = ["h","i"]
	code = transpile('f');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[[[1,2],3,[[4,[5]],6],7],[8,[9]]]]
	expected = [1,2,3,4,5,6,7,8,9]
	code = transpile('f');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [-1]
	expected = ["-",1]
	code = transpile('f');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Minimum():
	ctx = Context()
	ctx.stack = ["abc"]
	expected = "a"
	code = transpile('g');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,4,-2]]
	expected = -2
	code = transpile('g');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[5,3,9]]
	expected = 3
	code = transpile('g');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Head():
	ctx = Context()
	ctx.stack = ["hello"]
	expected = "h"
	code = transpile('h');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 1
	code = transpile('h');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Index():
	ctx = Context()
	ctx.stack = ["abc",1]
	expected = "b"
	code = transpile('i');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3], 0]
	expected = 1
	code = transpile('i');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[2,3,4,5], [2]]
	expected = [2,3]
	code = transpile('i');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,3,5,7],[1,3]]
	expected = [3,5]
	code = transpile('i');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3,4,5,6,7,8,9,10],[1,8,2]]
	expected = [2,4,6,8]
	code = transpile('i');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Join():
	ctx = Context()
	ctx.stack = [[1,2,3],"penguin"]
	expected = "1penguin2penguin3"
	code = transpile('j');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [["he","","o, wor","d!"], "l"]
	expected = "hello, world!"
	code = transpile('j');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_CumulativeGroups():
	ctx = Context()
	ctx.stack = ["hello",3]
	expected = ["hel","ell","llo"]
	code = transpile('l');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["cake",2]
	expected = ["ca","ak","ke"]
	code = transpile('l');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["cheese","cake"]
	expected = 0
	code = transpile('l');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["cheese","salads"]
	expected = 1
	code = transpile('l');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Mirror():
	ctx = Context()
	ctx.stack = [123]
	expected = 444
	code = transpile('m');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hi"]
	expected = "hiih"
	code = transpile('m');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = [1,2,3,3,2,1]
	code = transpile('m');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Remove():
	ctx = Context()
	ctx.stack = ["hello","l"]
	expected = "heo"
	code = transpile('o');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3,1,2],1]
	expected = [2,3,2]
	code = transpile('o');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["bananas and naan","an"]
	expected = "bas d na"
	code = transpile('o');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Prepend():
	ctx = Context()
	ctx.stack = ["ld","wor"]
	expected = "world"
	code = transpile('p');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3],13]
	expected = [13,1,2,3]
	code = transpile('p');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[3,4,5],"23"]
	expected = ["23",3,4,5]
	code = transpile('p');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Uneval():
	ctx = Context()
	ctx.stack = ["\\"]
	expected = "`\\`"
	code = transpile('q');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["`"]
	expected = "`\\``"
	code = transpile('q');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["a"]
	expected = "`a`"
	code = transpile('q');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Range():
	ctx = Context()
	ctx.stack = [3,6]
	expected = [3,4,5]
	code = transpile('r');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [4,8]
	expected = [4,5,6,7]
	code = transpile('r');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_sort():
	ctx = Context()
	ctx.stack = [[3,1,2]]
	expected = [1,2,3]
	code = transpile('s');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["bca"]
	expected = "abc"
	code = transpile('s');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Tail():
	ctx = Context()
	ctx.stack = ["hello"]
	expected = "o"
	code = transpile('t');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 3
	code = transpile('t');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_MinusOne():
	ctx = Context()
	ctx.stack = []
	expected = -1
	code = transpile('u');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Listify():
	ctx = Context()
	ctx.stack = [1]
	expected = [1]
	code = transpile('w');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello"]
	expected = ["hello"]
	code = transpile('w');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = [[1,2,3]]
	code = transpile('w');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Uninterleave():
	ctx = Context()
	ctx.stack = ["abcde"]
	expected = "bd"
	code = transpile('y');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3,4]]
	expected = [2,4]
	code = transpile('y');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Zipself():
	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = [[1,1],[2,2],[3,3]]
	code = transpile('z');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["zap"]
	expected = [["z","z"], ["a","a"],["p","p"]]
	code = transpile('z');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_MaxbyTail():
	ctx = Context()
	ctx.stack = [[[3,4],[9,2]]]
	expected = [3,4]
	code = transpile('↑');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[[1,2,3],[2,5]]]
	expected = [2,5]
	code = transpile('↑');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_MinbyTail():
	ctx = Context()
	ctx.stack = [[[3,4],[9,2]]]
	expected = [9,2]
	code = transpile('↓');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[[1,2,3],[2,5]]]
	expected = [1,2,3]
	code = transpile('↓');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_DyadicMaximum():
	ctx = Context()
	ctx.stack = [5,3]
	expected = 5
	code = transpile('∴');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello","goodbye"]
	expected = "hello"
	code = transpile('∴');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3,"(stuff)"]
	expected = 3
	code = transpile('∴');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_DyadicMinimum():
	ctx = Context()
	ctx.stack = [5,3]
	expected = 3
	code = transpile('∵');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello","goodbye"]
	expected = "goodbye"
	code = transpile('∵');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3,"(stuff)"]
	expected = "(stuff)"
	code = transpile('∵');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_IncrementSpaceReplaceWith0():
	ctx = Context()
	ctx.stack = [5]
	expected = 6
	code = transpile('›');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[3,4]]
	expected = [4,5]
	code = transpile('›');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["  101"]
	expected = "00101"
	code = transpile('›');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Decrement():
	ctx = Context()
	ctx.stack = [5]
	expected = 4
	code = transpile('‹');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[3,4]]
	expected = [2,3]
	code = transpile('‹');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello"]
	expected = "hello-"
	code = transpile('‹');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Parity():
	ctx = Context()
	ctx.stack = [2]
	expected = 0
	code = transpile('∷');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3]
	expected = 1
	code = transpile('∷');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello!"]
	expected = "lo!"
	code = transpile('∷');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_EmptyString():
	ctx = Context()
	ctx.stack = []
	expected = ""
	code = transpile('¤');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Space():
	ctx = Context()
	ctx.stack = []
	expected = " "
	code = transpile('ð');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_ToBaseTenFromCustomBase():
	ctx = Context()
	ctx.stack = [43,5]
	expected = 23
	code = transpile('β');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["banana","nab"]
	expected = 577
	code = transpile('β');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[15,23,9],31]
	expected = 15137
	code = transpile('β');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_FromBaseTenToCustomBase():
	ctx = Context()
	ctx.stack = [1234567,"abc"]
	expected = "cacccabbbbcab"
	code = transpile('τ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1234567,5]
	expected = [3,0,4,0,0,1,2,3,2]
	code = transpile('τ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [928343,["he","ll","o"]]
	expected = ["ll","o","he","o","he","ll","ll","ll","ll","he","he","he","o"]
	code = transpile('τ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Absolutevalue():
	ctx = Context()
	ctx.stack = [1]
	expected = 1
	code = transpile('ȧ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [-1]
	expected = 1
	code = transpile('ȧ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [" ch ee s e "]
	expected = "cheese"
	code = transpile('ȧ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[-1,2,-5]]
	expected = [1,2,5]
	code = transpile('ȧ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Boolify():
	ctx = Context()
	ctx.stack = [0]
	expected = 0
	code = transpile('ḃ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1]
	expected = 1
	code = transpile('ḃ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[]]
	expected = 0
	code = transpile('ḃ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["x"]
	expected = 1
	code = transpile('ḃ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_NotOne():
	ctx = Context()
	ctx.stack = [[]]
	expected = 1
	code = transpile('ċ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["1"]
	expected = 0
	code = transpile('ċ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [5]
	expected = 1
	code = transpile('ċ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1]
	expected = 0
	code = transpile('ċ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Divmod():
	ctx = Context()
	ctx.stack = [5,3]
	expected = [1,2]
	code = transpile('ḋ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abcd",3]
	expected = ["abc","abd","acd","bcd"]
	code = transpile('ḋ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3],2]
	expected = [[1,2],[1,3],[2,3]]
	code = transpile('ḋ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abcdef", "Joe"]
	expected = ["Joedef"]
	code = transpile('ḋ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Enumerate():
	ctx = Context()
	ctx.stack = ["abc"]
	expected = [[0,"a"],[1,"b"],[2,"c"]]
	code = transpile('ė');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = [[0,1],[1,2],[2,3]]
	code = transpile('ė');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Find():
	ctx = Context()
	ctx.stack = [[1,2,3],2]
	expected = 1
	code = transpile('ḟ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello","l"]
	expected = 2
	code = transpile('ḟ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Gcd():
	ctx = Context()
	ctx.stack = [[1,3,2]]
	expected = 1
	code = transpile('ġ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[60,42,108]]
	expected = 6
	code = transpile('ġ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [50,35]
	expected = 5
	code = transpile('ġ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["laugh","cough"]
	expected = "ugh"
	code = transpile('ġ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_HeadExtract():
	ctx = Context()
	ctx.stack = ["hello"]
	expected = "ello"
	code = transpile('ḣ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = [2,3]
	code = transpile('ḣ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_FloorDivision():
	ctx = Context()
	ctx.stack = [5,3]
	expected = 1
	code = transpile('ḭ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello!",3]
	expected = "he"
	code = transpile('ḭ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3,"hello!"]
	expected = "he"
	code = transpile('ḭ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_LeftJustifyGridifyInfiniteReplaceCollectuntilfale():
	ctx = Context()
	ctx.stack = [1, 3, 2]
	expected = 1
	code = transpile('ŀ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Mean():
	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 2
	code = transpile('ṁ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[4,71,-63]]
	expected = 4
	code = transpile('ṁ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_JoinByNothing():
	ctx = Context()
	ctx.stack = [["a","b","c"]]
	expected = "abc"
	code = transpile('ṅ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 123
	code = transpile('ṅ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Slice():
	ctx = Context()
	ctx.stack = ["hello",2]
	expected = "llo"
	code = transpile('ȯ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3],1]
	expected = [2,3]
	code = transpile('ȯ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Powerset():
	ctx = Context()
	ctx.stack = ["ab"]
	expected = [[],["a"],["b"],["a","b"]]
	code = transpile('ṗ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1,2,3]
	expected = [[],[1],[2],[3],[1,2],[1,3],[2,3],[1,2,3]]
	code = transpile('ṗ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Round():
	ctx = Context()
	ctx.stack = [5.5]
	expected = 6
	code = transpile('ṙ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3.2]
	expected = 3
	code = transpile('ṙ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[5.5,3.2]]
	expected = [6,3]
	code = transpile('ṙ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [-4.7]
	expected = -5
	code = transpile('ṙ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [-4.5]
	expected = -4
	code = transpile('ṙ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_FunctionSort():
	ctx = Context()
	ctx.stack = [3,4]
	expected = [3,4]
	code = transpile('ṡ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1,5]
	expected = [1,2,3,4,5]
	code = transpile('ṡ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc1def2ghi","\\d+"]
	expected = ["abc","def","ghi"]
	code = transpile('ṡ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_TailExtract():
	ctx = Context()
	ctx.stack = ["abc"]
	expected = "c"
	code = transpile('ṫ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 3
	code = transpile('ṫ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_ChunkWrap():
	ctx = Context()
	ctx.stack = ["abcdef",2]
	expected = ["ab","cd","ef"]
	code = transpile('ẇ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3,4,5,6],3]
	expected = [[1,2,3],[4,5,6]]
	code = transpile('ẇ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Repeat():
	ctx = Context()
	ctx.stack = [[1,2,3],3]
	expected = [[1,2,3],[1,2,3],[1,2,3]]
	code = transpile('ẋ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["x",5]
	expected = "xxxxx"
	code = transpile('ẋ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_ExclusiveRangeLength():
	ctx = Context()
	ctx.stack = ["abc"]
	expected = [0,1,2]
	code = transpile('ẏ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2]]
	expected = [0,1]
	code = transpile('ẏ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_InclusiveRangeLength():
	ctx = Context()
	ctx.stack = ["abc"]
	expected = [1,2,3]
	code = transpile('ż');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2]]
	expected = [1,2]
	code = transpile('ż');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_SquareRoot():
	ctx = Context()
	ctx.stack = [4]
	expected = 2
	code = transpile('√');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello"]
	expected = "hlo"
	code = transpile('√');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Ten():
	ctx = Context()
	ctx.stack = []
	expected = 10
	code = transpile('₀');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Hundred():
	ctx = Context()
	ctx.stack = []
	expected = 100
	code = transpile('₁');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_IsEven():
	ctx = Context()
	ctx.stack = [5]
	expected = 0
	code = transpile('₂');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [2]
	expected = 1
	code = transpile('₂');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello"]
	expected = 0
	code = transpile('₂');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2]]
	expected = 1
	code = transpile('₂');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_DivisibleBythree():
	ctx = Context()
	ctx.stack = [5]
	expected = 0
	code = transpile('₃');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [6]
	expected = 1
	code = transpile('₃');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hi"]
	expected = 0
	code = transpile('₃');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1]]
	expected = 1
	code = transpile('₃');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_TwentySix():
	ctx = Context()
	ctx.stack = []
	expected = 26
	code = transpile('₄');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_DivisibleByFive():
	ctx = Context()
	ctx.stack = [4]
	expected = 0
	code = transpile('₅');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [5]
	expected = 1
	code = transpile('₅');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello"]
	expected = 5
	code = transpile('₅');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 3
	code = transpile('₅');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_SixtyFour():
	ctx = Context()
	ctx.stack = []
	expected = 64
	code = transpile('₆');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_OneTwentyEight():
	ctx = Context()
	ctx.stack = []
	expected = 128
	code = transpile('₇');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_TwoFiftySix():
	ctx = Context()
	ctx.stack = []
	expected = 256
	code = transpile('₈');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Newline():
	ctx = Context()
	ctx.stack = []
	expected = "\\n"
	code = transpile('¶');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_JoinOnNewlines():
	ctx = Context()
	ctx.stack = [[1, 2, 3, 4, 5, 6]]
	expected = "1\n2\n3\n4\n5\n6"
	code = transpile('⁋');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [["Hello", "World!"]]
	expected = "Hello\nWorld!"
	code = transpile('⁋');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_VerticalJoin():
	ctx = Context()
	ctx.stack = [["abc", "def", "ghi"]]
	expected = "adg\nbeh\ncfi"
	code = transpile('§');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [["***", "****", "*****"]]
	expected = "  *\n **\n***\n***\n***"
	code = transpile('§');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_AbsoluteDifferencePaddedVerticalJoin():
	ctx = Context()
	ctx.stack = [5, 1]
	expected = 4
	code = transpile('ε');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1, 5]
	expected = 4
	code = transpile('ε');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3, 3]
	expected = 0
	code = transpile('ε');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [["***", "****", "*****"], "."]
	expected = "..*\n.**\n***\n***\n***"
	code = transpile('ε');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [["abc", "def", "ghi"], "."]
	expected = "adg\nbeh\ncfi"
	code = transpile('ε');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Factorial():
	ctx = Context()
	ctx.stack = [5]
	expected = 120
	code = transpile('¡');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello my name jeff. ur sussy baka"]
	expected = "Hello my name jeff. Ur sussy baka"
	code = transpile('¡');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1, 2, 3, 4, 5]]
	expected = [1, 2, 6, 24, 120]
	code = transpile('¡');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Summate():
	ctx = Context()
	ctx.stack = [[1, 2, 3, 4, 5]]
	expected = 15
	code = transpile('∑');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [["abc", "def", 10]]
	expected = "abcdef10"
	code = transpile('∑');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [12345]
	expected = 15
	code = transpile('∑');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_CumulativeSum():
	ctx = Context()
	ctx.stack = [12345]
	expected = [1, 3, 6, 10, 15]
	code = transpile('¦');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abcdef"]
	expected = ["a", "ab", "abc", "abcd", "abcde", "abcdef"]
	code = transpile('¦');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1, 2, 3, 4, 5]]
	expected = [1, 3, 6, 10, 15]
	code = transpile('¦');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_AllEqual():
	ctx = Context()
	ctx.stack = [1111]
	expected = 1
	code = transpile('≈');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["acc"]
	expected = 0
	code = transpile('≈');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1, 2, 2, 1]]
	expected = 0
	code = transpile('≈');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[]]
	expected = 1
	code = transpile('≈');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Assign():
	ctx = Context()
	ctx.stack = [[1, 2, 3, 4], 1, 0]
	expected = [1, 0, 3, 4]
	code = transpile('Ȧ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["Hello ", ", World!", 5]
	expected = "Hello, World!"
	code = transpile('Ȧ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [69320, 2, 4]
	expected = [6, 9, 4, 2, 0]
	code = transpile('Ȧ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Bifurcate():
	ctx = Context()
	ctx.stack = [203]
	expected = 302
	code = transpile('Ḃ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc"]
	expected = "cab"
	code = transpile('Ḃ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1, 2, 3, 4]]
	expected = [4, 3, 2, 1]
	code = transpile('Ḃ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Counts():
	ctx = Context()
	ctx.stack = [[1, 2, 2, 3, 3, 3, 3]]
	expected = [[1, 1], [2, 2], [3, 4]]
	code = transpile('Ċ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["Hello, World!"]
	expected = [["W", 1], ["!", 1], [" ", 1], ["o", 2], ["d", 1], [",", 1], ["H", 1], ["l", 3], ["e", 1], ["r", 1]]
	code = transpile('Ċ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_IsDivisibleArbitraryDuplicate():
	ctx = Context()
	ctx.stack = [15, 5]
	expected = 1
	code = transpile('Ḋ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc", 3]
	expected = "abc"
	code = transpile('Ḋ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[5, 13, 29, 48, 12], 2]
	expected = [0, 0, 0, 1, 1]
	code = transpile('Ḋ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_VyxalExecReciprocal():
	ctx = Context()
	ctx.stack = [[2, 3, -1]]
	expected = [0.5, 1/3, -1]
	code = transpile('Ė');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["kH"]
	expected = "Hello, World!"
	code = transpile('Ė');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Groupconsecutive():
	ctx = Context()
	ctx.stack = [[1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 5, 5]]
	expected = [[1, 1, 1], [2, 2, 2, 2, 2, 2], [3, 3, 3, 3, 3], [4, 4], [5, 5]]
	code = transpile('Ġ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["Hello, World!"]
	expected = [["H"], ["e"], ["l", "l"], ["o"], [","], [" "], ["W"], ["o"], ["r"], ["l"], ["d"], ["!"]]
	code = transpile('Ġ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_HeadRemoveBehead():
	ctx = Context()
	ctx.stack = [[0, [43, 69], "foo"]]
	expected = [[43, 69], "foo"]
	code = transpile('Ḣ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[]]
	expected = []
	code = transpile('Ḣ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["foo"]
	expected = "oo"
	code = transpile('Ḣ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [""]
	expected = ""
	code = transpile('Ḣ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1234.56]
	expected = 234.56
	code = transpile('Ḣ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [0.2]
	expected = 0.2
	code = transpile('Ḣ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Indexintoorfindcycle():
	ctx = Context()
	ctx.stack = [["foo", "bar", -69, 420, "baz"], [0, 2, 4]]
	expected = ["foo", -69, "baz"]
	code = transpile('İ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Transliterate():
	ctx = Context()
	ctx.stack = ["abcdefcba","abc","123"]
	expected = "123def321"
	code = transpile('Ŀ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,0], [2], [5]]
	expected = [1,5,0]
	code = transpile('Ŀ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc","ab",["bb","cc"]]
	expected = ["bb","cc","c"]
	code = transpile('Ŀ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Insert():
	ctx = Context()
	ctx.stack = [[1,3,4],1,2]
	expected = [1,2,3,4]
	code = transpile('Ṁ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["wyz",1,"x"]
	expected = "wxyz"
	code = transpile('Ṁ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["jknop",2,"lm"]
	expected = "jklmnop"
	code = transpile('Ṁ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Integerpartitions():
	ctx = Context()
	ctx.stack = [5]
	expected = [[5],[1,4],[1,1,3],[1,1,1,2],[1,1,1,1,1],[1,2,2],[2,3]]
	code = transpile('Ṅ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello"]
	expected = "h e l l o"
	code = transpile('Ṅ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = "1 2 3"
	code = transpile('Ṅ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Over():
	ctx = Context()
	ctx.stack = [4,5]
	expected = 4
	code = transpile('Ȯ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hi","bye"]
	expected = "hi"
	code = transpile('Ȯ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Permutations():
	ctx = Context()
	ctx.stack = ["abc"]
	expected = ["abc","abc","bac","bca","cab","cba"]
	code = transpile('Ṗ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2]]
	expected = [[1,2],[2,1]]
	code = transpile('Ṗ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Reverse():
	ctx = Context()
	ctx.stack = [203]
	expected = 302
	code = transpile('Ṙ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc"]
	expected = "cab"
	code = transpile('Ṙ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1, 2, 3, 4]]
	expected = [4, 3, 2, 1]
	code = transpile('Ṙ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Sumofstack():
	ctx = Context()
	ctx.stack = [[1,2,3],[4,5,6]]
	expected = [5,7,9]
	code = transpile('Ṡ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3,4,5]
	expected = 12
	code = transpile('Ṡ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hi","bye"]
	expected = "hibye"
	code = transpile('Ṡ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_TailRemove():
	ctx = Context()
	ctx.stack = ["1234"]
	expected = "234"
	code = transpile('Ṫ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = [1,2]
	code = transpile('Ṫ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_SplitAndKeepDelimiter():
	ctx = Context()
	ctx.stack = ["a b c"," "]
	expected = ["a"," ","b"," ","c"]
	code = transpile('Ẇ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["xyzabc123abc","b"]
	expected = ["xyza","b","c123a","b","c"]
	code = transpile('Ẇ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_CartesianProduct():
	ctx = Context()
	ctx.stack = ["ab","cd"]
	expected = ["ac","ad","bc","bd"]
	code = transpile('Ẋ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2],[3,4]]
	expected = [[1,3],[1,4],[2,3],[2,4]]
	code = transpile('Ẋ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_SliceUntil():
	ctx = Context()
	ctx.stack = ["abc",1]
	expected = "a"
	code = transpile('Ẏ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3],2]
	expected = [1,2]
	code = transpile('Ẏ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_SliceFromOneUntil():
	ctx = Context()
	ctx.stack = ["abc",2]
	expected = "b"
	code = transpile('Ż');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3],3]
	expected = [2,3]
	code = transpile('Ż');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Square():
	ctx = Context()
	ctx.stack = [5]
	expected = 25
	code = transpile('²');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello"]
	expected = ["hel","lo"]
	code = transpile('²');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["bye"]
	expected = ["by","e"]
	code = transpile('²');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = [1,4,9]
	code = transpile('²');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Shift():
	ctx = Context()
	ctx.stack = [1,4,5]
	expected = 4
	code = transpile('∇');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["my","hi","bye"]
	expected = "hi"
	code = transpile('∇');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Ceiling():
	ctx = Context()
	ctx.stack = [5]
	expected = 5
	code = transpile('⌈');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [4.5]
	expected = 5
	code = transpile('⌈');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1.52,2.9,3.3]]
	expected = [2,3,4]
	code = transpile('⌈');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello world"]
	expected = ["hello","world"]
	code = transpile('⌈');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Floor():
	ctx = Context()
	ctx.stack = [5.3]
	expected = 5
	code = transpile('⌊');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[5.3,4.7]]
	expected = [4,5]
	code = transpile('⌊');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["123abc"]
	expected = 123
	code = transpile('⌊');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Deltas():
	ctx = Context()
	ctx.stack = [1,2,3]
	expected = [1,1]
	code = transpile('¯');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1,1,1]
	expected = [0,0]
	code = transpile('¯');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [40,61,3]
	expected = [21,-58]
	code = transpile('¯');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Sign():
	ctx = Context()
	ctx.stack = [1]
	expected = 1
	code = transpile('±');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hi"]
	expected = "hi"
	code = transpile('±');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [-5]
	expected = -1
	code = transpile('±');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [0]
	expected = 0
	code = transpile('±');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_RightBitShift():
	ctx = Context()
	ctx.stack = [4,1]
	expected = 2
	code = transpile('↳');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [8,"green"]
	expected = "   green"
	code = transpile('↳');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello","cheeseburger"]
	expected = "       hello"
	code = transpile('↳');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_LeftBitShift():
	ctx = Context()
	ctx.stack = [4,1]
	expected = 8
	code = transpile('↲');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [8,"green"]
	expected = "green   "
	code = transpile('↲');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello","cheeseburger"]
	expected = "hello       "
	code = transpile('↲');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_BitwiseAnd():
	ctx = Context()
	ctx.stack = [420, 69]
	expected = 4
	code = transpile('⋏');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc", 10]
	expected = "   abc    "
	code = transpile('⋏');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["no", "yesnt"]
	expected = " no "
	code = transpile('⋏');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_BitwiseOr():
	ctx = Context()
	ctx.stack = [420, 69]
	expected = 485
	code = transpile('⋎');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [2, "abc"]
	expected = "ab"
	code = transpile('⋎');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc", 2]
	expected = "ab"
	code = transpile('⋎');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["Hello", "lower"]
	expected = "Hellower"
	code = transpile('⋎');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_BitwiseXor():
	ctx = Context()
	ctx.stack = [420, 69]
	expected = 481
	code = transpile('꘍');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [5, "ab"]
	expected = "     ab"
	code = transpile('꘍');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["ab", 5]
	expected = "ab     "
	code = transpile('꘍');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["atoll", "bowl"]
	expected = 3
	code = transpile('꘍');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_BitwiseNot():
	ctx = Context()
	ctx.stack = [220]
	expected = -221
	code = transpile('ꜝ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["Hello"]
	expected = 1
	code = transpile('ꜝ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_RandomChoice():
	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 2
	code = transpile('℅');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 1
	code = transpile('℅');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = 3
	code = transpile('℅');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_LesserThanorEqualTo():
	ctx = Context()
	ctx.stack = [1,2]
	expected = 1
	code = transpile('≤');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_GreaterThanorEqualTo():
	ctx = Context()
	ctx.stack = [1,2]
	expected = 0
	code = transpile('≥');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_NotEqualTo():
	ctx = Context()
	ctx.stack = [1,2]
	expected = 1
	code = transpile('≠');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_ExactlyEqualTo():
	ctx = Context()
	ctx.stack = [1,2]
	expected = 1
	code = transpile('⁼');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Fractionify():
	ctx = Context()
	ctx.stack = [0.5]
	expected = [1,2]
	code = transpile('ƒ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = "0.3"
	expected = [3,10]
	code = transpile('ƒ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Decimalify():
	ctx = Context()
	ctx.stack = [1,2]
	expected = 0.5
	code = transpile('ɖ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3,4]
	expected = 0.75
	code = transpile('ɖ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_SetUnion():
	ctx = Context()
	ctx.stack = [[1,2],[2,3,4]]
	expected = [1,2,3,4]
	code = transpile('∪');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_SetIntersection():
	ctx = Context()
	ctx.stack = [[1,2],[2,3,4]]
	expected = [2]
	code = transpile('∩');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_SymmetricSetdifference():
	ctx = Context()
	ctx.stack = [[1,2],[2,3,4]]
	expected = [1,3,4]
	code = transpile('⊍');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_GradeUp():
	ctx = Context()
	ctx.stack = [[420,69,1337]]
	expected = [2,1,3]
	code = transpile('⇧');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["Heloo"]
	expected = "HELOO"
	code = transpile('⇧');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [4]
	expected = 6
	code = transpile('⇧');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_GradeDown():
	ctx = Context()
	ctx.stack = [[420,69,1337]]
	expected = [3,1,2]
	code = transpile('⇩');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["Heloo"]
	expected = "heloo"
	code = transpile('⇩');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [4]
	expected = 2
	code = transpile('⇩');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Removenonalphabets():
	ctx = Context()
	ctx.stack = ["Helo1233adc__"]
	expected = "Heloadc"
	code = transpile('Ǎ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [8]
	expected = 256
	code = transpile('Ǎ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Nthprime():
	ctx = Context()
	ctx.stack = [3]
	expected = 7
	code = transpile('ǎ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc"]
	expected = ["a","ab","abc","","b","bc","","","c","","",""]
	code = transpile('ǎ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Primefactorization():
	ctx = Context()
	ctx.stack = [45]
	expected = [3,5]
	code = transpile('Ǐ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc"]
	expected = "abca"
	code = transpile('Ǐ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Primefactors():
	ctx = Context()
	ctx.stack = [45]
	expected = [3, 3, 5]
	code = transpile('ǐ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abc def"]
	expected = "Abc Def"
	code = transpile('ǐ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Multiplicity():
	ctx = Context()
	ctx.stack = [45, 3]
	expected = 2
	code = transpile('Ǒ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["aaabbbc", "ab"]
	expected = "c"
	code = transpile('Ǒ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Modulo3():
	ctx = Context()
	ctx.stack = [45]
	expected = 0
	code = transpile('ǒ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [""]
	expected = 1
	code = transpile('ǒ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_RotateLeft():
	ctx = Context()
	ctx.stack = [3, [4, 5, 5, 6]]
	expected = [6, 4, 5, 5]
	code = transpile('Ǔ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3, [1, 2, 3, 4]]
	expected = [2, 3, 4, 1]
	code = transpile('Ǔ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_RotateRight():
	ctx = Context()
	ctx.stack = [3, [4, 5, 5, 6]]
	expected = [5, 5, 6, 4]
	code = transpile('ǔ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3, [1, 2, 3, 4]]
	expected = [4, 1, 2, 3]
	code = transpile('ǔ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_SplitOnnewlines():
	ctx = Context()
	ctx.stack = ["a\nb\nc"]
	expected = ["a", "b", "c"]
	code = transpile('↵');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [3]
	expected = 1000
	code = transpile('↵');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_ProductofArray():
	ctx = Context()
	ctx.stack = [3,4,5]
	expected = 60
	code = transpile('Π');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Uppercasealphabet():
	ctx = Context()
	ctx.stack = []
	expected = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
	code = transpile('kA');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_eEulersnumber():
	ctx = Context()
	ctx.stack = []
	expected = 2.718281828459045
	code = transpile('ke');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Fizz():
	ctx = Context()
	ctx.stack = []
	expected = "Fizz"
	code = transpile('kf');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Buzz():
	ctx = Context()
	ctx.stack = []
	expected = "Buzz"
	code = transpile('kb');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_FizzBuzz():
	ctx = Context()
	ctx.stack = []
	expected = "FizzBuzz"
	code = transpile('kF');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_HelloWorld():
	ctx = Context()
	ctx.stack = []
	expected = "Hello, World!"
	code = transpile('kH');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_HelloWorld():
	ctx = Context()
	ctx.stack = []
	expected = "Hello World"
	code = transpile('kh');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_1000():
	ctx = Context()
	ctx.stack = []
	expected = 1000
	code = transpile('k1');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_1000():
	ctx = Context()
	ctx.stack = []
	expected = 10000
	code = transpile('k2');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_10000():
	ctx = Context()
	ctx.stack = []
	expected = 100000
	code = transpile('k3');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_1000000():
	ctx = Context()
	ctx.stack = []
	expected = 1000000
	code = transpile('k4');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Lowercasealphabet():
	ctx = Context()
	ctx.stack = []
	expected = "abcdefghijklmnopqrstuvwxyz"
	code = transpile('ka');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Lowercaseanduppercasealphabet():
	ctx = Context()
	ctx.stack = []
	expected = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
	code = transpile('kL');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Digits():
	ctx = Context()
	ctx.stack = []
	expected = "0123456789"
	code = transpile('kd');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Hexdigitslowercase():
	ctx = Context()
	ctx.stack = []
	expected = "0123456789abcdef"
	code = transpile('k6');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Hexdigitsuppercase():
	ctx = Context()
	ctx.stack = []
	expected = "0123456789ABCDEF"
	code = transpile('k^');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Octaldigits():
	ctx = Context()
	ctx.stack = []
	expected = "01234567"
	code = transpile('ko');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Punctuation():
	ctx = Context()
	ctx.stack = []
	expected = string.punctuation
	code = transpile('kp');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_PrintableASCII():
	ctx = Context()
	ctx.stack = []
	expected = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~'
	code = transpile('kP');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Digitslowercasealphabetanduppercasealphabet():
	ctx = Context()
	ctx.stack = []
	expected = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
	code = transpile('kr');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Uppercaseandlowercasealphabet():
	ctx = Context()
	ctx.stack = []
	expected = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
	code = transpile('kB');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Uppercasealphabetreversed():
	ctx = Context()
	ctx.stack = []
	expected = "ZYXWVUTSRQPONMLKJIHGFEDCBA"
	code = transpile('kZ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Lowercasealphabetreversed():
	ctx = Context()
	ctx.stack = []
	expected = "zyxwvutsrqponmlkjihgfedcba"
	code = transpile('kz');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Uppercaseandlowercasealphabetreversed():
	ctx = Context()
	ctx.stack = []
	expected = "ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba"
	code = transpile('kl');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Pi():
	ctx = Context()
	ctx.stack = []
	expected = 3.141592653589793
	code = transpile('ki');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_NaN():
	ctx = Context()
	ctx.stack = []
	expected = math.nan
	code = transpile('kn');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Goldenratiophi():
	ctx = Context()
	ctx.stack = []
	expected = 1.618033988749895
	code = transpile('kg');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Bracessquarebracketsanglebracketsandparentheses():
	ctx = Context()
	ctx.stack = []
	expected = "{}[]<>()"
	code = transpile('kβ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Parenthesessquarebracketsandbraces():
	ctx = Context()
	ctx.stack = []
	expected = "()[]{}"
	code = transpile('kḂ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Parenthesesandsquarebrackets():
	ctx = Context()
	ctx.stack = []
	expected = "()[]"
	code = transpile('kß');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Openingbrackets():
	ctx = Context()
	ctx.stack = []
	expected = "([{"
	code = transpile('kḃ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Closingbrackets():
	ctx = Context()
	ctx.stack = []
	expected = ")]}"
	code = transpile('k≥');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Openingbracketswith():
	ctx = Context()
	ctx.stack = []
	expected = "([{<"
	code = transpile('k≤');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Closingbracketswith():
	ctx = Context()
	ctx.stack = []
	expected = ")]}>"
	code = transpile('kΠ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Lowercasevowels():
	ctx = Context()
	ctx.stack = []
	expected = "aeiou"
	code = transpile('kv');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Upercasevowels():
	ctx = Context()
	ctx.stack = []
	expected = "AEIOU"
	code = transpile('kV');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Lowercaseanduppercasevowels():
	ctx = Context()
	ctx.stack = []
	expected = "aeiouAEIOU"
	code = transpile('k∨');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_12():
	ctx = Context()
	ctx.stack = []
	expected = [1, 2]
	code = transpile('k½');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_4294967296():
	ctx = Context()
	ctx.stack = []
	expected = 4294967296
	code = transpile('kḭ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_11():
	ctx = Context()
	ctx.stack = []
	expected = [1, -1]
	code = transpile('k+');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_11():
	ctx = Context()
	ctx.stack = []
	expected = [-1, 1]
	code = transpile('k-');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_01():
	ctx = Context()
	ctx.stack = []
	expected = [0, 1]
	code = transpile('k≈');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Slashes():
	ctx = Context()
	ctx.stack = []
	expected = "/\\"
	code = transpile('k/');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_360():
	ctx = Context()
	ctx.stack = []
	expected = 360
	code = transpile('kR');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_https():
	ctx = Context()
	ctx.stack = []
	expected = "https://"
	code = transpile('kW');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_http():
	ctx = Context()
	ctx.stack = []
	expected = "http://"
	code = transpile('k℅');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_httpswww():
	ctx = Context()
	ctx.stack = []
	expected = "https://www."
	code = transpile('k↳');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_httpwww():
	ctx = Context()
	ctx.stack = []
	expected = "http://www."
	code = transpile('k²');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_512():
	ctx = Context()
	ctx.stack = []
	expected = 512
	code = transpile('k¶');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_1024():
	ctx = Context()
	ctx.stack = []
	expected = 1024
	code = transpile('k⁋');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_2048():
	ctx = Context()
	ctx.stack = []
	expected = 2048
	code = transpile('k¦');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_4096():
	ctx = Context()
	ctx.stack = []
	expected = 4096
	code = transpile('kṄ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_8192():
	ctx = Context()
	ctx.stack = []
	expected = 8192
	code = transpile('kṅ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_16384():
	ctx = Context()
	ctx.stack = []
	expected = 16384
	code = transpile('k¡');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_32768():
	ctx = Context()
	ctx.stack = []
	expected = 32768
	code = transpile('kε');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_65536():
	ctx = Context()
	ctx.stack = []
	expected = 65536
	code = transpile('k₴');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_2147483648():
	ctx = Context()
	ctx.stack = []
	expected = 2147483648
	code = transpile('k×');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Lowercaseconsonantswithy():
	ctx = Context()
	ctx.stack = []
	expected = "bcdfghjklmnpqrstvwxyz"
	code = transpile('k⁰');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_BFcommandset():
	ctx = Context()
	ctx.stack = []
	expected = "[]<>-+.,"
	code = transpile('kT');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Bracketpairlist():
	ctx = Context()
	ctx.stack = []
	expected = ["()","[]","{}","<>"]
	code = transpile('kṗ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Nestedbrackets():
	ctx = Context()
	ctx.stack = []
	expected = "([{<>}])"
	code = transpile('kṖ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Amogus():
	ctx = Context()
	ctx.stack = []
	expected = "ඞ"
	code = transpile('kS');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_220():
	ctx = Context()
	ctx.stack = []
	expected = 1048576
	code = transpile('k₂');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_230():
	ctx = Context()
	ctx.stack = []
	expected = 1073741824
	code = transpile('k₃');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_LowercaseVowelsWithY():
	ctx = Context()
	ctx.stack = []
	expected = "aeiouy"
	code = transpile('k∪');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_UppercaseVowelsWithY():
	ctx = Context()
	ctx.stack = []
	expected = "AEIOUY"
	code = transpile('k⊍');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_VowelsWithY():
	ctx = Context()
	ctx.stack = []
	expected = "aeiouyAEIOUY"
	code = transpile('k∩');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Parenthesise():
	ctx = Context()
	ctx.stack = ["xyz"]
	expected = "(xyz)"
	code = transpile('bø');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [5]
	expected = "(5)"
	code = transpile('bø');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = ["(1)","(2)","(3)"]
	code = transpile('bø');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Bracketify():
	ctx = Context()
	ctx.stack = ["xyz"]
	expected = "[xyz]"
	code = transpile('øB');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [5]
	expected = "[5]"
	code = transpile('øB');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = ["[1]","[2]","[3]"]
	code = transpile('øB');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_CurlyBracketify():
	ctx = Context()
	ctx.stack = ["xyz"]
	expected = "{xyz}"
	code = transpile('øḃ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [5]
	expected = "{5}"
	code = transpile('øḃ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = ["{1}","{2}","{3}"]
	code = transpile('øḃ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_AngleBracketify():
	ctx = Context()
	ctx.stack = ["xyz"]
	expected = "<xyz>"
	code = transpile('øḂ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [5]
	expected = "<5>"
	code = transpile('øḂ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[1,2,3]]
	expected = ["<1>","<2>","<3>"]
	code = transpile('øḂ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_BalancedBrackets():
	ctx = Context()
	ctx.stack = ["xyz"]
	expected = 1
	code = transpile('øβ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["([)]"]
	expected = 0
	code = transpile('øβ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["({<[]>})"]
	expected = 1
	code = transpile('øβ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [")("]
	expected = 0
	code = transpile('øβ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_CustomPadLeft():
	ctx = Context()
	ctx.stack = ["xyz","x",4]
	expected = "xxyz"
	code = transpile('ø↳');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["123","&",8]
	expected = "&&&&&123"
	code = transpile('ø↳');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["324"," ",2]
	expected = "324"
	code = transpile('ø↳');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_CustomPadRight():
	ctx = Context()
	ctx.stack = ["xyz","x",4]
	expected = "xyzx"
	code = transpile('ø↲');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["123","&",8]
	expected = "123&&&&&"
	code = transpile('ø↲');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["324"," ",2]
	expected = "324"
	code = transpile('ø↲');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_FlipBracketsVerticalPalindromise():
	ctx = Context()
	ctx.stack = ["(x"]
	expected = "(x)"
	code = transpile('øM');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["{] "]
	expected = "{] [}"
	code = transpile('øM');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["/*>X"]
	expected = "/*>X<*\\"
	code = transpile('øM');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_RemoveUntilNochange():
	ctx = Context()
	ctx.stack = ["((()))","()"]
	expected = ""
	code = transpile('øo');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["--+--+-",["--","+-"]]
	expected = "+"
	code = transpile('øo');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_ReplaceUntilNoChange():
	ctx = Context()
	ctx.stack = ["xyzzzzz","yzz","yyyz"]
	expected = "xyyyyyyyyyz"
	code = transpile('øV');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["abb","ab","aa"]
	expected = "aaa"
	code = transpile('øV');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_StringCompress():
	ctx = Context()
	ctx.stack = ["hello"]
	expected = "«B²z«"
	code = transpile('øc');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello world"]
	expected = "«⟇÷Ċ$⌈¢2«"
	code = transpile('øc');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_NumberCompress():
	ctx = Context()
	ctx.stack = [234]
	expected = "»⇧»"
	code = transpile('øC');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [27914632409837421]
	expected = "»fðǐ4'∞Ẏ»"
	code = transpile('øC');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Center():
	ctx = Context()
	ctx.stack = [["ab","cdef"]]
	expected = [" ab ","cdef"]
	code = transpile('øĊ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [["xyz","a","bcdef"]]
	expected = [" xyz ","  a  ","bcdef"]
	code = transpile('øĊ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_RunLengthEncoding():
	ctx = Context()
	ctx.stack = ["abc"]
	expected = [["a",1],["b",1],["c",1]]
	code = transpile('øe');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["aaa"]
	expected = [["a",3]]
	code = transpile('øe');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_RunLengthDecoding():
	ctx = Context()
	ctx.stack = [[["x",3]]]
	expected = "xxx"
	code = transpile('ød');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [[["z",2],["a",3]]]
	expected = "zzaaa"
	code = transpile('ød');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_DictionaryCompression():
	ctx = Context()
	ctx.stack = ["withree"]
	expected = "`wi∧ḭ`"
	code = transpile('øD');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello"]
	expected = "`ƈṙ`"
	code = transpile('øD');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["Vyxal"]
	expected = "`₴ŀ`"
	code = transpile('øD');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Grouponwords():
	ctx = Context()
	ctx.stack = ["abc*xyz"]
	expected = ["abc","*","xyz"]
	code = transpile('øW');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["$$$"]
	expected = ["$","$","$"]
	code = transpile('øW');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_Regexreplace():
	ctx = Context()
	ctx.stack = [".{3}","hello","x"]
	expected = "xlo"
	code = transpile('øṙ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["\\W","Hello, World!","E"]
	expected = "HelloEEWorldE"
	code = transpile('øṙ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_StartsWith():
	ctx = Context()
	ctx.stack = ["hello","h"]
	expected = 1
	code = transpile('øp');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello","hello"]
	expected = 1
	code = transpile('øp');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello","x"]
	expected = 0
	code = transpile('øp');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["hello",""]
	expected = 1
	code = transpile('øp');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_PluraliseCount():
	ctx = Context()
	ctx.stack = [4,"hello"]
	expected = "4 hellos"
	code = transpile('øP');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [1,"hello"]
	expected = "1 hello"
	code = transpile('øP');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [0,"hello"]
	expected = "0 hellos"
	code = transpile('øP');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_VerticalMirror():
	pass #TODO implement this test!!!


def test_FlipBracketsVerticalMirror():
	ctx = Context()
	ctx.stack = ["[}"]
	expected = "[}{]"
	code = transpile('øṀ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = [")X"]
	expected = ")XX("
	code = transpile('øṀ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected

	ctx = Context()
	ctx.stack = ["</tag>"]
	expected = "</tag><gat\\>"
	code = transpile('øṀ');print(code)
	exec(code)
	assert simplify(ctx.stack[-1]) == expected


def test_VerticalMirrorCustomMapping():
	pass #TODO implement this test!!!


