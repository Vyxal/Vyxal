# `<` - Less Than

- Arity: 2
- In: a, b
- Out: a < b

Takes two values and returns whether or not a is less than b. Strings are compared by unicode charpoint value.


# Usage
```
1 2 <            ║⟨1⟩
`def` `abc` <    ║⟨0⟩
1 2 3 4 W 5 9 r <║⟨1|1|1|1⟩ # Vectorised
```
