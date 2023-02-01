package vyxal.impls

import vyxal.*

import org.scalatest.funspec.AnyFunSpec
import Elements.Impls

/** Tests for specific elements */
class ElementTests extends VyxalTests:

  describe("Element &") {
    it("should convert the first to a list and append the other onto it") {
      given Context = Context()
      assertResult(VList(1, 2, 3, VList(4, 5)))(
        Impls.append(VList(1, 2, 3), VList(4, 5))
      )
      assertResult(VList(1, 2, 3, 69))(Impls.append(VList(1, 2, 3), 69))
      assertResult(VList("a", "b", "c", VList()))(Impls.append("abc", VList()))
    }
  }

  describe("Element +") {
    describe("when given lists") {
      it("should vectorise properly") {
        given ctx: Context = Context()
        ctx.push(VList(VList(2, 5), "foo"), VList(VList(3, 4)))
        Interpreter.execute(AST.Command("+"))
        assertResult(VList(VList(5, 9), "foo0"))(ctx.pop())
      }
    }
    describe("when given two non-list values") {
      it("should add numbers properly") {
        given ctx: Context = Context()
        ctx.push(2, 3)
        Interpreter.execute(AST.Command("+"))
        assertResult(VNum(5))(ctx.pop())

        ctx.push(0, 0)
        Interpreter.execute(AST.Command("+"))
        assertResult(VNum(0))(ctx.pop())

        ctx.push(VNum("5.1"), VNum("-45.4"))
        Interpreter.execute(AST.Command("+"))
        assertResult(VNum("-40.3"))(ctx.pop())
      }
      it("should concatenate strings properly") {
        given ctx: Context = Context()
        ctx.push("foo", "bar")
        Interpreter.execute(AST.Command("+"))
        assertResult("foobar")(ctx.pop())
      }
      it("should concatenate numbers and strings properly") {
        given ctx: Context = Context()
        ctx.push("foo", 3)
        Interpreter.execute(AST.Command("+"))
        assertResult("foo3")(ctx.pop())

        ctx.push(3, "foo")
        Interpreter.execute(AST.Command("+"))
        assertResult("3foo")(ctx.pop())
      }
    }

    describe("when given functions") {
      it("should turn two functions into an fgh fork") {
        given ctx: Context = Context()
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
      it("should check if all are truthy") {
        given Context = Context()
        assertResult(1: VNum)(
          Impls.allTruthy(VList(1, 391, "dqw4w9wgxcq", VList(0)))
        )
        assertResult(0: VNum)(
          Impls.allTruthy(VList(0, 69420, VList()))
        )
      }
    }

    describe("when given a single-character string") {
      it("should return a single number according to if it is a vowel or not") {
        given Context = Context()
        assertResult(1: VNum)(Impls.allTruthy("a"))
        assertResult(1: VNum)(Impls.allTruthy("E"))
        assertResult(0: VNum)(Impls.allTruthy("y"))
      }
    }

    describe("when given a multi-character string") {
      it("should vectorize and work properly") {
        given Context = Context()
        assertResult(VList(1, 0, 0, 1, 0))(
          Impls.allTruthy("asdEy")
        )
      }
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
        given ctx: Context = Context()
        assertResult(5: VNum)(
          NumberHelpers.fromBinary(VList("1", "0", VList("0", "1")))
        )
      }
    }
  }

  describe("Element C") {
    describe("when given lists") {
      it("should count properly") {
        given Context = Context()
        assertResult(3: VNum)(
          Impls.count(VNum(3), VList(1, 3, 30, 2, 33, 4, 3, 3))
        )
        assertResult(0: VNum)(
          Impls.count(VList(1, 30, 2, 33, 4), VNum(3))
        )
        assertResult(1: VNum)(
          Impls.count(
            VList(1, 30, VList(VList("h"), VList("e"), VList("c")), 33, 4),
            VList(VList("h"), VList("e"), VList("c"))
          )
        )
      }
    }

    describe("when given strings") {
      it("should count properly") {
        given Context = Context()
        assertResult(3: VNum)(
          Impls.count("lolollol lol asd", "lol")
        )
        assertResult(0: VNum)(
          Impls.count("lolollol lol asd", "asdf")
        )
      }
    }

    describe("when given mixed types") {
      it("should convert both to string and count as usual") {
        testMulti("C")(
          List(VNum(12), VNum(1)) -> 1,
          List("ab1111ab", VNum(1)) -> 4,
          List(VNum(12341234), VNum(2)) -> 2,
          List(VNum(23432423), "3") -> 3
        )
      }
    }
  }

  describe("Element M") {
    describe("when given two lists") {
      it("should mold them properly") {
        given Context = Context()
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
        given ctx: Context = Context()
        assertResult(1: VNum)(
          Impls.reduction(
            VList(1),
            VFun(Elements.elements("+").impl, 2, List.empty, ctx)
          )
        )
      }
      it("should calculate sum properly") {
        given ctx: Context = Context()
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
        given ctx: Context = Context()
        assertResult(1: VNum)(Impls.execute(0))
        assertResult(100: VNum)(Impls.execute(2))
        assertResult(VNum(1) / 1000)(Impls.execute(-3))
      }
    }

    describe("when given a string") {
      it("should properly execute code that uses the stack") {
        given ctx: Context = Context()
        assertResult(3: VNum)(Impls.execute("1 2 + D"))
      }

      it("should use the same context for executing the code") {
        given ctx: Context = Context(inputs = List(3, 4))
        assertResult(7: VNum)(Impls.execute("+"))
      }
    }

    describe("when given a function") {
      it("should execute the function") {
        given ctx: Context = Context()
        ctx.push(1, 2)
        assertResult(3: VNum)(
          Impls.execute(VFun.fromElement(Elements.elements("+")))
        )
      }
    }
  }
end ElementTests
