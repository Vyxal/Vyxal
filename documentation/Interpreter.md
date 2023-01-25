# The Interpreter

*This file is to accompany [Interpreter.scala](/shared/src/main/scala/Interpreter.scala). For a general overview of how the whole interpretation process works, visit [INSERT MD FILE HERE](link).*

The interpreter file is the main brains of the whole Vyxal project - it's where the pipeline flows to after reading all neccesary inputs (like program files) and where vyxal programs are lexed and parsed. It also handles execution of Vyxal programs, via the `execute` function. There are two overloads of the execute function:

```scala
def execute(code: String)(using ctx: Context): Unit
```

and

```scala
def execute(ast: AST)(using ctx: Context): Unit
```

Don't worry about the `using`, it'll be explained [later](#the-ctx-parameter). If
you're curious about those multiple parameter lists, see [below](#multiple-parameter-lists).

The `code: String` overload of `execute` is for programss that are still in their string form. This overload takes the string, lexes and parses it, and then hands it to the `ast: AST` overload of execute.

The `ast: AST` overload of `execute` is the one that actually does the executing.
After parsing, the result is an `AST.Group`, which is an AST object that contains a list of `AST` objects that are executed sequentially. `AST`s are evaluated according to the following rules:

| AST Object           | How it's Executed | [Context Variable]((./README.md/#context-variables)) Involved? |
|----------------------|-------------------|----------------------------|
| `AST.Number`         | Value simply pushed to stack | ❌ |
| `AST.Str`            | Value simply pushed to stack | ❌ |
| `AST.Lst`            | Each AST group in the list's items are evaluated. The list of results are pushed to the stack. | ❌ |
| `AST.Command`        | The element name is indexed into the element dictionary (in [`Elements`](/shared/src/main/scala/Elements.scala)) | ❌ |
| `AST.Group`          | Each AST in the group is executed individually | ❌ |
| `AST.CompositeNilad` | Same as `AST.Group`. This AST type is for arity grouping purposes. | ❌ |
| `AST.If`             | Pop the top of the stack, truthy: execute truthy branch, else: execute falsey branch if present | ❌ |
| `AST.While` | While the condition branch evaluated on the stack is truthy, execute the loop body. | ✅<br><br>`N` = Last condition value<br>`M` = Number of while loop iterations (current loop index) |
| `AST.For`            | Pop the top of the stack, cast to iterable, and execute body loop for each item in that. | ✅<br><br>`N` = Current loop item<br>`M` = Current loop index |
| `AST.Lambda`         | Push a `VFun` object to the stack that represents the lambda | ❌(no context variable is set when pushing, but context variable may be set when executing function) |
| `AST.FnDef`          | Set variable equivalent to function name to lambda that represents function body | ❌ |
| `AST.GetVar`         | Push value of variable name to stack | ❌ |
| `AST.SetVar`         | Pop value from stack and set the variable with corresponding name to that | ❌ |
| `AST.AugmentVar`     | Push the value of the variable to the stack, execute the associated variable and pop that result into the same variable | 🟨 (execution of the element may have context variables set if the element is a lambda) |
| `AST.UnpackVar`      | Explained separately below | ❌ |
| `AST.ExecuteFn`      | Execute the corresponding function object and push the result to the stack (explained in more detail [below](#executing-functions)) | 🟨<br><br>`N` = function argument<br>`M` depends on how the function is called. |

## `AST.UnpackVar`

When an `AST.UnpackVar` is executed, a depth map of each variable is created (so something like `[x|y|[z]]` will turn into `[["x", 0], ["y", 0], ["z", 1]]`). This is then turned into a ragged list that matches the visual structure of the variables. The top of the stack is then popped and molded to that ragged list. Variable names and corresponding values are then zipped into a single list of `[String, VAny]` using a method that can be seen as overlaying one list on top of another and pulling out pairs that overlap.

## Contexts

If you know what `Context` is, feel free to [skip](#runtime-values) this section.

Note: the `Context` class is not to be confused with context variables, which
are a Vyxal feature. The `Context` class is an implementation thing that has
nothing to do with context variables apart from the fact that the `Context`
class holds the two context variables for the current scope.

### The `ctx` parameter

TL;DR: `ctx` is an implicit parameter ([docs][implicit docs]). You can skip
[ahead](#the-context-class) if you want.

You'll notice that `Interpreter`'s `execute` methods take a parameter of type
`Context`. As mentioned before, it's also in its own parameter list, with a
`using` in front of it. That `(using ctx: Context)` parameter list is called a
using clause. The official documentation for them is [here][implicit docs].
Feel free to read all of that if you like, but they go into a lot of detail that
you don't really need to worry about right now. A parameter list with a `using`
at the front means that all the parameters in that list are **implicit**
parameters.

Implicits are one of Scala's most powerful features, but here we're using it for
the simplest of use cases - we're code golfers and we want to write as little
code as possible :). If you've worked on Vyxal 2, you'll remember that nearly
every element implementation needs `ctx` passed to it, and sometimes we'd forget
to do that and it would error. Here, making `ctx` an implicit parameter avoids
all that.

Since `ctx` is an implicit parameter, if there's an
implicit value of type `Context` in scope, when calling `execute`, you won't
have to explicitly pass that value, you can just say `execute(myAst)` and the
compiler will automatically pass that implicit `Context` for you. If you want
to explicitly pass the context, you can do `execute(myAst)(using myContext)`.

If you are inside a method that has an implicit `Context` parameter (like
the `execute` method(s) inside `Interpreter` [above](#the-interpreter))

### The `Context` class

Now, on to the [`Context`][Context] class itself. Back in the days of Vyxal 2.4.x, there was a code clean-up planned. However, it
resulted in the horrible mess that was the 2.5.x releases. One of the reasons
the original code clean-up failed is that the one-file interpreter relied too
heavily on global variable; in order to access values such as flag settings,
input levels and context values (the ones used with n), variables had to be
scoped as global within functions. Ignoring the fact that global variables are
generally a bad programming practice, this was an issue because you can't access
global variables from one file inside another file.

The best solution to this was to have a special Context class that contains all
the variables that were previously global. And it's the same solution that's
used in Vyxal 3.x releases.

At the start of the interpreter, an instance of Context called ctx (think
`ctx = Context()`) will be created. This will need to be passed between element
functions, meaning they all need a ctx parameter.

The `Context` class is used for keeping track of everything in the current
execution context/scope. Every scope (while loops, for loops, functions) gets
its own child context holding the stack, variables, inputs, and a few other
things for that scope. The `Context` class also has a `globals` field. All the
`Context`s have the same [`Globals`](/shared/src/main/scala/Globals.scala) object
in their `globals` field, and it holds the settings (set using flags), the
global inputs (passed in the "Inputs" field of the online interpreter), and the
register.

Every element's implementation takes a `Context` as input, even if it
doesn't directly use it. See [ElementDocumentation.md] for information on how the
elements are implemented.

`Context` has methods for pushing to and popping from the stack, getting and
setting variables, and creating child contexts. See the [Context] class for all
of them.

## Runtime values

Vyxal has 4 basic types:

- Strings
- Numbers
- Functions
- Lists (which are heterogeneous and may contain any of these 4)

Internally, the [`VAny`](/shared/src/main/scala/VAny.scala) type is
used. It's a [union type], defined as `String | VNum | VFun | VList`.

### `String`

Represents a Vyxal string. This is simply Scala's own `String` class, so there's
no need for additional conversions or anything.

### `VNum`

Represents a Vyxal number. Under the hood, it uses a `Complex[Real]` (a complex
number whose components are real numbers) from the
[Spire](https://typelevel.org/spire/) library. There are implicit conversions
from `Int`s and `Double`s and stuff to `VNum` for convenience.

### `VFun`

Represents a Vyxal function object (*not* a function definition). It holds the
implementation of the function, its arity, its parameters (optional), and the
`Context` it was defined in (the reason for keeping the context is given
[here](#executing-functions)).

### `VList`

Represents a Vyxal list. Scala doesn't allow recursive type aliases, so it's
a whole class of its own wrapping around a `Seq[VAny]` (a class from Scala's
standard library).

## Executing functions

The `Interpreter.executeFn` method is used to execute a function. It's actually
called when someone uses the `Ė` (execute) element (note: this symbol was used
at the time of writing but it may be different when you're reading it). When
that element is used, the top of the stack is popped and if it's a function
object (a `VFun`), `Interpreter.executeFn` is called with that popped `VFun` as
input.

There, a new `Context` is made for the function to execute in. It's a
Frankensteinian monster made from the current context and the context the
functin was defined in.

- The new context's `inputs` (arguments) are obtained by popping the necessary
  number of elements off the outer context's stack (given by the `arity` field
  in `VFun`)
- The new context's `stack` is completely empty
- The new context's map of variables are copied from the context that the
  function was defined in (just the `Map`, not the values of the variables
  themselves). In addition to this, it has access to variables to the outer
  context (the `parent` context).

### Multiple parameter lists?!

Scala lets you define a method with multiple parameter lists so that you can do
[currying](https://en.wikipedia.org/wiki/Currying) more easily. These two
methods are basically equivalent (Scala does compile them to different things,
but we don't need to worry about that):

```scala
def foo(a: A)(b: B): C = c(a, b)
def foo(a: A): (B => C) = (b: B) => c(a, b)
```

Both can be called using `foo(a)(b)` (or `foo(a)`, to get a value of type
`B => C`).

[Back to top](#the-interpreter)

---

[implicit docs]: https://docs.scala-lang.org/scala3/book/ca-given-using-clauses.html
[Context]: /shared/src/main/scala/Context.scala
[ElementDocumentation.md]: ./ElementDocumentation.md
[union type]: https://docs.scala-lang.org/scala3/book/types-union.html
