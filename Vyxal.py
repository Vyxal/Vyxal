import VyParse
from VyParse import NAME
from VyParse import VALUE
from codepage import *
import math
import string
import hashlib

_context_level = 0
_context_values = []
_input_cycle = 0

_MAP_START = 0
_MAP_OFFSET = 1
_join = False
_vertical_join = False
_use_encoding = False

_RIGHT = "RIGHT"
_LEFT = "LEFT"

STANDARD = "standard"
LAMBDA = "lambda"

def random_choice(item):
    import random
    if type(item) is Stack:
        return random.choice(item)
    else:
        return random.choice(list(item))


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
        return lhs + rhs

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

    else:
        return lhs - rhs

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

    else:
        return lhs * rhs


def divide(lhs, rhs):
    import textwrap
    ts = types(lhs, rhs)

    if ts == [Number, Number]:
        if (lhs / rhs) == int(lhs / rhs):
            return int(lhs / rhs)
        else:
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

    else:
        return lhs / rhs

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

    else:
        return lhs % rhs

def vectorising_equals(lhs, rhs):
    ts = types(lhs, rhs)

    if ts == [Stack, Stack]:
        if len(lhs) != len(rhs):
            if len(lhs) < len(rhs):
                lhs.extend([0] * len(rhs) - len(lhs))
            else:
                rhs.extend([0] * len(lhs) - len(rhs))

        for n in range(len(lhs)):
            lhs[n] = int(lhs[n] == rhs[n])
        return lhs

    elif ts[-1] is Stack:
        for n in range(len(rhs)):
            rhs[n] = int(lhs == rhs[n])
        return rhs

    elif ts[0] is Stack:
        for n in range(len(lhs)):
            lhs[n] = int(lhs[n] == rhs)
        return lhs

    else:
        return int(lhs == rhs)

def join(lhs, rhs):
    ts = types(lhs, rhs)
    if ts[0] == Stack:
        if ts[-1] == Stack:
            return lhs + rhs
        lhs.push(rhs)
        return lhs

    elif ts[-1] == Stack:
        rhs.contents.insert(0, lhs)
        return rhs

    else:
        return str(lhs) + str(rhs)


def cumulative_sum(item):
    sums = Stack()
    for i in range(len(item)):
        sums.push(summate(item[:i + 1]))

    return sums

def counts(item):
    ret = []
    for i in set(as_iter(item)):
        ret.append(Stack([i, as_iter(item).count(i)]))

    return Stack(ret)


def lshift(lhs, rhs):
    ts = types(lhs, rhs)
    if ts[0] == Stack:
        if ts[-1] == Stack:
            if len(lhs) != len(rhs):
                if len(lhs) < len(rhs):
                    lhs.extend([0] * len(rhs) - len(lhs))
                else:
                    rhs.extend([0] * len(lhs) - len(rhs))

            for n in range(len(lhs)):
                lhs[n] = lshift(lhs[n], rhs[n])
            return lhs

        else:
            lhs = Stack([lshift(x, rhs) for x in lhs])
            return lhs
    elif ts[-1] == Stack:
        rhs = Stack([lshift(lhs, x) for x in rhs])
        return rhs

    elif ts[0] == str:
        return ''.join([chr(ord(x) << rhs) for x in lhs])

    else:
        return lhs << rhs

def rshift(lhs, rhs):
    ts = types(lhs, rhs)
    if ts[0] == Stack:
        if ts[-1] == Stack:
            if len(lhs) != len(rhs):
                if len(lhs) < len(rhs):
                    lhs.extend([0] * len(rhs) - len(lhs))
                else:
                    rhs.extend([0] * len(lhs) - len(rhs))

            for n in range(len(lhs)):
                lhs[n] = rshift(lhs[n], rhs[n])
            return lhs

        else:
            lhs = Stack([rshift(x, rhs) for x in lhs])
            return lhs
    elif ts[-1] == Stack:
        rhs = Stack([rshift(lhs, x) for x in rhs])
        return rhs

    elif ts[0] == str:
        return ''.join([chr(ord(x) >> rhs) for x in lhs])

    else:
        return lhs >> rhs

