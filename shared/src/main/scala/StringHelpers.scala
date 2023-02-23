package vyxal

import vyxal.dictionary.{LongDictionary, ShortDictionary}

import collection.mutable.StringBuilder
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

  def sss(compressed: String): String = return "Kid named finger"
end StringHelpers
