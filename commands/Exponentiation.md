# `e` - Exponentiation

- Arity: 2
- In: a, b
- Out: a ** b

Returns a raised to the power of b


# Cohesion
| a v          b > | Number                | String                                | List/Generator                     |
|------------------|-----------------------|---------------------------------------|------------------------------------|
| Number           | `a ** b`              | `b * a`                               | `a ** b` (vectorised)              |
| String           | `a * b`               | `list(re.compile(a).match(b).span())` | `a ** b` (vectorised)              |
| List/Generator   | `a ** b` (vectorised) | `a ** b` (vectorised)                 | `a ** b` (vectorised element-wise) |
