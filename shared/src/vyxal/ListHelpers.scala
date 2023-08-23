package vyxal

import vyxal.VNum.given

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scala.collection.mutable as mut

object ListHelpers:

  def cartesianPower(lhs: VAny, pow: VNum)(using Context): VList =
    if pow == VNum(0) then VList(VList())
    else
      val lst = ListHelpers.makeIterable(lhs)
      cartesianProductMulti(Seq.fill(pow.toInt)(lst))

  /** Cartesian product */
  def cartesianProduct(left: VAny, right: VAny, unsafe: Boolean = false)(using
      ctx: Context
  ): VList =
    val lhs = makeIterable(left)
    val rhs = makeIterable(right)

    if unsafe || (lhs.knownSize != -1 && rhs.knownSize != -1) then
      VList.from(lhs.flatMap(l => rhs.map(r => VList(l, r))))
    else VList.from(mergeInfLists(lhs.map(l => rhs.map(r => VList(l, r)))))

  def cartesianProductMulti(lists: Seq[VAny])(using Context): VList =
    lists.map(ListHelpers.makeIterable(_)) match
      case head +: tail =>
        val first = head.map(VList(_))
        VList.from(tail.foldLeft(first) { (acc, next) =>
          cartesianProduct(VList.from(acc), next).map { elem =>
            (elem: @unchecked) match
              case VList(l, r) =>
                VList.from(l.asInstanceOf[VList] :+ r)
          }
        })
      case _ => VList.empty

  /** Remove items that are duplicates after transforming by `fn` */
  def dedupBy(iterable: VList, fn: VFun)(using Context): VList =
    // Can't use a Set here because equal VNums don't hash to the same value
    val seen = mut.ArrayBuffer.empty[VAny]
    iterable.filter { item =>
      val res = fn.execute(item, 0, List(item))
      if seen.contains(res) then false
      else
        seen += res
        true
    }

  def dotProduct(left: VList, right: VList)(using Context): VAny =
    left *~ right match
      case l: VList => ListHelpers.sum(l)
      case x => x

  def filter(iterable: VList, predicate: VFun)(using Context): VList =
    predicate.originalAST match
      case Some(lam) =>
        val branches = lam.body
        val filtered = iterable.zipWithIndex.filter { (item, index) =>
          var keep = true
          var branchList = branches
          val sharedVars = mut.Map.empty[String, VAny]

          while branchList.nonEmpty && keep do
            val fun =
              VFun.fromLambda(AST.Lambda(1, List.empty, List(branchList.head)))
            val res = Interpreter.executeFn(
              fun,
              ctxVarPrimary = item,
              ctxVarSecondary = index,
              args = List(item),
              vars = sharedVars
            )
            keep = res.toBool
            branchList = branchList.tail

          keep
        }

        VList.from(filtered.map(_._1))
      case None =>
        VList.from(iterable.zipWithIndex.collect {
          case (item, index)
              if predicate.execute(item, index, List(item)).toBool =>
            item
        })

  end filter

  def flatten(xs: Seq[VAny]): VList =
    VList.from(
      xs.flatMap {
        case l: VList => flatten(l)
        case x => Seq(x)
      }
    )

  /** A wrapper call to the generator method in interpreter */
  def generate(function: VFun, initial: VList)(using ctx: Context): VList =
    val firstN = initial.length match
      case 0 => ctx.settings.defaultValue
      case 1 => initial.head
      case _ => initial.last

    val firstM = initial.length match
      case 0 => ctx.settings.defaultValue
      case 1 => initial.head
      case _ => initial.init.last
    VList.from(
      initial ++: Interpreter.generator(
        function,
        firstN,
        firstM,
        function.arity,
        initial
      )
    )
  end generate

  /** A wrapper call to the generator method in interpreter, but forced to be
    * dyadic
    *
    * @param function
    *   The function to generate with
    * @param initial
    *   The initial values to generate from
    * @param ctx
    *   The context to use
    * @return
    */
  def generateDyadic(function: VFun, initial: VList)(using
      ctx: Context
  ): VList =
    val firstN =
      if initial.isEmpty then ctx.settings.defaultValue else initial.last

    val firstM = initial.length match
      case 0 => ctx.settings.defaultValue
      case 1 => initial.head
      case _ => initial.init.last
    VList.from(
      initial ++: Interpreter.generator(
        function,
        firstN,
        firstM,
        2,
        initial
      )
    )
  end generateDyadic

  /** Group elements according to the result of some function
    * @return
    *   A VList where each element is again a VList containing a group of
    *   elements that all had the same result when `fn` was applied to them
    */
  def groupBy(iterable: VList, fn: VFun)(using Context): VList =
    // TODO Make this work on lazylists? Doable but extremely hard
    val nonNumGroups = mut.Map.empty[VAny, ArrayBuffer[VAny]]
    // VNums can't be used as HashMap keys so we need a separate list for them
    val numGroups = ArrayBuffer.empty[(VAny, ArrayBuffer[VAny])]
    for elem <- iterable do
      fn(elem) match
        case n: VNum =>
          numGroups.find((key, _) => key == n) match
            case Some((_, group)) => group += elem
            case _ => numGroups += ((n, ArrayBuffer(elem)))
        case res =>
          if nonNumGroups.contains(res) then nonNumGroups(res) += elem
          else nonNumGroups(res) = ArrayBuffer(elem)
    VList.from(
      (nonNumGroups.view ++ numGroups.view)
        .map((_, group) => VList.from(group.toSeq))
        .toSeq
    )
  end groupBy

  def groupConsecutive(iterable: VList): VList =
    VList.from(groupConsecutiveBy(iterable)(x => x).map(VList.from))

  def groupConsecutiveBy[T](
      iterable: Seq[T],
  )(function: T => Any): Seq[Seq[T]] =
    // TODO make this work on lazylists?
    val out = ArrayBuffer.empty[Seq[T]]
    var current = ArrayBuffer.empty[T]
    var last: Option[Any] = None
    iterable.foreach { item =>
      val key = function(item)
      if last.isEmpty || last.get == key then current += item
      else
        out += current.toSeq
        current = ArrayBuffer(item)
      last = Some(key)
    }
    if current.nonEmpty then out += current.toSeq
    out.toSeq

  def interleave(left: VList, right: VList)(using Context): VList =
    val out = ArrayBuffer.empty[VAny]
    val leftIter = left.iterator
    val rightIter = right.iterator
    while leftIter.hasNext && rightIter.hasNext do
      out += leftIter.next()
      out += rightIter.next()

    out ++= leftIter
    out ++= rightIter

    VList(out.toSeq*)

  /** Join a list on a string/number, or intersperse a list within `lst` */
  def join(lst: VList, sep: VAny)(using Context): VAny =
    sep match
      case s: String => lst.mkString(s)
      case sep: (VNum | VList) =>
        val l = ListHelpers.makeIterable(sep)
        ListHelpers.flatten(lst.map(Seq(_)).reduce(_ ++ l ++ _))
      case _: VFun => ??? // todo reduce?

  /** Make an iterable from a value
    *
    * @param value
    *   The value to make an iterable from
    * @param overrideRangify
    *   Whether to rangify (optional). If given, overrides
    *   `ctx.settings.rangify`
    * @return
    */
  def makeIterable(
      value: VAny,
      overrideRangify: Option[Boolean] = None
  )(using ctx: Context): VList =
    value match
      case list: VList => list
      case str: String => VList.from(str.map(_.toString))
      case fn: VFun => VList(fn)
      case num: VNum =>
        if overrideRangify.getOrElse(ctx.settings.rangify) then
          val start = ctx.settings.rangeStart
          val offset = ctx.settings.rangeOffset
          VList.from(start.to(num - offset))
        else
          VList.from(
            num.toString.map { x =>
              if x.isDigit then VNum(x - '0')
              else x.toString
            }
          )

  def matrixMultiply(lhs: VList, rhs: VList)(using Context): VList =
    val rhsTemp = transposeSafe(rhs)
    VList.from(
      lhs.map { row =>
        val rowIt = ListHelpers.makeIterable(row)
        VList.from(
          rhsTemp.map(col => dotProduct(rowIt, ListHelpers.makeIterable(col)))
        )
      }
    )

  def map(f: VFun, to: VList)(using Context): VList =
    f.originalAST match
      case Some(lam) =>
        val branches = lam.body
        val params = f.originalAST match
          case Some(lam) => lam.params
          case None => List.empty
        VList.from(to.zipWithIndex.map { (item, index) =>
          val sharedVars = mut.Map.empty[String, VAny]
          branches.foldLeft(item) { (out, branch) =>
            Interpreter.executeFn(
              VFun.fromLambda(AST.Lambda(1, params, List(branch))),
              ctxVarPrimary = out,
              ctxVarSecondary = index,
              args = List(out),
              vars = sharedVars
            )
          }
        })

      case None =>
        VList(to.zipWithIndex.map { (item, index) =>
          f.execute(item, index, List(item))
        }*)
  end map

  /** Merge a possibly infinite list of possibly infinite lists diagonally */
  def mergeInfLists[T](lists: Seq[Seq[T]]): LazyList[T] =
    // Based off of https://stackoverflow.com/a/20516638
    val it = lists.iterator

    val touched = mut.ListBuffer.empty[Iterator[T]]

    def gen(): LazyList[T] =
      touched.filterInPlace(_.hasNext)
      val diag = touched.map(_.next()).to(LazyList)

      if it.hasNext then
        touched += it.next().iterator
        diag #::: gen()
      else if touched.nonEmpty then diag #::: gen()
      else diag

    gen()
  end mergeInfLists

  /** Mold a list into a shape.
    * @param content
    *   The list to mold.
    * @param shape
    *   The shape to mold the list into.
    * @return
    *   VyList The content, molded into the shape.
    */
  def mold(content: VList, shape: VList)(using Context): VList =
    def moldHelper(content: VList, shape: VList, ind: Int): VList =
      val output = ArrayBuffer.empty[VAny]
      val mutContent = content
      val mutShape = shape.toList
      var index = ind
      for item <- mutShape do
        item match
          case item: VList =>
            output += moldHelper(mutContent, item, index)
            output.last match
              case list: VList => index += list.length - 1
              case _ => index += 1
          case item => output += mutContent(index)
        index += 1

      VList(output.toSeq*)
    end moldHelper
    moldHelper(content, shape, 0)
  end mold

  def nthItems(iterable: VList | String, index: VNum): VAny =
    val temp = iterable match
      case iterable: VList => iterable
      case iterable: String => VList(iterable.map(_.toString)*)

    val indInt = index.toInt
    val value =
      if indInt == 0 then temp ++ temp.reverse
      else
        temp.zipWithIndex.collect {
          case (elem, ind) if ind % indInt == 0 => elem
        }
    iterable match
      case _: VList => VList.from(value)
      case _: String => value.mkString

  def overlaps(iterable: Seq[VAny], size: Int): Seq[VList] =
    if size == 0 then Seq.empty
    else iterable.sliding(size).toSeq.map(VList.from)

  // Just for strings
  def overlaps(iterable: String, size: Int): Seq[String] =
    if size == 0 then Seq.empty
    else iterable.sliding(size).toSeq

  /** List partitions (like set partitions, but contiguous sublists) */
  def partitions(lst: VList)(using Context): VList =
    val size = lst.knownSize
    if size == -1 then
      // Possibly infinite
      VList.from(
        partitionsLazy(lst).map(part => VList.from(part.map(VList.from)))
      )
    else
      // Forces evaluation of the list because we need to know the length
      val shapes = NumberHelpers
        .partitions(size)
        .map(partition =>
          partition
            .asInstanceOf[VList]
            .map(v => VList.fill(v.asInstanceOf[VNum].toInt)(1))
        )
        .map(partition => partition.permutations.toSeq)
        .flatten

      val uniqueShapes = ListBuffer[Seq[VList]]()
      for shape <- shapes do
        if !uniqueShapes.exists(_.equals(shape)) then uniqueShapes += shape

      VList.from(uniqueShapes.map(shape => mold(lst, VList.from(shape))).toSeq)
    end if
  end partitions

  /** A version of [[partitions]] that hopefully works with infinite lists */
  private def partitionsLazy(lst: VList): Seq[Seq[Seq[VAny]]] =
    def helper(lst: Seq[VAny]): LazyList[LazyList[Seq[VAny]]] =
      if lst.isEmpty then LazyList.empty
      else
        LazyList(lst) #:: mergeInfLists(
          LazyList
            .from(1)
            .takeWhile(i => lst.sizeIs > i)
            .map { i =>
              val (left, right) = lst.splitAt(i)
              helper(right).map(partition => left #:: partition)
            }
        )
    helper(lst)

  def partitionBy(lst: VList, shapes: Seq[VNum])(using Context): VList =
    val shapeSublists = shapes.map(x => VList.fill(x.toInt)(1))
    mold(lst, VList.from(shapeSublists))

  def permutations(iterable: VList): Seq[VList] =
    val temp = iterable.toList
    val perms = temp.permutations
    perms.map(VList.from).toSeq

  def sortBy(iterable: VList, key: VFun)(using Context): VList =
    key.originalAST match
      case Some(lam) =>
        val branches = lam.body
        if branches.sizeIs < 2 then
          return VList(
            iterable.zipWithIndex
              .sorted { (a, b) =>
                MiscHelpers.compare(
                  key.executeResult(a(0), a(1), List(a(0))),
                  key.executeResult(b(0), b(1), List(b(0)))
                )
              }
              .map(_._1)*
          )

        val out = iterable.zipWithIndex
          .sortWith { (a, b) =>
            branches.view
              .map { branch =>
                val f =
                  VFun.fromLambda(AST.Lambda(1, List.empty, List(branch)))
                (
                  f.execute(a(0), a(1), List(a(0))),
                  f.execute(b(0), b(1), List(b(0)))
                )
              }
              .find(_ != _)
              // If they compare equal with all branches, a < b is false
              .fold(false) { case (aRes, bRes) =>
                MiscHelpers.compare(aRes, bRes) < 0
              }
          }
          .map(_._1)

        VList(out*)
      case None =>
        VList(
          iterable.zipWithIndex
            .sorted { (a, b) =>
              MiscHelpers.compare(
                key.executeResult(a(0), a(1), List(a(0))),
                key.executeResult(b(0), b(1), List(b(0)))
              )
            }
            .map(_._1)*
        )

  end sortBy

  def sum(lst: VList)(using ctx: Context): VAny =
    if lst.isEmpty then ctx.settings.defaultValue else lst.reduce(_ +~ _)

  def prefixes(iterable: VList): Seq[VList] =
    iterable.inits.toSeq.reverse.tail

  def reduce(iter: VAny, by: VFun, init: Option[VAny] = None)(using
      Context
  ): VAny =
    var remaining = ListHelpers.makeIterable(iter, Some(true)).toList

    // Convert niladic + monadic functions to be dyadic for reduction purposes
    val byFun = by.withArity(if by.arity < 2 then 2 else by.arity)

    // Take the first byFun.arity items as the initial set to operate on
    var operating = init match
      case Some(elem) => elem +: remaining.take(byFun.arity - 1)
      case None => remaining.take(byFun.arity)
    remaining = remaining.drop(operating.length)

    if operating.isEmpty then return 0
    if operating.sizeIs == 1 then return operating.head

    var current = operating(0)
    var previous = operating(1)

    while remaining.length + operating.length != 1 do
      val result = byFun.execute(previous, current, args = operating.reverse)
      previous = remaining.headOption.getOrElse(result)
      current = result
      operating = result +: remaining.take(byFun.arity - 1)
      remaining = remaining.drop(byFun.arity - 1)

    current
  end reduce

  /** Reverse a VAny - if it's a list, reverse the list, if it's a string,
    * reverse the string, if it's a number, reverse the number. Different to the
    * VList.reverse method because this preserves the type of the input.
    * @param iterable
    * @return
    *   The reversed iterable
    */
  def reverse(iterable: VAny): VAny =
    iterable match
      case list: VList => VList(list.reverse*)
      case str: String => str.reverse
      case num: VNum => VNum(num.toString.reverse)
      case _ => iterable

  /** Split a list on a sublist
    *
    * @param sep
    *   The separator to split on
    * @return
    *   A Seq of all the sublists between occurrences of `sep`. If `sep` occurs
    *   at the very beginning of the list, the first element of the returned
    *   sequence will be an empty list. If `sep` occurs at the very end of the
    *   list, the last element of the returned sequence will be an empty list.
    */
  def split[T](list: Seq[T], sep: Seq[T]): Seq[Seq[T]] =
    val parts = ArrayBuffer.empty[Seq[T]]

    var lastInd = 0
    var sliceInd = list.indexOfSlice(sep)

    while sliceInd != -1 do
      parts += list.slice(lastInd, sliceInd)
      lastInd = sliceInd + sep.length
      sliceInd = list.indexOfSlice(sep, lastInd)

    parts += list.slice(lastInd, list.length)

    parts.toSeq

  def splitNormal(iterable: VList, sep: VAny)(using Context): VList =
    val out = split(iterable, Seq(sep))
    VList(out.map(VList.from)*)

  def transliterate(source: VList, from: VAny, to: VAny)(using
      ctx: Context
  ): VList =
    val fromList = ListHelpers.makeIterable(from)
    val toList = ListHelpers.makeIterable(to)

    val pairs = fromList.lazyZip(toList).toMap

    VList.from(source.map(x => pairs.getOrElse(x, x)))

  /** Transpose a matrix.
    *
    * Hangs on infinite lists of finite lists. See [[transposeSafe]] for a
    * version that handles those. Based on
    * [[https://github.com/Adriandmen/05AB1E/blob/master/lib/commands/matrix_commands.ex 05AB1E's implementation]].
    */
  def transpose(iterable: VList, filler: Option[VAny] = None)(using
      ctx: Context
  ): VList =
    val matrix = iterable.map(makeIterable(_))

    val out = filler match
      case None =>
        LazyList.unfold(matrix) { matrix =>
          val remaining = matrix.filter(_.nonEmpty)
          Option.when(remaining.nonEmpty) {
            val col = VList.from(remaining.map(_.head))
            (col, remaining.map(_.tail))
          }
        }
      case Some(filler) =>
        LazyList.unfold(matrix) { matrix =>
          Option.when(matrix.exists(_.nonEmpty)) {
            val col = VList.from(matrix.map(_.headOption.getOrElse(filler)))
            (col, matrix.map(_.tail))
          }
        }
    VList.from(out)
  end transpose

  /** Transpose a matrix. Uses the length of the first row of the inputted
    * matrix as the number of columns in the resulting matrix.
    *
    * @see
    *   transpose
    */
  def transposeSafe(iterable: VList, filler: Option[VAny] = None)(using
      ctx: Context
  ): VList =
    val matrix = iterable.map(makeIterable(_))

    if matrix.isEmpty then VList.empty
    else
      val out = filler match
        case None =>
          LazyList.unfold(matrix) { matrix =>
            Option.when(matrix.head.nonEmpty) {
              // The first row must be preserved so we know when to stop,
              // so it isn't included in the filter.
              val remaining = matrix.head +: matrix.tail.filter(_.nonEmpty)
              val col = VList.from(remaining.map(_.head))
              (col, remaining.map(_.tail))
            }
          }
        case Some(filler) =>
          LazyList.unfold(matrix) { matrix =>
            Option.when(matrix.head.nonEmpty) {
              val col = VList.from(matrix.map(_.headOption.getOrElse(filler)))
              (col, matrix.map(_.tail))
            }
          }
      VList.from(out)
    end if
  end transposeSafe

  def wrapLength(iterable: VList, length: VNum): VList =
    if length <= 0 then VList.empty
    else
      val temp = LazyList.unfold(iterable) { remaining =>
        if remaining.isEmpty then None
        else
          val chunk = List.newBuilder[VAny]
          var mutRemaining = remaining
          var count = 0
          while mutRemaining.nonEmpty && count < length do
            chunk += mutRemaining.head
            mutRemaining = mutRemaining.tail
            count += 1
          Some((VList.from(chunk.result()), mutRemaining))
      }
      VList.from(temp)

  def vectorisedMaximum(iterable: VList, b: VVal)(using Context): VList =
    VList.from(iterable.map { a =>
      (a: @unchecked) match
        case a: VList => vectorisedMaximum(a, b)
        case a: VVal => MiscHelpers.dyadicMaximum(a, b)
    })

  def vectorisedMinimum(iterable: VList, b: VVal)(using Context): VList =
    VList.from(iterable.map { a =>
      (a: @unchecked) match
        case a: VList => vectorisedMinimum(a, b)
        case a: VVal => MiscHelpers.dyadicMinimum(a, b)
    })

  def gradeUp(iterable: VAny)(using Context): VList =
    VList.from(makeIterable(iterable).zipWithIndex.sortBy(_._1).map(_._2))

  def partitionAfterTruthyIndices(lst: VAny, part: VAny)(using Context): VList =
    var res = ArrayBuffer(VList())
    val a: List[VAny] = List(makeIterable(lst)*)
    val b: List[VAny] = List(makeIterable(part)*)
    for (i, j) <- a.zip(b) do
      res(res.length - 1) =
        VList.from(res(res.length - 1) :+ i.asInstanceOf[VAny])
      if j.asInstanceOf[VAny].toBool then res = res :+ VList()
    VList.from(res.toList)

end ListHelpers
