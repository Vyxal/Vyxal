package vyxal.debugger

import vyxal.*
import vyxal.impls.Elements

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

import StepRes.*

/** The result of stepping into an element */
enum StepRes:
  /** Call a function */
  case FnCall(fn: VFun)

  /** Process `orig`, then run `fn` right after (in the same step) */
  case Then(orig: StepRes, fn: () => Unit)

  /** Execute `first`, then `second` */
  case FlatMap(first: StepRes, second: () => StepRes)

  /** The element finished executing in a single step */
  case Done

  case Exec(ast: AST)

  def map(fn: => Unit): StepRes = Then(this, () => fn)

  def flatMap(next: => StepRes): StepRes = FlatMap(this, () => next)
end StepRes

class Debugger(code: AST)(using rootCtx: Context):
  case class StackFrame(ctx: Context, tree: AST)

  private val stackFrames: ArrayBuffer[StackFrame] = ArrayBuffer(
    StackFrame(rootCtx, code)
  )

  /** The current stack frame */
  private def frame: StackFrame = stackFrames.last

  /** A queue with the current steps to execute */
  private val currSteps = ArrayBuffer.empty[AST]

  /** The current context */
  private def ctx: Context = stackFrames.last.ctx

  def addBreakpoint(row: Int, col: Int): Unit = ???

  def stepInto(): Unit =
    if currSteps.nonEmpty then
      val curr = currSteps.remove(0)

  def stepOver(): Unit = ???

  def stepOut(): Unit = ???

  def continue(): Unit = ???

  private def singleStep(ast: AST): StepRes =
    ast match
      case AST.Number(value, _) =>
        ctx.push(value)
        Done
      case AST.Str(value, _) =>
        ctx.push(value)
        Done
      case AST.Lst(elems, _) => ???
//        val list = ListBuffer.empty[VAny]
//        elems.map { elem =>
//          Exec(elem).map { list.append(ctx.pop()) }
//        }
//        elems match
//          case first :: rest =>
//            rest.foldLeft(Exec(first)) { (res, elem) =>
//              res.flatMap(() => Exec(elem))
//            }
//          case _ => ???
      case AST.Command(symbol, _) =>
        // todo put the string "E" into a constant somewhere
        if symbol == "E" && ctx.peek.isInstanceOf[VFun] then
          FnCall(ctx.peek.asInstanceOf[VFun])
        else
          DebugImpls.impls.get(symbol) match
            case Some(impl) => impl()(using ctx)
            case None =>
              Elements.elements.get(symbol) match
                case Some(element) =>
                  element.impl()(using ctx)
                  Done
                case None => throw RuntimeException(s"No such element: $symbol")
      case AST.IfStatement(conds, bodies, elseBody, range) => ???
      case _: AST.While | _: AST.For =>
        val loopCtx = ctx.makeChild()
        loopCtx.ctxVarPrimary = true
        loopCtx.ctxVarSecondary = ctx.settings.rangeStart
        stackFrames += StackFrame(loopCtx, ast)
        Done
end Debugger
