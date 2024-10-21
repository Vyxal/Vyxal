package vyxal.elements

import vyxal.{Dyad, ImplHelpers, Monad, Tetrad, Triad}
import vyxal.Context
import vyxal.DirectFn
import vyxal.Interpreter
import vyxal.VAny
import vyxal.VFun

object NewElements:

  val elements: Map[String, DirectFn] = Map(
  )

  // Subject to being added as overloads onto things in elements
  val internalUseElements: Map[String, DirectFn] = Map(
    "#|fork" ->
      direct {
        val functionG = pop().asInstanceOf[VFun]
        val functionF = pop().asInstanceOf[VFun]

        functionF.ctx = copyCtx
        val resultF = Interpreter.executeFn(functionF)(using copyCtx)
        pop()
        val resultG = Interpreter.executeFn(functionG)(using summon[Context])
        push(resultG)
      }
  )

  private def niladify(value: VAny): DirectFn =
    () => (ctx: Context) ?=> ctx.push(value)

  /** Add an element that handles all `VAny`s (it doesn't take a
    * `PartialFunction`, hence "Full")
    */
  private def fullToImpl[F](arity: ImplHelpers[?, F], impl: F): DirectFn =
    arity.toDirectFn(impl)

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
  )(impl: P): (String, DirectFn) =
    symbol ->
      arity.toDirectFn(
        if vectorises then arity.vectorise(symbol)(impl)
        else arity.fill(symbol)(impl)
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
  )(impl: P): (String, DirectFn) =
    symbol -> arity.toDirectFn(arity.fill(symbol)(impl))

  private def direct(impl: Context ?=> Unit): DirectFn = () => impl

end NewElements
