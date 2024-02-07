package vyxal

import vyxal.debugger.DebugRepl
import vyxal.parsing.{Lexer, Parser}

import scopt.OParser

trait Repl:
  /** @param fancy
    *   Whether the REPL is allowed to have colors, history, and other fancy
    *   stuff
    */
  def startRepl(fancy: Boolean)(using Context): Unit

enum FlagCategory(val description: String) extends Enum[FlagCategory]:
  case RangeBehavior extends FlagCategory("Range behavior")
  case DefaultArity extends FlagCategory("Default arity")
  case EndPrintMode extends FlagCategory("End print mode")

object FlagCategory:
  val categories = Seq(RangeBehavior, DefaultArity, EndPrintMode)

enum Flag(
    val short: Char,
    val long: String,
    val helpText: String,
    val description: String,
    val category: Option[FlagCategory] = None,
    val hidden: Boolean = false,
) extends Enum[Flag]:
  case Trace
      extends Flag(
        'X',
        "trace",
        "Return full traceback on program error",
        "Full traceback",
      )
  case Preset100
      extends Flag(
        'H',
        "preset-100",
        "Preset stack to 100",
        "Preset stack to 100",
      )
  case Literate
      extends Flag('l', "literate", "Enable literate mode", "Literate mode")
  case RangeNone
      extends Flag(
        '\u0000',
        "",
        "Default behavior",
        "Default behavior",
        Some(FlagCategory.RangeBehavior),
        hidden = true,
      )
  case RangeStart0
      extends Flag(
        'M',
        "range-start-0",
        "Make implicit range generation and while loop counter start at 0 instead of 1",
        "Start range at 0",
        Some(FlagCategory.RangeBehavior),
      )
  case RangeEndExcl
      extends Flag(
        'm',
        "range-end-excl",
        "Make implicit range generation end at n-1 instead of n",
        "End range at n-1",
        Some(FlagCategory.RangeBehavior),
      )
  case RangeProgrammery
      extends Flag(
        'Ṁ',
        "range-programmery",
        "Equivalent to having both m and M flags",
        "Both",
        Some(FlagCategory.RangeBehavior),
      )
  case InputAsStrings
      extends Flag(
        'Ṡ',
        "inputs-as-strs",
        "Treat all inputs as strings",
        "Don't evaluate inputs",
      )
  case NumbersAsRanges
      extends Flag(
        'R',
        "numbers-as-ranges",
        "Treat numbers as ranges if ever used as an iterable",
        "Rangify",
      )
  case Arity1
      extends Flag(
        '\u0000',
        "",
        "Make the default arity of lambdas 1",
        "1",
        Some(FlagCategory.DefaultArity),
        hidden = true,
      )
  case Arity2
      extends Flag(
        '2',
        "arity-2",
        "Make the default arity of lambdas 2",
        "2",
        Some(FlagCategory.DefaultArity),
      )
  case Arity3
      extends Flag(
        '3',
        "arity-3",
        "Make the default arity of lambdas 3",
        "3",
        Some(FlagCategory.DefaultArity),
      )
  case LimitOutput
      extends Flag(
        '…',
        "limit-output",
        "Limit list output to the first 100 items of that list",
        "Limit list output",
      )

  case PrintTop
      extends Flag(
        '\u0000',
        "",
        "Print the top of the stack",
        "Default behavior",
        Some(FlagCategory.EndPrintMode),
        hidden = true,
      )
  case PrintJoinNewlines
      extends Flag(
        'j',
        "print-join-newlines",
        "Print top of stack joined by newlines on end of execution",
        "Join top with newlines",
        Some(FlagCategory.EndPrintMode),
      )
  case PrintSum
      extends Flag(
        's',
        "print-sum",
        "Sum/concatenate top of stack on end of execution",
        "Sum/concatenate top",
        Some(FlagCategory.EndPrintMode),
      )
  case PrintDeepSum
      extends Flag(
        'd',
        "print-deep-sum",
        "Print deep sum of top of stack on end of execution",
        "Deep sum of top",
        Some(FlagCategory.EndPrintMode),
      )
  case PrintJoinSpaces
      extends Flag(
        'S',
        "print-join-spaces",
        "Print top of stack joined by spaces on end of execution",
        "Join top with spaces",
        Some(FlagCategory.EndPrintMode),
      )
  case PrintNone
      extends Flag(
        'O',
        "disable-implicit-output",
        "Disable implicit output",
        "No implicit output",
        Some(FlagCategory.EndPrintMode),
      )
  case PrintForce
      extends Flag(
        'o',
        "force-implicit-output",
        "Force implicit output",
        "Force implicit output",
        Some(FlagCategory.EndPrintMode),
      )
  case PrintLength
      extends Flag(
        'L',
        "print-length",
        "Print length of top of stack on end of execution",
        "Length of top",
        Some(FlagCategory.EndPrintMode),
      )
  case PrintPretty
      extends Flag(
        '§',
        "print-pretty",
        "Pretty-print top of stack on end of execution",
        "Pretty-print top",
        Some(FlagCategory.EndPrintMode),
      )
  case PrintMax
      extends Flag(
        'G',
        "print-max",
        "Print the maximum item of the top of stack on end of execution",
        "Maximum of top",
        Some(FlagCategory.EndPrintMode),
      )
  case PrintMin
      extends Flag(
        'g',
        "print-min",
        "Print the minimum item of the top of the stack on end of execution",
        "Minimum of top",
        Some(FlagCategory.EndPrintMode),
      )
  case PrintStackLength
      extends Flag(
        '!',
        "print-stack-length",
        "Print the length of the stack on end of execution",
        "Length of stack",
        Some(FlagCategory.EndPrintMode),
      )
  case PrintNot
      extends Flag(
        '¬',
        "logical-not",
        "Logically negate the top of the stack on end of execution",
        "Logical negation of top",
        Some(FlagCategory.EndPrintMode),
      )
  case WrapStack
      extends Flag(
        'W',
        "wrap-stack",
        "Pop everything off the stack, wrap it in a list, and push that onto the stack",
        "Wrap stack",
        Some(FlagCategory.EndPrintMode),
      )
end Flag

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
          val keywords = Lexer.literateModeMappings(config.litInfoFor.get)
          println(keywords.mkString(", "))
          return

        if config.runLexer then
          while true do
            val line = io.StdIn.readLine(">")
            if line == null || line.isEmpty then return
            println(Lexer(line))

        if config.runLiterateLexer then
          while true do
            val line = io.StdIn.readLine(">")
            if line == null || line.isEmpty then return
            println(Lexer.lexLiterate(line))

        if config.runParser then
          while true do
            val line = io.StdIn.readLine(">")
            if line.isEmpty then return
            println(Parser.parse(Lexer(line)).ast)

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
            val sbcs = source.readAllBytes().map(c => Lexer.Codepage(c & 0xff))
            try runCode(sbcs.mkString)
            finally source.close()

          }
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
            .action((_, cfg) => cfg.copy(settings = cfg.settings.withFlag(f)))
            .text(f.helpText)
            .optional()
        })*
    )
  end parser
end CLI
