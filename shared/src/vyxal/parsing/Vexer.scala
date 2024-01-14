package vyxal.parsing

import scala.collection.mutable.{ArrayBuffer, Stack, StringBuilder}

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
  def lookaheadEquals(program: Stack[Char], str: String): Boolean =
    program.take(str.length).mkString == str

  def lookaheadIn(program: Stack[Char], str: String): Boolean =
    str.contains(program.head)
  def simpleNumber(program: Stack[Char]): String =
    val number = StringBuilder()
    if program.head == '0' then
      program.pop()
      return "0"

    while program.nonEmpty && program.head.isDigit do number += program.pop()

    number.result()

  def simpleDecimal(program: Stack[Char]): String =
    val number = StringBuilder()
    if program.head.isDigit then
      number ++= simpleNumber(program)
      if program.nonEmpty && program.head == '.' then
        number += program.pop()
        if program.nonEmpty && program.head.isDigit then
          number ++= simpleNumber(program)
      number.result()
    else if program.head == '.' then
      number += program.pop()
      if program.head.isDigit then number ++= simpleNumber(program)
      number.result()
    else "0"

  def number(program: Stack[Char]): String =
    val number = StringBuilder()
    if program.head.isDigit || program.head == '.' then
      number ++= simpleDecimal(program)
      if program.nonEmpty && program.head == 'ı' then
        number += program.pop()
        number ++= simpleNumber(program)
    else if program.head == 'ı' then
      number += program.pop()
      if program.nonEmpty && (program.head.isDigit || program.head == '.') then
        number ++= simpleNumber(program)
    else number ++= "0"
    if program.nonEmpty && program.head == '_' then number += program.pop()
    number.result()

  def string(program: Stack[Char]): String =
    val string = StringBuilder()
    if program.head == '"' then
      string += program.pop()
      while program.nonEmpty && !STRING_CLOSER(program.head) do
        if program.head == '\\' then
          program.pop()
          string += program.pop()
        else string += program.pop()
    string.result()

  def STRING_CLOSER(c: Char): Boolean =
    c == '"' || c == '„' || c == '”' || c == '“'

  def name(program: Stack[Char]): String =
    val name = StringBuilder()
    if program.head.isLetter || program.head == '_' then
      name += program.pop()
      while program.nonEmpty && program.head.isLetterOrDigit do
        name += program.pop()
    name.result()

  def getVar(program: Stack[Char]): String =
    val thisName = StringBuilder()

    thisName ++= "#$"
    program.pop()
    program.pop()
    thisName ++= name(program)
    thisName.result()

  def setVar(program: Stack[Char]): String =
    val thisName = StringBuilder()
    thisName ++= "#="
    program.pop()
    program.pop()
    thisName ++= name(program)
    thisName.result()

  def digraph(program: Stack[Char]): String =
    val graph = StringBuilder()
    if lookaheadIn(program, "∆øÞ") then
      graph += program.pop()
      graph += program.pop()
    else if program.head == '#' then
      // Temporarily remove the # from the queue
      program.pop()
      // and check that this isn't a trigraph
      if lookaheadIn(program, "[]$!=#>@{:") then program.push('#')
      else
        graph += '#'
        graph += program.pop()
    graph.result()

  def lex(program: String): Seq[VToken] =
    val tokens = ArrayBuffer[VToken]()

    val programStack = Stack[Char]()
    programStack.pushAll(program.reverseIterator)

    var index = 0

    while programStack.nonEmpty do
      val char = programStack.head
      if char != ' ' then
        val (tokenType, tokenValue) = char match
          case '"' => VTokenType.Str -> string(programStack)
          case _: Char if char.isDigit =>
            VTokenType.Number -> number(programStack)
          case _: Char if lookaheadIn(programStack, "∆øÞ#") =>
            VTokenType.Digraph -> digraph(programStack)
          case _: Char if lookaheadEquals(programStack, "#$") =>
            VTokenType.GetVar -> getVar(programStack)
          case _: Char if lookaheadEquals(programStack, "#=") =>
            VTokenType.SetVar -> setVar(programStack)
          case _ => VTokenType.Command -> char.toString
        val range = VRange(index, index + tokenValue.length)
        index += tokenValue.length
        tokens += VToken(tokenType, tokenValue, range)
      else
        programStack.pop()
        index += 1
    end while
    tokens.toSeq
  end lex
end Vexer
