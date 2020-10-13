import VyParse
from VyParse import NAME
from VyParse import VALUE
from codepage import *
import math
import string

_context_level = 0
_max_context_level = 0
_input_cycle = 0

_MAP_START = 0
_MAP_OFFSET = 1
_join = False
_vertical_join = False
_use_encoding = False

_RIGHT = "RIGHT"
_LEFT = "LEFT"

def as_iter(item):
    if type(item) in [int, float]:
        return [int(x) if x != "." else x for x in str(item)]
    else:
        return item

def types(*args):
    temp = list(map(type, args))
    return [Number if item in [int, float] else item for item in temp]

def get_input():
    global _input_cycle
    if inputs:
        item = inputs[_input_cycle % len(inputs)]
        _input_cycle += 1
        return item
    else:
        temp = input()
        return Vy_eval(temp)

def add(lhs, rhs):
    ts = types(lhs, rhs)
    if ts in [[Number, Number], [str, str]]:
        return lhs + rhs

    elif ts == [Number, str]:
        return str(lhs) + rhs

    elif ts == [str, Number]:
        return lhs + str(rhs)

    elif ts == [Stack, Stack]:
        if len(lhs) != len(rhs):
            if len(lhs) < len(rhs):
                lhs.extend([0] * len(rhs) - len(lhs))
            else:
                rhs.extend([0] * len(lhs) - len(rhs))

        for n in range(len(lhs)):
            lhs[n] = add(lhs[n], rhs[n])
        return lhs

    elif ts[-1] is Stack:
        for n in range(len(rhs)):
            rhs[n] = add(lhs, rhs[n])
        return rhs

    elif ts[0] is Stack:
        for n in range(len(lhs)):
            lhs[n] = add(lhs[n], rhs)
        return lhs

    else:
        return 0

def subtract(lhs, rhs):
    ts = types(lhs, rhs)
    if ts == [Number, Number]:
        return lhs - rhs

    elif ts == [Number, str]:
        return str(lhs).replace(rhs, "")

    elif ts == [str, Number]:
        return lhs.replace(str(rhs), "")

    elif ts == [str, str]:
        return lhs.replace(rhs, "")

    elif ts == [Stack, Stack]:
        if len(lhs) != len(rhs):
            if len(lhs) < len(rhs):
                lhs.extend([0] * len(rhs) - len(lhs))
            else:
                rhs.extend([0] * len(lhs) - len(rhs))

        for n in range(len(lhs)):
            lhs[n] = subtract(lhs[n], rhs[n])
        return lhs

    elif ts[-1] is Stack:
        for n in range(len(rhs)):
            rhs[n] = subtract(lhs, rhs[n])
        return rhs

    elif ts[0] is Stack:
        for n in range(len(lhs)):
            lhs[n] = subtract(lhs[n], rhs)
        return lhs

def multiply(lhs, rhs):
    ts = types(lhs, rhs)
    if ts in [[Number, Number], [Number, str], [str, Number]]:
        return lhs * rhs

    elif ts == [str, str]:
        result = ""
        if len(lhs) > len(rhs):
            for i in range(len(rhs)):
                result += lhs[i] + rhs[i]
            result += lhs[i + 1:]
        elif len(lhs) > len(rhs):
            for i in range(len(lhs)):
                result += lhs[i] + rhs[i]
            result += rhs[i + 1:]
        else:
            for i in range(len(lhs)):
                result += lhs[i] + rhs[i]
        return result

    elif ts == [Stack, Stack]:
        if len(lhs) != len(rhs):
            if len(lhs) < len(rhs):
                lhs.extend([0] * len(rhs) - len(lhs))
            else:
                rhs.extend([0] * len(lhs) - len(rhs))

        for n in range(len(lhs)):
            lhs[n] = multiply(lhs[n], rhs[n])
        return lhs

    elif ts[-1] is Stack:
        for n in range(len(rhs)):
            rhs[n] = multiply(lhs, rhs[n])
        return rhs

    elif ts[0] is Stack:
        for n in range(len(lhs)):
            lhs[n] = multiply(lhs[n], rhs)
        return lhs


