package vyxal

import vyxal.impls.UnimplementedOverloadException

/** Helpers for function-related stuff */
object FuncHelpers:
  /** Vectorise a function object */
  def vectorise(fn: VFun)(using ctx: Context): Unit =
    val res =
      fn.arity match
        case 1 =>
          val a = ctx.pop()
          Monad.vectoriseNoFill { a =>
            Interpreter.executeFn(fn, args = Some(List(a)))
          }(a)
        case 2 =>
          val b, a = ctx.pop()
          Dyad.vectoriseNoFill { (a, b) =>
            Interpreter.executeFn(fn, args = Some(List(a, b)))
          }(a, b)
        case 3 =>
          val c, b, a = ctx.pop()
          Triad.vectoriseNoFill { (a, b, c) =>
            Interpreter.executeFn(fn, args = Some(List(a, b, c)))
          }(a, b, c)
        case 4 =>
          val d, c, b, a = ctx.pop()
          Tetrad.vectoriseNoFill { (a, b, c, d) =>
            Interpreter.executeFn(fn, args = Some(List(a, b, c, d)))
          }(a, b, c, d)
        case _ =>
          throw UnsupportedOperationException(
            s"Vectorising functions of arity ${fn.arity} not possible"
          )

    ctx.push(res)
  end vectorise

  def reduceByElement(fn: VFun)(using ctx: Context): Unit =
    val iter = ctx.pop()
    ctx.push(MiscHelpers.reduce(iter, fn))
end FuncHelpers
