package vyxal

import vyxal.debugger.DebugRepl
import vyxal.parsing.Codepage
import vyxal.parsing.Lexer
import vyxal.parsing.Parser

import scopt.OParser

trait Repl:
  /** @param fancy
    *   Whether the REPL is allowed to have colors, history, and other fancy
    *   stuff
    */
  def startRepl(fancy: Boolean)(using Context): Unit

object CLI:
  /** Configuration for the command line argument parser
    *
    * @param filename
    *   File to read code from (optional)
    * @param code
    *   Code to run (optional)
    * @param inputs
    *   Inputs to program (optional)
    * @param litInfoFor
    *   Element to print literate mode keywords for (optional)
    * @param settings
    *   Extra settings passed on to the Context
    */
  case class CLIConfig(
      filename: Option[String] = None,
      code: Option[String] = None,
      inputs: List[String] = List.empty,
      litInfoFor: Option[String] = None,
      printHelp: Boolean = false,
      runLexer: Boolean = false,
      runParser: Boolean = false,
      settings: Settings = Settings(),
      runLiterateLexer: Boolean = false,
      runFancyRepl: Boolean = false,
      debug: Boolean = false,
      readBytes: Boolean = false,
      runLiterateParser: Boolean = false,
  )

  /** Run the CLI
    *
    * @param args
    *   Command-line arguments
    * @param repl
    *   Function to start the REPL if requested
    */
  def run(args: Array[String], repl: Repl): Unit =
    for logLevel <- sys.env.get("VYXAL_LOG_LEVEL") do
      scribe.Level.get(logLevel) match
        case None => println(s"No such logging level: $logLevel")
        case Some(level) =>
          // Change the logging level
          scribe.Logger.root
            .clearHandlers()
            .clearModifiers()
            .withHandler(minimumLevel = Some(level))
            .replace()

    OParser.parse(parser, args, CLIConfig()) match
      case Some(config) =>
        val inputList = config.inputs.reverse.map(x =>
          if config.settings.dontEvalInputs then x
          else MiscHelpers.eval(x)(using Context())
        )
        given ctx: Context =
          Context(
            inputs = inputList,
            ctxArgs = Some(inputList),
            globals = Globals(settings = config.settings),
          )

        ctx.globals.inputs = ctx.inputs

        if config.printHelp then
          println(OParser.usage(parser))
          return

        if config.litInfoFor.nonEmpty then
          // val keywords = Lexer.literateModeMappings(config.litInfoFor.get)
          // println(keywords.mkString(", "))
          return

        if config.runLexer then
          while true do
            val line = io.StdIn.readLine(">")
            if line == null || line.isEmpty then return
            println(Lexer.lexSBCS(line))

        if config.runLiterateLexer then
          while true do
            val line = io.StdIn.readLine(">")
            if line == null || line.isEmpty then return
            println(Lexer.lexLiterate(line))
            println(Lexer.sbcsify(Lexer.lexLiterate(line)))

        if config.runParser then
          while true do
            val line = io.StdIn.readLine(">")
            if line.isEmpty then return
            println(Parser.parse(Lexer.lexSBCS(line)).ast)

        if config.runLiterateParser then
          while true do
            val line = io.StdIn.readLine(">")
            if line.isEmpty then return
            println(Parser.parse(Lexer.lexLiterate(line)).ast)

        if config.debug then
          val code = config.filename match
            case Some(file) =>
              val source = io.Source.fromFile(file)
              try source.mkString
              finally source.close()
            case None => config.code match
                case Some(code) => code
                case None => throw VyxalException(
                    "Either file name or code must be given to debug"
                  )
          DebugRepl.start(code)
        else if config.readBytes then
          config.filename.foreach { filename =>
            val fileObj = java.io.File(filename)
            val source = java.io.FileInputStream(fileObj)
            val sbcs = source.readAllBytes().map(c => Codepage(c & 0xff))
            try runCode(sbcs.mkString)
            finally source.close()
          }
          return
        else
          config.filename.foreach { filename =>
            val source = io.Source.fromFile(filename)
            try runCode(source.mkString)
            finally source.close()
          }

          config.code.foreach { code => runCode(code) }

          if config.filename.isEmpty && config.code.isEmpty then
            repl.startRepl(
              config.runFancyRepl
            )
        end if
      case None => ???
    end match
  end run

  def helpText = OParser.usage(parser)
  def version = Interpreter.version

  private def runCode(code: String)(using ctx: Context): Unit =
    try Interpreter.execute(code)
    catch case ex: VyxalException => println(ex.getMessage(using ctx))

  private val builder = OParser.builder[CLIConfig]

  private val parser =
    import builder.*

    // todo come up with better names for the flags
    OParser.sequence(
      programName("vyxal"),
      (Seq(
        head("vyxal", CLI.version),
        cmd("debug")
          .action((_, cfg) => cfg.copy(debug = true))
          .text("Run the debugger"),
        opt[Unit]('h', "help")
          .action((_, cfg) => cfg.copy(printHelp = true))
          .text("Print this help message and exit")
          .optional(),
        opt[String]("file")
          .action((file, cfg) => cfg.copy(filename = Some(file)))
          .text("The file to read the program from")
          .optional(),
        opt[String]("code")
          .action((code, cfg) => cfg.copy(code = Some(code)))
          .text("Code to execute directly")
          .optional(),
        opt[Int]("recursions")
          .action((limit, cfg) =>
            cfg.copy(settings = cfg.settings.copy(recursionLimit = limit))
          )
          .text("Set recursion limit (default 100)")
          .optional(),
        opt[String]("docs-literate")
          .action((symbol, cfg) => cfg.copy(litInfoFor = Some(symbol)))
          .text("Print literate mode mappings and exit")
          .optional(),
        opt[Unit]("lexer")
          .action((_, cfg) => cfg.copy(runLexer = true))
          .text("Run the lexer on input. For internal use.")
          .optional(),
        opt[Unit]("literate-lexer")
          .action((_, cfg) => cfg.copy(runLiterateLexer = true))
          .text("Run the literate lexer on input. For internal use.")
          .optional(),
        opt[Unit]("parser")
          .action((_, cfg) => cfg.copy(runParser = true))
          .text("Run the parser on input. For internal use.")
          .optional(),
        opt[Unit]("literate-parser")
          .action((_, cfg) => cfg.copy(runLiterateParser = true))
          .text(
            "Run the literate lexer and then parser on input. For internal use."
          )
          .optional(),
        opt[Unit]("fancy-repl")
          .action((_, cfg) => cfg.copy(runFancyRepl = true))
          .text("Run the fancy REPL")
          .optional(),
        opt[Unit]('v', "bytes")
          .action((_, cfg) => cfg.copy(readBytes = true))
          .text("Read program as raw bytes - used for code golf scoring")
          .optional(),
        arg[String]("<input>...")
          .unbounded()
          .optional()
          .action((input, cfg) => cfg.copy(inputs = cfg.inputs :+ input))
          .text("Input to the program"),
      ) ++
        Flag.values.filterNot(_.hidden).map { f =>
          opt[Unit](f.short, f.long)
            .action((_, cfg) => cfg.copy(settings = f.action(cfg.settings)))
            .text(f.helpText)
            .optional()
        })*
    )
  end parser
end CLI
