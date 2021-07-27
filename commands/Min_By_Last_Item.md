# `⎝` - Min by Last Item

- Arity: 1
- In: a
- Out: min(a, key=lambda x: x[-1])

Returns the minimum item in an iterable based on the last item. This is mostly intended to be used with zipmaps (made using the `z` command)

# Usage
```
1 10r λn3+n5-*;z⎝║⟨⟨1|-16⟩⟩
`abcdef`λ\d=;z⎝  ║⟨⟨`a`|0⟩⟩
```
