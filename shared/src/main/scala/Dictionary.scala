package vyxal
object Dictionary:
  var shortDictionary: Seq[String] = Seq()
  var longDictionary: Seq[String] = Seq()
  var initalised: Boolean = false

  def initalise(short: Seq[String], long: Seq[String]): Unit =
    shortDictionary = short
    longDictionary = long
    initalised = true
