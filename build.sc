import java.net.{URL, URLClassLoader}

import mill._
import mill.api.Result
import mill.scalajslib._
import mill.scalajslib.api._
import mill.scalalib._
import mill.scalalib.scalafmt.ScalafmtModule
import mill.scalanativelib._
import mill.scalanativelib.api._

/** Shared settings for all modules */
trait VyxalModule extends ScalaModule with ScalafmtModule {
  override def scalaVersion = "3.3.1"

  def vyxalVersion = "3.12.0"

  def platform: String

  override def ivyDeps =
    Agg(
      ivy"org.typelevel::spire::0.18.0",
      ivy"com.github.scopt::scopt::4.1.0",
      ivy"com.outr::scribe::3.13.5",
      ivy"org.scala-lang::toolkit:0.6.0",
      ivy"io.github.cquiroz::scala-java-time::2.6.0",
    )

  override def scalacOptions =
    Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-encoding",
      "utf-8", // Specify character encoding used by source files.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      // Above options from https://tpolecat.github.io/2017/04/25/scalac-flags.html
      "-Wunused:all", // Warn about unused values and stuff
      // "-Wvalue-discard", // Warn about expressions whose values aren't used
      "-language:implicitConversions",
      // "-explain",
      "-print-lines",
    )

  // Combine shared sources and platform-specific sources
  override def sources =
    T.sources(
      build.millSourcePath / platform / "src",
      build.millSourcePath / "shared" / "src",
    )
  override def resources =
    T.sources(
      build.millSourcePath / platform / "resources",
      build.millSourcePath / "shared" / "resources",
    )

  trait VyxalTestModule
      extends JavaTests
      with TestModule.ScalaTest
      with ScalafmtModule {
    override def defaultCommandName() = "test"

    override def ivyDeps =
      Agg(
        ivy"org.scalatest::scalatest::3.2.19",
        ivy"org.scala-sbt:test-interface:1.0",
        ivy"org.virtuslab::scala-yaml::0.0.8",
      )

    // Task to only show output from failed tests
    def testQuiet(args: String*) = {
      val newArgs = if (args.contains("--")) args else args :+ "--"
      T.command { testOnly(newArgs :+ "-oNCXEORM": _*)() }
    }

    override def sources =
      T.sources(
        build.millSourcePath / platform / "test" / "src",
        build.millSourcePath / "shared" / "test" / "src",
      )
    override def resources =
      T.sources(
        build.millSourcePath / platform / "test" / "resources",
        build.millSourcePath / "shared" / "test" / "resources",
      )
  }
}

trait JvmCommon extends VyxalModule {
  val platform = "jvm"

  def ivyDeps =
    T {
      super.ivyDeps() ++
        Seq(
          // For the REPL
          ivy"org.jline:jline:3.30.17",
          ivy"org.jline:jline-terminal-jansi:3.30.4",
          ivy"org.fusesource.jansi:jansi:2.4.3",
        )
    }

  def forkEnv: T[Map[String, String]] =
    Map("REPL" -> "false", "VYXAL_LOG_LEVEL" -> "Debug")

  override def assembly =
    T {
      // Make sure to generate nanorcs first
      jvm.nanorc()
      // Rename jar to vyxal-<version>.jar
      val out = T.dest / s"vyxal-$vyxalVersion.jar"
      os.move(super.assembly().path, out)
      PathRef(out)
    }
}

/** Shared and JVM-specific code */
object jvm extends JvmCommon {
  val platform = "jvm"

  def mainClass: T[Option[String]] = Some("vyxal.Main")

  /** Generate elements.txt and trigraphs.txt */
  def docs =
    T {
      jvm.runMain(
        "vyxal.gen.generateDocs",
        (build.millSourcePath / "documentation" / "table.md").toString,
        (build.millSourcePath / "contributing" / "vyxal.grammar").toString,
        (build.millSourcePath / "contributing" / "vyxalLit.grammar").toString,
      )()
    }

  def sortYAML =
    T {
      jvm.runMain(
        "vyxal.gen.sortYAML",
        (build.millSourcePath / "shared" / "test" / "resources" /
          "tests.yaml").toString,
      )()
    }

  /** Generate nanorc files for JLine highlighting */
  def nanorc =
    T {
      jvm.runMain(
        "vyxal.gen.GenerateNanorc",
        (build.millSourcePath / "jvm" / "resources").toString,
      )()
    }

  /** Generate parsed_yaml.js and theseus.json */
  def theseus =
    T {
      jvm.runMain(
        "vyxal.gen.generateTheseus",
        (build.millSourcePath / "pages" / "theseus.json").toString,
      )()
    }

  def fuzz(min: Int, max: Int, timeout: Int) =
    T.command {
      jvm.runMain("vyxal.fuzz", min.toString, max.toString, timeout.toString)
    }

  object test extends ScalaTests with VyxalTestModule
}

object jvmLiterate extends JvmCommon {
  def mainClass: T[Option[String]] = Some("vyxal.MainLit")

  object test extends ScalaTests with VyxalTestModule
}

/** Shared and JS-specific code */
object js extends VyxalModule with ScalaJSModule {
  val platform = "js"

  def scalaJSVersion = "1.16.0"
  def moduleKind = T { ModuleKind.ESModule }

  def ivyDeps =
    T {
      super.ivyDeps() ++
        Seq(
          ivy"org.scala-js::scalajs-dom::2.8.0",
          ivy"io.github.cquiroz::scala-java-time-tzdb::2.6.0",
        )
    }

  def pagesDir = build.millSourcePath / "pages"

  override def fastLinkJS =
    T {
      val res = super.fastLinkJS()
      os.copy.over(res.dest.path / "vyxal.js", pagesDir / "vyxal.js")
      os.copy.over(res.dest.path / "vyxal.js.map", pagesDir / "vyxal.js.map")
      os.copy.over(res.dest.path / "helpText.js", pagesDir / "helpText.js")
      os.copy
        .over(res.dest.path / "helpText.js.map", pagesDir / "helpText.js.map")

      val generatedFiles = os
        .walk(res.dest.path)
        .filter(f =>
          (f.ext == "js" || f.ext == "map") && f.last.startsWith("internal")
        )
      // move each file to pages directory
      generatedFiles.foreach { file =>
        println(file)
        os.move.over(file, pagesDir / file.last)
      }

      copyDicts()
      res
    }

  override def fullLinkJS =
    T {
      val res = super.fastLinkJS()
      os.copy.over(res.dest.path / "vyxal.js", pagesDir / "vyxal.js")
      os.copy.over(res.dest.path / "vyxal.js.map", pagesDir / "vyxal.js.map")
      os.copy.over(res.dest.path / "helpText.js", pagesDir / "helpText.js")
      os.copy
        .over(res.dest.path / "helpText.js.map", pagesDir / "helpText.js.map")
      copyDicts()
      val generatedFiles = os
        .walk(res.dest.path)
        .filter(f =>
          (f.ext == "js" || f.ext == "map") && f.last.startsWith("internal")
        )
      // move each file to pages directory
      generatedFiles.foreach { file =>
        os.move.over(file, pagesDir / file.last)
      }
      res
    }

  def copyDicts =
    T.sources {
      val resources = build.millSourcePath / "shared" / "resources"
      val short = "ShortDictionary.txt"
      val long = "LongDictionary.txt"
      os.copy(resources / short, pagesDir / short)
      os.copy(resources / long, pagesDir / long)
      Seq(PathRef(pagesDir / short), PathRef(pagesDir / long))
    }

  object test extends ScalaJSTests with VyxalTestModule
}
