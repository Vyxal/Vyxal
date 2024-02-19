package vyxal.parsing

import scala.collection.mutable // For named imports
import scala.collection.mutable.{
  ArrayBuffer,
  Stack,
  StringBuilder,
} // for things that don't shadow non-mut variants
case class VToken(
    tokenType: VTokenType,
    value: String,
    range: VRange,
) derives CanEqual:
  override def equals(obj: Any): Boolean =
    obj match
      case other: VToken => (other `eq` this) ||
        (other.tokenType == this.tokenType && other.value == this.value)

      case _ => false

  override def toString: String = s"$tokenType(\"$value\")"

case class CommonToken(
    tokenType: VTokenType,
    value: String,
    range: VRange,
)
object VToken:
  def empty: VToken = VToken(VTokenType.Empty, "", VRange.fake)

case class VLitToken(
    tokenType: VTokenType,
    value: String | Seq[VLitToken],
    range: VRange,
) derives CanEqual:
  override def equals(obj: Any): Boolean =
    obj match
      case other: VLitToken => (other `eq` this) ||
        (other.tokenType == this.tokenType &&
          (other.value match
            case otherValue: String => otherValue ==
                this.value.asInstanceOf[String]
            case otherValue: Seq[VLitToken] => otherValue ==
                this.value.asInstanceOf[List[VLitToken]]
          ))

      case _ => false

  override def toString: String = s"$tokenType(\"$value\")"

  def toNormal: VToken = VToken(tokenType, value.toString, range)
end VLitToken

/** The range of a token or AST in the source code. The start offset is
  * inclusive, the end offset is exclusive.
  */
case class VRange(startOffset: Int, endOffset: Int) derives CanEqual:
  def includes(offset: Int): Boolean =
    startOffset <= offset && offset < endOffset

  /** Override the default equals method so Range.fake compares equal to
    * everything.
    */
  override def equals(obj: Any): Boolean =
    obj match
      case other: VRange => (other `eq` this) ||
        (this `eq` VRange.fake) ||
        (other `eq` VRange.fake) ||
        (other.startOffset == this.startOffset &&
          other.endOffset == this.endOffset)
      case _ => false

object VRange:
  /** A dummy Range (mainly for generated/desugared code) */
  val fake: VRange = VRange(-1, -1)

enum VTokenType(val canonicalSBCS: Option[String] = None)
    extends Enum[VTokenType] derives CanEqual:
  case Number
  case Str
  case StructureOpen
  case StructureClose extends VTokenType(Some("}"))
  case StructureDoubleClose extends VTokenType(Some(")"))
  case StructureAllClose extends VTokenType(Some("]"))
  case ListOpen extends VTokenType(Some("#["))
  case ListClose extends VTokenType(Some("#]"))
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
  case Branch extends VTokenType(Some("|"))
  case Newline extends VTokenType(Some("\n"))
  case Param
  case UnpackClose extends VTokenType(Some("]"))
  case GroupType
  case NegatedCommand
  case MoveRight
  case Group
  case Empty

  /** Helper to help go from the old VyxalToken to the new Token(TokenType,
    * text, range) format
    */
  def apply(text: String): VToken = VToken(this, text, VRange.fake)

  /** Helper to destructure tokens more concisely */
  def unapply(tok: VToken): Option[(String | List[VToken], VRange)] =
    if tok.tokenType == this then Some((tok.value, tok.range)) else None
end VTokenType

enum VStructureType(val open: String) derives CanEqual:
  case Ternary extends VStructureType("[")
  case While extends VStructureType("{")
  case For extends VStructureType("(")
  case Lambda extends VStructureType("λ")
  case LambdaMap extends VStructureType("ƛ")
  case LambdaFilter extends VStructureType("Ω")
  case LambdaReduce extends VStructureType("₳")
  case LambdaSort extends VStructureType("µ")
  case IfStatement extends VStructureType("#{")
  case DecisionStructure extends VStructureType("Ḍ")
  case GeneratorStructure extends VStructureType("Ṇ")
  case DefineStructure extends VStructureType("#::")

object VStructureType:
  val lambdaStructures: List[VStructureType] = List(
    VStructureType.Lambda,
    VStructureType.LambdaMap,
    VStructureType.LambdaFilter,
    VStructureType.LambdaReduce,
    VStructureType.LambdaSort,
  )

