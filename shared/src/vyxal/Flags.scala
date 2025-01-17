package vyxal

enum FlagCategory(val description: String) extends Enum[FlagCategory]:
  case EndPrintMode extends FlagCategory("End print mode")

object FlagCategory:
  val categories = Seq(EndPrintMode)

enum Flag(
    val short: Char,
    val long: String,
    val helpText: String,
    val description: String,
    val action: Settings => Settings,
    val category: Option[FlagCategory] = None,
    val hidden: Boolean = false,
) extends Enum[Flag]:
  case Trace
      extends Flag(
        'X',
        "trace",
        "Return full traceback on program error",
        "Full traceback",
        _.copy(fullTrace = true),
      )
  case Literate
      extends Flag(
        'l',
        "literate",
        "Enable literate mode",
        "Literate mode",
        _.copy(literate = true),
      )
  case InputAsStrings
      extends Flag(
        'Ṡ',
        "inputs-as-strs",
        "Treat all inputs as strings",
        "Don't evaluate inputs",
        _.copy(dontEvalInputs = true),
      )
  case LimitOutput
      extends Flag(
        '…',
        "limit-output",
        "Limit list output to the first 100 items of that list",
        "Limit list output",
        _.copy(limitPrint = true),
      )

  case PrintTop
      extends Flag(
        '\u0000',
        "",
        "Print the top of the stack",
        "Default behavior",
        Flag.setPrintMode(EndPrintMode.Default),
        Some(FlagCategory.EndPrintMode),
        hidden = true,
      )
  case PrintNone
      extends Flag(
        'O',
        "disable-implicit-output",
        "Disable implicit output",
        "No implicit output",
        Flag.setPrintMode(EndPrintMode.None),
        Some(FlagCategory.EndPrintMode),
      )
  case PrintForce
      extends Flag(
        'o',
        "force-implicit-output",
        "Force implicit output",
        "Force implicit output",
        Flag.setPrintMode(EndPrintMode.Force),
        Some(FlagCategory.EndPrintMode),
      )
  case PrintPretty
      extends Flag(
        '§',
        "print-pretty",
        "Pretty-print top of stack on end of execution",
        "Pretty-print top",
        Flag.setPrintMode(EndPrintMode.Pretty),
        Some(FlagCategory.EndPrintMode),
      )
  case PrintMax
      extends Flag(
        'G',
        "print-max",
        "Print the maximum item of the top of stack on end of execution",
        "Maximum of top",
        Flag.setPrintMode(EndPrintMode.Maximum),
        Some(FlagCategory.EndPrintMode),
      )
  case PrintMin
      extends Flag(
        'g',
        "print-min",
        "Print the minimum item of the top of the stack on end of execution",
        "Minimum of top",
        Flag.setPrintMode(EndPrintMode.Minimum),
        Some(FlagCategory.EndPrintMode),
      )
  case PrintNot
      extends Flag(
        '¬',
        "logical-not",
        "Logically negate the top of the stack on end of execution",
        "Logical negation of top",
        Flag.setPrintMode(EndPrintMode.LogicalNot),
        Some(FlagCategory.EndPrintMode),
      )
  case WrapStack
      extends Flag(
        'W',
        "wrap-stack",
        "Pop everything off the stack, wrap it in a list, and push that onto the stack",
        "Wrap stack",
        _.copy(wrapStack = true),
      )
end Flag

object Flag:
  /** Modify the given settings by applying all of the given flags */
  def applyFlags(flags: Seq[Flag], settings: Settings): Settings =
    flags.foldLeft(settings) { (settings, flag) => flag.action(settings) }

  /** Get the flag with the given short form */
  def from(short: Char): Flag =
    Flag.values.find(_.short == short) match
      case Some(flag) => flag
      case None => throw VyxalException(s"Invalid flag: '$short'")

  /** Helper to create flags that set end print mode */
  private def setPrintMode(mode: EndPrintMode)(settings: Settings) =
    settings.copy(endPrintMode = mode)
