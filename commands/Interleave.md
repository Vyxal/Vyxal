# `Y` - Interleave

- Arity:2
- In: a, b
- Out: interleaved(a, b)

Takes two items and Interleaves them into a single iterable. Has depth of 1.

# Usage
```
`abc` `def` Y║⟨`adbecf`⟩
1 10r 2 11r Y║⟨⟨1|2|2|3|3|4|4|5|5|6|6|7|7|8|8|9|9|10⟩⟩
`abc``defg`Y ║⟨`adbecfg`⟩
```


# Alternate Representations
```
Zf
```
