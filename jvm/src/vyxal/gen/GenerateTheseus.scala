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

@main def generateTheseus(dataFile: String) =
  Files.write(
    Paths.get(dataFile),
    generateData().getBytes(StandardCharsets.UTF_8),
  )

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
