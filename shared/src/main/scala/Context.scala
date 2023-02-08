package vyxal

import scala.collection.mutable.Stack
import scala.collection.mutable as mut
import scala.io.StdIn

/** @constructor
  *   Make a Context object for the current scope
  * @param stack
  *   The stack on which all operations happen
  * @param _ctxVarPrimary
  *   Context variable N. It's an Option because this scope might not have its
  *   own context variable(s). See [[this.ctxVarPrimary]] for more information.
  * @param _ctxVarSecondary
  *   Context variable M. It's an Option because this scope might not have its
  *   own context variable(s). See [[this.ctxVarSecondary]] for more
  *   information.
  * @param vars
  *   The variables currently in scope, accessible by their names. Null values
  *   signify that the variable is nonlocal, i.e., it should be gotten from the
  *   parent context
  * @param inputs
  *   The inputs available in this scope
  * @param parent
  *   The context inside which this context is (to inherit variables). `None`
  *   for toplevel contexts. When executing a [[VFun]], this is the context that
  *   the function was *defined* in, not the one it is executing inside.
  */
class Context private (
    private var stack: mut.ArrayBuffer[VAny],
    private var _ctxVarPrimary: Option[VAny] = None,
    private var _ctxVarSecondary: Option[VAny] = None,
    private val vars: mut.Map[String, VAny] = mut.Map(),
    private var inputs: Inputs = Inputs(),
    private val parent: Option[Context] = None,
    val globals: Globals = Globals(),
    val testMode: Boolean = false,
    var onlineOutput: String = ""
):
  def settings: Settings = if testMode then
    Settings(endPrintMode = EndPrintMode.None)
  else globals.settings

  /** Pop the top of the stack
    *
    * If the stack's empty, get the next input (inputs repeat). If there are no
    * inputs, read a line of input from stdin.
    */
  def pop(): VAny =
    val elem =
      if stack.nonEmpty then stack.remove(stack.size - 1)
      else if inputs.nonEmpty then inputs.next()
      else
        val temp =
          if settings.online then settings.defaultValue.toString()
          else StdIn.readLine()
        if temp.nonEmpty then Parser.parseInput(temp)
        else settings.defaultValue
    if settings.logLevel == LogLevel.Debug then println(s"Popped $elem")
    elem
  end pop

  /** Pop n elements and wrap in a list */
  def pop(n: Int): Seq[VAny] = Seq.fill(n)(this.pop()).reverse

  /** Get the top element on the stack without popping */
  def peek: VAny =
    if stack.nonEmpty then stack.last
    else if inputs.nonEmpty then inputs.peek
    else settings.defaultValue

  /** Get the top n elements on the stack without popping */
  def peek(n: Int): List[VAny] =
    // Number of elements peekable from the stack
    val numStack = n.max(stack.length)
    // Number of elements that need to be taken from the input
    val numInput = n - numStack
    // todo repeat the inputs or something?
    inputs.peek(numInput) ++ stack.slice(stack.length - numStack, stack.length)

  /** Push items onto the stack. The first argument will be pushed first. */
  def push(items: VAny*): Unit = stack ++= items

  /** Whether the stack is empty */
  def isStackEmpty: Boolean = stack.isEmpty

  /** Get the context variable N for this scope if it exists. If it doesn't, get
    * its parent's. If there's no parent Context, just get the default value (0)
    *
    *   - Inside while loops, this is the last condition value
    *   - Inside for loops, this is the current loop item
    *   - Inside lambdas/named functions, this is the argument
    */
  def ctxVarPrimary: VAny =
    _ctxVarPrimary
      .orElse(parent.map(_.ctxVarPrimary))
      .getOrElse(settings.defaultValue)

  /** Setter for context variable N so that outsiders don't have to deal with it
    * being an Option
    */
  def ctxVarPrimary_=(newCtx: VAny) =
    _ctxVarPrimary = Some(newCtx)

  /** Get the context variable M for this scope if it exists. If it doesn't, get
    * its parent's. If there's no parent Context, just get the default value (0)
    *
    *   - Inside both for loops and while loops, this is the current
    *     index/number of loop iterations
    */
  def ctxVarSecondary: VAny =
    _ctxVarSecondary
      .orElse(parent.map(_.ctxVarSecondary))
      .getOrElse(settings.defaultValue)

  /** Setter for context variable M so that outsiders don't have to deal with it
    * being an Option
    */
  def ctxVarSecondary_=(newCtx: VAny) =
    _ctxVarSecondary = Some(newCtx)

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
    if vars.contains(name) then vars(name) = value
    else
      Context.findParentWithVar(this, name) match
        case Some(parent) => parent.setVar(name, value)
        case None         => vars(name) = value

  /** Make a new Context for a structure inside the current structure */
  def makeChild() = new Context(
    stack,
    _ctxVarPrimary,
    _ctxVarSecondary,
    vars,
    inputs,
    Some(this),
    globals,
    testMode
  )

  def getTopCxt(): Context =
    parent match
      case Some(p) => p.getTopCxt()
      case None    => this
end Context

object Context:
  def apply(
      inputs: Seq[VAny] = Seq.empty,
      globals: Globals = Globals(),
      testMode: Boolean = false
  ): Context =
    new Context(
      stack = mut.ArrayBuffer(),
      inputs = Inputs(inputs),
      globals = globals,
      testMode = testMode
    )

  /** Find a parent that has a variable with the given name */
  @annotation.tailrec
  private def findParentWithVar(
      ctx: Context,
      varName: String
  ): Option[Context] =
    ctx.parent match
      case Some(parent) =>
        if parent.vars.contains(varName) then Some(parent)
        else findParentWithVar(parent, varName)
      case None => None

  /** Make a new Context for a function that was defined inside `origCtx` but is
    * now executing inside `currCtx`
    *
    * @param origCtx
    *   The context in which the function was defined
    * @param currCtx
    *   The context where the function is currently executing
    */
  def makeFnCtx(
      origCtx: Context,
      currCtx: Context,
      ctxVarPrimary: Option[VAny],
      ctxVarSecondary: Option[VAny],
      params: Seq[String],
      inputs: Seq[VAny]
  ) =
    new Context(
      mut.ArrayBuffer.empty,
      ctxVarPrimary.orElse(currCtx._ctxVarPrimary),
      ctxVarSecondary.orElse(currCtx._ctxVarSecondary),
      mut.Map(params.zip(inputs)*),
      Inputs(inputs),
      Some(origCtx),
      currCtx.globals,
      currCtx.testMode
    )
  end makeFnCtx

end Context
