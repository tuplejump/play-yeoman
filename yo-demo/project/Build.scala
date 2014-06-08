import sbt._
import Keys._
import com.tuplejump.sbt.yeoman.Yeoman
import play.Play.autoImport._
import PlayKeys._

object ApplicationBuild extends Build {

  val appName = "yo-demo"
  val appVersion = "1.7"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm
  )

  val appSettings = Seq(version := appVersion, libraryDependencies ++= appDependencies, scalaVersion := "2.11.1") ++
    Yeoman.yeomanSettings ++
    Yeoman.withTemplates

  val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
    // Add your own project settings here
    appSettings: _*
  )

}
