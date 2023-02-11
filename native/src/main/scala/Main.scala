package vyxal

import vyxal.cli.CLI

object Main:
  def main(args: Array[String]): Unit =
    if args.nonEmpty && args(0).charAt(0) != '-' then
      val source = io.Source.fromFile(args(0))
      CLI.run(args, Some(source.mkString))
    else CLI.run(args, None)
    end if
