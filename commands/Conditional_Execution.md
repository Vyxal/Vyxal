# `ß` - Conditional Execution

- Arity: 1 + *
- In: a, *
- Out: if a: <built-in>

Performs the next command if the top the stack is truthy

# Usage
```
1 10r 0 ßL║⟨⟨1|2|3|4|5|6|7|8|9⟩⟩
1 10r 1 ßL║⟨9⟩
```

# Cohesion

|a v          b >| Number | String | List/Generator |
|----------------|--------|--------|----------------|
| Number         |        |        |                |
| String         |        |        |                |
| List/Generator |        |        |                |

# Alternate Representations

```
[<built-in>]
```
