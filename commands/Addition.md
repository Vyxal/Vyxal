# `+` - Addition

- Arity: 2
- In: a, b
- Out: a + b

Takes two items and adds them together. If you want to join strings and numbers together, use `J`


# Cohesion

| a v          b > | Number               | String               | List/Generator                    |
|------------------|----------------------|----------------------|-----------------------------------|
| Number           | `a + b`              | `str(a) + b`         | `a + b` (vectorised)              |
| String           | `a + str(b)`         | `a + b`              | `a + b` (vectorised)              |
| List/Generator   | `a + b` (vectorised) | `a + b` (vectorised) | `a + b` (vectorised element-wise) |
