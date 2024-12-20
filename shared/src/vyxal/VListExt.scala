package vyxal

import vyxal.VNum.given

import scala.annotation.targetName
import scala.collection.immutable.ArraySeq
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

extension (self: Seq[VAny])
  /** Map the list using a Vyxal function */
  def vmap(f: VAny => Context ?=> VAny)(using Context): Seq[VAny] =
    self.map(f(_))

  /** Zip two VLists together with a function. If one is longer than the other,
    * keep the longer one's elements as-is.
    */
  def zipWith(
      other: Seq[VAny]
  )(f: (VAny, VAny) => Context ?=> VAny)(using ctx: Context): Seq[VAny] =
    self
      .zipAll(other, ctx.settings.defaultValue, ctx.settings.defaultValue)
      .map(f(_, _))

  /** Zip two VLists together without a function. If one is longer than the
    * other, keep the longer one's elements as-is.
    */
  def vzip(other: Seq[VAny])(using ctx: Context): Seq[VAny] =
    val temp = self
      .zipAll(other, ctx.settings.defaultValue, ctx.settings.defaultValue)
      .map((l, r) => VList(Seq(l, r)))
    temp

  def take(n: VNum): Seq[VAny] =
    if n <= Int.MaxValue then return VList(self.take(n.toInt))
    val ret = collection.mutable.ListBuffer.empty[VAny]
    var i: VNum = 0
    while i < n do
      ret += indexBig(i.real.toBigInt)
      i += 1
    ret.toList

  def drop(n: VNum): Seq[VAny] =
    var ret = self
    var ind = n.toBigInt
    while ind >= Int.MaxValue do
      ret = ret.drop(Int.MaxValue)
      ind += Int.MaxValue
    ret.drop(n.toInt)

  def index(ind: VAny)(using ctx: Context): VAny =
    ind match
      case ind: VNum => self.indexBig(ind.real.toBigInt)
      case inds: VList => inds.vmap(self.index)
      case _ => throw new Exception("Index must be a number or list")

  private def indexBig(ind: BigInt): VAny =
    if self.isEmpty then return 0

    var pos = if ind < 0 then ind % self.length else ind
    var temp = self
    while pos >= Int.MaxValue do
      // Instead of using modulo, reset the list if out of bounds
      if temp.isEmpty then temp = self
      temp = temp.drop(Int.MaxValue)
      pos -= Int.MaxValue

    val posInt = pos.toInt
    if self.isDefFinite then self(math.floorMod(posInt, self.size))
    else
      try self(posInt)
      catch
        case e: (IndexOutOfBoundsException | ArrayIndexOutOfBoundsException) =>
          self(posInt % self.size)
  end indexBig

  def bigLength: BigInt =
    self match
      case _: ArraySeq[?] =>
        // We know ArraySeqs can't be bigger than Int.MaxValue elements
        self.length
      case _ =>
        val iter = self.iterator
        var count = BigInt(0)
        while iter.nextOption().nonEmpty do count += 1
        count

  def extend(toSize: BigInt, elem: VAny): Seq[VAny] =
    if toSize <= Int.MaxValue && self.sizeIs >= toSize.toInt then return self
    var ret = self
    var currSize = BigInt(ret.size)
    while currSize < toSize do
      val rem = toSize - currSize
      val add = if rem <= Int.MaxValue then rem.toInt else Int.MaxValue
      ret = ret ++ Seq.fill(add)(elem)
      currSize += add
    ret

  /** self isn't an overload of isDefinedAt because it needs to take a `BigInt`
    */
  def hasIndex(ind: BigInt): Boolean =
    if ind <= Int.MaxValue && ind >= 0 then return self.isDefinedAt(ind.toInt)
    var pos = if ind < 0 then ind % self.length else ind
    var temp = self
    while pos >= Int.MaxValue do
      if temp.isEmpty then return false
      temp = temp.drop(Int.MaxValue)
      pos -= Int.MaxValue
    return true

  /** Whether this list is known to be finite. May be finite even if return
    * value is false
    */
  def isDefFinite: Boolean =
    self match
      case _: LazyList[?] => self.knownSize != -1
      case _ => true

  def vTail: Seq[VAny] =
    if self.isEmpty then Seq.empty
    else self.tail

  /** The default implementation of distinct doesn't work with VNums, so we must
    * override it
    */
  def vDistinct: Seq[VAny] =
    val seen = mutable.ArrayBuffer.empty[VAny]
    self.filter { elem =>
      if seen.contains(elem) then false
      else
        seen += elem
        true
    }

  @targetName("multiSetDiff")
  def --(other: Seq[VAny]): Seq[VAny] =
    var ret = self

    for elem <- other do
      if ret.contains(elem) then
        ret = ret.indexWhere(_ == elem) match
          case -1 => ret
          case ind => ret.take(ind) ++ ret.drop(ind + 1)
    ret

  @targetName("xor")
  def ^(other: Seq[VAny]): Seq[VAny] =
    self.filterNot(other.contains(_)) ++ other.filterNot(self.contains(_))
end extension
