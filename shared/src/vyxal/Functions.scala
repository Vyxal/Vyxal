package vyxal

type GenericMonad[T] = VAny => Context ?=> T
type GenericDyad[T] = (VAny, VAny) => Context ?=> T
type GenericTriad[T] = (VAny, VAny, VAny) => Context ?=> T
type GenericTetrad[T] = (VAny, VAny, VAny, VAny) => Context ?=> T

//These represent normal Scala functions, not functions operating on the stack
type Monad = GenericMonad[VAny]
type Dyad = GenericDyad[VAny]
type Triad = GenericTriad[VAny]
type Tetrad = GenericTetrad[VAny]

/** Like a [[Monad]], but doesn't accept all inputs */
type PartialMonad = Context ?=> PartialFunction[VAny, VAny]
type PartialDyad = Context ?=> PartialFunction[(VAny, VAny), VAny]
type PartialTriad = Context ?=> PartialFunction[(VAny, VAny, VAny), VAny]
type PartialTetrad = Context ?=> PartialFunction[(VAny, VAny, VAny, VAny), VAny]

/** A function that operates directly on the stack */
type DirectFn = () => Context ?=> Unit

/** Meta-helper for creating the helpers to add element implementations
  * @tparam P
  *   The partial version of the function this helper group takes (`Context =>
  *   PartialFunction[(VAny, ...), VAny]`)
  * @tparam F
  *   A type constructor of the form (`[T] =>> (VAny, ...) => Context => T`)
  */
sealed abstract class ImplHelpers[P, F[_]](val arity: Int):
  /** The full version of the function (`(VAny, ...) => Context => VAny`) */
  type Full = F[VAny]

  /** Turn a completed implementation into a [[DirectFn]] */
  def toDirectFn(impl: Full): DirectFn

  /** Turn a partial implementation into a complete one
    *
    * The returned function throws an [[vyxal.UnimplementedOverloadException]]
    * when passed an argument for which it's not defined
    */
  def fill(symbol: String, impl: P): Full

  /** Vectorise a function. There's no need to call [[fill]] first */
  def vectorise(symbol: String)(impl: P): Full =
    vectoriseNoFill(fill(symbol, impl))

  /** For subclasses to implement. Vectorise an implementation that's already
    * been passed to [[fill]] and isn't a `PartialFunction` but doesn't actually
    * work on lists.
    */
  protected def vectoriseNoFill(impl: Full): Full
end ImplHelpers

object Monad extends ImplHelpers[PartialMonad, GenericMonad](1):
  override def toDirectFn(impl: Monad) =
    () => ctx ?=> ctx.push(impl(ctx.pop()))

  override def fill(name: String, fn: PartialMonad) = arg =>
    if fn.isDefinedAt(arg) then fn(arg)
    else throw UnimplementedOverloadException(name, Seq(arg))

  override def vectoriseNoFill(f: Monad) =
    lazy val res: Monad = {
      case lhs: VAtom => f(lhs)
      case lst: VList => lst.vmap(res)
    }

    res

object Dyad extends ImplHelpers[PartialDyad, GenericDyad](2):
  override def toDirectFn(impl: Dyad): DirectFn =
    () =>
      ctx ?=>
        val arg2, arg1 = ctx.pop()
        ctx.push(impl(arg1, arg2))

  override def fill(name: String, fn: PartialDyad): Dyad = (a, b) =>
    val args = (a, b)
    if fn.isDefinedAt(args) then fn(args)
    else throw UnimplementedOverloadException(name, args.toList)

  override def vectoriseNoFill(f: Dyad): Dyad =
    lazy val res: Dyad = {
      case (lhs: VAtom, rhs: VAtom) => f(lhs, rhs)
      case (lhs: VAtom, rhs: VList) => rhs.vmap(res(lhs, _))
      case (lhs: VList, rhs: VAtom) => lhs.vmap(res(_, rhs))
      case (lhs: VList, rhs: VList) => lhs.zipWith(rhs)(res(_, _))
    }

    res
end Dyad

object Triad extends ImplHelpers[PartialTriad, GenericTriad](3):
  override def toDirectFn(impl: Triad): DirectFn =
    () =>
      ctx ?=>
        val arg3, arg2, arg1 = ctx.pop()
        ctx.push(impl(arg1, arg2, arg3))

  override def fill(name: String, fn: PartialTriad): Triad = (a, b, c) =>
    val args = (a, b, c)
    if fn.isDefinedAt(args) then fn(args)
    else throw UnimplementedOverloadException(name, args.toList)

  override def vectoriseNoFill(f: Triad): Triad =
    lazy val res: Triad = {
      case (lhs: VAtom, rhs: VAtom, third: VAtom) => f(lhs, rhs, third)
      case (lhs: VAtom, rhs: VList, third: VAtom) =>
        rhs.vmap(res(lhs, _, third))
      case (lhs: VList, rhs: VAtom, third: VAtom) =>
        lhs.vmap(res(_, rhs, third))
      case (lhs: VList, rhs: VList, third: VAtom) =>
        lhs.zipWith(rhs)(res(_, _, third))
      case (lhs: VAtom, rhs: VAtom, third: VList) =>
        third.vmap(res(lhs, rhs, _))
      case (lhs: VAtom, rhs: VList, third: VList) =>
        rhs.zipWith(third)(res(lhs, _, _))
      case (lhs: VList, rhs: VAtom, third: VList) =>
        lhs.zipWith(third)(res(_, rhs, _))
      case (lhs: VList, rhs: VList, third: VList) =>
        VList.zipMulti(lhs, rhs, third) { case VList(l, r, t) => res(l, r, t) }
    }

    res
  end vectoriseNoFill
