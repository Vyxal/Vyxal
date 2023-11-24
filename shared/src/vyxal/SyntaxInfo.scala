package vyxal

case class Syntax(name: String, description: String, usage: String)

object SyntaxInfo:
  val info: Map[String, Syntax] = Map(
    "[" ->
      Syntax(
        "Ternary Statement",
        "Open a ternary statement. Pop condition, if truthy, run <ontrue>, else run <onfalse>",
        "<condition> [<ontrue>|<onfalse>}",
      ),
    "]" ->
      Syntax(
        "Close All Structures",
        "Match and close all open structures.",
        "<structure openers>] <code not in structure>",
      ),
    "(" ->
      Syntax(
        "For Loop",
        "Open a for loop. For each item in the top of the stack, execute code, storing loop variable.",
        "<iterable> (<variable>|<code>}",
      ),
    ")" ->
      Syntax(
        "Close Two Structures",
        "Match and close two open structures.",
        "<structure open><structure open> <code> ) <code not in structure>",
      ),
    "{" ->
      Syntax(
        "While Loop",
        "Open a while loop. While the top of the stack is truthy, execute code.",
        "{<condition>|<code>}",
      ),
    "}" ->
      Syntax(
        "Close A Structure",
        "Match and close the nearest open structure.",
        "<structure open> <code> } <code not in structure>",
      ),
    "|" ->
      Syntax(
        "Structure Branch",
        "Delimit the next section in a structure.",
        "<structure open> <code> | <code> ...",
      ),
    "\"" ->
      Syntax(
        "Open/Close String",
        "Open/close a string. If the string is closed, push it to the stack. Closes all string types",
        "\"string contents\"",
      ),
    "'" ->
      Syntax(
        "One Character String",
        "Push the next character as a string",
        "'<character>",
      ),
    "ᶴ" ->
      Syntax(
        "Two Character String",
        "Push the next two characters as a string",
        "ᶴ<character><character>",
      ),
    "~" ->
      Syntax(
        "Two Byte Number",
        "Push the next two bytes as a number, converted from bijective base 255 using the codepage",
        "~<character><character>",
      ),
    "#[" ->
      Syntax(
        "Open List",
        "Open a list. Pushes the list to the stack when closed.",
        "#[item|item|item#]",
      ),
    "#]" ->
      Syntax(
        "Close List",
        "Close a list. Pushes the list to the stack when closed.",
        "#[item|item|item#]",
      ),
    "λ" ->
      Syntax(
        "Open Lambda",
        "Open a lambda.",
        "λ<parameters>|<code>}",
      ),
    "ƛ" ->
      Syntax(
        "Open Map Lambda",
        "Open a lambda that automatically maps its function to the top of the stack",
        "ƛ<code>}",
      ),
    "Ω" ->
      Syntax(
        "Open Filter Lambda",
        "Open a lambda that automatically filters the top of the stack by its function",
        "Ω<code>}",
      ),
    "₳" ->
      Syntax(
        "Open Reduce/Accumulate Lambda",
        "Open a lambda that automatically reduces/accumulates the top of the stack by its function",
        "₳<code>}",
      ),
    "µ" ->
      Syntax(
        "Open Sort Lambda",
        "Open a lambda that automatically sorts the top of the stack by its function",
        "µ<code>}",
      ),
    "#{" ->
      Syntax(
        "If/Elif/Else Statement",
        "Open an if statement. Allows for if/elif/else statements",
        "#{<if condition>|<code>|<else if condition>|<code>|<else code>}",
      ),
    "Ṇ" ->
      Syntax(
        "Generator Structure",
        "Open a generator structure. Allows for generator expressions",
        "Ṇ<code>|<initial vector>}",
      ),
    "Ḍ" ->
      Syntax(
        "Open Decision Problem Structure",
        "Open a decision problem structure. Returns whether an iterable has any items that match a predicate",
        "Ḍ<predicate>|<container> }",
      ),
    "∆" ->
      Syntax(
        "Mathematical Digraphs",
        "Used for math-related digraphs",
        "∆<character>",
      ),
    "ø" ->
      Syntax(
        "String Digraphs",
        "Used for string-related digraphs",
        "ø<character>",
      ),
    "Þ" ->
      Syntax(
        "List Digraphs",
        "Used for list-related digraphs",
        "Þ<character>",
      ),
    "k" ->
      Syntax(
        "Constant Digraphs",
        "Used for constant-related digraphs",
        "k<character>",
      ),
    "#" ->
      Syntax(
        "Miscellaneous Digraphs",
        "Used for miscellaneous digraphs",
        "#<character>",
      ),
    "##" ->
      Syntax(
        "Comment",
        "Comment out the rest of the line",
        "##<comment>",
      ),
    "." ->
      Syntax(
        "Decimal Separator",
        "Used to separate the integer and fractional parts of a number",
        "<integer>.<fractional>",
      ),
    "ı" ->
      Syntax(
        "Imaginary Number",
        "Used to represent the imaginary unit",
        "<real>ı<imaginary>",
      ),
    "0" -> Syntax("Numeric Literal", "The number 0", "0"),
    "1" -> Syntax("Numeric Literal", "The number 1", "1"),
    "2" -> Syntax("Numeric Literal", "The number 2", "2"),
    "3" -> Syntax("Numeric Literal", "The number 3", "3"),
    "4" -> Syntax("Numeric Literal", "The number 4", "4"),
    "5" -> Syntax("Numeric Literal", "The number 5", "5"),
    "6" -> Syntax("Numeric Literal", "The number 6", "6"),
    "7" -> Syntax("Numeric Literal", "The number 7", "7"),
    "8" -> Syntax("Numeric Literal", "The number 8", "8"),
    "9" -> Syntax("Numeric Literal", "The number 9", "9"),
    "„" ->
      Syntax(
        "Base-255 Compressed String",
        "Decompress and push a string, converted from a bijective base 255 number using the codepage",
        "\"<compressed string>„",
      ),
    "”" ->
      Syntax(
        "Dictionary Compressed String",
        "Decompress and push a string using SSS compression, shamelessly stolen from Jelly",
        "\"<compressed string>”",
      ),
    "“" ->
      Syntax(
        "Base-255 Compressed Number",
        "Decompress and push a number, converted from a bijective base 255 number using the codepage",
        "\"<compressed number>“",
      ),
    "#$" ->
      Syntax(
        "Retrieve Variable",
        "Push the value of a variable.",
        "#$<variable>",
      ),
    "#=" ->
      Syntax(
        "Assign Variable",
        "Assign a variable to a value.",
        "#=<variable>",
      ),
    "#>" ->
      Syntax(
        "Augmented Assignment",
        "Apply a function to a variable value and store the result in the same variable.",
        "<function> #> <variable>",
      ),
    "#:[" ->
      Syntax(
        "Variable Unpacking",
        "Unpack the top of the stack into a list of variables.",
        "#:[<var>|<var>|<var>]",
      ),
    "¤" ->
      Syntax(
        "Context Paramter Index",
        "Index into the list of context parameters.",
        "¤<number>",
      ),
  )
end SyntaxInfo
