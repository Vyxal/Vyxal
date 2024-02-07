package vyxal

enum FlagCategory(val description: String):
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
