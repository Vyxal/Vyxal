# `≕` - Non-Vectorising Equals

- Arity: 2
- In: a, b
- Out: a == b

Returns `a == b` but does not vectorise at all.

# Usage
```
69 69 ≕             ║⟨1⟩
`def` `abc` ≕       ║⟨0⟩
1 2 3 4 W ⟨1|4|3|2⟩ ≕║⟨0⟩
```
