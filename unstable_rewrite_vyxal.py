# Python modules

from datetime import date
from datetime import datetime as dt
import functools
import hashlib
import itertools
import math
import random
import string
import time
import warnings

# Vyxal modules
import commands
import encoding
import utilities
import VyParse
import words

# Generic type constants
Number = "NUMBER"
Iterable = "ITERABLE"
Function = type(lambda: None)

NEWLINE = "\n"

# Execution variables
inputs = []
context_level = 0
context_values = []
input_level = 0
register = 0
input_scopes = {0: 0}
stack = []
printed = False

MAP_START = 0
MAP_OFFSET = 1
_join = False
_vertical_join = False
use_encoding = False

# Helper classes
class Comparitors:
    EQUALS = 0
    LESS_THAN = 1
    GREATER_THAN = 2
    NOT_EQUALS = 3
    LESS_THAN_EQUALS = 4
    GREATER_THAN_EQUALS = 5
class Generator:
    def __init__(self, raw_generator):
        if type(raw_generator) is Function:
            def gen():
                index = 0
                while True:
                    yield raw_generator(index)
                    index += 1
            self.gen = gen()
        else:
            self.gen = raw_generator
            self.backup = self.gen
    def __index__(self, position):
        warnings.warn("Can't index generators. Check what you're doing")
        return self.gen
    def __next__(self):
        return next(self.gen)
    def _map(self, function):
        self.gen = map(function, self.gen)
        return self
    def _filter(self, function):
        self._map(function)
        self.gen = filter(None, self.gen)
        return self
    def _reduce(self, function):
        return functools.reduce(function, self.gen)
    def _dereference(self):
        '''
        Only call this when it is absolutely neccesary to convert to a list.
        '''
        temp = self.gen
        return list(temp)
    def _print(self):
        print("⟨", end="")
        try:
            print(next(self.gen), end="")
        except:
            print("⟩")
            return
        while True:
            try:
                item = next(self.gen)
            except StopIteration:
                print("⟩")
            print("|" + str(item), end="")
class ShiftDirections:
    LEFT = 1
    RIGHT = 2

# Helper functions
def add(lhs, rhs):
    types = VY_type(lhs), VY_type(rhs)
    return {
        (Number, Number): lambda: lhs + rhs,
        (str, str): lambda: lhs + rhs,
        (str, Number): lambda: lhs + str(rhs),
        (Number, str): lambda: str(lhs) + rhs,
        (list, types[1]): lambda: [add(item, rhs) for item in lhs],
        (types[0], list): lambda: [add(lhs, item) for item in rhs],
        (Generator, types[1]): lambda: vectorise(add, lhs, rhs),
        (types[1], Generator): lambda: vectorise(add, lhs, rhs),
        (list, list): lambda: list(map(sum, zip(lhs, rhs)))
    }[types]()
def assigned(vector, index, item):
    if type(vector) is str:
        vector = list(vector)
        vector[index] = item
        return "".join([str(x) for x in vector])
    else:
        vector[index] = item
        return vector
def bit_and(lhs, rhs):
    types = (VY_type(lhs), Vy_type(rhs))
    return {
        (Number, Number): lambda: lhs & rhs,
        (Number, str): lambda: "".join([chr(lhs & ord(let)) for let in rhs]),
        (str, Number): lambda: "".join([chr(ord(let) & rhs) for let in lhs]),
        (types[0], list): lambda: [bit_and(lhs, item) for item in rhs],
        (list, types[1]): lambda: [bit_and(item, rhs) for item in lhs],
        (Generator, types[1]): lambda: vectorise(bit_and, lhs, rhs),
        (types[1], Generator): lambda: vectorise(bit_and, lhs, rhs),
        (list, list): lambda: list(map(lambda x: bit_and(*x), zip(lhs, rhs)))
    }[types](rhs)
