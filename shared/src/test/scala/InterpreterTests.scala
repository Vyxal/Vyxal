package vyxal

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.tagobjects.Slow
import spire.math.{Complex, Real}

class InterpreterTests extends VyxalTests:
  describe("Literals") {
    it("should make lists") {
      testCode("#[1 | 2 3 + | 4#]", VList(1, 5, 4))
    }
  }

  describe("Structures") {
    describe("Ternary statements") {
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
            AST.Command("Ė")
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
            AST.Command("Ė")
          ),
          VNum(2),
          inputs = Seq(3, 1)
        )
      }
    }

    describe("Multibranch lambdas") {
      testMulti(
        "#[1|2|3|4#]ƛ5+|×}" -> VList(36, 49, 64, 81),
        "#[1|2|3|4#]ƛ5+×}" -> VList(6, 14, 24, 36),
        "#[1|2|3|4#]ƛ5+|×|÷}" -> VList(1, 1, 1, 1),
      )

      testMulti(
        """#["Hello"|"World"|"Gaming"|"Test String"#]Ω"o"C1=|m0=}""" -> VList(
          "Hello"
        ),
        """#["Hello"|"World"|"Goming"|"Test String"#]Ω"o"C1=|m2%0=}""" -> VList(
          "Hello",
          "Goming"
        )
      )

      testMulti(
        "#[4|3N|1|5|3|7|5N#]µ0<[N}|N" -> VList(1, 3, -3, 4, 5, -5, 7),
        "#[4|3N|1|5|3|7|5N#]µ0<[N}N" -> VList(7, 5, -5, 4, -3, 3, 1)
      )
    }

    describe("Lambda arguments") {
      testMulti(
        "#[1|2|3|4|5#]λx|#$x 5+}M" -> VList(6, 7, 8, 9, 10),
        "#[1|2|3|4|5#]λ5+}M" -> VList(6, 7, 8, 9, 10),
        "#[1|2|3|4|5#]λ1|5+}M" -> VList(6, 7, 8, 9, 10)
      )
    }

    describe("Operating on the stack") {
      testMulti(
        "3 6 1λ!|+}ĖW" -> VList(3, 7),
        "3 6 1λ!|++}ĖW" -> VList(10),
        "3 6 1λ!|n}ĖW" -> VList(3, 6, 1, 0)
      )
    }

    describe("Varargs") {
      testMulti("1 2 3 3λ*|/+}Ė" -> 6, "1 2 3 2λ*|/+}Ė" -> 5)
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
      it("should not be able to set nonlocal variables inside lambdas") {
        given ctx: Context = Context()
        ctx.setVar("x", 5)
        ctx.push(1)
        Interpreter.executeFn(
          VFun.fromLambda(
            AST.Lambda(1, Nil, List(AST.AugmentVar("x", AST.Command("+"))))
          )
        )
        assertResult(VNum(5))(ctx.getVar("x"))
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

  describe("Lambda types") {
    testMulti(
      "#[1|2|3#] ƛ5R}" -> VList(VList(1, 2, 3, 4), VList(2, 3, 4), VList(3, 4)),
      "#[1|2|3#] ƛ2+|+|-}" -> VList(0, 0, 0),
      "#[1|2|3|4|5#]ƛ#=x+|#$x}" -> VList(1, 2, 3, 4, 5)
    )

    testMulti(
      "10 Ω2%0=}" -> VList(2, 4, 6, 8, 10),
      "10 Ω2%0=|5<}" -> VList(2, 4),
      "1 20RΩ5+:#=x 20<|#$x 10>" -> VList(6, 7, 8, 9, 10, 11, 12, 13, 14),
      "1 20RΩ5+:#=x 20<|5+10>" -> VList(6, 7, 8, 9, 10, 11, 12, 13, 14),
    )
  }

  describe("If statements") {
    testMulti(
      "5 #=x #{#$x 4 =|1|#$x 5 =|2}" -> VNum(2),
      "6 #=x #{#$x 4 =|1|#$x 5 =|2|3}" -> VNum(3),
      "4 #=x #{#$x 4 =|1|#$x 5 =|2|3}" -> VNum(1),
    )
  }

  describe("Decision problem structure") {
    testMulti(
      "#[14|16|120|881#]Ḍ2%1=}" -> VNum(1),
      "#[14|16|120|882#]Ḍ2%1=}" -> VNum(0),
      "Ḍ2%1=|#[14|16|120|881#]}" -> VNum(1),
      "Ḍ2%1=|#[14|16|120|882#]}" -> VNum(0),
    )
  }

  describe("Generator structure") {
    testMulti(
      "#[1|1#]Ṇ+}10Θ" -> VList(1, 1, 2, 3, 5, 8, 13, 21, 34, 55),
      "Ṇ+|#[1|1#]}10Θ" -> VList(1, 1, 2, 3, 5, 8, 13, 21, 34, 55),
    )

    it("Should work with big numbers", Slow) {
      testCode(
        "Ṇ+|#[1|1#]}9999i",
        VNum(
          "33644764876431783266621612005107543310302148460680063906564769974680081442166662368155595513633734025582065332680836159373734790483865268263040892463056431887354544369559827491606602099884183933864652731300088830269235673613135117579297437854413752130520504347701602264758318906527890855154366159582987279682987510631200575428783453215515103870818298969791613127856265033195487140214287532698187962046936097879900350962302291026368131493195275630227837628441540360584402572114334961180023091208287046088923962328835461505776583271252546093591128203925285393434620904245248929403901706233888991085841065183173360437470737908552631764325733993712871937587746897479926305837065742830161637408969178426378624212835258112820516370298089332099905707920064367426202389783111470054074998459250360633560933883831923386783056136435351892133279732908133732642652633989763922723407882928177953580570993691049175470808931841056146322338217465637321248226383092103297701648054726243842374862411453093812206564914032751086643394517512161526545361333111314042436854805106765843493523836959653428071768775328348234345557366719731392746273629108210679280784718035329131176778924659089938635459327894523777674406192240337638674004021330343297496902028328145933418826817683893072003634795623117103101291953169794607632737589253530772552375943788434504067715555779056450443016640119462580972216729758615026968443146952034614932291105970676243268515992834709891284706740862008587135016260312071903172086094081298321581077282076353186624611278245537208532365305775956430072517744315051539600905168603220349163222640885248852433158051534849622434848299380905070483482449327453732624567755879089187190803662058009594743150052402532709746995318770724376825907419939632265984147498193609285223945039707165443156421328157688908058783183404917434556270520223564846495196112460268313970975069382648706613264507665074611512677522748621598642530711298441182622661057163515069260029861704945425047491378115154139941550671256271197133252763631939606902895650288268608362241082050562430701794976171121233066073310059947366875"
        )
      )
    }
  }

end InterpreterTests
