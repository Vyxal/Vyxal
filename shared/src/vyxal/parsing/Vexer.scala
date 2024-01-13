package vyxal.parsing

case class VToken(
    tokenType: VTokenType,
    value: String,
    range: Range,
) derives CanEqual:
  override def equals(obj: Any): Boolean =
    obj match
      case other: VToken => (other `eq` this) ||
        (other.tokenType == this.tokenType && other.value == this.value)

      case _ => false

  override def toString: String = s"$tokenType(\"$value\")"

case class VLitToken(
    tokenType: VTokenType,
    value: String | List[VLitToken],
    range: Range,
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

  /** Helper to help go from the old VyxalToken to the new Token(TokenType,
    * text, range) format
    */
  def apply(text: String): VToken = VToken(this, text, Range.fake)

  /** Helper to destructure tokens more concisely */
  def unapply(tok: VToken): Option[(String | List[VToken], Range)] =
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

object Vexer {}
