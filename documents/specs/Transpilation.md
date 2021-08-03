# Turning Bytes Into Code - the Guide to Transpiling Elements and Structures That I Wrote on a Tuesday Morning/Afternoon

When doing the actual code transpilation, there are templates that need to be followed in order to make transpiled code consistent and clear. This document relates
to what gets executed after transpilation, not the element functions.

## Elements

There will be a function called `process_element` that will take a single expression (called `expr`) as a string (don't pass raw code). 
`expr` must be a python _expression_, not a _statement_; an easy way to think of this is that you must be able to put `expr` inside a 
lambda without syntax errors. The function wraps `expr` in one of the following templates:

### Nilads

```python
stack.append(expr)
```

### Monads

```python
lhs = pop(stack); stack.append(expr)
```

### Dyads

```python
rhs, lhs = pop(stack, 2); stack.append(expr)
```

### Triads

```python
third, rhs, lhs = pop(stack, 3); stack.append(expr)
```

