package vyxal

import vyxal.*

import org.scalatest.funspec.AnyFunSpec

class LiterateTests extends VyxalTests:
  def testLiteral(input: String, expected: String) =
    assertResult(litLex(input))(expected)
  describe("Literals") {
    it("should leave numbers as-is") {
      group {
        testLiteral("123", "123")
        testLiteral("6.", "6.")
        testLiteral("3.4i1.2", "3.4ı1.2")
        testLiteral("3.4i1.", "3.4ı1.")
        testLiteral("3.4i.2", "3.4ı.2")
        testLiteral("3.4i.", "3.4ı.")
        testLiteral(".i.", ".ı.")
        testLiteral("3.4i", "3.4ı")
        testLiteral(".4", ".4")
        testLiteral(".", ".")
      }
    }

    it("should leave strings as-is") {
      testLiteral(""" "Hello, Vyxal!" """, """ "Hello, Vyxal!" """)
      testLiteral(
        """ "Vyxal is what \"you\" want!" """,
        """ "Vyxal is what \"you\" want!" """
      )
    }
  }
  describe("Comments") {
    it("should ignore them") {
      testLiteral("1 2 3 ## This is a comment", "1 2 3 ")
      testLiteral("## Hello, World!", "")
    }
  }

  describe("Lambdas") {
    testLiteral("10 { context-n add } map", "10 λn +} M")
    testLiteral("{{{}}{}}", "λλλ}} λ}}")
    testLiteral("{}{}", "λ} λ}")
  }

  describe("Lists") {
    testLiteral("[1|2|3|4]", "#[1|2|3|4#]")
    testLiteral("[]", "#[#]")
    testLiteral("[[]|[]]", "#[#[#]|#[#]#]")
  }

  describe("Variable Get") {
    testLiteral("$a", "#$a")
    testLiteral("$", "#$")
  }

  describe("Variable Set") {
    testLiteral("10 :=x", "10 #=x")
    testLiteral(":=x", "#=x")
  }

  describe("Variable Augmentation") {
    testLiteral("10 +:=x", "10 +#>x")
    testLiteral("+:=x", "+#>x")
  }

  describe("Variable unpacking") {
    testLiteral("[1|2|3] :=[x|y|z]", "#[1|2|3#] #:[x|y|z]")
  }

  describe("Ungrouping") {
    testLiteral("1 (3 4 add) times", "1 3 4 + ×")
    testLiteral("(((((add)))))", "+")
  }
end LiterateTests
