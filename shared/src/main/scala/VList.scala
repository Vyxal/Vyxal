package vyxal

import collection.immutable.SeqOps
import collection.mutable
import scala.collection.SpecificIterableFactory

/** A Vyxal list. It simply wraps around another list and could represent a
  * completely evaluated list, a finite lazy list that is in the process of
  * being evaluated, or an infinite list.
  * @param lst
  *   The wrapped list actually holdings this VList's elements.
  */
class VList private (val lst: Seq[VAny])
    extends Seq[VAny],
      SeqOps[VAny, Seq, VList]:

  /** Map the list using a Vyxal function */
  def vmap(f: VAny => Context ?=> VAny)(using Context): VList =
    new VList(lst.map(f(_)))

  /** Zip two VLists together with a function. If one is longer than the other,
    * keep the longer one's elements as-is.
    */
  def zipWith(other: VList)(f: (VAny, VAny) => Context ?=> VAny)(using
      ctx: Context
  ): VList =
    new VList(
      lst
        .zipAll(other.lst, ctx.settings.defaultValue, ctx.settings.defaultValue)
        .map(f(_, _))
    )

  /** Zip two VLists together without a function. If one is longer than the
    * other, keep the longer one's elements as-is.
    */

  def zip(other: VList)(using ctx: Context): VList =
    val temp = lst
      .zipAll(
        other.lst,
        ctx.settings.defaultValue,
        ctx.settings.defaultValue
      )
      .map(VList(_, _))
    VList(temp*)

  /** Get the element at index `ind` */
  override def apply(ind: Int): VAny =
    if ind < 0 then
      // floorMod because % gives negative results with negative dividends
      lst(Math.floorMod(ind, lst.length))
    else
      try lst(ind)
      catch case _: IndexOutOfBoundsException => lst(ind % lst.length)

  def applyBig(ind: BigInt): VAny =
    val pos = VNum(ind.toString) % VNum(lst.length.toString)
    val i = VNum("0")
    lst.find(_ => i == pos) match
      case Some(x) => x
      case None    => throw new IndexOutOfBoundsException

  override def iterator: Iterator[VAny] = lst.iterator

  /** Get the length of this `VList`. A word of caution: this fully evaluates
    * the list, meaning that it won't work with infinite lists.
    */
  override def length: Int = lst.length

  override def toString(): String =
    lst.map(_.toString).mkString("[ ", " | ", " ]")

  override protected def fromSpecific(coll: IterableOnce[VAny]): VList =
    VList.fromSpecific(coll)

  override protected def newSpecificBuilder
      : collection.mutable.Builder[VAny, VList] =
    VList.newBuilder

  override def empty: VList = VList.empty

  protected def fromIterable(it: Seq[VAny]): VList =
    VList.fromIterable(it)
end VList

object VList extends SpecificIterableFactory[VAny, VList]:

  /** Zip multiple VLists together with a function.
    *
    * The parameter is a `PartialFunction` instead of a function because it's
    * going to match on a list and assume it's a specific length
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def zipMulti(lists: VList*)(f: PartialFunction[Seq[VAny], VAny])(using
      ctx: Context
  ): VList =
    val maxSize = lists.view.map(_.size).max
    val padded = lists.map { list =>
      if list.size == maxSize then list
      else list ++ Seq.fill(maxSize - list.size)(null)
    }
    new VList(padded.transpose.map { lst => f(lst.filter(_ != null)) })

  /** Turn some VAnys into iterables, then zip them together with a function. */
  def zipValues(values: VAny*)(f: PartialFunction[Seq[VAny], VAny])(using
      ctx: Context
  ): VList =
    val filteredLists = values.collect { case l: VList => l }
    val lists =
      if values.size == filteredLists.size then filteredLists
      else if filteredLists.isEmpty then values.map(ListHelpers.makeIterable(_))
      else
        val maxSize = filteredLists.view.map(_.size).max
        values.map {
          case l: VList => l
          case x        =>
            // If one of the other elements is a list but this isn't, repeat
            // this one to be as long as that list
            VList.fill(maxSize)(x)
        }
    VList.zipMulti(lists*)(f)
  end zipValues

  /** This lets us pattern match on `VList`s, silly as the implementation may
    * be.
    */
  def unapplySeq(vlist: VList): Seq[VAny] = vlist.lst

  override def empty: VList = new VList(Seq.empty)

  override def newBuilder: mutable.Builder[VAny, VList] =
    mutable.ArrayBuffer
      .newBuilder[VAny]
      .mapResult(elems => new VList(elems.toSeq))

  override def fromSpecific(it: IterableOnce[VAny]): VList = new VList(
    it.iterator.toSeq
  )

  def fromIterable(it: Seq[VAny]): VList = new VList(it)
end VList
