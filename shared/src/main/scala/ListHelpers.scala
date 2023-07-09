package vyxal

import scala.collection.mutable as mut

import collection.mutable.ArrayBuffer
import VNum.given

object ListHelpers:

  def filter(iterable: VList, predicate: VFun)(using ctx: Context): VList =
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
            keep = MiscHelpers.boolify(res)
            branchList = branchList.tail

          keep
        }

        VList(filtered.map(_._1)*)
      case None =>
        VList(iterable.zipWithIndex.map { (item, index) =>
          predicate.execute(item, index, List(item))
        }*)

  end filter

  def flatten(xs: VList): VList =
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

  def groupConsecutive(iterable: VList): VList =
    VList.from(groupConsecutiveBy(iterable)(x => x).map(VList.from))

  def groupConsecutiveBy[T](
      iterable: Seq[T],
  )(function: T => Any): Seq[Seq[T]] =
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

  def interleave(left: VList, right: VList)(using ctx: Context): VList =
    val out = ArrayBuffer.empty[VAny]
    val leftIter = left.iterator
    val rightIter = right.iterator
    while leftIter.hasNext && rightIter.hasNext do
      out += leftIter.next()
      out += rightIter.next()

    out ++= leftIter
    out ++= rightIter

    VList(out.toSeq*)

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
      case str: String => VList(str.map(_.toString)*)
      case fn: VFun => VList(fn)
      case num: VNum =>
        if overrideRangify.getOrElse(ctx.settings.rangify) then
          val start = ctx.settings.rangeStart
          val offset = ctx.settings.rangeOffset
          VList(
            start.toInt
              .to(num.toInt - offset.toInt)
              .map(VNum(_))*
          )
        else VList(num.toString.map(x => VNum(x.toString))*)

  def map(f: VFun, to: VList)(using ctx: Context): VList =
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

  def maximum(iterable: VList)(using ctx: Context): VAny =
    if iterable.isEmpty then VList()
    else
      iterable.reduce { (a, b) =>
        if MiscHelpers.compareExact(a, b) > 0 then a else b
      }

  def minimum(iterable: VList)(using ctx: Context): VAny =
    if iterable.isEmpty then VList()
    else
      iterable.reduce { (a, b) =>
        if MiscHelpers.compareExact(a, b) < 0 then a else b
      }

  /** Mold a list into a shape.
    * @param content
    *   The list to mold.
    * @param shape
    *   The shape to mold the list into.
    * @return
    *   VyList The content, molded into the shape.
    */
  def mold(content: VList, shape: VList)(using ctx: Context): VList =
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

  def overlaps(iterable: Seq[VAny], size: Int): Seq[VList] =
    if size == 0 then Seq.empty
    else iterable.sliding(size).toSeq.map(VList.from)

  // Just for strings
  def overlaps(iterable: String, size: Int): Seq[String] =
    if size == 0 then Seq.empty
    else iterable.sliding(size).toSeq

  def sortBy(iterable: VList, key: VFun)(using ctx: Context): VList =
    key.originalAST match
      case Some(lam) =>
        val branches = lam.body
        if branches.length < 2 then
          return VList(
            iterable.zipWithIndex
              .sorted { (a, b) =>
                MiscHelpers.compareExact(
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
                MiscHelpers.compareExact(aRes, bRes) < 0
              }
          }
          .map(_._1)

        VList(out*)
      case None =>
        VList(
          iterable.zipWithIndex
            .sorted { (a, b) =>
              MiscHelpers.compareExact(
                key.executeResult(a(0), a(1), List(a(0))),
                key.executeResult(b(0), b(1), List(b(0)))
              )
            }
            .map(_._1)*
        )

  end sortBy

  def prefixes(iterable: VList): Seq[VList] =
    iterable.inits.toSeq.reverse.tail

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

  def splitNormal(iterable: VList, sep: VAny)(using ctx: Context): VList =
    val out = split(iterable, Seq(sep))
    VList(out.map(VList.from)*)

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

  def vectorisedMaximum(iterable: VList, b: VVal): VList =
    VList.from(iterable.map { a =>
      (a: @unchecked) match
        case a: VList => vectorisedMaximum(a, b)
        case a: VVal => MiscHelpers.dyadicMaximum(a, b)
        case _ => ???
    })

  def vectorisedMinimum(iterable: VList, b: VVal): VList =
    VList.from(iterable.map {
      case a: VList => vectorisedMinimum(a, b)
      case a: VVal => MiscHelpers.dyadicMinimum(a, b)
      case _ => ???
    })
end ListHelpers
