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
  case Contains(elems: Seq[VAny], monotonic: Boolean)

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
    val file = Source.fromInputStream(
      getClass().getResourceAsStream(TestsFile)
    )(using io.Codec.UTF8)
    val yaml = file.mkString
    file.close()

    yaml.as[Map[String, TestGroup]] match
      case Right(elemInfos) => elemInfos
      case Left(e) => throw Error(s"Parsing tests.yaml failed: $e")

  /** Assume a Node is a scalar, and get its text */
  private def scalarText(node: Node): Either[ConstructError, String] =
    node match
      case Node.ScalarNode(text, _) => Right(text)
      case _ =>
        Left(ConstructError.from("Node is not a string", node, "a string"))

  /** Assumes a Node is a mapping, and gets a value from it given the
    * corresponding key
    */
  private def getValue(node: Node, key: String): Option[Node] =
    node match
      case Node.MappingNode(map, _) => map.collectFirst {
          case (k, v) if scalarText(k).map(_ == key).getOrElse(false) => v
        }
      case _ => None

  given vanyDecoder: YamlDecoder[VAny] =
    new YamlDecoder:
      override def construct(node: Node)(using
          LoadSettings
      ): Either[ConstructError, VAny] =
        // todo make this use Lefts instead of throwing
        node match
          case Node.ScalarNode(text, tag) =>
            if tag == Tag.int || tag == Tag.float || tag == NumTag then
              Right(VNum(text))
            else if tag == VAnyTag then
              given ctx: Context =
                Context(globals =
                  Globals(settings = Settings(endPrintMode = EndPrintMode.None))
                )
              Interpreter.execute(text)
              Right(ctx.pop())
            else if tag == Tag.str then
              Right(text.replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t"))
            else
              Left(
                ConstructError.from(
                  s"Invalid tag: $tag",
                  node,
                  "int, float, num, str, or vany",
                )
              )
          case Node.SequenceNode(lst, _) =>
            combineEithers(lst.map(vanyDecoder.construct(_))).map(VList.from)
          case _ => Left(
              ConstructError.from(
                "Invalid Vyxal value (cannot be a map)",
                node,
                "list, string, or number",
              )
            )

  given YamlDecoder[TestGroup] =
    new YamlDecoder:
      private def parseTest(test: Node)(using
          LoadSettings
      ): Either[ConstructError, YamlTest] =
        for
          inputs <- getValue(test, "in") match
            case Some(Node.SequenceNode(inputs, _)) =>
              combineEithers(inputs.map(vanyDecoder.construct(_)))
            case Some(inNode) => Left(
                ConstructError
                  .from("Inputs need to be a list (wrap them in [])", inNode)
              )
            case None => Left(ConstructError.from("Test has no inputs", test))
          flags <- getValue(test, "flags") match
            case Some(flags) => scalarText(flags).map(_.toList)
            case None => Right(Nil)
          code <- getValue(test, "code") match
            case Some(code) => scalarText(code).map(Some(_))
            case None => Right(None)
          output <- getOutputCriteria(test)
        yield
          val excludeNative = getValue(test, "jvm-only").isDefined

          YamlTest(
            inputs,
            flags,
            code,
            output,
            excludeNative,
          )
      end parseTest

      override def construct(node: Node)(using LoadSettings) =
        node match
          case Node.SequenceNode(testInfos, _) =>
            combineEithers(testInfos.map(parseTest)).map(TestGroup.Tests(_))
          case Node.MappingNode(subgroupNodes, _) => combineEithers(
              subgroupNodes.map {
                case (Node.ScalarNode(name, _), groupInfo) =>
                  this.construct(groupInfo).map(name -> _)
                case (nameNode, _) => Left(
                    ConstructError.from(
                      "Name for test subgroup must be a string",
                      nameNode,
                      "a string",
                    )
                  )
              }
            ).map { subgroups => TestGroup.Subgroups(subgroups.toMap) }
          case _ => return Left(
              ConstructError.from(
                "Test groups cannot be scalars",
                node,
                "test subgroups or a list of test cases",
              )
            )

  private def getOutputCriteria(testInfo: Node)(using
      LoadSettings
  ): Either[ConstructError, Seq[Criterion]] =
    /** Helper to get criteria that are lists of values */
    def getList(fieldName: String)(toCriterion: Seq[VAny] => Criterion) =
      getValue(testInfo, fieldName) match
        case Some(Node.SequenceNode(vals, _)) => Some(
            combineEithers(vals.map(vanyDecoder.construct(_))).map(toCriterion)
          )
        case _ => None

    val criteria = List(
      getValue(testInfo, "out").map { output =>
        vanyDecoder.construct(output).map(Criterion.Equals(_))
      },
      getList("starts-with")(Criterion.StartsWith(_)),
      getList("ends-with")(Criterion.EndsWith(_)),
      getList("contains")(Criterion.Contains(_, false)),
      getList("contains-monotonic")(Criterion.Contains(_, true)),
      getList("stack")(Criterion.Stack(_)),
    ).flatten

    if criteria.isEmpty then
      Left(
        ConstructError.from(
          "No criteria",
          testInfo,
          "one of out, starts-with, ends-with, contains, contains-monotonic, and stack",
        )
      )
    else combineEithers(criteria)
  end getOutputCriteria

  /** Take a bunch of results from a decoder, and if all of them were Rights,
    * return a Right with a list of the results, otherwise return a Left
    * containing the first error
    */
  private def combineEithers[A, B](
      eithers: Iterable[Either[A, B]]
  ): Either[A, Seq[B]] =
    eithers.foldLeft(Right(Seq.empty): Either[A, Seq[B]]) { (acc, either) =>
      either.flatMap { right => acc.map(_ :+ right) }
    }
end YamlTests
