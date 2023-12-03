package vyxal

import vyxal.parsing.Lexer
import vyxal.Interpreter.executeFn
import vyxal.VNum.given

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack

object MiscHelpers:
  val add = Dyad.vectorise("add")(forkify {
    case (a: VNum, b: VNum) => a + b
    case (a: String, b: VNum) => s"$a$b"
    case (a: VNum, b: String) => s"$a$b"
    case (a: String, b: String) => s"$a$b"
  })

  def callWhile(pred: VFun, transform: VFun, value: VAny)(using Context): VAny =
    var curr = value
    while pred(curr).toBool do curr = transform(curr)
    curr

  def collectUnique(function: VFun, initial: VAny)(using ctx: Context): VList =
    val seen = collection.mutable.Set.empty[VAny]
    val result = ListBuffer[VAny]()
    var current = initial
    while !seen.contains(current) do
      seen += current
      result += current
      current =
        executeFn(function, ctxVarPrimary = current, args = Seq(current))
    VList.from(result.toList)

  def compare(a: VAny, b: VAny)(using ctx: Context): Int =
    (a, b) match
      case (a: VNum, b: VNum) => a.real.compare(b.real)
      case (a: String, b: VNum) => a.compareTo(b.toString)
      case (a: VNum, b: String) => a.toString.compareTo(b)
      case (a: String, b: String) => a.compareTo(b)
      case (a, b) =>
        // Lexographically compare the two values after converting both to iterable
        val aIter = ListHelpers.makeIterable(a)
        val bIter = ListHelpers.makeIterable(b)

        if aIter.length != bIter.length then
          return aIter.length.compare(bIter.length)

        var ind = 0
        var result = -1
        while ind < aIter.length && result != 0 do
          result = compare(aIter(ind), bIter(ind))
          ind += 1
        result

  // Returns the default value for a given type
  def defaultEmpty(a: VAny): VAny =
    a match
      case _: VNum => VNum(0)
      case _: String => ""
      case _: VList => 0
      case _ => throw Exception(s"Cannot get default value for $a")

  def dyadicMaximum(a: VAny, b: VAny)(using Context): VAny =
    if a > b then a else b

  def dyadicMinimum(a: VAny, b: VAny)(using Context): VAny =
    if a < b then a else b

  def eval(s: String)(using ctx: Context): VAny =
    if VNum.NumRegex.matches(s) then VNum(s)
    else if s.matches("""("(?:[^"\\]|\\.)*["])""") then
      s.substring(1, s.length - 1)
    else if Lexer.isList(s) then
      val tokens = Lexer.lexLiterate(s)
      val tempContext = Context(globals = Globals(settings = ctx.settings))
      tempContext.settings = tempContext.settings.useMode(EndPrintMode.None)
      Interpreter.execute(Lexer.sbcsify(tokens))(using tempContext)
      tempContext.peek
    else s

  /** A generalised "count up until the first positive integer is found that
    * satisfies a function". Helpful because you might want different hardcoded
    * offsets or even dynamic offsets.
    */
  @tailrec
  def firstFromN(f: VFun, n: Int)(using ctx: Context): Int =
    if f(n).toBool then n else firstFromN(f, n + 1)

  def firstNonNegative(f: VFun)(using Context): Int = firstFromN(f, 0)

  def firstPositive(f: VFun)(using Context): Int = firstFromN(f, 1)

  val joinNothing: Monad = Monad.fill("joinNothing") {
    // ALTERNATIVE (No vectorisation):
    // case a: VList => a.mkString
    case a: VList =>
      if a.exists(_.isInstanceOf[VList]) then a.vmap(MiscHelpers.joinNothing)
      else a.mkString
    case n: VNum => n.toString
    case s: String => StringHelpers.isAlphaNumeric(s)
    case f: VFun => firstPositive(f)
  }

  val modulo: Dyad = Dyad.fill("modulo") {
    case (_: VNum, VNum(0, _)) => 0
    case (a: VNum, b: VNum) => a % b
    case (a: VList, b: VNum) => a.vmap(MiscHelpers.modulo(_, b))
    case (a: VNum, b: VList) => b.vmap(MiscHelpers.modulo(a, _))
    case (a: VList, b: VList) => a.zipWith(b)(MiscHelpers.modulo)
    case (a: String, b: VList) => StringHelpers.formatString(a, b*)
    case (a: VList, b: String) => StringHelpers.formatString(b, a*)
    case (a: String, b) => StringHelpers.formatString(a, b)
    case (a, b: String) => StringHelpers.formatString(b, a)
  }

  val multiply = Dyad.vectorise("multiply") {
    case (a: VNum, b: VNum) => a * b
    case (a: String, b: VNum) => a * b.toInt
    case (a: VNum, b: String) => b * a.toInt
    case (a: String, b: String) => StringHelpers.ringTranslate(a, b)
    case (a: VFun, b: VNum) => a.withArity(b.toInt)
    case (a: VNum, b: VFun) => b.withArity(a.toInt)
  }

  def predicateSlice(predicate: VFun, limit: VNum, startFrom: VNum)(using
      ctx: Context
  ): VList =
    var i = startFrom
    var count = VNum(0)
    val result = List.newBuilder[VAny]
    while count < limit do
      ctx.push(i)
      val res = executeFn(predicate)
      if res.toBool then
        result += i
        count += 1
      i += 1
    VList.from(result.result())

  /** For pattern-matching. Unpacks the top of the stack into some variables */
  def unpack(names: List[(String, Int)])(using ctx: Context): Unit =
    // String = variable name
    // Int = depth inside ragged list

    val nameStack = Stack[ListBuffer[VAny]]()
    nameStack.push(ListBuffer[VAny]())
    var depth = 0

    for (name, varDepth) <- names do
      if depth == varDepth then nameStack.top += name
      else if varDepth > depth then
        for i <- 0 until varDepth - depth do nameStack.push(ListBuffer[VAny]())
        nameStack.top += name
      else if varDepth < depth then
        for i <- 0 until depth - varDepth do
          val temp = VList(nameStack.pop().toList*)
          nameStack.top += temp
        nameStack.top += name
      depth = varDepth
    for i <- 0 until depth do
      val temp = VList(nameStack.pop().toList*)
      nameStack.top += temp
    val unpackedNames = VList(nameStack.top.toList*)
    val shapedValues = ListHelpers.makeIterable(ctx.pop())

    unpackHelper(unpackedNames, shapedValues)
  end unpack

  def unpackHelper(nameShape: VAny, value: VAny)(using ctx: Context): Unit =
    (nameShape: @unchecked) match
      case n: String => ctx.setVar(n, value)
      case l: VList => value match
          case v: VList =>
            // make sure v is the same length as l by repeating items
            val v2 = l.indices.map(i => v(i % v.length))
            l.lazyZip(v2).foreach { (a, b) => unpackHelper(a, b) }
          case _ => unpackHelper(l, VList(value))
  end unpackHelper

  def vyPrint(x: VAny)(using ctx: Context): Unit =
    x match
      case lst: VList =>
        ctx.globals.printFn("[")
        var temp = lst
        while temp.nonEmpty do
          temp.head match
            case n: VNum => vyPrint(n)
            case s: String => vyPrint(StringHelpers.quotify(s))
            case l: VList => vyPrint(l)
            case f: VFun => vyPrint(executeFn(f))

          temp = temp.tail
          if temp.nonEmpty then vyPrint(", ")
        vyPrint("]")
      case _ => ctx.globals.printFn(StringHelpers.vyToString(x))

  def vyPrintln(x: VAny)(using Context): Unit =
    vyPrint(x)
    vyPrint("\n")

  def scanl(iterable: VList, function: VFun)(using ctx: Context): VList =
    if iterable.isEmpty then iterable
    else VList.from(iterable.tail.scanLeft(iterable.head)(function(_, _)))

  def subtract(a: VAny, b: VAny): VAny =
    (a, b) match
      case (a: VNum, b: VNum) => a - b
      case (a: String, b: VNum) =>
        if b.toInt > 0 then a + "-" * b.toInt else "-" * b.toInt.abs + a
      case (a: VNum, b: String) =>
        if a.toInt > 0 then "-" * a.toInt + b else b + "-" * a.toInt.abs
      case (a: String, b: String) => a.replace(b, "")
      case (a: VList, b: (VNum | String)) => VList.from(a.map(subtract(_, b)))
      case (a: (VNum | String), b: VList) => VList.from(b.map(subtract(a, _)))
      case (a: VList, b: VList) => VList.from((a, b).zipped.map(subtract(_, _)))
      case (a, b) => ???

  def untilNoChange(function: VFun, value: VAny)(using Context): VList =
    var prev = value
    val res = LazyList.unfold(value) { curr =>
      val next = function(curr)
      if next == prev then None
      else
        prev = next
        Some(next -> next)
    }
    VList.from(value #:: res)

  def zipWith(left: VList, right: VList, function: VFun)(using Context): VList =
    left.zipWith(right) { (a, b) => function(a, b) }

end MiscHelpers
