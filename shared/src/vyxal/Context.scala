package vyxal

import scala.collection.mutable as mut
import scala.io.StdIn

/** @constructor
  *   Make a Context object for the current scope
  * @param stack
  *   The stack on which all operations happen. The top of the stack is stored
  *   at the end so that popping is faster.
  * @param _ctxVarPrimary
  *   Context variable N. It's an Option because this scope might not have its
  *   own context variable(s). See [[this.ctxVarPrimary]] for more information.
  * @param _ctxVarSecondary
  *   Context variable M. It's an Option because this scope might not have its
  *   own context variable(s). See [[this.ctxVarSecondary]] for more
  *   information.
  * @param vars
  *   The variables currently in scope, accessible by their names. Since it's a
  *   mutable Map, the same object may be shared by a Context, its children, and
  *   its children's children.
  * @param inputs
  *   The inputs available in this scope
  * @param parent
  *   The context inside which this context is (to inherit variables). `None`
  *   for toplevel contexts. When executing a [[VFun]], this is the context that
  *   the function was *defined* in, not the one it is executing inside.
  */
class Context private (
    private var stack: mut.ArrayBuffer[VAny],
    private var _ctxVarPrimary: Option[VAny] =
      Some("abcdefghijklmnopqrstuvwxyz"),
    private var _ctxVarSecondary: Option[VAny] =
      Some("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    val ctxArgs: Option[Seq[VAny]] = None,
    private val vars: mut.Map[String, VAny] = mut.Map(),
    val inputs: Inputs = Inputs(),
    private val parent: Option[Context] = None,
    val globals: Globals = Globals(),
    val testMode: Boolean = false,
    val useStack: Boolean = false,
    var recursion: Int = 0,
):
  var settings: Settings = globals.settings

  /** Pop the top of the stack
    *
    * If the stack's empty, get the next input (inputs repeat). If there are no
    * inputs, read a line of input from stdin.
    */
  def pop(): VAny =
    if useStack then return parent.getOrElse(getTopCtx()).pop()
    val elem =
      if stack.nonEmpty then stack.remove(stack.size - 1)
      else if inputs.nonEmpty then inputs.next()
      else
        val temp =
          if settings.online then settings.defaultValue.toString
          else
            print("<: ")
            val g = StdIn.readLine()
            if g == null then settings.defaultValue.toString else g
        if temp.nonEmpty then
          if settings.dontEvalInputs then temp
          else MiscHelpers.eval(temp)(using this)
        else settings.defaultValue
    scribe.trace(s"Popped $elem")
    elem
  end pop

  /** Pop n elements and wrap in a list. The top of the stack will be at the
    * start of the list.
    */
  def pop(n: Int): Seq[VAny] =
    if useStack then return getTopCtx().pop(n)
    Seq.fill(n)(this.pop())

  /** Get the top element on the stack without popping */
  def peek: VAny =
    if useStack then getTopCtx().peek
    else if stack.nonEmpty then stack.last
    else if inputs.nonEmpty then inputs.peek
    else settings.defaultValue

  /** Get the top n elements on the stack without popping. The top of the stack
    * will be at the start of the list.
    */
  def peek(n: Int): List[VAny] =
    if useStack then getTopCtx().peek(n)
    else if n <= stack.length then
      stack.slice(stack.length - n, stack.length).toList.reverse
    else stack.toList.reverse ::: inputs.peek(n - stack.length)

  /** Push items onto the stack. The first argument will be pushed first. */
  def push(items: VAny*): Unit =
    if useStack then getTopCtx().push(items*) else stack ++= items

  def length: Int = stack.length

  def reverse(): Unit = stack = stack.reverse

  def wrap(): Unit =
    if useStack then getTopCtx().wrap()
    else
      val temp = stack.toList
      stack.clear()
      stack += VList.from(temp)

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
  def ctxVarPrimary_=(newCtx: VAny): Unit = _ctxVarPrimary = Some(newCtx)

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
  def ctxVarSecondary_=(newCtx: VAny): Unit = _ctxVarSecondary = Some(newCtx)

  /** Get a variable by the given name. If it doesn't exist in the current
    * context, looks in the parent context. If not found in any context, returns
    * the default value (0).
    */
  def getVar(name: String): VAny =
    vars
      .get(s"!$name")
      .orElse(vars.get(name))
      .orElse(parent.map(_.getVar(s"!$name")))
      .orElse(parent.map(_.getVar(name)))
      .getOrElse(settings.defaultValue)

  /** Set a variable to a given value. */
  def setVar(name: String, value: VAny): Unit =
    if vars.contains(s"!$name") then throw ConstantAssignmentException(name)
    else vars(name) = value

  /** Set a constant variable to a given value */
  def setConst(name: String, value: VAny): Unit =
    if vars.contains(s"!$name") then throw ConstantDuplicateException(name)
    else vars(s"!$name") = value

  /** Get all variables in this Context (parent variables not included) */
  def allVars: Map[String, VAny] = vars.toMap

  /** Make a new Context for a structure inside the current structure */
  def makeChild() =
    new Context(
      stack,
      _ctxVarPrimary,
      _ctxVarSecondary,
      ctxArgs,
      vars, // Share the same variables Map with the child
      inputs,
      Some(this),
      globals,
      testMode, // child shouldn't use stack just because parent does
      recursion = recursion,
    )

  def getTopCtx(): Context =
    parent match
      case Some(p) => p.getTopCtx()
      case None => this

  def rotateLeft: Unit =
    if isStackEmpty then push(pop())
    else stack += stack.remove(0)

  def rotateRight: Unit = stack.insert(0, pop())

  def copy: Context =
    new Context(
      mut.ArrayBuffer().addAll(stack),
      _ctxVarPrimary,
      _ctxVarSecondary,
      ctxArgs,
      vars.clone(),
      inputs,
      parent.map(_.copy),
      globals,
      testMode,
      useStack,
    )

  def getStack: Seq[VAny] = stack.toSeq
end Context

object Context:
  def apply(
      inputs: Seq[VAny] = Seq.empty,
      globals: Globals = Globals(),
      testMode: Boolean = false,
      ctxArgs: Option[Seq[VAny]] = None,
  ): Context =
    new Context(
      stack = mut.ArrayBuffer(),
      inputs = Inputs(inputs),
      globals = globals,
      testMode = testMode,
      ctxArgs = ctxArgs,
    )

  /** Make a new Context for a function that was defined inside `origCtx` but is
    * now executing inside `currCtx`
    *
    * @param origCtx
    *   The context in which the function was defined
    * @param currCtx
    *   The context where the function is currently executing
    * @param ctxVarSecondary
    *   Secondary context var. Not an Option because if not explicitly
    *   overridden, the inputs are wrapped in a VList and used as the secondary
    *   context var.
    */
  def makeFnCtx(
      origCtx: Context,
      currCtx: Context,
      ctxVarPrimary: Option[VAny],
      ctxVarSecondary: VAny,
      ctxArgs: Seq[VAny],
      vars: mut.Map[String, VAny],
      inputs: Seq[VAny],
      useStack: Boolean,
  ): Context =
    val stack = if useStack then currCtx.stack else mut.ArrayBuffer.from(inputs)
    vars ++= origCtx.vars

    new Context(
      stack,
      ctxVarPrimary.orElse(currCtx._ctxVarPrimary),
      Some(ctxVarSecondary),
      ctxArgs = Some(ctxArgs),
      vars = vars,
      inputs = Inputs(inputs),
      parent = Some(origCtx),
      globals = currCtx.globals,
      testMode = currCtx.testMode,
      useStack = useStack,
      recursion = currCtx.recursion,
    )
  end makeFnCtx

end Context
