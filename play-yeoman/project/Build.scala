import play.sbt.PlayScala
import sbt.Keys._
import sbt._

object ApplicationBuild extends Build {

  val appName = "play-yeoman"
  val appVersion = "0.8.1"

  val appDependencies = Seq(
    // Add your project dependencies here,
    //jdbc,
    //anorm
  )

  val main = Project(appName, file(".")).enablePlugins(PlayScala).settings(
    version := appVersion,
    libraryDependencies ++= appDependencies,
    // Add your own project settings here
    scalaVersion in Global := "2.11.6",
    crossScalaVersions := Seq("2.11.6"),
    homepage := Some(url("https://github.com/tuplejump/play-yeoman")),
    organization := "com.tuplejump",
    organizationName := "Tuplejump Software Pvt. Ltd.",
    organizationHomepage := Some(new java.net.URL("http://www.tuplejump.com")),
    licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    publishMavenStyle := true,
    publishTo <<= version {
      (v: String) =>
        val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := {
      _ => false
    },
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
  )
}
