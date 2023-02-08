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
    val settings = Settings(printFn = onlinePrint)
    val globals = Globals(Inputs(inputs.split("\n").toIndexedSeq), settings)
    val ctx = Context(globals = globals)

    // for flag <- flags do cfg.copy(settings = cfg.settings.withFlag(flag))
    Interpreter.execute(code)(using ctx)

  def onlinePrint(text: Any): Unit =
    val output = document.getElementById("output")
    output.textContent += text.toString
end JSVyxal
