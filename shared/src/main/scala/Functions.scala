package vyxal

//These represent normal Scala functions, not functions operating on the stack
type Monad = VAny => Context ?=> VAny
type Dyad = (VAny, VAny) => Context ?=> VAny
type Triad = (VAny, VAny, VAny) => Context ?=> VAny
type Tetrad = (VAny, VAny, VAny, VAny) => Context ?=> VAny

/** Make a function taking `Arity` VAnys in a tuple */
type VyFn[Arity <: Int] = TupleOfSize[Arity] => Context ?=> VAny

/** Same as [[VyFn]] but may be undefined for some inputs */
type PartialVyFn[Arity <: Int] =
  PartialFunction[TupleOfSize[Arity], Context ?=> VAny]

/** Make a tuple of size `N` filled with [[VAny]]s */
private type TupleOfSize[N <: Int] <: Tuple = N match {
  case 0                        => EmptyTuple
  case compiletime.ops.int.S[n] => VAny *: TupleOfSize[n]
}

/** A function that operates directly on the stack */
type DirectFn = () => Context ?=> Unit

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
def vect1(f: Monad): Monad = {
  lazy val res: Monad = {
    case lhs: VAtom => f(lhs)
    case lst: VList => lst.vmap(res)
  }

  res
}

/** Vectorise an unvectorised dyad
  */
def vect2(f: VyFn[2]): VyFn[2] = {
  lazy val res: VyFn[2] = {
    case (lhs: VAtom, rhs: VAtom) => f(lhs, rhs)
    case (lhs: VAtom, rhs: VList) => rhs.vmap(res(lhs, _))
    case (lhs: VList, rhs: VAtom) => lhs.vmap(res(_, rhs))
    case (lhs: VList, rhs: VList) => lhs.zipWith(rhs)(res(_, _))
  }

  res
}

/** Vectorise a triad
  */
def vect3(f: VyFn[3]): VyFn[3] = {
  lazy val res: VyFn[3] = {
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
