import org.scalatest.funsuite.AnyFunSuite

import AST.*

class ParserTests extends AnyFunSuite:
  test("Does the parser recognise numbers?") {
    assert(VyxalParser.parse("123") === Right(List(Number("123"))))
  }

  test("Does the parser recognise strings?") {
    assert(
      VyxalParser.parse(""" "Hello, world!" """) === Right(
        List(Str("Hello, world!"))
      )
    )
  }

  test("Does the parser recognise multiple literals?") {
    assert(
      VyxalParser.parse("""123 "Hello" 24 """) === Right(
        List(Number("123"), Str("Hello"), Number("24"))
      )
    )
  }

  test("Does the parser understand nested structures?") {
    assert(
      VyxalParser.parse("1 { 2 | { {3 | 4} | } } 6") ===
        Right(
          List(
            Number("1"),
            Structure(
              "{",
              List(
                List(Number("2")),
                List(
                  Structure(
                    "{",
                    List(
                      List(
                        Structure(
                          "{",
                          List(List(Number("3")), List(Number("4")))
                        )
                      ),
                      List()
                    )
                  )
                )
              )
            ),
            Number("6")
          )
        )
    )
  }

  test("Does the parser understand close all structures (']')?") {
    assert(
      VyxalParser.parse("1 { 2 | { {3 | 4} | ] 6") ===
        Right(
          List(
            Number("1"),
            Structure(
              "{",
              List(
                List(Number("2")),
                List(
                  Structure(
                    "{",
                    List(
                      List(
                        Structure(
                          "{",
                          List(List(Number("3")), List(Number("4")))
                        )
                      ),
                      List()
                    )
                  )
                )
              )
            ),
            Number("6")
          )
        )
    )
  }

  test("Does the parser understand mixed monadic modifiers and structures?") {
    assert(
      VyxalParser.parse("1 { 2 | { v{3 | 4 ] 5") ===
        Right(
          List(
            Number("1"),
            Structure(
              "{",
              List(
                List(Number("2")),
                List(
                  Structure(
                    "{",
                    List(
                      List(
                        MonadicModifier(
                          "v",
                          Structure(
                            "{",
                            List(List(Number("3")), List(Number("4")))
                          )
                        )
                      )
                    )
                  )
                )
              )
            ),
            Number("5")
          )
        )
    )
  }

end ParserTests
