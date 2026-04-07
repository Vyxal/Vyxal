package vyxal

import scala.language.implicitConversions

import vyxal.conversions.given
import vyxal.elements.ElementInformation
import vyxal.elements.Elements
import vyxal.Interpreter.executeFn

import scala.annotation.targetName
import scala.collection.immutable.NumericRange
import scala.collection.immutable.NumericRange.Inclusive
import scala.collection.mutable as mut
import scala.math.Ordered
import scala.reflect.TypeTest
import scala.util.matching.Regex

import java.time.{
  Duration as JDuration,
  Instant,
  LocalDateTime,
  Period,
  ZonedDateTime,
  ZoneId,
  ZoneOffset,
}
import java.time.format.DateTimeFormatter
import scala.util.Try

import spire.math.{Complex, Real}

/** A Vyxal value, represented as an ADT.
  *
  * Subclasses are:
  *   - [[VStr]]
  *   - [[VNum]]
  *   - [[VList]]
  *   - [[VFun]]
  *   - [[VConstructor]]
  *   - [[VObject]]
  *   - [[VDate]]
  *   - [[VDuration]]
  *
  * We derive [[CanEqual]] so that if you compare a `VAny`s to another type, the
  * compiler will complain
  */
sealed trait VAny derives CanEqual:
  @targetName("vEquals")
  def ===(that: VAny)(using Context): Boolean =
    (this, that) match
      case (a: VObject, b: VObject) => a.className == b.className &&
        a.fields == b.fields
      case (a: VList, b: VList) => a == b
      case (_: VFun, _) =>
        scribe.warn(s"Tried comparing function $this to $that")
        false
      case (_, _: VFun) =>
        scribe.warn(s"Tried comparing $this to function $that")
        false
      case (a: VDate, b: VDate) =>
        a.dt.toInstant == b.dt.toInstant
      case (a: VDuration, b: VDuration) => a.dur == b.dur
      case (a: VVal, b: VVal) => MiscHelpers.compare(a, b) == 0
      case _ => false

  @targetName("vNotEquals")
  def !==(that: VAny)(using Context): Boolean = !(this === that)

  @targetName("plus")
  def +~(that: VAny)(using Context): VAny = MiscHelpers.add(this, that)

  @targetName("times")
  def *~(that: VAny)(using Context): VAny = MiscHelpers.multiply(this, that)

  def toBool =
    this match
      case n: VNum => n != VNum(0)
      case VStr(s) => s.nonEmpty
      case f: VFun => true
      case l: VList => l.nonEmpty
      case c: VConstructor => true
      case o: VObject => true
      case d: VDate => true
      case d: VDuration => d.dur != JDuration.ZERO
end VAny

object VAny:
  given (using Context): Ordering[VAny] with
    override def compare(x: VAny, y: VAny): Int = MiscHelpers.compare(x, y)

type VVal = VNum | VStr
type VPhysical = VNum | VStr | VList
type VIter = VList | VStr
type VTemporal = VDate | VDuration

