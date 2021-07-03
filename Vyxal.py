from VyParse import *
from commands import *
import encoding
import utilities

import random
import regex
import secrets
import string
import sympy
import sys
import types

import pwn

context_level = 0
context_values = [0]
global_stack = []
input_level = 0
inputs = []
input_values = {0: [inputs, 0]} # input_level: [source, input_index]
last_popped = []
keg_mode = False
number_iterable = list
raw_strings = False
online_version = False
output = ""
printed = False
register = 0
retaIn_items = False
reverse_args = False
safe_mode = False # You may want to have safe evaluation but not be online.
stack = []
this_function = lambda x: VY_print(stack) or x

MAP_START = 1
MAP_OFFSET = 1

newline = "\n"
number = "number"
class LazyList():
    def __call__(self, *args, **kwargs):
        return self
    def __contains__(self, lhs):
        if self.infinite:
            if len(self.generated):
                last = self.generated[-1]
            else:
                last = 0 
        
            while last <= lhs:
                last = next(self)
                if last == lhs:
                    return 1
            return 0
        else:
            for temp in self:
                if temp == lhs: return 1
            return 0
    def __getitem__(self, position):
        if isinstance(position, slice):
            start, stop, step = position.start or 0, position.stop, position.step or 1
            if stop is None:
                @LazyList
                def infinite_index():
                    if len(self.generated):
                        for lhs in self.generated[position::step]: yield lhs
                        temp = next(self)
                        while temp:
                            yield temp; temp = next(self)
                return infinite_index()
            else:
                ret = []
                for i in range(start, stop, step):
                    ret.append(self.__getlhs__(i))
                return ret
        else:
            if position < 0: self.generated += list(self); return self.generated[position]
            elif position < len(self.generated): return self.generated[position]
            else:
                while len(self.generated) < position + 1:
                    try: self.__next__()
                    except: break
                return self.generated[position % len(self.generated)]
    def __init__(self, source, isinf=False):
        self.raw_object = source
        if isinstance(self.raw_object, types.FunctionType): self.raw_object = self.raw_object()
        elif not isinstance(self.raw_object, types.GeneratorType): self.raw_object = iter(self.raw_object)
        self.generated = []
        self.infinite = isinf
    def __iter__(self):
        return self
    def __len__(self):
        return len(self.listify())
    def __next__(self):
        lhs = next(self.raw_object)
        self.generated.append(lhs)
        return lhs
    def listify(self):
        temp = self.generated + list(self.raw_object)
        self.raw_object = iter(temp[::])
        self.generated = []
        return temp
    def output(self, end='\n'):
        VY_print("⟨", end="")
        for lhs in self.generated[:-1]:
            VY_print(lhs, end="|")
        if len(self.generated): print(self.generated[-1], end="")

        try:
            lhs = self.__next__()
            if len(self.generated) > 1: print("|", end="")
            while True:
                VY_print(lhs, end="")
                lhs = self.__next__()
                VY_print("|", end="")
        except:
            VY_print("⟩", end=end)
    def safe(self):
        import copy
        return copy.deepcopy(self)
def add(lhs, rhs):
    return {
        (number, number): lambda: lhs + rhs,
        (str, str): lambda: lhs + rhs,
        (str, number): lambda: str(lhs) + str(rhs),
        (number, str): lambda: str(lhs) + str(rhs)
    }.get(VY_type(lhs, rhs), lambda: vectorise(add, lhs, rhs))()
def apply(function, *args):
    if function.__name__.startswith("_lambda"):
        ret = function(list(args), len(args), function)
        if len(ret): return ret[-1]
        else: return []
    elif function.__name__.startswith("FN_"):
        ret = function(list(args))[-1]
        if len(ret): return ret[-1]
        else: return []
    return function(*args)
def chrord(lhs):
    return {
        number: lambda: chr(lhs),
        str: lambda: ord(lhs) if len(lhs) == 1 else vectorise(chrord, list(lhs))
    }.get(VY_type(lhs), lambda: vectorise(chrord, lhs))()
def copy_sign(lhs, rhs):
    return {
        (number, number): lambda: [-lhs, lhs][rhs >= 0],
        (number, str): lambda: [rhs[:lhs], rhs[lhs:]],
        (str, number): lambda: [lhs[:rhs], lhs[rhs:]],
        (str, str): lambda: lhs.split(rhs, 1),
        (number, types.FunctionType): lambda: LazyList(first_n_integers(rhs, lhs)),
        (types.FunctionType, number): lambda: LazyList(first_n_integers(lhs, rhs)),
    }.get(VY_type(lhs, rhs), lambda: vectorise(copy_sign, lhs, rhs))()
def cumulative_reduce(lhs, rhs):
    function = vector = None
    if isinstance(rhs, types.FunctionType):
        function, vector = rhs, iterable(lhs, range)
    else:
        function, vector = lhs, iterable(rhs, range)

    @LazyList
    def f():
        for prefix in prefixes(vector):
            yield VY_reduce(prefix, function)[0]
    return f()
def deref(lhs, listify=True):
    if VY_type(lhs) is LazyList: return [lhs.safe, lhs.listify][listify]()
    if type(lhs) not in [int, float, str]: return list(map(deref, lhs))
    return lhs
