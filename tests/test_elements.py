# flake8: noqa
import os
import sys
import sympy
THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + '/..'
sys.path.insert(1, THIS_FOLDER)

from vyxal.transpile import *
from vyxal.elements import *
from vyxal.context import Context
from vyxal.helpers import *
from vyxal.LazyList import *

def test_LogicalNot():

    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¬')
    # print('¬', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¬')
    # print('¬', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¬')
    # print('¬', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [""]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¬')
    # print('¬', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¬')
    # print('¬', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[]]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¬')
    # print('¬', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_LogicalAnd():

    stack = [vyxalify(item) for item in [0, 0]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∧')
    # print('∧', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["", 1]]
    expected = vyxalify("")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∧')
    # print('∧', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3], 0]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∧')
    # print('∧', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1, 2]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∧')
    # print('∧', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_LogicalOr():

    stack = [vyxalify(item) for item in [0, 0]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∨')
    # print('∨', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["", 1]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∨')
    # print('∨', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3], 0]]
    expected = vyxalify([1,2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∨')
    # print('∨', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1, 2]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∨')
    # print('∨', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_RemoveatIndex():

    stack = [vyxalify(item) for item in [[1,2,3], 0]]
    expected = vyxalify([2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⟇')
    # print('⟇', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3], 1]]
    expected = vyxalify([1,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⟇')
    # print('⟇', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3, [1,2,3,1]]]
    expected = vyxalify([1,2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⟇')
    # print('⟇', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0, [1,2,3,1]]]
    expected = vyxalify([2,3,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⟇')
    # print('⟇', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ItemSplit():

    stack = [vyxalify(item) for item in [123456]]
    expected = vyxalify(6)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('÷')
    # print('÷', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify("c")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('÷')
    # print('÷', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('÷')
    # print('÷', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_AsteriskLiteral():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("*")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('×')
    # print('×', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_MultiCommand():

    stack = [vyxalify(item) for item in [8, 2]]
    expected = vyxalify(3.0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('•')
    # print('•', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abcde", 4]]
    expected = vyxalify("aaaabbbbccccddddeeee")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('•')
    # print('•', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abcde", "FgHIj"]]
    expected = vyxalify("AbCDe")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('•')
    # print('•', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3,4,5,6,7], [[8, 9], 10, 11, 12, [13, 14]]]]
    expected = vyxalify([[1, 2], 3, 4, 5, [6, 7]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('•')
    # print('•', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_FunctionCall():

    stack = [vyxalify(item) for item in [12]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('†')
    # print('†', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 0, 1]]]
    expected = vyxalify([0, 1, 0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('†')
    # print('†', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_SplitOn():

    stack = [vyxalify(item) for item in [1231234, 3]]
    expected = vyxalify(["12", "12", "4"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('€')
    # print('€', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc3def", 3]]
    expected = vyxalify(["abc", "def"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('€')
    # print('€', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, 3, 4, 3, 2, 1], 4]]
    expected = vyxalify([[1, 2, 3], [3, 2, 1]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('€')
    # print('€', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Halve():

    stack = [vyxalify(item) for item in [8]]
    expected = vyxalify(4)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('½')
    # print('½', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["FizzBuzz"]]
    expected = vyxalify(["Fizz", "Buzz"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('½')
    # print('½', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[2, 4, 6, 8]]]
    expected = vyxalify([1, 2, 3, 4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('½')
    # print('½', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_CombinationsRemoveFixedPointCollection():

    stack = [vyxalify(item) for item in ["cabbage", "abcde"]]
    expected = vyxalify("cabbae")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↔')
    # print('↔', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,3,5,6,7,7,1],[1,3,5]]]
    expected = vyxalify([1,3,5,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↔')
    # print('↔', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2],2]]
    expected = vyxalify([[1,1],[1,2],[2,1],[2,2]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↔')
    # print('↔', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_InfiniteReplacement():

    stack = [vyxalify(item) for item in ["{[[[]]]}","[]",""]]
    expected = vyxalify("{}")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¢')
    # print('¢', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1444,44,34]]
    expected = vyxalify(1334)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¢')
    # print('¢', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ComplementCommaSplit():

    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(-4)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⌐')
    # print('⌐', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [-5]]
    expected = vyxalify(6)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⌐')
    # print('⌐', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["a,b,c"]]
    expected = vyxalify(["a","b","c"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⌐')
    # print('⌐', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_IsPrimeCaseCheck():

    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('æ')
    # print('æ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('æ')
    # print('æ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["a"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('æ')
    # print('æ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["A"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('æ')
    # print('æ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["!"]]
    expected = vyxalify(-1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('æ')
    # print('æ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_InclusiveZeroRange():

    stack = [vyxalify(item) for item in ["a$c"]]
    expected = vyxalify([1, 0, 1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ʀ')
    # print('ʀ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1]]]
    expected = vyxalify([[0, 1]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ʀ')
    # print('ʀ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify([0,1,2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ʀ')
    # print('ʀ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ExclusiveZeroRange():

    stack = [vyxalify(item) for item in ["1234"]]
    expected = vyxalify("1234321")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ʁ')
    # print('ʁ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1]]]
    expected = vyxalify([[0]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ʁ')
    # print('ʁ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify([0,1,2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ʁ')
    # print('ʁ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_InclusiveOneRange():

    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify("ABC")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ɾ')
    # print('ɾ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[4, 5]]]
    expected = vyxalify([[1, 2, 3, 4], [1, 2, 3, 4, 5]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ɾ')
    # print('ɾ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify([1,2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ɾ')
    # print('ɾ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ExclusiveOneRangeLowercase():

    stack = [vyxalify(item) for item in ["1aBC"]]
    expected = vyxalify("1abc")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ɽ')
    # print('ɽ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[0]]]
    expected = vyxalify([[]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ɽ')
    # print('ɽ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify([1,2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ɽ')
    # print('ɽ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Chooserandomchoicesetsame():

    stack = [vyxalify(item) for item in [5,3]]
    expected = vyxalify(10)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ƈ')
    # print('ƈ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc","aaccb"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ƈ')
    # print('ƈ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc","abcd"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ƈ')
    # print('ƈ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Palindromise():

    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify([1,2,3,2,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∞')
    # print('∞', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3,4]]]
    expected = vyxalify([1,2,3,4,3,2,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∞')
    # print('∞', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3,4,5]]]
    expected = vyxalify([1,2,3,4,5,4,3,2,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∞')
    # print('∞', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3,4,5,6]]]
    expected = vyxalify([1,2,3,4,5,6,5,4,3,2,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∞')
    # print('∞', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello"]]
    expected = vyxalify("hellolleh")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∞')
    # print('∞', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_StackLength():

    stack = [vyxalify(item) for item in [0,1,2]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('!')
    # print('!', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1,1,1,1,1]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('!')
    # print('!', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in []]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('!')
    # print('!', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Pair():

    stack = [vyxalify(item) for item in [1, 2]]
    expected = vyxalify([1, 2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('"')
    # print('"', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1, 2, 3]]
    expected = vyxalify([2, 3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('"')
    # print('"', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, 3], "abc", 3]]
    expected = vyxalify(["abc", 3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('"')
    # print('"', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Swap():

    stack = [vyxalify(item) for item in [1, 2]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('$')
    # print('$', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1, 2, 3]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('$')
    # print('$', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, 3], "abc", 3]]
    expected = vyxalify("abc")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('$')
    # print('$', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ModuloFormat():

    stack = [vyxalify(item) for item in [5,3]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('%')
    # print('%', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello %!",3]]
    expected = vyxalify("hello 3!")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('%')
    # print('%', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["Hel%ld!","lo, Wor"]]
    expected = vyxalify("Hello, World!")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('%')
    # print('%', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["% and % and %",[1,2,3]]]
    expected = vyxalify("1 and 2 and 3")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('%')
    # print('%', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_MultiplicationArityChange():

    stack = [vyxalify(item) for item in [3,5]]
    expected = vyxalify(15)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('*')
    # print('*', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4,-2]]
    expected = vyxalify(-8)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('*')
    # print('*', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4,"*"]]
    expected = vyxalify("****")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('*')
    # print('*', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["x",5]]
    expected = vyxalify("xxxxx")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('*')
    # print('*', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["aeiou","hello"]]
    expected = vyxalify("alihu")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('*')
    # print('*', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Addition():

    stack = [vyxalify(item) for item in [1, 1]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('+')
    # print('+', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0, -5]]
    expected = vyxalify(-5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('+')
    # print('+', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc", 5]]
    expected = vyxalify("abc5")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('+')
    # print('+', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5, "abc"]]
    expected = vyxalify("5abc")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('+')
    # print('+', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["Hello, ", "World!"]]
    expected = vyxalify("Hello, World!")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('+')
    # print('+', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3], 4]]
    expected = vyxalify([5, 6, 7])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('+')
    # print('+', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3], [4,5,6]]]
    expected = vyxalify([5, 7, 9])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('+')
    # print('+', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Subtract():

    stack = [vyxalify(item) for item in [5, 4]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('-')
    # print('-', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0, -5]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('-')
    # print('-', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["|", 5]]
    expected = vyxalify("|-----")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('-')
    # print('-', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3, "> arrow"]]
    expected = vyxalify("---> arrow")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('-')
    # print('-', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abcbde", "b"]]
    expected = vyxalify("acde")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('-')
    # print('-', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["aaa", "a"]]
    expected = vyxalify("")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('-')
    # print('-', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, 3], [1, 2, 3]]]
    expected = vyxalify([0, 0, 0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('-')
    # print('-', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[10, 20, 30], 5]]
    expected = vyxalify([5, 15, 25])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('-')
    # print('-', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_DivideSplit():

    stack = [vyxalify(item) for item in [4,2]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('/')
    # print('/', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abcdef",3]]
    expected = vyxalify(["ab","cd","ef"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('/')
    # print('/', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["1,2,3",","]]
    expected = vyxalify(["1","2","3"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('/')
    # print('/', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_LessThan():

    stack = [vyxalify(item) for item in [1, 2]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('<')
    # print('<', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2, 1]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('<')
    # print('<', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["a","b"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('<')
    # print('<', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [-5,2]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('<')
    # print('<', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3],2]]
    expected = vyxalify([1,0,0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('<')
    # print('<', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Equals():

    stack = [vyxalify(item) for item in [1, 1]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('=')
    # print('=', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2, 1]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('=')
    # print('=', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["a","b"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('=')
    # print('=', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["xyz","xyz"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('=')
    # print('=', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3],2]]
    expected = vyxalify([0,1,0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('=')
    # print('=', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1,"1"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('=')
    # print('=', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_GreaterThan():

    stack = [vyxalify(item) for item in [1, 2]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('>')
    # print('>', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2, 1]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('>')
    # print('>', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["a","b"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('>')
    # print('>', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2,-5]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('>')
    # print('>', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3],2]]
    expected = vyxalify([0,0,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('>')
    # print('>', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["5",10]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('>')
    # print('>', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_All():

    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('A')
    # print('A', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[0,1,2]]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('A')
    # print('A', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["",1,2]]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('A')
    # print('A', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[]]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('A')
    # print('A', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [""]]
    expected = vyxalify([])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('A')
    # print('A', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('A')
    # print('A', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["a"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('A')
    # print('A', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["y"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('A')
    # print('A', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hi"]]
    expected = vyxalify([0,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('A')
    # print('A', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_BinaryToDecimal():

    stack = [vyxalify(item) for item in [[1,0,1]]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('B')
    # print('B', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,1,1]]]
    expected = vyxalify(7)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('B')
    # print('B', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["1011"]]
    expected = vyxalify(11)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('B')
    # print('B', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ChrOrd():

    stack = [vyxalify(item) for item in [65]]
    expected = vyxalify("A")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('C')
    # print('C', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [8482]]
    expected = vyxalify("™")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('C')
    # print('C', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["Z"]]
    expected = vyxalify(90)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('C')
    # print('C', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["ABC"]]
    expected = vyxalify([65,66,67])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('C')
    # print('C', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[123,124,125]]]
    expected = vyxalify(["{","|","}"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('C')
    # print('C', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_TwoPowerPythonEval():

    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('E')
    # print('E', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(4)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('E')
    # print('E', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["[1,2,3]"]]
    expected = vyxalify([1,2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('E')
    # print('E', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Filter():

    stack = [vyxalify(item) for item in [[1,2,3],[2,4,6]]]
    expected = vyxalify([1,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('F')
    # print('F', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abcdef","daffodil"]]
    expected = vyxalify("bce")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('F')
    # print('F', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Max():

    stack = [vyxalify(item) for item in [[1,3,2]]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('G')
    # print('G', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["python"]]
    expected = vyxalify("y")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('G')
    # print('G', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_HexToDecimal():

    stack = [vyxalify(item) for item in [32]]
    expected = vyxalify('20')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('H')
    # print('H', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["b"]]
    expected = vyxalify(11)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('H')
    # print('H', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["beedab"]]
    expected = vyxalify(12512683)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('H')
    # print('H', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_IntoTwoPieces():

    stack = [vyxalify(item) for item in [6]]
    expected = vyxalify("      ")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('I')
    # print('I', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [":I"]]
    expected = vyxalify("`:I`:I")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('I')
    # print('I', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, 3, 4]]]
    expected = vyxalify([[1, 2], [3, 4]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('I')
    # print('I', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Merge():

    stack = [vyxalify(item) for item in [[1,2,3],4]]
    expected = vyxalify([1,2,3,4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('J')
    # print('J', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc","def"]]
    expected = vyxalify("abcdef")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('J')
    # print('J', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1,[2,3,4]]]
    expected = vyxalify([1,2,3,4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('J')
    # print('J', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2],[3,4]]]
    expected = vyxalify([1,2,3,4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('J')
    # print('J', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_FactorsSubstringsPrefixes():

    stack = [vyxalify(item) for item in [20]]
    expected = vyxalify([1,2,4,5,10,20])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('K')
    # print('K', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify([1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('K')
    # print('K', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["adbcdbcd"]]
    expected = vyxalify(["d", "db", "dbc", "b", "bc", "bcd", "c", "cd"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('K')
    # print('K', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify([[1],[1,2],[1,2,3]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('K')
    # print('K', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Length():

    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('L')
    # print('L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('L')
    # print('L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,"wrfwerfgbr",6]]]
    expected = vyxalify(4)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('L')
    # print('L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Map():

    stack = [vyxalify(item) for item in [5,[1,2,3]]]
    expected = vyxalify([[5,1],[5,2],[5,3]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('M')
    # print('M', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["z","hi"]]
    expected = vyxalify([["z","h"],["z","i"]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('M')
    # print('M', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_NegateSwapCase():

    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(-5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('N')
    # print('N', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [-1]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('N')
    # print('N', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["a"]]
    expected = vyxalify("A")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('N')
    # print('N', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["aBc"]]
    expected = vyxalify("AbC")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('N')
    # print('N', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Count():

    stack = [vyxalify(item) for item in [[1,2,3,4,5,4,3], 4]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('O')
    # print('O', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abcdbacsabdcabca","a"]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('O')
    # print('O', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Strip():

    stack = [vyxalify(item) for item in [[1, 2, 3, 4, 5, 4, 3, 2, 1], [1, 2]]]
    expected = vyxalify([3, 4, 5, 4, 3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('P')
    # print('P', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["    Hello, World!    ", " "]]
    expected = vyxalify("Hello, World!")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('P')
    # print('P', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Reduce():

    stack = [vyxalify(item) for item in [[[1,2],[3,4]]]]
    expected = vyxalify([[2,1],[4,3]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('R')
    # print('R', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[[1,2]]]]
    expected = vyxalify([[2,1]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('R')
    # print('R', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Stringify():

    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify("5")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('S')
    # print('S', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify("⟨ 1 | 2 | 3 ⟩")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('S')
    # print('S', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["X"]]
    expected = vyxalify("X")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('S')
    # print('S', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_TruthyIndicesTriadify():

    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('T')
    # print('T', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [-4]]
    expected = vyxalify(-12)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('T')
    # print('T', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[0,1,0,2]]]
    expected = vyxalify([1,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('T')
    # print('T', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3,4]]]
    expected = vyxalify([0,1,2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('T')
    # print('T', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Uniquify():

    stack = [vyxalify(item) for item in [[1,3,5,5]]]
    expected = vyxalify([1,3,5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('U')
    # print('U', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abdbcdbch"]]
    expected = vyxalify("abdch")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('U')
    # print('U', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Replace():

    stack = [vyxalify(item) for item in ["hela","a","lo"]]
    expected = vyxalify("hello")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('V')
    # print('V', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["banana","n","nan"]]
    expected = vyxalify("banananana")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('V')
    # print('V', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Wrap():

    stack = [vyxalify(item) for item in [1,2,3]]
    expected = vyxalify([1,2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('W')
    # print('W', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in []]
    expected = vyxalify([])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('W')
    # print('W', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello",1,9]]
    expected = vyxalify(["hello",1,9])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('W')
    # print('W', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Interleave():

    stack = [vyxalify(item) for item in [[1,3,5],[2,4]]]
    expected = vyxalify([1,2,3,4,5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Y')
    # print('Y', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["srn","tig"]]
    expected = vyxalify("string")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Y')
    # print('Y', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Zip():

    stack = [vyxalify(item) for item in [[1,2],[3,4]]]
    expected = vyxalify([[1,3],[2,4]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Z')
    # print('Z', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc",[1,2,3]]]
    expected = vyxalify([["a",1],["b",2],["c",3]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Z')
    # print('Z', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Any():

    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('a')
    # print('a', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[0,0,0]]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('a')
    # print('a', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[0,1,2]]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('a')
    # print('a', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["A"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('a')
    # print('a', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["a"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('a')
    # print('a', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["Hi"]]
    expected = vyxalify([1,0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('a')
    # print('a', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Binary():

    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify([1,0,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('b')
    # print('b', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [" "]]
    expected = vyxalify([[1,0,0,0,0,0]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('b')
    # print('b', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[2,3]]]
    expected = vyxalify([[1,0],[1,1]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('b')
    # print('b', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [-10]]
    expected = vyxalify([-1, 0, -1, 0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('b')
    # print('b', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Contains():

    stack = [vyxalify(item) for item in ["abcdef","a"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('c')
    # print('c', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["xyz","a"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('c')
    # print('c', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3],1]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('c')
    # print('c', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3],0]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('c')
    # print('c', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_DoubleDyadify():

    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(10)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('d')
    # print('d', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('d')
    # print('d', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2]]]
    expected = vyxalify([2,4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('d')
    # print('d', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["x"]]
    expected = vyxalify("xx")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('d')
    # print('d', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["ha"]]
    expected = vyxalify("haha")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('d')
    # print('d', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Exponentiation():

    stack = [vyxalify(item) for item in [5,3]]
    expected = vyxalify(125)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('e')
    # print('e', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0,0]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('e')
    # print('e', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello",7]]
    expected = vyxalify("hellohh")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('e')
    # print('e', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Flatten():

    stack = [vyxalify(item) for item in [135]]
    expected = vyxalify([1,3,5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('f')
    # print('f', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hi"]]
    expected = vyxalify(["h","i"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('f')
    # print('f', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[[[1,2],3,[[4,[5]],6],7],[8,[9]]]]]
    expected = vyxalify([1,2,3,4,5,6,7,8,9])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('f')
    # print('f', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [-1]]
    expected = vyxalify(["-",1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('f')
    # print('f', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Minimum():

    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify("a")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('g')
    # print('g', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,4,-2]]]
    expected = vyxalify(-2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('g')
    # print('g', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[5,3,9]]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('g')
    # print('g', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Head():

    stack = [vyxalify(item) for item in ["hello"]]
    expected = vyxalify("h")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('h')
    # print('h', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('h')
    # print('h', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Index():

    stack = [vyxalify(item) for item in ["abc",1]]
    expected = vyxalify("b")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('i')
    # print('i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3], 0]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('i')
    # print('i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[2,3,4,5], [2]]]
    expected = vyxalify([2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('i')
    # print('i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,3,5,7],[1,3]]]
    expected = vyxalify([3,5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('i')
    # print('i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3,4,5,6,7,8,9,10],[1,8,2]]]
    expected = vyxalify([2,4,6,8])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('i')
    # print('i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Join():

    stack = [vyxalify(item) for item in [[1,2,3],"penguin"]]
    expected = vyxalify("1penguin2penguin3")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('j')
    # print('j', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["he","","o, wor","d!"], "l"]]
    expected = vyxalify("hello, world!")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('j')
    # print('j', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_CumulativeGroups():

    stack = [vyxalify(item) for item in ["hello",3]]
    expected = vyxalify(["hel","ell","llo"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('l')
    # print('l', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["cake",2]]
    expected = vyxalify(["ca","ak","ke"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('l')
    # print('l', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["cheese","cake"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('l')
    # print('l', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["cheese","salads"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('l')
    # print('l', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Mirror():

    stack = [vyxalify(item) for item in [123]]
    expected = vyxalify(444)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('m')
    # print('m', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hi"]]
    expected = vyxalify("hiih")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('m')
    # print('m', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify([1,2,3,3,2,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('m')
    # print('m', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Remove():

    stack = [vyxalify(item) for item in ["hello","l"]]
    expected = vyxalify("heo")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('o')
    # print('o', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3,1,2],1]]
    expected = vyxalify([2,3,2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('o')
    # print('o', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["bananas and naan","an"]]
    expected = vyxalify("bas d na")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('o')
    # print('o', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Prepend():

    stack = [vyxalify(item) for item in ["ld","wor"]]
    expected = vyxalify("world")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('p')
    # print('p', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3],13]]
    expected = vyxalify([13,1,2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('p')
    # print('p', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[3,4,5],"23"]]
    expected = vyxalify(["23",3,4,5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('p')
    # print('p', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Uneval():

    stack = [vyxalify(item) for item in ["\\"]]
    expected = vyxalify("`\\\\`")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('q')
    # print('q', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["`"]]
    expected = vyxalify("`\\``")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('q')
    # print('q', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["a"]]
    expected = vyxalify("`a`")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('q')
    # print('q', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Range():

    stack = [vyxalify(item) for item in [3,6]]
    expected = vyxalify([3,4,5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('r')
    # print('r', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4,8]]
    expected = vyxalify([4,5,6,7])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('r')
    # print('r', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_sort():

    stack = [vyxalify(item) for item in [[3,1,2]]]
    expected = vyxalify([1,2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('s')
    # print('s', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["bca"]]
    expected = vyxalify("abc")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('s')
    # print('s', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Tail():

    stack = [vyxalify(item) for item in ["hello"]]
    expected = vyxalify("o")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('t')
    # print('t', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('t')
    # print('t', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_MinusOne():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(-1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('u')
    # print('u', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Listify():

    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify([1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('w')
    # print('w', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello"]]
    expected = vyxalify(["hello"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('w')
    # print('w', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify([[1,2,3]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('w')
    # print('w', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Uninterleave():

    stack = [vyxalify(item) for item in ["abcde"]]
    expected = vyxalify("bd")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('y')
    # print('y', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3,4]]]
    expected = vyxalify([2,4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('y')
    # print('y', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Zip_self():

    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify([[1,1],[2,2],[3,3]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('z')
    # print('z', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["zap"]]
    expected = vyxalify([["z","z"], ["a","a"],["p","p"]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('z')
    # print('z', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_MaxbyTail():

    stack = [vyxalify(item) for item in [[[3,4],[9,2]]]]
    expected = vyxalify([3,4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↑')
    # print('↑', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[[1,2,3],[2,5]]]]
    expected = vyxalify([2,5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↑')
    # print('↑', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_MinbyTail():

    stack = [vyxalify(item) for item in [[[3,4],[9,2]]]]
    expected = vyxalify([9,2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↓')
    # print('↓', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[[1,2,3],[2,5]]]]
    expected = vyxalify([1,2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↓')
    # print('↓', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_DyadicMaximum():

    stack = [vyxalify(item) for item in [5,3]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∴')
    # print('∴', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello","goodbye"]]
    expected = vyxalify("hello")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∴')
    # print('∴', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3,"(stuff)"]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∴')
    # print('∴', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_DyadicMinimum():

    stack = [vyxalify(item) for item in [5,3]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∵')
    # print('∵', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello","goodbye"]]
    expected = vyxalify("goodbye")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∵')
    # print('∵', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3,"(stuff)"]]
    expected = vyxalify("(stuff)")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∵')
    # print('∵', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_IncrementSpaceReplaceWith0():

    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(6)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('›')
    # print('›', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[3,4]]]
    expected = vyxalify([4,5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('›')
    # print('›', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["  101"]]
    expected = vyxalify("00101")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('›')
    # print('›', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Decrement():

    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(4)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('‹')
    # print('‹', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[3,4]]]
    expected = vyxalify([2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('‹')
    # print('‹', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello"]]
    expected = vyxalify("hello-")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('‹')
    # print('‹', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Parity():

    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∷')
    # print('∷', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∷')
    # print('∷', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello!"]]
    expected = vyxalify("lo!")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∷')
    # print('∷', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_EmptyString():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¤')
    # print('¤', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Space():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(" ")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ð')
    # print('ð', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ToBaseTenFromCustomBase():

    stack = [vyxalify(item) for item in [43,5]]
    expected = vyxalify(23)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('β')
    # print('β', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["banana","nab"]]
    expected = vyxalify(577)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('β')
    # print('β', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[15,23,9],31]]
    expected = vyxalify(15137)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('β')
    # print('β', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_FromBaseTenToCustomBase():

    stack = [vyxalify(item) for item in [1234567,"abc"]]
    expected = vyxalify("cacccabbbbcab")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('τ')
    # print('τ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1234567,5]]
    expected = vyxalify([3,0,4,0,0,1,2,3,2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('τ')
    # print('τ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [928343,["he","ll","o"]]]
    expected = vyxalify(["ll","o","he","o","he","ll","ll","ll","ll","he","he","he","o"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('τ')
    # print('τ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[10, 12, 6, 2, 8, 145], 2]]
    expected = vyxalify([[1, 0, 1, 0], [1, 1, 0, 0], [1, 1, 0], [1, 0], [1, 0, 0, 0], [1, 0, 0, 1, 0, 0, 0, 1]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('τ')
    # print('τ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Absolutevalue():

    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ȧ')
    # print('ȧ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [-1]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ȧ')
    # print('ȧ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [" ch ee s e "]]
    expected = vyxalify("cheese")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ȧ')
    # print('ȧ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[-1,2,-5]]]
    expected = vyxalify([1,2,5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ȧ')
    # print('ȧ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Boolify():

    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḃ')
    # print('ḃ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḃ')
    # print('ḃ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[69, 0]]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḃ')
    # print('ḃ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["x"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḃ')
    # print('ḃ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["", []]]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḃ')
    # print('ḃ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[]]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḃ')
    # print('ḃ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_NotOne():

    stack = [vyxalify(item) for item in [[1, 0]]]
    expected = vyxalify([0, 1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ċ')
    # print('ċ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["1"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ċ')
    # print('ċ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ċ')
    # print('ċ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ċ')
    # print('ċ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Divmod():

    stack = [vyxalify(item) for item in [5,3]]
    expected = vyxalify([1,2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḋ')
    # print('ḋ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abcd",3]]
    expected = vyxalify(["abc","abd","acd","bcd"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḋ')
    # print('ḋ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3],2]]
    expected = vyxalify([[1,2],[1,3],[2,3]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḋ')
    # print('ḋ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abcdef", "Joe"]]
    expected = vyxalify("Joedef")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḋ')
    # print('ḋ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Enumerate():

    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify([[0,"a"],[1,"b"],[2,"c"]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ė')
    # print('ė', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify([[0,1],[1,2],[2,3]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ė')
    # print('ė', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Find():

    stack = [vyxalify(item) for item in [[1,2,3],2]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḟ')
    # print('ḟ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello","l"]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḟ')
    # print('ḟ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Gcd():

    stack = [vyxalify(item) for item in [[1,3,2]]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ġ')
    # print('ġ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[60,42,108]]]
    expected = vyxalify(6)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ġ')
    # print('ġ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [50,35]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ġ')
    # print('ġ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["laugh","cough"]]
    expected = vyxalify("ugh")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ġ')
    # print('ġ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_HeadExtract():

    stack = [vyxalify(item) for item in ["hello"]]
    expected = vyxalify("ello")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḣ')
    # print('ḣ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify([2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḣ')
    # print('ḣ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_FloorDivision():

    stack = [vyxalify(item) for item in [5,3]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḭ')
    # print('ḭ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello!",3]]
    expected = vyxalify("he")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḭ')
    # print('ḭ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3,"hello!"]]
    expected = vyxalify("he")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ḭ')
    # print('ḭ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_LeftJustifyGridifyInfiniteReplaceCollectuntilfale():

    stack = [vyxalify(item) for item in [1, 3, 2]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ŀ')
    # print('ŀ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Mean():

    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṁ')
    # print('ṁ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[4,71,-63]]]
    expected = vyxalify(4)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṁ')
    # print('ṁ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_JoinByNothing():

    stack = [vyxalify(item) for item in [["a","b","c"]]]
    expected = vyxalify("abc")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṅ')
    # print('ṅ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify('123')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṅ')
    # print('ṅ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṅ')
    # print('ṅ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0.54]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṅ')
    # print('ṅ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Slice():

    stack = [vyxalify(item) for item in ["hello",2]]
    expected = vyxalify("llo")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ȯ')
    # print('ȯ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3],1]]
    expected = vyxalify([2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ȯ')
    # print('ȯ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3],-2]]
    expected = vyxalify([2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ȯ')
    # print('ȯ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Powerset():

    stack = [vyxalify(item) for item in ["ab"]]
    expected = vyxalify([[],["a"],["b"],["a","b"]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṗ')
    # print('ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify([[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṗ')
    # print('ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Round():

    stack = [vyxalify(item) for item in [5.5]]
    expected = vyxalify(6)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṙ')
    # print('ṙ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3.2]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṙ')
    # print('ṙ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[5.5,3.2]]]
    expected = vyxalify([6,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṙ')
    # print('ṙ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [-4.7]]
    expected = vyxalify(-5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṙ')
    # print('ṙ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [-4.5]]
    expected = vyxalify(-4)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṙ')
    # print('ṙ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_SortbyFunction():

    stack = [vyxalify(item) for item in [3,4]]
    expected = vyxalify([3,4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṡ')
    # print('ṡ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1,5]]
    expected = vyxalify([1,2,3,4,5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṡ')
    # print('ṡ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc1def2ghi","\\d+"]]
    expected = vyxalify(["abc","def","ghi"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṡ')
    # print('ṡ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_TailExtract():

    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify("c")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṫ')
    # print('ṫ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ṫ')
    # print('ṫ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ChunkWrap():

    stack = [vyxalify(item) for item in ["abcdef",2]]
    expected = vyxalify(["ab","cd","ef"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ẇ')
    # print('ẇ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3,4,5,6],3]]
    expected = vyxalify([[1,2,3],[4,5,6]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ẇ')
    # print('ẇ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abcdefghi",[2,3,4]]]
    expected = vyxalify(["ab","cde","fghi"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ẇ')
    # print('ẇ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3,4,5], [2,3] ]]
    expected = vyxalify([[1,2],[3,4,5]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ẇ')
    # print('ẇ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Repeat():

    stack = [vyxalify(item) for item in [[1,2,3],3]]
    expected = vyxalify([[1,2,3],[1,2,3],[1,2,3]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ẋ')
    # print('ẋ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["x",5]]
    expected = vyxalify("xxxxx")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ẋ')
    # print('ẋ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0, 4]]
    expected = vyxalify([0, 0, 0, 0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ẋ')
    # print('ẋ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ExclusiveRangeLength():

    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify([0,1,2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ẏ')
    # print('ẏ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2]]]
    expected = vyxalify([0,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ẏ')
    # print('ẏ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_InclusiveRangeLength():

    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify([1,2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ż')
    # print('ż', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2]]]
    expected = vyxalify([1,2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ż')
    # print('ż', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_SquareRoot():

    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('√')
    # print('√', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello"]]
    expected = vyxalify("hlo")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('√')
    # print('√', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Ten():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(10)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₀')
    # print('₀', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Hundred():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(100)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₁')
    # print('₁', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_IsEven():

    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₂')
    # print('₂', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₂')
    # print('₂', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₂')
    # print('₂', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2]]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₂')
    # print('₂', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_DivisibleBythree():

    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₃')
    # print('₃', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [6]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₃')
    # print('₃', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hi"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₃')
    # print('₃', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1]]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₃')
    # print('₃', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_TwentySix():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(26)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₄')
    # print('₄', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_DivisibleByFive():

    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₅')
    # print('₅', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₅')
    # print('₅', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello"]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₅')
    # print('₅', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₅')
    # print('₅', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_SixtyFour():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(64)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₆')
    # print('₆', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_OneTwentyEight():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(128)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₇')
    # print('₇', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_TwoFiftySix():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(256)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('₈')
    # print('₈', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Newline():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("\n")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¶')
    # print('¶', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_JoinOnNewlines():

    stack = [vyxalify(item) for item in [[1, 2, 3, 4, 5, 6]]]
    expected = vyxalify("1\n2\n3\n4\n5\n6")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⁋')
    # print('⁋', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["Hello", "World!"]]]
    expected = vyxalify("Hello\nWorld!")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⁋')
    # print('⁋', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_VerticalJoin():

    stack = [vyxalify(item) for item in [["abc", "def", "ghi"]]]
    expected = vyxalify("adg\nbeh\ncfi")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('§')
    # print('§', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["***", "****", "*****"]]]
    expected = vyxalify("  *\n **\n***\n***\n***")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('§')
    # print('§', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 22, 333]]]
    expected = vyxalify("  3\n 23\n123")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('§')
    # print('§', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_AbsoluteDifferencePaddedVerticalJoin():

    stack = [vyxalify(item) for item in [5, 1]]
    expected = vyxalify(4)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ε')
    # print('ε', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1, 5]]
    expected = vyxalify(4)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ε')
    # print('ε', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3, 3]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ε')
    # print('ε', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["***", "****", "*****"], "."]]
    expected = vyxalify("..*\n.**\n***\n***\n***")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ε')
    # print('ε', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["abc", "def", "ghi"], "."]]
    expected = vyxalify("adg\nbeh\ncfi")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ε')
    # print('ε', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Factorial():

    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(120)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¡')
    # print('¡', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello my name jeff. ur sussy baka"]]
    expected = vyxalify("Hello my name jeff. Ur sussy baka")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¡')
    # print('¡', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, 3, 4, 5]]]
    expected = vyxalify([1, 2, 6, 24, 120])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¡')
    # print('¡', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Summate():

    stack = [vyxalify(item) for item in [[1, 2, 3, 4, 5]]]
    expected = vyxalify(15)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∑')
    # print('∑', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["abc", "def", 10]]]
    expected = vyxalify("abcdef10")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∑')
    # print('∑', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [12345]]
    expected = vyxalify(15)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∑')
    # print('∑', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_CumulativeSum():

    stack = [vyxalify(item) for item in [12345]]
    expected = vyxalify([1, 3, 6, 10, 15])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¦')
    # print('¦', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abcdef"]]
    expected = vyxalify(["a", "ab", "abc", "abcd", "abcde", "abcdef"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¦')
    # print('¦', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, 3, 4, 5]]]
    expected = vyxalify([1, 3, 6, 10, 15])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¦')
    # print('¦', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_AllEqual():

    stack = [vyxalify(item) for item in [1111]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('≈')
    # print('≈', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["acc"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('≈')
    # print('≈', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, 2, 1]]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('≈')
    # print('≈', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[]]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('≈')
    # print('≈', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Assign():

    stack = [vyxalify(item) for item in [[1, 2, 3, 4], 1, 0]]
    expected = vyxalify([1, 0, 3, 4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ȧ')
    # print('Ȧ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["Hello ", 5, ", World!"]]
    expected = vyxalify("Hello, World!")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ȧ')
    # print('Ȧ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [69320, 2, 4]]
    expected = vyxalify([6, 9, 4, 2, 0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ȧ')
    # print('Ȧ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Bifurcate():

    stack = [vyxalify(item) for item in [203]]
    expected = vyxalify(302)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḃ')
    # print('Ḃ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify("cba")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḃ')
    # print('Ḃ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, 3, 4]]]
    expected = vyxalify([4, 3, 2, 1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḃ')
    # print('Ḃ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Counts():

    stack = [vyxalify(item) for item in [[1, 2, 2, 3, 3, 3, 3]]]
    expected = vyxalify([[1, 1], [2, 2], [3, 4]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ċ')
    # print('Ċ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["Hello, World!"]]
    expected = vyxalify([["H", 1], ["e", 1], ["l", 3], ["o", 2], [",", 1], [" ", 1], ["W", 1], ["r", 1], ["d", 1], ["!", 1]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ċ')
    # print('Ċ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_IsDivisibleArbitraryDuplicate():

    stack = [vyxalify(item) for item in [15, 5]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḋ')
    # print('Ḋ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc", 3]]
    expected = vyxalify("abc")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḋ')
    # print('Ḋ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[5, 13, 29, 48, 12], 2]]
    expected = vyxalify([0, 0, 0, 1, 1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḋ')
    # print('Ḋ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_VyxalExecReciprocal():

    stack = [vyxalify(item) for item in [[2, 3, -1]]]
    expected = vyxalify([0.5, 1/3, -1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ė')
    # print('Ė', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["kH"]]
    expected = vyxalify("Hello, World!")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ė')
    # print('Ė', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_GeneratorModuloIndexFormat():

    stack = [vyxalify(item) for item in [4.51, 3]]
    expected = vyxalify("4.51")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḟ')
    # print('Ḟ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1.69, 10]]
    expected = vyxalify("1.690000000")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḟ')
    # print('Ḟ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["Hello, World!", 3]]
    expected = vyxalify("Hl r!")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḟ')
    # print('Ḟ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["LQYWXUAOL", 2]]
    expected = vyxalify("LYXAL")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḟ')
    # print('Ḟ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, 3, 4, 5, 6, 7, 8, 9], 4]]
    expected = vyxalify([1, 5, 9])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḟ')
    # print('Ḟ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["Hello", "World!", "Gaming", "Pogchamp", "A"], 2]]
    expected = vyxalify(["Hello", "Gaming", "A"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḟ')
    # print('Ḟ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["    1111", "0"]]
    expected = vyxalify("00001111")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḟ')
    # print('Ḟ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["But who was phone?", "!"]]
    expected = vyxalify("But!who!was!phone?")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḟ')
    # print('Ḟ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Groupconsecutive():

    stack = [vyxalify(item) for item in [[1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 5, 5]]]
    expected = vyxalify([[1, 1, 1], [2, 2, 2, 2, 2, 2], [3, 3, 3, 3, 3], [4, 4], [5, 5]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ġ')
    # print('Ġ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["Hello, World!"]]
    expected = vyxalify([["H"], ["e"], ["l", "l"], ["o"], [","], [" "], ["W"], ["o"], ["r"], ["l"], ["d"], ["!"]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ġ')
    # print('Ġ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_HeadRemoveBehead():

    stack = [vyxalify(item) for item in [[0, [43, 69], "foo"]]]
    expected = vyxalify([[43, 69], "foo"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḣ')
    # print('Ḣ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[]]]
    expected = vyxalify([])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḣ')
    # print('Ḣ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["foo"]]
    expected = vyxalify("oo")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḣ')
    # print('Ḣ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [""]]
    expected = vyxalify("")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḣ')
    # print('Ḣ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1234.56]]
    expected = vyxalify(234.56)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḣ')
    # print('Ḣ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0.2]]
    expected = vyxalify(0.2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ḣ')
    # print('Ḣ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Indexintoorcollectwhileunique():

    stack = [vyxalify(item) for item in [["foo", "bar", -69, 420, "baz"], [0, 2, 4]]]
    expected = vyxalify(["foo", -69, "baz"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('İ')
    # print('İ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Transliterate():

    stack = [vyxalify(item) for item in ["abcdefcba","abc","123"]]
    expected = vyxalify("123def321")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ŀ')
    # print('Ŀ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,0], [2], [5]]]
    expected = vyxalify([1,5,0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ŀ')
    # print('Ŀ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc","ab",["bb","cc"]]]
    expected = vyxalify(["bb","cc","c"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ŀ')
    # print('Ŀ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Insert():

    stack = [vyxalify(item) for item in [[1,3,4],1,2]]
    expected = vyxalify([1,2,3,4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṁ')
    # print('Ṁ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["wyz",1,"x"]]
    expected = vyxalify("wxyz")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṁ')
    # print('Ṁ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["jknop",2,"lm"]]
    expected = vyxalify("jklmnop")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṁ')
    # print('Ṁ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Integerpartitions():

    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify([[1,1,1,1,1],[2,1,1,1],[3,1,1],[2,2,1],[4,1],[3,2],[5]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṅ')
    # print('Ṅ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello"]]
    expected = vyxalify("h e l l o")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṅ')
    # print('Ṅ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify("1 2 3")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṅ')
    # print('Ṅ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Over():

    stack = [vyxalify(item) for item in [4,5]]
    expected = vyxalify(4)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ȯ')
    # print('Ȯ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hi","bye"]]
    expected = vyxalify("hi")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ȯ')
    # print('Ȯ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Permutations():

    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify(["abc","acb","bac","bca","cab","cba"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṗ')
    # print('Ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2]]]
    expected = vyxalify([[1,2],[2,1]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṗ')
    # print('Ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Reverse():

    stack = [vyxalify(item) for item in [203]]
    expected = vyxalify(302)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṙ')
    # print('Ṙ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify("cba")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṙ')
    # print('Ṙ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, 3, 4]]]
    expected = vyxalify([4, 3, 2, 1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṙ')
    # print('Ṙ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Vectorisedsums():

    stack = [vyxalify(item) for item in [[[1,2,3],[4,5,6]]]]
    expected = vyxalify([6, 15])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṡ')
    # print('Ṡ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[3,4,5]]]
    expected = vyxalify([3, 4, 5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṡ')
    # print('Ṡ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[[1,2,3], [1, 2, 3, 4]]]]
    expected = vyxalify([6, 10])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṡ')
    # print('Ṡ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_TailRemove():

    stack = [vyxalify(item) for item in ["1234"]]
    expected = vyxalify("123")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṫ')
    # print('Ṫ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify([1,2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ṫ')
    # print('Ṫ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_SplitAndKeepDelimiter():

    stack = [vyxalify(item) for item in ["a b c"," "]]
    expected = vyxalify(["a"," ","b"," ","c"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ẇ')
    # print('Ẇ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["xyzabc123abc","b"]]
    expected = vyxalify(["xyza","b","c123a","b","c"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ẇ')
    # print('Ẇ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_CartesianProductFixpoint():

    stack = [vyxalify(item) for item in ["ab","cd"]]
    expected = vyxalify(["ac","ad","bc","bd"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ẋ')
    # print('Ẋ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2],[3,4]]]
    expected = vyxalify([[1,3],[1,4],[2,3],[2,4]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ẋ')
    # print('Ẋ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_SliceUntil():

    stack = [vyxalify(item) for item in ["abc",1]]
    expected = vyxalify("a")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ẏ')
    # print('Ẏ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3],2]]
    expected = vyxalify([1,2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ẏ')
    # print('Ẏ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_SliceFromOneUntil():

    stack = [vyxalify(item) for item in ["abc",2]]
    expected = vyxalify("b")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ż')
    # print('Ż', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3],3]]
    expected = vyxalify([2,3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ż')
    # print('Ż', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Square():

    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(25)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('²')
    # print('²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello"]]
    expected = vyxalify(["hel","lo ", "   "])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('²')
    # print('²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["bye"]]
    expected = vyxalify(["by","e "])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('²')
    # print('²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify([1,4,9])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('²')
    # print('²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Shift():

    stack = [vyxalify(item) for item in [1,4,5]]
    expected = vyxalify(4)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∇')
    # print('∇', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["my","hi","bye"]]
    expected = vyxalify("hi")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∇')
    # print('∇', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Ceiling():

    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⌈')
    # print('⌈', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4.5]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⌈')
    # print('⌈', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1.52,2.9,3.3]]]
    expected = vyxalify([2,3,4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⌈')
    # print('⌈', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello world"]]
    expected = vyxalify(["hello","world"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⌈')
    # print('⌈', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Floor():

    stack = [vyxalify(item) for item in [5.3]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⌊')
    # print('⌊', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[5.3,4.7]]]
    expected = vyxalify([5, 4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⌊')
    # print('⌊', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["123abc"]]
    expected = vyxalify(123)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⌊')
    # print('⌊', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Deltas():

    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify([1,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¯')
    # print('¯', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,1,1]]]
    expected = vyxalify([0,0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¯')
    # print('¯', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[40,61,3]]]
    expected = vyxalify([21,-58])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¯')
    # print('¯', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Sign():

    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('±')
    # print('±', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hi"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('±')
    # print('±', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [-5]]
    expected = vyxalify(-1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('±')
    # print('±', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('±')
    # print('±', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_RightBitShift():

    stack = [vyxalify(item) for item in [4,1]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↳')
    # print('↳', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [8,"green"]]
    expected = vyxalify("   green")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↳')
    # print('↳', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello","cheeseburger"]]
    expected = vyxalify("       hello")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↳')
    # print('↳', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_LeftBitShift():

    stack = [vyxalify(item) for item in [4,1]]
    expected = vyxalify(8)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↲')
    # print('↲', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [8,"green"]]
    expected = vyxalify("green   ")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↲')
    # print('↲', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello","cheeseburger"]]
    expected = vyxalify("hello       ")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↲')
    # print('↲', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_BitwiseAnd():

    stack = [vyxalify(item) for item in [420, 69]]
    expected = vyxalify(4)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⋏')
    # print('⋏', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc", 10]]
    expected = vyxalify("   abc    ")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⋏')
    # print('⋏', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["no", "gamers"]]
    expected = vyxalify(" no ")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⋏')
    # print('⋏', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_BitwiseOr():

    stack = [vyxalify(item) for item in [420, 69]]
    expected = vyxalify(485)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⋎')
    # print('⋎', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2, "abc"]]
    expected = vyxalify("ab")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⋎')
    # print('⋎', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc", 2]]
    expected = vyxalify("ab")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⋎')
    # print('⋎', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["Hello", "lower"]]
    expected = vyxalify("Hellower")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⋎')
    # print('⋎', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_BitwiseXor():

    stack = [vyxalify(item) for item in [420, 69]]
    expected = vyxalify(481)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('꘍')
    # print('꘍', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5, "ab"]]
    expected = vyxalify("     ab")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('꘍')
    # print('꘍', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["ab", 5]]
    expected = vyxalify("ab     ")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('꘍')
    # print('꘍', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["atoll", "bowl"]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('꘍')
    # print('꘍', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_BitwiseNot():

    stack = [vyxalify(item) for item in [220]]
    expected = vyxalify(-221)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ꜝ')
    # print('ꜝ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["Hello"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ꜝ')
    # print('ꜝ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_LesserThanorEqualTo():

    stack = [vyxalify(item) for item in [1,2]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('≤')
    # print('≤', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_GreaterThanorEqualTo():

    stack = [vyxalify(item) for item in [1,2]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('≥')
    # print('≥', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_NotEqualTo():

    stack = [vyxalify(item) for item in [1,2]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('≠')
    # print('≠', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ExactlyEqualTo():

    stack = [vyxalify(item) for item in [1,2]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⁼')
    # print('⁼', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_SetUnion():

    stack = [vyxalify(item) for item in [[1,2],[2,3,4]]]
    expected = vyxalify([1,2,3,4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∪')
    # print('∪', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Tranpose():

    stack = [vyxalify(item) for item in [[[1,2],[2,3,4]]]]
    expected = vyxalify([[1, 2], [2, 3], [4]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∩')
    # print('∩', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_SymmetricSetdifference():

    stack = [vyxalify(item) for item in [[1,2],[2,3,4]]]
    expected = vyxalify([1,3,4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⊍')
    # print('⊍', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_GradeUp():

    stack = [vyxalify(item) for item in [[420,69,1337]]]
    expected = vyxalify([1,0,2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⇧')
    # print('⇧', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["Heloo"]]
    expected = vyxalify("HELOO")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⇧')
    # print('⇧', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify(6)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⇧')
    # print('⇧', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_GradeDown():

    stack = [vyxalify(item) for item in [[420,69,1337]]]
    expected = vyxalify([2,0,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⇩')
    # print('⇩', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["Heloo"]]
    expected = vyxalify("heloo")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⇩')
    # print('⇩', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('⇩')
    # print('⇩', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Removenon_alphabets():

    stack = [vyxalify(item) for item in ["Helo1233adc__"]]
    expected = vyxalify("Heloadc")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ǎ')
    # print('Ǎ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [8]]
    expected = vyxalify(256)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ǎ')
    # print('Ǎ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Nthprime():

    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify(7)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ǎ')
    # print('ǎ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify(["a","ab","abc","b","bc","c"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ǎ')
    # print('ǎ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Primefactorization():

    stack = [vyxalify(item) for item in [45]]
    expected = vyxalify([3,5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ǐ')
    # print('Ǐ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify("abca")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ǐ')
    # print('Ǐ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Primefactors():

    stack = [vyxalify(item) for item in [45]]
    expected = vyxalify([3, 3, 5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ǐ')
    # print('ǐ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc def"]]
    expected = vyxalify("Abc Def")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ǐ')
    # print('ǐ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Multiplicity():

    stack = [vyxalify(item) for item in [45, 3]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ǒ')
    # print('Ǒ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["aaabbbc", "ab"]]
    expected = vyxalify("c")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ǒ')
    # print('Ǒ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Modulo3():

    stack = [vyxalify(item) for item in [45]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ǒ')
    # print('ǒ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abcdefghi"]]
    expected = vyxalify(["ab", "cd", "ef", "gh", "i"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ǒ')
    # print('ǒ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_RotateLeft():

    stack = [vyxalify(item) for item in [3, [4, 5, 5, 6]]]
    expected = vyxalify([5, 5, 6, 4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ǔ')
    # print('Ǔ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3, [1, 2, 3, 4]]]
    expected = vyxalify([2, 3, 4, 1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Ǔ')
    # print('Ǔ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_RotateRight():

    stack = [vyxalify(item) for item in [3, [4, 5, 5, 6]]]
    expected = vyxalify([6, 4, 5, 5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ǔ')
    # print('ǔ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3, [1, 2, 3, 4]]]
    expected = vyxalify([4, 1, 2, 3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ǔ')
    # print('ǔ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_SplitOnnewlines():

    stack = [vyxalify(item) for item in ["a\nb\nc"]]
    expected = vyxalify(["a", "b", "c"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↵')
    # print('↵', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify(1000)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('↵')
    # print('↵', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ProductofArray():

    stack = [vyxalify(item) for item in [[3,4,5]]]
    expected = vyxalify(60)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Π')
    # print('Π', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Uppercasealphabet():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kA')
    # print('kA', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_eEulersnumber():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(2.7182818284590452354)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ke')
    # print('ke', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Fizz():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("Fizz")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kf')
    # print('kf', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Buzz():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("Buzz")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kb')
    # print('kb', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_FizzBuzz():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("FizzBuzz")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kF')
    # print('kF', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_HelloWorld():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("Hello, World!")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kH')
    # print('kH', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_HelloWorldNoPunctuation():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("Hello World")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kh')
    # print('kh', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_1000():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(1000)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k1')
    # print('k1', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_10000():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(10000)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k2')
    # print('k2', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_100000():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(100000)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k3')
    # print('k3', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_1000000():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(1000000)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k4')
    # print('k4', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Lowercasealphabet():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("abcdefghijklmnopqrstuvwxyz")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ka')
    # print('ka', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Lowercaseanduppercasealphabet():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kL')
    # print('kL', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Digits():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("0123456789")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kd')
    # print('kd', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Hexdigitslowercase():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("0123456789abcdef")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k6')
    # print('k6', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Hexdigitsuppercase():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("0123456789ABCDEF")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k^')
    # print('k^', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Octaldigits():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("01234567")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ko')
    # print('ko', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Punctuation():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(string.punctuation)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kp')
    # print('kp', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_PrintableASCII():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify('0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kP')
    # print('kP', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Digitslowercasealphabetanduppercasealphabet():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kr')
    # print('kr', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Uppercaseandlowercasealphabet():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kB')
    # print('kB', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Uppercasealphabetreversed():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("ZYXWVUTSRQPONMLKJIHGFEDCBA")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kZ')
    # print('kZ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Lowercasealphabetreversed():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("zyxwvutsrqponmlkjihgfedcba")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kz')
    # print('kz', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Uppercaseandlowercasealphabetreversed():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kl')
    # print('kl', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Pi():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(3.141592653589793)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ki')
    # print('ki', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Goldenratiophi():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(1.618033988749895)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kg')
    # print('kg', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Bracessquarebracketsanglebracketsandparentheses():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("{}[]<>()")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kβ')
    # print('kβ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Parenthesessquarebracketsandbraces():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("()[]{}")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kḂ')
    # print('kḂ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Parenthesesandsquarebrackets():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("()[]")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kß')
    # print('kß', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Openingbrackets():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("([{")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kḃ')
    # print('kḃ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Closingbrackets():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(")]}")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k≥')
    # print('k≥', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Openingbracketswith():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("([{<")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k≤')
    # print('k≤', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Closingbracketswith():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(")]}>")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kΠ')
    # print('kΠ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Lowercasevowels():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("aeiou")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kv')
    # print('kv', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Upercasevowels():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("AEIOU")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kV')
    # print('kV', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Lowercaseanduppercasevowels():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("aeiouAEIOU")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k∨')
    # print('k∨', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_12():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify([1, 2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k½')
    # print('k½', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_4294967296():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(4294967296)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kḭ')
    # print('kḭ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_1_1():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify([1, -1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k+')
    # print('k+', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test__11():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify([-1, 1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k-')
    # print('k-', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_01():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify([0, 1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k≈')
    # print('k≈', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Slashes():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("/\\")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k/')
    # print('k/', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_360():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(360)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kR')
    # print('kR', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_https():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("https://")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kW')
    # print('kW', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_http():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("http://")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k℅')
    # print('k℅', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_httpswww():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("https://www.")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k↳')
    # print('k↳', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_httpwww():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("http://www.")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k²')
    # print('k²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_512():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(512)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k¶')
    # print('k¶', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_1024():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(1024)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k⁋')
    # print('k⁋', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_2048():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(2048)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k¦')
    # print('k¦', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_4096():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(4096)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kṄ')
    # print('kṄ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_8192():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(8192)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kṅ')
    # print('kṅ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_16384():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(16384)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k¡')
    # print('k¡', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_32768():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(32768)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kε')
    # print('kε', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_65536():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(65536)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k₴')
    # print('k₴', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_2147483648():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(2147483648)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k×')
    # print('k×', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Lowercaseconsonantswithy():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("bcdfghjklmnpqrstvwxyz")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k⁰')
    # print('k⁰', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_BFcommandset():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("[]<>-+.,")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kT')
    # print('kT', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Bracketpairlist():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(["()","[]","{}","<>"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kṗ')
    # print('kṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Nestedbrackets():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("([{<>}])")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kṖ')
    # print('kṖ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Amogus():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("ඞ")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kS')
    # print('kS', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_11():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify([1, 1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k₁')
    # print('k₁', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_220():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(1048576)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k₂')
    # print('k₂', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_230():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify(1073741824)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k₃')
    # print('k₃', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_LowercaseVowelsWithY():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("aeiouy")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k∪')
    # print('k∪', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_UppercaseVowelsWithY():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("AEIOUY")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k⊍')
    # print('k⊍', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_VowelsWithY():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("aeiouyAEIOUY")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k∩')
    # print('k∩', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Directions():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify([[0,1],[1,0],[0,-1],[-1,0]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('k□')
    # print('k□', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_RomanNumerals():

    stack = [vyxalify(item) for item in []]
    expected = vyxalify("IVXLCDM")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('kṘ')
    # print('kṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Cosine():

    stack = [vyxalify(item) for item in [3.14159265358979]]
    expected = vyxalify(-1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆c')
    # print('∆c', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆c')
    # print('∆c', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [6.283185307]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆c')
    # print('∆c', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ArcCosine():

    stack = [vyxalify(item) for item in [-1]]
    expected = vyxalify(3.14159265358979)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆C')
    # print('∆C', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆C')
    # print('∆C', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_QuadraticSolver():

    stack = [vyxalify(item) for item in [1, 2]]
    expected = vyxalify([-2, 0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆q')
    # print('∆q', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1, -2]]
    expected = vyxalify([0, 2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆q')
    # print('∆q', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [69, 420]]
    expected = vyxalify([-140/23, 0.0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆q')
    # print('∆q', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_GeneralQuadraticSolver():

    stack = [vyxalify(item) for item in [1, -2]]
    expected = vyxalify([-2, 1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Q')
    # print('∆Q', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [29, -30]]
    expected = vyxalify([-30, 1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Q')
    # print('∆Q', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [69, 420]]
    expected = vyxalify([-62.2533781727558, -6.74662182724416])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Q')
    # print('∆Q', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Sine():

    stack = [vyxalify(item) for item in [3.14159265358979]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆s')
    # print('∆s', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆s')
    # print('∆s', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [6.28318530717959]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆s')
    # print('∆s', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ArcSine():

    stack = [vyxalify(item) for item in [-1]]
    expected = vyxalify(-1.5707963267948966)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆S')
    # print('∆S', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(1.5707963267948966)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆S')
    # print('∆S', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Tangent():

    stack = [vyxalify(item) for item in [3.1415926535897932385]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆t')
    # print('∆t', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆t')
    # print('∆t', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [6.2831853071795864769]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆t')
    # print('∆t', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ArcTangent():

    stack = [vyxalify(item) for item in [-1]]
    expected = vyxalify(-0.78539816339744830962)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆T')
    # print('∆T', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(0.78539816339744830962)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆T')
    # print('∆T', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_PolynomialSolver():

    stack = [vyxalify(item) for item in [[4, -1005, 3, 4]]]
    expected = vyxalify([(0.06460672339563445+4.263256414560601e-14j), (-0.061605771543874255-1.4210854715202004e-14j), (251.24699904814824-6.938893903907228e-18j)])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆P')
    # print('∆P', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[69, 420, -1]]]
    expected = vyxalify([0.00238002178391728, -6.08933654352305])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆P')
    # print('∆P', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_nPickrnpr():

    stack = [vyxalify(item) for item in [[3, 4, 5, 6], [1, 2, 3, 4]]]
    expected = vyxalify([3,12,60,360])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ƈ')
    # print('∆ƈ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_CopySign():

    stack = [vyxalify(item) for item in [-1, 4]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆±')
    # print('∆±', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1, -69]]
    expected = vyxalify(-1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆±')
    # print('∆±', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [-1, -420]]
    expected = vyxalify(-1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆±')
    # print('∆±', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1, 203]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆±')
    # print('∆±', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_SumofProperDivisorsStationaryPoints():

    stack = [vyxalify(item) for item in [43]]
    expected = vyxalify([1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆K')
    # print('∆K', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [12]]
    expected = vyxalify([16])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆K')
    # print('∆K', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [97]]
    expected = vyxalify([1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆K')
    # print('∆K', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [34]]
    expected = vyxalify([20])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆K')
    # print('∆K', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [18]]
    expected = vyxalify([21])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆K')
    # print('∆K', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ['(x**2 + x + 1) / x']]
    expected = vyxalify([-1, 1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆K')
    # print('∆K', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_PerfectSquare():

    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆²')
    # print('∆²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆²')
    # print('∆²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [9]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆²')
    # print('∆²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [16]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆²')
    # print('∆²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [25]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆²')
    # print('∆²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [36]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆²')
    # print('∆²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [37]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆²')
    # print('∆²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [-1]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆²')
    # print('∆²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆²')
    # print('∆²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1.5]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆²')
    # print('∆²', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_EulersNumbereraisedtopowera():

    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆e')
    # print('∆e', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(2.718281828459045)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆e')
    # print('∆e', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(7.38905609893065)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆e')
    # print('∆e', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify(20.085536923187668)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆e')
    # print('∆e', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_EulersNumbereRaisedtoPowera_1():

    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆E')
    # print('∆E', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(1.718281828459045)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆E')
    # print('∆E', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(6.38905609893065)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆E')
    # print('∆E', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify(19.085536923187668)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆E')
    # print('∆E', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ['(x + 1)^2']]
    expected = vyxalify('x**2 + 2*x + 1')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆E')
    # print('∆E', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_NaturalLogarithm():

    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(0.6931471805599453)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify(1.0986122886681098)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify(1.3862943611198906)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(1.6094379124341003)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [6]]
    expected = vyxalify(1.791759469228055)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [7]]
    expected = vyxalify(1.9459101490553132)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [8]]
    expected = vyxalify(2.0794415416798357)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [9]]
    expected = vyxalify(2.1972245773362196)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [10]]
    expected = vyxalify(2.302585092994046)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [11]]
    expected = vyxalify(2.3978952727983707)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [12]]
    expected = vyxalify(2.4849066497880004)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [13]]
    expected = vyxalify(2.5649493574615367)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [14]]
    expected = vyxalify(2.6390573296152586)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [15]]
    expected = vyxalify(2.70805020110221)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [16]]
    expected = vyxalify(2.7725887222397813)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [17]]
    expected = vyxalify(2.833213344056216)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆L')
    # print('∆L', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Logarithmlog_2():

    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆l')
    # print('∆l', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆l')
    # print('∆l', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_CommonLogarithm():

    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆τ')
    # print('∆τ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(0.3010299956639812)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆τ')
    # print('∆τ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify(0.47712125471966244)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆τ')
    # print('∆τ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify(0.6020599913279624)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆τ')
    # print('∆τ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(0.6989700043360189)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆τ')
    # print('∆τ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [6]]
    expected = vyxalify(0.7781512503836436)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆τ')
    # print('∆τ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [7]]
    expected = vyxalify(0.8450980400142568)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆τ')
    # print('∆τ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [8]]
    expected = vyxalify(0.9030899869919435)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆τ')
    # print('∆τ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [9]]
    expected = vyxalify(0.9542425094393249)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆τ')
    # print('∆τ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [10]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆τ')
    # print('∆τ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_StraightLineDistance():

    stack = [vyxalify(item) for item in [[69, 420], [21, 42]]]
    expected = vyxalify(381.03543142337827)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆d')
    # print('∆d', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ToDegrees():

    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆D')
    # print('∆D', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(57.29577951308232)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆D')
    # print('∆D', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1.5707963267948966]]
    expected = vyxalify(90)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆D')
    # print('∆D', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(114.59155902616465)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆D')
    # print('∆D', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify(171.88733853924697)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆D')
    # print('∆D', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ToRadians():

    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆R')
    # print('∆R', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [90]]
    expected = vyxalify(1.5707963267948966)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆R')
    # print('∆R', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [180]]
    expected = vyxalify(3.141592653589793)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆R')
    # print('∆R', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [270]]
    expected = vyxalify(4.71238898038469)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆R')
    # print('∆R', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [360]]
    expected = vyxalify(6.283185307179586)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆R')
    # print('∆R', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_NextPrimeAfteraNumberDiscriminantofPolynomial():

    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ṗ')
    # print('∆Ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ṗ')
    # print('∆Ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ṗ')
    # print('∆Ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ṗ')
    # print('∆Ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(7)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ṗ')
    # print('∆Ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [69]]
    expected = vyxalify(71)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ṗ')
    # print('∆Ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ['3 * x ** 2 + 493 * x - 2319']]
    expected = vyxalify(270877)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ṗ')
    # print('∆Ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_FirstPrimeBeforeaNumberFactorExpression():

    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṗ')
    # print('∆ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṗ')
    # print('∆ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṗ')
    # print('∆ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṗ')
    # print('∆ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṗ')
    # print('∆ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [69]]
    expected = vyxalify(67)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṗ')
    # print('∆ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ['x**2 - 1']]
    expected = vyxalify('(x - 1)*(x + 1)')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṗ')
    # print('∆ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ['x*3 + x**2']]
    expected = vyxalify('x*(x + 3)')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṗ')
    # print('∆ṗ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_NearestPrimetoaNumberPythonequivalentofanexpression():

    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆p')
    # print('∆p', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆p')
    # print('∆p', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆p')
    # print('∆p', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆p')
    # print('∆p', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆p')
    # print('∆p', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [38]]
    expected = vyxalify(37)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆p')
    # print('∆p', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [40]]
    expected = vyxalify(41)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆p')
    # print('∆p', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [69]]
    expected = vyxalify(71)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆p')
    # print('∆p', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_PolynomialfromRoots():

    stack = [vyxalify(item) for item in [[1, 2, 3]]]
    expected = vyxalify([1, -6, 11, -6])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṙ')
    # print('∆ṙ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[19, 43, 12, 5, 129]]]
    expected = vyxalify([1, -208, 12122, -266708, 2320581, -6323580])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṙ')
    # print('∆ṙ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_RoundtonDecimalPlaces():

    stack = [vyxalify(item) for item in [1.2345, 2]]
    expected = vyxalify(1.23)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆W')
    # print('∆W', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1.2345, 3]]
    expected = vyxalify(1.234)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆W')
    # print('∆W', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1.2345, 4]]
    expected = vyxalify(1.2345)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆W')
    # print('∆W', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1.2345, 5]]
    expected = vyxalify(1.2345)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆W')
    # print('∆W', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_LeastCommonMultiple():

    stack = [vyxalify(item) for item in [1, 2]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ŀ')
    # print('∆Ŀ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [69, 420]]
    expected = vyxalify(9660)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ŀ')
    # print('∆Ŀ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[3,4,5,6]]]
    expected = vyxalify(60)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ŀ')
    # print('∆Ŀ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_nthDigitofPiIntegrate():

    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆i')
    # print('∆i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆i')
    # print('∆i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(4)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆i')
    # print('∆i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆i')
    # print('∆i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆i')
    # print('∆i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(9)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆i')
    # print('∆i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [6]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆i')
    # print('∆i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [7]]
    expected = vyxalify(6)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆i')
    # print('∆i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [8]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆i')
    # print('∆i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [9]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆i')
    # print('∆i', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_NDigitsofEulersNumbereSympyEvaluate():

    stack = [vyxalify(item) for item in [[0, 1, 2, '5 ** 2']]]
    expected = vyxalify([[2], [2, 7], [2, 7, 1], 25])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ė')
    # print('∆Ė', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_NthDigitofEulersNumbereDifferentiate():

    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ė')
    # print('∆ė', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(7)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ė')
    # print('∆ė', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ė')
    # print('∆ė', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify(8)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ė')
    # print('∆ė', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ė')
    # print('∆ė', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(8)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ė')
    # print('∆ė', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_nthFibonacciNumber():

    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆f')
    # print('∆f', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆f')
    # print('∆f', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆f')
    # print('∆f', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆f')
    # print('∆f', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆f')
    # print('∆f', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify(8)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆f')
    # print('∆f', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [6]]
    expected = vyxalify(13)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆f')
    # print('∆f', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [7]]
    expected = vyxalify(21)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆f')
    # print('∆f', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [8]]
    expected = vyxalify(34)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆f')
    # print('∆f', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [9]]
    expected = vyxalify(55)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆f')
    # print('∆f', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_TotientFunctionLocalMinima():

    stack = [vyxalify(item) for item in [[23, 76, 1234, 68, 234, 87, 12, 567]]]
    expected = vyxalify([22, 36, 616, 32, 72, 56, 4, 324])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṫ')
    # print('∆ṫ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ['5*x**2 - 34*x + 213']]
    expected = vyxalify([3.4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṫ')
    # print('∆ṫ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ['(x**2 + x + 1) / x']]
    expected = vyxalify([1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṫ')
    # print('∆ṫ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_NthCardinal():

    stack = [vyxalify(item) for item in [[4324, -48294, 0.5, 93424, 2.3]]]
    expected = vyxalify(['four thousand, three hundred and twenty-four', 'minus forty-eight thousand, two hundred and ninety-four', 'zero point five', 'ninety-three thousand, four hundred and twenty-four', 'two point three'])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ċ')
    # print('∆ċ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_NthOrdinal():

    stack = [vyxalify(item) for item in [0]]
    expected = vyxalify('zeroth')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆o')
    # print('∆o', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify('first')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆o')
    # print('∆o', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify('second')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆o')
    # print('∆o', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify('third')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆o')
    # print('∆o', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify('fourth')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆o')
    # print('∆o', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify('fifth')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆o')
    # print('∆o', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [6]]
    expected = vyxalify('sixth')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆o')
    # print('∆o', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [7]]
    expected = vyxalify('seventh')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆o')
    # print('∆o', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Mode():

    stack = [vyxalify(item) for item in [[1, 1, 1, 1, 2, 2, 3, 3, 3, 4]]]
    expected = vyxalify([1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆M')
    # print('∆M', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 1, 1, 1, 2, 2, 3, 3, 3, 4, 4, 4, 4]]]
    expected = vyxalify([1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆M')
    # print('∆M', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Median():

    stack = [vyxalify(item) for item in [[1, 2, 3, 4, 5]]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṁ')
    # print('∆ṁ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, 3, 4, 5, 6]]]
    expected = vyxalify([3, 4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆ṁ')
    # print('∆ṁ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_PolynomialExpressionFromCoefficients():

    stack = [vyxalify(item) for item in [[1,-12,45,8]]]
    expected = vyxalify('x**3 - 12*x**2 + 45*x + 8')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ċ')
    # print('∆Ċ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3,4,5]]]
    expected = vyxalify('x**4 + 2*x**3 + 3*x**2 + 4*x + 5')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ċ')
    # print('∆Ċ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify('x**3 + x**2 + x + 1')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ċ')
    # print('∆Ċ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[69, 420]]]
    expected = vyxalify('69*x + 420')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆Ċ')
    # print('∆Ċ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_CarmichaelFunction():

    stack = [vyxalify(item) for item in [[3, 8, 12, 78, 234, 786, 1234]]]
    expected = vyxalify([2, 2, 2, 12, 12, 130, 616])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆¢')
    # print('∆¢', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ['(x**2 + x + 1) / x']]
    expected = vyxalify([-1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('∆¢')
    # print('∆¢', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Parenthesise():

    stack = [vyxalify(item) for item in ["xyz"]]
    expected = vyxalify("(xyz)")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øb')
    # print('øb', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify("(5)")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øb')
    # print('øb', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(["(1)","(2)","(3)"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øb')
    # print('øb', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Bracketify():

    stack = [vyxalify(item) for item in ["xyz"]]
    expected = vyxalify("[xyz]")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øB')
    # print('øB', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify("[5]")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øB')
    # print('øB', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(["[1]","[2]","[3]"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øB')
    # print('øB', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_CurlyBracketify():

    stack = [vyxalify(item) for item in ["xyz"]]
    expected = vyxalify("{xyz}")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øḃ')
    # print('øḃ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify("{5}")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øḃ')
    # print('øḃ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(["{1}","{2}","{3}"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øḃ')
    # print('øḃ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_AngleBracketify():

    stack = [vyxalify(item) for item in ["xyz"]]
    expected = vyxalify("<xyz>")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øḂ')
    # print('øḂ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify("<5>")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øḂ')
    # print('øḂ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(["<1>","<2>","<3>"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øḂ')
    # print('øḂ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_BalancedBrackets():

    stack = [vyxalify(item) for item in ["xyz"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øβ')
    # print('øβ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["([)]"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øβ')
    # print('øβ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["({<[]>})"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øβ')
    # print('øβ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [")("]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øβ')
    # print('øβ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_CustomPadLeft():

    stack = [vyxalify(item) for item in ["xyz","x",4]]
    expected = vyxalify("xxyz")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ø↳')
    # print('ø↳', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["123","&",8]]
    expected = vyxalify("&&&&&123")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ø↳')
    # print('ø↳', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["324"," ",2]]
    expected = vyxalify("324")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ø↳')
    # print('ø↳', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_CustomPadRight():

    stack = [vyxalify(item) for item in ["xyz","x",4]]
    expected = vyxalify("xyzx")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ø↲')
    # print('ø↲', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["123","&",8]]
    expected = vyxalify("123&&&&&")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ø↲')
    # print('ø↲', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["324"," ",2]]
    expected = vyxalify("324")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ø↲')
    # print('ø↲', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_FlipBracketsVerticalPalindromise():

    stack = [vyxalify(item) for item in ["(x"]]
    expected = vyxalify("(x)")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øM')
    # print('øM', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["{] "]]
    expected = vyxalify("{] [}")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øM')
    # print('øM', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["/*>X"]]
    expected = vyxalify("/*>X<*\\")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øM')
    # print('øM', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_RemoveUntilNochange():

    stack = [vyxalify(item) for item in ["((()))","()"]]
    expected = vyxalify("")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øo')
    # print('øo', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["--+--+-",["--","+-"]]]
    expected = vyxalify("+")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øo')
    # print('øo', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ReplaceUntilNoChange():

    stack = [vyxalify(item) for item in ["xyzzzzz","yzz","yyyz"]]
    expected = vyxalify("xyyyyyyyyyz")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øV')
    # print('øV', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abb","ab","aa"]]
    expected = vyxalify("aaa")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øV')
    # print('øV', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_StringCompress():

    stack = [vyxalify(item) for item in ["hello"]]
    expected = vyxalify("«D\n=«")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øc')
    # print('øc', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello world"]]
    expected = vyxalify("«⟇%J^9vŀ«")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øc')
    # print('øc', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_NumberCompress():

    stack = [vyxalify(item) for item in [234]]
    expected = vyxalify("»⇧»")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øC')
    # print('øC', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [27914632409837421]]
    expected = vyxalify("»fðǐ4'∞Ẏ»")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øC')
    # print('øC', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Center():

    stack = [vyxalify(item) for item in [["ab","cdef"]]]
    expected = vyxalify([" ab ","cdef"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øĊ')
    # print('øĊ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["xyz","a","bcdef"]]]
    expected = vyxalify([" xyz ","  a  ","bcdef"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øĊ')
    # print('øĊ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 333, 55555]]]
    expected = vyxalify(["  1  ", " 333 ", "55555"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øĊ')
    # print('øĊ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_RunLengthEncoding():

    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify([["a",1],["b",1],["c",1]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øe')
    # print('øe', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["aaa"]]
    expected = vyxalify([["a",3]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øe')
    # print('øe', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_RunLengthDecoding():

    stack = [vyxalify(item) for item in [[["x",3]]]]
    expected = vyxalify("xxx")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ød')
    # print('ød', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[["z",2],["a",3]]]]
    expected = vyxalify("zzaaa")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ød')
    # print('ød', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_DictionaryCompression():

    stack = [vyxalify(item) for item in ["withree"]]
    expected = vyxalify("`wi∧ḭ`")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øD')
    # print('øD', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello"]]
    expected = vyxalify("`ƈṙ`")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øD')
    # print('øD', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["Vyxal"]]
    expected = vyxalify("`₴ŀ`")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øD')
    # print('øD', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Grouponwords():

    stack = [vyxalify(item) for item in ["abc*xyz"]]
    expected = vyxalify(["abc","*","xyz"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øW')
    # print('øW', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["$$$"]]
    expected = vyxalify(["$","$","$"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øW')
    # print('øW', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Regexreplace():

    stack = [vyxalify(item) for item in [".{3}","hello","x"]]
    expected = vyxalify("xlo")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṙ')
    # print('øṙ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["\\W","Hello, World!","E"]]
    expected = vyxalify("HelloEEWorldE")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṙ')
    # print('øṙ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_StartsWith():

    stack = [vyxalify(item) for item in ["hello","h"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øp')
    # print('øp', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello","hello"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øp')
    # print('øp', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello","x"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øp')
    # print('øp', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["hello",""]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øp')
    # print('øp', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_PluraliseCount():

    stack = [vyxalify(item) for item in [4,"hello"]]
    expected = vyxalify("4 hellos")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øP')
    # print('øP', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1,"hello"]]
    expected = vyxalify("1 hello")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øP')
    # print('øP', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [0,"hello"]]
    expected = vyxalify("0 hellos")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øP')
    # print('øP', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_VerticalMirror():

    stack = [vyxalify(item) for item in ["abc"]]
    expected = vyxalify("abccba")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṁ')
    # print('øṁ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_FlipBracketsVerticalMirror():

    stack = [vyxalify(item) for item in ["[}"]]
    expected = vyxalify("[}{]")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṀ')
    # print('øṀ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [")X"]]
    expected = vyxalify(")XX(")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṀ')
    # print('øṀ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["</tag>"]]
    expected = vyxalify("</tag><gat\\>")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṀ')
    # print('øṀ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_StringPartitions():

    stack = [vyxalify(item) for item in ["ab"]]
    expected = vyxalify([["a", "b"], ["ab"]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṖ')
    # print('øṖ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_RomanNumeral():

    stack = [vyxalify(item) for item in [1]]
    expected = vyxalify("I")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2]]
    expected = vyxalify("II")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3]]
    expected = vyxalify("III")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [4]]
    expected = vyxalify("IV")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [5]]
    expected = vyxalify("V")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [6]]
    expected = vyxalify("VI")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [10]]
    expected = vyxalify("X")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [15]]
    expected = vyxalify("XV")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [20]]
    expected = vyxalify("XX")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [30]]
    expected = vyxalify("XXX")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [40]]
    expected = vyxalify("XL")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [50]]
    expected = vyxalify("L")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [60]]
    expected = vyxalify("LX")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [100]]
    expected = vyxalify("C")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [400]]
    expected = vyxalify("CD")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [500]]
    expected = vyxalify("D")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [900]]
    expected = vyxalify("CM")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [1000]]
    expected = vyxalify("M")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [2000]]
    expected = vyxalify("MM")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [3000]]
    expected = vyxalify("MMM")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["I", "II", "III"]]]
    expected = vyxalify([1, 2, 3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["IV"]]
    expected = vyxalify(4)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["V"]]
    expected = vyxalify(5)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["VI"]]
    expected = vyxalify(6)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["X"]]
    expected = vyxalify(10)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["XV"]]
    expected = vyxalify(15)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["XX"]]
    expected = vyxalify(20)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["XXX"]]
    expected = vyxalify(30)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["XL"]]
    expected = vyxalify(40)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["L"]]
    expected = vyxalify(50)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["LX"]]
    expected = vyxalify(60)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["C"]]
    expected = vyxalify(100)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["CD"]]
    expected = vyxalify(400)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["D"]]
    expected = vyxalify(500)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["CM"]]
    expected = vyxalify(900)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["M"]]
    expected = vyxalify(1000)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["MM"]]
    expected = vyxalify(2000)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["MMM"]]
    expected = vyxalify(3000)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('øṘ')
    # print('øṘ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Cartesianproductoverlist():

    stack = [vyxalify(item) for item in [[[1, 2], [3], [4, 5]]]]
    expected = vyxalify([[1, 3, 4], [1, 3, 5], [2, 3, 4], [2, 3, 5]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þ*')
    # print('Þ*', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[[1, 2], [3, 4], []]]]
    expected = vyxalify([])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þ*')
    # print('Þ*', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_AllUnique():

    stack = [vyxalify(item) for item in ["hello"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þu')
    # print('Þu', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["eeee"]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þu')
    # print('Þu', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["Gaming"]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þu')
    # print('Þu', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þu')
    # print('Þu', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,1,1]]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þu')
    # print('Þu', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_CartesianPower():

    stack = [vyxalify(item) for item in ["ab",2]]
    expected = vyxalify(["aa","ab","ba","bb"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞẊ')
    # print('ÞẊ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2],3]]
    expected = vyxalify([[1,1,1],[1,1,2],[1,2,1],[1,2,2],[2,1,1],[2,1,2],[2,2,1],[2,2,2]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞẊ')
    # print('ÞẊ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["abc",3]]
    expected = vyxalify(["aaa","aab","aac","aba","abb","abc","aca","acb","acc","baa","bab","bac","bba","bbb","bbc","bca","bcb","bcc","caa","cab","cac","cba","cbb","cbc","cca","ccb","ccc"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞẊ')
    # print('ÞẊ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_FlattenBydepth():

    stack = [vyxalify(item) for item in [[[[[[1]]]]],3]]
    expected = vyxalify([[1]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þf')
    # print('Þf', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["xyz",[1,2,[3,4,[5,6]]]]]
    expected = vyxalify([1,2,3,4,[5,6]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þf')
    # print('Þf', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_AllLessThanIncreasing():

    stack = [vyxalify(item) for item in [[1,2,2,3,2,1,4,3,2,1], 3]]
    expected = vyxalify([1,2,2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þ<')
    # print('Þ<', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,1,2,3,3,2,4,5,6,7], 4]]
    expected = vyxalify([1,1,2,3,3,2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þ<')
    # print('Þ<', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Untruth():

    stack = [vyxalify(item) for item in [[1]]]
    expected = vyxalify([0,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þǔ')
    # print('Þǔ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[0,3,4,6]]]
    expected = vyxalify([1,0,0,1,1,0,1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þǔ')
    # print('Þǔ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_MultidimensionalIndexing():

    stack = [vyxalify(item) for item in [[1,[2,3]],[1,0]]]
    expected = vyxalify(2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þi')
    # print('Þi', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["xyzabc"], [0,4]]]
    expected = vyxalify("b")
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þi')
    # print('Þi', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_MultidimensionalSearch():

    stack = [vyxalify(item) for item in [[[1,2,3],[4,5,6]], 5]]
    expected = vyxalify([1, 1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þḟ')
    # print('Þḟ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["abc","def",["hij","klm","nop"]], "m"]]
    expected = vyxalify([2,1,2])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þḟ')
    # print('Þḟ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ZeroMatrix():

    stack = [vyxalify(item) for item in [[3,4]]]
    expected = vyxalify([[0,0,0],[0,0,0],[0,0,0],[0,0,0]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þm')
    # print('Þm', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[2,3,2]]]
    expected = vyxalify([[[0,0],[0,0],[0,0]], [[0,0],[0,0],[0,0]]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þm')
    # print('Þm', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_EvenlyDistribute():

    stack = [vyxalify(item) for item in [[1,2,3],6]]
    expected = vyxalify([3,4,5])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þ…')
    # print('Þ…', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3],5]]
    expected = vyxalify([3,4,4])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þ…')
    # print('Þ…', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_AllCombinations():

    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify([[1], [2], [3], [1, 1], [1, 2], [1, 3], [2, 2], [2, 3], [3, 3], [1, 1, 1], [1, 1, 2], [1, 1, 3], [1, 2, 2], [1, 2, 3], [1, 3, 3], [2, 2, 2], [2, 2, 3], [2, 3, 3], [3, 3, 3]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þ×')
    # print('Þ×', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ['ab']]
    expected = vyxalify(['a', 'b', 'aa', 'ab', 'bb'])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þ×')
    # print('Þ×', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_AllCombinationsWithoutReplacement():

    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify([[1], [2], [3], [1, 2], [2, 1], [1, 3], [3, 1], [2, 3], [3, 2], [1, 2, 3], [1, 3, 2], [2, 1, 3], [2, 3, 1], [3, 1, 2], [3, 2, 1]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þx')
    # print('Þx', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["ab"]]
    expected = vyxalify(["a","b","ab","ba"])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þx')
    # print('Þx', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_UniquifyMask():

    stack = [vyxalify(item) for item in [[1,2,3,1,2,3]]]
    expected = vyxalify([1,1,1,0,0,0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞU')
    # print('ÞU', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,1,1,2,3,1,2,2,1,3]]]
    expected = vyxalify([1,0,0,1,1,0,0,0,0,0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞU')
    # print('ÞU', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Diagonals():

    stack = [vyxalify(item) for item in [[[1,2,3],[4,5,6],[7,8,9]]]]
    expected = vyxalify([[1,5,9],[2,6],[3],[7],[4,8]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞD')
    # print('ÞD', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Sublists():

    stack = [vyxalify(item) for item in [[1,2,3]]]
    expected = vyxalify([[1], [1, 2], [2], [1, 2, 3], [2, 3], [3]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞS')
    # print('ÞS', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_TransposeWithFiller():

    stack = [vyxalify(item) for item in [[[1,2,3],[4,5]],0]]
    expected = vyxalify([[1,4],[2,5],[3,0]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞṪ')
    # print('ÞṪ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[[1,2,3,4],[5,6],[7,8,9],[0]],"X"]]
    expected = vyxalify([[1,5,7,0],[2,6,8,"X"],[3,"X",9,"X"],[4,"X","X","X"]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞṪ')
    # print('ÞṪ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_MatrixMultiplication():

    stack = [vyxalify(item) for item in [[[1,2],[3,4]],[[5,6],[7,8]]]]
    expected = vyxalify([[19, 22], [43, 50]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞṀ')
    # print('ÞṀ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_MatrixDeterminant():

    stack = [vyxalify(item) for item in [[[1,2],[3,4]]]]
    expected = vyxalify(-2)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞḊ')
    # print('ÞḊ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[[1,2,3],[4,5,6],[7,8,9]]]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞḊ')
    # print('ÞḊ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Antidiagonal():

    stack = [vyxalify(item) for item in [[[1,2,3],[4,5,6],[7,8,9]]]]
    expected = vyxalify([3,5,7])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þ\\')
    # print('Þ\\', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_MainDiagonal():

    stack = [vyxalify(item) for item in [[[1,2,3],[4,5,6],[7,8,9]]]]
    expected = vyxalify([1,5,9])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þ/')
    # print('Þ/', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_DotProduct():

    stack = [vyxalify(item) for item in [[1,2,3],[4,5,6]]]
    expected = vyxalify(32)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þ•')
    # print('Þ•', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[69, 420], [21, 42]]]
    expected = vyxalify(19089)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þ•')
    # print('Þ•', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Moldwithoutrepeat():

    stack = [vyxalify(item) for item in [[1, 2, 3, 4, 5, 6, 7, 8, 9], [[1], [1, 2], [1, 2, 3], [1], [1, 2], [1, 2, 3]]]]
    expected = vyxalify([[1], [2, 3], [4, 5, 6], [7], [8, 9]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þṁ')
    # print('Þṁ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_MaximalIndicies():

    stack = [vyxalify(item) for item in [[9,2,3,4,5,6,7,8,9]]]
    expected = vyxalify([0,8])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞM')
    # print('ÞM', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ElementwiseVectorisedDyadicMaximum():

    stack = [vyxalify(item) for item in [[1,5,3],[4,2,6]]]
    expected = vyxalify([4, 5, 6])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þ∴')
    # print('Þ∴', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ElementwiseVectorisedDyadicMinimum():

    stack = [vyxalify(item) for item in [[1,5,3],[4,2,6]]]
    expected = vyxalify([1, 2, 3])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þ∵')
    # print('Þ∵', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_AllSlicesofaList():

    stack = [vyxalify(item) for item in [[1, 2, 3, 4, 5, 6, 7, 8, 9], 2]]
    expected = vyxalify([[1, 3, 5, 7, 9], [2, 4, 6, 8]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þs')
    # print('Þs', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[3, 1, 7, 21, 5, 76, 14, 4, 123, 543], 4]]
    expected = vyxalify([[3, 5, 123], [1, 76, 543], [7, 14], [21, 4]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þs')
    # print('Þs', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[3, 1, 2, 4, 6, 4, 5, 2, 1, 9, 5, 3, 9, 3], -4]]
    expected = vyxalify([])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þs')
    # print('Þs', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_RemoveLastItemandPrepend0():

    stack = [vyxalify(item) for item in [[1,2,3,4,5,6,7,8,9]]]
    expected = vyxalify([0,1,2,3,4,5,6,7,8])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þr')
    # print('Þr', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ['abcde']]
    expected = vyxalify('0abcd')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þr')
    # print('Þr', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_RemoveLastItemFromCumulativeSumsandPrepend0():

    stack = [vyxalify(item) for item in [[5, 2, 7, 98, 34, 6, 21, 45]]]
    expected = vyxalify([0, 5, 7, 14, 112, 146, 152, 173])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞR')
    # print('ÞR', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ['abcde']]
    expected = vyxalify([0, 'a', 'ab', 'abc', 'abcd'])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞR')
    # print('ÞR', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Unwrap():

    stack = [vyxalify(item) for item in ['abcde']]
    expected = vyxalify('bcd')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þẇ')
    # print('Þẇ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3,4,5,6,7,8,9]]]
    expected = vyxalify([2, 3, 4, 5, 6, 7, 8])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þẇ')
    # print('Þẇ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1,2,3,4,5,6,7,8,9,10]]]
    expected = vyxalify([2, 3, 4, 5, 6, 7, 8, 9])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þẇ')
    # print('Þẇ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ['lsusp']]
    expected = vyxalify('sus')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þẇ')
    # print('Þẇ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_ShortestByLength():

    stack = [vyxalify(item) for item in [['abcde', 'ab', 'abc', 'abcd', 'abcde']]]
    expected = vyxalify('ab')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þg')
    # print('Þg', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [['abcde',  'abcd', 'abcde', 'abcdef']]]
    expected = vyxalify('abcd')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þg')
    # print('Þg', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [['abcde', 'abcde', 'abcdef', 'abcdefg']]]
    expected = vyxalify('abcde')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þg')
    # print('Þg', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_LongestByLength():

    stack = [vyxalify(item) for item in [['abcde', 'ab', 'abc', 'abcd', 'abcde']]]
    expected = vyxalify('abcde')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞG')
    # print('ÞG', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [['abcde',  'abcd', 'abcde', 'abcdef']]]
    expected = vyxalify('abcdef')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞG')
    # print('ÞG', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [['abcde', 'abcde', 'abcdef', 'abcdefg']]]
    expected = vyxalify('abcdefg')
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞG')
    # print('ÞG', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_SortByLength():

    stack = [vyxalify(item) for item in [['abcde', 'ab', 'abc', 'abcd', 'abcde']]]
    expected = vyxalify(['ab', 'abc', 'abcd', 'abcde', 'abcde'])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þṡ')
    # print('Þṡ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [['abcdef',  'abcd', 'abcde', 'abcdef']]]
    expected = vyxalify(['abcd', 'abcde', 'abcdef', 'abcdef'])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þṡ')
    # print('Þṡ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [['abcdefg', 'abcde', 'abcdef', 'abcdefg']]]
    expected = vyxalify(['abcde', 'abcdef', 'abcdefg', 'abcdefg'])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('Þṡ')
    # print('Þṡ', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Suffixes():

    stack = [vyxalify(item) for item in ['abcde']]
    expected = vyxalify(['abcde', 'bcde', 'cde', 'de', 'e'])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞK')
    # print('ÞK', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, 3, 4, 5, 6]]]
    expected = vyxalify([[1, 2, 3, 4, 5, 6], [2, 3, 4, 5, 6], [3, 4, 5, 6], [4, 5, 6], [5, 6], [6]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞK')
    # print('ÞK', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[]]]
    expected = vyxalify([])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('ÞK')
    # print('ÞK', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Parsedirectionarrowtointeger():

    stack = [vyxalify(item) for item in ["v"]]
    expected = vyxalify(3)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¨□')
    # print('¨□', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["^<><>"]]
    expected = vyxalify([1, 2, 0, 2, 0])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¨□')
    # print('¨□', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["^"]]]
    expected = vyxalify([1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¨□')
    # print('¨□', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["1V_☭"]]
    expected = vyxalify([-1, -1, -1, -1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¨□')
    # print('¨□', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_Parsedirectionarrowtovector():

    stack = [vyxalify(item) for item in ["v"]]
    expected = vyxalify([0, -1])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¨^')
    # print('¨^', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["^<><>"]]
    expected = vyxalify([[0, 1], [-1, 0], [1, 0], [-1, 0], [1, 0]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¨^')
    # print('¨^', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [["^"]]]
    expected = vyxalify([[0, 1]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¨^')
    # print('¨^', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in ["1V_☭"]]
    expected = vyxalify([[0, 0], [0, 0], [0, 0], [0, 0]])
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¨^')
    # print('¨^', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_StrictGreaterThan():

    stack = [vyxalify(item) for item in [[1, 1, 1], [9, 9, 9]]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¨>')
    # print('¨>', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, '3'], [1, 2, '2']]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¨>')
    # print('¨>', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


def test_StrictLessThan():

    stack = [vyxalify(item) for item in [[1, 1, 1], [9, 9, 9]]]
    expected = vyxalify(1)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¨<')
    # print('¨<', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


    stack = [vyxalify(item) for item in [[1, 2, '3'], [1, 2, '2']]]
    expected = vyxalify(0)
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile('¨<')
    # print('¨<', code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx), "Expected " + str(expected) + ", got " + str(simplify(actual))


