package vyxal

object MainLit:
  def main(args: Array[String]): Unit =
    // Append the -l flag to the args
    val newArgs = args :+ "--literate"
    CLI.run(newArgs, JvmRepl)
