package vyxal

import scala.collection.{mutable => mut}
import scala.collection.mutable.Stack

/** What kind of implicit output is wanted at the end
  */
enum EndPrintMode {

  /** Just print the top of the stack */
  case Default
  case JoinNewlines

  /** Join on newlines vertically */
  case JoinNewlinesVert

  /** Sum/concatenate the top of the stack */
  case Sum
}

// todo use a proper logging library instead
enum LogLevel {
  case Debug, Normal
}

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
    printFn: Any => Unit = print,
    logLevel: LogLevel = LogLevel.Normal
) {

  /** Add a flag to these settings
    *
    * @return
    *   A Some with the new settings, or None if the flag is invalid
    */
  def withFlag(flag: Char): Settings = flag match {
    case 'H' => this.copy(presetStack = true)
    case 'j' => this.copy(endPrintMode = EndPrintMode.JoinNewlines)
    case 'L' => this.copy(endPrintMode = EndPrintMode.JoinNewlinesVert)
    case 's' => this.copy(endPrintMode = EndPrintMode.Sum)
    case 'M' => this.copy(rangeStart = 0)
    case 'm' => this.copy(rangeOffset = -1)
    case 'Ṁ' => this.copy(rangeStart = 0, rangeOffset = -1)
    // todo implement the others
    case _ => throw IllegalArgumentException(s"$flag is an invalid flag")
  }
}

/** @constructor
  *   Make a Context object for the current scope
  * @param stack
  *   The stack on which all operations happen
  * @param _contextVar
  *   The context variable. It's an Option because this scope might not have its
  *   own context variable. See [[this.contextVar]] for more information.
  * @param vars
  *   The variables currently in scope, accessible by their names. Null values
  *   signify that the variable is nonlocal, i.e., it should be gotten from the
  *   parent context
  * @param inputs
  *   The inputs available in this scope
  * @param parent
  *   The context inside which this context is (to inherit variables). `None`
  *   for toplevel contexts
  */
class Context private (
    private var stack: mut.ArrayBuffer[VAny],
    private var _contextVar: Option[VAny] = None,
    private val vars: mut.Map[String, VAny] = mut.Map(),
    private var inputs: List[VAny] = List.empty,
    private val parent: Option[Context] = None,
    val settings: Settings = Settings()
) {
  def pop(): VAny = {
    val elem = if (stack.nonEmpty) {
      stack.remove(stack.size - 1)
    } else if (inputs.nonEmpty) {
      val (head :: tail) = inputs
      // todo this is a bad way to rotate inputs, use something like a
      // circular buffer instead
      inputs = tail :+ head
      head
    } else {
      settings.defaultValue
    }
    if (settings.logLevel == LogLevel.Debug) {
      println(s"Popped $elem")
    }
    elem
  }

  /** Pop n elements and wrap in a list */
  def pop(n: Int): List[VAny] = List.fill(n)(this.pop())

  /** Get the top element on the stack without popping */
  def peek: VAny =
    if (stack.nonEmpty) {
      stack.last
    } else if (inputs.nonEmpty) {
      inputs.head
    } else {
      settings.defaultValue
    }

  /** Get the top n elements on the stack without popping */
  def peek(n: Int): List[VAny] = {
    // Number of elements peekable from the stack
    val numStack = n.max(stack.length)
    // Number of elements that need to be taken from the input
    val numInput = n - numStack
    // todo repeat the inputs or something?
    inputs.slice(inputs.length - n, inputs.length)
      ++ stack.slice(stack.length - numStack, stack.length)
  }

  def push(item: VAny): Unit = stack += item

  /** Whether the stack is empty */
  def isStackEmpty: Boolean = stack.isEmpty

  /** Get the context variable for this scope if it exists. If it doesn't, get
    * the context variable from the parent. If there's no parent Context, just
    * get the default value (0)
    */
  def contextVar: VAny =
    _contextVar
      .orElse(parent.map(_.contextVar))
      .getOrElse(settings.defaultValue)

  /** Setter for the context variable so that outsiders don't have to deal with
    * it being an Option
    */
  def contextVar_=(newCtx: VAny) = {
    _contextVar = Some(newCtx)
  }

  /** Get a variable by the given name. If it doesn't exist in the current
    * context, looks in the parent context. If not found in any context, returns
    * the default value (0).
    */
  def getVar(name: String): VAny =
    vars
      .get(name)
      .orElse(parent.map(_.getVar(name)))
      .getOrElse(settings.defaultValue)

  /** Set a variable to a given value. If found in this context, changes its
    * value. If it's not found in the current context but it exists in the
    * parent context, sets it there. Otherwise, creates a new variable in the
    * current context.
    */
  def setVar(name: String, value: VAny): Unit =
    if (vars.contains(name)) {
      vars(name) = value
    } else {
      Context.findParentWithVar(this, name) match {
        case Some(parent) => parent.setVar(name, value)
        case None         => vars(name) = value
      }
    }

  /** Make a new Context for a structure inside the current structure */
  def makeChild() = new Context(
    stack,
    _contextVar,
    vars,
    inputs,
    Some(this),
    settings
  )
}

object Context {
  def apply(
      inputs: List[VAny] = List.empty,
      settings: Settings = Settings()
  ): Context =
    new Context(
      stack = mut.ArrayBuffer(),
      inputs = inputs,
      settings = settings
    )

  /** Find a parent that has a variable with the given name */
  @annotation.tailrec
  private def findParentWithVar(
      ctx: Context,
      varName: String
  ): Option[Context] =
    ctx.parent match {
      case Some(parent) =>
        if (parent.vars.contains(varName)) {
          Some(parent)
        } else {
          findParentWithVar(parent, varName)
        }
      case None => None
    }

  /** Make a new Context for a function that was defined inside `origCtx` but is
    * now executing inside `currCtx`
    *
    * @param popArgs
    *   Whether the inputs for the function will be popped from the stack
    *   (instead of merely peeking)
    */
  def makeFnCtx(
      origCtx: Context,
      currCtx: Context,
      arity: Int,
      params: List[String],
      popArgs: Boolean
  ) = {
    val newInputs = if (popArgs) currCtx.pop(arity) else currCtx.peek(arity)
    if (currCtx.settings.logLevel == LogLevel.Debug) {
      println(
        s"newInputs = $newInputs, arity = $arity, stack = ${currCtx.stack}, popArgs = $popArgs"
      )
    }
    new Context(
      mut.ArrayBuffer.empty,
      currCtx._contextVar,
      mut.Map(params.zip(newInputs)*),
      newInputs,
      Some(currCtx),
      currCtx.settings
    )
  }
}
