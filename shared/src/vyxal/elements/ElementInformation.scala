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
          "Short circuit and - if {any1} is false, return {any1}, else return {any2}",
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
          "Short circuit or - if {any1} is true, return {any1}, else return {any2}",
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
  )
end ElementInformation
