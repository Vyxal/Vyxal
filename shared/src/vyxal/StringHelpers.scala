package vyxal

import vyxal.parsing.Lexer

import java.util.regex.PatternSyntaxException
import scala.collection.mutable.StringBuilder
import scala.util.matching.Regex

object StringHelpers:

  def chrord(c: VAny): VAny =
    (c: @unchecked) match
      case a: String =>
        if a.length == 1 then a.codePointAt(0) else VList(a.map(_.toInt: VNum)*)
      case a: VNum => a.toInt.toChar.toString
      case a: VList => VList(a.map(chrord)*)

  def compress252(s: String)(using Context): String =
    "[^a-z ]".r.findFirstIn(s) match
      case Some(str) => throw InvalidCompressionCharException(str.charAt(0))
      case _ =>
    val temp = NumberHelpers
      .fromBaseAlphabet(s, "ඞabcdefghijklmnopqrstuvwxyz ")
      .asInstanceOf[VNum]
    s"\"${NumberHelpers
        .toBaseAlphabet(
          temp,
          Lexer.Codepage.filterNot(Lexer.StringClosers.contains(_)),
        )
        .asInstanceOf[String]}„"

  def compress252(n: VNum)(using Context): String =
    s"\"${NumberHelpers
        .toBaseAlphabet(
          n,
          Lexer.Codepage.filterNot(Lexer.StringClosers.contains(_)),
        )
        .asInstanceOf[String]}“"

  // https://codegolf.stackexchange.com/a/151721/78850
  def compressDictionary(s: String): String =
    val endLength = 2 + Dictionary.longDictionary.map(_.length).max

    val shortInds = Dictionary.shortDictionary.zipWithIndex.toMap
    val longInds = Dictionary.longDictionary.zipWithIndex.toMap

    def character(z: BigInt, c: Char) =
      val o =
        if c.toInt == 10 then 95
        else if ' ' <= c && c <= '~' then c.toInt - 32
        else throw InvalidCompressionCharException(c)

      3 * (96 * z + o)

    def dictionary(z: BigInt, w: String, nonempty: Boolean): Option[BigInt] =
      var ts = nonempty
      var subW = w
      if w.head == ' ' then
        subW = w.substring(1)
        ts = !ts
      if subW.isEmpty then return None
      val useShort = subW.length < 6
      val dict = if useShort then shortInds else longInds
      val toggleCase = !dict.contains(subW)
      // If the word isn't in the dictionary, see if its lowercase/uppercase version is
      val ww =
        if toggleCase then swapCase(subW.head.toString) + subW.substring(1)
        else subW

      if !dict.contains(ww) then return None

      val j = if ts then if toggleCase then 2 else 1 else 0
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

  def decompress252Number(s: String)(using Context): VAny =
    NumberHelpers.fromBaseAlphabet(
      s,
      Lexer.Codepage.filterNot(Lexer.StringClosers.contains(_)),
    )

  def decompress252String(s: String)(using Context): VAny =
    val temp = NumberHelpers
      .fromBaseAlphabet(
        s,
        Lexer.Codepage.filterNot(Lexer.StringClosers.contains(_)),
      )
      .asInstanceOf[VNum]
    NumberHelpers.toBaseAlphabet(temp, "ඞabcdefghijklmnopqrstuvwxyz ")

  def escapeRegex(s: String): String =
    val specialChars = List(
      "\\",
      "^",
      "$",
      ".",
      "|",
      "?",
      "*",
      "+",
      "(",
      ")",
      "[",
      "]",
      "{",
      "}",
      "-",
    )
    s.map { c =>
      if specialChars.contains(c.toString) then "\\" + c else c.toString
    }.mkString
  end escapeRegex

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

  def intoNPieces(s: String, n: VNum)(using Context): VList =
    val chars = ListHelpers.makeIterable(s)
    val pieces = ListHelpers.intoNPieces(chars, n)
    VList.from(pieces.map(_.asInstanceOf[VList].mkString))

  def isAlphaNumeric(s: String): Boolean = s.matches("^[0-9A-Za-z]*$")

  def isVowel(c: Char): VNum = "aeiouAEIOU".contains(c)

  def padLeft(s: String, to: VNum): String =
    if to < 0 then padLeft(s, to.vabs)
    else s.reverse.padTo(to.toInt, ' ').reverse
  def padRight(s: String, to: VNum): String =
    if to < 0 then padRight(s, to.vabs)
    else s.padTo(to.toInt, ' ')

  def r(s: VAny): Regex =
    try s.toString.r
    catch case _: PatternSyntaxException => throw BadRegexException(s.toString)

  def regexSub(string: String, pattern: String, replacement: String): String =
    try string.replaceAll(pattern, replacement)
    catch case _: PatternSyntaxException => throw BadRegexException(pattern)

  def regexSub(string: String, pattern: String, function: VFun)(using
      Context
  ): String =
    try
      s"($pattern)".r.replaceAllIn(
        string,
        m => function(m.group(0)).toString,
      )
    catch case _: PatternSyntaxException => throw BadRegexException(pattern)

  /** Remove the character at the given index */
  def remove(s: String, i: Int): String =
    val wrapped = (i + s.length) % s.length
    s.substring(0, wrapped) + s.substring(wrapped + 1)

  /** Get the string representation of a value (opposite of eval) */
  def repr(v: VAny): String =
    v match
      case n: VNum => n.toString
      case s: String => quotify(s)
      case l: VList => l.map(repr).mkString("#[", ",", "#]")
      case f: VFun => "λ...}"
      // TODO make a strict mode in which it can throw
      // throw VyxalException(s"Cannot get repr for function: $f")

  /** Ring translates a given string according to the provided mapping \- that
    * is, map matching elements to the subsequent element in the translation
    * ring. The ring wraps around.
    */
  def ringTranslate(source: String, mapping: String): String =
    source.map { c =>
      val index = mapping.indexOf(c)
      if index == -1 then c else mapping((index + 1) % mapping.length)
    }.mkString

  def transliterate(source: String, from: VList, to: VList): String =
    val out = StringBuilder()
    val mappings =
      from.map(_.toString()).zip(to.map(_.toString())).sortBy(_._1.length)
    mappings.reverse

    var temp = source
    while temp.size > 0 do
      val (from, to) = mappings
        .find { case (f, _) => temp.startsWith(f) }
        .getOrElse(" " -> temp(0).toString())
      out.append(to)
      temp = temp.substring(from.length)
    out.toString()

  def transliterate(source: String, from: String, to: String): String =
    transliterate(
      source,
      VList.from(from.toList.map(_.toString)),
      VList.from(to.toList.map(_.toString)),
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
    val temp = s.replace("\\", raw"\\").replace("\"", "\\\"")

    s""""$temp""""

  def split(s: String | VNum, pattern: String)(using Context): VList =
    try
      s match
        case str: String => VList.from(str.split(pattern).toSeq)
        case num: VNum =>
          VList.from(num.toString.split(pattern).toSeq.map(MiscHelpers.eval))
    catch case _: PatternSyntaxException => throw BadRegexException(pattern)

  /** Toggle case of each character in the string */
  def swapCase(s: String): String =
    s.map { c =>
      if c.isUpper then c.toLower else if c.isLower then c.toUpper else c
    }.mkString

  /** Split on "words" (sequences of letters) and capitalize each word. */
  def titlecase(s: String): String =
    val splitOnWords = ListHelpers.groupConsecutiveBy(s.toSeq)(_.isLetter)
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

  def characterMultiply(n: VNum, s: String)(using Context): VAny =
    s.map(_.toString * n.toInt).mkString

  def caseof(s: String)(using Context): VList =
    VList.from(
      s.map(c =>
        if c.isUpper then VNum(1) // Uppercase
        else if c.isLower then VNum(0) // Lowercase
        else VNum(-1) // Non-alphabet
      )
    )

  def sentenceCase(str: String): String =
    var capitalise = true
    val res = StringBuilder()
    for c <- str do
      res += (if capitalise then c.toUpper else c.toLower)
      if "?!.".contains(c) then capitalise = true
      else if c != ' ' then capitalise = false
    res.toString

  def zeroPad(s: String, n: VNum)(using Context): String =
    val zeros = "0".repeat(
      MiscHelpers.dyadicMaximum(0, n.vabs - s.length()).asInstanceOf[VNum].toInt
    )
    if n > 0 then zeros + s else s + zeros

end StringHelpers
