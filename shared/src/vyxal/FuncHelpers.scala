package vyxal

import vyxal.conversions.given
import scala.util.boundary, boundary.break

/** Helpers for function-related stuff */
object FuncHelpers:

  def atSimpleLevels(fn: VFun)(using ctx: Context): VAny =
    def monadHelper(iter: Seq[VAny]): VAny =
      if iter.forall(_.isInstanceOf[VVal]) then
        Interpreter.executeFn(fn, args = Seq(iter))
      else
        VList(iter.map {
          case VList(lst) => monadHelper(lst)
          case x => Interpreter.executeFn(fn, args = Seq(x))
        })

    def dyadHelper(left: VAny, right: VAny): VAny =
      println(s"left: $left, right: $right")
      (left, right) match
        case (VList(leftLst), VList(rightLst)) =>
          if leftLst.forall(_.isInstanceOf[VVal]) &&
            rightLst.forall(_.isInstanceOf[VVal])
          then Interpreter.executeFn(fn, args = Seq(rightLst, leftLst))
          else if leftLst.forall(_.isInstanceOf[VVal]) then
            VList(rightLst.map { rightElem =>
              dyadHelper(leftLst, rightElem)
            })
          else if rightLst.forall(_.isInstanceOf[VVal]) then
            VList(leftLst.map { leftElem =>
              dyadHelper(leftElem, rightLst)
            })
          else
            leftLst
              .zip(rightLst)
              .map((leftElem, rightElem) => dyadHelper(leftElem, rightElem))
        case (VList(leftLst), right) =>
          if leftLst.forall(_.isInstanceOf[VVal]) then
            Interpreter.executeFn(fn, args = Seq(right, leftLst))
          else
            VList(leftLst.map {
              case VList(lst) => dyadHelper(lst, right)
              case x => Interpreter.executeFn(fn, args = Seq(right, x))
            })
        case (left, VList(rightLst)) =>
          if rightLst.forall(_.isInstanceOf[VVal]) then
            Interpreter.executeFn(fn, args = Seq(rightLst, left))
          else
            VList(rightLst.map {
              case VList(lst) => dyadHelper(left, lst)
              case x => Interpreter.executeFn(fn, args = Seq(x, left))
            })
        case (left, right) => Interpreter.executeFn(fn, args = Seq(right, left))
      end match
    end dyadHelper

    fn.arity match
      case 1 =>
        val iter = ListHelpers.makeIterable(ctx.pop())
        monadHelper(iter)
      case 2 =>
        val right = ctx.pop()
        val left = ctx.pop()
        dyadHelper(left, right)
      case _ => throw VyxalRuntimeException(
          "Only functions with arity 1 or 2 can be vectorised at simple levels"
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
      case n => ListHelpers.zipValues(ctx.pop(n)) { args =>
          Interpreter.executeFn(fn, args = args)
        }

    ctx.push(res)
  end each

  def deepVectorise(fn: VFun)(using ctx: Context): VAny =
    def vecHelper(fn: VFun, iters: Seq[VAny]): VAny =
      if iters.length == 1 then
        ListHelpers.makeIterable(iters.head, Some(true)).map {
          case VList(lst) => vecHelper(fn, lst)
          case x => Interpreter.executeFn(fn, x)
        }
      else if iters.forall(_.isInstanceOf[VVal]) then
        val res = iters.map(_.asInstanceOf[VVal])
        Interpreter.executeFn(fn, args = res)
      else
        val zipped = ListHelpers.zipValues(iters) {
          case lst => VList(lst)
        }
        zipped.map {
          case VList(elem) =>
            if elem.forall(_.isInstanceOf[VVal]) then
              Interpreter.executeFn(fn, args = elem)
            else vecHelper(fn, elem)
          case _ => ???
        }

    val res = fn.arity match
      case 0 => VList(ListHelpers.makeIterable(ctx.pop()).vmap { _ =>
          Interpreter.executeFn(fn)
        })
      case n => vecHelper(
          fn,
          ctx.pop(n).map(elem => ListHelpers.makeIterable(elem, Some(true))),
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
        Interpreter.executeFn(ctx.globals.callStack.last)(using
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
  
  def collectByAnnotation(fns: VFun*)(lsts: VListOf[VType]*)(using ctx: Context): VFun =
    if (fns.map((f: Any) => f.asInstanceOf[VFun].arity).distinct.size != 1) then // Ensure arities are the same
      throw BadArgumentException("collectByAnnotation", (fns, lsts))
    else if (fns.head.arity == -1) then // Ensure no arity is -1
      throw BadArgumentException("collectByAnnotation", (fns, lsts))
    else if (fns.size != lsts.size || fns.size == 0 || lsts.size == 0) then // Ensure each annotation has 1-to-1 correspondence with a function and that there are actually annotations as well as functions
      throw BadArgumentException("collectByAnnotation", (fns, lsts))
    else if (lsts.map((l: Any) => l.asInstanceOf[VList].lst.size).distinct.size != 1) then // Ensure list lengths are the same
      throw BadArgumentException("collectByAnnotation", (fns, lsts))
    else if (!((fns lazyZip lsts).forall((x, y) => x.asInstanceOf[VFun].arity == y.asInstanceOf[VList].lst.size))) then // Ensure each list has the same length as its corresponding function's arity
      throw BadArgumentException("collectByAnnotation", (fns, lsts))
    else // Everything is A-OK
      def implFunc()(using implCtx: Context): Unit =
        val args = implCtx.peek(fns.head.arity)
        for (t, f) <- lsts zip fns do
          if (t.asInstanceOf[VList].lst zip args).forall((t: VAny, a: VAny) => t.asInstanceOf[VType].underlyingClass.isInstance(a)) then
            return f.impl()(using implCtx)
      VFun(implFunc, fns.head.arity, List.fill("<auto type composed function argument>")(fns.head.arity), ctx)

end FuncHelpers
