# `F` - Filter

- Arity: 2
- In: vector, function
- Out: filter(function, vector)

Keeps the elements in `vector` where, when `function` is applied to the value, the result is considered truthy (non-null/non-zero)

# Usage
```
1 10r λ3<; F    ║⟨⟨1|2⟩⟩
1 10r λ:*100<; F║⟨⟨1|2|3|4|5|6|7|8|9⟩⟩
1 10r λ:*100<; F║⟨⟨1|2|3|5|7|11|13|17|19|23|29|31|37|41|43|47|53|59|61|67|71|73|79|83|89|97⟩⟩
```
