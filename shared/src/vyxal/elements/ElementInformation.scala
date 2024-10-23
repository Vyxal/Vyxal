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
)

case class Options(
    castToIterable: Boolean,
    vectorises: Boolean,
)

object ElementInformation:
  def symbolFor(keyword: String): Option[String] =
    ElementInformation.elements.values
      .find(_.keywords.contains(keyword))
      .map(_.symbol)

  val elements: Map[String, Element] = Map(
    "⊞" ->
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
        ),
      )
  )
end ElementInformation
