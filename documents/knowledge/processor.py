import yaml

import os
import sys

ELEMENTS_YAML = "elements.yaml"
TEST_ELEMENTS_PY = "../../tests/test_elements.py"

with open(ELEMENTS_YAML, "r", encoding="utf-8") as elements:
    data = yaml.safe_load(elements)
# Generate test cases
with open(TEST_ELEMENTS_PY, "w", encoding="utf-8") as tests:
    tests.write(
        "import os, sys\n\n"
        + "THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + '/..'\n"
        + "sys.path.insert(1, THIS_FOLDER)\n\n"
        + "from vyxal.transpile import *\n"
        + "from vyxal.context import Context\n"
        + "from vyxal.elements import *\n"
        + "from vyxal.LazyList import *\n"
    )
    for element in data:
        if "tests" in element:
            cases = element["tests"]
            tests.write(
                "def test_"
                f"{element['name'].replace(' ', '').replace('/', '_')}():\n"
            )
            for test in cases:
                stack, expected = test.split(" : ", 1)
                tests.write(f"\tstack = {stack}; expected = {expected}\n")
                tests.write(f"\tctx = Context()\n")
                tests.write(f"\tcode = transpile('{element['element']}')\n")
                tests.write(f"\texec(code)\n")
                tests.write(f"\tassert simplify(stack[-1]) == expected\n\n")
            tests.write("\n\n")
        else:
            continue
