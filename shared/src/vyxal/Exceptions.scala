package vyxal

class VyxalException private[vyxal] (
    message: String,
    ex: Throwable = null,
    unknown: Boolean = false,
    report: Boolean = false,
) extends RuntimeException(message, ex):
  def getMessage(using ctx: Context): String =
    var message = ex match
      case _: VyxalException => super.getCause().getMessage()
      case _ => super.getMessage()
    if report then message += "\nPlease report this to the Vyxal devs"
    if unknown && !report && !ctx.settings.fullTrace then
      message += "\nUse 'X' flag for full traceback"
    if ctx.settings.fullTrace || report then
      if !ex.isInstanceOf[VyxalException] then message += "\n" + ex.getMessage()
      message += "\n" +
        super.getCause().getStackTrace.mkString("  ", "\n  ", "")
    message

class QuitException extends VyxalException("Program quit using Q")
sealed class VyxalRuntimeException(message: String)
    extends VyxalException(s"RuntimeException: $message")
sealed class VyxalUnknownException(location: String, ex: Throwable)
    extends VyxalException(s"Unknown $location Exception", ex, true, true)

/** VyxalRuntimeExceptions */
class BadRegexException(regex: String)
    extends VyxalRuntimeException(s"Invalid regex syntax: /$regex/")
class ConstantAssignmentException(name: String)
    extends VyxalRuntimeException(s"Variable $name is constant")
class ConstantDuplicateException(name: String)
    extends VyxalRuntimeException(s"Constant $name already exists")
class InvalidCompressionCharException(char: Char)
    extends VyxalRuntimeException(s"Unable to compress character '$char'")
class InvalidListOverloadException(
    element: String,
    list: VList,
    expected: String,
) extends VyxalRuntimeException(
      s"List $list contains invalid values. Element $element expected $expected values"
    )
class BadLHSException(element: String, lhs: VAny)
    extends VyxalRuntimeException(s"Element $element received bad LHS: $lhs")

class BadRHSException(element: String, rhs: VAny)
    extends VyxalRuntimeException(s"Element $element received bad RHS: $rhs")

class NoDefaultException(value: VAny)
    extends VyxalRuntimeException(s"No default value exists for $value")
class UnimplementedOverloadException(element: String, args: Seq[VAny])
    extends VyxalRuntimeException(
      s"$element not supported for input(s) ${args.mkString("[", ", ", "]")}"
    )
class VyxalRecursionException()
    extends VyxalRuntimeException("Too many recursions")

class IterificationOfNonIterableException(value: VAny)
    extends VyxalRuntimeException(s"Cannot iterify $value")

class BadArgumentException(message: String, arg: VAny)
    extends VyxalRuntimeException(s"$message received bad argument: $arg")

/** Class related exceptions */

class FieldNotFoundException(className: String, fieldName: String)
    extends VyxalRuntimeException(s"Field $fieldName not found in $className")

class AttemptedReadPrivateException(className: String, fieldName: String)
    extends VyxalRuntimeException(
      s"Attempted to read private field $fieldName of $className outside of class"
    )

class AttemptedWritePrivateException(className: String, fieldName: String)
    extends VyxalRuntimeException(
      s"Attempted to write private field $fieldName of $className outside of class"
    )

class AttemptedWriteRestrictedException(className: String, fieldName: String)
    extends VyxalRuntimeException(
      s"Attempted to write restricted field $fieldName of $className outside of class"
    )

class ReservedClassNameException(className: String)
    extends VyxalRuntimeException(s"Class name $className is reserved")

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
