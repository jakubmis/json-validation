name := "json-validation"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.2.5",
  "org.http4s" %% "http4s-core" % "0.23.1",
  "org.http4s" %% "http4s-blaze-server" % "0.23.1",
  "org.http4s" %% "http4s-blaze-client" % "0.23.1",
  "org.http4s" %% "http4s-dsl" % "0.23.1",
  "org.http4s" %% "http4s-circe" % "0.23.1",
  "org.http4s" %% "http4s-prometheus-metrics" % "0.23.1",
  "com.typesafe" % "config" % "1.4.2",
  "ch.qos.logback" % "logback-classic" % "1.2.5",
  "com.github.java-json-tools" % "json-schema-validator" % "2.2.14",
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "0.20.2",
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "0.20.2",
  "io.circe" %% "circe-generic" % "0.14.2",
  "io.circe" %% "circe-generic-extras" % "0.14.2",
  "io.circe" %% "circe-literal" % "0.14.2",
  "org.tpolecat" %% "doobie-h2" % "1.0.0-RC1",
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC1",
  "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC1",
  "org.scalatest" %% "scalatest" % "3.2.11" % Test,
)