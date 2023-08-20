# Element documentation

This doc will go over implementing elements. The elements are all
implemented in [Elements.scala](/shared/src/Elements.scala).

When implementing elements, it's important that they are documented with
all their overloads and test cases; doing so means that test case
generation, as well as reference list generation can be easliy automated.
Consequently, it is important that a common format is followed for each
element.

## The Template

There are 4 methods that can be used to add an element, and each one is intended
for a different sort of element implementation:

- `addElem`
- `addVect`
- `addFull`
- `addDirect`

See [below](#what-add-function-do-i-use) for when to use which one of these.

```scala
val elementName = add<Elem|Vect|Full|Direct>(
  <Arity>, // Not needed for addDirect
  "<character>",
  "Formal Name | Alternate Formal Name ...",
  List("literate-mode-keyword", "literate-mode-keyword", ...),
  arity, // only needed if using addDirect, all the other methods auto-fill this parameter
  vectorises?, // only needed if using addFull
  "a: type, b: type -> expression",
  "a: type, b: type -> expression",
  ...) {
    implementation
  }
```

## The Parts Explained

### `<Arity>`

Since there's a few methods that need to be implemented separately for functions
of each arity, such as `vectorise`, there are objects `Monad`, `Dyad`, `Triad`,
and `Tetrad` which hold arity-specific methods for that object's arity. It is one
of these objects that you need to pass as the first parameter to `addFull`,
`addElem`, and `addVect` (`addDirect` is a little special).

So if you're implementing a monad that vectorises, you would use
`addVect(Monad, ...)`. If you're implementing a dyad that doesn't vectorise, you
would use `addElem(Dyad, ...)`.

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

`addDirect` doesn't take one of those `Monad`, `Dyad`, `Triad`, `Tetrad` objects
mentioned above. Instead it only takes an `Option[Int]` representing the
element's arity (the number of items the element pops from the stack). The other
`addX` functions don't need this argument.

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

- If the element needs access to the stack or `Context`, use `addDirect`
- If the element is being implemented using a pattern matching function (if it has `case =>`s):
  - If it covers all cases and doesn't vectorise, use `addFull`
  - If it vectorises, use `addVect`
  - Otherwise, use `addElem`
  - If you're unsure whether an element covers all cases, try using `addFull` at
    first. If the compiler yells at you about the match not being exhaustive,
    that means you're not covering all cases and need to use `addElem` or `addVect`.
    Otherwise, carry on using `addFull`! Alternatively, don't worry about it too
    much and just use `addElem` if it doesn't vectorise.
- If the element has already been implemented elsewhere (e.g. `MiscHelpers`),
  use `addFull`
- If the element must be implemented using a normal function literal (such as
  `a => a.toString`), use `addFull`

The reason `addFull` is separate from `addElem` and `addVect` is that the latter
two take `PartialFunction`s instead of normal functions. This allows us to call
[`isDefined`](https://www.scala-lang.org/api/3.2.1/scala/PartialFunction.html#isDefinedAt-4ad)
to check if the element will work on the arguments we want to pass it.

## A Proper Example

### `addFull` example

The behavior of `add` has already been implemented elsewhere, so we just use
that and call `addFull`.

```scala
val add = addFull(
  Dyad,
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

### `addVect` example

Suppose we want to add a very useful function that adds two numbers
and then adds 1 to the result. We'll make it vectorise too.

```scala
val addPlusOne = addVect(
  Dyad,
  "<something>",
  "Addition Plus One",
  List("<something>"),
  "a: num, b: num -> a + b + 1",
) {
  case (a: VNum, b: VNum) => a + b + 1
}
```

Because we used `addVect`, the element will be vectorised and work on numbers
and lists of numbers. However, when it meets anything other than that, it will
throw an error saying it doesn't work for those arguments.

### `addDirect` example

Suppose we want to make an element to pop the top 69 items off the stack and
then push `"420"` to it.

```scala
val pop69 = addDirect(
  "S",
  "Pop 69 Push 420",
  List("pop-69-push-420", "nice"),
  Some(69), // Takes 69 arguments
  "_ * 69 -> \"420\""
) { ctx ?=> // The funky ?=> thing is because ctx is an implicit parameter
  ctx.pop(69)
  ctx.push("420")
}
```

## A little more detail on the `Elements` object

The `Elements` object contains a field `elements` of type `Map[String, Element]`, which
you can use to get an `Element` from its symbol. An `Element` object contains all the
information about an element, like the symbol, its name, its arity, some docs-related stuff,
and, most importantly, the implementation of that `Element`, represented by a `DirectFn`.

A `DirectFn` (in [Functions.scala](/shared/src/Functions.scala)) is just a type
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
