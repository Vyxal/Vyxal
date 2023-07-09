package vyxal

import vyxal.Interpreter.executeFn
import vyxal.Lexer.decimalRegex
import vyxal.VNum.given

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack

import spire.algebra.*

object MiscHelpers:
  val add = Dyad.vectorise("add")(forkify {
    case (a: VNum, b: VNum) => a + b
    case (a: String, b: VNum) => s"$a$b"
    case (a: VNum, b: String) => s"$a$b"
    case (a: String, b: String) => s"$a$b"
  })

  def boolify(x: VAny) = x match
    case n: VNum => n != VNum(0)
    case s: String => s.nonEmpty
    case f: VFun => true
    case l: VList => l.nonEmpty

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

  def compare(a: VVal, b: VVal): Int = (a, b) match
    case (a: VNum, b: VNum) => a.real.compare(b.real)
    case (a: String, b: VNum) => a.compareTo(b.toString)
    case (a: VNum, b: String) => a.toString.compareTo(b)
    case (a: String, b: String) => a.compareTo(b)

  def compareExact(
      a: VAny,
      b: VAny
  )(using ctx: Context): Int = (a, b) match
    case (a: VVal, b: VVal) => compare(a, b)
    case (a, b) =>
      // Lexographically compare the two values after converting both to iterable
      val aIter = ListHelpers.makeIterable(a)
      val bIter = ListHelpers.makeIterable(b)

      if aIter.length != bIter.length then
        return aIter.length.compare(bIter.length)

      var ind = 0
      var result = -1
      while ind < aIter.length && result != 0 do
        result = compareExact(aIter(ind), bIter(ind))
        ind += 1
      return result

  // Returns the default value for a given type
  def defaultEmpty(a: VAny): VAny =
    a match
      case _: VNum => VNum(0)
      case _: String => ""
      case _: VList => 0
      case _ => throw new Exception(s"Cannot get default value for $a")

  def dyadicMaximum(a: VVal, b: VVal): VVal =
    if compare(a, b) > 0 then a else b

  def dyadicMinimum(a: VVal, b: VVal): VVal =
    if compare(a, b) < 0 then a else b

  def eval(s: String)(using ctx: Context): VAny =
    if s.matches(raw"-?($decimalRegex?ı$decimalRegex?)|-?$decimalRegex") then
      VNum(s)
    else if s.matches(raw"""("(?:[^"\\]|\\.)*["])""") then s.substring(1).init
    else if LitLexer.isList(s) then
      LitLexer(s) match
        case Right(tokens) =>
          val tempContext = Context(globals = Globals(settings = ctx.settings))
          Interpreter.execute(LitLexer.sbcsify(tokens))(using tempContext)
          tempContext.peek
        case Left(err) => throw RuntimeException(s"Couldn't parse list: $err")
    else s

  def firstNonNegative(f: VFun)(using ctx: Context): Int =
    var i = 0
    while true do
      ctx.push(i)
      val result = executeFn(f)
      if boolify(result) then return i
      i += 1
    ???

  val multiply = Dyad.vectorise("multiply") {
    case (a: VNum, b: VNum) => a * b
    case (a: String, b: VNum) => a * b.toInt
    case (a: VNum, b: String) => b * a.toInt
    case (a: String, b: String) => StringHelpers.ringTranslate(a, b)
    case (a: VFun, b: VNum) => a.withArity(b.toInt)
  }

  def reduce(iter: VAny, by: VFun, init: Option[VAny] = None)(using
      ctx: Context
  ): VAny =
    var remaining = ListHelpers.makeIterable(iter, Some(true))(using ctx).toList

    // Convert niladic + monadic functions to be dyadic for reduction purposes
    val byFun = by.withArity(if by.arity < 2 then 2 else by.arity)

    // Take the first byFun.arity items as the initial set to operate on
    var operating = init match
      case Some(elem) => elem +: remaining.take(byFun.arity - 1)
      case None => remaining.take(byFun.arity)
    remaining = remaining.drop(operating.length)

    if operating.isEmpty then return 0
    if operating.length == 1 then return operating.head

    var current = operating(0)
    var previous = operating(1)

    while remaining.length + operating.length != 1 do
      val result = byFun.execute(previous, current, args = operating.reverse)
      previous = remaining.headOption.getOrElse(result)
      current = result
      operating = result +: remaining.take(byFun.arity - 1)
      remaining = remaining.drop(byFun.arity - 1)

    current
  end reduce

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
    val shapedValues = ListHelpers.makeIterable(ctx.pop())(using ctx)

    unpackHelper(unpackedNames, shapedValues)(using ctx)

  end unpack

  def unpackHelper(
      nameShape: VAny,
      value: VAny
  )(using ctx: Context): Unit =
    (nameShape: @unchecked) match
      case n: String =>
        ctx.setVar(n, value)
        VList(n, value)
      case l: VList =>
        value match
          case v: VList =>
            // make sure v is the same length as l by repeating items
            val v2 = ListBuffer[VAny]()
            for i <- 0 until l.length do v2 += v(i % v.length)
            l.zip(v2).map(x => unpackHelper(x(0), x(1))).toList
          case _ => unpackHelper(l, VList(value))
  end unpackHelper

  def vyPrint(x: VAny)(using ctx: Context): Unit =
    ctx.globals.printFn(StringHelpers.vyToString(x))

  def vyPrintln(x: VAny)(using Context): Unit =
    vyPrint(x)
    vyPrint("\n")
end MiscHelpers
