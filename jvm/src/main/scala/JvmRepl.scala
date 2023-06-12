package vyxal

import java.util.logging.Level
import java.util.logging.Logger

import org.fusesource.jansi.AnsiConsole
import org.jline.builtins.SyntaxHighlighter
import org.jline.reader.impl.DefaultHighlighter
import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.Size
import org.jline.terminal.TerminalBuilder
import org.jline.utils.AttributedString

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

    val highlighter = SyntaxHighlighter.build(
      getClass().getClassLoader().getResource(
        if literate then "vyxal-lit.nanorc" else "vyxal.nanorc"
      ).toString
    )

    val lineReader = LineReaderBuilder
      .builder()
      .terminal(terminal)
      .highlighter(
        new DefaultHighlighter:

          override def highlight(reader: LineReader, buffer: String) =
            highlighter.highlight(buffer)
      )
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
