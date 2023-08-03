package vyxal.debugger

import vyxal.*

import scala.annotation.tailrec
import scala.collection.mutable.Stack

/** @param name
  *   The name of the function this stack frame is for (if applicable)
  * @param ctx
  *   The Context for this stack frame. May be the same as the outer stack
  *   frame's context.
  * @param ast
  *   The AST for the structure that caused the new stack frame
  */
class StackFrame(
    val name: String,
    val ctx: Context,
    val ast: AST,
):
  private[debugger] var stepStack = Stack.empty[Option[Step]]

  override def toString = s"Frame $name for $ast (top: ${ctx.peek})"

class Debugger(code: AST)(using rootCtx: Context):
  private val stackFrames: Stack[StackFrame] =
    Stack(StackFrame("<root>", rootCtx, code))

  /** The current stack frame */
  private def frame: StackFrame = stackFrames.last

  private var currStep = removeBadSteps(Step.stepsForAST(code)).getOrElse(null)

  def finished: Boolean = currStep == null

  /** Debug a VFun
    *
    * @param callAST
    *   The AST of the element calling the function, not the AST of the function
    *   definition itself
    * @param fn
    *   The function to be called
    */
  def fnCall(
      fn: VFun,
      ctxVarPrimary: VAny | Null = null,
      ctxVarSecondary: VAny | Null = null,
      args: Seq[VAny] | Null = null
  ): Step =
    fn.originalAST match
      case Some(fnDef) =>
        NewStackFrame(
          fnDef,
          fn.name.getOrElse("<function>"),
          ctx => Context.makeFnCtx(fn.ctx, ctx, ???, ???, ???, ???, ???, ???),
          StepSeq(fnDef.body.map(Step.stepsForAST))
        )
      case None =>
        Step.hidden { Interpreter.executeFn(fn) }

  /** Keep popping either this frame's stepStack or the stackframes until we get
    * to the next step or until the stackframes are all gone
    */
  @tailrec
  private def popUntilNext(): Option[ProperStep] =
    if frame.stepStack.isEmpty then
      val lastFrame = stackFrames.pop()
      scribe.trace(s"Popped frame $lastFrame")
      if stackFrames.isEmpty then None
      else popUntilNext()
    else
      scribe.trace(s"Popping step: ${frame.stepStack.top}")
      frame.stepStack.pop() match
        case Some(nextStep) => removeBadSteps(nextStep)
        case None => popUntilNext()
  end popUntilNext

  /** Get rid of Lazy, Hidden, and StepSeq steps, because they aren't proper
    * steps
    */
  @tailrec
  private def removeBadSteps(step: Step): Option[ProperStep] =
    step match
      case Lazy(get) =>
        get()(using frame.ctx) match
          case Some(next) => removeBadSteps(next)
          case None => popUntilNext()
      case Hidden(exec) =>
        exec()(using frame.ctx)
        popUntilNext()
      case StepSeq(steps) =>
        steps match
          case first :: rest =>
            frame.stepStack.push(Some(StepSeq(rest)))
            removeBadSteps(first)
          case Nil => popUntilNext()
      case proper: ProperStep => Some(proper)

  def stepInto(): Unit =
    if stackFrames.isEmpty then
      throw new IllegalStateException("Debugger has finished, cannot step into")

    scribe.trace(s"Stepping in, frame: $frame, step: $currStep")
    val nextStep = this.currStep match
      case Exec(ast, exec) =>
        exec()(using this, frame.ctx)
          .flatMap(removeBadSteps)
          .orElse(popUntilNext())
      case Block(ast, inner) =>
        frame.stepStack += None
        removeBadSteps(inner)
      case NewStackFrame(ast, frameName, newCtx, inner) =>
        stackFrames.push(StackFrame(frameName, newCtx(frame.ctx), ast))
        removeBadSteps(inner)
    nextStep match
      case Some(nextStep) => this.currStep = nextStep
      case None =>
        this.currStep = null
        scribe.trace("Debugger has finished")
  end stepInto

  def stepOver(): Unit =
    if !this.finished then
      scribe.trace(s"Stepping over, frame: $frame")
      val startFrames = stackFrames.size
      val startStepDepth = frame.stepStack.size
      stepInto()
      while !this.finished && stackFrames.size >= startFrames && frame.stepStack.size > startStepDepth
      do stepInto()

  def stepOut(): Unit =
    scribe.trace(s"Stepping out, frame: $frame")
    val startFrames = stackFrames.size
    while !this.finished && stackFrames.size >= startFrames do stepInto()

  def continue(): Unit =
    while !this.finished do stepInto()

  def printState(): Unit =
    println(s"Next to execute: ${currStep.ast.toVyxal} <${currStep.ast.range}>")
    println(s"Top of stack is ${frame.ctx.peek}")
  // println("Frames:")
  // println(stackFrames.reverse.mkString("\n"))
end Debugger
