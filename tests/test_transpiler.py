from vyxal.parse import parse
from vyxal.transpile import transpile


def test_if():
    # TODO(user/cgccuser) try with more branches
    vy = """[ 1 | 2_ ]"""
    py = transpile(vy)
    expected = """condition = pop(stack, 1, ctx=ctx)
if boolify(condition, ctx):
    stack.append(sympy.S("1", rational=True))
else:
    stack.append(sympy.S("-2", rational=True))
"""
    assert py == expected
