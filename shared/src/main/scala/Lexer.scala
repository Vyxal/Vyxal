package vyxal

import scala.language.strictEquality

import vyxal.impls.Elements

import java.util.regex.Pattern
import scala.util.matching.Regex
import scala.util.parsing.combinator.*
import scala.util.parsing.input.Position
import scala.util.parsing.input.Positional

import TokenType.*

case class VyxalCompilationError(msg: String)

case class Token(tokenType: TokenType, value: String, range: Range)
    derives CanEqual

/** The range of a token or AST in the source code */
case class Range(startRow: Int, startCol: Int, endRow: Int, endCol: Int)
    derives CanEqual:
  /** Override the default equals method so Range.fake compares equal to
    * everything.
    */
  override def equals(x: Any): Boolean = x match
    case other: Range =>
      (this `eq` Range.fake) ||
      (other `eq` Range.fake) ||
      (other.startRow == startRow && other.startCol == startCol && other.endRow == endRow && other.endCol == endCol)
    case _ => false

object Range:
  /** A dummy Range (mainly for generated/desugared code) */
  val fake = Range(-1, -1, -1, -1)

enum TokenType derives CanEqual:
  case Number
  case Str
  case StructureOpen
  case StructureClose
  case StructureAllClose
  case ListOpen
  case ListClose
  case Command
  case Digraph
  case SugarTrigraph
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
  case Sugared

  /** This is only a temporary bandaid while we go from the old VyxalToken to
    * the new Token(TokenType, text, range) format
    */
  def apply(text: String): Token = Token(this, text, Range.fake)
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

object Lexer extends RegexParsers:
  override def skipWhitespace = true
  override val whiteSpace: Regex = "[ \t\r\f]+".r

  /** Whether the code lexed so far has sugar trigraphs */
  private var sugarUsed = false

  def decimalRegex = raw"(((0|[1-9][0-9]*)?\.[0-9]*|0|[1-9][0-9]*)_?)"
  def number: Parser[Token] =
    parseToken(Number, raw"($decimalRegex?ı($decimalRegex|_)?)|$decimalRegex".r)

  def string: Parser[Token] =
    withPos(raw"""("(?:[^"„”“\\]|\\.)*["„”“])""".r) ^^ { case (value, range) =>
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

  def contextIndex: Parser[Token] = withPos("""\d*¤""".r) ^^ {
    case (value, range) =>
      Token(ContextIndex, value.substring(0, value.length - 1).trim, range)
  }

  def singleCharString: Parser[Token] = withPos("""'.""".r) ^^ {
    case (value, range) =>
      Token(Str, value.substring(1), range)
  }

  def twoCharString: Parser[Token] = withPos("""ᶴ..""".r) ^^ {
    case (value, range) =>
      Token(Str, value.substring(1), range)
  }

  def twoCharNumber: Parser[Token] = withPos("~..".r) ^^ {
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

  val structureOpenRegex = """[\[\(\{λƛΩ₳µḌṆ]|#@|#\{"""
  def structureOpen: Parser[Token] =
    parseToken(StructureOpen, structureOpenRegex.r)

  def structureClose: Parser[Token] = parseToken(StructureClose, """[\}\)]""".r)

  def structureAllClose: Parser[Token] =
    parseToken(StructureAllClose, """\]""".r)

  def listOpen: Parser[Token] = parseToken(ListOpen, """(#\[)|⟨""".r)

  def listClose: Parser[Token] = parseToken(ListClose, """(#\])|⟩""".r)

  def multigraph: Parser[Token] =
    withPos("([∆øÞk].)|(#:\\[)|(#(([:.,^].)|([^\\[\\]$!=#>@{])))".r) ^^ {
      case (value, range) =>
        if value.length == 2 then processDigraph(value, range)
        else if value.charAt(1) == ':' then Token(SyntaxTrigraph, value, range)
        else
          sugarUsed = true
          val temp = SugarMap.trigraphs.getOrElse(value, value)
          apply(temp) match
            case Left(value)  => Token(Command, temp, range)
            case Right(value) => value(0)
    }

  private val commandRegex = CODEPAGE
    .replaceAll(raw"[|\[\](){}]", "")
    .replace("^", "\\^")
  def command: Parser[Token] = parseToken(Command, s"[$commandRegex🍪ඞ]".r)

  def getVariable: Parser[Token] = withPos("""(\#\$)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(GetVar, value.substring(2), range)
  }

  def setVariable: Parser[Token] = withPos("""(\#\=)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(SetVar, value.substring(2), range)
  }

  def setConstant: Parser[Token] = withPos("""(\#\!)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(Constant, value.substring(2), range)
  }

  def augVariable: Parser[Token] = withPos("""(\#\>)[0-9A-Za-z_]*""".r) ^^ {
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

  def branch = parseToken(Branch, raw"\|".r)

  def newlines = parseToken(Newline, "\n".r)

  def tokens: Parser[List[Token]] = phrase(
    rep(
      comment | multigraph | branch | contextIndex | number | string | augVariable | getVariable | setVariable
        | setConstant | twoCharNumber | twoCharString | singleCharString
        | monadicModifier | dyadicModifier | triadicModifier | tetradicModifier
        | specialModifier | structureOpen | structureClose | structureAllClose
        | listOpen | listClose | newlines | command
    )
  )

  private def withPos(parser: Parser[String]): Parser[(String, Range)] =
    class WithPos(val text: String) extends Positional
    positioned(parser.map(WithPos(_))).map { res =>
      val startRow = res.pos.line
      val startCol = res.pos.column
      val endRow = startRow + res.text.count(_ == '\n')
      val endCol =
        if startRow != endRow then
          res.text.length - res.text.lastIndexOf('\n') - 1
        else startCol
      (res.text, Range(startRow, startCol, endRow, endCol))
    }

  private def parseToken(tokenType: TokenType, regex: Regex): Parser[Token] =
    withPos(regex) ^^ { case (value, range) =>
      Token(tokenType, value, range)
    }

  def apply(code: String): Either[VyxalCompilationError, List[Token]] =
    (parse(tokens, code): @unchecked) match
      case NoSuccess(msg, next)  => Left(VyxalCompilationError(msg))
      case Success(result, next) => Right(result)

  def processDigraph(digraph: String, range: Range): Token =
    if Elements.elements.contains(digraph) then Token(Command, digraph, range)
    else if Modifiers.modifiers.contains(digraph) then
      val modifier = Modifiers.modifiers(digraph)
      val tokenType = modifier.arity match
        case 1     => MonadicModifier
        case 2     => DyadicModifier
        case 3     => TriadicModifier
        case 4     => TetradicModifier
        case -1    => SpecialModifier
        case arity => throw Exception(s"Invalid modifier arity: $arity")
      Token(tokenType, digraph, range)
    else Token(Digraph, digraph, range)

  def removeSugar(code: String): Option[String] =
    this.sugarUsed = false
    val lexed = apply(code).toOption
    if sugarUsed then lexed.map(_.map(_.value).mkString)
    else None
end Lexer
