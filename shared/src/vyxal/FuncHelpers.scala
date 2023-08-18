package vyxal

/** Helpers for function-related stuff */
object FuncHelpers:
  /** Vectorise a function object */
  def vectorise(fn: VFun)(using ctx: Context): Unit =
    val res =
      fn.arity match
        case 1 =>
          ListHelpers.makeIterable(ctx.pop()).vmap { a =>
            Interpreter.executeFn(fn, args = List(a))
          }
        case 2 =>
          val b, a = ctx.pop()
          ListHelpers.makeIterable(a).vmap { a =>
            Interpreter.executeFn(fn, args = List(b, a))
          }
        case n =>
          VList.zipValues(ctx.pop(n)*) { args =>
            Interpreter.executeFn(fn, args = args)
          }

    ctx.push(res)
  end vectorise

  def reduceByElement(fn: VFun)(using ctx: Context): Unit =
    val iter = ctx.pop()
    ctx.push(ListHelpers.reduce(iter, fn))
end FuncHelpers
