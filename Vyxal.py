import VyParse
from VyParse import NAME
from VyParse import VALUE
import string

context_level = 0
input_cycle = 0

_MAP_START = 0
_MAP_OFFSET = 1
join = False

commands = {
    '!': 'stack.push(stack.len())',
    '"': 'stack.shift(RIGHT)',
    "'": 'stack.shift(LEFT)',
    '$': 'stack.swap()',
    '%': 'lhs, rhs = stack.pop(2); stack.push(rhs % lhs)',
    '&': 'if VY_reg_reps % 2:VY_reg=stack.pop()\nelse:stack.push(VY_reg)\nVY_reg_reps += 1',
    '*': 'lhs, rhs = stack.pop(2); stack.push(rhs * lhs)',
    '+': 'lhs, rhs = stack.pop(2); stack.push(rhs + lhs)',
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
    'N': 'top = stack.pop(); stack.push(Number(top))',
    'O': 'lhs, rhs = stack.pop(2); stack.push(rhs.count(lhs))',
    'P': 'TODO',
    'Q': 'exit',
    'R': 'TODO',
    'S': 'stack.push(str(stack.pop()))',
    'T': 'stack.push([n for n in stack.pop() if bool(n)])',
    'U': 'TODO',
    'V': 'stack.push("{}")',
    'W': 'lhs, rhs = stack.pop(2); stack.push(textwrap.wrap(rhs, lhs))',
    'X': 'TODO',
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
    'f': 'TODO',
    'g': 'stack.push(VY_source[stack.pop()])',
    'h': 'stack.push(stack.pop()[0])',
    'i': 'lhs, rhs = stack.pop(2); stack.push(rhs[lhs])',
    'j': 'lhs, rhs = stack.pop(2); stack.push(lhs.join([str(_item) for _item in rhs])); ',
    'k': 'TODO',
    'l': 'stack.push([])',
    'm': 'TODO',
    'n': 'TODO',
    'o': 'stack.push(type(stack.pop()))',
    'p': 'TODO',
    'q': 'stack.push('"' + str(stack.pop()) + '"')',
    'r': 'lhs, rhs = stack.pop(2); stack.push(list(range(rhs, lhs)))',
    's': 'top = stack.pop(); stack.push(type(top)(sorted(top)))',
    't': 'stack.push(stack.pop()[-1])',
    'u': 'TODO',
    'v': 'TODO',
    'w': 'stack.push([stack.pop()])',
    'x': 'TODO',
    'y': 'TODO',
    'z': 'TODO',
    '~': 'stack.push(random.randint(-INT, INT))',
    '¬': 'stack.push(not stack.pop())',
    '∧': 'lhs, rhs = stack.pop(2); stack.push(bool(rhs and lhs))',
    '⟑': 'lhs, rhs = stack.pop(2); stack.push(rhs and lhs)',
    '∨': 'lhs, rhs = stack.pop(2); stack.push(bool(rhs or lhs))',
    '⟇': 'lhs, rhs = stack.pop(2); stack.push(rhs or lhs)',
    '÷': 'TODO',
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
    '\t': ''
}

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
    def len(self):
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


def get_input():
    global input_cycle
    if inputs:
        item = inputs[input_cycle % len(inputs)]
        input_cycle += 1
        return item
    else:
        try:
            item = input()
            return item
        except Exception:
            return 0


def Number(item):
    if type(item) in [float, int]:
        return item

    else:
        try:
            x = float(str(item))
            try:
                y = int(str(item))
                return y if x == y else x
            except ValueError:
                return x
        
        except ValueError:
            return item

def smart_range(item):
    if type(item) is int:
        x =  range(item)

    elif type(item) is float:
        x =  range(int(item))

    else:
        x =  item
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


def VyCompile(source):
    if not source:
        return "pass"
    global context_level
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
                context_level += 1

                if not VyParse.FOR_VARIABLE in token[VALUE]:
                    var_name = "_context_" + str(context_level)
                else:
                    var_name = strip_non_alphabet(token\
                                                  [VALUE][VyParse.FOR_VARIABLE])

                            
                compiled += f"for {var_name} in smart_range(stack.pop()):" + newline
                compiled += tab(VyCompile(token[VALUE][VyParse.FOR_BODY]))

                context_level -= 1
            elif token[NAME] == VyParse.WHILE_STMT:
                context_level += 1

                if not VyParse.WHILE_CONDITION in token[VALUE]:
                    condition = "stack.push(1)"
                else:
                    condition = VyCompile(token[VALUE][VyParse.WHILE_CONDITION])

                            
                compiled = f"{condition}\nwhile stack.pop():" + newline
                compiled += tab(VyCompile(token[VALUE][VyParse.WHILE_BODY])) + newline
                compiled += tab(condition)

                context_level -= 1

            elif token[NAME] == VyParse.FUNCTION_STMT:
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
                    compiled += tab(f"temp = Stack(stack.pop({number_of_parameters}))") + newline
                    compiled += tab("stack = temp") + newline
                    compiled += tab(VyCompile(token[VALUE][VyParse.FUNCTION_BODY])) + newline
                    compiled += tab("return stack") + newline

            elif token[NAME] == VyParse.LAMBDA_STMT:
                compiled += "def _lambda(item):" + newline
                compiled += tab("global VY_reg_reps; stack = Stack([item])") + newline
                compiled += tab(VyCompile(token[VALUE][VyParse.LAMBDA_BODY])) + newline
                compiled += tab("return stack[-1]") + newline
                compiled += "stack.push(_lambda)" + newline     
                
    return compiled

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
            line = VyCompile(line)
            exec(header + line)
            print(stack)
    else:
        if flags:
            if "M" in flags:
                _MAP_START = 1

            if "m" in flags:
                _MAP_OFFSET = 0

            if 'j' in flags:
                join = True
        file = open(file_location, "r", encoding="utf-8")
        code = file.read()

        code = VyCompile(code)
        exec(header + code)

        if not printed:
            if join:
                print("\n".join([str(n) for n in stack[-1]]))
            else:
                print(stack[-1])
