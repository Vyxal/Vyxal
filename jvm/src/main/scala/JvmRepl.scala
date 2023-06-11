package vyxal

import org.jline.reader.EndOfFileException
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.TerminalBuilder
import java.util.logging.Logger
import java.util.logging.Level

object JvmRepl:
  def startRepl(literate: Boolean)(using Context): Unit =
    // Enable debug logging
    Logger.getLogger("org.jline").setLevel(Level.FINE)

    val terminal = TerminalBuilder
      .builder()
      .name("vyxal")
      .system(true)
      // .streams(System.in, System.out)
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
            "Use Ctrl+D (on Unix) and Ctrl+Z followed by Enter (on Windows) to exit"
          )
        case _: EndOfFileException =>
          return
  end startRepl
end JvmRepl
