# `⊑` - Prepend

- Arity: 2
- In: a, b
- Out: a.insert(0, b)

Inserts `b` at the first position in `a`.

# Usage
```
1 10r 4⊑    ║⟨⟨4|1|2|3|4|5|6|7|8|9⟩⟩
`abcdef``l`⊑║⟨`labcdef`⟩
32048 4⊑    ║⟨⟨4|3|2|0|4|8⟩⟩
```
