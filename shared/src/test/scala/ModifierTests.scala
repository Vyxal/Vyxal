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

  describe("Function grouping modifiers") {
    testMulti(
      "#[1|2|3#] ′× M" -> VList(1, 4, 9),
      "#[1|2|3#] ′2× M" -> VList(2, 4, 6),
      "#[1|2|3#] ″×+ M" -> VList(2, 6, 12),
      "#[1|2|3#] ″2×+ M" -> VList(3, 6, 9),
      "#[1|2|3#] ‴×++ M" -> VList(3, 8, 15),
      "#[1|2|3#] ‴2×++ M" -> VList(4, 8, 12),
      "#[1|2|3#] ⁴×++× M" -> VList(3, 16, 45),
      "#[1|2|3#] ⁴2×++× M" -> VList(4, 16, 36)
    )
  }
end ModifierTests
