# Vyxal docs

This folder contains documentation on Vyxal. [elements.txt](./elements.txt) has all the elements.

## Miscellaneous stuff

### What's the register?

TODO

### Context Variables

Vyxal has 2 context variables, `N` and `M`. They hold different values inside different
structures:

- Inside for loops, `N` is the current value, and `M` is the current index
- Inside while loops, `N` is the last condition value and `M` is the current index
- Inside lambdas/named functions, `N` is the argument

### Ghost variable

The so-called ghost variable is the variable named ``. You can get and set it just like a normal
variable. Of course, you need to make sure there aren't any valid identifier characters after the
`#$` / `#=` like `#$a1`, or the parser will think you're referring to some variable `a1`. If you're
already using the register, the ghost variable is a good way to store something off the stack
without using up too many characters. Just keep the limitation above in mind.
