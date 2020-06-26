import VyParse
from VyParse import NAME
from VyParse import VALUE
source = input("Enter Source: ")


commands = {
    '!': 'stack.push(len(stack))',
    '"': 'stack.shift(RIGHT)',
    "'": 'stack.shift(LEFT)',
    '$': 'stack[-1], stack[-2] = stack[-2], stack[-1]',
    '%': 'lhs, rhs = stack.pop(2); stack.push(lhs % rhs)',
    '&': 'VY_reg = stack.pop() if VY_reg else stack.push(VY_reg)',
    '*': 'lhs, rhs = stack.pop(2); stack.push(lhs * rhs)',
    '+': 'lhs, rhs = stack.pop(2); stack.push(lhs + rhs)',
    ',': 'pprint(stack.pop())',
    '-': 'lhs, rhs = stack.pop(2); stack.push(lhs - rhs)',
    '.': 'print(stack.pop())',
    '/': 'lhs, rhs = stack.pop(2); stack.push(lhs / rhs)',
    ':': 'top = stack.pop(); stack.push(top); stack.push(top)',
    '<': 'lhs, rhs = stack.pop(2); stack.push(lhs < rhs)',
    '=': 'lhs, rhs = stack.pop(2); stack.push(lhs == rhs)',
    '>': 'lhs, rhs = stack.pop(2); stack.push(lhs > rhs)',
    '?': 'stack.push(get_input())',
    'A': 'stack.push(all(stack))',
    'B': 'stack.push(int(stack.pop(), 2))',
    'C': 'stack.push("<CHAR>")',
    'D': 'top = stack.pop(); stack.push(top); stack.push(top); stack.push(top)',
    'E': 'stack.push(eval(x))',
    'F': 'TODO',
    'G': 'lhs, rhs = stack.pop(2); stack.push(math.gcd(lhs, rhs))',
    'H': 'stack.push(int(stack.pop(), 16))',
    'I': 'stack.push(int(stack.pop()))',
    'J': 'lhs, rhs = stack.pop(2); stack.push(lhs + rhs)',
    'K': 'stack.push(<CONSTANT>)',
    'L': 'stack.push(len(stack.pop()))',
    'M': 'TODO',
    'N': 'TODO',
    'O': 'lhs, rhs = stack.pop(2); stack.push(lhs.count(rhs))',
    'P': 'TODO',
    'Q': 'exit',
    'R': 'TODO',
    'S': 'stack.push(str(stack.pop()))',
    'T': 'stack.push([n for n in stack.pop() if bool(n)])',
    'U': 'TODO',
    'V': 'stack.push("<CODEPAGE>")',
    'W': 'lhs, rhs = stack.pop(2); stack.push(textwrap.wrap(lhs, rhs))',
    'X': 'TODO',
    'Y': 'TODO',
    'Z': 'lhs, rhs = stack.pop(2); stack.push(list(zip(lhs, rhs)))',
    '^': 'stack.reverse()',
    '_': 'stack.pop()',
    '`': 'stack.push("<STRING>")',
    'a': 'stack.push(any(x))',
    'b': 'stack.push(bin(x))',
    'c': 'lhs, rhs = stack.pop(2); stack.push(lhs in rhs)',
    'd': 'stack.push(stack.pop() * 2)',
    'e': 'lhs, rhs = stack.pop(2); stack.push(lhs ** rhs)',
    'f': 'TODO',
    'g': 'stack.push(VY_source[stack.pop()])',
    'h': 'stack.push(stack.pop()[0])',
    'i': 'lhs, rhs = stack.pop(2); stack.push(lhs[rhs])',
    'j': 'lhs, rhs = stack.pop(2); stack.push(lhs.join(rhs))',
    'k': 'TODO',
    'l': 'stack.push([])',
    'm': 'TODO',
    'n': 'TODO',
    'o': 'stack.push(type(stack.pop()))',
    'p': 'TODO',
    'q': 'stack.push('"' + str(stack.pop()) + '"')',
    'r': 'lhs, rhs = stack.pop(2); stack.push(list(range(lhs, rhs)))',
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
    '∧': 'lhs, rhs = stack.pop(2); stack.push(bool(lhs and rhs))',
    '⟑': 'lhs, rhs = stack.pop(2); stack.push(lhs and rhs)',
    '∨': 'lhs, rhs = stack.pop(2); stack.push(bool(lhs or rhs))',
    '⟇': 'lhs, rhs = stack.pop(2); stack.push(lhs or rhs)',
    '÷': 'TODO',
    '⍎': 'TODO',
    'Ṛ': 'lhs, rhs = stack.pop(2); stack.push(random.randint(lhs, rhs))',
    'Ï': 'lhs, rhs = stack.pop(2); stack.push(lhs.index(rhs))',
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
    '∺': 'stack.push(stack.pop() % 2)'
}

class Stack(list):
    def __init__(self):
        self.contents = []
    def push(self, item):
        self.contents.append(item)

    def __repr__(self):
        return repr(self.contents)

    def __self__(self):
        return str(self.contents)


tab = lambda x: "    " + x

def VyCompile(source):
    tokens = VyParse.Tokenise(source)
    compiled = ""
    for token in tokens:
        if type(token[VALUE]) == str and token[VALUE] in commands:
            compiled += commands[token[VALUE]] + "\n"

        else:
            if token[NAME] == VyParse.INTEGER:
                compiled += f"stack.push({token[VALUE]})\n"

            elif token[NAME] == VyParse.STRING_STMT:
                compiled += f"stack.push('{token[VALUE][VyParse.STRING_CONTENTS]}')\n"

            elif token[NAME] == VyParse.IF_STMT:
                onTrue = token[VALUE][VyParse.IF_ON_TRUE]
                #onFalse = token[VALUE][VyParse.IF_ON_FALSE]

                onTrue = tab(VyCompile(onTrue))
                #onFalse = tab(VyCompile(onFalse))

                print(onTrue)

                compiled += "condition = bool(stack.pop())\n"
                compiled += "if condition:\n"
                compiled += onTrue
            

                
                
    return compiled

x = "stack = Stack()\n" + VyCompile(source)
print(x)
