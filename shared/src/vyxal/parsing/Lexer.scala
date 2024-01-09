package vyxal.parsing

import scala.language.strictEquality

import vyxal.parsing.TokenType.*
import vyxal.Context
import vyxal.Elements

import scala.collection.mutable.ListBuffer

import fastparse.*

case class Token(
    tokenType: TokenType,
    value: String,
    range: Range,
) derives CanEqual:
  override def equals(obj: Any): Boolean =
    obj match
      case other: Token => (other `eq` this) ||
        (other.tokenType == this.tokenType && other.value == this.value)

      case _ => false

  override def toString: String = s"$tokenType(\"$value\")"

case class LitToken(
    tokenType: TokenType,
    value: String | List[LitToken],
    range: Range,
) derives CanEqual:
  override def equals(obj: Any): Boolean =
    obj match
      case other: LitToken => (other `eq` this) ||
        (other.tokenType == this.tokenType &&
          (other.value match
            case otherValue: String => otherValue ==
                this.value.asInstanceOf[String]
            case otherValue: List[LitToken] => otherValue ==
                this.value.asInstanceOf[List[LitToken]]
          ))

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

enum TokenType(val canonicalSBCS: Option[String] = None) extends Enum[TokenType]
    derives CanEqual:
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
  case UnpackTrigraph
  case MonadicModifier
  case DyadicModifier
  case TriadicModifier
  case TetradicModifier
  case SpecialModifier
  case CompressedString
  case CompressedNumber
  case DictionaryString
  case ContextIndex
  case FunctionCall
  case ModifierSymbol
  case ElementSymbol
  case OriginalSymbol
  case DefineRecord
  case DefineExtension
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
  case MoveRight
  case Group

  /** Helper to help go from the old VyxalToken to the new Token(TokenType,
    * text, range) format
    */
  def apply(text: String): Token = Token(this, text, Range.fake)

  /** Helper to destructure tokens more concisely */
  def unapply(tok: Token): Option[(String | List[Token], Range)] =
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
  case DefineStructure extends StructureType("#::")

object StructureType:
  val lambdaStructures: List[StructureType] = List(
    StructureType.Lambda,
    StructureType.LambdaMap,
    StructureType.LambdaFilter,
    StructureType.LambdaReduce,
    StructureType.LambdaSort,
  )

