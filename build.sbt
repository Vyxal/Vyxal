//TODO clean this file up
// enablePlugins(ScalaJSPlugin)

val vyxalVersion = "3.0.0"

ThisBuild / scalaVersion := "3.2.1"

//Automatically reload SBT when build.sbt changes
Global / onChangedBuildSource := ReloadOnSourceChanges

import org.scalajs.linker.interface.OutputPatterns

// From https://www.scalatest.org/user_guide/using_the_runner
// Suppress output from successful tests
Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oNCXELOPQRM")

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
      "org.scala-lang.modules" %%% "scala-parser-combinators" % "2.1.1",
      // For command line parsing
      "com.github.scopt" %%% "scopt" % "4.1.0",
      // Used by ScalaTest
      "org.scalactic" %%% "scalactic" % "3.2.14",
      "org.scalatest" %%% "scalatest" % "3.2.14" % Test
    ),
    scalacOptions ++= Seq(
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
    ),
    // Configure Scaladoc
    Compile / doc / target := file("docs"),
    Compile / doc / scalacOptions ++= Seq(
      "-project-version",
      vyxalVersion,
      "-groups", // Group similar functions
      "-Ygenerate-inkuire", // Allow type-based searches
      "-external-mappings:.*vyxal.*::scaladoc3::https://vyxal.github.io/Vyxal/docs/",
      "-external-mappings:.*scala.util.parsing.*::scaladoc3::https://scala-lang.org/api/2.12.8/scala-parser-combinators/",
      "-external-mappings:.*scala(?!.util.parsing).*::scaladoc3::https://scala-lang.org/api/3.x/",
      "-external-mappings:.*java.*::javadoc::https://docs.oracle.com/javase/8/docs/api/"
    )
  )
  .jvmSettings(
    // JVM-specific settings
    Compile / mainClass := Some("vyxal.Main"),
    assembly / mainClass := Some("vyxal.Main"),
    assembly / assemblyJarName := s"vyxal-$vyxalVersion.jar"
  )
  .jsSettings(
    // JS-specific settings
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.2.0",
      "com.github.scopt" %%% "scopt" % "4.1.0"
    ),
    // Where the compiled JS is output
    Compile / fastOptJS / artifactPath := baseDirectory.value.getParentFile / "pages" / "vyxal.js",
    Compile / fullOptJS / artifactPath := baseDirectory.value.getParentFile / "pages" / "vyxal.js"
  )
  .nativeSettings(
    // Scala Native-specific settings
  )
