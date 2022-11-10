package vyxal

object Interpreter {
  def execute(code: String)(using Context): Unit = {
    VyxalParser.parse(code) match {
      case Right(ast) =>
        println(ast)
        execute(ast)
      case Left(error) =>
        println(error)
        ???
    }
  }

  def execute(ast: AST)(using Context): Unit = {
    // todo implement
  }
}
