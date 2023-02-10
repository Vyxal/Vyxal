package vyxal.cli

import vyxal.{Context, Interpreter}

import scala.io.StdIn

object Repl:
  def startRepl()(using ctx: Context): Unit =
    while true do
      print("> ")

      val code = StdIn.readLine()
      Interpreter.execute(code)
