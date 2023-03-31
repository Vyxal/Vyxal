"""This is where the cool functions go that help out stuff.

They aren't directly attached to an element. Consequently, you need to
use type annotations here.
"""

import ast
import collections
import inspect
import itertools
import math  # lgtm [py/unused-import]
import re
import string
import textwrap
import types
import unicodedata
from typing import Any, Iterable, List, Optional, Union

import sympy
from sympy.parsing.sympy_parser import (
    convert_xor,
    implicit_multiplication_application,
    standard_transformations,
)

import vyxal.dictionary
import vyxal.encoding
from vyxal import lexer, parse
from vyxal.context import DEFAULT_CTX, Context
from vyxal.LazyList import *

NUMBER_TYPE = "number"
SCALAR_TYPE = "scalar"
VyList = Union[list, LazyList]
VyIterable = Union[str, VyList]


def case_of(value: str) -> int:
    """Returns 1 for all uppercase, 0 for all lowercase, and -1 for
    mixed case."""
    if all(map(lambda x: x.isupper(), value)):
        return 1
    elif all(map(lambda x: x.islower(), value)):
        return 0
    return -1


def chop(it: VyIterable, n: int) -> LazyList:
    """Chop `it` into `n` chunks."""

    @lazylist_from(it)
    def gen():
        nonlocal it
        chunk_len = len(it) // n
        left_over = len(it) % n
        for i in range(n):
            length = chunk_len + (1 if left_over > i else 0)
            yield it[:length]
            it = it[length:]

    return gen()


def chunk_while(it: VyList, fun: types.FunctionType, ctx: Context) -> VyList:
    """Chunk a list while a function returns True."""
    it = iterable(it, ctx=ctx)
    indexable = LazyList(it)

    @lazylist_from(it)
    def gen():
        ind = 0
        while indexable.has_ind(ind + fun.arity):
            chunk = [indexable[ind]]
            while indexable.has_ind(ind + fun.arity) and safe_apply(
                fun, *indexable[ind : ind + fun.arity], ctx=ctx
            ):
                chunk.append(indexable[ind + 1])
                ind += 1
            yield chunk
            ind += 1
        yield indexable[ind:]

    return list(gen())


@lazylist
def collect_until_false(
    predicate: types.FunctionType,
    function: types.FunctionType,
    initial: Any,
    ctx: Context,
) -> List[Any]:
    """Given a function, apply it on a given value while a predicate function
    returns True. Return the list of values that were collected."""
    val = initial
    while safe_apply(predicate, val, ctx=ctx):
        yield val
        val = safe_apply(function, val, ctx=ctx)


def combinations(lst: VyList, size: int) -> VyList:
    """Combinations without replacement"""
    if size == 0:
        return [[]]
    elif isinstance(lst, list):
        return vyxalify(itertools.combinations(lst, size))

    # Infinite LazyLists won't work with itertools.combinations
    @lazylist_from(lst)
    def gen():
        prev_combs = [[]]
        for elem in lst:
            new_combs = []
            for comb in prev_combs:
                if len(comb) == size - 1:
                    yield comb + [elem]
                else:
                    new_combs.append(comb + [elem])
            prev_combs += new_combs

    return gen()


def concat(vec1: VyList, vec2: VyList, ctx: Context = DEFAULT_CTX) -> VyList:
    """Concatenate two lists/lazylists"""
    if LazyList not in (type(vec1), type(vec2)):
        return vec1 + vec2

    def gen():
        for item in vec1:
            yield item
        for item in vec2:
            yield item

    return LazyList(
        gen(),
        isinf=(
            (type(vec1) is LazyList and vec1.infinite)
            or (type(vec2) is LazyList and vec2.infinite)
        ),
    )


def deep_copy(value: Any) -> Any:
    """Because lists and lazylists use memory references. Frick them."""
    if type(value) not in (list, LazyList):
        return value  # because primitives are all like "ooh look at me
        # I don't have any fancy memory references because I'm an epic
        # chad unlike those virgin memory reference needing lists".

    return LazyList(itertools.tee(value)[-1], isinf=is_inf(value))


def dict_to_list(dictionary: dict) -> List[Any]:
    """Returns a dictionary as [[key, value]]"""
    return [[str(key), dictionary[key]] for key in dictionary]


def digits(num: NUMBER_TYPE) -> List[int]:
    """Get the digits of a (possibly Rational) number.
    This differs from to_base_digits because it works with floats.
    This does NOT include signs and decimal points"""
    return [int(let) if let not in "-./" else let for let in str(num)]


def drop_while(vec, fun, ctx):
    vec = iterable(vec, ctx=ctx)

    @lazylist_from(vec)
    def gen():
        t = True
        for item in vec:
            if not safe_apply(fun, item, ctx=ctx):
                t = False
            if not t:
                yield item

    return gen()


def enumerate_md(
    haystack: VyList, _index_stack: tuple = (), include_all=False
) -> VyList:
    """Enumerate multi-dimensional indices and items of a list.

    Parameters:
    include_str:
    Whether nested lists should be included as items too
    """

    @lazylist_from(haystack)
    def gen():
        for i, item in enumerate(haystack):
            if type(item) in (list, LazyList):
                if not item:
                    yield [LazyList(_index_stack) + [i], item]
                if include_all:
                    yield [LazyList(_index_stack) + [i], item]
                yield from enumerate_md(item, _index_stack + (i,), include_all)
            elif type(item) is str and len(item) > 1:
                if include_all:
                    yield [LazyList(_index_stack) + [i], item]
                yield from enumerate_md(
                    LazyList(item), _index_stack + (i,), include_all
                )
            else:
                yield (LazyList(_index_stack) + [i], item)

    return gen()


