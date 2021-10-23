"""A generic wrapper for all sorts of generators.

This is because itertools doesn't return things that return true when yeeted
into isinstance(<itertools object>, types.GeneratorType). Also, maps, ranges
and other stuff that needs to be lazily evaluated.
"""

import types
from typing import Any, Union

import sympy
from sympy import Rational


def vyxalify(value: Any) -> Any:
    """Takes a value and returns it as one of the four types we use here."""

    if isinstance(value, sympy.core.numbers.Integer):
        return int(value)

    elif (
        isinstance(value, int)
        or isinstance(value, Rational)
        or isinstance(value, str)
        or isinstance(value, list)
        or isinstance(value, LazyList)
    ):
        return value

    else:
        return LazyList(map(vyxalify, value))


def join_with(lhs, rhs):
    for item in lhs:
        yield item

    for item in rhs:
        yield item


def lazylist(fn):
    """A decorator to wrap function return values in `LazyList`"""

    def wrapped(*args, **kwargs):
        return LazyList(fn(*args, **kwargs))

    return wrapped


def simplify(value: Any) -> Union[int, float, str, list]:
    if (
        isinstance(value, int)
        or isinstance(value, float)
        or isinstance(value, str)
    ):
        return value

    elif isinstance(value, Rational):
        return float(value)

    else:
        return list(map(simplify, value))


class LazyList:
    def __add__(self, rhs):
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
                    if len(self.generated):
                        for lhs in self.generated[position::step]:
                            yield lhs
                        temp = next(self)
                        while temp:
                            yield temp
                            temp = next(self)

                return infinite_index()
            else:
                ret = []
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
                return self.generated[position % len(self.generated)]

    def __init__(self, source, isinf=False):
        self.raw_object = source
        if isinstance(self.raw_object, types.FunctionType):
            self.raw_object = self.raw_object()
        elif not isinstance(self.raw_object, types.GeneratorType):
            self.raw_object = iter(self.raw_object)
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

    def __setitem__(self, position, value):
        if position >= len(self.generated):
            self.__getitem__(position)
        self.generated[position] = value

    def count(self, other):
        temp = self.listify()
        return temp.count(other)

    def listify(self):
        temp = self.generated + simplify(self.raw_object)
        self.raw_object = iter(temp[::])
        self.generated = []
        return temp

    def output(self, end="\n", ctx=None):
        from vyxal.elements import vy_print, vy_repr

        ctx.stacks.append(self.generated)
        stacks_index = len(ctx.stacks) - 1
        vy_print("⟨", "", ctx)
        for lhs in self.generated[:-1]:
            vy_print(lhs, "|", ctx)
        if len(self.generated):
            vy_print(self.generated[-1], "", ctx)

        try:
            lhs = next(self)
            if len(self.generated) > 1:
                vy_print("|", "", ctx)
            while True:
                if type(lhs) is types.FunctionType:
                    vy_print(lhs, "", ctx)
                else:
                    vy_print(vy_repr(lhs, ctx), "", ctx)
                lhs = next(self)
                vy_print("|", "", ctx)
        except StopIteration:
            vy_print("⟩", end, ctx)

    def reversed(self):
        def temp():
            self.generated += list(itertools.tee(self.raw_object)[-1])
            for item in self.generated[::-1]:
                yield item

        return temp()
