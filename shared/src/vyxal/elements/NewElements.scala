package vyxal.elements

import scala.language.implicitConversions

import vyxal.*
import vyxal.{Dyad, ImplHelpers, Monad, Tetrad, Triad}
import vyxal.toBool
import vyxal.Context.{copyCtx, pop, push}
import vyxal.Context.given
import vyxal.ListHelpers.makeIterable
import vyxal.StringHelpers.padLeft
import vyxal.VNum.given

import scala.io.StdIn
import scala.util.matching.Regex

given (using Context): Ordering[VAny] with
  override def compare(x: VAny, y: VAny): Int = MiscHelpers.compare(x, y)

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
    addPart("∧", Dyad, true) {
      case (b: VVal, a: VVal) => if !a.toBool then a else b
    },
    addPart("∨", Dyad, true) {
      case (b: VVal, a: VVal) => if a.toBool then a else b
    },
    addPart("¬", Monad, false) { a =>
      VNum(!a.toBool)
    },
    addPart("ʀ", Monad, true) {
      case a: VNum => NumberHelpers.range(0, a - a.signum)
      case a: String => a.toLowerCase()
    },
    addPart("ʁ", Monad, true) {
      case a: VNum =>
        val endpoint = a + 1
        NumberHelpers.range(0, endpoint - endpoint.signum)
      case a: String => a.toUpperCase()
    },
    addPart("ɾ", Monad, true) {
      case a: VNum => NumberHelpers.range(1, a)
      case a: String if a.length() == 1 => a.head.isLetter
      case a: String => VList.from(a.map(char => VNum(char.isLetter)))
    },
    addPart("‹", Monad, true) {
      case a: VNum => a - 1
      case a: String =>
        val length = a.length()
        val padLength = (8 - length % 8)
        "0".repeat(padLength) + a
    },
    addPart("›", Monad, true) {
      case a: VNum => a + 1
      case a: String => a.replace(" ", "0")
    },
    addPart("!", Monad, true) {
      case a @ VNum(r, i) =>
        if r.isWhole then spire.math.fact(spire.math.abs(a.toLong))
        else NumberHelpers.gamma(spire.math.abs(a.underlying.real) + 1)
      case a: String => StringHelpers.titlecase(a)
    },
    "$" ->
      direct(Dyad) {
        val b, a = pop()
        push(b, a)
      },
    "%" -> fullToImpl(Dyad, MiscHelpers.modulo),
    addPart("&", Dyad, false) {
      case (a, b) => VList.from(ListHelpers.makeIterable(a) :+ b)
    },
    addPart("*", Dyad, false) {
      case (a: VNum, b: VNum) => a ** b
    },
    "+" -> fullToImpl(Dyad, MiscHelpers.add),
    "," ->
      direct(Monad) {
        MiscHelpers.vyPrintln(pop())
        summon[Context].globals.printed = true
      },
    "-" -> fullToImpl(Dyad, MiscHelpers.subtract),
    ":" ->
      direct(Monad) {
        val a = pop()
        push(a, a)
      },
    ";" ->
      direct(Dyad) {
        val b = pop()
        val a = pop()
        push(VList(a, b))
      },
    addPart("<", Dyad, true) {
      case (a: VVal, b: VVal) => a < b
    },
    addPart("=", Dyad, true) {
      case (a: VNum, b: VNum) => a == b
      case (a: VNum, b: String) => a.toString == b
      case (a: String, b: VNum) => a == b.toString
      case (a: String, b: String) => a == b
    },
    addPart(">", Dyad, true) {
      case (a: VVal, b: VVal) => a > b
    },
    "?" ->
      niladify(ctx ?=>
        if ctx.globals.inputs.nonEmpty then ctx.globals.inputs.next()
        else if ctx.settings.online then ctx.settings.defaultValue
        else
          val temp = StdIn.readLine()
          if temp.nonEmpty then MiscHelpers.eval(temp)
          else ctx.settings.defaultValue
      ),
    addPart("@", Dyad, true) {
      case (a: VNum, b: VNum) => (a - b).vabs
      case (a: String, b: String) => StringHelpers.levenshtein(a, b)
      case (a: VPhysical, b: VFun) =>
        FuncHelpers.reduceOverPairs(b, makeIterable(a))
      case (a: VFun, b: VPhysical) =>
        FuncHelpers.reduceOverPairs(a, makeIterable(b))
    },
    addPart("A", Monad, false) {
      case a: VNum => ListHelpers.makeIterable(a).forall(_.toBool)
      case a: String if a.length == 1 => StringHelpers.isVowel(a.head)
      case a: String => VList.from(a.map(StringHelpers.isVowel))
      case a: VList => a.forall(_.toBool)
    },
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
