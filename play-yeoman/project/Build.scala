import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._

object ApplicationBuild extends Build {

  val appName = "play-yeoman"
  val appVersion = "0.8.4-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    //jdbc,
    //anorm
  )

  val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
    version := appVersion,
    libraryDependencies ++= appDependencies,
    // Add your own project settings here
    scalaVersion in Global := "2.11.4",
    crossScalaVersions := Seq("2.10.4", "2.11.4"),
    homepage := Some(url("https://github.com/tuplejump/play-yeoman")),
    organization := "com.tuplejump",
    organizationName := "Tuplejump Software PVt. Ltd.",
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
            <id>milliondreams</id>
            <name>Rohit Rai</name>
            <url>https://twitter.com/milliondreams</url>
          </developer>
        </developers>)
  )
}
