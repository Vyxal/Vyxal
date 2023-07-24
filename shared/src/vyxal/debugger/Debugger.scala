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
  case NewStep(step: StepSeq)

// TODO this makes at least one new Step object for every command and structure
//   in the program, as well as closures. The chain method can't be cheap either.
//   Figure out if it's too inefficient or if it'll work.

sealed trait Step:
  /** Modify this step to run an extra bit of code after it's done */
  def thenDo(fn: Context ?=> Unit): Step

  def next: () => Context ?=> Option[Step]

  def chainLazy(chained: Context ?=> Option[Step]): Step

  /** Make a new Step that executes this step, then the given one */
  def chain(chained: Step): Step

/** Holds a step to aid with the "step over" command */
case class Block(
    ast: AST,
    step: StepSeq,
    next: () => Context ?=> Option[Step] = () => ctx ?=> None
) extends Step:
  /** Modify this step to run an extra bit of code after it's done */
  def thenDo(fn: Context ?=> Unit): Step =
    this.copy(next =
      () =>
        ctx ?=>
          this.next() match
            case Some(nextStep) => Some(nextStep.thenDo(fn))
            case None =>
              fn(using ctx)
              None
    )

  def chain(second: Step): Block =
    /** Turn the next step into a block */
    val block = second match
      case step: StepSeq => Block(step.ast, step)
      case block => block
    this.copy(next =
      () => ctx ?=> Some(this.next().fold(block: Step)(_.chain(second)))
    )

  def chainLazy(second: Context ?=> Option[Step]): Block =
    /** Turn the next step into a block */
    this.copy(next =
      () =>
        ctx ?=>
          this
            .next()
            .fold(second.map:
              case step: StepSeq => Block(step.ast, step)
              case block => block
            )(next => Some(next.chainLazy(second)))
    )
end Block

/** A sequence of steps that requires stepping into
  * @param ast
  *   The AST we're currently executing
  * @param next
  *   Return the next step if there is one and None if we're done
  * @param stackFrame
  *   If this step requires creating a new stack frame, this will be a Some
  *   containing a function that takes a context and makes a new context to use
  *   for the new stack frame, as well as a name for the new stack frame.
  */
case class StepSeq(
    ast: AST,
    next: () => Context ?=> Option[Step],
    stackFrame: Option[(String, Context => Context)] = None
) extends Step:

  /** Modify this step to run an extra bit of code after it's done */
  def thenDo(fn: Context ?=> Unit): StepSeq =
    this.copy(next =
      () =>
        ctx ?=>
          this.next() match
            case Some(nextStep) => Some(nextStep.thenDo(fn))
            case None =>
              fn(using ctx)
              None
    )

  def chain(chained: Step): StepSeq =
    this.copy(
      next = () => ctx ?=> Some(this.next().fold(chained)(_.chain(chained))),
    )

  // TODO(ysthakur): This is some ugly code. You should be ashamed of yourself.
  def chainLazy(chained: Context ?=> Option[Step]): StepSeq =
    this.copy(next =
      () =>
        ctx ?=>
          this
            .next()
            .fold(chained)(nextStep => Some(nextStep.chainLazy(chained))),
    )
end StepSeq

object Step:
  private def fnCall(fn: VFun): StepSeq =
    StepSeq(
      fn.originalAST.get,
      () =>
        ctx ?=>
          val body = fn.originalAST.get.body
          Option.when(body.nonEmpty)(
            fn.originalAST.get.body.map(blockForAST).reduce(_.chain(_))
          )
      ,
      Some(
        (
          fn.name.getOrElse("<function>"),
          ctx => Context.makeFnCtx(fn.ctx, ctx, ???, ???, ???, ???, ???, ???)
        )
      )
    )

  private def whileStep(loop: AST.While): StepSeq =
    val bodySteps = stepsForAST(loop.body)
    val condSteps = loop.cond.map(stepsForAST)
    StepSeq(
      loop,
      () =>
        ctx ?=>
          given loopCtx: Context = ctx.makeChild()

          loopCtx.ctxVarPrimary = ctx.peek
          loopCtx.ctxVarSecondary = ctx.settings.rangeStart

          condSteps match
            case Some(condSteps) =>
              // While loop with condition
              lazy val bodyBlock: Block =
                Block(loop.body, bodySteps, () => ctx ?=> Some(condBlock))
              lazy val condBlock: Block =
                Block(
                  loop.cond.get,
                  condSteps,
                  () =>
                    ctx ?=>
                      if MiscHelpers.boolify(ctx.pop()) then Some(bodyBlock)
                      else None
                )
              Some(condBlock)
            case None =>
              // Infinite loop, no condition
              lazy val loopBlock: Block =
                Block(loop.body, bodySteps, () => ctx ?=> Some(loopBlock))
              Some(loopBlock)
          end match
    )
  end whileStep

  private def forStep(loop: AST.For): StepSeq =
    ???

  private def ifStep(ifStmt: AST.IfStatement): StepSeq =
    StepSeq(
      ifStmt,
      () =>
        ctx ?=>
          ifStmt.conds
            .zip(ifStmt.bodies)
            .foldRight(ifStmt.elseBody.map(blockForAST)) {
              case ((cond, thenBody), elseBody) =>
                val condSteps = stepsForAST(cond)
                val thenSteps = stepsForAST(thenBody)
                Some(
                  Block(
                    cond,
                    condSteps,
                    () =>
                      ctx ?=>
                        if MiscHelpers.boolify(ctx.pop()) then
                          Some(blockForAST(thenBody))
                        else elseBody
                  )
                )
            }
    )

  private def listStep(lst: AST.Lst): StepSeq =
    val list = ListBuffer.empty[VAny]
    StepSeq(
      lst,
      () =>
        ctx ?=>
          lst.elems
            .foldRight(None: Option[Block]) { (elem, acc) =>
              Some(
                Block(
                  elem,
                  stepsForAST(elem).thenDo(ctx ?=> list.addOne(ctx.pop())),
                  () => ctx ?=> acc
                )
              )
            }
    )
  end listStep

  private def cmdStep(cmd: AST.Command): StepSeq =
    StepSeq(
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
  private def exec(ast: AST)(fn: Context ?=> Unit): StepSeq =
    StepSeq(
      ast,
      () =>
        ctx ?=>
          fn(using ctx)
          None
    )

  private def stepsForAST(ast: AST): StepSeq =
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

  def blockForAST(ast: AST): Block =
    Block(ast, stepsForAST(ast))
end Step

class Debugger(code: AST)(using rootCtx: Context):

  private val stackFrames: ArrayBuffer[StackFrame] = ArrayBuffer(
    StackFrame(Some("<root>"), rootCtx, code, Step.blockForAST(code))
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
