import java.net.{JarURLConnection, URL, URLClassLoader}

import $ivy.`com.goyeau::mill-scalafix::0.2.11`
import com.goyeau.mill.scalafix.ScalafixModule
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
  def platform: String

  def scalaVersion = "3.3.0"

  def vyxalVersion = "3.0.0"

  def ivyDeps = Agg(
    ivy"org.typelevel::spire::0.18.0",
    ivy"org.scala-lang.modules::scala-parser-combinators::2.3.0",
    ivy"com.github.scopt::scopt::4.1.0",
    ivy"com.outr::scribe::3.11.5"
    ivy"org.scalactic::scalactic::3.2.16"
  )

  def scalacOptions = Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8", // Specify character encoding used by source files.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    // Above options from https://tpolecat.github.io/2017/04/25/scalac-flags.html
    "-language:implicitConversions",
    // "-explain",
    "-print-lines"
  )

  // Combine shared sources and platform-specific sources
  def sources = T.sources(
    build.millSourcePath / platform / "src" / "main" / "scala",
    build.millSourcePath / "shared" / "src" / "main" / "scala"
  )
  def resources = T.sources(
    build.millSourcePath / platform / "src" / "main" / "resources",
    build.millSourcePath / "shared" / "src" / "main" / "resources"
  )

  trait VyxalTestModule
      extends JavaModuleTests
      with TestModule.ScalaTest
      with ScalafmtModule {
    override def defaultCommandName() = "test"

    def ivyDeps = Agg(
      ivy"org.scalatest::scalatest::3.2.16",
      ivy"org.scala-sbt:test-interface:1.0",
      ivy"org.virtuslab::scala-yaml::0.0.7"
    )

    // Task to only show output from failed tests
    def testQuiet(args: String*) = {
      val newArgs = if (args.contains("--")) args else args :+ "--"
      T.command { testOnly(newArgs :+ "-oNCXEOPQRM": _*)() }
    }

    override def sources = T.sources(
      build.millSourcePath / platform / "src" / "test" / "scala",
      build.millSourcePath / "shared" / "src" / "test" / "scala"
    )
    override def resources = T.sources(
      build.millSourcePath / platform / "src" / "test" / "resources",
      build.millSourcePath / "shared" / "src" / "test" / "resources"
    )
  }
}

/** Shared and JVM-specific code */
object jvm extends VyxalModule {
  def platform = "jvm"

  def ivyDeps = T {
    super.ivyDeps() ++ Seq(
      // For the REPL
      ivy"org.jline:jline:3.23.0",
      ivy"org.jline:jline-terminal-jansi:3.23.0",
      ivy"org.fusesource.jansi:jansi:2.4.0"
    )
  }

  def forkEnv: T[Map[String, String]] = Map("REPL" -> "false", "VYXAL_LOG_LEVEL" -> "Debug")

  override def assembly = T {
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
      methodName: String
  ): T = {
    val urls = classpath.map(_.path.toIO.toURI.toURL).toArray
    val cl = new URLClassLoader(urls, getClass.getClassLoader)
    val clazz = Class.forName(objectName + "$", false, cl)
    val method = clazz.getMethod(methodName)
    val singleton = clazz.getField("MODULE$").get(null)
    method.invoke(singleton).asInstanceOf[T]
  object test extends VyxalTestModule {
    def ivyDeps = T {
      super.ivyDeps() ++ Seq(ivy"org.yaml:snakeyaml::2.0")
    }
  }

  /** Generate elements.txt and trigraphs.txt */
  def docs = T.sources {
    val (elements, trigraphs) = runMethod[(String, String)](
      jvm.runClasspath(),
      "vyxal.gen.GenerateDocs",
      "generate"
    )
    val elementsFile = build.millSourcePath / "documentation" / "elements.txt"
    val trigraphsFile = build.millSourcePath / "documentation" / "trigraphs.txt"
    os.write.over(elementsFile, elements)
    os.write.over(trigraphsFile, trigraphs)
    Seq(PathRef(elementsFile), PathRef(trigraphsFile))
  }

  /** Generate nanorc files for JLine highlighting */
  def nanorc = T.sources {
    val nanorcs: Map[String, String] =
      runMethod(jvm.runClasspath(), "vyxal.gen.GenerateNanorc", "generate")
    nanorcs.map { case (fileName, contents) =>
      val file =
        build.millSourcePath / "jvm" / "src" / "main" / "resources" / fileName
      os.write.over(file, contents)
      PathRef(file)
    }.toSeq
  }

  object test extends ScalaTests with VyxalTestModule
}}

/** Shared and JS-specific code */
object js extends ScalaJSModule with VyxalModule {
  def platform = "js"
  def scalaJSVersion = "1.13.0"
  def moduleKind = T { ModuleKind.NoModule }

  override def fastLinkJS = T {
    val pagesDir = build.millSourcePath / "pages"
    val res = super.fastLinkJS()
    // Copy to pages/vyxal.js
    os.copy.over(res.dest.path / "main.js", pagesDir / "vyxal.js")
    os.copy.over(res.dest.path / "main.js.map", pagesDir / "vyxal.js.map")
    res
  }

  override def fullLinkJS = T {
    val pagesDir = build.millSourcePath / "pages"
    val res = super.fastLinkJS()
    // Copy to pages/vyxal.js
    os.copy.over(res.dest.path / "main.js", pagesDir / "vyxal.js")
    os.copy.over(res.dest.path / "main.js.map", pagesDir / "vyxal.js.map")
    res
  }

  object test extends ScalaJSTests with VyxalTestModule
}

/** Shared and native-specific code */
object native extends ScalaNativeModule with VyxalModule {
  def platform = "native"
  def scalaNativeVersion = "0.4.14"

  def ivyDeps = T {
    super.ivyDeps() ++ Seq(ivy"com.github.scopt::scopt:4.1.0")
  }

  override def nativeEmbedResources = true

  object test extends ScalaNativeTests with VyxalTestModule {
    override def nativeEmbedResources = true
  }
}
