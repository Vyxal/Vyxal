package vyxal

import vyxal.gen.GenerateNanorc
import vyxal.impls.Elements

import java.util.logging.Level
import java.util.logging.Logger

import org.fusesource.jansi.AnsiConsole
import org.jline.builtins.SyntaxHighlighter
import org.jline.reader.impl.completer.StringsCompleter
import org.jline.reader.impl.DefaultHighlighter
import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.Size
import org.jline.terminal.TerminalBuilder
import org.jline.utils.AttributedString

object JvmRepl extends Repl:

  override def startRepl()(using ctx: Context): Unit =
    // Enable debug logging
    if ctx.settings.logLevel == LogLevel.Debug then
      Logger.getLogger("org.jline").setLevel(Level.FINER)

    AnsiConsole.systemInstall()

    // TODO: Probably refactor method chaining if it can be any order
    val terminal =
      if ctx.globals.useFancyRepl then
        TerminalBuilder
          .builder()
          .name("vyxal")
          .jansi(true)
          .system(true)
          .streams(System.in, System.out)
          .build()
      else
        TerminalBuilder
          .builder()
          .name("vyxal")
          .jansi(true)
          .system(false)
          .dumb(true)
          .streams(System.in, System.out)
          .build()

    val highlighter = SyntaxHighlighter.build(
      getClass()
        .getClassLoader()
        .getResource(
          if ctx.settings.literate then GenerateNanorc.LitNanorc
          else GenerateNanorc.SBCSNanorc
        )
        .toString
    )

    val lineReaderBuilder = LineReaderBuilder
      .builder()
      .terminal(terminal)
      .highlighter(
        new DefaultHighlighter:
          override def highlight(reader: LineReader, buffer: String) =
            highlighter.highlight(buffer)
      )

    if ctx.settings.literate then
      lineReaderBuilder.completer(
        new StringsCompleter(
          Elements.elements.values.flatMap(_.keywords).toArray*
        )
      )

    val lineReader = lineReaderBuilder.build()

    while true do
      try
        val code = lineReader.readLine("> ")
        Interpreter.execute(code)
      catch
        case _: UserInterruptException =>
          println(
            "Use Ctrl+D (on Unix) or Ctrl+Z followed by Enter (on Windows) to exit"
          )
        case _: EndOfFileException =>
          return
  end startRepl
end JvmRepl
