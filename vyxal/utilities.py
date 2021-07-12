import base64
import functools

import vyxal
from vyxal import vy_globals
from vyxal import words

# Generic type constants
Number = "NUMBER"
Iterable = "ITERABLE"
Function = type(lambda: None)

Python_Generator = type(
    i for i in (0,)
)  # https://chat.stackexchange.com/transcript/message/57555979#57555979

NEWLINE = "\n"
ONE_TWO_EIGHT_KB = 1024000
base53alphabet = "¡etaoinshrdlcumwfgypbvkjxqz ETAOINSHRDLCUMWFGYPBVKJXQZ"
base27alphabet = " etaoinshrdlcumwfgypbvkjxqz"


# Helper classes
class Comparitors:
    EQUALS = 0
    LESS_THAN = 1
    GREATER_THAN = 2
    NOT_EQUALS = 3
    LESS_THAN_EQUALS = 4
    GREATER_THAN_EQUALS = 5


class Generator:
    def __init__(
            self,
            raw_generator,
            limit=-1,
            initial=[],
            is_numeric_sequence=False,
    ):
        self.next_index = 0
        self.end_reached = False
        self.is_numeric_sequence = is_numeric_sequence
        self.do_print = True
        if "__name__" in dir(raw_generator) and type(raw_generator) != Python_Generator:
            if raw_generator.__name__.startswith(
                    "FN_"
            ) or raw_generator.__name__.startswith("_lambda"):
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
            elif type(raw_generator) is Function:
                self.gen = raw_generator()
            else:

                def gen():
                    index = 0
                    while True:
                        yield raw_generator(index)
                        index += 1

                self.gen = gen()
        else:

            def niceify(item):
                t_item = vy_type(item)
                if t_item not in [Generator, list, Number, str]:
                    return list(item)
                return item

            self.gen = map(niceify, raw_generator)
        self.generated = []

    def __call__(self, *args, **kwargs):
        return self

    def __contains__(self, item):
        if self.is_numeric_sequence:
            if item in self.generated:
                return True
            temp = next(self)
            while temp <= item:
                temp = next(self)
            return item in self.generated
        else:
            for temp in self:
                if item == temp:
                    return True
            return False

    def __getitem__(self, position):
        if type(position) is slice:
            ret = []
            stop = position.stop or self.__len__()
            start = position.start or 0

            if start < 0 or stop < 0:
                self.generated += list(self.gen)
                return self.generated[position]
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
            self.generated += list(self.gen)
            return self.generated[position]
        if position < len(self.generated):
            return self.generated[position]
        while len(self.generated) < position + 1:
            try:
                self.__next__()
            except:
                self.end_reached = True
                position = position % len(self.generated)

        return self.generated[position]

    def __setitem__(self, position, value):
        if position >= len(self.generated):
            self.__getitem__(position)
        self.generated[position] = value

    def __len__(self):
        return len(self._dereference())

    def __next__(self):
        f = next(self.gen)
        self.generated.append(f)
        return f

    def __iter__(self):
        return self

    def _filter(self, function):
        index = 0
        l = self.__len__()
        while True:
            if index == l:
                break
            obj = self.__getitem__(index)
            ret = safe_apply(function, obj)
            if ret:
                yield obj
            index += 1

    def _reduce(self, function):
        def ensure_singleton(function, left, right):
            ret = safe_apply(function, left, right)
            if type(ret) in [Generator, list]:
                return ret[-1]
            return ret

        return functools.reduce(
            lambda x, y: ensure_singleton(function, x, y), self._dereference()
        )

    def _dereference(self):
        """
        Only call this when it is absolutely neccesary to convert to a list.
        """
        d = self.generated + list(self.gen)
        self.gen = iter(d[::])
        self.generated = []
        return d

    def _print(self, end="\n"):
        from vyxal.builtins import vy_print

        main = self.generated
        try:
            f = next(self)
            # If we're still going, there's stuff in main that needs printing before printing the generator
            vy_print("⟨", end="")
            for i in range(len(main)):
                vy_print(main[i], end="|" * (i >= len(main)))
            while True:
                try:
                    f = next(self)
                    vy_print("|", end="")
                    vy_print(f, end="")
                except:
                    break
            vy_print("⟩", end=end)

        except:
            vy_print(main, end=end)

    def zip_with(self, other):
        return Generator(zip(self.gen, iter(other)))

    def safe(self):
        import copy

        return copy.deepcopy(self)

    def __str__(self):
        return "⟨" + "|".join(str(item for item in self.generated)) + "...⟩"

    def limit_to_items(self, n):
        out = "⟨"
        item_count = 0
        while not self.end_reached and item_count <= n:
            item = self.__getitem__(item_count)
            if self.end_reached:
                break
            out += (
                str(item) if vy_type(item) is not Generator else item.limit_to_items(n)
            )
            item_count += 1
            out += "|"

        if item_count > n:
            out += "..."

        return out + "⟩"


