package vyxal

import org.scalatest.funsuite.AnyFunSuite

import AST.*

class InterpreterTests extends AnyFunSuite:
  test("Can the interpreter make lists?") {
    given ctx: Context = Context()
    Interpreter.execute("#[1 | 2 3 + | 4#]")
    assert(ctx.pop() == VList(1, 5, 4))
  }

  test("Can the interpreter execute if statements?") {
    given ctx: Context = Context()
    Interpreter.execute("0 [ 2 | 5 } : [ 6 | 4} +")
    assert(ctx.pop() == VNum(11))
  }

  test("Can the interpreter execute while loops?") {
    given ctx: Context = Context()
    Interpreter.execute("6 { : 2 - | 1 -}")
    assert(ctx.pop() == VNum(2))
  }

  test("Can the interpreter vectorise simple stuff?") {
    given ctx: Context = Context()
    Interpreter.execute("#[4 | #[5 | 6#] #] 3 v+")
    assert(ctx.pop() == VList(7, VList(8, 9)))
    Interpreter.execute("#[4 | #[5 | 6#] #] #[4#] v+")
    assert(ctx.pop() == VList(8, VList(5, 6)))
  }
end InterpreterTests
