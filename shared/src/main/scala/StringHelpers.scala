package vyxal

object StringHelpers {

  /** Ring translates a given string according to the provided mapping \- that
    * is, map matching elements to the subsequent element in the translation
    * ring. The ring wraps around.
    */
  def ringTranslate(source: String, mapping: String): String = {
    var result: String = ""
    for (c <- source) {
      val index = mapping.indexOf(c)
      if (index == -1) {
        result += c
      } else {
        result += mapping((index + 1) % mapping.length)
      }
    }
    result
  }
}
