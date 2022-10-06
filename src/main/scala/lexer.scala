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
case class MONADIC_MODIFIER(value: String) extends VyxalToken
case class DYADIC_MODIFIER(value: String) extends VyxalToken
case class TRADIC_MODIFIER(value: String) extends VyxalToken
case class SPECIAL_MODIFIER(value: String) extends VyxalToken
case class COMPRESSED_STRING(value: String) extends VyxalToken
case class COMPRESSED_NUMBER(value: String) extends VyxalToken
case class DICTIONARY_STRING(value: String) extends VyxalToken
case class COMMENT(value: String) extends VyxalToken

val CODEPAGE: String = """ᵃᵇᶜᵈᵉᶠᶢᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵗᵘᵛᵂᵡᵞᶻᶴ′″‴⁴ᵜ !"#$%&'()*+,-./0123456789:;
<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ\\[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~¦ȦḂĊḊĖḞĠḢİĿṀṄ
ȮṖṘṠṪẆẊικȧḃċḋėḟġḣŀṁṅȯṗṙṡṫẋƒΘΦ§ẠḄḌḤỊḶṂṆỌṚṢṬ…≤≥≠₌⁺⁻⁾√∑«»⌐∴∵⊻₀₁₂₃₄₅₆₇₈₉λƛΩ₳µ∆øÞ½ʀɾ¯
×÷£¥←↑→↓±‡†Π¬∧∨⁰¹²³¤¨∥∦ı„”ð€“¶ᶿᶲ•≈¿ꜝ"""

val MONADIC_MODIFIERS: String = "ᵃᵇᶜᵈᵉᶠᶢᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵘᵛᵂᵡᵞᶻᶴ¿′/\\~v@`ꜝ"
val DYADIC_MODIFIERS: String = "″∥∦"
val TRADIC_MODIFIERS: String = "‴"
val QUADRIC_MODIFIERS: String = "⁴"
val SPECIAL_MODIFIERS: String = "ᵗᵜ"

object Lexer extends RegexParsers {
  override def skipWhitespace = true
  def number: Parser[NUMBER] = """(0(?:[^.ı])|\d+(\.\d*)?(\ı\d*)?)""".r ^^ { case value => NUMBER(value) }
  def string: Parser[STRING] = """"[^"„”“]*["„”“]""".r ^^ { case value =>
    STRING(value.substring(1, value.length))
  }
  def structureOpen: Parser[STRUCTURE_OPEN] = """[\[\(\{λƛΩ₳µ]|#@""".r ^^ {
    case value => STRUCTURE_OPEN(value)
  }
  def structureClose: Parser[STRUCTURE_CLOSE] = """[\}\)\]]""".r ^^ { case value =>
    STRUCTURE_CLOSE(value)
  }
  def digraph: Parser[DIGRAPH] = s"[∆øÞ#][$CODEPAGE]".r ^^ { case value =>
    DIGRAPH(value)
  }
  def command: Parser[COMMAND] = s"[$CODEPAGE]".r ^^ { case value =>
    COMMAND(value)
  }
  def monadicModifier: Parser[MONADIC_MODIFIER] =
    s"""[$MONADIC_MODIFIERS]""".r ^^ { case value =>
      MONADIC_MODIFIER(value)
    }

  def dyadicModifier: Parser[DYADIC_MODIFIER] =
    s"""[$DYADIC_MODIFIERS]""".r ^^ { case value =>
      DYADIC_MODIFIER(value)
    }

  def tradicModifier: Parser[TRADIC_MODIFIER] =
    s"""[$TRADIC_MODIFIERS]""".r ^^ { case value =>
      TRADIC_MODIFIER(value)
    }

  def quadricModifier: Parser[TRADIC_MODIFIER] =
    s"""[$QUADRIC_MODIFIERS]""".r ^^ { case value =>
      TRADIC_MODIFIER(value)
    }

  def specialModifier: Parser[SPECIAL_MODIFIER] =
    s"""[$SPECIAL_MODIFIERS]""".r ^^ { case value =>
      SPECIAL_MODIFIER(value)
    }

  def comment: Parser[COMMENT] = """##[^\n]*""".r ^^ { case value =>
    COMMENT(value)
  }

  def tokens: Parser[List[VyxalToken]] = phrase(
    rep1(comment |
      number | string | digraph | monadicModifier | dyadicModifier | tradicModifier | quadricModifier | specialModifier | structureOpen | structureClose | command
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
