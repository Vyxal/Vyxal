"""This is where the element functions are stored

(that is, functions directly corresponding to Vyxal elements). It's also where
the python equivalent of command is stored
"""

from functools import reduce
import itertools
import math
import random
import string
import types
from typing import Union

import numpy
import sympy

from vyxal.context import DEFAULT_CTX, Context
from vyxal.helpers import *
from vyxal.LazyList import LazyList, lazylist, vyxalify

NUMBER_TYPE = "number"
SCALAR_TYPE = "scalar"


def process_element(
    expr: Union[str, types.FunctionType], arity: int
) -> tuple[str, int]:
    """Take a python expression and adds boilerplate for element functions to it

    expr can be a string, which will be added verbatim to the transpiled output,
    or a function, for which a function call will be generated.

    See documents/specs/Transpilation.md for information on what happens here.
    """
    if arity:
        arguments = ["third", "rhs", "lhs"][-arity:]
    else:
        arguments = "_"

    if isinstance(expr, types.FunctionType):
        pushed = f"{expr.__name__}({', '.join(arguments[::-1])}, ctx=ctx)"
    else:
        pushed = expr
    py_code = (
        f"{', '.join(arguments)} = pop(stack, {arity}, ctx); "
        f"stack.append({pushed})"
    )
    return py_code, arity


def add(lhs, rhs, ctx):
    """Element +
    (num, num) -> lhs + rhs
    (num, str) -> str(lhs) + rhs
    (str, num) -> lhs + str(rhs)
    (str, str) -> lhs + rhs
    """

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: lhs + rhs,
        (NUMBER_TYPE, str): lambda: str(lhs) + rhs,
        (str, NUMBER_TYPE): lambda: lhs + str(rhs),
        (str, str): lambda: lhs + rhs,
    }.get(ts, lambda: vectorise(add, lhs, rhs, ctx=ctx))()


def boolify(lhs, ctx):
    """Element ḃ
    (any) -> is truthy?
    """

    return int(bool(lhs))


def chr_ord(lhs, ctx):
    """Element C
    (num) -> chr(a)
    (str) -> ord(a)
    """

    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: chr(int(lhs)),
        (str): lambda: list(map(ord, lhs)) if len(lhs) > 1 else ord(lhs),
    }.get(ts, lambda: vectorise(chr_ord, lhs, ctx=ctx))()


def combinations_with_replacement(lhs, rhs, ctx):
    """Element ↔
    (any, num) -> combinations of lhs of length rhs with replacement
    (any, non-num) -> remove elements in lhs that are not in rhs
    (fun, any) -> apply lhs on rhs until the result does not change. Collects intermittent values
    (any, fun) -> apply rhs on lhs until the result does not change. Collects intermittent values
    """

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, ts[1]): lambda: LazyList(
            itertools.product(iterable(rhs, ctx), repeat=lhs)
        ),
        (ts[0], NUMBER_TYPE): lambda: LazyList(
            itertools.product(iterable(lhs, ctx), repeat=rhs)
        ),
        (types.FunctionType, ts[1]): lambda: fixed_point(lhs, rhs),
        (ts[0], types.FunctionType): lambda: fixed_point(rhs, lhs),
    }.get(ts, lambda: keep(lhs, rhs))()


def complement(lhs, ctx):
    """Element ⌐
    (num) -> 1 - a
    (str) -> a.split(",")
    """
    ts = vy_type(lhs)
    return {NUMBER_TYPE: lambda: 1 - lhs, str: lambda: lhs.split(",")}.get(
        ts, lambda: vectorise(complement, lhs, ctx=ctx)
    )()


def contains(lhs, rhs, ctx):
    """Element c
    (any, any) -> count of a in b
    """

    lhs = iterable(lhs, ctx=ctx)
    for item in lhs:
        if item == rhs:
            return 1
    return 0


def count(lhs, rhs, ctx):
    """Element O
    (any, any) -> returns the number of occurances of b in a
    """

    return iterable(lhs, ctx=ctx).count(rhs)


def decrement(lhs, ctx):
    """Element ‹
    (num) -> a - 1
    (str) -> a + "-"
    """

    ts = vy_type(lhs)
    return {NUMBER_TYPE: lambda: lhs - 1, str: lambda: lhs + "-"}.get(
        ts, lambda: vectorise(decrement, lhs, ctx=ctx)
    )()


def deep_flatten(lhs, ctx):
    """Element f
    (any) -> flatten list
    """
    ret = []
    for item in iterable(lhs):
        if type(item) in (LazyList, list):
            ret += deep_flatten(item, ctx)
        else:
            ret.append(item)
    return ret


def divide(lhs, rhs, ctx):
    """Element /
    (num, num) -> a / b
    (num, str) -> b split into a even length pieces, possibly with an extra part
    (str, num) -> a split into b even length pieces, possibly with an extra part
    (str, str) -> split a on b
    """

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: vyxalify(sympy.Rational(lhs, rhs)),
        (NUMBER_TYPE, str): lambda: wrap(rhs, len(rhs) // lhs),
        (str, NUMBER_TYPE): lambda: wrap(lhs, len(lhs) // rhs),
        (str, str): lambda: lhs.split(rhs),
    }.get(ts, lambda: vectorise(divide, lhs, rhs, ctx=ctx))()


def divisors(lhs, ctx):
    """Element K
    (num) -> divisors(a) # Factors or divisors of a
    (str) -> all substrings of a that occur more than once # they "divide" a into more than one piece
    (lst) -> prefixes(a) # Prefixes of a
    """

    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: sympy.divisors(lhs),
        (str): lambda: LazyList(
            filter(
                lambda x: len(lhs.split(x)) == 2 and all(lhs.split(x)),
                substrings(lhs, ctx),
            )
        ),
    }.get(ts, lambda: LazyList((lhs[: x + 1] for x in range(len(x)))))()


def dyadic_maximum(lhs, rhs, ctx):
    """Element ∴
    (any, any) -> max(a, b)
    """

    return lhs if greater_than(lhs, rhs, ctx) else rhs


def dyadic_minimum(lhs, rhs, ctx):
    """Element ∵
    (any, any) -> min(a, b)
    """

    return lhs if less_than(lhs, rhs, ctx) else rhs


def exclusive_one_range(lhs, ctx):
    """Element ɽ
    (num) -> range(1, a)
    (str) -> a.lower()
    """

    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: LazyList(range(1, int(lhs))),
        str: lambda: lhs.lower(),
    }.get(ts, lambda: vectorise(exclusive_one_range, lhs, ctx=ctx))


