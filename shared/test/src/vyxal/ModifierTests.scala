package vyxal

import vyxal.*

import org.scalatest.funspec.AnyFunSpec

/** Tests for specific elements */
class ModifierTests extends VyxalTests:
  describe("Modifier ᵛ") {
    testMulti(
      "#[1 10 R|1 5 R|6 8 R#] λ+} ᵛ R" -> VList(45, 10, 13),
      "#[1 10 R|1 5 R|6 8 R#] ᵛA" -> VList(1, 1, 1),
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
      "#[1|2|3#] ᵈ+ R" -> VNum(6),
      "#[1|2|3#] ᵉ+× R" -> VNum(27),
      "#[1|2|3#] ᶠ+×+ R" -> VNum(37),
      "#[1|2|3#] ᴳ+×+× R" -> VNum(195),
    )
  }

  describe("Modifier ᵃ (Monadic)") {
    testMulti(
      "#[1|2|3|4|5#] ᵃe" -> VNum(2),
      "#[#] ᵃe" -> VNum(0),
    )
  }
  describe("Modifier ᵃ (Dyadic)") {
    testMulti(
      "#[1|2|3|4|5#] ᵃ+" -> VList(3, 5, 7, 9),
      "#[#] ᵃ+" -> VList(),
    )
  }

  describe("Modifier ᵇ (Monadic)") {
    testMulti(
      "#[1|2|3|4|5#] ᵇe" -> VList(1, 2),
      "#[#] ᵇe" -> VList(),
      """#["abc"|"def"|"abc"|"ifff"#] ᵇL""" -> VList("abc", "ifff"),
    )
  }

  describe("Modifier ᵇ (Arity 2+)") {
    testStackLike("ᵇ+")(
      List[VAny](3, 4, 5) -> List[VAny](9, 5, 4, 3),
      List[VAny](1, 1) -> List[VAny](2, 1, 1),
    )
    testStackLike("ᵇr") {
      List[VAny]("abc", "b", "!!") -> List[VAny]("a!!c", "!!", "b", "abc")
    }
  }

  describe("Modifier ᶜ (Monadic)") {
    testMulti(
      "#[1|1|1|1|1#] ᶜL" -> VList(5, 4, 3, 2, 1),
      "#[1|2|3|4|5#] ᶜ⸠/+" -> VList(15, 14, 12, 9, 5),
    )
  }

  describe("Modifier ᶜ (Dyadic)") {
    testMulti(
      "#[#[1|2|3#]|#[4|5|6#]|#[7|8|9#]#] ᶜ+" -> VList(12, 15, 18),
      "#[#] ᶜ+" -> VList(),
    )
  }

  describe("Modifier ᴴ") {
    testMulti(
      "#[3|4|5#]ᴴ69" -> VList(69, 4, 5),
      "#[3|4|5#]ᴴd" -> VList(6, 4, 5),
      "#[3|4|5#]ᴴ+" -> VList(7, 8),
      "\"abcde\"ᴴ69" -> "69bcde",
      "\"abcde\"ᴴd" -> "aabcde",
      "\"abcde\"ᴴ+" -> "bacadaea",
      "#[3|4|\"abc\"#]ᴴ+" -> VList(7, "abc3"),
      "#[#[1|\"abc\"#]|2|\"def\"#]ᴴN" -> VList(VList(-1, "ABC"), 2, "def"),
      "#[#[1|\"abc\"#]|2|\"def\"#]ᴴ+" ->
        VList(VList(3, "2abc"), VList("def1", "defabc")),
    )
  }

  describe("Modifier ᶤ") {
    testMulti(
      "#[1|3|5|2#] ᶤe" -> VNum(3),
      "#[#] ᶤe" -> VNum(-1),
      "#[1|3|3|3|3#] ᶤe" -> VNum(-1),
    )
  }

  describe("Modifier ᶨ") {
    "#[1|2|3|4#] ᶨḢ" -> VList(VList(2, 3, 4), VList(3, 4), VList(4), VList())
  }

  describe("Modifier ᵏ") {
    testMulti(
      "#[1|1|2|3|1|2|3|3|3|2|2|1#] ᵏL" -> VList(4, 4, 4)
    )
  }

  describe("Modifier ᶪ") {
    "#[1|2|3|4#] ᶪḢ" -> VList()
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

  describe("Modifier ᵖ") {
    testMulti(
      "#[1|1|1|1|1#] ᵖL" -> VList(1, 2, 3, 4, 5),
      "#[1|2|3|4|5#] ᵖ⸠/+" -> VList(1, 3, 6, 10, 15),
    )
  }

  describe("Modifier ᶳ") {
    testMulti(
      "#[2|3|1#]ᶳN" -> VList(3, 2, 1)
    )
  }

  describe("Modifier ᵘ (Monadic)") {
    testMulti(
      "9ᵘϩ½⌊" -> VList(9, 4, 2, 1, 0)
    )
  }

  describe("Modifier ᵘ (Dyadic)") {
    testMulti(
      "#[1|2|3|4|5#] ᵘᵉ+e" -> VNum(1),
      "#[#] ᵘ+" -> VNum(1),
    )
  }

  describe("Modifier ᵂ") {
    testStackLike("ᵂ+") {
      List[VAny](3, 4, 5) -> List[VAny](5, 7)
      List[VAny](1, 1, 1) -> List[VAny](1, 2)
    }

    testStackLike("ᵂᵂ+") {
      List[VAny](3, 4, 5, 6) -> List[VAny](6, 5, 7)
    }
  }

  describe("Modifier ᵡ") {
    testMulti(
      "10 ᵡϩe[2÷|3×1+}" -> VList(10, 5, 16, 8, 4, 2, 1)
    )
  }

  describe("Modifier ᵞ (Monadic)") {
    testMulti(
      "\"abc\" ᵞϩṚṚ" -> VNum(1),
      "6 ᵞϩṚe" -> VNum(0),
    )
  }

  describe("Modifier ᵞ (Dyadic)") {
    testMulti(
      "#[#[1|2|3#]|#[4|5|6#]|#[7|8|9#]#] ᵞ+" ->
        VList(VList(1, 5, 12), VList(2, 7, 15), VList(3, 9, 18))
    )
  }

  describe("Modifier ᶻ (Monadic)") {
    testMulti(
      "#[1|2|3|4|5#] ᶻe" -> VList(1, 3, 5)
    )
  }

  describe("Modifier ᶻ (Dyadic)") {
    testMulti(
      "#[1|2|3#] #[4|5|6#] ᶻ+" -> VList(5, 7, 9)
    )

  }

  describe("Modifier ∥") {
    testStackLike("∥+-")(
      List[VAny](3, 4) -> List[VAny](-1, 7)
    )

    testStackLike("∥+d")(
      List[VAny](3, 4) -> List[VAny](VNum(8), VNum(7), VNum(3))
    )
  }

  describe("Modifier ∦") {
    testMulti(
      "3 4 ∦+-" -> VList(7, -1),
      "3 4 ∦+d" -> VList(7, 8),
      "1 3 4 5 ∦∦+-+" -> VList(VList(9, -1), 9),
    )
  }

  describe("Modifier ¿") {
    testMulti(
      "3 4 1 ¿+" -> VNum(7),
      "3 4 0 ¿+" -> VNum(4),
    )
  }

  describe("Modifier ᵗ") {
    testMulti(
      "#[#[1|2|3#]|#[4|5|6#]#] ᵗϩ++" -> VList(6, 15)
    )
  }

end ModifierTests
