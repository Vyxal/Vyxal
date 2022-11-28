package vyxal

import vyxal.impls.Elements
import scala.collection.mutable.ListBuffer

object NumberHelpers {

  def fromBinary(a: VAny)(using ctx: Context): VAny = {
    a match {
      case n: VNum   => fromBinary(n.toString())
      case l: VList  => toint(l, 2)
      case s: String => toint(s, 2)
      case _         => throw new Exception("Cannot convert to binary")
    }
  }

  def multiplicity(a: VNum, b: VNum): VNum = {
    var result = 0
    var current = a.toInt
    while (current % b.toInt == 0) {
      result += 1
      current /= b.toInt
    }
    result
  }

  def range(a: VNum, b: VNum): VList = {
    val start = a.toInt
    val end = b.toInt
    val step = if (start < end) 1 else -1

    VList((start to end by step).map(VNum(_))*)
  }

  def toBinary(a: VAny): VAny = {
    a match {
      case n: VNum =>
        val binary = n.toInt.toBinaryString
        VList(binary.map(_.asDigit).map(VNum(_))*)
      case s: String =>
        // get binary representation of each character
        var result = ListBuffer.empty[VAny]
        for (c <- s) {
          val binary = c.toInt.toBinaryString
          result += VList(binary.map(_.asDigit).map(VNum(_)).toList*)
        }
        VList(result.toList*)
      case _ => throw new Exception("Cannot convert to binary")
    }
  }

  def toint(value: VAny, radix: Int)(using ctx: Context): VAny = {
    value match {
      case n: VNum => toint(n.toString(), radix)
      case l: VList => {
        var result: VAny = VNum(0)
        for (i <- l) {
          result = Elements.elements.get("×") match {
            case Some(elem) => {
              ctx.push(result)
              ctx.push(radix)
              elem.impl()(using ctx)
              ctx.pop()
            }
          }
          result = Elements.elements.get("+") match {
            case Some(elem) => {
              ctx.push(result)
              ctx.push(i)
              elem.impl()(using ctx)
              ctx.pop()
            }
          }
        }
        result
      }
      case s: String =>
        // Python has a built-in function for this, but Scala doesn't
        // so we have to do it ourselves. Uses the alphabet 0-9A-Z for
        // bases 2-36
        var result = 0
        for (character <- s) {
          result *= radix
          result += "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(
            character.toUpper
          )
        }
        result
      case _ => throw new Exception("Cannot convert to int")
    }
  }
}
