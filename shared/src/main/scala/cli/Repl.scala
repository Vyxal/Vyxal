package vyxal.cli

import vyxal.{Context, Interpreter}

import scala.io.StdIn

object Repl:
  def startRepl(literate: Boolean = false)(using ctx: Context): Unit =
    while true do
      print("> ")

      val code = StdIn.readLine()
      if literate then Interpreter.runLiterate(code)
      else Interpreter.execute(code)
