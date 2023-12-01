package vyxal

import vyxal.parsing.Token

class VyxalException(message: String) extends RuntimeException(message)
class VyxalLexingException(message: String)
    extends VyxalException(s"LexingException: $message")
class VyxalParsingException(message: String)
    extends VyxalException(s"ParsingException: $message")
class VyxalRuntimeException(message: String)
    extends VyxalException(s"RuntimeException: $message")

/** VyxalLexingExceptions */
class LeftoverCodeException(leftover: String) extends VyxalLexingException(s"Lexing completed with leftover code: '$leftover'")

/** VyxalParsingExceptions */
class NoSuchElementException(token: Token) extends VyxalParsingException(s"No such element: ${token.value}")
class BadStructureException(structure: String) extends VyxalParsingException(s"Invalid $structure statement")
class NoSuchModifierException(modifier: String) extends VyxalParsingException(s"No such modifier: ${modifier}")
class BadAugmentedAssignException() extends VyxalParsingException("Missing element for augmented assign")

/** VyxalRuntimeExceptions */
case class UnimplementedOverloadException(element: String, args: Seq[VAny])
    extends VyxalException(
      s"$element not supported for input(s) ${args.mkString("[", ", ", "]")}"
    )
class RecursionError(message: String) extends VyxalRuntimeException(message)


/** Exception to end program using element Q */
class QuitException extends VyxalException("Program quit")

/** These exceptions should never be unhandled */
class ContinueLoopException
    extends VyxalException("Tried to continue outside of a loop context")
class BreakLoopException
    extends VyxalException("Tried to break outside of a loop context")
class ReturnFromFunctionException
    extends VyxalException("Tried to return outside of a function context")