object Vexer:

  def lexSBCS(program: String): Seq[VToken] =
    val lexer = SBCSVexer()
    lexer.lex(program)

  def lexLiterate(program: String): Seq[VToken] =
    val lexer = LiterateVexer()
    lexer.lex(program)

abstract class VexerCommon:

  private val stringTokenToQuote = Map(
    VTokenType.Str -> "\"",
    VTokenType.CompressedString -> "„",
    VTokenType.DictionaryString -> "”",
    VTokenType.CompressedNumber -> "“",
  )

  protected var index = 0
  val symbolTable = mutable.Map[String, Option[Int]]()
  protected val programStack = Stack[String]()
  protected val tokens = ArrayBuffer[VToken]()

  // Abstract method for adding a token irregardles of lexer type

  protected def addToken(
      tokenType: VTokenType,
      value: String,
      range: VRange,
  ): Unit

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
  protected def quickToken(tokenType: VTokenType, value: String): Unit =
    addToken(tokenType, value, VRange(index, index + value.length))
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
          case "\"" => VTokenType.Str
          case "„" => VTokenType.CompressedString
          case "”" => VTokenType.DictionaryString
          case "“" => VTokenType.CompressedNumber
      else VTokenType.Str

    addToken(
      tokenType,
      text,
      VRange(rangeStart, index),
    )
    return text
  end stringToken
  protected def lambdaParameters: String =
    var depth = 1
    val popped = StringBuilder()
    val start = index
    var branchFound = false
    var stringPopped = false

    while depth > 0 && !branchFound && programStack.nonEmpty do
      stringPopped = false
      if headIsOpener then depth += 1
      else if headEqual("\"") then
        stringPopped = true
        popped += '"'
        popped ++= stringToken
        popped ++= stringTokenToQuote(tokens.last.tokenType)
        tokens.dropRightInPlace(1)
      else if headIsCloser then depth -= 1
      else if headIsBranch && depth == 1 then branchFound = true
      if !stringPopped && !branchFound then popped ++= pop()
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
          VTokenType.Param,
          toValidParam(param),
          VRange(paramStart, paramEnd),
        )

    paramTokens.toSeq

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
      VTokenType.GetVar,
      name,
      VRange(rangeStart, index),
    )

  protected def setVariableToken: Unit =
    val rangeStart = index
    val name = simpleName()
    addToken(
      VTokenType.SetVar,
      name,
      VRange(rangeStart, index),
    )

  protected def setConstantToken: Unit =
    val rangeStart = index
    val name = simpleName()
    addToken(
      VTokenType.Constant,
      name,
      VRange(rangeStart, index),
    )

  protected def augmentedAssignToken: Unit =
    val rangeStart = index
    val name = simpleName()
    addToken(
      VTokenType.AugmentVar,
      name,
      VRange(rangeStart, index),
    )

  protected def originalCommandToken: Unit =
    val rangeStart = index
    val command = pop()
    addToken(
      VTokenType.OriginalSymbol,
      command,
      VRange(rangeStart, index),
    )

  protected def commandSymbolToken: Unit =
    val rangeStart = index
    val command = pop()
    addToken(
      VTokenType.ElementSymbol,
      command,
      VRange(rangeStart, index),
    )

  protected def modifierSymbolToken: Unit =
    val rangeStart = index
    val command = pop()
    addToken(
      VTokenType.ModifierSymbol,
      command,
      VRange(rangeStart, index),
    )

  protected def defineRecordToken: Unit =
    val rangeStart = index
    eatWhitespace()
    val name = simpleName()
    addToken(
      VTokenType.DefineRecord,
      name,
      VRange(rangeStart, index),
    )
end VexerCommon

def Codepage: String = """⎂⇝∯⊠ß≟₾◌⋊
ϩэЧ♳♴♵♶∥∦¿⎇↻⊙⁙∩∪⊕⊝⚇῟⚃ !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~ȦḂĊḊĖḞĠḢİĿṀṄȮṖṘṠṪẆẊικȧḃċḋėḟġḣŀṁṅȯṗṙṡṫẋƒΘΦ§ẠḄḌḤỊḶṂṆỌṚṢṬ…≤≥≠₌⁺⁻⁾√∑«»⌐∴∵⊻₀₁₂₃₄₅₆₇₈₉λƛΩ₳µ∆øÞ½ʀɾ¯×÷£¥←↑→↓±¤†Π¬∧∨⁰¹²⌈⌊Ɠɠı┉„”ð€“¶ᶿᶲ•≈⌙‹›"""
