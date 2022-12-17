package vyxal

import scala.util.matching.Regex
import scala.util.parsing.combinator.*
import VyxalToken.*

case class VyxalCompilationError(msg: String)

// todo maybe make a separate TokenType enum and make VyxalToken a simple case class

enum VyxalToken(val value: String):
  case Number(override val value: String) extends VyxalToken(value)
  case Str(override val value: String) extends VyxalToken(value)
  case StructureOpen(structureType: StructureType)
      extends VyxalToken(structureType.open)
  case StructureClose(override val value: String) extends VyxalToken(value)
  case StructureAllClose extends VyxalToken("]")
  case ListOpen extends VyxalToken("#[")
  case ListClose extends VyxalToken("#]")
  case Command(override val value: String) extends VyxalToken(value)
  case Digraph(override val value: String) extends VyxalToken(value)
  case SugarTrigraph(override val value: String) extends VyxalToken(value)
  case SyntaxTrigraph(override val value: String) extends VyxalToken(value)
  case MonadicModifier(override val value: String) extends VyxalToken(value)
  case DyadicModifier(override val value: String) extends VyxalToken(value)
  case TriadicModifier(override val value: String) extends VyxalToken(value)
  case TetradicModifier(override val value: String) extends VyxalToken(value)
  case SpecialModifier(override val value: String) extends VyxalToken(value)
  case CompressedString(override val value: String) extends VyxalToken(value)
  case CompressedNumber(override val value: String) extends VyxalToken(value)
  case DictionaryString(override val value: String) extends VyxalToken(value)
  case Comment(override val value: String) extends VyxalToken(value)
  case GetVar(override val value: String) extends VyxalToken(value)
  case SetVar(override val value: String) extends VyxalToken(value)
  case AugmentVar(override val value: String) extends VyxalToken(value)
  case UnpackVar(override val value: String) extends VyxalToken(value)
  case Branch extends VyxalToken("|")
  case Newline extends VyxalToken("\n")
end VyxalToken

enum StructureType(val open: String):
  case If extends StructureType("[")
  case While extends StructureType("{")
  case For extends StructureType("(")
  case Lambda extends StructureType("λ")
  case LambdaMap extends StructureType("ƛ")
  case LambdaFilter extends StructureType("Ω")
  case LambdaReduce extends StructureType("₳")
  case LambdaSort extends StructureType("µ")

val CODEPAGE = """ᵃᵇᶜᵈᵉᶠᶢᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵗᵘᵛᵂᵡᵞᶻᶴ′″‴⁴ᵜ !"#$%&'()*+,-./0123456789:;
<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ\\[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~¦ȦḂĊḊĖḞĠḢİĿṀṄ
ȮṖṘṠṪẆẊικȧḃċḋėḟġḣŀṁṅȯṗṙṡṫẋƒΘΦ§ẠḄḌḤỊḶṂṆỌṚṢṬ…≤≥≠₌⁺⁻⁾√∑«»⌐∴∵⊻₀₁₂₃₄₅₆₇₈₉λƛΩ₳µ∆øÞ½ʀɾ¯
×÷£¥←↑→↓±‡†Π¬∧∨⁰¹²³¤¨∥∦ı„”ð€“¶ᶿᶲ•≈¿ꜝ"""

val MONADIC_MODIFIERS = "ᵃᵇᶜᵈᵉᶠᶢᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵘᵛᵂᵡᵞᶻ¿′/\\~v@`ꜝ"
val DYADIC_MODIFIERS = "″∥∦"
val TRIADIC_MODIFIERS = "‴"
val TETRADIC_MODIFIERS = "⁴"
val SPECIAL_MODIFIERS = "ᵗᵜ"

