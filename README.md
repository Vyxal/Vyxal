This is the official version 3 branch.

The readme will be updated when it's time to write a proper readme.

Directory Guide (for people who aren't the best at mentally parsing nested folders):

- `shared/src` is where the Scala source code shared across the JVM and JS is.
  - `shared/src/main/scala` is where the interpreter files are.
    - `Elements` contains the definitions of the elements
    - `VAny` contains the definitions of the types of Vyxal's runtime values (numbers, strings, functions, lists)
  - `shared/src/test/scala` is where the test files are
- `jvm/src/main/scala` is where the entry file for the command line and all the
   JVM-specific stuff is. It's the `__main__.py` of the project in a sort of a way.
- `js` is where all the JS-specific stuff is.

It's preferred to be inside the sbt shell when executing sbt tasks. The reason for this is that
sbt has to start up again every time you run it, so if you're inside the shell, sbt only has to
start up once. If you know you only need to run one command, though, you can do `sbt somecommand`
directly instead of going into the shell and then running just `somecommand`.

Run `sbt` without any arguments to drop into the sbt shell.

To run the CLI inside the shell, use `vyxalJVM/run`.

To run only the tests in a specific class, say `ParserTests`, run `vyxalJVM/testOnly *ParserTests`

To run only the tests in a specific class whose names contain some specific text, say "for loops", run `vyxalJVM/testOnly *ParserTests -- -t "for loops"`
