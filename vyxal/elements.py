"""This is where the element functions are stored

(that is, functions directly corresponding to Vyxal elements). It's also where
the python equivalent of command is stored
"""

from vyxal.LazyList import LazyList, lazylist, vyxalify
from vyxal.helpers import *
from vyxal.encoding import (
    codepage_number_compress,
    codepage_string_compress,
    codepage,
)
from vyxal.context import DEFAULT_CTX, Context

import sympy
import numpy

import itertools
import math
import random
import re
import string
import types
from functools import reduce
from typing import Union
from datetime import datetime

currentdate = datetime.now()


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


def absolute_difference(lhs, rhs, ctx):
    """Element ε
    (num, num) -> abs(a - b)
    (any, str) -> Transpose a (filling with b), join on newlines
    """

    ts = vy_type(lhs, rhs)
    if ts == (NUMBER_TYPE, NUMBER_TYPE):
        return abs(lhs - rhs)
    else:
        return vertical_join(lhs, rhs, ctx)


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


def all_equal(lhs, ctx):
    """Element ≈
    (any) -> are all items in a the same?
    """

    lhs = iterable(lhs, ctx=ctx)
    if not len(lhs):
        return 0

    else:
        first = lhs[0]
        for item in lhs[1:]:
            if not non_vectorising_equals(item, first, ctx):
                return 0
        return 1


def angle_bracketify(lhs, ctx):
    """Element øḂ
    (any) -> "<" + lhs + ">"
    (lst) -> vectorised
    """
    if isinstance(lhs, LazyList):
        return vectorise(parenthesise, lhs)
    return "<" + lhs + ">"


def all_true(lhs, ctx):
    """Element A
    (lst) -> all of lhs is truthy?
    (str) -> is_vowel (vectorises over multichar strings)
    """
    if isinstance(lhs, str):
        if len(lhs) == 1:
            return int(lhs in "aeiouAEIOU")
        else:
            return [int(char in "aeiouAEIOU") for char in lhs]
    return int(all(iterable(lhs, ctx)))


def any_true(lhs, ctx):
    """Element a
    (lst) -> any of lhs is truthy?
    (str) -> is_capital_letter (vectorises over multichar strings)
    """
    if isinstance(lhs, str):
        if len(lhs) == 1:
            return int(91 >= ord(lhs) >= 65)
        else:
            return [int(91 >= ord(lhs) >= 65) for char in lhs]
    return int(any(iterable(lhs, ctx=ctx)))


def assign_iterable(lhs, rhs, other, ctx):
    """Element Ȧ
    (any, num, any) -> a but item b (0-indexed) is set to c
    """

    lhs = iterable(lhs, ctx=ctx)
    if type(rhs) is str:
        rhs = chr_ord(rhs, ctx)

    if vy_type(rhs, simple=True) is list:
        for item in rhs:
            lhs = assign_iterable(lhs, item, other, ctx)
        return lhs
    if type(lhs) is str:
        lhs = list(lhs)
        lhs[rhs] = other
        return vy_sum(lhs, ctx=ctx)
    else:
        lhs[rhs] = other
        return lhs


def base_255_string_compress(lhs, ctx):
    """Element øc
    (str) -> Compress a string of lowercase letters and spaces in base 255
    """
    return (
        "«"
        + to_base(
            from_base(lhs, string.ascii_lowercase + " ", ctx),
            codepage_string_compress,
            ctx,
        )
        + "«"
    )


def base_255_number_compress(lhs, ctx):
    """Element øC
    (num) -> Compress a number in base 255
    """
    return "»" + to_base(lhs, codepage_number_compress, ctx) + "»"


def boolify(lhs, ctx):
    """Element ḃ
    (any) -> is truthy?
    """

    if vy_type(lhs, simple):
        return vectorise(boolify, lhs, ctx=ctx)
    else:
        return int(bool(lhs))


def bracketify(lhs, ctx):
    """Element øB
    (any) -> "[" + lhs + "]"
    (lst) -> vectorised
    """
    if isinstance(lhs, LazyList):
        return vectorise(bracketify, lhs)
    return "[" + lhs + "]"


def brackets_balanced(lhs):
    """Element øβ
    (str) -> is lhs balanced?
    """
    brackets = {"(": ")", "[": "]", "{": "}", "<": ">"}
    stack = []
    for char in lhs:
        if char in brackets.keys():
            stack.append(char)
        elif char in brackets.values():
            if not stack or stack.pop() != char:
                return 0
    return int(len(stack) == 0)


def center(lhs, ctx):
    """Element øc
    (list) -> center align list by padding with spaces
    """
    return [line.center(max(lhs, key=len)) for line in lhs]


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
    (fun, any) -> apply lhs on rhs until the result does not change. Collects intermediate values
    (any, fun) -> apply rhs on lhs until the result does not change. Collects intermediate values
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


def counts(lhs, ctx):
    temp = uniquify(lhs, ctx=ctx)
    return [[x, count(lhs, x, ctx)] for x in temp]


def cumulative_sum(lhs, ctx):
    """Element ¦
    (any) -> cumulative sum of a
    """

    return LazyList(scanl(add, iterable(lhs, ctx=ctx), ctx))


def curly_bracketify(lhs, ctx):
    """Element øḃ
    (any) -> "[" + lhs + "]"
    (lst) -> vectorised
    """
    if isinstance(lhs, LazyList):
        return vectorise(curly_bracketify, lhs)
    return "{" + lhs + "}"


def custom_pad_left(lhs, rhs, other, ctx):
    """Element ø↲
    (any, num, str) -> pad a on the left with c to length b
    (any, str, num) -> pad a on the left with b to length c
    (lst, any, any) -> vectorised
    """
    if isinstance(lhs, LazyList):
        return vectorise(custom_pad_left, lhs, rhs, other)
    if isinstance(rhs, int):
        return lhs.ljust(rhs, other)
    if isinstance(other, int):
        return lhs.ljust(other, rhs)


