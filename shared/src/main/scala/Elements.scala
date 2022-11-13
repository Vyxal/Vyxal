package vyxal
import scala.language.implicitConversions
given Conversion[Boolean, VNum] with
  def apply(s: Boolean): VNum = if s then 1 else 0

/** Implementations for elements */
case class Element(
    symbol: String,
    name: String,
    arity: Option[Int],
    vectorises: Boolean,
    overloads: Seq[String],
    impl: DirectFn
)

object Elements {
  val elements: Map[String, Element] = Impls.elements.toMap

  private object Impls {
    val x = 2
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

    def addNilad(symbol: String, name: String, overloads: String*)(
        impl: Context ?=> VAny
    ): Unit = {
      elements += symbol -> Element(
        symbol,
        name,
        Some(0),
        false,
        overloads,
        () => ctx ?=> ctx.push(impl(using ctx))
      )
    }

    def addMonad(
        symbol: String,
        name: String,
        overloads: String*
    )(
        impl: Monad,
        vectorises: Boolean = false
    ): Monad = {
      elements += symbol -> Element(
        symbol,
        name,
        Some(1),
        vectorises,
        overloads,
        monadToDirect(impl)
      )
      impl
    }

    def addMonadVect(
        symbol: String,
        name: String,
        overloads: String*
    )(impl: SimpleMonad): Monad =
      addMonad(symbol, name, overloads*)(vect1(impl), vectorises = true)

    def addDyad(
        symbol: String,
        name: String,
        overloads: String*
    )(impl: Dyad, vectorises: Boolean = false): Dyad = {
      elements += symbol -> Element(
        symbol,
        name,
        Some(2),
        vectorises,
        overloads,
        dyadToDirect(impl)
      )
      impl
    }

    def addDyadVect(symbol: String, name: String, overloads: String*)(
        impl: SimpleDyad
    ) = addDyad(symbol, name, overloads*)(vect2(impl), vectorises = true)

    def addTriad(
        symbol: String,
        name: String,
        overloads: String*
    )(impl: Triad, vectorises: Boolean = false): Triad = {
      elements += symbol -> Element(
        symbol,
        name,
        Some(3),
        vectorises,
        overloads,
        triadToDirect(impl)
      )
      impl
    }

    def addTriadVect(symbol: String, name: String, overloads: List[String])(
        impl: SimpleTriad
    ) =
      addTriad(symbol, name, overloads*)(vect3(impl), vectorises = true)

    def addTetrad(
        symbol: String,
        name: String,
        overloads: List[String],
        vectorises: Boolean = false
    )(
        impl: Tetrad
    ): Tetrad = {
      elements += symbol -> Element(
        symbol,
        name,
        Some(4),
        vectorises,
        overloads,
        tetradToDirect(impl)
      )
      impl
    }

    /** Add an element that works directly on the entire stack */
    def addDirect(symbol: String, name: String, overloads: String*)(
        impl: Context ?=> Unit
    ): Unit =
      elements += symbol -> Element(
        symbol,
        name,
        None,
        false,
        overloads,
        () => impl
      )

    val add: Dyad = addDyadVect(
      "+",
      "Addition",
      "a: num, b: num -> a + b",
      "a: num, b: str -> a + b",
      "a: str, b: num -> a + b",
      "a: str, b: str -> a + b"
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
      "a: any, b: any -> a ++ b"
    ) {
      case (a: VList, b: VList) => VList(a ++ b*)
      case (a: VList, b: VAny)  => VList(a :+ b*)
      case (a: VAny, b: VList)  => VList(a +: b*)
      case (a: VNum, b: VNum)   => VNum.from(f"$a$b")
      case (a: VAny, b: VAny)   => add(a, b)
    }

    val dup = addDirect(":", "Duplicate", "a -> a, a") { ctx ?=>
      val a = ctx.pop()
      ctx.push(a)
      ctx.push(a)
    }

    val equals: Dyad = addDyadVect(
      "=",
      "Equals",
      "a: any, b: any -> a == b"
    ) {
      case (a: VNum, b: VNum)     => a == b
      case (a: String, b: VNum)   => a == b.toString
      case (a: String, b: String) => a == b
    }