object conversions:
  given Conversion[String, VAny] = VStr(_)
  given Conversion[VList, Seq[VAny]] = _.lst
  given Conversion[Seq[VAny], VList] = VList(_)
  given [T](using c: Conversion[T, VAny]): Conversion[Seq[T], VList] =
    seq => VList(seq.map(c))

  trait ToVyxal[T, V]:
    def apply(t: T): V
  extension [T](t: T)
    def v[V](using conv: Conversion[T, V]): V = conv(t)
    def vs[V](using conv: ToVyxal[T, V]): V = conv(t)

  given [T](using c: ToVyxal[T, VAny]): ToVyxal[Seq[T], Seq[VAny]] =
    seq => seq.map(c(_))
  given [T, V](using c: Conversion[T, V]): ToVyxal[T, V] = c(_)
  given unionRightToVy[T](using c: ToVyxal[T, VAny]): ToVyxal[VAny | T, VAny] =
    x =>
      x match
        case v: VAny => v
        case _ => c(x.asInstanceOf[T])
  given unionLeftToVy[T](using c: ToVyxal[T, VAny]): ToVyxal[T | VAny, VAny] =
    x =>
      x match
        case v: VAny => v
        case _ => c(x.asInstanceOf[T])

  given Conversion[Int, VNum] = n => VNum.complex(n, 0)
  given Conversion[Double, VNum] = n => VNum.complex(n, 0)
  given Conversion[Long, VNum] = n => VNum.complex(n, 0)
  given Conversion[BigInt, VNum] = n => VNum.complex(n, 0)
  given Conversion[BigDecimal, VNum] = n => VNum.complex(n, 0)
  given Conversion[Real, VNum] = n => VNum.complex(n, 0)
  given Conversion[Complex[Real], VNum] = new VNum(_)
  given Conversion[Boolean, VNum] = b => if b then 1 else 0
  given Conversion[ZonedDateTime, VDate] = VDate(_)
  given Conversion[JDuration, VDuration] = VDuration(_)
end conversions

final case class VStr(s: String) extends VAny:
  override def toString: String = s

/** A function object (not a function definition)
  *
  * @param impl
  *   The implementation of this function
  * @param arity
  *   The arity of this function (may have been changed)
  * @param params
  *   Parameter names
  * @param ctx
  *   The context in which this function was defined
  */
case class VFun(
    impl: DirectFn,
    arity: Int,
    params: List[String | Int],
    var ctx: Context,
    originalAST: Option[AST.Lambda] = None,
    name: Option[String] = None,
) extends VAny:

  /** Make a copy of this function with a different arity. */
  def withArity(newArity: Int): VFun = this.copy(arity = newArity)

  /** Call this function on the given arguments, using custom context variables. */
  def execute(
      contextVarPrimary: VAny,
      contextVarSecondary: VAny,
      args: Seq[VAny],
  )(using ctx: Context): VAny =
    Interpreter.executeFn(
      this,
      contextVarPrimary,
      contextVarSecondary,
      args = args,
    )

  def executeResult(
      contextVarPrimary: VAny,
      contextVarSecondary: VAny,
      args: Seq[VAny],
      overwriteCtx: Boolean = false,
      vars: mut.Map[String, VAny] = mut.Map(),
  )(using ctx: Context): VAny =
    val res = Interpreter.executeFn(
      this,
      contextVarPrimary,
      contextVarSecondary,
      args = args,
    )

    res match
      case f: VFun => Interpreter.executeFn(
          f,
          contextVarPrimary,
          contextVarSecondary,
          args = args,
        )
      case _ => res
  end executeResult

  def apply(args: VAny*)(using ctx: Context): VAny =
    val contextN = if args.length == 1 then args(0) else VList(Seq(args))
    Interpreter.executeFn(this, contextN, args = args)

  override def toString =
    originalAST match
      case None => s"λ${if arity == -1 then "!" else arity.toInt}|<unknown>}"
      case Some(ast) => ast.toVyxal
end VFun

object VFun:
  def fromLambda(lam: AST.Lambda)(using origCtx: Context): VFun =
    val AST.Lambda(arity, params, body, originallyFunction, _) = lam
    VFun(
      () => ctx ?=> body.foreach(Interpreter.execute(_)(using ctx)),
      arity.getOrElse(origCtx.settings.defaultArity),
      params,
      origCtx,
      Some(lam),
    )

  def fromElement(elem: String)(using origCtx: Context): VFun =
    val correspondingInfo = ElementInformation.elements(elem)
    VFun(
      Elements.elements(elem).impl,
      if correspondingInfo.arity == -1 then origCtx.settings.defaultArity
      else correspondingInfo.arity,
      List.empty,
      origCtx,
    )
end VFun

