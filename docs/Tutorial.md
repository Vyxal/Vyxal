# How To: Vyxal

So you want to use Vyxal as a golfing language? Well, lucky for you, you've reached the tutorial page of the documentation! Throughout this document, you'll:

- learn how to use a stack
- learn how to use the control-flow structures of Vyxal
- learn how implicit input works

## The Stack

Vyxal's main mode of storage is stacks: one main stack and sub-stacks used within functions and lambdas. A stack works by the principle of Last In, First Out (LIFO). What is pushed onto the stack first, will be popped off last. 
This leads to the two primary stack operations: PUSH, which adds something to the top of the stack, and POP, which removes something from the top and returns it. Popping from an empty stack attempts to take input, otherwise returning 0 (more on that later).

To push integers, you simply write the integer followed by a non-digit character.

Let's take a look at a program that pushes two numbers and adds them together:

```
1 1+,
```
The result of this program is simply:

```
2
```

A walkthrough:

```
1      # Push 1 to the stack. STACK => [1]
  1    # Push 1 to the stack. STACK => [1, 1]
  +    # Add the top two numbers. STACK => [2]
   ,   # Print the top of the stack. STACK => []
```

As aforementioned, integers need to be separated by non-digit characters. For example, `11` does **not** push `[1, 1]` but rather `[11]`... `321` does **not** push `[3, 2, 1]` but rather `[321]`. `3 21` does **not** push `[3, 2, 1]` but rather `[3, 21]`

Here's a program that shows other ways of separating integers:

```
1:_1:_+
```

A walkthrough:

```
1         # Push 1 to the stack. STACK => [1]
 :_       # Duplicate the number and immediantly discard (pop) it. STACK => [1, 1] ... [1]
   1      # Push 1 to the stack. STACK => [1, 1]
    :_    # Duplicate the number and immediantly discard (pop) it. STACK => [1, 1, 1] ... [1, 1]
      +   # Add the two top numbers. STACK => [2]
```

It's worth noting that, if during program execution, nothing has been printed explcitly (using either `,` or `.`), the top of the stack will be implicitally outputted.

If you want decimals, use `•` instead of `.`:

```
3•1415926535897      # Pushes 3.1415926535897 (pi to a precision higher than most mathematical fields actually need)
```

## Strings

Now, it's time to introduce a key data type common to most programming languages: strings. Like integers, strings are simply pushed onto the stack. Here are some examples of strings:

