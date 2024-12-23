package vyxal

import vyxal.conversions.{*, given}

import scala.annotation.unchecked.uncheckedVariance
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scala.collection.mutable as mut

object ListHelpers:

  def assign(iterable: Seq[VAny], index: VNum, value: VAny): Seq[VAny] =
    val ind = if index < 0 then iterable.bigLength + index else index
    val temp = iterable.extend(ind.toBigInt, VNum(0))
    temp.take(ind) ++ (value +: temp.drop(ind + 1))

  def augmentAssign(iterable: Seq[VAny], index: VNum, function: VFun)(using
      ctx: Context
  ): Seq[VAny] =
    val ind = if index < 0 then iterable.bigLength + index else index
    val temp = iterable.extend(ind.toBigInt, VNum(0))
    val item = iterable.index(ind)
    ctx.push(item)
    val res = Interpreter.executeFn(
      function,
      ctxVarPrimary = item,
      ctxVarSecondary = index,
    )
    temp.take(ind) ++ (res +: temp.drop(ind + 1))

  def cartesianPower(lhs: VAny, pow: VNum)(using Context): Seq[VAny] =
    if pow == VNum(0) then Seq(Seq())
    else
      val lst = makeIterable(lhs)
      val temp = cartesianProductMultiSeqs(Seq.fill(pow.toInt)(lst))
      lhs match
        case _: VStr => temp.map(_.mkString)
        case _ => temp.map(VList(_))

  def cartesianProductSeqs[T <: VAny](
      left: Seq[T],
      right: Seq[T],
  ): Seq[Seq[T]] = left.flatMap(l => right.map(r => Seq(l, r)))

  /** Cartesian product */
  def cartesianProduct(left: VAny, right: VAny, unsafe: Boolean = false)(using
      ctx: Context
  ): Seq[Seq[VAny]] =
    val lhs = makeIterable(left, Some(true))
    val rhs = makeIterable(right, Some(true))

    if unsafe || (lhs.isDefFinite && rhs.isDefFinite) then
      cartesianProductSeqs(lhs, rhs)
    else mergeInfLists(lhs.map(l => rhs.map(r => Seq(l, r))))

  def cartesianProductMultiSeqs[T <: VAny](lists: Seq[Seq[T]]): Seq[Seq[T]] =
    lists.foldRight(Seq(Seq.empty[T])) { (lst, acc) =>
      for (l <- lst; r <- acc) yield l +: r
    }

  def combinations(
      iterable: Seq[VAny],
      size: VNum,
      withReplacement: Boolean = false,
  ): Seq[VAny] =
    if withReplacement then combinationsWithReplacement(iterable, size).vs
    else combinationsWithoutReplacement(iterable, size).vs

  def combinationsWithReplacement(list: Seq[VAny], n: VNum): Seq[Seq[VAny]] =
    if n == VNum(0) then Seq(Seq()) // Base case: one combination of size 0
    else
      for
        (head, index) <- list.zipWithIndex
        tail <- combinationsWithReplacement(list, n - 1)
      yield head +: tail

  def combinationsWithoutReplacement(list: Seq[VAny], n: VNum): Seq[Seq[VAny]] =
    if n == VNum(0) then Seq(Seq()) // Base case: one combination of size 0
    else
      for
        (head, index) <- list.zipWithIndex
        // Exclude current and previous elements
        tail <- combinationsWithoutReplacement(list.drop(index + 1), n - 1)
      yield head +: tail

  def countDepth(left: Seq[VAny], right: Seq[VAny])(using Context): VNum =
    val Seq(needle, haystack) = Seq(left, right).sortBy(maxDepth)
    haystack.count(needle === _)

  /** Remove items that are duplicates after transforming by `fn` */
  def dedupBy(iterable: Seq[VAny], fn: VFun)(using Context): Seq[VAny] =
    // Can't use a Set here because equal VNums don't hash to the same value
    val seen = mut.ArrayBuffer.empty[VAny]
    iterable.filter { item =>
      val res = fn.execute(item, 0, List(item))
      if seen.contains(res) then false
      else
        seen += res
        true
    }

  /** Matrix determinant */
  def determinant(mat: Seq[Seq[VNum]]): VNum =
    if mat.isEmpty then 0
    else if mat.size == 1 then mat.head.head
    else
      val restRows = mat.tail
      mat.head.zipWithIndex.map { (elem, c) =>
        val minor = restRows.map(row => row.take(c) ++ row.drop(c + 1))
        val sign = if c % 2 == 0 then 1 else -1
        sign * elem * determinant(minor)
      }.sum

  def dotProduct(left: Seq[VAny], right: Seq[VAny])(using Context): VAny =
    left *~ right match
      case VList(l) => ListHelpers.sum(l)
      case x => x

  def drop(iterable: Seq[VAny], index: VNum): Seq[VAny] =
    val ind = if index < 0 then iterable.bigLength + index else index
    iterable.drop(ind)

  def drop(iterable: Seq[VAny], shape: Seq[VNum])(using Context): Seq[VAny] =
    if shape.isEmpty then iterable
    else if shape.length == 1 then drop(iterable, shape.head)
    else
      drop(iterable, shape.head).map { row =>
        drop(makeIterable(row), shape.tail)
      }

  def filter(iterable: Seq[VAny], predicate: VFun)(using Context): Seq[VAny] =
    predicate.originalAST match
      case Some(lam) =>
        val branches = lam.body
        val filtered = iterable.zipWithIndex.filter { (item, index) =>
          var keep = true
          var branchList = branches
          val sharedVars = mut.Map.empty[String, VAny]

          while branchList.nonEmpty && keep do
            val fun = VFun.fromLambda(
              AST.Lambda(Some(1), List.empty, List(branchList.head))
            )
            val res = Interpreter.executeFn(
              fun,
              ctxVarPrimary = item,
              ctxVarSecondary = index,
              args = List(item),
              vars = sharedVars,
            )
            keep = res.toBool
            branchList = branchList.tail

          keep
        }

        filtered.map(_._1)
      case None => iterable.zipWithIndex.collect {
          case (item, index)
              if predicate.execute(item, index, List(item)).toBool => item
        }

  end filter

  def flatten(xs: Seq[VAny]): Seq[VAny] =
    xs.flatMap {
      case VList(l) => flatten(l)
      case x => Seq(x)
    }

  def flattenByDepth(iterable: Seq[VAny], depth: VNum)(using
      Context
  ): Seq[VAny] =
    if depth == VNum(0) then iterable
    else
      iterable.flatMap {
        case VList(l) => flattenByDepth(l, depth - 1)
        case x => Seq(x)
      }

  /** A wrapper call to the generator method in interpreter */
  def generate(function: VFun, initial: Seq[VAny])(using
      ctx: Context
  ): Seq[VAny] =
    val firstN = initial.length match
      case 0 => ctx.settings.defaultValue
      case 1 => initial.head
      case _ => initial.last

    val firstM = initial.length match
      case 0 => ctx.settings.defaultValue
      case 1 => initial.head
      case _ => initial.init.last

    initial ++:
      Interpreter.generator(function, firstN, firstM, function.arity, initial)

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
  def generateDyadic(function: VFun, initial: Seq[VAny])(using
      ctx: Context
  ): Seq[VAny] =
    val firstN =
      if initial.isEmpty then ctx.settings.defaultValue else initial.last

    val firstM = initial.length match
      case 0 => ctx.settings.defaultValue
      case 1 => initial.head
      case _ => initial.init.last

    initial ++: Interpreter.generator(function, firstN, firstM, 2, initial)

  private def neighbourDirections =
    Seq(
      (1, 0, 'r') ->
        ((row: Int, col: Int, matrix: Seq[VAny], matRow: Seq[VAny]) =>
          row < matrix.length - 1
        ),
      (0, -1, 'c') ->
        ((row: Int, col: Int, matrix: Seq[VAny], matRow: Seq[VAny]) => col > 0),
      (-1, 0, 'r') ->
        ((row: Int, col: Int, matrix: Seq[VAny], matRow: Seq[VAny]) => row > 0),
      (0, 1, 'c') ->
        ((row: Int, col: Int, matrix: Seq[VAny], matRow: Seq[VAny]) =>
          col < matRow.length - 1
        ),
      (1, 1, 'c') ->
        ((row: Int, col: Int, matrix: Seq[VAny], matRow: Seq[VAny]) =>
          col < matRow.length - 1 && row < matrix.length - 1
        ),
      (-1, -1, 'c') ->
        ((row: Int, col: Int, matrix: Seq[VAny], matRow: Seq[VAny]) =>
          col > 0 && row > 0
        ),
      (1, -1, 'c') ->
        ((row: Int, col: Int, matrix: Seq[VAny], matRow: Seq[VAny]) =>
          col > 0 && row < matrix.length - 1
        ),
      (-1, 1, 'c') ->
        ((row: Int, col: Int, matrix: Seq[VAny], matRow: Seq[VAny]) =>
          col < matRow.length - 1 && row > 0
        ),
    )

  def gridNeighbours(
      matrix: Seq[VAny],
      includeCell: Boolean = false,
      directionOffset: Int = 0,
  )(using Context): Seq[VAny] =
    val temp = matrix.zipWithIndex.map { (row, r) =>
      VList(makeIterable(row).zipWithIndex.map { (_, c) =>
        val neighbours = ArrayBuffer.empty[VAny]
        val directions = neighbourDirections.drop(directionOffset) ++
          neighbourDirections.take(directionOffset)
        for
          (dir, check) <- directions
          if dir(0).abs != dir(1).abs
        do
          val (dr, dc, dimension) = dir
          if dimension == 'r' then
            if check(r, c, matrix, Seq.empty) then
              neighbours += makeIterable(matrix.index(r + dr)).index(c + dc)
          else if check(r, c, matrix, makeIterable(row)) then
            neighbours += makeIterable(matrix.index(r + dr)).index(c + dc)
        if includeCell then neighbours += makeIterable(matrix.index(r)).index(c)
        VList(neighbours.toList)
      })
    }
    temp
  end gridNeighbours

  def gridNeighboursWrap(
      matrix: Seq[VAny],
      includeCell: Boolean = false,
      directionOffset: Int = 0,
  )(using Context): Seq[VAny] =
    val temp = matrix.zipWithIndex.map { (row, r) =>
      VList(makeIterable(row).zipWithIndex.map { (_, c) =>
        val neighbours = ArrayBuffer.empty[VAny]
        val directions = neighbourDirections.drop(directionOffset) ++
          neighbourDirections.take(directionOffset)
        for
          (dir, _) <- directions
          if dir(0).abs != dir(1).abs
        do
          neighbours +=
            makeIterable(matrix.index((r + dir(0)))).index(
              (c + dir(1))
            )
        if includeCell then neighbours += makeIterable(matrix.index(r)).index(c)
        VList(neighbours.toList)
      })
    }

    temp
  end gridNeighboursWrap

  def gridNeighboursDiagonal(
      matrix: Seq[VAny],
      includeCell: Boolean = false,
      directionOffset: Int = 0,
  )(using Context): Seq[VAny] =
    val temp = matrix.zipWithIndex.map { (row, r) =>
      VList(makeIterable(row).zipWithIndex.map { (_, c) =>
        val neighbours = ArrayBuffer.empty[VAny]
        val directions = neighbourDirections.drop(directionOffset) ++
          neighbourDirections.take(directionOffset)
        for (dir, check) <- directions do
          val (dr, dc, dimension) = dir
          if dimension == 'r' then
            if check(r, c, matrix, Seq()) then
              neighbours += makeIterable(matrix.index(r + dr)).index(c + dc)
          else if check(r, c, matrix, makeIterable(row)) then
            neighbours += makeIterable(matrix.index(r + dr)).index(c + dc)

        if includeCell then neighbours += makeIterable(matrix.index(r)).index(c)
        VList(neighbours.toList)
      })
    }
    temp
  end gridNeighboursDiagonal

  def gridNeighboursDiagonalWrap(
      matrix: Seq[VAny],
      includeCell: Boolean = false,
      directionOffset: Int = 0,
  )(using Context): Seq[VAny] =
    val temp = matrix.zipWithIndex.map { (row, r) =>
      VList(makeIterable(row).zipWithIndex.map { (_, c) =>
        val neighbours = ArrayBuffer.empty[VAny]
        val directions = neighbourDirections.drop(directionOffset) ++
          neighbourDirections.take(directionOffset)
        for (dir, _) <- directions do
          val (dr, dc, _) = dir
          neighbours +=
            makeIterable(matrix.index((r + dr))).index(
              (c + dc)
            )
        if includeCell then neighbours += makeIterable(matrix.index(r)).index(c)
        VList(neighbours.toList)
      })
    }

    temp
  end gridNeighboursDiagonalWrap

  /** Group elements according to the result of some function
    * @return
    *   A VList where each element is again a VList containing a group of
    *   elements that all had the same result when `fn` was applied to them
    */
  def groupBy(iterable: Seq[VAny], fn: VFun)(using Context): Seq[VAny] =
    // TODO Make this work on lazylists? Doable but extremely hard
    val nonNumGroups = mut.Map.empty[VAny, ArrayBuffer[VAny]]
    // VNums can't be used as HashMap keys so we need a separate list for them
    val numGroups = ArrayBuffer.empty[(VAny, ArrayBuffer[VAny])]
    for elem <- iterable do
      fn(elem) match
        case n: VNum => numGroups.find((key, _) => key == n) match
            case Some((_, group)) => group += elem
            case _ => numGroups += ((n, ArrayBuffer(elem)))
        case res =>
          if nonNumGroups.contains(res) then nonNumGroups(res) += elem
          else nonNumGroups(res) = ArrayBuffer(elem)
    (nonNumGroups.view ++ numGroups.view)
      .map((_, group) => VList(group.toSeq))
      .toSeq
  end groupBy

  def groupConsecutive(iterable: Seq[VAny]): Seq[VAny] =
    groupByConsecutive(iterable, x => x)

  def groupByConsecutive(
      iterable: Seq[VAny],
      function: VAny => VAny,
  ): Seq[VAny] =
    val it = iterable.iterator
    def gen(first: VAny): LazyList[VList] =
      val buf = ListBuffer(first)
      val transformed = function(first)
      while it.hasNext do
        val next = it.next()
        if function(next) == transformed then buf.append(next)
        else return VList(buf.toList) #:: gen(next)
      LazyList(VList(buf.toList))

    if it.hasNext then gen(it.next()) else Seq.empty

  def groupByConsecutive(iterable: VAny, function: VFun)(using
      Context
  ): Seq[VAny] =
    val it = makeIterable(iterable).iterator
    def gen(first: VAny): LazyList[VList] =
      val buf = ListBuffer(first)
      val transformed = function.execute(first, 0, List(first))
      while it.hasNext do
        val next = it.next()
        if function.execute(next, 0, List(next)) == transformed then
          buf.append(
            next
          )
        else return VList(buf.toList) #:: gen(next)
      LazyList(VList(buf.toList))

    val res = if it.hasNext then gen(it.next()) else Seq.empty
    iterable match
      case VStr(_) => res.map(_.asInstanceOf[VList].mkString)
      case _ => res
  end groupByConsecutive

  def insert(iterable: Seq[VAny], index: VNum, value: VAny)(using
      Context
  ): Seq[VAny] =
    val ind = if index < 0 then iterable.bigLength + index + 1 else index
    val temp = iterable.extend(ind.toBigInt, VNum(0))
    temp.take(ind) ++ (value +: temp.drop(ind))

  def interleave(left: Seq[VAny], right: Seq[VAny])(using Context): Seq[VAny] =
    val out = ArrayBuffer.empty[VAny]
    val leftIter = left.iterator
    val rightIter = right.iterator
    while leftIter.hasNext && rightIter.hasNext do
      out += leftIter.next()
      out += rightIter.next()

    out ++= leftIter
    out ++= rightIter

    out.toSeq

  def intoNPieces(iterable: Seq[VAny], pieces: VNum)(using
      Context
  ): Seq[Seq[VAny]] =
    if pieces == VNum(0) then return Seq.empty
    if iterable.isEmpty then return Seq.empty
    val size = iterable.length
    val pieceSize = (size / pieces).floor
    var remaining = iterable
    val out = ListBuffer.empty[Seq[VAny]]
    while remaining.length >= pieceSize do
      val (thisPiece, rest) =
        (remaining.take(pieceSize), remaining.drop(pieceSize))
      out += thisPiece
      remaining = rest
    if remaining.nonEmpty then out += remaining
    out.toSeq

  /** Join a list on a string/number, or intersperse a list within `lst` */
  def join(lst: Seq[VAny], sep: VAny)(using Context): VAny =
    sep match
      case VStr(s) => lst.mkString(s)
      case sep: (VNum | VList) =>
        val l = sep match
          case VList(l) => l
          case sep => Seq(sep)
        VList(
          lst.map(makeIterable(_)).reduce((ret, item) => ret ++ l ++ item)
        )
      case _ => ??? // todo reduce?

  def matrixInverse(lst: Seq[VAny])(using Context): Option[VList] =
    validateMatrix(lst).flatMap { mat =>
      val det = determinant(mat)
      if det === 0 then None
      else
        val size = mat.size
        Some(VList((0 until size).map { c =>
          VList((0 until size).map { r =>
            val minor = matrixMinor(mat, r, c)
            val sign = if (r + c) % 2 == 0 then 1 else -1
            sign * determinant(minor) / det
          })
        }))
    }

  /** Make an iterable from a value
    *
    * @param value
    *   The value to make an iterable from
    * @param overrideRangify
    *   Whether to rangify (optional). If given, overrides
    *   `ctx.settings.rangify`
    * @return
    */
  def makeIterable(value: VAny, overrideRangify: Option[Boolean] = None)(using
      ctx: Context
  ): Seq[VAny] =
    value match
      case VList(list) => list
      case VStr(str) => str.map(c => VStr(c.toString))
      case fn: VFun => Seq(fn)
      case num: VNum =>
        if overrideRangify.getOrElse(ctx.settings.rangify) then
          val start = ctx.settings.rangeStart
          val offset = ctx.settings.rangeOffset
          start.range(num - offset)
        else
          num.toString.map { x =>
            if x.isDigit then VNum(x - '0')
            else if x == 'ı' || x == 'i' then VNum.complex(0, 1)
            else VStr(x.toString)
          }
      case _ => throw IterificationOfNonIterableException(value)

  def matrixMinor(mat: Seq[Seq[VNum]], r: Int, c: Int): Seq[Seq[VNum]] =
    (mat.take(r) ++ mat.drop(r + 1)).map(row => row.take(c) ++ row.drop(c + 1))

  def matrixMultiply(lhs: Seq[VAny], rhs: Seq[VAny])(using Context): Seq[VAny] =
    val rhsTemp = transposeSafe(rhs)
    lhs.map { row =>
      val rowIt = ListHelpers.makeIterable(row)
      VList(
        rhsTemp.map(col => dotProduct(rowIt, ListHelpers.makeIterable(col)))
      )
    }

  def map(f: VFun, to: Seq[VAny])(using Context): Seq[VAny] =
    f.originalAST match
      case Some(lam) =>
        val branches = lam.body
        val params = f.originalAST match
          case Some(lam) => lam.params
          case None => List.empty
        to.zipWithIndex.map { (item, index) =>
          val sharedVars = mut.Map.empty[String, VAny]
          branches.foldLeft(item) { (out, branch) =>
            Interpreter.executeFn(
              VFun.fromLambda(AST.Lambda(Some(1), params, List(branch))),
              ctxVarPrimary = out,
              ctxVarSecondary = index,
              args = List(out),
              vars = sharedVars,
            )
          }
        }

      case None => to.zipWithIndex.map { (item, index) =>
          f.execute(item, index, List(item))
        }
  end map

  def maxDepth(iter: Seq[VAny])(using Context): VNum =
    iter
      .map {
        case VList(s) => 1 + maxDepth(s)
        case _ => VNum(1)
      }
      .foldLeft(VNum(0))(
        MiscHelpers.dyadicMaximum(_, _).asInstanceOf[VNum]
      ) // Guaranteed to be a VNum

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
  def mold(content: Seq[VAny], shape: Seq[VAny])(using Context): VList =
    def moldHelper(content: Seq[VAny], shape: Seq[VAny], ind: Int): VList =
      val output = ArrayBuffer.empty[VAny]
      val mutContent = content
      val mutShape = shape.toList
      var index = ind
      for item <- mutShape do
        item match
          case VList(item) =>
            output += moldHelper(mutContent, item, index)
            output.last match
              case list: VList => index += list.length - 1
              case _ => index += 1
          case item => output += mutContent.index(index)
        index += 1

      VList(output.toSeq)
    end moldHelper
    moldHelper(content, shape, 0)
  end mold

  def multiDimAssign(iterable: Seq[VAny], indices: Seq[VAny], value: VAny)(using
      Context
  ): Seq[VAny] =
    if !indices.forall(_.isInstanceOf[VNum]) then
      value match
        case v: VList =>
          var out = iterable
          for (index, subvalue) <- indices.zip(v) do
            out = multiDimAssign(out, makeIterable(index), subvalue)
          return out
        case _ =>
          var temp = iterable
          for index <- indices do
            temp = multiDimAssign(temp, makeIterable(index), value)
          temp
    else
      if indices.isEmpty then return iterable
      // Move down the list of indices, assigning the value at the last index
      val dimensionItems = ListBuffer[Seq[VAny]](iterable)

      for index <- indices.init do
        dimensionItems +=
          makeIterable(makeIterable(dimensionItems.last).index(index))

      var out =
        assign(dimensionItems.last, indices.last.asInstanceOf[VNum], value)
      dimensionItems.dropRightInPlace(1)
      for index <- indices.init.reverse do
        out = assign(dimensionItems.last, index.asInstanceOf[VNum], out)
        dimensionItems.dropRightInPlace(1)
      out

  def multiDimIndex(iterable: Seq[VAny], indices: Seq[VAny])(using
      Context
  ): VAny =
    if !indices.forall(_.isInstanceOf[VNum]) then
      VList(
        indices.map(index => multiDimIndex(iterable, makeIterable(index)))
      )
    else
      var temp = iterable
      for ind <- indices.init do
        temp = makeIterable(makeIterable(temp).index(ind))
      temp.index(indices.last)

  def multiDimIndexNoWrap(iterable: Seq[VAny], indices: Seq[VNum])(using
      Context
  ): VAny =

    var temp = iterable
    var doesNotExist = false
    for ind <- indices.init do
      if !doesNotExist && temp.hasIndex(ind.toBigInt) then
        temp = makeIterable(makeIterable(temp).index(ind))
      else doesNotExist = true
    if doesNotExist || !temp.hasIndex(indices.last.toBigInt) then
      null // *gasp* null! In a 2024 codebase! The horror!
    // I needed a way to signal that the index doesn't exist
    // that also plays nice with typing
    else temp.index(indices.last)

  def multiSetIntersection(left: Seq[VAny], right: Seq[VAny]): Seq[VAny] =
    val out = ListBuffer.empty[VAny]
    var rightMut = right
    for item <- left do
      if rightMut.contains(item) then
        out += item
        rightMut = rightMut.indexOf(item) match
          case -1 => rightMut
          case ind => rightMut.take(ind) ++ rightMut.drop(ind + 1)
    out.toSeq

  def nthItems(iterable: VList | VStr, index: VNum): VAny =
    val temp = iterable match
      case VStr(s) => s.map(c => VStr(c.toString))
      case VList(l) => l

    val indInt = index.toInt
    val value =
      if indInt == 0 then temp ++ temp.reverse
      else
        temp.zipWithIndex.collect {
          case (elem, ind) if ind % indInt == 0 => elem
        }
    iterable match
      case _: VList => VList(value)
      case _: VStr => value.mkString

  def overlaps(iterable: Seq[VAny], size: Int): Seq[Seq[VAny]] =
    size compare 0 match
      case 0 => Seq.empty
      case 1 => LazyList.from(iterable.sliding(size))
      case -1 => iterable.sliding(-size).toSeq.reverse

  // Just for strings
  def overlaps(iterable: String, size: Int): Seq[String] =
    size compare 0 match
      case 0 => Seq.empty
      case 1 => LazyList.from(iterable.sliding(size))
      case -1 => iterable.sliding(-size).toSeq.reverse

  // multi-dimensional overlaps
  def overlapsMd(iterable: Seq[VAny], shape: Seq[VNum]): Seq[VAny] =
    if shape.isEmpty then VList(iterable)
    else if shape.length == 1 then overlaps(iterable, shape.head.toInt).vs
    else windows(iterable, shape)

  def windows(iter: Seq[VAny], winSize: Seq[VNum]): Seq[VAny] =
    val iterShape = shapeOf(iter).take(winSize.length).map(_.asInstanceOf[VNum])
    val ends = iterShape
      .zip(winSize)
      .map((length, size) => if size > length then length else length - size)
      .map(end => 0.range(end))

    val windowOrigins = cartesianProductMultiSeqs(ends)

    windowOrigins.map(start => windowAt(iter, start, winSize))

  def windowAt(
      iterable: Seq[VAny],
      origin: Seq[VNum],
      size: Seq[VNum],
  ): Seq[VAny] =
    val includedDimensions = origin
      .zip(size)
      .map((start, length) =>
        start.range(start + length - 1).map(_.asInstanceOf[VNum])
      )
    val inWindow = cartesianProductMultiSeqs(includedDimensions)
    val ctx = Context()
    val cells =
      inWindow.map(coords => multiDimIndexNoWrap(iterable, coords)(using ctx))

    val reshaped = reshape(cells, size)
    // Remove any nulls that were inserted by multiDimIndexNoWrap
    def removeNulls(lst: Seq[VAny]): Seq[VAny] =
      lst.filter(_ != null).map {
        case VList(l) => removeNulls(l)
        case x => x
      }

    removeNulls(makeIterable(reshaped)(using ctx))
  end windowAt

  def palindromise(lst: Seq[VAny]): Seq[VAny] = lst ++ lst.reverse.tail

  def palindromise(str: String): String = str + str.reverse.tail

  def palindromise(num: VNum): VNum =
    val str = num.toString
    VNum(str + str.reverse.tail)

  /** List partitions (like set partitions, but contiguous sublists) */
  def partitions(lst: Seq[VAny])(using Context): Seq[Seq[Seq[VAny]]] =
    if !lst.isDefFinite then
      // Possibly infinite
      partitionsLazy(lst)
    else
      // Forces evaluation of the list because we need to know the length
      val shapes = NumberHelpers.partitions(lst.size).flatMap { partition =>
        val shape = partition
          .map(v => VList(Seq.fill(v.asInstanceOf[VNum].toInt)(VNum(1))))
        shape.permutations.toSeq
      }

      val uniqueShapes = ListBuffer[VList]()
      for shape <- shapes do
        if !uniqueShapes.exists(_ === shape) then uniqueShapes += shape

      uniqueShapes
        .map(shape => mold(lst, shape).map(_.asInstanceOf[VList].lst))
        .toSeq
    end if
  end partitions

  /** A version of [[partitions]] that hopefully works with infinite lists */
  private def partitionsLazy(lst: Seq[VAny]): Seq[Seq[Seq[VAny]]] =
    def helper(lst: Seq[VAny]): LazyList[LazyList[Seq[VAny]]] =
      if lst.isEmpty then LazyList.empty
      else
        LazyList(lst) #::
          mergeInfLists(
            LazyList.from(1).takeWhile(i => lst.sizeIs > i).map { i =>
              val (left, right) = lst.splitAt(i)
              helper(right).map(partition => left #:: partition)
            }
          )
    helper(lst)

  def partitionBy(lst: Seq[VAny], shapes: Seq[VNum])(using Context): Seq[VAny] =
    val shapeSublists = shapes.map(x => VList(Seq.fill(x.toInt)(1)))
    mold(lst, shapeSublists)

  def permutations(iterable: Seq[VAny]): Seq[VList] =
    val temp = iterable.toList
    val perms = temp.permutations
    LazyList.from(perms.map(VList(_)))

  def product(iterable: Seq[VAny])(using Context): VAny =
    if iterable.forall(_.isInstanceOf[VNum])
    then iterable.fold(VNum(1))(_.asInstanceOf[VNum] * _.asInstanceOf[VNum])
    else
      // Cartesian product over the list
      val temp = iterable.map(ListHelpers.makeIterable(_))
      VList(cartesianProductMultiSeqs(temp).vs)

  def setIntersection(left: Seq[VAny], right: Seq[VAny]): Seq[VAny] =
    val result = LazyList
      .unfold(
        (left, right, (List.empty[VAny], List.empty[VAny]), ListBuffer[VAny]())
      ) {
        case (left, right, (leftGenerated, rightGenerated), inBoth) =>
          if left.isEmpty && right.isEmpty then None
          else
            val leftGen =
              if left.nonEmpty then leftGenerated :+ left.head
              else leftGenerated
            val rightGen =
              if right.nonEmpty then rightGenerated :+ right.head
              else rightGenerated
            val thisReturn = ListBuffer.empty[VAny]
            if left.nonEmpty then
              if rightGen.contains(left.head) && !inBoth.contains(left.head)
              then
                inBoth += left.head
                thisReturn += left.head
            if right.nonEmpty then
              if leftGen.contains(right.head) && !inBoth.contains(right.head)
              then
                inBoth += right.head
                thisReturn += right.head
            Some(
              thisReturn.toList ->
                (
                  if left.nonEmpty then VList(left.tail) else Seq.empty,
                  if right.nonEmpty then VList(right.tail) else Seq.empty,
                  (leftGen, rightGen),
                  inBoth,
                )
            )
      }
      .flatten
    result
  end setIntersection

  def sortBy(iterable: Seq[VAny], key: VFun)(using Context): Seq[VAny] =
    key.originalAST match
      case Some(lam) =>
        val branches = lam.body
        if branches.sizeIs < 2 then
          return iterable.zipWithIndex
            .sorted { (a, b) =>
              MiscHelpers.compare(
                key.executeResult(a(0), a(1), List(a(0))),
                key.executeResult(b(0), b(1), List(b(0))),
              )
            }
            .map(_._1)

        iterable.zipWithIndex
          .sortWith { (a, b) =>
            branches.view
              .map { branch =>
                val f =
                  VFun.fromLambda(AST.Lambda(Some(1), List.empty, List(branch)))
                (
                  f.execute(a(0), a(1), List(a(0))),
                  f.execute(b(0), b(1), List(b(0))),
                )
              }
              .find(_ != _)
              // If they compare equal with all branches, a < b is false
              .fold(false) {
                case (aRes, bRes) => MiscHelpers.compare(aRes, bRes) < 0
              }
          }
          .map(_._1)
      case None => iterable.zipWithIndex
          .sorted { (a, b) =>
            MiscHelpers.compare(
              key.executeResult(a(0), a(1), List(a(0))),
              key.executeResult(b(0), b(1), List(b(0))),
            )
          }
          .map(_._1)

  end sortBy

  def sum(lst: Seq[VAny])(using ctx: Context): VAny =
    if lst.isEmpty then ctx.settings.defaultValue else lst.reduce(_ +~ _)

  def prefixes(iterable: Seq[VAny]): Seq[VList] =
    val prefix = ListBuffer.empty[VAny]
    LazyList.unfold(iterable) { remaining =>
      Option.when(remaining.nonEmpty) {
        prefix += remaining.head
        (VList(prefix.toList), remaining.tail)
      }
    }

  def suffixes(iterable: Seq[VAny]): Seq[Seq[VAny]] =
    LazyList.unfold(iterable) { remaining =>
      Option.when(remaining.nonEmpty) {
        (remaining, remaining.tail)
      }
    }

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

  def reshape(iterable: Seq[VAny], shape: Seq[VNum]): VAny =
    if iterable.isEmpty then throw BadArgumentException("reshape", iterable)
    var iterator = iterable.iterator
    def nextElement(): VAny =
      if iterator.hasNext then iterator.next()
      else
        iterator = iterable.iterator
        iterator.next()

    def go(shape: Seq[Int]): VAny =
      if shape.isEmpty then iterable(0)
      else if shape.length == 1 then Seq.fill(shape.head)(nextElement())
      else Seq.fill(shape.head)(go(shape.tail))
    go(shape.map(_.toInt))

  /** Reverse a VAny - if it's a list, reverse the list, if it's a string,
    * reverse the string, if it's a number, reverse the number. Different to the
    * VList.reverse method because this preserves the type of the input.
    * @param iterable
    * @return
    *   The reversed iterable
    */
  def reverse(iterable: VAny): VAny =
    iterable match
      case list: VList => VList(list.reverse)
      case VStr(str) => str.reverse
      case num: VNum => VNum(num.toString.reverse)
      case _ => iterable

  /** Rotate a list by a given amount. Positive amounts rotate left, negative
    * amounts rotate right.
    *
    * @param iterable
    *   The list to rotate
    * @param amount
    *   The amount to rotate by
    * @return
    *   The rotated list
    */
  def rotate(iterable: VAny, amount: VNum): VAny =
    var counter = 0
    val direction = if amount < 0 then -1 else 1 // -1 for right, 1 for left
    val amountInt = amount.vabs
    var temp = iterable

    while counter < amountInt do
      temp = temp match
        case list: VList =>
          if direction == 1 then VList(list.tail :+ list.head)
          else VList(list.last +: list.init)
        case VStr(str) =>
          if direction == 1 then str.tail + str.head
          else s"${str.last}${str.init}"
        case num: VNum =>
          val str = num.toString
          if direction == 1 then VNum(str.tail + str.head)
          else VNum(s"${str.last}${str.init}")
        case _ => throw BadArgumentException("rotate", iterable)
      counter += 1

    temp
  end rotate

  /** Get the shape of a VList, assuming padding (even though it can be rugged)
    * Requires a finite list of finite lists
    */
  def shapeOf(iterable: Seq[VAny]): Seq[VNum] =
    val shape = ArrayBuffer.empty[VNum]
    var temp = iterable
    while temp.nonEmpty do
      shape += VNum(temp.length)
      val items = temp.map { x =>
        x match
          case l: VList => (l.length, l)
          case _ => (0, VList(Seq.empty))
      }
      temp = items.maxBy(_._1)._2

    if shape.isEmpty then Seq(0) else shape.toSeq

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

  def splitNormal(iterable: Seq[VAny], sep: VAny)(using Context): Seq[VAny] =
    val out = split(iterable, Seq(sep))
    out.map(VList(_))

  // Assumes finite and non-empty lists
  // Also assumes that the haystack depth >= needle depth
  def sublistExists(
      haystack: (Seq[VAny], VNum),
      needle: (Seq[VAny], VNum, Seq[VNum]),
  ): VNum =
    val (hList, hDepth) = haystack
    val (nList, nDepth, nShape) = needle
    if hDepth > nDepth then
      hList.map {
        (_: @unchecked) match
          case VList(elem) => sublistExists((elem, hDepth - 1), needle)
      }.max
    else // Depth of needle == depth of haystack
      val overlaps = overlapsMd(hList, nShape)
      overlaps
        .map(sublist => sublist.asInstanceOf[VList].lst == nList)
        .count(_ == true)

  def take(iterable: Seq[VAny], amount: VNum): Seq[VAny] =
    if amount < 0 then iterable.takeRight(amount.toInt.abs)
    else iterable.take(amount.toInt)

  def take(iterable: Seq[VAny], amount: VNum, fill: VAny): Seq[VAny] =
    if amount < 0 then
      (List.fill(amount.toInt.abs)(fill) ++ iterable).takeRight(-amount.toInt)
    else (iterable ++ List.fill(amount.toInt)(fill)).take(amount.toInt)

  def take(iterable: Seq[VAny], shape: Seq[VNum])(using
      Context
  ): Seq[VAny] =
    if shape.isEmpty then iterable
    else if shape.length == 1 then take(iterable, shape.head, 0)
    else
      take(iterable, shape.head, 0).map { row =>
        val temp = makeIterable(row)
        take(temp, shape.tail)
      }

  def transliterate(source: Seq[VAny], from: VAny, to: VAny)(using
      ctx: Context
  ): Seq[VAny] =
    val fromList = ListHelpers.makeIterable(from)
    val toList = ListHelpers.makeIterable(to)
    val pairs = fromList.lazyZip(toList).toMap
    source.map(x => pairs.getOrElse(x, x))

  /** Transpose a matrix.
    *
    * Hangs on infinite lists of finite lists. See [[transposeSafe]] for a
    * version that handles those. Based on
    * [[https://github.com/Adriandmen/05AB1E/blob/master/lib/commands/matrix_commands.ex 05AB1E's implementation]].
    */
  def transpose(iterable: Seq[VAny], filler: Option[VAny] = None)(using
      ctx: Context
  ): Seq[VAny] =
    val matrix = iterable.map(makeIterable(_))

    val out = filler match
      case None => LazyList.unfold(matrix) { matrix =>
          val remaining = matrix.filter(_.nonEmpty)
          Option.when(remaining.nonEmpty) {
            val col = VList(remaining.map(_.head))
            (col, remaining.map(_.tail))
          }
        }
      case Some(filler) => LazyList.unfold(matrix) { matrix =>
          Option.when(matrix.exists(_.nonEmpty)) {
            val col = VList(matrix.map(_.headOption.getOrElse(filler)))
            (col, matrix.map(_.vTail))
          }
        }
    out
  end transpose

  /** Transpose a matrix. Uses the length of the first row of the inputted
    * matrix as the number of columns in the resulting matrix.
    *
    * @see
    *   transpose
    */
  def transposeSafe(iterable: Seq[VAny], filler: Option[VAny] = None)(using
      ctx: Context
  ): Seq[VAny] =
    val matrix = iterable.map(makeIterable(_))

    if matrix.isEmpty then Seq.empty
    else
      val out = filler match
        case None => LazyList.unfold(matrix) { matrix =>
            Option.when(matrix.head.nonEmpty) {
              // The first row must be preserved so we know when to stop,
              // so it isn't included in the filter.
              val remaining = matrix.head +: matrix.tail.filter(_.nonEmpty)
              val col = VList(remaining.map(_.head))
              (col, remaining.map(_.tail))
            }
          }
        case Some(filler) => LazyList.unfold(matrix) { matrix =>
            Option.when(matrix.head.nonEmpty) {
              val col = VList(matrix.map(_.headOption.getOrElse(filler)))
              (col, matrix.map(_.tail))
            }
          }
      out
    end if
  end transposeSafe

  def trim(iterable: Seq[VAny], value: VAny): Seq[VAny] =
    val temp = iterable.toList
    val trimmed = temp.dropWhile(_ == value).reverse.dropWhile(_ == value)
    trimmed.reverse

  def trimList(iterable: Seq[VAny], pattern: Seq[VAny])(using
      ctx: Context
  ): Seq[VAny] =
    var temp = iterable.toList
    while temp.startsWith(pattern) do temp = temp.drop(pattern.length)
    while temp.endsWith(pattern) do temp = temp.dropRight(pattern.length)
    temp

  /** Ensure that a VList is a matrix */
  def validateMatrix(lst: Seq[VAny])(using
      Context
  ): Option[Seq[Seq[VNum]]] =
    val rows = lst.map(ListHelpers.makeIterable(_))
    val numRows = rows.size
    // TODO(user): refactor to not use is/asInstanceOf?
    if rows.exists(_.size != numRows) then None
    else if rows.exists(_.exists(!_.isInstanceOf[VNum])) then None
    else Some(rows.asInstanceOf[Seq[Seq[VNum]]])

  def wrapLength(iterable: Seq[VAny], length: VNum): Seq[VAny] =
    if length <= 0 then Seq.empty
    else
      LazyList.unfold(iterable) { remaining =>
        if remaining.isEmpty then None
        else
          val chunk = List.newBuilder[VAny]
          var mutRemaining = remaining
          var count = 0
          while mutRemaining.nonEmpty && count < length do
            chunk += mutRemaining.head
            mutRemaining = mutRemaining.tail
            count += 1
          Some((chunk.result(), mutRemaining))
      }

  def vectorisedMaximum(iterable: Seq[VAny], b: VVal)(using
      Context
  ): Seq[VAny] =
    iterable.map { a =>
      (a: @unchecked) match
        case a: VList => vectorisedMaximum(a, b)
        case a: VVal => MiscHelpers.dyadicMaximum(a, b)
    }

  def vectorisedMinimum(iterable: Seq[VAny], b: VVal)(using
      Context
  ): Seq[VAny] =
    iterable.map { a =>
      (a: @unchecked) match
        case a: VList => vectorisedMinimum(a, b)
        case a: VVal => MiscHelpers.dyadicMinimum(a, b)
    }

  def gradeUp(iterable: VAny)(using Context): Seq[VAny] =
    makeIterable(iterable).zipWithIndex.sortBy(_._1).map(_._2)

  def gradeDown(iterable: VAny)(using Context): Seq[VAny] =
    makeIterable(iterable).zipWithIndex
      .sorted(Ordering.by((a: (VAny, Int)) => a._1).reverse)
      .map(_._2)

  def partitionAfterTruthyIndices(lst: VAny, part: VAny)(using
      Context
  ): Seq[VAny] =
    val res = ListBuffer(VList(Seq.empty))
    for (i, j) <- makeIterable(lst).zip(makeIterable(part)) do
      res(res.length - 1) = VList(res(res.length - 1) :+ i)
      if j.toBool then res += Seq.empty
    res.toList

  def powerset(iterable: Seq[VAny])(using Context): Seq[VAny] =
    val temp: LazyList[Seq[VList]] =
      LazyList.unfold((iterable, Seq(VList(Seq.empty)))) {
        case (it, prevSets) =>
          if it.isEmpty then None
          else
            val newSets = prevSets.map(_ :+ it.head).map(VList(_))
            Some((newSets, (it.tail, prevSets ++ newSets)))
      }
    Seq.empty +: temp.flatten

  def sortByLength(lst: VAny)(using ctx: Context): Seq[VAny] =
    makeIterable(lst).sortBy((a: VAny) => ListHelpers.makeIterable(a).length)

  def deltas(lst: Seq[VAny])(using Context): Seq[VAny] =
    lst.drop(1).zip(lst).map(MiscHelpers.subtract(_, _))

  def zeroPad(lst: Seq[VAny], length: VNum)(using Context): Seq[VAny] =
    val temp = lst
    val extra = MiscHelpers
      .dyadicMaximum(0, length.vabs - lst.bigLength)
      .asInstanceOf[VNum]
    val zeros = LazyList.unfold(extra) { n =>
      Option.when(n > 0) {
        (VNum(0), n - 1)
      }
    }
    if length < 0 then temp ++ zeros
    else zeros ++ temp

  def truthyIndices(lst: Seq[VAny]): Seq[VAny] =
    lst.zipWithIndex.filter { case (v, idx) => v.toBool }.map {
      case (_, idx) => VNum(idx)
    }

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
    ListHelpers.zipMulti(lists*)(f)
end ListHelpers
