package vyxal

import scala.language.strictEquality

import vyxal.impls.Elements
import vyxal.TokenType.*

import java.util.regex.Pattern
import scala.collection.mutable.{ListBuffer, Queue}
import scala.util.matching.Regex
import scala.util.parsing.combinator.*
import scala.util.parsing.input.Position
import scala.util.parsing.input.Positional

case class VyxalCompilationError(msg: String)

case class Token(tokenType: TokenType, value: String, range: Range)
    derives CanEqual:
  override def equals(obj: Any): Boolean = obj match
    case other: Token =>
      (other `eq` this) || (other.tokenType == this.tokenType && other.value == this.value)
    case _ => false

/** The range of a token or AST in the source code */
case class Range(startRow: Int, startCol: Int, endRow: Int, endCol: Int)
    derives CanEqual:
  /** Override the default equals method so Range.fake compares equal to
    * everything.
    */
  override def equals(obj: Any): Boolean = obj match
    case other: Range =>
      (other `eq` this) ||
      (this `eq` Range.fake) ||
      (other `eq` Range.fake) ||
      (other.startRow == startRow && other.startCol == startCol && other.endRow == endRow && other.endCol == endCol)
    case _ => false

object Range:
  /** A dummy Range (mainly for generated/desugared code) */
  val fake: Range = Range(-1, -1, -1, -1)

enum TokenType derives CanEqual:
  case Number
  case Str
  case StructureOpen
  case StructureClose
  case StructureDoubleClose
  case StructureAllClose
  case ListOpen
  case ListClose
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
  case Branch
  case Newline
  case Param

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

object Lexer:
  val decimalRegex: Regex = raw"(((0|[1-9][0-9]*)?\.[0-9]*|0|[1-9][0-9]*)_?)".r
  val structureOpenRegex: String = """[\[\(\{λƛΩ₳µḌṆ]|#@|#\{"""

  def apply(code: String): Either[VyxalCompilationError, List[Token]] =
    new Lexer().lex(code)

  def removeSugar(code: String): Option[String] =
    val lexer = new Lexer()
    lexer.lex(code) match
      case Right(result) =>
        if lexer.sugarUsed then Some(result.map(_.value).mkString)
        else None
      case _ => None

