package vyxal

import org.scalatest.compatible.Assertion
import org.scalatest.funspec.AnyFunSpec

trait VyxalTests extends AnyFunSpec:

  def testEquals(expected: VAny)(getRes: Context ?=> VAny): Assertion =
    assertResult(expected)(getRes(using Context()))

  /** Run some code and check if it matches the expected value
    * @param ctx
    *   If needed, context to override default context
    */
  def testCode(
      desc: String,
      code: String,
      expected: VAny,
      ctx: Context = Context()
  ) =
    it(desc) {
      Interpreter.execute(code)(using ctx)
      assert(!ctx.isStackEmpty)
      assertResult(expected)(ctx.pop())
    }

  /** Like [[testCode]], but with an already-parsed AST */
  def testAST(
      desc: String,
      ast: AST,
      expected: VAny,
      ctx: Context = Context()
  ) =
    it(desc) {
      Interpreter.execute(ast)(using ctx)
      assert(!ctx.isStackEmpty)
      assertResult(expected)(ctx.pop())
    }

  /** Run a function on multiple inputs
    * @param tests
    *   A list of pairs with the inputs and expected output for each case
    */
  def testMulti(getRes: Context ?=> VAny)(tests: (List[VAny], VAny)*) =
    for (inputs, expected) <- tests do
      it(s"$inputs -> $expected") {
        val res = getRes(using Context(inputs = inputs))
        assertResult(expected)(res)
      }

  /** Run a piece of code on multiple inputs
    * @param tests
    *   A list of pairs with the inputs and expected output for each case
    */
  def testMulti(code: String)(tests: (List[VAny], VAny)*) =
    for (inputs, expected) <- tests do
      it(s"${inputs.mkString("[", ",", "]")} -> $expected") {
        given ctx: Context = Context(inputs = inputs)
        Interpreter.execute(code)
        assert(!ctx.isStackEmpty)
        assertResult(expected)(ctx.peek)
      }

  /** Run multiple pieces of code with expected outputs
    * @param tests
    *   A list of pairs with the code and expected output for each case
    */
  def testMulti(tests: (String, VAny)*) =
    for (code, expected) <- tests do
      it(s"$code -> $expected") {
        given ctx: Context = Context()
        Interpreter.execute(code)
        assert(!ctx.isStackEmpty)
        assertResult(expected)(ctx.peek)
      }
end VyxalTests
