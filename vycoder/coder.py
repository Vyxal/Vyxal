from typing import *
from numbers import Real
from itertools import accumulate


def bin_list(x: int, len: int) -> list[int]:
    return [int(bool(x & (2**n))) for n in reversed(range(len))]


def from_bin(x: list[int]) -> int:
    return int("".join(str(b) for b in x), base=2) if len(x) else 0


def encode(
    inp: list[int],
    prediction: Callable[[list[int]], list[Real]],
    min_bits: int = 16,
):
    """
    prediction return a list of numbers >=1 where the biggest is < sum/2

    returns the shortest sequence such that sum(x/(2*2**i) for i, x in enumerate(ret) is in the correct range
        and it is the shortest such sequence such that the 2**(-len) is less than or equal to the length of the range
    """
    out = []

    # inclusive range [bottom, top]
    bottom: int = 0
    top: int = 0

    bits = 0
    for i in range(len(inp)):
        bits_to_add = max(min_bits - (top + 1 - bottom).bit_length() + 1, 0)

        bottom *= 2**bits_to_add
        top = (top + 1) * (2**bits_to_add) - 1

        bits += bits_to_add

        ranges = list(accumulate(prediction(inp[:i])))
        ranges = [
            int(y) * (top + 1 - bottom) // int(ranges[-1]) + bottom
            for y in ranges
        ]
        ranges.insert(0, bottom)

        bottom = ranges[inp[i]]
        top = ranges[inp[i] + 1] - 1

        different_bits = (top ^ bottom).bit_length()
        bits_to_store = bits - different_bits

        out += bin_list(top, bits)[:bits_to_store]

        bits = different_bits
        bottom &= 2**bits - 1
        top &= 2**bits - 1

    if bottom == 0:
        if top + 1 != 2**bits:
            out.append(0)
    else:
        out.append(1)
        for _ in range(bits - (top - bottom + 1).bit_length()):
            out.append(0)

    return out


def decode(
    inp: list[int],
    prediction: Callable[[list[int]], list[Real]],
    min_bits: int = 16,
):
    out = []

    # inclusive range [bottom, top]
    bottom: int = 0
    top: int = 0

    bits = 0
    acc = 0
    i = 0
    consumed = 0
    while top - bottom + 1 > 2 ** (i - len(inp) + 1):
        bits_to_add = max(min_bits - (top + 1 - bottom).bit_length() + 1, 0)

        bottom *= 2**bits_to_add
        top = (top + 1) * (2**bits_to_add) - 1
        l = max(min(len(inp) - i, bits_to_add), 0)
        acc = acc * (2**l) + from_bin(inp[i : i + l])
        acc *= 2 ** (bits_to_add - l)
        i += bits_to_add

        bits += bits_to_add

        ranges = list(accumulate(prediction(out)))
        ranges = [
            int(y) * (top + 1 - bottom) // int(ranges[-1]) + bottom
            for y in ranges
        ]

        x = next(j for j in range(len(ranges)) if ranges[j] > acc)
        out.append(x)

        ranges.insert(0, bottom)

        bottom = ranges[x]
        top = ranges[x + 1] - 1

        different_bits = (top ^ bottom).bit_length()
        consumed += bits - different_bits

        bits = different_bits
        bottom &= 2**bits - 1
        top &= 2**bits - 1
        acc &= 2**bits - 1

    return out
