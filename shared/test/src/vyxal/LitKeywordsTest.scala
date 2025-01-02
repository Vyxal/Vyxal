package vyxal

import scala.language.strictEquality

import vyxal.elements.ElementInformation

import org.scalatest.funspec.AnyFunSpec

class LitKeywordsTest extends AnyFunSpec:
  describe("Literate keywords") {
    they("should not be repeated") {
      val allKeywords = ElementInformation.elements.values.flatMap(_.keywords)
      val dups = allKeywords.groupBy(k => k).collect {
        case (keyword, group) if group.size > 1 => keyword
      }
      if dups.nonEmpty then
        fail(
          s"The following keywords are duplicates: ${dups.mkString("[", ",", "]")}"
        )
    }
  }
