package vyxal.parsing

import scala.language.strictEquality

import vyxal.parsing.TokenType.*
import vyxal.Context
import vyxal.Elements

import fastparse.*

case class VyxalCompilationError(msg: String)

case class Token(tokenType: TokenType, value: String, range: Range)
    derives CanEqual:
  override def equals(obj: Any): Boolean =
    obj match
      case other: Token => (other `eq` this) ||
        (other.tokenType == this.tokenType && other.value == this.value)
      case _ => false

  override def toString: String = s"$tokenType(\"$value\")"

/** The range of a token or AST in the source code. The start offset is
  * inclusive, the end offset is exclusive.
  */
case class Range(startOffset: Int, endOffset: Int) derives CanEqual:
  def includes(offset: Int): Boolean =
    startOffset <= offset && offset < endOffset

  /** Override the default equals method so Range.fake compares equal to
    * everything.
    */
  override def equals(obj: Any): Boolean =
    obj match
      case other: Range => (other `eq` this) ||
        (this `eq` Range.fake) ||
        (other `eq` Range.fake) ||
        (other.startOffset == this.startOffset &&
          other.endOffset == this.endOffset)
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
  case GroupType
  case NegatedCommand

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
    StructureType.LambdaSort,
  )

private[parsing] trait Lexer:
  def parseAll[$: P]: P[Seq[Token]]

  final def lex(code: String): Either[VyxalCompilationError, List[Token]] =
    parse(code, this.parseAll) match
      case Parsed.Success(res, ind) =>
        if ind == code.length then Right(res.toList)
        else
          Left(
            VyxalCompilationError(
              s"Parsed $res but did not consume '${code.substring(ind)}'"
            )
          )
      case f @ Parsed.Failure(label, index, extra) =>
        val trace = f.trace()
        Left(VyxalCompilationError(s"Lexing failed: ${trace.longMsg}"))
end Lexer

object Lexer:
  val structureOpenRegex: String = """[\[\(\{λƛΩ₳µḌṆ]|#@|#\{"""

  val Codepage = "ᵃᵇᶜᵈᵉᶠᴳᴴᶤᶨ\nᵏᶪᵐⁿᵒᵖᴿᶳᵗᵘᵛᵂᵡᵞᶻᶴ⸠ϩэЧᵜ !" +
    "\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFG" +
    "HIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmn" +
    "opqrstuvwxyz{|}~ȦḂĊḊĖḞĠḢİĿṀṄȮṖṘṠṪẆẊικȧḃċ" +
    "ḋėḟġḣŀṁṅȯṗṙṡṫẋƒΘΦ§ẠḄḌḤỊḶṂṆỌṚṢṬ…≤≥≠₌⁺⁻⁾√∑«»" +
    "⌐∴∵⊻₀₁₂₃₄₅₆₇₈₉λƛΩ₳µ∆øÞ½ʀɾ¯×÷£¥←↑→↓±¤†Π¬∧∨⁰" + "¹²⌈⌊Ɠɠ∥∦ı„”ð€“¶ᶿᶲ•≈¿ꜝ"

  val UnicodeCommands = "🍪ඞ"

  def literateModeMappings: Map[String, String] =
    LiterateLexer.literateModeMappings

  def apply(
      code: String
  )(using ctx: Context): Either[VyxalCompilationError, List[Token]] =
    if ctx.settings.literate then lexLiterate(code) else lexSBCS(code)

  def lexSBCS(code: String): Either[VyxalCompilationError, List[Token]] =
    SBCSLexer.lex(code)

  def lexLiterate(code: String): Either[VyxalCompilationError, List[Token]] =
    LiterateLexer.lex(code)

  def isList(code: String): Boolean =
    parse(code, LiterateLexer.list(_)).isSuccess

  def removeSugar(code: String): Option[String] =
    SBCSLexer.lex(code) match
      case Right(result) =>
        if SBCSLexer.sugarUsed then Some(result.map(_.value).mkString) else None
      case _ => None

  private def sbcsifySingle(token: Token): String =
    val Token(tokenType, value, _) = token
    tokenType match
      case GetVar => "#$" + value
      case SetVar => s"#=$value"
      case AugmentVar => s"#>$value"
      case Constant => s"#!$value"
      case Str => s""""$value""""
      case SyntaxTrigraph if value == ":=[" => "#:["
      case Command if !Elements.elements.contains(value) =>
        Elements.symbolFor(value).getOrElse(value)
      case Comment => ""
      case _ => tokenType.canonicalSBCS.getOrElse(value)

  /** Convert literate mode code into SBCS mode code */
  def sbcsify(tokens: List[Token]): String =
    val out = StringBuilder()

    for i <- tokens.indices do
      val token @ Token(tokenType, value, _) = tokens(i)
      val sbcs = sbcsifySingle(token)
      out.append(sbcs)

      if i < tokens.length - 1 then
        val next = tokens(i + 1)
        tokenType match
          case Number =>
            if value != "0" && next.tokenType == Number
            then out.append(" ")
          case GetVar | SetVar | AugmentVar | Constant =>
            if "[a-zA-Z0-9_]+".r.matches(sbcsifySingle(next)) then
              out.append(" ")
          case _ =>

    out.toString
  end sbcsify
end Lexer
