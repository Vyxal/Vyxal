# Vectorisation - Done Properly

When vectorising a function, the first thing to be considered is the arity of
the function - mapping a monad works differently to mapping a dyad and a triad.
Then, types of the items need to be considered.

# Nilads

This just fills the list with the nilad.

# Monads

Numbers are converted to either `range(int(left))` or `digits_of(left)` based on the
flags used.

Then, it's just roughly equivalent to:

```python
LazyList(map(function, left))
```
# Dyads

```python
LazyList(map(lambda x: apply(*x), vy_zip(left, right)))
```

# Triads

```python
LazyList(map(lambda x, right, other: apply(x, right, other), left))
```
