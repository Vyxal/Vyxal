# `⋯` - Partitions/Join on Spaces

- Arity: 1
- In: a
- Out: partitions(a) (if int) " ".join(a) (if str)

Returns integer partitions of `a` if integer, else `a` joined on spaces. Does not vectorise.

# Usage
```
5    ⋯║⟨⟨⟨5⟩|⟨1|4⟩|⟨1|1|3⟩|⟨1|1|1|2⟩|⟨1|1|1|1|1⟩|⟨1|2|2⟩|⟨2|3⟩⟩⟩
1 10r⋯║⟨`1 2 3 4 5 6 7 8 9`⟩
```
