package vyxal

import scala.collection.mutable as mut
import scala.io.StdIn

import VNum.given

/** Stuff that's shared across all contexts
  *
  * @param inputs
  *   The original/global inputs to the program
  * @param settings
  *   Settings set through flags
  * @param register
  *   Value of the register
  */
case class Globals(
    inputs: Inputs = Inputs(),
    settings: Settings = Settings(),
    printFn: String => Unit = print,
    callStack: mut.Stack[VFun] = mut.Stack(),
):
  var register: VAny = settings.defaultValue
  val literate: Boolean = false
  var useFancyRepl: Boolean = true

/** Stores the inputs for some Context. Inputs can be overridden (see
  * [[Inputs#overrideInputs]]).
  *
  * Implemented as a circular buffer to wrap around.
  */
class Inputs(origInputs: Seq[VAny] = Seq.empty):
  private var origArr = origInputs.toArray.reverse

  /** Uses an array for constant access, not for mutating items */
  private var currInputs = origArr

  /** Keeps track of the next input's index */
  private var ind = 0

  def nonEmpty: Boolean = currInputs.nonEmpty

  /** Make sure to call [[this.nonEmpty]] first */
  def next(): VAny =
    val res = currInputs(ind)
    ind = (ind + 1) % currInputs.size
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
      val numRepeats = (n - numNonWrapping) / currInputs.size
      val repeats = List.fill(numRepeats)(currInputs.toList)

      // The number of extra elems needed at the end of wrapping
      val numEnd = n - numNonWrapping - numRepeats * currInputs.size
      val end = currInputs.take(numEnd).toList

      nonWrapping ::: repeats.flatten ::: end
  end peek

  override def toString() = origArr.mkString("Inputs(", ", ", ")")
end Inputs

/** What kind of implicit output is wanted at the end */
enum EndPrintMode:

  /** Just print the top of the stack */
  case Default
  case JoinNewlines

  /** Join on newlines vertically */
  case JoinNewlinesVert

  /** Sum/concatenate the top of the stack */
  case Sum

  /** Don't print anything - disable implicit output */
  case None

/** Settings set by flags
  *
  * @param presetStack
  *   Whether to push a 100 onto the stack before the program starts
  * @param endPrintMode
  *   How to print implicit output at the end
  * @param defaultValue
  *   Value to give when empty stack is popped
  * @param printFn
  *   Function used to output (necessary for online interpreter)
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
):

  /** Add a flag to these settings
    *
    * @return
    *   An updated `Settings` object
    */
  def withFlag(flag: Char): Settings = flag match
    case 'H' => this.copy(presetStack = true)
    case 'j' => this.copy(endPrintMode = EndPrintMode.JoinNewlines)
    case 'L' => this.copy(endPrintMode = EndPrintMode.JoinNewlinesVert)
    case 's' => this.copy(endPrintMode = EndPrintMode.Sum)
    case 'M' => this.copy(rangeStart = 0)
    case 'm' => this.copy(rangeOffset = -1)
    case 'Ṁ' => this.copy(rangeStart = 0, rangeOffset = -1)
    case 'l' => this.copy(literate = true)
    case _   => throw IllegalArgumentException(s"$flag is an invalid flag")

  /** Helper to update these settings with multiple flags
    *
    * @see
    *   withFlag
    */
  def withFlags(flags: List[Char]): Settings =
    flags.foldLeft(this)(_.withFlag(_))
end Settings
