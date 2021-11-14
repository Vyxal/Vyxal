import os
import re
import yaml

CURR_DIR = os.path.dirname(os.path.realpath(__file__))
ELEMENTS_YAML = os.path.join(CURR_DIR, "elements.yaml")
TEST_ELEMENTS_PY = os.path.join(
    CURR_DIR, "..", "..", "tests", "test_elements.py"
)

with open(ELEMENTS_YAML, "r", encoding="utf-8") as elements:
    data = yaml.safe_load(elements)
# Generate test cases
with open(TEST_ELEMENTS_PY, "w", encoding="utf-8") as tests:
    tests.write(
        "import os, sys, sympy\n\n"
        + "THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + '/..'\n"
        + "sys.path.insert(1, THIS_FOLDER)\n\n"
        + "from vyxal.transpile import *\n"
        + "from vyxal.context import Context\n"
        + "from vyxal.elements import *\n"
        + "from vyxal.helpers import *\n"
        + "from vyxal.LazyList import *\n"
    )

    tests.write(
        "def make_nice(x):\n"
        "    x = simplify(x)\n"
        "    if isinstance(x, float):\n"
        "        return sympy.nsimplify(x)\n"
        "    else:\n"
        "        return x\n"
    )
    for element in data:
        try:
            if "tests" in element:
                cases = element["tests"]
                name = re.sub("[^A-Za-z0-9_]", "", str(element["name"]))
                tests.write(f"def test_{name}():\n")
                if cases:
                    for test in cases:
                        try:
                            stack, expected = test.split(" : ", 1)
                        except Exception as e:
                            print("Failed on test", test)
                            raise e
                        tests.write(
                            f"\tstack = [vyxalify(elem) for elem in {stack}]\n"
                        )
                        tests.write(f"\texpected = make_nice({expected})\n")
                        tests.write(f"\tctx = Context()\n")
                        tests.write("\tctx.stacks.append(stack)\n")
                        tests.write(
                            f"\tcode = transpile({element['element']!r})"
                            "; print(code)\n"
                        )
                        tests.write(f"\texec(code)\n")
                        tests.write("\tctx.stacks.pop()\n")

                        tests.write(
                            f"\tassert make_nice(stack[-1]) == expected\n\n"
                        )
                else:
                    tests.write("\tpass #TODO implement this test!!!\n\n")
                tests.write("\n")
            else:
                continue
        except Exception as e:
            print("Failed in element", element)
            raise e
