"""This is where the element functions are stored

(that is, functions directly corresponding to Vyxal elements). It's also where
the python equivalent of command is stored
"""

import collections
import itertools
import math
import random
import re
import string
import sys
import types
import urllib
import json
from datetime import datetime
from typing import Callable, Union

import num2words
import sympy

from vyxal import dictionary
from vyxal.context import DEFAULT_CTX, Context
from vyxal.encoding import (
    base_27_alphabet,
    codepage_number_compress,
    codepage_string_compress,
    compression,
    codepage,
)
from vyxal.helpers import *
from vyxal.LazyList import LazyList, lazylist
from vyxal.Canvas import Canvas

NUMBER_TYPE = "number"
SCALAR_TYPE = "scalar"

EPSILON = 1e-10


def process_element(
    expr: Union[str, Callable[..., Any]], arity: int
) -> tuple[str, int]:
    """Take a python expression and adds boilerplate for element functions to it

    expr can be a string, which will be added verbatim to the transpiled output,
    or a function, for which a function call will be generated.

    See documents/specs/Transpilation.md for information on what happens here.
    """
    if arity > 0:
        arguments = ["fourth", "third", "rhs", "lhs"][-arity:]
    else:
        arguments = ["_"]
    if isinstance(expr, types.FunctionType):
        pushed = f"{expr.__name__}({', '.join(arguments[::-1])}, ctx=ctx)"
    else:
        pushed = expr

    if arity != -1:
        py_code = (
            f"{', '.join(arguments)} = pop(stack, {arity}, ctx); "
            f"stack.append({pushed})"
        )
    else:
        py_code = f"stack.append({pushed})"
    return py_code, arity


def absolute_difference(lhs, rhs, ctx):
    """Element ε
    (num, num) -> abs(a - b)
    (num, str) -> Array of length a filled with b
    (str, num) -> Array of length b filled with a
    (str, str) -> single regex match of b against a
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: abs(lhs - rhs),
        (NUMBER_TYPE, str): lambda: [rhs] * lhs,
        (str, NUMBER_TYPE): lambda: [lhs] * rhs,
        (str, str): lambda: re.match(rhs, lhs)
        and re.match(rhs, lhs).group()
        or "",
    }.get(ts, lambda: vectorise(absolute_difference, lhs, rhs, ctx=ctx))()


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


def adjacency_matrix_dir(lhs, ctx):
    """Element Þa
    (lst) -> adjacency matrix of directed graph.
    If A_ij is nonzero, it means there are edges from i to j"""
    graph = edges_to_dir_graph(lhs, ctx=ctx)
    vertices = vy_sort(graph.keys(), ctx=ctx)
    adj = [[0] * len(vertices) for _ in vertices]
    for i, elem1 in enumerate(vertices):
        for j, elem2 in enumerate(vertices):
            adj[i][j] += graph[elem1].count(elem2)
    return adj


def adjacency_matrix_undir(lhs, ctx):
    """Element ÞA
    (lst) -> adjacency matrix of undirected graph"""
    graph = edges_to_undir_graph(lhs, ctx=ctx)
    vertices = vy_sort(graph.keys(), ctx=ctx)
    adj = [[0] * len(vertices) for _ in vertices]
    for i, elem1 in enumerate(vertices):
        for j in range(i + 1):
            elem2 = vertices[j]
            n_edges = graph[elem1].count(elem2)
            adj[i][j] += n_edges
            adj[j][i] += n_edges
    return adj


def align_left(lhs, ctx):
    """Element øŀ
    (str) -> left-aligned string
    (lst) -> left-align lines
    """
    ts = vy_type(lhs)

    if ts == str:
        lhs = lhs.split("\n")

    lhs = [str(line) for line in lhs]

    maxlen = max(len(line) for line in lhs)

    result = [line.ljust(maxlen) for line in lhs]

    if ts == str:
        return "\n".join(result)

    return result


def align_right(lhs, ctx):
    """element øɽ
    (str) -> right-aligned string
    (lst) -> right-align lines
    """
    ts = vy_type(lhs)

    if ts == str:
        lhs = lhs.split("\n")

    lhs = [str(line) for line in lhs]

    maxlen = max(len(line) for line in lhs)

    result = [line.rjust(maxlen) for line in lhs]

    if ts == str:
        return "\n".join(result)

    return result


def all_antidiagonals(lhs, ctx):
    """Element Þḋ
    Anti-diagonals of a matrix, starting with the main anti-diagonal.
    """
    vector = [iterable(x, ctx=ctx) for x in lhs]
    if not vector:
        return []
    all_diags = [[] for _ in range(len(vector) + len(vector[0]) - 1)]
    start = 0
    for row in vector:
        for i in range(len(vector[0])):
            all_diags[
                (start - i + min(len(vector), len(vector[0])) - 1)
                % len(all_diags)
            ].append(row[i])
        start -= 1
    return all_diags


def all_combos(lhs, ctx):
    """Element Þx
    (any) -> all combinations without replacement of lhs (all lengths)
    """

    @lazylist_from(lhs)
    def gen():
        i = 1
        for _ in lhs:
            combo = itertools.combinations(lhs, i)
            for item in combo:
                for x in itertools.permutations(item):
                    if all(isinstance(y, str) for y in x):
                        x = "".join(x)
                    yield vyxalify(x)
            i += 1

    return gen()


def all_combos_with_replacement(lhs, ctx):
    """Element Þ×
    (any) -> all combinations with replacement of lhs (all lengths)
    """

    @lazylist_from(lhs)
    def gen():
        i = 1
        for _ in lhs:
            combo = itertools.combinations_with_replacement(lhs, i)
            for x in combo:
                if all(isinstance(y, str) for y in x):
                    x = "".join(x)
                yield vyxalify(x)
            i += 1

    return gen()


def all_diagonals(lhs, ctx):
    """Element ÞD
    Diagonals of a matrix, starting with the main diagonal.
    """
    vector = [iterable(x, ctx=ctx) for x in lhs]
    if not vector:
        return []
    all_diags = [[] for _ in range(len(vector) + len(vector[0]) - 1)]
    start = 0
    for row in vector:
        for i in range(len(vector[0])):
            all_diags[(start + i) % len(all_diags)].append(row[i])
        start -= 1
    return all_diags


def all_equal(lhs, ctx):
    """Element ≈
    (any) -> are all items in a the same?
    """
    lhs = iterable(lhs, ctx=ctx)
    first = None
    for item in lhs:
        if first is None:
            first = item
        elif not non_vectorising_equals(item, first, ctx):
            return 0
    return 1


def all_indices_multidim(lhs, rhs, ctx):
    """Element ÞI
    (lst, any) -> All indices of rhs in lhs (multidimensional)
    (num|str, lst) -> All indices of lhs in rhs (multidimensional)
    (num|str, num|str) -> All indices of rhs in lhs (multidimensional)
    """
    ts = vy_type(lhs, rhs, simple=True)

    if ts == (str, str):
        return [i for i in range(len(lhs)) if lhs.startswith(rhs, i)]

    if ts[0] != list and ts[1] == list:
        temp = lhs
        lhs = iterable(rhs, ctx=ctx)
        rhs = temp

    @lazylist_from(lhs)
    def gen():
        for ind, item in enumerate_md(lhs, include_all=True):
            if non_vectorising_equals(item, rhs, ctx):
                yield ind

    return gen()


def all_less_than_increasing(lhs, rhs, ctx):
    """Element Þ<
    (any, num): All values of a up to (not including) the first greater
                than or equal to b
    """
    lhs = iterable(lhs, ctx)

    @lazylist_from(lhs)
    def gen():
        for elem in lhs:
            if elem < rhs:
                yield elem
            else:
                return

    return gen()


def all_multiples(lhs, ctx):
    """Element ¨*
    (num) -> [a*1, a*2, a*3, a*4, ...]
    (str) -> [a*1, a*2, a*3, a*4, ...]
    """
    return multiply(lhs, infinite_positives(ctx), ctx)


def all_partitions(lhs, ctx):
    """Element øṖ
    (any) -> all_partitions(a)
    """
    if primitive_type(lhs) == SCALAR_TYPE:
        temp = all_partitions(list(iterable(lhs, ctx=ctx)), ctx)
        if isinstance(lhs, str):
            return LazyList((map(lambda x: "".join(x), x)) for x in temp)

    shapes = integer_parts_or_join_spaces(len(lhs), ctx=ctx)

    @lazylist
    def gen():
        for shape in shapes:
            for i in range(len(shape)):
                temp = rotate_left(shape, i, ctx)
                temp = repeat(temp, temp, ctx)
                yield [
                    wrapify(x, ctx=ctx) for x in log_mold_multi(lhs, temp, ctx)
                ]

    return uniquify(gen(), ctx=ctx)


def all_slices(lhs, rhs, ctx):
    """Element Þs
    (lst, int) -> Get all slices of a list, skipping a certain number of items
    (int, lst) -> Same as (lst, int) but swapped
    """
    ts = vy_type(lhs, rhs)
    lhs, rhs = (rhs, lhs) if ts[1] != NUMBER_TYPE else (lhs, rhs)
    lhs = iterable(lhs, ctx=ctx)

    return LazyList(index(lhs, [start, None, rhs], ctx) for start in range(rhs))


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


def all_unique(lhs, ctx):
    """Element Þu
    (any) -> Are all elements of a unique?
    """
    return int(len(uniquify(lhs, ctx)) == len(iterable(lhs, ctx=ctx)))


def alternating_negations(lhs, ctx):
    """Element ÞN
    (any) -> alternating negations of lhs
    """

    @infinite_lazylist
    def gen():
        flag = False
        while True:
            yield negate(lhs, ctx) if flag else lhs
            flag = not flag

    return gen()


def angle_bracketify(lhs, ctx):
    """Element øḂ
    (any) -> "<" + lhs + ">"
    (lst) -> vectorised
    """
    if vy_type(lhs, simple=True) is list:
        return vectorise(angle_bracketify, lhs)
    return "<" + str(lhs) + ">"


def anti_diagonal(lhs, ctx):
    """Element Þ\\
    (lst) -> Antidiagonal of matrix
    """
    lhs = [iterable(elem, ctx=ctx) for elem in iterable(lhs, ctx=ctx)]
    m = min(len(lhs), len(lhs[0]))
    return [lhs[i][m - i - 1] for i in range(m)]


def any_true(lhs, ctx):
    """Element a
    (lst) -> any of lhs is truthy?
    (str) -> is_capital_letter (vectorises over multichar strings)
    """
    if isinstance(lhs, str):
        if len(lhs) == 1:
            return int(91 >= ord(lhs) >= 65)
        else:
            return [int(91 >= ord(char) >= 65) for char in lhs]
    return int(any(iterable(lhs, ctx=ctx)))


def apply_at(lhs, rhs, other, ctx):
    """Element ¨M
    (lst, lst, fun) -> Map a function to elements of a list whose
                       indices are in another list
    """
    if vy_type(lhs) == types.FunctionType:
        return apply_at(rhs, other, lhs, ctx)
    if vy_type(rhs) == types.FunctionType:
        return apply_at(lhs, other, rhs, ctx)
    lhs = iterable(lhs, ctx=ctx)
    rhs = wrapify(rhs)
    for pos in rhs:
        lhs = assign_iterable(
            lhs, pos, safe_apply(other, index(lhs, pos, ctx), ctx=ctx), ctx
        )

    return lhs


def arccos(lhs, ctx):
    """Element ∆C
    (num) -> arccos(lhs)
    (str) -> arccos(expression)
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: sympy.nsimplify(sympy.acos(lhs), rational=True),
        (str): lambda: str(
            sympy.nsimplify(sympy.acos(make_expression(lhs)), rational=True)
        ),
    }.get(ts, lambda: vectorise(arccos, lhs, ctx=ctx))()


def arcsin(lhs, ctx):
    """Element ∆S
    (num) -> arcsin(a)
    (str) -> arcsin(expression)
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: sympy.nsimplify(sympy.asin(lhs), rational=True),
        (str): lambda: str(
            sympy.nsimplify(sympy.asin(make_expression(lhs)), rational=True)
        ),
    }.get(ts, lambda: vectorise(arcsin, lhs, ctx=ctx))()


def arctan(lhs, ctx):
    """Element ∆T
    (num) -> arctan(a)
    (str) -> arctan(expression)
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: sympy.nsimplify(sympy.atan(lhs), rational=True),
        (str): lambda: str(
            sympy.nsimplify(sympy.atan(make_expression(lhs)), rational=True)
        ),
    }.get(ts, lambda: vectorise(arctan, lhs, ctx=ctx))()


def assign_iterable(lhs, rhs, other, ctx):
    """Element Ȧ
    (any, num, any) -> a but item b (0-indexed) is set to c
    """
    lhs = iterable(lhs, ctx=ctx)
    if type(rhs) is str:
        rhs = chr_ord(rhs, ctx)

    if vy_type(rhs, simple=True) is list:
        if vy_type(other, simple=True) is list:
            for a in vy_zip(rhs, other, ctx=ctx):
                lhs = assign_iterable(lhs, *a, ctx)
        else:
            for item in rhs:
                lhs = assign_iterable(lhs, item, other, ctx)
        return lhs

    if type(lhs) is str:
        if len(lhs) <= rhs:
            lhs += " " * (rhs - len(lhs) + 1)
        lhs = list(lhs)
        lhs[rhs] = other
        return vy_sum(lhs, ctx=ctx)
    else:

        @lazylist_from(lhs)
        def gen():
            temp = lhs[:rhs]
            if len(temp) != rhs and rhs > -1:
                temp += [0] * abs(rhs - len(temp))
            yield from temp
            yield other
            if rhs != -1:
                yield from lhs[rhs + 1 :]

        return gen()


def base_255_number_compress(lhs, ctx):
    """Element øC
    (num) -> Compress a number in base 255
    """
    return "»" + to_base(lhs, codepage_number_compress, ctx) + "»"


def base_255_string_compress(lhs, ctx):
    """Element øc
    (str) -> Compress a string of lowercase letters and spaces in base 255
    """
    return (
        "«"
        + to_base(
            from_base(lhs, base_27_alphabet, ctx),
            codepage_string_compress,
            ctx,
        )
        + "«"
    )


def binary_string(lhs, ctx: Context):
    if vy_type(lhs, simple=True) == list:
        return vectorise(binary_string, lhs, ctx=ctx)
    return bin(lhs).replace("0b", "")


def bitwise_and(lhs, rhs, ctx):
    """Element ⋏
    (num, num) -> a & b
    (num, str) -> b.center(a)
    (str, num) -> a.center(b)
    (str, str) -> a.center(len(b) - len(a))
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: int(lhs) & int(rhs),
        (NUMBER_TYPE, str): lambda: rhs.center(lhs),
        (str, NUMBER_TYPE): lambda: lhs.center(rhs),
        (str, str): lambda: lhs.center(abs(len(rhs) - len(lhs))),
    }.get(ts, lambda: vectorise(bitwise_and, lhs, rhs, ctx=ctx))()


def bitwise_not(lhs, ctx):
    """Element ꜝ
    (num) -> ~a
    (str) -> any_upper(a)
    (list) -> filter(a, is_truthy)
    """
    if vy_type(lhs) is NUMBER_TYPE:
        return ~int(lhs)
    elif vy_type(lhs, simple=True) is list:
        return vy_filter(lhs, boolify, ctx=ctx)
    else:
        return int(any(char.isupper() for char in str(lhs)))


def bitwise_or(lhs, rhs, ctx):
    """Element ⋎
    (num, num) -> a | b
    (num, str) -> b[:a]+b[a+1:]
    (str, num) -> a[:b]+a[b+1:]
    (str, str) -> merge_join(a,b)
    """
    ts = vy_type(lhs, rhs)
    if ts == (str, str):
        suffix_set = {lhs[-i:] for i in range(1, len(lhs) + 1)}
        prefix_set = {rhs[:i] for i in range(1, len(rhs) + 1)}
        common = suffix_set & prefix_set
        if len(common) == 0:
            return lhs + rhs
        common = sorted(common, key=lambda x: len(x))[-1]
        return lhs[: -len(common)] + common + rhs[len(common) :]
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: int(lhs) | int(rhs),
        (NUMBER_TYPE, str): lambda: rhs[:lhs] + rhs[lhs + 1 :],
        (str, NUMBER_TYPE): lambda: lhs[:rhs] + lhs[rhs + 1 :],
    }.get(ts, lambda: vectorise(bitwise_or, lhs, rhs, ctx=ctx))()


def bitwise_xor(lhs, rhs, ctx):
    """Element ꘍
    (num, num) -> a ^ b
    (num, str) -> " " * a + b
    (str, num) -> a + " " * b
    (str, str) -> levenshtein_distance(a,b)
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: int(lhs) ^ int(rhs),
        (NUMBER_TYPE, str): lambda: " " * lhs + rhs,
        (str, NUMBER_TYPE): lambda: lhs + " " * rhs,
        (str, str): lambda: levenshtein_distance(lhs, rhs),
    }.get(ts, lambda: vectorise(bitwise_xor, lhs, rhs, ctx=ctx))()


def boolean_partition(lhs, rhs, ctx):
    """Element Þṗ
    (lst, lst) -> Split lhs on truthy indices in rhs
    """
    return partition_at(rhs, lhs)


def boolify(lhs, ctx):
    """Element ḃ
    (any) -> is truthy?
    """
    if ctx.vectorise_boolify and vy_type(lhs, simple=True) is list:
        return vectorise(boolify, lhs, ctx=ctx)
    else:
        return int(bool(lhs))


def bracketify(lhs, ctx):
    """Element øB
    (any) -> "[" + lhs + "]"
    (lst) -> vectorised
    """
    if vy_type(lhs, simple=True) is list:
        return vectorise(bracketify, lhs)
    return "[" + str(lhs) + "]"


def brackets_balanced(lhs, ctx):
    """Element øβ
    (str) -> is lhs balanced?
    """
    brackets = {"(": ")", "[": "]", "{": "}", "<": ">"}
    temp = []
    for char in lhs:
        if char in brackets:
            temp.append(brackets[char])
        elif char in brackets.values():
            if temp and temp[-1] != char:
                return 0
            elif not temp:
                return 0
            else:
                temp.pop()
    return int(len(temp) == 0)


def canvas_draw(lhs, rhs, other, ctx):
    """Element ø^
    Creates an empty canvas and draws on it, returning the result. Does some complex type overloading.
        (num, lst, str) -> Draw with a = length, b = dirs, c = text
        (num, str, str) -> Draw with a = length, b/c dependent on dir validity
        (any, num, any) -> Draw with b = length ^
        (any, any, num) -> Draw with c = length ^
        (str, any, any) -> Draw with a = text, b/c dependent on dir validity
        (lst, str, any) -> Draw with b = text, ^
        (lst, lst, str) -> Draw with c = text, ^
    """
    new_canvas = Canvas()
    new_canvas.draw(*overloaded_canvas_draw(lhs, rhs, other, ctx=ctx))

    return str(new_canvas)


def canvas_global_draw(lhs, rhs, other, ctx):
    """Element ø∧
    Draws on the global canvas, returning nothing. Does some complex type overloading.
        (num, lst, str) -> Draw with a = length, b = dirs, c = text
        (num, str, str) -> Draw with a = length, b/c dependent on dir validity
        (any, num, any) -> Draw with b = length ^
        (any, any, num) -> Draw with c = length ^
        (str, any, any) -> Draw with a = text, b/c dependent on dir validity
        (lst, str, any) -> Draw with b = text, ^
        (lst, lst, str) -> Draw with c = text, ^
    """
    ctx.canvas.draw(*overloaded_canvas_draw(lhs, rhs, other, ctx=ctx))


def carmichael_function(lhs, ctx):
    """Element ∆¢
    (num) -> is lhs a Carmichael number?
    (str) -> local maxima
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.ntheory.reduced_totient(lhs),
        str: lambda: local_maxima(lhs),
    }.get(ts, lambda: vectorise(carmichael_function, lhs, ctx=ctx))()


def cartesian_over_list(lhs, ctx):
    """Element Þ*
    (lst) -> itertools.product(*lhs)
    """
    # todo maybe handle generators separately
    lhs = [iterable(elem, ctx=ctx) for elem in iterable(lhs, ctx=ctx)]
    return LazyList(
        "".join(x) if all(isinstance(y, str) for y in x) else list(x)
        for x in itertools.product(*lhs)
    )


def cartesian_power(lhs, rhs, ctx):
    """Element ÞẊ
    (any, num) -> cartesian_power(a, b)
    (num, any) -> cartesian_power(b, a)
    """
    ts = vy_type(lhs, rhs)
    if NUMBER_TYPE not in ts:
        return rhs

    if ts[0] == NUMBER_TYPE and ts[1] != NUMBER_TYPE:
        vector, n = rhs, lhs
    else:
        vector, n = lhs, rhs
    vector = (
        list(vector)
        if isinstance(vector, str)
        else iterable(vector, ctx=ctx.copy(number_as_range=True))
    )
    n = int(n)
    if n < 1 or not vector:
        return []
    elif n == 1:
        return vector
    elif not isinstance(vector, LazyList):
        return LazyList(
            "".join(x) if all(isinstance(y, str) for y in x) else x
            for x in itertools.product(vector, repeat=n)
        )
    else:
        # Will be updated later
        length = None

        def gen_diag(
            diag_num: int, dim: int = 0, prevss: list[list[list]] = None
        ):
            """Generate the diag_num'th n-dimensional diagonal slice
            The ith element of prevss is a list of all the previous
            partial power elements whose indices in vector added up to i
            """
            nonlocal length
            if prevss is None:
                prevss = [[] for _ in range(diag_num)]
            if dim == n:
                return prevss[-1]
            new_prevss = [[] for _ in range(diag_num)]
            end = diag_num if length is None else min(diag_num, length)
            for i in range(end):
                if not has_ind(vector, i):
                    length = i
                    break
                curr = vector[i]
                for j in range(diag_num - i):
                    prevs = prevss[j]
                    if not prevs:
                        if j == 0:
                            new_prev = [vector[0] for _ in range(dim + 1)]
                            new_prev[dim] = curr
                            new_prevss[i + j] = [new_prev]
                    else:
                        for prev in prevs:
                            new_prevss[i + j].append(prev + [curr])
            return gen_diag(diag_num, dim + 1, new_prevss)

        @lazylist_from(vector)
        def gen():
            diag_num = 1
            while True:
                diag = gen_diag(diag_num)
                if length and not vector.infinite:
                    if math.ceil((diag_num - 1) / n) >= length:
                        break
                if diag:
                    yield from diag
                else:
                    break
                diag_num += 1

        return gen()


def cartesian_product(lhs, rhs, ctx):
    """Element Ẋ
    (any, any) -> cartesian product of lhs and rhs
    (fun, any) -> Apply a to b until no change (fixpoint)
    (any, fun) -> Apply a to b until no change (fixpoint)
    """
    ts = vy_type(lhs, rhs)
    if types.FunctionType in ts:
        fn, arg = (lhs, rhs) if ts[0] == types.FunctionType else (rhs, lhs)
        prev = arg
        arg = safe_apply(fn, arg, ctx=ctx)
        while simplify(prev) != simplify(arg):
            prev = arg
            arg = safe_apply(fn, arg, ctx=ctx)
        return prev
    elif ts == (str, str):
        return [left + right for left in lhs for right in rhs]
    else:
        lhs = iterable(lhs, ctx=ctx.copy(number_as_range=True))
        rhs = iterable(rhs, ctx=ctx.copy(number_as_range=True))

        def gen():
            if not (lhs and rhs):
                return

            diag_num = 0
            lhs_max = len(lhs) - 1 if isinstance(lhs, list) else None
            rhs_max = len(rhs) - 1 if isinstance(rhs, list) else None
            while True:
                lhs_start = max(0, diag_num - rhs_max) if rhs_max else 0
                lhs_end = min(diag_num, lhs_max) if lhs_max else diag_num
                # Whether at least 1 new pair was yielded
                touched = False
                for left in range(lhs_start, lhs_end + 1):
                    right = diag_num - left
                    if rhs_max and right > rhs_max:
                        continue
                    if not has_ind(rhs, right):
                        rhs_max = right
                        continue
                    if lhs_max and left > lhs_max:
                        break
                    if not has_ind(lhs, left):
                        lhs_max = left
                        break
                    touched = True
                    yield [lhs[left], rhs[right]]
                if not touched:
                    break
                diag_num += 1

        return LazyList(
            gen(),
            isinf=(
                (type(lhs) is LazyList and lhs.infinite)
                or (type(rhs) is LazyList and rhs.infinite)
            ),
        )


def center(lhs, ctx):
    """Element øĊ
    (list) -> center align list by padding with spaces
    """
    lhs = vectorise(vy_str, lhs, ctx=ctx)
    focal = max(map(lambda x: len(iterable(x, ctx=ctx)), lhs))
    return [line.center(focal) for line in lhs]


def chr_ord(lhs, ctx):
    """Element C
    (num) -> chr(a)
    (str) -> ord(a)
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: chr(int(lhs)),
        (str): lambda: list(map(ord, lhs))
        if len(lhs) > 1
        else ord(lhs)
        if lhs
        else [],
    }.get(ts, lambda: vectorise(chr_ord, lhs, ctx=ctx))()


