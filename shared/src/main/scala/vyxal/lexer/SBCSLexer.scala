package vyxal.lexer

import scala.language.strictEquality

import vyxal.impls.Elements
import vyxal.lexer.Common.{parseToken, withInd, withRange}
import vyxal.lexer.Common.given // For custom whitespace
import vyxal.lexer.TokenType.*

import scala.util.matching.Regex

import fastparse.*

private[lexer] object SBCSLexer:
  /** Whether the code lexed so far has sugar trigraphs */
  var sugarUsed = false

  def sbcsDecimal[$: P]: P[String] =
    P(((Common.int ~ ("." ~ Common.digits).? | "." ~ Common.digits) ~ "_".?).!)
  def sbcsNumber[$: P]: P[Token] =
    Common.number(sbcsDecimal, P("ı".!))

  def contextIndex[$: P]: P[Token] = withRange("""\d*¤""".r) ^^ {
    case (value, range) =>
      Token(ContextIndex, value.substring(0, value.length - 1).trim, range)
  }

  def getVariable[$: P]: P[Token] = withRange("""(#\$)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(GetVar, value.substring(2), range)
  }

  def setVariable[$: P]: P[Token] = withRange("""(#=)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(SetVar, value.substring(2), range)
  }

  def setConstant[$: P]: P[Token] = withRange("""(#!)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(Constant, value.substring(2), range)
  }

  def augVariable[$: P]: P[Token] = withRange("""(#>)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(AugmentVar, value.substring(2, value.length), range)
  }

  def newlines[$: P]: P[Token] = parseToken(Newline, Common.eol)

  def token[$: P]: P[Token] =
    sugarTrigraph | syntaxTrigraph | digraph | branch | contextIndex
      | number | string | augVariable | getVariable | setVariable
      | setConstant | twoCharNumber | twoCharString | singleCharString
      | monadicModifier | dyadicModifier | triadicModifier | tetradicModifier
      | specialModifier | structureOpen | structureSingleClose | structureAllClose
      | listOpen | listClose | newlines | command

  // structureDoubleClose (")") has to be here to avoid interfering with `normalGroup` in literate lexer
  def tokens[$: P]: P[List[Token]] = rep(token | structureDoubleClose)

end SBCSLexer
