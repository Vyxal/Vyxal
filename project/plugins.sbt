addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.1")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.14.0")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.4.15")
// For making an uber JAR
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.1.3")
// For auto-formatting
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")
// For linting
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.11.1")