class ShiftDirections:
    LEFT = 1
    RIGHT = 2


# Helper functions
def deep_vectorised(fn):
    def vectorised_fn(first, second=None, third=None):
        types = map(vy_type, [first, second, third])
        any_list = any(typ is list for typ in types)
        any_gen = any(typ is Generator for typ in types)
        if any_list or any_gen:
            res = vectorise(fn, second, first, third)
            if any_gen:
                return res
            else:
                return res._dereference()
        elif third is not None:
            return fn(first, second, third)
        elif second is not None:
            return fn(first, second)
        else:
            return fn(first)

    return vectorised_fn


def safe_apply(function, *args):
    """
    Applies function to args that adapts to the input style of the passed function.

    If the function is a _lambda (it's been defined within λ...;), it passes a list of arguments and length of argument list
    Otherwise, if the function is a user-defined function (starts with FN_), it simply passes the argument list
    Otherwise, unpack args and call as usual
    """

    if function.__name__.startswith("_lambda"):
        ret = function(list(args), len(args), function)
        if len(ret):
            return ret[-1]
        else:
            return []
    elif function.__name__.startswith("FN_"):
        ret = function(list(args))[-1]
        if len(ret):
            return ret[-1]
        else:
            return []
    return function(*args)


def _mangle(value):
    byte_list = bytes(value, encoding="utf-8")
    return base64.b32encode(byte_list).decode().replace("=", "_")


def two_argument(function, left, right):
    """
    Used for vectorising user-defined lambas/dyads over generators
    """
    if function.__name__.startswith("_lambda"):
        return Generator(map(lambda x: function(x, arity=2), vy_zip(left, right)))
    return Generator(map(lambda x: function(*x), vy_zip(left, right)))


def to_ten(number, custom_base):
    # custom to ten
    # Turns something like 20 in base 5 to 10 in base 10
    # (int, int): uses an arbitrary base, and treats as a list of digits
    # (str, str): uses the provided base
    # (list, int): uses an arbitrary base, and treats as a list of digits
    # (str, int): uses an arbitrary base, and treats as a list of codepoints
    # (int, str): what the actual frick.
    # always returns Number

    result = 0
    alphabet = (lambda: custom_base, lambda: range(0, int(custom_base)))[
        type(custom_base) in (int, float)
        ]()
    base_exponent = len(alphabet)
    number = list(
        (lambda: number, lambda: map(int, str(int(number))))[
            type(number) in (int, float)
            ]()
    )
    power = 0
    for digit in reversed(number):
        if digit in alphabet:
            result += alphabet.index(digit) * (base_exponent ** power)
        else:
            result += -1 * (base_exponent ** power)
        power += 1

    return result


def from_ten(number, custom_base):
    # ten to custom
    # Turns something like 10 in base 10 to 20 in base 5
    # (int, int): use an arbitrary base, return a list of digits
    # (int, str): use provided base, return a string
    # (int, list): use provided base, return a list of "digits"
    # (non-int, any): what the actual frick.

    import math

    if type(number) not in (int, float):
        return number

    if type(custom_base) in (int, float):
        custom_base = range(0, int(custom_base))

    result = ([], "")[isinstance(custom_base, str)]
    append = (lambda x: result + [x], lambda x: result + x)[isinstance(result, str)]
    base_exponent = len(custom_base)
    temp = number
    power = int(math.log(number if number else 1) / math.log(base_exponent))

    while power >= 0:
        interesting_part, temp = divmod(temp, base_exponent ** power)
        result = append(custom_base[interesting_part])
        power -= 1

    if temp == 0:
        result

    return result


def iterable(item, t=None):
    t = t or vy_globals.number_iterable
    if vy_type(item) == Number:
        if t is list:
            return [int(let) if let not in "-." else let for let in str(item)]
        if t is range:
            return Generator(
                range(
                    vy_globals.MAP_START,
                    int(item) + vy_globals.MAP_OFFSET,
                )
            )
        return t(item)
    else:
        return item


def uncompress(s):
    final = ""
    current_two = ""
    escaped = False

    from vyxal import encoding

    for char in s:
        if escaped:
            if char not in encoding.compression:
                final += "\\"
            final += char
            escaped = False
            continue

        elif char == "\\":
            escaped = True
            continue

        elif char in encoding.compression:
            current_two += char
            if len(current_two) == 2:
                if to_ten(current_two, encoding.compression) < len(words._words):

                    final += words.extract_word(current_two)
                else:
                    final += current_two

                current_two = ""
            continue

        else:
            final += char

    return final.replace("\n", "\\n").replace("\r", "")


