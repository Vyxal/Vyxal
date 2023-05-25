package vyxal

case class UnimplementedOverloadException(element: String, args: Seq[VAny])
    extends RuntimeException(
      s"$element not supported for input(s) ${args.mkString("(", ", ", ")")}"
    )

class QuitException extends RuntimeException("Program quit")
