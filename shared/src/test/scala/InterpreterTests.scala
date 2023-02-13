package vyxal

import org.scalatest.funsuite.AnyFunSuite
import spire.math.{Complex, Real}

class InterpreterTests extends VyxalTests:
  describe("Literals") {
    it("should make lists") {
      testCode("#[1 | 2 3 + | 4#]", VList(1, 5, 4))
    }
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

    describe("For loops") {
      testMulti(
        "1 5 ( 1 + }" -> VNum(6)
      )
    }

    describe("Map lambda") {
      testMulti(
        "10 ƛ 5 + }" -> VList(6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
        "10 ƛ n + }" -> VList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20),
        "10 ƛ m + }" -> VList(1, 3, 5, 7, 9, 11, 13, 15, 17, 19)
      )
    }

    describe("Reduce lambda") {
      testMulti(
        "10 ₳ + }" -> VNum(55),
        "10 ₳ n m + }" -> VNum(55)
      )
    }
  }

  describe("Vectorisation") {
    describe("Simple monads") {
      testMulti("#[100 | #[101 | 0#] #] vB" -> VList(4, 202))
    }

    describe("Simple dyads") {
      testMulti(
        "#[4 | #[5 | 6#] #] 3 v;" -> VList(
          VList(4, 3),
          VList(VList(5, 6), 3)
        ),
        "#[4 | #[5 | 6#] #] #[4#] v;" -> VList(
          VList(4, VList(4)),
          VList(VList(5, 6), VList(4))
        )
      )
    }

    describe("Monadic lambdas") {
      it("should vectorise lambda for factorial") {
        testAST(
          Modifiers
            .modifiers("v")
            .from(List(AST.Lambda(1, List.empty, List(AST.Command("!"))))),
          VList(1, 6, VList(2, 1)),
          inputs = Seq(VList(0, 3, VList(2, 1)))
        )
      }
    }

    describe("Dyadic lambdas") {
      it("should vectorise lambda for subtraction") {
        testAST(
          Modifiers
            .modifiers("v")
            .from(List(AST.Lambda(2, List.empty, List(AST.Command("-"))))),
          VList(VList(-4, -2, -6), VList(-1, 1, -3), VList(-2, -1, -6)),
          inputs = Seq(VList(0, 3, VList(2, 1)), VList(4, 2, 6))
        )
      }
    }
  }

  describe("Executing lambdas/functions") {
    it("should execute a simple named function") {
      testAST(
        AST.makeSingle(
          AST.FnDef("f", AST.Lambda(2, List.empty, List(AST.Command("-")))),
          AST.GetVar("f"),
          AST.Command("Ė")
        ),
        VNum(-1),
        inputs = Seq(3, 4)
      )
    }

    describe("Monadic lambdas") {
      it("Simple lambda") {
        testAST(
          AST.makeSingle(
            AST.Lambda(1, List.empty, List(AST.Command("!"))),
            AST.ExecuteFn
          ),
          VNum(6),
          inputs = Seq(3)
        )
      }
    }

    describe("Dyadic lambdas") {
      it("Simple lambda") {
        testAST(
          AST.makeSingle(
            AST.Lambda(2, List.empty, List(AST.Command("-"))),
            AST.ExecuteFn
          ),
          VNum(2),
          inputs = Seq(3, 1)
        )
      }
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
    describe("Variable unpacking") {
      it("should handle non nested lists") {
        given ctx: Context = Context(testMode = true)
        Interpreter.execute("#[1 | 2 | 3#] #:[x|y|z]")
        group {
          assertResult(VNum(1))(ctx.getVar("x"))
          assertResult(VNum(2))(ctx.getVar("y"))
          assertResult(VNum(3))(ctx.getVar("z"))
        }
      }
      it("should handle nested lists") {
        given ctx: Context = Context()
        Interpreter.execute("#[1 | 2 | #[3#]#] #:[x|y|z]")
        group {
          assertResult(VNum(1))(ctx.getVar("x"))
          assertResult(VNum(2))(ctx.getVar("y"))
          assertResult(VList(3))(ctx.getVar("z"))
        }
      }
      it("should handle simple nested patterns") {
        given ctx: Context = Context()
        Interpreter.execute("#[1 | 2 | #[3#]#] #:[x|y|[z]]")
        group {
          assertResult(VNum(1))(ctx.getVar("x"))
          assertResult(VNum(2))(ctx.getVar("y"))
          assertResult(VNum(3))(ctx.getVar("z"))
        }
      }
    }
    describe("Nonlocal variables") {
      it("should be able to get nonlocal variables inside lambdas") {
        given ctx: Context = Context()
        ctx.setVar("x", 5)
        ctx.push(1)
        assertResult(VNum(5))(
          Interpreter.executeFn(
            VFun.fromLambda(AST.Lambda(1, Nil, List(AST.GetVar("x"))))
          )
        )
      }
      it("should be able to set nonlocal variables inside lambdas?") {
        given ctx: Context = Context()
        ctx.setVar("x", 5)
        ctx.push(1)
        Interpreter.executeFn(
          VFun.fromLambda(
            AST.Lambda(1, Nil, List(AST.AugmentVar("x", AST.Command("+"))))
          )
        )
        assertResult(VNum(6))(ctx.getVar("x"))
      }
      it(
        "should resolve to variables from original ctx inside of current ctx"
      ) {
        val ctx1 = Context()
        ctx1.setVar("x", 5)
        Interpreter.execute(
          AST.Lambda(1, Nil, List(AST.AugmentVar("x", AST.Command("+"))))
        )(using ctx1)
        val ctx2 = Context()
        ctx2.setVar("x", "foo")
        ctx2.push(1)
        ctx2.push(ctx1.pop()) // Push the lambda defined earlier
        Interpreter.execute(AST.ExecuteFn)(using ctx2)
        assertResult((VNum(6), "foo"))((ctx1.getVar("x"), ctx2.getVar("x")))
      }
    }
  }

  describe("Numeric literals") {
    it("should parse simple integers correctly") {
      group {
        assertResult(VNum(0))(VNum("0"))
        assertResult(VNum(1))(VNum("1"))
        assertResult(VNum(BigInt("9999999999999999999")))(
          VNum("9999999999999999999")
        )
      }
    }
    it("should parse decimals correctly") {
      group {
        assertResult(VNum(6.9))(VNum("6.9"))
        assertResult(VNum(0.1))(VNum("0.1"))
        assertResult(VNum(0.9))(VNum(".9"))
      }
    }
    it("should handle signs correctly") {
      group {
        assertResult(VNum(-6.9))(VNum("-6.9"))
        assertResult(VNum(3.0))(VNum("+3.0"))
        assertResult(VNum.complex(5.7, -1))(VNum("+5.7ı-"))
      }
    }
    it("should handle trailing dots correctly") {
      group {
        assertResult(VNum(0.5))(VNum("."))
        assertResult(VNum(0.5))(VNum("0."))
        assertResult(VNum(5.5))(VNum("5."))
      }
    }
    it("should handle complex numbers correctly") {
      group {
        assertResult(VNum.complex(0, 1))(VNum("ı"))
        assertResult(VNum.complex(0, 1))(VNum("0ı"))
        assertResult(VNum.complex(0.5, 1))(VNum("0.ı"))
        assertResult(VNum.complex(0.5, 0.5))(VNum(".ı."))
        assertResult(VNum.complex(69, 420))(VNum("69ı420"))
      }
    }
    it("should handle digits too large for the radix") {
      group {
        assertResult(VNum(23))(VNum("A3", 2))
      }
    }
    it("should handle invalid characters") {
      group {
        assertResult(VNum(12.3))(VNum("1@#$%2#$%. 3"))
        assertResult(VNum(0))(VNum("@#  `/$%#$% "))
      }
    }
    it("shouldn't be too tolerant when comparing") {
      group {
        assert(VNum(12.2999) != VNum("12.3"))
        assert(VNum(0.01) != VNum(0))
      }
    }
  }

end InterpreterTests
