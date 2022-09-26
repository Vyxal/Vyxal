name := "vyxal3"
version := "3.0.0"
scalaVersion := "3.1.1"
Compile / mainClass := Some("vyxal")

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1"
)
