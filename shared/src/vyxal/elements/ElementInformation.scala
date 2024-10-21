package vyxal.elements

case class Element(
    symbol: String,
    name: String,
    keywords: Seq[String],
    arity: Option[Int],
    vectorises: Boolean,
    overloads: Seq[String],
)

object ElementInformation:
  def symbolFor(keyword: String): Option[String] =
    ElementInformation.elements.values
      .find(_.keywords.contains(keyword))
      .map(_.symbol)

  val elements: Map[String, Element] = Map(
  )
