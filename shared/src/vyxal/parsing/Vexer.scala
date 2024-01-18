package vyxal.parsing

import scala.collection.mutable.{ArrayBuffer, StringBuilder}
import scala.collection.mutable.Stack

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

object VToken:
  def empty: VToken = VToken(VTokenType.Empty, "", VRange.fake)

case class VLitToken(
    tokenType: VTokenType,
    value: String | List[VLitToken],
    range: VRange,
) derives CanEqual:
  override def equals(obj: Any): Boolean =
    obj match
      case other: VLitToken => (other `eq` this) ||
        (other.tokenType == this.tokenType &&
          (other.value match
            case otherValue: String => otherValue ==
                this.value.asInstanceOf[String]
            case otherValue: List[VLitToken] => otherValue ==
                this.value.asInstanceOf[List[VLitToken]]
          ))

      case _ => false

  override def toString: String = s"$tokenType(\"$value\")"

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

class Vexer(val program: String = ""):
  private var index = 0
  private val programStack = Stack[Char]()
  private val tokens = ArrayBuffer[VToken]()
  private def pop(n: Int = 1): String =
    val res = StringBuilder()
    for _ <- 0 until n do res ++= s"${programStack.pop()}"
    index += n
    res.toString()
  private def safeCheck(pred: Char => Boolean): Boolean =
    programStack.nonEmpty && pred(programStack.head)
  private def headEqual(c: Char): Boolean =
    programStack.nonEmpty && programStack.head == c

  def lex: Seq[VToken] =
    programStack.pushAll(program.reverse)

    while programStack.nonEmpty do
      if program.head.isDigit || program.head == '.' then numberToken
      if program.head.isWhitespace then pop(1)

    tokens.toSeq

  private def numberToken: Unit =
    val rangeStart = index
    if headEqual('0') then
      val zeroToken = VToken(VTokenType.Number, "0", VRange(index, index))
      pop(1)
      tokens += zeroToken
    else if headEqual('.') then
      if safeCheck(c => c.isDigit) then
        val head = simpleNumber()
        val numberToken = VToken(
          VTokenType.Number,
          s"0.$head",
          VRange(rangeStart, index),
        )
        tokens += numberToken
      else
        val zeroToken = VToken(
          VTokenType.Number,
          "0.5",
          VRange(rangeStart, index),
        )
        tokens += zeroToken
    else
      val head = simpleNumber()
      if program.head == '.' then // Check for the decimal tail
        pop(1)
        if safeCheck(c => c.isDigit) then
          val tail = simpleNumber()
          val numberToken = VToken(
            VTokenType.Number,
            s"$head.$tail",
            VRange(rangeStart, index),
          )
          tokens += numberToken
        else
          val numberToken = VToken(
            VTokenType.Number,
            s"$head.5",
            VRange(rangeStart, index),
          )
          tokens += numberToken
      else
        val numberToken = VToken(
          VTokenType.Number,
          head,
          VRange(rangeStart, index),
        )
        tokens += numberToken
    end if
  end numberToken

  private def simpleNumber(): String =
    val numberVal = StringBuilder()
    while safeCheck(c => c.isDigit) do numberVal ++= s"${pop()}"
    numberVal.toString()

end Vexer
