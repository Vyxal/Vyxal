# `R` - Reduce

- Arity: 2
- In: iterable, function
- Out: reduce(function, iterable)

Reduces the iterable by the given function. Similar to writing `f(f(f(f(f(x[0], x[1]), x[2]), x[3]), x[...]), x[-1])`

# Usage
```
1 10 r ․+ R║⟨45⟩
1 10 r ․* R║⟨362880⟩
```
