import argparse

from . import coder
from .predictions import pair_frequency2
from .codepage import codepage


def vyxal_to_int(s):
    return [codepage.find(c) for c in s]


def int_to_vyxal(lst):
    return "".join(codepage[x] for x in lst)


pred = pair_frequency2(16, 128)


def encode():
    parser = argparse.ArgumentParser(
        description="Encodes Vyxal code as a bit string."
    )
    parser.add_argument("code", type=str, help="Vyxal code to be encoded")
    parser.add_argument(
        "-v",
        dest="verbose",
        action="store_const",
        const=sum,
        default=max,
        help="verbose",
    )

    args = parser.parse_args()

    int_lst = vyxal_to_int(args.code)
    bin_lst = coder.encode(int_lst, pred)

    if args.verbose:
        print(
            f"input size: {len(int_lst)*8} bits ({len(int_lst)} bytes), output size: {len(bin_lst)} bits ({len(bin_lst)/8} bytes), ratio: {len(bin_lst)/(len(int_lst)*8)}"
        )

    print("".join(str(b) for b in bin_lst))


def decode():
    parser = argparse.ArgumentParser(
        description="Decodes Vyxal code from a bit string."
    )
    parser.add_argument("str", type=str, help="string to be decoded")

    args = parser.parse_args()

    int_lst = coder.decode([int(c) for c in args.str], pred)

    print(int_to_vyxal(int_lst))
