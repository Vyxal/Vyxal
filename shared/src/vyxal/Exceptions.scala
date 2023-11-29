package vyxal

class VyxalException(message: String) extends RuntimeException(message)

case class UnimplementedOverloadException(element: String, args: Seq[VAny])
  extends VyxalException(s"$element not supported for input(s) ${args.mkString("[", ", ", "]")}")

/** Exception to end program using element Q */
class QuitException extends VyxalException("Program quit")

/** Exception to signal that a loop should continue. Should technically never be
  * unhandled.
  */
class ContinueLoopException
    extends VyxalException("Tried to continue outside of a loop context")

/** Exception to signal that a loop should break. Should technically never be
  * unhandled.
  */
class BreakLoopException
    extends VyxalException("Tried to break outside of a loop context")

/** Exception to signal that a function should return. Should technically never
  * be unhandled.
  */
class ReturnFromFunctionException
    extends VyxalException("Tried to return outside of a function context")

class RecursionError(message: String) extends VyxalException(message)
