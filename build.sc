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
  override def scalaVersion = "3.3.0"

  def vyxalVersion = "3.0.0"

  def platform: String

  override def ivyDeps =
    Agg(
      ivy"org.typelevel::spire::0.18.0",
      ivy"org.scala-lang.modules::scala-parser-combinators::2.3.0",
      ivy"com.lihaoyi::fastparse::3.0.2",
      ivy"com.github.scopt::scopt::4.1.0",
      ivy"com.outr::scribe::3.12.2",
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
      extends JavaModuleTests
      with TestModule.ScalaTest
      with ScalafmtModule {
    override def defaultCommandName() = "test"

    override def ivyDeps =
      Agg(
        ivy"org.scalatest::scalatest::3.2.17",
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

/** Shared and JVM-specific code */
object jvm extends VyxalModule {
  val platform = "jvm"

  def ivyDeps =
    T {
      super.ivyDeps() ++
        Seq(
          // For the REPL
          ivy"org.jline:jline:3.24.1",
          ivy"org.jline:jline-terminal-jansi:3.24.1",
          ivy"org.fusesource.jansi:jansi:2.4.1",
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

  /** Run a method on a singleton object */
  def runMethod[T](
      classpath: Seq[PathRef],
      objectName: String,
      methodName: String,
  ): T = {
    val urls = classpath.map(_.path.toIO.toURI.toURL).toArray
    val cl = new URLClassLoader(urls, getClass.getClassLoader)
    val clazz = Class.forName(objectName + "$", false, cl)
    val method = clazz.getMethod(methodName)
    val singleton = clazz.getField("MODULE$").get(null)
    method.invoke(singleton).asInstanceOf[T]
  }

  /** Generate elements.txt and trigraphs.txt */
  def docs =
    T.sources {
      val (elements, trigraphs) = runMethod[(String, String)](
        jvm.runClasspath(),
        "vyxal.gen.GenerateDocs",
        "generate",
      )
      val elementsFile = build.millSourcePath / "documentation" / "elements.txt"
      val trigraphsFile = build.millSourcePath / "documentation" /
        "trigraphs.txt"
      os.write.over(elementsFile, elements)
      os.write.over(trigraphsFile, trigraphs)
      Seq(PathRef(elementsFile), PathRef(trigraphsFile))
    }

  /** Generate nanorc files for JLine highlighting */
  def nanorc =
    T.sources {
      val nanorcs: Map[String, String] =
        runMethod(jvm.runClasspath(), "vyxal.gen.GenerateNanorc", "generate")
      nanorcs.map {
        case (fileName, contents) =>
          val file = build.millSourcePath / "jvm" / "resources" / fileName
          os.write.over(file, contents)
          PathRef(file)
      }.toSeq
    }

  object test extends ScalaTests with VyxalTestModule
}

/** Shared and JS-specific code */
object js extends VyxalModule with ScalaJSModule {
  val platform = "js"

  def scalaJSVersion = "1.14.0"
  def moduleKind = T { ModuleKind.ESModule }

  def ivyDeps =
    T {
      super.ivyDeps() ++ Seq(ivy"org.scala-js::scalajs-dom::2.8.0")
    }

  def pagesDir = build.millSourcePath / "pages"

  override def fastLinkJS =
    T {
      val res = super.fastLinkJS()
      os.copy.over(res.dest.path / "main.js", pagesDir / "vyxal.js")
      os.copy.over(res.dest.path / "main.js.map", pagesDir / "vyxal.js.map")

      val generatedFiles = os
        .walk(res.dest.path)
        .filter(f =>
          (f.ext == "js" || f.ext == "map") && f.last.startsWith("internal")
        )
      // move each file to pages directory
      generatedFiles.foreach { file =>
        println(file)
        os.move(file, pagesDir / file.last)
      }

      copyDicts()
      res
    }

  override def fullLinkJS =
    T {
      val res = super.fastLinkJS()
      os.copy.over(res.dest.path / "main.js", pagesDir / "vyxal.js")
      os.copy.over(res.dest.path / "main.js.map", pagesDir / "vyxal.js.map")
      copyDicts()
      val generatedFiles = os
        .walk(res.dest.path)
        .filter(f =>
          (f.ext == "js" || f.ext == "map") && f.last.startsWith("internal")
        )
      // move each file to pages directory
      generatedFiles.foreach { file =>
        println(file)
        os.move(file, pagesDir / file.last)
      }
      res
    }

  def copyDicts =
    T.sources {
      val resources = build.millSourcePath / "shared" / "resources"
      val short = "ShortDictionary.txt"
      val long = "LongDictionary.txt"
      os.copy.over(resources / short, pagesDir / short)
      os.copy.over(resources / long, pagesDir / long)
      Seq(PathRef(pagesDir / short), PathRef(pagesDir / long))
    }

  object test extends ScalaJSTests with VyxalTestModule
}

/** Shared and native-specific code */
object native extends VyxalModule with ScalaNativeModule {
  val platform = "native"

  def scalaNativeVersion = "0.4.16"

  def ivyDeps =
    T {
      super.ivyDeps() ++ Seq(ivy"com.github.scopt::scopt:4.1.0")
    }

  override def nativeEmbedResources = true

  object test extends ScalaNativeTests with VyxalTestModule {
    override def nativeEmbedResources = true
  }
}
