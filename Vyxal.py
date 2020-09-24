# -*- coding: UTF-8 -*-
import VyParse
from VyParse import NAME
from VyParse import VALUE
import string

_context_level = 0
_max_context_level = 0
_input_cycle = 0

_MAP_START = 0
_MAP_OFFSET = 1
_join = False
_use_encoding = False

_RIGHT = "RIGHT"
_LEFT = "LEFT"

codepage = "λ¬∧⟑∨⟇÷«»°\n․⍎½∆øÏÔÇæʀʁɾɽÞƈ∞⫙ß⎝⎠ !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~⎡⎣⨥⨪∺❝ð£¥§¦¡∂ÐřŠč√∖ẊȦȮḊĖẸṙ∑Ṡİ•\t"
codepage += "Ĥ⟨⟩ƛıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋŌōŎŏŐőŒœŔŕŖŗŘŚśŜŝŞşšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽžſƀƁƂƃƄƅƆƇƊƋƌƍƎ¢≈Ωªº"

commands = {
    '!': 'stack.push(len(stack))',
    '"': 'stack.shift(_RIGHT)',
    "'": 'stack.shift(_LEFT)',
    '$': 'stack.swap()',
    '%': 'lhs, rhs = stack.pop(2); stack.push(rhs % lhs)',
    '&': 'if VY_reg_reps % 2:VY_reg=stack.pop()\nelse:stack.push(VY_reg)\nVY_reg_reps += 1',
    '*': 'lhs, rhs = stack.pop(2); stack.push(rhs * lhs)',
    '+': 'rhs, lhs = stack.pop(2); stack.push(add(lhs, rhs))',
    ',': 'pprint(stack.pop()); printed = True',
    '-': 'lhs, rhs = stack.pop(2); stack.push(rhs - lhs)',
    '.': 'print(stack.pop(), end=""); printed = True',
    '/': 'lhs, rhs = stack.pop(2); stack.push(rhs / lhs)',
    ':': 'top = stack.pop(); stack.push(top); stack.push(top)',
    '<': 'lhs, rhs = stack.pop(2); stack.push(rhs < lhs)',
    '=': 'lhs, rhs = stack.pop(2); stack.push(rhs == lhs)',
    '>': 'lhs, rhs = stack.pop(2); stack.push(rhs > lhs)',
    '?': 'stack.push(get_input())',
    'A': 'stack.push(stack.all())',
    'B': 'stack.push(int(stack.pop(), 2))',
    'C': 'stack.push("{}")',
    'D': 'top = stack.pop(); stack.push(top); stack.push(top); stack.push(top)',
    'E': 'x = stack.pop(); stack.push(eval(x))',
    'F': 'stack.do_filter(stack.pop())',
    'G': 'lhs, rhs = stack.pop(2); stack.push(math.gcd(rhs, lhs))',
    'H': 'stack.push(int(stack.pop(), 16))',
    'I': 'stack.push(int(stack.pop()))',
    'J': 'lhs, rhs = stack.pop(2); stack.push(rhs + lhs)',
    'K': 'stack.push({})',
    'L': 'stack.push(len(stack.pop()))',
    'M': 'stack.do_map(stack.pop())',
    'N': 'top = stack.pop(); stack.push(to_number(top))',
    'O': 'lhs, rhs = stack.pop(2); stack.push(rhs.count(lhs))',
    'P': 'TODO',
    'Q': 'exit()',
    'R': 'TODO',
    'S': 'stack.push(str(stack.pop()))',
    'T': 'stack.push([n for n in stack.pop() if bool(n)])',
    'U': 'TODO',
    'V': 'stack.push("{}")',
    'W': 'lhs, rhs = stack.pop(2); stack.push(textwrap.wrap(rhs, lhs))',
    'X': 'if _context_level + 1 < _max_context_level: _context_level += 1',
    'Y': 'TODO',
    'Z': 'lhs, rhs = stack.pop(2); stack.push(list(zip(rhs, lhs)))',
    '^': 'stack.reverse()',
    '_': 'stack.pop()',
    '`': 'stack.push("{}")',
    'a': 'stack.push(any(x))',
    'b': 'stack.push(bin(x))',
    'c': 'lhs, rhs = stack.pop(2); stack.push(rhs in lhs)',
    'd': 'stack.push(stack.pop() * 2)',
    'e': 'lhs, rhs = stack.pop(2); stack.push(rhs ** lhs)',
    'f': 'stack.push(flatten(stack.pop())',
    'g': 'stack.push(VY_source[stack.pop()])',
    'h': 'stack.push(stack.pop()[0])',
    'i': 'lhs, rhs = stack.pop(2); stack.push(rhs[lhs])',
    'j': 'lhs, rhs = stack.pop(2); stack.push(lhs.join([str(_item) for _item in rhs])); ',
    'l': 'stack.push([])',
    'm': 'TODO',
    'n': 'stack.push(eval(f"_context_{_context_level}"))',
    'o': 'stack.push(type(stack.pop()))',
    'p': 'TODO',
    'q': 'stack.push('"' + str(stack.pop()) + '"')',
    'r': 'lhs, rhs = stack.pop(2); stack.push(list(range(rhs, lhs)))',
    's': 'top = stack.pop(); stack.push(type(top)(sorted(top)))',
    't': 'stack.push(stack.pop()[-1])',
    'u': 'TODO',
    'w': 'stack.push([stack.pop()])',
    'x': '_context_level -= 1 * (1 - (_context_level == 0))',
    'y': 'TODO',
    'z': 'TODO',
    '~': 'stack.push(random.randint(-INT, INT))',
    '¬': 'stack.push(not stack.pop())',
    '∧': 'lhs, rhs = stack.pop(2); stack.push(bool(rhs and lhs))',
    '⟑': 'lhs, rhs = stack.pop(2); stack.push(rhs and lhs)',
    '∨': 'lhs, rhs = stack.pop(2); stack.push(bool(rhs or lhs))',
    '⟇': 'lhs, rhs = stack.pop(2); stack.push(rhs or lhs)',
    '÷': 'for item in stack.pop(): stack.push(item)',
    '⍎': 'stack += (stack.pop())(stack)',
    'Ṛ': 'lhs, rhs = stack.pop(2); stack.push(random.randint(rhs, lhs))',
    'Ï': 'lhs, rhs = stack.pop(2); stack.push(rhs.index(lhs))',
    'Ô': 'TODO',
    'Ç': 'TODO',
    'ʀ': 'stack.push(list(range(0, stack.pop() + 1)))',
    'ʁ': 'stack.push(list(range(0, stack.pop())))',
    'ɾ': 'stack.push(list(range(1, stack.pop() + 1)))',
    'ɽ': 'stack.push(list(range(1, stack.pop())))',
    'Þ': 'top = stack.pop(); stack.push(top == top[::-1])',
    'ƈ': 'TODO',
    '∞': 'TODO',
    'ß': 'TODO',
    '∺': 'stack.push(stack.pop() % 2)',
    "∻": 'lhs, rhs = stack.pop(2); stack.push((rhs % lhs) == 0)',
    '\n': '',
    '\t': '',
    "Ĥ": "stack.push(Number(100))",
    "Ĵ": "stack.push(''.join(stack.pop())",
    "Ĳ": "stack.push('\\n'.join(stack.pop()))",
    "ĳ": "stack.push(Number(10))"
    }

