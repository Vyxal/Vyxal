package vyxal

object NumberHelpers {
  def range(a: VNum, b: VNum): VList = {
    val start = a.toInt
    val end = b.toInt
    val step = if (start < end) 1 else -1

    VList((start to end by step).map(VNum(_))*)
  }
}
