package vyxal

import scala.language.implicitConversions
import spire.math.{Complex, Real}

class VNum private (val underlying: Complex[Real]):
  def real = underlying.real
  def imag = underlying.imag

  def toInt = underlying.toInt
  def toLong = underlying.toLong

  /** Round the real and imaginary parts */
  def toIntegral = underlying.round

  def unary_- : VNum = -underlying
  def +(rhs: VNum): VNum = underlying + rhs.underlying
  def -(rhs: VNum): VNum = underlying - rhs.underlying
  def *(rhs: VNum): VNum = underlying * rhs.underlying
  def /(rhs: VNum): VNum = underlying / rhs.underlying
  def **(rhs: VNum): VNum = underlying ** rhs.underlying

  def %(rhs: VNum): VNum =
    Complex(this.real.tmod(rhs.real), this.imag.tmod(rhs.imag))

  override def toString =
    if this.imag == 0 then this.real.toString else this.underlying.toString

  override def equals(obj: Any) = obj match
    case n: VNum => underlying == n.underlying
    case _       => false
end VNum

/** Be sure to import `VNum.given` to be able to match on VNums and stuff */
object VNum:

  private val MaxRadix = 36

  /** To force an implicit conversion */
  def apply[T](n: T)(using Conversion[T, VNum]): VNum = n

  def complex(real: Real, imag: Real) = new VNum(Complex(real, imag))

  /** Parse a number from a string in the given base */
  def from(s: String, radix: Int = 10): VNum =
    s.replaceAll("[^0-9a-zA-Z.ı]", "") match
      case s"${real}ı$imag" =>
        complex(
          parseDecimal(real, radix),
          if imag.isEmpty then 1 else parseDecimal(imag, radix)
        )
      case n => complex(parseDecimal(n, radix), 0)

  /** Parse a real number that possibly has `.`s */
  def parseDecimal(comp: String, radix: Int): Real =
    val sepInd = comp.indexOf('.')
    if comp.isEmpty then 0
    else if sepInd == -1 then parseIntegral(comp, radix)
    else
      val integral: Real =
        if sepInd == 0 then 0
        else parseIntegral(comp.substring(0, sepInd), radix)
      val fracStr = comp.substring(sepInd + 1)
      val frac: Real =
        if sepInd == comp.length - 1 then 0.5
        else parseIntegral(fracStr, radix) / (Real(radix) ** fracStr.length)
      integral + frac

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
    * using `import VNum.given`
    */
  given Conversion[Int, VNum] = n => complex(n, 0)
  given Conversion[Double, VNum] = n => complex(n, 0)
  given Conversion[Long, VNum] = n => complex(n, 0)
  given Conversion[BigInt, VNum] = n => complex(n, 0)
  given Conversion[Real, VNum] = n => complex(n, 0)
  given Conversion[Complex[Real], VNum] = new VNum(_)
  given Conversion[Boolean, VNum] =
    b => if b then 1 else 0 // scalafix:ok DisableSyntax.BooleanToVNum
end VNum
