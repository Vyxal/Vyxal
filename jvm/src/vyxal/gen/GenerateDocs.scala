package vyxal.gen

import vyxal.{Element, Elements, Modifiers, SugarMap}
import vyxal.parsing.Lexer
import vyxal.Modifier
import vyxal.Syntax
import vyxal.SyntaxInfo

/** For generating elements.txt and trigraphs.txt. See build.sc */
private object GenerateDocs:
  def generate(): (String, String, String) =
    (elementsTxt(), trigraphs(), elementsMarkdown())

  /** Generate the text for elements.txt */
  def elementsTxt(): String =
    val sb = StringBuilder()
    Elements.elements.values.toSeq
      .sortBy { elem =>
        // Have to use tuple in case of digraphs
        (
          Lexer.Codepage.indexOf(elem.symbol.charAt(0)) +
            (if "#∆øÞ".contains(elem.symbol.charAt(0)) then 400 else 0),
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
          sb ++= "----------------------\n"
      }

    Modifiers.modifiers.foreach {
      case (name, info) =>
        sb ++= s"$name\n"
        sb ++= s"Keywords:${info.keywords.mkString(" ", ", ", "")}\n"
        sb ++= s"Description: ${info.description}\n"
        if info.overloads.nonEmpty then
          sb ++= info.overloads.mkString("", "\n", "\n")
        SugarMap.trigraphs
          .collect { case (tri, s) if s == name => tri }
          .foreach { tri => sb ++= s"Trigraph: $tri\n" }
        sb ++= "-----------------------\n"
    }

    sb.toString
  end elementsTxt

  def trigraphs(): String =
    SugarMap.trigraphs
      .map { case (key, value) => s"$key -> $value" }
      .mkString("", "\n", "\n")

  /** Prepare some text to be put into a Markdown table */
  private def sanitizeTable(text: String): String =
    text.replace("\\", "\\\\").replace("|", raw"\|")

  /** Generate the Markdown for table.md */
  def elementsMarkdown(): String =
    val formatOverload = (overload: String) =>
      overload.split("->", 2) match
        case Array(description) => s"<code>$description</code>"
        case Array(args, description) =>
          s"<code>$args</code> => <code>$description</code>"

    val elements = Elements.elements.values.toSeq
      .filter(!_.symbol.startsWith("#|"))
      .sortBy { elem =>
        // Have to use tuple in case of digraphs
        (
          Lexer.Codepage.indexOf(elem.symbol.charAt(0)) +
            (if "#∆øÞ".contains(elem.symbol.charAt(0)) then 400 else 0),
          Lexer.Codepage.indexOf(elem.symbol.substring(1)),
        )
      }
      .map { elem =>
        val trigraph = SugarMap.trigraphs
          .collectFirst {
            case (tri, s) if s == elem.symbol =>
              sanitizeTable(tri.replace("\n", "␤"))
          }
          .getOrElse("")
        val overloads = elem.overloads
        val name = sanitizeTable(elem.name)
        val symbol = sanitizeTable(elem.symbol)
        val keywords = elem.keywords.map("`" + _ + "`").mkString(", ")
        val vectorises =
          if elem.vectorises then ":white_check_mark:"
          else ":x:"

        Seq(
          s"<code>$symbol</code>",
          trigraph,
          name,
          keywords,
          elem.arity.getOrElse("NA").toString,
          vectorises,
          overloads.map(formatOverload).mkString("\n"),
        )
      }

    val elementsTable = createTable(
      Seq(
        "Symbol",
        "Trigraph",
        " Name",
        "Keywords",
        "Arity",
        "Vectorises",
        "Overloads",
      ),
      elements,
    )

    val modifiers = Modifiers.modifiers.keys
      .zip(Modifiers.modifiers.values)
      .toSeq
      .sortBy((modi, _) =>
        (
          Lexer.Codepage.indexOf(modi.charAt(0)) +
            (if "#∆øÞ".contains(modi.charAt(0)) then 400 else 0),
          Lexer.Codepage.indexOf(modi.substring(1)),
        )
      )
      .map {
        case (
              symbol,
              Modifier(name, description, keywords, arity, overloads),
            ) =>
          val trigraph = SugarMap.trigraphs
            .collectFirst { case (tri, s) if s == symbol => tri }
            .getOrElse("")
          val formatSymbol = symbol.replace("|", "\\|")
          val formatKeywords = keywords.map("`" + _ + "`").mkString(", ")
          Seq(
            s"<code>$formatSymbol</code>",
            s"<code>$trigraph</code>",
            sanitizeTable(name),
            formatKeywords,
            arity.toString,
            sanitizeTable(description),
            overloads.map(formatOverload).mkString("\n"),
          )
      }

    val modifiersTable = createTable(
      Seq(
        "Symbol",
        "Trigraph",
        "Name",
        "Keywords",
        "Arity",
        "Description",
        "Usage",
      ),
      modifiers,
    )

    val syntaxInfos = SyntaxInfo.info.keys
      .zip(SyntaxInfo.info.values)
      .toSeq
      .sortBy((symbol, _) =>
        (
          Lexer.Codepage.indexOf(symbol.charAt(0)) +
            (if "#∆øÞ".contains(symbol.charAt(0)) then 400 else 0),
          Lexer.Codepage.indexOf(symbol.substring(1)),
        )
      )
      .map {
        case (symbol, Syntax(name, literate, description, usage)) =>
          val trigraph = SugarMap.trigraphs
            .collectFirst { case (tri, s) if s == symbol => s"`$tri`" }
            .getOrElse("")
          val formatUsage =
            sanitizeTable(usage).replace("<", "&lt;").replace(">", "&gt;")
          Seq(
            s"`${sanitizeTable(symbol.replace("`", raw"\`"))}`",
            trigraph,
            name,
            s"<code>${literate.mkString(" ")}</code>",
            description,
            s"<pre>$formatUsage</pre>",
          )
      }

    val syntaxInformation = createTable(
      Seq(
        "Symbol",
        "Trigraph",
        "Name",
        "Keywords (if applicable)",
        "Description",
        "Usage",
      ),
      syntaxInfos,
    )

    s"""
       |# Information Tables
       |
       |## Elements
       |
       |$elementsTable
       |
       |## Modifiers
       |
       |$modifiersTable
       |
       |## Syntax Features
       |
       |$syntaxInformation
       |""".stripMargin

  end elementsMarkdown

  private def createTable(
      columns: Seq[String],
      rows: Seq[Seq[String]],
  ): String =
    val header = columns.mkString("| ", " | ", " |")
    val divider = Seq.fill(columns.length)("---").mkString("| ", " | ", " |")
    val rowsMarkdown = rows
      .map { row =>
        if row.length == columns.length then
          row
            .map(
              _.strip().replace("\n", "<br>")
            )
            .mkString("| ", " | ", "")
        else
          throw Error(
            s"Row length does not match column length (columns are $columns, row was $rows)"
          )
      }
      .mkString("\n")
    s"$header\n$divider\n$rowsMarkdown"
  end createTable
end GenerateDocs
