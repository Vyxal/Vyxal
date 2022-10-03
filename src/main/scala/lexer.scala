import scala.util.parsing.combinator._

sealed trait VyxalToken
trait VyxalCompilationError
case class VyxalLexerError(msg: String) extends VyxalCompilationError

case class NUMBER(value: String) extends VyxalToken
case class STRING(value: String) extends VyxalToken
case class STRUCTURE_OPEN(value: String) extends VyxalToken
case class STRUCTURE_CLOSE(value: String) extends VyxalToken
case class COMMAND(value: String) extends VyxalToken
case class DIGRAPH(value: String) extends VyxalToken
case class MODIFIER(value: String) extends VyxalToken
case class COMPRESSED_STRING(value: String) extends VyxalToken
case class COMPRESSED_NUMBER(value: String) extends VyxalToken
case class DICTIONARY_STRING(value: String) extends VyxalToken

val CODEPAGE: String = """ᵃᵇᶜᵈᵉᶠᶢᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵗᵘᵛᵂᵡᵞᶻᶴ′″‴⁴ᵜ !"#$%&'()*+,-./0123456789:;
<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ\\[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~¦ȦḂĊḊĖḞĠḢİĿṀṄ
ȮṖṘṠṪẆẊικȧḃċḋėḟġḣŀṁṅȯṗṙṡṫẋƒΘΦ§ẠḄḌḤỊḶṂṆỌṚṢṬ…≤≥≠₌⁺⁻⁾√∑«»⌐∴∵⊻₀₁₂₃₄₅₆₇₈₉λƛΩ₳µ∆øÞ½ʀɾ¯
×÷£¥←↑→↓±‡†Π¬∧∨⁰¹²³¤¨∥∦ı„”ð€“¶ᶿᶲ•≈¿ꜝ"""

val MODIFIERS: String = "ᵃᵇᶜᵈᵉᶠᶢᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵗᵘᵛᵂᵡᵞᶻᶴ′″‴⁴ᵜ/\\~v@`∥∦¿ꜝ"

object lexer extends RegexParsers {
  override def skipWhitespace = true
  def number: Parser[NUMBER] = """[0-9]+""".r ^^ { case value => NUMBER(value) }
  def string: Parser[STRING] = """"[^"„”“]*["„”“]""".r ^^ { case value =>
    STRING(value.substring(1, value.length))
  }
  def structureOpen: Parser[STRUCTURE_OPEN] = """[\[\(\{λƛΩ₳µ]|#@""".r ^^ {
    case value => STRUCTURE_OPEN(value)
  }
  def structureClose: Parser[STRUCTURE_CLOSE] = """\}""".r ^^ { case value =>
    STRUCTURE_CLOSE(value)
  }
  def digraph: Parser[DIGRAPH] = s"[∆øÞ#][$CODEPAGE]".r ^^ { case value =>
    DIGRAPH(value)
  }
  def command: Parser[COMMAND] = s"[$CODEPAGE]".r ^^ { case value =>
    COMMAND(value)
  }
  def modifier: Parser[MODIFIER] = s"""[$MODIFIERS]""".r ^^ { case value =>
    MODIFIER(value)
  }

  def tokens: Parser[List[VyxalToken]] = phrase(
    rep1(
      number | string | digraph | modifier | structureOpen | structureClose | command
    )
  ) ^^ { case tokens => handleStrings(tokens) }
  def apply(code: String): Either[VyxalLexerError, List[VyxalToken]] = {
    parse(tokens, code) match {
      case NoSuccess(msg, next)  => Left(VyxalLexerError(msg))
      case Success(result, next) => Right(result)
    }
  }
}

private def handleStrings(tokens: List[VyxalToken]): List[VyxalToken] = {
  // If the last character of each token is ", then it's a normal string
  // If the last character of each token is „, then it's a compressed string
  // If the last character of each token is ”, then it's a dictionary string
  // If the last character of each token is “, then it's a compressed number

  // So replace the non-normal string tokens with the appropriate token type

  tokens.map {
    case STRING(value) =>
      if (value.last == '"') {
        STRING(value.dropRight(1))
      } else if (value.last == '„') {
        COMPRESSED_STRING(value.dropRight(1))
      } else if (value.last == '”') {
        DICTIONARY_STRING(value.dropRight(1))
      } else if (value.last == '“') {
        COMPRESSED_NUMBER(value.dropRight(1))
      } else {
        throw new Exception("Invalid string")
      }
    case token => token
  }

}
