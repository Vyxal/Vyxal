from vyxal.parse import parse
from vyxal.transpile import transpile


def test_if():
    # TODO(user/ysthakur) try with more branches
    vy = """[ 1 | 2 ]"""
    py = transpile(vy)
    expected = """condition = pop(stack)
context_values.append(condition)
if boolify(condition):
    stack.append(1)
else:
    stack.append(2)
context_values.pop()
"""
    print(py)
    assert py == expected
