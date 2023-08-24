package vyxal.debugger

import vyxal.*

import scala.collection.mutable.ListBuffer

sealed trait Step:
  def thenDo(fn: Context ?=> Option[Step]): Step =
    StepSeq(List(this, Lazy(() => fn)))

  def map(fn: VAny => Context ?=> Option[Step]): Step =
    this.thenDo(ctx ?=> fn(ctx.pop()))

  def flatMap(fn: VAny => Context ?=> Option[Step]): Step = this.map(fn)

  def foreach(fn: VAny => Context ?=> Unit): Step =
    StepSeq(List(this, Step.hidden { ctx ?=> fn(ctx.pop()) }))

/** A step corresponding to a real AST */
sealed trait ProperStep extends Step:
  def ast: AST

/** A structure that requires a new context, or a function call */
case class NewStackFrame(
    ast: AST,
    frameName: String,
    newCtx: Context => Context,
    inner: Step,
) extends ProperStep

/** Represents a structure that doesn't create a new frame, to allow stepping
  * over
  */
case class Block(ast: AST, inner: Step) extends ProperStep

/** Execute a command or push a number/string */
case class Exec(ast: AST, exec: () => (Debugger, Context) ?=> Option[Step])
    extends ProperStep

/** Run something but don't show it as an extra step */
case class Hidden(exec: () => Context ?=> Unit) extends Step

/** Execute a sequence of steps */
case class StepSeq(steps: Seq[Step]) extends Step

/** Lazily get the next step (used for if/while) */
case class Lazy(get: () => Context ?=> Option[Step]) extends Step

object Step:
  /** A step that simply pushes a value onto the stack */
  def push(ast: AST, value: VAny): Step =
    Exec(
      ast,
      () =>
        (_, ctx) ?=>
          ctx.push(value)
          None,
    )

  /** Helper so you don't have to write () => */
  def hidden(exec: Context ?=> Unit): Step = Hidden(() => exec)

  private def whileStep(loop: AST.While): Step =
    val inner = loop.cond match
      case Some(cond) =>
        // While loop with condition
        lazy val condStep: Step = stepsForAST(cond).thenDo { ctx ?=>
          if ctx.pop().toBool then
            Some(StepSeq(List(stepsForAST(loop.body), condStep)))
          else None
        }

        Block(loop, condStep)
      case None =>
        // Infinite loop, no condition
        lazy val bodyStep: Step = stepsForAST(loop.body)
          .thenDo { Some(bodyStep) }
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
      inner,
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
      loopBlock,
    )
  end forStep

  private def ifStep(ifStmt: AST.IfStatement): Step =
    val elseStep = ifStmt.elseBody.map(stepsForAST)
    val inner = ifStmt
      .conds
      .zip(ifStmt.bodies)
      .foldRight(elseStep) { case ((cond, thenBody), elseStep) =>
        val thenBlock = stepsForAST(thenBody)
        Some(stepsForAST(cond).thenDo { ctx ?=>
          if ctx.pop().toBool then Some(thenBlock) else elseStep
        })
      }
    Block(ifStmt, inner.getOrElse(StepSeq(List.empty)))

  private def listStep(lst: AST.Lst): Step =
    val buf = ListBuffer.empty[VAny]
    val inner = lst
      .elems
      .flatMap { (elem) =>
        List(stepsForAST(elem), Step.hidden { ctx ?=> buf += ctx.pop() })
      }
    val last = Step.hidden { ctx ?=> ctx.push(VList.from(buf.toList)) }
    Block(lst, StepSeq(inner :+ last))

  private def cmdStep(cmd: AST.Command): Step =
    val symbol = cmd.value
    DebugImpls.impls.get(symbol) match
      case Some(debugImpl) => Exec(cmd, debugImpl)
      case None => Elements.elements.get(symbol) match
          case Some(element) => Step.hidden { element.impl() }
          case None => throw RuntimeException(s"No such element: $symbol")

  def stepsForAST(ast: AST): Step =
    ast match
      case AST.Number(value, _) => Step.push(ast, value)
      case AST.Str(value, _) => Step.push(ast, value)
      case lst: AST.Lst => listStep(lst)
      case loop: AST.For => forStep(loop)
      case loop: AST.While => whileStep(loop)
      case ifStmt: AST.IfStatement => ifStep(ifStmt)
      case cmd: AST.Command => cmdStep(cmd)
      case AST.Group(elems, _, _) => Block(ast, StepSeq(elems.map(stepsForAST)))
      case _ => ???
end Step
