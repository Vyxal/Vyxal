package vyxal.debugger

import vyxal.*

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
  private[debugger] var stepDepth = 0

  override def toString = s"Frame $name for $ast (top: ${ctx.peek})"

case class Step(ast: AST, kind: StepKind, next: Step.NextStep)

sealed trait StepKind

case class NewStackFrame(
    frameName: String,
    newCtx: Context => Context,
) extends StepKind

object EndStackFrame extends StepKind

object NewBlock extends StepKind

object EndBlock extends StepKind

object Exec extends StepKind

object Step:
  type NextStep = () => Context ?=> Option[Step]

  val defaultNext: NextStep = () => _ ?=> None

  /** Run first, then second */
  def chain(first: Step, second: NextStep): Step =
    first.copy(next =
      () =>
        ctx ?=>
          first.next() match
            case Some(next) => Some(Step.chain(next, second))
            case None => second()
    )

  def fnCall(fn: VFun, fnDef: AST.Lambda, next: NextStep): Step =
    val inner = fnDef.body.foldRight(Step(fnDef, EndStackFrame, next)) {
      (bodyPart, acc) =>
        stepsForAST(bodyPart, () => ctx ?=> Some(acc))
    }
    Step(
      fnDef,
      NewStackFrame(
        fn.name.getOrElse("<function>"),
        ctx => Context.makeFnCtx(fn.ctx, ctx, ???, ???, ???, ???, ???, ???)
      ),
      () => ctx ?=> Some(inner)
    )

  private def whileStep(loop: AST.While, next: NextStep): Step =
    val inner = loop.cond match
      case Some(cond) =>
        // While loop with condition
        val endLoop = Step(loop, EndStackFrame, next)
        lazy val bodyStep: Step =
          stepsForAST(loop.body, () => ctx ?=> Some(condStep))
        lazy val condStep: Step =
          stepsForAST(
            cond,
            () =>
              ctx ?=>
                if MiscHelpers.boolify(ctx.pop()) then Some(bodyStep)
                else Some(endLoop)
          )

        condStep
      case None =>
        // Infinite loop, no condition
        lazy val bodyStep: Step = stepsForAST(
          loop.body,
          () => ctx ?=> Some(bodyStep)
        )
        bodyStep

    Step(
      loop,
      NewStackFrame(
        "<while>",
        ctx =>
          val loopCtx = ctx.makeChild()
          loopCtx.ctxVarPrimary = ctx.peek
          loopCtx.ctxVarSecondary = ctx.settings.rangeStart

          loopCtx
      ),
      () => ctx ?=> Some(inner)
    )
  end whileStep

  private def forStep(loop: AST.For, next: NextStep): Step =
    val loopEnd = Step(loop, EndBlock, next)

    // TODO Forgive me, for I have sinned...
    var index = 0
    var loopIterable: Iterator[VAny] = null
    lazy val loopBlock: Step = stepsForAST(
      loop.body,
      () =>
        ctx ?=>
          if !loopIterable.hasNext then Some(loopEnd)
          else
            val elem = loopIterable.next()
            index += 1
            loop.loopVar.foreach(name => ctx.setVar(name, elem))
            ctx.ctxVarPrimary = elem
            ctx.ctxVarSecondary = index
            Some(loopBlock)
    )
    Step(
      loop,
      NewStackFrame(
        "<for>",
        ctx =>
          loopIterable =
            ListHelpers.makeIterable(ctx.pop(), Some(true))(using ctx).iterator
          ctx.makeChild()
      ),
      () => ctx ?=> Some(loopBlock)
    )
  end forStep

  private def ifStep(ifStmt: AST.IfStatement, next: NextStep): Step =
    val endStep = Step(ifStmt, EndBlock, next)
    val elseStep =
      ifStmt.elseBody
        .map(body => stepsForAST(body, () => ctx ?=> Some(endStep)))
        .getOrElse(endStep)
    val inner = ifStmt.conds
      .zip(ifStmt.bodies)
      .foldRight(elseStep) { case ((cond, thenBody), elseStep) =>
        val thenBlock = stepsForAST(thenBody, () => ctx ?=> Some(endStep))
        stepsForAST(
          cond,
          () =>
            ctx ?=>
              if MiscHelpers.boolify(ctx.pop()) then Some(thenBlock)
              else Some(elseStep)
        )
      }
    Step(ifStmt, NewBlock, () => ctx ?=> Some(inner))
  end ifStep

  private def listStep(lst: AST.Lst, next: NextStep): Step =
    val list = ListBuffer.empty[VAny]
    val endStep = Step(
      lst,
      EndBlock,
      () =>
        ctx ?=>
          ctx.push(VList.from(list.toList))
          next()
    )
    val inner = lst.elems
      .foldRight(endStep) { (elem, acc) =>
        stepsForAST(
          elem,
          () =>
            ctx ?=>
              list += ctx.pop()
              Some(acc)
        )
      }
    Step(lst, NewBlock, () => ctx ?=> Some(inner))
  end listStep

  private def cmdStep(cmd: AST.Command, next: NextStep): Step =
    val symbol = cmd.value
    DebugImpls.impls.get(symbol) match
      case Some(debugImpl) =>
        Step.chain(Step(cmd, Exec, debugImpl), next)
      case None =>
        Elements.elements.get(symbol) match
          case Some(element) =>
            Step(
              cmd,
              Exec,
              () =>
                ctx ?=>
                  element.impl()
                  next()
            )
          case None =>
            throw new RuntimeException(s"No such element: $symbol")
    end match
  end cmdStep

  def stepsForAST(ast: AST, next: NextStep = () => _ ?=> None): Step =
    ast match
      case AST.Number(value, _) =>
        Step(
          ast,
          Exec,
          () =>
            ctx ?=>
              ctx.push(value)
              next()
        )
      case lst: AST.Lst => listStep(lst, next)
      case loop: AST.For => forStep(loop, next)
      case loop: AST.While => whileStep(loop, next)
      case ifStmt: AST.IfStatement => ifStep(ifStmt, next)
      case cmd: AST.Command => cmdStep(cmd, next)
      case AST.Group(elems, _, _) =>
        val inner = elems.foldRight(Step(ast, EndBlock, next)) { (elem, acc) =>
          stepsForAST(elem, () => ctx ?=> Some(acc))
        }
        Step(ast, NewBlock, () => ctx ?=> Some(inner))
      case _ => ???
