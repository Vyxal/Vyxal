# `i` - Index

- Arity: 2
- In: a, b
- Out: a[b]

Returns the `b`th element of `a`. If `b` is a vector, then it acts as a splice:

```
⟨n⟩: a[:n]
⟨n|m⟩: a[n:m]
⟨n|m|s⟩: a[n:m:s]
```

For `a[b:]`, use `ⁱ`
# Usage
```
`abcdefgh`3i     ║⟨`d`⟩
`abcdefgh`⟨3⟩i    ║⟨`abc`⟩
`abcdefgh`⟨3|6⟩i  ║⟨`def`⟩
`abcdefgh`⟨3|6|2⟩i║⟨`df`⟩
```
