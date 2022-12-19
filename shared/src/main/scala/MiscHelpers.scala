package vyxal

import vyxal.Interpreter.executeFn
import vyxal.VNum.given

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack
import spire.algebra.*

object MiscHelpers:
  // todo consider doing something like APL's forks so this doesn't have to be a partial function
  val add = vect2("add")(forkify {
    case (a: VNum, b: VNum)     => a + b
    case (a: String, b: VNum)   => s"$a$b"
    case (a: VNum, b: String)   => s"$a$b"
    case (a: String, b: String) => s"$a$b"
  })

  def boolify(x: VAny) = x match
    case n: VNum   => n != VNum(0)
    case s: String => s.nonEmpty
    case f: VFun   => true
    case l: VList  => l.nonEmpty

  def compare(a: VVal, b: VVal): Int = (a, b) match
    case (a: VNum, b: VNum)     => a.real.compare(b.real)
    case (a: String, b: VNum)   => a.compareTo(b.toString)
    case (a: VNum, b: String)   => a.toString.compareTo(b)
    case (a: String, b: String) => a.compareTo(b)

  def firstNonNegative(f: VFun)(using ctx: Context): Int =
    var i = 0
    while true do
      ctx.push(i)
      val result = executeFn(f)
      if boolify(result) then return i
      i += 1
    ???

  val multiply = vect2("multiply") {
    case (a: VNum, b: VNum)     => a * b
    case (a: String, b: VNum)   => a * b.toInt
    case (a: VNum, b: String)   => b * a.toInt
    case (a: String, b: String) => StringHelpers.ringTranslate(a, b)
    case (a: VFun, b: VNum)     => a.withArity(b.toInt)
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
      case None       => remaining.take(byFun.arity)
    remaining = remaining.drop(operating.length)

    if operating.length == 0 then return 0
    if operating.length == 1 then return operating.head

    var contextVarN = operating(0)
    var contextVarM = operating(1)

    while remaining.length + operating.length != 1 do
      val result = byFun.execute(contextVarM, contextVarN, operating.reverse)
      contextVarM = remaining.headOption.getOrElse(result)
      contextVarN = result
      operating = result +: remaining.take(byFun.arity - 1)
      remaining = remaining.drop(byFun.arity - 1)

    contextVarN
  end reduce

  def unpack(names: List[Tuple2[String, VNum]]): Unit =
    // String = variable name
    // VNum = depth inside ragged list

    val nameStack = Stack[ListBuffer[VAny]]()
    nameStack.push(ListBuffer[VAny]())
    var depth = 0

    for tup <- names do
      if depth == tup._2.toInt then nameStack.top += tup._1
      else if tup._2.toInt > depth then
        for i <- 0 until tup._2.toInt - depth do
          nameStack.push(ListBuffer[VAny]())
        nameStack.top += tup._1
      else if tup._2.toInt < depth then
        for i <- 0 until depth - tup._2.toInt do
          val temp = VList(nameStack.pop().toList*)
          nameStack.top += temp
        nameStack.top += tup._1
      depth = tup._2.toInt
    end for
    for i <- 0 until depth do
      val temp = VList(nameStack.pop().toList*)
      nameStack.top += temp
    end for
    println(nameStack.top.toList)
  end unpack

  def vyPrint(x: VAny)(using ctx: Context): Unit =
    // todo change later
    ctx.settings.printFn(x)

  def vyPrintln(x: VAny)(using Context): Unit =
    vyPrint(x)
    vyPrint("\n")
end MiscHelpers