def exclusive_zero_range(lhs, ctx):
    """Element ʁ
    (num) -> range(0, a)
    (str) -> mirror(a)
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: LazyList(range(0, int(lhs))),
        str: lambda: merge(lhs, reverse(lhs, ctx)[1:], ctx),
    }.get(ts, lambda: vectorise(exclusive_zero_range, lhs, ctx=ctx))()


def exp2_or_eval(lhs, ctx):
    """Element E
    (num) -> 2 ** a
    (str) -> eval(a)
    """
    ts = vy_type(lhs)

    return {
        NUMBER_TYPE: lambda: 2 ** lhs,
        str: lambda: vy_eval(lhs, ctx),
    }.get(ts, lambda: vectorise(exp2_or_eval, lhs, ctx=ctx))()


def exponent(lhs, rhs, ctx):
    """Element e
    (num, num) -> a ** b (exponentiation)
    (str, num) -> every bth character of a
    (num, str) -> every ath character of b
    (str, str) -> regex.search(pattern=a, string=b).span() (Length of regex match)
    """

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: lhs ** rhs,
        (NUMBER_TYPE, str): lambda: rhs[:: int(lhs)],
        (str, NUMBER_TYPE): lambda: lhs[:: int(rhs)],
        (str, str): lambda: list(re.search(lhs, rhs).span()),
    }.get(ts, lambda: vectorise(exponent, lhs, rhs, ctx=ctx))()


def find(lhs, rhs, ctx):
    """Element ḟ
    (any, any) -> a.find(b)
    (any, fun) -> truthy indices of mapping b over a
    """

    ts = vy_type(lhs, rhs)
    if types.FunctionType not in ts:
        return iterable(lhs, ctx=ctx).find(rhs)
    else:
        return {
            (ts[0], types.FunctionType): lambda: LazyList(
                (
                    i
                    for i in range(len(iterable(lhs, ctx=ctx)))
                    if safe_apply(rhs, iterable(lhs, ctx=ctx)[i], ctx=ctx)
                )
            ),
            (types.FunctionType, ts[1]): lambda: LazyList(
                (
                    i
                    for i in range(len(iterable(rhs, ctx=ctx)))
                    if safe_apply(lhs, iterable(rhs, ctx=ctx)[i], ctx=ctx)
                )
            ),
        }.get(ts)()


def function_call(lhs, ctx):
    """Element †
    (fun) -> lhs()
    (num) -> count of prime factors
    (str) -> vyxal exec lhs
    (lst) -> vectorised not
    """

    # Modifies lhs, because lhs = stack
    top = pop(lhs, 1, ctx=ctx)
    ts = vy_type(top, simple=True)
    if isinstance(top, types.FunctionType):
        lhs += wrapify(top(lhs, top, ctx=ctx))
        return None
    else:
        return {
            NUMBER_TYPE: lambda: len(prime_factors(top, ctx)),
            str: lambda: exec(lhs) or [],
            list: lambda: vectorised_not(top, ctx),
        }.get(ts)()


def from_base(lhs, rhs, ctx):
    """Element β
    Convert lhs from base rhs to base 10
    """

    ts = vy_type(lhs, rhs)
    if ts == (str, str):
        return from_base([rhs.find(x) + 1 for x in lhs], len(rhs), ctx)
    elif ts[-1] == NUMBER_TYPE:
        lhs = [chr(x) if type(x) is str else x for x in iterable(lhs)]
        exponents = list(exponent(rhs, list(range(0, len(lhs))), ctx))[::-1]
        return sum(multiply(lhs, exponents, ctx))


def equals(lhs, rhs, ctx):
    """Element =
    (num, num) -> lhs == rhs
    (num, str) -> str(lhs) == rhs
    (str, num) -> lhs == str(rhs)
    (str, str) -> lhs == rhs
    """

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: int(lhs == rhs),
        (NUMBER_TYPE, str): lambda: int(str(lhs) == rhs),
        (str, NUMBER_TYPE): lambda: int(lhs == str(rhs)),
        (str, str): lambda: int(lhs == rhs),
    }.get(ts, lambda: vectorise(equals, lhs, rhs, ctx=ctx))()


def greater_than(lhs, rhs, ctx):
    """Element <
    (num, num) -> a > b
    (num, str) -> str(a) > b
    (str, num) -> a > str(b)
    (str, str) -> a > b
    """

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: lhs > rhs,
        (NUMBER_TYPE, str): lambda: str(lhs) > rhs,
        (str, NUMBER_TYPE): lambda: lhs > str(rhs),
        (str, str): lambda: lhs > rhs,
    }.get(ts, lambda: vectorise(greater_than, lhs, rhs, ctx=ctx))()


def halve(lhs, ctx):
    """Element ½
    (num) -> lhs / 2
    (str) -> a split into two strings of equal lengths (as close as possible)
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.Rational(lhs, 2),
        str: lambda: wrap(lhs, math.ceil(len(lhs) / 2)),
    }.get(ts, lambda: vectorise(halve, lhs, ctx=ctx))()


def head(lhs, ctx):
    """Element h
    (any) -> a[0]
    """
    return (
        iterable(lhs, ctx)[0]
        if len(iterable(lhs, ctx))
        else ""
        if type(lhs) is str
        else 0
    )


def inclusive_one_range(lhs, ctx):
    """Element ɾ
    (num) -> range(1, a + 1)
    (str) -> a.uppercase()
    """

    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: LazyList(range(1, int(lhs) + 1)),
        str: lambda: lhs.upper(),
    }.get(ts, lambda: vectorise(inclusive_one_range, lhs, ctx=ctx))()


def inclusive_zero_range(lhs, ctx):
    """Element ʀ
    (num) -> range(0, a + 1)
    (str) -> [char is alphabetical? for char in a]
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: LazyList(range(0, int(lhs) + 1)),
        str: lambda: scalarify(
            [int(char in string.ascii_letters) for char in lhs]
        ),
    }.get(ts, lambda: vectorise(inclusive_zero_range, lhs, ctx=ctx))()


def increment(lhs, ctx):
    """Element ›
    (num) -> lhs + 1
    (str) -> replace spaces with 0s
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: lhs + 1,
        str: lambda: lhs.replace(" ", "0"),
    }.get(ts, lambda: vectorise(increment, lhs, ctx=ctx))()


