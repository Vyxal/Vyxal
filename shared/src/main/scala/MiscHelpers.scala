package vyxal

import vyxal.Interpreter.executeFn

object MiscHelpers {
  // todo consider doing something like APL's forks so this doesn't have to be a partial function
  val add: VyFn[2] = forkify("add") {
    case (a: VNum, b: VNum)     => a + b
    case (a: String, b: VNum)   => s"$a$b"
    case (a: VNum, b: String)   => s"$a$b"
    case (a: String, b: String) => s"$a$b"
  }

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

  def multiply(a: VAny, b: VAny)(using Context): VAny = (a, b) match {
    case (a: VNum, b: VNum)     => a * b
    case (a: String, b: VNum)   => a * b.toInt
    case (a: VNum, b: String)   => b * a.toInt
    case (a: String, b: String) => StringHelpers.ringTranslate(a, b)
    case (a: VFun, b: VNum)     => a.withArity(b.toInt)
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
