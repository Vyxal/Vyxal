# `n` - Context Variable

- Arity: 0
- In: NA
- Out: *

(From the tutorial)
The context variable is a variable that pushes a value based on the _context_ of the current structure/scope. In a for-loop, the context variable is the current iteration value. In a function/lambda, the context variable is the arguments passed. In a while-loop, the context variable is the result of the condition.

When there are nested contexts (for example, a for-loop in a lambda), the different values of the contexts can be accessed by modifying the contextual depth of `n`. Contextual depth starts at level `0` (the top-most context level) and spans all the way to the inner-most context (the bottom-most context level).  By default, `n` has the contextual depth of the inner-most structure. `X` moves the context level deeper (`+1`) and `x` moves the context level shallower (`-1`).