class Number(int):
    def __init__(self, value):
        self.value = value

    def __getitem__(self, pos):
        temp = str(self.value)[pos]
        return int(temp) if temp.isnumeric() else temp 

    def __add__(self, rhs):
        return Number(self.value + rhs)

    def __sub__(self, rhs):
        return Number(self.value - rhs)

    def __mul__(self, rhs):
        return Number(self.value * rhs)

    def __div__(self, rhs):
        return Number(self.value / rhs)

    def __mod__(self, rhs):
        return Number(self.value % rhs)

class Stack(list):
    def __init__(self, prelist=None):
        if prelist:
            if type(prelist) is list:
                self.contents = prelist
            else:
                self.contents = [prelist]
        else:
            self.contents = []
    def push(self, item):
        self.contents.append(item)

    def swap(self):
        self.contents[-1], self.contents[-2] = self.contents[-2], self.contents[-1]
    def __len__(self):
        return len(self.contents)
    def all(self):
        return all(self.contents)
    def reverse(self):
        self.contents = self.contents[::-1]

    def __repr__(self):
        return repr(self.contents)

    def __self__(self):
        return str(self.contents)

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

    def do_map(self, fn):
        temp = []
        obj = self.contents.pop()
        if type(obj) is Number:
            obj = list(range(_MAP_START, int(obj) + _MAP_OFFSET))
            obj = [Number(x) for x in obj]
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

    def shift(self, direction):
        if direction == _LEFT:
            self.contents = self.contents[::-1]
            temp = self.pop()
            self.contents = self.contents[::-1]
            self.contents.append(temp)
        else:
            self.contents.insert(0, self.pop())

    def pop(self, n=1):
        if n == 1:
            if len(self.contents):
                return self.contents.pop()
            else:
                return get_input()
        items = []

        if len(self.contents) >= n:
            for i in range(n):
                items.append(self.contents.pop())
        else:
            for i in range(len(self.contents)):
                items.append(self.contents.pop())
        
            while len(items) < n:
                items.append(get_input())
        return items


