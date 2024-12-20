package vyxal

import vyxal.VNum.given

import scala.annotation.targetName
import scala.collection.immutable.ArraySeq
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

extension (self: Seq[VAny])
  /** Map the list using a Vyxal function */
  def vmap(f: VAny => Context ?=> VAny)(using Context): Seq[VAny] =
    self.lst.map(f(_))

  /** Zip two VLists together with a function. If one is longer than the other,
    * keep the longer one's elements as-is.
    */
  def zipWith(
      other: Seq[VAny]
  )(f: (VAny, VAny) => Context ?=> VAny)(using ctx: Context): Seq[VAny] =
    self.lst
      .zipAll(other, ctx.settings.defaultValue, ctx.settings.defaultValue)
      .map(f(_, _))

  /** Zip two VLists together without a function. If one is longer than the
    * other, keep the longer one's elements as-is.
    */
  def vzip(other: Seq[VAny])(using ctx: Context): Seq[VAny] =
    val temp = self.lst
      .zipAll(other, ctx.settings.defaultValue, ctx.settings.defaultValue)
      .map((l, r) => VList.from(Seq(l, r)))
    temp

  def take(n: VNum): Seq[VAny] =
    if n <= Int.MaxValue then return VList.from(self.lst.take(n.toInt))
    val ret = collection.mutable.ListBuffer.empty[VAny]
    var i: VNum = 0
    while i < n do
      ret += indexBig(i.real.toBigInt)
      i += 1
    ret.toList

  def drop(n: VNum): Seq[VAny] =
    var ret = self.lst
    var ind = n.toBigInt
    while ind >= Int.MaxValue do
      ret = ret.drop(Int.MaxValue)
      ind += Int.MaxValue
    ret.drop(n.toInt)

  def index(ind: VAny)(using ctx: Context): VAny =
    ind match
      case ind: VNum => self.indexBig(ind.real.toBigInt)
      case inds: VList => inds.vmap(self.index)
      case _ => throw new Exception("Index must be a number")

  private def indexBig(ind: BigInt): VAny =
    var pos = if ind < 0 then ind % self.lst.length else ind
    var temp = self.lst
    while pos >= Int.MaxValue do
      // Instead of using modulo, reset the list if out of bounds
      if temp.isEmpty then temp = self.lst
      temp = temp.drop(Int.MaxValue)
      pos -= Int.MaxValue
    VList.index(temp, pos.toInt)

  def bigLength: BigInt =
    self.lst match
      case _: ArraySeq[?] =>
        // We know ArraySeqs can't be bigger than Int.MaxValue elements
        self.lst.length
      case _ =>
        val iter = self.lst.iterator
        var count = BigInt(0)
        while iter.nextOption().nonEmpty do count += 1
        count

  def extend(toSize: BigInt, elem: VAny): Seq[VAny] =
    if toSize <= Int.MaxValue && self.lst.sizeIs >= toSize.toInt then
      return self
    var ret = self.lst
    var currSize = BigInt(ret.size)
    while currSize < toSize do
      val rem = toSize - currSize
      val add = if rem <= Int.MaxValue then rem.toInt else Int.MaxValue
      ret = ret.lst ++ Seq.fill(add)(elem)
      currSize += add
    ret

  /** self isn't an overload of isDefinedAt because it needs to take a `BigInt`
    */
  def hasIndex(ind: BigInt): Boolean =
    if ind <= Int.MaxValue && ind >= 0 then
      return self.lst.isDefinedAt(ind.toInt)
    var pos = if ind < 0 then ind % self.lst.length else ind
    var temp = self.lst
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
    if self.lst.isEmpty then Seq.empty
    else self.lst.tail

  /** The default implementation of distinct doesn't work with VNums, so we must
    * override it
    */
  def vDistinct: Seq[VAny] =
    val seen = mutable.ArrayBuffer.empty[VAny]
    self.lst.filter { elem =>
      if seen.contains(elem) then false
      else
        seen += elem
        true
    }

  @targetName("multiSetDiff")
  def --(other: Seq[VAny]): Seq[VAny] =
    var ret = self.lst

    for elem <- other do
      if ret.contains(elem) then
        ret = ret.indexWhere(_ == elem) match
          case -1 => ret
          case ind => ret.take(ind) ++ ret.drop(ind + 1)
    ret

  @targetName("xor")
  def ^(other: Seq[VAny]): Seq[VAny] =
    self.lst.filterNot(other.contains(_)) ++ other.filterNot(self.contains(_))
end extension
