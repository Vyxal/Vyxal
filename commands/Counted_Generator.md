# `ƀ` - Counted Generator

- Arity: 2 / 3
- In: f, a / f, a, b
- Out: Generator(function=f, initial_stack=a, limit=b or infinity)

Returns a generator that uses the function and the initial stack. If `b` is passed, the generator only generates `b` items.

# Usage
```
λ2|~+;⟨0|1⟩ 5ƀ║⟨⟨0|1|1|2|3|5|8⟩⟩ # Fibonacci sequence
λ!2$e; ⟨⟩ 10ƀ ║⟨⟨1|2|4|8|16|32|64|128|256|512⟩⟩ # Powers of 2
```