def custom_pad_right(lhs, rhs, other, ctx):
    """Element ø↳
    (any, num, str) -> pad a on the right with c to length b
    (any, str, num) -> pad a on the right with b to length c
    (lst, any, any) -> vectorised
    """
    if isinstance(lhs, LazyList):
        return vectorise(custom_pad_left, lhs, rhs, other)
    if isinstance(rhs, int):
        return lhs.rjust(rhs, other)
    if isinstance(other, int):
        return lhs.rjust(other, rhs)


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
        (NUMBER_TYPE, str): lambda: wrap(rhs, len(rhs) // lhs, ctx),
        (str, NUMBER_TYPE): lambda: wrap(lhs, len(lhs) // rhs, ctx),
        (str, str): lambda: lhs.split(rhs),
    }.get(ts, lambda: vectorise(divide, lhs, rhs, ctx=ctx))()


def divisors(lhs, ctx):
    """Element K
    (num) -> divisors(a) # Factors or divisors of a
    # they "divide" a into more than one piece
    (str) -> all substrings of a that occur more than once
    (lst) -> prefixes(a) # Prefixes of a
    """

    ts = vy_type(lhs)
    if ts == NUMBER_TYPE:
        return sympy.divisors(lhs)
    elif ts == str:
        return LazyList(
            filter(
                lambda substr: lhs.count(substr) > 1,
                substrings(lhs, ctx),
            )
        )
    return LazyList((lhs[: x + 1] for x in range(len(lhs))))


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


def exclusive_one_range(lhs, ctx):
    """Element ɽ
    (num) -> range(1, a)
    (str) -> a.lower()
    """

    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: LazyList(range(1, int(lhs))),
        str: lambda: lhs.lower(),
    }.get(ts, lambda: vectorise(exclusive_one_range, lhs, ctx=ctx))()


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


def factorial(lhs, ctx):
    """Element ¡
    (num) -> factorial(a) (math.gamma(a + 1))
    (str) -> a.sentence_case()
    """

    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: vyxalify(sympy.factorial(lhs)),
        # Because otherwise, it returns a very unhelpful factorial obj
        str: lambda: sentence_case(lhs),
    }.get(ts, lambda: vectorise(factorial, lhs, ctx=ctx))()


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


def first_integer(lhs, ctx):
    """Element ṅ
    (num) -> abs(a) <= 1
    (str) -> pad with 0s to nearest multiple of 8
    (lst) -> "".join(a)
    (fun) -> first integer x where a(x) is truthy
    """

    if isinstance(lhs, types.FunctionType):
        value = 1

        while not safe_apply(lhs, value, ctx):
            value += 1

        return value

    ts = vy_type(lhs, simple=True)
    return {
        (NUMBER_TYPE): lambda: abs(lhs) <= 1,
        (str): lambda: lhs.zfill(len(lhs) + (8 - len(lhs) % 8)),
        (list): lambda: join(lhs, "", ctx),
    }.get(ts, lambda: vectorise(first_integer, lhs, ctx=ctx))()


def flip_brackets_vertical_palindromise(lhs, ctx):
    """Element øM
    (str) -> lhs vertically palindromised without duplicating the center, with brackets flipped.
    """
    result = lhs.split("\n")
    for i in range(len(result)):
        result[i] += invert_brackets(result[i][:-1][::-1])
    return "\n".join(result)


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
    return {
        NUMBER_TYPE: lambda: len(prime_factors(top, ctx)),
        str: lambda: exec(lhs) or [],
        list: lambda: vectorised_not(top, ctx=ctx),
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


def gen_from_fn(lhs, rhs, ctx):
    """Element Ḟ
    (fun, lst) -> Generator from a with initial vector b
    (lst, fun) -> Generator from b with initial vector a
    """

    rhs_type = vy_type(rhs)

    if rhs_type == types.FunctionType:
        temp = rhs
        rhs = iterable(lhs)
        lhs = temp

    @lazylist
    def gen():
        for item in lhs:
            yield item

        while True:
            yield safe_apply(lhs, ctx=ctx)

    return gen()


def greater_than(lhs, rhs, ctx):
    """Element <
    (num, num) -> a > b
    (num, str) -> str(a) > b
    (str, num) -> a > str(b)
    (str, str) -> a > b
    """

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: int(lhs > rhs),
        (NUMBER_TYPE, str): lambda: int(str(lhs) > rhs),
        (str, NUMBER_TYPE): lambda: int(lhs > str(rhs)),
        (str, str): lambda: int(lhs > rhs),
    }.get(ts, lambda: vectorise(greater_than, lhs, rhs, ctx=ctx))()


def group_consecutive(lhs, ctx):
    """Element Ġ
    (lst) -> Group consecutive identical items
    (str) -> Group consecutive identical characters
    (num) -> Group consecutive identical digits"""

    typ = vy_type(lhs)

    if typ == NUMBER_TYPE:
        lhs = digits(lhs)

    def gen():
        prev = None
        count = 0

        for item in lhs:
            if not non_vectorising_equals(prev, item, ctx):
                yield [prev] * count
                prev = item
                count = 0

    if typ is LazyList:
        return LazyList(gen())

    res = list(gen())

    if typ == NUMBER_TYPE:
        res = [int(group) for group in res]

    return res


def group_on_words(lhs, ctx):
    """Element øW
    (str) -> Group lhs on sequences of letters
    """
    result, word = [], ""
    for char in lhs:
        if char in string.ascii_letters:
            word += char
        else:
            if word:
                result.append(word)
            word = ""
            result.append(char)
    return result