def divide(lhs, rhs):
    return {
        (number, number): lambda: realify(sympy.Rational(lhs, rhs)),
        (number, str): lambda: wrap(rhs, len(rhs) // lhs),
        (str, number): lambda: wrap(lhs, len(lhs) // rhs),
        (str, str): lambda: lhs.split(rhs, maxsplit=1),
    }.get(VY_type(lhs, rhs), lambda: vectorise(divide, lhs, rhs))()
def divisors(lhs):
    if VY_type(lhs) == number: return sympy.divisors(int(lhs))
    else: return prefixes(lhs)
def eq(lhs, rhs):
    return {
        (number, number): lambda: int(lhs == rhs),
        (number, str): lambda: int(str(lhs) == rhs),
        (str, number): lambda: int(lhs == str(rhs)),
        (str, str): lambda: int(lhs == rhs)
    }.get(VY_type(lhs, rhs), lambda: vectorise(eq, lhs, rhs))()
def exponate(lhs, rhs):
    ts = (VY_type(lhs), VY_type(rhs))

    if ts == (str, str):
        pobj = regex.compile(lhs)
        mobj = pobj.search(rhs)
        return list(mobj.span()) if mobj else []

    elif ts == (str, number):
        factor = rhs
        if 0 < rhs < 1:
            factor = int(1 / rhs)
        return lhs[::factor]
    return {
        (number, number): lambda: lhs ** rhs,
    }.get(ts, lambda: vectorise(exponate, lhs, rhs))()
def factorial(lhs):
    return {
        number: lambda: realify(sympy.gamma(lhs)),
        str: lambda: sentence_case(lhs)
    }.get(VY_type(lhs), lambda: vectorise(factorial, lhs))()
def format_string(value, items):
    ret = ""
    index = 0
    f_index = 0

    while index < len(value):
        if value[index] == "\\":
            ret += "\\" + value[index + 1]
            index += 1
        elif value[index] == "%":
            ret += str(items[f_index % len(items)])
            f_index += 1
        else:
            ret += value[index]
        index += 1
    return ret
def first_n_integers(function, limit):
    generated = 0
    n = 1
    while generated < limit:
        temp = apply(function, n)
        
        if bool(temp):
            yield n
            generated += 1
        n += 1
def flatten(item):
    '''
    Returns a deep-flattened (all sublists expanded) version of the input
    '''
    t_item = VY_type(item)
    if isinstance(item, LazyList):
        return flatten(item.listify())
    else:
        ret = []
        for x in item:
            if type(x) in [list, LazyList]:
                ret += flatten(x)
            else:
                ret.append(x)
        return ret
def function_call(function, vector):
    if type(function) is types.FunctionType:
        return function(vector, self=function)
    else:
        return [{
            number: lambda: len(prime_factors(function)),
            str: lambda: exec(transpile(function)) or []
        }.get(VY_type(function), lambda: vectorised_not(function))()]
def get_input(predefined_level=None):
    global input_values

    level = input_level
    if predefined_level is not None:
        level = predefined_level

    if level in input_values:
        source, index = input_values[level]
    else:
        source, index = [], -1
    if source:
        ret = source[index % len(source)]
        input_values[level][1] += 1

        if keg_mode and type(ret) is str:
            return [ord(c) for c in ret]
        return ret
    else:
        try:
            temp = VY_eval(input())
            if keg_mode and type(temp) is str:
                return [ord(c) for c in temp]
            return temp
        except:
            return 0
def gt(lhs, rhs):
    return {
        (number, number): lambda: int(lhs > rhs),
        (number, str): lambda: int(str(lhs) > rhs),
        (str, number): lambda: int(lhs > str(rhs)),
        (str, str): lambda: int(lhs > rhs)
    }.get(VY_type(lhs, rhs), lambda: vectorise(gt, lhs, rhs))()
def head_extract(lhs, rhs):
    ts = VY_type(lhs, rhs)
    return {
        (ts[0], number): lambda: iterable(lhs)[0:rhs],
        (str, str): lambda: regex.compile(lhs).findall(rhs)
    }.get(ts, lambda: vectorise(head_extract, lhs, rhs))()
def interleave(lhs, rhs):
    ret = []
    for i in range(min(len(lhs), len(rhs))):
        ret.append(lhs[i])
        ret.append(rhs[i])
    if len(lhs) != len(rhs):
        if len(lhs) < len(rhs):
            # The rhs is longer
            ret += list(rhs[i + 1:])
        else:
            ret += list(lhs[i + 1:])
    if type(lhs) is str and type(rhs) is str: return "".join(ret)
    return ret
def iterable(lhs, t=None):
    t = t or number_iterable
    if VY_type(lhs) == number:
        if t is list:
            return [int(let) if let not in "-." else let for let in str(lhs)]
        if t is range:
            return LazyList(range(MAP_START, int(lhs) + MAP_OFFSET))
        return t(lhs)
    else:
        return lhs
def log(lhs, rhs):
    ts = (VY_type(lhs), VY_type(rhs))
    if ts == (str, str):
        ret = ""
        for i in range(min(len(lhs), len(rhs))):
            if rhs[i].isupper():
                ret += lhs[i].upper()
            elif rhs[i].islower():
                ret += lhs[i].lower()
            else:
                ret += lhs[i]

        if len(lhs) > len(rhs):
            ret += lhs[i + 1:]

        return ret

    return {
        (number, number): lambda: Sympy.Rational(str(math.log(lhs, rhs))),
        (str, number): lambda: "".join([c * rhs for c in lhs]),
        (number, str): lambda: "".join([c * lhs for c in rhs]),
        (list, list): lambda: mold(lhs, rhs),
        (list, LazyList): lambda: mold(lhs, list(rhs)),
        (LazyList, list): lambda: mold(list(lhs), rhs),
        (LazyList, LazyList): lambda: mold(list(lhs), list(rhs)) #There's a chance molding raw generators won't work
    }.get(ts, lambda: vectorise(log, lhs, rhs))()
def lt(lhs, rhs):
    return {
        (number, number): lambda: int(lhs < rhs),
        (number, str): lambda: int(str(lhs) < rhs),
        (str, number): lambda: int(lhs < str(rhs)),
        (str, str): lambda: int(lhs < rhs)
    }.get(VY_type(lhs, rhs), lambda: vectorise(lt, lhs, rhs))()
def map_every_n(function, lhs, index):
    @LazyList
    def f():
        for pos, element in enumerate(lhs):
            if (pos + 1) % index:
                yield element
            else:
                yield function([element])[-1]
    return f()
def merge(lhs, rhs):
    ts = VY_type(lhs, rhs)
    return {
        (ts[0], ts[1]): lambda: str(lhs) + str(rhs),
        (number, number): lambda: VY_eval(str(lhs) + str(rhs)),
        (ts[0], list): lambda: [lhs] + rhs,
        (list, ts[1]): lambda: lhs + [rhs],
        (ts[0], LazyList): lambda: [lhs] + rhs._dereference(),
        (LazyList, ts[1]): lambda: lhs._dereference() + [rhs],
        (list, list): lambda: lhs + rhs,
        (list, LazyList): lambda: lhs + rhs._dereference(),
        (LazyList, list): lambda: lhs._dereference() + rhs,
        (LazyList, LazyList): lambda: lhs._dereference() + rhs._dereference()
    }[ts]()
def modulo(lhs, rhs):
    ts = VY_type(lhs, rhs, simple=True)
    if ts[0] == number and rhs == 0: return 0
    return {
        (number, number): lambda: lhs % rhs,
        (number, str): lambda: divide(lhs, rhs)[-1],
        (str, number): lambda: divide(lhs, rhs)[-1],
        (str, str): lambda: format_string(lhs, [rhs]),
        (str, list): lambda: format_string(lhs, rhs)
    }.get(ts, lambda: vectorise(modulo, lhs, rhs))()
def multiply(lhs, rhs):
    ts = VY_type(lhs, rhs)
    if ts == (types.FunctionType, number):
        lhs.stored_arity = int(rhs)
        return lhs
    elif ts == (number, types.FunctionType):
        rhs.stored_arity = int(lhs)
        return rhs
    return {
        (number, number): lambda: lhs * rhs,
        (number, str): lambda: int(lhs) * rhs,
        (str, number): lambda: lhs * rhs,
        (str, str): lambda: [x + rhs for x in lhs]
    }.get(ts, lambda: vectorise(multiply, lhs, rhs))()
def negate(lhs):
    return {
        number: lambda: -lhs,
        str: lambda: lhs.swapcase()
    }.get(VY_type(lhs), lambda: vectorise(negate, lhs))()
def pop(vector, num=1, wrap=False):
    global last_popped
    ret = []
    for _ in range(num):
        if vector: ret.append(vector.pop())
        else: x = get_input(); ret.append(x)

    if retaIn_items: vector += ret[::-1]

    last_popped = ret
    if num == 1 and not wrap: return ret[0]
    if reverse_args: return ret[::-1]
    return ret
def prefixes(vector):
    for i in range(len(iterable(vector))):
        yield iterable(vector)[0:i+1]
def prepend(lhs, rhs):
    types = (VY_type(lhs), VY_type(rhs))
    return {
        (types[0], types[1]): lambda: merge(rhs, lhs),
        (list, types[1]): lambda: [rhs] + lhs,
        (LazyList, types[1]): lambda: [rhs] +lhs.listify()
    }[types]()
def realify(lhs):
    if isinstance(lhs, sympy.core.numbers.ComplexInfinity) or isinstance(lhs, sympy.core.numbers.NaN):
        return 0
    else: return lhs
def replace(haystack, needle, replacement):
    t_haystack = VY_type(haystack)
    if t_haystack is list:
        return [replacement if value == needle else value for value in haystack]
    elif t_haystack is LazyList:
        return replace(haystack.listify(), needle, replacement) # Not sure how to do replacement on generators yet
    else:
        return str(haystack).replace(str(needle), str(replacement))
def reverse(vector):
    if type(vector) in [float, int]:
        s_vector = str(vector)
        if vector < 0:
            return -type(vector)(s_vector[1:][::-1])
        else:
            return type(vector)(s_vector[::-1])
    return vector[::-1]
def sentence_case(lhs):
    ret = ""
    capitalise = True
    for char in lhs:
        ret += (lambda: char.lower(), lambda: char.upper())[capitalise]()
        if capitalise and char != " ": capitalise = False
        capitalise = capitalise or char in "!?."
    return ret
def strip_non_alphabet(name):
    stripped = filter(lambda char: char in string.ascii_letters + "_", name)
    return "".join(stripped)
def subtract(lhs, rhs):
    return {
        (number, number): lambda: lhs - rhs,
        (number, str): lambda: ("-" * lhs) + rhs,
        (str, number): lambda: lhs + ("-" * rhs),
        (str, str): lambda: lhs.replace(rhs, "")
    }.get(VY_type(lhs, rhs), lambda: vectorise(subtract, lhs, rhs))()
tab = lambda x: newline.join(["    " + lhs for lhs in x.split(newline)]).rstrip("    ")
def transformer_vectorise(function, vector):
    return vectorise(function, *pop(vector, function.stored_arity), explicit=True)
def uninterleave(lhs):
    left, right = [], []
    for i in range(len(lhs)):
        if i % 2 == 0: left.append(lhs[i])
        else: right.append(lhs[i])
    if type(lhs) is str:
        return ["".join(left), "".join(right)]
    return [left, right]
def uniquify(vector):
    seen = []
    for item in vector:
        if item not in seen:
            yield item
            seen.append(item)
def vectorise(fn, left, right=None, third=None, explicit=False):
    if third:
        ts = (VY_type(left), VY_type(right))
        def gen():
            for pair in VY_zip(right, left):
                yield apply(fn, *pair, third)
        def expl(l, r):
            for item in l:
                yield apply(fn, item, r, third)
        def swapped_expl(l, r):
            for item in r:
                yield apply(fn, l, item, third)

        ret =  {
            (ts[0], ts[1]): (lambda: apply(fn, left, right),
                                   lambda: expl(iterable(left), right)),
            (list, ts[1]): (lambda: [apply(fn, x, right) for x in left],
                               lambda: expl(left, right)),
            (ts[0], list): (lambda: [apply(fn, left, x) for x in right],
                               lambda: swapped_expl(left, right)),
            (LazyList, ts[1]): (lambda: expl(left, right),
                                    lambda: expl(left, right)),
            (ts[0], LazyList): (lambda: swapped_expl(left, right),
                                    lambda: swapped_expl(left, right)),
            (list, list): (lambda: gen(),
                           lambda: expl(left, right)),
            (LazyList, LazyList): (lambda: gen(),
                                     lambda: expl(left, right)),
            (list, LazyList): (lambda: gen(),
                                lambda: expl(left, right)),
            (LazyList, list): (lambda: gen(),
                                lambda: expl(left, right))
        }[ts][explicit]()

        if type(ret) is types.GeneratorType: return LazyList(ret)
        else: return ret
    elif right:
        ts = (VY_type(left), VY_type(right))
        def gen():
            for pair in VY_zip(left, right): yield apply(fn, *pair)
        def expl(l, r):
            for item in l: yield apply(fn, item, r)
        def swapped_expl(l, r):
            for item in r:
                yield apply(fn, item, l)
        ret = {
            (ts[0], ts[1]): (lambda: apply(fn, left, right),
                                   lambda: expl(iterable(left), right)),
            (list, ts[1]): (lambda: [apply(fn, x, right) for x in left],
                               lambda: expl(left, right)),
            (ts[0], list): (lambda: [apply(fn, left, x) for x in right],
                               lambda: swapped_expl(left, right)),
            (LazyList, ts[1]): (lambda: expl(left, right),
                                    lambda: expl(left, right)),
            (ts[0], LazyList): (lambda: swapped_expl(left, right),
                                    lambda: swapped_expl(left, right)),
            (list, list): (lambda: gen(),
                           lambda: expl(left, right)),
            (LazyList, LazyList): (lambda: gen(),
                                     lambda: expl(left, right)),
            (list, LazyList): (lambda: gen(),
                                lambda: expl(left, right)),
            (LazyList, list): (lambda: gen(),
                                lambda: expl(left, right))
        }[ts][explicit]()

        if type(ret) is types.GeneratorType: return LazyList(ret)
        else: return ret

    else:
        if VY_type(left) is LazyList:
            def gen():
                for item in left:
                    yield apply(fn, item)
            return LazyList(gen())
        elif VY_type(left) in (str, number):
            return apply(fn, list(iterable(left)))
        else:
            ret =  [apply(fn, x) for x in left]
            return ret
def wrap(lhs, rhs):
    ts = VY_type(lhs, rhs)
    if ts == (types.FunctionType, ts[1]):
        return map_every_n(rhs, lhs, 2)
    elif ts == (ts[0], types.FunctionType):
        return map_every_n(lhs, rhs, 2)

    # Because textwrap.wrap doesn't consistently play nice with spaces
    ret = []
    temp = []
    for lhs in lhs:
        temp.append(lhs)
        if len(temp) == rhs:
            if all([type(x) is str for x in temp]):
                ret.append("".join(temp))
            else:
                ret.append(temp[::])
            temp = []
    if len(temp) < rhs and temp:
        if all([type(x) is str for x in temp]):
            ret.append("".join(temp))
        else:
            ret.append(temp[::])

    return ret
def wrap_in_lambda(tokens):
    if tokens[0] == Structure.NONE:
        return [(Structure.LAMBDA, {Keys.LAMBDA_BODY: [tokens], Keys.LAMBDA_ARGS: str(command_dict.get(tokens[1], (0,0))[1])})]
    elif tokens[0] == Structure.LAMBDA:
        return tokens
    else:
        return (Structure.LAMBDA, {Keys.LAMBDA_BODY: tokens})
def VY_bin(lhs):
    return {
        number: lambda: [int(x) for x in bin(int(lhs))[2:]],
        str: lambda: [[int(x) for x in bin(ord(let))[2:]] for let in lhs]
    }.get(VY_type(lhs), lambda: vectorise(VY_bin, lhs))()
def VY_eval(lhs):
    if online_version or safe_mode:
        try: return pwn.safeeval.const(lhs)
        except:
            t = parse(lhs)
            if len(t) and t[-1][0] in (Structure.NUMBER, Structure.STRING, Structure.LIST):
                try:
                    temp = transpile(lhs)
                    stack = []
                    exec(temp)
                    return stack[-1]
                except Exception as e:
                    print(e)
                    return lhs
            else:
                return lhs
    else:
        try: ret = eval(lhs); return ret
        except: return lhs
def VY_filter(lhs, rhs):
    def default_case(left, right):
        # remove elements from a that are in b
        out = "" if type(left) is str else []
        for item in left:
            if item not in right:
                if type(out) is str:
                    out += str(lhs)
                else:
                    out.append(lhs)
        return out

    def _filter(function, vec):
        for lhs in vec:
            val = function([lhs])[-1]
            if bool(val):
                yield lhs
    ts = (VY_type(fn), VY_type(vector))
    return {
        ts: lambda: default_case(iterable(lhs, str), iterable(rhs, str)),
        (types.FunctionType, types[1]): lambda: LazyList(_filter(lhs, iterable(rhs, range))),
        (types[0], types.FunctionType): lambda: LazyList(_filter(rhs, iterable(lhs, range)))
    }[ts]()
def VY_int(lhs, base=10):
    t_lhs = type(lhs)
    if t_lhs not in [str, sympy.core.numbers.Rational, int, complex]:
        ret = 0
        for element in lhs:
            ret = multiply(ret, base)
            ret = add(ret, element)
        return ret
    elif t_lhs is str:
        return int(lhs, base)
    elif t_lhs is complex:
        return numpy.real(lhs)
    elif t_lhs is sympy.core.numbers.Rational:
        return int(lhs)
    elif t_lhs:
        return VY_int(iterable(lhs), base)
def VY_max(lhs, rhs=None):
    if rhs is not None:
        return {
            (number, number): lambda: max(lhs, other),
            (number, str): lambda: max(str(lhs), other),
            (str, number): lambda: max(lhs, str(other)),
            (str, str): lambda: max(lhs, other)
        }.get((VY_type(lhs), VY_type(other)), lambda: vectorise(VY_max, lhs, other))()
    else:
        obj = iterable(lhs)
        maximum = obj[0]
        for lhs in obj[1:]:
            if gt(lhs, maximum):
                maximum = lhs
        return maximum
def VY_min(lhs, rhs=None):
    if rhs is not None:
        return {
            (number, number): lambda: min(lhs, other),
            (number, str): lambda: min(str(lhs), other),
            (str, number): lambda: min(lhs, str(other)),
            (str, str): lambda: min(lhs, other)
        }.get((VY_type(lhs), VY_type(other)), lambda: vectorise(VY_min, lhs, other))()
    else:
        obj = iterable(lhs)
        minimum = obj[0]
        for lhs in obj[1:]:
            if lt(lhs, minimum):
                minimum = lhs
        return minimum
def VY_map(lhs, rhs, indicies=None):
    if types.FunctionType not in VY_type(lhs, rhs):
        @LazyList
        def f(): 
            for item in iterable(lhs): yield [rhs, item]
        return f()
    
    function = vector = None
    if isinstance(rhs, types.FunctionType):
        function, vector = rhs, iterable(lhs, range)
    else:
        function, vector = lhs, iterable(rhs, range)
    
    @LazyList
    def g():
        for item in vector:
            yield apply(function, item)
    return g()
def VY_print(lhs, end="\n"):
    if isinstance(lhs, LazyList):
        lhs.output(end)
    elif isinstance(lhs, list):
        VY_print(LazyList(lhs), end=end)
    else:
        if online_version:
            output[1] += str(lhs) + end
        else:
            print(lhs, end=end)
def VY_str(lhs):
    t_lhs = VY_type(lhs)
    return {
        number: str,
        str: lambda x: x,
        list: lambda x: "⟨" + "|".join([VY_repr(y) for y in x]) + "⟩",
        LazyList: lambda x: VY_str(x._dereference()),
        types.FunctionType: lambda x: VY_str(function_call(lhs, stack)[0])
    }[t_lhs](lhs)
def VY_range(lhs, start=0, lift_factor=0):
    t_lhs = VY_type(lhs)
    if t_lhs == number:
        if lhs < 0:
            return range(start, int(lhs) + lift_factor, -1)
        return range(start, int(lhs) + lift_factor)
    return lhs
def VY_reduce(lhs, rhs):
    if types.FunctionType not in VY_type(lhs, rhs):
        return [lhs, vectorise(reverse, rhs)]
    
    function = vector = working_value = None
    if isinstance(rhs, types.FunctionType):
        function, vector = rhs, iterable(lhs, range)
    else:
        function, vector = lhs, iterable(rhs, range)
    if not isinstance(vector, LazyList): vector = iter(vector)
    working_value = next(vector)
    

    for item in vector:
        working_value = function([working_value, item], arity=2)[-1]
    return [working_value]
def VY_repr(lhs):
    t_lhs = VY_type(lhs)
    return {
        number: str,
        list: lambda x: "⟨" + "|".join([str(VY_repr(y)) for y in x]) + "⟩",
        LazyList: lambda x: VY_repr(x._dereference()),
        str: lambda x: "`" + x + "`",
        types.FunctionType: lambda x: "@FUNCTION:" + x.__name__
    }[t_lhs](lhs)
def VY_type(lhs, rhs=None, simple=False):
    if rhs is not None:
        return (VY_type(lhs, simple=simple), VY_type(rhs, simple=simple))
    elif isinstance(lhs, int) or isinstance(lhs, sympy.core.numbers.Rational):
        return number
    elif isinstance(lhs, LazyList):
        return (LazyList, list)[simple]
    else:
        return type(lhs)
def VY_zip(lhs, rhs):
    @LazyList
    def f():
        lengths = (len(lhs), len(rhs))
        for i in range(max(lengths)):
            left = lhs[i] if i < lengths[0] else 0
            right = rhs[i] if i < lengths[1] else 0
            yield [left, right]
    return f()
def zipmap(lhs, rhs):
    if types.FunctionType not in VY_type(lhs, rhs):
        return VY_zip(lhs, rhs)
    
    function = vector = None
    if isinstance(rhs, types.FunctionType):
        function, vector = rhs, iterable(lhs, range)
    else:
        function, vector = lhs, iterable(rhs, range) 
    return VY_zip(vector, VY_map(function, deref(vector)))
def zipwith(function, lhs, rhs):
    @LazyList
    def f():
        for pair in VY_zip(lhs, rhs):
            yield apply(function, *pair)
    return f()
def transpile(program, header=""):
    if not program: return header or "pass" # If the program is empty, we probably just want the header or the shortest do-nothing program
    compiled = ""

    if isinstance(program, str):
        program = parse(program)
    for token in program:
        token_name, token_value = token
        if token_name == Structure.NONE:
            compiled += command_dict.get(token[1], "  ")[0]
        elif token_name == Structure.NUMBER:
            value = token[-1]
            end = value.find(".", value.find(".") + 1)

            if end != -1: value = value[:end]

            if value.isnumeric():
                compiled += f"stack.append({value})"
            else:
                try:
                    float(value)
                    compiled += f"stack.append(sympy.Rational({value}))"
                except:
                    compiled += f"stack.append(sympy.Rational('0.5'))"
        elif token_name == Structure.STRING:
            string, string_type = token_value[0], token_value[1]
            if string_type == StringDelimiters.NORMAL:
                value = string.replace('"', "\\\"")
                compiled += f"stack.append(\"{value}\")"
            elif string_type == StringDelimiters.DICTIONARY:
                value = string.replace('"', "\\\"")
                compiled += f"stack.append(\"{utilities.uncompress(value)}\")"
            elif string_type == StringDelimiters.COM_NUMBER:
                number = utilities.to_ten(string, encoding.codepage_number_compress)
                compiled += f"stack.append({number})"
            elif string_type == StringDelimiters.COM_STRING:
                value = utilities.to_ten(string, encoding.codepage_string_compress)
                value = utilities.from_ten(value, utilities.base27alphabet)
                compiled += f"stack.append('{value}')"
        elif token_name == Structure.CHARACTER:
            compiled += f"stack.append({repr(token[1])})"
        elif token_name == Structure.IF:
            compiled += "temp_value = pop(stack)\n"
            compiled += "if temp_value:\n" + tab(transpile(token_value[Keys.IF_TRUE])) + newline
            if Keys.IF_FALSE in token_value: compiled += "else:\n" + tab(transpile(token_value[Keys.IF_FALSE]))
        elif token_name == Structure.FOR:
            loop_variable = "LOOP_" + secrets.token_hex(16)
            if Keys.FOR_VAR in token_value:
                loop_variable = "VAR_" + strip_non_alphabet(token_value[Keys.FOR_VAR])
            compiled += "for " + loop_variable + " in VY_range(pop(stack)):" + newline
            compiled += tab("context_level += 1") + newline
            compiled += tab("context_values.append(" + loop_variable + ")") + newline
            compiled += tab(transpile(token_value[Keys.FOR_BODY])) + newline
            compiled += tab("context_level -= 1") + newline
            compiled += tab("context_values.pop()")
        elif token_name == Structure.WHILE:
            condition = "stack.append(1)"
            if Keys.WHILE_COND in value:
                condition = transpile(token_value[Keys.WHILE_COND])
            
            compiled += condition + newline
            compiled += "while pop(stack):\n"
            compiled += tab(transpile(token_value[Keys.WHILE_BODY])) + newline
            compiled += tab(condition)
        elif token_name == Structure.FUNCTION:
            # Determine if it's a function call or definition
            if Keys.FUNC_BODY not in token_value:
                # Function call
                compiled += "stack += FN_" + token_value[Keys.FUNC_NAME] + "(stack)"
            else:
                function_information = token_value[Keys.FUNC_NAME].split(":")
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

                compiled += "def FN_" + function_name + "(parameter_stack, arity=None):\n"
                compiled += tab("global context_level, context_values, input_level, input_values, retaIn_items, printed, register") + newline
                compiled += tab("context_level += 1") + newline
                compiled += tab("input_level += 1") + newline
                compiled += tab(f"this_function = FN_{function_name}") + newline
                if parameter_count == 1:
                    # There's only one parameter, so instead of pushing it as a list
                    # (which is kinda rather inconvienient), push it as a "scalar"

                    compiled += tab("context_values.append(parameter_stack[-1])")
                elif parameter_count != -1:
                    compiled += tab(f"context_values.append(parameter_stack[:-{parameter_count}])")
                else:
                    compiled += tab("context_values.append(parameter_stack)")

                compiled += newline

                compiled += tab("parameters = []") + newline

                for parameter in parameters:
                    if parameter == -1:
                        compiled += tab("""arity = pop(parameter_stack)
if VY_type(arity) == Number:
    parameters += parameter_stack[-int(arity):]
else:
    parameters += [arity]
""")
                    elif parameter == 1:
                        compiled += tab("parameters.append(pop(parameter_stack))")
                    elif isinstance(parameter, int):
                        compiled += tab(f"parameters += pop(parameter_stack, {parameter})")
                    else:
                        compiled += tab("VAR_" + parameter + " = pop(parameter_stack)")
                    compiled += newline

                compiled += tab("stack = parameters[::]") + newline
                compiled += tab("input_values[input_level] = [stack[::], 0]") + newline
                compiled += tab(transpile(token_value[Keys.FUNC_BODY])) + newline
                compiled += tab("context_level -= 1; context_values.pop()") + newline
                compiled += tab("input_level -= 1") + newline
                compiled += tab("return stack")
        elif token_name == Structure.LAMBDA:
            defined_arity = 1
            if Keys.LAMBDA_ARGS in token_value:
                lambda_argument = token_value[Keys.LAMBDA_ARGS]
                if lambda_argument.isnumeric():
                    defined_arity = int(lambda_argument)
            signature = secrets.token_hex(16)
            compiled += f"def _lambda_{signature}(parameter_stack, arity=-1, self=None):" + newline
            compiled += tab("global context_level, context_values, input_level, input_values, retaIn_items, printed, register") + newline
            compiled += tab("context_level += 1") + newline
            compiled += tab("input_level += 1") + newline
            compiled += tab(f"this_function = _lambda_{signature}") + newline
            compiled += tab("stored = False") + newline
            compiled += tab(f"if self.stored_arity != {defined_arity}: stored = self.stored_arity;") + newline
            compiled += tab(f"if arity != {defined_arity} and arity >= 0: parameters = pop(parameter_stack, arity); stack = parameters[::]") + newline
            compiled += tab("elif stored: parameters = pop(parameter_stack, stored); print(parameters); stack = parameters[::]") + newline
            if defined_arity == 1:
                compiled += tab(f"else: parameters = pop(parameter_stack); stack = [parameters]") + newline
            else:
                compiled += tab(f"else: parameters = pop(parameter_stack, {defined_arity}); stack = parameters[::]") + newline
            compiled += tab("context_values.append(parameters)") + newline
            compiled += tab("input_values[input_level] = [stack[::], 0]") + newline
            compiled += tab(transpile(token_value[Keys.LAMBDA_BODY])) + newline
            compiled += tab("ret = [pop(stack)]") + newline
            compiled += tab("context_level -= 1; context_values.pop()") + newline
            compiled += tab("input_level -= 1") + newline
            compiled += tab("return ret") + newline
            compiled += f"_lambda_{signature}.stored_arity = {defined_arity}" + newline
            compiled += f"stack.append(_lambda_{signature})"
        elif token_name == Structure.LIST:
            compiled += "temp_list = []" + newline
            for element in token_value[Keys.LIST_ITEMS]:
                if element:
                    compiled += "def list_lhs(parameter_stack):" + newline
                    compiled += tab("stack = parameter_stack[::]") + newline
                    compiled += tab(transpile(element)) + newline
                    compiled += tab("return pop(stack)") + newline
                    compiled += "temp_list.append(list_lhs(stack))" + newline
            compiled += "stack.append(temp_list[::])"
        elif token_name == Structure.FUNC_REF:
            compiled += f"stack.append(FN_{token_value[Keys.FUNC_NAME]})"
        elif token_name == Structure.VAR_SET:
            compiled += "VAR_" + token_value[Keys.VAR_NAME] + " = pop(stack)"
        elif token_name == Structure.VAR_GET:
            compiled += "stack.append(VAR_" + token_value[Keys.VAR_NAME] + ")"
        elif token_name == Structure.MONAD_TRANSFORMER:
            function_A = transpile(wrap_in_lambda(token_value[1][0]))
            compiled += function_A + newline
            compiled += "function_A = pop(stack)\n"
            compiled += transformers[token_value[0]] + newline
        elif token_name == Structure.DYAD_TRANSFORMER:
            if token_value[0] in Grouping_Transformers:
                compiled += transpile([(Structure.LAMBDA, {Keys.LAMBDA_BODY: token_value[1]})]) + newline
            else:
                function_A = transpile(wrap_in_lambda(token_value[1][0]))
                function_B = transpile(wrap_in_lambda(token_value[1][1]))
                compiled += function_A + newline + function_B + newline
                compiled += "function_B = pop(stack); function_A = pop(stack)\n"
                compiled += transformers[token_value[0]] + newline
        elif token_name == Structure.TRIAD_TRANSFORMER:
            if token_value[0] in Grouping_Transformers:
                compiled += transpile([(Structure.LAMBDA, {Keys.LAMBDA_BODY: token_value[1]})]) + newline
            else:
                function_A = transpile(wrap_in_lambda(token_value[1][0]))
                function_B = transpile(wrap_in_lambda(token_value[1][1]))
                function_C = transpile(wrap_in_lambda(token_value[1][2]))
                compiled += function_A + newline + function_B + newline + function_C + newline
                compiled += "function_C = pop(stack); function_B = pop(stack); function_A = pop(stack)\n"
                compiled += transformers[token_value[0]] + newline
        elif token_name == Structure.FOUR_ARITY_TRANSFORMER:
            if token_value[0] in Grouping_Transformers:
                compiled += transpile([(Structure.LAMBDA, {Keys.LAMBDA_BODY: token_value[1]})]) + newline
            else:
                function_A = transpile(wrap_in_lambda(token_value[1][0]))
                function_B = transpile(wrap_in_lambda(token_value[1][1]))
                function_C = transpile(wrap_in_lambda(token_value[1][2]))
                function_D = transpile(wrap_in_lambda(token_value[1][3]))
                compiled += function_A + newline + function_B + newline + function_C + newline + function_D + newline
                compiled += "function_D = pop(stack); function_C = pop(stack); function_B = pop(stack); function_A = pop(stack)\n"
                compiled += transformers[token_value[0]] + newline
        elif token_name == Structure.LAMBDA_NEWLINE:
            compiled += transpile([(Structure.LAMBDA, {Keys.LAMBDA_BODY: token_value})]) + newline

        compiled += "\n"

    
    return header + compiled

if __name__ == "__main__":

    filepath = flags = ""
    inputs = []
    header = "stack = []\nregister = 0\nprinted = False\n"

    if len(sys.argv) > 1: filepath = sys.argv[1]
    if len(sys.argv) > 2: 
        flags = sys.argv[2]
        eval_function = str if 'Ṡ' in flags else VY_eval
        if 'f' in flags: # where do we get the input from?
            inputs = list(map(eval_function, open(sys.argv[3]).readlines()))
        else:
            inputs = list(map(eval_function,sys.argv[3:]))

    if not filepath:
        while True:
            repl_line = input("   ")
            context_level = 0
            repl_line = transpile(repl_line, header)
            print(repl_line)
            exec(repl_line)
            VY_print(stack)

    # Handle all the other flags now
    inputs = [inputs] if 'a' in flags else inputs
    reverse_args = 'r' in flags
    use_encoding = 'v' in flags
    MAP_START = 0 if 'M' in flags or 'Ṁ' in flags else 1
    MAP_OFFSET = 0 if 'm' in flags or 'Ṁ' in flags else 1
    safe_mode = 'E' in flags
    keg_mode = 'K' in flags
    number_iterable = 'R' in flags
    if 'H' in flags: header += "stack = [100]\n"
    
    if use_encoding:
        import encoding
        code = open(filepath, "rb").read()
        code = encoding.vyxal_to_utf8(code)
    else:
        code = open(filepath, "r", encoding="utf-8").read()
    
    input_values[0] = [inputs, 0]
    code = transpile(code, header)
    context_level = 0
    if flags and 'c' in flags:
        print(code)
    exec(code)

    if (not printed and 'O' not in flags) or 'o' in flags:
        if flags and 's' in flags:
            print(summate(pop(stack)))
        elif flags and "ṡ" in flags:
            print(" ".join([VY_str(n) for n in stack]))
        elif flags and 'd' in flags:
            print(summate(flatten(pop(stack))))
        elif flags and 'Ṫ' in flags:
            VY_print(summate(stack))
        elif flags and 'S' in flags:
            print(" ".join([VY_str(n) for n in pop(stack)]))
        elif flags and 'C' in flags:
            print("\n".join(centre([VY_str(n) for n in pop(stack)])))
        elif flags and 'l' in flags:
            print(len(pop(stack)))
        elif flags and 'G' in flags:
            print(VY_max(pop(stack)))
        elif flags and 'g' in flags:
            print(VY_min(pop(stack)))
        elif flags and 'W' in flags:
            print(VY_str(stack))
        elif _vertical_join:
            print(vertical_join(pop(stack)))
        elif _join:
            print("\n".join([VY_str(n) for n in pop(stack)]))
        elif flags and 'J' in flags:
            print("\n".join([VY_str(n) for n in stack]))
        else:
            VY_print(pop(stack))