def bit_or(lhs, rhs):
    types = (VY_type(lhs), Vy_type(rhs))
    return {
        (Number, Number): lambda: lhs | rhs,
        (Number, str): lambda: "".join([chr(lhs | ord(let)) for let in rhs]),
        (str, Number): lambda: "".join([chr(ord(let) | rhs) for let in lhs]),
        (types[0], list): lambda: [bit_or(lhs, item) for item in rhs],
        (list, types[1]): lambda: [bit_or(item, rhs) for item in lhs],
        (Generator, types[1]): lambda: vectorise(bit_or, lhs, rhs),
        (types[1], Generator): lambda: vectorise(bit_or, lhs, rhs),
        (list, list): lambda: list(map(lambda x: bit_or(*x), zip(lhs, rhs)))
    }[types](rhs)
def bit_not(item):
    return {
        list: lambda: [bit_not(x) for x in item],
        str: lambda: [~ord(let) for let in item],
        Number: lambda: ~item,
        Generator: lambda: vectorise(bit_not, item)
    }[VY_type(item)]()
def bit_xor(lhs, rhs):
    types = (VY_type(lhs), Vy_type(rhs))
    return {
        (Number, Number): lambda: lhs ^ rhs,
        (Number, str): lambda: "".join([chr(lhs ^ ord(let)) for let in rhs]),
        (str, Number): lambda: "".join([chr(ord(let) ^ rhs) for let in lhs]),
        (types[0], list): lambda: [bit_xor(lhs, item) for item in rhs],
        (list, types[1]): lambda: [bit_xor(item, rhs) for item in lhs],
        (list, list): lambda: list(map(lambda x: bit_xor(*x), zip(lhs, rhs)))
    }[types](rhs)
def chrord(item):
    t_item = VY_type(item)
    if t_item is str and len(item) == 1:
        return ord(item)
    elif t_item == Number:
        return chr(int(item))
    else:
        return map(chrord, item)
def compare(lhs, rhs, mode):
    op = ["==", "<", ">", "!=", "<=", ">="][mode]
    types = tuple(map(VY_type, [lhs, rhs]))

    return {
        types: lambda: eval(f"lhs {op} rhs"),
        (Number, str): lambda: eval(f"str(lhs) {op} rhs"),
        (str, Number): lambda: eval(f"lhs {op} str(rhs)"),
        (types[0], list): lambda: [compare(lhs, item, mode) for item in rhs],
        (list, types[1]): lambda: [compare(item, rhs, mode) for item in lhs],
        (list, list): lambda: list(map(lambda x: compare(*x, mode), zip(lhs, rhs)))
    }[types]()
def counts(vector):
    ret = []
    vector = iterable(vector)
    for item in set(vector):
        ret.append([item, vector.count(item)])
    return ret
def cumulative_sum(vector):
    ret = []
    vector = iterable(vector)
    for i in range(len(vector)):
        ret.append(summate(vector[:i]))
    return ret
def deltas(vector):
        ret = []
        vector = iterable(vector)
        for i in range(len(iterable) - 1):
            ret.append(subtract(vector[i], vector[i + 1]))
        return ret
def deref(item):
        if type(item) not in [int, float, str]: return item[::]
        return item
def distribute(vector, value):
    vector = interable(vector)
    remaining = value
    index = 0
    while remaining > 0:
        vector[index % len(iterable)] += 1
        index += 1
        remaining -= 1

    return vector
def divide(lhs, rhs):
        import textwrap
        types = VY_type(lhs), VY_type(rhs)

        return {
            (Number, Number): lambda: lhs / rhs,
            (str, str): lambda: lhs.split(rhs),
            (str, Number): lambda: textwrap.wrap(lhs, rhs),
            (Number, str): lambda: textwrap.wrap(rhs, lhs),
            (list, types[1]): lambda: [divide(item, rhs) for item in lhs],
            (types[0], list): lambda: [divide(lhs, item) for item in rhs],
            (list, list): lambda: list(map(lambda x: divide(*x), zip(lhs, rhs)))
        }[types]()
def divisors_of(item):
    t_item = VY_type(stack)
    if t_item in [list, Generator]:
        return vectorise(divisors_of, item)

    divisors = []

    for value in VY_range(item, 1, 1):
        if modulo(item, value) == 0:
            divisors.append(item)

    return divisors
