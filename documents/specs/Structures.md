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

<!-- TODO: Hyperlink each item to its appropriate subheading -->

## If Statement

The full form of an if statement is as so:

```
[Truthy Branch|Falsey Branch]
```

With only 1 branch, the `Falsey` branch is considered to be empty.

The if statement pops a single value off the stack - we'll call it `x`.

If `x` is a truthy value (non-zero for numeric values, non-empty for strings, and [insert list truthy condition here]), then the code in the `Truthy` branch will be executed. 
