import mill._
import mill.define.Target
import mill.scalajslib._
import mill.scalajslib.api._
import mill.scalalib._
import mill.scalanativelib._
import mill.scalanativelib.api._

/** Shared settings for all modules */
trait VyxalModule extends ScalaModule {
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
    "-language:adhocExtensions",
    // "-explain",
    "-print-lines"
  )

  // Combine shared sources and platform-specific sources
  def sources = T.sources(
    build.millSourcePath / platform / "src" / "main" / "scala",
    build.millSourcePath / "shared" / "src" / "main" / "scala"
  )

  trait VyxalTestModule extends Tests with TestModule.ScalaTest {
    def scalaVersion = VyxalModule.this.scalaVersion()

    def ivyDeps = Agg(ivy"org.scalatest::scalatest:3.2.15")

    // Task to only show output from failed tests
    def testQuiet(args: String*) =
      T.command { testOnly(args :+ "-oNCXELOPQRM": _*)() }

    def sources = T.sources(
      build.millSourcePath / platform / "src" / "test" / "scala",
      build.millSourcePath / "shared" / "src" / "test" / "scala"
    )
  }
}

/** Shared and JVM-specific code */
object jvm extends VyxalModule {
  def platform = "jvm"

  object test extends VyxalTestModule
}

/** Shared and JS-specific code */
object js extends ScalaJSModule with VyxalModule {
  def platform = "js"
  def scalaJSVersion = "1.13.0"
  def moduleKind = T { ModuleKind.NoModule }

  override def scalaJSOutputPatterns = T {
    OutputPatterns.fromJSFile("pages/vyxal.js")
  }

  object test extends VyxalTestModule
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

  object test extends ScalaNativeModule with VyxalTestModule {
    def scalaNativeVersion = native.this.scalaNativeVersion
  }
}