def index(lhs, rhs, ctx):
    """Element i
    (any, num) -> a[b] (Index)
    (str, str) -> enclose b in a # b[0:len(b)//2] + a + b[len(b)//2:]
    (any, [x]) -> a[:b] (0 to bth item of a)
    (any, [x,y]) -> a[x:y] (x to yth item of a)
    (any, [x,y,m]) -> a[x:y:m] (x to yth item of a, taking every mth)
    """

    ts = vy_type(lhs, rhs)
    if ts == (str, str):
        # b[0:len(b)//2] + a + b[len(b)//2:]
        return lhs[: len(rhs) // 2] + rhs + lhs[len(rhs) // 2 :]

    elif ts == (LazyList, NUMBER_TYPE):
        return lhs[int(rhs)]

    elif ts[-1] == NUMBER_TYPE:
        if len(iterable(lhs)):
            return iterable(lhs, ctx)[int(rhs) % len(iterable(lhs, ctx))]
        else:
            return "" if ts[0] is str else 0

    elif ts[-1] == str:
        return vectorise(index, lhs, rhs, ctx=ctx)

    else:
        return iterable(lhs, ctx)[slice(*rhs)]


def infinite_list(ctx):
    """Element ∞
    Yields a (lazy)list of positive integers
    """

    @lazylist
    def f():
        n = 1
        while n:
            yield n
            n += 1

    return f()


def infinite_replace(lhs, rhs, other, ctx):
    """Element ¢
    (any, any, any) -> replace b in a with c until a doesn't change
    """

    prev = deep_copy(lhs)
    while True:
        lhs = replace(lhs, rhs, other, ctx)
        if lhs == prev:
            break
        prev = deep_copy(lhs)

    return lhs


def interleave(lhs, rhs, ctx):
    """Element Y
    (any, any) -> interleave a and b
    """
    # Essentially, Zf but whatever

    lhs = iterable(lhs, ctx=ctx)
    rhs = iterable(rhs, ctx=ctx)

    @lazylist
    def f():
        for i in range(max(len(lhs), len(rhs))):
            if i < len(lhs):
                yield lhs[i]
            if i < len(rhs):
                yield rhs[i]

    if type(lhs) == type(rhs) == str:
        return "".join(f())
    else:
        return f()


def is_falsey(lhs, ctx):
    """Element ċ
    (any) -> a != 1
    """

    return vectorised_not(equals(lhs, 1, ctx=ctx))


def is_prime(lhs, ctx):
    """Element æ
    (num) -> is a prime?
    (str) -> case of a (1 if all letters in a are uppercase,
             0 if all letters in a are lowercase,
            -1 if mixed case)
    """

    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: int(sympy.ntheory.isprime(lhs)),
        str: lambda: case_of(lhs),
    }.get(ts, vectorise(is_prime, lhs, ctx=ctx))()


def join(lhs, rhs, ctx):
    """Element j
    (any, any) -> a.join(b)
    """

    return vy_str(rhs).join(map(vy_str, iterable(lhs, ctx=ctx)))


def log_mold_multi(lhs, rhs, ctx):
    """Element •
    (num, num) -> log_lhs(rhs)
    (num, str) -> [char * lhs for char in rhs]
    (str, num) -> [char * rhs for char in lhs]
    (str, str) -> lhs.with_capitalisation_of(rhs)
    (lst, lst) -> lhs molded to the shape of rhs
    """

    ts = vy_type(lhs, rhs, True)

    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: sympy.Rational(math.log(lhs, rhs)),
        (NUMBER_TYPE, str): lambda: "".join([char * lhs for char in rhs]),
        (str, NUMBER_TYPE): lambda: "".join([char * rhs for char in lhs]),
        (str, str): lambda: transfer_capitalisation(rhs, lhs),
        (list, list): lambda: mold(lhs, rhs),
    }.get(ts, lambda: vectorise(log_mold_multi, lhs, rhs, ctx=ctx))()


def length(lhs, ctx):
    """Element L
    (any) -> len(a)
    """

    return len(iterable(lhs, ctx=ctx))


def less_than(lhs, rhs, ctx):
    """Element <
    (num, num) -> a < b
    (num, str) -> str(a) < b
    (str, num) -> a < str(b)
    (str, str) -> a < b
    """

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: lhs < rhs,
        (NUMBER_TYPE, str): lambda: str(lhs) < rhs,
        (str, NUMBER_TYPE): lambda: lhs < str(rhs),
        (str, str): lambda: lhs < rhs,
    }.get(ts, lambda: vectorise(less_than, lhs, rhs, ctx=ctx))()


def max_by_tail(lhs, ctx):
    """Element ↑
    (any) -> max(a, key=lambda x: x[-1])
    """

    lhs = iterable(lhs, ctx=ctx)
    if len(lhs) == 0:
        return []
    elif len(lhs) == 1:
        return lhs[0]
    else:
        biggest = lhs[0]
        for item in lhs[1:]:
            if greater_than(tail(item, ctx), tail(biggest, ctx), ctx):
                biggest = item
        return biggest


def merge(lhs, rhs, ctx):
    """Element J
    (scl, scl) -> concatenate a and b
    (lst, scl) -> append b to a
    (scl, lst) -> prepend a to b
    (lst, lst) -> merged a and b
    """

    ts = vy_type(lhs, rhs, simple=True)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: vy_eval(
            str(lhs) + str(rhs), ctx=ctx
        ),
        (NUMBER_TYPE, str): lambda: add(lhs, rhs),
        (str, NUMBER_TYPE): lambda: add(lhs, rhs),
        (str, str): lambda: lhs + rhs,
        (list, ts[1]): lambda: lhs + [rhs],
        (ts[0], list): lambda: [lhs] + rhs,
        (list, list): lambda: lhs + rhs,
    }.get(ts)()


def min_by_tail(lhs, ctx):
    """Element ↓
    (any) -> min(a, key=lambda x: x[-1])
    """
    lhs = iterable(lhs, ctx=ctx)
    if len(lhs) == 0:
        return []
    elif len(lhs) == 1:
        return lhs[0]
    else:
        smallest = lhs[0]
        for item in lhs[1:]:
            if less_than(tail(item, ctx), tail(smallest, ctx), ctx):
                smallest = item
        return smallest


def mirror(lhs, ctx):
    """Element m
    (num) -> a + reversed(a) (as number)
    (str) -> a + reversed(a)
    (lst) -> Append reversed(a) to a
    """

    return add(lhs, reverse(lhs))


