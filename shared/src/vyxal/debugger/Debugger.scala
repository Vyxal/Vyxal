package vyxal.debugger

import vyxal.{AST, Context, MiscHelpers}
import vyxal.impls.Elements

import scala.collection.mutable.ArrayBuffer

import StepRes.*

case class StackFrame(ctx: Context, tree: AST)

/** The result of stepping into an element */
enum StepRes:
  /** Call a function */
  case FnCall(fn: VFun)

  /** Execute `first`, then `second` */
  case Then(first: StepRes, second: StepRes)

  /** The element finished executing in a single step */
  case Done

class Debugger(code: AST)(using rootCtx: Context):
  private val stackFrames: ArrayBuffer[StackFrame] = ArrayBuffer(
    StackFrame(rootCtx, code)
  )

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
      case AST.Lst(elems, _) =>
        elems match
          case first :: rest =>
            Structure(
              List(first),
              rest.foldRight { () => Done } { (elem, nextRes) => () =>
                Structure(List(elem), nextRes)
              }
            )
          case _ => Structure(Nil, () => Done)
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
      case (_: AST.While) | (_: AST.For) =>
        val loopCtx = ctx.makeChild()
        loopCtx.ctxVarPrimary = true
        loopCtx.ctxVarSecondary = ctx.settings.rangeStart
        stackFrames += StackFrame(loopCtx, ast)
        Done
      case _ => ???
end Debugger
