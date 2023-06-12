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
  def startRepl(literate: Boolean)(using ctx: Context): Unit =
    // Enable debug logging
    if ctx.settings.logLevel == LogLevel.Debug then
      Logger.getLogger("org.jline").setLevel(Level.FINER)

    AnsiConsole.systemInstall()

    val terminal = TerminalBuilder
      .builder()
      .name("vyxal")
      .jansi(true)
      .system(true)
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
            "Use Ctrl+D (on Unix) or Ctrl+Z followed by Enter (on Windows) to exit"
          )
        case _: EndOfFileException =>
          return
  end startRepl
end JvmRepl
