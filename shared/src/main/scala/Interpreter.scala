package vyxal

object Interpreter {
  def execute(code: String)(using ctx: Context): Unit = {
    VyxalParser.parse(code) match {
      case Right(ast) =>
        println(ast)
        execute(ast)
        // todo implicit output according to settings
        if (!ctx.isStackEmpty) {
          println(ctx.peek)
        }
      case Left(error) =>
        println(error)
        ???
    }
  }

  def execute(ast: AST)(using ctx: Context): Unit = {
    ast match {
      case AST.Number(value) => ctx.push(value)
      case AST.Str(value) => ctx.push(value)
      case AST.Command(cmd) => Elements.elements(cmd).impl()
      case AST.Chain(head, tail) =>
        execute(head)
        execute(tail)
      case _ => ???
    }
  }
}
