package vyxal.elements

import scala.language.implicitConversions

import vyxal.*
import vyxal.{Dyad, ImplHelpers, Monad, Triad}
import vyxal.conversions.{*, given}
import vyxal.elements.Modifiers.addPart
import vyxal.parsing.Codepage
import vyxal.Context.{peek, pop, push}
import vyxal.ListHelpers.makeIterable
import vyxal.MiscHelpers.defaultEmpty

import java.time.{Duration as JDuration, ZoneId, ZonedDateTime}
import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn

import spire.math.Polynomial

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
    "🍪" ->
      direct(0) {
        while true do println("🍪")
      },
    "Ƶ" ->
      direct(Monad) {
        val ctx = summon[Context]
        pop() match
          case fn: VFun =>
            val iter = pop()
            if iter.isInstanceOf[VPhysical] then
              push(ListHelpers.augmentAssign(iter.itr, -1, fn))
            else throw UnimplementedOverloadException("Ƶ", List(fn, iter))
          case a: VPhysical =>
            val iter = a.itr
            if iter.isEmpty then push(VList(Seq.empty), 0)
            else push(iter.init, iter.last)
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
      case (a: VDuration, b: VNum) =>
        if b.toLong == 0 then VDuration.Zero
        else VDuration(JDuration.ofMillis(a.dur.toMillis / b.toLong))
      case (a: VDuration, b: VDuration) =>
        if b.dur.toMillis == 0 then VNum(0)
        else VNum(a.dur.toMillis.toDouble / b.dur.toMillis.toDouble)
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
      case d: VDate => NumberHelpers
          .range(1, 12)
          .asInstanceOf[Seq[VNum]]
          .map((m: VNum) =>
            VDate.of(
              d.year.toInt,
              m.toInt,
              d.day.toInt,
              d.hour.toInt,
              d.minute.toInt,
              d.second.toInt,
            )
          )
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
      case a: VDate => VDate(a.dt.minusDays(1))
      case a: VDuration => VDuration(a.dur.minus(JDuration.ofDays(1)))
    },
    addPart("›", Monad, true) {
      case a: VNum => a + 1
      case VStr(a) => a.replace(" ", "0")
      case a: VDate => VDate(a.dt.plusDays(1))
      case a: VDuration => VDuration(a.dur.plus(JDuration.ofDays(1)))
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
      case (VList(a), b) => VList(a.itr :+ b)
      case (VStr(a), b) => VList(a.itr :+ b)
      case (a, b) => VList(Seq(a) :+ b)
    },
    "Ꮬ" ->
      fullToImpl(
        Monad,
        a => a.itr.map(v => v.itr.mkString("")).mkString("\n"),
      ),
    "Ꮠ" ->
      fullToImpl(
        Monad,
        a => ListHelpers.gridify(a),
      ),
    "'" ->
      fullToImpl(
        Monad,
        a => a.itr.map(v => v.itr.mkString(" ")).mkString("\n"),
      ),
    addPart("*", Dyad, true) {
      case (a: VNum, b: VNum) => a ** b
      case (VStr(a), b: VNum) => StringHelpers.extendString(b, a)
      case (a: VNum, VStr(b)) => StringHelpers.extendString(a, b)
      case (VStr(a), VStr(b)) =>
        if a.length > b.length then StringHelpers.extendString(a, b)
        else StringHelpers.extendString(b, a)
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
      case (b: VNum, a: VFun) =>
        var res = b
        while !a(res).toBool do res -= 1
        res
      case (a: VFun, b: VNum) =>
        var res = b
        while !a(res).toBool do res -= 1
        res
      case (b: VDate, a: VFun) =>
        var res = b
        while !a(res).toBool do res = VDate(res.dt.minusDays(1))
        res
      case (a: VFun, b: VDate) =>
        var res = b
        while !a(res).toBool do res = VDate(res.dt.minusDays(1))
        res
    },
    addPart("=", Dyad, true) {
      case (a: VNum, b: VNum) => a == b
      case (a: VNum, VStr(b)) => a.toString == b
      case (VStr(a), b: VNum) => a == b.toString
      case (VStr(a), VStr(b)) => a == b
      case (a: VDate, b: VDate) => a === b
      case (a: VDuration, b: VDuration) => a === b
    },
    addPart(">", Dyad, true) {
      case (a: VVal, b: VVal) => a > b
      case (a: VFun, b: VNum) =>
        var res = b
        while a(res).toBool do res += 1
        res
      case (b: VNum, a: VFun) =>
        var res = b
        while a(res).toBool do res += 1
        res
      case (a: VFun, b: VDate) =>
        var res = b
        while a(res).toBool do res = VDate(res.dt.plusDays(1))
        res
      case (b: VDate, a: VFun) =>
        var res = b
        while a(res).toBool do res = VDate(res.dt.plusDays(1))
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
      case (a: VDate, b: VDate) =>
        val dur = JDuration.between(b.dt, a.dt)
        VDuration(if dur.isNegative then dur.negated else dur)
      case (a: VDuration, b: VDuration) =>
        val dur = a.dur.minus(b.dur)
        VDuration(if dur.isNegative then dur.negated else dur)
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
      case d: VDate => d.dayOfWeek
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
      case a: VDate => VList(
          Seq(
            a.year,
            a.month,
            a.day,
            a.hour,
            a.minute,
            a.second,
          )
        )
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
      case VStr(a) =>
        NumberHelpers.fromBaseAlphabet(a.toUpperCase, "0123456789ABCDEF")
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
        a match
          case d: VDuration => push(d.toSeconds)
          case _ => push(a.itr.length)
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
      case a: VDuration => VDuration(a.dur.negated)
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
      case (a: VPhysical, b: VNum) =>
        val lst = a.itr
        val index = b.toInt
        if index < 0 then
          VList(
            lst.take(lst.length + index) ++ lst.drop(lst.length + index + 1)
          )
        else VList(lst.take(index) ++ lst.drop(index + 1))
      case (a: VNum, b: VPhysical) =>
        val lst = b.itr
        val index = a.toInt
        if index < 0 then
          VList(
            lst.take(lst.length + index) ++ lst.drop(lst.length + index + 1)
          )
        else VList(lst.take(index) ++ lst.drop(index + 1))
      case (VStr(a), VStr(b)) =>
        val res = StringHelpers.r(b).findFirstMatchIn(a)
        if res.isDefined then res.get.subgroups else Seq.empty
      case (a: VPhysical, b: VFun) =>
        VList(0 +: FuncHelpers.reduceOverPairs(b, makeIterable(a)))
      case (a: VFun, b: VPhysical) =>
        VList(0 +: FuncHelpers.reduceOverPairs(a, makeIterable(b)))
    },
    addPart("R", Dyad, true) {
      case (a: VNum, b: VNum) => NumberHelpers.range(a, b).dropRight(1)
      case (a: VDate, b: VDate) =>
        val step = if a < b then 1L else -1L
        VList(LazyList.unfold(a) { current =>
          if (step > 0 && current < b) || (step < 0 && current > b) then
            Some((current, VDate(current.dt.plusDays(step))))
          else None
        })
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
      case a: VDate => VList(Seq(a.year, a.month, a.day))
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
      case (a: VIter, b: VList) =>
        val temp = b
          .map {
            case n: VNum => n.toInt
            case l: VIter => ListHelpers.makeIterable(l).length
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
      fullToNullImpl(
        NullMonad,
        NullHelpers.popArgs(_),
      ), // null objects pop args and return nothing
    "#`" -> niladify(ctx ?=> ctx.getStack.bigLength),
    addPart("a", Monad, false) {
      case a: VNum => a.itr.exists(_ != VNum(0))
      case VStr(a) if a.length == 1 => a.head.isUpper
      case VStr(a) => VList(a.map(c => VNum(c.isUpper)))
      case a: VList => a.itr.exists(_.toBool)
    },
    "b" -> fullToImpl(Monad, NumberHelpers.fromBinary),
    addPart("c", Dyad, false) {
      case (a: VDate, b: VNum) => a.plusMonths(b.toLong)
      case (a: VNum, b: VDate) => b.plusMonths(a.toLong)
      case (a: VVal, b: VVal) => a.toString().contains(b.toString())
      case (a: VList, b: VVal) => a.contains(b)
      case (a: VVal, b: VList) => b.contains(a)
      case (a: VList, b: VList) =>
        val (needle, haystack) =
          if ListHelpers.maxDepth(a.lst) <= ListHelpers.maxDepth(b.lst) then
            (a, b)
          else (b, a)
        haystack.contains(needle)

      case (predicate: VFun, initial) =>
        MiscHelpers.untilNoChange(predicate, initial).length
      case (initial, predicate: VFun) =>
        MiscHelpers.untilNoChange(predicate, initial).length
    },
    addPart("d", Monad, true) {
      case a: VNum => a + a
      case VStr(a) => s"$a$a"
      case a: VDuration => VDuration(a.dur.plus(a.dur))
    },
    addPart("e", Monad, true) {
      case a: VNum => a % 2 == VNum(0)
      case VStr(a) => a.split("\n").toIndexedSeq
      case a: VDate =>
        val y = a.dt.getYear
        VNum(y % 4 == 0 && (y % 100 != 0 || y % 400 == 0))
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
      case (VStr(a), b: VVal) => b.toString + a
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
    addPart("Þv", Monad, false) {
      case VStr(a) =>
        val v = ListHelpers.overlaps(a, 2).map(VStr(_))
        VList(0 +: v)
      case a =>
        val v = ListHelpers.overlaps(a.itr, 2).map(VList(_))
        VList(0 +: v)
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
      case (VStr(a), b: VPhysical, c: VPhysical) => StringHelpers.transliterate(
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
            val reversedFlipped =
              StringHelpers.invertBrackets(s).reverse.drop(1).map {
                case '/' => '\\'
                case '\\' => '/'
                case c => c
              }
            s"$s$reversedFlipped"
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
    addPart("∆σ", Monad, false) {
      case a =>
        val list = ListHelpers.makeIterable(a)
        if list.isEmpty then Seq(0)
        else if list.tail.isEmpty then Seq(VNum(0), list.head)
        else
          val sums = VList(
            list.tail.scanLeft(
              list.head
            )((x, y) => MiscHelpers.add(x, y))
          )
          VList(VNum(0) +: sums)
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
      case (a: VList, b: VVal) => a.filter(_ != b)
      case (a: VVal, b: VList) => b.filter(_ != a)
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
      case a: VDate => VList(
          Seq(
            a.year,
            a.month,
            a.day,
            a.hour,
            a.minute,
            a.second,
          )
        )
    },
    addPart("⌊", Monad, true) {
      case a: VNum => a.floor
      case a: VDate => a.toUnixTime
      case VStr(a) =>
        if a.isEmpty then 0
        else
          val filtered = a.filter(c => c.isDigit || "-.".contains(c))
          val negated =
            s"${filtered.headOption.getOrElse(0)}${filtered.tail.replace("-", "")}"
          val decimaled = negated.splitAt(negated.indexOf('.')) match
            case ("", s) =>
              if a.count('.' == _) > 1 then ("." + s.dropWhile(_ == '.')) else s
            case (a, b) => a + "." + b.replace(".", "")
          val zeroless =
            if decimaled.startsWith("-") then
              "-" + decimaled.drop(1).dropWhile(_ == '0')
            else decimaled.dropWhile(_ == '0')
          if zeroless.isEmpty then 0
          else MiscHelpers.eval(zeroless)
    },
    addPart("⊖", Dyad, false) {
      case (a, b: VNum) =>
        val temp = ListHelpers.take(a.itr, b)
        a match
          case VStr(_) => temp.mkString
          case _ => temp
      case (a: VNum, b: VIter) => ListHelpers.take(b.itr, a)
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

    // Register stuff, see Ͼ for apply-to-head and ⎘ for map

    "£" ->
      direct(1) {
        val a = pop()
        RegisterHelpers.push(a)
        if summon[Context].settings.registerPeek then push(a)
      },
    "¥" -> niladify { RegisterHelpers.pop(peek = true) },
    "`" -> niladify { RegisterHelpers.pop() },
    "Þ¥" -> niladify { RegisterHelpers.popAll() },
    "Þw" -> niladify { RegisterHelpers.popAll(peek = true) },
    "Þ`" -> niladify { RegisterHelpers.length },
    "Þ_" -> nop() { RegisterHelpers.clear },
    "Þ^" -> nop() { RegisterHelpers.reverseRegister },
    "Þ⍨" -> direct(0) { RegisterHelpers.popAll().itr.foreach(push(_)) },
    addPart("Þ⦷", Monad, true) {
      case i: VNum => RegisterHelpers.index(i)
    },
    "Þc" -> fullToImpl(Monad, x => RegisterHelpers.contains(x)),
    "Þ£" ->
      direct(1) {
        val a = pop()
        for v <- ListHelpers.flatten(a.itr) do RegisterHelpers.push(v)
        if summon[Context].settings.registerPeek then push(a)
      },
    addNullPart("ÞϾ", NullDyad) {
      case (idx: VNum, fn: VFun) => RegisterHelpers.applyFn(fn, idx)
      case (fn: VFun, idx: VNum) => RegisterHelpers.applyFn(fn, idx)
      case (VListOf[VNum](lst), fn: VFun) =>
        for num <- lst do RegisterHelpers.applyFn(fn, num)
      case (fn: VFun, VListOf[VNum](lst)) =>
        for num <- lst do RegisterHelpers.applyFn(fn, num)
    },
    addPart("Þ⊖", Monad, false) {
      case n: VNum => RegisterHelpers.pop(n)
      case VListOf[VNum](lst) => VList(
          Polynomial.dense(lst.map(_.real).toArray).roots.map(VNum(_)).toSeq
        )
    },
    addPart("Þ⌽", Monad, false) {
      case n: VNum => RegisterHelpers.pop(n, peek = true)
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
            push(MiscHelpers.collectUnique(predicate, item).itr.vDistinct)
          case a: VNum =>
            val times = a
            val iterable = pop()
            push(ListHelpers.rotate(iterable, times))
          case a: VDate => push(a.plusYears(1))
          case _ => throw UnsupportedOverloadException("↺", "function | object")
      },
    "↻" ->
      direct(Dyad) {
        val top = pop()
        top match
          case a: VIter => push(ListHelpers.rotate(a, -1))
          case predicate: VFun =>
            val item = pop()
            push(MiscHelpers.collectUnique(predicate, item).length)
          case a: VNum =>
            val times = a
            val iterable = pop()
            push(ListHelpers.rotate(iterable, -times))
          case a: VDate => push(a.plusYears(-1))
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
      case (a, b: VList, c: VFun) =>
        var temp = ListHelpers.makeIterable(a)
        for index <- b.map(_.asInstanceOf[VNum]) do
          temp = ListHelpers.augmentAssign(temp, index, c)
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
            case _ => throw InvalidListOverloadException("≜", b, "Number")
          }
        if a.isInstanceOf[VStr] then temp.mkString
        else temp
      case (VStr(a), VStr(b), VStr(c)) => StringHelpers.regexSub(a, b, c)
      case (VStr(a), VStr(b), c: VFun) => StringHelpers.regexSub(a, b, c)
      case (VStr(a), b: VFun, VStr(c)) => StringHelpers.regexSub(a, c, b)
      case (a: VFun, VStr(b), VStr(c)) => StringHelpers.regexSub(b, c, a)
    },
    addPart("⎀", Triad, false) {
      case (a: VDate, b: VDate, c: VDate) =>
        val lo = if b <= c then b else c
        val hi = if b <= c then c else b
        VNum(a >= lo && a <= hi)
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
      case (date: VDate, VStr(tz)) => date.withZone(tz)
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
      case (a, b: VFun) => ListHelpers.truthyIndices(ListHelpers.map(b, a.itr))
      case (a: VFun, b) => ListHelpers.truthyIndices(ListHelpers.map(a, b.itr))

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
        var res = 0
        while !predicate(VNum(res)).toBool do res += 1
        res
    },
    addPart("Ϣ", Dyad, false) {
      case (a: VList, b: VNum) => ListHelpers.wrapLength(a, b)
      case (a: VNum, b: VNum) =>
        if b <= 0 then Seq.empty
        else a.toString.grouped(b.toInt).toSeq.map(n => VNum(n))
      case (VStr(a), b: VNum) =>
        if b <= 0 then Seq.empty
        else a.grouped(b.toInt).toSeq
      case (a: VNum, VStr(b)) =>
        if a <= 0 then Seq.empty
        else b.grouped(a.toInt).toSeq
      case (a: VNum, b: VList) => ListHelpers.wrapLength(b, a)
      case (VStr(a), b: VList) =>
        if a.isEmpty then Seq.empty
        else ListHelpers.wrapLength(b, VNum(a.length))
      case (a: VList, VStr(b)) =>
        if b.isEmpty then Seq.empty
        else ListHelpers.wrapLength(a, VNum(b.length))
      case (VStr(a), VStr(b)) =>
        if a.isEmpty || b.isEmpty then Seq.empty
        else if b.length < a.length then
          a.grouped(b.length).toSeq // always chunk to the shorter length
        else b.grouped(a.length).toSeq
      case (a: VList, b: VList) =>
        if b.forall(_.isInstanceOf[VNum]) then
          ListHelpers.partitionBy(a, b.map(_.asInstanceOf[VNum]))
        else throw InvalidListOverloadException("Ϣ", b, "Number")
      case (a: VFun, b: VNum) => MiscHelpers.predicateSlice(a, b, 0)
      case (a: VNum, b: VFun) => MiscHelpers.predicateSlice(b, a, 0)
      case (a: VFun, b: VList) =>
        // All permutations where the function is true.
        ListHelpers
          .permutations(b)
          .filter { perm =>
            a(perm).toBool
          }
          .toSeq
      case (a: VList, b: VFun) => ListHelpers
          .permutations(a)
          .filter { perm =>
            b(perm).toBool
          }
          .toSeq
    },
    addPart("≤", Dyad, true) {
      case (a, b: VFun) => a.itr.minByOption(x => b(x)) match
          case Some(max) => max
          case None => VNum(0)
      case (b: VFun, a) => a.itr.minByOption(x => b(x)) match
          case Some(min) => min
          case None => VNum(0)
      case (a: VVal, b: VVal) => a <= b
    },
    addPart("≥", Dyad, true) {
      case (a, b: VFun) => a.itr.maxByOption(x => b(x)) match
          case Some(max) => max
          case None => VNum(0)
      case (b: VFun, a) => a.itr.maxByOption(x => b(x)) match
          case Some(min) => min
          case None => VNum(0)
      case (a: VVal, b: VVal) => a >= b
    },
    addPart("≠", Dyad, true) {
      case (a: VVal, b: VVal) => a.toString != b.toString
    },
    "≡" -> fullToImpl(Dyad, (a, b) => a === b),
    addPart(
      "•",
      Dyad,
      false,
    ) { // welcome back multi-command
      case (a: VList, b: VVal) => VList(a.map(x => VList(Seq(x, b))))
      case (a: VVal, b: VList) => VList(b.map(x => VList(Seq(x, a))))
      case (a: VList, b: VList) => ListHelpers.dotProduct(a, b)

      case (VStr(a), VStr(b)) =>
        val capitals = a.map(l => StringHelpers.caseOf(l.toString))
        b.zip(capitals)
          .map { (char: Char, upper: VNum) =>
            if char.isLetter then
              if upper.toBool then char.toString().toUpperCase()
              else char.toString().toLowerCase()
            else char
          }
          .mkString

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
    addPart("†", Monad, false) {
      case x: VPhysical => VList(
          ListHelpers
            .groupConsecutive(x.itr)
            .map(group => VNum(group.itr.bigLength))
        )
    },
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
          if temp.startsWith("-") then temp.tail.reverse
          else temp.reverse
        VNum(temp + reversed)
      case VStr(str) => str + str.reverse
      case lst: VList => VList(lst ++ lst.reverse)
    },
    addPart("Þ≓", Monad, false) {
      case num: VNum =>
        val temp = num.toString
        val reversed =
          if temp.startsWith("-") then temp.tail.reverse
          else temp.reverse
        VNum(temp + reversed.tail)
      case VStr(str) => str + str.reverse.tail
      case lst: VList => VList(lst ++ lst.reverse.tail)
    },
    "Ͼ" ->
      direct(1) {
        val top = pop()
        top match
          case num: VNum => push(" " * num.toInt) // hallelujah
          case lst: VList =>
            push(VList(lst.map(item => ListHelpers.sum(item.itr))))
          case f: VFun => RegisterHelpers.applyFn(f, RegisterHelpers.length - 1)
          case _ => throw UnsupportedOverloadException("Ͼ", "String")
      },
    "ᴥ" -> fullToImpl(Monad, x => MiscHelpers.exec(x)),
    addPart("ℳ", Dyad, false) {
      case (a: VIter, b: VNum) => ListHelpers.nthItems(a, b)
      case (a: VNum, b: VIter) => ListHelpers.nthItems(b, a)
      case (a: VList, b: VList) => ListHelpers.matrixMultiply(a, b)
      case (a: VNum, b: VNum) =>
        if b == VNum(0) then NumberHelpers.round(a)
        else if b.toInt <= 9 then a - (a % (10 ** -(b.toInt)))
        else if a == VNum(0) then a
        else
          val intLogA =
            NumberHelpers.round(NumberHelpers.log(a.vabs, 10) + 0.5).toInt
          val sign = if a < 0 then "-" else ""
          val leadingZeroes = "0" * (if a < 1 then -intLogA + 1 else 0)
          (sign + leadingZeroes +
            NumberHelpers.round((a.vabs * (10 ** b.toInt))).toString())
            .patch( // insert the decimal dot
              (if intLogA > 0 then intLogA else 1) + (a < 0).toInt,
              ".",
              0,
            ) // fallback to return the string representation if the precision is too high
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
      case d: VDate => d.numDayOfWeek
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
      case (start: VDate, end: VDate) =>
        val step = if start <= end then 1L else -1L
        VList(LazyList.unfold(start) { current =>
          if (step > 0 && current <= end) || (step < 0 && current >= end) then
            Some((current, VDate(current.dt.plusDays(step))))
          else None
        })
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
    "␣" -> constant(" "),
    "¶" -> constant("\n"),
    "★" -> constant("*"),
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
          case fn: VFun =>
            val iter = pop()
            if iter.isInstanceOf[VPhysical] then
              push(ListHelpers.augmentAssign(iter.itr, 0, fn))
            else throw UnimplementedOverloadException("ᑂ", List(fn, iter))
          case arg => throw UnimplementedOverloadException("ᑂ", List(arg))
        end match
      },
    addPart("∻", Dyad, true) {
      case (a: VNum, b: VNum) => (a / b).floor
      case (VStr(a), VStr(b)) =>
        if a.length > b.length then b + a.slice(b.length, a.length)
        else a + b.slice(a.length, b.length)
    },
    addPart("√", Monad, true) {
      case a: VNum => a.sqrt
      case VStr(s) => s
          .split("\n")
          .map { line =>
            val reversed = s.reverse.drop(1)
            s"$s$reversed"
          }
          .mkString("\n")
    },
    addPart("⍰", Monad, true) {
      case a: VNum => a != VNum(0)
      case VStr(a) => a.nonEmpty
      case d: VDate => d.toBool
      case dur: VDuration => dur.toBool
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
      case (fn: VFun, iter: VList) =>
        // Get the first permutation of iter that satisfies fn, if it exists
        ListHelpers
          .permutations(iter)
          .find(permutation => fn(permutation).toBool)
          .getOrElse(Seq.empty)
      case (iter: VList, fn: VFun) => ListHelpers
          .permutations(iter)
          .find(permutation => fn(permutation).toBool)
          .getOrElse(Seq.empty)

    },
    "⍨" -> direct(Monad) { pop().itr.foreach(push(_)) },
    addPart("γ", Monad, false) {
      case VStr(str) => str.grouped(2).toSeq
      case VList(lst) => ListHelpers.wrapLength(lst, 2)
      case num: VNum => num.toString.grouped(2).toSeq.map(n => VNum(n))
    },
    "⎘" ->
      direct(Monad) {
        pop() match
          case layerCount: VNum =>
            val iterable = pop().itr
            push(ListHelpers.flattenByDepth(iterable, layerCount))
          case VList(a) =>
            if a.forall(_.isInstanceOf[VVal]) then
              val t = a.map(x => VList(ListHelpers.flatten(x.itr)))
              push(VList(t)) // there has GOT to be a better way to do this
            else push(ListHelpers.flattenByDepth(a, 1))
          case fn: VFun => RegisterHelpers.map(fn)
          case _ => throw UnsupportedOverloadException("⎘", "Object")
      },
    addPart("ꜝ", Monad, false) {
      case a: VNum => VNum(a.itr.filter(x => x != VNum(0)).mkString)
      case VStr(a) => a.split(",").toIndexedSeq
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
          case predicate: VFun => pop() match
              case VList(lst) =>
                val ret = lst.find(predicate(_).toBool).getOrElse(null)
                val (before, atAndAfter) = lst.span(_ != ret)
                push(before.appendedAll(atAndAfter.drop(1)))
                push(if ret != null then ret else VNum(0))
              case _ => throw UnsupportedOverloadException(
                  "κ",
                  "Truthy head extract only works on lists",
                )
          case _ => throw UnsupportedOverloadException("κ", "String | Function")
      },
    "#↸" ->
      direct(Monad) {
        val index = pop()
        index match
          case simpleOuter: VNum =>
            val ctx = summon[Context]
            val parentCtx = ctx.getParentCtx.getOrElse(ctx)
            val stack = parentCtx.getStack
            if stack.length == 1 then
              push(
                parentCtx.ctxArgs
                  .flatMap(args =>
                    Some(args.vlst.indexBig(simpleOuter.toBigInt))
                  )
                  .getOrElse(VNum(0))
              )
            else push(stack.indexBig(simpleOuter.toBigInt))
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
          case _ => throw UnsupportedOverloadException("”", "Function")
      },
    addPart("„", Monad, false) {
      case VList(lst) => ListHelpers.join(lst, " ")
      case num: VNum => num < 0
      case VStr(str) => ListHelpers.join(str.itr, " ") match
          case l: VList => l.mkString
          case res => res
    },
    "“" -> fullToImpl(Monad, x => MiscHelpers.joinNothing(x)),
    "↸" ->
      direct(Triad) {
        val top = pop()
        val under = pop()
        val bottom = pop()
        push(top, bottom, under)
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
      case VStr(s) => VDate.parse(s)
      case a: VNum => VDate.fromEpochSecond(a.toLong)
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
    "①" -> constant(10),
    "②" -> constant(16),
    "③" -> constant(32),
    "④" -> constant(64),
    "⑤" -> constant(100),
    "⑥" -> constant(128),
    "⑦" -> constant(256),
    "⑧" -> constant(-1),

    // 2 byte numerical constants
    "ke" -> constant(spire.math.Real.e),
    "kg" -> constant(spire.math.Real.phi),
    "ki" -> constant(spire.math.Real.pi),
    "k1" -> constant(1000),
    "k2" -> constant(10000),
    "k3" -> constant(100000),
    "k4" -> constant(1000000),
    "k①" -> constant(180),
    "k②" -> constant(270),
    "k0" -> constant(360),
    "k③" -> constant(2048),
    "k④" -> constant(4096),
    "k⑤" -> constant(8192),
    "k⑥" -> constant(16384),
    "k⑦" -> constant(32768),
    "k⑧" -> constant(65536),
    "k²" -> constant(VNum("1048576")),
    "k³" -> constant(VNum("1073741824")),
    "k⁰" -> constant(VNum("2147483648")),
    "k5" -> constant(VNum("4294967296")),

    // List of numbers
    "k+" -> constant(Seq(-1, 1)),
    "k-" -> constant(Seq(1, -1)),
    "k≈" -> constant(Seq(0, 1)),
    "k±" -> constant(Seq(1, 1)),
    "k=" -> constant(Seq(0, 0)),
    "k½" -> constant(Seq(1, 2)),
    "k≡" -> constant(Seq(-1, 0, 1)),
    "k◌" -> constant(VList(Seq(Seq(0, 1), Seq(1, 0), Seq(0, -1), Seq(-1, 0)))),

    // Alphanumerics
    "kA" -> constant("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    "kZ" -> constant("ZYXWVUTSRQPONMLKJIHGFEDCBA"),
    "ka" -> constant("abcdefghijklmnopqrstuvwxyz"),
    "kz" -> constant("zyxwvutsrqponmlkjihgfedcba"),
    "kB" -> constant("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"),
    "kL" -> constant("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    "kl" -> constant("ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba"),
    "kb" -> constant("zyxwvutsrqponmlkjihgfedcbaZYXWVUTSRQPONMLKJIHGFEDCBA"),
    "kr" ->
      constant(
        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
      ),
    "kR" ->
      constant(
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
      ),
    "kt" -> constant("0123456789abcdefghijklmnopqrstuvwxyz"),
    "kT" -> constant("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    "k^" -> constant("0123456789ABCDEF"),
    "k6" -> constant("0123456789abcdef"),
    "kd" -> constant("0123456789"),
    "kn" -> constant("1234567890"),
    "k9" -> constant("123456789"),
    "ko" -> constant("01234567"),
    "k•" -> constant(Seq("qwertyuiop", "asdfghjkl", "zxcvbnm")),

    // Consonants and vowels
    "kV" -> constant("AEIOU"),
    "kY" -> constant("AEIOUY"),
    "kv" -> constant("aeiou"),
    "ky" -> constant("aeiouy"),
    "k⎀" -> constant("aeiouAEIOU"),
    "kγ" -> constant("aeiouyAEIOUY"),
    "kġ" -> constant("bcdfghjklmnpqrstvwxyz"),
    "kɠ" -> constant("bcdfghjklmnpqrstvwxz"),
    "kĠ" -> constant("BCDFGHJKLMNPQRSTVWXYZ"),
    "kƓ" -> constant("BCDFGHJKLMNPQRSTVWXZ"),

    // Brackets, lines and arrows
    "k⎶" -> constant("{}[]<>()"),
    "k☷" -> constant("()[]{}"),
    "k◲" -> constant("()[]"),
    "k(" -> constant("()"),
    "k[" -> constant("[]"),
    "k{" -> constant("{}"),
    "k×" -> constant("<>"),
    "k∪" -> constant("([{"),
    "k∩" -> constant(")]}"),
    "k<" -> constant("([{<"),
    "k>" -> constant(")]}>"),
    "k¤" -> constant("([{<>}])"),
    "k⌹" -> constant(Seq("()", "[]", "{}", "<>")),
    "kD" -> constant("\\|/-_"),
    "k/" -> constant("/\\"),
    "k⇄" -> constant("^>v<"),

    // Ascii stuff
    "kP" -> constant(((' ' to '~').toList).mkString),
    "kQ" -> constant((('!' to '~').toList).mkString),
    "kp" ->
      constant(
        ((' ' to '/').toList ++:
          (':' to '@').toList ++:
          ('[' to '`').toList ++:
          ('{' to '~').toList).mkString
      ),

    // Misc Constants
    "kH" -> constant("Hello, World!"),
    "kh" -> constant("Hello World"),
    "kk" -> constant("Hello, World!"),
    "kF" -> constant("FizzBuzz"),
    "k⍾" -> constant("ඞ"), // this setup will save us 2 bytes
    "k⩔" -> constant(Codepage),
    "k¹" -> constant(Seq.empty), // empty list for multiple inputs
    "k⎘" -> constant("[]<>-+.,"),
    "kℂ" -> constant("IVXLCDM"),
    "kẄ" ->
      constant(
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
      ),

    // other digraphs
    addPart("#C", Monad, true) {
      case VStr(a) => StringHelpers.compressDictionary(a)
    },
    addPart("#D", Monad, true) {
      case VStr(a) => StringHelpers.decompress(a)
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
    "#W" ->
      direct(Monad) {
        pop() match
          case n: VNum =>
            val ctx = summon[Context]
            val l =
              Seq(VNum(ctx.getStack.bigLength), n).minOption.getOrElse(VNum(0))
            val wrapped = Seq.fill(l.toInt)(ctx.pop())
            push(VList(wrapped.padTo(n.toInt, 0)))
          case _ =>
            throw UnsupportedOverloadException("#W", "String | List | Function")
      },
    "#¿" -> niladify { summon[Context].globals.inputs.length },
    addPart("#ᴥ", Monad, false) {
      case VStr(top) => MiscHelpers.validCode(top)
    },
    "#n" -> niladify { VDate.now() },
    "#d" -> niladify {
      val now = ZonedDateTime.now()
      VDate(now.toLocalDate.atStartOfDay(now.getZone))
    },
    "#m" -> niladify {
      val now = ZonedDateTime.now()
      VDate(now.withDayOfMonth(1).toLocalDate.atStartOfDay(now.getZone))
    },
    "#y" -> niladify {
      val now = ZonedDateTime.now()
      VDate(
        now.withDayOfYear(1).toLocalDate.atStartOfDay(now.getZone)
      )
    },
    "#z" -> niladify {
      import scala.jdk.CollectionConverters.*
      VList(
        ZoneId.getAvailableZoneIds.asScala.toSeq.sorted.map(VStr(_))
      )
    },
    addPart("#t", Monad, false) {
      case VStr(s) => VDate.parse(s)
      case a: VNum => VDate.fromEpochSecond(a.toLong)
      case a: VList if a.lst.forall(_.isInstanceOf[VNum]) =>
        VDate.fromComponents(a.lst)
      case a: VList =>
        // If the list contains strings, join with spaces and parse
        VDate.parse(a.lst.map(StringHelpers.vyToString(_)).mkString(" "))
    },
    "#Z" ->
      direct(Monad) {
        VDate.setDefaultZone(pop().asInstanceOf[VStr].s)
      },
    addPart("#U", Monad, false) {
      case VStr(s) => VDuration.parse(s)
      case a: VNum => VDuration.ofDaysDecimal(a.toDouble)
    },
    addPart("∆<", Monad, true) {
      case a: VNum => a.arg
    },
    "kæ" -> niladify(NumberHelpers.probablePrimes),
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
    addPart("∆⌊", Triad, false) {
      case (a: VNum, b: VNum, c: VNum) => NumberHelpers.clamp(a, b, c)
      case (VListOf[VNum](a), b: VNum, c: VNum) =>
        a.map(n => NumberHelpers.clamp(n, b, c))
    },
    addPart(
      "∆p",
      Monad,
      true,
    ) { // I wish we had a vectorizing monad that didn't do anything useful to numbers
      case a: VNum => NumberHelpers.primeFactors(a)
    },
    addPart("∆P", Monad, true) { // One of these can be a digraph
      case a: VNum => NumberHelpers.primeFactors(a).distinct
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
    addPart("∆½", Monad, true) {
      case a: VNum =>
        val asRational = a.real.toRational
        if asRational.denominator.toLong != 0
        then // TODO: make a better rationality test. i'm not good enough at whatever branch of maths this is to figure it out.
          // I'm pretty sure you could scan the decimal places, it is only rational if it terminates or has a repeating series of digits.
          VList(
            Seq(
              VNum(asRational.numerator.toLong),
              VNum(asRational.denominator.toLong),
            )
          )
        else throw UserYikesException("Tried to convert irrational to rational")

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
    addPart(
      "∆-",
      Monad,
      true,
    ) { // because * does powers in the wrong order for -1* to work
      case a: VNum => -1 ** a
    },
    addPart("∆A", Monad, true) {
      case VListOf[VNum](numbers) => numbers.sum / numbers.length
      case a: VNum => a
    },
    addPart("∆G", Monad, true) {
      case VListOf[VNum](numbers) => numbers.product **
          (1 / VNum(numbers.length))
      case a: VNum => a
    },
    addPart("∆H", Monad, true) {
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
    "∆M" ->
      fullToImpl(
        Monad,
        x =>
          val iterable = x.itr
          if iterable.isEmpty then VList(Seq.empty)
          else iterable.vDistinct.maxBy(item => iterable.count(_ == item)),
      ),
    addPart("∆ℳ", Monad, true) {
      case VListOf[VNum](a) =>
        val length = a.length
        if length == 0 then VNum(0)
        else
          var initial = a.itr.headOption.getOrElse(VNum(0))
          for n <- a.tail.itr do initial = NumberHelpers.cantorPair(n, initial)
          NumberHelpers.cantorPair(VNum(length), initial)
      case a: VNum =>
        val (length, l): (VNum, VNum) = NumberHelpers.cantorUnpair(a)
        var listNums: VNum = l
        var resultList = List[VNum]()
        for i <- 1 until length.underlying.real.toDouble.toInt do
          val tup = NumberHelpers.cantorUnpair(listNums)
          listNums = tup(1)
          resultList = resultList :+ tup(0)
        resultList = resultList :+ listNums
        VList(resultList)
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
    addPart("øa", Monad, true) {
      case a: VNum =>
        "abcdefghijklmnopqrstuvwxyz".charAt((a % 26).toInt).toString
      case VStr(a) =>
        val inds = a
          .map(char => VNum("abcdefghijklmnopqrstuvwxyz".indexOf(char.toLower)))
        if inds.length == 1 then inds.head else VList(inds)
    },
    addPart("ø»", Triad, true) {
      case (VStr(s), len: VNum, VStr(padwith)) =>
        StringHelpers.padLeftWith(s, len, padwith)
      case (VStr(s), VStr(padwith), len: VNum) =>
        StringHelpers.padLeftWith(s, len, padwith)
    },
    addPart("ø«", Triad, false) {
      case (VStr(s), len: VNum, VStr(padwith)) =>
        StringHelpers.padRightWith(s, len, padwith)
      case (VStr(s), VStr(padwith), len: VNum) =>
        StringHelpers.padRightWith(s, len, padwith)
    },
    addPart("ø‹", Dyad, true) {
      case (a: VVal, b: VVal) =>
        StringHelpers.stripRight(a.toString(), b.toString())
    },
    addPart("ø›", Dyad, true) {
      case (a: VVal, b: VVal) =>
        StringHelpers.stripLeft(a.toString(), b.toString())
    },
    addPart("øS", Monad, true) {
      case VStr(a) => a.strip()
    },
    addPart("øR", Monad, true) {
      case VStr(a) => a.stripTrailing()
    },
    addPart("øL", Monad, true) {
      case VStr(a) => a.stripLeading()
    },
    addPart("øh", Dyad, true) {
      case (a: VVal, b: VVal) => a.toString().startsWith(b.toString())
    },
    addPart("øt", Dyad, true) {
      case (a: VVal, b: VVal) => a.toString().endsWith(b.toString())
    },
    addPart("øC", Monad, false) {
      case VListOf[VVal](lst) => StringHelpers.center(lst)
    },
    addPart("ø◲", Dyad, false) { // 1D surround
      case (VList(a), b) => VList((b +: a) :+ b)
      case (VStr(a), VStr(b)) => b + a + b
      case (a, VList(b)) => VList((a +: b) :+ a)
      case (a, b) => Seq(b, a, b)
    },
    addPart("ø⊠", Dyad, false) { // 2D surround
      case (VListOf[VList](lst), b: VPhysical) =>
        val inner = lst.map(i => VList((b +: i) :+ b))
        val outer = VList(Seq.fill(inner(0).length)(b))
        VList((outer +: inner) :+ outer)
    },
    addPart("ø⩔", Monad, true) {
      case n: VNum => Codepage(n.toInt).toString()
      case VStr(s) if s.length == 1 => Codepage.indexOf(s)
      case VStr(s) => VList(s.map(c => VNum(Codepage.indexOf(c))))
    },
    addPart("ø(", Monad, true) {
      case VStr(s) => "(" + s + ")"
      case n: VNum => "(" + n.toString() + ")"
    },
    addPart("ø[", Monad, true) {
      case VStr(s) => "[" + s + "]"
      case n: VNum => "[" + n.toString() + "]"
    },
    addPart("ø{", Monad, true) {
      case VStr(s) => "{" + s + "}"
      case n: VNum => "{" + n.toString() + "}"
    },
    addPart("ø<", Monad, true) {
      case VStr(s) => "<" + s + ">"
      case n: VNum => "<" + n.toString() + ">"
    },
    addPart("øe", Monad, true) {
      case s: VVal =>
        val groups = ListHelpers.groupConsecutive(s.toString().itr)
        val lengths = groups.map(g => VNum(g.itr.bigLength))
        groups.map(_.itr.headOption.getOrElse(VStr(""))).vzip(lengths)
    },
    addPart("ød", Monad, false) {
      case VListOf[VList](lst) => ListHelpers.runLengthDecode(lst)
    },
    addPart("øD", Dyad, false) {
      case (VListOf[VNum](a), VListOf[VStr](b)) =>
        ListHelpers.runLengthDecode(b.vzip(a).map(_.asInstanceOf[VList]))
      case (VListOf[VStr](a), VListOf[VNum](b)) =>
        ListHelpers.runLengthDecode(a.vzip(b).map(_.asInstanceOf[VList]))
      case (VListOf[VNum](a), VListOf[VNum](b)) => ListHelpers.runLengthDecode(
          a.map((x: VNum) => VStr(x.toString()))
            .vzip(b)
            .map(_.asInstanceOf[VList])
        )
    },
    "øE" ->
      direct(Monad) {
        val top = pop()
        top match
          case s: VVal =>
            val groups = ListHelpers.groupConsecutive(s.toString().itr)
            val lengths = groups.map(g => VNum(g.itr.bigLength))
            push(groups.map(_.itr.headOption.getOrElse(VStr(""))), lengths)
          case _ => throw UnsupportedOverloadException("øE", "List | Function")
      },
    addPart("øJ", Dyad, false) {
      case (VStr(a), VStr(b)) => VStr(
          StringHelpers
            .split(a, "\n")
            .map(_.toString())
            .zip(StringHelpers.split(b, "\n").map(_.toString()))
            .map((a, b) => s"$a$b")
            .mkString("\n")
        )

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
    addPart("Þe", Monad, false) {
      case a: VPhysical =>
        val iter = ListHelpers.makeIterable(a)
        VList(iter.vzip(NumberHelpers.range(0, iter.length - 1)))
    },
    addPart("ÞE", Monad, false) {
      case a: VPhysical =>
        val iter = ListHelpers.makeIterable(a)
        VList(iter.vzip(NumberHelpers.range(1, iter.length)))
    },
    addPart("ÞT", Monad, false) {
      case a: VFun => throw UnimplementedOverloadException("ÞT", List(a))
      case a => ListHelpers.transposeSafe(ListHelpers.makeIterable(a))
    },
    addPart("Þi", Dyad, true) {
      case (a, VList(b)) => ListHelpers.multiDimIndex(makeIterable(a), b)
    },
    addPart("ÞG", Dyad, false) {
      case (a, b: VNum) => ListHelpers.gridifyDim(a, b)
    },
    addPart("ÞṬ", Monad, false) {
      case VList(lst) => ListHelpers.multiDimTruthyIndices(lst)
    },
    addPart("Þ▲", Dyad, false) {
      case (a: VPhysical, b: VPhysical) =>
        a.itr.zip(b.itr).map((i, j) => if j.toBool then i else VNum(0))
      case (a: VPhysical, f: VFun) => // mask after applying
        val mask = f(a.itr)
        a.itr.zip(mask.itr).map((i, j) => if j.toBool then i else VNum(0))
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
    "Þ≡" ->
      direct(Monad) {
        val top = pop()
        top match
          case VList(lst) => push(ListHelpers.makeRectangle(lst))
          case _ =>
            val next = pop()
            (top, next) match
              case (v: VVal, VList(lst)) =>
                push(ListHelpers.makeRectangle(lst, v))
              case _ =>
                throw UnimplementedOverloadException("Þ≡", List(top, next))
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
    addPart("Þ⊞", Monad, false) {
      case VList(lst) => ListHelpers.itemDepth(lst)
    },
    addPart("ÞY", Triad, false) {
      case (a, b: VNum, c: VNum) => Seq.fill(c.toInt)(Seq.fill(b.toInt)(a))
    },
    addPart("Þ≤", Monad, false) {
      case a: VPhysical => ListHelpers.minimumIndices(a.itr)
    },
    addPart("Þ≥", Monad, false) {
      case a: VPhysical => ListHelpers.maximumIndices(a.itr)
    },
    addPart("Þ/", Monad, false) {
      case VList(lst) => ListHelpers.antiDiagonals(lst)
    },
    addPart("Þ\\", Monad, false) {
      case VList(lst) => ListHelpers.diagonals(lst)
    },
    "Þ„" ->
      direct(Monad) {
        val top = pop()
        top match
          case VList(lst) => push(ListHelpers.fromDiagonals(lst))
          case n: VNum =>
            val under = pop()
            under match
              case VList(lst) => push(ListHelpers.fromDiagonals(lst, Some(n)))
              case _ =>
                throw UnimplementedOverloadException("Þ„", List(under, n))
          case _ => throw UnimplementedOverloadException("Þ„", List(top))
      },
    "Þ”" ->
      direct(Monad) {
        val top = pop()
        top match
          case VList(lst) => push(ListHelpers.fromAntiDiagonals(lst))
          case n: VNum =>
            val under = pop()
            under match
              case VList(lst) =>
                push(ListHelpers.fromAntiDiagonals(lst, Some(n)))
              case _ =>
                throw UnimplementedOverloadException("Þ„", List(under, n))
          case _ => throw UnimplementedOverloadException("Þ„", List(top))
      },
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
    addPart("Þ⧖", Monad, false) {
      case a: VPhysical => ListHelpers.classify(a.itr)
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
    "#|apply-at-truthy" ->
      direct(Dyad) {
        val function = pop().asInstanceOf[VFun]
        val arg1 = pop()
        val arg2 = pop()
        var truthyList = VList(Seq(0))
        var argument: VPhysical = VNum(0)

        (arg1, arg2) match
          case (arg1: VList, arg2: VList) =>
            truthyList = arg1
            argument = arg2
          case (arg1: VPhysical, arg2: VList) =>
            truthyList = arg2
            argument = arg1
          case (arg1: VList, arg2: VPhysical) =>
            truthyList = arg1
            argument = arg2
          case arg => throw UnimplementedOverloadException(
              "#|apply-at-truthy",
              List(arg1, arg2),
            )

        val indices = ListHelpers.truthyIndices(truthyList)
        var temp = ListHelpers.makeIterable(argument)
        for index <- indices do
          temp = ListHelpers.augmentAssign(temp, index, function)
        if argument.isInstanceOf[VStr] then push(temp.mkString)
        else push(temp)

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

  /** Take no input and push a constant to the stack */
  private def constant(value: VAny): Element =
    Element(0, () => (ctx: Context) ?=> ctx.push(value))

  /** Take no input and push something to the stack based on Context */
  private def niladify(function: Context ?=> VAny): Element =
    Element(0, () => (ctx: Context) ?=> ctx.push(function))

  /** Take no input and do nothing with the stack */
  private def nop()(impl: Context ?=> Unit): Element = Element(0, () => impl)

  /** Add an element that handles all `VAny`s (it doesn't take a
    * `PartialFunction`, hence "Full")
    */
  private def fullToImpl[F](arity: ImplHelpers[?, F], impl: F): Element =
    Element(arity.arity, arity.toDirectFn(impl))

  private def fullToNullImpl[F](
      arity: NullImplHelpers[?, F],
      impl: F,
  ): Element = Element(arity.arity, arity.toDirectFn(impl))

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

  private def addNullPart[P, F](
      symbol: String,
      arity: NullImplHelpers[P, F],
  )(impl: P): (String, Element) =
    symbol ->
      Element(
        arity.arity,
        arity.toDirectFn(
          arity.fill(symbol)(impl)
        ),
      )

  private def direct[P, F](arity: ImplHelpers[P, F])(
      impl: Context ?=> Unit
  ): Element = Element(arity.arity, () => impl)

  private def direct(arity: Int)(impl: Context ?=> Unit): Element =
    Element(arity, () => impl)

end Elements
