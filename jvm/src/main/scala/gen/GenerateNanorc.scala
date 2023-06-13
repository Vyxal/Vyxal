package vyxal.gen

import vyxal.{Lexer, Modifiers}
import vyxal.impls.Elements

import scala.annotation.static
import scala.util.matching.Regex

/** To generate nanorc files for syntax highlighting in JLine. See build.sc */
private[vyxal] object GenerateNanorc:
  /** The name of the nanorc file for Vyxal in SBCS mode */
  val SBCSNanorc = "vyxal.nanorc"

  /** The name of the nanorc file for Vyxal in literate mode */
  val LitNanorc = "vyxal-lit.nanorc"

  val codepage = vyxal.CODEPAGE.filter(_ != '\n').map(c => Regex.quote(c.toString)).mkString

  /** NOTE: Make sure to escape each $ with another $ */
  val commonHeader = raw"""|syntax "Vyxal" "\.(vy)$$"
    |comment "##"

    |## Default
    |color white "^.+$$"
    |
    |## Structures and lists
    |color yellow "${Lexer.structureOpenRegex}"
    |color yellow "[})\]]"
    |color yellow "\|"
    |color yellow "(#\[)|⟨"
    |color yellow "(#\])|⟩"
    |
    |## Numbers
    |color cyan "\<((${Lexer.decimalRegex}?ı(${Lexer.decimalRegex}|_)?)|${Lexer.decimalRegex})\>"
    |
    |## Invalid characters
    |color yellow,red "[^$codepage]*"
    |
    |## Modifiers
    |color brightmagenta "${Modifiers.modifiers.keys
                            .map(Regex.quote)
                            .mkString("|")}"
    |""".stripMargin

  val commonFooter = """|
    |## Variables
    |color yellow "#[$=!>]\w*"
    |
    |## Strings
    |color green "L?\"[^"„”“\\]*([\"„”“]|$)"
    |
    |## Comments
    |color blue "\s*##.*$$"
    |""".stripMargin

  val litDecimalRegex = raw"(-?((0|[1-9][0-9_]*)?\.[0-9]*|0|[1-9][0-9_]*))"
  val elementKeywords = Elements.elements.values.flatMap(_.keywords)
  val litSpecific = raw"""|
    |## Numbers (literate)
    |color cyan "\<((${litDecimalRegex}i($litDecimalRegex)?)|(i$litDecimalRegex)|$litDecimalRegex|(i\b))\>"
    |
    |## Elements (literate)
    |color magenta "\<(${elementKeywords.map(Regex.quote).mkString("|")})\>"
    |""".stripMargin

  /** Generates nanorc files for both SBCS and literate modes, returning a
    * mapping from each file's name to its contents
    */
  def generate(): Map[String, String] =
    val sbcs = commonHeader + commonFooter
    val lit = commonHeader + litSpecific + commonFooter
    Map(SBCSNanorc -> sbcs, LitNanorc -> lit)
end GenerateNanorc
