# Context

> Functions that require access to flags or functions that directly or indirectly call functions requiring access to flags must be decorated using `@implicits("ctx")`, and their last parameters must be `*` and `ctx`. The `ctx` parameter refers to an object containing settings, the stack, and other values that need to be passed around to various functions. The decorator is from the implicits package. Here's an example:
```python
@implicits("ctx")
def add(lhs, rhs, *, ctx):
  # body of function
```
