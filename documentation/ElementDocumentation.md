# Element documentation

This doc will go over implementing elements. The elements are all
implemented in [Elements.scala](/shared/src/main/scala/Elements.scala), while
the modifiers are in [Modifiers.scala](/shared/src/main/scala/Modifiers.scala).

When implementing elements, it's important that they are documented with
all their overloads and test cases; doing so means that test case
generation, as well as reference list generation can be easliy automated.
Consequently, it is important that a common format is followed for each
element.

## The Template

For elements:

```scala
val elementName = add...(
  "<character>",
  "Formal Name | Alternate Formal Name ...",
  List("literate-mode-keyword", "literate-mode-keyword", ...),
  arity, // only needed if using addDirect, all the other methods auto-fill this parameter
  vectorises?, // only needed if not using an add<Whatever>Vect
  "a: type, b: type -> expression",
  "a: type, b: type -> expression",...){
    implementation
  }
```

For modifiers:

```scala
val modifierName = Modifier(
  "Formal Name | Alternate Formal Name ...",
  """|Description
     |usage: what it does to f
  """.stripMargin,
  List("literate-mode-keyword"...)
 ){
   implementation
 }
```

## The Parts Explained

### `<character>`

The byte, or bytes, being documented (the symbol for the element, like `"+"`).

### `"Formal Name | Alternate Formal Name ..."`

One or two words that you would use to describe the element.
This is a name, so use title case.
For example, you might call `+` "Add", `R` "Reduce" and `Ẏ` "One Slice".
Separate different functions with pipes, like `€` "Split On | Fill By Coordinates".

### `"List("literate-mode-keyword", "literate-mode-keyword", ...)"`

A list of keywords that can be used for this element in literate mode. Please see [this document](literate-mode-naming.md) to learn about naming conventions.

### arity

The number of items the element pops from the stack.

### Overloads

For each overload of the element, what expression it evaluates to.
Use lowercase psuedocode, or Scala/Python.
The types to use are:

```
num - any number (int or Rational)
str - string
lst - list
any - any value
fun - function
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

### Vectorise

This is whether or not the default behaviour of the element is to
vectorise its functionality if given a list as an argument. Either true
or false.

## What `add...` Function Do I Use?

If you're implmenting something that takes a fixed number of arguments and always returns a single result (or is a nilad), use one of:

- `addNilad`
- `addMonad`
- `addDyad`
- `addTriad`
- `addTetrad`

If the element always vectorises, the `add<Arity>Vect` variants can be used.

If you're using a full function (something defined elsewhere) or something that can be expressed without type checks, use `add<Arity>Full` variants.

Otherwise, use `addDirect`.

## A Proper Example

```scala
val add = addDyadFull(
  "+",
  "Addition",
  List("add", "+", "plus"),
  true,
  "a: num, b: num -> a + b",
  "a: num, b: str -> a + b",
  "a: str, b: num -> a + b",
  "a: str, b: str -> a + b"
)(MiscHelpers.add)
```

## A little more detail on the `Elements` object

The `Elements` object contains a field `elements` of type `Map[String, Element]`, which
you can use to get an `Element` from its symbol. An `Element` object contains all the
information about an element, like the symbol, its name, its arity, some docs-related stuff,
and, most importantly, the implementation of that `Element`, represented by a `DirectFn`.

A `DirectFn` (in [Functions.scala](/shared/src/main/scala/Functions.scala)) is just a type
alias for `() => Context ?=> Unit`. That funny `?=>` thing means that the `Context` parameter
is implicit. Basically, a method like this would fit `DirectFn`:

```scala
def pop()(using ctx: Context): Unit =
  ctx.pop()
```

### Why's the `() =>` needed?

Why not just use `Context ?=> Unit`? For one, there's a (temporary)
implementation restriction disallowing that :(. Hopefully, Scala will eventually
allow having a function with implicit parameters without normal parameters
before it. Second, even if it were allowed to have `Context ?=> Unit`, the
`() =>` makes it easier to pass `DirectFn`s around.

Suppose `DirectFn` really was `Context ?=> Unit`. Then if you have a
variable `val x: DirectFn = ...` and you call some function like `f(x)`,
it's now ambiguous whether you want to pass the `DirectFn` `x` or if you want to
evaluate `x` using any implicit context in scope, and pass the result of that to
`f`. Of course, here, we know that it would be useless to pass a `Unit` to a
function, but the compiler isn't going to know that.

But since we're using `() => Context ?=> Unit`, we would have to use `f(x())`
for the compiler to think we want to evaluate `x`. This avoids the problem above.

## `Impls`

All the element implementations live inside `Impls`, which is a private singleton object inside `Elements`.
Why not put all the element implementations inside `Elements` directly? Because we want them to all be
inaccessible from the outside, so you want all of them to be private. If each and every element
implementation needed to be marked `private`, at some point, someone would inevitably forget to mark one
element private. It wouldn't be a big deal but it'd be kinda irksome. It's easier to mark `Impls` as private,
put the element implementations inside that, and leave them without accessibility modifiers.
