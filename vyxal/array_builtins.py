import itertools
import os


import vyxal
from vyxal.utilities import Function
from vyxal.utilities import *

try:
    import numpy
    import pwn
    import regex
    import sympy
except:
    os.system("pip3 install -r requirements.txt --quiet --disable-pip-version-check")
    import numpy
    import pwn
    import regex
    import sympy


def atleast_ndims(vector, n):
    """Check if an array has at least n dimensions"""
    if n == 0:
        return 1
    vec_type = vy_type(vector)
    if vec_type is Generator:
        try:
            return atleast_ndims(next(vector), n - 1)
        except StopIteration:
            return 1
    if vec_type is list:
        return not vector or atleast_ndims(vector[0], n - 1)
    return 0


def cartesian_product(lhs, rhs):
    if Function not in (vy_type(lhs), vy_type(rhs)):
        lhs, rhs = iterable(lhs), iterable(rhs)
        if (vy_type(lhs), vy_type(rhs)) in (
            (Number, Number),
            (Number, str),
            (str, Number),
            (str, str),
        ):
            return Generator(
                map(first_n, itertools.product(iterable(lhs), iterable(rhs)))
            )
        return Generator(itertools.product(iterable(lhs), iterable(rhs)))

    if vy_type(lhs) is Function:
        fn, init = lhs, rhs
    else:
        fn, init = rhs, lhs

    @Generator
    def gen():
        prev = None
        curr = init
        while prev != curr:
            prev = deref(curr)
            curr = fn([curr])[-1]
        yield curr

    return gen()[-1]


def cumulative_sum(vector):
    return scanl_by_axis(vyxal.builtins.add, vector, axis=0)


def deref(item, generator_to_list=True, limit=-1):
    if vy_type(item) is Generator:
        if limit != -1:
            return item.limit_to_items(limit)
        return [item.safe, item._dereference][generator_to_list]()
    if type(item) not in [int, float, str]:
        return list(map(deref, item))
    return item


def determinant(matrix):
    det = numpy.linalg.det(numpy.asarray(deref(matrix)))
    # If it's a number, don't convert to list
    if isinstance(matrix, numpy.number):
        return det
    else:
        return det.tolist()


def diagonals(vector):
    # Getting real heavy Mornington Crescent vibes from this
    # joke explanation: the diagonals are the most important part of the game
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


def diagonal_main(matrix):
    return numpy.asarray(matrix).diagonal().tolist()


def diagonal_anti(matrix):
    flipped = numpy.fliplr(numpy.asarray(matrix)).diagonal().tolist()
    return flipped


def dot_product(lhs, rhs):
    return summate(vyxal.builtins.multiply(lhs, rhs))


def first_n(func, n=None):
    if Function not in (type(func), type(n)):
        if n:
            return iterable(func)[n:]

        ret = "".join([vyxal.builtins.vy_str(n) for n in iterable(func)])
        return vyxal.builtins.vy_eval(ret)
    ret = []
    current_index = 0
    n = n or 1
    if isinstance(n, Function):
        call, limit = n, func
    else:
        call, limit = func, n
    while len(ret) < limit:
        result = call([current_index])[-1]
        if result:
            ret.append(current_index)
        current_index += 1

    return ret


def foldl_by_axis(fn, vector, axis, init=None):
    if axis > 0:
        return map_norm(
            lambda inner_arr: foldl_by_axis(fn, inner_arr, axis - 1, init=init), vector
        )
    vec_type = vy_type(vector)
    if init is None:
        acc = next(vector) if vec_type is Generator else vector.pop(0)
    else:
        acc = init
    for inner_arr in vector:
        acc = vectorise(fn, inner_arr, acc)
    return acc