def codepage_digraph(lhs, ctx):
    """Element ø⟇
    (num) -> vyxal_codepage[a]
    (str) -> vyxal_codepage.index(a)
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: vyxal.encoding.codepage[int(lhs)],
        (str): lambda: vyxal.encoding.codepage.find(lhs)
        if len(lhs) <= 1
        else vectorise(codepage_digraph, list(lhs), ctx=ctx),
    }.get(ts, lambda: vectorise(codepage_digraph, lhs, ctx=ctx))()


def combinations_with_replacement(lhs, rhs, ctx):
    """Element ↔
    (any, num) -> combinations of lhs of length rhs with replacement
    (any, non-num) -> remove elements in lhs that are not in rhs
    (fun, any) -> apply lhs on rhs until the result does not change. Collects intermediate values
    (any, fun) -> apply rhs on lhs until the result does not change. Collects intermediate values
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, ts[1]): lambda: vyxalify(
            itertools.product(iterable(rhs, ctx), repeat=lhs)
        ),
        (ts[0], NUMBER_TYPE): lambda: vyxalify(
            itertools.product(iterable(lhs, ctx), repeat=rhs)
        ),
        (types.FunctionType, ts[1]): lambda: fixed_point(lhs, rhs, ctx=ctx),
        (ts[0], types.FunctionType): lambda: fixed_point(rhs, lhs, ctx=ctx),
    }.get(ts, lambda: set_intersection(lhs, rhs))()


def complement(lhs, ctx):
    """Element ⌐
    (num) -> 1 - a
    (str) -> a.split(",")
    """
    ts = vy_type(lhs)
    return {NUMBER_TYPE: lambda: 1 - lhs, str: lambda: lhs.split(",")}.get(
        ts, lambda: vectorise(complement, lhs, ctx=ctx)
    )()


def connected_uniquify(lhs, ctx: Context):
    """Element ÞǓ
    (any) -> connected uniquify a (Ġvh)
    """
    ts = vy_type(lhs, simple=True)
    return {
        NUMBER_TYPE: lambda: sympy.nsimplify(
            connected_uniquify(str(lhs), ctx=ctx), rational=True
        ),
        str: lambda: "".join(x[0] for x in group_consecutive(lhs, ctx=ctx)),
        list: lambda: LazyList(x[0] for x in group_consecutive(lhs, ctx=ctx)),
    }.get(ts)()


def contains(lhs, rhs, ctx):
    """Element c
    (any, fun) -> first item in a where b is truthy
    (any, any) -> does a contain b
    """
    ts = vy_type(lhs, rhs, simple=True)
    if types.FunctionType in ts:
        fn, arg = (lhs, rhs) if ts[0] == types.FunctionType else (rhs, lhs)
        return vy_filter(fn, arg, ctx=ctx)[0]
    if list in ts:
        lhs, rhs = (
            (rhs, lhs) if primitive_type(lhs) == SCALAR_TYPE else (lhs, rhs)
        )
        lhs = iterable(lhs, ctx=ctx)
        return int(rhs in lhs)
    return int(vy_str(rhs, ctx=ctx) in vy_str(lhs, ctx=ctx))


def cookie(_, ctx):
    while 1:
        vy_print("cookie", ctx=ctx)


def coords_deepmap(lhs, rhs, ctx):
    """Element ÞZ
    (any, fun) -> For each value of a (all the way down) call b with the
                  coordinates of that value and put that at the
                  appropriate position in a.

    Or, as hyper said: for each value of a, call b with the coordinates
    of that value is just deepmap(b, multidimindex(a))

    https://chat.stackexchange.com/transcript/message/59662626#59662626
    """
    lhs, rhs = (lhs, rhs) if type(rhs) is types.FunctionType else (rhs, lhs)
    # arrange so that lhs is always the list and rhs is always the
    # function

    lhs = iterable(lhs, ctx=ctx)  # Make sure lhs is actually iterable

    def f(a, g, pos=()):
        return [
            f(b, g, (*pos, i))
            if vy_type(b, simple=True) == list
            else safe_apply(g, [*pos, i], ctx=ctx)
            for i, b in enumerate(a)
        ]

    # the above curtosey of pxeger
    # https://chat.stackexchange.com/transcript/message/59662694#59662694
    # thank you very cool

    return f(lhs, rhs)


def copy_sign(lhs, rhs, ctx):
    """Element ∆±
    (num, num) -> math.copysign(a, b)
    """
    return multiply(
        vy_abs(lhs, ctx), (-1 if less_than(rhs, 0, ctx) else 1), ctx
    )


def cosine(lhs, ctx):
    """Element ∆c
    (num) -> cosine(a)
    (str) -> cosine(expression)
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.nsimplify(sympy.cos(lhs), rational=True),
        str: lambda: str(
            sympy.nsimplify(sympy.cos(make_expression(lhs)), rational=True)
        ),
    }.get(ts, lambda: vectorise(cosine, lhs, ctx=ctx))()


def count_item(lhs, rhs, ctx):
    """Element O
    (any, any) -> returns the number of occurances of b in a
    (any, fun) -> all elements in a where the result of b(x) is highest
    """
    if vy_type(lhs) == types.FunctionType:
        lhs, rhs = rhs, lhs
    if vy_type(rhs) == types.FunctionType:
        lhs = iterable(lhs, ctx=ctx)
        if not lhs:
            return lhs
        m = None
        fun_vals = []
        for item in lhs:
            fn_val = safe_apply(rhs, item, ctx=ctx)
            fun_vals.append(fn_val)
            if m is None:
                m = fn_val
            else:
                m = dyadic_maximum(m, fn_val, ctx=ctx)

        return LazyList(item for item in lhs if fun_vals.pop(0) == m)
    if (primitive_type(lhs), primitive_type(rhs)) == (SCALAR_TYPE, list):
        lhs, rhs = rhs, lhs
    if vy_type(lhs) in (NUMBER_TYPE, str):
        lhs, rhs = str(lhs), str(rhs)
    return iterable(lhs, ctx=ctx).count(rhs)


def count_overlapping(lhs, rhs, ctx):
    """Element øO
    (any, any) -> returns the number of overlapping occurances of b in a
    """
    lhs = iterable(lhs, ctx=ctx)
    rhs = iterable(rhs, ctx=ctx)
    count = 0
    for i in range(len(lhs)):
        if lhs[i : len(rhs) + i] == rhs:
            count += 1
    return count


def counts(lhs, ctx):
    """Element Ċ
    (any) -> Counts: [[x, a.count(x)] for x in a]"""
    temp = uniquify(lhs, ctx=ctx)
    return [[x, count_item(lhs, x, ctx)] for x in temp]


def cumul_sum_sans_last_prepend_zero(lhs, ctx):
    """Element ÞR
    Remove the last item of the cumulative sums of a list and prepend 0.
    """
    return prepend(cumulative_sum(lhs[:-1], ctx=ctx), 0, ctx)


def cumulative_sum(lhs, ctx):
    """Element ¦
    (any) -> cumulative sum of a
    """
    if lhs == "":
        return []
    elif vy_type(lhs, simple=True) is list and len(lhs) == 0:
        return []
    return LazyList(scanl(add, iterable(lhs, ctx=ctx), ctx))


def curly_bracketify(lhs, ctx):
    """Element øḃ
    (any) -> "[" + lhs + "]"
    (lst) -> vectorised
    """
    if vy_type(lhs, simple=True) is list:
        return vectorise(curly_bracketify, lhs)
    return "{" + str(lhs) + "}"


def custom_pad_left(lhs, rhs, other, ctx):
    """Element ø↲
    (any, num, str) -> pad a on the left with c to length b
    (any, str, num) -> pad a on the left with b to length c
    (lst, any, any) -> vectorised
    """
    if isinstance(lhs, LazyList):
        return vectorise(custom_pad_left, lhs, rhs, other)
    if vy_type(rhs) == NUMBER_TYPE:
        return lhs.ljust(int(rhs), other)
    if vy_type(other) == NUMBER_TYPE:
        return lhs.ljust(int(other), rhs)


def custom_pad_right(lhs, rhs, other, ctx):
    """Element ø↳
    (any, num, str) -> pad a on the right with c to length b
    (any, str, num) -> pad a on the right with b to length c
    (lst, any, any) -> vectorised
    """
    if isinstance(lhs, LazyList):
        return vectorise(custom_pad_left, lhs, rhs, other)
    if vy_type(rhs) == NUMBER_TYPE:
        return lhs.rjust(int(rhs), other)
    if vy_type(other) == NUMBER_TYPE:
        return lhs.rjust(int(other), rhs)


@infinite_lazylist
def cycle(lhs, ctx):
    """Element Þċ
    (any) -> infinite list of elements of a
    """
    lhs = iterable(lhs, range, ctx=ctx)
    while True:
        yield from lhs


def decrement(lhs, ctx):
    """Element ‹
    (num) -> a - 1
    (str) -> a + "-"
    """
    ts = vy_type(lhs)
    return {NUMBER_TYPE: lambda: lhs - 1, str: lambda: lhs + "-"}.get(
        ts, lambda: vectorise(decrement, lhs, ctx=ctx)
    )()


def decrement_until_false(lhs, rhs, ctx):
    """Element ∆‹
    (any, fun) -> while b(a): a -= 1
    (fun, any) -> while a(b): b -= 1
    """
    function, value = (
        (rhs, lhs) if type(rhs) is types.FunctionType else (lhs, rhs)
    )
    while safe_apply(function, value, ctx=ctx):
        value = decrement(value, ctx)
    return value


def deep_flatten(lhs, ctx):
    """Element f
    (any) -> flatten list completely
    """

    @lazylist_from(lhs)
    def gen():
        for item in iterable(lhs, ctx=ctx):
            if type(item) in (LazyList, list):
                yield from deep_flatten(item, ctx)
            else:
                yield item

    return gen()


def deltas(lhs, ctx):
    """Element ¯
    (any) -> deltas of a
    """
    lhs = iterable(lhs, ctx=ctx)

    @lazylist_from(lhs)
    def gen():
        prev = None
        for item in lhs:
            if prev is not None:
                yield subtract(item, prev, ctx=ctx)
            prev = item

    return gen()


def depth(lhs, ctx=None):
    """Element Þj
    (lst) -> depth of a
    (any) -> 0
    """
    if vy_type(lhs, simple=True) == list:
        return max(map(depth, lhs)) + 1 if lhs else 1
    else:
        return 0


def diagonal(lhs, ctx):
    """Element Þ/
    (any) -> diagonal of a
    """
    lhs = [iterable(elem, ctx=ctx) for elem in iterable(lhs, ctx=ctx)]
    if not lhs:
        return []
    return [lhs[i][i] for i in range(min(len(lhs), len(lhs[0])))]


def dist_matrix_dir(lhs, ctx):
    """Element Þd
    (lst) -> distance matrix of directed graph"""
    graph = edges_to_dir_graph(lhs, ctx=ctx)
    vertices = vy_sort(graph.keys(), ctx=ctx)
    return [
        [graph_distance(graph, elem1, elem2) for elem2 in vertices]
        for elem1 in vertices
    ]


def dist_matrix_undir(lhs, ctx):
    """Element Þw
    (lst) -> distance matrix of undirected graph"""
    graph = edges_to_undir_graph(lhs, ctx=ctx)
    vertices = vy_sort(graph.keys(), ctx=ctx)
    return [
        [graph_distance(graph, elem1, elem2) for elem2 in vertices]
        for elem1 in vertices
    ]


def divide(lhs, rhs, ctx):
    """Element /
    (num, num) -> a / b
    (num, str) -> b split into a even length pieces, possibly with an extra part
    (str, num) -> a split into b even length pieces, possibly with an extra part
    (str, str) -> split a on b
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: 0
        if rhs == 0
        else vyxalify(sympy.nsimplify(lhs / rhs, rational=True)),
        (NUMBER_TYPE, str): lambda: chop(rhs, lhs),
        (str, NUMBER_TYPE): lambda: chop(lhs, rhs),
        (str, str): lambda: lhs.split(rhs),
    }.get(ts, lambda: vectorise(divide, lhs, rhs, ctx=ctx))()


def divide_lists(lhs, rhs, ctx):
    """Element Þ÷
    (any, num) -> a split into b even length pieces, possibly with an extra part
    (num, any) -> b split into a even length pieces, possibly with an extra part
    """
    ts = vy_type(lhs, rhs, simple=True)
    if ts[1] == list:
        return divide_lists(rhs, lhs, ctx)
    return chop(lhs, rhs)


def divisor_sum(lhs, ctx):
    """Element ∆K
    (num) -> sum of proper divisors of a
    (str) -> stationary points of a
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: vy_sum(divisors_or_prefixes(lhs, ctx)[:-1], ctx),
        str: lambda: stationary_points(lhs),
    }.get(ts, lambda: vectorise(divisor_sum, lhs, ctx=ctx))()


def divisors_or_prefixes(lhs, ctx):
    """Element K
    (num) -> divisors(a) # Factors or divisors of a
    (str) -> all substrings of a that occur more than once
    (lst) -> prefixes(a) # Prefixes of a
    """
    ts = vy_type(lhs)
    if ts == NUMBER_TYPE:
        return multiply(
            vy_abs(sympy.divisors(lhs), ctx), sign_of(lhs, ctx), ctx
        )
    elif ts == str:
        return uniquify(
            LazyList(
                filter(
                    lambda substr: lhs.count(substr) > 1,
                    substrings(lhs, ctx),
                )
            ),
            ctx,
        )
    else:
        return prefixes(lhs, ctx=ctx)


def dot_product(lhs, rhs, ctx):
    """Element Þ•
    Return the dot product of lhs and rhs
    """
    return vy_sum(multiply(lhs, rhs, ctx), ctx)


def dyadic_maximum(lhs, rhs, ctx):
    """Element ∴
    (any, any) -> max(a, b)
    """
    ts = vy_type(lhs, rhs)
    if types.FunctionType in ts:
        return max_by_function(lhs, rhs, ctx)

    return lhs if strict_greater_than(lhs, rhs, ctx) else rhs


def dyadic_minimum(lhs, rhs, ctx):
    """Element ∵
    (any, any) -> min(a, b)
    """
    ts = vy_type(lhs, rhs)
    if types.FunctionType in ts:
        return min_by_function(lhs, rhs, ctx)
    return lhs if strict_less_than(lhs, rhs, ctx) else rhs


def dyadic_runl_decode(lhs, rhs, ctx: Context):
    """Element øḊ
    (any, any) -> run length decode a with lengths b
    """
    return run_length_decoding(vy_zip(lhs, rhs, ctx=ctx), ctx=ctx)


def e_digits(lhs, ctx):
    """Element ∆Ė
    (int) -> e_digits(a)
    (str) -> evaluate as sympy
    """
    if vy_type(lhs) == NUMBER_TYPE:
        estr = str(sympy.N(sympy.E, int(lhs) + 2))
        estr = estr[0] + estr[2:-1]
        return LazyList(map(int, estr))
    elif vy_type(lhs) is str:
        return lhs
        # return sympy.nsimplify(lhs, rational=True)
    else:
        return vectorise(e_digits, lhs, ctx=ctx)


def element_wise_dyadic_maximum(lhs, rhs, ctx):
    """Element Þ∴
    (lst, lst) -> max(a, b)
    """
    ts = vy_type(lhs, rhs, simple=True)
    if list in ts:
        # In theory this gets TCO'd lol
        return vectorise(element_wise_dyadic_maximum, lhs, rhs, ctx=ctx)
    return max(lhs, rhs)


def element_wise_dyadic_minimum(lhs, rhs, ctx):
    """Element Þ∵
    (lst, lst) -> min(a, b)
    """
    ts = vy_type(lhs, rhs, simple=True)
    if list in ts:
        return vectorise(element_wise_dyadic_minimum, lhs, rhs, ctx=ctx)
    return min(lhs, rhs)


def ends_with(lhs, rhs, ctx):
    """Element øE
    (str, str) -> True if a ends with b
    """
    ts = primitive_type(lhs), primitive_type(rhs)
    return int(
        {
            (list, SCALAR_TYPE): lambda: lhs[-1] == rhs,
            (SCALAR_TYPE, list): lambda: rhs[-1] == lhs,
            (list, list): lambda: lhs[-1] == rhs,
        }.get(ts, lambda: vy_str(lhs).endswith(vy_str(rhs)))()
    )


def ends_with_set(lhs, rhs, ctx):
    """Element øf
    (list, list) -> True if a ends with all of b
    """
    ts = primitive_type(lhs), primitive_type(rhs)
    return int(
        {
            (list, list): lambda: lhs[-len(rhs) :] == rhs if len(rhs) else 1,
            (list, SCALAR_TYPE): lambda: lhs[-1] == rhs,
            (SCALAR_TYPE, list): lambda: rhs[-1] == lhs,
        }.get(ts, lambda: vy_str(lhs).endswith(vy_str(rhs)))()
    )


def equals(lhs, rhs, ctx):
    """Element =
    (num, num) -> lhs == rhs
    (num, str) -> str(lhs) == rhs
    (str, num) -> lhs == str(rhs)
    (str, str) -> lhs == rhs
    """
    ts = vy_type(lhs, rhs)
    if ts == (NUMBER_TYPE, NUMBER_TYPE):
        abs_lhs = abs(lhs)
        abs_rhs = abs(rhs)
        abs_diff = abs(simplify(lhs - rhs))
        if lhs == rhs:
            return 1  # Takes care of the easy stuff
        elif abs_diff > 1:
            return 0
        elif (
            lhs == 0
            or rhs == 0
            or abs_diff < sys.float_info.min
            or sympy.im(lhs)
            or sympy.im(rhs)
        ):
            return int(bool(abs_diff < EPSILON))
        else:
            return int(bool(abs_diff / (abs_lhs + abs_rhs) < EPSILON))

    return {
        (NUMBER_TYPE, str): lambda: int(str(lhs) == rhs),
        (str, NUMBER_TYPE): lambda: int(lhs == str(rhs)),
        (str, str): lambda: int(lhs == rhs),
    }.get(ts, lambda: vectorise(equals, lhs, rhs, ctx=ctx))()


def euclidean_distance(lhs, rhs, ctx):
    """Element ∆d
    (num, num) -> distance between a and b
    """
    return square_root(
        vy_sum(exponent(subtract(lhs, rhs, ctx), 2, ctx), ctx), ctx
    )


def evenly_distribute(lhs, rhs, ctx):
    """Element Þ…
    (list, num) -> Evenly distribute a over all elements of b,
                   adding each part
    """
    lhs = iterable(lhs, ctx=ctx)
    if not lhs:
        return lhs

    each = rhs // len(lhs)
    extra = rhs - each * len(lhs)

    if isinstance(lhs, list):
        return [
            lhs[i] + each + 1 if i < extra else lhs[i] + each
            for i in range(len(lhs))
        ]

    @lazylist
    def gen():
        i = 0
        for elem in lhs:
            if i < extra:
                yield elem + each + 1
            else:
                yield elem + each
            i += 1

    return gen()


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
        NUMBER_TYPE: lambda: 2**lhs,
        str: lambda: vy_eval(lhs, ctx),
    }.get(ts, lambda: vectorise(exp2_or_eval, lhs, ctx=ctx))()


def expe(lhs, ctx):
    """Element ∆e
    (num) -> e ** a
    (str) -> simplify expression a
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.exp(lhs),
        str: lambda: str(sympy.simplify(make_expression(lhs))),
    }.get(ts, lambda: vectorise(expe, lhs, ctx=ctx))()


def expe_minus_1(lhs, ctx):
    """Element ∆E
    (num) -> (e ** a) - 1
    (str) -> expand expression a
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.exp(lhs) - 1,
        str: lambda: str(sympy.expand(make_expression(lhs))),
    }.get(ts, lambda: vectorise(expe_minus_1, lhs, ctx=ctx))()


def exponent(lhs, rhs, ctx):
    """Element e
    (num, num) -> a ** b (exponentiation)
    (num, str) -> append b[0] to b until b is length a (spaces if b is empty)
    (str, num) -> append a[0] to a until a is length b (spaces if a is empty)
    (str, str) -> regex.search(pattern=a, string=b).span() (Length of regex match)
    (any, fun) -> map b to a, but don't lazily evaluate the result
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: lhs**rhs,
        (NUMBER_TYPE, str): lambda: rhs
        + ((rhs[0] or " ") * (int(lhs) - len(rhs))),
        (str, NUMBER_TYPE): lambda: lhs
        + ((lhs[0] or " ") * (int(rhs) - len(lhs))),
        (str, str): lambda: []
        if (mobj := re.search(rhs, lhs)) is None
        else list(mobj.span()),
        (ts[0], types.FunctionType): lambda: list(vy_map(rhs, lhs, ctx)),
        (types.FunctionType, ts[1]): lambda: list(vy_map(lhs, rhs, ctx)),
    }.get(ts, lambda: vectorise(exponent, lhs, rhs, ctx=ctx))()


def factorial(lhs, ctx):
    """Element ¡
    (num) -> factorial(a) (math.gamma(a + 1))
    (str) -> a.sentence_case()
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: vyxalify(sympy.factorial(abs(lhs))),
        # Because otherwise, it returns a very unhelpful factorial obj
        str: lambda: sentence_case(lhs),
    }.get(ts, lambda: vectorise(factorial, lhs, ctx=ctx))()


def factorial_of_range(lhs, ctx):  # WHY does this still exist lmao
    """Element øF
    (num, num) -> factorial of range
    (num, str) -> vectorised
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: math.factorial(lhs),
        str: lambda: vectorise(factorial_of_range, lhs, ctx=ctx),
    }.get(ts, lambda: vectorise(factorial_of_range, lhs, ctx=ctx))()


def fibonaacis(_, ctx):
    """Element ÞF
    An infinite lazylist of fibonaaci numbers
    """

    def gen():
        i = 0
        while True:
            yield sympy.fibonacci(i + 1)
            i += 1

    return LazyList(gen(), isinf=True)


def factorials(_, ctx):
    """Element Þ!
    An infinite lazylist of factorials
    """

    def gen():
        i = 0
        while True:
            yield factorial(i, ctx)
            i += 1

    return LazyList(gen(), isinf=True)


def fill(lhs, rhs, ctx: Context):
    """Element ÞḞ
    (any, any) -> fill a with b to make a rectangular
    """
    ts = vy_type(lhs, rhs, simple=True)
    if ts[1] == list and ts[0] != list:
        return fill(rhs, lhs, ctx)
    return transpose(transpose(lhs, filler=rhs, ctx=ctx))


def find(lhs, rhs, ctx):
    """Element ḟ
    (any, any) -> a.find(b)
    (any, fun) -> truthy indices of mapping b over a
    """
    ts = vy_type(lhs, rhs)
    if types.FunctionType not in ts:
        lhs, rhs = (
            (rhs, lhs)
            if primitive_type(rhs) != SCALAR_TYPE
            and primitive_type(lhs) == SCALAR_TYPE
            else (lhs, rhs)
        )

        if list not in ts and LazyList not in ts:
            lhs, rhs = str(lhs), str(rhs)
            needle, haystack = (
                sorted((lhs, rhs), key=len)
                if len(lhs) != len(rhs)
                else (rhs, lhs)
            )
            return haystack.find(needle)
        pos = 0
        lhs = iterable(lhs, ctx=ctx)
        if vy_type(lhs) is LazyList and lhs.infinite:
            while strict_less_than(
                lhs[pos], rhs, ctx
            ) or non_vectorising_equals(lhs[pos], rhs, ctx):
                if non_vectorising_equals(index(lhs, pos, ctx), rhs, ctx):
                    return pos
                pos += 1
            return -1
        lhs = LazyList(lhs)
        while lhs.has_ind(pos):
            if non_vectorising_equals(index(lhs, pos, ctx), rhs, ctx):
                return pos
            pos += 1
        return -1
    else:
        if ts[0] == types.FunctionType:
            return find(rhs, lhs, ctx)

        @lazylist
        def f(it, fun):
            idx = 0
            for x in it:
                if safe_apply(fun, x, ctx=ctx):
                    yield idx
                idx += 1

        return f(lhs, rhs)


def first_integer(lhs, ctx):
    """Element ṅ
    (num) -> abs(a) <= 1
    (str) -> pad with 0s to nearest multiple of 8
    (lst) -> "".join(a)
    (fun) -> first integer x where a(x) is truthy
    """
    if isinstance(lhs, types.FunctionType):
        value = 1

        while not safe_apply(lhs, value, ctx=ctx):
            value += 1

        return value

    ts = vy_type(lhs, simple=True)
    return {
        (NUMBER_TYPE): lambda: int(bool(abs(lhs) <= 1)),
        (str): lambda: lhs.zfill((len(lhs) + ((8 - len(lhs)) % 8)) or 8),
        (list): lambda: join(lhs, "", ctx)
        if all(vy_type(x, simple=True) is not list for x in lhs)
        else vectorise(first_integer, lhs, ctx=ctx),
    }.get(ts, lambda: vectorise(first_integer, lhs, ctx=ctx))()


def flatten_by(lhs, rhs, ctx):
    """Element Þf
    (lst, num) -> Flatten a by depth b
    (any, lst) -> Flatten b by depth 1, push a as well
    """
    if rhs < 0:
        raise ValueError(f"Cannot flatten by depth {rhs}")
    if int(rhs) == 0:
        return lhs
    elif vy_type(lhs, simple=True) is list:

        @lazylist_from(lhs)
        def gen():
            for item in lhs:
                if vy_type(item, simple=True) is list:
                    yield from flatten_by(item, int(rhs - 1), ctx)
                else:
                    yield item

        return gen()
    else:
        return [lhs]


