package vyxal

import collection.mutable.StringBuilder
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import spire.implicits.*
import VNum.given

object StringHelpers:

  def chrord(c: VAny): VAny =
    (c: @unchecked) match
      case a: String =>
        if a.length == 1 then a.codePointAt(0)
        else VList(a.map(_.toInt: VNum)*)
      case a: VNum  => a.toInt.toChar.toString
      case a: VList => VList(a.map(chrord)*)

  // https://codegolf.stackexchange.com/a/151721/78850
  def compressDictionary(s: String)(using ctx: Context): String =
    val endLength = 2 + ctx.globals.longDictionary.map(_.length).max

    def character(z: Int, c: Char) =
      val o =
        if c.toInt == 10 then 95
        else if ' ' <= c && c <= '~' then c.toInt - 32
        else throw new Exception(s"Invalid character $c")

      3 * 96 * z + o

    def dictionary(z: Int, w: String, nonempty: Boolean) =
      var ts = nonempty
      var subW = w
      if w.headOption.exists(_ == ' ') then
        subW = w.substring(1)
        ts = !ts
      val dictionary =
        if w.size < 6 then ctx.globals.shortDictionary
        else ctx.globals.longDictionary
      val swapcase = !dictionary.exists(_ == w)
      val word =
        if !swapcase then w
        else
          val first = w.charAt(0)
          val rest = w.substring(1)
          val firstToggled =
            if first.isUpper then first.toLower else first.toUpper
          firstToggled.toString + rest

      if !dictionary.contains(word) then
        throw new Exception(s"Invalid word $word")
      val f = ts || swapcase
      val j = if swapcase then 2 else 1
      val i = dictionary.indexOf(word)

      var res = dictionary.size * z + i
      if f then
        res = 3 * res + j
        res = 3 * res + 2
      else res = 3 * res + 1
      res
    end dictionary

    def go(z: Int) =
      val compressed = StringBuilder()
      var z1 = z
      while z1 != 0 do
        val c = z1 % 250
        z1 = z1 / 250
        compressed.append(CODEPAGE(c))
      compressed.toString.reverse

    val dp = Array.fill(s.length + 1)(0)
    // scala equivalent of for i in range(len(str) -1,-1,-1)
    for i <- (s.length - 1) to 0 by -1 do
      dp(i) = character(dp(i + 1), s(i))
      for j <- 1 to Math.min(endLength, s.length - i) do
        try
          dp(i) = Math.min(
            dp(i),
            dictionary(dp(i + j), s.substring(i, i + j), i != 0)
          )
        catch case _: Exception => () // todo (lyxal): is this necessary?

    s""""${go(dp(0))}”"""
  end compressDictionary

  def countString(haystack: String, needle: String): Int =
    haystack.split(needle, -1).length - 1

  def formatString(fmtstr: String, args: VAny*): String =
    val sb = StringBuilder()
    var i = 0
    var j = 0
    while i < fmtstr.length do
      if fmtstr(i) == '%' then
        if i + 1 < fmtstr.length && fmtstr(i + 1) == '%' then
          sb.append('%')
          i += 2
        else
          sb.append(args(j % args.length))
          j += 1
          i += 1
      else
        sb.append(fmtstr(i))
        i += 1
    sb.toString
  end formatString

  def isVowel(c: Char): VNum = "aeiouAEIOU".contains(c)

  /** Remove the character at the given index */
  def remove(s: String, i: Int): String =
    val wrapped = (i + s.length) % s.length
    s.substring(0, wrapped) + s.substring(wrapped + 1)

  /** Ring translates a given string according to the provided mapping \- that
    * is, map matching elements to the subsequent element in the translation
    * ring. The ring wraps around.
    */
  def ringTranslate(source: String, mapping: String): String =
    source.map { c =>
      val index = mapping.indexOf(c)
      if index == -1 then c
      else mapping((index + 1) % mapping.length)
    }.mkString

  // https://github.com/DennisMitchell/jellylanguage/blob/70c9fd93ab009c05dc396f8cc091f72b212fb188/jelly/interpreter.py#L1055
  def sss(compressed: String)(using ctx: Context): String =
    val decompressed = StringBuilder()
    var integer =
      NumberHelpers
        .fromBase(
          VList.from(compressed.map(CODEPAGE.indexOf(_) + 1: VNum)),
          250
        ) match
        case a: VNum => a.toInt
        case _       => throw new Exception("InternalError: SSS failed")

    while integer > 0 do
      val mode = integer % 3
      integer = integer / 3

      if mode == 0 then
        val code = integer % 96
        integer = integer / 96
        decompressed.append(CODEPAGE(code + 32))
      else
        var flagSwap = false
        var flagSpace = decompressed.isEmpty
        if mode == 2 then
          val flag = integer % 3
          integer = integer / 3
          flagSwap = flag != 1
          flagSpace = flagSpace ^ (flag != 0)
        val useShort = integer % 2 == 0
        integer = integer / 2
        val words =
          if useShort then ctx.globals.shortDictionary
          else ctx.globals.longDictionary
        val index = integer % words.length
        integer = integer / words.length
        var word = words(index)
        if flagSwap then
          word = (
            word.map(c => if c.isUpper then c.toLower else c.toUpper)
          )
        if flagSpace then word += " "
        decompressed.append(word)
      end if
    end while

    decompressed.mkString
  end sss
end StringHelpers
