# Tests

We use [ScalaTest](https://www.scalatest.org/) for testing.
For info on writing tests, skip [ahead](#writing-tests).

## Running Tests

There are two commands for running tests, `test` and `testOnly`. `test` runs all
tests in the project, while `testOnly` can be used to only run tests in a specific class.
For example, `testOnly *ParserTests` would only run the tests in `ParserTests`.

Here are some options you can pass to `testOnly`
(note that `testOnly` requires a `--` before the flags):

- `-z "text"`: Run only tests whose names contain some specific test,
  e.g. `testOnly *ParserTests -- -z "for loops"`
- `-t "text"`: Same as before, but look for an exact match instead of
  just tests containing the text
- `-n "<tag>"`: Only run tests with a specific tag, e.g. `-n org.scalatest.tagobjects.Slow`
  to only run tests tagged `Slow`
- `-l "<tag>"`: Only run tests *without* a specific tag, e.g. `-l org.scalatest.tagobjects.Slow`
  to run all but tests tagged `Slow`
- `-o`: Suppress things like passed tests, ignored tests, etc.
  More [here](https://www.scalatest.org/user_guide/using_the_runner#configuringReporters)

A comprehensive list of all flags is in the ScalaTest
[user guide](https://www.scalatest.org/user_guide/using_the_runner).

### Mill

**Important**: Mill treats the tests as being in their own module (`jvm.test`, `js.test`, `native.test`),
so when running `testOnly`, you'll have to run `jvm.test.testOnly`, not `jvm.testOnly`.

We have a `testQuick` task defined only for Mill that's equivalent to `testOnly` but with
`-oNCXELOPQRM` so that all passed and skipped tests are ignored.

## Writing Tests

Writing tests is very important.
