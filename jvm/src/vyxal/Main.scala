package vyxal

object Main:
  def main(args: Array[String]): Unit =
    try
      CLI.run(args, JvmRepl)
    catch
      case ex: VyxalException => scribe.error(ex.getMessage(), if (args contains "--trace") ex.getStackTrace.mkString("\n") else "")
