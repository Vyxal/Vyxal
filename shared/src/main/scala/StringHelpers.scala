package vyxal

object StringHelpers {

  def formatString(fmtstr: String, args: AnyRef*): String = {
    val sb = new collection.mutable.StringBuilder()
    var i = 0
    var j = 0
    while (i < fmtstr.length) {
      if (fmtstr(i) == '%') {
        if (i + 1 < fmtstr.length && fmtstr(i + 1) == '%') {
          sb.append('%')
          i += 2
        } else {
          sb.append(args(j % args.length))
          i += 1
        }
      } else {
        sb.append(fmtstr(i))
        i += 1
      }
    }
    sb.toString
  }

  /** Remove the character at the given index */
  def remove(s: String, i: Int): String = {
    val wrapped = (i + s.length) % s.length
    s.substring(0, wrapped) + s.substring(wrapped + 1)
  }

  /** Ring translates a given string according to the provided mapping \- that
    * is, map matching elements to the subsequent element in the translation
    * ring. The ring wraps around.
    */
  def ringTranslate(source: String, mapping: String): String =
    source.map { c =>
      val index = mapping.indexOf(c)
      if (index == -1) c
      else mapping((index + 1) % mapping.length)
    }.mkString
}
