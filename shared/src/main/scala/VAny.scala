package vyxal

import vyxal.impls.Element
import vyxal.Interpreter.executeFn

import spire.math.Number
import spire.math.Complex

// todo check if these names or this whole way of structuring need to be changed
type VAny = VAtom | VList
type VAtom = VVal | VFun
type VVal = VNum | String
type VNum = Complex[Number] | Number

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
case class VFun(
    impl: DirectFn,
    arity: Int,
    params: List[String],
    ctx: Context
) {

  /** Make a copy of this function with a different arity. */
  def withArity(newArity: Int): VFun = this.copy(arity = newArity)

  /** Call this function on the given arguments, using custom context variables.
    */
  def execute(
      contextVarM: VAny,
      contextVarN: VAny,
      args: Seq[VAny]
  )(using ctx: Context): VAny =
    Interpreter.executeFn(
      this,
      Some(contextVarM),
      Some(contextVarN),
      Some(args)
    )

  def apply(args: VAny*)(using ctx: Context): VAny =
    Interpreter.executeFn(this)
}

object VFun {
  def fromLambda(lam: AST.Lambda)(using origCtx: Context): VFun = {
    val AST.Lambda(arity, params, body) = lam
    VFun(
      () => ctx ?=> Interpreter.execute(body)(using ctx),
      arity,
      params,
      origCtx
    )
  }

  def fromElement(elem: Element)(using origCtx: Context): VFun = {
    val Element(symbol, name, _, arity, _, _, impl) = elem
    VFun(impl, arity.getOrElse(1), List.empty, origCtx)
  }
}

object VNum {

  /** To force an implicit conversion */
  def apply(n: VNum): VNum = n

  // todo implement properly
  /** Parse a number from a string */
  def from(s: String): VNum = {
    // Not as simple as it seems - can't just use Number.parse
    // because it doesn't handle hanging decimals (3. -> 3.5) nor
    // complex numbers (3ı4 -> 3+4i)

    val parts = s.split("ı") // Spits into real and imaginary parts
    val real = parts(0)
    val imag = parts.lift(1).getOrElse("0")

    val realNum = (if (real.last == '.') then real + "5" else real).toInt
    val imagNum = (if (imag.last == '.') then imag + "5" else imag).toInt

    if (imagNum == 0) then return realNum
    else return Complex(realNum, imagNum)
  }
}

extension (self: VAny)
  def ===(that: VAny): Boolean = {
    (self, that) match {
      case (a: VAtom, b: VAtom) => MiscHelpers.compare(a, b) == 0
      case (a: VList, b: VList) => a == b
      case _                    => false
    }
  }
