# `‹` - Left Bitshift

- Arity: 2
- In: a, b
- Out: a << b

Returns `a` bitshifted left by factor `b`. Vectorises if needed.

# Cohesion

| a v          b > | Number                | String                     | List/Generator                     |
|------------------|-----------------------|----------------------------|------------------------------------|
| Number           | `a << b`              | `b.ljust(a)`               | `a << b` (vectorised)              |
| String           | `a.ljust(b)`          | `a.ljust(len(b) - len(a))` | `a << b` (vectorised)              |
| List/Generator   | `a << b` (vectorised) | `a << b` (vectorised)      | `a << b` (vectorised element-wise) |
