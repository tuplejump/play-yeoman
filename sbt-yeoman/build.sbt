name := """sbt-yeoman"""

organization := "com.tuplejump"

sbtPlugin := true

version := "0.8.1"

sbtVersion in Global := "0.13.8"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.0.2")

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

homepage := Some(url("https://github.com/tuplejump/play-yeoman"))

organizationName := "Tuplejump Software Pvt. Ltd."

organizationHomepage := Some(url("http://www.tuplejump.com"))

Seq(
  scalaSource in Compile <<= baseDirectory / "app",
  javaSource in Compile <<= baseDirectory / "app",
  sourceDirectory in Compile <<= baseDirectory / "app",
  scalaSource in Test <<= baseDirectory / "test",
  javaSource in Test <<= baseDirectory / "test",
  sourceDirectory in Test <<= baseDirectory / "test",
  resourceDirectory in Compile <<= baseDirectory / "conf"
)

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <scm>
    <url>git@github.com:tuplejump/play-yeoman.git</url>
    <connection>scm:git:git@github.com:tuplejump/play-yeoman.git</connection>
  </scm>
  <developers>
    <developer>
      <id>eraoferrors</id>
      <name>Shiti Saxena</name>
      <url>https://twitter.com/eraoferrors</url>
    </developer>
    <developer>
      <id>milliondreams</id>
      <name>Rohit Rai</name>
      <url>https://twitter.com/milliondreams</url>
    </developer>
  </developers>)