def flip_brackets_vertical_mirror(lhs, ctx):
    """Element øṀ
    (str) -> vertical_mirror(a, mapping = flip brackets and slashes)
    """
    if vy_type(lhs, simple=True) in (str, NUMBER_TYPE):
        result = str(lhs).split("\n")
        for i in range(len(result)):
            result[i] += invert_brackets(result[i])[::-1]
        return "\n".join(result)
    else:
        return vectorise(flip_brackets_vertical_mirror, lhs, ctx=ctx)()


def flip_brackets_vertical_palindromise(lhs, ctx):
    """Element øM
    (str) -> lhs vertically palindromised without duplicating the center, with brackets flipped.
    """
    ts = vy_type(lhs)
    if ts == NUMBER_TYPE:
        return lhs
    elif ts in (list, LazyList):
        return vectorise(flip_brackets_vertical_palindromise, lhs, ctx=ctx)
    result = lhs.split("\n")
    for i in range(len(result)):
        result[i] += invert_brackets(result[i][:-1][::-1])
    return "\n".join(result)


def foldl_columns(lhs, rhs, ctx):
    """Element ÞC
    (lst, fun) -> reduce the columns of a by function b
    """
    lhs, rhs = (lhs, rhs) if vy_type(lhs, simple=True) is list else (rhs, lhs)
    lhs = transpose(iterable(lhs, ctx=ctx), ctx=ctx)
    return [foldl(rhs, col, ctx=ctx) for col in lhs]


def from_base(lhs, rhs, ctx):
    """Element β
    Convert lhs from base rhs to base 10
    """
    ts = vy_type(lhs, rhs, simple=True)
    if (ts[0] == NUMBER_TYPE and ts[1] != NUMBER_TYPE) or (
        ts[1] == list and ts[0] != list
    ):
        return from_base(rhs, lhs, ctx)
    if ts == (str, str):
        return from_base_alphabet(lhs, rhs)
    elif ts[-1] == NUMBER_TYPE:
        if ts[0] == str:
            return from_base(
                lhs, (string.digits + string.ascii_lowercase)[:rhs], ctx
            )
        return from_base_digits(iterable(lhs, ctx=ctx), rhs)
    else:
        raise ValueError("from_base: invalid types")


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
        args = wrapify(lhs, top.arity, ctx=ctx) if top.arity != -1 else [lhs]
        arity_override = None if top.arity != -1 else top.arity
        lhs += wrapify(
            safe_apply(top, *args, ctx=ctx, arity_override=arity_override),
            ctx=ctx,
        )
        return None
    return {
        NUMBER_TYPE: lambda: len(prime_factorisation(top, ctx)),
        str: lambda: exec(top) or [] if not ctx.online else [],
        list: lambda: vectorised_not(top, ctx=ctx),
    }.get(ts)()


def gen_from_fn(lhs, rhs, ctx):
    """Element Ḟ
    (num, num) -> sympy.N(a, b) (evaluate a to b decimal places)
    (num, str) -> every ath letter of b
    (str, num) -> every bth letter of a
    (str, str) -> replace spaces in a with b
    (lst, num) -> every bth item of a
    (num, lst) -> every ath item of b
    (fun, any) -> Generator from function a with initial vector b
    """
    ts = vy_type(lhs, rhs, simple=True)
    if types.FunctionType not in ts:
        return {
            (NUMBER_TYPE, NUMBER_TYPE): lambda: str(sympy.N(lhs, rhs)),
            (NUMBER_TYPE, str): lambda: rhs[::lhs],
            (str, NUMBER_TYPE): lambda: lhs[::rhs],
            (str, str): lambda: lhs.replace(" ", rhs),
            (list, NUMBER_TYPE): lambda: index(lhs, [None, None, rhs], ctx),
            (NUMBER_TYPE, list): lambda: index(rhs, [None, None, lhs], ctx),
        }.get(ts, lambda: vectorise(gen_from_fn, lhs, rhs, ctx=ctx))()

    lhs, rhs = (rhs, lhs) if ts[0] is types.FunctionType else (lhs, rhs)
    lhs = iterable(lhs, ctx=ctx)

    def gen():
        yield from lhs

        if isinstance(lhs, LazyList):
            made = lhs
        else:
            made = lhs[:]

        func_arity = (
            rhs.stored_arity if "stored_arity" in dir(rhs) else rhs.arity
        )
        while True:
            ret = safe_apply(rhs, *made[-func_arity:], ctx=ctx)
            if isinstance(made, LazyList):
                made = made.appended(ret)
            else:
                made.append(ret)
            yield ret

    return LazyList(gen(), isinf=True)


def general_quadratic_solver(lhs, rhs, ctx):
    """Element ∆Q
    (num, num) -> roots(a, b) # x^2 + ax + b = 0
    (num, str) -> evaluate single variable expression b with x=a
    (str, num) -> evaluate single variable expression a with x=b
    (str, str) -> solve a and b simulatenously
    """
    ts = vy_type(lhs, rhs)
    x, y = sympy.symbols("x y")
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: sympy.solve(
            sympy.Eq(x**2 + lhs * x + rhs, 0), x
        ),
        (NUMBER_TYPE, str): lambda: make_expression(rhs).subs(x, lhs),
        (str, NUMBER_TYPE): lambda: make_expression(lhs).subs(x, rhs),
        (str, str): lambda: dict_to_list(
            sympy.solve([make_equation(lhs), make_equation(rhs)], (x, y))
        ),
    }.get(ts, lambda: vectorise(general_quadratic_solver, lhs, rhs, ctx=ctx))()


def grade_down(lhs, ctx):
    """Element ⇩
    (lst) -> graded_down(a)
    (str) -> a.lower()
    (num) -> a - 2
    """
    ts = vy_type(lhs)
    return {(NUMBER_TYPE): lambda: lhs - 2, (str): lambda: lhs.lower()}.get(
        ts,
        lambda: LazyList(
            map(
                lambda x: x[0],
                sorted(
                    enumerate(deep_copy(lhs)), key=lambda x: x[-1], reverse=True
                ),
            ),
        ),
    )()


def grade_up(lhs, ctx):
    """Element ⇧
    (lst) -> graded_up(a)
    (str) -> a.upper()
    (num) -> a + 2
    """
    ts = vy_type(lhs)
    return {(NUMBER_TYPE): lambda: lhs + 2, (str): lambda: lhs.upper()}.get(
        ts,
        lambda: LazyList(
            map(
                lambda x: x[0],
                sorted(enumerate(deep_copy(lhs)), key=lambda x: x[-1]),
            ),
        ),
    )()


def greater_than(lhs, rhs, ctx):
    """Element >
    (num, num) -> a > b
    (num, str) -> str(a) > b
    (str, num) -> a > str(b)
    (str, str) -> a > b
    (any, fun) -> increment a until b returns false
    (fun, any) -> increment b until a returns false
    """
    ts = vy_type(lhs, rhs)
    if types.FunctionType in ts:
        return increment_until_false(lhs, rhs, ctx=ctx)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: int(bool(lhs > rhs)),
        (NUMBER_TYPE, str): lambda: int(str(lhs) > rhs),
        (str, NUMBER_TYPE): lambda: int(lhs > str(rhs)),
        (str, str): lambda: int(lhs > rhs),
    }.get(ts, lambda: vectorise(greater_than, lhs, rhs, ctx=ctx))()


def greater_than_or_equal(lhs, rhs, ctx):
    """Element ≥
    (num, num) -> a ≥ b
    (num, str) -> str(a) ≥ b
    (str, num) -> a ≥ str(b)
    (str, str) -> a ≥ b
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: int(bool(lhs >= rhs)),
        (NUMBER_TYPE, str): lambda: int(str(lhs) >= rhs),
        (str, NUMBER_TYPE): lambda: int(lhs >= str(rhs)),
        (str, str): lambda: int(lhs >= rhs),
    }.get(ts, lambda: vectorise(greater_than_or_equal, lhs, rhs, ctx=ctx))()


def group_consecutive(lhs, ctx):
    """Element Ġ
    (lst) -> Group consecutive identical items
    (str) -> Group consecutive identical characters
    (num) -> Group consecutive identical digits"""
    typ = vy_type(lhs)

    if typ == NUMBER_TYPE:
        lhs = digits(lhs)

    if not lhs:
        return lhs

    def gen():
        prev = lhs[0]
        no_found = 1

        for item in lhs[1:]:
            if not non_vectorising_equals(prev, item, ctx):
                yield [prev] * no_found
                prev = item
                no_found = 1
            else:
                no_found += 1
        yield [prev] * no_found

    if typ is LazyList:
        return LazyList(gen(), isinf=lhs.infinite)

    res = list(gen())

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
    if word:
        result.append(word)
    return result


def halve(lhs, ctx):
    """Element ½
    (num) -> lhs / 2
    (str) -> a split into two strings of equal lengths (as close as possible)
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.nsimplify(lhs / 2, rational=True),
        str: lambda: wrap(lhs, math.ceil(len(lhs) / 2), ctx=ctx)
        if len(lhs) > 1
        else [lhs, ""],
    }.get(ts, lambda: vectorise(halve, lhs, ctx=ctx))()


def head(lhs, ctx):
    """Element h
    (any) -> a[0]
    """
    return next(
        iter(lhs) if type(lhs) is str else iter(iterable(lhs, ctx=ctx)),
        "" if type(lhs) is str else 0,
    )


def head_remove(lhs, ctx):
    """Element Ḣ
    (lst) -> a[1:] or [] if empty
    (str) -> a[1:] or '' if empty
    (num) -> range(2, a + 1)"""
    if vy_type(lhs, simple=True) in (list, str):
        return lhs[1:] if lhs else []
    if lhs < 2:
        return []

    return iterable(lhs, range, ctx=ctx)[1:]


def identity_matrix(lhs, ctx):
    """Element Þ□
    (num) -> A matrix with 1s on the main diagonal and zeroes elsewhere
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: [
            [1 if i == j else 0 for j in range(lhs)] for i in range(lhs)
        ],
        str: lambda: lhs,
    }.get(ts, lambda: vectorise(identity_matrix, lhs, ctx=ctx))()


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


def increment_until_false(lhs, rhs, ctx):
    """Element ∆‹
    (any, fun) -> while b(a): a += 1
    (fun, any) -> while a(b): b += 1
    """
    function, value = (
        (rhs, lhs) if type(rhs) is types.FunctionType else (lhs, rhs)
    )
    while safe_apply(function, value, ctx=ctx):
        value = increment(value, ctx)
    return value


def index(lhs, rhs, ctx):
    """Element i
    (any, num) -> a[b] (Index)
    (num, any) -> b[a] (Index)
    (str, str) -> enclose b in a # b[0:len(b)//2] + a + b[len(b)//2:]
    (any, [x]) -> a[:b] (0 to bth item of a)
    (any, [x,y]) -> a[x:y] (x to yth item of a)
    (any, [x,y,m]) -> a[x:y:m] (x to yth item of a, taking every mth)
    """
    ts = vy_type(lhs, rhs)
    lhs = deep_copy(lhs)
    if ts == (str, str):
        # b[0:len(b)//2] + a + b[len(b)//2:]
        return lhs[: len(rhs) // 2] + rhs + lhs[len(rhs) // 2 :]

    elif ts == (LazyList, NUMBER_TYPE):
        return lhs[int(rhs)]

    elif ts[1] == NUMBER_TYPE:
        lhs = iterable(lhs, ctx=ctx)
        if lhs:
            return lhs[int(rhs) % len(lhs)]
        else:
            return "" if ts[0] is str else 0

    elif ts[0] == NUMBER_TYPE:
        return index(rhs, lhs, ctx)

    elif ts[1] == str:
        return vectorise(index, lhs, rhs, ctx=ctx)

    else:
        assert len(rhs) <= 3
        originally_string = type(lhs) is str
        if originally_string:
            lhs = LazyList(list(lhs))

        temp = lhs[
            slice(*[None if vy_type(v) != NUMBER_TYPE else int(v) for v in rhs])
        ]

        return "".join(temp) if originally_string else temp


def index_indices_or_cycle(lhs, rhs, ctx):
    """Element İ
    (any, lst) -> [a[item] for item in b]
    (any, fun) -> apply b on a and collect unique values"""
    if types.FunctionType in [type(lhs), type(rhs)]:
        # swap lhs and rhs such that rhs contains the function
        lhs, rhs = (rhs, lhs) if type(lhs) is types.FunctionType else (lhs, rhs)
        prevs = []

        @lazylist
        def gen():
            curr = lhs
            first = True
            while True:
                curr = deep_copy(safe_apply(rhs, curr, ctx=ctx))
                if curr == lhs and not first:
                    break
                if curr in prevs:
                    yield from prevs
                    break
                first = False

                prevs.append(curr)

        return gen()

    else:
        if vy_type(lhs, rhs) == (NUMBER_TYPE, NUMBER_TYPE):
            return lhs + rhs * sympy.I
        if primitive_type(rhs) is SCALAR_TYPE:
            lhs, rhs = rhs, lhs
        lhs = LazyList(iterable(lhs))
        rhs = iterable(rhs)

        # Don't ask me why vectorise doesn't work here
        # I tried.
        def recursive_helper(value):
            if primitive_type(value) is SCALAR_TYPE:
                return lhs[vy_int(value, ctx=ctx)]
            else:
                return LazyList(recursive_helper(v) for v in value)

        return recursive_helper(rhs)


def index_partition(lhs, rhs, ctx):
    """Element ÞṖ
    (lst, lst) -> Parititon lhs before indices of rhs
    """
    return partition_at_indices(rhs, lhs)


@infinite_lazylist
def infinite_all_integers(_, ctx=None):
    """Element Þn
    An infinite list of all integers (0, 1, -1, 2, -2, ...)
    """
    yield 0
    i = 1
    while 1:
        yield i
        yield -i
        i += 1


def infinite_cardinals(_, ctx=None):
    """Element Þc
    infinite sequence of cardinals
    """
    return LazyList(map(num2words.num2words, itertools.count(1)), isinf=True)


def infinite_integer_partitions(_, ctx=None):
    """Element ÞṄ"""

    def gen():
        for n in itertools.count(1):
            yield from integer_parts_or_join_spaces(n, ctx=ctx)

    return LazyList(gen(), isinf=True)


def infinite_ordinals(_, ctx=None):
    """Element Þo
    infinite list of place numbers starting at a - first, second,
    third, fourth, fifth, etc.

    This function returns an infinite generator that yields the word
    form of each ordinal number starting at first.
    """

    def gen():
        i = 1
        while True:
            yield num2words.num2words(i, to="ordinal")
            i += 1

    return LazyList(gen(), isinf=True)


def infinite_positives(_, ctx=None):
    """Element Þ∞
    An infinite list of positive numbers
    """

    @infinite_lazylist
    def gen():
        i = 1
        while True:
            yield i
            i += 1

    return gen()


def infinite_primes(_, ctx=None):
    """Element Þp
    An infinite list of primes
    """

    def gen():
        i = 1
        while True:
            i += 1
            if i == 2 or (i % 2 == 1 and is_prime(i, ctx)):
                yield i

    return LazyList(gen(), isinf=True)


def infinite_replace(lhs, rhs, other, ctx):
    """Element ¢
    (any, any, any) -> replace b in a with c until a doesn't change
    (lst, fun, lst) -> apply function b to items in a at indices in c
    (lst, lst, fun) -> apply function c to items in a at indices in b
    (fun, lst, lst) -> apply function a to items in b at indices in c
    """
    ts = (vy_type(lhs), vy_type(rhs), vy_type(other))
    if types.FunctionType in ts:
        function = first_where(
            lambda val: isinstance(val, types.FunctionType),
            (lhs, rhs, other),
            ctx,
        )

        vector, indicies = filter(
            lambda val: not isinstance(val, types.FunctionType),
            (lhs, rhs, other),
        )
        values = safe_apply(
            function, index_indices_or_cycle(vector, indicies, ctx), ctx=ctx
        )
        new_vector = []
        for i, v in enumerate(vector):
            if i in indicies:
                new_vector.append(values[i])
            else:
                new_vector.append(v)
        return new_vector

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

    If `ind` is greater than or equal to the LazyList's length, `other` is appended to the end.
    """
    is_number = vy_type(lhs) == NUMBER_TYPE
    lhs = iterable(lhs, ctx)

    if vy_type(other) != types.FunctionType:
        if vy_type(lhs) is str:
            if vy_type(other) == NUMBER_TYPE and vy_type(rhs) != NUMBER_TYPE:
                rhs, other = other, rhs
            return lhs[: int(rhs)] + str(other) + lhs[int(rhs) :]

        @lazylist_from(lhs)
        def gen():
            i = 0
            places = chr_ord(rhs) if isinstance(rhs, str) else int(rhs)
            position_is_number = vy_type(places) == NUMBER_TYPE
            if position_is_number and places < 0:
                if places == -1:
                    yield from lhs
                    yield other
                else:
                    temp = list(lhs)
                    temp.insert(places + 1, other)
                    yield from temp
                return
            for elem in lhs:
                if position_is_number:
                    if i == places:
                        yield other
                elif i in places:
                    yield other
                yield elem
                i += 1
            if position_is_number and i < places:
                yield other

        if is_number:
            return vy_eval("".join(map(str, gen())), ctx=ctx)
        return gen()

    @lazylist_from(lhs)
    def gen():
        i = 0
        assert vy_type(rhs) == NUMBER_TYPE
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
        (NUMBER_TYPE, NUMBER_TYPE): lambda: 0 if rhs == 0 else lhs // rhs,
        (NUMBER_TYPE, str): lambda: divide(lhs, rhs, ctx=ctx)[0],
        (str, NUMBER_TYPE): lambda: divide(rhs, lhs, ctx=ctx)[0],
        (ts[0], types.FunctionType): lambda: foldl(
            rhs, reverse(iterable(lhs, ctx=ctx), ctx=ctx), ctx=ctx
        ),
        (types.FunctionType, ts[1]): lambda: foldl(
            lhs, reverse(iterable(rhs, ctx=ctx), ctx=ctx), ctx=ctx
        ),
    }.get(ts, lambda: vectorise(integer_divide, lhs, rhs, ctx=ctx))()


def integer_parts_or_join_spaces(lhs, ctx):
    """Element Ṅ
    (num) -> Integer partitions of a. [] if 0, all negative if n < 0
    (any) -> Join on spaces
    """
    if vy_type(lhs) == NUMBER_TYPE:
        if lhs == 0:
            return []
        sign = -1 if lhs < 0 else 1

        @lazylist
        def helper(n, minimum):
            for i in range(minimum, n // 2 + 1):
                for part in helper(n - i, i):
                    yield part + [i * sign]
            yield [n * sign]

        return helper(abs(lhs), 1)

    return join(lhs, " ", ctx)


def interleave(lhs, rhs, ctx):
    """Element Y
    (any, any) -> interleave a and b
    """
    # Essentially, Zf but whatever

    lhs = iterable(lhs, ctx=ctx)
    rhs = iterable(rhs, ctx=ctx)

    def gen():
        lhs_iter = iter(lhs)
        rhs_iter = iter(rhs)
        while True:
            try:
                yield next(lhs_iter)
            except StopIteration:
                yield from rhs_iter
                break
            try:
                yield next(rhs_iter)
            except StopIteration:
                yield from lhs_iter
                break

    if type(lhs) is type(rhs) is str:
        return "".join(gen())
    else:
        return LazyList(
            gen(),
            isinf=(
                (type(lhs) is LazyList and lhs.infinite)
                or (type(rhs) is LazyList and rhs.infinite)
            ),
        )


def into_two(lhs, ctx):
    """Element I
    (num) -> push a spaces
    (str) -> equivlaent to `qp`
    (lst) -> split a list into two halves
    """
    ts = vy_type(lhs, simple=True)
    return {
        NUMBER_TYPE: lambda: " " * int(lhs),
        str: lambda: quotify(lhs, ctx) + lhs,
        list: lambda: [
            index(lhs, [None, int(len(lhs) / 2) + len(lhs) % 2], ctx),
            index(lhs, [int(len(lhs) / 2) + len(lhs) % 2, None], ctx),
        ],
    }.get(ts)()


def is_divisible(lhs, rhs, ctx):
    """Element Ḋ
    (num, num) -> a % b == 0
    (num, str) -> a copies of b
    (str, num) -> b copies of a
    (str, str) -> b + " " + a ($ẋ)

    Beware, this function returns a singleton list for its first and
    fourth overloads and a list of copies of the top of the stack
    otherwise, not a single value!
    """
    ts = vy_type(lhs, rhs)

    def helper(lhs, rhs):
        ts = vy_type(lhs, rhs)
        return {
            (NUMBER_TYPE, NUMBER_TYPE): lambda: int(lhs % rhs == 0)
            if rhs != 0
            else 0,
            (NUMBER_TYPE, str): lambda: [rhs] * lhs,
            (str, NUMBER_TYPE): lambda: [lhs] * rhs,
            (str, str): lambda: rhs + " " + lhs,
            (types.FunctionType, ts[1]): lambda: group_by_function_ordered(
                iterable(rhs, ctx=ctx), lhs, ctx=ctx
            ),
            (ts[0], types.FunctionType): lambda: group_by_function_ordered(
                iterable(lhs, ctx=ctx), rhs, ctx=ctx
            ),
        }.get(ts, lambda: vectorise(helper, lhs, rhs, ctx=ctx))()

    return {
        (NUMBER_TYPE, str): lambda: [rhs] * lhs,
        (str, NUMBER_TYPE): lambda: [lhs] * rhs,
    }.get(ts, lambda: [helper(lhs, rhs)])()


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


def is_divisible_by_three(lhs, ctx):
    """Element ₃
    (num) -> a % 3 == 0
    (str) -> len(a) == 1
    """
    if vy_type(lhs) == NUMBER_TYPE:
        return int(lhs % 3 == 0)
    else:
        return int(len(lhs) == 1)


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
    return vectorised_not(equals(lhs, 1, ctx=ctx), ctx=ctx)


def is_ordered(lhs, ctx):
    """Element ÞȮ
    (lst) -> Returns true if the item is sorted in either descending
             or ascending order.
    """
    return is_sorted_ascending(lhs, ctx) or is_sorted_descending(lhs, ctx)


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
    }.get(ts, lambda: vectorise(is_prime, lhs, ctx=ctx))()


def is_sorted_ascending(lhs, ctx):
    """Element ÞṠ
    (lst) -> Returns true if an item is sorted in ascending order
             using default sorting rules.
    """
    return non_vectorising_equals(lhs, vy_sort(lhs, ctx), ctx=ctx)


def is_sorted_descending(lhs, ctx):
    """Element ÞṠ
    (lst) -> Returns true if an item is sorted in ascending order
             using default sorting rules.
    """
    return non_vectorising_equals(
        reverse(deep_copy(lhs), ctx), vy_sort(lhs, ctx), ctx=ctx
    )


def is_sorted_strictly_ascending(lhs, ctx):
    """Element Þ⇧
    (lst) -> Returns true if an item is sorted in strictly
             ascending order using default sorting rules.
    """
    return int(
        all(
            strict_less_than(x[0], x[1], ctx)
            for x in overlapping_groups(iterable(lhs, ctx=ctx), 2, ctx)
        )
    )


def is_sorted_strictly_descending(lhs, ctx):
    """Element Þ⇩
    (lst) -> Returns true if an item is sorted in strictly
             descending order using default sorting rules.
    """
    return int(
        all(
            strict_greater_than(x[0], x[1], ctx)
            for x in overlapping_groups(iterable(lhs, ctx=ctx), 2, ctx)
        )
    )


