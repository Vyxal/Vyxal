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
          VList(ListHelpers.makeIterable(a).map { a =>
            Interpreter.executeFn(fn, args = Some(List(a)))
          }*)
        case 2 =>
          val b, a = ctx.pop()
          VList(ListHelpers.makeIterable(a).zipWithIndex.map { (a, i) =>
            Interpreter.executeFn(
              fn,
              args = Some(List(a, b))
            )
          }*)
        case 3 =>
          val c, b, a = ctx.pop()
          val temp = ListHelpers.makeIterable(b).zipWithIndex.map { (b, i) =>
            VList(b, ListHelpers.makeIterable(c)(i))
          }
          VList(
            ListHelpers
              .makeIterable(a)
              .zipWithIndex
              .map((a, i) =>
                Interpreter
                  .executeFn(fn, args = Some(List(a, temp(i)(0), temp(i)(1))))
              )*
          )
        case 4 =>
          val d, c, b, a = ctx.pop()
          val temp = ListHelpers.makeIterable(b).zipWithIndex.map { (b, i) =>
            VList(
              b,
              ListHelpers.makeIterable(c)(i),
              ListHelpers.makeIterable(d)(i)
            )
          }
          VList(
            ListHelpers
              .makeIterable(a)
              .zipWithIndex
              .map((a, i) =>
                Interpreter.executeFn(
                  fn,
                  args = Some(List(a, temp(i)(0), temp(i)(1), temp(i)(2)))
                )
              )*
          )
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
