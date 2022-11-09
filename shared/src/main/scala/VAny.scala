package vyxal

import spire.math.Number

// todo check if these names or this whole way of structuring need to be changed
type VAny = VAtom | VList
type VAtom = VVal | VFun
type VVal = Number | String

/** A function object (not a function definition)
  *
  * todo rethink the structure of this, maybe make a separate wrapper for arity
  * and ctx?
  */
enum VFun {
  /** A lambda just left on the stack */
  case Lam(lam: AST.Lambda, arity: Int, ctx: Context)

  /** A reference to a user-defined function */
  case FnRef(fnDef: AST.FnDef, arity: Int, ctx: Context)

  /** Make a copy of this function with a different arity.
    *
    * If the function is composed from two functions, only the arity of the
    * first function is changed.
    */
  def withArity(newArity: Int): VFun = this match {
    case Lam(lam, _, ctx)     => Lam(lam, newArity, ctx)
    case FnRef(fnDef, _, ctx) => FnRef(fnDef, newArity, ctx)
  }
}
