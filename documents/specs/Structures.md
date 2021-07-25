# Structure Specifications

Very simply, a _structure_ is an element that is made of sub-elements grouped together. Structures can have branches that impact either a) the attributes of the structure or b) how the structure is executed. Branches are created by using `|`. 

Here is a list of every structure defined within version 2:

- If statement
- For loop
- While loop
- Defined function
- Lambda
    - Map
    - Filter
    - Sort
- List literal
- Variables

<!-- TODO: Hyperlink each item to its appropriate subheading -->

## If Statement

The full form of an if statement is as so:

```
[Truthy Branch|Falsey Branch]
```

With only 1 branch, the `Falsey` branch is considered to be empty.

The if statement pops a single value off the stack - we'll call it `x`. If `x` is a function, it is evaluated as if it was called using `†`. `x` is then set to the result of the function (`x = x()`). This is repeated until a non-function value is returned. The context variable (`n`) is set to `x`.

If `x` is a truthy value (non-zero for numeric values, non-empty for strings and lists), then the code in the `Truthy` branch will be executed. Otherwise, the `Falsey` branch will be executed.

## For Loop

The full form of a for loop is as so:

```
(Loop Variable|Loop Code)
```

With only 1 branch, the `Loop Variable` branch is considered to use the context variable (`n`). Note that `(n|...)` and `(...)` are NOT equivalent.

The for loop pops a single value off the stack - we'll call it `x`. If `x` is a function, it is evaluated as if it was called using `†`. `x` is then set to the result of the function (`x = x()`). This is repeated until a non-function value is returned. If `x` is numeric, `x` is set to the range `[0, ⌊x⌋)`.

For each item in `x`, the `Loop Code` branch is executed, with the item being stored in the variable contained in the `Loop Variable` branch.

## While Loop

The full form of a while loop is as so:

```
{Loop Condition|Loop Code}
```

With only 1 branch, the `Loop Condition` branch is considered to be always truthy (think infinite loop).

Each iteration of the while loop, `Loop Condition` is executed on the main stack. The top of the stack is then popped, and if it is truthy, `Loop Code` is executed. Otherwise, the loop is exited.