def halve(lhs, ctx):
    """Element ½
    (num) -> lhs / 2
    (str) -> a split into two strings of equal lengths (as close as possible)
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.Rational(lhs, 2),
        str: lambda: wrap(lhs, math.ceil(len(lhs) / 2), ctx=ctx),
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


def head_remove(lhs, ctx):
    """Element Ḣ
    (lst) -> a[1:] or [] if empty
    (str) -> a[1:] or '' if empty
    (num) -> Remove first digit or do nothing if <1"""

    if vy_type(lhs, simple=True) in (list, str):
        return a[1:] if a else a
    if lhs < 1:
        return lhs
    if isinstance(lhs, int):
        return lhs if lhs < 1 else int(str(lhs)[1:])
    assert isinstance(lhs, sympy.Rational)
    return sympy.Rational(str(float(lhs))[1:])


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
    (num, any) -> b[a] (Index)
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

    elif ts[0] == NUMBER_TYPE:
        return index(rhs, lhs, ctx)

    elif ts[-1] == str:
        return vectorise(index, lhs, rhs, ctx=ctx)

    else:
        return iterable(lhs, ctx)[slice(*rhs)]


def index_indices_or_cycle(lhs, rhs, ctx):
    """Element İ
    (any, lst) -> [a[item] for item in b]
    (any, fun) -> Repeatedly apply b to a until cycle is formed, then
                  return cycle, not including the repeated item"""

    if vy_type(rhs) is types.FunctionType:
        prevs = []
        curr = None

        while True:
            curr = safe_apply(rhs, lhs, ctx=ctx)

            for i in range(prevs):
                if equals(prevs[i], curr):
                    return prevs[i:]

            prevs.append(curr)
    else:
        lhs = iterable(lhs)
        rhs = iterable(rhs)
        return vy_map(rhs, lambda item: lhs[item])


def infinite_replace(lhs, rhs, other, ctx):
    """Element ¢
    (any, any, any) -> replace b in a with c until a doesn't change
    """

    orig_type = type(lhs)

    prev = deep_copy(lhs)
    while True:
        lhs = replace(lhs, rhs, other, ctx)
        if lhs == prev:
            break
        prev = deep_copy(lhs)

    if orig_type is int:
        try:
            return int(lhs)
        except ValueError:
            return lhs
    return lhs


def insert_or_map_nth(lhs, rhs, other, ctx):
    """Element Ṁ
    (any, num, any) -> a.insert(b, c) (Insert c at position b in a)
    (any, num, fun) -> c mapped over every bth item of a

    If `ind` is negative, the absolute value is used. If `ind` is greater than
    or equal to the LazyList's length, `other` is appended to the end."""

    lhs = iterable(lhs)
    assert vy_type(rhs) == NUMBER_TYPE

    if vy_type(other) != types.FunctionType:

        @lazylist
        def gen():
            i = 0
            for elem in lhs:
                if i == rhs:
                    yield other
                yield elem
                i += 1
            if i < rhs:
                yield other

        return gen()

    @lazylist
    def gen():
        i = 0
        for item in lhs:
            yield safe_apply(other, item, ctx=ctx) if i % rhs == 0 else item
            i += 1

    return gen()


def integer_divide(lhs, rhs, ctx):
    """Element ḭ
    (num, num) -> a // b (Floor division, floor(a / b))
    (str, num) -> (a divided into b pieces)[0]
    (num, str) -> (b divided into a pieces)[0]
    (any, fun) -> Right reduce a by b (foldr)
    (fun, any) -> Right reduce b by a (foldr)
    """

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: lhs // rhs,
        (NUMBER_TYPE, str): lambda: divide(lhs, rhs, ctx=ctx)[0],
        (str, NUMBER_TYPE): lambda: divide(rhs, lhs, ctx=ctx)[0],
        (ts[0], types.FunctionType): lambda: foldl(
            rhs, reverse(iterable(lhs, ctx=ctx), ctx=ctx), ctx
        ),
        (types.FunctionType, ts[1]): lambda: foldl(
            lhs, reverse(iterable(rhs, ctx=ctx), ctx=ctx), ctx
        ),
    }.get(ts, lambda: vectorise(integer_divide, lhs, rhs, ctx=ctx))()


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


def is_divisible(lhs, rhs, ctx):
    """Element Ḋ
    (num, num) -> a % b == 0
    (num, str) -> a copies of b
    (str, num) -> b copies of a
    (str, str) -> b + " " + a ($ẋ)
    """

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: [int(lhs % rhs == 0)],
        (NUMBER_TYPE, str): lambda: [rhs] * lhs,
        (str, NUMBER_TYPE): lambda: [lhs] * rhs,
        (str, str): lambda: [rhs + " " + lhs],
    }.get(ts, lambda: [vectorise(is_divisible, lhs, rhs, ctx=ctx)])()


def is_divisible_by_three(lhs, ctx):
    """Element ₃
    (num) -> a % 3 == 0
    (str) -> len(a) == 1
    """

    if vy_type(lhs) == NUMBER_TYPE:
        return int(lhs % 3 == 0)
    else:
        return int(len(lhs) == 1)


def is_divisible_by_five(lhs, ctx):
    """
    Element ₅
    (num) -> a % 5 == 0
    (str) -> a, len(a)
    """

    # wrap in list because you might need to return more than 1 item
    if vy_type(lhs) == NUMBER_TYPE:
        return [int(lhs % 5 == 0)]
    else:
        return [lhs, len(lhs)]


def is_even(lhs, ctx):
    """Element ₂
    (num) -> a % 2 == 0
    (str) -> len(a) % 2 == 0
    """
    if vy_type(lhs) == NUMBER_TYPE:
        return int(lhs % 2 == 0)
    else:
        return int(len(lhs) % 2 == 0)


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


def join_newlines(lhs, ctx):
    """Element ⁋
    (any) -> a.join("\n")
    """

    ret = []
    for n in iterable(lhs, ctx):
        if vy_type(n) in [list, LazyList]:
            ret.append(join(n, " ", ctx))
        else:
            ret.append(str(n))
    return "\n".join(ret)


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
        (NUMBER_TYPE, NUMBER_TYPE): lambda: int(lhs < rhs),
        (NUMBER_TYPE, str): lambda: int(str(lhs) < rhs),
        (str, NUMBER_TYPE): lambda: int(lhs < str(rhs)),
        (str, str): lambda: int(lhs < rhs),
    }.get(ts, lambda: vectorise(less_than, lhs, rhs, ctx=ctx))()