def exponate(lhs, rhs):
    types = (VY_type(lhs), VY_type(rhs))

    return {
        (Number, Number): lambda: lhs ** rhs,
        (str, Number): lambda: lhs * int(rhs),
        (Number, str): lambda: rhs * int(lhs),
        (types[0], list): lambda: [exponate(lhs, item) for item in rhs],
        (list, types[1]): lambda: [exponate(item, rhs) for item in lhs],
        (list, list): lambda: list(map(exponate(*x), zip(lhs, rhs)))
    }[types]()
def first_n(func, n=1):
    ret = []
    current_index = 0

    while len(ret) < n:
        result = fn([current_index])
        if result: ret.append(current_index)

    return ret
def flatten(item):
    t_item = VY_type(item)
    if t_item is Generator:
        return Generator(functools.reduce(list.__add__, item))
    else:
        ret = []
        for x in item:
            if type(x) is list:
                ret += x
            else:
                ret.append(x)
        return ret
def get_input(explicit=None):
    global input_index
    if inputs:
        if context_level:
            return context_values[context_level % len(context_values)]
        input_index += 1
        return inputs[(input_index - 1) % len(inputs)]
    else:
        return VY_eval(input())
def graded(vector):
    return Generator(sorted(enumerate(iterable), key=lambda x: x[-1]))
def group_consecutive(vector):
    ret = []
    temp = [vector[0]]
    last = vector[0]
    for item in vector[1:]:
        if item == last:
            temp.append(item)
        else:
            ret.append(temp)
            temp = [item]
            last = item

    if len(ret) == 0 or temp != ret[-1]:
        ret.append(temp)

    return ret
def indexes_where(fn, vector):
    ret = []
    for i in range(len(vector)):
        if fn(vector[i]):
            ret.append(i)
    return ret
def inserted(vector, item, index):
    vector = iterable(vector, range)
    t_vector = type(vector)
    return {
        list: lambda: vector.insert(index, item),
        range: lambda: list(vector[:index]) + [item] + list(vector[index:]),
        str: lambda: vector[:index] + str(item) + vector[index:]
    }[t_vector]()
def iterable(item, t=list):
    if VY_type(item) == Number:
        if t is list:
            return [int(let) if let != "." else let for let in str(item)]
        if t is range:
            return range(int(item))
        return t(item)
    else:
        return item
def iterable_shift(vector, direction):
    vector = iterable(vector)
    t_vector = type(vector)
    if direction == ShiftDirections.LEFT:
        if t_vector is list:
            # [1, 2, 3] -> [2, 3, 1]
            temp = pop(vector[::-1])
            vector.append(temp)
            return vector
        else:
            # abc -> bca
            return vector[1:] + vector[0]
    elif direction == ShiftDirections.RIGHT:
        if t_vector is list:
            # [1, 2, 3] -> [3, 1, 2]
            temp = pop(vector)
            vector.insert(0, temp)
            return vector
        else:
            # abc -> cab
            return vector[-1] + vector[:-1]
def integer_list(string):
    charmap = dict(zip("cetaoinshr ", "0123456789,"))
    temp = "["
    for c in o:
        temp += charmap.get(c, "")
    temp += "]"
    return eval(temp)
def interleave(lhs, rhs):
    interleaved = flatten(zip(iterable(lhs), iterable(rhs)))
    if len({VY_type(lhs), VY_type(rhs)} & {list, Generator}) == 0:
        return "".join([str(x) for x in interleaved._dereference()])
    return interleaved
def is_prime(n):
    if n % 2 == 0 and n > 2:
        return False
    return all(n % i for i in range(3, int(math.sqrt(n)) + 1, 2))
def join(lhs, rhs):
    types = tuple(map(Vy_type, [lhs, rhs]))
    return {
        (types[0], types[1]): lambda: str(lhs) + str(rhs),
        (types[0], list): lambda: rhs.insert(0, lhs),
        (list, types[1]): lambda: lhs.append(rhs),
        (list, list): lambda: lhs + rhs
    }[types]()
