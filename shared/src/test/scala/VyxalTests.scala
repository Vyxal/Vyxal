package vyxal

import org.scalatest.compatible.Assertion
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.Checkpoints.Checkpoint
import org.scalatest.Succeeded
import scala.compiletime.codeOf
import scala.quoted.*

trait VyxalTests extends AnyFunSpec:

  private val ShortDictionaryFile = "/ShortDictionary.txt"
  private val LongDictionaryFile = "/LongDictionary.txt"
  private def readResource(path: String) =
    io.Source
      .fromInputStream(getClass.getResourceAsStream(path))
      .getLines()
      .toSeq
  Dictionary.initialise(
    readResource(ShortDictionaryFile),
    readResource(LongDictionaryFile)
  )

  def testEquals(expected: VAny)(getRes: Context ?=> VAny): Assertion =
    assertResult(expected)(getRes(using Context(testMode = true)))

  /** Run some code and check if it matches the expected value
    * @param inputs
    *   Inputs to pass to the code
    */
  def testCode(
      code: String,
      expected: VAny,
      inputs: Seq[VAny] = Seq.empty
  ) =
    val ctx = Context(testMode = true, inputs = inputs)
    Interpreter.execute(code)(using ctx)
    assert(!ctx.isStackEmpty)
    assertResult(expected)(ctx.peek)

  /** Like [[testCode]], but with an already-parsed AST */
  def testAST(
      ast: AST,
      expected: VAny,
      inputs: Seq[VAny] = Seq.empty
  ) =
    val ctx = Context(testMode = true, inputs = inputs)
    Interpreter.execute(ast)(using ctx)
    assert(!ctx.isStackEmpty)
    assertResult(expected)(ctx.peek)

  /** Run a function on multiple inputs
    * @param tests
    *   A list of pairs with the inputs and expected output for each case
    */
  def testMulti(getRes: Context ?=> VAny)(tests: (List[VAny], VAny)*) =
    for (inputs, expected) <- tests do
      it(s"$inputs -> $expected") {
        val res = getRes(using Context(inputs = inputs, testMode = true))
        assertResult(expected)(res)
      }

  /** Run a piece of code on multiple inputs
    * @param tests
    *   A list of pairs with the inputs and expected output for each case
    */
  def testMulti(code: String)(tests: (Seq[VAny], VAny)*) =
    for (inputs, expected) <- tests do
      it(s"${inputs.mkString("[", ",", "]")} -> $expected") {
        given ctx: Context = Context(inputs = inputs, testMode = true)
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
        given ctx: Context = Context(testMode = true)
        Interpreter.execute(code)
        assert(!ctx.isStackEmpty)
        assertResult(expected)(ctx.peek)
      }

  /** Run a piece of code on multiple inputs and check the stack at the end
    * @param tests
    *   A list of pairs with the inputs and expected stack for each case
    */
  def testStackLike(code: String)(tests: (List[VAny], List[VAny])*) =
    for (inputs, stackEnd) <- tests do
      it(s"$inputs -> $stackEnd") {
        given ctx: Context = Context(inputs = inputs, testMode = true)
        for i <- inputs do ctx.push(i)
        Interpreter.execute(code)
        assertResult(ctx.pop(stackEnd.length))(stackEnd)
      }

  // TODO figure out how to do group without macros
  // TODO figure out why we need to go through another inline method in the
  //     companion instead of calling groupImpl directly from here
  /** Group multiple assertions together.
    *
    * An example:
    * ```scala
    * group {
    *   assert(false)
    *   assertResult(VNum(1))(VNum("1"))
    *   assertResult(VNum(0.5))(VNum("."))
    * }
    * ```
    * The first assertion will fail, but the second and third will still be
    * checked and found to pass because they're run separately using
    * [[org.scalatest.Checkpoints]]. That block would turn into roughly
    * ```scala
    * val cp = new Checkpoint()
    * cp { assert(false) }
    * cp { assertResult(VNum(1))(VNum("1")) }
    * cp { assertResult(VNum(0.5))(VNum(".")) }
    * cp.reportAll()
    * ```
    */
  inline def group(inline asserts: Unit): Unit = VyxalTests.group(asserts)
end VyxalTests

object VyxalTests:
  private inline def group(inline asserts: Unit): Unit =
    ${ VyxalTests.groupImpl('asserts) }

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
end VyxalTests
