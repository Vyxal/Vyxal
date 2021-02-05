# `≕` - Vectorising Equals

- Arity: 2
- In: a, b
- Out: [n == m for n,m in zip(a, b)]

Returns `a == b` but vectorised element-wise. If neither `a` or `b` is a list/generator, perform normal equality.

# Usage
```
1 2 3 4 5 W 1 6 r ≕║⟨⟨1|1|1|1|1⟩⟩
`abc``abd` ≕       ║⟨0⟩
1 10r 2% 1  ≕      ║⟨⟨1|0|1|0|1|0|1|0|1⟩⟩
```
