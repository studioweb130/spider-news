import sbt._

object Dependencies {

  val akkaV = "2.4.4"
  val slickV = "3.1.1"
  val json4sV = "3.3.0"
  val kamonV = "0.6.0"
  val aspectJV = "1.8.9"
  val scalaTestV  = "2.2.6"
  val cardapiV = "0.1.1"

  lazy val service = { logging ++ rest ++ test ++ httpClient ++ batch}
  lazy val entities = { util ++ json ++ logging ++ rest ++ test ++ httpClient}

  val rest = {
    Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaV
    )
  }

  val logging = {
    Seq(
      "ch.qos.logback" % "logback-classic" % "1.1.7",
      "com.typesafe.akka" %% "akka-slf4j" % akkaV,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
    )}


  val util = {
    Seq(
      "joda-time" % "joda-time" % "2.9.3",
      "com.amazonaws" % "aws-java-sdk" % "1.11.7",
      "net.ruippeixotog" %% "scala-scraper" % "1.0.0",
      "org.apache.commons" % "commons-email" % "1.2",
      "org.typelevel" %% "cats-core" % "0.7.0"
    )}

  val json = {
    Seq(
      "org.json4s" %% "json4s-native" % json4sV,
      "org.json4s" %% "json4s-ext" % json4sV,
      "de.heikoseeberger" %% "akka-http-json4s" % "1.6.0"
    )
  }

  val httpClient = {
    Seq(
      "com.typesafe.akka" %% "akka-http-experimental" % akkaV
    )
  }

  val batch = {
    Seq(
      "com.enragedginger" %% "akka-quartz-scheduler" % "1.5.0-akka-2.4.x"
    )
  }


  val test = {
    Seq(
      "org.scalatest" %% "scalatest" % scalaTestV % "test",
      "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
      "com.typesafe.akka" %% "akka-http-testkit" % akkaV % "test",
      "org.mockito"       %  "mockito-core" % "1.10.19" % "test",
      "org.scalamock"     %% "scalamock-scalatest-support" % "3.2.2" % "test"
    )}

}