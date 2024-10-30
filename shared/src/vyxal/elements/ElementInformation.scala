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
        description = "[{lst}.count(x) for x in set({lst})]",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "÷",
      keywords = Seq("divide", "string-pieces", "regex-split", "/"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Division",
        args = Seq("num", "num"),
        description = "{num} / {num}",
        typeSwitchable = false,
      ),
      Overload(
        name = "String into N Pieces",
        args = Seq("str", "num"),
        description = "Split {str} string into {num} pieces",
        typeSwitchable = true,
      ),
      Overload(
        name = "Regex Split",
        args = Seq("str", "str"),
        description = "Split {str} by regex {str}",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "×",
      keywords = Seq("multiply", "string-repeat", "ring-translate", "*"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Multiplication",
        args = Seq("num", "num"),
        description = "{num} * {num}",
        typeSwitchable = false,
      ),
      Overload(
        name = "String Repeat",
        args = Seq("str", "num"),
        description = "Repeat {str} string {num} times",
        typeSwitchable = true,
      ),
      Overload(
        name = "Ring Translate",
        args = Seq("str", "str"),
        description = "Ring translate {str} according to {str}",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "∧",
      keywords = Seq("and", "&&"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Short Circuit And",
        args = Seq("any", "any"),
        description =
          "Short circuit and - if {any2} is false, return {any2}, else return {any2}",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "∨",
      keywords = Seq("or", "!!"),
      arity = 2,
      Options(
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Short Circuit Or",
        args = Seq("any", "any"),
        description =
          "Short circuit or - if {any2} is true, return {any2}, else return {any1}",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "¬",
      keywords = Seq("not", "~"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = false,
      ),
      Overload(
        name = "Not",
        args = Seq("any"),
        description = "Not {any}",
        typeSwitchable = false,
      ),
    ),
    Element(
      symbol = "ʀ",
      keywords = Seq("0->n", "lowercase"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Range 0",
        args = Seq("num"),
        description = "Range from 0 to {num}, exclusive",
        typeSwitchable = false,
      ),
      Overload(
        name = "Lowercase",
        args = Seq("str"),
        description = "Lowercase {str}",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "ʁ",
      keywords = Seq("0->n++", "uppercase"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Range 0 Inclusive",
        args = Seq("num"),
        description = "Range from 0 to {num}, inclusive",
        typeSwitchable = false,
      ),
      Overload(
        name = "Uppercase",
        args = Seq("str"),
        description = "Uppercase {str}",
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
        description = "Range from 1 to {num}, inclusive",
        typeSwitchable = false,
      ),
      Overload(
        name = "Is Character Alphabetical",
        args = Seq("str"),
        description = "Check if {str} is alphabetical (i.e. is a letter)",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "‹",
      keywords = Seq("decrement", "--", "pad-to-8"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Decrement",
        args = Seq("num"),
        description = "{num} - 1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Pad to 8",
        args = Seq("str"),
        description = "Pad {str} to a length that is a multiple of 8 with '0's",
        typeSwitchable = true,
      ),
    ),
    Element(
      symbol = "›",
      keywords = Seq("increment", "++", "space-to-0"),
      arity = 1,
      Options(
        castToIterable = false,
        vectorises = true,
      ),
      Overload(
        name = "Increment",
        args = Seq("num"),
        description = "{num} + 1",
        typeSwitchable = false,
      ),
      Overload(
        name = "Spaces to 0s",
        args = Seq("str"),
        description = "Replace spaces in {str} with '0's",
        typeSwitchable = true,
      ),
    ),
  )
end ElementInformation
