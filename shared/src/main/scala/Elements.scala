package vyxal.impls
// todo figure out a better solution than putting this in a different package
// it's in a different package so that ElementTests can access the impls without
// other classes being able to access them

import scala.language.implicitConversions

import vyxal.*
import vyxal.ListHelpers.makeIterable

import scala.io.StdIn

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

object Elements:
  val elements: Map[String, Element] = Impls.elements.toMap
  
  /** Find the symbol for a keyword in literate mode, if it exists */
  def symbolFor(keyword: String): Option[String] =
    Elements.elements.values.find(_.keywords.contains(keyword)).map(_.symbol)

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

    val absoluteValue = addVect(
      Monad,
      "Ȧ",
      "Absolute Value | Keep Alphabet Characters",
      List("abs", "absolute-value", "keep-alphabet"),
      "a: num -> |a|",
      "a: str -> keep alphabet characters of a"
    ) {
      case a: VNum   => a.vabs
      case a: String => a.filter(_.isLetter)
    }

    val allTruthy = addElem(
      Monad,
      "A",
      "All Truthy | All() | Is Vowel?",
      List("all", "is-vowel?", "vowel?"),
      "a: str -> is (a) a vowel? vectorises for strings len > 1",
      "a: list -> is (a) all truthy?"
    ) {
      case a: VNum => ListHelpers.makeIterable(a).forall(MiscHelpers.boolify)
      case a: String if a.length == 1 => StringHelpers.isVowel(a.head)
      case a: String                  => VList(a.map(StringHelpers.isVowel)*)
      case a: VList                   => a.forall(MiscHelpers.boolify)
    }

    val anyTruthy = addElem(
      Monad,
      "a",
      "Any Truthy | Any() | Is Uppercase?",
      List("any", "is-uppercase?", "is-upper?", "upper?"),
      "a: str -> is (a) uppercase? vectorises for strings len > 1",
      "a: list -> is (a) any truthy?"
    ) {
      case a: VNum => ListHelpers.makeIterable(a).exists(MiscHelpers.boolify)
      case a: String if a.length == 1 => a.head.isUpper
      case a: String                  => VList(a.map(c => VNum(c.isUpper))*)
      case a: VList                   => a.exists(MiscHelpers.boolify)
    }

    val append = addElem(
      Dyad,
      "&",
      "Append",
      List("append"),
      "a: any, b: any -> list(a) ++ [b]"
    ) { case (a, b) =>
      VList.from(ListHelpers.makeIterable(a) :+ b)
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

    val compressDictionary = addElem(
      Monad,
      "#C",
      "Compress String Using Dictionary",
      List("compress-dict", "dict-comp", "compress"),
      "a: str -> compress a using the dictionary"
    ) { case a: String =>
      StringHelpers.compressDictionary(a)
    }

    val contains = addElem(
      Dyad,
      "c",
      "Contains",
      List("contains", "in"),
      "a: any, b: any -> is (b) in (a)?"
    ) {
      case (a: VList, b: VVal)  => a.contains(b)
      case (a: VVal, b: VList)  => b.contains(a)
      case (a: VList, b: VList) => a.contains(b)
      case (a: VVal, b: VVal)   => a.toString().contains(b.toString())
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

    val sus = addDirect(
      "ඞ",
      "ඞ",
      List("sus"),
      None,
      "ඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞ"
    ) { ctx ?=>
      MiscHelpers.vyPrintln("sus")
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

    val cycle = addElem(
      Monad,
      "Ċ",
      "Cycle | Is Positive?",
      List("cycle", "is-positive?", "positive?", ">0?"),
      "a: lst -> a ++ a ++ a ++ ...",
      "a: num -> a > 0"
    ) {
      case a: VList =>
        if a.isEmpty then VList()
        else
          lazy val temp: LazyList[VAny] = LazyList.from(a) #::: temp
          VList.from(temp)
      case a: VNum => a > 0
    }

    val divide = addVect(
      Dyad,
      "÷",
      "Divide | Split",
      List("divide", "div", "str-split"),
      "a: num, b: num -> a / b",
      "a: str, b: str -> Split a on the regex b"
    ) {
      case (a: VNum, b: VNum)     => a / b
      case (a: String, b: String) => VList(a.split(b)*)
    }

    val double = addVect(
      Monad,
      "d",
      "Double",
      List("double"),
      "a: num -> a * 2",
      "a: str -> a + a"
    ) {
      case (a: VNum)   => a * 2
      case (a: String) => a + a
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

    val exitProgram = addDirect(
      "Q",
      "Exit | Quit",
      List("exit", "quit"),
      None,
      "a -> Stop program execution"
    ) { ctx ?=> throw new QuitException }

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
      "Execute lambda without popping | Evaluate as Vyxal without popping | Boolean Mask | Is 1?",
      List(
        "peek-call",
        "exec-peek",
        "boolean-mask",
        "bool-mask",
        "strict-boolify",
        "is-1?"
      ),
      Some(1),
      "a: fun -> Execute a without popping",
      "a: str -> Evaluate a as Vyxal without popping",
      "a: lst -> Return a boolean array with 1s at the indices in a list.",
      "a: num -> Is a == 1?"
    ) { ctx ?=>
      (ctx.pop(): @unchecked) match
        case fn: VFun =>
          ctx.push(Interpreter.executeFn(fn, popArgs = false))
          if fn.arity == -1 then
            ctx.pop() // Handle the extra value pushed by lambdas that operate on the stack
        case code: String => Interpreter.execute(code)
        case a: VNum      => ctx.push(a == VNum(1))
        case a: VList =>
          if a.isEmpty then ctx.push(VList())
          else
            val indices = ListHelpers.makeIterable(a).map {
              case x: VNum => x.toInt
              case x =>
                throw new IllegalArgumentException(s"$x is not a number")
            }
            ctx.push(
              VList(
                (0 until indices.max + 1).map(x => VNum(indices.contains(x)))*
              )
            )
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

    val factors: Monad = addVect(
      Monad,
      "K",
      "Factors | Is Numeric?",
      List(
        "factors",
        "divisors",
        "is-numeric",
        "is-num",
        "is-number",
        "is-num?",
        "is-number?"
      ),
      "a: num -> Factors of a",
      "a: str -> Is a numeric?"
    ) {
      case a: VNum   => NumberHelpers.factors(a)
      case a: String => VNum(Lexer.decimalRegex.matches(a))
    }

    val factorial = addVect(
      Monad,
      "!",
      "Factorial | To Uppercase",
      List("fact", "factorial", "to-upper", "upper", "uppercase"),
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
      "a: num, b: num -> a from base b to base 10",
      "a: num, b: str|lst -> a from base with alphabet b to base 10",
    ) {
      case (a: VFun, b) =>
        ListHelpers.filter(ListHelpers.makeIterable(b, Some(true)), a)
      case (a, b: VFun) =>
        ListHelpers.filter(ListHelpers.makeIterable(a, Some(true)), b)
      case (a: VNum, b) =>
        NumberHelpers.fromBase(a, b)
      case (a: String, b: VNum) =>
        // Requires special casing
        val alphabet = "0123456789abcdefghijklmnopqrstuvwxyz".take(b.toInt)
        NumberHelpers.fromBase(a, alphabet)
      case (a, b) =>
        NumberHelpers.fromBase(a, b)
    }

    val flatten = addElem(
      Monad,
      "f",
      "Flatten",
      List("flatten", "flat"),
      "a: lst -> Flattened a"
    ) { case a =>
      ListHelpers.flatten(ListHelpers.makeIterable(a))
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
        if temp.nonEmpty then MiscHelpers.eval(temp)
        else ctx.settings.defaultValue
    }

    val greaterThan = addVect(
      Dyad,
      ">",
      "Greater Than",
      List("gt", "greater", "greater-than", "greater?", "bigger?"),
      "a: num, b: num -> a > b",
      "a: str, b: num -> a > str(b)",
      "a: num, b: str -> str(a) > b",
      "a: str, b: str -> a > b"
    ) { case (a: VVal, b: VVal) => MiscHelpers.compare(a, b) > 0 }

    val head: Monad = addElem(
      Monad,
      "h",
      "Head | First Item",
      List("head", "first", "first-item"),
      "a: lst -> a[0]"
    ) { case a =>
      ListHelpers
        .makeIterable(a)
        .headOption
        .getOrElse(MiscHelpers.defaultEmpty(a))
    }
    val hexadecimal: Monad = addVect(
      Monad,
      "H",
      "Hexadecimal | To Hexadecimal",
      List("hex", "hexadecimal", "to-hex", "to-hexadecimal"),
      "a: num -> a in hexadecimal",
      "a: str -> a as a hexadecimal number to base 10"
    ) {
      case a: VNum   => NumberHelpers.toBaseAlphabet(a, "0123456789ABCDEF")
      case a: String => NumberHelpers.fromBaseAlphabet(a, "0123456789ABCDEF")
    }

    val index: Dyad = addElem(
      Dyad,
      "i",
      "Index | Collect Unique Application Values | Enclose",
      List("index", "at", "item-at", "nth-item", "collect-unique", "enclose"),
      "a: lst, b: num -> a[b]",
      "a: lst, b: lst -> a[_] for _ in b",
      "a: str, b: lst -> ''.join(a[i] for i in b)",
      "a: any, b: fun -> Apply b on a and collect unique values. Does include the initial value.",
      "a: str, b: str -> enclose b in a (a[0:len(a)//2] + b + a[len(a)//2:])"
    ) {
      case (a: VList, b: VList) => a.index(b)
      case (a: String, b: VList) =>
        val temp = ListHelpers.makeIterable(a).index(b)
        temp match
          case l: VList => l.mkString
          case _        => temp
      case (a: VList, b: String) =>
        val temp = ListHelpers.makeIterable(b).index(a)
        temp match
          case l: VList => l.mkString
          case _        => temp
      case (a, b: VFun) => MiscHelpers.collectUnique(b, a)
      case (a: VFun, b) => MiscHelpers.collectUnique(a, b)
      case (a: VNum, b) => ListHelpers.makeIterable(b).index(a)
      case (a, b: VNum) => ListHelpers.makeIterable(a).index(b)
      case (a: String, b: String) =>
        val temp = a.length / 2
        a.slice(0, temp) + b + a.slice(temp, a.length)
    }

    val interleave: Dyad = addElem(
      Dyad,
      "I",
      "Interleave",
      List("interleave"),
      "a: lst, b: lst -> Interleave a and b"
    ) { case (a, b) =>
      val temp = ListHelpers.interleave(
        ListHelpers.makeIterable(a),
        ListHelpers.makeIterable(b)
      )
      if a.isInstanceOf[String] && b.isInstanceOf[String] then temp.mkString
      else temp
    }

    val isEven: Monad = addVect(
      Monad,
      "e",
      "Is Even / Split on Newlines",
      List(
        "even?",
        "even",
        "is-even?",
        "split-on-newlines",
        "newline-split",
        "split-newlines"
      ),
      "a: num -> a % 2 == 0",
      "a: str -> a split on newlines"
    ) {
      case a: VNum   => (a.underlying % 2) == VNum(0)
      case a: String => VList.from(a.split("\n").toSeq)
    }

    val joinOn: Dyad = addElem(
      Dyad,
      "j",
      "Join On",
      List("join-on", "join", "join-with", "join-by"),
      "a: lst, b: str -> a join on b"
    ) {
      case (a: VList, b: String) => a.mkString(b)
      case (a: VVal, b: VVal) =>
        ListHelpers.makeIterable(a).mkString(b.toString())
      case (a, b) =>
        val lst = ListHelpers.makeIterable(a)
        ListHelpers.flatten(VList.from(lst.head +: lst.tail.flatMap(Seq(b, _))))

    }

    val length: Monad = addElem(
      Monad,
      "L",
      "Length | Length of List",
      List("length", "len", "length-of", "len-of", "size"),
      "a: any -> Length of a"
    ) {
      case a: VList => a.length
      case a        => ListHelpers.makeIterable(a).length
    }

    val lengthVecorised: Monad = addElem(
      Monad,
      "l",
      "Length of Each Item",
      List(
        "length-vectorised",
        "length-vect",
        "len-vect",
        "len-vectorised",
        "vec-len",
        "vec-length",
        "vlen"
      ),
      "a: lst -> Length of each item in a"
    ) { case a =>
      VList.from(
        ListHelpers.makeIterable(a).map(ListHelpers.makeIterable(_).length)
      )
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

    val listRepeat: Dyad = addElem(
      Dyad,
      "Y",
      "List Repeat",
      List("wrap-repeat"),
      "a: any, b: num -> a repeated b times, wrapped in a list",
      "a: num, b: any -> b repeated a times, wrapped in a list",
      "a: lst|str, b: lst[num] -> a[_] repeated b[_] times, wrapped in a list",
    ) {
      case (a, b: VNum) => VList.fill(b.toInt)(a)
      case (a: VNum, b) => VList.fill(a.toInt)(b)
      case (a: (VList | String), b: VList) =>
        val temp = b
          .map {
            case n: VNum => n.toInt
            case x       =>
              // todo(lyxal): Are we sure we don't want to convert to VNum or
              //              something instead of erroring?
              throw new IllegalArgumentException(
                s"Can't repeat an item a non-integer number of times (found $x in $b)"
              )
          }
          .lazyZip(ListHelpers.makeIterable(a))
          .map((n, item) => VList.fill(n)(item))
        if a.isInstanceOf[String] then temp.map(_.mkString).mkString
        else VList.from(temp)
      case _ =>
        throw new IllegalArgumentException(
          "Can't repeat an item a non-integer number of times"
        )
    }

    val loopBreak = addDirect(
      "#X",
      "Loop Break",
      List("break"),
      Some(0),
      " -> break out of the current loop"
    ) { ctx ?=>
      throw new BreakLoopException
    }

    val loopContinue = addDirect(
      "#x",
      "Loop Continue",
      List("continue"),
      Some(0),
      " -> continue the current loop"
    ) { ctx ?=>
      throw new ContinueLoopException
    }

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

    val maximum = addDirect(
      "G",
      "Monadic Maximum | Dyadic Maximum | Generate From Function | Vectorised Maximum",
      List("max", "maximum", "generator"),
      Some(2),
      "a: lst -> Maximum of a",
      "a: non-lst, b: non-lst -> Maximum of a and b",
      "a: lst, b: fun -> Call b infinitely with items of a as starting values"
    ) { ctx ?=>
      val top = ctx.pop()
      top match
        case a: VList =>
          ctx.push(ListHelpers.maximum(a))
        case _ =>
          val next = ctx.pop()
          (top, next) match
            case (a: VVal, b: VVal) => ctx.push(MiscHelpers.dyadicMaximum(a, b))
            case (a: VFun, b: VList) =>
              ctx.push(ListHelpers.generate(a, b))
            case (a: VVal, b: VList) =>
              ctx.push(ListHelpers.vectorisedMaximum(b, a))
            case _ =>
              throw new Exception("Invalid arguments for maximum")
    }

    val merge: Dyad = addElem(
      Dyad,
      "J",
      "Merge",
      List("merge"),
      "a: lst, b: lst -> Merge a and b",
    ) {
      case (a: VNum, b: VNum)   => MiscHelpers.eval(a.toString + b.toString)
      case (a: VVal, b: VVal)   => MiscHelpers.add(a, b)
      case (a: VList, b: VList) => VList.from(a ++ b)
      case (a, b: VList)        => VList.from(a +: b)
      case (a: VList, b)        => VList.from(a :+ b)
    }

    val minimum = addDirect(
      "g",
      "Monadic Minimum | Dyadic Minimum | Generate From Function | Vectorised Minimum",
      List("max", "maximum", "generator"),
      Some(2),
      "a: lst -> Maximum of a",
      "a: non-lst, b: non-lst -> Maximum of a and b",
      "a: lst, b: fun -> Call b infinitely with items of a as starting values"
    ) { ctx ?=>
      val top = ctx.pop()
      top match
        case a: VList =>
          ctx.push(ListHelpers.minimum(a))
        case _ =>
          val next = ctx.pop()
          (top, next) match
            case (a: VVal, b: VVal) => ctx.push(MiscHelpers.dyadicMinimum(a, b))
            case (a: VFun, b: VList) =>
              ctx.push(ListHelpers.generateDyadic(a, b))
            case (a: VVal, b: VList) =>
              ctx.push(ListHelpers.vectorisedMinimum(b, a))
            case _ =>
              throw new Exception("Invalid arguments for mimimum")
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
      "a: str -> a.swapCase()",
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

    val overlaps = addDirect(
      "o",
      "Overlap | Overlapping Slices",
      List("overlap", "overlaps", "overlapping", "overlapping-slices"),
      Some(2),
      "a: lst, b: num -> Overlapping slices of a of length b",
      "a: lst|str -> Overlapping slices of a of length 2"
    ) { ctx ?=>
      val top = ctx.pop()
      top match
        case a: VList =>
          ctx.push(
            VList.from(
              ListHelpers.overlaps(a, 2)
            )
          )
        case a: String => ctx.push(VList.from(ListHelpers.overlaps(a, 2)))
        case _ =>
          val next = ctx.pop()
          (top, next) match
            case (a: VNum, b: String) =>
              ctx.push(VList.from(ListHelpers.overlaps(b, a.toInt)))
            case (a: VNum, b: VList) =>
              ctx.push(VList.from(ListHelpers.overlaps(b.lst, a.toInt)))
            case _ =>
              throw new Exception("Invalid arguments for overlaps")
      end match
    }

    val pair =
      addFull(Dyad, ";", "Pair", List("pair"), false, "a, b -> [a, b]") {
        VList(_, _)
      }

    val prefixes =
      addElem(
        Monad,
        "P",
        "Prefixes",
        List("prefixes"),
        "a: lst -> Prefixes of a"
      ) {
        case a: VList => VList.from(ListHelpers.prefixes(a))
        case a: String =>
          VList.from(
            ListHelpers
              .prefixes(ListHelpers.makeIterable(a))
              .map(_.mkString)
          )
        case a: VNum =>
          VList.from(
            ListHelpers
              .prefixes(ListHelpers.makeIterable(a.vabs))
              .map(n => MiscHelpers.eval(n.mkString))
          )
      }

    val prepend = addElem(
      Dyad,
      "p",
      "Prepend",
      List("prepend"),
      "a: lst, b: any -> b prepended to a"
    ) {
      case (a: String, b: (String | VNum)) => b.toString() + a
      case (a: VNum, b: String)            => b + a.toString()
      case (a: VNum, b: VNum) => MiscHelpers.eval(b.toString() + a.toString())
      case (a: VList, b)      => VList.from(b +: a)
      case (a, b)             => VList(b, a)
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

    val quotify = addElem(
      Monad,
      "q",
      "Quotify",
      List("quotify"),
      "a: any -> enclose a in quotes, escape backslashes and quote marks"
    ) {
      case a: String => StringHelpers.quotify(a)
      case a         => StringHelpers.quotify(a.toString())
    }

    val recurse = addDirect(
      "x",
      "Recursion | Recurse",
      List("recurse"),
      None,
      " -> call the current function recursively"
    ) { ctx ?=>
      if ctx.globals.callStack.isEmpty then
        throw new RecursionError("No function to recurse")
      else ctx.push(Interpreter.executeFn(ctx.globals.callStack.top))
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

    val replace = addElem(
      Triad,
      "r",
      "Replace",
      List("replace"),
      "a: str, b: str, c: str -> replace all instances of b in a with c"
    ) {
      case (a: String, b: VVal, c: VVal) =>
        a.replace(b.toString(), c.toString())
      case (a: VNum, b: VVal, c: VVal) =>
        MiscHelpers.eval(a.toString().replace(b.toString(), c.toString()))
      case (a: VList, b, c) =>
        VList.from(a.lst.map(x => if x == b then c else x))
      case (a: VVal, b: VVal, c: VList) =>
        VList.from(c.lst.map(x => if x == a then b else x))
      case (a: VVal, b: VList, c: VVal) =>
        VList.from(b.lst.map(x => if x == a then c else x))
      case (a: VVal, b: VList, c: VList) =>
        VList.from(b.lst.map(x => if x == a then c else x))

    }

    val returnStatement = addDirect(
      "X",
      "Return Statement",
      List("return", "ret"),
      None,
      "a -> return a"
    ) { ctx ?=>
      throw new ReturnFromFunctionException
    }

    val sort: Monad = addFull(
      Monad,
      "S",
      "Sort ascending",
      List(
        "sort",
        "sortasc",
        "sort-asc",
      ),
      false,
      "a: any -> convert to list and sort ascending",
    ) {
      // should do something else for num overload later
      case s: String => s.sorted
      case a =>
        VList.from(
          ListHelpers.makeIterable(a).sorted(MiscHelpers.compareExact(_, _))
        )
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

    val split = addElem(
      Dyad,
      "s",
      "Split",
      List("split"),
      "a: any, b: any -> split a by b"
    ) {
      case (a: String, b) =>
        if b.isInstanceOf[String] && b.toString.isEmpty then
          ListHelpers.makeIterable(a)
        else VList.from(a.split(b.toString()).toSeq)
      case (a: VNum, b) =>
        VList.from(a.toString().split(b.toString()).toSeq.map(MiscHelpers.eval))
      case (a: VList, b) => ListHelpers.splitNormal(a, b)
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

    val tail: Monad = addElem(
      Monad,
      "t",
      "Tail | Last Item",
      List("tail", "last", "last-item"),
      "a: lst -> a[-1]"
    ) { case a =>
      ListHelpers
        .makeIterable(a)
        .lastOption
        .getOrElse(MiscHelpers.defaultEmpty(a))
    }

    val toBase = addElem(
      Dyad,
      "y",
      "To Base",
      List("to-base"),
      "a: num, b: num -> a in base b",
      "a: num, b: str|lst -> a in base with alphabet b",
      "a: lst, b: num -> each x in a in base b",
      "a: lst, b: str|lst -> each x in a in base with alphabet b"
    ) {
      case (a: VNum, b)  => NumberHelpers.toBase(a, b)
      case (a: VList, b) => a.vmap(NumberHelpers.toBase(_, b))
    }

    val transposeSafe = addElem(
      Monad,
      "ÞT",
      "Transpose Safe",
      List("transpose-safe"),
      "a: any -> transpose a"
    ) {
      case a: VFun =>
        throw RuntimeException(s"Can't transpose (ÞT) function: $a")
      case a => ListHelpers.transposeSafe(ListHelpers.makeIterable(a))
    }

    val triple: Monad = addElem(
      Monad,
      "T",
      "Triple | Contains Only Alphabet | Transpose",
      List(
        "triple",
        "alphabet?",
        "alphabetical?",
        "contains-only-alphabet?",
        "contains-only-alphabetical?",
        "transpose",
        "flip",
        "reverse-axes",
        "flip-axes",
        "permute-axes",
      ),
      "a: num -> 3 * a",
      "a: str -> does a contain only alphabet characters?",
      "a: any -> transpose a"
    ) {
      case a: VNum   => a * 3
      case a: String => a.forall(_.isLetter)
      case a: VList  => ListHelpers.transpose(a)
    }

    val triplicate =
      addDirect("D", "Triplicate", List("trip"), None, "a -> [a, a, a]") {
        ctx ?=>
          val a = ctx.pop()
          ctx.push(a, a, a)
      }

    val twoPower = addVect(
      Monad,
      "E",
      "2 Power | Evaluate",
      List("two^", "two**", "eval"),
      "a: num -> 2^a",
      "a: str -> evaluate (not execute) a"
    ) {
      case a: VNum   => exponentation(VNum(2), a)
      case a: String => MiscHelpers.eval(a)
    }

    val uninterleave = addDirect(
      "U",
      "Uninterleave",
      List("uninterleave"),
      None,
      "a: any -> uninterleave a"
    ) { ctx ?=>
      val a = ctx.pop()
      val lst = ListHelpers.makeIterable(a)
      val (evens, odds) = lst.zipWithIndex.partition(_._2 % 2 == 0)
      // Make sure to preserve type
      val (pushEven, pushOdd) = a match
        case _: VList =>
          VList.from(evens.map(_._1)) -> VList.from(odds.map(_._1))
        case _: VNum =>
          MiscHelpers.eval(evens.map(_._1).mkString) -> MiscHelpers.eval(
            odds.map(_._1).mkString
          )
        case _: String => evens.map(_._1).mkString -> odds.map(_._1).mkString
        case _ =>
          throw RuntimeException("Uninterleave: Can't uninterleave functions")

      ctx.push(pushEven, pushOdd)
    }

    val uniquify = addElem(
      Monad,
      "u",
      "Uniquify",
      List("uniquify"),
      "a: lst -> a with duplicates removed"
    ) { case a =>
      val iter = ListHelpers.makeIterable(a)
      val uniq: LazyList[Option[VAny]] = LazyList.unfold(Seq[VAny]() -> 0) {
        state =>
          if !iter.hasIndex(state._2) then None
          else if state._1.contains(iter.index(state._2)) then
            Some(None, state._1 -> (state._2 + 1))
          else
            Some(
              Some(iter.index(state._2)),
              (state._1 :+ iter.index(state._2)) -> (state._2 + 1)
            )
      }
      a match
        case _: VList  => VList.from(uniq.flatten)
        case _: VNum   => MiscHelpers.eval(uniq.flatten.mkString)
        case _: String => uniq.flatten.mkString
        case _ => throw RuntimeException("Uniquify: Can't uniquify functions")

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

    val vectorisedReverse = addElem(
      Monad,
      "V",
      "Vectorised Reverse / Complement / Title Case",
      List(
        "vectorised-reverse",
        "vec-reverse",
        "complement",
        "titlecase",
        "title-case"
      ),
      "a: lst -> each element of a reversed",
      "a: num -> 1 - a",
      "a: str -> a converted to title case"
    ) {
      case a: VList  => VList.from(a.map(ListHelpers.reverse))
      case a: VNum   => 1 - a
      case a: String => StringHelpers.titlecase(a)
    }

    val wrap = addDirect(
      "W",
      "Wrap",
      List("wrap"),
      None,
      "a, b, c, ..., -> [a, b, c, ...]"
    ) { ctx ?=>
      ctx.wrap
    }

    val wrapSingleton = addFull(
      Monad,
      "w",
      "Wrap Singleton",
      List("wrap-singleton"),
      false,
      "a -> [a]"
    ) { a => VList(a) }

    val zeroRange = addVect(
      Monad,
      "z",
      "Zero Range | Is Lowercase",
      List("zero-range", "zero->n", "is-lowercase?", "lowercase?", "lower?"),
      "a: num -> [0, 1, ..., a]",
      "a: str -> is a lowercase?"
    ) {
      case a: VNum => NumberHelpers.range(0, a)
      case a: String =>
        if a.length == 1 then a.forall(_.isLower)
        else VList.from(a.map(x => VNum(x.isLower)))
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

    val zip = addElem(
      Dyad,
      "Z",
      "Zip",
      List("zip", "zip-map"),
      "a: lst, b: lst -> zip a and b",
      "a: lst, b: fun -> [[x, b(x)] for x in a]",
      "a: fun, b: lst -> [[a(x), x] for x in b]",
    ) {
      case (a: VData, b: VData) =>
        ListHelpers.makeIterable(a).zip(ListHelpers.makeIterable(b))
      case (a: VData, b: VFun) =>
        val iter = ListHelpers.makeIterable(a)
        VList.from(iter.zip(ListHelpers.map(b, iter)))
      case (a: VFun, b: VData) =>
        val iter = ListHelpers.makeIterable(b)
        VList.from(ListHelpers.map(a, iter).zip(iter))
    }

    // Constants
    addNilad("¦", "Pipe", List("pipe"), "|") { "|" }
    addNilad("ð", "Space", List("space"), " ") { " " }
    addNilad("¶", "Newline", List("newline"), "\n") { "\n" }
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
