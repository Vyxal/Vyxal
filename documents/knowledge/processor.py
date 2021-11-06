import os
import re
import yaml

CURR_DIR = os.path.dirname(os.path.realpath(__file__))
ELEMENTS_YAML = os.path.join(CURR_DIR, "elements.yaml")
TEST_ELEMENTS_PY = os.path.join(CURR_DIR, "..", "..", "tests", "test_elements.py")

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
        try:
            if "tests" in element:
                cases = element["tests"]
                name = re.sub('[^A-Za-z0-9_]', '', str(element['name']))
                tests.write(f"def test_{name}():\n")
                if cases:
                    for test in cases:
                        try:
                            stack, expected = test.split(" : ", 1)
                        except Exception as e:
                            print('Failed on test', test)
                            raise e
                        unordered = (
                            len(expected) > 1
                            and expected[0] == '{' and expected[-1] == '}')
                        tests.write(f"\tstack = {stack}\n")
                        tests.write(f"\texpected = {expected}\n")
                        tests.write(f"\tctx = Context()\n")
                        tests.write(
                            f"\tcode = transpile('{element['element']}');print(code)\n")
                        tests.write(f"\texec(code)\n")
                        if unordered:
                            tests.write(
                                f"\tassert set(simplify(stack[-1])) == expected\n\n")
                        else:
                            tests.write(
                                f"\tassert simplify(stack[-1]) == expected\n\n")
                else:
                    tests.write("\tpass #TODO implement this test!!!\n\n")
                tests.write("\n")
            else:
                continue
        except Exception as e:
            print("Failed in element", element)
            raise e
