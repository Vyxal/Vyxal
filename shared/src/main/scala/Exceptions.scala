package vyxal

case class UnimplementedOverloadException(element: String, args: Seq[VAny])
    extends RuntimeException(
      s"$element not supported for input(s) ${args.mkString("(", ", ", ")")}"
    )

class QuitException extends RuntimeException("Program quit")
class ContinueLoopException
    extends RuntimeException("Continue loop") // Should never be unhandled
class BreakLoopException
    extends RuntimeException("Break loop") // Should never be unhandled
