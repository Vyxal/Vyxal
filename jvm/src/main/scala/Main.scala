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
  * @param printDocs
  *   Whether to print descriptions of all the elements
  * @param settings
  *   Extra settings passed on to the Context
  */
case class CLIConfig(
    file: Option[File] = None,
    code: Option[String] = None,
    inputs: List[String] = List.empty,
    printDocs: Boolean = false,
    printHelp: Boolean = false,
    settings: Settings = Settings()
)

object Main {
  def main(args: Array[String]): Unit = {
    OParser.parse(parser, args, CLIConfig()) match {
      case Some(config) =>
        given Context = Context(
          config.inputs.reverse.map(VyxalParser.parseInput)
        )

        if (config.printHelp) {
          println(OParser.usage(parser))
          return
        }

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
        } else {
          Repl.startRepl()
        }
      case None => ???
    }
  }

  private def printDocs(): Unit = {
    Elements.elements.values.foreach {
      case Element(symbol, name, arity, vectorises, overloads, impl) =>
        print(
          s"$symbol ($name) (${if (vectorises) "" else "non-"}vectorising)\n"
        )
        overloads.foreach { overload =>
          println(s"- $overload")
        }
        println("---------------------")
    }
  }

  private val builder = OParser.builder[CLIConfig]

  private val parser = {
    import builder.*

    def flag(short: Char, name: String, text: String) =
      opt[Unit](short, name)
        .action((_, cfg) => cfg.copy(settings = cfg.settings.withFlag(short)))
        .text(text)
        .optional()

    // todo come up with better names for the flags
    OParser.sequence(
      programName("vyxal"),
      head("vyxal", "3.1.1"),
      opt[Unit]('h', "help")
        .action((_, cfg) => cfg.copy(printHelp = true))
        .text("Print this help message and exit")
        .optional(),
      opt[File]('f', "file")
        .action((file, cfg) => cfg.copy(file = Some(file)))
        .text("The file to read the program from")
        .optional(),
      opt[String]('c', "code")
        .action((code, cfg) => cfg.copy(code = Some(code)))
        .text("Code to execute directly")
        .optional(),
      opt[Unit]('d', "docs")
        .action((_, cfg) => cfg.copy(printDocs = true))
        .text("Print documentation for elements and exit")
        .optional(),
      arg[String]("<input>...")
        .unbounded()
        .optional()
        .action((input, cfg) => cfg.copy(inputs = cfg.inputs :+ input))
        .text("Input to the program"),
      flag('H', "preset-100", "Preset stack to 100"),
      flag(
        'j',
        "print-join-newlines",
        "Print top of stack joined by newlines on end of execution"
      ),
      flag(
        'L',
        "print-join-newlines-vert",
        "Print top of stack joined by newlines (Vertically) on end of execution"
      ),
      flag(
        's',
        "print-sum",
        "Sum/concatenate top of stack on end of execution"
      ),
      flag(
        'M',
        "range-start-0",
        "Make implicit range generation and while loop counter start at 0 instead of 1"
      ),
      flag(
        'm',
        "range-end-excl",
        "Make implicit range generation end at n-1 instead of n"
      ),
      flag(
        'Ṁ',
        "range-programmery",
        "Equivalent to having both m and M flags"
      ),
      flag('v', "vyxal-enc", "Use Vyxal encoding for input file"),
      // todo output ASTs instead?
      flag('c', "output-compiled", "Output compiled code (for debugging)"),
      flag(
        'a',
        "newline-sep-as-list",
        "Treat newline seperated values as a list"
      ),
      flag(
        'd',
        "print-deep-sum",
        "Print deep sum of top of stack on end of execution"
      ),
      flag(
        'r',
        "reverse-ops",
        "Makes all operations happen with reverse arguments"
      ),
      flag(
        'S',
        "print-join-spaces",
        "Print top of stack joined by spaces on end of execution"
      ),
      flag(
        'C',
        "print-centre-join-newlines",
        "Centre the output and join on newlines on end of execution"
      ),
      flag('O', "disable-implicit-output", "Disable implicit output"),
      flag('o', "force-implicit-output", "Force implicit output"),
      flag(
        'l',
        "print-length",
        "Print length of top of stack on end of execution"
      ),
      flag(
        'G',
        "print-max",
        "Print the maximum item of the top of stack on end of execution"
      ),
      flag(
        'g',
        "print-max",
        "Print the minimum item of the top of the stack on end of execution"
      ),
      flag('W', "print-all", "Print the entire stack on end of execution"),
      flag('Ṡ', "inputs-as-strs", "Treat all inputs as strings"),
      flag(
        'R',
        "numbers-as-ranges",
        "Treat numbers as ranges if ever used as an iterable"
      ),
      flag(
        'D',
        "no-decompress-str",
        "Treat all strings as raw strings (don't decompress strings)"
      ),
      flag(
        'U',
        "strings-utf8",
        "Treat all strings as UTF-8 byte sequences (also don't decompress strings)"
      ),
      flag('Ṫ', "print-sum-all", "Print the sum of the entire stack"),
      flag(
        'ṡ',
        "print-all-join-spaces",
        "Print the entire stack, joined on spaces"
      ),
      flag(
        'Z',
        "zip-tetrad",
        "With four argument vectorization where all arguments are lists, use zip(zip(a, b), zip(c, d)) instead of zip(a, b, c, d)"
      ),
      flag(
        'J',
        "print-all-join-newlines",
        "Print the entire stack, separated by newlines"
      ),
      flag('t', "vect-boolify", "Vectorise boolify on Lists"),
      flag(
        'P',
        "print-lists-python",
        "Print lists as their python representation"
      ),
      flag('ḋ', "print-rat-decimal", "Print rationals in their decimal form"),
      flag('V', "var-single-char", "Variables are one character long"),
      flag(
        '?',
        "empty-as-0",
        "If there is empty input, treat it as 0 instead of empty string."
      ),
      flag('2', "arity-2", "Make the default arity of lambdas 2"),
      flag('3', "arity-3", "Make the default arity of lambdas 3"),
      flag('A', "run-tests", "Run test cases on all inputs"),
      flag(
        '…',
        "limit-output",
        "Limit list output to the first 100 items of that list"
      )
    )
  }
}
