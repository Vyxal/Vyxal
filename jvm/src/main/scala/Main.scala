package vyxal

import java.io.File

import scopt.OParser

/** Configuration for the command line argument parser
  *
  * @param file
  *   File to read code from (optional)
  * @param code
  *   Code to run (optional)
  * @param inputs
  *   Inputs to program (optional)
  */
case class Config(
    file: Option[File] = None,
    code: Option[String] = None,
    inputs: Seq[String] = Seq.empty,
    printDocs: Boolean = false
)

object Main {
  def main(args: Array[String]): Unit = {
    val builder = OParser.builder[Config]
    val parser = {
      import builder.*

      OParser.sequence(
        programName("vyxal"),
        head("vyxal", "3.1.1"),
        opt[File]("file")
          .action((file, cfg) => cfg.copy(file = Some(file)))
          .text("The file to read the program from")
          .optional(),
        opt[String]("code")
          .action((code, cfg) => cfg.copy(code = Some(code)))
          .text("Code to execute directly")
          .optional(),
        opt[Unit]("docs")
          .action((printDocs, cfg) => cfg.copy(printDocs = true))
          .text("Print documentation for elements and exit")
          .optional(),
        arg[String]("<input>...")
          .unbounded()
          .optional()
          .action((input, cfg) => cfg.copy(inputs = cfg.inputs :+ input))
          .text("Input to the program")
      )
    }

    OParser.parse(parser, args, Config()) match {
      case Some(config) =>
        given Context = Context()

        if (config.printDocs) {
          printDocs()
          return
        }

        config.file.foreach { file =>
          val source = io.Source.fromFile(config.file.get)
          try {
            Interpreter.execute(source.mkString)
          } finally {
            source.close()
          }
        }

        config.code.foreach { code =>
          Interpreter.execute(code)
        }

        if (config.file.nonEmpty || config.code.nonEmpty) {
          return
        }
      case None => ???
    }
  }

  private def printDocs(): Unit = {
    Elements.elements.values.foreach {
      case Element(symbol, name, arity, overloads, impl) =>
        println(s"$symbol ($name)\n")
        overloads.foreach{
          overload => println(s"- $overload")
        }
        println("-----------------------")
    }
  }
}
