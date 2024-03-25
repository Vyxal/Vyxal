package vyxal

import vyxal.parsing.{Codepage, Lexer, Token}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.JSConverters.*

class JSToken(
    val tokenType: String,
    val value: String,
    val startOffset: Int,
    val endOffset: Int,
) extends js.Object
object JSToken:
  def apply(token: Token) =
    new JSToken(
      token.tokenType.name(),
      token.value,
      token.range.startOffset,
      token.range.endOffset,
    )

/** To avoid loading Scopt with the JSVyxal object */
@JSExportTopLevel("HelpText", moduleID = "helpText")
object HelpText:
  @JSExport
  def getHelpText(): String = CLI.helpText

/** A bridge between the interpreter and JS */
@JSExportTopLevel("Vyxal", moduleID = "vyxal")
object JSVyxal:
  @JSExport
  def execute(
      code: String,
      inputs: String,
      flags: String,
      printFunc: js.Function1[String, Unit],
      errorFunc: js.Function1[String, Unit],
  ): Unit =
    // todo take functions to print to custom stdout and stderr

    // The help flag should be handled in the JS
    if flags.contains('h') then return

    var printRequestCount = 0

    val settings =
      Flag.applyFlags(flags.map(Flag.from), Settings(online = true))

    val inputList = inputs
      .split("\n")
      .map(x =>
        if settings.dontEvalInputs then x
        else MiscHelpers.eval(x)(using Context())
      )
      .toSeq
      .reverse

    val globals: Globals = Globals(
      settings = settings,
      printFn = (str: String) =>
        if printRequestCount <= 20000 then
          printFunc(str)
          printRequestCount += 1,
    )
    globals.inputs = Inputs(inputList)

    val ctx = Context(
      inputs = inputList,
      globals = globals,
    )
    try Interpreter.execute(code)(using ctx)
    catch case ex: VyxalException => errorFunc(ex.getMessage(using ctx))
  end execute

  @JSExport
  def setShortDict(dict: String): Unit =
    Dictionary._shortDictionary = dict.split("\r\n").toSeq

  @JSExport
  def setLongDict(dict: String): Unit =
    Dictionary._longDictionary = dict.split("\r\n").toSeq

  @JSExport
  def compress(text: String): String = StringHelpers.compressDictionary(text)

  @JSExport
  def decompress(compressed: String): String =
    StringHelpers.decompress(compressed)

  /** Bridge to turn literate code into SBCS */
  @JSExport
  def getSBCSified(code: String): String =
    Lexer.sbcsify(Lexer.lexLiterate(code))

  @JSExport
  def getCodepage(): String = Codepage

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
            _,
          ) => js.Dynamic.literal(
          "symbol" -> symbol,
          "name" -> name,
          "keywords" -> keywords.toJSArray,
          "vectorises" -> vectorises,
          "overloads" -> overloads.toJSArray,
        )
    }.toJSArray

  @JSExport
  def getModifiers() =
    Modifiers.modifiers.map {
      case (symbol, info) => js.Dynamic.literal(
          "symbol" -> symbol,
          "name" -> info.name,
          "description" -> info.description,
          "keywords" -> info.keywords.toJSArray,
        )
    }.toJSArray

  @JSExport
  def getVersion(): String = Interpreter.version

  @JSExport
  def lexSBCS(code: String): js.Array[JSToken] =
    js.Array(Lexer.lexSBCS(code).map(JSToken(_))*)

  @JSExport
  def lexLiterate(code: String): js.Array[JSToken] =
    js.Array(Lexer.lexLiterate(code).map(JSToken(_))*)
end JSVyxal
