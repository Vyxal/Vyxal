# `Ð` - To Base Ten

- Arity: 2
- In: a, b
- Out: int(a, alphabet/base=b)

Returns `a` converted to decimal from a custom base `b`. If `b` is a string, then it is the "alphabet" of `a`: allowing for bijective bases to easily be defined. If `a` is a list/generator and `b` is an integer, then the list is converted from base `b`.

# Usage
```
`dab`k6Ð         ║⟨3499⟩
`heck``HhEeCcKk`Ð║⟨751⟩
1 10r 4Ð         ║⟨116505⟩
```
