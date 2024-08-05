package vyxal

import vyxal.parsing.Lexer
import vyxal.Interpreter.executeFn
import vyxal.VNum.given

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
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

  def callWhileAndCollect(
      pred: VFun,
      transform: VFun,
      value: VAny,
  )(using ctx: Context): VList =
    val res = LazyList.unfold(value) { curr =>
      if pred(curr).toBool then
        val next = transform(curr)
        Some(next -> next)
      else None
    }
    VList.from(value #:: res)

  def collectUnique(function: VFun, initial: VAny)(using ctx: Context): VList =
    val prevVals = ArrayBuffer.empty[VAny]
    VList.from(
      initial +:
        LazyList.unfold(initial: VAny) { prevVal =>
          val next = function(prevVal)
          if prevVals.contains(next) then None
          else
            prevVals += next
            Some(next -> next)
        }
    )

  def compare(a: VAny, b: VAny)(using ctx: Context): Int =
    (a, b) match
      case (a: VNum, b: VNum) => a.compare(b)
      case (a: String, b: VNum) => a.compareTo(b.toString)
      case (a: VNum, b: String) => a.toString.compareTo(b)
      case (a: String, b: String) => a.compareTo(b)
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
      case _: String => ""
      case _: VList => 0
      case _ => throw NoDefaultException(a)

  def dyadicMaximum(a: VAny, b: VAny)(using Context): VAny =
    if a > b then a else b

  def dyadicMinimum(a: VAny, b: VAny)(using Context): VAny =
    if a < b then a else b

  def eval(s: String)(using ctx: Context): VAny =
    if VNum.NumRegex.matches(s) then VNum(s)
    else if s.matches("""("(?:[^"\\]|\\.)*["])""") then
      s.substring(1, s.length - 1)
    else if isList(s) then
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

  def getObjectMember(obj: VObject, name: String)(using ctx: Context): VAny =
    val (visibility, value) = obj.fields
      .getOrElse(name, throw FieldNotFoundException(obj.className, name))
    visibility match
      case Visibility.Public => value
      case Visibility.Restricted => value
      case Visibility.Private if ctx.privatable.contains(obj.className) => value
      case _ => throw AttemptedReadPrivateException(obj.className, name)

  val index: Dyad = Dyad.fill("index") {
    case (a: VList, b: VList) => a.index(b)
    case (a: String, b: VList) =>
      val temp = b.vmap(MiscHelpers.index(a, _))
      if b.lst.forall(_.isInstanceOf[VNum]) then temp.mkString
      else temp
    case (a: VList, b: String) =>
      val temp = a.vmap(MiscHelpers.index(_, b))
      if a.lst.forall(_.isInstanceOf[VNum]) then temp.mkString
      else temp
    case (a, b: VFun) => MiscHelpers.collectUnique(b, a)
    case (a: VFun, b) => MiscHelpers.collectUnique(a, b)
    case (a: VNum, b) => ListHelpers.makeIterable(b).index(a)
    case (a, b: VNum) => ListHelpers.makeIterable(a).index(b)
    case (a: String, b: String) =>
      val temp = a.length / 2
      a.slice(0, temp) + b + a.slice(temp, a.length)
    case (a: VObject, b: String) => MiscHelpers.getObjectMember(a, b)
    case (a: String, b: VObject) => MiscHelpers.getObjectMember(b, a)
  }

  def isList(code: String): Boolean =
    val stack = Stack[Char]()
    stack.pushAll(code)
    isList(stack)

  private def isList(stack: Stack[Char]): Boolean =
    if stack.isEmpty then return false
    else if stack.head == '[' then
      stack.pop()
      while stack.nonEmpty do
        // Skip whitespace
        if stack.head.isWhitespace then stack.pop()
        // Consume sublists
        else if stack.head == '[' then
          if !isList(stack) then return false
          // and make sure there's a comma after it
          if stack.head != ',' then return false
        else if stack.head == ']' then
          stack.pop()
          if stack.isEmpty then return true
          else return false
        else if stack.head == '"' then
          stack.pop()
          var escaped = false
          // Remove strings, making sure to skip escaped quotes
          // and that lists inside strings are ignored
          while stack.nonEmpty && (!escaped && stack.head != '"') do
            if stack.head == '\\' then escaped = !escaped
            else escaped = false
            stack.pop()

          // Make sure that the string is finished and that there's still
          // stuff after it
          if stack.isEmpty then return false
          // and that there's a comma after it
          if stack.head != ',' then return false
          stack.pop()
        else if stack.head == ',' then stack.pop()
        else
          stack.pop() // Sure there can be "invalid" list items here, but
          // those'll be handled by the parser. We just want to see if something
          // matches a list-like structure.
      end while
    end if

    false
  end isList

  val joinNothing: Monad = Monad.fill("joinNothing") {
    // ALTERNATIVE (No vectorisation):
    // case a: VList => a.mkString
    case a: VList =>
      if a.exists(_.isInstanceOf[VList]) then a.vmap(MiscHelpers.joinNothing)
      else a.mkString
    case n: VNum => n.vabs <= 1
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
      case _: String => "str"
      case _: VList => "lst"
      case _: VFun => "fun"
      case _: VConstructor => "con"
      case o: VObject => o.className
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
        var temp = if ctx.settings.limitPrint then lst.take(100) else lst
        while temp.nonEmpty do
          temp.head match
            case n: VNum => vyPrint(n)
            case s: String => vyPrint(StringHelpers.quotify(s))
            case l: VList => vyPrint(l)
            case f: VFun => vyPrint(executeFn(f))
            case c: VConstructor => vyPrint(c.toString)
            case o: VObject => vyPrint(o.toString)

          temp = temp.tail
          if temp.nonEmpty then vyPrint(", ")
        vyPrint("]")
      case f: VFun => vyPrint(executeFn(f))
      case _ => ctx.globals.printFn(StringHelpers.vyToString(x))

  def vyPrintln(x: VAny)(using Context): Unit =
    vyPrint(x)
    vyPrint("\n")

  def scanl(iterable: VList, function: VFun)(using ctx: Context): VList =
    if iterable.isEmpty then iterable
    else
      VList.from(
        iterable.tail.scanLeft(iterable.head)((lhs, rhs) => function(rhs, lhs))
      )

  val subtract: Dyad = Dyad.fill("subtract") {
    case (a: VNum, b: VNum) => a - b
    case (a: String, b: VNum) =>
      if b.toInt > 0 then a + "-" * b.toInt else "-" * b.toInt.abs + a
    case (a: VNum, b: String) =>
      if a.toInt > 0 then "-" * a.toInt + b else b + "-" * a.toInt.abs
    case (a: String, b: String) => a.replace(b, "")
  }

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
