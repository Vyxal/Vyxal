package vyxal

import vyxal.parsing.Token
import fastparse.Parsed

class VyxalException(message: String, ex: Throwable = Exception(), known: Boolean = true) extends RuntimeException(message, ex):
  def getMessage(using ctx: Context): String =
    if (ctx.settings.fullTrace)
      super.getMessage() + "\n" + super.getCause().getStackTrace.mkString("\n")
    else
      if (!known)
        super.getMessage() + ", use 'X' flag for full traceback"
      else
        super.getMessage()
class QuitException extends VyxalException("Program quit using Q")
class VyxalLexingException(message: String)
    extends VyxalException(s"LexingException: $message")
class VyxalParsingException(message: String)
    extends VyxalException(s"ParsingException: $message")
class VyxalRuntimeException(message: String)
    extends VyxalException(s"RuntimeException: $message")
class VyxalUnknownException(location: String, ex: Throwable)
    extends VyxalException(s"Unknown $location Exception", ex, false)

/** VyxalLexingExceptions */
class LeftoverCodeException(leftover: String)
    extends VyxalLexingException(
      s"Lexing completed with leftover code: '$leftover'"
    )

/** VyxalParsingExceptions */
class NoSuchElementException(token: Token)
    extends VyxalParsingException(s"No such element: ${token.value}")
class BadStructureException(structure: String)
    extends VyxalParsingException(s"Invalid $structure statement")
class NoSuchModifierException(modifier: String)
    extends VyxalParsingException(s"No such modifier: ${modifier}")
class BadModifierException(modifier: String)
    extends VyxalParsingException(s"Modifier '$modifier' is missing arguments")
class BadAugmentedAssignException()
    extends VyxalParsingException("Missing element for augmented assign")

/** VyxalRuntimeExceptions */
class UnimplementedOverloadException(element: String, args: Seq[VAny])
    extends VyxalRuntimeException(
      s"$element not supported for input(s) ${args.mkString("[", ", ", "]")}"
    )
class InvalidLHSException(element: String, lhs: VAny, message: String)
    extends VyxalRuntimeException(
      s"Invalid LHS for $element: $lhs ($message)"
    )
class InvalidRHSException(element: String, rhs: VAny, message: String)
    extends VyxalRuntimeException(
      s"Invalid RHS for $element: $rhs ($message)"
    )
class RecursionError(message: String) extends VyxalRuntimeException(message)

/** Unrecognized Exceptions */
// Due to the fastparse library, the lexing exception is a bit different
class UnknownLexingException(ex: Parsed.TracedFailure) extends VyxalLexingException(ex.longMsg)
class UnknownParsingException(ex: Throwable) extends VyxalUnknownException("Parsing", ex)
class UnknownRuntimeException(ex: Throwable) extends VyxalUnknownException("Runtime", ex)

/** These exceptions should never be unhandled */
class ContinueLoopException
    extends VyxalException("Tried to continue outside of a loop context")
class BreakLoopException
    extends VyxalException("Tried to break outside of a loop context")
class ReturnFromFunctionException
    extends VyxalException("Tried to return outside of a function context")