case class VConstructor(
    name: String
) extends VAny:
  override def toString = s"$name constructor"

case class VObject(
    className: String,
    fields: Map[String, (Visibility, VAny)],
) extends VAny:
  override def toString =
    val fs = fields.map {
      case (name, (vis, value)) => s"${vis.sigil}$name: $value"
    }
    s"$className { ${fs.mkString(", ")} }"

/** A Vyxal date/time value wrapping a [[java.time.ZonedDateTime]].
  *
  * Every VDate carries a timezone. The default is the system local zone.
  * Comparison is based on the underlying [[java.time.Instant]] so that two
  * VDates representing the same point in time are equal regardless of zone.
  */
final case class VDate(dt: ZonedDateTime) extends VAny, Ordered[VDate]:
  def year: VNum = VNum(dt.getYear)
  def month: VNum = VNum(dt.getMonthValue)
  def day: VNum = VNum(dt.getDayOfMonth)
  def hour: VNum = VNum(dt.getHour)
  def minute: VNum = VNum(dt.getMinute)
  def second: VNum = VNum(dt.getSecond)
  def zone: String = dt.getZone.getId

  /** Calendar-aware: add whole months. */
  def plusMonths(n: Long): VDate = VDate(dt.plusMonths(n))

  /** Calendar-aware: add whole years. */
  def plusYears(n: Long): VDate = VDate(dt.plusYears(n))

  /** Calendar-aware: add whole weeks. */
  def plusWeeks(n: Long): VDate = VDate(dt.plusWeeks(n))

  /** Compare by instant (absolute point in time). */
  override def compare(that: VDate): Int =
    dt.toInstant.compareTo(that.dt.toInstant)
  override def toString: String = dt.toString
end VDate

object VDate:
  /** The zone used when none is specified. */
  private def defaultZone: ZoneId = ZoneId.systemDefault()

  def now(): VDate = VDate(ZonedDateTime.now())

  def of(
      year: Int,
      month: Int,
      day: Int,
      hour: Int = 0,
      minute: Int = 0,
      second: Int = 0,
  ): VDate =
    VDate(
      ZonedDateTime.of(year, month, day, hour, minute, second, 0, defaultZone)
    )

  /** Parse a date/time string.  Accepts any format the underlying library
    * can handle — ISO-8601 with or without timezone, RFC-1123,
    * date-only, etc.  If the parsed result has no zone information the
    * system default zone is assumed.
    */
  def parse(s: String): VDate =
    // Chain of attempts — first success wins
    val attempts: LazyList[Try[ZonedDateTime]] = LazyList(
      // Try ZonedDateTime directly (uses ISO_ZONED_DATE_TIME internally)
      Try(ZonedDateTime.parse(s)),
      // RFC-1123 (e.g. "Tue, 3 Jun 2008 11:05:30 GMT")
      Try(ZonedDateTime.parse(s, DateTimeFormatter.RFC_1123_DATE_TIME)),
      // Offset date-time without zone id (e.g. "2024-03-15T10:30+05:00")
      Try(ZonedDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME)),
      // LocalDateTime (no zone) — attach default zone
      Try(LocalDateTime.parse(s).atZone(defaultZone)),
      // Date-only — midnight in default zone
      Try(java.time.LocalDate.parse(s).atStartOfDay(defaultZone)),
      // Instant — convert to default zone
      Try(Instant.parse(s).atZone(defaultZone)),
    )

    attempts
      .collectFirst { case scala.util.Success(zdt) => VDate(zdt) }
      // Last resort: let java.time throw a descriptive exception
      .getOrElse(VDate(ZonedDateTime.parse(s)))
  end parse

  /** Creates a [[VDate]] from a Unix epoch second in the system default zone.
    */
  def fromEpochSecond(epoch: Long): VDate =
    VDate(
      ZonedDateTime.ofInstant(Instant.ofEpochSecond(epoch), defaultZone)
    )

  /** Creates a [[VDate]] from a Unix epoch second in a specific zone. */
  def fromEpochSecond(epoch: Long, zone: ZoneId): VDate =
    VDate(ZonedDateTime.ofInstant(Instant.ofEpochSecond(epoch), zone))
