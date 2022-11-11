package vyxal

object ListHelpers {
  def makeIterable(
      value: VAny,
      ctx: Context,
      rangify: Boolean = true
  ): VList =
    /** Make an iterable from a value
      * @param value
      *   The value to make an iterable from
      * @param ctx
      *   The context to use
      * @param rangify
      *   Whether to rangify the value if it's a number
      */

    value match {
      case list: VList => list
      case num: VNum =>
        if (rangify) {
          val start = ctx.settings.rangeStart
          val offset = ctx.settings.rangeOffset
          VList.fromSpecific(
            (start.toInt to (num.toInt - offset.toInt)).toList
              .map(VNum.apply(_))
          )
        } else {
          VList.fromSpecific(List(num))
        }

      case strval: String => VList.fromSpecific(strval.toList.map(_.toString))
      case _              => throw RuntimeException("Cannot iterate over value")
    }
}