def foldl_cols(fn, vector, init=None):
    """
    Fold each column of a matrix from top to bottom, possibly with a starting value.
    TODO generalize to multiple dimensions
    """
    vec_type = vy_type(vector)
    print(f"vector={vector}")
    if vec_type is Generator:
        if vector.end_reached:
            return []
        first_row = next(vector)
        if atleast_ndims(first_row, 2):
            return map_norm(lambda arr: foldl_cols(fn, arr, init=init), vector)
        num_cols = len(first_row)
        cs = range(num_cols)
        if init is None:
            res = next(vector)
            start = 1
        else:
            res = [init] * num_cols
            start = 0
        while not vector.end_reached:
            res = zip_with2(fn, res, next(vector))
        return res
    elif vec_type is list:
        num_rows = len(vector)
        if not num_rows:
            return []
        if atleast_ndims(vector[0], 2):
            print("recursioning")
            return map_norm(lambda arr: foldl_cols(fn, arr, init=init), vector)
        num_cols = len(vector[0])
        cs = range(num_cols)
        if init is None:
            res = vector[0]
            start = 1
        else:
            res = [init] * num_cols
            start = 0
        for r in range(start, num_rows):
            res = zip_with2(fn, res, vector[r])
        return res
    raise ValueError("Expected list or generator, cannot fold the columns of an atom")


def foldl_first(fn, vector, init=None):
    return foldl_by_axis(fn, vector, 0, init)


def foldl_rows(fn, vector, init=None):
    """
    Fold each row of a matrix from the left, possibly with a starting value.
    """
    if not vector:
        return []
    vec_type = vy_type(vector)
    first_row = vector[0] if vec_type is list else next(vector)
    inner_type = vy_type(first_row)
    if inner_type is list:
        return [foldl_rows(fn, row, init=init) for row in vector]
    elif inner_type is Generator:
        @Generator
        def gen():
            yield foldl_rows(fn, first_row, init=init)
            for row in vector:
                yield foldl_rows(fn, row, init=init)

        return gen()
    else:  # 1D fold/reduction
        if vec_type is Generator:
            acc = next(vector) if init is None else init
            while not vector.end_reached:
                acc = safe_apply(fn, acc, next(vector))
            return acc
        else:
            if init is None:
                acc = vector[0]
                start = 1
            else:
                acc = init
                start = 0
            for i in range(start, len(vector)):
                acc = safe_apply(fn, vector[i], acc)
            return acc


def foldr_by_axis(fn, vector, axis, init=None):
    if axis > 0:
        return map_norm(
            lambda inner_arr: foldr_by_axis(fn, inner_arr, axis - 1, init=init), vector
        )
    vec_type = vy_type(vector)
    if vec_type is Generator:
        vector = vector._dereference()
    if init is None:
        acc = vector[-1]
        start = len(vector) - 2
    else:
        acc = init
        start = len(vector) - 1
    for i in range(start, -1, -1):
        acc = vectorise(fn, acc, vector[i])
    return acc


def foldr_cols(fn, vector, init=None):
    """
    Fold each column of a matrix from top to bottom, possibly with a starting value.
    TODO generalize to multiple dimensions
    """
    vec_type = vy_type(vector)
    if vec_type is Generator:
        vector = vector._dereference()
    num_rows = len(vector)
    if not num_rows:
        return []
    num_cols = len(vector[0])
    cs = range(num_cols)
    if init is None:
        res = vector[-1]
        start = len(vector) - 2
    else:
        res = [init] * num_cols
        start = len(vector) - 1
    for r in range(start, -1, -1):
        res = zip_with2(fn, vector[r], res)
    return res


def foldr_first(fn, vector, init=None):
    return foldr_by_axis(fn, vector, 0, init)


def foldr_rows(fn, vector, init=None):
    """
    Fold each row of a matrix from the left, possibly with a starting value.
    """
    vec_type = vy_type(vector)
    if vec_type is Generator:
        vector = vector._dereference()
    if not vector:
        return []
    inner_type = vy_type(vector[0])
    if inner_type is list:
        return [foldr_rows(fn, row, init=init) for row in vector]
    elif inner_type is Generator:
        @Generator
        def gen():
            yield foldr_rows(fn, vector[0], init=init)
            for row in vector:
                yield foldr_rows(fn, row, init=init)

        return gen()
    # 1D fold/reduction
    if init is None:
        acc = vector[-1]
        start = len(vector) - 2
    else:
        acc = init
        start = len(vector) - 1
    for i in range(start, -1, -1):
        acc = safe_apply(fn, acc, vector[i])
    return acc


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


