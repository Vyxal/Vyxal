package vyxal.gen

import vyxal.{Element, Elements, Modifiers}
import vyxal.parsing.Lexer
import vyxal.Modifier
import vyxal.Syntax
import vyxal.SyntaxInfo

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

import upickle.default.*

private object GenerateKeyboard:
  def generate(): String = generateDescriptions()

  def generateDescriptions(): String =
    val data: scala.collection.mutable.HashMap[Int, ListBuffer[
      Map[String, String]
    ]] = HashMap()
    for
      (symbol, element) <- Elements.elements
      if Lexer.Codepage.contains(symbol.last) && !symbol.startsWith("#|")
    do
      val token = symbol
      val index =
        if token == " " then 32 else Lexer.Codepage.indexOf(token.last)

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
          thisElement("description") =
            s"${literate.mkString(" ")}\n$description"
          thisElement("overloads") = usage
          thisElement("token") = symbol

          if data.contains(index) then data(index) += thisElement.toMap
          else data(index) = ListBuffer(thisElement.toMap)
    end for

    val finalData =
      scala.collection.mutable.HashMap[Int, List[Map[String, String]]]()
    for (index, elements) <- data do finalData(index) = elements.toList

    val escapedCodepage = Lexer.Codepage
      .replace("\\", "\\\\")
      .replace("\"", "\\\"")
      .replace("\n", "\\n")
    val header =
      s"var codepage = \"$escapedCodepage\";\nvar codepage_descriptions ="
    header + upickle.default.write(finalData.toMap)
  end generateDescriptions
end GenerateKeyboard
