# `z` - Zipmap

- Arity: 2
- In: a (vector), b (function)
- Out: zip(a, map(b, a))

Returns vector `a` zipped with the result of mapping function `b` over `a`.

# Usage
```
1 2 3 W ․dz     ║⟨⟨⟨1|2⟩|⟨2|4⟩|⟨3|6⟩⟩⟩
`aBcDe` λkAnc; z║⟨⟨⟨`a`|0⟩|⟨`B`|1⟩|⟨`c`|0⟩|⟨`D`|1⟩|⟨`e`|0⟩⟩⟩
`abcde` ․C z    ║⟨⟨⟨`a`|97⟩|⟨`b`|98⟩|⟨`c`|99⟩|⟨`d`|100⟩|⟨`e`|101⟩⟩⟩
```

# Alternate Representations

```
↜↭MZ
```
