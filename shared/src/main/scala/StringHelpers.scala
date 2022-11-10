package vyxal

object StringHelpers {
  def ringTranslate(source: String, mapping: String): String = {
    /* Ring translates a given string according to the provided mapping
    - that is, map matching elements to the subsequent element in the
  translation ring. The ring wraps around. */

    val sourceChars = source.toList
    val mappingChars = mapping.toList

    val sourceIndices = sourceChars.map(mappingChars.indexOf(_))
    val translatedIndices =
      sourceIndices.map((i: Int) => (i + 1) % mappingChars.length)
    val translatedChars = translatedIndices.map(mappingChars(_))

    translatedChars.mkString
  }
}