def divide(lhs, rhs):
    import textwrap
    ts = types(lhs, rhs)

    if ts == [Number, Number]:
        return lhs / rhs

    elif ts == [Number, str]:
        return Stack(textwrap.wrap(rhs, lhs))

    elif ts == [str, Number]:
        return Stack(textwrap.wrap(lhs, rhs))

    elif ts == [str, str]:
        return Stack(lhs.split(rhs))

    elif ts == [Stack, Stack]:
        if len(lhs) != len(rhs):
            if len(lhs) < len(rhs):
                lhs.extend([0] * len(rhs) - len(lhs))
            else:
                rhs.extend([0] * len(lhs) - len(rhs))

        for n in range(len(lhs)):
            lhs[n] = divide(lhs[n], rhs[n])
        return lhs

    elif ts[-1] == Stack:
        for n in range(len(rhs)):
            rhs[n] = divide(lhs, rhs[n])
        return rhs

    elif ts[0] == Stack:
        for n in range(len(lhs)):
            lhs[n] = divide(lhs[n], rhs)
        return lhs

def modulo(lhs, rhs):
    ts = types(lhs, rhs)

    if ts == [Number, Number]:
        return lhs % rhs

    elif ts in [[Number, str], [str, Number]]:
        return divide(lhs, rhs)[-1]

    elif ts == [str, str]:
        return lhs.format(rhs)

    elif ts == [Stack, Stack]:
        if len(lhs) != len(rhs):
            if len(lhs) < len(rhs):
                lhs.extend([0] * len(rhs) - len(lhs))
            else:
                rhs.extend([0] * len(lhs) - len(rhs))

        for n in range(len(lhs)):
            lhs[n] = modulo(lhs[n], rhs[n])
        return lhs

    elif ts[-1] is Stack:
        for n in range(len(rhs)):
            rhs[n] = modulo(lhs, rhs[n])
        return rhs

    elif ts[0] is Stack:
        for n in range(len(lhs)):
            lhs[n] = modulo(lhs[n], rhs)
        return lhs

def join(lhs, rhs):
    ts = types(lhs, rhs)
    if ts[0] == Stack:
        lhs.push(rhs)
        return lhs

    elif ts[-1] == Stack:
        rhs.contents.insert(0, lhs)
        return rhs

    else:
        return str(lhs) + str(rhs)

class Number: pass
class Stack(list):
    def __init__(self, prelist=None, inputs=[]):
        if prelist:
            if type(prelist) is list:
                self.contents = prelist
            else:
                self.contents = [prelist]
        else:
            self.contents = []

        self.inputs = inputs
        self.input_number = 0
    def push(self, item):
        self.contents.append(item)

    def swap(self):
        top, topnt = stack.pop(2)
        stack.push(top)
        stack.push(topnt)
    def __len__(self):
        return len(self.contents)
    def all(self):
        return all(self.contents)
    def reverse(self):
        self.contents = self.contents[::-1]

    def __repr__(self):
        return "⟨" + "|".join([repr(x) for x in self.contents]) + "⟩"

    def __str__(self):
        return "⟨" + "|".join([repr(x) for x in self.contents]) + "⟩"

    def __getitem__(self, n):
        return self.contents[n]

    def __setitem__(self, index, value):
        self.contents[index] = value

    def __iadd__(self, rhs):
        if type(rhs) == type(self):
            self.contents += rhs.contents
        else:
            self.contents += rhs
        return self

    def __add__(self, rhs):
        return self.contents + rhs

    def __mult__(self, rhs):
        return self.contents * rhs

    def __iter__(self):
        return iter(self.contents)

    def do_map(self, fn):
        temp = []
        obj = self.pop()
        if type(obj) in [int, float]:
            obj = list(range(_MAP_START, int(obj) + _MAP_OFFSET))
        for item in obj:
            temp.append(fn(item))
        self.contents.append(temp)

    def do_filter(self, fn):
        temp = []
        obj = self.contents.pop()
        if type(obj) in [int, float]:
            obj = list(range(_MAP_START, int(obj) + _MAP_OFFSET))

        for item in obj:
            x = fn(item)
            if bool(x):
                temp.append(item)

        self.contents.append(temp)


    def do_fixed_gen(self, fn, n=1):
        temp = []
        curr = 0
        while len(temp) < n:
            result = fn(curr)
            if result:
                temp.append(curr)
            curr += 1

        self.contents.append(temp)

    def shift(self, direction):
        if direction == _LEFT:
            self.contents = self.contents[::-1]
            temp = self.pop()
            self.contents = self.contents[::-1]
            self.contents.append(temp)
        else:
            self.contents.insert(0, self.pop())

    def pop(self, n=1, wrap=False):
        # wrap tells us if we need to wrap lists of len 1 into a list
        items = []
        for _ in range(n):
            if len(self.contents) != 0:
                items.append(self.contents.pop())
            else:
                if self.inputs:
                    items.append(self.inputs[self.input_number % len(items)])
                    self.input_number += 1
                else:
                    items.append(get_input())
        if n == 1:
            if wrap == False:
                return items[0]

        return items


