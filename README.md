# Vyxal - Terse, Elegant and Readable

**Vyxal** is the latest addition to the plethora of stack-based languages used for code golfing. But unlike its competitors, Vyxal has a special focus upon readability and elegancy. Indeed, the codepage has been specially chosen to be as mnemonic as possible. Further, constructs from practical languages (such as functions, lambdas and easy list manipulation) are present.

(Vyxal is pronounced Vikesal)

## How to use the interpreter:

`python3 Vyxal.py <file> <flags (single string of flags)> <input(s)>`

For a list of command-line flags:
`python3 Vyxal.py h`

## Data Types

There are 5 data types supported by Vyxal:

- Numbers (integers and reals/floats)
- Strings
- Lists
- Generators
- Functions

## Basic Operators

- `+-*/%` perform addition, subtraction, multiplication, division and modulo respectively.
- `,` prints the top of the stack
- `:_` duplicates the top of the stack and pops the top of the stack respectively
- `!` pushes the length of the stack

## Syntax Constructs
### If statements

```
[truthy_branch|falsey_branch]
[truthy_branch]
```

The if statement pops the top of the stack, and if it is truthy, executes the truthy branch. Otherwise, if a falsey branch is present, it will branch to execute that.

### For loop

```
(variable|body)
(body)
```

The for loop pops the top of the stack and iterates through each item. If the value popped is an integer, it loops through the range `[0, n)`. If `variable` is present, the iteration value is stored in that. Otherwise, the iteration value is stored in the context variable `n`.

### While loop

```
{condition|body}
{body}
```

The while loop repeats `body` until `condition` evaluates as true. If there is no explicit condition, `1` is used as the condition, meaning that `{...}` is an infinite loop.

### Functions

```
@name|code;
@name:number_of_arguments|code;
@name:variable|code;
@name:argument_list|code;
@name;
```

If `code` isn't present, the function with name `name` is called. Otherwise, the function is defined. The arguments can be a combination of variables and numbers. Numbers tell the function how many items to pop from the main stack as arguments, and variables store a single value in the variable. Numbered arguments are pushed to the function's stack -- functions operate on their own scoped stack with scoped variables (much like Python).

For example:

```
@triple:1|3*;
```

Takes 1 parameter and pushes it to the function's stack

```
@triple:value|←value 3*;
```

Takes a single argument and stores it in variable `value`.

```
@add_and_halve:1:rhs|←rhs +2/;
```

Takes two arguments: pushes the first on to the stack and stores the second in variable `rhs`

```
@average:*|W:L$∑$/;
```

Takes however many arguments as defined by the first value popped from the stack. A function call of `2 3 3 3 @average;` would take three arguments.

### Lambdas

```
λarity|code;
λcode;
```

Where the `@...;` function stores the definition for infinite re-use, the lambda pushes a reference to the code inside it. This is similar to python's lambdas, which are temporary functions, or literal functions (for lack of better word).

These can be applied using `⍎`. For example: `3 λ3*; ⍎` will result in `9`. Lambdas are also useful for mapping/filtering/reducing a vector according to the lambda's code.

### Implicit input and output

- At the end of program execution (eof), if nothing has been printed (using `,` or other printing commands), the top value on the stack is automatically printed.
- If there isn't enough values on the stack to perform an operation, implicit input is taken. If input is passed through command line arguments, then the input used is cycled.
- Input can be either through arguments or STDIN. STDIN is used if arguments aren't avaliable. If no input is avaliable at all, 0 is returned.
- In functions (and lambdas), if implicit input is needed, the argument(s) passed are used as the input "list".

### Commands

Vyxal has so many commands that it is impractical to list them all here. [Here is the reference page](https://github.com/Lyxal/Vyxal/blob/master/docs/reference.txt)

## Examples

### Hello, World!

```
`Hello, World!`
```
[Try it Online!](http://lyxal.pythonanywhere.com?flags=&code=%60Hello%2C%20World!%60&inputs=&header=&footer=)

```
`∞∧, ƛ⍎!
```
[Try it Online!](http://lyxal.pythonanywhere.com?flags=&code=%60%E2%88%9E%E2%88%A7%2C%20%C6%9B%E2%8D%8E!&inputs=&header=&footer=)

The above program uses dictionary compression: words in a predefined list are indexed using a subjective base-162 literal.

```
kH
```
[Try it Online!](http://lyxal.pythonanywhere.com?flags=&code=kH&inputs=&header=&footer=)

### Fizzbuzz

```
Ĥƛ3œı⇿⌊*n5œıₛÔ*+⟇
```
[Try it Online!](http://lyxal.pythonanywhere.com/?flags=jM&code=%C4%A4%C6%9B3%C5%93%C4%B1%E2%87%BF%E2%8C%8A*n5%C5%93%C4%B1%E2%82%9B%C3%94*%2B%E2%9F%87&inputs=&header=&footer=)
[Explanation](https://codegolf.stackexchange.com/a/210307/78850)

### Prime Checking

```
æ
```
[Try it Online!](http://lyxal.pythonanywhere.com?flags=&code=%C3%A6&inputs=31&header=&footer=)


```
KL2=
```
[Try it Online!](http://lyxal.pythonanywhere.com?flags=&code=KL2%3D&inputs=10&header=&footer=)

## Links

- [Repository](https://github.com/Lyxal/Vyxal)
- [Online Interpreter](http://lyxal.pythonanywhere.com)
- [Tutorial](https://github.com/Lyxal/Vyxal/blob/master/docs/Tutorial.md)
- [Codepage](https://github.com/Lyxal/Vyxal/blob/master/docs/codepage.txt)
- [Chat Room (SE Chat)](https://chat.stackexchange.com/rooms/106764/vyxal)

## Very Special Contributors

- Massive thanks to @Razetime for helping me with the online interpreter's design
- Massive thanks to @ysthakur for making the reference.md file and making an automated process to do so.
- Massive thanks to code-golf se user @2x-1 for helping me establish the fundamentals of Vyxal and being my first collaborator on this journey.
- Massive thanks to @8dion8 for language suggestions and motivation in the MAWP discord group. 
