# `W` - Wrap Stack

- Arity: *
- In: the entire stack
- Out: [stack]

Takes the entire stack and wraps it into a single list. Useful for creating a list of several items when there isn't anything else on the stack

# Usage
```
1 2 3 4 5 6 7 W║⟨⟨1|2|3|4|5|6|7⟩⟩
1 2 3 W 4 5 6 W║⟨⟨⟨1|2|3⟩|4|5|6⟩⟩
69 `abc` W 3432║⟨⟨69|`abc`⟩|3432⟩
```
