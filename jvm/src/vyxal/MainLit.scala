package vyxal

/** This exists so we can generate binaries that only run literate mode */
object MainLit:
  def main(args: Array[String]): Unit =
    // Append the -l flag to the args
    Main.main(args :+ "--literate")
