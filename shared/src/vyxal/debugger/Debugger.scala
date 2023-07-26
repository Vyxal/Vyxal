package vyxal.debugger

import vyxal.*

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/** @param name
  *   The name of the function this stack frame is for (if applicable)
  * @param ctx
  *   The Context for this stack frame. May be the same as the outer stack
  *   frame's context.
  * @param ast
  *   The AST for the structure that caused the new stack frame
  * @param origBlock
  *   The block for which the stack frame was made
  * @param step
  *   The next step to be executed
  */
class StackFrame(
    val name: String,
    val ctx: Context,
    val ast: AST,
    val origBlock: Block,
    private[debugger] var step: Step
):
  override def toString = s"Frame $name for $ast (top: ${ctx.peek})"

// TODO this makes at least one new Step object for every command and structure
//   in the program, as well as closures.
//   Figure out if it's too inefficient or if it'll work.
sealed trait Step:
  /** Modify this step to run an extra bit of code after it's done */
  def thenDo(fn: Context ?=> Unit): Step

  def next: () => Context ?=> Option[Step]

  def currAST: AST

/** Holds a step to aid with the "step over" command
  *
  * @param stackFrame
  *   If this step requires creating a new stack frame, this will be a Some
  *   containing a function that takes a context and makes a new context to use
  *   for the new stack frame, as well as a name for the new stack frame.
  */
case class Block(
    ast: AST,
    step: Step,
    next: () => Context ?=> Option[Step] = () => ctx ?=> None,
    stackFrame: Option[(String, Context => Context)] = None
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

  override def currAST = step.currAST

/** A sequence of steps that requires stepping into
  * @param ast
  *   The AST we're currently executing
  * @param next
  *   Return the next step if there is one and None if we're done
  */
case class StepSeq(
    ast: AST,
    next: () => Context ?=> Option[Step]
) extends Step:

  override def currAST = ast

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

object Step:
  private def fnCall(fn: VFun): Block =
    Block(
      fn.originalAST.get,
      fn.originalAST.get.body
        .foldRight(None: Option[Step]) { (bodyPart, acc) =>
          Some(Block(bodyPart, stepsForAST(bodyPart), () => ctx ?=> acc))
        }
        .getOrElse(StepSeq(fn.originalAST.get, () => ctx ?=> None)),
      () => ctx ?=> None,
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
    val loopVariable = loop.loopVar.getOrElse("")
    val loopBody = stepsForAST(loop.body)
    StepSeq(
      loop,
      () =>
        ctx ?=>
          given loopCtx: Context = ctx.makeChild()
          val loopIterable = ListHelpers.makeIterable(ctx.pop())
          ctx.ctxVarSecondary = -1
          lazy val loopBlock: Block = Block(
            loop.body,
            loopBody,
            () =>
              ctx ?=>
                ctx.ctxVarSecondary =
                  (ctx.ctxVarSecondary.asInstanceOf[VNum]) + 1
                if ctx.ctxVarSecondary
                    .asInstanceOf[VNum]
                    .toBigInt >= loopIterable.length
                then None
                else
                  ctx.ctxVarPrimary = loopIterable(
                    ctx.ctxVarSecondary.asInstanceOf[VNum].toInt
                  )
                  Some(loopBlock)
          )
          Some(loopBlock)
    )
  end forStep

  private def ifStep(ifStmt: AST.IfStatement): StepSeq =
    StepSeq(
      ifStmt,
      () =>
        ctx ?=>
          ifStmt.conds
            .zip(ifStmt.bodies)
            .foldRight(ifStmt.elseBody.map(blockForAST)) {
              case ((cond, thenBody), elseBody) =>
                val thenBlock = blockForAST(thenBody)
                Some(
                  Block(
                    cond,
                    stepsForAST(cond),
                    () =>
                      ctx ?=>
                        if MiscHelpers.boolify(ctx.pop()) then Some(thenBlock)
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

  private def stepsForAST(ast: AST): Step =
    ast match
      case AST.Number(value, _) =>
        exec(ast) { ctx ?=> ctx.push(value) }
      case lst: AST.Lst => listStep(lst)
      case loop: AST.For => forStep(loop)
      case loop: AST.While => whileStep(loop)
      case ifStmt: AST.IfStatement => ifStep(ifStmt)
      case cmd: AST.Command => cmdStep(cmd)
      case AST.Group(elems, _, _) =>
        elems
          .foldRight(None: Option[Step]) { (elem, acc) =>
            Some(
              Block(
                elem,
                stepsForAST(elem),
                () => ctx ?=> acc
              )
            )
          }
          // For empty groups
          .getOrElse(StepSeq(ast, () => ctx ?=> None))
      case _ => ???

  def blockForAST(ast: AST): Block =
    Block(ast, stepsForAST(ast))
end Step

class Debugger(code: AST)(using rootCtx: Context):

  private val stackFrames: ArrayBuffer[StackFrame] =
    val block = Step.blockForAST(code)
    ArrayBuffer(StackFrame("<root>", rootCtx, code, block, block.step))

  /** The current stack frame */
  private def frame: StackFrame = stackFrames.last

  def addBreakpoint(row: Int, col: Int): Unit = ???

  def stepInto(): Unit =
    scribe.trace(s"Stepping in, frame: $frame")
    frame.step match
      case Block(ast, step, nextBlock, _) =>
        step.next() match
          case Some(nextStep) =>
            makeNewStackFrameIfNecessary(nextStep)
          case None =>
            nextBlock() match
              case Some(block) => frame.step = block
              case None => popFrame()
      case StepSeq(ast, next) =>
        next() match
          case Some(nextStep) =>
            makeNewStackFrameIfNecessary(nextStep)
          case None => popFrame()
  end stepInto

  def stepOver(): Unit =
    scribe.trace(s"Stepping over, frame: $frame")
    ???

  def stepOut(): Unit =
    scribe.trace(s"Stepping out, frame: $frame")
    ???

  def continue(): Unit =
    while stackFrames.nonEmpty do stepInto()

  def printState(): Unit =
    if stackFrames.nonEmpty then
      println(s"Next to execute: ${frame.step.currAST}")
      println(s"Top of stack is ${frame.ctx.peek}")
      println("Frames:")
      println(stackFrames.reverse.mkString("\n"))
    else println("Debugger finished")

  // @tailrec // Commenting for debugging purposes
  private def popFrame(): Unit =
    scribe.trace(s"stackFrames = ${stackFrames.toSeq}")
    val lastFrame = stackFrames.remove(stackFrames.size - 1)
    lastFrame.origBlock.next()(using lastFrame.ctx) match
      case Some(nextStep) => frame.step = nextStep
      case None =>
        if stackFrames.nonEmpty then popFrame()

  private def makeNewStackFrameIfNecessary(step: Step): Unit =
    step match
      case block @ Block(ast, firstStep, next, stackFrame) =>
        stackFrame match
          case Some((name, makeCtx)) =>
            stackFrames += StackFrame(
              name,
              makeCtx(frame.ctx),
              ast,
              block,
              firstStep
            )
          case None => frame.step = block
      case stepSeq =>
        frame.step = stepSeq
end Debugger
