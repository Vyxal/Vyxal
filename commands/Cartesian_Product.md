# `Ẋ` - Cartesian Product

- Arity: 2
- In: a, b
- Out: itertools.product(a, b)

Returns the cartesian product of `a` and `b`. Numbers are treated as a list of digits.

# Usage
```
1 4 r 4 7 r Ẋ║⟨⟨1|4⟩|⟨1|5⟩|⟨1|6⟩|⟨2|4⟩|⟨2|5⟩|⟨2|6⟩|⟨3|4⟩|⟨3|5⟩|⟨3|6⟩⟩
`abc` `def` Ẋ║⟨⟨`a`|`d`⟩|⟨`a`|`e`⟩|⟨`a`|`f`⟩|⟨`b`|`d`⟩|⟨`b`|`e`⟩|⟨`b`|`f`⟩|⟨`c`|`d`⟩|⟨`c`|`e`⟩|⟨`c`|`f`⟩⟩
1234 5678 Ẋ  ║⟨⟨1|5⟩|⟨1|6⟩|⟨1|7⟩|⟨1|8⟩|⟨2|5⟩|⟨2|6⟩|⟨2|7⟩|⟨2|8⟩|⟨3|5⟩|⟨3|6⟩|⟨3|7⟩|⟨3|8⟩|⟨4|5⟩|⟨4|6⟩|⟨4|7⟩|⟨4|8⟩⟩
```
