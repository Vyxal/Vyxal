package vyxal

import vyxal.VyxalTests.testContext

import org.scalatest.funspec.AnyFunSpec

/** Tests for specific elements */
class ElementTests extends VyxalTests:
  /** Helper to avoid doing List[VAny](...) */
  private def in(inputs: VAny*): Seq[VAny] = inputs

  describe("Element +") {

    describe("when given functions") {
      it("should turn two functions into an fgh fork") {
        given ctx: Context = Context(testMode = true)
        // Factorial
        val f = VFun.fromElement(Elements.elements("!"))
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
        assertResult(VList(3, 3, 3))(VList(ctx.pop(), ctx.pop(), ctx.pop()))
      }
    }
  }

  describe("Element G") {
    it("Should work as a generator") {
      testCode(
        "#[1|1#]λ2|+}G10Θ",
        VList(1, 1, 2, 3, 5, 8, 13, 21, 34, 55),
      )
    }
  }

  describe("Element M") {
    describe("when given a function and any value") {
      it("should map the function over the value") {
        testEquals(VList(2, 4, 6))(ctx ?=>
          ctx.push(VList(1, 2, 3))
          ctx.push(VFun(Elements.elements("+").impl, 2, List.empty, ctx))
          Interpreter.execute(AST.Command("M"))
          ctx.peek
        )
      }
      it("should work with strings") {
        testEquals(VList("aa", "bb", "cc"))(ctx ?=>
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
          ctx.push(VList(1))
          ctx.push(VFun(Elements.elements("+").impl, 2, List.empty, ctx))
          Interpreter.execute(AST.Command("R"))
          ctx.peek
        )
      }
      it("should calculate sum properly") {
        testEquals(15)(ctx ?=>
          ctx.push(VList(1, 2, 3, 4, 5))
          ctx.push(VFun(Elements.elements("+").impl, 2, List.empty, ctx))
          Interpreter.execute(AST.Command("R"))
          ctx.peek
        )
      }
    }
  }

  describe("Element g") {
    testCode(
      "#[1|1#]λ+}g10Θ",
      VList(1, 1, 2, 3, 5, 8, 13, 21, 34, 55),
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
    testCode("5 λ0=[1|1-x×}}Ė", 120)
    testCode("0 λ0=[1|1-x×}}Ė", 1)
  }

  describe("Element ÞĊ") {
    it("should work on lists") {
      testCode("#[1|2|3#] ÞĊ 10 Θ", VList(1, 2, 3, 1, 2, 3, 1, 2, 3, 1))
    }
  }

  describe("Element Ḋ") {
    it("simple test") {
      testCode(
        "#[1|2|3|4|5|6|7|8|9|10|1|4|5|1|3|6|4#] λ5%} Ḋ",
        VList(1, 2, 3, 4, 5),
      )
    }
  }

  describe("Element Ė") {
    describe("when given a number") {
      testMulti("Ė")(
        in(0) -> 1,
        in(1) -> 10,
        in(2) -> 100,
        in(-3) -> VNum(1) / 1000,
      )
    }

    describe("when given a string") {
      it("should properly execute code that uses the stack") {
        testCode(""" "1 2 + D" Ė """, 3)
      }

      it("should use the same context for executing the code") {
        // Doesn't use the test helpers because of context handling
        given ctx: Context = Context(inputs = List(3, 4), testMode = true)
        ctx.push("+")
        ctx.settings = ctx.settings.useMode(EndPrintMode.None)
        Interpreter.execute("Ė")
        assertResult(7: VNum)(ctx.peek)
      }
    }

    describe("when given a function") {
      it("should execute the function") {
        testEquals(3)(ctx ?=>
          ctx.push(1, 2)
          ctx.push(VFun.fromElement(Elements.elements("+")))
          Interpreter.execute(AST.Command("Ė"))
          ctx.peek
        )
      }
    }
  }

  describe("Element Ŀ") {
    it(
      "Generates a list of all numbers in the collatz conjecture minus the first number"
    ) {
      testCode("10 λe[2÷|3×1+}} Ŀ", VList(10, 5, 16, 8, 4, 2, 1))
    }
  }

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
        VList(
          VList(1, "A"),
          VList(1, "B"),
          VList(2, "A"),
          VList(2, "B"),
          VList(3, "A"),
          VList(3, "B"),
        )
      )(
        ListHelpers.cartesianProduct(
          VList(1, 2, 3),
          VList("A", "B"),
        )
      )
    }
    it("should handle two infinite lists properly") {
      assertResult(
        VList(
          VList(1, "A"),
          VList(1, "B"),
          VList(2, "A"),
          VList(1, "C"),
          VList(2, "B"),
          VList(3, "A"),
        )
      )(
        ListHelpers
          .cartesianProduct(
            VList.from(LazyList.iterate(VNum(1))(_ + 1)),
            VList.from(LazyList.from('A'.toInt).map(_.toChar.toString)),
          )
          .take(6)
      )
    }
  }

  describe("Element Ẇ") {
    testMulti(
      "λ5%3=}5Ẇ" -> VList(3, 8, 13, 18, 23)
    )
  }

  describe("Element ȧ") {
    testMulti(
      "#[1|2|3|4|5|6#] λ+} ȧ" -> VList(3, 5, 7, 9, 11),
      "#[1|2|3|4|5|6#] λ++} ȧ" -> VList(4, 7, 10, 13, 16),
    )
  }

  describe("Element form of ”") {
    testMulti(
      "#[1|2|3|4|5|6#] ƛ0ne”}" -> VList(0, 2, 0, 4, 0, 6)
    )
  }

  describe("Element Ạ") {
    testMulti(
      "#[1|2|3|4#] 0 λ1+} Ạ" -> VList(2, 2, 3, 4),
      "#[2|#[1|2|3|4#]|2|3|4#] 1 λṚ} Ạ" -> VList(2, VList(4, 3, 2, 1), 2, 3, 4),
    )
  }

  describe("Element Ɠ") {
    testStackLike("Ɠ")(
      in(VList(1, 2, 3)) -> in(VList(1, 2, 3), 3),
      in(VList(1, 2, 3), VList(4, 5, 6)) ->
        in(VList(1, 2, 3), VList(4, 5, 6), 6),
    )
  }

  describe("Element ɠ") {
    testStackLike("ɠ")(
      in(VList(1, 2, 3)) -> in(VList(1, 2, 3), 1),
      in(VList(1, 2, 3), VList(4, 5, 6)) ->
        in(VList(1, 2, 3), VList(4, 5, 6), 4),
    )
  }

  describe("Element Ṣ") {
    testMulti(
      "#[1|1#]Ṇ+}Ṣ10Θ" ->
        VList(
          VList(1),
          VList(1, 1),
          VList(1),
          VList(1, 1, 2),
          VList(1, 2),
          VList(1, 1, 2, 3),
          VList(2),
          VList(1, 2, 3),
          VList(1, 1, 2, 3, 5),
          VList(2, 3),
        ),
      "#[1#]Ṇ1+}Ṣ10Θ" ->
        VList(
          VList(1),
          VList(1, 2),
          VList(2),
          VList(1, 2, 3),
          VList(2, 3),
          VList(1, 2, 3, 4),
          VList(3),
          VList(2, 3, 4),
          VList(1, 2, 3, 4, 5),
          VList(3, 4),
        ),
    )
  }

  describe("Element ċ") {
    testMulti(
      "9ϩ½⌊ċ" -> VList(9, 4, 2, 1, 0)
    )
  }

  describe("Element ÞṆ") {
    testMulti(
      "ÞṆ10Θ" -> VList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
      "ÞṆ5+10Θ" -> VList(6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
    )
  }

  describe("Element ÞṬ") {
    testMulti(
      "ÞṬ20Θ" ->
        VList(0, 1, -1, 2, -2, 3, -3, 4, -4, 5, -5, 6, -6, 7, -7, 8, -8, 9, -9,
          10),
      "ÞṬ5+20Θ" ->
        VList(5, 6, 4, 7, 3, 8, 2, 9, 1, 10, 0, 11, -1, 12, -2, 13, -3, 14, -4,
          15),
    )
  }

  describe("Element ÞP") {
    testMulti(
      "ÞP20Θ" ->
        VList(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59,
          61, 67, 71)
    )
  }
end ElementTests
