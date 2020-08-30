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