def first_where(
    function: types.FunctionType, vector: VyList, ctx: Context
) -> Any:
    """Returns the first element in vector where function returns True"""
    for item in vector:
        if safe_apply(function, item, ctx=ctx):
            return item

    return None


@lazylist
def fixed_point(
    function: types.FunctionType, initial: Any, ctx: Context
) -> List[Any]:
    """Repeat function until the result is no longer unique.
    Uses initial as the initial value"""
    previous = None
    current = initial

    while simplify(previous) != simplify(current):
        yield current
        previous = deep_copy(current)
        current = safe_apply(function, current, ctx=ctx)


def foldl(
    function: types.FunctionType,
    vector: List[Any],
    initial=None,
    *,
    ctx: Context,
) -> Any:
    """Reduce vector by function"""
    working = initial

    for item in vector:
        if working is None:
            working = item
        else:
            working = safe_apply(function, working, item, ctx=ctx)

    return working if working is not None else 0


def format_string(pattern: str, data: VyIterable) -> str:
    """Returns the pattern formatted with the given data. If the data is
    a string, then the string is reused if there is more than one % to
    be formatted. Otherwise (the data is a list), % are cyclically
    substituted"""
    ret = ""
    index = 0
    f_index = 0

    while index < len(pattern):
        if pattern[index] == "\\":
            ret += "\\" + pattern[index + 1]
            index += 1
        elif pattern[index] == "%":
            ret += str(data[f_index % len(data)])
            f_index += 1
        else:
            ret += pattern[index]
        index += 1
    return ret


def from_base_alphabet(value: str, alphabet: str) -> int:
    """Returns value in base 10 using base len(alphabet)
    [bijective base]"""
    ret = 0
    for digit in value:
        ret = len(alphabet) * ret + alphabet.find(digit)

    return ret


def from_base_digits(digit_list: List[NUMBER_TYPE], base: int) -> int:
    """Returns digits in base 10 using arbitrary base 'base'"""
    # I may have stolen this from Jelly
    ret = 0
    for digit in digit_list:
        ret = base * ret + digit

    return ret


def get_input(ctx: Context, explicit=False) -> Any:
    """Returns the next input depending on where ctx tells to get the
    input from."""
    if ctx.use_top_input:
        if ctx.array_inputs and not explicit:
            return vyxalify(ctx.inputs[0][0])
        if ctx.inputs[0][0]:
            ret = ctx.inputs[0][0][ctx.inputs[0][1] % len(ctx.inputs[0][0])]
            ctx.inputs[0][1] += 1
            return vyxalify(ret)
        else:
            try:
                temp = vy_eval(input("> " * ctx.repl_mode), ctx)
                if ctx.empty_input_is_zero and temp == "":
                    return 0
            except Exception:  # skipcq: PYL-W0703
                temp = 0
            return temp
    else:
        if len(ctx.inputs) == 1:
            ctx.use_top_input = True
            temp = get_input(ctx)
            ctx.use_top_input = False
            return vyxalify(temp)
        elif ctx.inputs[-1][0]:
            ret = ctx.inputs[-1][0][ctx.inputs[-1][1] % len(ctx.inputs[-1][0])]
            ctx.inputs[-1][1] += 1
            return vyxalify(ret)
        else:
            return 0


def edges_to_dir_graph(edges: list, ctx: Context) -> dict:
    """Convert a list of edges to a directed graph (as a dictionary)"""
    edges = [iterable(edge, ctx) for edge in iterable(edges, ctx)]
    graph = {}
    for edge in edges:
        if len(edge) != 2:
            raise ValueError(
                "Graph edge expected to be list of 2 elements,"
                f"got {edge} instead."
            )
        if edge[0] in graph:
            graph[edge[0]].append(edge[1])
        else:
            graph[edge[0]] = [edge[1]]
        if edge[1] not in graph:
            graph[edge[1]] = []

    vertices = graph.keys()
    if all(
        isinstance(vert, int) or (isinstance(vert, float) and int(vert) == vert)
        for vert in vertices
    ):
        # If we have just integers, assume the graph vertices are a range
        # and fill in the middle disconnected vertices
        # TODO make this behavior configurable with flags
        min_vert = min_by(vertices, ctx=ctx)
        max_vert = max_by(vertices, ctx=ctx)
        vertices = LazyList(range(int(min_vert), int(max_vert) + 1))
        for vert in vertices:
            if vert not in graph:
                graph[vert] = []

    return graph


def edges_to_undir_graph(edges: list, ctx: Context) -> dict:
    """Convert a list of edges representing an undirected graph to a dictionary"""
    edges = [iterable(edge, ctx) for edge in iterable(edges, ctx)]
    graph = {}
    for edge in edges:
        if len(edge) != 2:
            raise ValueError(
                "Graph edge expected to be list of 2 elements,"
                f"got {edge} instead."
            )
        if edge[0] not in graph:
            graph[edge[0]] = []
        graph[edge[0]].append(edge[1])

        if edge[1] not in graph:
            graph[edge[1]] = [edge[0]]
        elif edge[0] != edge[1]:
            graph[edge[1]].append(edge[0])

    vertices = graph.keys()
    if all(
        isinstance(vert, int)
        or ((isinstance(vert, float) or is_sympy(vert)) and int(vert) == vert)
        for vert in vertices
    ):
        # If we have just integers, assume the graph vertices are a range
        # and fill in the middle disconnected vertices
        # TODO make this behavior configurable with flags
        min_vert = min_by(vertices, ctx=ctx)
        max_vert = max_by(vertices, ctx=ctx)
        vertices = LazyList(range(int(min_vert), int(max_vert) + 1))
        for vert in vertices:
            if vert not in graph:
                graph[vert] = []

    return graph


