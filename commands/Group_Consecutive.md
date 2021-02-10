# `≎` - Group Consecutive

- Arity: 1
- In: a
- Out: group_consecutive(a)

Returns `a` with similar elements grouped together.

# Usage
```
`aaaabbbccccdddddeeeee`≎                        ║⟨⟨⟨`a`|`a`|`a`|`a`⟩|⟨`b`|`b`|`b`⟩|⟨`c`|`c`|`c`|`c`⟩|⟨`d`|`d`|`d`|`d`|`d`⟩|⟨`e`|`e`|`e`|`e`|`e`⟩⟩⟩
1 1 1 1 1 1 1 2 2 2 3 3 3 1 1 2 3 3 3 2 2 1 1 W≎║⟨⟨⟨1|1|1|1|1|1|1⟩|⟨2|2|2⟩|⟨3|3|3⟩|⟨1|1⟩|⟨2⟩|⟨3|3|3⟩|⟨2|2⟩|⟨1|1⟩⟩⟩
```
