package vyxal

import vyxal.*

import org.scalatest.funspec.AnyFunSpec

/** Tests for specific elements */
class ModifierTests extends VyxalTests:
  describe("Modifier v") {
    testMulti(
      "#[1 10 R|1 5 R|6 8 R#] λ+} v R" -> VList(45, 10, 13),
      "#[1 10 R|1 5 R|6 8 R#] vA" -> VList(1, 1, 1),
    )
  }
  describe("Modifier /") {
    testMulti(
      "1 10 R /+" -> 45,
      """#["abc"|"def"|"ghi"#] /+""" -> "abcdefghi",
    )
  }

  describe("Function grouping modifiers") {
    testMulti(
      "#[1|2|3#] ⸠× M" -> VList(1, 4, 9),
      "#[1|2|3#] ⸠2× M" -> VList(2, 4, 6),
      "#[1|2|3#] ϩ×+ M" -> VList(2, 6, 12),
      "#[1|2|3#] ϩ2×+ M" -> VList(3, 6, 9),
      "#[1|2|3#] э×++ M" -> VList(3, 8, 15),
      "#[1|2|3#] э2×++ M" -> VList(4, 8, 12),
      "#[1|2|3#] Ч×++× M" -> VList(3, 16, 45),
      "#[1|2|3#] Ч2×++× M" -> VList(4, 16, 36),
    )
  }

  describe("Modifier ᵃ (Dyadic)") {
    testMulti(
      "#[1|2|3|4|5#] ᵃ+" -> VList(3, 5, 7, 9),
      "#[#] ᵃ+" -> VList(),
    )
  }

  describe("Modifier ᵃ (Monadic)") {
    testMulti(
      "#[1|2|3|4|5#] ᵃe" -> VNum(2),
      "#[#] ᵃe" -> VNum(0),
    )
  }

  describe("Modifier ᴴ") {
    testMulti(
      "#[3|4|5#]ᴴd" -> VList(6, 4, 5)
    )
  }

  describe("Maximum and minimum by (ᵐ and ⁿ)") {
    testMulti(
      "#[2|1|3#]ᵐN" -> 1,
      "#[2|1|3#]ⁿN" -> 3,
    )
  }

  describe("Modifier ᵒ") {
    given ctx: Context = VyxalTests.testContext()

    it("should work on finite lists") {
      ctx.push(VList(1, 2, 3))
      ctx.push(VList(4, 5))
      Interpreter.execute("ᵒ;")
      val top = ctx.pop()
      assertResult(
        VList(
          VList(VList(1, 4), VList(1, 5)),
          VList(VList(2, 4), VList(2, 5)),
          VList(VList(3, 4), VList(3, 5)),
        )
      )(top)
    }

    it("should work on infinite lists") {
      ctx.push(VList.from(LazyList.iterate(VNum(2))(_ * 2)))
      ctx.push(VList.from(LazyList.iterate(VNum(1))(_ + 3)))
      Interpreter.execute("ᵒ-")
      val top = ctx.pop()
      assertResult(
        VList(
          VList(1, -2, -5),
          VList(3, 0, -3),
          VList(7, 4, 1),
        )
      )(top.asInstanceOf[VList].take(3).map(_.asInstanceOf[VList].take(3)))
    }
  }

  describe("Modifier ᶳ") {
    testMulti(
      "#[2|3|1#]ᶳN" -> VList(3, 2, 1)
    )
  }

end ModifierTests