def bit_and(lhs, rhs):
    ts = types(lhs, rhs)
    if ts[0] == Stack:
        if ts[-1] == Stack:
            if len(lhs) != len(rhs):
                if len(lhs) < len(rhs):
                    lhs.extend([0] * len(rhs) - len(lhs))
                else:
                    rhs.extend([0] * len(lhs) - len(rhs))

            for n in range(len(lhs)):
                lhs[n] = bit_and(lhs[n], rhs[n])
            return lhs

        else:
            lhs = Stack([bit_and(x, rhs) for x in lhs])
            return lhs
    elif ts[-1] == Stack:
        rhs = Stack([bit_and(lhs, x) for x in rhs])
        return rhs

    elif ts[0] == ts[-1] == str:
        out = ""
        for i in range(min(len(lhs), len(rhs))):
            out += chr(ord(lhs[i]) & ord(rhs[i]))
        return out

    elif ts[0] == ts[-1] == Number:
        return lhs & rhs

    else:
        return bit_and(str(lhs), str(rhs))

def bit_or(lhs, rhs):
    ts = types(lhs, rhs)
    if ts[0] == Stack:
        if ts[-1] == Stack:
            if len(lhs) != len(rhs):
                if len(lhs) < len(rhs):
                    lhs.extend([0] * len(rhs) - len(lhs))
                else:
                    rhs.extend([0] * len(lhs) - len(rhs))

            for n in range(len(lhs)):
                lhs[n] = bit_or(lhs[n], rhs[n])
            return lhs

        else:
            lhs = Stack([bit_or(x, rhs) for x in lhs])
            return lhs
    elif ts[-1] == Stack:
        rhs = Stack([bit_or(lhs, x) for x in rhs])
        return rhs

    elif ts[0] == ts[-1] == str:
        out = ""
        for i in range(min(len(lhs), len(rhs))):
            out += chr(ord(lhs[i]) | ord(rhs[i]))
        return out

    elif ts[0] == ts[-1] == Number:
        return lhs | rhs

    else:
        return bit_or(str(lhs), str(rhs))

def bit_not(item):
    if type(item) is Stack:
        return Stack([bit_not(x) for x in item])

    elif type(item) is str:
        return Stack([~ord(x) for x in item])

    else:
        return ~item

def bit_xor(lhs, rhs):
    ts = types(lhs, rhs)
    if ts[0] == Stack:
        if ts[-1] == Stack:
            if len(lhs) != len(rhs):
                if len(lhs) < len(rhs):
                    lhs.extend([0] * len(rhs) - len(lhs))
                else:
                    rhs.extend([0] * len(lhs) - len(rhs))

            for n in range(len(lhs)):
                lhs[n] = bit_xor(lhs[n], rhs[n])
            return lhs

        else:
            lhs = Stack([bit_xor(x, rhs) for x in lhs])
            return lhs
    elif ts[-1] == Stack:
        rhs = Stack([bit_xor(lhs, x) for x in rhs])
        return rhs

    elif ts[0] == ts[-1] == str:
        out = ""
        for i in range(min(len(lhs), len(rhs))):
            out += chr(ord(lhs[i]) ^ ord(rhs[i]))
        return out

    elif ts[0] == ts[-1] == Number:
        return lhs ^ rhs

    else:
        return bit_or(str(lhs), str(rhs))


def prepend(iterable, item):
    if type(iterable) is Stack:
        iterable.contents.insert(0, item)
        return iterable

    elif type(iterable) is str:
        return str(item) + iterable

def inserted(iterable, index, item):
    if type(iterable) is Stack:
        iterable.contents.insert(index, item)
        return iterable

    else:
        return iterable[:index] + str(item) + iterable[index:]