def flatten(nested_list):
    flattened = []
    for item in nested_list:
        if type(item) is list:
            flattened += flatten(item)
        else:
            flattened.append(item)

    return flattened

def Vy_repr(item):
    if type(item) is str:
        return "`" + item + "`"

    else:
        return repr(item)


def Vy_int(item, base=10):
    if type(item) is Stack:
        result = 0
        for value in item:
            result = multiply(result, base)
            result = add(result, value)

        return result
    else:
        return int(item, base)


def chrord(item):
    if type(item) in [int, float]:
        return chr(int(item))

    elif type(item) is str and len(item) == 1:
        return ord(item)

    else:
        return Stack([chrord(x) for x in item])


def Vy_eval(item):
        try:
            if type(eval(item)) in [float, int]:
                item = int(item)
            elif type(eval(item)) is list:
                item = Stack(eval(item))
            else:
                pass
        except Exception:
            return item
        return item

def to_number(item):
    if type(item) in [float, int]:
        return item
    else:
        try:
            x = (float(str(item)))
            try:
                y = (int(str(item)))
                return y if x == y else x
            except ValueError:
                return x

        except ValueError:
            return item

def sums(item):
    new = Stack()
    for i in range(len(item)):
        new.push(summate(item[0:i+1]))
    return new

def smart_range(item, start=0, lift_factor=0):
    if type(item):
        x =  range(start, item + lift_factor)
        x = [int(y) for y in x]
    elif type(item) is float:
        x = range(start, int(item) + lift_factor)
        x = [int(y) for y in x]
    else:
        x = item
    return x

def orderless_range(a, b, lift_factor=0):
    if a < b:
        return range(a, b + lift_factor)
    else:
        return range(b, a + lift_factor)

def VyRound(item):
    if type(item) is str:
        return item
    elif type(item) in [float, int]:
        return round(item)
    elif type(item) is Stack:
        return Stack([VyRound(x) for x in item])
    else:
        return item

def divisors_of(value):
    if type(value) is Stack:
        return Stack([divisors_of(x) for x in value])


    divs = []

    for item in smart_range(value, 1, 1):
        if modulo(value, item) == 0:
            divs.append(item)

    return Stack(divs)

def distribute(iterable, value):
    # [1, 2, 3, 4] 4 => [2, 3, 4, 5]
    # [1, 1, 1] 2 => [2, 2, 1]

    remaining = value
    index = 0
    while remaining > 0:
        iterable[index % len(iterable)] += 1
        index += 1
        remaining -= 1

    return iterable
def summate(item):
    x = as_iter(item)
    result = 0
    for v in x:
        result = add(result, v)

    return result

def strip_non_alphabet(name):
    result = ""
    for char in name:
        if char in string.ascii_letters:
            result += char

    return result


