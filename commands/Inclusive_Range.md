# `⁂` - Inclusive Range

- Arity: 2
- In: a, b
- Out: the range `[a, b]`

Returns a range between `a` and `b`, including both end points in the range. If either value passed isn't a number, convert both to strings and return `a` split on regex `b`.

# Usage
```
1 10⁂             ║⟨⟨1|2|3|4|5|6|7|8|9|10⟩⟩
`abc123def``\\d+`⁂║⟨⟨`abc`|`def`⟩⟩
```
