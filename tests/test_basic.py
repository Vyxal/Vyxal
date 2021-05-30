# Simple tests

import os, sys
from multiprocessing import Manager
THIS_FOLDER = os.path.dirname(os.path.abspath(__file__)) + "/.."
sys.path.insert(1, THIS_FOLDER)
from Vyxal import *

header = "stack = []\nregister = 0\nprinted = False\n"
manager = Manager()

def run_code(code, flags=[], input_list=[], output_variable=manager.dict()):
    code = VY_compile(code, "global stack, register, printed, output, MAP_START, MAP_OFFSET, _join, _vertical_join, use_encoding, input_level, retain_items, reverse_args, this_function\n")
    context_level = 0
    
    execute(code, flags, input_list, output_variable)
    
    return stack

# This is just a dummy test, it's not feasible to write multiple tests for every single
# overload of every single command
def test_not():
    stack = run_code("2¬")
    assert pop(stack) == 0

def test_is_square():
    stack = run_code("1000'∆²;")
    f = pop(stack)._dereference()
    assert f == [1, 4, 9, 16, 25, 36, 49, 64, 81, 100, 121, 144, 169, 196, 225, 256, 289, 324, 361, 400, 441, 484, 529, 576, 625, 676, 729, 784, 841, 900, 961]

fizzbuzz_output = [1,2,"Fizz",4,"Buzz","Fizz",7,8,"Fizz","Buzz",11,"Fizz",13,14,"FizzBuzz",16,17,"Fizz",19,"Buzz","Fizz",22,23,"Fizz","Buzz",26,"Fizz",28,29,"FizzBuzz",31,32,"Fizz",34,"Buzz","Fizz",37,38,"Fizz","Buzz",41,"Fizz",43,44,"FizzBuzz",46,47,"Fizz",49,"Buzz","Fizz",52,53,"Fizz","Buzz",56,"Fizz",58,59,"FizzBuzz",61,62,"Fizz",64,"Buzz","Fizz",67,68,"Fizz","Buzz",71,"Fizz",73,74,"FizzBuzz",76,77,"Fizz",79,"Buzz","Fizz",82,83,"Fizz","Buzz",86,"Fizz",88,89,"FizzBuzz",91,92,"Fizz",94,"Buzz","Fizz",97,98,"Fizz","Buzz"]
def test_fizzbuzz():
    stack = run_code("₁ƛ₍₃₅kF½*ṅ⟇", flags=['j'])
    f = pop(stack)._dereference()
    assert f == fizzbuzz_output