def vertical_join(iterable, padding=" "):
    lengths = list(map(len, iterable))
    iterable = [padding * (max(lengths) - len(x)) + x for x in iterable]

    out = ""
    for i in range(max(lengths)):
        for item in iterable:
            out += item[i]
        out += "\n"

    return out

newline = "\n"
tab = lambda x: newline.join(["    " + m for m in x.split(newline)]).rstrip("    ")


def VyCompile(source, header=""):
    if not source:
        return "pass"
    global _context_level
    global _max_context_level
    tokens = VyParse.Tokenise(source)
    compiled = ""
    for token in tokens:
        if token[NAME] == VyParse.NO_STMT and token[VALUE] in commands:
            compiled += commands[token[VALUE]] + newline

        else:
            if token[NAME] == VyParse.INTEGER:
                compiled += f"stack.push({token[VALUE]})" + newline

            elif token[NAME] == VyParse.STRING_STMT:
                string = token[VALUE][VyParse.STRING_CONTENTS]
                string = string.replace("'", "\\'")
                string = string.replace('"', "\\\"")

                import uncompress
                string = uncompress.uncompress(string)
                #string = string.replace("\\", "\\\\")
                compiled += f"stack.push(\"{string}\")" \
                            + newline

            elif token[NAME] == VyParse.CHARACTER:
                compiled += f"stack.push({repr(token[VALUE][0])})" + newline

            elif token[NAME] == VyParse.IF_STMT:
                onTrue = token[VALUE][VyParse.IF_ON_TRUE]
                onTrue = tab(VyCompile(onTrue))

                compiled += "condition = bool(stack.pop())" + newline
                compiled += "if condition:" + newline
                compiled += onTrue

                if VyParse.IF_ON_FALSE in token[VALUE]:
                    onFalse = token[VALUE][VyParse.IF_ON_FALSE]
                    onFalse = tab(VyCompile(onFalse))

                    compiled += "else:"  + newline
                    compiled += onFalse

            elif token[NAME] == VyParse.FOR_STMT:
                _context_level += 1
                if _context_level > _max_context_level:
                    _max_context_level = _context_level

                if not VyParse.FOR_VARIABLE in token[VALUE]:
                    var_name = "_context_" + str(_context_level)
                else:
                    var_name = strip_non_alphabet(token\
                                                  [VALUE][VyParse.FOR_VARIABLE])


                compiled += f"for {var_name} in smart_range(stack.pop()):" + newline
                compiled += tab(VyCompile(token[VALUE][VyParse.FOR_BODY]))

                _context_level -= 1
            elif token[NAME] == VyParse.WHILE_STMT:
                _context_level += 1
                if _context_level > _max_context_level:
                    _max_context_level = _context_level

                if not VyParse.WHILE_CONDITION in token[VALUE]:
                    condition = "stack.push(1)"
                else:
                    condition = VyCompile(token[VALUE][VyParse.WHILE_CONDITION])

                compiled += f"_context_{_context_level} = stack[-1]"
                compiled = f"{condition}\nwhile stack.pop():" + newline
                compiled += tab(VyCompile(token[VALUE][VyParse.WHILE_BODY])) + newline
                compiled += tab(condition) + newline
                compiled += tab(f"_context_{_context_level} = stack[-1]") + newline

                _context_level -= 1

            elif token[NAME] == VyParse.FUNCTION_STMT:
                _context_level += 1

                if _context_level > _max_context_level:
                    _max_context_level = _context_level
                if VyParse.FUNCTION_BODY not in token[VALUE]:
                    compiled += f"stack += {token[VALUE][VyParse.FUNCTION_NAME]}(stack)" + newline
                else:
                    function_data = token[VALUE][VyParse.FUNCTION_NAME].split(":")
                    number_of_parameters = 0
                    name = function_data[0]

                    if len(function_data) == 2:
                        number_of_parameters = int(function_data[1])

                    compiled += f"def {name}(in_stack):" + newline
                    compiled += tab("global VY_reg_reps") + newline
                    compiled += tab(f"_context_{_context_level} = Stack(in_stack[:-{number_of_parameters}])") + newline
                    compiled += f"args = in_stack.pop({number_of_parameters}"
                    compiled += tab(f"stack = Stack(args, args))") + newline
                    x = VyCompile(token[VALUE][VyParse.FUNCTION_BODY])
                    compiled += tab(x) + newline

                    compiled += tab("return stack") + newline

                _context_level -= 1

            elif token[NAME] == VyParse.LAMBDA_STMT:
                _context_level += 1
                if _context_level > _max_context_level:
                    _max_context_level = _context_level
                compiled += "def _lambda(item):" + newline
                compiled += tab("global VY_reg_reps; stack = Stack([item], [item])") + newline
                compiled += tab(f"_context_{_context_level} = item") + newline
                compiled += tab(VyCompile(token[VALUE][VyParse.LAMBDA_BODY])) + newline
                compiled += tab("return stack[-1]") + newline
                compiled += "stack.push(_lambda)" + newline
                _context_level -= 1

            elif token[NAME] == VyParse.LIST_STMT:
                compiled += "_temp_list = []" + newline
                for item in token[VALUE][VyParse.LIST_ITEMS]:
                    compiled += VyCompile(item) + newline
                    compiled += "_temp_list.append(stack.pop())" + newline
                compiled += "_temp_list = Stack(_temp_list)" + newline
                compiled += "stack.push(_temp_list)" + newline


            elif token[NAME] == VyParse.CONSTANT_CHAR:
                import string
                import math
                constants = {
                    "A": string.ascii_uppercase,
                    "e": math.e,
                    "f": "Fizz",
                    "b": "Buzz",
                    "F": "FizzBuzz",
                    "H": "Hello, World!",
                    "h": "Hello World",
                    "1": 1000
                }

                compiled += f"stack.push({repr(constants[token[VALUE]])})" + newline

            elif token[NAME] == VyParse.SINGLE_SCC_CHAR:
                import words
                import utilities
                import encoding

                if utilities.to_ten(token[VALUE], encoding.compression) < len(words._words):
                    compiled += f"stack.push({repr(words.extract_word(token[VALUE]))})" + newline

            elif token[NAME] == VyParse.VARIABLE_SET:
                compiled += "VAR_" + token[VALUE][VyParse.VARIABLE_NAME] +\
                            " = stack.pop()" + newline

            elif token[NAME] == VyParse.VARIABLE_GET:
                compiled += "stack.push(VAR_" + token[VALUE][VyParse.VARIABLE_NAME] +\
                            ")" + newline
    return header + compiled

