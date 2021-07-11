# `∈` - Truthy Indices of Function

- Arity: 2
- In: a, f
- Out: truthy_indices_of(f(a))

Returns all indices of `a` where the result of  `f(a)` is truthy.

# Usage
```
1 50 r ․æ ∈      ║⟨⟨0|1|2|4|6|10|12|16|18|22|28|30|36|40|42|46⟩⟩
1 10 r λ3*2%1=; ∈║⟨⟨0|2|4|6|8⟩⟩
```
