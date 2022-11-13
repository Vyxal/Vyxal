package vyxal

import scala.io.StdIn

object Repl {
  def startRepl()(using ctx: Context): Unit = {
    while (true) {
      print("> ")

      val code = StdIn.readLine()

      println(code)

      Interpreter.execute(code)
    }
  }
}
