package vyxal.gen

import vyxal.parsing.Codepage
import vyxal.Elements
import vyxal.Flag
import vyxal.FlagCategory
import vyxal.Interpreter
import vyxal.Modifiers
import vyxal.SugarMap
import vyxal.SyntaxInfo

/** Generates the theseus.json data file for the New Vyxal 3 Web Interpreter™ */
private object GenerateTheseusData:
  def generate(): String =
    val data = ujson.Obj(
      "elements" -> ujson.Arr(),
      "modifiers" -> ujson.Arr(),
      "syntax" -> ujson.Arr(),
      "flags" -> ujson.Arr(),
      "sugars" -> SugarMap.trigraphs,
      "codepage" -> Codepage,
      "version" -> Interpreter.version,
    )
    for
      (symbol, element) <- Elements.elements
      if Codepage.contains(symbol.last) && !symbol.startsWith("#|")
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
  end generate
end GenerateTheseusData
