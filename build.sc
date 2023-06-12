import $ivy.`com.goyeau::mill-scalafix::0.2.9`
import com.goyeau.mill.scalafix.ScalafixModule
import mill._
import mill.define.Target
import mill.scalajslib._
import mill.scalajslib.api._
import mill.scalalib._
import mill.scalalib.scalafmt.ScalafmtModule
import mill.scalanativelib._
import mill.scalanativelib.api._

/** Shared settings for all modules */
trait VyxalModule extends ScalaModule with ScalafmtModule with ScalafixModule {
  def platform: String

  def scalaVersion = "3.2.2"

  def ivyDeps = Agg(
    ivy"org.typelevel::spire::0.18.0",
    ivy"org.scala-lang.modules::scala-parser-combinators::2.2.0",
    ivy"com.github.scopt::scopt::4.1.0",
    ivy"org.scalactic::scalactic::3.2.15"
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
      extends Tests
      with TestModule.ScalaTest
      with ScalafmtModule
      with ScalafixModule {
    def scalaVersion = VyxalModule.this.scalaVersion()

    def ivyDeps = Agg(
      ivy"org.scalatest::scalatest::3.2.15",
      ivy"org.virtuslab::scala-yaml::0.0.7"
    )

    // Task to only show output from failed tests
    def testQuiet(args: String*) = {
      val newArgs = if (args.contains("--")) args else args :+ "--"
      T.command { testOnly(newArgs :+ "-oNCXEOPQRM": _*)() }
    }

    def sources = T.sources(
      build.millSourcePath / platform / "src" / "test" / "scala",
      build.millSourcePath / "shared" / "src" / "test" / "scala"
    )
    def resources = T.sources(
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
      ivy"org.jline:jline::3.23.0",
      ivy"org.jline:jline-terminal-jansi::3.23.0",
      ivy"org.fusesource.jansi:jansi::2.4.0"
    )
  }

  object test extends VyxalTestModule
}

/** Shared and JS-specific code */
object js extends ScalaJSModule with VyxalModule {
  def platform = "js"
  def scalaJSVersion = "1.13.0"
  def moduleKind = T { ModuleKind.NoModule }

  override def fastLinkJS = T {
    val pagesDir = build.millSourcePath / "pages"
    val res = super.fastLinkJS()
    os.copy(res.dest.path / "main.js", pagesDir / "vyxal.js")
    os.copy(res.dest.path / "main.js.map", pagesDir / "vyxal.js.map")
    res
  }

  override def fullLinkJS = T {
    val pagesDir = build.millSourcePath / "pages"
    val res = super.fastLinkJS()
    os.copy(res.dest.path / "main.js", pagesDir / "vyxal.js")
    os.copy(res.dest.path / "main.js.map", pagesDir / "vyxal.js.map")
    res
  }

  // Works in Mill 0.11 but not 0.10
  // todo when the third-party Scalafix plugin supports Mill 0.11,
  // use this again instead of the janky fastLinkJS/fullLinkJS overrides above
  // override def scalaJSOutputPatterns = T {
  //   OutputPatterns.fromJSFile("pages/vyxal.js")
  // }

  object test extends VyxalTestModule with ScalaJSModule {
    def scalaJSVersion = "1.13.0"
  }
}

/** Shared and native-specific code */
object native extends ScalaNativeModule with VyxalModule {
  def platform = "native"
  def scalaNativeVersion = "0.4.9"

  def ivyDeps = T {
    super.ivyDeps() ++ Seq(ivy"com.github.scopt::scopt::4.1.0")
  }

  def nativeEmbedResources = true

  def releaseMode = ReleaseMode.ReleaseFast
  def nativeLTO = LTO.Thin

  object test extends VyxalTestModule with ScalaNativeModule {
    def scalaNativeVersion = native.this.scalaNativeVersion
  }
}
