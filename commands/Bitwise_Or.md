# `⋎` - Bitwise Or

- Arity: 2
- In: a, b
- Out: a | b

Returns bitwise or of `a` and `b`. Analogous to `a | b` in python.

# Cohesion

| a v          b > | Number                            | String                        | List/Generator                     |
|------------------|-----------------------------------|-------------------------------|------------------------------------|
| Number           | `a \| b`                          | `"".join([c * a for c in b])` | `a \|  b` (vectorised)             |
| String           | `"".join([c * rhs for c in lhs])` | `merged(a, b)`                | `a \| b` (vectorised)              |
| List/Generator   | `a \| b` (vectorised)             | `a \| b` (vectorised)         | `a \| b` (vectorised element-wise) |