end VDate

/** A Vyxal duration value wrapping a [[java.time.Duration]]. */
final case class VDuration(dur: JDuration) extends VAny, Ordered[VDuration]:
  def toDays: VNum = VNum(dur.toDays)
  def toHours: VNum = VNum(dur.toHours)
  def toMinutes: VNum = VNum(dur.toMinutes)
  def toSeconds: VNum = VNum(dur.getSeconds)
  def toMillis: VNum = VNum(dur.toMillis)

  override def compare(that: VDuration): Int = dur.compareTo(that.dur)
  override def toString: String = dur.toString
end VDuration

object VDuration:
  def ofDays(n: Long): VDuration = VDuration(JDuration.ofDays(n))
  def ofHours(n: Long): VDuration = VDuration(JDuration.ofHours(n))
  def ofMinutes(n: Long): VDuration = VDuration(JDuration.ofMinutes(n))
  def ofSeconds(n: Long): VDuration = VDuration(JDuration.ofSeconds(n))
  def ofMillis(n: Long): VDuration = VDuration(JDuration.ofMillis(n))
  def ofWeeks(n: Long): VDuration = VDuration(JDuration.ofDays(n * 7))
  def parse(s: String): VDuration = VDuration(JDuration.parse(s))
  val Zero: VDuration = VDuration(JDuration.ZERO)
end VDuration

/** A Vyxal list. It simply wraps around another list and could represent a
    * completely evaluated list, a finite lazy list that is in the process of
    * being evaluated, or an infinite list.
    *
    * To construct a VList, use VList.apply or VList
    * @param lst
    *   The wrapped list actually holdings this VList's elements.
    */
final case class VList(lst: Seq[VAny]) extends VAny:
  override def toString(): String =
    lst.map(_.toString).mkString("[ ", " | ", " ]")

