package vyxal.impls
// todo figure out a better solution than putting this in a different package
// it's in a different package so that ElementTests can access the impls without
// other classes being able to access them

import scala.language.implicitConversions

import vyxal.*
import FuncHelpers.errorIfUndefined
import scala.io.StdIn

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

case class UnimplementedOverloadException(element: String, args: Any)
    extends RuntimeException(s"$element not supported for inputs $args")

object Elements {
  val elements: Map[String, Element] = Impls.elements.toMap

  private[impls] object Impls {
    val elements = collection.mutable.Map.empty[String, Element]

    def addNilad(
        symbol: String,
        name: String,
        keywords: Seq[String],
        desc: String
    )(impl: Context ?=> VAny): Unit = {
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        Some(0),
        false,
        List(s"-> $desc"),
        () => ctx ?=> ctx.push(impl(using ctx))
      )
    }

    def addMonadHelper(
        symbol: String,
        name: String,
        keywords: Seq[String],
        vectorises: Boolean,
        overloads: Seq[String],
        impl: Monad
    ): Monad = {
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        Some(1),
        vectorises,
        overloads,
        { () => ctx ?=>
          ctx.push(impl(ctx.pop()))
        }
      )
      impl
    }

    def addMonad(
        symbol: String,
        name: String,
        keywords: Seq[String],
        vectorises: Boolean,
        overloads: String*
    )(impl: PartialFunction[VAny, Context ?=> VAny]): Monad =
      addMonadHelper(
        symbol,
        name,
        keywords,
        vectorises,
        overloads,
        { arg =>
          // need to specially implement this because it doesn't take a tuple
          if (impl.isDefinedAt(arg)) impl(arg)
          else throw UnimplementedOverloadException(symbol, arg)
        }
      )

    def addMonadVect(
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: String*
    )(impl: PartialFunction[VAny, Context ?=> VAny]): Monad =
      addMonadHelper(
        symbol,
        name,
        keywords,
        true,
        overloads,
        vect1 { arg =>
          // need to specially implement this because it doesn't take a tuple
          if (impl.isDefinedAt(arg)) impl(arg)
          else throw UnimplementedOverloadException(symbol, arg)
        }
      )

    def addDyadHelper(
        symbol: String,
        name: String,
        keywords: Seq[String],
        vectorises: Boolean,
        overloads: Seq[String],
        impl: VyFn[2]
    ): Dyad = {
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        Some(2),
        vectorises,
        overloads,
        { () => ctx ?=>
          val arg2, arg1 = ctx.pop()
          val args = (arg1, arg2)
          ctx.push(impl(args))
        }
      )
      (a, b) => impl((a, b))
    }

    def addDyad(
        symbol: String,
        name: String,
        keywords: Seq[String],
        vectorises: Boolean,
        overloads: String*
    )(impl: PartialVyFn[2]): Dyad =
      addDyadHelper(
        symbol,
        name,
        keywords,
        vectorises,
        overloads,
        errorIfUndefined(symbol, impl)
      )

    def addDyadVect(
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: String*
    )(impl: PartialVyFn[2]) =
      addDyadHelper(
        symbol,
        name,
        keywords,
        true,
        overloads,
        vect2(errorIfUndefined(symbol, impl))
      )

    def addTriadHelper(
        symbol: String,
        name: String,
        keywords: Seq[String],
        vectorises: Boolean,
        overloads: Seq[String],
        impl: VyFn[3]
    ): Triad = {
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        Some(3),
        vectorises,
        overloads,
        { () => ctx ?=>
          val arg3, arg2, arg1 = ctx.pop()
          val args = (arg1, arg2, arg3)
          ctx.push(impl(args))
        }
      )
      (a, b, c) => impl((a, b, c))
    }

    def addTriad(
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: String*
    )(impl: PartialVyFn[3]): Triad = addTriadHelper(
      symbol,
      name,
      keywords,
      false,
      overloads,
      errorIfUndefined(name, impl)
    )

    def addTriadVect(
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: String*
    )(impl: PartialVyFn[3]) =
      addTriadHelper(
        symbol,
        name,
        keywords,
        true,
        overloads,
        vect3(errorIfUndefined(symbol, impl))
      )

    def addTetradHelper(
        symbol: String,
        name: String,
        keywords: Seq[String],
        vectorises: Boolean,
        overloads: Seq[String],
        impl: VyFn[4]
    ): Tetrad = {
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        Some(4),
        vectorises,
        overloads,
        { () => ctx ?=>
          val arg4, arg3, arg2, arg1 = ctx.pop()
          val args = (arg1, arg2, arg3, arg4)
          ctx.push(impl(args))
        }
      )
      (a, b, c, d) => impl((a, b, c, d))
    }

    def addTetrad(
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: String*
    )(impl: PartialVyFn[4]): Tetrad = addTetradHelper(
      symbol,
      name,
      keywords,
      false,
      overloads,
      errorIfUndefined(symbol, impl)
    )

    /** Add an element that works directly on the entire stack */
    def addDirect(
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: String*
    )(impl: Context ?=> Unit): Unit =
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        None,
        false,
        overloads,
        () => impl
      )

    val add = addDyad(
      "+",
      "Addition",
      List("add", "+", "plus"),
      true,
      "a: num, b: num -> a + b",
      "a: num, b: str -> a + b",
      "a: str, b: num -> a + b",
      "a: str, b: str -> a + b"
    ) {
      MiscHelpers.add(_, _)
    }

    val allTruthy = addMonad(
      "A",
      "All Truthy | All() | Is Vowel?",
      List("all", "is-vowel?"),
      false,
      "a: str -> is (a) a vowel? vectorises for strings len > 1",
      "a: list -> is (a) all truthy?"
    ) {
      case a: VNum =>
        if (ListHelpers.makeIterable(a).forall(MiscHelpers.boolify(_))) { 1 }
        else { 0 }
      case a: String => {
        var temp = VList()
        for (i <- a) {
          temp = VList(temp :+ StringHelpers.isVowel(i.toString)*)
        }
        temp
      }
      case a: VList =>
        if (a.forall(MiscHelpers.boolify(_))) { 1 }
        else { 0 }
    }

    val concatenate = addDyad(
      "&",
      "Concatenate",
      List("concat", "&&", "append"),
      false,
      "a: any, b: any -> a ++ b"
    ) {
      case (a: VList, b: VList) => VList(a ++ b*)
      case (a: VList, b: VAny)  => VList(a :+ b*)
      case (a: VAny, b: VList)  => VList(a +: b*)
      case (a: VNum, b: VNum)   => VNum.from(f"$a$b")
      case (a: VAny, b: VAny)   => add(a, b)
    }

    val convertFromBinary = addMonad(
      "B",
      "Convert From Binary",
      List("from-binary", "bin->dec", "bin->decimal"),
      false,
      "a: num -> str(a) from binary",
      "a: str -> int(a, 2)",
      "a: lst -> int(a, 2), using list of digits"
    ) { case a: VAny =>
      NumberHelpers.fromBinary(a)
    }

    val convertToBinary = addMonad(
      "b",
      "Convert To Binary",
      List("to-binary", "dec->bin", "decimal->bin"),
      true,
      "a: num -> convert a to binary",
      "a: str -> bin(chr(x) for x in a)"
    ) { case a: VAny =>
      NumberHelpers.toBinary(a)
    }

    val divide = addDyadVect(
      "÷",
      "Divide | Split",
      List("divide", "div", "split"),
      "a: num, b: num -> a / b",
      "a: str, b: str -> Split a on the regex b"
    ) {
      case (a: VNum, b: VNum)     => a / b
      case (a: String, b: String) => VList(a.split(b)*)
    }

    val dup = addDirect(":", "Duplicate", List("dup"), "a -> a, a") { ctx ?=>
      val a = ctx.pop()
      ctx.push(a)
      ctx.push(a)
    }

    val equals = addDyadVect(
      "=",
      "Equals",
      List("eq", "==", "equal", "same?", "equals?", "equal?"),
      "a: any, b: any -> a == b"
    ) {
      case (a: VNum, b: VNum)     => a == b
      case (a: VNum, b: String)   => a.toString == b
      case (a: String, b: VNum)   => a == b.toString
      case (a: String, b: String) => a == b
    }

    val exponentation = addDyadVect(
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
    }

    val discard = addDirect(
      "_",
      "Pop and Discard",
      List("pop", "discard"),
      "a ->"
    ) { ctx ?=> ctx.pop() }

    val factorial = addMonadVect(
      "!",
      "Factorial | To Uppercase",
      List("fact", "factorial", "to-upper", "upper", "uppercase", "!"),
      "a: num -> a!",
      "a: str -> a.toUpperCase()"
    ) {
      case a: VNum   => spire.math.fact(a.toLong)
      case a: String => a.toUpperCase()
    }

    val getContextVariableM = addNilad(
      "m",
      "Get Context Variable M",
      List("get-context-m", "context-m", "c-var-m", "ctx-m"),
      " -> context variable m"
    ) { ctx ?=> ctx.contextVarM }

    val getContextVariableN = addNilad(
      "n",
      "Get Context Variable N",
      List("get-context-n", "context-n", "c-var-n", "ctx-n"),
      " -> context variable n"
    ) { ctx ?=> ctx.contextVarN }

    val getInput = addNilad(
      "?",
      "Get Input",
      List("get-input", "input", "stdin", "readline"),
      " -> input"
    ) { ctx ?=>
      if (ctx.globals.inputs.nonEmpty) {
        ctx.globals.inputs.next()
      } else {
        val temp = StdIn.readLine()
        if (temp.nonEmpty) {
          VyxalParser.parseInput(temp)
        } else {
          ctx.settings.defaultValue
        }
      }
    }

    val greaterThan = addDyadVect(
      ">",
      "Greater Than",
      List("gt", "greater", "greater-than", ">", "greater?", "bigger?"),
      "a: num, b: num -> a > b",
      "a: str, b: num -> a > str(b)",
      "a: num, b: str -> str(a) > b",
      "a: str, b: str -> a > b"
    ) { MiscHelpers.compare(_, _) > 0 }

    val lessThan: Dyad = addDyadVect(
      "<",
      "Less Than",
      List("lt", "less", "less-than", "<", "less?", "smaller?"),
      "a: num, b: num -> a < b",
      "a: str, b: num -> a < str(b)",
      "a: num, b: str -> str(a) < b",
      "a: str, b: str -> a < b"
    ) { MiscHelpers.compare(_, _) < 0 }

    val mapElement: Dyad = addDyad(
      "M",
      "Map Function | Mold Lists | Multiplicity",
      List("map", "mold", "multiplicity", "times-divide"),
      false,
      "a: any, b: fun -> a.map(b)",
      "a: fun, b: any -> b.map(a)",
      "a: lst, b: lst -> a molded to the shape of b",
      "a: num, b: num -> how many times b divides a"
    ) {
      case (a: VList, b: VList) => ListHelpers.mold(a, b)
      case (a: VNum, b: VNum)   => NumberHelpers.multiplicity(a, b)
      case (a: VAny, b: VFun) =>
        ListHelpers.map(b, ListHelpers.makeIterable(a, Some(true)))
      case (a: VFun, b: VAny) =>
        ListHelpers.map(a, ListHelpers.makeIterable(b, Some(true)))
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
    }

    val multiply = addDyad(
      "×",
      "Multiplication",
      List("mul", "multiply", "times", "str-repeat", "*", "ring-trans"),
      true,
      "a: num, b: num -> a * b",
      "a: num, b: str -> b repeated a times",
      "a: str, b: num -> a repeated b times",
      "a: str, b: str -> ring translate a according to b"
    ) { MiscHelpers.multiply(_, _) }

    val negate = addMonadVect(
      "N",
      "Negation | Swap Case | First Non-Negative Integer Where Predicate is True",
      List(
        "neg",
        "negate",
        "swap-case",
        "caseswap",
        "first-non-negative",
        "first-nonneg",
        "first>-1"
      ),
      "a: num -> -a",
      "a: str -> a.swapcase()",
      "a: fun -> first non-negative integer where predicate a is true"
    ) {
      case a: VNum   => -a
      case a: String => a.map(c => if (c.isUpper) c.toLower else c.toUpper)
      case a: VFun   => MiscHelpers.firstNonNegative(a)
    }

    val ordChr =
      addMonadVect(
        "O",
        "Ord/Chr",
        List("ord", "chr"),
        "a: str -> ord(a)",
        "a: num -> chr(a)"
      ) {
        case a: String =>
          if (a.length == 1) a.codePointAt(0)
          else VList(a.map(_.toInt: VNum)*)
        case a: VNum => a.toInt.toChar.toString
      }

    val pair = addDyad(";", "Pair", List("pair"), false, "a, b -> [a, b]") {
      (a, b) => VList(a, b)
    }

    val print = addDirect(
      ",",
      "Print",
      List("print", "puts", "out"),
      "a -> printed to stdout"
    ) { ctx ?=>
      MiscHelpers.vyPrintln(ctx.pop())
    }

    val reduction = addDyad(
      "R",
      "Reduce by Function Object | Dyadic Range | Regex Match",
      List(
        "fun-reduce",
        "reduce",
        "fold-by",
        "range",
        "a->b",
        "regex-match?",
        "re-match?",
        "has-regex-match?"
      ),
      false,
      "a: fun, b: any -> reduce iterable b by function a",
      "a: any, b: fun -> reduce iterable a by function b",
      "a: num, b: num -> the range [a, b)",
      "a: str, b: num|str -> does regex pattern b match haystack a?"
    ) {
      case (a: VNum, b: VNum) =>
        NumberHelpers.range(a, b - 1)
      case (a: String, b: String) => a.r.findFirstIn(b).isDefined
      case (a: String, b: VNum)   => a.r.findFirstIn(b.toString).isDefined
      case (a: VFun, b: VAny) =>
        MiscHelpers.reduce(b, a)
      case (a: VAny, b: VFun) =>
        MiscHelpers.reduce(a, b)
    }

    val subtraction = addDyadVect(
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
