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
      keywords = Seq("divide", "string-pieces", "regex-split", "/"),
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
        description = "Split [str] string into [num] pieces",
        typeSwitchable = true,
      ),
      Overload(
        name = "Regex Split",
        args = Seq("str", "str"),
        description = "Split a string by a regex",
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
        description = "lhs * rhs",
        typeSwitchable = false,
      ),
      Overload(
        name = "String Repeat",
        args = Seq("str", "num"),
        description = "Repeat [str] string [num] times",
        typeSwitchable = true,
      ),
      Overload(
        name = "Ring Translate",
        args = Seq("str", "str"),
        description = "Ring translate [str] according to b",
        typeSwitchable = false,
      ),
    ),
  )
end ElementInformation
