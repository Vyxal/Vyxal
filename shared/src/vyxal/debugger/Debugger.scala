package vyxal.debugger

import vyxal.{AST, Context}

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

  def addBreakpoint(row: Int, col: Int): Unit = ???

  def stepInto(): Unit = ???

  def stepOver(): Unit = ???

  def stepOut(): Unit = ???

  def continue(): Unit = ???
end Debugger
