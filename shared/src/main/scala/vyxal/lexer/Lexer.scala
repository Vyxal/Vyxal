package vyxal.lexer

import scala.language.strictEquality

import vyxal.impls.Elements

import java.util.regex.Pattern
import scala.collection.mutable.{ListBuffer, Queue}
import scala.util.matching.Regex

import fastparse.*

case class VyxalCompilationError(msg: String)

case class Token(tokenType: TokenType, value: String, range: Range)
    derives CanEqual:
  override def equals(obj: Any): Boolean = obj match
    case other: Token =>
      (other `eq` this) || (other.tokenType == this.tokenType && other.value == this.value)
    case _ => false

/** The range of a token or AST in the source code */
case class Range(startOffset: Int, endOffset: Int) derives CanEqual:
  /** Override the default equals method so Range.fake compares equal to
    * everything.
    */
  override def equals(obj: Any): Boolean = obj match
    case other: Range =>
      (other `eq` this) ||
      (this `eq` Range.fake) ||
      (other `eq` Range.fake) ||
      (other.startOffset == this.startOffset && other.endOffset == this.endOffset)
    case _ => false

object Range:
  /** A dummy Range (mainly for generated/desugared code) */
  val fake: Range = Range(-1, -1)

enum TokenType(val canonicalSBCS: Option[String] = None) derives CanEqual:
  case Number
  case Str
  case StructureOpen
  case StructureClose extends TokenType(Some("}"))
  case StructureDoubleClose extends TokenType(Some(")"))
  case StructureAllClose extends TokenType(Some("]"))
  case ListOpen extends TokenType(Some("#["))
  case ListClose extends TokenType(Some("#]"))
  case Command
  case Digraph
  case SyntaxTrigraph
  case MonadicModifier
  case DyadicModifier
  case TriadicModifier
  case TetradicModifier
  case SpecialModifier
  case CompressedString
  case CompressedNumber
  case DictionaryString
  case ContextIndex
  case Comment
  case GetVar
  case SetVar
  case Constant
  case AugmentVar
  case UnpackVar
  case Branch extends TokenType(Some("|"))
  case Newline extends TokenType(Some("\n"))
  case Param
  case UnpackClose extends TokenType(Some("]"))

  /** Helper to help go from the old VyxalToken to the new Token(TokenType,
    * text, range) format
    */
  def apply(text: String): Token = Token(this, text, Range.fake)

  /** Helper to destructure tokens more concisely */
  def unapply(tok: Token): Option[(String, Range)] =
    if tok.tokenType == this then Some((tok.value, tok.range)) else None
end TokenType

enum StructureType(val open: String) derives CanEqual:
  case Ternary extends StructureType("[")
  case While extends StructureType("{")
  case For extends StructureType("(")
  case Lambda extends StructureType("λ")
  case LambdaMap extends StructureType("ƛ")
  case LambdaFilter extends StructureType("Ω")
  case LambdaReduce extends StructureType("₳")
  case LambdaSort extends StructureType("µ")
  case IfStatement extends StructureType("#{")
  case DecisionStructure extends StructureType("Ḍ")
  case GeneratorStructure extends StructureType("Ṇ")

object StructureType:
  val lambdaStructures: List[StructureType] = List(
    StructureType.Lambda,
    StructureType.LambdaMap,
    StructureType.LambdaFilter,
    StructureType.LambdaReduce,
    StructureType.LambdaSort
  )

val CODEPAGE = "ᵃᵇᶜᵈᵉᶠᶢᴴᶤᶨ\nᵏᶪᵐⁿᵒᵖᴿᶳᵗᵘᵛᵂᵡᵞᶻᶴ⸠ϩэЧᵜ !"
  + "\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFG"
  + "HIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmn"
  + "opqrstuvwxyz{|}~¦ȦḂĊḊĖḞĠḢİĿṀṄȮṖṘṠṪẆẊικȧḃċ"
  + "ḋėḟġḣŀṁṅȯṗṙṡṫẋƒΘΦ§ẠḄḌḤỊḶṂṆỌṚṢṬ…≤≥≠₌⁺⁻⁾√∑«»"
  + "⌐∴∵⊻₀₁₂₃₄₅₆₇₈₉λƛΩ₳µ∆øÞ½ʀɾ¯×÷£¥←↑→↓±¤†Π¬∧∨⁰"
  + "¹²³Ɠɠ∥∦ı„”ð€“¶ᶿᶲ•≈¿ꜝ"

val MONADIC_MODIFIERS = "ᵃᵇᶜᵈᵉᶠᶢᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵘᵛᵂᵡᵞᶻ¿⸠/\\~v@`ꜝ"
val DYADIC_MODIFIERS = "ϩ∥∦"
val TRIADIC_MODIFIERS = "э"
val TETRADIC_MODIFIERS = "Ч"
val SPECIAL_MODIFIERS = "ᵗᵜ"

trait Lexer:
  def tokens: P[List[Token]]

  final def lex(code: String): Either[VyxalCompilationError, List[Token]] =
    parseAll(code) match
      case Parsed.Success(_, _) => ???
      case _ => Left(VyxalCompilationError(???))

object Lexer:
  val decimalRegex: Regex = raw"(((0|[1-9][0-9]*)?\.[0-9]*|0|[1-9][0-9]*)_?)".r
  val structureOpenRegex: String = """[\[\(\{λƛΩ₳µḌṆ]|#@|#\{"""

  def apply(code: String): Either[VyxalCompilationError, List[Token]] =
    SBCSLexer.lex(code)

  def removeSugar(code: String): Option[String] =
    SBCSLexer.lex(code) match
      case Right(result) =>
        if SBCSLexer.sugarUsed then Some(result.map(_.value).mkString)
        else None
      case _ => None
