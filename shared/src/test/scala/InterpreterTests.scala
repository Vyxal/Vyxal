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

  test("Can the interpreter vectorise simple monads?") {
    val sb = new StringBuilder()
    // Instead of printing, add to sb so we can inspect it
    given ctx: Context = Context(printFn = sb.append)
    Interpreter.execute("#[4 | #[5 | 6#] #] v,")
    assert(sb.toString == "4\n5\n6\n")
  }

  test("Can the interpreter vectorise simple dyads?") {
    given ctx: Context = Context()
    Interpreter.execute("#[4 | #[5 | 6#] #] 3 v;")
    assert(ctx.pop() == VList(VList(4, 3), VList(VList(5, 3), VList(6, 3))))
    Interpreter.execute("#[4 | #[5 | 6#] #] #[4#] v;")
    assert(ctx.pop() == VList(VList(4, 4), VList(VList(5, 0), VList(6, 0))))
  }
end InterpreterTests
