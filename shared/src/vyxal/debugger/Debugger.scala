package vyxal.debugger

import vyxal.*
import vyxal.debugger.StepRes.*

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

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
  /** @param ctx
    *   The Context for this stack frame. May be the same as the outer stack
    *   frame's context.
    * @param ast
    *   The AST for the structure that caused the new stack frame
    * @param code
    *   The ASTs to be executed inside the structure
    */
  case class StackFrame(ctx: Context, ast: AST, code: Iterator[AST])

  private val stackFrames: ArrayBuffer[StackFrame] = ArrayBuffer(
    StackFrame(rootCtx, code, astIterator(code))
  )

  /** The current stack frame */
  private def frame: StackFrame = stackFrames.last

  /** The current context */
  private def ctx: Context = stackFrames.last.ctx

  def addBreakpoint(row: Int, col: Int): Unit = ???

  def stepInto(): Unit =
    if frame.code.hasNext then singleStep(frame.code.next())

  def stepOver(): Unit = ???

  def stepOut(): Unit = ???

  def continue(): Unit = ???

  private def singleStep(ast: AST): Unit =
    ast match
      case AST.Number(value, _) =>
        ctx.push(value)
      case AST.Str(value, _) =>
        ctx.push(value)
      case AST.DictionaryString(value, _) =>
        ctx.push(StringHelpers.decompress(value))
      case AST.Lst(elems, _) =>
        val list = ListBuffer.empty[VAny]
        val code = new Iterator[AST]:
          private val elemIter = elems.iterator
          private var runFirst = false
          override def hasNext: Boolean = elemIter.hasNext
          override def next(): AST =
            if this.runFirst then list += ctx.pop()
            else this.runFirst = true
            elemIter.next()
        stackFrames += StackFrame(ctx, ast, code)
      case AST.Command(symbol, _) =>
        // todo put the string "E" into a constant somewhere
        if symbol == "E" && ctx.peek.isInstanceOf[VFun] then
          val fn = ctx.peek.asInstanceOf[VFun]
          fn.originalAST match
            case None =>
              // No need to step in if the function wasn't user-defined
              ctx.push(Interpreter.executeFn(fn))
            case Some(origAST) =>
              val fnCtx = Context.makeFnCtx(
                fn.ctx,
                ctx,
                ctxVarPrimary = None,
                ctxVarSecondary = ???,
                ctxArgs = ???,
                vars = ???,
                inputs = ???,
                useStack = ???
              )
              stackFrames += StackFrame(fnCtx, origAST, origAST.body.iterator)
          end match
        else
          DebugImpls.impls.get(symbol) match
            case Some(impl) => impl()(using ctx)
            case None =>
              Elements.elements.get(symbol) match
                case Some(element) =>
                  element.impl()(using ctx)
                case None => throw RuntimeException(s"No such element: $symbol")
      case AST.IfStatement(conds, bodies, elseBody, _) =>
        val code = conds.lazyZip(bodies).foldRight(elseBody.iterator) {
          case ((cond, body), elseIt) =>
            astIterator(cond) ++ lazyIterator(
              if MiscHelpers.boolify(ctx.pop()) then astIterator(body)
              else elseIt
            )
        }
        stackFrames += StackFrame(ctx, ast, code)
      case AST.While(cond, body, _) =>
        val loopCtx = ctx.makeChild()
        loopCtx.ctxVarPrimary = true
        loopCtx.ctxVarSecondary = ctx.settings.rangeStart
        val code = cond match
          case None =>
            // Infinite loop
            Iterator.continually(astIterator(body)).flatten
          case Some(cond) =>
            def whileIter(): Iterator[AST] =
              astIterator(cond) ++ lazyIterator(
                if MiscHelpers.boolify(loopCtx.pop()) then
                  astIterator(body) ++ whileIter()
                else Iterator.empty
              )
            whileIter()
        stackFrames += StackFrame(loopCtx, ast, code)
      case AST.For(loopVar, body, _) =>
        ???

  /** Iterate over all ASTs inside the given ASTs. This really only exists
    * because of [[AST.Group]]
    */
  private def astIterator(ast: AST): Iterator[AST] =
    ast match
      case AST.Group(asts, _, _) => asts.flatMap(astIterator).iterator
      case _ => Iterator.single(ast)

  /** Lazily delegate an iterator */
  private def lazyIterator(iter: => Iterator[AST]): Iterator[AST] =
    new Iterator[AST]:
      private lazy val it = iter
      override def hasNext: Boolean = it.hasNext
      override def next(): AST = it.next()
end Debugger
