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
case class VList(val lst: Seq[VAny])
    extends Seq[VAny],
      SeqOps[VAny, Seq, VList] {

  /** Get the element at index `ind`
    */
  override def apply(ind: Int): VAny = lst(ind)

  override def iterator: Iterator[VAny] = lst.iterator

  /** Get the length of this `VList`. A word of caution: this fully evaluates
    * the list, meaning that it won't work with infinite lists.
    */
  override def length: Int = lst.length

  override protected def fromSpecific(coll: IterableOnce[VAny]): VList =
    VList.fromSpecific(coll)

  override protected def newSpecificBuilder
      : collection.mutable.Builder[VAny, VList] =
    VList.newBuilder

  override def empty: VList = VList.empty
}

object VList extends SpecificIterableFactory[VAny, VList] {
  override def empty: VList = new VList(Seq.empty)

  override def newBuilder: mutable.Builder[VAny, VList] =
    mutable.ArrayBuffer
      .newBuilder[VAny]
      .mapResult(elems => new VList(elems.toSeq))

  override def fromSpecific(it: IterableOnce[VAny]): VList = new VList(
    it.iterator.toSeq
  )
}
