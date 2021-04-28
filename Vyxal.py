# Python modules
import copy
from datetime import date
from datetime import datetime as dt
import functools
import hashlib
import itertools
import math
import random
import string
import time
import urllib.request
import warnings

# Vyxal modules
import commands
import encoding
import utilities
import VyParse
import words

# Pipped modules

try:
    import numpy
    import regex
    import sympy
except:
    import os
    os.system("pip install -r requirements.txt --quiet --disable-pip-version-check")

# Generic type constants
Number = "NUMBER"
Iterable = "ITERABLE"
Function = type(lambda: None)

Python_Generator = type(i for i in(0,)) # https://chat.stackexchange.com/transcript/message/57555979#57555979

NEWLINE = "\n"
ONE_TWO_EIGHT_KB = 1024000

# Execution variables
context_level = 0
context_values = [0]
global_stack = []
input_level = 0
inputs = []
input_values = {0: [inputs, 0]} # input_level: [source, input_index]
keg_mode = False
number_iterable = list
online_version = False
output = ""
printed = False
register = 0
retain_items = False
reverse_args = False
safe_mode = False # You may want to have safe evaluation but not be online.
stack = []

MAP_START = 1
MAP_OFFSET = 1
_join = False
_vertical_join = False
use_encoding = False
FIRST_50_FACTORIALS = [1,1,2,6,24,120,720,5040,40320,362880,3628800,39916800,479001600,6227020800,87178291200,1307674368000,20922789888000,355687428096000,6402373705728000,121645100408832000,2432902008176640000,51090942171709440000,1124000727777607680000,25852016738884976640000,620448401733239439360000,15511210043330985984000000,403291461126605635584000000,10888869450418352160768000000,304888344611713860501504000000,8841761993739701954543616000000,265252859812191058636308480000000,8222838654177922817725562880000000,263130836933693530167218012160000000,8683317618811886495518194401280000000,295232799039604140847618609643520000000,10333147966386144929666651337523200000000,371993326789901217467999448150835200000000,13763753091226345046315979581580902400000000,523022617466601111760007224100074291200000000,20397882081197443358640281739902897356800000000,815915283247897734345611269596115894272000000000,33452526613163807108170062053440751665152000000000,1405006117752879898543142606244511569936384000000000,60415263063373835637355132068513997507264512000000000,2658271574788448768043625811014615890319638528000000000,119622220865480194561963161495657715064383733760000000000,5502622159812088949850305428800254892961651752960000000000,258623241511168180642964355153611979969197632389120000000000,12413915592536072670862289047373375038521486354677760000000000,608281864034267560872252163321295376887552831379210240000000000,30414093201713378043612608166064768844377641568960512000000000000]

# Helper classes
class Comparitors:
    EQUALS = 0
    LESS_THAN = 1
    GREATER_THAN = 2
    NOT_EQUALS = 3
    LESS_THAN_EQUALS = 4
    GREATER_THAN_EQUALS = 5
class Generator:
    def __init__(self, raw_generator, limit=-1, initial=[], condition=None, is_numeric_sequence=False):
        self.next_index = 0
        self.is_numeric_sequence = is_numeric_sequence
        if "__name__" in dir(raw_generator) and type(raw_generator) != Python_Generator:
            if raw_generator.__name__.startswith("FN_") or raw_generator.__name__ == "_lambda":
                # User defined function
                def gen():
                    generated = initial
                    factor = len(initial)
                    for item in initial:
                        yield item
                    while True:
                        if len(generated) >= (limit + factor) and limit > 0:
                            break
                        else:
                            ret = raw_generator(generated[::-1], arity=len(generated))
                            generated.append(ret[-1])
                            yield ret[-1]
                self.gen = gen()
            else:
                def gen():
                    index = 0
                    while True:
                        yield raw_generator(index)
                        index += 1
                self.gen = gen()
        else:
            def niceify(item):
                t_item = VY_type(item)
                if t_item not in [Generator, list, Number, str]:
                    return list(item)
                return item
            self.gen = map(niceify, raw_generator)
        self.generated = []
    def __contains__(self, item):
        if self.is_numeric_sequence:
            if item in self.generated: return True
            temp = next(self)
            while temp <= item:
                temp = next(self)
            return item in self.generated
        else:
            for temp in self:
                if item == temp: return True
            return False
    def __getitem__(self, position):
        if type(position) is slice:
            ret = []
            stop = position.stop or self.__len__()
            start = position.start or 0

            if stop < 0:
                stop = self.__len__() - position.stop - 2


            if position.step and position.step < 0:
                start, stop = stop, start
                stop -= 1
                start -= 1
            # print(start, stop, position.step or 1)
            for i in range(start, stop, position.step or 1):
                ret.append(self.__getitem__(i))
                # print(self.__getitem__(i))
            return ret
        if position < 0:
            return list(self.gen)[position]
        if position < len(self.generated):
            return self.generated[position]
        while len(self.generated) < position + 1:
            try:
                self.__next__()
            except:
                position = position % len(self.generated)

        return self.generated[position]
    def __setitem__(self, position, value):
        if position >= len(self.generated):
            temp = self.__getitem__(position)
        self.generated[position] = value
    def __len__(self):
        return len(self._dereference())
    def __next__(self):
        f = next(self.gen)
        self.generated.append(f)
        return f
    def __iter__(self):
        return self
    def _map(self, function):
        return Generator(map(lambda x: _safe_apply(function, x) , self.gen))
    def _filter(self, function):
        index = 0
        l = self.__len__()
        while True:
            if index == l:
                break
            obj = self.__getitem__(index)
            ret = _safe_apply(function, obj)
            if ret:
                yield obj
            index += 1
    def _reduce(self, function):
        def ensure_singleton(function, left, right):
            ret = _safe_apply(function, right, left)
            if type(ret) in [Generator, list]:
                return ret[-1]
            return ret
        return functools.reduce(lambda x, y: ensure_singleton(function, x, y), self._dereference())
    def _dereference(self):
        '''
        Only call this when it is absolutely neccesary to convert to a list.
        '''
        d = self.generated + list(self.gen)
        self.gen = iter(d[::])
        self.generated = []
        return d
    def _print(self, end="\n"):
        main = self.generated
        try:
            f = next(self)
            # If we're still going, there's stuff in main that needs printing before printing the generator
            VY_print("⟨", end="")
            for i in range(len(main)):
                VY_print(main[i], end="|"*(i >= len(main)))
            while True:
                try:
                    f = next(self)
                    VY_print("|", end="")
                    VY_print(f, end="")
                except:
                    break
            VY_print("⟩", end=end)

        except:
            VY_print(main, end=end)


    def zip_with(self, other):
        return Generator(zip(self.gen, iter(other)))
    def safe(self):
        import copy
        return copy.deepcopy(self)
class ShiftDirections:
    LEFT = 1
    RIGHT = 2

# Helper functions
def _safe_apply(function, *args):
    '''
    Applies function to args that adapts to the input style of the passed function.

    If the function is a _lambda (it's been defined within λ...;), it passes a list of arguments and length of argument list
    Otherwise, if the function is a user-defined function (starts with FN_), it simply passes the argument list
    Otherwise, unpack args and call as usual
    '''
    args = reverse(args)
    if function.__name__ == "_lambda":
        ret = function(list(args), len(args))
        if len(ret): return ret[-1]
        else: return []
    elif function.__name__.startswith("FN_"):
        ret = function(list(args))[-1]
        if len(ret): return ret[-1]
        else: return []
    return function(*args)
def _two_argument(function, lhs, rhs):
    '''
    Used for vectorising user-defined lambas/dyads over generators
    '''
    if function.__name__ == "_lambda":
        return Generator(map(lambda x: function(x, arity=2), VY_zip(lhs, rhs)))
    return Generator(map(lambda x: function(*x), VY_zip(lhs, rhs)))
def add(lhs, rhs):
    '''
    Returns lhs + rhs. Check command docs for type cohesion.
    '''
    types = VY_type(lhs), VY_type(rhs)
    return {
        (Number, Number): lambda: lhs + rhs,
        (str, str): lambda: lhs + rhs,
        (str, Number): lambda: str(lhs) + str(rhs),
        (Number, str): lambda: str(lhs) + str(rhs),
        (list, types[1]): lambda: [add(item, rhs) for item in lhs],
        (types[0], list): lambda: [add(lhs, item) for item in rhs],
        (list, list): lambda: list(map(lambda x: add(*x), VY_zip(lhs, rhs))),
        (list, Generator): lambda: _two_argument(add, lhs, rhs),
        (Generator, list): lambda: _two_argument(add, lhs, rhs),
        (Generator, Generator): lambda: _two_argument(add, lhs, rhs)
    }.get(types, lambda: vectorise(add, lhs, rhs))()
def all_combinations(vector):
    ret = []
    for i in range(len(vector) + 1):
        ret = join(ret, combinations_replace_generate(vector, i))
    return ret
def all_prime_factors(item):
    if VY_type(item) == Number:
        m = sympy.ntheory.factorint(int(item))
        out = []
        for key in sorted(m.keys()):
            out += [key] * m[key]
        return out
    elif VY_type(item) is str:
        return item.title()
    return vectorise(all_prime_factors, item)
def assigned(vector, index, item):
    if type(vector) is str:
        vector = list(vector)
        vector[index] = item
        return "".join([str(x) for x in vector])
    else:
        vector[index] = item
        return vector
def bifuricate(item):
    t_item = VY_type(item)
    if t_item in (Number, list, str):
        return [item, reverse(item)]
    else:
        g = item._dereference()
        return [g, reverse(g)]
def bit_and(lhs, rhs):
    types = (VY_type(lhs), VY_type(rhs))
    return {
        (Number, Number): lambda: lhs & rhs,
        (Number, str): lambda: rhs.centre(lhs),
        (str, Number): lambda: lhs.centre(rhs),
        (str, str): lambda: lhs.centre(len(rhs) - len(lhs)),
        (types[0], list): lambda: [bit_and(lhs, item) for item in rhs],
        (list, types[1]): lambda: [bit_and(item, rhs) for item in lhs],
        (list, list): lambda: list(map(lambda x: bit_and(*x), VY_zip(lhs, rhs))),
        (list, Generator): lambda: _two_argument(bit_and, lhs, rhs),
        (Generator, list): lambda: _two_argument(bit_and, lhs, rhs),
        (Generator, Generator): lambda: _two_argument(bit_and, lhs, rhs)
    }.get(types, lambda: vectorise(bit_and, lhs, rhs))()
