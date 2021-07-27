# `M` - Map

- Arity: 2
- In: iterable, function
- Out: map(fuction, iterable)

Maps the function over the iterable. If `iterable` is an integer, then generate a range [0, iterable). This range can be modified using command line flags.

# Usage
```
1 10r ․dM  ║⟨⟨2|4|6|8|10|12|14|16|18⟩⟩
1 10r λ3+;M║⟨⟨4|5|6|7|8|9|10|11|12⟩⟩
```
