# `ĸ` - Distribute

- Arity: 2
- In: a, b
- Out: b distributed evenly over a

Returns `a` with `b` spread over each element: think of it as incrementing each element of `a` until the number of increments equals `b`.

E.g. `1 6r 14ĸ`
```
a = ⟨1|2|3|4|5⟩ b = 14 next_index = 0
a = ⟨2|2|3|4|5⟩ b = 13 next_index = 1
a = ⟨2|3|3|4|5⟩ b = 12 next_index = 2
a = ⟨2|3|4|4|5⟩ b = 11 next_index = 3
a = ⟨2|3|4|5|5⟩ b = 10 next_index = 4
a = ⟨2|3|4|5|6⟩ b = 9 next_index = 0 # (Wraps around to the start because b > 0)
a = ⟨3|3|4|5|6⟩ b = 8 next_index = 1
.
.
.
a = ⟨3|4|5|6|7⟩ b = 4 next_index = 0
a = ⟨4|4|5|6|7⟩ b = 3 next_index = 1
a = ⟨4|5|5|6|7⟩ b = 2 next_index = 2
a = ⟨4|5|6|6|7⟩ b = 1 next_index = 3
a = ⟨4|5|6|7|7⟩ b = 0 next_index = NA
(Complete)
```
# Usage
```
1 6r 14ĸ     ║⟨⟨4|5|6|7|7⟩⟩
33 32 31 W 3ĸ║⟨⟨34|33|32⟩⟩
```
