package vyxal

import scala.io.StdIn

object NativeRepl extends Repl:
  override def startRepl()(using Context): Unit =
    while true do
      print("> ")

      val code = StdIn.readLine()
      Interpreter.execute(code)
