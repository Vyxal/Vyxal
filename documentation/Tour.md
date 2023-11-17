# A Tour of Vyxal 3

## Table of Contents

- [Introduction](#introduction)
- [Stacks](#stacks)
- [Numeric Literals](#numeric-literals)
- [Glossary](#glossary)

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

## Strings

Another very useful type of value that can exist on the stack is Strings.

## Glossary
