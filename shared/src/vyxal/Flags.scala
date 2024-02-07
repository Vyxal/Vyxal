package vyxal

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
  case Preset100
      extends Flag(
        'H',
        "preset-100",
        "Preset stack to 100",
        "Preset stack to 100",
        _.copy(presetStack = true),
      )
  case Literate
      extends Flag(
        'l',
        "literate",
        "Enable literate mode",
        "Literate mode",
        _.copy(literate = true),
      )
  case RangeNone
      extends Flag(
        '\u0000',
        "",
        "Default behavior",
        "Default behavior",
        settings => settings,
        Some(FlagCategory.RangeBehavior),
        hidden = true,
      )
  case RangeStart0
      extends Flag(
        'M',
        "range-start-0",
        "Make implicit range generation and while loop counter start at 0 instead of 1",
        "Start range at 0",
        _.copy(rangeStart = 0),
        Some(FlagCategory.RangeBehavior),
      )
  case RangeEndExcl
      extends Flag(
        'm',
        "range-end-excl",
        "Make implicit range generation end at n-1 instead of n",
        "End range at n-1",
        _.copy(rangeOffset = -1),
        Some(FlagCategory.RangeBehavior),
      )
  case RangeProgrammery
      extends Flag(
        'Ṁ',
        "range-programmery",
        "Equivalent to having both m and M flags",
        "Both",
        _.copy(rangeStart = 0, rangeOffset = -1),
        Some(FlagCategory.RangeBehavior),
      )
  case InputAsStrings
      extends Flag(
        'Ṡ',
        "inputs-as-strs",
        "Treat all inputs as strings",
        "Don't evaluate inputs",
        _.copy(dontEvalInputs = true),
      )
  case NumbersAsRanges
      extends Flag(
        'R',
        "numbers-as-ranges",
        "Treat numbers as ranges if ever used as an iterable",
        "Rangify",
        _.copy(rangify = true),
      )
  case Arity1
      extends Flag(
        '\u0000',
        "",
        "Make the default arity of lambdas 1",
        "1",
        _.copy(defaultArity = 1),
        Some(FlagCategory.DefaultArity),
        hidden = true,
      )
  case Arity2
      extends Flag(
        '2',
        "arity-2",
        "Make the default arity of lambdas 2",
        "2",
        _.copy(defaultArity = 2),
        Some(FlagCategory.DefaultArity),
      )
  case Arity3
      extends Flag(
        '3',
        "arity-3",
        "Make the default arity of lambdas 3",
        "3",
        _.copy(defaultArity = 3),
        Some(FlagCategory.DefaultArity),
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
  case PrintJoinNewlines
      extends Flag(
        'j',
        "print-join-newlines",
        "Print top of stack joined by newlines on end of execution",
        "Join top with newlines",
        Flag.setPrintMode(EndPrintMode.JoinNewlines),
        Some(FlagCategory.EndPrintMode),
      )
  case PrintSum
      extends Flag(
        's',
        "print-sum",
        "Sum/concatenate top of stack on end of execution",
        "Sum/concatenate top",
        Flag.setPrintMode(EndPrintMode.Sum),
        Some(FlagCategory.EndPrintMode),
      )
  case PrintDeepSum
      extends Flag(
        'd',
        "print-deep-sum",
        "Print deep sum of top of stack on end of execution",
        "Deep sum of top",
        Flag.setPrintMode(EndPrintMode.DeepSum),
        Some(FlagCategory.EndPrintMode),
      )
  case PrintJoinSpaces
      extends Flag(
        'S',
        "print-join-spaces",
        "Print top of stack joined by spaces on end of execution",
        "Join top with spaces",
        Flag.setPrintMode(EndPrintMode.JoinSpaces),
        Some(FlagCategory.EndPrintMode),
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
  case PrintLength
      extends Flag(
        'L',
        "print-length",
        "Print length of top of stack on end of execution",
        "Length of top",
        Flag.setPrintMode(EndPrintMode.Length),
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
