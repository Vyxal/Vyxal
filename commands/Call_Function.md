# `⍎` - Call Function

- Arity: 1 + *
- In: f + args
- Out: f(args)

Takes the top of the stack, and, if it is a function reference (i.e. a callable), executes it.

# Usage
```
3 λ3*; ⍎║⟨9⟩
3 ․d ⍎  ║⟨6⟩
```
