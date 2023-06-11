package vyxal

import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.TerminalBuilder
import org.jline.reader.EndOfFileException

object JvmRepl:
  def startRepl(literate: Boolean)(using Context): Unit =
    val terminal = TerminalBuilder
      .builder()
      .name("vyxal")
      .system(true)
      .build()
    val lineReader = LineReaderBuilder
      .builder()
      .terminal(terminal)
      .build()

    while true do
      try
        val code = lineReader.readLine("> ")
        Interpreter.execute(code, literate)
      catch
        case _: UserInterruptException =>
          println(
            s"Use Ctrl+D (on Unix) and Ctrl+Z followed by Enter (on Windows) to exit"
          )
        case _: EndOfFileException =>
          return
  end startRepl
end JvmRepl
