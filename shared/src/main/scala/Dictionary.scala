package vyxal

private[vyxal] object Dictionary:
  var shortDictionary: Seq[String] = Seq()
  var longDictionary: Seq[String] = Seq()
  var initialised: Boolean = false

  private val ShortDictionaryFile = "/ShortDictionary.txt"
  private val LongDictionaryFile = "/LongDictionary.txt"
  private def readResource(path: String) =
    io.Source
      .fromInputStream(getClass.getResourceAsStream(path))
      .getLines()
      .toSeq

  def manualInitialise(short: Seq[String], long: Seq[String]): Unit =
    shortDictionary = short
    longDictionary = long
    initialised = true

  def fileInitialise(): Unit =
    shortDictionary = readResource(ShortDictionaryFile)
    longDictionary = readResource(LongDictionaryFile)
    initialised = true
end Dictionary
