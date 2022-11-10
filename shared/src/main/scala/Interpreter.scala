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
    println(s"Executing $ast")
    ast match {
      case AST.Empty =>
      case AST.Number(value) => ctx.push(value)
      case AST.Str(value)    => ctx.push(value)
      case AST.Lst(elems)    =>
        val list = collection.mutable.ListBuffer.empty[VAny]
        for (elem <- elems) {
          execute(elem)
          list += ctx.pop()
        }
        ctx.push(VList(list.toList*))
      case AST.Command(cmd)  => Elements.elements.get(cmd) match {
        case Some(elem) => elem.impl()
        case None => throw RuntimeException(s"No such command: '$cmd'")
      }
      case AST.Chain(head, tail) =>
        execute(head)
        execute(tail)
      case AST.If(thenBody, elseBody) =>
        if (MiscHelpers.boolify(ctx.pop())) {
          execute(thenBody)
        } else if (elseBody.nonEmpty) {
          execute(elseBody.get)
        }
      case AST.While(None, body) =>
        while (true) {
          execute(body)
        }
      case AST.While(Some(cond), body) =>
        execute(cond)
        while (MiscHelpers.boolify(ctx.pop())) {
          execute(body)
          execute(cond)
        }
    }
  }
}