def graph_distance(
    graph: dict, vert1, vert2, prev: list = []
) -> list[list[int]]:
    """Find the distance from vert1 to vert2 in a directed graph

    Parameters:
    graph: `dict[Vertex, list[Vertex]]`\\
    A dictionary where keys are vertices and values are lists of
    neighboring vertices
    prev: `list[Vertex]`\\
    A list of previously visited vertices, to avoid going in cycles"""
    if vert1 == vert2:
        return 0
    # I know this is the American spelling, but it's shorter than neighbour
    neighbors = [neighbor for neighbor in graph[vert1] if neighbor not in prev]
    if not neighbors:
        return float("inf")
    elif vert2 in neighbors:
        return 1
    else:
        new_prev = prev + [vert1]
        return 1 + min(
            graph_distance(graph, neighbor, vert2, new_prev)
            for neighbor in neighbors
        )


def group_by_function(
    lst: VyList, function: types.FunctionType, ctx: Context
) -> LazyList:
    """Group a list of elements by a function"""
    ret = {}
    for el in lst:
        key = safe_apply(function, el, ctx=ctx)
        if key in ret:
            ret[key].append(el)
        else:
            ret[key] = [el]
    return list(ret.values())


def group_by_function_ordered(
    lst: VyList, function: types.FunctionType, ctx: Context
) -> LazyList:
    """Group a list of elements by a function, but order is preserved"""
    ret = []
    is_lst = isinstance(lst, LazyList) or isinstance(lst, list)
    for el in lst:
        k = safe_apply(function, el, ctx=ctx)
        if ret == []:
            ret.append([k, [el] if is_lst else el])
        elif ret[-1][0] == k:
            if is_lst:
                ret[-1][1].append(el)
            else:
                ret[-1][1] += el
        else:
            ret.append([k, [el] if is_lst else el])
    return [x[1] for x in ret]


def has_ind(lst: VyList, ind: int) -> bool:
    """Whether or not the list is long enough for that index"""
    if isinstance(lst, LazyList):
        return lst.has_ind(ind)
    else:
        return 0 <= ind < len(lst)


def indent_str(string: str, indent: int, end="\n") -> str:
    """Indent a multiline string with 4 spaces, with a newline or `end` afterwards."""
    return textwrap.indent(string, "    " * indent) + end


def indent_code(*code, indent: int = 1) -> str:
    """Indent multiple lines (`*code`) by the given amount, then join on newlines."""
    return "\n".join(indent_str(line, indent, end="") for line in code) + "\n"


def invert_brackets(lhs: str) -> str:
    """
    Helper function to swap brackets and parentheses in a string
    """
    res = ""
    pairs = ["()", "[]", "{}", "<>", "/\\"]
    open_close = {x[0]: x[1] for x in pairs}
    close_open = {x[1]: x[0] for x in pairs}
    for char in lhs:
        if char in open_close:
            res += open_close[char]
        elif char in close_open:
            res += close_open[char]
        else:
            res += char
    return res


def is_inf(lst: VyList) -> bool:
    """Whether or not a list/LazyList is infinite"""
    return isinstance(lst, LazyList) and lst.infinite


def is_sympy(value):
    """Whether or not this is a Sympy type"""
    return isinstance(value, sympy.Basic)


def iterable(
    item: Any, number_type: Any = None, ctx: Context = DEFAULT_CTX
) -> Union[LazyList, Union[list, str]]:
    """Turn a value into an iterable"""
    item_type = type(item)
    if item_type is int or is_sympy(item):
        if ctx.number_as_range or number_type is range:
            return LazyList(range(ctx.range_start, int(item) + ctx.range_end))
        else:
            return digits(item)
    else:
        return item


def join_with(lhs, rhs):
    """A generator to concatenate two iterables together"""
    for item in lhs:
        yield vyxalify(item)

    for item in rhs:
        yield vyxalify(item)


def levenshtein_distance(s1: str, s2: str) -> int:
    """Returns the levenshtein distance between two strings"""
    # https://stackoverflow.com/a/32558749
    if len(s1) > len(s2):
        s1, s2 = s2, s1

    distances: Iterable[int] = range(len(s1) + 1)
    for i2, c2 in enumerate(s2):
        distances_ = [i2 + 1]
        for i1, c1 in enumerate(s1):
            if c1 == c2:
                distances_.append(distances[i1])
            else:
                distances_.append(
                    1 + min((distances[i1], distances[i1 + 1], distances_[-1]))
                )
        distances = distances_
    return distances[-1]


def local_minima(lhs: str) -> List[Union[int, float]]:
    """Find the local minima of a mathematical function using Sympy"""
    x = sympy.symbols("x")
    d_dx = sympy.diff(make_expression(lhs), x)
    second_dx = sympy.diff(d_dx, x)
    zeros = sympy.solve(d_dx, x)

    return LazyList(z for z in zeros if second_dx.subs(x, z) > 0)


