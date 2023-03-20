package vyxal.impls

import vyxal.*

import org.scalatest.funspec.AnyFunSpec
import Elements.Impls

/** Tests for specific elements */
class ElementTests extends VyxalTests:

  describe("Element !") {
    testMulti("!")(
      List[VAny](0) -> 1,
      List[VAny](10) -> 3628800,
      List[VAny](-5) -> 120,
      List[VAny](VNum("5.1")) -> VNum("142.4519440656788"),
      List[VAny](VNum("1.654")) -> VNum("1.4898045048177275")
    )
  }

  describe("Element $") {
    testStackLike("$")(
      List[VAny](1, 2, 3, 4, 5) -> List[VAny](5, 4),
      List[VAny](4) -> List[VAny](4, 4)
    )
  }

  describe("Element %") {
    describe("When given two numbers") {
      testMulti("%")(
        List[VAny](5, 2) -> 1,
        List[VAny](5, 3) -> 2,
        List[VAny](0, 1) -> 0,
        List[VAny](1, 0) -> 0,
        List[VAny](VNum("6.9"), VNum("4.2")) -> VNum("2.7"),
        List[VAny](420, -69) -> -63,
        List[VAny](420, -69.69) -> VNum("-67.83")
      )
    }
    describe("When given a string and any value") {
      testMulti("%")(
        List[VAny]("Hello, %", "World") -> "Hello, World",
        List[VAny]("Hello, %", 69) -> "Hello, 69",
        List[VAny]("Hello, %", VList(VList(1, 2, 3))) -> "Hello, [ 1 | 2 | 3 ]",
        List[VAny]("Hello, %", VNum("69.69")) -> "Hello, 69.69",
        List[VAny]("% + % = %", VList(1, 2, 3)) -> "1 + 2 = 3"
      )
    }
  }

  describe("Element &") {
    testMulti("&")(
      List[VAny](VList(1, 2, 3), VList(4, 5)) -> VList(1, 2, 3, VList(4, 5)),
      List[VAny](VList(1, 2, 3), 69) -> VList(1, 2, 3, 69),
      List[VAny]("abc", VList()) -> VList("a", "b", "c", VList())
    )
  }

  describe("Element *") {
    describe("when given two numbers") {
      testMulti("*")(
        List[VAny](3, 2) -> 9,
        List[VAny](0, 1) -> 0,
        List[VAny](2, -1) -> VNum(0.5),
        List[VAny](VNum("5.1"), VNum("4.2")) -> VNum("937.11899215207"),
        List[VAny](3, 0) -> 1,
        List[VAny](0, 0) -> 1
      )
    }

    describe("when given a string and a number") {
      testMulti("*")(
        List[VAny](
          "the fitnessgram pacer test",
          6
        ) -> "the finessgram pacer test",
        List[VAny](4.2, "airpod shotty") -> "airpd shotty",
        List[VAny]("sussy baka", 0) -> "ussy baka",
        List[VAny]("sussy baka", -1) -> "sussy bak",
        List[VAny]("vyxal", 7) -> "vyal"
      )
    }

    describe("when given two strings") {
      testMulti("*")(
        List[VAny]("abcdefabc", "abc") -> "def",
        List[VAny]("abc", "abcdefabc") -> "abc",
        List[VAny]("abc", "abc") -> "",
        List[VAny]("abcdefabc", "") -> "abcdefabc"
      )
    }
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
        List[VAny](3, "foo") -> "3foo",
        List[VAny](VNum("0.1"), VNum("0.2")) -> VNum("0.3"),
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
            List(AST.Number(8), AST.Command("-"))
          )
        )
        ctx.push(3, f, g)
        Interpreter.execute(AST.Command("+"))
        Impls.exec()
        assertResult(VNum(1))(ctx.pop())
      }
    }
  }

  describe("Element -") {
    describe("when given two numbers") {
      testMulti("-")(
        List[VAny](420, 69) -> 351,
        List[VAny](420, -69) -> 489,
        List[VAny](VNum("6.9"), VNum("4.2")) -> VNum("2.7")
      )
    }
    describe("when given a string and a number") {
      testMulti("-")(
        List[VAny]("Hello, World", 5) -> "Hello, World-----",
        List[VAny]("Hello, World", -5) -> "-----Hello, World",
        List[VAny]("Hello, World", 0) -> "Hello, World",
        List[VAny](5, "Hello, World") -> "-----Hello, World",
        List[VAny]("Hello, World", 5.2) -> "Hello, World-----"
      )
    }
    describe("when given two strings") {
      testMulti("-")(
        List[VAny]("Hello, World", "Hello, ") -> "World",
        List[VAny]("Hello, World", "World") -> "Hello, ",
        List[VAny]("Hello, World", "Hello, World") -> "",
        List[VAny]("Hello, World", "Hello, World!") -> "Hello, World",
        List[VAny]("abcbde", "b") -> "acde",
        List[VAny]("aaa", "a") -> "",
        List[VAny]("Hello, World", "") -> "Hello, World"
      )
    }
  }

  describe("Element :") {
    testStackLike(":")(
      List[VAny](1, 2, 3) -> List[VAny](1, 2, 3, 3),
      List[VAny](1, 2, 3, 4) -> List[VAny](1, 2, 3, 4, 4),
      List[VAny](1, 2, 3, 4, 5) -> List[VAny](1, 2, 3, 4, 5, 5)
    )
  }

  describe("Element ;") {
    testMulti(";")(
      List[VAny](1, 2, 3) -> VList(2, 3),
      List[VAny](1, 2, 3, 4) -> VList(3, 4),
      List[VAny](1, 2, 3, 4, 5) -> VList(4, 5)
    )
  }

  describe("Element <") {
    describe("With numbers") {
      testMulti("<")(
        Seq[VAny](1, 2) -> VNum(1),
        Seq[VAny](1, 1) -> VNum(0),
        Seq[VAny](2, 1) -> VNum(0),
        Seq[VAny](VNum.complex(1, 50), 2) -> VNum(1)
      )
    }
    describe("Should stringify") {
      testMulti("<")(
        Seq[VAny]("abc", 1) -> VNum(0),
        Seq[VAny](20, "3") -> VNum(1),
        Seq[VAny]("ABC", "abc") -> VNum(1)
      )
    }
    describe("should vectorise") {
      testMulti("<")(
        Seq[VAny](VList(1, VList(2, 4), -5), 3) -> VList(1, VList(1, 0), 1),
        Seq[VAny](VList(6, "foo"), VList(4, 20)) -> VList(0, 0)
      )
    }
  }

  describe("Element >") {
    describe("With numbers") {
      testMulti(">")(
        Seq[VAny](1, 2) -> VNum(0),
        Seq[VAny](1, 1) -> VNum(0),
        Seq[VAny](2, 1) -> VNum(1),
        Seq[VAny](VNum.complex(1, 50), 2) -> VNum(0)
      )
    }
    describe("Should stringify") {
      testMulti(">")(
        Seq[VAny]("abc", 1) -> VNum(1),
        Seq[VAny](20, "3") -> VNum(0),
        Seq[VAny]("ABC", "abc") -> VNum(0)
      )
    }
    describe("should vectorise") {
      testMulti(">")(
        Seq[VAny](VList(1, VList(2, 4), -5), 3) -> VList(0, VList(0, 1), 0),
        Seq[VAny](VList(6, "foo"), VList(4, 20)) -> VList(1, 1)
      )
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
      testMulti("B")(
        List[VAny](110) -> VNum(6),
        List[VAny](1000) -> VNum(8),
        List[VAny](69) -> VNum(21),
        List[VAny](69420) -> VNum(188),
        List[VAny](-7654) -> VNum(-94),
        List[VAny](-111) -> VNum(-7)
      )
    }
    describe("when given a string") {
      testMulti("B")(
        List[VAny]("110") -> VNum(6),
        List[VAny]("1000") -> VNum(8),
        List[VAny]("69") -> VNum(21),
        List[VAny]("69420") -> VNum(188),
        List[VAny]("-7654") -> VNum(-94),
        List[VAny]("-111") -> VNum(-7)
      )
    }
    describe("when given a list") {
      testMulti("B")(
        List[VAny](VList(1, 1, 0)) -> VNum(6),
        List[VAny](VList(1, 0, 0, 0)) -> VNum(8),
        List[VAny](VList(6, 9)) -> VNum(21),
        List[VAny](VList(6, 9, 4, 2, 0)) -> VNum(188),
        List[VAny](VList(-7, -6, -5, -4)) -> VNum(-94),
        List[VAny](VList(-1, -1, -1)) -> VNum(-7)
      )
    }
    describe("With lists of strings and lists") {
      it("Shouldn't do string multiplication") {
        testCode("B", 5, inputs = List(VList("1", "0", VList("0", "1"))))
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

  describe("Element D") {
    describe("when given anything") {
      it("should leave three copies of it on the stack") {
        given ctx: Context = Context(testMode = true)
        ctx.push(1, 2, 3)
        Interpreter.execute(AST.Command("D"))
        assertResult(VList(3, 3, 3))(VList(ctx.pop(), ctx.pop(), ctx.pop()))
      }
    }
  }

  describe("Element E") {
    describe("when given a number") {
      testMulti("E")(
        List[VAny](1) -> VNum(2),
        List[VAny](2) -> VNum(4),
        List[VAny](3) -> VNum(8),
        List[VAny](4) -> VNum(16),
        List[VAny](VNum("6.9")) -> VNum(
          "119.4282229167113492456119380671925973794854"
        ),
        List[VAny](VNum(-234)) -> VNum(0)
      )
    }

    describe("when given a string") {
      testMulti("E")(
        List[VAny]("1") -> VNum(1),
        List[VAny]("2") -> VNum(2),
        List[VAny]("3243") -> VNum(3243),
        List[VAny]("-234") -> VNum(-234),
        List[VAny]("0") -> VNum(0),
        List[VAny]("[1, 2, 3, 4, 5, 6]") -> VList(1, 2, 3, 4, 5, 6),
        List[VAny]("[]") -> VList(),
        List[VAny]("\"lol\"") -> String("lol")
      )
    }
  }

  describe("Element H") {
    testMulti("H")(
      List[VAny](VNum(69420)) -> "10F2C",
      List[VAny](VNum(0)) -> "0",
      List[VAny](VNum(89)) -> "59",
      List[VAny]("10F2C") -> VNum(69420),
      List[VAny]("59") -> VNum(89)
    )
  }

  describe("Element I") {
    testMulti("I")(
      List[VAny](VList(1, 2, 3), VList(4, 5, 6)) -> VList(1, 4, 2, 5, 3, 6),
      List[VAny](VList(1, 2, 3), VList(4, 5, 6, 7)) -> VList(1, 4, 2, 5, 3, 6,
        7),
      List[VAny](VList(1, 2, 3), VList(4, 5)) -> VList(1, 4, 2, 5, 3),
      List[VAny]("srn", "tig") -> String("string"),
      List[VAny]("aaaa", "") -> String("aaaa"),
      List[VAny]("aaa", VList(1)) -> VList("a", 1, "a", "a"),
      List[VAny](123, 456) -> VList(1, 4, 2, 5, 3, 6)
    )
  }

  describe("Element J") {
    testMulti("J")(
      List[VAny](VList(1, 2, 3), 4) -> VList(1, 2, 3, 4),
      List[VAny]("abc", "def") -> String("abcdef"),
      List[VAny](1, VList(2, 3, 4)) -> VList(1, 2, 3, 4),
      List[VAny](VList(1, 2), VList(3, 4)) -> VList(1, 2, 3, 4),
      List[VAny](123, 456) -> VNum(123456),
      List[VAny](123, "4567") -> String("1234567"),
      List[VAny]("123", 4568) -> String("1234568")
    )
  }

  describe("Element K") {
    testMulti("K")(
      List[VAny](20) -> VList(1, 2, 4, 5, 10, 20),
      List[VAny](100) -> VList(1, 2, 4, 5, 10, 20, 25, 50, 100),
      List[VAny](1) -> VList(1),
      List[VAny](0) -> VList(),
      List[VAny](-1) -> VList(-1),
      List[VAny]("23423") -> VNum(1),
      List[VAny]("0") -> VNum(1),
      List[VAny]("ljlkerg23423") -> VNum(0)
    )
  }

  describe("Element M") {
    describe("when given two lists") {
      testMulti("M")(
        List[VAny](
          VList(1, 2, VList(3, 4), 5),
          VList(1, 2, VList(VList(3, 4, 6), 5))
        ) -> VList(1, 2, VList(VList(VList(3, 4), 5, 1), 2)),
        List[VAny](
          VList(1, 2, 3, 4, 5, 6, 7),
          VList(VList(8, 9), 10, 11, 12, VList(13, 14))
        ) -> VList(VList(1, 2), 3, 4, 5, VList(6, 7)),
        List[VAny](VList(1, 2, 3), VList(VList(4), VList(), VList(6))) -> VList(
          VList(1),
          VList(),
          VList(2)
        ),
        List[VAny](VList(1, 2, 3), VList(4, 5, 6, 7, 8, 9, 10)) -> VList(1, 2,
          3, 1, 2, 3, 1)
      )
    }
    describe("when given a function and any value") {
      it("should map the function over the value") {
        testEquals(VList(2, 4, 6))(ctx ?=>
          ctx.push(VList(1, 2, 3))
          ctx.push(VFun(Elements.elements("+").impl, 2, List.empty, ctx))
          Interpreter.execute(AST.Command("M"))
          ctx.peek
        )
      }
      it("should work with strings") {
        testEquals(VList("aa", "bb", "cc"))(ctx ?=>
          ctx.push("abc")
          ctx.push(VFun(Elements.elements("+").impl, 2, List.empty, ctx))
          Interpreter.execute(AST.Command("M"))
          ctx.peek
        )
      }
    }
    describe("when given two numbers") {
      testMulti("M")(
        List[VAny](45, 3) -> 2,
        List[VAny](1.125, 2) -> 0,
        List[VAny](1.125, 3) -> 0,
        List[VAny](0, 2) -> 0,
        List[VAny](-3, 1) -> 3
      )
    }
  }

  describe("Element N") {
    describe("when given a number") {
      testMulti("N")(
        List[VAny](420) -> -420,
        List[VAny](0) -> 0,
        List[VAny](-69) -> 69
      )
    }
    describe("when given a string") {
      testMulti("N")(
        List[VAny]("a") -> "A",
        List[VAny]("A") -> "a",
        List[VAny]("abc") -> "ABC",
        List[VAny]("ABC") -> "abc",
        List[VAny]("123") -> "123",
        List[VAny]("abC123") -> "ABc123"
      )
    }

    describe("when given a function") {
      testMulti(
        "λ×16=}N" -> 4,
        "λ7×35=}N" -> 5
      )
    }
  }

  describe("Element O") {
    describe("when given a number") {
      testMulti("O")(
        List[VAny](65) -> "A",
        List[VAny](97) -> "a",
        List[VAny](8482) -> "™",
        List[VAny](VList(97, 98, 99)) -> "abc",
        List[VAny](VList(49, 50, 51)) -> "123"
      )
    }
    describe("when given a string") {
      testMulti("O")(
        List[VAny]("A") -> 65,
        List[VAny]("a") -> 97,
        List[VAny]("™") -> 8482,
        List[VAny]("abc") -> VList(97, 98, 99),
        List[VAny]("123") -> VList(49, 50, 51)
      )
    }

    describe("when given a list of mixed types") {
      testMulti("O")(
        List[VAny](VList(49, "a", 50, "b", 51, "c")) -> VList(
          "1",
          97,
          "2",
          98,
          "3",
          99
        ),
        List[VAny](VList("1", 97, "2", 98, "3", 99)) -> VList(
          49,
          "a",
          50,
          "b",
          51,
          "c"
        )
      )
    }
  }

  describe("Element R") {
    describe("when given function and iterable") {
      it("should work with singleton lists") {
        testEquals(1)(ctx ?=>
          ctx.push(VList(1))
          ctx.push(VFun(Elements.elements("+").impl, 2, List.empty, ctx))
          Interpreter.execute(AST.Command("R"))
          ctx.peek
        )
      }
      it("should calculate sum properly") {
        testEquals(15)(ctx ?=>
          ctx.push(VList(1, 2, 3, 4, 5))
          ctx.push(VFun(Elements.elements("+").impl, 2, List.empty, ctx))
          Interpreter.execute(AST.Command("R"))
          ctx.peek
        )
      }
    }

    describe("when given two numbers") {
      testMulti("R")(
        List[VAny](1, 5) -> VList(1, 2, 3, 4),
        List[VAny](5, 1) -> VList(5, 4, 3, 2),
        List[VAny](1, 1) -> VList(),
        List[VAny](1, 0) -> VList(1),
        List[VAny](0, 1) -> VList(0),
        List[VAny](-5, 5) -> VList(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4)
      )
    }

    describe("when given two strings") {
      testMulti("R")(
        List[VAny]("56.234", "\\d+\\.\\d+") -> 1,
        List[VAny]("Hello, World", ".+") -> 1,
        List[VAny]("Hello, World", "Hello, world") -> 0,
        List[VAny]("https://www.google.com", "https?://.+") -> 1
      )
    }
  }

  describe("Element _") {
    testStackLike("_")(
      List[VAny](1, 2, 3) -> List(1, 2),
      List[VAny](1, 2, 3, 4, 5) -> List(1, 2, 3, 4),
      List[VAny](1) -> List()
    )
  }

  describe("Element b") {
    describe("when given a number") {
      testMulti("b")(
        List[VAny](5) -> VList(1, 0, 1),
        List[VAny](0) -> VList(0),
        List[VAny](-10) -> VList(-1, 0, -1, 0)
      )
    }

    describe("when given a string") {
      testMulti("b")(
        List[VAny]("VTI") -> VList(
          VList(1, 0, 1, 0, 1, 1, 0),
          VList(1, 0, 1, 0, 1, 0, 0),
          VList(1, 0, 0, 1, 0, 0, 1)
        ),
        List[VAny](" ") -> VList(VList(1, 0, 0, 0, 0, 0))
      )
    }

    describe("when given a list") {
      testMulti("b")(
        List[VAny](VList(2, 3)) -> VList(VList(1, 0), VList(1, 1))
      )
    }
  }

  describe("Element Ė") {
    describe("when given a number") {
      testMulti("Ė")(
        List[VAny](0) -> 1,
        List[VAny](1) -> 10,
        List[VAny](2) -> 100,
        List[VAny](-3) -> VNum(1) / 1000
      )
    }

    describe("when given a string") {
      it("should properly execute code that uses the stack") {
        testCode(""" "1 2 + D" Ė """, 3)
      }

      it("should use the same context for executing the code") {
        // Doesn't use the test helpers because of context handling
        given ctx: Context = Context(inputs = List(3, 4), testMode = true)
        ctx.push("+")
        Impls.exec()
        assertResult(7: VNum)(ctx.peek)
      }
    }

    describe("when given a function") {
      it("should execute the function") {
        testEquals(3)(ctx ?=>
          ctx.push(1, 2)
          ctx.push(VFun.fromElement(Elements.elements("+")))
          Interpreter.execute(AST.Command("Ė"))
          ctx.peek
        )
      }
    }
  }

end ElementTests
