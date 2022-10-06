object vyxal {
  def main(args: Array[String]) = {
    if (args.length == 0) {
      println("Usage: vyxal <file>")
      System.exit(1)

    }
    val fileLocation = args(0)
    var flags = ""
    var inputs: Array[String] = Array()
    if (args.length > 1) {
      flags = args(2)
      if (args.length > 2) {
        inputs = args.slice(3, args.length)
      } else {
        inputs = Array()
      }
    }

    println(fileLocation)
    println(flags)
    println(inputs.mkString("[ ", " | ", " ]"))
    println(Lexer(fileLocation))
  }
}
