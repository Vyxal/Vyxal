package vyxal

import vyxal.*
import vyxal.impls.Elements

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.math

object NumberHelpers:

  def factors(a: VNum): VList =
    VList.from(
      VNum(1).toBigInt
        .to(a.toBigInt.abs)
        .filter(a % _ == VNum(0))
        .map(_ * a.toBigInt.signum)
    )

  def fromBase(a: VAny, b: VAny)(using ctx: Context): VAny =
    (a, b) match
      case (a: VNum, b: VNum)     => toInt(a.toString(), b.toInt)
      case (n: VNum, _)           => fromBase(b, a)
      case (a: String, b: String) => fromBaseAlphabet(a, b)
      case _ => fromBaseDigits(ListHelpers.makeIterable(a), b)

  /** Returns value in base 10 using base len(alphabet) [bijective base] */
  def fromBaseAlphabet(value: String, alphabet: String): VAny =
    value.foldLeft(VNum(0)) { (ret, digit) =>
      alphabet.size * ret + alphabet.indexOf(digit): VNum
    }

  /** Returns digits in base 10 using arbitrary base `base` */
  def fromBaseDigits(digits: VList, base: VAny)(using ctx: Context): VAny =
    digits.foldLeft(VNum(0): VAny) { (ret, digit) =>
      MiscHelpers.add(MiscHelpers.multiply(base, ret), digit)
    }

  def fromBinary(a: VAny)(using ctx: Context): VAny =
    a match
      case n: VNum   => fromBinary(n.toString())
      case l: VList  => toInt(l, 2)
      case s: String => toInt(s, 2)
      case _         => throw new Exception("Cannot convert to binary")

  def gamma(a: VNum): VNum =
    val colist = List(
      "57.156235665862923517",
      "-59.597960355475491248",
      "14.136097974741747174",
      "-0.49191381609762019978",
      "0.000033994649984811888699",
      "0.000046523628927048575665",
      "-0.000098374475304879564677",
      "0.00015808870322491248884",
      "-0.00021026444172410488319",
      "0.00021743961811521264320",
      "-0.00016431810653676389022",
      "0.000084418223983852743293",
      "-0.000026190838401581408670",
      "0.0000036899182659531622704"
    )

    val coefficents =
      colist.map(g =>
        VNum(g)
      ) // from http://www.mrob.com/pub/ries/lanczos-gamma.html

    val A_g = VNum("0.99999999999999709182") + coefficents.zipWithIndex
      .map((c, i) => c / ((a - 1) + (i + 1)))
      .reduce(_ + _)

    val g = VNum("4.7421875")
    val z = a - 1
    spire.math.sqrt(2 * math.Pi) * ((z + g + 0.5) ** (z + 0.5)) * spire.math
      .exp(
        -(z + g + 0.5).underlying.toDouble
      ) * A_g

  end gamma

  def multiplicity(a: VNum, b: VNum): VNum =
    if a == VNum(0) || b == VNum(0) then return VNum(0)
    if b.vabs == VNum(1) then return a.vabs
    var result = 0
    var current = a
    while current % b == VNum(0) do
      result += 1
      current /= b
    result

  def range(a: VNum, b: VNum): VList =
    val start = a.toInt
    val end = b.toInt
    val step = if start < end then 1 else -1

    VList((start to end by step).map(VNum(_))*)

  def toBinary(a: VAny)(using Context): VAny =
    a match
      case n: VNum =>
        val binary = n.toInt.abs.toBinaryString
        val temp = VList(binary.map(_.asDigit: VNum)*)
        if n.toInt < 0 then temp.vmap(v => -v.asInstanceOf[VNum])
        else temp
      case s: String =>
        // get binary representation of each character
        var result = ListBuffer.empty[VAny]
        for c <- s do
          val binary = c.toInt.toBinaryString
          result += VList(binary.map(_.asDigit).map(VNum(_)).toList*)
        VList(result.toList*)
      case _ => throw new Exception("Cannot convert to binary")

  def toBase(a: VAny, b: VAny)(using ctx: Context): VAny =
    (a, b) match
      case (a: VNum, b: VNum) =>
        if b.toBigInt == 0 then 0
        else toBaseDigits(a, b)
      case (n: VNum, _)  => toBaseAlphabet(n, ListHelpers.makeIterable(b))
      case (a: VList, _) => VList(a.map(toBase(_, b))*)
      case _ => throw new Exception("toBase only works on numbers and lists")

  /** Returns value in base len(alphabet) using base 10 [bijective base] */
  def toBaseAlphabet(value: VNum, alphabet: VIter)(using ctx: Context): VAny =
    val indexes = toBaseDigits(value, alphabet.iterLength)
    val alphalist = VList((alphabet match
      case a: String => a.toString.toList.map(_.toString)
      case l: VList  => l
    )*)

    val temp = indexes.map(alphalist.index(_).toString())
    alphabet match
      case a: String => temp.mkString("")
      case l: VList  => l

  def toBaseDigits(value: VNum, base: VNum): VList =
    if value == VNum(0) then VList(List(VNum(0))*)
    else if base.toBigInt == -1 then
      VList.from(
        (1 until value.toInt.abs * 2 - (if value.toBigInt > 0 then 0 else 1))
          .map(_ % 2)
      )
    else
      val sign = if value.toBigInt < 0 && base.toBigInt > 0 then -1 else 1
      var current = sign * value.toBigInt
      if base.toBigInt == 1 then VList(List.fill(current.toInt.abs)(sign)*)
      else
        val digits = ListBuffer[VNum]()
        while current > 0 do
          var digit = current % base.toBigInt
          current = current / base.toBigInt
          if digit < 0 then
            current += 1
            digit -= base.toBigInt
          VNum(digit * sign) +=: digits
        VList(digits.toList*)

  def toInt(value: VAny, radix: Int)(using ctx: Context): VAny =
    value match
      case n: VNum =>
        if radix != 10 then toInt(n.toIntegral.toString(), radix)
        else n.toIntegral
      case l: VList =>
        var res: VAny = VNum(0)
        var exponent = 0
        for i <- l.reverse do
          res = MiscHelpers.add(
            res,
            MiscHelpers.multiply(toInt(i, 10), VNum(radix) ** VNum(exponent))(
              using ctx
            )
          )
          exponent += 1
        res
      case s: String => VNum(s, radix).toIntegral
      case _         => throw new Exception("Cannot convert to int")
end NumberHelpers
