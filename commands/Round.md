# `ṙ` - Round

- Arity: 1
- In: a
- Out: round(a)

Returns `a` rounded according to python's `round` function. If `a` is string, returns suffixes of `a`. Vectorises if needed.

# Usage
```
1.5ṙ       ║⟨2⟩
`abcdefg`ṙ ║⟨⟨`g`|`fg`|`efg`|`defg`|`cdefg`|`bcdefg`|`abcdefg`⟩⟩
1 10r 2/ṙ  ║⟨⟨0|1|2|2|2|3|4|4|4⟩⟩
```
