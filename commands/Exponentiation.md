# `e` - Exponentiation

- Arity: 2
- In: a, b
- Out: a ** b

Returns a raised to the power of b.

If `a` is a string and `b` is in the range `(0, 1)`, return every `1/b`th character


# Cohesion
| a v          b > | Number                | String                                | List/Generator                     |
|------------------|-----------------------|---------------------------------------|------------------------------------|
| Number           | `a ** b`              | `b multplied by itself a times`       | `a ** b` (vectorised)              |
| String           | `a multplied by itself b times` | `list(re.compile(a).match(b).span())` | `a ** b` (vectorised)              |
| List/Generator   | `a ** b` (vectorised) | `a ** b` (vectorised)                 | `a ** b` (vectorised element-wise) |
