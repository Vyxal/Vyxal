import mill._
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._

/** Shared settings for all modules */
trait VyxalModule extends SbtModule {
  def scalaVersion = "3.2.1"

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
}

/** Code shared between JVM and JS */
object shared extends VyxalModule {
  def ivyDeps = Agg(
    ivy"org.typelevel::spire:0.18.0",
    ivy"org.scala-lang.modules::scala-parser-combinators:2.1.1",
    ivy"org.scalactic::scalactic:3.2.14",
  )

  object test extends Tests with TestModule.ScalaTest {
    def ivyDeps = Agg(ivy"org.scalatest::scalatest:3.2.14")
  }
}

/** JVM-specific code */
object jvm extends VyxalModule {
  def moduleDeps = Seq(shared)

  def ivyDeps = Agg(ivy"com.github.scopt::scopt:4.1.0")
}

/** JS-specific code */
object js extends VyxalModule with ScalaJSModule {
  def scalaJSVersion = "1.12.0"
  def moduleDeps = Seq(shared)
  def ivyDeps = Agg(ivy"org.scala-js::scalajs-dom::2.2.0")
  def moduleKind = T { ModuleKind.CommonJSModule }
  object test extends Tests with TestModule.Utest {
    def ivyDeps = Agg(ivy"com.lihaoyi::utest::0.8.0")
  }
}