def types(*args):
    return list(map(type, args))

def add(lhs, rhs):
    ts = types(lhs, rhs)
    if ts == [Number, Number] or ts == [str, str]:
        return lhs + rhs

    elif ts == [Number, str]:
        rhs = to_number(rhs)
        if type(rhs) is Number:
            return lhs + rhs
        else:
            return str(lhs) + rhs

    elif ts == [Number, Stack] or ts == [str, Stack]:
        for n in range(len(rhs)):
            rhs[n] = add(lhs, rhs[n])
        return rhs

    elif ts == [str, Number]:
        lhs = to_number(lhs)
        if type(lhs) is Number:
            return lhs + rhs
        else:
            return str(lhs) + str(rhs)

    elif ts == [Stack, Number] or ts == [Stack, str]:
        for n in range(len(lhs)):
            lhs[n] = add(lhs[n], rhs)
        return lhs

    elif ts == [Stack, Stack]:
        if len(lhs) != len(rhs):
            if len(lhs) < len(rhs):
                lhs.extend([0] * len(rhs) - len(lhs))
            else:
                rhs.extend([0] * len(lhs) - len(rhs))

        for n in range(len(lhs)):
            lhs[n] = add(lhs[n], rhs[n])
        return lhs
    else:
        return 0
            
    
        

def flatten(nested_list):
    flattened = []
    for item in nested_list:
        if type(item) is list:
            flattened += flatten(item)
        else:
            flattened.append(item)

    return flattened

def get_input():
    global _input_cycle
    if inputs:
        item = inputs[_input_cycle % len(inputs)]
        _input_cycle += 1
        return item
    else:
        try:
            temp = input()
            if type(eval(temp)) is float:
                temp = float(temp)
            elif type(eval(temp)) is int:
                temp = int(temp)
            elif type(eval(temp)) is list:
                temp = Stack(eval(temp))
            elif type(eval(temp)) is str:
                temp = temp
        except Exception:
            return 0
        return temp


def to_number(item):
    if type(item) in [float, int]:
        return Number(item)

    else:
        try:
            x = Number(float(str(item)))
            try:
                y = Number(int(str(item)))
                return y if x == y else x
            except ValueError:
                return x
        
        except ValueError:
            return item

def smart_range(item):
    if type(item) is Number and type(item.value) is int:
        x =  range(item)
        x = [Number(y) for y in x]
    elif type(item) is Number and type(item.value) is float:
        x = range(int(item))
        x = [Number(y) for y in x]
    else:
        x = item
    return x

def pprint(item):
    print(item)

def strip_non_alphabet(name):
    result = ""
    for char in name:
        if char in string.ascii_letters:
            result += char

    return result

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
                compiled += f"stack.push(Number({token[VALUE]}))" + newline

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
                        
                    compiled += f"def {name}(stack):" + newline
                    compiled += tab("global VY_reg_reps") + newline
                    compiled += tab(f"_context_{_context_level} = stack[:-{number_of_paramters}]") + newline
                    compiled += tab(f"temp = Stack(stack.pop({number_of_parameters}))") + newline
                    compiled += tab("stack = temp") + newline
                    compiled += tab(VyCompile(token[VALUE][VyParse.FUNCTION_BODY])) + newline
                    compiled += tab("return stack") + newline

                _context_level -= 1

            elif token[NAME] == VyParse.LAMBDA_STMT:
                _context_level += 1
                if _context_level > _max_context_level:
                    _max_context_level = _context_level
                compiled += "def _lambda(item):" + newline
                compiled += tab("global VY_reg_reps; stack = Stack([item])") + newline
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
                import bases
                import encoding
                
                if bases.to_ten(token[VALUE], encoding.compression) < len(words._words):
                    compiled += f"stack.push({repr(words.extract_word(token[VALUE]))})" + newline
                    
                
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
        inputs = list(map(eval,sys.argv[3:]))


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
        # print(code)
        exec(code)

        if not printed:
            if _join:
                print("\n".join([str(n) for n in stack[-1]]))
            else:
                print(stack[-1])
