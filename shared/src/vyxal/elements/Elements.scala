package vyxal.elements

import scala.language.implicitConversions

import vyxal.*
import vyxal.{Dyad, ImplHelpers, Monad, Triad}
import vyxal.conversions.{*, given}
import vyxal.parsing.Codepage
import vyxal.Context.{peek, pop, push}
import vyxal.ListHelpers.makeIterable
import vyxal.MiscHelpers.defaultEmpty
import vyxal.StringHelpers.padLeft

import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn

given (using Context): Ordering[VAny] with
  override def compare(x: VAny, y: VAny): Int = MiscHelpers.compare(x, y)

extension (a: VAny)(using Context)
  /** Convert to an iterable */
  def itr = ListHelpers.makeIterable(a)

  /** Convert to an iterable. Forcibly rangifies numbers, unlike `.itr` */
  def ritr = ListHelpers.makeIterable(a, Some(true))

extension (a: String)(using Context) def toNum: VNum = VNum(a)
extension (s: Seq[VAny]) def vlst = VList(s)

object Elements:
  case class Element(
      arity: Int,
      impl: DirectFn,
  )

  val elements: Map[String, Element] = Map(
    "Ƶ" ->
      direct(Monad) {
        val a = pop().itr
        if a.isEmpty then push(VList(Seq.empty), 0)
        else push(a.init, a.last)
      },
    "⊞" ->
      direct(Monad) {
        val iterable = ListHelpers.makeIterable(pop())
        val uniq = iterable.vDistinct
        val counts = uniq.map(item => VNum(iterable.count(_ == item)))
        push(VList(counts))
      },
    addPart("÷", Dyad, true) {
      case (a: VNum, b: VNum) => a / b
      case (VStr(a), b: VNum) => StringHelpers.intoNPieces(a, b)
      case (a: VNum, VStr(b)) => StringHelpers.intoNPieces(b, a)
      case (VStr(a), VStr(b)) => VList(a.split(b).toSeq.vs)
    },
    "×" -> fullToImpl(Dyad, MiscHelpers.multiply),
    addPart("∧", Dyad, true) {
      case (b: VVal, a: VVal) => if !a.toBool then a else b
      case (b: VFun, a: VFun) =>
        val resA = Interpreter.executeFn(a)
        if !resA.toBool then resA else Interpreter.executeFn(b)
    },
    addPart("∨", Dyad, true) {
      case (b: VVal, a: VVal) => if a.toBool then a else b
      case (b: VFun, a: VFun) =>
        val resA = Interpreter.executeFn(a)
        if resA.toBool then resA else Interpreter.executeFn(b)
    },
    addPart("¬", Monad, false) { a =>
      VNum(!a.toBool)
    },
    addPart("ʀ", Monad, true) {
      case a: VNum => NumberHelpers.range(0, a - a.signum)
      case VStr(a) => a.toLowerCase()
    },
    addPart("ʁ", Monad, true) {
      case a: VNum =>
        val endpoint = a + 1
        NumberHelpers.range(0, endpoint - endpoint.signum)
      case VStr(a) => a.toUpperCase()
    },
    addPart("ɾ", Monad, true) {
      case a: VNum => NumberHelpers.range(1, a)
      case VStr(a) if a.length() == 1 => a.head.isLetter
      case VStr(a) => VList(a.map(char => VNum(char.isLetter)))
    },
    addPart("‹", Monad, true) {
      case a: VNum => a - 1
      case VStr(a) =>
        val temp = a.length % 8
        if temp == 0 then a else ("0" * (8 - temp)) + a
    },
    addPart("›", Monad, true) {
      case a: VNum => a + 1
      case VStr(a) => a.replace(" ", "0")
    },
    addPart("!", Monad, true) {
      case a @ VNum(r, i) =>
        if r.isWhole then spire.math.fact(spire.math.abs(a.toLong))
        else NumberHelpers.gamma(spire.math.abs(a.underlying.real) + 1)
      case VStr(a) => StringHelpers.titlecase(a)
    },
    "$" ->
      direct(Dyad) {
        val b, a = pop()
        push(b, a)
      },
    "%" -> fullToImpl(Dyad, MiscHelpers.modulo),
    addPart("&", Dyad, false) {
      case (a, b) => VList(a.itr :+ b)
    },
    "'" ->
      fullToImpl(
        Monad,
        a => a.itr.map(v => v.itr.mkString(" ")).mkString("\n"),
      ),
    addPart("*", Dyad, true) {
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
        push(Seq(a, b))
      },
    addPart("<", Dyad, true) {
      case (a: VVal, b: VVal) => a < b
      case (a: VFun, b: VNum) =>
        var res = b
        while !a(res).toBool do res -= 1
        res
    },
    addPart("=", Dyad, true) {
      case (a: VNum, b: VNum) => a == b
      case (a: VNum, VStr(b)) => a.toString == b
      case (VStr(a), b: VNum) => a == b.toString
      case (VStr(a), VStr(b)) => a == b
    },
    addPart(">", Dyad, true) {
      case (a: VVal, b: VVal) => a > b
      case (a: VFun, b: VNum) =>
        var res = b
        while a(res).toBool do res += 1
        res
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
    "#?" -> niladify(ctx ?=> VList(ctx.globals.inputs.getAll)),
    addPart("@", Dyad, true) {
      case (a: VNum, b: VNum) => (a - b).vabs
      case (VStr(a), VStr(b)) => StringHelpers.levenshtein(a, b)
      case (a: VPhysical, b: VFun) =>
        FuncHelpers.reduceOverPairs(b, makeIterable(a))
      case (a: VFun, b: VPhysical) =>
        FuncHelpers.reduceOverPairs(a, makeIterable(b))
    },
    addPart("A", Monad, false) {
      case a: VNum => ListHelpers.makeIterable(a).forall(_.toBool)
      case VStr(a) if a.length == 1 => StringHelpers.isVowel(a.head)
      case VStr(a) => VList(a.map(StringHelpers.isVowel))
      case a: VList => a.forall(_.toBool)
    },
    addPart("B", Monad, true) {
      case a: VNum => NumberHelpers.toBinary(a)
      case VStr(a) => VList(
          a.map(x => NumberHelpers.toBinary(StringHelpers.chrord(x.toString)))
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
      case VStr(a) => MiscHelpers.eval(a)
    },
    addPart("F", Dyad, false) {
      case (a: VFun, b) => ListHelpers.filter(b.ritr, a)
      case (a, b: VFun) => ListHelpers.filter(a.ritr, b)
      case (VStr(a), VStr(b)) => a.indexOf(b)
      case (a: VNum, b: VNum) => a.toString.indexOf(b.toString)
      case (a: VList, b: VVal) => a.indexOf(b)
      case (a: VVal, b: VList) => b.indexOf(a)
      case (a, b) =>
        val aList = ListHelpers.makeIterable(a)
        val bList = ListHelpers.makeIterable(b)
        val (needle, haystack) =
          if ListHelpers.maxDepth(aList) <= ListHelpers.maxDepth(bList) then
            (a, bList)
          else (b, aList)
        haystack.indexOf(needle)
    },
    "G" -> fullToImpl(Monad, a => a.itr.maxOption.getOrElse(Seq.empty)),
    addPart("H", Monad, true) {
      case a: VNum => NumberHelpers.toBaseAlphabet(a, "0123456789ABCDEF")
      case VStr(a) => NumberHelpers.fromBaseAlphabet(a, "0123456789ABCDEF")
    },
    addPart("I", Dyad, false) {
      case (a, b: VFun) => VList(a.ritr.filter(x => !b(x).toBool))
      case (a, b) =>
        val temp = ListHelpers.interleave(a.itr, b.itr)
        if a.isInstanceOf[VStr] && b.isInstanceOf[VStr] then temp.mkString
        else temp
    },
    addPart("J", Dyad, false) {
      case (a: VList, b: VList) => VList(a ++ b)
      case (a, b: VList) => VList(a +: b)
      case (a: VList, b) => VList(a :+ b)
      case (a: VNum, b: VNum) => Seq(a, b)
      case (a, b) => a.toString + b.toString
    },
    addPart("K", Monad, true) {
      case a: VNum => NumberHelpers.factors(a)
      case VStr(a) => VNum(VNum.DecimalRegex.matches(a))
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
      case (VStr(a), VStr(b)) => StringHelpers.r(b).findFirstIn(a).getOrElse("")
      case (VStr(a), VList(b)) =>
        VList(b.map(StringHelpers.r(_).findFirstIn(a).getOrElse("")))
      case (VList(a), VStr(b)) => VList(
          a.map(x => StringHelpers.r(b).findFirstIn(x.toString()).getOrElse(""))
        )
    },
    addPart("N", Monad, true) {
      case a: VNum => -a
      case VStr(a) => StringHelpers.swapCase(a)
      case a: VFun => MiscHelpers.firstNonNegative(a)
    },
    addPart("O", Monad, false) {
      case a: VNum => StringHelpers.chrord(a)
      case VStr(a) => StringHelpers.chrord(a)
      case a: VList =>
        val temp = a.map(StringHelpers.chrord)
        if temp.forall(_.isInstanceOf[VStr]) then temp.mkString
        else VList(temp)
    },
    addPart("P", Monad, false) {
      case a: VList => VList(ListHelpers.prefixes(a))
      case VStr(a) => VList(
          ListHelpers.prefixes(a.itr).map(_.mkString)
        )
      case a: VNum => VList(
          ListHelpers
            .prefixes(a.vabs.itr)
            .map(n => MiscHelpers.eval(n.mkString))
        )
    },
    addPart("Q", Dyad, false) {
      case (VStr(a), b: VNum) =>
        val index = b.toInt
        if index < 0 then
          a.take(a.length + index) + a.drop(a.length + index + 1)
        else a.take(index) + a.drop(index + 1)
      case (a, b: VNum) =>
        val lst = a.itr
        val index = b.toInt
        if index < 0 then
          VList(
            lst.take(lst.length + index) ++ lst.drop(lst.length + index + 1)
          )
        else VList(lst.take(index) ++ lst.drop(index + 1))
      case (VStr(a), VStr(b)) =>
        val res = StringHelpers.r(b).findFirstMatchIn(a)
        if res.isDefined then res.get.subgroups else Seq.empty
    },
    addPart("R", Dyad, false) {
      case (a: VNum, b: VNum) => NumberHelpers.range(a, b).dropRight(1)
      case (VStr(a), VStr(b)) => StringHelpers.r(b).findFirstIn(a).isDefined
      case (VStr(a), b: VNum) => StringHelpers.r(b).findFirstIn(a).isDefined
      case (a: VNum, VStr(b)) =>
        StringHelpers.r(b).findFirstIn(a.toString).isDefined
      case (a: VFun, b) => ListHelpers.reduce(b, a)
      case (a, b: VFun) => ListHelpers.reduce(a, b)
    },
    addPart("S", Monad, false) {
      case VStr(s) => s.sorted
      case a => VList(
          a.itr.sorted(MiscHelpers.compare(_, _))
        )
    },
    addPart("T", Monad, false) {
      case a: VNum => a * 3
      case VStr(a) => a.forall(_.isLetter)
      case a: VList => ListHelpers.transpose(a)
    },
    "U" ->
      direct(Monad) {
        val top = pop()
        val itr = top.itr
        val odds = itr.zipWithIndex.collect { case (x, i) if i % 2 == 0 => x }
        val evens = itr.zipWithIndex.collect { case (x, i) if i % 2 == 1 => x }
        top match
          case VStr(_) => push(odds.mkString, evens.mkString)
          case _: VNum => push(odds.mkString.toNum, evens.mkString.toNum)
          case _ => push(VList(odds), VList(evens))

      },
    addPart("V", Monad, false) {
      case a: VList => VList(a.map(ListHelpers.reverse))
      case VStr(a) => VList(a.split(" ").map(_.reverse).toSeq.map(VStr(_)))
      case a: VNum => 1 - a
    },
    "W" ->
      direct(-1) {
        summon[Context].wrap()
      },
    "X" -> fullToImpl(Dyad, ListHelpers.cartesianProduct(_, _)),
    addPart("Y", Dyad, false) {
      case (a: VFun, b) => MiscHelpers.scanl(b.ritr, a)
      case (a, b: VFun) => MiscHelpers.scanl(a.ritr, b)
      case (a, b: VNum) => Seq.fill(b.toInt)(a)
      case (a: VNum, b) => Seq.fill(a.toInt)(b)
      case (a: (VList | VStr), b: VList) =>
        val temp = b
          .map {
            case n: VNum => n.toInt
            case l: (VStr | VList) => ListHelpers.makeIterable(l).length
            case _ =>
              // Function / Object, which doesn't have a reasonable
              // way to convert to a number
              throw InvalidListOverloadException("Y", b, "Number")
          }
          .lazyZip(ListHelpers.makeIterable(a))
          .map((n, item) => Seq.fill(n)(item))
        if a.isInstanceOf[VStr] then temp.map(_.mkString).mkString
        else temp
    },
    addPart("Z", Dyad, false) {
      case (a, b: VFun) =>
        val iter = ListHelpers.makeIterable(a)
        VList(iter.vzip(ListHelpers.map(b, iter)))
      case (a: VFun, b) =>
        val iter = ListHelpers.makeIterable(b)
        VList(ListHelpers.map(a, iter).vzip(iter))
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
    "`" -> niladify(ctx ?=> ctx.getStack.bigLength),
    addPart("a", Monad, false) {
      case a: VNum => a.itr.exists(_ == VNum(0))
      case VStr(a) if a.length == 1 => a.head.isUpper
      case VStr(a) => VList(a.map(c => VNum(c.isUpper)))
      case a: VList => a.itr.exists(_.toBool)
    },
    "b" -> fullToImpl(Monad, NumberHelpers.fromBinary),
    addPart("c", Dyad, false) {
      case (a: VVal, b: VVal) => a.toString().contains(b.toString())
      case (a: VList, b: VVal) => a.contains(b)
      case (a: VVal, b: VList) => b.contains(a)
      case (a: VList, b: VList) =>
        val (needle, haystack) =
          if ListHelpers.maxDepth(a.lst) <= ListHelpers.maxDepth(b.lst) then
            (a, b)
          else (b, a)
        haystack.contains(needle)
    },
    addPart("d", Monad, true) {
      case a: VNum => a + a
      case VStr(a) => s"$a$a"
    },
    addPart("e", Monad, true) {
      case a: VNum => a % 2 == VNum(0)
      case VStr(a) => a.split("\n").toIndexedSeq
    },
    "f" -> fullToImpl(Monad, x => ListHelpers.flatten(x.itr)),
    "g" -> fullToImpl(Monad, a => a.itr.minOption.getOrElse(Seq.empty)),
    "h" -> fullToImpl(Monad, x => x.itr.headOption.getOrElse(defaultEmpty(x))),
    "i" -> fullToImpl(Dyad, MiscHelpers.index),
    addPart("j", Dyad, false) {
      case (a: VList, b) => ListHelpers.join(a, b)
      case (a, b: VList) => ListHelpers.join(b, a)
      case (a: VNum, b: VNum) =>
        VNum.complex(a.underlying.real, b.underlying.real)
      case (a, b) => ListHelpers.join(a.itr, b) match
          case l: VList => l.mkString
          case res => res
    },
    addPart("l", Dyad, true) {
      case (a: VNum, b: VNum) => NumberHelpers.log(a, b)
      case (VStr(a), b: VNum) => a.length == b.toInt
      case (VStr(a), VStr(b)) => a.length == b.length
      case (a: VNum, VStr(b)) => b.length == a.toInt
      case (a: VPhysical, b: VFun) => MiscHelpers.untilNoChange(b, a)
      case (a: VFun, b) => MiscHelpers.untilNoChange(a, b)
    },
    "m" -> niladify(ctx ?=> ctx.ctxVarSecondary),
    "n" -> niladify(ctx ?=> ctx.ctxVarPrimary),
    addPart("o", Dyad, false) {
      case (VStr(a), b: VNum) => ListHelpers.overlaps(a, b.toInt)
      case (a: VPhysical, b: VNum) => ListHelpers.overlaps(a.itr, b.toInt)
      case (a: VNum, VStr(b)) => ListHelpers.overlaps(b, a.toInt)
      case (a: VNum, b: VPhysical) => ListHelpers.overlaps(b.itr, a.toInt)
      case (a: VList, VList(b)) =>
        if !b.forall(_.isInstanceOf[VNum]) then ???
        else ListHelpers.overlapsMd(a, b.map(_.asInstanceOf[VNum]))
      case (a: VIter, b: VFun) => ListHelpers
          .overlaps(a.ritr, b.arity)
          .map(overlap => ListHelpers.reduce(overlap, b))
      case (a: VFun, b: VIter) => ListHelpers
          .overlaps(b.ritr, a.arity)
          .map(overlap => ListHelpers.reduce(overlap, a))
      case (a: VFun, b: VNum) =>
        val lst = pop()
        ListHelpers
          .overlaps(lst.itr, b.toInt)
          .map(overlap => ListHelpers.reduce(overlap, a))
      case (a: VNum, b: VFun) =>
        val lst = pop()
        ListHelpers
          .overlaps(lst.itr, a.toInt)
          .map(overlap => ListHelpers.reduce(overlap, b))
    },
    addPart("p", Dyad, false) {
      case (VStr(a), b: (VStr | VNum)) => b.toString + a
      case (a: VNum, VStr(b)) => b + a.toString
      case (a: VNum, b: VNum) => MiscHelpers.eval(b.toString + a.toString)
      case (a: VList, b) => VList(b +: a)
      case (a, b) => Seq(b, a)
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
      case (VList(a), b, c) => VList(a.map(x => if x == b then c else x))
      case (a, VList(b), c: VList) => VList(b.map(x => if x == a then c else x))
      case (a, b, VList(c)) => VList(c.map(x => if x == a then b else x))
      case (a, VList(b), c) => VList(b.map(x => if x == a then c else x))
      case (VStr(a), b: VVal, c: VVal) => a.replace(b.toString, c.toString)
      case (a: VNum, b: VVal, c: VVal) =>
        MiscHelpers.eval(a.toString().replace(b.toString, c.toString))
    },
    addPart("s", Dyad, false) {
      case (VStr(a), b) =>
        if b.isInstanceOf[VStr] && b.toString.isEmpty then a.itr
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
          case lst: VList => push(lst.vDistinct)
          case n: VNum => push(
              MiscHelpers.eval(ListHelpers.makeIterable(n).vDistinct.mkString)
            )
          case VStr(s) => push(s.distinct.mkString)
          case predicate: VFun =>
            val lst = pop()
            val unique = ListHelpers.dedupBy(lst.itr, predicate)
            lst match
              case VStr(_) => push(unique.mkString)
              case _ => push(unique)
          case _ => throw UnsupportedOverloadException("u", "object")

      },
    addPart("v", Monad, false) {
      case VStr(a) => ListHelpers.overlaps(a, 2)
      case fn: VFun =>
        val lst = pop()
        FuncHelpers.reduceOverPairs(fn, lst.itr)
      case a => ListHelpers.overlaps(a.itr, 2)
    },
    "w" ->
      direct(Monad) {
        push(Seq(pop())) // Tacit!
      },
    "x" ->
      direct(1) {
        FuncHelpers.recursion()
      },
    addPart("y", Triad, false) {
      case (
            VStr(a),
            b: (VList | VNum | VStr),
            c: (VList | VNum | VStr),
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
      case VStr(a) => a.length() == 1
    },
    addPart("⨪", Monad, true) {
      case a: VNum => a - 2
      case VStr(s) => s
          .split("\n")
          .map { line =>
            val reversedFlipped = StringHelpers.invertBrackets(s).reverse
            s"$s${reversedFlipped.replace("/", "\\")}"
          }
          .mkString("\n")
    },
    addPart("∑", Monad, false) {
      case a: VVal => ListHelpers.sum(a.itr)
      case VList(a) if !a.exists(_.isInstanceOf[VStr]) => ListHelpers.sum(a)
      case default => MiscHelpers.eval(default.itr.mkString)
    },
    addPart("Π", Monad, false) {
      case VList(itr) => ListHelpers.product(itr)
      case num: VNum => NumberHelpers.toBinary(num).mkString
      case predicate: VFun =>
        // Amusingly, copilot originally tried to put a `getOrElse` here
        // despite the fact that the function will never terminate if
        // there is no such integer that fulfills the predicate.
        NumberHelpers.allIntegers.find(predicate(_).toBool).get
    },
    addPart("σ", Monad, false) {
      case a =>
        val list = ListHelpers.makeIterable(a)
        if list.isEmpty then Seq.empty
        else if list.tail.isEmpty then Seq(list.head)
        else
          VList(
            list.tail.scanLeft(
              list.head
            )((x, y) => MiscHelpers.add(x, y))
          )
    },
    "⇧" -> fullToImpl(Monad, lhs => ListHelpers.gradeUp(lhs.itr)),
    "⇩" -> fullToImpl(Monad, lhs => ListHelpers.gradeDown(lhs.itr)),
    addPart("∪", Dyad, false) {
      case (VStr(a), VStr(b)) => a + b.filterNot(a.contains(_))
      case (a, b) => VList(a.itr ++ b.itr.filterNot(a.itr.contains(_)))
    },
    addPart("∩", Dyad, false) {
      case (VStr(lhs), VStr(rhs)) => lhs.filter(rhs.contains(_))
      case (lhs, rhs) => VList(lhs.itr.filter(rhs.itr.contains(_)))
    },
    addPart("⊍", Dyad, false) {
      case (VStr(lhs), VStr(rhs)) => (lhs.itr ^ rhs.itr).mkString
      case (lhs, rhs) => VList(lhs.itr ^ rhs.itr)
    },
    addPart("⦰", Dyad, false) {
      case (VStr(a), VStr(b)) => a.filterNot(b.contains(_))
      case (a: VList, b: (VNum | VStr)) => a.filter(_ != b)
      case (a: (VNum | VStr), b: VList) => b.filter(_ != a)
      case (a, b) =>
        val left = ListHelpers.makeIterable(a)
        val right = ListHelpers.makeIterable(b)
        VList(left.filterNot(right.contains(_)))
    },
    addPart("«", Dyad, true) {
      case (a: VNum, b: VNum) => a.toBigInt << b.toInt
      case (a: VNum, VStr(b)) => StringHelpers.padLeft(b, a)
      case (VStr(a), b: VNum) => StringHelpers.padLeft(a, b)
      case (VStr(a), VStr(b)) => StringHelpers.padLeft(a, b.length)
    },
    addPart("»", Dyad, true) {
      case (a: VNum, b: VNum) => a.toBigInt >> b.toInt
      case (a: VNum, VStr(b)) => StringHelpers.padRight(b, a)
      case (VStr(a), b: VNum) => StringHelpers.padRight(a, b)
      case (VStr(a), VStr(b)) => StringHelpers.padRight(a, b.length)
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
      case (a: VList, b: VVal) => VList(a.map(MiscHelpers.dyadicMaximum(_, b)))
      case (a: VVal, b: VList) => VList(b.map(MiscHelpers.dyadicMaximum(a, _)))
      case (a: VList, b: VList) =>
        VList(a.zip(b).map((x, y) => MiscHelpers.dyadicMaximum(x, y)))
      case (a: VVal, b: VVal) => MiscHelpers.dyadicMaximum(a, b)
      case (initial: VList, function: VFun) =>
        ListHelpers.generate(function, initial)
      case (function: VFun, initial: VList) =>
        ListHelpers.generate(function, initial)
      case (initial, function: VFun) =>
        ListHelpers.generate(function, Seq(initial))
      case (function: VFun, initial) =>
        ListHelpers.generate(function, Seq(initial))
    },
    addPart("ġ", Dyad, false) {
      case (a: VList, b: VVal) => VList(a.map(MiscHelpers.dyadicMinimum(_, b)))
      case (a: VVal, b: VList) => VList(b.map(MiscHelpers.dyadicMinimum(a, _)))
      case (a: VList, b: VList) =>
        val zipped = a.vzip(b)
        VList(zipped.map(pair =>
          val items = pair.asInstanceOf[VList]
          if items.length == 1 then items.head
          else MiscHelpers.dyadicMinimum(items.head, items(1))
        ))
      case (a: VVal, b: VVal) => MiscHelpers.dyadicMinimum(a, b)
      case (initial, function: VFun) =>
        ListHelpers.generateDyadic(function, initial.itr)
      case (function: VFun, initial) =>
        ListHelpers.generateDyadic(function, initial.itr)
    },
    addPart("⌈", Monad, true) {
      case a: VNum => a.ceil
      case VStr(a) => a.split(" ").toIndexedSeq
    },
    addPart("⌊", Monad, true) {
      case a: VNum => a.floor
      case VStr(a) =>
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
      case (a: VNum, b: (VList | VStr)) => ListHelpers.take(b.itr, a)
      case (iterable, predicate: VFun) =>
        iterable.itr.takeWhile(predicate(_).toBool)
      case (predicate: VFun, iterable) =>
        iterable.itr.takeWhile(predicate(_).toBool)
      case (a: VList, VList(b)) =>
        if !b.forall(_.isInstanceOf[VNum]) then ???
        else ListHelpers.take(a, b.map(_.asInstanceOf[VNum]))
    },
    addPart("⌽", Dyad, false) {
      case (a, b: VNum) =>
        val temp = ListHelpers.makeIterable(a).slice(1, b.toInt)
        a match
          case VStr(_) => temp.mkString
          case _ => temp
      case (a: VNum, b) =>
        val temp = ListHelpers.makeIterable(b).slice(1, a.toInt)
        b match
          case VStr(_) => temp.mkString
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
    "↺" ->
      direct(Dyad) {
        val top = pop()
        top match
          case a: VIter => push(ListHelpers.rotate(a, 1))
          case predicate: VFun =>
            val item = pop()
            MiscHelpers.collectUnique(predicate, item).last
          case a: VNum =>
            val times = a
            val iterable = pop()
            push(ListHelpers.rotate(iterable, times))
          case _ => throw UnsupportedOverloadException("↺", "function | object")
      },
    "↻" ->
      direct(Dyad) {
        val top = pop()
        top match
          case a: VIter => push(ListHelpers.rotate(a, -1))
          case a: VNum =>
            val times = a
            val iterable = pop()
            push(ListHelpers.rotate(iterable, -times))
          case _ => throw UnsupportedOverloadException("↻", "function | object")
      },
    addPart("≜", Triad, false) {
      case (a: VObject, VStr(b), c) => MiscHelpers.setObjectMember(a, b, c)
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
        if a.isInstanceOf[VStr] then temp.mkString
        else temp
      case (a, b: VVal, c: VNum) =>
        val temp = ListHelpers.assign(ListHelpers.makeIterable(a), c, b)
        if a.isInstanceOf[VStr] then temp.mkString
        else temp
      case (a, b: VNum, c: VFun) =>
        val temp = ListHelpers.augmentAssign(ListHelpers.makeIterable(a), b, c)
        if a.isInstanceOf[VStr] then temp.mkString
        else temp
      case (a, b: VList, c: VList) =>
        var temp = ListHelpers.makeIterable(a)
        for (i, j) <-
            ListHelpers.makeIterable(b).zip(ListHelpers.makeIterable(c))
        do
          i match
            case ind: VNum => j match
                case value: VPhysical => temp = ListHelpers.assign(temp, ind, j)
                case obj: VObject => temp = ListHelpers.assign(temp, ind, obj)
                case function: VFun =>
                  temp = ListHelpers.augmentAssign(temp, ind, function)
                case _ => throw UnsupportedOverloadException("≜", "Constructor")
            case _ => throw InvalidListOverloadException("≜", b, "Number")
        if a.isInstanceOf[VStr] then temp.mkString
        else temp
      case (a, b: VList, c) =>
        val temp =
          ListHelpers.makeIterable(b).foldLeft(ListHelpers.makeIterable(a)) {
            case (temp, ind: VNum) => ListHelpers.assign(temp, ind, c)
            case _ => throw InvalidListOverloadException("Ạ", b, "Number")
          }
        if a.isInstanceOf[VStr] then temp.mkString
        else temp
      case (VStr(a), VStr(b), VStr(c)) => StringHelpers.regexSub(a, b, c)
      case (VStr(a), VStr(b), c: VFun) => StringHelpers.regexSub(a, b, c)
      case (VStr(a), b: VFun, VStr(c)) => StringHelpers.regexSub(a, c, b)
      case (a: VFun, VStr(b), VStr(c)) => StringHelpers.regexSub(b, c, a)
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
          ListHelpers.mergeInfLists(
            ListHelpers.prefixes(x.itr).map(b => ListHelpers.suffixes(b.itr))
          ),
      ),
    addPart("⊢", Dyad, false) {
      case (number: VNum, base: VNum) => NumberHelpers.toBase(number, base)
      case (number: VNum, baseAlphabet: VIter) =>
        NumberHelpers.toBase(number, baseAlphabet)
      case (VList(list), base: VNum) =>
        VList(list.map(NumberHelpers.toBase(_, base)))
      case (VList(list), VStr(baseAlphabet)) =>
        VList(list.map(NumberHelpers.toBase(_, baseAlphabet)))
      case (values: VList, bases: VList) => VList(
          values
            .vzip(bases)
            .map(item =>
              val Seq(value, base) = item.itr
              NumberHelpers.toBase(value, base)
            )
        )
      case (VStr(haystack), VStr(needle)) =>
        needle.r.findAllIn(haystack).toList.vs
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
      case VStr(a) => a.init
      case VList(a) => if a.nonEmpty then a.init else Seq.empty
    },
    addPart("ᐵ", Dyad, false) {
      case (iterable: VList, slice: VNum) => iterable.itr.drop(slice)
      case (VStr(iterable), slice: VNum) => iterable.itr.drop(slice).mkString
      case (slice: VNum, iterable: VList) => iterable.itr.drop(slice)
      case (slice: VNum, VStr(iterable)) => iterable.itr.drop(slice).mkString
      case (iterable: VNum, slice: VNum) =>
        MiscHelpers.eval(iterable.itr.drop(slice).mkString)
      case (a: VFun, b) => MiscHelpers.collectUnique(a, b).tail
      case (a, b: VFun) => MiscHelpers.collectUnique(b, a).tail
      case (iterable: VList, VList(slices)) =>
        if !slices.forall(_.isInstanceOf[VNum]) then ???
        else ListHelpers.drop(iterable, slices.map(_.asInstanceOf[VNum]))
    },
    addPart("ᐕ", Monad, false) {
      case a: VNum => MiscHelpers.eval(a.toString.tail)
      case VStr(a) => a.tail
      case VList(a) => if a.nonEmpty then a.tail else Seq.empty
    },
    addPart("½", Monad, true) {
      case a: VNum => a / 2
      case VStr(a) =>
        val (fst, snd) = a.splitAt(a.length / 2)
        Seq(fst, snd)
    },
    "ƶ" -> fullToImpl(Monad, x => NumberHelpers.range(0, x.itr.length - 1)),
    "ÞƵ" -> fullToImpl(Monad, x => NumberHelpers.range(1, x.itr.length)),
    "⁰" ->
      niladify(ctx ?=>
        if ctx.globals.inputs.nonEmpty then ctx.globals.inputs(0)
        else "0"
      ),
    "¹" ->
      niladify(ctx ?=>
        if ctx.globals.inputs.length > 1 then ctx.globals.inputs(1)
        else VList(Seq.empty)
      ),
    addPart("²", Monad, true) {
      case a: VNum => a ** 2
      case VStr(a) => a.grouped(2).toSeq
    },
    addPart("³", Monad, true) {
      case a: VNum => a ** 3
      case VStr(a) => a.grouped(3).toSeq
    },
    addPart("⅟", Monad, true) {
      case a: VNum => 1 / a
      case VStr(a) => a.filterNot(_.isWhitespace)
    },
    "※" ->
      direct(Monad) {
        pop() match
          case it: VPhysical =>
            val res = ListHelpers.groupConsecutive(it.itr)
            if it.isInstanceOf[VStr] then
              push(VList(res.map(_.asInstanceOf[VList].mkString)))
            else push(res)
          case predicate: VFun =>
            val it = pop()
            push(ListHelpers.groupByConsecutive(it.itr, predicate))
          case _ => ???
      },
    "⇄" -> fullToImpl(Monad, x => ListHelpers.reverse(x)),
    addPart("⧖", Monad, false) {
      case num: VNum => VList(ListHelpers.permutations(num.ritr))
      case VStr(str) => VList(ListHelpers.permutations(str.itr).map(_.mkString))
      case lst: VList => VList(ListHelpers.permutations(lst))
      case fn: VFun => pop() match
          case VList(itr) => ListHelpers.map(fn, ListHelpers.permutations(itr))
          case VStr(s) =>
            ListHelpers.map(fn, ListHelpers.permutations(s.itr).map(_.mkString))
          case n: VNum => ListHelpers.map(fn, ListHelpers.permutations(n.ritr))
          case x => throw new UnimplementedOverloadException("⧖", Seq(x))
    },
    addPart("‰", Dyad, true) {
      case (a: VNum, b: VNum) =>
        if b == VNum(0) then Seq(0, 0) else Seq((a / b).floor, a % b)
      case (fn: VFun, iterable) => ListHelpers.flatten(iterable.itr.map(fn(_)))
      case (iterable, fn: VFun) => ListHelpers.flatten(iterable.itr.map(fn(_)))
    },
    addPart("≛", Dyad, true) {
      case (a: VNum, b: VNum) => (a % b) == VNum(0)
      case (VStr(a), b: VNum) => a + " " * b.toInt
      case (a: VNum, VStr(b)) => b + " " * a.toInt
      case (VStr(a), VStr(b)) => b.r
          .findFirstMatchIn(a)
          .map(mobj => Seq(mobj.start, mobj.end))
          .getOrElse(Seq.empty)
    },
    addPart("ℭ", Dyad, false) {
      case (itr: VNum, size: VNum) =>
        ListHelpers.combinations(itr.ritr, size, withReplacement = true)
      case (VStr(itr), size: VNum) => VList(
          ListHelpers
            .combinations(itr.itr, size.toInt, withReplacement = true)
            .map(_.mkString)
        )
      case (itr: VList, size: VNum) =>
        ListHelpers.combinations(itr, size.toInt, withReplacement = true)

      case (size: VNum, VStr(itr)) => VList(
          ListHelpers
            .combinations(itr.itr, size.toInt, withReplacement = true)
            .map(_.mkString)
        )
      case (size: VNum, itr: VList) =>
        ListHelpers.combinations(itr, size.toInt, withReplacement = true)

    },
    addPart("℈", Dyad, false) {
      case (itr: VNum, size: VNum) =>
        ListHelpers.combinations(itr.ritr, size, withReplacement = false)
      case (VStr(itr), size: VNum) => VList(
          ListHelpers
            .combinations(itr.itr, size.toInt, withReplacement = false)
            .map(_.mkString)
        )
      case (itr: VList, size: VNum) =>
        ListHelpers.combinations(itr, size.toInt, withReplacement = false).vs
      case (size: VNum, VStr(itr)) => VList(
          ListHelpers
            .combinations(itr.itr, size.toInt, withReplacement = false)
            .map(_.mkString)
        )
      case (size: VNum, itr: VList) =>
        ListHelpers.combinations(itr, size.toInt, withReplacement = false).vs
    },
    addPart("⦷", Monad, true) {
      case num: VNum => num.vabs
      case VStr(str) => str.filter(_.isLetter)
      case predicate: VFun =>
        var res = 1
        while !predicate(VNum(res)).toBool do res += 1
        res
    },
    addPart("Ϣ", Dyad, false) {
      case (a: VList, b: VNum) => ListHelpers.wrapLength(a, b)
      case (VStr(a), b: VNum) =>
        if b <= 0 then Seq.empty
        else a.grouped(b.toInt).toSeq
      case (a: VNum, VStr(b)) =>
        if a <= 0 then Seq.empty
        else b.grouped(a.toInt).toSeq
      case (a: VNum, b: VList) => ListHelpers.wrapLength(b, a)
      case (a: VList, b: VList) =>
        if b.forall(_.isInstanceOf[VNum]) then
          ListHelpers.partitionBy(a, b.map(_.asInstanceOf[VNum]))
        else throw InvalidListOverloadException("Ϣ", b, "Number")
      case (a: VFun, b: VNum) => MiscHelpers.predicateSlice(a, b, 0)
      case (a: VNum, b: VFun) => MiscHelpers.predicateSlice(b, a, 0)
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
      case VStr(s) =>
        if s.length() == 1 then StringHelpers.caseOf(s)
        else s.map(c => StringHelpers.caseOf(c.toString))
    },
    "†" ->
      fullToImpl(
        Monad,
        x =>
          VList(
            ListHelpers
              .groupConsecutive(x.itr)
              .map(group => VNum(group.itr.bigLength))
          ),
      ),
    "⎙" ->
      direct(Monad) {
        MiscHelpers.vyPrintln(peek())
      },
    "#," ->
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
      case VStr(str) => str + str.reverse
      case lst: VList => VList(lst ++ lst.reverse)
    },
    addPart("Ͼ", Monad, false) {
      case lst: VList => VList(lst.map(item => ListHelpers.sum(item.itr)))
    },
    "ᴥ" -> fullToImpl(Monad, x => MiscHelpers.exec(x)),
    addPart("ℳ", Dyad, false) {
      case (a: (VList | VStr), b: VNum) => ListHelpers.nthItems(a, b)
      case (a: VNum, b: (VList | VStr)) => ListHelpers.nthItems(b, a)
      case (a: VList, b: VList) => ListHelpers.matrixMultiply(a, b)
      case (VStr(a), VStr(b)) => StringHelpers.r(b).matches(a)
    },
    addPart("℗", Monad, true) {
      case a: VNum => NumberHelpers.isMostLikelyPrime(a)
      case VStr(a) => StringHelpers.quotify(a) + a
    },
    "⤻" ->
      direct(Monad) {
        val top = pop()
        val under = pop()
        push(under, top, under)
      },
    "⤺" ->
      direct(Monad) {
        val top = pop()
        val under = pop()
        push(top, under, top)
      },
    addPart("⍢", Monad, true) {
      case a: VNum => a % 2
      case VStr(a) => a.slice(a.length / 2, a.length)
    },
    addPart("ℂ", Dyad, true) {
      case (a: VNum, b: VNum) => NumberHelpers.nChooseK(a, b)
      case (VStr(a), VStr(b)) => a.toSet == b.toSet
      case (a: VFun, b) => MiscHelpers.untilNoChange(a, b).last
      case (a, b: VFun) => MiscHelpers.untilNoChange(b, a).last
    },
    addPart("⌹", Monad, false) {
      case a: VList => ListHelpers.partitions(a)
      case VStr(s) => ListHelpers
          .partitions(ListHelpers.makeIterable(s))
          .map(_.map(_.mkString))
      case n: VNum => NumberHelpers.partitions(n)
    },
    addPart("⏚", Monad, false) {
      case a: VNum => ListHelpers.powerset(a.ritr)
      case VStr(a) =>
        ListHelpers.powerset(a.itr).map(_.asInstanceOf[VList].mkString)
      case a: VList => ListHelpers.powerset(a)
      case a: VFun => FuncHelpers.deepVectorise(a)
    },
    addPart("↯", Dyad, true) {
      case (lst: VAny, predicate: VFun) =>
        ListHelpers.sortBy(lst.ritr, predicate)
      case (predicate: VFun, lst: VAny) =>
        ListHelpers.sortBy(lst.ritr, predicate)
      case (start: VNum, end: VNum) => NumberHelpers.range(start, end)
      case (VStr(haystack), VStr(pattern)) =>
        StringHelpers.splitKeepDelimiters(haystack, pattern)
    },
    addPart("⊠", Dyad, false) {
      case (a, n: VNum) => ListHelpers.cartesianPower(a, n)
      case (n: VNum, a) => ListHelpers.cartesianPower(a, n)
      case (VStr(a), VStr(b)) =>
        val res = StringHelpers.r(b).findFirstMatchIn(a)
        if res.isDefined then res.get.start else -1
      case (VList(a), VStr(b)) => VList(
          a.map { x =>
            val res = StringHelpers.r(b).findFirstMatchIn(x.toString)
            if res.isDefined then res.get.start else -1
          }
        )
      case (a, b: VList) =>
        summon[Context].push(a)
        ListHelpers.cartesianProduct(b, b)
    },
    addPart("⚅", Monad, false) {
      case a: VNum => NumberHelpers.randrange(a)
      case lst: VIter => lst.itr.index(NumberHelpers.randrange(lst.itr.length))
    },
    "æ" ->
      direct(Monad) {
        pop() match
          case function: VFun =>
            push(Interpreter.executeFn(function, popArgs = false))
            if function.arity == -1 then
              pop() // Handle the extra value pushed by lambdas that operate on the stack
          case value =>
            push(value)
            push(ListHelpers.reverse(value))
      },
    "␣" -> niladify(" "),
    "¶" -> niladify("\n"),
    "★" -> niladify("*"),
    "ᑂ" ->
      direct(Monad) {
        val ctx = summon[Context]
        pop() match
          case lst: VList => push(
              lst.drop(1),
              lst.headOption.getOrElse(ctx.settings.defaultValue),
            )
          case VStr(s) =>
            push(s.drop(1), if s.isEmpty then "" else s.charAt(0).toString)
          case n: VNum =>
            val iter = n.ritr
            push(
              iter.drop(1)
            )
          case arg => throw UnimplementedOverloadException("ᑂ", List(arg))
      },
    addPart("∻", Dyad, true) {
      case (a: VNum, b: VNum) => (a / b).floor
    },
    addPart("√", Monad, true) {
      case a: VNum => a.sqrt
    },
    addPart("⍰", Monad, true) {
      case a: VNum => a != VNum(0)
      case VStr(a) => a.nonEmpty
    },
    addPart("◌", Monad, true) {
      case a: VNum => NumberHelpers.round(a)
      case VStr(s) if s.length() == 1 => VNum(s.head.isLower)
      case VStr(s) => s.map(c => VNum(c.isLower))
    },
    "δ" -> fullToImpl(Monad, x => ListHelpers.deltas(x.itr)),
    addPart("☷", Dyad, false) {
      case (iterable, predicate: VFun) =>
        ListHelpers.groupBy(iterable.itr, predicate)
      case (predicate: VFun, iterable) =>
        ListHelpers.groupBy(iterable.itr, predicate)
      case (a, b) => ListHelpers.partitionAfterTruthyIndices(a, b)
    },
    addPart("Þ⎶", Monad, false) {
      case a: VNum => Seq(a.real, a.imag)
      case a =>
        val iterable = a.itr
        if iterable.isEmpty then VList(Seq.empty)
        else if iterable.length == 1 then Seq(iterable.head)
        else Seq(iterable.head, iterable.last)
    },
    addPart("⎶", Dyad, false) {
      case (VStr(a), VStr(b)) => a.stripPrefix(b).stripSuffix(b)
      case (VStr(a), b: VNum) =>
        a.stripPrefix(b.toString).stripSuffix(b.toString)
      case (a: VNum, VStr(b)) => VNum(a.toString.stripPrefix(b).stripSuffix(b))
      case (a: VNum, b: VNum) =>
        VNum(a.toString.stripPrefix(b.toString).stripSuffix(b.toString))
      case (a: VFun, b) => MiscHelpers.scanl(ListHelpers.makeIterable(b), a)
      case (a, b: VFun) => MiscHelpers.scanl(ListHelpers.makeIterable(a), b)
      case (a: VList, b: VList) => ListHelpers.trimList(a, b)
      case (a: VList, b) => ListHelpers.trim(a, b)
      case (a, b: VList) => ListHelpers.trim(b, a)
      case (a, b) => ListHelpers.trim(ListHelpers.makeIterable(a), b)
    },
    addPart("⊆", Dyad, false) {
      case (VList(haystack), VList(needle)) =>
        val hDepth = ListHelpers.maxDepth(haystack)
        val nShape = ListHelpers.shapeOf(needle)
        val nDepth = nShape.length
        val (haystackList, needleList) =
          if hDepth >= nDepth then (haystack, needle)
          else (needle, haystack)
        if haystackList.isEmpty || needleList.isEmpty then Seq.empty
        else
          ListHelpers
            .sublistExists((haystackList, hDepth), (needleList, nDepth, nShape))
      case (VStr(haystack), VStr(needle)) => haystack.contains(needle)
      case (haystack: VList, needle: VVal) =>
        def contains(needle: VVal, haystack: VList): Boolean =
          haystack.lst.exists {
            case lst: VList => contains(needle, lst)
            case value => value == needle
          }
        contains(needle, haystack)
      case (needle: VVal, haystack: VList) =>
        def contains(needle: VVal, haystack: VList): Boolean =
          haystack.lst.exists {
            case lst: VList => contains(needle, lst)
            case value => value == needle
          }
        contains(needle, haystack)

    },
    "⍨" -> direct(Monad) { pop().itr.foreach(push(_)) },
    addPart("γ", Monad, false) {
      case VStr(str) => str.grouped(2).toSeq
      case VList(lst) => ListHelpers.wrapLength(lst, 2)
    },
    "⎘" ->
      direct(Monad) {
        pop() match
          case layerCount: VNum =>
            val iterable = pop().itr
            push(ListHelpers.flattenByDepth(iterable, layerCount))
          case VList(lst) => push(ListHelpers.flattenByDepth(lst, 1))
          case _ => throw UnsupportedOverloadException("⎘", "String | Function")
      },
    addPart("ꜝ", Monad, false) {
      case a: VNum => VNum(a.itr.filter(x => x != VNum(0)).mkString)
      case VStr(a) => a // TODO: Better overload
      case VList(a) => a.itr.filter(elem => elem.toBool)
    },
    addPart("≈", Monad, false) {
      case iter: VPhysical =>
        val lst = iter.itr
        lst.isEmpty || lst.forall(_ === lst(0))
    },
    addPart("≊", Dyad, false) {
      case (iter: VPhysical, item) =>
        val lst = iter.itr
        lst.nonEmpty && lst.forall(_ == item)
      case (item, iter: VPhysical) =>
        val lst = iter.itr
        lst.nonEmpty && lst.forall(_ == item)
    },
    "κ" ->
      direct(Dyad) {
        pop() match
          case VList(lst) => push(NumberHelpers.gcd(lst))
          case rhs: VNum => pop() match
              case lhs: VNum => push(NumberHelpers.gcd(lhs, rhs))
              case VList(lst) => push(NumberHelpers.gcd(lst :+ rhs))
              case _ =>
                throw UnsupportedOverloadException("κ", "String | Function")
          case _ => throw UnsupportedOverloadException("κ", "String | Function")
      },
    "#↸" ->
      direct(Monad) {
        val index = pop()
        index match
          case simpleOuter: VNum =>
            val ctx = summon[Context]
            val parentCtx = ctx.getParentCtx.getOrElse(ctx)
            val value = parentCtx.getStack.vlst.indexBig(simpleOuter.toBigInt)
            push(value)
          case VList(coordinates) =>
            if coordinates.length != 2 then
              throw InvalidListOverloadException("#↸", coordinates, "2")
            if !coordinates.forall(_.isInstanceOf[VNum]) then
              throw InvalidListOverloadException("#↸", coordinates, "numeric")
            val ctx = summon[Context]
            var parentCtx = ctx
            for _ <-
                NumberHelpers.range(0, coordinates.head.asInstanceOf[VNum] - 1)
            do parentCtx = parentCtx.getParentCtx.getOrElse(parentCtx)
            val value = parentCtx.getStack.vlst
              .indexBig(coordinates(1).asInstanceOf[VNum].toBigInt)
            push(value)
          case _ => throw UnsupportedOverloadException("#↸", "Non-number")
        end match
      },
    "”" ->
      direct(Monad) {
        pop() match
          case VList(lst) => push(ListHelpers.join(lst, "\n"))
          case VStr(str) => push(str)
          case num: VNum =>
            if num == VNum(1) then push(summon[Context].ctxVarPrimary)
          case _ => throw UnsupportedOverloadException("”", "Function | Object")
      },
    addPart("„", Monad, false) {
      case VList(lst) => ListHelpers.join(lst, " ")
      case num: VNum => num < 0
    },
    "“" -> fullToImpl(Monad, x => MiscHelpers.joinNothing(x)),
    "↸" ->
      direct(Triad) {
        val top = pop()
        val under = pop()
        val kicker = pop()
        push(top, kicker, under)
      },
    addPart("⧢", Dyad, false) {
      case (VList(lst), numberOfChunks: VNum) =>
        ListHelpers.intoNPieces(lst, numberOfChunks)
      case (VStr(str), numberOfChunks: VNum) =>
        ListHelpers.intoNPieces(str.itr, numberOfChunks)
      case (numberOfChunks: VNum, VList(lst)) =>
        ListHelpers.intoNPieces(lst, numberOfChunks)
      case (numberOfChunks: VNum, VStr(str)) =>
        ListHelpers.intoNPieces(str.itr, numberOfChunks)
      case (iterable: VNum, numberOfChunks: VNum) =>
        ListHelpers.intoNPieces(iterable.itr, numberOfChunks)
      case (predicate: VFun, initial) =>
        MiscHelpers.untilNoChange(predicate, initial).tail
      case (initial, predicate: VFun) =>
        MiscHelpers.untilNoChange(predicate, initial).tail
    },
    "▲" ->
      fullToImpl(
        Dyad,
        (iterable, mask) =>
          iterable.itr.zip(mask.itr).filter(_._2.toBool).map(_._1),
      ),
    "Ṭ" -> fullToImpl(Monad, x => ListHelpers.truthyIndices(x.itr)),
    addPart("Ṫ", Monad, false) {
      case VList(indices) =>
        if !indices.forall(_.isInstanceOf[VNum]) then
          throw InvalidListOverloadException("Ṫ", indices, "Number")
        val greatestIndex = indices.max.asInstanceOf[VNum]
        var result = Seq.fill(greatestIndex.toInt + 1)(VNum(0))
        for index <- indices do
          result = result.updated(index.asInstanceOf[VNum].toInt, VNum(1))
        result
    },
    "Ŀ" -> fullToImpl(Monad, x => x.itr.map(_.itr.bigLength)),
    "¤" -> fullToImpl(Monad, x => x.toString),
    "Ł" ->
      direct(Monad) {
        push(peek().itr.bigLength)
      },
    "ḧ" ->
      fullToImpl(Monad, x => x.itr.map(_.itr.headOption.getOrElse(VNum(0)))),
    "①" -> niladify(10),
    "②" -> niladify(16),
    "③" -> niladify(32),
    "④" -> niladify(64),
    "⑤" -> niladify(100),
    "⑥" -> niladify(128),
    "⑦" -> niladify(256),
    "⑧" -> niladify(-1),
    "kæ" -> niladify(NumberHelpers.probablePrimes),
    "k+" -> niladify(Seq(-1, 1)),
    "k-" -> niladify(Seq(1, -1)),
    "k≈" -> niladify(Seq(0, 1)),
    "k±" -> niladify(Seq(1, 1)),
    "k0" -> niladify(360),
    "k1" -> niladify(1000),
    "k2" -> niladify(10000),
    "k3" -> niladify(100000),
    "k4" -> niladify(1000000),
    "k5" -> niladify(VNum("4294967296")),
    "k6" -> niladify("0123456789abcdef"),
    "kA" -> niladify("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    "kB" -> niladify("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"),
    "kD" -> niladify("|/-_"),
    "kF" -> niladify("FizzBuzz"),
    "kH" -> niladify("Hello, World!"),
    "kL" -> niladify("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    "kP" -> niladify(((' ' to '~').toList).mkString),
    "kR" ->
      niladify(
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
      ),
    "kV" -> niladify("AEIOU"),
    "kY" -> niladify("AEIOUY"),
    "kZ" -> niladify("ZYXWVUTSRQPONMLKJIHGFEDCBA"),
    "k^" -> niladify("0123456789ABCDEF"),
    "ka" -> niladify("abcdefghijklmnopqrstuvwxyz"),
    "kd" -> niladify("0123456789"),
    "ke" -> niladify(spire.math.Real.e),
    "kg" -> niladify(spire.math.Real.phi),
    "kh" -> niladify("Hello World"),
    "ki" -> niladify(spire.math.Real.pi),
    "kk" -> niladify("Hello, World!"),
    "kl" -> niladify("ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba"),
    "ko" -> niladify("01234567"),
    "kp" ->
      niladify(
        ((' ' to '/').toList ++:
          (':' to '@').toList ++:
          ('[' to '`').toList ++:
          ('{' to '~').toList).mkString
      ),
    "kr" ->
      niladify(
        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
      ),
    "kv" -> niladify("aeiou"),
    "ky" -> niladify("aeiouy"),
    "kz" -> niladify("zyxwvutsrqponmlkjihgfedcba"),
    "k⎶" -> niladify("{}[]<>()"),
    "k☷" -> niladify("()[]{}"),
    "k◲" -> niladify("()[]"),
    "k∪" -> niladify("([{"),
    "k∩" -> niladify(")]}"),
    "k<" -> niladify("([{<"),
    "k>" -> niladify(")]}>"),
    "k⎀" -> niladify("aeiouAEIOU"),
    "k⩔" -> niladify(Codepage),
    "k½" -> niladify(Seq(1, 2)),
    "k①" -> niladify(180),
    "k②" -> niladify(270),
    "k③" -> niladify(2048),
    "k④" -> niladify(4096),
    "k⑤" -> niladify(8192),
    "k⑥" -> niladify(16384),
    "k⑦" -> niladify(32768),
    "k⑧" -> niladify(65536),
    "k⁰" -> niladify(VNum("2147483648")),
    "kġ" -> niladify("bcdfghjklmnpqrstvwxyz"),
    "kɠ" -> niladify("bcdfghjklmnpqrstvwxz"),
    "kĠ" -> niladify("BCDFGHJKLMNPQRSTVWXYZ"),
    "kƓ" -> niladify("BCDFGHJKLMNPQRSTVWXZ"),
    "k⎘" -> niladify("[]<>-+.,"),
    "k⌹" -> niladify(Seq("()", "[]", "{}", "<>")),
    "k¤" -> niladify("([{<>}])"),
    "k²" -> niladify(VNum("1048576")),
    "k³" -> niladify(VNum("1073741824")),
    "kγ" -> niladify("aeiouyAEIOUY"),
    "k◌" -> niladify(VList(Seq(Seq(0, 1), Seq(1, 0), Seq(0, -1), Seq(-1, 0)))),
    "kℂ" -> niladify("IVXLCDM"),
    "k•" -> niladify(Seq("qwertyuiop", "asdfghjkl", "zxcvbnm")),
    addPart("#C", Monad, true) {
      case VStr(a) => StringHelpers.compressDictionary(a)
    },
    "#Q" ->
      direct(0) {
        throw QuitException()
      },
    "#X" ->
      direct(0) {
        throw BreakLoopException()
      },
    "#x" ->
      direct(0) {
        throw ContinueLoopException()
      },
    addPart("#c", Monad, true) {
      case VStr(a) => StringHelpers.compress252(a)
      case a: VNum => StringHelpers.compress252(a)
    },
    addPart("#w", Monad, false) {
      case scalar: (VVal | VFun) => VList(Seq(scalar))
      case lst: VList => lst
    },
    "#¿" ->
      direct(0) {
        push(summon[Context].globals.inputs.length)
      },
    addPart("∆<", Monad, true) {
      case a: VNum => a.arg
    },
    "kN" ->
      niladify(VList(LazyList.unfold(VNum(1)) {
        case VNum(n, _) => Some((VNum(n), VNum(n + 1)))
      })),
    "kṬ" ->
      niladify(
        VList(
          LazyList.unfold(VNum(0) -> true) {
            case (num, negate) =>
              val now = if negate then -num else num
              val next = if negate then num + 1 else num
              Some((now, next -> !negate))
          }
        )
      ),
    addPart("∆s", Monad, true) {
      case a: VNum => a.sin
    },
    addPart("∆c", Monad, true) {
      case a: VNum => a.cos
    },
    addPart("∆t", Monad, true) {
      case a: VNum => a.tan
    },
    addPart("∆↯", Monad, true) {
      case a: VNum => a.asin
    },
    addPart("∆ℭ", Monad, true) {
      case a: VNum => a.acos
    },
    addPart("∆ʈ", Monad, true) {
      case a: VNum => a.atan
    },
    addPart("∆Ṭ", Dyad, true) {
      case (y: VNum, x: VNum) => y.atan2(x)
    },
    addPart("∆S", Monad, true) {
      case a: VNum => a.sinh
    },
    addPart("∆C", Monad, true) {
      case a: VNum => a.cosh
    },
    addPart("∆T", Monad, true) {
      case a: VNum => a.tanh
    },
    addPart("∆<", Monad, true) {
      case a: VNum => a.arg
    },
    addPart("∆R", Monad, true) {
      case a: VNum => VNum(a.real)
    },
    addPart("∆I", Monad, true) {
      case a: VNum => VNum(a.imag)
    },
    addPart("∆q", Monad, true) {
      case a: VNum =>
        val factors = NumberHelpers.primeFactors(a)
        val primes = factors.distinct
        val exponents = primes
          .map(prime => NumberHelpers.multiplicity(a, prime.asInstanceOf[VNum]))
        VList(exponents)
    },
    addPart("∆L", Dyad, false) {
      case (a: VNum, b: VNum) => NumberHelpers.lcm(a, b)
      case (a: VList, b: VNum) => NumberHelpers.lcm(b +: a)
      case (a, b: VList) =>
        summon[Context].push(a)
        NumberHelpers.lcm(b)
    },
    addPart("∆⌹", Monad, true) {
      case a: VNum => VList(Seq(a.real, a.imag))
    },
    addPart("∆⎀", Monad, true) {
      case a: VNum => VList(Seq(a.vabs, a.arg))
    },
    addPart("∆r", Monad, true) {
      case a: VNum => a * VNum(spire.math.Real.pi) / VNum(180)
    },
    addPart("∆d", Monad, true) {
      case a: VNum => a / VNum(spire.math.Real.pi) * VNum(180)
    },
    addPart("ÞR", Dyad, false) {
      case (a, VListOf[VNum](b)) =>
        val shape = b
        ListHelpers.reshape(ListHelpers.makeIterable(a), shape)
      case (a, b: VNum) =>
        ListHelpers.reshape(ListHelpers.makeIterable(a), Seq(b))
    },
    addPart("∆⧢", Monad, true) {
      case a: VNum => VNum(spire.math.Real.e) **
          (VNum.complex(0, 2) * VNum(spire.math.Real.pi) / a)
    },
    addPart("∆A", Monad, false) {
      case VListOf[VNum](numbers) => numbers.sum / numbers.length
      case a: VNum => a
    },
    addPart("∆G", Monad, false) {
      case VListOf[VNum](numbers) => numbers.product **
          (1 / VNum(numbers.length))
      case a: VNum => a
    },
    addPart("∆H", Monad, false) {
      case VListOf[VNum](numbers) => numbers.length / numbers.map(1 / _).sum
      case a: VNum => a
    },
    addPart("∆æ", Monad, true) {
      case a: VNum =>
        if a < 2 then VList(Seq.empty)
        else
          val primes = NumberHelpers.probablePrimes.takeWhile(
            _ <= NumberHelpers.primeFactors(a).maxOption.getOrElse(2)
          )
          val exponents = primes.map(prime =>
            NumberHelpers.multiplicity(a, prime.asInstanceOf[VNum])
          )
          VList(exponents)
    },
    addPart("∆∧", Dyad, true) {
      case (a: VNum, b: VNum) => a.toBigInt & b.toBigInt
    },
    addPart("∆∨", Dyad, true) {
      case (a: VNum, b: VNum) => a.toBigInt | b.toBigInt
    },
    addPart("∆¬", Monad, true) {
      case a: VNum => ~a.toBigInt
    },
    addPart("∆⊍", Dyad, true) {
      case (a: VNum, b: VNum) => a.toBigInt ^ b.toBigInt
    },
    addPart("øA", Monad, true) {
      case a: VNum =>
        "abcdefghijklmnopqrstuvwxyz".charAt(((a - 1) % 26).toInt).toString
      case VStr(a) =>
        val inds = a.map(char =>
          VNum("abcdefghijklmnopqrstuvwxyz".indexOf(char.toLower) + 1)
        )
        if inds.length == 1 then inds.head else VList(inds)
    },
    addPart("ø◲", Dyad, false) {
      case (VList(a), b) => VList((b +: a) :+ b)
      case (VStr(a), VStr(b)) => b + a + b
      case (a, VList(b)) => VList((a +: b) :+ a)
    },
    addPart("Þ0", Dyad, false) {
      case (a: VList, b: VNum) => ListHelpers.zeroPad(a, b)
      case (VStr(a), b: VNum) => StringHelpers.zeroPad(a, b)
      case (a: VNum, b: VNum) => StringHelpers.zeroPad(a.toString, b)
      case (a: VNum, b: VList) => ListHelpers.zeroPad(b, a)
      case (a: VNum, VStr(b)) => StringHelpers.zeroPad(b, a)
      case (a: VList, b) => ListHelpers.zeroPad(a, makeIterable(b).bigLength)
      case (VStr(a), b) => StringHelpers.zeroPad(a, makeIterable(b).bigLength)
    },
    "ÞO" ->
      direct(Monad) {
        val top = pop()
        top match
          case VList(a) => push(
              ListHelpers.gridNeighboursWrap(a)
            )
          case _ =>
            val next = pop()
            (top, next) match
              case (a: VNum, b: VList) => push(
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
    addPart("ÞR", Dyad, false) {
      case (a, VList(b)) =>
        if !b.forall(_.isInstanceOf[VNum]) then
          // I'd use VListOf[VNum] here, but that seems to make
          // the tests break
          throw InvalidListOverloadException("ÞR", b, "Number")
        ListHelpers.reshape(a.itr, b.map(_.asInstanceOf[VNum]))
      case (a, b: VNum) => ListHelpers.reshape(a.itr, Seq(b))
    },
    addPart("ÞT", Monad, false) {
      case a: VFun => throw UnimplementedOverloadException("ÞT", List(a))
      case a => ListHelpers.transposeSafe(ListHelpers.makeIterable(a))
    },
    addPart("Þi", Dyad, true) {
      case (a, VList(b)) => ListHelpers.multiDimIndex(makeIterable(a), b)
    },
    "Þo" ->
      direct(Monad) {
        val top = pop()
        top match
          case VList(a) => push(
              ListHelpers.gridNeighbours(a)
            )
          case _ =>
            val next = pop()
            (top, next) match
              case (a: VNum, b: VList) => push(
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
    addPart("Þ↻", Monad, false) {
      case VList(a) =>
        if a.isEmpty then VList(Seq.empty)
        else
          lazy val temp: LazyList[VAny] = LazyList.from(a) #::: temp
          VList(temp)
      case other => LazyList.continually(other)
    },
    "Þ¤" ->
      direct(Monad) {
        val top = pop()
        top match
          case VList(a) => push(
              ListHelpers.gridNeighboursDiagonalWrap(a)
            )
          case _ =>
            val next = pop()
            (top, next) match
              case (a: VNum, VList(b)) => push(
                  ListHelpers.gridNeighboursDiagonalWrap(
                    b,
                    a >= 0,
                    (a.vabs % 8).toInt,
                  )
                )
              case (a, b) =>
                throw UnimplementedOverloadException("Þ¤", List(a, b))
        end match
      },
    "ÞX" ->
      fullToImpl(
        Dyad,
        (left, right) =>
          ListHelpers.cartesianProduct(left, right, unsafe = true),
      ),
    "Þ⁰" ->
      fullToImpl(
        Monad,
        x =>
          x.itr.zipWithIndex
            .map((value, index) => MiscHelpers.multiply(value, index)),
      ),
    "Þ¹" ->
      fullToImpl(
        Monad,
        x =>
          x.itr.zipWithIndex
            .map((value, index) => MiscHelpers.multiply(value, index + 1)),
      ),
    "Þ⊍" ->
      fullToImpl(
        Dyad,
        (left, right) => (left.itr -- right.itr) ++ (right.itr -- left.itr),
      ),
    "Þ⦰" -> fullToImpl(Dyad, (left, right) => left.itr -- right.itr),
    "Þ∩" ->
      fullToImpl(
        Dyad,
        (left, right) => ListHelpers.multiSetIntersection(left.itr, right.itr),
      ),
    addPart("Þ⎀", Triad, false) {
      case (a, VList(b), c) => ListHelpers.multiDimAssign(a.itr, b, c)
    },
    "Þ◌" ->
      direct(Monad) {
        val top = pop()
        top match
          case VList(a) => push(
              ListHelpers.gridNeighboursDiagonal(a)
            )
          case _ =>
            val next = pop()
            (top, next) match
              case (a: VNum, b: VList) => push(
                  ListHelpers.gridNeighboursDiagonal(
                    b,
                    a >= 0,
                    (a.vabs % 8).toInt,
                  )
                )
              case (a, b) =>
                throw UnimplementedOverloadException("Þ◌", List(a, b))
        end match
      },
    addPart("Þ⅟", Monad, false) {
      case VListOf[VList](lst) => ListHelpers.matrixInverse(lst).getOrElse {
          scribe.warn(s"Could not invert matrix $lst")
          lst
        }
    },
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
      },
    "#|parallel-apply" ->
      direct(Dyad) {
        val ctx = summon[Context]
        val functionG = pop().asInstanceOf[VFun]
        val functionF = pop().asInstanceOf[VFun]

        functionF.ctx = ctx.copy

        val resF = Interpreter.executeFn(functionF)(using ctx.copy)
        val resG = Interpreter.executeFn(functionG)(using ctx)
        pop()
        push(resF, resG)
      },
    "#|both" ->
      direct(Monad) {
        val ctx = summon[Context]
        val functionF = pop().asInstanceOf[VFun]

        val args1 = ctx.pop(functionF.arity)
        val args2 = ctx.pop(functionF.arity)

        push(Interpreter.executeFn(functionF, args = args2))
        push(Interpreter.executeFn(functionF, args = args1))
      },
    "#|fork" ->
      direct(Dyad) {
        val ctx = summon[Context]
        val functionG = pop().asInstanceOf[VFun]
        val functionF = pop().asInstanceOf[VFun]

        val y = peek()
        val resF = Interpreter.executeFn(functionF)
        push(resF)
        push(y)
        val resG = Interpreter.executeFn(functionG)(using ctx.copy)
        push(resG)
      },
    "#|inner-product" ->
      direct(Dyad) {
        val functionG = pop().asInstanceOf[VFun]
        val functionF = pop().asInstanceOf[VFun]

        val rightList = pop().ritr
        val leftList = pop().ritr

        val result = leftList.zip(rightList).map {
          case (left, right) =>
            Interpreter.executeFn(functionF, args = Seq(left, right))
        }
        push(
          result.reduceLeft((a, b) =>
            Interpreter.executeFn(functionG, args = Seq(a, b))
          )
        )
      },
    "#|outer-product" ->
      direct(Monad) {
        val function = pop().asInstanceOf[VFun]
        val rightList = pop().ritr
        val leftList = pop().ritr

        push(leftList.map { elem =>
          rightList.map { otherElem =>
            Interpreter.executeFn(function, args = Seq(otherElem, elem))
          }
        })
      },
    "#|each" ->
      direct(Monad) {
        val function = pop().asInstanceOf[VFun]
        FuncHelpers.each(function)
      },
    "#|zip-with" ->
      direct(Dyad) {
        val function = pop().asInstanceOf[VFun]
        val rightList = pop().itr
        val leftList = pop().itr

        push(
          leftList.zip(rightList).map {
            case (left, right) =>
              Interpreter.executeFn(function, args = Seq(right, left))
          }
        )
      },
    "#|if" ->
      direct(Monad) {
        val function = pop().asInstanceOf[VFun]
        val condition = pop().toBool

        if condition then push(Interpreter.executeFn(function))
      },
    "#|if-else" ->
      direct(Dyad) {
        val elseFunction = pop().asInstanceOf[VFun]
        val ifFunction = pop().asInstanceOf[VFun]
        val condition = pop().toBool

        if condition then push(Interpreter.executeFn(ifFunction))
        else push(Interpreter.executeFn(elseFunction))
      },
    "#|dip" ->
      direct(Monad) {
        val f = pop()
        val top = pop()
        f match
          case fun: VFun =>
            Interpreter.executeFn(fun)
            push(top)
          case arg => throw UnimplementedOverloadException("#|dip", List(arg))
      },
    "#|invariant" ->
      direct(Monad) {
        val function = pop().asInstanceOf[VFun]
        val argument = pop()
        val result = Interpreter.executeFn(function, args = Seq(argument))
        push(result == argument)
      },
    "#|vectorise" ->
      direct(Monad) {
        val function = pop().asInstanceOf[VFun]
        FuncHelpers.deepVectorise(function)
      },
    "#|eager-map" ->
      direct(Monad) {
        val function = pop().asInstanceOf[VFun]
        val list = pop().ritr
        val res = ArrayBuffer[VAny]()
        for elem <- list do
          res += Interpreter.executeFn(function, args = Seq(elem))
        push(res.toSeq)
      },
    "#|at-simple-levels" ->
      direct(Monad) {
        val function = pop().asInstanceOf[VFun]
        push(FuncHelpers.atSimpleLevels(function))
      },
    "#|permutations-map" ->
      direct(Monad) {
        val function = pop().asInstanceOf[VFun]
        val list = pop()
        push(ListHelpers.permutations(list.ritr).map { perm =>
          val arg =
            if list.isInstanceOf[VStr] then VStr(perm.mkString) else perm
          Interpreter.executeFn(function, args = Seq(arg))
        })
      },
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

  private def direct[P, F](arity: ImplHelpers[P, F])(
      impl: Context ?=> Unit
  ): Element = Element(arity.arity, () => impl)

  private def direct(arity: Int)(impl: Context ?=> Unit): Element =
    Element(arity, () => impl)

end Elements
