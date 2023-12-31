package vyxal

import vyxal.parsing.Lexer

import org.scalatest.tagobjects.Slow
import spire.math.Real

class InterpreterTests extends VyxalTests:
  def testCodeAsLiterate(input: String, expected: VAny): Unit =
    val literate = Lexer.lexLiterate(input)
    val sbcsified = Lexer.sbcsify(literate)
    println(sbcsified)
    testCode(sbcsified, expected)
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
        "10 ƛ m + }" -> VList(1, 3, 5, 7, 9, 11, 13, 15, 17, 19),
      )
    }

    describe("Reduce lambda") {
      testMulti(
        "10 ₳ + }" -> VNum(55),
        "10 ₳ n m + }" -> VNum(55),
      )
    }
  }

  describe("Vectorisation") {
    describe("Simple monads") {
      testMulti("#[100 | #[101 | 0#] #] ᵛB" -> VList(4, 202))
    }

    describe("Simple dyads") {
      testMulti(
        "#[4 | #[5 | 6#] #] 3 ᵛ;" ->
          VList(
            VList(4, 3),
            VList(VList(5, 6), 3),
          ),
        "#[4 | #[5 | 6#] #] #[4#] ᵛ;" ->
          VList(
            VList(4, VList(4)),
            VList(VList(5, 6), VList(4)),
          ),
      )
    }

    describe("Monadic lambdas") {
      it("should vectorise lambda for factorial") {
        testAST(
          Modifiers
            .modifiers("ᵛ")
            .from(
              List(AST.Lambda(Some(1), List.empty, List(AST.Command("!"))))
            ),
          VList(1, 6, VList(2, 1)),
          inputs = Seq(VList(0, 3, VList(2, 1))),
        )
      }
    }

    describe("Dyadic lambdas") {
      it("should vectorise lambda for subtraction") {
        testAST(
          Modifiers
            .modifiers("ᵛ")
            .from(
              List(AST.Lambda(Some(2), List.empty, List(AST.Command("-"))))
            ),
          VList(VList(-4, -2, -6), VList(-1, 1, -3), VList(-2, -1, -6)),
          inputs = Seq(VList(0, 3, VList(2, 1)), VList(4, 2, 6)),
        )
      }
    }
  }

  describe("Executing lambdas/functions") {
    it("should execute a simple named function") {
      testAST(
        AST.makeSingle(
          AST.FnDef(
            "f",
            AST.Lambda(Some(2), List.empty, List(AST.Command("-"))),
          ),
          AST.GetVar("f"),
          AST.Command("Ė"),
        ),
        VNum(-1),
        inputs = Seq(3, 4),
      )
    }

    describe("Monadic lambdas") {
      it("Simple lambda") {
        testAST(
          AST.makeSingle(
            AST.Lambda(Some(1), List.empty, List(AST.Command("!"))),
            AST.Command("Ė"),
          ),
          VNum(6),
          inputs = Seq(3),
        )
      }
    }

    describe("Dyadic lambdas") {
      it("Simple lambda") {
        testAST(
          AST.makeSingle(
            AST.Lambda(Some(2), List.empty, List(AST.Command("-"))),
            AST.Command("Ė"),
          ),
          VNum(2),
          inputs = Seq(3, 1),
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
        """#["Hello"|"World"|"Gaming"|"Test String"#]Ω"o"C1=|m0=}""" ->
          VList(
            "Hello"
          ),
        """#["Hello"|"World"|"Goming"|"Test String"#]Ω"o"C1=|m2%0=}""" ->
          VList(
            "Hello",
            "Goming",
          ),
      )

      testMulti(
        "#[4|3N|1|5|3|7|5N#]µ0<[N}|N" -> VList(1, 3, -3, 4, 5, -5, 7),
        "#[4|3N|1|5|3|7|5N#]µ0<[N}N" -> VList(7, 5, -5, 4, -3, 3, 1),
      )
    }

    describe("Lambda arguments") {
      testMulti(
        "#[1|2|3|4|5#]λx|#$x 5+}M" -> VList(6, 7, 8, 9, 10),
        "#[1|2|3|4|5#]λ5+}M" -> VList(6, 7, 8, 9, 10),
        "#[1|2|3|4|5#]λ1|5+}M" -> VList(6, 7, 8, 9, 10),
      )
    }

    describe("Operating on the stack") {
      testMulti(
        "3 6 1λ!|+}ĖW" -> VList(3, 7),
        "3 6 1λ!|++}ĖW" -> VList(10),
        "3 6 1λ!|n}ĖW" -> VList(3, 6, 1, "abcdefghijklmnopqrstuvwxyz"),
      )
    }

    describe("Varargs") {
      testMulti("1 2 3 3λ*|W/+}Ė" -> 6, "1 2 3 2λ*|W/+}Ė" -> 5)
    }

    describe("Explicit arguments") {
      they("should actually be passed to the function") {
        given Context = VyxalTests.testContext()
        assertResult(VNum(3))(
          Interpreter.executeFn(
            VFun.fromElement(Elements.elements("+")),
            args = Seq(1, 2),
          )
        )
      }
    }
  }

  describe("Variables") {
    it("should set the ghost variable") {
      testEquals(3) { ctx ?=>
        Interpreter.execute("3 #=₉")
        ctx.getVar("")
      }
    }

    describe("Augmented assignment") {
      it("should work with builtin elements") {
        testEquals(4) { ctx ?=>
          ctx.setVar("x", 3)
          Interpreter.execute("1 +#>x₉")
          ctx.getVar("x")
        }
      }
      it("should work with lambdas") {
        testEquals(18) { ctx ?=>
          ctx.setVar("x", 3)
          Interpreter.execute("λ+×}#>x #$x₉")
          ctx.getVar("x")
        }
      }
    }
    describe("Variable unpacking") {
      it("should handle non nested lists") {
        given ctx: Context = Context(testMode = true)
        Interpreter.execute("#[1 | 2 | 3#] #:[x|y|z]₉")
        group {
          assertResult(VNum(1))(ctx.getVar("x"))
          assertResult(VNum(2))(ctx.getVar("y"))
          assertResult(VNum(3))(ctx.getVar("z"))
        }
      }
      it("should handle nested lists") {
        given ctx: Context = Context()
        Interpreter.execute("#[1 | 2 | #[3#]#] #:[x|y|z]₉")
        group {
          assertResult(VNum(1))(ctx.getVar("x"))
          assertResult(VNum(2))(ctx.getVar("y"))
          assertResult(VList(3))(ctx.getVar("z"))
        }
      }
      it("should handle simple nested patterns") {
        given ctx: Context = Context()
        Interpreter.execute("#[1 | 2 | #[3#]#] #:[x|y|[z]]₉")
        group {
          assertResult(VNum(1))(ctx.getVar("x"))
          assertResult(VNum(2))(ctx.getVar("y"))
          assertResult(VNum(3))(ctx.getVar("z"))
        }
      }
    }

    describe("Constants") {
      it("should allow first assignment as normal") {
        given ctx: Context = Context()
        Interpreter.execute("1 #!x₉")
        assertResult(VNum(1))(ctx.getVar("x"))
      }

      it("should not allow reassignment") {
        given ctx: Context = Context()
        Interpreter.execute("1 #!x₉")
        assertThrows[Exception] {
          Interpreter.execute("2 #!x")
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
            VFun.fromLambda(AST.Lambda(Some(1), Nil, List(AST.GetVar("x"))))
          )
        )
      }
      it("should not be able to set nonlocal variables inside lambdas") {
        given ctx: Context = Context()
        ctx.setVar("x", 5)
        ctx.push(1)
        Interpreter.executeFn(
          VFun.fromLambda(
            AST
              .Lambda(Some(1), Nil, List(AST.AugmentVar("x", AST.Command("+"))))
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
      "#[1|2|3|4|5#]ƛ#=x+|#$x}" -> VList(1, 2, 3, 4, 5),
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
        ),
      )
    }
  }

  describe("String compression") {
    describe("Dictionary compression") {
      it("should handle Hello World") {
        val compressed = StringHelpers.compressDictionary("Hello World")
        assertResult("Hello World")(
          StringHelpers.decompress(
            compressed.substring(1, compressed.length - 1)
          )
        )
      }
      it("should handle a really long string", Slow) {
        val temp =
          "We're no strangers to love. You know the rules and so do I. A full commitment's what I'm thinking of. You wouldn't get this from any other guy."
        val compressed = StringHelpers.compressDictionary(temp)
        assertResult(temp)(
          StringHelpers.decompress(
            compressed.substring(1, compressed.length - 1)
          )
        )
      }
    }
    describe("Base-252 compression") {
      it("should not remove leading 'a' characters") {
        given Context = VyxalTests.testContext()
        val compressed = StringHelpers.compress252("aabbcc sussybaka")
        assertResult("aabbcc sussybaka")(
          StringHelpers.decompress252String(
            compressed.substring(1, compressed.length - 1)
          )
        )
      }
    }
  }

  describe("Global Recursion (Factorial)") {
    testMulti(":1>[:vx×}")(
      VList(1) -> VNum(1),
      VList(2) -> VNum(2),
      VList(3) -> VNum(6),
      VList(4) -> VNum(24),
      VList(5) -> VNum(120),
      VList(6) -> VNum(720),
      VList(7) -> VNum(5040),
      VList(8) -> VNum(40320),
    )
  }

  describe("Global Recursion (Fibonacci)") {

    testMulti("#{:0=|_0|:1=|_1|:vx$2-x+")(
      VList(0) -> VNum(0),
      VList(1) -> VNum(1),
      VList(2) -> VNum(1),
      VList(3) -> VNum(2),
      VList(4) -> VNum(3),
      VList(5) -> VNum(5),
      VList(6) -> VNum(8),
      VList(7) -> VNum(13),
      VList(8) -> VNum(21),
    )
  }

  describe("Vectorised recursion") {
    testCode("#[#[1|2|3#]|#[4|5|6#]#]λᶲ\"[\"c[ᵛx∑}}Ė", VNum(21), Seq())
  }

  describe("Register chicanery") {
    testMulti(
      "5£9::++" -> VNum(27),
      "5£9::++¥" -> VNum(5),
    )
  }

  describe("Stack Rotating Chicanery") {
    testStackLike("←")(
      List[VAny](1, 2, 3, 9) -> List[VAny](1, 9, 3, 2),
      List[VAny](1, 2, 3, 4, 6) -> List[VAny](1, 6, 4, 3, 2),
      List[VAny](1) -> List[VAny](1),
    )

    testStackLike("→")(
      List[VAny](1, 2, 3, 4) -> List[VAny](3, 2, 1, 4),
      List[VAny](1, 2, 3, 4, 5) -> List[VAny](4, 3, 2, 1, 5),
      List[VAny](8) -> List[VAny](8),
    )
  }

  describe("Dumping things") {
    testStackLike("\\")(
      List[VAny](VList(3, 4, 5)) -> List[VAny](5, 4, 3),
      List[VAny](VList()) -> List[VAny](),
      List[VAny](VList(1)) -> List[VAny](1),
    )
  }

  describe("The define structure") {
    it("does stuff") {
      group {
        testCode(
          "#:: @incrementAndHalf | x | #$x 1+ 2÷} 5 #:@incrementAndHalf",
          VNum(3),
          Seq(),
        )

        testCode("#:: @+ | lhs, rhs | #$lhs #$rhs -} 4 6 +", VNum(2), Seq())
        testCode("#:: @+ | lhs, rhs | #$lhs #$rhs -} 1 1 +", VNum(0), Seq())

        testCode(
          "#:: *ReduceRange | f | 1 | ɾ #$f R } 5 #:`ReduceRange +",
          VNum(15),
          Seq(),
        )

        testCode(
          "#:: *RevRow | f | arr | #$arr V #$f M V } 12ʀ4Ẇ #:`RevRow 1İ",
          VList(VList(0, 1, 2), VList(4, 5, 6), VList(8, 9, 10)),
          Seq(),
        )

        testCode(
          "#:: *p | f, g | ! | #$f Ḃ #=temp #$g Ė #$temp } 4 5 p+- ;",
          VList(9, -1),
          Seq(),
        )

        testCode(
          "#:: @+ | lhs, rhs | #[#$lhs|#$rhs#] #[2|2#] ₌ [5|#$lhs #$rhs #:~+}} 2 2 +",
          VNum(5),
          Seq(),
        )

        testCode(
          "#:: @+ | lhs, rhs | #[#$lhs|#$rhs#] #[2|2#] ₌ [5|#$lhs #$rhs #:~+}} 6 9 +",
          VNum(15),
          Seq(),
        )
      }
    }
  }

  describe("Objects") {
    it("should have correct read access modifiers") {
      val boilerplate =
        "object TestObj => 1 :!=public 2 :=private 3 $restricted end"
      testCodeAsLiterate(s"""$boilerplate `TestObj` "public" @<=""", VNum(1))
      testCodeAsLiterate(
        s"""$boilerplate `TestObj` "restricted" @<=""",
        VNum(3),
      )
      try
        testCodeAsLiterate(s"""$boilerplate `TestObj` "private" @<=""", VNum(2))
        fail("Should have thrown an exception on read private")
      catch case _: Exception => ()
    }

    it("should have the correct write access modifiers") {
      val boilerplate =
        "object TestObj => 1 :!=public 2 :=private 3 $restricted end"
      testCodeAsLiterate(
        s"""$boilerplate `TestObj` "public" 69 @=> "public" @<=""",
        VNum(69),
      )
      try
        testCodeAsLiterate(
          s"""$boilerplate `TestObj` "private" 69 @=>""",
          VNum(2),
        )
        fail("Should have thrown an exception on write private")
        testCodeAsLiterate(
          s"""$boilerplate `TestObj` "restricted" 69 @=>""",
          VNum(3),
        )
        fail("Should have thrown an exception on write restricted")
      catch case _: Exception => ()
    }

    it("should update object attributes upon writing") {
      testCodeAsLiterate(
        """
          |object TestObj => 1 :!=public 2 :=private 3 $restricted end
          |`TestObj` "public" 69 @=> `TestObj` "public" @<=""",
        VNum(69),
      )
    }
  }

  describe("Extension Methods") {
    it("Should error when only < 3 branches") {
      assertThrows[Exception] {
        testCodeAsLiterate("extension Fail end", VNum(1))
      }
      assertThrows[Exception] {
        testCodeAsLiterate("extension Fail => 1 end", VNum(1))
      }
    }
    it("Should allow an extension with a single item") {
      testCodeAsLiterate(
        "extension inc given a as num does $a 1 $.+ end 5 $@inc",
        VNum(6),
      )
      testCodeAsLiterate(
        "extension + given a as num does $a 1 $.+ end 5 +",
        VNum(6),
      )
      testCodeAsLiterate(
        "extension + given a as num does $a 1 $.+ end [1,2,3] [4,5,6] +",
        VList(VNum(5), VNum(7), VNum(9)),
      )
      testCodeAsLiterate(
        "extension Test given a as * does $a $a === end 5 $@Test",
        VNum(1),
      )
      testCodeAsLiterate(
        "object T => 5 $mem end extension F given a as T does $a \"mem\" @<= end `T` $@F",
        VNum(5),
      )
    }
  }

end InterpreterTests
