package vyxal

import vyxal.conversions.given

import org.scalatest.funspec.AnyFunSpec

/** Tests for specific elements */
class ModifierTests extends VyxalTests:

  // todo get rid of this and use Seq(...).v instead
  def vSeq(elems: VAny*) = VList(elems)

  describe("Modifier /") {
    describe("With a dyad performs reduce") {
      testMulti(
        "1 10 R /+" -> 45,
        """#["abc"|"def"|"ghi"#] /+""" -> "abcdefghi",
      )
    }
    describe("With a monad peforms invariant under") {
      testMulti(
        "\"abc\" /⑵⇄⇄" -> VNum(1),
        "6 /⑵⇄e" -> VNum(0),
      )
    }
  }

  describe("Modifier \\") {
    describe("With a monad, performs reject-by") {
      testMulti(
        "#[1|2|3|4|5|6#] \\e" -> vSeq(1, 3, 5)
      )
    }
    describe("With a dyad, performs scan") {
      testMulti(
        "#[1|2|3|4|5|6#] \\+" -> vSeq(1, 3, 6, 10, 15, 21)
      )
    }
  }

  describe("Function grouping modifiers") {
    testMulti(
      "#[1|2|3#] ⑴× M" -> vSeq(1, 4, 9),
      "#[1|2|3#] ⑴2× M" -> vSeq(2, 4, 6),
      "#[1|2|3#] ⑵×+ M" -> vSeq(2, 6, 12),
      "#[1|2|3#] ⑵2×+ M" -> vSeq(3, 6, 9),
      "#[1|2|3#] ⑶×++ M" -> vSeq(3, 8, 15),
      "#[1|2|3#] ⑶2×++ M" -> vSeq(4, 8, 12),
      "#[1|2|3#] ⑷×++× M" -> vSeq(3, 16, 45),
      "#[1|2|3#] ⑷2×++× M" -> vSeq(4, 16, 36),
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

  describe("Modifier ∺") {
    testStackLike("∺d½")(
      List[VAny](3, 4) -> List[VAny](VNum(2), VNum(6))
    )
    testStackLike("∺+-")(
      List[VAny](3, 4, 5, 6) -> List[VAny](-1, 7)
    )
  }

  describe("Modifier ⁜") {
    testMulti(
      "#[1|3|4|5|2|4#] ⁜e" -> vSeq(vSeq(1, 3), vSeq(4), vSeq(5), vSeq(2, 4)),
      "#[1|2|3|4|5|6#] ⁜λ3|++}" -> vSeq(6, 9, 12, 15),
    )
  }

  describe("Modifier ⎂") {
    testStackLike("⎂d")(
      List[VAny](3, 4) -> List[VAny](VNum(8), VNum(6))
    )
    testStackLike("⎂+")(
      List[VAny](1, 2, 3, 4) -> List[VAny](7, 3)
    )
    testCode("★6⎂λnd};", VList(Seq(VStr("**"), VNum(12))), Seq())
  }

  describe("Modifier ⟒") {
    testMulti(
      "3 4 ⟒+×" -> VNum(28),
      "\"hEllO\"f ⟒ʀ=" -> vSeq(1, 0, 1, 1, 0),
    )
  }

  describe("Modifier ᛞ") {
    testMulti(
      "#[1|2|3#] #[4|5|6#] ᛞ×+" -> VNum(32)
    )
  }

  describe("Modifier ▦") {
    testMulti(
      "#[1|2|3#] #[4|5|6#] ▦;" ->
        vSeq(
          vSeq(vSeq(1, 4), vSeq(1, 5), vSeq(1, 6)),
          vSeq(vSeq(2, 4), vSeq(2, 5), vSeq(2, 6)),
          vSeq(vSeq(3, 4), vSeq(3, 5), vSeq(3, 6)),
        )
    )
  }

  describe("Modifier ¨") {
    testMulti(
      "#[#[1|2|3#]|#[4|2|3#]|#[1|5|3#]#] ¨G" -> vSeq(3, 4, 5),
      "#[1 10 R|1 5 R|6 8 R#] λ+} ¨ R" -> vSeq(45, 10, 13),
      "#[1 10 R|1 5 R|6 8 R#] ¨A" -> vSeq(1, 1, 1),
    )
  }

  describe("Modifier #⍰") {
    testMulti(
      "3 0 #⍰d½" -> VNum(1.5),
      "3 1 #⍰d½" -> VNum(6),
    )
  }

  describe("Modifier ⎇") {
    testStackLike("⎇+") {
      List[VAny](3, 4, 5) -> List[VAny](5, 7)
      List[VAny](1, 1, 1) -> List[VAny](1, 2)
    }

    testStackLike("⎇⎇+") {
      List[VAny](3, 4, 5, 6) -> List[VAny](6, 5, 7)
    }
  }

  describe("Modifier ~") {
    describe("With a monad, performs filter") {
      testMulti(
        "#[1|2|3|4|5|6#] ~e" -> vSeq(2, 4, 6)
      )
    }

    describe("With a dyad+, performs without popping") {
      testStackLike("~+")(
        List[VAny](3, 4, 5) -> List[VAny](9, 5, 4, 3)
      )
    }
  }

  describe("Modifier Ẅ") {
    testMulti(
      "#[1|2|3#] #[4|5|6#] Ẅ;" -> vSeq(vSeq(1, 4), vSeq(2, 5), vSeq(3, 6))
    )
  }

  describe("Modifier ⩔") {
    testMulti(
      "#[#[#[1|2|3#]|#[#[4|5|#[6|7|8#]#]#]#]#] ⩔L" ->
        vSeq(vSeq(3, vSeq(vSeq(1, 1, 3)))),
      "#[#[#[1|2|3#]|#[4|5|6#]#]|#[#[7|8|9#]|#[10|11|12#]#]#] #[1#] ⩔i" ->
        vSeq(vSeq(vSeq(2), vSeq(5)), vSeq(vSeq(8), vSeq(11))),
    )
  }

  describe("Modifier #Ṭ") {
    testMulti(
      "#[1|2|3|4|5#] #[1|0|0|1|1#] #Ṭ›" -> vSeq(2,2,3,5,6),
      """ "hello" #[1|0|0|1|1#] #Ṭʁ""" -> "HelLO",
      """ #[1|0|0|1|1#] "hello" #Ṭʁ""" -> "HelLO",
    )
  }
end ModifierTests
