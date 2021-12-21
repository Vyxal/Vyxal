import os
import re
import yaml

CURR_DIR = os.path.dirname(os.path.realpath(__file__))
ELEMENTS_YAML = os.path.join(CURR_DIR, "elements.yaml")
TEST_ELEMENTS_PY = os.path.join(
    CURR_DIR, "..", "..", "tests", "test_elements.py"
)


prologue = """import os
import sys
import sympy
THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + '/..'
sys.path.insert(1, THIS_FOLDER)

from vyxal.transpile import *
from vyxal.elements import *
from vyxal.context import Context
from vyxal.helpers import *
from vyxal.LazyList import *
"""

function_template = """
    stack = [vyxalify(item) for item in {}]
    expected = vyxalify({})
    ctx = Context()

    ctx.stacks.append(stack)

    code = transpile({})
    # print({}, code)
    exec(code)

    ctx.stacks.pop()
    actual = vyxalify(stack[-1])

    print(simplify(expected), simplify(actual))

    if vy_type(actual, simple=True) is list or vy_type(expected, simple=True) is list:
        assert all(deep_flatten(equals(actual, expected, ctx), ctx)) or non_vectorising_equals(actual, expected, ctx)
    else:
        assert equals(actual, expected, ctx) or non_vectorising_equals(actual, expected, ctx)

"""


with open(ELEMENTS_YAML, "r", encoding="utf-8") as elements:
    data = yaml.safe_load(elements)

# Generate test cases

names = []

with open(TEST_ELEMENTS_PY, "w", encoding="utf-8") as tests:
    tests.write(prologue + "\n")

    for element in data:
        try:
            if "tests" in element:
                cases = element["tests"] or []
                name = re.sub("[^A-Za-z0-9_\-]", "", str(element["name"]))
                name = name.replace("-", "_")
                names.append(name)
                tests.write(f"def test_{name}():\n")
                if not cases:
                    tests.write("    pass #TODO implement this test!!!\n\n")
                    continue
                for test in cases:
                    try:
                        stack, expected = test.split(" : ", 1)
                    except Exception as e:
                        print("Failed on test", test)
                        raise e

                    tests.write(
                        function_template.format(
                            stack,
                            expected,
                            repr(element["element"]),
                            repr(element["element"]),
                        )
                    )
                tests.write("\n")
            else:
                continue
        except Exception as e:
            print("Failed in element", element)
            raise e

print([x for x in names if names.count(x) > 1])
