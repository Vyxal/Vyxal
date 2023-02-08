package vyxal

import java.util.{ArrayList, Map as JavaMap}
import scala.jdk.CollectionConverters.*

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.Checkpoints.Checkpoint
import org.yaml.snakeyaml.Yaml

/** Tests for specific elements */
class YamlTests extends AnyFunSuite:
  val TestsFile = "tests.yaml"

  {
    val yaml = new Yaml()
    val inputStream = this
      .getClass()
      .getClassLoader()
      .getResourceAsStream(TestsFile)
    val parsed: ArrayList[JavaMap[String, Any]] = yaml.load(inputStream)

    for elementInfo <- parsed.asScala do
      val symbol = elementInfo.get("element").asInstanceOf[String]
      val testsInfo = elementInfo
        .get("tests")
        .asInstanceOf[ArrayList[Any]]
        .asScala
      test(s"Element $symbol") {
        val cp = Checkpoint()
        for testInfo <- testsInfo do
          (testInfo: @unchecked) match
            case testInfo: JavaMap[?, ?] =>
              if testInfo.containsKey("inputs") then
                val inputs = testInfo
                  .get("inputs")
                  .asInstanceOf[ArrayList[Any]]
                  .asScala
                  .toSeq
                  .map(javaToVyxal)
                val output = javaToVyxal(testInfo.get("outputs"))
                cp {
                  given ctx: Context = Context(inputs = inputs)
                  Interpreter.execute(symbol)
                  assertResult(output)(ctx.peek)
                }
              else if testInfo.size() == 1 then
                // Normal [...input]: output test
                val inputs = testInfo
                  .keySet()
                  .iterator()
                  .next()
                  .asInstanceOf[ArrayList[Any]]
                  .asScala
                  .toSeq
                  .map(javaToVyxal)
                val output = javaToVyxal(testInfo.values().iterator().next())
                cp {
                  given ctx: Context = Context(inputs = inputs)
                  Interpreter.execute(symbol)
                  assertResult(output)(ctx.peek)
                }
              else
                throw new Error(
                  s"Invalid test format (no inputs given): $testInfo"
                )
            case _ =>
              throw new Error(
                s"Invalid test format: $testInfo (${testInfo.getClass})"
              )
        end for
        cp.reportAll()
      }
    end for
  }

  private def javaToVyxal(obj: Any): VAny = obj match
    case s: String          => s
    case i: Int             => VNum(i)
    case d: Double          => VNum(d)
    case list: ArrayList[?] => VList(list.asScala.toSeq.map(javaToVyxal)*)
end YamlTests
