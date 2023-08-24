package vyxal.debugger

import vyxal.*
import vyxal.VNum.given

import scala.collection.mutable.ListBuffer

object DebugImpls:
  type DebugImpl = () => (Debugger, Context) ?=> Option[Step]

  val impls: Map[String, DebugImpl] = Map(
    debugDyad("Ḋ") {
      case (a: VList, b: VFun) => Some(DebugHelpers.dedupBy(a, b))
      case (a: VFun, b: VList) => Some(DebugHelpers.dedupBy(b, a))
    },
    debugMonad("Ḃ") {
      case fn: VFun =>
        val execStep = Debugger.fnCall(fn)
        if fn.arity == -1 then
          Some(StepSeq(List(execStep, Step.hidden { ctx ?=> ctx.pop() })))
        else Some(execStep)
      case code: String => Some(Debugger.execCode(code))
    },
    debugDyad("F") {
      case (a: VFun, b) =>
        Some(DebugHelpers.filter(ListHelpers.makeIterable(b, Some(true)), a))
      case (a, b: VFun) =>
        Some(DebugHelpers.filter(ListHelpers.makeIterable(a, Some(true)), b))
    },
    debugDyad("Ḟ") {
      case (a, pred: VFun) =>
        val buf = ListBuffer.empty[VAny]
        val steps = ListHelpers.makeIterable(a).zipWithIndex.map {
          case (elem, ind) => Debugger
              .fnCall(
                pred,
                ctxVarPrimary = elem,
                ctxVarSecondary = ind,
                args = List(elem, ind),
              )
              .map { res =>
                Some(Step.hidden { if res.toBool then buf += ind })
              }
        }
        Some(StepSeq(steps))
    },
  )

  /** Fills a PartialFunction monad implementation so that if the debug impl
    * can't handle the input, the normal implementation from Elements is used.
    */
  private def debugMonad(symbol: String)(
      impl: (Debugger, Context) ?=> PartialFunction[VAny, Option[Step]]
  ): (String, DebugImpl) =
    val processed: DebugImpl = () =>
      (dbg, ctx) ?=>
        val arg = ctx.pop()
        if impl.isDefinedAt(arg) then impl(arg)
        else
          Elements.elements(symbol).impl()
          None
    symbol -> processed

  private def debugDyad(symbol: String)(
      impl: (Debugger, Context) ?=> PartialFunction[(VAny, VAny), Option[Step]]
  ): (String, DebugImpl) =
    val processed: DebugImpl = () =>
      (dbg, ctx) ?=>
        val args = (ctx.pop(), ctx.pop())
        if impl.isDefinedAt(args) then impl(args)
        else
          Elements.elements(symbol).impl()
          None
    symbol -> processed

  private def debugTriad(symbol: String)(
      impl: (
          Debugger,
          Context,
      ) ?=> PartialFunction[(VAny, VAny, VAny), Option[Step]]
  ): (String, DebugImpl) =
    val processed: DebugImpl = () =>
      (dbg, ctx) ?=>
        val args = (ctx.pop(), ctx.pop(), ctx.pop())
        if impl.isDefinedAt(args) then impl(args)
        else
          Elements.elements(symbol).impl()
          None
    symbol -> processed

  private def debugTetrad(symbol: String)(
      impl: (
          Debugger,
          Context,
      ) ?=> PartialFunction[(VAny, VAny, VAny, VAny), Option[Step]]
  ): (String, DebugImpl) =
    val processed: DebugImpl = () =>
      (dbg, ctx) ?=>
        val args = (ctx.pop(), ctx.pop(), ctx.pop(), ctx.pop())
        if impl.isDefinedAt(args) then impl(args)
        else
          Elements.elements(symbol).impl()
          None
    symbol -> processed
end DebugImpls
