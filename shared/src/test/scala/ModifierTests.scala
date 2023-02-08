package vyxal

import vyxal.*

import org.scalatest.funspec.AnyFunSpec

/** Tests for specific elements */
class ModifierTests extends VyxalTests:
  describe("Modifier v") {
    testMulti(
      "#[1 10 R|1 5 R|6 8 R#] λ+} v R" -> VList(45, 10, 13),
      "#[1 10 R|1 5 R|6 8 R#] vA" -> VList(1, 1, 1)
    )
  }
  describe("Modifier /") {
    testMulti(
      "1 10 R /+" -> 45,
      """#["abc"|"def"|"ghi"#] /+""" -> "abcdefghi"
    )
  }
