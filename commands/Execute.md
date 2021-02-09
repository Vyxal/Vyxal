# `ₑ` - Execute

- Arity: 1
- In: a
- Out: exec(VY_compile(a))

Executes `a` as if it was a snippet of Vyxal code.

# Usage
```
`1 1+`ₑ  ║⟨2⟩
`1 1+`ₑ1+║⟨3⟩
`10(n,)`ₑ║⟨⟩ STDOUT = "0\n1\n...\n8\n9"
```