def modulo(lhs, rhs, ctx):
    """Element %
    (num, num) -> a % b
    (num, str) -> (b split into a equal pieces)[-1]
    (str, num) -> (a split into b equal pieces)[-1]
    (str, str) -> a.format(b)
    """

    ts = vy_type(lhs, rhs, simple=True)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: lhs % rhs,
        (NUMBER_TYPE, str): lambda: divide(rhs, lhs, ctx)[-1],
        (str, NUMBER_TYPE): lambda: divide(lhs, rhs, ctx)[-1],
        (str, str): lambda: format_string(lhs, [rhs]),
        (str, list): lambda: format_string(lhs, rhs),
    }.get(ts, lambda: vectorise(modulo, lhs, rhs, ctx=ctx))()


def monadic_maximum(lhs, ctx):
    """Element G
    (any) -> returns the maximal element of the input
    """

    lhs = deep_flatten(lhs, ctx)

    if len(lhs) == 0:
        return []

    elif len(lhs) == 1:
        return lhs[0]

    else:
        biggest = lhs[0]
        for item in lhs[1:]:
            if greater_than(item, biggest, ctx):
                biggest = item

        return item


def monadic_minimum(lhs, ctx):
    """Element g
    (any) -> smallest item of a
    """

    lhs = deep_flatten(lhs, ctx)
    if len(lhs) == 0:
        return []

    elif len(lhs) == 1:
        return lhs[0]

    else:
        smallest = lhs[0]
        for item in lhs[1:]:
            if less_than(item, smallest, ctx):
                smallest = item

        return item


def multiply(lhs, rhs, ctx):
    """Element *
    (num, num) -> a * b
    (num, str) -> repeat b a times
    (str, num) -> repeat a b times
    (str, str) -> ring translate b according to a
    """

    ts = vy_type(lhs, rhs)

    if ts[0] is types.FunctionType:
        lhs.stored_arity = rhs
        return lhs

    elif ts[1] is types.FunctionType:
        rhs.stored_arity = lhs
        return rhs
    else:
        return {
            (NUMBER_TYPE, NUMBER_TYPE): lambda: lhs * rhs,
            (NUMBER_TYPE, str): lambda: lhs * rhs,
            (str, NUMBER_TYPE): lambda: lhs * rhs,
            (str, str): lambda: ring_translate(lhs, rhs),
        }.get(ts, lambda: vectorise(multiply, lhs, rhs, ctx=ctx))()


def n_choose_r(lhs, rhs, ctx):
    """Element ƈ
    (num, num) -> n choose r
    (num, str) -> [random.choice(b) for _ in range(a)]
    (str, num) -> [random.choice(a) for _ in range(b)]
    (str, str) -> does a have the same characters as b
    """

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: scipy.special.comb(lhs, rhs),
        (NUMBER_TYPE, str): lambda: [
            random.choice(rhs) for _ in range(abs(int(lhs)))
        ],
        (str, NUMBER_TYPE): lambda: [
            random.choice(lhs) for _ in range(abs(int(rhs)))
        ],
        (str, str): lambda: int(set(lhs) == set(rhs)),
    }.get(ts, lambda: vectorise(n_choose_r, lhs, rhs, ctx=ctx))()


def negate(lhs, ctx):
    """Element N
    (num) -> -a
    (str) -> swapcase of a
    """

    ts = vy_type(lhs)
    return {(NUMBER_TYPE): lambda: -lhs, (str): lambda: lhs.swapcase()}.get(
        ts, lambda: vectorise(negate, lhs, ctx=ctx)
    )()


def orderless_range(lhs, rhs, ctx):
    """Element r
    (num, num) -> range(a,b) (Range form a to b)
    (num, str) -> append 0s to b to make it length a
    (str, num) -> preprend 0s to a to make it length b
    (any, fun) -> cumulative_reduce(a,function=b) (Prefixes of a reduced by b)
    (str, str) -> regex.has_match(pattern=a,string= b) ( Does b match a)
    """

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: LazyList(
            range(int(lhs), int(rhs), (-1, 1)[lhs < rhs])
        ),
        (NUMBER_TYPE, str): lambda: rhs + ("0" * abs(len(rhs) - lhs)),
        (NUMBER_TYPE, str): lambda: ("0" * abs(len(rhs) - lhs)) + lhs,
        (ts[0], types.FunctionType): lambda: scanl(
            multiply(rhs, 2, ctx), iterable(lhs, range, ctx=ctx), ctx=ctx
        ),
        (types.FunctionType, ts[1]): lambda: scanl(
            multiply(lhs, 2, ctx), iterable(rhs, range, ctx=ctx), ctx=ctx
        ),
        (str, str): lambda: int(re.compile(lhs).search(rhs)),
    }.get(ts, lambda: vectorise(orderless_range, lhs, rhs, ctx=ctx))()


def overlapping_groups(lhs, rhs, ctx):
    """Element l
    (any, num) -> Overlapping groups of a of length b
    (any, any) -> length(a) == length(b)
    """

    ts = vy_type(lhs, rhs)
    if vy_type(rhs) != NUMBER_TYPE:
        return len(iterable(lhs)) == len(rhs)
    iters = itertools.tee(iterable(lhs), rhs)
    for i in range(len(iters)):
        for j in range(i):
            next(iters[i], None)

    return LazyList(zip(*iters))


def parity(lhs, ctx):
    """Element ∷
    (num) -> parity of a
    (str) -> parity of a
    """

    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: int(lhs % 2),
        (str): lambda: divide(lhs, 2)[-1],
    }.get(ts, lambda: vectorise(parity, lhs, ctx=ctx))()


def prime_factors(lhs, ctx):
    """Element Ǐ
    (num) -> prime factors
    (str) -> lhs + lhs[0]"""
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.ntheory.primefactors(int(lhs)),
        str: lambda: lhs + lhs[0],
    }.get(ts, lambda: vectorise(prime_factors, lhs, ctx=ctx))()


def prepend(lhs, rhs, ctx):
    """Element p
    (any, any) -> a.prepend(b) (Prepend b to a)
    """

    ts = vy_type(lhs, rhs)
    return {(ts[0], ts[1]): lambda: merge(rhs, lhs, ctx=ctx)}.get(
        ts, lambda: [rhs] + lhs
    )()


def quotify(lhs, ctx):
    """Element q
    (any) -> ` + a + ` (Quotify a)
    """

    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: "`{}`".format(lhs),
        (str): lambda: "`{}`".format(lhs.replace("`", "\\`")),
        (types.FunctionType): lambda: "`{}`".format(lhs.__name__),
    }.get(ts, lambda: quotify(vy_str(lhs, ctx=ctx), ctx))()