object Lexer extends RegexParsers:
  override def skipWhitespace = true
  override val whiteSpace: Regex = "[ \t\r\f]+".r

  def number: Parser[VyxalToken] = """(0(?=[^.ı])|\d+(\.\d*)?(\ı\d*)?)""".r ^^ {
    value => Number(value)
  }

  def string: Parser[VyxalToken] = """("(?:[^"„”“\\\\]|\\.)*["„”“])""".r ^^ {
    value =>
      // If the last character of each token is ", then it's a normal string
      // If the last character of each token is „, then it's a compressed string
      // If the last character of each token is ”, then it's a dictionary string
      // If the last character of each token is “, then it's a compressed number

      // So replace the non-normal string tokens with the appropriate token type

      // btw thanks to @pxeger and @mousetail for the regex
      val text = value.substring(1, value.length - 1).replaceAll("\\\\\"", "\"")

      value.charAt(value.length - 1) match
        case '"' => Str(text)
        case '„' => CompressedString(text)
        case '”' => DictionaryString(text)
        case '“' => CompressedNumber(text)
        case _   => throw Exception("Invalid string")
  }

  def singleCharString: Parser[VyxalToken] = """'.""".r ^^ { value =>
    Str(value.substring(1))
  }

  def twoCharString: Parser[VyxalToken] = """ᶴ..""".r ^^ { value =>
    Str(value.substring(1))
  }

  def structureOpen: Parser[VyxalToken] = """[\[\(\{λƛΩ₳µ]|#@""".r ^^ { value =>
    StructureOpen(StructureType.values.find(_.open == value).get)
  }

  def structureClose: Parser[VyxalToken] = """[\}\)]""".r ^^ { value =>
    StructureClose(value)
  }

  def structureAllClose: Parser[VyxalToken] = """\]""".r ^^^ StructureAllClose

  def listOpen: Parser[VyxalToken] = """(#\[)|⟨""".r ^^^ ListOpen

  def listClose: Parser[VyxalToken] = """(#\])|⟩""".r ^^^ ListClose

  def multigraph: Parser[VyxalToken] =
    s"([∆øÞk].)|(#[:.,]?.)".r ^^ { value =>
      if value.length == 2 then Digraph(value)
      else if value.charAt(1) == ':' then SyntaxTrigraph(value)
      else SugarTrigraph(value)
    }

  def command: Parser[VyxalToken] = s"[$CODEPAGE]".r ^^ { value =>
    Command(value)
  }

  def getVariable: Parser[VyxalToken] = """(\#\$)[0-9A-Za-z_]*""".r ^^ {
    value =>
      GetVar(value.substring(2, value.length))
  }

  def setVariable: Parser[VyxalToken] = """(\#\=)[0-9A-Za-z_]*""".r ^^ {
    value =>
      SetVar(value.substring(2, value.length))
  }

  def monadicModifier: Parser[VyxalToken] =
    s"""[$MONADIC_MODIFIERS]""".r ^^ { value => MonadicModifier(value) }

  def dyadicModifier: Parser[VyxalToken] =
    s"""[$DYADIC_MODIFIERS]""".r ^^ { value => DyadicModifier(value) }

  def triadicModifier: Parser[VyxalToken] =
    s"""[$TRIADIC_MODIFIERS]""".r ^^ { value => TriadicModifier(value) }

  def tetradicModifier: Parser[VyxalToken] =
    s"""[$TETRADIC_MODIFIERS]""".r ^^ { value => TetradicModifier(value) }

  def specialModifier: Parser[VyxalToken] =
    s"""[$SPECIAL_MODIFIERS]""".r ^^ { value => SpecialModifier(value) }

  def comment: Parser[VyxalToken] = """##[^\n]*""".r ^^ { value =>
    Comment(value)
  }

  def branch = "|" ^^^ Branch

  def newlines = "\n" ^^^ Newline

  def tokens: Parser[List[VyxalToken]] = phrase(
    rep1(
      comment | multigraph | branch | number | string | getVariable | setVariable
        | twoCharString | singleCharString
        | monadicModifier | dyadicModifier | triadicModifier | tetradicModifier
        | specialModifier | structureOpen | structureClose | structureAllClose
        | listOpen | listClose | newlines | command
    )
  )

  def apply(code: String): Either[VyxalCompilationError, List[VyxalToken]] =
    (parse(tokens, code): @unchecked) match
      case NoSuccess(msg, next)  => Left(VyxalCompilationError(msg))
      case Success(result, next) => Right(result)
end Lexer
