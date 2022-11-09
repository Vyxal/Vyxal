package vyxal

//These represent normal Scala functions, not functions operating on the stack
type Monad = VAny => Context ?=> VAny
type Dyad = (VAny, VAny) => Context ?=> VAny
type Triad = (VAny, VAny, VAny) => Context ?=> VAny
type Tetrad = (VAny, VAny, VAny, VAny) => Context ?=> VAny
//These are the same as Monad, Dyad, and Triad, except they don't work on lists
type SimpleMonad = VAtom => Context ?=> VAny
type SimpleDyad = (VAtom, VAtom) => Context ?=> VAny
type SimpleTriad = (VAtom, VAtom, VAtom) => Context ?=> VAny
type SimpleTetrad = (VAtom, VAtom, VAtom, VAtom) => Context ?=> VAny

/** A function that works directly on the stack */
case class DirectFn(fn: () => Context ?=> Unit, arity: Int) {
  def apply()(using Context) = fn()
}

extension (f: Monad)
  /** Turn the monad into a normal function of type `VAny => VAny`
    */
  def norm(using ctx: Context): VAny => VAny = f(_)(using ctx)

  def vectorised = {
    lazy val res: Monad = {
      case lhs: VAtom => f(lhs)
      case lst: VList => lst.vmap(res)
    }
    res
  }

extension (f: Dyad)
  /** Turn the dyad into a normal function of type `(VAny, VAny) => VAny`
    */
  def norm(using ctx: Context): (VAny, VAny) => VAny =
    f(_, _)(using ctx)
extension (f: Triad)
  /** Turn the triad into a normal function of type `(VAny, VAny, VAny) => VAny`
    */
  def norm(using ctx: Context): (VAny, VAny, VAny) => VAny =
    f(_, _, _)(using ctx)

/** Vectorise an unvectorised monad
  */
def vect1(f: SimpleMonad) = {
  lazy val res: Monad = {
    case lhs: VAtom => f(lhs)
    case lst: VList => lst.vmap(res)
  }
  res
}

/** Vectorise an unvectorised dyad
  */
def vect2(f: SimpleDyad): Dyad = {
  lazy val res: Dyad = {
    case (lhs: VAtom, rhs: VAtom) => f(lhs, rhs)
    case (lhs: VAtom, rhs: VList) => rhs.vmap(res(lhs, _))
    case (lhs: VList, rhs: VAtom) => lhs.vmap(res(_, rhs))
    case (lhs: VList, rhs: VList) => lhs.zipWith(rhs)(res(_, _))
  }
  res
}

/** Vectorise a triad
  */
def vect3(f: SimpleTriad): Triad = {
  lazy val res: Triad = {
    case (lhs: VAtom, rhs: VAtom, third: VAtom) => f(lhs, rhs, third)
    case (lhs: VAtom, rhs: VList, third: VAtom) => rhs.vmap(res(lhs, _, third))
    case (lhs: VList, rhs: VAtom, third: VAtom) => lhs.vmap(res(_, rhs, third))
    case (lhs: VList, rhs: VList, third: VAtom) =>
      lhs.zipWith(rhs)(res(_, _, third))
    case (lhs: VAtom, rhs: VAtom, third: VList) => third.vmap(res(lhs, rhs, _))
    case (lhs: VAtom, rhs: VList, third: VList) =>
      rhs.zipWith(third)(res(lhs, _, _))
    case (lhs: VList, rhs: VAtom, third: VList) =>
      lhs.zipWith(third)(res(_, rhs, _))
    case (lhs: VList, rhs: VList, third: VList) =>
      VList.zipMulti(lhs, rhs, third) { zipped =>
        (zipped: @unchecked) match {
          case VList(l, r, t) =>
            f(
              l.asInstanceOf[VAtom],
              r.asInstanceOf[VAtom],
              t.asInstanceOf[VAtom]
            )
        }
      }
  }
  res
}

// TODO: add vect4
