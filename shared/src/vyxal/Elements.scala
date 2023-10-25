package vyxal

import scala.language.implicitConversions

import vyxal.ListHelpers.makeIterable
import vyxal.NumberHelpers.range
import vyxal.VNum.given

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.io.StdIn

case class Element(
    symbol: String,
    name: String,
    keywords: Seq[String],
    arity: Option[Int],
    vectorises: Boolean,
    overloads: Seq[String],
    impl: DirectFn,
)

/** Implementations for all the elements */
object Elements:
  /** Find the symbol for a keyword in literate mode, if it exists */
  def symbolFor(keyword: String): Option[String] =
    Elements.elements.values.find(_.keywords.contains(keyword)).map(_.symbol)

  val elements: Map[String, Element] = Map(
    addFull(
      Dyad,
      "+",
      "Addition",
      List("add", "+", "plus"),
      true,
      "a: num, b: num -> a + b",
      "a: num, b: str -> a + b",
      "a: str, b: num -> a + b",
      "a: str, b: str -> a + b",
    )(MiscHelpers.add),
    addPart(
      Dyad,
      "ȧ",
      "Absolute Difference | Apply to Neighbours",
      List("abs-diff", "apply-to-neighbours"),
      true,
      "a: num, b: num -> |a - b|",
      "a: lst, b: fun -> apply b to each pair of neighbours in a [applies to windows of length 2]",
    ) {
      case (a: VNum, b: VNum) => (a - b).vabs
      case (a: VList, b: VFun) =>
        VList.from(ListHelpers.overlaps(a, 2).map(x => b(x*)))
      case (a: VFun, b: VList) =>
        VList.from(ListHelpers.overlaps(b, 2).map(x => a(x*)))
    },
    addPart(
      Monad,
      "Ȧ",
      "Absolute Value | Keep Alphabet Characters",
      List("abs", "absolute-value", "keep-alphabet"),
      true,
      "a: num -> |a|",
      "a: str -> keep alphabet characters of a",
    ) {
      case a: VNum => a.vabs
      case a: String => a.filter(_.isLetter)
    },
    addPart(
      Monad,
      "A",
      "All Truthy | All() | Is Vowel?",
      List("all", "is-vowel?", "vowel?"),
      false,
      "a: str -> is (a) a vowel? vectorises for strings len > 1",
      "a: list -> is (a) all truthy?",
    ) {
      case a: VNum => ListHelpers.makeIterable(a).forall(_.toBool)
      case a: String if a.length == 1 => StringHelpers.isVowel(a.head)
      case a: String => VList(a.map(StringHelpers.isVowel)*)
      case a: VList => a.forall(_.toBool)
    },
    addPart(
      Monad,
      "≈",
      "All Equal?",
      List("all-equal", "all-equal?"),
      false,
      "a: lst -> are all elements of a equal?",
    ) { a =>
      val lst = ListHelpers.makeIterable(a)
      if lst.isEmpty then 1
      else lst.forall(_ == lst(0))
    },
    addPart(
      Monad,
      "a",
      "Any Truthy | Any() | Is Uppercase?",
      List("any", "is-uppercase?", "is-upper?", "upper?"),
      false,
      "a: str -> is (a) uppercase? vectorises for strings len > 1",
      "a: list -> is (a) any truthy?",
    ) {
      case a: VNum => ListHelpers.makeIterable(a).exists(_.toBool)
      case a: String if a.length == 1 => a.head.isUpper
      case a: String => VList(a.map(c => VNum(c.isUpper))*)
      case a: VList => a.exists(_.toBool)
    },
    addPart(
      Dyad,
      "&",
      "Append",
      List("append"),
      false,
      "a: any, b: any -> list(a) ++ [b]",
    ) { case (a, b) => VList.from(ListHelpers.makeIterable(a) :+ b) },
    addPart(
      Triad,
      "Ạ",
      "Assign",
      List(
        "assign",
        "assign-at",
        "assign<>",
        "assign<x>",
        "a<x>=",
        "a<x>=y",
        "a<x>?=y",
        "set-item",
        "apply-at",
      ),
      false,
      "a: lst, b: num, c: non-fun -> assign c to a at the index b / a[b] = c",
      "a: lst, b: num, c: fun -> a[b] c= <stack items> (augmented assignment to list)",
      "a: lst, b: lst, c: lst -> assign c to a at the indices in b",
    ) {
      case (a, b: VNum, c: VPhysical) =>
        val temp = ListHelpers.assign(ListHelpers.makeIterable(a), b, c)
        if a.isInstanceOf[String] then temp.mkString
        else temp
      case (a, b: VVal, c: VNum) =>
        val temp = ListHelpers.assign(ListHelpers.makeIterable(a), c, b)
        if a.isInstanceOf[String] then temp.mkString
        else temp
      case (a, b: VNum, c: VFun) =>
        val temp = ListHelpers.augmentAssign(ListHelpers.makeIterable(a), b, c)
        if a.isInstanceOf[String] then temp.mkString
        else temp
      case (a, b: VList, c) =>
        var temp = ListHelpers.makeIterable(a)
        for i <- ListHelpers.makeIterable(b) do
          i match
            case ind: VNum => temp = ListHelpers.assign(temp, ind, c)
            case _ => throw IllegalArgumentException("Index must be a number")
        if a.isInstanceOf[String] then temp.mkString
        else temp
      case (a, b: VList, c: VList) =>
        var temp = ListHelpers.makeIterable(a)
        for (i, j) <-
            ListHelpers.makeIterable(b).zip(ListHelpers.makeIterable(c))
        do
          i match
            case ind: VNum => j match
                case value: VPhysical => temp = ListHelpers.assign(temp, ind, j)
                case function: VFun =>
                  temp = ListHelpers.augmentAssign(temp, ind, function)
            case _ => throw IllegalArgumentException("Index must be a number")
        if a.isInstanceOf[String] then temp.mkString
        else temp
    },
    addPart(
      Monad,
      "ḃ",
      "Bit | Parity | Last Half of String",
      List("bit", "parity", "str-last-half"),
      true,
      "a: num -> parity of a (a % 2)",
      "a: str -> last half of a",
    ) {
      case a: VNum => a % 2
      case a: String => a.slice(a.length / 2, a.length)
    },
    addPart(
      Monad,
      "Ṃ",
      "Bit Length | Matrix Inverse",
      List("bit-length", "matrix-inverse"),
      true,
      "a: num -> bit length of a",
      "a: lst[lst] -> matrix inverse of a",
    ) {
      case n: VNum => VNum(NumberHelpers.toBinary(n).size)
      case l: VList if l.forall(_.isInstanceOf[VList]) =>
        ListHelpers.matrixInverse(l).getOrElse {
          scribe.warn(s"Could not invert matrix $l")
          l
        }
      case l: VList => l.vmap(item =>
          item match
            case n: VNum => VNum(NumberHelpers.toBinary(n).size)
            case _ => item
        )

    },
    addPart(
      Monad,
      "⌐",
      "Bitwise Not",
      List("bitwise-not"),
      true,
      "a: num -> ~a",
    ) {
      case a: VNum => ~a.toBigInt
    },
    addPart(
      Dyad,
      "∴",
      "Bitwise And",
      List("bitwise-and"),
      true,
      "a: num, b: num -> a & b",
    ) {
      case (a: VNum, b: VNum) => a.toBigInt & b.toBigInt
    },
    addPart(
      Dyad,
      "∵",
      "Bitwise Or",
      List("bitwise-or"),
      true,
      "a: num, b: num -> a | b",
    ) {
      case (a: VNum, b: VNum) => a.toBigInt | b.toBigInt
    },
    addPart(
      Dyad,
      "⊻",
      "Bitwise Xor",
      List("bitwise-xor"),
      true,
      "a: num, b: num -> a ^ b",
    ) {
      case (a: VNum, b: VNum) => a.toBigInt ^ b.toBigInt
    },
    addPart(
      Dyad,
      "«",
      "Bitshift Left",
      List("bitwise-left-shift", "left-shift"),
      true,
      "a: num, b: num -> a << b",
    ) {
      case (a: VNum, b: VNum) => a.toBigInt << b.toInt
    },
    addPart(
      Dyad,
      "»",
      "Bitshift Right",
      List("bitwise-right-shift", "right-shift"),
      true,
      "a: num, b: num -> a >> b",
    ) {
      case (a: VNum, b: VNum) => a.toBigInt >> b.toInt
    },
    addFull(
      Monad,
      "ȯ",
      "Boolify",
      List("boolify"),
      false,
      "a: any -> bool(a)",
    )(_.toBool),
    addFull(
      Dyad,
      "Ẋ",
      "Cartesian Product",
      List("cartesian-product", "cartesian", "cart-prod", "cart"),
      false,
      "a: list, b: list -> cartesian product of a and b",
    )(ListHelpers.cartesianProduct(_, _)),
    addFull(
      Dyad,
      "ᶿ",
      "Cartesian Product Unsafe",
      List(
        "cartesian-product-unsafe",
        "cartesian-unsafe",
        "cart-prod-unsafe",
        "cart-unsafe",
      ),
      false,
      "a: list, b: list -> cartesian product of a and b in the standard order, but without accounting for infinite lists",
    )(ListHelpers.cartesianProduct(_, _, unsafe = true)),
    addFull(
      Monad,
      "B",
      "Convert From Binary",
      List("from-binary", "bin->dec", "bin->decimal"),
      false,
      "a: num -> str(a) from binary",
      "a: str -> int(a, 2)",
      "a: lst -> int(a, 2), using list of digits",
    )(NumberHelpers.fromBinary),
    addPart(
      Monad,
      "b",
      "Convert To Binary",
      List("to-binary", "dec->bin", "decimal->bin"),
      true,
      "a: num -> convert a to binary",
      "a: str -> bin(ord(x) for x in a)",
    ) {
      case a: VNum => NumberHelpers.toBinary(a)
      case a: String => VList(
          a.map(x => NumberHelpers.toBinary(StringHelpers.chrord(x.toString)))*
        )
    },
    addPart(
      Monad,
      "#C",
      "Compress String Using Dictionary",
      List("compress-dict", "dict-comp", "compress"),
      false,
      "a: str -> compress a using the dictionary",
    ) { case a: String => StringHelpers.compressDictionary(a) },
    addPart(
      Dyad,
      "c",
      "Contains",
      List("contains", "in"),
      false,
      "a: any, b: lst -> is element a in list b?",
      "a: any, b: any -> is str(b) in str(a)?",
    ) {
      case (a: VList, b) => a.contains(b)
      case (a, b: VList) => b.contains(a)
      case (a, b) => a.toString.contains(b.toString)
    },
    addDirect("🍪", "Cookie", List("cookie"), None, "cookie.") {
      while true do MiscHelpers.vyPrintln("cookie")
    },
    addDirect(
      "ඞ",
      "ඞ",
      List("sus"),
      None,
      "ඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞඞ",
    ) { MiscHelpers.vyPrintln("sus") },
    addPart(
      Dyad,
      "C",
      "Count",
      List("count"),
      false,
      "a: any, b: any -> count(b in a)",
    ) {
      case (a: VList, b) => a.count(_ === b)
      case (a, b: VList) => b.count(_ === a)
      case (a, b) => StringHelpers.countString(a.toString, b.toString)
    },
    addPart(
      Monad,
      "Ċ",
      "Cycle | Is Positive?",
      List("cycle", "is-positive?", "positive?", ">0?"),
      false,
      "a: lst -> a ++ a ++ a ++ ...",
      "a: num -> a > 0",
    ) {
      case a: VList =>
        if a.isEmpty then VList()
        else
          lazy val temp: LazyList[VAny] = LazyList.from(a) #::: temp
          VList.from(temp)
      case a: VNum => a > 0
    },
    addPart(
      Monad,
      "ᵛ",
      "Decrement",
      List("decr", "decrement"),
      true,
      "a: num -> a - 1",
    ) {
      case a: VNum => a - 1
    },
    addPart(
      Monad,
      "¯",
      "Deltas",
      List("deltas"),
      false,
      "a: lst -> forward-differences of a",
    ) { a =>
      ListHelpers.deltas(ListHelpers.makeIterable(a))
    },
    addPart(
      Dyad,
      "÷",
      "Divide | Split",
      List("divide", "div", "str-split"),
      true,
      "a: num, b: num -> a / b",
      "a: str, b: str -> Split a on the regex b",
    ) {
      case (a: VNum, b: VNum) => a / b
      case (a: String, b: String) => VList(a.split(b)*)
    },
    addFull(
      Dyad,
      "Ḋ",
      "Divides? | Append Spaces | Remove Duplicates by Function",
      List("divides?", "+-spaces", "dedup-by"),
      false,
      "a: num, b: num -> a % b == 0",
      "a: str, b: num -> a + ' ' * b",
      "a: num, b: str -> b + ' ' * a",
      "a: lst, b: fun -> Remove duplicates from a by applying b to each element",
    ) { (a, b) => NumberHelpers.divides(a, b) },
    addPart(
      Dyad,
      "ḋ",
      "Dot Product | To Bijective Base | First Index Where Predicate Truthy",
      List("dot-product", "bijective-base", "dot-prod", "first-index-where"),
      false,
      "a: lst, b: lst -> Dot product of a and b",
      "a: num, b: num -> Convert a to bijective base b",
    ) {
      case (a: VList, b: VList) => ListHelpers.dotProduct(a, b)
      case (a: VNum, b: VNum) => NumberHelpers.toBijectiveBase(a, b)
      case (a, b: VFun) =>
        // Any other day of the week I'd have used a filter or a functional
        // programming thing, but because JVM can't do indices bigger than
        // the int limit and you might plausibly want to hypothetically
        // use this on a list with more than 2^31 elements, I'm using a
        // while loop instead.
        var pos = VNum(0)
        val list = ListHelpers.makeIterable(a)
        while list.hasIndex(pos.toBigInt) && b(list.index(pos)) == VNum(0) do
          pos += 1
        if list.hasIndex(pos.toBigInt) then pos else VNum(-1)
    },
    addPart(
      Monad,
      "d",
      "Double",
      List("double"),
      true,
      "a: num -> a * 2",
      "a: str -> a + a",
    ) {
      case a: VNum => a * 2
      case a: String => a + a
    },
    addDirect(":", "Duplicate", List("dup"), None, "a -> a, a") { ctx ?=>
      val a = ctx.pop()
      ctx.push(a, a)
    }

    // todo extract to helper in MiscHelpers?
    ,
    addPart(
      Dyad,
      "=",
      "Equals",
      List("eq", "==", "equal", "same?", "equals?", "equal?"),
      true,
      "a: any, b: any -> a == b",
    ) {
      case (a: VNum, b: VNum) => a == b
      case (a: VNum, b: String) => a.toString == b
      case (a: String, b: VNum) => a == b.toString
      case (a: String, b: String) => a == b
    },
    addFull(
      Dyad,
      "₌",
      "Exactly Equals",
      List("===", "exactly-equal", "strictly-equal?"),
      false,
      "a: any, b: any -> a === b (non-vectorising)",
    ) { (a, b) =>
      a === b
    },
    addFull(
      Dyad,
      "≠",
      "Not Equal",
      List("not-equal", "=n't"),
      false,
      "a: any, b: any -> a !== b (non-vectorising)",
    ) { (a, b) =>
      a !== b
    },
    addDirect(
      "Ė",
      "Execute lambda | Evaluate as Vyxal | Power with base 10",
      List("execute-lambda", "evaluate-as-vyxal", "power-base-10", "call", "@"),
      Some(1),
      "a: fun -> Execute a",
      "a: str -> Evaluate a as Vyxal",
      "a: num -> 10 ** n",
    ) { ctx ?=> ctx.push(execHelper(ctx.pop())) },
    addDirect(
      "Q",
      "Exit | Quit",
      List("exit", "quit"),
      None,
      "a -> Stop program execution",
    ) { throw QuitException() },
    addDirect(
      "Ḃ",
      "Execute lambda without popping | Evaluate as Vyxal without popping | Boolean Mask | Is 1?",
      List(
        "peek-call",
        "exec-peek",
        "boolean-mask",
        "bool-mask",
        "strict-boolify",
        "is-1?",
      ),
      Some(1),
      "a: fun -> Execute a without popping",
      "a: str -> Evaluate a as Vyxal without popping",
      "a: lst -> Return a boolean array with 1s at the indices in a list.",
      "a: num -> Is a == 1?",
    ) { ctx ?=>
      ctx.pop() match
        case fn: VFun =>
          ctx.push(Interpreter.executeFn(fn, popArgs = false))
          if fn.arity == -1 then
            ctx.pop() // Handle the extra value pushed by lambdas that operate on the stack
        case code: String => Interpreter.execute(code)
        case a: VNum => ctx.push(a == VNum(1))
        case a: VList =>
          if a.isEmpty then ctx.push(VList())
          else
            val indices = ListHelpers.makeIterable(a).map {
              case x: VNum => x.toInt
              case x => throw IllegalArgumentException(s"$x is not a number")
            }
            ctx.push(
              VList(
                (0 until indices.max + 1).map(x => VNum(indices.contains(x)))*
              )
            )
    },
    addPart(
      Dyad,
      "*",
      "Exponentation | Remove Nth Letter | Trim",
      List("exp", "**", "pow", "exponent", "remove-letter", "str-trim"),
      true,
      "a: num, b: num -> a ^ b",
      "a: str, b: num -> a with the bth letter removed",
      "a: num, b: str -> b with the ath letter removed",
      "a: str, b: str -> trim b from both sides of a",
    ) {
      case (a: VNum, b: VNum) => a ** b
      case (a: String, b: VNum) => StringHelpers.remove(a, b.toInt)
      case (a: VNum, b: String) => StringHelpers.remove(b, a.toInt)
      case (a: String, b: String) =>
        if b == "" then a
        else
          var res = a
          while res.startsWith(b) do res = res.drop(b.length)
          while res.endsWith(b) do res = res.dropRight(b.length)
          res
    },
    addDirect("_", "Pop and Discard", List("pop", "discard"), None, "a ->") {
      ctx ?=> ctx.pop()
    },
    addPart(
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
        "is-number?",
      ),
      true,
      "a: num -> Factors of a",
      "a: str -> Is a numeric?",
    ) {
      case a: VNum => NumberHelpers.factors(a)
      case a: String => VNum(VNum.DecimalRegex.matches(a))
    },
    addPart(
      Monad,
      "!",
      "Factorial",
      List("fact", "factorial"),
      true,
      "a: num -> a!",
    ) {
      case a @ VNum(r, i) =>
        if r.isWhole then spire.math.fact(spire.math.abs(a.toLong))
        else NumberHelpers.gamma(spire.math.abs(a.underlying.real) + 1)
    },
    addPart(
      Dyad,
      "F",
      "Filter by Function | From Base",
      List("filter", "keep-by", "from-base", "10->b"),
      false,
      "a: fun, b: lst -> Filter b by truthy results of a",
      "a: lst, b: fun -> Filter a by truthy results of b",
      "a: num, b: num -> a from base b to base 10",
      "a: num, b: str|lst -> a from base with alphabet b to base 10",
    ) {
      case (a: VFun, b) =>
        ListHelpers.filter(ListHelpers.makeIterable(b, Some(true)), a)
      case (a, b: VFun) =>
        ListHelpers.filter(ListHelpers.makeIterable(a, Some(true)), b)
      case (a: VNum, b) => NumberHelpers.fromBase(a, b)
      case (a: String, b: VNum) =>
        // Requires special casing
        val alphabet = "0123456789abcdefghijklmnopqrstuvwxyz".take(b.toInt)
        NumberHelpers.fromBase(a, alphabet)
      case (a, b) => NumberHelpers.fromBase(a, b)
    },
    addFull(
      Dyad,
      "Ḟ",
      "Find",
      List("find"),
      false,
      "a: any, b: any -> a.indexOf(b) (-1 if not found)",
      "a: any, b: fun -> truthy indices of mapping b over a",
    ) {
      case (a, b: VFun) =>
        VList.from(ListHelpers.makeIterable(a).zipWithIndex.collect {
          case (elem, ind) if b(elem).toBool => VNum(ind)
        })
      case (a, b) => ListHelpers.makeIterable(a).indexOf(b)
    },
    addFull(
      Monad,
      "f",
      "Flatten",
      List("flatten", "flat"),
      false,
      "a: lst -> Flattened a",
    ) { a => ListHelpers.flatten(ListHelpers.makeIterable(a)) },
    addNilad(
      "m",
      "Get Context Variable M",
      List("get-context-m", "context-m", "c-var-m", "ctx-m", "ctx-secondary"),
      " -> context variable m",
    ) { ctx ?=> ctx.ctxVarSecondary },
    addNilad(
      "n",
      "Get Context Variable N",
      List("get-context-n", "context-n", "c-var-n", "ctx-n", "ctx-primary"),
      " -> context variable n",
    ) { ctx ?=> ctx.ctxVarPrimary },
    addNilad(
      "?",
      "Get Input",
      List("get-input", "input", "stdin", "readline"),
      " -> input",
    ) { ctx ?=>
      if ctx.globals.inputs.nonEmpty then ctx.globals.inputs.next()
      else if ctx.settings.online then ctx.settings.defaultValue
      else
        val temp = StdIn.readLine()
        if temp.nonEmpty then MiscHelpers.eval(temp)
        else ctx.settings.defaultValue
    },
    addPart(
      Monad,
      "↑",
      "Grade Up",
      List("grade-up"),
      false,
      "a: any -> indices that will sort a",
    ) { a => ListHelpers.gradeUp(a) },
    addPart(
      Monad,
      "↓",
      "Grade Down",
      List("grade-down"),
      false,
      "a: any -> indices that will reverse-sort a",
    ) { a => ListHelpers.gradeDown(a) },
    addPart(
      Dyad,
      ">",
      "Greater Than",
      List("gt", "greater", "greater-than", "greater?", "bigger?"),
      true,
      "a: num, b: num -> a > b",
      "a: str, b: num -> a > str(b)",
      "a: num, b: str -> str(a) > b",
      "a: str, b: str -> a > b",
    ) { case (a: VVal, b: VVal) => a > b },
    addPart(
      Dyad,
      "≥",
      "Greater Than Or Equal To",
      List("ge", "greater-than-or-equal-to"),
      true,
      "a: num, b: num -> a >= b",
      "a: str, b: num -> a >= str(b)",
      "a: num, b: str -> str(a) >= b",
      "a: str, b: str -> a >= b",
    ) { case (a: VVal, b: VVal) => a >= b },
    addPart(
      Dyad,
      "Ġ",
      "Group by Function Result",
      List("group-by"),
      false,
      "a: any, b: fun -> group a by the results of b",
      "a: fun, b: any -> group b by the results of a",
    ) {
      case (a, b: VFun) => ListHelpers.groupBy(ListHelpers.makeIterable(a), b)
      case (a: VFun, b) => ListHelpers.groupBy(ListHelpers.makeIterable(b), a)
    },
    addPart(
      Monad,
      "½",
      "Halve",
      List("halve"),
      true,
      "a: num -> a / 2",
      "a: str -> a split into two pieces",
    ) {
      case a: VNum => a / 2
      case a: String =>
        val (fst, snd) = a.splitAt(a.length / 2)
        VList(fst, snd)
    },
    addFull(
      Monad,
      "h",
      "Head | First Item",
      List("head", "first", "first-item"),
      false,
      "a: lst -> a[0]",
    ) { a =>
      ListHelpers
        .makeIterable(a)
        .headOption
        .getOrElse(MiscHelpers.defaultEmpty(a))
    },
    addFull(
      Monad,
      "Ḣ",
      "Head Remove | Behead",
      List("head-remove", "behead"),
      false,
      "a: str -> a[1:]",
      "a: any -> toList(a)[1:]",
    ) {
      case s: String => if s.nonEmpty then s.substring(1) else ""
      case a => ListHelpers.makeIterable(a).drop(1)
    },
    addPart(
      Monad,
      "H",
      "Hexadecimal | To Hexadecimal",
      List("hex", "hexadecimal", "to-hex", "to-hexadecimal"),
      true,
      "a: num -> a in hexadecimal",
      "a: str -> a as a hexadecimal number to base 10",
    ) {
      case a: VNum => NumberHelpers.toBaseAlphabet(a, "0123456789ABCDEF")
      case a: String => NumberHelpers.fromBaseAlphabet(a, "0123456789ABCDEF")
    },
    addDirect(
      "ḣ",
      "Head Extract",
      List("head-extract", "split-at-head"),
      Some(1),
      "a: lst|str -> Push a[0], then a[1:] onto the stack",
    ) { ctx ?=>
      ctx.pop() match
        case lst: VList => ctx.push(
            lst.headOption.getOrElse(ctx.settings.defaultValue),
            lst.drop(1),
          )
        case s: String =>
          ctx.push(if s.isEmpty then "" else s.charAt(0).toString, s.drop(1))
        case arg => throw UnimplementedOverloadException("ḣ", List(arg))
    },
    addDirect(
      "Ḥ",
      "Head Extract",
      List("head-extract-swap", "split-at-head-swap"),
      Some(1),
      "a: lst|str -> Push a[1:], then a[0] onto the stack",
    ) { ctx ?=>
      ctx.pop() match
        case lst: VList => ctx.push(
            lst.drop(1),
            lst.headOption.getOrElse(ctx.settings.defaultValue),
          )
        case s: String =>
          ctx.push(s.drop(1), if s.isEmpty then "" else s.charAt(0).toString)
        case arg => throw UnimplementedOverloadException("Ḥ", List(arg))
    },
    addDirect(
      "ṫ",
      "Last Extract",
      List("last-extract", "split-at-last"),
      Some(1),
      "a: lst|str -> Push a[-1], then a[:-1] onto the stack",
    ) { ctx ?=>
      ctx.pop() match
        case lst: VList => ctx.push(
            lst.lastOption.getOrElse(ctx.settings.defaultValue),
            lst.dropRight(1),
          )
        case s: String =>
          ctx.push(if s.isEmpty then "" else s.last.toString, s.dropRight(1))
        case arg => throw UnimplementedOverloadException("ṫ", List(arg))
    },
    addPart(
      Monad,
      "ꜝ",
      "Increment",
      List("incr", "increment"),
      true,
      "a: num -> a + 1",
    ) {
      case a: VNum => a + 1
    },
    addPart(
      Monad,
      "…",
      "Increment Twice | Vectorised Head",
      List("incr-twice", "vec-head"),
      false,
      "a: num -> a + 2",
      "a: lst -> [x[0] for x in a]",
    ) {
      case a: VNum => a + 2
      case a: VList => VList.from(
          a.map(x =>
            ListHelpers
              .makeIterable(x)
              .headOption
              .getOrElse(MiscHelpers.defaultEmpty(x))
          )
        )
    },
    addPart(
      Dyad,
      "i",
      "Index | Collect Unique Application Values | Enclose",
      List("index", "at", "item-at", "nth-item", "collect-unique", "enclose"),
      false,
      "a: lst, b: num -> a[b]",
      "a: lst, b: lst -> a[_] for _ in b",
      "a: str, b: lst -> ''.join(a[i] for i in b)",
      "a: any, b: fun -> Apply b on a and collect unique values. Does include the initial value.",
      "a: str, b: str -> enclose b in a (a[0:len(a)//2] + b + a[len(a)//2:])",
    ) {
      case (a: VList, b: VList) => a.index(b)
      case (a: String, b: VList) =>
        val temp = ListHelpers.makeIterable(a).index(b)
        temp match
          case l: VList => l.mkString
          case _ => temp
      case (a: VList, b: String) =>
        val temp = ListHelpers.makeIterable(b).index(a)
        temp match
          case l: VList => l.mkString
          case _ => temp
      case (a, b: VFun) => MiscHelpers.collectUnique(b, a)
      case (a: VFun, b) => MiscHelpers.collectUnique(a, b)
      case (a: VNum, b) => ListHelpers.makeIterable(b).index(a)
      case (a, b: VNum) => ListHelpers.makeIterable(a).index(b)
      case (a: String, b: String) =>
        val temp = a.length / 2
        a.slice(0, temp) + b + a.slice(temp, a.length)
    },
    addPart(
      Dyad,
      "İ",
      "Index into Multiple | Collect While Unique | Complex Number",
      List("index-into-multiple", "collect-while-unique", "complex"),
      false,
      "a: num, b: num -> a.real + b.real * i",
      "a: any, b: lst -> `[a[item] for item in b]`",
      "a: any, b: fun -> Apply b on a and collect unique values (until fixpoint). Does not include the initial value.",
    ) {
      case (a: VNum, b: VNum) => VNum.complex(a.real, b.real)
      case (a, inds: VList) =>
        val lst = ListHelpers.makeIterable(a)
        inds.vmap(lst.index(_))
      case (init, fn: VFun) =>
        val prevVals = ArrayBuffer.empty[VAny]
        VList.from(LazyList.unfold(init) { prev =>
          val newVal = fn(prev)
          if prevVals.contains(newVal) then None
          else
            prevVals += newVal
            Some((newVal, newVal))
        })
    },
    addPart(
      Monad,
      "Ṫ",
      "Init",
      List("init", "remove-last"),
      false,
      "a: lst -> a[:-1]",
      "a: str -> a[:-1]",
    ) {
      case lst: VList => lst.dropRight(1)
      case s: String => s.dropRight(1)
    },
    addPart(
      Triad,
      "Ị",
      "Insert",
      List("insert", "insert-at"),
      false,
      "",
    ) {
      case (a, b: VNum, c) =>
        ListHelpers.insert(ListHelpers.makeIterable(a), b, c)
      case (a, b: VList, c) =>
        var temp = ListHelpers.makeIterable(a)
        for i <- b.reverse do
          i match
            case index: VNum => temp = ListHelpers.insert(temp, index, c)
            case _ => throw IllegalArgumentException(s"$i is not a number")
        temp
      case (a, b: VList, c: VList) =>
        var temp = ListHelpers.makeIterable(a)
        for (i, j) <- b.reverse.zip(c.reverse) do
          (i, j) match
            case (index: VNum, elem) =>
              temp = ListHelpers.insert(temp, index, elem)
            case _ => throw IllegalArgumentException(s"$i is not a number")
        temp
    },
    addPart(
      Dyad,
      "I",
      "Interleave",
      List("interleave"),
      false,
      "a: lst, b: lst -> Interleave a and b",
    ) {
      case (a, b) =>
        val temp = ListHelpers
          .interleave(ListHelpers.makeIterable(a), ListHelpers.makeIterable(b))
        if a.isInstanceOf[String] && b.isInstanceOf[String] then temp.mkString
        else temp
    },
    addPart(
      Monad,
      "e",
      "Is Even / Split on Newlines",
      List(
        "even?",
        "even",
        "is-even?",
        "split-on-newlines",
        "newline-split",
        "split-newlines",
      ),
      true,
      "a: num -> a % 2 == 0",
      "a: str -> a split on newlines",
    ) {
      case a: VNum => (a.underlying % 2) == VNum(0)
      case a: String => VList.from(a.split("\n").toSeq)
    },
    addDirect(
      "ṅ",
      "Join On Newlines | Pad Binary to Mod 8 | Context if 1",
      List(
        "join-newlines",
        "newline-join",
        "join-on-newlines",
        "binary-pad-8",
        "bin-pad-8",
        "one?->context",
        "one?->n",
      ),
      Some(1),
      "a: lst -> a join on newlines",
      "a: str -> a padded to a multiple of 8 with 0s",
      "a: num -> a if a == 1 push context variable n",
    ) { ctx ?=>
      ctx.pop() match
        case a: VList => ctx.push(a.mkString("\n"))
        case a: String =>
          val temp = a.length % 8
          ctx.push(if temp == 0 then a else ("0" * (8 - temp)) + a)
        case a: VNum => if a == VNum(1) then ctx.push(ctx.ctxVarPrimary)
        case _ => ???

    },
    addPart(
      Dyad,
      "j",
      "Join On",
      List("join-on", "join", "join-with", "join-by"),
      false,
      "a: lst, b: str|num -> a join on b",
      "a: lst, b: lst -> Intersperse elements of b within a",
    ) {
      case (a: VList, b) => ListHelpers.join(a, b)
      case (a, b: VList) => ListHelpers.join(b, a)
      case (a, b) => ListHelpers.join(ListHelpers.makeIterable(a), b) match
          case l: VList => l.mkString
          case res => res
    },
    addFull(
      Monad,
      "Ṅ",
      "Join on Nothing | First Positive Integer | Is Alphanumeric",
      List(
        "nothing-join",
        "concat-fold",
        "join-on-nothing",
        "empty-join",
        "single-string",
        "as-single-string",
        "first-positive-integer",
        "first-n>0",
        "is-alphanumeric",
        "is-alphanum",
        "is-alnum",
      ),
      false,
      "a: lst -> a join on nothing",
      "a: str -> is a alphanumeric?",
      "a: fun -> First positive integer ([1, 2, 3, ...]) for which a returns true",
    ) { a => MiscHelpers.joinNothing(a) },
    addPart(
      Monad,
      "L",
      "Length | Length of List",
      List("length", "len", "length-of", "len-of", "size"),
      false,
      "a: any -> Length of a",
    ) {
      case a: VList => a.length
      case a => ListHelpers.makeIterable(a).length
    },
    addFull(
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
        "vlen",
      ),
      false,
      "a: lst -> Length of each item in a",
    ) { a =>
      VList.from(
        ListHelpers.makeIterable(a).map(ListHelpers.makeIterable(_).length)
      )
    },
    addPart(
      Monad,
      "Ḷ",
      "Sort by Length",
      List(
        "sort-by-length",
        "sort-by-len",
        "order-by-length",
        "order-by-len",
        "length-sort",
        "len-sort",
      ),
      false,
      "a: lst -> sort a by length",
    ) {
      case a: VFun => ??? // Not implemented
      case a => ListHelpers.sortByLength(a)
    },
    addFull(
      Monad,
      "ι",
      "Length 0-Range",
      List("zero->len"),
      false,
      "a: any -> `[0, 1, 2, ..., len(a)-1]`",
    ) { a => range(0, ListHelpers.makeIterable(a).length - 1) },
    addPart(
      Monad,
      "κ",
      "Lenght 1-Range",
      List("one->len"),
      false,
      "a: any -> `[1, 2, 3, ..., len(a)]`",
    ) { case a => range(1, ListHelpers.makeIterable(a).length) },
    addPart(
      Dyad,
      "<",
      "Less Than",
      List("lt", "less", "less-than", "<", "less?", "smaller?"),
      true,
      "a: num, b: num -> a < b",
      "a: str, b: num -> a < str(b)",
      "a: num, b: str -> str(a) < b",
      "a: str, b: str -> a < b",
    ) { case (a: VVal, b: VVal) => a < b },
    addPart(
      Dyad,
      "≤",
      "Less Than Or Equal To",
      List("le", "less-than-or-equal-to"),
      true,
      "a: num, b: num -> a <= b",
      "a: str, b: num -> a <= str(b)",
      "a: num, b: str -> str(a) <= b",
      "a: str, b: str -> a <= b",
    ) { case (a: VVal, b: VVal) => a <= b },
    addPart(
      Dyad,
      "Y",
      "List Repeat",
      List("wrap-repeat"),
      false,
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
            case l: (String | VList) => ListHelpers.makeIterable(l).length
            case x => // (decidedly not a number, but a function)
              // todo(lyxal): Are we sure we don't want to convert to VNum or
              //              something instead of erroring?
              // @user: how you gonna convert something that isn't a VNum to
              // a VNum? You goofy. ~ lyxal
              throw IllegalArgumentException(
                s"Can't repeat an item a non-integer number of times (found $x in $b)"
              )
          }
          .lazyZip(ListHelpers.makeIterable(a))
          .map((n, item) => VList.fill(n)(item))
        if a.isInstanceOf[String] then temp.map(_.mkString).mkString
        else VList.from(temp)
      case _ => throw IllegalArgumentException(
          "Can't repeat an item a non-integer number of times"
        )
    },
    addPart(
      Dyad,
      "Ŀ",
      "Logarithm | Scan Fixpoint | Same Length? | Length Equals?",
      List(
        "log",
        "logarithm",
        "scan-fixpoint",
        "scan-fix",
        "same-length?",
        "same-length",
        "length-equals?",
        "length-equals",
        "len-eq?",
      ),
      true,
      "a: num, b: num -> log_b(a)",
      "a: fun, b: any -> apply until a previous value is repeated, collecting intermediate results",
      "a: str, b: str -> a same length as b",
      "a: str, b: num -> len(a) == b",
    ) {
      case (a: VNum, b: VNum) => NumberHelpers.log(a, b)
      case (a: String, b: VNum) => a.length == b.toInt
      case (a: String, b: String) => a.length == b.length
      case (a: VNum, b: String) => b.length == a.toInt
      case (a: VVal, b: VFun) =>
        val prevVals = ArrayBuffer.empty[VAny]
        VList.from(LazyList.unfold(a: VAny) { prevVal =>
          val next = b(prevVal)
          if prevVals.contains(next) then None
          else
            prevVals += next
            Some(next -> next)
        })

    },
    addDirect(
      "#X",
      "Loop Break",
      List("break"),
      Some(0),
      " -> break out of the current loop",
    ) { throw BreakLoopException() },
    addDirect(
      "#x",
      "Loop Continue",
      List("continue"),
      Some(0),
      " -> continue the current loop",
    ) { throw ContinueLoopException() },
    addPart(
      Dyad,
      "M",
      "Map Function | Mold Lists | Multiplicity",
      List("map", "mold", "multiplicity", "times-divide"),
      false,
      "a: any, b: fun -> a.map(b)",
      "a: fun, b: any -> b.map(a)",
      "a: lst, b: lst -> a molded to the shape of b",
      "a: num, b: num -> how many times b divides a",
    ) {
      case (a: VList, b: VList) => ListHelpers.mold(a, b)
      case (a: VNum, b: VNum) => NumberHelpers.multiplicity(a, b)
      case (a, b: VFun) =>
        ListHelpers.map(b, ListHelpers.makeIterable(a, Some(true)))
      case (a: VFun, b) =>
        ListHelpers.map(a, ListHelpers.makeIterable(b, Some(true)))
    },
    addDirect(
      "G",
      "Monadic Maximum | Dyadic Maximum | Generate From Function | Vectorised Maximum",
      List("max", "maximum", "generator"),
      Some(2),
      "a: lst -> Maximum of a",
      "a: non-lst, b: non-lst -> Maximum of a and b",
      "a: lst, b: fun -> Call b infinitely with items of a as starting values",
    ) { ctx ?=>
      val top = ctx.pop()
      top match
        case a: VList => ctx.push(a.maxOption.getOrElse(VList()))
        case _ =>
          val next = ctx.pop()
          (top, next) match
            case (a: VFun, b: VList) => ctx.push(ListHelpers.generate(a, b))
            case (a: VVal, b: VList) =>
              ctx.push(ListHelpers.vectorisedMaximum(b, a))
            case (a: VVal, b: VVal) => ctx.push(MiscHelpers.dyadicMaximum(a, b))
            case _ => throw Exception("Invalid arguments for maximum")
    },
    addDirect(
      "Ɠ",
      "Maximum without popping",
      List("max-no-pop"),
      Some(1),
      "a: lst -> max(a) without popping a",
    ) { ctx ?=>
      ctx.push(ListHelpers.makeIterable(ctx.peek).maxOption.getOrElse(VList()))
    },
    addDirect(
      "ɠ",
      "Minimum without popping",
      List("min-no-pop"),
      Some(1),
      "a: lst -> min(a) without popping a",
    ) { ctx ?=>
      ctx.push(ListHelpers.makeIterable(ctx.peek).minOption.getOrElse(VList()))
    },
    addPart(
      Dyad,
      "J",
      "Merge",
      List("merge"),
      false,
      "a: lst, b: lst -> Merge a and b",
      "a: any, b: lst -> Prepend a to b",
      "a: lst, b: any -> Append b to a",
      "a: num, b: num -> num(str(a) + str(b))",
      "a: any, b: any -> str(a) + str(b)",
    ) {
      case (a: VList, b: VList) => VList.from(a ++ b)
      case (a, b: VList) => VList.from(a +: b)
      case (a: VList, b) => VList.from(a :+ b)
      case (a: VNum, b: VNum) => MiscHelpers.eval(a.toString + b.toString)
      case (a, b) => a.toString + b.toString
    },
    addPart(
      Monad,
      "ṁ",
      "Mirror",
      List("mirror"),
      false,
      "num a: a + reversed(a) (as number)",
      "str a: a + reversed(a)",
      "lst a: append reversed(a) to a",
    ) {
      case a: VNum =>
        val temp = a.toString
        val reversed =
          if temp.startsWith("-") then temp + temp.reverse.tail
          else temp.reverse
        a + VNum(reversed)
      case a: String => a + a.reverse
      case a: VList => VList.from(a ++ a.reverse)
    },
    addPart(
      Dyad,
      "Ṁ",
      "Modular | Matrix Multiply | Regex Full Match?",
      List(
        "nth-items",
        "modular",
        "maxtrix-multiply",
        "mat-multiply",
        "mat-mul",
        "regex-full-match?",
        "full-match?",
      ),
      false,
      "a: str|lst, b: num -> return every b-th element of a. If b is zero, mirror: prepend a to its reverse.",
      "a: num, b: str|lst -> return every a-th element of b. If a is zero, mirror: append b to its reverse.",
      "a: lst, b: lst -> a * b (matrix multiply)",
      "a: str, b: str -> does the entirety of a match b?",
    ) {
      case (a: (VList | String), b: VNum) => ListHelpers.nthItems(a, b)
      case (a: VNum, b: (VList | String)) => ListHelpers.nthItems(b, a)
      case (a: VList, b: VList) => ListHelpers.matrixMultiply(a, b)
      case (a: String, b: String) => a.r.matches(b)
    },
    addDirect(
      "g",
      "Monadic Minimum | Dyadic Minimum | Generate From Function (Dyadic) | Vectorised Minimum",
      List("min", "minimum", "generator-dyadic"),
      Some(2),
      "a: lst -> Minimum of a",
      "a: non-lst, b: non-lst -> Minimum of a and b",
      "a: lst, b: fun -> Call b infinitely with items of a as starting values (dyadic)",
    ) { ctx ?=>
      val top = ctx.pop()
      top match
        case a: VList => ctx.push(a.minOption.getOrElse(VList()))
        case _ =>
          val next = ctx.pop()
          (top, next) match
            case (a: VFun, b: VList) =>
              ctx.push(ListHelpers.generateDyadic(a, b))
            case (a: VVal, b: VList) =>
              ctx.push(ListHelpers.vectorisedMinimum(b, a))
            case (a: VVal, b: VVal) => ctx.push(MiscHelpers.dyadicMinimum(a, b))
            case _ => throw Exception("Invalid arguments for mimimum")
    },
    addFull(
      Dyad,
      "%",
      "Modulo | String Formatting",
      List("mod", "modulo", "str-format", "format", "%", "strfmt"),
      false,
      "a: num, b: num -> a % b",
      "a: str, b: any -> a.format(b) (replace %s with b if scalar value or each item in b if vector)",
    )(MiscHelpers.modulo),
    addFull(
      Dyad,
      "×",
      "Multiplication",
      List("mul", "multiply", "times", "str-repeat", "*", "ring-trans"),
      true,
      "a: num, b: num -> a * b",
      "a: num, b: str -> b repeated a times",
      "a: str, b: num -> a repeated b times",
      "a: str, b: str -> ring translate a according to b",
    )(MiscHelpers.multiply),
    addPart(
      Dyad,
      "ċ",
      "N Choose K | Character Set Equal?",
      List(
        "n-choose-k",
        "ncr",
        "nck",
        "choose",
        "char-set-equal?",
        "char-set-eq?",
      ),
      true,
      "a: num, b: num -> a choose b",
      "a: str, b: str -> are the character sets of a and b equal?",
    ) {
      case (a: VNum, b: VNum) =>
        if a > b then NumberHelpers.nChooseK(a, b) else 0
      case (a: String, b: String) => a.toSet == b.toSet
    },
    addPart(
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
        "first>-1",
      ),
      true,
      "a: num -> -a",
      "a: str -> a.swapCase()",
      "a: fun -> first non-negative integer where predicate a is true",
    ) {
      case a: VNum => -a
      case a: String => a.map(c => if c.isUpper then c.toLower else c.toUpper)
      case a: VFun => MiscHelpers.firstNonNegative(a)
    },
    addPart(
      Monad,
      "¬",
      "Logical Not",
      List("non-vec-not", "non-vec-logical-not"),
      false,
      "a: any -> !a",
    ) { a =>
      VNum(!a.toBool)
    },
    addPart(
      Dyad,
      "∧",
      "Logical And",
      List("and", "logical-and"),
      true,
      "a: any, b: any -> a && b",
    ) { (a, b) =>
      if a.toBool then b else a
    },
    addPart(
      Dyad,
      "∨",
      "Logical Or",
      List("or", "logical-or"),
      true,
      "a: any, b: any -> a || b",
    ) { (a, b) =>
      if a.toBool then a else b
    },
    addPart(
      Monad,
      "O",
      "Ord/Chr",
      List("ord", "chr"),
      false,
      "a: str -> ord(a)",
      "a: num -> chr(a)",
    ) {
      case a: VNum => StringHelpers.chrord(a)
      case a: String => StringHelpers.chrord(a)
      case a: VList =>
        val temp = a.map(StringHelpers.chrord)
        if temp.forall(_.isInstanceOf[String]) then temp.mkString
        else VList(temp*)
    },
    addDirect(
      "Ȯ",
      "Over",
      List("over"),
      Some(0),
      "_ -> push a copy of the second item on the stack over the first",
      "a b -> a b a",
    ) { ctx ?=>
      val top = ctx.pop()
      val next = ctx.pop()
      ctx.push(next)
      ctx.push(top)
      ctx.push(next)
    },
    addDirect(
      "o",
      "Overlap | Overlapping Slices",
      List("overlap", "overlaps", "overlapping", "overlapping-slices"),
      Some(2),
      "a: lst, b: num -> Overlapping slices of a of length b",
      "a: lst|str -> Overlapping slices of a of length 2",
    ) { ctx ?=>
      val top = ctx.pop()
      top match
        case a: VList => ctx.push(VList.from(ListHelpers.overlaps(a, 2)))
        case a: String => ctx.push(VList.from(ListHelpers.overlaps(a, 2)))
        case _ =>
          val next = ctx.pop()
          (top, next) match
            case (a: VNum, b: String) =>
              ctx.push(VList.from(ListHelpers.overlaps(b, a.toInt)))
            case (a: VNum, b: VList) =>
              ctx.push(VList.from(ListHelpers.overlaps(b.lst, a.toInt)))
            case _ => throw Exception("Invalid arguments for overlaps")
    },
    addFull(Dyad, ";", "Pair", List("pair"), false, "a, b -> [a, b]") {
      VList(_, _)
    },
    addPart(
      Monad,
      "Ṗ",
      "Permutations",
      List("permutations", "perms"),
      false,
      "a: lst -> Permutations of a",
    ) {
      case a: VNum => VList.from(
          ListHelpers
            .permutations(ListHelpers.makeIterable(a))
            .map(n => MiscHelpers.eval(n.mkString))
        )
      case a: VList => VList.from(ListHelpers.permutations(a))
      case a: String => VList.from(
          ListHelpers.permutations(ListHelpers.makeIterable(a)).map(_.mkString)
        )
    },
    addPart(
      Monad,
      "P",
      "Prefixes",
      List("prefixes"),
      false,
      "a: lst -> Prefixes of a",
    ) {
      case a: VList => VList.from(ListHelpers.prefixes(a))
      case a: String => VList.from(
          ListHelpers.prefixes(ListHelpers.makeIterable(a)).map(_.mkString)
        )
      case a: VNum => VList.from(
          ListHelpers
            .prefixes(ListHelpers.makeIterable(a.vabs))
            .map(n => MiscHelpers.eval(n.mkString))
        )
    },
    addPart(
      Dyad,
      "p",
      "Prepend",
      List("prepend"),
      false,
      "a: lst, b: any -> b prepended to a",
    ) {
      case (a: String, b: (String | VNum)) => b.toString + a
      case (a: VNum, b: String) => b + a.toString
      case (a: VNum, b: VNum) => MiscHelpers.eval(b.toString + a.toString)
      case (a: VList, b) => VList.from(b +: a)
      case (a, b) => VList(b, a)
    },
    addPart(
      Monad,
      "ḟ",
      "Prime Factors | Remove Non-Alphabet",
      List("prime-factors", "remove-non-alphabet"),
      true,
      "a: num -> prime factors of a",
      "a: str -> a with all non-alphabet characters removed",
    ) {
      case a: VNum => NumberHelpers.primeFactors(a)
      case a: String => StringHelpers.removeNonAlphabet(a)
    },
    addDirect(
      ",",
      "Print",
      List("print", "puts", "out", "println"),
      None,
      "a -> printed to stdout",
    ) { ctx ?=> MiscHelpers.vyPrintln(ctx.pop()) },
    addDirect(
      "§",
      "Print without newline",
      List("print-no-newline"),
      None,
      "a -> printed to stdout without newline",
    ) { ctx ?=> MiscHelpers.vyPrint(ctx.pop()) },
    addDirect(
      "Ọ",
      "Print without popping",
      List("print-no-pop"),
      None,
      "a -> printed to stdout without popping",
    ) { ctx ?=> MiscHelpers.vyPrintln(ctx.peek) },
    addPart(
      Monad,
      "q",
      "Quotify",
      List("quotify"),
      false,
      "a: any -> enclose a in quotes, escape backslashes and quote marks",
    ) {
      case a: String => StringHelpers.quotify(a)
      case a => StringHelpers.quotify(a.toString)
    },
    addPart(
      Monad,
      "ė",
      "Reciprocal | Remove Whitespace",
      List("reciprocal", "recip", "remove-whitespace", "remove-space", "1/"),
      true,
      "a: num -> 1/a",
      "a: str -> a with all whitespace removed",
    ) {
      case a: VNum => 1 / a
      case a: String => a.replaceAll("\\s", "")
    },
    addFull(
      Monad,
      "ġ",
      "Group By Consecutive Items",
      List("group-by-consecutive"),
      false,
      "a: any -> group consecutive identical items of lst(a)",
    ) { lst =>
      val it = ListHelpers.makeIterable(lst).iterator
      def gen(first: VAny): LazyList[VList] =
        val buf = ListBuffer(first)
        while it.hasNext do
          val next = it.next()
          if next == first then buf.append(next)
          else return VList.from(buf.toList) #:: gen(next)
        LazyList(VList.from(buf.toList))

      val res = if it.hasNext then gen(it.next()) else Seq.empty
      lst match
        case _: String => VList.from(res.map(_.mkString))
        case _ => VList.from(res)
    },
    addPart(
      Monad,
      "ṗ",
      "List Partitions | Integer Partitions",
      List(
        "list-partitions",
        "list-parts",
        "integer-partitions",
        "int-partitions",
        "int-parts",
      ),
      false,
      "a: lst -> List partitions of a",
      "a: num -> Integer partitions of a (all possible ways to sum to a)",
    ) {
      case a: VList => ListHelpers.partitions(a)
      case n: VNum => NumberHelpers.partitions(n)
    },
    addFull(
      Dyad,
      "ƒ",
      "Partition After Truthy Indices",
      List("partition-after-truthy"),
      false,
      "a: lst, b: lst -> partition a after truthy indices in b",
    ) { case (a, b) => ListHelpers.partitionAfterTruthyIndices(a, b) },
    addDirect(
      "x",
      "Recursion | Recurse",
      List("recurse"),
      None,
      " -> call the current function recursively",
    ) { ctx ?=>
      if ctx.globals.callStack.isEmpty then
        Interpreter.execute(ctx.globals.originalProgram)(using ctx)
      else
        ctx.push(
          Interpreter.executeFn(ctx.globals.callStack.top)(using
            ctx.makeChild()
          )
        )
    },
    addPart(
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
        "fold",
      ),
      false,
      "a: fun, b: any -> reduce iterable b by function a",
      "a: any, b: fun -> reduce iterable a by function b",
      "a: num, b: num -> the range [a, b)",
      "a: str, b: num|str -> does regex pattern b match haystack a?",
    ) {
      case (a: VNum, b: VNum) => NumberHelpers.range(a, b).dropRight(1)
      case (a: String, b: String) => b.r.findFirstIn(a).isDefined
      case (a: String, b: VNum) => b.toString.r.findFirstIn(a).isDefined
      case (a: VNum, b: String) => b.r.findFirstIn(a.toString).isDefined
      case (a: VFun, b) => ListHelpers.reduce(b, a)
      case (a, b: VFun) => ListHelpers.reduce(a, b)
    },
    addPart(
      Triad,
      "r",
      "Replace",
      List("replace"),
      false,
      "a: str, b: str, c: str -> replace all instances of b in a with c",
    ) {
      case (a: VList, b, c) =>
        VList.from(a.lst.map(x => if x == b then c else x))
      case (a, b: VList, c: VList) =>
        VList.from(b.lst.map(x => if x == a then c else x))
      case (a, b, c: VList) =>
        VList.from(c.lst.map(x => if x == a then b else x))
      case (a, b: VList, c) =>
        VList.from(b.lst.map(x => if x == a then c else x))
      case (a: String, b: VVal, c: VVal) => a.replace(b.toString, c.toString)
      case (a: VNum, b: VVal, c: VVal) =>
        MiscHelpers.eval(a.toString().replace(b.toString, c.toString))
    },
    addPart(
      Monad,
      "Ṛ",
      "Reverse",
      List("reverse"),
      false,
      "a: any -> reverse a",
    ) { a => ListHelpers.reverse(a) },
    addDirect(
      "X",
      "Return Statement",
      List("return", "ret"),
      None,
      "a -> return a",
    ) { throw ReturnFromFunctionException() },
    addFull(
      Monad,
      "S",
      "Sort ascending",
      List("sort", "sortasc", "sort-asc"),
      false,
      "a: any -> convert to list and sort ascending",
    ) {
      // should do something else for num overload later
      case s: String => s.sorted
      case a => VList
          .from(ListHelpers.makeIterable(a).sorted(MiscHelpers.compare(_, _)))
    },
    addDirect(
      "Ṙ",
      "Rotate Left",
      List("abc->bca", "rot-left", "rotate-left"),
      Some(1),
      "a: any -> rotate left once",
    ) { ctx ?=>
      val original = ctx.pop()
      val a = ListHelpers.makeIterable(original)
      val temp = VList.from(a.tail :+ a.head)
      original match
        case _: String => ctx.push(temp.mkString)
        case _: VNum => ctx.push(VNum(temp.mkString))
        case _ => ctx.push(temp)
    },
    addPart(
      Monad,
      "ṙ",
      "Rotate Right",
      List("abc->cab", "rot-right", "rotate-right"),
      false,
      "a: any -> rotate right once",
    ) {
      case a: String => s"${a.last}${a.dropRight(1)}"
      case a: VNum => VNum(s"${a.toString.last}${a.toString.dropRight(1)}")
      case a: VList => VList.from(a.lst.last +: a.lst.dropRight(1))

    },
    addPart(
      Monad,
      "±",
      "Sign",
      List("sign"),
      true,
      "a: num -> sign of a",
    ) {
      case a: VNum => a.signum
    },
    addPart(
      Dyad,
      "ṡ",
      "Sort by Function Object | Partition by Numbers",
      List(
        "sort-by",
        "sortby",
        "sort-by-fun",
        "sortbyfun",
        "sort-fun",
        "sortfun",
        "partition-by",
      ),
      false,
      "a: fun, b: any -> sort iterable b by function a",
      "a: any, b: fun -> sort iterable a by function b",
      "a: lst, b: lst[num] -> partition a into sublists of length items in b",
    ) {
      case (a: VFun, b) =>
        ListHelpers.sortBy(ListHelpers.makeIterable(b, Some(true)), a)
      case (a, b: VFun) =>
        ListHelpers.sortBy(ListHelpers.makeIterable(a, Some(true)), b)
      case (a: VList, b: VList) =>
        if b.lst.forall(_.isInstanceOf[VNum]) then
          ListHelpers.partitionBy(a, b.lst.map(_.asInstanceOf[VNum]))
        else ???
    },
    addPart(
      Dyad,
      "s",
      "Split",
      List("split"),
      false,
      "a: any, b: any -> split a by b",
    ) {
      case (a: String, b) =>
        if b.isInstanceOf[String] && b.toString.isEmpty then
          ListHelpers.makeIterable(a)
        else VList.from(a.split(b.toString()).toSeq)
      case (a: VNum, b) =>
        VList.from(a.toString().split(b.toString()).toSeq.map(MiscHelpers.eval))
      case (a: VList, b) => ListHelpers.splitNormal(a, b)
    },
    addPart(
      Monad,
      "Ṣ",
      "Sublists",
      List("sublists"),
      false,
      "a: lst -> sublists of a",
    ) {
      case a: (VVal | VList) => VList.from(
          ListHelpers.mergeInfLists(
            ListHelpers
              .prefixes(ListHelpers.makeIterable(a))
              .map(b => ListHelpers.suffixes(ListHelpers.makeIterable(b)))
          )
        )
    },
    addPart(
      Monad,
      "€",
      "Suffixes",
      List("suffixes"),
      false,
      "a: lst -> Suffixes of a",
    ) {
      case a: VList => VList.from(ListHelpers.suffixes(a))
      case a: String => VList.from(
          ListHelpers.suffixes(ListHelpers.makeIterable(a)).map(_.mkString)
        )
      case a: VNum => VList.from(
          ListHelpers
            .suffixes(ListHelpers.makeIterable(a.vabs))
            .map(n => MiscHelpers.eval(n.mkString))
        )
    },
    addPart(
      Monad,
      "√",
      "Square Root",
      List("sqrt", "square-root"),
      true,
      "a: num -> sqrt(a)",
    ) {
      case a: VNum => VNum.complex(a.real.sqrt, a.imag.sqrt)
    },
    addPart(
      Monad,
      "⁺",
      "Square | Pairs",
      List("square", "pairs"),
      true,
      "a: num -> a ** 2",
      "a: str -> a split into pairs",
    ) {
      case a: VNum => a ** 2
      case a: String => VList.from(a.grouped(2).toSeq)
    },
    addPart(
      Monad,
      "⁻",
      "Cube | Threes",
      List("cube", "threes"),
      true,
      "a: num -> a ** 3",
      "a: str -> a split into chunks of length 3",
    ) {
      case a: VNum => a ** 3
      case a: String => VList.from(a.grouped(3).toSeq)
    },
    addPart(
      Monad,
      "ɾ",
      "Inclusive One Range | Uppercase",
      List("one->n", "one-range", "to-upper", "upper", "uppercase"),
      true,
      "a: num -> [1..a]",
      "a: str -> a.upper()",
    ) {
      case a: VNum => NumberHelpers.range(1, a)
      case a: String => a.toUpperCase
    },
    addPart(
      Monad,
      "ʀ",
      "Exclusive Zero Range | Lowercase",
      List(
        "0->n",
        "zero-range",
        "lowered-range",
        "to-lower",
        "lower",
        "lowercase",
      ),
      true,
      "a: num -> [0..a)",
      "a: str -> a.lower()",
    ) {
      case a: VNum => NumberHelpers.range(0, a - 1)
      case a: String => a.toLowerCase
    },
    addFull(
      Monad,
      "ᶲ",
      "Stringify",
      List("to-string", "stringify", "str"),
      false,
      "a: any -> str(a)",
    ) { a =>
      a.toString
    },
    addPart(
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
      true,
      "a: num, b: num -> a - b",
      "a: str, b: num -> a + b '-'s (or '-'s + a if b < 0)",
      "a: num, b: str -> a '-'s + b (or b + '-'s if a < 0)",
      "a: str, b: str -> a with b removed",
    ) {
      case (a: (VNum | String), b: (VNum | String)) =>
        MiscHelpers.subtract(a, b)
    },
    addPart(
      Monad,
      "∑",
      "Sum",
      List("sum", "/+", "+/"),
      false,
      "a: lst -> sum of a",
    ) { case a => ListHelpers.sum(ListHelpers.makeIterable(a)) },
    addDirect("$", "Swap", List("swap"), None, "a, b -> b, a") { ctx ?=>
      val b, a = ctx.pop()
      ctx.push(b, a)
    },
    addFull(
      Monad,
      "t",
      "Tail | Last Item",
      List("tail", "last", "last-item"),
      false,
      "a: lst -> a[-1]",
    ) { a =>
      ListHelpers
        .makeIterable(a)
        .lastOption
        .getOrElse(MiscHelpers.defaultEmpty(a))
    },
    addPart(
      Dyad,
      "y",
      "To Base",
      List("to-base"),
      false,
      "a: num, b: num -> a in base b",
      "a: num, b: str|lst -> a in base with alphabet b",
      "a: lst, b: num -> each x in a in base b",
      "a: lst, b: str|lst -> each x in a in base with alphabet b",
    ) {
      case (a: VNum, b) => NumberHelpers.toBase(a, b)
      case (a: VList, b) => a.vmap(NumberHelpers.toBase(_, b))
    },
    addPart(
      Triad,
      "ŀ",
      "Transliterate | Call While",
      List("transliterate", "call-while"),
      false,
      "any a, any b, any c -> transliterate(a,b,c) (in a, replace b[0] with c[0], b[1] with c[1], b[2] with c[2], ...)",
      "a: fun, b: fun, c: any -> call b on c until a(c) is falsy",
    ) {
      case (
            a: String,
            b: (VList | VNum | String),
            c: (VList | VNum | String),
          ) => StringHelpers.transliterate(
          a,
          ListHelpers.makeIterable(b),
          ListHelpers.makeIterable(c),
        )
      case (p: VFun, f: VFun, v) => MiscHelpers.callWhile(p, f, v)
      case (p: VFun, v, f: VFun) => MiscHelpers.callWhile(p, f, v)
      case (v, p: VFun, f: VFun) => MiscHelpers.callWhile(p, f, v)
      case (a: VList, b, c) => ListHelpers.transliterate(a, b, c)
      case (a: VNum, b, c) =>
        val temp =
          ListHelpers.transliterate(ListHelpers.makeIterable(a), b, c).mkString
        if VNum.NumRegex.matches(temp) then VNum(temp) else temp
    },
    addPart(
      Dyad,
      "Ṭ",
      "Trim",
      List("trim"),
      false,
      "a: any, b: any -> Trim all elements of b from both sides of a.",
    ) {
      case (a: String, b: String) => a.stripPrefix(b).stripSuffix(b)
      case (a: String, b: VNum) =>
        a.stripPrefix(b.toString).stripSuffix(b.toString)
      case (a: VNum, b: String) =>
        VNum(a.toString.stripPrefix(b).stripSuffix(b))
      case (a: VNum, b: VNum) =>
        VNum(a.toString.stripPrefix(b.toString).stripSuffix(b.toString))
      case (a: VList, b: VList) => ListHelpers.trimList(a, b)
      case (a: VList, b) => ListHelpers.trim(a, b)
      case (a, b: VList) => ListHelpers.trim(b, a)
      case (a, b) => ListHelpers.trim(ListHelpers.makeIterable(a), b)
    },
    addPart(
      Dyad,
      "ẋ",
      "Cartesian Power",
      List("cartesian-power"),
      false,
      "a: lst, b: num -> cart_prod([a] * n)",
    ) {
      case (a, n: VNum) => ListHelpers.cartesianPower(a, n)
      case (n: VNum, a) => ListHelpers.cartesianPower(a, n)
    },
    addPart(
      Dyad,
      "⁾",
      "Surround | Character Multiply",
      List("surround", "character-multiply"),
      false,
      "a: num, b: str -> each character in b repeated a times",
      "a: any, b: any -> a prepended and appended to b",
    ) {
      case (a: VList, b) => VList.from((b +: a) :+ b)
      case (a: String, b: String) => b + a + b
      case (a, b: VList) => VList.from((a +: b) :+ a)
      case (a: VNum, b: String) => StringHelpers.characterMultiply(a, b)
      case (a: String, b: VNum) => StringHelpers.characterMultiply(b, a)
      case (a: VNum, b: VNum) => ??? // Doesn't say anything in info.txt
    },
    addPart(
      Monad,
      "ÞT",
      "Transpose Safe",
      List("transpose-safe"),
      false,
      "a: any -> transpose a",
    ) {
      case a: VFun =>
        throw RuntimeException(s"Can't transpose (ÞT) function: $a")
      case a => ListHelpers.transposeSafe(ListHelpers.makeIterable(a))
    },
    addPart(
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
      false,
      "a: num -> 3 * a",
      "a: str -> does a contain only alphabet characters?",
      "a: any -> transpose a",
    ) {
      case a: VNum => a * 3
      case a: String => a.forall(_.isLetter)
      case a: VList => ListHelpers.transpose(a)
    },
    addDirect("D", "Triplicate", List("trip"), None, "a -> [a, a, a]") { ctx ?=>
      val a = ctx.pop()
      ctx.push(a, a, a)
    },
    addPart(
      Monad,
      "E",
      "2 Power | Evaluate",
      List("two^", "two**", "eval"),
      true,
      "a: num -> 2^a",
      "a: str -> evaluate (not execute) a",
    ) {
      case a: VNum => VNum(2) ** a
      case a: String => MiscHelpers.eval(a)
    },
    addDirect(
      "U",
      "Uninterleave",
      List("uninterleave"),
      None,
      "a: any -> uninterleave a",
    ) { ctx ?=>
      val a = ctx.pop()
      val lst = ListHelpers.makeIterable(a)
      val (evens, odds) = lst.zipWithIndex.partition(_._2 % 2 == 0)
      // Make sure to preserve type
      val (pushEven, pushOdd) = a match
        case _: VList => VList.from(evens.map(_._1)) ->
            VList.from(odds.map(_._1))
        case _: VNum => MiscHelpers.eval(evens.map(_._1).mkString) ->
            MiscHelpers.eval(odds.map(_._1).mkString)
        case _: String => evens.map(_._1).mkString -> odds.map(_._1).mkString
        case _ =>
          throw RuntimeException("Uninterleave: Can't uninterleave functions")

      ctx.push(pushEven, pushOdd)
    },
    addPart(
      Monad,
      "Ḅ",
      "Unique Prime Factors | Case Of",
      List("unique-prime-factors", "case-of"),
      true,
      "a: num -> unique prime factors of a",
      "a: str -> case of each character of a (uppercase = 1, lowercase = 0)",
    ) {
      case a: VNum => NumberHelpers.primeFactors(a).distinct
      case a: String => StringHelpers.caseof(a)
    },
    addPart(
      Dyad,
      "Φ",
      "Slice from 1",
      List("1->b"),
      false,
      "a: lst, b: num -> a[1:b]",
      "a: num, b: lst -> b[1:a]",
    ) {
      case (a, b: VNum) => ListHelpers.makeIterable(a).slice(1, b.toInt)
      case (a: VNum, b) => ListHelpers.makeIterable(b).slice(1, a.toInt)
    },
    addPart(
      Monad,
      "u",
      "Uniquify",
      List("uniquify"),
      false,
      "a: lst|str|num -> a with duplicates removed",
    ) {
      case lst: VList => lst.distinct
      case n: VNum =>
        MiscHelpers.eval(ListHelpers.makeIterable(n).distinct.mkString)
      case s: String => s.distinct.mkString
    },
    addDirect(
      "#v",
      "[Internal Use] Vectorise (Element Form) ",
      List(),
      None,
      "*a, f -> f vectorised over however many arguments in a. It is recommended to use the modifier instead",
    ) { ctx ?=>
      // For sake of simplicity, error if not a function
      ctx.pop() match
        case f: VFun => FuncHelpers.vectorise(f)
        case _ => throw IllegalArgumentException(
            "Vectorise: First argument should be a function"
          )
    },
    addDirect(
      "#~",
      "[Internal Use] Apply Without Popping (Element Form)",
      List(),
      None,
      "*a, f -> f applied to the stack without popping items. Use the modifier instead.",
    ) { ctx ?=>
      ctx.pop() match
        case f: VFun =>
          val args = ctx.peek(f.arity)
          ctx.push(f(args*))
        case _ => throw IllegalArgumentException(
            "Apply Without Popping: First argument should be a function"
          )

    },
    addDirect(
      "#|map-suffixes",
      "[Internal Use] Map Suffixes (Element Form)",
      List(),
      None,
      "*a, f -> f applied to each suffix of a. Use the modifier instead.",
    ) { ctx ?=>
      ctx.pop() match
        case f: VFun =>
          val arg = ListHelpers.makeIterable(ctx.pop())
          val suffixes = ListHelpers.suffixes(arg)
          ctx.push(VList.from(suffixes.map(suffix => f(suffix))))

        case _ => throw IllegalArgumentException(
            "Map Suffixes: First argument should be a function"
          )
    },
    addDirect(
      "#|map-prefixes",
      "[Internal Use] Map Prefixes (Element Form)",
      List(),
      None,
      "*a, f -> f applied to each prefix of a. Use the modifier instead.",
    ) { ctx ?=>
      ctx.pop() match
        case f: VFun =>
          val arg = ListHelpers.makeIterable(ctx.pop())
          val prefixes = arg.indices.map(i => arg.slice(0, i + 1))
          ctx.push(VList.from(prefixes.map(prefix => f(prefix))))

        case _ => throw IllegalArgumentException(
            "Map Prefixes: First argument should be a function"
          )
    },
    addDirect(
      "#|reduce-cols",
      "[Internal Use] Reduce Columns (Element Form)",
      List(),
      None,
      "*a, f -> each column of a reduced by f. Use the modifier instead.",
    ) { ctx ?=>
      ctx.pop() match
        case f: VFun =>
          val arg = ListHelpers.makeIterable(ctx.pop())
          val cols = ListHelpers.transpose(arg)
          ctx.push(
            VList.from(cols.map(col => ListHelpers.reduce(col, f, None)))
          )
        case _ => throw IllegalArgumentException(
            "Reduce Columns: First argument should be a function"
          )
    },
    addDirect(
      "#|maximum-by",
      "[Internal Use] Maximum By (Element Form)",
      List(),
      None,
      "*a, f -> maximum of a by f. Use the modifier instead.",
    ) { ctx ?=>
      ctx.pop() match
        case f: VFun =>
          val arg = ListHelpers.makeIterable(ctx.pop())
          ctx.push(arg.maxBy(v => f(v)))
        case _ => throw IllegalArgumentException(
            "Maximum By: First argument should be a function"
          )
    },
    addDirect(
      "#|minimum-by",
      "[Internal Use] Minimum By (Element Form)",
      List(),
      None,
      "*a, f -> minimum of a by f. Use the modifier instead.",
    ) { ctx ?=>
      ctx.pop() match
        case f: VFun =>
          val arg = ListHelpers.makeIterable(ctx.pop())
          ctx.push(arg.minBy(v => f(v)))
        case _ => throw IllegalArgumentException(
            "Maximum By: First argument should be a function"
          )
    },
    addPart(
      Monad,
      "V",
      "Vectorised Reverse | Complement | Title Case",
      List(
        "vectorised-reverse",
        "vec-reverse",
        "complement",
        "titlecase",
        "title-case",
      ),
      false,
      "a: lst -> each element of a reversed",
      "a: num -> 1 - a",
      "a: str -> a converted to title case",
    ) {
      case a: VList => VList.from(a.map(ListHelpers.reverse))
      case a: VNum => 1 - a
      case a: String => StringHelpers.titlecase(a)
    },
    addPart(
      Monad,
      "Ṡ",
      "Vectorised Sums",
      List("vectorised-sums", "vec-sums"),
      false,
      "a: lst -> sum of each element of a",
    ) {
      case a: VList => a.vmap(x => ListHelpers.sum(ListHelpers.makeIterable(x)))
    },
    addDirect(
      "W",
      "Wrap",
      List("wrap"),
      None,
      "a, b, c, ..., -> [a, b, c, ...]",
    ) { ctx ?=> ctx.wrap() },
    addFull(
      Monad,
      "w",
      "Wrap Singleton",
      List("wrap-singleton", "enlist"),
      false,
      "a -> [a]",
    ) { a => VList(a) },
    addPart(
      Dyad,
      "Ẇ",
      "Wrap to Length | Predicate Slice From 0",
      List("wrap-length", "pred-slice-0"),
      false,
      "a: lst, b: num -> a wrapped in chunks of length b",
      "a: fun, b: num -> first b truthy integers where a is truthy",
    ) {
      case (a: VList, b: VNum) => ListHelpers.wrapLength(a, b)
      case (a: String, b: VNum) =>
        if b <= 0 then VList.empty
        else VList.from(a.grouped(b.toInt).toSeq)
      case (a: VNum, b: String) =>
        if a <= 0 then VList.empty
        else VList.from(b.grouped(a.toInt).toSeq)
      case (a: VNum, b: VList) => ListHelpers.wrapLength(b, a)
      case (a: VFun, b: VNum) => MiscHelpers.predicateSlice(a, b, 0)
      case (a: VNum, b: VFun) => MiscHelpers.predicateSlice(b, a, 0)
    },
    addPart(
      Monad,
      "z",
      "Inclusive zero Range | Is Lowercase",
      List(
        "inclusive-zero-range",
        "zero->n",
        "is-lowercase?",
        "lowercase?",
        "lower?",
      ),
      true,
      "a: num -> [0, 1, ..., a]",
      "a: str -> is a lowercase?",
    ) {
      case a: VNum => NumberHelpers.range(0, a)
      case a: String =>
        if a.length == 1 then a.forall(_.isLower)
        else VList.from(a.map(x => VNum(x.isLower)))
    },
    addPart(
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
        "first-n",
      ),
      false,
      "a: lst, b: num -> [a[0], a[1], ..., a[b-1]]",
    ) {
      case (a, b: VNum) => ListHelpers.makeIterable(a, Some(true)).take(b.toInt)
    },
    addPart(
      Dyad,
      "Z",
      "Zip",
      List("zip", "zip-map"),
      false,
      "a: lst, b: lst -> zip a and b",
      "a: lst, b: fun -> [[x, b(x)] for x in a]",
      "a: fun, b: lst -> [[a(x), x] for x in b]",
    ) {
      case (a: VFun, b: VFun) => ??? // todo(lyxal) overload for two functions
      case (a, b: VFun) =>
        val iter = ListHelpers.makeIterable(a)
        VList.from(iter.vzip(ListHelpers.map(b, iter)))
      case (a: VFun, b) =>
        val iter = ListHelpers.makeIterable(b)
        VList.from(ListHelpers.map(a, iter).vzip(iter))
      case (a, b) =>
        ListHelpers.makeIterable(a).vzip(ListHelpers.makeIterable(b))
    },
    addDirect(
      "£",
      "Set Register",
      List("set-register"),
      Some(1),
      "a: any -> register = a"
    ){ctx ?=>
      ctx.globals.register = ctx.pop()
    },
    addDirect(
      "¥",
      "Get Register",
      List("get-register"),
      Some(0),
      " -> push the value of the register"
    ){
      ctx ?=> ctx.push(ctx.globals.register)
    },
    addDirect(
      "←",
      "Rotate Stack Left",
      List("rotate-stack-left"),
      None,
      " -> rotate the entire stack left once"
    ){
      ctx ?=>
        val a = ctx.pop()
        ctx.push(ctx.stack :+ a)
    }

    // Constants
    ,
    addNilad("¦", "Pipe", List("pipe"), "|") { "|" },
    addNilad("ð", "Space", List("space"), " ") { " " },
    addNilad("¶", "Newline", List("newline"), "\n") { "\n" },
    addNilad("•", "Asterisk", List("asterisk"), "*") { "*" },
    addNilad("₀", "Ten", List("ten"), "10") { 10 },
    addNilad("₁", "Sixteen", List("sixteen"), "16") { 26 },
    addNilad("₂", "Twenty-six", List("twenty-six"), "26") { 26 },
    addNilad("₃", "Thirty-two", List("thirty-two"), "32") { 32 },
    addNilad("₄", "Sixty-four", List("sixty-four"), "64") { 64 },
    addNilad("₅", "One hundred", List("one-hundred"), "100") { 100 },
    addNilad(
      "₆",
      "One hundred twenty-eight",
      List("one-hundred-twenty-eight"),
      "128",
    ) { 128 },
    addNilad(
      "₇",
      "Two hundred fifty-six",
      List("two-hundred-fifty-six"),
      "256",
    ) { 256 },
    addNilad(
      "₈",
      "Alphabet",
      List("alphabet", "a-z"),
      "\"abcdefghijklmnopqrstuvwxyz\"",
    ) { "abcdefghijklmnopqrstuvwxyz" },
    addNilad(
      "₉",
      "Empty array",
      List("empty-list", "nil-list", "new-list"),
      "[]",
    ) { VList.empty },
  )

  private def execHelper(value: VAny)(using ctx: Context): VAny =
    value match
      case code: String =>
        Interpreter.execute(code)
        ctx.pop()
      case n: VNum => 10 ** n
      case list: VList => list.vmap(execHelper)
      case fn: VFun =>
        val res = Interpreter.executeFn(fn)
        if fn.arity == -1 then
          ctx.pop() // Handle the extra value pushed by lambdas that operate on the stack
        res

  private def addNilad(
      symbol: String,
      name: String,
      keywords: Seq[String],
      desc: String,
  )(impl: Context ?=> VAny): (String, Element) =
    symbol ->
      Element(
        symbol,
        name,
        keywords,
        Some(0),
        false,
        List(s"-> $desc"),
        () => ctx ?=> ctx.push(impl(using ctx)),
      )

  /** Add an element that handles all `VAny`s (it doesn't take a
    * `PartialFunction`, hence "Full")
    */
  private def addFull[F](
      helper: ImplHelpers[?, F],
      symbol: String,
      name: String,
      keywords: Seq[String],
      vectorises: Boolean,
      overloads: String*
  )(impl: F): (String, Element) =
    symbol ->
      Element(
        symbol,
        name,
        keywords,
        Some(helper.arity),
        vectorises,
        overloads,
        helper.toDirectFn(impl),
      )

  /** Define an element that doesn't necessarily work on all inputs
    *
    * If using this method, make sure to use `case` to define the function,
    * since it needs a `PartialFunction`. If it is possible to define it using a
    * normal function literal or it covers every single case, then try
    * [[addFull]] instead.
    */
  private def addPart[P, F](
      helper: ImplHelpers[P, F],
      symbol: String,
      name: String,
      keywords: Seq[String],
      vectorises: Boolean,
      overloads: String*
  )(impl: P): (String, Element) =
    symbol ->
      Element(
        symbol,
        name,
        keywords,
        Some(helper.arity),
        vectorises,
        overloads,
        helper.toDirectFn(
          if vectorises then helper.vectorise(symbol)(impl)
          else helper.fill(symbol)(impl)
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
      helper: ImplHelpers[P, F],
      symbol: String,
      name: String,
      keywords: Seq[String],
      overloads: String*
  )(impl: P): (String, Element) =
    symbol ->
      Element(
        symbol,
        name,
        keywords,
        Some(helper.arity),
        true,
        overloads,
        helper.toDirectFn(helper.fill(symbol)(impl)),
      )

  /** Add an element that works directly on the entire stack */
  private def addDirect(
      symbol: String,
      name: String,
      keywords: Seq[String],
      arity: Option[Int],
      overloads: String*
  )(impl: Context ?=> Unit): (String, Element) =
    symbol ->
      Element(symbol, name, keywords, arity, false, overloads, () => impl)
end Elements
