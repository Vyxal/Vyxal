# `ⁱ` - From Until End

- Arity: 2
- In: a, b
- Out: a[b:]

Allows one to do `a[b:]` (usual `i` only allows for `a[:b]`, `a[b:c]`, `a[b:c:d]`)

# Usage
```
`abcdef` 3ⁱ║⟨`def`⟩
1 10r 6 ⁱ  ║⟨⟨7|8|9⟩⟩
```
