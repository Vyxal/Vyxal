package vyxal

import vyxal.conversions.{*, given}
import vyxal.parsing.{Codepage, Lexer}

import java.util.regex.PatternSyntaxException
import scala.annotation.tailrec
import scala.collection.mutable.StringBuilder
import scala.util.matching.Regex

object StringHelpers:
  def caseOf(c: String): VNum =
    if "ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(c) then VNum(1)
    else if "abcdefghijklmnopqrstuvwxyz".contains(c) then VNum(0)
    else VNum(-1)

  def chrord(c: VAny): VAny =
    (c: @unchecked) match
      case VStr(a) =>
        if a.length == 1 then VNum(a.codePointAt(0))
        else VList(a.map(_.toInt: VNum))
      case a: VNum => a.toInt.toChar.toString
      case a: VList => VList(a.map(chrord))

  def compress252(s: String)(using Context): String =
    "[^a-z ]".r.findFirstIn(s) match
      case Some(str) => throw InvalidCompressionCharException(str.charAt(0))
      case _ =>
    val temp = NumberHelpers
      .fromBaseAlphabet(s, "ඞabcdefghijklmnopqrstuvwxyz ")
      .asInstanceOf[VNum]
    val res = NumberHelpers.toBaseAlphabet(
      temp,
      Codepage.filterNot(Lexer.StringClosers.contains(_)),
    )
    s"\"$res„"

  def compress252(n: VNum)(using Context): String =
    if n < 0 then
      throw VyxalRuntimeException(
        s"Cannot compress negative number: $n"
      )
    val MAX_TWO_BYTE_COMPRESSABLE = 65535
    if n <= MAX_TWO_BYTE_COMPRESSABLE then
      val indices = NumberHelpers.toBase(n, 256)
      var res = indices
        .asInstanceOf[VList]
        .map(num => Codepage(num.asInstanceOf[VNum].toInt))
        .mkString
      if res.length == 1 then res = s"λ$res"
      return s"Ꮠ$res"
    val res = NumberHelpers.toBaseAlphabet(
      n,
      Codepage.filterNot(Lexer.StringClosers.contains(_)),
    )
    s"\"$res“"
  end compress252

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
        compressed.append(Codepage(c.toInt))
      compressed
        .toString()
        .replace(
          "\"",
          "ø",
        )

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
    @tailrec
    def helper(count: Int, start: Int): Int =
      haystack.indexOf(needle, start) match
        case -1 => count
        case ind => helper(count + 1, ind + needle.length)
    helper(0, 0)

  def decompress252Number(s: String)(using Context): VNum =
    NumberHelpers.fromBaseAlphabet(
      s,
      Codepage.filterNot(Lexer.StringClosers.contains(_)),
    )

  def decompress252String(s: String)(using Context): VAny =
    val temp = NumberHelpers
      .fromBaseAlphabet(
        s,
        Codepage.filterNot(Lexer.StringClosers.contains(_)),
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

  def intoNPieces(s: String, n: VNum)(using Context): Seq[String] =
    val chars = ListHelpers.makeIterable(s)
    val pieces = ListHelpers.intoNPieces(chars, n)
    pieces.map(_.mkString)

  def invertBrackets(s: String): String =
    s.map { c =>
      c match
        case '(' => ')'
        case ')' => '('
        case '[' => ']'
        case ']' => '['
        case '{' => '}'
        case '}' => '{'
        case '<' => '>'
        case '>' => '<'
        case _ => c
    }.mkString

  def isAlphaNumeric(s: String): Boolean = s.matches("^[0-9A-Za-z]*$")

  def isVowel(c: Char): VNum = "aeiouAEIOU".contains(c)

  def levenshtein(s1: String, s2: String): VNum =

    val len1 = s1.length
    val len2 = s2.length

    val dp = Array.ofDim[Int](len1 + 1, len2 + 1)

    for i <- 0 to len1 do dp(i)(0) = i
    for j <- 0 to len2 do dp(0)(j) = j

    for i <- 1 to len1 do
      for j <- 1 to len2 do
        val cost = if s1(i - 1) == s2(j - 1) then 0 else 1
        dp(i)(j) = List(
          dp(i - 1)(j) + 1,
          dp(i)(j - 1) + 1,
          dp(i - 1)(j - 1) + cost,
        ).min

    dp(len1)(len2)

  end levenshtein

  def padLeft(s: String, to: VNum): String =
    if to < 0 then padLeft(s, to.vabs)
    else s.reverse.padTo(to.toInt, ' ').reverse
  def padRight(s: String, to: VNum): String =
    if to < 0 then padRight(s, to.vabs)
    else s.padTo(to.toInt, ' ')

  def padLeftWith(s: String, endlen: VNum, padwith: String)(using
      Context
  ): String =
    if endlen < 0 then padRightWith(s, endlen.vabs, padwith)
    else
      val extra = endlen.toInt - s.length
      (s ++ (padwith * extra)).slice(0, endlen.toInt.max(s.length))

  def padRightWith(s: String, endlen: VNum, padwith: String)(using
      Context
  ): String =
    if endlen < 0 then padLeftWith(s, endlen.vabs, padwith)
    else
      val extra = endlen.toInt - s.length
      ((padwith * extra) ++ s).reverse
        .slice(0, endlen.toInt.max(s.length))
        .reverse

  def stripRight(s: String, r: String)(using Context): String =
    if r.isEmpty then return s
    else
      var tr = s
      while tr.endsWith(r) do tr = tr.dropRight(r.length())
      tr

  def stripLeft(s: String, r: String)(using Context): String =
    if r.isEmpty then return s
    else
      var tr = s
      while tr.startsWith(r) do tr = tr.drop(r.length())
      tr

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
      case VStr(s) => quotify(s)
      case l: VList => l.map(repr).mkString("#[", ",", "#]")
      case f: VFun => "λ...}"
      case c: VConstructor => "#$" + c
      case o: VObject => o.toString
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

  def transliterate(source: String, from: Seq[VAny], to: Seq[VAny]): String =
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
      from.toList.map(_.toString).vs,
      to.toList.map(_.toString).vs,
    )

  // https://github.com/DennisMitchell/jellylanguage/blob/70c9fd93ab009c05dc396f8cc091f72b212fb188/jelly/interpreter.py#L1055
  def decompress(compressed: String): String =
    val decompressed = StringBuilder()
    val comp = compressed.replace("ø", "\"").reverse
    var integer =
      comp.map(Codepage.indexOf(_) + 1).foldLeft(BigInt(0))(_ * 252 + _)

    while integer > 0 do
      val mode = integer % 3
      integer = integer / 3

      if mode == 0 then
        val code = integer % 96
        integer = integer / 96
        decompressed.append(Codepage(code.toInt + 32))
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

    decompressed.mkString.replace("◲", "\n")
  end decompress

  def quotify(s: String): String =
    val temp = s.replace("\\", raw"\\").replace("\"", "\\\"")

    s""""$temp""""

  def split(s: String | VNum, pattern: String)(using Context): Seq[VAny] =
    try
      s match
        case str: String => str.split(Regex.quote(pattern), -1).toSeq.vs
        case num: VNum =>
          num.toString.split(pattern).toSeq.map(MiscHelpers.eval)
    catch case _: PatternSyntaxException => throw BadRegexException(pattern)

  def splitKeepDelimiters(s: String, pattern: String): Seq[VAny] =
    pattern.r
      .split(s)
      .zipAll(pattern.r.findAllIn(s).toSeq, "", "")
      .flatMap {
        case (part, delimiter) => Seq(part, delimiter)
      }
      .filter(_.nonEmpty)
      .map(parts => VStr(parts.mkString))
      .toSeq

  /** Toggle case of each character in the string */
  def swapCase(s: String): String =
    s.map { c =>
      if c.isUpper then c.toLower else if c.isLower then c.toUpper else c
    }.mkString

  /** Split on "words" (sequences of letters) and capitalize each word. */
  def titlecase(s: String): String =
    val delimiter = "[^a-zA-Z]".r
    val splitOnWords = delimiter
      .split(s)
      .zipAll(delimiter.findAllIn(s).toSeq, "", "")
      .flatMap {
        case (part, delimiter) => Seq(part, delimiter)
      }
      .filter(_.nonEmpty) // Filter out any empty strings

    val words = splitOnWords.map(_.mkString)
    words.map { word =>
      s"${word.head.toUpper}${word.tail.toLowerCase}"
    }.mkString

  def vyToString(item: VAny)(using Context): String =
    item match
      case n: VNum => NumberHelpers.numToString(n)
      case VStr(s) => s
      case l: VList => l.map(vyToString).mkString("[", "|", "]")
      case f: VFun => f.toString
      case c: VConstructor => s"$c()"
      case o: VObject => o.toString
      case d: VDate => d.toString
      case d: VDuration => d.toString

  def prettyPrint(item: VAny)(using Context): String =
    def go(item: VAny, indentation: Int)(using Context): (String, Boolean) =
      item match
        case n: VNum => (NumberHelpers.numToString(n), false)
        case VStr(s) => (s, false)
        case f: VFun => (vyToString(f), false)
        case c: VConstructor => (c.toString, false)
        case l: VList =>
          if l.isEmpty then ("[]", false)
          else
            val (items, nested) = l.map(go(_, indentation + 1)).unzip
            val isNested = nested.exists(_ == true) ||
              items.mkString(", ").length > 80
            if isNested then
              (
                s"[\n${items.map("  ".repeat(indentation + 1) + _).mkString(",\n")}\n${"  ".repeat(indentation)}]",
                true,
              )
            else (s"[ ${items.mkString(", ")} ]", true)
        case o: VObject =>
          if o.fields.isEmpty then (s"${o.className} {}", false)
          else
            val (keys, values) = o.fields.unzip
            val (vs, nested) = values.map {
              case (vis, value) => go(value, indentation + 1)
            }.unzip
            val sigils = values.map(_._1.sigil)
            val entries = keys.zip(vs.zip(sigils)).map {
              case (key, (value, sigil)) => s"$sigil$key: $value"
            }
            val isNested = nested.exists(_ == true) ||
              entries.mkString(", ").length > 80
            if isNested then
              (
                s"${o.className} {\n${entries.map("  ".repeat(indentation + 1) + _).mkString(",\n")}${"  ".repeat(indentation)}\n}",
                true,
              )
            else (s"${o.className} { ${entries.mkString(", ")} }", true)
        case d: VDate => (d.toString, false)
        case d: VDuration => (d.toString, false)
    go(item, 0)._1
  end prettyPrint

  def characterMultiply(n: VNum, s: String)(using Context): VAny =
    s.map(_.toString * n.toInt).mkString

  def caseof(s: String)(using Context): Seq[VAny] =
    s.map(c =>
      if c.isUpper then VNum(1) // Uppercase
      else if c.isLower then VNum(0) // Lowercase
      else VNum(-1) // Non-alphabet
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
      MiscHelpers
        .dyadicMaximum(VNum(0), n.vabs - s.length())
        .asInstanceOf[VNum]
        .toInt
    )
    if n > 0 then zeros + s else s + zeros

  def extendString(long: String, short: String): String =
    val repeat: Int = (long.length.toFloat / short.length).ceil.toInt
    (short * repeat).slice(0, long.length)

  def extendString(len: VNum, str: String): String =
    val repeat: Int = (len.toDouble / str.length).ceil.toInt
    (str * repeat).slice(0, len.toInt)

  def center(lst: Seq[VVal])(using Context): String =
    val strs = lst.map(_.toString())
    val longest = strs.map(_.length.toFloat).max
    strs
      .map { s =>
        " " * ((longest - s.length) / 2).ceil.toInt + s
      }
      .mkString("\n")

  def hash(
      s: VAny,
      encoding: String = "UTF-8",
  ): Array[VNum] =
    s match
      case VStr(s) => binArraySha256(
          s.toString
            .getBytes(encoding)
            .flatMap(b => (7 to 0 by -1).map(i => (b >> i) & 1))
            .toArray
        ).grouped(8)
          .map((x: Array[Int]) =>
            VNum(
              x.zipWithIndex.map {
                case (item, index) => item << (7 - index)
              }.sum
            )
          )
          .toArray

  def hashBytes(a: VList): Array[VNum] =
    a match
      case VListOf[VNum](a) => binArraySha256(
          a.flatMap(b => (7 to 0 by -1).map(i => (b.toInt >> i) & 1)).toArray
        ).grouped(8)
          .map((x: Array[Int]) =>
            VNum(
              x.zipWithIndex
                .map { case (item, index) => item << (7 - index) }
                .sum
                .toByte
            )
          )
          .toArray

  def binArraySha256(a: Array[Int]): Array[Int] =
    def rightShift(x: Array[Int], a: Int): Array[Int] =
      Array.fill(a)(0) ++ x.dropRight(a)
    end rightShift
    def rightRotate(x: Array[Int], a: Int): Array[Int] =
      x.takeRight(a) ++ x.dropRight(a)
    end rightRotate
    def add2(a: Array[Int], b: Array[Int]): Array[Int] =
      var carry = 0
      var res = Array[Int]()
      val findCarry = Array(0, 0, 1, 1)
      (a.reverse lazyZip b.reverse).foreach { (x, y) =>
        val result = x + y + carry
        val digit = result % 2
        carry = result / 2
        res = digit +: res
      }
      res
    def add4(
        a: Array[Int],
        b: Array[Int],
        c: Array[Int],
        d: Array[Int],
    ): Array[Int] = add2(add2(a, b), add2(c, d))
    end add4
    def add5(
        a: Array[Int],
        b: Array[Int],
        c: Array[Int],
        d: Array[Int],
        e: Array[Int],
    ): Array[Int] = add2(add2(add2(a, b), add2(c, d)), e)
    end add5
    def choice(s: Array[Int], a: Array[Int], b: Array[Int]): Array[Int] =
      (s lazyZip a lazyZip b).map((x, y, z) => (x & y) | ((x ^ 1) & z))
    end choice
    def maj3(a: Array[Int], b: Array[Int], c: Array[Int]): Array[Int] =
      (a lazyZip b lazyZip c).map((x: Int, y: Int, z: Int) => (x + y + z) / 2)
    end maj3
    val K = Array(
      Array(0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1,
        1, 1, 0, 0, 1, 1, 0, 0, 0),
      Array(0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 1, 0, 0, 0, 1),
      Array(1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1,
        1, 1, 1, 0, 0, 1, 1, 1, 1),
      Array(1, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1,
        1, 1, 0, 1, 0, 0, 1, 0, 1),
      Array(0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 1,
        0, 0, 1, 0, 1, 1, 0, 1, 1),
      Array(0, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0,
        1, 1, 1, 1, 1, 0, 0, 0, 1),
      Array(1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1,
        0, 1, 0, 1, 0, 0, 1, 0, 0),
      Array(1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 1, 1, 1,
        0, 1, 1, 0, 1, 0, 1, 0, 1),
      Array(1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1,
        0, 1, 0, 0, 1, 1, 0, 0, 0),
      Array(0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1,
        1, 0, 0, 0, 0, 0, 0, 0, 1),
      Array(0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0,
        1, 1, 0, 1, 1, 1, 1, 1, 0),
      Array(0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0,
        1, 1, 1, 0, 0, 0, 0, 1, 1),
      Array(0, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0,
        1, 0, 1, 1, 1, 0, 1, 0, 0),
      Array(1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0,
        1, 1, 1, 1, 1, 1, 1, 1, 0),
      Array(1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1,
        0, 1, 0, 1, 0, 0, 1, 1, 1),
      Array(1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0,
        1, 0, 1, 1, 1, 0, 1, 0, 0),
      Array(1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0,
        1, 1, 1, 0, 0, 0, 0, 0, 1),
      Array(1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 1,
        1, 1, 0, 0, 0, 0, 1, 1, 0),
      Array(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0,
        1, 1, 1, 0, 0, 0, 1, 1, 0),
      Array(0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0,
        1, 1, 1, 0, 0, 1, 1, 0, 0),
      Array(0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0,
        0, 0, 1, 1, 0, 1, 1, 1, 1),
      Array(0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0,
        0, 1, 0, 1, 0, 1, 0, 1, 0),
      Array(0, 1, 0, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0,
        1, 1, 1, 0, 1, 1, 1, 0, 0),
      Array(0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0,
        0, 1, 1, 0, 1, 1, 0, 1, 0),
      Array(1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0,
        1, 0, 1, 0, 1, 0, 0, 1, 0),
      Array(1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1,
        0, 0, 1, 1, 0, 1, 1, 0, 1),
      Array(1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1,
        1, 1, 1, 0, 0, 1, 0, 0, 0),
      Array(1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 0, 0, 0, 1, 1, 1),
      Array(1, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1,
        1, 1, 1, 1, 1, 0, 0, 1, 1),
      Array(1, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0,
        1, 0, 1, 0, 0, 0, 1, 1, 1),
      Array(0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1,
        1, 0, 1, 0, 1, 0, 0, 0, 1),
      Array(0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0,
        1, 0, 1, 1, 0, 0, 1, 1, 1),
      Array(0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1,
        0, 1, 0, 0, 0, 0, 1, 0, 1),
      Array(0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0,
        1, 0, 0, 1, 1, 1, 0, 0, 0),
      Array(0, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1, 0,
        1, 1, 1, 1, 1, 1, 1, 0, 0),
      Array(0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0,
        1, 0, 0, 0, 1, 0, 0, 1, 1),
      Array(0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1,
        1, 0, 1, 0, 1, 0, 1, 0, 0),
      Array(0, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1,
        0, 1, 0, 1, 1, 1, 0, 1, 1),
      Array(1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0, 0,
        1, 0, 0, 1, 0, 1, 1, 1, 0),
      Array(1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0, 1),
      Array(1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0,
        0, 1, 0, 1, 0, 0, 0, 0, 1),
      Array(1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1,
        0, 0, 1, 0, 0, 1, 0, 1, 1),
      Array(1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1,
        1, 0, 1, 1, 1, 0, 0, 0, 0),
      Array(1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0,
        1, 1, 0, 1, 0, 0, 0, 1, 1),
      Array(1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0,
        0, 0, 0, 0, 1, 1, 0, 0, 1),
      Array(1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1,
        0, 0, 0, 1, 0, 0, 1, 0, 0),
      Array(1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0,
        1, 1, 0, 0, 0, 0, 1, 0, 1),
      Array(0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0,
        0, 0, 1, 1, 1, 0, 0, 0, 0),
      Array(0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0,
        1, 0, 0, 0, 1, 0, 1, 1, 0),
      Array(0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0,
        0, 0, 0, 0, 0, 1, 0, 0, 0),
      Array(0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1,
        1, 0, 1, 0, 0, 1, 1, 0, 0),
      Array(0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0,
        0, 1, 0, 1, 1, 0, 1, 0, 1),
      Array(0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0,
        0, 1, 0, 1, 1, 0, 0, 1, 1),
      Array(0, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1,
        0, 0, 1, 0, 0, 1, 0, 1, 0),
      Array(0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1,
        0, 0, 1, 0, 0, 1, 1, 1, 1),
      Array(0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1,
        1, 1, 1, 1, 1, 0, 0, 1, 1),
      Array(0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1,
        0, 1, 1, 1, 0, 1, 1, 1, 0),
      Array(0, 1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1,
        1, 0, 1, 1, 0, 1, 1, 1, 1),
      Array(1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0,
        0, 0, 0, 0, 1, 0, 1, 0, 0),
      Array(1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1,
        0, 0, 0, 0, 0, 1, 0, 0, 0),
      Array(1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 0, 1, 0),
      Array(1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0,
        0, 1, 1, 1, 0, 1, 0, 1, 1),
      Array(1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1,
        1, 1, 1, 1, 1, 0, 1, 1, 1),
      Array(1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0,
        0, 1, 1, 1, 1, 0, 0, 1, 0),
    )
    val message = a :+ 1
    val length = String
      .format("%64s", a.length.toBinaryString)
      .replace(" ", "0")
      .map(_.asDigit)
      .toArray
    val zeroes =
      Array.fill(512 - ((message.length + length.length - 1) % 512 + 1))(0)
    val block = Array.concat(message, zeroes, length)
    val blocks = block.grouped(512)
    var h0 = Array(0, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0,
      0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1)
    var h1 = Array(1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 0,
      1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1)
    var h2 = Array(0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1,
      0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1, 0)
    var h3 = Array(1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1,
      0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0)
    var h4 = Array(0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1,
      0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1)
    var h5 = Array(1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0,
      1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0)
    var h6 = Array(0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1,
      1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1)
    var h7 = Array(0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0,
      1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1)
    blocks.foreach { bl =>
      val schedule = bl.grouped(32).toArray ++ Array.fill(48)(Array.fill(32)(0))
      for i <- 0 until 48 do
        val s0 = schedule(i)
        val s1 = schedule(i + 1)
        val s9 = schedule(i + 9)
        val s14 = schedule(i + 14)
        val o0 =
          (rightRotate(s1, 7) lazyZip rightRotate(s1, 18) lazyZip
            rightShift(s1, 3)).map(_ ^ _ ^ _)
        val o1 =
          (rightRotate(s14, 17) lazyZip rightRotate(s14, 19) lazyZip
            rightShift(s14, 10)).map(_ ^ _ ^ _)
        schedule(i + 16) = add4(s0, o0, s9, o1)
      var a = h0
      var b = h1
      var c = h2
      var d = h3
      var e = h4
      var f = h5
      var g = h6
      var h = h7
      for (s, i) <- schedule.zipWithIndex do
        val E0 =
          (rightRotate(a, 2) lazyZip rightRotate(a, 13) lazyZip
            rightRotate(a, 22)).map(_ ^ _ ^ _)
        val E1 =
          (rightRotate(e, 6) lazyZip rightRotate(e, 11) lazyZip
            rightRotate(e, 25)).map(_ ^ _ ^ _)
        val T1 = add5(h, E1, choice(e, f, g), K(i), s)
        val T2 = add2(E0, maj3(a, b, c))
        h = g
        g = f
        f = e
        e = add2(d, T1)
        d = c
        c = b
        b = a
        a = add2(T1, T2)
      end for
      h0 = add2(h0, a)
      h1 = add2(h1, b)
      h2 = add2(h2, c)
      h3 = add2(h3, d)
      h4 = add2(h4, e)
      h5 = add2(h5, f)
      h6 = add2(h6, g)
      h7 = add2(h7, h)
    }
    Array.concat(h0, h1, h2, h3, h4, h5, h6, h7)
  end binArraySha256
end StringHelpers
