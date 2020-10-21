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
