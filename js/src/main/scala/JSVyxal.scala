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
    // cast output object to textarea
    val textarea = output.asInstanceOf[dom.html.TextArea]
    textarea.value = ""
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

  @JSExport
  def getSBCSified(code: String): Unit =
    val output = document.getElementById("output")
    // cast output object to textarea
    val textarea = output.asInstanceOf[dom.html.TextArea]
    textarea.value = litLex(code)

  def onlinePrint(text: Any): Unit =
    val output = document.getElementById("output")
    // cast output object to textarea
    val textarea = output.asInstanceOf[dom.html.TextArea]
    textarea.value += text.toString
end JSVyxal
