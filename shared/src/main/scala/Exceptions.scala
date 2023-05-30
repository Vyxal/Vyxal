package vyxal

case class UnimplementedOverloadException(element: String, args: Seq[VAny])
    extends RuntimeException(
      s"$element not supported for input(s) ${args.mkString("(", ", ", ")")}"
    )

class QuitException extends RuntimeException("Program quit")
class ContinueLoopException
    extends RuntimeException(
      "Tried to continue outside of a loop context"
    ) // Should technically never be unhandled
class BreakLoopException
    extends RuntimeException(
      "Tried to break outside of a loop context"
    ) // Should technically never be unhandled

class ReturnFromFunctionException
    extends RuntimeException("Tried to return outside of a function context")
