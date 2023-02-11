package vyxal

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
    val settings = flags.foldLeft(Settings(online = true))(_.withFlag(_))
    val globals = Globals(
      settings = settings,
      printFn = printFunc
    )

    val ctx = Context(
      inputs = inputs.split("\n").map(Parser.parseInput).toIndexedSeq,
      globals = globals
    )
    if flags.contains("l") then Interpreter.runLiterate(code)(using ctx)
    else Interpreter.execute(code)(using ctx)
  end execute

  /** Bridge to turn literate code into SBCS */
  @JSExport
  def getSBCSified(code: String): String = litLex(code)

  @JSExport
  def getCodepage(): String = vyxal.CODEPAGE

end JSVyxal
