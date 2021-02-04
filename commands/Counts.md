# `ʗ` - Counts

- Arity: 1
- In: a
- Out: zip(uniquified(a), [a.count(m) for m in uniquified(a)])

Returns the number of occurrences of each unique item in `a`

# Usage
```
`aabccdeE`ʗ║⟨⟨⟨`e`|1⟩|⟨`E`|1⟩|⟨`c`|2⟩|⟨`b`|1⟩|⟨`d`|1⟩|⟨`a`|2⟩⟩⟩
12312332ʗ  ║⟨⟨⟨1|2⟩|⟨2|3⟩|⟨3|3⟩⟩⟩
```
