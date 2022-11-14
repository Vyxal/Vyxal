package vyxal

object MiscHelpers {
  def boolify(x: VAny) = x match {
    case n: VNum => n != 0
    case s: String => s.nonEmpty
    case f: VFun => true
    case l: VList => l.nonEmpty
  }

  def compare(a: VAny, b: VAny): Int = (a, b) match {
    case (a: VNum, b: VNum)     => a.compare(b)
    case (a: String, b: VNum)   => a.compareTo(b.toString)
    case (a: VNum, b: String)   => a.toString.compareTo(b)
    case (a: String, b: String) => a.compareTo(b)
    case (a, b) => throw IllegalArgumentException(s"'$a' and '$b' can't be compared")
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