def ljust(lhs, rhs, other, ctx):
    """Element ŀ
    (num, num, num) -> a <= c <= b
    (num, num, str) -> a by b grid of c
    (num, str, num) -> a by c grid of b
    (num, str, str) -> b.ljust(a,filler=c)
    (str, num, num) -> b by c grid of a
    (str, num, str) -> a.ljust(c,filler=b)
    (str, str, num) -> a.ljust(b,filler=c)
    (str, str, str) -> a.infinite_replace(b, c)
    (fun, fun, any) -> collect_until_false(predicate=a,
                       modifying_function=b, inital=c)
                       # Collect the results of apply a on c while b(c)
                       # is truthy
    """

    ts = vy_type(lhs, rhs, other)

    return {
        (NUMBER_TYPE, NUMBER_TYPE, NUMBER_TYPE): lambda: lhs <= other <= rhs,
        (NUMBER_TYPE, NUMBER_TYPE, str): lambda: "\n".join([other * lhs] * rhs),
        (NUMBER_TYPE, str, NUMBER_TYPE): lambda: "\n".join([rhs * lhs] * other),
        (NUMBER_TYPE, str, str): lambda: vy_str(rhs).ljust(lhs, other),
        (str, NUMBER_TYPE, NUMBER_TYPE): lambda: "\n".join([lhs * other] * rhs),
        (str, NUMBER_TYPE, str): lambda: vy_str(lhs).ljust(rhs, other),
        (str, str, NUMBER_TYPE): lambda: vy_str(lhs).ljust(rhs, other),
        (str, str, str): lambda: infinite_replace(lhs, rhs, other, ctx),
        (
            types.FunctionType,
            types.FunctionType,
            ts[-1],
        ): lambda: collect_until_false(lhs, rhs, other, ctx),
        (
            ts[0],
            types.FunctionType,
            types.FunctionType,
        ): lambda: collect_until_false(rhs, other, lhs, ctx),
        (
            types.FunctionType,
            ts[1],
            types.FunctionType,
        ): lambda: collect_until_false(lhs, other, rhs, ctx),
    }.get(ts, vectorise(ljust, lhs, rhs, other, ctx))()


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


def max_by_tail(lhs, ctx):
    """Element ↑
    (any) -> max(a, key=lambda x: x[-1])
    """

    lhs = iterable(lhs, ctx=ctx)
    if len(lhs) == 0:
        return []
    else:
        return max_by(lhs, key=tail, cmp=less_than, ctx=ctx)


def mean(lhs, ctx):
    """Element ṁ
    (num) -> random.randint(0, a)
    (str) -> palindromise a
    (lst) -> arithmetic mean of a
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: random.randint(0, lhs),
        (str): lambda: palindromise(lhs, ctx),
    }.get(ts, lambda: divide(vy_sum(lhs, ctx), len(lhs), ctx))()


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
        (list, ts[1]): lambda: concat(lhs, [rhs]),
        (ts[0], list): lambda: concat([lhs], rhs),
        (list, list): lambda: concat(lhs, rhs),
    }.get(ts)()


def min_by_tail(lhs, ctx):
    """Element ↓
    (any) -> min(a, key=lambda x: x[-1])
    """
    lhs = iterable(lhs, ctx=ctx)
    if len(lhs) == 0:
        return []
    else:
        return min_by(lhs, key=tail, cmp=less_than, ctx=ctx)


def mirror(lhs, ctx):
    """Element m
    (num) -> a + reversed(a) (as number)
    (str) -> a + reversed(a)
    (lst) -> Concatenate reversed(a) to a
    """
    if vy_type(lhs) in (NUMBER_TYPE, str):
        return add(lhs, reverse(lhs, ctx), ctx)
    else:
        return concat(lhs, reverse(lhs, ctx), ctx)


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
    (any) -> Maximal element of the input (deep flattens first)
    """

    lhs = deep_flatten(lhs, ctx)
    if len(lhs) == 0:
        return []
    else:
        return max_by(lhs, cmp=less_than, ctx=ctx)


def monadic_minimum(lhs, ctx):
    """Element g
    (any) -> Smallest item of a (deep flattens)
    """

    lhs = deep_flatten(lhs, ctx)
    if len(lhs) == 0:
        return []
    else:
        return min_by(lhs, cmp=less_than, ctx=ctx)


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
        (NUMBER_TYPE, NUMBER_TYPE): lambda: sympy.binomial(lhs, rhs),
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


