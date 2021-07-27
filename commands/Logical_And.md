# `∧` - Logical And

- Arity: 2
- In: a, b
- Out: a and b

Returns short-circuited `a and b`. Does not vectorise

# Usage
```
0 1 ∧       ║⟨0⟩
`abc` `def`∧║⟨`def`⟩
```

# Notes

`⟑` performs the same as `∧` but with reversed arguments (`b and a` instead of `a and b`)
