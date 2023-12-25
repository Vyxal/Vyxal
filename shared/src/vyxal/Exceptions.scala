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
      if !ex.isInstanceOf[VyxalException] then message += "\n" + ex.getMessage()
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
class ModifierArityException(modifier: String, arity: Option[Int])
    extends VyxalParsingException(
      s"Modifier '$modifier' does not support elements of arity ${arity.getOrElse("None")}"
    )
class NoSuchElementException(element: String)
    extends VyxalParsingException(s"No such element: $element"):
  def this(token: Token) = this(token.value)
class TokensFailedParsingException(tokens: List[Token])
    extends VyxalParsingException(s"Some elements failed to parse: $tokens")
class UnmatchedCloserException(closer: Token)
    extends VyxalParsingException(
      s"A closer/branch was found outside of a structure: ${closer.value}"
    )
class UndefinedCustomModifierException(modifier: String)
    extends VyxalParsingException(s"Custom modifier '$modifier' not defined")

class UndefinedCustomElementException(element: String)
    extends VyxalParsingException(s"Custom element '$element' not defined")

class CustomModifierActuallyElementException(modifier: String)
    extends VyxalParsingException(
      s"Custom modifier '$modifier' is actually a custom element"
    )

class CustomElementActuallyModifierException(element: String)
    extends VyxalParsingException(
      s"Custom element '$element' is actually a custom modifier"
    )

class EmptyRedefine()
    extends VyxalParsingException(
      "Redefine statement is empty. Requires at least name and implementation."
    )

class BadRedefineMode(mode: String)
    extends VyxalParsingException(
      s"Invalid redefine mode: '$mode'. Should either be @ for element, or * for modifier"
    )

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
