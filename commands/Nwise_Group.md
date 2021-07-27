# `l` - N-Wise Group

- Arity: 2
- In: a, b
- Out: nwise_group(a, b)

Returns `a` with overlapping groups of length b. The usage describes better what this does.

# Usage
```
1 2 3 4 5 6 7 8W2l║⟨⟨⟨1|2⟩|⟨2|3⟩|⟨3|4⟩|⟨4|5⟩|⟨5|6⟩|⟨6|7⟩|⟨7|8⟩⟩⟩
1 2 3 4 5 6 7 8W3l║⟨⟨⟨1|2|3⟩|⟨2|3|4⟩|⟨3|4|5⟩|⟨4|5|6⟩|⟨5|6|7⟩|⟨6|7|8⟩⟩⟩
1 2 3 4 5 6 7 8W6l║⟨⟨⟨1|2|3|4|5|6⟩|⟨2|3|4|5|6|7⟩|⟨3|4|5|6|7|8⟩⟩⟩
```
