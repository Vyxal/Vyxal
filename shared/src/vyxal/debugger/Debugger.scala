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
  */
class StackFrame(
    val name: String,
    val ctx: Context,
    val ast: AST,
):
  private[debugger] val stepStack = mutable.Stack.empty[Step]

  override def toString = s"Frame $name for $ast (top: ${ctx.peek})"

type NextStep = () => Context ?=> Option[Step]

sealed trait Step:
  def ast: AST

  def next: NextStep

case class NewStackFrame(
    ast: AST,
    frameName: String,
    newCtx: Context => Context,
    next: NextStep
) extends Step

case class EndStackFrame(next: NextStep) extends Step

case class NewBlock(ast: AST, next: NextStep) extends Step

case class EndBlock(ast: AST, next: NextStep) extends Step

case class Exec(ast: AST, exec: () => Context ?=> Unit, next: NextStep)
    extends Step

object Step:
  private def fnCall(fn: VFun): Block =
    NewStackFrame(
      fn.originalAST.get,
      fn.name.getOrElse("<function>"),
      ctx => Context.makeFnCtx(fn.ctx, ctx, ???, ???, ???, ???, ???, ???),
      fn.originalAST.get.body
        .foldRight(None: Option[Step]) { (bodyPart, acc) =>
          Some(Block(bodyPart, stepsForAST(bodyPart), () => ctx ?=> acc))
        }
        .getOrElse(StepSeq(fn.originalAST.get, () => ctx ?=> None)),
      () => ctx ?=> None,
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
    Exec(
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

  def stepsForAST(ast: AST): Iterator[Step] =
    ast match
      case AST.Number(value, _) =>
        Exec(ast, () => ctx ?=> ctx.push(value))
      case lst: AST.Lst => listStep(lst)
      case loop: AST.For => forStep(loop)
      case loop: AST.While => whileStep(loop)
      case ifStmt: AST.IfStatement => ifStep(ifStmt)
      case cmd: AST.Command => cmdStep(cmd)
      case AST.Group(elems, _, _) => elems.map(stepsForAST).reduce(_ ++ _)
      case _ => ???
end Step

class Debugger(code: AST)(using rootCtx: Context):

  private val stackFrames: ArrayBuffer[StackFrame] =
    val block = Step.blockForAST(code)
    ArrayBuffer(StackFrame("<root>", rootCtx, code, block, block.step))

  /** The current stack frame */
  private def frame: StackFrame = stackFrames.last

  private val steps: Iterator[Step] = ???

  def addBreakpoint(row: Int, col: Int): Unit = ???

  def stepInto(): Unit =
    scribe.trace(s"Stepping in, frame: $frame")
    if steps.hasNext then
      steps.next() match
        case NewStackFrame(ast, frameName, newCtx) =>
          stackFrames += StackFrame(frameName, newCtx(frame.ctx), ast)
        case EndStackFrame(ast) =>
          stackFrames.remove(stackFrames.size - 1)
        case block @ NewBlock(ast) =>
          frame.stepStack += block
        case EndBlock(ast) =>
          frame.stepStack.remove(stackFrames.size - 1)
        case Exec(ast, exec) =>
          exec()(using frame.ctx)

  def stepOver(): Unit =
    scribe.trace(s"Stepping over, frame: $frame")
    val startFrames = stackFrames.size
    val startSteps = frame.stepStack.size
    if steps.hasNext then stepInto()
    while steps.hasNext && stackFrames.size >= startFrames && frame.stepStack.size > startSteps
    do stepInto()

  def stepOut(): Unit =
    scribe.trace(s"Stepping out, frame: $frame")
    val startFrames = stackFrames.size
    while steps.hasNext && stackFrames.size >= startFrames do stepInto()

  def continue(): Unit =
    while stackFrames.nonEmpty do stepInto()

  def printState(): Unit =
    if stackFrames.nonEmpty then
      println(s"Next to execute: ${frame.step.currAST}")
      println(s"Top of stack is ${frame.ctx.peek}")
      println("Frames:")
      println(stackFrames.reverse.mkString("\n"))
    else println("Debugger finished")

  private def makeNewStackFrameIfNecessary(step: Step): Unit =
    step match
      case newFrame @ NewStackFrame(ast, frameName, newCtx, inner, next) =>
        stackFrames += StackFrame(
          frameName,
          newCtx(frame.ctx),
          ast,
          newFrame,
          inner
        )
      case _ =>
        frame.step = step
end Debugger