def local_maxima(lhs: str) -> List[Union[int, float]]:
    """Find the local minima of a mathematical function using Sympy"""
    x = sympy.symbols("x")
    d_dx = sympy.diff(make_expression(lhs), x)
    second_dx = sympy.diff(d_dx, x)
    zeros = sympy.solve(d_dx, x)

    return LazyList(z for z in zeros if second_dx.subs(x, z) < 0)


def longest_suffix(a: VyIterable, b: VyIterable) -> VyIterable:
    """Find the longest suffix of a pair of strings or lists.
    If bothare strings, the result is a string."""
    i = 1
    while i <= len(a) and i <= len(b):
        if a[-i] == b[-i]:
            i += 1
        else:
            break
    i -= 1
    if i == 0:
        return "" if isinstance(a, str) and isinstance(b, str) else []
    else:
        return b[-i:] if isinstance(a, str) else a[-i:]


def keep(haystack: Any, needle: Any) -> Any:
    """Used for keeping only needle in haystack"""
    if isinstance(haystack, str):
        return "".join(char for char in haystack if char in needle)
    else:
        return LazyList(item for item in haystack if item in needle)


def make_equation(eqn: str) -> sympy:
    """Returns a sympy equation from a string"""
    eqn = eqn.split("=")
    return sympy.Eq(make_expression(eqn[0]), make_expression(eqn[1]))


def make_expression(expr: str) -> sympy:
    """Turns a string into a nice sympy expression"""

    # Normalize the string according to how python normalizes it first
    expr = unicodedata.normalize("NFKC", expr)

    # Remove all problematic characters from expr
    # "\\\"'{}_`" is the set of characters that are problematic

    expr = "".join(char for char in expr if char not in "\\\"'{}[]_`")

    # Keep only "."s that have numbers on either side

    expr = re.sub(r"(\D)\.(\D)", "", expr)

    # Remove runs of characters longer than 1

    parts = re.split(r"([A-Za-z]+)", expr)
    expr = "".join(
        part[0] if part and part[0] in string.ascii_letters else part
        for part in parts
    )

    # Substitute some letters for their Sympy equivalents

    expr = expr.replace("T", "tan")
    expr = expr.replace("S", "sin")
    expr = expr.replace("C", "cos")
    expr = expr.replace("N", "log")
    expr = expr.replace("E", "exp")
    expr = expr.replace("I", "integrate")
    expr = expr.replace("D", "diff")

    transformations = standard_transformations + (
        implicit_multiplication_application,
        convert_xor,
    )

    return sympy.parse_expr(expr, transformations=transformations)


def max_by(vec: VyList, key=lambda x: x, cmp=None, ctx=DEFAULT_CTX):
    """
    The maximum of a list according to a key function and/or a comparator.

    Parameters:
    key: Any -> Any
    A function to first transform each element of the list before comparing.
    cmp: (Any, Any) -> bool
    A binary function to check if its first argument is less than the second.
    """
    if key is None:

        def key(x, ctx=None):
            return x

    if cmp is None:

        def cmp(a, b, ctx=None):
            return a > b

    if not len(vec):
        return 0

    return foldl(
        lambda a, b, ctx=ctx: a
        if safe_apply(
            cmp,
            safe_apply(key, a, ctx=ctx),
            safe_apply(key, b, ctx=ctx),
            ctx=ctx,
        )
        else b,
        vec,
        ctx=ctx,
    )


def min_by(vec: VyList, key=None, cmp=None, ctx=DEFAULT_CTX):
    """
    The minimum of a list according to a key function and/or a comparator.

    Parameters:
    key: Any -> Any
    A function to first transform each element of the list before comparing.
    cmp: (Any, Any) -> bool
    A binary function to check if its first argument is less than the second.
    """
    if key is None:

        def key(x, ctx=None):
            return x

    if cmp is None:

        def cmp(a, b, ctx=None):
            return a < b

    if not len(vec):
        return 0

    return foldl(
        lambda a, b, ctx=ctx: a
        if cmp(key(a, ctx=ctx), key(b, ctx=ctx), ctx=ctx)
        else b,
        vec,
        ctx=ctx,
    )


def mold(content: VyList, shape: VyList) -> VyList:
    """
    Mold a list into a shape.

    Parameters:
    content: VyList
    The list to mold.
    shape: VyList
    The shape to mold the list into.

    Returns:
    VyList
    The content, molded into the shape.
    """
    # Because something needs to be mutated.

    content, shape = LazyList(content), LazyList(shape)

    @lazylist_from(content)
    def _mold(content, shape, index=0):
        output = []
        index = index
        for item in shape:
            if vy_type(item, simple=True) is list:
                output.append(_mold(content, item, index))
                yield output[-1]
                index += len(output[-1]) - 1
            else:
                output.append(content[index])
                yield output[-1]
            index += 1

    return _mold(content, shape)


def mold_without_repeat(
    content: VyList,
    shape: VyList,
) -> VyList:
    """
    Mold a list into a shape but don't reuse content.

    Parameters:
    content: VyList
    The list to mold.
    shape: VyList
    The shape to mold the list into.

    Returns:
    VyList
    The content, molded into the shape.
    """

    @lazylist_from(content)
    def _mold(content, shape, index=0):
        index = index
        output = []
        for item in shape:
            if type(item) is list or type(item) is LazyList:
                output.append(_mold(content, item, index))
                yield output[-1]
                index += len(output[-1]) - 1
            else:
                try:
                    output.append(content[index])
                    yield output[-1]
                except IndexError:
                    break  # We've reached the end of content, stop looping
            index += 1

    return _mold(content, shape)


