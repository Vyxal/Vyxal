import os, sys

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + '/..'
sys.path.insert(1, THIS_FOLDER)

from vyxal.transpile import *
from vyxal.context import Context
from vyxal.elements import *
from vyxal.LazyList import *
def test_LogicalNot():
	stack = [1]; expected = 0
	ctx = Context()
	code = transpile('¬')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [0]; expected = 1
	ctx = Context()
	code = transpile('¬')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["abc"]; expected = 0
	ctx = Context()
	code = transpile('¬')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [""]; expected = 1
	ctx = Context()
	code = transpile('¬')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]; expected = 0
	ctx = Context()
	code = transpile('¬')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[]]; expected = 1
	ctx = Context()
	code = transpile('¬')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_LogicalAnd():
	stack = [0, 0]; expected = 0
	ctx = Context()
	code = transpile('∧')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["", 1]; expected = ""
	ctx = Context()
	code = transpile('∧')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3], 0]; expected = 0
	ctx = Context()
	code = transpile('∧')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [1, 2]; expected = 2
	ctx = Context()
	code = transpile('∧')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_ReversedLogicalAnd():
	stack = [0, 0]; expected = 0
	ctx = Context()
	code = transpile('⟑')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["", 1]; expected = ""
	ctx = Context()
	code = transpile('⟑')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3], 0]; expected = 0
	ctx = Context()
	code = transpile('⟑')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [1, 2]; expected = 1
	ctx = Context()
	code = transpile('⟑')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_LogicalOr():
	stack = [0, 0]; expected = 0
	ctx = Context()
	code = transpile('∨')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["", 1]; expected = 1
	ctx = Context()
	code = transpile('∨')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3], 0]; expected = [1,2,3]
	ctx = Context()
	code = transpile('∨')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [1, 2]; expected = 1
	ctx = Context()
	code = transpile('∨')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_ReversedLogicalOr():
	stack = [0, 0]; expected = 0
	ctx = Context()
	code = transpile('⟇')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["", 1]; expected = 1
	ctx = Context()
	code = transpile('⟇')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3], 0]; expected = [1,2,3]
	ctx = Context()
	code = transpile('⟇')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [1, 2]; expected = 2
	ctx = Context()
	code = transpile('⟇')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_ItemSplit():
	stack = [123456]; expected = 6
	ctx = Context()
	code = transpile('÷')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["abc"]; expected = "c"
	ctx = Context()
	code = transpile('÷')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3]]; expected = 3
	ctx = Context()
	code = transpile('÷')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_AsteriskLiteral():
	stack = []; expected = "*"
	ctx = Context()
	code = transpile('×')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_MultiCommand():
	stack = [8, 2]; expected = 3.0
	ctx = Context()
	code = transpile('•')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["abcde", 4]; expected = "aaaabbbbccccddddeeee"
	ctx = Context()
	code = transpile('•')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["abcde", "FgHIj"]; expected = "AbCDe"
	ctx = Context()
	code = transpile('•')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3,4,5,6,7], [[8, 9], 10, 11, 12, [13, 14]]]; expected = [[1, 2], 3, 4, 5, [6, 7]]
	ctx = Context()
	code = transpile('•')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_FunctionCall():
	stack = [12]; expected = 2
	ctx = Context()
	code = transpile('†')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1, 0, 1]]; expected = [0, 1, 0]
	ctx = Context()
	code = transpile('†')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_SplitOn():
	stack = [1231234, 3]; expected = ["12", "12", "4"]
	ctx = Context()
	code = transpile('€')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["abc3def", 3]; expected = ["abc", "def"]
	ctx = Context()
	code = transpile('€')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1, 2, 3, 4, 3, 2, 1], 4]; expected = [[1, 2, 3], [3, 2, 1]]
	ctx = Context()
	code = transpile('€')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_Halve():
	stack = [8]; expected = 4
	ctx = Context()
	code = transpile('½')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["FizzBuzz"]; expected = ["Fizz", "Buzz"]
	ctx = Context()
	code = transpile('½')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[2, 4, 6, 8]]; expected = [1, 2, 3, 4]
	ctx = Context()
	code = transpile('½')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_add():
	stack = [1, 1]; expected = 2
	ctx = Context()
	code = transpile('+')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [0, -5]; expected = -5
	ctx = Context()
	code = transpile('+')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["abc", 5]; expected = "abc5"
	ctx = Context()
	code = transpile('+')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [5, "abc"]; expected = "5abc"
	ctx = Context()
	code = transpile('+')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["Hello, ", "World!"]; expected = "Hello, World!"
	ctx = Context()
	code = transpile('+')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3], 4]; expected = [5, 6, 7]
	ctx = Context()
	code = transpile('+')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3], [4,5,6]]; expected = [5, 7, 9]
	ctx = Context()
	code = transpile('+')
	exec(code)
	assert simplify(stack[-1]) == expected



