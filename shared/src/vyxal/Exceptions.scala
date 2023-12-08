package vyxal

import vyxal.parsing.Token

class VyxalException(
    message: String,
    ex: Throwable = Exception(),
    unknown: Boolean = false,
    report: Boolean = false,
) extends RuntimeException(message, ex):
  def getMessage(using ctx: Context): String =
    var message = ex match
      case _: VyxalException => super.getCause().getMessage()
      case _: Throwable => super.getMessage()
    if report then message += "\nPlease report this to the Vyxal devs"
    if unknown && !report && !ctx.settings.fullTrace then
      message += "\nUse 'X' flag for full traceback"
    if ctx.settings.fullTrace || report then
      message += "\n" +
        super.getCause().getStackTrace.mkString("  ", "\n  ", "")
    message
class QuitException extends VyxalException("Program quit using Q")
class VyxalLexingException(message: String)
    extends VyxalException(s"LexingException: $message")
class VyxalParsingException(message: String)
    extends VyxalException(s"ParsingException: $message")
class VyxalRuntimeException(message: String)
    extends VyxalException(s"RuntimeException: $message")
class VyxalUnknownException(location: String, ex: Throwable)
    extends VyxalException(s"Unknown $location Exception", ex, true, true)

/** VyxalLexingExceptions */
class LeftoverCodeException(leftover: String)
    extends VyxalLexingException(
      s"Lexing completed with leftover code: '$leftover'"
    )

/** VyxalParsingExceptions */
class BadAugmentedAssignException()
    extends VyxalParsingException("Missing element for augmented assign")
class BadModifierException(modifier: String)
    extends VyxalParsingException(s"Modifier '$modifier' is missing arguments")
class BadStructureException(structure: String)
    extends VyxalParsingException(s"Invalid $structure statement")
class NoSuchElementException(token: Token)
    extends VyxalParsingException(s"No such element: ${token.value}")
class NoSuchModifierException(modifier: String)
    extends VyxalParsingException(s"No such modifier: ${modifier}")
class TokensFailedParsingException(tokens: List[Token])
    extends VyxalParsingException(s"Some elements failed to parse: $tokens")
class UnmatchedCloserException(closer: Token)
    extends VyxalParsingException(
      s"A closer/branch was found outside of a structure: ${closer.value}"
    )

/** VyxalRuntimeExceptions */
class InvalidLHSException(element: String, lhs: VAny, message: String)
    extends VyxalRuntimeException(
      s"Invalid LHS for $element: $lhs ($message)"
    )
class InvalidRHSException(element: String, rhs: VAny, message: String)
    extends VyxalRuntimeException(
      s"Invalid RHS for $element: $rhs ($message)"
    )
class RecursionError(message: String) extends VyxalRuntimeException(message)
class UnimplementedOverloadException(element: String, args: Seq[VAny])
    extends VyxalRuntimeException(
      s"$element not supported for input(s) ${args.mkString("[", ", ", "]")}"
    )

/** Unrecognized Exceptions */
class UnknownLexingException(ex: Throwable)
    extends VyxalUnknownException("Lexing", ex)
class UnknownParsingException(ex: Throwable)
    extends VyxalUnknownException("Parsing", ex)
class UnknownRuntimeException(ex: Throwable)
    extends VyxalUnknownException("Runtime", ex)

/** These exceptions should never be unhandled */
class ContinueLoopException
    extends VyxalException("Tried to continue outside of a loop context")
class BreakLoopException
    extends VyxalException("Tried to break outside of a loop context")
class ReturnFromFunctionException
    extends VyxalException("Tried to return outside of a function context")

/** This is for any errors that are caught, but should NEVER happen. If this
  * gets thrown, somebody done messed up
  */
class VyxalYikesException(message: String)
    extends VyxalException(s"Something is very yikes: $message", report = true)
