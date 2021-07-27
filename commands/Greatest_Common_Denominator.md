# `ᶢ` - Greatest Common Denominator

- Arity: 1/2
- In: a / a, b
- Out: gcd(a) / gcd(a, b)

If `a` is a list, return the great common denominator of the elements of `a`. Otherwise, return `gcd(a, b)`. When either argument is a string, convert both arguments to strings and find the longest common suffix.

# Usage
```
420 69ᶢ           ║⟨3⟩
`abcdef` `abcdfe`ᶢ║⟨`abcd`⟩
690 69690 420690Wᶢ║⟨30⟩
```
