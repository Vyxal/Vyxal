This is the official version 3 branch.

The readme will be updated when it's time to write a proper readme.
See the [documentation](./documentation/README.md) folder for info on how the
interpreter works, Vyxal elements, and other stuff.

Directory Guide (for people who aren't the best at mentally parsing nested folders):

- `shared/src` is where the Scala source code shared across the JVM and JS is.
  - `shared/src/main/scala` is where the interpreter files are.
    - `Elements` contains the definitions of the elements
    - `VAny` contains the definitions of the types of Vyxal's runtime values (numbers, strings, functions, lists)
  - `shared/src/test/scala` is where the test files are
- `jvm/src/main/scala` is where the entry file for the command line and all the
   JVM-specific stuff is. It's the `__main__.py` of the project in a sort of a way.
- `js` is where all the JS-specific stuff is.
