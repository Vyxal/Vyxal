package vyxal

import vyxal.*
import vyxal.VNum.given

import org.scalatest.funspec.AnyFunSpec

/** Tests for specific elements */
class ModifierTests extends VyxalTests:

  // todo get rid of this and use Seq(...).v instead
  def vSeq(elems: VAny*) = VList.from(elems)

  describe("Modifier ᵛ") {
    testMulti(
      "#[1 10 R|1 5 R|6 8 R#] λ+} ᵛ R" -> vSeq(45, 10, 13),
      "#[1 10 R|1 5 R|6 8 R#] ᵛA" -> vSeq(1, 1, 1),
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
      "#[1|2|3#] ⸠× M" -> vSeq(1, 4, 9),
      "#[1|2|3#] ⸠2× M" -> vSeq(2, 4, 6),
      "#[1|2|3#] ϩ×+ M" -> vSeq(2, 6, 12),
      "#[1|2|3#] ϩ2×+ M" -> vSeq(3, 6, 9),
      "#[1|2|3#] э×++ M" -> vSeq(3, 8, 15),
      "#[1|2|3#] э2×++ M" -> vSeq(4, 8, 12),
      "#[1|2|3#] Ч×++× M" -> vSeq(3, 16, 45),
      "#[1|2|3#] Ч2×++× M" -> vSeq(4, 16, 36),
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
      "#[1|2|3|4|5#] ᵃ+" -> vSeq(3, 5, 7, 9),
      "#[#] ᵃ+" -> vSeq(),
    )
  }

  describe("Modifier ᵇ (Monadic)") {
    testMulti(
      "#[1|2|3|4|5#] ᵇe" -> vSeq(1, 2),
      "#[#] ᵇe" -> vSeq(),
      """#["abc"|"def"|"abc"|"ifff"#] ᵇL""" -> vSeq("abc", "ifff"),
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
      "#[1|1|1|1|1#] ᶜL" -> vSeq(5, 4, 3, 2, 1),
      "#[1|2|3|4|5#] ᶜ⸠/+" -> vSeq(15, 14, 12, 9, 5),
    )
  }

  describe("Modifier ᶜ (Dyadic)") {
    testMulti(
      "#[#[1|2|3#]|#[4|5|6#]|#[7|8|9#]#] ᶜ+" -> vSeq(12, 15, 18),
      "#[#] ᶜ+" -> vSeq(),
    )
  }

  describe("Modifier ᴴ") {
    testMulti(
      "#[3|4|5#]ᴴ69" -> vSeq(69, 4, 5),
      "#[3|4|5#]ᴴd" -> vSeq(6, 4, 5),
      "#[3|4|5#]ᴴ+" -> vSeq(7, 8),
      "\"abcde\"ᴴ69" -> "69bcde",
      "\"abcde\"ᴴd" -> "aabcde",
      "\"abcde\"ᴴ+" -> "bacadaea",
      "#[3|4|\"abc\"#]ᴴ+" -> vSeq(7, "abc3"),
      "#[#[1|\"abc\"#]|2|\"def\"#]ᴴN" -> vSeq(vSeq(-1, "ABC"), 2, "def"),
      "#[#[1|\"abc\"#]|2|\"def\"#]ᴴ+" ->
        vSeq(vSeq(3, "2abc"), vSeq("def1", "defabc")),
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
    "#[1|2|3|4#] ᶨḢ" -> vSeq(vSeq(2, 3, 4), vSeq(3, 4), vSeq(4), vSeq())
  }

  describe("Modifier ᵏ") {
    testMulti(
      "#[1|1|2|3|1|2|3|3|3|2|2|1#] ᵏL" -> vSeq(4, 4, 4)
    )
  }

  describe("Modifier ᶪ") {
    "#[1|2|3|4#] ᶪḢ" -> vSeq()
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
      ctx.push(vSeq(1, 2, 3))
      ctx.push(vSeq(4, 5))
      Interpreter.execute("ᵒ;")
      val top = ctx.pop()
      assertResult(
        vSeq(
          vSeq(vSeq(1, 4), vSeq(1, 5)),
          vSeq(vSeq(2, 4), vSeq(2, 5)),
          vSeq(vSeq(3, 4), vSeq(3, 5)),
        )
      )(top)
    }

    it("should work on infinite lists") {
      ctx.push(VList.from(LazyList.iterate(VNum(2))(_ * 2)))
      ctx.push(VList.from(LazyList.iterate(VNum(1))(_ + 3)))
      Interpreter.execute("ᵒ-")
      val top = ctx.pop()
      assertResult(
        Seq(
          Seq[VNum](1, -2, -5),
          Seq[VNum](3, 0, -3),
          Seq[VNum](7, 4, 1),
        )
      )(top.asInstanceOf[VList].take(3).map(_.asInstanceOf[VList].take(3)))
    }
  }

  describe("Modifier ᵖ") {
    testMulti(
      "#[1|1|1|1|1#] ᵖL" -> vSeq(1, 2, 3, 4, 5),
      "#[1|2|3|4|5#] ᵖ⸠/+" -> vSeq(1, 3, 6, 10, 15),
    )
  }

  describe("Modifier ᶳ") {
    testMulti(
      "#[2|3|1#]ᶳN" -> vSeq(3, 2, 1)
    )
  }

  describe("Modifier ᵘ (Monadic)") {
    testMulti(
      "9ᵘϩ½⌊" -> vSeq(9, 4, 2, 1, 0)
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

    testCode("λ0|3 4 5λ!|+}ĖW}Ė", vSeq(3, 9), List())
  }

  describe("Modifier ᵡ") {
    testMulti(
      "10 ᵡϩe[2÷|3×1+}" -> vSeq(10, 5, 16, 8, 4, 2, 1)
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
        vSeq(vSeq(1, 5, 12), vSeq(2, 7, 15), vSeq(3, 9, 18))
    )
  }

  describe("Modifier ᶻ (Monadic)") {
    testMulti(
      "#[1|2|3|4|5#] ᶻe" -> vSeq(1, 3, 5)
    )
  }

  describe("Modifier ᶻ (Dyadic)") {
    testMulti(
      "#[1|2|3#] #[4|5|6#] ᶻ+" -> vSeq(5, 7, 9)
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
      "3 4 ∦+-" -> vSeq(7, -1),
      "3 4 ∦+d" -> vSeq(7, 8),
      "1 3 4 5 ∦∦+-+" -> vSeq(vSeq(9, -1), 9),
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
      "#[#[1|2|3#]|#[4|5|6#]#] ᵗϩ++" -> vSeq(6, 15)
    )
  }

end ModifierTests
