package vyxal
import scala.language.implicitConversions
given Conversion[Boolean, VNum] with
  def apply(s: Boolean): VNum = if s then 1 else 0

/** Implementations for elements */
case class Element(
    symbol: String,
    name: String,
    keywords: Seq[String],
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

    def addNilad(
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: String*
    )(
        impl: Context ?=> VAny
    ): Unit = {
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        Some(0),
        false,
        overloads,
        () => ctx ?=> ctx.push(impl(using ctx))
      )
    }

    def addMonad(
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: String*
    )(
        impl: Monad,
        vectorises: Boolean = false
    ): Monad = {
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
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
        keywords: Seq[String],
        overloads: String*
    )(impl: SimpleMonad): Monad =
      addMonad(symbol, name, keywords, overloads*)(
        vect1(impl),
        vectorises = true
      )

    def addDyad(
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: String*
    )(impl: Dyad, vectorises: Boolean = false): Dyad = {
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        Some(2),
        vectorises,
        overloads,
        dyadToDirect(impl)
      )
      impl
    }

    def addDyadVect(
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: String*
    )(
        impl: SimpleDyad
    ) = addDyad(symbol, name, keywords, overloads*)(
      vect2(impl),
      vectorises = true
    )

    def addTriad(
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: String*
    )(impl: Triad, vectorises: Boolean = false): Triad = {
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        Some(3),
        vectorises,
        overloads,
        triadToDirect(impl)
      )
      impl
    }

    def addTriadVect(
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: List[String]
    )(
        impl: SimpleTriad
    ) =
      addTriad(symbol, name, keywords, overloads*)(
        vect3(impl),
        vectorises = true
      )

    def addTetrad(
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: List[String],
        vectorises: Boolean = false
    )(
        impl: Tetrad
    ): Tetrad = {
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        Some(4),
        vectorises,
        overloads,
        tetradToDirect(impl)
      )
      impl
    }

    /** Add an element that works directly on the entire stack */
    def addDirect(
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: String*
    )(
        impl: Context ?=> Unit
    ): Unit =
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        None,
        false,
        overloads,
        () => impl
      )

    val add: Dyad = addDyadVect(
      "+",
      "Addition",
      List("add", "+", "plus"),
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
      List("concat", "&&", "append"),
      "a: any, b: any -> a ++ b"
    ) {
      case (a: VList, b: VList) => VList(a ++ b*)
      case (a: VList, b: VAny)  => VList(a :+ b*)
      case (a: VAny, b: VList)  => VList(a +: b*)
      case (a: VNum, b: VNum)   => VNum.from(f"$a$b")
      case (a: VAny, b: VAny)   => add(a, b)
    }

    val divide: Dyad = addDyadVect(
      "÷",
      "Divide | Split",
      List("divide", "div", "str-split"),
      "a: num, b: num -> a / b",
      "a: str, b: str -> a.split(b)"
    ) {
      case (a: VNum, b: VNum)     => a / b
      case (a: String, b: String) => VList.fromSpecific(a.split(b))
      case (a, b) =>
        throw NotImplementedError(s"Modulo won't work on $a and $b")
    }

    val dup = addDirect(":", "Duplicate", List("dup"), "a -> a, a") { ctx ?=>
      val a = ctx.pop()
      ctx.push(a)
      ctx.push(a)
    }

    val equals: Dyad = addDyadVect(
      "=",
      "Equals",
      List("eq", "==", "equal", "same?"),
      "a: any, b: any -> a == b"
    ) {
      case (a: VNum, b: VNum)     => a == b
      case (a: VNum, b: String)   => a.toString == b
      case (a: String, b: VNum)   => a == b.toString
      case (a: String, b: String) => a == b
    }

    val exponentation: Dyad = addDyadVect(
      "*",
      "Exponentation | Remove Nth Letter | Trim",
      List("exp", "**", "pow", "exponent", "remove-letter", "str-trim"),
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
      List("fact", "factorial", "to-upper", "upper", "uppercase", "!"),
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
      List("get-context", "context", "c-var", "ctx"),
      " -> context variable n"
    ) { ctx ?=> ctx.contextVar }

    val greaterThan: Dyad = addDyadVect(
      ">",
      "Greater Than",
      List("gt", "greater", "greater-than", ">"),
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
      List("lt", "less", "less-than", "<"),
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
      List("mod", "modulo", "str-format", "format", "%"),
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
      List("mul", "multiply", "times", "str-repeat", "*", "ring-trans"),
      "a: num, b: num -> a * b",
      "a: num, b: str -> b repeated a times",
      "a: str, b: num -> a repeated b times",
      "a: str, b: str -> ring translate a according to b"
    ) {
      case (a: VNum, b: VNum)     => a * b
      case (a: String, b: VNum)   => a * b.toInt
      case (a: VNum, b: String)   => b * a.toInt
      case (a: String, b: String) => StringHelpers.ringTranslate(a, b)
      case (a: VFun, b: VNum)     => a.withArity(b.toInt)
      case _ =>
        throw NotImplementedError("Unsupported types for multiplication")
    }

    val ordChr =
      addMonadVect(
        "O",
        "Ord/Chr",
        List("ord", "chr"),
        "a: str -> ord(a)",
        "a: num -> chr(a)"
      ) {
        case (a: String) =>
          if (a.length == 1) a.codePointAt(0)
          else VList.fromSpecific(a.map(x => VNum(x.toInt)))
        case (a: VNum) => a.toInt.toChar.toString
      }

    val pair = addDirect(";", "Pair", List("pair"), "a, b -> [a, b]") { ctx ?=>
      val a = ctx.pop()
      val b = ctx.pop()
      ctx.push(VList(a, b))
    }

    val print = addDirect(
      ",",
      "Print",
      List("print", "puts", "out"),
      "a -> printed to stdout"
    ) { ctx ?=>
      MiscHelpers.vyPrintln(ctx.pop())
    }

    val subtraction: Dyad = addDyadVect(
      "-",
      "Subtraction",
      List(
        "sub",
        "subtract",
        "minus",
        "str-remove",
        "remove",
        "str-remove-all",
        "remove-all",
        "-"
      ),
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

    val swap = addDirect("$", "Swap", List("swap"), "a, b -> b, a") { ctx ?=>
      val b = ctx.pop()
      val a = ctx.pop()
      ctx.push(b)
      ctx.push(a)
    }

    // Constants

    addNilad("₀", "Ten", List("ten"), "10") { 10 }
    addNilad("₁", "Sixteen", List("sixteen"), "16") { 26 }
    addNilad("₂", "Twenty-six", List("twenty-six"), "26") { 26 }
    addNilad("₃", "Thirty-two", List("thirty-two"), "32") { 32 }
    addNilad("₄", "Sixty-four", List("sixty-four"), "64") { 64 }
    addNilad("₅", "One hundred", List("one-hundred"), "100") { 100 }
    addNilad(
      "₆",
      "One hundred twenty-eight",
      List("one-hundred-twenty-eight"),
      "128"
    ) { 128 }
    addNilad(
      "₇",
      "Two hundred fifty-six",
      List("two-hundred-fifty-six"),
      "256"
    ) { 256 }
    addNilad(
      "₈",
      "Alphabet",
      List("alphabet", "a-z"),
      "\"abcdefghijklmnopqrstuvwxyz\""
    ) {
      "abcdefghijklmnopqrstuvwxyz"
    }
    addNilad(
      "₉",
      "Empty array",
      List("empty-list", "nil-list", "new-list"),
      "[]"
    ) { VList.empty }

  }
}