end Triad

object Tetrad extends ImplHelpers[PartialTetrad, GenericTetrad](4):
  override def toDirectFn(impl: Tetrad): DirectFn =
    () =>
      ctx ?=>
        val arg4, arg3, arg2, arg1 = ctx.pop()
        ctx.push(impl(arg1, arg2, arg3, arg4))

  override def fill(name: String, fn: PartialTetrad): Tetrad = (a, b, c, d) =>
    val args = (a, b, c, d)
    if fn.isDefinedAt(args) then fn(args)
    else throw UnimplementedOverloadException(name, args.toList)

  override def vectoriseNoFill(f: Tetrad) =
    lazy val res: Tetrad = {
      case (as: VList, bs: VList, cs: VList, ds: VList) =>
        VList.zipMulti(as, bs, cs, ds) { case VList(a, b, c, d) =>
          res(a, b, c, d)
        }
      case (a, bs: VList, cs: VList, ds: VList) =>
        VList.zipMulti(bs, cs, ds) { case VList(b, c, d) =>
          res(a, b, c, d)
        }

      case (as: VList, b, cs: VList, ds: VList) =>
        VList.zipMulti(as, cs, ds) { case VList(a, c, d) =>
          res(a, b, c, d)
        }
      case (as: VList, bs: VList, c, ds: VList) =>
        VList.zipMulti(as, bs, ds) { case VList(a, b, d) =>
          res(a, b, c, d)
        }
      case (as: VList, bs: VList, cs: VList, d) =>
        VList.zipMulti(as, bs, cs) { case VList(a, b, c) =>
          res(a, b, c, d)
        }
      case (a, b, cs: VList, ds: VList) =>
        cs.zipWith(ds) { (c, d) => res(a, b, c, d) }
      case (a, bs: VList, c, ds: VList) =>
        bs.zipWith(ds) { (b, d) => res(a, b, c, d) }
      case (a, bs: VList, cs: VList, d) =>
        bs.zipWith(cs) { (b, c) => res(a, b, c, d) }
      case (as: VList, b, c, ds: VList) =>
        as.zipWith(ds) { (a, d) => res(a, b, c, d) }
      case (as: VList, b, cs: VList, d) =>
        as.zipWith(cs) { (a, c) => res(a, b, c, d) }
      case (as: VList, bs: VList, c, d) =>
        as.zipWith(bs) { (a, b) => res(a, b, c, d) }
      case (a: VList, b, c, d) => a.vmap(res(_, b, c, d))
      case (a, b: VList, c, d) => b.vmap(res(a, _, c, d))
      case (a, b, c: VList, d) => c.vmap(res(a, b, _, d))
      case (a, b, c, d: VList) => d.vmap(res(a, b, c, _))
      case (a, b, c, d) => f(a, b, c, d)
    }

    res
  end vectoriseNoFill
end Tetrad

/** Make a partial function that only works on non-functions act kinda like an
  * APL fork when given functions as arguments.
  *
  *   - If the arguments are something `impl` can take, then it just calls
  *     `impl` on them
  *   - If the arguments are 2 functions f and g, then it applies `impl` to the
  *     result of `f` and the result of `g`
  *   - If the arguments are a value `a` and a function f, then it applies
  *     `impl` to `a` and the result of `f`
  *   - If the arguments are a function `f` and a value `b`, then it applies
  *     `impl` to the result of `f` and `b`
  */
def forkify(impl: PartialDyad): PartialDyad = {
  case (a, b) if impl.isDefinedAt((a, b)) => impl(a, b)
  case (f: VFun, g: VFun) =>
    VFun(
      { () => ctx ?=>
        // Don't pop args so that g can reuse them
        val a = Interpreter.executeFn(f, popArgs = false)
        val b = Interpreter.executeFn(g, popArgs = false)
        ctx.push(impl(a, b))
      },
      f.arity.max(g.arity),
      List.empty,
      summon[Context]
    )
  case (f: VFun, b) =>
    VFun(
      { () => ctx ?=>
        // Don't pop args so that g can reuse them
        val a = Interpreter.executeFn(f, popArgs = false)
        ctx.push(impl(a, b))
      },
      f.arity,
      List.empty,
      summon[Context]
    )
  case (a, f: VFun) =>
    VFun(
      { () => ctx ?=>
        // Don't pop args so that g can reuse them
        val b = Interpreter.executeFn(f, popArgs = false)
        ctx.push(impl(a, b))
      },
      f.arity,
      List.empty,
      summon[Context]
    )
}
