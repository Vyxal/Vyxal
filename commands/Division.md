# `/` - Division

- Arity: 2
- In: a, b
- Out: a % b

Takes two values and divides them. Trying to divide by zero will raise an error.


# Cohesion
| a v          b > | Number               | String               | List/Generator                    |
|------------------|----------------------|----------------------|-----------------------------------|
| Number           | `a / b`              | `wrap(b, length=a)`  | `a / b` (vectorised)              |
| String           | `wrap(a, length=b)`  | `a.split(b)`         | `a / b` (vectorised)              |
| List/Generator   | `a / b` (vectorised) | `a / b` (vectorised) | `a / b` (vectorised element-wise) |
