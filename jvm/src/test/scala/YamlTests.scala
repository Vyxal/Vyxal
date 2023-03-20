package vyxal

import java.util.{ArrayList, Map as JavaMap}
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.Checkpoints.Checkpoint
import org.yaml.snakeyaml.Yaml

/** Tests for specific elements. The format is something like this:
  * {{{
  * - element: "ඞ"
  *   tests:
  *     # A simple v2-style test
  *     - [1, [2, 4]]: [3, 5]
  *     # In case you have lots of inputs or you just want to be explicit
  *     - inputs:
  *         - "foo"
  *         - 2
  *         - ["nested"]
  *     - output: "asdf"
  *     # If you want to specify some conditions about the output
  *     - inputs: "gimme all the primes"
  *       # Ensure the output starts with a certain prefix
  *       starts-with: [2, 3, 5]
  *       # Make sure it contains 0 and 1
  *       contains: [0, 1]
  *       # Same as contains, but assume the list is monotonic (useful for infinite lists)
  *       contains-monotonic: [2]
  *       # To make sure the output *doesn't* meet some conditions
  *       not:
  *         contains: [3]
  *         # Make sure the output *doesn't* end with -9, -10
  *         ends-with: [-9, -10]
  *     # In case you want to group tests
  *     - "should work on numbers":
  *       - [0.1, 4.5]: 42
  *       - [1, 2]: 5
  * }}}
  */
class YamlTests extends AnyFunSpec:
  val TestsFile = "tests.yaml"

  Dictionary.fileInitialise()

  for (element, testGroup) <- loadTests() do
    describe(s"Element $element") {
      execTests(element, testGroup)
    }

  private def execTests(element: String, testGroup: TestGroup): Unit =
    for YamlTest(inputs, codeOverride, criteria) <- testGroup.tests do
      val code = codeOverride.getOrElse(element)
      it(s"Execute `$code` on inputs ${inputs.mkString(", ")}") {
        given ctx: Context = Context(inputs = inputs)
        Interpreter.execute(code)
        val output = ctx.peek
        val cp = Checkpoint()

        criteria.foreach {
          case Criterion.Equals(expected) =>
            cp { assertResult(expected)(output) }
          case crit =>
            cp {
              output match
                case lst: VList =>
                  (crit: @unchecked) match
                    case Criterion.StartsWith(prefix) =>
                      assertResult(prefix)(lst.slice(0, prefix.length))
                    case Criterion.EndsWith(suffix) =>
                      assertResult(suffix)(
                        lst.slice(lst.length - suffix.length, lst.length)
                      )
                    case Criterion.Contains(elems, false) =>
                      val notFound = elems.filter(lst.contains)
                      fail(s"$lst does not contain ${notFound.mkString(",")}")
                    case Criterion.Contains(elems, true) =>
                      ???
                case _ => fail(s"$output is not a list")
            }
        }

        cp.reportAll()
      }
    end for

    for (desc, subgroup) <- testGroup.subgroups do
      describe(desc) {
        execTests(element, subgroup)
      }
  end execTests

  /** Load all the tests, mapping elements to test groups */
  private def loadTests(): Map[String, TestGroup] =
    val yaml = new Yaml()
    val inputStream = this
      .getClass()
      .getClassLoader()
      .getResourceAsStream(TestsFile)
    val parsed: ArrayList[JavaMap[String, Any]] = yaml.load(inputStream)

    parsed.asScala.map { elemInfo =>
      val symbol = elemInfo.get("element").asInstanceOf[String]
      val testsInfo = elemInfo.get("tests").asInstanceOf[ArrayList[Any]]
      symbol -> getTestGroup(testsInfo)
    }.toMap

  /** Load a TestGroup from a part of the parsed YAML */
  private def getTestGroup(testsInfo: ArrayList[Any]): TestGroup =
    val tests = mutable.ArrayBuffer.empty[YamlTest]
    val subgroups = mutable.Map.empty[String, TestGroup]
    for possibleTestInfo <- testsInfo.asScala do
      val testInfo = possibleTestInfo.asInstanceOf[JavaMap[Any, Any]]
      if testInfo.containsKey("inputs") then
        // A test that looks like
        // - inputs: ["foo", "bar"]
        //   outupt: "foobar"
        val inputs = testInfo
          .get("inputs")
          .asInstanceOf[ArrayList[Any]]
          .asScala
          .toSeq
          .map(javaToVyxal)
        val code = Option(testInfo.get("code").asInstanceOf[String])
        tests.append(YamlTest(inputs, code, getOutputCriteria(testInfo)))
      else if testInfo.size() == 1 then
        // Either a test group or a v2-style [...input]: output test
        val firstEntry = testInfo
          .entrySet()
          .iterator()
          .next()
        firstEntry.getKey() match
          case groupName: String =>
            // It's a nested group of tests
            subgroups.put(
              groupName,
              getTestGroup(firstEntry.getValue().asInstanceOf[ArrayList[Any]])
            )
          case inputs: ArrayList[?] =>
            // It's a v2-style test
            val output = javaToVyxal(firstEntry.getValue())
            tests.append(
              YamlTest(
                inputs.asScala.toSeq.map(javaToVyxal),
                None,
                Seq(Criterion.Equals(output))
              )
            )
        end match
      else
        throw new Error(
          s"Invalid test format (no inputs given): $testInfo"
        )
      end if
    end for

    TestGroup(tests.toSeq, subgroups.toMap)
  end getTestGroup

  private def getOutputCriteria(testInfo: JavaMap[Any, Any]): Seq[Criterion] =
    val criteria = mutable.ArrayBuffer.empty[Criterion]
    val output = testInfo.get("output")
    if output != null then criteria += Criterion.Equals(javaToVyxal(output))
    val startsWith = testInfo.get("starts-with")
    if startsWith != null then
      criteria += Criterion.StartsWith(javaListToVyxal(startsWith))
    val endsWith = testInfo.get("starts-with")
    if endsWith != null then
      criteria += Criterion.EndsWith(javaListToVyxal(endsWith))
    val contains = testInfo.get("contains")
    if contains != null then
      criteria += Criterion.Contains(javaListToVyxal(contains))
    val containsMonotonic = testInfo.get("contains-monotonic")
    if containsMonotonic != null then
      criteria +=
        Criterion.Contains(javaListToVyxal(containsMonotonic), monotonic = true)

    criteria.toSeq
  end getOutputCriteria

  private def javaToVyxal(obj: Any): VAny = obj match
    case s: String          => s
    case i: Int             => VNum(i)
    case d: Double          => VNum(d)
    case list: ArrayList[?] => VList.from(list.asScala.toSeq.map(javaToVyxal))

  private def javaListToVyxal(list: Any): Seq[VAny] =
    list.asInstanceOf[ArrayList[Any]].asScala.toSeq.map(javaToVyxal)
end YamlTests

/** A list of tests. Can be nested
  *
  * @param subgroups
  *   Maps descriptions of test groups to the tests themselves
  */
case class TestGroup(tests: Seq[YamlTest], subgroups: Map[String, TestGroup])

/** A single test
  *
  * @param code
  *   Specific bit of code to be run, in case the element cannot be used
  *   directly
  */
case class YamlTest(
    inputs: Seq[VAny],
    code: Option[String],
    criteria: Seq[Criterion]
)

/** A criterion for the output of a test to meet */
enum Criterion:
  case Equals(expected: VAny)
  case StartsWith(prefix: Seq[VAny])
  case EndsWith(suffix: Seq[VAny])
  case Contains(elems: Seq[VAny], monotonic: Boolean = false)
