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
    )(impl: Context ?=> Unit): () => Context ?=> Unit =
      elements += symbol -> Element(
        symbol,
        name,
        keywords,
        arity,
        false,
        overloads,
        () => impl
      )
      () => impl
    end addDirect

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

    val append = addElem(
      Dyad,
      "&",
      "Append",
      List("append"),
      "a: any, b: any -> list(a) ++ [b]"
    ) { case (a, b) =>
      VList(ListHelpers.makeIterable(a) :+ b*)
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

    val toBinary = addVect(
      Monad,
      "b",
      "Convert To Binary",
      List("to-binary", "dec->bin", "decimal->bin"),
      "a: num -> convert a to binary",
      "a: str -> bin(ord(x) for x in a)"
    ) {
      case a: VNum => NumberHelpers.toBinary(a)
      case a: String =>
        VList(
          a.map(x => NumberHelpers.toBinary(StringHelpers.chrord(x.toString)))*
        )
    }

    val cookie = addDirect(
      "🍪",
      "Cookie",
      List("cookie"),
      None,
      "cookie."
    ) { ctx ?=>
      while true do MiscHelpers.vyPrintln("cookie")
    }

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

    val exec = addDirect(
      "Ė",
      "Execute lambda | Evaluate as Vyxal | Power with base 10",
      List("execute-lambda", "evaluate-as-vyxal", "power-base-10", "call", "@"),
      Some(1),
      "a: fun -> Execute a",
      "a: str -> Evaluate a as Vyxal",
      "a: num -> 10 ** n"
    ) { ctx ?=>
      ctx.push(execHelper(ctx.pop()))
    }

    def execHelper(value: VAny)(using ctx: Context): VAny =
      value match
        case code: String =>
          Interpreter.execute(code)
          ctx.pop()
        case n: VNum     => 10 ** n
        case list: VList => list.vmap(execHelper)
        case fn: VFun =>
          ctx.push(Interpreter.executeFn(fn))
          if fn.arity == -1 then
            ctx.pop() // Handle the extra value pushed by lambdas that operate on the stack
          ctx.pop()

    val execNotPop = addDirect(
      "Ḃ",
      "Execute lambda without popping | Evaluate as Vyxal without popping",
      List("peek-call"),
      Some(1),
      "a: fun -> Execute a without popping"
    ) { ctx ?=>
      (ctx.pop(): @unchecked) match
        case fn: VFun =>
          ctx.push(Interpreter.executeFn(fn, popArgs = false))
          if fn.arity == -1 then
            ctx.pop() // Handle the extra value pushed by lambdas that operate on the stack
        case code: String => Interpreter.execute(code)
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
        if b == "" then a
        else
          var res = a
          while res.startsWith(b) do res = res.drop(b.length)
          while res.endsWith(b) do res = res.dropRight(b.length)
          res
    }

    val discard = addDirect(
      "_",
      "Pop and Discard",
      List("pop", "discard"),
      None,
      "a ->"
    ) { ctx ?=> ctx.pop() }

    val factorial = addVect(
      Monad,
      "!",
      "Factorial | To Uppercase",
      List("fact", "factorial", "to-upper", "upper", "uppercase", "!"),
      "a: num -> a!",
      "a: str -> a.toUpperCase()"
    ) {
      case a @ VNum(r, i) =>
        if r.isWhole then spire.math.fact(spire.math.abs(a.toLong))
        else NumberHelpers.gamma(spire.math.abs(a.underlying.real) + 1)
      case a: String => a.toUpperCase()
    }

    val filterElement: Dyad = addElem(
      Dyad,
      "F",
      "Filter by Function | From Base",
      List("filter", "keep-by", "from-base", "10->b"),
      "a: fun, b: lst -> Filter b by truthy results of a",
      "a: lst, b: fun -> Filter a by truthy results of b",
      "a: num, b: num -> a in base b - list of digits",
      "a: num, b: str|lst -> a in base with alphabet b",
    ) {
      case (a: VFun, b) =>
        ListHelpers.filter(ListHelpers.makeIterable(b, Some(true)), a)
      case (a, b: VFun) =>
        ListHelpers.filter(ListHelpers.makeIterable(a, Some(true)), b)
    }

    val getContextVariableM = addNilad(
      "m",
      "Get Context Variable M",
      List(
        "get-context-m",
        "context-m",
        "c-var-m",
        "ctx-m",
        "ctx-secondary"
      ),
      " -> context variable m"
    ) { ctx ?=> ctx.ctxVarSecondary }

    val getContextVariableN = addNilad(
      "n",
      "Get Context Variable N",
      List(
        "get-context-n",
        "context-n",
        "c-var-n",
        "ctx-n",
        "ctx-primary"
      ),
      " -> context variable n"
    ) { ctx ?=> ctx.ctxVarPrimary }

    val getInput = addNilad(
      "?",
      "Get Input",
      List("get-input", "input", "stdin", "readline"),
      " -> input"
    ) { ctx ?=>
      if ctx.globals.inputs.nonEmpty then ctx.globals.inputs.next()
      else if ctx.settings.online then ctx.settings.defaultValue
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

    val index: Dyad = addElem(
      Dyad,
      "i",
      "Index | Collect Unique Application Values",
      List("index", "at", "item-at", "nth-item", "collect-unique"),
      "a: lst, b: num -> a[b]",
      "a: lst, b: lst -> a[_] for _ in b",
      "a: any, b: fun -> Apply b on a and collect unique values. Does include the initial value."
    ) {
      case (a: VNum, b: VList) => ListHelpers.index(b, a)
      case (a, b: VNum)  => ListHelpers.index(ListHelpers.makeIterable(a), b)
      case (a, b: VList) => ListHelpers.index(ListHelpers.makeIterable(a), b)
      case (a, b: VFun)  => MiscHelpers.collectUnique(b, a)
      case (a: VFun, b)  => MiscHelpers.collectUnique(a, b)
    }

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

    val modulo: Dyad = addElem(
      Dyad,
      "%",
      "Modulo | String Formatting",
      List("mod", "modulo", "str-format", "format", "%", "strfmt"),
      "a: num, b: num -> a % b",
      "a: str, b: any -> a.format(b) (replace %s with b if scalar value or each item in b if vector)"
    ) {
      case (_: VNum, VNum(0, _)) => 0
      case (a: VNum, b: VNum)    => a % b
      case (a: VList, b: VNum)   => a.vmap(Impls.modulo(_, b))
      case (a: VNum, b: VList)   => b.vmap(Impls.modulo(a, _))
      case (a: VList, b: VList)  => a.zipWith(b)(Impls.modulo)
      case (a: String, b: VList) => StringHelpers.formatString(a, b*)
      case (a: VList, b: String) => StringHelpers.formatString(b, a*)
      case (a: String, b)        => StringHelpers.formatString(a, b)
      case (a, b: String)        => StringHelpers.formatString(b, a)
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
      addElem(
        Monad,
        "O",
        "Ord/Chr",
        List("ord", "chr"),
        "a: str -> ord(a)",
        "a: num -> chr(a)"
      ) {
        case a: VNum   => StringHelpers.chrord(a)
        case a: String => StringHelpers.chrord(a)
        case a: VList =>
          val temp = a.map(StringHelpers.chrord)
          if temp.forall(_.isInstanceOf[String])
          then temp.mkString
          else VList(temp*)
      }

    val pair =
      addFull(Dyad, ";", "Pair", List("pair"), false, "a, b -> [a, b]") {
        VList(_, _)
      }

    val print = addDirect(
      ",",
      "Print",
      List("print", "puts", "out", "println"),
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
        "has-regex-match?",
        "fold"
      ),
      "a: fun, b: any -> reduce iterable b by function a",
      "a: any, b: fun -> reduce iterable a by function b",
      "a: num, b: num -> the range [a, b)",
      "a: str, b: num|str -> does regex pattern b match haystack a?"
    ) {
      case (a: VNum, b: VNum) =>
        NumberHelpers.range(a, b).dropRight(1)
      case (a: String, b: String) => b.r.findFirstIn(a).isDefined
      case (a: String, b: VNum)   => (b.toString).r.findFirstIn(a).isDefined
      case (a: VNum, b: String)   => b.r.findFirstIn(a.toString).isDefined
      case (a: VFun, b) =>
        MiscHelpers.reduce(b, a)
      case (a, b: VFun) =>
        MiscHelpers.reduce(a, b)
    }

    val sortByFunction: Dyad = addElem(
      Dyad,
      "ṡ",
      "Sort by Function Object | Reshape (APL Style)",
      List(
        "sort-by",
        "sortby",
        "sort-by-fun",
        "sortbyfun",
        "sort-fun",
        "sortfun"
      ),
      "a: fun, b: any -> sort iterable b by function a",
      "a: any, b: fun -> sort iterable a by function b"
    ) {
      case (a: VFun, b) =>
        ListHelpers.sortBy(ListHelpers.makeIterable(b, Some(true)), a)
      case (a, b: VFun) =>
        ListHelpers.sortBy(ListHelpers.makeIterable(a, Some(true)), b)
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
      "a: str, b: num -> a + b '-'s (or '-'s + a if b < 0)",
      "a: num, b: str -> a '-'s + b (or b + '-'s if a < 0)",
      "a: str, b: str -> a with b removed"
    ) {
      case (a: VNum, b: VNum) => a - b
      case (a: String, b: VNum) =>
        if b.toInt > 0 then a + "-" * b.toInt else "-" * b.toInt.abs + a
      case (a: VNum, b: String) =>
        if a.toInt > 0 then "-" * a.toInt + b else b + "-" * a.toInt.abs
      case (a: String, b: String) =>
        a.replace(b, "")
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

    val wrap = addDirect(
      "W",
      "Wrap",
      List("wrap"),
      None,
      "a, b, c, ..., -> [a, b, c, ...]"
    ) { ctx ?=>
      val args = ctx.pop(ctx.length)
      ctx.push(VList(args*))
    }

    val zeroSliceUntil = addElem(
      Dyad,
      "Θ",
      "Zero Slice Until",
      List(
        "0>b",
        "zero-slice",
        "zero-slice-until",
        "take",
        "slice-to",
        "lst-truncate",
        "first-n-items",
        "first"
      ),
      "a: lst, b: num -> [a[0], a[1], ..., a[b-1]]"
    ) { case (a, b: VNum) =>
      ListHelpers.makeIterable(a, Some(true)).take(b.toInt)
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
