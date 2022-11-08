object vyxal {
  def main(args: Array[String]) = {
    if (args.length == 0) {
      println("Usage: vyxal <file>")
      System.exit(1)
    }

    val fileLocation = args(0)
    val flags = if (args.length > 1) args(2) else ""
    val inputs: Array[String] =
      if (args.length > 2) args.slice(3, args.length)
      else Array()

    // println(fileLocation)
    // println(flags)
    // println(inputs.mkString("[ ", " | ", " ]"))
    println(Lexer(fileLocation))
    println(VyxalCompiler(fileLocation))
  }
}

object VyxalCompiler {
  def apply(code: String): AST = {
    // todo handle errors
    VyxalParser.parse(code).getOrElse(AST.Group(List.empty))
  }
}
