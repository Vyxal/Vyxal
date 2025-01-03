package vyxal.gen

import vyxal.parsing.Codepage

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

@main def sortYAML(yamlLocation: String): Unit =
  val yaml = io.Source.fromFile(yamlLocation).mkString
  val lines = yaml.split("\n")
  // Read the first 4 lines into a string - these won't be sorted, but simply
  // readded

  val header = lines.take(4).mkString("\n")

  // Read the rest of the file into a map of string -> list of strings
  // Each block starts with a fully outdented `"<char>:"` line, and is followed
  // by an arbitrary number of lines that are indented by any number of spaces.
  // These following lines are the test cases for the block.

  val blocks = collection.mutable.Map[String, String]()
  var currentBlock: Seq[String] = Seq()
  var currentBlockName = ""
  for line <- lines.drop(4) do
    if line.startsWith("\"") then
      if currentBlockName.nonEmpty then
        blocks += currentBlockName -> currentBlock.mkString("\n")
      currentBlockName = line.drop(1).takeWhile(_ != '"')
      currentBlock = Seq()
    else currentBlock = currentBlock :+ line

  // Sort the blocks by their position in the Vyxal codepage
  val sortedBlocks = blocks.toSeq
    .filter { (symbol, _) =>
      if symbol.length() == 1 then Codepage.contains(symbol)
      else true
    }
    .sortBy { (symbol, _) =>
      if symbol.length == 1 then Codepage.indexOf(symbol)
      else Codepage.indexOf(symbol(1))
    }

  // Write the header and the sorted blocks to a new file

  Files.write(
    Paths.get(yamlLocation),
    (s"$header\n" +
      sortedBlocks
        .map { (symbol, tests) =>
          s"\"$symbol\":\n" + tests
        }
        .mkString("\n")).getBytes(StandardCharsets.UTF_8),
  )
end sortYAML
