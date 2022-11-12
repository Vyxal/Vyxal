package vyxal

/** Helpers for function-related stuff */
object FuncHelpers {
  def vectorise(fn: VFun)(using ctx: Context) = {
    if (fn.arity == 1) {
      ctx.push(vectorise1(fn))
    } else if (fn.arity == 2) {
      ctx.push(vectorise2(fn))
    } else if (fn.arity == 3) {
      ???
    } else if (fn.arity == 4) {
      ???
    } else {
      throw UnsupportedOperationException(
        s"Vectorising functions of arity ${fn.arity} not possible"
      )
    }
  }

  private def vectorise1(fn: VFun)(using ctx: Context): VAny = {
    ctx.pop() match {
      case lst: VList =>
        lst.vmap { elem =>
          ctx.push(elem)
          vectorise1(fn)
        }
      case x =>
        ctx.push(x)
        Interpreter.executeFn(fn).getOrElse(ctx.settings.defaultValue)
    }
  }

  private def vectorise2(fn: VFun)(using ctx: Context): VAny = {
    val a = ctx.pop()
    val b = ctx.pop()

    (a, b) match {
      case (a: VList, b: VList) =>
        a.zipWith(b) { (x, y) =>
          ctx.push(x)
          ctx.push(y)
          vectorise2(fn)
        }
      case (a: VList, b) =>
        a.vmap { x =>
          ctx.push(x)
          ctx.push(b)
          vectorise2(fn)
        }
      case (a, b: VList) =>
        b.vmap { y =>
          ctx.push(a)
          ctx.push(y)
          vectorise2(fn)
        }
      case (a, b) =>
        ctx.push(a)
        ctx.push(b)
        Interpreter.executeFn(fn).getOrElse(ctx.settings.defaultValue)
    }
  }
}
