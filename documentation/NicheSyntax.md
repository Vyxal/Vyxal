This is simply all the niche syntax bits for making look nice later.

## SBCS
```
#$name -> get variable
#=name -> set variable
f#>name -> name f= top of stack
#:[x|y|z] -> x, y, z = top of stack
```

## Literate

```
$name -> get
:=name -> set
f:>name -> name f= top of stack
:=[x|y|z] -> x, y, z = top of stack
# ... #} -> raw sbcs
```

- Lists and lambdas (`[...]` and `{...}`) must be closed
- Groups (`(...)`) must be closed. The parenthesis are removed, so they are purely aesthetic