package vyxal
import vyxal.conversions.{*, given}

type NullMonad = VAny => Context ?=> Unit
type NullDyad = (VAny, VAny) => Context ?=> Unit
type PartialNullMonad = Context ?=> PartialFunction[VAny, Unit]
type PartialNullDyad = Context ?=> PartialFunction[(VAny, VAny), Unit]

/** Meta-helper for creating the helpers to add element implementations
  * @tparam P
  *   The partial version of the function this helper group takes (`Context =>
  *   PartialFunction[(VAny, ...), VAny]`)
  * @tparam F
  *   The full version of the function (`(VAny, ...) => Context => VAny`)
  */
sealed abstract class NullImplHelpers[P, F](val arity: Int):
  /** Turn a completed implementation into a [[DirectFn]] */
  def toDirectFn(impl: F): DirectFn

  /** Turn a partial implementation into a complete one
    *
    * The returned function throws an [[vyxal.UnimplementedOverloadException]]
    * when passed an argument for which it's not defined
    */
  def fill(symbol: String)(impl: P): F

object NullMonad extends NullImplHelpers[PartialNullMonad, NullMonad](1):
  override def toDirectFn(impl: NullMonad) = () => ctx ?=> impl(ctx.pop())

  override def fill(name: String)(fn: PartialNullMonad) =
    arg =>
      if fn.isDefinedAt(arg) then fn(arg)
      else throw UnimplementedOverloadException(name, Seq(arg))

object NullDyad extends NullImplHelpers[PartialNullDyad, NullDyad](2):
  override def toDirectFn(impl: NullDyad): DirectFn =
    () =>
      ctx ?=>
        val arg2, arg1 = ctx.pop()
        impl(arg1, arg2)
  override def fill(name: String)(fn: PartialNullDyad): NullDyad =
    (a, b) =>
      val args = (a, b)
      if fn.isDefinedAt(args) then fn(args)
      else throw UnimplementedOverloadException(name, args.toList)

object NullHelpers:
  /** NullMonad and NullDyad will pop args automatically, use this instead of
    * ctx.pop()
    */
  def popArgs(m: VAny)(using ctx: Context): Unit = ctx.nop()
