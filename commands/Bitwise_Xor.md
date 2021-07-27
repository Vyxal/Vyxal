# `꘍` - Bitwise XOR

- Arity: 2
- In: a, b
- Out: a ^ b

Returns the bitwise xor of `a` and `b`

# Cohesion

| a v          b > | Number                     | String                     | List/Generator                    |
|------------------|----------------------------|----------------------------|-----------------------------------|
| Number           | `a ^ b`                    | `every ath character of b` | `a ^ b` (vectorised)              |
| String           | `every bth character of a` | `edit_distance(a, b)`      | `a ^ b` (vectorised)              |
| List/Generator   | `a ^ b` (vectorised)       | `a ^ b` (vectorised)       | `a ^ b` (vectorised element wise) |
