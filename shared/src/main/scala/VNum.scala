package vyxal

import scala.language.implicitConversions
import spire.math.{Complex, Real}

class VNum private (val underlying: Complex[Real]):
  def real = underlying.real
  def imag = underlying.imag

  def toInt = underlying.toInt
  def toLong = underlying.toLong

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

  /** To force an implicit conversion */
  def apply(n: VNum): VNum = n

  def complex(real: Real, imag: Real) = new VNum(Complex(real, imag))

  // todo implement properly
  /** Parse a number from a string */
  def from(s: String): VNum =
    // Not as simple as it seems - can't just use Number.parse
    // because it doesn't handle hanging decimals (3. -> 3.5) nor
    // complex numbers (3ı4 -> 3+4i)

    // Spits into real and imaginary parts
    // todo handle empty real part
    val parts = s.split("ı")
    val real = parts(0)
    val imag = parts.lift(1).getOrElse("0")

    val realNum = (if real.last == '.' then real + "5" else real).toInt
    val imagNum = (if imag.last == '.' then imag + "5" else imag).toInt

    if imagNum == 0 then complex(realNum, 0)
    else complex(realNum, imagNum)
  end from

  /** Allow pattern matching like `VNum(r, i)` */
  def unapply(n: VNum): (Real, Real) = n.underlying.asTuple

  /** Implicit conversion to a VNum. Note that this needs to be imported first,
    * using `import VNum.given`
    */
  given Conversion[Int, VNum] = n => complex(n, 0)
  given Conversion[Double, VNum] = n => complex(n, 0)
  given Conversion[Long, VNum] = n => complex(n, 0)
  given Conversion[BigInt, VNum] = n => complex(n, 0)
  given Conversion[Complex[Real], VNum] = new VNum(_)
  given Conversion[Boolean, VNum] = b => if b then 1 else 0
end VNum
