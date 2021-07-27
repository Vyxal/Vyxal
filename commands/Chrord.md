# `C` - Chr/Ord

- Arity: 1
- In: a
- Out: [ord(b) if type(b) is str else ord(b) for b in a]

When passed an integer, `a` is converted to a character.
When passed a string, each character of `a` is converted to it's unicode codepoint.
Vectorises if needed

# Usage
```
69C                     ║⟨"E"⟩
`Heck`C                 ║⟨72|101|99|107⟩
⟨72|101|99|107|\O|\f|\f⟩C║⟨`H`|`e`|`c`|`k`|79|102|102⟩
```
