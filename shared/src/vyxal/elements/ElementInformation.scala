package vyxal.elements

case class Element(
    symbol: String,
    /** Names that can be used for this in literate mode */
    keywords: Seq[String],
    arity: Int,
    options: Options,
    overloads: Overload*
)

case class Modifier(
    symbol: String,
    /** Names that can be used for this in literate mode */
    keywords: Seq[String],
    /** How many elements this modifier accepts as input (its arity) */
    numberOfElements: Int,
    overloads: ModifierOverload*
)

case class Overload(
    name: String,
    args: Seq[String],
    description: String,
    typeSwitchable: Boolean = false,
)

case class ModifierOverload(
    name: String,
    args: Seq[String],
    description: String,
    example: String,
)

case class Options(
    vectorises: Boolean = false,
    peeks: Boolean = false,
)

object ElementInformation:

  def AddElement(
      symbol: String,
      keywords: Seq[String],
      arity: Int,
      options: Options,
      overloads: Overload*
  ): (String, Element) =
    symbol -> Element(symbol, keywords, arity, options, overloads*)

  def AddModifier(
      symbol: String,
      keywords: Seq[String],
      numberOfElements: Int,
      overloads: ModifierOverload*
  ): (String, Modifier) =
    symbol -> Modifier(symbol, keywords, numberOfElements, overloads*)

  def symbolForElement(keyword: String): Option[String] =
    ElementInformation.elements
      .find((_, elem) => elem.keywords.contains(keyword))
      .map((_, elem) => elem.symbol)

  def symbolForModifier(keyword: String): Option[String] =
    ElementInformation.modifiers
      .find((_, mod) => mod.keywords.contains(keyword))
      .map((_, mod) => mod.symbol)

  val elements: Map[String, Element] = Map(
    AddElement(
      symbol = "Ƶ",
      keywords = Seq("tailless-top", "tail-extract"),
      arity = 1,
      Options(),
      Overload(
        name = "Tailless Top",
        args = Seq("any"),
        description = "Push #1[:-1], #1[-1] to the stack",
      ),
    ),
    AddElement(
      symbol = "⊞",
      keywords = Seq("counts", "counts-of"),
      arity = 1,
      Options(),
      Overload(
        name = "Counts of Items",
        args = Seq("lst"),
        description = "[#1.count(x) for x in set(#1)]",
      ),
    ),
    AddElement(
      symbol = "÷",
      keywords = Seq("divide", "string-pieces", "regex-split", "/", "div"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Division",
        args = Seq("num", "num"),
        description = "#1 / #2",
      ),
      Overload(
        name = "String into N Pieces",
        args = Seq("str", "num"),
        description = "Split string {#1|#2} into {#2|#1} pieces",
        typeSwitchable = true,
      ),
      Overload(
        name = "Regex Split",
        args = Seq("str", "str"),
        description = "Split #1 by regex #2",
      ),
    ),
    AddElement(
      symbol = "×",
      keywords =
        Seq("multiply", "string-repeat", "ring-translate", "*", "times"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Multiplication",
        args = Seq("num", "num"),
        description = "#1 * #2 (#1 times #2)",
      ),
      Overload(
        name = "String Repeat",
        args = Seq("str", "num"),
        description = "Repeat string {#1|#2} {#2|#1} times",
        typeSwitchable = true,
      ),
      Overload(
        name = "Ring Translate",
        args = Seq("str", "str"),
        description = "Ring translate #1 according to #2. ",
      ),
      Overload(
        name = "Function Arity Change",
        args = Seq("fun", "num"),
        description =
          "Change the arity of function {#1|#2} to {#2|#1}. If {#2|#1} is 0, returns a function that ignores its input.",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "∧",
      keywords = Seq("and", "&&", "logical-and"),
      arity = 2,
      Options(),
      Overload(
        name = "Logical And",
        args = Seq("nsl", "nsl"),
        description =
          "Python-style and - if #2 is false, return #2, else return #1",
      ),
      Overload(
        name = "Short Circuit And",
        args = Seq("fun", "fun"),
        description =
          "Short circuit and - if #2() is false, return #2(), else return #1()",
      ),
    ),
    AddElement(
      symbol = "∨",
      keywords = Seq("or", "!!", "logical-or"),
      arity = 2,
      Options(),
      Overload(
        name = "Logical Or",
        args = Seq("any", "any"),
        description =
          "Python style or - if #2 is true, return #2, else return #1",
      ),
      Overload(
        name = "Short Circuit Or",
        args = Seq("fun", "fun"),
        description =
          "Short circuit or - if #2() is true, return #2(), else return #1()",
      ),
    ),
    AddElement(
      symbol = "¬",
      keywords = Seq("not", "~", "logical-not"),
      arity = 1,
      Options(),
      Overload(
        name = "Not",
        args = Seq("any"),
        description = "if #1 is truthy, return False, else return True",
      ),
    ),
    AddElement(
      symbol = "ʀ",
      keywords = Seq("zero-range", "lowercase", "range-zero", "nrange-zero"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Range 0",
        args = Seq("num"),
        description = "Range from 0 to #1, exclusive",
      ),
      Overload(
        name = "Lowercase",
        args = Seq("str"),
        description = "Lowercase #1",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "ʁ",
      keywords = Seq(
        "0->n++",
        "uppercase",
        "range-0->n++",
        "n+range-0",
        "inclusive-zero-range",
      ),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Range 0 Inclusive",
        args = Seq("num"),
        description = "Range from 0 to #1, inclusive",
      ),
      Overload(
        name = "Uppercase",
        args = Seq("str"),
        description = "Uppercase #1",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "ɾ",
      keywords = Seq("one->n++", "inclusive-one-range", "is-alpha?"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Range 1 Inclusive",
        args = Seq("num"),
        description = "Range from 1 to #1, inclusive",
      ),
      Overload(
        name = "Is Character Alphabetical",
        args = Seq("str"),
        description = "Check if #1 is alphabetical (i.e. is a letter)",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "‹",
      keywords =
        Seq("decrement", "--", "pad-to-8", "dec", "pad-8", "pad-to-byte"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Decrement",
        args = Seq("num"),
        description = "#1 - 1",
      ),
      Overload(
        name = "Pad to 8",
        args = Seq("str"),
        description = "Pad #1 to a length that is a multiple of 8 with '0's",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "›",
      keywords =
        Seq("increment", "++", "space-to-0", "replace-spaces-with-0s", "inc"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Increment",
        args = Seq("num"),
        description = "#1 + 1",
      ),
      Overload(
        name = "Spaces to 0s",
        args = Seq("str"),
        description = "Replace spaces in #1 with '0's",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "!",
      keywords = Seq("factorial", "!", "titlecase", "fact", "title", "fac"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Factorial",
        args = Seq("num"),
        description = "Factorial of #1",
      ),
      Overload(
        name = "Titlecase",
        args = Seq("str"),
        description = "Titlecase #1",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "$",
      keywords = Seq("swap"),
      arity = 2,
      Options(),
      Overload(
        name = "Swap",
        args = Seq("any", "any"),
        description = "Swap #1 and #2 on the stack: #1 #2 -> #2 #1",
      ),
    ),
    AddElement(
      symbol = "%",
      keywords = Seq("mod", "modulo", "%", "remainder"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Modulo",
        args = Seq("num", "num"),
        description = "#1 % #2 (remainder of #1 divided by #2)",
      ),
      Overload(
        name = "String Format",
        args = Seq("str", "any"),
        description = "Format {#1|#2} with {#2|#1}",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "&",
      keywords = Seq("append"),
      arity = 2,
      Options(),
      Overload(
        name = "Append",
        args = Seq("any", "any"),
        description = "Append #2 to #1",
      ),
    ),
    AddElement(
      symbol = "'",
      keywords = Seq(
        "join-sublists",
        "join-sublists-on-spaces-then-newlines",
        "space-grid",
      ),
      arity = 1,
      Options(),
      Overload(
        name = "Join Sublists on Spaces then Newlines (Element Form of ')",
        args = Seq("lst"),
        description =
          "Join sublists of #1 on spaces, then join those on newlines",
      ),
    ),
    AddElement(
      symbol = "Ꮬ",
      keywords = Seq("concat-sublists", "concat-grid", "grid"),
      arity = 1,
      Options(),
      Overload(
        name = "Concatenate sublists then join on Newlines (Element Form of Ꮬ)",
        args = Seq("lst"),
        description = "Concatenate sublists, then join those on newlines",
      ),
    ),
    AddElement(
      symbol = "*",
      keywords = Seq("exponentiate", "pow", "**", "power"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Exponentiation",
        args = Seq("num", "num"),
        description = "#1 ** #2",
      ),
    ),
    AddElement(
      symbol = "+",
      keywords = Seq("add", "+", "plus", "addition"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Addition",
        args = Seq("num", "num"),
        description = "#1 + #2",
      ),
      Overload(
        name = "String and Number Concatenation",
        args = Seq("str", "num"),
        description = "{#1|str(#1)} + {str(#2)|#2}",
        typeSwitchable = true,
      ),
      Overload(
        name = "String Concatenation",
        args = Seq("str", "str"),
        description = "#1 + #2",
      ),
    ),
    AddElement(
      symbol = ",",
      keywords = Seq("println", "stdout", "output", "out"),
      arity = 1,
      Options(),
      Overload(
        name = "Print",
        args = Seq("any"),
        description = "Print #1 to stdout, followed by a newline",
      ),
    ),
    AddElement(
      symbol = "-",
      keywords = Seq("subtract", "-", "minus", "subtraction", "regex-remove"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Subtraction",
        args = Seq("num", "num"),
        description = "#1 - #2",
      ),
      Overload(
        name = "Prepend/Append Hyphens",
        args = Seq("str", "num"),
        description = "{#1|'-' * #1} + {#2 * '-'|#2}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Regex Remove",
        args = Seq("str", "str"),
        description = "Remove matches of #2 from #1",
      ),
    ),
    AddElement(
      ":",
      Seq("dup", "duplicate", "dupe"),
      arity = 1,
      Options(),
      Overload(
        name = "Duplicate",
        args = Seq("any"),
        description = "Push #1 twice to the stack: #1 -> #1 #1",
      ),
    ),
    AddElement(
      symbol = ";",
      keywords = Seq("pair", "cons"),
      arity = 2,
      Options(),
      Overload(
        name = "Pair",
        args = Seq("any", "any"),
        description = "Push a list [#1, #2] to the stack: #1 #2 -> [#1, #2]",
      ),
    ),
    AddElement(
      symbol = "<",
      keywords = Seq("less-than", "<", "lt", "---until-false"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Less Than",
        args = Seq("scl", "scl"),
        description = "#1 < #2",
      ),
      Overload(
        name = "Decrement Until False",
        args = Seq("fun", "num"),
        description = "Decrement {#2|#1} until {#1|#2} is false",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "=",
      keywords = Seq("equals", "==", "eq"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Equals",
        args = Seq("scl", "scl"),
        description = "#1 == #2",
      ),
    ),
    AddElement(
      symbol = ">",
      keywords = Seq("greater-than", ">", "gt", "++-until-false"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Greater Than",
        args = Seq("scl", "scl"),
        description = "#1 > #2",
      ),
      Overload(
        name = "Increment Until False",
        args = Seq("fun", "num"),
        description = "Increment {#2|#1} until {#1|#2} is false",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "?",
      keywords = Seq("stdin", "input", "in"),
      arity = 0,
      Options(),
      Overload(
        name = "Input",
        args = Seq(),
        description = "Get the next input item, evaluated.",
      ),
    ),
    AddElement(
      symbol = "#?",
      keywords = Seq("inputs", "all-inputs", "all-stdin"),
      arity = 0,
      Options(vectorises = false),
      Overload(
        name = "Inputs",
        args = Seq(),
        description = "Get all the global inputs as a list",
      ),
    ),
    AddElement(
      symbol = "@",
      keywords =
        Seq("absolute-difference", "abs-diff", "levenstein", "to-overpairs"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Absolute Difference",
        args = Seq("num", "num"),
        description = "Absolute difference between #1 and #2",
      ),
      Overload(
        name = "Levenstein Distance",
        args = Seq("str", "str"),
        description = "Levenstein distance between #1 and #2",
      ),
      Overload(
        name = "Reduce Overlapping Pairs",
        args = Seq("lst", "fun"),
        description = "Reduce overlapping pairs in {#1|#2} by function {#2|#1}",
      ),
    ),
    AddElement(
      symbol = "A",
      keywords = Seq("all", "all?", "vowel?", "is-vowel", "is-vowel?"),
      arity = 1,
      Options(),
      Overload(
        name = "All",
        args = Seq("any"),
        description = "Are all elements of #1 are truthy",
      ),
    ),
    AddElement(
      symbol = "B",
      keywords = Seq("to-binary"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "To Binary",
        args = Seq("num"),
        description = "Convert #1 to binary",
      ),
      Overload(
        name = "String to Binary",
        args = Seq("str"),
        description =
          "Convert each character in #1 to a binary representation of its unicode value",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "C",
      keywords = Seq("count"),
      arity = 2,
      Options(),
      Overload(
        name = "Count",
        args = Seq("lst", "scl"),
        description = "Count occurrences of {#2|#1} in {#1|#2}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Count",
        args = Seq("lst", "lst"),
        description =
          "Count occurrences of the list with shallower depth in the list with deeper depth",
      ),
    ),
    AddElement(
      symbol = "D",
      keywords = Seq("triplicate"),
      arity = 1,
      Options(),
      Overload(
        name = "Triplicate",
        args = Seq("any"),
        description = "Push #1 thrice to the stack: #1 -> #1 #1 #1",
      ),
    ),
    AddElement(
      symbol = "E",
      keywords = Seq("two-power", "eval"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "2 to the Power of N",
        args = Seq("num"),
        description = "2 ** #1",
      ),
      Overload(
        name = "Eval",
        args = Seq("str"),
        description = "Evaluate #1",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "F",
      keywords = Seq("filter", "find", "index-of"),
      arity = 2,
      Options(),
      Overload(
        name = "Filter",
        args = Seq("fun", "any"),
        description = "Filter {#1|#2} by function {#2|#1}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Find",
        args = Seq("nls", "nls"),
        description =
          "Find the index of #1 in #2. Switches #1 and #2 so that the haystack is the deeper list",
      ),
    ),
    AddElement(
      symbol = "G",
      keywords = Seq("max-of", "maximum-of", "max", "maximum"),
      arity = 1,
      Options(),
      Overload(
        name = "Monadic Maximum",
        args = Seq("lst"),
        description = "Maximum of #1",
      ),
    ),
    AddElement(
      symbol = "H",
      keywords = Seq("to-hex", "from-hex"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "To Hex",
        args = Seq("num"),
        description = "Convert #1 to hexadecimal",
      ),
      Overload(
        name = "From Hex",
        args = Seq("str"),
        description =
          "Convert #1 from hexadecimal to a number. Inverse of 'to-hex'",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "I",
      keywords = Seq("interleave", "reject"),
      arity = 2,
      Options(),
      Overload(
        name = "Interleave",
        args = Seq("any", "any"),
        description = "Interleave #1 and #2",
      ),
      Overload(
        name = "Reject",
        args = Seq("any", "fun"),
        description =
          "Remove elements of {#1|#2} that satisfy function {#2|#1}",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "J",
      keywords = Seq("join", "concat", "merge"),
      arity = 2,
      Options(),
      Overload(
        name = "Join",
        args = Seq("lst", "scl"),
        description = "{Add #2 to the end of #1|Prepend #1 to #2}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Join / Merge",
        args = Seq("lst", "lst"),
        description = "Add all elements of #2 to #1",
      ),
      Overload(
        name = "Number Pair",
        args = Seq("num", "num"),
        description = "Create a list of #1 and #2",
      ),
      Overload(
        name = "String Concatenation",
        args = Seq("str|num", "str|num"),
        description = "string(#1) + string(#2) (if either #1 or #2 is a string)",
      ),
    ),
    AddElement(
      symbol = "K",
      keywords = Seq("factors", "divisors", "is-numeric?", "is-numeric"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Factors",
        args = Seq("num"),
        description = "Get the factors of #1",
      ),
      Overload(
        name = "Is Numeric",
        args = Seq("str"),
        description = "Check if #1 is numeric",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "L",
      keywords = Seq("length", "len"),
      arity = 1,
      Options(),
      Overload(
        name = "Length",
        args = Seq("any"),
        description = "Length of #1",
      ),
    ),
    AddElement(
      symbol = "M",
      keywords = Seq("map", "mold", "multiplicity", "regex-match"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Map",
        args = Seq("fun", "any"),
        description = "Map function {#1|#2} over {#2|#1}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Mold",
        args = Seq("lst", "lst"),
        description = "Reshape #1 to the shape of #2",
      ),
      Overload(
        name = "Multiplicity",
        args = Seq("num", "num"),
        description = "How many times #1 divides #2",
      ),
      Overload(
        name = "Regex Match",
        args = Seq("str", "str"),
        description = "Return the first match of #2 in #1",
      ),
    ),
    AddElement(
      symbol = "N",
      keywords = Seq("negate", "swapcase", "first>-1"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Negate",
        args = Seq("num"),
        description = "-#1",
      ),
      Overload(
        name = "Negate",
        args = Seq("str"),
        description = "Swap the case of each letter #1",
      ),
      Overload(
        name = "First Non-Negative Integer Where Predicate is True",
        args = Seq("fun"),
        description = "First non-negative integer where #1 is true",
      ),
    ),
    AddElement(
      symbol = "O",
      keywords = Seq("ord", "chr"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Character to Unicode",
        args = Seq("str"),
        description = "Unicode value of each letter in #1",
      ),
      Overload(
        name = "Unicode to Character",
        args = Seq("num"),
        description = "Character of each unicode value in #1",
      ),
    ),
    AddElement(
      symbol = "P",
      keywords = Seq("prefixes"),
      arity = 1,
      Options(),
      Overload(
        name = "Prefixes",
        args = Seq("lst"),
        description =
          "Get all prefixes of #1. Treats numbers as a list of digits",
      ),
    ),
    AddElement(
      symbol = "Q",
      keywords = Seq("remove-at", "regex-groups"),
      arity = 2,
      Options(),
      Overload(
        name = "Remove At",
        args = Seq("nsl", "num"),
        description = "Remove the element at index #2 from #1",
      ),
      Overload(
        name = "Regex Groups",
        args = Seq("str", "str"),
        description = "Return the groups of the first match of #2 in #1",
      ),
    ),
    AddElement(
      symbol = "R",
      keywords = Seq("range", "reduce", "regex-match?"),
      arity = 2,
      Options(),
      Overload(
        name = "Range",
        args = Seq("num", "num"),
        description = "Range from #1 to #2, exclusive",
      ),
      Overload(
        name = "Reduce",
        args = Seq("lst", "fun"),
        description = "Reduce #1 by function #2",
      ),
      Overload(
        name = "Regex Match?",
        args = Seq("str", "str"),
        description = "Check if #2 matches #1",
      ),
    ),
    AddElement(
      symbol = "S",
      keywords = Seq("sort"),
      arity = 1,
      Options(),
      Overload(
        name = "Sort",
        args = Seq("itr"),
        description = "Sort #1",
      ),
    ),
    AddElement(
      symbol = "T",
      keywords = Seq("transpose", "triple", "alpha-only?"),
      arity = 1,
      Options(),
      Overload(
        name = "Transpose",
        args = Seq("lst"),
        description =
          "Transpose #1. Will not terminate on an infinite list of finite lists. Use ÞT if you need that.",
      ),
      Overload(
        name = "Triple",
        args = Seq("num"),
        description = "#1 * 3",
      ),
      Overload(
        name = "Does String Contain Only Alphabetic Characters",
        args = Seq("str"),
        description = "Check if #1 contains only alphabetic characters",
      ),
    ),
    AddElement(
      symbol = "U",
      keywords = Seq("uninterleave"),
      arity = 1,
      Options(),
      Overload(
        name = "Uninterleave",
        args = Seq("lst"),
        description = "Uninterleave #1",
      ),
    ),
    AddElement(
      symbol = "V",
      keywords = Seq(
        "vectorise-reverse",
        "one-minus-x",
        "complement",
        "split-spaces-reverse",
      ),
      arity = 1,
      Options(),
      Overload(
        name = "Vectorise Reverse",
        args = Seq("lst"),
        description = "Reverse each item in #1",
      ),
      Overload(
        name = "1 - X",
        args = Seq("num"),
        description = "1 - #1",
      ),
      Overload(
        name = "Split on Spaces and Reverse Each Substring",
        args = Seq("str"),
        description = "Split #1 on spaces and reverse each substring",
      ),
    ),
    AddElement(
      symbol = "W",
      keywords = Seq("wrap", "stack-wrap"),
      arity = -1,
      Options(),
      Overload(
        name = "Wrap",
        args = Seq(),
        description = "Wrap the entire stack into a list",
      ),
    ),
    AddElement(
      symbol = "X",
      keywords = Seq("cartesian-product"),
      arity = 2,
      Options(),
      Overload(
        name = "Cartesian Product",
        args = Seq("lst", "lst"),
        description = "Cartesian product of #1 and #2",
      ),
    ),
    AddElement(
      symbol = "Y",
      keywords = Seq("list-repeat"),
      arity = 2,
      Options(),
      Overload(
        name = "List Repeat",
        args = Seq("num", "num"),
        description = "A list of #1 repeated #2 times. E.g. 3 4 -> [3, 3, 3, 3]",
      ),
      Overload(
        name = "List Repeat",
        args = Seq("itr", "num"),
        description = "A list of {#2|#1} instances of string {#1|#2}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Vectorised Repeat",
        args = Seq("itr", "lst[nsl]"),
        description = "Repeat each element of #2 (#1|#1.length) times",
      ),
    ),
    AddElement(
      symbol = "Z",
      keywords = Seq("zip"),
      arity = 2,
      Options(),
      Overload(
        name = "Zip",
        args = Seq("lst", "lst"),
        description = "Zip #1 and #2",
      ),
    ),
    AddElement(
      symbol = "^",
      keywords = Seq("reverse-stack"),
      arity = -1,
      Options(),
      Overload(
        name = "Reverse Stack",
        args = Seq(),
        description = "Reverse the stack",
      ),
    ),
    AddElement(
      symbol = "_",
      keywords = Seq("pop", "discard"),
      arity = 1,
      Options(),
      Overload(
        name = "Pop",
        args = Seq(),
        description = "Pop the top of the stack",
      ),
    ),
    AddElement(
      symbol = "`",
      keywords = Seq("len-stack", "stack-len"),
      arity = 0,
      Options(),
      Overload(
        name = "Length of Stack",
        args = Seq(),
        description = "Push the length of the stack to the stack",
      ),
    ),
    AddElement(
      symbol = "a",
      keywords = Seq("any", "any?", "uppercase?"),
      arity = 1,
      Options(),
      Overload(
        name = "Any",
        args = Seq("num"),
        description = "Are any digits of #1 truthy",
      ),
      Overload(
        name = "Is Uppercase",
        args = Seq("str"),
        description =
          "Check if #1 is uppercase. With string.len > 1, vectorises over each character",
      ),
      Overload(
        name = "Any",
        args = Seq("lst"),
        description = "Are any elements of #1 truthy",
      ),
    ),
    AddElement(
      symbol = "b",
      keywords = Seq("from-binary"),
      arity = 1,
      Options(),
      Overload(
        name = "Binary Digits",
        args = Seq("num"),
        description = "Convert #1's list of digits from binary to base 10",
      ),
      Overload(
        name = "From Binary",
        args = Seq("str"),
        description = "Convert #1 from binary to a number",
      ),
      Overload(
        name = "From Binary",
        args = Seq("lst"),
        description = "Convert #1 from binary to a number",
      ),
    ),
    AddElement(
      symbol = "c",
      keywords = Seq("contains", "contains?", "is-in"),
      arity = 2,
      Options(),
      Overload(
        name = "Contains",
        args = Seq("scl", "scl"),
        description = "Is #2 in #1",
      ),
      Overload(
        name = "Contains",
        args = Seq("lst", "scl"),
        description = "Is {#2|#1} in {#1|#2}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Contains",
        args = Seq("lst", "lst"),
        description =
          "Is the list with shallower depth in the list with deeper depth",
      ),
    ),
    AddElement(
      symbol = "d",
      keywords = Seq("double"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Double",
        args = Seq("num"),
        description = "#1 * 2",
      ),
      Overload(
        name = "Double",
        args = Seq("str"),
        description = "Append a copy of #1 to itself",
      ),
    ),
    AddElement(
      symbol = "e",
      keywords = Seq("even?", "is-even", "split-newlines", "/newline"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Is Even",
        args = Seq("num"),
        description = "Is #1 even",
      ),
      Overload(
        name = "Split Newlines",
        args = Seq("str"),
        description = "Split #1 by newlines",
      ),
    ),
    AddElement(
      symbol = "f",
      keywords = Seq("flatten"),
      arity = 1,
      Options(),
      Overload(
        name = "List of Digits",
        args = Seq("num"),
        description = "Push a list of the digits of #1 to the stack",
      ),
      Overload(
        name = "List of Characters",
        args = Seq("str"),
        description = "Push a list of the characters of #1 to the stack",
      ),
      Overload(
        name = "Flatten",
        args = Seq("lst"),
        description = "Flatten #1",
      ),
    ),
    AddElement(
      symbol = "g",
      keywords = Seq("min-of", "minimum-of", "min", "minimum"),
      arity = 1,
      Options(),
      Overload(
        name = "Monadic Minimum",
        args = Seq("lst"),
        description = "Minimum of #1",
      ),
    ),
    AddElement(
      symbol = "h",
      keywords = Seq("head", "first"),
      arity = 1,
      Options(),
      Overload(
        name = "Head",
        args = Seq("any"),
        description = "First element of #1",
      ),
    ),
    AddElement(
      symbol = "i",
      keywords = Seq(
        "index",
        "at",
        "item-at",
        "nth-item",
        "collect-unique",
        "enclose",
        "<**",
      ),
      arity = 2,
      Options(),
      Overload(
        name = "Nth Element",
        args = Seq("itr", "num"),
        description = "Get the {#2|#1}th element of {#1|#2}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Vectorised Index",
        args = Seq("itr", "lst[num]"),
        description = "[#1[_] for _ in #2]",
      ),
      Overload(
        name = "String Enclose",
        args = Seq("str", "str"),
        description =
          "enclose #2 in #1 (#1[0:len(#1)//2] + #2 + #1[len(#1)//2:])",
      ),
      Overload(
        name = "Object Member Retrieval",
        args = Seq("obj", "str"),
        description = "{#1|#2}.{#2|#1}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Collect Unique Values (+ Initial Value)",
        args = Seq("any", "fun"),
        description =
          "Apply #2 on #1 and collect unique values. Does include the initial value.",
      ),
    ),
    AddElement(
      symbol = "j",
      keywords = Seq("join-on"),
      arity = 2,
      Options(),
      Overload(
        name = "Join On",
        args = Seq("lst", "scl"),
        description = "Join {#1|#2} on {#2|#1}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Intersperse",
        args = Seq("lst", "lst"),
        description =
          "Intersperse elements of #2 within #1 (e.g. [1, [2,3], 4] [5, 6] -> [1, 5, 6, [2, 3], 5, 6, 4])",
      ),
      Overload(
        name = "Make Complex Number",
        args = Seq("num", "num"),
        description = "Create a complex number from #1 and #2 - #1 + #2i",
      ),
    ),
    AddElement(
      symbol = "l",
      keywords = Seq(
        "log",
        "logarithm",
        "scan-fixpoint",
        "scan-fix",
        "same-length?",
        "same-length",
        "length-equals?",
        "length-equals",
        "len-eq?",
      ),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Logarithm",
        args = Seq("num", "num"),
        description = "Log base #2 of #1",
      ),
      Overload(
        name = "Scan Fixpoint",
        args = Seq("fun", "any"),
        description =
          "Repeatedly apply {#1|#2} to {#2|#1} until it doesn't change, collecting intermediate values, including initial value.",
        typeSwitchable = true,
      ),
      Overload(
        name = "Same Length",
        args = Seq("str", "str"),
        description = "Are #1 and #2 the same length",
      ),
      Overload(
        name = "String Length Equals",
        args = Seq("str", "num"),
        description = "Is the length of {#1|#2} equal to {#2|#1}",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "m",
      keywords =
        Seq("ctx-secondary", "ctx2", "ctx-m", "context-m", "context-secondary"),
      arity = 0,
      Options(),
      Overload(
        name = "Context Secondary",
        args = Seq(),
        description =
          "Push the secondary context variable to the stack. If not in a function, push the string 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'",
      ),
    ),
    AddElement(
      symbol = "n",
      keywords =
        Seq("ctx-primary", "ctx", "ctx-n", "context-n", "context-primary"),
      arity = 0,
      Options(),
      Overload(
        name = "Context Primary",
        args = Seq(),
        description =
          "Push the primary context variable to the stack. If not in a function, push the string 'abcdefghijklmnopqrstuvwxyz'",
      ),
    ),
    AddElement(
      symbol = "o",
      keywords = Seq(
        "overlapping-sliding-window",
        "windows",
        "reduce-overlaps-by",
      ),
      arity = 2,
      Options(),
      Overload(
        name = "Windows",
        args = Seq("lst", "lst[num]"),
        description = "Get overlapping windows of #1 with a window of size #2",
      ),
      Overload(
        name = "Overlapping Slices",
        args = Seq("any", "num"),
        description =
          "Get overlapping pairs of iterable({#1|#2}) with a window of size {#2|#1}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Reduce Overlapping Slices",
        args = Seq("lst", "fun"),
        description =
          "Reduce overlapping slices of length #2.arity in #1 by function #2",
        typeSwitchable = true,
      ),
      Overload(
        name = "Reduce Set-Sized Overlapping Slices",
        args = Seq("lst", "num", "fun"),
        description =
          "Reduce overlapping slices of length #2 in #1 by function #3",
      ),
      Overload(
        name = "Reduce Set-Sized Overlapping Slices",
        args = Seq("lst", "fun", "num"),
        description =
          "Reduce overlapping slices of length #3 in #1 by function #2",
      ),
    ),
    AddElement(
      symbol = "p",
      keywords = Seq("prepend"),
      arity = 2,
      Options(),
      Overload(
        name = "Prepend",
        args = Seq("any", "any"),
        description = "Prepend #2 to #1",
      ),
    ),
    AddElement(
      symbol = "q",
      keywords = Seq("quotify"),
      arity = 1,
      Options(),
      Overload(
        name = "Quotify",
        args = Seq("any"),
        description = "Cast #1 to a string and wrap in quotes",
      ),
    ),
    AddElement(
      symbol = "r",
      keywords = Seq("replace"),
      arity = 3,
      Options(),
      Overload(
        name = "Replace",
        args = Seq("nsl", "nsl", "nsl"),
        description = "Replace all occurrences of #2 in #3 with #1",
      ),
      Overload(
        name = "Zip-With",
        args = Seq("lst", "lst", "fun"),
        description = "Zip #1 and #2 and apply #3 to each pair",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "s",
      keywords = Seq("split"),
      arity = 2,
      Options(),
      Overload(
        name = "Split",
        args = Seq("any", "any"),
        description = "Split #1 by #2",
      ),
    ),
    AddElement(
      symbol = "t",
      keywords = Seq("tail", "last"),
      arity = 1,
      Options(),
      Overload(
        name = "Tail",
        args = Seq("any"),
        description = "Last element of #1",
      ),
    ),
    AddElement(
      symbol = "u",
      keywords = Seq("unique", "uniquify"),
      arity = 1,
      Options(),
      Overload(
        name = "Unique",
        args = Seq("lst"),
        description = "Unique elements of #1",
      ),
      Overload(
        name = "Unique By Function",
        args = Seq("lst", "fun"),
        description = "Unique elements of #1 by applying #2",
      ),
    ),
    AddElement(
      symbol = "v",
      keywords = Seq("overlapping-pairs", "reduce-pairs-by"),
      arity = 1,
      Options(),
      Overload(
        name = "Overlapping Pairs",
        args = Seq("lst"),
        description = "Get overlapping pairs of #1",
      ),
      Overload(
        name = "Reduce Overlapping Pairs",
        args = Seq("lst", "fun"),
        description = "Reduce overlapping pairs in #1 by function #2",
      ),
    ),
    AddElement(
      symbol = "w",
      keywords = Seq("wrap-in-list", "singleton", "wrap-self"),
      arity = 1,
      Options(),
      Overload(
        name = "Wrap in List",
        args = Seq("any"),
        description = "Wrap #1 in a singleton list, creating a half pair",
      ),
    ),
    AddElement(
      symbol = "x",
      keywords = Seq("recurse"),
      arity = -1,
      Options(),
      Overload(
        name = "Recurse",
        args = Seq(),
        description =
          "Recursively call the current function (or the top-level program if not in a function)",
      ),
    ),
    AddElement(
      symbol = "y",
      keywords = Seq("transliterate", "call-while"),
      arity = 3,
      Options(),
      Overload(
        name = "Transliterate",
        args = Seq("nsl", "nsl", "nsl"),
        description = "Replace all occurrences of #2 in #1 with #3",
      ),
      Overload(
        name = "Call While",
        args = Seq("fun", "fun", "any"),
        description =
          "While #1(#3) is true, #3 = #2(#3). Return the result. Type switchable.",
      ),
    ),
    AddElement(
      symbol = "z",
      keywords = Seq("zip-with-filler"),
      arity = 2,
      Options(),
      Overload(
        name = "Zip With Filler",
        args = Seq("lst", "any"),
        description = "Transpose #1, filling empty spaces with #2",
      ),
    ),
    AddElement(
      symbol = "⨥",
      keywords = Seq("+2", "add-2", "++++", "inc-inc", "strlen==1"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Add 2",
        args = Seq("num"),
        description = "#1 + 2",
      ),
      Overload(
        name = "String Length Equals 1",
        args = Seq("str"),
        description = "Is the length of #1 equal to 1",
      ),
    ),
    AddElement(
      symbol = "⨪",
      keywords =
        Seq("-2", "subtract-2", "----", "dec-dec", "flip-bracket-palindrome"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Subtract 2",
        args = Seq("num"),
        description = "#1 - 2",
      ),
      Overload(
        name = "Flip Bracket Palindrome",
        args = Seq("str"),
        description =
          "Palindromise #1 by appending the reverse with brackets and slashes flipped",
      ),
    ),
    AddElement(
      symbol = "∑",
      keywords = Seq("sum", "sum-of", "+/", "/+", "sigma", "sigma-in-ohio"),
      arity = 1,
      Options(),
      Overload(
        name = "Sum",
        args = Seq("lst"),
        description = "Sum of #1",
      ),
      Overload(
        name = "Join and Evaluate",
        args = Seq("lst[at least 1 str]"),
        description = "Join #1 and evaluate the result",
      ),
    ),
    AddElement(
      symbol = "Π",
      keywords = Seq("product", "product-of", "*/", "first-int"),
      arity = 1,
      Options(),
      Overload(
        name = "Product",
        args = Seq("lst"),
        description = "Product of #1",
      ),
      Overload(
        name = "Number to Binary as String",
        args = Seq("num"),
        description = "Convert #1 to binary as a string",
      ),
      Overload(
        name = "First Integer Where Predicate is True",
        args = Seq("fun"),
        description = "First integer where #1 is true (positive or negative)",
      ),
    ),
    AddElement(
      symbol = "σ",
      keywords = Seq("cumulative-sums", "cumsums", "cumsum", "cum-sum", "-_-"),
      arity = 1,
      Options(),
      Overload(
        name = "Cumulative Sums",
        args = Seq("lst"),
        description = "Cumulative sums of #1",
      ),
    ),
    AddElement(
      symbol = "⇧",
      keywords = Seq("grade-up"),
      arity = 1,
      Options(),
      Overload(
        name = "Grade Up",
        args = Seq("lst"),
        description = "Indices that would sort #1",
      ),
    ),
    AddElement(
      symbol = "⇩",
      keywords = Seq("grade-down"),
      arity = 1,
      Options(),
      Overload(
        name = "Grade Down",
        args = Seq("lst"),
        description = "Indices that would sort #1 in reverse",
      ),
    ),
    AddElement(
      symbol = "∪",
      keywords = Seq("union", "set-union"),
      arity = 2,
      Options(),
      Overload(
        name = "Union",
        args = Seq("lst", "lst"),
        description = "Union of #1 and #2",
      ),
    ),
    AddElement(
      symbol = "∩",
      keywords = Seq("intersection", "set-intersection"),
      arity = 2,
      Options(),
      Overload(
        name = "Intersection",
        args = Seq("lst", "lst"),
        description = "Intersection of #1 and #2",
      ),
    ),
    AddElement(
      symbol = "⊍",
      keywords = Seq("set-xor"),
      arity = 2,
      Options(),
      Overload(
        name = "Set XOR",
        args = Seq("lst", "lst"),
        description = "Set XOR of #1 and #2",
      ),
    ),
    AddElement(
      symbol = "⦰",
      keywords = Seq("set-difference", "set-diff"),
      arity = 2,
      Options(),
      Overload(
        name = "Set Difference",
        args = Seq("lst", "lst"),
        description = "Set difference of #1 and #2",
      ),
    ),
    AddElement(
      symbol = "«",
      keywords = Seq("left-shift", "<<", "left-pad"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Left Shift",
        args = Seq("num", "num"),
        description = "#1 << #2",
      ),
      Overload(
        name = "Prepend Spaces to Given Length",
        args = Seq("str", "num"),
        description =
          "Prepend spaces to string {#1|#2} until it is {#2|#1} characters long",
        typeSwitchable = true,
      ),
      Overload(
        name = "Prepend Spaces to Length of Second String",
        args = Seq("str", "str"),
        description = "Prepend spaces to string #1 until it is the length of #2",
      ),
    ),
    AddElement(
      symbol = "»",
      keywords = Seq("right-shift", ">>", "right-pad"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Right Shift",
        args = Seq("num", "num"),
        description = "#1 >> #2",
      ),
      Overload(
        name = "Append Spaces to Given Length",
        args = Seq("str", "num"),
        description =
          "Append spaces to string {#1|#2} until it is {#2|#1} characters long",
        typeSwitchable = true,
      ),
      Overload(
        name = "Append Spaces to Length of Second String",
        args = Seq("str", "str"),
        description = "Append spaces to string #1 until it is the length of #2",
      ),
    ),
    AddElement(
      symbol = "Ɠ",
      keywords = Seq("max-peek"),
      arity = 1,
      Options(peeks = true),
      Overload(
        name = "Max Peek",
        args = Seq("lst"),
        description = "Maximum of #1 without popping",
      ),
    ),
    AddElement(
      symbol = "ɠ",
      keywords = Seq("min-peek"),
      arity = 1,
      Options(peeks = true),
      Overload(
        name = "Min Peek",
        args = Seq("lst"),
        description = "Minimum of #1 without popping",
      ),
    ),
    AddElement(
      symbol = "Ġ",
      keywords = Seq("zip-max", "max-dyad", "max-ab", "gen"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Zipped Maximum",
        args = Seq("lst", "lst"),
        description = "Maximum of corresponding elements of #1 and #2",
      ),
      Overload(
        name = "Vectorised Maximum",
        args = Seq("lst", "scl"),
        description = "Maximum of {#2|#1} and {#1|#2}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Dyadic Maximum",
        args = Seq("scl", "scl"),
        description = "Maximum of #1 and #2",
      ),
      Overload(
        name = "Generate Sequence",
        args = Seq("nls", "fun"),
        description =
          "Call {#2|#1} on previous results of {#2|#1}, starting with {#1|#2}.",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "ġ",
      keywords = Seq("zip-min", "min-dyad", "min-ab", "dyad-gen"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Zipped Minimum",
        args = Seq("lst", "lst"),
        description = "Minimum of corresponding elements of #1 and #2",
      ),
      Overload(
        name = "Vectorised Minimum",
        args = Seq("lst", "scl"),
        description = "Minimum of {#2|#1} and {#1|#2}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Dyadic Minimum",
        args = Seq("scl", "scl"),
        description = "Minimum of #1 and #2",
      ),
      Overload(
        name = "Generate Sequence",
        args = Seq("nls", "fun"),
        description =
          "Call #2 as a dyad infinitely with items of #1 as starting values",
      ),
    ),
    AddElement(
      symbol = "⌈",
      keywords = Seq("ceil", "ceiling", "split-on-spaces"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Ceiling",
        args = Seq("num"),
        description = "Ceiling of #1",
      ),
      Overload(
        name = "Split on Spaces",
        args = Seq("str"),
        description = "Split #1 by spaces",
      ),
    ),
    AddElement(
      symbol = "⌊",
      keywords = Seq("floor", "str-to-num"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Floor",
        args = Seq("num"),
        description = "Floor of #1",
      ),
      Overload(
        name = "String to Number",
        args = Seq("str"),
        description =
          "Convert #1 to a number, ignoring non-digit characters. Returns 0 if no digits are found",
      ),
    ),
    AddElement(
      symbol = "⊖",
      keywords = Seq("zero-slice", "take", "zero-take"),
      arity = 2,
      Options(),
      Overload(
        name = "0 Slice",
        args = Seq("itr", "num"),
        description = "First {#2|#1} elements of {#1|#2}",
        typeSwitchable = true,
      ),
      Overload(
        name = "APL Style Take",
        args = Seq("lst", "lst[num]"),
        description = "APL style take",
      ),
      Overload(
        name = "Take While True",
        args = Seq("lst", "fun"),
        description = "Take elements from {#1|#2} while {#2|#1} is true",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "⌽",
      keywords = Seq("one-slice", "tail-take", "one-take"),
      arity = 2,
      Options(),
      Overload(
        name = "1 Slice",
        args = Seq("itr", "num"),
        description = "First {#2|#1} elements of {#1|#2}[1:]",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "£",
      keywords = Seq("set-register"),
      arity = 1,
      Options(),
      Overload(
        name = "Set Register",
        args = Seq("any"),
        description = "Set the register to #1",
      ),
    ),
    AddElement(
      symbol = "¥",
      keywords = Seq("get-register", "push-register"),
      arity = 0,
      Options(),
      Overload(
        name = "Get Register",
        args = Seq(),
        description = "Push the register to the stack",
      ),
    ),
    AddElement(
      symbol = "↜",
      keywords = Seq("rotate-stack-left"),
      arity = -1,
      Options(),
      Overload(
        name = "Rotate Stack Left",
        args = Seq(),
        description = "Rotate the stack left",
      ),
    ),
    AddElement(
      symbol = "↝",
      keywords = Seq("rotate-stack-right"),
      arity = -1,
      Options(),
      Overload(
        name = "Rotate Stack Right",
        args = Seq(),
        description = "Rotate the stack right",
      ),
    ),
    AddElement(
      symbol = "↺",
      keywords = Seq("rot-left", "iterate-while-unique"),
      arity = 1,
      Options(),
      Overload(
        name = "Rotate Left",
        args = Seq("lst|str"),
        description = "Rotate #1 left",
      ),
      Overload(
        name = "Rotate Left",
        args = Seq("lst|str", "num"),
        description = "Rotate #1 left #2 times. Right if #2 is negative",
      ),
      Overload(
        name = "Iterate While Unique",
        args = Seq("any", "fun"),
        description =
          "Repeatedly apply #2 to #1 until a result is repeated. Return all results",
      ),
    ),
    AddElement(
      symbol = "↻",
      keywords = Seq("rot-right"),
      arity = 1,
      Options(),
      Overload(
        name = "Rotate Right",
        args = Seq("lst|str"),
        description = "Rotate #1 right",
      ),
      Overload(
        name = "Rotate Right",
        args = Seq("lst|str", "num"),
        description = "Rotate #1 right #2 times. Left if #2 is negative",
      ),
    ),
    AddElement(
      symbol = "≜",
      keywords = Seq("assign", "**>"),
      arity = 3,
      Options(),
      Overload(
        name = "List Assign",
        args = Seq("any", "num", "nsl"),
        description = "#1[#2] = #3",
      ),
      Overload(
        name = "Augmented List Assignment",
        args = Seq("any", "num", "fun"),
        description = "#1[#2] = #3(#1[#2])",
      ),
      Overload(
        name = "Vectorised Augmented List Assignment",
        args = Seq("lst", "lst[num]", "fun"),
        description = "#1[_] = #3(#1[_]) for _ in #2",
      ),
      Overload(
        name = "Zipped Assignment",
        args = Seq("lst", "lst", "lst"),
        description = "#1[ind] = val for ind, val in zip(#2, #3)",
      ),
      Overload(
        name = "Regex String Replacement",
        args = Seq("str", "str", "str"),
        description = "Replace all occurrences of #2 in #1 with #3",
      ),
      Overload(
        name = "Regex Substitution",
        args = Seq("str", "str", "fun"),
        description =
          "Replace all occurrences of #2 in #1 with the result of #3",
      ),
      Overload(
        name = "Object Member Assignment",
        args = Seq("obj", "str", "any"),
        description = "#1.#2 = #3",
      ),
    ),
    AddElement(
      symbol = "⎀",
      keywords = Seq("insert"),
      arity = 3,
      Options(),
      Overload(
        name = "Insert",
        args = Seq("any", "num", "any"),
        description = "Insert #3 into #1 at index #2",
      ),
      Overload(
        name = "Insert",
        args = Seq("any", "lst[num]", "scl"),
        description = "Insert #3 into #1 at indices #2",
      ),
      Overload(
        name = "Insert",
        args = Seq("any", "lst[num]", "lst"),
        description = "Insert items of #3 into #1 at indices #2",
      ),
    ),
    AddElement(
      symbol = "◲",
      keywords = Seq("sublists"),
      arity = 1,
      Options(),
      Overload(
        name = "Sublists",
        args = Seq("any"),
        description = "All sublists of #1",
      ),
    ),
    AddElement(
      symbol = "⊢",
      keywords = Seq("ten-to-base", "all-regex-matches", "to-base"),
      arity = 2,
      Options(),
      Overload(
        name = "10 to Base",
        args = Seq("num", "num"),
        description = "Convert #1 to base #2",
      ),
      Overload(
        name = "10 to Base",
        args = Seq("num", "str|lst"),
        description = "Convert #1 to base len(#2) using the items of #2",
      ),
      Overload(
        name = "10 to Base",
        args = Seq("lst", "num"),
        description = "Convert each item in #1 to base #2",
      ),
      Overload(
        name = "10 to Base",
        args = Seq("lst", "lst"),
        description =
          "Convert each item in #1 to the base of the corresponding item in #2",
      ),
      Overload(
        name = "All Regex Matches",
        args = Seq("str", "str"),
        description = "All matches of #2 in #1",
      ),
    ),
    AddElement(
      symbol = "⊣",
      keywords = Seq("base-to-10", "from-base", "first>n"),
      arity = 2,
      Options(),
      Overload(
        name = "Base to 10",
        args = Seq("scl", "num"),
        description =
          "Convert #1 from base #2 to base 10, assuming a base that is a prefix of [0-9A-Z] for strings",
      ),
      Overload(
        name = "Base to 10",
        args = Seq("lst[num|str]", "num"),
        description =
          "Convert #1 from base #2 to base 10, using the items of #1 as digits",
      ),
      Overload(
        name = "Base to 10",
        args = Seq("lst", "num"),
        description =
          "Convert each item in #1 from base #2 to base 10, assuming a base that is a prefix of [0-9A-Z] for strings",
      ),
      Overload(
        name = "First Number Greater Than Where Function is True",
        args = Seq("fun", "num"),
        description =
          "The first number greater than {#2|#1} where {#1|#2} returns true",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "ɦ",
      keywords = Seq("head-peek"),
      arity = 1,
      Options(peeks = true),
      Overload(
        name = "Head Peek",
        args = Seq("lst"),
        description = "First element of #1 without popping",
      ),
    ),
    AddElement(
      symbol = "ʈ",
      keywords = Seq("tail-peek"),
      arity = 1,
      Options(peeks = true),
      Overload(
        name = "Tail Peek",
        args = Seq("lst"),
        description = "Last element of #1 without popping",
      ),
    ),
    AddElement(
      symbol = "ᐐ",
      keywords = Seq("init", "without-tail", "tailless"),
      arity = 1,
      Options(),
      Overload(
        name = "Init",
        args = Seq("any"),
        description = "All but the last element of #1",
      ),
    ),
    AddElement(
      symbol = "ᐵ",
      keywords = Seq("drop", "fixed-point-headless"),
      arity = 2,
      Options(),
      Overload(
        name = "Drop",
        args = Seq("any", "num"),
        description = "All but the first {#2|#1} elements of {#1|#2}",
        typeSwitchable = true,
      ),
      Overload(
        name = "APL Style Drop",
        args = Seq("lst", "lst[num]"),
        description = "APL style drop",
      ),
      Overload(
        name = "Fixed Point Headless",
        args = Seq("fun", "any"),
        description =
          "Repeatedly apply #1 to #2 until it doesn't change. Do not include the initial value",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "ᐕ",
      keywords = Seq("behead", "without-head", "headless"),
      arity = 1,
      Options(),
      Overload(
        name = "Behead",
        args = Seq("any"),
        description = "All but the first element of #1",
      ),
    ),
    AddElement(
      symbol = "½",
      keywords = Seq("half", "halve"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Halve",
        args = Seq("num"),
        description = "#1 / 2",
      ),
      Overload(
        name = "Two String Halves",
        args = Seq("str"),
        description = "Split #1 in half",
      ),
    ),
    AddElement(
      symbol = "ƶ",
      keywords = Seq("range-to-length"),
      arity = 1,
      Options(),
      Overload(
        name = "Range to Length",
        args = Seq("lst"),
        description = "Range from 0 to len(#1) - 1",
      ),
    ),
    AddElement(
      symbol = "ÞƵ",
      keywords = Seq("range-to-length-1"),
      arity = 1,
      Options(),
      Overload(
        name = "Range to Length 1",
        args = Seq("lst"),
        description = "Range from 1 to len(#1)",
      ),
    ),
    AddElement(
      symbol = "⁰",
      keywords = Seq("first-input", "input-0"),
      arity = 0,
      Options(),
      Overload(
        name = "First Input",
        args = Seq(),
        description = "Push the first input to the stack",
      ),
    ),
    AddElement(
      symbol = "¹",
      keywords = Seq("second-input", "input-1"),
      arity = 0,
      Options(),
      Overload(
        name = "Second Input",
        args = Seq(),
        description = "Push the second input to the stack",
      ),
    ),
    AddElement(
      symbol = "²",
      keywords = Seq("square", "string-pairs"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Square",
        args = Seq("num"),
        description = "#1 ** 2",
      ),
      Overload(
        name = "String Pairs",
        args = Seq("str"),
        description = "Split #1 into pairs of characters",
      ),
    ),
    AddElement(
      symbol = "³",
      keywords = Seq("cube", "string-triples"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Cube",
        args = Seq("num"),
        description = "#1 ** 3",
      ),
      Overload(
        name = "String Triples",
        args = Seq("str"),
        description = "Split #1 into triples of characters",
      ),
    ),
    AddElement(
      symbol = "⅟",
      keywords = Seq(
        "reciprocal",
        "inverse",
        "1/",
        "without-whitespace",
        "no-space",
        "spaceless",
        "remove-whitespace",
      ),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Reciprocal",
        args = Seq("num"),
        description = "1 / #1",
      ),
      Overload(
        name = "Without Whitespace",
        args = Seq("str"),
        description = "Remove all whitespace from #1",
      ),
    ),
    AddElement(
      symbol = "※",
      keywords = Seq("group-by-consecutive"),
      arity = 1,
      Options(),
      Overload(
        name = "Group by Consecutive",
        args = Seq("lst"),
        description = "Group consecutive equal elements of #1",
      ),
      Overload(
        name = "Group Consecutive by Function",
        args = Seq("lst", "fun"),
        description = "Group elements of #1 by function #2",
      ),
    ),
    AddElement(
      symbol = "⇄",
      keywords = Seq("reverse"),
      arity = 1,
      Options(),
      Overload(
        name = "Reverse",
        args = Seq("any"),
        description = "Reverse #1",
      ),
    ),
    AddElement(
      symbol = "⧖",
      keywords = Seq("permutations", "map-over-permutations", "map-perms"),
      arity = 1,
      Options(),
      Overload(
        name = "Permutations",
        args = Seq("any"),
        description = "All permutations of #1",
      ),
      Overload(
        name = "Map Over Permutations",
        args = Seq("any", "fun"),
        description = "Map #2 over all permutations of #1",
      ),
    ),
    AddElement(
      symbol = "‰",
      keywords = Seq("divmod", "flatmap"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Divmod",
        args = Seq("num", "num"),
        description = "Divmod of #1 and #2 ([#1 // #2, #1 % #2])",
      ),
      Overload(
        name = "Flatmap",
        args = Seq("lst", "fun"),
        description = "Flatmap {#2|#1} over {#1|#2}",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "≛",
      keywords = Seq("divides?", "append-spaces", "regex-span"),
      arity = 2,
      Options(),
      Overload(
        name = "Divides?",
        args = Seq("num", "num"),
        description = "#2 % #1 == 0",
      ),
      Overload(
        name = "Append Spaces",
        args = Seq("str", "num"),
        description = "Append {#2|#1} spaces to {#1|#2}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Regex Span",
        args = Seq("str", "str"),
        description = "Span of regex match of pattern #2 in #1",
      ),
    ),
    AddElement(
      symbol = "ℭ",
      keywords = Seq("combinations-with-replacement"),
      arity = 2,
      Options(),
      Overload(
        name = "Combinations with Replacement",
        args = Seq("itr", "num"),
        description =
          "All combinations of {#1|#2} of length {#2|#1} with replacement",
        typeSwitchable = true,
      ),
      Overload(
        name = "Combinations of Range with Replacement",
        args = Seq("num", "num"),
        description =
          "All combinations of range(#1) of length #2 with replacement",
      ),
    ),
    AddElement(
      symbol = "℈",
      keywords = Seq("combinations-without-replacement"),
      arity = 2,
      Options(),
      Overload(
        name = "Combinations without Replacement",
        args = Seq("itr", "num"),
        description =
          "All combinations of {#1|#2} of length {#2|#1} without replacement",
        typeSwitchable = true,
      ),
      Overload(
        name = "Combinations of Range without Replacement",
        args = Seq("num", "num"),
        description =
          "All combinations of range(#1) of length #2 without replacement",
      ),
    ),
    AddElement(
      symbol = "⦷",
      keywords = Seq("abs", "absolute-value", "keep-letters", "first>0"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Absolute Value",
        args = Seq("num"),
        description = "Absolute value of #1",
      ),
      Overload(
        name = "Keep Letters",
        args = Seq("str"),
        description = "Keep only the letters of #1",
      ),
      Overload(
        name = "First Positive Integer Where Function is True",
        args = Seq("fun"),
        description = "First positive integer where #1 is true (>= 1)",
      ),
    ),
    AddElement(
      symbol = "Ϣ",
      keywords = Seq("chunk-to-length", "partition-to-length", "first-n-true"),
      arity = 2,
      Options(),
      Overload(
        name = "Chunk to Length",
        args = Seq("any", "num"),
        description = "Chunk {#1|#2} into parts of length {#2|#1}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Partition to Lengths",
        args = Seq("itr", "lst[num]"),
        description = "Partition #1 into parts of lengths #2",
      ),
      Overload(
        name = "First N Integers Where Function is True",
        args = Seq("num", "fun"),
        description = "First {#1|#2} integers where function {#2|#1} is true",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "≤",
      keywords = Seq("less-than-or-equal", "lte", "<=", "min-by"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Less Than or Equal",
        args = Seq("scl", "scl"),
        description = "#1 <= #2",
      ),
      Overload(
        name = "Min By",
        args = Seq("nsl", "fun"),
        description = "Minimum of list({#1|#2}) by function {#2|#1}",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "≥",
      keywords = Seq("greater-than-or-equal", "gte", ">=", "max-by"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Greater Than or Equal",
        args = Seq("scl", "scl"),
        description = "#1 >= #2",
      ),
      Overload(
        name = "Max By",
        args = Seq("nsl", "fun"),
        description = "Maximum of list({#1|#2}) by function {#2|#1}",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "≠",
      keywords = Seq("not-equal", "neq", "!=", "=n't", "eqn't", "equaln't"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Not Equal",
        args = Seq("scl", "scl"),
        description = "str(#1) != str(#2)",
      ),
    ),
    AddElement(
      symbol = "≡",
      keywords = Seq("exact-equals", "eq+", "==="),
      arity = 2,
      Options(),
      Overload(
        name = "Equals",
        args = Seq("any", "any"),
        description = "Does #1 exactly equal #2",
      ),
    ),
    AddElement(
      symbol = "•",
      keywords = Seq(
        "dot-product",
        "string-repeat-concat",
        "bijective-base",
        "first-predicate-index",
      ),
      arity = 2,
      Options(),
      Overload(
        name = "Dot Product",
        args = Seq("lst", "lst"),
        description = "Dot product of #1 and #2",
      ),
      Overload(
        name = "String-repeat concatenate",
        args = Seq("lst[num]", "lst[str]"),
        description =
          "Repeat each string in #2 #1[i] times, then concatenate the result",
      ),
      Overload(
        name = "Bijective Base Conversion",
        args = Seq("num", "num"),
        description = " Convert #1 to bijective base #2",
      ),
      Overload(
        name = "First Index Where Predicate True",
        args = Seq("nsl", "fun"),
        description =
          "Index of the first value in {#1|#2} where function {#2|#1} is true",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "±",
      keywords = Seq("signum", "case-of", "case", "sign"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Signum",
        args = Seq("num"),
        description = "Sign of #1 (1 if positive, 0 if 0, -1 if negative)",
      ),
      Overload(
        name = "Case of",
        args = Seq("str"),
        description = "Case of #1 (1 if uppercase, 0 if lowercase, -1 if mixed)",
      ),
    ),
    AddElement(
      symbol = "†",
      keywords = Seq("lengths-of-consecutives"),
      arity = 1,
      Options(),
      Overload(
        name = "Lengths of Consecutives",
        args = Seq("lst"),
        description = "Lengths of consecutive runs of equal elements in #1",
      ),
    ),
    AddElement(
      symbol = "⎙",
      keywords = Seq("peek-print"),
      arity = 1,
      Options(peeks = true),
      Overload(
        name = "Peek Print",
        args = Seq("any"),
        description = "Print #1 without popping",
      ),
    ),
    AddElement(
      symbol = "#,",
      keywords = Seq("print"),
      arity = 1,
      Options(),
      Overload(
        name = "Print",
        args = Seq("any"),
        description = "Print #1 without a trailing newline",
      ),
    ),
    AddElement(
      symbol = "≓",
      keywords = Seq("mirror"),
      arity = 1,
      Options(),
      Overload(
        name = "Mirror",
        args = Seq("any"),
        description = "Mirror #1 (#1 + reverse(#1)), as the original type",
      ),
    ),
    AddElement(
      symbol = "Ͼ",
      keywords = Seq("vectorised-sums", "v/+"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Vectorised Sums",
        args = Seq("lst"),
        description = "Sum of each item in #1. Functionally equivalent to `¨∑`",
      ),
    ),
    AddElement(
      symbol = "ᴥ",
      keywords = Seq("exec", "ten-power", "call", "@"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "10 to the Power of",
        args = Seq("num"),
        description = "10 ** #1",
      ),
      Overload(
        name = "Execute",
        args = Seq("str"),
        description = "Execute #1 as Vyxal code",
      ),
      Overload(
        name = "Call Function",
        args = Seq("fun"),
        description = "Call function #1",
      ),
    ),
    AddElement(
      symbol = "#ᴥ",
      keywords = Seq("valid-exec", "valid-code"),
      arity = 1,
      Options(),
      Overload(
        name = "Valid Vyxal Code",
        args = Seq("str"),
        description = "returns 0 if exec'd code errors, 1 if successful",
      ),
    ),
    AddElement(
      symbol = "ℳ",
      keywords = Seq("modular", "matrix-multiply", "regex-full-match?"),
      arity = 2,
      Options(),
      Overload(
        name = "Every Nth Element",
        args = Seq("itr", "num"),
        description = "Every {#2|#1}th element of {#1|#2}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Matrix Multiply",
        args = Seq("lst", "lst"),
        description = "Matrix multiply #1 and #2",
      ),
      Overload(
        name = "Regex Full Match?",
        args = Seq("str", "str"),
        description = "Does pattern #2 fully match #1",
      ),
    ),
    AddElement(
      symbol = "℗",
      keywords = Seq("is-prime", "prime?", "quine-cheese"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Is Prime",
        args = Seq("num"),
        description = "Is #1 a prime number?",
      ),
      Overload(
        name = "Quine Cheese",
        args = Seq("str"),
        description =
          "Quotify #1 and prepend it to #1. (Useful for quines like `\"℗\"℗`)",
      ),
    ),
    AddElement(
      symbol = "⍢",
      keywords = Seq("parity", "bit", "last-half"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Parity",
        args = Seq("num"),
        description = "Parity of #1 (1 if odd, 0 if even) --> #1 % 2",
      ),
      Overload(
        name = "Last String Half",
        args = Seq("str"),
        description = "Last half of #1",
      ),
    ),
    AddElement(
      symbol = "ℂ",
      keywords = Seq("ncr", "choose", "characters-same?", "fixpoint"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "NCR / N Choose R",
        args = Seq("num", "num"),
        description = "nCr of #1 and #2 (n choose r)",
      ),
      Overload(
        name = "Characters Same?",
        args = Seq("str", "str"),
        description = "Are all characters in #1 the same as #2?",
      ),
      Overload(
        name = "Fixpoint",
        args = Seq("fun", "any"),
        description =
          "Repeatedly apply {#1|#2} on {#2|#1} until a fixed point is reached",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "⌹",
      keywords = Seq("list-partitions", "integer-partitions"),
      arity = 1,
      Options(),
      Overload(
        name = "Integers Partitions",
        args = Seq("num"),
        description = "All possible ways to sum positive integers to #1",
      ),
      Overload(
        name = "List Partitions",
        args = Seq("itr"),
        description = "All possible ways to partition #1 into sublists",
      ),
    ),
    AddElement(
      symbol = "⏚",
      keywords = Seq("powerset", "vectorise-function"),
      arity = 1,
      Options(),
      Overload(
        name = "Powerset",
        args = Seq("nsl"),
        description = "Powerset of #1",
      ),
      Overload(
        name = "Vectorise",
        args = Seq("fun"),
        description = "Apply #1 as if it were a pervasive element",
      ),
    ),
    AddElement(
      symbol = "↯",
      keywords =
        Seq("inclusive-range", "sort-by", "regex-split-keep-delimiters"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Inclusive Range",
        args = Seq("num", "num"),
        description = "Inclusive range from #1 to #2",
      ),
      Overload(
        name = "Sort By",
        args = Seq("nsl", "fun"),
        description = "Sort list {#1|#2} (range if num) by function {#2|#1}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Regex Split Keep Delimiters",
        args = Seq("str", "str"),
        description = "Split #1 by regex #2, keeping the delimiters",
      ),
    ),
    AddElement(
      symbol = "⊠",
      keywords = Seq("cartesian-power", "regex-index"),
      arity = 2,
      Options(),
      Overload(
        name = "Cartesian Power",
        args = Seq("any", "num"),
        description = "Cartesian power of {#1|#2} to the power of {#2|#1}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Regex Index",
        args = Seq("str", "str"),
        description =
          "Return first index of pattern match #2 in target string #1, -1 if not found",
      ),
      Overload(
        name = "Self-Cartesian Power",
        args = Seq("itr", "any"),
        description =
          "Push #1, and then push the cartesian product of #2 with itself",
      ),
    ),
    AddElement(
      symbol = "⚅",
      keywords = Seq("random-choice", "random-element", "randint", "random"),
      arity = 1,
      Options(),
      Overload(
        name = "Random Choice",
        args = Seq("itr"),
        description = "Random element of #1",
      ),
      Overload(
        name = "Random Integer",
        args = Seq("num"),
        description = "Random integer from 0 to #1",
      ),
    ),
    AddElement(
      symbol = "æ",
      keywords = Seq(
        "bifuricate",
        "bifur",
        "bif",
        "furry",
        "uwu",
        "dup-rev",
        "dup-reverse",
        "owo",
        "peek-function",
        "peek-call",
        "@@",
      ),
      arity = 1,
      Options(),
      Overload(
        name = "Bifurcate",
        args = Seq("any"),
        description = "Duplicate #1 and reverse the duplicate",
      ),
      Overload(
        name = "Call Function Without Popping",
        args = Seq("fun"),
        description = "Call #1 without popping its arguments",
      ),
    ),
    AddElement(
      symbol = "␣",
      keywords = Seq("space"),
      arity = 0,
      Options(),
      Overload(
        name = "Space",
        args = Seq(),
        description = "Push a space to the stack",
      ),
    ),
    AddElement(
      symbol = "¶",
      keywords = Seq("newline"),
      arity = 0,
      Options(),
      Overload(
        name = "Newline",
        args = Seq(),
        description = "Push a newline to the stack",
      ),
    ),
    AddElement(
      symbol = "★",
      keywords = Seq("asterisk", "star"),
      arity = 0,
      Options(),
      Overload(
        name = "Asterisk",
        args = Seq(),
        description = "Push an asterisk to the stack",
      ),
    ),
    AddElement(
      symbol = "ᑂ",
      keywords = Seq("headless-top", "head-extract-under"),
      arity = 1,
      Options(),
      Overload(
        name = "Head on Top, Rest on Bottom",
        args = Seq("any"),
        description = "Push #1[1:] and #1[0]",
      ),
      Overload(
        name = "Range [2, n]",
        args = Seq("num"),
        description = "Range from 2 to #1 inclusive",
      ),
    ),
    AddElement(
      symbol = "∻",
      keywords = Seq("integer-divide", "int-div", "//"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Integer Divide",
        args = Seq("num", "num"),
        description = "#1 // #2",
      ),
    ),
    AddElement(
      symbol = "√",
      keywords = Seq("square-root", "sqrt", "palindromise"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Square Root",
        args = Seq("num"),
        description = "Square root of #1",
      ),
      Overload(
        name = "Palindromise String",
        args = Seq("str"),
        description = "Palindromise #1",
      ),
    ),
    AddElement(
      symbol = "⍰",
      keywords = Seq("truthy?"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Truthy?",
        args = Seq("scl"),
        description = "Is #1 truthy? (Not 0, empty, or false)",
      ),
    ),
    AddElement(
      symbol = "◌",
      keywords = Seq("round", "lowercase?", "is-lowercase"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Round",
        args = Seq("num"),
        description = "Round #1 to the nearest integer, half-up",
      ),
      Overload(
        name = "Is Lowercase",
        args = Seq("str"),
        description =
          "Check if #1 is lowercase. With string.len > 1, vectorises over each character",
      ),
    ),
    AddElement(
      symbol = "δ",
      keywords = Seq("deltas", "differences"),
      arity = 1,
      Options(),
      Overload(
        name = "Deltas",
        args = Seq("lst"),
        description =
          "Deltas/forward differences of #1 - [a - b, b - c, c - d, ...]",
      ),
    ),
    AddElement(
      symbol = "☷",
      keywords = Seq("partition-after-truthy", "group-by"),
      arity = 2,
      Options(),
      Overload(
        name = "Partition After Truthy",
        args = Seq("lst", "lst"),
        description = " Partition #1 after truthy indices of #2.",
      ),
      Overload(
        name = "Group By",
        args = Seq("lst", "fun"),
        description = "Group elements of #1 by function #2",
      ),
    ),
    AddElement(
      symbol = "Þ⎶",
      keywords = Seq("edges", "ends", "real-imaginary"),
      arity = 1,
      Options(),
      Overload(
        name = "Edges",
        args = Seq("itr"),
        description = "First and last element of #1",
      ),
      Overload(
        name = "Real and Imaginary",
        args = Seq("num"),
        description = "Real and imaginary parts of #1",
      ),
    ),
    AddElement(
      symbol = "⎶",
      keywords = Seq("trim"),
      arity = 2,
      Options(),
      Overload(
        name = "Trim",
        args = Seq("any", "any"),
        description = "Trim #1 of leading and trailing #2",
      ),
    ),
    AddElement(
      symbol = "⊆",
      keywords = Seq("subset?"),
      arity = 2,
      Options(),
      Overload(
        name = "Subset?",
        args = Seq("lst", "lst"),
        description =
          "Is the shallower list a subset of the deeper list? Checks windows corresponding to the length of the shallower list",
      ),
    ),
    AddElement(
      symbol = "⍨",
      keywords = Seq("dump"),
      arity = 1,
      Options(),
      Overload(
        name = "Dump",
        args = Seq("any"),
        description = "Push all items of #1 to the stack",
      ),
    ),
    AddElement(
      symbol = "γ",
      keywords = Seq("wrap-len-2", "pairs"),
      arity = 1,
      Options(),
      Overload(
        name = "Wrap to Length 2",
        args = Seq("any"),
        description = "Wrap #1 into chunks of length 2",
      ),
    ),
    AddElement(
      symbol = "⎘",
      keywords =
        Seq("flatten-by-depth", "flatten-depth", "one-flatten"),
      arity = 2,
      Options(),
      Overload(
        name = "Flatten by Depth",
        args = Seq("lst", "num"),
        description = "Flatten #1 by #2 levels",
      ),
      Overload(
        name = "Flatten by Depth",
        args = Seq("lst"),
        description = "Flatten #1 by 1 level",
      ),
      Overload(
        name = "Flatten-each",
        args = Seq("lst[num|str]"),
        description = "Flattens each item in #1",
      ),
    ),
    AddElement(
      symbol = "ꜝ",
      keywords = Seq("keep-truthy"),
      arity = 1,
      Options(),
      Overload(
        name = "Keep Truthy",
        args = Seq("lst"),
        description = "Keep only the truthy elements of #1",
      ),
    ),
    AddElement(
      symbol = "≈",
      keywords = Seq("all-same"),
      arity = 1,
      Options(),
      Overload(
        name = "All Same",
        args = Seq("any"),
        description = "Are all elements of #1 the same?",
      ),
    ),
    AddElement(
      symbol = "≊",
      keywords = Seq("all-equal-item", "all-equal-to"),
      arity = 2,
      Options(),
      Overload(
        name = "All Equal Item",
        args = Seq("lst", "any"),
        description = "Are all elements of #1 equal to #2?",
      ),
    ),
    AddElement(
      symbol = "κ",
      keywords = Seq("gcd"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "GCD",
        args = Seq("num", "num"),
        description = "GCD of #1 and #2",
      ),
      Overload(
        name = "GCD of List",
        args = Seq("lst"),
        description = "GCD of all elements of #1",
      ),
      Overload(
        name = "GCD of List with Initial Value",
        args = Seq("lst", "num"),
        description = "GCD of all elements of #1.append(#2)",
      ),
    ),
    AddElement(
      symbol = "#↸",
      keywords = Seq("retrieve-from-outer"),
      arity = 1,
      Options(),
      Overload(
        name = "Retrieve Item at Index from Outer Stack",
        args = Seq("num"),
        description =
          "Retrieve the item at index #1 from the outer stack, current stack if at top level",
      ),
      Overload(
        name = "Retrieve Item at Index from Outer Stack N-Layers Up",
        args = Seq("lst[num, num]"),
        description =
          "Retrieve the item at index #2 from the stack #1 levels up",
      ),
    ),
    AddElement(
      symbol = "⤻",
      keywords = Seq("over"),
      arity = -1,
      Options(),
      Overload(
        name = "Over",
        args = Seq(),
        description =
          "Duplicate the item below the top of the stack -> #2 #1 #2",
      ),
    ),
    AddElement(
      symbol = "⤺",
      keywords = Seq("around"),
      arity = -1,
      Options(),
      Overload(
        name = "Around",
        args = Seq(),
        description =
          "Duplicate the top of the stack around the item below the top of the stack -> #1 #2 #1",
      ),
    ),
    AddElement(
      symbol = "↸",
      keywords = Seq("shift", "bread"),
      arity = 3,
      Options(),
      Overload(
        name = "Shift",
        args = Seq("any", "any", "any"),
        description = "Move the top of the stack down two places",
      ),
    ),
    AddElement(
      symbol = "”",
      keywords = Seq("join-on-newlines", "*newline", "one?->n"),
      arity = 1,
      Options(),
      Overload(
        name = "Join on Newlines",
        args = Seq("lst"),
        description = "Join #1 on newlines",
      ),
      Overload(
        name = "Push Context Variable N if 1",
        args = Seq("num"),
        description = "Push the context variable N if #1 is 1",
      ),
    ),
    AddElement(
      symbol = "„",
      keywords = Seq(
        "join-on-spaces",
        "*space",
        "<0",
        "is-negative?",
        "intersperse-spaces",
      ),
      arity = 1,
      Options(),
      Overload(
        name = "Join on Spaces",
        args = Seq("lst"),
        description = "Join #1 on spaces",
      ),
      Overload(
        name = "Is negative?",
        args = Seq("num"),
        description = "Push 1 if #1 < 0, 0 otherwise",
      ),
      Overload(
        name = "Intersperse #1 with spaces",
        args = Seq("str"),
        description = "Insert spaces between each character of #1",
      ),
    ),
    AddElement(
      symbol = "“",
      keywords = Seq(
        "join-on-empty-string",
        "join-on-nothing",
        "*empty",
        "is-alphanumeric?",
        "insignificant?",
        "first-positive-integer",
        "first-n>0",
      ),
      arity = 1,
      Options(),
      Overload(
        name = "Join on Empty String",
        args = Seq("lst"),
        description = "Join #1 on the empty string",
      ),
      Overload(
        name = "Is alphanumeric?",
        args = Seq("str"),
        description = "Push 1 if #1 is alphanumeric, 0 otherwise",
      ),
      Overload(
        name = "First Positive Integer Where Function is Truthy",
        args = Seq("fun"),
        description = "Push the first positive integer where #1 is truthy",
      ),
      Overload(
        name = "Is Insignificant?",
        args = Seq("num"),
        description = "abs(#1) <= 1",
      ),
    ),
    AddElement(
      symbol = "⧢",
      keywords =
        Seq("into-n-pieces", "split-into-n-pieces", "fixpoint-collect-tail"),
      arity = 2,
      Options(vectorises = false),
      Overload(
        name = "Split Into N Pieces",
        args = Seq("itr", "num"),
        description = "Split {#1|#2} into {#2|#1} pieces",
        typeSwitchable = true,
      ),
      Overload(
        name = "Split Number Into N Pieces",
        args = Seq("itr", "num"),
        description = "Listify {#1|#2} and split it into {#2|#1} pieces",
        typeSwitchable = true,
      ),
      Overload(
        name = "Fixpoint Unfold Without Initial Value",
        args = Seq("fun", "any"),
        description =
          "Repeatedly apply {#1|#2} to {#2|#1}, collecting results (not including initial value)",
        typeSwitchable = true,
      ),
    ),
    AddElement(
      symbol = "▲",
      keywords = Seq("mask"),
      arity = 2,
      Options(),
      Overload(
        name = "Mask",
        args = Seq("any", "any"),
        description =
          "Keep elements of #1 where the corresponding element of #2 is truthy",
      ),
    ),
    AddElement(
      symbol = "Ṭ",
      keywords = Seq("truthy-indexes", "truthy-indices"),
      arity = 1,
      Options(),
      Overload(
        name = "Truthy Indexes",
        args = Seq("lst"),
        description = "Indexes of truthy elements in #1",
      ),
    ),
    AddElement(
      symbol = "Ṫ",
      keywords = Seq("untruth"),
      arity = 1,
      Options(),
      Overload(
        name = "Untruth",
        args = Seq("lst"),
        description = "Create a list of 1s at indices in #1, 0s elsewhere",
      ),
    ),
    AddElement(
      symbol = "Ŀ",
      keywords = Seq("vlen", "lengths"),
      arity = 1,
      Options(),
      Overload(
        name = "Vectorised Lengths",
        args = Seq("lst"),
        description = "Length of each element in #1",
      ),
    ),
    AddElement(
      symbol = "Ł",
      keywords = Seq("length-peek"),
      arity = 1,
      Options(peeks = true),
      Overload(
        name = "Length Peek",
        args = Seq("any"),
        description = "Push the length of #1 without popping",
      ),
    ),
    AddElement(
      symbol = "ḧ",
      keywords = Seq("heads", "head-each", "vec-head"),
      arity = 1,
      Options(),
      Overload(
        name = "Heads",
        args = Seq("lst"),
        description = "First element of each element in #1",
      ),
    ),
    AddElement(
      symbol = "¤",
      keywords = Seq("stringify", "to-str", "str"),
      arity = 1,
      Options(),
      Overload(
        name = "Stringify",
        args = Seq("any"),
        description = "Stringify #1",
      ),
    ),
    AddElement(
      symbol = "①",
      keywords = Seq("10", "ten"),
      arity = 0,
      Options(),
      Overload(
        name = "10",
        args = Seq(),
        description = "Push 10 to the stack",
      ),
    ),
    AddElement(
      symbol = "②",
      keywords = Seq("16", "sixteen"),
      arity = 0,
      Options(),
      Overload(
        name = "16",
        args = Seq(),
        description = "Push 16 to the stack",
      ),
    ),
    AddElement(
      symbol = "③",
      keywords = Seq("32", "thirty-two"),
      arity = 0,
      Options(),
      Overload(
        name = "32",
        args = Seq(),
        description = "Push 32 to the stack",
      ),
    ),
    AddElement(
      symbol = "④",
      keywords = Seq("64", "sixty-four"),
      arity = 0,
      Options(),
      Overload(
        name = "64",
        args = Seq(),
        description = "Push 64 to the stack",
      ),
    ),
    AddElement(
      symbol = "⑤",
      keywords = Seq("100", "one-hundred", "hundred"),
      arity = 0,
      Options(),
      Overload(
        name = "100",
        args = Seq(),
        description = "Push 100 to the stack",
      ),
    ),
    AddElement(
      symbol = "⑥",
      keywords = Seq("128", "one-twenty-eight", "one-hundred-twenty-eight"),
      arity = 0,
      Options(),
      Overload(
        name = "128",
        args = Seq(),
        description = "Push 128 to the stack",
      ),
    ),
    AddElement(
      symbol = "⑦",
      keywords = Seq("256", "two-fifty-six", "two-five-six", "pacman-number"),
      arity = 0,
      Options(),
      Overload(
        name = "256",
        args = Seq(),
        description = "Push 256 to the stack",
      ),
    ),
    AddElement(
      symbol = "⑧",
      keywords = Seq("-1", "minus-one", "negative-one"),
      arity = 0,
      Options(),
      Overload(
        name = "-1",
        args = Seq(),
        description = "Push -1 to the stack",
      ),
    ),
    AddElement(
      symbol = "kæ",
      keywords = Seq("&ALL-PRIMES", "&PRIMES"),
      arity = 0,
      Options(),
      Overload(
        name = "All Primes",
        args = Seq(),
        description = "Push a list of every prime number to the stack",
      ),
    ),
    AddElement(
      symbol = "k+",
      keywords = Seq("-1~1", "neg-one-one", "NW", "northwest"),
      arity = 0,
      Options(vectorises = false),
      Overload(
        name = "[-1, 1]",
        args = Seq(),
        description = "Push the list [-1, 1] to the stack",
      ),
    ),
    AddElement(
      symbol = "k-",
      keywords = Seq("1~-1", "one-neg-one", "southeast", "SE"),
      arity = 0,
      Options(vectorises = false),
      Overload(
        name = "[1, -1]",
        args = Seq(),
        description = "Push the list [1, -1] to the stack",
      ),
    ),
    AddElement(
      symbol = "k≈",
      keywords = Seq("0~1", "zero-one", "north"),
      arity = 0,
      Options(vectorises = false),
      Overload(
        name = "[0, 1]",
        args = Seq(),
        description = "Push the list [0, 1] to the stack",
      ),
    ),
    AddElement(
      symbol = "k±",
      keywords = Seq("1~1", "one-one", "NE", "northeast"),
      arity = 0,
      Options(vectorises = false),
      Overload(
        name = "[1, 1]",
        args = Seq(),
        description = "Push the list [1, 1] to the stack",
      ),
    ),
    AddElement(
      symbol = "k=",
      keywords = Seq("zero-vector", "zero-zero"),
      arity = 0,
      Options(vectorises = false),
      Overload(
        name = "[0,0]",
        args = Seq(),
        description = "Push the list [0,0] to the stack",
      ),
    ),
    AddElement(
      symbol = "k≡",
      keywords = Seq("num-signs", "neg-one-zero-one"),
      arity = 0,
      Options(vectorises = false),
      Overload(
        name = "[-1,0,1]",
        args = Seq(),
        description = "Push the list [-1,0,1] to the stack",
      ),
    ),
    AddElement(
      symbol = "k0",
      keywords = Seq("360", "three-sixty"),
      arity = 0,
      Options(),
      Overload(
        name = "360",
        args = Seq(),
        description = "Push 360 to the stack",
      ),
    ),
    AddElement(
      symbol = "k1",
      keywords = Seq("1000", "one-thousand", "thousand"),
      arity = 0,
      Options(),
      Overload(
        name = "1000",
        args = Seq(),
        description = "Push 1000 to the stack",
      ),
    ),
    AddElement(
      symbol = "k2",
      keywords = Seq("10000", "ten-thousand"),
      arity = 0,
      Options(),
      Overload(
        name = "10000",
        args = Seq(),
        description = "Push 10000 to the stack",
      ),
    ),
    AddElement(
      symbol = "k3",
      keywords = Seq("100000", "hundred-thousand"),
      arity = 0,
      Options(),
      Overload(
        name = "100000",
        args = Seq(),
        description = "Push 100000 to the stack",
      ),
    ),
    AddElement(
      symbol = "k4",
      keywords = Seq("1000000", "million"),
      arity = 0,
      Options(),
      Overload(
        name = "1000000",
        args = Seq(),
        description = "Push 1000000 to the stack",
      ),
    ),
    AddElement(
      symbol = "k5",
      keywords = Seq("4294967296", "b32"),
      arity = 0,
      Options(),
      Overload(
        name = "4294967296",
        args = Seq(),
        description = "Push 4294967296 to the stack",
      ),
    ),
    AddElement(
      symbol = "k6",
      keywords = Seq("&HEX-DIGITS"),
      arity = 0,
      Options(),
      Overload(
        name = "Hex Digits",
        args = Seq(),
        description = "Push \"0123456789abcdef\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k9",
      keywords = Seq("&NON-ZERO-DIGITS"),
      arity = 0,
      Options(),
      Overload(
        name = "Non-Zero Digits",
        args = Seq(),
        description = "Push \"123456789\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kA",
      keywords = Seq("&UPPERCASE-LETTERS"),
      arity = 0,
      Options(),
      Overload(
        name = "Uppercase Letters",
        args = Seq(),
        description = "Push \"ABCDEFGHIJKLMNOPQRSTUVWXYZ\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kB",
      keywords = Seq("&UPPERCASE-LOWERCASE"),
      arity = 0,
      Options(),
      Overload(
        name = "Uppercase and Lowercase",
        args = Seq(),
        description =
          "Push \"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kD",
      keywords = Seq("&LINE-CHARS"),
      arity = 0,
      Options(),
      Overload(
        name = "Line Chars",
        args = Seq(),
        description = "Push \"\\|/-_\" to the stack - useful for drawing lines",
      ),
    ),
    AddElement(
      symbol = "kF",
      keywords = Seq(
        "&FIZZBUZZ"
      ),
      arity = 0,
      Options(),
      Overload(
        name = "Fizzbuzz Constant",
        args = Seq(),
        description = "Push \"FizzBuzz\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kH",
      keywords = Seq("&HELLO-WORLD"),
      arity = 0,
      Options(),
      Overload(
        name = "Hello, World!",
        args = Seq(),
        description = "Push \"Hello, World!\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kL",
      keywords = Seq("&LOWERCASE-UPPERCASE"),
      arity = 0,
      Options(),
      Overload(
        name = "Lowercase and Uppercase",
        args = Seq(),
        description =
          "Push \"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kN",
      keywords = Seq("&NN", "&NATURAL-NUMBERS"),
      arity = 0,
      Options(),
      Overload(
        name = "Natural Numbers",
        args = Seq(),
        description = "Push a list of every natural number to the stack",
      ),
    ),
    AddElement(
      symbol = "kP",
      keywords = Seq("&PRINTABLE-ASCII"),
      arity = 0,
      Options(),
      Overload(
        name = "Printable ASCII",
        args = Seq(),
        description = "Push \"!\" to \"~\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kR",
      keywords = Seq("&DIGITS-UPPERCASE-LOWERCASE"),
      arity = 0,
      Options(),
      Overload(
        name = "Digits, Uppercase, Lowercase",
        args = Seq(),
        description =
          "Push \"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kV",
      keywords = Seq("&UPPERCASE-VOWELS"),
      arity = 0,
      Options(),
      Overload(
        name = "Uppercase Vowels",
        args = Seq(),
        description = "Push \"AEIOU\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kY",
      keywords = Seq("&UPPERCASE-VOWELSY"),
      arity = 0,
      Options(),
      Overload(
        name = "Uppercase Vowels and Y",
        args = Seq(),
        description = "Push \"AEIOUY\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kZ",
      keywords = Seq("&REVERSE-UPPERCASE"),
      arity = 0,
      Options(),
      Overload(
        name = "Reverse Uppercase",
        args = Seq(),
        description = "Push \"ZYXWVUTSRQPONMLKJIHGFEDCBA\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k^",
      keywords = Seq("&UPPERCASE-HEX-DIGITS"),
      arity = 0,
      Options(),
      Overload(
        name = "Uppercase Hex Digits",
        args = Seq(),
        description = "Push \"0123456789ABCDEF\" to the stack",
      ),
    ),
    AddElement(
      symbol = "ka",
      keywords = Seq("&LOWERCASE-LETTERS"),
      arity = 0,
      Options(),
      Overload(
        name = "Lowercase Letters",
        args = Seq(),
        description = "Push \"abcdefghijklmnopqrstuvwxyz\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kd",
      keywords = Seq("&DIGITS"),
      arity = 0,
      Options(),
      Overload(
        name = "Digits",
        args = Seq(),
        description = "Push \"0123456789\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kn",
      keywords = Seq("&DIGIT-ZERO"),
      arity = 0,
      Options(),
      Overload(
        name = "Digits with ending zero",
        args = Seq(),
        description = "Push \"1234567890\" to the stack",
      ),
    ),
    AddElement(
      symbol = "ke",
      keywords = Seq("&E-CONSTANT"),
      arity = 0,
      Options(),
      Overload(
        name = "E Constant",
        args = Seq(),
        description = "Push 2.718281828459045 to the stack",
      ),
    ),
    AddElement(
      symbol = "kg",
      keywords = Seq("&GOLDEN-RATIO", "&PHI"),
      arity = 0,
      Options(),
      Overload(
        name = "Golden Ratio",
        args = Seq(),
        description = "Push 1.618033988749895 to the stack",
      ),
    ),
    AddElement(
      symbol = "kh",
      keywords = Seq("&HELLOWORLD"),
      arity = 0,
      Options(),
      Overload(
        name = "Hello World!",
        args = Seq(),
        description = "Push \"Hello World\" to the stack, no punctuation",
      ),
    ),
    AddElement(
      symbol = "ki",
      keywords = Seq("&PI"),
      arity = 0,
      Options(),
      Overload(
        name = "Pi Constant",
        args = Seq(),
        description = "Push 3.141592653589793 to the stack",
      ),
    ),
    AddElement(
      symbol = "kk",
      keywords = Seq("&RDHW"),
      arity = 0,
      Options(),
      Overload(
        name = "Radiation Hardening Hello World cheese",
        args = Seq(),
        description =
          "Push \"Hello, World!\" to the stack. Useful for radiation hardening hello worlds, because you can submit kkH as your answer.",
      ),
    ),
    AddElement(
      symbol = "kl",
      keywords = Seq("&REVERSE-UPPERCASE-LOWERCASE"),
      arity = 0,
      Options(),
      Overload(
        name = "Reverse Uppercase and Lowercase",
        args = Seq(),
        description =
          "Push \"ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba\" to the stack",
      ),
    ),
    AddElement(
      symbol = "ko",
      keywords = Seq("&OCTAL-DIGITS"),
      arity = 0,
      Options(),
      Overload(
        name = "Octal Digits",
        args = Seq(),
        description = "Push \"01234567\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kp",
      keywords = Seq("&PUNCTUATION"),
      arity = 0,
      Options(),
      Overload(
        name = "Punctuation",
        args = Seq(),
        description = "Push all punctuation characters to the stack",
      ),
    ),
    AddElement(
      symbol = "kr",
      keywords = Seq("&DIGITS-LOWERCASE-UPPERCASE"),
      arity = 0,
      Options(),
      Overload(
        name = "Digits, Lowercase, Uppercase",
        args = Seq(),
        description =
          "Push \"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kv",
      keywords = Seq("&LOWERCASE-VOWELS"),
      arity = 0,
      Options(),
      Overload(
        name = "Lowercase Vowels",
        args = Seq(),
        description = "Push \"aeiou\" to the stack",
      ),
    ),
    AddElement(
      symbol = "ky",
      keywords = Seq("&LOWERCASE-VOWELSY"),
      arity = 0,
      Options(),
      Overload(
        name = "Lowercase Vowels and Y",
        args = Seq(),
        description = "Push \"aeiouy\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kz",
      keywords = Seq("&REVERSE-LOWERCASE"),
      arity = 0,
      Options(),
      Overload(
        name = "Reverse Lowercase",
        args = Seq(),
        description = "Push \"zyxwvutsrqponmlkjihgfedcba\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k⎶",
      keywords = Seq("&ALL-BRACKETS"),
      arity = 0,
      Options(),
      Overload(
        name = "All Brackets",
        args = Seq(),
        description = "Push \"{}[]<>()\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k☷",
      keywords = Seq("&BRACKETS-WITHOUT-<>"),
      arity = 0,
      Options(),
      Overload(
        name = "Brackets Without <>",
        args = Seq(),
        description = "Push \"{}[]()\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k◲",
      keywords = Seq("&PARENS-SQUARES"),
      arity = 0,
      Options(),
      Overload(
        name = "Parens and Squares",
        args = Seq(),
        description = "Push \"()[]\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k∪",
      keywords = Seq("&OPEN-BRACKETS"),
      arity = 0,
      Options(),
      Overload(
        name = "Open Brackets",
        args = Seq(),
        description = "Push \"([{\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k∩",
      keywords = Seq("&CLOSE-BRACKETS"),
      arity = 0,
      Options(),
      Overload(
        name = "Close Brackets",
        args = Seq(),
        description = "Push \")]}\"",
      ),
    ),
    AddElement(
      symbol = "k<",
      keywords = Seq("&OPEN-FISH-BRACKETS"),
      arity = 0,
      Options(),
      Overload(
        name = "Open Brackets",
        args = Seq(),
        description = "Push \"([{<\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k>",
      keywords = Seq("&CLOSE-FISH-BRACKETS"),
      arity = 0,
      Options(),
      Overload(
        name = "Close Brackets",
        args = Seq(),
        description = "Push \")]}>\"",
      ),
    ),
    AddElement(
      symbol = "k⇄",
      keywords = Seq("&ARROWS", "&DIRECTION-CHARS"),
      arity = 0,
      Options(),
      Overload(
        name = "Arrows",
        args = Seq(),
        description = "Push \"^>v<\"",
      ),
    ),
    AddElement(
      symbol = "k⎀",
      keywords = Seq("&LOWER-UPPER-VOWELS"),
      arity = 0,
      Options(),
      Overload(
        name = "Lowercase and Uppercase Vowels",
        args = Seq(),
        description = "Push \"aeiouAEIOU\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k⩔",
      keywords = Seq("&CODEPAGE"),
      arity = 0,
      Options(),
      Overload(
        name = "Codepage",
        args = Seq(),
        description = "Push the Vyxal codepage to the stack",
      ),
    ),
    AddElement(
      symbol = "k½",
      keywords = Seq("1~2", "one-two"),
      arity = 0,
      Options(),
      Overload(
        name = "[1, 2]",
        args = Seq(),
        description = "Push the list [1, 2] to the stack",
      ),
    ),
    AddElement(
      symbol = "k①",
      keywords = Seq("180", "one-eighty"),
      arity = 0,
      Options(),
      Overload(
        name = "180",
        args = Seq(),
        description = "Push 180 to the stack",
      ),
    ),
    AddElement(
      symbol = "k②",
      keywords = Seq("270", "two-seventy"),
      arity = 0,
      Options(),
      Overload(
        name = "270",
        args = Seq(),
        description = "Push 270 to the stack",
      ),
    ),
    AddElement(
      symbol = "k③",
      keywords = Seq("2048", "twenty-forty-eight", "bit-11"),
      arity = 0,
      Options(),
      Overload(
        name = "2048",
        args = Seq(),
        description = "Push 2048 to the stack",
      ),
    ),
    AddElement(
      symbol = "k④",
      keywords = Seq("4096", "forty-ninety-six", "bit-12"),
      arity = 0,
      Options(),
      Overload(
        name = "4096",
        args = Seq(),
        description = "Push 4096 to the stack",
      ),
    ),
    AddElement(
      symbol = "k⑤",
      keywords = Seq("8192", "eighty-one-ninety-two", "bit-13"),
      arity = 0,
      Options(),
      Overload(
        name = "8192",
        args = Seq(),
        description = "Push 8192 to the stack",
      ),
    ),
    AddElement(
      symbol = "k⑥",
      keywords = Seq("16384", "sixteen-three-eight-four", "bit-14"),
      arity = 0,
      Options(),
      Overload(
        name = "16384",
        args = Seq(),
        description = "Push 16384 to the stack",
      ),
    ),
    AddElement(
      symbol = "k⑦",
      keywords = Seq("32768", "bit-15"),
      arity = 0,
      Options(),
      Overload(
        name = "32768",
        args = Seq(),
        description = "Push 32768 to the stack",
      ),
    ),
    AddElement(
      symbol = "k⑧",
      keywords = Seq("65536", "bit-16"),
      arity = 0,
      Options(),
      Overload(
        name = "65536",
        args = Seq(),
        description = "Push 65536 to the stack",
      ),
    ),
    AddElement(
      symbol = "k⁰",
      keywords = Seq("2147483648", "bit-31"),
      arity = 0,
      Options(),
      Overload(
        name = "2147483648",
        args = Seq(),
        description = "Push 2147483648 to the stack",
      ),
    ),
    AddElement(
      symbol = "kġ",
      keywords = Seq("&LOWERCASE-CONSONANTS"),
      arity = 0,
      Options(),
      Overload(
        name = "Lowercase Consonants",
        args = Seq(),
        description = "Push \"bcdfghjklmnpqrstvwxyz\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kɠ",
      keywords = Seq("&LOWERCASE-CONSONANTS-WITHOUT-Y"),
      arity = 0,
      Options(),
      Overload(
        name = "Lowercase Consonants Without Y",
        args = Seq(),
        description = "Push \"bcdfghjklmnpqrstvwxz\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kĠ",
      keywords = Seq("&UPPERCASE-CONSONANTS"),
      arity = 0,
      Options(),
      Overload(
        name = "Uppercase Consonants",
        args = Seq(),
        description = "Push \"BCDFGHJKLMNPQRSTVWXYZ\" to the stack",
      ),
    ),
    AddElement(
      symbol = "kƓ",
      keywords = Seq("&UPPERCASE-CONSONANTS-WITHOUT-Y"),
      arity = 0,
      Options(),
      Overload(
        name = "Uppercase Consonants Without Y",
        args = Seq(),
        description = "Push \"BCDFGHJKLMNPQRSTVWXZ\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k⎘",
      keywords = Seq("&BRAINF*CK-COMMANDS"),
      arity = 0,
      Options(),
      Overload(
        name = "Brainf*ck Commands",
        args = Seq(),
        description = "Push \"[]<>-+.,\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k⌹",
      keywords = Seq("&PAIRED-BRACKETS"),
      arity = 0,
      Options(),
      Overload(
        name = "Paired Brackets",
        args = Seq(),
        description = "Push [\"()\", \"[]\", \"{}\", \"<>\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k¤",
      keywords = Seq("&NESTED-BRACKETS"),
      arity = 0,
      Options(),
      Overload(
        name = "Nested Brackets",
        args = Seq(),
        description = "Push \"([{<>}])\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k²",
      keywords = Seq("1048576", "bit-20"),
      arity = 0,
      Options(),
      Overload(
        name = "1048576 (2^20)",
        args = Seq(),
        description = "Push 1048576 to the stack",
      ),
    ),
    AddElement(
      symbol = "k³",
      keywords = Seq("1073741824", "bit-30"),
      arity = 0,
      Options(),
      Overload(
        name = "1073741824 (2^30)",
        args = Seq(),
        description = "Push 1073741824 to the stack",
      ),
    ),
    AddElement(
      symbol = "kγ",
      keywords = Seq("&LOWER-UPPER-VOWELSY"),
      arity = 0,
      Options(),
      Overload(
        name = "Lowercase and Uppercase Vowels and Y",
        args = Seq(),
        description = "Push \"aeiouyAEIOUY\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k◌",
      keywords = Seq("&DIRECTIONS"),
      arity = 0,
      Options(),
      Overload(
        name = "Directions List",
        args = Seq(),
        description =
          "Push [[0, 1], [0, -1], [1, 0], [-1, 0]] to the stack (up, down, right, left in a 2D grid)",
      ),
    ),
    AddElement(
      symbol = "kℂ",
      keywords = Seq("&ROMAN-NUMERALS"),
      arity = 0,
      Options(),
      Overload(
        name = "Roman Numerals",
        args = Seq(),
        description = "Push \"IVXLCDM\" to the stack",
      ),
    ),
    AddElement(
      symbol = "k•",
      keywords = Seq("&QWERTY-ROWS"),
      arity = 0,
      Options(),
      Overload(
        name = "QWERTY Rows",
        args = Seq(),
        description =
          "Push [\"qwertyuiop\", \"asdfghjkl\", \"zxcvbnm\"] to the stack",
      ),
    ),
    AddElement(
      symbol = "kṬ",
      keywords = Seq("&ZZ", "&INTEGERS"),
      arity = 0,
      Options(),
      Overload(
        name = "Integers",
        args = Seq(),
        description = "Push a list of every integer to the stack",
      ),
    ),
    AddElement(
      symbol = "∆∧",
      keywords = Seq("bitwise-and"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Bitwise And",
        args = Seq("num", "num"),
        description = "#1 & #2",
      ),
    ),
    AddElement(
      symbol = "∆∨",
      keywords = Seq("bitwise-or"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Bitwise Or",
        args = Seq("num", "num"),
        description = "#1 | #2",
      ),
    ),
    AddElement(
      symbol = "∆¬",
      keywords = Seq("bitwise-not"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Bitwise Not",
        args = Seq("num"),
        description = "~#1",
      ),
    ),
    AddElement(
      symbol = "∆⊍",
      keywords = Seq("bitwise-xor"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Bitwise Xor",
        args = Seq("num", "num"),
        description = "#1 ^ #2",
      ),
    ),
    AddElement(
      symbol = "#C",
      keywords = Seq("compress", "dictionary-compress", "dict-compress"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Compress",
        args = Seq("str"),
        description = "Compress #1 using the Vyxal compression algorithm",
      ),
    ),
    AddElement(
      symbol = "#c",
      keywords = Seq("b252compress"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Base 252 Compress String",
        args = Seq("str"),
        description =
          "Compress #1 using simple base 252 compression. Requires all characters to be lowercase letters, or spaces.",
      ),
      Overload(
        name = "Base 252 Compress Number",
        args = Seq("num"),
        description = "Convert #1 to base 252, using the codepage as the digits",
      ),
    ),
    AddElement(
      symbol = "#Q",
      keywords = Seq("quit"),
      arity = 0,
      Options(vectorises = false),
      Overload(
        name = "Quit",
        args = Seq(),
        description = "Quit the program",
      ),
    ),
    AddElement(
      symbol = "#X",
      keywords = Seq("break"),
      arity = 0,
      Options(vectorises = false),
      Overload(
        name = "Break",
        args = Seq(),
        description = "Break out of the current loop",
      ),
    ),
    AddElement(
      symbol = "#x",
      keywords = Seq("continue"),
      arity = 0,
      Options(vectorises = false),
      Overload(
        name = "Continue",
        args = Seq(),
        description = "Continue to the next iteration of the current loop",
      ),
    ),
    AddElement(
      symbol = "#w",
      keywords = Seq("nest", "ensure-wrapped"),
      arity = 1,
      Options(vectorises = false),
      Overload(
        name = "Ensure Wrapped",
        args = Seq("any"),
        description =
          "Ensure #1 is wrapped in a list. Returns scalars wrapped in a list. Returns lists as-is",
      ),
    ),
    AddElement(
      symbol = "#¿",
      keywords = Seq("input-count"),
      arity = 0,
      Options(vectorises = false),
      Overload(
        name = "Input Count",
        args = Seq(),
        description = "Push the number of inputs to the stack",
      ),
    ),
    AddElement(
      symbol = "∆<",
      keywords = Seq("arg", "phase", "angle"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Argument",
        args = Seq("num"),
        description =
          "The angle (argument) between the positive real axis and the line joining the origin to #1 in the complex plane.",
      ),
    ),
    AddElement(
      symbol = "∆A",
      keywords = Seq("arithmetic-mean"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Arithmetic Mean",
        args = Seq("lst"),
        description =
          "Arithmetic mean of #1 (sum(#1) / len(#1)). Vectorises over lists of lists.",
      ),
    ),
    AddElement(
      symbol = "∆C",
      keywords = Seq("cosh", "hyperbolic-cosine"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Hyperbolic Cosine",
        args = Seq("num"),
        description = "Hyperbolic cosine of #1",
      ),
    ),
    AddElement(
      symbol = "∆G",
      keywords = Seq("geometric-mean"),
      arity = 1,
      Options(vectorises = false),
      Overload(
        name = "Geometric Mean",
        args = Seq("lst"),
        description = "Geometric mean of #1 (product(#1) ^ (1 / len(#1)))",
      ),
    ),
    AddElement(
      symbol = "∆H",
      keywords = Seq("harmonic-mean"),
      arity = 1,
      Options(vectorises = false),
      Overload(
        name = "Harmonic Mean",
        args = Seq("lst"),
        description = "Harmonic mean of #1 (len(#1) / sum(1 / #1))",
      ),
    ),
    AddElement(
      symbol = "∆I",
      keywords = Seq("imaginary-part"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Imaginary Part",
        args = Seq("num"),
        description = "Imaginary part of #1",
      ),
    ),
    AddElement(
      symbol = "∆L",
      keywords = Seq("least-common-multiple"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Least Common Multiple",
        args = Seq("num", "num"),
        description = "Least common multiple of #1 and #2",
      ),
      Overload(
        name = "Least Common Multiple of List",
        args = Seq("lst"),
        description = "Least common multiple of all elements of #1",
      ),
      Overload(
        name = "Least Common Multiple",
        args = Seq("lst", "num"),
        description = "Least common multiple of #1.append(#2)",
      ),
    ),
    AddElement(
      symbol = "∆M",
      keywords = Seq("mode"),
      arity = 1,
      Options(vectorises = false),
      Overload(
        name = "Mode",
        args = Seq("lst"),
        description =
          "Mode of #1 (most common element in #1). If there are multiple modes, returns the first one.",
      ),
    ),
    AddElement(
      symbol = "∆R",
      keywords = Seq("real-part"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Real Part",
        args = Seq("num"),
        description = "Real part of #1",
      ),
    ),
    AddElement(
      symbol = "∆S",
      keywords = Seq("sinh", "hyperbolic-sine"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Hyperbolic Sine",
        args = Seq("num"),
        description = "Hyperbolic sine of #1",
      ),
    ),
    AddElement(
      symbol = "∆T",
      keywords = Seq("tanh", "hyperbolic-tangent"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Hyperbolic Tangent",
        args = Seq("num"),
        description = "Hyperbolic tangent of #1",
      ),
    ),
    AddElement(
      symbol = "∆c",
      keywords = Seq("cos", "cosine"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Cosine",
        args = Seq("num"),
        description = "Cosine of #1",
      ),
    ),
    AddElement(
      symbol = "∆q",
      keywords = Seq("prime-exponents"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Prime Exponents",
        args = Seq("num"),
        description =
          "push a list of the power of each prime in the prime factors of #1",
      ),
    ),
    AddElement(
      symbol = "∆s",
      keywords = Seq("sin", "sine"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Sine",
        args = Seq("num"),
        description = "Sine of #1",
      ),
    ),
    AddElement(
      symbol = "∆t",
      keywords = Seq("tan", "tangent"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Tangent",
        args = Seq("num"),
        description = "Tangent of #1",
      ),
    ),
    AddElement(
      symbol = "∆⎀",
      keywords = Seq("polar-parts"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Polar Parts",
        args = Seq("num"),
        description = "Push the magnitude and argument of #1",
      ),
    ),
    AddElement(
      symbol = "∆⌹",
      keywords = Seq("complex-parts"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Complex Parts",
        args = Seq("num"),
        description = "Push the real and imaginary parts of #1",
      ),
    ),
    AddElement(
      symbol = "∆Ṭ",
      keywords = Seq("atan2", "arctan2", "arctangent2"),
      arity = 2,
      Options(vectorises = true),
      Overload(
        name = "Arctangent 2",
        args = Seq("num", "num"),
        description = "atan2(#1, #2)",
      ),
    ),
    AddElement(
      symbol = "∆↯",
      keywords = Seq("arcsin", "arcsine"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Arcsine",
        args = Seq("num"),
        description = "Arcsine of #1",
      ),
    ),
    AddElement(
      symbol = "∆ℭ",
      keywords = Seq("arccos", "arccosine"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Arccosine",
        args = Seq("num"),
        description = "Arccosine of #1",
      ),
    ),
    AddElement(
      symbol = "∆ʈ",
      keywords = Seq("arctan", "arctangent"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Arctangent",
        args = Seq("num"),
        description = "Arctangent of #1",
      ),
    ),
    AddElement(
      symbol = "∆d",
      keywords = Seq("rad2deg", "rad-to-deg"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Radians to Degrees",
        args = Seq("num"),
        description = "Convert #1 from radians to degrees",
      ),
    ),
    AddElement(
      symbol = "∆r",
      keywords = Seq("deg2rad", "deg-to-rad"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Degrees to Radians",
        args = Seq("num"),
        description = "Convert #1 from degrees to radians",
      ),
    ),
    AddElement(
      symbol = "∆æ",
      keywords = Seq("all-prime-exponents", "all-prime-exps"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "All Prime Exponents",
        args = Seq("num"),
        description =
          "For all primes less than or equal to #1, push the power of that prime in the factorisation of #1",
      ),
    ),
    AddElement(
      symbol = "∆⧢",
      keywords = Seq("root-of-unity"),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Principal Root of Unity",
        args = Seq("num"),
        description = "Principal #1-th root of unity (e^(2i * pi / #1))",
      ),
    ),
    AddElement(
      symbol = "øA",
      keywords = Seq(
        "letter-to-number",
        "number-to-letter",
        "letter-number-swap",
        "number-letter-swap",
        "a1-swap",
      ),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Letter to Number",
        args = Seq("str"),
        description = "the index of #1 in the alphabet (one-indexed)",
      ),
      Overload(
        name = "Number to Letter",
        args = Seq("num"),
        description = "the letter at index #1 in the alphabet (one-indexed)",
      ),
    ),
    AddElement(
      symbol = "øa",
      keywords = Seq(
        "letter-to-index",
        "index-to-letter",
        "letter-index-swap",
        "index-letter-swap",
        "a0-swap",
      ),
      arity = 1,
      Options(vectorises = true),
      Overload(
        name = "Letter to Index",
        args = Seq("str"),
        description = "the index of #1 in the alphabet (zero-indexed)",
      ),
      Overload(
        name = "Index to Letter",
        args = Seq("num"),
        description = "the letter at index #1 in the alphabet (zero-indexed)",
      ),
    ),
    AddElement(
      symbol = "ø◲",
      keywords = Seq("surround"),
      arity = 2,
      Options(vectorises = false),
      Overload(
        name = "Surround",
        args = Seq("any", "any"),
        description = "#1 prepended and appended to #2",
      ),
    ),
    AddElement(
      symbol = "Þ0",
      keywords = Seq("zero-pad"),
      arity = 2,
      Options(vectorises = false),
      Overload(
        name = "Zero Pad",
        args = Seq("itr", "num"),
        description = "{#1|#2} zero-padded to length {#2|#1}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Zero Pad",
        args = Seq("itr", "itr"),
        description = "#1 zero-padded to length of #2",
      ),
    ),
    AddElement(
      symbol = "ÞO",
      keywords = Seq(
        "grid-neighbours-wrap",
        "grid-neighbors-wrap",
        "adjacent-cells-wrap",
        "adj-cells-wrap",
        "surrounding-cells-wrap",
      ),
      arity = 1,
      Options(vectorises = false),
      Overload(
        name = "Grid Neighbours Wrap",
        args = Seq("lst"),
        description =
          "Grid neighbours of #1 - up, down, left, right - wrapping around",
      ),
      Overload(
        name = "Grid Neighbours Wrap With Starting Direction",
        args = Seq("lst", "num"),
        description =
          "Grid neighbours of cells in #1 - right, down, left, up - wrapping around and start from direction #2 => 0: right, 1: down, 2: left, 3: up. Negative #2 does not include middle, positive #2 does",
      ),
    ),
    AddElement(
      symbol = "ÞR",
      keywords = Seq("reshape"),
      arity = 2,
      Options(vectorises = false),
      Overload(
        name = "Reshape",
        args = Seq("lst", "lst[num]"),
        description = "Reshape #1 to the shape #2",
      ),
    ),
    AddElement(
      symbol = "ÞT",
      keywords = Seq("transpose-safe"),
      arity = 1,
      Options(vectorises = false),
      Overload(
        name = "Transpose Safe",
        args = Seq("lst"),
        description =
          "Transpose #1, does not hang on an infinite list of finite lists",
      ),
    ),
    AddElement(
      symbol = "ÞX",
      keywords = Seq(
        "cartesian-product-unsafe",
        "cartesian-unsafe",
        "cart-prod-unsafe",
        "cart-unsafe",
      ),
      arity = 2,
      Options(vectorises = false),
      Overload(
        name = "Cartesian Product Unsafe",
        args = Seq("lst", "lst"),
        description =
          "Cartesian product of #1 and #2 in the standard order, but without accounting for infinite lists",
      ),
    ),
    AddElement(
      symbol = "Þi",
      keywords = Seq("md-index"),
      arity = 2,
      Options(vectorises = false),
      Overload(
        name = "Multi-Dimensional Index",
        args = Seq("lst", "lst[num]"),
        description =
          "Index #1 at the multi-dimensional index #2 - #1[#2[0]][#2[1]]...[#2[n]]",
      ),
    ),
    AddElement(
      symbol = "Þo",
      keywords = Seq(
        "grid-neighbours",
        "grid-neighbors",
        "adjacent-cells",
        "adj-cells",
        "surrounding-cells",
      ),
      arity = 1,
      Options(vectorises = false),
      Overload(
        name = "Grid Neighbours",
        args = Seq("lst"),
        description = "Grid neighbours of #1 - up, down, left, right",
      ),
      Overload(
        name = "Grid Neighbours With Starting Direction",
        args = Seq("lst", "num"),
        description =
          "Grid neighbours of cells in #1 - right, down, left, up - start from direction #2 => 0: right, 1: down, 2: left, 3: up",
      ),
    ),
    AddElement(
      symbol = "Þ↻",
      keywords = Seq("cycle"),
      arity = 1,
      Options(vectorises = false),
      Overload(
        name = "Cycle",
        args = Seq("lst"),
        description =
          "Cycle #1 - Append all items of #1 to itself infinite times",
      ),
      Overload(
        name = "List-Repeat Infinitely",
        args = Seq("num|str"),
        description = "Repeat #1 infinitely - [#1, #1, #1, ...]",
      ),
    ),
    AddElement(
      symbol = "Þ¤",
      keywords = Seq(
        "grid-neighbours-diagonals-wrap",
        "grid-neighbors-diagonals-wrap",
        "adjacent-cells-diagonals-wrap",
        "adj-cells-diagonals-wrap",
        "surrounding-cells-diagonals-wrap",
        "eight-cells-wrap",
      ),
      arity = 1,
      Options(vectorises = false),
      Overload(
        name = "Grid Neighbours Diagonals Wrap",
        args = Seq("lst"),
        description =
          "Grid neighbours of #1 - up, down, left, right, and diagonals - wrapping around",
      ),
      Overload(
        name = "Grid Neighbours Diagonals Wrap With Starting Direction",
        args = Seq("lst", "num"),
        description =
          "Grid neighbours of cells in #1 - right, down, left, up, and diagonals - wrapping around and start from direction #2 => 0: right, 1: down, 2: left, 3: up. Negative #2 does not include middle, positive #2 does",
      ),
    ),
    AddElement(
      symbol = "Þ⁰",
      keywords = Seq("zero-lift"),
      arity = 1,
      Options(vectorises = false),
      Overload(
        name = "Zero Lift",
        args = Seq("lst"),
        description = "Multiply each element of #1 by its 0-based index",
      ),
    ),
    AddElement(
      symbol = "Þ¹",
      keywords = Seq("one-lift"),
      arity = 1,
      Options(vectorises = false),
      Overload(
        name = "One Lift",
        args = Seq("lst"),
        description = "Multiply each element of #1 by its 1-based index",
      ),
    ),
    AddElement(
      symbol = "Þ⊍",
      keywords = Seq("multiset-xor", "mset-xor"),
      arity = 2,
      Options(vectorises = false),
      Overload(
        name = "Multiset XOR",
        args = Seq("lst", "lst"),
        description = "Multiset XOR of #1 and #2",
      ),
    ),
    AddElement(
      symbol = "Þ⦰",
      keywords = Seq("multiset-difference", "mset-diff"),
      arity = 2,
      Options(vectorises = false),
      Overload(
        name = "Multiset Difference",
        args = Seq("lst", "lst"),
        description = "Multiset difference of #1 and #2",
      ),
    ),
    AddElement(
      symbol = "Þ∩",
      keywords = Seq("multiset-intersection", "mset-isect"),
      arity = 2,
      Options(vectorises = false),
      Overload(
        name = "Multiset Intersection",
        args = Seq("lst", "lst"),
        description = "Multiset intersection of #1 and #2",
      ),
    ),
    AddElement(
      symbol = "Þ⎀",
      keywords = Seq("md-assign"),
      arity = 3,
      Options(vectorises = false),
      Overload(
        name = "Multi-Dimensional Assign",
        args = Seq("lst", "lst[num]", "any"),
        description =
          "Assign #3 to the multi-dimensional index #2 in #1 - #1[#2[0]][#2[1]]...[#2[n]] = #3",
      ),
    ),
    AddElement(
      symbol = "Þ◌",
      keywords = Seq(
        "grid-neighbours-diagonals",
        "grid-neighbors-diagonals",
        "adjacent-cells-diagonals",
        "adj-cells-diagonals",
        "surrounding-cells-diagonals",
        "eight-cells",
      ),
      arity = 1,
      Options(vectorises = false),
      Overload(
        name = "Grid Neighbours Diagonals",
        args = Seq("lst"),
        description =
          "Grid neighbours of #1 - up, down, left, right, and diagonals",
      ),
      Overload(
        name = "Grid Neighbours Diagonals With Starting Direction",
        args = Seq("lst", "num"),
        description =
          "Grid neighbours of cells in #1 - right, down, left, up, and diagonals - start from direction #2 => 0: right, 1: down, 2: left, 3: up",
      ),
    ),
    AddElement(
      symbol = "Þ⅟",
      keywords = Seq("matrix-inverse", "m**-1"),
      arity = 1,
      Options(vectorises = false),
      Overload(
        name = "Matrix Inverse",
        args = Seq("lst[lst]"),
        description = "Inverse of #1",
      ),
    ),
  )

  val modifiers: Map[String, Modifier] = Map(
    AddModifier(
      symbol = "∥",
      keywords = Seq("parallel-apply:", "para:"),
      numberOfElements = 2,
      ModifierOverload(
        name = "Parallel Apply",
        args = Seq("mon", "mon"),
        description =
          "Apply #1 and #2 on separate stacks and push both results",
        example = "3 4 ∥d½ -> 8 2",
      ),
    ),
    AddModifier(
      symbol = "∦",
      keywords = Seq("parallel-apply-wrap:", "paraw:"),
      numberOfElements = 2,
      ModifierOverload(
        name = "Parallel Apply Wrap",
        args = Seq("mon", "mon"),
        description =
          "Apply #1 and #2 on separate stacks and push both results wrapped in a list. Equivalent to `∥#1#2;`",
        example = "3 4 ∦d½ -> [8, 2]",
      ),
    ),
    AddModifier(
      symbol = "∺",
      keywords = Seq("correspond:"),
      numberOfElements = 2,
      ModifierOverload(
        name = "Correspond",
        args = Seq("mon", "mon"),
        description = "Apply #1 to <under> and #2 to <top>",
        example = "3 4 ∺d½ -> 6 2",
      ),
      ModifierOverload(
        name = "Dyadic Correspond",
        args = Seq("dyd+", "dyd+"),
        description =
          "Apply #2 to #2.arity top items, and #1 to #1.arity items under that",
        example = "3 4 5 6 ∺+- -> 7 1_",
      ),
    ),
    AddModifier(
      symbol = "⁜",
      keywords = Seq("group-by:", "window-reduce:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "Group By",
        args = Seq("mon"),
        description = "Group items of the top of the stack by results of #1",
        example = "#[1|3|4|5|2|4#] ⁜e -> [[1,3],[4],[5],[2,4]]",
      ),
      ModifierOverload(
        name = "Window Reduce",
        args = Seq("dyd+"),
        description = "Reduce each overlapping window of size #1.arity with #1",
        example = "#[1|2|3|4|5|6#] ⁜λ3|+} -> [6, 9, 12, 15]",
      ),
    ),
    AddModifier(
      symbol = "⑴",
      keywords = Seq("*:", "one-element-lambda:", "single-element-lambda:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "Next Element as Lambda",
        args = Seq("any"),
        description = "Wrap #1 in a lambda and push it",
        example = "⑴+ = λ+}",
      ),
    ),
    AddModifier(
      symbol = "⑵",
      keywords = Seq("**:", "two-element-lambda:"),
      numberOfElements = 2,
      ModifierOverload(
        name = "Next Two Elements as Lambda",
        args = Seq("any", "any"),
        description = "Wrap #1 and #2 in a lambda and push it",
        example = "⑵+* = λ+*}",
      ),
    ),
    AddModifier(
      symbol = "⑶",
      keywords = Seq("***:", "three-element-lambda:"),
      numberOfElements = 3,
      ModifierOverload(
        name = "Next Three Elements as Lambda",
        args = Seq("any", "any", "any"),
        description = "Wrap #1, #2, and #3 in a lambda and push it",
        example = "⑶+*~ = λ+*~}",
      ),
    ),
    AddModifier(
      symbol = "⑷",
      keywords = Seq("****:", "four-element-lambda:"),
      numberOfElements = 4,
      ModifierOverload(
        name = "Next Four Elements as Lambda",
        args = Seq("any", "any", "any", "any"),
        description = "Wrap #1, #2, #3, and #4 in a lambda and push it",
        example = "⑷+*~d = λ+*~d}",
      ),
    ),
    AddModifier(
      symbol = "⎂",
      keywords = Seq("both:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "Both",
        args = Seq("any"),
        description =
          "Apply #1 to both the top of stack (or however many arguments), and under stack (or however many arguments under the arity). Effectively ... #1(top - arity, top - arity * 2) #1(top -> top - arity)",
        example = "3 4 ⎂d -> 6 8 || 1 2 3 4 ⎂+ -> 3 7",
      ),
    ),
    AddModifier(
      symbol = "⟒",
      keywords = Seq("left-fork:", "hook:"),
      numberOfElements = 2,
      ModifierOverload(
        name = "Left Fork",
        args = Seq("dyd+", "dyd+"),
        description =
          "Apply #1 but keep the under stack, and then apply #2. Effectively #2(#1(top, under), under)",
        example = "3 4 ⟒+× -> 28",
      ),
      ModifierOverload(
        name = "Left Fork",
        args = Seq("mon", "dyd+"),
        description =
          "Apply #1 but keep the under stack, and then apply #2. Effectively #2(#1(top, under), under)",
        example = "\"hEllO\"f ⟒ʀ= -> [1, 0, 1, 1, 0]",
      ),
    ),
    AddModifier(
      symbol = "ᛞ",
      keywords = Seq("inner-product:"),
      numberOfElements = 2,
      ModifierOverload(
        name = "Inner Product",
        args = Seq("dyd", "dyd"),
        description = "Inner product of #1 and #2",
        example = "#[1|2|3#] #[4|5|6#] ᛞ×+ -> 32",
      ),
    ),
    AddModifier(
      symbol = "▦",
      keywords = Seq("outer-product:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "Outer Product",
        args = Seq("dyd"),
        description = "Outer product of #1 and #2",
        example =
          "#[1|2|3#] #[4|5|6#] ▦; -> [[[1,4],[1,5],[1,6]],[[2,4],[2,5],[2,6],[3,4],[3,5],[3,6]]]",
      ),
    ),
    AddModifier(
      symbol = "¨",
      keywords = Seq("each:", "vectorise:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "Each",
        args = Seq("any"),
        description = "Map #1 over the top of the stack",
        example = "#[#[1|2|3#]|#[4|2|3#]|#[1|5|3#]#] ¨G -> [3, 4, 5]",
      ),
    ),
    AddModifier(
      symbol = "Ẅ",
      keywords = Seq("zip-with:", "zip-reduce:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "Zip With",
        args = Seq("dyd"),
        description = "Pop two lists and zip them, reducing each pair with #1",
        example = "#[1|2|3#] #[4|5|6#] ¨; -> [[1, 4], [2, 5], [3, 6]]",
      ),
    ),
    AddModifier(
      symbol = "¿",
      keywords = Seq("if:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "If",
        args = Seq("any"),
        description = "If the top of the stack is truthy, apply #1",
        example = "3 1 ¿d -> 6",
      ),
    ),
    AddModifier(
      symbol = "#⍰",
      keywords = Seq("if-else:"),
      numberOfElements = 2,
      ModifierOverload(
        name = "If Else",
        args = Seq("any", "any"),
        description =
          "If the top of the stack is truthy, apply #1, else apply #2",
        example = "3 1 #⍰d½ -> 6",
      ),
    ),
    AddModifier(
      symbol = "⎇",
      keywords = Seq("dip:", "under:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "Dip",
        args = Seq("mon"),
        description =
          "Save the top stack item, apply #1, then push the saved item",
        example = "3 4 5 2 ⎇+ -> 3 9 2",
      ),
    ),
    AddModifier(
      symbol = "~",
      keywords = Seq("filter:", "without-popping:", "peek:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "Filter",
        args = Seq("mon"),
        description = "Filter the top of the stack with #1",
        example = "#[1|2|3|4|5#] ~2≛ -> [2, 4]",
      ),
      ModifierOverload(
        name = "Peek",
        args = Seq("dyd+"),
        description = "Apply #1 without popping",
        example = "3 4 5 ~+ -> 3 4 9",
      ),
    ),
    AddModifier(
      symbol = "⩔",
      keywords = Seq("at-simple-levels:", "@simple:", "deep-vectorise:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "At Simple Levels",
        args = Seq("mon"),
        description = "Apply #1 at the simple levels of the top of the stack",
        example =
          "#[#[#[1|2|3#]|#[#[4|5|#[6|7|8#]#]#]#]#] ⩔L -> [[3, [[1, 1, 3]]]]",
      ),
    ),
    AddModifier(
      symbol = "\\",
      keywords = Seq("reject-by:", "scanl:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "Reject By",
        args = Seq("mon"),
        description = "Reject items of the top of the stack by results of #1",
        example = "#[1|3|4|5|2|4#] \\e -> [1, 3, 5]",
      ),
      ModifierOverload(
        name = "Scanl",
        args = Seq("dyd+"),
        description = "Scan left with #1",
        example = "#[1|2|3|4#] \\+ -> [1, 3, 6, 10]",
      ),
    ),
    AddModifier(
      symbol = "/",
      keywords = Seq("invariant-by:", "foldl:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "Invariant By",
        args = Seq("mon"),
        description =
          "Is the top of the stack invariant under #1? (i.e. #1(x) == x)",
        example = "0 /d -> 1",
      ),
      ModifierOverload(
        name = "Foldl",
        args = Seq("dyd+"),
        description = "Fold left with #1",
        example = "#[1|2|3|4#] /+ -> 10",
      ),
    ),
  )
end ElementInformation
