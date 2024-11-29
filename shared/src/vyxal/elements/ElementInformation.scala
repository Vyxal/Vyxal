package vyxal.elements

case class Element(
    symbol: String,
    keywords: Seq[String],
    arity: Int,
    options: Options,
    overloads: Overload*
)

case class Overload(
    name: String,
    args: Seq[String],
    description: String,
    typeSwitchable: Boolean = false,
)

case class Options(
    castToIterable: Boolean,
    vectorises: Boolean,
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
        castToIterable = true,
        vectorises = false,
      ),
      Overload(
        name = "Counts of Items",
        args = Seq("lst"),
        description = "[lhs.count(x) for x in set(lhs)]",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "÷",
      keywords = Seq("divide", "string-pieces", "regex-split", "/", "div"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Division",
        args = Seq("num", "num"),
        description = "lhs / rhs",
        typeSwitchable = false,
      ),
      Overload(
        name = "String into N Pieces",
        args = Seq("str", "num"),
        description = "Split string {lhs|rhs} into {rhs|lhs} pieces",
        typeSwitchable = true,
      ),
      Overload(
        name = "Regex Split",
        args = Seq("str", "str"),
        description = "Split lhs by regex rhs",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "×",
      keywords =
        Seq("multiply", "string-repeat", "ring-translate", "*", "times"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Multiplication",
        args = Seq("num", "num"),
        description = "lhs * rhs (lhs times rhs)",
        typeSwitchable = false,
      ),
      Overload(
        name = "String Repeat",
        args = Seq("str", "num"),
        description = "Repeat string {lhs|rhs} {rhs|lhs} times",
        typeSwitchable = true,
      ),
      Overload(
        name = "Ring Translate",
        args = Seq("str", "str"),
        description = "Ring translate lhs according to rhs. ",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "∧",
      keywords = Seq("and", "&&", "logical-and"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Short Circuit And",
        args = Seq("any", "any"),
        description =
          "Short circuit and - if rhs is false, return rhs, else return lhs",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "∨",
      keywords = Seq("or", "!!", "logical-or"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Short Circuit Or",
        args = Seq("any", "any"),
        description =
          "Short circuit or - if rhs is true, return rhs, else return lhs",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "¬",
      keywords = Seq("not", "~", "logical-not"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Not",
        args = Seq("any"),
        description = "if lhs is truthy, return False, else return True",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ʀ",
      keywords = Seq("0->n", "lowercase", "range-0->n", "nrange-0"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Range 0",
        args = Seq("num"),
        description = "Range from 0 to lhs, exclusive",
        typeSwitchable = false,
      ),
      Overload(
        name = "Lowercase",
        args = Seq("str"),
        description = "Lowercase lhs",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "ʁ",
      keywords = Seq("0->n++", "uppercase", "range-0->n++", "n+range-0"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Range 0 Inclusive",
        args = Seq("num"),
        description = "Range from 0 to lhs, inclusive",
        typeSwitchable = false,
      ),
      Overload(
        name = "Uppercase",
        args = Seq("str"),
        description = "Uppercase lhs",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "ɾ",
      keywords = Seq("1->n++", "is-alpha?"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Range 1 Inclusive",
        args = Seq("num"),
        description = "Range from 1 to lhs, inclusive",
        typeSwitchable = false,
      ),
      Overload(
        name = "Is Character Alphabetical",
        args = Seq("str"),
        description = "Check if lhs is alphabetical (i.e. is a letter)",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "‹",
      keywords =
        Seq("decrement", "--", "pad-to-8", "dec", "pad-8", "pad-to-byte"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Decrement",
        args = Seq("num"),
        description = "lhs - 1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Pad to 8",
        args = Seq("str"),
        description = "Pad lhs to a length that is a multiple of 8 with '0's",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "›",
      keywords =
        Seq("increment", "++", "space-to-0", "replace-spaces-with-0s", "inc"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Increment",
        args = Seq("num"),
        description = "lhs + 1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Spaces to 0s",
        args = Seq("str"),
        description = "Replace spaces in lhs with '0's",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "!",
      keywords = Seq("factorial", "!", "titlecase", "fact", "title", "fac"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Factorial",
        args = Seq("num"),
        description = "Factorial of lhs",
        typeSwitchable = false,
      ),
      Overload(
        name = "Titlecase",
        args = Seq("str"),
        description = "Titlecase lhs",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "$",
      keywords = Seq("swap"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Swap",
        args = Seq("any", "any"),
        description = "Swap lhs and rhs on the stack: #1 #2 -> #2 #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "%",
      keywords = Seq("mod", "modulo", "%", "remainder"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Modulo",
        args = Seq("num", "num"),
        description = "lhs % rhs (remainder of lhs divided by rhs)",
        typeSwitchable = false,
      ),
      Overload(
        name = "String Format",
        args = Seq("str", "any"),
        description = "Format {lhs|rhs} with {rhs|lhs}",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "&",
      keywords = Seq("append"),
      arity = 2,
      Options(
        castToIterable = true,
        vectorises = false,
      ),
      Overload(
        name = "Append",
        args = Seq("any", "any"),
        description = "Append rhs to lhs",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "*",
      keywords = Seq("exponentiate", "pow", "**", "power"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Exponentiation",
        args = Seq("num", "num"),
        description = "lhs ** rhs",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "+",
      keywords = Seq("add", "+", "plus", "addition"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Addition",
        args = Seq("num", "num"),
        description = "lhs + rhs",
        typeSwitchable = false,
      ),
      Overload(
        name = "String and Number Concatenation",
        args = Seq("str", "num"),
        description = "{lhs|str(lhs)} + {str(rhs)|rhs}",
        typeSwitchable = true,
      ),
      Overload(
        name = "String Concatenation",
        args = Seq("str", "str"),
        description = "lhs + rhs",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = ",",
      keywords = Seq("println", "stdout", "output", "out"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Print",
        args = Seq("any"),
        description = "Print lhs to stdout, followed by a newline",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "-",
      keywords = Seq("subtract", "-", "minus", "subtraction", "regex-remove"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Subtraction",
        args = Seq("num", "num"),
        description = "lhs - rhs",
        typeSwitchable = false,
      ),
      Overload(
        name = "Prepend/Append Hyphens",
        args = Seq("str", "num"),
        description = "{lhs|'-' * lhs} + {rhs * '-'|rhs}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Regex Remove",
        args = Seq("str", "str"),
        description = "Remove matches of rhs from lhs",
        typeSwitchable = false,
      ),
    ),
    Element(
      ":",
      Seq("dup", "duplicate"),
      1,
      Options(
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Duplicate",
        args = Seq("any"),
        description = "Push lhs twice to the stack: #1 -> #1 #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = ";",
      keywords = Seq("pair", "cons"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Pair",
        args = Seq("any", "any"),
        description = "Push a list [lhs, rhs] to the stack: #1 #2 -> [#1, #2]",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "<",
      keywords = Seq("less-than", "<", "lt"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Less Than",
        args = Seq("scl", "scl"),
        description = "lhs < rhs",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "=",
      keywords = Seq("equals", "==", "eq"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Equals",
        args = Seq("scl", "scl"),
        description = "lhs == rhs",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = ">",
      keywords = Seq("greater-than", ">", "gt"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Greater Than",
        args = Seq("scl", "scl"),
        description = "lhs > rhs",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "?",
      keywords = Seq("stdin", "input", "in"),
      arity = 0,
      Options(
        castToIterable = false,
        vectorises = false,
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
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Absolute Difference",
        args = Seq("num", "num"),
        description = "Absolute difference between lhs and rhs",
        typeSwitchable = false,
      ),
      Overload(
        name = "Levenstein Distance",
        args = Seq("str", "str"),
        description = "Levenstein distance between lhs and rhs",
        typeSwitchable = false,
      ),
      Overload(
        name = "Reduce Overlapping Pairs",
        args = Seq("lst", "fun"),
        description =
          "Reduce overlapping pairs in {lhs|rhs} by function {rhs|lhs}",
      ),
    ),
    Element(
      symbol = "A",
      keywords = Seq("all", "all?", "vowel?", "is-vowel", "is-vowel?"),
      arity = 1,
      Options(
        castToIterable = true,
        vectorises = false,
      ),
      Overload(
        name = "All",
        args = Seq("any"),
        description = "Are all elements of lhs are truthy",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "B",
      keywords = Seq("to-binary"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "To Binary",
        args = Seq("num"),
        description = "Convert lhs to binary",
        typeSwitchable = false,
      ),
      Overload(
        name = "String to Binary",
        args = Seq("str"),
        description =
          "Convert each character in lhs to a binary representation of its unicode value",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "C",
      keywords = Seq("count"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Count",
        args = Seq("lst", "scl"),
        description = "Count occurrences of {rhs|lhs} in {lhs|rhs}",
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
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Triplicate",
        args = Seq("any"),
        description = "Push lhs thrice to the stack: #1 -> #1 #1 #1",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "E",
      keywords = Seq("2**n", "2pow", "eval", "2**"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "2 to the Power of N",
        args = Seq("num"),
        description = "2 ** lhs",
        typeSwitchable = false,
      ),
      Overload(
        name = "Eval",
        args = Seq("str"),
        description = "Evaluate lhs",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "F",
      keywords = Seq("filter", "find", "index-of"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Filter",
        args = Seq("fun", "any"),
        description = "Filter {lhs|rhs} by function {rhs|lhs}",
        typeSwitchable = true,
      ),
      Overload(
        name = "Find",
        args = Seq("nls", "nls"),
        description =
          "Find the index of lhs in rhs. Switches lhs and rhs so that the haystack is the deeper list",
      ),
    ),
    Element(
      symbol = "G",
      keywords = Seq("max", "maximum", "gen"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Dyadic Maximum",
        args = Seq("scl", "scl"),
        description = "Maximum of lhs and rhs",
        typeSwitchable = false,
      ),
      Overload(
        name = "Monadic Maximum",
        args = Seq("lst"),
        description = "Maximum of lhs",
        typeSwitchable = false,
      ),
      Overload(
        name = "Generate Sequence",
        args = Seq("nls", "fun"),
        description =
          "Call rhs on previous results of rhs, starting with lhs. If lhs is not a list, it is made iterable",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "H",
      keywords = Seq("to-hex", "from-hex"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "To Hex",
        args = Seq("num"),
        description = "Convert lhs to hexadecimal",
        typeSwitchable = false,
      ),
      Overload(
        name = "From Hex",
        args = Seq("str"),
        description =
          "Convert lhs from hexadecimal to a number. Inverse of 'to-hex'",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "I",
      keywords = Seq("interleave", "reject"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Interleave",
        args = Seq("any", "any"),
        description = "Interleave lhs and rhs",
        typeSwitchable = false,
      ),
      Overload(
        name = "Reject",
        args = Seq("any", "fun"),
        description =
          "Remove elements of {lhs|rhs} that satisfy function {rhs|lhs}",
        typeSwitchable = true,
      ),
    ),
  )
end ElementInformation
