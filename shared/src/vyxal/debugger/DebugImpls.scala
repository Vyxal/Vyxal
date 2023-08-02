package vyxal.debugger

import vyxal.*
import vyxal.VNum.given

object DebugImpls:
  type DebugImpl = () => Context ?=> Option[Step]

  val impls: Map[String, DebugImpl] = Map(
  )

  /** Fills a PartialFunction monad implementation so that if the debug impl
    * can't handle the input, the normal implementation from Elements is used.
    */
  private def debugMonad(
      symbol: String,
      impl: Context ?=> PartialFunction[VAny, Option[Step]]
  ): (String, DebugImpl) =
    val processed: DebugImpl = () =>
      ctx ?=>
        val arg = ctx.pop()
        if impl.isDefinedAt(arg) then impl(arg)
        else
          Elements.elements(symbol).impl()
          None
    symbol -> processed

  private def debugDyad(
      symbol: String,
      impl: Context ?=> PartialFunction[(VAny, VAny), Option[Step]]
  ): (String, DebugImpl) =
    val processed: DebugImpl = () =>
      ctx ?=>
        val args = (ctx.pop(), ctx.pop())
        if impl.isDefinedAt(args) then impl(args)
        else
          Elements.elements(symbol).impl()
          None
    symbol -> processed

  private def debugTriad(
      symbol: String,
      impl: Context ?=> PartialFunction[(VAny, VAny, VAny), Option[Step]]
  ): (String, DebugImpl) =
    val processed: DebugImpl = () =>
      ctx ?=>
        val args = (ctx.pop(), ctx.pop(), ctx.pop())
        if impl.isDefinedAt(args) then impl(args)
        else
          Elements.elements(symbol).impl()
          None
    symbol -> processed

  private def debugTetrad(
      symbol: String,
      impl: Context ?=> PartialFunction[(VAny, VAny, VAny, VAny), Option[Step]]
  ): (String, DebugImpl) =
    val processed: DebugImpl = () =>
      ctx ?=>
        val args = (ctx.pop(), ctx.pop(), ctx.pop(), ctx.pop())
        if impl.isDefinedAt(args) then impl(args)
        else
          Elements.elements(symbol).impl()
          None
    symbol -> processed
end DebugImpls
