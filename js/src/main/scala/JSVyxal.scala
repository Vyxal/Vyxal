package vyxal

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.JSConverters.*

/** A bridge between the interpreter and JS */
@JSExportTopLevel("Vyxal")
object JSVyxal:
  @JSExport
  def execute(code: String, inputs: String): Unit =
    // todo take flags to set settings
    // todo take functions to print to custom stdout and stderr
    given Context = Context(inputs = inputs.split("\n").toIndexedSeq)
    Interpreter.execute(code)
