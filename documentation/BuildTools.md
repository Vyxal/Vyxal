# Build tools and tasks

This file is for documenting how to use the two build tools the project supports
as well as the tasks we've defined for them. You can use either the [sbt](https://www.scala-sbt.org/)
build tool or the [Mill](https://com-lihaoyi.github.io/mill/mill/Intro_to_Mill.html) build tool.

## Should I use sbt or Mill?

If you already know what you want, skip ahead to [sbt](#sbt) or [Mill](#mill).<br>
If you just want to test out a single contribution you're making to Vyxal and you
don't plan on contributing to Vyxal in the future, go with Mill, since you won't
have to install anything other than Java.<br>
If you're a newcomer to Scala but plan on working on other Scala projects in the future,
sbt is probably a better choice, because that's what most other Scala projects use.<br>
If you're a newcomer to Scala and don't plan on working on other Scala projects in
the future, Mill is probably a better choice since you won't have to install anything
other than Java.

Getting up and running with Mill is much easier than with sbt, if that's what you're
concerned about. You just need to install Java, and then you can run the `mill`
script included in the root directory of this project. With sbt, the process is
slightly more involved, although if you're planning on working on more Scala
projects in the future, it's certainly worth it because sbt is much more commonly
used than Mill in the Scala world.

While Mill is more flexible and less complex, it's newer and has a smaller ecosystem
than sbt, so there may come a day when we need a critical feature that sbt has but
Mill doesn't, in which case we'll get rid of the Mill build file and only use sbt
from then on. If Vyxal v3 reaches stability without any such hiccups, then we may
continue supporting both or drop support for sbt in favor of Mill.

If you're a newcomer to Scala and don't plan on working on Scala projects other than
Vyxal, then Mill is probably the best option for you. Do note that you may have to
switch to sbt at some point if there are any problems, but there probably won't be.

## sbt

If you know how to use sbt already, you can skip to the [Projects](#projects-for-sbt) section.

### Installation

Make sure you have Java installed, since sbt needs it.

[This page](https://www.scala-sbt.org/download.html) has various methods for
installing sbt.

- If you're planning on also playing around with the Scala REPL, installing with
  [Coursier](https://get-coursier.io/docs/cli-installation) is the best choice,
  since you can then just run `cs setup` to install sbt, scalac, and other stuff
  (or just `cs install sbt` if you don't want everything).
- If you're just going to work on Vyxal and not do any other Scala stuff,
  then you can instead just install sbt without also installing Scala.

### The sbt client

sbt used to force you to run everything in its [shell](#the-sbt-shell), but now it
has a client so that you can run commands directly from the command line instead
of going into its shell first.

Depending on how you installed sbt, you may have a command called `sbtn` available,
which you can use like `sbtn vyxalJVM/test` (tests the JVM target).

If you don't have a command called `sbtn` available, you can use `sbt` with the `--client`
flag (e.g. `sbt --client vyxalJVM/test`). If you like, you can alias `sbtn` to
`sbt --client` by putting the following in your .bashrc/.zshrc/whatever (won't work on Windows):

```sh
alias sbtn="sbt --client"
```

Be very careful to either use `sbtn <taskname>` or `sbt --client <taskname>`
rather than `sbt <taskname>`. If you use `sbt <taskname>`, it will not use a client
but rather start up a new instance of sbt, run that task, and then exit. This is
fine if you only want to run a single task and then go away, but because
starting up sbt takes a while, this will become annoying when you want to run
multiple tasks (e.g. you're debugging something and need to compile or run tests
every time you make some changes). With `sbtn`/`sbt --client`, it will only start
one instance of sbt the first time and keep it alive for future tasks.

### The sbt shell

`sbt` also has a shell with history and some basic tab completion. If you run
`sbt` (no arguments), you will see a prompt like this:

```bash
$ sbt
...
sbt:root>
```

Then inside the shell, you can execute sbt tasks such as `compile`, `test`, etc.
You can exit the shell with `exit` or Ctrl+C. Just like any other shell, you can
use the up and down arrows to go to previous commands.

### Projects (for sbt)

There are 4 projects defined in sbt:

- `vyxalJVM` - For running Vyxal on the JVM. Includes code in `jvm/` and `shared/`.
- `vyxalJS` - For compiling Vyxal to JS. Includes code in `js/` and `shared/`
- `vyxalNative` - For compiling Vyxal to native executables. Includes code in `native/` and `shared/`
- `root` - Not a real project, just collects the other 3 projects together

You'll notice the prompt says `sbt:root>` when you start up. You can switch over
to the `vyxalJVM` project by running `project vyxalJVM` and then your prompt will show
`sbt:vyxal>`. You can switch back to `root` with `project root`. To check which
project you're currently in, use `project` (no arguments).

You'll probably want to run tasks in the `vyxalJVM` project most of the time

### sbt Settings

Nearly all the configuration for sbt goes in [build.sbt](/build.sbt). build.sbt
files are written in Scala, although like Gradle, sbt uses a DSL.

Depending on which project(s) a setting applies to, you will have to put it in a
different place in `build.sbt`.

- JVM-specific settings go in the `.jvmSettings` call
- JS-specific settings go in the `.jsSettings` call
- Native-specific settings go in the `.nativeSettings` call
- Shared settings go in the `.settings` call (look for a comment saying `// Shared settings`)

There is also some configuration in [projects/plugins.sbt](/project/plugins.sbt).
Since we need to make Vyxal cross-compile to the JVM, JS, and to native executables,
we have to use a lot of plugins, and they go there. They will occasionally have
to be updated, but otherwise, you don't need to do much there.

### Dependencies (sbt)

The `libraryDependencies` setting is used to add libraries. If there's a libary
needed by all 3 platforms, put it in the shared settings. If a library is only
used for a specific platform, put it in the settings for that platform.

For sbt, an individual library dependency is written in one of these ways:

```scala
// What most libraries will need
"groupId" %%% "artifactId" % "version"
// If it's only needed for testing
"groupId" %%% "artifactId" % "version" % Test
// If it's JVM-specific (%% instead of %%%)
"groupId" %% "artifactId" % "version"
// If a Scala library only supports Scala 2, not Scala 3
("groupId" %%% "artifactId" % "version").cross(CrossVersion.for3Use2_13)
```

Here's an example: `"org.scala-js" %%% "scalajs-dom" % "2.2.0"`.

Keep in mind that if you use a library that only supports Scala 2 using the
`.cross(CrossVersion.for3Use2_13)` thing mentioned above, then there may very well
be some incompatibilities that keep you from making full use of that library.
For example, Scala 2 macros don't work in Scala 3, so try to avoid libraries
using them.

---

## Mill

The only thing you need to use Mill is Java. The root folder of this project
includes two scripts, `mill` (for Linux and MacOS) and `mill.bat` (for Windows).
You can use these to run everything. They'll download Mill for you, so you don't
need to go around installing stuff yourself.

### Projects (for Mill)

The Mill build has 3 modules:

- `jvm` - For running Vyxal on the JVM. Includes code in `jvm/src` and `shared/src`.
- `js` - For compiling Vyxal to JS. Includes code in `js/src` and `shared/src`
- `native` - For compiling Vyxal to native executables. Includes code in `native/src` and `shared/src`

Each of these modules has a `test` module inside it (e.g. `jvm.test` includes
code in `jvm/src/test/scala` and `shared/src/test/scala`).

### Mill Settings

All Mill settings go in [`build.sc`](/build.sc). Settings shared by the JVM, JS,
and Native modules go in the `VyxalModule` trait, and settings shared by their
test modules go in the `VyxalTestModule` trait. JVM-specific settings go in the
`jvm` object, JS-specific settings go in the `js` object, and native-specific
settings go in the `native` object. Each of those singleton objects have a `test`
object inside them that holds test settings for that platform.

### Dependencies (Mill)

In Mill, dependencies are declared in the `ivyDeps` task. Here's an example:

```scala
def ivyDeps = Agg(
  ivy"org.typelevel::spire::0.18.0",
  ivy"org.scala-lang.modules::scala-parser-combinators::2.2.0",
)
```

They are declared in a fashion very similar to sbt: `ivy"groupId::artifactId::version"`.

**Note**: Java dependencies use a single colon between the group and artifact (`ivy"groupId:artifactId::version"`).

---

## Tasks

There are several tasks you can run (you can [skip](#list-of-tasks) to the list
of tasks we use).

### Running tasks with sbt

You can see most available tasks with the `tasks` sbt command. Use `inspect <task>`
to get some more low-level info about a task (its dependencies, where it's defined, related tasks, etc.).

**Important**: Make sure that you do not run these tasks in the `root` project. If, for example,
you run `test` in the root project, it will test *all* the projects: `vyxalJVM`,
`vyxalJS`, and `vyxalNative`. You can either:

- Switch to the `vyxalJVM`, `vyxalJS`, or `vyxalNative` projects (use `project vyxalJVM`).
- Prefix the name of the task with `vyxalJVM/`, `vyxalJS/`, or `vyxalNative/`
  - For example, if you're in root, `vyxalJVM/test` will run the `test` task inside
    `vyxalJVM`.

In most cases, just use `vyxalJVM`. `vyxalNative` is extremely slow to compile.
`vyxalJS` is slow to compile and `vyxalJS/run` requires Node.js.

### Running tasks with Mill

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

### List of tasks

- `compile`: Compile your code to see if there's any errors or warnings
- `run`: Run the Vyxal CLI/REPL
  - Note that if you want to run `vyxalJS` directly, you need Node.js on your computer
- Testing (see [Tests.md](Tests.md) for more info):
  - `test`: Run all tests.
  - `testOnly`: Run specific tests.
  - `testQuiet`: Only defined for Mill (currently). Runs tests and suppresses output from passed/ignored tests.
- Run `scalafix` to lint/refactor your code (only with sbt)
  - If you don't want Scalafix modifying your code, run `scalafix --check` so it'll
    just give you errors and you can fix them manually
  - You can use specific rule names, e.g. `scalafix RemoveUnused` to only run the
    `RemoveUnused` rule
- Automatically formatting files with Scalafmt:
  - `scalafmt` if you're using sbt
    - Use `scalafmtOnly <filepath>` to only format a particular file. The file path
      is relative to your current project's root, so if you're in the `vyxalJVM`
      project, it'll be relative to the `jvm/` folder, and if you're in `vyxalJS`,
      it'll be relative to `js/`.
      - e.g. `scalafmtOnly ../shared/src/Interpreter.scala`
    - `reformat` if you're using Mill
      - `checkFormat` to only check if everything is formatted.
- `fastOptJS` - Quickly build and link the JS code, not too many optimizations.
  Use this one for development.
  - Use `fullOptJS` to build and link the JS code with optimizations. This will
  only be needed when releasing, not when developing.
  - Both tasks output `lib/scalajs-<version>.js`.
  - Both tasks only work for the JS target.
- `assembly` - Package everything up into an uber jar, outputs
  `jvm/target/scala-<scalaVersion>/vyxal-<vyxalVersoin>.jar`. Only works inside
  the JVM target, i.e., you need to use `vyxalJVM/assembly` if using sbt and
  `jvm.assembly` if using Mill.
- `nativeLink` - Generate an executable. Only works inside the Scala Native target.
  Beware, this is quite slow! You probably want to let the GitHub workflow do this.

## Scalafmt

Scalafmt is a formatter for Scala. The configuration for it is in
[.scalafmt.conf](/.scalafmt.conf). Docs on configuration are
[here](https://scalameta.org/scalafmt/docs/configuration.html). `.scalafmt.conf`
won't have to be touched much.

TODO write more on this later

## Scalafix

Scalafix is a linting and refactoring tool. The configuration for it is in
[.scalafix.conf](/.scalafix.conf). Docs on configuration are
[here](https://scalacenter.github.io/scalafix/docs/users/configuration.html).
`.scalafix.conf` will probably be modified even less often than `.scalafmt.conf`.

TODO write more on this later