```
`Hello, World!` # Pushes "Hello, World"
`I'm using Vyxal` # Pushes "I'm using Vyxal"
`"You can't just change the string quote marker" "haha backticks go brr"` # Pushes "\"You can't just change the string quote marker\" \"haha backticks go brr\""
```

As indicated in the last string (albeit, in a memed fashion), strings don't use standard quotation marks (" and ') like other programming/golfing languages: backticks delimit strings.

## Type Cohesion
I present to you: The Ballad of the Types

```
There are three types: numbers, strings and lists
If you add a number to a number you get a number
But if you add a number to a string you get a string
Adding two strings gives a concatenation
And adding with lists performs vectorising
Number minus number is a number
But number minus string is still a string
A string minus a number is the same as above
And subtracting with lists still performs vectorising
When you subtract a string from string it does replacement
And when you multiply instead it does zipping
Any string multiplied by a number is a string
And multiplication with lists performs vectorising
A number x a number is a number
So is number over number (how interesting)
Dividing strings by numbers splits them into lengthened chunks
And division with lists still performs vectorising
When you take a string and divide it by another string it splits
It'd be stupid if the result was still a string.
Now I know that you're saying "Lyxal, why did you have to make everything rhyme". And ngl, I kinda don't know why. But the one thing that I do know is:
Anything done with lists performs vectorising
```

As seen above, Vyxal implements the 5 basic arithmetic operators: `+` (addition), `-` (subtraction), `*` (multiplication), `/` (division) and `%` (modulo). Here are tables summarising how they operate on different types:

| lhs + rhs | Number                       | String                       | List                                       |
|-----------|------------------------------|------------------------------|--------------------------------------------|
| Number    | lhs + rhs                    | Concat(lhs, rhs)             | [lhs + item for item in rhs]               |
| String    | Concat(lhs, rhs)             | Concat(lhs, rhs)             | [lhs + item for item in rhs]               |
| List      | [rhs + item for item in lhs] | [rhs + item for item in lhs] | [lhs[i] + rhs[i] for i in range(len(lhs))] |

| lhs - rhs | Number                       | String                              | List                                       |
|-----------|------------------------------|-------------------------------------|--------------------------------------------|
| Number    | lhs - rhs                    | str(lhs).replace(rhs, "")           | [lhs - item for item in rhs]               |
| String    | lhs.replace(str(rhs), "")    | lhs.replace(rhs, "")                | [lhs.replace(item, "") for item in rhs]    |
| List      | [rhs - item for item in lhs] | [rhs.replace(item) for item in lhs] | [lhs[i] - rhs[i] for i in range(len(lhs))] |

| lhs * rhs | Number                       | String                       | List                                       |
|-----------|------------------------------|------------------------------|--------------------------------------------|
| Number    | lhs * rhs                    | lhs * rhs                    | [lhs * item for item in rhs]               |
| String    | lhs * rhs                    | interleave(lhs, rhs)         | [lhs * item for item in rhs]               |
| List      | [rhs * item for item in lhs] | [rhs * item for item in lhs] | [lhs[i] * rhs[i] for i in range(len(lhs))] |

| lhs / rhs | Number                       | String                       | List                                       |
|-----------|------------------------------|------------------------------|--------------------------------------------|
| Number    | lhs / rhs                    | textwrap.wrap(rhs, lhs)      | [lhs / item for item in rhs]               |
| String    | textwrap.wrap(lhs, rhs)      | lhs.split(rhs)               | [lhs / item for item in rhs]               |
| List      | [rhs / item for item in lhs] | [rhs / item for item in lhs] | [lhs[i] / rhs[i] for i in range(len(lhs))] |

| lhs % rhs | Number                       | String                       | List                                       |
|-----------|------------------------------|------------------------------|--------------------------------------------|
| Number    | lhs % rhs                    | textwrap.wrap(rhs, lhs)[-1]  | [lhs % item for item in rhs]               |
| String    | textwrap.wrap(lhs, rhs)[-1]  | format(lhs, rhs)             | [lhs % item for item in rhs]               |
| List      | [rhs % item for item in lhs] | [rhs % item for item in lhs] | [lhs[i] % rhs[i] for i in range(len(lhs))] |

Note that when it's two lists, they are extended to be of the same length.

## Program Flow
### `If` Statements
Like _Keg_, Vyxal has a readable and intuative way of expressing `if` statements, `for` and `while` loops. The form of an `if` statement is:

    [...1|...2]

When an `if` statement is run, the last item on the stack is popped, and if it is non-zero, `...1` is executed. If there is a `|...2`, it is executed if the popped value is 0.

### `For` Loops
The form of a `for` loop is:

    (...1|...2)

When a `for` loop is run, the top of the stack is popped and is iterated through, executing `...2` each time. If `...1` is present, the current iteration value is stored in the variable given. Otherwise, the iteration value is stored as a context variable, retrivable through `n`

### `While` Loops
The form of a `while` loop is:

    {...1|...2}

When a `while` loop is run,  `...1` (if given) will be the condition of the loop (if it isn't present, `1` will be used as the condition of the loop) and `...2` will be executed until the given condition is false.

## User Defined Functions
One of the special features of Vyxal is user-defined functions, which are defined using the following form:

    @name:n|...;

Where:
`name` = the name of the function (note that it needs to be one full word, and that it can't contain any `@`'s)
`n` = the number of items popped from the stack to use as arguments
`...` = the body of the function

If `n` isn't present, no items will be popped from the stack, consequently making the function a niladic function.

## Lambdas
```
λarity|code;
λcode;
```

`arity` = the number of arguments popped from the stack to use as arguments
`code` = the body of the lambda.
Where the `@...;` function stores the definition for infinite re-use, the lambda pushes a reference to the code inside it. This is similar to python's lambdas, which are temporary functions, or literal functions (for lack of better word).

These can be applied using `⍎`. For example: `3 λ3*; ⍎` will result in 9. Lambdas are also useful for mapping/filtering/reducing a vector according to the lambda's code.

## Summary of Structures

- ![https://i.stack.imgur.com/um1Ve.jpg](https://i.stack.imgur.com/um1Ve.jpg)

- ![https://i.stack.imgur.com/RGBWe.jpg](https://i.stack.imgur.com/RGBWe.jpg)

- ![https://i.stack.imgur.com/GfSVK.jpg](https://i.stack.imgur.com/GfSVK.jpg)

## The Context Variable

One last thing to cover is the special context variable - a variable that pushes a value based on the _context_ of the current structure/scope. In a for-loop, the context variable is the current iteration value. In a function/lambda, the context variable is the arguments passed. In a while-loop, the context variable is the result of the condition.

When there are nested contexts (for example, a for-loop in a lambda), the different values of the contexts can be accessed by modifying the contextual depth of `n`. Contextual depth starts at level `0` (the top-most context level) and spans all the way to the inner-most context (the bottom-most context level).  By default, `n` has the contextual depth of the inner-most structure. `X` moves the context level deeper (`+1`) and `x` moves the context level shallower (`-1`).

