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

import os.copy.over

@main def generateDocs(
    elementsFile: String,
    trigraphsFile: String,
    tableFile: String,
) =
  Files.write(
    Paths.get(tableFile),
    genTable().getBytes(StandardCharsets.UTF_8),
  )

def genTable(): String =
  val HEADER_ROW = "| Symbol | Arity | Vectorises | Peeks | Overloads |" +
    "\n|--------|-------|------------|-------|-----------|"

  val elementMap = ElementInformation.elements

  val lines = elementMap.map { elem =>
    val symbol = elem.symbol
    val arity = elem.arity
    val vectorises = elem.options.vectorises
    val peeks = elem.options.peeks
    val overloads = elem.overloads.map(overloadToString)
    val overloadsFlat = overloads.foldLeft(Seq.empty[String]) {
      case (acc, s: String) => acc :+ s
      case (acc, s: Seq[String]) => acc ++ s
    }

    s"| $symbol | $arity | $vectorises | $peeks | ${overloadsFlat.mkString("</br>")} |"
  }

  (HEADER_ROW +: lines).mkString("\n")
end genTable

private def overloadToString(overload: Overload): Seq[String] | String =
  val description = overload.description
  if overload.typeSwitchable then
    // Extract all type switch templates
    val FIELD_REGEX = """\{((?:\\[\{\}|\\\\]|[^\{\}\\])*)\}""".r
    val fields = FIELD_REGEX.findAllMatchIn(description).map(_.group(1)).toSeq
    val components = FIELD_REGEX.split(description).toSeq
    val overloads = fields.map { field =>
      field.split("(?<!\\\\)\\|").toSeq.permutations.toSeq
    }
    overloads.map { arg =>
      val description =
        components.zip(arg).map { case (c, a) => c + a }.mkString
      s"${overload.name}: $description"
    }
  else s"${overload.name}: ${overload.description}"
end overloadToString
