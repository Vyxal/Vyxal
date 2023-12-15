package vyxal.gen

import vyxal.{Element, Elements, Modifiers, SugarMap}
import vyxal.parsing.Lexer
import vyxal.Modifier
import vyxal.Syntax
import vyxal.SyntaxInfo

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
        SugarMap.trigraphs
          .collect { case (tri, s) if s == name => tri }
          .foreach { tri => sb ++= s"Trigraph: $tri\n" }
        sb ++= "-----------------------\n"
    }

    sb.toString
  end elements

  def trigraphs(): String =
    SugarMap.trigraphs
      .map { case (key, value) => s"$key -> $value" }
      .mkString("", "\n", "\n")

  def elementTable(): String =
    val header =
      "| Symbol | Trigraph |  Name | Keywords | Arity | Vectorises | Overloads |"
    val divider = "| --- | --- | --- | --- | --- | --- | --- |"
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
    Elements.elements.values.toSeq
      .sortBy { elem =>
        // Have to use tuple in case of digraphs
        (
          Lexer.Codepage.indexOf(elem.symbol.charAt(0)) +
            (if "#∆øÞ".contains(elem.symbol.charAt(0)) then 400 else 0),
          Lexer.Codepage.indexOf(elem.symbol.substring(1)),
        )
      }
      .foreach(elem =>
        if !elem.symbol.startsWith("#|") then
          var trigraph = ""
          SugarMap.trigraphs
            .collect { case (tri, s) if s == elem.symbol => tri }
            .foreach { tri => trigraph = tri.replace("\n", "␤") }
          if trigraph.nonEmpty then trigraph = s"<code>$trigraph</code>"
          var overloads = elem.overloads
          val name = elem.name.replace("|", "/")
          val symbol = elem.symbol.replace("|", "\\|")
          val keywords = elem.keywords.map("`" + _ + "`").mkString(", ")
          val vectorises =
            if elem.vectorises then ":white_check_mark:"
            else ":x:"

          contents ++=
            s"| <code>${symbol.replace("\\", "\\\\")}</code> | ${trigraph
                .replace("|", "\\|")} | $name | $keywords | ${elem.arity
                .getOrElse("NA")} | $vectorises | ${formatOverload(overloads.head)}\n"
          overloads = overloads.tail
          while overloads.nonEmpty do
            contents ++= s"| | | | | | | | ${formatOverload(overloads.head)}\n"
            overloads = overloads.tail
      )

    val elements = header + "\n" + divider + "\n" + contents.toString

    contents.setLength(0)

    val modifierHeader =
      "| Symbol | Trigraph | Name | Keywords | Arity | Description |"
    val modiDivider = "| --- | --- | --- | --- | --- | --- |"

    Modifiers.modifiers.keys
      .zip(Modifiers.modifiers.values)
      .toSeq
      .sortBy((modi, _) =>
        (
          Lexer.Codepage.indexOf(modi.charAt(0)) +
            (if "#∆øÞ".contains(modi.charAt(0)) then 400 else 0),
          Lexer.Codepage.indexOf(modi.substring(1)),
        )
      )
      .foreach {
        case (symbol, Modifier(name, description, keywords, arity)) =>
          var trigraph = ""
          SugarMap.trigraphs
            .collect { case (tri, s) if s == symbol => tri }
            .foreach { tri => trigraph = tri }
          val formatSymbol = symbol.replace("|", "\\|")
          val formatKeywords = keywords.map("`" + _ + "`").mkString(", ")
          contents ++=
            s"| <code>$formatSymbol</code> | <code>$trigraph</code> | ${name
                .replace("|", "/")} | $formatKeywords | ${arity} | <pre>${description.replace("|", "").replace("\n", "<br>")}</pre> |\n"
      }

    val modifiers = modifierHeader + "\n" + modiDivider + "\n" +
      contents.toString

    contents.setLength(0)

    val syntaxHeader =
      "| Symbol | Trigraph | Name | Keywords (if applicable) | Description | Usage |"
    val syntaxDivider = "| --- | --- | --- | --- | --- | --- |"

    SyntaxInfo.info.keys
      .zip(SyntaxInfo.info.values)
      .toSeq
      .sortBy((symbol, _) =>
        (
          Lexer.Codepage.indexOf(symbol.charAt(0)) +
            (if "#∆øÞ".contains(symbol.charAt(0)) then 400 else 0),
          Lexer.Codepage.indexOf(symbol.substring(1)),
        )
      )
      .foreach {
        case (symbol, Syntax(name, literate, description, usage)) =>
          var trigraph = ""
          SugarMap.trigraphs
            .collect { case (tri, s) if s == symbol => tri }
            .foreach { tri => trigraph = tri }

          if trigraph.nonEmpty then trigraph = s"`$trigraph`"
          val formatSymbol = "\\".repeat(if symbol == "`" then 1 else 0) +
            symbol.replace("|", "\\|")
          val formatUsage = usage
            .replace("|", "\\|")
            .replace("\n", "<br>")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
          contents ++=
            s"| `$formatSymbol` | $trigraph | $name | <code>${literate.mkString(" ")}</code> | $description | <pre>$formatUsage</pre> |\n"
      }

    val syntaxInformation = syntaxHeader + "\n" + syntaxDivider + "\n" +
      contents.toString

    s"""
       |# Information Tables
       |
       |## Elements
       |
       |$elements
       |
       |## Modifiers
       |
       |$modifiers
       |
       |## Syntax Features
       |
       |$syntaxInformation
       |""".stripMargin

  end elementTable
end GenerateDocs
