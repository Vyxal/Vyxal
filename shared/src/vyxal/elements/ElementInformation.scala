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
    vectorises: Boolean
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
        name = "Short Circuit And",
        args = Seq("any", "any"),
        description =
          "Short circuit and - if #2 is false, return #2, else return #1",
        typeSwitchable = false,
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
        name = "Short Circuit Or",
        args = Seq("any", "any"),
        description =
          "Short circuit or - if #2 is true, return #2, else return #1",
        typeSwitchable = false,
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
      keywords = Seq("max", "maximum", "gen"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Dyadic Maximum",
        args = Seq("scl", "scl"),
        description = "Maximum of #1 and #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Monadic Maximum",
        args = Seq("lst"),
        description = "Maximum of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Generate Sequence",
        args = Seq("nls", "fun"),
        description =
          "Call #2 on previous results of #2, starting with #1. If #1 is not a list, it is made iterable",
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
      keywords = Seq("min", "minimum", "2gen"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Dyadic Minimum",
        args = Seq("scl", "scl"),
        description = "Minimum of #1 and #2",
        typeSwitchable = false,
      ),
      Overload(
        name = "Monadic Minimum",
        args = Seq("lst"),
        description = "Minimum of #1",
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
      keywords =
        Seq("overlapping-pairs", "overlapping-sliding-window", "windows"),
      arity = 2,
      Options(
        vectorises = false
      ),
      Overload(
        name = "Overlapping Pairs",
        args = Seq("lst|str"),
        description = "Get overlapping pairs of #1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Overlapping Pairs",
        args = Seq("any", "num"),
        description =
          "Get overlapping pairs of iterable(#1) with a window of size #2",
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
  )
end ElementInformation
