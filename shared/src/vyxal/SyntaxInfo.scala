package vyxal

case class Syntax(
    name: String,
    literateKeywords: Seq[String],
    description: String,
    usage: String,
)

object SyntaxInfo:
  val info: Map[String, Syntax] = Map(
    "[" ->
      Syntax(
        "Ternary Statement",
        Seq("?", "?->"),
        "Open a ternary statement. Pop condition, if truthy, run <ontrue>, else run <onfalse>",
        "<condition> [<ontrue>|<onfalse>}",
      ),
    "]" ->
      Syntax(
        "Close All Structures",
        Seq("close-all", "end-all"),
        "Match and close all open structures.",
        "<structure openers>] <code not in structure>",
      ),
    "(" ->
      Syntax(
        "For Loop",
        Seq("for", "for<", "do-to-each", "each-as"),
        "Open a for loop. For each item in the top of the stack, execute code, storing loop variable.",
        "<iterable> (<variable>|<code>}",
      ),
    ")" ->
      Syntax(
        "Close Two Structures",
        Seq("end-end"),
        "Match and close two open structures.",
        "<structure open><structure open> <code> ) <code not in structure>",
      ),
    "{" ->
      Syntax(
        "While Loop",
        Seq("while", "while<"),
        "Open a while loop. While the top of the stack is truthy, execute code.",
        "{<condition>|<code>}",
      ),
    "}" ->
      Syntax(
        "Close A Structure",
        Seq(
          "end",
          "endfor",
          "end-for",
          "endwhile",
          "end-while",
          "endlambda",
          "end-lambda",
          "end",
        ),
        "Match and close the nearest open structure.",
        "<structure open> <code> } <code not in structure>",
      ),
    "|" ->
      Syntax(
        "Structure Branch",
        Seq(
          ":",
          "->",
          "else:",
          "else",
          "elif",
          "else-if",
          "body",
          "do",
          "branch",
          "then",
          "in",
          "using",
          "no?",
          "=>",
          "from",
        ),
        "Delimit the next section in a structure.",
        "<structure open> <code> | <code> ...",
      ),
    "\"" ->
      Syntax(
        "Open/Close String",
        Seq(),
        "Open/close a string. If the string is closed, push it to the stack. Closes all string types",
        "\"string contents\"",
      ),
    "'" ->
      Syntax(
        "One Character String",
        Seq(),
        "Push the next character as a string",
        "'<character>",
      ),
    "ᶴ" ->
      Syntax(
        "Two Character String",
        Seq(),
        "Push the next two characters as a string",
        "ᶴ<character><character>",
      ),
    "~" ->
      Syntax(
        "Two Byte Number",
        Seq(),
        "Push the next two bytes as a number, converted from bijective base 255 using the codepage",
        "~<character><character>",
      ),
    "#[" ->
      Syntax(
        "Open List",
        Seq("["),
        "Open a list. Pushes the list to the stack when closed.",
        "#[item|item|item#]",
      ),
    "#]" ->
      Syntax(
        "Close List",
        Seq("]"),
        "Close a list. Pushes the list to the stack when closed.",
        "#[item|item|item#]",
      ),
    "λ" ->
      Syntax(
        "Open Lambda",
        Seq("lam", "lambda", "{"),
        "Open a lambda.",
        "λ<parameters>|<code>}",
      ),
    "ƛ" ->
      Syntax(
        "Open Map Lambda",
        Seq("map-lam", "map<", "map-lambda"),
        "Open a lambda that automatically maps its function to the top of the stack",
        "ƛ<code>}",
      ),
    "Ω" ->
      Syntax(
        "Open Filter Lambda",
        Seq("filter-lam", "filter<", "filter-lambda"),
        "Open a lambda that automatically filters the top of the stack by its function",
        "Ω<code>}",
      ),
    "₳" ->
      Syntax(
        "Open Reduce/Accumulate Lambda",
        Seq(
          "reduce-lam",
          "reduce<",
          "reduce-lambda",
          "fold<",
          "fold-lam",
          "fold-lambda",
        ),
        "Open a lambda that automatically reduces/accumulates the top of the stack by its function",
        "₳<code>}",
      ),
    "µ" ->
      Syntax(
        "Open Sort Lambda",
        Seq("sort-lam", "sort<", "sort-lambda"),
        "Open a lambda that automatically sorts the top of the stack by its function",
        "µ<code>}",
      ),
    "#{" ->
      Syntax(
        "If/Elif/Else Statement",
        Seq("if"),
        "Open an if statement. Allows for if/elif/else statements",
        "#{<if condition>|<code>|<else if condition>|<code>|<else code>}",
      ),
    "Ṇ" ->
      Syntax(
        "Generator Structure",
        Seq("relation<", "generate<", "generate-from<"),
        "Open a generator structure. Allows for generator expressions",
        "Ṇ<code>|<initial vector>}",
      ),
    "Ḍ" ->
      Syntax(
        "Open Decision Problem Structure",
        Seq("exists<"),
        "Open a decision problem structure. Returns whether an iterable has any items that match a predicate",
        "Ḍ<predicate>|<container> }",
      ),
    "∆" ->
      Syntax(
        "Mathematical Digraphs",
        Seq(),
        "Used for math-related digraphs",
        "∆<character>",
      ),
    "ø" ->
      Syntax(
        "String Digraphs",
        Seq(),
        "Used for string-related digraphs",
        "ø<character>",
      ),
    "Þ" ->
      Syntax(
        "List Digraphs",
        Seq(),
        "Used for list-related digraphs",
        "Þ<character>",
      ),
    "k" ->
      Syntax(
        "Constant Digraphs",
        Seq(),
        "Used for constant-related digraphs",
        "k<character>",
      ),
    "#" ->
      Syntax(
        "Miscellaneous Digraphs",
        Seq(),
        "Used for miscellaneous digraphs",
        "#<character>",
      ),
    "##" ->
      Syntax(
        "Comment",
        Seq(),
        "Comment out the rest of the line",
        "##<comment>",
      ),
    "." ->
      Syntax(
        "Decimal Separator",
        Seq(),
        "Used to separate the integer and fractional parts of a number",
        "<integer>.<fractional>",
      ),
    "ı" ->
      Syntax(
        "Imaginary Number",
        Seq("i"),
        "Used to represent the imaginary unit",
        "<real>ı<imaginary>",
      ),
    "0" -> Syntax("Numeric Literal", Seq(), "The number 0", "0"),
    "1" -> Syntax("Numeric Literal", Seq(), "The number 1", "1"),
    "2" -> Syntax("Numeric Literal", Seq(), "The number 2", "2"),
    "3" -> Syntax("Numeric Literal", Seq(), "The number 3", "3"),
    "4" -> Syntax("Numeric Literal", Seq(), "The number 4", "4"),
    "5" -> Syntax("Numeric Literal", Seq(), "The number 5", "5"),
    "6" -> Syntax("Numeric Literal", Seq(), "The number 6", "6"),
    "7" -> Syntax("Numeric Literal", Seq(), "The number 7", "7"),
    "8" -> Syntax("Numeric Literal", Seq(), "The number 8", "8"),
    "9" -> Syntax("Numeric Literal", Seq(), "The number 9", "9"),
    "„" ->
      Syntax(
        "Base-252 Compressed String",
        Seq(),
        "Decompress and push a string, converted from a bijective base 252 number using the codepage",
        "\"<compressed string>„",
      ),
    "”" ->
      Syntax(
        "Dictionary Compressed String",
        Seq(),
        "Decompress and push a string using SSS compression, shamelessly stolen from Jelly",
        "\"<compressed string>”",
      ),
    "“" ->
      Syntax(
        "Base-252 Compressed Number",
        Seq(),
        "Decompress and push a number, converted from a bijective base 252 number using the codepage",
        "\"<compressed number>“",
      ),
    "#$" ->
      Syntax(
        "Retrieve Variable",
        Seq("$"),
        "Push the value of a variable.",
        "#$<variable>",
      ),
    "#=" ->
      Syntax(
        "Assign Variable",
        Seq(":="),
        "Assign a variable to a value.",
        "#=<variable>",
      ),
    "#>" ->
      Syntax(
        "Augmented Assignment",
        Seq(":>"),
        "Apply a function to a variable value and store the result in the same variable.",
        "<function> #> <variable>",
      ),
    "#:[" ->
      Syntax(
        "Variable Unpacking",
        Seq(":=["),
        "Unpack the top of the stack into a list of variables.",
        "#:[<var>|<var>|<var>]",
      ),
    "¤" ->
      Syntax(
        "Context Paramter Index",
        Seq("`n`"),
        "Index into the list of context parameters.",
        "¤<number>",
      ),
    "#::" ->
      Syntax(
        "Element/Modifier Definition",
        Seq("define"),
        "Define a custom element/modifier that can be used in programs",
        "#::<mode><name>|<arg>|<arg>...|<code>}",
      ),
    "#:@" ->
      Syntax(
        "Defined Element Call",
        Seq("$@"),
        "Call a defined element",
        "#:@<name>",
      ),
    "#:`" ->
      Syntax(
        "Defined Modifier Call",
        Seq("$:"),
        "Call a defined modifier",
        "#:`<name>",
      ),
    "#:~" ->
      Syntax(
        "Retrieve Original Element",
        Seq("$."),
        "Call the original, vyxal defined, meaning of an element. Useful for when you want to define a new element with the same name as a built-in one",
        "#:~<name>",
      ),
    "#:R" ->
      Syntax(
        "Record Definition",
        Seq("record"),
        "Define a record with members",
        "#:R<name>|#$restricted #=private #!public}",
      ),
    "#:>>" ->
      Syntax(
        "Extension Method",
        Seq("extension"),
        "Define an overload on a custom element based on types. Requires at least one type to be specified.",
        "#:>><name>|<arg1>|<type1>|<arg2>|<type2>...|<impl>}",
      ),
  )
end SyntaxInfo
