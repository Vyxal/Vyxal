package vyxal

import vyxal.elements.ElementInformation
import vyxal.elements.Elements
import vyxal.parsing.Codepage

import org.scalatest.funsuite.AnyFunSuite

class ElementInfoTests extends AnyFunSuite:
  test("All elements should be documented") {
    val undocumented = Elements.elements.keySet
      .filterNot(ElementInformation.elements.keySet)
      .-("🍪")

    if undocumented.nonEmpty then fail(s"Undocumented: $undocumented")
  }

  test("All documented elements should be implemented") {
    val unimplemented =
      ElementInformation.elements.keySet.filterNot(Elements.elements.keySet)
    if unimplemented.nonEmpty then fail(s"Unimplemented: $unimplemented")
  }

  test("All elements should be in the codepage") {
    val missing =
      (Elements.elements.keySet ++ ElementInformation.elements.keySet)
        .filterNot(_.forall(Codepage.contains))
        .-("🍪")
    if missing.nonEmpty then fail(s"Missing: $missing")
  }

  test("No keywords are duplicated") {
    val bad = ElementInformation.elements.filter { (sym, info) =>
      ElementInformation.elements
        .filter(_._1 != sym)
        .exists(_._2.keywords.exists(info.keywords.contains))
    }.keySet
    if bad.nonEmpty then fail(s"Duplicated: $bad")
  }
end ElementInfoTests
