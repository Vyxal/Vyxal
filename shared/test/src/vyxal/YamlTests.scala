package vyxal

import scala.collection.mutable
import scala.io.Source

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.Checkpoints.Checkpoint
import org.virtuslab.yaml.*

/** A list of tests. Can be nested */
enum TestGroup:
  case Subgroups(subgroups: Map[String, TestGroup])
  case Tests(tests: Iterable[YamlTest])

/** A single test
  *
  * @param code
  *   Specific bit of code to be run, in case the element cannot be used
  *   directly
  */
case class YamlTest(
    inputs: Seq[VAny],
    flags: List[Char],
    code: Option[String],
    criterion: Seq[Criterion],
    excludeNative: Boolean = false,
)

/** A criterion for the output of a test to meet */
enum Criterion:
  case Equals(expected: VAny)
  case StartsWith(prefix: Seq[VAny])
  case EndsWith(suffix: Seq[VAny])

  /** The output must be a list containing all of the given elements */
  case Contains(elems: Seq[VAny], monotonic: Boolean = false)

  /** The top of the stack must match `elems` */
  case Stack(elems: Seq[VAny])

/** Tests for specific elements, loaded from tests.yaml. See the documentation
  * for information about the format.
  */
class YamlTests extends AnyFunSpec:

  var usingNative = false

  try "(?!.*@)".r.findFirstMatchIn("h")
  catch
    case _ => usingNative = true

    /** The file to load tests from */
  val TestsFile = "/tests.yaml"

  /** YAML tag for scalars to be parsed as VNums */
  val NumTag = CustomTag("!num")

  /** YAML tag for scalars that are to be evaluated as Vyxal values */
  val VAnyTag = CustomTag("!vany")

  for (element, testGroup) <- loadTests() do
    describe(s"Element $element") {
      execTests(element, testGroup)
    }

  private def execTests(element: String, testGroup: TestGroup): Unit =
    testGroup match
      case TestGroup.Subgroups(subgroups) =>
        for (desc, subgroup) <- subgroups do
          describe(desc) {
            execTests(element, subgroup)
          }
      case TestGroup.Tests(tests) =>
        for YamlTest(inputs, flags, codeOverride, criteria, excludeNative) <-
            tests
        do
          if usingNative && excludeNative then
            println(s"Skipping JVM-only test for $element")
          else
            val code = codeOverride.getOrElse(element)
            val inputStr = inputs.map(StringHelpers.repr).mkString(", ")
            val msg =
              if codeOverride.isEmpty then s"Element: $code, Inputs: $inputStr"
              else s"Code: `$code, inputs: $inputStr"

            Elements.elements(element).arity match
              case Some(arity) => if arity > 0 && arity != inputs.size then
                  println(
                    s"[Element $element] Inputs (${inputs.mkString(",")}) don't match arity ($arity)"
                  )
              case _ => ()
            it(msg) {
              given ctx: Context =
                VyxalTests.testContext(inputs = inputs, flags = flags)
              Interpreter.execute(code)
              val output = ctx.peek
              val checkpoint = Checkpoint()

              criteria.foreach {
                case Criterion.Equals(expected) =>
                  checkpoint { assertResult(expected)(output) }
                case Criterion.Stack(elems) =>
                  checkpoint { assertResult(elems)(ctx.peek(elems.length)) }
                case crit => checkpoint {
                    output match
                      case lst: VList => (crit: @unchecked) match
                          case Criterion.StartsWith(prefix) =>
                            assertResult(prefix)(lst.slice(0, prefix.length))
                          case Criterion.EndsWith(suffix) =>
                            assertResult(suffix)(
                              lst.slice(lst.length - suffix.length, lst.length)
                            )
                          case Criterion.Contains(elems, false) =>
                            val notFound = elems.filterNot(lst.contains)
                            if notFound.nonEmpty then
                              fail(
                                s"$lst does not contain ${notFound.mkString(",")}"
                              )
                          case Criterion.Contains(elems, true) => ???
                      case _ => fail(s"$output is not a list")
                  }
              }

              checkpoint.reportAll()
            }
          end if
        end for

  end execTests

  /** Load all the tests, mapping elements to test groups */
  private def loadTests(): Map[String, TestGroup] =
    val file = Source.fromInputStream(getClass().getResourceAsStream(TestsFile))
    val yaml = file.mkString
    file.close()

    yaml.as[Map[String, TestGroup]] match
      case Right(elemInfos) => elemInfos
      case Left(e) => throw Error(s"Parsing tests.yaml failed: $e")

  /** Assume a Node is a scalar, and get its text */
  private def scalarText(node: Node): String =
    val Node.ScalarNode(text, _) = node: @unchecked
    text

  /** Assumes a Node is a mapping, and gets a value from it given the
    * corresponding key
    */
  private def getValue(node: Node, key: String): Option[Node] =
    val Node.MappingNode(map, _) = node: @unchecked
    map.find((k, _) => scalarText(k) == key).map(_._2)

  def decodeNode(node: Node): VAny =
    // todo make this use Lefts instead of throwing
    node match
      case Node.ScalarNode(text, tag) =>
        if tag == Tag.int || tag == Tag.float || tag == NumTag then VNum(text)
        else if tag == VAnyTag then
          given ctx: Context =
            Context(globals =
              Globals(settings = Settings(endPrintMode = EndPrintMode.None))
            )
          Interpreter.execute(text)
          ctx.pop()
        else if tag == Tag.str then
          text.replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t")
        else throw Error(s"Invalid Vyxal value: $text $tag")
      case Node.SequenceNode(lst, _) => VList.from(lst.map(decodeNode))
      case _ => throw Error(s"Invalid Vyxal value (cannot be map): $node")

  given YamlDecoder[VAny] =
    new YamlDecoder:
      override def construct(node: Node)(using LoadSettings) =
        Right(decodeNode(node))

  given YamlDecoder[TestGroup] =
    new YamlDecoder:
      override def construct(node: Node)(using LoadSettings) =
        node match
          case Node.SequenceNode(testInfos, _) =>
            val tests = testInfos.map { test =>
              val Node.SequenceNode(inputs, _) =
                getValue(test, "in").get: @unchecked
              val flags =
                getValue(test, "flags").fold(Nil)(scalarText(_).toList)
              val code = getValue(test, "code").map(scalarText)
              val output = getOutputCriteria(test)
              val excludeNative = getValue(test, "jvm-only").isDefined
              YamlTest(
                inputs.map(decodeNode),
                flags,
                code,
                output,
                excludeNative,
              )
            }
            Right(TestGroup.Tests(tests))
          case Node.MappingNode(subgroupNodes, _) =>
            val subgroups = subgroupNodes.map { (nameNode, groupInfo) =>
              val Node.ScalarNode(name, _) = nameNode: @unchecked
              name ->
                this
                  .construct(groupInfo)
                  .toOption
                  .getOrElse(
                    throw Error(s"Error encountered parsing group $name")
                  )
            }.toMap
            // todo return a Left if errors were found instead of throwing immediately
            Right(TestGroup.Subgroups(subgroups))
          case _ => throw Error(s"Test groups cannot be scalars: $node")

  private def getOutputCriteria(testInfo: Node): Seq[Criterion] =
    val criteria = mutable.ArrayBuffer.empty[Criterion]
    for output <- getValue(testInfo, "out") do
      criteria += Criterion.Equals(decodeNode(output))
    for case Node
        .SequenceNode(startsWith, _) <- getValue(testInfo, "starts-with")
    do criteria += Criterion.StartsWith(startsWith.map(decodeNode))
    for case Node.SequenceNode(endsWith, _) <- getValue(testInfo, "ends-with")
    do criteria += Criterion.EndsWith(endsWith.map(decodeNode))
    for case Node.SequenceNode(contains, _) <- getValue(testInfo, "contains") do
      criteria += Criterion.Contains(contains.map(decodeNode))
    for case Node
        .SequenceNode(contains, _) <- getValue(testInfo, "contains-monotonic")
    do
      criteria +=
        Criterion.Contains(
          contains.map(decodeNode),
          monotonic = true,
        )
    for case Node.SequenceNode(stack, _) <- getValue(testInfo, "stack") do
      criteria += Criterion.Stack(stack.map(decodeNode))

    if criteria.isEmpty then
      throw Error(s"No criteria given for test case $testInfo")

    criteria.toSeq
  end getOutputCriteria
end YamlTests