if __name__ == "__main__":
    import sys

    file_location = ""
    flags = ""
    inputs = []
    header = "stack = Stack()\nVY_reg_reps = 1\nVY_reg = 0\nprinted = False\n"

    if len(sys.argv) > 1:
        file_location = sys.argv[1]

    if len(sys.argv) > 2:
        flags = sys.argv[2]
        inputs = list(map(Vy_eval,sys.argv[3:]))

    if not file_location: #repl mode
        while 1:
            line = input(">>> ")
            _context_level = 0
            line = VyCompile(line, header)
            _context_level = 1
            exec(line)
            print(stack)
    else:
        if flags:
            if "M" in flags:
                _MAP_START = 1

            if "m" in flags:
                _MAP_OFFSET = 0

            if 'j' in flags:
                _join = True

            if 'L' in flags:
                _vertical_join = True

            if 'v' in flags:
                _use_encoding = True

        # Encoding method thanks to Adnan (taken from the old 05AB1E interpreter)
        if _use_encoding:
            import encoding
            code = open(file_location, "rb").read()
            code = encoding.vyxal_to_utf8(code)
        else:
            code = open(file_location, "r", encoding="utf-8").read()

        code = VyCompile(code, header)
        _context_level = 1
        if flags and 'c' in flags:
            print(code)
        exec(code)

        if not printed:
            if flags and 's' in flags:
                print(summate(stack.pop()))
            elif _vertical_join:
                print(vertical_join(stack.pop()))
            elif _join:
                print("\n".join([str(n) for n in stack.pop()]))
            else:
                print(stack.pop())
