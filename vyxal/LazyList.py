"""A generic wrapper for all sorts of generators.

This is because itertools doesn't return things that return true when yeeted
into isinstance(<itertools object>, types.GeneratorType). Also, maps, ranges
and other stuff that needs to be lazily evaluated.
"""

import itertools
import types
import vyxal.helpers


def lazylist(fn):
    """A decorator to wrap function return values in `LazyList`"""

    def wrapped(*args, **kwargs):
        return LazyList(fn(*args, **kwargs))

    wrapped.__name__ = fn.__name__

    return wrapped


def infinite_lazylist(fn):
    """A decorator to wrap function return values in `LazyList`"""

    def wrapped(*args, **kwargs):
        return LazyList(fn(*args, **kwargs), isinf=True)

    wrapped.__name__ = fn.__name__

    return wrapped


def lazylist_from(iterable):
    def fn():
        def wrapped(*args, **kwargs):
            return LazyList(
                fn(*args, **kwargs),
                isinf=(type(iterable) is LazyList and iterable.infinite),
            )

        wrapped.__name__ = fn.__name__

        return wrapped

    return fn


class LazyList:
    def __add__(self, rhs):
        def gen():
            yield from self
            yield from rhs

        return LazyList(
            gen(),
            isinf=(self.infinite or (type(rhs) is LazyList and rhs.infinite)),
        )

    def __bool__(self):
        try:
            next(self)
            return True
        except StopIteration:
            return False

    def __call__(self, *args, **kwargs):
        return self

    def __contains__(self, lhs):
        if self.infinite:
            if self.generated:
                last = self.generated[-1]
            else:
                last = 0

            while last <= lhs:
                try:
                    last = next(self)
                    if last == lhs:
                        return 1
                except StopIteration:
                    break
            return 0
        else:
            for temp in self:
                if temp == lhs:
                    return 1
            return 0

    def __eq__(self, other):
        from vyxal.helpers import simplify

        if isinstance(other, list):
            return self.listify() == simplify(other)
        elif isinstance(other, LazyList):
            return self.listify() == other.listify()
        else:
            return False

    def __ge__(self, other):
        return self.compare(other) >= 0

    def __getitem__(self, position):
        if isinstance(position, slice):
            start, stop, step = (
                position.start,
                position.stop,
                position.step or 1,
            )
            if stop is None:

                @lazylist_from(self)
                def infinite_index():
                    i = start or 0
                    if i >= 0:
                        while self.has_ind(i):
                            yield self[i]
                            i += step
                    else:
                        while i < 0:
                            yield self[i]
                            i += step

                return infinite_index()
            else:
                ret = []
                if step < 0:
                    return LazyList(
                        itertools.islice(self.listify(), start, stop, step)
                    )
                if stop < 0:
                    stop = len(self) + stop
                for i in range(start or 0, stop, step):
                    ret.append(self[i])
                return ret
        else:
            if position < 0:
                while True:
                    try:
                        next(self)
                    except StopIteration:
                        break
                return self.generated[position]
            elif position < len(self.generated):
                return self.generated[position]
            else:
                while len(self.generated) < position + 1:
                    try:
                        next(self)
                    except StopIteration:
                        break
                if self.generated:
                    return self.generated[position % len(self.generated)]
                else:
                    return 0

    def __gt__(self, other):
        return self.compare(other) > 0

    def __init__(self, source, isinf=False):
        self.raw_object = source
        if isinstance(self.raw_object, types.FunctionType):
            self.raw_object = self.raw_object()
        elif not isinstance(self.raw_object, types.GeneratorType):
            self.raw_object = iter(self.raw_object)
        self.generated = []
        self.infinite = isinf

    def __iter__(self):
        yield from self.generated
        i = len(self.generated)
        while self.has_ind(i):
            yield self[i]
            i += 1

    def __len__(self):
        while True:
            try:
                next(self)
            except StopIteration:
                break
        return len(self.generated)

    def __le__(self, other):
        return self.compare(other) <= 0

    def __lt__(self, other):
        return self.compare(other) == -1

    def __next__(self):
        from vyxal.helpers import vyxalify

        item = vyxalify(next(self.raw_object))
        self.generated.append(item)
        return item

    def __setitem__(self, position, value):
        raise NotImplementedError("LazyList does not support assignment")

    def __delitem__(self, index):
        if index >= len(self.generated):
            self.__getitem__(index)
        del self.generated[index]

    def compare(self, other):
        # Returns -1 / 0 / 1 depending on whether this is smaller / equal /
        # greater than `other`

        # Should work for infinite lists
        self_clone = vyxal.helpers.deep_copy(self)
        other_clone = vyxal.helpers.deep_copy(other)
        item = next(self_clone)
        other_item = next(other_clone)
        while item == other_item:
            try:
                item = next(self_clone)
            except StopIteration:
                if self_clone == other_clone:
                    return 0
                else:
                    return -1
            try:
                other_item = next(other_clone)
            except StopIteration:
                return 1
        if item > other_item:
            return 1
        else:
            return -1

    def count(self, other):
        """Number of occurrences of `other` in this `LazyList`"""
        temp = self.listify()
        return temp.count(other)

    def filter(self, fn):
        @lazylist_from(self)
        def f():
            """A `LazyList` containing only elements for whom `fn` is true"""
            for item in self:
                if fn(item):
                    yield item

        return f()

    def has_ind(self, ind: int):
        """Whether or not this list is long enough for index `ind`"""
        if ind < len(self.generated):
            return 0 <= ind
        else:
            for _ in range(ind - len(self.generated) + 1):
                try:
                    next(self)
                except StopIteration:
                    return False
            return True

    def listify(self):
        temp = self.generated[::]
        while True:
            try:
                temp.append(self.__next__())
            except StopIteration:
                break
        return temp

    def output(self, open=None, sep=None, close=None, end="\n", ctx=None):
        from vyxal.elements import vy_print, vy_repr

        ctx.stacks.append(self.generated)
        if open is None:
            open = "⟨ " if ctx.vyxal_lists else "["
        if sep is None:
            sep = " | " if ctx.vyxal_lists else ", "
        if close is None:
            close = " ⟩" if ctx.vyxal_lists else "]"

        vy_print(open, "", ctx=ctx)
        for lhs in self.generated[:-1]:
            if isinstance(lhs, (types.FunctionType, LazyList)):
                vy_print(lhs, sep, ctx=ctx)
            else:
                vy_print(vy_repr(lhs, ctx), sep, ctx=ctx)
        if self.generated:
            if isinstance(self.generated[-1], (types.FunctionType, LazyList)):
                vy_print(self.generated[-1], "", ctx=ctx)
            else:
                vy_print(vy_repr(self.generated[-1], ctx), "", ctx=ctx)

        try:
            lhs = next(self)
            if len(self.generated) > 1:
                vy_print(sep, "", ctx=ctx)
            while True:
                if isinstance(lhs, (types.FunctionType, LazyList)):
                    vy_print(lhs, "", ctx=ctx)
                else:
                    vy_print(vy_repr(lhs, ctx), "", ctx=ctx)
                lhs = next(self)
                vy_print(sep, "", ctx=ctx)
        except StopIteration:
            vy_print(close, end, ctx=ctx)

    def appended(self, value):
        @lazylist_from(self)
        def gen():
            yield from self
            yield value

        return gen()

    @lazylist
    def reversed(self):
        self.generated += list(itertools.tee(self.raw_object)[-1])
        for item in self.generated[::-1]:
            yield item

    def remove(self, value):
        """Remove first occurance of value from the list"""
        temp = vyxal.helpers.deep_copy(self)
        try:
            while True:
                if next(temp) == value:
                    del temp.generated[-1]
                    return temp
        except StopIteration:
            return temp
