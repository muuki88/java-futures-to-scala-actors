name := "java-futures-to-scala-actors"

version := "1.0"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.3.4",
    "com.google.guava" % "guava" % "17.0",
    // Testing
    "com.typesafe.akka" %% "akka-testkit" % "2.3.4" % "test",
    "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature"
)

javacOptions ++= Seq(
  "-source", "1.8",
  "-target", "1.8"
)