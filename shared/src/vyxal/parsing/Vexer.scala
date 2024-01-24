package vyxal.parsing

import vyxal.parsing.SBCSLexer.sugarTrigraph
import vyxal.SugarMap
import vyxal.VyxalException

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
    index += n + 1
    res.toString()
  private def safeCheck(pred: Char => Boolean): Boolean =
    programStack.nonEmpty && pred(programStack.head)
  private def headEqual(c: Char): Boolean =
    programStack.nonEmpty && programStack.head == c
  private def headLookaheadEqual(s: String): Boolean =
    programStack.length >= s.length &&
      programStack.slice(0, s.length).mkString == s
  private def headLookaheadMatch(s: String): Boolean =
    programStack.nonEmpty && s.r.findFirstIn(programStack.mkString).isDefined
  private def headIsDigit: Boolean = safeCheck(c => c.isDigit)
  private def headIsWhitespace: Boolean = safeCheck(c => c.isWhitespace)
  private def headIn(s: String): Boolean = safeCheck(c => s.contains(c))
  private def quickToken(tokenType: VTokenType, value: String): Unit =
    tokens += VToken(tokenType, value, VRange(index, index + value.length))
    index += value.length
    pop(value.length)
  private def eat(c: Char): Unit =
    if headEqual(c) then pop(1)
    else
      throw new Exception(
        s"Expected $c, got ${programStack.head}" + s" at index $index"
      )
  private def eat(s: String): Unit =
    if headLookaheadEqual(s) then pop(s.length)
    else
      throw new Exception(
        s"Expected $s, got ${programStack.mkString}" + s" at index $index"
      )
  private def eatWhitespace(): Unit = while headIsWhitespace do pop()

  def lex: Seq[VToken] =
    programStack.pushAll(program.reverse)

    while programStack.nonEmpty do
      if headIsDigit || headEqual('.') then numberToken
      else if headIsWhitespace then pop(1)
      else if headEqual('"') then stringToken
      else if headEqual('\'') then oneCharStringToken
      else if headEqual('῟') then twoCharStringToken
      else if headEqual('⚇') then twoCharNumberToken
      else if headIn("∆øÞ") || headLookaheadMatch("""#[^\[\]$!=#>@{:.,^]""")
      then digraphToken
      else if headLookaheadEqual("##") then
        pop(2)
        while safeCheck(c => c != '\n' && c != '\r') do pop()
      else if headLookaheadMatch("#[.,^]") then sugarTrigraph
      else if headLookaheadEqual("#[") then
        quickToken(VTokenType.ListOpen, "#[")
      else if headLookaheadEqual("#]") then
        quickToken(VTokenType.ListClose, "#]")
      else if headIn("[({ṆḌλƛΩ₳µ") then
        quickToken(VTokenType.StructureOpen, s"${programStack.head}")
      else if headLookaheadEqual("#{") then
        quickToken(VTokenType.StructureOpen, "#{")
      else if headLookaheadEqual("#:[") then
        quickToken(VTokenType.UnpackTrigraph, "#:[")
      else if headIn("⎂⇝∯⊠ß≟₾◌v⸠♳¿⎇↻⁙") then
        quickToken(VTokenType.MonadicModifier, s"${programStack.head}")
      else if headIn("ϩ∥∦♴⁙") then
        quickToken(VTokenType.DyadicModifier, s"${programStack.head}")
      else if headIn("э♵") then
        quickToken(VTokenType.TriadicModifier, s"${programStack.head}")
      else if headIn("Ч♶") then
        quickToken(VTokenType.TetradicModifier, s"${programStack.head}")
      else if headIn("⋊⊙") then
        quickToken(VTokenType.SpecialModifier, s"${programStack.head}")
      else if headEqual('|') then quickToken(VTokenType.Branch, "|")
      else if headEqual('¤') then contextIndexToken
      else if headLookaheadEqual("#$") then getVariableToken
      else if headLookaheadEqual("#=") then setVariableToken
      else if headLookaheadEqual("#!") then setConstantToken
      else if headLookaheadEqual("#>") then augmentedAssignToken
      else if headLookaheadEqual("#:~") then originalCommandToken
      else if headLookaheadEqual("#:@") then commandSymbolToken
      else if headLookaheadEqual("#:=") then modifierSymbolToken
      else if headLookaheadEqual("#::R") then defineRecordToken
      else if headLookaheadEqual("#::+") then defineExtensionToken
      else if headLookaheadMatch("#::[EM]") then customDefinitionToken
      else
        val rangeStart = index
        val char = pop()
        tokens +=
          VToken(
            VTokenType.Command,
            char,
            VRange(rangeStart, index),
          )
    end while

    tokens.toSeq
  end lex

  /** Number = 0 | [1-9][0-9]*(\.[0-9]*)? | \.[0-9]* */
  private def numberToken: Unit =
    val rangeStart = index
    // Check the single zero case
    if headEqual('0') then
      val zeroToken = VToken(VTokenType.Number, "0", VRange(index, index))
      pop(1)
      tokens += zeroToken
    // Then the headless decimal case
    else if headEqual('.') then
      pop(1)
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
      // Not a 0, and not a headless decimal, so it's a normal number
      val head = simpleNumber()
      // Test for a decimal tail
      if headEqual('.') then
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
      // No decimal tail, so normal number
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

  /** String = '"' (\\.|[^„”“"])* [„”“"] */
  private def stringToken: Unit =
    val rangeStart = index
    val stringVal = StringBuilder()

    pop() // Pop the opening quote

    while !headIn("\"„”“") do
      if headEqual('\\') then stringVal ++= pop(2)
      else stringVal ++= pop()

    val text = stringVal
      .toString()
      .replace("\\\"", "\"")
      .replace(raw"\n", "\n")
      .replace(raw"\t", "\t")

    val tokenType = pop() match
      case "\"" => VTokenType.Str
      case "„" => VTokenType.CompressedString
      case "”" => VTokenType.DictionaryString
      case "“" => VTokenType.CompressedNumber

    tokens +=
      VToken(
        tokenType,
        text,
        VRange(rangeStart, index),
      )
  end stringToken

  private def oneCharStringToken: Unit =
    val rangeStart = index
    pop() // Pop the opening quote
    val char = pop()
    tokens +=
      VToken(
        VTokenType.Str,
        char,
        VRange(rangeStart, index),
      )

  private def twoCharStringToken: Unit =
    val rangeStart = index
    pop() // Pop the opening quote
    val char = pop(2)
    tokens +=
      VToken(
        VTokenType.Str,
        char,
        VRange(rangeStart, index),
      )

  private def twoCharNumberToken: Unit =
    val rangeStart = index
    pop() // Pop the opening quote
    val value = pop(2)
    val number = value.zipWithIndex
      .map((c, ind) => math.pow(Codepage.length, ind) * Codepage.indexOf(c))
      .sum
      .toString
    tokens +=
      VToken(
        VTokenType.Number,
        number,
        VRange(rangeStart, index),
      )

  /** Digraph = [∆øÞ] . | # [^[]$!=#>@{:] */
  private def digraphToken: Unit =
    val rangeStart = index
    val digraph = pop(2)

    tokens +=
      VToken(
        VTokenType.Digraph,
        digraph,
        VRange(rangeStart, index),
      )

  /** Convert a sugar trigraph to its normal form */
  private def sugarTrigraph: Unit =
    val trigraph = pop(3)
    val normal = SugarMap.trigraphs.getOrElse(trigraph, trigraph)
    programStack.pushAll(normal.reverse)

  private def contextIndexToken: Unit =
    val rangeStart = index
    pop()
    val value = simpleNumber()
    tokens +=
      VToken(
        VTokenType.ContextIndex,
        value,
        VRange(rangeStart, index),
      )

  private def getVariableToken: Unit =
    val rangeStart = index
    pop(2)
    val name = simpleName()
    tokens +=
      VToken(
        VTokenType.GetVar,
        name,
        VRange(rangeStart, index),
      )

  private def setVariableToken: Unit =
    val rangeStart = index
    pop(2)
    val name = simpleName()
    tokens +=
      VToken(
        VTokenType.SetVar,
        name,
        VRange(rangeStart, index),
      )

  private def setConstantToken: Unit =
    val rangeStart = index
    pop(2)
    val name = simpleName()
    tokens +=
      VToken(
        VTokenType.Constant,
        name,
        VRange(rangeStart, index),
      )

  private def augmentedAssignToken: Unit =
    val rangeStart = index
    pop(2)
    val name = simpleName()
    tokens +=
      VToken(
        VTokenType.AugmentVar,
        name,
        VRange(rangeStart, index),
      )

  private def simpleName(): String =
    val name = StringBuilder()
    if headLookaheadEqual("_") then name ++= pop()
    while safeCheck(c => c.isLetterOrDigit || c == '_') do name ++= s"${pop()}"
    name.toString()

  private def originalCommandToken: Unit =
    val rangeStart = index
    eat("#:~")
    val command = pop()
    tokens +=
      VToken(
        VTokenType.OriginalSymbol,
        command,
        VRange(rangeStart, index),
      )

  private def commandSymbolToken: Unit =
    val rangeStart = index
    eat("#:@")
    val command = pop()
    tokens +=
      VToken(
        VTokenType.ElementSymbol,
        command,
        VRange(rangeStart, index),
      )

  private def modifierSymbolToken: Unit =
    val rangeStart = index
    eat("#:`")
    val command = pop()
    tokens +=
      VToken(
        VTokenType.ModifierSymbol,
        command,
        VRange(rangeStart, index),
      )

  private def defineRecordToken: Unit =
    val rangeStart = index
    eat("#::R")
    eatWhitespace()
    val name = simpleName()
    tokens +=
      VToken(
        VTokenType.DefineRecord,
        name,
        VRange(rangeStart, index),
      )

  /** Extension ::= "#::+" [a-zA-Z_][a-zA-Z0-9_] "|" (Name ">" Name)* "|" impl }
    */
  private def defineExtensionToken: Unit =
    val rangeStart = index
    eat("#::+")
    val name = if headLookaheadMatch(". ") then pop() else simpleName()
    tokens +=
      VToken(
        VTokenType.DefineExtension,
        name,
        VRange(rangeStart, index),
      )
    if headEqual('|') then
      pop()
      // Get the arguments and put them into tokens
      while !headEqual('|') do
        val argNameStart = index
        val argName = simpleName()
        tokens +=
          VToken(
            VTokenType.Param,
            argName,
            VRange(argNameStart, index),
          )
        eatWhitespace()
        eat(">")
        eatWhitespace()
        val argTypeStart = index
        val argType = simpleName()
        tokens +=
          VToken(
            VTokenType.Param,
            argType,
            VRange(argTypeStart, index),
          )
      end while
    end if
  end defineExtensionToken

  private def customDefinitionToken: Unit =
    val rangeStart = index
    pop(3)
    tokens +=
      VToken(
        VTokenType.StructureOpen,
        "#::",
        VRange(rangeStart, index),
      )
    val definitionType = pop()
    if !"EM".contains(definitionType) then
      throw VyxalException(
        s"Invalid definition type: $definitionType. Expected E or M"
      )
    eatWhitespace()
    val nameRangeStart = index
    val name = simpleName()

    tokens +=
      VToken(
        VTokenType.Param,
        s"$definitionType$name",
        VRange(nameRangeStart, index),
      )
  end customDefinitionToken

end Vexer

object Vexer
def Codepage: String = """⎂⇝∯⊠ß≟₾◌⋊
ϩэЧ♳♴♵♶∥∦¿⎇↻⊙⁙∩∪⊕⊝⚇῟⚃ !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~ȦḂĊḊĖḞĠḢİĿṀṄȮṖṘṠṪẆẊικȧḃċḋėḟġḣŀṁṅȯṗṙṡṫẋƒΘΦ§ẠḄḌḤỊḶṂṆỌṚṢṬ…≤≥≠₌⁺⁻⁾√∑«»⌐∴∵⊻₀₁₂₃₄₅₆₇₈₉λƛΩ₳µ∆øÞ½ʀɾ¯×÷£¥←↑→↓±¤†Π¬∧∨⁰¹²⌈⌊Ɠɠı┉„”ð€“¶ᶿᶲ•≈⌙‹›"""
