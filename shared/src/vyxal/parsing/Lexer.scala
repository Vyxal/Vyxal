package vyxal.parsing

import vyxal.Context
import vyxal.Elements

import scala.collection.mutable // For named imports
import scala.collection.mutable.{
  ArrayBuffer,
  Stack,
  StringBuilder,
} // for things that don't shadow non-mut variants

import TokenType.*

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

case class CommonToken(
    tokenType: TokenType,
    value: String,
    range: Range,
)
object Token:
  def empty: Token = Token(TokenType.Empty, "", Range.fake)

case class LitToken(
    tokenType: TokenType,
    value: String | Seq[LitToken],
    range: Range,
) derives CanEqual:
  override def equals(obj: Any): Boolean =
    obj match
      case other: LitToken => (other `eq` this) ||
        (other.tokenType == this.tokenType &&
          (other.value match
            case otherValue: String => otherValue ==
                this.value.asInstanceOf[String]
            case otherValue: Seq[LitToken] => otherValue ==
                this.value.asInstanceOf[List[LitToken]]
          ))

      case _ => false

  override def toString: String = s"$tokenType(\"$value\")"

  def toNormal: Token = Token(tokenType, value.toString, range)
end LitToken

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
  case Empty

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
  val StringClosers = "\"„”“"

  def lexSBCS(program: String): Seq[Token] =
    val lexer = SBCSLexer()
    lexer.lex(program)

  def lexLiterate(program: String): Seq[Token] =
    val lexer = LiterateLexer()
    lexer.lex(program)

  def lex(program: String)(using ctx: Context): Seq[Token] =
    if ctx.settings.literate then lexLiterate(program)
    else lexSBCS(program)

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
  def sbcsify(tokens: Seq[Token]): String =
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

