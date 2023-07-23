package vyxal.debugger

import vyxal.*

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/** @param name
  *   The name of the function this stack frame is for (if applicable)
  * @param ctx
  *   The Context for this stack frame. May be the same as the outer stack
  *   frame's context.
  * @param ast
  *   The AST for the structure that caused the new stack frame
  */
class StackFrame(
    val name: Option[String],
    val ctx: Context,
    val ast: AST,
    private var step: Step,
)

enum StepRes:
  /** We're completely done executing this structure/element */
  case Done

  /** Move to the next part of this structure/element */
  case Next

  /** Execute this new step before continuing with the structure/element */
  case NewStep(step: Step)

// TODO this makes at least one new Step object for every command and structure
//   in the program, as well as closures. The chain method can't be cheap either.
//   Figure out if it's too inefficient or if it'll work.

/** @param currAST
  *   The AST we're currently executing
  * @param next
  *   Return the next step if there is one and None if we're done
  * @param stackFrame
  *   A new stack frame for this step, if applicable
  */
case class Step(
    currAST: AST,
    next: () => Context ?=> Option[Step],
    stackFrame: Option[StackFrame] = None
):

  /** Make a new Step that executes this step, then the given one */
  def chain(chained: Step): Step =
    Step(
      this.currAST,
      () => ctx ?=> Some(this.next().fold(chained)(_.chain(chained))),
      this.stackFrame
    )

  // TODO(ysthakur): This is some ugly code. You should be ashamed of yourself.
  def chainLazy(chained: Context ?=> Option[Step]): Step =
    Step(
      this.currAST,
      () =>
        ctx ?=>
          this
            .next()
            .fold(chained)(nextStep => Some(nextStep.chainLazy(chained))),
      this.stackFrame
    )
end Step

object Step:
  def fnCall(fn: VFun): Step =
    Step(fn.originalAST.get, ???, ???)

  def whileStep(loop: AST.While): Step =
    Step(
      loop,
      () =>
        ctx ?=>
          given loopCtx: Context = ctx.makeChild()

          loopCtx.ctxVarPrimary = ctx.peek
          loopCtx.ctxVarSecondary = ctx.settings.rangeStart

          val bodyStep = stepsForAST(loop.body)
          loop.cond match
            case Some(cond) =>
              val condStep = stepsForAST(cond)
              ???
            case None =>
              // Infinite loop
              lazy val res: Step = bodyStep.chainLazy { Some(res) }
              Some(res)
    )

  def forStep(loop: AST.For): Step =
    ???

  def ifStep(ifStmt: AST.IfStatement): Step =
    Step(
      ifStmt,
      () =>
        ctx ?=>
          ifStmt.conds
            .zip(ifStmt.bodies)
            .foldRight(ifStmt.elseBody.map(stepsForAST)) {
              case ((cond, thenBody), elseBody) =>
                Some(stepsForAST(cond).chainLazy { ctx ?=>
                  if MiscHelpers.boolify(ctx.pop()) then
                    Some(stepsForAST(thenBody))
                  else elseBody
                })
            }
    )

  def listStep(lst: AST.Lst): Step = ???

  def cmdStep(cmd: AST.Command): Step =
    Step(
      cmd,
      () =>
        ctx ?=>
          val symbol = cmd.value
          DebugImpls.impls.get(symbol) match
            case Some(debugImpl) => debugImpl()
            case None =>
              Elements.elements.get(symbol) match
                case Some(element) =>
                  element.impl()
                  None
                case None =>
                  throw new RuntimeException(s"No such element: $symbol")
    )

  /** Helper to concisely make steps that run a non-Vyxal thing */
  def exec(ast: AST)(fn: Context ?=> Unit): Step =
    Step(
      ast,
      () =>
        ctx ?=>
          fn(using ctx)
          None
    )

  def stepsForAST(ast: AST): Step =
    ast match
      case AST.Number(value, _) =>
        exec(ast) { ctx ?=> ctx.push(value) }
      case lst: AST.Lst => listStep(lst)
      case loop: AST.For => forStep(loop)
      case loop: AST.While => whileStep(loop)
      case ifStmt: AST.IfStatement => ifStep(ifStmt)
      case AST.Group(elems, _, _) =>
        // TODO(ysthakur): handle empty groups?
        elems.map(stepsForAST).reduce(_.chain(_))
      case _ => ???
end Step

class Debugger(code: AST)(using rootCtx: Context):

  private val stackFrames: ArrayBuffer[StackFrame] = ArrayBuffer(
    StackFrame(Some("<root>"), rootCtx, code, Step.stepsForAST(code))
  )

  /** The current stack frame */
  private def frame: StackFrame = stackFrames.last

  /** The current context */
  private def ctx: Context = stackFrames.last.ctx

  def getStackFrames: List[(String, Context)] = stackFrames.toList
    .filter(_.name.nonEmpty)
    .map(frame => (frame.name.get, frame.ctx))

  def addBreakpoint(row: Int, col: Int): Unit = ???

  def stepInto(): Unit = ???

  def stepOver(): Unit = ???

  def stepOut(): Unit = ???

  def continue(): Unit = ???
end Debugger