def remove(lhs, rhs, ctx):
    """Element o
    (any, any) -> a.remove(b)
    """

    return replace(lhs, rhs, "", ctx)


def replace(lhs, rhs, other, ctx):
    """Element V
    (any, any, any) -> a.replace(b, c)
    """

    if vy_type(lhs, simple=True) is not list:
        return str(lhs).replace(str(rhs), str(other))
    else:
        return [other if value == rhs else value for value in iterable(lhs)]


def reverse(lhs, ctx):
    """Element Ṙ
    (any) -> a reversed
    """

    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: reverse_number(lhs),
        str: lambda: lhs[::-1],
        list: lambda: lhs[::-1],
        LazyList: lambda: lhs.reversed(),
    }.get(ts)()


def split_on(lhs, rhs, ctx):
    """
    Element €
    (num, num) -> str(lhs).split(rhs)
    (num, str) -> str(lhs).split(rhs)
    (str, num) -> lhs.split(str(rhs))
    (str, str) -> lhs.split(rhs)

    """
    if [primitive_type(lhs), primitive_type(rhs)] == [SCALAR_TYPE, SCALAR_TYPE]:
        return str(lhs).split(str(rhs))

    else:
        ret, temp = [], []
        for item in iterable(lhs, ctx=ctx):
            if item == rhs:
                ret.append(temp[::])
                temp = []
            else:
                temp.append(item)
        if temp:
            ret.append(temp)
        return ret


def strip(lhs, rhs, ctx):
    """Element P
    (any, any) -> a.strip(b)
    """

    def list_helper(left, right):
        """This doesn't make sense anywhere but here"""

        if vy_type(left) is LazyList:
            left = left.listify()
        if vy_type(right) is LazyList:
            right = right.listify()
        if len(left) == 0:
            return []  # how you gonna strip from nothing

        # Strip from the right side first
        # check to make sure there's stuff to strip

        if len(left) < len(right):
            # left is smaller than right
            # e.g. [1, 2, 3].strip([2, 3, 4, 5, 6])
            if left in (right[: len(left)], right[: len(left) : -1]):
                return []

        if left[-len(right) :] == right[::-1]:
            del left[-len(right) :]

        if left[: len(right)] == right:
            del left[: len(right)]

        return left

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: vy_eval(
            vy_str(lhs).strip(vy_str(rhs))
        ),
        (NUMBER_TYPE, str): lambda: vy_eval(vy_str(lhs).strip(rhs)),
        (str, NUMBER_TYPE): lambda: lhs.strip(str(rhs)),
        (str, str): lambda: lhs.strip(rhs),
    }.get(ts, lambda: list_helper(lhs, rhs))()


def substrings(lhs, ctx):
    """Element ǎ
    (num) -> ath prime
    (str) -> all substrings of a
    """

    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.ntheory.prime(int(lhs) + 1),
        str: lambda: LazyList(
            (
                lhs[i:j]
                for i in range(len(lhs) + 1)
                for j in range(1, len(lhs) + 1)
                if i < j
            )
        ),
    }.get(ts, lambda: vectorise(substrings, lhs, ctx=ctx))()


def subtract(lhs, rhs, ctx):
    """Element -
    (num, num) -> lhs - rhs
    (num, str) -> ("-" * lhs) + rhs
    (str, num) -> lhs + ("-" * rhs)
    (str, str) -> lhs.replace(rhs, "")
    """

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: lhs - rhs,
        (NUMBER_TYPE, str): lambda: ("-" * lhs) + rhs,
        (str, NUMBER_TYPE): lambda: lhs + ("-" * rhs),
        (str, str): lambda: lhs.replace(rhs, ""),
    }.get(ts, lambda: vectorise(subtract, lhs, rhs, ctx=ctx))()


def tail(lhs, ctx):
    """Element t
    (any) -> a[-1]
    """

    return (
        iterable(lhs, ctx)[-1]
        if len(iterable(lhs, ctx))
        else ""
        if type(lhs) is str
        else 0
    )


def to_base(lhs, rhs, ctx):
    """Element τ
    Convert lhs from base 10 to base rhs
    """

    if vy_type(lhs) is not NUMBER_TYPE:
        raise ValueError("to_base only works on numbers")

    if vy_type(rhs) == NUMBER_TYPE:
        rhs = list(range(0, int(rhs)))
    else:
        rhs = iterable(rhs, ctx=ctx)
    if len(rhs) == 1:
        maximal_exponent = lhs
    else:
        maximal_exponent = int(log_mold_multi(lhs, len(rhs), ctx))

    res = []
    for i in range(maximal_exponent, -1, -1):
        digit, remaining = divmod(lhs, len(rhs) ** i)
        res.append(index(rhs, digit, ctx))
        lhs = remaining

    return res


def truthy_indicies(lhs, ctx):
    """Element T
    (any) -> indicies of truthy elements
    """

    lhs = iterable(lhs, ctx=ctx)

    @lazylist
    def helper():
        for i in range(len(lhs)):
            if lhs[i]:
                yield i

    return helper()


def uninterleave(lhs, ctx):
    """- element: "y"
    name: Uninterleave
    description: Push every other item of a, and the rest.
    arity: 1
    overloads:
      any: a[::2], a[1::2] (Every second item, the rest)
    vectorise: false
    tests:
      - '["abcde"] : "bd"'
      - "[[1,2,3,4]] : [2,4]"
    """
    return [
        index(deep_copy(lhs), [None, None, 2], ctx),
        index(lhs, [1, None, 2], ctx),
    ]


def uniquify(lhs, ctx):
    """Element U
    (any) -> only unique items of a
    """

    @lazylist
    def f():
        seen = []
        for item in lhs:
            if item not in seen:
                yield item
                seen.append(item)

    return f()


