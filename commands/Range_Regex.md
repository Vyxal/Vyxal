# `r` - Range/Regex

- Arity: 2
- In: a, b
- Out: range(a, b)

Returns a range from `a` to `b` if both `a` and `b` are numbers. Otherwise, converts both strings and returns if `b` matches the pattern `a`

# Usage
```
`\\d+` 69 r ║⟨1⟩
1 10 r      ║⟨⟨1|2|3|4|5|6|7|8|9⟩⟩
10 1 r      ║⟨⟨10|9|8|7|6|5|4|3|2⟩⟩
```
