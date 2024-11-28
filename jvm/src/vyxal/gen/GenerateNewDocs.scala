package vyxal.gen

import vyxal.elements.Overload

private def generateTextForOverload(overload: Overload): String =
  // A regex that scans from a `{` to an unescaped `}`.
  val field_regex = "\\{(.*?(?<!\\\\))\\}".r

  // Extract all fields from the overload description, and remove the braces.
  val fields = field_regex
    .findAllIn(overload.description)
    .toList
    .map(_.drop(1).dropRight(1))

  val text = field_regex.split(overload.description)

  "TODO"
