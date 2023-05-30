package vyxal

import vyxal.impls.*

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.JSConverters.*

/** A bridge between the interpreter and JS */
@JSExportTopLevel("Vyxal")
object JSVyxal:

  @JSExport
  def execute(
      code: String,
      inputs: String,
      flags: String,
      printFunc: js.Function1[String, Unit]
  ): Unit =
    // todo take functions to print to custom stdout and stderr
    val settings = Settings(online = true).withFlags(flags.toList)
    val globals = Globals(
      settings = settings,
      printFn = printFunc,
      inputs = Inputs(inputs.split("\n").map(Parser.parseInput).toSeq),
    )

    val ctx = Context(
      inputs = inputs.split("\n").map(Parser.parseInput).toIndexedSeq,
      globals = globals
    )
    Interpreter.execute(code, literate = flags.contains("l"))(using ctx)
  end execute

  @JSExport
  def compress(text: String): String = StringHelpers.compressDictionary(text)

  @JSExport
  def decompress(compressed: String): String =
    StringHelpers.decompress(compressed)

  /** Bridge to turn literate code into SBCS */
  @JSExport
  def getSBCSified(code: String): String =
    LiterateLexer.processLit(code).getOrElse(code)

  @JSExport
  def getCodepage(): String = vyxal.CODEPAGE

  @JSExport
  def getElements() =
    Elements.elements.values.map {
      case Element(
            symbol,
            name,
            keywords,
            _,
            vectorises,
            overloads,
            _
          ) =>
        js.Dynamic.literal(
          "symbol" -> symbol,
          "name" -> name,
          "keywords" -> keywords.toJSArray,
          "vectorises" -> vectorises,
          "overloads" -> overloads.toJSArray
        )
    }.toJSArray

  @JSExport
  def getModifiers() =
    Modifiers.modifiers.map { case (symbol, info) =>
      js.Dynamic.literal(
        "symbol" -> symbol,
        "name" -> info.name,
        "description" -> info.description,
        "keywords" -> info.keywords.toJSArray,
      )
    }.toJSArray

end JSVyxal