object Lexer:
  val structureOpenRegex: String = """[\[\(\{λƛΩ₳µḌṆ]|#@|#\{"""

  val Codepage = "ᵃᵇᶜᵈᵉᶠᴳᴴᶤᶨ\nᵏᶪᵐⁿᵒᵖᴿᶳᵗᵘᵛᵂᵡᵞᶻᶴ⸠ϩэЧᵜ !" +
    "\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFG" +
    "HIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmn" +
    "opqrstuvwxyz{|}~ȦḂĊḊĖḞĠḢİĿṀṄȮṖṘṠṪẆẊικȧḃċ" +
    "ḋėḟġḣŀṁṅȯṗṙṡṫẋƒΘΦ§ẠḄḌḤỊḶṂṆỌṚṢṬ…≤≥≠₌⁺⁻⁾√∑«»" +
    "⌐∴∵⊻₀₁₂₃₄₅₆₇₈₉λƛΩ₳µ∆øÞ½ʀɾ¯×÷£¥←↑→↓±¤†Π¬∧∨⁰" + "¹²⌈⌊Ɠɠ∥∦ı„”ð€“¶ᶿᶲ•≈¿ꜝ"

  val UnicodeCommands = "🍪ඞ🌮"

  val StringClosers = "\"„”“"

  def literateModeMappings: Map[String, String] =
    LiterateLexer.literateModeMappings

  def apply(
      code: String
  )(using ctx: Context): List[Token] =
    if ctx.settings.literate then lexLiterate(code) else lexSBCS(code)

  def lexSBCS(code: String): List[Token] = SBCSLexer.lex(code)

  def performMoves(tkns: List[LitToken]): List[LitToken] =
    val tokens = tkns.map {
      case LitToken(TokenType.Group, tokens, range) => LitToken(
          TokenType.Group,
          performMoves(tokens.asInstanceOf[List[LitToken]]),
          range,
        )
      case token => token
    }
    val merged = ListBuffer[LitToken]()
    for token <- tokens do
      if token.tokenType == TokenType.MoveRight then
        if merged.nonEmpty && merged.last.tokenType == TokenType.MoveRight then
          merged.last.copy(value =
            merged.last.value.asInstanceOf[String] +
              token.value.asInstanceOf[String]
          )
        else merged += token
      else merged += token

    // Now, bind the move right tokens to the next token

    val bound = ListBuffer[LitToken | (LitToken, Int)]()
    for token <- merged do
      if bound.nonEmpty then
        bound.last match
          case _: (LitToken, Int) => bound += token
          case last: LitToken =>
            if last.tokenType == TokenType.MoveRight then
              bound.dropRightInPlace(1)
              bound += (token -> last.value.asInstanceOf[String].length)
            else bound += token
      else bound += token

    // Move the tuple2's to the right, storing indices of where they were

    while bound.exists(_.isInstanceOf[(LitToken, Int)]) do
      val index = bound.indexWhere(_.isInstanceOf[(LitToken, Int)])
      val (token, offset) = bound(index).asInstanceOf[(LitToken, Int)]
      bound.remove(index)
      if index + offset >= bound.length then bound += token
      else bound.insert(index + offset, token)

    // And flatten the list into just tokens
    bound.map {
      case (y, _) => y
      case token: LitToken => token
    }.toList

  end performMoves

  def lexLiterate(code: String): List[Token] =
    val tokens = LiterateLexer.lex(code)
    val moved = performMoves(tokens)

    // Convert all tokens into SBCS tokens

    moved
      .map {
        case LitToken(tokenType, value, range) => tokenType match
            case Group => flattenGroup(LitToken(tokenType, value, range))
            case _ => List(LitToken(tokenType, value, range))
      }
      .flatten
      .map {
        case LitToken(tokenType, value, range) => Token(
            tokenType,
            value.asInstanceOf[String],
            range,
          )
      }
  end lexLiterate

  private def flattenGroup(token: LitToken): List[LitToken] =
    token.tokenType match
      case TokenType.Group =>
        token.value.asInstanceOf[List[LitToken]].map(flattenGroup).flatten
      case _ => List(token)

  def isList(code: String): Boolean =
    parse(code, LiterateLexer.list(_)).isSuccess

  def removeSugar(code: String): Option[String] =
    if SBCSLexer.sugarUsed then Some(SBCSLexer.lex(code).map(_.value).mkString)
    else None

  private def sbcsifySingle(token: Token): String =
    val Token(tokenType, value, _) = token

    tokenType match
      case GetVar => "#$" + value
      case SetVar => s"#=$value"
      case AugmentVar => s"#>$value"
      case Constant => s"#!$value"
      case Str => s""""$value""""
      case DictionaryString => s""""$value”"""
      case CompressedString => s""""$value„"""
      case CompressedNumber => s""""$value“"""
      case UnpackTrigraph if value == ":=[" => "#:["
      case ElementSymbol => s"#:@$value "
      case ModifierSymbol => s"#:`$value "
      case DefineRecord => s"#:R $value"
      case FunctionCall => "#$" + value + "Ė"
      case OriginalSymbol => s"#:~$value"
      case Command if !Elements.elements.contains(value) =>
        Elements.symbolFor(value).getOrElse(value.stripSuffix("|"))
      case Comment => ""
      case _ => tokenType.canonicalSBCS.getOrElse(value)
    end match
  end sbcsifySingle

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

/** Lexing exceptions */
enum LexingException(message: String)
    extends vyxal.VyxalException(s"LexingException: $message"):
  case LeftoverCodeException(leftover: String)
      extends LexingException(
        s"Lexing completed with leftover code: '$leftover'"
      )
  case Fastparse(msg: String)
      extends LexingException(s"Lexing with FastParse failed: $msg")
