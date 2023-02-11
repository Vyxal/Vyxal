package vyxal

import vyxal.cli.CLI

import java.io.File

object Main:
  def main(args: Array[String]): Unit =
    CLI.run(args)
