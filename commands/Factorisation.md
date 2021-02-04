# `K` - Factorisation

- Arity: 1
- In: a
- Out: factors_of(a)

Returns the divisors of a. Vectorises if needed. If `a` is a string, return all substrings that split the `a` into more than two parts.

# Usage
```
69K        ║⟨⟨1|3|23|69⟩⟩
420K       ║⟨⟨1|2|3|4|5|6|7|10|12|14|15|20|21|28|30|35|42|60|70|84|105|140|210|420⟩⟩
⟨1|3|23|69⟩K║⟨⟨⟨1⟩|⟨1|3⟩|⟨1|23⟩|⟨1|3|23|69⟩⟩⟩
```
