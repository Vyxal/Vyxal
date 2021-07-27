# `-` - Subtraction

- Arity: 2
- In: a, b
- Out: a - b

Takes two values and subtracts them.


# Cohesion

| a v          b > | Number               | String               | List/Generator                    |
|------------------|----------------------|----------------------|-----------------------------------|
| Number           | `a - b`              | `("-" * a) + b`      | `a - b` (vectorised)              |
| String           | `a + ("-" * b)`      | `a.rstrip(b)`        | `a - b` (vectorised)              |
| List/Generator   | `a - b` (vectorised) | `a - b` (vectorised) | `a - b` (vectorised element-wise) |
