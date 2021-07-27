# `%` - Modulo

- Arity: 2
- In: a, b
- Out: a % b

Takes two values and performs modulo on them.

# Cohesion

| a v       b >  | Number                  | String                        | List/Generator                                        |
|----------------|-------------------------|-------------------------------|-------------------------------------------------------|
| Number         | `a % b`                 | `wrap(b, length=a)[-1]`       | `a % b` (vectorised)                                  |
| String         | `wrap(a, length=b)[-1]` | `a.format(b)` (single string) | `a.format(b)` (reuses the items to format the string) |
| List/Generator | `a % b` (vectorised)    | `a % b` (vectorised)          | `a % b` (vectorised)                                  |
