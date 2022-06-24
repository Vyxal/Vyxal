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


    stack = [vyxalify(item) for item in ["43",5]]
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


    stack = [vyxalify(item) for item in ["43",5]]
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


    stack = [vyxalify(item) for item in ["acv",36]]
    expected = vyxalify(13423)
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


    stack = [vyxalify(item) for item in ["1b2",12]]
    expected = vyxalify(278)
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