def vectorise(function, lhs, rhs=None, other=None, explicit=False, ctx=None):
    """
    Maps a function over arguments
    Probably cursed but whatever.
    The explicit argument is mainly for stopping element-wise
    vectorisation happening.
    """

    if other is not None:
        # That is, three argument vectorisation
        # That is:

        ts = primitive_type(lhs), primitive_type(rhs), primitive_type(other)

        simple = {
            (SCALAR_TYPE, SCALAR_TYPE, SCALAR_TYPE): lambda: safe_apply(
                function, lhs, rhs, other, ctx=ctx
            ),
            (SCALAR_TYPE, SCALAR_TYPE, list): lambda: (
                safe_apply(function, lhs, rhs, x, ctx=ctx) for x in other
            ),
            (SCALAR_TYPE, list, SCALAR_TYPE): lambda: (
                safe_apply(function, lhs, x, other, ctx=ctx) for x in rhs
            ),
            (SCALAR_TYPE, list, list): lambda: (
                safe_apply(function, lhs, x, y, ctx=ctx)
                for x, y in vy_zip(rhs, other)
            ),
            (list, SCALAR_TYPE, SCALAR_TYPE): lambda: (
                safe_apply(function, x, rhs, other, ctx=ctx) for x in lhs
            ),
            (list, SCALAR_TYPE, list): lambda: (
                safe_apply(function, x, rhs, y, ctx=ctx)
                for x, y in vy_zip(lhs, other)
            ),
            (list, list, SCALAR_TYPE): lambda: (
                safe_apply(function, x, y, other, ctx=ctx)
                for x, y in vy_zip(lhs, rhs)
            ),
            (list, list, list): lambda: (
                safe_apply(function, x, y, z, ctx=ctx)
                for x, y in vy_zip(lhs, rhs)
                for z in other
            ),
        }

        if explicit:
            return LazyList(
                (
                    safe_apply(x, rhs, other, ctx=ctx)
                    for x in iterable(lhs, ctx=ctx)
                )
            )
        else:
            return LazyList(simple.get(ts)())
    elif rhs is not None:
        # That is, two argument vectorisation
        ts = primitive_type(lhs), primitive_type(rhs)
        simple = {
            (SCALAR_TYPE, SCALAR_TYPE): lambda: safe_apply(
                function, lhs, rhs, ctx=ctx
            ),
            (SCALAR_TYPE, list): lambda: (
                safe_apply(function, lhs, x, ctx=ctx) for x in rhs
            ),
            (list, SCALAR_TYPE): lambda: (
                safe_apply(function, x, rhs, ctx=ctx) for x in lhs
            ),
            (list, list): lambda: (
                safe_apply(function, x, y, ctx=ctx)
                for x, y in vy_zip(lhs, rhs, ctx=ctx)
            ),
        }

        explicit_dict = {
            (SCALAR_TYPE, SCALAR_TYPE): lambda: (
                safe_apply(function, x, rhs, ctx=ctx) for x in iterable(lhs)
            ),
            (SCALAR_TYPE, list): lambda: (
                safe_apply(function, lhs, x, ctx=ctx) for x in rhs
            ),
            (list, SCALAR_TYPE): lambda: (
                safe_apply(function, x, rhs, ctx=ctx) for x in lhs
            ),
            (list, list): lambda: (
                safe_apply(function, x, rhs, ctx=ctx) for x in lhs
            ),
        }

        if explicit:
            return LazyList(explicit_dict.get(ts)())
        else:
            return LazyList(simple.get(ts)())
    else:
        # That is, single argument vectorisation
        if explicit:
            lhs = iterable(lhs, range, ctx=ctx)
        else:
            lhs = iterable(lhs, ctx=ctx)

        return LazyList((safe_apply(function, x, ctx=ctx) for x in lhs))


def vectorised_not(lhs, ctx):
    return {NUMBER_TYPE: lambda: int(not lhs), str: lambda: int(not lhs)}.get(
        vy_type(lhs), lambda: vectorise(vectorised_not, lhs, ctx=ctx)
    )()


def vy_abs(lhs, ctx):
    """Elements ȧ
    (num) -> abs(a)
    (str) -> remove whitespace from a
    """
    return {
        NUMBER_TYPE: lambda: abs(lhs),
        str: lambda: lhs.replace(" ", ""),
    }.get(vy_type(lhs), lambda: vectorise(vy_abs, lhs, ctx=ctx))()


def vy_bin(lhs, ctx):
    """Element b
    (num) -> list of binary digits
    (str) -> binary of each codepoint
    """

    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: [int(x) for x in bin(int(lhs))[2:]],
        (str): lambda: vectorise(vy_bin, wrapify(chr_ord(lhs)), ctx=ctx),
    }.get(ts, lambda: vectorise(vy_bin, lhs, ctx=ctx))()


def vy_ceil(lhs, ctx):
    """Element ⌈
    (num) -> ceil(a)
    (str) -> a.split(' ') # split a on spaces
    """

    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: math.ceil(lhs),
        (str): lambda: lhs.split(" "),
    }.get(ts, lambda: vectorise(vy_ceil, lhs, ctx=ctx))()


def vy_divmod(lhs, rhs, ctx):
    """Element ḋ
    (num, num) -> [lhs // rhs, lhs % rhs]
    (iterable, num) -> combinations of a with length b
    (str, str) ->  overwrite the start of a with b
    """

    ts = vy_type(lhs, rhs, True)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: [lhs // rhs, lhs % rhs],
        (NUMBER_TYPE, str): lambda: LazyList(
            map(sum, itertools.combinations(lhs, rhs))
        ),
        (str, NUMBER_TYPE): lambda: LazyList(
            map(sum, itertools.combinations(rhs, lhs))
        ),
        (str, str): lambda: lhs[: len(rhs)] + rhs,
        (list, NUMBER_TYPE): lambda: LazyList(itertools.combinations(lhs, rhs)),
        (NUMBER_TYPE, list): lambda: LazyList(itertools.combinations(rhs, lhs)),
    }.get(ts, lambda: vectorise(vy_divmod, lhs, rhs, ctx=ctx))()


def vy_enumerate(lhs, ctx):
    """Element ė
    (any) -> Zip with a range of the same length
    """

    return LazyList(enumerate(iterable(lhs, ctx=ctx)))


def vy_filter(lhs: Any, rhs: Any, ctx):
    """Element F
    (any, fun) -> Keep elements in a that b is true for
    (any, any) -> Remove elements of a that are in b
    """

    ts = vy_type(lhs, rhs)
    return {
        (ts[0], types.FunctionType): lambda: LazyList(
            filter(
                lambda x: safe_apply(rhs, x, ctx=ctx),
                iterable(lhs, range, ctx=ctx),
            )
        ),
        (types.FunctionType, ts[1]): lambda: LazyList(
            filter(
                lambda x: safe_apply(lhs, x, ctx=ctx),
                iterable(rhs, range, ctx=ctx),
            )
        ),
    }.get(ts, lambda: LazyList([elem for elem in lhs if elem not in rhs]))()


