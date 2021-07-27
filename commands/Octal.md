# `Ȯ` - Octal Representation

- Arity: 1
- In: a
- Out: oct(a)

Returns octal representation of `a`. If `a` is string, try to cast to number and get octal, otherwise, return `a[2:]`. Vectorises if needed

# Usage
```
23Ȯ   ║⟨`27`⟩
`23`Ȯ ║⟨`27`⟩
`abc`Ȯ║⟨`c`⟩
```
