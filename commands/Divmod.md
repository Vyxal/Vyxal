# `ḋ` - Divmod

- Arity: 2
- In: a, b
- Out: [a // b, a % b]

Returns a list of `integer_divide(a, b), modulo(a, b)`.

# Usage
```
10 4ḋ      ║⟨⟨2|2⟩⟩
`abcdef` 3ḋ║⟨⟨`ab`|`ef`⟩⟩
1 10r 2ḋ   ║⟨⟨⟨0|1|1|2|2|3|3|4|4⟩|⟨1|0|1|0|1|0|1|0|1⟩⟩⟩
```

# Alternate Representations

```
₍ḭ%
```
