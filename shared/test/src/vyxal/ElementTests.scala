package vyxal

import vyxal.conversions.given
import vyxal.elements.Elements

import org.scalatest.funspec.AnyFunSpec
import VyxalTests.testContext

/** Tests for specific elements */
class ElementTests extends VyxalTests:

  /** Helper to avoid doing List[VAny](...) */
  private def in(inputs: VAny*): Seq[VAny] = inputs

  private def vSeq(elems: VAny*) = VList(elems)

  describe("Element +") {

    describe("when given functions") {
      it("should turn two functions into an fgh fork") {
        given ctx: Context = Context(testMode = true)
        // Factorial
        val f = VFun.fromElement("!")
        // Function to subtract 8
        val g = VFun.fromLambda(
          AST.Lambda(
            Some(1),
            List.empty,
            List(AST.Number(8), AST.Command("-")),
          )
        )
        ctx.push(3, f, g)
        Interpreter.execute(AST.Command("+"))
        ctx.pop() match
          case fork: VFun => assertResult(VNum(1))(Interpreter.executeFn(fork))
          case res => fail(s"Expected a function, got $res")
      }
    }
  }

  describe("Element D") {
    describe("when given anything") {
      it("should leave three copies of it on the stack") {
        given ctx: Context = Context(testMode = true)
        ctx.push(1, 2, 3)
        Interpreter.execute(AST.Command("D"))
        assertResult(Seq[VAny](3, 3, 3))(Seq(ctx.pop(), ctx.pop(), ctx.pop()))
      }
    }
  }

  describe("Element Ġ") {
    it("Should work as a generator") {
      testCode(
        "#[1|1#]λ2|+}Ġ10⊖",
        vSeq(1, 1, 2, 3, 5, 8, 13, 21, 34, 55),
      )
    }
  }

  describe("Element M") {
    describe("when given a function and any value") {
      it("should map the function over the value") {
        testEquals(vSeq(2, 4, 6))(ctx ?=>
          ctx.push(vSeq(1, 2, 3))
          ctx.push(VFun(Elements.elements("+").impl, 2, List.empty, ctx))
          Interpreter.execute(AST.Command("M"))
          ctx.peek
        )
      }
      it("should work with strings") {
        testEquals(vSeq("aa", "bb", "cc"))(ctx ?=>
          ctx.push("abc")
          ctx.push(VFun(Elements.elements("+").impl, 2, List.empty, ctx))
          Interpreter.execute(AST.Command("M"))
          ctx.peek
        )
      }
    }
  }

  describe("Element N") {

    describe("when given a function") {
      testMulti(
        "λ×16=}N" -> 4,
        "λ7×35=}N" -> 5,
      )
    }
  }

  describe("Element R") {
    describe("when given function and iterable") {
      it("should work with singleton lists") {
        testEquals(1)(ctx ?=>
          ctx.push(vSeq(1))
          ctx.push(VFun(Elements.elements("+").impl, 2, List.empty, ctx))
          Interpreter.execute(AST.Command("R"))
          ctx.peek
        )
      }
      it("should calculate sum properly") {
        testEquals(15)(ctx ?=>
          ctx.push(vSeq(1, 2, 3, 4, 5))
          ctx.push(VFun(Elements.elements("+").impl, 2, List.empty, ctx))
          Interpreter.execute(AST.Command("R"))
          ctx.peek
        )
      }
    }
  }

  describe("Element ġ") {
    testCode(
      "#[1|1#]λ+}ġ10⊖",
      vSeq(1, 1, 2, 3, 5, 8, 13, 21, 34, 55),
    )
  }

  describe("Element q") {
    testMulti("q")(
      in("\\") -> "\"\\\\\"",
      in("\"") -> "\"\\\"\"",
      in("a") -> "\"a\"",
    )
  }

  describe("Element x") {
    it("recursion") {
      testCode("5 λ0=[1|1-x×}}ᴥ", 120)
      testCode("0 λ0=[1|1-x×}}ᴥ", 1)
    }
  }

  describe("Element Þ↻") {
    it("should work on lists") {
      testCode("#[1|2|3#] Þ↻ 10 ⊖", vSeq(1, 2, 3, 1, 2, 3, 1, 2, 3, 1))
    }
  }

  describe("Element u") {
    it("simple test") {
      testCode(
        "#[1|2|3|4|5|6|7|8|9|10|1|4|5|1|3|6|4#] λ5%} u",
        vSeq(1, 2, 3, 4, 5),
      )
    }
  }

  describe("Element ᴥ") {
    describe("when given a string") {
      it("should properly execute code that uses the stack") {
        testCode(""" "1 2 + D" ᴥ """, 3)
      }

      it("should use the same context for executing the code") {
        // Doesn't use the test helpers because of context handling
        given ctx: Context = Context(inputs = List(3, 4), testMode = true)
        ctx.push("+")
        ctx.settings = ctx.settings.useMode(EndPrintMode.None)
        Interpreter.execute("ᴥ")
        assertResult(7: VNum)(ctx.peek)
      }
    }

    describe("when given a function") {
      it("should execute the function") {
        testEquals(3)(ctx ?=>
          ctx.push(1, 2)
          ctx.push(VFun.fromElement("+"))
          Interpreter.execute(AST.Command("ᴥ"))
          ctx.peek
        )
      }
    }
  }

  describe("Element #ᴥ") {
    describe(
      "should identify valid vyxal code"
    ) { // keeping this in case I want to change it later or overload more
      testMulti("#ᴥ")(
        in("2 3+") -> 1,
        in("[]") -> 1,
        in("") -> 1,
        in("[]]") -> 0,
        in("Ч") -> 0,
        in("⎂") -> 0,
      )
    }
  }

  describe("Element ⏚") {
    describe("As the vectorise overload") {
      testMulti(
        "#[#[1|2|3#]|#[4|5|6#]#] #[#[7|8|9#]|#[2|1|0#]#] 2 λ3|++} ⏚" ->
          vSeq(vSeq(9, 11, 13), vSeq(8, 8, 8))
      )
    }
  }

  /*describe("Element Ŀ") {
    it(
      "Generates a list of all numbers in the collatz conjecture minus the first number"
    ) {
      testCode("10 λe[2÷|3×1+}} Ŀ", vSeq(10, 5, 16, 8, 4, 2, 1))
    }
  }*/

  describe("Element Form of “") {
    testMulti(
      "λ5-0=}“" -> 5,
      "λ1+} “" -> 1,
      "λ×16=}“" -> 4,
      "λ7×35=}“" -> 5,
    )
  }

  describe("Element X") {
    given Context = testContext()
    it("should handle two finite lists properly") {
      assertResult(
        Seq(
          Seq[VAny](1, "A"),
          Seq[VAny](1, "B"),
          Seq[VAny](2, "A"),
          Seq[VAny](2, "B"),
          Seq[VAny](3, "A"),
          Seq[VAny](3, "B"),
        )
      )(
        ListHelpers.cartesianProduct(
          Seq[VAny](1, 2, 3),
          Seq[VAny]("A", "B"),
        )
      )
    }
    it("should handle two infinite lists properly") {
      assertResult(
        Seq(
          Seq[VAny](1, "A"),
          Seq[VAny](1, "B"),
          Seq[VAny](2, "A"),
          Seq[VAny](1, "C"),
          Seq[VAny](2, "B"),
          Seq[VAny](3, "A"),
        )
      )(
        ListHelpers
          .cartesianProduct(
            VList(LazyList.iterate(VNum(1))(_ + 1)),
            VList(LazyList.from('A'.toInt).map(_.toChar.toString)),
          )
          .take(6)
      )
    }
  }

  describe("Element Ϣ") {
    testMulti(
      "λ5%3=}5Ϣ" -> vSeq(3, 8, 13, 18, 23)
    )
  }

  describe("Element @") {
    testMulti(
      "#[1|2|3|4|5|6#] λ+} @" -> vSeq(3, 5, 7, 9, 11),
      "#[1|2|3|4|5|6#] λ++} @" -> vSeq(4, 7, 10, 13, 16),
    )
  }

  describe("Element form of ”") {
    testMulti(
      "#[1|2|3|4|5|6#] ƛ0ne”}" -> vSeq(0, 2, 0, 4, 0, 6)
    )
  }

  describe("Element ≜") {
    testMulti(
      "#[1|2|3|4#] 0 λ1+} ≜" -> vSeq(2, 2, 3, 4),
      "#[2|#[1|2|3|4#]|2|3|4#] 1 λ⇄} ≜" -> vSeq(2, vSeq(4, 3, 2, 1), 2, 3, 4),
    )
  }

  describe("Element Ɠ") {
    testStackLike("Ɠ")(
      in(vSeq(1, 2, 3)) -> List[VAny](3, vSeq(1, 2, 3)),
      in(vSeq(1, 2, 3), vSeq(4, 5, 6)) ->
        List[VAny](6, vSeq(4, 5, 6), vSeq(1, 2, 3)),
    )
  }

  describe("Element ɠ") {
    testStackLike("ɠ")(
      in(vSeq(1, 2, 3)) -> List[VAny](1, vSeq(1, 2, 3)),
      in(vSeq(1, 2, 3), vSeq(4, 5, 6)) ->
        List[VAny](4, vSeq(4, 5, 6), vSeq(1, 2, 3)),
    )
  }

  describe("Element ◲") {
    testMulti(
      "#[1|1#]⎄+}◲10⊖" ->
        vSeq(
          vSeq(1),
          vSeq(1, 1),
          vSeq(1),
          vSeq(1, 1, 2),
          vSeq(1, 2),
          vSeq(1, 1, 2, 3),
          vSeq(2),
          vSeq(1, 2, 3),
          vSeq(1, 1, 2, 3, 5),
          vSeq(2, 3),
        ),
      "#[1#]⎄1+}◲10⊖" ->
        vSeq(
          vSeq(1),
          vSeq(1, 2),
          vSeq(2),
          vSeq(1, 2, 3),
          vSeq(2, 3),
          vSeq(1, 2, 3, 4),
          vSeq(3),
          vSeq(2, 3, 4),
          vSeq(1, 2, 3, 4, 5),
          vSeq(3, 4),
        ),
    )
  }

  describe("Element ↜") {
    testStackLike("↜")(
      in(1, 2, 3) -> List[VAny](1, 3, 2),
      in(1) -> List[VAny](1),
    )
  }

  describe("Element ↝") {
    testStackLike("↝")(
      in(1, 2, 3) -> List[VAny](2, 1, 3),
      in(1) -> List[VAny](1),
    )
  }

  describe("Element ɦ") {
    testStackLike("ɦ")(
      in(vSeq(1, 2, 3, 4, 5)) -> List[VAny](1, vSeq(1, 2, 3, 4, 5))
    )
  }

  describe("Element ʈ") {
    testStackLike("ʈ")(
      in(vSeq(1, 2, 3, 4, 5)) -> List[VAny](5, vSeq(1, 2, 3, 4, 5))
    )
  }

  describe("Element ᑂ") {
    testMulti(
      " #[5|4|3|2|1#] ⑵½⌊ᑂ" -> vSeq(2, 4, 3, 2, 1)
    )
  }

  describe("Element ℂ") {
    testMulti(
      "9⑵½⌊ℂ" -> VNum(0)
    )
  }

  describe("Element kN") {
    testMulti(
      "kN10⊖" -> vSeq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
      "kN5+10⊖" -> vSeq(6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
    )
  }

  describe("Element kṬ") {
    testMulti(
      "kṬ20⊖" ->
        vSeq(0, 1, -1, 2, -2, 3, -3, 4, -4, 5, -5, 6, -6, 7, -7, 8, -8, 9, -9,
          10),
      "kṬ5+20⊖" ->
        vSeq(5, 6, 4, 7, 3, 8, 2, 9, 1, 10, 0, 11, -1, 12, -2, 13, -3, 14, -4,
          15),
    )
  }

  describe("Element kæ") {
    testMulti(
      "kæ20⊖" ->
        vSeq(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61,
          67, 71)
    )
  }

  describe("Element #↸") {
    testMulti(
      "1 2 3 4 5 λ2#↸}ᴥ" -> VNum(3),
      "1 1 λλλ3 0;#↸}ᴥ}ᴥ}ᴥ" -> VNum(1),
    )
  }

  describe("Element ↸") {
    testStackLike("↸")(
      in(5, 1, 2, 3) -> List[VAny](2, 1, 3, 5)
    )
  }

  describe("Element #?") {
    describe("should get all inputs even after reading inputs individually") {
      testMulti("??#?")(
        in(1, 2, 3) -> vSeq(3, 2, 1)
      )
    }
  }

  describe("Element Ł") {
    describe("Should leave its argument on the stack") {
      testStackLike("Ł")(
        in(VList(Seq(1, 2, 3, 4))) -> List[VAny](4, vSeq(1, 2, 3, 4))
      )
    }
  }

  describe("Element ≤") {
    describe("Using it's 'min-by' overload") {
      testMulti("#[1|2|3|4|5|6#] ⑴N ≤" -> VNum(6))
      testMulti("#[#] ⑴N ≤" -> VNum(0))
    }
  }

  describe("Element ≥") {
    describe("Using it's 'max-by' overload") {
      testMulti("#[1|2|3|4|5|6#] ⑴N ≥" -> VNum(1))
      testMulti("#[#] ⑴N ≥" -> VNum(0))
    }
  }

  describe("Element Þ/") {
    testMulti(
      """ #[#[1|2|3#]|#[4|5|6#]|#[7|8|9#]|#["a"|"b"|"c"#]#] Þ/ """ ->
        vSeq(
          vSeq(1),
          vSeq(4, 2),
          vSeq(7, 5, 3),
          vSeq("a", 8, 6),
          vSeq("b", 9),
          vSeq("c"),
        ),
      "k=w0J Þ/" -> vSeq(vSeq(0, 0), 0),
      "9ɾ¨w3Ϣ Þ/" ->
        vSeq(
          vSeq(vSeq(1)),
          vSeq(vSeq(4), vSeq(2)),
          vSeq(vSeq(7), vSeq(5), vSeq(3)),
          vSeq(vSeq(8), vSeq(6)),
          vSeq(vSeq(9)),
        ),
    )
  }
  describe("Element Þ\\") {
    testMulti(
      """ #[#[1|2|3#]|#[4|5|6#]|#[7|8|9#]|#["a"|"b"|"c"#]#] Þ\ """ ->
        vSeq(
          vSeq(3),
          vSeq(2, 6),
          vSeq(1, 5, 9),
          vSeq(4, 8, "c"),
          vSeq(7, "b"),
          vSeq("a"),
        ),
      "k=w0J Þ\\ " -> vSeq(vSeq(0, 0), 0),
      "9ɾ¨w3Ϣ Þ\\ " ->
        vSeq(
          vSeq(vSeq(3)),
          vSeq(vSeq(2), vSeq(6)),
          vSeq(vSeq(1), vSeq(5), vSeq(9)),
          vSeq(vSeq(4), vSeq(8)),
          vSeq(vSeq(7)),
        ),
    )
  }
  describe("Element Þ„") {
    testMulti(
      """ #[#[3#]|#[2|6#]|#[1|5|9#]|#[4|8|"c"#]|#[7|"b"#]|#["a"#]#] Þ„""" ->
        vSeq(vSeq(1, 2, 3), vSeq(4, 5, 6), vSeq(7, 8, 9), vSeq("a", "b", "c")),
      """#[#[#[3#]#]|#[#[2#]|#[6#]#]|#[#[1#]|#[5#]|#[9#]#]|#[#[4#]|#[8#]#]|#[#[7#]#]#] Þ„""" ->
        vSeq(
          vSeq(vSeq(1), vSeq(2), vSeq(3)),
          vSeq(vSeq(4), vSeq(5), vSeq(6)),
          vSeq(vSeq(7), vSeq(8), vSeq(9)),
        ),
      """ #[#[4#]|#[3|8#]|#[2|7|"c"#]|#[1|6|"b"#]|#[5|"a"#]|#[9#]#] 4 Þ„""" ->
        vSeq(vSeq(1, 2, 3, 4), vSeq(5, 6, 7, 8), vSeq(9, "a", "b", "c")),
    )
  }

  describe("Element Þ”") {
    testMulti(
      """ #[#[3#]|#[2|6#]|#[1|5|9#]|#[4|8|"c"#]|#[7|"b"#]|#["a"#]#] Þ”""" ->
        vSeq(vSeq(3, 2, 1), vSeq(6, 5, 4), vSeq(9, 8, 7), vSeq("c", "b", "a")),
      """#[#[#[3#]#]|#[#[2#]|#[6#]#]|#[#[1#]|#[5#]|#[9#]#]|#[#[4#]|#[8#]#]|#[#[7#]#]#] Þ”""" ->
        vSeq(
          vSeq(vSeq(3), vSeq(2), vSeq(1)),
          vSeq(vSeq(6), vSeq(5), vSeq(4)),
          vSeq(vSeq(9), vSeq(8), vSeq(7)),
        ),
      """ #[#[4#]|#[3|8#]|#[2|7|"c"#]|#[1|6|"b"#]|#[5|"a"#]|#[9#]#] 4 Þ”""" ->
        vSeq(vSeq(4, 3, 2, 1), vSeq(8, 7, 6, 5), vSeq("c", "b", "a", 9)),
    )
  }

  describe("Element Þ▲") {
    testMulti(
      """ "hello world" 10010101101 Þ▲""" ->
        vSeq("h", 0, 0, "l", 0, " ", 0, "o", "r", 0, "d"),
      """ k⁰ λ⍢] Þ▲ """ -> vSeq(0, 1, 0, 7, 0, 0, 3, 0, 0, 0),
    )
  }

  describe("Element #W") {
    testMulti(
      "7ʀ⍨3#W" -> vSeq(6, 5, 4),
      "7ʀ⍨9#W" -> vSeq(6, 5, 4, 3, 2, 1, 0, 0, 0),
    )
  }
  describe("Element øE") {
    testMulti(
      "101001000000000110110øE;" ->
        vSeq(
          vSeq("1", "0", "1", "0", "1", "0", "1", "0", "1", "0"),
          vSeq(1, 1, 1, 2, 1, 9, 2, 1, 2, 1),
        )
    )
  }
  describe("Element i") {
    it("should still error if neither input is a list of numbers") {
      given ctx: Context = Context()
      assertThrows[Exception] {
        Interpreter.execute("kvfkẄf$i")
      }
    }
    testMulti(
      "2 12 5 22Wnfi" -> vSeq("c", "m", "f", "w"),
      "2 12 5 22Wnf$i" -> vSeq("c", "m", "f", "w"),
    )
  }
end ElementTests