def lshift(lhs, rhs):
    types = (VY_type(lhs), Vy_type(rhs))
    return {
        (Number, Number): lambda: lhs << rhs,
        (Number, str): lambda: "".join([chr(lhs << ord(let)) for let in rhs]),
        (str, Number): lambda: "".join([chr(ord(let) << rhs) for let in lhs]),
        (types[0], list): lambda: [lshift(lhs, item) for item in rhs],
        (list, types[1]): lambda: [lshift(item, rhs) for item in lhs],
        (list, list): lambda: list(map(lambda x:lshift(*x), zip(lhs, rhs)))
    }[types](rhs)
def modulo(lhs, rhs):
    types = VY_type(lhs), VY_type(rhs)
    return {
        (Number, Number): lambda: lhs % rhs,
        (str, str): lambda: lhs.format(rhs),
        (str, Number): lambda: divide(lhs, rhs)[-1],
        (Number, str): lambda: divide(lhs, rhs)[-1],
        (list, types[1]): lambda: [modulo(item, rhs) for item in lhs],
        (types[0], list): lambda: [modulo(lhs, item) for item in rhs],
        (list, list): lambda: list(map(lambda x: modulo(*x), zip(lhs, rhs)))
    }[types]()
def multiply(lhs, rhs):
    types = VY_type(lhs), VY_type(rhs)

    return {
        (Number, Number): lambda: lhs * rhs,
        (str, str): lambda: "".join(["".join(x) for x in zip(lhs, rhs)]),
        (str, Number): lambda: lhs * rhs,
        (Number, str): lambda: lhs * rhs,
        (list, types[1]): lambda: [multiply(item, rhs) for item in lhs],
        (types[0], list): lambda: [multiply(lhs, item) for item in rhs],
        (list, list): lambda: list(map(lambda x: multiply(*x), zip(lhs, rhs)))
    }[types]()
def orderless_range(*args, lift_factor=0):
    return range(min(args), max(args) + lift_factor)
