import sbt._
import Keys._
import play.Project._

import com.tuplejump.sbt.yeoman._

object ApplicationBuild extends Build {

  val appName = "hello-play"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Select Play modules
    //jdbc,      // The JDBC connection pool and the play.api.db API
    //anorm,     // Scala RDBMS Library
    //javaJdbc,  // Java database API
    //javaEbean, // Java Ebean plugin
    //javaJpa,   // Java JPA plugin
    //filters,   // A set of built-in filters
    //javaCore, // The core Java API

    // WebJars pull in client-side web libraries
    "org.webjars" % "webjars-play" % "2.1.0",
    "org.webjars" % "bootstrap" % "2.3.1"

    // Add your own project dependencies in the form:
    // "group" % "artifact" % "version"
  )

  val customSettings = Seq(scalaVersion := "2.10.0",
    licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))) ++
    Yeoman.yeomanSettings

  val main = play.Project(appName, appVersion, appDependencies).settings(
    customSettings: _*
  )

}
