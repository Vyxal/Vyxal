package vyxal.gen

import vyxal.{Modifiers, SugarMap}
import vyxal.impls.{Element, Elements}

/** For generating elements.txt and trigraphs.txt. See build.sc */
private object GenerateDocs:
  def generate(): (String, String) = (elements(), trigraphs())

  def elements(): String =
    val sb = StringBuilder()
    Elements.elements.values.toSeq
      .sortBy { elem =>
        // Have to use tuple in case of digraphs
        (
          vyxal.CODEPAGE.indexOf(elem.symbol.charAt(0)),
          vyxal.CODEPAGE.indexOf(elem.symbol.substring(1))
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

          sb ++= s"Keywords:${keywords.mkString(" ", ", ", "")}"
          overloads.foreach { overload =>
            sb ++= s"- $overload"
          }
          sb ++= "---------------------"
      }

    Modifiers.modifiers.foreach { case (name, info) =>
      sb ++= s"$name\n"
      sb ++= s"Keywords:${info.keywords.mkString(" ", ", ", "")}"
      sb ++= s"Description: ${info.description}"
      sb ++= "---------------------"
    }

    sb += '\n'
    sb.toString
  end elements

  def trigraphs(): String =
    SugarMap.trigraphs
      .map { case (key, value) =>
        s"$key -> $value"
      }
      .mkString("", "\n", "\n")
end GenerateDocs