def vectorise(fn, left, right=None, third=None, explicit=False):
    if third is not None:
        types = (vy_type(left), vy_type(right))

        def gen():
            for pair in vy_zip(right, left):
                yield safe_apply(fn, third, *pair)

        def expl(l, r):
            for item in l:
                yield safe_apply(fn, third, r, item)

        def swapped_expl(l, r):
            for item in r:
                yield safe_apply(fn, third, item, l)

        ret = {
            (types[0], types[1]): (
                lambda: safe_apply(fn, left, right),
                lambda: expl(vyxal.array_builtins.iterable(left), right),
            ),
            (list, types[1]): (
                lambda: [safe_apply(fn, x, right) for x in left],
                lambda: expl(left, right),
            ),
            (types[0], list): (
                lambda: [safe_apply(fn, left, x) for x in right],
                lambda: swapped_expl(left, right),
            ),
            (Generator, types[1]): (
                lambda: expl(left, right),
                lambda: expl(left, right),
            ),
            (types[0], Generator): (
                lambda: swapped_expl(left, right),
                lambda: swapped_expl(left, right),
            ),
            (list, list): (lambda: gen(), lambda: expl(left, right)),
            (Generator, Generator): (lambda: gen(), lambda: expl(left, right)),
            (list, Generator): (lambda: gen(), lambda: expl(left, right)),
            (Generator, list): (lambda: gen(), lambda: expl(left, right)),
        }[types][explicit]()

        if type(ret) is Python_Generator:
            return Generator(ret)
        else:
            return ret
    elif right is not None:
        types = (vy_type(left), vy_type(right))

        def gen():
            for pair in vy_zip(left, right):
                yield safe_apply(fn, *pair[::-1])

        def expl(l, r):
            for item in l:
                yield safe_apply(fn, item, r)

        def swapped_expl(l, r):
            for item in r:
                yield safe_apply(fn, l, item)

        ret = {
            (types[0], types[1]): (
                lambda: safe_apply(fn, left, right),
                lambda: expl(vyxal.array_builtins.iterable(left), right),
            ),
            (list, types[1]): (
                lambda: [safe_apply(fn, x, right) for x in left],
                lambda: expl(left, right),
            ),
            (types[0], list): (
                lambda: [safe_apply(fn, left, x) for x in right],
                lambda: swapped_expl(left, right),
            ),
            (Generator, types[1]): (
                lambda: expl(left, right),
                lambda: expl(left, right),
            ),
            (types[0], Generator): (
                lambda: swapped_expl(left, right),
                lambda: swapped_expl(left, right),
            ),
            (list, list): (lambda: gen(), lambda: expl(left, right)),
            (Generator, Generator): (lambda: gen(), lambda: expl(left, right)),
            (list, Generator): (lambda: gen(), lambda: expl(left, right)),
            (Generator, list): (lambda: gen(), lambda: expl(left, right)),
        }[types][explicit]()

        if type(ret) is Python_Generator:
            return Generator(ret)
        else:
            return ret

    else:
        if vy_type(left) is Generator:

            @Generator
            def gen():
                for item in left:
                    yield safe_apply(fn, item)

            return gen()
        elif vy_type(left) in (str, Number):
            return safe_apply(fn, list(vyxal.array_builtins.iterable(left)))
        else:
            ret = [safe_apply(fn, x) for x in left]
            return ret


def vy_repr(item):
    t_item = vy_type(item)
    return {
        Number: str,
        list: lambda x: "⟨" + "|".join([str(vy_repr(y)) for y in x]) + "⟩",
        Generator: lambda x: vy_repr(x._dereference()),
        str: lambda x: "`" + x + "`",
        Function: lambda x: "@FUNCTION:" + x.__name__,
    }[t_item](item)


def vy_type(item):
    ty = type(item)
    if ty in [int, float, complex]:
        return Number
    return ty


def vy_zip(lhs, rhs):
    ind = 0
    if type(lhs) in [list, str]:
        lhs = iter(lhs)
    if type(rhs) in [list, str]:
        rhs = iter(rhs)
    while True:
        exhausted = 0
        try:
            left = next(lhs)
        except:
            left = 0
            exhausted += 1

        try:
            right = next(rhs)
        except:
            right = 0
            exhausted += 1
        if exhausted == 2:
            break
        yield [left, right]
        ind += 1


if __name__ == "__main__":
    import encoding

    while 1:
        word = input(">>> ")
        if word.isnumeric():
            print("»" + from_ten(int(word), encoding.codepage_number_compress) + "»")

        else:
            try:
                if type(eval(word)) is list:
                    charmap = dict(zip("0123456789,", "etaoinshrd "))
                    word = word.replace(" ", "").replace("[", "").replace("]", "")
                    out = ""
                    for char in word:
                        out += charmap.get(char, "")
                    c = from_ten(
                        to_ten(out, base27alphabet), encoding.codepage_string_compress
                    )
                    print("«" + c + "«ũ")
                    print()
                    print(repr(c))
                else:
                    print(
                        "«"
                        + from_ten(
                            to_ten(word, base27alphabet),
                            encoding.codepage_string_compress,
                        )
                        + "«"
                    )
            except:
                print(
                    "«"
                    + from_ten(
                        to_ten(word, base27alphabet), encoding.codepage_string_compress
                    )
                    + "«"
                )
