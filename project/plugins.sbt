resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.bintrayRepo("hseeberger", "maven"))


addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.2")

resolvers += "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "Era7 maven releases" at "https://s3-eu-west-1.amazonaws.com/releases.era7.com"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.chatwork" % "sbt-aws-eb" % "1.0.19")

addSbtPlugin("ohnosequences" % "sbt-s3-resolver" % "0.14.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-aspectj" % "0.10.6")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.5")