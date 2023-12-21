package vyxal.gen

import vyxal.parsing.Lexer
import vyxal.Elements
import vyxal.Interpreter
import vyxal.Modifiers
import vyxal.SugarMap
import vyxal.SyntaxInfo

private object GenerateTheseusData:
  def generate(): String =
    val data = ujson.Obj(
      "elements" -> ujson.Arr(),
      "modifiers" -> ujson.Arr(),
      "syntax" -> ujson.Arr(),
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

    for
      (symbol, modifier) <- Modifiers.modifiers if !modifier._1.startsWith("#|")
    do
      val modifierData = ujson.Obj()
      modifierData("name") = modifier.name
      modifierData("symbol") = symbol
      modifierData("description") = modifier.description
      modifierData("keywords") = modifier.keywords.toList
      data("modifiers").arr.addOne(modifierData)

    for (symbol, syntax) <- SyntaxInfo.info do
      val syntaxData = ujson.Obj()
      syntaxData("name") = syntax.name
      syntaxData("symbol") = symbol
      syntaxData("description") = syntax.description
      syntaxData("usage") = syntax.usage
      data("syntax").arr.addOne(syntaxData)

    ujson.write(data)
  end generate
end GenerateTheseusData
