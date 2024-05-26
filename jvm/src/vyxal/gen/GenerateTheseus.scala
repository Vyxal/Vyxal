package vyxal.gen

import vyxal.parsing.Lexer
import vyxal.Element
import vyxal.Elements
import vyxal.Flag
import vyxal.FlagCategory
import vyxal.Interpreter
import vyxal.Modifier
import vyxal.Modifiers
import vyxal.SugarMap
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
  val data: scala.collection.mutable.HashMap[Int, ListBuffer[
    Map[String, String]
  ]] = HashMap()
  for
    (symbol, element) <- Elements.elements
    if Lexer.Codepage.contains(symbol.last) && !symbol.startsWith("#|")
  do
    val token = symbol
    val index = if token == " " then 32 else Lexer.Codepage.indexOf(token.last)

    val thisElement = scala.collection.mutable.Map[String, String]()
    thisElement("name") = element.name
    thisElement("description") = element.keywords.mkString(" ")
    thisElement("overloads") = element.overloads.mkString("\n")
    thisElement("token") = token

    if data.contains(index) then data(index) += thisElement.toMap
    else data(index) = ListBuffer(thisElement.toMap)

  for modifier <- Modifiers.modifiers do
    val (symbol, info) = modifier
    info match
      case Modifier(name, description, keywords, _, overloads) =>
        val token = symbol
        val index =
          if token == " " then 32 else Lexer.Codepage.indexOf(token.last)
        val thisElement = scala.collection.mutable.HashMap[String, String]()
        thisElement("name") = name
        thisElement("description") = description
        thisElement("keywords") = keywords.mkString(" ")
        thisElement("overloads") = overloads.mkString("\n")
        thisElement("token") = symbol

        if data.contains(index) then data(index) += thisElement.toMap
        else data(index) = ListBuffer(thisElement.toMap)
  end for

  for syntax <- SyntaxInfo.info do
    val (symbol, info) = syntax
    info match
      case Syntax(name, literate, description, usage) =>
        val token = symbol
        val index =
          if token == " " then 32 else Lexer.Codepage.indexOf(token.last)
        val thisElement = scala.collection.mutable.HashMap[String, String]()
        thisElement("name") = name
        thisElement("description") = s"${literate.mkString(" ")}\n$description"
        thisElement("overloads") = usage
        thisElement("token") = symbol

        if data.contains(index) then data(index) += thisElement.toMap
        else data(index) = ListBuffer(thisElement.toMap)

  val finalData =
    scala.collection.mutable.HashMap[Int, List[Map[String, String]]]()
  for (index, elements) <- data do finalData(index) = elements.toList

  val escapedCodepage = Lexer.Codepage
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")
    .replace("\n", "\\n")
  val header =
    s"var codepage = \"$escapedCodepage\";\nvar codepage_descriptions ="
  header + write(finalData.toMap)
end generateDescriptions

/** Generate theseus data (theseus.json) */
def generateData(): String =
  val data = ujson.Obj(
    "elements" -> ujson.Arr(),
    "modifiers" -> ujson.Arr(),
    "syntax" -> ujson.Arr(),
    "flags" -> ujson.Arr(),
    "sugars" -> SugarMap.trigraphs,
    "codepage" -> Lexer.Codepage,
    "version" -> Interpreter.version,
  )
  for
    (symbol, element) <- Elements.elements
    if Lexer.Codepage.contains(symbol.last) && !symbol.startsWith("#|")
  do
    val elementData = ujson.Obj()
    elementData("name") = element.name
    elementData("symbol") = element.symbol
    elementData("keywords") = element.keywords.toList
    elementData("overloads") = element.overloads.toList
    elementData("vectorises") = element.vectorises
    data("elements").arr.addOne(elementData)

  for (symbol, modifier) <- Modifiers.modifiers do
    val modifierData = ujson.Obj()
    modifierData("name") = modifier.name
    modifierData("symbol") = symbol
    modifierData("description") = modifier.description
    modifierData("keywords") = modifier.keywords.toList
    modifierData("overloads") = modifier.overloads
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
