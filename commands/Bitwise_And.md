# `⋏` - Bitwise And

- Arity: 2
- In: a, b
- Out: bitwise_and(a, b)

Returns bitwise and between `a` and `b`. Analogous to `a & b` in python.


# Cohesion

| a v          b > | Number               | String                      | List/Generator                    |
|------------------|----------------------|-----------------------------|-----------------------------------|
| Number           | `a & b`              | `b.centre(a)`               | `a & b` (vectorised)              |
| String           | `a.centre(b)`        | `a.centre(len(b) - len(a))` | `a & b` (vectorised)              |
| List/Generator   | `a & b` (vectorised) | `a & b` (vectorised)        | `a & b` (vectorised element-wise) |