def pad_to_square(array: VyList) -> VyList:
    """
    Returns an array padded to the square of the largest dimension.
    """
    mat = list(map(list, array))
    max_dim = max(len(mat), max(map(len, mat)))
    for row in mat:
        for _ in range(max_dim - len(row)):
            row.append(0)
    if max_dim > len(mat):
        mat += [[0] * max_dim for _ in range(max_dim - len(mat))]
    return mat


def partition_at(booleans: VyList, array: VyList) -> VyList:
    """
    Partitions an array at the indices where booleans is True.
    """

    is_infinite = (
        type(array) is LazyList
        and array.infinite
        or type(booleans) is LazyList
        and booleans.infinite
    )

    def gen(array, booleans):
        chunk = []
        index = 0
        booleans, array = LazyList(booleans), LazyList(array)
        while array.has_ind(index):
            if booleans.has_ind(index) and booleans[index]:
                yield chunk
                chunk = []
            chunk.append(array[index])
            index += 1
        if chunk:
            yield chunk

    return LazyList(gen(array, booleans), is_infinite)


def partition_at_indices(indices: VyList, array: VyList) -> VyList:
    """
    Partitions an array at the indices given.
    """

    is_infinite = (
        type(array) is LazyList
        and array.infinite
        or type(indices) is LazyList
        and indices.infinite
    )

    def gen(array, indices):
        chunk = []
        index = 0
        indices, array = LazyList(indices), LazyList(array)
        while array.has_ind(index):
            if index + 1 in indices:
                yield chunk
                chunk = []
            chunk.append(array[index])
            index += 1
        if chunk:
            yield chunk

    return LazyList(gen(array, indices), is_infinite)


def pi_digits(n: int):
    """Generate x digits of Pi. Spigot's formula."""

    @lazylist
    def gen():
        x = n + 1
        k, a, b, a1, b1 = 2, 4, 1, 12, 4
        while x > 0:
            p, q, k = k * k, 2 * k + 1, k + 1
            a, b, a1, b1 = a1, b1, p * a + q * a1, p * b + q * b1
            d, d1 = a / b, a1 / b1
            while d == d1 and x > 0:
                yield int(d)
                x -= 1
                a, a1 = 10 * (a % b), 10 * (a1 % b1)
                d, d1 = a / b, a1 / b1

    return gen()


def pop(iterable_object: VyList, count: int, ctx: Context) -> List[Any]:
    """Pops (count) items from iterable. If there isn't enough items
    within iterable, input is used as filler."""
    popped_items = []
    for _ in range(count):
        if iterable_object:
            popped_items.append(iterable_object.pop())
        else:
            temp = get_input(ctx)
            popped_items.append(temp)

    if ctx.retain_popped:
        for item in popped_items[::-1]:
            iterable_object.append(item)

    if ctx.reverse_flag:
        popped_items = popped_items[::-1]

    if count == 1:
        return popped_items[0]
    return popped_items


def prefixes(lhs: Union[VyList, str], ctx: Context) -> VyList:
    """Returns a list of prefixes of a string or list
    (not including [] or '')"""
    if isinstance(lhs, str):
        return [lhs[: i + 1] for i in range(len(lhs))]
    else:

        @lazylist_from(lhs)
        def gen():
            temp = []
            for item in iterable(lhs, ctx=ctx):
                temp.append(deep_copy(item))
                yield temp

        return gen()


def primitive_type(item: Any) -> Union[str, type]:
    """Turns int/Rational/str into 'Scalar' and everything else
    into list"""
    if type(item) in [int, sympy.Rational, str, types.FunctionType] or is_sympy(
        item
    ):
        return SCALAR_TYPE
    assert type(item) in [list, LazyList]
    return list


def reverse_number(
    item: Union[int, sympy.Rational]
) -> Union[int, sympy.Rational]:
    """Reverses a number. Negative numbers are returned negative"""
    if item == 0:
        return 0
    sign = -1 if item < 0 else 1
    rev = str(abs(item)).strip("0")[::-1]
    return vyxalify(sympy.Rational(eval(rev) * sign))


def ring_translate(string: str, map_source: Union[str, list]) -> str:
    """Ring translates a given string according to the provided mapping
    - that is, map matching elements to the subsequent element in the
    translation ring. The ring wraps around."""
    ret = ""
    for char in string:
        if char in map_source:
            ret += map_source[(map_source.index(char) + 1) % len(map_source)]
        else:
            ret += char
    return ret


def run(ast: vyxal.structure):
    code = vyxal.transpile.transpile_ast(ast)
    stack = []
    ctx = Context()
    exec(code, locals() | globals())
    return pop(stack, 1, ctx)


def safe_apply(
    function: types.FunctionType, *args, ctx, arity_override=None
) -> Any:
    """
    Applies function to args that adapts to the input style of the passed function.
    If the function is a _lambda (it's been defined within λ...;), it passes a
      list of arguments and length of argument list.
    Otherwise, if the function is a user-defined function (starts with FN_), it
      simply passes the argument list.
    Otherwise, unpack args and call as usual

    *args does NOT contain ctx
    """
    if function.__name__.startswith("_lambda"):
        ret = function(
            list(args)[::-1], function, arity_override or len(args), ctx=ctx
        )
        if len(ret):
            return ret[-1]
        else:
            return []
    elif function.__name__.startswith("VAR_"):
        return function(list(args)[::-1], function, ctx=ctx)[-1]
    elif takes_ctx(function):
        return function(*args, ctx=ctx)
    return function(*args)


