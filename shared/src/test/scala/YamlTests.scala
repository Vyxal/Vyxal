package vyxal

import java.io.InputStreamReader
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

import io.circe.{yaml, Decoder, HCursor, Json}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.Checkpoints.Checkpoint


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
    code: Option[String],
    criteria: Seq[Criterion]
)

/** A criterion for the output of a test to meet */
enum Criterion:
  case Equals(expected: VAny)
  case StartsWith(prefix: Seq[VAny])
  case EndsWith(suffix: Seq[VAny])
  /** The output must be a list containing all of the given elements */
  case Contains(elems: Seq[VAny], monotonic: Boolean = false)

/** Tests for specific elements. The format is something like this:
  * {{{
  * # The element itself
  * "ඞ":
  *   # For relatively small cases
  *   - { in: [1, 2], out: 3 }
  *   # In case you have lots of inputs or you just want to be explicit
  *   - in:
  *     - "foo"
  *     - 2
  *     - ["nested"]
  *   - out: "asdf"
  *   # If you want to specify some conditions about the output
  *   - in: ["gimme all the primes"]
  *     # Ensure the output starts with a certain prefix
  *     starts-with: [2, 3, 5]
  *     # Make sure it contains 0 and 1
  *     contains: [0, 1]
  *     # Same as contains, but assume the list is monotonic (useful for infinite lists)
  *     contains-monotonic: [2]
  *     # To make sure the output *doesn't* meet some conditions
  *     not:
  *       contains: [3]
  *       # Make sure the output *doesn't* end with -9, -10
  *       ends-with: [-9, -10]
  * "+":
  *   # You can also make groups of tests
  *   should work on numbers:
  *     nested:
  *       - { in: [0.1, 4.5], out: 42 }
  *       - { in: [1, 2], out: 5 }
  *   other group of tests:
  *     - { in: [1], out: 5 }
  *   # But you can't mix test groups and test lists
  *   - { in: [], out: [] } # This line isn't allowed here
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
    testGroup match
      case TestGroup.Subgroups(subgroups) =>
        for (desc, subgroup) <- subgroups do
          describe(desc) {
            execTests(element, subgroup)
          }
      case TestGroup.Tests(tests) =>
        for YamlTest(inputs, codeOverride, criteria) <- tests do
          val code = codeOverride.getOrElse(element)
          it(s"Execute `$code` on inputs ${inputs.mkString(", ")}") {
            given ctx: Context = Context(inputs = inputs)
            Interpreter.execute(code)
            val output = ctx.peek
            val checkpoint = Checkpoint()

            criteria.foreach {
              case Criterion.Equals(expected) =>
                checkpoint { assertResult(expected)(output) }
              case crit =>
                checkpoint {
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
                          fail(
                            s"$lst does not contain ${notFound.mkString(",")}"
                          )
                        case Criterion.Contains(elems, true) =>
                          ???
                    case _ => fail(s"$output is not a list")
                }
            }

            checkpoint.reportAll()
          }
        end for

  end execTests

  /** Load all the tests, mapping elements to test groups */
  private def loadTests(): Map[String, TestGroup] =
    val json = yaml.parser.parse(
      new InputStreamReader(
        getClass().getClassLoader().getResourceAsStream(TestsFile)
      )
    )

    json match
      case Right(parsed) =>
        val elemInfos = parsed.asObject.getOrElse(
          throw Error(s"Expected tests.yaml to be a map")
        )
        elemInfos.keys.map { symbol =>
          val elem = elemInfos(symbol).get
          symbol -> parseOrThrow[TestGroup](elem)
        }.toMap
      case Left(e) => throw Error("Parsing tests.yaml failed", e)
  end loadTests

  /** Try parsing some JSON as type `A` and throw if parsing failed */
  private def parseOrThrow[A](json: Json)(using Decoder[A]): A =
    json.as[A] match
      case Right(v) => v
      case Left(e)  => throw e

  given Decoder[VAny] = new Decoder:
    override def apply(c: HCursor): Decoder.Result[VAny] =
      // todo make this return an Either instead
      def fromJson(json: Json): VAny =
        if json.isArray then VList.from(json.asArray.get.map(fromJson))
        else if json.isNumber then json.asNumber.get.toDouble
        else if json.isString then json.asString.get
        else throw Error(s"Invalid Vyxal value: $json")
      Right(fromJson(c.value))

  given Decoder[TestGroup] = new Decoder:
    override def apply(c: HCursor): Decoder.Result[TestGroup] =
      c.values match
        case Some(testInfos) =>
          val tests = testInfos.map { test =>
            val inputs = (test \\ "in").head.asArray.get.map(parseOrThrow[VAny])
            val code = (test \\ "code").headOption.map(_.asString.get)
            val output = getOutputCriteria(test)
            YamlTest(inputs, code, output)
          }
          Right(TestGroup.Tests(tests))
        case None =>
          val subgroups = c.keys.get.map { groupName =>
            groupName -> parseOrThrow[TestGroup](
              c
                .downField(groupName)
                .success
                .get
                .value
            )
          }.toMap
          // todo return a Left if errors were found instead of throwing immediately
          Right(TestGroup.Subgroups(subgroups))
    end apply

  private def getOutputCriteria(testInfo: Json): Seq[Criterion] =
    val criteria = mutable.ArrayBuffer.empty[Criterion]
    for output <- testInfo \\ "out" do
      criteria += Criterion.Equals(parseOrThrow(output))
    for startsWith <- testInfo \\ "starts-with" do
      criteria += Criterion.StartsWith(parseOrThrow[List[VAny]](startsWith))
    for endsWith <- testInfo \\ "ends-with" do
      criteria += Criterion.EndsWith(parseOrThrow[List[VAny]](endsWith))
    for contains <- testInfo \\ "contains" do
      criteria += Criterion.Contains(parseOrThrow[List[VAny]](contains))
    for contains <- testInfo \\ "contains-monotonic" do
      criteria += Criterion.Contains(
        parseOrThrow[List[VAny]](contains),
        monotonic = true
      )

    criteria.toSeq
  end getOutputCriteria
end YamlTests
