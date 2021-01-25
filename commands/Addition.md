# `+` - Addition

- Arity: 2
- In: a, b
- Out: a + b

Takes two items and adds them together. If you want to join strings and numbers together, use `J`


# Cohesion

| a v          b > | Number               | String               | List/Generator                    |
|------------------|----------------------|----------------------|-----------------------------------|
| Number           | `a + b`              | `b.ljust(a)`         | `a + b` (vectorised)              |
| String           | `a.rjust(a)`         | `a + b`              | `a + b` (vectorised)              |
| List/Generator   | `a + b` (vectorised) | `a + b` (vectorised) | `a + b` (vectorised element-wise) |
