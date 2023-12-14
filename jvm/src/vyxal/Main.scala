package vyxal

object Main:
  def main(args: Array[String]): Unit =
    if args.contains("--fuzz") then
      Fuzz.fuzz(args(1).toInt, args(2).toInt)
    else
      try CLI.run(args, JvmRepl)
      catch
        case ex: VyxalException => scribe.error(
            ex.getMessage(),
            if args contains "--trace" then ex.getStackTrace.mkString("\n")
            else "",
          )