def scalarify(value: Any) -> Union[Any, List[Any]]:
    """Returns value[0] if value is a list of length 1, else value"""
    if type(value) in (list, LazyList):
        if len(value) == 1:
            return value[0]
        else:
            return value
    else:
        return value


def scanl(
    function: types.FunctionType, vector: List[Any], ctx: Context
) -> List[Any]:
    """Cumulative reduction of vector by function"""
    vector = iterable(vector, ctx=ctx)

    @lazylist_from(vector)
    def gen():
        working = None
        for item in vector:
            if working is None:
                working = item
            else:
                yield working
                working = safe_apply(function, working, item, ctx=ctx)
        yield working

    return gen()


def sentence_case(item: str) -> str:
    """Returns the string sentence-cased in an 05AB1E manner"""
    ret = ""
    capitalise = True
    for char in item:
        if capitalise:
            ret += char.upper()
        else:
            ret += char.lower()
        if capitalise and char != " ":
            capitalise = False
        capitalise = capitalise or char in "!?."
    return ret


def set_intersection(
    lhs: Union[VyList, str], rhs: Union[VyList, str]
) -> VyList:
    """Returns the intersection of two (possibly infinite) sets"""
    ts = vy_type(lhs, rhs, simple=True)
    if ts == (str, str):
        return keep(lhs, rhs)
    if ts == (list, list):
        if is_inf(lhs) and is_inf(rhs):
            # Not taking any chances
            lhs = deep_copy(lhs)
            rhs = deep_copy(rhs)

            # This is unordered and woefully inefficient, but whatever, it works in theory
            @infinite_lazylist
            def gen():
                result = []  # Store what's been counted before
                while True:
                    a = next(lhs)
                    b = next(rhs)
                    # Consider each value between the two sets. Only the ones appearing in both sets will be in the result
                    # We store only the first n generated items at a time, so checking doesn't take infinite time.
                    # The second time a value is encountered, it will be in the other's generated values, and is yielded.
                    # It is appended to the list of what's been counted before so it can never appear again.
                    if a in rhs.generated and a not in result:
                        yield a
                        result.append(a)
                    if b in lhs.generated and b not in result:
                        yield b
                        result.append(b)

            return gen()
        # Otherwise, the result will be finite
        # So a more efficient algorithm can be used
        if is_inf(rhs):
            lhs, rhs = rhs, lhs
        return keep(lhs, rhs)
    if ts[0] == list:
        lhs, rhs = rhs, lhs
    return keep(rhs, lhs)


def simplify(value: Any) -> Union[int, float, str, list]:
    """
    Simplify values.
    Turns sympy values into floats, including sympy values in lists
    """
    if value is None or isinstance(value, (int, float, str)):
        return value
    elif is_sympy(value):
        if value.is_real:
            return float(value)
        else:
            return complex(value)
    elif isinstance(value, types.FunctionType):
        return str(value)
    else:
        return [simplify(x) for x in value]


def stationary_points(lhs: str) -> List[Union[int, float]]:
    """Returns a list of stationary points of a mathematical function"""
    x = sympy.symbols("x")
    d_dx = sympy.diff(make_expression(lhs), x)
    zeros = sympy.solve(d_dx, x)

    return LazyList(zeros)


def string_replace(
    text: str, target: Union[str, VyList], repl: Union[str, VyList], count=0
) -> str:
    """Generic helper to replace substrings

    Parameters:
    text: The text in which to make replacements.
    targets: The substring(s) to replace. Should be a list if repl is a list. If
        targets is longer than repls, repls will be extended with empty strings.
    repls: The substring(s) to replace with. If a list, should not be longer
        than target.
    count: How many replacements to make. Replaces all by default.
    """
    if vy_type(target, simple=True) is not list:
        # str.replace requires -1 to replace all occurrences, not 0
        return text.replace(str(target), str(repl), count or -1)
    elif vy_type(repl, simple=True) is list:
        # Multiple targets, multiple replacements
        if len(repl) < len(target):
            repl += [""] * (len(target) - len(repl))
        # Approach taken from https://stackoverflow.com/a/6117124
        # Dictionary mapping targets to their replacements
        repl_dict = dict(zip(map(str, target), map(str, repl)))
        # OR all the targets together
        pattern = re.compile("|".join(map(re.escape, repl_dict.keys())))
        # Map each matched target to its corresponding replacement
        return pattern.sub(lambda m: repl_dict[m.group(0)], text, count=count)
    else:
        # Multiple targets, single replacement
        pattern = re.compile("|".join(re.escape(str(elem)) for elem in target))
        return pattern.sub(str(repl), text, count=count)


def suffixes(lhs: VyIterable, ctx: Context) -> VyList:
    """Returns a list of suffixes, including the original list"""
    if isinstance(lhs, str):
        return [lhs[-i:] for i in range(len(lhs), 0, -1)]

    @lazylist_from(lhs)
    def gen():
        lst = iterable(lhs, ctx=ctx)
        while lst:
            yield lst
            lst = lst[1:]

    return gen()


