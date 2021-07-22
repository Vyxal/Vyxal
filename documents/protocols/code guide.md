# The Definitive Vyxal Code Style Guide
_First revision_

When contributing to the Vyxal repository, make sure you follow the conventions in this document like an epic gamer. Doing so will make everyone's lives hunky-dory, and you'll be an absolute pogchamp. Who doesn't want to be an absolute pogchamp?

This document is different to `contributing.md` because it specifically defines things such as semantics of functions and variables within the actual code.

## The Obvious/Easy Stuff

First of all, because this is a python project, we comply to [PEP8](https://www.python.org/dev/peps/pep-0008/)'s code formatting standards around here. That means that you need to follow all the juicy guidelines that make python code very readable and good. "But sticking to that is hard and I can't remember all the rules" I hear you say. Well, don't worry...you can use [black](https://pypi.org/project/black/) to automatically lint your code to comply with PEP8 (protip: if you're a true gamer and you use an editor like VS Code, you can set it to automatically format with black everytime you save a file). Our testing workflow uses flake8 for code compliance.

Also, we use [isort](https://pypi.org/project/isort/) to make sure that all imports are in an epic order. 

Alright. Now to the specific stuff.

## File Structure

Here's a list of all the main python files in the repository:

- `main.py` - this is the file that actually runs Vyxal - it contains the compiler and the offline command line runner. The only things that should be in this file are helper functions for the transpiler, and anything needed to execute programs.

- `parse.py` - this is the parser that turns Vyxal programs into tokens. It contains the parser and a whole series of token related constants.

- `commands.py` - this is where the python equivalent of each element is stored. Each element has a string representing what it is compiled as, and a command arity. Also in this file is the python equivalent of each modifier

- `functions.py` - this file contains functions directly connected to elements/modifiers - in other words, this is where the element overloading happens.

- `utilities.py` - this file contains helper functions that are not directly connected to elements/modifers: e.g. string decompression, the LazyList class and useful generators.

- `encoding.py` - this file is used for enforcing the SBCS used by Vyxal. Consequently, there isn't much reason to touch this file.

- `dictionary.py` - this file has the words used for dictionary compression. Consequently, there isn't much reason to touch this file.


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

Very important: Only the type dictionary should be inside the function definition. Also, if there happens to be a `Function` overload, use `types.FunctionType`.

Do NOT manually vectorise EVER.

If you're implementing an element which has a non-vectorising overload, set the `simple` parameter of `vy_type` to `True`. This will treat LazyLists as Lists.

## Identifier Semantics

Around here, we use `snake_case`. Class names start with a capital letter. Constants are all caps.

_Note that this revision is far from complete._
