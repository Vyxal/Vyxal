package vyxal

import vyxal.cli.CLI

object Main:
  def main(args: Array[String]): Unit =

    Dictionary.fileInitialise()
    CLI.run(args)
