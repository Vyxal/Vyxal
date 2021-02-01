# `Š` - From Base Ten

- Arity: 2
- In: a, b
- Out: to_custom_base(a, base=b)

Returns `a` converted to a custom base `b` from decimal. If `b` is a string, then it is the "alphabet" of `a`: allowing for bijective bases to easily be defined. If `a` is a list/generator and `b` is an integer, then the list is converted to base `b`.

# Usage
```
3499 16Š         ║⟨⟨13|10|11⟩⟩
3499 k6Š         ║⟨`dab`⟩
34534 7Š         ║⟨⟨2|0|2|4|5|3⟩⟩
```
