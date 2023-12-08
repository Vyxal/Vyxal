package vyxal

import vyxal.*
import vyxal.parsing.Lexer

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.math

import fastparse.internal.Lazy
import spire.*
import spire.math.Real
import spire.math.SafeLong
import spire.syntax.isReal.partialOrderOps // So we can compare Reals to stuff

object NumberHelpers:

  def factors(a: VNum): VList =
    VList.from(
      VNum(1).toBigInt
        .to(a.toBigInt.abs)
        .filter(a % _ == VNum(0))
        .map(_ * a.toBigInt.signum)
    )

  @tailrec
  def fromBase(a: VAny, b: VAny)(using ctx: Context): VAny =
    (a, b) match
      case (a: VNum, b: VNum) => toInt(a.toString(), b.toInt)
      case (n: VNum, _) => fromBase(b, a)
      case (a: String, b: String) => fromBaseAlphabet(a, b)
      case _ => fromBaseDigits(ListHelpers.makeIterable(a), b)

  /** Returns value in base 10 using base len(alphabet) [bijective base] */
  def fromBaseAlphabet(value: String, alphabet: String): VAny =
    value.foldLeft(VNum(0)) { (ret, digit) =>
      alphabet.length * ret + alphabet.indexOf(digit)
    }

  /** Returns digits in base 10 using arbitrary base `base` */
  def fromBaseDigits(digits: VList, base: VAny)(using ctx: Context): VAny =
    digits.foldLeft(0: VAny) { (ret, digit) => base *~ ret +~ digit }

  @tailrec
  def fromBinary(a: VAny)(using ctx: Context): VAny =
    a match
      case n: VNum => fromBinary(n.toString())
      case l: VList => toInt(l, 2)
      case s: String => toInt(s, 2)
      case arg => throw UnimplementedOverloadException("fromBinary", List(arg))

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
      "0.0000036899182659531622704",
    )

    val coefficents =
      colist.map(g =>
        VNum(g)
      ) // from http://www.mrob.com/pub/ries/lanczos-gamma.html

    val A_g = VNum("0.99999999999999709182") +
      coefficents.zipWithIndex
        .map((c, i) => c / ((a - 1) + (i + 1)))
        .reduce(_ + _)

    val g = VNum("4.7421875")
    val z = spire.math.abs(a.underlying.real) - 1

    val TWO_PI = spire.math.Real.pi * 2
    val ROOT_TWO_PI = TWO_PI ** VNum("0.5")

    val Z_G_HALF = z + g + VNum("0.5")

    val LHS = ROOT_TWO_PI * (Z_G_HALF ** (z + VNum("0.5")))
    val RHS = spire.math.Real.exp(-Z_G_HALF.underlying.real) * A_g

    LHS * RHS

  end gamma

  def gcd(a: VNum, b: VNum): VNum =
    if b == VNum(0) then a.vabs else gcd(b.vabs, a.vabs % b.vabs)

  def gcd(a: Seq[VAny]): VNum =
    a.foldLeft(VNum(0)) { (a, b) =>
      b match
        case b: VNum => gcd(a, b)
        case b => throw UnimplementedOverloadException("gcd", List(a, b))
    }

  def isMostLikelyPrime(a: VNum, k: VNum = 40): Boolean =
    if a < 2 || (a > 2 && a % 2 == VNum(0)) then return false
    if a == VNum(2) || a == VNum(3) then return true
    val s = a - 1
    try
      for _ <- 0 until k.toInt do
        val test = randrange(VNum(2), Some(a - 1))
        val modResult = modpow(test, s, a)
        if modResult != VNum(1) && modResult != a - 1 then
          throw VyxalYikesException(
            "this is scala being silly and not allowing me to return from a for loop because it isn't actually a for loop"
          )
    catch case _ => return false
    true

  def log(a: VNum, b: VNum): VNum =
    // Only works for real numbers for now
    VNum(
      spire.math.Real.log(a.underlying.real) /
        spire.math.Real.log(b.underlying.real)
    )

  def modpow(base: VNum, power: VNum, modulus: VNum): VNum =
    // https://en.wikipedia.org/wiki/Modular_exponentiation#Right-to-left_binary_method
    if modulus == VNum(1) then return VNum(0)
    var result = VNum(1)
    var current = base % modulus
    var currentPower = power
    while currentPower > 0 do
      if currentPower % VNum(2) == VNum(1) then
        result = (result * current) % modulus
      currentPower /= VNum(2)
      currentPower = currentPower.floor
      current = (current * current) % modulus
    result
  def multiplicity(a: VNum, b: VNum): VNum =
    if a == VNum(0) || b == VNum(0) then return VNum(0)
    if b.vabs == VNum(1) then return a.vabs
    var result = 0
    var current = a
    while current % b == VNum(0) do
      result += 1
      current /= b
    result

  def nChooseK(a: VNum, b: VNum): VNum =
    spire.math.fact(a.toLong) /
      (spire.math.fact(b.toLong) * spire.math.fact((a - b).toLong))

  /** A version of VNum.toString that differentiates between literate and sbcs
    * mode
    */
  def numToString(a: VNum)(using ctx: Context): String =
    if ctx.settings.literate then a.toString.replace("ı", "i")
    else
      a.toString
        .split("ı")
        .toSeq
        .map(x => if x.startsWith("-") then x.tail + "_" else x)
        .mkString("ı")

  def partitions(a: VNum): VList =
    // Return all ways to sum to a number
    val result = mutable.ListBuffer.empty[VList]
    def helper(current: VList, remaining: VNum, last: VNum): Unit =
      if remaining == VNum(0) then result += current
      else
        for i <- last.toBigInt to remaining.toBigInt do
          helper(VList.from(current :+ VNum(i)), remaining - i, i)
    helper(VList(), a, VNum(1))
    VList.from(result.toList)

  def probablePrimes: VList =
    VList.from(
      LazyList
        .unfold(VNum(2)) { n =>
          Some(n -> (n + 1))
        }
        .filter(isMostLikelyPrime(_))
    )

  def primeFactors(a: VNum): VList =
    val result = mutable.ListBuffer.empty[VNum]
    var current = a
    var i = VNum(2)
    while i <= current do
      if current % i == VNum(0) then
        result += i
        current /= i
      else i += 1
    VList.from(result.toList)

  // The exceptions for 'randrange' are very specific, so it just throws VyxalRuntimeExceptions
  def randrange(start: VNum, stop: Option[VNum] = None, step: VNum = 1): VNum =
    if stop.isEmpty then
      if step != VNum(1) then
        throw VyxalRuntimeException("Cannot have step without stop")
      if start > 0 then return randbelow(start)
      else throw VyxalRuntimeException("empty range for randrange()")

    val stopVal = stop.get
    val width = stopVal - start

    if step == VNum(1) then
      if width > 0 then return start + randbelow(width)
      else throw VyxalRuntimeException("empty range for randrange()")

    if step == VNum(0) then
      throw VyxalRuntimeException("step cannot be 0 in randrange()")
    val n =
      if step > 0 then (width + step - 1) / step else (width + step + 1) / step
    if n <= 0 then
      throw VyxalRuntimeException(
        s"empty range for randrange($start, $stopVal, $step)"
      )
    return start + step * randbelow(n)
  end randrange

  private def randbelow(n: VNum): VNum =
    val bitCount = n.toBigInt.bitLength
    var temp = getRandBits(bitCount)
    while temp >= n do temp = getRandBits(bitCount)
    temp

  private def getRandBits(n: VNum): VNum =
    val bits = ListBuffer.empty[VNum]
    for _ <- 0 until n.toInt do bits += VNum(scala.util.Random.nextInt(2))
    bits.foldLeft(VNum(0)) { (ret, digit) =>
      2 * ret + digit
    }

  def range(start: VNum, end: VNum): VList =
    val step = if start < end then 1 else -1
    start.to(end, step = step)

  def toBinary(a: VAny)(using Context): VList =
    a match
      case n: VNum =>
        val binary = n.toBigInt.abs.toString(2)
        val temp = VList(binary.map(_.asDigit: VNum)*)
        if n.toBigInt < 0 then temp.vmap(v => -v.asInstanceOf[VNum]) else temp
      case s: String =>
        // get binary representation of each character
        val result = ListBuffer.empty[VAny]
        for c <- s do
          val binary = c.toInt.toBinaryString
          result += VList(binary.map(_.asDigit).map(VNum(_)).toList*)
        VList(result.toList*)
      case arg => throw UnimplementedOverloadException("toBinary", List(arg))

  def toBase(a: VAny, b: VAny)(using ctx: Context): VAny =
    (a, b) match
      case (a: VNum, b: VNum) => VList.from(toBaseDigits(a, b))
      case (n: VNum, b: (String | VList)) => toBaseAlphabet(n, b)
      case (a: VList, _) => VList(a.map(toBase(_, b))*)
      case (a, b) => throw UnimplementedOverloadException("toBase", List(a, b))

  /** Returns value in base len(alphabet) using base 10 [bijective base]. If the
    * alphabet is a string, returns a string.
    */
  def toBaseAlphabet(value: VNum, alphabet: String | VList)(using
      Context
  ): VAny =
    val (isStr, length) = alphabet match
      case a: String => (true, a.length)
      case l: VList => (false, l.size)

    if length == 0 then return 0

    val indexes = toBaseDigits(value, length)
    val alphaList = ListHelpers.makeIterable(alphabet)

    val temp = indexes.map(alphaList.index(_).toString())

    if isStr then temp.mkString("") else VList.from(temp)

  def toBaseDigits(value: VNum, base: VNum): Seq[VNum] =
    /** Helper to get digits for single component of a VNum */
    def compToBase(valueComp: Real, baseComp: Real): Seq[Real] =
      val value = valueComp.floor
      val base = baseComp.floor
      if value == Real(0) then List(0)
      else if base == Real(0) then List(value)
      else if base == Real(1) then Seq.fill(value.toInt.abs)(value.signum)
      else if base == Real(-1) then
        Seq
          .fill(value.toInt.abs)(Seq[Real](1, 0))
          .flatten
          .dropRight(if value > 0 then 1 else 0)
      else
        List
          .unfold(value) { current =>
            Option.when(current != Real(0)) {
              val rem = current.tmod(base)
              val digit = if rem < 0 then rem + base.abs else rem
              val quot = (current - digit) / base
              (digit, quot)
            }
          }
          .reverse
      end if
    end compToBase
    val real = compToBase(value.real, base.real)
    val imag = compToBase(value.imag, base.imag)
    val realPadded =
      if real.size < imag.size then
        Seq.fill(imag.size - real.size)(Real(0)) ++ real
      else real
    val imagPadded =
      if imag.size < real.size then
        Seq.fill(real.size - imag.size)(Real(0)) ++ imag
      else imag
    realPadded.lazyZip(imagPadded).map(VNum.complex)
  end toBaseDigits

  def toBijectiveBase(value: VNum, radix: VNum)(using ctx: Context): VList =
    // It's okay that this doesn't work for complex numbers
    if value == VNum(0) then return VList()
    val base = radix.toBigInt.abs
    if base == 0 then return VList(value)
    if base == 1 then return VList.fill(value.toInt.abs)(1)
    val digits = ListBuffer.empty[VNum]
    var current = value
    while current != VNum(0) do
      current -= 1
      val digit = (current % radix) + 1
      digits += digit
      current /= radix
      current = current.floor
    VList(digits.reverse.toList*)

  def toBaseString(value: VNum, base: VNum)(using Context): VAny =
    val lst = NumberHelpers.toBaseDigits(value, base)
    val temp = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    val codepage = temp + Lexer.Codepage.filterNot(temp.contains(_))
    lst.map(d => codepage((d % 256).toInt)).mkString

  def toInt(value: VAny, radix: Int)(using Context): VAny =
    value match
      case n: VNum =>
        if radix != 10 then toInt(n.toIntegral.toString(), radix)
        else n.toIntegral
      case l: VList =>
        var res: VAny = VNum(0)
        var exponent = 0
        for i <- l.reverse do
          res = res +~ toInt(i, 10) *~ (VNum(radix) ** VNum(exponent))
          exponent += 1
        res
      case s: String => VNum(s, radix).toIntegral
      case _ =>
        throw UnimplementedOverloadException("toInt", List(value, radix))

  def divides(a: VAny, b: VAny)(using Context): VAny =
    (a, b) match
      case (a: VNum, b: VNum) => (a % b) == VNum(0)
      case (a: String, b: VNum) => a.toString + MiscHelpers.multiply(" ", b)
      case (a: VNum, b: String) => b.toString + MiscHelpers.multiply(" ", a)
      case (a: VList, b: VFun) => ListHelpers.dedupBy(a, b)
      case (a: VFun, b: VList) => ListHelpers.dedupBy(b, a)
      case (a: VList, b) => a.vmap(divides(_, b))
      case (a, b: VList) => b.vmap(divides(a, _))
      case _ => throw UnimplementedOverloadException("divides", List(a, b))

end NumberHelpers
