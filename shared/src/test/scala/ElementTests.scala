package vyxal.impls

import vyxal.*

import org.scalatest.funspec.AnyFunSpec
import Elements.Impls

/** Tests for specific elements */
class ElementTests extends VyxalTests:

  describe("Element &") {
    testMulti("&")(
      List[VAny](VList(1, 2, 3), VList(4, 5)) -> VList(1, 2, 3, VList(4, 5)),
      List[VAny](VList(1, 2, 3), 69) -> VList(1, 2, 3, 69),
      List[VAny]("abc", VList()) -> VList("a", "b", "c", VList())
    )
  }

  describe("Element +") {
    describe("when given lists") {
      testMulti("+")(
        List[VAny](VList(VList(2, 5), "foo"), VList(VList(3, 4))) -> VList(
          VList(5, 9),
          "foo0"
        )
      )
    }
    describe("when given two non-list values") {
      testMulti("+")(
        List[VAny](2, 3) -> 5,
        List[VAny](0, 0) -> 0,
        List[VAny](VNum("5.1"), VNum("-45.4")) -> VNum("-40.3"),
        List[VAny]("foo", "bar") -> "foobar",
        List[VAny]("foo", 3) -> "foo3",
        List[VAny](3, "foo") -> "3foo"
      )
    }

    describe("when given functions") {
      it("should turn two functions into an fgh fork") {
        given ctx: Context = Context(testMode = true)
        // Factorial
        val f = VFun.fromElement(Elements.elements("!"))
        // Function to subtract 8
        val g = VFun.fromLambda(
          AST.Lambda(
            1,
            List.empty,
            AST.makeSingle(AST.Number(8), AST.Command("-"))
          )
        )
        ctx.push(3, f, g)
        Interpreter.execute(AST.Command("+"))
        Interpreter.execute(AST.ExecuteFn)
        assertResult(VNum(1))(ctx.pop())
      }
    }
  }

  describe("Element A") {
    describe("when given lists") {
      testMulti("A")(
        List[VAny](VList(1, 391, "dqw4w9wgxcq", VList(0))) -> 1,
        List[VAny](VList(0, 69420, VList())) -> 0
      )
    }

    describe("when given a single-character string") {
      testMulti("A")(
        List[VAny]("a") -> 1,
        List[VAny]("E") -> 1,
        List[VAny]("y") -> 0
      )
    }

    describe("when given a multi-character string") {
      testMulti("A")(List[VAny]("asdEy") -> VList(1, 0, 0, 1, 0))
    }
  }

  describe("Element B") {
    describe("when given a number") {
      testMulti(
        "110 B" -> VNum(6),
        "1000 B" -> VNum(8),
        "69 B" -> VNum(21),
        "69420 B" -> VNum(188),
        "7654 N B" -> VNum(-94),
        "111 N B" -> VNum(-7)
      )
    }
    describe("when given a string") {
      testMulti(
        "\"110\" B" -> VNum(6),
        "\"1000\" B" -> VNum(8),
        "\"69\" B" -> VNum(21),
        "\"69420\" B" -> VNum(188),
        "\"-7654\" B" -> VNum(-94),
        "\"-111\" B" -> VNum(-7)
      )
    }
    describe("when given a list") {
      testMulti(
        "#[1|1|0#] B" -> VNum(6),
        "#[1|0|0|0#] B" -> VNum(8),
        "#[6|9#] B" -> VNum(21),
        "#[6|9|4|2|0#] B" -> VNum(188),
        "#[7|6|5|4#] N B" -> VNum(-94),
        "#[1|1|1#] N B" -> VNum(-7)
      )
    }
    describe("With lists of strings and lists") {
      it("Shouldn't do string multiplication") {
        given ctx: Context = Context(testMode = true)
        assertResult(5: VNum)(
          NumberHelpers.fromBinary(VList("1", "0", VList("0", "1")))
        )
      }
    }
  }

  describe("Element C") {
    describe("when given lists") {
      testMulti("C")(
        List[VAny](VNum(3), VList(1, 3, 30, 2, 33, 4, 3, 3)) -> 3,
        List[VAny](VList(1, 30, 2, 33, 4), VNum(3)) -> 0,
        List[VAny](
          VList(1, 30, VList(VList("h"), VList("e"), VList("c")), 33, 4),
          VList(VList("h"), VList("e"), VList("c"))
        ) -> 1
      )
    }

    describe("when given strings") {
      testMulti("C")(
        List[VAny]("lolollol lol asd", "lol") -> 3,
        List[VAny]("lolollol lol asd", "asdf") -> 0
      )
    }

    describe("when given mixed types") {
      testMulti("C")(
        List[VAny](VNum(12), VNum(1)) -> 1,
        List[VAny]("ab1111ab", VNum(1)) -> 4,
        List[VAny](VNum(12341234), VNum(2)) -> 2,
        List[VAny](VNum(23432423), "3") -> 3
      )
    }
  }

  describe("Element M") {
    describe("when given two lists") {
      it("should mold them properly") {
        given Context = Context(testMode = true)
        assertResult(VList(1, 2, VList(VList(VList(3, 4), 5, 1), 2)))(
          Impls.mapElement(
            VList(1, 2, VList(3, 4), 5),
            VList(1, 2, VList(VList(3, 4, 6), 5))
          )
        )
      }
    }
  }

  describe("Element R") {
    describe("when given function and iterable") {
      it("should work with singleton lists") {
        given ctx: Context = Context(testMode = true)
        assertResult(1: VNum)(
          Impls.reduction(
            VList(1),
            VFun(Elements.elements("+").impl, 2, List.empty, ctx)
          )
        )
      }
      it("should calculate sum properly") {
        given ctx: Context = Context(testMode = true)
        assertResult(15: VNum)(
          Impls.reduction(
            VNum(5),
            VFun(Elements.elements("+").impl, 2, List.empty, ctx)
          )
        )
      }
    }
  }

  describe("Element Ė") {
    describe("when given a number") {
      it("should do 10**n properly") {
        given ctx: Context = Context(testMode = true)
        assertResult(1: VNum)(Impls.execute(0))
        assertResult(100: VNum)(Impls.execute(2))
        assertResult(VNum(1) / 1000)(Impls.execute(-3))
      }
    }

    describe("when given a string") {
      it("should properly execute code that uses the stack") {
        given ctx: Context = Context(testMode = true)
        assertResult(3: VNum)(Impls.execute("1 2 + D"))
      }

      it("should use the same context for executing the code") {
        given ctx: Context = Context(inputs = List(3, 4), testMode = true)
        assertResult(7: VNum)(Impls.execute("+"))
      }
    }

    describe("when given a function") {
      it("should execute the function") {
        given ctx: Context = Context(testMode = true)
        ctx.push(1, 2)
        assertResult(3: VNum)(
          Impls.execute(VFun.fromElement(Elements.elements("+")))
        )
      }
    }
  }
end ElementTests