class VNum(val underlying: Complex[Real]) extends VAny, Ordered[VNum]:
  def real: Real = underlying.real
  def imag: Real = underlying.imag

  def toInt: Int = underlying.toInt
  def toDouble: Double = underlying.real.toDouble
  def toLong: Long = underlying.toLong
  def toBigInt: BigInt = underlying.real.toRational.toBigInt

  def signum: VNum = underlying.complexSignum

  /** Whether the real part is small enough to be converted to an `Int` */
  def isValidInt: Boolean = underlying.real.isValidInt

  /** Whether there is only a real part */
  def isReal: Boolean = underlying.isReal

  /** Whether there is only an imaginary part */
  def isImaginary: Boolean = underlying.isImaginary

  /** Whether there is both an imaginary and a real part */
  def isComplex: Boolean = !(underlying.isImaginary || underlying.isReal)

  /** Round the real and imaginary parts */
  def toIntegral: VNum = underlying.round

  def floor: VNum = underlying.floor
  def ceil: VNum = underlying.ceil

  def sqrt: VNum = underlying.sqrt

  @targetName("neg")
  def unary_- : VNum = -underlying
  @targetName("plus")
  def +(rhs: VNum): VNum = underlying + rhs.underlying
  @targetName("minus")
  def -(rhs: VNum): VNum = underlying - rhs.underlying
  @targetName("times")
  def *(rhs: VNum): VNum = underlying * rhs.underlying
  @targetName("divide")
  def /(rhs: VNum): VNum =
    if rhs == VNum(0) then 0 else underlying / rhs.underlying
  @targetName("pow")
  def **(rhs: VNum): VNum = underlying ** rhs.underlying

  /** Floating-point floored modulus, not Java's `%` */
  @targetName("mod")
  def %(rhs: VNum): VNum = this - (this / rhs).floor * rhs

  def vabs: VNum = underlying.abs
  def arg: VNum = underlying.arg

  /** Inclusive range */
  def range(end: VNum, step: VNum = 1): NumericRange[VNum] =
    Inclusive(this, end, step)

  def sin: VNum = underlying.sin
  def cos: VNum = underlying.cos
  def tan: VNum = underlying.tan
  def asin: VNum = underlying.asin
  def acos: VNum = underlying.acos
  def atan: VNum = underlying.atan
  // TODO switch to this code when Spire fixes their bugs
  // def atan2(rhs: VNum): VNum = spire.math.atan2(underlying, rhs.underlying)
  def atan2(rhs: VNum): VNum =
    // atan2(a, b) = -i * ln((b + i*a)/sqrt(a^2 + b^2)), according to WolframAlpha
    Complex[Real](0, -1) *
      spire.math.log(
        (rhs.underlying + underlying * Complex[Real](0, 1)) /
          spire.math.sqrt(underlying ** 2 + rhs.underlying ** 2)
      )
  def sinh: VNum = underlying.sinh
  def cosh: VNum = underlying.cosh
  // TODO same as above
  // def tanh: VNum = underlying.tanh
  def tanh: VNum = underlying.sinh / underlying.cosh

  override def compare(that: VNum): Int =
    this.underlying.real.compare(that.underlying.real) match
      case 0 => this.underlying.imag.compare(that.underlying.imag)
      case x => x

  override def toString =
    if this.imag == 0 then this.real.getString(Real.digits)
    else
      s"${this.real.getString(Real.digits)}j${this.imag.getString(Real.digits)}"

  override def equals(obj: Any) =
    obj match
      case n: VNum => (underlying `eq` n.underlying) ||
        ((this.real - n.real).abs < VNum.Epsilon &&
          (this.imag - n.imag).abs < VNum.Epsilon)
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
    raw"-?($DecimalRegexStr?j$DecimalRegexStr?)|-?$DecimalRegexStr".r

  /** To force an implicit conversion */
  def apply[T](n: T)(using Conversion[T, VNum]): VNum = n

  def complex(real: Real, imag: Real) = new VNum(Complex(real, imag))

  /** Parse a number from a string */
  def apply(s: String): VNum = apply(s, 10)

  /** Parse a number from a string in the given base */
  def apply(s: String, radix: Int): VNum =
    s.replaceAll("[^-0-9a-zA-Z._]", "") match
      case s"${real}j$imag" => complex(
          parseDecimal(real, radix, 0),
          if imag.isEmpty then 1 else parseDecimal(imag, radix, 1),
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

  given Integral[VNum] with
    override def negate(x: VNum): VNum = -x
    override def plus(x: VNum, y: VNum): VNum = x + y
    override def minus(x: VNum, y: VNum): VNum = x - y
    override def times(x: VNum, y: VNum): VNum = x * y
    override def rem(x: VNum, y: VNum): VNum = x % y
    override def quot(x: VNum, y: VNum): VNum = (x / y).toIntegral
    override def compare(x: VNum, y: VNum): Int = x.compare(y)
    override def fromInt(x: Int): VNum = x
    override def parseString(str: String): Option[VNum] = Some(VNum(str))
    override def toInt(x: VNum): Int = x.toInt
    override def toLong(x: VNum): Long = x.toLong
    override def toFloat(x: VNum): Float = x.toFloat
    override def toDouble(x: VNum): Double = x.toDouble
end VNum

object VListOf:
  def unapply[T](seq: Seq[Any] | VList)(using
      tt: TypeTest[Any, T]
  ): Option[Seq[T]] =
    val seq_ = seq match
      case VList(l) => l
      case s: Seq[Any] => s
    val matches = seq_.forall {
      case tt(_) => true
      case _ => false
    }
    Option.when(matches)(seq_.asInstanceOf[Seq[T]])