def inclusive_range(lhs, rhs):
    types = (vy_type(lhs), vy_type(rhs))
    if Function in types:
        if types[0] is Function:
            func, vector = lhs, rhs
        else:
            func, vector = rhs, lhs
        @Generator
        def gen():
            for index, item in enumerate(vector):
                if (index + 1) % 2:
                    yield item
                else:
                    yield func([item])[-1]

        return gen()
    if types != (Number, Number):
        lhs, rhs = vyxal.builtins.vy_str(lhs), vyxal.builtins.vy_str(rhs)
        pobj = regex.compile(rhs)
        return pobj.split(lhs)

    if lhs < rhs:
        return Generator(range(int(lhs), int(rhs) + 1))
    else:
        return Generator(range(int(lhs), int(rhs) - 1, -1))


def index(vector, index):
    types = vy_type(vector), vy_type(index)
    if Function in types:
        if types[0] is Function:
            fn, init = vector, index
        else:
            fn, init = index, vector
        @Generator
        def gen():
            seen = []
            curr = deref(init)
            while curr not in seen:
                yield curr
                seen.append(curr)
                curr = deref(fn([curr])[-1])

        return gen()
    elif vy_type(index) == Number:
        if vy_type(vector) is Generator:
            return vector[int(index)]
        return vector[int(index) % len(vector)]
    elif vy_type(index) in (list, Generator):
        return vector[slice(*index)]
    else:
        return [vector, index, vyxal.builtins.join(vector, index)]


def indexed_into(vector, indices):
    types = (vy_type(vector), vy_type(indices))
    if Function not in types:
        ret = []
        vector = iterable(vector)
        for ind in iterable(indices):
            ret.append(vector[ind % len(vector)])
        return ret
    else:
        if vy_type(vector) is Function:
            fn, init = vector, indices
        else:
            fn, init = indices, vector
        @Generator
        def gen():
            seen = []
            curr = deref(init)
            while curr not in seen:
                curr = deref(fn([curr])[-1])
                seen.append(curr)
            yield curr

        return (gen())[-1]


def indices_where(fn, vector):
    ret = []
    for i in range(len(vector)):
        if fn([vector[i]])[-1]:
            ret.append(i)
    return ret


def interleave(lhs, rhs):
    ret = []
    for i in range(min(len(lhs), len(rhs))):
        ret.append(lhs[i])
        ret.append(rhs[i])
    if len(lhs) != len(rhs):
        if len(lhs) < len(rhs):
            # The rhs is longer
            ret += list(rhs[i + 1 :])
        else:
            ret += list(lhs[i + 1 :])
    if type(lhs) is str and type(rhs) is str:
        return "".join(ret)
    return ret


def iterable(item, t=None):
    t = t or vyxal.interpreter.number_iterable
    if vy_type(item) == Number:
        if t is list:
            return [int(let) if let not in "-." else let for let in str(item)]
        if t is range:
            return Generator(
                range(
                    vyxal.interpreter.MAP_START,
                    int(item) + vyxal.interpreter.MAP_OFFSET,
                )
            )
        return t(item)
    else:
        return item


def map_at(function, vector, indices):
    @Generator
    def gen():
        for pos, element in enumerate(vector):
            if pos in indices:
                yield function([element])[-1]
            else:
                yield element

    return (gen())


def map_every_n(vector, function, index):
    @Generator
    def gen():
        for pos, element in enumerate(vector):
            if (pos + 1) % index:
                yield element
            else:
                yield function([element])[-1]

    return (gen())


def map_norm(fn, vector):
    vec_type = vy_type(vector)
    if vec_type is Generator:
        @Generator
        def gen():
            for item in vector:
                yield fn(item)

        return (gen())
    elif vec_type is Number:
        pass  # idk what to do here, make a range or use it as a singleton?
    else:
        return Generator(map(fn, vector))


def matrix_multiply(lhs, rhs):
    transformed_right = deref(transpose(rhs))
    ret = []

    for row in lhs:
        temp = []
        for col in transformed_right:
            temp.append(summate(vyxal.builtins.multiply(row, col)))
        ret.append(temp[::])
    return ret


