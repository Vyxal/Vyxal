package vyxal

import vyxal.parsing.Lexer
import vyxal.AST.*

import org.scalatest.funsuite.AnyFunSuite

def parse(code: String) = Parser.parse(Lexer.lexSBCS(code))

class ParserTests extends AnyFunSuite:
  test("Can the parser parse an empty string?") {
    assert(parse("") === AST.makeSingle())
  }

  test("Does the parser recognise numbers?") {
    assert(parse("123") === Number(123))
  }

  test("Does the parser recognise strings?") {
    assert(
      parse(""" "Hello, world!" """) === Str("Hello, world!")
    )
  }

  test("Does the parser recognise basic expressions?") {
    assert(
      parse("1 1 +") === Group(List(Number(1), Number(1), Command("+")), None)
    )

    assert(
      parse("1 1 + 2 *") ===
        Group(
          List(
            Group(List(Number(1), Number(1), Command("+")), Some(0)),
            Number(2),
            Command("*"),
          ),
          None,
        )
    )

  }

  test("Does the parser recognise lists?") {
    assert(
      parse(""" ⟨"foo" | 1 2 | 3 #] """) ===
        Lst(
          List(
            Str("foo"),
            AST.makeSingle(Number(1), Number(2)),
            Number(3),
          )
        )
    )
  }

  test("Does the parser recognise multiple literals?") {
    assert(
      parse("""123 "Hello" 24 #[ | 1 2 | 3 ⟩""") ===
        AST.makeSingle(
          Number(123),
          Str("Hello"),
          Number(24),
          Lst(
            List(
              AST.makeSingle(),
              AST.makeSingle(Number(1), Number(2)),
              Number(3),
            )
          ),
        )
    )
  }

  test("Does the parser understand nested structures?") {
    assert(
      parse("1 { 2 | { {3 | 4} | } } 6") ===
        AST.makeSingle(
          Number(6),
          Number(1),
          AST.While(
            Some(Number(2)),
            AST.While(
              Some(
                AST.While(
                  Some(Number(3)),
                  Number(4),
                )
              ),
              AST.makeSingle(),
            ),
          ),
        )
    )
  }

  test("Does the parser understand basic structures?") {
    assert(
      parse("""[1 1 +|"nice" """) ===
        Ternary(
          Group(List(Number(1), Number(1), Command("+")), Some(0)),
          Some(Str("nice")),
        )
    )

    assert(
      parse("1 10R(i|n2*,") ===
        Group(
          List(
            Group(List(Number(1), Number(10), Command("R")), Some(0)),
            For(
              Some("i"),
              Group(
                List(
                  Group(List(Command("n"), Number(2), Command("*")), Some(0)),
                  Command(","),
                ),
                None,
              ),
            ),
          ),
          None,
        )
    )
  }

  test("Does the parser recognise single-character strings in structures?") {
    assert(
      parse("('|") === For(None, Str("|"))
    )
  }

  test("Does the parser recognise two-character strings in structures?") {
    assert(
      parse("(bᶴ|c") === For(None, Group(List(Command("b"), Str("|c")), None))
    )
  }

  test("Does the parser understand close all structures (']')?") {
    assert(
      parse("1 { 2 | { {3 | 4} | ] 6") ===
        AST.makeSingle(
          Number(6),
          Number(1),
          AST.While(
            Some(Number(2)),
            AST.While(
              Some(
                AST.While(
                  Some(Number(3)),
                  Number(4),
                )
              ),
              AST.makeSingle(),
            ),
          ),
        )
    )
  }

  test("Does the parser auto-close lists in structures?") {
    assert(
      parse("(2 #[} +") ===
        Group(
          List(
            For(None, Group(List(Number(2), Lst(List())), None)),
            Command("+"),
          ),
          None,
        )
    )
  }

  test(
    "Is the parser capable of parsing (something that looks like) FizzBuzz?"
  ) {
    assert(
      parse("""100 ƛ35O+"FizzBuzz"O++O""") ===
        Group(
          List(
            Number(100),
            Group(
              List(
                Lambda(
                  None,
                  List(),
                  List(
                    Group(
                      List(
                        Group(
                          List(
                            Group(List(Number(35), Command("O")), Some(0)),
                            Command("+"),
                          ),
                          Some(1),
                        ),
                        Group(
                          List(
                            Group(
                              List(Str("FizzBuzz"), Command("O")),
                              Some(0),
                            ),
                            Command("+"),
                          ),
                          Some(1),
                        ),
                        Command("+"),
                        Command("O"),
                      ),
                      None,
                    )
                  ),
                ),
                Command("M"),
              ),
              None,
            ),
          ),
          None,
        )
    )
  }

  test("Does the parser handle basic modifiers?") {
    assert(
      parse("ᵛ+ +") ===
        Group(
          List(
            Group(
              List(
                Lambda(Some(2), List(), List(Command("+")), false),
                Command("#v"),
              ),
              None,
            ),
            Command("+"),
          ),
          None,
        )
    )

    assert(
      parse("1 /+ 2") ===
        Group(
          List(
            Number(2),
            Number(1),
            Group(
              List(
                Lambda(Some(2), List(), List(Command("+")), false),
                Command("R"),
              ),
              None,
            ),
          ),
          None,
        )
    )
  }

  test("Does the parser identify for loop branches correctly?") {
    assert(
      parse("(hello|++}") ===
        For(Some("hello"), Group(List(Command("+"), Command("+")), None))
    )

    assert(
      parse("([1|2}}") === For(None, Ternary(Number(1), Some(Number(2))))
    )
  }

  test("Does the parser handle nested modifiers?") {
    assert(
      parse("#[#[1|2|3#]|#[4|5|6#]#] ᵛ/+") ===
        Group(
          List(
            Lst(
              List(
                Lst(List(Number(1), Number(2), Number(3))),
                Lst(List(Number(4), Number(5), Number(6))),
              )
            ),
            Group(
              List(
                Lambda(
                  Some(1),
                  List(),
                  List(
                    Group(
                      List(
                        Lambda(Some(2), List(), List(Command("+")), false),
                        Command("R"),
                      ),
                      None,
                    )
                  ),
                  false,
                ),
                Command("#v"),
              ),
              None,
            ),
          ),
          None,
        )
    )
    assert(
      parse("ϩϩ*O+OO") ===
        Group(
          List(
            Lambda(
              Some(1),
              List(),
              List(
                Group(
                  List(
                    Lambda(
                      Some(1),
                      List(),
                      List(Group(List(Command("*"), Command("O")), None)),
                    ),
                    Command("+"),
                  ),
                  None,
                )
              ),
            ),
            Command("O"),
            Command("O"),
          ),
          None,
        )
    )
  }

  test("Does the parser recognise lambda to newline?") {
    assert(
      parse("1 + 2 * ᵜ #[1|2|3#] M") ===
        Group(
          List(
            Lambda(
              Some(1),
              List(),
              List(
                Group(
                  List(
                    Group(List(Number(1), Command("+")), Some(1)),
                    Group(List(Number(2), Command("*")), Some(1)),
                  ),
                  None,
                )
              ),
            ),
            Group(
              List(Lst(List(Number(1), Number(2), Number(3))), Command("M")),
              Some(1),
            ),
          ),
          None,
        )
    )
    assert(
      parse("1 + 2 * ᵜ") ===
        Lambda(
          Some(1),
          List(),
          List(
            Group(
              List(
                Group(List(Number(1), Command("+")), Some(1)),
                Group(List(Number(2), Command("*")), Some(1)),
              ),
              None,
            )
          ),
        )
    )
    assert(
      parse("#[1|2|3#]\n1 + 2 * ᵜ M") ===
        Group(
          List(
            Lst(List(Number(1), Number(2), Number(3))),
            Lambda(
              Some(1),
              List(),
              List(
                Group(
                  List(
                    Group(List(Number(1), Command("+")), Some(1)),
                    Group(List(Number(2), Command("*")), Some(1)),
                  ),
                  None,
                )
              ),
            ),
            Command("M"),
          ),
          None,
        )
    )
  }
  test("Does the parser correctly handle variable unpacking?") {
    assert(
      parse("#[1|2|3#] #:[a|b|c]") ===
        Group(
          List(
            Lst(
              List(
                Number(1),
                Number(2),
                Number(3),
              )
            ),
            UnpackVar(List(("a", 0), ("b", 0), ("c", 0))),
          ),
          None,
        )
    )

    assert(
      parse("#[1|2|3#] #:[a]") ===
        Group(
          List(
            Lst(List(Number(1), Number(2), Number(3))),
            UnpackVar(List(("a", 0))),
          ),
          None,
        )
    )

    assert(parse("#:[[[[[a]]]]]") === UnpackVar(List(("a", 4))))
  }
end ParserTests
