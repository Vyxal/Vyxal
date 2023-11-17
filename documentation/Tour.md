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
7. [Glossary](#glossary)

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

Strings can contain any character within the Vyxal [codepage](#codepage) (well, technically any character can be used, but using a character outside the codepage requires UTF-8 scoring). To include a `"` in a string, escape it with a `\`. For example:

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

I haven't written this section yet because it's 11:33pm and writing all the
above took like half an hour.

## Glossary

### Codepage

The codepage is a byte-to-character mapping that Vyxal uses to display bytes
as characters.


