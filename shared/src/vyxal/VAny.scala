package vyxal

import scala.language.implicitConversions

import vyxal.Interpreter.executeFn
import vyxal.VNum.given

import scala.annotation.targetName
import scala.collection.immutable.NumericRange
import scala.collection.immutable.NumericRange.Inclusive
import scala.collection.mutable as mut
import scala.math.Ordered
import scala.util.matching.Regex

import spire.implicits.*
import spire.math.{Complex, Real}

sealed trait VAny
final case class VStr(s: String) extends VAny:
  override def toString: String = s

type VOptional = VAny | VNil
type VVal = VNum | VStr
type VPhysical = VNum | VStr | VList
type VIter = VList | VStr

given Conversion[String, VAny] = VStr(_)
given Conversion[VList, Seq[VAny]] = _.lst
given Conversion[Seq[VAny], VList] = VList.from(_)
given [T](using c: Conversion[T, VAny]): Conversion[Seq[T], VList] =
  seq => VList.from(seq.map(c))

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

given (using Context): Ordering[VAny] with
  override def compare(x: VAny, y: VAny): Int = MiscHelpers.compare(x, y)

case class VNil():
  override def toString = "nil"

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

  /** Call this function on the given arguments, using custom context variables.
    */
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
    val contextN = if args.length == 1 then args(0) else VList.from(Seq(args))
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

  def fromElement(elem: Element)(using origCtx: Context): VFun =
    VFun(
      elem.impl,
      elem.arity.getOrElse(origCtx.settings.defaultArity),
      List.empty,
      origCtx,
    )
end VFun

extension (self: VAny)
  @targetName("vEquals")
  def ===(that: VAny)(using Context): Boolean =
    (self, that) match
      case (a: VObject, b: VObject) => a.className == b.className &&
        a.fields == b.fields
      case (a: VList, b: VList) => a == b
      case (_: VFun, _) =>
        scribe.warn(s"Tried comparing function $self to $that")
        false
      case (_, _: VFun) =>
        scribe.warn(s"Tried comparing $self to function $that")
        false
      case (a: VVal, b: VVal) => MiscHelpers.compare(a, b) == 0
      case _ => false

  @targetName("vNotEquals")
  def !==(that: VAny)(using Context): Boolean = !(self === that)

  @targetName("plus")
  def +~(that: VAny)(using Context): VAny = MiscHelpers.add(self, that)

  @targetName("times")
  def *~(that: VAny)(using Context): VAny = MiscHelpers.multiply(self, that)

  def toBool =
    self match
      case n: VNum => n != VNum(0)
      case VStr(s) => s.nonEmpty
      case f: VFun => true
      case l: VList => l.nonEmpty
      case c: VConstructor => true
      case o: VObject => true
end extension

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

  def sliding(size: Int): Iterator[VList] = lst.sliding(size).map(VList.from(_))
  def sliding(size: Int, step: Int): Iterator[VList] =
    lst.sliding(size, step).map(VList.from(_))

object VList:
  def from(seq: Seq[VAny]): VList = new VList(seq)
  def from[T](seq: Seq[T])(using c: Conversion[T, VAny]): VList =
    new VList(seq.map(c))

  /** Zip multiple VLists together with a function.
    *
    * The parameter is a `PartialFunction` instead of a function because it's
    * going to match on a list and assume it's a specific length
    */
  def zipMulti(lists: Seq[VAny]*)(f: PartialFunction[Seq[VAny], VAny])(using
      ctx: Context
  ): Seq[VAny] =
    val maxSize = lists.view.map(_.size).max
    val padded = lists.map { list =>
      if list.sizeIs == maxSize then list
      else list ++ Seq.fill(maxSize - list.size)(null)
    }
    padded.transpose.map { lst => f(lst.filter(_ != null)) }

  /** Turn some VAnys into iterables, then zip them together with a function. */
  def zipValues(values: VAny*)(f: PartialFunction[Seq[VAny], VAny])(using
      ctx: Context
  ): Seq[VAny] =
    val filteredLists = values.collect { case VList(l) => l }
    val lists =
      if values.size == filteredLists.size then filteredLists
      else if filteredLists.isEmpty then values.map(ListHelpers.makeIterable(_))
      else
        val maxSize = filteredLists.view.map(_.size).max
        values.map {
          case VList(l) => l
          case x =>
            // If one of the other elements is a list but this isn't, repeat
            // this one to be as long as that list
            Seq.fill(maxSize)(x)
        }
    VList.zipMulti(lists*)(f)

  def seqToVList(seq: Seq[Seq[VAny]]): VList = new VList(seq.map(VList(_)))

  def index(lst: Seq[VAny], ind: Int): VAny =
    if lst.isEmpty then 0
    else if ind < 0 then
      // floorMod because % gives negative results with negative dividends
      lst(math.floorMod(ind, lst.length))
    else
      try lst(ind)
      catch
        case e: (IndexOutOfBoundsException | ArrayIndexOutOfBoundsException) =>
          lst(ind % lst.length)
end VList

class VNum private (val underlying: Complex[Real]) extends VAny, Ordered[VNum]:
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
  def to(end: VNum, step: VNum = 1): NumericRange[VNum] =
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
      s"${this.real.getString(Real.digits)}ı${this.imag.getString(Real.digits)}"

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
    raw"-?($DecimalRegexStr?ı$DecimalRegexStr?)|-?$DecimalRegexStr".r

  /** To force an implicit conversion */
  def apply[T](n: T)(using Conversion[T, VNum]): VNum = n

  def complex(real: Real, imag: Real) = new VNum(Complex(real, imag))

  /** Parse a number from a string */
  def apply(s: String): VNum = apply(s, 10)

  /** Parse a number from a string in the given base */
  def apply(s: String, radix: Int): VNum =
    s.replaceAll("[^-0-9a-zA-Z.ı_]", "") match
      case s"${real}ı$imag" => complex(
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
