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
  )
end SyntaxInfo
