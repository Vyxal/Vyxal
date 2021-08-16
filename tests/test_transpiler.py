from vyxal.parse import parse
from vyxal.transpile import transpile


def test_if():
    # TODO(user/ysthakur) try with more branches
    vy = """[ 1 | 2 ]"""
    py = transpile(vy)
    expected = """condition = pop(stack, ctx=ctx)
ctx.context_values.append(condition)
if boolify(condition, ctx):
    stack.append(1)
else:
    stack.append(2)
ctx.context_values.pop()
"""
    assert py == expected
