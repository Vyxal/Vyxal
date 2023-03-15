package vyxal

import vyxal.cli.CLI

object Main:
  def main(args: Array[String]): Unit =

    Dictionary.initialise(
      readResource(ShortDictionaryFile),
      readResource(LongDictionaryFile)
    )
    CLI.run(args)

  private val ShortDictionaryFile = "/ShortDictionary.txt"
  private val LongDictionaryFile = "/LongDictionary.txt"
  private def readResource(path: String) =
    io.Source
      .fromInputStream(getClass.getResourceAsStream(path))
      .getLines()
      .toSeq
end Main
