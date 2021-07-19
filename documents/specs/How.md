# How Does the Vyxal get turned into the Python and then turned into the Output?


# How Does the Vyxal get turned into the Python and then turned into the Output?

That's a great question, albeit somewhat poorly worded. Nonetheless, I shall explain the process of turning raw Vyxal source code into executable Python.

The first step is to read the Vyxal source file; offline, we use the file name given on the command line. Online, we use the contents of the header, code and footer box, joined on newlines.

Next, the program is passed to the compilation function. At this stage, the program is still a string, so it gets tokenised. The tokenisation process is simple: group strings, pair digraphs and determine structures. The resulting tokens each have a token name and structure data. 

For example:

```
[1|`abc`] -> (IF, {Truthy: [(NUMBER, 1)], Falsey: [(STRING, "abc")]})
```

Next, the resulting tokens get turned into their Python equivalent. Usually, this is a recursive process, as tokens sometimes contain code that needs further tokenising and compiling. This then gets executed.

I've probably missed a whole lot in my attempts to obtain a 69 readability score within Grammarly. I failed btw.
