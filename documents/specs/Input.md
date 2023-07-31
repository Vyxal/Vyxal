# How Does Input Work?

## Explicit Input

Explicit input happens when you use the `?` element, which takes the next input from the provided input source. The input source is one of either `argv` or
`STDIN` - if `argv` isn't present, the next line of `STDIN` is used. And if `STDIN` isn't present, all input is `0`.

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

Note that whatever is on the stack is popped before the implicit input is taken. Also, taking implicit input cycles the input just as explicit input would.

## Input Evaluation

The evaluation chain for input is as follows:

```
number
explicit string
list
function
implicit string
```

A number is either a) any valid vyxal numeric literal (e.g. `23423`, `454.23`, `.` and `6°2`), b) a fraction or c) a simple python numeric literal (e.g. `-1324.234`).

An explicit string is a line of input explicitly wrapped in quotes, e.g. `"Hello, World!"`. Note that this is not the same as an implicit string, which is the result of all other input types failing.

A list is one of three things:

- A python list of only numbers, strings and other lists
- A python tuple of only numbers, strings and other lists
- Any valid vyxal list

A function is a string starting with `λ`.

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
