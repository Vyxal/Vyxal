package vyxal.elements

import scala.language.implicitConversions

import vyxal.*
import vyxal.{Dyad, ImplHelpers, Monad, Tetrad, Triad}
import vyxal.parsing.TokenType
import vyxal.toBool
import vyxal.Context.{copyCtx, peek, pop, push}
import vyxal.Context.given
import vyxal.ListHelpers.makeIterable
import vyxal.MiscHelpers.defaultEmpty
import vyxal.StringHelpers.padLeft
import vyxal.VNum.given

import scala.io.StdIn
import scala.util.matching.Regex

given (using Context): Ordering[VAny] with
  override def compare(x: VAny, y: VAny): Int = MiscHelpers.compare(x, y)

extension (a: VAny)(using Context) def itr = ListHelpers.makeIterable(a)
extension (a: VAny)(using Context)
  def ritr = ListHelpers.makeIterable(a, Some(true))

extension (a: String)(using Context) def toNum: VNum = VNum(a)

object NewElements:
  case class Element(
      arity: Int,
      impl: DirectFn,
  )

  val elements: Map[String, Element] = Map(
    "⊞" ->
      direct(Monad) {
        val iterable = ListHelpers.makeIterable(pop())
        val uniq = iterable.distinct
        val counts = uniq.map(item => VNum(iterable.count(_ == item)))
        push(VList.from(counts))
      },
    addPart("÷", Dyad, true) {
      case (a: VNum, b: VNum) => a / b
      case (a: String, b: VNum) => StringHelpers.intoNPieces(a, b)
      case (a: VNum, b: String) => StringHelpers.intoNPieces(b, a)
      case (a: String, b: String) => StringHelpers.split(a, Regex.quote(b))
    },
    "×" -> fullToImpl(Dyad, MiscHelpers.multiply),
    addPart("∧", Dyad, true) {
      case (b: VVal, a: VVal) => if !a.toBool then a else b
    },
    addPart("∨", Dyad, true) {
      case (b: VVal, a: VVal) => if a.toBool then a else b
    },
    addPart("¬", Monad, false) { a =>
      VNum(!a.toBool)
    },
    addPart("ʀ", Monad, true) {
      case a: VNum => NumberHelpers.range(0, a - a.signum)
      case a: String => a.toLowerCase()
    },
    addPart("ʁ", Monad, true) {
      case a: VNum =>
        val endpoint = a + 1
        NumberHelpers.range(0, endpoint - endpoint.signum)
      case a: String => a.toUpperCase()
    },
    addPart("ɾ", Monad, true) {
      case a: VNum => NumberHelpers.range(1, a)
      case a: String if a.length() == 1 => a.head.isLetter
      case a: String => VList.from(a.map(char => VNum(char.isLetter)))
    },
    addPart("‹", Monad, true) {
      case a: VNum => a - 1
      case a: String =>
        val length = a.length()
        val padLength = (8 - length % 8)
        "0".repeat(padLength) + a
    },
    addPart("›", Monad, true) {
      case a: VNum => a + 1
      case a: String => a.replace(" ", "0")
    },
    addPart("!", Monad, true) {
      case a @ VNum(r, i) =>
        if r.isWhole then spire.math.fact(spire.math.abs(a.toLong))
        else NumberHelpers.gamma(spire.math.abs(a.underlying.real) + 1)
      case a: String => StringHelpers.titlecase(a)
    },
    "$" ->
      direct(Dyad) {
        val b, a = pop()
        push(b, a)
      },
    "%" -> fullToImpl(Dyad, MiscHelpers.modulo),
    addPart("&", Dyad, false) {
      case (a, b) => VList.from(a.itr :+ b)
    },
    addPart("*", Dyad, false) {
      case (a: VNum, b: VNum) => a ** b
    },
    "+" -> fullToImpl(Dyad, MiscHelpers.add),
    "," ->
      direct(Monad) {
        MiscHelpers.vyPrintln(pop())
        summon[Context].globals.printed = true
      },
    "-" -> fullToImpl(Dyad, MiscHelpers.subtract),
    ":" ->
      direct(Monad) {
        val a = pop()
        push(a, a)
      },
    ";" ->
      direct(Dyad) {
        val b = pop()
        val a = pop()
        push(VList(a, b))
      },
    addPart("<", Dyad, true) {
      case (a: VVal, b: VVal) => a < b
    },
    addPart("=", Dyad, true) {
      case (a: VNum, b: VNum) => a == b
      case (a: VNum, b: String) => a.toString == b
      case (a: String, b: VNum) => a == b.toString
      case (a: String, b: String) => a == b
    },
    addPart(">", Dyad, true) {
      case (a: VVal, b: VVal) => a > b
    },
    "?" ->
      niladify(ctx ?=>
        if ctx.globals.inputs.nonEmpty then ctx.globals.inputs.next()
        else if ctx.settings.online then ctx.settings.defaultValue
        else
          val temp = StdIn.readLine()
          if temp.nonEmpty then MiscHelpers.eval(temp)
          else ctx.settings.defaultValue
      ),
    addPart("@", Dyad, true) {
      case (a: VNum, b: VNum) => (a - b).vabs
      case (a: String, b: String) => StringHelpers.levenshtein(a, b)
      case (a: VPhysical, b: VFun) =>
        FuncHelpers.reduceOverPairs(b, makeIterable(a))
      case (a: VFun, b: VPhysical) =>
        FuncHelpers.reduceOverPairs(a, makeIterable(b))
    },
    addPart("A", Monad, false) {
      case a: VNum => ListHelpers.makeIterable(a).forall(_.toBool)
      case a: String if a.length == 1 => StringHelpers.isVowel(a.head)
      case a: String => VList.from(a.map(StringHelpers.isVowel))
      case a: VList => a.forall(_.toBool)
    },
    addPart("B", Monad, true) {
      case a: VNum => NumberHelpers.toBinary(a)
      case a: String => VList(
          a.map(x => NumberHelpers.toBinary(StringHelpers.chrord(x.toString)))*
        )
    },
    addPart("C", Dyad, false) {
      case (a: VList, b: VVal) => a.count(_ === b)
      case (a: VVal, b: VList) => b.count(_ === a)
      case (a: VList, b: VList) => ListHelpers.countDepth(a, b)
      case (a, b) => StringHelpers.countString(a.toString, b.toString)
    },
    "D" ->
      direct(Monad) {
        val a = pop()
        push(a, a, a)
      },
    addPart("E", Monad, true) {
      case a: VNum => VNum(2) ** a
      case a: String => MiscHelpers.eval(a)
    },
    addPart("F", Dyad, false) {
      case (a: VFun, b) => ListHelpers.filter(b.ritr, a)
      case (a, b: VFun) => ListHelpers.filter(a.ritr, b)
      case (a: String, b: String) => a.indexOf(b)
      case (a: VNum, b: VNum) => a.toString.indexOf(b.toString)
      case (a: VList, b: VVal) => a.indexOf(b)
      case (a: VVal, b: VList) => b.indexOf(a)
      case (a, b) =>
        val aList = ListHelpers.makeIterable(a)
        val bList = ListHelpers.makeIterable(b)
        val Seq(needle, haystack) =
          Seq(aList, bList).sortBy(ListHelpers.maxDepth)
        haystack.indexOf(needle)
    },
    "G" ->
      direct(Dyad) {
        val top = pop()
        top match
          case a: VList => push(a.maxOption.getOrElse(VList()))
          case _ =>
            val under = pop()
            (top, under) match
              case (a: VFun, b: VList) => push(ListHelpers.generate(a, b))
              case (a: VFun, b) => push(ListHelpers.generate(a, VList(b)))
              case _ => push(MiscHelpers.dyadicMaximum(under, top))
      },
    addPart("H", Monad, true) {
      case a: VNum => NumberHelpers.toBaseAlphabet(a, "0123456789ABCDEF")
      case a: String => NumberHelpers.fromBaseAlphabet(a, "0123456789ABCDEF")
    },
    addPart("I", Dyad, false) {
      case (a, b: VFun) => VList.from(a.ritr.filter(x => !b(x).toBool))
      case (a, b) =>
        val temp = ListHelpers.interleave(a.itr, b.itr)
        if a.isInstanceOf[String] && b.isInstanceOf[String] then temp.mkString
        else temp
    },
    addPart("J", Dyad, false) {
      case (a: VList, b: VList) => VList.from(a ++ b)
      case (a, b: VList) => VList.from(a +: b)
      case (a: VList, b) => VList.from(a :+ b)
      case (a: VNum, b: VNum) => VList(a, b)
      case (a, b) => a.toString + b.toString
    },
    addPart("K", Monad, true) {
      case a: VNum => NumberHelpers.factors(a)
      case a: String => VNum(VNum.DecimalRegex.matches(a))
    },
    "L" ->
      direct(Monad) {
        val a = pop()
        push(a.itr.length)
      },
    addPart("M", Dyad, true) {
      case (a: VFun, b) => ListHelpers.map(a, b.ritr)
      case (a, b: VFun) => ListHelpers.map(b, a.ritr)
      case (a: VList, b: VList) => ListHelpers.mold(a, b)
      case (a: VNum, b: VNum) => NumberHelpers.multiplicity(a, b)
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
    addPart("N", Monad, true) {
      case a: VNum => -a
      case a: String => StringHelpers.swapCase(a)
      case a: VFun => MiscHelpers.firstNonNegative(a)
    },
    addPart("O", Monad, false) {
      case a: VNum => StringHelpers.chrord(a)
      case a: String => StringHelpers.chrord(a)
      case a: VList =>
        val temp = a.map(StringHelpers.chrord)
        if temp.forall(_.isInstanceOf[String]) then temp.mkString
        else VList.from(temp)
    },
    addPart("P", Monad, false) {
      case a: VList => VList.from(ListHelpers.prefixes(a))
      case a: String => VList.from(
          ListHelpers.prefixes(a.itr).map(_.mkString)
        )
      case a: VNum => VList.from(
          ListHelpers
            .prefixes(a.vabs.itr)
            .map(n => MiscHelpers.eval(n.mkString))
        )
    },
    addPart("Q", Dyad, false) {
      case (a: String, b: VNum) =>
        val index = b.toInt
        if index < 0 then
          a.take(a.length + index) + a.drop(a.length + index + 1)
        else a.take(index) + a.drop(index + 1)
      case (a, b: VNum) =>
        val lst = a.itr
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
    addPart("R", Dyad, false) {
      case (a: VNum, b: VNum) => NumberHelpers.range(a, b).dropRight(1)
      case (a: String, b: String) => StringHelpers.r(b).findFirstIn(a).isDefined
      case (a: String, b: VNum) => StringHelpers.r(b).findFirstIn(a).isDefined
      case (a: VNum, b: String) =>
        StringHelpers.r(b).findFirstIn(a.toString).isDefined
      case (a: VFun, b) => ListHelpers.reduce(b, a)
      case (a, b: VFun) => ListHelpers.reduce(a, b)
    },
    addPart("S", Monad, false) {
      case s: String => s.sorted
      case a => VList.from(
          a.itr.sorted(MiscHelpers.compare(_, _))
        )
    },
    addPart("T", Monad, false) {
      case a: VNum => a * 3
      case a: String => a.forall(_.isLetter)
      case a: VList => ListHelpers.transpose(a)
    },
    "U" ->
      direct(Monad) {
        val top = pop()
        val itr = top.itr
        val odds = itr.zipWithIndex.collect { case (x, i) if i % 2 == 0 => x }
        val evens = itr.zipWithIndex.collect { case (x, i) if i % 2 == 1 => x }
        top match
          case _: String => push(odds.mkString, evens.mkString)
          case _: VNum => push(odds.mkString.toNum, evens.mkString.toNum)
          case _ => push(VList.from(odds), VList.from(evens))

      },
    addPart("V", Monad, false) {
      case a: VList => VList.from(a.map(ListHelpers.reverse))
      case a: VNum => 1 - a
    },
    "W" ->
      direct(-1) {
        summon[Context].wrap()
      },
    "X" -> fullToImpl(Dyad, ListHelpers.cartesianProduct(_, _)),
    addPart("Y", Dyad, false) {
      case (a, b: VNum) => VList.fill(b.toInt)(a)
      case (a: VNum, b) => VList.fill(a.toInt)(b)
      case (a: (VList | String), b: VList) =>
        val temp = b
          .map {
            case n: VNum => n.toInt
            case l: (String | VList) => ListHelpers.makeIterable(l).length
            case _ =>
              // Function / Object, which doesn't have a reasonable
              // way to convert to a number
              throw InvalidListOverloadException("Y", b, "Number")
          }
          .lazyZip(ListHelpers.makeIterable(a))
          .map((n, item) => VList.fill(n)(item))
        if a.isInstanceOf[String] then temp.map(_.mkString).mkString
        else VList.from(temp)
    },
    addPart("Z", Dyad, false) {
      case (a, b: VFun) =>
        val iter = ListHelpers.makeIterable(a)
        VList.from(iter.vzip(ListHelpers.map(b, iter)))
      case (a: VFun, b) =>
        val iter = ListHelpers.makeIterable(b)
        VList.from(ListHelpers.map(a, iter).vzip(iter))
      case (a, b) =>
        ListHelpers.makeIterable(a).vzip(ListHelpers.makeIterable(b))
    },
    "^" ->
      direct(-1) {
        summon[Context].reverse()
      },
    "_" ->
      direct(Monad) {
        pop()
      },
    addPart("a", Monad, false) {
      case a: VNum => a.itr.exists(_ == VNum(0))
      case a: String if a.length == 1 => a.head.isUpper
      case a: String => VList.from(a.map(c => VNum(c.isUpper)))
      case a: VList => a.itr.exists(_.toBool)
    },
    "b" -> fullToImpl(Monad, NumberHelpers.fromBinary),
    addPart("c", Dyad, false) {
      case (a: VVal, b: VVal) => a.toString().contains(b.toString())
      case (a: VList, b: VVal) => a.contains(b)
      case (a: VVal, b: VList) => b.contains(a)
      case (a: VList, b: VList) =>
        val Seq(needle, haystack) = Seq(a, b).sortBy(ListHelpers.maxDepth)
        haystack.contains(needle)
    },
    addPart("d", Monad, true) {
      case a: VNum => a + a
      case a: String => s"$a$a"
    },
    addPart("e", Monad, true) {
      case a: VNum => a % 2 == VNum(0)
      case a: String => VList.from(a.split("\n").toIndexedSeq)
    },
    "f" -> fullToImpl(Monad, x => ListHelpers.flatten(x.itr)),
    "g" ->
      direct(Dyad) {
        val top = pop()
        top match
          case a: VList => push(a.minOption.getOrElse(VList()))
          case _ =>
            val under = pop()
            (top, under) match
              case (a: VFun, b: VList) => push(ListHelpers.generateDyadic(a, b))
              case _ => push(MiscHelpers.dyadicMinimum(under, top))
      },
    "h" -> fullToImpl(Monad, x => x.itr.headOption.getOrElse(defaultEmpty(x))),
    "i" -> fullToImpl(Dyad, MiscHelpers.index),
    addPart("j", Dyad, false) {
      case (a: VList, b) => ListHelpers.join(a, b)
      case (a, b: VList) => ListHelpers.join(b, a)
      case (a, b) => ListHelpers.join(a.itr, b) match
          case l: VList => l.mkString
          case res => res
    },
    addPart("l", Dyad, true) {
      case (a: VNum, b: VNum) => NumberHelpers.log(a, b)
      case (a: String, b: VNum) => a.length == b.toInt
      case (a: String, b: String) => a.length == b.length
      case (a: VNum, b: String) => b.length == a.toInt
      case (a: VPhysical, b: VFun) => MiscHelpers.collectUnique(b, a)
      case (a: VFun, b) => MiscHelpers.collectUnique(a, b)
    },
    "m" -> niladify(ctx ?=> ctx.ctxVarSecondary),
    "n" -> niladify(ctx ?=> ctx.ctxVarPrimary),
    "o" ->
      direct(Dyad) {
        val top = pop()
        top match
          case a: VList => push(VList.from(ListHelpers.overlaps(a, 2)))
          case a: String => push(VList.from(ListHelpers.overlaps(a, 2)))
          case _ =>
            val next = pop()
            (top, next) match
              case (a: VNum, b: String) =>
                push(VList.from(ListHelpers.overlaps(b, a.toInt)))
              case (a: VNum, b: VList) =>
                push(VList.from(ListHelpers.overlaps(b, a.toInt)))
              case (a, b) =>
                throw UnimplementedOverloadException("o", List(a, b))
      },
    addPart("p", Dyad, false) {
      case (a: String, b: (String | VNum)) => b.toString + a
      case (a: VNum, b: String) => b + a.toString
      case (a: VNum, b: VNum) => MiscHelpers.eval(b.toString + a.toString)
      case (a: VList, b) => VList.from(b +: a)
      case (a, b) => VList(b, a)
    },
    "q" -> fullToImpl(Monad, obj => StringHelpers.quotify(obj.toString)),
    addPart("r", Triad, false) {
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
    addPart("s", Dyad, false) {
      case (a: String, b) =>
        if b.isInstanceOf[String] && b.toString.isEmpty then a.itr
        else StringHelpers.split(a, b.toString())
      case (a: VNum, b) => StringHelpers.split(a, b.toString())
      case (a: VList, b) => ListHelpers.splitNormal(a, b)
    },
    "t" ->
      fullToImpl(
        Monad,
        lst => lst.itr.lastOption.getOrElse(MiscHelpers.defaultEmpty(lst)),
      ),
    "u" ->
      direct(Monad) {
        val top = pop()
        top match
          case lst: VList => push(lst.distinct)
          case n: VNum => push(
              MiscHelpers.eval(ListHelpers.makeIterable(n).distinct.mkString)
            )
          case s: String => push(s.distinct.mkString)
          case predicate: VFun =>
            val lst = pop()
            val unique = ListHelpers.dedupBy(lst.itr, predicate)
            lst match
              case _: String => push(unique.mkString)
              case _ => push(unique)
          case _ => throw UnsupportedOverloadException("u", "object")

      },
    addPart("v", Dyad, true) {
      case (a: VNum, b: VNum) => (a / b).floor
    },
    "w" ->
      direct(Monad) {
        push(VList(pop())) // Tacit!
      },
    "x" ->
      direct(-1) {
        val ctx = summon[Context]
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
    addPart("y", Triad, false) {
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
    "z" ->
      fullToImpl(Dyad, (lhs, rhs) => ListHelpers.transpose(lhs.itr, Some(rhs))),
    addPart("⨥", Monad, true) {
      case a: VNum => a + 2
      case a: String => a.length() == 1
    },
    addPart("⨪", Monad, true) {
      case a: VNum => a - 2
    },
    addPart("∑", Monad, false) {
      case a: VVal => ListHelpers.sum(a.itr)
      case a: VList if !a.lst.exists(_.isInstanceOf[String]) =>
        ListHelpers.sum(a)
      case default => MiscHelpers.eval(default.itr.mkString)
    },
    "Π" -> fullToImpl(Monad, lhs => ListHelpers.product(lhs.itr)),
    addPart("σ", Monad, false) {
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
    "⇧" -> fullToImpl(Monad, lhs => ListHelpers.gradeUp(lhs.itr)),
    "⇩" -> fullToImpl(Monad, lhs => ListHelpers.gradeDown(lhs.itr)),
    addPart("∪", Dyad, false) {
      case (a: String, b: String) => a + b.filterNot(a.contains(_))
      case (a, b) => VList.from(a.itr ++ b.itr.filterNot(a.itr.contains(_)))
    },
    addPart("∩", Dyad, false) {
      case (lhs: String, rhs: String) => lhs.filter(rhs.contains(_))
      case (lhs, rhs) => VList.from(lhs.itr.filter(rhs.itr.contains(_)))
    },
    addPart("⊍", Dyad, false) {
      case (lhs: String, rhs: String) => (lhs.itr ^ rhs.itr).mkString
      case (lhs, rhs) => VList.from(lhs.itr ^ rhs.itr)
    },
    addPart("⦰", Dyad, false) {
      case (a: String, b: String) => a.filterNot(b.contains(_))
      case (a: VList, b: (VNum | String)) => a.filter(_ != b)
      case (a: (VNum | String), b: VList) => b.filter(_ != a)
      case (a, b) =>
        val left = ListHelpers.makeIterable(a)
        val right = ListHelpers.makeIterable(b)
        VList.from(left.filterNot(right.contains(_)))
    },
    addPart("«", Dyad, true) {
      case (a: VNum, b: VNum) => a.toBigInt << b.toInt
      case (a: VNum, b: String) => StringHelpers.padLeft(b, a)
      case (a: String, b: VNum) => StringHelpers.padLeft(a, b)
      case (a: String, b: String) => StringHelpers.padLeft(a, b.length)
    },
    addPart("»", Dyad, true) {
      case (a: VNum, b: VNum) => a.toBigInt >> b.toInt
      case (a: VNum, b: String) => StringHelpers.padRight(b, a)
      case (a: String, b: VNum) => StringHelpers.padRight(a, b)
      case (a: String, b: String) => StringHelpers.padRight(a, b.length)
    },
    "Ɠ" ->
      direct(1) {
        val top = peek()
        push(top.itr.maxOption.getOrElse(defaultEmpty(top)))
      },
    "ɠ" ->
      direct(1) {
        val top = peek()
        push(top.itr.minOption.getOrElse(defaultEmpty(top)))
      },
    addPart("Ġ", Dyad, false) {
      case (a: VList, b: VVal) =>
        VList.from(a.map(MiscHelpers.dyadicMaximum(_, b)))
      case (a: VVal, b: VList) =>
        VList.from(b.map(MiscHelpers.dyadicMaximum(a, _)))
      case (a: VList, b: VList) =>
        VList.from(a.zip(b).map((x, y) => MiscHelpers.dyadicMaximum(x, y)))
    },
    addPart("ġ", Dyad, false) {
      case (a: VList, b: VVal) =>
        VList.from(a.map(MiscHelpers.dyadicMinimum(_, b)))
      case (a: VVal, b: VList) =>
        VList.from(b.map(MiscHelpers.dyadicMinimum(a, _)))
      case (a: VList, b: VList) =>
        val zipped = a.vzip(b)
        VList.from(zipped.map(pair =>
          val items = pair.asInstanceOf[VList]
          if items.length == 1 then items.head
          else MiscHelpers.dyadicMinimum(items.head, items(1))
        ))
    },
    addPart("⌈", Monad, false) {
      case a: VNum => a.ceil
      case a: String => VList.from(a.split(" ").toIndexedSeq)
    },
    addPart("⌊", Monad, false) {
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
    addPart("⊖", Dyad, false) {
      case (a, b: VNum) => ListHelpers.take(a.itr, b)
      case (a: VNum, b: (VList | String)) => ListHelpers.take(b.itr, a)
      case (a: VList, b: VList) =>
        if !b.lst.forall(_.isInstanceOf[VNum]) then ???
        else ListHelpers.take(a, b.lst.map(_.asInstanceOf[VNum]))
    },
    addPart("⌽", Dyad, false) {
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
    "£" ->
      direct(Monad) {
        summon[Context].globals.register = pop()
      },
    "¥" ->
      direct(0) {
        push(summon[Context].globals.register)
      },
    "↜" ->
      direct(-1) {
        summon[Context].rotateLeft
      },
    "↝" ->
      direct(-1) {
        summon[Context].rotateRight
      },
    "⬳" ->
      direct(Monad) {
        val top = pop()
        top match
          case a: VIter => push(ListHelpers.rotate(a, 1))
          case a: VNum =>
            val times = a
            val iterable = pop()
            push(ListHelpers.rotate(iterable, times))
          case _ => throw UnsupportedOverloadException("⬳", "function | object")
      },
    "⟿" ->
      direct(Monad) {
        val top = pop()
        top match
          case a: VIter => push(ListHelpers.rotate(a, -1))
          case a: VNum =>
            val times = a
            val iterable = pop()
            push(ListHelpers.rotate(iterable, -times))
          case _ => throw UnsupportedOverloadException("⟿", "function | object")
      },
    addPart("≜", Triad, false) {
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
    addPart("⎀", Triad, false) {
      case (a, b: VNum, c) =>
        ListHelpers.insert(ListHelpers.makeIterable(a), b, c)
      case (a, b: VList, c: VList) =>
        var temp = ListHelpers.makeIterable(a)
        for (i, j) <- b.reverse.zip(c.reverse) do
          (i, j) match
            case (index: VNum, elem) =>
              temp = ListHelpers.insert(temp, index, elem)
            case _ => throw InvalidListOverloadException("⎀", b, "Number")
        temp
      case (a, b: VList, c) => b.foldRight(ListHelpers.makeIterable(a)) {
          case (index: VNum, acc) => ListHelpers.insert(acc, index, c)
          case _ => throw InvalidListOverloadException("⎀", b, "Number")
        }
    },
    "◲" ->
      fullToImpl(
        Monad,
        x =>
          VList.from(
            ListHelpers.mergeInfLists(
              ListHelpers.prefixes(x.itr).map(b => ListHelpers.suffixes(b.itr))
            )
          ),
      ),
    addPart("⊢", Dyad, false) {
      case (number: VNum, base: VNum) => NumberHelpers.toBase(number, base)
      case (number: VNum, baseAlphabet: VIter) =>
        NumberHelpers.toBase(number, baseAlphabet)
      case (list: VList, base: VNum) =>
        VList.from(list.lst.map(NumberHelpers.toBase(_, base)))
      case (list: VList, baseAlphabet: String) =>
        VList.from(list.lst.map(NumberHelpers.toBase(_, baseAlphabet)))
      case (values: VList, bases: VList) => VList.from(
          values
            .vzip(bases)
            .map(item =>
              val Seq(value, base) = item.itr
              NumberHelpers.toBase(value, base)
            )
        )
      case (haystack: String, needle: String) =>
        VList.from(needle.r.findAllIn(haystack).toList)
    },
    "⊣" ->
      fullToImpl(Dyad, (number, base) => NumberHelpers.fromBase(number, base)),
    "ɦ" ->
      direct(Monad) {
        push(peek().itr.headOption.getOrElse(defaultEmpty(peek())))
      },
    "ʈ" ->
      direct(Monad) {
        push(peek().itr.lastOption.getOrElse(defaultEmpty(peek())))
      },
    addPart("ᐐ", Monad, false) {
      case a: VNum => MiscHelpers.eval(a.toString.init)
      case a: String => a.init
      case a: VList if a.lst.nonEmpty => a.init
      case a: VList => VList()
    },
    addPart("ᐵ", Dyad, false) {
      case (iterable: VList, slice: VNum) => iterable.itr.drop(slice)
      case (iterable: String, slice: VNum) => iterable.itr.drop(slice).mkString
      case (slice: VNum, iterable: VList) => iterable.itr.drop(slice)
      case (slice: VNum, iterable: String) => iterable.itr.drop(slice).mkString
      case (iterable: VNum, slice: VNum) =>
        MiscHelpers.eval(iterable.itr.drop(slice).mkString)
      case (iterable: VList, slices: VList) =>
        if !slices.lst.forall(_.isInstanceOf[VNum]) then ???
        else ListHelpers.drop(iterable, slices.lst.map(_.asInstanceOf[VNum]))
    },
    addPart("ᐕ", Monad, false) {
      case a: VNum => MiscHelpers.eval(a.toString.tail)
      case a: String => a.tail
      case a: VList if a.lst.nonEmpty => a.tail
      case a: VList => VList()
    },
    addPart("½", Monad, true) {
      case a: VNum => a / 2
      case a: String =>
        val (fst, snd) = a.splitAt(a.length / 2)
        VList(fst, snd)
    },
    "ƶ" -> fullToImpl(Monad, x => NumberHelpers.range(0, x.itr.length - 1)),
    "Ƶ" -> fullToImpl(Monad, x => NumberHelpers.range(1, x.itr.length)),
    "⁰" ->
      niladify(ctx ?=>
        if ctx.globals.inputs.nonEmpty then ctx.globals.inputs(0)
        else "0"
      ),
    "¹" ->
      niladify(ctx ?=>
        if ctx.globals.inputs.length > 1 then ctx.globals.inputs(1)
        else VList.from(Seq.empty)
      ),
    addPart("²", Monad, true) {
      case a: VNum => a ** 2
      case a: String => VList.from(a.grouped(2).toSeq)
    },
    addPart("³", Monad, true) {
      case a: VNum => a ** 3
      case a: String => VList.from(a.grouped(3).toSeq)
    },
    addPart("⅟", Monad, true) {
      case a: VNum => 1 / a
      case a: String => a.filterNot(_.isWhitespace)
    },
    "※" ->
      direct(Monad) {
        pop() match
          case it: VPhysical =>
            val res = ListHelpers.groupConsecutive(it.itr)
            if it.isInstanceOf[String] then
              push(VList.from(res.map(_.asInstanceOf[VList].mkString)))
            else push(res)
          case predicate: VFun =>
            val it = pop()
            push(ListHelpers.groupByConsecutive(it.itr, predicate))
          case _ => ???
      },
    "⇄" -> fullToImpl(Monad, x => ListHelpers.reverse(x)),
    addPart("⧖", Monad, false) {
      case num: VNum => VList.from(ListHelpers.permutations(num.ritr))
      case str: String =>
        VList.from(ListHelpers.permutations(str.itr).map(_.mkString))
      case lst: VList => VList.from(ListHelpers.permutations(lst))
    },
    addPart("‰", Dyad, true) {
      case (a: VNum, b: VNum) =>
        if b == VNum(0) then VList(0, 0) else VList((a / b).floor, a % b)
    },
    addPart("≛", Dyad, true) {
      case (a: VNum, b: VNum) => (a % b) == VNum(0)
      case (a: String, b: VNum) => a + " " * b.toInt
      case (a: VNum, b: String) => b + " " * a.toInt
      case (a: String, b: String) => b.r
          .findFirstMatchIn(a)
          .map(mobj => VList(mobj.start, mobj.end))
          .getOrElse(VList())
    },
    addPart("ℭ", Dyad, false) {
      case (itr: VNum, size: VNum) =>
        ListHelpers.combinations(itr.ritr, size, withReplacement = true)
      case (itr: String, size: VNum) => VList.from(
          ListHelpers
            .combinations(itr.itr, size.toInt, withReplacement = true)
            .map(x => x.asInstanceOf[VList].mkString)
        )
      case (itr: VList, size: VNum) =>
        ListHelpers.combinations(itr, size.toInt, withReplacement = true)

      case (size: VNum, itr: String) => VList.from(
          ListHelpers
            .combinations(itr.itr, size.toInt, withReplacement = true)
            .map(x => x.asInstanceOf[VList].mkString)
        )
      case (size: VNum, itr: VList) =>
        ListHelpers.combinations(itr, size.toInt, withReplacement = true)

    },
    addPart("℈", Dyad, false) {
      case (itr: VNum, size: VNum) =>
        ListHelpers.combinations(itr.ritr, size, withReplacement = false)
      case (itr: String, size: VNum) => VList.from(
          ListHelpers
            .combinations(itr.itr, size.toInt, withReplacement = false)
            .map(x => x.asInstanceOf[VList].mkString)
        )
      case (itr: VList, size: VNum) => VList.from(
          ListHelpers.combinations(itr, size.toInt, withReplacement = false)
        )
      case (size: VNum, itr: String) => VList.from(
          ListHelpers
            .combinations(itr.itr, size.toInt, withReplacement = false)
            .map(x => x.asInstanceOf[VList].mkString)
        )
      case (size: VNum, itr: VList) => VList.from(
          ListHelpers.combinations(itr, size.toInt, withReplacement = false)
        )
    },
    addPart("⦷", Monad, true) {
      case num: VNum => num.vabs
      case str: String => str.filter(_.isLetter)
    },
    addPart("Ϣ", Dyad, false) {
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
        else throw InvalidListOverloadException("Ϣ", b, "Number")
    },
    addPart("≤", Dyad, true) {
      case (a: VVal, b: VVal) => a <= b
    },
    addPart("≥", Dyad, true) {
      case (a: VVal, b: VVal) => a >= b
    },
    addPart("≠", Dyad, true) {
      case (a: VVal, b: VVal) => a.toString != b.toString
    },
    "≡" -> fullToImpl(Dyad, (a, b) => a === b),
    addPart("•", Dyad, false) {
      case (a: VList, b: VList) => ListHelpers.dotProduct(a, b)
      case (number: VNum, base: VNum) =>
        NumberHelpers.toBijectiveBase(number, base)
      case (itr, predicate: VFun) =>
        var pos = VNum(0)
        val list = itr.itr
        while list.hasIndex(pos.toBigInt) &&
          predicate(list.index(pos)) == VNum(0)
        do pos += 1
        if list.hasIndex(pos.toBigInt) then pos else VNum(-1)
      case (predicate: VFun, itr) =>
        var pos = VNum(0)
        val list = itr.itr
        while list.hasIndex(pos.toBigInt) &&
          predicate(list.index(pos)) == VNum(0)
        do pos += 1
        if list.hasIndex(pos.toBigInt) then pos else VNum(-1)
    },
    addPart("±", Monad, true) {
      case a: VNum => a.signum
    },
    "†" ->
      fullToImpl(
        Monad,
        x =>
          VList.from(
            ListHelpers
              .groupConsecutive(x.itr)
              .map(group => VNum(group.itr.bigLength))
          ),
      ),
    "⎙" ->
      direct(Monad) {
        MiscHelpers.vyPrintln(peek())
      },
    "✒" ->
      direct(Monad) {
        MiscHelpers.vyPrint(pop())
      },
    addPart("≓", Monad, false) {
      case num: VNum =>
        val temp = num.toString
        val reversed =
          if temp.startsWith("-") then temp + temp.reverse.tail
          else temp.reverse
        VNum(temp + reversed)
      case str: String => str + str.reverse
      case lst: VList => VList.from(lst ++ lst.reverse)
    },
    addPart("Ͼ", Monad, false) {
      case lst: VList => VList.from(lst.map(item => ListHelpers.sum(item.itr)))
    },
    "⛭" -> fullToImpl(Monad, x => MiscHelpers.exec(x)),
  )

  // Subject to being added as overloads onto things in elements
  val internalUseElements: Map[String, Element] = Map(
    "#|correspond" ->
      direct(Dyad) {
        val functionG = pop().asInstanceOf[VFun]
        val functionF = pop().asInstanceOf[VFun]

        val result = Interpreter.executeFn(functionG)
        val otherResult = Interpreter.executeFn(functionF)
        push(otherResult, result)
      }
  )

  private def niladify(value: VAny): Element =
    Element(0, () => (ctx: Context) ?=> ctx.push(value))

  private def niladify(function: Context ?=> VAny): Element =
    Element(0, () => (ctx: Context) ?=> ctx.push(function))

  /** Add an element that handles all `VAny`s (it doesn't take a
    * `PartialFunction`, hence "Full")
    */
  private def fullToImpl[F](arity: ImplHelpers[?, F], impl: F): Element =
    Element(arity.arity, arity.toDirectFn(impl))

  /** Define an element that doesn't necessarily work on all inputs
    *
    * If using this method, make sure to use `case` to define the function,
    * since it needs a `PartialFunction`. If it is possible to define it using a
    * normal function literal or it covers every single case, then try
    * [[addFull]] instead.
    */
  private def addPart[P, F](
      symbol: String,
      arity: ImplHelpers[P, F],
      vectorises: Boolean,
  )(impl: P): (String, Element) =
    symbol ->
      Element(
        arity.arity,
        arity.toDirectFn(
          if vectorises then arity.vectorise(symbol)(impl)
          else arity.fill(symbol)(impl)
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
      arity: ImplHelpers[P, F],
      symbol: String,
  )(impl: P): (String, Element) =
    symbol -> Element(arity.arity, arity.toDirectFn(arity.fill(symbol)(impl)))

  private def direct[P, F](arity: ImplHelpers[P, F])(
      impl: Context ?=> Unit
  ): Element = Element(arity.arity, () => impl)

  private def direct(impl: Context ?=> Unit): Element = Element(0, () => impl)
  private def direct(arity: Int)(impl: Context ?=> Unit): Element =
    Element(arity, () => impl)

end NewElements
