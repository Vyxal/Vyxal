//TODO clean this file up
// enablePlugins(ScalaJSPlugin)

val vyxalVersion = "3.0.0"

ThisBuild / scalaVersion := "3.3.0"

//Automatically reload SBT when build.sbt changes
Global / onChangedBuildSource := ReloadOnSourceChanges

import org.scalajs.linker.interface.OutputPatterns

lazy val root: Project = project
  .in(file("."))
  .aggregate(vyxal.js, vyxal.jvm, vyxal.native)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val vyxal = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("."))
  .settings(
    // Shared settings
    name := "vyxal",
    version := vyxalVersion,
    semanticdbEnabled := true,
    libraryDependencies ++= Seq(
      // For number stuff
      "org.typelevel" %%% "spire" % "0.18.0",
      "org.scala-lang.modules" %%% "scala-parser-combinators" % "2.2.0",
      // For command line parsing
      "com.github.scopt" %%% "scopt" % "4.1.0",
      // For logging
      "com.outr" %%% "scribe" % "3.11.5",
      // For reading tests.yaml
      "org.virtuslab" %%% "scala-yaml" % "0.0.7" % Test,
      "org.scalatest" %%% "scalatest" % "3.2.15" % Test
    ),
    scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-encoding",
      "utf-8", // Specify character encoding used by source files.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      // Above options from https://tpolecat.github.io/2017/04/25/scalac-flags.html
      "-language:implicitConversions",
      // "-explain",
      "-print-lines"
    ),
    // From https://www.scalatest.org/user_guide/using_the_runner
    // Suppress output from successful tests
    Test / testOptions += Tests
      .Argument(TestFrameworks.ScalaTest, "-oNCXEOPQRM")
  )
  .jvmSettings(
    // JVM-specific settings
    Compile / mainClass := Some("vyxal.Main"),
    assembly / mainClass := Some("vyxal.Main"),
    assembly / assemblyJarName := s"vyxal-$vyxalVersion.jar",
    // Necessary for tests to be able to access src/main/resources
    Test / fork := true,
    libraryDependencies ++= Seq(
      "org.jline" % "jline" % "3.23.0",
      "org.jline" % "jline-terminal-jansi" % "3.23.0",
      "org.fusesource.jansi" % "jansi" % "2.4.0"
    ),
    Compile / run / fork := true,
    Compile / run / connectInput := true,
    Compile / run / outputStrategy := Some(StdoutOutput),
    Compile / run / envVars := Map(
      "REPL" -> "false",
      "VYXAL_LOG_LEVEL" -> "Debug"
    ),
  )
  .jsSettings(
    // JS-specific settings
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.4.0"
    ),
    // Where the compiled JS is output
    Compile / fastOptJS / artifactPath := baseDirectory.value.getParentFile / "pages" / "vyxal.js",
    Compile / fullOptJS / artifactPath := baseDirectory.value.getParentFile / "pages" / "vyxal.js",
  )
  .nativeSettings(
    // Scala Native-specific settings
    nativeConfig ~= {
      _.withEmbedResources(true)
    },
  )
