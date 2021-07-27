# `⳹` - Integer Division

- Arity: 2
- In: a, b
- Out: a // b

Returns the result of "integer" dividing `a` and `b`. Inspired by the idea that integer division seems to truncate the result of dividing `a` and `b`.

# Cohesion

|a v          b >| Number   | String | List/Generator |
|----------------|----------|--------|----------------|
| Number         |`a // b`  |`(b/a)[0]`|`a // b` (vectorised)|
| String         |`(a/b)[0]`|`(b/a)[0]`|`a // b ` (vectorised)              |
| List/Generator |`a // b` (vectorised)| `a // b` (vectorised)       |`a // b` (vectorised element-wise)                |