def bit_or(lhs, rhs):
    types = (VY_type(lhs), VY_type(rhs))
    if types == (str, str):
        suffixes = {lhs[-i:] for i in range(1, len(lhs) + 1)}
        prefixes = {rhs[:i] for i in range(1, len(rhs) + 1)}
        common = suffixes & prefixes
        if len(common) == 0:
            return lhs + rhs
        common = common.pop()
        return lhs[:-len(common)] + common + rhs[len(common):]
    return {
        (Number, Number): lambda: lhs | rhs,
        (Number, str): lambda: lhs[:rhs] + lhs[rhs + 1:],
        (str, Number): lambda:  rhs[:lhs] + rhs[lhs + 1:],
        (types[0], list): lambda: [bit_or(lhs, item) for item in rhs],
        (list, types[1]): lambda: [bit_or(item, rhs) for item in lhs],
        (list, list): lambda: list(map(lambda x: bit_or(*x), VY_zip(lhs, rhs))),
        (list, Generator): lambda: _two_argument(bit_or, lhs, rhs),
        (Generator, list): lambda: _two_argument(bit_or, lhs, rhs),
        (Generator, Generator): lambda: _two_argument(bit_or, lhs, rhs)
    }.get(types, lambda: vectorise(bit_or, lhs, rhs))()
def bit_not(item):
    return {
        str: lambda: int(any(map(lambda x: x.isupper(), item))),
        Number: lambda: ~item
    }.get(VY_type(item), lambda: vectorise(bit_not, item))()
def bit_xor(lhs, rhs):
    types = (VY_type(lhs), VY_type(rhs))
    return {
        (Number, Number): lambda: lhs ^ rhs,
        (Number, str): lambda: (" " * lhs) + rhs,
        (str, Number): lambda: lhs + (" " * rhs),
        (str, str): lambda: levenshtein_distance(lhs, rhs),
        (types[0], list): lambda: [bit_xor(lhs, item) for item in rhs],
        (list, types[1]): lambda: [bit_xor(item, rhs) for item in lhs],
        (list, list): lambda: list(map(lambda x: bit_xor(*x), VY_zip(lhs, rhs))),
        (list, Generator): lambda: _two_argument(bit_xor, lhs, rhs),
        (Generator, list): lambda: _two_argument(bit_xor, lhs, rhs),
        (Generator, Generator): lambda: _two_argument(bit_xor, lhs, rhs)
    }.get(types, lambda: vectorise(bit_xor, lhs, rhs))()
def cartesian_product(lhs, rhs):
    if Function not in (VY_type(lhs), VY_type(rhs)):
        lhs, rhs = iterable(lhs), iterable(rhs)
        return Generator(itertools.product(iterable(lhs), iterable(rhs)))
    
    if VY_type(lhs) is Function:
        fn, init = lhs, rhs
    else:
        fn, init = rhs, lhs
    def gen():
        prev = None
        curr = init
        while prev != curr:
            prev = deref(curr)
            curr = fn([curr])[-1]
        yield curr
    return Generator(gen())
def ceiling(item):
    return {
        Number: lambda: math.ceil(item),
        str: lambda: item.split(" ")
    }.get(VY_type(item), lambda: vectorise(ceiling, item))()
def centre(vector):
    focal = max(map(len, vector))
    return Generator(map(lambda x: x.center(focal), vector))
def chrord(item):
    t_item = VY_type(item)
    if t_item is str and len(item) == 1:
        return ord(item)
    elif t_item == Number:
        return chr(int(item))
    else:
        return Generator(map(chrord, item))
def closest_prime(item):
    up, down = next_prime(item), prev_prime(item)
    if abs(item - down) < abs(item - up): return down
    return up
def compare(lhs, rhs, mode):
    op = ["==", "<", ">", "!=", "<=", ">="][mode]

    types = tuple(map(VY_type, [lhs, rhs]))
    boolean =  {
        types: lambda lhs, rhs: eval(f"lhs {op} rhs"),
        (Number, str): lambda lhs, rhs: eval(f"str(lhs) {op} rhs"),
        (str, Number): lambda lhs, rhs: eval(f"lhs {op} str(rhs)"),
        (types[0], list): lambda *x: [compare(lhs, item, mode) for item in rhs],
        (list, types[1]): lambda *x : [compare(item, rhs, mode) for item in lhs],
        (Generator, types[1]): lambda *y: vectorise(lambda x: compare(x, rhs, mode), lhs),
        (types[0], Generator): lambda *y: vectorise(lambda x: compare(lhs, x, mode), rhs),
        (list, list): lambda *y: list(map(lambda x: compare(*x, mode), VY_zip(lhs, rhs))),
        (list, Generator): lambda *y: Generator(map(lambda x: compare(*x, mode), VY_zip(lhs, rhs))),
        (Generator, list): lambda *y: Generator(map(lambda x: compare(*x, mode), VY_zip(lhs, rhs))),
        (Generator, Generator): lambda *y: Generator(map(lambda x: compare(*x, mode), VY_zip(lhs, rhs))),
    }[types](lhs, rhs)
    if type(boolean) is bool:
        return int(boolean)
    else:
        return boolean
def complement(item):
    return {
        Number: lambda: 1 - item,
        str: lambda: item.split(",")
    }.get(VY_type(item), lambda: vectorise(complement, item))()
def combinations_replace_generate(lhs, rhs):
    types = VY_type(lhs), VY_type(rhs)
    if Function not in types:
        ret = {
            (Number, types[1]): lambda: Generator(itertools.product(iterable(rhs), repeat=lhs)),
            (types[0], Number): lambda: Generator(itertools.product(iterable(lhs), repeat=rhs))
        }.get(types, lambda: -1)()
        if ret != -1: return ret
        out = "" if type(lhs) is str else []
        for item in lhs:
            if item in rhs:
                if type(lhs) is str: out += item
                else: out.append(item)
        return out
    else:
        if VY_type(lhs) is Function:
            fn, init = lhs, rhs
        else:
            fn, init = rhs, lhs
        def gen():
            prev = None
            curr = init
            while prev != curr:
                yield curr
                prev = deref(curr)
                curr = fn([curr])[-1]
        return Generator(gen())
def const_divisibility(item, n, string_overload):
    return {
        Number: lambda: int(item % n == 0),
        str: lambda: int(string_overload(item))
    }.get(VY_type(item), lambda: vectorise(const_divisibility, item, n, string_overload))()
def counts(vector):
    ret = []
    vector = iterable(vector)
    for item in set(vector):
        ret.append([item, vector.count(item)])
    return ret
def cumulative_sum(vector):
    ret = []
    vector = iterable(vector)
    # if VY_type(vector) is Generator: vector = vector._dereference()
    for i in range(len(vector)):
        ret.append(summate(vector[:i + 1]))
    return ret
def decimalify(vector):
    if VY_type(vector) == Number:
        return iterable(vector)
    elif VY_type(vector) is str:
        return list(vector)
    else:
        return functools.reduce(lambda x, y: divide(x, y), vector)
def deltas(vector):
        ret = []
        vector = iterable(vector)
        for i in range(len(vector) - 1):
            ret.append(subtract(vector[i], vector[i + 1]))
        return ret
def deref(item, generator_to_list=True):
    if VY_type(item) is Generator:return [item.safe, item._dereference][generator_to_list]()
    if type(item) not in [int, float, str]: return list(map(deref, item))
    return item
def dictionary_compress(item):
    item = split_on_words(VY_str(item))
    out = ""

    for word in item:
        temp = words.word_index(word)
        if temp == -1:
            out += word
        else:
            out += temp
    return "`" + out + "`"
def diagonals(vector):
    # Getting real heavy Mornington Crescent vibes from this
    vector = numpy.asarray(vector)
    diag_num = 0
    diagonal = numpy.diag(vector)
    # postive diags first
    while len(diagonal):
        yield vectorise(lambda x: x.item(), list(diagonal))
        diag_num += 1
        diagonal = numpy.diag(vector, k=diag_num)
    
    diag_num = -1
    diagonal = numpy.diag(vector, k=diag_num)
    # now the other diagonals
    while len(diagonal):
        yield vectorise(lambda x: x.item(), list(diagonal))
        diag_num -= 1
        diagonal = numpy.diag(vector, k=diag_num)
def distance_between(lhs, rhs):
    inner = Generator(map(lambda x: exponate(subtract(x[0], x[1]), 2), VY_zip(lhs, rhs)))
    inner = summate(inner)
    return exponate(inner, 0.5)
def distribute(vector, value):
    types = VY_type(vector), VY_type(value)
    if types == (Number, Number):
        return abs(vector - value)
    vector = iterable(vector)
    if VY_type(vector) is Generator:
        vector = vector._dereference()
    remaining = value
    index = 0
    while remaining > 0:
        vector[index % len(vector)] += 1
        index += 1
        remaining -= 1

    return vector
