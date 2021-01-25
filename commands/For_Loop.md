# `()` - For loop

- Arity: 1
- In: any
- Out: NA

A for loop takes the top of the stack and iterates through each item. If the top of the stack is numeric, a range between 0 and that number truncated is generated and used as the iterator.


# Syntax

```
(variable|body)
```

Iterate over the top of the stack, storing the iteration variable in `variable` and executing the body each time. Analogous to:

```py
for variable in iterable(top_of_stack):
  # body
```

```
(body)
```

Same as the two component for-loop, but the iteration value is stored in the context variable. Analogous to:

```py
for context_variable in iterable(top_of_stack):
  # body
```
