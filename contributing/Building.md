# Build tools and tasks

This file is for documenting how to compile Vyxal and build JARs/executables, as
well as how to run other tasks (formatting, generating docs).

It looks like a long document, but don't worry, you don't actually need to read
most of it. If you're viewing this from GitHub, look at the table of contents.

We use the [Mill](https://mill-build.com/) build tool. If you've used Scala in
the past, you may be used to sbt, which is a lot more commonly used than Mill.
We originally used both sbt and Mill, and found that it was much easier to
define custom tasks with Mill.

It's also easier to set up, because the only thing you need to install is Java.
The root folder of this project includes two scripts, `mill` (for Linux and
MacOS) and `mill.bat` (for Windows). You can use these to run everything.
They'll download Mill for you, so you don't need to install stuff yourself.

## Projects

The build has 3 modules:

- `jvm` - For running Vyxal on the JVM. Includes code in `jvm/src` and `shared/src`.
- `js` - For compiling Vyxal to JS. Includes code in `js/src` and `shared/src`
- `native` - For compiling Vyxal to native executables. Includes code in `native/src` and `shared/src`

Each of these modules has a `test` module inside it (e.g. `jvm.test` includes
code in `jvm/src/test/scala` and `shared/src/test/scala`).

## Settings

All Mill settings go in [`build.sc`](/build.sc). Settings shared by the JVM, JS,
and Native modules go in the `VyxalModule` trait, and settings shared by their
test modules go in the `VyxalTestModule` trait. JVM-specific settings go in the
`jvm` object, JS-specific settings go in the `js` object, and native-specific
settings go in the `native` object. Each of those singleton objects have a `test`
object inside them that holds test settings for that platform.

I'd recommend checking out [`build.sc`](/build.sc) real quick before coming back
here. Don't worry if you don't understand any of it, all will be made clear soon
enough (also, you'll rarely need to modify it).

## Dependencies

In Mill, dependencies are declared in the `ivyDeps` task in the format
`ivy"groupId::artifactId::version"`. Here's an example:

```scala
def ivyDeps = Agg(
  ivy"org.typelevel::spire::0.18.0",
  ivy"org.scala-lang.modules::scala-parser-combinators::2.2.0",
)
```

> [!note]
> Java dependencies use a single colon between the group and artifact (`ivy"groupId:artifactId::version"`).

## Running tasks

If on Windows, you can run tasks using `.\mill <taskname>`. If on Linux or MacOS,
you can run tasks using `./mill <taskname>`. The rest of this guide will use
`./mill`, but if you're on Windows, make sure to use `.\mill` when running on your computer.

You can use `resolve` to find available tasks inside a module, e.g.
`./mill resolve jvm.test._` shows all tasks available for the JVM test module.
If you want to check if a task exists, you can do that too, e.g.
`./mill resolve jvm.doesthisexist` will give an error, while `./mill resolve jvm.assembly` won't.

If you want Mill to watch files and automatically recompile/rerun tasks, use the
`-w` option (e.g. `mill -w js.fastLinkJS` to keep building the JS every time you
make any changes).

## Building

To compile your code to see if there's any errors or warnings, use `<platform>.compile`,
e.g., `mill jvm.compile`.

### Building a JAR

`jvm.assembly` packages everything up into an uber jar and outputs to
`jvm/target/scala-<scalaVersion>/vyxal-<vyxalVersion>.jar`.

### Building a native executable

`native.nativeLink` will generate an executable. Beware, this is quite slow!
You probably want to let the GitHub workflow do this. The JVM version should be
good enough for most purposes.

### Building JS

If you want to quickly build and link the JS code for development purposes, without
too many optimizations, use

```sh
mill js.fastOptJS
```

When releasing, we use `js.fullOptJS` instead, which will perform all optimizations.
Both tasks output `lib/scalajs-<version>.js`.

## Running

To run the JVM version of the Vyxal REPL, use

```sh
mill -i jvm.runLocal
```

The `-i`/`--interactive` flag will tell Mill not to run as a client/daemon, so that
interactive programs (such as our REPL) will work. It's also necessary to use
`jvm.runLocal` rather than `jvm.run` so the REPL won't be run as a subprocess.

However, if you don't want the REPL, i.e., you want to use the Vyxal CLI non-interactively,
you can use `mill jvm.run ...<args>`.

To run the Native version of the Vyxal REPL, use

```sh
mill -i native.run
```

To run the JS version of the Vyxal REPL, use

```sh
mill -i js.run
```

Note that we haven't actually tried this. Also, for this to work, you need Node.js on your computer.

## Testing

See [`Tests.md`](./Tests.md) for more information.

## Formatting

We use Scalafmt to format our code. The configuration for it is in
[.scalafmt.conf](/.scalafmt.conf). Docs on configuration are
[here](https://scalameta.org/scalafmt/docs/configuration.html). `.scalafmt.conf`
won't have to be touched much.

Use the `reformat` task to automatically format code in a specific module, e.g.,

```sh
mill jvm.reformat
```

Or `checkFormat` to only check it:

```sh
mill jvm.checkFormat # Report issues in JVM code but don't fix it
```

Note that the above two tasks will not format *all* the code. For example,
`jvm.reformat` will format the shared code and the JVM-specific code, but not JS
or Native. To format every source file, use

```sh
mill mill.scalalib.scalafmt.ScalafmtModule/reformatAll __.sources
```

If you forget to format before committing, no problem! We have a workflow that
automatically formats the code. Just make sure to pull so you won't have merge
conflicts.

## Scalafix

Scalafix is a linting and refactoring tool. The configuration for it is in
[.scalafix.conf](/.scalafix.conf). Docs on configuration are
[here](https://scalacenter.github.io/scalafix/docs/users/configuration.html).
`.scalafix.conf` will probably be modified even less often than `.scalafmt.conf`.
As of writing, Scalafix's support for Scala 3 is pretty limited (only syntactic
rules are checked, not semantic rules). Still, it doesn't hurt. We run it in a
workflow so that every commit is checked for issues.
