package vyxal

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.JSConverters.*

/** A bridge between the interpreter and JS */
@JSExportTopLevel("Vyxal")
object JSVyxal:
  @JSExport
  def execute(code: String, inputs: String, flags: String): Unit =
    // todo take flags to set settings
    // todo take functions to print to custom stdout and stderr
    val output = document.getElementById("output")
    output.textContent = ""
    val settings = Settings(printFn = onlinePrint, online = true)
    val globals = Globals(
      settings = settings
    )
    val ctx = Context(
      inputs = inputs.split("\n").map(Parser.parseInput).toIndexedSeq,
      globals = globals
    )

    // println(ctx.globals.inputs.origArr.toList)

    for flag <- flags do settings.withFlag(flag)
    if flags.contains("l") then Interpreter.runLiterate(code)(using ctx)
    else Interpreter.execute(code)(using ctx)
  end execute

  def onlinePrint(text: Any): Unit =
    val output = document.getElementById("output")
    output.textContent += text.toString
end JSVyxal
