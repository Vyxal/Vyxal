# `J` - Concatenate

- Arity: 2
- In: a,b
- Out: merged(a, b)

Concatenates the two values passed.

# Cohesion

| a v          b > | Number            | String        | List/Generator          |
|------------------|-------------------|---------------|-------------------------|
| Number           | `str(a) + str(b)` | `str(a) + b`  | `b.insert(0, a)`        |
| String           | `a + str(b)`      | `a + b`       | `b.insert(0, a)`        |
| List/Generator   | `a.append(b)`     | `a.append(b)` | `a + b` (list addition) |
