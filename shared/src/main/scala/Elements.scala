package vyxal

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

    val add: Dyad = addDyadVect(
      "+",
      "Addition",
      List(
        "a: num, b: num -> a + b",
        "a: num, b: str -> a + b",
        "a: str, b: num -> a + b",
        "a: str, b: str -> a + b"
      )
    ) {
      case (a: VNum, b: VNum)     => a + b
      case (a: String, b: VNum)   => s"$a$b"
      case (a: VNum, b: String)   => s"$a$b"
      case (a: String, b: String) => s"$a$b"
      case _ =>
        throw NotImplementedError("Unsupported types for addition")
      // todo consider doing something like APL's forks
    }

    val concatenate: Dyad = addDyad(
      "&",
      "Concatenate",
      List(
        "a: any, b: any -> a ++ b"
      ),
      false
    ) {
      case (a: VList, b: VList) => a.append(b)
      case (a: VList, b: VAny)  => a.append(b)
      case (a: VAny, b: VList)  => VList(a).append(b)
      case (a: VNum, b: VNum)   => VNum.from(f"$a$b")
      case (a: VAny, b: VAny)   => add(a, b)
      case _ =>
        throw NotImplementedError("Unsupported types for concatenation")
    }

    val dup = addDirect(":", "Duplicate", List("a -> a, a")) { ctx ?=>
      val a = ctx.pop()
      ctx.push(a)
      ctx.push(a)
    }

    val exponentation: Dyad = addDyadVect(
      "*",
      "Exponentation | Remove Nth Letter | Trim",
      List(
        "a: num, b: num -> a ^ b",
        "a: str, b: num -> a with the bth letter removed",
        "a: num, b: str -> b with the ath letter removed",
        "a: str, b: str -> trim b from both sides of a"
      )
    ) {
      case (a: VNum, b: VNum) => a.pow(b)
      case (a: String, b: VNum) => {
        var i = b.toInt
        if (i < 0) { i = a.length + i }
        if (i >= a.length) { i = i % a.length }
        a.substring(0, i) + a.substring(i + 1)
      }
      case (a: VNum, b: String) => exponentation(b, a)
      case (a: String, b: String) =>
        a.dropWhile(_.toString == b)
          .reverse
          .dropWhile(_.toString == b)
          .reverse // https://stackoverflow.com/a/17995686/9363594
      case _ =>
        throw NotImplementedError("Unsupported types for exponentation")
    }

    val factorial: Monad = addMonadVect(
      "!",
      "Factorial | To Uppercase",
      List("a: num -> a!", "a: str -> a.toUpperCase()")
    ) {
      case a: VNum   => spire.math.fact(a.toLong)
      case a: String => a.toUpperCase()
      case _ =>
        throw NotImplementedError("Unsuported type for factorial")
    }

    val getContextVariable = addNilad(
      "n",
      "Get Context Variable",
      List("_ -> context variable n")
    ) { ctx ?=>
      ctx.contextVar.top
    }

    val modulo: Dyad = addDyadVect(
      "%",
      "Modulo | String Formatting",
      List(
        "a: num, b: num -> a % b",
        "a: str, b: any -> a.format(b) (replace %s with b if scalar value or each item in b if vector)"
      )
    ) {
      case (a: VNum, b: VNum)   => a.tmod(b)
      case (a: String, b: VAny) => StringHelpers.formatString(a, b)
      case (a: VAny, b: String) => StringHelpers.formatString(b, a)
      case (a, b) =>
        throw NotImplementedError(s"Modulo won't work on $a and $b")
    }

    val multiply: Dyad = addDyadVect(
      "×",
      "Multiplication",
      List(
        "a: num, b: num -> a * b",
        "a: num, b: str -> b repeated a times",
        "a: str, b: num -> a repeated b times",
        "a: str, b: str -> ring translate a according to b"
      )
    ) {
      case (a: VNum, b: VNum)     => a * b
      case (a: String, b: VNum)   => a.repeat(b.toInt)
      case (a: VNum, b: String)   => b.repeat(a.toInt)
      case (a: String, b: String) => StringHelpers.ringTranslate(a, b)
      case (a: VFun, b: VNum)     => a.withArity(b.toInt)
      case _ =>
        throw NotImplementedError("Unsupported types for multiplication")
    }

    val print = addDirect(
      ",",
      "Print",
      List("a -> print a")
    ) { ctx ?=>
      val a = ctx.pop()
      println(a)
    }

    val subtraction: Dyad = addDyadVect(
      "-",
      "Subtraction",
      List(
        "a: num, b: num -> a - b"
      )
    ) {
      case (a: VNum, b: VNum) => a - b
      case _ =>
        throw NotImplementedError("Unsupported types for subtraction")
      // todo consider doing something like APL's forks
    }

    val swap = addDirect("$", "Swap", List("a, b -> b, a")) { ctx ?=>
      val b = ctx.pop()
      val a = ctx.pop()
      ctx.push(b)
      ctx.push(a)
    }
  }
}
