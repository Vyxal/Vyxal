package vyxal.gen

import vyxal.elements.{Element, ElementInformation, Modifier}
import vyxal.parsing.Codepage
import vyxal.Flag
import vyxal.FlagCategory
import vyxal.Interpreter
import vyxal.Syntax
import vyxal.SyntaxInfo

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.collection.mutable.{HashMap, ListBuffer}

import upickle.default.*

@main def generateTheseus(descriptionFile: String, dataFile: String) =
  Files.write(
    Paths.get(descriptionFile),
    generateDescriptions().getBytes(StandardCharsets.UTF_8),
  )
  Files.write(
    Paths.get(dataFile),
    generateData().getBytes(StandardCharsets.UTF_8),
  )

/** Generate keyboard data (parsed_yaml.js) */
def generateDescriptions(): String =
  val data = HashMap[Int, ListBuffer[Map[String, String]]]()
  for
    (symbol, element) <- ElementInformation.elements
    if Codepage.contains(symbol.last)
  do
    val token = symbol
    val index = if token == " " then 32 else Codepage.indexOf(token.last)

    val thisElement = HashMap[String, String]()
    thisElement("name") = element.overloads.map(_.name).mkString(" / ")
    thisElement("description") = element.keywords.mkString(" ")
    thisElement("overloads") = element.overloads
      .map(overload => DocsUtils.overloadToString(overload))
      .foldLeft(Seq.empty[String]) {
        case (acc, s: String) => acc :+ s
        case (acc, s: Seq[String]) => acc ++ s
      }
      .mkString("\n")
    thisElement("token") = token

    if data.contains(index) then data(index) += thisElement.toMap
    else data(index) = ListBuffer(thisElement.toMap)
  end for

  for modifier <- ElementInformation.modifiers do
    val (symbol, info) = modifier
    info match
      case Modifier(symbol, keywords, numberOfElements, overloads*) =>
        val token = symbol
        val index = if token == " " then 32 else Codepage.indexOf(token.last)
        val thisElement = HashMap[String, String]()
        val overloadsSeq = Seq(overloads*)
        thisElement("name") = overloadsSeq.map(_.name).mkString(" / ")
        thisElement("description") =
          overloadsSeq.map(_.description).mkString(" / ")
        thisElement("keywords") = keywords.mkString(" ")
        thisElement("overloads") = overloadsSeq
          .map(overload => DocsUtils.overloadToString(overload))
          .mkString("\n")
        thisElement("token") = symbol

        if data.contains(index) then data(index) += thisElement.toMap
        else data(index) = ListBuffer(thisElement.toMap)
    end match
  end for

  for syntax <- SyntaxInfo.info do
    val (symbol, info) = syntax
    info match
      case Syntax(name, literate, description, usage, _, _) =>
        val token = symbol
        val index = if token == " " then 32 else Codepage.indexOf(token.last)
        val thisElement = HashMap[String, String]()
        thisElement("name") = name
        thisElement("description") = s"${literate.mkString(" ")}\n$description"
        thisElement("overloads") = usage
        thisElement("token") = symbol

        if data.contains(index) then data(index) += thisElement.toMap
        else data(index) = ListBuffer(thisElement.toMap)

  val finalData = data.map(_ -> _.toList).toMap

  val escapedCodepage =
    Codepage.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
  val header =
    s"var codepage = \"$escapedCodepage\";\nvar codepage_descriptions ="

  header + write(finalData)
end generateDescriptions

/** Generate theseus data (theseus.json) */
def generateData(): String =
  val data = ujson.Obj(
    "elements" -> ujson.Arr(),
    "modifiers" -> ujson.Arr(),
    "syntax" -> ujson.Arr(),
    "flags" -> ujson.Arr(),
    "codepage" -> Codepage,
    "version" -> Interpreter.version,
  )
  for
    (symbol, element) <- ElementInformation.elements
    if Codepage.contains(symbol.last)
  do
    val elementData = ujson.Obj()
    elementData("name") = element.overloads.map(_.name).mkString(" / ")
    elementData("symbol") = element.symbol
    elementData("keywords") = element.keywords.toList
    elementData("overloads") = element.overloads
      .map(overload => DocsUtils.overloadToString(overload))
      .foldLeft(Seq.empty[String]) {
        case (acc, s: String) => acc :+ s
        case (acc, s: Seq[String]) => acc ++ s
      }
    elementData("vectorises") = element.options.vectorises
    data("elements").arr.addOne(elementData)

  for (symbol, modifier) <- ElementInformation.modifiers do
    val modifierData = ujson.Obj()
    modifierData("name") = modifier.overloads.map(_.name).mkString(" / ")
    modifierData("symbol") = symbol
    modifierData("description") =
      modifier.overloads.map(_.description).mkString(" / ")
    modifierData("keywords") = modifier.keywords.toList
    modifierData("overloads") =
      modifier.overloads.map(DocsUtils.overloadToString(_))
    data("modifiers").arr.addOne(modifierData)

  for (symbol, syntax) <- SyntaxInfo.info do
    val syntaxData = ujson.Obj()
    syntaxData("name") = syntax.name
    syntaxData("symbol") = symbol
    syntaxData("description") = syntax.description
    syntaxData("usage") = syntax.usage
    data("syntax").arr.addOne(syntaxData)

  for category <- FlagCategory.categories do
    val flagData = ujson.Obj()
    flagData("name") = category.description
    flagData("type") = "choice"
    // NOTE: the format does technically allow for the default flag to be non-empty,
    // making it required; not sure why we'd need to do that though
    flagData("default") = ""
    val choices = ujson.Obj()
    for flag <- Flag.values.filter(_.category == Some(category)) do
      choices(if flag.short == '\u0000' then "" else flag.short.toString) =
        flag.description
    flagData("choices") = choices
    data("flags").arr.addOne(flagData)

  for flag <- Flag.values.filter(_.category == None) do
    val flagData = ujson.Obj()
    flagData("name") = flag.description
    flagData("type") = "boolean"
    flagData("flag") =
      if flag.short == '\u0000' then "" else flag.short.toString
    data("flags").arr.addOne(flagData)

  ujson.write(data)
end generateData