def is_square(lhs, ctx):
    """Element ∆²
    (num) -> is square number?
    (str) -> square the expression
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: int(
            int(lhs) == lhs and sympy.ntheory.primetest.is_square(lhs)
        ),
        str: lambda: str(sympy.expand(make_expression(lhs + " ** 2"))),
    }.get(ts, lambda: vectorise(is_square, lhs, ctx=ctx))()


def is_unordered(lhs, ctx):
    """Element ÞĊ
    (lst) -> Returns true if the item is not sorted in either
            descending or ascending order. (i.e. chaos chaos)
    """
    return int(not is_ordered(lhs, ctx))


def join(lhs, rhs, ctx):
    """Element j
    (any, any) -> a.join(b)
    """
    if (vy_type(lhs) is not LazyList or not lhs.infinite) and (
        vy_type(lhs, simple=True) is not list
        or vy_type(rhs) is str
        or any(vy_type(x) is str for x in lhs)
    ):
        return vy_str(rhs, ctx=ctx).join(
            map(lambda a: vy_str(a, ctx=ctx), iterable(lhs, ctx=ctx))
        )

    @lazylist_from(lhs)
    def gen():
        ind = 0
        for i in lhs:
            if vy_type(i, simple=True) is list:
                yield from i
            else:
                yield i
            if (vy_type(lhs) is LazyList and lhs.has_ind(ind + 1)) or (
                vy_type(lhs) is list and ind < len(lhs) - 1
            ):
                if vy_type(rhs, simple=True) is list:
                    yield from rhs
                else:
                    yield rhs
            ind += 1

    return gen()


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


def json_parse(lhs, ctx):
    """Element øJ
    (str) -> json.loads(a)
    """
    return vyxalify(json.loads(lhs))


def left_bit_shift(lhs, rhs, ctx):
    """Element ↲
    (num, num) -> a << b
    (num, str) -> a.ljust(b)
    (str, num) -> b.ljust(a)
    (str, str) -> a.ljust(len(b)-len(a))
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: int(lhs) << int(rhs),
        (NUMBER_TYPE, str): lambda: rhs.ljust(lhs),
        (str, NUMBER_TYPE): lambda: lhs.ljust(rhs),
        (str, str): lambda: lhs.ljust(len(rhs)),
    }.get(ts, lambda: vectorise(left_bit_shift, lhs, rhs, ctx=ctx))()


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
    (any, fun) -> decrement a until b returns false
    (fun, any) -> decrement b until a returns false
    """
    ts = vy_type(lhs, rhs)
    if types.FunctionType in ts:
        return decrement_until_false(lhs, rhs, ctx=ctx)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: int(bool(lhs < rhs)),
        (NUMBER_TYPE, str): lambda: int(str(lhs) < rhs),
        (str, NUMBER_TYPE): lambda: int(lhs < str(rhs)),
        (str, str): lambda: int(lhs < rhs),
    }.get(ts, lambda: vectorise(less_than, lhs, rhs, ctx=ctx))()


def less_than_or_equal(lhs, rhs, ctx):
    """Element ≤
    (num, num) -> a ≤ b
    (num, str) -> str(a) ≤ b
    (str, num) -> a ≤ str(b)
    (str, str) -> a ≤ b
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: int(bool(lhs <= rhs)),
        (NUMBER_TYPE, str): lambda: int(str(lhs) <= rhs),
        (str, NUMBER_TYPE): lambda: int(lhs <= str(rhs)),
        (str, str): lambda: int(lhs <= rhs),
    }.get(ts, lambda: vectorise(less_than_or_equal, lhs, rhs, ctx=ctx))()


def letter_to_number(lhs, ctx):
    """Element øA
    (num) -> number_to_letter(a)
    (str) -> letter_to_number(a)
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: chr(lhs + 96),
        str: lambda: (
            ord(lhs) - 96 if lhs > "Z" else ord(lhs) - 64
        )  # No, I'm not making this less cursed
        if len(lhs) == 1
        else LazyList(
            ord(char) - 96 if char > "Z" else ord(char) - 64 for char in lhs
        )
        if len(lhs)
        else [],
    }.get(ts, lambda: vectorise(letter_to_number, lhs, ctx=ctx))()


def lift(lhs, ctx):
    """Element Þż
    (any) -> a * 1...a.length
    """

    @lazylist_from(lhs)
    def gen():
        i = 1
        for item in lhs:
            yield item * i
            i += 1

    return gen()


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
        (NUMBER_TYPE, NUMBER_TYPE, NUMBER_TYPE): lambda: int(
            simplify(lhs) <= simplify(other) <= simplify(rhs)
        ),
        (NUMBER_TYPE, NUMBER_TYPE, str): lambda: "\n".join([other * lhs] * rhs),
        (NUMBER_TYPE, str, NUMBER_TYPE): lambda: "\n".join([rhs * lhs] * other),
        (NUMBER_TYPE, str, str): lambda: vy_str(rhs, ctx=ctx).ljust(lhs, other),
        (str, NUMBER_TYPE, NUMBER_TYPE): lambda: "\n".join([lhs * rhs] * other),
        (str, NUMBER_TYPE, str): lambda: vy_str(lhs, ctx=ctx).ljust(rhs, other),
        (str, str, NUMBER_TYPE): lambda: vy_str(lhs, ctx=ctx).ljust(other, rhs),
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
    }.get(ts, lambda: vectorise(ljust, lhs, rhs, other, ctx=ctx))()


def log_10(lhs, ctx):
    """Element ∆τ
    (num) -> log10(a)
    (str) -> log10(a)
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: sympy.log(lhs, 10),
        (str): lambda: str(sympy.log(make_expression(lhs), 10)),
    }.get(ts, lambda: vectorise(log_10, lhs, ctx=ctx))()


def log_2(lhs, ctx):
    """Element ∆l
    (num) -> log2(a)
    (str) -> log2(a)
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: sympy.log(lhs, 2),
        (str): lambda: str(sympy.log(make_expression(lhs), 2)),
    }.get(ts, lambda: vectorise(log_2, lhs, ctx=ctx))()


def log_mold_multi(lhs, rhs, ctx):
    """Element •
    (num, num) -> log_lhs(rhs)
    (num, str) -> [char * lhs for char in rhs]
    (str, num) -> [char * rhs for char in lhs]
    (str, str) -> lhs.with_capitalisation_of(rhs)
    (lst, lst) -> lhs molded to the shape of rhs
    """
    ts = vy_type(lhs, rhs, simple=True)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: sympy.nsimplify(
            math.log(lhs, rhs), rational=True
        ),
        (NUMBER_TYPE, str): lambda: "".join([char * lhs for char in rhs]),
        (str, NUMBER_TYPE): lambda: "".join([char * rhs for char in lhs]),
        (str, str): lambda: transfer_capitalisation(rhs, lhs),
        (list, list): lambda: mold(lhs, rhs),
    }.get(ts, lambda: vectorise(log_mold_multi, lhs, rhs, ctx=ctx))()


def longest(lhs, ctx):
    """Element ÞG
    (lst) -> Return the longest item in a list
    """
    return max_by_function(lhs, length, ctx)


def lowest_common_multiple(lhs, rhs=None, ctx=None):
    """Element ∆Ŀ
    (num, num) -> lcm(a, b)
    """
    if rhs is None:
        return sympy.lcm(lhs)
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: sympy.nsimplify(
            sympy.lcm(lhs, rhs), rational=True
        ),
        (NUMBER_TYPE, str): lambda: -1,
        (str, NUMBER_TYPE): lambda: -1,
        (str, str): lambda: -1,
    }.get(ts, lambda: vectorise(lowest_common_multiple, lhs, rhs, ctx=ctx))()


def matrix_determinant(lhs, ctx):
    """Element ÞḊ
    (mat) -> determinant(a)
    """
    if lhs and (len(lhs) > 1 or len(lhs[0])):
        lhs = pad_to_square(iterable(lhs, ctx=ctx))
    return sympy.det(sympy.Matrix(lhs))


def matrix_exponentiation(lhs, rhs, ctx):
    """Element Þe
    (lst, num) -> (a * a) b times
    (num, lst) -> (b * b) a times
    """
    ts = vy_type(lhs, rhs, simple=True)
    if set(ts) != {list, NUMBER_TYPE}:
        raise TypeError("Matrix exponentiation requires a matrix and a number")

    matrix, times = (rhs, lhs) if ts[0] == NUMBER_TYPE else (lhs, rhs)
    original_matrix = matrix
    for _ in range(times - 1):
        matrix = matrix_multiply(original_matrix, matrix, ctx=ctx)

    return matrix


def matrix_multiply(lhs, rhs, ctx):
    """Element ÞṀ
    (lst, lst) -> Matrix multiplication
    """
    rhs = transpose(rhs, ctx)

    return LazyList(
        [dot_product(row, column, ctx) for column in rhs] for row in lhs
    )


def max_by_function(lhs, rhs, ctx):
    """Element Þ↑
    (lst, fun) -> Maximum value of a by applying b to each element
    """
    lhs, rhs = (lhs, rhs) if isinstance(rhs, types.FunctionType) else (rhs, lhs)
    lhs = iterable(lhs, ctx=ctx)
    if len(lhs) == 0:
        return []
    elif len(lhs) == 1:
        return lhs[0]
    else:
        biggest, biggest_fn = lhs[0], safe_apply(rhs, lhs[0], ctx=ctx)
        for item in lhs[1:]:
            if strict_greater_than(
                safe_apply(rhs, item, ctx=ctx), biggest_fn, ctx
            ):
                biggest, biggest_fn = item, safe_apply(rhs, item, ctx=ctx)
        return biggest


def max_by_tail(lhs, ctx):
    """Element ↑
    (any) -> max(a, key=lambda x: x[-1])
    """
    lhs = iterable(lhs, ctx=ctx)
    if len(lhs) == 0:
        return []
    else:
        return max_by(lhs, key=tail, cmp=greater_than, ctx=ctx)


def maximal_indices(lhs, ctx):
    """Element ÞM
    Return the indexes of maximal objects in lhs
    """

    @lazylist
    def gen():
        biggest = monadic_maximum(lhs, ctx=ctx)
        for i, item in enumerate(list(lhs)):
            if non_vectorising_equals(item, biggest, ctx=ctx):
                yield i

    return gen()


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


def median(lhs, ctx):
    """Element ∆ṁ
    Return the median of a list - the middle item(s)
    """
    lhs = iterable(vy_sort(lhs, ctx), ctx=ctx)
    if len(lhs) % 2 == 0:
        return [lhs[len(lhs) // 2 - 1], lhs[len(lhs) // 2]]
    return lhs[len(lhs) // 2]


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
        (NUMBER_TYPE, str): lambda: add(lhs, rhs, ctx),
        (str, NUMBER_TYPE): lambda: add(lhs, rhs, ctx),
        (str, str): lambda: lhs + rhs,
        (list, ts[1]): lambda: concat(lhs, [rhs], ctx),
        (ts[0], list): lambda: concat([lhs], rhs, ctx),
        (list, list): lambda: concat(lhs, rhs, ctx),
    }.get(ts)()


def min_by_function(lhs, rhs, ctx):
    """Element Þ↓
    (lst, fun) -> Minimum value of a by applying b to each element
    """
    lhs, rhs = (lhs, rhs) if isinstance(rhs, types.FunctionType) else (rhs, lhs)
    lhs = iterable(lhs, ctx=ctx)
    if len(lhs) == 0:
        return []
    elif len(lhs) == 1:
        return lhs[0]
    else:
        smallest, smallest_fn = lhs[0], safe_apply(rhs, lhs[0], ctx=ctx)
        for item in lhs[1:]:
            if strict_less_than(
                safe_apply(rhs, item, ctx=ctx), smallest_fn, ctx
            ):
                smallest, smallest_fn = item, safe_apply(rhs, item, ctx=ctx)
        return smallest


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


def mod_pow(lhs, rhs, other, ctx: Context):
    """Element ∆%
    (any, any, any) -> a ** b mod c
    """
    ts = vy_type(lhs, rhs, other, simple=True)
    if list in ts:
        return vectorise(mod_pow, lhs, rhs, other, ctx=ctx)
    return sympy.nsimplify(pow(int(lhs), int(rhs), int(other)), rational=True)


def mode(lhs, ctx):
    """Element ∆M
    Most common item in a list.
    Equivalent to Ċ↑h
    """
    item_counts = collections.Counter(iterable(lhs, ctx=ctx))
    return item_counts.most_common(1)[0][0]


def modulo(lhs, rhs, ctx):
    """Element %
    (num, num) -> a % b
    (num, str) -> (b split into a equal pieces)[-1]
    (str, num) -> (a split into b equal pieces)[-1]
    (str, str) -> a.format(b)
    """
    ts = vy_type(lhs, rhs, simple=True)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: lhs % rhs if rhs != 0 else 0,
        (NUMBER_TYPE, str): lambda: format_string(rhs, [lhs]),
        (str, NUMBER_TYPE): lambda: format_string(lhs, [rhs]),
        (str, str): lambda: format_string(lhs, [rhs]),
        (str, list): lambda: format_string(lhs, rhs),
    }.get(ts, lambda: vectorise(modulo, lhs, rhs, ctx=ctx))()


def modulo_3(lhs, ctx):
    """Element ǒ
    (num) -> a % 3
    (str) -> a split into chunks of size 2
    """
    return {
        (NUMBER_TYPE): lambda: lhs % 3,
        (str): lambda: [lhs[i : i + 2] for i in range(0, len(lhs), 2)],
    }.get(vy_type(lhs), lambda: vectorise(modulo_3, lhs, ctx=ctx))()


def mold_special(lhs, rhs, ctx):
    """Element Þṁ
    (lst, lst) -> mold, but don't reuse items"""
    lhs, rhs = iterable(lhs, ctx=ctx), iterable(rhs, ctx=ctx)
    return mold_without_repeat(lhs, rhs)


def monadic_maximum(lhs, ctx):
    """Element G
    (any) -> Maximal element of the input
    """
    if len(lhs) == 0:
        return []
    else:
        return max_by(lhs, cmp=strict_greater_than, ctx=ctx)


def monadic_minimum(lhs, ctx):
    """Element g
    (any) -> Smallest item of a
    """
    if len(lhs) == 0:
        return []
    else:
        return min_by(lhs, cmp=strict_less_than, ctx=ctx)


def multi_dimensional_index(lhs, rhs, ctx):
    """Element Þi
    (lst, lst) -> a[b[0]][b[1]][b[2]]... Reduce by indexing with
                  a as initial value
    """
    for item in iterable(rhs, ctx=ctx):
        lhs = index(lhs, item, ctx)

    return lhs


def multi_dimensional_search(lhs, rhs, ctx):
    """Element Þḟ
    (lst, any) -> Find the first occurrence of a in b and return as a
                  multidimensional index
    """
    lhs = iterable(lhs, ctx=ctx)

    for ind, item in enumerate_md(lhs):
        if non_vectorising_equals(item, rhs, ctx):
            return ind

    return []


def multidimensional_truthy_indices(lhs, ctx: Context):
    """Element ÞT
    (any) -> multi-dimensional truthy indices
    """

    @lazylist_from(lhs)
    def f(a, i=[]):
        if vy_type(a, simple=True) != list:
            if a:
                yield i
        else:
            for j, x in enumerate(a):
                yield from f(x, i + [j])

    return f(lhs)


def multiplicity(lhs, rhs, ctx):
    """Element Ǒ
    (num, num) -> number of times a divides b
    (str, str) -> Remove a from b until b does not change
    (any, fun) -> Index of the first truthy item in a under b(x)
    """
    ts = vy_type(lhs, rhs)
    if ts == (NUMBER_TYPE, NUMBER_TYPE):
        if lhs == 0 or rhs == 0:
            return 0
        if abs(rhs) == 1:
            return abs(lhs)
        times = 0
        while lhs % rhs == 0:
            lhs /= rhs
            times += 1
        return times
    elif ts == (str, str):
        return remove_until_no_change(lhs, rhs, ctx)
    elif types.FunctionType in ts:
        return find(lhs, rhs, ctx)[0]
    else:
        return vectorise(multiplicity, lhs, rhs, ctx=ctx)


def multiply(lhs, rhs, ctx):
    """Element *
    (num, num) -> a * b
    (num, str) -> repeat b a times
    (str, num) -> repeat a b times
    (str, str) -> ring translate b according to a
    """
    ts = vy_type(lhs, rhs)

    if ts[0] is types.FunctionType:
        if ts[1] in (list, LazyList):
            return vectorise(multiply, lhs, rhs, ctx=ctx)
        lhs.stored_arity = rhs
        return lhs

    elif ts[1] is types.FunctionType:
        if ts[0] in (list, LazyList):
            return vectorise(multiply, lhs, rhs, ctx=ctx)
        rhs.stored_arity = lhs
        return rhs
    else:
        return {
            (NUMBER_TYPE, NUMBER_TYPE): lambda: lhs * rhs,
            (NUMBER_TYPE, str): lambda: lhs * rhs,
            (str, NUMBER_TYPE): lambda: lhs * rhs,
            (str, str): lambda: ring_translate(lhs, rhs),
        }.get(ts, lambda: vectorise(multiply, lhs, rhs, ctx=ctx))()


def multiset_difference(lhs, rhs, ctx):
    """Element Þ∨
    (lst, lst) -> Return the mutli-set difference of two lists
    """
    original_type = vy_type(lhs)
    lhs = iterable(lhs, ctx=ctx)
    if type(lhs) is str:
        lhs = list(lhs)
    lhs_copy = deep_copy(lhs)
    rhs = iterable(rhs, ctx=ctx)

    for item in rhs:
        if contains(lhs_copy, item, ctx):
            lhs_copy = lhs_copy.remove(item)

    if original_type is str:
        return "".join(lhs_copy)
    elif original_type == NUMBER_TYPE:
        return vy_eval("".join(map(str, lhs_copy)), ctx)
    return lhs_copy


def multiset_intersection(lhs, rhs, ctx):
    """Element Þ∩
    (lst, lst) -> Return the multi-set intersection of two lists
    """
    lhs = iterable(lhs, ctx=ctx)
    rhs = deep_copy(iterable(rhs, ctx=ctx))

    original_type = vy_type(lhs)
    lhs = iterable(lhs, ctx=ctx)
    if type(lhs) is str:
        lhs = list(lhs)

    @lazylist_from(lhs)
    def gen():
        nonlocal rhs
        rhs = deep_copy(iterable(rhs, ctx=ctx))
        if type(rhs) is str:
            rhs = list(rhs)

        for item in lhs:
            if contains(rhs, item, ctx):
                yield item
                if vy_type(rhs) == LazyList:
                    rhs = rhs.remove(item)
                else:
                    rhs.remove(item)

    if original_type is str:
        return "".join(map(str, gen()))
    elif original_type == NUMBER_TYPE:
        return vy_eval("".join(map(str, gen())), ctx=ctx)
    return gen()


def multiset_symmetric_difference(lhs, rhs, ctx):
    """Element Þ⊍
    (lst, lst) -> Return the multi-set symmetric difference of two lists
    """
    return multiset_union(
        multiset_difference(lhs, rhs, ctx),
        multiset_difference(rhs, lhs, ctx),
        ctx,
    )


def multiset_union(lhs, rhs, ctx):
    """Element Þ∪
    (lst, lst) -> Return the multi-set union of two lists
    """
    return LazyList(iterable(lhs, ctx=ctx)) + LazyList(
        iterable(multiset_difference(rhs, lhs, ctx), ctx=ctx)
    )


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
        (types.FunctionType, ts[1]): lambda: drop_while(rhs, lhs, ctx),
        (ts[0], types.FunctionType): lambda: drop_while(lhs, rhs, ctx),
    }.get(ts, lambda: vectorise(n_choose_r, lhs, rhs, ctx=ctx))()


def n_pick_r(lhs, rhs, ctx):
    """Element ∆ƈ
    (num, num) -> n_pick_r(a, b)
    (num, str) -> n_pick_r(a, len(b))
    (str, num) -> n_pick_r(len(a), b)
    (str, str) -> n_pick_r(len(a), len(b))
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: divide(
            factorial(lhs, ctx), factorial(lhs - rhs, ctx), ctx
        ),
        (NUMBER_TYPE, str): lambda: n_pick_r(lhs, len(rhs), ctx),
        (str, NUMBER_TYPE): lambda: n_pick_r(len(lhs), rhs, ctx),
        (str, str): lambda: n_pick_r(len(lhs), len(rhs), ctx),
    }.get(ts, lambda: vectorise(n_pick_r, lhs, rhs, ctx=ctx))()


def natural_log(lhs, ctx):
    """Element ∆L
    (num) -> ln(a)
    (str) -> inverse of expression a
    """
    x, y = sympy.symbols("x, y")
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: sympy.ln(lhs),
        (str): lambda: map(
            lambda ex: str(ex.subs(y, x)),
            wrapify(sympy.solve(y - make_expression(lhs), x)),
        ),
    }.get(ts, lambda: vectorise(natural_log, lhs, ctx=ctx))()


def nearest_prime(lhs, ctx):
    """Element ∆p
    (num) -> nearest prime to a
    (str) -> python code from expression
    """
    ts = vy_type(lhs)
    if ts == NUMBER_TYPE:
        if lhs < 2:
            return 2
        elif is_prime(lhs, ctx):
            return lhs

    return {
        (NUMBER_TYPE): lambda: min(
            next_prime(lhs, ctx),
            prev_prime(lhs, ctx),
            key=lambda x: abs(x - lhs),
        ),
        (str): lambda: sympy.pycode(make_expression(lhs)),
    }.get(ts, lambda: vectorise(nearest_prime, lhs, ctx=ctx))()


def negate(lhs, ctx):
    """Element N
    (num) -> -a
    (str) -> swapcase of a
    (fun) -> first integer where function is truthy
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: -lhs,
        (str): lambda: lhs.swapcase(),
        (types.FunctionType): lambda: LazyList(
            vy_filter(lhs, infinite_all_integers(None, ctx), ctx)
        )[0],
    }.get(ts, lambda: vectorise(negate, lhs, ctx=ctx))()


def newline_split(lhs, ctx):
    """Element ↵
    (num) -> 10 ** a
    (str) -> a.split("\\n")
    """
    return {
        (NUMBER_TYPE): lambda: 10**lhs,
        (str): lambda: lhs.split("\n"),
    }.get(vy_type(lhs), lambda: vectorise(newline_split, lhs, ctx=ctx))()


def next_multiple(lhs, rhs, ctx):
    """Element ∆*
    (num, num) -> get the next multiple of b that is greater than a
    """

    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: lhs + rhs - lhs % rhs,
    }.get(ts, lambda: vectorise(next_multiple, lhs, rhs, ctx=ctx))()


def next_power(lhs, rhs, ctx):
    if list in vy_type(lhs, rhs, simple=True):
        return vectorise(next_power, lhs, rhs, ctx=ctx)()
    return rhs ** sympy.floor(sympy.log(lhs, rhs) + 1)


def next_prime(lhs, ctx):
    """Element ∆Ṗ
    (num) -> next prime after a
    (str) -> discrimiant of a
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: sympy.nextprime(lhs),
        (str): lambda: sympy.discriminant(make_expression(lhs)),
    }.get(ts, lambda: vectorise(next_prime, lhs, ctx=ctx))()


def non_vectorising_equals(lhs, rhs, ctx):
    """Element ⁼
    (num, num) -> a == b
    (str, str) -> a == b
    (lst, lst) -> a == b
    """
    ts = vy_type(lhs, rhs, simple=True)
    return int(
        {
            (NUMBER_TYPE, NUMBER_TYPE): lambda: lhs == rhs,
            (NUMBER_TYPE, str): lambda: str(lhs) == rhs,
            (str, NUMBER_TYPE): lambda: lhs == str(rhs),
            (str, str): lambda: lhs == rhs,
            (list, list): lambda: lhs == rhs,
        }.get(ts, lambda: 0)()
    )


def not_equals(lhs, rhs, ctx):
    """Element ≠
    (num, num) -> a != b
    (str, str) -> a != b
    (lst, lst) -> a != b
    """
    ts = vy_type(lhs, rhs, simple=True)
    return int(
        {
            (NUMBER_TYPE, str): lambda: str(lhs) != rhs,
            (str, NUMBER_TYPE): lambda: lhs != str(rhs),
        }.get(ts, lambda: lhs != rhs)()
    )


def nth_cardinal(lhs, ctx):
    """Element ∆ċ
    Given a number, return that number as a cardinal - minus one, zero,
    one, two, three etc
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: num2words.num2words(
            lhs, lang="en", to="cardinal"
        ),
        (str): lambda: lhs,
    }.get(ts, lambda: vectorise(nth_cardinal, lhs, ctx=ctx))()


def nth_e(lhs, ctx):
    """Element ∆ė
    (int) -> nth_e(a)
    (str) -> derivative of a
    """
    if type(lhs) is str:
        x = sympy.symbols("x")
        return str(sympy.diff(make_expression(lhs), x))
    elif vy_type(lhs) == NUMBER_TYPE:
        if lhs == 0:
            return 2
        elif lhs == 1:
            return 7
        else:
            return int(str(sympy.N(sympy.E, int(lhs) + 2))[lhs + 1])
    else:
        return vectorise(nth_e, lhs, ctx=ctx)


def nth_fibonacci(lhs, ctx):
    """Element ∆f
    (num) -> nth fibonacci number # sympy.fibonacci(lhs + 1)
    (str) -> nop
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: sympy.fibonacci(lhs + 1),
        (str): lambda: lhs,
    }.get(ts, lambda: vectorise(nth_fibonacci, lhs, ctx=ctx))()


def nth_fibonacci_0(lhs, ctx):
    """Element ∆F
    (num) -> nth fibonacci number, 0 indexed
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: sympy.fibonacci(lhs),
        (str): lambda: lhs,
    }.get(ts, lambda: vectorise(nth_fibonacci_0, lhs, ctx=ctx))()


def nth_ordinal(lhs, ctx):
    """Element ∆o
    Nth item of Þo
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: num2words.num2words(
            lhs, lang="en", to="ordinal"
        ),
        (str): lambda: lhs,
    }.get(ts, lambda: vectorise(nth_ordinal, lhs, ctx=ctx))()


def nth_pi(lhs, ctx):
    """Element ∆i
    (int) -> nth_pi(a)
    (str) -> indefinte integral of a
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: pi_digits(int(lhs))[int(lhs)],
        (str): lambda: str(lhs)
        # (str): lambda: str(sympy.integrate(make_expression(lhs))),
    }.get(ts, lambda: vectorise(nth_pi, lhs, ctx=ctx))()


def one_length_range(lhs, ctx):
    """Element ż
    (any) -> range(1, len(a) + 1) (Inclusive range from 1 to length of a)
    """

    @lazylist_from(lhs)
    def gen():
        count = sympy.nsimplify(1)
        for item in iterable(lhs, ctx=ctx):
            yield count
            count += 1

    return gen()


