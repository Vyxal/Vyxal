# `Ṛ` - Random Between

- Arity: 2
- In: a,b
- Out: random_between(a, b)

Returns a random value between a and b if both values are integers. Otherwise, returns a random item from `[a, b]`

# Usage
```
2 10 Ṛ      ║⟨6⟩ # Results will vary
`abc` `def`Ṛ║⟨`abc`⟩ OR ⟨`def`⟩
1 10r:Ṛ     ║⟨⟨1|2|3|4|5|6|7|8|9⟩⟩ # Deterministic
```