class Number: pass
class Stack(list):
    def __init__(self, prelist=None, inputs=[]):
        if prelist is not None:
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
        top = self.pop();
        topnt = self.pop();
        self.push(top)
        self.push(topnt)
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
        if type(n) is slice:
            return Stack(self.contents[n])

        elif type(n) is Stack:
            length = len(n)
            if length < 1:
                return 0

            elif length == 1:
                return Stack(self.contents[slice(n[0])])

            elif length == 2:
                return Stack(self.contents[slice(n[0], n[1], None)])

            else:
                return Stack(self.contents[slice(n[0], n[1], n[2])])


        elif type(n) is str:
            return 0 #for now

        elif type(n) is float:
            return self.contents[int(n)]

        else:
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
        self.contents += rhs
        return self

    def __ladd__(self, lhs):
        self.contents = lhs + self.contents
        return self

    def __mul__(self, rhs):
        self.contents *= rhs
        return self

    def __lmul__(self, lhs):
        self.contents = lhs * self.contents
        return self

    def __iter__(self):
        return iter(self.contents)

    def __contains__(self, item):
        return int(item in self.contents)

    def index(self, item, start=0):
        return self.contents.index(item, start)

    def do_map(self, fn):
        temp = []
        obj = self.pop()
        if type(obj) in [int, float]:
            obj = list(range(_MAP_START, int(obj) + _MAP_OFFSET))
        for item in obj:
            temp.append(fn(Stack(item, item))[-1])
        self.contents.append(Stack(temp))


    def do_zipmap(self, fn):
        temp = []
        obj = self.pop()
        if type(obj) in [int, float]:
            obj = list(range(_MAP_START, int(obj) + _MAP_OFFSET))
        for item in obj:
            temp.append(Stack([item, fn(Stack(item, item))][-1]))
        self.contents.append(Stack(temp))

    def do_filter(self, fn):
        temp = []
        obj = self.contents.pop()
        if type(obj) in [int, float]:
            obj = list(range(_MAP_START, int(obj) + _MAP_OFFSET))

        for item in obj:
            x = fn(Stack(item))
            if type(x) is Stack: x = x[-1]
            if bool(x):
                temp.append(item)

        self.contents.append(Stack(temp))


    def do_fixed_gen(self, fn, n=1):
        temp = []
        curr = 0
        while len(temp) < n:
            result = fn(Stack(curr))
            if result:
                temp.append(curr)
            curr += 1

        self.contents.append(Stack(temp))

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

    def count(self, item):
        return self.contents.count(item)

class Infinite_List:
    def __init__(self, function, init=0):
        self.index = init
        self.generated = Stack()
        self.functions = [function]

    def __next__(self):
        value = self.index
        for function in self.functions:
            value = function(value)
        self.generated.push(value)
        self.index += 1
        return self.generated[-1]

    def __getitem__(self, index):
        if index < len(self.generated):
            return self.generated[index]
        else:
            while self.index < index:
                self.__next__()

            return self.__next__()

    def __add__(self, rhs):
        self.functions.append(lambda x: x + rhs)
        self.generated = add(self.generated, rhs)
        return self

    def __mult__(self, rhs):
        self.functions.append(lambda x: x * rhs)
        self.generated = multiply(self.generated, rhs)
        return self

    def __sub__(self, rhs):
        self.functions.append(lambda x: x - rhs)
        self.generated = subtract(self.generated, rhs)
        return self

    def __div__(self, rhs):
        self.functions.append(lambda x: x / rhs)
        self.generated = divide(self.generated, rhs)
        return self

    def __str__(self):
        print("⟨", end="")
        for item in self.generated:
            print(item, end="|")

        while True:
            print(self.__next__(), end="|")

        print("⟩")  # This line will never run but it's for the sake of completion

    def __repr__(self):
        return str(self)

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

def Vy_Uniquify(iterable):
    all_items = []
    seen_items = set()

    for item in iterable:
        if item in seen_items:
            continue

        all_items.append(item)
        seen_items.add(item)

    return Stack(all_items)

