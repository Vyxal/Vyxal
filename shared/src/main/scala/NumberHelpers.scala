package vyxal

import vyxal.*
import scala.collection.mutable.ListBuffer

object NumberHelpers:

  def fromBinary(a: VAny)(using ctx: Context): VAny =
    a match
      case n: VNum   => fromBinary(n.toString())
      case l: VList  => toInt(l, 2)
      case s: String => toInt(s, 2)
      case _         => throw new Exception("Cannot convert to binary")

  def multiplicity(a: VNum, b: VNum): VNum =
    var result = 0
    var current = a.toInt
    while current % b.toInt == 0 do
      result += 1
      current /= b.toInt
    result

  def range(a: VNum, b: VNum): VList =
    val start = a.toInt
    val end = b.toInt
    val step = if start < end then 1 else -1

    VList((start to end by step).map(VNum(_))*)

  def toBinary(a: VAny): VAny =
    a match
      case n: VNum =>
        val binary = n.toInt.toBinaryString
        VList(binary.map(_.asDigit: VNum)*)
      case s: String =>
        // get binary representation of each character
        var result = ListBuffer.empty[VAny]
        for c <- s do
          val binary = c.toInt.toBinaryString
          result += VList(binary.map(_.asDigit).map(VNum(_)).toList*)
        VList(result.toList*)
      case _ => throw new Exception("Cannot convert to binary")

  def toInt(value: VAny, radix: Int)(using ctx: Context): VAny =
    value match
      case n: VNum =>
        if radix != 10 then toInt(n.toIntegral.toString(), radix)
        else n.toIntegral
      case l: VList =>
        var res: VAny = VNum(0)
        var exponent = 0
        for i <- l.reverse do
          res = MiscHelpers.add(res, MiscHelpers.multiply(toInt(i, 10), VNum(radix) ** VNum(exponent))(using ctx))
          exponent += 1
        res
      case s: String => VNum(s, radix).toIntegral
      case _         => throw new Exception("Cannot convert to int")
end NumberHelpers
