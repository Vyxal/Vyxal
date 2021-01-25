# `>` - Greater Than

- Arity: 2
- In: a, b
- Out: a > b

Takes two values and returns whether or not a is greater than b. Strings are compared by unicode charpoint value.


# Usage
```
1 2 >            ║⟨0⟩
`def` `abc` >    ║⟨1⟩
1 2 3 4 W 5 9 r >║⟨0|0|0|0⟩ # Vectorised
```
