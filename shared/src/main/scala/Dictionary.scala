package vyxal
object Dictionary:
  var shortDictionary: Seq[String] = Seq()
  var longDictionary: Seq[String] = Seq()
  var initialised: Boolean = false

  def initialise(short: Seq[String], long: Seq[String]): Unit =
    shortDictionary = short
    longDictionary = long
    initialised = true
