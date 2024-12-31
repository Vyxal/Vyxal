package vyxal

import vyxal.conversions.given

/** Helpers for function-related stuff */
object FuncHelpers:

  def atSimpleLevels(fn: VFun)(using ctx: Context): VAny =
    def vecHelper(fn: VFun, iters: VAny*): VAny =
      if iters.length == 1 then
        val lst = ListHelpers.makeIterable(iters.head)
        if lst.forall(_.isInstanceOf[VList]) then
          lst.map(elem => vecHelper(fn, elem))
        else if lst.forall(_.isInstanceOf[VVal]) then
          Interpreter.executeFn(fn, args = Seq(lst))
        else
          lst.map { elem =>
            if elem.isInstanceOf[VList] then vecHelper(fn, elem)
            else Interpreter.executeFn(fn, args = Seq(elem))
          }
      else
        val zipped = iters.map(_.asInstanceOf[VList]).reduceLeft(_.vzip(_))
        zipped.map {
          case VList(items) => items.map {
              case VList(lst) =>
                if lst.forall(_.isInstanceOf[VVal]) then
                  Interpreter.executeFn(fn, args = Seq(lst))
                else VList(lst.map(elem => vecHelper(fn, elem)))
              case x => Interpreter.executeFn(fn, args = Seq(x))
            }
          case _ => ???
        }
    end vecHelper
    fn.arity match
      case 0 => ListHelpers.makeIterable(ctx.pop()).vmap { _ =>
          Interpreter.executeFn(fn)
        }
      case n => vecHelper(
          fn,
          ctx.pop(n).map(elem => ListHelpers.makeIterable(elem))*
        )
  end atSimpleLevels

  /** Vectorise a function object */
  def each(fn: VFun)(using ctx: Context): Unit =
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
  end each

  def deepVectorise(fn: VFun)(using ctx: Context): VAny =
    def vecHelper(fn: VFun, iters: VAny*): VAny =
      if iters.length == 1 then
        ListHelpers.makeIterable(iters.head, Some(true)).map {
          case VList(lst) => vecHelper(fn, lst)
          case x => Interpreter.executeFn(fn, x)
        }
      else if iters.forall(_.isInstanceOf[VVal]) then
        val res = iters.map(_.asInstanceOf[VVal])
        Interpreter.executeFn(fn, args = res)
      else
        val zipped = iters.map(_.asInstanceOf[VList]).reduceLeft(_.vzip(_))
        zipped.map {
          case VList(elem) =>
            if elem.forall(_.isInstanceOf[VVal]) then
              Interpreter.executeFn(fn, args = elem.map(_.asInstanceOf[VVal]))
            else vecHelper(fn, elem*)
          case _ => ???
        }

    val res = fn.arity match
      case 0 => VList(ListHelpers.makeIterable(ctx.pop()).vmap { _ =>
          Interpreter.executeFn(fn)
        })
      case n => vecHelper(
          fn,
          ctx.pop(n).map(elem => ListHelpers.makeIterable(elem, Some(true)))*
        )

    res
  end deepVectorise

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

  def reduceOverPairs(fn: VFun, iter: Seq[VAny])(using
      ctx: Context
  ): Seq[VAny] =
    ListHelpers.overlaps(iter, 2).map { slice =>
      slice match
        case Seq(left: VAny, right: VAny) =>
          Interpreter.executeFn(fn, left, right, Seq(left, right))
    }
end FuncHelpers
