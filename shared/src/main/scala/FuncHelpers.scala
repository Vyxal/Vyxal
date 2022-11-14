package vyxal

/** Helpers for function-related stuff */
object FuncHelpers {
  def vectorise(fn: VFun)(using ctx: Context): Unit = {
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
        Interpreter.executeFn(fn)
    }
  }

  private def vectorise2(fn: VFun)(using ctx: Context): VAny = {
    val b, a = ctx.pop()

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
        Interpreter.executeFn(fn)
    }
  }

  private def vectorise3(fn: VFun)(using ctx: Context): VAny = {
    val a = ctx.pop()
    val b = ctx.pop()
    val c = ctx.pop()

    (a, b, c) match {
      case (a: VList, b: VList, c: VList) =>
        VList.zipMulti(a, b, c) { case Seq(x, y, z) =>
          ctx.push(x)
          ctx.push(y)
          ctx.push(z)
          vectorise3(fn)
        }
      case (a: VList, b: VList, c) =>
        a.zipWith(b) { (x, y) =>
          ctx.push(x)
          ctx.push(y)
          ctx.push(c)
          vectorise3(fn)
        }
      case (a: VList, b, c: VList) =>
        a.zipWith(c) { (x, z) =>
          ctx.push(x)
          ctx.push(b)
          ctx.push(z)
          vectorise3(fn)
        }
      case (a, b: VList, c: VList) =>
        b.zipWith(c) { (y, z) =>
          ctx.push(a)
          ctx.push(y)
          ctx.push(z)
          vectorise3(fn)
        }
      case (a: VList, b, c) =>
        a.vmap { x =>
          ctx.push(x)
          ctx.push(b)
          ctx.push(c)
          vectorise3(fn)
        }
      case (a, b: VList, c) =>
        b.vmap { y =>
          ctx.push(a)
          ctx.push(y)
          ctx.push(c)
          vectorise3(fn)
        }
      case (a, b, c: VList) =>
        c.vmap { z =>
          ctx.push(a)
          ctx.push(b)
          ctx.push(z)
          vectorise3(fn)
        }
      case (a, b, c) =>
        ctx.push(a)
        ctx.push(b)
        ctx.push(c)
        Interpreter.executeFn(fn)
    }
  }
}