def mold(content, shape):
    # https://github.com/DennisMitchell/jellylanguage/blob/70c9fd93ab009c05dc396f8cc091f72b212fb188/jelly/interpreter.py#L578
    for index in range(len(shape)):
        if type(shape[index]) == list:
            mold(content, shape[index])
        else:
            item = content.pop(0)
            shape[index] = item
            content.append(item)
    return shape


def nub_sieve(vector):
    @Generator
    def gen():
        occurances = {}
        for item in vector:
            yield int(item not in occurances)
            if item in occurances:
                occurances[item] += 1
            else:
                occurances[item] = 1

    return (gen())


def partition(item, I=1):
    # https://stackoverflow.com/a/44209393/9363594
    yield [item]
    for i in range(I, item // 2 + 1):
        for p in partition(item - i, i):
            yield [i] + p


def permutations(vector):
    t_vector = vy_type(vector)
    vector = itertools.permutations(vector)

    if t_vector is str:
        return Generator(map(lambda x: "".join(x), vector))
    return Generator(vector)


def powerset(vector):
    "powerset([1,2,3]) --> () (1,) (2,) (3,) (1,2) (1,3) (2,3) (1,2,3)"
    if type(vector) is Generator:
        vector = vector._dereference()
    elif type(vector) is str:
        vector = list(vector)
    return Generator(
        itertools.chain.from_iterable(
            itertools.combinations(vector, r) for r in range(len(vector) + 1)
        )
    )


def prefixes(vector):
    for i in range(len(iterable(vector))):
        yield iterable(vector)[0 : i + 1]


def run_length_encode(item):
    item = group_consecutive(iterable(item))
    return Generator(map(lambda x: [x[0], len(x)], item))


def scanl_by_axis(fn, vector, axis, init=None):
    """
    Cumulative reduce from the left
    With axis=0, vector is treated as a 1-D list,
    with axis=1, it's treated as a 2-D list and the second/innermost dimension is scanned,
    with axis=2, it's treated as a 3-D list and the third/innermost dimension is scanned, etc.
    """
    if vy_type(vector) is Function and vy_type(fn) in (list, Generator):
        temp = fn
        fn = vector
        vector = temp
    if axis > 0:
        return map_norm(
            lambda inner_arr: scanl_by_axis(fn, inner_arr, axis - 1, init=init), vector
        )
    vec_type = vy_type(vector)
    if init is None:
        acc = [next(vector) if vec_type is Generator else vector.pop(0)]
    else:
        acc = [init]
    for inner_arr in vector:
        acc.append(vectorise(fn, inner_arr, acc[-1]))
    return acc


def scanl_first(fn, vector, init=None):
    return scanl_by_axis(fn, vector, 0, init)


def scanl_rows(fn, vector, init=None):
    """
    Fold each row of a matrix from the left, possibly with a starting value.
    """
    if vy_type(vector) is Function and vy_type(fn) in (list, Generator):
        temp = fn
        fn = vector
        vector = temp
    if not vector:
        return []
    vec_type = vy_type(vector)
    first_row = vector[0] if vec_type is list else next(vector)
    inner_type = vy_type(first_row)
    if inner_type is list:
        return [scanl_rows(fn, row, init=init) for row in vector]
    elif inner_type is Generator:
        @Generator
        def gen():
            yield scanl_rows(fn, first_row, init=init)
            for row in vector:
                yield scanl_rows(fn, row, init=init)

        return (gen())
    else:  # 1D fold/reduction
        if vec_type is Generator:
            acc = [next(vector)] if init is None else [init]
            while not vector.end_reached:
                acc.append(safe_apply(fn, acc[-1], next(vector)))
            return acc
        else:
            if init is None:
                acc = [vector[0]]
                start = 1
            else:
                acc = [init]
                start = 0
            for i in range(start, len(vector)):
                acc.append(safe_apply(fn, vector[i], acc[-1]))
            return acc


def scanr_by_axis(fn, vector, axis, init=None):
    """
    Cumulative reduce from the left
    With axis=0, vector is treated as a 1-D list,
    with axis=1, it's treated as a 2-D list and the second/innermost dimension is scanned,
    with axis=2, it's treated as a 3-D list and the third/innermost dimension is scanned, etc.
    """
    if vy_type(vector) is Function and vy_type(fn) in (list, Generator):
        temp = fn
        fn = vector
        vector = temp
    if axis > 0:
        return map_norm(
            lambda inner_arr: scanr_by_axis(fn, inner_arr, axis - 1, init=init), vector
        )
    vec_type = vy_type(vector)
    if vec_type is Generator:
        vector = vector._dereference()
    if init is None:
        acc = [vector[-1]]
        start = len(vector) - 2
    else:
        acc = [init]
        start = len(vector) - 1
    for i in range(start, -1, -1):
        acc.append(vectorise(fn, acc[-1], vector[i]))
    return acc


def scanr_first(fn, vector, init=None):
    return scanr_by_axis(fn, vector, 0, init)


def scanr_rows(fn, vector, init=None):
    """
    Fold each row of a matrix from the left, possibly with a starting value.
    """
    if vy_type(vector) is Function and vy_type(fn) in (list, Generator):
        temp = fn
        fn = vector
        vector = temp
    vec_type = vy_type(vector)
    if vec_type is Generator:
        vector = vector._dereference()
    if not vector:
        return []
    inner_type = vy_type(vector[0])
    if inner_type is list:
        return [scanl_rows(fn, row, init=init) for row in vector]
    elif inner_type is Generator:
        @Generator
        def gen():
            yield scanr_rows(fn, vector[0], init=init)
            for row in vector:
                yield scanr_rows(fn, row, init=init)

        return (gen())
    # 1D fold/reduction
    if init is None:
        acc = [vector[-1]]
        start = len(vector) - 2
    else:
        acc = [init]
        start = len(vector) - 1
    for i in range(start, -1, -1):
        acc.append(safe_apply(fn, acc[-1], vector[i]))
    return acc


def sublists(item):
    yield []
    length = len(item)
    for size in range(1, length + 1):
        for sub in range((length - size) + 1):
            yield item[sub : sub + size]


def summate(vector):
    vector = iterable(vector)
    if type(vector) is Generator:
        return vector._reduce(vyxal.builtins.add)
    if len(vector) > 0:
        ret = vector[0]
        for item in vector[1:]:
            ret = vyxal.builtins.add(ret, item)
        return ret
    else:
        return 0


def sums(vector):
    ret = []
    for i in range(len(vector)):
        ret.append(summate(vector[0 : i + 1]))
    return ret


def transpose(vector):
    # https://github.com/DennisMitchell/jellylanguage/blob/70c9fd93ab009c05dc396f8cc091f72b212fb188/jelly/interpreter.py#L1311
    vector = iterable(vector)
    vector = list(vector)
    return Generator(
        map(
            lambda t: filter(None.__ne__, t),
            itertools.zip_longest(*map(iterable, vector)),
        )
    )


def uninterleave(item):
    left, right = [], []
    for i in range(len(item)):
        if i % 2 == 0:
            left.append(item[i])
        else:
            right.append(item[i])
    if type(item) is str:
        return ["".join(left), "".join(right)]
    return [left, right]


def uniquify(vector):
    seen = []
    for item in vector:
        if item not in seen:
            yield item
            seen.append(item)


def zip_with2(fn, xs, ys):
    xs_type, ys_type = vy_type(xs), vy_type(ys)
    # Convert both to Generators if not already
    xs = xs if xs_type is Generator else Generator((x for x in xs))
    ys = ys if ys_type is Generator else Generator((y for y in ys))
    @Generator
    def gen():
        try:
            while not (xs.end_reached or ys.end_reached):
                yield safe_apply(fn, next(ys), next(xs))
        except StopIteration:
            pass

    return (gen())


def zip_with_multi(fn, lists):
    lists = [
        lst if vy_type(lst) is Generator else Generator((x for x in lst))
        for lst in lists
    ]
    @Generator
    def gen():
        try:
            while not any(lst.end_reached for lst in lists):
                yield safe_apply(fn, *map(next, lists))
        except StopIteration:
            pass

    return (gen())
