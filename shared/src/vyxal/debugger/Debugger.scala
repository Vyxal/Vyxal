package vyxal.debugger

import vyxal.*
import vyxal.parsing.Lexer

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.Stack

/** @param name
  *   The name of the function this stack frame is for (if applicable)
  * @param ctx
  *   The Context for this stack frame. May be the same as the outer stack
  *   frame's context.
  * @param ast
  *   The AST for the structure that caused the new stack frame
  */
class StackFrame(val name: String, val ctx: Context, val ast: AST):
  private[debugger] var stepStack = Stack.empty[Option[Step]]

  override def toString = s"Frame $name for $ast (top: ${ctx.peek})"

/** @param offset
  *   The offset at which the breakpoint is set. In a praclang, this would be a
  *   line number instead
  */
class Breakpoint(
    val offset: Int,
    val label: Option[String] = None,
    val matches: AST => Boolean = _ => true,
)

class Debugger(code: AST)(using rootCtx: Context):
  /** Separate private variable from [[stackFrames]] to keep outsiders from
    * mutating it
    */
  private val frames: Stack[StackFrame] =
    Stack(StackFrame("<root>", rootCtx, code))

  private val breakpoints = mutable.Set.empty[Breakpoint]

  /** The current stack frame */
  private def frame: StackFrame = frames.last

  private var currStep = removeBadSteps(Step.stepsForAST(code)).getOrElse(null)

  /** Separate private variable to keep outsiders from reassigning */
  private var currCtx = frame.ctx

  def ctx: Context = currCtx

  def currAST = currStep.ast

  def stackFrames: Iterable[StackFrame] = this.frames

  def finished: Boolean = currStep == null

  def addBreakpoint(breakpoint: Breakpoint): Unit = breakpoints += breakpoint

  def removeBreakpoint(offset: Int): Unit =
    breakpoints.filterInPlace(_.offset != offset)

  def removeBreakpoint(label: String): Unit =
    breakpoints.filterInPlace(_.label != Some(label))

  def getBreakpoints(): Set[Breakpoint] = breakpoints.toSet

  /** Keep popping either this frame's stepStack or the stackframes until we get
    * to the next step or until the stackframes are all gone
    */
  @tailrec
  private def popUntilNext(): Option[ProperStep] =
    if frame.stepStack.isEmpty then
      val lastFrame = frames.pop()
      scribe.trace(s"Popped frame $lastFrame")
      this.currCtx = lastFrame.ctx
      if frames.isEmpty then None
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
      case Lazy(get) => get()(using frame.ctx) match
          case Some(next) => removeBadSteps(next)
          case None => popUntilNext()
      case Hidden(exec) =>
        exec()(using frame.ctx)
        popUntilNext()
      case StepSeq(steps) => steps match
          case first :: rest =>
            frame.stepStack.push(Some(StepSeq(rest)))
            removeBadSteps(first)
          case Nil => popUntilNext()
      case proper: ProperStep => Some(proper)

  private def atBreakpoint: Boolean =
    breakpoints.exists { b =>
      currAST.range.includes(b.offset) && b.matches(currAST)
    }

  def stepInto(): Unit =
    if frames.isEmpty then
      throw IllegalStateException("Debugger has finished, cannot step into")

    scribe.trace(s"Stepping in, frame: $frame, step: $currStep")
    val nextStep = this.currStep match
      case Exec(ast, exec) => exec()(using this, frame.ctx)
          .flatMap(removeBadSteps)
          .orElse(popUntilNext())
      case Block(ast, inner) =>
        frame.stepStack += None
        removeBadSteps(inner)
      case NewStackFrame(ast, frameName, newCtx, inner) =>
        frames.push(StackFrame(frameName, newCtx(frame.ctx), ast))
        removeBadSteps(inner)
    nextStep match
      case Some(nextStep) =>
        this.currStep = nextStep
        this.currCtx = frame.ctx
      case None =>
        this.currStep = null
        scribe.trace("Debugger has finished")
  end stepInto

  def stepOver(): Unit =
    if !this.finished then
      scribe.trace(s"Stepping over, frame: $frame")
      val startFrames = frames.size
      val startStepDepth = frame.stepStack.size
      stepInto()
      while !this.finished && !this.atBreakpoint &&
        frames.size >= startFrames && frame.stepStack.size > startStepDepth
      do stepInto()

  def stepOut(): Unit =
    scribe.trace(s"Stepping out, frame: $frame")
    val startFrames = frames.size
    if !this.finished then
      stepInto()
      while !this.finished && !this.atBreakpoint && frames.size >= startFrames
      do stepInto()

  /** Continue to the next breakpoint */
  def continue(): Unit =
    if !this.finished then
      stepInto()
      while !this.finished && !this.atBreakpoint do stepInto()

  /** Continue to the end of the program */
  def resume(): Unit = while !this.finished do stepInto()

  /** Evaluate some code in the current frame's context */
  def eval(code: String): Unit = Interpreter.execute(code)(using frame.ctx)
end Debugger

object Debugger:

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
      args: Seq[VAny] | Null = null,
  ): Step =
    fn.originalAST match
      case Some(fnDef) => NewStackFrame(
          fnDef,
          fn.name.getOrElse("<function>"),
          ctx => Context.makeFnCtx(fn.ctx, ctx, ???, ???, ???, ???, ???, ???),
          StepSeq(fnDef.body.map(Step.stepsForAST)),
        )
      case None => Step.hidden { Interpreter.executeFn(fn) }

  /** Make a step to execute code */
  def execCode(code: String)(using Context): Step =
    val ast = Parser().parse(Lexer(code))
    Step.stepsForAST(ast)

end Debugger
