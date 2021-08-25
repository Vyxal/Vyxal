# Element Documentation

When implementing elements, it's important that they are documented with
all their overloads and test cases; doing so means that test case
generation, as well as reference list generation can be easliy automated.
Consequently, it is important that a common format is followed for each
element.

## The Template

```yaml
- element: "<character>"
  name: <one or two word proper name for the element>
  arity: <0/1/2/3/*>
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

This is very simple: it's the byte being documented

### Name

This is one or two words that you would use to describe the element.
For example, you might call `+` "add", `R` "reduce" and `Ẏ` "One Slice".

### Arity

This is very simple: how many items does the element pop from the stack?

### Description

This is what you would say the element does in general, regardless of
type overloads.

### Overloads (\*)

For each overload of the element, list what expression it evaluates as.
The types to use are:

```
num - any number (int or Rational)
str - string
lst - list
any - any value
```

### Vectorise (\*)

This is whether or not the default behaviour of the element is to
vectorise its functionality if given a list as an argument. Either true
or false.

### Tests (\*)

These are expected input and output. Make sure to cover edge cases here.
Also, use python expressions. The example stack is to be what the
stack would look like and the expected output is what the top of the
stack would be after using the element. Each test case needs to be
wrapped in `'` to make it valid YAML.

## A Proper Example

```yaml
- element: '+'
  name: add
  arity: 2
  description: adds the top two items on the stack
  overloads:
    num-num: a + b
    num-str: str(a) + b
    str-num: a + str(b)
    str-str: a + b
  vectorise: true
  tests:
    - '[1, 1] -> 2'
    - '[0, -5] -> -5'
    - '["abc", 5] -> "abc5"'
    - '[5, "abc"] -> "5abc"'
    - '["Hello, ", "World!"] -> "Hello, World!"'
    - '[[1,2,3], 4] -> [5, 6, 7]'
    - '[[1,2,3], [4,5,6]] -> [5, 7, 9]'
```
