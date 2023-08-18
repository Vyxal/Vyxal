package vyxal

/** These will be set by [[JSVyxal.setShortDict]] and [[JSVyxal.setLongDict]] */
object Dictionary:
  var _shortDictionary: Seq[String] = null
  var _longDictionary: Seq[String] = null

  def shortDictionary: Seq[String] =
    if _shortDictionary == null then
      throw IllegalStateException("Short dictionary was not initialized")
    else _shortDictionary

  def longDictionary: Seq[String] =
    if _longDictionary == null then
      throw IllegalStateException("Long dictionary was not initialized")
    else _longDictionary