def divide(lhs, rhs):
    types = VY_type(lhs), VY_type(rhs)
    def handle_numbers(lhs, rhs):
        if rhs == 0: return 0
        normal, int_div = lhs / rhs, lhs // rhs
        return [normal, int_div][normal == int_div]
    return {
        (Number, Number): lambda: handle_numbers(lhs, rhs),
        (str, str): lambda: split(lhs, rhs),
        (str, Number): lambda: wrap(lhs, len(lhs) // rhs),
        (Number, str): lambda: wrap(rhs, len(rhs) // lhs),
        (list, types[1]): lambda: [divide(item, rhs) for item in lhs],
        (types[0], list): lambda: [divide(lhs, item) for item in rhs],
        (list, list): lambda: list(map(lambda x: divide(*x), VY_zip(lhs, rhs))),
        (list, Generator): lambda: _two_argument(divide, lhs, rhs),
        (Generator, list): lambda: _two_argument(divide, lhs, rhs),
        (Generator, Generator): lambda: _two_argument(divide, lhs, rhs)
    }.get(types, lambda: vectorise(divide, lhs, rhs))()
def divisors_of(item):
    t_item = VY_type(item)
    if t_item in [list, Generator]:
        return vectorise(divisors_of, item)

    divisors = []
    if t_item == str:
        def gen():
            s = list(item)
            i = itertools.chain.from_iterable(\
                itertools.combinations(s, r) for r in range(1,len(s)+1))

            for sub in i:
                sub = "".join(sub)
                if len(item.split(sub)) == 2:
                    yield sub

        return Generator(gen())

    for value in VY_range(item, 1, 1):
        if modulo(item, value) == 0:
            divisors.append(value)

    return divisors
def exponate(lhs, rhs):
    types = (VY_type(lhs), VY_type(rhs))

    if types == (str, str):
        pobj = regex.compile(lhs)
        mobj = pobj.search(rhs)
        return list(mobj.span()) if mobj else []

    if types == (str, Number):
        factor = rhs
        if 0 < rhs < 1:
            factor = int(1 / rhs)
        return lhs[::factor]
    return {
        (Number, Number): lambda: lhs ** rhs,
        (types[0], list): lambda: [exponate(lhs, item) for item in rhs],
        (list, types[1]): lambda: [exponate(item, rhs) for item in lhs],
        (list, list): lambda: list(map(lambda x: exponate(*x), VY_zip(lhs, rhs))),
        (list, Generator): lambda: _two_argument(exponate, lhs, rhs),
        (Generator, list): lambda: _two_argument(exponate, lhs, rhs),
        (Generator, Generator): lambda: _two_argument(exponate, lhs, rhs)
    }.get(types, lambda: vectorise(exponate, lhs, rhs))()
def factorial(item):
    t_item = VY_type(item)
    if t_item == Number:
        if item <= 50:
            return FIRST_50_FACTORIALS[item]
        return math.factorial(item)
    elif t_item == str:
        return sentence_case(item)
    else:
        return vectorise(factorial, item)
def factorials():
    # Different to factorial because this is a list of all factorials
    for item in FIRST_50_FACTORIALS:
        yield item
    
    temp = FIRST_50_FACTORIALS[-1]
    n = 51

    while True:
        temp *= n
        n += 1
        yield temp
def fibonacci():
    # A generator of all the fibonacci numbers
    # Pro-tip: wrap in a generator before pushing to stack
    
    yield 0
    yield 1
    
    memory = [0, 1]
    while True:
        temp = memory[-1] + memory[-2]
        memory.append(temp)
        yield temp
def find(haystack, needle, start=0):
    if type(needle) is Function:
        return indexes_where(haystack, needle)
    # It looks like something from 2001
    index = 0
    haystack = iterable(haystack)
    if type(haystack) is str:
        needle = str(needle)
    if type(start) is int or (type(start) is str and start.isnumeric()):
        index = start
    while index < len(haystack):
        if haystack[index] == needle:
            return index
        index += 1
    return -1
def first_n(func, n=None):
    if Function not in (type(func), type(n)):
        if n:
            return iterable(func)[n:]
        
        ret = "".join([VY_str(n) for n in iterable(func)])
        return VY_eval(ret)
    ret = []
    current_index = 0
    n = n or 1
    if isinstance(n, Function):
        call, limit = n, func
    else:
        call, limit = func, n
    while len(ret) < limit:
        result = call([current_index])[-1]
        if result: ret.append(current_index)
        current_index += 1

    return ret
def flatten(item):
    '''
    Returns a deep-flattened (all sublists expanded) version of the input
    '''
    t_item = VY_type(item)
    if t_item is Generator:
        return Generator(functools.reduce(list.__add__, item))
    else:
        ret = []
        for x in item:
            if type(x) is list:
                ret += flatten(x)
            else:
                ret.append(x)
        return ret
def floor(item):
    return {
        Number: lambda: math.floor(item),
        str: lambda: int("".join([l for l in item if l in "0123456789"]))
    }.get(VY_type(item), lambda: vectorise(floor, item))()
def format_string(string, items):
    ret = ""
    index = 0
    f_index = 0

    while index < len(string):
        if string[index] == "\\":
            ret += "\\" + string[index + 1]
            index += 1
        elif string[index] == "%":
            #print(f_index, f_index % len(items))
            ret += str(items[f_index % len(items)])
            f_index += 1
        else:
            ret += string[index]
        index += 1
    return ret
def fractionify(item):
    import re
    if VY_type(item) == Number:
        from fractions import Fraction
        from decimal import Decimal
        frac = Fraction(item).limit_denominator()
        return [frac.numerator, frac.denominator]
    elif type(item) is str:
        if re.match(r"\-?\d+(\.\d+)?", item): return fractionify(eval(item))
        else: return item
    else:
        return vectorise(fractionify, item)
def function_call(fn, vector):
    if type(fn) is Function:
        return fn(vector)
    else:
        return [{
            Number: lambda: len(prime_factors(fn)),
            str: lambda: exec(VY_compile(fn))
        }.get(VY_type(fn), lambda: vectorise(lambda x: int(not x), fn))()]
def gcd(lhs, rhs=None):
    if rhs:
        return {
            (Number, Number): lambda: math.gcd(int(lhs), int(rhs)),
            (Number, str): lambda: max(set(divisors_of(str(lhs))) & set(divisors_of(rhs)), key=lambda x: len(x)),
            (str, Number): lambda: max(set(divisors_of(lhs)) & set(divisors_of(str(rhs))), key=lambda x: len(x)),
            (str, str): lambda: max(set(divisors_of(lhs)) & set(divisors_of(rhs)), key=lambda x: len(x)),
        }.get((VY_type(lhs), VY_type(rhs)), lambda: vectorise(gcd, lhs, rhs))()

    else:
        # I can't use VY_reduce because ugh reasons
        lhs = deref(lhs, True)
        return int(numpy.gcd.reduce(lhs))   
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
def graded(item):
    return {
        Number: lambda: item + 2,
        str: lambda: item.upper(),

    }.get(VY_type(item), lambda: Generator(map(lambda x: x[0], sorted(enumerate(item), key=lambda x: x[-1]))))()
def graded_down(item):
       return {
        Number: lambda: item - 2,
        str: lambda: item.lower(),

    }.get(VY_type(item), lambda: reverse(Generator(map(lambda x: x[0], sorted(enumerate(item), key=lambda x: x[-1])))))()
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
def halve(item):
    return {
        Number: lambda: divide(item, 2),
        str: lambda: wrap(item, ceiling(len(item) / 2))
    }.get(VY_type(item), lambda: vectorise(halve, item))()
def inclusive_range(lhs, rhs):
    types = (VY_type(lhs), VY_type(rhs))
    if Function in types:
        if types[0] is Function:
            func, vector = lhs, rhs
        else:
            func, vector = rhs, lhs
        
        def gen():
            for index, item in enumerate(vector):
                if (index + 1) % 2:
                    yield item
                else:
                    yield func([item])[-1]
        
        return Generator(gen())
    if types != (Number, Number):
        lhs, rhs = VY_str(lhs), VY_str(rhs)
        pobj = regex.compile(rhs)
        return pobj.split(lhs)

    if lhs < rhs:
        return Generator(range(int(lhs), int(rhs) + 1))
    else:
        return Generator(range(int(lhs), int(rhs) - 1, -1))
def index(vector, index):
    if VY_type(index) == Number:
        return vector[int(index) % len(vector)]
    elif VY_type(index) in (list, Generator):
        return vector[slice(*index)]
    else:
        return [vector, index, join(vector, index)]
def indexed_into(vector, indexes):
    ret = []
    for ind in indexes:
        ret.append(vector[ind % len(vector)])
    return ret
def indexes_where(fn, vector):
    ret = []
    for i in range(len(vector)):
        if fn([vector[i]])[-1]:
            ret.append(i)
    return ret
def infinite_replace(haystack, needle, replacement):
    import copy
    loop = True
    prev = copy.deepcopy(haystack)
    while loop: # I intentionally used a post-test loop here to avoid making more calls to replace than neccesary
        haystack = replace(haystack, needle, replacement)
        loop = haystack != prev
        prev = copy.deepcopy(haystack)
    return haystack
def inserted(vector, item, index):
    vector = iterable(vector)
    t_vector = type(vector)
    if t_vector is list:
        vector.insert(index, item)
        return vector
    return {
        str: lambda: vector[:index] + str(item) + vector[index:],
    }.get(t_vector, lambda: inserted(vector._dereference(), item, index))()
def integer_divide(lhs, rhs):
    types = VY_type(lhs), VY_type(rhs)
    return {
        (Number, Number): lambda: lhs // rhs,
        (Number, str): lambda: divide(lhs, rhs)[0],
        (str, Number): lambda: divide(lhs, rhs)[0],
        (Function, types[1]): lambda: VY_reduce(lhs, reverse(rhs))[0],
        (types[0], Function): lambda: VY_reduce(rhs, reverse(lhs))[0]
    }.get(types, lambda: vectorise(integer_divide, lhs, rhs))()
def integer_list(string):
    charmap = dict(zip("etaoinshrd", "0123456789"))
    ret = []
    for c in string.split():
        temp = ""
        for m in c:
            temp += charmap[m]
        ret.append(int(temp))
    return ret
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
def is_divisble(lhs, rhs):
    types = VY_type(lhs), VY_type(rhs)
    return {
        (Number, Number): lambda: [int(lhs % rhs == 0)],
        (str, str): lambda: [lhs] * len(rhs),
        (str, Number): lambda: [lhs] * rhs,
        (Number, str): lambda: [rhs] * lhs
    }.get(types, lambda: vectorise(is_divisble, lhs, rhs))()
def is_empty(item):
    return {
        Number: lambda: item % 3,
        str: lambda: int(item == "")
    }.get(VY_type(item), lambda: vectorise(is_empty, item))()
def is_prime(n):
    if type(n) is str:
        if n.upper() == n.lower(): return -1
        else: return int(n.upper() == n)
    if VY_type(n) in [list, Generator]: return vectorise(is_prime, n)
    if n < 2: return 0
    if n % 2 == 0 and n > 2:
        return 0
    return int(all(n % i for i in range(3, int(math.sqrt(n)) + 1, 2)))
def iterable(item, t=None):
    t = t or number_iterable
    if VY_type(item) == Number:
        if t is list:
            return [int(let) if let not in "-." else let for let in str(item)]
        if t is range:
            return range(int(item))
        return t(item)
    else:
        return item
def iterable_shift(vector, direction, times=1):
    vector = iterable(vector)
    t_vector = type(vector)
    for _ in range(times):
        if direction == ShiftDirections.LEFT:
            if t_vector is list:
                # [1, 2, 3] -> [2, 3, 1]
                vector = vector[::-1]
                temp = pop(vector)
                vector = vector[::-1]
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
def join(lhs, rhs):
    types = tuple(map(VY_type, [lhs, rhs]))
    return {
        (types[0], types[1]): lambda: str(lhs) + str(rhs),
        (Number, Number): lambda: VY_eval(str(lhs) + str(rhs)),
        (types[0], list): lambda: [lhs] + rhs,
        (list, types[1]): lambda: lhs + [rhs],
        (types[0], Generator): lambda: [lhs] + rhs._dereference(),
        (Generator, types[1]): lambda: lhs._dereference() + [rhs],
        (list, list): lambda: lhs + rhs,
        (list, Generator): lambda: lhs + rhs._dereference(),
        (Generator, list): lambda: lhs._dereference() + rhs,
        (Generator, Generator): lambda: lhs._dereference() + rhs._dereference()
    }[types]()
def levenshtein_distance(s1, s2):
    # https://stackoverflow.com/a/32558749
    if len(s1) > len(s2):
        s1, s2 = s2, s1

    distances = range(len(s1) + 1)
    for i2, c2 in enumerate(s2):
        distances_ = [i2+1]
        for i1, c1 in enumerate(s1):
            if c1 == c2:
                distances_.append(distances[i1])
            else:
                distances_.append(1 + min((distances[i1], distances[i1 + 1], distances_[-1])))
        distances = distances_
    return distances[-1]
def log(lhs, rhs):
    types = (VY_type(lhs), VY_type(rhs))
    if types == (str, str):
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
        (Number, Number): lambda: math.log(lhs, rhs),
        (str, Number): lambda: "".join([c * rhs for c in lhs]),
        (Number, str): lambda: "".join([c * lhs for c in rhs]),
        (list, list): lambda: mold(lhs, rhs),
        (list, Generator): lambda: mold(lhs, list(rhs)),
        (Generator, list): lambda: mold(list(lhs), rhs),
        (Generator, Generator): lambda: mold(list(lhs), list(rhs)) #There's a chance molding raw generators won't work
    }.get(types, lambda: vectorise(log, lhs, rhs))()
def lshift(lhs, rhs):
    types = (VY_type(lhs), VY_type(rhs))
    return {
        (Number, Number): lambda: lhs << rhs,
        (Number, str): lambda: rhs.ljust(lhs),
        (str, Number): lambda: lhs.ljust(rhs),
        (str, str): lambda: lhs.ljust(len(rhs) - len(lhs)),
        (types[0], list): lambda: [lshift(lhs, item) for item in rhs],
        (list, types[1]): lambda: [lshift(item, rhs) for item in lhs],
        (list, list): lambda: list(map(lambda x:lshift(*x), VY_zip(lhs, rhs))),
        (list, Generator): lambda: _two_argument(lshift, lhs, rhs),
        (Generator, list): lambda: _two_argument(lshift, lhs, rhs),
        (Generator, Generator): lambda: _two_argument(lshift, lhs, rhs)
    }.get(types, lambda: vectorise(lshift, lhs, rhs))()
def map_every_n(vector, function, index):
    def gen():
        for pos, element in enumerate(vector):
            if (pos + 1) % index:
                yield element
            else:
                yield function([element])[-1]
    return Generator(gen())
def modulo(lhs, rhs):
    types = VY_type(lhs), VY_type(rhs)
    return {
        (Number, Number): lambda: lhs % rhs,
        (str, str): lambda: format_string(lhs, [rhs]),
        (str, Number): lambda: divide(lhs, rhs)[-1],
        (Number, str): lambda: divide(lhs, rhs)[-1],
        (list, types[1]): lambda: [modulo(item, rhs) for item in lhs],
        (types[0], list): lambda: [modulo(lhs, item) for item in rhs],
        (str, list): lambda: format_string(lhs, rhs),
        (list, list): lambda: list(map(lambda x: modulo(*x), VY_zip(lhs, rhs))),
        (list, Generator): lambda: _two_argument(modulo, lhs, rhs),
        (Generator, list): lambda: _two_argument(modulo, lhs, rhs),
        (Generator, Generator): lambda: _two_argument(modulo, lhs, rhs)
    }.get(types, lambda: vectorise(modulo, lhs, rhs))()
def mold(content, shape):
    #https://github.com/DennisMitchell/jellylanguage/blob/70c9fd93ab009c05dc396f8cc091f72b212fb188/jelly/interpreter.py#L578
    for index in range(len(shape)):
        if type(shape[index]) == list:
            mold(content, shape[index])
        else:
            item = content.pop(0)
            shape[index] = item
            content.append(item)
    return shape
def multiply(lhs, rhs):
    types = VY_type(lhs), VY_type(rhs)
    return {
        (Number, Number): lambda: lhs * rhs,
        (str, str): lambda: [x + rhs for x in lhs],
        (str, Number): lambda: lhs * rhs,
        (Number, str): lambda: lhs * rhs
    }.get(types, lambda: vectorise(multiply, lhs, rhs))()
def ncr(lhs, rhs):
    types = VY_type(lhs), VY_type(rhs)
    return {
        (Number, Number): lambda: math.gcd(int(lhs), int(rhs)),
        (str, Number): lambda: [random.choice(lhs) for c in range(rhs)],
        (Number, str): lambda: [random.choice(rhs) for c in range(lhs)],
        (str, str): lambda: int(set(lhs) == set(rhs)),
        (types[0], list): lambda: [ncr(lhs, item) for item in rhs],
        (list, types[1]): lambda: [ncr(item, rhs) for item in lhs],
    }.get(types, lambda: vectorise(ncr, lhs, rhs))()
def negate(item):
    return {
        Number: lambda: -item,
        str: lambda: item.swapcase()
    }.get(VY_type(item), lambda: vectorise(negate, item))()
def next_prime(item):
    if not isinstance(item, int):
        return item
    
    factor = 1
    while not is_prime(item + factor):
        factor += 1
    
    return item + factor
def nth_prime(item):
    t_item = VY_type(item)
    return {
        Number: lambda: sympy.ntheory.prime(int(item) + 1),
        str: lambda: Generator(substrings(item))
    }.get(t_item, lambda: vectorise(nth_prime, item))()
def nwise_pair(lhs, rhs):
    if VY_type(rhs) != Number:
        return len(iterable(lhs)) == len(rhs)
    iters = itertools.tee(iterable(lhs), rhs)
    for i in range(len(iters)):
        for j in range(i):
            next(iters[i], None)

    return Generator(zip(*iters))
def nub_sieve(vector):
    def gen():
        occurances = {}
        for item in vector:
            yield int(item not in occurances)
            if item in occurances: occurances[item] += 1
            else: occurances[item] = 1
    return Generator(gen())
def one_argument_tail_index(vector, index, start):
    types = (VY_type(vector), VY_type(index))
    return {
        types: lambda: start,
        (Number, Number): lambda: iterable(vector)[start:index],
        (Number, types[1]): lambda: index[start:vector],
        (types[0], Number): lambda: vector[start:index]
    }[types]()
def order(lhs, rhs):
    types = VY_type(lhs), VY_type(rhs)
    if types == (Number, Number):
        if rhs == 0 or abs(rhs) == 1: return "Infinite"
        elif lhs == 0: return 0
        temp, remainder = lhs, 0
        count = 0
        while True:
            temp, remainder = divmod(temp, rhs)
            if remainder: break
            count += 1
        return count
    else:
        return infinite_replace(iterable(lhs, str), iterable(rhs, str), "")
def orderless_range(lhs, rhs, lift_factor=0):
    if (VY_type(lhs), VY_type(rhs)) == (Number, Number):
        if lhs < rhs:
            return Generator(range(lhs, rhs + lift_factor))
        else:
            return Generator(range(lhs, rhs + lift_factor, -1))
    else:
        lhs, rhs = VY_str(lhs), VY_str(rhs)
        import regex
        pobj = regex.compile(lhs)
        mobj = pobj.search(rhs)
        return int(bool(mobj))
def overloaded_iterable_shift(lhs, rhs, direction):
    if type(rhs) is not int:
        return [lhs, iterable_shift(rhs, direction)]
    else:
        return [iterable_shift(lhs, direction, rhs)]  
def palindromise(item):
    # This is different to m or bifuricate and join because it doesn't have two duplicate in the middle
    return join(item, reverse(item)[1:])
def partition(item, I=1):
    # https://stackoverflow.com/a/44209393/9363594
    yield [item]
    for i in range(I, item//2 + 1):
        for p in partition(item-i, i):
            yield [i] + p
def permutations(vector):
    t_vector = VY_type(vector)
    vector = itertools.permutations(vector)

    if t_vector is str:
        return Generator(map(lambda x: "".join(x), vector))
    return Generator(vector)
def polynomial(vector):
    t_vector = VY_type(vector)
    if t_vector is Generator:
        vector = vector._dereference()
    return numpy.roots(vector).tolist()
def pop(vector, num=1, wrap=False):
    ret = []

    for _ in range(num):
        if vector:
            ret.append(vector.pop())
        else:
            x = get_input()
            ret.append(x)

    if retain_items:
        vector += ret[::-1]
    
    if num == 1 and not wrap:
        return ret[0]
    
    if reverse_args:
        return ret[::-1]
    return ret
def powerset(vector):
    "powerset([1,2,3]) --> () (1,) (2,) (3,) (1,2) (1,3) (2,3) (1,2,3)"
    if type(vector) is Generator:
        vector = vector._dereference()
    elif type(vector) is str:
        vector = list(vector)
    return Generator(itertools.chain.from_iterable(itertools.combinations(vector, r) for r in range(len(vector)+1)))
def prime_factors(item):
    t_item = VY_type(item)
    return {
        Number: lambda: sympy.ntheory.primefactors(int(item)),
        str: lambda: item + item[0]
    }.get(t_item, lambda: vectorise(prime_factors, item))()
def prepend(vector, item):
    vector = iterable(vector, range)
    t_vector = type(vector)
    return {
    list: lambda: [item] + vector,
    str: lambda: str(item) + vector,
    range: lambda: [item] + list(vector)
    }.get(t_vector, lambda: prepend(vector._dereference(), item))()
def prev_prime(item):
    if not isinstance(item, int):
        return item
    if item <= 2: return 0
    factor = 1
    while not is_prime(item - factor) and item - factor >= 2:
        factor += 1
    
    return item - factor
def product(vector):
    if type(vector) is Generator:
        return vector._reduce(multiply)
    if not vector: return 0
    ret = vector[0]
    for item in vector[1:]:
        ret = multiply(ret, item)
    return ret
def rand_between(lhs, rhs):
    if type(lhs) is int and type(rhs) is int:
        return random.randint(lhs, rhs)

    else:
        return random.choice([lhs, rhs])
def remove(vector, item):
    return {
        str: lambda: vector.replace(str(item), ""),
        Number: lambda: str(vector).replace(str(item), ""),
        list: lambda: Generator(filter(lambda x: x != item, vector)),
        Generator: lambda: remove(vector._dereference(), item)
    }[VY_type(vector)]()
def repeat(vector, times, extra=None):
    vector = iterable(vector)
    t_vector = VY_type(vector)
    if t_vector is Function and VY_type(times) is Function:
        def gen():
            item = extra
            while vector([item])[-1]:
                item = times([item])[-1]
                yield item
        return Generator(gen())
    
    elif times < 0:
        if t_vector is str: return vector[::-1] * times
        return Generator(itertools.repeat(reversed(vector), times))
    else:
        if t_vector is str: return vector * times
        return Generator(itertools.repeat(vector, times))
def repeat_no_collect(predicate, modifier, value):
    def gen():
        item = value
        while predicate([item])[-1]:
            item = modifier([item])[-1]
        yield item
    return gen()
def replace(haystack, needle, replacement):
    t_haystack = VY_type(haystack)
    if t_haystack is list:
        return [replacement if value == needle else value for value in haystack]
    elif t_haystack is Generator:
        return replace(haystack._dereference(), needle, replacement) # Not sure how to do replacement on generators yet
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
def rshift(lhs, rhs):
    types = (VY_type(lhs), VY_type(rhs))
    return {
        (Number, Number): lambda: lhs >> rhs,
        (Number, str): lambda: rhs.rjust(lhs),
        (str, Number): lambda: lhs.rjust(rhs),
        (str, str): lambda: lhs.rjust(len(lhs) - len(rhs)),
        (types[0], list): lambda: [rshift(lhs, item) for item in rhs],
        (list, types[1]): lambda: [rshift(item, rhs) for item in lhs],
        (list, list): lambda: list(map(lambda x:rshift(*x), VY_zip(lhs, rhs))),
        (list, Generator): lambda: _two_argument(rshift, lhs, rhs),
        (Generator, list): lambda: _two_argument(rshift, lhs, rhs),
        (Generator, Generator): lambda: _two_argument(rshift, lhs, rhs)
    }.get(types, lambda: vectorise(rshift, lhs, rhs))()
def run_length_decode(vector):
    ret = ""
    for item in vector:
        ret += item[0] * item[1]
    return ret
def run_length_encode(item):
    item = group_consecutive(iterable(item))
    return Generator(map(lambda x: [x[0], len(x)], item))
def sentence_case(item):
    ret = ""
    capitalise = True
    for char in item:
        ret += (lambda: char.lower(), lambda: char.upper())[capitalise]()
        if capitalise and char != " ": capitalise = False
        capitalise = capitalise or char in "!?."
    return ret
def set_caret(lhs, rhs):
    # Why make my own function instead of using standard ^? Because numbers and strings. that's why.
    types = VY_type(lhs), VY_type(rhs)
    new_lhs, new_rhs = {
        (Number, Number): lambda: (iterable(lhs), iterable(rhs)),
        (Number, str): lambda: (str(lhs), rhs),
        (str, Number): lambda: (lhs, str(rhs))
    }.get(types, lambda: (iterable(lhs), iterable(rhs)))()

    return list(set(new_lhs) ^ set(new_rhs))
def set_intersection(lhs, rhs):
    # Why make my own function instead of using standard &? Because numbers and strings. that's why.
    types = VY_type(lhs), VY_type(rhs)
    new_lhs, new_rhs = {
        (Number, Number): lambda: (iterable(lhs), iterable(rhs)),
        (Number, str): lambda: (str(lhs), rhs),
        (str, Number): lambda: (lhs, str(rhs))
    }.get(types, lambda: (iterable(lhs), iterable(rhs)))()

    return list(set(new_lhs) & set(new_rhs))
def set_union(lhs, rhs):
    # Why make my own function instead of using standard |? Because numbers and strings. that's why.
    types = VY_type(lhs), VY_type(rhs)
    new_lhs, new_rhs = {
        (Number, Number): lambda: (iterable(lhs), iterable(rhs)),
        (Number, str): lambda: (str(lhs), rhs),
        (str, Number): lambda: (lhs, str(rhs))
    }.get(types, lambda: (iterable(lhs), iterable(rhs)))()

    return list(set(new_lhs) | set(new_rhs))
def sign_of(item):
    t = VY_type(item)
    if t == Number:
        if item < 0: return -1
        else: return [0, 1][item != 0]
    elif t is list:
        return vectorise(sign_of, item)
    else:
        return item
def split(haystack, needle, keep_needle=False):
    t_haystack = VY_type(haystack)
    if t_haystack in [Number, str]:
        haystack, needle = str(haystack), str(needle)
        if keep_needle:
            import re
            return re.split(f"({re.escape(needle)})", haystack) # I'm so glad Vyxal now uses built-in lists
        return haystack.split(needle)
    elif t_haystack is Generator:
        return split(haystack._dereference(), needle, keep_needle)
    else: #t_haystack is list
        ret = []
        temp = []
        for item in haystack:
            if item == needle:
                ret.append(temp)
                if keep_needle:
                    ret.append([needle])
                temp = []
            else:
                temp.append(item)
        if temp:
            ret.append(temp)
        return ret
def split_newlines_or_pow_10(item):
    return {
        Number: lambda: 10 ** item,
        str: lambda: item.split("\n")
    }.get(VY_type(item), lambda: vectorise(split_newlines_or_pow_10, item))()
def split_on_words(item):
    parts = []
    word = ""

    for char in item:
        if char not in string.ascii_letters:
            if word: parts.append(word)
            word = ""
            parts.append(char)
        else:
            word += char
    
    if word: parts.append(word)
    return parts
def string_empty(item):
    return {
        Number: lambda: item % 3,
        str: len(item) == 0
    }.get(VY_type(item), lambda: vectorise(string_empty, item))()
def strip_non_alphabet(name):
    stripped = filter(lambda char: char in string.ascii_letters + "_", name)
    return "".join(stripped)
def substrings(item):
    for i in range(0, len(item) + 1):
        for j in range(1, len(item) + 1):
            yield item[i:j]
def subtract(lhs, rhs):
    types = VY_type(lhs), VY_type(rhs)

    return {
        (Number, Number): lambda: lhs - rhs,
        (str, str): lambda: lhs.replace(rhs, ""),
        (str, Number): lambda: lhs + ("-" * rhs),
        (Number, str): lambda: ("-" * lhs) + rhs,
        (list, types[1]): lambda: [subtract(item, rhs) for item in lhs],
        (types[0], list): lambda: [subtract(lhs, item) for item in rhs],
        (list, list): lambda: list(map(lambda x: subtract(*x), VY_zip(lhs, rhs))),
        (list, Generator): lambda: _two_argument(subtract, lhs, rhs),
        (Generator, list): lambda: _two_argument(subtract, lhs, rhs),
        (Generator, Generator): lambda: _two_argument(subtract, lhs, rhs)
    }.get(types, lambda: vectorise(subtract, lhs, rhs))()
def summate(vector):
    vector = iterable(vector)
    if type(vector) is Generator:
        return vector._reduce(add)
    ret = vector[0]
    for item in vector[1:]:
        ret = add(ret, item)
    return ret
def sums(vector):
    ret = []
    for i in range(len(vector)):
        ret.append(summate(vector[0:i+1]))
    return ret
tab = lambda string: NEWLINE.join(["    " + item for item in string.split(NEWLINE)]).rstrip("    ")
def transilterate(original, new, string):
    t_string = type(string)
    original = deref(original, True)
    if t_string == Generator:
        t_string = list
    ret = t_string()
    for char in string:
        if t_string is str: char = str(char)
        try:
            ind = original.index(char)
            ret += t_string(new[ind])
        except:
            ret += t_string(char)
    return ret
def transpose(vector):
    # https://github.com/DennisMitchell/jellylanguage/blob/70c9fd93ab009c05dc396f8cc091f72b212fb188/jelly/interpreter.py#L1311
    vector = iterable(vector); vector = list(vector)
    return Generator(map(lambda t: filter(None.__ne__, t), itertools.zip_longest(*map(iterable, vector))))
def trim(lhs, rhs, left = False, right = False):
    # I stole this from Jelly
    #https://github.com/DennisMitchell/jellylanguage/blob/master/jelly/interpreter.py#L1131

    if type(rhs) is Function:
        lhs = iterable(lhs)
        def gen():
            for index, item in enumerate(lhs):
                if index % 2:
                    yield _safe_apply(rhs, item)
        return gen()

    if VY_type(lhs) == Number:
        lhs = str(lhs)
    if VY_type(rhs) == Number:
        rhs = str(rhs)
    lindex = 0
    rindex = len(lhs)
    if left:
        while lindex < rindex and rhs[lindex] in lhs:
            lindex += 1
    if right:
        while lindex < rindex and rhs[rindex - 1] in lhs:
            rindex -= 1
    return lhs[lindex:rindex]
def truthy_indexes(vector):
    ret = []
    for i in range(len(vector)):
        if bool(vector[i]):
            ret.append(i)
    return ret
def two_power(item):
    if VY_type(item) == Number: return 2 ** item
    elif VY_type(item) is str:
        out = ""
        for char in item:
            if char in string.ascii_letters:
                out += char
        return out
    else:
        return vectorise(two_power, item)
def uninterleave(item):
    left, right = [], []
    for i in range(len(item)):
        if i % 2 == 0: left.append(item[i])
        else: right.append(item[i])
    if type(item) is str:
        return ["".join(left), "".join(right)]
    return [left, right]
def uniquify(vector):
    seen = []
    for item in vector:
        if not item in seen:
            yield item
            seen.append(item)
def unsympy(item):
    if type(item) in (list, Generator): return vectorise(unsympy, item)
    if item.is_Integer: return int(item)
    elif item.is_Float: return float(item)
    else: return item
def urlify(item):
    if not (item.startswith("http://") or item.startswith("https://")):
        return "https://" + item
    return item
def vectorise(fn, left, right=None, third=None):
    if third:
        left = iterable(left)
        types = (VY_type(left), VY_type(right))

        def gen():
            for pair in VY_zip(left, right):
                yield _safe_apply(fn, *pair, third)

        gen_lambda = lambda: Generator(gen())
        return {
            (types[0], types[1]): lambda: _safe_apply(fn, iterable(left), right, third),
            (list, types[1]): lambda: [_safe_apply(fn, x, right, third) for x in left],
            (types[0], list): lambda: [_safe_apply(fn, left, x, third) for x in right],
            (Generator, types[1]): lambda: left._map(lambda x: _safe_apply(fn, x, right, third)),
            (types[0], Generator): lambda: right._map(lambda x: _safe_apply(fn, left, x, third)),
            (list, list): lambda: gen_lambda,
            (Generator, Generator): gen_lambda,
            (list, Generator): gen_lambda,
            (Generator, list): gen_lambda
        }[types]()
    elif right:
        left = iterable(left)
        types = (VY_type(left), VY_type(right))

        def gen():
            for pair in VY_zip(left, right):
                yield _safe_apply(fn, *pair)

        gen_lambda = lambda: Generator(gen())

        return {
            (types[0], types[1]): lambda: safe_apply(fn, iterable(left), right),
            (list, types[1]): lambda: [_safe_apply(fn, x, right) for x in left],
            (types[0], list): lambda: [_safe_apply(fn, left, x) for x in right],
            (Generator, types[1]): lambda: left._map(lambda x: _safe_apply(fn, x, right)),
            (types[0], Generator): lambda: right._map(lambda x: _safe_apply(fn, left, x)),
            (list, list): gen_lambda,
            (Generator, Generator): gen_lambda,
            (list, Generator): gen_lambda,
            (Generator, list): gen_lambda
        }[types]()
            
    else:
        if VY_type(left) is Generator:
            return left._map(fn)
        elif VY_type(left) in (str, Number):
            return _safe_apply(fn, iterable(left))
        else:
            ret =  [_safe_apply(fn, x) for x in left]
            return ret
def vertical_join(vector, padding=" "):
    if VY_type(padding) == VY_type(vector) == Number:
        return abs(vector - padding)

    lengths = list(map(len, deref(vector, True)))
    vector = [padding * (max(lengths) - len(x)) + x for x in vector]

    out = ""
    for i in range(max(lengths)):
        for item in vector:
            out += item[i]
        out += "\n"

    return out
def wrap(vector, width):
    types = VY_type(vector), VY_type(width)
    if types == (Function, types[1]):
        return map_every_n(width, vector, 2)
    elif types == (types[0], Function):
        return map_every_n(vector, width, 2)

    # Because textwrap.wrap doesn't consistently play nice with spaces
    ret = []
    temp = []
    for item in vector:
        temp.append(item)
        if len(temp) == width:
            if all([type(x) is str for x in temp]):
                ret.append("".join(temp))
            else:
                ret.append(temp[::])
            temp = []
    if len(temp) < width and temp:
        if all([type(x) is str for x in temp]):
            ret.append("".join(temp))
        else:
            ret.append(temp[::])

    return ret
def VY_abs(item):
    return {
        Number: lambda: abs(item),
        str: lambda: remove(remove(remove(item, " "), "\n"), "\t"),
    }.get(VY_type(item), lambda: vectorise(VY_abs, item))()
def VY_bin(item):
    t_item = VY_type(item)
    return {
        Number: lambda: bin(int(item))[2:],
        str: lambda: [bin(ord(let))[2:] for let in item]
    }.get(t_item, lambda: vectorise(VY_bin, item))()
def VY_divmod(lhs, rhs):
    types = VY_type(lhs), VY_type(rhs)
    return {
        (Number, Number): lambda: [lhs // rhs, lhs % rhs],
        (types[0], Number): lambda: Generator(itertools.combinations(lhs, rhs)),
        (str, str): lambda: trim(lhs, rhs)
    }[types]()
def VY_eval(item):
    if VY_type(item) is Number: return 2 ** item
    elif VY_type(item) in [list, Generator]: return vectorise(VY_eval, item)
    if online_version or safe_mode:
        import regex
        pobj = regex.compile(r"""(\[(((-?\d+(\.\d+)?)|\g<1>|"[^"]*"|'[^']*')(, *)?)*\])|(-?\d+(\.\d+)?$)|"[^"]*"|'[^']*'""")
        mobj = pobj.match(item)
        if mobj:
            try:
                ret = eval(item)
                return ret
            except:
                return item
        else:
            return item
    else:
        try:
            ret = eval(item)
            return ret
        except:
            return item
def VY_exec(item):
    if type(item) is str:
        exec(VY_compile(item))
    elif VY_type(item) == Number:
        return divide(1, item)
    else:
        return vectorise(VY_exec, item)
def VY_filter(fn, vector):
    def default_case(lhs, rhs):
        # remove elements from a that are in b
        out = "" if type(lhs) is str else []
        for item in lhs:
            if item not in rhs:
                if type(out) is str:
                    out += str(item)
                else:
                    out.append(item)
        return out
    
    def _filter(function, vec):
        for item in vec:
            val = function([item])[-1]
            if bool(val):
                yield item
    types = (VY_type(fn), VY_type(vector))
    return {
        types: lambda: default_case(iterable(fn, str), iterable(vector, str)),
        (Function, Generator): lambda: Generator(vector._filter(fn)),
        (Generator, Function): lambda: Generator(fn._filter(fn)),
        (Function, types[1]): lambda: Generator(_filter(fn, iterable(vector, range))),
        (types[0], Function): lambda: Generator(_filter(vector, iterable(fn, range)))

    }[types]()
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
    elif t_item:
        return int(item, base)
def VY_map(fn, vector):
    ret = []
    t_vector = VY_type(vector)
    t_function = VY_type(fn)
    if t_function is not Function:
        def gen():
            for item in iterable(fn):
                yield [vector, item]
        return Generator(gen())
    if t_vector == Number:
        vector = range(MAP_START, int(vector) + MAP_OFFSET)
    if t_vector is Generator:
        return vector._map(fn)
    for item in vector:
        result = fn([item])
        ret.append(result[-1])
    return ret
def VY_max(item, *others):
    if others:
        biggest = item
        for sub in others:
            res = compare(deref(sub), deref(biggest), Comparitors.GREATER_THAN)
            if VY_type(res) in [list, Generator]:
                res = any(res)
            if res:
                biggest = sub
        return biggest
    else:
        item = flatten(item)
        if item:
            biggest = item[0]
            for sub in item[1:]:
                res = compare(deref(sub), deref(biggest), Comparitors.GREATER_THAN)
                if VY_type(res) in [list, Generator]:
                    res = any(res)
                if res:
                    biggest = sub
            return biggest
        return item
def VY_min(item, *others):
    if others:
        smallest = item
        for sub in others:
            res = compare(deref(sub), deref(smallest), Comparitors.LESS_THAN)
            if VY_type(res) in [list, Generator]:
                res = any(res)
            if res:
                smallest = sub
        return smallest
    else:
        item = flatten(item)
        if item:
            smallest = item[0]
            for sub in item[1:]:
                res = compare(deref(sub), deref(smallest), Comparitors.LESS_THAN)
                if VY_type(res) in [list, Generator]:
                    res = any(res)
                if res:
                    smallest = sub
            return smallest
        return item
def VY_oct(item):
    return {
        Number: lambda: oct(item)[2:],
        str: lambda: (lambda: item, lambda: oct(int(item)))[item.isnumeric()]()[2:]
    }.get(VY_type(item), lambda:vectorise(VY_oct, item))()
def VY_print(item, end="\n", raw=False):
    global output
    t_item = type(item)
    if t_item is Generator:
        item._print(end)
    
    elif t_item is list:
        VY_print("⟨", "", False)
        if item:
            for value in item[:-1]:
                VY_print(value, "|", True)
            VY_print(item[-1], "", True)
        VY_print("⟩", end, False)
    else:
        if t_item is int and keg_mode:
            item = chr(item)
        if raw:
            if online_version:
                output[1] += VY_repr(item) + end
            else:
                print(VY_repr(item), end=end)
        else:
            if online_version:
                output[1] += VY_str(item) + end
            else:
                print(VY_str(item), end=end)
    if online_version and len(output) > ONE_TWO_EIGHT_KB:
        exit()
def VY_sorted(vector, fn=None):
    if fn is not None and type(fn) is not Function:
        return inclusive_range(vector, fn)
    t_vector = type(vector)
    vector = iterable(vector, str)
    if t_vector is Generator:
        vector = vector.gen
    if fn:
        sorted_vector = sorted(vector, key=lambda x: fn([x]))
    else:
        sorted_vector = sorted(vector)


    return {
        int: lambda: int("".join(map(str, sorted_vector))),
        float: lambda: float("".join(map(str, sorted_vector))),
        str: lambda: "".join(map(str, sorted_vector))
    }.get(t_vector, lambda: Generator(sorted_vector))()
def VY_range(item, start=0, lift_factor=0):
    t_item = VY_type(item)
    if t_item == Number:
        if item < 0:
            return range(start, int(item) + lift_factor, -1)
        return range(start, int(item) + lift_factor)
    return item
def VY_reduce(fn, vector):
    t_type = VY_type(vector)
    if type(fn) != Function:
        return [vector, vectorise(reverse, fn)]
    if t_type is Generator: return Generator(vector)._reduce(fn)
    if t_type is Number:
        vector = range(MAP_START, int(vector) + MAP_OFFSET)
    vector = vector[::-1]
    working_value = pop(vector)
    vector = vector[::-1]

    for item in vector:
        working_value = fn([working_value, item], arity=2)[-1]

    return [working_value]
def VY_repr(item):
    t_item = VY_type(item)
    return {
        Number: lambda x: str(x),
        list: lambda x: "⟨" + "|".join([str(VY_repr(y)) for y in x]) + "⟩",
        Generator: lambda x: VY_repr(x._dereference()),
        str: lambda x: "`" + x + "`",
        Function: lambda x: "@FUNCTION:" + x.__name__
    }[t_item](item)
def VY_round(item):
    t_item = VY_type(item)
    if t_item == Number:
        return round(item)

    elif t_item is str:
        return [item[n:] for n in range(len(item) - 1, -1, -1)]
    return vectorise(VY_round, item)
def VY_str(item):  
    t_item = VY_type(item)
    return {
        Number: lambda x: str(x),
        str: lambda x: x,
        list: lambda x: "⟨" + "|".join([VY_repr(y) for y in x]) + "⟩",
        Generator: lambda x: VY_str(x._dereference()),
        Function: lambda x: "@FUNCTION:" + x.__name__
    }[t_item](item)
def VY_type(item):
    ty = type(item)
    if ty in [int, float, complex]:
        return Number
    return ty
def VY_zip(lhs, rhs):
    ind = 0
    if type(lhs) in [list, str]: lhs = iter(lhs)
    if type(rhs) in [list, str]: rhs = iter(rhs)
    while True:
        exhausted = 0
        try:
            l = next(lhs)
        except:
            l = 0
            exhausted += 1

        try:
            r = next(rhs)
        except:
            r = 0
            exhausted += 1
        if exhausted == 2:
            break
        else:
            yield [l, r]

        ind += 1
def VY_zipmap(fn, vector):
    if type(fn) is not Function:
        return [fn, VY_zip(vector, vector)]
    t_vector = VY_type(vector)
    if t_vector is Generator:
        orig = copy.deepcopy(vector)
        new = Generator(vector._map(fn))
        return Generator(orig.zip_with(new))
    if t_vector == Number:
        vector = range(MAP_START, int(vector) + MAP_OFFSET)

    ret = []
    for item in vector:
        ret.append([item, fn([item])[-1]])

    return [ret]

constants = {
    "A": "string.ascii_uppercase",
    "e": "math.e",
    "f": "'Fizz'",
    "b": "'Buzz'",
    "F": "'FizzBuzz'",
    "H": "'Hello, World!'",
    "h": "'Hello World'",
    "1": "1000",
    "2": "10000",
    "3": "100000",
    "4": "1000000",
    "5": "10000000",
    "a": "string.ascii_lowercase",
    "L": "string.ascii_letters",
    "d": "string.digits",
    "6": "'0123456789abcdef'",
    "^": "'0123456789ABCDEF'",
    "o": "string.octdigits",
    "p": "string.punctuation",
    "P": "string.printable",
    "w": "string.whitespace",
    "r": "string.digits + string.ascii_letters",
    "B": "string.ascii_uppercase + string.ascii_lowercase",
    "Z": "string.ascii_uppercase[::-1]",
    "z": "string.ascii_lowercase[::-1]",
    "l": "string.ascii_letters[::-1]",
    "i": "math.pi",
    "n": "math.nan",
    "t": "math.tau",
    "D": "date.today().isoformat()",
    "N": "[dt.now().hour, dt.now().minute, dt.now().second]",
    "ḋ": "date.today().strftime('%d/%m/%Y')",
    "Ḋ": "date.today().strftime('%m/%d/%y')",
    "ð": "[date.today().day, date.today().month, date.today().year]",
    "β": "'{}[]<>()'",
    "Ḃ": "'()[]{}'",
    "ß": "'()[]'",
    "ḃ": "'([{'",
    "≥": "')]}'",
    "≤": "'([{<'",
    "Π": "')]}>'",
    "v": "'aeiou'",
    "V": "'AEIOU'",
    "∨": "'aeiouAEIOU'",
    "⟇": "commands.codepage",
    "½": "[1, 2]",
    "ḭ": "2 ** 32",
    "+": "[1, -1]",
    "-": "[-1, 1]",
    "≈": "[0, 1]",
    "/": "'/\\\\'",
    "R": "360",
    "W": "'https://'",
    "℅": "'http://'",
    "↳": "'https://www.'",
    "²": "'http://www.'",
    '"': "16",
    "∴": "32",
    "…": "64",
    "¶": "512",
    "⁋": "1024",
    "¦": "2048",
    "Ṅ": "4096",
    "ṅ": "8192",
    "¡": "16384",
    "ε": "32768",
    "₴": "65536",
    "×": "2147483648",
    "⁰": "'bcfghjklmnpqrstvwxyz'",
    "¹": "'bcfghjklmnpqrstvwxz'",
    "□": "string.printable",
    "•": "['qwertyuiop', 'asdfghjkl', 'zxcvbnm']",
    "Ṡ": "dt.now().second",
    "Ṁ": "dt.now().minute",
    "Ḣ": "dt.now().hour",
    "τ": "int(dt.now().strftime('%j'))",
    "ṡ": "time.time()"
}

def VY_compile(source, header=""):
    if not source: return header or "pass"
    source = VyParse.Tokenise(VyParse.group_two_bytes(VyParse.group_strings(source)))
    compiled = ""
    for token in source:
        NAME, VALUE = token[VyParse.NAME], token[VyParse.VALUE]
        # print(NAME, VALUE)
        if NAME == VyParse.NO_STMT:
            compiled += commands.command_dict.get(VALUE, "  ")[0]
        elif NAME == VyParse.INTEGER:
            compiled += f"stack.append({VALUE})"
        elif NAME == VyParse.STRING_STMT:
            import utilities
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

                compiled += "def FN_" + function_name + "(parameter_stack, arity=None):" + NEWLINE
                compiled += tab("global context_level, context_values, input_level, input_values, retain_items, printed") + NEWLINE
                compiled += tab("context_level += 1") + NEWLINE
                compiled += tab("input_level += 1") + NEWLINE
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
                    compiled += NEWLINE

                compiled += tab("stack = parameters[::]") + NEWLINE
                compiled += tab("input_values[input_level] = [stack[::], 0]") + NEWLINE
                compiled += tab(VY_compile(VALUE[VyParse.FUNCTION_BODY])) + NEWLINE
                compiled += tab("context_level -= 1; context_values.pop()") + NEWLINE
                compiled += tab("input_level -= 1") + NEWLINE
                compiled += tab("return stack")
        elif NAME == VyParse.LAMBDA_STMT:
            defined_arity = 1
            if VyParse.LAMBDA_ARGUMENTS in VALUE:
                lambda_argument = VALUE[VyParse.LAMBDA_ARGUMENTS]
                if lambda_argument.isnumeric():
                    defined_arity = int(lambda_argument)

            compiled += "def _lambda(parameter_stack, arity=-1):" + NEWLINE
            compiled += tab("global context_level, context_values, input_level, input_values, retain_items, printed") + NEWLINE
            compiled += tab("context_level += 1") + NEWLINE
            compiled += tab("input_level += 1") + NEWLINE
            compiled += tab(f"if arity != {defined_arity} and arity >= 0: parameters = pop(parameter_stack, arity); stack = parameters[::]") + NEWLINE
            if defined_arity == 1:
                compiled += tab(f"else: parameters = pop(parameter_stack); stack = [parameters]") + NEWLINE
            else:
                compiled += tab(f"else: parameters = pop(parameter_stack, {defined_arity}); stack = parameters[::]") + NEWLINE
            compiled += tab("context_values.append(parameters);") + NEWLINE
            compiled += tab("input_values[input_level] = [stack[::], 0]") + NEWLINE
            compiled += tab(VY_compile(VALUE[VyParse.LAMBDA_BODY])) + NEWLINE
            compiled += tab("ret = [pop(stack)]") + NEWLINE
            compiled += tab("context_level -= 1; context_values.pop()") + NEWLINE
            compiled += tab("input_level -= 1;") + NEWLINE
            compiled += tab("return ret") + NEWLINE
            compiled += "stack.append(_lambda)"
        elif NAME == VyParse.LIST_STMT:
            compiled += "temp_list = []" + NEWLINE
            for element in VALUE[VyParse.LIST_ITEMS]:
                if element:
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
        elif NAME == VyParse.VECTORISATION_CHAR:
            compiled += VY_compile("λ" + VALUE + ";") + NEWLINE
            m = commands.command_dict.get(VALUE, "\n\n")[1]
            if m == 0:
                compiled += "fn = pop(stack); stack += fn(stack)"
            elif m == 1:
                compiled += "fn = pop(stack); stack.append(vectorise(fn, pop(stack)))"
            elif m == 2:
                compiled += "fn = pop(stack); rhs, lhs = pop(stack, 2); stack.append(vectorise(fn, lhs, rhs))"
        elif NAME == VyParse.CODEPAGE_INDEX:
            compiled += f"stack.append({commands.codepage.find(VALUE)} + 101)"
        elif NAME == VyParse.TWO_BYTE_MATH:
            compiled += commands.math_command_dict.get(VALUE, "  ")[0]
        elif NAME == VyParse.TWO_BYTE_STRING:
            compiled += commands.string_command_dict.get(VALUE, "  ")[0]
        elif NAME == VyParse.TWO_BYTE_LIST:
            compiled += commands.list_command_dict.get(VALUE, "  ")[0]
        elif NAME == VyParse.TWO_BYTE_MISC:
            compiled += commands.misc_command_dict.get(VALUE, "  ")[0]
        elif NAME == VyParse.SINGLE_SCC_CHAR:
            import utilities
            import encoding
            if -1 < utilities.to_ten(VALUE, encoding.compression) < len(words._words):
                compiled += f"stack.append({repr(words.extract_word(VALUE))})"
            else:
                compiled += f"stack.append({repr(VALUE)})"

        elif NAME == VyParse.VARIABLE_SET:
            compiled += "VAR_" + VALUE[VyParse.VARIABLE_NAME] + " = pop(stack)"
        elif NAME == VyParse.VARIABLE_GET:
            compiled += "stack.append(VAR_" + VALUE[VyParse.VARIABLE_NAME] + ")"
        elif NAME == VyParse.COMPRESSED_NUMBER:
            import utilities, encoding
            number = utilities.to_ten(VALUE[VyParse.COMPRESSED_NUMBER_VALUE],
             encoding.codepage_number_compress)
            compiled += f"stack.append({number})" + NEWLINE
        elif NAME == VyParse.COMPRESSED_STRING:
            import utilities, encoding
            string = utilities.to_ten(VALUE[VyParse.COMPRESSED_STRING_VALUE],
             encoding.codepage_string_compress)
            string = utilities.from_ten(string, utilities.base27alphabet)
            compiled += f"stack.append('{string}')" + NEWLINE
        elif NAME == VyParse.PARA_APPLY:
            compiled += "temp_stack = stack[::]" + NEWLINE
            compiled += commands.command_dict.get(VALUE[0], "  ")[0] + NEWLINE
            compiled += "def _para_lambda(stack):" + NEWLINE
            compiled += tab(commands.command_dict.get(VALUE[1], "  ")[0]) + NEWLINE
            compiled += tab("return stack") + NEWLINE
            compiled += "stack.append(_para_lambda(temp_stack)[-1])"
        elif NAME == VyParse.PARA_APPLY_COLLECT:
            compiled += "temp_stack = stack[::]" + NEWLINE
            compiled += commands.command_dict.get(VALUE[0], "  ")[0] + NEWLINE
            compiled += "def _para_lambda(stack):" + NEWLINE
            compiled += tab(commands.command_dict.get(VALUE[1], "  ")[0]) + NEWLINE
            compiled += tab("return stack") + NEWLINE
            compiled += "stack.append(_para_lambda(temp_stack)[-1])" + NEWLINE
            compiled += "rhs, lhs = pop(stack, 2); stack.append([lhs, rhs])"
        elif NAME == VyParse.REGISTER_MODIFIER:
            compiled += "stack.append(register)" + NEWLINE
            built_in = commands.command_dict[VALUE]
            if built_in[1] > 1:
                compiled += commands.command_dict["$"][0] + NEWLINE
            compiled += built_in[0] + NEWLINE
            compiled += "register = pop(stack)"
        elif NAME == VyParse.ONE_CHAR_FUNCTION_REFERENCE:
            compiled += VY_compile("λ" + str(commands.command_dict[VALUE][1]) + "|" + VALUE)
        elif NAME == VyParse.DONT_POP:
            compiled += "retain_items = True" + NEWLINE
            compiled += VY_compile(VALUE) + NEWLINE
            compiled += "retain_items = False"
        elif NAME == VyParse.CONDITIONAL_EXECUTION:
            compiled += "if bool(pop(stack)):" + NEWLINE
            compiled += tab(VY_compile(VALUE))
        compiled += NEWLINE
    return header + compiled

def execute(code, flags, input_list, output_variable):
    global stack, register, printed, output, MAP_START, MAP_OFFSET
    global _join, _vertical_join, use_encoding, input_level, online_version
    global inputs, reverse_args, keg_mode, number_iterable
    online_version = True
    output = output_variable
    output[1] = ""
    output[2] = ""
    flags = flags

    if input_list:
        eval_function = VY_eval
        if 'Ṡ' in flags: eval_function = str
        inputs = list(map(eval_function, input_list.split("\r\n")))

    if 'a' in flags:
        inputs = [inputs]

    if flags:
        if 'H' in flags:
            stack = [100]
        if "M" in flags:
            MAP_START = 0

        if "m" in flags:
            MAP_OFFSET = 0
        
        if "Ṁ" in flags:
            MAP_START = 0
            MAP_OFFSET = 0

        if 'j' in flags:
            _join = True

        if 'L' in flags:
            _vertical_join = True

        if 'v' in flags:
            use_encoding = True
        
        if 'r' in flags:
            reverse_args = True
        
        if "K" in flags:
            keg_mode = True
        
        if 'R' in flags:
            number_iterable = range
        
        if 'h' in flags:
            output[1] = """
ALL flags should be used as is (no '-' prefix)
\tH\tPreset stack to 100
\tj\tPrint top of stack joined by newlines on end of execution
\tL\tPrint top of stack joined by newlines (Vertically) on end of execution
\ts\tSum/concatenate top of stack on end of execution
\tM\tMake implicit range generation start at 0 instead of 1
\tm\tMake implicit range generation end at n-1 instead of n
\tv\tUse Vyxal encoding for input file
\tc\tOutput compiled code
\tf\tGet input from file instead of arguments
\ta\tTreat newline seperated values as a list
\td\tPrint deep sum of top of stack on end of execution
\tr\tMakes all operations happen with reverse arguments
\tS\tPrint top of stack joined by spaces on end of execution
\tC\tCentre the output and join on newlines on end of execution
\tO\tDisable implicit output
\tK\tEnable Keg mode (input as ordinal values and integers as characters when outputting)
\tl\tPrint length of top of stack on end of execution
\tG\tPrint the maximum item of the top of stack on end of execution
\tg\tPrint the minimum item of the top of the stack on end of execution
\tW\tPrint the entire stack on end of execution
\tṠ\tTreat all inputs as strings (usually obtainable by wrapping in quotations)
\tR\tTreat numbers as ranges if ever used as an iterable
\tṪ\tPrint the sum of the entire stack
\t5\tMake the interpreter timeout after 5 seconds
\tT\tMake the interpreter timeout after 10 seconds
\tb\tMake the interpreter timeout after 15 seconds
\tB\tMake the interpreter timeout after 30 seconds
\tṀ\tEquivalent to having both m and M flags
"""
            return
    input_values[0] = [inputs, 0]
    code = VY_compile(code, "global stack, register, printed, output, MAP_START, MAP_OFFSET, _join, _vertical_join, use_encoding, input_level, retain_items, reverse_args\n")
    context_level = 0
    if flags and 'c' in flags:
        output[2] = code

    try:
        exec(code, globals())
    except Exception as e:
        output[2] += "\n" + str(e.args[0])

    if not printed and ("O" not in flags):
        if flags and 's' in flags:
            VY_print(summate(pop(stack)))
        elif flags and 'd' in flags:
            VY_print(summate(flatten(pop(stack))))
        elif flags and 'Ṫ' in flags:
            VY_print(summate(stack))
        elif flags and "S" in flags:
            VY_print(" ".join([str(n) for n in pop(stack)]))
        elif flags and "C" in flags:
            VY_print("\n".join(centre(pop(stack))))
        elif flags and "l" in flags:
            VY_print(len(pop(stack)))
        elif flags and "G" in flags:
            VY_print(VY_max(pop(stack)))
        elif flags and "g" in flags:
            VY_print(VY_min(pop(stack)))
        elif flags and "W" in flags:
            VY_print(stack)
        elif _vertical_join:
            VY_print(vertical_join(pop(stack)))
        elif _join:
            VY_print("\n".join([str(n) for n in pop(stack)]))
        else:
            VY_print(pop(stack))


if __name__ == "__main__":
    ### Debugging area
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
            eval_function = VY_eval
            if "Ṡ" in flags:
                eval_function = str
            if 'H' in flags:
                stack = [100]
            if 'f' in flags:
                inputs = list(map(eval_function, open(sys.argv[3]).readlines()))
            else:
                inputs = list(map(eval_function,sys.argv[3:]))

        if 'a' in flags:
            inputs = [inputs]
        
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
        print("\tH\tPreset stack to 100")
        print("\tj\tPrint top of stack joined by newlines")
        print("\tL\tPrint top of stack joined by newlines (Vertically)")
        print("\ts\tSum/concatenate top of stack on end of execution")
        print("\tM\tMake implicit range generation start at 0 instead of 1")
        print("\tm\tMake implicit range generation end at n-1 instead of n")
        print("\tv\tUse Vyxal encoding for input file")
        print("\tc\tOutput compiled code")
        print("\tf\tGet input from file instead of arguments")
        print("\ta\tTreat newline seperated values as a list")
        print("\td\tDeep sum of top of stack")
        print("\tr\tMakes all operations happen with reverse arguments")
        print("\tS\tPrint top of stack joined by spaces")
        print("\tC\tCentre the output and join on newlines")
        print("\tO\tDisable implicit output")
        print("\tK\tEnable Keg mode")
        print("\tE\tEnable safe evaluation (offline interpreter only)")
        print("\tl\tPrint length of top of stack")
        print("\tG\tPrint the maximum item of the top of stack on end of execution")
        print("\tg\tPrint the minimum item of the top of the stack on end of execution")
        print("\tW\tPrint the entire stack on end of execution")
        print("\tṠ\tTreat all inputs as strings")
        print("\tR\tTreat numbers as ranges if ever used as an iterable")
        print("\tṪ\tPrint the sum of the entire stack")
        print("\tṀ\tEquivalent to having both m and M flags")
    else:
        if flags:
            if "M" in flags:
                MAP_START = 0

            if "m" in flags:
                MAP_OFFSET = 0
            
            if "Ṁ" in flags:
                MAP_START = 0
                MAP_OFFSET = 0

            if 'j' in flags:
                _join = True

            if 'L' in flags:
                _vertical_join = True

            if 'v' in flags:
                use_encoding = True
            
            if 'r' in flags:
                reverse_args = True
            
            if 'K' in flags:
                keg_mode = True
            
            if 'E' in flags:
                safe_mode = True
            
            if 'H' in flags:
                header = "stack = [100]\nregister = 0\nprinted = False\n"
            
            if 'R' in flags:
                number_iterable = range

        # Encoding method thanks to Adnan (taken from the old 05AB1E interpreter)
        if use_encoding:
            import encoding
            code = open(file_location, "rb").read()
            code = encoding.vyxal_to_utf8(code)
        else:
            code = open(file_location, "r", encoding="utf-8").read()
        input_values[0] = [inputs, 0]
        code = VY_compile(code, header)
        context_level = 0
        if flags and 'c' in flags:
            print(code)
        exec(code)

        if not printed and ("O" not in flags):
            if flags and 's' in flags:
                print(summate(pop(stack)))
            elif flags and 'd' in flags:
                print(summate(flatten(pop(stack))))
            elif flags and 'Ṫ' in flags:
                VY_print(summate(stack))
            elif flags and "S" in flags:
                print(" ".join([VY_str(n) for n in pop(stack)]))
            elif flags and "C" in flags:
                print("\n".join(centre(pop(stack))))
            elif flags and "l" in flags:
                print(len(pop(stack)))
            elif flags and "G" in flags:
                print(VY_max(pop(stack)))
            elif flags and "g" in flags:
                print(VY_min(pop(stack)))
            elif flags and "W" in flags:
                print(VY_str(stack))
            elif _vertical_join:
                print(vertical_join(pop(stack)))
            elif _join:
                print("\n".join([VY_str(n) for n in pop(stack)]))
            else:
                VY_print(pop(stack))
