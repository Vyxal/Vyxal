package vyxal

import vyxal.gen.GenerateNanorc

import java.util.logging.Level
import java.util.logging.Logger
import scala.io.StdIn
import scala.jdk.CollectionConverters.*

import org.fusesource.jansi.AnsiConsole
import org.jline.builtins.SyntaxHighlighter
import org.jline.reader.impl.completer.StringsCompleter
import org.jline.reader.impl.DefaultHighlighter
import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.TerminalBuilder
import org.jline.widget.AutosuggestionWidgets

object JvmRepl extends Repl:

  override def startRepl(fancy: Boolean)(using ctx: Context): Unit =
    if fancy then fancyRepl() else plainRepl()

  private def plainRepl()(using ctx: Context): Unit =
    println("Starting plain repl...")
    while true do
      val code = StdIn.readLine("> ")
      if code == null then return
      ctx.globals.printed = false
      try Interpreter.execute(code)
      catch case ex: VyxalException => scribe.error(ex.getMessage(using ctx))

  private def fancyRepl()(using ctx: Context): Unit =
    // Enable debug logging
    if scribe.includes(scribe.Level.Debug) then
      Logger.getLogger("org.jline").setLevel(Level.FINE)
    else if scribe.includes(scribe.Level.Warn) then
      Logger.getLogger("org.jline").setLevel(Level.WARNING)

    AnsiConsole.systemInstall()

    val terminal =
      TerminalBuilder.builder().name("Vyxal").jansi(true).system(true).build()

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
        StringsCompleter(
          Elements.elements.values.flatMap(_.keywords).toArray*
        )
      )

    val lineReader = lineReaderBuilder.build()

    AutosuggestionWidgets(lineReader).enable()

    while true do
      try
        val code = lineReader.readLine("> ")
        ctx.globals.printed = false
        Interpreter.execute(code)
      catch
        case _: UserInterruptException => return
        case _: EndOfFileException => return
        case ex: VyxalException => scribe.error(ex.getMessage(using ctx))
  end fancyRepl
end JvmRepl
