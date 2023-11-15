package vyxal.gen

import vyxal.{Element, Elements, Modifiers}
import vyxal.parsing.Lexer
import vyxal.Modifier

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

import upickle.default.*

private object GenerateKeyboard:
  def generate(): String = generateDescriptions()

  def generateDescriptions(): String =
    val data: scala.collection.mutable.HashMap[Int, ListBuffer[
      HashMap[String, String]
    ]] = HashMap()
    for
      (symbol, element) <- Elements.elements
      if Lexer.Codepage.contains(symbol.tail)
    do
      val token = symbol
      val index =
        if token == " " then 32 else Lexer.Codepage.indexOf(token.tail)

      val thisElement = scala.collection.mutable.HashMap[String, String]()
      thisElement("name") = element.name
      thisElement("description") = element.keywords.mkString(" ")
      thisElement("overloads") = element.overloads.mkString("\n")
      thisElement("token") = token

      if data.contains(index) then data(index) += thisElement
      else data(index) = ListBuffer(thisElement)

    for modifier <- Modifiers.modifiers do
      val (symbol, info) = modifier
      info match
        case Modifier(name, description, keywords, _) =>
          val token = symbol
          val index =
            if token == " " then 32 else Lexer.Codepage.indexOf(token.tail)
          val thisElement = scala.collection.mutable.HashMap[String, String]()
          thisElement("name") = name
          thisElement("description") = description
          thisElement("overloads") = keywords.mkString(" ")
          thisElement("token") = symbol

          if data.contains(index) then data(index) += thisElement
          else data(index) = ListBuffer(thisElement)

    upickle.default.write(data.toMap)
  end generateDescriptions
end GenerateKeyboard
