//TODO clean this file up
// enablePlugins(ScalaJSPlugin)

val vyxalVersion = "3.0.0"

ThisBuild / scalaVersion := "3.3.0"

//Automatically reload SBT when build.sbt changes
Global / onChangedBuildSource := ReloadOnSourceChanges

import org.scalajs.linker.interface.OutputPatterns

import sbtcrossproject.{CrossType, Platform}

lazy val root: Project = project
  .in(file("."))
  .aggregate(vyxal.js, vyxal.jvm, vyxal.native)
  .settings(
    publish := {},
    publishLocal := {},
  )

/** Custom folder structure */
// TODO (user): Figure out why this can't be a singleton object
val CustomCrossType = new sbtcrossproject.CrossType {
  def projectDir(crossBase: File, projectType: String): File =
    crossBase / projectType

  def projectDir(crossBase: File, platform: Platform): File =
    crossBase / platform.identifier

  def moduleDirFor(moduleBase: File, conf: String): Option[File] =
    if (conf == "main") Some(moduleBase)
    else if (conf == "test") Some(moduleBase / "test")
    else None

  def sharedSrcDir(projectBase: File, conf: String): Option[File] =
    moduleDirFor(projectBase.getParentFile / "shared", conf).map(_ / "src")

  override def partiallySharedSrcDir(
      projectBase: File,
      platforms: Seq[Platform],
      conf: String,
  ): Option[File] = {
    val dir = platforms.map(_.identifier).mkString("-")
    moduleDirFor(projectBase.getParentFile / dir, conf).map(_ / "src")
  }

  override def sharedResourcesDir(
      projectBase: File,
      conf: String,
  ): Option[File] =
    moduleDirFor(projectBase.getParentFile / "shared", conf)
      .map(_ / "resources")

  override def partiallySharedResourcesDir(
      projectBase: File,
      platforms: Seq[Platform],
      conf: String,
  ): Option[File] = {
    val dir = platforms.map(_.identifier).mkString("-")
    moduleDirFor(projectBase.getParentFile / dir, conf).map(_ / "resources")
  }
}

lazy val vyxal = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CustomCrossType)
  .in(file("."))
  .settings(
    // Shared settings
    name := "vyxal",
    version := vyxalVersion,
    semanticdbEnabled := true,
    // The custom CrossType above only changes where the compiler looks for
    // shared/partially shared code. This is required for changing the location
    // of platform-specific code.
    Compile / scalaSource := baseDirectory.value / "src",
    Compile / resourceDirectory := baseDirectory.value / "resources",
    Test / scalaSource := baseDirectory.value / "test" / "src",
    Test / resourceDirectory := baseDirectory.value / "test" / "resources",
    libraryDependencies ++=
      Seq(
        // For number stuff
        "org.typelevel" %%% "spire" % "0.18.0",
        "org.scala-lang.modules" %%% "scala-parser-combinators" % "2.3.0",
        "com.lihaoyi" %%% "fastparse" % "3.0.2",
        // For command line parsing
        "com.github.scopt" %%% "scopt" % "4.1.0",
        // For logging
        "com.outr" %%% "scribe" % "3.11.9",
        // For reading tests.yaml
        "org.virtuslab" %%% "scala-yaml" % "0.0.8" % Test,
        "org.scalatest" %%% "scalatest" % "3.2.16" % Test,
      ),
    scalacOptions ++=
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
      ),
    // From https://www.scalatest.org/user_guide/using_the_runner
    // Suppress output from successful tests
    Test / testOptions +=
      Tests.Argument(TestFrameworks.ScalaTest, "-oNCXEOPQRM"),
  )
  .jvmSettings(
    // JVM-specific settings
    Compile / mainClass := Some("vyxal.Main"),
    assembly / mainClass := Some("vyxal.Main"),
    assembly / assemblyJarName := s"vyxal-$vyxalVersion.jar",
    // Necessary for tests to be able to access resources folder
    Test / fork := true,
    libraryDependencies ++=
      Seq(
        "org.jline" % "jline" % "3.23.0",
        "org.jline" % "jline-terminal-jansi" % "3.23.0",
        "org.fusesource.jansi" % "jansi" % "2.4.0",
      ),
    Compile / run / fork := true,
    Compile / run / connectInput := true,
    Compile / run / outputStrategy := Some(StdoutOutput),
    Compile / run / envVars :=
      Map(
        "REPL" -> "false",
        "VYXAL_LOG_LEVEL" -> "Debug",
      ),
  )
  .jsSettings(
    // JS-specific settings
    libraryDependencies ++=
      Seq(
        "org.scala-js" %%% "scalajs-dom" % "2.6.0"
      ),
    // Where the compiled JS is output
    Compile / fastOptJS / artifactPath :=
      baseDirectory.value.getParentFile / "pages" / "vyxal.js",
    Compile / fullOptJS / artifactPath :=
      baseDirectory.value.getParentFile / "pages" / "vyxal.js",
  )
  .nativeSettings(
    // Scala Native-specific settings
    nativeConfig ~= {
      _.withEmbedResources(true)
    }
  )
