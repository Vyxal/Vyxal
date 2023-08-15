package vyxal

import vyxal.parsing.Lexer
import vyxal.Interpreter.executeFn
import vyxal.VNum.given

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack

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

  def callWhile(pred: VFun, transform: VFun, value: VAny)(using Context): VAny =
    var curr = value
    while MiscHelpers.boolify(pred(curr)) do curr = transform(curr)
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
      result

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
    if VNum.NumRegex.matches(s) then VNum(s)
    else if s.matches(raw"""("(?:[^"\\]|\\.)*["])""") then s.substring(1).init
    else if Lexer.isList(s) then
      Lexer.lexLiterate(s) match
        case Right(tokens) =>
          val tempContext = Context(globals = Globals(settings = ctx.settings))
          Interpreter.execute(Lexer.sbcsify(tokens))(using tempContext)
          tempContext.peek
        case Left(err) => throw RuntimeException(s"Couldn't parse list: $err")
    else s

  def firstNonNegative(f: VFun)(using ctx: Context): Int =
    firstFromN(f, 0)

  /** A generalised "count up until the first positive integer is found that
    * satisfies a function". Helpful because you might want different hardcoded
    * offsets or even dynamic offsets.
    */
  def firstFromN(f: VFun, n: Int)(using ctx: Context): Int =
    var i = n
    while true do
      ctx.push(i)
      val result = executeFn(f)
      if boolify(result) then return i
      i += 1
    ???

  def firstPositive(f: VFun)(using ctx: Context): Int =
    firstFromN(f, 1)

  val joinNothing: Monad = Monad.fill("joinNothing") {
    case (a: VList) =>
      if a.exists(_.isInstanceOf[VList]) then a.vmap(MiscHelpers.joinNothing)
      else a.mkString
    case (a: VNum) => NumberHelpers.partitions(a)
    case (a: String) => ""
    case (a: VFun) => firstPositive(a)
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
      if boolify(res) then
        result += i
        count += 1
      i += 1
    VList.from(result.result())

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
      case l: VList =>
        value match
          case v: VList =>
            // make sure v is the same length as l by repeating items
            val v2 = l.indices.map(i => v(i % v.length))
            l.lazyZip(v2).foreach { (a, b) => unpackHelper(a, b) }
          case _ => unpackHelper(l, VList(value))
  end unpackHelper

  def vyPrint(x: VAny)(using ctx: Context): Unit =
    ctx.globals.printFn(StringHelpers.vyToString(x))

  def vyPrintln(x: VAny)(using Context): Unit =
    vyPrint(x)
    vyPrint("\n")
end MiscHelpers