end Step

class Debugger(code: AST)(using rootCtx: Context):

  private val stackFrames: ArrayBuffer[StackFrame] =
    ArrayBuffer(StackFrame("<root>", rootCtx, code))

  /** The current stack frame */
  private def frame: StackFrame = stackFrames.last

  private var currStep: Option[Step] = Some(Step.stepsForAST(code))

  def addBreakpoint(row: Int, col: Int): Unit = ???

  def finished: Boolean = currStep.isEmpty

  def stepInto(): Unit =
    scribe.trace(s"Stepping in, frame: $frame")
    for step <- currStep do
      step.kind match
        case NewStackFrame(frameName, newCtx) =>
          stackFrames += StackFrame(frameName, newCtx(frame.ctx), step.ast)
        case EndStackFrame =>
          stackFrames.remove(stackFrames.size - 1)
        case NewBlock =>
          frame.stepDepth += 1
        case EndBlock =>
          frame.stepDepth -= 1
        case Exec => ()
      this.currStep = step.next()(using frame.ctx)

  def stepOver(): Unit =
    scribe.trace(s"Stepping over, frame: $frame")
    val startFrames = stackFrames.size
    val startStepDepth = frame.stepDepth
    if currStep.isDefined then stepInto()
    while currStep.isDefined && stackFrames.size >= startFrames && frame.stepDepth > startStepDepth
    do stepInto()

  def stepOut(): Unit =
    scribe.trace(s"Stepping out, frame: $frame")
    val startFrames = stackFrames.size
    while currStep.isDefined && stackFrames.size >= startFrames do stepInto()

  def continue(): Unit =
    while stackFrames.nonEmpty do stepInto()

  def printState(): Unit =
    this.currStep match
      case Some(step) =>
        println(s"Next to execute: ${step.ast}")
        println(s"Top of stack is ${frame.ctx.peek}")
        println("Frames:")
        println(stackFrames.reverse.mkString("\n"))
      case _ => println("Debugger finished")
end Debugger
