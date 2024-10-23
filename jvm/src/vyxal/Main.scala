package vyxal

object Main:
  def main(args: Array[String]): Unit =
  CLI.run(args, JvmRepl)
  /*
    try CLI.run(args, JvmRepl)
    catch
      case ex: VyxalException => scribe.error(
          ex.getMessage(),
          if args contains "--trace" then ex.getStackTrace.mkString("\n")
          else "",
        )
   */
