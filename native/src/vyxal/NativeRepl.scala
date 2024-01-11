package vyxal

import scala.io.StdIn

object NativeRepl extends Repl:
  override def startRepl(fancy: Boolean)(using ctx: Context): Unit =
    while true do
      val code = StdIn.readLine("> ")
      ctx.globals.printed = false
      Interpreter.execute(code)