def vy_gcd(lhs, rhs=None, ctx=None):

    ts = vy_type(lhs, rhs)
    NONE = type(None)

    return {
        (ts[0], NONE): lambda: reduce(
            lambda x, y: vy_gcd(x, y, ctx=ctx), iterable(lhs, ctx=ctx)
        ),
        (NUMBER_TYPE, NUMBER_TYPE): lambda: math.gcd(lhs, rhs),
        (NUMBER_TYPE, str): lambda: vy_gcd(lhs, wrapify(chr_ord(rhs)), ctx=ctx),
        (str, str): lambda: monadic_maximum(
            set(suffixes(lhs, ctx)) & set(suffixes(rhs, ctx)), ctx=ctx
        ),
    }.get(ts, lambda: vectorise(vy_gcd, lhs, rhs, ctx=ctx))()


def vy_int(item: Any, base: int = 10, ctx: Context = DEFAULT_CTX):
    """Converts the item to the given base. Lists are treated as if
    each item was a digit."""

    """Used for multiple elements, and has to be here because it uses
    functions defined only here."""
    t_item = type(item)
    if t_item not in [str, float, int, complex]:
        ret = 0
        for element in item:
            ret = multiply(ret, base, ctx)
            ret = add(ret, element, ctx)
        return ret
    elif t_item is str:
        return int(item, base)
    elif t_item is complex:
        return numpy.real(item)
    elif t_item is float:
        return int(item)
    elif t_item:
        return vy_int(iterable(item, ctx=ctx), base)


def vy_map(lhs, rhs, ctx):
    """Element M
    (any, fun) -> apply function b to each element of a
    (any, any) -> a paired with each item of b
    """

    ts = vy_type(lhs, rhs)
    return {
        (ts[0], types.FunctionType): lambda: list(
            map(
                lambda x: safe_apply(rhs, x, ctx=ctx),
                iterable(lhs, range, ctx=ctx),
            )
        ),
        (types.FunctionType, ts[1]): lambda: list(
            map(
                lambda x: safe_apply(lhs, x, ctx=ctx),
                iterable(rhs, range, ctx=ctx),
            )
        ),
    }.get(ts, lambda: LazyList([[lhs, x] for x in rhs]))()


def vy_sort(lhs, ctx):
    """
    (any) -> sorted(a)
    """

    # This one deviates from the usual type dictionary, because lambas
    # just don't cut it.

    if isinstance(lhs, int):
        if lhs >= 0:
            return int("".join(sorted(str(lhs))))
        else:
            return int("".join(sorted(str(-lhs)))) * -1
    elif vy_type(lhs) == NUMBER_TYPE:
        numerator, denomiator = str(lhs).split("/")
        numerator = vy_sort(numerator, ctx)
        denomiator = vy_sort(denomiator, ctx)
        return sympy.Rational(numerator, denomiator)
    elif isinstance(lhs, str):
        return "".join(sorted(lhs))
    else:
        return LazyList(sorted(lhs))


