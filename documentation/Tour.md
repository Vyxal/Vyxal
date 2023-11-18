# A Tour of Vyxal 3

_Note that this tour assumes SBCS syntax. For a guide on the specifics of
literate mode, see the [Literate Mode help file](./Literate%20Mode.md)._

## Table of Contents

0. [Introduction](#introduction)
1. [Stacks](#stacks)
2. [Numeric Literals](#numeric-literals)
3. [Strings](#strings)
4. [Lists](#lists)
5. [Basic Operations](#basic-operations)
6. [Control Flow](#control-flow)
7. [Stack Control](#stack-control)
8. [Input/Output](#io)
9. [Functions](#functions)
10. [Context](#number-ranges)
11. [Modifiers](#modifiers)
12. [Variables](#variables)
13. [What is a SBCS?](#single-byte-character-set)
14. [Arity Grouping](#arity-grouping)
15. [Nilad Moving](#nilad-moving)

## Introduction

Hi there, hello! Welcome to Vyxal 3, a stack and array based golfing language that's designed to be easy to learn and use. If this is your first time ever
using a stack language, array language, or golfing language, then this tour is
for you. Or if you're already a seasoned code golfer switching from another golfing language, then this tour is also for you. This tour will cover the basics of Vyxal, and will hopefully give you a good idea of how to use it.

## The Stack

Vyxal is a stack based language. This means that all operations are performed
on a FILO (first in, last out) data structure. As a metaphor, think of a pile
of plates in your kitchen. That pile of plates is a stack. You can only take
the top plate off the pile at any time, and you can only put a plate on top of
the pile. You can't take a plate from the middle of the pile, and you can't
put a plate under the pile. This is the same with Vyxal. Operations use values
on top of the stack, and push their result(s) back on top of the stack.

The stack can contain any type of value supported by Vyxal, those being:

- Numbers (integers, floats, complex numbers, etc)
- Strings
- Lists (of any type, including nested lists)
- Functions

## Numeric Literals

But what use is a pile of anything if you can't put anything on it? After all,
you can't put a plate on a pile of plates if you don't have any plates. In Vyxal,
you can push numbers onto the stack by simply typing them.

To push an integer, you simply type the digits of the number. For example:

```
69
420
69420
```

are all examples of (nice) integer literals. Each numeric literal pushes the
corresponding integer onto the stack. If you wish to push a negative integer,
append a `_` to the end of the number. For example:

```
69_
420_
69420_
```

are all examples of negative integer literals.

To push a float, you type the decimal representation of the number. Floats can
be to any precision, and can also be negated by appending a `_` to the end of
the number. For example:

```
123.456
123.456_
0.0
```

are all examples of float literals.

Floats can also have the integer and/or fractional part omitted. If the 
integer part of a decimal is omitted, it is assumed to be `0`. If the fractional
part of a decimal is omitted, it is assumed to be `0.5`. For example:

```
.5 => 0.5
.212 => 0.212
1. => 1.5
. => 0.5
```

Complex numbers can be pushed by using the `ı` character. `ı` is used in much the
same way you would use a `.` in a float. The left side of the `ı` is the real
part of the complex number, and the right side is the imaginary part. For example:

```
1ı2 => 1+2j
69ı420 => 69+420j
```

are both examples of complex number literals.

Much like floats, complex numbers can have the real and/or imaginary part omitted. If the real part is omitted, it is assumed to be `0`. If the imaginary part is omitted, it is assumed to be `1j`. For example:

```
ı2 => 0+2j
1ı => 1+1j
ı => 0+1j
```

Consecutive numeric literals require a space or other non-numeric separator:

```
3 4 5 => counts as 3, 4, and 5
345 => counts as 345
```

## Strings

Another very useful type of value that can exist on the stack are strings.
Strings are much like strings in any other programming language; they are
a sequence of characters. Strings can be pushed onto the stack by surrounding
them with `"`s. For example:

```
"Hello, World!"
"Red is sus."
""
```

are all examples of string literals. Note that the empty string is a valid
string, as shown in the last example. 

Strings can contain any character within the Vyxal codepage (well, technically any character can be used, but using a character outside the codepage requires UTF-8 scoring). To include a `"` in a string, escape it with a `\`. For example:

```
"\""
```

Further, strings can span multiple lines:

```
"Hello World!
Hey wait why is this string still going
who put these newlines in my string?!?"
```

is a self-aware multi-line string.

Finally, if a string is at the end of a program, it can be left unterminated. For example:

```
"Hello, World!
```

at the end of a program will automatically fill in the missing `"`.

## Lists

Lists are a core feature of Vyxal; a lot of data processing in code golf
challenges involves lists in one way or another. Vyxal lists are
(potentially infinite) sequences of values. Lists can be nested, meaning
that a list can contain other lists.

The syntax for lists is:

```
#[item|item|item|...|item#]
```

The `#[` opens the list, the `|` separates items, and the `#]` closes the list.
That's similar to how `[` opens a list, `,` separates items, and `]` closes a
list in languages like Python and JavaScript.

Some example lists include:

```
#[1|2|3|4|5#]
#[#[1|2|3#]|#[4|5|6#]|7|8|9#]
#[#]
```

Note that lists do not need to have a fixed shape. That is to say, they do
not follow a strict array model like you might find in APL or J. Some might
argue that not having an actual array model means Vyxal isn't an array
language, but the other key component of array languages are present. If K
is an array language, then Vyxal is an array language.

## Basic Operations

Now that the basic literal types have been covered (more on functions later),
it's time to start doing things with them. The typical first program in any
language is the classic "Hello, World!" program. However, the first program
covered here will be addition of two numbers, as "Hello, World!" has been
done in basically every beginner tutorial ever. And we appreciate originality
here at Vyxal! :p

To add two numbers, you first need to push them onto the stack. For this example,
5 will be used as the first number, and 7 will be used as the second number.

In order to achieve that order, the 5 must be put on the stack first, then the 7.
So our program starts as:

```
5 7
```

which sets the stack as:

```
7 -- top
5 -- bottom
```

Now comes the juicy part: adding the numbers. To add two numbers, you use the
`+` element. Elements are what other languages might call built-in functions or
commands or operators. `+` takes the top two values on the stack, adds them
together, and pushes its result back onto the stack. 

The program therefore is:

```
5 7+
```

The first value taken from the stack is designated as the right-hand value. 
The second value taken from the stack is designated as the left-hand value.
So in this case, the 7 is the right-hand value, and the 5 is the left-hand value.

After addition, the stack is:

```
12 -- top | bottom
```

Congratulations! You've just written your first Vyxal program! Now, let's do
the inverse of addition: subtraction. Subtraction is done with the `-` element.
Using the numbers 5 and 7 again, to compute `5 - 7`, you would write:

```
5 7-
```

which has the following stack trace:

```
(push 5)    (push 7)     (subtract)
5       ... 7        ... -2 -- top
        ... 5        ...    -- bottom
```

Notice how the left-hand value is the first value pushed onto the stack, and
the right-hand value is the second value pushed onto the stack. This is
typical of most stack based languages; instead of writing `lhs op rhs` as you
might in a traditional language, you write `lhs rhs op`. This is called
reverse Polish notation (RPN), and is the standard notation for stack based
languages.

## Control Flow

Using the basic operations, you can now write simple programs! Execellent stuff.
But to do anything more useful than what you can accomplish with a calculator,
you need to be able to control the flow of your program. That is to say, you
need to be able to conditionally execute code, or execute code multiple times.

Control flow is accomplished with what are called "structures". These structures
represent traditional programming constructs like ternarys, loops, and if/else 
statements.

### The Ternary Structure

Some languages have a ternary operator that acts as an inline if/else statement.
Typically, they are of the form `condition ? if_true : if_false`. Vyxal has a
similar structure:

```
[if_true|if_false}
```

This structure first pops the top of the stack. If that value is considered
truthy (non-0 for numbers, non-empty for strings and lists, functions are always truthy), then the `if_true` part is executed. Otherwise, the `if_false` part is executed.

The `if_false` part can be omitted. If it is, then it is assumed to be an empty branch. The `if_true` part can be empty, in case you want to only execute the `if_false` part. For example:

```
[if_true}
[if_true|if_false}
[|if_false}
```

Note that the ternary structure is _not_ an if-statement. Those have their own
structure.

For a demonstration of the ternary, say you wanted to print whether a number
is even or odd. You could do that with the following program:

```
2%0= ["even"|"odd"},
```

The `2%0=` retrieves the even-ness of the top of the stack. This value is used
as the condition. If the number is even, the `if_true` part is executed, which
pushes the string `"even"` onto the stack. If the number is odd, the `if_false`
part is executed, which pushes the string `"odd"` onto the stack. The `,` then
prints the top of the stack.

### The For Loop Structure

The for loop structure is the same as you would find in a traditional language:

```
(loop_variable|code}
```

The keen among us (!) will notice that there is no explicit iterable. That's
because the iterable is popped from the top of the stack. If the popped
value is a number, it is converted to the range `[1, number]` (1 through to
number inclusive).

The `loop_variable` part specifies the variable to store the current iteration
value. This part is optional; if it is omitted, no variables are created.

"But how do I access the loop value then?" you ask. For that you can use `n`,
which is the "context variable". It has different meanings depending on the
structure it's used in. More on that later.

The `code` part is the code to execute for each iteration.

For a demonstration of the for loop, say you wanted to print the numbers 1
through 10. You could do that with any of the following programs:

```
10ɾ (i|#$i,}
10ɾ (n,}
10 (i|#i,}
10 (n,}
```

Note how `#$i` is used to retrieve the loop variable `i`. Variables will be
discussed in depth later, but know for now that the loop variable declaration
does not require any sigil, while the variable retrieval does.

### The While Loop Structure

The while loop structure is used to execute code while a certain condition
is truthy. Note that it is a while loop, not a do-while loop. That is to say,
the condition is checked _before_ the code is executed.

The structure is:

```
{condition|code}
{code}
```

The `condition` part is the condition to check. If it is truthy, the `code`
part is executed. If it is falsy, the loop is exited.

The `condition` part can also be omitted. If it is, then it is assumed to be
truthy, and the loop will run forever. This is a nice shorthand for when you
want to execute an infinite loop.

For a demonstration of the while loop, say you wanted to implement a simple
collatz sequence. A collatz sequence is a sequence of numbers where each
number is either halved if it is even, or tripled and incremented by 1 if it
is odd. The sequence ends when the number 1 is reached.

The condition for the while loop might be:

```
:1≠
```

Which checks if the top of the stack is not equal to 1, while retaining the
value on the stack. The code to execute might be:

```
:2%0=[2/|3*1+}:,
```

Which halves the number if it is even, or triples it and increments it by 1.
It then prints the number without popping it.

Putting this all together, you get:

```
{:1≠|:2%0=[2/|3*1+}:,}
```

### The If/Else Structure

Vyxal 3 also has a more traditional if/else structure. Unlike versions 1 and 2,
this structure represents an if-statement with self-contained conditions.

The structure is:

```
#{condition|if_true|if_false}
#{condition|if_true|elif_condition|elif_true|...|else_code}
```

This is similar to a python if/elif/else statement. Rather than give a 
theoretical example, a direct translation of a python program will be given.

```python
if some_condition:
    print("some_condition is truthy")
elif some_other_condition:
    print("some_other_condition is truthy")
else:
    print("neither condition is truthy")
```

would be written as

```
#{some_condition|"some_condition is truthy"|some_other_condition|"some_other_condition is truthy"|"neither condition is truthy"},
```

An if statement with more than 3 branches will always have the `else` branch
as the last branch.

## Stack Control

All these structures are great, but things quickly become difficult if
the data you're working with is in the wrong order. For example, you might
need to swap the top two values on the stack, or you might need to duplicate
the top value on the stack so that you can use it in multiple places.

Vyxal has a number of elements that allow you to control the stack. These
elements include:

```
: -- Duplicate the top of the stack
$ -- Swap the top two values on the stack
_ -- Pop the top of the stack (unless following a number)
^ -- Reverse the stack
← -- Rotate the stack left
→ -- Rotate the stack right
W -- Wrap the entire stack in a list
` -- Length of the stack
```

More stack control elements may be added in the future.

## Functions

Right now, you have everything you need to solve every problem ever. Like
literally, the current subset of Vyxal is Turing complete. However, only
using structures and basic elements misses out on a lot of the power of Vyxal.
Indeed, the real power of Vyxal comes from doing cool things with functions.

Functions are a core part of Vyxal. They are first class objects, meaning
that you can have functions on the stack, functions can take functions as
arguments, and functions can return functions. Practical languages like
Python have such function features, so that'll be used as the base line
for comparison.

### Function Declaration

To create a function, you use the lambda structure. And you thought we
were done with structures! The lambda structure is:

```
λcode}
λarguments|code}
```

The `code` part is the code to execute when the function is called. The
`arguments` part is the argument list for the function. If it is omitted,
the function takes a single argument. This structure does not immediately
execute the function. Rather, it pushes the function onto the stack. One
way to think about this is that it pushes a reference to the function.

The idea is that functions are like python lambdas. They are anonymous
functions that can be passed around and used in other functions. For example,
say you wanted to add 5 to a number. In python, you might write:

```python
lambda x: x + 5
```

In Vyxal, you would write:

```
λ5+}
```

### Function Execution

When a function is executed, it pops its arguments from the stack, and
creates its own inner stack. This inner stack is used for all operations
within the function, and is destroyed when the function returns. This inner
stack does not interact with the outer stack in any way - popping from an
empty inner stack does not pop from the outer stack.