package vyxal

import vyxal.conversions.given
import vyxal.elements.Elements
import vyxal.VyxalTests.testContext

import org.scalatest.funspec.AnyFunSpec

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

  // Ignored because Θ doesn't exist now
  ignore("Element Ġ") {
    it("Should work as a generator") {
      testCode(
        "#[1|1#]λ2|+}Ġ10Θ",
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

  // Ignored because Θ doesn't exist now
  ignore("Element ġ") {
    testCode(
      "#[1|1#]λ+}ġ10Θ",
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

  // Ignored because Ė doesn't exist now
  ignore("Element x") {
    testCode("5 λ0=[1|1-x×}}Ė", 120)
    testCode("0 λ0=[1|1-x×}}Ė", 1)
  }

  // Ignored because Θ doesn't exist now
  ignore("Element ÞĊ") {
    it("should work on lists") {
      testCode("#[1|2|3#] ÞĊ 10 Θ", vSeq(1, 2, 3, 1, 2, 3, 1, 2, 3, 1))
    }
  }

  // Ignored because Ḋ doesn't exist right now
  ignore("Element Ḋ") {
    it("simple test") {
      testCode(
        "#[1|2|3|4|5|6|7|8|9|10|1|4|5|1|3|6|4#] λ5%} Ḋ",
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

  describe("Element ⏚") {
    describe("As the vectorise overload") {
      testMulti(
        "#[#[1|2|3#]|#[4|5|6#]#] #[#[7|8|9#]|#[1|2|3#]#] λ2|+2÷} ⏚" ->
          vSeq(vSeq(4, 5, 6), vSeq(2.5, 3.5, 4.5))
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

  describe("Element Ẋ") {
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

  // Ignored because Ẇ doesn't exist right now
  ignore("Element Ẇ") {
    testMulti(
      "λ5%3=}5Ẇ" -> vSeq(3, 8, 13, 18, 23)
    )
  }

  // Ignored because ȧ doesn't exist right now
  ignore("Element ȧ") {
    testMulti(
      "#[1|2|3|4|5|6#] λ+} ȧ" -> vSeq(3, 5, 7, 9, 11),
      "#[1|2|3|4|5|6#] λ++} ȧ" -> vSeq(4, 7, 10, 13, 16),
    )
  }

  describe("Element form of ”") {
    testMulti(
      "#[1|2|3|4|5|6#] ƛ0ne”}" -> vSeq(0, 2, 0, 4, 0, 6)
    )
  }

  // Ignored because Ṛ doesn't exist now
  ignore("Element Ạ") {
    testMulti(
      "#[1|2|3|4#] 0 λ1+} Ạ" -> vSeq(2, 2, 3, 4),
      "#[2|#[1|2|3|4#]|2|3|4#] 1 λṚ} Ạ" -> vSeq(2, vSeq(4, 3, 2, 1), 2, 3, 4),
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

  describe("Element ċ") {
    testMulti(
      "9⑵½⌊ℂ" -> vSeq(9, 4, 2, 1, 0)
    )
  }

  // Ignored because Θ doesn't exist now
  ignore("Element ÞṆ") {
    testMulti(
      "ÞṆ10Θ" -> vSeq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
      "ÞṆ5+10Θ" -> vSeq(6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
    )
  }

  // Ignored because Θ doesn't exist now
  ignore("Element ÞṬ") {
    testMulti(
      "ÞṬ20Θ" ->
        vSeq(0, 1, -1, 2, -2, 3, -3, 4, -4, 5, -5, 6, -6, 7, -7, 8, -8, 9, -9,
          10),
      "ÞṬ5+20Θ" ->
        vSeq(5, 6, 4, 7, 3, 8, 2, 9, 1, 10, 0, 11, -1, 12, -2, 13, -3, 14, -4,
          15),
    )
  }

  // Ignored because Θ doesn't exist now
  ignore("Element ÞP") {
    testMulti(
      "ÞP20Θ" ->
        vSeq(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61,
          67, 71)
    )
  }

  describe("Element ↳") {
    testMulti(
      "1 2 3 4 5 λ2↳}ᴥ" -> VNum(3),
      "1 1 λλλ3 0;↳}ᴥ}ᴥ}ᴥ" -> VNum(1),
    )
  }

  describe("Element ↸") {
    testStackLike("↸")(
      in(5, 1, 2, 3) -> List[VAny](2, 1, 3, 5)
    )
  }

  describe("Element #◌") {
    describe("should get all inputs even after reading inputs individually") {
      testMulti("??#◌")(
        in(1, 2, 3) -> vSeq(3, 2, 1)
      )
    }
  }
end ElementTests
