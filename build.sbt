import sbt.Keys._

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

scalacOptions ++= Seq(
  "-language:reflectiveCalls",
  "-language:implicitConversions",
  "-unchecked",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  organization := "com.studioweb",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.11")


lazy val spider = (project in file("."))
  .settings(Revolver.settings: _*)
  .settings(commonSettings: _*)
  .aggregate(entities, service)

lazy val entities = project
  .settings(Revolver.settings: _*)
  .settings(commonSettings: _*)
  .settings(aspectjSettings: _*)
  .settings(libraryDependencies ++= Dependencies.entities)
  .settings(
    name := "spider-entities"
  )

lazy val service = project
  .settings(Revolver.settings: _*)
  .settings(commonSettings: _*)
  .settings(aspectjSettings: _*)
  .settings(libraryDependencies ++= Dependencies.service)
  .settings(
    name := "spider-service"
  )
  .settings(
    testOptions in Test += Tests.Argument("-oF"),
    parallelExecution in Test := false
  )
  .settings(assemblyJarName in assembly := "spider-service.jar")
  .settings(mainClass in assembly := Some("com.studioweb.spider.service.MainApp"))
  .settings(
    ebBundleTargetFiles in aws <<= Def.task {
      val base = baseDirectory.value
      val packageJarFile = (packageBin in Compile).value
      Seq(
        (base / "Dockerfile", "Dockerfile"),
        (base / "Dockerrun.aws.json", "Dockerrun.aws.json"),
        (base / "target/scala-2.11/spider-service.jar", "spider-service.jar")
      )
    }
  )
  .dependsOn(entities)