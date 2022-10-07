import scala.util.parsing.combinator._

trait VyxalCompilationError
case class VyxalLexerError(msg: String) extends VyxalCompilationError

enum VyxalToken {
  case Number(value: String)
  case Str(value: String)
  case StructureOpen(value: String)
  case StructureClose(value: String)
  case Command(value: String)
  case Digraph(value: String)
  case MonadicModifier(value: String)
  case DyadicModifier(value: String)
  case TriadicModifier(value: String)
  case QuadricModifier(value: String)
  case SpecialModifier(value: String)
  case CompressedString(value: String)
  case CompressedNumber(value: String)
  case DictionaryString(value: String)
  case Comment(value: String)
  case Branch
}

import VyxalToken.*

val CODEPAGE: String = """ᵃᵇᶜᵈᵉᶠᶢᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵗᵘᵛᵂᵡᵞᶻᶴ′″‴⁴ᵜ !"#$%&'()*+,-./0123456789:;
<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ\\[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~¦ȦḂĊḊĖḞĠḢİĿṀṄ
ȮṖṘṠṪẆẊικȧḃċḋėḟġḣŀṁṅȯṗṙṡṫẋƒΘΦ§ẠḄḌḤỊḶṂṆỌṚṢṬ…≤≥≠₌⁺⁻⁾√∑«»⌐∴∵⊻₀₁₂₃₄₅₆₇₈₉λƛΩ₳µ∆øÞ½ʀɾ¯
×÷£¥←↑→↓±‡†Π¬∧∨⁰¹²³¤¨∥∦ı„”ð€“¶ᶿᶲ•≈¿ꜝ"""

val MONADIC_MODIFIERS: String = "ᵃᵇᶜᵈᵉᶠᶢᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵘᵛᵂᵡᵞᶻᶴ¿′/\\~v@`ꜝ"
val DYADIC_MODIFIERS: String = "″∥∦"
val TRIADIC_MODIFIERS: String = "‴"
val QUADRIC_MODIFIERS: String = "⁴"
val SPECIAL_MODIFIERS: String = "ᵗᵜ"

object Lexer extends RegexParsers {
  override def skipWhitespace = true

  def number: Parser[VyxalToken] = """(0(?:[^.ı])|\d+(\.\d*)?(\ı\d*)?)""".r ^^ {
    value => Number(value)
  }

  def string: Parser[VyxalToken] = """"[^"„”“]*["„”“]""".r ^^ { value =>
    // If the last character of each token is ", then it's a normal string
    // If the last character of each token is „, then it's a compressed string
    // If the last character of each token is ”, then it's a dictionary string
    // If the last character of each token is “, then it's a compressed number

    // So replace the non-normal string tokens with the appropriate token type

    val text = value.substring(1, value.length - 1)
    value.charAt(value.length - 1) match {
      case '"' => Str(text)
      case '„' => CompressedString(text)
      case '”' => DictionaryString(text)
      case '“' => CompressedNumber(text)
      case _   => throw Exception("Invalid string")
    }
  }

  def structureOpen: Parser[VyxalToken] = """[\[\(\{λƛΩ₳µ]|#@""".r ^^ { value =>
    StructureOpen(value)
  }

  def structureClose: Parser[VyxalToken] = """[\}\)\]]""".r ^^ { value =>
    StructureClose(value)
  }

  def digraph: Parser[VyxalToken] = s"[∆øÞ#][$CODEPAGE]".r ^^ { value =>
    Digraph(value)
  }

  def command: Parser[VyxalToken] = s"[$CODEPAGE]".r ^^ { value =>
    Command(value)
  }

  def monadicModifier: Parser[VyxalToken] =
    s"""[$MONADIC_MODIFIERS]""".r ^^ { value => MonadicModifier(value) }

  def dyadicModifier: Parser[VyxalToken] =
    s"""[$DYADIC_MODIFIERS]""".r ^^ { value => DyadicModifier(value) }

  def triadicModifier: Parser[VyxalToken] =
    s"""[$TRIADIC_MODIFIERS]""".r ^^ { value => TriadicModifier(value) }

  def quadricModifier: Parser[VyxalToken] =
    s"""[$QUADRIC_MODIFIERS]""".r ^^ { value => QuadricModifier(value) }

  def specialModifier: Parser[VyxalToken] =
    s"""[$SPECIAL_MODIFIERS]""".r ^^ { value => SpecialModifier(value) }

  def comment: Parser[VyxalToken] = """##[^\n]*""".r ^^ { value =>
    Comment(value)
  }

  def branch = "|" ^^ { _ => Branch }

  def tokens: Parser[List[VyxalToken]] = phrase(
    rep1(
      comment | branch | number | string | digraph | monadicModifier |
        dyadicModifier | triadicModifier | quadricModifier | specialModifier |
        structureOpen | structureClose | command
    )
  )

  def apply(code: String): Either[VyxalLexerError, List[VyxalToken]] = {
    parse(tokens, code) match {
      case NoSuccess(msg, next)  => Left(VyxalLexerError(msg))
      case Success(result, next) => Right(result)
    }
  }
}
