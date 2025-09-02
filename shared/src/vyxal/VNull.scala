package vyxal
import vyxal.conversions.{*, given}

type NullMonad = VAny => Context ?=> VNull
type NullDyad = (VAny, VAny) => Context ?=> VNull
type PartialNullMonad = Context ?=> PartialFunction[VAny, VNull]
type PartialNullDyad = Context ?=> PartialFunction[(VAny, VAny), VNull]

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

  /** Vectorise a function. There's no need to call [[fill]] first */
  def vectorise(symbol: String)(impl: P): F

object NullMonad extends NullImplHelpers[PartialNullMonad, NullMonad](1):
  override def toDirectFn(impl: NullMonad) = () => ctx ?=> ctx.nop()

  override def fill(name: String)(fn: PartialNullMonad) =
    arg =>
      if fn.isDefinedAt(arg) then VNull(fn(arg))
      else throw UnimplementedOverloadException(name, Seq(arg))

  override def vectorise(name: String)(f: PartialNullMonad) =
    lazy val res: NullMonad = {
      case lhs if f.isDefinedAt(lhs) => f(lhs)
      case lst: VList => VNull.nullify(lst.vmap(res))
      case lhs => throw UnimplementedOverloadException(name, List(lhs))
    }
    res

object NullDyad extends NullImplHelpers[PartialNullDyad, NullDyad](2):
  override def toDirectFn(impl: NullDyad): DirectFn = () => ctx ?=> ctx.nop()

  override def fill(name: String)(fn: PartialNullDyad): NullDyad =
    (a, b) =>
      val args = (a, b)
      if fn.isDefinedAt(args) then fn(args)
      else throw UnimplementedOverloadException(name, args.toList)

  override def vectorise(name: String)(f: PartialNullDyad) =
    lazy val res: NullDyad = {
      case args if f.isDefinedAt(args) => f(args)
      case (lhs: VList, rhs: VList) =>
        VNull.nullify(lhs.zipWith(rhs)(res(_, _)))
      case (lhs, rhs: VList) => VNull.nullify(rhs.vmap(res(lhs, _)))
      case (lhs: VList, rhs) => VNull.nullify(lhs.vmap(res(_, rhs)))
      case args => throw UnimplementedOverloadException(name, args.toList)
    }

    res

end NullDyad
