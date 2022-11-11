package vyxal

object MiscHelpers {
  def boolify(x: VAny) = x match {
    case n: VNum => n != 0
    case s: String => s.nonEmpty
    case f: VFun => true
    case l: VList => l.nonEmpty
  }

  def vyPrint(x: VAny)(using Context): Unit = {
    print(x)
    // todo change later
  }

  def vyPrintln(x: VAny)(using Context): Unit = {
    vyPrint(x)
    vyPrint("\n")
  }
}
