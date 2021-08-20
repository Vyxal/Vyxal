# How Does Input Work?

## Explicit Input

Explicit input happens when you use the `?` element, which takes the next input from the provided input source. The input source is one of either `argv` or 
`STDIN` - if `argv` isn't present, `STDIN` is used. And if `STDIN` isn't present, all input is `0`.

When `End of Input` (`EOI`) is reached, the input "wraps around" back to the first input, as if the list of inputs is circular/infinitely repeated. For `STDIN`, this
means that an empty input is given.

## Implicit Input

Implicit input happens when an element requires more items from the stack than there are items on the stack; `arity(element) > len(stack)`. 
This happens for all stacks - main and function (yes, functions have their own stack). In a nutshell, this is the equivalent of inserting `?` before the
element at run-time.

For example:

```
??+
```

and

```
+
```

are both equivalent, assuming that the stack is empty.

## But how is this implemented internally?

With lists. More specifically, a list containing lists containing a list of any, and an integer:

```
[
  [
    [inputs],
    0
  ],
  
  [
    [inputs],
    0
  ],
  
  [
    [inputs],
    0
  ]
]
```

### Hang on, why are there multiple lists?

Because functions have their own stack, and it's more helpful to have functions re-use their arguments as input rather than global input. Hence, each list acts as
a sort of input scope.

### But why is there a number?

To keep track of where the input is up to - this allows for easier cycling through input, because you can just use `%` to bring the index into the range
`[0, len(input_scope))`