def one_slice(lhs, rhs, ctx):
    """Element Ż
    (any, num) -> a[1:b] (Slice from 1 until b)
    (num, any) -> b[1:a] (Slice from 1 until a)
    (str, str) -> re.match(pattern=a,string=b)
    """
    ts = vy_type(lhs, rhs)
    if types.FunctionType in ts:
        if ts[0] == types.FunctionType:
            lhs, rhs = rhs, lhs
        lhs = iterable(lhs, ctx=ctx)

        @lazylist_from(lhs)
        def gen():
            last_state = None
            group = []
            for item in lhs:
                state = bool(safe_apply(rhs, item, ctx=ctx))
                if state != last_state and last_state is not None:
                    if group:
                        yield group
                    group = []
                if state:
                    group.append(item)
                last_state = state
            if group:
                yield group

        return gen()
    return {
        (ts[0], NUMBER_TYPE): lambda: index(
            iterable(lhs, ctx=ctx), [1, rhs], ctx
        ),
        (NUMBER_TYPE, ts[1]): lambda: index(
            iterable(rhs, ctx=ctx), [1, lhs], ctx
        ),
        (NUMBER_TYPE, NUMBER_TYPE): lambda: vy_floor(
            index(str(lhs), [1, rhs], ctx=ctx), ctx
        ),
        (str, str): lambda: vyxalify(
            (a := re.match(rhs, lhs)) and a.groups() or 0
        ),
    }.get(ts, lambda: vectorise(one_slice, lhs, rhs, ctx=ctx))()


def optimal_compress(lhs, ctx):
    """Element øD
    (str) -> return the most optimal dictionary compressed string
    """
    dp = [""] + [" " * (len(lhs) + 1)] * len(lhs)

    for i in range(1, len(lhs) + 1):
        for left in range(max(0, i - dictionary.max_word_len), i - 1):
            j = dictionary.word_index(lhs[left:i])
            if j != -1:
                dp[i] = min([dp[i], dp[left] + j], key=len)
                break

        sub = lhs[:i]
        for index, word in enumerate(dictionary.small_dictionary):
            if sub.endswith(word):
                j = compression[index]
                dp[i] = min([dp[i], dp[i - len(word)] + j], key=len)

        replace = dp[i - 1] + lhs[i - 1]
        if len(replace) < len(dp[i]):
            dp[i] = replace

    return quotify(dp[i], ctx)


def optimal_number_compress(lhs, ctx):
    """Element øċ
    (num) -> Semi-optimally compress a number
    """
    if lhs < 0:
        return optimal_number_compress(-lhs, ctx) + "N"
    num_dict = {
        10: "₀",
        26: "₄",
        64: "₆",
        100: "₁",
        128: "₇",
        256: "₈",
        512: "k¶",
        1024: "k⁋",
        2048: "k¦",
        4096: "kṄ",
        8192: "kṅ",
        16384: "k¡",
        32768: "kε",
        65536: "k₴",
        2**20: "k₂",
        2**30: "k₃",
        2**32: "kḭ",
        1: "1",
        2: "2",
        3: "3",
        4: "4",
        5: "5",
        6: "6",
        7: "7",
        8: "8",
        9: "9",
        360: "kR",
    }
    if lhs in num_dict:
        return num_dict.get(lhs)
    if lhs < 100 or 356 < lhs < 1000:
        return str(lhs)
    funs = [
        (lambda x: x + 1, "›"),
        (lambda x: x - 1, "‹"),
        (lambda x: x * 2, "d"),
        (lambda x: x / 2, "½"),
        (lambda x: x**2, "²"),
        (lambda x: x * 3, "T"),
        (lambda x: 2**x, "E"),
        (lambda x: 10**x, "↵"),
        (lambda x: x**0.5, "√"),
        (lambda x: x + 2, "⇧"),
        (lambda x: x - 2, "⇩"),
    ]
    # Brute force functions applied to constants
    for (fun, name) in funs:
        for key in num_dict:
            # safeguard to avoid calculating huge numbers
            if (name not in "E↵" or key < 100) and fun(key) == lhs:
                return num_dict.get(key) + name
    if lhs <= 356:
        return "⁺" + codepage[lhs - 101]
    # Brute force functions applied to constants twice
    for (fun, name) in funs:
        for (fun2, name2) in funs:
            for key in num_dict:
                # safeguard to avoid calculating huge numbers
                if (
                    (name not in "E↵" or key < 100)
                    and (name not in "E↵" or name2 not in "E↵")
                    and (key < 100 or name2 not in "E↵")
                    and fun2(fun(key)) == lhs
                ):
                    return num_dict.get(key) + name + name2
    return "»" + to_base(lhs, codepage_number_compress, ctx) + "»"


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
            range(int(lhs), int(rhs), (-1, 1)[int(bool(lhs < rhs))])
            # int(bool(...)) is needed because sympy decides to
            # return a special boolean class sometimes
        ),
        (NUMBER_TYPE, str): lambda: rhs + (" " * abs(len(rhs) - lhs))
        if len(rhs) < lhs
        else rhs,
        (str, NUMBER_TYPE): lambda: (" " * abs(len(lhs) - rhs)) + lhs
        if len(lhs) < rhs
        else lhs,
        (ts[0], types.FunctionType): lambda: scanl(
            multiply(rhs, 2, ctx), iterable(lhs, range, ctx=ctx), ctx=ctx
        ),
        (types.FunctionType, ts[1]): lambda: scanl(
            multiply(lhs, 2, ctx), iterable(rhs, range, ctx=ctx), ctx=ctx
        ),
        (str, str): lambda: int(bool(re.compile(rhs).search(lhs))),
    }.get(ts, lambda: vectorise(orderless_range, lhs, rhs, ctx=ctx))()


def overlapping_groups(lhs, rhs, ctx):
    """Element l
    (any, num) -> Overlapping groups/windows of a of length b
    (any, any) -> length(a) == length(b)
    """
    if NUMBER_TYPE not in vy_type(lhs, rhs):
        return int(len(iterable(lhs, ctx=ctx)) == len(rhs))

    if vy_type(lhs) == NUMBER_TYPE:
        lhs, rhs = rhs, lhs

    stringify = vy_type(lhs) is str

    @lazylist_from(lhs)
    def gen():
        window = "" if stringify else []
        for item in iterable(lhs, ctx=ctx):
            if stringify:
                window += item
            else:
                window.append(item)
            if len(window) == rhs:
                yield window
                window = window[1:]

    return gen()


def overloaded_canvas_draw(lhs, rhs, other, ctx):
    ts = vy_type(lhs, rhs, other, simple=True)

    def is_valid_dirs(lst):
        return all([item in range(9) or item in "+x[]^v<>" for item in lst])

    def is_valid_lengths(lst):
        return all([isinstance(item, int) and item > 0 for item in lst])

    def make_dirs(lst):
        return [
            int(item) if (type(item) is str and item.isnumeric()) else item
            for item in lst
        ]

    if NUMBER_TYPE in ts:
        if ts[0] == NUMBER_TYPE:
            length = lhs
            rest = (rhs, other)
        elif ts[1] == NUMBER_TYPE:
            length = rhs
            rest = (lhs, other)
        else:
            length = other
            rest = (lhs, rhs)

        ts2 = vy_type(rest[0], rest[1])

        if ts2[0] == str:
            text, dirs = rest
        else:
            dirs, text = rest

        dirs = make_dirs(iterable(dirs, ctx=ctx))

        return (dirs, [length], text)
    else:
        if ts[0] == str:
            text = lhs
            rest = (rhs, other)
        elif ts[1] == str:
            text = rhs
            rest = (lhs, other)
        else:
            text = other
            rest = (lhs, rhs)

        # At this point, both rest values are lists or strings
        if is_valid_dirs(make_dirs(rest[0])):
            dirs, length = rest
        else:
            length, dirs = rest

        dirs = make_dirs(dirs)

        if vy_type(length) == str:
            length = [int(length)]

        return (dirs, length, text)


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
    if vy_type(lhs, simple=True) is list:
        return vectorise(parenthesise, lhs)
    return "(" + str(lhs) + ")"


def parity(lhs, ctx):
    """Element ∷
    (num) -> parity of a
    (str) -> parity of a
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: lhs % 2,
        (str): lambda: halve(lhs, ctx)[-1],
    }.get(ts, lambda: vectorise(parity, lhs, ctx=ctx))()


def parse_direction_arrow_to_integer(lhs, ctx):
    """Element ¨^
    (str) -> map characters in `>^<v` to integers
    """
    ts = vy_type(lhs)
    if ts is str and len(lhs) == 1:
        return {
            ">": 0,
            "^": 1,
            "<": 2,
            "v": 3,
        }.get(lhs, -1)
    else:
        return vectorise(parse_direction_arrow_to_integer, list(lhs), ctx=ctx)()


def parse_direction_arrow_to_vector(lhs, ctx):
    """Element ¨^
    (str) -> map characters in `>^<v` to direction vectors
    """
    ts = vy_type(lhs)
    if ts is str and len(lhs) == 1:
        return {
            ">": [sympy.nsimplify(+1), sympy.nsimplify(0)],
            "^": [sympy.nsimplify(0), sympy.nsimplify(+1)],
            "<": [sympy.nsimplify(-1), sympy.nsimplify(0)],
            "v": [sympy.nsimplify(0), sympy.nsimplify(-1)],
        }.get(lhs, [sympy.nsimplify(0), sympy.nsimplify(0)])
    else:
        return vectorise(parse_direction_arrow_to_vector, list(lhs), ctx=ctx)()


def permutations(lhs, ctx):
    """Element Ṗ
    (any) -> Permutations of a
    """
    lhs = iterable(lhs, ctx=ctx)
    return LazyList(
        map(
            lambda x: "".join(x) if all(isinstance(y, str) for y in x) else x,
            itertools.permutations(
                iterable(lhs, number_type=range, ctx=ctx), len(lhs)
            ),
        )
    )


def pluralise_count(lhs, rhs, ctx):
    """Element øP
    (str, num) -> count lhs lots of rhs
    (num, str) -> count rhs lots of lhs
    """
    if vy_type(lhs) == NUMBER_TYPE:
        return pluralise_count(rhs, int(lhs), ctx)
    return str(rhs) + " " + str(lhs) + "s" * (rhs != 1)


def polynomial_expr_from_coeffs(lhs, ctx):
    """Element ∆Ċ
    (num) -> symbolic math representation of polynomial of degree n
             where each coefficient is 1
    (str) -> a
    (lst) -> symbolic math representation of polynomial with coeffs in
             lhs
    """
    ts = vy_type(lhs)
    x = sympy.symbols("x")
    return {
        NUMBER_TYPE: lambda: str(sum(x**arg for arg in range(0, lhs + 1))),
        str: lambda: lhs,
        list: lambda: str(
            sum(c * x**i for i, c in enumerate(reverse(lhs, ctx)))
        ),
    }.get(ts, lambda: vectorise(polynomial_expr_from_coeffs, lhs, ctx=ctx))()


def polynomial_from_roots(lhs, ctx):
    """Element ∆ṙ
    (lst) -> Get the polynomial with coefficients from the roots of a polynomial
    """
    assert all(vy_type(x) == NUMBER_TYPE for x in lhs)
    eqn = " * ".join(map(lambda x: "(x - " + str(x) + ")", lhs))
    x = sympy.symbols("x")
    return sympy.Poly(eqn, x).coeffs()


def polynomial_roots(lhs, ctx):
    """Element ∆P
    (lst) -> roots(a)
    """
    x = sympy.symbols("x")

    equation = make_expression(
        " + ".join(
            map(
                lambda power: "("
                + str(power[1])
                + ")x^("
                + str(power[0])
                + ")",
                enumerate(reverse(iterable(lhs, ctx=ctx), ctx=ctx)),
            )
        )
    )

    return vyxalify(sympy.solve(sympy.Eq(equation, 0), x))


def powerset(lhs, ctx):
    """Element ṗ
    (any) -> powerset of a
    """
    lhs = iterable(lhs, ctx=ctx)

    @lazylist_from(lhs)
    def gen():
        it = iter(lhs)

        prev_sets = [[]]
        yield []
        while True:
            try:
                elem = next(it)
            except StopIteration:
                break
            new_sets = [prev + [elem] for prev in prev_sets]
            prev_sets += [subset[:] for subset in new_sets]
            yield from new_sets

    if vy_type(lhs) == str:
        return LazyList("".join(x) for x in gen())
    return gen()


def prepend(lhs, rhs, ctx):
    """Element p
    (any, any) -> a.prepend(b) (Prepend b to a)
    """
    ts = vy_type(lhs, rhs, simple=True)
    if ts != (list, list):
        return merge(rhs, lhs, ctx)
    else:
        return LazyList([rhs]) + LazyList(lhs)


def prev_power(lhs, rhs, ctx):
    if list in vy_type(lhs, rhs, simple=True):
        return vectorise(prev_power, lhs, rhs, ctx=ctx)()
    return rhs ** sympy.ceiling(sympy.log(lhs, rhs) - 1)


def prev_prime(lhs, ctx):
    """Element ∆ṗ
    (num) -> previous prime
    (str) -> factorise expression
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.prevprime(int(lhs)) if lhs >= 3 else 1,
        str: lambda: str(sympy.factor(make_expression(lhs))),
    }.get(ts, lambda: vectorise(prev_prime, lhs, ctx=ctx))()


def prime_exponents(lhs, ctx):
    """Element ∆ǐ
    (num) -> prime exponents of a
    (str) -> factorise expression
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: [
            value for key, value in sympy.factorint(int(lhs)).items()
        ],
    }.get(ts, lambda: vectorise(prime_exponents, lhs, ctx=ctx))()


def prime_factorisation(lhs, ctx):
    """Element Ǐ
    (num) -> prime_factors(a) (no duplicates)
    (str) -> lhs + lhs[0]"""
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.ntheory.primefactors(int(lhs)),
        str: lambda: lhs + lhs[0],
    }.get(ts, lambda: lhs + [lhs[0]] if lhs else lhs)()


def prime_factors(lhs, ctx):
    """Element ǐ
    (num) -> prime_factors(a) (with duplicates)
    (str) -> title_case(a)
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: deep_flatten(
            [
                [key] * value
                for key, value in sympy.ntheory.factorint(int(lhs)).items()
            ],
            ctx=ctx,
        ),
        (str): lambda: lhs.title(),
    }.get(ts, lambda: vectorise(prime_factors, lhs, ctx=ctx))()


def product(lhs, ctx):
    """Element Π
    (lst[num]) -> product(list)
    (lst[str|lst]) -> Cartesian product over a list of lists
    """
    if all(vy_type(x) == NUMBER_TYPE for x in lhs):
        return foldl(multiply, lhs, initial=1, ctx=ctx)

    return cartesian_over_list(lhs, ctx)


def quadratic_solver(lhs, rhs, ctx):
    """Element ∆q
    (num, num) -> x such that ax^2 + bx = 0
    (num, str) -> evaluate single variable equation b with x=a
    (str, num) -> evaluate single variable equation a with x=b
    (str, str) -> solve equation a = b for x
    """
    ts = vy_type(lhs, rhs)
    x = sympy.symbols("x")
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: sympy.solve(
            sympy.Eq((lhs * x**2) + rhs * x, 0), x
        ),
        (NUMBER_TYPE, str): lambda: sympy.solve(
            sympy.Eq(make_expression(rhs), lhs), x
        ),
        (str, NUMBER_TYPE): lambda: sympy.solve(
            sympy.Eq(make_expression(lhs), rhs), x
        ),
        (str, str): lambda: sympy.solve(
            sympy.Eq(make_expression(lhs), make_expression(rhs)), x
        ),
    }.get(ts, lambda: vectorise(quadratic_solver, lhs, rhs, ctx=ctx))()


def quotify(lhs, ctx):
    """Element q
    (any) -> ` + a + ` (Quotify a)
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: "`{}`".format(lhs),
        str: lambda: "`{}`".format(
            lhs.replace("\\", "\\\\").replace("`", "\\`")
        ),
        types.FunctionType: lambda: "`{}`".format(lhs.__name__),
    }.get(ts, lambda: quotify(vy_str(lhs, ctx=ctx), ctx))()


def rand_bits(lhs, ctx):
    """Element ÞB
    (int) -> rand_bits(a)
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: [random.randint(0, 1) for i in range(lhs)],
        (str): lambda: [int(random.choice(bin(ord(c))[2:])) for c in lhs],
    }.get(ts, lambda: vectorise(rand_bits, lhs, ctx=ctx))()


def random_choice(lhs, ctx):
    """Element ℅
    (lst) -> random element of a
    (num) -> Random integer from 0 to a
    """
    if lhs == "":
        return ""
    elif vy_type(lhs, simple=True) is list and len(lhs) == 0:
        return 0
    return random.choice(iterable(lhs, range, ctx=ctx))


def regex_sub(lhs, rhs, other, ctx):
    """Element øṙ
    (str, str, str) -> Replace matches of a with c in b
    (any, any, fun) -> Apply c to matches of a in b
    """
    ts = (vy_type(lhs), vy_type(rhs), vy_type(other))

    if ts[-1] != types.FunctionType:
        return re.sub(
            vy_str(lhs, ctx=ctx), vy_str(other, ctx=ctx), vy_str(rhs, ctx=ctx)
        )
    else:
        parts = re.split("(" + lhs + ")", rhs)
        out = ""
        switch = 1
        for item in parts:

            if switch % 2:
                out += item
            else:
                out += vy_str(safe_apply(other, item, ctx=ctx), ctx=ctx)
            switch += 1

        return out


def remove(lhs, rhs, ctx):
    """Element o
    (num, fun) -> first a positive integers where b is truthy
    (fun, num) -> first b positive integers where a is truthy
    (any, any) -> a.remove(b)
    """
    lhs = iterable(lhs)
    ts = vy_type(lhs)
    if set(vy_type(lhs, rhs)) == {types.FunctionType, NUMBER_TYPE}:
        lhs, rhs = (rhs, lhs) if ts is types.FunctionType else (lhs, rhs)

        @lazylist
        def gen():
            value = 1
            found = 0
            while found != rhs:
                if lhs(value):
                    yield value
                    found += 1
                value += 1

        return gen()
    if ts == str:
        return replace(lhs, rhs, "", ctx)
    elif ts == LazyList:
        return lhs.filter(lambda elem: elem != rhs)
    else:
        return [elem for elem in lhs if elem != rhs]


def remove_at_index(lhs, rhs, ctx):
    """Element ⟇
    (any, num) -> remove item b of a
    """
    lhs, rhs = (rhs, lhs) if vy_type(rhs) != NUMBER_TYPE else (lhs, rhs)
    lhs = iterable(lhs, ctx=ctx)

    return LazyList(item for i, item in enumerate(lhs) if i != rhs)


def remove_non_alphabets(lhs, ctx):
    """Element Ǎ
    (str) -> filter(isalpha, a)
    (num) -> 2 ** a
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: 2**lhs,
        str: lambda: "".join(filter(str.isalpha, lhs)),
    }.get(ts, lambda: vectorise(remove_non_alphabets, lhs, ctx=ctx))()


def remove_until_no_change(lhs, rhs, ctx):
    """Element øo
    (any, any) -> a.remove_until_no_change(b)
    """
    loop = True
    prev = deep_copy(lhs)

    while loop:
        if vy_type(rhs, simple=True) is list:
            for item in rhs:
                lhs = remove(lhs, item, ctx)
        else:
            lhs = remove(lhs, rhs, ctx=ctx)
        if non_vectorising_equals(lhs, prev, ctx):
            loop = False
        else:
            prev = deep_copy(lhs)

    return lhs


def repeat(lhs, rhs, ctx):
    """Element ẋ
    (str, num) -> a * b
    (num, str) -> b * a
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
            curr = value
            while True:
                curr = safe_apply(function, curr, ctx=ctx)
                if curr == prev:
                    break
                prev = curr
                yield curr

        return gen()
    elif ts == (str, NUMBER_TYPE):
        return lhs * int(abs(rhs))
    elif ts == (NUMBER_TYPE, str):
        return rhs * int(abs(lhs))
    elif ts == (str, str):
        return lhs + " " + rhs
    elif ts[0] == NUMBER_TYPE:
        return LazyList(rhs for _ in range(int(abs(lhs))))
    elif ts[1] == NUMBER_TYPE:
        return LazyList(lhs for _ in range(int(abs(rhs))))
    else:
        return vectorise(repeat, lhs, rhs, ctx=ctx)


def replace(lhs, rhs, other, ctx):
    """Element V
    (any, any, any) -> a.replace(b, c)
    """
    ts = vy_type(lhs, rhs, other)
    if types.FunctionType in ts:
        return apply_at(lhs, rhs, other, ctx=ctx)
    if vy_type(lhs, simple=True) is not list:
        res = str(lhs).replace(str(rhs), str(other))
        if vy_type(lhs) == NUMBER_TYPE:
            return vy_eval(res, ctx)
        return res
    else:
        return [other if value == rhs else value for value in iterable(lhs)]


def replace_first(lhs, rhs, other, ctx):
    """Element øḞ
    (any, any, any) -> a.replace_first(b, c)
    """
    if vy_type(lhs, simple=True) is not list:
        return str(lhs).replace(str(rhs), str(other), 1)
    else:

        @lazylist_from(lhs)
        def gen():
            first_found = False
            for item in iterable(lhs, ctx=ctx):
                if first_found:
                    yield item
                elif non_vectorising_equals(rhs, item, ctx):
                    yield other
                    first_found = True
                else:
                    yield item

        return gen()


def replace_nth_occurrence(lhs, rhs, other, n, ctx):
    """Element øṄ
    (any, any, any, num) -> a.replace_nth_occurrence(b, c, d)
    """
    if vy_type(lhs, simple=True) is not list:
        try:
            where = [m.start() for m in re.finditer(str(rhs), str(lhs))][
                n if n < 0 else n - 1
            ]
        except IndexError:
            return str(lhs)
        before = str(lhs)[:where]
        after = str(lhs)[where:].replace(str(rhs), str(other), 1)
        return before + after
    else:

        @lazylist_from(lhs)
        def gen():
            if n >= 0:
                num = 1
            else:
                num = -list(lhs).count(rhs)
            for item in iterable(lhs, ctx=ctx):
                if item == rhs and num == n:
                    yield other
                    num += 1
                else:
                    if item == rhs:
                        num += 1
                    yield item

        return gen()


def replace_until_no_change(lhs, rhs, other, ctx):
    """Element øV
    (any,any,any) -> Replace rhs with other in lhs while lhs changes
    """
    prev = None
    while prev != lhs:
        prev = deep_copy(lhs)
        lhs = replace(lhs, rhs, other, ctx)
    return lhs


def request(lhs, ctx):
    """Element ¨U
    (str) -> Send a GET request to a URL if online"""
    req = urllib.request.Request(
        urlify(lhs), headers={"User-Agent": "Mozilla/5.0 Vyxal"}
    )
    x = urllib.request.urlopen(req).read()
    try:
        return x.decode("utf-8")
    except UnicodeDecodeError:
        return x.decode("latin-1")


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


def right_bit_shift(lhs, rhs, ctx):
    """Element ↳
    (num, num) -> a << b
    (str, num) -> a.rjust(b, " ")
    (num, str) -> b.rjust(a, " ")
    (str, str) -> a.rjust(len(b)-len(a), " ")
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: int(lhs) >> int(rhs),
        (str, NUMBER_TYPE): lambda: lhs.rjust(int(rhs), " "),
        (NUMBER_TYPE, str): lambda: rhs.rjust(int(lhs), " "),
        (str, str): lambda: lhs.rjust(len(rhs), " "),
    }.get(ts, lambda: vectorise(right_bit_shift, lhs, rhs, ctx=ctx))()


def roman_numeral(lhs, ctx):
    """Element øṘ
    (num) -> roman numeral of a
    (str) -> a to decimal from roman numeral
    """
    ints = (1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
    nums = (
        "M",
        "CM",
        "D",
        "CD",
        "C",
        "XC",
        "L",
        "XL",
        "X",
        "IX",
        "V",
        "IV",
        "I",
    )

    big_ints = (
        1000,
        999,
        995,
        990,
        950,
        900,
        500,
        499,
        495,
        490,
        450,
        400,
        100,
        99,
        95,
        90,
        50,
        49,
        45,
        40,
        10,
        9,
        5,
        4,
        1,
    )
    big_nums = (
        "M",
        "IM",
        "VM",
        "XM",
        "LM",
        "CM",
        "D",
        "ID",
        "VD",
        "XD",
        "LD",
        "CD",
        "C",
        "IC",
        "VC",
        "XC",
        "L",
        "IL",
        "VL",
        "XL",
        "X",
        "IX",
        "V",
        "IV",
        "I",
    )
    if vy_type(lhs) is NUMBER_TYPE:
        result = ""
        for i, n in enumerate(ints):
            count = int(lhs / n)
            result += nums[i] * count
            lhs -= n * count
        return result
    elif vy_type(lhs) is str:
        lhs = lhs.upper()
        result = 0
        for i, n in enumerate(big_nums):
            while lhs.startswith(n):
                result += big_ints[i]
                lhs = lhs[len(n) :]
        return result
    elif vy_type(lhs, simple=True) is list:
        return vectorise(roman_numeral, lhs, ctx=ctx)


def rotate_left(lhs, rhs, ctx):
    """Element Ǔ
    (any, num) -> a rotated left by b
    (any) -> a rotated left by 1
    """
    lhs = iterable(lhs, ctx=ctx)
    ts = vy_type(lhs)

    if (ts is LazyList and not bool(lhs)) or len(lhs) == 0:
        return lhs

    if ts is str:
        return lhs[rhs:] + lhs[:rhs]

    @lazylist
    def gen():
        rotating_list = lhs
        for _ in range(rhs):
            rotating_list = rotating_list[1:] + [rotating_list[0]]
        yield from rotating_list

    return gen()


def rotate_right(lhs, rhs, ctx):
    """Element ǔ
    (any, num) -> a rotated right by b
    (any) -> a rotated right by 1
    """
    lhs = iterable(lhs, ctx=ctx)
    ts = vy_type(lhs)

    if (ts is LazyList and not bool(lhs)) or len(lhs) == 0:
        return lhs

    if ts is str:
        return lhs[-rhs:] + lhs[:-rhs]

    @lazylist
    def gen():
        rotating_list = list(lhs)
        for _ in range(abs(int(rhs))):
            rotating_list = [rotating_list[-1]] + rotating_list[:-1]
        yield from rotating_list

    return gen()


def round_to(lhs, rhs, ctx):
    """Element ∆W
    (num, num) -> round(a, no_dec_places=b)
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: sympy.nsimplify(
            str(sympy.N(lhs, int(rhs) + 1)), rational=True
        ),
        (NUMBER_TYPE, str): lambda: -1,
        (str, NUMBER_TYPE): lambda: -1,
        (str, str): lambda: -1,
    }.get(ts, lambda: vectorise(round_to, lhs, rhs, ctx=ctx))()


