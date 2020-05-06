# Vyxal to Python Transpilation Guide
Like Keg, Vyxal will be simply turned into Python code and then `exec`'d to produce a result. Here's a translation table for how that will happen

## IO
### Printing
#### Vyxal
    , # Print nicely
    . # Print rawly
    ? # Take input
    
#### Python

    print(_Vy_nice(stack.pop()))
    print(stack.pop())
    stack.push(_Vy_input())

