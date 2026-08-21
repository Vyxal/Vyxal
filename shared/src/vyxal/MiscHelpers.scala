package vyxal

import vyxal.conversions.{*, given}
import vyxal.parsing.Lexer
import vyxal.Interpreter.executeFn

import java.time.format.DateTimeFormatter
import java.time.Duration as JDuration
import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack
import scala.math.Ordering.Implicits.infixOrderingOps
import scala.util.{Failure, Success, Try}

object MiscHelpers:
  val add = Dyad.vectorise("add")(forkify {
    case (a: VNum, b: VNum) => a + b
    case (VStr(a), b: VNum) => s"$a$b"
    case (a: VNum, VStr(b)) => s"$a$b"
    case (VStr(a), VStr(b)) => s"$a$b"
    case (a: VDate, b: VDuration) => VDate(a.dt.plus(b.dur))
    case (a: VDuration, b: VDate) => VDate(b.dt.plus(a.dur))
    case (a: VDuration, b: VDuration) => VDuration(a.dur.plus(b.dur))
    case (a: VDate, b: VNum) =>
      VDate(a.dt.plus(VDuration.ofDaysDecimal(b.toDouble).dur))
    case (a: VNum, b: VDate) =>
      VDate(b.dt.plus(VDuration.ofDaysDecimal(a.toDouble).dur))
    case (a: VStr, b: VDate) =>
      VDate(b.dt.plus(VDuration.parse(a.toString).dur))
    case (a: VDate, b: VStr) =>
      VDate(a.dt.plus(VDuration.parse(b.toString).dur))
  })

  def callWhile(pred: VFun, transform: VFun, value: VAny)(using Context): VAny =
    var curr = value
    while pred(curr).toBool do curr = transform(curr)
    curr

  def callWhileAndCollect(
      pred: VFun,
      transform: VFun,
      value: VAny,
  )(using ctx: Context): Seq[VAny] =
    val res = LazyList.unfold(value) { curr =>
      if pred(curr).toBool then
        val next = transform(curr)
        Some(next -> next)
      else None
    }
    value #:: res

  def collectUnique(function: VFun, initial: VAny)(using
      ctx: Context
  ): Seq[VAny] =
    val prevVals = ArrayBuffer.empty[VAny]

    initial +:
      LazyList.unfold(initial: VAny) { prevVal =>
        val next = function(prevVal)
        if prevVals.contains(next) then None
        else
          prevVals += next
          Some(next -> next)
      }

  def sleep(
      millis: Long
  ): Long = // TODO: Implement something that doesn't freeze the webpage completely to sleep
    val start = System.currentTimeMillis()
    val stop = start + millis
    while System.currentTimeMillis() < stop do {}
    System.currentTimeMillis() - start

  def compare(a: VAny, b: VAny)(using ctx: Context): Int =
    (a, b) match
      case (a: VNum, b: VNum) => a.compare(b)
      case (VStr(a), b: VNum) => a.compareTo(b.toString)
      case (a: VNum, VStr(b)) => a.toString.compareTo(b)
      case (VStr(a), VStr(b)) => a.compareTo(b)
      case (a: VDate, b: VDate) => a.compare(b)
      case (a: VDuration, b: VDuration) => a.compare(b)
      case (a, b) =>
        // Lexographically compare the two values after converting both to iterable
        val aIter = ListHelpers.makeIterable(a)
        val bIter = ListHelpers.makeIterable(b)

        if aIter.length != bIter.length then
          return aIter.length.compare(bIter.length)

        aIter
          .zip(bIter)
          .map { case (a, b) => compare(a, b) }
          .find(_ != 0)
          .getOrElse(0)

  // Returns the default value for a given type
  def defaultEmpty(a: VAny): VAny =
    a match
      case _: VNum => VNum(0)
      case VStr(_) => ""
      case _: VList => 0
      case _: VDuration => VDuration.Zero
      case _ => throw NoDefaultException(a)

  def dyadicMaximum(a: VAny, b: VAny)(using Context): VAny =
    if a > b then a else b

  def dyadicMinimum(a: VAny, b: VAny)(using Context): VAny =
    if a < b then a else b

  def eval(s: String)(using ctx: Context): VAny =
    if "^0+$".r.matches(s) then
      VNum(
        0
      ) // special case: if the number is made up of only 0s, it will get destroyed by the next line
    else if VNum.NumRegex.matches(StringHelpers.stripLeft(s, "0")) then VNum(s)
    else if s.matches("""("(?:[^"\\]|\\.)*["])""") then
      s.substring(1, s.length - 1)
    else if isList(s) then
      val tokens = Lexer.lexLiterate(s)
      val tempContext = Context(globals = Globals(settings = ctx.settings))
      tempContext.settings = tempContext.settings.useMode(EndPrintMode.None)
      Interpreter.execute(Lexer.sbcsify(tokens))(using tempContext)
      tempContext.peek
    else s

  def exec(value: VAny)(using ctx: Context): VAny =
    value match
      case VStr(code) =>
        val originalMode = ctx.settings.endPrintMode
        ctx.settings = ctx.settings.useMode(EndPrintMode.None)
        Interpreter.execute(code)(using ctx)
        ctx.settings = ctx.settings.useMode(originalMode)
        ctx.pop()
      case n: VNum => 10 ** n
      case list: VList => list.vmap(exec)
      case fn: VFun =>
        val res = Interpreter.executeFn(fn)
        if fn.arity == -1 then
          ctx.pop() // Handle the extra value pushed by lambdas that operate on the stack
        res
      case _: VObject => throw BadArgumentException("exec", "object")
      case con: VConstructor => Interpreter.createObject(con)
      case _: VDate => throw BadArgumentException("exec", "date")
      case _: VDuration => throw BadArgumentException("exec", "duration")
    end match
  end exec

  def validCode(value: VAny)(using ctx: Context): VAny =
    value match
      case VStr(a) =>
        val validCode = Try(Interpreter.execute(a.toString)(using ctx))
        validCode match
          case Success(str) => 1
          case Failure(ex) =>
            if ex.isInstanceOf[VyxalException] then 0
            else throw ex // idk how this might happen but you never know
      case _ => ??? // right now this is always a string
    end match

  /** A generalised "count up until the first positive integer is found that
    * satisfies a function". Helpful because you might want different hardcoded
    * offsets or even dynamic offsets.
    */
  @tailrec
  def firstFromN(f: VFun, n: Int)(using ctx: Context): Int =
    if f(n).toBool then n else firstFromN(f, n + 1)

  def firstNonNegative(f: VFun)(using Context): Int = firstFromN(f, 0)

  def firstPositive(f: VFun)(using Context): Int = firstFromN(f, 1)

  def getObjectMember(obj: VObject, name: String)(using ctx: Context): VAny =
    val (visibility, value) = obj.fields
      .getOrElse(name, throw FieldNotFoundException(obj.className, name))
    visibility match
      case Visibility.Public => value
      case Visibility.Restricted => value
      case Visibility.Private if ctx.privatable.contains(obj.className) => value
      case _ => throw AttemptedReadPrivateException(obj.className, name)

  val index: Dyad = Dyad.fill("index") {
    case (a: VList, b: VList) =>
      try a.index(b)
      catch
        case _: BadArgumentException =>
          try b.index(a)
          catch
            case _: BadArgumentException => throw BadArgumentException(
                "Index must be a number or list",
                Seq(a, b),
              )

    case (VStr(a), VList(b)) =>
      val temp = b.vmap(MiscHelpers.index(a, _))
      if b.forall(_.isInstanceOf[VNum]) then temp.mkString
      else temp
    case (VList(a), VStr(b)) =>
      val temp = a.vmap(MiscHelpers.index(_, b))
      if a.forall(_.isInstanceOf[VNum]) then temp.mkString
      else temp
    case (a, b: VFun) => MiscHelpers.collectUnique(b, a)
    case (a: VFun, b) => MiscHelpers.collectUnique(a, b)
    case (a: VNum, b) => ListHelpers.makeIterable(b).index(a)
    case (a, b: VNum) => ListHelpers.makeIterable(a).index(b)
    case (VStr(a), VStr(b)) =>
      val temp = a.length / 2
      a.slice(0, temp) + b + a.slice(temp, a.length)
    case (a: VObject, VStr(b)) => MiscHelpers.getObjectMember(a, b)
    case (VStr(a), b: VObject) => MiscHelpers.getObjectMember(b, a)
  }

  def isList(code: String): Boolean =

    if code.isEmpty then return false
    if code.head != '[' || code.last != ']' then return false

    val characters = Stack(code*)
    var depth = 0
    var inString = false
    var escaped = false
    var expectingComma = false

    while characters.nonEmpty do
      val char = characters.pop()
      // If in string, pop until we find the end of the string
      if inString then
        while characters.nonEmpty && (escaped || characters.head != '"') do
          escaped = char == '\\' && !escaped
          characters.pop()
        if characters.isEmpty then return false
        if characters.head != '"' then return false

        characters.pop()
        inString = false
        expectingComma = true
      else if char == '"' then inString = true
      else if expectingComma then
        if char == ',' then expectingComma = false
        else if char == ']' then
          depth -= 1
          expectingComma = depth > 0
        else return false
      else if char.isDigit || "+-.i".contains(char) then
        while characters.nonEmpty &&
          (characters.head.isDigit || "+-.i".contains(characters.head))
        do characters.pop()
        expectingComma = true
      else if char == '[' then depth += 1
      else if char == ']' then
        depth -= 1
        expectingComma = depth > 0
      end if
    end while
    depth == 0
  end isList

  val joinNothing: Monad = Monad.fill("joinNothing") {
    // ALTERNATIVE (No vectorisation):
    // case a: VList => a.mkString
    case a: VList =>
      if a.exists(_.isInstanceOf[VList]) then a.vmap(MiscHelpers.joinNothing)
      else a.mkString
    case n: VNum => n.vabs <= 1
    case VStr(s) => StringHelpers.isAlphaNumeric(s)
    case f: VFun => firstPositive(f)
  }

  val modulo: Dyad = Dyad.fill("modulo") {
    case (_: VNum, VNum(0, _)) => 0
    case (a: VNum, b: VNum) => a % b
    case (a: VList, b: VNum) => a.vmap(MiscHelpers.modulo(_, b))
    case (a: VNum, b: VList) => b.vmap(MiscHelpers.modulo(a, _))
    case (a: VList, b: VList) => a.zipWith(b)(MiscHelpers.modulo)
    case (a: VDate, VStr(b)) =>
      VStr(a.dt.format(DateTimeFormatter.ofPattern(b)))
    case (a: VDuration, b: VDuration) =>
      if b.dur.toMillis == 0 then VDuration.Zero
      else
        val millis = a.dur.toMillis % b.dur.toMillis
        VDuration(JDuration.ofMillis(millis))
    case (VStr(a), b: VList) => StringHelpers.formatString(a, b*)
    case (a: VList, VStr(b)) => StringHelpers.formatString(b, a*)
    case (VStr(a), b) => StringHelpers.formatString(a, b)
    case (a, VStr(b)) => StringHelpers.formatString(b, a)
  }

  val multiply = Dyad.vectorise("multiply") {
    case (a: VNum, b: VNum) => a * b
    case (VStr(a), b: VNum) => a * b.toInt
    case (a: VNum, VStr(b)) => b * a.toInt
    case (VStr(a), VStr(b)) => StringHelpers.ringTranslate(a, b)
    case (a: VFun, b: VNum) => a.withArity(b.toInt)
    case (a: VNum, b: VFun) => b.withArity(a.toInt)
    case (a: VDuration, b: VNum) => VDuration(a.dur.multipliedBy(b.toLong))
    case (a: VNum, b: VDuration) => VDuration(b.dur.multipliedBy(a.toLong))
  }

  def predicateSlice(predicate: VFun, limit: VNum, startFrom: VNum)(using
      ctx: Context
  ): Seq[VAny] =
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
    result.result()

  def setObjectMember(obj: VObject, name: String, value: VAny)(using
      ctx: Context
  ): VObject =
    val (visibility, _) = obj.fields
      .getOrElse(name, throw FieldNotFoundException(obj.className, name))

    val objName = obj.className
    val newPair = (name -> (visibility -> value))
    val fields = obj.fields

    visibility match
      case Visibility.Restricted if !ctx.privatable.contains(objName) =>
        throw AttemptedWriteRestrictedException(obj.className, name)
      case Visibility.Private if !ctx.privatable.contains(objName) =>
        throw AttemptedWritePrivateException(obj.className, name)
      case _ => ()

    VObject(obj.className, fields + newPair)
  end setObjectMember

  def typesOf(values: VAny*): List[String] =
    values.map {
      case _: VNum => "num"
      case VStr(_) => "str"
      case _: VList => "lst"
      case _: VFun => "fun"
      case _: VConstructor => "con"
      case o: VObject => o.className
      case _: VDate => "date"
      case _: VDuration => "dur"
    }.toList

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
          val temp = VList(nameStack.pop().toList)
          nameStack.top += temp
        nameStack.top += name
      depth = varDepth
    for i <- 0 until depth do
      val temp = VList(nameStack.pop().toList)
      nameStack.top += temp
    val unpackedNames = VList(nameStack.top.toList)
    val shapedValues = ListHelpers.makeIterable(ctx.pop())

    unpackHelper(unpackedNames, shapedValues)
  end unpack

  def unpackHelper(nameShape: VAny, value: VAny)(using ctx: Context): Unit =
    (nameShape: @unchecked) match
      case VStr(n) => ctx.setVar(n, value)
      case l: VList => value match
          case v: VList =>
            // make sure v is the same length as l by repeating items
            val v2 = l.indices.map(i => v(i % v.length))
            l.lazyZip(v2).foreach { (a, b) => unpackHelper(a, b) }
          case _ => unpackHelper(l, Seq(value))
  end unpackHelper

  def vyPrint(x: VAny, endOfProgram: Boolean = false)(using
      ctx: Context
  ): Unit =
    x match
      case VList(lst) =>
        ctx.globals.printFn("[")
        var temp = if ctx.settings.limitPrint then lst.take(100) else lst
        while temp.nonEmpty do
          temp.head match
            case n: VNum => vyPrint(n)
            case VStr(s) => vyPrint(StringHelpers.quotify(s))
            case l: VList => vyPrint(l)
            case f: VFun =>
              val res = executeFn(f)
              if !endOfProgram then vyPrint(res)
            case c: VConstructor => vyPrint(c.toString)
            case o: VObject => vyPrint(o.toString)
            case d: VDate => vyPrint(d.toString)
            case d: VDuration => vyPrint(d.toString)
          temp = temp.tail
          if temp.nonEmpty then vyPrint(", ")
        vyPrint("]")
      case f: VFun =>
        val res = executeFn(f)
        if !endOfProgram then vyPrint(res)
        else if !ctx.globals.printed then vyPrint(res)
      case _ => ctx.globals.printFn(StringHelpers.vyToString(x))

  def vyPrintln(x: VAny, endOfProgram: Boolean = false)(using
      ctx: Context
  ): Unit =
    vyPrint(x, endOfProgram)
    vyPrint("\n")

  def scanl(iterable: Seq[VAny], function: VFun)(using
      ctx: Context
  ): Seq[VAny] =
    if iterable.isEmpty then iterable
    else

      iterable.tail.scanLeft(iterable.head)((lhs, rhs) => function(rhs, lhs))

  val subtract: Dyad = Dyad.vectorise("subtract") {
    case (a: VNum, b: VNum) => a - b
    case (VStr(a), b: VNum) =>
      if b.toInt > 0 then a + "-" * b.toInt else "-" * b.toInt.abs + a
    case (a: VNum, VStr(b)) =>
      if a.toInt > 0 then "-" * a.toInt + b else b + "-" * a.toInt.abs
    case (VStr(a), VStr(b)) => a.replaceAll(b, "")
    case (a: VDate, b: VDuration) => VDate(a.dt.minus(b.dur))
    case (a: VDate, b: VDate) => VDuration(JDuration.between(b.dt, a.dt))
    case (a: VDuration, b: VDuration) => VDuration(a.dur.minus(b.dur))
    case (a: VDate, b: VNum) =>
      VDate(a.dt.minus(VDuration.ofDaysDecimal(b.toDouble).dur))
  }

  /** Generate a LazyList by repeatedly applying the given function to the given
    * initial value until there is no change, i.e., the last element is the
    * fixpoint. Includes the initial value.
    */
  def untilNoChange(function: VFun, initial: VAny)(using Context): Seq[VAny] =
    val res = LazyList.unfold(initial) { curr =>
      val next = function(curr)
      Option.when(next != curr)(next -> next)
    }
    initial #:: res

  def zipWith(left: Seq[VAny], right: Seq[VAny], function: VFun)(using
      Context
  ): Seq[VAny] = left.zipWith(right) { (a, b) => function(a, b) }

  def isTruthy(value: VAny): Boolean =
    value match
      case a: VNum => a != VNum(0)
      case VStr(a) => a.nonEmpty
      case d: VDate => d.toBool
      case dur: VDuration => dur.toBool
      case t: VTimer => t.toBool
      case wildcard => wildcard.toBool
end MiscHelpers
