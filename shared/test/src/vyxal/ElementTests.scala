package vyxal

import vyxal.VyxalTests.testContext

import org.scalatest.funspec.AnyFunSpec

/** Tests for specific elements */
class ElementTests extends VyxalTests:
  /** Helper to avoid doing List[VAny](...) */
  private def in(inputs: VAny*): Seq[VAny] = inputs

  describe("Element *") {
    describe("when given two numbers") {
      testMulti("*")(
        in(3, 2) -> 9,
        in(0, 1) -> 0,
        in(2, -1) -> 0.5,
        in(VNum("5.1"), VNum("4.2")) -> VNum("937.11899215207"),
        in(3, 0) -> 1,
        in(0, 0) -> 1,
      )
    }

    describe("when given a string and a number") {
      testMulti("*")(
        in("the fitnessgram pacer test", 6) -> "the finessgram pacer test",
        in(4.2, "airpod shotty") -> "airpd shotty",
        in("sussy baka", 0) -> "ussy baka",
        in("sussy baka", -1) -> "sussy bak",
        in("vyxal", 7) -> "vyal",
      )
    }

    describe("when given two strings") {
      testMulti("*")(
        in("abcdefabc", "abc") -> "def",
        in("abc", "abcdefabc") -> "abc",
        in("abc", "abc") -> "",
        in("abcdefabc", "") -> "abcdefabc",
      )
    }
  }

  describe("Element +") {
    describe("when given lists") {
      testMulti("+")(
        in(VList(VList(2, 5), "foo"), VList(VList(3, 4))) ->
          VList(
            VList(5, 9),
            "foo0",
          )
      )
    }
    describe("when given two non-list values") {
      testMulti("+")(
        in(2, 3) -> 5,
        in(0, 0) -> 0,
        in(VNum("5.1"), VNum("-45.4")) -> VNum("-40.3"),
        in("foo", "bar") -> "foobar",
        in("foo", 3) -> "foo3",
        in(3, "foo") -> "3foo",
        in(VNum("0.1"), VNum("0.2")) -> VNum("0.3"),
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
            List(AST.Number(8), AST.Command("-")),
          )
        )
        ctx.push(3, f, g)
        Interpreter.execute(AST.Command("+"))
        ctx.pop() match
          case fork: VFun => assertResult(VNum(1))(Interpreter.executeFn(fork))
          case res => fail(s"Expected a function, got $res")
      }
    }
  }

  describe("Element ;") {
    testMulti(";")(
      in(1, 2, 3) -> VList(2, 3),
      in(1, 2, 3, 4) -> VList(3, 4),
      in(1, 2, 3, 4, 5) -> VList(4, 5),
    )
  }

  describe("Element <") {
    describe("With numbers") {
      testMulti("<")(
        Seq[VAny](1, 2) -> 1,
        Seq[VAny](1, 1) -> 0,
        Seq[VAny](2, 1) -> 0,
        Seq[VAny](VNum.complex(1, 50), 2) -> 1,
      )
    }
    describe("Should stringify") {
      testMulti("<")(
        Seq[VAny]("abc", 1) -> 0,
        Seq[VAny](20, "3") -> 1,
        Seq[VAny]("ABC", "abc") -> 1,
      )
    }
    describe("should vectorise") {
      testMulti("<")(
        Seq[VAny](VList(1, VList(2, 4), -5), 3) -> VList(1, VList(1, 0), 1),
        Seq[VAny](VList(6, "foo"), VList(4, 20)) -> VList(0, 0),
      )
    }
  }

  describe("Element >") {
    describe("With numbers") {
      testMulti(">")(
        Seq[VAny](1, 2) -> 0,
        Seq[VAny](1, 1) -> 0,
        Seq[VAny](2, 1) -> 1,
        Seq[VAny](VNum.complex(1, 50), 2) -> 0,
      )
    }
    describe("Should stringify") {
      testMulti(">")(
        Seq[VAny]("abc", 1) -> 1,
        Seq[VAny](20, "3") -> 0,
        Seq[VAny]("ABC", "abc") -> 0,
      )
    }
    describe("should vectorise") {
      testMulti(">")(
        Seq[VAny](VList(1, VList(2, 4), -5), 3) -> VList(0, VList(0, 1), 0),
        Seq[VAny](VList(6, "foo"), VList(4, 20)) -> VList(1, 1),
      )
    }
  }

  describe("Element A") {
    describe("when given lists") {
      testMulti("A")(
        in(VList(1, 391, "dqw4w9wgxcq", VList(0))) -> 1,
        in(VList(0, 69420, VList())) -> 0,
      )
    }

    describe("when given a single-character string") {
      testMulti("A")(
        in("a") -> 1,
        in("E") -> 1,
        in("y") -> 0,
      )
    }

    describe("when given a multi-character string") {
      testMulti("A")(in("asdEy") -> VList(1, 0, 0, 1, 0))
    }
  }

  describe("Element C") {
    describe("when given lists") {
      testMulti("C")(
        in(3, VList(1, 3, 30, 2, 33, 4, 3, 3)) -> 3,
        in(VList(1, 30, 2, 33, 4), 3) -> 0,
        in(
          VList(1, 30, VList(VList("h"), VList("e"), VList("c")), 33, 4),
          VList(VList("h"), VList("e"), VList("c")),
        ) -> 1,
      )
    }

    describe("when given strings") {
      testMulti("C")(
        in("lolollol lol asd", "lol") -> 3,
        in("lolollol lol asd", "asdf") -> 0,
      )
    }

    describe("when given mixed types") {
      testMulti("C")(
        in(12, 1) -> 1,
        in("ab1111ab", 1) -> 4,
        in(12341234, 2) -> 2,
        in(23432423, "3") -> 3,
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
        in(1) -> 2,
        in(2) -> 4,
        in(3) -> 8,
        in(4) -> 16,
        in(VNum("6.9")) -> VNum("119.4282229167113492456119380671925973794854"),
        in(-234) -> 0,
      )
    }

    describe("when given a string") {
      testMulti("E")(
        in("1") -> 1,
        in("2") -> 2,
        in("3243") -> 3243,
        in("-234") -> -234,
        in("0") -> 0,
        in("[1, 2, 3, 4, 5, 6]") -> VList(1, 2, 3, 4, 5, 6),
        in("[]") -> VList(),
        in("\"lol\"") -> String("lol"),
      )
    }
  }

  describe("Element G") {
    testMulti("G")(
      in(VList(1, 2, 3, 4, 5, 6, 7)) -> 7,
      in(VList()) -> VList(),
      in(6, 9) -> 9,
      in(9, 6) -> 9,
      in(VList(1, 2, 3, 4, 5, 6, 7), 3) -> VList(3, 3, 3, 4, 5, 6, 7),
    )
    testCode(
      "#[1|1#]λ2|+}G10Θ",
      VList(1, 1, 2, 3, 5, 8, 13, 21, 34, 55),
    )
  }

  describe("Element H") {
    testMulti("H")(
      in(69420) -> "10F2C",
      in(0) -> "0",
      in(89) -> "59",
      in("10F2C") -> 69420,
      in("59") -> 89,
    )
  }

  describe("Element I") {
    testMulti("I")(
      in(VList(1, 2, 3), VList(4, 5, 6)) -> VList(1, 4, 2, 5, 3, 6),
      in(VList(1, 2, 3), VList(4, 5, 6, 7)) -> VList(1, 4, 2, 5, 3, 6, 7),
      in(VList(1, 2, 3), VList(4, 5)) -> VList(1, 4, 2, 5, 3),
      in("srn", "tig") -> String("string"),
      in("aaaa", "") -> String("aaaa"),
      in("aaa", VList(1)) -> VList("a", 1, "a", "a"),
      in(123, 456) -> VList(1, 4, 2, 5, 3, 6),
    )
  }

  describe("Element J") {
    testMulti("J")(
      in(VList(1, 2, 3), 4) -> VList(1, 2, 3, 4),
      in("abc", "def") -> String("abcdef"),
      in(1, VList(2, 3, 4)) -> VList(1, 2, 3, 4),
      in(VList(1, 2), VList(3, 4)) -> VList(1, 2, 3, 4),
      in(123, 456) -> 123456,
      in(123, "4567") -> String("1234567"),
      in("123", 4568) -> String("1234568"),
    )
  }

  describe("Element K") {
    testMulti("K")(
      in(20) -> VList(1, 2, 4, 5, 10, 20),
      in(100) -> VList(1, 2, 4, 5, 10, 20, 25, 50, 100),
      in(1) -> VList(1),
      in(0) -> VList(),
      in(-1) -> VList(-1),
      in("23423") -> 1,
      in("0") -> 1,
      in("ljlkerg23423") -> 0,
    )
  }

  describe("Element L") {
    testMulti("L")(
      in(-234) -> 4,
      in(0) -> 1,
      in(1) -> 1,
      in(6782342) -> 7,
      in(123456789) -> 9,
      in("w;ergn") -> 6,
      in("h") -> 1,
      in("") -> 0,
      in(VList(1, 2, 3, 4, 5, 6, 7)) -> 7,
      in(VList()) -> 0,
    )
  }

  describe("Element M") {
    describe("when given two lists") {
      testMulti("M")(
        in(
          VList(1, 2, VList(3, 4), 5),
          VList(1, 2, VList(VList(3, 4, 6), 5)),
        ) -> VList(1, 2, VList(VList(VList(3, 4), 5, 1), 2)),
        in(
          VList(1, 2, 3, 4, 5, 6, 7),
          VList(VList(8, 9), 10, 11, 12, VList(13, 14)),
        ) -> VList(VList(1, 2), 3, 4, 5, VList(6, 7)),
        in(VList(1, 2, 3), VList(VList(4), VList(), VList(6))) ->
          VList(
            VList(1),
            VList(),
            VList(2),
          ),
        in(VList(1, 2, 3), VList(4, 5, 6, 7, 8, 9, 10)) ->
          VList(1, 2, 3, 1, 2, 3, 1),
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
        in(45, 3) -> 2,
        in(1.125, 2) -> 0,
        in(1.125, 3) -> 0,
        in(0, 2) -> 0,
        in(-3, 1) -> 3,
      )
    }
  }

  describe("Element N") {
    describe("when given a number") {
      testMulti("N")(
        in(420) -> -420,
        in(0) -> 0,
        in(-69) -> 69,
      )
    }
    describe("when given a string") {
      testMulti("N")(
        in("a") -> "A",
        in("A") -> "a",
        in("abc") -> "ABC",
        in("ABC") -> "abc",
        in("123") -> "123",
        in("abC123") -> "ABc123",
      )
    }

    describe("when given a function") {
      testMulti(
        "λ×16=}N" -> 4,
        "λ7×35=}N" -> 5,
      )
    }
  }

  describe("Element O") {
    describe("when given a number") {
      testMulti("O")(
        in(65) -> "A",
        in(97) -> "a",
        in(8482) -> "™",
        in(VList(97, 98, 99)) -> "abc",
        in(VList(49, 50, 51)) -> "123",
      )
    }
    describe("when given a string") {
      testMulti("O")(
        in("A") -> 65,
        in("a") -> 97,
        in("™") -> 8482,
        in("abc") -> VList(97, 98, 99),
        in("123") -> VList(49, 50, 51),
      )
    }

    describe("when given a list of mixed types") {
      testMulti("O")(
        in(VList(49, "a", 50, "b", 51, "c")) ->
          VList(
            "1",
            97,
            "2",
            98,
            "3",
            99,
          ),
        in(VList("1", 97, "2", 98, "3", 99)) ->
          VList(
            49,
            "a",
            50,
            "b",
            51,
            "c",
          ),
      )
    }
  }

  describe("Element P") {
    describe("when given a number") {
      testMulti("P")(
        in(4824) -> VList(4, 48, 482, 4824),
        in(-342) -> VList(3, 34, 342),
      )
    }

    describe("when given a string") {
      testMulti("P")(
        in("Hello") -> VList("H", "He", "Hel", "Hell", "Hello"),
        in("abc") -> VList("a", "ab", "abc"),
        in("123") -> VList("1", "12", "123"),
        in("") -> VList(),
      )
    }

    describe("when given a list") {
      testMulti("P")(
        in(VList(1, 2, 3)) ->
          VList(
            VList(1),
            VList(1, 2),
            VList(1, 2, 3),
          ),
        in(VList(1, 2, 3, 4, 5)) ->
          VList(
            VList(1),
            VList(1, 2),
            VList(1, 2, 3),
            VList(1, 2, 3, 4),
            VList(1, 2, 3, 4, 5),
          ),
        in(VList(1)) -> VList(VList(1)),
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
        in(1, 5) -> VList(1, 2, 3, 4),
        in(5, 1) -> VList(5, 4, 3, 2),
        in(1, 1) -> VList(),
        in(1, 0) -> VList(1),
        in(0, 1) -> VList(0),
        in(-5, 5) -> VList(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4),
      )
    }

    describe("when given two strings") {
      testMulti("R")(
        in("56.234", "\\d+\\.\\d+") -> 1,
        in("Hello, World", ".+") -> 1,
        in("Hello, World", "Hello, world") -> 0,
        in("https://www.google.com", "https?://.+") -> 1,
      )
    }
  }

  describe("Element S") {
    testMulti("S")(
      in("qzqadbA;z") -> ";Aabdqqzz",
      in(891738) -> VList(1, 3, 7, 8, 8, 9),
      in(VList(8, 9, "acb", "abc", 1, 9, 2, VList(1, 2))) ->
        VList(
          1,
          2,
          8,
          9,
          9,
          VList(1, 2),
          "abc",
          "acb",
        ),
    )
  }

  describe("Element T") {
    testMulti("T")(
      // triple overload
      in(5) -> 15,
      in(0) -> 0,
      in(-5) -> -15,
      // string overload
      in("hello") -> 1,
      in("") -> 1,
      in("Hello, World!") -> 0,

      // list overload
      in(VList(VList(1, 2, 3), VList(4, 5, 6))) ->
        VList(
          VList(1, 4),
          VList(2, 5),
          VList(3, 6),
        ),
      in(
        VList(VList(1, 2, 3), VList(4, 5, 6), VList(7, 8, 9))
      ) -> VList(VList(1, 4, 7), VList(2, 5, 8), VList(3, 6, 9)),
      in(VList(VList(1, 2, 3))) -> VList(VList(1), VList(2), VList(3)),
      in(VList(VList(1, 2, 3), VList(4, 5))) ->
        VList(
          VList(1, 4),
          VList(2, 5),
          VList(3),
        ),
    )
  }

  describe("Element Z") {
    describe("when given a function") {
      testMulti(
        "#[1|2|3|4|5#]⸠5+Z" ->
          VList(
            VList(1, 6),
            VList(2, 7),
            VList(3, 8),
            VList(4, 9),
            VList(5, 10),
          )
      )
    }
  }

  describe("Element b") {
    describe("when given a number") {
      testMulti("b")(
        in(5) -> VList(1, 0, 1),
        in(0) -> VList(0),
        in(-10) -> VList(-1, 0, -1, 0),
      )
    }

    describe("when given a string") {
      testMulti("b")(
        in("VTI") ->
          VList(
            VList(1, 0, 1, 0, 1, 1, 0),
            VList(1, 0, 1, 0, 1, 0, 0),
            VList(1, 0, 0, 1, 0, 0, 1),
          ),
        in(" ") -> VList(VList(1, 0, 0, 0, 0, 0)),
      )
    }

    describe("when given a list") {
      testMulti("b")(
        in(VList(2, 3)) -> VList(VList(1, 0), VList(1, 1))
      )
    }
  }

  describe("Element g") {
    testCode(
      "#[1|1#]λ+}g10Θ",
      VList(1, 1, 2, 3, 5, 8, 13, 21, 34, 55),
    )
  }

  describe("Element q") {
    testMulti("q")(
      in("\\") -> "\"\\\\\"",
      in("\"") -> "\"\\\"\"",
      in("a") -> "\"a\"",
    )
  }

  describe("Element x") {
    testCode("5 λ0=[1|1-x×}}Ė", 120)
    testCode("0 λ0=[1|1-x×}}Ė", 1)
  }

  describe("Element Ċ") {
    it("should work on lists") {
      testCode("#[1|2|3#] Ċ 10 Θ", VList(1, 2, 3, 1, 2, 3, 1, 2, 3, 1))
    }
  }

  describe("Element Ḋ") {
    it("simple test") {
      testCode(
        "#[1|2|3|4|5|6|7|8|9|10|1|4|5|1|3|6|4#] λ5%} Ḋ",
        VList(1, 2, 3, 4, 5),
      )
    }
  }

  describe("Element Ė") {
    describe("when given a number") {
      testMulti("Ė")(
        in(0) -> 1,
        in(1) -> 10,
        in(2) -> 100,
        in(-3) -> VNum(1) / 1000,
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
        Interpreter.execute("Ė")
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

  describe("Element Ŀ") {
    it(
      "Generates a list of all numbers in the collatz conjecture minus the first number"
    ) {
      testCode("10 λe[2÷|3×1+}} Ŀ", VList(5, 16, 8, 4, 2, 1))
    }
  }

  describe("Element Ṅ") {
    testMulti(
      "λ5-0=}Ṅ" -> 5,
      "λ1+} Ṅ" -> 1,
      "λ×16=}Ṅ" -> 4,
      "λ7×35=}Ṅ" -> 5,
    )
  }

  describe("Element Ẋ") {
    given Context = testContext()
    it("should handle two finite lists properly") {
      assertResult(
        VList(
          VList(1, "A"),
          VList(1, "B"),
          VList(2, "A"),
          VList(2, "B"),
          VList(3, "A"),
          VList(3, "B"),
        )
      )(
        ListHelpers.cartesianProduct(
          VList(1, 2, 3),
          VList("A", "B"),
        )
      )
    }
    it("should handle two infinite lists properly") {
      assertResult(
        VList(
          VList(1, "A"),
          VList(1, "B"),
          VList(2, "A"),
          VList(1, "C"),
          VList(2, "B"),
          VList(3, "A"),
        )
      )(
        ListHelpers
          .cartesianProduct(
            VList.from(LazyList.iterate(VNum(1))(_ + 1)),
            VList.from(LazyList.from('A'.toInt).map(_.toChar.toString)),
          )
          .take(6)
      )
    }
  }

  describe("Element Ẇ") {
    testMulti(
      "λ5%3=}5Ẇ" -> VList(3, 8, 13, 18, 23)
    )
  }

  describe("Element ȧ") {
    testMulti(
      "#[1|2|3|4|5|6#] λ+} ȧ" -> VList(3, 5, 7, 9, 11),
      "#[1|2|3|4|5|6#] λ++} ȧ" -> VList(4, 7, 10, 13, 16),
    )
  }

  describe("Element ṅ") {
    testMulti(
      "#[1|2|3|4|5|6#] ƛ0neṅ}" -> VList(0, 2, 0, 4, 0, 6)
    )
  }

end ElementTests
