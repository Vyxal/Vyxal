package vyxal.debugger

import vyxal.*
import vyxal.debugger.StepRes.*

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/** The result of stepping into an element */
enum StepRes:
  /** The element finished executing in a single step */
  case Done

  /** Call a function */
  case FnCall(fn: VFun)

  case For(loop: AST.For)
  case While(loop: AST.While)
  case If(ifStmt: AST.IfStatement)
  case List(elems: Iterable[AST])

  /** Process `orig`, then run `fn` right after (in the same step) */
  case Then(orig: StepRes, fn: () => Unit)

  /** Execute `first`, then `second` */
  case Chain(first: StepRes, second: StepRes)

  /** Lazily get the next step to execute */
  case Lazy(get: () => StepRes)

  /** Execute an AST */
  case Exec(ast: AST)

  def map(fn: => Unit): StepRes = Then(this, () => fn)

  def flatMap(next: () => StepRes): StepRes = Chain(this, Lazy(next))
end StepRes

class Debugger(code: AST)(using rootCtx: Context):
  /** @param ctx
    *   The Context for this stack frame. May be the same as the outer stack
    *   frame's context.
    * @param ast
    *   The AST for the structure that caused the new stack frame
    * @param code
    *   The ASTs to be executed inside the structure. Note that this isn't
    *   simply an iterator over the body of the structure. For example, for an
    *   if statement, this would first provide the AST for the condition, then,
    *   depending on the top of the stack, choose whether to provide the AST for
    *   the truthy or falsy branch.
    */
  case class StackFrame(ctx: Context, ast: AST, step: StepRes)

  private val stackFrames: ArrayBuffer[StackFrame] = ArrayBuffer(
    StackFrame(rootCtx, code, Exec(code))
  )

  /** The current stack frame */
  private def frame: StackFrame = stackFrames.last

  /** The current context */
  private def ctx: Context = stackFrames.last.ctx

  def addBreakpoint(row: Int, col: Int): Unit = ???

  def stepInto(): Unit =
    if frame.step != StepRes.Done then nextStep(frame.step)

  def stepOver(): Unit = ???

  def stepOut(): Unit = ???

  def continue(): Unit = ???

  private def nextStep(step: StepRes): StepRes =
    step match
      case Done => Done
      case Exec(ast) => stepIntoAST(ast)
      case Then(orig, fn) =>
        if orig == StepRes.Done then
          fn()
          Done
        else Then(nextStep(orig), fn)
      case Lazy(get) => nextStep(get())
      case Chain(first, second) =>
        if first == StepRes.Done then second
        else Chain(nextStep(first), second)
      case _ => ???

  private def stepIntoAST(ast: AST): StepRes =
    ast match
      case AST.Number(value, _) =>
        ctx.push(value)
        Done
      case AST.Str(value, _) =>
        ctx.push(value)
        Done
      case AST.DictionaryString(value, _) =>
        ctx.push(StringHelpers.decompress(value))
        Done
      case AST.Lst(elems, _) => StepRes.List(elems)
      case AST.Command(symbol, _) =>
        // todo put the string "E" into a constant somewhere
        if symbol == "E" && ctx.peek.isInstanceOf[VFun] then
          val fn = ctx.peek.asInstanceOf[VFun]
          fn.originalAST match
            case None =>
              // No need to step in if the function wasn't user-defined
              ctx.push(Interpreter.executeFn(fn))
              Done
            case Some(origAST) => FnCall(fn)
        else
          DebugImpls.impls.get(symbol) match
            case Some(impl) => impl()(using ctx)
            case None =>
              Elements.elements.get(symbol) match
                case Some(element) =>
                  element.impl()(using ctx)
                  Done
                case None => throw RuntimeException(s"No such element: $symbol")
      case ifStmt: AST.IfStatement => StepRes.If(ifStmt)
      case loop: AST.While => StepRes.While(loop)
      case loop: AST.For => StepRes.For(loop)
      case AST.Group(elems, _, _) =>
        elems match
          case first :: rest =>
            elems.foldLeft(stepIntoAST(first)) { (acc, next) =>
              Chain(acc, Exec(next))
            }
          case Nil => Done
end Debugger
