package vyxal.elements

import vyxal.{Dyad, ImplHelpers, Monad, Tetrad, Triad}
import vyxal.Context
import vyxal.Context.{copyCtx, pop, push}
import vyxal.DirectFn
import vyxal.Interpreter
import vyxal.ListHelpers
import vyxal.MiscHelpers
import vyxal.StringHelpers
import vyxal.VAny
import vyxal.VFun
import vyxal.VList
import vyxal.VNum
import vyxal.VNum.given

import scala.util.matching.Regex

object NewElements:
  case class Element(
      arity: Int,
      impl: DirectFn,
  )

  val elements: Map[String, Element] = Map(
    "⊞" ->
      direct(Monad) {
        val iterable = ListHelpers.makeIterable(pop())
        val uniq = iterable.distinct
        val counts = uniq.map(item => VNum(iterable.count(_ == item)))
        push(VList.from(counts))
      },
    addPart("÷", Dyad, true) {
      case (a: VNum, b: VNum) => a / b
      case (a: String, b: VNum) => StringHelpers.intoNPieces(a, b)
      case (a: VNum, b: String) => StringHelpers.intoNPieces(b, a)
      case (a: String, b: String) => StringHelpers.split(a, Regex.quote(b))
    },
    "×" -> fullToImpl(Dyad, MiscHelpers.multiply),
  )

  // Subject to being added as overloads onto things in elements
  val internalUseElements: Map[String, Element] = Map(
    "#|correspond" ->
      direct(Dyad) {
        val functionG = pop().asInstanceOf[VFun]
        val functionF = pop().asInstanceOf[VFun]

        val result = Interpreter.executeFn(functionG)
        val otherResult = Interpreter.executeFn(functionF)
        push(otherResult, result)
      }
  )

  private def niladify(value: VAny): Element =
    Element(0, () => (ctx: Context) ?=> ctx.push(value))

  private def niladify(function: Context ?=> VAny): Element =
    Element(0, () => (ctx: Context) ?=> ctx.push(function))

  /** Add an element that handles all `VAny`s (it doesn't take a
    * `PartialFunction`, hence "Full")
    */
  private def fullToImpl[F](arity: ImplHelpers[?, F], impl: F): Element =
    Element(arity.arity, arity.toDirectFn(impl))

  /** Define an element that doesn't necessarily work on all inputs
    *
    * If using this method, make sure to use `case` to define the function,
    * since it needs a `PartialFunction`. If it is possible to define it using a
    * normal function literal or it covers every single case, then try
    * [[addFull]] instead.
    */
  private def addPart[P, F](
      symbol: String,
      arity: ImplHelpers[P, F],
      vectorises: Boolean,
  )(impl: P): (String, Element) =
    symbol ->
      Element(
        arity.arity,
        arity.toDirectFn(
          if vectorises then arity.vectorise(symbol)(impl)
          else arity.fill(symbol)(impl)
        ),
      )

  /** Define an element that doesn't necessarily work on all inputs. It may
    * vectorise on some inputs but not others.
    *
    * Note that this helper assumes you've already done the work of vectorising
    * the element, i.e., unlike [[addPart]], vectorisation will not be done for
    * you.
    *
    * If using this method, make sure to use `case` to define the function,
    * since it needs a `PartialFunction`. If it is possible to define it using a
    * normal function literal or it covers every single case, then try
    * [[addFull]] instead.
    */
  private def addPartialVect[P, F](
      arity: ImplHelpers[P, F],
      symbol: String,
  )(impl: P): (String, Element) =
    symbol -> Element(arity.arity, arity.toDirectFn(arity.fill(symbol)(impl)))

  private def direct[P, F](arity: ImplHelpers[P, F])(
      impl: Context ?=> Unit
  ): Element = Element(arity.arity, () => impl)

  private def direct(impl: Context ?=> Unit): Element = Element(0, () => impl)

end NewElements
