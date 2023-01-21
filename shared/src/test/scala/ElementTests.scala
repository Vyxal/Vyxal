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

  describe("Element &") {
    describe("when given two lists") {
      it("should merge them") {
        given Context = Context()
        assertResult(VList(1, 2, 3, 4, 5, 6))(
          Impls.concatenate(VList(1, 2, 3), VList(4, 5, 6))
        )
      }
      it("should retain nestedness of lists flattened one level") {
        given Context = Context()
        assertResult(VList(1, 2, VList(3, 4)))(
          Impls.concatenate(VList(1, 2), VList(VList(3, 4)))
        )
      }
    }
    describe("when given a list and any non-list object") {
      it("should append the non-list to the end of the list") {
        given Context = Context()
        assertResult(VList(1, 2, 3))(
          Impls.concatenate(VList(1, 2), 3)
        )
      }
    }

    describe("when given a non-list and a list") {
      it("should prepend the non-list to the start of the list") {
        given Context = Context()
        assertResult(VList(1, 2, 3))(
          Impls.concatenate(1, VList(2, 3))
        )
      }
    }

    describe("when given two non-lists") {
      it("should concatenate two strings") {
        given Context = Context()
        assertResult("foo")(
          Impls.concatenate("f", "oo")
        )
        assertResult("vyxal")(
          Impls.concatenate("", "vyxal")
        )
      }
      it("should concatenate two numbers") {
        given Context = Context()
        assertResult(VNum(12))(
          Impls.concatenate(1, 2)
        )
        assertResult(VNum(0))(
          Impls.concatenate(0, 0)
        )
      }
      it("should cast num to string and concatenate") {
        given Context = Context()
        assertResult("1foo")(
          Impls.concatenate(1, "foo")
        )
        assertResult("foo1")(
          Impls.concatenate("foo", 1)
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
