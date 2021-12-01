from vyxal.parse import parse
from vyxal.transpile import transpile


def test_if():
    # TODO(user/cgccuser) try with more branches
    vy = """[ 1 | 2 ]"""
    py = transpile(vy)
    expected = """condition = pop(stack, 1, ctx=ctx)
if boolify(condition, ctx):
    stack.append(1)
else:
    stack.append(2)
"""
    assert py == expected