def run_length_decoding(lhs, ctx):
    """Element ød
    (lst) -> Run length decoding
    """
    temp = flatten_by(
        list(
            map(
                lambda elem: (
                    [elem[1]] * elem[0]
                    if isinstance(elem[1], str)
                    else [elem[0]] * elem[1]
                ),
                lhs,
            )
        ),
        1,
        ctx=ctx,
    )
    if all(isinstance(x, str) for x in temp):
        return "".join(temp)
    else:
        return LazyList(temp)


def run_length_encoding(lhs, ctx):
    """Element øe
    (str) -> List of the form [[character, count], ...]
    """
    lhs = iterable(lhs, ctx=ctx)
    return LazyList(
        map(
            lambda elem: [elem[0], len(list(elem[1]))],
            itertools.groupby(lhs),
        )
    )


def sans_last_prepend_zero(lhs, ctx):
    """Element Þr
    Remove the last item of a list and prepend 0
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: tail_remove(lhs, ctx),  # prepending a 0 to
        # a number just makes it the same number
        str: lambda: "0" + lhs[:-1],  # leave as string
    }.get(ts, lambda: prepend(tail_remove(lhs, ctx), 0, ctx=ctx))()


def separate_runl_encode(lhs, ctx: Context):
    """Element øĖ
    (any) -> run length encode a and push items and lengths both to the stack separately
    """
    enc = run_length_encoding(lhs, ctx)
    items, lengths = transpose(enc)
    ctx.stacks[-1].append(items)
    return lengths


def shortest(lhs, ctx):
    """Element Þg
    (lst) -> Return the shortest item in a list.
    """
    return min_by_function(lhs, length, ctx)


def shuffle(lhs, ctx):
    """Element Þ℅
    (lst) -> Return a random permutation of a
    """
    temp = list(deep_copy(iterable(lhs, range, ctx=ctx)))
    random.shuffle(temp)
    if type(lhs) is str:
        return "".join(temp)
    return LazyList(temp)


def sign_of(lhs, ctx):
    """
    (num) -> sign_of(a) (positive = 1, 0 = 0; negative = -1)
    (str) -> is a numeric
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.sign(lhs),
        str: lambda: int(lhs.isnumeric()),
    }.get(ts, lambda: vectorise(sign_of, lhs, ctx=ctx))()


def sine(lhs, ctx):
    """Element ∆s
    (num) -> sin(a)
    (str) -> sin(expression)
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.nsimplify(sympy.sin(lhs), rational=True),
        str: lambda: str(
            sympy.nsimplify(sympy.sin(make_expression(lhs)), rational=True)
        ),
    }.get(ts, lambda: vectorise(sine, lhs, ctx=ctx))()


def slice_from(lhs, rhs, ctx):
    """Element ȯ
    (fun, num) -> First b integers for which a(x) is truthy
    (any, num) -> a[b:] (Slice from b to the end)
    (str, str) -> vertically merge a and b
    """
    ts = vy_type(lhs, rhs, simple=True)
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
                res = safe_apply(function, item, ctx=ctx)
                if boolify(res, ctx=ctx):
                    found += 1
                    yield item
                item += 1

        return gen()

    else:
        return {
            (str, str): lambda: lhs + "\n" + rhs,
            (str, NUMBER_TYPE): lambda: lhs[int(rhs) :],
            (NUMBER_TYPE, str): lambda: rhs[int(lhs) :],
            (list, NUMBER_TYPE): lambda: lhs[int(rhs) :],
            (NUMBER_TYPE, list): lambda: rhs[int(lhs) :],
            (NUMBER_TYPE, NUMBER_TYPE): lambda: sympy.nsimplify(
                str(lhs)[int(rhs) :].lstrip("0")
            ),
        }.get(ts)()


def sort_by(lhs, rhs, ctx):
    """Element ṡ
    (any, fun) -> sorted(a, key=b) (Sort by b)
    (num, num) -> range(a, b + 1) (Inclusive range from a to b)
    (str, str) -> regex.split(pattern=b, string=a)
    """
    ts = vy_type(lhs, rhs)
    if types.FunctionType in ts:
        function, vector = (
            (lhs, rhs) if ts[0] is types.FunctionType else (rhs, lhs)
        )
        return sorted(
            iterable(vector, ctx=ctx),
            key=lambda x: safe_apply(function, x, ctx=ctx),
        )
    else:
        return {
            (NUMBER_TYPE, NUMBER_TYPE): lambda: LazyList(range(lhs, rhs + 1))
            if lhs <= rhs
            else LazyList(range(lhs, rhs - 1, -1)),
            (str, str): lambda: re.split(rhs, lhs),
        }.get(ts, lambda: vectorise(sort_by, lhs, rhs, ctx=ctx))()


def sort_by_length(lhs, ctx):
    """Element Þṡ
    (lst) -> Sort a list by length.
    """
    return sort_by(lhs, length, ctx)


def sort_every_level(lhs, ctx):
    """Element ÞŻ
    (lst) -> Sort every level of a multidimensional list
    """
    if vy_type(lhs, simple=True) is not list:
        return lhs
    return vy_sort((sort_every_level(item, ctx) for item in lhs), ctx=ctx)


def split_keep(lhs, rhs, ctx):
    """Element Ẇ
    (any, any) -> a.split_and_keep_delimiter(b) (Split and keep the delimiter)
    (fun, any) -> apply a to every second item of b starting with the first item
    """
    ts = vy_type(lhs, rhs)
    if types.FunctionType in ts:
        lhs, rhs = (rhs, lhs) if ts[1] is types.FunctionType else (lhs, rhs)

        @lazylist_from(rhs)
        def gen():
            switch = True
            for item in rhs:
                if switch:
                    yield safe_apply(lhs, item, ctx=ctx)
                    switch = False
                else:
                    yield item
                    switch = True

        return gen()
    if isinstance(lhs, str):
        return re.split(f"({re.escape(vy_str(rhs, ctx=ctx))})", lhs)
    else:
        is_num = vy_type(lhs) == NUMBER_TYPE
        lhs = iterable(abs(lhs) if is_num else lhs, ctx)

        def gen():
            temp = []
            for item in lhs:
                if item == rhs:
                    yield temp[::]
                    yield [item]
                    temp = []
                else:
                    temp.append(item)
            if temp:
                yield temp

        if is_num:
            return LazyList(vy_eval("".join(map(str, x)), ctx) for x in gen())
        else:
            return LazyList(
                gen(), isinf=(type(lhs) is LazyList and lhs.infinite)
            )


def split_on(lhs, rhs, ctx):
    """
    Element €
    (num, num) -> str(lhs).split(rhs)
    (num, str) -> str(lhs).split(rhs)
    (str, num) -> lhs.split(str(rhs))
    (str, str) -> lhs.split(rhs)

    """
    if types.FunctionType in vy_type(lhs, rhs):
        return coords_deepmap(lhs, rhs, ctx=ctx)

    if [primitive_type(lhs), primitive_type(rhs)] == [SCALAR_TYPE, SCALAR_TYPE]:
        temp = str(lhs).split(str(rhs))
        return (
            LazyList(map(lambda x: vy_eval(x, ctx), temp))
            if vy_type(lhs) == NUMBER_TYPE
            else temp
        )

    @lazylist_from(lhs)
    def gen():
        ret, temp = [], []
        for item in iterable(lhs, ctx=ctx):
            if item == rhs:
                yield temp[::]
                temp = []
            else:
                temp.append(item)
        if temp:
            yield temp

    return gen()


def square(lhs, ctx):
    """Element ²
    (num) -> a ** 2 (Squared)
    (str) -> a formatted as a square
    """

    def grid_helper(string):
        temp = string
        while not is_square(len(temp), ctx):
            temp += " "
        return wrap(temp, int(square_root(len(temp), ctx)), ctx)

    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: exponent(lhs, 2, ctx),
        str: lambda: grid_helper(lhs),
    }.get(ts, lambda: vectorise(square, lhs, ctx=ctx))()


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


def starts_with(lhs, rhs, ctx):
    """Element øp
    (str, str) -> True if a starts with b
    """
    ts = primitive_type(lhs), primitive_type(rhs)
    return int(
        {
            (list, SCALAR_TYPE): lambda: lhs[0] == rhs,
            (SCALAR_TYPE, list): lambda: rhs[0] == lhs,
            (list, list): lambda: lhs[0] == rhs,
        }.get(ts, lambda: vy_str(lhs).startswith(vy_str(rhs)))()
    )


def starts_with_set(lhs, rhs, ctx):
    """Element øs
    (list, list) -> True if a starts with all of b
    """
    ts = primitive_type(lhs), primitive_type(rhs)
    return int(
        {
            (list, list): lambda: lhs[: len(rhs)] == rhs,
            (list, SCALAR_TYPE): lambda: lhs[0] == rhs,
            (SCALAR_TYPE, list): lambda: rhs[0] == lhs,
        }.get(ts, lambda: vy_str(lhs).startswith(vy_str(rhs)))()
    )


def strict_greater_than(lhs, rhs, ctx):
    """Element ¨>
    Non-vectorising greater than
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: int(bool(lhs > rhs)),
        (NUMBER_TYPE, str): lambda: int(str(lhs) > rhs),
        (str, NUMBER_TYPE): lambda: int(lhs > str(rhs)),
        (str, str): lambda: int(lhs > rhs),
    }.get(
        ts,
        lambda: int(
            bool(list(iterable(lhs, ctx=ctx)) > list(iterable(rhs, ctx=ctx)))
        ),
    )()


def strict_less_than(lhs, rhs, ctx):
    """Element ¨>
    Non-vectorising less than
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: int(bool(lhs < rhs)),
        (NUMBER_TYPE, str): lambda: int(str(lhs) < rhs),
        (str, NUMBER_TYPE): lambda: int(lhs < str(rhs)),
        (str, str): lambda: int(lhs < rhs),
    }.get(
        ts,
        lambda: int(
            bool(list(iterable(lhs, ctx=ctx)) < list(iterable(rhs, ctx=ctx)))
        ),
    )()


def strip(lhs, rhs, ctx):
    """Element P
    (any, any) -> a.strip(b)
    (any, fun) -> all elements in a where the result of b(x) is lowest
    """
    if vy_type(lhs) == types.FunctionType:
        lhs, rhs = rhs, lhs
    if vy_type(rhs) == types.FunctionType:
        lhs = iterable(lhs, ctx=ctx)
        if not lhs:
            return lhs
        m = None
        fun_vals = []
        for item in lhs:
            fn_val = safe_apply(rhs, item, ctx=ctx)
            fun_vals.append(fn_val)
            if m is None:
                m = fn_val
            else:
                m = dyadic_minimum(m, fn_val, ctx=ctx)

        return LazyList(item for item in lhs if fun_vals.pop(0) == m)
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: vy_eval(
            vy_str(lhs, ctx=ctx).strip(vy_str(rhs, ctx=ctx)),
            ctx,
        ),
        (NUMBER_TYPE, str): lambda: vy_eval(
            vy_str(lhs, ctx=ctx).strip(rhs), ctx
        ),
        (str, NUMBER_TYPE): lambda: lhs.strip(str(rhs)),
        (str, str): lambda: lhs.strip(rhs),
    }.get(ts, lambda: strip_list_helper(lhs, rhs, ctx))()


def strip_left(lhs, rhs, ctx):
    """Element øl
    (any, any) -> a.lstrip(b)
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: vy_eval(
            vy_str(lhs, ctx=ctx).lstrip(vy_str(rhs, ctx=ctx)),
            ctx,
        ),
        (NUMBER_TYPE, str): lambda: vy_eval(
            vy_str(lhs, ctx=ctx).lstrip(rhs), ctx
        ),
        (str, NUMBER_TYPE): lambda: lhs.lstrip(str(rhs)),
        (str, str): lambda: lhs.lstrip(rhs),
    }.get(ts, lambda: strip_list_helper(lhs, rhs, ctx, -1))()


def strip_list_helper(left, right, ctx, sign=0):
    """This doesn't make sense anywhere but here"""
    inf_left, inf_right = False, False
    if vy_type(left) is LazyList and left.infinite:
        inf_left = True
    if vy_type(right) is LazyList and right.infinite:
        inf_right = True
    if (
        vy_type(left, simple=True) != list
        and vy_type(right, simple=True) == list
    ):
        return strip_list_helper(right, left, ctx, sign)
    if vy_type(right, simple=True) != list:
        right = [right]
    if not left:
        return []  # how you gonna strip from nothing
    if not right:
        return left

    # Strip from the right side first
    # check to make sure there's stuff to strip

    if not inf_left and not inf_right and len(left) < len(right):
        # left is smaller than right
        # e.g. [1, 2, 3].strip([2, 3, 4, 5, 6])
        if left in (right[: len(left)], right[: len(left) : -1]):
            return []

    def strip_front(lst, unwanted):
        """Strip from only the front"""
        start_ind = 0
        for item in lst:
            if item not in unwanted:
                break
            start_ind += 1

        return lst[start_ind:]

    if sign == 0:
        left = strip_front(left, right)

        if not inf_left:
            left = strip_front(left[::-1], right)[::-1]
    elif sign == -1:
        left = strip_front(left, right)
    else:
        left = strip_front(left[::-1], right)[::-1]

    return left


def strip_right(lhs, rhs, ctx):
    """Element ør
    (any, any) -> a.rstrip(b)
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: vy_eval(
            vy_str(lhs, ctx=ctx).rstrip(vy_str(rhs, ctx=ctx)),
            ctx,
        ),
        (NUMBER_TYPE, str): lambda: vy_eval(
            vy_str(lhs, ctx=ctx).rstrip(rhs), ctx
        ),
        (str, NUMBER_TYPE): lambda: lhs.rstrip(str(rhs)),
        (str, str): lambda: lhs.rstrip(rhs),
    }.get(ts, lambda: strip_list_helper(lhs, rhs, ctx, 1))()


def strip_whitespace(lhs, ctx):
    """Element øS
    (str) -> a.strip()
    (num) -> Remove trailing zeros
    """
    ts = vy_type(lhs)
    return {
        str: lambda: vy_str(lhs, ctx=ctx).strip(),
        NUMBER_TYPE: lambda: (
            strip_whitespace(lhs // 10, ctx) if lhs % 10 == 0 else lhs
        ),
    }.get(ts, lambda: vectorise(strip_whitespace, lhs, ctx=ctx))()


def strip_whitespace_left(lhs, ctx):
    """Element øL
    (str) -> a.lstrip()
    """
    ts = vy_type(lhs)
    return {str: lambda: vy_str(lhs, ctx=ctx).lstrip()}.get(
        ts, lambda: vectorise(strip_whitespace_left, lhs, ctx=ctx)
    )()


def strip_whitespace_right(lhs, ctx):
    """Element øR
    (str) -> a.rstrip()
    """
    ts = vy_type(lhs)
    return {str: lambda: vy_str(lhs, ctx=ctx).rstrip()}.get(
        ts, lambda: vectorise(strip_whitespace_right, lhs, ctx=ctx)
    )()


def sublists(lhs, ctx):
    """Element ÞS
    Sublists of a list.
    """
    lhs = iterable(lhs, range, ctx=ctx)

    @lazylist_from(lhs)
    def gen():
        for prefix in prefixes(lhs, ctx=ctx):
            yield from suffixes(prefix, ctx=ctx)

    return gen()


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


def suffixes_element(lhs, ctx):
    """Element ÞK
    (lst) -> Suffixes of a
    """
    temp = suffixes(lhs, ctx)
    if vy_type(lhs) == NUMBER_TYPE:
        return LazyList(
            map(lambda x: vy_eval(first_integer(x, ctx), ctx), temp)
        )
    else:
        return temp


def surround(lhs, rhs, ctx):
    """Element ø.
    (str, str) -> Surround a with b
    (list, any) -> Surround a with b
    """
    # Also works with lists!
    ts = vy_type(lhs, rhs, simple=True)
    print(ts)
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: sympy.nsimplify(
            str(rhs) + str(lhs) + str(rhs), rational=True
        ),
        (str, NUMBER_TYPE): lambda: str(rhs) + lhs + str(rhs),
        (NUMBER_TYPE, str): lambda: rhs + str(lhs) + rhs,
        (str, str): lambda: rhs + lhs + rhs,
        (list, list): lambda: rhs + lhs + rhs,
        (list, NUMBER_TYPE): lambda: [rhs] + list(lhs) + [rhs],
        (list, str): lambda: [rhs] + list(lhs) + [rhs],
        (str, list): lambda: [lhs] + list(rhs) + [lhs],
        (NUMBER_TYPE, list): lambda: [lhs] + list(rhs) + [lhs],
    }.get(ts)()


def symmetric_difference(lhs, rhs, ctx):
    """Element ⊍
    (any, any) -> set(a) ^ set(b)
    """
    if type(lhs) == str and vy_type(rhs) == NUMBER_TYPE:
        return symmetric_difference(lhs, str(rhs), ctx)
    if type(rhs) == str and vy_type(lhs) == NUMBER_TYPE:
        return symmetric_difference(str(lhs), rhs, ctx)
    lhs = uniquify(iterable(lhs, ctx=ctx), ctx)
    rhs = uniquify(iterable(rhs, ctx=ctx), ctx)

    @lazylist
    def gen():
        for item in lhs:
            if item not in rhs:
                yield item
        for item in rhs:
            if item not in lhs:
                yield item

    return gen()


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


def tail_remove(lhs, ctx):
    """Element Ṫ
    (any) -> a[:-1] (All but the last item)
    """
    temp = index(iterable(lhs, ctx=ctx), [0, -1], ctx=ctx)
    if is_sympy(lhs) and all(isinstance(x, int) for x in temp):
        return int("".join(str(x) for x in temp or "0"))
    else:
        return temp


def tangent(lhs, ctx):
    """Element ∆t
    (num) -> tan(a)
    (str) -> tan(expression)
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.nsimplify(sympy.tan(lhs), rational=True),
        str: lambda: str(
            sympy.nsimplify(sympy.tan(make_expression(lhs)), rational=True)
        ),
    }.get(ts, lambda: vectorise(tangent, lhs, ctx=ctx))()


def to_base(lhs, rhs, ctx):
    """Element τ
    Convert lhs from base 10 to base rhs
    """
    if vy_type(lhs) not in [NUMBER_TYPE, list, LazyList]:
        raise ValueError("to_base only works on numbers and lists")

    if vy_type(lhs, simple=True) is list:
        return vectorise(to_base, lhs, rhs, ctx=ctx)

    if vy_type(rhs) == NUMBER_TYPE:
        return to_base_digits(lhs, rhs)
    else:
        rhs = iterable(rhs, ctx=ctx)

    if len(rhs) == 0:
        return 0
    elif len(rhs) == 1:
        maximal_exponent = lhs
    elif lhs == 0:
        return [index(rhs, 0, ctx)]
    else:
        maximal_exponent = int(log_mold_multi(lhs, len(rhs), ctx))

    res = []
    for i in range(maximal_exponent, -1, -1):
        digit, remaining = divmod(lhs, len(rhs) ** i)
        res.append(index(rhs, digit, ctx))
        lhs = remaining

    if all(isinstance(x, str) for x in res) and all(len(x) == 1 for x in res):
        return "".join(res)
    return res


def to_decimal(lhs, ctx):
    """Element øḋ
    (num) -> to_decimal(lhs)
    (str) -> decimal representation of interpreting lhs as a fraction
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: str(float(lhs)),
        str: lambda: to_decimal(vy_eval(lhs, ctx), ctx),
    }.get(ts, lambda: vectorise(to_decimal, lhs, ctx=ctx))()


def to_degrees(lhs, ctx):
    """Element ∆D
    (num) -> a * (180 / pi)
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: lhs * (180 / sympy.pi),
        str: lambda: int(lhs) * (180 / sympy.pi),
    }.get(ts, lambda: vectorise(to_degrees, lhs, ctx=ctx))()


def to_radians(lhs, ctx):
    """Element ∆R
    (num) -> a * (pi / 180)
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: lhs * (sympy.pi / 180),
        str: lambda: int(lhs) * (sympy.pi / 180),
    }.get(ts, lambda: vectorise(to_radians, lhs, ctx=ctx))()


def totient(lhs, ctx):
    """Element ∆ṫ
    (num) -> Euler's totient function
    (str) -> local minima of a function
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: sympy.totient(lhs),
        str: lambda: local_minima(lhs),
    }.get(ts, lambda: vectorise(totient, lhs, ctx=ctx))()


def transliterate(lhs, rhs, other, ctx):
    """Element Ŀ
    (any, any, any) -> transliterate lhs according to the
                       mapping rhs->other
    """
    ts = (vy_type(lhs), vy_type(rhs), vy_type(other))
    if types.FunctionType in ts:
        if ts.count(types.FunctionType) < 2:
            raise TypeError(
                "Repeat while false requires two or three functions"
            )
        # Swap the arguments so that the scalar is always in other if
        # there's a scalar else leave as is

        functions = list(
            filter(lambda x: isinstance(x, types.FunctionType), ts)
        )
        scalars = list(
            filter(lambda x: not isinstance(x, types.FunctionType), ts)
        )

        function, predicate, scalar = functions + scalars

        result = collect_until_false(predicate, function, scalar)
        return safe_apply(function, result[-1], ctx=ctx)

    mapping = dict(vy_zip(iterable(rhs, ctx), iterable(other, ctx), ctx=ctx))

    ret = []

    for item in iterable(lhs, ctx):
        for x in mapping:
            if non_vectorising_equals(item, x, ctx):
                ret.append(mapping[x])
                break
        else:
            ret.append(item)

    if (
        type(lhs) is str
        and all(isinstance(x, str) for x in ret)
        and all(len(x) == 1 for x in ret)
    ):
        return "".join(ret)
    elif vy_type(lhs) == NUMBER_TYPE and all(
        vy_type(x) == NUMBER_TYPE or x in ".-" for x in ret
    ):
        try:
            return sympy.nsimplify(int("".join(map(str, ret))), rational=True)
        except:
            return "".join(map(str, ret))
    else:
        return ret


def truthy_indices(lhs, ctx):
    """Element T
    (any) -> indices of truthy elements
    (num) -> lhs * 3
    """
    if vy_type(lhs) in (types.FunctionType, NUMBER_TYPE):
        return multiply(lhs, 3, ctx)

    lhs = iterable(lhs, ctx=ctx)

    @lazylist_from(lhs)
    def helper():
        i = 0
        for x in lhs:
            if x:
                yield i
            i += 1

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
    lhs = iterable(lhs, ctx=ctx)
    return [
        index(deep_copy(lhs), [None, None, 2], ctx),
        index(lhs, [1, None, 2], ctx),
    ]


def union(lhs, rhs, ctx):
    """Element ∪
    (any, any) -> union of lhs and rhs
    """

    def gen():
        seen = []
        for item in iterable(lhs, ctx=ctx):
            if item not in seen:
                yield item
                seen.append(item)
        for item in iterable(rhs, ctx=ctx):
            if item not in seen:
                yield item
                seen.append(item)

    return LazyList(
        gen(),
        isinf=(
            (type(lhs) is LazyList and lhs.isinf)
            or (type(rhs) is LazyList and rhs.isinf)
        ),
    )


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

    @lazylist_from(lhs)
    def f():
        seen = []
        t = iterable(lhs, ctx=ctx)
        for item in t:
            if item not in seen:
                yield item
                seen.append(item)

    return f()


def uniquify_mask(lhs, ctx):
    """Element ÞU
    (any) -> A list of booleans describing which elements of a will
             remain after uniquifying.
    """
    lhs = iterable(lhs, ctx=ctx)
    # TODO (user/cgccuser): Reduce code duplication here?
    if isinstance(lhs, list):
        seen = set()
        mask = []
        for elem in lhs:
            if elem not in seen:
                mask.append(1)
                seen.add(elem)
            else:
                mask.append(0)
        return mask

    @lazylist_from(lhs)
    def gen():
        seen = set()
        for elem in lhs:
            if elem not in seen:
                seen.add(elem)
                yield 1
            else:
                yield 0

    return gen()


