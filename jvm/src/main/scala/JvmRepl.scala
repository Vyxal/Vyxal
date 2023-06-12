package vyxal

import java.util.logging.Level
import java.util.logging.Logger

import org.fusesource.jansi.AnsiConsole
import org.jline.reader.EndOfFileException
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.Size
import org.jline.terminal.TerminalBuilder

object JvmRepl:
  def startRepl(literate: Boolean)(using Context): Unit =
    // Enable debug logging
    Logger.getLogger("org.jline").setLevel(Level.FINER)

    // AnsiConsole.systemInstall()

    println(System.getProperty(TerminalBuilder.PROP_OUTPUT))
    println(System.getProperty(TerminalBuilder.PROP_OUTPUT_OUT))
    println(System.getProperty("org.jline.terminal.providers"))

    val terminal = TerminalBuilder
      .builder()
      .name("vyxal")
      .system(false)
      .jna(true)
      .jansi(true)
      .exec(true)
      .dumb(false)
      .size(Size())
      .streams(System.in, System.out)
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
