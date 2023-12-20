# Contributing

## Setting up a development environment

Vyxal v3 is written in Scala, which is a JVM language, so you're going to need
to install Java. That's the only requirement for working on Vyxal!

This is optional, but if you want to try the Scala REPL or use other Scala stuff,
you may want to install [Coursier](https://get-coursier.io/docs/overview).
Once you do that, you can either install Scala-related applications individually
(e.g. `cs install scalafmt`) or you can run [`cs setup`](https://get-coursier.io/docs/cli-setup)
to install a bunch of stuff at once.

### Editor

- [Metals](https://scalameta.org/metals/docs/) is an LSP language server that
  has a VS Code extension and a Sublime Text plugin. It can also be used with
  Vim and Emacs.
- IntelliJ also has a Scala plugin.

## Writing code

Vyxal v3 compiles to JARs that can run on the JVM, but it also compiles to JS so
that it can run online, and it also compiles to native executables.

Directory Guide (for people who aren't the best at mentally parsing nested folders):

- `shared` is where the Scala source code shared across the JVM and JS is.
  - `shared/src/` is where the interpreter files are.
    - `Elements` contains the definitions of the elements
    - `VAny` contains the definitions of the types of Vyxal's runtime values (numbers, strings, functions, lists)
  - `shared/test/src` is where the test files are
  - `shared/resources/` contains resources needed at runtime (currently only the
    dictionaries).
- `jvm/` is where all the JVM-specific stuff is. `jvm/src/vyxal/Main.scala` is
  the entry file for the command line--the `__main__.py` of the project in a
  sort of a way.
- `js` is where all the JS-specific stuff is.
- `native` is where all the Native-specific stuff is.

Each of these folders has an `src` folder which contains all the source code.

Now that you're done setting up, take a look at the README in the
[`contributing`](/contributing/) folder to get more information about how
the interpreter works so you can add to it.

## Building

See [Building.md](./Building.md) for instructions on compiling


## Testing

You'll want to add tests if you're adding a new element (to ensure it works) or
fixing a bug (so the bug doesn't pop up again). For more information on how to
write and run tests, take a look at [`Tests.md`](/contributing/Tests.md).

## Running the website locally

### Generating the JS

Generating `pages/vyxal.js` is already covered in [`Building.md`](/contributing/Building.md),
but here it is again:

- Run `mill js.fastLinkJS` to compile JS and produce `pages/vyxal.js`.
- If you want it to automatically rebuild JS every time you make changes, use
  `-w` like so: `mill -w js.fastLinkJS`

### Local HTTP server

If you want to test the site locally, you'll need to serve it from a local
server. That's because some browsers won't allow webworkers to load local files
from raw HTML files. You also won't be able to use dictionary (de)compression
since local requests will be blocked. There are a few ways to run the server:

- If you have Node.js installed:
  1. `npm install http-server`
  2. Make sure you're inside the Vyxal project folder (`cd Vyxal`)
  3. `http-server`
- If you have Python installed:
  - `python3 -m http.server --directory pages`
  - If you don't want to type all that out, you can also use the
    [`local_server.py`](/local_server.py) script in the root of this project:
    `python local_server.py`

And that's it - head over to 127.0.0.1:8080 (or whatever url it gives you) and test away.

## Running the JVM REPL

If you want to test out the fancy JLine REPL that only the JVM has, you can run

```sh
mill -i jvm.runLocal
```

The `-i` stands for interactive mode, and the `runLocal` must be used instead of
`run` so that it doesn't start a new process.
