# `¦` - Cumulative Sums

- Arity: 1
- In: a
- Out: anti_deltas(a)

Returns the cumulative sums of `a` (`[a[0], sum(a[:1]), sum(a[:2]) ...]`)

# Usage
```
123456¦  ║⟨⟨1|3|6|10|15|21⟩⟩
`abcdef`¦║⟨⟨`a`|`ab`|`abc`|`abcd`|`abcde`|`abcdef`⟩⟩
1 10r¦   ║⟨⟨1|3|6|10|15|21|28|36|45⟩⟩
```
