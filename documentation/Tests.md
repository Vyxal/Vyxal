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

Simple tests go in [tests.yaml](/shared/src/test/resources/tests.yaml). Below is
a description of how they look.

```yaml
# The element itself
"ඞ":
  # For relatively small cases
  - { in: [1, 2], out: 3 }
  # In case you have lots of inputs or you just want to be explicit
  - in:
    - "foo"
    - 2
    - ["nested"]
  - out: "asdf"
  # If you want to specify some conditions about the output
  - in: ["gimme all the primes"]
    # Ensure the output starts with a certain prefix
    starts-with: [2, 3, 5]
    # Make sure it contains 0 and 1
    contains: [0, 1]
    # Same as contains, but assume the list is monotonic (useful for infinite lists)
    contains-monotonic: [2]
    # To make sure the output *doesn't* meet some conditions
    not:
      contains: [3]
      # Make sure the output *doesn't* end with -9, -10
      ends-with: [-9, -10]
"+":
  # You can also make groups of tests
  should work on numbers:
    nested:
      - { in: [0.1, 4.5], out: 42 }
      - { in: [1, 2], out: 5 }
  other group of tests:
    - { in: [1], out: 5 }
  # But you can't mix test groups and test lists
  - { in: [], out: [] } # This line isn't allowed here
```

More complex tests go in [ElementTests.scala](/shared/src/test/scala/ElementTests.scala).
TODO describe how they work.
