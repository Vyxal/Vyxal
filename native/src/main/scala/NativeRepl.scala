package vyxal

import scala.io.StdIn

object NativeRepl:
  def startRepl(literate: Boolean)(using Context): Unit =
    while true do
      print("> ")

      val code = StdIn.readLine()
      Interpreter.execute(code, literate)
