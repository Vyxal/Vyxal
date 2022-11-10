package vyxal

object StringHelpers {

  /** Ring translates a given string according to the provided mapping
    * \- that is, map matching elements to the subsequent element in the
    * translation ring. The ring wraps around.
    */
  def ringTranslate(source: String, mapping: String): String = {
    val sourceIndices = source.map(mapping.indexOf(_))
    val translatedIndices =
      sourceIndices.map(i => (i + 1) % mapping.length)
    val translatedChars = translatedIndices.map(mapping.charAt(_))

    translatedChars.mkString
  }
}
