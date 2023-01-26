package vyxal.impls
// todo figure out a better solution than putting this in a different package
// it's in a different package so that ElementTests can access the impls without
// other classes being able to access them

import vyxal.*

import scala.io.StdIn
import scala.language.implicitConversions
import spire.algebra.*
import VNum.given

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

case class UnimplementedOverloadException(element: String, args: Seq[VAny])
    extends RuntimeException(
      s"$element not supported for input(s) ${args.mkString("(", ", ", ")")}"
    )

object Elements:
  val elements: Map[String, Element] = Impls.elements.toMap

  private[impls] object Impls:
    val elements = collection.mutable.Map.empty[String, Element]

    def addNilad(
        symbol: String,
        name: String,
        keywords: Seq[String],
        desc: String
    )(impl: Context ?=> VAny): Unit =
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        Some(0),
        false,
        List(s"-> $desc"),
        () => ctx ?=> ctx.push(impl(using ctx))
      )

    /** Add a monad that handles all `VAny`s (it doesn't take a
      * `PartialFunction`, hence "Full")
      */
    def addFull[F](
        helper: ImplHelpers[?, F],
        symbol: String,
        name: String,
        keywords: Seq[String],
        vectorises: Boolean,
        overloads: String*
    )(impl: F): F =
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        Some(helper.arity),
        vectorises,
        overloads,
        helper.toDirectFn(impl)
      )

      impl
    end addFull

    /** Define an unvectorised element that doesn't necessarily work on all
      * inputs
      *
      * If using this method, make sure to use `case` to define the function,
      * since it needs a `PartialFunction`. If it is possible to define it using
      * a normal function literal or it covers every single case, then try
      * [[addFull]] instead.
      */
    def addElem[P, F](
        helper: ImplHelpers[P, F],
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: String*
    )(impl: P): F =
      val full = helper.fill(symbol, impl)
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        Some(helper.arity),
        false,
        overloads,
        helper.toDirectFn(full)
      )
      full
    end addElem

    /** If using this method, make sure to use `case` to define the function,
      * since it needs a `PartialFunction`. If it is possible to define it using
      * a normal function literal, then try [[addFull]] instead.
      */
    def addVect[P, F](
        helper: ImplHelpers[P, F],
        symbol: String,
        name: String,
        keywords: Seq[String],
        overloads: String*
    )(impl: P): F =
      val vectorised = helper.vectorise(symbol)(impl)
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        Some(helper.arity),
        true,
        overloads,
        helper.toDirectFn(vectorised)
      )
      vectorised
    end addVect

    /** Add an element that works directly on the entire stack */
    def addDirect(
        symbol: String,
        name: String,
        keywords: Seq[String],
        arity: Option[Int],
        overloads: String*
    )(impl: Context ?=> Unit): Unit =
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        arity,
        false,
        overloads,
        () => impl
      )

    addFull(
      Dyad,
      "+",
      "Addition",
      List("add", "+", "plus"),
      true,
      "a: num, b: num -> a + b",
      "a: num, b: str -> a + b",
      "a: str, b: num -> a + b",
      "a: str, b: str -> a + b"
    )(MiscHelpers.add)

    val allTruthy = addElem(
      Monad,
      "A",
      "All Truthy | All() | Is Vowel?",
      List("all", "is-vowel?"),
      "a: str -> is (a) a vowel? vectorises for strings len > 1",
      "a: list -> is (a) all truthy?"
    ) {
      case a: VNum => ListHelpers.makeIterable(a).forall(MiscHelpers.boolify)
      case a: String if a.length == 1 => StringHelpers.isVowel(a.head)
      case a: String                  => VList(a.map(StringHelpers.isVowel)*)
      case a: VList                   => a.forall(MiscHelpers.boolify)
    }

    val concatenate = addElem(
      Dyad,
      "&",
      "Concatenate",
      List("concat", "&&", "append"),
      "a: any, b: any -> a ++ b"
    ) {
      case (a: VList, b: VList) => VList(a ++ b*)
      case (a: VList, b)        => VList(a :+ b*)
      case (a, b: VList)        => VList(a +: b*)
      case (a: VNum, b: VNum)   => VNum.from(f"$a$b")
      case (a, b)               => MiscHelpers.add(a, b)
    }

    addFull(
      Monad,
      "B",
      "Convert From Binary",
      List("from-binary", "bin->dec", "bin->decimal"),
      false,
      "a: num -> str(a) from binary",
      "a: str -> int(a, 2)",
      "a: lst -> int(a, 2), using list of digits"
    )(NumberHelpers.fromBinary)

    addFull(
      Monad,
      "b",
      "Convert To Binary",
      List("to-binary", "dec->bin", "decimal->bin"),
      true,
      "a: num -> convert a to binary",
      "a: str -> bin(ord(x) for x in a)"
    )(NumberHelpers.toBinary)

    val count = addElem(
      Dyad,
      "C",
      "Count",
      List("count"),
      "a: any, b: any -> count(b in a)"
    ) {
      case (a: VList, b) => a.count(_ === b)
      case (a, b: VList) => b.count(_ === a)
      case (a, b) =>
        StringHelpers.countString(a.toString, b.toString)
    }

    val divide = addVect(
      Dyad,
      "÷",
      "Divide | Split",
      List("divide", "div", "split"),
      "a: num, b: num -> a / b",
      "a: str, b: str -> Split a on the regex b"
    ) {
      case (a: VNum, b: VNum)     => a / b
      case (a: String, b: String) => VList(a.split(b)*)
    }

    val dup = addDirect(":", "Duplicate", List("dup"), None, "a -> a, a") {
      ctx ?=>
        val a = ctx.pop()
        ctx.push(a, a)
    }

    // todo extract to helper in MiscHelpers?
    val equals = addVect(
      Dyad,
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

    val exponentation = addVect(
      Dyad,
      "*",
      "Exponentation | Remove Nth Letter | Trim",
      List("exp", "**", "pow", "exponent", "remove-letter", "str-trim"),
      "a: num, b: num -> a ^ b",
      "a: str, b: num -> a with the bth letter removed",
      "a: num, b: str -> b with the ath letter removed",
      "a: str, b: str -> trim b from both sides of a"
    ) {
      case (a: VNum, b: VNum)   => a ** b
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
      None,
      "a ->"
    ) { ctx ?=> ctx.pop() }

    val execute = addElem(
      Monad,
      "Ė",
      "Execute lambda | Evaluate as Vyxal | Power with base 10",
      List("execute-lambda", "evaluate-as-vyxal", "power-base-10"),
      "a: fun -> Execute a",
      "a: str -> Evaluate a as Vyxal",
      "a: num -> 10 ** n"
    ) {
      case fn: VFun => Interpreter.executeFn(fn)
      case code: String =>
        Interpreter.execute(code)
        summon[Context].pop()
      case n: VNum => 10 ** n
    }

    val factorial = addVect(
      Monad,
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
      if ctx.globals.inputs.nonEmpty then ctx.globals.inputs.next()
      else
        val temp = StdIn.readLine()
        if temp.nonEmpty then Parser.parseInput(temp)
        else ctx.settings.defaultValue
    }

    val greaterThan = addVect(
      Dyad,
      ">",
      "Greater Than",
      List("gt", "greater", "greater-than", ">", "greater?", "bigger?"),
      "a: num, b: num -> a > b",
      "a: str, b: num -> a > str(b)",
      "a: num, b: str -> str(a) > b",
      "a: str, b: str -> a > b"
    ) { case (a: VVal, b: VVal) => MiscHelpers.compare(a, b) > 0 }

    val lessThan: Dyad = addVect(
      Dyad,
      "<",
      "Less Than",
      List("lt", "less", "less-than", "<", "less?", "smaller?"),
      "a: num, b: num -> a < b",
      "a: str, b: num -> a < str(b)",
      "a: num, b: str -> str(a) < b",
      "a: str, b: str -> a < b"
    ) { case (a: VVal, b: VVal) => MiscHelpers.compare(a, b) < 0 }

    val mapElement: Dyad = addElem(
      Dyad,
      "M",
      "Map Function | Mold Lists | Multiplicity",
      List("map", "mold", "multiplicity", "times-divide"),
      "a: any, b: fun -> a.map(b)",
      "a: fun, b: any -> b.map(a)",
      "a: lst, b: lst -> a molded to the shape of b",
      "a: num, b: num -> how many times b divides a"
    ) {
      case (a: VList, b: VList) => ListHelpers.mold(a, b)
      case (a: VNum, b: VNum)   => NumberHelpers.multiplicity(a, b)
      case (a, b: VFun) =>
        ListHelpers.map(b, ListHelpers.makeIterable(a, Some(true)))
      case (a: VFun, b) =>
        ListHelpers.map(a, ListHelpers.makeIterable(b, Some(true)))
    }

    val modulo: Dyad = addVect(
      Dyad,
      "%",
      "Modulo | String Formatting",
      List("mod", "modulo", "str-format", "format", "%"),
      "a: num, b: num -> a % b",
      "a: str, b: any -> a.format(b) (replace %s with b if scalar value or each item in b if vector)"
    ) {
      case (a: VNum, b: VNum) => a % b
      case (a: String, b)     => StringHelpers.formatString(a, b)
      case (a, b: String)     => StringHelpers.formatString(b, a)
    }

    val multiply = addFull(
      Dyad,
      "×",
      "Multiplication",
      List("mul", "multiply", "times", "str-repeat", "*", "ring-trans"),
      true,
      "a: num, b: num -> a * b",
      "a: num, b: str -> b repeated a times",
      "a: str, b: num -> a repeated b times",
      "a: str, b: str -> ring translate a according to b"
    )(MiscHelpers.multiply)

    val negate = addVect(
      Monad,
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
      case a: String => a.map(c => if c.isUpper then c.toLower else c.toUpper)
      case a: VFun   => MiscHelpers.firstNonNegative(a)
    }

    val ordChr =
      addVect(
        Monad,
        "O",
        "Ord/Chr",
        List("ord", "chr"),
        "a: str -> ord(a)",
        "a: num -> chr(a)"
      ) {
        case a: String =>
          if a.length == 1 then a.codePointAt(0)
          else VList(a.map(_.toInt: VNum)*)
        case a: VNum => a.toInt.toChar.toString
      }

    val pair =
      addFull(Dyad, ";", "Pair", List("pair"), false, "a, b -> [a, b]") {
        VList(_, _)
      }

    val print = addDirect(
      ",",
      "Print",
      List("print", "puts", "out"),
      None,
      "a -> printed to stdout"
    ) { ctx ?=>
      MiscHelpers.vyPrintln(ctx.pop())
    }

    val reduction = addElem(
      Dyad,
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
      "a: fun, b: any -> reduce iterable b by function a",
      "a: any, b: fun -> reduce iterable a by function b",
      "a: num, b: num -> the range [a, b)",
      "a: str, b: num|str -> does regex pattern b match haystack a?"
    ) {
      case (a: VNum, b: VNum) =>
        NumberHelpers.range(a, b - 1)
      case (a: String, b: String) => a.r.findFirstIn(b).isDefined
      case (a: String, b: VNum)   => a.r.findFirstIn(b.toString).isDefined
      case (a: VFun, b) =>
        MiscHelpers.reduce(b, a)
      case (a, b: VFun) =>
        MiscHelpers.reduce(a, b)
    }

    val subtraction = addVect(
      Dyad,
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

    val swap = addDirect("$", "Swap", List("swap"), None, "a, b -> b, a") {
      ctx ?=>
        val b, a = ctx.pop()
        ctx.push(b, a)
    }

    val triplicate =
      addDirect("D", "Triplicate", List("trip"), None, "a -> [a, a, a]") {
        ctx ?=>
          val a = ctx.pop()
          ctx.push(a, a, a)
      }

    val vectoriseAsElement = addDirect(
      "#v",
      "Vectorise (Element Form) [Internal Use]",
      List(),
      None,
      "*a, f -> f vectorised over however many arguments in a. It is recommended to use the modifier instead"
    ) { ctx ?=>
      // For sake of simplicity, error if not a function
      ctx.pop() match
        case f: VFun => FuncHelpers.vectorise(f)
        case _ =>
          throw IllegalArgumentException(
            "Vectorise: First argument should be a function"
          )
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
  end Impls
end Elements
