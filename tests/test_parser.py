import os
import sys

THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)

from vyxal.lexer import *
from vyxal.parser import *


if __name__ == "__main__":  # For testing outside of the workflow
    print(tokenise("1 1+"))
    print(parse(tokenise("1 1 +")))

    print(tokenise("₁ƛ₍₃₅kF½*∑∴"))
    print(parse(tokenise("₁ƛ₍₃₅kF½*∑∴")))
