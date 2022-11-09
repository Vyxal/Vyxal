package vyxal

import spire.math.Number

type Overload = String

case class Element(
    symbol: String,
    name: String,
    arity: Option[Int],
    overloads: List[Overload],
    impl: DirectFn
)

object Elements {
  val elements: Map[String, Element] = Impls.elements.toMap

  private object Impls {
    val elements = collection.mutable.Map.empty[String, Element]

    /** Turn a monad into a function that operates on the stack */
    def monadToDirect(f: Monad): DirectFn = { () => ctx ?=>
      ctx.push(f(ctx.pop()))
    }

    /** Turn a dyad into a function that operates on the stack */
    def dyadToDirect(f: Dyad): DirectFn = { () => ctx ?=>
      val arg2, arg1 = ctx.pop()
      ctx.push(f(arg1, arg2))
    }

    /** Turn a triad into a function that operates on the stack */
    def triadToDirect(f: Triad): DirectFn = { () => ctx ?=>
      val arg3, arg2, arg1 = ctx.pop()
      ctx.push(f(arg1, arg2, arg3))
    }

    /** Turn a tetrad into a function that operates on the stack */
    def tetradToDirect(f: Tetrad): DirectFn = { () => ctx ?=>
      val arg4, arg3, arg2, arg1 = ctx.pop()
      ctx.push(f(arg1, arg2, arg3, arg4))
    }

    def addNilad(symbol: String, name: String, overloads: List[Overload])(
        impl: Context ?=> VAny
    ): Unit = {
      elements += symbol -> Element(
        symbol,
        name,
        Some(0),
        overloads,
        () => ctx ?=> ctx.push(impl(using ctx))
      )
    }

    def addMonad(
        symbol: String,
        name: String,
        overloads: List[Overload],
        vectorises: Boolean = false
    )(
        impl: Monad
    ): Monad = {
      elements += symbol -> Element(
        symbol,
        name,
        Some(1),
        overloads,
        monadToDirect(impl)
      )
      impl
    }

    def addMonadVect(symbol: String, name: String, overloads: List[Overload])(
        impl: SimpleMonad
    ) =
      addMonad(symbol, name, overloads, vectorises = true)(vect1(impl))

    def addDyad(
        symbol: String,
        name: String,
        overloads: List[Overload],
        vectorises: Boolean = false
    )(
        impl: Dyad
    ): Dyad = {
      elements += symbol -> Element(
        symbol,
        name,
        Some(2),
        overloads,
        dyadToDirect(impl)
      )
      impl
    }

    def addDyadVect(symbol: String, name: String, overloads: List[Overload])(
        impl: SimpleDyad
    ) = addDyad(symbol, name, overloads, vectorises = true)(vect2(impl))

    def addTriad(
        symbol: String,
        name: String,
        overloads: List[Overload],
        vectorises: Boolean = false
    )(
        impl: Triad
    ): Triad = {
      elements += symbol -> Element(
        symbol,
        name,
        Some(3),
        overloads,
        triadToDirect(impl)
      )
      impl
    }

    def addTriadVect(symbol: String, name: String, overloads: List[Overload])(
        impl: SimpleTriad
    ) =
      addTriad(symbol, name, overloads, vectorises = true)(vect3(impl))

    def addTetrad(
        symbol: String,
        name: String,
        overloads: List[Overload],
        vectorises: Boolean = false
    )(
        impl: Tetrad
    ): Tetrad = {
      elements += symbol -> Element(
        symbol,
        name,
        Some(4),
        overloads,
        tetradToDirect(impl)
      )
      impl
    }

    /** Add an element that works directly on the entire stack */
    def addDirect(symbol: String, name: String, overloads: List[Overload])(
        impl: Context ?=> Unit
    ): Unit =
      elements += symbol -> Element(symbol, name, None, overloads, () => impl)

    val swap = addDirect("$", "Swap", List("a, b -> b, a")) { ctx ?=>
      val b = ctx.pop()
      val a = ctx.pop()
      ctx.push(a)
      ctx.push(b)
    }

    val add: Dyad = addDyadVect(
      "+",
      "Add stuff",
      List(
        "a: num, b: num -> a + b",
        "a: num, b: str -> a + b",
        "a: str, b: num -> a + b",
        "a: str, b: str -> a + b"
      )
    ) {
      case (a: Number, b: Number) => a + b
      case (a: String, b: Number) => s"$a$b"
      case (a: Number, b: String) => s"$a$b"
      case (a: String, b: String) => s"$a$b"
      case _ => throw NotImplementedError("Can't add functions :(")
      // todo consider doing something like APL's forks
    }
  }
}
