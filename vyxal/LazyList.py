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
        from vyxal.helpers import join_with

        return LazyList(join_with(self.raw_object, rhs))

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
                if temp == lhs:
                    return 1
            return 0

    def __eq__(self, other):
        from vyxal.helpers import simplify

        return self.listify() == simplify(other)

    def __getitem__(self, position):
        if isinstance(position, slice):
            start, stop, step = (
                position.start or 0,
                position.stop,
                position.step or 1,
            )
            if stop is None:

                @lazylist
                def infinite_index():
                    self.listify()
                    yield from self.generated[start:stop:step]

                return infinite_index()
            else:
                ret = []
                if stop < 0:
                    stop = len(self.listify()) + stop
                for i in range(start, stop, step):
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

    def __init__(self, source, isinf=False):
        self.raw_object = source
        if isinstance(self.raw_object, types.FunctionType):
            self.raw_object = self.raw_object()
        elif not isinstance(self.raw_object, types.GeneratorType):
            self.raw_object = iter(self.raw_object)
        self.generated = []
        self.infinite = isinf

    def __iter__(self):
        from vyxal.helpers import join_with

        raw_object_clones = itertools.tee(self.raw_object)
        self.raw_object = raw_object_clones[0]
        return join_with(self.generated[::], raw_object_clones[1])

    def __len__(self):
        return len(self.listify())

    def __next__(self):
        from vyxal.helpers import vyxalify

        lhs = vyxalify(next(self.raw_object))
        self.generated.append(lhs)
        return lhs

    def __setitem__(self, position, value):
        if position >= len(self.generated):
            self.__getitem__(position)
        self.generated[position] = value

    def count(self, other):
        temp = self.listify()
        return temp.count(other)

    def filter(self, fn):
        @lazylist
        def gen():
            for item in self:
                if fn(item):
                    yield item

        return gen()

    def listify(self):
        from vyxal.helpers import vyxalify

        temp = self.generated + vyxalify(list(self.raw_object))
        self.raw_object = iter(temp[::])
        self.generated = []
        return temp

    def output(self, end="\n", ctx=None):
        from vyxal.elements import vy_print, vy_repr

        ctx.stacks.append(self.generated)
        vy_print("⟨ " if ctx.vyxal_lists else "[", "", ctx)
        for lhs in self.generated[:-1]:
            vy_print(lhs, " | " if ctx.vyxal_lists else ", ", ctx)
        if len(self.generated):
            vy_print(self.generated[-1], "", ctx)

        try:
            lhs = next(self)
            if len(self.generated) > 1:
                vy_print(" | " if ctx.vyxal_lists else ", ", "", ctx)
            while True:
                if isinstance(lhs, (types.FunctionType, LazyList)):
                    vy_print(lhs, "", ctx)
                else:
                    vy_print(vy_repr(lhs, ctx), "", ctx)
                lhs = next(self)
                vy_print(" | " if ctx.vyxal_lists else ", ", "", ctx)
        except StopIteration:
            vy_print(" ⟩" if ctx.vyxal_lists else "]", end, ctx)

    def reversed(self):
        def temp():
            self.generated += list(itertools.tee(self.raw_object)[-1])
            for item in self.generated[::-1]:
                yield item

        return temp()
