# `ƈ` - N Choose R

- Arity: 2
- In: a, b
- Out: ncr

Returns N choose R


# Cohesion

| a v          b > | Number                                 | String                                 | List/Generator                        |
|------------------|----------------------------------------|----------------------------------------|---------------------------------------|
| Number           | `ncr(a, b)`                            | `[random.choice(b) for c in range(a)]` | `ncr(a, b)` (vectorised)              |
| String           | `[random.choice(a) for c in range(b)]` | `set(a) == set(b)`                     | `ncr(a, b)` (vectorised)              |
| List/Generator   | `ncr(a, b)` (vectorised)               | `ncr(a, b)` (vectorised)               | `ncr(a, b)` (vectorised element-wise) |
