"""A generic wrapper for all sorts of generators.

This is because itertools doesn't return things that return true when yeeted
into isinstance(<itertools object>, types.GeneratorType). Also, maps, ranges
and other stuff that needs to be lazily evaluated.
"""

import itertools
import types


def lazylist(fn):
    """A decorator to wrap function return values in `LazyList`"""

    def wrapped(*args, **kwargs):
        return LazyList(fn(*args, **kwargs))

    return wrapped


class LazyList:
    def __add__(self, rhs):
        @lazylist
        def gen():
            yield from self
            yield from rhs

        return gen()

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

                @lazylist
                def infinite_index():
                    copy = iter(self)
                    for _ in range(start or 0):
                        try:
                            next(copy)
                        except StopIteration:
                            return
                    i = 0
                    while True:
                        try:
                            item = next(copy)
                        except StopIteration:
                            break
                        if i % step == 0:
                            yield item
                        i += 1

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
                    ret.append(self.__getitem__(i))
                return ret
        else:
            if position < 0:
                self.generated += list(self)
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
        while True:
            try:
                yield self.__next__()
            except StopIteration:
                break

    def __len__(self):
        length = len(self.generated)
        while True:
            try:
                next(self)
                length += 1
            except StopIteration:
                break
        return length

    def __le__(self, other):
        return self.compare(other) <= 0

    def __lt__(self, other):
        return self.compare(other) == -1

    def __next__(self):
        from vyxal.helpers import vyxalify

        lhs = vyxalify(next(self.raw_object))
        self.generated.append(lhs)
        return lhs

    def __setitem__(self, position, value):
        if position >= len(self.generated):
            self.__getitem__(position)
        self.generated[position] = value

    def compare(self, other):
        # Returns -1 / 0 / 1 depending on whether this is smaller / equal /
        # greater than `other`

        # Should work for infinite lists
        item = next(self)
        other_item = next(other)
        while item == other_item:
            try:
                item = next(self)
            except StopIteration:
                if self == other:
                    return 0
                else:
                    return -1
            try:
                other_item = next(other)
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

    @lazylist
    def filter(self, fn):
        """A `LazyList` containing only elements for whom `fn` is true"""
        for item in self:
            if fn(item):
                yield item

    def listify(self):
        temp = self.generated[::]
        while True:
            try:
                temp.append(self.__next__())
            except StopIteration:
                break
        return temp

    def output(self, end="\n", ctx=None):
        from vyxal.elements import vy_print, vy_repr

        ctx.stacks.append(self.generated)
        vy_print("⟨ " if ctx.vyxal_lists else "[", "", ctx=ctx)
        for lhs in self.generated[:-1]:
            vy_print(lhs, " | " if ctx.vyxal_lists else ", ", ctx=ctx)
        if self.generated:
            vy_print(self.generated[-1], "", ctx=ctx)

        try:
            lhs = next(self)
            if len(self.generated) > 1:
                vy_print(" | " if ctx.vyxal_lists else ", ", "", ctx=ctx)
            while True:
                if isinstance(lhs, (types.FunctionType, LazyList)):
                    vy_print(lhs, "", ctx=ctx)
                else:
                    vy_print(vy_repr(lhs, ctx), "", ctx=ctx)
                lhs = next(self)
                vy_print(" | " if ctx.vyxal_lists else ", ", "", ctx=ctx)
        except StopIteration:
            vy_print(" ⟩" if ctx.vyxal_lists else "]", end, ctx=ctx)

    @lazylist
    def reversed(self):
        self.generated += list(itertools.tee(self.raw_object)[-1])
        for item in self.generated[::-1]:
            yield item
