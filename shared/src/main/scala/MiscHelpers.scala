package vyxal

object MiscHelpers {
  def boolify(x: VAny) = x match {
    case n: VNum => n != 0
    case s: String => s.nonEmpty
    case f: VFun => true
    case l: VList => l.nonEmpty
  }
}
