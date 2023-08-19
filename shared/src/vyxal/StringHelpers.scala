package vyxal

import vyxal.parsing.Lexer

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.StringBuilder

object StringHelpers:

  def chrord(c: VAny): VAny =
    (c: @unchecked) match
      case a: String =>
        if a.length == 1 then a.codePointAt(0)
        else VList(a.map(_.toInt: VNum)*)
      case a: VNum => a.toInt.toChar.toString
      case a: VList => VList(a.map(chrord)*)

  // https://codegolf.stackexchange.com/a/151721/78850
  def compressDictionary(s: String): String =
    val endLength = 2 + Dictionary.longDictionary.map(_.length).max

    val shortInds = Dictionary.shortDictionary.zipWithIndex.toMap
    val longInds = Dictionary.longDictionary.zipWithIndex.toMap

    def character(z: BigInt, c: Char) =
      val o =
        if c.toInt == 10 then 95
        else if ' ' <= c && c <= '~' then c.toInt - 32
        else throw Exception(s"Invalid character $c")

      3 * (96 * z + o)

    def dictionary(z: BigInt, w: String, nonempty: Boolean): Option[BigInt] =
      var ts = nonempty
      var subW = w
      if w.head == ' ' then
        subW = w.substring(1)
        ts = !ts
      if subW.isEmpty then return None
      val useShort = subW.length < 6
      val dict =
        if useShort then shortInds
        else longInds
      val toggleCase = !dict.contains(subW)
      // If the word isn't in the dictionary, see if its lowercase/uppercase version is
      val ww =
        if toggleCase then swapCase(subW.head.toString) + subW.substring(1)
        else subW

      if !dict.contains(ww) then return None

      val j =
        if ts then if toggleCase then 2 else 1
        else 0
      val i = dict.getOrElse(ww, 0)

      var z1 = dict.keys.size * z + i
      z1 = 2 * z1
      if useShort then z1 += 1
      z1 *= 3
      if ts || toggleCase then
        z1 += j
        z1 = 3 * z1 + 2
      else z1 += 1
      Some(z1)

    end dictionary

    def go(z: BigInt) =
      val compressed = StringBuilder()
      var z1 = z
      while z1 != 0 do
        val c = (z1 - 1) % 252
        z1 = (z1 - 1) / 252
        compressed.append(Lexer.Codepage(c.toInt))
      compressed.toString
        .replace('"', '•')
        .replace('„', '≈')
        .replace('”', '¿')
        .replace('“', 'ꜝ')

    val dp = Array.fill(s.length + 1)(BigInt(0))
    // scala equivalent of for i in range(len(str) -1,-1,-1)
    for i <- (s.length - 1) to 0 by -1 do
      dp(i) = character(dp(i + 1), s(i))
      for j <- 1 to Math.min(endLength, s.length - i) do
        dictionary(dp(i + j), s.substring(i, i + j), i != 0).foreach { temp =>
          if temp < dp(i) then dp(i) = temp
        }

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

  def removeNonAlphabet(s: String): String =
    s.filter(_.isLetter)

  /** Get the string representation of a value (opposite of eval) */
  def repr(v: VAny): String =
    v match
      case n: VNum => n.toString
      case s: String => quotify(s)
      case l: VList => l.map(repr).mkString("#[", ",", "#]")
      case f: VFun => "λ...}"
      // TODO make a strict mode in which it can throw
      // throw IllegalArgumentException(s"Cannot get repr for function: $f")

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

  def transliterate(source: String, from: VList, to: VList): String =
    val out = StringBuilder()
    val mappings =
      from.map(_.toString()).zip(to.map(_.toString())).sortBy(_._1.length)
    mappings.reverse

    var temp = source
    while temp.size > 0 do
      val (from, to) =
        mappings
          .find { case (f, _) => temp.startsWith(f) }
          .getOrElse(" " -> temp(0).toString())
      out.append(to)
      temp = temp.substring(from.length)
    out.toString()

  def transliterate(source: String, from: String, to: String): String =
    transliterate(
      source,
      VList.from(from.toList.map(_.toString)),
      VList.from(to.toList.map(_.toString))
    )

  // https://github.com/DennisMitchell/jellylanguage/blob/70c9fd93ab009c05dc396f8cc091f72b212fb188/jelly/interpreter.py#L1055
  def decompress(compressed: String): String =
    val decompressed = StringBuilder()
    val comp = compressed
      .replace('•', '"')
      .replace('≈', '„')
      .replace('¿', '”')
      .replace('ꜝ', '“')
      .reverse
    var integer =
      comp.map(Lexer.Codepage.indexOf(_) + 1).foldLeft(BigInt(0))(_ * 252 + _)

    while integer > 0 do
      val mode = integer % 3
      integer = integer / 3

      if mode == 0 then
        val code = integer % 96
        integer = integer / 96
        decompressed.append(Lexer.Codepage(code.toInt + 32))
      else
        var flagSwap = false
        var flagSpace = decompressed.nonEmpty
        if mode == 2 then
          val flag = integer % 3
          integer = integer / 3
          flagSwap = flag != 1
          flagSpace = flagSpace != (flag != 0)
        val useShort = (integer % 2).toInt == 1
        integer = integer / 2
        val words =
          if useShort then Dictionary.shortDictionary
          else Dictionary.longDictionary
        val index = integer % words.length
        integer = integer / words.length
        var word = words(index.toInt)
        if flagSwap then word = swapCase(word.head.toString) + word.substring(1)
        if flagSpace then word = " " + word
        decompressed.append(word)
      end if
    end while

    decompressed.mkString.replace("¦", "\n")
  end decompress

  def quotify(s: String): String =
    val temp = s
      .replace("\\", raw"\\")
      .replace("\"", "\\\"")

    s""""$temp""""

  /** Toggle case of each character in the string */
  def swapCase(s: String): String =
    s.map { c =>
      if c.isUpper then c.toLower
      else if c.isLower then c.toUpper
      else c
    }.mkString

  /** Split on "words" (sequences of letters) and capitalize each word. */
  def titlecase(s: String): String =
    val splitOnWords =
      ListHelpers.groupConsecutiveBy(s.toSeq)(_.isLetter)
    val words = splitOnWords.map(_.mkString)
    words.map { word =>
      s"${word.head.toUpper}${word.tail.toLowerCase}"
    }.mkString

  def vyToString(item: VAny)(using Context): String =
    item match
      case n: VNum => NumberHelpers.numToString(n)
      case s: String => s
      case l: VList => l.map(vyToString).mkString("[", "|", "]")
      case f: VFun => vyToString(Interpreter.executeFn(f))

  def caseof(s: String): VList =
      VList.from(
        s.map(
          if _.toString.matches("[A-Z]") then 1    // Uppercase
          else
            if _.toString.matches("[a-z]") then 0  // Lowercase
            else -1                                // Non-alphabet
        )
      )
  
end StringHelpers
