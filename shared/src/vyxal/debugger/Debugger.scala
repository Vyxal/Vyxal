package vyxal.lexer.debugger

import vyxal.*

import scala.collection.mutable.ArrayBuffer

case class StackFrame(ctx: Context, tree: AST)

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
