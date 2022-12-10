package vyxal

import org.scalatest.funsuite.AnyFunSuite

import AST.*

class ParserTests extends AnyFunSuite {
  test("Does the parser recognise numbers?") {
    assert(Parser.parse("123") === Right(Number(123)))
  }

  test("Does the parser recognise strings?") {
    assert(
      Parser.parse(""" "Hello, world!" """) === Right(
        Str("Hello, world!")
      )
    )
  }

  test("Does the parser recognise basic expressions?") {
    assert(
      Parser.parse("1 1 +") === Right(
        Group(List(Number(1), Number(1), Command("+")), None)
      )
    )

    assert(
      Parser.parse("1 1 + 2 *") === Right(
        Group(
          List(Number(1), Number(1), Command("+"), Number(2), Command("*")),
          Some(0)
        )
      )
    )

  }

  test("Does the parser recognise lists?") {
    assert(
      Parser.parse(""" ⟨"foo" | 1 2 | 3 #] """) === Right(
        Lst(
          List(
            Str("foo"),
            AST.makeSingle(Number(1), Number(2)),
            Number(3)
          )
        )
      )
    )
  }

  test("Does the parser recognise multiple literals?") {
    assert(
      Parser.parse("""123 "Hello" 24 #[ | 1 2 | 3 ⟩""") === Right(
        AST.makeSingle(
          Number(123),
          Str("Hello"),
          Number(24),
          Lst(
            List(
              AST.makeSingle(),
              AST.makeSingle(Number(1), Number(2)),
              Number(3)
            )
          )
        )
      )
    )
  }

  test("Does the parser understand nested structures?") {
    assert(
      Parser.parse("1 { 2 | { {3 | 4} | } } 6") ===
        Right(
          AST.makeSingle(
            Number(6),
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
            )
          )
        )
    )
  }

  test("Does the parser understand close all structures (']')?") {
    assert(
      Parser.parse("1 { 2 | { {3 | 4} | ] 6") ===
        Right(
          AST.makeSingle(
            Number(6),
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
            )
          )
        )
    )
  }

}
