package vyxal.elements

case class Element(
    symbol: String,
    keywords: Seq[String],
    arity: Int,
    options: Options,
    overloads: Overload*
)

case class Modifier(
    symbol: String,
    keywords: Seq[String],
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
    vectorises: Boolean,
    peeks: Boolean = false,
)

object ElementInformation:
  def symbolFor(keyword: String): Option[String] =
    ElementInformation.elements.find(_.keywords.contains(keyword)).map(_.symbol)

  val elements: Seq[Element] = List(
    Element(
      symbol = "⊞",
      keywords = Seq("counts", "counts-of"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Counts of Items",
        args = Seq("lst"),
        description = "[#1.count(x) for x in set(#1)]",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "÷",
      keywords = Seq("divide", "string-pieces", "regex-split", "/", "div"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Division",
        args = Seq("num", "num"),
        description = "#1 / #2",
        typeSwitchable = false,
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
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "×",
      keywords =
        Seq("multiply", "string-repeat", "ring-translate", "*", "times"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Multiplication",
        args = Seq("num", "num"),
        description = "#1 * #2 (#1 times #2)",
        typeSwitchable = false,
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
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "∧",
      keywords = Seq("and", "&&", "logical-and"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Logical And",
        args = Seq("nsl", "nsl"),
        description =
          "Python-style and - if #2 is false, return #2, else return #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Short Circuit And",
        args = Seq("fun", "fun"),
        description =
          "Short circuit and - if #2() is false, return #2(), else return #1()",
      ),
    ),
    Element(
      symbol = "∨",
      keywords = Seq("or", "!!", "logical-or"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Loigcal Or",
        args = Seq("any", "any"),
        description =
          "Python style or - if #2 is true, return #2, else return #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Short Circuit Or",
        args = Seq("fun", "fun"),
        description =
          "Short circuit or - if #2() is true, return #2(), else return #1()",
      ),
    ),
    Element(
      symbol = "¬",
      keywords = Seq("not", "~", "logical-not"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Not",
        args = Seq("any"),
        description = "if #1 is truthy, return False, else return True",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ʀ",
      keywords = Seq("0->n", "lowercase", "range-0->n", "nrange-0"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Range 0",
        args = Seq("num"),
        description = "Range from 0 to #1, exclusive",
        typeSwitchable = false,
      ),
      Overload(
        name = "Lowercase",
        args = Seq("str"),
        description = "Lowercase #1",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "ʁ",
      keywords = Seq("0->n++", "uppercase", "range-0->n++", "n+range-0"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Range 0 Inclusive",
        args = Seq("num"),
        description = "Range from 0 to #1, inclusive",
        typeSwitchable = false,
      ),
      Overload(
        name = "Uppercase",
        args = Seq("str"),
        description = "Uppercase #1",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "ɾ",
      keywords = Seq("1->n++", "is-alpha?"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Range 1 Inclusive",
        args = Seq("num"),
        description = "Range from 1 to #1, inclusive",
        typeSwitchable = false,
      ),
      Overload(
        name = "Is Character Alphabetical",
        args = Seq("str"),
        description = "Check if #1 is alphabetical (i.e. is a letter)",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "‹",
      keywords =
        Seq("decrement", "--", "pad-to-8", "dec", "pad-8", "pad-to-byte"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Decrement",
        args = Seq("num"),
        description = "#1 - 1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Pad to 8",
        args = Seq("str"),
        description = "Pad #1 to a length that is a multiple of 8 with '0's",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "›",
      keywords =
        Seq("increment", "++", "space-to-0", "replace-spaces-with-0s", "inc"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Increment",
        args = Seq("num"),
        description = "#1 + 1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Spaces to 0s",
        args = Seq("str"),
        description = "Replace spaces in #1 with '0's",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "!",
      keywords = Seq("factorial", "!", "titlecase", "fact", "title", "fac"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Factorial",
        args = Seq("num"),
        description = "Factorial of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Titlecase",
        args = Seq("str"),
        description = "Titlecase #1",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "$",
      keywords = Seq("swap"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Swap",
        args = Seq("any", "any"),
        description = "Swap #1 and #2 on the stack: #1 #2 -> #2 #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "%",
      keywords = Seq("mod", "modulo", "%", "remainder"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Modulo",
        args = Seq("num", "num"),
        description = "#1 % #2 (remainder of #1 divided by #2)",
        typeSwitchable = false,
      ),
      Overload(
        name = "String Format",
        args = Seq("str", "any"),
        description = "Format {#1|#2} with {#2|#1}",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "&",
      keywords = Seq("append"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Append",
        args = Seq("any", "any"),
        description = "Append #2 to #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "*",
      keywords = Seq("exponentiate", "pow", "**", "power"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Exponentiation",
        args = Seq("num", "num"),
        description = "#1 ** #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "+",
      keywords = Seq("add", "+", "plus", "addition"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Addition",
        args = Seq("num", "num"),
        description = "#1 + #2",
        typeSwitchable = false,
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
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = ",",
      keywords = Seq("println", "stdout", "output", "out"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Print",
        args = Seq("any"),
        description = "Print #1 to stdout, followed by a newline",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "-",
      keywords = Seq("subtract", "-", "minus", "subtraction", "regex-remove"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Subtraction",
        args = Seq("num", "num"),
        description = "#1 - #2",
        typeSwitchable = false,
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
        typeSwitchable = false,
      ),
    ),
    Element(
      ":",
      Seq("dup", "duplicate"),
      1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Duplicate",
        args = Seq("any"),
        description = "Push #1 twice to the stack: #1 -> #1 #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = ";",
      keywords = Seq("pair", "cons"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Pair",
        args = Seq("any", "any"),
        description = "Push a list [#1, #2] to the stack: #1 #2 -> [#1, #2]",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "<",
      keywords = Seq("less-than", "<", "lt"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Less Than",
        args = Seq("scl", "scl"),
        description = "#1 < #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "=",
      keywords = Seq("equals", "==", "eq"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Equals",
        args = Seq("scl", "scl"),
        description = "#1 == #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = ">",
      keywords = Seq("greater-than", ">", "gt"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Greater Than",
        args = Seq("scl", "scl"),
        description = "#1 > #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "?",
      keywords = Seq("stdin", "input", "in"),
      arity = 0,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Input",
        args = Seq(),
        description = "Get the next input item, evaluated.",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "@",
      keywords =
        Seq("absolute-difference", "abs-diff", "levenstein", "to-overpairs"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Absolute Difference",
        args = Seq("num", "num"),
        description = "Absolute difference between #1 and #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Levenstein Distance",
        args = Seq("str", "str"),
        description = "Levenstein distance between #1 and #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Reduce Overlapping Pairs",
        args = Seq("lst", "fun"),
        description = "Reduce overlapping pairs in {#1|#2} by function {#2|#1}",
      ),
    ),
    Element(
      symbol = "A",
      keywords = Seq("all", "all?", "vowel?", "is-vowel", "is-vowel?"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "All",
        args = Seq("any"),
        description = "Are all elements of #1 are truthy",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "B",
      keywords = Seq("to-binary"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "To Binary",
        args = Seq("num"),
        description = "Convert #1 to binary",
        typeSwitchable = false,
      ),
      Overload(
        name = "String to Binary",
        args = Seq("str"),
        description =
          "Convert each character in #1 to a binary representation of its unicode value",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "C",
      keywords = Seq("count"),
      arity = 2,
      Options(
        vectorises = false
      ),
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
    Element(
      symbol = "D",
      keywords = Seq("triplicate"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Triplicate",
        args = Seq("any"),
        description = "Push #1 thrice to the stack: #1 -> #1 #1 #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "E",
      keywords = Seq("2**n", "2pow", "eval", "2**"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "2 to the Power of N",
        args = Seq("num"),
        description = "2 ** #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Eval",
        args = Seq("str"),
        description = "Evaluate #1",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "F",
      keywords = Seq("filter", "find", "index-of"),
      arity = 2,
      Options(
        vectorises = false
      ),
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
    Element(
      symbol = "G",
      keywords = Seq("max-of", "maximum-of"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Monadic Maximum",
        args = Seq("lst"),
        description = "Maximum of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "H",
      keywords = Seq("to-hex", "from-hex"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "To Hex",
        args = Seq("num"),
        description = "Convert #1 to hexadecimal",
        typeSwitchable = false,
      ),
      Overload(
        name = "From Hex",
        args = Seq("str"),
        description =
          "Convert #1 from hexadecimal to a number. Inverse of 'to-hex'",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "I",
      keywords = Seq("interleave", "reject"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Interleave",
        args = Seq("any", "any"),
        description = "Interleave #1 and #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Reject",
        args = Seq("any", "fun"),
        description =
          "Remove elements of {#1|#2} that satisfy function {#2|#1}",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "J",
      keywords = Seq("join", "concat"),
      arity = 2,
      Options(
        vectorises = false
      ),
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
        typeSwitchable = false,
      ),
      Overload(
        name = "Number Pair",
        args = Seq("num", "num"),
        description = "Create a list of #1 and #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "String Concatenation",
        args = Seq("str|num", "str|num"),
        description =
          "string(#1) + string(#2) (if either #1 or #2 is a string)",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "K",
      keywords = Seq("factors", "is-numeric?", "is-numeric"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Factors",
        args = Seq("num"),
        description = "Get the factors of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Is Numeric",
        args = Seq("str"),
        description = "Check if #1 is numeric",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "L",
      keywords = Seq("length", "len"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Length",
        args = Seq("any"),
        description = "Length of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "M",
      keywords = Seq("map", "mold", "multiplicity", "regex-match"),
      arity = 2,
      Options(
        vectorises = true
      ),
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
        typeSwitchable = false,
      ),
      Overload(
        name = "Multiplicity",
        args = Seq("num", "num"),
        description = "How many times #1 divides #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Regex Match",
        args = Seq("str", "str"),
        description = "Return the first match of #2 in #1",
      ),
    ),
    Element(
      symbol = "N",
      keywords = Seq("negate", "swapcase", "first>-1"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Negate",
        args = Seq("num"),
        description = "-#1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Negate",
        args = Seq("str"),
        description = "Swap the case of each letter #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "First Non-Negative Integer Where Predicate is True",
        args = Seq("fun"),
        description = "First non-negative integer where #1 is true",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "O",
      keywords = Seq("ord", "chr"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Character to Unicode",
        args = Seq("str"),
        description = "Unicode value of each letter in #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Unicode to Character",
        args = Seq("num"),
        description = "Character of each unicode value in #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "P",
      keywords = Seq("prefixes"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Prefixes",
        args = Seq("lst"),
        description =
          "Get all prefixes of #1. Treats numbers as a list of digits",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "Q",
      keywords = Seq("remove-at", "regex-groups"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Remove At",
        args = Seq("nsl", "num"),
        description = "Remove the element at index #2 from #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Regex Groups",
        args = Seq("str", "str"),
        description = "Return the groups of the first match of #2 in #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "R",
      keywords = Seq("range", "reduce", "regex-match?"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Range",
        args = Seq("num", "num"),
        description = "Range from #1 to #2, exclusive",
        typeSwitchable = false,
      ),
      Overload(
        name = "Reduce",
        args = Seq("lst", "fun"),
        description = "Reduce #1 by function #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Regex Match?",
        args = Seq("str", "str"),
        description = "Check if #2 matches #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "S",
      keywords = Seq("sort"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Sort",
        args = Seq("itr"),
        description = "Sort #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "T",
      keywords = Seq("transpose", "triple", "alpha-only?"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Transpose",
        args = Seq("lst"),
        description = "Transpose #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Triple",
        args = Seq("num"),
        description = "#1 * 3",
        typeSwitchable = false,
      ),
      Overload(
        name = "Does String Contain Only Alphabetic Characters",
        args = Seq("str"),
        description = "Check if #1 contains only alphabetic characters",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "U",
      keywords = Seq("uninterleave"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Uninterleave",
        args = Seq("lst"),
        description = "Uninterleave #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "V",
      keywords = Seq("vectorse-reverse", "1-x"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Vectorise Reverse",
        args = Seq("lst"),
        description = "Reverse each item in #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "1 - X",
        args = Seq("num"),
        description = "1 - #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "W",
      keywords = Seq("wrap"),
      arity = -1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Wrap",
        args = Seq(),
        description = "Wrap the entire stack into a list",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "X",
      keywords = Seq("cartesian-product"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Cartesian Product",
        args = Seq("lst", "lst"),
        description = "Cartesian product of #1 and #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "Y",
      keywords = Seq("list-repeat"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "List Repeat",
        args = Seq("num", "num"),
        description =
          "A list of #1 repeated #2 times. E.g. 3 4 -> [3, 3, 3, 3]",
        typeSwitchable = false,
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
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "Z",
      keywords = Seq("zip"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Zip",
        args = Seq("lst", "lst"),
        description = "Zip #1 and #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "^",
      keywords = Seq("reverse-stack"),
      arity = -1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Reverse Stack",
        args = Seq(),
        description = "Reverse the stack",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "_",
      keywords = Seq("pop", "discard"),
      arity = 0,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Pop",
        args = Seq(),
        description = "Pop the top of the stack",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "a",
      keywords = Seq("any", "any?", "uppercase?"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Any",
        args = Seq("num"),
        description = "Are any digits of #1 truthy",
        typeSwitchable = false,
      ),
      Overload(
        name = "Is Uppercase",
        args = Seq("str"),
        description =
          "Check if #1 is uppercase. With string.len > 1, vectorises over each character",
        typeSwitchable = false,
      ),
      Overload(
        name = "Any",
        args = Seq("lst"),
        description = "Are any elements of #1 truthy",
      ),
    ),
    Element(
      symbol = "b",
      keywords = Seq("from-binary"),
      arity = 1,
      Options(
        vectorises = false
      ),
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
    Element(
      symbol = "c",
      keywords = Seq("contains", "contains?", "is-in"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Contains",
        args = Seq("scl", "scl"),
        description = "Is #2 in #1",
        typeSwitchable = false,
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
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "d",
      keywords = Seq("double"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Double",
        args = Seq("num"),
        description = "#1 * 2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Double",
        args = Seq("str"),
        description = "Append a copy of #1 to itself",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "e",
      keywords = Seq("even?", "is-even", "split-newlines", "/newline"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Is Even",
        args = Seq("num"),
        description = "Is #1 even",
        typeSwitchable = false,
      ),
      Overload(
        name = "Split Newlines",
        args = Seq("str"),
        description = "Split #1 by newlines",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "f",
      keywords = Seq("flatten"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "List of Digits",
        args = Seq("num"),
        description = "Push a list of the digits of #1 to the stack",
        typeSwitchable = false,
      ),
      Overload(
        name = "List of Characters",
        args = Seq("str"),
        description = "Push a list of the characters of #1 to the stack",
        typeSwitchable = false,
      ),
      Overload(
        name = "Flatten",
        args = Seq("lst"),
        description = "Flatten #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "g",
      keywords = Seq("min-of", "minimum-of"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Monadic Minimum",
        args = Seq("lst"),
        description = "Minimum of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "h",
      keywords = Seq("head", "first"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Head",
        args = Seq("any"),
        description = "First element of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "i",
      keywords = Seq(
        "index",
        "at",
        "item-at",
        "nth-item",
        "collect-unique",
        "enclose",
        "@<=",
      ),
      arity = 2,
      Options(
        vectorises = false
      ),
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
        typeSwitchable = false,
      ),
      Overload(
        name = "String Enclose",
        args = Seq("str", "str"),
        description =
          "enclose #2 in #1 (#1[0:len(#1)//2] + #2 + #1[len(#1)//2:])",
        typeSwitchable = false,
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
    Element(
      symbol = "j",
      keywords = Seq("join-on"),
      arity = 2,
      Options(
        vectorises = false
      ),
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
    ),
    Element(
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
      Options(
        vectorises = true
      ),
      Overload(
        name = "Logarithm",
        args = Seq("num", "num"),
        description = "Log base #2 of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Scan Fixpoint",
        args = Seq("fun", "any"),
        description = "Repeatedly apply #1 to #2 until it doesn't change",
        typeSwitchable = true,
      ),
      Overload(
        name = "Same Length",
        args = Seq("str", "str"),
        description = "Are #1 and #2 the same length",
        typeSwitchable = false,
      ),
      Overload(
        name = "String Length Equals",
        args = Seq("str", "num"),
        description = "Is the length of {#1|#2} equal to {#2|#1}",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "m",
      keywords =
        Seq("ctx-secondary", "ctx2", "ctx-m", "context-m", "context-secondary"),
      arity = 0,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Context Secondary",
        args = Seq(),
        description = "Push the secondary context variable to the stack",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "n",
      keywords =
        Seq("ctx-primary", "ctx", "ctx-n", "context-n", "context-primary"),
      arity = 0,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Context Primary",
        args = Seq(),
        description = "Push the primary context variable to the stack",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "o",
      keywords = Seq(
        "overlapping-pairs",
        "overlapping-sliding-window",
        "windows",
        "reduce-overlaps-by",
      ),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Windows",
        args = Seq("lst", "lst[num]"),
        description = "Get overlapping windows of #1 with a window of size #2",
        typeSwitchable = false,
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
        typeSwitchable = false,
      ),
      Overload(
        name = "Reduce Set-Sized Overlapping Slices",
        args = Seq("lst", "fun", "num"),
        description =
          "Reduce overlapping slices of length #3 in #1 by function #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "p",
      keywords = Seq("prepend"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Prepend",
        args = Seq("any", "any"),
        description = "Prepend #2 to #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "q",
      keywords = Seq("quotify"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Quotify",
        args = Seq("any"),
        description = "Cast #1 to a string and wrap in quotes",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "r",
      keywords = Seq("replace"),
      arity = 3,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Replace",
        args = Seq("nsl", "nsl", "nsl"),
        description = "Replace all occurrences of #2 in #1 with #3",
        typeSwitchable = false,
      ),
      Overload(
        name = "Zip-With",
        args = Seq("lst", "lst", "fun"),
        description = "Zip #1 and #2 and apply #3 to each pair",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "s",
      keywords = Seq("split"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Split",
        args = Seq("any", "any"),
        description = "Split #1 by #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "t",
      keywords = Seq("tail", "last"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Tail",
        args = Seq("any"),
        description = "Last element of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "u",
      keywords = Seq("unique"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Unique",
        args = Seq("lst"),
        description = "Unique elements of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Unique By Function",
        args = Seq("lst", "fun"),
        description = "Unique elements of #1 by applying #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "v",
      keywords = Seq("overlapping-pairs", "reduce-pairs-by"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Overlapping Pairs",
        args = Seq("lst"),
        description = "Get overlapping pairs of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Reduce Overlapping Pairs",
        args = Seq("lst", "fun"),
        description = "Reduce overlapping pairs in #1 by function #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "w",
      keywords = Seq("wrap-in-list"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Wrap in List",
        args = Seq("any"),
        description = "Wrap #1 in a list",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "x",
      keywords = Seq("recurse"),
      arity = -1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Recurse",
        args = Seq(),
        description =
          "Recursively call the current function (or the top-level program if not in a function)",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "y",
      keywords = Seq("transliterate", "call-while"),
      arity = 3,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Transliterate",
        args = Seq("nsl", "nsl", "nsl"),
        description = "Replace all occurrences of #2 in #1 with #3",
        typeSwitchable = false,
      ),
      Overload(
        name = "Call While",
        args = Seq("fun", "fun", "any"),
        description =
          "While #1(#3) is true, #3 = #2(#3). Return the result. Type switchable.",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "z",
      keywords = Seq("zip-with-filler"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Zip With Filler",
        args = Seq("lst", "any"),
        description = "Transpose #1, filling empty spaces with #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⨥",
      keywords = Seq("+2", "add-2", "++++", "inc-inc", "strlen==1"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Add 2",
        args = Seq("num"),
        description = "#1 + 2",
        typeSwitchable = false,
      ),
      Overload(
        name = "String Length Equals 1",
        args = Seq("str"),
        description = "Is the length of #1 equal to 1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⨪",
      keywords =
        Seq("-2", "subtract-2", "----", "dec-dec", "flip-bracket-palindrome"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Subtract 2",
        args = Seq("num"),
        description = "#1 - 2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Flip Bracket Palindrome",
        args = Seq("str"),
        description =
          "Palindromise #1 by appending the reverse with brackets and slashes flipped",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "∑",
      keywords = Seq("sum", "sum-of", "+/", "/+", "sigma", "sigma-in-ohio"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Sum",
        args = Seq("lst"),
        description = "Sum of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Join and Evaluate",
        args = Seq("lst[at least 1 str]"),
        description = "Join #1 and evaluate the result",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "Π",
      keywords = Seq("product", "product-of", "*/"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Product",
        args = Seq("lst"),
        description = "Product of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Number to Binary as String",
        args = Seq("num"),
        description = "Convert #1 to binary as a string",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "σ",
      keywords = Seq("cumulative-sums", "cumsums", "cumsum", "cum-sum", "-_-"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Cumulative Sums",
        args = Seq("lst"),
        description = "Cumulative sums of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⇧",
      keywords = Seq("grade-up"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Grade Up",
        args = Seq("lst"),
        description = "Indices that would sort #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⇩",
      keywords = Seq("grade-down"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Grade Down",
        args = Seq("lst"),
        description = "Indices that would sort #1 in reverse",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "∪",
      keywords = Seq("union", "set-union"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Union",
        args = Seq("lst", "lst"),
        description = "Union of #1 and #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "∩",
      keywords = Seq("intersection", "set-intersection"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Intersection",
        args = Seq("lst", "lst"),
        description = "Intersection of #1 and #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⊍",
      keywords = Seq("set-xor"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Set XOR",
        args = Seq("lst", "lst"),
        description = "Set XOR of #1 and #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⦰",
      keywords = Seq("set-difference", "set-diff"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Set Difference",
        args = Seq("lst", "lst"),
        description = "Set difference of #1 and #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "«",
      keywords = Seq("left-shift", "<<"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Left Shift",
        args = Seq("num", "num"),
        description = "#1 << #2",
        typeSwitchable = false,
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
        description =
          "Prepend spaces to string #1 until it is the length of #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "»",
      keywords = Seq("right-shift", ">>"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Right Shift",
        args = Seq("num", "num"),
        description = "#1 >> #2",
        typeSwitchable = false,
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
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "Ɠ",
      keywords = Seq("max-peek"),
      arity = 1,
      Options(
        vectorises = false,
        peeks = true,
      ),
      Overload(
        name = "Max Peek",
        args = Seq("lst"),
        description = "Maximum of #1 without popping",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ɠ",
      keywords = Seq("min-peek"),
      arity = 1,
      Options(
        vectorises = false,
        peeks = true,
      ),
      Overload(
        name = "Min Peek",
        args = Seq("lst"),
        description = "Minimum of #1 without popping",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "Ġ",
      keywords = Seq("zip-max", "max-dyad", "max-ab", "gen"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Zipped Maximum",
        args = Seq("lst", "lst"),
        description = "Maximum of corresponding elements of #1 and #2",
        typeSwitchable = false,
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
        typeSwitchable = false,
      ),
      Overload(
        name = "Generate Sequence",
        args = Seq("nls", "fun"),
        description =
          "Call {#2|#1} on previous results of {#2|#1}, starting with {#1|#2}.",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "ġ",
      keywords = Seq("zip-min", "min-dyad", "min-ab", "2gen"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Zipped Minimum",
        args = Seq("lst", "lst"),
        description = "Minimum of corresponding elements of #1 and #2",
        typeSwitchable = false,
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
        typeSwitchable = false,
      ),
      Overload(
        name = "Generate Sequence",
        args = Seq("nls", "fun"),
        description =
          "Call #2 as a dyad infinitely with items of #1 as starting values",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⌈",
      keywords = Seq("ceil", "ceiling", "split-on-spaces"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Ceiling",
        args = Seq("num"),
        description = "Ceiling of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Split on Spaces",
        args = Seq("str"),
        description = "Split #1 by spaces",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⌊",
      keywords = Seq("floor", "str-to-num"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Floor",
        args = Seq("num"),
        description = "Floor of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "String to Number",
        args = Seq("str"),
        description =
          "Convert #1 to a number, ignoring non-digit characters. Returns 0 if no digits are found",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⊖",
      keywords = Seq("0-slice", "take", "0-take"),
      arity = 2,
      Options(
        vectorises = false
      ),
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
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⌽",
      keywords = Seq("1-slice", "tail-take", "1-take"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "1 Slice",
        args = Seq("itr", "num"),
        description = "First {#2|#1} elements of {#1|#2}[1:]",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "£",
      keywords = Seq("set-register"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Set Register",
        args = Seq("any"),
        description = "Set the register to #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "¥",
      keywords = Seq("get-register"),
      arity = 0,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Get Register",
        args = Seq(),
        description = "Push the register to the stack",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "↜",
      keywords = Seq("rotate-stack-left"),
      arity = -1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Rotate Stack Left",
        args = Seq(),
        description = "Rotate the stack left",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "↝",
      keywords = Seq("rotate-stack-right"),
      arity = -1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Rotate Stack Right",
        args = Seq(),
        description = "Rotate the stack right",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "↺",
      keywords = Seq("rot-left"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Rotate Left",
        args = Seq("lst|str"),
        description = "Rotate #1 left",
        typeSwitchable = false,
      ),
      Overload(
        name = "Rotate Left",
        args = Seq("lst|str", "num"),
        description = "Rotate #1 left #2 times. Right if #2 is negative",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "↻",
      keywords = Seq("rot-right"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Rotate Right",
        args = Seq("lst|str"),
        description = "Rotate #1 right",
        typeSwitchable = false,
      ),
      Overload(
        name = "Rotate Right",
        args = Seq("lst|str", "num"),
        description = "Rotate #1 right #2 times. Left if #2 is negative",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "≜",
      keywords = Seq("assign"),
      arity = 3,
      Options(
        vectorises = false
      ),
      Overload(
        name = "List Assign",
        args = Seq("any", "num", "nsl"),
        description = "#1[#2] = #3",
        typeSwitchable = false,
      ),
      Overload(
        name = "Augmented List Assignment",
        args = Seq("any", "num", "fun"),
        description = "#1[#2] = #3(#1[#2])",
        typeSwitchable = false,
      ),
      Overload(
        name = "Vectorised Augmented List Assignment",
        args = Seq("lst", "lst[num]", "fun"),
        description = "#1[_] = #3(#1[_]) for _ in #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Zipped Assignment",
        args = Seq("lst", "lst", "lst"),
        description = "#1[ind] = val for ind, val in zip(#2, #3)",
        typeSwitchable = false,
      ),
      Overload(
        name = "Regex String Replacement",
        args = Seq("str", "str", "str"),
        description = "Replace all occurrences of #2 in #1 with #3",
        typeSwitchable = false,
      ),
      Overload(
        name = "Regex Substitution",
        args = Seq("str", "str", "fun"),
        description =
          "Replace all occurrences of #2 in #1 with the result of #3",
        typeSwitchable = false,
      ),
      Overload(
        name = "Object Member Assignment",
        args = Seq("obj", "str", "any"),
        description = "#1.#2 = #3",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⎀",
      keywords = Seq("insert"),
      arity = 3,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Insert",
        args = Seq("any", "num", "any"),
        description = "Insert #3 into #1 at index #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Insert",
        args = Seq("any", "lst[num]", "scl"),
        description = "Insert #3 into #1 at indices #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Insert",
        args = Seq("any", "lst[num]", "lst"),
        description = "Insert items of #3 into #1 at indices #2",
      ),
    ),
    Element(
      symbol = "◲",
      keywords = Seq("sublists"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Sublists",
        args = Seq("any"),
        description = "All sublists of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⊢",
      keywords = Seq("10-to-base", "all-regex-matches"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "10 to Base",
        args = Seq("num", "num"),
        description = "Convert #1 to base #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "10 to Base",
        args = Seq("num", "str|lst"),
        description = "Convert #1 to base len(#2) using the items of #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "10 to Base",
        args = Seq("lst", "num"),
        description = "Convert each item in #1 to base #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "10 to Base",
        args = Seq("lst", "lst"),
        description =
          "Convert each item in #1 to the base of the corresponding item in #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "All Regex Matches",
        args = Seq("str", "str"),
        description = "All matches of #2 in #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⊣",
      keywords = Seq("base-to-10"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Base to 10",
        args = Seq("scl", "num"),
        description =
          "Convert #1 from base #2 to base 10, assuming a base that is a prefix of [0-9A-Z] for strings",
        typeSwitchable = false,
      ),
      Overload(
        name = "Base to 10",
        args = Seq("lst[num|str]", "num"),
        description =
          "Convert #1 from base #2 to base 10, using the items of #1 as digits",
        typeSwitchable = false,
      ),
      Overload(
        name = "Base to 10",
        args = Seq("lst", "num"),
        description =
          "Convert each item in #1 from base #2 to base 10, assuming a base that is a prefix of [0-9A-Z] for strings",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ɦ",
      keywords = Seq("head-peek"),
      arity = 1,
      Options(
        vectorises = false,
        peeks = true,
      ),
      Overload(
        name = "Head Peek",
        args = Seq("lst"),
        description = "First element of #1 without popping",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ʈ",
      keywords = Seq("tail-peek"),
      arity = 1,
      Options(
        vectorises = false,
        peeks = true,
      ),
      Overload(
        name = "Tail Peek",
        args = Seq("lst"),
        description = "Last element of #1 without popping",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ᐐ",
      keywords = Seq("init"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Init",
        args = Seq("any"),
        description = "All but the last element of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ᐵ",
      keywords = Seq("drop"),
      arity = 2,
      Options(
        vectorises = false
      ),
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
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ᐕ",
      keywords = Seq("behead"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Behead",
        args = Seq("any"),
        description = "All but the first element of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "½",
      keywords = Seq("half", "halve"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Halve",
        args = Seq("num"),
        description = "#1 / 2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Two String Halves",
        args = Seq("str"),
        description = "Split #1 in half",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ƶ",
      keywords = Seq("range-to-length"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Range to Length",
        args = Seq("lst"),
        description = "Range from 0 to len(#1) - 1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "Ƶ",
      keywords = Seq("range-to-length-1"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Range to Length 1",
        args = Seq("lst"),
        description = "Range from 1 to len(#1)",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⁰",
      keywords = Seq("first-input", "input-0"),
      arity = 0,
      Options(
        vectorises = false
      ),
      Overload(
        name = "First Input",
        args = Seq(),
        description = "Push the first input to the stack",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "¹",
      keywords = Seq("second-input", "input-1"),
      arity = 0,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Second Input",
        args = Seq(),
        description = "Push the second input to the stack",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "²",
      keywords = Seq("square", "string-pairs"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Square",
        args = Seq("num"),
        description = "#1 ** 2",
        typeSwitchable = false,
      ),
      Overload(
        name = "String Pairs",
        args = Seq("str"),
        description = "Split #1 into pairs of characters",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "³",
      keywords = Seq("cube", "string-triples"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Cube",
        args = Seq("num"),
        description = "#1 ** 3",
        typeSwitchable = false,
      ),
      Overload(
        name = "String Triples",
        args = Seq("str"),
        description = "Split #1 into triples of characters",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⅟",
      keywords = Seq(
        "reciprocal",
        "inverse",
        "1/",
        "without-whitespace",
        "no-space",
        "spaceless",
      ),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Reciprocal",
        args = Seq("num"),
        description = "1 / #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Without Whitespace",
        args = Seq("str"),
        description = "Remove all whitespace from #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⇄",
      keywords = Seq("reverse"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Reverse",
        args = Seq("any"),
        description = "Reverse #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⧖",
      keywords = Seq("permutations", "map-over-permutations"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Permutations",
        args = Seq("any"),
        description = "All permutations of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Map Over Permutations",
        args = Seq("any", "fun"),
        description = "Map #2 over all permutations of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "‰",
      keywords = Seq("divmod"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Divmod",
        args = Seq("num", "num"),
        description = "Divmod of #1 and #2 ([#1 // #2, #1 % #2])",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "≛",
      keywords = Seq("divides?", "append-spaces", "regex-span"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Divides?",
        args = Seq("num", "num"),
        description = "#2 % #1 == 0",
        typeSwitchable = false,
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
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ℭ",
      keywords = Seq("combinations-with-replacement"),
      arity = 2,
      Options(
        vectorises = false
      ),
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
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "℈",
      keywords = Seq("combinations-without-replacement"),
      arity = 2,
      Options(
        vectorises = false
      ),
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
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⦷",
      keywords = Seq("abs", "absolute-value", "keep-letters"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Absolute Value",
        args = Seq("num"),
        description = "Absolute value of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Keep Letters",
        args = Seq("str"),
        description = "Keep only the letters of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "Ϣ",
      keywords = Seq("chunk-to-length", "partition-to-length"),
      arity = 2,
      Options(
        vectorises = false
      ),
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
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "≤",
      keywords = Seq("less-than-or-equal", "lte", "<="),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Less Than or Equal",
        args = Seq("scl", "scl"),
        description = "#1 <= #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "≥",
      keywords = Seq("greater-than-or-equal", "gte", ">="),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Greater Than or Equal",
        args = Seq("scl", "scl"),
        description = "#1 >= #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "≠",
      keywords = Seq("not-equal", "neq", "!=", "=n't", "eqn't", "equaln't"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Not Equal",
        args = Seq("scl", "scl"),
        description = "str(#1) != str(#2)",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "≡",
      keywords = Seq("exact-equals", "eq+", "==="),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Equals",
        args = Seq("any", "any"),
        description = "Does #1 exactly equal #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "•",
      keywords = Seq("dot-product", "bijective-base", "first-predicate-index"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Dot Product",
        args = Seq("lst", "lst"),
        description = "Dot product of #1 and #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Bijective Base Conversion",
        args = Seq("num", "num"),
        description = " Convert #1 to bijective base #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "First Index Where Predicate True",
        args = Seq("nsl", "fun"),
        description =
          "Index of the first value in {#1|#2} where function {#2|#1} is true",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "±",
      keywords = Seq("signum"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Signum",
        args = Seq("num"),
        description = "Sign of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "†",
      keywords = Seq("lengths-of-consecutives"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Lengths of Consecutives",
        args = Seq("lst"),
        description = "Lengths of consecutive runs of equal elements in #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⎙",
      keywords = Seq("peek-print"),
      arity = 1,
      Options(
        vectorises = false,
        peeks = true,
      ),
      Overload(
        name = "Peek Print",
        args = Seq("any"),
        description = "Print #1 without popping",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "✒",
      keywords = Seq("print"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Print",
        args = Seq("any"),
        description = "Print #1 without a trailing newline",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "≓",
      keywords = Seq("mirror"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Mirror",
        args = Seq("any"),
        description = "Mirror #1 (#1 + reverse(#1)), as the original type",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "Ͼ",
      keywords = Seq("vectorised-sums", "v/+"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Vectorised Sums",
        args = Seq("lst"),
        description = "Sum of each item in #1. Functionally equivalent to `¨Σ`",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ᴥ",
      keywords = Seq("exec", "10**", "call", "@"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "10 to the Power of",
        args = Seq("num"),
        description = "10 ** #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Execute",
        args = Seq("str"),
        description = "Execute #1 as Vyxal code",
        typeSwitchable = false,
      ),
      Overload(
        name = "Call Function",
        args = Seq("fun"),
        description = "Call function #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ℳ",
      keywords = Seq("modular", "matrix-multiply", "regex-full-match?"),
      arity = 2,
      Options(
        vectorises = false
      ),
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
        typeSwitchable = false,
      ),
      Overload(
        name = "Regex Full Match?",
        args = Seq("str", "str"),
        description = "Does pattern #2 fully match #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "℗",
      keywords = Seq("is-prime", "prime?", "quine-cheese"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Is Prime",
        args = Seq("num"),
        description = "Is #1 a prime number?",
        typeSwitchable = false,
      ),
      Overload(
        name = "Quine Cheese",
        args = Seq("str"),
        description =
          "Quotify #1 and prepend it to #1. (Useful for quines like `\"⌭\"⌭`)",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⍢",
      keywords = Seq("parity", "bit", "last-half"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Parity",
        args = Seq("num"),
        description = "Parity of #1 (1 if odd, 0 if even) --> #1 % 2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Last String Half",
        args = Seq("str"),
        description = "Last half of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ℂ",
      keywords = Seq("ncr", "choose", "characters-same?", "fixpoint-collect"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "NCR | N Choose R",
        args = Seq("num", "num"),
        description = "nCr of #1 and #2 (n choose r)",
        typeSwitchable = false,
      ),
      Overload(
        name = "Characters Same?",
        args = Seq("str", "str"),
        description = "Are all characters in #1 the same as #2?",
        typeSwitchable = false,
      ),
      Overload(
        name = "Fixpoint Collect",
        args = Seq("fun", "any"),
        description =
          "Repeatedly apply {#1|#2} on {#2|#1} until a fixed point is reached, collecting intermediate results",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "⌹",
      keywords = Seq("list-partitions", "integer-partitions"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Integers Partitions",
        args = Seq("num"),
        description = "All possible ways to sum positive integers to #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "List Partitions",
        args = Seq("itr"),
        description = "All possible ways to partition #1 into sublists",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⏚",
      keywords = Seq("powerset"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Powerset",
        args = Seq("any"),
        description = "Powerset of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "↯",
      keywords =
        Seq("inclusive-range", "sort-by", "regex-split-keep-delimiters"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Inclusive Range",
        args = Seq("num", "num"),
        description = "Inclusive range from #1 to #2",
        typeSwitchable = false,
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
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⊠",
      keywords = Seq("cartesian-power", "regex-index"),
      arity = 2,
      Options(
        vectorises = false
      ),
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
        typeSwitchable = false,
      ),
      Overload(
        name = "Self-Cartesian Power",
        args = Seq("itr", "any"),
        description =
          "Push #1, and then push the cartesian product of #2 with itself",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⚅",
      keywords = Seq("random-choice", "random-element", "randint", "random"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Random Choice",
        args = Seq("itr"),
        description = "Random element of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Random Integer",
        args = Seq("num"),
        description = "Random integer from 0 to #1",
        typeSwitchable = false,
      ),
    ),
    Element(
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
      ),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Bifurcate",
        args = Seq("any"),
        description = "Duplicate #1 and reverse the duplicate",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "␣",
      keywords = Seq("space"),
      arity = 0,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Space",
        args = Seq(),
        description = "Push a space to the stack",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "¶",
      keywords = Seq("newline"),
      arity = 0,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Newline",
        args = Seq(),
        description = "Push a newline to the stack",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "★",
      keywords = Seq("asterisk"),
      arity = 0,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Asterisk",
        args = Seq(),
        description = "Push an asterisk to the stack",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ᑂ",
      keywords = Seq("headless-top"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Head on Top, Rest on Bottom",
        args = Seq("any"),
        description = "Push #1[1:] and #1[0]",
      ),
    ),
    Element(
      symbol = "∻",
      keywords = Seq("integer-divide", "int-div", "//"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Integer Divide",
        args = Seq("num", "num"),
        description = "#1 // #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "√",
      keywords = Seq("square-root", "sqrt"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Square Root",
        args = Seq("num"),
        description = "Square root of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⍰",
      keywords = Seq("truthy?"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Truthy?",
        args = Seq("scl"),
        description = "Is #1 truthy? (Not 0, empty, or false)",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "◌",
      keywords = Seq("round"),
      arity = 1,
      Options(
        vectorises = true
      ),
      Overload(
        name = "Round",
        args = Seq("num"),
        description = "Round #1 to the nearest integer, half-up",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "δ",
      keywords = Seq("deltas", "differences"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Deltas",
        args = Seq("lst"),
        description =
          "Deltas/forward differences of #1 - [a - b, b - c, c - d, ...]",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "☷",
      keywords = Seq("partition-after-truthy"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Partition After Truthy",
        args = Seq("lst", "lst"),
        description = " Partition #1 after truthy indices of #2.",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "✇",
      keywords = Seq("edges", "ends", "real-imaginary"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Edges",
        args = Seq("itr"),
        description = "First and last element of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Real and Imaginary",
        args = Seq("num"),
        description = "Real and imaginary parts of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⎃",
      keywords = Seq("flatten-and-join-on-nothing"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Flatten and Join on Nothing",
        args = Seq("lst"),
        description = "Flatten #1 and join on nothing",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⎶",
      keywords = Seq("trim"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Trim",
        args = Seq("any", "any"),
        description = "Trim #1 of leading and trailing #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⊆",
      keywords = Seq("subset?"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Subset?",
        args = Seq("lst", "lst"),
        description =
          "Is the shallower list a subset of the deeper list? Checks windows corresponding to the length of the shallower list",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⍨",
      keywords = Seq("dump"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Dump",
        args = Seq("any"),
        description = "Push all items of #1 to the stack",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "γ",
      keywords = Seq("wrap-len-2"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Wrap to Length 2",
        args = Seq("any"),
        description = "Wrap #1 into chunks of length 2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⎘",
      keywords = Seq("flatten-by-depth", "flatten-depth"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Flatten by Depth",
        args = Seq("lst", "num"),
        description = "Flatten #1 by #2 levels",
        typeSwitchable = false,
      ),
      Overload(
        name = "Flatten by Depth",
        args = Seq("lst"),
        description = "Flatten #1 by 1 level",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ꜝ",
      keywords = Seq("keep-truthy"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Keep Truthy",
        args = Seq("lst"),
        description = "Keep only the truthy elements of #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "≈",
      keywords = Seq("all-same"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "All Same",
        args = Seq("any"),
        description = "Are all elements of #1 the same?",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "≊",
      keywords = Seq("all-equal-item"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "All Equal Item",
        args = Seq("lst", "any"),
        description = "Are all elements of #1 equal to #2?",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "κ",
      keywords = Seq("gcd"),
      arity = 2,
      Options(
        vectorises = true
      ),
      Overload(
        name = "GCD",
        args = Seq("num", "num"),
        description = "GCD of #1 and #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "GCD of List",
        args = Seq("lst"),
        description = "GCD of all elements of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "GCD of List with Initial Value",
        args = Seq("lst", "num"),
        description = "GCD of all elements of #1.append(#2)",
      ),
    ),
    Element(
      symbol = "↳",
      keywords = Seq("retrieve-from-outer"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Retrieve Item at Index from Outer Stack",
        args = Seq("num"),
        description =
          "Retrieve the item at index #1 from the outer stack, current stack if at top level",
        typeSwitchable = false,
      ),
      Overload(
        name = "Retrieve Item at Index from Outer Stack N-Layers Up",
        args = Seq("lst[num, num]"),
        description =
          "Retrieve the item at index #2 from the stack #1 levels up",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⤻",
      keywords = Seq("over"),
      arity = -1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Over",
        args = Seq(),
        description =
          "Duplicate the item below the top of the stack -> #2 #1 #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "⤺",
      keywords = Seq("around"),
      arity = -1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Around",
        args = Seq(),
        description =
          "Duplicate the top of the stack around the item below the top of the stack -> #1 #2 #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "↸",
      keywords = Seq("roll"),
      arity = 3,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Roll",
        args = Seq("any", "any", "any"),
        description = "#1 #2 #3 -> #3 #1 #2",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "”",
      keywords = Seq("join-on-newlines", "*newline", "one?->n"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Join on Newlines",
        args = Seq("lst"),
        description = "Join #1 on newlines",
        typeSwitchable = false,
      ),
      Overload(
        name = "Push Context Variable N if 1",
        args = Seq("num"),
        description = "Push the context variable N if #1 is 1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "„",
      keywords = Seq("join-on-spaces", "*space", "<0", "is-negative?"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Join on Spaces",
        args = Seq("lst"),
        description = "Join #1 on spaces",
        typeSwitchable = false,
      ),
      Overload(
        name = "Is negative?",
        args = Seq("num"),
        description = "Push 1 if #1 < 0, 0 otherwise",
      ),
    ),
    Element(
      symbol = "“",
      keywords = Seq(
        "join-on-empty-string",
        "*empty",
        "is-alphanumeric?",
        "insignificant?",
        "first-positive-integer",
        "first-n>0",
      ),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Join on Empty String",
        args = Seq("lst"),
        description = "Join #1 on the empty string",
        typeSwitchable = false,
      ),
      Overload(
        name = "Is alphanumeric?",
        args = Seq("str"),
        description = "Push 1 if #1 is alphanumeric, 0 otherwise",
        typeSwitchable = false,
      ),
      Overload(
        name = "First Positive Integer Where Function is Truthy",
        args = Seq("fun"),
        description = "Push the first positive integer where #1 is truthy",
        typeSwitchable = false,
      ),
      Overload(
        name = "Is Insignificant?",
        args = Seq("num"),
        description = "abs(#1) <= 1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "▲",
      keywords = Seq("mask"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Mask",
        args = Seq("any", "any"),
        description =
          "Keep elements of #1 where the corresponding element of #2 is truthy",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "Ṭ",
      keywords = Seq("truthy-indexes"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Truthy Indexes",
        args = Seq("lst"),
        description = "Indexes of truthy elements in #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "Ṫ",
      keywords = Seq("untruth"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Untruth",
        args = Seq("lst"),
        description = "Create a list of 1s at indices in #1, 0s elsewhere",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "Ŀ",
      keywords = Seq("vlen", "lengths"),
      arity = 1,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Vectorised Lengths",
        args = Seq("lst"),
        description = "Length of each element in #1",
        typeSwitchable = false,
      ),
    ),
  )

  val modifiers: Seq[Modifier] = List(
    Modifier(
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
    Modifier(
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
    Modifier(
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
    Modifier(
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
    Modifier(
      symbol = "⑴",
      keywords = Seq("*:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "Next Element as Lambda",
        args = Seq("any"),
        description = "Wrap #1 in a lambda and push it",
        example = "⑴+ = λ+}",
      ),
    ),
    Modifier(
      symbol = "⑵",
      keywords = Seq("**:"),
      numberOfElements = 2,
      ModifierOverload(
        name = "Next Two Elements as Lambda",
        args = Seq("any", "any"),
        description = "Wrap #1 and #2 in a lambda and push it",
        example = "⑵+* = λ+*}",
      ),
    ),
    Modifier(
      symbol = "⑶",
      keywords = Seq("***:"),
      numberOfElements = 3,
      ModifierOverload(
        name = "Next Three Elements as Lambda",
        args = Seq("any", "any", "any"),
        description = "Wrap #1, #2, and #3 in a lambda and push it",
        example = "⑶+*~ = λ+*~}",
      ),
    ),
    Modifier(
      symbol = "⑷",
      keywords = Seq("****:"),
      numberOfElements = 4,
      ModifierOverload(
        name = "Next Four Elements as Lambda",
        args = Seq("any", "any", "any", "any"),
        description = "Wrap #1, #2, #3, and #4 in a lambda and push it",
        example = "⑷+*~d = λ+*~d}",
      ),
    ),
    Modifier(
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
    Modifier(
      symbol = "⟒",
      keywords = Seq("left-fork:"),
      numberOfElements = 2,
      ModifierOverload(
        name = "Left Fork",
        args = Seq("dyd+", "dyd+"),
        description =
          "Apply #1 but keep the under stack, and then apply #2. Effectively #2(#1(top, under), under)",
        example = "3 4 ⟒+× -> 28",
      ),
    ),
    Modifier(
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
    Modifier(
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
    Modifier(
      symbol = "¨",
      keywords = Seq("each:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "Each",
        args = Seq("any"),
        description = "Map #1 over the top of the stack",
        example = "#[#[1|2|3#]|#[4|2|3#]|#[1|5|3#]#] ¨G -> [3, 4, 5]",
      ),
    ),
    Modifier(
      symbol = "Ẅ",
      keywords = Seq("zip-with:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "Zip With",
        args = Seq("dyd"),
        description = "Pop two lists and zip them, reducing each pair with #1",
        example = "#[1|2|3#] #[4|5|6#] ¨; -> [[1, 4], [2, 5], [3, 6]]",
      ),
    ),
    Modifier(
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
    Modifier(
      symbol = "ᖶ",
      keywords = Seq("if-else:"),
      numberOfElements = 2,
      ModifierOverload(
        name = "If Else",
        args = Seq("any", "any"),
        description =
          "If the top of the stack is truthy, apply #1, else apply #2",
        example = "3 1 ᖶd½ -> 6",
      ),
    ),
    Modifier(
      symbol = "⎇",
      keywords = Seq("dip:"),
      numberOfElements = 1,
      ModifierOverload(
        name = "Dip",
        args = Seq("mon"),
        description =
          "Save the top stack item, apply #1, then push the saved item",
        example = "3 4 5 2 ⎇+ -> 3 9 2",
      ),
    ),
    Modifier(
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
  )
end ElementInformation