def takes_ctx(function: types.FunctionType) -> bool:
    """Whether or not a function accepts a context argument"""
    argspec = inspect.getfullargspec(function)
    return "ctx" in argspec.args or "ctx" in argspec.kwonlyargs


def to_base_digits(value: int, base: int) -> List[int]:
    """Returns value in base 'base' from base 10 as a list of digits"""
    if base == 0:
        return 0

    if value == 0:
        return [0]

    ret = []
    n = value

    if base >= 0:
        while n >= base:
            n, digit = divmod(n, base)
            ret.append(digit)
        ret.append(n)
        return ret[::-1]
    elif base == -1:
        if n > 0:
            return [1] + [0, 1] * (n - 1)
        else:
            return [1, 0] * -n
    else:
        while True:
            n, remainder = divmod(n, base)

            if remainder < 0:
                n, remainder = n + 1, remainder - base

            ret = [remainder] + ret
            if n == 0:
                break
        return ret


def to_base_alphabet(value: int, alphabet: str) -> str:
    """to_base_digit with a custom base"""
    temp = to_base_digits(value, len(alphabet))
    return "".join([alphabet[i] for i in temp])


def to_simple_number(value: NUMBER_TYPE) -> Union[int, float, complex]:
    if not is_sympy(value):
        return value
    if sympy.im(value) != 0:
        return complex(value)
    elif value.is_integer:
        return int(value)
    else:
        return float(value)


def transfer_capitalisation(source: str, target: str) -> str:
    """Returns target with the capitalisation of source"""
    ret = ""
    for i in range(min(len(source), len(target))):
        if source[i].isupper():
            ret += target[i].upper()
        elif source[i].islower():
            ret += target[i].lower()
        else:
            ret += target[i]

    if len(target) > len(source):
        ret += target[i + 1 :]

    return ret


@lazylist
def transpose(
    matrix: VyList, filler: Optional[Any] = None, ctx: Context = DEFAULT_CTX
) -> VyList:
    """Transposes a matrix
    In order to handle infinite lists, it generates the transpose
    antidiagonal by antidiagonal.
    """
    matrix = iterable(matrix, ctx=ctx)
    matrix = vy_map(iterable, matrix, ctx=ctx)

    @lazylist_from(matrix)
    def gen_row(r: int):
        c = 0
        while has_ind(matrix, c):
            if has_ind(matrix[c], r):
                yield matrix[c][r]
            elif filler is not None:
                yield filler
            c += 1

    r = 0
    while True:
        if any(has_ind(row, r) for row in matrix):
            this_row = gen_row(r)
            if type(matrix[r]) is str:
                yield "".join(this_row)
            else:
                yield LazyList(this_row)
            r += 1

        else:
            break


def uncompress(token: lexer.Token) -> Union[int, str]:
    """Uncompress the token's value based on the token type.

    Handles the following token types: TokenType.STRING,
    TokenType.COMPRESSED_NUMBER, TokenType.COMPRESSED_STRING
    """
    if token.name == lexer.TokenType.STRING:
        return uncompress_dict(token.value)
    if token.name == lexer.TokenType.COMPRESSED_STRING:
        return uncompress_str(token.value)
    if token.name == lexer.TokenType.COMPRESSED_NUMBER:
        return uncompress_num(token.value)

    return token.value


def uncompress_dict(source: str) -> str:
    """Implements Vyxal dictionary compression"""
    characters = collections.deque(source)
    ret, temp_scc = "", ""
    escaped = False
    while characters:
        char = characters.popleft()
        if escaped:
            if temp_scc:
                pos = vyxal.encoding.compression.find(temp_scc)
                if pos < len(vyxal.dictionary.small_dictionary):
                    ret += vyxal.dictionary.small_dictionary[pos]
                temp_scc = ""
            if char not in vyxal.encoding.compression or char == "Π":
                ret += "\\"
            ret += char
            escaped = False

        elif char == "\\":
            escaped = True
        elif char in vyxal.encoding.compression:
            temp_scc += char
            if len(temp_scc) == 2:
                index = from_base_alphabet(temp_scc, vyxal.encoding.compression)
                if index < len(vyxal.dictionary.contents):
                    ret += vyxal.dictionary.contents[index]
                elif temp_scc == "ΠΠ":
                    ret += "ΠΠ"
                temp_scc = ""
        else:
            if temp_scc:
                pos = vyxal.encoding.compression.find(temp_scc)
                interop = False
                if pos < len(vyxal.dictionary.small_dictionary):
                    ret += vyxal.dictionary.small_dictionary[pos]
                elif temp_scc == "Π":
                    ret += "Π"
                    interop = True
                temp_scc = ""
                if char == " " and not interop:
                    continue
            ret += char

    if temp_scc:
        pos = vyxal.encoding.compression.find(temp_scc)
        if pos < len(vyxal.dictionary.small_dictionary):
            ret += vyxal.dictionary.small_dictionary[pos]
        elif temp_scc == "Π":
            ret += "Π"

    return ret


def uncompress_str(string: str) -> str:
    """Decompress a base 255 compressed string"""
    base_10_representation = from_base_alphabet(
        string, vyxal.encoding.codepage_string_compress
    )

    actual = to_base_alphabet(
        base_10_representation, vyxal.encoding.base_27_alphabet
    )
    return actual


def uncompress_num(num: str) -> int:
    """Decompress a base 255 compressed number"""
    return from_base_alphabet(num, vyxal.encoding.codepage_number_compress)


