/** For generating elements.txt and trigraphs.txt. See build.sc */
package vyxal.gen

import vyxal.{Modifiers, SugarMap}
import vyxal.elements.Element
import vyxal.elements.ElementInformation
import vyxal.elements.Overload
import vyxal.parsing.Codepage
import vyxal.Modifier
import vyxal.Syntax
import vyxal.SyntaxInfo

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

@main def generateDocs(
    elementsFile: String,
    trigraphsFile: String,
    tableFile: String,
) =
  Files.write(
    Paths.get(elementsFile),
    elementsMd(ElementInformation.elements).getBytes(StandardCharsets.UTF_8),
  )

def elementsMd(elements: Seq[Element]): String =
  val tableHeader =
    "| Symbol | Keywords | Arity | Vectorises | Peeks | Overloads |\n|---|---|---|---|---|---|"

  val tableRows = elements
    .map { element =>
      val symbol = element.symbol
      val keywords = element.keywords.mkString(", ")
      val arity = element.arity
      val vectorises = if element.options.vectorises then "Yes" else "No"
      val peeks = if element.options.peeks then "Yes" else "No"
      val overloads =
        element.overloads.map(overloadToMarkdown).mkString("<br/>")

      s"| $symbol | $keywords | $arity | $vectorises | $peeks | $overloads |"
    }
    .mkString("\n")

  s"$tableHeader\n$tableRows"
end elementsMd

private def overloadToMarkdown(overload: Overload): String =
  val args = overload.args.mkString(", ")
  val typeSwitchable =
    if overload.typeSwitchable then " (Type-Switchable)" else ""
  s"**${overload.name}**($args): ${overload.description}$typeSwitchable"
