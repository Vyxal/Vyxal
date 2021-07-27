# `Ė` - Execute

- Arity: 1
- In: a
- Out: exec(vy_compile(a))

Executes `a` as if it was a snippet of Vyxal code.

# Usage

```
`1 1+`Ė  ║⟨2⟩
`1 1+`Ė1+║⟨3⟩
`10(n,)`Ė║⟨⟩ STDOUT = "0\n1\n...\n8\n9"
```
