addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.2.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.1")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.13.1")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.2.0")
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.4.14")
// For making an uber JAR
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.1.1")
// For auto-formatting
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.0")
// For linting
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.11.0")
