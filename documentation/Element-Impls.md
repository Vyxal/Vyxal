# Element implementations

This doc will go over the elements' implementations. The elements are all
implemented in [Elements.scala](/shared/src/main/scala/Elements.scala).

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

<!-- todo turn into a normal Markdown (sub)-section? HTML is annoying -->
<details>
  <summary>Why's the <code>() =></code> needed?</summary>
  Why not just use <code>Context ?=> Unit</code>? For one, there's a
  (temporary) implementation restriction disallowing that :(. Hopefully, Scala
  will eventually allow having a function with implicit parameters without normal parameters
  before it. Second, even if it were allowed to have <code>Context ?=> Unit</code>, the
  <code>() =></code> makes it easier to pass `DirectFn`s around.

  Suppose <code>DirectFn</code> really was <code>Context ?=> Unit</code>. Then if you have a
  variable <code>val x: DirectFn = ...</code> and you call some function like <code>f(x)</code>,
  it's now ambiguous whether you want to pass the <code>DirectFn</code>
  <code>x</code> or if you want to evaluate <code>x</code> using any implicit context in scope,
  and pass the result of that to <code>f</code>. Of course, here, we know that it would be useless
  to pass a <code>Unit</code> to a function, but the compiler isn't going to know that.

  But since we're using <code>() => Context ?=> Unit</code>, we would have to use <code>f(x())</code>
  for the compiler to think we want to evaluate <code>x</code>
</details>

## `Impls`

All the element implementations live inside `Impls`, which is a private singleton object inside `Elements`.
Why not put all the element implementations inside `Elements` directly? Because we want them to all be
inaccessible from the outside, so you want all of them to be private. If each and every element
implementation needed to be marked `private`, at some point, someone would inevitably forget to mark one
element private. It wouldn't be a big deal but it'd be kinda irksome. It's easier to mark `Impls` as private,
put the element implementations inside that, and leave them without accessibility modifiers.
