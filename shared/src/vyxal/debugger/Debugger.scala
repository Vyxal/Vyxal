package vyxal.debugger

import vyxal.*
import vyxal.debugger.StepRes.*

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/** @param ctx
  *   The Context for this stack frame. May be the same as the outer stack
  *   frame's context.
  * @param ast
  *   The AST for the structure that caused the new stack frame
  * @param code
  *   The ASTs to be executed inside the structure. Note that this isn't simply
  *   an iterator over the body of the structure. For example, for an if
  *   statement, this would first provide the AST for the condition, then,
  *   depending on the top of the stack, choose whether to provide the AST for
  *   the truthy or falsy branch.
  */
case class StackFrame(
    name: Option[String],
    ctx: Context,
    ast: AST,
    step: StepRes,
    var code: Iterator[AST]
)

sealed trait Steps:
  def currAST: AST

  /** The new stack frame for this structure/element, if applicable */
  def frame(using Context): Option[StackFrame] = None

  def next()(using Context): Option[Steps]

object Steps:
  case class FnCall(fn: VFun) extends Steps:
    override def frame(using currCtx: Context): Option[StackFrame] =
      val fnCtx = Context.makeFnCtx(
          fn.ctx,
          currCtx,
          ctxVarPrimary = None,
          ctxVarSecondary = null
        )
      Some(StackFrame(???, fnCtx, ???, ???))

  case class While(loop: AST.While) extends Steps:
    override def frame(using Context): Option[StackFrame] = Some()

  case class For(loop: AST.For) extends Steps

  case class IfStmt(ifStmt: AST.IfStatement) extends Steps

  case class Lst(lst: AST.Lst) extends Steps

  case class ExecAST(ast: AST) extends Steps
end Steps

/** The result of stepping into an element */
enum StepRes(val ast: AST):
  /** Call a function */
  case FnCall(fn: VFun) extends StepRes(fn.originalAST.get)

  case For(loop: AST.For) extends StepRes(loop)
  case While(loop: AST.While) extends StepRes(loop)
  case If(ifStmt: AST.IfStatement) extends StepRes(ifStmt)
  case Lst(lst: AST.Lst) extends StepRes(lst)

  /** Process `orig`, then run `fn` right after (in the same step) */
  case Then(orig: StepRes, fn: () => Unit) extends StepRes(orig.ast)

  /** Execute `first`, then `second` */
  case Chain(first: StepRes, second: StepRes) extends StepRes(first.ast)

  /** Execute an AST */
  case Exec(ast: AST) extends StepRes(ast)

  def map(fn: => Unit): StepRes = Then(this, () => fn)
end StepRes

class Debugger(code: AST)(using rootCtx: Context):

  private val stackFrames: ArrayBuffer[StackFrame] = ArrayBuffer(
    StackFrame(Some("<root>"), rootCtx, code, Exec(code), Iterator.single(code))
  )

  /** The current stack frame */
  private def frame: StackFrame = stackFrames.last

  /** The current context */
  private def ctx: Context = stackFrames.last.ctx

  def getStackFrames: List[(String, Context)] = stackFrames.toList
    .filter(_.name.nonEmpty)
    .map(frame => (frame.name.get, frame.ctx))

  def addBreakpoint(row: Int, col: Int): Unit = ???

  def stepInto(): Unit =
    val foo = nextStep(frame.step)
    ???

  def stepOver(): Unit = ???

  def stepOut(): Unit = ???

  def continue(): Unit = ???

  private def nextStep(step: StepRes): Option[StepRes] =
    step match
      case Exec(ast) => stepIntoAST(ast)
      case Then(orig, fn) =>
        nextStep(orig) match
          case Some(res) => Some(Then(res, fn))
          case None =>
            fn()
            None
      case Chain(first, second) =>
        nextStep(first) match
          case Some(res) => Some(Chain(res, second))
          case None => second
      case _ => ???

  private def stepIntoAST(ast: AST): Option[StepRes] =
    ast match
      case AST.Number(value, _) =>
        ctx.push(value)
        None
      case AST.Str(value, _) =>
        ctx.push(value)
        None
      case AST.DictionaryString(value, _) =>
        ctx.push(StringHelpers.decompress(value))
        None
      case AST.Lst(elems, _) => StepRes.Lst(ast)
      case AST.Command(symbol, _) =>
        // todo put the string "E" into a constant somewhere
        if symbol == "E" && ctx.peek.isInstanceOf[VFun] then
          val fn = ctx.peek.asInstanceOf[VFun]
          fn.originalAST match
            case None =>
              // No need to step in if the function wasn't user-defined
              ctx.push(Interpreter.executeFn(fn))
              None
            case Some(origAST) => Some(FnCall(fn))
        else
          DebugImpls.impls.get(symbol) match
            case Some(impl) => impl()(using ctx)
            case None =>
              Elements.elements.get(symbol) match
                case Some(element) =>
                  element.impl()(using ctx)
                  None
                case None => throw RuntimeException(s"No such element: $symbol")
      case ifStmt: AST.IfStatement => Some(StepRes.If(ifStmt))
      case loop: AST.While => Some(StepRes.While(loop))
      case loop: AST.For => Some(StepRes.For(loop))
      case AST.Group(elems, _, _) =>
        elems match
          case first :: rest =>
            val restSteps = rest.map(Exec(_))
            stepIntoAST(first) match
              case Some(firstStep) =>
                Some(restSteps.foldLeft(firstStep)(Chain(_, _)))
              case None =>
                Some(restSteps.reduce(Chain(_, _)))
          case Nil => None
end Debugger
