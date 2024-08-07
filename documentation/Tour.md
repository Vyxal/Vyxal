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
6. [Vectorisation](#vectorisation)
7. [Control Flow](#control-flow)
8. [Stack Control](#stack-control)
9. [Input/Output](#io)
10. [Functions](#functions)
11. [Context](#context)
12. [Specialised Structures](#specialised-structures)
13. [Modifiers](#modifiers)
14. [Arity Grouping](#arity-grouping)
15. [Variables](#variables)
16. [What is a SBCS?](#single-byte-character-set)
17. [Nilad Moving](#nilad-moving)

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

## Vectorisation

It is worth noting that a lot of elements in Vyxal are vectorised. Vectorisation
is when a function is applied to each element of a list, rather than the list
as a whole. Essentially, vectorisation is a form of implicit mapping.

For example, say you wanted to add 1 to each element of a list. In a traditional
language, you might write:

```python
[1, 2, 3, 4, 5].map(lambda x: x + 1)
```

or

```python
lst = [1, 2, 3, 4, 5]
for i in range(len(lst)):
    lst[i] += 1
```

In Vyxal, you would write:

```
#[1|2|3|4|5#]1+
```

The `+` is automatically applied to each item in the list.

Vectorisation will "dig down" into nested lists until it reaches a non-list
value. This is what is known as "pervasiveness" or "deep vectorisation".

### Vectorisation and Arity

How many arguments a function takes (its arity) impacts how vectorisation is
performed.

If an element is monadic (takes one argument), then it is applied to each
non-list item in the list. This occurs regardless of the type of the top
of the stack. Numbers are converted to ranges.

If an element is dyadic (takes two arguments), the following table describes
how it is applied:

| Top of Stack (`a`) | Second on Stack (`b`) | Result                           |
| ------------------ | --------------------- | -------------------------------- |
| List               | List                  | `[a op b for a, b in zip(a, b)]` |
| List               | Non-list              | `[x op b for x in a]`            |
| Non-list           | List                  | `[a op x for x in b]`            |
| Non-list           | Non-list              | `a op b`                         |

If an element is triadic (takes three arguments), the following table describes
how it is applied:

| Top of Stack (`a`) | Second on Stack (`b`) | Third on Stack (`c`) | Result                                      |
| ------------------ | --------------------- | -------------------- | ------------------------------------------- |
| List               | List                  | List                 | `[a op b op c for a, b, c in zip(a, b, c)]` |
| List               | List                  | Non-list             | `[x op y op c for x, y in zip(a, b)]`       |
| List               | Non-list              | List                 | `[x op b op y for x, y in zip(a, c)]`       |
| List               | Non-list              | Non-list             | `[x op b op c for x in a]`                  |
| Non-list           | List                  | List                 | `[a op x op y for x, y in zip(b, c)]`       |
| Non-list           | List                  | Non-list             | `[a op x op c for x in b]`                  |
| Non-list           | Non-list              | List                 | `[a op b op x for x in c]`                  |
| Non-list           | Non-list              | Non-list             | `a op b op c`                               |

If an element takes 4 arguments, a similar method to the triadic case is used.
The behaviour table is omitted for brevity.

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

## Input and Output

99.9% of all code golf challenges require input and output. The other .1% are
closed as unobservable. Vyxal has two forms of input and output: implicit and
explicit. Explicit IO is the easiest to explain - `,` is used to send values
to STDOUT, and `?` is used to read values from argv/STDIN. Upon reading 
from argv/STDIN, `?` will attempt to evaluate the input as a number, then
a list, and finally as a string literal. Failing all 3, it will push 
the input as-is, as a string.

_Note that `,` is not the only way to print to STDOUT. Indeed, there is an
element to print without a trailing newline (`,` prints with a trailing
newline), and an element to print without popping. Likewise, there will
be a "read from STDIN" element at some point to explicitly read from STDIN_

Implicit IO is split into two parts: implicit input and implicit output. Implicit
output occurs at the end of program execution if no explicit output has occurred.

Implicit input occurs when popping from an empty stack. In most practical
stack languages, popping from an empty stack is an error. In Vyxal, it 
is the equivalent of inserting `?`s before an element.

For example:

```
+
```

will first try to pop a lhs value from the stack. Seeing nothing on the stack,
it reads from argv/STDIN. 
It will then try to pop a rhs value from the stack. Seeing nothing on the stack,
it once again reads from argv/STDIN.

### Argv vs STDIN

By default, input is read from argv (command line arguments). This is the
preferred method of input, as it cleanly lets the interpreter know how
much input there is. However, if no inputs are provided on the command line,
input is read from STDIN (standard input).

When input is read from argv, the interpreter is able to cycle through
inputs once it reaches the end. For example, if the program `+` is run
with the arguments `1`, the result will be `2`. The `1` is reused.

When input is read from STDIN, the interpreter does not cycle through inputs.
After all, how would it know when to stop? Instead, the interpreter will
prompt for more input each time it is needed.

### Empty Input

If no input at all is provided (e.g. running the interpreter online with no 
inputs in the input box -- inputs are treated as argv online), 
the interpreter will push `0` to the stack.

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

### Function Arguments

The `arguments` part of the lambda structure requires its own section, as it
veers away from the usual "push a value onto the stack" syntax. The argument
list defines the parameters a lambda will take. Arguments can be: a) unnamed
arguments popped directly from the stack, b) named parameters, or c) variadic
parameters. 

Rather than trying to explain the syntax of the argument list, it's probably
easier to just give some examples. The following are all valid argument lists:

```
λ3|...} # Pop 3 values from the outer stack. Push them to the inner stack.
λ1|...} # Pop 1 value from the outer stack. Push it to the inner stack.
λname|...} # Pop 1 value from the outer stack. Assign it to the variable `name`.
λ*|...} # First, pop a number from the stack. Then, pop that many arguments from the stack and push to inner stack.
λ3,name|...} # Pop 3 values from the outer stack. Push them to the inner stack. Then pop another value from the outer stack and assign it to the variable `name`.
λa,b,c|...} # Pop the top of the stack into `a`, the second value into `b`, and the third value into `c`.
λ3,4|...} # Pop 3 values from the outer stack. Push them to the inner stack. Then pop 4 values from the outer stack and push them to the inner stack.
λ7|...} # Not exactly equivalent to the above. Pops 7 consecutive values from the outer stack and pushes them to the inner stack.
λ1,name,2|...} # Names and numbers can be mixed together
λ3,name,*|...} # As can varargs
λ*,*,*|...} # As can multiple varargs
λ!|...} # A `!` indicates that the function operates on the outer stack. All pops and pushes are done on the outer stack.
```

### Function Execution

When a function is executed, it pops its arguments from the stack, 
according to the argument list. These arguments are pushed to an inner
stack. This inner stack is used for all operations within the function, 
and is destroyed when the function returns. This inner stack does not 
interact with the outer stack in any way, unless the function is marked
with a `!` in the argument list.

The function stores the first argument in the context variable `n`. This
is the same `n` that is used in the for loop structure. The function will
also store an additional value in the context variable `m`, depending on
how the function was called. More on that in the next section.

Functions can recursively call themselves by using the `x` element. This
will execute the function again, taking its arguments from the inner stack.

Returning early from a function is done with the `X` element. This will
halt execution of the function, and push the top of the inner stack to
the outer stack.

When a function returns, it pushes whatever is on the top of the inner stack
back onto the outer stack.

### Specialised Lambda Structures

There are a few specialised lambda structures that are shortcuts for
common lambda-element combinations. These are:

```
ƛ...} # Mapping lambda. Equivalent to λ...}M
Ω...} # Filter lambda. Equivalent to λ...}F
₳...} # Accumulation lambda. Equivalent to λ...}R
µ...} # Sorting lambda. Equivalent to λ...}ṡ
```

Mapping lambdas, filter lambdas, and sorting lambdas also have another special
feature: multiple branches. Where a normal lambda only has a single code
section, these lambdas can have multiple code sections.

For mapping lambdas, each code section resets the context variable `n` to
whatever is on the top of the inner stack. This will make more sense when
context variables are discussed later.

For filter lambdas, each code section acts as an additional filter. Essentially,
the `|`s become logical ands. This is helpful for when you want to filter
by multiple conditions without using multiple `n`s.

For sorting lambdas, each code section acts as an additional key. The first
code section is the primary key, the second code section is the secondary key,
and so on.

Normal lambdas and accumulation lambdas do not perform any special actions
with multiple code sections.

### Printing Functions 

Whenever you send a function object to stdout, it calls the function and prints the result.

## Context

The concept of a "context" has been alluded to a few times now, but what is it?
Well, vyxal contexts are what they sound like - different environmental settings
that depend on the type of structure being executed. Each context has two
dedicated variables that are elements: `n` and `m`. `n` is referred to as the
primary context variable, and `m` is referred to as the secondary context
variable. The meaning of these variables depends on the context (ha) of the
structure.

### Global Context

The global context is the context that is used when no structure is being
executed. `n` is set to the string `"abcdefghijklmnopqrstuvwxyz"`. `m` is simply set to `0`.

### For Loop Context

Within a for loop, `n` is set to the current iteration value. `m` is set to the
current iteration index.

### While Loop Context

Within a while loop, `n` is set to the last value of the condition. `m` is set
to the number of iterations.

### Standard Function Context

In addition to the inner-stack mentioned in the function section, functions
set `n` to the first argument, and `m` to the list of all arguments.

Functions can also have their argument list indexed using `¤`. `¤<number>` is equivalent to `m<number>i`.

### Mapped Function Context

The way a function is called can also affect the context. If a function is
called using the `M` element, or through a mapping lambda, then `n` is set to
the current value being mapped over. `m` is set to the index of `n` in the
original list.

### Filtered Function Context

Like mapped functions, filtered functions also have their context affected by
the way they are called. If a function is called using the `F` element, or
through a filter lambda, then `n` is set to the current value being filtered.
`m` is set to the index of `n` in the original list.

### Accumulated Function Context

Accumulated functions are called using the `R` element, or through an
accumulation lambda, or by using the fold/scan modifiers (more on those later).
Typically, such a function is dyadic, so `n` is set to the accumulator value,
and `m` is set to the next value in the list.

### Sorted Function Context

Sorted functions are called using the `ṡ` element, or through a sorting lambda.
`n` is set to the current value being sorted, as is `m`. This may be changed
in the future.

## Specialised Structures

There exist two structures within the Vyxal language that don't fit in
the category of "control flow" or "function". These are the "specialised
structures". These structures are the decision problem structure, and the
generator structure.

### The Decision Problem Structure

A common problem in code golf is to determine whether an input contains
an item that satisfies a certain condition. It can be seen as a shortcut
for `ƛ...}a` or `Ω...}ȯ`.

The structure is:

```
Ḍpredicate|iterable}
```

The `predicate` part is the predicate to check. The `iterable` part is the
iterable to check. If `iterable` is omitted, it is assumed to be the top of
the stack.

The decision problem structure will push `1` if the predicate is satisfied
by any item in the iterable, and `0` otherwise.

### The Generator Structure

The generator structure is used to generate a list of values. It maintains a
list of all values generated, and allows those values to be used to generate
new values. Basically, a state-mainaining generator like python's `yield`.

The structure is:

```
Ṇcode|inital vector}
```

The `code` part is the code to execute to generate the next value. It operates
on a stack pre-filled with all previously generated values. The `initial vector`
part is the initial vector to use. If it is omitted, it is assumed to be the
top of the stack.

The generator structure will push the generated list to the stack, which will
infinitely generate values. A way to stop the generator will be added in the
future.

## Modifiers

Most elements in Vyxal operate on stack values - they pop values from the stack
and push their result back onto the stack. However, some elements modify the
behaviour of other elements - instead of popping values from the stack, they
modify the behaviour of the next element. These are called "modifiers".

For example, the most commonly used modifier is the `ᵛ` modifier. This modifier
`ᵛ`ectorises (vectorises) the next element over the top of the stack. For
elements that do not vectorise by default, this allows them to be vectorised.

Another common modifier is the `/` modifier. This modifier is used to reduce
a list of values by a function. It's as if you wrapped the element in a lambda
and then used the `R` element.

That's the case with most modifiers - they are just shorthand for wrapping
an element in a lambda and then using another element. However, there are
cases where modifiers perform different actions based on the arity of the
modified element.

Going back to `/` for a second, `/` will reduce if the element is dyadic,
but will instead filter if the element is monadic. This is because it makes
no sense to reduce a list of values by a monadic function. When there
are multiple possible actions for a modifier, the different actions are
documented in the modifier's help text.

### Dyadic Modifiers

Some modifiers take two elements instead of one. One such modifier is the
`∥` modifier, which is called "parallel apply". This modifier takes two
elements, and applies them to the same stack as if they were executed
with their own copy of the stack. The results of both elements are then
pushed to the stack.

For example:

```
3 4∥+×
```

results in the stack:

```
12 -- top
7  -- bottom
```

### Shorthand Lambda Modifiers

Some modifiers are shorthand for wrapping a number of elements in a lambda.
These modifiers will take the next _n_ elements, wrap them in a lambda, and
then push the lambda to the stack. These modifiers are:

| Modifier | Number of Elements | Arity of Lambda |
| -------- | ------------------ | --------------- |
| `⸠`      | 1                  | 1               |
| `ϩ`      | 2                  | 1               |
| `э`      | 3                  | 1               |
| `Ч`      | 4                  | 1               |
| `ᵈ`      | 1                  | 2               |
| `ᵉ`      | 2                  | 2               |
| `ᶠ`      | 3                  | 2               |
| `ᴳ`      | 4                  | 2               |

## Arity Grouping

Sometimes, you'll feel like a series of elements should be grouped together
when using a modifier. For example, you might expect `5+` to be a single
element, as it's effectively a monadic element. Therefore, Vyxal will
automatically group elements together if they effectively act as a single
element. This is called "arity grouping".

The following arity sequences are grouped together:

```
0 1 # grouped as a nilad
0 0 2 # grouped as a nilad
0 0 0 3 # grouped as a nilad
0 0 0 0 4 # grouped as a nilad
0 2 # grouped as a monad
0 0 3 # grouped as a monad
0 0 0 4 # grouped as a monad
```

This can best be seen with the lambda shorthand modifiers. For example:

```
⸠5+ # Pushes a monadic lambda that adds 5 to its argument
ϩ5+8× # Pushes a dyadic lambda that adds 5 to its first argument and multiplies it by 8
```

While it looks like `⸠5+` should be `⸠5` then `+`, it is grouped as a monad.

Arity grouping stacks too. For example, `5 4++` is grouped as a monad, as
`5 4+` is first grouped as a nilad. That leaves a nilad-dyad pattern,
which is grouped as a monad.

It may not make sense now, but it will when you start writing more complex
programs.


## Variables

So far, a lot of features have involved a lot of concepts that may be new to
people who haven't experienced a stack/array/golfing language before. However,
to ease people into the language, Vyxal has support for locally scoped variables.
When golfing, variables end up being unnecessary most of the time, but they
can be useful for readability and for longer/more complex programs outside of
golfing.

Variables are declared by writing `#=` followed by the variable name. This will
pop the top of the stack and assign it to the variable. For example:

```
42 #=x
```

will assign the number `42` to the variable `x`. Variables can be retrieved
by writing `#$` followed by the variable name. For example:

```
#$x
```

will push the value of `x` to the stack. An unassigned variable will push `0`
to the stack.

Variables are local to the current scope. That is to say, if you declare a
variable in a function, it will only exist within that function. And assigning
variables inside a scope will not affect variables outside of that scope.

Global variables will be added in the future, as version 2 had them and they
are a neat little feature.

### Constant Variables

Variables can be declared as constant by using `#!` instead of `#=`. This
will prevent the variable from being reassigned. Attempting to reassign a
constant variable will result in an error.

## Single Byte Character Set

At the very top of this tour, there was a statement that this document
assumes SBCS syntax. This is because Vyxal has two syntaxes: SBCS and
literate mode. But what is a SBCS? 

SBCS stands for "single byte character set". It is a mapping from bytes
to a set of human readable characters. Indeed, when writing Vyxal code,
you are technically writing bytes, not characters. But for convenience, 
UTF-8 characters are used to represent the bytes. Imagine if you had to
write your code in binary or hex!

The SBCS used by Vyxal is a custom codepage. It can be found 
[in codepage.txt](./codepage.txt) and [in codepage.md](./codepage.md).

It's important to know that a SBCS is used, as it means that only the
characters in the codepage can be used in Vyxal code. If you need to
use a character outside of the codepage, you will have to use UTF-8
scoring.

By default, the interpreter will assume that the code is in UTF-8. This
may seem like it contradicts the point of a SBCS, but it is done for
user convenience. Indeed, the SBCS is only for code golf scoring purposes.
To prove to someone that your program is indeed x amount of bytes, you
can use the `-v` or `--bytes` flag. This will tell the interpreter to
read the input program as a series of bytes, rather than UTF-8 characters.

## Conclusion

That's the end of the tour. Hopefully you've learned something about Vyxal,
and are now ready to start writing your own programs.