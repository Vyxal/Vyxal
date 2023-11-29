package vyxal

object MainLit:
  def main(args: Array[String]): Unit =
    // Append the -l flag to the args
    val newArgs = args :+ "--literate"
    try
      CLI.run(newArgs, JvmRepl)
    catch
      case ex: VyxalException => scribe.error(ex.getMessage(), if (args contains "--trace") ex.getStackTrace.mkString("\n") else "")
