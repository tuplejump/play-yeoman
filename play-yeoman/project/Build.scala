import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "play-yeoman"
  val appVersion = "0.0.8-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    //jdbc,
    //anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    organization := "com.tuplejump",
    organizationName := "tuplejump",
    organizationHomepage := Some(new java.net.URL("http://www.tuplejump.com")),
    licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
  )
}
