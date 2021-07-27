# `•` - Multi-Command

- Arity: 2
- In: a, b
- Out: Depends on types of input

Does various things including:

- Logarithm
- Capitalisation Transfer
- List molding


# Cohesion

| a v          b > | Number                        | String                        | List/Generator        |
|------------------|-------------------------------|-------------------------------|-----------------------|
| Number           | `math.log(a, b)`              | `"".join([c * a for c in b])` | `a • b` (vectorising) |
| String           | `"".join([c * b for c in a])` | `a.with_capitalisation_of(b)` | `a • b` (vectorising) |
| List/Generator   | `a • b` (vectorising)         | `a • b` (vectorising)         | `a.mold(b)`           |
