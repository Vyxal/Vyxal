package vyxal.gen

import vyxal.{Element, Elements, Modifiers, SugarMap}
import vyxal.parsing.Lexer

/** For generating elements.txt and trigraphs.txt. See build.sc */
private object GenerateDocs:
  def generate(): (String, String, String) =
    (elements(), trigraphs(), elementTable())

  def elements(): String =
    val sb = StringBuilder()
    Elements.elements.values.toSeq
      .sortBy { elem =>
        // Have to use tuple in case of digraphs
        (
          Lexer.Codepage.indexOf(elem.symbol.charAt(0)),
          Lexer.Codepage.indexOf(elem.symbol.substring(1)),
        )
      }
      .foreach {
        case Element(
              symbol,
              name,
              keywords,
              arity,
              vectorises,
              overloads,
              impl,
            ) =>
          sb ++=
            s"$symbol ($name) (${if vectorises then "" else "non-"}vectorising)\n"

          SugarMap.trigraphs
            .collect { case (tri, s) if s == symbol => tri }
            .foreach { tri => sb ++= s"Trigraph: $tri\n" }

          sb ++= s"Keywords:${keywords.mkString(" ", ", ", "")}\n"
          overloads.foreach { overload => sb ++= s"- $overload\n" }
          sb ++= "---------------------\n"
      }

    Modifiers.modifiers.foreach {
      case (name, info) =>
        sb ++= s"$name\n"
        sb ++= s"Keywords:${info.keywords.mkString(" ", ", ", "")}\n"
        sb ++= s"Description: ${info.description}\n"
        SugarMap.trigraphs
          .collect { case (tri, s) if s == name => tri }
          .foreach { tri => sb ++= s"Trigraph: $tri\n" }
        sb ++= "---------------------\n"
    }

    sb.toString
  end elements

  def trigraphs(): String =
    SugarMap.trigraphs
      .map { case (key, value) => s"$key -> $value" }
      .mkString("", "\n", "\n")

  def elementTable(): String =
    val header = "| Symbol | Name | Keywords | Arity | Vectorises | Overloads |"
    val divider = "| --- | --- | --- | --- | --- | --- |"
    val contents = StringBuilder()
    val formatOverload = (overload: String) =>
      val (args, description) = overload.splitAt(overload.indexOf("->"))
      val newDesc = description
        .replace("|", "\\|")
        .replace("->", "")
        .stripLeading()
        .stripTrailing()
      if args.stripTrailing() == "" then s"`$newDesc`"
      else
        s"`${args.stripTrailing().replace("|", "\\|").replace("->", "")}` => `$newDesc`"
    val addRow = (elem: Element) =>
      if !elem.symbol.startsWith("#|") then
        var overloads = elem.overloads
        contents ++=
          s"| `${"\\".repeat(if elem.symbol == "`" then 1 else 0) +
              elem.symbol.replace("|", "\\|")}` | ${elem.name.replace("|", "/")} | ${elem.keywords
              .map("`" + _ + "`")
              .mkString(", ")} | ${elem.arity.getOrElse("NA")} | ${if elem.vectorises then "✅"
            else "❌"} | ${formatOverload(overloads.head)}\n"
        overloads = overloads.tail
        while overloads.nonEmpty do
          contents ++= s"| | | | | | ${formatOverload(overloads.head)}\n"
          overloads = overloads.tail

    Elements.elements.values.toSeq
      .sortBy { elem =>
        // Have to use tuple in case of digraphs
        (
          Lexer.Codepage.indexOf(elem.symbol.charAt(0)),
          Lexer.Codepage.indexOf(elem.symbol.substring(1)),
        )
      }
      .foreach(addRow)

    header + "\n" + divider + "\n" + contents.toString
  end elementTable
end GenerateDocs
