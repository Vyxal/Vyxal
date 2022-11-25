package vyxal

import vyxal.Interpreter.executeFn

object MiscHelpers {
  // todo consider doing something like APL's forks so this doesn't have to be a partial function
  val add: VyFn[2] = vect2(forkify("add") {
    case (a: VNum, b: VNum)     => a + b
    case (a: String, b: VNum)   => s"$a$b"
    case (a: VNum, b: String)   => s"$a$b"
    case (a: String, b: String) => s"$a$b"
  })

  def boolify(x: VAny) = x match {
    case n: VNum   => n != 0
    case s: String => s.nonEmpty
    case f: VFun   => true
    case l: VList  => l.nonEmpty
  }

  def compare(a: VAny, b: VAny): Int = (a, b) match {
    case (a: VNum, b: VNum)     => a.compare(b)
    case (a: String, b: VNum)   => a.compareTo(b.toString)
    case (a: VNum, b: String)   => a.toString.compareTo(b)
    case (a: String, b: String) => a.compareTo(b)
    case (a, b) =>
      throw IllegalArgumentException(s"'$a' and '$b' can't be compared")
  }

  def firstNonNegative(f: VFun)(using ctx: Context): Int = {
    var i = 0
    while (true) {
      ctx.push(i)
      val result = executeFn(f)
      if (boolify(result)) return i
      i += 1
    }
    ???
  }

  val multiply: VyFn[2] = vect2(
    FuncHelpers.errorIfUndefined(
      "multiply",
      {
        case (a: VNum, b: VNum)     => a * b
        case (a: String, b: VNum)   => a * b.toInt
        case (a: VNum, b: String)   => b * a.toInt
        case (a: String, b: String) => StringHelpers.ringTranslate(a, b)
        case (a: VFun, b: VNum)     => a.withArity(b.toInt)
      }
    )
  )

  def reduce(iter: VAny, by: VFun, init: Option[VAny] = None)(using
      ctx: Context
  ): VAny = {
    var remaining = ListHelpers.makeIterable(iter, Some(true))(using ctx).toList

    // Convert niladic + monadic functions to be dyadic for reduction purposes
    val byFun = by.withArity(if (by.arity < 2) 2 else by.arity)

    // Take the first byFun.arity items as the initial set to operate on
    var operating = init match {
      case Some(elem) => elem +: remaining.take(byFun.arity - 1)
      case None       => remaining.take(byFun.arity)
    }
    remaining = remaining.drop(operating.length)

    if (operating.length == 0) { return 0 }
    if (operating.length == 1) { return operating.head }

    var contextVarN = operating(0)
    var contextVarM = operating(1)

    while remaining.length + operating.length != 1 do {
      val result = byFun.execute(contextVarM, contextVarN, operating)
      contextVarM = remaining.headOption.getOrElse(result)
      contextVarN = result
      println(s"m: ${contextVarM}, n: ${contextVarN}")
      operating = result +: remaining.take(byFun.arity - 1)
      remaining = remaining.drop(byFun.arity - 1)
    }

    contextVarN
  }

  def vyPrint(x: VAny)(using ctx: Context): Unit = {
    // todo change later
    ctx.settings.printFn(x)
  }

  def vyPrintln(x: VAny)(using Context): Unit = {
    vyPrint(x)
    vyPrint("\n")
  }
}
