# `⁺` - Codepage Constant

- Arity: 0
- In: NA
- Out: codepage.index(next_character) + 101

Returns the position of the next character in the code page with 101 added. This is because every number below 100 can be pushed in 2 bytes or less.

# Usage
```
⁺£║⟨282⟩
⁺e║⟨201⟩
⁺ð║⟨236⟩
```
