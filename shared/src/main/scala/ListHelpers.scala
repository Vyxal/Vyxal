package vyxal

import collection.mutable.ArrayBuffer

import VNum.given

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
          VList(num.toString.map(x => VNum.from(x.toString))*)
        }
    }

  def map(f: VFun, to: VList)(using ctx: Context): VList = {
    VList(to.zipWithIndex.map { (item, index) =>
      f.execute(index, item, List(item))
    }*)
  }

  /** Mold a list into a shape.
    * @param content
    *   The list to mold.
    * @param shape
    *   The shape to mold the list into.
    * @return
    *   VyList The content, molded into the shape.
    */
  def mold(content: VList, shape: VList)(using ctx: Context): VList = {
    def moldHelper(content: VList, shape: VList, ind: Int): VList = {
      val output = ArrayBuffer.empty[VAny]
      val mutContent = content
      val mutShape = shape.toList
      var index = ind
      for item <- mutShape do {
        item match {
          case item: VList =>
            output += moldHelper(mutContent, item, index)
            output.last match {
              case list: VList => index += list.length - 1
              case _           => index += 1
            }
          case item: VAny => output += mutContent(index)
        }
        index += 1
      }

      VList(output.toSeq*)
    }
    moldHelper(content, shape, 0)
  }

  /** Split a list on a sublist
    *
    * @param sep
    *   The separator to split on
    * @return
    *   A Seq of all the sublists between occurrences of `sep`. If `sep` occurs
    *   at the very beginning of the list, the first element of the returned
    *   sequence will be an empty list. If `sep` occurs at the very end of the
    *   list, the last element of the returned sequence will be an empty list.
    */
  def split[T](list: Seq[T], sep: Seq[T]): Seq[Seq[T]] = {
    val parts = ArrayBuffer.empty[Seq[T]]

    var lastInd = 0
    var sliceInd = list.indexOfSlice(sep)

    while (sliceInd != -1) {
      parts += list.slice(lastInd, sliceInd)
      lastInd = sliceInd + sep.length
      sliceInd = list.indexOfSlice(sep, lastInd)
    }

    parts += list.slice(lastInd, list.length)

    parts.toSeq
  }
}
