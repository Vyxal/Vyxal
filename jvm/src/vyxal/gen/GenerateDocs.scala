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
import scala.collection.mutable.ArrayBuffer
import scala.compiletime.ops.double

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
  val HEADER_ROW =
    "| Symbol | Keywords | Arity | Vectorises | Peeks | Overloads |" +
      "\n|--------|--|-------|------------|-------|-----------|"

  val elementMap = ElementInformation.elements

  val lines = elementMap.map { elem =>
    val symbol =
      if "`|<>\\".contains(elem.symbol) then s"\\${elem.symbol}"
      else elem.symbol
    val keywords = elem.keywords.map(kw => s"* `$kw`").mkString("</br>")
    val arity = if elem.arity == -1 then "STACK" else elem.arity
    val vectorises = if elem.options.vectorises then "🎶" else "🎵"
    val peeks = if elem.options.peeks then "👀" else "⛓️‍💥"
    val overloads = elem.overloads.map(overloadToString)
    val overloadsFlat = overloads.foldLeft(Seq.empty[String]) {
      case (acc, s: String) => acc :+ s
      case (acc, s: Seq[String]) => acc ++ s
    }

    s"| `$symbol` | $keywords | $arity | $vectorises | $peeks | ${overloadsFlat.mkString("</br>")} |"
  }

  (HEADER_ROW +: lines).mkString("\n")
end genTable

private def overloadToString(overload: Overload): Seq[String] | String =
  val description = overload.description
  if overload.typeSwitchable then
    // Extract all type switch templates
    val FIELD_REGEX = """\{((?:\\[\{\}|\\\\]|[^\{\}\\])*)\}""".r
    val fields = FIELD_REGEX.findAllMatchIn(description).map(_.group(1)).toSeq
    val fieldOptions = fields.map { field =>
      field.split("(?<!\\\\)\\|").toSeq
    } // A list of the options for each type switch field

    val descriptions = ArrayBuffer[String]()

    for (args, index) <- overload.args.permutations.zipWithIndex do
      val argsString = args.map(_.replace("|", "\\|")).mkString(",")
      val descriptionString = fields.zip(fieldOptions).foldLeft(description) {
        case (acc, (field, options)) =>
          acc.replace(s"{$field}", options(index % options.length))
      }
      descriptions +=
        s"**${overload.name}** (`$argsString`): $descriptionString"
    descriptions.toSeq
  else if overload.args.isEmpty then s"**${overload.name}**: $description"
  else
    s"**${overload.name}** (`${overload.args.map(_.replaceAll("|", "\\|")).mkString(",")}`): $description"
  end if
end overloadToString
