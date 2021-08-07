# Context

> Functions that require access to flags or functions that directly or indirectly call functions requiring access to flags must be decorated using `@implicits("ctx")`, and their last parameters must be `*` and `ctx`. The `ctx` parameter refers to an object containing settings, the stack, and other values that need to be passed around to various functions. The decorator is from the implicits package. Here's an example:
```python
@implicits("ctx")
def add(lhs, rhs, *, ctx):
  # body of function
```

## Relevant Vyxal Example

(for example, this could be `vyxal.py`)

```python
# Simulate main.py

from implicits import implicits

import elements


class Context:
    def __init__(self):
        self.reverse_arguments = False


ctx = Context()
ctx.reverse_arguments = False

lst = [1, 2, 3, 4]
print(lst)
x = elements.pop(lst, 2)
print(x, lst)
```

(and this could be `elements.py`)

```python
from implicits import implicits

@implicits("ctx")
def pop(stack, n=1, *, ctx):
    ret = [stack.pop() for i in range(n)]
    if ctx.reverse_arguments: ret = ret[::-1]
    return ret
```
