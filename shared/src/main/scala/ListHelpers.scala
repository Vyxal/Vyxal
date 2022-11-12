package vyxal

object ListHelpers {

  /** Make an iterable from a value
    *
    * @param value
    *   The value to make an iterable from
    * @param overrideRangify
    *   Whether to rangify (optional). If given, overrides
    *   `ctx.settings.rangify`
    * @return
    */
  def makeIterable(
      value: VAny,
      overrideRangify: Option[Boolean] = None
  )(using ctx: Context): VList =
    value match {
      case list: VList => list
      case str: String => VList(str.map(_.toString)*)
      case fn: VFun    => VList(fn)
      case num: VNum =>
        if (overrideRangify.getOrElse(ctx.settings.rangify)) {
          val start = ctx.settings.rangeStart
          val offset = ctx.settings.rangeOffset
          VList(
            start.toInt
              .to(num.toInt - offset.toInt)
              .map(VNum(_))*
          )
        } else {
          VList(num)
        }
    }
}
