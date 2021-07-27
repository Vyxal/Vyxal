# `E` - Evaluate

- Arity: 1
- In: a
- Out: python_eval(a)

When using the offline interpreter, standard `eval` is called, consequently evaluating the value as python code. When using the online interpreter, the value is safely evaluated if it is considered a string, list or a number.

# Usage
```
`print((6 * 9) + (6 + 9))`E║⟨⟩ STDOUT = "69" # Offline only
`[1, 2, 3, 4, 5]`E║⟨1|2|3|4|5⟩ # Offline and online
```
