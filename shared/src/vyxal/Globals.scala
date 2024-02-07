package vyxal

import vyxal.VNum.given

import scala.collection.mutable as mut

/** Stuff that's shared across all contexts
  *
  * @param inputs
  *   The original/global inputs to the program
  * @param settings
  *   Settings set through flags
  */
case class Globals(
    settings: Settings = Settings(),
    printFn: String => Unit = print,
    callStack: mut.Stack[VFun] = mut.Stack(),
):
  var register: VAny = settings.defaultValue
  var debug: Boolean = false
  var originalProgram: AST = null
  var printed: Boolean = false
  var inputs: Inputs = Inputs()
  var symbols: Map[String, CustomDefinition] = Map()
  var classes: Map[String, CustomClass] = Map()
  var extensions: Map[String, (List[(List[String], CustomDefinition)], Int)] =
    Map()

/** Stores the inputs for some Context. Inputs can be overridden (see
  * [[Inputs#overrideInputs]]).
  *
  * Implemented as a circular buffer to wrap around.
  */
class Inputs(origInputs: Seq[VAny] = Seq.empty):
  private val origArr = origInputs.toArray.reverse

  /** Uses an array for constant access, not for mutating items */
  private var currInputs = origArr
  private val allInputs = origInputs.reverse

  /** Keeps track of the next input's index */
  private var ind = 0

  def nonEmpty: Boolean = currInputs.nonEmpty

  def length: Int = currInputs.length

  /** Make sure to call [[this.nonEmpty]] first */
  def next(): VAny =
    val res = currInputs(ind)
    ind = (ind + 1) % currInputs.length
    res

  /** Temporarily replace inputs with the given `Seq`
    *
    * @see
    *   [[reset]]
    */
  def overrideInputs(newInputs: Seq[VAny]): Unit =
    currInputs = newInputs.toArray
    ind = 0

  /** Use the original inputs again
    *
    * @see
    *   [[overrideInputs]]
    */
  def reset(): Unit =
    currInputs = origArr
    ind = 0

  /** Get the current input without moving on to the one after that */
  def peek: VAny = currInputs(ind)

  /** Get the next n inputs without moving the index/pointer forward (wrap if
    * necessary)
    */
  def peek(n: Int): List[VAny] =
    if currInputs.isEmpty then return Nil

    // The number of elems that can be gotten without wrapping
    val numNonWrapping = n.min(currInputs.length - ind)
    val nonWrapping = currInputs.slice(ind, ind + numNonWrapping).toList

    if n == numNonWrapping then nonWrapping
    else
      // The number of times the entire inputs array has to be repeated
      val numRepeats = (n - numNonWrapping) / currInputs.length
      val repeats = List.fill(numRepeats)(currInputs.toList)

      // The number of extra elems needed at the end of wrapping
      val numEnd = n - numNonWrapping - numRepeats * currInputs.length
      val end = currInputs.take(numEnd).toList

      nonWrapping ::: repeats.flatten ::: end
  end peek

  def apply(i: Int): VAny = currInputs(i)

  def getAll: Seq[VAny] = allInputs

  override def toString = origArr.mkString("Inputs(", ", ", ")")
end Inputs

/** What kind of implicit output is wanted at the end */
enum EndPrintMode:

  /** Just print the top of the stack */
  case Default
  case Pretty
  case JoinNewlines
  case JoinSpaces
  case JoinNothing

  /** Sum/concatenate the top of the stack */
  case Sum
  case DeepSum

  /** Do stuff to the top of the stack */
  case LogicalNot

  /** Print property of top of stack */
  case Maximum
  case Minimum
  case Length

  /** Print something to do with stack */
  case LengthStack
  case SumStack
  case SpaceStack

  /** Don't print anything - disable implicit output */
  case Force
  case None
end EndPrintMode

/** Settings set by flags
  *
  * @param presetStack
  *   Whether to push a 100 onto the stack before the program starts
  * @param endPrintMode
  *   How to print implicit output at the end
  * @param defaultValue
  *   Value to give when empty stack is popped
  */
case class Settings(
    presetStack: Boolean = false,
    endPrintMode: EndPrintMode = EndPrintMode.Default,
    defaultValue: VAny = 0,
    rangify: Boolean = false,
    rangeStart: VNum = 1,
    rangeOffset: VNum = 0,
    numToRange: Boolean = false,
    online: Boolean = false,
    literate: Boolean = false,
    fullTrace: Boolean = false,
    defaultArity: Int = 1,
    limitPrint: Boolean = false,
    dontEvalInputs: Boolean = false,
    recursionLimit: Int = 100,
):

  /** Add a flag to these settings
    *
    * @return
    *   An updated `Settings` object
    */
  def withFlag(flag: Flag): Settings =
    import Flag.*
    flag match
      case Trace => this.copy(fullTrace = true)
      case Preset100 => this.copy(presetStack = true)
      case Literate => this.copy(literate = true)
      case RangeNone => this
      case RangeStart0 => this.copy(rangeStart = 0)
      case RangeEndExcl => this.copy(rangeOffset = -1)
      case RangeProgrammery => this.copy(rangeStart = 0, rangeOffset = -1)
      case InputAsStrings => this.copy(dontEvalInputs = true)
      case NumbersAsRanges => this.copy(rangify = true)
      case Arity1 => this.copy(defaultArity = 1)
      case Arity2 => this.copy(defaultArity = 2)
      case Arity3 => this.copy(defaultArity = 3)
      case LimitOutput => this.copy(limitPrint = true)

      case PrintTop => this.copy(endPrintMode = EndPrintMode.Default)
      case PrintJoinNewlines =>
        this.copy(endPrintMode = EndPrintMode.JoinNewlines)
      case PrintSum => this.copy(endPrintMode = EndPrintMode.Sum)
      case PrintDeepSum => this.copy(endPrintMode = EndPrintMode.DeepSum)
      case PrintJoinSpaces => this.copy(endPrintMode = EndPrintMode.JoinSpaces)
      case PrintNone => this.copy(endPrintMode = EndPrintMode.None)
      case PrintForce => this.copy(endPrintMode = EndPrintMode.Force)
      case PrintLength => this.copy(endPrintMode = EndPrintMode.Length)
      case PrintPretty => this.copy(endPrintMode = EndPrintMode.Pretty)
      case PrintMax => this.copy(endPrintMode = EndPrintMode.Maximum)
      case PrintMin => this.copy(endPrintMode = EndPrintMode.Minimum)
      case PrintSumAll => this.copy(endPrintMode = EndPrintMode.SumStack)
      case PrintStackLength =>
        this.copy(endPrintMode = EndPrintMode.LengthStack)
      case PrintAllJoinNothing =>
        this.copy(endPrintMode = EndPrintMode.JoinNothing)
      case PrintAllJoinSpaces =>
        this.copy(endPrintMode = EndPrintMode.SpaceStack)
      case PrintNot => this.copy(endPrintMode = EndPrintMode.LogicalNot)
    end match
  end withFlag

  def withFlag(flag: Char): Settings =
    Flag.flags.find(_.short == flag) match
      case Some(f) => withFlag(f)
      case None => throw VyxalException(s"$flag is an invalid flag")

  /** Helper to update these settings with multiple flags
    *
    * @see
    *   withFlag
    */
  def withFlags(flags: List[Char]): Settings =
    flags.foldLeft(this)(_.withFlag(_))

  /** Set an end print mode based */
  def useMode(mode: EndPrintMode): Settings = this.copy(endPrintMode = mode)
end Settings