    val exponentation: Dyad = addDyadVect(
      "*",
      "Exponentation | Remove Nth Letter | Trim",
      "a: num, b: num -> a ^ b",
      "a: str, b: num -> a with the bth letter removed",
      "a: num, b: str -> b with the ath letter removed",
      "a: str, b: str -> trim b from both sides of a"
    ) {
      case (a: VNum, b: VNum)   => a.pow(b)
      case (a: String, b: VNum) => StringHelpers.remove(a, b.toInt)
      case (a: VNum, b: String) => StringHelpers.remove(b, a.toInt)
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
      "a: num -> a!",
      "a: str -> a.toUpperCase()"
    ) {
      case a: VNum   => spire.math.fact(a.toLong)
      case a: String => a.toUpperCase()
      case _ =>
        throw NotImplementedError("Unsuported type for factorial")
    }

    val getContextVariable = addNilad(
      "n",
      "Get Context Variable",
      " -> context variable n"
    ) { ctx ?=> ctx.contextVar }

    val greaterThan: Dyad = addDyadVect(
      ">",
      "Greater Than",
      "a: num, b: num -> a > b",
      "a: str, b: num -> a > str(b)",
      "a: num, b: str -> str(a) > b",
      "a: str, b: str -> a > b"
    ) {
      case (a: VNum, b: VNum)     => a > b
      case (a: String, b: VNum)   => a > b.toString
      case (a: VNum, b: String)   => a.toString > b
      case (a: String, b: String) => a > b
      case _ =>
        throw NotImplementedError("Unsupported types for less than")
    }

    val lessThan: Dyad = addDyadVect(
      "<",
      "Less Than",
      "a: num, b: num -> a < b",
      "a: str, b: num -> a < str(b)",
      "a: num, b: str -> str(a) < b",
      "a: str, b: str -> a < b"
    ) {
      case (a: VNum, b: VNum)     => a < b
      case (a: String, b: VNum)   => a < b.toString
      case (a: VNum, b: String)   => a.toString < b
      case (a: String, b: String) => a < b
      case _ =>
        throw NotImplementedError("Unsupported types for less than")
    }

    val modulo: Dyad = addDyadVect(
      "%",
      "Modulo | String Formatting",
      "a: num, b: num -> a % b",
      "a: str, b: any -> a.format(b) (replace %s with b if scalar value or each item in b if vector)"
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
      "a: num, b: num -> a * b",
      "a: num, b: str -> b repeated a times",
      "a: str, b: num -> a repeated b times",
      "a: str, b: str -> ring translate a according to b"
    ) {
      case (a: VNum, b: VNum)     => a * b
      case (a: String, b: VNum)   => a.repeat(b.toInt)
      case (a: VNum, b: String)   => b.repeat(a.toInt)
      case (a: String, b: String) => StringHelpers.ringTranslate(a, b)
      case (a: VFun, b: VNum)     => a.withArity(b.toInt)
      case _ =>
        throw NotImplementedError("Unsupported types for multiplication")
    }

    val pair = addDirect(";", "Pair", "a, b -> [a, b]") { ctx ?=>
      val a = ctx.pop()
      val b = ctx.pop()
      ctx.push(VList(a, b))
    }

    val print = addDirect(",", "Print", "a -> printed to stdout") { ctx ?=>
      MiscHelpers.vyPrintln(ctx.pop())
    }

    val subtraction: Dyad = addDyadVect(
      "-",
      "Subtraction",
      "a: num, b: num -> a - b",
      "a: str, b: num -> a + b '-'s",
      "a: num, b: str -> a '-'s + b",
      "a: str, b: str -> a with b removed"
    ) {
      case (a: VNum, b: VNum) => a - b
      case (a: String, b: VNum) =>
        a + "-" * b.toInt
      case (a: VNum, b: String) => "-" * a.toInt + b
      case (a: String, b: String) =>
        a.replace(b, "")
      case _ =>
        throw NotImplementedError("Unsupported types for subtraction")
      // todo consider doing something like APL's forks
    }

    val swap = addDirect("$", "Swap", "a, b -> b, a") { ctx ?=>
      val b = ctx.pop()
      val a = ctx.pop()
      ctx.push(b)
      ctx.push(a)
    }

  }
}
