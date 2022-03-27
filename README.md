# Vyxal

![Vyxal Logo](./documents/logo/vylogo.png)

[![Language grade: Python](https://img.shields.io/lgtm/grade/python/g/Vyxal/Vyxal.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Vyxal/Vyxal/context:python) ![Test status](https://github.com/Vyxal/Vyxal/actions/workflows/run-tests.yaml/badge.svg)

**Vyxal** is a golfing language that takes the idea that conciseness comes at the cost of practicality and throws it out the window. That's right - where other golflangs throw you into the deep-end of keyboard mashing, Vyxal eases you into the concept of elegantly crafting built-ins into a functioning program.

And yes, this design goal really _does_ warrant adding another golfing language into the already densely populated mix of golflangs. If you go and take a look at the current state of the art of golfing languages, you'll find that 99% of languages are either a) powerful and concise, but not easy to pick up or b) easy to learn, but not that useful for anything non-trivial (I say this as someone who's made and contributed to both kinds of languages). Vyxal aims to bridge the gap between simplicity and "golfability".

## Fun Vyxal Features

- Comments!

```
1 1+ # This is a comment
     # This is a comment too, but it's longer
     # Btw the expression evaluates to 2
```

[Try it Online!](https://vyxal.pythonanywhere.com/#WyIiLCIiLCIxIDErICMgVGhpcyBpcyBhIGNvbW1lbnRcbiAgICAgIyBUaGlzIGlzIGEgY29tbWVudCB0b28sIGJ1dCBpdCdzIGxvbmdlclxuICAgICAjIEJ0dyB0aGUgZXhwcmVzc2lvbiBldmFsdWF0ZXMgdG8gMiIsIiIsIiJd)

- Variables!

```
`Joe` →first_name # The variable "first_name" now has the value "Joe"
69 →age # The variable "age" now has the value 69 (nice)
←first_name ` is ` ←age ` years old` +++ # "Joe is 69 years old"
```

[Try it Online!](https://vyxal.pythonanywhere.com/#WyIiLCIiLCJgSm9lYCDihpJmaXJzdF9uYW1lICMgVGhlIHZhcmlhYmxlIFwiZmlyc3RfbmFtZVwiIG5vdyBoYXMgdGhlIHZhbHVlIFwiSm9lXCJcbjY5IOKGkmFnZSAjIFRoZSB2YXJpYWJsZSBcImFnZVwiIG5vdyBoYXMgdGhlIHZhbHVlIDY5IChuaWNlKVxu4oaQZmlyc3RfbmFtZSBgIGlzIGAg4oaQYWdlIGAgeWVhcnMgb2xkYCArKysgIyBcIkpvZSBpcyA2OSB5ZWFycyBvbGRcIiIsIiIsIiJd)

- Named Functions!

```
@fibonacii:N|                # def fibonacii(N):
  ←N 0 = [ 0 |               #   if N == 0: return 0
    ←N 1 = [ 1 |             #   elif N == 1: return 1
      ←N 2 - @fibonacii;     #   else: return fibonacii(N - 2) + fibonacii(N - 1)
      ←N 1 - @fibonacii; +
    ]
  ]
;

6 @fibonacii;
```

[Try it Online!](https://vyxal.pythonanywhere.com/#WyIiLCIiLCJAZmlib25hY2lpOk58ICAgICAgICAgICAgICAgICMgZGVmIGZpYm9uYWNpaShOKTpcbiAg4oaQTiAwID0gWyAwIHwgICAgICAgICAgICAgICAjICAgaWYgTiA9PSAwOiByZXR1cm4gMFxuICAgIOKGkE4gMSA9IFsgMSB8ICAgICAgICAgICAgICMgICBlbGlmIE4gPT0gMTogcmV0dXJuIDFcbiAgICAgIOKGkE4gMiAtIEBmaWJvbmFjaWk7ICAgICAjICAgZWxzZTogcmV0dXJuIGZpYm9uYWNpaShOIC0gMikgKyBmaWJvbmFjaWkoTiAtIDEpXG4gICAgICDihpBOIDEgLSBAZmlib25hY2lpOyArXG4gICAgXVxuICBdXG47XG5cbjYgQGZpYm9uYWNpaTsiLCIiLCIiXQ==)

- And Nice Syntax Choices!

In conclusion, if you're coming from a traditional programming language, Vyxal is the right golfing language for you - you'll appreciate the familiar control structures! If you're coming from another golfing language, Vyxal is also the right golfing language for you - you'll be able to jump right into complex problem solving!

_(Btw we also have cookies - the tasty kind, not the track your info kind)_

## Installation

You can also use the [online interpreter](https://vyxal.pythonanywhere.com) with no need to install!

If you only want to run Vyxal, all you need to run is this:

```
pip install vyxal
```

If you are working on Vyxal, install [Poetry](https://python-poetry.org), and then you can clone this repo and run:

```
poetry install
```

## Usage

To run using the script:

```
vyxal <file> <flags (single string of flags)> <input(s)>
```

If you're using Poetry:

```
poetry run vyxal <file> <flags (single string of flags)> <input(s)>
```

## Links

- [Repository](https://github.com/Vyxal/Vyxal)
- [Online Interpreter](http://vyxal.pythonanywhere.com)
<!-- TODO: fix broken links
- [Tutorial](https://github.com/Vyxal/Vyxal/blob/master/docs/Tutorial.md)
- [Codepage](https://github.com/Vyxal/Vyxal/blob/master/docs/codepage.txt)
  -->
- [Main Chat Room (SE Chat)](https://chat.stackexchange.com/rooms/106764/vyxal)
- [Vycord (Discord)](https://discord.gg/hER4Avd6fz)
- [Elements](https://github.com/Vyxal/Vyxal/blob/v2.6.0/documents/knowledge/elements.md)
- [Vyxapedia](https://vyxapedia.hyper-neutrino.xyz/)
