package vyxal

import scala.language.strictEquality

import vyxal.parsing.Vexer

import org.scalatest.compatible.Assertion
import org.scalatest.funspec.AnyFunSpec

class VexerLiterateTests extends VyxalTests:
  def testLiterate(input: String, expected: String): Assertion =
    val literate = Vexer.lexLiterate(input)
    val sbcsified = Vexer.sbcsify(literate)
    assertResult(
      expected,
      literate.map(tok => s"${tok.tokenType}(${tok.value})"),
    )(sbcsified)

  describe("Literals") {
    it("should leave numbers as-is") {
      group {
        testLiterate("123", "123")
        testLiterate("6.", "6.")
        testLiterate("3.4i1.2", "3.4ı1.2")
        testLiterate("3.4i1.", "3.4ı1.")
        testLiterate("3.4i.2", "3.4ı.2")
        testLiterate("3.4i.", "3.4ı.")
        testLiterate(".i.", ".ı.")
        testLiterate("3.4i", "3.4ı")
        testLiterate(".4", ".4")
        testLiterate(".", ".")
        testLiterate("add.5", "+.5")
        testLiterate("1_000_000", "1000000")
        testLiterate("1_0______0", "100")
      }
    }

    it("should leave strings as-is") {
      testLiterate(""""Hello, Vyxal!"""", """"Hello, Vyxal!"""")
      testLiterate(
        """"Vyxal is what \"you\" want!"""",
        """"Vyxal is what "you" want!"""",
      )
    }
  }
  describe("Comments") {
    it("should ignore them") {
      testLiterate("1 2 3 ## This is a comment", "1 2 3")
      testLiterate("## Hello, World!", "")
    }
  }

  describe("Lambdas") {
    it("should transpile them correctly") {
      testLiterate("10 { context-n add } map", "10λn+}M")
      testLiterate("{{{}}{}}", "λλλ}}λ}}")
      testLiterate("{}{}", "λ}λ}")
    }

    it("should do arguments correctly") {
      testLiterate("lambda x, y -> $x $y add end", "λx,y|#$x#$y+}")
      testLiterate("lambda add -> $add", "λadd|#$add")
      testLiterate("lambda lambda add -> $add end end", "λλadd|#$add}}")
      testLiterate("lambda add lambda add ->", "λ+λadd|")
    }

    it("shouldn't mistake nested structures's branches as params") {
      testLiterate("{ even? ? 1 : 2 end }", "λe[1|2}}")
    }
  }

  describe("Lists") {
    it("should transpile them correctly") {
      testLiterate("[1|2|3|4]", "#[1|2|3|4#]")
      testLiterate("[]", "#[#]")
      testLiterate("[[]|[]]", "#[#[#]|#[#]#]")
    }
  }

  describe("Variable Get") {
    it("should transpile them correctly") {
      testLiterate("$a", "#$a")
      testLiterate("$", "#$")
    }
  }

  describe("Variable Set") {
    it("should transpile them correctly") {
      testLiterate("10 :=x", "10#=x")
      testLiterate(":=x", "#=x")
    }
  }

  describe("Constants") {
    it("should transpile them correctly") {
      testLiterate(":!=x", "#!x")
      testLiterate("10 :!=x", "10#!x")
    }
  }

  describe("Variable Augmentation") {
    it("should transpile them correctly") {
      testLiterate("10 +:>x", "10+#>x")
      testLiterate("+:>x", "+#>x")
    }
  }

  describe("Variable unpacking") {
    it("should transpile them correctly") {
      testLiterate("[1|2|3] :=[x|y|z]", "#[1|2|3#]#:[x|y|z]")
    }
  }

  describe("Ungrouping") {
    it("should remove the parentheses") {
      testLiterate("1 (3 4 add) times", "1 3 4+×")
      testLiterate("(((((add)))))", "+")
    }
  }

  describe("Ternaries") {
    it("should transpile them correctly") {
      testLiterate("1 ? 2 : 3 end", "1[2|3}")
      testLiterate("1 ?-> 2 : 3 end", "1[2|3}")
    }
  }

  describe("Existing elements") {
    they("should be overridden by literate keywords") {
      testLiterate("*", "×")
    }
  }

  describe("Misc") {
    it("should not treat words with i as complex") {
      testLiterate("is-vowel?", "A")
      testLiterate("is-vowel? i", "Aı")
      testLiterate("i is-vowel?", "ıA")
    }
  }

  describe("Right Moving") {
    it("should move tokens right once when given a single quote") {
      testLiterate("3 '+ 4", "3 4+")
      testLiterate("'print \"Hello, World!\"", "\"Hello, World!\",")
    }

    it("should leave tokens at the end alone") {
      testLiterate("3 4 '+", "3 4+")
      testLiterate("'print", ",")
    }

    it("should move more than once if there's more than one") {
      testLiterate("''+ 3 4", "3 4+")
      testLiterate("''''+ 3 4", "3 4+")
      testLiterate("1 2 '''3 4 5 6 7", "1 2 4 5 6 3 7")
    }

    it("should move multiple in order of left to right") {
      testLiterate("'3 '+ 4", "3+4")
      testLiterate("''''for ''''$i ''''-> 'range(1 100)", "1 100R(#$i|")
    }

    it("should move and count groups as a single unit") {
      testLiterate("'(4 +) 5", "5 4+")
      testLiterate("'print(69)", "69,")
    }

    it("should move tokens inside groups within the group") {
      testLiterate("(3 '+ 4)", "3 4+")
      testLiterate("(3 4 '+) 5 +", "3 4+5+")
    }

    it("should count lambdas as a single unit") {
      testLiterate("[1,2,3] 'filter {'% 2 '== 0}", "#[1|2|3#]λ2%0=}F")
    }
  }
end VexerLiterateTests
