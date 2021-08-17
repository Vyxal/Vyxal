# The Definitive Vyxal Code Style Guide
_Seventeenth revision_

When contributing to the Vyxal repository, make sure you follow the conventions in this document like an epic gamer. Doing so will make everyone's lives hunky-dory, and you'll be an absolute pogchamp. Who doesn't want to be an absolute pogchamp?

This document is different to [`contributing.md`](/documents/protocols/contributing.md) because it specifically defines things such as semantics of functions and variables within the actual code.

## The Obvious/Easy Stuff

First of all, because this is a python project, we comply to [PEP8](https://www.python.org/dev/peps/pep-0008/)'s code formatting standards around here. That means that you need to follow all the juicy guidelines that make python code very readable and good. "But sticking to that is hard and I can't remember all the rules" I hear you say. Well, that's why we use [black](https://pypi.org/project/black/) (with the `--line-length=80` cli flag) to automatically lint our code to comply with PEP8 (protip: if you're a true gamer and you use an editor like VS Code, you can set it to automatically format with black everytime you save a file). Our testing workflow uses flake8 for code compliance.

Also, we use [isort](https://pypi.org/project/isort/) to make sure that all imports are in an epic order. 

Finally, we're using an 80 character limit per line, with comments limited to 72 characters per line. If you're a VS Code user, you can paste `"editor.rulers": [72,80]` into your `settings.json` to have a visual indicator of the line limits. Also, you'll have to configure black to wrap on 80 characters: change `python.formatting.blackArgs` to 

```json
"python.formatting.blackArgs": [
  "--line-length=80"
]
```

Alright. Now to the specific stuff.

## File Structure

Here's a list of all the main python files in the repository:

- [`vyxal.py`](../../vyxal/vyxal.py) - this is the file that actually runs Vyxal - it contains the compiler and the offline command line runner. The only things that should be in this file is anything needed to execute programs.

- [`parse.py`](../../vyxal/parse.py) - this is the parser that turns tokens into Structures.

- [`lexer.py`](../../vyxal/lexer.py) - this is the lexer that turns Vyxal programs into tokens.

- [`structure.py`](../../vyxal/structure.py) - this contains structure classes that are used by the parser.

- [`encoding.py`](../../vyxal/encoding.py) - this file is used for enforcing the SBCS used by Vyxal. Consequently, there isn't much reason to touch this file.

- [`dictionary.py`](../../vyxal/dictionary.py) - this file has the words used for dictionary compression. Consequently, there isn't much reason to touch this file.

- [`elements.py`](../../vyxal/elements.py) - this file has the element functions and element/modifier dictionaries.

- [`helpers.py`](../../vyxal/helpers.py) - this file has helper functions for elements and other files.

- [`transpile.py`](../../vyxal/transpile.py) - this file has the transpiling functions.

- [`LazyList.py`](../../vyxal/LazyList.py) - our generator wrapper.

## Function Overloads
### Monads

```python
def NAME(lhs):
    return {
        num: lambda: NUMBER_OVERLOAD,
        str: lambda: STRING_OVERLOAD
    }.get(vy_type(lhs), lambda: vectorise(NAME, lhs))()
```

### Dyads

```python
def NAME(lhs, rhs):
    ts = vy_type(lhs, rhs)
    return {
        (num, num): lambda: NUMBER_NUMBER_OVERLOAD,
        (num, str): lambda: NUMBER_STRING_OVERLOAD,
        (str, num): lambda: STRING_NUMBER_OVERLOAD,
        (str, str): lambda: STRING_STRING_OVERLOAD
    }.get(ts, lambda: vectorise(NAME, lhs, rhs))()
```

### Triads

```python
def NAME(lhs, rhs, other):
    ts = vy_type(lhs, rhs, other)
    return {
        (num, num, num): lambda: ...,
        (num, num, str): lambda: ...,
        (num, str, num): lambda: ...,
        # and so forth
    }.get(ts, lambda: vectorise(NAME, lhs, rhs, other))()
```

Very important: Only the type dictionary should be inside the function definition. Also, if there happens to be a `Function` overload, use `types.FunctionType`.

Do NOT manually vectorise EVER.

If you're implementing an element which has a non-vectorising overload, set the `simple` parameter of `vy_type` to `True`. This will treat LazyLists as Lists.

## Identifier Semantics

Around here, we use `snake_case`. Class names start with a capital letter. Constants are all caps. Be descriptive - Vyxal is a golfing language, but that doesn't mean you need to golf the interpreter. Generally, be sensible and use common sense - don't name your variables `bob`, `joeBiden69quickscope_gamer` or `E`.

To summarise: don't be a sussy baka with your variable names.

## Comments and Documentation

In general, [Google's style guide](https://github.com/google/styleguide/blob/gh-pages/pyguide.md#38-comments-and-docstrings)
is a good set of rules to follow here, but these are some guidelines specific to the Vyxal project:

### Line Comments

Line comments are started with `#` and terminated by a newline (we use `\n` for newlines - don't go using `\r\n` or anything stupid like that). These should be used when one of the following is true:

- The functionality of a line of code isn't obvious
- Something needs to be stated as a warning/important note

When writing a line comment:

- Don't just repeat what the line says. Keep your comments DRY (don't repeat yourself).
- Try and keep it concise enough to fully convey what you want to say without losing too much detail.
- Explain why you are doing something.
- Remember to keep it 72 characters or less - split over two comments if need be.

### Multiline Comments / Docstrings

These comments are started and terminated with either `"""` (frick `'''`). This should be used:

- At the start of an element function to list the overloads:

```python
def add(lhs, rhs):
    """Element: +

    (num, num) -> a + b
    (num, str) -> concatenate a and b, converting a to string first
    (str, num) -> concatenate a and b, converting b to string first
    (str, str) -> concatenate a and b
    """
    
    ...
```

- At the start of a helper function to describe what the function is doing (unless the function is all of {not public
  API, short, and obvious})

```python
def function(arg1: type, arg2: type) -> type:
    """calculate the frobbernickel of the two input shnops"""  
```

If the function is complicated, it may be worth adding additional details a bit like the [numpy/scipy](https://numpydoc.readthedocs.io/en/latest/format.html) docstring format:
```python
def function(arg1: type, arg2: type) -> type:
    """Function description

    Parameters
    arg1: type
        description of arg1
    arg2: type
        description of arg2
    
    Returns a `type`
        description of the returned value
    """
```

- At the start of a file to describe the function of the file

```python
"""<summarise the file's purpose>

<describe in more detail if necessary>
<mutliple lines are okay>
"""
```

When writing a multiline comment, make sure to follow [PEP257](https://www.python.org/dev/peps/pep-0257/). Don't be afraid to use plain language in multiline comments.

## Test Cases

This is a very simple section: we here at Vyxal do _not_ use:

```python
if __name__ == "__main__":
    # tests
```

Instead, we create test files for each file so that they can be all run together in the testing workflow.

## Other Things

- No directly popping from the stack within element/helper functions. Only pop from the stack in the transpiled versions of each element.
- Helper functions are to be stand alone functions. That is, they could be used outside of the context of element functions.
- Whenever you write an element function, make sure you include a `ctx` parameter as the last parameter. This allows element functions to access values that would otherwise be global variables. That is, `def function(<whatever args the function takes>, ctx)`. (For more info, go to [context.md](/documents/specs/Context.md))