class Lexer extends RegexParsers:
  import vyxal.Lexer.decimalRegex

  override def skipWhitespace = true
  override val whiteSpace: Regex = "[ \t\r\f]+".r

  /** Whether the code lexed so far has sugar trigraphs */
  private var sugarUsed = false

  def lex(code: String): Either[VyxalCompilationError, List[Token]] =
    (parseAll(tokens, code): @unchecked) match
      case NoSuccess(msg, next) => Left(VyxalCompilationError(msg))
      case Success(result, next) => Right(result)

  def number: Parser[Token] =
    parseToken(Number, raw"($decimalRegex?ı($decimalRegex|_)?)|$decimalRegex".r)

  def string: Parser[Token] =
    withRange(raw"""("(?:[^"„”“\\]|\\.)*["„”“])""".r) ^^ {
      case (value, range) =>
        // If the last character of each token is ", then it's a normal string
        // If the last character of each token is „, then it's a compressed string
        // If the last character of each token is ”, then it's a dictionary string
        // If the last character of each token is “, then it's a compressed number

        // So replace the non-normal string tokens with the appropriate token type

        // btw thanks to @pxeger and @mousetail for the regex

        val text = value
          .substring(1, value.length - 1)
          .replace("\\\"", "\"")
          .replace(raw"\n", "\n")
          .replace(raw"\t", "\t")

        val tokenType = (value.last: @unchecked) match
          case '"' => Str
          case '„' => CompressedString
          case '”' => DictionaryString
          case '“' => CompressedNumber

        Token(tokenType, text, range)
    }

  def contextIndex: Parser[Token] = withRange("""\d*¤""".r) ^^ {
    case (value, range) =>
      Token(ContextIndex, value.substring(0, value.length - 1).trim, range)
  }

  def singleCharString: Parser[Token] = withRange("'.".r) ^^ {
    case (value, range) =>
      Token(Str, value.substring(1), range)
  }

  def twoCharString: Parser[Token] = withRange("ᶴ..".r) ^^ {
    case (value, range) =>
      Token(Str, value.substring(1), range)
  }

  def twoCharNumber: Parser[Token] = withRange("~..".r) ^^ {
    case (value, range) =>
      Token(
        Number,
        value
          .substring(1)
          .zipWithIndex
          .map((c, ind) => math.pow(CODEPAGE.length, ind) * CODEPAGE.indexOf(c))
          .sum
          .toString,
        range
      )
  }

  def structureOpen: Parser[Token] =
    parseToken(StructureOpen, Lexer.structureOpenRegex.r)

  def structureSingleClose: Parser[Token] = parseToken(StructureClose, "}")

  def structureDoubleClose: Parser[Token] =
    parseToken(StructureDoubleClose, ")")

  def structureAllClose: Parser[Token] =
    parseToken(StructureAllClose, "]")

  def listOpen: Parser[Token] = parseToken(ListOpen, """(#\[)|⟨""".r)

  def listClose: Parser[Token] = parseToken(ListClose, """#]|⟩""".r)

  def digraph: Parser[Token] = withRange("[∆øÞk].|#[^\\[\\]$!=#>@{]".r) ^^ {
    case (digraph, range) =>
      if Elements.elements.contains(digraph) then Token(Command, digraph, range)
      else if Modifiers.modifiers.contains(digraph) then
        val modifier = Modifiers.modifiers(digraph)
        val tokenType = modifier.arity match
          case 1 => MonadicModifier
          case 2 => DyadicModifier
          case 3 => TriadicModifier
          case 4 => TetradicModifier
          case -1 => SpecialModifier
          case arity => throw Exception(s"Invalid modifier arity: $arity")
        Token(tokenType, digraph, range)
      else Token(Digraph, digraph, range)
  }

  def syntaxTrigraph: Parser[Token] = parseToken(SyntaxTrigraph, "#:.".r)

  def sugarTrigraph: Parser[Token] =
    withRange("#[.,^].".r) ^^ { case (value, range) =>
      this.sugarUsed = true
      SugarMap.trigraphs
        .get(value)
        .flatMap(char => this.lex(char).toOption.map(_.head))
        .getOrElse(Token(Command, value, range))
    }

  private val commandRegex = CODEPAGE
    .replaceAll(raw"[|\[\](){}]", "")
    .replace("^", "\\^")
  def command: Parser[Token] = parseToken(Command, s"[$commandRegex🍪ඞ]".r)

  def getVariable: Parser[Token] = withRange("""(#\$)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(GetVar, value.substring(2), range)
  }

  def setVariable: Parser[Token] = withRange("""(#=)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(SetVar, value.substring(2), range)
  }

  def setConstant: Parser[Token] = withRange("""(#!)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(Constant, value.substring(2), range)
  }

  def augVariable: Parser[Token] = withRange("""(#>)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(AugmentVar, value.substring(2, value.length), range)
  }

  def monadicModifier: Parser[Token] =
    parseToken(MonadicModifier, s"""[$MONADIC_MODIFIERS]""".r)

  def dyadicModifier: Parser[Token] =
    parseToken(DyadicModifier, s"""[$DYADIC_MODIFIERS]""".r)

  def triadicModifier: Parser[Token] =
    parseToken(TriadicModifier, s"""[$TRIADIC_MODIFIERS]""".r)

  def tetradicModifier: Parser[Token] =
    parseToken(TetradicModifier, s"""[$TETRADIC_MODIFIERS]""".r)

  def specialModifier: Parser[Token] =
    parseToken(SpecialModifier, s"""[$SPECIAL_MODIFIERS]""".r)

  def comment: Parser[Token] = parseToken(Comment, """##[^\n]*""".r)

  def branch: Parser[Token] = parseToken(Branch, "|")

  def newlines: Parser[Token] = parseToken(Newline, "\n")

  def token: Parser[Token] =
    comment | sugarTrigraph | syntaxTrigraph | digraph | branch | contextIndex
      | number | string | augVariable | getVariable | setVariable
      | setConstant | twoCharNumber | twoCharString | singleCharString
      | monadicModifier | dyadicModifier | triadicModifier | tetradicModifier
      | specialModifier | structureOpen | structureSingleClose | structureAllClose
      | listOpen | listClose | newlines | command

  // structureDoubleClose (")") has to be here to avoid interfering with `normalGroup` in literate lexer
  def tokens: Parser[List[Token]] = rep(token | structureDoubleClose)

  protected def withStartPos[T](parser: Parser[T]): Parser[(T, Int, Int)] =
    class WithPos(val value: T) extends Positional
    positioned(parser.map(WithPos(_))).map { res =>
      (res.value, res.pos.line, res.pos.column)
    }

  protected def withRange(parser: Parser[String]): Parser[(String, Range)] =
    withStartPos(parser).map { (value, startRow, startCol) =>
      val endRow = startRow + value.count(_ == '\n')
      val endCol =
        if startRow != endRow then value.length - value.lastIndexOf('\n') - 1
        else startCol
      (value, Range(startRow, startCol, endRow, endCol))
    }

  protected def parseToken(
      tokenType: TokenType,
      tokenParser: Parser[String]
  ): Parser[Token] =
    withRange(tokenParser) ^^ { case (value, range) =>
      Token(tokenType, value, range)
    }
end Lexer
