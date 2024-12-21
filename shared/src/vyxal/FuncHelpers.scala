package vyxal

import vyxal.conversions.given

/** Helpers for function-related stuff */
object FuncHelpers:
  /** Vectorise a function object */
  def vectorise(fn: VFun)(using ctx: Context): Unit =
    val res = fn.arity match
      case 0 => ListHelpers.makeIterable(ctx.pop()).vmap { _ =>
          Interpreter.executeFn(fn)
        }
      case 1 => ListHelpers.makeIterable(ctx.pop()).vmap { a =>
          Interpreter.executeFn(fn, args = List(a))
        }
      case 2 =>
        val b, a = ctx.pop()
        ListHelpers.makeIterable(a).vmap { a =>
          Interpreter.executeFn(fn, args = List(b, a))
        }
      case n => ListHelpers.zipValues(ctx.pop(n)*) { args =>
          Interpreter.executeFn(fn, args = args)
        }

    ctx.push(res)
  end vectorise

  def reduceByElement(fn: VFun)(using ctx: Context): Unit =
    val iter = ctx.pop()
    ctx.push(ListHelpers.reduce(iter, fn))

  def recursion()(using ctx: Context): Unit =
    if ctx.recursion >= ctx.settings.recursionLimit then
      throw VyxalRecursionException()
    ctx.recursion += 1
    if ctx.globals.callStack.isEmpty then
      Interpreter.execute(ctx.globals.originalProgram)(using ctx)
    else
      ctx.push(
        Interpreter.executeFn(ctx.globals.callStack.top)(using
          ctx.makeChild()
        )
      )

  def reduceOverPairs(fn: VFun, iter: VList)(using ctx: Context): VList =
    val slices = ListHelpers.overlaps(iter, 2)
    val result = slices.map { slice =>
      slice match
        case Seq(left: VAny, right: VAny) =>
          Interpreter.executeFn(fn, left, right, Seq(left, right))
    }
    VList(result)
end FuncHelpers
