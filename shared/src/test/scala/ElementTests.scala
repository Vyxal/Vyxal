package vyxal.impls

import vyxal.*

import org.scalatest.funspec.AnyFunSpec
import Elements.Impls

/** Tests for specific elements */
class ElementTests extends AnyFunSpec:

  describe("Element +") {
    describe("when given lists") {
      it("should vectorise properly") {
        given ctx: Context = Context()
        ctx.push(VList(VList(2, 5), "foo"), VList(VList(3, 4)))
        Interpreter.execute(AST.Command("+"))
        assertResult(VList(VList(5, 9), "foo0"))(ctx.pop())
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

  describe("Element &") {
    it("should convert the first to a list and append the other onto it") {
      given Context = Context()
      assertResult(VList(1, 2, 3, VList(4, 5)))(Impls.append(VList(1, 2, 3), VList(4, 5)))
      assertResult(VList(1, 2, 3, 69))(Impls.append(VList(1, 2, 3), 69))
      assertResult(VList("a", "b", "c", VList()))(Impls.append("abc", VList()))
    }
  }

  describe("Element C") {
    describe("when given lists") {
      it("should count properly") {
        given Context = Context()
        assertResult(3: VNum)(
          Impls.count(VNum(3), VList(1, 3, 30, 2, 33, 4, 3, 3))
        )
      }
    }

    describe("when given strings") {
      it("should count properly") {
        given Context = Context()
        assertResult(3: VNum)(
          Impls.count("lolollol lol asd", "lol")
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
