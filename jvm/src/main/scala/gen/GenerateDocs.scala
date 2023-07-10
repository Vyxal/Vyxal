package vyxal.gen

import vyxal.{Modifiers, SugarMap}
import vyxal.impls.{Element, Elements}
import vyxal.lexer.Lexer

/** For generating elements.txt and trigraphs.txt. See build.sc */
private object GenerateDocs:
  def generate(): (String, String) = (elements(), trigraphs())

  def elements(): String =
    val sb = StringBuilder()
    Elements.elements.values.toSeq
      .sortBy { elem =>
        // Have to use tuple in case of digraphs
        (
          Lexer.Codepage.indexOf(elem.symbol.charAt(0)),
          Lexer.Codepage.indexOf(elem.symbol.substring(1))
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
              impl
            ) =>
          sb ++=
            s"$symbol ($name) (${if vectorises then "" else "non-"}vectorising)\n"

          SugarMap.trigraphs
            .collect { case (tri, s) if s == symbol => tri }
            .foreach { tri => sb ++= s"Trigraph: $tri\n" }

          sb ++= s"Keywords:${keywords.mkString(" ", ", ", "")}\n"
          overloads.foreach { overload =>
            sb ++= s"- $overload\n"
          }
          sb ++= "---------------------\n"
      }

    Modifiers.modifiers.foreach { case (name, info) =>
      sb ++= s"$name\n"
      sb ++= s"Keywords:${info.keywords.mkString(" ", ", ", "")}\n"
      sb ++= s"Description: ${info.description}\n"
      sb ++= "---------------------\n"
    }

    sb.toString
  end elements

  def trigraphs(): String =
    SugarMap.trigraphs
      .map { case (key, value) =>
        s"$key -> $value"
      }
      .mkString("", "\n", "\n")
end GenerateDocs