def non_vectorising_equals(lhs, rhs, ctx):
    """Element ⁼
    (num, num) -> a == b
    (str, str) -> a == b
    (lst, lst) -> a == b
    """

    ts = vy_type(lhs, rhs, True)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: lhs == rhs,
        (NUMBER_TYPE, str): lambda: str(lhs) == rhs,
        (str, NUMBER_TYPE): lambda: lhs == str(rhs),
        (str, str): lambda: lhs == rhs,
        (list, list): lambda: lhs == rhs,
    }.get(ts)()


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
        (str, NUMBER_TYPE): lambda: ("0" * abs(len(rhs) - lhs)) + lhs,
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
    (any, num) -> Overlapping groups/windows of a of length b
    (any, any) -> length(a) == length(b)
    """

    if vy_type(rhs) != NUMBER_TYPE:
        return len(iterable(lhs)) == len(rhs)

    stringify = vy_type(lhs) is str

    @lazylist
    def gen():
        window = "" if stringify else []
        for item in lhs:
            if stringify:
                window += item
            else:
                window.append(item)
            if len(window) == rhs:
                yield window
                window = window[1:]

    return gen()

    # TODO (lyxal) This was erroring and idk what this even does
    # so I commented it out
    # iters = itertools.tee(iterable(lhs), rhs)
    # for i in range(len(iters)):
    #     for j in range(i):
    #         next(iters[i], None)

    # return LazyList(zip(*iters))


def palindromise(lhs, ctx):
    """Element ∞
    (num) -> equivalent to m
    (str) -> a + a[:-1:-1]
    (lst) -> a + a[:-1:-1]
    """

    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: mirror(lhs, ctx),
        str: lambda: lhs + lhs[:-1][::-1],
        list: lambda: lhs + lhs[:-1][::-1],
        LazyList: lambda: merge(
            deep_copy(lhs),
            reverse(index(deep_copy(lhs), [None, -1, None], ctx), ctx),
            ctx=ctx,
        ),
    }.get(ts)()


def parenthesise(lhs, ctx):
    """Element øb
    (any) -> "(" + lhs + ")"
    (lst) -> vectorised
    """
    if isinstance(lhs, LazyList):
        return vectorise(parenthesise, lhs)
    return "(" + lhs + ")"


def parity(lhs, ctx):
    """Element ∷
    (num) -> parity of a
    (str) -> parity of a
    """

    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: int(lhs % 2),
        (str): lambda: halve(lhs, ctx)[-1],
    }.get(ts, lambda: vectorise(parity, lhs, ctx=ctx))()


def pluralise_count(lhs, rhs, ctx):
    """Element øP
    (str, num) -> count lhs lots of rhs
    (num, str) -> count rhs lots of lhs
    """
    if isinstance(int, lhs):
        return pluralise_count(rhs, lhs, ctx)
    return lhs + " " + rhs + "s" * (rhs != 1)


def powerset(lhs, ctx):
    """Element ṗ
    (any) -> powerset of a
    """
    return LazyList(
        itertools.chain.from_iterable(
            itertools.combinations(iterable(lhs, ctx), r)
            for r in range(len(iterable(lhs, ctx)) + 1)
        )
    )


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
        NUMBER_TYPE: lambda: "`{}`".format(lhs),
        str: lambda: "`{}`".format(lhs.replace("`", "\\`")),
        types.FunctionType: lambda: "`{}`".format(lhs.__name__),
    }.get(ts, lambda: quotify(vy_str(lhs, ctx=ctx), ctx))()


def remove(lhs, rhs, ctx):
    """Element o
    (any, any) -> a.remove(b)
    """
    lhs = iterable(lhs)
    ts = vy_type(lhs)
    if ts == str:
        return replace(lhs, rhs, "", ctx)
    elif ts == LazyList:
        return lhs.filter(lambda elem: elem != rhs)
    else:
        return [elem for elem in lhs if elem != rhs]


def remove_until_no_change(lhs, rhs, ctx):
    """Element øo
    (any, any) -> a.remove_until_no_change(b)
    """
    if not isinstance(rhs, LazyList):
        rhs = LazyList([rhs])
    # Remove each item of rhs from lhs until lhs does not change
    prev = deep_copy(lhs)
    while prev != lhs:
        for item in rhs:
            lhs = remove(lhs, item, ctx)
        prev = deep_copy(lhs)
    return lhs


def repeat(lhs, rhs, ctx):
    """Element ẋ
    (any, num) -> Repeat a b times
    (str, str) -> a + " " + b
    (fun, any) -> repeat function a on b while the function results are not-unique
    (any, fun) -> repeat function b on a while the function results are not-unique
    """

    ts = vy_type(lhs, rhs)
    if types.FunctionType in ts:
        function, value = (
            (lhs, rhs) if ts[0] == types.FunctionType else (rhs, lhs)
        )

        @lazylist
        def gen():
            prev = value
            while True:
                val = safe_apply(function, value, ctx=ctx)
                if val == prev:
                    break
                prev = val
                yield val

        return gen()

    else:
        return {
            (ts[0], NUMBER_TYPE): lambda: LazyList(
                itertools.repeat(iterable(lhs, ctx=ctx), int(rhs))
            ),
            (NUMBER_TYPE, ts[1]): lambda: LazyList(
                itertools.repeat(iterable(rhs, ctx=ctx), int(lhs))
            ),
            (str, str): lambda: lhs + " " + rhs,
        }.get(ts, lambda: vectorise(repeat, lhs, rhs, ctx=ctx))()


def replace(lhs, rhs, other, ctx):
    """Element V
    (any, any, any) -> a.replace(b, c)
    """

    if vy_type(lhs, simple=True) is not list:
        return str(lhs).replace(str(rhs), str(other))
    else:
        return [other if value == rhs else value for value in iterable(lhs)]


def replace_until_no_change(lhs, rhs, other, ctx):
    """Element øV
    (any,any,any) -> Replace rhs with other in lhs while lhs changes
    """
    prev = deep_copy(lhs)
    while prev != lhs:
        lhs = replace(lhs, rhs, other, ctx)
        prev = deep_copy(lhs)
    return lhs


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


# Written by copilot. Looks like it works.
def run_length_encoding(lhs, ctx):
    """Element øe
    (str) -> List of the form [[character, count], ...]
    """
    return LazyList(
        map(
            itertools.groupby(lhs),
            lambda elem: [list(elem[1]), len(list(elem[1]))],
        )
    )


def run_length_decoding(lhs, ctx):
    """Element ød
    (lst) -> Run length decoding
    """
    return map(lhs, lambda elem: elem[0] * elem[1])


def slice_from(lhs, rhs, ctx):
    """Element ȯ
    (fun, num) -> First b integers for which a(x) is truthy
    (any, num) -> a[b:] (Slice from b to the end)
    (str, str) -> vertically merge a and b
    """

    ts = vy_type(lhs, rhs)
    if types.FunctionType in ts:
        function, count = (
            (lhs, rhs) if ts[0] is types.FunctionType else (rhs, lhs)
        )

        @lazylist
        def gen():
            found = 0
            item = 1
            while True:
                if found == count:
                    break
                if safe_apply(function, item, ctx):
                    found += 1
                    yield item
                item += 1

        return gen()

    else:
        return {
            (str, str): lambda: lhs + "\n" + rhs,
        }.get(ts, lambda: index(lhs, [rhs, None, None], ctx))()


def sort_by(lhs, rhs, ctx):
    """Element ṡ
    (any, fun) -> sorted(a, key=b) (Sort by b)
    (num, num) -> range(a, b + 1) (Inclusive range from a to b)
    (str, str) -> regex.split(string=a, pattern=b)
    """

    ts = vy_type(lhs, rhs)
    if types.FunctionType in ts:
        function, vector = (
            (lhs, rhs) if ts[0] is types.FunctionType else (rhs, lhs)
        )
        return sorted(vector, key=lambda x: safe_apply(function, x, ctx))
    else:
        return {
            (NUMBER_TYPE, NUMBER_TYPE): lambda: range(lhs, rhs + 1),
            (str, str): lambda: re.split(lhs, rhs),
        }.get(ts, lambda: vectorise(sort_by, lhs, rhs, ctx=ctx))()


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


def square_root(lhs, ctx):
    """Element √
    (num) -> sqrt(a)
    (str) -> every second character of a
    """

    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.sqrt(lhs),
        str: lambda: "".join(lhs[::2]),
    }.get(ts, lambda: vectorise(square_root, lhs, ctx=ctx))()


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


def starts_with(lhs, rhs):
    """Element øp
    (str, str) -> True if a starts with b
    """
    return lhs.startswith(rhs)


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


def transliterate(lhs, rhs, other, ctx):
    """Element Ŀ
    (any, any, any) -> transliterate lhs according to the
                       mapping rhs->other
    """

    mapping = {
        x: y
        for x, y in vy_zip(iterable(rhs, ctx), iterable(other, ctx), ctx=ctx)
    }

    ret = []

    for item in lhs:
        for x in mapping:
            if non_vectorising_equals(item, x, ctx):
                ret.append(mapping[x])
                break
        else:
            ret.append(item)

    if type(lhs) is str:
        return "".join(ret)
    else:
        return ret


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

    if type(lhs) is str:
        seen = ""
        for item in lhs:
            if item not in seen:
                seen += item
        return seen

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
                for x, y in vy_zip(rhs, other, ctx=ctx)
            ),
            (list, SCALAR_TYPE, SCALAR_TYPE): lambda: (
                safe_apply(function, x, rhs, other, ctx=ctx) for x in lhs
            ),
            (list, SCALAR_TYPE, list): lambda: (
                safe_apply(function, x, rhs, y, ctx=ctx)
                for x, y in vy_zip(lhs, other, ctx=ctx)
            ),
            (list, list, SCALAR_TYPE): lambda: (
                safe_apply(function, x, y, other, ctx=ctx)
                for x, y in vy_zip(lhs, rhs, ctx=ctx)
            ),
            (list, list, list): lambda: (
                safe_apply(function, x, y, z, ctx=ctx)
                for x, y in vy_zip(lhs, rhs, ctx=ctx)
                for z in other
            ),
        }

        if explicit:
            return LazyList(
                (
                    safe_apply(function, x, rhs, other, ctx=ctx)
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


def vertical_join(lhs, rhs=" ", ctx=None):
    """Element §
    any: Transpose a (filling with b), join on newlines
    """
    # Make every list in lhs the same length, padding left with b

    max_length = max(len(x) for x in lhs)
    temp = [
        [rhs] * (len(x) < max_length and max_length - len(x)) + x
        if vy_type(x, simple=True) is list
        else rhs * (len(x) < max_length and max_length - len(x)) + x
        for x in lhs
    ]
    temp = [join(x, "", ctx) for x in transpose(temp, rhs, ctx=ctx)]
    return join(temp, "\n", ctx)


def vertical_mirror(lhs, rhs=None, ctx=None):
    """Element øṁ and øṀ"""
    if type(lhs) is str:
        if rhs:
            temp = [
                s + transliterate(rhs[0], rhs[1], s[::-1])
                for s in lhs.split("\n")
            ]
            return "\n".join(temp)
        else:
            return "\n".join([mirror(s, ctx) for s in lhs.split("\n")])
    elif vy_type(lhs) == NUMBER_TYPE:
        return mirror(lhs, ctx=ctx)
    else:
        return vectorise(vertical_mirror, lhs, rhs, ctx=ctx)


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
        (str): lambda: vectorise(
            vy_bin, wrapify(chr_ord(lhs, ctx=ctx)), ctx=ctx
        ),
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


def vy_exec(lhs, ctx):
    """Element Ė
    (str) -> vy_exec(a)
    (num) -> 1 / a
    """
    if vy_type(lhs) is str:
        import vyxal.transpile

        stack = ctx.stacks[-1]
        exec(vyxal.transpile.transpile(lhs))
        return []
    elif vy_type(lhs) == NUMBER_TYPE:
        return [divide(1, lhs, ctx)]
    else:
        return [vectorise(vy_exec, lhs, ctx)]


def vy_filter(lhs: Any, rhs: Any, ctx):
    """Element F
    (any, fun) -> Keep elements in a that b is true for
    (any, any) -> Remove elements of a that are in b
    """

    ts = vy_type(lhs, rhs)
    if ts[0] == types.FunctionType:
        return LazyList(
            filter(
                lambda x: safe_apply(rhs, x, ctx=ctx),
                iterable(lhs, range, ctx=ctx),
            )
        )
    elif ts[1] == types.FunctionType:
        return LazyList(
            filter(
                lambda x: safe_apply(lhs, x, ctx=ctx),
                iterable(rhs, range, ctx=ctx),
            )
        )
    elif ts == (str, str):
        return "".join(elem for elem in lhs if elem not in rhs)
    return LazyList([elem for elem in lhs if elem not in rhs])


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
        (NUMBER_TYPE): lambda: str(sympy.Rational(str(float(lhs)))),
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


def vy_sum(lhs, ctx=None):
    """Element ∑
    (any) -> reduce a by addition
    """

    return foldl(add, iterable(lhs, range, ctx=ctx), ctx=ctx)


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
        if ts == NUMBER_TYPE:
            lhs = sympy.Rational(str(float(lhs)))
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
        (NUMBER_TYPE): lambda: str(sympy.Rational(str(float(lhs)))),
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


def vy_round(lhs, ctx):
    """Element ṙ
    (num) -> round(a)
    (str) -> quad palindromize with overlap
    """

    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: round(lhs),
        (str): lambda: vertical_mirror(lhs, ctx=ctx)
        + "\n"
        + vertical_mirror(lhs, ctx=ctx)[::-1],
    }.get(ts)()


def vy_type(item, other=None, simple=False):
    if other is not None:
        return (vy_type(item, simple=simple), vy_type(other, simple=simple))
    if (x := type(item)) in (int, complex) or "sympy" in str(type(x)):
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


def wrap(lhs, rhs, ctx):
    """Element ẇ
    (any, num) -> a wrapped in chunks of length b
    (any, fun) -> Apply b to every second item of a
    (fun, any) -> Apply a to every second item of b
    (str, str) -> split a on first occurance of b
    """

    # Because textwrap.wrap doesn't consistently play nice with spaces

    ts = vy_type(lhs, rhs)
    if types.FunctionType in ts:
        function, vector = (
            (lhs, rhs) if ts[0] is types.FunctionType else (rhs, lhs)
        )
        return LazyList(
            safe_apply(function, vector[i], ctx=ctx) if i % 2 else vector[i]
            for i in range(len(vector))
        )

    else:
        if ts == (str, str):
            return list(lhs.partition(rhs)[::2])

        else:
            vector, chunk_size = (
                (iterable(lhs, ctx=ctx), rhs)
                if ts[1] is NUMBER_TYPE
                else (iterable(rhs, ctx=ctx), lhs)
            )
            ret, temp = [], []
            for item in vector:
                temp.append(item)
                if len(temp) == chunk_size:
                    if all([type(x) is str for x in temp]):
                        ret.append("".join(temp))
                    else:
                        ret.append(temp[::])
                    temp = []
            if len(temp) < chunk_size and temp:
                if all([type(x) is str for x in temp]):
                    ret.append("".join(temp))
                else:
                    ret.append(temp[::])
            return ret


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
    "∞": process_element(palindromise, 1),
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
        "top = pop(stack, 1, ctx); stack.append(deep_copy(top)); "
        "stack.append(top)",
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
    "A": process_element(all_true, 1),
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
    "W": ("print(stack); stack = [stack]; print(stack)", 0),
    # X doesn't need to be implemented here, because it's already a structure
    "Y": process_element(interleave, 2),
    "Z": process_element(vy_zip, 2),
    "^": ("stack = stack[::-1]", 0),
    "_": ("pop(stack, 1, ctx)", 1),
    "a": process_element(any_true, 1),
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
    "n": process_element("ctx.context_values[-1]", 0),
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
    "ḣ": (
        "top = pop(stack, 1, ctx); stack.append(head(top, ctx));"
        " stack.append(index(top, [1, None], ctx))",
        1,
    ),
    "ḭ": process_element(integer_divide, 2),
    "ŀ": process_element(ljust, 3),
    "ṁ": process_element(mean, 1),
    "ṅ": process_element(first_integer, 1),
    "ȯ": process_element(slice_from, 2),
    "ṗ": process_element(powerset, 1),
    "ṙ": process_element(vy_round, 1),
    "ṡ": process_element(sort_by, 2),
    "ṫ": (
        "top = pop(stack, 1, ctx);"
        " stack.append(index(top, [None, -1], ctx));"
        " stack.append(tail(top, ctx))",
        1,
    ),
    "ẇ": process_element(wrap, 2),
    "ẋ": process_element(repeat, 2),
    "ẏ": process_element("LazyList(range(0, len(iterable(lhs, ctx))))", 1),
    "ż": process_element("LazyList(range(1, len(iterable(lhs, ctx)) + 1))", 1),
    "√": process_element(square_root, 1),
    "₀": process_element("10", 0),
    "₁": process_element("100", 0),
    "₂": process_element(is_even, 1),
    "₃": process_element(is_divisible_by_three, 1),
    "₄": process_element("26", 0),
    "₅": (
        "top = pop(stack, 1, ctx); stack += is_divisible_by_five(top, ctx)",
        1,
    ),
    "₆": process_element("64", 0),
    "₇": process_element("128", 0),
    "₈": process_element("256", 0),
    "¶": process_element("'\\n'", 0),
    "⁋": process_element(join_newlines, 1),
    "§": process_element(vertical_join, 1),
    "ε": process_element(absolute_difference, 2),
    "¡": process_element(factorial, 1),
    "∑": process_element(vy_sum, 1),
    "¦": process_element(cumulative_sum, 1),
    "≈": process_element(all_equal, 1),
    "Ȧ": process_element(assign_iterable, 3),
    "Ḃ": (
        "top = pop(stack, 1, ctx); stack.append(deep_copy(top)); "
        "stack.append(reverse(top, ctx))",
        1,
    ),
    "Ċ": process_element(counts, 1),
    "Ḋ": (
        "rhs, lhs = pop(stack, 2, ctx); stack += is_divisible(lhs, rhs, ctx)",
        2,
    ),
    "Ė": ("stack += vy_exec(pop(stack, 1, ctx), ctx)", 1),
    "Ḟ": process_element(gen_from_fn, 2),
    "Ġ": process_element(group_consecutive, 1),
    "Ḣ": process_element(head_remove, 1),
    "İ": process_element(index_indices_or_cycle, 2),
    "Ŀ": process_element(transliterate, 3),
    "Ṁ": process_element(insert_or_map_nth, 3),
    "Ṙ": process_element(reverse, 1),
    "⌈": process_element(vy_ceil, 1),
    "⁼": process_element(non_vectorising_equals, 2),
    "ǎ": process_element(substrings, 1),
    "øḂ": process_element(angle_bracketify, 1),
    "øḃ": process_element(curly_bracketify, 1),
    "øb": process_element(parenthesise, 1),
    "øB": process_element(bracketify, 1),
    "øc": process_element(base_255_string_compress, 1),
    "øC": process_element(base_255_number_compress, 1),
    "ød": process_element(run_length_decoding, 1),
    "øe": process_element(run_length_encoding, 1),
    "ø↲": process_element(custom_pad_left, 3),
    "ø↳": process_element(custom_pad_right, 3),
    "øM": process_element(flip_brackets_vertical_palindromise, 1),
    "øW": process_element(group_on_words, 1),
    "øP": process_element(pluralise_count, 2),
    "øp": process_element(starts_with, 2),
    "øo": process_element(remove_until_no_change, 2),
    "øV": process_element(replace_until_no_change, 3),
    "kA": process_element('"ABCDEFGHIJKLMNOPQRSTUVWXYZ"', 0),
    "ke": process_element("math.e", 0),
    "kf": process_element('"Fizz"', 0),
    "kb": process_element('"Buzz"', 0),
    "kF": process_element('"FizzBuzz"', 0),
    "kH": process_element('"Hello, World!"', 0),
    "kh": process_element('"Hello World!"', 0),
    "k1": process_element("1000", 0),
    "k2": process_element("10000", 0),
    "k3": process_element("100000", 0),
    "k4": process_element("1000000", 0),
    "ka": process_element('"abcdefghijklmnopqrstuvwxyz"', 0),
    "kL": process_element(
        '"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"', 0
    ),
    "kd": process_element('"0123456789"', 0),
    "k6": process_element('"0123456789abcdef"', 0),
    "k^": process_element('"0123456789ABCDEF"', 0),
    "ko": process_element('"01234567"', 0),
    "kp": process_element("string.punctuatioin", 0),
    "kP": process_element(
        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        "!\\\"#$%&\\'()*+,-./:;<=>?@[\\\\]^_`{|}~",
        0,
    ),
    "kw": process_element('" \\t\\n\\r\\u000b\\u000c"', 0),
    "kr": process_element(
        '"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"', 0
    ),
    "kB": process_element(
        '"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"', 0
    ),
    "kZ": process_element('"ZYXWVUTSRQPONMLKJIHGFEDCBA"', 0),
    "kz": process_element(' "zyxwvutsrqponmlkjihgfedcba"', 0),
    "kl": process_element(
        '"ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba"', 0
    ),
    "ki": process_element("math.pi", 0),
    "kn": process_element("math.nan", 0),
    "kg": process_element("(1 + math.sqrt(5)) / 2", 0),
    "kD": process_element('currenttime.strftime("%Y-%m-%d")', 0),
    "kN": process_element(
        'LazyList(eval(currenttime.strftime("[%H,%M,%S]")))', 0
    ),
    "kḋ": process_element('currenttime.strftime("%d/%m/%Y")', 0),
    "kḊ": process_element('currenttime.strftime("%m/%d/%Y")', 0),
    "kð": process_element(
        'LazyList(eval(currenttime.strftime("[%d,%m,%Y]")))', 0
    ),
    "kβ": process_element('"{}[]<>()"', 0),
    "kḂ": process_element('"()[]{}"', 0),
    "kß": process_element('"()[]"', 0),
    "k≥": process_element('"([{<"', 0),
    "kΠ": process_element('")]}>"', 0),
    "kv": process_element('"aeiou"', 0),
    "kV": process_element('"AEIOU"', 0),
    "k∨": process_element('"aeiouAEIOU"', 0),
    "k⟇": process_element("codepage", 0),
    "k½": process_element("LazyList([1,2])", 0),
    "kḭ": process_element("2 ** 32", 0),
    "k+": process_element("LazyList([1, -1])", 0),
    "k-": process_element("LazyList([-1, 1])", 0),
    "k=": process_element("LazyList([0, 1])", 0),
    "k/": process_element('"/\\\\"', 0),
    "kR": process_element("360", 0),
    "kW": process_element('"https://"', 0),
    "k℅": process_element('"http://"', 0),
    "k↳": process_element('"https://www."', 0),
    "k²": process_element('"http://www."', 0),
    "k¶": process_element("512", 0),
    "k⁋": process_element("1024", 0),
    "k¦": process_element("2048", 0),
    "kṄ": process_element("4096", 0),
    "kṅ": process_element("8192", 0),
    "k¡": process_element("2 ** 14", 0),
    "kε": process_element("2 ** 15", 0),
    "k₴": process_element("2 ** 16", 0),
    "k×": process_element("2 ** 31", 0),
    "k⁰": process_element('"bcdfghjklmnpqrstvwxyz"', 0),
    "k¹": process_element('"bcdfghjklmnpqrstvwxz"', 0),
    "kT": process_element('"[]<>-+.,"', 0),
    "kṗ": process_element('LazyList("()","[]","{}","<>"])', 0),
    "kS": process_element('"ඞ"', 0),
    "k₂": process_element("2 ** 20", 0),
    "k₃": process_element("2 ** 30", 0),
    "k∪": process_element('"aeiouy"', 0),
    "k⊍": process_element('"AEIOUY"', 0),
    "k∩": process_element('"aeiouyAEIOUY"', 0),
}
modifiers: dict[str, str] = {
    "v": (
        "arguments = wrapify(pop(stack, function_A.arity, ctx=ctx))\n"
        + "stack.append"
        + "(vectorise(function_A, *(arguments[::-1]), explicit=True, ctx=ctx))"
        + "\n"
    ),
    "~": (
        "ctx.retain_popped = True\n"
        + "arguments = wrapify(pop(stack, function_A.arity, ctx=ctx))\n"
        + "ctx.retain_popped = False\n"
        + "stack.append(safe_apply(function_A, *(arguments[::-1]), ctx=ctx))\n"
    ),
}
