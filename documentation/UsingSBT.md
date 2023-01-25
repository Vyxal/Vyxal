# Using sbt

This file is for documenting how to use the build tool [sbt](https://www.scala-sbt.org/)
in this project and the different sbt tasks we've defined. This guide will
likely be inconsistent in its capitalization of sbt/SBT. Sorry about that in advance.

If you know how to use SBT and everything already, you can skip to the [Vyxal-specific stuff](#vyxal-specific-stuff).

## Installation

Make sure you have Java installed, since sbt needs it.

[This page](https://www.scala-sbt.org/download.html) has various methods for
installing sbt.

- If you're planning on also playing around with the Scala REPL, installing with
  [Coursier](https://get-coursier.io/docs/cli-installation) is the best choice,
  since you can then just run `cs setup` to install sbt, scalac, and other stuff
  (or just `cs install sbt` if you don't want everything).
- If you're just going to work on Vyxal and not do any other Scala stuff,
  then you can instead just install sbt without also installing Scala.

## The sbt shell

If you've never used SBT, you should know that it has a shell.
If you run `sbt` (no arguments), you should see a prompt like this:

```bash
$ sbt
...
sbt:root>
```

Then inside the shell, you can execute sbt tasks such as `compile`, `test`, etc.
You can exit the shell with `exit` or Ctrl+C. Just like any other shell, you can
use the up and down arrows to go to previous commands.

Sometimes you just want to run one task without dropping into the shell. In that
case, you can run `sbt <taskname>`. You might be wondering why we need to bother
with a shell at all in that case. Gradle and other popular build systems don't
do that. Unfortunately, sbt isn't really designed to be run that way.

One minor result of this is that if you try to run a task with arguments like
`sbt testOnly *ParserTests`, then sbt will think you're running two tasks, one
named `testOnly`, and another named `*ParserTests` (called batch mode). The
solution to this is using quotes: `sbt "testOnly *ParserTests"`.

However, there's a bigger problem: unlike Gradle, sbt doesn't seem to use a daemon.
That means a new instance starts up every time you run it, which will add a few
seconds to every task if you run every task directly with `sbt <taskname>`.

That's why it's preferred to be inside the sbt shell when executing sbt tasks.
Only run `sbt <task>` directly if you know you're only going to be running one
task or if you need to run multiple tasks non-interactively (like in a GitHub Actions workflow).

99% of the time, it's not all that inconvenient and you don't really need an sbt
task to communicate with the outside world, but if you do need it, you can always
do `sbt <sometask> | outputfile`. It does take a few seconds for sbt to start up
each time, but it's manageable.

We're using SBT since it's the standard in the
Scala world, but we might consider [Mill](https://com-lihaoyi.github.io/mill/)
in the future, since it's (supposedly) simpler, it uses a daemon so you don't
need to enter a shell, and it's made by Li Haoyi, which doesn't really count as
an advantage but he's a huge contributor to the Scala ecosystem. Unfortunately,
right now, Mill simply doesn't have as many contributors, as large as an
ecosystem, or as good IDE support as SBT does.

## `build.sbt`

Nearly all the configuration for sbt goes in [build.sbt](/build.sbt). build.sbt
files are written in Scala, although like Gradle, sbt uses a DSL.

Since we need to make Vyxal cross-compile to the JVM and JS, we're using a lot of
plugins, declared in [projects/plugins.sbt](/project/plugins.sbt).

## Vyxal-specific stuff

Okay, enough about SBT, on to what you came here for!

### Projects

There are 3 projects: `root`, `vyxalJVM`, and `vyxalJS`. `root` isn't really a project by itself, it
just collects the other two together. `vyxalJVM` is the project you want if you're
running Vyxal on the JVM, and `vyxalJS` is the one you want if you're using JS.
You'll notice the prompt says `sbt:root>` when you start up. You can switch over
to the `vyxalJVM` project by running `project vyxalJVM` and then your prompt will show
`sbt:vyxal>`. You can switch back to `root` with `project root`. To check which
project you're currently in, use `project` (no arguments).

### Tasks

There are several tasks you can run. You can see most of them by running `tasks`.
We only need to use a handful of them, though:

- `compile`: Compile your code to see if there's any errors or warnings
- `run`: Run the Vyxal CLI/REPL
  - Note that if you want to run `vyxalJS` directly, you need Node.js on your computer
- `test`: Run all tests in the project
  - `testOnly *ClassName`: Run only the tests in a specific class, e.g.
    `testOnly *ParserTests` to only run the tests in `ParserTests`
  - `testOnly *ClassName -- -t "text"`: Run only tests inside a specific class
    whose names contain some specific test, e.g. `testOnly *ParserTests -- -z "for loops"`
  - `testOnly *ClassName -- -z "text"`: Same as before, but look for an exact match
    instead of just tests containing the text
- `scalafix` - Run Scalafix to lint/refactor your code
  - If you don't want Scalafix modifying your code, run `scalafix --check` so it'll
    just give you errors and you can fix them manually
  - You can use specific rule names, e.g. `scalafix RemoveUnused` to only run the
    `RemoveUnused` rule
- `scalafmt` - Run Scalafmt to format all your code
  - Use `scalafmtOnly <filepath>` to only format a particular file. The file path
    is relative to your current project's root, so if you're in the `vyxalJVM`
    project, it'll be relative to the `jvm/` folder, and if you're in `vyxalJS`,
    it'll be relative to `js/`.
    - e.g. `scalafmtOnly ../shared/src/main/scala/Interpreter.scala`
- `fastOptJS` - Quickly build and link the JS code, not too many optimizations.
  Use this one for development.
  - Use `fullOptJS` to build and link the JS code with optimizations. This will
  only be needed when releasing, not when developing.
  - Both tasks output `lib/scalajs-<version>.js`.
  - Both tasks only work inside the `vyxalJS` project.
- `assembly` - Package everything up into an uber jar, outputs
  `jvm/target/scala-<scalaVersion>/vyxal-<vyxalVersoin>.jar`. Only works inside
  the `vyxalJVM` project.

Make sure that when you run these tasks, you either:

- Switch to the `vyxalJVM` or `vyxalJS` projects (use `project vyxalJVM`)
- Prefix the name of the task with `vyxalJVM/` (or `vyxalJS/` if you want to build/test/run the JS project)
  - For example, if you're in root, `vyxalJVM/test` will run the `test` task inside
    `vyxalJVM`, while just `test` will run both `vyxalJVM/test` and `vyxalJS/test`

Use `inspect <task>` to get some more low-level info about a task (its
dependencies, where it's defined, related tasks, etc.).

### Dependencies

The `libraryDependencies` setting is used to add libraries. If there's a libary
needed by both the JVM and JS, put it in the shared settings (look for a comment
saying `// Shared settings` in build.sbt). If there's a library only for JS, put it
inside the `.jsSettings()` call. If there's a library only for the JVM,
put it inside the `.jvmSettings()` call. You can use any Java library, not just
Scala libraries, but those Java libraries can only be used in the JVM-specific
code.

An individual library dependency is written in one of these ways:

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

## Scalafix

Scalafix is a linting and refactoring tool. The configuration for it is in
[.scalafix.conf](/.scalafix.conf). TODO write more on this later

## Scalafmt

Scalafmt is a formatter for Scala. The configuration for it is in
[.scalafmt.conf](./.scalafmt.conf). TODO write more on this later
