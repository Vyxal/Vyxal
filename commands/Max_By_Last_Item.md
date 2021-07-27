# `⎠` - action

- Arity: 1
- In: a
- Out: max(a, key=lambda x: x[-1])

Returns the maximum item in an iterable based on the last item. This is mostly intended to be used with zipmaps (made using the `z` command)


# Usage
```
1 10r λn3+n5-*;z⎠║⟨⟨9|48⟩⟩
`abcdef`λ\d=;z⎠  ║⟨⟨`d`|1⟩⟩
```
