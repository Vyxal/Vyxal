# `U` - Uniquify

- Arity: 1
- In: a
- Out: uniquified(a)

Returns `a` with one instance of each item. Does not vectorise.

# Usage
```
⟨2|9|3|8|5|2⟩U║⟨⟨2|9|3|8|5⟩⟩
`abcabcabca`U║⟨⟨`a`|`b`|`c`⟩⟩
69696969U    ║⟨⟨6|9⟩⟩
```
