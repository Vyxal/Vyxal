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
  private val dummyAST = AST.Newline

  private val stackFrames: Stack[StackFrame] =
    Stack(StackFrame("<root>", rootCtx, code))

  /** The current stack frame */
  private def frame: StackFrame = stackFrames.last

  private var currStep: Option[Step] = Some(Step.stepsForAST(code))

  def addBreakpoint(row: Int, col: Int): Unit = ???

  def finished: Boolean = currStep.isEmpty && stackFrames.isEmpty

  /** Debug a VFun
    *
    * @param callAST
    *   The AST of the element calling the function, not the AST of the function
    *   definition itself
    * @param fn
    *   The function to be called
    */
  def fnCall(fn: VFun): Step =
    fn.originalAST match
      case Some(fnDef) =>
        NewStackFrame(
          fnDef,
          fn.name.getOrElse("<function>"),
          ctx => Context.makeFnCtx(fn.ctx, ctx, ???, ???, ???, ???, ???, ???),
          Step.seq(fnDef.body.map(Step.stepsForAST))
        )
      case None =>
        // We don't have the source, so it can't be debugged
        Exec(
          // TODO (user): find a way around using a dummy AST
          dummyAST,
          () =>
            ctx ?=>
              Interpreter.executeFn(fn)
              None
        )

  @tailrec
  private def stepIntoHelper(step: Step): Option[Step] =
    scribe.trace(s"Step: $step")
    step match
      case Exec(ast, exec) =>
        exec()(using frame.ctx)
      case Hidden(exec) =>
        exec()(using frame.ctx)
        popUntilNext()
      case Lazy(get) =>
        get()(using frame.ctx) match
          case Some(step) => stepIntoHelper(step)
          case None => None
      case StepSeq(firstStep, rest) =>
        rest match
          case head :: tail =>
            frame.stepStack += Some(StepSeq(head, tail))
          case Nil => ()
        stepIntoHelper(firstStep)
      case Block(ast, inner) =>
        frame.stepStack += None
        inner match
          case Some(Lazy(get)) => get()(using frame.ctx)
          case _ => inner
      case NewStackFrame(ast, frameName, newCtx, inner) =>
        stackFrames.push(StackFrame(frameName, newCtx(frame.ctx), ast))
        inner match
          case Some(Lazy(get)) => get()(using frame.ctx)
          case _ => inner
    end match
  end stepIntoHelper

  /** Keep popping either this frame's stepStack or the stackframes until we get
    * to the next step or until the stackframes are all gone
    */
  @tailrec
  private def popUntilNext(): Option[Step] =
    if stackFrames.isEmpty then None
    else if frame.stepStack.isEmpty then
      stackFrames.pop()
      popUntilNext()
    else
      frame.stepStack.pop() match
        case Some(Hidden(exec)) =>
          exec()(using frame.ctx)
          popUntilNext()
        case None => popUntilNext()
        case Some(next) => Some(next)

  def stepInto(): Unit =
    if stackFrames.isEmpty then
      scribe.trace("Cannot step in, debugger finished")
    else
      scribe.trace(s"Stepping in, frame: $frame, step: $currStep")
      currStep match
        case Some(step) => this.currStep = stepIntoHelper(step)
        case None => this.currStep = popUntilNext()

  def stepOver(): Unit =
    scribe.trace(s"Stepping over, frame: $frame")
    val startFrames = stackFrames.size
    val startStepDepth = frame.stepStack.size
    if currStep.isDefined then stepInto()
    while currStep.isDefined && stackFrames.size >= startFrames && frame.stepStack.size > startStepDepth
    do stepInto()

  def stepOut(): Unit =
    scribe.trace(s"Stepping out, frame: $frame")
    val startFrames = stackFrames.size
    while stackFrames.nonEmpty && stackFrames.size >= startFrames do stepInto()

  def continue(): Unit =
    while stackFrames.nonEmpty do stepInto()

  def printState(): Unit =
    this.currStep match
      case Some(step) =>
        println(s"Next to execute: ${step.ast.toVyxal} <${step.ast.range}>")
        println(s"Top of stack is ${frame.ctx.peek}")
      // println("Frames:")
      // println(stackFrames.reverse.mkString("\n"))
      case _ => println("Next: ???")
end Debugger