def untruth(lhs, ctx):
    """Element Þǔ
    (any) -> [int(x in a) for x in range(max(a))]
    """
    lhs = iterable(lhs, ctx=ctx)
    if any(vy_type(x) != NUMBER_TYPE for x in lhs):
        lhs = [iterable(x, ctx=ctx) for x in lhs]
        dimensions = len(lhs[0])
        maxCoords = [max(x[i] for x in lhs) + 1 for i in range(dimensions)]
        deep_listify = (
            lambda a: [deep_listify(x) for x in a]
            if vy_type(a, simple=True) is list
            else a
        )
        matrix = deep_listify(zero_matrix(maxCoords[::-1], ctx=ctx))
        for x in lhs:
            ref = matrix
            for i in range(dimensions - 1):
                ref = ref[x[i]]
            ref[x[dimensions - 1]] = 1
        return matrix
    return [int(x in lhs) for x in range((monadic_maximum(lhs, ctx) or -1) + 1)]


def unwrap(lhs, ctx):
    """Element Þẇ
    (lst) -> Take a and push a[0]+a[-1] and a[1:-1]
    """
    lhs = iterable(lhs, ctx=ctx)

    if vy_type(lhs) is str:
        if not lhs:
            return ("", "")
        return (lhs[0] + lhs[-1], lhs[1:-1])
    else:
        if vy_type(lhs, simple=True) == list and not len(lhs):
            return ([], [])
        rest = head_remove(tail_remove(lhs, ctx), ctx)
        return ([lhs[0], lhs[-1]], rest)


def vectorise(
    function,
    *args,
    explicit=False,
    ctx: Context = None,
):
    """
    Maps a function over arguments
    Probably cursed but whatever.
    The explicit argument is mainly for stopping element-wise
    vectorisation happening.
    """
    if explicit:
        # explicit vectorisation
        # Algorithm: If there are no lists, vectorise over lhs.
        # Else, vectorise over the first list.
        ts = [primitive_type(x) for x in args]
        if list not in ts:
            return LazyList(
                safe_apply(function, x, *args[1:], ctx=ctx)
                for x in iterable(args[0], range, ctx=ctx)
            )
        else:
            index = ts.index(list)
            return LazyList(
                safe_apply(
                    function, *args[:index], x, *args[index + 1 :], ctx=ctx
                )
                for x in iterable(args[index], ctx=ctx)
            )
    else:
        # regular vectorisation
        def vectorise_helper():
            # Like zip, but repeats scalars
            @lazylist
            def row_helper(index):
                for item in args:
                    if primitive_type(item) is list:
                        if has_ind(item, index):
                            yield item[index]
                        else:
                            yield 0
                    else:
                        yield item

            r = 0
            while True:
                if any(
                    primitive_type(arg) is list and has_ind(arg, r)
                    for arg in args
                ):
                    row = row_helper(r)
                    yield safe_apply(function, *row, ctx=ctx)
                else:
                    break
                r += 1

        return LazyList(
            vectorise_helper(),
            isinf=any(type(x) is LazyList and x.infinite for x in args),
        )


def vectorised_not(lhs, ctx):
    """List overload for element †"""
    return {NUMBER_TYPE: lambda: int(not lhs), str: lambda: int(not lhs)}.get(
        vy_type(lhs), lambda: vectorise(vectorised_not, lhs, ctx=ctx)
    )()


def vectorised_sum(lhs, ctx):
    """Element Ṡ
    (lst) -> the equivalent of v∑
    (str) -> strip whitespace from both sides
    (num) -> is positive
    """
    ts = vy_type(lhs, simple=True)
    return {
        list: lambda: LazyList(
            vy_sum(iterable(x, ctx=ctx), ctx) for x in iterable(lhs, ctx=ctx)
        ),
        str: lambda: vy_str(lhs, ctx=ctx).strip(),
        NUMBER_TYPE: lambda: 1 if lhs > 0 else 0,
    }.get(ts)()


def vertical_join(lhs, rhs=" ", ctx=None):
    """Element §
    any: Transpose a (filling with b), join on newlines
    """
    if not lhs:
        return ""
    # Make every list in lhs the same length, padding left with b
    lhs = vectorise(vy_str, lhs, ctx=ctx)
    lhs, rhs = iterable(lhs, ctx=ctx), iterable(rhs, ctx=ctx)
    max_length = max(len(x) for x in lhs)
    temp = [
        [rhs] * (len(x) < max_length and max_length - len(x)) + x
        if vy_type(x, simple=True) is list
        else rhs * (len(x) < max_length and max_length - len(x)) + x
        for x in lhs
    ]
    temp = [join(x, "", ctx) for x in transpose(temp, rhs, ctx=ctx)]
    return join(temp, "\n", ctx)


def vertical_join_with_filler(lhs, rhs, ctx):
    """Element øε
    (lst, any) -> Vertical join of lhs with rhs, with filler
    """
    return vertical_join(lhs, rhs, ctx)


def vertical_mirror(lhs, rhs=None, ctx=None):
    """Element øṁ and øṀ"""
    if type(lhs) is str:
        if rhs:
            temp = [
                s + transliterate(rhs[0], rhs[1], s[::-1], ctx)
                for s in lhs.split("\n")
            ]
            return "\n".join(temp)
        else:
            return "\n".join([mirror(s, ctx) for s in lhs.split("\n")])
    elif vy_type(lhs) == NUMBER_TYPE:
        return mirror(lhs, ctx=ctx)
    else:
        return vectorise(vertical_mirror, lhs, rhs, ctx=ctx)


def vertical_mirror_center_join(lhs, ctx):
    """Element øm
    (str) -> lhs vertically mirrored, with brackets flipped, then centered by padding with spaces, then joined on newlines.
    """
    return join_newlines(
        center(flip_brackets_vertical_mirror(lhs, ctx=ctx), ctx=ctx), ctx=ctx
    )


def vertical_palindromise_center_join(lhs, ctx):
    """Element øṗ
    (str) -> lhs vertically palindromised without duplicating the center, with brackets flipped, then centered by padding with spaces, then joined on newlines.
    """
    return join_newlines(
        center(flip_brackets_vertical_palindromise(lhs, ctx=ctx), ctx=ctx),
        ctx=ctx,
    )


def vy_abs(lhs, ctx):
    """Elements ȧ
    (num) -> abs(a)
    (str) -> remove whitespace from a
    """
    return {
        NUMBER_TYPE: lambda: abs(lhs),
        str: lambda: "".join(lhs.split()),
    }.get(vy_type(lhs), lambda: vectorise(vy_abs, lhs, ctx=ctx))()


def vy_bin(lhs, ctx):
    """Element b
    (num) -> list of binary digits
    (str) -> binary of each codepoint
    """
    ts = vy_type(lhs)
    if ts == NUMBER_TYPE:
        if lhs < 0:
            temp = [int(x) for x in bin(int(lhs))[3:]]
            return vectorise(negate, temp, ctx=ctx)
        else:
            return [int(x) for x in bin(int(lhs))[2:]]
    elif ts == str:
        return vectorise(
            vy_bin, wrapify(chr_ord(lhs, ctx=ctx), None, ctx=ctx), ctx=ctx
        )
    else:
        return vectorise(vy_bin, lhs, ctx=ctx)


