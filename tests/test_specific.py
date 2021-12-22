# Test specific test cases that can't be put in the yaml easy

import os
import sys
import sympy
THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + '/..'
sys.path.insert(1, THIS_FOLDER)

import vyxal.main
from vyxal.transpile import *
from vyxal.elements import *
from vyxal.context import Context
from vyxal.helpers import *
from vyxal.LazyList import *

def test_exec():
    stack = []
    ctx = Context()

    ctx.stacks.append(stack)

    vyxal.main.execute_vyxal("tests/exec_test.vy", "a", """6,10
0,14
9,10
0,3
10,4
4,11
6,0
6,12
4,1
0,13
10,12
3,4
3,0
8,4
1,10
2,14
8,10
9,0

fold along y=7
fold along x=5""".split("\n"))