def partition(item):
    # https://stackoverflow.com/a/44209393/9363594
    yield [n]
    for i in range(I, n//2 + 1):
        for p in partition(n-i, i):
            yield [i] + p
def prepend(vector, item):
    vector = iterable(vector, range)
    t_vector = type(vector)
    return {
        list: lambda: vector.insert(0, item),
        str: lambda: str(item) + vector,
        range: lambda: [item] + list(vector)
    }[t_vector]()
def pop(vector, num=1, wrap=False, explicit=None):
    ret = []
    for _ in range(num):
        if vector:
            ret.append(vector.pop())
        else:
            ret.append(get_input(explicit))

    if num == 1 and not wrap:
        return ret[0]
    return ret
def repeat(vector, times):
    vector = iterable(vector)
    t_vector = VY_type(vector)
    if times < 0:
        return vector[::-1] * times
    else:
        return vector * times
def replace(haystack, needle, replacement):
    t_haystack = VY_type(haystack)
    if t_haystack is list:
        return [replacement if value == needle else value for value in haystack]
    elif t_haystack is Generator:
        return haystack # Not sure how to do replacement on generators yet
    else:
        return str(haystack).replace(str(needle), str(haystack))
def reverse(vector):
        if type(vector) in [float, int]:
            s_vector = str(vector)
            if vector < 0:
                return -int(s_vector[1:][::-1])
            else:
                return int(s_vector[::-1])
        else:
            return vector[::-1]
def rshift(lhs, rhs):
    types = (VY_type(lhs), Vy_type(rhs))
    return {
        (Number, Number): lambda: lhs >> rhs,
        (Number, str): lambda: "".join([chr(lhs >> ord(let)) for let in rhs]),
        (str, Number): lambda: "".join([chr(ord(let) >> rhs) for let in lhs]),
        (types[0], list): lambda: [rshift(lhs, item) for item in rhs],
        (list, types[1]): lambda: [rshift(item, rhs) for item in lhs],
        (list, list): lambda: list(map(lambda x:rshift(*x), zip(lhs, rhs)))
    }[types](rhs)
def sign_of(item):
    t = VY_type(item)
    if t == Number:
        if item < 0: return -1
        else: return [0, 1][item != 0]
    elif t is list:
        return vectorise(sign_of, item)
    else:
        return item
def strip_non_alphabet(name):
    stripped = filter(lambda char: char in string.ascii_letters, name)
    return "".join(stripped)
def subtract(lhs, rhs):
    types = VY_type(lhs), VY_type(rhs)

    return {
        (Number, Number): lambda: lhs - rhs,
        (str, str): lambda: lhs.replace(rhs, ""),
        (str, Number): lambda: lhs.replace(str(rhs), ""),
        (Number, str): lambda: str(lhs).replace(rhs, ""),
        (list, types[1]): lambda: [subtract(item, rhs) for item in lhs],
        (types[0], list): lambda: [subtract(lhs, item) for item in rhs],
        (list, list): lambda: list(map(lambda x: subtract(*x), zip(lhs, rhs)))
    }[types]()
def summate(vector):
    vector = iterable(vector)
    ret = 0
    if type(vector[0]) is str: ret = ""

    for item in vector:
        ret = add(ret, item)

    return ret
def sums(vector):
    ret = []
    for i in range(len(item)):
        ret.append(summate(item[0:i+1]))
    return ret
tab = lambda string: NEWLINE.join(["    " + item for item in string.split(NEWLINE)]).rstrip("    ")
def transilterate(original, new, string):
    ret = ""
    for char in string:
        ind = original.find(char)
        if ind != -1:
            ret += new[ind]
        else:
            ret += char
    return ret
def truthy_indexes(vector):
    ret = []
    for i in range(len(vector)):
        if bool(vector[i]):
            ret.append(i)
    return ret
def uninterleave(item):
    left, right = [], []
    for i in range(len(item)):
        if i % 2: left.append(item[i])
        else: right.append(item[i])
    return [left, right]
uniquify = lambda item: list(dict.fromkeys(iterable(item)))
def vectorise(fn, left, right=None):
    left = iterable(left)
    t_left = type(left)
    if right:
        if t_left is Generator:
            return left._map(lambda x: fn(x, right))
        return [fn(x, right) for x in left]
    else:
        if t_left is Generator:
            return left._map(fn)
        return list(map(fn, left))
def vectorising_equals(lhs, rhs):
    return all(map(lambda x: x[0] == x[1], zip(iterable(lhs), iterable(rhs))))
def vertical_join(vector, padding=" "):
    lengths = list(map(len, iterable))
    iterable = [padding * (max(lengths) - len(x)) + x for x in iterable]

    out = ""
    for i in range(max(lengths)):
        for item in iterable:
            out += item[i]
        out += "\n"

    return out
def VY_bin(item):
    t_item = VY_type(item)
    return {
        Number: lambda: bin(int(item))[2:],
        str: lambda: [bin(ord(let))[2:] for let in item]
    }.get(t_item, lambda: vectorise(VY_bin, item))()
def VY_eval(item):
    try:
        return eval(item)
    except:
        return item
def VY_filter(fn, vector):
    t_vector = VY_type(vector)
    if t_vector == Number:
        vector = range(MAP_START, int(vector) + MAP_OFFSET)
    elif t_vector is Generator:
        return vector._filter(fn)
    else:
        ret = []
        for item in vector:
            val = fn(item)
            if bool(val):
                ret.append(item)

        return ret
def VY_int(item, base=10):
    t_item = type(item)
    if t_item not in [str, float, int]:
        ret = 0
        for element in item:
            ret = multiply(ret, base)
            ret = add(ret, element)
        return ret
    elif t_item is not str:
        return int(item)
    else:
        return int(item, base)
def VY_map(fn, vector):
    ret = []
    t_vector = VY_type(vector)
    if t_vector == Number:
        vector = range(MAP_START, int(vector) + MAP_OFFSET)
    if t_vector is Generator:
        temp = Generator(vector)
        return vector._map(fn)
    for item in vector:
        result = fn([item])
        ret.append(result[-1])
    return ret
def VY_print(item, end="\n"):
    t_item = type(item)
    if t_item is Generator:
        item._print()
    elif t_item is list:
        print(VY_repr(item))
    else: print(item)
def VY_sorted(vector, fn=None):
    if fn:
        return Generator(sorted(vector, key=fn))
    else:
        return Generator(sorted(vector))
def VY_range(item, start=0, lift_factor=0):
    t_item = VY_type(item)
    if t_item == Number:
        return range(start, int(item) + lift_factor)
    return item
def VY_reduce(fn, vector):
    t_type = VY_type(vector)
    if t_type is Generator: return Generator(vector)._reduce(fn)
    if t_type is Number:
        vector = range(MAP_START, int(vector) + MAP_OFFSET)
    vector = vector[::-1]
    working_value = pop(vector)
    vector = vector[::-1]

    for item in vector:
        working_value = fn([working_value, item], arity=2)[-1]

    return working_value
def VY_repr(item):
    t_item = VY_type(item)
    return {
        Number: lambda x: x,
        list: lambda x: "⟨" + "|".join([str(VY_repr(y)) for y in x]) + "⟩",
        Generator: lambda x: VY_repr(x._dereference()),
        str: lambda x: "`" + x + "`"
    }[t_item](item)
def VY_type(item):
    ty = type(item)
    if ty in [int, float]:
        return Number
    return ty
def VY_zipmap(fn, vector):
    if type(vector) in [int, float]:
        vector = range(MAP_START, int(vector) + MAP_OFFSET)

    ret = []
    for item in vector:
        ret.append([item, fn(item)[-1]])

    return ret

def VY_compile(source, header=""):
    if not source: return header or "pass"

    tokenifed_source = VyParse.Tokenise(source)
    compiled = ""

    for token in tokenifed_source:
        NAME, VALUE = token[VyParse.NAME], token[VyParse.VALUE]

        if NAME == VyParse.NO_STMT:
            compiled += commands.command_dict.get(VALUE, "")

        elif NAME == VyParse.INTEGER:
            compiled += f"stack.append({VALUE})"
        elif NAME == VyParse.STRING_STMT:
            string = VALUE[VyParse.STRING_CONTENTS].replace('"', "\\\"")
            compiled += f"stack.append(\"{utilities.uncompress(string)}\")" + NEWLINE
        elif NAME == VyParse.CHARACTER:
            compiled += f"stack.append({repr(VALUE[0])})"
        elif NAME == VyParse.IF_STMT:
            true_branch = VALUE[VyParse.IF_ON_TRUE]
            true_branch = tab(VY_compile(true_branch))

            compiled += "_IF_condition = bool(pop(stack))" + NEWLINE
            compiled += "if _IF_condition:" + NEWLINE + true_branch

            if VyParse.IF_ON_FALSE in VALUE:
                false_branch = VALUE[VyParse.IF_ON_FALSE]
                false_branch = tab(VY_compile(false_branch))
                compiled += NEWLINE + "else:" + NEWLINE
                compiled += false_branch
        elif NAME == VyParse.FOR_STMT:
            loop_variable = "LOOP_" + str(int(time.time()))
            if VyParse.FOR_VARIABLE in VALUE:
                loop_variable = "VAR_" + strip_non_alphabet(VALUE[VyParse.FOR_VARIABLE])

            compiled += "for " + loop_variable + " in VY_range(pop(stack)):" + NEWLINE
            compiled += tab("context_level += 1") + NEWLINE
            compiled += tab("context_values.append(" + loop_variable + ")") + NEWLINE
            compiled += tab(VY_compile(VALUE[VyParse.FOR_BODY])) + NEWLINE
            compiled += tab("context_level -= 1") + NEWLINE
            compiled += tab("context_values.pop()")
        elif NAME == VyParse.WHILE_STMT:
            condition = "stack.append(1)"
            if VyParse.WHILE_CONDITION in VALUE:
                condition = VY_compile(VALUE[VyParse.WHILE_CONDITION])

            compiled += condition + NEWLINE
            compiled += "while pop(stack):" + NEWLINE
            compiled += tab(VY_compile(VALUE[VyParse.WHILE_BODY])) + NEWLINE
            compiled += tab(condition)
        elif NAME == VyParse.FUNCTION_STMT:
            if VyParse.FUNCTION_BODY not in VALUE:
                # Function call
                compiled += "stack += FN_" + VALUE[VyParse.FUNCTION_NAME] + "(stack)"
            else:
                function_information = VALUE[VyParse.FUNCTION_NAME].split(":")
                # This will either be a single name, or name and parameter information

                parameter_count = 0
                function_name = function_information[0]
                parameters = []

                if len(function_information) >= 2:
                    for parameter in function_information[1:]:
                        if parameter == "*":
                            # Variadic parameters
                            parameters.append(-1)
                        elif parameter.isnumeric():
                            # Fixed arity
                            parameters.append(int(parameter))
                            parameter_count += parameters[-1]
                        else:
                            # Named parameter
                            parameters.append(parameter)
                            parameter_count += 1

                compiled += "def FN_" + function_name + "(parameter_stack):" + NEWLINE
                compiled += tab(NEWLINE.join(map(lambda x: "global " + x,
                 ["context_level", "context_values"]))) + NEWLINE

                # Yes I did get lazy and decide it better to write a lambda instead of having two seperate lines. Also, why isn't softwrap working like it supposed to?

                # ^ I didn't have it toggled in Atom. Please ignore the implications of the above comment.

                compiled += tab("context_level += 1") + NEWLINE
                if parameter_count == 1:
                    # There's only one parameter, so instead of pushing it as a list
                    # (which is kinda rather inconvienient), push it as a "scalar"

                    compiled += tab("context_values.append(parameter_stack[-1])")
                elif parameter_count != -1:
                    compiled += tab(f"context_values.append(parameter_stack[:-{parameter_count}])")
                else:
                    compiled += tab("context_values.append(parameter_stack)")

                compiled += NEWLINE

                compiled += tab("parameters = []") + NEWLINE

                for parameter in parameters:
                    if parameter == -1:
                        compiled += tab("""arity = pop(parameter_stack)
if VY_type(arity) == NUMBER:
    parameters += parameter_stack[:-int(arity)]
else:
    parameters += [arity]
""")
                    elif parameter == 1:
                        compiled += tab("parameters.append(pop(parameter_stack))")
                    elif isintance(parameter, int):
                        compiled += tab(f"parameters += pop(parameter_stack, {parameter})")
                    else:
                        compiled += tab("VAR_" + parameter + " = pop(parameter_stack)")
                    compiled += NEWLINE

                compiled += tab("stack = parameters[::]") + NEWLINE
                compiled += tab(VY_compile(VALUE[VyParse.FUNCTION_BODY])) + NEWLINE
                compiled += tab("context_level -= 1; context_values.pop()") + NEWLINE
                compiled += tab("return stack")
        elif NAME == VyParse.LAMBDA_STMT:
            defined_arity = 1
            if VyParse.LAMBDA_ARGUMENTS in VALUE:
                lambda_argument = VALUE[VyParse.LAMBDA_ARGUMENTS]
                if lambda_argument.isnumeric():
                    defined_arity = int(lambda_argument)

            compiled += "def _lambda(parameter_stack, arity=None):" + NEWLINE
            compiled += tab(NEWLINE.join(map(lambda x: "global " + x,
             ["context_level", "context_values"]))) + NEWLINE

            compiled += tab("context_level += 1") + NEWLINE
            compiled += tab(f"if arity < {defined_arity}: parameters = pop(parameter_stack, arity)") + NEWLINE
            compiled += tab(f"else: parameters = pop(parameter_stack, {defined_arity}, True)") + NEWLINE
            compiled += tab("stack = parameters[::]") + NEWLINE
            compiled += tab("context_values.append(parameters)") + NEWLINE
            compiled += tab(VY_compile(VALUE[VyParse.LAMBDA_BODY])) + NEWLINE
            compiled += tab("context_level -= 1; context_values.pop()") + NEWLINE
            compiled += tab("return stack[-1]") + NEWLINE
            compiled += "stack.append(_lambda)"
        elif NAME == VyParse.LIST_STMT:
            compiled += "temp_list = []" + NEWLINE
            for element in VALUE[VyParse.LIST_ITEMS]:
                compiled += "def list_item(parameter_stack):" + NEWLINE
                compiled += tab("stack = parameter_stack[::]") + NEWLINE
                compiled += tab(VY_compile(element)) + NEWLINE
                compiled += tab("return pop(stack)") + NEWLINE
                compiled += "temp_list.append(list_item(stack))" + NEWLINE
            compiled += "stack.append(temp_list[::])"
        elif NAME == VyParse.FUNCTION_REFERENCE:
            compiled += f"stack.append(FN_{VALUE[VyParse.FUNCTION_NAME]})"
        elif NAME == VyParse.CONSTANT_CHAR:
            compiled += f"stack.append({constants[VALUE]})"
        elif NAME == VyParse.CODEPAGE_INDEX:
            compiled += f"stack.append({commands.codepage.find(VALUE)})"
        elif NAME == VyParse.TWO_BYTE_MATH:
            compiled += "# TODO: Implement the math stuff again"
        elif NAME == VyParse.SINGLE_SCC_CHAR:
            import utilities
            import encoding
            if utilities.to_ten(VALUE, encoding.compression) < len(words._words):
                compiled += f"stack.append({repr(words.extract_word(VALUE))})"
        elif NAME == VyParse.VARIABLE_SET:
            compiled += "VAR_" + VALUE[VyParse.VARIABLE_NAME] + " = pop(stack)"
        elif NAME == VyParse.VARIABLE_GET:
            compiled += "stack.append(VAR_" + VALUE[VyParse.VARIABLE_NAME] + ")"
        elif token[NAME] == VyParse.COMPRESSED_NUMBER:
            import utilities, encoding
            number = utilities.to_ten(VALUE[VyParse.COMPRESSED_NUMBER_VALUE],
             encoding.codepage_number_compress)
            compiled += f"stack.append({number})" + newline
        elif token[NAME] == VyParse.COMPRESSED_STRING:
            import utilities, encoding
            string = utilities.to_ten(VALUE[VyParse.COMPRESSED_STRING_VALUE],
             encoding.codepage_string_compress)
            string = utilities.from_ten(string, utilities.base53alphabet)
            compiled += f"stack.append('{string}')" + newline
        compiled += NEWLINE
    return header + compiled

if __name__ == "__main__":
    import sys

    file_location = ""
    flags = ""
    inputs = []
    header = "stack = []\nregister = 0\nprinted = False\n"

    if len(sys.argv) > 1:
        file_location = sys.argv[1]
    if len(sys.argv) > 2:
        flags = sys.argv[2]
        if flags:
            if 'f' in flags:
                inputs = list(map(VY_eval, open(sys.argv[3]).readlines()))
            else:
                inputs = list(map(VY_eval,sys.argv[3:]))

        if 'a' in flags:
            inputs = [[inputs]]

    if not file_location: #repl mode
        while 1:
            line = input(">>> ")
            context_level = 0
            line = VY_compile(line, header)
            exec(line)
            VY_print(stack)
    elif file_location == "h":
        print("\nUsage: python3 Vyxal.py <file> <flags (single string of flags)> <input(s) (if not from STDIN)>")
        print("ALL flags should be used as is (no '-' prefix)")
        print("\tj\tPrint top of stack joined by newlines")
        print("\tL\tPrint top of stack joined by newlines(Vertically)")
        print("\ts\tSum/concatenate top of stack on execution")
        print("\tM\tUse 1-indexed range [1,n] for mapping integers")
        print("\tm\tUse 0-indexed range [0,n) for mapping integers")
        print("\tv\tUse Vyxal encoding for input file")
        print("\tc\tOutput compiled code")
        print("\tf\tGet input from file instead of arguments")
        print("\ta\tTreat newline seperated values as a list")
        print("");
    else:
        if flags:
            if "M" in flags:
                MAP_START = 1

            if "m" in flags:
                MAP_OFFSET = 0

            if 'j' in flags:
                _join = True

            if 'L' in flags:
                _vertical_join = True

            if 'v' in flags:
                use_encoding = True

        # Encoding method thanks to Adnan (taken from the old 05AB1E interpreter)
        if use_encoding:
            import encoding
            code = open(file_location, "rb").read()
            code = encoding.vyxal_to_utf8(code)
        else:
            code = open(file_location, "r", encoding="utf-8").read()

        code = VY_compile(code, header)
        context_level = 0
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
                VY_print(stack.pop())