def sort_unique(iterable):
    return VySort(Vy_Uniquify(iterable))

def Vy_zip(lhs, rhs):
    if len(lhs) != len(rhs):
        if len(lhs) < len(rhs):
            lhs.extend([0] * len(rhs) - len(lhs))
        else:
            rhs.extend([0] * len(lhs) - len(rhs))

def as_iter(item, t=Stack):
    if type(item) in [int, float]:
        if t is Stack:
            return Stack([int(x) if x != "." else x for x in str(item)])
        return t(item)
    else:
        return item

def try_cast(item, t):
    try: return t(item)
    except: return item

def Vy_reduce(fn, iterable):
    if type(iterable) is not Stack: iterable = Stack(list(as_iter(iterable)))
    iterable.reverse()
    value = iterable.pop()
    iterable.reverse()
    for n in iterable.contents:
        value = fn(Stack([value, n]), arity=2)[0]
    return Stack(value)


def Vy_int(item, base=10):
    if type(item) is Stack:
        result = 0
        for value in item:
            result = multiply(result, base)
            result = add(result, value)

        return result
    elif type(item) in [int, float]:
        return int(item)
    else:
        return int(item, base)

def graded(iterable):
    iterable = sorted(enumerate(iterable), key=lambda x: x[-1])
    return Stack([x[0] for x in iterable])

def chrord(item):
    if type(item) in [int, float]:
        return chr(int(item))

    elif type(item) is str and len(item) == 1:
        return ord(item)

    else:
        return Stack([chrord(x) for x in item])

def deref(item):
    if type(item) is Stack:
        return Stack(item.contents.copy())
    else:
        return item

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
    if type(item) is int:
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
        return Stack(list(range(a, b + lift_factor)))
    else:
        return Stack(list(range(b, a + lift_factor)))

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

def repeat(iterable, times):
    if type(iterable) is Stack:
        return Stack(iterable.contents * times)
    else:
        return iterable * times

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
    if type(x[0]) is str: result = ""
    else: result = 0
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


def indexes_where(fn, iterable):
    indexes = Stack()
    for i in range(len(iterable)):
        if bool(fn(Stack(item[i]))):
            indexes.push(i)
    return indexes

def VySort(iterable, key=None):
    if key:
        iterable = [(x, key(x)) for x in iterable]
        return Stack([m[0] for m in sorted(iterable, key=lambda x:x[-1])])
    else:
        if type(iterable) is int:
            return int("".join(sorted(str(iterable))))
        elif type(iterable) in [str, float]:
            return "".join(sorted(str(iterable)))
        else:
            return Stack(sorted(iterable))

def interleave(lhs, rhs):
    out = Stack() if Stack in types(lhs, rhs) else ""
    t = type(out)
    i = 0
    for i in range(min(len(lhs), len(rhs))):
        if t is Stack:
            out.push(lhs[i])
            out.push(rhs[i])

        else:
            out += str(lhs[i])
            out += str(rhs[i])

    if len(lhs) < len(rhs):
        out += t(rhs[i:])
    elif len(lhs) > len(rhs):
        out += t(lhs[i:])

    return out

def uninterleave(item):
    lhs, rhs = type(item)(), type(item)()
    for i in range(len(item)):
        if i % 2 == 0:
            lhs += type(item)(item[i])
        else:
            rhs += type(item)(item[i])

    return [lhs, rhs]


newline = "\n"
tab = lambda x: newline.join(["    " + m for m in x.split(newline)]).rstrip("    ")