def vy_str(lhs, ctx=None):
    """Element S
    (any) -> str(s)
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: str(eval(str(lhs))),
        (str): lambda: lhs,  # wow so complex and hard to understand /s
        (types.FunctionType): lambda: vy_str(
            safe_apply(lhs, *ctx.stacks[-1], ctx=ctx), ctx
        ),
    }.get(
        ts,
        lambda: "⟨"
        + "|".join(
            map(
                lambda x: vy_repr(x, ctx),
                lhs,
            )
        )
        + "⟩",
    )()


def vy_print(lhs, end="\n", ctx=None):
    """Element ,
    (any) -> send to stdout
    """

    ctx.printed = True
    ts = vy_type(lhs)

    if ts is LazyList:
        lhs.output(end, ctx)
    elif ts is list:
        LazyList(lhs).output(end, ctx)
    elif ts is types.FunctionType:
        res = lhs(ctx.stacks[-1], lhs, ctx=ctx)[-1]
        vy_print(res, ctx=ctx)
    else:
        if ctx.online:
            ctx.online_output += vy_str(lhs, ctx=ctx)
        else:
            print(lhs, end=end)


def vy_reduce(lhs, rhs, ctx):
    """Element R
    (any, fun) -> Reduce a by function b
    (fun, any) -> Reduce b by function a
    """

    ts = vy_type(lhs, rhs)
    return {
        (ts[0], types.FunctionType): lambda: foldl(rhs, lhs, ctx),
        (types.FunctionType, ts[1]): lambda: foldl(lhs, rhs, ctx),
    }.get(ts)()


def vy_repr(lhs, ctx):
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: str(lhs),
        (str): lambda: "`" + lhs.replace("`", "\\`") + "`",
        (types.FunctionType): lambda: vy_repr(
            safe_apply(lhs, *ctx.stacks[-1], ctx=ctx), ctx
        )
        # actually make the repr kinda make sense
    }.get(
        ts,
        lambda: "⟨"
        + "|".join(
            map(
                lambda x: vy_repr(x, ctx),
                lhs or [],
            )
        )
        + "⟩",
    )()


def vy_type(item, other=None, simple=False):
    if other is not None:
        return (vy_type(item, simple=simple), vy_type(other, simple=simple))
    if (x := type(item)) in (
        int,
        sympy.Rational,
        complex,
        sympy.core.numbers.Half,
    ):
        return NUMBER_TYPE
    elif simple and isinstance(item, LazyList):
        return list
    else:
        return x


def vy_zip(lhs, rhs, ctx):
    if isinstance(lhs, types.FunctionType):
        return vy_zip(
            rhs,
            LazyList(map(lambda x: safe_apply(lhs, x, ctx=ctx), rhs)),
            ctx=ctx,
        )
    elif isinstance(rhs, types.FunctionType):
        return vy_zip(
            lhs,
            LazyList(map(lambda x: safe_apply(rhs, x, ctx=ctx), lhs)),
            ctx=ctx,
        )
    else:

        @lazylist
        def f():
            left = iter(iterable(lhs))
            right = iter(iterable(rhs))
            while True:
                exhausted = 0
                try:
                    left_item = next(left)
                except StopIteration:
                    left_item = 0
                    exhausted += 1

                try:
                    right_item = next(right)
                except StopIteration:
                    right_item = 0
                    exhausted += 1
                if exhausted == 2:
                    break
                else:
                    yield [left_item, right_item]

        return f()


elements: dict[str, tuple[str, int]] = {
    "¬": process_element("int(not lhs)", 1),
    "∧": process_element("lhs and rhs", 2),
    "⟑": process_element("rhs and lhs", 2),
    "∨": process_element("lhs or rhs", 2),
    "⟇": process_element("rhs or lhs", 2),
    "÷": (
        "lhs = pop(stack, 1, ctx); stack += iterable(lhs, ctx=ctx)",
        1,
    ),
    "×": process_element("'*'", 0),
    "•": process_element(log_mold_multi, 2),
    "†": (
        "top = function_call(stack, ctx)\n"
        + "if top is not None: stack.append(top)",
        1,
    ),
    "€": process_element(split_on, 2),
    "½": process_element(halve, 1),
    "↔": process_element(combinations_with_replacement, 2),
    "¢": process_element(infinite_replace, 3),
    "⌐": process_element(complement, 1),
    "æ": process_element(is_prime, 1),
    "ʀ": process_element(inclusive_zero_range, 1),
    "ʁ": process_element(exclusive_zero_range, 1),
    "ɾ": process_element(inclusive_one_range, 1),
    "ɽ": process_element(exclusive_one_range, 1),
    "ƈ": process_element(n_choose_r, 2),
    "∞": process_element("infinite_list(ctx)", 0),
    "!": process_element("len(stack)", 0),
    '"': process_element("[lhs, rhs]", 2),
    "$": (
        "rhs, lhs = pop(stack, 2, ctx); stack.append(rhs); "
        "stack.append(lhs)",
        2,
    ),
    "%": process_element(modulo, 2),
    "*": process_element(multiply, 2),
    "+": process_element(add, 2),
    ",": ("top = pop(stack, 1, ctx); vy_print(top, ctx=ctx)", 1),
    "-": process_element(subtract, 2),
    "/": process_element(divide, 2),
    ":": (
        "top = pop(stack, 1, ctx); stack.append(top); "
        "stack.append(deep_copy(top))",
        1,
    ),
    "<": process_element(less_than, 2),
    "=": process_element(equals, 2),
    ">": process_element(greater_than, 2),
    "?": (
        "ctx.use_top_input = True; lhs = get_input(ctx); "
        "ctx.use_top_input = False; stack.append(lhs)",
        0,
    ),
    "A": process_element("int(any(lhs))", 1),
    "B": process_element("vy_int(lhs, 2)", 1),
    "C": process_element(chr_ord, 1),
    "D": (
        "top = pop(stack, 1, ctx); stack.append(top);"
        "stack.append(deep_copy(top)); stack.append(deep_copy(top));",
        1,
    ),
    "E": process_element(exp2_or_eval, 1),
    "F": process_element(vy_filter, 2),
    "G": process_element(monadic_maximum, 1),
    "H": process_element("vy_int(lhs, 16)", 1),
    "I": process_element(vy_int, 1),
    "J": process_element(merge, 2),
    "K": process_element(divisors, 1),
    "L": process_element(length, 1),
    "M": process_element(vy_map, 2),
    "N": process_element(negate, 1),
    "O": process_element(count, 2),
    "P": process_element(strip, 2),
    "Q": process_element("exit()", 0),
    "R": (
        "if len(stack) > 1 and types.FunctionType "
        "in vy_type(stack[-1], stack[-2]):\n"
        "    rhs, lhs = pop(stack, 2, ctx);"
        "    stack.append(vy_reduce(lhs, rhs, ctx))\n"
        "else:\n"
        "    stack.append(vectorise(reverse, pop(stack, 1, ctx), ctx=ctx))",
        2,
    ),
    "S": process_element(vy_str, 1),
    "T": process_element(truthy_indicies, 1),
    "U": process_element(uniquify, 1),
    "V": process_element(replace, 3),
    "W": ("stack = [stack]", 0),
    # X doesn't need to be implemented here, because it's already a structure
    "Y": process_element(interleave, 2),
    "Z": process_element(vy_zip, 2),
    "^": ("stack = stack[::-1]", 0),
    "_": ("pop(stack, 1, ctx)", 1),
    "a": process_element("int(all(lhs))", 1),
    "b": process_element(vy_bin, 1),
    "c": process_element(contains, 2),
    "d": process_element("multiply(lhs, 2, ctx)", 1),
    "e": process_element(exponent, 2),
    "f": process_element(deep_flatten, 1),
    "g": process_element(monadic_minimum, 1),
    "h": process_element(head, 1),
    "i": process_element(index, 2),
    "j": process_element(join, 2),
    "l": process_element(overlapping_groups, 2),
    "m": process_element(mirror, 1),
    "o": process_element(remove, 2),
    "p": process_element(prepend, 2),
    "q": process_element(quotify, 1),
    "r": process_element(orderless_range, 2),
    "s": process_element(vy_sort, 1),
    "t": process_element(tail, 1),
    "u": process_element("-1", 0),
    "w": process_element("[lhs]", 1),
    "y": ("stack += uninterleave(pop(stack, 1, ctx), ctx)", 1),
    "z": process_element("vy_zip(lhs, deep_copy(lhs), ctx)", 1),
    "↑": process_element(max_by_tail, 1),
    "↓": process_element(min_by_tail, 1),
    "∴": process_element(dyadic_maximum, 2),
    "∵": process_element(dyadic_minimum, 2),
    "∷": process_element(parity, 1),
    "¤": process_element("''", 0),
    "ð": process_element("' '", 0),
    "β": process_element(from_base, 2),
    "τ": process_element(to_base, 2),
    "›": process_element(increment, 1),
    "‹": process_element(decrement, 1),
    "ȧ": process_element(vy_abs, 1),
    "ḃ": process_element(boolify, 1),
    "ċ": process_element(is_falsey, 1),
    "ḋ": process_element(vy_divmod, 2),
    "ė": process_element(vy_enumerate, 1),
    "ḟ": process_element(find, 2),
    "ġ": (
        "top = pop(stack, 1, ctx)\n"
        "if vy_type(top, simple=True) is list:\n"
        "    stack.append(vy_gcd(top, ctx=ctx))\n"
        "else:\n"
        "    stack.append(vy_gcd(pop(stack, 1, ctx), top, ctx))\n",
        2,
    ),
    "Ṙ": process_element(reverse, 1),
    "⌈": process_element(vy_ceil, 1),
    "ǎ": process_element(substrings, 1),
}
modifiers: dict[str, str] = {
    "v": (
        "arguments = wrapify(pop(stack, function_A.arity, ctx=ctx))\n"
        + "stack.append"
        + "(vectorise(function_A, *arguments[::-1], explicit=True, ctx=ctx))\n"
    ),
    "~": (
        "ctx.retain_popped = True\n"
        + "arguments = wrapify(pop(stack, function_A.arity, ctx=ctx))\n"
        + "ctx.retain_popped = False\n"
        + "stack.append(safe_apply(function_A, *arguments[::-1], ctx=ctx))\n"
    ),
}
