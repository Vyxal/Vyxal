This is simply all the niche syntax bits for making look nice later.

## SBCS
```
#$name -> get variable
#=name -> set variable
f#>name -> name f= top of stack
#:[x|y|z] -> x, y, z = top of stack
λN|...} -> lambda that takes N arguments
λname|...} -> lambda that takes 1 argument and sets name
λ*|...} -> lambda that pops N and takes N arguments in a list
λ!|...} -> lambda that operates on the stack
λ|...} -> lambda that takes 1 argument
¤number -> index n of context parameters
```

## Literate

```
$name -> get
:=name -> set
f:>name -> name f= top of stack
:=[x|y|z] -> x, y, z = top of stack
# ... #} -> raw sbcs
lambda ~ | ... } -> lambda that operates on the stack
`number` -> index n of context parameters
(. f) (: f g) (:. f g h) (:: f g h p) -> next n items as monadic lambda
(, f) (; f g) (;, f g h) (;; f g h p) -> next n items as dyadic lambda
```

- Lists and lambdas (`[...]` and `{...}`) must be closed
- Groups (`(...)`) must be closed. The parenthesis are removed, so they are purely aesthetic (except for grouping modifiers)
