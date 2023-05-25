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
            Interpreter.executeFn(fn, args = List(a, b))
          }
        case 3 =>
          val c, b, a = ctx.pop()
          VList.zipValues(a, b, c) { args =>
            Interpreter.executeFn(fn, args = args)
          }
        case 4 =>
          val d, c, b, a = ctx.pop()
          VList.zipValues(a, b, c, d) { args =>
            Interpreter.executeFn(fn, args = args)
          }
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
