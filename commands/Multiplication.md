# `*` - Multiplication

- Arity: 2
- In: a, b
- Out: a * b

Takes two values and multiplies them.

# Cohesion

| a v          b > | Number                            | String                            | List/Generator                     |
|------------------|-----------------------------------|-----------------------------------|------------------------------------|
| Number           | `a * b`                           | `a * b` (string repeated a times) | `a * b` (vectorising)              |
| String           | `a * b` (string repeated b times) | `[char + b for char in a]`        | `a * b` (vectorising)              |
| List/Generator   | `a * b` (vectorising)             | `a * b` (vectorising)             | `a * b` (vectorising element-wise) |
