package vyxal

/** Must be duplicated across platforms because JS doesn't bundle resources */
object Dictionary:
  val shortDictionary: Seq[String] = readResource("/ShortDictionary.txt")
  val longDictionary: Seq[String] = readResource("/LongDictionary.txt")

  private def readResource(path: String) =
    io.Source
      .fromInputStream(getClass.getResourceAsStream(path))
      .getLines()
      .toSeq
end Dictionary
