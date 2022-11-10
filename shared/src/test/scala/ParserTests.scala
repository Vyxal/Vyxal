package vyxal

import org.scalatest.funsuite.AnyFunSuite

import AST.*

class ParserTests extends AnyFunSuite:
  test("Does the parser recognise numbers?") {
    assert(VyxalParser.parse("123") === Right(Number(123)))
  }

  test("Does the parser recognise strings?") {
    assert(
      VyxalParser.parse(""" "Hello, world!" """) === Right(
        Str("Hello, world!")
      )
    )
  }

  test("Does the parser recognise multiple literals?") {
    assert(
      VyxalParser.parse("""123 "Hello" 24 """) === Right(
        AST.makeSingle(Number(123), Str("Hello"), Number(24))
      )
    )
  }

  test("Does the parser understand nested structures?") {
    assert(
      VyxalParser.parse("1 { 2 | { {3 | 4} | } } 6") ===
        Right(
          AST.makeSingle(
            Number(1),
            AST.While(
              Some(Number(2)),
              AST.While(
                Some(
                  AST.While(
                    Some(Number(3)),
                    Number(4)
                  )
                ),
                AST.makeSingle()
              )
            ),
            Number(6)
          )
        )
    )
  }

  test("Does the parser understand close all structures (']')?") {
    assert(
      VyxalParser.parse("1 { 2 | { {3 | 4} | ] 6") ===
        Right(
          AST.makeSingle(
            Number(1),
            AST.While(
              Some(Number(2)),
              AST.While(
                Some(
                  AST.While(
                    Some(Number(3)),
                    Number(4)
                  )
                ),
                AST.makeSingle()
              )
            ),
            Number(6)
          )
        )
    )
  }

  test("Does the parser understand mixed monadic modifiers and structures?") {
    assert(
      VyxalParser.parse("1 { 2 | { v{3 | 4 ] 5") ===
        Right(
          AST.makeSingle(
            Number(1),
            AST.While(
              Some(Number(2)),
              AST.While(
                None,
                MonadicModifier(
                  "v",
                  AST.While(
                    Some(Number(3)),
                    Number(4)
                  )
                )
              )
            ),
            Number(5)
          )
        )
    )
  }

end ParserTests
