package vyxal

import vyxal.elements.Element
import vyxal.elements.ElementInformation
import vyxal.gen.DocsUtils
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
      inputs: js.Array[String],
      flags: String,
      printFunc: js.Function1[String, Unit],
      errorFunc: js.Function1[String, Unit],
  ): Unit =
    var printRequestCount = 0

    val settings =
      Flag.applyFlags(flags.map(Flag.from), Settings(online = true))

    val inputList = inputs
      .map(x =>
        if settings.dontEvalInputs then VStr(x)
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
    ElementInformation.elements.values.map {
      case Element(symbol, keywords, arity, options, overloads*) =>
        js.Dynamic.literal(
          "symbol" -> symbol,
          "name" -> overloads.map(_.name).mkString(" / "),
          "keywords" -> keywords.toJSArray,
          "vectorises" -> options.vectorises,
          "overloads" ->
            overloads
              .map(DocsUtils.overloadToString)
              .foldLeft(Seq.empty[String]) {
                case (acc, s: String) => acc :+ s
                case (acc, s: Seq[String]) => acc ++ s
              }
              .toJSArray,
        )
    }.toJSArray

  @JSExport
  def getModifiers() =
    ElementInformation.modifiers.map {
      case (symbol, info) => js.Dynamic.literal(
          "symbol" -> symbol,
          "name" -> info.overloads.map(_.name).mkString(" / "),
          "description" ->
            info.overloads.map(DocsUtils.overloadToString).toJSArray,
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
