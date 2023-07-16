package vyxal.debugger

import vyxal.{AST, Context, MiscHelpers}
import vyxal.impls.Elements

import scala.collection.mutable.ArrayBuffer

import StepRes.*

case class StackFrame(ctx: Context, tree: AST)

/** The result of stepping into an element */
enum StepRes:
  /** Call a function, and then run `next` to finish executing this element */
  case FnCall(fn: AST.FnDef, next: () => StepRes)
  case Structure(body: Iterable[AST], next: () => StepRes)

  /** The element finished executing in a single step */
  case Done

class Debugger(code: AST)(using rootCtx: Context):
  private val stackframes: ArrayBuffer[StackFrame] = ArrayBuffer(
    StackFrame(rootCtx, code)
  )

  /** A queue with the current steps to execute */
  private val currSteps = ArrayBuffer.empty[AST]

  /** The current context */
  private def ctx: Context = stackframes.last.ctx

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
        DebugImpls.impls.get(symbol) match
          case Some(impl) => impl()(using ctx)
          case None =>
            Elements.elements.get(symbol) match
              case Some(element) =>
                element.impl()(using ctx)
                Done
              case None => throw RuntimeException(s"No such element: $symbol")
      case AST.While(Some(cond), body, _) =>
        // todo this might not work with breaks and stuff
        def runWhile(): StepRes =
          if MiscHelpers.boolify(ctx.pop()) then
            Structure(List(body, cond), () => runWhile())
          else Done
        Structure(List(cond), () => runWhile())
      case _ => ???
end Debugger