def vy_ceil(lhs, ctx):
    """Element ⌈
    (num) -> ceil(a)
    (str) -> a.split(' ') # split a on spaces
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: lhs.imag
        if type(lhs) == complex
        else (
            sympy.im(lhs)
            if is_sympy(lhs) and not lhs.is_real
            else math.ceil(lhs)
        ),
        (str): lambda: lhs.split(" "),
    }.get(ts, lambda: vectorise(vy_ceil, lhs, ctx=ctx))()


def vy_divmod(lhs, rhs, ctx):
    """Element ḋ
    (num, num) -> [lhs // rhs, lhs % rhs]
    (iterable, num) -> combinations of a with length b
    (str, str) ->  overwrite the start of a with b
    """
    ts = vy_type(lhs, rhs, simple=True)

    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: [
            integer_divide(lhs, rhs, ctx),
            modulo(lhs, rhs, ctx),
        ],
        (NUMBER_TYPE, str): lambda: vyxalify(
            map(vy_sum, itertools.combinations(rhs, lhs))
        ),
        (str, NUMBER_TYPE): lambda: vyxalify(
            map(vy_sum, itertools.combinations(lhs, rhs))
        ),
        (str, str): lambda: rhs + lhs[len(rhs) :],
        (list, NUMBER_TYPE): lambda: combinations(lhs, rhs),
        (NUMBER_TYPE, list): lambda: combinations(rhs, lhs),
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

    Beware, this doesn't return a single value, it returns a list!
    If lhs is a str, it executes it and returns an empty list.
    Otherwise, it wraps the result in a singleton list.
    """
    if vy_type(lhs) is str:
        import vyxal.transpile

        stack = ctx.stacks[-1]
        exec(vyxal.transpile.transpile(lhs, options=ctx.transpilation_options))

        return []

    def helper(lhs):
        if vy_type(lhs) == NUMBER_TYPE:
            return divide(1, lhs, ctx)
        elif vy_type(lhs) is str:
            import vyxal.transpile

            stack = []
            exec(
                vyxal.transpile.transpile(
                    lhs, options=ctx.transpilation_options
                )
            )

            return pop(stack, 1, ctx)
        else:
            return vectorise(helper, lhs, ctx=ctx)

    temp = helper(lhs)
    return [temp]


def vy_filter(lhs: Any, rhs: Any, ctx):
    """Element F
    (any, fun) -> Keep elements in a that b is true for
    (any, any) -> Remove elements of a that are in b
    """
    ts = vy_type(lhs, rhs)
    if ts[0] == types.FunctionType:
        return vy_filter(rhs, lhs, ctx)
    if ts[1] == types.FunctionType:
        arity = (
            rhs.stored_arity
            if hasattr(rhs, "stored_arity")
            else (rhs.arity if hasattr(rhs, "arity") else None)
        )
        if not arity or arity == 1:

            @lazylist_from(lhs)
            def gen():
                for x in iterable(lhs, range, ctx=ctx):
                    if safe_apply(rhs, x, ctx=ctx):
                        yield x

            return gen()
        if arity == 2:

            @lazylist_from(lhs)
            def gen():
                idx = 0
                for x in iterable(lhs, range, ctx=ctx):
                    if safe_apply(rhs, x, idx, ctx=ctx):
                        yield x
                    idx += 1

            return gen()
        if arity == 3:

            @lazylist_from(lhs)
            def gen():
                idx = 0
                for x in iterable(lhs, range, ctx=ctx):
                    if safe_apply(rhs, x, idx, lhs, ctx=ctx):
                        yield x
                    idx += 1

            return gen()
    if ts == (str, str):
        return "".join(elem for elem in lhs if elem not in rhs)

    res = LazyList(
        elem
        for elem in iterable(lhs, ctx=ctx)
        if elem not in iterable(rhs, ctx=ctx)
    )

    if ts[0] in (NUMBER_TYPE, str):
        res = "".join(map(str, res))
        if ts[0] == NUMBER_TYPE:
            return vy_eval(res, ctx)
        return res
    return res


def vy_floor(lhs, ctx):
    """Element ⌊
    (num) -> floor(a)
    (str) -> integer part of a
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: lhs.real
        if type(lhs) == complex
        else (
            sympy.re(lhs)
            if is_sympy(lhs) and not lhs.is_real
            else sympy.floor(lhs)
        ),
        (str): lambda: vy_floor_str_helper(lhs),
    }.get(ts, lambda: vectorise(vy_floor, lhs, ctx=ctx))()


def vy_gcd(lhs, rhs=None, ctx=None):

    ts = vy_type(lhs, rhs)

    if rhs is None:
        return math.gcd(*iterable(lhs, ctx=ctx))
    return {
        (NUMBER_TYPE, NUMBER_TYPE): lambda: math.gcd(lhs, rhs),
        (NUMBER_TYPE, str): lambda: vy_gcd(
            lhs, wrapify(chr_ord(rhs, ctx), None, ctx), ctx=ctx
        ),
        (str, str): lambda: longest_suffix(lhs, rhs),
        (types.FunctionType, ts[1]): lambda: group_by_function(
            iterable(rhs, ctx=ctx), lhs, ctx
        ),
        (ts[0], types.FunctionType): lambda: group_by_function(
            iterable(lhs, ctx=ctx), rhs, ctx
        ),
    }.get(ts, lambda: vectorise(vy_gcd, lhs, rhs, ctx=ctx))()


def vy_hex(lhs, ctx):
    """Element H
    (num) -> hex(a)
    (str) -> int(a, 16)
    """
    ts = vy_type(lhs)
    return {
        (NUMBER_TYPE): lambda: hex(lhs)[2:],
        (str): lambda: int(lhs, 16),
    }.get(ts, lambda: vectorise(vy_hex, lhs, ctx=ctx))()


def vy_int(item: Any, base: int = 10, ctx: Context = DEFAULT_CTX):
    """Converts the item to the given base. Lists are treated as if
    each item was a digit.

    Used for multiple elements, and has to be here because it uses
    functions defined only here."""
    t_item = type(item)
    if primitive_type(item) != SCALAR_TYPE:
        ret = 0
        for element in item:
            ret = multiply(ret, base, ctx)
            ret = add(ret, element, ctx)
        return ret
    elif t_item is str:
        try:
            return int(item, base)
        except ValueError:
            return 0
    elif t_item is complex:
        return item.real
    elif t_item is float or is_sympy(item):
        return int(item)
    elif t_item:
        return vy_int(iterable(item, ctx=ctx), base)


def vy_map_or_pair_each(lhs, rhs, ctx):
    """Element M
    (any, fun) -> apply function b to each element of a
    (any, any) -> a paired with each item of b
    """
    ts = vy_type(lhs, rhs)
    if types.FunctionType not in ts:
        return LazyList([[lhs, x] for x in iterable(rhs, range, ctx=ctx)])

    function, itr = (rhs, lhs) if ts[-1] is types.FunctionType else (lhs, rhs)
    return vy_map(function, iterable(itr, range, ctx=ctx), ctx=ctx)


def vy_print(lhs, end="\n", ctx=None):
    """Element ,
    (any) -> send to stdout
    """
    ctx.printed = True
    ts = vy_type(lhs)

    if ts is LazyList:
        lhs.output(end=end, ctx=ctx)
    elif ts is list:
        vy_print(LazyList(lhs), end, ctx)
    elif ts is types.FunctionType:
        args = (
            wrapify(ctx.stacks[-1], lhs.arity, ctx=ctx)
            if lhs.arity != -1
            else [ctx.stacks[-1]]
        )

        override = None if lhs.arity != -1 else lhs.arity
        res = safe_apply(lhs, *args, ctx=ctx, arity_override=override)
        if lhs.arity == -1:
            res = res[-1]
        vy_print(res, ctx=ctx)
    else:
        if is_sympy(lhs):
            if ctx.print_decimals and not lhs.is_Integer:
                lhs = str(float(lhs))
            else:
                lhs = sympy.nsimplify(lhs.round(20), rational=True)
        if ctx.online:
            ctx.online_output[1] += vy_str(lhs, ctx=ctx) + end
        else:
            print(lhs, end=end)


def vy_reduce(lhs, rhs, ctx):
    """Element R
    (any, fun) -> Reduce a by function b
    (fun, any) -> Reduce b by function a
    """
    ts = vy_type(lhs, rhs)
    return {
        (ts[0], types.FunctionType): lambda: foldl(
            rhs, iterable(lhs, ctx=ctx), ctx=ctx
        ),
        (types.FunctionType, ts[1]): lambda: foldl(
            lhs, iterable(rhs, ctx=ctx), ctx=ctx
        ),
    }.get(ts)()


def vy_repr(lhs, ctx):
    ts = vy_type(lhs)
    string_character = "`" if ctx.vyxal_lists else '"'
    return {
        (NUMBER_TYPE): lambda: str(float(lhs))
        if ctx.print_decimals and not lhs.is_Integer
        else str(sympy.nsimplify(lhs.round(20), rational=True)),
        (str): lambda: string_character
        + lhs.replace("\\", "\\\\").replace(
            string_character, "\\" + string_character
        )
        + string_character,
        (types.FunctionType): lambda: vy_repr(
            safe_apply(lhs, *ctx.stacks[-1], ctx=ctx), ctx
        )
        # actually make the repr kinda make sense
    }.get(
        ts,
        lambda: ("⟨ " if ctx.vyxal_lists else "[")
        + (" | " if ctx.vyxal_lists else ", ").join(
            map(
                lambda x: vy_repr(x, ctx),
                list(lhs) or [],
            )
        )
        + (" ⟩" if ctx.vyxal_lists else "]"),
    )()


def vy_round(lhs, ctx):
    """Element ṙ
    (num) -> round(a)
    (str) -> quad palindromize with overlap
    (lst) -> vectorised
    """
    ts = vy_type(lhs)
    return {
        NUMBER_TYPE: lambda: [lhs.real, lhs.imag]
        if type(lhs) == complex
        else (
            list(lhs.as_real_imag())
            if is_sympy(lhs) and not lhs.is_real
            else round(lhs)
        ),
        str: lambda: vertical_mirror(lhs, ctx=ctx)
        + "\n"
        + vertical_mirror(lhs, ctx=ctx)[::-1],
    }.get(ts, lambda: vectorise(vy_round, lhs, ctx=ctx))()


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
    if vy_type(lhs) == NUMBER_TYPE:
        sign = 1 if lhs >= 0 else -1
        number = str(sympy.N(abs(lhs), 15))
        parts = [
            "".join(sorted(x.strip("0"))) or "0" for x in number.split(".")
        ]
        return sympy.nsimplify(".".join(parts), rational=True) * sign

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
        (NUMBER_TYPE): lambda: str(sympy.nsimplify(lhs, rational=True)),
        (str): lambda: lhs,  # wow so complex and hard to understand /s
        (types.FunctionType): lambda: vy_str(
            safe_apply(lhs, *ctx.stacks[-1], ctx=ctx), ctx
        ),
    }.get(
        ts,
        lambda: ("⟨ " if ctx.vyxal_lists else "[")
        + (" | " if ctx.vyxal_lists else ", ").join(
            map(
                lambda x: vy_repr(x, ctx),
                list(lhs) or [],
            )
        )
        + (" ⟩" if ctx.vyxal_lists else "]"),
    )()


def vy_sum(lhs, ctx=None):
    """Element ∑
    (any) -> reduce a by addition
    """
    lhs = iterable(lhs, ctx=ctx)
    if not lhs:
        return 0
    return foldl(add, lhs, ctx=ctx)


def vy_zip(lhs, rhs, ctx):
    """Element Z
    (any, any) -> zip(a, b)
    (any, fun) -> zip(a,map(b,a)) (Zipmap, map and zip)
    """
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

        return LazyList(
            f(),
            isinf=(
                (type(lhs) is LazyList and lhs.infinite)
                or (type(rhs) is LazyList and rhs.infinite)
            ),
        )


def wrap(lhs, rhs, ctx):
    """Element ẇ
    (any, num) -> a wrapped in chunks of length b
    (num, any) -> b wrapped in chunks of length a
    (any, lst) -> Wrap a into chunks with lengths given in b, repeating if necessary
    (lst, str) -> Wrap b into chunks with lengths given in a, repeating if necessary
    (any, fun) -> Apply b to every second item of a
    (fun, any) -> Apply a to every second item of b
    (str, str) -> split a on first occurance of b
    """
    ts = vy_type(lhs, rhs)
    if types.FunctionType in ts:
        if ts[0] == types.FunctionType:
            fun, elem = lhs, rhs
        else:
            fun, elem = rhs, lhs

        @lazylist_from(elem)
        def gen():
            apply = False
            for item in iterable(elem, ctx=ctx):
                if apply:
                    yield safe_apply(fun, item, ctx=ctx)
                else:
                    yield item
                apply = not apply

        return gen()
    elif NUMBER_TYPE in ts:
        if ts[0] == NUMBER_TYPE:
            slice, elem = lhs, rhs
        else:
            slice, elem = rhs, lhs

        @lazylist_from(elem)
        def gen():
            working = []
            for item in iterable(elem, ctx=ctx):
                working.append(item)
                if len(working) == slice:
                    if vy_type(elem) == str:
                        yield "".join(working)
                    else:
                        yield working
                    working = []
            if working:
                if vy_type(elem) == str:
                    yield "".join(working)
                else:
                    yield working

        return gen()

    elif ts == (str, str):
        parts = lhs.partition(rhs)[::2]
        print(parts)
        if parts[1] == "" and not lhs.endswith(rhs):
            return [parts[0]]
        return list(parts)

    else:
        if ts[1] == LazyList or ts[1] == list:
            elem, wraps = lhs, rhs
        else:
            elem, wraps = rhs, lhs

        @lazylist_from(elem)
        def gen():
            working = []
            lengths = wraps
            for item in iterable(elem, ctx=ctx):
                # Handle multiple zeroes in sequence
                while lengths[0] == 0:
                    yield []
                    lengths = lengths[1:] + [lengths[0]]
                working.append(item)
                if len(working) == lengths[0]:
                    if vy_type(elem) == str:
                        yield "".join(working)
                    else:
                        yield working
                    working = []
                    lengths = lengths[1:] + [lengths[0]]
            if working:
                if vy_type(elem) == str:
                    yield "".join(working)
                else:
                    yield working

        return gen()


def zero_length_range(lhs, ctx):
    """Element ẏ
    (any) -> range(0, len(a)) (exlcusive range from 0 to length of a)
    """

    @lazylist_from(lhs)
    def gen():
        count = sympy.nsimplify(0)
        for _ in iterable(lhs, ctx=ctx):
            yield count
            count += 1

    return gen()


def zero_matrix(lhs, ctx):
    """Element Þm
    Return a matrix with dimensions each item of a, where the first is the
    innermost and the last is the outermost
    """
    mat = []
    temp = 0
    for ind in iterable(lhs, ctx=ctx):
        mat = []
        for _ in range(ind):
            mat.append(temp)
        temp = deep_copy(mat)

    return mat


def zero_slice(lhs, rhs, ctx):
    """Element Ẏ
    (any, num) -> a[0:b]
    (num, any) -> b[0:a]
    (str, str) -> regex.findall(pattern=a,string=b) (Find all matches for a regex)
    """
    if type(lhs) == types.FunctionType:
        return zero_slice(rhs, lhs, ctx)
    if type(rhs) == types.FunctionType:

        @lazylist
        def f(l, fun):
            for item in l:
                if not safe_apply(fun, item, ctx=ctx):
                    return
                yield item

        return f(iterable(lhs, ctx=ctx), rhs)
    ts = vy_type(lhs, rhs)
    return {
        (ts[0], NUMBER_TYPE): lambda: index(
            iterable(lhs, ctx=ctx), [0, rhs], ctx=ctx
        ),
        (NUMBER_TYPE, ts[1]): lambda: index(
            iterable(rhs, ctx=ctx), [0, lhs], ctx=ctx
        ),
        (NUMBER_TYPE, NUMBER_TYPE): lambda: vy_floor(
            index(str(lhs), [0, rhs], ctx=ctx), ctx
        ),
        (str, str): lambda: re.findall(rhs, lhs),
    }.get(ts, lambda: vectorise(zero_slice, lhs, rhs, ctx=ctx))()


def zfiller(lhs, rhs, ctx):
    """Element ∆Z
    zfill to rhs
    """
    ts = vy_type(lhs, rhs)
    return {
        (NUMBER_TYPE, str): lambda: rhs.zfill(lhs),
        (str, NUMBER_TYPE): lambda: lhs.zfill(rhs),
        (NUMBER_TYPE, list): lambda: [0 for i in range(max(0, lhs - len(rhs)))]
        + rhs,
        (list, NUMBER_TYPE): lambda: [0 for i in range(max(0, rhs - len(lhs)))]
        + lhs,
        (str, str): lambda: lhs.zfill(len(rhs)),
    }.get(ts, lambda: vectorise(zfiller, lhs, rhs, ctx=ctx))()


elements: dict[str, tuple[str, int]] = {
    "¬": process_element("sympy.nsimplify(int(not lhs))", 1),
    "∧": process_element("rhs and lhs", 2),
    "∨": process_element("rhs or lhs", 2),
    "⟇": process_element(remove_at_index, 2),
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
    "!": process_element("len(stack)", -1),
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
        "ctx.use_top_input = True; lhs = get_input(ctx, explicit=True); "
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
    "H": process_element(vy_hex, 1),
    "I": process_element(into_two, 1),
    "J": process_element(merge, 2),
    "K": process_element(divisors_or_prefixes, 1),
    "L": process_element(length, 1),
    "M": process_element(vy_map_or_pair_each, 2),
    "N": process_element(negate, 1),
    "O": process_element(count_item, 2),
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
    "T": process_element(truthy_indices, 1),
    "U": process_element(uniquify, 1),
    "V": process_element(replace, 3),
    "W": (
        "temp = list(deep_copy(stack))\n"
        "pop(stack, len(stack), ctx)\n"
        "stack.append(temp)",
        -1,
    ),
    # X doesn't need to be implemented here, because it's already a structure
    "Y": process_element(interleave, 2),
    "Z": process_element(vy_zip, 2),
    "^": ("stack += wrapify(stack, len(stack), ctx)", -1),
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
    "x": process_element("", 2),
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
        "top = iterable(pop(stack, 1, ctx), ctx=ctx);"
        " stack.append(head(top, ctx));"
        " stack.append(top[1:])",
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
        "top = iterable(pop(stack, 1, ctx), ctx=ctx);"
        " stack.append(index(top, [None, -1], ctx));"
        " stack.append(tail(top, ctx))",
        1,
    ),
    "ẇ": process_element(wrap, 2),
    "ẋ": process_element(repeat, 2),
    "ẏ": process_element(zero_length_range, 1),
    "ż": process_element(one_length_range, 1),
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
    "Ė": (
        "stack += vy_exec(pop(stack, 1, ctx), ctx)",
        1,
    ),
    "Ḟ": process_element(gen_from_fn, 2),
    "Ġ": process_element(group_consecutive, 1),
    "Ḣ": process_element(head_remove, 1),
    "İ": process_element(index_indices_or_cycle, 2),
    "Ŀ": process_element(transliterate, 3),
    "Ṁ": process_element(insert_or_map_nth, 3),
    "Ṅ": process_element(integer_parts_or_join_spaces, 1),
    "Ȯ": (
        "if len(stack) > 1: stack.append(index(stack, -2, ctx))\n"
        "else: stack.append(get_input(ctx))",
        0,
    ),
    "Ṗ": process_element(permutations, 1),
    "Ṙ": process_element(reverse, 1),
    "Ṡ": process_element(vectorised_sum, 1),
    "Ṫ": (
        "top = pop(stack, 1, ctx)\n"
        "if vy_type(top) == NUMBER_TYPE:\n"
        "    stack.append(1)\n"
        "    stack.append(top)\n"
        "else:\n"
        "    stack.append(tail_remove(top, ctx))",
        1,
    ),
    "Ẇ": process_element(split_keep, 2),
    "Ẋ": process_element(cartesian_product, 2),
    "Ẏ": process_element(zero_slice, 2),
    "Ż": process_element(one_slice, 2),
    "⁰": process_element("ctx.inputs[0][0][-1]", 0),
    "¹": process_element("ctx.inputs[0][0][-2]", 0),
    "²": process_element(square, 1),
    "∇": (
        "third, second, first = pop(stack, 3, ctx); "
        "stack.append(third); stack.append(first); "
        "stack.append(second)",
        3,
    ),
    "⌈": process_element(vy_ceil, 1),
    "⌊": process_element(vy_floor, 1),
    "¯": process_element(deltas, 1),
    "±": process_element(sign_of, 1),
    "₴": ("top = pop(stack, 1, ctx); vy_print(top, end='', ctx=ctx)", 1),
    "…": (
        "top = pop(stack, 1, ctx); "
        "vy_print(top, end='\\n', ctx=ctx); stack.append(top)",
        1,
    ),
    "□": (
        "if ctx.inputs[0]: stack.append(ctx.inputs[0][0])\n"
        "else:\n"
        "    input_list, temp = [], input()\n"
        "    while temp:\n"
        "        input_list.append(vy_eval(temp))\n"
        "        temp = input()",
        0,
    ),
    "↳": process_element(right_bit_shift, 2),
    "↲": process_element(left_bit_shift, 2),
    "⋏": process_element(bitwise_and, 2),
    "⋎": process_element(bitwise_or, 2),
    "꘍": process_element(bitwise_xor, 2),
    "ꜝ": process_element(bitwise_not, 1),
    "℅": process_element(random_choice, 1),
    "≤": process_element(less_than_or_equal, 2),
    "≥": process_element(greater_than_or_equal, 2),
    "≠": process_element(not_equals, 2),
    "⁼": process_element(non_vectorising_equals, 2),
    "∪": process_element(union, 2),
    "∩": process_element(transpose, 1),
    "⊍": process_element(symmetric_difference, 2),
    "£": ("ctx.register = pop(stack, 1, ctx)", 1),
    "¥": process_element("ctx.register", 0),
    "⇧": process_element(grade_up, 1),
    "⇩": process_element(grade_down, 1),
    "Ǎ": process_element(remove_non_alphabets, 1),
    "ǎ": process_element(substrings, 1),
    "Ǐ": process_element(prime_factorisation, 1),
    "ǐ": process_element(prime_factors, 1),
    "Ǒ": process_element(multiplicity, 2),
    "ǒ": process_element(modulo_3, 1),
    "Ǔ": (
        "rhs = pop(stack, 1, ctx)\n"
        + "if vy_type(rhs) == NUMBER_TYPE: \n"
        + "    lhs = pop(stack, 1, ctx)\n"
        + "    stack.append(rotate_left(lhs, rhs, ctx))\n"
        + "else:\n"
        + "    stack.append(rotate_left(rhs, 1, ctx))\n",
        2,
    ),
    "ǔ": (
        "rhs = pop(stack, 1, ctx)\n"
        + "if vy_type(rhs) == NUMBER_TYPE: \n"
        + "    lhs = pop(stack, 1, ctx)\n"
        + "    stack.append(rotate_right(lhs, rhs, ctx))\n"
        + "else:\n"
        + "    stack.append(rotate_right(rhs, 1, ctx))\n",
        2,
    ),
    "↵": process_element(newline_split, 1),
    "¼": process_element("ctx.global_array.pop()", 0),
    "⅛": ("lhs = pop(stack,1,ctx); ctx.global_array.append(lhs)", 1),
    "¾": process_element("list(deep_copy(ctx.global_array))", 0),
    "Π": process_element(product, 1),
    "„": (
        "temp = wrapify(stack, len(stack), ctx)[::-1]; "
        "stack += temp[1:] + [temp[0]]",
        -1,
    ),
    "‟": (
        "temp = wrapify(stack, len(stack), ctx)[::-1]; "
        "stack += [temp[-1]] + temp[:-1]",
        -1,
    ),
    "ඞ": process_element('"sus"', 0),
    "🍪": process_element(cookie, 0),
    "∆²": process_element(is_square, 1),
    "∆c": process_element(cosine, 1),
    "∆C": process_element(arccos, 1),
    "∆s": process_element(sine, 1),
    "∆S": process_element(arcsin, 1),
    "∆t": process_element(tangent, 1),
    "∆T": process_element(arctan, 1),
    "∆q": process_element(quadratic_solver, 2),
    "∆Q": process_element(general_quadratic_solver, 2),
    "∆P": process_element(polynomial_roots, 1),
    "∆ƈ": process_element(n_pick_r, 2),
    "∆i": process_element(nth_pi, 1),
    "∆b": process_element(binary_string, 1),
    "∆ė": process_element(nth_e, 1),
    "∆I": process_element("pi_digits(lhs)", 1),
    "∆Ė": process_element(e_digits, 1),
    "∆f": process_element(nth_fibonacci, 1),
    "∆F": process_element(nth_fibonacci_0, 1),
    "∆±": process_element(copy_sign, 2),
    "∆%": process_element(mod_pow, 3),
    "∆K": process_element(divisor_sum, 1),
    "∆e": process_element(expe, 1),
    "∆E": process_element(expe_minus_1, 1),
    "∆L": process_element(natural_log, 1),
    "∆l": process_element(log_2, 1),
    "∆τ": process_element(log_10, 1),
    "∆d": process_element(euclidean_distance, 2),
    "∆D": process_element(to_degrees, 1),
    "∆R": process_element(to_radians, 1),
    "∆Ṗ": process_element(next_prime, 1),
    "∆ṗ": process_element(prev_prime, 1),
    "∆p": process_element(nearest_prime, 1),
    "∆ṙ": process_element(polynomial_from_roots, 1),
    "∆W": process_element(round_to, 2),
    "∆Ŀ": (
        "top = pop(stack, 1, ctx)\n"
        "if vy_type(top, simple=True) is list:\n"
        "    stack.append(lowest_common_multiple(top, ctx=ctx))\n"
        "else:\n"
        "    stack.append(lowest_common_multiple(pop(stack, 1, ctx), top, ctx))\n",
        2,
    ),
    "∆Ṙ": process_element("sympy.nsimplify(random.random(), rational=True)", 0),
    "∆Z": process_element(zfiller, 2),
    "∆ċ": process_element(nth_cardinal, 1),
    "∆o": process_element(nth_ordinal, 1),
    "∆M": process_element(mode, 1),
    "∆ṁ": process_element(median, 1),
    "∆ṫ": process_element(totient, 1),
    "∆Ċ": process_element(polynomial_expr_from_coeffs, 1),
    "∆¢": process_element(carmichael_function, 1),
    "∆›": process_element(increment_until_false, 2),
    "∆‹": process_element(decrement_until_false, 2),
    "∆ǐ": process_element(prime_exponents, 1),
    "∆*": process_element(next_multiple, 2),
    "∆n": process_element(next_power, 2),
    "∆ḟ": process_element(prev_power, 2),
    "øḂ": process_element(angle_bracketify, 1),
    "øḃ": process_element(curly_bracketify, 1),
    "øb": process_element(parenthesise, 1),
    "øB": process_element(bracketify, 1),
    "øβ": process_element(brackets_balanced, 1),
    "øc": process_element(base_255_string_compress, 1),
    "øC": process_element(base_255_number_compress, 1),
    "øĊ": process_element(center, 1),
    "øċ": process_element(optimal_number_compress, 1),
    "ød": process_element(run_length_decoding, 1),
    "øD": process_element(optimal_compress, 1),
    "øḋ": process_element(to_decimal, 1),
    "øḊ": process_element(dyadic_runl_decode, 2),
    "øe": process_element(run_length_encoding, 1),
    "øĖ": process_element(separate_runl_encode, 1),
    "ø↲": process_element(custom_pad_left, 3),
    "ø↳": process_element(custom_pad_right, 3),
    "øM": process_element(flip_brackets_vertical_palindromise, 1),
    "øṁ": process_element(vertical_mirror, 1),
    "øṗ": process_element(vertical_palindromise_center_join, 1),
    "øm": process_element(vertical_mirror_center_join, 1),
    "øṀ": process_element(flip_brackets_vertical_mirror, 1),
    "øW": process_element(group_on_words, 1),
    "øA": process_element(letter_to_number, 1),
    "øP": process_element(pluralise_count, 2),
    "øp": process_element(starts_with, 2),
    "øE": process_element(ends_with, 2),
    "øs": process_element(starts_with_set, 2),
    "øf": process_element(ends_with_set, 2),
    "øṖ": process_element(all_partitions, 1),
    "øo": process_element(remove_until_no_change, 2),
    "øO": process_element(count_overlapping, 2),
    "øV": process_element(replace_until_no_change, 3),
    "øF": process_element(factorial_of_range, 1),
    "øṙ": process_element(regex_sub, 3),
    "øJ": process_element(json_parse, 1),
    "øṄ": process_element(replace_nth_occurrence, 4),
    "øṘ": process_element(roman_numeral, 1),
    "ø⟇": process_element(codepage_digraph, 1),
    "øḞ": process_element(replace_first, 3),
    "øS": process_element(strip_whitespace, 1),
    "øL": process_element(strip_whitespace_left, 1),
    "øR": process_element(strip_whitespace_right, 1),
    "øl": process_element(strip_left, 2),
    "ør": process_element(strip_right, 2),
    "ø^": process_element(canvas_draw, 3),
    "ø∧": (
        "other, rhs, lhs = pop(stack, 3, ctx)\n"
        "canvas_global_draw(lhs, rhs, other, ctx)\n",
    ),
    "øε": process_element(vertical_join_with_filler, 2),
    "ø.": process_element(surround, 2),
    "øŀ": process_element(align_left, 1),
    "øɽ": process_element(align_right, 1),
    "Þ*": process_element(cartesian_over_list, 1),
    "Þa": process_element(adjacency_matrix_dir, 1),
    "ÞA": process_element(adjacency_matrix_undir, 1),
    "Þo": process_element(infinite_ordinals, 0),
    "Þc": process_element(infinite_cardinals, 0),
    "Þp": process_element(infinite_primes, 0),
    "Þx": process_element(all_combos, 1),
    "Þ×": process_element(all_combos_with_replacement, 1),
    "Þu": process_element(all_unique, 1),
    "Þj": process_element(depth, 1),
    "ÞẊ": process_element(cartesian_power, 2),
    "ÞB": process_element(rand_bits, 1),
    "ÞU": process_element(uniquify_mask, 1),
    "Þ÷": process_element(divide_lists, 2),
    "Þf": (
        "rhs = pop(stack, 1, ctx)\n"
        "if vy_type(rhs) != NUMBER_TYPE:\n"
        "    stack.append(flatten_by(rhs, 1, ctx))\n"
        "else:\n"
        "    stack.append(flatten_by(pop(stack, 1, ctx), rhs, ctx))\n",
        2,
    ),
    "Þċ": process_element(cycle, 1),
    "Þǔ": process_element(untruth, 1),
    "Þi": process_element(multi_dimensional_index, 2),
    "ÞI": process_element(all_indices_multidim, 2),
    "Þḟ": process_element(multi_dimensional_search, 2),
    "ÞḞ": process_element(fill, 2),
    "Þm": process_element(zero_matrix, 1),
    "Þ…": process_element(evenly_distribute, 2),
    "Þ<": process_element(all_less_than_increasing, 2),
    "ÞD": process_element(all_diagonals, 1),
    "Þḋ": process_element(all_antidiagonals, 1),
    "ÞS": process_element(sublists, 1),
    "ÞṪ": process_element(transpose, 2),
    "ÞṀ": process_element(matrix_multiply, 2),
    "Þ•": process_element(dot_product, 2),
    "ÞḊ": process_element(matrix_determinant, 1),
    "Þ\\": process_element(anti_diagonal, 1),
    "Þ/": process_element(diagonal, 1),
    "Þ↓": process_element(min_by_function, 2),
    "Þ↑": process_element(max_by_function, 2),
    "ÞZ": process_element(coords_deepmap, 2),
    "ÞF": process_element(fibonaacis, 0),
    "ÞṄ": process_element(infinite_integer_partitions, 0),
    "Þ!": process_element(factorials, 0),
    "Þ℅": process_element(shuffle, 1),
    "ÞC": process_element(foldl_columns, 2),
    "Þṁ": process_element(mold_special, 2),
    "ÞM": process_element(maximal_indices, 1),
    "Þ∞": process_element(infinite_positives, 0),
    "Þn": process_element(infinite_all_integers, 0),
    "Þ∴": process_element(element_wise_dyadic_maximum, 2),
    "Þ∵": process_element(element_wise_dyadic_minimum, 2),
    "Þs": process_element(all_slices, 2),
    "ÞǓ": process_element(connected_uniquify, 1),
    "Þ¾": ("ctx.global_array = []", 0),
    "Þr": process_element(sans_last_prepend_zero, 1),
    "ÞR": process_element(cumul_sum_sans_last_prepend_zero, 1),
    "Þẇ": (
        "res = unwrap(pop(stack, 1, ctx), ctx); "
        "stack.append(res[0]); stack.append(res[1])",
        1,
    ),
    "Þż": process_element(lift, 1),
    "Þg": process_element(shortest, 1),
    "ÞG": process_element(longest, 1),
    "Þṡ": process_element(sort_by_length, 1),
    "ÞṠ": process_element(is_sorted_ascending, 1),
    "ÞṘ": process_element(is_sorted_descending, 1),
    "Þ⇧": process_element(is_sorted_strictly_ascending, 1),
    "Þ⇩": process_element(is_sorted_strictly_descending, 1),
    "ÞȮ": process_element(is_ordered, 1),
    "ÞĊ": process_element(is_unordered, 1),
    "ÞK": process_element(suffixes_element, 1),
    "ÞT": process_element(multidimensional_truthy_indices, 1),
    "Þİ": (
        "rhs, lhs = pop(stack, 2, ctx)\n"
        "if vy_type(rhs) != NUMBER_TYPE:\n"
        "    lhs, rhs = rhs, lhs\n"
        "stack.append(index(lhs, [0, rhs], ctx))\n"
        "stack.append(index(lhs, [rhs, None], ctx))\n",
        2,
    ),
    "ÞN": process_element(alternating_negations, 1),
    "Þ□": process_element(identity_matrix, 1),
    "Þe": process_element(matrix_exponentiation, 2),
    "Þd": process_element(dist_matrix_dir, 1),
    "Þw": process_element(dist_matrix_undir, 1),
    "Þ∨": process_element(multiset_difference, 2),
    "Þ∩": process_element(multiset_intersection, 2),
    "Þ∪": process_element(multiset_union, 2),
    "Þ⊍": process_element(multiset_symmetric_difference, 2),
    "ÞŻ": process_element(sort_every_level, 1),
    "ÞṖ": process_element(index_partition, 2),
    "Þṗ": process_element(boolean_partition, 2),
    "¨□": process_element(parse_direction_arrow_to_integer, 1),
    "¨^": process_element(parse_direction_arrow_to_vector, 1),
    "¨,": ("top = pop(stack, 1, ctx); vy_print(top, end=' ', ctx=ctx)", 1),
    "¨…": (
        "top = pop(stack, 1, ctx); vy_print(top, end=' ', ctx=ctx); "
        "stack.append(top)",
        1,
    ),
    "¨M": process_element(apply_at, 3),
    "¨U": ("if ctx.online: stack.append(request(pop(stack, 1, ctx), ctx))", 1),
    "¨>": process_element(strict_greater_than, 2),
    "¨<": process_element(strict_less_than, 2),
    "¨ẇ": ("stack.append(wrapify(stack, pop(stack, 1, ctx), ctx)[::-1])", 1),
    "¨*": process_element(all_multiples, 1),
    "¨?": (
        'stack.append(vy_eval(input("> " * ctx.repl_mode), ctx))',
        0,
    ),
    "¨S": (
        "a = [list(stack.pop()), 0]; ctx.inputs.insert(0, a); ctx.inputs.append(a)",
        1,
    ),
    "¨R": ("ctx.inputs.pop(0); ctx.inputs.pop()", 0),
    "kA": process_element('"ABCDEFGHIJKLMNOPQRSTUVWXYZ"', 0),
    "ke": process_element("sympy.E", 0),
    "kf": process_element('"Fizz"', 0),
    "kb": process_element('"Buzz"', 0),
    "kF": process_element('"FizzBuzz"', 0),
    "kH": process_element('"Hello, World!"', 0),
    "kh": process_element('"Hello World"', 0),
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
    "kp": process_element("string.punctuation", 0),
    "kP": process_element(
        '"!\\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"',
        0,
    ),
    "kQ": process_element(
        '" !\\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"',
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
    "ki": process_element("sympy.pi", 0),
    "kn": process_element("math.nan", 0),
    "kg": process_element(
        "sympy.nsimplify('1/2 + sqrt(5)/2', rational=True)", 0
    ),
    "kD": process_element('datetime.now().strftime("%Y-%m-%d")', 0),
    "kN": process_element(
        "LazyList([(t:=datetime.now()).hour, t.minute, t.second])", 0
    ),
    "kḋ": process_element('datetime.now().strftime("%d/%m/%Y")', 0),
    "kḊ": process_element('datetime.now().strftime("%m/%d/%Y")', 0),
    "kð": process_element(
        "LazyList([(d:=datetime.now()).day, d.month, d.year])", 0
    ),
    "kβ": process_element('"{}[]<>()"', 0),
    "kḂ": process_element('"()[]{}"', 0),
    "kḃ": process_element("'([{'", 0),
    "kß": process_element('"()[]"', 0),
    "k≤": process_element('"([{<"', 0),
    "k≥": process_element('")]}"', 0),
    "kΠ": process_element('")]}>"', 0),
    "kv": process_element('"aeiou"', 0),
    "kV": process_element('"AEIOU"', 0),
    "k∨": process_element('"aeiouAEIOU"', 0),
    "k⟇": process_element("vyxal.encoding.codepage", 0),
    "k½": process_element("LazyList([1,2])", 0),
    "kḭ": process_element("2 ** 32", 0),
    "k₁": process_element("LazyList([1, 1])", 0),
    "k+": process_element("LazyList([1, -1])", 0),
    "k-": process_element("LazyList([-1, 1])", 0),
    "k≈": process_element("LazyList([0, 1])", 0),
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
    "kṗ": process_element('LazyList(["()","[]","{}","<>"])', 0),
    "kṖ": process_element('"([{<>}])"', 0),
    "kS": process_element('"ඞ"', 0),
    "k₂": process_element("2 ** 20", 0),
    "k₃": process_element("2 ** 30", 0),
    "k∪": process_element('"aeiouy"', 0),
    "k⊍": process_element('"AEIOUY"', 0),
    "k∩": process_element('"aeiouyAEIOUY"', 0),
    "k□": process_element("[[0,1],[1,0],[0,-1],[-1,0]]", 0),
    "kṘ": process_element('"IVXLCDM"', 0),
    "k•": process_element('["qwertyuiop","asdfghjkl","zxcvbnm"]', 0),
}
modifiers: dict[str, str] = {
    "&": (
        "stack.append(ctx.register)\n"
        "ctx.register = safe_apply(function_A, "
        "pop(stack, function_A.arity, ctx), ctx=ctx)\n"
    ),
    "v": (
        "arguments = wrapify(stack, function_A.arity if function_A.arity != 0 else 1, ctx=ctx)\n"
        "res = vectorise(function_A, *(arguments[::-1]), explicit=True, ctx=ctx)\n"
        "if eager: res = list(res)\n"
        "stack.append(res)"
    ),
    "¨v": (
        "arguments = wrapify(stack, function_A.arity if function_A.arity != 0 else 1, ctx=ctx)\n"
        "stack.append"
        "(vectorise(function_A, *(arguments[::-1]), ctx=ctx))"
        "\n"
    ),
    "~": (
        "if function_A.arity >= 2:\n"
        "    ctx.retain_popped = True\n"
        "    arguments = wrapify(stack, function_A.arity, ctx=ctx)\n"
        "    ctx.retain_popped = False\n"
        "    stack.append(safe_apply(function_A, *(arguments[::-1]), "
        "ctx=ctx))\n"
        "elif function_A.arity == -1:\n"
        "    arguments = list(deep_copy(stack))\n"
        "    pop(stack, len(stack), ctx)\n"
        "    stack += wrapify(safe_apply(function_A, arguments, ctx=ctx, arity_override=-1),"
        " ctx=ctx)\n"
        "elif function_A.arity == 1:\n"
        "    stack.append(vy_filter(pop(stack, 1, ctx=ctx), function_A,"
        "ctx=ctx))\n"
    ),
    "₌": (
        "stack_copy = list(deep_copy(stack))\n"
        "arguments_A = wrapify(stack_copy, max(function_A.arity, "
        "len(stack_copy)), ctx=ctx)\n"
        "arguments_B = wrapify(stack, max(function_B.arity, len(stack)), "
        "ctx=ctx)\n"
        "stack.append(safe_apply(function_A, *(arguments_A[::-1]), ctx=ctx))\n"
        "stack.append(safe_apply(function_B, *(arguments_B[::-1]), ctx=ctx))\n"
    ),
    "₍": (
        "stack_copy = list(deep_copy(stack))\n"
        "arguments_A = wrapify(stack_copy, max(function_A.arity, "
        "len(stack_copy)), ctx=ctx)\n"
        "arguments_B = wrapify(stack, max(function_B.arity, len(stack)), "
        "ctx=ctx)\n"
        "res_A = safe_apply(function_A, *(arguments_A[::-1]), ctx=ctx)\n"
        "res_B = safe_apply(function_B, *(arguments_B[::-1]), ctx=ctx)\n"
        "stack.append([res_A, res_B])\n"
    ),
    "ƒ": (
        "function_A.stored_arity = 2\n"
        "stack.append(vy_reduce(function_A, pop(stack, 1, ctx), ctx))"
    ),
    "ɖ": (
        "function_A.stored_arity = 2\n"
        "stack.append(scanl(function_A, pop(stack, 1, ctx), ctx))"
    ),
    # This currently doesn't work, and for now has been replaced with a
    # hacky workaround in the parser - see line 271.
    # Uncomment this when (if ever) we can make it work
    # "ß": (
    #    "if boolify(pop(stack, 1, ctx), ctx):\n"
    #    "    stack.append(safe_apply(function_A, *stack, ctx=ctx))\n"
    # ),
    # "¨i": (
    #     "if boolify(pop(stack, 1, ctx), ctx):\n"
    #     "    stack.append(safe_apply(function_A, *stack, ctx=ctx))\n"
    #     "else:\n"
    #     "    stack.append(safe_apply(function_B, *stack, ctx=ctx))\n"
    # ),
    "¨=": (
        "original = pop(stack, 1, ctx)\n"
        "res = safe_apply(function_A, deep_copy(original), ctx=ctx)\n"
        "stack.append(non_vectorising_equals(original, res, ctx=ctx))"
    ),
    "¨£": (
        "rhs, lhs = pop(stack, 2, ctx)\n"
        "zipped = vy_zip(lhs, rhs, ctx)\n"
        "mapped = map(lambda item, function_A=function_A, ctx=ctx: vy_reduce(function_A, item, ctx), zipped)\n"
        "stack.append(LazyList(mapped))\n"
    ),
    "¨p": (
        "lhs = pop(stack, 1, ctx)\n"
        "over = overlapping_groups(lhs, 2, ctx)\n"
        "mapped = map(lambda item, function_A=function_A, ctx=ctx: vy_reduce(function_A, item, ctx), over)\n"
        "stack.append(LazyList(mapped))\n"
    ),
}
