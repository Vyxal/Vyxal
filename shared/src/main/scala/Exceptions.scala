package vyxal

case class UnimplementedOverloadException(element: String, args: Seq[VAny])
    extends RuntimeException(
      s"$element not supported for input(s) ${args.mkString("(", ", ", ")")}"
    )

/** Exception to end program using element Q */
class QuitException extends RuntimeException("Program quit")

/** Exception to signal that a loop should continue. Should technically never be
  * unhandled.
  */
class ContinueLoopException
    extends RuntimeException(
      "Tried to continue outside of a loop context"
    )

/** Exception to signal that a loop should break. Should technically never be
  * unhandled.
  */
class BreakLoopException
    extends RuntimeException(
      "Tried to break outside of a loop context"
    ) // Should technically never be unhandled

/** Exception to signal that a function should return. Should technically never
  * be unhandled.
  */
class ReturnFromFunctionException
    extends RuntimeException("Tried to return outside of a function context")

class RecursionError(val message: String)
    extends RuntimeException(s"RecursionError: $message")
