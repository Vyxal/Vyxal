import os, sys

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + '/..'
sys.path.insert(1, THIS_FOLDER)

from vyxal.transpile import *
from vyxal.context import Context
from vyxal.elements import *
from vyxal.LazyList import *
def test_LogicalNot():
	stack = [1]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¬'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [0]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¬'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¬'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [""]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¬'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¬'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[]]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¬'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_LogicalAnd():
	stack = [0, 0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∧'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["", 1]
	expected = ""
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∧'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3], 0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∧'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1, 2]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∧'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ReversedLogicalAnd():
	stack = [0, 0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⟑'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["", 1]
	expected = ""
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⟑'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3], 0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⟑'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1, 2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⟑'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_LogicalOr():
	stack = [0, 0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∨'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["", 1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∨'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3], 0]
	expected = [1,2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∨'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1, 2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∨'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ReversedLogicalOr():
	stack = [0, 0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⟇'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["", 1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⟇'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3], 0]
	expected = [1,2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⟇'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1, 2]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⟇'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ItemSplit():
	stack = [123456]
	expected = 6
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('÷'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc"]
	expected = "c"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('÷'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('÷'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_AsteriskLiteral():
	stack = []
	expected = "*"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('×'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_MultiCommand():
	stack = [8, 2]
	expected = 3.0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('•'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abcde", 4]
	expected = "aaaabbbbccccddddeeee"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('•'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abcde", "FgHIj"]
	expected = "AbCDe"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('•'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3,4,5,6,7], [[8, 9], 10, 11, 12, [13, 14]]]
	expected = [[1, 2], 3, 4, 5, [6, 7]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('•'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_FunctionCall():
	stack = [12]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('†'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1, 0, 1]]
	expected = [0, 1, 0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('†'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_SplitOn():
	stack = [1231234, 3]
	expected = ["12", "12", "4"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('€'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc3def", 3]
	expected = ["abc", "def"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('€'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1, 2, 3, 4, 3, 2, 1], 4]
	expected = [[1, 2, 3], [3, 2, 1]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('€'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Halve():
	stack = [8]
	expected = 4
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('½'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["FizzBuzz"]
	expected = ["Fizz", "Buzz"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('½'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[2, 4, 6, 8]]
	expected = [1, 2, 3, 4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('½'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_CombinationsRemoveFixedPointCollection():
	stack = ["cabbage", "abcde"]
	expected = "cabbae"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↔'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,3,5,6,7,7,1],[1,3,5]]
	expected = [1,3,5,1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↔'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2],2]
	expected = [[1,1],[1,2],[2,1],[2,2]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↔'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_InfiniteReplacement():
	stack = ["{[[[]]]}","[]",""]
	expected = "{}"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¢'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1444,44,34]
	expected = 1334
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¢'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ComplementCommaSplit():
	stack = [5]
	expected = -4
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⌐'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [-5]
	expected = 6
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⌐'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["a,b,c"]
	expected = ["a","b","c"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⌐'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_IsPrimeCaseCheck():
	stack = [2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('æ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('æ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["a"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('æ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["A"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('æ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["!"]
	expected = -1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('æ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_InclusiveZeroRange():
	stack = ["a$c"]
	expected = [1, 0, 1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ʀ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1]]
	expected = [[0, 1]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ʀ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = [0,1,2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ʀ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ExclusiveZeroRange():
	stack = ["1234"]
	expected = "1234321"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ʁ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1]]
	expected = [[0]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ʁ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = [0,1,2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ʁ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_InclusiveOneRange():
	stack = ["abc"]
	expected = "ABC"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ɾ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[4, 5]]
	expected = [[1, 2, 3, 4], [1, 2, 3, 4, 5]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ɾ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = [1,2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ɾ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ExclusiveOneRangeLowercase():
	stack = ["1aBC"]
	expected = "1abc"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ɽ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[0]]
	expected = [[]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ɽ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = [1,2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ɽ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Chooserandomchoicesetsame():
	stack = [5,3]
	expected = 10
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ƈ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc","aaccb"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ƈ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc","abcd"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ƈ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Palindromise():
	stack = [[1,2,3]]
	expected = [1,2,3,2,1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∞'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3,4]]
	expected = [1,2,3,4,3,2,1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∞'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3,4,5]]
	expected = [1,2,3,4,5,4,3,2,1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∞'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3,4,5,6]]
	expected = [1,2,3,4,5,6,5,4,3,2,1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∞'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello"]
	expected = "hellolleh"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∞'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_StackLength():
	stack = [0,1,2]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('!'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1,1,1,1,1]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('!'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = []
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('!'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Pair():
	stack = [1, 2]
	expected = [1, 2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('"'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1, 2, 3]
	expected = [2, 3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('"'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1, 2, 3], "abc", 3]
	expected = ["abc", 3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('"'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Swap():
	stack = [1, 2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('$'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1, 2, 3]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('$'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1, 2, 3], "abc", 3]
	expected = "abc"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('$'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ModuloFormat():
	stack = [5,3]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('%'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello!",3]
	expected = "o!"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('%'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["Hel%ld!","lo, Wor"]
	expected = "Hello, World!"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('%'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["% and % and %",[1,2,3]]
	expected = "1 and 2 and 3"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('%'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Multiplication():
	stack = [3,5]
	expected = 15
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('*'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4,-2]
	expected = -8
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('*'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4,"*"]
	expected = "****"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('*'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["x",5]
	expected = "xxxxx"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('*'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["aeiou","hello"]
	expected = "hillu"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('*'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Addition():
	stack = [1, 1]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('+'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [0, -5]
	expected = -5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('+'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc", 5]
	expected = "abc5"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('+'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5, "abc"]
	expected = "5abc"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('+'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["Hello, ", "World!"]
	expected = "Hello, World!"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('+'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3], 4]
	expected = [5, 6, 7]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('+'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3], [4,5,6]]
	expected = [5, 7, 9]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('+'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Subtract():
	stack = [5, 4]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('-'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [0, -5]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('-'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["|", 5]
	expected = "|-----"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('-'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3, "> arrow"]
	expected = "---> arrow"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('-'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abcbde", "b"]
	expected = "acde"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('-'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["aaa", "a"]
	expected = ""
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('-'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1, 2, 3], [1, 2, 3]]
	expected = [0, 0, 0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('-'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[10, 20, 30], 5]
	expected = [5, 15, 25]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('-'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_DivideSplit():
	stack = [4,2]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('/'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abcdef",3]
	expected = ["ab","cd","ef"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('/'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["1,2,3",","]
	expected = ["1","2","3"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('/'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_LessThan():
	stack = [1, 2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('<'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2, 1]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('<'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["a","b"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('<'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [-5,2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('<'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3],2]
	expected = [1,0,0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('<'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Equals():
	stack = [1, 1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('='); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2, 1]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('='); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["a","b"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('='); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["xyz","xyz"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('='); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3],2]
	expected = [0,1,0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('='); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1,"1"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('='); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_GreaterThan():
	stack = [1, 2]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('>'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2, 1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('>'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["a","b"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('>'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2,-5]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('>'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3],2]
	expected = [0,0,1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('>'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["5",10]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('>'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_All():
	stack = [[1,2,3]]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('A'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[0,1,2]]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('A'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [["",1,2]]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('A'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[]]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('A'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [""]
	expected = []
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('A'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('A'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["a"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('A'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["y"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('A'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hi"]
	expected = [0,1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('A'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_BinaryToDecimal():
	stack = [[1,0,1]]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('B'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,1,1]]
	expected = 7
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('B'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["1011"]
	expected = 11
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('B'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ChrOrd():
	stack = [65]
	expected = "A"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('C'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [8482]
	expected = "™"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('C'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["Z"]
	expected = 90
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('C'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["ABC"]
	expected = [65,66,67]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('C'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[123,124,125]]
	expected = ["{","|","}"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('C'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_TwoPowerPythonEval():
	stack = [0]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('E'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2]
	expected = 4
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('E'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["[1,2,3]"]
	expected = [1,2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('E'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Filter():
	stack = [[1,2,3],[2,4,6]]
	expected = [1,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('F'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abcdef","daffodil"]
	expected = "bce"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('F'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Max():
	stack = [[1,3,2]]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('G'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["python"]
	expected = "y"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('G'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_HexToDecimal():
	stack = [[1,2,3]]
	expected = 291
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('H'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["b"]
	expected = 11
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('H'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["beedab"]
	expected = 12512683
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('H'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Int():
	stack = ["5"]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('I'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('I'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[5]]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('I'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Join():
	stack = [[1,2,3],4]
	expected = [1,2,3,4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('J'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc","def"]
	expected = "abcdef"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('J'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1,[2,3,4]]
	expected = [1,2,3,4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('J'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2],[3,4]]
	expected = [1,2,3,4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('J'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_FactorsSubstringsPrefixes():
	stack = [20]
	expected = [1,2,4,5,10,20]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('K'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1]
	expected = [1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('K'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["adbcdbcd"]
	expected = {"b","c","d","bc","cd","bcd","db","dbc"}
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('K'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert set(simplify(stack[-1])) == expected

	stack = [[1,2,3]]
	expected = [[1],[1,2],[1,2,3]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('K'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Length():
	stack = ["abc"]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,"wrfwerfgbr",6]]
	expected = 4
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Map():
	stack = [5,[1,2,3]]
	expected = [[5,1],[5,2],[5,3]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('M'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["z","hi"]
	expected = [["z","h"],["z","i"]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('M'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_NegateSwapCase():
	stack = [5]
	expected = -5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('N'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [-1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('N'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["a"]
	expected = "A"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('N'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["aBc"]
	expected = "AbC"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('N'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Count():
	stack = [[1,2,3,4,5,4,3], 4]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('O'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abcdbacsabdcabca","a"]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('O'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Strip():
	stack = [[1, 2, 3, 4, 5, 4, 3, 2, 1], [1, 2]]
	expected = [3, 4, 5, 4, 3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('P'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["    Hello, World!    ", " "]
	expected = "Hello, World!"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('P'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Reduce():
	stack = [[[1,2],[3,4]]]
	expected = [[2,1],[4,3]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('R'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[[1,2]]]
	expected = [[2,1]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('R'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Stringify():
	stack = [5]
	expected = "5"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('S'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = "⟨1|2|3⟩"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('S'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["X"]
	expected = "X"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('S'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_TruthyIndices():
	stack = [1]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('T'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [-4]
	expected = -12
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('T'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[0,1,0,2]]
	expected = [1,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('T'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3,4]]
	expected = [0,1,2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('T'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Uniquify():
	stack = [[1,3,5,5]]
	expected = [1,3,5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('U'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abdbcdbch"]
	expected = "abdch"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('U'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Replace():
	stack = ["hela","a","lo"]
	expected = "hello"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('V'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["banana","n","nan"]
	expected = "banananana"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('V'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Wrap():
	stack = [1,2,3]
	expected = [1,2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('W'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = []
	expected = []
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('W'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello",1,9]
	expected = ["hello",1,9]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('W'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Interleave():
	stack = [[1,3,5],[2,4]]
	expected = [1,2,3,4,5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Y'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["srn","tig"]
	expected = "string"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Y'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Zip():
	stack = [[1,2],[3,4]]
	expected = [[1,3],[2,4]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Z'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc",[1,2,3]]
	expected = [["a",1],["b",2],["c",3]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Z'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Any():
	stack = [[1,2,3]]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('a'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[0,0,0]]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('a'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[0,1,2]]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('a'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["A"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('a'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["a"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('a'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["Hi"]
	expected = [1,0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('a'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Binary():
	stack = [5]
	expected = [1,0,1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('b'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [" "]
	expected = [[1,0,0,0,0,0]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('b'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[2,3]]
	expected = [[1,0],[1,1]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('b'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Contains():
	stack = ["abcdef","a"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('c'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["xyz","a"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('c'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3],1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('c'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3],0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('c'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Double():
	stack = [5]
	expected = 10
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('d'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('d'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2]]
	expected = [2,4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('d'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["x"]
	expected = "xx"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('d'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["ha"]
	expected = "haha"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('d'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Exponentiation():
	stack = [5,3]
	expected = 125
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('e'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [0,0]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('e'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello",2]
	expected = "hlo"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('e'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Flatten():
	stack = [135]
	expected = [1,3,5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('f'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hi"]
	expected = ["h","i"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('f'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[[[1,2],3,[[4,[5]],6],7],[8,[9]]]]
	expected = [1,2,3,4,5,6,7,8,9]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('f'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [-1]
	expected = ["-",1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('f'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Minimum():
	stack = ["abc"]
	expected = "a"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('g'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,4,-2]]
	expected = -2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('g'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[5,3,9]]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('g'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Head():
	stack = ["hello"]
	expected = "h"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('h'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('h'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Index():
	stack = ["abc",1]
	expected = "b"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3], 0]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[2,3,4,5], [2]]
	expected = [2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,3,5,7],[1,3]]
	expected = [3,5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3,4,5,6,7,8,9,10],[1,8,2]]
	expected = [2,4,6,8]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Join():
	stack = [[1,2,3],"penguin"]
	expected = "1penguin2penguin3"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('j'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [["he","","o, wor","d!"], "l"]
	expected = "hello, world!"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('j'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_CumulativeGroups():
	stack = ["hello",3]
	expected = ["hel","ell","llo"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('l'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["cake",2]
	expected = ["ca","ak","ke"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('l'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["cheese","cake"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('l'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["cheese","salads"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('l'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Mirror():
	stack = [123]
	expected = 444
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('m'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hi"]
	expected = "hiih"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('m'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = [1,2,3,3,2,1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('m'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Remove():
	stack = ["hello","l"]
	expected = "heo"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('o'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3,1,2],1]
	expected = [2,3,2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('o'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["bananas and naan","an"]
	expected = "bas d na"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('o'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Prepend():
	stack = ["ld","wor"]
	expected = "world"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('p'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3],13]
	expected = [13,1,2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('p'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[3,4,5],"23"]
	expected = ["23",3,4,5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('p'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Uneval():
	stack = ["\\"]
	expected = "`\\`"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('q'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["`"]
	expected = "`\\``"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('q'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["a"]
	expected = "`a`"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('q'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Range():
	stack = [3,6]
	expected = [3,4,5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('r'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4,8]
	expected = [4,5,6,7]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('r'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_sort():
	stack = [[3,1,2]]
	expected = [1,2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('s'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["bca"]
	expected = "abc"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('s'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Tail():
	stack = ["hello"]
	expected = "o"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('t'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('t'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_MinusOne():
	stack = []
	expected = -1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('u'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Listify():
	stack = [1]
	expected = [1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('w'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello"]
	expected = ["hello"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('w'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = [[1,2,3]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('w'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Uninterleave():
	stack = ["abcde"]
	expected = "bd"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('y'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3,4]]
	expected = [2,4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('y'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Zipself():
	stack = [[1,2,3]]
	expected = [[1,1],[2,2],[3,3]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('z'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["zap"]
	expected = [["z","z"], ["a","a"],["p","p"]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('z'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_MaxbyTail():
	stack = [[[3,4],[9,2]]]
	expected = [3,4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↑'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[[1,2,3],[2,5]]]
	expected = [2,5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↑'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_MinbyTail():
	stack = [[[3,4],[9,2]]]
	expected = [9,2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↓'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[[1,2,3],[2,5]]]
	expected = [1,2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↓'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_DyadicMaximum():
	stack = [5,3]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∴'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello","goodbye"]
	expected = "hello"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∴'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3,"(stuff)"]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∴'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_DyadicMinimum():
	stack = [5,3]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∵'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello","goodbye"]
	expected = "goodbye"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∵'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3,"(stuff)"]
	expected = "(stuff)"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∵'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_IncrementSpaceReplaceWith0():
	stack = [5]
	expected = 6
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('›'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[3,4]]
	expected = [4,5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('›'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["  101"]
	expected = "00101"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('›'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Decrement():
	stack = [5]
	expected = 4
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('‹'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[3,4]]
	expected = [2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('‹'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello"]
	expected = "hello-"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('‹'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Parity():
	stack = [2]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∷'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∷'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello!"]
	expected = "lo!"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∷'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_EmptyString():
	stack = []
	expected = ""
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¤'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Space():
	stack = []
	expected = " "
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ð'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ToBaseTenFromCustomBase():
	stack = [43,5]
	expected = 23
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('β'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["banana","nab"]
	expected = 577
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('β'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[15,23,9],31]
	expected = 15137
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('β'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_FromBaseTenToCustomBase():
	stack = [1234567,"abc"]
	expected = "cacccabbbbcab"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1234567,5]
	expected = [3,0,4,0,0,1,2,3,2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [928343,["he","ll","o"]]
	expected = ["ll","o","he","o","he","ll","ll","ll","ll","he","he","he","o"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Absolutevalue():
	stack = [1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ȧ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [-1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ȧ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [" ch ee s e "]
	expected = "cheese"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ȧ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[-1,2,-5]]
	expected = [1,2,5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ȧ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Boolify():
	stack = [0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḃ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḃ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[69, 0]]
	expected = [1, 0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḃ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["x"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḃ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_NotOne():
	stack = [[1, 0]]
	expected = [0, 1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ċ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["1"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ċ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ċ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ċ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Divmod():
	stack = [5,3]
	expected = [1,2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḋ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abcd",3]
	expected = ["abc","abd","acd","bcd"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḋ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3],2]
	expected = [[1,2],[1,3],[2,3]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḋ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abcdef", "Joe"]
	expected = ["Joedef"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḋ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Enumerate():
	stack = ["abc"]
	expected = [[0,"a"],[1,"b"],[2,"c"]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ė'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = [[0,1],[1,2],[2,3]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ė'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Find():
	stack = [[1,2,3],2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḟ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello","l"]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḟ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Gcd():
	stack = [[1,3,2]]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ġ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[60,42,108]]
	expected = 6
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ġ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [50,35]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ġ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["laugh","cough"]
	expected = "ugh"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ġ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_HeadExtract():
	stack = ["hello"]
	expected = "ello"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḣ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = [2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḣ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_FloorDivision():
	stack = [5,3]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḭ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello!",3]
	expected = "he"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḭ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3,"hello!"]
	expected = "he"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ḭ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_LeftJustifyGridifyInfiniteReplaceCollectuntilfale():
	stack = [1, 3, 2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ŀ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Mean():
	stack = [[1,2,3]]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṁ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[4,71,-63]]
	expected = 4
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṁ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_JoinByNothing():
	stack = [["a","b","c"]]
	expected = "abc"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṅ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = 123
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṅ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Slice():
	stack = ["hello",2]
	expected = "llo"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ȯ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3],1]
	expected = [2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ȯ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Powerset():
	stack = ["ab"]
	expected = [[],["a"],["b"],["a","b"]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1,2,3]
	expected = [[],[1],[2],[3],[1,2],[1,3],[2,3],[1,2,3]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Round():
	stack = [5.5]
	expected = 6
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṙ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3.2]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṙ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[5.5,3.2]]
	expected = [6,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṙ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [-4.7]
	expected = -5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṙ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [-4.5]
	expected = -4
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṙ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_FunctionSort():
	stack = [3,4]
	expected = [3,4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṡ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1,5]
	expected = [1,2,3,4,5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṡ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc1def2ghi","\\d+"]
	expected = ["abc","def","ghi"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṡ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_TailExtract():
	stack = ["abc"]
	expected = "c"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṫ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ṫ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ChunkWrap():
	stack = ["abcdef",2]
	expected = ["ab","cd","ef"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ẇ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3,4,5,6],3]
	expected = [[1,2,3],[4,5,6]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ẇ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Repeat():
	stack = [[1,2,3],3]
	expected = [[1,2,3],[1,2,3],[1,2,3]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ẋ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["x",5]
	expected = "xxxxx"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ẋ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ExclusiveRangeLength():
	stack = ["abc"]
	expected = [0,1,2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ẏ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2]]
	expected = [0,1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ẏ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_InclusiveRangeLength():
	stack = ["abc"]
	expected = [1,2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ż'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2]]
	expected = [1,2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ż'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_SquareRoot():
	stack = [4]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('√'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello"]
	expected = "hlo"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('√'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Ten():
	stack = []
	expected = 10
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₀'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Hundred():
	stack = []
	expected = 100
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₁'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_IsEven():
	stack = [5]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₂'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₂'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₂'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2]]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₂'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_DivisibleBythree():
	stack = [5]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₃'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [6]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₃'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hi"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₃'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1]]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₃'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_TwentySix():
	stack = []
	expected = 26
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₄'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_DivisibleByFive():
	stack = [4]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₅'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₅'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello"]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₅'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₅'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_SixtyFour():
	stack = []
	expected = 64
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₆'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_OneTwentyEight():
	stack = []
	expected = 128
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₇'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_TwoFiftySix():
	stack = []
	expected = 256
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('₈'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Newline():
	stack = []
	expected = "\\n"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¶'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_JoinOnNewlines():
	stack = [[1, 2, 3, 4, 5, 6]]
	expected = "1\n2\n3\n4\n5\n6"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⁋'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [["Hello", "World!"]]
	expected = "Hello\nWorld!"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⁋'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_VerticalJoin():
	stack = [["abc", "def", "ghi"]]
	expected = "adg\nbeh\ncfi"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('§'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [["***", "****", "*****"]]
	expected = "  *\n **\n***\n***\n***"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('§'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_AbsoluteDifferencePaddedVerticalJoin():
	stack = [5, 1]
	expected = 4
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ε'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1, 5]
	expected = 4
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ε'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3, 3]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ε'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [["***", "****", "*****"], "."]
	expected = "..*\n.**\n***\n***\n***"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ε'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [["abc", "def", "ghi"], "."]
	expected = "adg\nbeh\ncfi"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ε'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Factorial():
	stack = [5]
	expected = 120
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¡'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello my name jeff. ur sussy baka"]
	expected = "Hello my name jeff. Ur sussy baka"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¡'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1, 2, 3, 4, 5]]
	expected = [1, 2, 6, 24, 120]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¡'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Summate():
	stack = [[1, 2, 3, 4, 5]]
	expected = 15
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∑'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [["abc", "def", 10]]
	expected = "abcdef10"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∑'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [12345]
	expected = 15
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∑'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_CumulativeSum():
	stack = [12345]
	expected = [1, 3, 6, 10, 15]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¦'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abcdef"]
	expected = ["a", "ab", "abc", "abcd", "abcde", "abcdef"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¦'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1, 2, 3, 4, 5]]
	expected = [1, 3, 6, 10, 15]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¦'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_AllEqual():
	stack = [1111]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('≈'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["acc"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('≈'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1, 2, 2, 1]]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('≈'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[]]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('≈'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Assign():
	stack = [[1, 2, 3, 4], 1, 0]
	expected = [1, 0, 3, 4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ȧ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["Hello ", ", World!", 5]
	expected = "Hello, World!"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ȧ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [69320, 2, 4]
	expected = [6, 9, 4, 2, 0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ȧ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Bifurcate():
	stack = [203]
	expected = 302
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ḃ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc"]
	expected = "cab"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ḃ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1, 2, 3, 4]]
	expected = [4, 3, 2, 1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ḃ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Counts():
	stack = [[1, 2, 2, 3, 3, 3, 3]]
	expected = [[1, 1], [2, 2], [3, 4]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ċ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["Hello, World!"]
	expected = [["W", 1], ["!", 1], [" ", 1], ["o", 2], ["d", 1], [",", 1], ["H", 1], ["l", 3], ["e", 1], ["r", 1]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ċ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_IsDivisibleArbitraryDuplicate():
	stack = [15, 5]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ḋ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc", 3]
	expected = "abc"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ḋ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[5, 13, 29, 48, 12], 2]
	expected = [0, 0, 0, 1, 1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ḋ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_VyxalExecReciprocal():
	stack = [[2, 3, -1]]
	expected = [0.5, 1/3, -1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ė'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["kH"]
	expected = "Hello, World!"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ė'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Groupconsecutive():
	stack = [[1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 5, 5]]
	expected = [[1, 1, 1], [2, 2, 2, 2, 2, 2], [3, 3, 3, 3, 3], [4, 4], [5, 5]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ġ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["Hello, World!"]
	expected = [["H"], ["e"], ["l", "l"], ["o"], [","], [" "], ["W"], ["o"], ["r"], ["l"], ["d"], ["!"]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ġ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_HeadRemoveBehead():
	stack = [[0, [43, 69], "foo"]]
	expected = [[43, 69], "foo"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ḣ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[]]
	expected = []
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ḣ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["foo"]
	expected = "oo"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ḣ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [""]
	expected = ""
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ḣ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1234.56]
	expected = 234.56
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ḣ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [0.2]
	expected = 0.2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ḣ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Indexintoorfindcycle():
	stack = [["foo", "bar", -69, 420, "baz"], [0, 2, 4]]
	expected = ["foo", -69, "baz"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('İ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Transliterate():
	stack = ["abcdefcba","abc","123"]
	expected = "123def321"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ŀ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,0], [2], [5]]
	expected = [1,5,0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ŀ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc","ab",["bb","cc"]]
	expected = ["bb","cc","c"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ŀ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Insert():
	stack = [[1,3,4],1,2]
	expected = [1,2,3,4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṁ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["wyz",1,"x"]
	expected = "wxyz"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṁ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["jknop",2,"lm"]
	expected = "jklmnop"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṁ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Integerpartitions():
	stack = [5]
	expected = [[1,1,1,1,1],[2,1,1,1],[3,1,1],[2,2,1],[4,1],[3,2],[5]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṅ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello"]
	expected = "h e l l o"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṅ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = "1 2 3"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṅ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Over():
	stack = [4,5]
	expected = 4
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ȯ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hi","bye"]
	expected = "hi"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ȯ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Permutations():
	stack = ["abc"]
	expected = ["abc","abc","bac","bca","cab","cba"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2]]
	expected = [[1,2],[2,1]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Reverse():
	stack = [203]
	expected = 302
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṙ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc"]
	expected = "cab"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṙ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1, 2, 3, 4]]
	expected = [4, 3, 2, 1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṙ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Vectorisedsums():
	stack = [[[1,2,3],[4,5,6]]]
	expected = [6, 15]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṡ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[3,4,5]]
	expected = [3, 4, 5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṡ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[[1,2,3], [1, 2, 3, 4]]]
	expected = [6, 10]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṡ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_TailRemove():
	stack = ["1234"]
	expected = "234"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṫ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = [1,2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ṫ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_SplitAndKeepDelimiter():
	stack = ["a b c"," "]
	expected = ["a"," ","b"," ","c"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ẇ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["xyzabc123abc","b"]
	expected = ["xyza","b","c123a","b","c"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ẇ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_CartesianProduct():
	stack = ["ab","cd"]
	expected = ["ac","ad","bc","bd"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ẋ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2],[3,4]]
	expected = [[1,3],[1,4],[2,3],[2,4]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ẋ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_SliceUntil():
	stack = ["abc",1]
	expected = "a"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ẏ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3],2]
	expected = [1,2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ẏ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_SliceFromOneUntil():
	stack = ["abc",2]
	expected = "b"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ż'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3],3]
	expected = [2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ż'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Square():
	stack = [5]
	expected = 25
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello"]
	expected = ["hel","lo"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["bye"]
	expected = ["by","e"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = [1,4,9]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Shift():
	stack = [1,4,5]
	expected = 4
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∇'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["my","hi","bye"]
	expected = "hi"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∇'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Ceiling():
	stack = [5]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⌈'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4.5]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⌈'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1.52,2.9,3.3]]
	expected = [2,3,4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⌈'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello world"]
	expected = ["hello","world"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⌈'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Floor():
	stack = [5.3]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⌊'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[5.3,4.7]]
	expected = [4,5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⌊'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["123abc"]
	expected = 123
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⌊'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Deltas():
	stack = [1,2,3]
	expected = [1,1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¯'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1,1,1]
	expected = [0,0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¯'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [40,61,3]
	expected = [21,-58]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('¯'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Sign():
	stack = [1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('±'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hi"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('±'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [-5]
	expected = -1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('±'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('±'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_RightBitShift():
	stack = [4,1]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↳'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [8,"green"]
	expected = "   green"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↳'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello","cheeseburger"]
	expected = "       hello"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↳'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_LeftBitShift():
	stack = [4,1]
	expected = 8
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↲'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [8,"green"]
	expected = "green   "
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↲'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello","cheeseburger"]
	expected = "hello       "
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↲'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_BitwiseAnd():
	stack = [420, 69]
	expected = 4
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⋏'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc", 10]
	expected = "   abc    "
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⋏'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["no", "yesnt"]
	expected = " no "
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⋏'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_BitwiseOr():
	stack = [420, 69]
	expected = 485
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⋎'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2, "abc"]
	expected = "ab"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⋎'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc", 2]
	expected = "ab"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⋎'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["Hello", "lower"]
	expected = "Hellower"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⋎'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_BitwiseXor():
	stack = [420, 69]
	expected = 481
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('꘍'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5, "ab"]
	expected = "     ab"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('꘍'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["ab", 5]
	expected = "ab     "
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('꘍'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["atoll", "bowl"]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('꘍'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_BitwiseNot():
	stack = [220]
	expected = -221
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ꜝ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["Hello"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ꜝ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_RandomChoice():
	stack = [[1,2,3]]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('℅'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('℅'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('℅'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_LesserThanorEqualTo():
	stack = [1,2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('≤'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_GreaterThanorEqualTo():
	stack = [1,2]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('≥'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_NotEqualTo():
	stack = [1,2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('≠'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ExactlyEqualTo():
	stack = [1,2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⁼'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_SetUnion():
	stack = [[1,2],[2,3,4]]
	expected = [1,2,3,4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∪'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Tranpose():
	stack = [[1,2],[2,3,4]]
	expected = [[1, 2], [2, 3], [4]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∩'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_SymmetricSetdifference():
	stack = [[1,2],[2,3,4]]
	expected = [1,3,4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⊍'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_GradeUp():
	stack = [[420,69,1337]]
	expected = [2,1,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⇧'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["Heloo"]
	expected = "HELOO"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⇧'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4]
	expected = 6
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⇧'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_GradeDown():
	stack = [[420,69,1337]]
	expected = [3,1,2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⇩'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["Heloo"]
	expected = "heloo"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⇩'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('⇩'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Removenonalphabets():
	stack = ["Helo1233adc__"]
	expected = "Heloadc"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ǎ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [8]
	expected = 256
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ǎ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Nthprime():
	stack = [3]
	expected = 7
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ǎ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc"]
	expected = ["a","ab","abc","","b","bc","","","c","","",""]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ǎ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Primefactorization():
	stack = [45]
	expected = [3,5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ǐ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc"]
	expected = "abca"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ǐ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Primefactors():
	stack = [45]
	expected = [3, 3, 5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ǐ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc def"]
	expected = "Abc Def"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ǐ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Multiplicity():
	stack = [45, 3]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ǒ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["aaabbbc", "ab"]
	expected = "c"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ǒ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Modulo3():
	stack = [45]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ǒ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [""]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ǒ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_RotateLeft():
	stack = [3, [4, 5, 5, 6]]
	expected = [6, 4, 5, 5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ǔ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3, [1, 2, 3, 4]]
	expected = [2, 3, 4, 1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Ǔ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_RotateRight():
	stack = [3, [4, 5, 5, 6]]
	expected = [5, 5, 6, 4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ǔ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3, [1, 2, 3, 4]]
	expected = [4, 1, 2, 3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ǔ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_SplitOnnewlines():
	stack = ["a\nb\nc"]
	expected = ["a", "b", "c"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↵'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = 1000
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('↵'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ProductofArray():
	stack = [3,4,5]
	expected = 60
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Π'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Uppercasealphabet():
	stack = []
	expected = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kA'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_eEulersnumber():
	stack = []
	expected = 2.718281828459045
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ke'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Fizz():
	stack = []
	expected = "Fizz"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kf'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Buzz():
	stack = []
	expected = "Buzz"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kb'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_FizzBuzz():
	stack = []
	expected = "FizzBuzz"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kF'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_HelloWorld():
	stack = []
	expected = "Hello, World!"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kH'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_HelloWorld():
	stack = []
	expected = "Hello World"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kh'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_1000():
	stack = []
	expected = 1000
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k1'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_1000():
	stack = []
	expected = 10000
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k2'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_10000():
	stack = []
	expected = 100000
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k3'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_1000000():
	stack = []
	expected = 1000000
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k4'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Lowercasealphabet():
	stack = []
	expected = "abcdefghijklmnopqrstuvwxyz"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ka'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Lowercaseanduppercasealphabet():
	stack = []
	expected = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kL'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Digits():
	stack = []
	expected = "0123456789"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kd'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Hexdigitslowercase():
	stack = []
	expected = "0123456789abcdef"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k6'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Hexdigitsuppercase():
	stack = []
	expected = "0123456789ABCDEF"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k^'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Octaldigits():
	stack = []
	expected = "01234567"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ko'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Punctuation():
	stack = []
	expected = string.punctuation
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kp'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_PrintableASCII():
	stack = []
	expected = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~'
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kP'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Digitslowercasealphabetanduppercasealphabet():
	stack = []
	expected = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kr'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Uppercaseandlowercasealphabet():
	stack = []
	expected = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kB'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Uppercasealphabetreversed():
	stack = []
	expected = "ZYXWVUTSRQPONMLKJIHGFEDCBA"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kZ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Lowercasealphabetreversed():
	stack = []
	expected = "zyxwvutsrqponmlkjihgfedcba"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kz'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Uppercaseandlowercasealphabetreversed():
	stack = []
	expected = "ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kl'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Pi():
	stack = []
	expected = 3.141592653589793
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ki'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_NaN():
	stack = []
	expected = math.nan
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kn'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Goldenratiophi():
	stack = []
	expected = 1.618033988749895
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kg'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Bracessquarebracketsanglebracketsandparentheses():
	stack = []
	expected = "{}[]<>()"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kβ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Parenthesessquarebracketsandbraces():
	stack = []
	expected = "()[]{}"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kḂ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Parenthesesandsquarebrackets():
	stack = []
	expected = "()[]"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kß'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Openingbrackets():
	stack = []
	expected = "([{"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kḃ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Closingbrackets():
	stack = []
	expected = ")]}"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k≥'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Openingbracketswith():
	stack = []
	expected = "([{<"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k≤'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Closingbracketswith():
	stack = []
	expected = ")]}>"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kΠ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Lowercasevowels():
	stack = []
	expected = "aeiou"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kv'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Upercasevowels():
	stack = []
	expected = "AEIOU"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kV'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Lowercaseanduppercasevowels():
	stack = []
	expected = "aeiouAEIOU"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k∨'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_12():
	stack = []
	expected = [1, 2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k½'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_4294967296():
	stack = []
	expected = 4294967296
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kḭ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_11():
	stack = []
	expected = [1, -1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k+'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_11():
	stack = []
	expected = [-1, 1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k-'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_01():
	stack = []
	expected = [0, 1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k≈'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Slashes():
	stack = []
	expected = "/\\"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k/'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_360():
	stack = []
	expected = 360
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kR'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_https():
	stack = []
	expected = "https://"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kW'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_http():
	stack = []
	expected = "http://"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k℅'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_httpswww():
	stack = []
	expected = "https://www."
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k↳'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_httpwww():
	stack = []
	expected = "http://www."
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_512():
	stack = []
	expected = 512
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k¶'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_1024():
	stack = []
	expected = 1024
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k⁋'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_2048():
	stack = []
	expected = 2048
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k¦'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_4096():
	stack = []
	expected = 4096
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kṄ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_8192():
	stack = []
	expected = 8192
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kṅ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_16384():
	stack = []
	expected = 16384
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k¡'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_32768():
	stack = []
	expected = 32768
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kε'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_65536():
	stack = []
	expected = 65536
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k₴'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_2147483648():
	stack = []
	expected = 2147483648
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k×'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Lowercaseconsonantswithy():
	stack = []
	expected = "bcdfghjklmnpqrstvwxyz"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k⁰'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_BFcommandset():
	stack = []
	expected = "[]<>-+.,"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kT'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Bracketpairlist():
	stack = []
	expected = ["()","[]","{}","<>"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Nestedbrackets():
	stack = []
	expected = "([{<>}])"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kṖ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Amogus():
	stack = []
	expected = "ඞ"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('kS'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_220():
	stack = []
	expected = 1048576
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k₂'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_230():
	stack = []
	expected = 1073741824
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k₃'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_LowercaseVowelsWithY():
	stack = []
	expected = "aeiouy"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k∪'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_UppercaseVowelsWithY():
	stack = []
	expected = "AEIOUY"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k⊍'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_VowelsWithY():
	stack = []
	expected = "aeiouyAEIOUY"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('k∩'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Cosine():
	stack = [3.141519265]
	expected = -1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆c'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [0]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆c'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [6.283185307]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆c'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ArcCosine():
	stack = [-1]
	expected = 3.141519265
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆C'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆C'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_QuadraticSolver():
	stack = [1, 2]
	expected = [-0.5, 0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆q'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1, -2]
	expected = [0.5, 0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆q'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [69, 420]
	expected = [-0.16428571428571428, 0.0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆q'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_GeneralQuadraticSolver():
	stack = [1, 2]
	expected = [-1, -1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆Q'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1, -2]
	expected = [1, 1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆Q'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [69, 420]
	expected = [-0.16428571428571428, 0.0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆Q'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Sine():
	stack = [3.141519265]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆s'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆s'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [6.283185307]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆s'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ArcSine():
	stack = [-1]
	expected = -1.5707963267948966
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆S'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1]
	expected = 1.5707963267948966
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆S'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Tangent():
	stack = [3.141519265]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆t'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆t'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [6.283185307]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆t'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ArcTangent():
	stack = [-1]
	expected = -0.78539816
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆T'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1]
	expected = 0.78539816
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆T'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_PolynomialSolver():
	stack = [[4, -1005, 3, 4]]
	expected = [251.2469990481482, 0.06460672339563359, -0.06160577154387768]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆P'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[69, 420, -1]]
	expected = [-6.089336543523048, 0.0023800217839172796]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆P'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_nPickrnpr():
	stack = [[3, 4, 5, 6], [1, 2, 3, 4]]
	expected = [3,12,60,360]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆ƈ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_CopySign():
	stack = [-1, 1]
	expected = -1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆±'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1, -1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆±'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [-1, -1]
	expected = -1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆±'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1, 1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆±'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_SumofProperDivisors():
	stack = [43]
	expected = [1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆K'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [12]
	expected = [16]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆K'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [97]
	expected = [1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆K'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [34]
	expected = [20]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆K'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [18]
	expected = [21]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆K'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_PerfectSquare():
	stack = [1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [9]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [16]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [25]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [36]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [37]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [-1]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1.5]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆²'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_eraisedtopowera():
	stack = [0]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆e'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1]
	expected = 2.718281828459045
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆e'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2]
	expected = 7.38905609893065
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆e'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = 20.085536923187668
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆e'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_eraisedtopowera1():
	stack = [0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆E'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1]
	expected = 1.718281828459045
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆E'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2]
	expected = 6.38905609893065
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆E'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = 19.085536923187668
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆E'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_NaturalLogarithm():
	stack = [1]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2]
	expected = 0.6931471805599453
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = 1.0986122886681098
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4]
	expected = 1.3862943611198906
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = 1.6094379124341003
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [6]
	expected = 1.791759469228055
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [7]
	expected = 1.9459101490553132
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [8]
	expected = 2.0794415416798357
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [9]
	expected = 2.1972245773362196
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [10]
	expected = 2.302585092994046
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [11]
	expected = 2.3978952727983707
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [12]
	expected = 2.4849066497880004
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [13]
	expected = 2.5649493574615367
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [14]
	expected = 2.6390573296152586
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [15]
	expected = 2.70805020110221
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [16]
	expected = 2.7725887222397813
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [17]
	expected = 2.833213344056216
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [18]
	expected = 2.889279713667798
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [19]
	expected = 2.940980663340675
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [20]
	expected = 2.98885267308838
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [21]
	expected = 3.03164900591155
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [22]
	expected = 3.069078890930626
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [23]
	expected = 3.101444148692257
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [24]
	expected = 3.129283016944946
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆L'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Logarithmlog_2():
	stack = [1]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆l'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆l'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_CommonLogarithm():
	stack = [1]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2]
	expected = 0.3010299956639812
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = 0.47712125471966244
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4]
	expected = 0.6020599913279624
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = 0.6989700043360189
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [6]
	expected = 0.7781512503836436
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [7]
	expected = 0.8450980400142568
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [8]
	expected = 0.9030899869919435
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [9]
	expected = 0.9542425094393249
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [10]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [11]
	expected = 1.0373648063829815
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [12]
	expected = 1.0794415416798357
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [13]
	expected = 1.1180339887498949
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [14]
	expected = 1.1512925464970229
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [15]
	expected = 1.1832159566199232
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [16]
	expected = 1.2138765413770488
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [17]
	expected = 1.2415866898954712
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [18]
	expected = 1.2686525285981229
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [19]
	expected = 1.293995220556003
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [20]
	expected = 1.318385620278294
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [21]
	expected = 1.341998858493418
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [22]
	expected = 1.3647343448018976
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [23]
	expected = 1.3867261498125963
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [24]
	expected = 1.4079441410500514
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [25]
	expected = 1.4283545351906137
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [26]
	expected = 1.4479441410500514
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [27]
	expected = 1.4667563108421037
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆τ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_StraightLineDistance():
	stack = [[69, 420], [21, 42]]
	expected = 381.03543142337827
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆d'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ToDegrees():
	stack = [0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1]
	expected = 57.29577951308232
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2]
	expected = 114.59155902616465
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = 171.88733853924697
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4]
	expected = 229.18264859012092
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = 286.47895353103696
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [6]
	expected = 343.77426756035296
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [7]
	expected = 401.06957159047897
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [8]
	expected = 458.36387551059497
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [9]
	expected = 515.65910164161098
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [10]
	expected = 573.95432767272599
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [11]
	expected = 632.24964270280999
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [12]
	expected = 690.54595673282499
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [13]
	expected = 748.84127076392999
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [14]
	expected = 807.13658579503499
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [15]
	expected = 865.43189982514
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [16]
	expected = 923.72721485605
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [17]
	expected = 982.02262888616
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [18]
	expected = 1040.31894291626
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [19]
	expected = 1098.61525694636
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [20]
	expected = 1156.91057097646
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [21]
	expected = 1215.20588500656
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [22]
	expected = 1273.50119903646
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [23]
	expected = 1331.79651306656
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [24]
	expected = 1390.09182709656
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [25]
	expected = 1448.38714112656
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [26]
	expected = 1506.68245515656
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [27]
	expected = 1564.97776018656
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆D'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ToRadians():
	stack = [0]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆R'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [90]
	expected = 1.5707963267948966
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆R'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [180]
	expected = 3.141592653589793
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆R'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [270]
	expected = 4.71238898038469
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆R'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [360]
	expected = 6.283185307179586
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆R'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_NextPrimeAfteraNumber():
	stack = [1]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆Ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆Ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆Ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆Ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = 7
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆Ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [69]
	expected = 71
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆Ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_FirstPrimeBeforeaNumber():
	stack = [1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [69]
	expected = 67
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆ṗ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_NearestPrimetoaNumber():
	stack = [1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆p'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆p'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆p'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆p'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆p'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [38]
	expected = 37
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆p'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [40]
	expected = 41
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆p'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [69]
	expected = 71
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆p'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_PolynomialfromRoots():
	stack = [[1, 2, 3]]
	expected = [-6, 11, -6, 1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆ṙ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[19, 43, 12, 5, 129]]
	expected = [-6323580, 2320581, -266708, 12122, -208, 1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆ṙ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_RoundtonDecimalPlaces():
	stack = [1.2345, 2]
	expected = 1.23
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆W'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1.2345, 3]
	expected = 1.235
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆W'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1.2345, 4]
	expected = 1.2345
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆W'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1.2345, 5]
	expected = 1.2345
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆W'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_LeastCommonMultiple():
	stack = [1, 2]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆Ŀ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [69, 420]
	expected = 9660
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆Ŀ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_nthDigitofPi():
	stack = [0]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2]
	expected = 4
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = 9
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [6]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [7]
	expected = 6
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [8]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [9]
	expected = 9
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆i'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_nthDigitofe():
	stack = [0]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆ė'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1]
	expected = 7
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆ė'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆ė'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = 8
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆ė'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_nthFibonacciNumber():
	stack = [0]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆f'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆f'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [2]
	expected = 2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆f'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [3]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆f'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [4]
	expected = 5
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆f'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = 8
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆f'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [6]
	expected = 13
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆f'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [7]
	expected = 21
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆f'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [8]
	expected = 34
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆f'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [9]
	expected = 55
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('∆f'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Parenthesise():
	stack = ["xyz"]
	expected = "(xyz)"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øb'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = "(5)"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øb'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = ["(1)","(2)","(3)"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øb'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Bracketify():
	stack = ["xyz"]
	expected = "[xyz]"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øB'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = "[5]"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øB'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = ["[1]","[2]","[3]"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øB'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_CurlyBracketify():
	stack = ["xyz"]
	expected = "{xyz}"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øḃ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = "{5}"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øḃ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = ["{1}","{2}","{3}"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øḃ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_AngleBracketify():
	stack = ["xyz"]
	expected = "<xyz>"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øḂ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [5]
	expected = "<5>"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øḂ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = ["<1>","<2>","<3>"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øḂ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_BalancedBrackets():
	stack = ["xyz"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øβ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["([)]"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øβ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["({<[]>})"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øβ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [")("]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øβ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_CustomPadLeft():
	stack = ["xyz","x",4]
	expected = "xxyz"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ø↳'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["123","&",8]
	expected = "&&&&&123"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ø↳'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["324"," ",2]
	expected = "324"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ø↳'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_CustomPadRight():
	stack = ["xyz","x",4]
	expected = "xyzx"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ø↲'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["123","&",8]
	expected = "123&&&&&"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ø↲'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["324"," ",2]
	expected = "324"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ø↲'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_FlipBracketsVerticalPalindromise():
	stack = ["(x"]
	expected = "(x)"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øM'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["{] "]
	expected = "{] [}"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øM'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["/*>X"]
	expected = "/*>X<*\\"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øM'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_RemoveUntilNochange():
	stack = ["((()))","()"]
	expected = ""
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øo'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["--+--+-",["--","+-"]]
	expected = "+"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øo'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ReplaceUntilNoChange():
	stack = ["xyzzzzz","yzz","yyyz"]
	expected = "xyyyyyyyyyz"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øV'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abb","ab","aa"]
	expected = "aaa"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øV'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_StringCompress():
	stack = ["hello"]
	expected = "«B²z«"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øc'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello world"]
	expected = "«⟇÷Ċ$⌈¢2«"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øc'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_NumberCompress():
	stack = [234]
	expected = "»⇧»"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øC'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [27914632409837421]
	expected = "»fðǐ4'∞Ẏ»"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øC'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Center():
	stack = [["ab","cdef"]]
	expected = [" ab ","cdef"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øĊ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [["xyz","a","bcdef"]]
	expected = [" xyz ","  a  ","bcdef"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øĊ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_RunLengthEncoding():
	stack = ["abc"]
	expected = [["a",1],["b",1],["c",1]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øe'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["aaa"]
	expected = [["a",3]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øe'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_RunLengthDecoding():
	stack = [[["x",3]]]
	expected = "xxx"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ød'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[["z",2],["a",3]]]
	expected = "zzaaa"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ød'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_DictionaryCompression():
	stack = ["withree"]
	expected = "`wi∧ḭ`"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øD'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello"]
	expected = "`ƈṙ`"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øD'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["Vyxal"]
	expected = "`₴ŀ`"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øD'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Grouponwords():
	stack = ["abc*xyz"]
	expected = ["abc","*","xyz"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øW'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["$$$"]
	expected = ["$","$","$"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øW'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Regexreplace():
	stack = [".{3}","hello","x"]
	expected = "xlo"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øṙ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["\\W","Hello, World!","E"]
	expected = "HelloEEWorldE"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øṙ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_StartsWith():
	stack = ["hello","h"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øp'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello","hello"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øp'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello","x"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øp'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["hello",""]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øp'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_PluraliseCount():
	stack = [4,"hello"]
	expected = "4 hellos"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øP'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [1,"hello"]
	expected = "1 hello"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øP'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [0,"hello"]
	expected = "0 hellos"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øP'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_VerticalMirror():
	pass #TODO implement this test!!!


def test_FlipBracketsVerticalMirror():
	stack = ["[}"]
	expected = "[}{]"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øṀ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [")X"]
	expected = ")XX("
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øṀ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["</tag>"]
	expected = "</tag><gat\\>"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øṀ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_StringPartitions():
	stack = ["ab"]
	expected = [["a","b"]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('øṖ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_AllUnique():
	stack = ["hello"]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þu'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["eeee"]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þu'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þu'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,1,1]]
	expected = 1
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þu'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_CartesianPower():
	stack = ["ab",2]
	expected = ["aa","ab","ba","bb"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ÞẊ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2],3]
	expected = [[1,1,1],[1,1,2],[1,2,1],[1,2,2],[2,1,1],[2,1,2],[2,2,1],[2,2,2]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ÞẊ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["abc",3]
	expected = ["aaa","aab","aac","aba","abb","abc","aca","acb","acc","baa","bab","bac","bba","bbb","bbc","bca","bcb","bcc","caa","cab","cac","cba","cbb","cbc","cca","ccb","ccc"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ÞẊ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Flattentodepth():
	stack = [[[[[[1]]]]],3]
	expected = [[1]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þf'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["xyz",[1,2,[3,4,[5,6]]]]
	expected = [1,2,3,4,[5,6]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þf'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ChunksOfSpecifiedLength():
	stack = ["abcdefghi",[2,3,4]]
	expected = ["ab","cde","fghi"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ÞC'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3,4,5], [2,3] ]
	expected = [[1,2],[3,4,5]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ÞC'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_AllLessThanIncreasing():
	stack = [[1,2,3,2,1,4,3,2,1], 3]
	expected = [1,2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þ<'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3,4,5,2,1], 4]
	expected = [1,2,3]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þ<'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Untruth():
	stack = [[1]]
	expected = [0,1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þǔ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[0,3,4,6]]
	expected = [1,0,0,1,1,0,1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þǔ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_MultidimensionalIndexing():
	stack = [[1,[2,3]],[0,1]]
	expected = 3
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þi'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [["xyzabc"], [0,4]]
	expected = "b"
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þi'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_MultidimensionalSearch():
	stack = [[[1,2,3],[4,5,6]], 5]
	expected = [1, 1]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þḟ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [["abc","def",["hij","klm","nop"]], "m"]
	expected = [1,1,2]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þḟ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_ZeroMatrix():
	stack = [[3,4]]
	expected = [[0,0,0],[0,0,0],[0,0,0],[0,0,0]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þm'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[2,3,2]]
	expected = [[0,0],[0,0]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þm'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_EvenlyDistribute():
	stack = [[1,2,3],6]
	expected = [3,4,5]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þ…'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3],5]
	expected = [3,4,4]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þ…'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_AllCombinations():
	stack = [[1,2,3]]
	expected = [[1],[2],[3],[1,2],[1,3],[2,1],[2,3],[3,1],[3,2],[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þ×'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = ["ab"]
	expected = ["a","b","ab","ba"]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þ×'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_UniquifyMask():
	stack = [[1,2,3,1,2,3]]
	expected = [1,1,1,0,0,0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ÞU'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[1,1,1,2,3,1,2,2,1,3]]
	expected = [1,0,0,1,1,0,0,0,0,0]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ÞU'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Diagonals():
	stack = [[1,2,3],[4,5,6],[7,8,9]]
	expected = [[1,5,9],[2,6],[3],[4,8],[7]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ÞD'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Sublists():
	stack = [[1,2,3]]
	expected = [[1],[2],[3],[1,2],[2,3],[1,2,3]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ÞS'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_TransposeWithFiller():
	stack = [[[1,2,3],[4,5]],0]
	expected = [[1,4],[2,5],[3,0]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ÞṪ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[[1,2,3,4],[5,6],[7,8,9],[0]],"X"]
	expected = [[1,5,7,0],[2,6,8,"X"],[3,"X",9,"X"],[4,"X","X","X"]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ÞṪ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_MatrixMultiplication():
	stack = [[[1,2],[3,4]],[[5,6],[7,8]]]
	expected = [[23,34],[31,46]]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ÞṀ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_MatrixDeterminant():
	stack = [[[1,2],[3,4]]]
	expected = -2
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ÞḊ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected

	stack = [[[1,2,3],[4,5,6],[7,8,9]]]
	expected = 0
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('ÞḊ'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Antidiagonal():
	stack = [[[1,2,3],[4,5,6],[7,8,9]]]
	expected = [3,5,7]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þ\\'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


def test_Diagonal():
	stack = [[[1,2,3],[4,5,6],[7,8,9]]]
	expected = [1,5,9]
	ctx = Context()
	ctx.stacks.append(stack)
	code = transpile('Þ/'); print(code)
	exec(code)
	ctx.stacks.pop()
	assert simplify(stack[-1]) == expected


