package vyxal

import org.scalatest.funsuite.AnyFunSuite
import spire.math.{Complex, Real}

class InterpreterTests extends VyxalTests:
  describe("Literals") {
    testCode("Make lists", "#[1 | 2 3 + | 4#]", VList(1, 5, 4))
  }

  describe("Structures") {
    describe("If statements") {
      testMulti(
        "0 [ 2 | 5 } : [ 6 | 4} +" -> VNum(11)
      )
    }

    describe("While loops") {
      testMulti(
        "6 { : 2 - | 1 -}" -> VNum(2)
      )
    }
  }

  describe("Vectorisation") {
    describe("Simple monads") {
      testMulti(
        "#[100 | #[101 | 0#] #] vB" -> VList(4, VList(5, 0))
      )
    }

    describe("Simple dyads") {
      testMulti(
        "#[4 | #[5 | 6#] #] 3 v;" -> VList(
          VList(4, 3),
          VList(VList(5, 3), VList(6, 3))
        ),
        "#[4 | #[5 | 6#] #] #[4#] v;" -> VList(
          VList(4, 4),
          VList(VList(5, 0), VList(6, 0))
        )
      )
    }

    describe("Monadic lambdas") {
      testAST(
        "Vectorise lambda for factorial",
        Modifiers
          .modifiers("v")
          .from(List(AST.Lambda(1, List.empty, AST.Command("!")))),
        VList(1, 6, VList(2, 1)),
        Context(inputs = Seq(VList(0, 3, VList(2, 1))))
      )
    }

    describe("Dyadic lambdas") {
      testAST(
        "Vectorise lambda for subtraction",
        Modifiers
          .modifiers("v")
          .from(List(AST.Lambda(2, List.empty, AST.Command("-")))),
        VList(-4, 1, VList(-4, -5)),
        Context(inputs = Seq(VList(0, 3, VList(2, 1)), VList(4, 2, 6)))
      )
    }
  }

  describe("Executing lambdas/functions") {
    describe("Named functions") {
      testAST(
        "should execute a simple named function",
        AST.makeSingle(
          AST.FnDef("f", AST.Lambda(2, List.empty, AST.Command("-"))),
          AST.GetVar("f"),
          AST.Command("Ė")
        ),
        VNum(-1),
        Context(inputs = Seq(3, 4))
      )
    }

    describe("Monadic lambdas") {
      testAST(
        "Simple lambda",
        AST.makeSingle(
          AST.Lambda(1, List.empty, AST.Command("!")),
          AST.ExecuteFn
        ),
        VNum(6),
        Context(inputs = Seq(3))
      )
    }

    describe("Dyadic lambdas") {
      testAST(
        "Simple lambda",
        AST.makeSingle(
          AST.Lambda(2, List.empty, AST.Command("-")),
          AST.ExecuteFn
        ),
        VNum(2),
        Context(inputs = Seq(3, 1))
      )
    }
  }

  describe("Variables") {
    it("should set the ghost variable") {
      testEquals(3) { ctx ?=>
        ctx.push(3)
        Interpreter.execute("#=")
        ctx.getVar("")
      }
    }

    describe("Augmented assignment") {
      it("should work with builtin elements") {
        testEquals(4) { ctx ?=>
          ctx.setVar("x", 3)
          Interpreter.execute("1 +#>x")
          ctx.getVar("x")
        }
      }
      it("should work with lambdas") {
        testEquals(18) { ctx ?=>
          ctx.setVar("x", 3)
          Interpreter.execute("λ+×}#>x #$x")
          ctx.getVar("x")
        }
      }
    }
  }

  // todo convert the rest of these tests
  // test("Does the interpreter handle variable unpacking?") {
  //   given ctx: Context = Context()
  //   Interpreter.execute("#[1 | 2 | 3#] #:[x|y|z]")
  //   assert(ctx.getVar("x") == VNum(1))
  //   assert(ctx.getVar("y") == VNum(2))
  //   assert(ctx.getVar("z") == VNum(3))

  //   Interpreter.execute("#[1 | 2 | #[3#]#] #:[x|y|z]")
  //   assert(ctx.getVar("x") == VNum(1))
  //   assert(ctx.getVar("y") == VNum(2))
  //   assert(ctx.getVar("z") == VList(3))

  //   Interpreter.execute("#[1 | 2 | #[3#]#] #:[x|y|[z]]")
  //   assert(ctx.getVar("x") == VNum(1))
  //   assert(ctx.getVar("y") == VNum(2))
  //   assert(ctx.getVar("z") == VNum(3))
  // }

  // test("Can the interpreter get nonlocal variables inside lambdas?") {
  //   given ctx: Context = Context()
  //   ctx.setVar("x", 5)
  //   ctx.push(1)
  //   assertResult(VNum(5))(
  //     Interpreter.executeFn(
  //       VFun.fromLambda(AST.Lambda(1, Nil, AST.GetVar("x")))
  //     )
  //   )
  // }

  // test("Can the interpreter set nonlocal variables inside lambdas?") {
  //   given ctx: Context = Context()
  //   ctx.setVar("x", 5)
  //   ctx.push(1)
  //   Interpreter.executeFn(
  //     VFun.fromLambda(AST.Lambda(1, Nil, AST.AugmentVar("x", AST.Command("+"))))
  //   )
  //   assertResult(VNum(6))(ctx.getVar("x"))
  // }

  // test("References to variables in other contexts") {
  //   val ctx1 = Context()
  //   ctx1.setVar("x", 5)
  //   Interpreter.execute(
  //     AST.Lambda(1, Nil, AST.AugmentVar("x", AST.Command("+")))
  //   )(using ctx1)
  //   val ctx2 = Context()
  //   ctx2.setVar("x", "foo")
  //   ctx2.push(1)
  //   ctx2.push(ctx1.pop())
  //   Interpreter.execute(AST.ExecuteFn)(using ctx2)
  //   assertResult((VNum(6), "foo"))((ctx1.getVar("x"), ctx2.getVar("x")))
  // }

  // todo come up with a way to easily have multiple assertResults in one group
  // and still run them all
  describe("Numeric literals") {
    it("should parse simple integers correctly") {
      assertResult(VNum(0))(VNum.from("0"))
      assertResult(VNum(1))(VNum.from("1"))
      assertResult(VNum(BigInt("123456789123456789")))(
        VNum.from("123456789123456789")
      )
    }
    it("should parse decimals correctly") {
      assertResult(VNum(Real("6.9")))(VNum.from("6.9"))
      assertResult(VNum(Real("0.9")))(VNum.from("0.9"))
      assertResult(VNum(Real("0.9")))(VNum.from(".9"))
    }
    it("should handle trailing dots correctly") {
      assertResult(VNum(Real("0.5")))(VNum.from("."))
      assertResult(VNum(Real("0.5")))(VNum.from("0."))
      assertResult(VNum(Real("5.5")))(VNum.from("5."))
    }
    it("should handle complex numbers correctly") {
      assertResult(VNum.complex(0, 1))(VNum.from("ı"))
      assertResult(VNum.complex(0, 1))(VNum.from("0ı"))
      assertResult(VNum.complex(0.5, 1))(VNum.from("0.ı"))
      assertResult(VNum.complex(0.5, 0.5))(VNum.from(".ı."))
      assertResult(VNum.complex(69, 420))(VNum.from("69ı420"))
    }
    it("should handle digits too large for the radix") {
      assertResult(VNum(23))(VNum.from("A3", 2))
    }
    it("should handle invalid characters") {
      assertResult(VNum(Real("12.3")))(VNum.from("1@#$%2#$%. 3"))
      assertResult(VNum(Real(0)))(VNum.from("@#  `/$%#$% "))
    }
  }

end InterpreterTests