def urlify(item: str) -> str:
    """Makes a url ready for requesting"""
    if not (item.startswith("http://") or item.startswith("https://")):
        return "https://" + item
    return item


def vy_eval(item: str, ctx: Context) -> Any:
    """Evaluates an item. Does so safely if using the online
    interpreter"""
    if item and type(item) is str and item[0] == "λ":
        # Lambda, so return that
        ctx.stacks.append([])
        vyxal.elements.vy_exec(item, ctx=ctx)
        fn = ctx.stacks.pop().pop()
        return fn
    if ctx.online:
        print("evaluating", item, parse.parse(lexer.tokenise(item)))
        try:
            t = ast.literal_eval(item)
            if type(t) is float:
                t = sympy.Rational(str(t))
            return vyxalify(t)
        except Exception:  # skipcq: PYL-W0703
            vyobj = parse.parse(lexer.tokenise(item))
            if len(vyobj) == 1 and parse.is_literal(vyobj[0]):
                try:
                    return run(vyobj)
                except Exception as e:
                    pass
            t = item
            pobj = re.compile(r"(\d+)/(\d+)")
            mobj = pobj.match(t)
            if mobj:
                t = sympy.nsimplify(
                    sympy.nsimplify(mobj.group(1))
                    / sympy.nsimplify(mobj.group(2)),
                    rational=True,
                )
                return t
            return item
    else:
        try:
            t = eval(item)
            if type(t) is float:
                t = sympy.Rational(str(t))
            return vyxalify(t)
        except Exception:  # skipcq: PYL-W0703
            vyobj = parse.parse(lexer.tokenise(item))
            if len(vyobj) == 1 and parse.is_literal(vyobj[0]):
                try:
                    return run(vyobj)
                except Exception as e:
                    pass
            return item


def vy_floor_str_helper(item):
    temp = ""
    for char in item:
        if char == "-" and temp == "":
            temp += char
        elif char == "0" and (temp == "" or temp == "-"):
            continue
        elif char.isdigit():
            temp += char
        elif char == "." and "." not in temp:
            temp += char
    if not temp:
        return sympy.nsimplify(0)
    return sympy.nsimplify(temp, rational=True)


def vy_map(function, vector, ctx: Context = DEFAULT_CTX):
    """Apply function to every element of vector"""

    @lazylist_from(vector)
    def gen():
        idx = 0
        arity = (
            function.stored_arity
            if hasattr(function, "stored_arity")
            else (function.arity if hasattr(function, "arity") else None)
        )
        for element in iterable(vector, range, ctx=ctx):
            if not arity or arity == 1:
                yield safe_apply(function, element, ctx=ctx)
            elif arity == 2:
                yield safe_apply(function, element, idx, ctx=ctx)
            elif arity == 3:
                yield safe_apply(function, element, idx, vector, ctx=ctx)
            else:
                yield safe_apply(function, element, ctx=ctx)
            idx += 1

    return gen()


def vy_type(item, rhs=None, other=None, simple=False):
    """
    Get the Vyxal-friendly type(s) of 1-3 values.
    If only `item` is given, returns the Vyxal type of `item`.
    If both`item` and `rhs` or all three (`item`, `rhs`, and `other`)
    are given, then it returns a tuple containing their types.

    Returns `list` for lists
    Returns `str` for strings
    Returns `NUMBER_TYPE` if a value is int, complex, float, or sympy
    Returns `LazyList` for `LazyList`s if `simple` is `False`
      (the default) but `list` if `simple` is `True`
    """
    if other is not None:
        return (
            vy_type(item, simple=simple),
            vy_type(rhs, simple=simple),
            vy_type(other, simple=simple),
        )
    elif rhs is not None:
        return (vy_type(item, simple=simple), vy_type(rhs, simple=simple))
    elif (x := type(item)) in (int, complex, float) or is_sympy(item):
        assert x is not float
        return NUMBER_TYPE
    elif simple and isinstance(item, LazyList):
        return list
    else:
        return x


def vyxalify(value: Any) -> Any:
    """Takes a value and returns it as one of the four types we use here."""
    if isinstance(value, bool):
        return str(value)
    elif is_sympy(value):
        return sympy.nsimplify(value, rational=True)
    elif isinstance(value, (float, complex, int)):
        return sympy.nsimplify(value, rational=True)
    elif isinstance(value, (str, LazyList, types.FunctionType)):
        return value
    elif isinstance(value, list):
        return list(map(vyxalify, value))
    else:
        return LazyList(map(vyxalify, value))


def wrap_with_width(vector: Union[str, list], width: int) -> list[Any]:
    """A version of textwrap.wrap that plays nice with spaces"""
    ret: list[Union[str, list]] = []
    temp = []
    for item in vector:
        temp.append(item)
        if len(temp) == width:
            if all(type(x) is str for x in temp):
                ret.append("".join(temp))
            else:
                ret.append(temp[::])
            temp = []
    if len(temp) < width and temp:
        if all(type(x) is str for x in temp):
            ret.append("".join(temp))
        else:
            ret.append(temp[::])

    return ret


def wrapify(
    item: Any, count: int = None, ctx: Context = DEFAULT_CTX
) -> List[Any]:
    """Leaves lists as lists, wraps scalars into a list"""
    if count is not None:
        temp = pop(item, count, ctx)
        if count == 1:
            return [temp]
        else:
            return temp
    else:
        if primitive_type(item) == SCALAR_TYPE:
            return [item]
        else:
            return item
