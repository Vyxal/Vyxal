package vyxal

import scala.language.implicitConversions

import vyxal.ListHelpers.makeIterable
import vyxal.MiscHelpers.collectUnique
import vyxal.NumberHelpers.range
import vyxal.VNum.given

import scala.collection.mutable.ListBuffer
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
    addDirect(
      "#?",
      "All Inputs",
      List("all-inputs", "inputs", "all-stdin", "all-stdin?"),
      None,
      "A list of all inputs to the program",
    ) { ctx ?=>
      ctx.push(VList.from(ctx.globals.inputs.getAll))
    },
    addNilad(
      "#¿",
      "Number of Inputs",
      List("number-of-inputs", "count-inputs", "count-stdin"),
      "The number of inputs to the program",
    ) { ctx ?=> ctx.globals.inputs.length },
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
        "re-sub",
        "regex-sub",
        "@=>",
      ),
      false,
      "a: lst, b: num, c: non-fun -> assign c to a at the index b / a[b] = c",
      "a: lst, b: num, c: fun -> a[b] c= <stack items> (augmented assignment to list)",
      "a: lst, b: lst, c: lst -> assign c to a at the indices in b",
      "a: str, b: str, c: str -> replace regex matches of pattern b in string a with c",
      "a: str, b: str, c: fun -> replace regex matches of pattern b in string a with the result of applying c to each match",
      "a: str, b: fun, c: str -> replace regex matches of pattern c in string a with the result of applying b to each match",
      "a: fun, b: str, c: str -> replace regex matches of pattern c in string b with the result of applying a to each match",
      "a: rec, b: str, c: str -> a.b = c",
    ) {
      case (a: VObject, b: String, c) => MiscHelpers.setObjectMember(a, b, c)
      case (a: VObject, b: VList, c) =>
        var obj = a
        for i <- b do
          obj = MiscHelpers.setObjectMember(
            obj,
            b.toString,
            c,
          )
        obj
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
            case _ => throw InvalidListOverloadException("Ạ", b, "Number")
        if a.isInstanceOf[String] then temp.mkString
        else temp
      case (a, b: VList, c) =>
        val temp =
          ListHelpers.makeIterable(b).foldLeft(ListHelpers.makeIterable(a)) {
            case (temp, ind: VNum) => ListHelpers.assign(temp, ind, c)
            case _ => throw InvalidListOverloadException("Ạ", b, "Number")
          }
        if a.isInstanceOf[String] then temp.mkString
        else temp
      case (a: String, b: String, c: String) => StringHelpers.regexSub(a, b, c)
      case (a: String, b: String, c: VFun) => StringHelpers.regexSub(a, b, c)
      case (a: String, b: VFun, c: String) => StringHelpers.regexSub(a, c, b)
      case (a: VFun, b: String, c: String) => StringHelpers.regexSub(b, c, a)
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
      "ÞṂ",
      "Matrix Inverse",
      List("matrix-inverse"),
      true,
      "a: lst[lst] -> matrix inverse of a",
    ) {
      case l: VList if l.forall(_.isInstanceOf[VList]) =>
        ListHelpers.matrixInverse(l).getOrElse {
          scribe.warn(s"Could not invert matrix $l")
          l
        }
    },
    addDirect(
      "ᶿ",
      "Bifuricate",
      List(
        "bifuricate",
        "bifur",
        "bif",
        "furry",
        "uwu",
        "dup-rev",
        "dup-reverse",
        "owo",
      ),
      Some(1),
      "a: lst -> Push a, then push a reversed",
    ) { ctx ?=>
      val a = ctx.pop()
      ctx.push(a, ListHelpers.reverse(a))
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
      List("bitwise-xor", "insert-space"),
      true,
      "a: num, b: num -> a ^ b",
      "a: str, b: str -> a + space + b",
    ) {
      case (a: VNum, b: VNum) => a.toBigInt ^ b.toBigInt
      case (a: String, b: String) => a + " " + b
    },
    addPart(
      Dyad,
      "«",
      "Bitshift Left",
      List("bitwise-left-shift", "left-shift", "left-pad", "pad-left"),
      true,
      "a: num, b: num -> a << b",
      "a: num, b: str -> b padded to length a with spaces prepended",
      "a: str, b: num -> a padded to length b with spaces prepended",
      "a: str, b: str -> a padded to length of b with spaces prepended",
    ) {
      case (a: VNum, b: VNum) => a.toBigInt << b.toInt
      case (a: VNum, b: String) => StringHelpers.padLeft(b, a)
      case (a: String, b: VNum) => StringHelpers.padLeft(a, b)
      case (a: String, b: String) => StringHelpers.padLeft(a, b.length)

    },
    addPart(
      Dyad,
      "»",
      "Bitshift Right",
      List(
        "bitwise-right-shift",
        "right-shift",
        "right-pad",
        "pad-right",
      ),
      true,
      "a: num, b: num -> a >> b",
      "a: num, b: str -> b padded to length a with spaces appended",
      "a: str, b: num -> a padded to length b with spaces appended",
      "a: str, b: str -> a padded to length of b with spaces appended",
    ) {
      case (a: VNum, b: VNum) => a.toBigInt >> b.toInt
      case (a: VNum, b: String) => StringHelpers.padRight(b, a)
      case (a: String, b: VNum) => StringHelpers.padRight(a, b)
      case (a: String, b: String) => StringHelpers.padRight(a, b.length)
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
      "ÞẊ",
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
      Monad,
      "#c",
      "Base-252 Compress String or Number",
      List("compress-252", "compress-b"),
      true,
      "a: str -> compress a using base 252",
      "a: num -> compress a using base 252",
    ) {
      case a: String => StringHelpers.compress252(a)
      case a: VNum => StringHelpers.compress252(a)
    },
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
      "ඞ",
    ) { MiscHelpers.vyPrintln("sus") },
    addDirect("🌮", "Taco", List("taco"), None, "very funky") { ctx ?=>
      ctx.push("https://codegolf.stackexchange.com/users/58375/ataco")
    },
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
      "@",
      "Cumulative Sums",
      List("cumulative-sums", "cumsums", "cumsum", "cum-sum", "-_-"),
      false,
      "a: lst -> cumulative sums of a",
    ) {
      case a =>
        val list = ListHelpers.makeIterable(a)
        if list.isEmpty then VList()
        else if list.tail.isEmpty then VList(list.head)
        else
          VList.from(
            list.tail.scanLeft(
              list.head
            )((x, y) => MiscHelpers.add(x, y))
          )
    },
    addPart(
      Monad,
      "ÞĊ",
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
      "v",
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
      List("deltas", "pairwise-differences", "differences"),
      false,
      "a: lst -> forward pairwise differences of a",
    ) { a =>
      ListHelpers.deltas(ListHelpers.makeIterable(a))
    },
    addPart(
      Dyad,
      "÷",
      "Divide | Split",
      List(
        "divide",
        "div",
        "str-split",
        "re-split",
        "str-n-pieces",
        "n-strings",
        "str-pieces",
        "string-pieces",
      ),
      true,
      "a: num, b: num -> a / b",
      "a: str, b: num -> a split into b equal sized chunks, with the last chunk potentially smaller",
      "a: num, b: str -> b split into a equal sized chunks, with the last chunk potentially smaller",
      "a: str, b: str -> Split a on the regex b",
    ) {
      case (a: VNum, b: VNum) => a / b
      case (a: String, b: VNum) => StringHelpers.intoNPieces(a, b)
      case (a: VNum, b: String) => StringHelpers.intoNPieces(b, a)
      case (a: String, b: String) => StringHelpers.split(a, b)
    },
    addFull(
      Dyad,
      "Ḋ",
      "Divides? | Append Spaces | Remove Duplicates by Function",
      List("divides?", "+-spaces", "dedup-by", "re-span", "regex-span"),
      false,
      "a: num, b: num -> a % b == 0",
      "a: str, b: num -> a + ' ' * b",
      "a: num, b: str -> b + ' ' * a",
      "a: lst, b: fun -> Remove duplicates from a by applying b to each element",
      "a: str, b: str -> span of first regex match of b in a",
    ) { (a, b) => NumberHelpers.divides(a, b) },
    addPart(
      Dyad,
      "ḋ",
      "Dot Product | To Bijective Base | First Index Where Predicate Truthy",
      List(
        "dot-product",
        "bijective-base",
        "dot-prod",
        "first-index-where",
        "_*",
      ),
      false,
      "a: lst, b: lst -> Dot product of a and b",
      "a: num, b: num -> Convert a to bijective base b",
      "a: lst, b: fun -> First index of a where b is truthy",
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
    addPart(
      Dyad,
      "≠",
      "Not Equal",
      List("not-equal", "=n't"),
      true,
      "a: any, b: any -> a != b",
    ) {
      case (a: VNum, b: VNum) => a != b
      case (a: VNum, b: String) => a.toString != b
      case (a: String, b: VNum) => a != b.toString
      case (a: String, b: String) => a != b
    },
    addDirect(
      "Ė",
      "Execute lambda | Evaluate as Vyxal | Power with base 10",
      List("execute-lambda", "evaluate-as-vyxal", "power-base-10", "call", "@"),
      Some(1),
      "a: fun -> Execute a",
      "a: str -> Evaluate a as Vyxal",
      "a: num -> 10 ** n",
    ) { ctx ?=>
      ctx.push(execHelper(ctx.pop()))
    },
    addDirect(
      "#Q",
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
              case x => throw InvalidListOverloadException("Ḃ", a, "Number")
            }
            ctx.push(
              VList(
                (0 until indices.max + 1).map(x => VNum(indices.contains(x)))*
              )
            )
        case a => throw BadArgumentException("Ḃ", a)

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
      List(
        "filter",
        "keep-by",
        "from-base",
        "10->b",
      ),
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
      "context variable m - defaults to uppercase alphabet if outside context",
    ) { ctx ?=> ctx.ctxVarSecondary },
    addNilad(
      "n",
      "Get Context Variable N",
      List("get-context-n", "context-n", "c-var-n", "ctx-n", "ctx-primary"),
      "context variable n - defaults to lowercase alphabet if outside context",
    ) { ctx ?=> ctx.ctxVarPrimary },
    addNilad(
      "#¤",
      "Number of Context Parameters",
      List("number-of-context", "context-number", "context-count"),
      "number of context parameters",
    ) { ctx ?=> ctx.ctxArgs.getOrElse(Seq.empty).length },
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
      "Group by Function Result | Greatest Common Divisor | Find all overlapping regex matches",
      List(
        "group-by",
        "gcd",
        "re-find-overlapping",
        "regex-find-overlapping",
        "re-find-overlap",
        "regex-find-overlap",
      ),
      false,
      "a: any, b: fun -> group a by the results of b",
      "a: fun, b: any -> group b by the results of a",
      "a: num, b: num -> gcd(a, b)",
      "a: lst[num], b: num -> gcd of b and all elements of a",
      "a: lst[num] -> gcd of all items in a.",
      "a: str, b: str -> all overlapping regex matches of b in a (similar to `y` but with overlaps) (JVM/JS Only)",
      "a: str, b: lst[str] -> vectorised string overload of the above",
      "a: lst, b: str -> vectorised pattern overload of the above",
    ) {
      case (a: VNum, b: VNum) => NumberHelpers.gcd(a, b)
      case (a: VList, b: VNum) => NumberHelpers.gcd(b +: a)
      case (a: VFun, b) => ListHelpers.groupBy(ListHelpers.makeIterable(b), a)
      case (a, b: VList) =>
        if a.isInstanceOf[String] && b.lst.forall(_.isInstanceOf[String]) then
          val pattern = b.map(StringHelpers.r).map(_.findAllMatchIn(a.toString))
          VList.from(pattern.map(x => VList.from(x.map(_.group(1)).toSeq)))
        else
          summon[Context].push(a)
          NumberHelpers.gcd(b)
      case (a, b: VFun) => ListHelpers.groupBy(ListHelpers.makeIterable(a), b)
      case (a: String, b: String) =>
        val pattern = StringHelpers.r(s"(?=($b))")
        VList.from(pattern.findAllMatchIn(a).map(_.group(1)).toSeq)

    },
    addPart(
      Dyad,
      "∆L",
      "Least Common Multiple",
      List("lcm"),
      false,
      "a: num, b: num -> lcm(a, b)",
      "a: lst[num], b: num -> lcm of b and all elements of a",
      "a: lst[num] -> lcm of all items in a.",
    ) {
      case (a: VNum, b: VNum) => NumberHelpers.lcm(a, b)
      case (a: VList, b: VNum) => NumberHelpers.lcm(b +: a)
      case (a, b: VList) =>
        summon[Context].push(a)
        NumberHelpers.lcm(b)
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
      case a => ListHelpers.makeIterable(a, Some(true)).drop(1)
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
        case n: VNum =>
          val iter = makeIterable(n, Some(true))
          ctx.push(
            iter.headOption.getOrElse(ctx.settings.defaultValue),
            iter.drop(1),
          )
        case arg => throw UnimplementedOverloadException("ḣ", List(arg))
    },
    addDirect(
      "Ḥ",
      "Head Extract Under",
      List(
        "head-extract-under",
        "split-at-head-under",
        "head-extract-swap",
        "headless-swap",
        "head-swap",
      ),
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
        case n: VNum =>
          val iter = makeIterable(n, Some(true))
          ctx.push(
            iter.drop(1),
            iter.headOption.getOrElse(ctx.settings.defaultValue),
          )
        case arg => throw UnimplementedOverloadException("Ḥ", List(arg))
    },
    addDirect(
      "ṫ",
      "Last Extract | Tail Extract",
      List("last-extract", "split-at-last", "tail-extract"),
      Some(1),
      "a: lst|str -> Push a[:-1], a[-1] onto the stack",
    ) { ctx ?=>
      ctx.pop() match
        case lst: VList => ctx.push(
            lst.dropRight(1),
            lst.lastOption.getOrElse(ctx.settings.defaultValue),
          )
        case s: String =>
          ctx.push(s.dropRight(1), if s.isEmpty then "" else s.last.toString)
        case arg => throw UnimplementedOverloadException("ṫ", List(arg))
    },
    addFull(
      Monad,
      "Þh",
      "Ends",
      List("ends", "sides", "edges"),
      false,
      "a: lst -> [a[0], a[-1]]",
      "a: str -> [a[0], a[-1]]",
      "a: cmx -> [real, imaginary]",
      "a: num -> [digit[0], digit[-1]]",
    ) {
      case a: VNum if (a.isComplex || a.isImaginary) => VList(a.real, a.imag)
      case a =>
        val iterable = ListHelpers.makeIterable(a)
        if iterable.isEmpty then VList.from(Seq.empty)
        else if iterable.length == 1 then VList(iterable.head)
        else VList(iterable.head, iterable.last)
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
    addFull(
      Dyad,
      "i",
      "Index | Collect Unique Application Values | Enclose | Read Member",
      List(
        "index",
        "at",
        "item-at",
        "nth-item",
        "collect-unique",
        "enclose",
        "@<=",
      ),
      false,
      "a: lst, b: num -> a[b]",
      "a: num, b: num -> b[x] for x in a",
      "a: lst, b: lst -> a[_] for _ in b",
      "a: str, b: lst[num] -> ''.join(a[i] for i in b)",
      "a: str, b: lst[any] -> x[a] for x in b",
      "a: lst, b: str -> x[b] for x in a",
      "a: any, b: fun -> Apply b on a and collect unique values. Does include the initial value.",
      "a: str, b: str -> enclose b in a (a[0:len(a)//2] + b + a[len(a)//2:])",
      "a: rec, b: str -> get member b of a",
      "a: str, b: rec -> get member a of b",
    ) { MiscHelpers.index },
    addPart(
      Dyad,
      "İ",
      "Drop/Zero Slice From | Collect While Unique | Complex Number",
      List(
        "drop",
        "zero-slice-from",
        "slice-from",
        "collect-while-unique",
        "complex",
      ),
      false,
      "a: num, b: num -> a.real + b.real * i",
      "a: str|lst, b: num -> a[b:]",
      "a: lst, b: lst[num] -> apl style drop",
      "a: any, b: fun -> Apply b on a and collect unique values (until fixpoint). Does not include the initial value.",
    ) {
      case (a: VNum, b: VNum) => VNum.complex(a.real, b.real)
      case (a: VList, b: VNum) => ListHelpers.drop(a, b)
      case (a: String, b: VNum) =>
        ListHelpers.drop(ListHelpers.makeIterable(a), b).mkString
      case (a: VNum, b: VList) => ListHelpers.drop(b, a)
      case (a: VNum, b: String) =>
        ListHelpers.drop(ListHelpers.makeIterable(b), a).mkString
      case (init, fn: VFun) => collectUnique(fn, init).tail
      case (fn: VFun, init) => collectUnique(fn, init).tail
      case (a: VList, b: VList) =>
        if !b.lst.forall(_.isInstanceOf[VNum]) then ???
        else ListHelpers.drop(a, b.lst.map(_.asInstanceOf[VNum]))
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
      "a: any, b: num, c: any -> insert c at position b in a",
      "a: any, b: lst, c: any -> insert c at positions b in a",
      "a: any, b: lst[num], c: lst -> insert c[i] at position b[i] in a",
    ) {
      case (a, b: VNum, c) =>
        ListHelpers.insert(ListHelpers.makeIterable(a), b, c)
      case (a, b: VList, c: VList) =>
        var temp = ListHelpers.makeIterable(a)
        for (i, j) <- b.reverse.zip(c.reverse) do
          (i, j) match
            case (index: VNum, elem) =>
              temp = ListHelpers.insert(temp, index, elem)
            case _ => throw InvalidListOverloadException("Ị", b, "Number")
        temp
      case (a, b: VList, c) => b.foldRight(ListHelpers.makeIterable(a)) {
          case (index: VNum, acc) => ListHelpers.insert(acc, index, c)
          case _ => throw InvalidListOverloadException("Ị", b, "Number")
        }
    },
    addPart(
      Dyad,
      "I",
      "Interleave / Reject By Function",
      List("interleave", "reject"),
      false,
      "a: lst, b: lst -> Interleave a and b",
      "a: any, b: fun -> Reject elements of a by applying b",
    ) {
      case (a, b: VFun) =>
        VList.from(ListHelpers.makeIterable(a).filter(x => !b(x).toBool))
      case (a, b) =>
        val temp = ListHelpers
          .interleave(ListHelpers.makeIterable(a), ListHelpers.makeIterable(b))
        if a.isInstanceOf[String] && b.isInstanceOf[String] then temp.mkString
        else temp
    },
    addPart(
      Dyad,
      "Þ÷",
      "Into N Pieces | Split Into N Pieces",
      List("into-n-pieces", "split-into-n-pieces"),
      false,
      "a: lst, b: num -> a split into b equal sized chunks, with the last chunk potentially smaller",
      "a: str, b: num -> a split into b equal sized chunks, with the last chunk potentially smaller",
    ) {
      case (a: VList, b: VNum) => ListHelpers.intoNPieces(a, b)
      case (a: VNum, b: VList) => ListHelpers.intoNPieces(b, a)
      case (a: String, b: VNum) => StringHelpers.intoNPieces(a, b)
      case (a: VNum, b: String) => StringHelpers.intoNPieces(b, a)
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
      case a: String => StringHelpers.split(a, "\n")
    },
    addPart(
      Monad,
      "Ṅ",
      "Is Prime? | Quine Cheese",
      List("prime?", "quineify"),
      true,
      "a: num -> is a prime?",
      "a: str -> quote a and prepend to a",
    ) {
      case a: VNum => NumberHelpers.isMostLikelyPrime(a)
      case a: String => StringHelpers.quotify(a) + a
    },
    addDirect(
      "”",
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
      "“",
      "Join on Nothing | First Positive Integer | Is Alphanumeric | Insignificant?",
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
        "abs<=1",
        "insignificant?",
        "insignificant",
        "insig?",
        "insig",
      ),
      false,
      "a: lst -> a join on nothing",
      "a: str -> is a alphanumeric?",
      "a: fun -> First positive integer ([1, 2, 3, ...]) for which a returns true",
      "a: num -> abs(a) <= 1",
    ) { a => MiscHelpers.joinNothing(a) },
    addPart(
      Monad,
      "„",
      "Join on Spaces | Is Negative? (Used when not closing a string)",
      List("space-join", "join-on-spaces", "is-negative?", "negative?"),
      false,
      "a: lst -> a join on spaces",
      "a: num -> a < 0",
    ) {
      case a: VList => a.mkString(" ")
      case a: VNum => a < 0
    },
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
      "Sort by Length | Regex Escape",
      List(
        "sort-by-length",
        "sort-by-len",
        "order-by-length",
        "order-by-len",
        "length-sort",
        "len-sort",
        "re-escape",
        "regex-escape",
      ),
      false,
      "a: lst -> sort a by length",
      "a: str -> escape a for regex",
    ) {
      case a: VList => ListHelpers.sortByLength(a)
      case a: String => StringHelpers.escapeRegex(a)
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
      "Length 1-Range",
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
              throw InvalidListOverloadException("Y", b, "Number")
          }
          .lazyZip(ListHelpers.makeIterable(a))
          .map((n, item) => VList.fill(n)(item))
        if a.isInstanceOf[String] then temp.map(_.mkString).mkString
        else VList.from(temp)
      case (a, b) => throw UnimplementedOverloadException("Y", List(a, b))
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
      case (a: VPhysical, b: VFun) => MiscHelpers.collectUnique(b, a)
      case (a: VFun, b) => MiscHelpers.collectUnique(a, b)

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
      List(
        "map",
        "mold",
        "multiplicity",
        "times-divide",
        "re-match",
        "regex-match",
      ),
      false,
      "a: any, b: fun -> a.map(b)",
      "a: fun, b: any -> b.map(a)",
      "a: lst, b: lst -> a molded to the shape of b",
      "a: num, b: num -> how many times b divides a",
      "a: str, b: str -> regex match of b in a",
      "a: list, b: str -> regex match of b of each element of a",
      "a: str, b: list -> regex match of each element of b in a",
    ) {
      case (a: VList, b: VList) => ListHelpers.mold(a, b)
      case (a: VNum, b: VNum) => NumberHelpers.multiplicity(a, b)
      case (a, b: VFun) =>
        ListHelpers.map(b, ListHelpers.makeIterable(a, Some(true)))
      case (a: VFun, b) =>
        ListHelpers.map(a, ListHelpers.makeIterable(b, Some(true)))
      case (a: String, b: String) =>
        StringHelpers.r(b).findFirstIn(a).getOrElse("")
      case (a: String, b: VList) =>
        VList.from(b.lst.map(StringHelpers.r(_).findFirstIn(a).getOrElse("")))
      case (a: VList, b: String) => VList.from(
          a.lst.map(x =>
            StringHelpers.r(b).findFirstIn(x.toString()).getOrElse("")
          )
        )
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
            case (a, b) => throw UnimplementedOverloadException("G", List(a, b))
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
      List("mirror", "ab->abba"),
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
      case (a: String, b: String) => StringHelpers.r(a).matches(b)
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
            case (a, b) => throw UnimplementedOverloadException("g", List(a, b))
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
    addPart(
      Triad,
      "ÞẠ",
      "Multidimensional Assignment",
      List("md-assign"),
      false,
      "a: lst, b: lst[num], c: any -> a[b[0]][b[1]]...[b[n]] = c",
    ) {
      case (a, b: VList, c) => ListHelpers.multiDimAssign(makeIterable(a), b, c)

    },
    addPart(
      Dyad,
      "Þi",
      "Multidimensional Index",
      List("md-index"),
      false,
      "a: lst, b: lst[num] -> a[b[0]][b[1]]...[b[n]]",
    ) {
      case (a, b: VList) => ListHelpers.multiDimIndex(makeIterable(a), b)

    },
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
      "N Choose K (Binomial Coefficient) | Character Set Equal? | Repeat Until No Change",
      List(
        "n-choose-k",
        "ncr",
        "nck",
        "choose",
        "binomial",
        "char-set-equal?",
        "char-set-eq?",
        "until-stable",
      ),
      true,
      "a: num, b: num -> a choose b",
      "a: str, b: str -> are the character sets of a and b equal?",
      "a: fun, b: any -> run a on b until the result no longer changes returning all intermediate results",
    ) {
      case (a: VNum, b: VNum) => NumberHelpers.nChooseK(a, b)
      case (a: String, b: String) => a.toSet == b.toSet
      case (a: VFun, b) => MiscHelpers.untilNoChange(a, b)
      case (a, b: VFun) => MiscHelpers.untilNoChange(b, a)
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
      "Þι",
      "0-Lift",
      List("zero-lift", "lift-0", "O-lift"),
      false,
      "a: lst -> each item of a multiplied by its 0-based index",
    ) {
      case a: VList =>
        VList.from(a.zipWithIndex.map((x, i) => MiscHelpers.multiply(x, i)))
    },
    addPart(
      Monad,
      "Þκ",
      "1-Lift",
      List("one-lift", "lift-1", "l-lift"),
      false,
      "a: lst -> each item of a multiplied by its 1-based index",
    ) {
      case a: VList =>
        VList.from(a.zipWithIndex.map((x, i) => MiscHelpers.multiply(x, i + 1)))
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
    ) {
      case (a: VVal, b: VVal) => if !a.toBool then a else b
    },
    addPart(
      Dyad,
      "∨",
      "Logical Or",
      List("or", "logical-or"),
      true,
      "a: any, b: any -> a || b",
    ) { case (a: VVal, b: VVal) => if a.toBool then a else b },
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
            case (a, b) => throw UnimplementedOverloadException("o", List(a, b))
    },
    addDirect(
      "Þo",
      "Grid Neighbours",
      List(
        "grid-neighbours",
        "grid-neighbors",
        "adjacent-cells",
        "adj-cells",
        "surrounding-cells",
      ),
      Some(1),
      "a: lst[lst] -> Grid neighbours of a - right, down, left, up of a",
      "a: lst[lst], b: num -> Grid neighbours of a - right, down, left, up of a and start from direction b => " +
        "0: right, 1: down, 2: left, 3: up. Negative b does not include middle, positive b does",
    ) { ctx ?=>
      val top = ctx.pop()
      top match
        case a: VList => ctx.push(
            ListHelpers.gridNeighbours(a)
          )
        case _ =>
          val next = ctx.pop()
          (top, next) match
            case (a: VNum, b: VList) => ctx.push(
                ListHelpers.gridNeighbours(
                  b,
                  a >= 0,
                  (a.vabs % 4).toInt,
                )
              )
            case (a, b) =>
              throw UnimplementedOverloadException("Þo", List(a, b))
      end match
    },
    addDirect(
      "ÞO",
      "Grid Neighbours (Wrap Around)",
      List(
        "grid-neighbours-wrap",
        "grid-neighbors-wrap",
        "adjacent-cells-wrap",
        "adj-cells-wrap",
        "surrounding-cells-wrap",
      ),
      Some(1),
      "a: lst[lst] -> Grid neighbours of a - up, down, left, right - wrapping around",
      "a: lst[lst], b: num -> Grid neighbours of a - right, down, left, up of a, wrapping around and start from direction b => " +
        "0: right, 1: down, 2: left, 3: up. Negative b does not include middle, positive b does",
    ) { ctx ?=>
      val top = ctx.pop()
      top match
        case a: VList => ctx.push(
            ListHelpers.gridNeighboursWrap(ListHelpers.makeIterable(a))
          )
        case _ =>
          val next = ctx.pop()
          (top, next) match
            case (a: VNum, b: VList) => ctx.push(
                ListHelpers.gridNeighboursWrap(
                  ListHelpers.makeIterable(b),
                  a >= 0,
                  (a.vabs % 4).toInt,
                )
              )
            case (a, b) =>
              throw UnimplementedOverloadException("ÞO", List(a, b))
      end match
    },
    addDirect(
      "Þȯ",
      "Grid Neighbours (Diagonals)",
      List(
        "grid-neighbours-diagonals",
        "grid-neighbors-diagonals",
        "adjacent-cells-diagonals",
        "adj-cells-diagonals",
        "surrounding-cells-diagonals",
        "eight-cells",
      ),
      Some(1),
      "a: lst[lst] -> Grid neighbours of a - up, down, left, right, diagonals",
      "a: lst[lst], b: num -> Grid neighbours of a - right, down, left, up of a and start from direction b => " +
        "0: right, 1: down, 2: left, 3: up, 4: down-right, 5: up-left, 6: down-left, 7: up-left. Negative b does not include middle, positive b does",
    ) { ctx ?=>
      val top = ctx.pop()
      top match
        case a: VList => ctx.push(
            ListHelpers.gridNeighboursDiagonal(a)
          )
        case _ =>
          val next = ctx.pop()
          (top, next) match
            case (a: VNum, b: VList) => ctx.push(
                ListHelpers.gridNeighboursDiagonal(
                  b,
                  a >= 0,
                  (a.vabs % 8).toInt,
                )
              )
            case (a, b) =>
              throw UnimplementedOverloadException("Þȯ", List(a, b))
      end match
    },
    addDirect(
      "ÞȮ",
      "Grid Neighbours (Diagonals, Wrap Around)",
      List(
        "grid-neighbours-diagonals-wrap",
        "grid-neighbors-diagonals-wrap",
        "adjacent-cells-diagonals-wrap",
        "adj-cells-diagonals-wrap",
        "surrounding-cells-diagonals-wrap",
        "eight-cells-wrap",
      ),
      Some(1),
      "a: lst[lst] -> Grid neighbours of a - up, down, left, right, diagonals - wrapping around",
      "a: lst[lst], b: num -> Grid neighbours of a - right, down, left, up of a, wrapping around and start from direction b => " +
        "0: right, 1: down, 2: left, 3: up, 4: down-right, 5: up-left, 6: down-left, 7: up-left. Negative b does not include middle, positive b does",
    ) { ctx ?=>
      val top = ctx.pop()
      top match
        case a: VList => ctx.push(
            ListHelpers.gridNeighboursDiagonalWrap(a)
          )
        case _ =>
          val next = ctx.pop()
          (top, next) match
            case (a: VNum, b: VList) => ctx.push(
                ListHelpers.gridNeighboursDiagonalWrap(
                  b,
                  a >= 0,
                  (a.vabs % 8).toInt,
                )
              )
            case (a, b) =>
              throw UnimplementedOverloadException("ÞȮ", List(a, b))
      end match
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
      Monad,
      "Ṃ",
      "-1 Power Of | Split on Spaces",
      List(
        "neg-one-power-of",
        "neg1**",
        "neg1^",
        "neg1-power-of",
        "neg1-power",
        "split-on-spaces",
        "split-spaces",
        "space-split",
      ),
      true,
      "a: num -> -1 ** a",
      "a: str -> a split on spaces",
    ) {
      case a: VNum => (-1) ** a
      case a: String => StringHelpers.split(a, " ")
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
      "∆q",
      "Prime Exponents",
      List("prime-exponents", "prime-exps"),
      true,
      "a: num -> push a list of the power of each prime in the prime factors of a",
    ) {
      case a: VNum =>
        val factors = NumberHelpers.primeFactors(a)
        val primes = factors.distinct
        val exponents = primes.map(prime =>
          NumberHelpers.multiplicity(a, prime.asInstanceOf[VNum])
        )
        VList.from(exponents)
    },
    addPart(
      Monad,
      "∆ḟ",
      "All Prime Exponents",
      List("all-prime-exponents", "all-prime-exps"),
      true,
      "a: num -> for all primes less than or equal to a, push the power of that prime in the factorisation of a",
    ) {
      case a: VNum =>
        if a < 2 then VList()
        else
          val primes = NumberHelpers.probablePrimes.takeWhile(
            _ <= NumberHelpers.primeFactors(a).maxOption.getOrElse(2)
          )
          val exponents = primes.map(prime =>
            NumberHelpers.multiplicity(a, prime.asInstanceOf[VNum])
          )
          VList.from(exponents)
    },
    addPart(
      Monad,
      "ḟ",
      "Prime Factors | Remove Alphabet",
      List("prime-factors", "remove-alphabet"),
      true,
      "a: num -> prime factors of a",
      "a: str -> a with all alphabet characters removed",
    ) {
      case a: VNum => NumberHelpers.primeFactors(a)
      case a: String => a.filter(!_.isLetter)
    },
    addDirect(
      ",",
      "Print",
      List("print", "puts", "out", "println"),
      None,
      "a -> printed to stdout",
    ) { ctx ?=>
      MiscHelpers.vyPrintln(ctx.pop())
      ctx.globals.printed = true
    },
    addDirect(
      "§",
      "Print without newline",
      List("print-no-newline"),
      None,
      "a -> printed to stdout without newline",
    ) { ctx ?=>
      MiscHelpers.vyPrint(ctx.pop())
      ctx.globals.printed = true
    },
    addDirect(
      "Ọ",
      "Print without popping",
      List("print-no-pop"),
      None,
      "a -> printed to stdout without popping",
    ) { ctx ?=>
      MiscHelpers.vyPrintln(ctx.peek)
      ctx.globals.printed = true
    },
    addPart(
      Monad,
      "q",
      "Quotify | Nth Prime",
      List("quotify", "nth-prime", "prime-n"),
      true,
      "a: str -> enclose a in quotes, escape backslashes and quote marks",
      "a: num -> nth prime",
    ) {
      case a: String => StringHelpers.quotify(a)
      case a: VNum => NumberHelpers.probablePrimes.index(a)
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
        "partitions",
      ),
      false,
      "a: lst -> List partitions of a",
      "a: num -> Integer partitions of a (all possible ways to sum to a)",
    ) {
      case a: VList => ListHelpers.partitions(a)
      case s: String => ListHelpers
          .partitions(ListHelpers.makeIterable(s))
          .vmap(
            _.asInstanceOf[VList].vmap(_.asInstanceOf[VList].mkString)
          )
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
      if ctx.recursion >= ctx.settings.recursionLimit then
        throw VyxalRecursionException()
      ctx.recursion += 1
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
      "Reduce by Function Object | Dyadic Range | Regex Match | Set Union",
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
        "union",
        "to",
      ),
      false,
      "a: fun, b: any -> reduce iterable b by function a",
      "a: any, b: fun -> reduce iterable a by function b",
      "a: num, b: num -> the range [a, b)",
      "a: str, b: num|str -> does regex pattern b match haystack a?",
      "a: lst, b: lst -> union of a and b",
    ) {
      case (a: VNum, b: VNum) => NumberHelpers.range(a, b).dropRight(1)
      case (a: String, b: String) => StringHelpers.r(b).findFirstIn(a).isDefined
      case (a: String, b: VNum) => StringHelpers.r(b).findFirstIn(a).isDefined
      case (a: VNum, b: String) =>
        StringHelpers.r(b).findFirstIn(a.toString).isDefined
      case (a: VFun, b) => ListHelpers.reduce(b, a)
      case (a, b: VFun) => ListHelpers.reduce(a, b)
      case (a: VList, b: VList) => VList.from(a ++ b.filterNot(a.contains(_)))
    },
    addPart(
      Triad,
      "r",
      "Replace",
      List("replace", "zip-with"),
      false,
      "a: str, b: str, c: str -> replace all instances of b in a with c",
      "a: fun, b: any, c: any -> reduce items in zip(b, c) by a",
    ) {
      case (a: VFun, b, c) => MiscHelpers
          .zipWith(ListHelpers.makeIterable(b), ListHelpers.makeIterable(c), a)
      case (a, b: VFun, c) => MiscHelpers
          .zipWith(ListHelpers.makeIterable(a), ListHelpers.makeIterable(c), b)
      case (a, b, c: VFun) => MiscHelpers.zipWith(
          ListHelpers.makeIterable(a),
          ListHelpers.makeIterable(b),
          c,
        )
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
      List("reverse", "rev"),
      false,
      "a: any -> reverse a",
    ) { a => ListHelpers.reverse(a) },
    addDirect(
      "^",
      "Reverse Stack",
      List("reverse-stack", "rev-stack"),
      None,
      " -> reverse the stack",
    ) { ctx ?=>
      ctx.reverse()
    },
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
    addPart(
      Monad,
      "Ṙ",
      "Rotate Left",
      List("abc->bca", "rot-left", "rotate-left"),
      false,
      "a: any -> rotate left once",
    ) { a =>
      val iterable = ListHelpers.makeIterable(a)
      val temp =
        if iterable.isEmpty then VList.from(Seq.empty)
        else VList.from(iterable.tail :+ iterable.head)
      a match
        case _: String => temp.mkString
        case _: VNum => VNum(temp.mkString)
        case _ => temp
    },
    addPart(
      Monad,
      "ṙ",
      "Rotate Right",
      List("abc->cab", "rot-right", "rotate-right"),
      false,
      "a: any -> rotate right once",
    ) { a =>
      val iterable = ListHelpers.makeIterable(a)
      val temp =
        if iterable.isEmpty then VList.from(Seq.empty)
        else VList.from(iterable.last +: iterable.init)
      a match
        case _: String => temp.mkString
        case _: VNum => VNum(temp.mkString)
        case _ => temp

    },
    addPart(
      Dyad,
      "Þṅ",
      "Multi-Set Difference",
      List("multi-set-difference", "multi-set-diff"),
      false,
      "a: lst, b: lst -> multi-set difference of a and b",
    ) {
      case (a, b) => VList.from(
          ListHelpers.makeIterable(a) -- ListHelpers.makeIterable(b)
        )
    },
    addPart(
      Dyad,
      "Ċ",
      "Set XOR",
      List("set-xor"),
      false,
      "a: lst, b: lst -> set xor of a and b",
    ) {
      case (a, b) =>
        VList.from(ListHelpers.makeIterable(a) ^ (ListHelpers.makeIterable(b)))
    },
    addPart(
      Dyad,
      "Þċ",
      "Multi-Set XOR",
      List("multi-set-xor"),
      false,
      "a: lst, b: lst -> multi-set xor of a and b",
    ) {
      case (a, b) =>
        val aSet = ListHelpers.makeIterable(a)
        val bSet = ListHelpers.makeIterable(b)
        VList.from((aSet -- bSet) ++ (bSet -- aSet))
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
      "Sort by Function Object | Partition by Numbers | Set Difference",
      List(
        "sort-by",
        "sortby",
        "sort-by-fun",
        "sortbyfun",
        "sort-fun",
        "sortfun",
        "partition-by",
        "set-difference",
        "set-diff",
      ),
      false,
      "a: fun, b: any -> sort iterable b by function a",
      "a: any, b: fun -> sort iterable a by function b",
      "a: lst, b: lst -> set difference of a and b",
      "a: lst, b: num|str -> remove b from a",
      "a: num|str, b: lst -> remove a from b",
    ) {
      case (a: VFun, b) =>
        ListHelpers.sortBy(ListHelpers.makeIterable(b, Some(true)), a)
      case (a, b: VFun) =>
        ListHelpers.sortBy(ListHelpers.makeIterable(a, Some(true)), b)
      case (a: VList, b: (VNum | String)) => a.filter(_ != b)
      case (a: (VNum | String), b: VList) => b.filter(_ != a)
      case (a, b) =>
        val left = ListHelpers.makeIterable(a)
        val right = ListHelpers.makeIterable(b)
        VList.from(left.filterNot(right.contains(_)))
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
        else StringHelpers.split(a, b.toString())
      case (a: VNum, b) => StringHelpers.split(a, b.toString())
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
      case a: VNum => a.sqrt
    },
    addPart(
      Monad,
      "²",
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
      "a: lst[num] -> apl-style iota from 1 to a",
      "a: str -> a.upper()",
    ) {
      case a: VNum => NumberHelpers.range(1, a)
      case a: VList if a.forall(_.isInstanceOf[VNum]) =>
        NumberHelpers.range(1, a.map(_.asInstanceOf[VNum]))
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
      "a: lst[num] -> apl-style iota from 0 until a",
      "a: str -> a.lower()",
    ) {
      case a: VNum => NumberHelpers.range(0, a - a.signum)
      case a: VList if a.forall(_.isInstanceOf[VNum]) =>
        NumberHelpers.range(
          0,
          a.map(x => x.asInstanceOf[VNum] - x.asInstanceOf[VNum].signum),
        )
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
      "To Base | Regex Find",
      List("to-base", "re-find", "regex-find"),
      false,
      "a: num, b: num -> a in base b",
      "a: num, b: str|lst -> a in base with alphabet b",
      "a: lst, b: num -> each x in a in base b",
      "a: lst, b: str|lst -> each x in a in base with alphabet b",
      "a: str, b: str -> All matches of b in a",
    ) {
      case (a: VNum, b) => NumberHelpers.toBase(a, b)
      case (a: VList, b) => a.vmap(NumberHelpers.toBase(_, b))
      case (a: String, b: String) =>
        VList.from(StringHelpers.r(b).findAllIn(a).toSeq)
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
      case (p: VFun, f: VFun, v) => MiscHelpers.callWhileAndCollect(p, f, v)
      case (p: VFun, v, f: VFun) => MiscHelpers.callWhileAndCollect(p, f, v)
      case (v, p: VFun, f: VFun) => MiscHelpers.callWhileAndCollect(p, f, v)
      case (a: VList, b, c) => ListHelpers.transliterate(a, b, c)
      case (a: VNum, b, c) =>
        val temp =
          ListHelpers.transliterate(ListHelpers.makeIterable(a), b, c).mkString
        if VNum.NumRegex.matches(temp) then VNum(temp) else temp

    },
    addPart(
      Dyad,
      "Ṭ",
      "Trim / Cumulative Reduce",
      List("trim", "scanl", "cumulative-reduce"),
      false,
      "a: any, b: any -> Trim all elements of b from both sides of a.",
      "a: fun, b: any -> cumulative reduce b by function a",
    ) {
      case (a: String, b: String) => a.stripPrefix(b).stripSuffix(b)
      case (a: String, b: VNum) =>
        a.stripPrefix(b.toString).stripSuffix(b.toString)
      case (a: VNum, b: String) =>
        VNum(a.toString.stripPrefix(b).stripSuffix(b))
      case (a: VNum, b: VNum) =>
        VNum(a.toString.stripPrefix(b.toString).stripSuffix(b.toString))
      case (a: VFun, b) => MiscHelpers.scanl(ListHelpers.makeIterable(b), a)
      case (a, b: VFun) => MiscHelpers.scanl(ListHelpers.makeIterable(a), b)
      case (a: VList, b: VList) => ListHelpers.trimList(a, b)
      case (a: VList, b) => ListHelpers.trim(a, b)
      case (a, b: VList) => ListHelpers.trim(b, a)
      case (a, b) => ListHelpers.trim(ListHelpers.makeIterable(a), b)
    },
    addPart(
      Dyad,
      "ẋ",
      "Cartesian Power | Regex Search for Match",
      List("cartesian-power", "re-search", "regex-search"),
      false,
      "a: lst, b: num -> cart_prod([a] * n)",
      "a: num, b: lst -> cart_prod([b] * n)",
      "a: str, b: str -> return first index of pattern match b in target string a, -1 if not found",
      "a: lst, b: str -> regex search vectorised",
      "a: str|lst, b: lst -> push a, push cartesian product of b and b",
    ) {
      case (a, n: VNum) => ListHelpers.cartesianPower(a, n)
      case (n: VNum, a) => ListHelpers.cartesianPower(a, n)
      case (a: String, b: String) =>
        val res = StringHelpers.r(b).findFirstMatchIn(a)
        if res.isDefined then res.get.start else -1
      case (a: VList, b: String) => VList.from(
          a.lst
            .map(_.toString)
            .map(x =>
              val res = StringHelpers.r(b).findFirstMatchIn(x)
              if res.isDefined then res.get.start else -1
            )
        )
      case (a, b: VList) =>
        summon[Context].push(a)
        ListHelpers.cartesianProduct(b, b)

    },
    addPart(
      Dyad,
      "ø⁾",
      "Surround",
      List("surround"),
      false,
      "a: any, b: any -> a prepended and appended to b",
    ) {
      case (a: VList, b) => VList.from((b +: a) :+ b)
      case (a: String, b: String) => b + a + b
      case (a, b: VList) => VList.from((a +: b) :+ a)
    },
    addPart(
      Dyad,
      "⁾",
      "Set Intersection | Flatten By Depth | Character Multiply",
      List("set-intersection", "intersection", "flatten-by-depth", "intersect"),
      false,
      "a: lst, b: lst -> set intersection of a and b",
      "a: str, b: str -> set intersection of a and b",
      "a: lst, b: num -> flatten a by depth b",
      "a: num, b: str -> each character in b repeated a times",
      "a: str, b: num -> each character in a repeated b times",
    ) {
      case (a: VNum, b: String) => StringHelpers.characterMultiply(a, b)
      case (a: String, b: VNum) => StringHelpers.characterMultiply(b, a)
      case (a: VList, b: VList) => VList.from(a.filter(b.contains(_)))
      case (a: VList, b: VNum) => ListHelpers.flattenByDepth(a, b)
    },
    addPart(
      Dyad,
      "Þ⁾",
      "Multi-Set Intersection",
      List("multi-set-intersection", "multi-set-intersect"),
      false,
      "a: lst, b: lst -> multi-set intersection of a and b",
    ) {
      case (a, b) =>
        ListHelpers.multiSetIntersection(makeIterable(a), makeIterable(b))
    },
    addPart(
      Monad,
      "ÞT",
      "Transpose Safe",
      List("transpose-safe"),
      false,
      "a: any -> transpose a",
    ) {
      case a: VFun => throw UnimplementedOverloadException("ÞT", List(a))
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
        case a => throw UnimplementedOverloadException("U", List(a))

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
      List("one->b", "one-slice"),
      false,
      "a: lst, b: num -> a[1:b]",
      "a: num, b: lst -> b[1:a]",
    ) {
      case (a, b: VNum) =>
        val temp = ListHelpers.makeIterable(a).slice(1, b.toInt)
        a match
          case _: String => temp.mkString
          case _ => temp
      case (a: VNum, b) =>
        val temp = ListHelpers.makeIterable(b).slice(1, a.toInt)
        b match
          case _: String => temp.mkString
          case _ => temp
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
        case arg => UnimplementedOverloadException("#v", List(arg))
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
        case arg => throw UnimplementedOverloadException("#~", List(arg))
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
          val arg = ctx.pop()
          val suffixes = ListHelpers.suffixes(makeIterable(arg))
          ctx.push(
            VList.from(
              suffixes.map(suffix =>
                f(arg match
                  case s: String => suffix.mkString
                  case _ => suffix
                )
              )
            )
          )
        case arg =>
          throw UnimplementedOverloadException("#|map-suffixes", List(arg))
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
          val arg = ctx.pop()
          val iterArg = makeIterable(arg)
          val prefixes = iterArg.indices.map(i => iterArg.slice(0, i + 1))
          ctx.push(
            VList.from(
              prefixes.map(prefix =>
                f(arg match
                  case s: String => prefix.mkString
                  case _ => prefix
                )
              )
            )
          )
        case arg =>
          throw UnimplementedOverloadException("#|map-prefixes", List(arg))
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
        case arg =>
          throw UnimplementedOverloadException("#|reduce-cols", List(arg))
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
        case arg =>
          throw UnimplementedOverloadException("#|maximum-by", List(arg))
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
        case arg =>
          throw UnimplementedOverloadException("#|minimum-by", List(arg))
    },
    addDirect(
      "#|apply-to-register",
      "[Internal Use] Apply to Register (Element Form)",
      List(),
      None,
      "*a, f -> f applied to the register. Use the modifier instead.",
    ) { ctx ?=>
      ctx.pop() match
        case f: VFun =>
          ctx.push(ctx.globals.register)
          ctx.push(Interpreter.executeFn(f)(using ctx.makeChild()))
          ctx.globals.register = ctx.pop()
        case arg =>
          throw UnimplementedOverloadException("#|apply-to-register", List(arg))
    },
    addDirect(
      "#|dip",
      "[Internal Use] Dip (Element Form)",
      List(),
      None,
      "*a, f -> f applied to a with a pushed back. Use the modifier instead.",
    ) { ctx ?=>
      val f = ctx.pop()
      val top = ctx.pop()
      f match
        case fun: VFun =>
          Interpreter.executeFn(fun)
          ctx.push(top)
        case arg => throw UnimplementedOverloadException("#|dip", List(arg))
    },
    addDirect(
      "#|invar",
      "[Internal Use] Invariant (Element Form)",
      List(),
      None,
      "*a, f -> Use the ᵞ modifier instead.",
    ) { ctx ?=>
      val f = ctx.pop()
      val copy = ctx.peek
      f match
        case fun: VFun =>
          val result = Interpreter.executeFn(fun)(using ctx.makeChild())
          ctx.push(result === copy)
        case arg => throw UnimplementedOverloadException("#|invar", List(arg))
    },
    addDirect(
      "#|vscan",
      "[Internal Use] Vectorised Scan (Element Form)",
      List(),
      None,
      "*a, f -> scanl each column. Use the modifier instead.",
    ) { ctx ?=>
      val f = ctx.pop()
      val arg = VList.from(
        ListHelpers
          .makeIterable(ctx.pop())
          .map(x => ListHelpers.makeIterable(x))
      )
      f match
        case fun: VFun => ctx.push(
            VList.from(
              ListHelpers
                .transposeSafe(arg)
                .map(col => MiscHelpers.scanl(col.asInstanceOf[VList], fun))
            )
          )
        case arg => throw UnimplementedOverloadException("#|vscan", List(arg))
    },
    addDirect(
      "#|all-neigh",
      "[Internal Use] All Neighbours (Element Form)",
      List(),
      None,
      "*a, f -> f applied to each neighbour of a. Use the modifier instead.",
    ) { ctx ?=>
      ctx.pop() match
        case f: VFun =>
          val neighbours =
            ListHelpers.overlaps(ListHelpers.makeIterable(ctx.pop()), 2)
          val results = neighbours.map(x => f(x*))
          ctx.push(results.forall(_ == results(0)))
        case arg =>
          throw UnimplementedOverloadException("#|all-neigh", List(arg))
    },
    addDirect(
      "#|para-apply",
      "[Internal Use] Parallel Apply (Element Form)",
      List(),
      None,
      "*a, f -> The iconic parallel apply. Use the modifier instead bingus.",
    ) { ctx ?=>
      val second = ctx.pop().asInstanceOf[VFun]
      val first = ctx.pop().asInstanceOf[VFun]

      first.ctx = ctx.copy

      val firstRes = Interpreter.executeFn(first)(using ctx.copy)
      val secondRes = Interpreter.executeFn(second)(using ctx)
      ctx.push(firstRes, secondRes)

    },
    addDirect(
      "#|para-apply-wrap",
      "[Internal Use] Parallel Apply Wrap (Element Form)",
      List(),
      None,
      "*a, f -> The iconic parallel apply. Use the modifier instead bingus.",
    ) { ctx ?=>
      val second = ctx.pop().asInstanceOf[VFun]
      val first = ctx.pop().asInstanceOf[VFun]

      first.ctx = ctx.copy

      val firstRes = Interpreter.executeFn(first)(using ctx.copy)
      val secondRes = Interpreter.executeFn(second)(using ctx)
      ctx.pop()
      ctx.push(VList(firstRes, secondRes))

    },
    addDirect(
      "#|vec-dump",
      "[Internal Use] Map Dump (Element Form)",
      List(),
      None,
      "*a, f -> f applied to each element of a, treating as a stack. Use the modifier instead.",
    ) { ctx ?=>
      val f = ctx.pop()
      val arg = ListHelpers.makeIterable(ctx.pop())
      f match
        case fun: VFun => ctx.push(
            VList.from(
              arg.map(x =>
                Interpreter.executeFn(fun, args = ListHelpers.makeIterable(x))(
                  using ctx.makeChild()
                )
              )
            )
          )
        case arg =>
          throw UnimplementedOverloadException("#|vec-dump", List(arg))
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
    addDirect(
      "Ṡ",
      "Vectorised Sums | Integer Division",
      List(
        "vectorised-sums",
        "vec-sums",
        "integer-division",
        "int-div",
        "int-rizz",
        "sums",
      ),
      Some(1),
      "a: lst -> sum of each element of a",
      "a: num, b: num -> a // b",
    ) { ctx ?=>
      ctx.pop() match
        case b: VNum =>
          val a = ctx.pop()
          (a, b) match
            case (a: VNum, b: VNum) => ctx.push((a / b).floor)
            case (a: VList, b: VNum) => ctx.push(
                VList.from(
                  a.lst.map(x =>
                    x match
                      case n: VNum => (n / b).floor
                      case _ =>
                        throw InvalidListOverloadException("Ṡ", a, "Numbers")
                  )
                )
              )
            case (a, b) => throw UnimplementedOverloadException("Ṡ", List(a, b))
        case a: VList =>
          ctx.push(a.vmap(x => ListHelpers.sum(ListHelpers.makeIterable(x))))
        case arg => throw UnimplementedOverloadException("Ṡ", List(arg))
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
      List("wrap-length", "pred-slice-0", "size-chunk"),
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
      case (a: VList, b: VList) =>
        if b.lst.forall(_.isInstanceOf[VNum]) then
          ListHelpers.partitionBy(a, b.lst.map(_.asInstanceOf[VNum]))
        else throw InvalidListOverloadException("Ẇ", b, "Number")
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
      "a: lst[num] -> apl-style iota from 0 to a",
      "a: str -> is a lowercase?",
    ) {
      case a: VNum => NumberHelpers.range(0, a)
      case a: VList if a.forall(_.isInstanceOf[VNum]) =>
        NumberHelpers.range(0, a.map(_.asInstanceOf[VNum]))
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
      "a: lst, b: num>=0 -> [a[0], a[1], ..., a[b-1]]",
      "a: lst, b: num<0 -> [a[b + 1], a[b + 2], ..., a[-1]]",
      "a: lst, b: lst[num] -> apl style take",
    ) {
      case (a, b: VNum) => ListHelpers.take(ListHelpers.makeIterable(a), b)
      case (a: VNum, b: (VList | String)) =>
        ListHelpers.take(ListHelpers.makeIterable(b), a)
      case (a: VList, b: VList) =>
        if !b.lst.forall(_.isInstanceOf[VNum]) then ???
        else ListHelpers.take(a, b.lst.map(_.asInstanceOf[VNum]))
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
      List("set-register", "->register", "set-reg", "->reg"),
      Some(1),
      "a: any -> register = a",
    ) { ctx ?=>
      ctx.globals.register = ctx.pop()
    },
    addDirect(
      "¥",
      "Get Register",
      List("get-register", "get-reg", "register", "<-register", "<-reg"),
      None,
      " -> push the value of the register",
    ) { ctx ?=>
      ctx.push(ctx.globals.register)
    },
    addDirect(
      "←",
      "Rotate Stack Left",
      List("rotate-stack-left"),
      None,
      " -> rotate the entire stack left once",
    ) { ctx ?=>
      ctx.rotateLeft
    },
    addDirect(
      "→",
      "Rotate Stack Right",
      List("rotate-stack-right"),
      None,
      " -> rotate the entire stack right once",
    ) { ctx ?=>
      ctx.rotateRight
    },
    addDirect(
      "`",
      "Length of Stack",
      List("length-of-stack", "stack-length", "stack-len"),
      None,
      " -> push the length of the stack",
    ) { ctx ?=>
      ctx.push(ctx.length)
    },
    addDirect(
      "\\",
      "Dump",
      List("dump"),
      Some(1),
      "a: any -> dump all values on the stack",
    ) { ctx ?=>
      ListHelpers.makeIterable(ctx.pop()).foreach(v => ctx.push(v))
    },
    addPart(
      Monad,
      "†",
      "Length of Consecutive Groups",
      List("len-consecutive", "gvl", "gavel"),
      false,
      "a: any -> lengths of consecutive groups of a",
    ) {
      case a =>
        val iterable = ListHelpers.makeIterable(a)
        val groups = ListHelpers.groupConsecutive(iterable)
        VList.from(groups.map(ListHelpers.makeIterable(_).length))
    },
    addPart(
      Monad,
      "Π",
      "Product",
      List("product", "prod"),
      false,
      "a: lst -> product of a",
    ) {
      case a: VList => ListHelpers.product(a)
      case a: String => ListHelpers.product(ListHelpers.makeIterable(a))
      case a: VNum => ListHelpers.product(ListHelpers.makeIterable(a))
    }

    // Input reading
    ,
    addDirect(
      "⁰",
      "First Input",
      List("first-input", "input-0"),
      Some(0),
      "The first input to the program",
    ) { ctx ?=>
      if ctx.globals.inputs.nonEmpty then ctx.push(ctx.globals.inputs(0))
      else ctx.push("0")
    },
    addDirect(
      "¹",
      "Second Input",
      List("second-input", "input-1"),
      Some(0),
      "The second input to the program",
    ) { ctx ?=>
      if ctx.globals.inputs.length > 1 then ctx.push(ctx.globals.inputs(1))
      else ctx.push(VList.from(Seq.empty))
    },
    addPart(
      Monad,
      "⁺",
      "Powerset",
      List("powerset"),
      false,
      "a: lst -> powerset of a",
    ) {
      case a: VList => ListHelpers.powerset(a)
      case a: String =>
        val temp = ListHelpers.powerset(ListHelpers.makeIterable(a))
        VList.from(temp.map(_.asInstanceOf[VList].mkString))
      case a: VNum => ListHelpers.powerset(ListHelpers.makeIterable(a))
    },
    addPart(
      Monad,
      "⌈",
      "Ceiling",
      List("ceiling", "ceil"),
      true,
      "a: num -> ceil(a)",
    ) {
      case a: VNum => a.ceil
    },
    addPart(
      Monad,
      "⌊",
      "Floor",
      List("floor", "str-num", "str->num", "str-to-num"),
      true,
      "a: num -> floor(a)",
      "a: str -> cast a to num by ignoring non-numeric digits. Returns 0 if there's no valid number",
    ) {
      case a: VNum => a.floor
      case a: String =>
        if a.isEmpty then 0
        else
          val filtered = a.filter(c => c.isDigit || "-.".contains(c))
          val negated =
            s"${filtered.headOption.getOrElse(0)}${filtered.tail.replace("-", "")}"
          val decimaled = negated.splitAt(negated.indexOf('.')) match
            case ("", s) =>
              if a.count('.' == _) > 1 then s.stripPrefix(".") else s
            case (a, b) => a + "." + b.replace(".", "")
          val zeroless =
            if decimaled.startsWith("-") then
              "-" + decimaled.drop(1).dropWhile(_ == '0')
            else decimaled.dropWhile(_ == '0')
          if zeroless.isEmpty then 0
          else MiscHelpers.eval(zeroless)
    },
    addPart(
      Monad,
      "ṅ",
      "Palindromise",
      List("palindromise", "palindrome", "ab->aba"),
      false,
      "a: any -> palindromise a",
    ) {
      case a: VList => ListHelpers.palindromise(a)
      case a: String => ListHelpers.palindromise(a)
      case a: VNum => ListHelpers.palindromise(a)
    },
    addPart(
      Dyad,
      "Q",
      "Remove At | Regex Groups",
      List("remove-at", "re-groups", "regex-groups"),
      false,
      "a: lst, b: num -> a with bth element removed",
      "a: str, b: str -> regex groups of a with regex b",
    ) {
      case (a: String, b: VNum) =>
        val index = b.toInt
        if index < 0 then
          a.take(a.length + index) + a.drop(a.length + index + 1)
        else a.take(index) + a.drop(index + 1)
      case (a, b: VNum) =>
        val lst = ListHelpers.makeIterable(a)
        val index = b.toInt
        if index < 0 then
          VList.from(
            lst.take(lst.length + index) ++ lst.drop(lst.length + index + 1)
          )
        else VList.from(lst.take(index) ++ lst.drop(index + 1))
      case (a: String, b: String) =>
        val res = StringHelpers.r(b).findFirstMatchIn(a)
        if res.isDefined then VList.from(res.get.subgroups) else VList.empty
    },
    addPart(
      Dyad,
      "Þ0",
      "Zero Pad",
      List("zero-pad", "pizza-tower"),
      false,
      "a: lst|str, b: num -> a padded with 0s to length b. Positive b prepends 0s, negative b appends 0s",
      "a: lst|str, b: lst|str -> a padded with 0s to length of b. Positive b prepends 0s, negative b appends 0s",
    ) {
      case (a: VList, b: VNum) => ListHelpers.zeroPad(a, b)
      case (a: String, b: VNum) => StringHelpers.zeroPad(a, b)
      case (a: VNum, b: VNum) => StringHelpers.zeroPad(a.toString, b)
      case (a: VNum, b: VList) => ListHelpers.zeroPad(b, a)
      case (a: VNum, b: String) => StringHelpers.zeroPad(b, a)
      case (a: VList, b) => ListHelpers.zeroPad(a, makeIterable(b).bigLength)
      case (a: String, b) => StringHelpers.zeroPad(a, makeIterable(b).bigLength)
    },
    addPart(
      Monad,
      "'",
      "Join Sublists on Spaces then Newlines (Element Form of ')",
      List("join-sublists", "join-sublists-on-spaces-then-newlines", "grid"),
      false,
      "a: lst -> sublists of a joined on spaces then that joined on newlines",
    ) {
      case a =>
        makeIterable(a).map(v => makeIterable(v).mkString(" ")).mkString("\n")
    },

    // Constants
    addNilad("ð", "Space", List("space"), "\" \"") { " " },
    addNilad("¶", "Newline", List("newline"), "chr(10)") { "\n" },
    addNilad("•", "Asterisk", List("asterisk"), "\"*\"") { "*" },
    addNilad("₀", "Ten", List("ten", "l0"), "10") { 10 },
    addNilad("₁", "Sixteen", List("sixteen", "l6"), "16") { 16 },
    addNilad("₂", "Twenty-six", List("twenty-six", "Z6", "z6"), "26") { 26 },
    addNilad("₃", "Thirty-two", List("thirty-two", "E2"), "32") { 32 },
    addNilad("₄", "Sixty-four", List("sixty-four", "b4"), "64") { 64 },
    addNilad("₅", "One hundred", List("one-hundred", "l00"), "100") { 100 },
    addNilad(
      "₆",
      "One hundred twenty-eight",
      List("one-hundred-twenty-eight", "l28"),
      "128",
    ) { 128 },
    addNilad(
      "₇",
      "Two hundred fifty-six",
      List("two-hundred-fifty-six", "Z56", "z56"),
      "256",
    ) { 256 },
    addNilad(
      "₈",
      "-1",
      List("negative-one", "neg-1", "-1"),
      "-1",
    ) { -1 },
    addNilad(
      "₉",
      "Empty string",
      List("empty-string", "<>"),
      "\"\"",
    ) { "" },

    // k-constants
    addNilad(
      "kH",
      "Hello, World!",
      List("hello-world!", "HW!"),
      "\"Hello, World!\"",
    ) { "Hello, World!" },
    addNilad(
      "kh",
      "Hello World",
      List("hello-world", "HW"),
      "\"Hello World\"",
    ) { "Hello World" },
    addNilad(
      "kF",
      "FizzBuzz",
      List("fizzbuzz", "FB"),
      "\"FizzBuzz\"",
    ) { "FizzBuzz" },
    addNilad(
      "kf",
      "Fizz",
      List("fizz", "FIZZ"),
      "\"Fizz\"",
    ) {
      "Fizz"
    },
    addNilad(
      "kb",
      "Buzz",
      List("buzz", "BUZZ"),
      "\"Buzz\"",
    ) {
      "Buzz"
    },
    addNilad(
      "kA",
      "Uppercase Alphabet",
      List("uppercase-alphabet", "uppercase-alpha", "A->Z", "A-Z", "amazon"),
      "\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\"",
    ) { "ABCDEFGHIJKLMNOPQRSTUVWXYZ" },
    addNilad(
      "ka",
      "Lowercase Alphabet",
      List("lowercase-alphabet", "lowercase-alpha", "a->z", "a-z"),
      "\"abcdefghijklmnopqrstuvwxyz\"",
    ) { "abcdefghijklmnopqrstuvwxyz" },
    addNilad(
      "ke",
      "Euler's Number",
      List("euler's-number", "euler", "e-num"),
      "2.718281828459045",
    ) {
      spire.math.Real.e
    },
    addNilad(
      "k1",
      "1000",
      List("one-thousand", "l000", "lk"),
      "1000",
    ) {
      1000
    },
    addNilad(
      "k2",
      "10000",
      List("ten-thousand", "l0000", "l0k"),
      "10000",
    ) {
      10000
    },
    addNilad(
      "k3",
      "100000",
      List("one-hundered-thousand", "l00000", "l00k"),
      "100000",
    ) {
      100000
    },
    addNilad(
      "k4",
      "1000000",
      List("one-million", "l000000", "l000k", "lm"),
      "1000000",
    ) {
      1000000
    },
    addNilad(
      "kL",
      "Lowercase and Uppercase Alphabet",
      List(
        "lowercase-and-uppercase-alphabet",
        "lowercase-and-uppercase-alpha",
        "a->zA->Z",
        "a-zA-Z",
      ),
      "\"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ\"",
    ) {
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    },
    addNilad(
      "kd",
      "Digits",
      List("digits", "digs", "o-9"),
      "\"0123456789\"",
    ) {
      "0123456789"
    },
    addNilad(
      "k6",
      "Hex Digits (lowercase)",
      List(
        "hex-digits",
        "hex-digs",
        "hex-lowercase",
        "hex-lower",
        "hex-l",
        "hex-lc",
      ),
      "\"0123456789abcdef\"",
    ) {
      "0123456789abcdef"
    },
    addNilad(
      "k^",
      "Hex Digits (uppercase)",
      List(
        "hex-uppercase",
        "hex-upper",
        "hex-u",
        "hex-uc",
      ),
      "\"0123456789ABCDEF\"",
    ) {
      "0123456789ABCDEF"
    },
    addNilad(
      "ko",
      "Octal Digits",
      List("octal-digits", "octal-digs", "o-7"),
      "\"01234567\"",
    ) {
      "01234567"
    },
    addNilad(
      "kp",
      "Punctuation",
      List("punctuation", "punct"),
      "All punctuation characters",
    ) {
      // Code from https://alvinalexander.com/source-code/scala-sequence-list-all-ascii-printable-characters/
      ((' ' to '/').toList ++:
        (':' to '@').toList ++:
        ('[' to '`').toList ++:
        ('{' to '~').toList).mkString
    },
    addNilad(
      "kP",
      "Printable Ascii",
      List("printable-ascii", "all-ascii"),
      "All of printable ascci. That excludes newline",
    ) {
      // https://alvinalexander.com/source-code/scala-sequence-list-all-ascii-printable-characters/
      ((' ' to '~').toList).mkString
    },
    addNilad(
      "kr",
      "Digits, Lowercase, Uppercase",
      List(
        "digits-lowercase-uppercase",
        "digs-lower-upper",
        "o9azAZ",
        "o-9a-zA-Z",
      ),
      "\"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ\"",
    ) {
      "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    },
    addNilad(
      "kR",
      "Digits, Uppercase, Lowercase",
      List(
        "digits-uppercase-lowercase",
        "digs-upper-lower",
        "o9AZaz",
        "o-9A-Za-z",
      ),
      "\"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz\"",
    ) {
      "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    },
    addNilad(
      "kB",
      "Uppercase and lowercase",
      List(
        "uppercase-and-lowercase",
        "uppercase-and-lowercase-alpha",
        "A->Za->z",
        "A-Za-z",
      ),
      "\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz\"",
    ) {
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    },
    addNilad(
      "kZ",
      "Uppercase Alphabet Reversed",
      List("uppercase-alphabet-reversed", "uppercase-alpha-reversed", "Z->A"),
      "\"ZYXWVUTSRQPONMLKJIHGFEDCBA\"",
    ) {
      "ZYXWVUTSRQPONMLKJIHGFEDCBA"
    },
    addNilad(
      "kz",
      "Lowercase Alphabet Reversed",
      List(
        "lowercase-alphabet-reversed",
        "lowercase-alpha-reversed",
        "z->a",
        "nozama",
      ),
      "\"zyxwvutsrqponmlkjihgfedcba\"",
    ) {
      "zyxwvutsrqponmlkjihgfedcba"
    },
    addNilad(
      "kl",
      "Upper and Lowercase Alphabet Reversed",
      List(
        "upper-and-lowercase-alphabet-reversed",
        "upper-and-lowercase-alpha-reversed",
        "Z->Az->a",
        "Z-Az-a",
      ),
      "\"ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba\"",
    ) {
      "ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba"
    },
    addNilad(
      "ki",
      "Pi",
      List("pi", "E-14", "E-1415926535897"),
      "Literally just pi",
    ) {
      spire.math.Real.pi
    },
    addNilad(
      "kg",
      "Phi",
      List("phi", "golden-ratio", "golden", "l-618033988749895"),
      "Literally just phi",
    ) {
      spire.math.Real.phi
    },
    addNilad(
      "kD",
      "Lines",
      List("lines", "dashes", "bars"),
      "\"|/-\\_\"",
    ) {
      "|/-\\_"
    },
    addNilad(
      "ÞṆ",
      "Set of Natural Numbers",
      List("NN"),
      "The set of all natural numbers",
    ) {
      VList.from(LazyList.unfold(VNum(1)) {
        case VNum(n, _) => Some((VNum(n), VNum(n + 1)))
      })
    },
    addNilad(
      "ÞṬ",
      "Set of Integers",
      List("ZZ"),
      "The set of all integers",
    ) {
      VList.from(
        LazyList.unfold(VNum(0) -> true) {
          case (num, negate) =>
            val now = if negate then -num else num
            val next = if negate then num + 1 else num
            Some((now, next -> !negate))
        }
      )
    },
    addNilad(
      "ÞP",
      "Set of All Primes",
      List("PP", "primes"),
      "The set of all primes",
    ) {
      NumberHelpers.probablePrimes
    },
    addPart(
      Monad,
      "∆s",
      "Sine",
      List("sin", "sine"),
      true,
      "a: num -> sin(a)",
    ) {
      case a: VNum => a.sin
    },
    addPart(
      Monad,
      "∆c",
      "Cosine",
      List("cos", "cosine"),
      true,
      "a: num -> cos(a)",
    ) {
      case a: VNum => a.cos
    },
    addPart(
      Monad,
      "∆t",
      "Tangent",
      List("tan", "tangent"),
      true,
      "a: num -> tan(a)",
    ) {
      case a: VNum => a.tan
    },
    addPart(
      Monad,
      "∆ṡ",
      "Arcsine / Inverse Sine",
      List("asin", "arcsin", "arcsine"),
      true,
      "a: num -> asin(a)",
    ) {
      case a: VNum => a.asin
    },
    addPart(
      Monad,
      "∆ċ",
      "Arccosine / Inverse Cosine",
      List("acos", "arccos", "arccosine"),
      true,
      "a: num -> acos(a)",
    ) {
      case a: VNum => a.acos
    },
    addPart(
      Monad,
      "∆ṫ",
      "Arctangent / Inverse Tangent",
      List("atan", "arctan", "arctangent"),
      true,
      "a: num -> atan(a)",
    ) {
      case a: VNum => a.atan
    },
    addPart(
      Dyad,
      "∆Ṫ",
      "Dyadic Arctangent / Dyadic Inverse Tangent",
      List("atan2", "arctan2", "arctangent2"),
      true,
      "y: num, x: num -> atan2(y, x)",
    ) {
      case (y: VNum, x: VNum) => y.atan2(x)
    },
    addPart(
      Monad,
      "∆S",
      "Hyperbolic Sine",
      List("sinh", "hyperbolic-sine"),
      true,
      "a: num -> sinh(a)",
    ) {
      case a: VNum => a.sinh
    },
    addPart(
      Monad,
      "∆C",
      "Hyperbolic Cosine",
      List("cosh", "hyperbolic-cosine"),
      true,
      "a: num -> cosh(a)",
    ) {
      case a: VNum => a.cosh
    },
    addPart(
      Monad,
      "∆T",
      "Hyperbolic Tangent",
      List("tanh", "hyperbolic-tangent"),
      true,
      "a: num -> tanh(a)",
    ) {
      case a: VNum => a.tanh
    },
    addPart(
      Monad,
      "∆<",
      "Argument / Phase / Angle",
      List("arg", "phase", "angle"),
      true,
      "a: num -> Arg(a)",
    ) {
      case a: VNum => a.arg
    },
    addPart(
      Monad,
      "∆R",
      "Real Part",
      List("real", "real-part"),
      true,
      "a: num -> Re(a)",
    ) {
      case a: VNum => VNum(a.real)
    },
    addPart(
      Monad,
      "∆I",
      "Imaginary Part",
      List("imag", "imaginary", "imaginary-part"),
      true,
      "a: num -> Im(a)",
    ) {
      case a: VNum => VNum(a.imag)
    },
    addPart(
      Monad,
      "∆ṙ",
      "Degrees to Radians",
      List("deg2rad", "deg-to-rad"),
      true,
      "a: num -> a from degrees to radians (a * pi / 180)",
    ) {
      case a: VNum => a * VNum(spire.math.Real.pi) / VNum(180)
    },
    addPart(
      Monad,
      "∆ḋ",
      "Radians to Degrees",
      List("rad2deg", "rad-to-deg"),
      true,
      "a: num -> a from radians to degrees (a * 180 / pi)",
    ) {
      case a: VNum => a / VNum(spire.math.Real.pi) * VNum(180)
    },
    addPart(
      Dyad,
      "ÞR",
      "Reshape",
      List("reshape"),
      false,
      "a: lst, b: lst[num] => a reshaped to shape b",
    ) {
      case (a, b: VList) =>
        val shape = b.map {
          case n: VNum => n
          case other => throw BadRHSException(
              "ÞR",
              s"$b (expected a list of natural numbers)",
            )
        }
        ListHelpers.reshape(ListHelpers.makeIterable(a), shape)
      case (a, b: VNum) =>
        ListHelpers.reshape(ListHelpers.makeIterable(a), Seq(b))
    },
    addPart(
      Monad,
      "∆Ṛ",
      "Principal Root Of Unity",
      List("root-of-unity"),
      true,
      "a: num => principal a-th root of unity (e^(2i * pi / a))",
    ) {
      case a: VNum => VNum(spire.math.Real.e) **
          (VNum.complex(0, 2) * VNum(spire.math.Real.pi) / a)
    },
    addPart(
      Monad,
      "∆A",
      "Arithmetic Mean",
      List("mean", "arithmetic-mean"),
      false,
      "a: lst[num] => arithmetic mean of a (sum(a) / len(a))",
    ) {
      case a: VList if a.forall(_.isInstanceOf[VNum]) =>
        a.map(_.asInstanceOf[VNum]).sum / a.length
      case a: VNum => a
    },
    addPart(
      Monad,
      "∆G",
      "Geometric Mean",
      List("geometric-mean"),
      false,
      "a: lst[num] => geometric mean of a (prod(a) ** (1 / len(a)))",
    ) {
      case a: VList if a.forall(_.isInstanceOf[VNum]) =>
        a.map(_.asInstanceOf[VNum]).product ** (1 / VNum(a.length))
      case a: VNum => a
    },
    addPart(
      Monad,
      "∆H",
      "Harmonic Mean",
      List("harmonic-mean"),
      false,
      "a: lst[num] => harmonic mean of a (len(a) / sum(1 / a))",
    ) {
      case a: VList if a.forall(_.isInstanceOf[VNum]) =>
        a.length / a.map(_.asInstanceOf[VNum]).map(1 / _).sum
      case a: VNum => a
    },
    addFull(
      Monad,
      "ÞỊ",
      "Indices Where Truthy",
      List("where", "where-truthy", "indices-truthy", "indices-where-truthy"),
      false,
      "a: lst => indices of truthy elements of a",
    ) { a => ListHelpers.truthyIndices(ListHelpers.makeIterable(a)) },
  )

  private def execHelper(value: VAny)(using ctx: Context): VAny =
    value match
      case code: String =>
        val originalMode = ctx.settings.endPrintMode
        ctx.settings = ctx.settings.useMode(EndPrintMode.None)
        Interpreter.execute(code)(using ctx)
        ctx.settings = ctx.settings.useMode(originalMode)
        ctx.pop()
      case n: VNum => 10 ** n
      case list: VList => list.vmap(execHelper)
      case fn: VFun =>
        val res = Interpreter.executeFn(fn)
        if fn.arity == -1 then
          ctx.pop() // Handle the extra value pushed by lambdas that operate on the stack
        res
      case _: VObject => throw BadArgumentException("exec", "object")
      case con: VConstructor => Interpreter.createObject(con)
    end match
  end execHelper

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
