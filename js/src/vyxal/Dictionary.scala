package vyxal

import org.scalajs.dom
import scala.concurrent.ExecutionContext.Implicits.global

/** Fetch the dictionaries from /ShortDictionary.txt and /LongDictionary.txt
  *
  * See documentation/DictionaryCompression.md for how they're generated.
  */
object Dictionary:
  private var shortFetched = FetchedDict("/ShortDictionary.txt")
  private var longFetched = FetchedDict("/LongDictionary.txt")

  def shortDictionary: Seq[String] = shortFetched.lines
  def longDictionary: Seq[String] = longFetched.lines

  /** Helper to fetch a dictionary asynchronously */
  private class FetchedDict(path: String):
    private var _lines: Seq[String] = null

    dom.console.debug(s"Fetching $path")
    for
      response <- dom.fetch(path).toFuture
      text <- response.text().toFuture
    do
      this._lines = text.split("\n").toSeq
      dom.console.debug(s"Fetched dictionary from $path")

    def lines: Seq[String] =
      // TODO find a way to await the response
      if _lines == null then
        throw new IllegalStateException("Dictionaries were not fetched")
      this._lines
  end FetchedDict
end Dictionary