def VyCompile(source, header=""):
    if not source:
        if header:
            return header
        return "pass"
    tokens = VyParse.Tokenise(source)
    compiled = ""
    for token in tokens:
        # print(token[NAME], token[VALUE], compiled)
        if token[NAME] == VyParse.NO_STMT and token[VALUE] in commands:
            compiled += commands[token[VALUE]] + newline

        else:
            if token[NAME] == VyParse.INTEGER:
                compiled += f"stack.push({token[VALUE]})" + newline

            elif token[NAME] == VyParse.STRING_STMT:
                string = token[VALUE][VyParse.STRING_CONTENTS]
                string = string.replace('"', "\\\"")
                import utilities
                string = utilities.uncompress(string)
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
                loopvar = str(hashlib.md5(bytes(compiled, "UTF-8")).hexdigest())
                if not VyParse.FOR_VARIABLE in token[VALUE]:
                    var_name = "_CONTEXT_" + loopvar
                else:
                    var_name = strip_non_alphabet(token\
                                                  [VALUE][VyParse.FOR_VARIABLE])


                compiled += f"for VAR_{var_name} in smart_range(stack.pop()):" + newline
                compiled += tab("_context_level += 1") + newline
                compiled += tab("if len(_context_values) + 1 == _context_level - 1:") + newline
                compiled += tab(\
                tab("_context_values[(_context_level - 1) % (len(_context_values))] = VAR_" + var_name)) + newline
                compiled += tab("else:") + newline
                compiled += tab(\
                tab("_context_values.append(VAR_" + var_name + ")")) + newline
                compiled += tab(VyCompile(token[VALUE][VyParse.FOR_BODY])) + newline
                compiled += tab("_context_level -= 1") + newline
                compiled += tab("_context_values.pop()") + newline

            elif token[NAME] == VyParse.WHILE_STMT:


                if not VyParse.WHILE_CONDITION in token[VALUE]:
                    condition = "stack.push(1)"
                else:
                    condition = VyCompile(token[VALUE][VyParse.WHILE_CONDITION])

                compiled += f"{condition}" + newline
                compiled += tab("if len(_context_values) + 1 == _context_level - 1:") + newline
                compiled += tab(\
                tab("_context_values[(_context_level - 1) % (len(_context_values))] = VAR_" + var_name)) + newline
                compiled += tab("else:") + newline
                compiled += tab(\
                tab("_context_values.append(VAR_" + var_name + ")")) + newline
                compiled += "while stack.pop():" + newline
                compiled += tab("_context_level += 1")
                compiled += tab(VyCompile(token[VALUE][VyParse.WHILE_BODY])) + newline
                compiled += tab(condition) + newline
                compiled += tab("if len(_context_values) + 1 == _context_level - 1:") + newline
                compiled += tab(\
                tab("_context_values[(_context_level - 1) % (len(_context_values))] = VAR_" + var_name)) + newline
                compiled += tab("else:") + newline
                compiled += tab(\
                tab("_context_values.append(VAR_" + var_name + ")")) + newline
                compiled += tab("_context_level -= 1") + newline

            elif token[NAME] == VyParse.FUNCTION_STMT:
                if VyParse.FUNCTION_BODY not in token[VALUE]:
                    compiled += f"res = FN_{token[VALUE][VyParse.FUNCTION_NAME]}(stack); stack += res" + newline
                else:
                    function_data = token[VALUE][VyParse.FUNCTION_NAME].split(":")
                    number_of_parameters = 0
                    name = function_data[0]
                    arguments = []
                    if len(function_data) >= 2:
                        for argument in function_data[1:]:
                            if argument == "*":
                                arguments.append(-1)
                            elif argument.isnumeric():
                                arguments.append(int(argument))
                                number_of_parameters += arguments[-1]
                            else:
                                arguments.append(argument)
                                number_of_parameters += 1

                    compiled += f"def FN_{name}(in_stack, arity=None):" + newline
                    compiled += tab("global VY_reg_reps") + newline
                    compiled += tab("global _context_level") + newline
                    compiled += tab("global _context_values") + newline
                    compiled += tab("_context_level += 1") + newline
                    if number_of_parameters == 1:
                        compiled += tab("_context_values.append(in_stack[-1])") + newline
                    else:
                        compiled += tab(f"_context_values.append(Stack(in_stack[:-{number_of_parameters}]))") + newline
                    compiled += tab("args = []") + newline
                    for arg in arguments:
                        if type(arg) is int:
                            if arg == -1:
                                compiled += tab("arity = in_stack.pop()") + newline
                                compiled += tab("if type(arity) is Stack:") + newline
                                compiled += tab(tab("args = arity")) + newline
                                compiled += tab("elif type(arity) is int:") + newline
                                compiled += tab(tab("args = in_stack.pop(arity)")) + newline
                                compiled += tab("else:") + newline
                                compiled += tab(tab("args = Stack(arity, arity)")) + newline
                            elif arg == 1:
                                compiled += tab("args += [in_stack.pop()]") + newline
                            else:
                                compiled += tab(f"args += in_stack.pop({arg})") + newline
                        else:
                            compiled += tab(f"VAR_{arg} = in_stack.pop()") + newline
                    compiled += tab(f"stack = Stack(args, args);") + newline
                    x = VyCompile(token[VALUE][VyParse.FUNCTION_BODY])
                    compiled += tab(x) + newline
                    compiled += tab("_context_level -= 1") + newline
                    compiled += tab("_context_values.pop()") + newline
                    compiled += tab("return stack") + newline

            elif token[NAME] == VyParse.LAMBDA_STMT:
                args = 1
                if VyParse.LAMBDA_ARGUMENTS in token[VALUE]:
                    if token[VALUE][VyParse.LAMBDA_ARGUMENTS].isnumeric():
                        args = int(token[VALUE][VyParse.LAMBDA_ARGUMENTS])
                compiled += "def _lambda(in_stack, arity=None):" + newline
                compiled += tab("global VY_reg_reps") + newline
                compiled += tab("global _context_level") + newline
                compiled += tab("global _context_values") + newline
                compiled += tab("_context_level += 1") + newline
                compiled += tab(f"if arity and arity > {args}: args = in_stack.pop(arity)") + newline
                compiled += tab(f"else: args = in_stack.pop({args})") + newline
                compiled += tab("stack = Stack(args, args)") + newline
                compiled += tab(f"_context_values.append(args)") + newline
                compiled += tab(VyCompile(token[VALUE][VyParse.LAMBDA_BODY])) + newline
                compiled += tab("_context_values.pop()") + newline
                compiled += tab("return Stack(stack[-1])") + newline
                compiled += "stack.push(_lambda)" + newline

            elif token[NAME] == VyParse.LIST_STMT:
                compiled += "_temp_list = []" + newline

                for item in token[VALUE][VyParse.LIST_ITEMS]:
                    compiled += "def list_item(in_stack):" + newline
                    compiled += tab("stack = in_stack") + newline
                    compiled += tab(VyCompile(item)) + newline
                    compiled += tab("return stack.pop()") + newline
                    compiled += "_temp_list.append(list_item(stack))" + newline
                compiled += "_temp_list = Stack(_temp_list)" + newline
                compiled += "stack.push(_temp_list)" + newline


            elif token[NAME] == VyParse.FUNCTION_REFERENCE:
                compiled += f"stack.push(FN_{token[VALUE][VyParse.FUNCTION_NAME]})" + newline

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

            elif token[NAME] == VyParse.COMPRESSED_NUMBER:
                import utilities, encoding
                number = utilities.to_ten(token[VALUE][VyParse.COMPRESSED_NUMBER_VALUE], encoding.codepage_number_compress)
                compiled += f"stack.push({number})" + newline

            elif token[NAME] == VyParse.COMPRESSED_STRING:
                import utilities, encoding
                string = utilities.to_ten(token[VALUE][VyParse.COMPRESSED_STRING_VALUE], encoding.codepage_string_compress)
                string = utilities.from_ten(string, utilities.base53alphabet)
                compiled += f"stack.push('{string}')" + newline
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
            exec(line)
            print(stack)
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
        print("");
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
        _context_level = 0
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
