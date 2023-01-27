package vyxal

import org.scalatest.compatible.Assertion
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.Checkpoints.Checkpoint
import org.scalatest.Succeeded
import scala.compiletime.codeOf
import scala.quoted.*

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

/** Group multiple assertions together.
  *
  * An example:
  * ```scala
  * group {
  *   assert(false)
  *   assertResult(VNum(1))(VNum.from("1"))
  *   assertResult(VNum(0.5))(VNum.from("."))
  * }
  * ```
  * The first assertion will fail, but the second and third will still be
  * checked and found to pass because they're run separately using
  * [[org.scalatest.Checkpoints]]. That block would turn into roughly
  * ```scala
  * val cp = new Checkpoint()
  * cp { assert(false) }
  * cp { assertResult(VNum(1))(VNum.from("1")) }
  * cp { assertResult(VNum(0.5))(VNum.from(".")) }
  * cp.reportAll()
  * ```
  *
  * TODO figure out how to do this without macros
  */
inline def group(inline asserts: Unit): Unit =
  ${ groupImpl('asserts) }

/** Implementation for [[group]] */
private def groupImpl(asserts: Expr[Unit])(using Quotes): Expr[Unit] =
  import quotes.reflect.*
  asserts.asTerm match
    case Inlined(_, _, term) =>
      term match
        case Block(stmts, lastTerm) =>
          '{
            val cp = Checkpoint()
            ${
              Block(
                stmts.map { stmt => '{ cp { ${ stmt.asExpr } } }.asTerm },
                '{ cp { ${ lastTerm.asExpr } } }.asTerm
              ).asExpr.asInstanceOf[Expr[Unit]]
            }
            cp.reportAll()
          }
        case expr =>
          '{
            val cp = Checkpoint()
            cp { ${ expr.asExpr } }
            cp.reportAll()
          }
    case _ => throw new IllegalArgumentException(asserts.show)
  end match
end groupImpl
