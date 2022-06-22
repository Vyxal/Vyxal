# Element documentation

When implementing elements, it's important that they are documented with
all their overloads and test cases; doing so means that test case
generation, as well as reference list generation can be easliy automated.
Consequently, it is important that a common format is followed for each
element.

## The Template

```yaml
- element: "<character>"
  name: <one or two word proper name for the element>
  arity: <0/1/2/3/*/NA>
  description: <short general description of the command>
  overloads:
    type1-type2: <expression>
    type3-type4: <expression>
    # ...
 vectorise: <true/false>
 tests:
    - '[example stack] : expected result'
    - '[example stack] : expected result'
    - '[example stack] : expected result'
```

```yaml
- modifier: "<character>"
  name: <one or two word proper name for the modifier>
  arity: <0/1/2/3/*> + <0/1/2/3/*>
  usage: <modifier><elementA><elementB> # as many/as few as needed.
  description: <short general description of the modifier>
```

## The Parts Explained

Parts marked with an asterisk (\*) aren't required for elements relating to structures.

### Element

The byte, or bytes, being documented.

### Name

One or two words that you would use to describe the element.
This is a name, so use title case.
For example, you might call `+` "Add", `R` "Reduce" and `Ẏ` "One Slice".
Separate different functions with slashes, like `€` "Split On / Fill By Coordinates".

### Arity

The number of items the element pops from the stack.

### Description

What the element does in general.
One or two sentences, in sentence case.
Separate different functions with slashes.
Avoid mentioning the element's arguments.
This is not the place to specify the exact behavior, or the behavior on edge cases.

---

*Bad:*

> Round a to b decimal places

This mentions the arguments `a` and `b`.

*Bad:*

> Round a number

This description makes the element sound like it only takes one argument,
a number, when it takes two arguments.
If you were looking for this element, you might read this description and
think that the element only rounds to the nearest integer.

*Better:*

> Round a number to n significant figures (not digits after the decimal point)

While this uses a variable `n`, it's not a specific argument like `a` or `b`.
That way, it won't be confused for whether the number of decimal places is
the first or the second argument.
The detail about "digits after the deimcal point" is perhaps better in the
overloads, however.

*Better:*

> Round a number to n decimal places

The description is short.
If you're looking for which element to use, and you read this description,
this is exactly enough information to tell if you want to use this element.

### Overloads (\*)

For each overload of the element, what expression it evaluates to.
Use lowercase psuedocode, or Python.
The types to use are:

```
num - any number (int or Rational)
str - string
lst - list
any - any value
```

This is the place to specify exact behavior.
For example, if it takes "every other element", does it start with the first
or second element?
What are the element's important edge cases?
How does the element handle empty cases?

---

*Bad:*

> - lst-fun: the minimum value of the list after applying the function to
> each element

This does not mention the arguments `a` or `b`.

*Better:*

> - lst-fun: minimum value of the list a by applying function b to each element

Naming the arguments is better, but it's not necessary to specify their types.
Their types are already given by the overload.
It's also not clear what "applying to each element" means here.

*Better:*

> - lst-fun: map b over a, then return the minimum (or [] if a is empty)
> - lst-fun: minimum of b(a[0]), b(a[1]), ..., or [] if a is empty
> - lst-fun: min( b(a[0]), b(a[1]), ... ), or [] if a is empty
> - lst-fun: min(map(b, a)), or [] if a is empty

The choice between these is a matter of preference.

For more details, use a general description followed by more details in parentheses.
For example:

> - str-str: overwrite the start of a with b (b + a[len(b):])

### Vectorise (\*)

This is whether or not the default behaviour of the element is to
vectorise its functionality if given a list as an argument. Either true
or false.

### Tests (\*)

These are expected input and output. Make sure to cover:

- All overloads, with at least one "typical" test case per overload.
The typical test case should show how to use the element.
It's nice if the typical test case can explain how the element works on its own.
- Edge cases.
If you have lists, what about empty lists?
If you have numbers, what about zero? Negative numbers? Rationals?
If you have strings, what about empty strings?
- Mixing types.
If you have numbers, do you accept stringified numbers?
If you have lists, do you accept strings as lists of characters?

Use Python expressions.
The example stack is a list, with the top of the stack at the end.
The expected output is the top of the stack after using the element.
Wrap test cases in `'` or `"` to make it valid YAML.

There's no need to test:
- The edge cases covered by other elements.
If an overload for A is equivalent to an overload for B, and B has edge case
tests for that overload, there's no need to add edge cases for that overload.
- Vectorisation, unless the vectorisation is unusual or complex.

## A Proper Example

```yaml
- element: '+'
  name: Add
  arity: 2
  description: Add the top two items on the stack
  overloads:
    num-num: a + b
    num-str: str(a) + b
    str-num: a + str(b)
    str-str: a + b
  vectorise: true
  tests:
    - "[1, 1] -> 2"
    - "[0, -5] -> -5"
    - '["abc", 5] -> "abc5"'
    - '[5, "abc"] -> "5abc"'
    - '["Hello, ", "World!"] -> "Hello, World!"'
```
