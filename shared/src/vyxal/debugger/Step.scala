package vyxal.debugger

import vyxal.*

import scala.collection.mutable.ListBuffer

sealed trait Step:
  def ast: AST

  def thenDo(fn: Context ?=> Option[Step]): Step =
    StepSeq(this, List(Lazy(() => fn)))

  def map(fn: VAny => Context ?=> Option[Step]): Step =
    this.thenDo(ctx ?=> fn(ctx.pop()))

  def flatMap(fn: VAny => Context ?=> Option[Step]): Step = this.map(fn)

case class NewStackFrame(
    ast: AST,
    frameName: String,
    newCtx: Context => Context,
    inner: Option[Step]
) extends Step

case class Block(ast: AST, inner: Option[Step]) extends Step

case class Exec(ast: AST, exec: () => Context ?=> Option[Step]) extends Step

/** Run something but don't show it as an extra step */
case class Hidden(exec: () => Context ?=> Unit) extends Step:
  override def ast: AST = throw UnsupportedOperationException(
    "Hey, this step is hidden, you're not supposed to be looking at its AST!"
  )

/** Execute a sequence of steps, beginning with `firstStep`
  *
  * `firstStep` is a separate parameter to ensure we always have at least one
  * step
  */
case class StepSeq(firstStep: Step, rest: Seq[Step]) extends Step:
  override def ast = firstStep.ast

case class Lazy(get: () => Context ?=> Option[Step]) extends Step:
  // TODO (user): Fix this somehow
  override def ast = throw UnsupportedOperationException(
    "Uhh whoops I haven't really bothered getting my AST yet I'll get around to it tomorrow"
  )

object Step:

  def empty: Step = Lazy(() => ctx ?=> None)

  /** A step that simply pushes a value onto the stack */
  def push(ast: AST, value: VAny): Step = Exec(
    ast,
    () =>
      ctx ?=>
        ctx.push(value)
        None
  )

  def seq(steps: Seq[Step]): Option[Step] =
    if steps.nonEmpty then Some(StepSeq(steps.head, steps.tail))
    else None

  private def whileStep(loop: AST.While): Step =
    val inner = loop.cond match
      case Some(cond) =>
        // While loop with condition
        lazy val condStep: Step =
          stepsForAST(cond).thenDo { ctx ?=>
            if MiscHelpers.boolify(ctx.pop()) then
              Some(StepSeq(stepsForAST(loop.body), List(condStep)))
            else None
          }

        Block(loop, Some(condStep))
      case None =>
        // Infinite loop, no condition
        lazy val bodyStep: Step =
          stepsForAST(loop.body).thenDo { ctx ?=> Some(bodyStep) }
        bodyStep

    NewStackFrame(
      loop,
      "<while>",
      ctx =>
        val loopCtx = ctx.makeChild()
        loopCtx.ctxVarPrimary = ctx.peek
        loopCtx.ctxVarSecondary = ctx.settings.rangeStart

        loopCtx
      ,
      Some(inner)
    )
  end whileStep

  private def forStep(loop: AST.For): Step =
    // TODO Forgive me, for I have sinned...
    var index = 0
    var loopIterable: Iterator[VAny] = null
    lazy val loopBlock: Step = stepsForAST(loop.body).thenDo { ctx ?=>
      if !loopIterable.hasNext then None
      else
        val elem = loopIterable.next()
        index += 1
        loop.loopVar.foreach(name => ctx.setVar(name, elem))
        ctx.ctxVarPrimary = elem
        ctx.ctxVarSecondary = index
        Some(loopBlock)
    }
    NewStackFrame(
      loop,
      "<for>",
      ctx =>
        loopIterable =
          ListHelpers.makeIterable(ctx.pop(), Some(true))(using ctx).iterator
        ctx.makeChild()
      ,
      Some(loopBlock)
    )
  end forStep

  private def ifStep(ifStmt: AST.IfStatement): Step =
    val elseStep = ifStmt.elseBody.map(stepsForAST)
    val inner = ifStmt.conds
      .zip(ifStmt.bodies)
      .foldRight(elseStep) { case ((cond, thenBody), elseStep) =>
        val thenBlock = stepsForAST(thenBody)
        Some(stepsForAST(cond).thenDo { ctx ?=>
          if MiscHelpers.boolify(ctx.pop()) then Some(thenBlock)
          else elseStep
        })
      }
    Block(ifStmt, inner)

  private def listStep(lst: AST.Lst): Step =
    val buf = ListBuffer.empty[VAny]
    val inner = lst.elems.flatMap { (elem) =>
      List(
        stepsForAST(elem),
        Hidden { () => ctx ?=>
          buf += ctx.pop()
        }
      )
    }
    val last = Hidden { () => ctx ?=> ctx.push(VList.from(buf.toList)) }
    Block(lst, Step.seq(inner :+ last))

  private def cmdStep(cmd: AST.Command): Step =
    val symbol = cmd.value
    DebugImpls.impls.get(symbol) match
      case Some(debugImpl) => Exec(cmd, debugImpl)
      case None =>
        Elements.elements.get(symbol) match
          case Some(element) =>
            Hidden(() => ctx ?=> element.impl())
          case None =>
            throw new RuntimeException(s"No such element: $symbol")

  def stepsForAST(ast: AST): Step =
    ast match
      case AST.Number(value, _) => Step.push(ast, value)
      case lst: AST.Lst => listStep(lst)
      case loop: AST.For => forStep(loop)
      case loop: AST.While => whileStep(loop)
      case ifStmt: AST.IfStatement => ifStep(ifStmt)
      case cmd: AST.Command => cmdStep(cmd)
      case AST.Group(elems, _, _) =>
        Block(ast, Step.seq(elems.map(stepsForAST)))
      case _ => ???
end Step
