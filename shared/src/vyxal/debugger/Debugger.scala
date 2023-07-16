package vyxal.debugger

import vyxal.{AST, Context}
import vyxal.impls.Elements

import scala.collection.mutable.ArrayBuffer

case class StackFrame(ctx: Context, tree: AST)

/** The result of stepping into an element */
enum StepRes:
  /** Call a function, and then run `next` to finish executing this element */
  case FnCall(fn: AST.FnDef, next: () => StepRes)

  /** The element finished executing in a single step */
  case Done

class Debugger(code: AST)(using rootCtx: Context):
  private val stackframes: ArrayBuffer[StackFrame] = ArrayBuffer(
    StackFrame(rootCtx, code)
  )

  private var currSteps: Iterator[AST] = ???

  /** The current context */
  private def ctx: Context = stackframes.last.ctx

  def addBreakpoint(row: Int, col: Int): Unit = ???

  def stepInto(): Unit =
    if currSteps.hasNext then
      val curr = currSteps.next

  def stepOver(): Unit = ???

  def stepOut(): Unit = ???

  def continue(): Unit = ???

  private def singleStep(ast: AST): StepRes =
    ast match
      case AST.Number(value, _) =>
        ctx.push(value)
        StepRes.Done
      case AST.Str(value, _) =>
        ctx.push(value)
        StepRes.Done
      case AST.Command(symbol, _) =>
        DebugImpls.impls.get(symbol) match
          case Some(impl) => impl()(using ctx)
          case None =>
            Elements.elements.get(symbol) match
              case Some(element) =>
                element.impl()(using ctx)
                StepRes.Done
              case None => throw RuntimeException(s"No such element: $symbol")
      case _ => ???
end Debugger
