package vyxal

import spire.math.Number

// todo check if these names or this whole way of structuring need to be changed
type VAny = VAtom | VList
type VAtom = VVal | VFun
type VVal = VNum | String
type VNum = Number

/** A function object (not a function definition)
  *
  * @param impl
  *   The implementation of this function
  * @param arity
  *   The arity of this function (may have been changed)
  * @param params
  *   Parameter names
  * @param ctx
  *   The context in which this function was defined
  */
case class VFun(impl: DirectFn, arity: Int, params: List[String], ctx: Context) {
  /** Make a copy of this function with a different arity. */
  def withArity(newArity: Int): VFun = this.copy(arity = newArity)
}

object VFun {
  def fromLambda(lam: AST.Lambda)(using ctx: Context) = {
    val AST.Lambda(arity, params, body) = lam
    VFun(() => ctx ?=> Interpreter.execute(body), arity, params, ctx)
  }
}

object VNum {

  /** To force an implicit conversion */
  def apply(n: VNum): VNum = n

  // todo implement properly
  /** Parse a number from a string */
  def from(s: String): VNum = s.toInt
}
