# Contextual Object

One of the reasons the original code clean-up failed is that the one-file interpreter relied too heavily on global variable; in order to access values such as flag settings, input levels and context values (the ones used with `n`), variables had to be scoped as `global` within functions. Ignoring the fact that global variables are generally a bad programming practice, this was an issue because you can't access global variables from one file inside another file.

The best solution to this is to have a special `Context` class that contains all the variables that were previously global. The definition of the class might look like:

```python
class Context:
    def __init__(self):
        self.stack = []
        self.register = 0
        # and so on
```

At the start of the interpreter, an instance of `Context` called `ctx` (think `ctx = Context()`) will be created. This will need to be passed between element functions, meaning they all need a `ctx` parameter.

However, we're yet to decide whether this should happen explicitly or implicitly.

---
## The implicit way
> Functions that require access to flags or functions that directly or indirectly call functions requiring access to flags must be decorated using `@implicits("ctx")`, and their last parameters must be `*` and `ctx`. The `ctx` parameter refers to an object containing settings, the stack, and other values that need to be passed around to various functions. The decorator is from the implicits package. Here's an example:
```python
@implicits("ctx")
def add(lhs, rhs, *, ctx):
  # body of function
```


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

## The Explicit Way

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
x = elements.pop(lst, 2, ctx)
print(x, lst)
```

(and this could be `elements.py`)

```python
from implicits import implicits

def pop(stack, n=1, *, ctx):
    ret = [stack.pop() for i in range(n)]
    if ctx.reverse_arguments: ret = ret[::-1]
    return ret
```
