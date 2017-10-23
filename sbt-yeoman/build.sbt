name := """sbt-yeoman"""

organization := "com.tuplejump"

sbtPlugin := true

version := "0.9.1"

scalaVersion := "2.12.4"

sbtVersion in Global := "1.0.2"

//scalaCompilerBridgeSource :=
//  ("org.scala-sbt" % "compiler-interface" % "0.13.15" % "component").sources

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.4.3")

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

homepage := Some(url("https://github.com/tuplejump/play-yeoman"))

organizationName := "Tuplejump Software Pvt. Ltd."

organizationHomepage := Some(url("http://www.tuplejump.com"))

Seq(
  scalaSource in Compile := (baseDirectory.value / "app"),
  javaSource in Compile := (baseDirectory.value / "app"),
  sourceDirectory in Compile := (baseDirectory.value / "app"),
  scalaSource in Test := (baseDirectory.value / "test"),
  javaSource in Test := (baseDirectory.value / "test"),
  sourceDirectory in Test := (baseDirectory.value / "test"),
  resourceDirectory in Compile := (baseDirectory.value / "conf")
)

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
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