abstract class LexerCommon:

  private val stringTokenToQuote = Map(
    TokenType.Str -> "\"",
    TokenType.CompressedString -> "„",
    TokenType.DictionaryString -> "”",
    TokenType.CompressedNumber -> "“",
  )

  protected var index = 0
  val symbolTable = mutable.Map[String, Option[Int]]()
  protected val programStack = Stack[String]()
  protected val tokens = ArrayBuffer[Token]()

  // Abstract method for adding a token irregardles of lexer type

  protected def addToken(
      tokenType: TokenType,
      value: String,
      range: Range,
  ): Unit

  protected def dropLastToken(): Unit

  private def addToken(tok: CommonToken): Unit =
    addToken(tok.tokenType, tok.value, tok.range)

  protected def pop(n: Int = 1): String =
    val res = StringBuilder()
    for _ <- 0 until n do res ++= programStack.pop()
    index += n + 1
    res.toString()
  protected def safeCheck(pred: String => Boolean): Boolean =
    programStack.nonEmpty && pred(programStack.head)
  protected def headEqual(c: String): Boolean =
    programStack.nonEmpty && programStack.head == c
  protected def headLookaheadEqual(s: String): Boolean =
    programStack.mkString.length >= s.length &&
      programStack.mkString.startsWith(s)
  protected def headLookaheadMatch(s: String): Boolean =
    programStack.nonEmpty &&
      ("^" + s).r.findFirstIn(programStack.mkString).isDefined
  protected def headIsDigit: Boolean = safeCheck(c => c.head.isDigit)
  protected def headIsWhitespace: Boolean = safeCheck(c => c.head.isWhitespace)
  protected def headIn(s: String): Boolean = safeCheck(c => s.contains(c))
  protected def headIsCloser: Boolean
  protected def headIsBranch: Boolean
  protected def headIsOpener: Boolean
  protected def quickToken(tokenType: TokenType, value: String): Unit =
    addToken(tokenType, value, Range(index, index + value.length))
    index += value.length
    pop(value.length)
  protected def eat(s: String): Unit =
    if headLookaheadEqual(s) then pop(s.length)
    else
      throw new Exception(
        s"Expected $s, got ${programStack.mkString}" + s" at index $index"
      )
  protected def eatWhitespace(): Unit = while headIsWhitespace do pop()

  // Some universal token types
  /** String = '"' (\\.|[^„”“"])* [„”“"] */
  protected def stringToken: String =
    val rangeStart = index
    val stringVal = StringBuilder()

    pop() // Pop the opening quote

    while programStack.nonEmpty && !headIn("\"„”“") do
      if headEqual("\\") then stringVal ++= pop(2)
      else stringVal ++= pop()

    val text = stringVal
      .toString()
      .replace("\\\"", "\"")
      .replace(raw"\n", "\n")
      .replace(raw"\t", "\t")

    val tokenType =
      if programStack.nonEmpty then
        pop() match
          case "\"" => TokenType.Str
          case "„" => TokenType.CompressedString
          case "”" => TokenType.DictionaryString
          case "“" => TokenType.CompressedNumber
      else TokenType.Str

    addToken(
      tokenType,
      text,
      Range(rangeStart, index),
    )
    return text
  end stringToken
  protected def lambdaParameters: String =
    var break = false
    val popped = StringBuilder()
    val start = index
    var branchFound = false
    var stringPopped = false

    while !break && !branchFound && programStack.nonEmpty do
      stringPopped = false
      if headIsOpener then break = true
      else if headEqual("\"") then
        stringPopped = true
        popped += '"'
        popped ++= stringToken
        popped ++= stringTokenToQuote(tokens.last.tokenType)
        dropLastToken()
      else if headIsBranch && !headEqual(",") then branchFound = true

      if !break && !stringPopped && !branchFound then popped ++= pop()
    val params = popped.toString()
    if !branchFound then
      for c <- params.reverse do programStack.push(c.toString())
      index -= popped.length
    else for tok <- extractParamters(popped.toString(), start) do addToken(tok)

    return params
  end lambdaParameters

  protected def extractParamters(popped: String, start: Int): Seq[CommonToken] =
    val params = popped.split(',')
    val paramTokens = ArrayBuffer[CommonToken]()
    for param <- params do
      val paramStart = start + popped.indexOf(param)
      val paramEnd = paramStart + param.length
      paramTokens +=
        CommonToken(
          TokenType.Param,
          toValidParam(param),
          Range(paramStart, paramEnd),
        )
      paramTokens +=
        CommonToken(
          TokenType.Command,
          ",",
          Range(paramEnd, paramEnd + 1),
        )

    paramTokens.dropRightInPlace(1) // Remove the trailing comma

    paramTokens.toSeq
  end extractParamters

  protected def toValidParam(param: String): String =
    val filtered =
      param.filter(c => c.isLetterOrDigit || c == '_' || c == '*' || c == '!')
    if filtered.isEmpty then filtered
    else if filtered.head.isDigit && !filtered.forall(c => c.isDigit) then
      filtered.dropWhile(c => c.isDigit)
    else if filtered == "*" then filtered
    else if filtered == "!" then filtered
    else filtered.replaceAll(raw"\*", "")

  /** Returns how many arguments a parameter list expects. None represents an
    * arity that can't be determined statically. Some(-1) represents a stack
    * lambda Some(n) represents a lambda with a fixed arity of n
    *
    * @param params
    * @return
    */
  protected def calcArity(params: String): Option[Int] =
    var arity: Option[Int] = Some(0)
    try
      for param <- params.split(",") do
        val actual = toValidParam(param)
        if actual == "*" then
          arity = None
          throw Exception()
        else if actual == "!" then
          arity = Some(-1)
          throw Exception()
        else if param.forall(c => c.isDigit) then
          val num = param.toInt
          arity = arity.map(_ + num)
        else arity = arity.map(_ + 1)
    catch
      case _: Exception =>
        // Poor man's break
        ???

    return arity
  end calcArity

  protected def simpleName(): String =
    val name = StringBuilder()
    if headLookaheadEqual("_") then name ++= pop()
    while safeCheck(c => c.head.isLetterOrDigit || c == "_") do
      name ++= s"${pop()}"
    name.toString()

  protected def getVariableToken: Unit =
    val rangeStart = index
    val name = simpleName()
    addToken(
      TokenType.GetVar,
      name,
      Range(rangeStart, index),
    )

  protected def setVariableToken: Unit =
    val rangeStart = index
    val name = simpleName()
    addToken(
      TokenType.SetVar,
      name,
      Range(rangeStart, index),
    )

  protected def setConstantToken: Unit =
    val rangeStart = index
    val name = simpleName()
    addToken(
      TokenType.Constant,
      name,
      Range(rangeStart, index),
    )

  protected def augmentedAssignToken: Unit =
    val rangeStart = index
    val name = simpleName()
    addToken(
      TokenType.AugmentVar,
      name,
      Range(rangeStart, index),
    )

  protected def originalCommandToken: Unit =
    val rangeStart = index
    val command = pop()
    addToken(
      TokenType.OriginalSymbol,
      command,
      Range(rangeStart, index),
    )

  protected def commandSymbolToken: Unit =
    val rangeStart = index
    val name = simpleName()
    addToken(
      TokenType.ElementSymbol,
      name,
      Range(rangeStart, index),
    )

  protected def modifierSymbolToken: Unit =
    val rangeStart = index
    val name = simpleName()
    addToken(
      TokenType.ModifierSymbol,
      name,
      Range(rangeStart, index),
    )

  protected def defineRecordToken: Unit =
    val rangeStart = index
    eatWhitespace()
    val name = simpleName()
    addToken(
      TokenType.DefineRecord,
      name,
      Range(rangeStart, index),
    )
end LexerCommon

def Codepage: String = """⎂⇝∯⊠ß≟₾◌⋊
ϩэЧ♳♴♵♶∥∦¿⎇↻⊙⁙∩∪⊕⊝⚇῟⚃ !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~ȦḂĊḊĖḞĠḢİĿṀṄȮṖṘṠṪẆẊικȧḃċḋėḟġḣŀṁṅȯṗṙṡṫẋƒΘΦ§ẠḄḌḤỊḶṂṆỌṚṢṬ…≤≥≠₌⁺⁻⁾√∑«»⌐∴∵⊻₀₁₂₃₄₅₆₇₈₉λƛΩ₳µ∆øÞ½ʀɾ¯×÷£¥←↑→↓±¤†Π¬∧∨⁰¹²⌈⌊Ɠɠı┉„”ð€“¶ᶿᶲ•≈⌙‹›"""
