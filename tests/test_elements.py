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



def test_Combinations_Remove_FixedPoint():
	stack = ["cabbage", "abcde"]; expected = "cabbae"
	ctx = Context()
	code = transpile('↔')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1,3,5,6,7,7,1],[1,3,5]]; expected = [1,3,5,1]
	ctx = Context()
	code = transpile('↔')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1,2],2]; expected = [[1,1],[1,2],[2,1],[2,2]]
	ctx = Context()
	code = transpile('↔')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_Infinitereplacement():
	stack = ["{[[[]]]}","[]",""]; expected = "{}"
	ctx = Context()
	code = transpile('¢')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [1444,44,34]; expected = 1334
	ctx = Context()
	code = transpile('¢')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_Complement_CommaSplit():
	stack = [5]; expected = -4
	ctx = Context()
	code = transpile('⌐')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [-5]; expected = 6
	ctx = Context()
	code = transpile('⌐')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["a,b,c"]; expected = ["a","b","c"]
	ctx = Context()
	code = transpile('⌐')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_IsPrime_CaseCheck():
	stack = [2]; expected = 1
	ctx = Context()
	code = transpile('æ')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [4]; expected = 0
	ctx = Context()
	code = transpile('æ')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["a"]; expected = 0
	ctx = Context()
	code = transpile('æ')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["A"]; expected = 1
	ctx = Context()
	code = transpile('æ')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["!"]; expected = -1
	ctx = Context()
	code = transpile('æ')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_InclusiveZeroRange():
	stack = ["0"]; expected = 1
	ctx = Context()
	code = transpile('ʀ')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1]]; expected = 0
	ctx = Context()
	code = transpile('ʀ')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [3]; expected = [0,1,2,3]
	ctx = Context()
	code = transpile('ʀ')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_ExclusiveZeroRange():
	stack = ["0"]; expected = 1
	ctx = Context()
	code = transpile('ʁ')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1]]; expected = 0
	ctx = Context()
	code = transpile('ʁ')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [3]; expected = [0,1,2]
	ctx = Context()
	code = transpile('ʁ')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_InclusiveOneRange():
	stack = ["1"]; expected = 1
	ctx = Context()
	code = transpile('ɾ')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[0]]; expected = 0
	ctx = Context()
	code = transpile('ɾ')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [3]; expected = [1,2,3]
	ctx = Context()
	code = transpile('ɾ')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_ExclusiveOneRange():
	stack = ["1"]; expected = 1
	ctx = Context()
	code = transpile('ɽ')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[0]]; expected = 0
	ctx = Context()
	code = transpile('ɽ')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [3]; expected = [1,2]
	ctx = Context()
	code = transpile('ɽ')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_Choose_randomchoice_setsame():
	stack = [5,3]; expected = 10
	ctx = Context()
	code = transpile('ƈ')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["abc","aaccb"]; expected = 1
	ctx = Context()
	code = transpile('ƈ')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["abc","abcd"]; expected = 0
	ctx = Context()
	code = transpile('ƈ')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_Stacklength():
	stack = [0,1,2]; expected = 3
	ctx = Context()
	code = transpile('!')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [1,1,1,1,1]; expected = 5
	ctx = Context()
	code = transpile('!')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = []; expected = 0
	ctx = Context()
	code = transpile('!')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_Pair():
	stack = [1, 2]; expected = [1, 2]
	ctx = Context()
	code = transpile('"')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [1, 2, 3]; expected = [2, 3]
	ctx = Context()
	code = transpile('"')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1, 2, 3], "abc", 3]; expected = ["abc", 3]
	ctx = Context()
	code = transpile('"')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_Swap():
	stack = [1, 2]; expected = 1
	ctx = Context()
	code = transpile('$')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [1, 2, 3]; expected = 2
	ctx = Context()
	code = transpile('$')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1, 2, 3], "abc", 3]; expected = "abc"
	ctx = Context()
	code = transpile('$')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_Modulo_Format():
	stack = [5,3]; expected = 2
	ctx = Context()
	code = transpile('%')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["hello!",3]; expected = "o!"
	ctx = Context()
	code = transpile('%')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["Hel%ld!","lo, Wor"]; expected = "Hello, World!"
	ctx = Context()
	code = transpile('%')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["% and % and %",[1,2,3]]; expected = "1 and 2 and 3"
	ctx = Context()
	code = transpile('%')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_Multiplication():
	stack = [3,5]; expected = 15
	ctx = Context()
	code = transpile('*')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [4,-2]; expected = -8
	ctx = Context()
	code = transpile('*')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [4,"*"]; expected = "****"
	ctx = Context()
	code = transpile('*')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["x",5]; expected = "xxxxx"
	ctx = Context()
	code = transpile('*')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_Addition():
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



def test_Subtract():
	stack = [5, 4]; expected = 1
	ctx = Context()
	code = transpile('-')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [0, -5]; expected = 5
	ctx = Context()
	code = transpile('-')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["|", 5]; expected = "|-----"
	ctx = Context()
	code = transpile('-')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [3, "> arrow"]; expected = "---> arrow"
	ctx = Context()
	code = transpile('-')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["abcbde", "b"]; expected = "acde"
	ctx = Context()
	code = transpile('-')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["aaa", "a"]; expected = ""
	ctx = Context()
	code = transpile('-')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1, 2, 3], [1, 2, 3]]; expected = [0, 0, 0]
	ctx = Context()
	code = transpile('-')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[10, 20, 30], 5]; expected = [5, 15, 25]
	ctx = Context()
	code = transpile('-')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_Divide_Split():
	stack = [4,2]; expected = 2
	ctx = Context()
	code = transpile('/')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["abcdef",3]; expected = ["ab","cd","ef"]
	ctx = Context()
	code = transpile('/')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["1,2,3",","]; expected = ["1","2","3"]
	ctx = Context()
	code = transpile('/')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_Lessthan():
	stack = [1, 2]; expected = 1
	ctx = Context()
	code = transpile('<')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [2, 1]; expected = 0
	ctx = Context()
	code = transpile('<')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["a","b"]; expected = 1
	ctx = Context()
	code = transpile('<')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [-5,2]; expected = 1
	ctx = Context()
	code = transpile('<')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3],2]; expected = [1,0,0]
	ctx = Context()
	code = transpile('<')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_Equals():
	stack = [1, 1]; expected = 1
	ctx = Context()
	code = transpile('=')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [2, 1]; expected = 0
	ctx = Context()
	code = transpile('=')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["a","b"]; expected = 0
	ctx = Context()
	code = transpile('=')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["xyz","xyz"]; expected = 1
	ctx = Context()
	code = transpile('=')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3],2]; expected = [0,1,0]
	ctx = Context()
	code = transpile('=')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [1,"1"]; expected = 1
	ctx = Context()
	code = transpile('=')
	exec(code)
	assert simplify(stack[-1]) == expected



def test_GreaterThan():
	stack = [1, 2]; expected = 0
	ctx = Context()
	code = transpile('>')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [2, 1]; expected = 1
	ctx = Context()
	code = transpile('>')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["a","b"]; expected = 0
	ctx = Context()
	code = transpile('>')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [2,-5]; expected = 1
	ctx = Context()
	code = transpile('>')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = [[1,2,3],2]; expected = [0,0,1]
	ctx = Context()
	code = transpile('>')
	exec(code)
	assert simplify(stack[-1]) == expected

	stack = ["5",10]; expected = 0
	ctx = Context()
	code = transpile('>')
	exec(code)
	assert simplify(stack[-1]) == expected



