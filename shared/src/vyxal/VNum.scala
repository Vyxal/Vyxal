package vyxal

import scala.language.implicitConversions

import scala.math.Ordered
import scala.util.matching.Regex

import spire.implicits.partialOrderOps // For <, >, etc.
import spire.math.{Complex, Real}

class VNum private (val underlying: Complex[Real]) extends Ordered[VNum]:
  def real: Real = underlying.real
  def imag: Real = underlying.imag

  def toInt: Int = underlying.toInt
  def toLong: Long = underlying.toLong
  def toBigInt: BigInt = underlying.real.toRational.toBigInt

  /** Whether the real part is small enough to be converted to an `Int` */
  def isValidInt: Boolean = underlying.real.isValidInt

  /** Round the real and imaginary parts */
  def toIntegral: VNum = underlying.round

  def floor: VNum = underlying.floor

  def unary_- : VNum = -underlying
  def +(rhs: VNum): VNum = underlying + rhs.underlying
  def -(rhs: VNum): VNum = underlying - rhs.underlying
  def *(rhs: VNum): VNum = VNum.complex(real * rhs.real, imag * rhs.imag)
  def /(rhs: VNum): VNum = VNum.complex(
    if rhs.real === 0 then 0 else real / rhs.real,
    if rhs.imag === 0 then 0 else imag / rhs.imag
  )
  def **(rhs: VNum): VNum = underlying ** rhs.underlying

  def %(rhs: VNum): VNum =
    // implement floating point floored modulus
    val q = this / rhs
    this - spire.math.floor(q.real) * rhs

  def vabs: VNum = underlying.abs

  /** Inclusive range */
  def to(end: VNum, step: VNum = 1): VList =
    VList.from(LazyList.unfold(this.floor) { n =>
      Option.when(n <= end)((n, n + step))
    })

  override def compare(that: VNum): Int =
    this.underlying.real.compare(that.underlying.real)

  override def toString =
    if this.imag == 0 then this.real.getString(Real.digits)
    else
      s"${this.real.getString(Real.digits)}ı${this.imag.getString(Real.digits)}"

  override def equals(obj: Any) = obj match
    case n: VNum =>
      (underlying `eq` n.underlying) ||
      ((this.real - n.real).abs < VNum.Epsilon && (this.imag - n.imag).abs < VNum.Epsilon)
    case _ => false
end VNum

/** Be sure to import `VNum.given` to be able to match on VNums and stuff */
object VNum:

  private val MaxRadix = 36

  private val Epsilon = Real(10) ** -9

  private val DecimalRegexStr =
    raw"(((0|[1-9][0-9]*)?\.[0-9]*|0|[1-9][0-9]*)_?)"

  val DecimalRegex: Regex = DecimalRegexStr.r

  val NumRegex: Regex =
    raw"-?($DecimalRegexStr?ı$DecimalRegexStr?)|-?$DecimalRegexStr".r

  /** To force an implicit conversion */
  def apply[T](n: T)(using Conversion[T, VNum]): VNum = n

  def complex(real: Real, imag: Real) = new VNum(Complex(real, imag))

  /** Parse a number from a string */
  def apply(s: String): VNum = apply(s, 10)

  /** Parse a number from a string in the given base */
  def apply(s: String, radix: Int): VNum =
    s.replaceAll("[^-0-9a-zA-Z.ı_]", "") match
      case s"${real}ı$imag" =>
        complex(
          parseDecimal(real, radix, 0),
          if imag.isEmpty then 1 else parseDecimal(imag, radix, 1)
        )
      case n => complex(parseDecimal(n, radix, 0), 0)

  /** Parse a real number that possibly has `.`s
    * @param default
    *   What to return if `component` is empty (not including minus sign)
    */
  private def parseDecimal(component: String, radix: Int, default: Int): Real =
    val neg = component.startsWith("-") || component.endsWith("_")
    val comp =
      if component.startsWith("-") then component.substring(1)
      else if component.endsWith("_") then component.init
      else component
    val sepInd = comp.indexOf('.')
    if comp.isEmpty then if neg then -default else default
    else if sepInd == -1 then
      val i = parseIntegral(comp, radix)
      if neg then -i else i
    else
      val integral: Real =
        if sepInd == 0 then 0
        else parseIntegral(comp.substring(0, sepInd), radix)
      val fracStr = comp.substring(sepInd + 1)
      val frac: Real =
        if sepInd == comp.length - 1 then 0.5
        else parseIntegral(fracStr, radix) / (Real(radix) ** fracStr.length)
      if neg then -integral - frac else integral + frac
  end parseDecimal

  /** Parse an integral number (no `.`). BigInt doesn't allow passing strings
    * with digits higher than the radix, so this method lets you do that.
    */
  private def parseIntegral(n: String, radix: Int): Real =
    n.foldLeft(0: BigInt) { (acc, c) =>
      acc * radix + Character.digit(c, MaxRadix)
    }

  /** Allow pattern matching like `VNum(r, i)` */
  def unapply(n: VNum): (Real, Real) = n.underlying.asTuple

  /** Implicit conversion to a VNum. Note that this needs to be imported first,
    * using `import vyxal.VNum.given`
    */
  given Conversion[Int, VNum] = n => complex(n, 0)
  given Conversion[Double, VNum] = n => complex(n, 0)
  given Conversion[Long, VNum] = n => complex(n, 0)
  given Conversion[BigInt, VNum] = n => complex(n, 0)
  given Conversion[BigDecimal, VNum] = n => complex(n, 0)
  given Conversion[Real, VNum] = n => complex(n, 0)
  given Conversion[Complex[Real], VNum] = new VNum(_)
  given Conversion[Boolean, VNum] = b => if b then 1 else 0
end VNum
