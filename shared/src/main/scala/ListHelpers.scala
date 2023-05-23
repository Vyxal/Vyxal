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
      case fn: VFun    => VList(fn)
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
          case None      => List.empty
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
              case _           => index += 1
          case item => output += mutContent(index)
        index += 1

      VList(output.toSeq*)
    end moldHelper
    moldHelper(content, shape, 0)
  end mold

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

  end sortBy

  def prefixes(iterable: VList)(using ctx: Context): VList =
    return VList.from(iterable.inits.toSeq.reverse.tail)

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
  end split

  def vectorisedMaximum(iterable: VList, b: VVal): VList =
    VList.from(iterable.map { a =>
      (a: @unchecked) match
        case a: VList => vectorisedMaximum(a, b)
        case a: VVal  => MiscHelpers.dyadicMaximum(a, b)
    })
end ListHelpers
